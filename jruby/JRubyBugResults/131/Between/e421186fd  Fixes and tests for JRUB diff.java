diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index fbb02af22a..64479b9e88 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,2442 +1,2442 @@
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
 import org.jruby.runtime.MethodIndex;
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
-        arrayc.defineFastMethod("eql?", callbackFactory.getFastMethod("eql", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
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
     public static final byte TO_S_SWITCHVALUE = 11;
     public static final byte AT_SWITCHVALUE = 12;
     public static final byte TO_ARY_SWITCHVALUE = 13;
     public static final byte TO_A_SWITCHVALUE = 14;
     public static final byte HASH_SWITCHVALUE = 15;
     public static final byte OP_TIMES_SWITCHVALUE = 16;
     public static final byte OP_SPACESHIP_SWITCHVALUE = 17;
     public static final byte LENGTH_SWITCHVALUE = 18;
     public static final byte LAST_SWITCHVALUE = 19;
     public static final byte SHIFT_SWITCHVALUE = 20;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex,
             String name, IRubyObject[] args, CallType callType, Block block) {
         // If tracing is on, don't do STI dispatch
         if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
         
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_plus(args[0]);
         case AREF_SWITCHVALUE:
             return aref(args);
         case ASET_SWITCHVALUE:
             return aset(args);
         case POP_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return pop();
         case PUSH_SWITCHVALUE:
             return push_m(args);
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_equal(args[0]);
         case UNSHIFT_SWITCHVALUE:
             return unshift_m(args);
         case OP_LSHIFT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return append(args[0]);
         case EMPTY_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return empty_p();
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case AT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return at(args[0]);
         case TO_ARY_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_ary();
         case TO_A_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_a();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case OP_TIMES_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_times(args[0]);
         case OP_SPACESHIP_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_cmp(args[0]);
         case LENGTH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return length();
         case LAST_SWITCHVALUE:
             return last(args);
         case SHIFT_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return shift();
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
 
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
     private boolean tmpLock = false;
     private boolean shared = false;
 
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
         return !shared ? values : toJavaArray();
     }    
 
     public IRubyObject[] toJavaArrayMaybeUnsafe() {
         return (!shared && begin == 0 && values.length == realLength) ? values : toJavaArray();
     }    
 
     /** rb_ary_make_shared
      *
      */
     private final RubyArray makeShared(int beg, int len, RubyClass klass, boolean objectSpace) {
         RubyArray sharedArray = new RubyArray(getRuntime(), klass, objectSpace);
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
     private final RubyArray aryDup() {
         RubyArray dup = new RubyArray(getRuntime(), getMetaClass(), this);
         dup.setTaint(isTaint()); // from DUP_SETUP
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
         return getRuntime().newBoolean(isFrozen() || tmpLock);
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
         if (shared) {
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
 
             shared = true;
             dup.values = values;
             dup.realLength = realLength; 
             dup.begin = begin;
             dup.shared = true;
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
-    public RubyBoolean eql(IRubyObject obj) {
+    public RubyBoolean eql_p(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
-        for (long i = 0; i < realLength; i++) {
-            if (!elt(i).callMethod(context, "eql?", ary.elt(i)).isTrue()) return runtime.getFalse();
+        for (int i = 0; i < realLength; i++) {
+            if (!elt(i).eqlInternal(context, ary.elt(i))) return runtime.getFalse();
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
 
         if(shared){
             alloc(ARRAY_DEFAULT_SIZE);
             shared = false;
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
         if (shared) {
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
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < len; i++) {
             IRubyObject v = elt(i).callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ary2.elt(i));
             if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
         }
         len = realLength - ary2.realLength;
 
         if (len == 0) return RubyFixnum.zero(runtime);
         if (len > 0) return RubyFixnum.one(runtime);
 
         return RubyFixnum.minus_one(runtime);
     }
 
     /** rb_ary_slice_bang
      *
      */
     public IRubyObject slice_bang(IRubyObject[] args) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             long pos = RubyNumeric.num2long(args[0]);
             long len = RubyNumeric.num2long(args[1]);
             
             if (pos < 0) pos = realLength + pos;
 
             args[1] = subseq(pos, len);
             splice(pos, len, null);
             
             return args[1];
         }
         
         IRubyObject arg = args[0];
         if (arg instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg).begLen(realLength, 1);
             long pos = beglen[0];
             long len = beglen[1];
 
             if (pos < 0) {
                 pos = realLength + pos;
             }
             arg = subseq(pos, len);
             splice(pos, len, null);
             return arg;
         }
 
         return delete_at((int) RubyNumeric.num2long(args[0]));
     }
 
     /** rb_ary_assoc
      *
      */
     public IRubyObject assoc(IRubyObject key) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 0
                     && ((RubyArray) v).values[0].equalInternal(context, key).isTrue()) {
                 return v;
             }
         }
         return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     public IRubyObject rassoc(IRubyObject value) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 1
                     && ((RubyArray) v).values[1].equalInternal(context, value).isTrue()) {
                 return v;
             }
         }
 
         return runtime.getNil();
     }
 
     /** flatten
      * 
      */
     private final int flatten(int index, RubyArray ary2, RubyArray memo) {
         int i = index;
         int n;
         int lim = index + ary2.realLength;
 
         IRubyObject id = ary2.id();
 
         if (memo.includes(id)) throw getRuntime().newArgumentError("tried to flatten recursive array");
 
         memo.append(id);
         splice(index, 1, ary2);
         while (i < lim) {
             IRubyObject tmp = elt(i).checkArrayType();
             if (!tmp.isNil()) {
                 n = flatten(i, (RubyArray) tmp, memo);
                 i += n;
                 lim += n;
             }
             i++;
         }
         memo.pop();
         return lim - index - 1; /* returns number of increased items */
     }
 
     /** rb_ary_flatten_bang
      *
      */
     public IRubyObject flatten_bang() {
         int i = 0;
         RubyArray memo = null;
 
         while (i < realLength) {
             IRubyObject ary2 = values[begin + i];
             IRubyObject tmp = ary2.checkArrayType();
             if (!tmp.isNil()) {
                 if (memo == null) {
                     memo = new RubyArray(getRuntime(), false);
                     memo.values = reserve(ARRAY_DEFAULT_SIZE);
                 }
 
                 i += flatten(i, (RubyArray) tmp, memo);
             }
             i++;
         }
         if (memo == null) return getRuntime().getNil();
 
         return this;
     }
 
     /** rb_ary_flatten
      *
      */
     public IRubyObject flatten() {
         RubyArray ary = aryDup();
         ary.flatten_bang();
         return ary;
     }
 
     /** rb_ary_nitems
      *
      */
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
     public IRubyObject op_plus(IRubyObject obj) {
         RubyArray y = obj.convertToArray();
         int len = realLength + y.realLength;
         RubyArray z = new RubyArray(getRuntime(), len);
         System.arraycopy(values, begin, z.values, 0, realLength);
         System.arraycopy(y.values, y.begin, z.values, realLength, y.realLength);
         z.realLength = len;
         return z;
     }
 
     /** rb_ary_times
      *
      */
     public IRubyObject op_times(IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(tmp);
 
         long len = RubyNumeric.num2long(times);
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0);
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         if (Long.MAX_VALUE / len < realLength) {
             throw getRuntime().newArgumentError("argument too big");
         }
 
         len *= realLength;
 
         RubyArray ary2 = new RubyArray(getRuntime(), getMetaClass(), len);
         ary2.realLength = (int) len;
 
         for (int i = 0; i < len; i += realLength) {
             System.arraycopy(values, begin, ary2.values, i, realLength);
         }
 
         ary2.infectBy(this);
 
         return ary2;
     }
 
     /** ary_make_hash
      * 
      */
     private final Set makeSet(RubyArray ary2) {
         final Set set = new HashSet();
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             set.add(values[i]);
         }
 
         if (ary2 != null) {
             begin = ary2.begin;            
             for (int i = begin; i < begin + ary2.realLength; i++) {
                 set.add(ary2.values[i]);
             }
         }
         return set;
     }
 
     /** rb_ary_uniq_bang
      *
      */
     public IRubyObject uniq_bang() {
         Set set = makeSet(null);
 
         if (realLength == set.size()) return getRuntime().getNil();
 
         int j = 0;
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.remove(v)) store(j++, v);
         }
         realLength = j;
         return this;
     }
 
     /** rb_ary_uniq
      *
      */
     public IRubyObject uniq() {
         RubyArray ary = aryDup();
         ary.uniq_bang();
         return ary;
     }
 
     /** rb_ary_diff
      *
      */
     public IRubyObject op_diff(IRubyObject other) {
         Set set = other.convertToArray().makeSet(null);
         RubyArray ary3 = new RubyArray(getRuntime());
 
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             if (set.contains(values[i])) continue;
 
             ary3.append(elt(i - begin));
         }
 
         return ary3;
     }
 
     /** rb_ary_and
      *
      */
     public IRubyObject op_and(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         Set set = ary2.makeSet(null);
         RubyArray ary3 = new RubyArray(getRuntime(), 
                 realLength < ary2.realLength ? realLength : ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.remove(v)) ary3.append(v);
         }
 
         return ary3;
     }
 
     /** rb_ary_or
      *
      */
     public IRubyObject op_or(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         Set set = makeSet(ary2);
 
         RubyArray ary3 = new RubyArray(getRuntime(), realLength + ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.remove(v)) ary3.append(v);
         }
         for (int i = 0; i < ary2.realLength; i++) {
             IRubyObject v = ary2.elt(i);
             if (set.remove(v)) ary3.append(v);
         }
         return ary3;
     }
 
     /** rb_ary_sort
      *
      */
     public RubyArray sort(Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
     public RubyArray sort_bang(Block block) {
         modify();
         if (realLength > 1) {
             tmpLock = true;
             try {
                 if (block.isGiven()) {
                     Arrays.sort(values, 0, realLength, new BlockComparator(block));
                 } else {
                     Arrays.sort(values, 0, realLength, new DefaultComparator());
                 }
             } finally {
                 tmpLock = false;
             }
         }
         return this;
     }
 
     final class BlockComparator implements Comparator {
         private Block block;
 
         public BlockComparator(Block block) {
             this.block = block;
         }
 
         public int compare(Object o1, Object o2) {
             ThreadContext context = getRuntime().getCurrentContext();
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
             IRubyObject ret = block.yield(context, getRuntime().newArray(obj1, obj2), null, null, true);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     final class DefaultComparator implements Comparator {
         public int compare(Object o1, Object o2) {
             if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
                 long a = ((RubyFixnum) o1).getLongValue();
                 long b = ((RubyFixnum) o2).getLongValue();
                 if (a > b) return 1;
                 if (a < b) return -1;
                 return 0;
             }
             if (o1 instanceof RubyString && o2 instanceof RubyString) {
                 return ((RubyString) o1).cmp((RubyString) o2);
             }
 
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
 
             IRubyObject ret = obj1.callMethod(obj1.getRuntime().getCurrentContext(), MethodIndex.OP_SPACESHIP, "<=>", obj2);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     public static void marshalTo(RubyArray array, MarshalStream output) throws IOException {
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
 
     /**
      * @see org.jruby.util.Pack#pack
      */
     public RubyString pack(IRubyObject obj) {
         RubyString iFmt = RubyString.objAsString(obj);
         return Pack.pack(getRuntime(), this, iFmt.getByteList());
     }
 
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
         append(JavaUtil.convertJavaToRuby(getRuntime(), element));
         return true;
 	}
 
 	public boolean remove(Object element) {
         IRubyObject deleted = delete(JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
         return deleted.isNil() ? false : true; // TODO: is this correct ?
 	}
 
 	public boolean containsAll(Collection c) {
 		for (Iterator iter = c.iterator(); iter.hasNext();) {
 			if (indexOf(iter.next()) == -1) return false;
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
         return store(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
     // TODO: make more efficient by not creating IRubyArray[]
 	public void add(int index, Object element) {
         insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element) });
 	}
 
 	public Object remove(int index) {
         return JavaUtil.convertRubyToJava(delete_at(index), Object.class);
 	}
 
 	public int indexOf(Object element) {
         int begin = this.begin;
         
         if (element == null) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (values[i] == null) return i;
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
             
             for (int i = begin; i < begin + realLength; i++) {
                 if (convertedElement.equals(values[i])) return i;
             }
         }
         return -1;
     }
 
 	public int lastIndexOf(Object element) {
         int begin = this.begin;
 
         if (element == null) {
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (values[i] == null) return i;
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
             
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (convertedElement.equals(values[i])) return i;
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
diff --git a/src/org/jruby/RubyBignum.java b/src/org/jruby/RubyBignum.java
index cae1d33c60..22693bf74f 100644
--- a/src/org/jruby/RubyBignum.java
+++ b/src/org/jruby/RubyBignum.java
@@ -1,663 +1,663 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 
 import java.io.IOException;
 import java.math.BigDecimal;
 import java.math.BigInteger;
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
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyBignum extends RubyInteger {
     public static RubyClass createBignumClass(Ruby runtime) {
         RubyClass bignum = runtime.defineClass("Bignum", runtime.getClass("Integer"),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         bignum.index = ClassIndex.BIGNUM;
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyBignum.class);
 
         bignum.defineFastMethod("to_s", callbackFactory.getFastOptMethod("to_s"));
         bignum.defineFastMethod("coerce", callbackFactory.getFastMethod("coerce", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("-@", callbackFactory.getFastMethod("uminus"));
         bignum.defineFastMethod("+", callbackFactory.getFastMethod("plus", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("-", callbackFactory.getFastMethod("minus", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("*", callbackFactory.getFastMethod("mul", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("/", callbackFactory.getFastMethod("div", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("%", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("div", callbackFactory.getFastMethod("div", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("divmod", callbackFactory.getFastMethod("divmod", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("modulo", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("remainder", callbackFactory.getFastMethod("remainder", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("quo", callbackFactory.getFastMethod("quo", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("**", callbackFactory.getFastMethod("pow", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("&", callbackFactory.getFastMethod("and", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("|", callbackFactory.getFastMethod("or", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("^", callbackFactory.getFastMethod("xor", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("~", callbackFactory.getFastMethod("neg"));
         bignum.defineFastMethod("<<", callbackFactory.getFastMethod("lshift", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod(">>", callbackFactory.getFastMethod("rshift", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
 
         bignum.defineFastMethod("<=>", callbackFactory.getFastMethod("cmp", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
         bignum.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         bignum.defineFastMethod("to_f", callbackFactory.getFastMethod("to_f"));
         bignum.defineFastMethod("abs", callbackFactory.getFastMethod("abs"));
         bignum.defineFastMethod("size", callbackFactory.getFastMethod("size"));
 
         return bignum;
     }
 
     private static final int BIT_SIZE = 64;
     private static final long MAX = (1L << (BIT_SIZE - 1)) - 1;
     private static final BigInteger LONG_MAX = BigInteger.valueOf(MAX);
     private static final BigInteger LONG_MIN = BigInteger.valueOf(-MAX - 1);
     
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_MINUS_SWITCHVALUE = 2;
     public static final byte OP_LT_SWITCHVALUE = 3;
     public static final byte TO_S_SWITCHVALUE = 4;
     public static final byte TO_I_SWITCHVALUE = 5;
     public static final byte HASH_SWITCHVALUE = 6;
     public static final byte OP_TIMES_SWITCHVALUE = 7;
     public static final byte EQUALEQUAL_SWITCHVALUE = 8;
     public static final byte OP_SPACESHIP_SWITCHVALUE = 9;
 
     private final BigInteger value;
 
     public RubyBignum(Ruby runtime, BigInteger value) {
         super(runtime, runtime.getClass("Bignum"));
         this.value = value;
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         // If tracing is on, don't do STI dispatch
         if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
         
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return plus(args[0]);
         case OP_MINUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return minus(args[0]);
         case TO_S_SWITCHVALUE:
             return to_s(args);
         case TO_I_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_i();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case OP_TIMES_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return mul(args[0]);
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case OP_SPACESHIP_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return cmp(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.BIGNUM;
     }
 
     public static RubyBignum newBignum(Ruby runtime, long value) {
         return newBignum(runtime, BigInteger.valueOf(value));
     }
 
     public static RubyBignum newBignum(Ruby runtime, double value) {
         return newBignum(runtime, new BigDecimal(value).toBigInteger());
     }
 
     public static RubyBignum newBignum(Ruby runtime, BigInteger value) {
         return new RubyBignum(runtime, value);
     }
 
     public static RubyBignum newBignum(Ruby runtime, String value) {
         return new RubyBignum(runtime, new BigInteger(value));
     }
 
     public double getDoubleValue() {
         return big2dbl(this);
     }
 
     public long getLongValue() {
         return big2long(this);
     }
 
     /** Getter for property value.
      * @return Value of property value.
      */
     public BigInteger getValue() {
         return value;
     }
 
     /*  ================
      *  Utility Methods
      *  ================ 
      */
 
     /* If the value will fit in a Fixnum, return one of those. */
     /** rb_big_norm
      * 
      */
     public static RubyInteger bignorm(Ruby runtime, BigInteger bi) {
         if (bi.compareTo(LONG_MIN) < 0 || bi.compareTo(LONG_MAX) > 0) {
             return newBignum(runtime, bi);
         }
         return runtime.newFixnum(bi.longValue());
     }
 
     /** rb_big2long
      * 
      */
     public static long big2long(RubyBignum value) {
         BigInteger big = value.getValue();
 
         if (big.compareTo(LONG_MIN) < 0 || big.compareTo(LONG_MAX) > 0) {
             throw value.getRuntime().newRangeError("bignum too big to convert into `long'");
     }
         return big.longValue();
         }
 
     /** rb_big2dbl
      * 
      */
     public static double big2dbl(RubyBignum value) {
         BigInteger big = value.getValue();
         double dbl = big.doubleValue();
         if (dbl == Double.NEGATIVE_INFINITY || dbl == Double.POSITIVE_INFINITY) {
             value.getRuntime().getWarnings().warn("Bignum out of Float range");
     }
         return dbl;
     }
 
     /** rb_int2big
      * 
      */
     public static BigInteger fix2big(RubyFixnum arg) {
         return BigInteger.valueOf(arg.getLongValue());
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_big_to_s
      * 
      */
     public IRubyObject to_s(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         int base = args.length == 0 ? 10 : num2int(args[0]);
         if (base < 2 || base > 36) {
             throw getRuntime().newArgumentError("illegal radix " + base);
     }
         return getRuntime().newString(getValue().toString(base));
     }
 
     /** rb_big_coerce
      * 
      */
     public IRubyObject coerce(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return getRuntime().newArray(newBignum(getRuntime(), ((RubyFixnum) other).getLongValue()), this);
         } else if (other instanceof RubyBignum) {
             return getRuntime().newArray(newBignum(getRuntime(), ((RubyBignum) other).getValue()), this);
     }
 
         throw getRuntime().newTypeError("Can't coerce " + other.getMetaClass().getName() + " to Bignum");
     }
 
     /** rb_big_uminus
      * 
      */
     public IRubyObject uminus() {
         return bignorm(getRuntime(), value.negate());
     }
 
     /** rb_big_plus
      * 
      */
     public IRubyObject plus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.add(fix2big(((RubyFixnum) other))));
         }
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.add(((RubyBignum) other).value));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) + ((RubyFloat) other).getDoubleValue());
     }
         return coerceBin("+", other);
     }
 
     /** rb_big_minus
      * 
      */
     public IRubyObject minus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.subtract(fix2big(((RubyFixnum) other))));
     }
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.subtract(((RubyBignum) other).value));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) - ((RubyFloat) other).getDoubleValue());
     }
         return coerceBin("-", other);
     }
 
     /** rb_big_mul
      * 
      */
     public IRubyObject mul(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.multiply(fix2big(((RubyFixnum) other))));
         }
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.multiply(((RubyBignum) other).value));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) * ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("*", other);
     }
 
     /** rb_big_div
      * 
      */
     public IRubyObject div(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) / ((RubyFloat) other).getDoubleValue());
         } else {
             return coerceBin("/", other);
         }
 
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
         }
 
         BigInteger[] results = value.divideAndRemainder(otherValue);
 
         if (results[0].signum() == -1 && results[1].signum() != 0) {
             return bignorm(getRuntime(), results[0].subtract(BigInteger.ONE));
         }
         return bignorm(getRuntime(), results[0]);
     }
 
     /** rb_big_divmod
      * 
      */
     public IRubyObject divmod(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else {
             return coerceBin("divmod", other);
         }
 
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
         }
 
         BigInteger[] results = value.divideAndRemainder(otherValue);
 
         if (results[0].signum() == -1 && results[1].signum() != 0) {
             return bignorm(getRuntime(), results[0].subtract(BigInteger.ONE));
     	}
         final Ruby runtime = getRuntime();
         return RubyArray.newArray(getRuntime(), bignorm(runtime, results[0]), bignorm(runtime, results[1]));
     }
 
     /** rb_big_modulo
      * 
      */
     public IRubyObject mod(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else {
             return coerceBin("%", other);
         }
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
         }
         BigInteger result = value.mod(otherValue.abs());
         if (otherValue.signum() == -1) {
             result = otherValue.add(result);
         }
         return bignorm(getRuntime(), result);
 
             }
 
     /** rb_big_remainder
      * 
      */
     public IRubyObject remainder(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big(((RubyFixnum) other));
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else {
             return coerceBin("remainder", other);
         }
         if (otherValue.equals(BigInteger.ZERO)) {
             throw getRuntime().newZeroDivisionError();
     }
         return bignorm(getRuntime(), value.remainder(otherValue));
     }
 
     /** rb_big_quo
 
      * 
      */
     public IRubyObject quo(IRubyObject other) {
     	if (other instanceof RubyNumeric) {
             return RubyFloat.newFloat(getRuntime(), big2dbl(this) / ((RubyNumeric) other).getDoubleValue());
         } else {
             return coerceBin("quo", other);
     	}
     }
 
     /** rb_big_pow
      * 
      */
     public IRubyObject pow(IRubyObject other) {
         double d;
         if (other instanceof RubyFixnum) {
             RubyFixnum fix = (RubyFixnum) other;
             long fixValue = fix.getLongValue();
             // MRI issuses warning here on (RBIGNUM(x)->len * SIZEOF_BDIGITS * yy > 1024*1024)
             if (((value.bitLength() + 7) / 8) * 4 * Math.abs(fixValue) > 1024 * 1024) {
                 getRuntime().getWarnings().warn("in a**b, b may be too big");
     	}
             if (fixValue >= 0) {
                 return bignorm(getRuntime(), value.pow((int) fixValue)); // num2int is also implemented
             } else {
                 return RubyFloat.newFloat(getRuntime(), Math.pow(big2dbl(this), (double)fixValue));
             }
         } else if (other instanceof RubyBignum) {
             getRuntime().getWarnings().warn("in a**b, b may be too big");
             d = ((RubyBignum) other).getDoubleValue();
         } else if (other instanceof RubyFloat) {
             d = ((RubyFloat) other).getDoubleValue();
         } else {
             return coerceBin("**", other);
 
     }
         return RubyFloat.newFloat(getRuntime(), Math.pow(big2dbl(this), d));
     }
 
     /** rb_big_and
      * 
      */
     public IRubyObject and(IRubyObject other) {
         other = other.convertToInteger();
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.and(((RubyBignum) other).value));
         } else if(other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.and(fix2big((RubyFixnum)other)));
         }
         return coerceBin("&", other);
     }
 
     /** rb_big_or
      * 
      */
     public IRubyObject or(IRubyObject other) {
         other = other.convertToInteger();
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.or(((RubyBignum) other).value));
         }
         if (other instanceof RubyFixnum) { // no bignorm here needed
             return bignorm(getRuntime(), value.or(fix2big((RubyFixnum)other)));
         }
         return coerceBin("|", other);
     }
 
     /** rb_big_xor
      * 
      */
     public IRubyObject xor(IRubyObject other) {
         other = other.convertToInteger();
         if (other instanceof RubyBignum) {
             return bignorm(getRuntime(), value.xor(((RubyBignum) other).value));
 		}
         if (other instanceof RubyFixnum) {
             return bignorm(getRuntime(), value.xor(BigInteger.valueOf(((RubyFixnum) other).getLongValue())));
     }
 
         return coerceBin("^", other);
     }
 
     /** rb_big_neg     
      * 
      */
     public IRubyObject neg() {
         return RubyBignum.newBignum(getRuntime(), value.not());
     	}
 
     /** rb_big_lshift     
      * 
      */
     public IRubyObject lshift(IRubyObject other) {
         int width = num2int(other);
         if (width < 0) {
             return rshift(RubyFixnum.newFixnum(getRuntime(), -width));
         }
     	
         return bignorm(getRuntime(), value.shiftLeft(width));
     }
 
     /** rb_big_rshift     
      * 
      */
     public IRubyObject rshift(IRubyObject other) {
         int width = num2int(other);
 
         if (width < 0) {
             return lshift(RubyFixnum.newFixnum(getRuntime(), -width));
         }
         return bignorm(getRuntime(), value.shiftRight(width));
     }
 
     /** rb_big_aref     
      * 
      */
     public RubyFixnum aref(IRubyObject other) {
         if (other instanceof RubyBignum) {
             if (((RubyBignum) other).value.signum() >= 0 || value.signum() == -1) {
                 return RubyFixnum.zero(getRuntime());
             }
             return RubyFixnum.one(getRuntime());
         }
         int position = num2int(other);
         if (position < 0) {
             return RubyFixnum.zero(getRuntime());
         }
         
         return value.testBit(num2int(other)) ? RubyFixnum.one(getRuntime()) : RubyFixnum.zero(getRuntime());
     }
 
     /** rb_big_cmp     
      * 
      */
     public IRubyObject cmp(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else if (other instanceof RubyFloat) {
             return dbl_cmp(getRuntime(), big2dbl(this), ((RubyFloat) other).getDoubleValue());
         } else {
             return coerceCmp("<=>", other);
         }
 
         // wow, the only time we can use the java protocol ;)        
         return RubyFixnum.newFixnum(getRuntime(), value.compareTo(otherValue));
     }
 
     /** rb_big_eq     
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         final BigInteger otherValue;
         if (other instanceof RubyFixnum) {
             otherValue = fix2big((RubyFixnum) other);
         } else if (other instanceof RubyBignum) {
             otherValue = ((RubyBignum) other).value;
         } else if (other instanceof RubyFloat) {
             double a = ((RubyFloat) other).getDoubleValue();
             if (Double.isNaN(a)) {
                 return getRuntime().getFalse();
             }
             return RubyBoolean.newBoolean(getRuntime(), a == big2dbl(this));
         } else {
             return super.equal(other);
         }
         return RubyBoolean.newBoolean(getRuntime(), value.compareTo(otherValue) == 0);
     }
 
     /** rb_big_eql     
      * 
      */
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyBignum) {
-            return RubyBoolean.newBoolean(getRuntime(), value.compareTo(((RubyBignum)other).value) == 0);
+            return value.compareTo(((RubyBignum)other).value) == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         return getRuntime().getFalse();
     }
 
     /** rb_big_hash
      * 
      */
     public RubyFixnum hash() {
         return getRuntime().newFixnum(value.hashCode());
         }
 
     /** rb_big_to_f
      * 
      */
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(), getDoubleValue());
     }
 
     /** rb_big_abs
      * 
      */
     public IRubyObject abs() {
         return RubyBignum.newBignum(getRuntime(), value.abs());
     }
 
     /** rb_big_size
      * 
      */
     public RubyFixnum size() {
         return getRuntime().newFixnum((value.bitLength() + 7) / 8);
     }
 
     public static void marshalTo(RubyBignum bignum, MarshalStream output) throws IOException {
         output.write(bignum.value.signum() >= 0 ? '+' : '-');
         
         BigInteger absValue = bignum.value.abs();
         
         byte[] digits = absValue.toByteArray();
         
         boolean oddLengthNonzeroStart = (digits.length % 2 != 0 && digits[0] != 0);
         int shortLength = digits.length / 2;
         if (oddLengthNonzeroStart) {
             shortLength++;
         }
         output.writeInt(shortLength);
         
         for (int i = 1; i <= shortLength * 2 && i <= digits.length; i++) {
             output.write(digits[digits.length - i]);
         }
         
         if (oddLengthNonzeroStart) {
             // Pad with a 0
             output.write(0);
         }
     }
 
     public static RubyNumeric unmarshalFrom(UnmarshalStream input) throws IOException {
         boolean positive = input.readUnsignedByte() == '+';
         int shortLength = input.unmarshalInt();
 
         // BigInteger required a sign byte in incoming array
         byte[] digits = new byte[shortLength * 2 + 1];
 
         for (int i = digits.length - 1; i >= 1; i--) {
         	digits[i] = input.readSignedByte();
         }
 
         BigInteger value = new BigInteger(digits);
         if (!positive) {
             value = value.negate();
         }
 
         RubyNumeric result = bignorm(input.getRuntime(), value);
         input.registerLinkTarget(result);
         return result;
     }
 }
diff --git a/src/org/jruby/RubyFixnum.java b/src/org/jruby/RubyFixnum.java
index 56b1d61479..6af579fa1a 100644
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
@@ -1,753 +1,760 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Antti Karanta <antti.karanta@napa.fi>
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
 
 import java.math.BigInteger;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Convert;
 
 /** Implementation of the Fixnum class.
  *
  * @author jpetersen
  */
 public class RubyFixnum extends RubyInteger {
     
     public static RubyClass createFixnumClass(Ruby runtime) {
         RubyClass fixnum = runtime.defineClass("Fixnum", runtime.getClass("Integer"),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         fixnum.index = ClassIndex.FIXNUM;
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyFixnum.class);
 
         fixnum.includeModule(runtime.getModule("Precision"));
         fixnum.getMetaClass().defineFastMethod("induced_from", callbackFactory.getFastSingletonMethod(
                 "induced_from", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("to_s", callbackFactory.getFastOptMethod("to_s"));
 
         fixnum.defineFastMethod("id2name", callbackFactory.getFastMethod("id2name"));
         fixnum.defineFastMethod("to_sym", callbackFactory.getFastMethod("to_sym"));
 
         fixnum.defineFastMethod("-@", callbackFactory.getFastMethod("uminus"));
         fixnum.defineFastMethod("+", callbackFactory.getFastMethod("plus", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("-", callbackFactory.getFastMethod("minus", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("*", callbackFactory.getFastMethod("mul", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("/", callbackFactory.getFastMethod("div_slash", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("div", callbackFactory.getFastMethod("div_div", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("%", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("modulo", callbackFactory.getFastMethod("mod", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("divmod", callbackFactory.getFastMethod("divmod", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("quo", callbackFactory.getFastMethod("quo", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("**", callbackFactory.getFastMethod("pow", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("abs", callbackFactory.getFastMethod("abs"));
 
         fixnum.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<=>", callbackFactory.getFastMethod("cmp", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod(">", callbackFactory.getFastMethod("gt", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod(">=", callbackFactory.getFastMethod("ge", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<", callbackFactory.getFastMethod("lt", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<=", callbackFactory.getFastMethod("le", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("~", callbackFactory.getFastMethod("rev"));
         fixnum.defineFastMethod("&", callbackFactory.getFastMethod("and", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("|", callbackFactory.getFastMethod("or", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("^", callbackFactory.getFastMethod("xor", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod("<<", callbackFactory.getFastMethod("lshift", RubyKernel.IRUBY_OBJECT));
         fixnum.defineFastMethod(">>", callbackFactory.getFastMethod("rshift", RubyKernel.IRUBY_OBJECT));
 
         fixnum.defineFastMethod("to_f", callbackFactory.getFastMethod("to_f"));
         fixnum.defineFastMethod("size", callbackFactory.getFastMethod("size"));
         fixnum.defineFastMethod("zero?", callbackFactory.getFastMethod("zero_p"));
 
         return fixnum;
     }    
     
     private long value;
     private static final int BIT_SIZE = 64;
     private static final long SIGN_BIT = (1L << (BIT_SIZE - 1));
     public static final long MAX = (1L<<(BIT_SIZE - 1)) - 1;
     public static final long MIN = -1 * MAX - 1;
     public static final long MAX_MARSHAL_FIXNUM = (1L << 30) - 1; // 0x3fff_ffff
     public static final long MIN_MARSHAL_FIXNUM = - (1L << 30);   // -0x4000_0000
 
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_MINUS_SWITCHVALUE = 2;
     public static final byte OP_LT_SWITCHVALUE = 3;
     public static final byte TO_S_SWITCHVALUE = 4;
     public static final byte TO_I_SWITCHVALUE = 5;
     public static final byte TO_INT_SWITCHVALUE = 6;
     public static final byte HASH_SWITCHVALUE = 7;
     public static final byte OP_GT_SWITCHVALUE = 8;
     public static final byte OP_TIMES_SWITCHVALUE = 9;
     public static final byte EQUALEQUAL_SWITCHVALUE = 10;
     public static final byte OP_LE_SWITCHVALUE = 11;
     public static final byte OP_SPACESHIP_SWITCHVALUE = 12;
 
     public RubyFixnum(Ruby runtime) {
         this(runtime, 0);
     }
 
     public RubyFixnum(Ruby runtime, long value) {
         super(runtime, runtime.getFixnum());
         this.value = value;
     }
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         // If tracing is on, don't do STI dispatch
         if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
         
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return plus(args[0]);
         case OP_MINUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return minus(args[0]);
         case OP_LT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return lt(args[0]);
         case TO_S_SWITCHVALUE:
             return to_s(args);
         case TO_I_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_i();
         case TO_INT_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_int();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case OP_GT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return gt(args[0]);
         case OP_TIMES_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return mul(args[0]);
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case OP_LE_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return le(args[0]);
         case OP_SPACESHIP_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return cmp(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.FIXNUM;
     }
     
+    /** short circuit for Fixnum key comparison
+     * 
+     */
+    public final boolean eql(IRubyObject other) {
+        return other instanceof RubyFixnum && value == ((RubyFixnum)other).value;
+    }
+    
     public boolean isImmediate() {
     	return true;
     }
 
     public Class getJavaClass() {
         return Long.TYPE;
     }
 
     public double getDoubleValue() {
         return value;
     }
 
     public long getLongValue() {
         return value;
     }
 
     public static RubyFixnum newFixnum(Ruby runtime, long value) {
         RubyFixnum fixnum;
         RubyFixnum[] fixnumCache = runtime.getFixnumCache();
 
         if (value >= 0 && value < fixnumCache.length) {
             fixnum = fixnumCache[(int) value];
             if (fixnum == null) {
                 fixnum = new RubyFixnum(runtime, value);
                 fixnumCache[(int) value] = fixnum;
             }
         } else {
             fixnum = new RubyFixnum(runtime, value);
         }
         return fixnum;
     }
 
     public RubyFixnum newFixnum(long newValue) {
         return newFixnum(getRuntime(), newValue);
     }
 
     public static RubyFixnum zero(Ruby runtime) {
         return newFixnum(runtime, 0);
     }
 
     public static RubyFixnum one(Ruby runtime) {
         return newFixnum(runtime, 1);
     }
 
     public static RubyFixnum minus_one(Ruby runtime) {
         return newFixnum(runtime, -1);
     }
 
     public RubyFixnum hash() {
         return newFixnum(hashCode());
     }
 
     public final int hashCode() {
         return (int)(value ^ value >>> 32);
     }
 
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
 
     /** fix_to_s
      * 
      */
     public RubyString to_s(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         
         int base = args.length == 0 ? 10 : num2int(args[0]);
         if (base < 2 || base > 36) {
             throw getRuntime().newArgumentError("illegal radix " + base);
             }
         return getRuntime().newString(Convert.longToByteList(value, base));
         }
 
     /** fix_id2name
      * 
      */
     public IRubyObject id2name() {
         String symbol = RubySymbol.getSymbol(getRuntime(), value);
         if (symbol != null) {
             return getRuntime().newString(symbol);
     }
         return getRuntime().getNil();
     }
 
     /** fix_to_sym
      * 
      */
     public IRubyObject to_sym() {
         String symbol = RubySymbol.getSymbol(getRuntime(), value);
         if (symbol != null) {
             return RubySymbol.newSymbol(getRuntime(), symbol);
     }
         return getRuntime().getNil();
     }
 
     /** fix_uminus
      * 
      */
     public IRubyObject uminus() {
         if (value == MIN) { // a gotcha
             return RubyBignum.newBignum(getRuntime(), BigInteger.valueOf(value).negate());
         }
         return RubyFixnum.newFixnum(getRuntime(), -value);
         }
 
     /** fix_plus
      * 
      */
     public IRubyObject plus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             long result = value + otherValue;
             if ((~(value ^ otherValue) & (value ^ result) & SIGN_BIT) != 0) {
                 return RubyBignum.newBignum(getRuntime(), value).plus(other);
             }
 		return newFixnum(result);
     }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).plus(this);
     }
         if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value + ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("+", other);
     }
 
     /** fix_minus
      * 
      */
     public IRubyObject minus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             long result = value - otherValue;
             if ((~(value ^ ~otherValue) & (value ^ result) & SIGN_BIT) != 0) {
                 return RubyBignum.newBignum(getRuntime(), value).minus(other);
     }
             return newFixnum(result);
         } else if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(getRuntime(), value).minus(other);
         } else if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value - ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("-", other);
     }
 
     /** fix_mul
      * 
      */
     public IRubyObject mul(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             if (value == 0) {
                 return RubyFixnum.zero(getRuntime());
     }
             long result = value * otherValue;
             IRubyObject r = newFixnum(getRuntime(),result);
             if(RubyNumeric.fix2long(r) != result || result/value != otherValue) {
                 return (RubyNumeric) RubyBignum.newBignum(getRuntime(), value).mul(other);
             }
             return r;
         } else if (other instanceof RubyBignum) {
             return ((RubyBignum) other).mul(this);
         } else if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value * ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("*", other);
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
     public IRubyObject div_div(IRubyObject other) {
         return idiv(other, "div");
     	}
     	
     public IRubyObject div_slash(IRubyObject other) {
         return idiv(other, "/");
     }
 
     public IRubyObject idiv(IRubyObject other, String method) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             
             if (y == 0) {
             	throw getRuntime().newZeroDivisionError();
             }
             
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
             }
 
             return getRuntime().newFixnum(div);
         } 
         return coerceBin(method, other);
     }
         
     /** fix_mod
      * 
      */
     public IRubyObject mod(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             // Java / and % are not the same as ruby
             long x = value;
             long y = ((RubyFixnum) other).value;
 
             if (y == 0) {
             	throw getRuntime().newZeroDivisionError();
     }
 
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 mod += y;
             }
                 
             return getRuntime().newFixnum(mod);
 	            }
         return coerceBin("%", other);
     }
                 
     /** fix_divmod
      * 
      */
     public IRubyObject divmod(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             final Ruby runtime = getRuntime();
 
             if (y == 0) {
                 throw runtime.newZeroDivisionError();
                 }
 
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
                 mod += y;
             }
 
             IRubyObject fixDiv = RubyFixnum.newFixnum(getRuntime(), div);
             IRubyObject fixMod = RubyFixnum.newFixnum(getRuntime(), mod);
 
             return RubyArray.newArray(runtime, fixDiv, fixMod);
 
     	}
         return coerceBin("divmod", other);
     }
     	
     /** fix_quo
      * 
      */
     public IRubyObject quo(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyFloat.newFloat(getRuntime(), (double) value
                     / (double) ((RubyFixnum) other).value);
     }
         return coerceBin("quo", other);
 	            }
 
     /** fix_pow 
      * 
      */
     public IRubyObject pow(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             long b = ((RubyFixnum) other).value;
             if (b == 0) {
                 return RubyFixnum.one(getRuntime());
             }
             if (b == 1) {
                 return this;
             }
             if (b > 0) {
                 return RubyBignum.newBignum(getRuntime(), value).pow(other);
             }
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, b));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, ((RubyFloat) other)
                     .getDoubleValue()));
         }
         return coerceBin("**", other);
     }
             
     /** fix_abs
      * 
      */
     public IRubyObject abs() {
         if (value < 0) {
             return RubyFixnum.newFixnum(getRuntime(), -value);
             }
         return this;
     }
             
     /** fix_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value == ((RubyFixnum) other).value);
         }
         return super.equal(other);
             }
 
     /** fix_cmp
      * 
      */
     public IRubyObject cmp(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             if (value == otherValue) {
                 return RubyFixnum.zero(getRuntime());
     }
             if (value > otherValue) {
                 return RubyFixnum.one(getRuntime());
             }
             return RubyFixnum.minus_one(getRuntime());
         }
         return coerceCmp("<=>", other);
     }
 
     /** fix_gt
      * 
      */
     public IRubyObject gt(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value > ((RubyFixnum) other).value);
     }
         return coerceRelOp(">", other);
     }
 
     /** fix_ge
      * 
      */
     public IRubyObject ge(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value >= ((RubyFixnum) other).value);
             }
         return coerceRelOp(">=", other);
     }
 
     /** fix_lt
      * 
      */
     public IRubyObject lt(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value < ((RubyFixnum) other).value);
         }
         return coerceRelOp("<", other);
     }
         
     /** fix_le
      * 
      */
     public IRubyObject le(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value <= ((RubyFixnum) other).value);
     }
         return coerceRelOp("<=", other);
     }
 
     /** fix_rev
      * 
      */
     public IRubyObject rev() {
         return newFixnum(~value);
     	}
     	
     /** fix_and
      * 
      */
     public IRubyObject and(IRubyObject other) {
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).and(this);
     }
         return RubyFixnum.newFixnum(getRuntime(), value & num2long(other));
     }
 
     /** fix_or 
      * 
      */
     public IRubyObject or(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return newFixnum(value | ((RubyFixnum) other).value);
         }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).or(this);
         }
         if (other instanceof RubyNumeric) {
             return newFixnum(value | ((RubyNumeric) other).getLongValue());
         }
         
         return or(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
     }
 
     /** fix_xor 
      * 
      */
     public IRubyObject xor(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             return newFixnum(value ^ ((RubyFixnum) other).value);
             }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).xor(this);
         }
         if (other instanceof RubyNumeric) {
             return newFixnum(value ^ ((RubyNumeric) other).getLongValue());
         }
 
         return xor(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
         }
 
     /** fix_aref 
      * 
      */
     public IRubyObject aref(IRubyObject other) {
         if(other instanceof RubyBignum) {
             RubyBignum big = (RubyBignum) other;
             RubyObject tryFix = RubyBignum.bignorm(getRuntime(), big.getValue());
             if (!(tryFix instanceof RubyFixnum)) {
                 if (big.getValue().signum() == 0 || value >= 0) {
                     return RubyFixnum.zero(getRuntime());
         }
                 return RubyFixnum.one(getRuntime());
         }
     }
 
         long otherValue = num2long(other);
             
         if (otherValue < 0) {
             return RubyFixnum.zero(getRuntime());
 		    } 
 		      
         if (BIT_SIZE - 1 < otherValue) {
             if (value < 0) {
                 return RubyFixnum.one(getRuntime());
         }
             return RubyFixnum.zero(getRuntime());
         }
         
         return (value & (1L << otherValue)) == 0 ? RubyFixnum.zero(getRuntime()) : RubyFixnum.one(getRuntime());
     }
 
     /** fix_lshift 
      * 
      */
     public IRubyObject lshift(IRubyObject other) {
         long width = num2long(other);
 
             if (width < 0) {
             return rshift(RubyFixnum.newFixnum(getRuntime(), -width));
 		    }
     	
         if (width == 0) {
             return this;
     }
 
         if (width > BIT_SIZE - 1 || ((~0L << BIT_SIZE - width - 1) & value) != 0) {
             return RubyBignum.newBignum(getRuntime(), value).lshift(other);
         }
         
         return newFixnum(value << width);
     }
 
     /** fix_rshift 
      * 
      */
     public IRubyObject rshift(IRubyObject other) {
         long width = num2long(other);
 
         if (width < 0) {
             return lshift(RubyFixnum.newFixnum(getRuntime(), -width));
     }
     
         if (width == 0) {
             return this;
     }
 
         if (width >= BIT_SIZE - 1) {
             if (value < 0) {
                 return RubyFixnum.minus_one(getRuntime());
     }
             return RubyFixnum.zero(getRuntime());
         }
 
         return newFixnum(value >> width);
         }
 
     /** fix_to_f 
      * 
      */
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(), (double) value);
     }
 
     /** fix_size 
      * 
      */
     public IRubyObject size() {
         return newFixnum((long) ((BIT_SIZE + 7) / 8));
         }
 
     /** fix_zero_p 
      * 
      */
     public IRubyObject zero_p() {
         return RubyBoolean.newBoolean(getRuntime(), value == 0);
     }
 
     public RubyFixnum id() {
         return newFixnum(value * 2 + 1);
     }
 
     public IRubyObject taint() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 
     public static RubyFixnum unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         return input.getRuntime().newFixnum(input.unmarshalInt());
     }
 
     /*  ================
      *  Singleton Methods
      *  ================ 
      */
 
     /** rb_fix_induced_from
      * 
      */
 
     public static IRubyObject induced_from(IRubyObject recv, IRubyObject other) {
         return RubyNumeric.num2fix(other);
 }
 }
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 3c554af326..b34e60dd89 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1677 +1,1677 @@
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
 
 import java.io.IOException;
 import java.util.AbstractCollection;
 import java.util.AbstractSet;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.NoSuchElementException;
 import java.util.Set;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /** Implementation of the Hash class.
  *
  * @author  jpetersen
  */
 public class RubyHash extends RubyObject implements Map {
     
     public static RubyClass createHashClass(Ruby runtime) {
         RubyClass hashc = runtime.defineClass("Hash", runtime.getObject(), HASH_ALLOCATOR);
         hashc.index = ClassIndex.HASH;
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyHash.class);
 
         hashc.includeModule(runtime.getModule("Enumerable"));
         hashc.getMetaClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("create"));
 
         hashc.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         hashc.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("rehash", callbackFactory.getFastMethod("rehash"));
 
         hashc.defineFastMethod("to_hash", callbackFactory.getFastMethod("to_hash"));        
         hashc.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         hashc.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));        
         hashc.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
 
         hashc.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("fetch", callbackFactory.getOptMethod("fetch"));
         hashc.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("store", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("default", callbackFactory.getOptMethod("default_value_get"));
         hashc.defineFastMethod("default=", callbackFactory.getFastMethod("default_value_set", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("default_proc", callbackFactory.getMethod("default_proc"));
         hashc.defineFastMethod("index", callbackFactory.getFastMethod("index", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("indexes", callbackFactory.getFastOptMethod("indices"));
         hashc.defineFastMethod("indices", callbackFactory.getFastOptMethod("indices"));
         hashc.defineFastMethod("size", callbackFactory.getFastMethod("rb_size"));
         hashc.defineFastMethod("length", callbackFactory.getFastMethod("rb_size"));        
         hashc.defineFastMethod("empty?", callbackFactory.getFastMethod("empty_p"));
 
         hashc.defineMethod("each", callbackFactory.getMethod("each"));
         hashc.defineMethod("each_value", callbackFactory.getMethod("each_value"));
         hashc.defineMethod("each_key", callbackFactory.getMethod("each_key"));
         hashc.defineMethod("each_pair", callbackFactory.getMethod("each_pair"));        
         hashc.defineMethod("sort", callbackFactory.getMethod("sort"));
 
         hashc.defineFastMethod("keys", callbackFactory.getFastMethod("keys"));
         hashc.defineFastMethod("values", callbackFactory.getFastMethod("rb_values"));
         hashc.defineFastMethod("values_at", callbackFactory.getFastOptMethod("values_at"));
 
         hashc.defineFastMethod("shift", callbackFactory.getFastMethod("shift"));
         hashc.defineMethod("delete", callbackFactory.getMethod("delete", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("delete_if", callbackFactory.getMethod("delete_if"));
         hashc.defineMethod("select", callbackFactory.getOptMethod("select"));
         hashc.defineMethod("reject", callbackFactory.getMethod("reject"));
         hashc.defineMethod("reject!", callbackFactory.getMethod("reject_bang"));
         hashc.defineFastMethod("clear", callbackFactory.getFastMethod("rb_clear"));
         hashc.defineFastMethod("invert", callbackFactory.getFastMethod("invert"));
         hashc.defineMethod("update", callbackFactory.getMethod("update", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("replace", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("merge!", callbackFactory.getMethod("update", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("merge", callbackFactory.getMethod("merge", RubyKernel.IRUBY_OBJECT));
 
         hashc.defineFastMethod("include?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("member?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("has_key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("has_value?", callbackFactory.getFastMethod("has_value", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("value?", callbackFactory.getFastMethod("has_value", RubyKernel.IRUBY_OBJECT));
 
         return hashc;
         }
 
     private final static ObjectAllocator HASH_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyHash(runtime, klass);
         }
     };
 
     public int getNativeTypeIndex() {
         return ClassIndex.HASH;
     }    
 
 
     public static final byte AREF_SWITCHVALUE = 1;
     public static final byte ASET_SWITCHVALUE = 2;
     public static final byte NIL_P_SWITCHVALUE = 3;
     public static final byte EQUALEQUAL_SWITCHVALUE = 4;
     public static final byte EMPTY_P_SWITCHVALUE = 5;
     public static final byte TO_S_SWITCHVALUE = 6;
     public static final byte TO_A_SWITCHVALUE = 7;
     public static final byte HASH_SWITCHVALUE = 8;
     public static final byte LENGTH_SWITCHVALUE = 9;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         // If tracing is on, don't do STI dispatch
         if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
         
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case AREF_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return aref(args[0]);
         case ASET_SWITCHVALUE:
             if (args.length != 2) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 2 + ")");
             return aset(args[0],args[1]);
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case EMPTY_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return empty_p();
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case TO_A_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_a();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case LENGTH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return rb_size();
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
 
 
 
     /** rb_hash_s_create
      * 
      */
     public static IRubyObject create(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass) recv;
         RubyHash hash;
 
         if (args.length == 1 && args[0] instanceof RubyHash) {
             RubyHash otherHash = (RubyHash)args[0];
             return new RubyHash(recv.getRuntime(), klass, otherHash.internalCopyTable(), otherHash.size); // hash_alloc0
         }
 
         if ((args.length & 1) != 0) throw recv.getRuntime().newArgumentError("odd number of args for Hash");
 
         hash = (RubyHash)klass.allocate();
         for (int i=0; i < args.length; i+=2) hash.aset(args[i], args[i+1]);
 
         return hash;
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
     private int size = 0;
     private int threshold;
 
     private int iterLevel = 0;
     private boolean deleted = false;
 
     private boolean procDefault = false;
     private IRubyObject ifNone;
 
     private RubyHash(Ruby runtime, RubyClass klass, RubyHashEntry[]newTable, int newSize) {
         super(runtime, klass);
         this.ifNone = runtime.getNil();
         threshold = INITIAL_THRESHOLD;
         table = newTable;
         size = newSize;
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
 
     // TODO should this be deprecated ? (to be efficient, internals should deal with RubyHash directly) 
     public RubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = runtime.getNil();
         alloc();
 
         for (Iterator iter = valueMap.entrySet().iterator();iter.hasNext();) {
             Map.Entry e = (Map.Entry)iter.next();
             internalPut((IRubyObject)e.getKey(), (IRubyObject)e.getValue());
     }
     }
 
     private final void alloc() {
         threshold = INITIAL_THRESHOLD;
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
 
     static final class RubyHashEntry implements Map.Entry {
         private IRubyObject key; 
         private IRubyObject value; 
         private RubyHashEntry next; 
         private int hash;
 
         RubyHashEntry(int h, IRubyObject k, IRubyObject v, RubyHashEntry e) {
             key = k; value = v; next = e; hash = h;
         }
         public Object getKey() {
             return key;
     }
         public Object getJavaifiedKey(){
             return JavaUtil.convertRubyToJava(key);
         }
         public Object getValue() {
             return value;            
         }
-        public Object getPlainValue() {
+        public Object getJavaifiedValue() {
             return JavaUtil.convertRubyToJava(value);
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
         public boolean equals(Object other){
             if(!(other instanceof RubyHashEntry)) return false;
             RubyHashEntry otherEntry = (RubyHashEntry)other;
-            if(key == otherEntry.key && key != NEVER && key.equals(otherEntry.key)){
+            if(key == otherEntry.key && key != NEVER && key.eql(otherEntry.key)){
                 if(value == otherEntry.value || value.equals(otherEntry.value)) return true;
             }            
             return false;
         }
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
         return (h % length);
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
         if (size > threshold) {
             int oldCapacity = table.length; 
             if (oldCapacity == MAXIMUM_CAPACITY) {
                 threshold = Integer.MAX_VALUE;
                 return;
             }
             int newCapacity = table.length << 1;
             resize(newCapacity);
             threshold = newCapacity - (newCapacity >> 2);
     }
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
     private static boolean MRI_HASH = true; 
     private static boolean MRI_HASH_RESIZE = true;
 
     private static int hashValue(final int h) {
         return MRI_HASH ? MRIHashValue(h) : JavaSoftHashValue(h);
     }
 
     private static int bucketIndex(final int h, final int length) {
         return MRI_HASH ? MRIBucketIndex(h, length) : JavaSoftBucketIndex(h, length); 
     }   
 
     private void checkResize() {
         if (MRI_HASH_RESIZE) MRICheckResize(); else JavaSoftCheckResize();
 	}
     // ------------------------------
     public static long collisions = 0;
 
     private final void internalPut(final IRubyObject key, final IRubyObject value) {
         checkResize();
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
 
         // if (table[i] != null) collisions++;
 
         for (RubyHashEntry entry = table[i]; entry != null; entry = entry.next) {
-            Object k;
-            if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) {
+            IRubyObject k;
+            if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
                 entry.value = value;
                 return;
 	}
         }
 
         table[i] = new RubyHashEntry(hash, key, value, table[i]);
         size++;
     }
 
     private final void internalPutDirect(final IRubyObject key, final IRubyObject value){
         checkResize();
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
         table[i] = new RubyHashEntry(hash, key, value, table[i]);
         size++;
 	}
 
     private final IRubyObject internalGet(IRubyObject key) { // specialized for value
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
-            Object k;
-            if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) return entry.value;
+            IRubyObject k;
+            if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) return entry.value;
 	}
         return null;
     }
 
     private final RubyHashEntry internalGetEntry(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
-            Object k;
-            if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) return entry;
+            IRubyObject k;
+            if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) return entry;
         }
         return null;
     }
 
     private final RubyHashEntry internalDelete(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
         RubyHashEntry entry = table[i];
 
         if (entry == null) return null;
 
         IRubyObject k;
-        if (entry.hash == hash && ((k = entry.key) == key || key.equals(k))) {
+        if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
             table[i] = entry.next;
             size--;
             return entry;
     }
         for (; entry.next != null; entry = entry.next) {
             RubyHashEntry tmp = entry.next;
-            if (tmp.hash == hash && ((k = tmp.key) == key || key.equals(k))) {
+            if (tmp.hash == hash && ((k = tmp.key) == key || key.eql(k))) {
                 entry.next = entry.next.next;
                 size--;
                 return tmp;
             }
         }
         return null;
     }
 
     private final RubyHashEntry internalDeleteSafe(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         RubyHashEntry entry = table[bucketIndex(hash, table.length)];
 
         if (entry == null) return null;
         IRubyObject k;
 
         for (; entry != null; entry = entry.next) {           
-            if (entry.key != NEVER && entry.hash == hash && ((k = entry.key) == key || key.equals(k))) {
+            if (entry.key != NEVER && entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
                 entry.key = NEVER; // make it a skip node 
                 size--;             
                 return entry;
     }
         }
         return null;
     }   
 
     private final RubyHashEntry internalDeleteEntry(RubyHashEntry entry) {
         final int hash = hashValue(entry.key.hashCode());
         final int i = bucketIndex(hash, table.length);
         RubyHashEntry prev = table[i];
         RubyHashEntry e = prev;
         while (e != null){
             RubyHashEntry next = e.next;
             if (e.hash == hash && e.equals(entry)) {
                 size--;
                 if(iterLevel > 0){
                     if (prev == e) table[i] = next; else prev.next = next;
                 } else {
                     e.key = NEVER;
                 }
                 return e;
             }
             prev = e;
             e = next;
         }
         return e;
     }
 
     private final void internalCleanupSafe() { // synchronized ?
         for (int i=0; i < table.length; i++) {
             RubyHashEntry entry = table[i];
             while (entry != null && entry.key == NEVER) table[i] = entry = entry.next;
             if (entry != null) {
                 RubyHashEntry prev = entry;
                 entry = entry.next;
                 while (entry != null) {
                     if (entry.key == NEVER) { 
                         prev.next = entry.next;
                     } else {
                         prev = prev.next;
                     }
                     entry = prev.next;
                 }
             }
         }        
     }
 
     private final RubyHashEntry[] internalCopyTable() {
          RubyHashEntry[]newTable = new RubyHashEntry[table.length];
 
          for (int i=0; i < table.length; i++) {
              for (RubyHashEntry entry = table[i]; entry != null; entry = entry.next) {
                  if (entry.key != NEVER) newTable[i] = new RubyHashEntry(entry.hash, entry.key, entry.value, newTable[i]);
              }
          }
          return newTable;
     }
 
     // flags for callback based interation
     public static final int ST_CONTINUE = 0;    
     public static final int ST_STOP = 1;
     public static final int ST_DELETE = 2;
     public static final int ST_CHECK = 3;
 
     private void rehashOccured(){
         throw getRuntime().newRuntimeError("rehash occurred during iteration");
     }
 
     public static abstract class Callback { // a class to prevent invokeinterface
         public abstract int call(RubyHash hash, RubyHashEntry entry);
     }    
 
     private final int hashForEachEntry(final RubyHashEntry entry, final Callback callback) {
         if (entry.key == NEVER) return ST_CONTINUE;
         RubyHashEntry[]ltable = table;
 		
         int status = callback.call(this, entry);
         
         if (ltable != table) rehashOccured();
         
         switch (status) {
         case ST_DELETE:
             internalDeleteSafe(entry.key);
             deleted = true;
         case ST_CONTINUE:
             break;
         case ST_STOP:
             return ST_STOP;
 	}
         return ST_CHECK;
     }    
 
     private final boolean internalForEach(final Callback callback) {
         RubyHashEntry entry, last, tmp;
         int length = table.length;
         for (int i = 0; i < length; i++) {
             last = null;
             for (entry = table[i]; entry != null;) {
                 switch (hashForEachEntry(entry, callback)) {
                 case ST_CHECK:
                     tmp = null;
                     if (i < length) for (tmp = table[i]; tmp != null && tmp != entry; tmp = tmp.next);
                     if (tmp == null) return true;
                 case ST_CONTINUE:
                     last = entry;
                     entry = entry.next;
                     break;
                 case ST_STOP:
                     return false;
                 case ST_DELETE:
                     tmp = entry;
                     if (last == null) table[i] = entry.next; else last.next = entry.next;
                     entry = entry.next;
                     size--;
                 }
             }
         }
         return false;
     }
 
     public final void forEach(final Callback callback) {
         try{
             preIter();
             if (internalForEach(callback)) rehashOccured();
         }finally{
             postIter();
         }
     }
 
     private final void preIter() {
         iterLevel++;
     }
 
     private final void postIter() {
         iterLevel--;
         if (deleted) {
             internalCleanupSafe();
             deleted = false;
         }
     }
 
     private final RubyHashEntry checkIter(RubyHashEntry[]ltable, RubyHashEntry node) {
         while (node != null && node.key == NEVER) node = node.next;
         if (ltable != table) rehashOccured();
         return node;
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
     public IRubyObject initialize(IRubyObject[] args, final Block block) {
             modify();
 
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments");
             ifNone = getRuntime().newProc(false, block);
             procDefault = true;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 0, 1);
             if (args.length == 1) ifNone = args[0];
         }
         return this;
     }
 
     /** rb_hash_default
      * 
      */
     public IRubyObject default_value_get(IRubyObject[] args, Block unusedBlock) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if (procDefault) {
             if (args.length == 0) return getRuntime().getNil();
             return ifNone.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[]{this, args[0]});
         }
         return ifNone;
     }
 
     /** rb_hash_set_default
      * 
      */
     public IRubyObject default_value_set(final IRubyObject defaultValue) {
         modify();
 
         ifNone = defaultValue;
         procDefault = false;
 
         return ifNone;
     }
 
     /** rb_hash_default_proc
      * 
      */    
     public IRubyObject default_proc(Block unusedBlock) {
         return procDefault ? ifNone : getRuntime().getNil();
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
 
     /** rb_hash_inspect
      * 
      */
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if (!runtime.registerInspecting(this)) {
             return runtime.newString("{...}");
         }
 
         try {
             final String sep = ", ";
             final String arrow = "=>";
             final StringBuffer sb = new StringBuffer("{");
             boolean firstEntry = true;
         
             ThreadContext context = runtime.getCurrentContext();
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (!firstEntry) sb.append(sep);
                     sb.append(entry.key.callMethod(context, "inspect")).append(arrow);
                     sb.append(entry.value.callMethod(context, "inspect"));
                 firstEntry = false;
             }
             }
             sb.append("}");
             return runtime.newString(sb.toString());
         } finally {
             postIter();
             runtime.unregisterInspecting(this);
         }
     }
 
     /** rb_hash_size
      * 
      */    
     public RubyFixnum rb_size() {
         return getRuntime().newFixnum(size);
     }
 
     /** rb_hash_empty_p
      * 
      */
     public RubyBoolean empty_p() {
         return size == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_hash_to_a
      * 
      */
     public RubyArray to_a() {
         Ruby runtime = getRuntime();
         RubyArray result = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {                
                     result.append(RubyArray.newArray(runtime, entry.key, entry.value));
         }
             }
         } finally {postIter();}
 
         result.setTaint(isTaint());
         return result;
     }
 
     /** rb_hash_to_s
      * 
      */
     public IRubyObject to_s() {
         if (!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("{...}");
         }
         try {
             return to_a().to_s();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_hash_rehash
      * 
      */
     public RubyHash rehash() {
         modify();
         final RubyHashEntry[] oldTable = table;
         final RubyHashEntry[] newTable = new RubyHashEntry[oldTable.length];
         for (int j = 0; j < oldTable.length; j++) {
             RubyHashEntry entry = oldTable[j];
             oldTable[j] = null;
             while (entry != null) {    
                 RubyHashEntry next = entry.next;
                 if (entry.key != NEVER) {
                     entry.hash = entry.key.hashCode(); // update the hash value
                     int i = bucketIndex(entry.hash, newTable.length);
                     entry.next = newTable[i];
                     newTable[i] = entry;
         }
                 entry = next;
             }
         }
         table = newTable;
         return this;
     }
 
     /** rb_hash_to_hash
      * 
      */
     public RubyHash to_hash() {
         return this;        
     }
 
     public RubyHash convertToHash() {    
         return this;
     }
 
     public final void fastASet(IRubyObject key, IRubyObject value) {
         internalPut(key, value);
     }
 
     /** rb_hash_aset
      * 
      */
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         modify();
 
         if (!(key instanceof RubyString)) {
             internalPut(key, value);
             return value;
         } 
 
         RubyHashEntry entry = null;        
         if ((entry = internalGetEntry(key)) != null) {
             entry.value = value;
         } else {
           IRubyObject realKey = ((RubyString)key).strDup();
             realKey.setFrozen(true);
           internalPutDirect(realKey, value);
         }
 
         return value;
     }
 
     public final IRubyObject fastARef(IRubyObject key) { // retuns null when not found to avoid unnecessary getRuntime().getNil() call
         return internalGet(key);
     }
 
     /** rb_hash_aref
      * 
      */
     public IRubyObject aref(IRubyObject key) {        
         IRubyObject value;        
         return ((value = internalGet(key)) == null) ? callMethod(getRuntime().getCurrentContext(), "default", key) : value;        
     }
 
     /** rb_hash_fetch
      * 
      */
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2 && block.isGiven()) {
             getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
         IRubyObject value;
         if ((value = internalGet(args[0])) == null) {
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), args[0]);
             if (args.length == 1) throw getRuntime().newIndexError("key not found");
             return args[1];
         }
         return value;
     }
 
     /** rb_hash_has_key
      * 
      */
     public RubyBoolean has_key(IRubyObject key) {
         return internalGetEntry(key) == null ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     /** rb_hash_has_value
      * 
      */
     public RubyBoolean has_value(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, value).isTrue()) return runtime.getTrue();
     }
             }
         } finally {postIter();}
         return runtime.getFalse();
     }
 
     /** rb_hash_each
      * 
      */
 	public RubyHash each(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     // rb_assoc_new equivalent
                     block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, false);
 	}
             }
         } finally {postIter();}
 
         return this;
     }
 
     /** rb_hash_each_pair
      * 
      */
 	public RubyHash each_pair(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     // rb_yield_values(2,...) equivalent
                     block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, true);                    
         }
     }
         } finally {postIter();}
 
         return this;	
 	}
 
     /** rb_hash_each_value
      * 
      */
     public RubyHash each_value(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.value);
 		}
 	}
         } finally {postIter();}
 
         return this;        
 	}
 
     /** rb_hash_each_key
      * 
      */
 	public RubyHash each_key(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.key);
 		}
 	}
         } finally {postIter();}
 
         return this;  
 	}
 
     /** rb_hash_sort
      * 
      */
 	public RubyArray sort(Block block) {
 		return to_a().sort_bang(block);
 	}
 
     /** rb_hash_index
      * 
      */
     public IRubyObject index(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, value).isTrue()) return entry.key;
             }
         }
         } finally {postIter();}
 
         return getRuntime().getNil();        
     }
 
     /** rb_hash_indexes
      * 
      */
     public RubyArray indices(IRubyObject[] indices) {
         RubyArray values = RubyArray.newArray(getRuntime(), indices.length);
 
         for (int i = 0; i < indices.length; i++) {
             values.append(aref(indices[i]));
         }
 
         return values;
     }
 
     /** rb_hash_keys 
      * 
      */
     public RubyArray keys() {
         Ruby runtime = getRuntime();
         RubyArray keys = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     keys.append(entry.key);
     }
             }
         } finally {postIter();}
 
         return keys;          
     }
 
     /** rb_hash_values
      * 
      */
     public RubyArray rb_values() {
         RubyArray values = RubyArray.newArray(getRuntime(), size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     values.append(entry.value);
     }
             }
         } finally {postIter();}
 
         return values;
     }
 
     /** rb_hash_equal
      * 
      */
 
     private static final boolean EQUAL_CHECK_DEFAULT_VALUE = false; 
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other ) return getRuntime().getTrue();
         if (!(other instanceof RubyHash)) {
             if (!other.respondsTo("to_hash")) return getRuntime().getFalse();
             return other.equalInternal(getRuntime().getCurrentContext(), this);
         }
 
         RubyHash otherHash = (RubyHash)other;
         if (size != otherHash.size) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();        
         ThreadContext context = runtime.getCurrentContext();
 
         if (EQUAL_CHECK_DEFAULT_VALUE) {
             if (!ifNone.equalInternal(context, otherHash.ifNone).isTrue() &&
                procDefault != otherHash.procDefault) return runtime.getFalse();
             }
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     IRubyObject value = otherHash.internalGet(entry.key);
                     if (value == null) return runtime.getFalse();
                     if (!entry.value.equalInternal(context, value).isTrue()) return runtime.getFalse();
         }
     }
         } finally {postIter();}        
 
         return runtime.getTrue();
     }
 
     /** rb_hash_shift
      * 
      */
     public IRubyObject shift() {
 		modify();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     RubyArray result = RubyArray.newArray(getRuntime(), entry.key, entry.value);
                     internalDeleteSafe(entry.key);
                     deleted = true;
                     return result;
     }
             }
         } finally {postIter();}          
 
         if (procDefault) return ifNone.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[]{this, getRuntime().getNil()});
         return ifNone;
     }
 
     /** rb_hash_delete
      * 
      */
 	public IRubyObject delete(IRubyObject key, Block block) {
 		modify();
 
         RubyHashEntry entry;
         if (iterLevel > 0) {
             if ((entry = internalDeleteSafe(key)) != null) {
                 deleted = true;
                 return entry.value;                
             }
         } else if ((entry = internalDelete(key)) != null) return entry.value;
 
 		if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key);
         return getRuntime().getNil();
     }
 
     /** rb_hash_select
      * 
      */
     public IRubyObject select(IRubyObject[] args, Block block) {
         if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 0)");
         RubyArray result = getRuntime().newArray();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (block.yield(context, runtime.newArray(entry.key, entry.value)).isTrue())
                         result.append(runtime.newArray(entry.key, entry.value));
 	}
             }
         } finally {postIter();}
         return result;
     }
 
     /** rb_hash_delete_if
      * 
      */
 	public RubyHash delete_if(Block block) {
         modify();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, true).isTrue())
                         delete(entry.key, block);
                 }
             }
         } finally {postIter();}        
 
 		return this;
 	}
 
     /** rb_hash_reject
      * 
      */
 	public RubyHash reject(Block block) {
         return ((RubyHash)dup()).delete_if(block);
 	}
 
     /** rb_hash_reject_bang
      * 
      */
 	public IRubyObject reject_bang(Block block) {
         int n = size;
         delete_if(block);
         if (n == size) return getRuntime().getNil();
         return this;
 			}
 
     /** rb_hash_clear
      * 
      */
 	public RubyHash rb_clear() {
 		modify();
 
         if (size > 0) { 
             alloc();
             size = 0;
             deleted = false;
 	}
 
 		return this;
 	}
 
     /** rb_hash_invert
      * 
      */
 	public RubyHash invert() {
 		RubyHash result = newHash(getRuntime());
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     result.aset(entry.value, entry.key);
 		}
 	}
         } finally {postIter();}        
 
         return result;        
 	}
 
     /** rb_hash_update
      * 
      */
     public RubyHash update(IRubyObject other, Block block) {
         modify();
 
         RubyHash otherHash = other.convertToHash();
 
         try {
              otherHash.preIter();
              RubyHashEntry[]ltable = otherHash.table;
         if (block.isGiven()) {
                  Ruby runtime = getRuntime();
                  ThreadContext context = runtime.getCurrentContext();
 
                  for (int i = 0; i < ltable.length; i++) {
                      for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                          IRubyObject value;
                          if (internalGet(entry.key) != null)
                              value = block.yield(context, RubyArray.newArrayNoCopy(runtime, new IRubyObject[]{entry.key, aref(entry.key), entry.value}));
                          else
                              value = entry.value;
                          aset(entry.key, value);
                 }
             }
             } else { 
                 for (int i = 0; i < ltable.length; i++) {
                     for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                         aset(entry.key, entry.value);
         }
                 }
             }  
         } finally {otherHash.postIter();}
 
         return this;
     }
 
     /** rb_hash_merge
      * 
      */
     public RubyHash merge(IRubyObject other, Block block) {
         return ((RubyHash)dup()).update(other, block);
     }
 
     /** rb_hash_replace
      * 
      */
     public RubyHash replace(IRubyObject other) {
         RubyHash otherHash = other.convertToHash();
 
         if (this == otherHash) return this;
 
         rb_clear();
 
         try {
             otherHash.preIter();
             RubyHashEntry[]ltable = otherHash.table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                     aset(entry.key, entry.value);
                 }
             }
         } finally {otherHash.postIter();}
 
         ifNone = otherHash.ifNone;
         procDefault = otherHash.procDefault;
 
         return this;
     }
 
     /** rb_hash_values_at
      * 
      */
     public RubyArray values_at(IRubyObject[] args) {
         RubyArray result = RubyArray.newArray(getRuntime(), args.length);
         for (int i = 0; i < args.length; i++) {
             result.append(aref(args[i]));
         }
         return result;
     }
 
     public boolean hasDefaultProc() {
         return procDefault;
     }
 
     public IRubyObject getIfNone(){
         return ifNone;
     }
 
     // FIXME:  Total hack to get flash in Rails marshalling/unmarshalling in session ok...We need
     // to totally change marshalling to work with overridden core classes.
     public static void marshalTo(RubyHash hash, MarshalStream output) throws IOException {
         output.writeInt(hash.size);
         try {
             hash.preIter();
             RubyHashEntry[]ltable = hash.table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = hash.checkIter(ltable, entry)) != null; entry = entry.next) {
                     output.dumpObject(entry.key);
                     output.dumpObject(entry.value);
         }
         }
         } finally {hash.postIter();}         
 
         if (!hash.ifNone.isNil()) output.dumpObject(hash.ifNone);
     }
 
     public static RubyHash unmarshalFrom(UnmarshalStream input, boolean defaultValue) throws IOException {
         RubyHash result = newHash(input.getRuntime());
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.aset(input.unmarshalObject(), input.unmarshalObject());
         }
         if (defaultValue) result.default_value_set(input.unmarshalObject());
         return result;
     }
 
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
 		return internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)) != null;
 	}
 
 	public boolean containsValue(Object value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 		IRubyObject element = JavaUtil.convertJavaToRuby(runtime, value);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, element).isTrue()) return true;
 			}
 		}
         } finally {postIter();}        
 
 		return false;
 	}
 
 	public Object get(Object key) {
 		return JavaUtil.convertRubyToJava(internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)));
 	}
 
 	public Object put(Object key, Object value) {
 		internalPut(JavaUtil.convertJavaToRuby(getRuntime(), key), JavaUtil.convertJavaToRuby(getRuntime(), value));
         return value;
 	}
 
 	public Object remove(Object key) {
         IRubyObject rubyKey = JavaUtil.convertJavaToRuby(getRuntime(), key);
         RubyHashEntry entry;
         if (iterLevel > 0) {
             entry = internalDeleteSafe(rubyKey);
             deleted = true;
         } else {
             entry = internalDelete(rubyKey);
 	}
 		 
         return entry != null ? entry.value : null;
 	}
 
 	public void putAll(Map map) {
         Ruby runtime = getRuntime();
 		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
 			Object key = iter.next();
 			internalPut(JavaUtil.convertJavaToRuby(runtime, key), JavaUtil.convertJavaToRuby(runtime, map.get(key))); 
 		}
 	}
 
 	public void clear() {
         rb_clear();
 	}
 
     private abstract class RubyHashIterator implements Iterator {
         RubyHashEntry entry, current;
         int index;
         RubyHashEntry[]iterTable;
         Ruby runtime = getRuntime();
         
         public RubyHashIterator(){
             iterTable = table;
             if(size > 0) seekNextValidEntry();
 	}
 
         private final void seekNextValidEntry(){
             do {
                 while (index < iterTable.length && (entry = iterTable[index++]) == null);
                 while (entry != null && entry.key == NEVER) entry = entry.next;
             } while (entry == null && index < iterTable.length);
 	}
 
         public boolean hasNext() {
             return entry != null;
 	}
 
         public final RubyHashEntry nextEntry() {
             if (entry == null) throw new NoSuchElementException();
             RubyHashEntry e = current = entry;
             if ((entry = checkIter(iterTable, entry.next)) == null) seekNextValidEntry(); 
             return e;
         }
 
         public void remove() {
             if (current == null) throw new IllegalStateException();
             internalDeleteSafe(current.key);
             deleted = true;
         }
         
     }
 
     private final class KeyIterator extends RubyHashIterator {
 					public Object next() {
             return JavaUtil.convertRubyToJava(nextEntry().key);
 					}
 			}
 
     private class KeySet extends AbstractSet {
         public Iterator iterator() {
             return new KeyIterator();
         }
 			public int size() {
             return size;
 			}
         public boolean contains(Object o) {
             return containsKey(o);
 			}
         public boolean remove(Object o) {
             return RubyHash.this.remove(o) != null;
 	}
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
 	public Set keySet() {
         return new KeySet();
 					}
 
     private final class DirectKeyIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry().key;
 			}
 	}	
 
     private final class DirectKeySet extends KeySet {
         public Iterator iterator() {
             return new DirectKeyIterator();
         }        
 		}
 
     public Set directKeySet() {
         return new DirectKeySet();
 		}
 
     private final class ValueIterator extends RubyHashIterator {
 		public Object next() {
             return JavaUtil.convertRubyToJava(nextEntry().value);
 		}
 		}		
 
     private class Values extends AbstractCollection {
         public Iterator iterator() {
             return new ValueIterator();
         }
         public int size() {
             return size;
         }
         public boolean contains(Object o) {
             return containsValue(o);
             }
         public void clear() {
             RubyHash.this.clear();
         }
             }
 
     public Collection values() {
         return new Values();
         }
 
     private final class DirectValueIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry().value;
 		}
         }
 
     private final class DirectValues extends Values {
         public Iterator iterator() {
             return new DirectValueIterator();
         }        
     }
 
     public Collection directValues() {
         return new DirectValues();
     }    
 
     static final class ConversionMapEntry implements Map.Entry {
         private final RubyHashEntry entry;
         private final Ruby runtime;
 
         public ConversionMapEntry(Ruby runtime, RubyHashEntry entry) {
             this.entry = entry;
             this.runtime = runtime;
         }
 
 				public Object getKey() {
             return JavaUtil.convertRubyToJava(entry.key, Object.class); 
 				}
 
 				public Object getValue() {
             return JavaUtil.convertRubyToJava(entry.value, Object.class);
 				}
 
         public Object setValue(Object value) {
             return entry.value = JavaUtil.convertJavaToRuby(runtime, value);            
 				}
 
         public boolean equals(Object other){
             if(!(other instanceof RubyHashEntry)) return false;
             RubyHashEntry otherEntry = (RubyHashEntry)other;
-            if(entry.key != NEVER && entry.key == otherEntry.key && entry.key.equals(otherEntry.key)){
+            if(entry.key != NEVER && entry.key == otherEntry.key && entry.key.eql(otherEntry.key)){
                 if(entry.value == otherEntry.value || entry.value.equals(otherEntry.value)) return true;
             }            
             return false;
 		}
         public int hashCode(){
             return entry.hashCode();
         }
     }    
 
     private final class EntryIterator extends RubyHashIterator {
         public Object next() {
             return new ConversionMapEntry(runtime, nextEntry());
         }
     }
 
     private final class EntrySet extends AbstractSet {
         public Iterator iterator() {
             return new EntryIterator();
         }
         public boolean contains(Object o) {
             if (!(o instanceof ConversionMapEntry))
                 return false;
             ConversionMapEntry entry = (ConversionMapEntry)o;
             if (entry.entry.key == NEVER) return false;
             RubyHashEntry candidate = internalGetEntry(entry.entry.key);
             return candidate != null && candidate.equals(entry.entry);
         }
         public boolean remove(Object o) {
             if (!(o instanceof ConversionMapEntry)) return false;
             return internalDeleteEntry(((ConversionMapEntry)o).entry) != null;
         }
         public int size() {
             return size;
         }
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
     public Set entrySet() {
         return new EntrySet();
     }    
 
     private final class DirectEntryIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry();
         }
     }    
 
     private final class DirectEntrySet extends AbstractSet {
         public Iterator iterator() {
             return new DirectEntryIterator();
         }
         public boolean contains(Object o) {
             if (!(o instanceof RubyHashEntry))
                 return false;
             RubyHashEntry entry = (RubyHashEntry)o;
             if (entry.key == NEVER) return false;
             RubyHashEntry candidate = internalGetEntry(entry.key);
             return candidate != null && candidate.equals(entry);
     }
         public boolean remove(Object o) {
             if (!(o instanceof RubyHashEntry)) return false;
             return internalDeleteEntry((RubyHashEntry)o) != null;
         }
         public int size() {
             return size;
         }
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
     /** return an entry set who's entries do not convert their values, faster
      * 
      */
     public Set directEntrySet() {
         return new DirectEntrySet();
     }       
 
     public boolean equals(Object other){
         if (!(other instanceof RubyHash)) return false;
         if (this == other) return true;
         return equal((RubyHash)other).isTrue() ? true : false;
         }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 93cead45f1..6b7045817d 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1035 +1,1036 @@
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
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.math.BigInteger;
 import java.util.Calendar;
 import java.util.Iterator;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.Sprintf;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public final static Class IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineFastModuleFunction("Array", callbackFactory.getFastSingletonMethod("new_array", IRUBY_OBJECT));
         module.defineFastModuleFunction("Float", callbackFactory.getFastSingletonMethod("new_float", IRUBY_OBJECT));
         module.defineFastModuleFunction("Integer", callbackFactory.getFastSingletonMethod("new_integer", IRUBY_OBJECT));
         module.defineFastModuleFunction("String", callbackFactory.getFastSingletonMethod("new_string", IRUBY_OBJECT));
         module.defineFastModuleFunction("`", callbackFactory.getFastSingletonMethod("backquote", IRUBY_OBJECT));
         module.defineFastModuleFunction("abort", callbackFactory.getFastOptSingletonMethod("abort"));
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineFastModuleFunction("autoload", callbackFactory.getFastSingletonMethod("autoload", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastModuleFunction("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", IRUBY_OBJECT));
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("callcc", callbackFactory.getOptSingletonMethod("callcc"));
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRUBY_OBJECT));
         module.defineFastModuleFunction("chomp", callbackFactory.getFastOptSingletonMethod("chomp"));
         module.defineFastModuleFunction("chomp!", callbackFactory.getFastOptSingletonMethod("chomp_bang"));
         module.defineFastModuleFunction("chop", callbackFactory.getFastSingletonMethod("chop"));
         module.defineFastModuleFunction("chop!", callbackFactory.getFastSingletonMethod("chop_bang"));
         module.defineModuleFunction("eval", callbackFactory.getOptSingletonMethod("eval"));
         module.defineFastModuleFunction("exit", callbackFactory.getFastOptSingletonMethod("exit"));
         module.defineFastModuleFunction("exit!", callbackFactory.getFastOptSingletonMethod("exit_bang"));
         module.defineModuleFunction("fail", callbackFactory.getOptSingletonMethod("raise"));
         // TODO: Implement Kernel#fork
         module.defineFastModuleFunction("format", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("gets", callbackFactory.getFastOptSingletonMethod("gets"));
         module.defineFastModuleFunction("global_variables", callbackFactory.getFastSingletonMethod("global_variables"));
         module.defineModuleFunction("gsub", callbackFactory.getOptSingletonMethod("gsub"));
         module.defineModuleFunction("gsub!", callbackFactory.getOptSingletonMethod("gsub_bang"));
         // TODO: Add deprecation to Kernel#iterator? (maybe formal deprecation mech.)
         module.defineModuleFunction("iterator?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("lambda", callbackFactory.getSingletonMethod("proc"));
         module.defineModuleFunction("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineFastModuleFunction("local_variables", callbackFactory.getFastSingletonMethod("local_variables"));
         module.defineModuleFunction("loop", callbackFactory.getSingletonMethod("loop"));
         // Note: method_missing is documented as being in Object, but ruby appears to stick it in Kernel.
         module.defineModuleFunction("method_missing", callbackFactory.getOptSingletonMethod("method_missing"));
         module.defineModuleFunction("open", callbackFactory.getOptSingletonMethod("open"));
         module.defineFastModuleFunction("p", callbackFactory.getFastOptSingletonMethod("p"));
         module.defineFastModuleFunction("print", callbackFactory.getFastOptSingletonMethod("print"));
         module.defineFastModuleFunction("printf", callbackFactory.getFastOptSingletonMethod("printf"));
         module.defineModuleFunction("proc", callbackFactory.getSingletonMethod("proc"));
         // TODO: implement Kernel#putc
         module.defineFastModuleFunction("putc", callbackFactory.getFastSingletonMethod("putc", IRubyObject.class));
         module.defineFastModuleFunction("puts", callbackFactory.getFastOptSingletonMethod("puts"));
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineFastModuleFunction("rand", callbackFactory.getFastOptSingletonMethod("rand"));
         module.defineFastModuleFunction("readline", callbackFactory.getFastOptSingletonMethod("readline"));
         module.defineFastModuleFunction("readlines", callbackFactory.getFastOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRUBY_OBJECT));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRUBY_OBJECT));
         module.defineFastModuleFunction("select", callbackFactory.getFastOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRUBY_OBJECT));
         module.defineFastModuleFunction("sleep", callbackFactory.getFastSingletonMethod("sleep", IRUBY_OBJECT));
         module.defineFastModuleFunction("split", callbackFactory.getFastOptSingletonMethod("split"));
         module.defineFastModuleFunction("sprintf", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("srand", callbackFactory.getFastOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineFastModuleFunction("system", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineFastModuleFunction("exec", callbackFactory.getFastOptSingletonMethod("system"));
         module.defineFastModuleFunction("test", callbackFactory.getFastOptSingletonMethod("test"));
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineFastModuleFunction("warn", callbackFactory.getFastSingletonMethod("warn", IRUBY_OBJECT));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_removed", callbackFactory.getSingletonMethod("singleton_method_removed", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_undefined", callbackFactory.getSingletonMethod("singleton_method_undefined", IRUBY_OBJECT));
         
         // Object methods
         module.defineFastPublicModuleFunction("==", objectCallbackFactory.getFastMethod("obj_equal", IRUBY_OBJECT));
+        module.defineFastPublicModuleFunction("eql?", objectCallbackFactory.getFastMethod("obj_equal", IRUBY_OBJECT));
+        module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getFastMethod("obj_equal", IRUBY_OBJECT));
+
         module.defineFastPublicModuleFunction("===", objectCallbackFactory.getFastMethod("equal", IRUBY_OBJECT));
 
-        module.defineAlias("eql?", "==");
         module.defineFastPublicModuleFunction("to_s", objectCallbackFactory.getFastMethod("to_s"));
         module.defineFastPublicModuleFunction("nil?", objectCallbackFactory.getFastMethod("nil_p"));
         module.defineFastPublicModuleFunction("to_a", callbackFactory.getFastSingletonMethod("to_a"));
         module.defineFastPublicModuleFunction("hash", objectCallbackFactory.getFastMethod("hash"));
         module.defineFastPublicModuleFunction("id", objectCallbackFactory.getFastMethod("id_deprecated"));
         module.defineFastPublicModuleFunction("object_id", objectCallbackFactory.getFastMethod("id"));
         module.defineAlias("__id__", "object_id");
         module.defineFastPublicModuleFunction("is_a?", objectCallbackFactory.getFastMethod("kind_of", IRUBY_OBJECT));
         module.defineAlias("kind_of?", "is_a?");
         module.defineFastPublicModuleFunction("dup", objectCallbackFactory.getFastMethod("dup"));
-        module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getFastMethod("same", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("type", objectCallbackFactory.getFastMethod("type_deprecated"));
         module.defineFastPublicModuleFunction("class", objectCallbackFactory.getFastMethod("type"));
         module.defineFastPublicModuleFunction("inspect", objectCallbackFactory.getFastMethod("inspect"));
         module.defineFastPublicModuleFunction("=~", objectCallbackFactory.getFastMethod("match", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("clone", objectCallbackFactory.getFastMethod("rbClone"));
         module.defineFastPublicModuleFunction("display", objectCallbackFactory.getFastOptMethod("display"));
         module.defineFastPublicModuleFunction("extend", objectCallbackFactory.getFastOptMethod("extend"));
         module.defineFastPublicModuleFunction("freeze", objectCallbackFactory.getFastMethod("freeze"));
         module.defineFastPublicModuleFunction("frozen?", objectCallbackFactory.getFastMethod("frozen"));
         module.defineFastModuleFunction("initialize_copy", objectCallbackFactory.getFastMethod("initialize_copy", IRUBY_OBJECT));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.defineFastPublicModuleFunction("instance_of?", objectCallbackFactory.getFastMethod("instance_of", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variables", objectCallbackFactory.getFastMethod("instance_variables"));
         module.defineFastPublicModuleFunction("instance_variable_get", objectCallbackFactory.getFastMethod("instance_variable_get", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variable_set", objectCallbackFactory.getFastMethod("instance_variable_set", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("method", objectCallbackFactory.getFastMethod("method", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("methods", objectCallbackFactory.getFastOptMethod("methods"));
         module.defineFastPublicModuleFunction("private_methods", objectCallbackFactory.getFastMethod("private_methods"));
         module.defineFastPublicModuleFunction("protected_methods", objectCallbackFactory.getFastMethod("protected_methods"));
         module.defineFastPublicModuleFunction("public_methods", objectCallbackFactory.getFastOptMethod("public_methods"));
         module.defineFastModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("respond_to?", objectCallbackFactory.getFastOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.defineFastPublicModuleFunction("singleton_methods", objectCallbackFactory.getFastOptMethod("singleton_methods"));
         module.defineFastPublicModuleFunction("taint", objectCallbackFactory.getFastMethod("taint"));
         module.defineFastPublicModuleFunction("tainted?", objectCallbackFactory.getFastMethod("tainted"));
         module.defineFastPublicModuleFunction("untaint", objectCallbackFactory.getFastMethod("untaint"));
 
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
 
         return module;
     }
 
     public static IRubyObject at_exit(IRubyObject recv, Block block) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(false, block));
     }
 
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         String name = symbol.asSymbol();
         if (recv instanceof RubyModule) {
             name = ((RubyModule)recv).getName() + "::" + name;
         }
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if(autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         final LoadService loadService = recv.getRuntime().getLoadService();
         final String baseName = symbol.asSymbol();
         String nm = baseName;
         if(recv instanceof RubyModule) {
             nm = ((RubyModule)recv).getName() + "::" + nm;
         }
         loadService.addAutoload(nm, new IAutoloadMethod() {
                 public String file() {
                     return file.toString();
                 }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 loadService.require(file.toString());
                 if(recv instanceof RubyModule) {
                     return ((RubyModule)recv).getConstant(baseName);
                 }
                 return runtime.getObject().getConstant(baseName);
             }
         });
         return recv;
     }
 
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = recv.anyToString().toString();
         } else {
             description = recv.inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         // FIXME: Modify sprintf to accept Object[] as well...
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), 
                         runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : recv.getType().getName())
                 })).toString();
         
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
     }
 
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(recv.getRuntime(), args,1,3);
         String arg = args[0].convertToString().toString();
         Ruby runtime = recv.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             try {
                 Process p = new ShellLauncher(runtime).run(RubyString.newString(runtime,command));
                 RubyIO io = new RubyIO(runtime, p);
                 
                 if (block.isGiven()) {
                     try {
                         block.yield(recv.getRuntime().getCurrentContext(), io);
                         return runtime.getNil();
                     } finally {
                         io.close();
                     }
                 }
 
                 return io;
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         } 
 
         return RubyFile.open(runtime.getClass("File"), args, block);
     }
 
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).gets(args);
     }
 
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(Arity.checkArgumentCount(recv.getRuntime(), args,0,1) == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.convertToType(recv.getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary", false, true, true);
         
         if (value.isNil()) {
             DynamicMethod method = object.getMetaClass().searchMethod("to_a");
             
             if (method.getImplementationClass() == recv.getRuntime().getKernel()) {
                 return recv.getRuntime().newArray(object);
             }
             
             // Strange that Ruby has custom code here and not convertToTypeWithCheck equivalent.
             value = object.callMethod(recv.getRuntime().getCurrentContext(), MethodIndex.TO_A, "to_a");
             if (value.getMetaClass() != recv.getRuntime().getClass("Array")) {
                 throw recv.getRuntime().newTypeError("`to_a' did not return Array");
                
             }
         }
         
         return value;
     }
     
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getValue().length() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = object.convertToFloat();
             if(Double.isNaN(rFloat.getDoubleValue())){
                 recv.getRuntime().newArgumentError("invalid value for Float()");
         }
             return rFloat;
     }
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,0,true);
                     }
         return object.callMethod(context,MethodIndex.TO_I, "to_i");
     }
     
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return object.callMethod(recv.getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s");
     }
     
     
     public static IRubyObject p(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", args[i].callMethod(context, "inspect"));
                 defout.callMethod(context, "write", recv.getRuntime().newString("\n"));
             }
         }
         return recv.getRuntime().getNil();
     }
 
     /** rb_f_putc
      */
     public static IRubyObject putc(IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(recv.getRuntime().getCurrentContext(), "putc", ch);
     }
 
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         defout.callMethod(context, "puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         defout.callMethod(context, "print", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject printf(IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             ThreadContext context = recv.getRuntime().getCurrentContext();
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).readlines(args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(Ruby runtime) {
         IRubyObject line = runtime.getCurrentContext().getLastline();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).sub_bang(args, block);
     }
 
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args, block);
     }
 
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chop_bang(IRubyObject recv) {
         return getLastlineString(recv.getRuntime()).chop_bang();
     }
 
     public static IRubyObject chop(IRubyObject recv) {
         RubyString str = getLastlineString(recv.getRuntime());
 
         if (str.getValue().length() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chomp_bang(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).chomp_bang(args);
     }
 
     public static IRubyObject chomp(IRubyObject recv, IRubyObject[] args) {
         RubyString str = getLastlineString(recv.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         recv.getRuntime().getCurrentContext().setLastline(dup);
         return dup;
     }
 
     public static IRubyObject split(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).split(args);
     }
 
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(recv.getRuntime()).scan(pattern, block);
     }
 
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(recv.getRuntime(), args);
     }
 
     public static IRubyObject sleep(IRubyObject recv, IRubyObject seconds) {
         long milliseconds = (long) (seconds.convertToFloat().getDoubleValue() * 1000);
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = recv.getRuntime().getThreadService().getCurrentContext().getThread();
         
         try {
             rubyThread.sleep(milliseconds);
         } catch (InterruptedException iExcptn) {
         }
 
         return recv.getRuntime().newFixnum(
                 Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         recv.getRuntime().secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         throw recv.getRuntime().newSystemExit(status);
     }
 
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return exit(recv, args);
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     public static RubyArray global_variables(IRubyObject recv) {
         RubyArray globalVariables = recv.getRuntime().newArray();
 
         Iterator iter = recv.getRuntime().getGlobalVariables().getNames();
         while (iter.hasNext()) {
             String globalVariableName = (String) iter.next();
 
             globalVariables.append(recv.getRuntime().newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     public static RubyArray local_variables(IRubyObject recv) {
         final Ruby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         String[] names = runtime.getCurrentContext().getCurrentScope().getAllNamesInScope();
         for (int i = 0; i < names.length; i++) {
             localVariables.append(runtime.newString(names[i]));
         }
 
         return localVariables;
     }
 
     public static RubyBinding binding(IRubyObject recv, Block block) {
         // FIXME: Pass block into binding
         return recv.getRuntime().newBinding();
     }
 
     public static RubyBoolean block_given(IRubyObject recv, Block block) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock().isGiven());
     }
 
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArrayNoCopy(args);
         newArgs.shift();
 
         return str.format(newArgs);
     }
 
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Arity.checkArgumentCount(recv.getRuntime(), args, 0, 3); 
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getClass("RuntimeError").newInstance(args, block));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.getClass("Exception"))) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         throw new RaiseException((RubyException) exception);
     }
     
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString file = args[0].convertToString();
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args == null || args.length == 0) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
             
         RubyString src = args[0].convertToString();
         IRubyObject scope = null;
         String file = "(eval)";
         
         if (args.length > 1) {
             if (!args[1].isNil()) {
                 scope = args[1];
             }
             
             if (args.length > 2) {
                 file = args[2].toString();
             }
         }
         // FIXME: line number is not supported yet
         //int line = args.length > 3 ? RubyNumeric.fix2int(args[3]) : 1;
 
         recv.getRuntime().checkSafeString(src);
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
             scope = RubyBinding.newBindingForEval(recv.getRuntime());
         }
         
         return recv.evalWithBinding(context, src, scope, file);
     }
 
     public static IRubyObject callcc(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         runtime.getWarnings().warn("Kernel#callcc: Continuations are not implemented in JRuby and will not work");
         IRubyObject cc = runtime.getClass("Continuation").callMethod(runtime.getCurrentContext(),"new");
         cc.dataWrapStruct(block);
         return block.yield(runtime.getCurrentContext(),cc);
     }
 
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return recv.getRuntime().getCurrentContext().createBacktrace(level, false);
     }
 
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         try {
             context.pushCatch(tag.asSymbol());
             return block.yield(context, tag);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ThrowJump &&
                 je.getTarget().equals(tag.asSymbol())) {
                     return (IRubyObject) je.getValue();
             }
             throw je;
         } finally {
             context.popCatch();
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         String[] catches = runtime.getCurrentContext().getActiveCatches();
 
         String message = "uncaught throw '" + tag + '\'';
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 JumpException je = recv.getRuntime().getCurrentContext().controlException;
                 je.setJumpType(JumpException.JumpType.ThrowJump);
 
                 je.setTarget(tag);
                 je.setValue(args.length > 1 ? args[1] : runtime.getNil());
                 throw je;
             }
         }
 
         //No catch active for this throw
         throw runtime.newNameError(message, tag);
     }
 
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: We can probably fake some basic signals, but obviously can't do everything. For now, stub.
         return recv.getRuntime().getNil();
     }
     
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
         Ruby runtime = recv.getRuntime();
         IRubyObject out = runtime.getObject().getConstant("STDERR");
         RubyIO io = (RubyIO) out.convertToType(runtime.getClass("IO"), 0, "to_io", true); 
 
         io.puts(new IRubyObject[] { message });
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject set_trace_func(IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             recv.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw recv.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             recv.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_removed(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_undefined(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
     
     
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(true, block);
     }
 
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             try {
                 block.yield(context, recv.getRuntime().getNil());
                 
                 context.pollThreadEvents();
             } catch (JumpException je) {
                 // JRUBY-530, specifically the Kernel#loop case:
                 // Kernel#loop always takes a block.  But what we're looking
                 // for here is breaking an iteration where the block is one 
                 // used inside loop's block, not loop's block itself.  Set the 
                 // appropriate flag on the JumpException if this is the case
                 // (the FCALLNODE case in EvaluationState will deal with it)
                 if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                     if (je.getTarget() != null && je.getTarget() != block) {
                         je.setBreakInKernelLoop(true);
                     }
                 }
                  
                 throw je;
             }
         }
     }
     public static IRubyObject test(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         if (args.length == 0) {
             // MRI message if no args given
             throw runtime.newArgumentError("wrong number of arguments");
         }
         IRubyObject cmdArg = args[0];
         int cmd;
         if (cmdArg instanceof RubyFixnum) {
             cmd = (int)((RubyFixnum)cmdArg).getLongValue();
         } else if (cmdArg instanceof RubyString &&
                 ((RubyString)cmdArg).getByteList().length() > 0) {
             // MRI behavior: use first byte of string value if len > 0
             cmd = ((RubyString)cmdArg).getByteList().charAt(0);
         } else {
             cmd = (int)cmdArg.convertToInteger().getLongValue();
         }
         
         // MRI behavior: raise ArgumentError for 'unknown command' before
         // checking number of args.
         switch(cmd) {
         
         // implemented commands
         case 'f':
         case 'M':
             break;
 
         // unimplemented commands
 
         // FIXME: obviously, these are mostly unimplemented.  Raising an
         // ArgumentError 'unimplemented command' for them.
         
         case 'A': // ?A  | Time    | Last access time for file1
         case 'b': // ?b  | boolean | True if file1 is a block device
         case 'c': // ?c  | boolean | True if file1 is a character device
         case 'C': // ?C  | Time    | Last change time for file1
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
         case 'e': // ?e  | boolean | True if file1 exists
         case 'g': // ?g  | boolean | True if file1 has the \CF{setgid} bit
         case 'G': // ?G  | boolean | True if file1 exists and has a group
                   //     |         | ownership equal to the caller's group
         case 'k': // ?k  | boolean | True if file1 exists and has the sticky bit set
         case 'l': // ?l  | boolean | True if file1 exists and is a symbolic link
         case 'o': // ?o  | boolean | True if file1 exists and is owned by
                   //     |         | the caller's effective uid  
         case 'O': // ?O  | boolean | True if file1 exists and is owned by 
                   //     |         | the caller's real uid
         case 'p': // ?p  | boolean | True if file1 exists and is a fifo
         case 'r': // ?r  | boolean | True if file1 is readable by the effective
                   //     |         | uid/gid of the caller
         case 'R': // ?R  | boolean | True if file is readable by the real
                   //     |         | uid/gid of the caller
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size,
                   //     |         | otherwise return nil
         case 'S': // ?S  | boolean | True if file1 exists and is a socket
         case 'u': // ?u  | boolean | True if file1 has the setuid bit set
         case 'w': // ?w  | boolean | True if file1 exists and is writable by
         case 'W': // ?W  | boolean | True if file1 exists and is writable by
                   //     |         | the real uid/gid
         case 'x': // ?x  | boolean | True if file1 exists and is executable by
                   //     |         | the effective uid/gid
         case 'X': // ?X  | boolean | True if file1 exists and is executable by
                   //     |         | the real uid/gid
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
                   //
                   //        Tests that take two files:
                   //
         case '-': // ?-  | boolean | True if file1 and file2 are identical
         case '=': // ?=  | boolean | True if the modification times of file1
                   //     |         | and file2 are equal
         case '<': // ?<  | boolean | True if the modification time of file1
                   //     |         | is prior to that of file2
         case '>': // ?>  | boolean | True if the modification time of file1
                   //     |         | is after that of file2
             
             throw runtime.newArgumentError("unimplemented command ?"+(char)cmd);
             
         default:
             // matches MRI message
             throw runtime.newArgumentError("unknown command ?"+(char)cmd);
             
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-':
         case '=':
         case '<':
         case '>':
             if (args.length != 3) {
                 throw runtime.newArgumentError(args.length,3);
             }
             break;
         default:
             if (args.length != 2) {
                 throw runtime.newArgumentError(args.length,2);
             }
             break;
         }
         
         File pwd = new File(runtime.getCurrentDirectory());
         File file1 = new File(pwd,args[1].convertToString().toString());
         Calendar calendar = null;
                 
         switch (cmd) {
         case 'f': // ?f  | boolean | True if file1 exists and is a regular file
             return RubyBoolean.newBoolean(runtime, file1.isFile());
         
         case 'M': // ?M  | Time    | Last modification time for file1
             calendar = Calendar.getInstance();
             calendar.setTimeInMillis(file1.lastModified());
             return RubyTime.newTime(runtime, calendar);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newString(recv.getRuntime(), output.toByteArray());
     }
     
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long oldRandomSeed = runtime.getRandomSeed();
 
         if (args.length > 0) {
             RubyInteger integerSeed = 
                 (RubyInteger) args[0].convertToType(runtime.getClass("Integer"), MethodIndex.TO_I, "to_i", true);
             runtime.setRandomSeed(integerSeed.getLongValue());
         } else {
             // Not sure how well this works, but it works much better than
             // just currentTimeMillis by itself.
             runtime.setRandomSeed(System.currentTimeMillis() ^
               recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
               runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
         }
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     public static RubyNumeric rand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long ceil;
         if (args.length == 0) {
             ceil = 0;
         } else if (args.length == 1) {
             if (args[0] instanceof RubyBignum) {
                 byte[] bytes = new byte[((RubyBignum) args[0]).getValue().toByteArray().length - 1];
                 
                 runtime.getRandom().nextBytes(bytes);
                 
                 return new RubyBignum(runtime, new BigInteger(bytes).abs()); 
             }
              
             RubyInteger integerCeil = (RubyInteger) args[0].convertToType(runtime.getClass("Integer"), MethodIndex.TO_I, "to_i", true);
             ceil = Math.abs(integerCeil.getLongValue());
         } else {
             throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble()); 
         }
         if (ceil > Integer.MAX_VALUE) {
             return runtime.newFixnum(runtime.getRandom().nextLong() % ceil);
         }
             
         return runtime.newFixnum(runtime.getRandom().nextInt((int) ceil));
     }
 
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int resultCode = new ShellLauncher(runtime).runAndWait(args);
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     public static RubyArray to_a(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn("default 'to_a' will be obsolete");
         return recv.getRuntime().newArray(recv);
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 1b359adc0a..8189dcce7e 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1457 +1,1463 @@
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
 
 import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.Sprintf;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.jruby.ast.Node;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
     
     private RubyObject(){};
     // An instance that never equals any other instance
     public static final IRubyObject NEVER = new RubyObject();
     
     // The class of this object
     protected RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     private transient Object dataStruct;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
     protected boolean isTrue = true;
     
     private Finalizer finalizer;
     
     public class Finalizer {
         private long id;
         private List finalizers;
         private AtomicBoolean finalized;
         
         public Finalizer(long id) {
             this.id = id;
             this.finalized = new AtomicBoolean(false);
         }
         
         public void addFinalizer(RubyProc finalizer) {
             if (finalizers == null) {
                 finalizers = new ArrayList();
             }
             finalizers.add(finalizer);
         }
 
         public void removeFinalizers() {
             finalizers = null;
         }
     
         public void finalize() {
             if (finalized.compareAndSet(false, true)) {
                 if (finalizers != null) {
                     IRubyObject idFixnum = getRuntime().newFixnum(id);
                     for (int i = 0; i < finalizers.size(); i++) {
                         ((RubyProc)finalizers.get(i)).call(
                                 new IRubyObject[] {idFixnum});
                     }
                 }
             }
         }
     }
 
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
     
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyObject.class);   
         objectClass.index = ClassIndex.OBJECT;
         
         objectClass.definePrivateMethod("initialize", callbackFactory.getOptMethod("initialize"));
         objectClass.definePrivateMethod("inherited", callbackFactory.getMethod("inherited", IRubyObject.class));
         objectClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", IRubyObject.class));
         
         return objectClass;
     }
     
     public static ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             IRubyObject instance = new RubyObject(runtime, klass);
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
     
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
         return other == this || 
                 other instanceof IRubyObject && 
                 callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY).toString();
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
     public final RubyClass getMetaClass() {
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
 
     public final boolean isTrue() {
         return isTrue;
     }
 
     public final boolean isFalse() {
         return !isTrue;
     }
 
     public boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
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
 
     /** init_copy
      * 
      */
     public static void initCopy(IRubyObject clone, IRubyObject original) {
         assert original != null;
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         if (original.safeHasInstanceVariables()) {
         clone.setInstanceVariables(new HashMap(original.getInstanceVariables()));
         }
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = klazz.getSuperClass();
         
         assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
 
         return callMethod(context, superClass, context.getFrameName(), args, CallType.SUPER, block);
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
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject arg) {
         return callMethod(context,getMetaClass(),methodIndex,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject[] args) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name,
                                   IRubyObject[] args, CallType callType) {
         return callMethod(context,getMetaClass(),methodIndex,name,args,callType, Block.NULL_BLOCK);
     }
     
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public IRubyObject compilerCallMethodWithIndex(ThreadContext context, int methodIndex, String name, IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, methodIndex, name, args, callType, block);
         }
         
         return compilerCallMethod(context, name, args, self, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public IRubyObject compilerCallMethod(ThreadContext context, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = getMetaClass();
         method = rubyclass.searchMethod(name);
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, self, callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
     
     public static IRubyObject callMethodMissingIfNecessary(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(self, callType))) {
 
             if (callType == CallType.SUPER) {
                 throw self.getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(self, args, block);
             }
 
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
             return receiver.callMethod(context, "method_missing", newArgs, block);
         }
         
         // kludgy.
         return null;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, methodIndex, name, args, callType, Block.NULL_BLOCK);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
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
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, context.getFrameSelf(), callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name) {
         return callMethod(context, getMetaClass(), methodIndex, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, getMetaClass(), name, IRubyObject.NULL_ARRAY, null, block);
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
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
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
         return (RubyArray) convertToType(getRuntime().getArray(), MethodIndex.TO_ARY, true);
     }
 
     public RubyHash convertToHash() {
-        return (RubyHash)convertToType(getRuntime().getHash(), 0, "to_hash", true, true, false);
+        return (RubyHash)convertToType(getRuntime().getHash(), MethodIndex.TO_HASH, "to_hash", true, true, false);
     }
     
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType(getRuntime().getClass("Float"), MethodIndex.TO_F, true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType(getRuntime().getClass("Integer"), MethodIndex.TO_INT, true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType(getRuntime().getString(), MethodIndex.TO_STR, true);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(RubyClass targetType, int convertMethodIndex, String convertMethod) {
         return convertToType(targetType, convertMethodIndex, convertMethod, false, true, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raise) {
         return convertToType(targetType, convertMethodIndex, convertMethod, raise, false, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, boolean raise) {
         return convertToType(targetType, convertMethodIndex, MethodIndex.NAMES[convertMethodIndex], raise, false, false);
     }
     
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough) {
         if (isKindOf(targetType)) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raiseOnMissingMethod) {
                 throw getRuntime().newTypeError("can't convert " + trueFalseNil(this) + " into " + trueFalseNil(targetType.getName()));
             } 
 
             return getRuntime().getNil();
         }
         
         IRubyObject value = callMethod(getRuntime().getCurrentContext(), convertMethodIndex, convertMethod, IRubyObject.NULL_ARRAY);
         
         if (allowNilThrough && value.isNil()) {
             return value;
         }
         
         if (raiseOnWrongTypeResult && !value.isKindOf(targetType)) {
             throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod +
                     " should return " + targetType);
         }
         
         return value;
     }
 
     /** rb_obj_as_string
      */
     public RubyString asString() {
         if (this instanceof RubyString) return (RubyString) this;
         
         IRubyObject str = this.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
         
         if (!(str instanceof RubyString)) str = anyToString();
 
         return (RubyString) str;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck(getRuntime().getString(), MethodIndex.TO_STR, "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck(getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary");
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
 		    String lastFuncName = tc.getFrameName();
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
             IRubyObject newSelf = threadContext.getFrameSelf();
             Node node = getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope());
 
             return EvaluationState.eval(getRuntime(), threadContext, node, newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
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
             Node node = getRuntime().parse(src.toString(), file, context.getCurrentScope());
             
             return EvaluationState.eval(getRuntime(), context, node, this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.BreakJump) {
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
-//        if (isNil()) {
-//            return getRuntime().newBoolean(obj.isNil());
-//        }
-//        return getRuntime().newBoolean(this == obj);
     }
 
-	public IRubyObject same(IRubyObject other) {
-		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
+    /** rb_equal
+     * 
+     */
+    public IRubyObject equal(IRubyObject other) {
+        if(this == other || callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==",other).isTrue()){
+            return getRuntime().getTrue();
 	}
-
+ 
+        return getRuntime().getFalse();
+    }
     
+    public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
+        if (this == other) return getRuntime().getTrue();
+        return callMethod(context, MethodIndex.EQUALEQUAL, "==", other);
+    }
+
+    /** rb_eql
+     *  this method is not defind for Ruby objects directly.
+     *  notably overriden by RubyFixnum, RubyString, RubySymbol - these do a short-circuit calls.
+     *  see: rb_any_cmp() in hash.c
+     *  do not confuse this method with eql_p methods (which it calls by default), eql is mainly used for hash key comparison 
+     */
+    public boolean eql(IRubyObject other) {
+        return callMethod(getRuntime().getCurrentContext(), MethodIndex.EQL_P, "eql?", other).isTrue();
+    }
+
+    public final boolean eqlInternal(final ThreadContext context, final IRubyObject other){
+        if (this == other) return true;
+        return callMethod(context, MethodIndex.EQL_P, "eql?", other).isTrue();
+    }
+
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
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
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
 
     public synchronized RubyFixnum id_deprecated() {
         getRuntime().getWarnings().warn("Object#id will be deprecated; use Object#object_id");
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(super.hashCode());
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), MethodIndex.HASH, "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return super.hashCode();
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
         initCopy(clone, this);
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
         
         initCopy(dup, this);
 
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
                         part.append(sep);
                         part.append(" ");
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
         }
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (IdUtil.isInstanceVariable(name)) {
                 names.add(getRuntime().newString(name));
             }
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!type.isKindOf(getRuntime().getClass("Module"))) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 
 	public IRubyObject public_methods(IRubyObject[] args) {
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods() {
         return getMetaClass().protected_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods() {
         return getMetaClass().private_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(Arity.checkArgumentCount(getRuntime(), args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
                 	continue;
                 }
 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         return specificEval(getSingletonClass(), args, block);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg, Block block) {
     	return getRuntime().getNil();
     }
     
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 0);
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = anyToString().toString();
         } else {
             description = inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         Ruby runtime = getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : getType().getName())
                 })).toString();
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg, name);
         }
         throw getRuntime().newNoMethodError(msg, name);
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
-
-    /** rb_equal
-     * 
-     */
-    public IRubyObject equal(IRubyObject other) {
-        if(this == other || callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==",other).isTrue()){
-            return getRuntime().getTrue();
-        }
  
-        return getRuntime().getFalse();
-    }
-    
-    public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
-        if (this == other) return getRuntime().getTrue();
-        return callMethod(context, MethodIndex.EQUALEQUAL, "==", other);
-    }
-        
     public void addFinalizer(RubyProc finalizer) {
         if (this.finalizer == null) {
             this.finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             getRuntime().addFinalizer(this.finalizer);
         }
         this.finalizer.addFinalizer(finalizer);
     }
 
     public void removeFinalizers() {
         if (finalizer != null) {
             finalizer.removeFinalizers();
             finalizer = null;
             getRuntime().removeFinalizer(this.finalizer);
         }
     }
 }
diff --git a/src/org/jruby/RubyRange.java b/src/org/jruby/RubyRange.java
index 4fa679443a..84ce3350d1 100644
--- a/src/org/jruby/RubyRange.java
+++ b/src/org/jruby/RubyRange.java
@@ -1,500 +1,509 @@
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
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.IOException;
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  * @author jpetersen
  */
 public class RubyRange extends RubyObject {
 
     private IRubyObject begin;
     private IRubyObject end;
     private boolean isExclusive;
 
     public RubyRange(Ruby runtime, RubyClass impl) {
         super(runtime, impl);
     }
 
     public void init(IRubyObject aBegin, IRubyObject aEnd, RubyBoolean aIsExclusive) {
         if (!(aBegin instanceof RubyFixnum && aEnd instanceof RubyFixnum)) {
             try {
                 aBegin.callMethod(getRuntime().getCurrentContext(), MethodIndex.OP_SPACESHIP, "<=>", aEnd);
             } catch (RaiseException rExcptn) {
                 throw getRuntime().newArgumentError("bad value for range");
             }
         }
 
         this.begin = aBegin;
         this.end = aEnd;
         this.isExclusive = aIsExclusive.isTrue();
     }
     
     private static ObjectAllocator RANGE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyRange(runtime, klass);
         }
     };
 
     public IRubyObject doClone(){
         return RubyRange.newRange(getRuntime(), begin, end, isExclusive);
     }
 
     private static final ObjectMarshal RANGE_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             RubyRange range = (RubyRange)obj;
             
             // FIXME: This is a pretty inefficient way to do this, but we need child class
             // ivars and begin/end together
             Map iVars = new HashMap(range.getInstanceVariables());
             
             // add our "begin" and "end" instance vars to the collection
             iVars.put("begin", range.begin);
             iVars.put("end", range.end);
             iVars.put("excl", range.isExclusive? runtime.getTrue() : runtime.getFalse());
             
             marshalStream.dumpInstanceVars(iVars);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             RubyRange range = (RubyRange)type.allocate();
             
             unmarshalStream.registerLinkTarget(range);
 
             unmarshalStream.defaultInstanceVarsUnmarshal(range);
             
             range.begin = range.getInstanceVariable("begin");
             range.end = range.getInstanceVariable("end");
             range.isExclusive = range.getInstanceVariable("excl").isTrue();
 
             return range;
         }
     };
     
     public static RubyClass createRangeClass(Ruby runtime) {
         RubyClass result = runtime.defineClass("Range", runtime.getObject(), RANGE_ALLOCATOR);
         
         result.setMarshal(RANGE_MARSHAL);
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyRange.class);
         
         result.includeModule(runtime.getModule("Enumerable"));
 
         result.defineMethod("==", callbackFactory.getMethod("equal", RubyKernel.IRUBY_OBJECT));
+        result.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
         result.defineFastMethod("begin", callbackFactory.getFastMethod("first"));
         result.defineMethod("each", callbackFactory.getMethod("each"));
         result.defineFastMethod("end", callbackFactory.getFastMethod("last"));
         result.defineFastMethod("exclude_end?", callbackFactory.getFastMethod("exclude_end_p"));
         result.defineFastMethod("first", callbackFactory.getFastMethod("first"));
         result.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         result.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         result.defineMethod("inspect", callbackFactory.getMethod("inspect"));
         result.defineFastMethod("last", callbackFactory.getFastMethod("last"));
         result.defineMethod("length", callbackFactory.getMethod("length"));
         result.defineMethod("size", callbackFactory.getMethod("length"));
         result.defineMethod("step", callbackFactory.getOptMethod("step"));
         result.defineMethod("to_s", callbackFactory.getMethod("to_s"));
 
         result.defineMethod("to_a", callbackFactory.getMethod("to_a"));
         result.defineMethod("include?", callbackFactory.getMethod("include_p", RubyKernel.IRUBY_OBJECT));
         // We override Enumerable#member? since ranges in 1.8.1 are continuous.
         result.defineAlias("member?", "include?");
         result.defineAlias("===", "include?");
         
         CallbackFactory classCB = runtime.callbackFactory(RubyClass.class);
         result.getMetaClass().defineMethod("new", classCB.getOptMethod("newInstance"));
         
         return result;
     }
 
     /**
      * Converts this Range to a pair of integers representing a start position 
      * and length.  If either of the range's endpoints is negative, it is added to 
      * the <code>limit</code> parameter in an attempt to arrive at a position 
      * <i>p</i> such that <i>0&nbsp;&lt;=&nbsp;p&nbsp;&lt;=&nbsp;limit</i>. If 
      * <code>truncate</code> is true, the result will be adjusted, if possible, so 
      * that <i>begin&nbsp;+&nbsp;length&nbsp;&lt;=&nbsp;limit</i>.  If <code>strict</code> 
      * is true, an exception will be raised if the range can't be converted as 
      * described above; otherwise it just returns <b>null</b>. 
      * 
      * @param limit    the size of the object (e.g., a String or Array) that 
      *                 this range is being evaluated against.
      * @param truncate if true, result must fit within the range <i>(0..limit)</i>.
      * @param isStrict   if true, raises an exception if the range can't be converted.
      * @return         a two-element array representing a start value and a length, 
      *                 or <b>null</b> if the conversion failed.
      */
     public long[] getBeginLength(long limit, boolean truncate, boolean isStrict) {
         long beginLong = RubyNumeric.num2long(begin);
         long endLong = RubyNumeric.num2long(end);
         
         // Apparent legend for MRI 'err' param to JRuby 'truncate' and 'isStrict':
         // 0 =>  truncate && !strict
         // 1 => !truncate &&  strict
         // 2 =>  truncate &&  strict
 
         if (! isExclusive) {
             endLong++;
         }
 
         if (beginLong < 0) {
             beginLong += limit;
             if (beginLong < 0) {
                 if (isStrict) {
                     throw getRuntime().newRangeError(inspect().toString() + " out of range.");
                 }
                 return null;
             }
         }
 
         if (truncate && beginLong > limit) {
             if (isStrict) {
                 throw getRuntime().newRangeError(inspect().toString() + " out of range.");
             }
             return null;
         }
 
         if (truncate && endLong > limit) {
             endLong = limit;
         }
 
 		if (endLong < 0  || (!isExclusive && endLong == 0)) {
 			endLong += limit;
 			if (endLong < 0) {
 				if (isStrict) {
 					throw getRuntime().newRangeError(inspect().toString() + " out of range.");
 				}
 				return null;
 			}
 		}
 
         return new long[] { beginLong, Math.max(endLong - beginLong, 0L) };
     }
     
     public long[] begLen(long len, int err){
         long beg = RubyNumeric.num2long(this.begin);
         long end = RubyNumeric.num2long(this.end);
 
         if(beg < 0){
             beg += len;
             if(beg < 0){
                 if(err != 0){
                     throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 }
                 return null;
             }
         }
 
         if(err == 0 || err == 2){
             if(beg > len){
                 if(err != 0){
                     throw getRuntime().newRangeError(beg + ".." + (isExclusive ? "." : "") + end + " out of range");
                 }
                 return null;
             }
             if(end > len){
                 end = len;
             }
         }
         if(end < 0){
             end += len;
         }
         if(!isExclusive){
             end++;
         }
         len = end - beg;
         if(len < 0){
             len = 0;
         }
 
         return new long[]{beg, len};
     }    
 
     public static RubyRange newRange(Ruby runtime, IRubyObject begin, IRubyObject end, boolean isExclusive) {
         RubyRange range = new RubyRange(runtime, runtime.getClass("Range"));
         range.init(begin, end, isExclusive ? runtime.getTrue() : runtime.getFalse());
         return range;
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         if (args.length == 3) {
             init(args[0], args[1], (RubyBoolean) args[2]);
         } else if (args.length == 2) {
             init(args[0], args[1], getRuntime().getFalse());
         } else {
             throw getRuntime().newArgumentError("Wrong arguments. (anObject, anObject, aBoolean = false) expected");
         }
         return getRuntime().getNil();
     }
 
     public IRubyObject first() {
         return begin;
     }
 
     public IRubyObject last() {
         return end;
     }
     
     public RubyFixnum hash() {
         ThreadContext context = getRuntime().getCurrentContext();
         long baseHash = (isExclusive ? 1 : 0);
         long beginHash = ((RubyFixnum) begin.callMethod(context, MethodIndex.HASH, "hash")).getLongValue();
         long endHash = ((RubyFixnum) end.callMethod(context, MethodIndex.HASH, "hash")).getLongValue();
         
         long hash = baseHash;
         hash = hash ^ (beginHash << 1);
         hash = hash ^ (endHash << 9);
         hash = hash ^ (baseHash << 24);
         
         return getRuntime().newFixnum(hash);
     }
     
     private static byte[] DOTDOTDOT = "...".getBytes();
     private static byte[] DOTDOT = "..".getBytes();
 
     private IRubyObject asString(String stringMethod) {
         ThreadContext context = getRuntime().getCurrentContext();
         RubyString begStr = (RubyString) begin.callMethod(context, stringMethod);
         RubyString endStr = (RubyString) end.callMethod(context, stringMethod);
 
         return begStr.cat(isExclusive ? DOTDOTDOT : DOTDOT).concat(endStr);
     }
     
     public IRubyObject inspect(Block block) {
         return asString("inspect");
     }
     
     public IRubyObject to_s(Block block) {
         return asString("to_s");
     }
 
     public RubyBoolean exclude_end_p() {
         return getRuntime().newBoolean(isExclusive);
     }
 
     public RubyFixnum length(Block block) {
         long size = 0;
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (begin.callMethod(context, MethodIndex.OP_GT, ">", end).isTrue()) {
             return getRuntime().newFixnum(0);
         }
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             size = ((RubyNumeric) end).getLongValue() - ((RubyNumeric) begin).getLongValue();
             if (!isExclusive) {
                 size++;
             }
         } else { // Support length for arbitrary classes
             IRubyObject currentObject = begin;
 	    int compareMethod = isExclusive ? MethodIndex.OP_LT : MethodIndex.OP_LE;
 
 	    while (currentObject.callMethod(context, compareMethod, MethodIndex.NAMES[compareMethod], end).isTrue()) {
 		size++;
 		if (currentObject.equals(end)) {
 		    break;
 		}
 		currentObject = currentObject.callMethod(context, "succ");
 	    }
 	}
         return getRuntime().newFixnum(size);
     }
 
     public IRubyObject equal(IRubyObject obj, Block block) {
         if (!(obj instanceof RubyRange)) {
             return getRuntime().getFalse();
         }
         RubyRange otherRange = (RubyRange) obj;
         boolean result =
             begin.equals(otherRange.begin) &&
             end.equals(otherRange.end) &&
             isExclusive == otherRange.isExclusive;
         return getRuntime().newBoolean(result);
     }
+    
+    public IRubyObject eql_p(IRubyObject other) {
+        if (this == other) return getRuntime().getTrue();
+        if (!(other instanceof RubyRange)) return getRuntime().getFalse();
+        RubyRange otherRange = (RubyRange)other;
+        if (begin != otherRange.begin || end != otherRange.end || isExclusive != otherRange.isExclusive) return getRuntime().getFalse();
+        return getRuntime().getTrue();
+    }
 
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             long endLong = ((RubyNumeric) end).getLongValue();
             long i = ((RubyNumeric) begin).getLongValue();
 
             if (!isExclusive) {
                 endLong += 1;
             }
 
             for (; i < endLong; i++) {
                 block.yield(context, getRuntime().newFixnum(i));
             }
         } else if (begin instanceof RubyString) {
             ((RubyString) begin).upto(end, isExclusive, block);
         } else if (begin.isKindOf(getRuntime().getClass("Numeric"))) {
             if (!isExclusive) {
                 end = end.callMethod(context, MethodIndex.OP_PLUS, "+", RubyFixnum.one(getRuntime()));
             }
             while (begin.callMethod(context, MethodIndex.OP_LT, "<", end).isTrue()) {
                 block.yield(context, begin);
                 begin = begin.callMethod(context, MethodIndex.OP_PLUS, "+", RubyFixnum.one(getRuntime()));
             }
         } else {
             IRubyObject v = begin;
 
             if (isExclusive) {
                 while (v.callMethod(context, MethodIndex.OP_LT, "<", end).isTrue()) {
                     if (v.equals(end)) {
                         break;
                     }
                     block.yield(context, v);
                     v = v.callMethod(context, "succ");
                 }
             } else {
                 while (v.callMethod(context, MethodIndex.OP_LE, "<=", end).isTrue()) {
                     block.yield(context, v);
                     if (v.equals(end)) {
                         break;
                     }
                     v = v.callMethod(context, "succ");
                 }
             }
         }
 
         return this;
     }
     
     public IRubyObject step(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         
         IRubyObject currentObject = begin;
         int compareMethod = isExclusive ? MethodIndex.OP_LT : MethodIndex.OP_LE;
         int stepSize = (int) (args.length == 0 ? 1 : args[0].convertToInteger().getLongValue());
         
         if (stepSize <= 0) {
             throw getRuntime().newArgumentError("step can't be negative");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
         if (begin instanceof RubyNumeric && end instanceof RubyNumeric) {
             RubyFixnum stepNum = getRuntime().newFixnum(stepSize);
             while (currentObject.callMethod(context, compareMethod, MethodIndex.NAMES[compareMethod], end).isTrue()) {
                 block.yield(context, currentObject);
                 currentObject = currentObject.callMethod(context, MethodIndex.OP_PLUS, "+", stepNum);
             }
         } else {
             while (currentObject.callMethod(context, compareMethod, MethodIndex.NAMES[compareMethod], end).isTrue()) {
                 block.yield(context, currentObject);
                 
                 for (int i = 0; i < stepSize; i++) {
                     currentObject = currentObject.callMethod(context, "succ");
                 }
             }
         }
         
         return this;
     }
     
     public RubyArray to_a(Block block) {
         IRubyObject currentObject = begin;
 	    String compareMethod = isExclusive ? "<" : "<=";
 	    RubyArray array = getRuntime().newArray();
         ThreadContext context = getRuntime().getCurrentContext();
         
 	    while (currentObject.callMethod(context, compareMethod, end).isTrue()) {
 	        array.append(currentObject);
 	        
 			if (currentObject.equals(end)) {
 			    break;
 			}
 			
 			currentObject = currentObject.callMethod(context, "succ");
 	    }
 	    
 	    return array;
     }
 
     private boolean r_lt(IRubyObject a, IRubyObject b) {
         IRubyObject r = a.callMethod(getRuntime().getCurrentContext(),MethodIndex.OP_SPACESHIP, "<=>",b);
         if(r.isNil()) {
             return false;
         }
         if(RubyComparable.cmpint(r,a,b) < 0) {
             return true;
         }
         return false;
     }
 
     private boolean r_le(IRubyObject a, IRubyObject b) {
         IRubyObject r = a.callMethod(getRuntime().getCurrentContext(),MethodIndex.OP_SPACESHIP, "<=>",b);
         if(r.isNil()) {
             return false;
         }
         if(RubyComparable.cmpint(r,a,b) <= 0) {
             return true;
         }
         return false;
     }
 
     public RubyBoolean include_p(IRubyObject obj, Block block) {
         if(r_le(begin,obj)) {
             if(isExclusive) {
                 if(r_lt(obj,end)) {
                     return getRuntime().getTrue();
                 }
             } else {
                 if(r_le(obj,end)) {
                     return getRuntime().getTrue();
                 }
             }
         }
         return getRuntime().getFalse();
     }
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 3c195271c2..65b762bb14 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,3069 +1,3077 @@
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
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.UnsupportedEncodingException;
 import java.nio.ByteBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CodingErrorAction;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Locale;
 import jregex.Matcher;
 import jregex.Pattern;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyString extends RubyObject {
     // Default record seperator
     private static final String DEFAULT_RS = "\n";
 
     private static final int SHARE_LEVEL_NONE = 0;      // nothing shared, string is independant
     private static final int SHARE_LEVEL_BYTELIST = 1;  // string doesnt have it's own ByteList (values)
     private static final int SHARE_LEVEL_BUFFER = 2;    // string has it's own ByteList, but it's pointing to a shared buffer (byte[]) 
 
     private ByteList value;
     private int shareLevel = 0;
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyString newString = runtime.newString("");
             
             newString.setMetaClass(klass);
             
             return newString;
         }
     };
     
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         stringClass.index = ClassIndex.STRING;
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyString.class);
         
         stringClass.includeModule(runtime.getModule("Comparable"));
         stringClass.includeModule(runtime.getModule("Enumerable"));
         
         stringClass.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", RubyKernel.IRUBY_OBJECT));
-        stringClass.defineFastMethod("==", callbackFactory.getFastMethod("eql", RubyKernel.IRUBY_OBJECT));
+        stringClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("+", callbackFactory.getFastMethod("op_plus", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("*", callbackFactory.getFastMethod("op_mul", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("%", callbackFactory.getFastMethod("format", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         
         // To override Comparable with faster String ones
         stringClass.defineFastMethod(">=", callbackFactory.getFastMethod("op_ge", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod(">", callbackFactory.getFastMethod("op_gt", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("<=", callbackFactory.getFastMethod("op_le", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("<", callbackFactory.getFastMethod("op_lt", RubyKernel.IRUBY_OBJECT));
         
-        stringClass.defineFastMethod("eql?", callbackFactory.getFastMethod("op_eql", RubyKernel.IRUBY_OBJECT));
+        stringClass.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
         
         stringClass.defineFastMethod("[]", callbackFactory.getFastOptMethod("aref"));
         stringClass.defineFastMethod("[]=", callbackFactory.getFastOptMethod("aset"));
         stringClass.defineFastMethod("=~", callbackFactory.getFastMethod("match", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("~", callbackFactory.getFastMethod("match2"));
         stringClass.defineFastMethod("capitalize", callbackFactory.getFastMethod("capitalize"));
         stringClass.defineFastMethod("capitalize!", callbackFactory.getFastMethod("capitalize_bang"));
         stringClass.defineFastMethod("casecmp", callbackFactory.getFastMethod("casecmp", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("center", callbackFactory.getFastOptMethod("center"));
         stringClass.defineFastMethod("chop", callbackFactory.getFastMethod("chop"));
         stringClass.defineFastMethod("chop!", callbackFactory.getFastMethod("chop_bang"));
         stringClass.defineFastMethod("chomp", callbackFactory.getFastOptMethod("chomp"));
         stringClass.defineFastMethod("chomp!", callbackFactory.getFastOptMethod("chomp_bang"));
         stringClass.defineFastMethod("concat", callbackFactory.getFastMethod("concat", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("count", callbackFactory.getFastOptMethod("count"));
         stringClass.defineFastMethod("crypt", callbackFactory.getFastMethod("crypt", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("delete", callbackFactory.getFastOptMethod("delete"));
         stringClass.defineFastMethod("delete!", callbackFactory.getFastOptMethod("delete_bang"));
         stringClass.defineFastMethod("downcase", callbackFactory.getFastMethod("downcase"));
         stringClass.defineFastMethod("downcase!", callbackFactory.getFastMethod("downcase_bang"));
         stringClass.defineFastMethod("dump", callbackFactory.getFastMethod("dump"));
         stringClass.defineMethod("each_line", callbackFactory.getOptMethod("each_line"));
         stringClass.defineMethod("each_byte", callbackFactory.getMethod("each_byte"));
         stringClass.defineFastMethod("empty?", callbackFactory.getFastMethod("empty"));
         stringClass.defineMethod("gsub", callbackFactory.getOptMethod("gsub"));
         stringClass.defineMethod("gsub!", callbackFactory.getOptMethod("gsub_bang"));
         stringClass.defineFastMethod("hex", callbackFactory.getFastMethod("hex"));
         stringClass.defineFastMethod("include?", callbackFactory.getFastMethod("include", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("index", callbackFactory.getFastOptMethod("index"));
         stringClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         stringClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("insert", callbackFactory.getFastMethod("insert", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         stringClass.defineFastMethod("length", callbackFactory.getFastMethod("length"));
         stringClass.defineFastMethod("ljust", callbackFactory.getFastOptMethod("ljust"));
         stringClass.defineFastMethod("lstrip", callbackFactory.getFastMethod("lstrip"));
         stringClass.defineFastMethod("lstrip!", callbackFactory.getFastMethod("lstrip_bang"));
         stringClass.defineFastMethod("match", callbackFactory.getFastMethod("match3", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("oct", callbackFactory.getFastMethod("oct"));
         stringClass.defineFastMethod("replace", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("reverse", callbackFactory.getFastMethod("reverse"));
         stringClass.defineFastMethod("reverse!", callbackFactory.getFastMethod("reverse_bang"));
         stringClass.defineFastMethod("rindex", callbackFactory.getFastOptMethod("rindex"));
         stringClass.defineFastMethod("rjust", callbackFactory.getFastOptMethod("rjust"));
         stringClass.defineFastMethod("rstrip", callbackFactory.getFastMethod("rstrip"));
         stringClass.defineFastMethod("rstrip!", callbackFactory.getFastMethod("rstrip_bang"));
         stringClass.defineMethod("scan", callbackFactory.getMethod("scan", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("slice!", callbackFactory.getFastOptMethod("slice_bang"));
         stringClass.defineFastMethod("split", callbackFactory.getFastOptMethod("split"));
         stringClass.defineFastMethod("strip", callbackFactory.getFastMethod("strip"));
         stringClass.defineFastMethod("strip!", callbackFactory.getFastMethod("strip_bang"));
         stringClass.defineFastMethod("succ", callbackFactory.getFastMethod("succ"));
         stringClass.defineFastMethod("succ!", callbackFactory.getFastMethod("succ_bang"));
         stringClass.defineFastMethod("squeeze", callbackFactory.getFastOptMethod("squeeze"));
         stringClass.defineFastMethod("squeeze!", callbackFactory.getFastOptMethod("squeeze_bang"));
         stringClass.defineMethod("sub", callbackFactory.getOptMethod("sub"));
         stringClass.defineMethod("sub!", callbackFactory.getOptMethod("sub_bang"));
         stringClass.defineFastMethod("sum", callbackFactory.getFastOptMethod("sum"));
         stringClass.defineFastMethod("swapcase", callbackFactory.getFastMethod("swapcase"));
         stringClass.defineFastMethod("swapcase!", callbackFactory.getFastMethod("swapcase_bang"));
         stringClass.defineFastMethod("to_f", callbackFactory.getFastMethod("to_f"));
         stringClass.defineFastMethod("to_i", callbackFactory.getFastOptMethod("to_i"));
         stringClass.defineFastMethod("to_str", callbackFactory.getFastMethod("to_s"));
         stringClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         stringClass.defineFastMethod("to_sym", callbackFactory.getFastMethod("to_sym"));
         stringClass.defineFastMethod("tr", callbackFactory.getFastMethod("tr", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("tr!", callbackFactory.getFastMethod("tr_bang", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("tr_s", callbackFactory.getFastMethod("tr_s", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("tr_s!", callbackFactory.getFastMethod("tr_s_bang", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("unpack", callbackFactory.getFastMethod("unpack", RubyKernel.IRUBY_OBJECT));
         stringClass.defineFastMethod("upcase", callbackFactory.getFastMethod("upcase"));
         stringClass.defineFastMethod("upcase!", callbackFactory.getFastMethod("upcase_bang"));
         stringClass.defineMethod("upto", callbackFactory.getMethod("upto", RubyKernel.IRUBY_OBJECT));
         
         stringClass.defineAlias("<<", "concat");
         stringClass.defineAlias("each", "each_line");
         stringClass.defineAlias("intern", "to_sym");
         stringClass.defineAlias("next", "succ");
         stringClass.defineAlias("next!", "succ!");
         stringClass.defineAlias("size", "length");
         stringClass.defineAlias("slice", "[]");
         
         return stringClass;
     }
 
     public static final byte OP_PLUS_SWITCHVALUE = 1;
     public static final byte OP_LT_SWITCHVALUE = 2;
     public static final byte AREF_SWITCHVALUE = 3;
     public static final byte ASET_SWITCHVALUE = 4;
     public static final byte NIL_P_SWITCHVALUE = 5;
     public static final byte EQUALEQUAL_SWITCHVALUE = 6;
     public static final byte OP_GE_SWITCHVALUE = 7;
     public static final byte OP_LSHIFT_SWITCHVALUE = 8;
     public static final byte EMPTY_P_SWITCHVALUE = 9;
     public static final byte TO_S_SWITCHVALUE = 10;
     public static final byte TO_I_SWITCHVALUE = 11;
     public static final byte TO_STR_SWITCHVALUE = 12;
     public static final byte TO_SYM_SWITCHVALUE = 13;
     public static final byte HASH_SWITCHVALUE = 14;
     public static final byte OP_GT_SWITCHVALUE = 15;
     public static final byte OP_TIMES_SWITCHVALUE = 16;
     public static final byte OP_LE_SWITCHVALUE = 17;
     public static final byte OP_SPACESHIP_SWITCHVALUE = 18;
     public static final byte LENGTH_SWITCHVALUE = 19;
     public static final byte MATCH_SWITCHVALUE = 20;
     public static final byte EQQ_SWITCHVALUE = 21;
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case OP_PLUS_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_plus(args[0]);
         case OP_LT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_lt(args[0]);
         case AREF_SWITCHVALUE:
             return aref(args);
         case ASET_SWITCHVALUE:
             return aset(args);
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
-            return eql(args[0]);
+            return equal(args[0]);
         case OP_GE_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_ge(args[0]);
         case OP_LSHIFT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return concat(args[0]);
         case EMPTY_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return empty();
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case TO_I_SWITCHVALUE:
             return to_i(args);
         case TO_STR_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case TO_SYM_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_sym();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case OP_GT_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_gt(args[0]);
         case OP_TIMES_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_mul(args[0]);
         case OP_LE_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_le(args[0]);
         case OP_SPACESHIP_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_cmp(args[0]);
         case LENGTH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return length();
         case MATCH_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return match(args[0]);
         case EQQ_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
 
+    /** short circuit for String key comparison
+     * 
+     */
+    public final boolean eql(IRubyObject other) {
+        return other instanceof RubyString && value.equal(((RubyString)other).value);
+    }    
+    
     // @see IRuby.newString(...)
     private RubyString(Ruby runtime, CharSequence value) {
             this(runtime, runtime.getString(), value);
     }
 
     private RubyString(Ruby runtime, byte[] value) {
             this(runtime, runtime.getString(), value);
     }
 
     private RubyString(Ruby runtime, ByteList value) {
             this(runtime, runtime.getString(), value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
 
         assert value != null;
 
         this.value = new ByteList(ByteList.plain(value),false);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
 
         assert value != null;
 
         this.value = new ByteList(value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
 
         assert value != null;
 
         this.value = value;
     }
 
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     public Class getJavaClass() {
         return String.class;
     }
 
     public RubyString convertToString() {
         return this;
     }
 
     public String toString() {
         return value.toString();
     }
 
     /** rb_str_dup
      * 
      */
     public final RubyString strDup() {
         return strDup(getMetaClass());
     }
 
     private final RubyString strDup(RubyClass clazz) {
         shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString dup = new RubyString(getRuntime(), clazz, value);
         dup.shareLevel = SHARE_LEVEL_BYTELIST;
 
         dup.infectBy(this);
         return dup;
     }    
 
     public final RubyString makeShared(int index, int len) {
         if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
         RubyString shared = new RubyString(getRuntime(), getMetaClass(), value.makeShared(index, len));
         shared.shareLevel = SHARE_LEVEL_BUFFER;
 
         shared.infectBy(this);
         return shared;
     }
 
     private final void modifyCheck() {
         // TODO: tmp lock here!
         testFrozen("string");
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
 
     /** rb_str_modify
      * 
      */
     public final void modify() {
         modifyCheck();
 
         if(shareLevel != SHARE_LEVEL_NONE){
             if(shareLevel == SHARE_LEVEL_BYTELIST) {            
                 value = value.dup();
             } else if(shareLevel == SHARE_LEVEL_BUFFER) {
                 value.unshare();
             }
             shareLevel = SHARE_LEVEL_NONE;
         }
         value.invalidate();
     }
     
     private final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shareLevel = SHARE_LEVEL_NONE;
     }
 
     private final void view(byte[]bytes) {
         modifyCheck();        
 
         value.replace(bytes);
         shareLevel = SHARE_LEVEL_NONE;
 
         value.invalidate();        
     }
 
     private final void view(int index, int len) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.makeShared(index, len);
                 shareLevel = SHARE_LEVEL_BUFFER; 
             } else if (shareLevel == SHARE_LEVEL_BUFFER) {
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
         return bytesToString(bytes.unsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     public static boolean isDigit(int c) {
         return c >= '0' && c <= '9';
     }
 
     public static boolean isUpper(int c) {
         return c >= 'A' && c <= 'Z';
     }
 
     public static boolean isLower(int c) {
         return c >= 'a' && c <= 'z';
     }
 
     public static boolean isLetter(int c) {
         return isUpper(c) || isLower(c);
     }
 
     public static boolean isAlnum(int c) {
         return isUpper(c) || isLower(c) || isDigit(c);
     }
 
     public static boolean isPrint(int c) {
         return c >= 0x20 && c <= 0x7E;
     }
     
     public IRubyObject checkStringType() {
         return this;
     }
 
     public IRubyObject to_s() {
         if (getMetaClass().getRealClass() != getRuntime().getString()) {
             return strDup(getRuntime().getString());
         }
         return this;
     }
 
     /* rb_str_cmp_m */
     public IRubyObject op_cmp(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newFixnum(cmp((RubyString)other));
         }
 
         return getRuntime().getNil();
     }
-
-    public IRubyObject eql(IRubyObject other) {
-        Ruby runtime = getRuntime();
-        if (other == this) return runtime.getTrue();
         
+    /**
+     * 
+     */
+    public IRubyObject equal(IRubyObject other) {
+        if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyString)) {
-            if (other.respondsTo("to_str")) {
-                return other.callMethod(runtime.getCurrentContext(), MethodIndex.EQUALEQUAL, "==", this);
+            if (!other.respondsTo("to_str")) return getRuntime().getFalse();
+            Ruby runtime = getRuntime();
+            return other.callMethod(runtime.getCurrentContext(), MethodIndex.EQUALEQUAL, "==", this).isTrue() ? runtime.getTrue() : runtime.getFalse();
             }
-            return runtime.getFalse();
+        return value.equal(((RubyString)other).value) ? getRuntime().getTrue() : getRuntime().getFalse();
         }
-        /* use Java implementation if both different String instances */
-        return runtime.newBoolean(value.equals(((RubyString) other).value));
-    }
 
     public IRubyObject op_plus(IRubyObject other) {
         RubyString str = RubyString.stringValue(other);
 
         ByteList newValue = new ByteList(value.length() + str.value.length());
         newValue.append(value);
         newValue.append(str.value);
         return newString(getRuntime(), newValue).infectBy(other).infectBy(this);
     }
 
     public IRubyObject op_mul(IRubyObject other) {
         RubyInteger otherInteger = (RubyInteger) other.convertToInteger();
         long len = otherInteger.getLongValue();
 
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.length()) {
             throw getRuntime().newArgumentError("argument too big");
         }
         ByteList newBytes = new ByteList(value.length() * (int)len);
 
         for (int i = 0; i < len; i++) {
             newBytes.append(value);
         }
 
         RubyString newString = newString(getRuntime(), newBytes);
         newString.setTaint(isTaint());
         return newString;
     }
 
     public IRubyObject format(IRubyObject arg) {
         // FIXME: Should we make this work with platform's locale, or continue hardcoding US?
         return getRuntime().newString((ByteList)Sprintf.sprintf(Locale.US,getByteList(),arg));
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(value.hashCode());
     }
 
     public int hashCode() {
         return value.hashCode();
     }
 
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             RubyString string = (RubyString) other;
 
-            if (string.value.equals(value)) return true;
+            if (string.value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
 
         IRubyObject str = obj.callMethod(obj.getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
 
         if (obj.isTaint()) str.setTaint(true);
 
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public int cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         return toString();
     }
 
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     // Methods of the String class (rb_str_*):
 
     /** rb_str_new2
      *
      */
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, str);
     }
 
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         try {
             return new RubyString(runtime, str.getBytes("UTF8"));
         } catch (UnsupportedEncodingException uee) {
             return new RubyString(runtime, str);
         }
     }
 
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, bytes);
     }
     
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         RubyString str = new RubyString(runtime, bytes);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }    
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] bytes2 = new byte[length];
         System.arraycopy(bytes, start, bytes2, 0, length);
         return new RubyString(runtime, bytes2);
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     // FIXME: cat methods should be more aware of sharing to prevent unnecessary reallocations in certain situations 
     public RubyString cat(byte[] str) {
         modify();
         value.append(str);
         return this;
     }
 
     public RubyString cat(byte[] str, int beg, int len) {
         modify();        
         value.append(str, beg, len);
         return this;
     }
 
     public RubyString cat(ByteList str) {
         modify();        
         value.append(str);
         return this;
     }
 
     public RubyString cat(byte ch) {
         modify();        
         value.append(ch);
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     public RubyString replace(IRubyObject other) {
         modifyCheck();
 
         if (this == other) return this;
          
         RubyString otherStr =  stringValue(other);
 
         shareLevel = SHARE_LEVEL_BYTELIST;
         otherStr.shareLevel = SHARE_LEVEL_BYTELIST;
         
         value = otherStr.value;
 
         infectBy(other);
         return this;
     }
 
     public RubyString reverse() {
         if (value.length() <= 1) return strDup();
 
         ByteList buf = new ByteList(value.length()+2);
         buf.realSize = value.length();
         int src = value.length() - 1;
         int dst = 0;
         
         while (src >= 0) buf.set(dst++, value.get(src--));
 
         RubyString rev = new RubyString(getRuntime(), getMetaClass(), buf);
         rev.infectBy(this);
         return rev;
     }
 
     public RubyString reverse_bang() {
         if (value.length() > 1) {
             modify();
             for (int i = 0; i < (value.length() / 2); i++) {
                 byte b = (byte) value.get(i);
                 
                 value.set(i, value.get(value.length() - i - 1));
                 value.set(value.length() - i - 1, b);
             }
         }
         
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newString(recv.getRuntime(), "");
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 1) replace(args[0]);
 
         return this;
     }
 
     public IRubyObject casecmp(IRubyObject other) {
         int compare = toString().compareToIgnoreCase(stringValue(other).toString());
         
         return RubyFixnum.newFixnum(getRuntime(), compare == 0 ? 0 : (compare < 0 ? -1 : 1));
     }
 
     /** rb_str_match
      *
      */
     public IRubyObject match(IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).match(this);
         if (other instanceof RubyString) {
             throw getRuntime().newTypeError("type mismatch: String given");
         }
         return other.callMethod(getRuntime().getCurrentContext(), "=~", this);
     }
 
     /** rb_str_match2
      *
      */
     public IRubyObject match2() {
         return RubyRegexp.newRegexp(this, 0, null).match2();
     }
 
     /**
      * String#match(pattern)
      *
      * @param pattern Regexp or String
      */
     public IRubyObject match3(IRubyObject pattern) {
         if (pattern instanceof RubyRegexp) return ((RubyRegexp)pattern).search2(toString(), this);
         if (pattern instanceof RubyString) {
             RubyRegexp regexp = RubyRegexp.newRegexp((RubyString) pattern, 0, null);
             return regexp.search2(toString(), this);
         } else if (pattern.respondsTo("to_str")) {
             // FIXME: is this cast safe?
             RubyRegexp regexp = RubyRegexp.newRegexp((RubyString) pattern.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY), 0, null);
             return regexp.search2(toString(), this);
         }
 
         // not regexp and not string, can't convert
         throw getRuntime().newTypeError("wrong argument type " + pattern.getMetaClass().getBaseName() + " (expected Regexp)");
     }
 
     /** rb_str_capitalize
      *
      */
     public IRubyObject capitalize() {
         RubyString str = strDup();
         str.capitalize_bang();
         return str;
     }
 
     /** rb_str_capitalize_bang
      *
      */
     public IRubyObject capitalize_bang() {
         if (value.length() == 0) return getRuntime().getNil();
         
         modify();
         
         char capital = value.charAt(0);
         boolean changed = false;
         if (Character.isLetter(capital) && Character.isLowerCase(capital)) {
             value.set(0, (byte)Character.toUpperCase(capital));
             changed = true;
         }
 
         for (int i = 1; i < value.length(); i++) {
             capital = value.charAt(i);
             if (Character.isLetter(capital) && Character.isUpperCase(capital)) {
                 value.set(i, (byte)Character.toLowerCase(capital));
                 changed = true;
             }
         }
 
         return changed ? this : getRuntime().getNil();
     }
 
     public IRubyObject op_ge(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) >= 0);
         }
 
         return RubyComparable.op_ge(this, other);
     }
 
     public IRubyObject op_gt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) > 0);
         }
 
         return RubyComparable.op_gt(this, other);
     }
 
     public IRubyObject op_le(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) <= 0);
         }
 
         return RubyComparable.op_le(this, other);
     }
 
     public IRubyObject op_lt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(cmp((RubyString) other) < 0);
         }
 
         return RubyComparable.op_lt(this, other);
     }
 
-    public IRubyObject op_eql(IRubyObject other) {
-        return equals(other) ? other.getRuntime().getTrue() : other.getRuntime().getFalse();
+    public IRubyObject eql_p(IRubyObject other) {
+        if (!(other instanceof RubyString)) return getRuntime().getFalse();
+        RubyString otherString = (RubyString)other;
+        return value.equal(otherString.value) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_str_upcase
      *
      */
     public RubyString upcase() {
         RubyString str = strDup();
         str.upcase_bang();
         return str;
     }
 
     /** rb_str_upcase_bang
      *
      */
     public IRubyObject upcase_bang() {
         if (value.length() == 0)  return getRuntime().getNil();
         
         boolean changed = false;
         
         modify();
         
         for (int i = 0; i < value.length(); i++) {
             char c = value.charAt(i);
             if (!Character.isLetter(c) || Character.isUpperCase(c)) continue;
             value.set(i, (byte)Character.toUpperCase(c));
             changed = true;
         }
         return changed ? this : getRuntime().getNil();
     }
 
     /** rb_str_downcase
      *
      */
     public RubyString downcase() {
         RubyString str = strDup();
         str.downcase_bang();
         return str;
     }
 
     /** rb_str_downcase_bang
      *
      */
     public IRubyObject downcase_bang() {
         if (value.length() == 0)  return getRuntime().getNil();        
         
         boolean changed = false;
         
         modify();
         
         for (int i = 0; i < value.length(); i++) {
             char c = value.charAt(i);
             if (!Character.isLetter(c) || Character.isLowerCase(c)) continue;
             value.set(i, (byte)Character.toLowerCase(c));
             changed = true;
         }
         
         return changed ? this : getRuntime().getNil();
     }
 
     /** rb_str_swapcase
      *
      */
     public RubyString swapcase() {
         RubyString str = strDup();
         str.swapcase_bang();
         return str;
     }
 
     /** rb_str_swapcase_bang
      *
      */
     public IRubyObject swapcase_bang() {
         if (value.length() == 0)  return getRuntime().getNil();        
         
         boolean changed = false;
         
         modify();        
 
         for (int i = 0; i < value.length(); i++) {
             char c = value.charAt(i);
 
             if (!Character.isLetter(c)) {
                 continue;
             } else if (Character.isLowerCase(c)) {
                 changed = true;
                 value.set(i, (byte)Character.toUpperCase(c));
             } else {
                 changed = true;
                 value.set(i, (byte)Character.toLowerCase(c));
             }
         }
         
         return changed ? this : getRuntime().getNil();
     }
 
     /** rb_str_dump
      *
      */
     public RubyString dump() {
         return inspect(true);
     }
 
     public IRubyObject insert(IRubyObject indexArg, IRubyObject stringArg) {
         int index = (int) indexArg.convertToInteger().getLongValue();
         if (index < 0) index += value.length() + 1;
 
         if (index < 0 || index > value.length()) {
             throw getRuntime().newIndexError("index " + index + " out of range");
         }
 
         modify();
         
         ByteList insert = ((RubyString)stringArg.convertToString()).value;
         value.unsafeReplace(index, 0, insert);
         return this;
     }
 
     /** rb_str_inspect
      *
      */
     public IRubyObject inspect() {
         return inspect(false);
     }
 
     private RubyString inspect(boolean dump) {
         final int length = value.length();
         Ruby runtime = getRuntime();
         ByteList sb = new ByteList(length + 2 + length / 100);
 
         sb.append('\"');
 
         // FIXME: This may not be unicode-safe
         for (int i = 0; i < length; i++) {
             int c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 sb.append((char)c);
             } else if (runtime.getKCode() == KCode.UTF8 && c == 0xEF) {
                 // don't escape encoded UTF8 characters, leave them as bytes
                 // append byte order mark plus two character bytes
                 sb.append((char)c);
                 sb.append((char)(value.get(++i) & 0xFF));
                 sb.append((char)(value.get(++i) & 0xFF));
             } else if (c == '\"' || c == '\\') {
                 sb.append('\\').append((char)c);
             } else if (c == '#') {
                 sb.append('\\').append((char)c);
             } else if (isPrint(c)) {
                 sb.append((char)c);
             } else if (c == '\n') {
                 sb.append('\\').append('n');
             } else if (c == '\r') {
                 sb.append('\\').append('r');
             } else if (c == '\t') {
                 sb.append('\\').append('t');
             } else if (c == '\f') {
                 sb.append('\\').append('f');
             } else if (c == '\u000B') {
                 sb.append('\\').append('v');
             } else if (c == '\u0007') {
                 sb.append('\\').append('a');
             } else if (c == '\u001B') {
                 sb.append('\\').append('e');
             } else {
                 sb.append(ByteList.plain(Sprintf.sprintf(runtime,"\\%.3o",c)));
             }
         }
 
         sb.append('\"');
         return getRuntime().newString(sb);
     }
 
     /** rb_str_length
      *
      */
     public RubyFixnum length() {
         return getRuntime().newFixnum(value.length());
     }
 
     /** rb_str_empty
      *
      */
     public RubyBoolean empty() {
         return isEmpty() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     private boolean isEmpty() {
         return value.length() == 0;
     }
 
     /** rb_str_append
      *
      */
     public RubyString append(IRubyObject other) {
         infectBy(other);
         return cat(stringValue(other).value);
     }
 
     /** rb_str_concat
      *
      */
     public RubyString concat(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long value = ((RubyFixnum) other).getLongValue();
             if (value >= 0 && value < 256) return cat((byte) value);
         }
         return append(other);
     }
 
     /** rb_str_crypt
      *
      */
     public RubyString crypt(IRubyObject other) {
         String salt = stringValue(other).getValue().toString();
         if (salt.length() < 2) {
             throw getRuntime().newArgumentError("salt too short(need >=2 bytes)");
         }
 
         salt = salt.substring(0, 2);
         return getRuntime().newString(JavaCrypt.crypt(salt, this.toString()));
     }
 
 
     public static class JavaCrypt {
         private static java.util.Random r_gen = new java.util.Random();
 
         private static final char theBaseSalts[] = {
             'a','b','c','d','e','f','g','h','i','j','k','l','m',
             'n','o','p','q','r','s','t','u','v','w','x','y','z',
             'A','B','C','D','E','F','G','H','I','J','K','L','M',
             'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
             '0','1','2','3','4','5','6','7','8','9','/','.'};
 
         private static final int ITERATIONS = 16;
 
         private static final int con_salt[] = {
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
             0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
             0x0A, 0x0B, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A,
             0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12,
             0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
             0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22,
             0x23, 0x24, 0x25, 0x20, 0x21, 0x22, 0x23, 0x24,
             0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C,
             0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34,
             0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C,
             0x3D, 0x3E, 0x3F, 0x00, 0x00, 0x00, 0x00, 0x00,
         };
 
         private static final boolean shifts2[] = {
             false, false, true, true, true, true, true, true,
             false, true,  true, true, true, true, true, false };
 
         private static final int skb[][] = {
             {
                 /* for C bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x00000010, 0x20000000, 0x20000010,
                 0x00010000, 0x00010010, 0x20010000, 0x20010010,
                 0x00000800, 0x00000810, 0x20000800, 0x20000810,
                 0x00010800, 0x00010810, 0x20010800, 0x20010810,
                 0x00000020, 0x00000030, 0x20000020, 0x20000030,
                 0x00010020, 0x00010030, 0x20010020, 0x20010030,
                 0x00000820, 0x00000830, 0x20000820, 0x20000830,
                 0x00010820, 0x00010830, 0x20010820, 0x20010830,
                 0x00080000, 0x00080010, 0x20080000, 0x20080010,
                 0x00090000, 0x00090010, 0x20090000, 0x20090010,
                 0x00080800, 0x00080810, 0x20080800, 0x20080810,
                 0x00090800, 0x00090810, 0x20090800, 0x20090810,
                 0x00080020, 0x00080030, 0x20080020, 0x20080030,
                 0x00090020, 0x00090030, 0x20090020, 0x20090030,
                 0x00080820, 0x00080830, 0x20080820, 0x20080830,
                 0x00090820, 0x00090830, 0x20090820, 0x20090830,
             },{
                 /* for C bits (numbered as per FIPS 46) 7 8 10 11 12 13 */
                 0x00000000, 0x02000000, 0x00002000, 0x02002000,
                 0x00200000, 0x02200000, 0x00202000, 0x02202000,
                 0x00000004, 0x02000004, 0x00002004, 0x02002004,
                 0x00200004, 0x02200004, 0x00202004, 0x02202004,
                 0x00000400, 0x02000400, 0x00002400, 0x02002400,
                 0x00200400, 0x02200400, 0x00202400, 0x02202400,
                 0x00000404, 0x02000404, 0x00002404, 0x02002404,
                 0x00200404, 0x02200404, 0x00202404, 0x02202404,
                 0x10000000, 0x12000000, 0x10002000, 0x12002000,
                 0x10200000, 0x12200000, 0x10202000, 0x12202000,
                 0x10000004, 0x12000004, 0x10002004, 0x12002004,
                 0x10200004, 0x12200004, 0x10202004, 0x12202004,
                 0x10000400, 0x12000400, 0x10002400, 0x12002400,
                 0x10200400, 0x12200400, 0x10202400, 0x12202400,
                 0x10000404, 0x12000404, 0x10002404, 0x12002404,
                 0x10200404, 0x12200404, 0x10202404, 0x12202404,
             },{
                 /* for C bits (numbered as per FIPS 46) 14 15 16 17 19 20 */
                 0x00000000, 0x00000001, 0x00040000, 0x00040001,
                 0x01000000, 0x01000001, 0x01040000, 0x01040001,
                 0x00000002, 0x00000003, 0x00040002, 0x00040003,
                 0x01000002, 0x01000003, 0x01040002, 0x01040003,
                 0x00000200, 0x00000201, 0x00040200, 0x00040201,
                 0x01000200, 0x01000201, 0x01040200, 0x01040201,
                 0x00000202, 0x00000203, 0x00040202, 0x00040203,
                 0x01000202, 0x01000203, 0x01040202, 0x01040203,
                 0x08000000, 0x08000001, 0x08040000, 0x08040001,
                 0x09000000, 0x09000001, 0x09040000, 0x09040001,
                 0x08000002, 0x08000003, 0x08040002, 0x08040003,
                 0x09000002, 0x09000003, 0x09040002, 0x09040003,
                 0x08000200, 0x08000201, 0x08040200, 0x08040201,
                 0x09000200, 0x09000201, 0x09040200, 0x09040201,
                 0x08000202, 0x08000203, 0x08040202, 0x08040203,
                 0x09000202, 0x09000203, 0x09040202, 0x09040203,
             },{
                 /* for C bits (numbered as per FIPS 46) 21 23 24 26 27 28 */
                 0x00000000, 0x00100000, 0x00000100, 0x00100100,
                 0x00000008, 0x00100008, 0x00000108, 0x00100108,
                 0x00001000, 0x00101000, 0x00001100, 0x00101100,
                 0x00001008, 0x00101008, 0x00001108, 0x00101108,
                 0x04000000, 0x04100000, 0x04000100, 0x04100100,
                 0x04000008, 0x04100008, 0x04000108, 0x04100108,
                 0x04001000, 0x04101000, 0x04001100, 0x04101100,
                 0x04001008, 0x04101008, 0x04001108, 0x04101108,
                 0x00020000, 0x00120000, 0x00020100, 0x00120100,
                 0x00020008, 0x00120008, 0x00020108, 0x00120108,
                 0x00021000, 0x00121000, 0x00021100, 0x00121100,
                 0x00021008, 0x00121008, 0x00021108, 0x00121108,
                 0x04020000, 0x04120000, 0x04020100, 0x04120100,
                 0x04020008, 0x04120008, 0x04020108, 0x04120108,
                 0x04021000, 0x04121000, 0x04021100, 0x04121100,
                 0x04021008, 0x04121008, 0x04021108, 0x04121108,
             },{
                 /* for D bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x10000000, 0x00010000, 0x10010000,
                 0x00000004, 0x10000004, 0x00010004, 0x10010004,
                 0x20000000, 0x30000000, 0x20010000, 0x30010000,
                 0x20000004, 0x30000004, 0x20010004, 0x30010004,
                 0x00100000, 0x10100000, 0x00110000, 0x10110000,
                 0x00100004, 0x10100004, 0x00110004, 0x10110004,
                 0x20100000, 0x30100000, 0x20110000, 0x30110000,
                 0x20100004, 0x30100004, 0x20110004, 0x30110004,
                 0x00001000, 0x10001000, 0x00011000, 0x10011000,
                 0x00001004, 0x10001004, 0x00011004, 0x10011004,
                 0x20001000, 0x30001000, 0x20011000, 0x30011000,
                 0x20001004, 0x30001004, 0x20011004, 0x30011004,
                 0x00101000, 0x10101000, 0x00111000, 0x10111000,
                 0x00101004, 0x10101004, 0x00111004, 0x10111004,
                 0x20101000, 0x30101000, 0x20111000, 0x30111000,
                 0x20101004, 0x30101004, 0x20111004, 0x30111004,
             },{
                 /* for D bits (numbered as per FIPS 46) 8 9 11 12 13 14 */
                 0x00000000, 0x08000000, 0x00000008, 0x08000008,
                 0x00000400, 0x08000400, 0x00000408, 0x08000408,
                 0x00020000, 0x08020000, 0x00020008, 0x08020008,
                 0x00020400, 0x08020400, 0x00020408, 0x08020408,
                 0x00000001, 0x08000001, 0x00000009, 0x08000009,
                 0x00000401, 0x08000401, 0x00000409, 0x08000409,
                 0x00020001, 0x08020001, 0x00020009, 0x08020009,
                 0x00020401, 0x08020401, 0x00020409, 0x08020409,
                 0x02000000, 0x0A000000, 0x02000008, 0x0A000008,
                 0x02000400, 0x0A000400, 0x02000408, 0x0A000408,
                 0x02020000, 0x0A020000, 0x02020008, 0x0A020008,
                 0x02020400, 0x0A020400, 0x02020408, 0x0A020408,
                 0x02000001, 0x0A000001, 0x02000009, 0x0A000009,
                 0x02000401, 0x0A000401, 0x02000409, 0x0A000409,
                 0x02020001, 0x0A020001, 0x02020009, 0x0A020009,
                 0x02020401, 0x0A020401, 0x02020409, 0x0A020409,
             },{
                 /* for D bits (numbered as per FIPS 46) 16 17 18 19 20 21 */
                 0x00000000, 0x00000100, 0x00080000, 0x00080100,
                 0x01000000, 0x01000100, 0x01080000, 0x01080100,
                 0x00000010, 0x00000110, 0x00080010, 0x00080110,
                 0x01000010, 0x01000110, 0x01080010, 0x01080110,
                 0x00200000, 0x00200100, 0x00280000, 0x00280100,
                 0x01200000, 0x01200100, 0x01280000, 0x01280100,
                 0x00200010, 0x00200110, 0x00280010, 0x00280110,
                 0x01200010, 0x01200110, 0x01280010, 0x01280110,
                 0x00000200, 0x00000300, 0x00080200, 0x00080300,
                 0x01000200, 0x01000300, 0x01080200, 0x01080300,
                 0x00000210, 0x00000310, 0x00080210, 0x00080310,
                 0x01000210, 0x01000310, 0x01080210, 0x01080310,
                 0x00200200, 0x00200300, 0x00280200, 0x00280300,
                 0x01200200, 0x01200300, 0x01280200, 0x01280300,
                 0x00200210, 0x00200310, 0x00280210, 0x00280310,
                 0x01200210, 0x01200310, 0x01280210, 0x01280310,
             },{
                 /* for D bits (numbered as per FIPS 46) 22 23 24 25 27 28 */
                 0x00000000, 0x04000000, 0x00040000, 0x04040000,
                 0x00000002, 0x04000002, 0x00040002, 0x04040002,
                 0x00002000, 0x04002000, 0x00042000, 0x04042000,
                 0x00002002, 0x04002002, 0x00042002, 0x04042002,
                 0x00000020, 0x04000020, 0x00040020, 0x04040020,
                 0x00000022, 0x04000022, 0x00040022, 0x04040022,
                 0x00002020, 0x04002020, 0x00042020, 0x04042020,
                 0x00002022, 0x04002022, 0x00042022, 0x04042022,
                 0x00000800, 0x04000800, 0x00040800, 0x04040800,
                 0x00000802, 0x04000802, 0x00040802, 0x04040802,
                 0x00002800, 0x04002800, 0x00042800, 0x04042800,
                 0x00002802, 0x04002802, 0x00042802, 0x04042802,
                 0x00000820, 0x04000820, 0x00040820, 0x04040820,
                 0x00000822, 0x04000822, 0x00040822, 0x04040822,
                 0x00002820, 0x04002820, 0x00042820, 0x04042820,
                 0x00002822, 0x04002822, 0x00042822, 0x04042822,
             }
         };
 
         private static final int SPtrans[][] = {
             {
                 /* nibble 0 */
                 0x00820200, 0x00020000, 0x80800000, 0x80820200,
                 0x00800000, 0x80020200, 0x80020000, 0x80800000,
                 0x80020200, 0x00820200, 0x00820000, 0x80000200,
                 0x80800200, 0x00800000, 0x00000000, 0x80020000,
                 0x00020000, 0x80000000, 0x00800200, 0x00020200,
                 0x80820200, 0x00820000, 0x80000200, 0x00800200,
                 0x80000000, 0x00000200, 0x00020200, 0x80820000,
                 0x00000200, 0x80800200, 0x80820000, 0x00000000,
                 0x00000000, 0x80820200, 0x00800200, 0x80020000,
                 0x00820200, 0x00020000, 0x80000200, 0x00800200,
                 0x80820000, 0x00000200, 0x00020200, 0x80800000,
                 0x80020200, 0x80000000, 0x80800000, 0x00820000,
                 0x80820200, 0x00020200, 0x00820000, 0x80800200,
                 0x00800000, 0x80000200, 0x80020000, 0x00000000,
                 0x00020000, 0x00800000, 0x80800200, 0x00820200,
                 0x80000000, 0x80820000, 0x00000200, 0x80020200,
             },{
                 /* nibble 1 */
                 0x10042004, 0x00000000, 0x00042000, 0x10040000,
                 0x10000004, 0x00002004, 0x10002000, 0x00042000,
                 0x00002000, 0x10040004, 0x00000004, 0x10002000,
                 0x00040004, 0x10042000, 0x10040000, 0x00000004,
                 0x00040000, 0x10002004, 0x10040004, 0x00002000,
                 0x00042004, 0x10000000, 0x00000000, 0x00040004,
                 0x10002004, 0x00042004, 0x10042000, 0x10000004,
                 0x10000000, 0x00040000, 0x00002004, 0x10042004,
                 0x00040004, 0x10042000, 0x10002000, 0x00042004,
                 0x10042004, 0x00040004, 0x10000004, 0x00000000,
                 0x10000000, 0x00002004, 0x00040000, 0x10040004,
                 0x00002000, 0x10000000, 0x00042004, 0x10002004,
                 0x10042000, 0x00002000, 0x00000000, 0x10000004,
                 0x00000004, 0x10042004, 0x00042000, 0x10040000,
                 0x10040004, 0x00040000, 0x00002004, 0x10002000,
                 0x10002004, 0x00000004, 0x10040000, 0x00042000,
             },{
                 /* nibble 2 */
                 0x41000000, 0x01010040, 0x00000040, 0x41000040,
                 0x40010000, 0x01000000, 0x41000040, 0x00010040,
                 0x01000040, 0x00010000, 0x01010000, 0x40000000,
                 0x41010040, 0x40000040, 0x40000000, 0x41010000,
                 0x00000000, 0x40010000, 0x01010040, 0x00000040,
                 0x40000040, 0x41010040, 0x00010000, 0x41000000,
                 0x41010000, 0x01000040, 0x40010040, 0x01010000,
                 0x00010040, 0x00000000, 0x01000000, 0x40010040,
                 0x01010040, 0x00000040, 0x40000000, 0x00010000,
                 0x40000040, 0x40010000, 0x01010000, 0x41000040,
                 0x00000000, 0x01010040, 0x00010040, 0x41010000,
                 0x40010000, 0x01000000, 0x41010040, 0x40000000,
                 0x40010040, 0x41000000, 0x01000000, 0x41010040,
                 0x00010000, 0x01000040, 0x41000040, 0x00010040,
                 0x01000040, 0x00000000, 0x41010000, 0x40000040,
                 0x41000000, 0x40010040, 0x00000040, 0x01010000,
             },{
                 /* nibble 3 */
                 0x00100402, 0x04000400, 0x00000002, 0x04100402,
                 0x00000000, 0x04100000, 0x04000402, 0x00100002,
                 0x04100400, 0x04000002, 0x04000000, 0x00000402,
                 0x04000002, 0x00100402, 0x00100000, 0x04000000,
                 0x04100002, 0x00100400, 0x00000400, 0x00000002,
                 0x00100400, 0x04000402, 0x04100000, 0x00000400,
                 0x00000402, 0x00000000, 0x00100002, 0x04100400,
                 0x04000400, 0x04100002, 0x04100402, 0x00100000,
                 0x04100002, 0x00000402, 0x00100000, 0x04000002,
                 0x00100400, 0x04000400, 0x00000002, 0x04100000,
                 0x04000402, 0x00000000, 0x00000400, 0x00100002,
                 0x00000000, 0x04100002, 0x04100400, 0x00000400,
                 0x04000000, 0x04100402, 0x00100402, 0x00100000,
                 0x04100402, 0x00000002, 0x04000400, 0x00100402,
                 0x00100002, 0x00100400, 0x04100000, 0x04000402,
                 0x00000402, 0x04000000, 0x04000002, 0x04100400,
             },{
                 /* nibble 4 */
                 0x02000000, 0x00004000, 0x00000100, 0x02004108,
                 0x02004008, 0x02000100, 0x00004108, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x00004100,
                 0x02000108, 0x02004008, 0x02004100, 0x00000000,
                 0x00004100, 0x02000000, 0x00004008, 0x00000108,
                 0x02000100, 0x00004108, 0x00000000, 0x02000008,
                 0x00000008, 0x02000108, 0x02004108, 0x00004008,
                 0x02004000, 0x00000100, 0x00000108, 0x02004100,
                 0x02004100, 0x02000108, 0x00004008, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x02000100,
                 0x02000000, 0x00004100, 0x02004108, 0x00000000,
                 0x00004108, 0x02000000, 0x00000100, 0x00004008,
                 0x02000108, 0x00000100, 0x00000000, 0x02004108,
                 0x02004008, 0x02004100, 0x00000108, 0x00004000,
                 0x00004100, 0x02004008, 0x02000100, 0x00000108,
                 0x00000008, 0x00004108, 0x02004000, 0x02000008,
             },{
                 /* nibble 5 */
                 0x20000010, 0x00080010, 0x00000000, 0x20080800,
                 0x00080010, 0x00000800, 0x20000810, 0x00080000,
                 0x00000810, 0x20080810, 0x00080800, 0x20000000,
                 0x20000800, 0x20000010, 0x20080000, 0x00080810,
                 0x00080000, 0x20000810, 0x20080010, 0x00000000,
                 0x00000800, 0x00000010, 0x20080800, 0x20080010,
                 0x20080810, 0x20080000, 0x20000000, 0x00000810,
                 0x00000010, 0x00080800, 0x00080810, 0x20000800,
                 0x00000810, 0x20000000, 0x20000800, 0x00080810,
                 0x20080800, 0x00080010, 0x00000000, 0x20000800,
                 0x20000000, 0x00000800, 0x20080010, 0x00080000,
                 0x00080010, 0x20080810, 0x00080800, 0x00000010,
                 0x20080810, 0x00080800, 0x00080000, 0x20000810,
                 0x20000010, 0x20080000, 0x00080810, 0x00000000,
                 0x00000800, 0x20000010, 0x20000810, 0x20080800,
                 0x20080000, 0x00000810, 0x00000010, 0x20080010,
             },{
                 /* nibble 6 */
                 0x00001000, 0x00000080, 0x00400080, 0x00400001,
                 0x00401081, 0x00001001, 0x00001080, 0x00000000,
                 0x00400000, 0x00400081, 0x00000081, 0x00401000,
                 0x00000001, 0x00401080, 0x00401000, 0x00000081,
                 0x00400081, 0x00001000, 0x00001001, 0x00401081,
                 0x00000000, 0x00400080, 0x00400001, 0x00001080,
                 0x00401001, 0x00001081, 0x00401080, 0x00000001,
                 0x00001081, 0x00401001, 0x00000080, 0x00400000,
                 0x00001081, 0x00401000, 0x00401001, 0x00000081,
                 0x00001000, 0x00000080, 0x00400000, 0x00401001,
                 0x00400081, 0x00001081, 0x00001080, 0x00000000,
                 0x00000080, 0x00400001, 0x00000001, 0x00400080,
                 0x00000000, 0x00400081, 0x00400080, 0x00001080,
                 0x00000081, 0x00001000, 0x00401081, 0x00400000,
                 0x00401080, 0x00000001, 0x00001001, 0x00401081,
                 0x00400001, 0x00401080, 0x00401000, 0x00001001,
             },{
                 /* nibble 7 */
                 0x08200020, 0x08208000, 0x00008020, 0x00000000,
                 0x08008000, 0x00200020, 0x08200000, 0x08208020,
                 0x00000020, 0x08000000, 0x00208000, 0x00008020,
                 0x00208020, 0x08008020, 0x08000020, 0x08200000,
                 0x00008000, 0x00208020, 0x00200020, 0x08008000,
                 0x08208020, 0x08000020, 0x00000000, 0x00208000,
                 0x08000000, 0x00200000, 0x08008020, 0x08200020,
                 0x00200000, 0x00008000, 0x08208000, 0x00000020,
                 0x00200000, 0x00008000, 0x08000020, 0x08208020,
                 0x00008020, 0x08000000, 0x00000000, 0x00208000,
                 0x08200020, 0x08008020, 0x08008000, 0x00200020,
                 0x08208000, 0x00000020, 0x00200020, 0x08008000,
                 0x08208020, 0x00200000, 0x08200000, 0x08000020,
                 0x00208000, 0x00008020, 0x08008020, 0x08200000,
                 0x00000020, 0x08208000, 0x00208020, 0x00000000,
                 0x08000000, 0x08200020, 0x00008000, 0x00208020
             }
         };
 
         private static final int cov_2char[] = {
             0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35,
             0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44,
             0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C,
             0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54,
             0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x61, 0x62,
             0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A,
             0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70, 0x71, 0x72,
             0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
         };
 
         private static final int byteToUnsigned(byte b) {
             return b & 0xFF;
         }
 
         private static int fourBytesToInt(byte b[], int offset) {
             int value;
             value  =  byteToUnsigned(b[offset++]);
             value |= (byteToUnsigned(b[offset++]) <<  8);
             value |= (byteToUnsigned(b[offset++]) << 16);
             value |= (byteToUnsigned(b[offset++]) << 24);
             return(value);
         }
 
         private static final void intToFourBytes(int iValue, byte b[], int offset) {
             b[offset++] = (byte)((iValue)        & 0xff);
             b[offset++] = (byte)((iValue >>> 8 ) & 0xff);
             b[offset++] = (byte)((iValue >>> 16) & 0xff);
             b[offset++] = (byte)((iValue >>> 24) & 0xff);
         }
 
         private static final void PERM_OP(int a, int b, int n, int m, int results[]) {
             int t;
 
             t = ((a >>> n) ^ b) & m;
             a ^= t << n;
             b ^= t;
 
             results[0] = a;
             results[1] = b;
         }
 
         private static final int HPERM_OP(int a, int n, int m) {
             int t;
 
             t = ((a << (16 - n)) ^ a) & m;
             a = a ^ t ^ (t >>> (16 - n));
 
             return a;
         }
 
         private static int [] des_set_key(byte key[]) {
             int schedule[] = new int[ITERATIONS * 2];
 
             int c = fourBytesToInt(key, 0);
             int d = fourBytesToInt(key, 4);
 
             int results[] = new int[2];
 
             PERM_OP(d, c, 4, 0x0f0f0f0f, results);
             d = results[0]; c = results[1];
 
             c = HPERM_OP(c, -2, 0xcccc0000);
             d = HPERM_OP(d, -2, 0xcccc0000);
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             PERM_OP(c, d, 8, 0x00ff00ff, results);
             c = results[0]; d = results[1];
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             d = (((d & 0x000000ff) <<  16) |  (d & 0x0000ff00)     |
                  ((d & 0x00ff0000) >>> 16) | ((c & 0xf0000000) >>> 4));
             c &= 0x0fffffff;
 
             int s, t;
             int j = 0;
 
             for(int i = 0; i < ITERATIONS; i ++) {
                 if(shifts2[i]) {
                     c = (c >>> 2) | (c << 26);
                     d = (d >>> 2) | (d << 26);
                 } else {
                     c = (c >>> 1) | (c << 27);
                     d = (d >>> 1) | (d << 27);
                 }
 
                 c &= 0x0fffffff;
                 d &= 0x0fffffff;
 
                 s = skb[0][ (c       ) & 0x3f                       ]|
                     skb[1][((c >>>  6) & 0x03) | ((c >>>  7) & 0x3c)]|
                     skb[2][((c >>> 13) & 0x0f) | ((c >>> 14) & 0x30)]|
                     skb[3][((c >>> 20) & 0x01) | ((c >>> 21) & 0x06) |
                            ((c >>> 22) & 0x38)];
 
                 t = skb[4][ (d     )  & 0x3f                       ]|
                     skb[5][((d >>> 7) & 0x03) | ((d >>>  8) & 0x3c)]|
                     skb[6][ (d >>>15) & 0x3f                       ]|
                     skb[7][((d >>>21) & 0x0f) | ((d >>> 22) & 0x30)];
 
                 schedule[j++] = ((t <<  16) | (s & 0x0000ffff)) & 0xffffffff;
                 s             = ((s >>> 16) | (t & 0xffff0000));
 
                 s             = (s << 4) | (s >>> 28);
                 schedule[j++] = s & 0xffffffff;
             }
             return(schedule);
         }
 
         private static final int D_ENCRYPT(int L, int R, int S, int E0, int E1, int s[]) {
             int t, u, v;
 
             v = R ^ (R >>> 16);
             u = v & E0;
             v = v & E1;
             u = (u ^ (u << 16)) ^ R ^ s[S];
             t = (v ^ (v << 16)) ^ R ^ s[S + 1];
             t = (t >>> 4) | (t << 28);
 
             L ^= SPtrans[1][(t       ) & 0x3f] |
                 SPtrans[3][(t >>>  8) & 0x3f] |
                 SPtrans[5][(t >>> 16) & 0x3f] |
                 SPtrans[7][(t >>> 24) & 0x3f] |
                 SPtrans[0][(u       ) & 0x3f] |
                 SPtrans[2][(u >>>  8) & 0x3f] |
                 SPtrans[4][(u >>> 16) & 0x3f] |
                 SPtrans[6][(u >>> 24) & 0x3f];
 
             return(L);
         }
 
         private static final int [] body(int schedule[], int Eswap0, int Eswap1) {
             int left = 0;
             int right = 0;
             int t     = 0;
 
             for(int j = 0; j < 25; j ++) {
                 for(int i = 0; i < ITERATIONS * 2; i += 4) {
                     left  = D_ENCRYPT(left,  right, i,     Eswap0, Eswap1, schedule);
                     right = D_ENCRYPT(right, left,  i + 2, Eswap0, Eswap1, schedule);
                 }
                 t     = left;
                 left  = right;
                 right = t;
             }
 
             t = right;
 
             right = (left >>> 1) | (left << 31);
             left  = (t    >>> 1) | (t    << 31);
 
             left  &= 0xffffffff;
             right &= 0xffffffff;
 
             int results[] = new int[2];
 
             PERM_OP(right, left, 1, 0x55555555, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 8, 0x00ff00ff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 2, 0x33333333, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 16, 0x0000ffff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 4, 0x0f0f0f0f, results);
             right = results[0]; left = results[1];
 
             int out[] = new int[2];
 
             out[0] = left; out[1] = right;
 
             return(out);
         }
 
         public static final String crypt(String salt, String original) {
             while(salt.length() < 2)
                 salt += getSaltChar();
 
             StringBuffer buffer = new StringBuffer("             ");
 
             char charZero = salt.charAt(0);
             char charOne  = salt.charAt(1);
 
             buffer.setCharAt(0, charZero);
             buffer.setCharAt(1, charOne);
 
             int Eswap0 = con_salt[(int)charZero];
             int Eswap1 = con_salt[(int)charOne] << 4;
 
             byte key[] = new byte[8];
 
             for(int i = 0; i < key.length; i ++) {
                 key[i] = (byte)0;
             }
 
             for(int i = 0; i < key.length && i < original.length(); i ++) {
                 int iChar = (int)original.charAt(i);
 
                 key[i] = (byte)(iChar << 1);
             }
 
             int schedule[] = des_set_key(key);
             int out[]      = body(schedule, Eswap0, Eswap1);
 
             byte b[] = new byte[9];
 
             intToFourBytes(out[0], b, 0);
             intToFourBytes(out[1], b, 4);
             b[8] = 0;
 
             for(int i = 2, y = 0, u = 0x80; i < 13; i ++) {
                 for(int j = 0, c = 0; j < 6; j ++) {
                     c <<= 1;
 
                     if(((int)b[y] & u) != 0)
                         c |= 1;
 
                     u >>>= 1;
 
                     if(u == 0) {
                         y++;
                         u = 0x80;
                     }
                     buffer.setCharAt(i, (char)cov_2char[c]);
                 }
             }
             return(buffer.toString());
         }
 
         private static String getSaltChar() {
             return JavaCrypt.getSaltChar(1);
         }
 
         private static String getSaltChar(int amount) {
             StringBuffer sb = new StringBuffer();
             for(int i=amount;i>0;i--) {
                 sb.append(theBaseSalts[(Math.abs(r_gen.nextInt())%64)]);
             }
             return sb.toString();
         }
 
         public static boolean check(String theClear,String theCrypt) {
             String theTest = JavaCrypt.crypt(theCrypt.substring(0,2),theClear);
             return theTest.equals(theCrypt);
         }
 
         public static String crypt(String theClear) {
             return JavaCrypt.crypt(getSaltChar(2),theClear);
         }
     }
 
     /* RubyString aka rb_string_value */
     public static RubyString stringValue(IRubyObject object) {
         return (RubyString) (object instanceof RubyString ? object :
             object.convertToString());
     }
 
     /** rb_str_sub
      *
      */
     public IRubyObject sub(IRubyObject[] args, Block block) {
         RubyString str = strDup();
         str.sub_bang(args, block);
         return str;
     }
 
     /** rb_str_sub_bang
      *
      */
     public IRubyObject sub_bang(IRubyObject[] args, Block block) {
         return sub(args, true, block);
     }
 
     private IRubyObject sub(IRubyObject[] args, boolean bang, Block block) {
         IRubyObject repl = getRuntime().getNil();
         boolean iter = false;
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 1 && block.isGiven()) {
             iter = true;
         } else if (args.length == 2) {
             repl = args[1];
             if (!repl.isKindOf(getRuntime().getString())) {
                 repl = repl.convertToString();
             }
         } else {
             throw getRuntime().newArgumentError("wrong number of arguments");
         }
         RubyRegexp pat = RubyRegexp.regexpValue(args[0]);
 
         String intern = toString();
 
         if (pat.search(intern, this, 0) >= 0) {
             RubyMatchData match = (RubyMatchData) tc.getBackref();
             RubyString newStr = match.pre_match();
             newStr.append(iter ? block.yield(tc, match.group(0)) : pat.regsub(repl, match));
             newStr.append(match.post_match());
             if (bang) {
                 view(newStr.value);
                 infectBy(repl);
                 return this;
             }
 
             newStr.setTaint(isTaint() || repl.isTaint());
             
             return newStr;
         }
 
         return bang ? getRuntime().getNil() : this;
     }
 
     /** rb_str_gsub
      *
      */
     public IRubyObject gsub(IRubyObject[] args, Block block) {
         return gsub(args, false, block);
     }
 
     /** rb_str_gsub_bang
      *
      */
     public IRubyObject gsub_bang(IRubyObject[] args, Block block) {
         return gsub(args, true, block);
     }
 
     private IRubyObject gsub(IRubyObject[] args, boolean bang, Block block) {
         // TODO: improve implementation. this is _really_ slow
         IRubyObject repl = getRuntime().getNil();
         RubyMatchData match;
         boolean iter = false;
         if (args.length == 1 && block.isGiven()) {
             iter = true;
         } else if (args.length == 2) {
             repl = args[1];
         } else {
             throw getRuntime().newArgumentError("wrong number of arguments");
         }
         boolean taint = repl.isTaint();
         RubyRegexp pat = null;
          if (args[0] instanceof RubyRegexp) {
             pat = (RubyRegexp)args[0];
         } else if (args[0].isKindOf(getRuntime().getString())) {
             pat = RubyRegexp.regexpValue(args[0]);
         } else {
             // FIXME: This should result in an error about not converting to regexp, no?
             pat = RubyRegexp.regexpValue(args[0].convertToString());
         }
 
         String str = toString();
         int beg = pat.search(str, this, 0);
         if (beg < 0) {
             return bang ? getRuntime().getNil() : strDup();
         }
         ByteList sbuf = new ByteList(this.value.length());
         IRubyObject newStr;
         int offset = 0;
 
         // Fix for JRUBY-97: Temporary fix pending
         // decision on UTF8-based string implementation.
         ThreadContext tc = getRuntime().getCurrentContext();
         if(iter) {
             while (beg >= 0) {
                 match = (RubyMatchData) tc.getBackref();
                 sbuf.append(this.value,offset,beg-offset);
                 newStr = block.yield(tc, match.group(0));
                 taint |= newStr.isTaint();
                 sbuf.append(newStr.asString().getByteList());
                 offset = match.matchEndPosition();
                 beg = pat.search(str, this, offset == beg ? beg + 1 : offset);
             }
         } else {
             RubyString r = stringValue(repl);
             while (beg >= 0) {
                 match = (RubyMatchData) tc.getBackref();
                 sbuf.append(this.value,offset,beg-offset);
                 pat.regsub(r, match, sbuf);
                 offset = match.matchEndPosition();
                 beg = pat.search(str, this, offset == beg ? beg + 1 : offset);
             }
         }
 
         sbuf.append(this.value,offset,this.value.length()-offset);
 
         if (bang) {
             view(sbuf);
             setTaint(isTaint() || taint);            
             return this;
         }
         RubyString result = new RubyString(getRuntime(), getMetaClass(), sbuf); 
         result.setTaint(isTaint() || taint);
         return result;
     }
 
     /** rb_str_index_m
      *
      */
     public IRubyObject index(IRubyObject[] args) {
         return index(args, false);
     }
 
     /** rb_str_rindex_m
      *
      */
     public IRubyObject rindex(IRubyObject[] args) {
         return index(args, true);
     }
 
     /**
      *	@fixme may be a problem with pos when doing reverse searches
      */
     private IRubyObject index(IRubyObject[] args, boolean reverse) {
         //FIXME may be a problem with pos when doing reverse searches
         int pos = !reverse ? 0 : value.length();
 
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             pos = RubyNumeric.fix2int(args[1]);
         }
         if (pos < 0) {
             pos += value.length();
             if (pos < 0) return getRuntime().getNil();
         }
         if (args[0] instanceof RubyRegexp) {
             int doNotLookPastIfReverse = pos;
 
             // RubyRegexp doesn't (yet?) support reverse searches, so we
             // find all matches and use the last one--very inefficient.
             // XXX - find a better way
             pos = ((RubyRegexp) args[0]).search(toString(), this, reverse ? 0 : pos);
 
             int dummy = pos;
             while (reverse && dummy > -1 && dummy <= doNotLookPastIfReverse) {
                 pos = dummy;
                 dummy = ((RubyRegexp) args[0]).search(toString(), this, pos + 1);
             }
         } else if (args[0] instanceof RubyString) {
             ByteList sub = ((RubyString) args[0]).value;
             // the empty string is always found at the beginning of a string
             if (sub.realSize == 0) return getRuntime().newFixnum(0);
 
             // FIXME: any compelling reason to clone here? we don't
             // for fixnum search below...
             ByteList sb = value.dup();
             pos = reverse ? sb.lastIndexOf(sub, pos) : sb.indexOf(sub, pos);
         } else if (args[0] instanceof RubyFixnum) {
             char c = (char) ((RubyFixnum) args[0]).getLongValue();
             pos = reverse ? value.lastIndexOf(c, pos) : value.indexOf(c, pos);
         } else {
             throw getRuntime().newArgumentError("wrong type of argument");
         }
 
         return pos == -1 ? getRuntime().getNil() : getRuntime().newFixnum(pos);
     }
 
     /* rb_str_substr */
     public IRubyObject substr(int beg, int len) {
         int length = value.length();
         if (len < 0 || beg > length) return getRuntime().getNil();
 
         if (beg < 0) {
             beg += length;
             if (beg < 0) return getRuntime().getNil();
         }
         
         int end = Math.min(length, beg + len);
         return makeShared(beg, end - beg);
     }
 
     /* rb_str_replace */
     public IRubyObject replace(int beg, int len, RubyString replaceWith) {
         if (beg + len >= value.length()) len = value.length() - beg;
 
         modify();
         value.unsafeReplace(beg,len,replaceWith.value);
 
         return infectBy(replaceWith);
     }
 
     /** rb_str_aref, rb_str_aref_m
      *
      */
     public IRubyObject aref(IRubyObject[] args) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             if (args[0] instanceof RubyRegexp) {
                 IRubyObject match = RubyRegexp.regexpValue(args[0]).match(toString(), this, 0);
                 long idx = args[1].convertToInteger().getLongValue();
                 getRuntime().getCurrentContext().setBackref(match);
                 return RubyRegexp.nth_match((int) idx, match);
             }
             return substr(RubyNumeric.fix2int(args[0]), RubyNumeric.fix2int(args[1]));
         }
 
         if (args[0] instanceof RubyRegexp) {
             return RubyRegexp.regexpValue(args[0]).search(toString(), this, 0) >= 0 ?
                 RubyRegexp.last_match(getRuntime().getCurrentContext().getBackref()) :
                 getRuntime().getNil();
         } else if (args[0] instanceof RubyString) {
             return toString().indexOf(stringValue(args[0]).toString()) != -1 ?
                 args[0] : getRuntime().getNil();
         } else if (args[0] instanceof RubyRange) {
             long[] begLen = ((RubyRange) args[0]).getBeginLength(value.length(), true, false);
             return begLen == null ? getRuntime().getNil() :
                 substr((int) begLen[0], (int) begLen[1]);
         }
         int idx = (int) args[0].convertToInteger().getLongValue();
         
         if (idx < 0) idx += value.length();
         if (idx < 0 || idx >= value.length()) return getRuntime().getNil();
 
         return getRuntime().newFixnum(value.get(idx) & 0xFF);
     }
 
     /**
      * rb_str_subpat_set
      *
      */
     private void subpatSet(RubyRegexp regexp, int nth, IRubyObject repl) {
         int found = regexp.search(this.toString(), this, 0);
         if (found == -1) throw getRuntime().newIndexError("regexp not matched");
 
         RubyMatchData match = (RubyMatchData) getRuntime().getCurrentContext().getBackref();
 
         if (nth >= match.getSize()) {
             throw getRuntime().newIndexError("index " + nth + " out of regexp");
         }
         if (nth < 0) {
             if (-nth >= match.getSize()) {
                 throw getRuntime().newIndexError("index " + nth + " out of regexp");
             }
             nth += match.getSize();
         }
 
         IRubyObject group = match.group(nth);
         if (getRuntime().getNil().equals(group)) {
             throw getRuntime().newIndexError("regexp group " + nth + " not matched");
         }
 
         int beg = (int) match.begin(nth);
         int len = (int) (match.end(nth) - beg);
 
         replace(beg, len, stringValue(repl));
     }
 
     /** rb_str_aset, rb_str_aset_m
      *
      */
     public IRubyObject aset(IRubyObject[] args) {
         int strLen = value.length();
         if (Arity.checkArgumentCount(getRuntime(), args, 2, 3) == 3) {
             if (args[0] instanceof RubyFixnum) {
                 RubyString repl = stringValue(args[2]);
                 int beg = RubyNumeric.fix2int(args[0]);
                 int len = RubyNumeric.fix2int(args[1]);
                 if (len < 0) throw getRuntime().newIndexError("negative length");
                 if (beg < 0) beg += strLen;
 
                 if (beg < 0 || (beg > 0 && beg >= strLen)) {
                     throw getRuntime().newIndexError("string index out of bounds");
                 }
                 if (beg + len > strLen) len = strLen - beg;
 
                 replace(beg, len, repl);
                 return repl;
             }
             if (args[0] instanceof RubyRegexp) {
                 RubyString repl = stringValue(args[2]);
                 int nth = RubyNumeric.fix2int(args[1]);
                 subpatSet((RubyRegexp) args[0], nth, repl);
                 return repl;
             }
         }
         if (args[0] instanceof RubyFixnum || args[0].respondsTo("to_int")) { // FIXME: RubyNumeric or RubyInteger instead?
             int idx = 0;
 
             // FIXME: second instanceof check adds overhead?
             if (!(args[0] instanceof RubyFixnum)) {
                 // FIXME: ok to cast?
                 idx = (int)args[0].convertToInteger().getLongValue();
             } else {
                 idx = RubyNumeric.fix2int(args[0]); // num2int?
             }
             
             if (idx < 0) idx += value.length();
 
             if (idx < 0 || idx >= value.length()) {
                 throw getRuntime().newIndexError("string index out of bounds");
             }
             if (args[1] instanceof RubyFixnum) {
                 modify();
                 value.set(idx, (byte) RubyNumeric.fix2int(args[1]));
             } else {
                 replace(idx, 1, stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRegexp) {
             sub_bang(args, null);
             return args[1];
         }
         if (args[0] instanceof RubyString) {
             RubyString orig = stringValue(args[0]);
             int beg = toString().indexOf(orig.toString());
             if (beg != -1) {
                 replace(beg, orig.value.length(), stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRange) {
             long[] idxs = ((RubyRange) args[0]).getBeginLength(value.length(), true, true);
             replace((int) idxs[0], (int) idxs[1], stringValue(args[1]));
             return args[1];
         }
         throw getRuntime().newTypeError("wrong argument type");
     }
 
     /** rb_str_slice_bang
      *
      */
     public IRubyObject slice_bang(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         IRubyObject[] newArgs = new IRubyObject[argc + 1];
         newArgs[0] = args[0];
         if (argc > 1) newArgs[1] = args[1];
 
         newArgs[argc] = newString("");
         IRubyObject result = aref(args);
         if (result.isNil()) return result;
 
         aset(newArgs);
         return result;
     }
 
     public IRubyObject succ() {
         return strDup().succ_bang();
     }
 
     public IRubyObject succ_bang() {
         if (value.length() == 0) return this;
 
         modify();
         
         boolean alnumSeen = false;
         int pos = -1;
         int c = 0;
         int n = 0;
         for (int i = value.length() - 1; i >= 0; i--) {
             c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 alnumSeen = true;
                 if ((isDigit(c) && c < '9') || (isLower(c) && c < 'z') || (isUpper(c) && c < 'Z')) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = isDigit(c) ? '1' : (isLower(c) ? 'a' : 'A');
                 value.set(i, (byte)(isDigit(c) ? '0' : (isLower(c) ? 'a' : 'A')));
             }
         }
         if (!alnumSeen) {
             for (int i = value.length() - 1; i >= 0; i--) {
                 c = value.get(i) & 0xFF;
                 if (c < 0xff) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = '\u0001';
                 value.set(i, 0);
             }
         }
         if (pos > -1) {
             // This represents left most digit in a set of incremented
             // values?  Therefore leftmost numeric must be '1' and not '0'
             // 999 -> 1000, not 999 -> 0000.  whereas chars should be
             // zzz -> aaaa and non-alnum byte values should be "\377" -> "\001\000"
             value.prepend((byte) n);
         }
         return this;
     }
 
     /** rb_str_upto_m
      *
      */
     public IRubyObject upto(IRubyObject str, Block block) {
         return upto(str, false, block);
     }
 
     /* rb_str_upto */
     public IRubyObject upto(IRubyObject str, boolean excl, Block block) {
         // alias 'this' to 'beg' for ease of comparison with MRI
         RubyString beg = this;
         RubyString end = stringValue(str);
 
         int n = beg.cmp(end);
         if (n > 0 || (excl && n == 0)) return beg;
 
         RubyString afterEnd = stringValue(end.succ());
         RubyString current = beg;
 
         ThreadContext context = getRuntime().getCurrentContext();
         while (!current.equals(afterEnd)) {
             block.yield(context, current);
             if (!excl && current.equals(end)) break;
 
             current = (RubyString) current.succ();
             
             if (excl && current.equals(end)) break;
 
             if (current.length().getLongValue() > end.length().getLongValue()) break;
         }
 
         return beg;
 
     }
 
 
     /** rb_str_include
      *
      */
     public RubyBoolean include(IRubyObject obj) {
         if (obj instanceof RubyFixnum) {
             int c = RubyNumeric.fix2int(obj);
             for (int i = 0; i < value.length(); i++) {
                 if (value.get(i) == (byte)c) {
                     return getRuntime().getTrue();
                 }
             }
             return getRuntime().getFalse();
         }
         ByteList str = stringValue(obj).value;
         return getRuntime().newBoolean(value.indexOf(str) != -1);
     }
 
     /** rb_str_to_i
      *
      */
     public IRubyObject to_i(IRubyObject[] args) {
         long base = Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 0 ? 10 : args[0].convertToInteger().getLongValue();
         return RubyNumeric.str2inum(getRuntime(), this, (int) base);
     }
 
     /** rb_str_oct
      *
      */
     public IRubyObject oct() {
         if (isEmpty()) {
             return getRuntime().newFixnum(0);
         }
 
         int base = 8;
         String str = toString().trim();
         int pos = (str.charAt(0) == '-' || str.charAt(0) == '+') ? 1 : 0;
         if (str.indexOf("0x") == pos || str.indexOf("0X") == pos) {
             base = 16;
         } else if (str.indexOf("0b") == pos || str.indexOf("0B") == pos) {
             base = 2;
         }
         return RubyNumeric.str2inum(getRuntime(), this, base);
     }
 
     /** rb_str_hex
      *
      */
     public IRubyObject hex() {
         return RubyNumeric.str2inum(getRuntime(), this, 16);
     }
 
     /** rb_str_to_f
      *
      */
     public IRubyObject to_f() {
         return RubyNumeric.str2fnum(getRuntime(), this);
     }
 
     /** rb_str_split
      *
      */
     public RubyArray split(IRubyObject[] args) {
         RubyRegexp pattern;
         Ruby runtime = getRuntime();
         boolean isWhitespace = false;
 
         // get the pattern based on args
         if (args.length == 0 || args[0].isNil()) {
             isWhitespace = true;
             IRubyObject defaultPattern = runtime.getGlobalVariables().get("$;");
             
             if (defaultPattern.isNil()) {
                 pattern = RubyRegexp.newRegexp(runtime, "\\s+", 0, null);
             } else {
                 // FIXME: Is toString correct here?
                 pattern = RubyRegexp.newRegexp(runtime, defaultPattern.toString(), 0, null);
             }
         } else if (args[0] instanceof RubyRegexp) {
             // Even if we have whitespace-only explicit regexp we do not
             // mark it as whitespace.  Apparently, this is so ruby can
             // still get the do not ignore the front match behavior.
             pattern = RubyRegexp.regexpValue(args[0]);
         } else {
             String stringPattern = RubyString.stringValue(args[0]).toString();
             
             if (stringPattern.equals(" ")) {
                 isWhitespace = true;
                 pattern = RubyRegexp.newRegexp(runtime, "\\s+", 0, null);
             } else {
                 pattern = RubyRegexp.newRegexp(runtime, RubyRegexp.escapeSpecialChars(stringPattern), 0, null);
             }
         }
 
         int limit = getLimit(args);
         String[] result = null;
         // attempt to convert to Unicode when appropriate
         String splitee = toString();
 
         boolean unicodeSuccess = false;
         if (runtime.getKCode() == KCode.UTF8) {
             // We're in UTF8 mode; try to convert the string to UTF8, but fall back on raw bytes if we can't decode
             // TODO: all this decoder and charset stuff could be centralized...in KCode perhaps?
             CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
             decoder.onMalformedInput(CodingErrorAction.REPORT);
             decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
 
             try {
                 splitee = decoder.decode(ByteBuffer.wrap(value.unsafeBytes(), value.begin, value.realSize)).toString();
                 unicodeSuccess = true;
             } catch (CharacterCodingException cce) {
                 // ignore, just use the unencoded string
             }
         }
 
 
         if (limit == 1) {
             if (splitee.length() == 0) {
                 return runtime.newArray();
             } else {
                 return runtime.newArray(this);
             }
         } else {
             List list = new ArrayList();
             int numberOfHits = 0;
             int stringLength = splitee.length();
 
             Pattern pat = pattern.getPattern();
             Matcher matt = pat.matcher(splitee);
 
             int startOfCurrentHit = 0;
             int endOfCurrentHit = 0;
             String group = null;
 
             // TODO: There's a fast path in here somewhere that could just use Pattern.split
 
             if (matt.find()) {
                 // we have matches, proceed
 
                 // end of current hit is start of first match
                 endOfCurrentHit = matt.start();
 
                 // filter out starting whitespace matches for non-regex whitespace splits
                 if (endOfCurrentHit != 0 || !isWhitespace) {
                     // not a non-regex whitespace split, proceed
 
                     numberOfHits++;
 
                     // skip first positive lookahead match
                     if (matt.end() != 0) {
 
                         // add the first hit
                         list.add(splitee.substring(startOfCurrentHit, endOfCurrentHit));
 
                         // add any matched groups found while splitting the first hit
                         for (int groupIndex = 1; groupIndex < matt.groupCount(); groupIndex++) {
                             group = matt.group(groupIndex);
                             if (group == null) continue;
                             
                             list.add(group);
                         }
                     }
                 }
 
                 // advance start to the end of the current hit
                 startOfCurrentHit = matt.end();
 
                 // ensure we haven't exceeded the hit limit
                 if (numberOfHits + 1 != limit) {
                     // loop over the remaining matches
                     while (matt.find()) {
                         // end of current hit is start of the next match
                         endOfCurrentHit = matt.start();
                         numberOfHits++;
 
                         // add the current hit
                         list.add(splitee.substring(startOfCurrentHit, endOfCurrentHit));
 
                         // add any matched groups found while splitting the current hit
                         for (int groupIndex = 1; groupIndex < matt.groupCount(); groupIndex++) {
                             group = matt.group(groupIndex);
                             if (group == null) continue;
                             
                             list.add(group);
                         }
 
                         // advance start to the end of the current hit
                         startOfCurrentHit = matt.end();
                     }
                 }
             }
 
             if (numberOfHits == 0) {
                 // we found no hits, use the entire string
                 list.add(splitee);
             } else if (startOfCurrentHit <= stringLength) {
                 // our last match ended before the end of the string, add remainder
                 list.add(splitee.substring(startOfCurrentHit, stringLength));
             }
 
             // Remove trailing whitespace when limit 0 is specified
             if (limit == 0 && list.size() > 0) {
                 for (int size = list.size() - 1;
                         size >= 0 && ((String) list.get(size)).length() == 0;
                         size--) {
                     list.remove(size);
                 }
             }
 
             result = (String[])list.toArray(new String[list.size()]);
         }
 
         // convert arraylist of strings to RubyArray of RubyStrings
         RubyArray resultArray = runtime.newArray(result.length);
 
         for (int i = 0; i < result.length; i++) {
             
             RubyString string = new RubyString(runtime, getMetaClass(), result[i]);
 
             // if we're in unicode mode and successfully converted to a unicode string before,
             // make sure to keep unicode in the split values
             if (unicodeSuccess && runtime.getKCode() == KCode.UTF8) {
                 string.setUnicodeValue(result[i]);
             }
 
             resultArray.append(string);
         }
 
         return resultArray;
     }
 
     private static int getLimit(IRubyObject[] args) {
         return args.length == 2 ? RubyNumeric.fix2int(args[1]) : 0;
     }
 
     /** rb_str_scan
      *
      */
     public IRubyObject scan(IRubyObject arg, Block block) {
         RubyRegexp pattern = RubyRegexp.regexpValue(arg);
         int start = 0;
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Fix for JRUBY-97: Temporary fix pending
         // decision on UTF8-based string implementation.
         // Move toString() call outside loop.
         String toString = toString();
 
         if (!block.isGiven()) {
             RubyArray ary = getRuntime().newArray();
             while (pattern.search(toString, this, start) != -1) {
                 RubyMatchData md = (RubyMatchData) tc.getBackref();
 
                 ary.append(md.getSize() == 1 ? md.group(0) : md.subseq(1, md.getSize()));
 
                 if (md.matchEndPosition() == md.matchStartPosition()) {
                     start++;
                 } else {
                     start = md.matchEndPosition();
                 }
 
             }
             return ary;
         }
 
         while (pattern.search(toString, this, start) != -1) {
             RubyMatchData md = (RubyMatchData) tc.getBackref();
 
             block.yield(tc, md.getSize() == 1 ? md.group(0) : md.subseq(1, md.getSize()));
 
             if (md.matchEndPosition() == md.matchStartPosition()) {
                 start++;
             } else {
                 start = md.matchEndPosition();
             }
 
         }
         return this;
     }
     
     private static ByteList SPACE_BYTELIST = new ByteList(ByteList.plain(" "));
 
     private IRubyObject justify(IRubyObject [] args, boolean leftJustify) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
         ByteList paddingArg;
 
         if (args.length == 2) {
             paddingArg = args[1].convertToString().value;
             if (paddingArg.length() == 0) {
                 throw getRuntime().newArgumentError("zero width padding");
             }
         } else {
             paddingArg = SPACE_BYTELIST;
         }
         
         int length = RubyNumeric.fix2int(args[0]);
         if (length <= value.length()) {
             return strDup();
         }
 
         ByteList sbuf = new ByteList(length);
         ByteList thisStr = value;
 
         if (leftJustify) {
             sbuf.append(thisStr);
         }
 
         // Add n whole paddings
         int whole = (length - thisStr.length()) / paddingArg.length();
         for (int w = 0; w < whole; w++ ) {
             sbuf.append(paddingArg);
         }
 
         // Add fractional amount of padding to make up difference
         int fractionalLength = (length - thisStr.length()) % paddingArg.length();
         if (fractionalLength > 0) {
             sbuf.append((ByteList)paddingArg.subSequence(0, fractionalLength));
         }
 
         if (!leftJustify) {
             sbuf.append(thisStr);
         }
 
         RubyString ret = new RubyString(getRuntime(), getMetaClass(), sbuf);
 
         ret.infectBy(this);
         if (args.length == 2) ret.infectBy(args[1]);
 
         return ret;
     }
 
     /** rb_str_ljust
      *
      */
     public IRubyObject ljust(IRubyObject [] args) {
         return justify(args, true);
     }
 
     /** rb_str_rjust
      *
      */
     public IRubyObject rjust(IRubyObject [] args) {
         return justify(args, false);
     }
 
     public IRubyObject center(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         int len = RubyNumeric.fix2int(args[0]);
         ByteList pad = args.length == 2 ? args[1].convertToString().value : SPACE_BYTELIST;
         int strLen = value.length();
         int padLen = pad.length();
 
         if (padLen == 0) {
             throw getRuntime().newArgumentError("zero width padding");
         }
         if (len <= strLen) {
             return strDup();
         }
         ByteList sbuf = new ByteList(len);
         int lead = (len - strLen) / 2;
         for (int i = 0; i < lead; i++) {
             sbuf.append(pad.charAt(i % padLen));
         }
         sbuf.append(value);
         int remaining = len - (lead + strLen);
         for (int i = 0; i < remaining; i++) {
             sbuf.append(pad.charAt(i % padLen));
         }
         return new RubyString(getRuntime(), getMetaClass(), sbuf);
     }
 
     public IRubyObject chop() {
         RubyString str = strDup();
         str.chop_bang();
         return str;
     }
 
     public IRubyObject chop_bang() {
         int end = value.length() - 1;
 
         if (end < 0) return getRuntime().getNil(); 
 
         if ((value.get(end) & 0xFF) == '\n') {
             if (end > 0 && (value.get(end-1) & 0xFF) == '\r') end--;
             }
 
         view(0, end);
         return this;
     }
 
     public RubyString chomp(IRubyObject[] args) {
         RubyString str = strDup();
         str.chomp_bang(args);
         return str;
     }
 
     /**
      * rb_str_chomp_bang
      *
      * In the common case, removes CR and LF characters in various ways depending on the value of
      *   the optional args[0].
      * If args.length==0 removes one instance of CR, CRLF or LF from the end of the string.
      * If args.length>0 and args[0] is "\n" then same behaviour as args.length==0 .
      * If args.length>0 and args[0] is "" then removes trailing multiple LF or CRLF (but no CRs at
      *   all(!)).
      * @param args See method description.
      */
     public IRubyObject chomp_bang(IRubyObject[] args) {
         if (isEmpty()) {
             return getRuntime().getNil();
         }
 
         // Separator (a.k.a. $/) can be overriden by the -0 (zero) command line option
         String separator = (args.length == 0) ?
             getRuntime().getGlobalVariables().get("$/").asSymbol() : args[0].asSymbol();
 
         if (separator.equals(DEFAULT_RS)) {
             int end = value.length() - 1;
             int removeCount = 0;
 
             if (end < 0) {
                 return getRuntime().getNil();
             }
 
             if ((value.get(end) & 0xFF) == '\n') {
                 removeCount++;
                 if (end > 0 && (value.get(end-1) & 0xFF) == '\r') {
                     removeCount++;
                 }
             } else if ((value.get(end) & 0xFF) == '\r') {
                 removeCount++;
             }
 
             if (removeCount == 0) {
                 return getRuntime().getNil();
             }
 
             view(0, end - removeCount + 1);
             return this;
         }
 
         if (separator.length() == 0) {
             int end = value.length() - 1;
             int removeCount = 0;
             while(end - removeCount >= 0 && (value.get(end - removeCount) & 0xFF) == '\n') {
                 removeCount++;
                 if (end - removeCount >= 0 && (value.get(end - removeCount) & 0xFF) == '\r') {
                     removeCount++;
                 }
             }
             if (removeCount == 0) {
                 return getRuntime().getNil();
             }
 
             view(0, end - removeCount + 1);
             return this;
         }
 
         // Uncommon case of str.chomp!("xxx")
         if (toString().endsWith(separator)) {
             view(0, value.length() - separator.length());
             return this;
         }
         return getRuntime().getNil();
     }
 
     /** rb_str_lstrip
      * 
      */
     public IRubyObject lstrip() {
         RubyString str = strDup();
         str.lstrip_bang();
         return str;
     }
 
     /** rb_str_lstrip_bang
      * FIXME support buffer shared
      */
     public IRubyObject lstrip_bang() {
         if (value.length() == 0) return getRuntime().getNil();
         
         int i=0;
         while (i < value.length() && Character.isWhitespace(value.charAt(i))) i++;
         
         if (i > 0) {
             view(i, value.length() - i);
             return this;
             }
         
             return getRuntime().getNil();
         }
 
     /** rb_str_rstrip
      *  
      */
     public IRubyObject rstrip() {
         RubyString str = strDup();
         str.rstrip_bang();
         return str;
     }
 
     /** rb_str_rstrip_bang
      * FIXME support buffer shared
      */ 
     public IRubyObject rstrip_bang() {
         if (value.length() == 0) return getRuntime().getNil();
         int i=value.length() - 1;
         
         while (i >= 0 && Character.isWhitespace(value.charAt(i))) i--;
         
         if (i < value.length() - 1) {
             view(0, i + 1);
             return this;
             }
 
             return getRuntime().getNil();
         }
 
     /** rb_str_strip
      *
      */
     public IRubyObject strip() {
         RubyString str = strDup();
         str.strip_bang();
         return str;
         }
 
     /** rb_str_strip_bang
      *  FIXME support buffer shared
      */
     public IRubyObject strip_bang() {
         if (value.length() == 0) return getRuntime().getNil();
         
         int left = 0;
         while (left < value.length() && Character.isWhitespace(value.charAt(left))) left++;
         
         int right = value.length() - 1;
         while (right > left && Character.isWhitespace(value.charAt(right))) right--;
         
         if (left == 0 && right == value.length() - 1) {
             return getRuntime().getNil();
         }
         
         if (left <= right) {
             view(left, right - left + 1);
         return this;
     }
         
         if (left > right) {
             view(new ByteList());
             return this;            
         }        
         
         return getRuntime().getNil();
         }
 
     private static ByteList expandTemplate(ByteList spec, boolean invertOK) {
         int len = spec.length();
         if (len <= 1) {
             return spec;
         }
         ByteList sbuf = new ByteList();
         int pos = (invertOK && spec.charAt(0) == '^') ? 1 : 0;
         while (pos < len) {
             char c1 = spec.charAt(pos), c2;
             if (pos + 2 < len && spec.charAt(pos + 1) == '-') {
                 if ((c2 = spec.charAt(pos + 2)) > c1) {
                     for (int i = c1; i <= c2; i++) {
                         sbuf.append((char) i);
                     }
                 }
                 pos += 3;
                 continue;
             }
             sbuf.append(c1);
             pos++;
         }
         return sbuf;
     }
 
     private ByteList setupTable(ByteList[] specs) {
         int[] table = new int[256];
         int numSets = 0;
         for (int i = 0; i < specs.length; i++) {
             ByteList template = expandTemplate(specs[i], true);
             boolean invert = specs[i].length() > 1 && specs[i].charAt(0) == '^';
             for (int j = 0; j < 256; j++) {
                 if (template.indexOf(j) != -1) {
                     table[j] += invert ? -1 : 1;
                 }
             }
             numSets += invert ? 0 : 1;
         }
         ByteList sbuf = new ByteList();
         for (int k = 0; k < 256; k++) {
             if (table[k] == numSets) {
                 sbuf.append((char) k);
             }
         }
         return sbuf;
     }
 
     /** rb_str_count
      *
      */
     public IRubyObject count(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 1, -1);
         ByteList[] specs = new ByteList[argc];
         for (int i = 0; i < argc; i++) {
             specs[i] = stringValue(args[i]).value;
         }
         ByteList table = setupTable(specs);
 
         int count = 0;
         for (int j = 0; j < value.length(); j++) {
             if (table.indexOf(value.get(j) & 0xFF) != -1) {
                 count++;
             }
         }
         return getRuntime().newFixnum(count);
     }
 
     private ByteList getDelete(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 1, -1);
         ByteList[] specs = new ByteList[argc];
         for (int i = 0; i < argc; i++) {
             specs[i] = stringValue(args[i]).value;
         }
         ByteList table = setupTable(specs);
 
         int strLen = value.length();
         ByteList sbuf = new ByteList(strLen);
         int c;
         for (int j = 0; j < strLen; j++) {
             c = value.get(j) & 0xFF;
             if (table.indexOf(c) == -1) {
                 sbuf.append((char)c);
             }
         }
         return sbuf;
     }
 
     /** rb_str_delete
      *
      */
     public IRubyObject delete(IRubyObject[] args) {
         RubyString str = strDup();
         str.delete_bang(args);
         return str;
     }
 
     /** rb_str_delete_bang
      *
      */
     public IRubyObject delete_bang(IRubyObject[] args) {
         ByteList newStr = getDelete(args);
-        if (value.equals(newStr)) {
+        if (value.equal(newStr)) {
             return getRuntime().getNil();
         }
         view(newStr);
         return this;
     }
 
     private ByteList getSqueeze(IRubyObject[] args) {
         int argc = args.length;
         ByteList[] specs = null;
         if (argc > 0) {
             specs = new ByteList[argc];
             for (int i = 0; i < argc; i++) {
                 specs[i] = stringValue(args[i]).value;
             }
         }
         ByteList table = specs == null ? null : setupTable(specs);
 
         int strLen = value.length();
         if (strLen <= 1) {
             return value;
         }
         ByteList sbuf = new ByteList(strLen);
         int c1 = value.get(0) & 0xFF;
         sbuf.append((char)c1);
         int c2;
         for (int j = 1; j < strLen; j++) {
             c2 = value.get(j) & 0xFF;
             if (c2 == c1 && (table == null || table.indexOf(c2) != -1)) {
                 continue;
             }
             sbuf.append((char)c2);
             c1 = c2;
         }
         return sbuf;
     }
 
     /** rb_str_squeeze
      *
      */
     public IRubyObject squeeze(IRubyObject[] args) {
         RubyString str = strDup();
         str.squeeze_bang(args);        
         return str;        
     }
 
     /** rb_str_squeeze_bang
      *
      */
     public IRubyObject squeeze_bang(IRubyObject[] args) {
         ByteList newStr = getSqueeze(args);
-        if (value.equals(newStr)) {
+        if (value.equal(newStr)) {
             return getRuntime().getNil();
         }
         view(newStr);
         return this;
     }
 
     private ByteList tr(IRubyObject search, IRubyObject replace, boolean squeeze) {
         ByteList srchSpec = search.convertToString().value;
         ByteList srch = expandTemplate(srchSpec, true);
         if (srchSpec.charAt(0) == '^') {
             ByteList sbuf = new ByteList(256);
             for (int i = 0; i < 256; i++) {
                 char c = (char) i;
                 if (srch.indexOf(c) == -1) {
                     sbuf.append(c);
                 }
             }
             srch = sbuf;
         }
         ByteList repl = expandTemplate(replace.convertToString().value, false);
 
         int strLen = value.length();
         if (strLen == 0 || srch.length() == 0) {
             return value;
         }
         int repLen = repl.length();
         ByteList sbuf = new ByteList(strLen);
         int last = -1;
         for (int i = 0; i < strLen; i++) {
             int cs = value.get(i) & 0xFF;
             int pos = srch.indexOf(cs);
             if (pos == -1) {
                 sbuf.append((char)cs);
                 last = -1;
             } else if (repLen > 0) {
                 char cr = repl.charAt(Math.min(pos, repLen - 1));
                 if (squeeze && cr == last) {
                     continue;
                 }
                 sbuf.append((char)cr);
                 last = cr;
             }
         }
         return sbuf;
     }
 
     /** rb_str_tr
      *
      */
     public IRubyObject tr(IRubyObject search, IRubyObject replace) {
         RubyString str = strDup();
         str.tr_bang(search, replace);        
         return str;        
     }
 
     /** rb_str_tr_bang
      *
      */
     public IRubyObject tr_bang(IRubyObject search, IRubyObject replace) {
         ByteList newStr = tr(search, replace, false);
-        if (value.equals(newStr)) {
+        if (value.equal(newStr)) {
             return getRuntime().getNil();
         }
         view(newStr);
         return this;
     }
 
     /** rb_str_tr_s
      *
      */
     public IRubyObject tr_s(IRubyObject search, IRubyObject replace) {
         return newString(getRuntime(), tr(search, replace, true)).infectBy(this);
     }
 
     /** rb_str_tr_s_bang
      *
      */
     public IRubyObject tr_s_bang(IRubyObject search, IRubyObject replace) {
         ByteList newStr = tr(search, replace, true);
-        if (value.equals(newStr)) {
+        if (value.equal(newStr)) {
             return getRuntime().getNil();
         }
         view(newStr);
         return this;
     }
 
     /** rb_str_each_line
      *
      */
     public IRubyObject each_line(IRubyObject[] args, Block block) {
         int strLen = value.length();
         if (strLen == 0) {
             return this;
         }
         String sep;
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 1) {
             sep = RubyRegexp.escapeSpecialChars(stringValue(args[0]).toString());
         } else {
             sep = RubyRegexp.escapeSpecialChars(getRuntime().getGlobalVariables().get("$/").asSymbol());
         }
         if (sep == null) {
             sep = "(?:\\n|\\r\\n?)";
         } else if (sep.length() == 0) {
             sep = "(?:\\n|\\r\\n?){2,}";
         }
         RubyRegexp pat = RubyRegexp.newRegexp(getRuntime(), ".*?" + sep, RubyRegexp.RE_OPTION_MULTILINE, null);
         int start = 0;
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Fix for JRUBY-97: Temporary fix pending
         // decision on UTF8-based string implementation.
         // Move toString() call outside loop.
         String toString = toString();
 
         if (pat.search(toString, this, start) != -1) {
             RubyMatchData md = (RubyMatchData) tc.getBackref();
             
             block.yield(tc, md.group(0));
             start = md.end(0);
             while (md.find()) {
                 block.yield(tc, md.group(0));
                 start = md.end(0);
             }
         }
         if (start < strLen) {
             block.yield(tc, substr(start, strLen - start));
         }
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     public RubyString each_byte(Block block) {
         int lLength = value.length();
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < lLength; i++) {
             block.yield(context, runtime.newFixnum(value.get(i) & 0xFF));
         }
         return this;
     }
 
     /** rb_str_intern
      *
      */
     public RubySymbol intern() {
         String s = toString();
         if (s.equals("")) {
             throw getRuntime().newArgumentError("interning empty string");
         }
         if (s.indexOf('\0') >= 0) {
             throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return RubySymbol.newSymbol(getRuntime(), toString());
     }
 
     public RubySymbol to_sym() {
         return intern();
     }
 
     public RubyInteger sum(IRubyObject[] args) {
         long bitSize = 16;
         if (args.length > 0) {
             bitSize = ((RubyInteger) args[0].convertToInteger()).getLongValue();
         }
 
         long result = 0;
         for (int i = 0; i < value.length(); i++) {
             result += value.get(i) & 0xFF;
         }
         return getRuntime().newFixnum(bitSize == 0 ? result : result % (long) Math.pow(2, bitSize));
     }
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      */
     public void setValue(CharSequence value) {
         view(ByteList.plain(value));
     }
 
     public void setValue(ByteList value) {
         view(value);
     }
 
     public CharSequence getValue() {
         return toString();
     }
 
     public String getUnicodeValue() {
         try {
             return new String(value.bytes,value.begin,value.realSize, "UTF8");
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
 
     public void setUnicodeValue(String newValue) {
         try {
             view(newValue.getBytes("UTF8"));
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 }
diff --git a/src/org/jruby/RubyStruct.java b/src/org/jruby/RubyStruct.java
index 0d975bdcbb..cb969fc54c 100644
--- a/src/org/jruby/RubyStruct.java
+++ b/src/org/jruby/RubyStruct.java
@@ -1,483 +1,499 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import java.util.List;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 
 /**
  * @author  jpetersen
  */
 public class RubyStruct extends RubyObject {
     private IRubyObject[] values;
 
     /**
      * Constructor for RubyStruct.
      * @param runtime
      * @param rubyClass
      */
     public RubyStruct(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
 
     public static RubyClass createStructClass(Ruby runtime) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here, but it's unclear how Structs
         // work with marshalling. Confirm behavior and ensure we're doing this correctly. JRUBY-415
         RubyClass structClass = runtime.defineClass("Struct", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         structClass.index = ClassIndex.STRUCT;
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyStruct.class);
         structClass.includeModule(runtime.getModule("Enumerable"));
 
         structClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
 
         structClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         structClass.defineMethod("clone", callbackFactory.getMethod("rbClone"));
 
         structClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
+        structClass.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
 
         structClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         structClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         structClass.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         structClass.defineFastMethod("values", callbackFactory.getFastMethod("to_a"));
         structClass.defineFastMethod("size", callbackFactory.getFastMethod("size"));
         structClass.defineFastMethod("length", callbackFactory.getFastMethod("size"));
 
         structClass.defineMethod("each", callbackFactory.getMethod("each"));
         structClass.defineMethod("each_pair", callbackFactory.getMethod("each_pair"));
         structClass.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         structClass.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
 
         structClass.defineFastMethod("members", callbackFactory.getFastMethod("members"));
 
         return structClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.STRUCT;
     }
 
     private static IRubyObject getInstanceVariable(RubyClass type, String name) {
         RubyClass structClass = type.getRuntime().getClass("Struct");
 
         while (type != null && type != structClass) {
             IRubyObject variable = type.getInstanceVariable(name);
             if (variable != null) {
                 return variable;
             }
 
             type = type.getSuperClass();
         }
 
         return type.getRuntime().getNil();
     }
 
     private RubyClass classOf() {
         return getMetaClass() instanceof MetaClass ? getMetaClass().getSuperClass() : getMetaClass();
     }
 
     private void modify() {
         testFrozen("Struct is frozen");
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify struct");
         }
     }
 
     private IRubyObject setByName(String name, IRubyObject value) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private IRubyObject getByName(String name) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     // Struct methods
 
     /** Create new Struct class.
      *
      * MRI: rb_struct_s_def / make_struct
      *
      */
     public static RubyClass newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = null;
         Ruby runtime = recv.getRuntime();
 
         if (args.length > 0 && args[0] instanceof RubyString) {
             name = args[0].toString();
         }
 
         RubyArray member = recv.getRuntime().newArray();
 
         for (int i = name == null ? 0 : 1; i < args.length; i++) {
             member.append(RubySymbol.newSymbol(recv.getRuntime(), args[i].asSymbol()));
         }
 
         RubyClass newStruct;
         RubyClass superClass = (RubyClass)recv;
 
         if (name == null) {
             newStruct = new RubyClass(superClass, superClass.getAllocator());
         } else {
             if (!IdUtil.isConstant(name)) {
                 throw runtime.newNameError("identifier " + name + " needs to be constant", name);
             }
 
             IRubyObject type = superClass.getConstantAt(name);
 
             if (type != null) {
                 runtime.getWarnings().warn(runtime.getCurrentContext().getFramePosition(), "redefining constant Struct::" + name);
             }
             newStruct = superClass.newSubClass(name, superClass.getAllocator(), superClass.getCRef());
         }
 
         newStruct.index = ClassIndex.STRUCT;
         
         newStruct.setInstanceVariable("__size__", member.length());
         newStruct.setInstanceVariable("__member__", member);
 
         CallbackFactory callbackFactory = recv.getRuntime().callbackFactory(RubyStruct.class);
         newStruct.getSingletonClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.getSingletonClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.getSingletonClass().defineMethod("members", callbackFactory.getSingletonMethod("members"));
 
         // define access methods.
         for (int i = name == null ? 0 : 1; i < args.length; i++) {
             String memberName = args[i].asSymbol();
             newStruct.defineMethod(memberName, callbackFactory.getMethod("get"));
             newStruct.defineMethod(memberName + "=", callbackFactory.getMethod("set", RubyKernel.IRUBY_OBJECT));
         }
         
         if (block.isGiven()) {
             block.yield(recv.getRuntime().getCurrentContext(), null, newStruct, newStruct, false);
         }
 
         return newStruct;
     }
 
     /** Create new Structure.
      *
      * MRI: struct_alloc
      *
      */
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         int size = RubyNumeric.fix2int(getInstanceVariable((RubyClass) recv, "__size__"));
 
         struct.values = new IRubyObject[size];
 
         struct.callInit(args, block);
 
         return struct;
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         modify();
 
         int size = RubyNumeric.fix2int(getInstanceVariable(getMetaClass(), "__size__"));
 
         if (args.length > size) {
             throw getRuntime().newArgumentError("struct size differs (" + args.length +" for " + size + ")");
         }
 
         for (int i = 0; i < args.length; i++) {
             values[i] = args[i];
         }
 
         for (int i = args.length; i < size; i++) {
             values[i] = getRuntime().getNil();
         }
 
         return getRuntime().getNil();
     }
     
     public static RubyArray members(IRubyObject recv, Block block) {
         RubyArray member = (RubyArray) getInstanceVariable((RubyClass) recv, "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         RubyArray result = recv.getRuntime().newArray(member.getLength());
         for (int i = 0,k=member.getLength(); i < k; i++) {
             result.append(recv.getRuntime().newString(member.eltInternal(i).asSymbol()));
         }
 
         return result;
     }
 
     public RubyArray members() {
         return members(classOf(), Block.NULL_BLOCK);
     }
 
     public IRubyObject set(IRubyObject value, Block block) {
         String name = getRuntime().getCurrentContext().getFrameName();
         if (name.endsWith("=")) {
             name = name.substring(0, name.length() - 1);
         }
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private RaiseException notStructMemberError(String name) {
         return getRuntime().newNameError(name + " is not struct member", name);
     }
 
     public IRubyObject get(Block block) {
         String name = getRuntime().getCurrentContext().getFrameName();
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     public IRubyObject rbClone(Block block) {
         RubyStruct clone = new RubyStruct(getRuntime(), getMetaClass());
 
         clone.values = new IRubyObject[values.length];
         System.arraycopy(values, 0, clone.values, 0, values.length);
 
         clone.setFrozen(this.isFrozen());
         clone.setTaint(this.isTaint());
 
         return clone;
     }
 
     public IRubyObject equal(IRubyObject other) {
-        if (this == other) {
-            return getRuntime().getTrue();
-        } else if (!(other instanceof RubyStruct)) {
-            return getRuntime().getFalse();
-        } else if (getMetaClass() != other.getMetaClass()) {
-            return getRuntime().getFalse();
-        } else {
+        if (this == other) return getRuntime().getTrue();
+        if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
+        if (getMetaClass() != other.getMetaClass()) return getRuntime().getFalse();
+        
+        Ruby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        RubyStruct otherStruct = (RubyStruct)other;
             for (int i = 0; i < values.length; i++) {
-                if (!values[i].equals(((RubyStruct) other).values[i])) {
-                    return getRuntime().getFalse();
+            if (!values[i].equalInternal(context, otherStruct.values[i]).isTrue()) {
+                return runtime.getFalse();
                 }
             }
-            return getRuntime().getTrue();
+        return runtime.getTrue();
+        }
+    
+    public IRubyObject eql_p(IRubyObject other) {
+        if (this == other) return getRuntime().getTrue();
+        if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
+        if (getMetaClass() != other.getMetaClass()) return getRuntime().getFalse();
+        
+        Ruby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        RubyStruct otherStruct = (RubyStruct)other;
+        for (int i = 0; i < values.length; i++) {
+            if (!values[i].eqlInternal(context, otherStruct.values[i])) {
+                return runtime.getFalse();
+    }
         }
+        return runtime.getTrue();        
     }
 
     public IRubyObject to_s() {
         return inspect();
     }
 
     public IRubyObject inspect() {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         StringBuffer sb = new StringBuffer(100);
 
         sb.append("#<struct ").append(getMetaClass().getName()).append(' ');
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (i > 0) {
                 sb.append(", ");
             }
 
             sb.append(member.eltInternal(i).asSymbol()).append("=");
             sb.append(values[i].callMethod(getRuntime().getCurrentContext(), "inspect"));
         }
 
         sb.append('>');
 
         return getRuntime().newString(sb.toString()); // OBJ_INFECT
     }
 
     public RubyArray to_a() {
         return getRuntime().newArray(values);
     }
 
     public RubyFixnum size() {
         return getRuntime().newFixnum(values.length);
     }
 
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < values.length; i++) {
             block.yield(context, values[i]);
         }
 
         return this;
     }
 
     public IRubyObject each_pair(Block block) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < values.length; i++) {
             block.yield(context, getRuntime().newArrayNoCopy(new IRubyObject[]{member.eltInternal(i), values[i]}));
         }
 
         return this;
     }
 
     public IRubyObject aref(IRubyObject key) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return getByName(key.asSymbol());
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         return values[idx];
     }
 
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return setByName(key.asSymbol(), value);
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         modify();
         return values[idx] = value;
     }
 
     public static void marshalTo(RubyStruct struct, MarshalStream output) throws java.io.IOException {
         output.dumpDefaultObjectHeader('S', struct.getMetaClass());
 
         List members = ((RubyArray) getInstanceVariable(struct.classOf(), "__member__")).getList();
         output.writeInt(members.size());
 
         for (int i = 0; i < members.size(); i++) {
             RubySymbol name = (RubySymbol) members.get(i);
             output.dumpObject(name);
             output.dumpObject(struct.values[i]);
         }
     }
 
     public static RubyStruct unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         Ruby runtime = input.getRuntime();
 
         RubySymbol className = (RubySymbol) input.unmarshalObject();
         RubyClass rbClass = pathToClass(runtime, className.asSymbol());
         if (rbClass == null) {
             throw runtime.newNameError("uninitialized constant " + className, className.asSymbol());
         }
 
         RubyArray mem = members(rbClass, Block.NULL_BLOCK);
 
         int len = input.unmarshalInt();
         IRubyObject[] values = new IRubyObject[len];
         for(int i = 0; i < len; i++) {
             values[i] = runtime.getNil();
         }
         RubyStruct result = newStruct(rbClass, values, Block.NULL_BLOCK);
         input.registerLinkTarget(result);
         for(int i = 0; i < len; i++) {
             IRubyObject slot = input.unmarshalObject();
             if(!mem.eltInternal(i).toString().equals(slot.toString())) {
                 throw runtime.newTypeError("struct " + rbClass.getName() + " not compatible (:" + slot + " for :" + mem.eltInternal(i) + ")");
             }
             result.aset(runtime.newFixnum(i), input.unmarshalObject());
         }
         return result;
     }
 
     private static RubyClass pathToClass(Ruby runtime, String path) {
         // FIXME: Throw the right ArgumentError's if the class is missing
         // or if it's a module.
         return (RubyClass) runtime.getClassFromPath(path);
     }
 }
diff --git a/src/org/jruby/RubySymbol.java b/src/org/jruby/RubySymbol.java
index a0cdad14db..3a0bf9aada 100644
--- a/src/org/jruby/RubySymbol.java
+++ b/src/org/jruby/RubySymbol.java
@@ -1,380 +1,387 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Derek Berner <derek.berner@state.nm.us>
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
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubySymbol extends RubyObject {
     private final String symbol;
     private final int id;
     
     private RubySymbol(Ruby runtime, String symbol) {
         super(runtime, runtime.getClass("Symbol"));
         this.symbol = symbol;
 
         runtime.symbolLastId++;
         this.id = runtime.symbolLastId;
     }
     
     public static RubyClass createSymbolClass(Ruby runtime) {
         RubyClass symbolClass = runtime.defineClass("Symbol", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubySymbol.class);   
         RubyClass symbolMetaClass = symbolClass.getMetaClass();
         symbolClass.index = ClassIndex.SYMBOL;
 
         
-        symbolClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", IRubyObject.class));
+        symbolClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         symbolClass.defineFastMethod("freeze", callbackFactory.getFastMethod("freeze"));
         symbolClass.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         symbolClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         symbolClass.defineFastMethod("taint", callbackFactory.getFastMethod("taint"));
         symbolClass.defineFastMethod("to_i", callbackFactory.getFastMethod("to_i"));
         symbolClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         symbolClass.defineFastMethod("to_sym", callbackFactory.getFastMethod("to_sym"));
         symbolClass.defineAlias("id2name", "to_s");
         symbolClass.defineAlias("to_int", "to_i");
 
         symbolMetaClass.defineFastMethod("all_symbols", callbackFactory.getFastSingletonMethod("all_symbols"));
 
         symbolMetaClass.undefineMethod("new");
         
         return symbolClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.SYMBOL;
     }
 
     public static final byte NIL_P_SWITCHVALUE = 1;
     public static final byte EQUALEQUAL_SWITCHVALUE = 2;
     public static final byte TO_S_SWITCHVALUE = 3;
     public static final byte TO_I_SWITCHVALUE = 4;
     public static final byte TO_SYM_SWITCHVALUE = 5;
     public static final byte HASH_SWITCHVALUE = 6;
     
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         // If tracing is on, don't do STI dispatch
         if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
         
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case NIL_P_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return nil_p();
         case EQUALEQUAL_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return equal(args[0]);
         case TO_S_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_s();
         case TO_I_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_i();
         case TO_SYM_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return to_sym();
         case HASH_SWITCHVALUE:
             if (args.length != 0) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 0 + ")");
             return hash();
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
 
     /** rb_to_id
      * 
      * @return a String representation of the symbol 
      */
     public String asSymbol() {
         return symbol;
     }
+    
+    /** short circuit for Symbol key comparison
+     * 
+     */
+    public final boolean eql(IRubyObject other) {
+        return other == this;
+    }
 
     public boolean isImmediate() {
     	return true;
     }
     
     public static String getSymbol(Ruby runtime, long id) {
         RubySymbol result = runtime.getSymbolTable().lookup(id);
         if (result != null) {
             return result.symbol;
         }
         return null;
     }
 
     /* Symbol class methods.
      * 
      */
 
     public static RubySymbol newSymbol(Ruby runtime, String name) {
         RubySymbol result;
         synchronized (RubySymbol.class) {
             // Locked to prevent the creation of multiple instances of
             // the same symbol. Most code depends on them being unique.
 
             result = runtime.getSymbolTable().lookup(name);
             if (result == null) {
                 result = new RubySymbol(runtime, name);
                 runtime.getSymbolTable().store(result);
             }
         }
         return result;
     }
 
     public IRubyObject equal(IRubyObject other) {
         // Symbol table ensures only one instance for every name,
         // so object identity is enough to compare symbols.
         return RubyBoolean.newBoolean(getRuntime(), this == other);
     }
 
     public RubyFixnum to_i() {
         return getRuntime().newFixnum(id);
     }
 
     public IRubyObject inspect() {
         return getRuntime().newString(":" + 
             (isSymbolName(symbol) ? symbol : getRuntime().newString(symbol).dump().toString())); 
     }
 
     public IRubyObject to_s() {
         return getRuntime().newString(symbol);
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
     
     public int hashCode() {
         return id;
     }
     
     public boolean equals(Object other) {
         return other == this;
     }
     
     public IRubyObject to_sym() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 
     public IRubyObject taint() {
         return this;
     }
     
     private static boolean isIdentStart(char c) {
         return ((c >= 'a' && c <= 'z')|| (c >= 'A' && c <= 'Z')
                 || c == '_');
     }
     private static boolean isIdentChar(char c) {
         return ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')
                 || c == '_');
     }
     
     private static boolean isIdentifier(String s) {
         if (s == null || s.length() <= 0) {
             return false;
         } 
         
         if (!isIdentStart(s.charAt(0))) {
             return false;
         }
         for (int i = 1; i < s.length(); i++) {
             if (!isIdentChar(s.charAt(i))) {
                 return false;
             }
         }
         
         return true;
     }
     
     /**
      * is_special_global_name from parse.c.  
      * @param s
      * @return
      */
     private static boolean isSpecialGlobalName(String s) {
         if (s == null || s.length() <= 0) {
             return false;
         }
 
         int length = s.length();
            
         switch (s.charAt(0)) {        
         case '~': case '*': case '$': case '?': case '!': case '@': case '/': case '\\':        
         case ';': case ',': case '.': case '=': case ':': case '<': case '>': case '\"':        
         case '&': case '`': case '\'': case '+': case '0':
             return length == 1;            
         case '-':
             return (length == 1 || (length == 2 && isIdentChar(s.charAt(1))));
             
         default:
             // we already confirmed above that length > 0
             for (int i = 0; i < length; i++) {
                 if (!Character.isDigit(s.charAt(i))) {
                     return false;
                 }
             }
         }
         return true;
     }
     
     private static boolean isSymbolName(String s) {
         if (s == null || s.length() < 1) {
             return false;
         }
 
         int length = s.length();
 
         char c = s.charAt(0);
         switch (c) {
         case '$':
             return length > 1 && isSpecialGlobalName(s.substring(1));
         case '@':
             int offset = 1;
             if (length >= 2 && s.charAt(1) == '@') {
                 offset++;
             }
 
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
         
         if (!isIdentStart(c)) {
             return false;
         }
 
         boolean localID = (c >= 'a' && c <= 'z');
         int last = 1;
         
         for (; last < length; last++) {
             char d = s.charAt(last);
             
             if (!isIdentChar(d)) {
                 break;
             }
         }
                     
         if (last == length) {
             return true;
         } else if (localID && last == length - 1) {
             char d = s.charAt(last);
             
             return d == '!' || d == '?' || d == '=';
         }
         
         return false;
     }
     
     public static IRubyObject all_symbols(IRubyObject recv) {
         return recv.getRuntime().newArrayNoCopy(recv.getRuntime().getSymbolTable().all_symbols());
     }
 
     public static RubySymbol unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubySymbol result = RubySymbol.newSymbol(input.getRuntime(), RubyString.byteListToString(input.unmarshalString()));
         input.registerLinkTarget(result);
         return result;
     }
 
     public static class SymbolTable {
        
         private Map table = new HashMap();
         
         public IRubyObject[] all_symbols() {
             int length = table.size();
             IRubyObject[] array = new IRubyObject[length];
             System.arraycopy(table.values().toArray(), 0, array, 0, length);
             return array;
         }
         
         public RubySymbol lookup(long symbolId) {
             Iterator iter = table.values().iterator();
             while (iter.hasNext()) {
                 RubySymbol symbol = (RubySymbol) iter.next();
                 if (symbol != null) {
                     if (symbol.id == symbolId) {
                         return symbol;
                     }
                 }
             }
             return null;
         }
         
         public RubySymbol lookup(String name) {
             return (RubySymbol) table.get(name);
         }
         
         public void store(RubySymbol symbol) {
             table.put(symbol.asSymbol(), symbol);
         }
         
     }
     
 }
diff --git a/src/org/jruby/RubyTime.java b/src/org/jruby/RubyTime.java
index b4a467887e..8f162d9e01 100644
--- a/src/org/jruby/RubyTime.java
+++ b/src/org/jruby/RubyTime.java
@@ -1,698 +1,707 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
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
 
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.Locale;
 import java.util.TimeZone;
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.RubyDateFormat;
 import org.jruby.util.ByteList;
 
 /** The Time class.
  * 
  * @author chadfowler, jpetersen
  */
 public class RubyTime extends RubyObject {
     public static final String UTC = "UTC";
 	private Calendar cal;
     private long usec;
 
     private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("-", Locale.US);
 
     public static TimeZone getLocalTimeZone(Ruby runtime) {
         // TODO: cache the RubyString "TZ" so it doesn't need to be recreated for each call?
         RubyString tzVar = runtime.newString("TZ");
         RubyHash h = ((RubyHash)runtime.getObject().getConstant("ENV"));
         IRubyObject tz = h.aref(tzVar);
         if (tz == null || ! (tz instanceof RubyString)) {
             return TimeZone.getDefault();
         } else {
             return TimeZone.getTimeZone(tz.toString());
         }
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass, Calendar cal) {
         super(runtime, rubyClass);
         this.cal = cal;
     }
     
     private static ObjectAllocator TIME_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyTime instance = new RubyTime(runtime, klass);
             GregorianCalendar cal = new GregorianCalendar();
             cal.setTime(new Date());
             instance.setJavaCalendar(cal);
             return instance;
         }
     };
     
     public static RubyClass createTimeClass(Ruby runtime) {
         RubyClass timeClass = runtime.defineClass("Time", runtime.getObject(), TIME_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyTime.class);
         RubyClass timeMetaClass = timeClass.getMetaClass();
         
         timeClass.includeModule(runtime.getModule("Comparable"));
         
         timeMetaClass.defineAlias("now","new");
         timeMetaClass.defineFastMethod("at", callbackFactory.getFastOptSingletonMethod("new_at"));
         timeMetaClass.defineFastMethod("local", callbackFactory.getFastOptSingletonMethod("new_local"));
         timeMetaClass.defineFastMethod("mktime", callbackFactory.getFastOptSingletonMethod("new_local"));
         timeMetaClass.defineFastMethod("utc", callbackFactory.getFastOptSingletonMethod("new_utc"));
         timeMetaClass.defineFastMethod("gm", callbackFactory.getFastOptSingletonMethod("new_utc"));
-        timeMetaClass.defineMethod("_load", callbackFactory.getSingletonMethod("s_load", IRubyObject.class));
+        timeMetaClass.defineMethod("_load", callbackFactory.getSingletonMethod("s_load", RubyKernel.IRUBY_OBJECT));
         
         // To override Comparable with faster String ones
-        timeClass.defineFastMethod(">=", callbackFactory.getFastMethod("op_ge", IRubyObject.class));
-        timeClass.defineFastMethod(">", callbackFactory.getFastMethod("op_gt", IRubyObject.class));
-        timeClass.defineFastMethod("<=", callbackFactory.getFastMethod("op_le", IRubyObject.class));
-        timeClass.defineFastMethod("<", callbackFactory.getFastMethod("op_lt", IRubyObject.class));
+        timeClass.defineFastMethod(">=", callbackFactory.getFastMethod("op_ge", RubyKernel.IRUBY_OBJECT));
+        timeClass.defineFastMethod(">", callbackFactory.getFastMethod("op_gt", RubyKernel.IRUBY_OBJECT));
+        timeClass.defineFastMethod("<=", callbackFactory.getFastMethod("op_le", RubyKernel.IRUBY_OBJECT));
+        timeClass.defineFastMethod("<", callbackFactory.getFastMethod("op_lt", RubyKernel.IRUBY_OBJECT));
         
-        timeClass.defineFastMethod("===", callbackFactory.getFastMethod("same2", IRubyObject.class));
-        timeClass.defineFastMethod("+", callbackFactory.getFastMethod("op_plus", IRubyObject.class));
-        timeClass.defineFastMethod("-", callbackFactory.getFastMethod("op_minus", IRubyObject.class));
-        timeClass.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", IRubyObject.class));
+        timeClass.defineFastMethod("===", callbackFactory.getFastMethod("same2", RubyKernel.IRUBY_OBJECT));
+        timeClass.defineFastMethod("+", callbackFactory.getFastMethod("op_plus", RubyKernel.IRUBY_OBJECT));
+        timeClass.defineFastMethod("-", callbackFactory.getFastMethod("op_minus", RubyKernel.IRUBY_OBJECT));
+        timeClass.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", RubyKernel.IRUBY_OBJECT));
+        timeClass.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
         timeClass.defineFastMethod("asctime", callbackFactory.getFastMethod("asctime"));
         timeClass.defineFastMethod("mday", callbackFactory.getFastMethod("mday"));
         timeClass.defineAlias("day", "mday"); 
         timeClass.defineAlias("ctime", "asctime");
         timeClass.defineFastMethod("sec", callbackFactory.getFastMethod("sec"));
         timeClass.defineFastMethod("min", callbackFactory.getFastMethod("min"));
         timeClass.defineFastMethod("hour", callbackFactory.getFastMethod("hour"));
         timeClass.defineFastMethod("month", callbackFactory.getFastMethod("month"));
         timeClass.defineAlias("mon", "month"); 
         timeClass.defineFastMethod("year", callbackFactory.getFastMethod("year"));
         timeClass.defineFastMethod("wday", callbackFactory.getFastMethod("wday"));
         timeClass.defineFastMethod("yday", callbackFactory.getFastMethod("yday"));
         timeClass.defineFastMethod("isdst", callbackFactory.getFastMethod("isdst"));
         timeClass.defineAlias("dst?", "isdst");
         timeClass.defineFastMethod("zone", callbackFactory.getFastMethod("zone"));
         timeClass.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         timeClass.defineFastMethod("to_f", callbackFactory.getFastMethod("to_f"));
         timeClass.defineFastMethod("succ", callbackFactory.getFastMethod("succ"));
         timeClass.defineFastMethod("to_i", callbackFactory.getFastMethod("to_i"));
         timeClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         timeClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         timeClass.defineFastMethod("strftime", callbackFactory.getFastMethod("strftime", IRubyObject.class));
         timeClass.defineFastMethod("usec",  callbackFactory.getFastMethod("usec"));
         timeClass.defineAlias("tv_usec", "usec"); 
         timeClass.defineAlias("tv_sec", "to_i"); 
         timeClass.defineFastMethod("gmtime", callbackFactory.getFastMethod("gmtime")); 
         timeClass.defineAlias("utc", "gmtime"); 
         timeClass.defineFastMethod("gmt?", callbackFactory.getFastMethod("gmt"));
         timeClass.defineAlias("utc?", "gmt?");
         timeClass.defineAlias("gmtime?", "gmt?");
         timeClass.defineFastMethod("localtime", callbackFactory.getFastMethod("localtime"));
         timeClass.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         timeClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         timeClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", IRubyObject.class));
         timeClass.defineMethod("_dump", callbackFactory.getOptMethod("dump"));
         timeClass.defineFastMethod("gmt_offset", callbackFactory.getFastMethod("gmt_offset"));
         timeClass.defineAlias("gmtoff", "gmt_offset");
         timeClass.defineAlias("utc_offset", "gmt_offset");
         timeClass.defineFastMethod("getgm", callbackFactory.getFastMethod("getgm"));
         timeClass.defineFastMethod("getlocal", callbackFactory.getFastMethod("getlocal"));
         timeClass.defineAlias("getutc", "getgm");
         
         return timeClass;
     }
     
     public void setUSec(long usec) {
         this.usec = usec;
     }
     
     public void updateCal(Calendar calendar) {
         calendar.setTimeZone(cal.getTimeZone());
         calendar.setTimeInMillis(getTimeInMillis());
     }
     
     protected long getTimeInMillis() {
         return cal.getTimeInMillis();  // For JDK 1.4 we can use "cal.getTimeInMillis()"
     }
     
     public static RubyTime newTime(Ruby runtime, long milliseconds) {
         Calendar cal = Calendar.getInstance(); 
         RubyTime time = new RubyTime(runtime, runtime.getClass("Time"), cal);
         
         cal.setTimeInMillis(milliseconds);
         
         return time;
     }
     
     public static RubyTime newTime(Ruby runtime, Calendar cal) {
         RubyTime time = new RubyTime(runtime, runtime.getClass("Time"), cal);
         
         return time;
     }
 
     public IRubyObject initialize_copy(IRubyObject original) {
         if (!(original instanceof RubyTime)) {
             throw getRuntime().newTypeError("Expecting an instance of class Time");
         }
         
         RubyTime originalTime = (RubyTime) original;
         
         cal = (Calendar)(originalTime.cal.clone());
         usec = originalTime.usec;
         
         return this;
     }
 
     public RubyTime succ() {
         Calendar newCal = (Calendar)cal.clone();
         newCal.add(Calendar.SECOND,1);
         return newTime(getRuntime(),newCal);
     }
 
     public RubyTime gmtime() {
         cal.setTimeZone(TimeZone.getTimeZone(UTC));
         return this;
     }
 
     public RubyTime localtime() {
         long dump = cal.getTimeInMillis();
         cal = Calendar.getInstance(getLocalTimeZone(getRuntime()));
         cal.setTimeInMillis(dump);
         return this;
     }
     
     public RubyBoolean gmt() {
         return getRuntime().newBoolean(cal.getTimeZone().getID().equals(UTC));
     }
     
     public RubyTime getgm() {
         Calendar newCal = (Calendar)cal.clone();
         newCal.setTimeZone(TimeZone.getTimeZone(UTC));
         return newTime(getRuntime(), newCal);
     }
 
     public RubyTime getlocal() {
         Calendar newCal = (Calendar)cal.clone();
         newCal.setTimeZone(getLocalTimeZone(getRuntime()));
         return newTime(getRuntime(), newCal);
     }
 
     public RubyString strftime(IRubyObject format) {
         final RubyDateFormat rubyDateFormat = new RubyDateFormat("-", Locale.US);
         rubyDateFormat.setCalendar(cal);
         rubyDateFormat.applyPattern(format.toString());
         String result = rubyDateFormat.format(cal.getTime());
 
         return getRuntime().newString(result);
     }
     
     public IRubyObject op_ge(IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) >= 0);
         }
         
         return RubyComparable.op_ge(this, other);
     }
     
     public IRubyObject op_gt(IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) > 0);
         }
         
         return RubyComparable.op_gt(this, other);
     }
     
     public IRubyObject op_le(IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) <= 0);
         }
         
         return RubyComparable.op_le(this, other);
     }
     
     public IRubyObject op_lt(IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) < 0);
         }
         
         return RubyComparable.op_lt(this, other);
     }
     
     private int cmp(RubyTime other) {
         long millis = getTimeInMillis();
 		long millis_other = other.getTimeInMillis();
         long usec_other = other.usec;
         
 		if (millis > millis_other || (millis == millis_other && usec > usec_other)) {
 		    return 1;
 		} else if (millis < millis_other || (millis == millis_other && usec < usec_other)) {
 		    return -1;
 		} 
 
         return 0;
     }
     
     public IRubyObject op_plus(IRubyObject other) {
         long time = getTimeInMillis();
 
         if (other instanceof RubyTime) {
             throw getRuntime().newTypeError("time + time ?");
         }
 		time += ((RubyNumeric) other).getDoubleValue() * 1000;
 
 		RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
 		newTime.cal = Calendar.getInstance();
         newTime.cal.setTimeZone(cal.getTimeZone());
 		newTime.cal.setTime(new Date(time));
 
 		return newTime;
     }
 
     public IRubyObject op_minus(IRubyObject other) {
         long time = getTimeInMillis();
 
         if (other instanceof RubyTime) {
             time -= ((RubyTime) other).getTimeInMillis();
 
             return RubyFloat.newFloat(getRuntime(), time * 10e-4);
         }
 		time -= ((RubyNumeric) other).getDoubleValue() * 1000;
 
 		RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
 		newTime.cal = Calendar.getInstance();
         newTime.cal.setTimeZone(cal.getTimeZone());
 		newTime.cal.setTime(new Date(time));
 
 		return newTime;
     }
 
     public IRubyObject same2(IRubyObject other) {
         return (RubyNumeric.fix2int(callMethod(getRuntime().getCurrentContext(), MethodIndex.OP_SPACESHIP, "<=>", other)) == 0) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public IRubyObject op_cmp(IRubyObject other) {
         if (other.isNil()) {
         	return other;
         }
         
         if (other instanceof RubyTime) {
             return getRuntime().newFixnum(cmp((RubyTime) other));
         }
         
         long millis = getTimeInMillis();
 
         if(other instanceof RubyNumeric) {
             if (other instanceof RubyFloat || other instanceof RubyBignum) {
                 double time = millis / 1000.0;
 
                 double time_other = ((RubyNumeric) other).getDoubleValue();
 
                 if (time > time_other) {
                     return RubyFixnum.one(getRuntime());
                 } else if (time < time_other) {
                     return RubyFixnum.minus_one(getRuntime());
                 }
 
                 return RubyFixnum.zero(getRuntime());
             }
             long millis_other = RubyNumeric.num2long(other) * 1000;
 
             if (millis > millis_other || (millis == millis_other && usec > 0)) {
                 return RubyFixnum.one(getRuntime());
             } else if (millis < millis_other || (millis == millis_other && usec < 0)) {
                 return RubyFixnum.minus_one(getRuntime());
             }
 
             return RubyFixnum.zero(getRuntime());
         }
         return getRuntime().getNil();
     }
+    
+    public IRubyObject eql_p(IRubyObject other) {
+        if (other instanceof RubyTime) {
+            RubyTime otherTime = (RubyTime)other; 
+            return (usec == otherTime.usec && getTimeInMillis() == otherTime.getTimeInMillis()) ? getRuntime().getTrue() : getRuntime().getFalse();
+        }
+        return getRuntime().getFalse();
+    }
 
     public RubyString asctime() {
         simpleDateFormat.setCalendar(cal);
         simpleDateFormat.applyPattern("EEE MMM dd HH:mm:ss yyyy");
         String result = simpleDateFormat.format(cal.getTime());
 
         return getRuntime().newString(result);
     }
 
     public IRubyObject to_s() {
         simpleDateFormat.setCalendar(cal);
         simpleDateFormat.applyPattern("EEE MMM dd HH:mm:ss Z yyyy");
         String result = simpleDateFormat.format(cal.getTime());
 
         return getRuntime().newString(result);
     }
 
     public RubyArray to_a() {
         return getRuntime().newArrayNoCopy(new IRubyObject[] { sec(), min(), hour(), mday(), month(), 
                 year(), wday(), yday(), isdst(), zone() });
     }
 
     public RubyFloat to_f() {
         return RubyFloat.newFloat(getRuntime(), getTimeInMillis() / 1000 + microseconds() / 1000000.0);
     }
 
     public RubyInteger to_i() {
         return getRuntime().newFixnum(getTimeInMillis() / 1000);
     }
 
     public RubyInteger usec() {
         return getRuntime().newFixnum(microseconds());
     }
     
     public void setMicroseconds(long mic) {
         long millis = getTimeInMillis() % 1000;
         long withoutMillis = getTimeInMillis() - millis;
         withoutMillis += (mic / 1000);
         cal.setTimeInMillis(withoutMillis);
         usec = mic % 1000;
     }
     
     public long microseconds() {
     	return getTimeInMillis() % 1000 * 1000 + usec;
     }
 
     public RubyInteger sec() {
         return getRuntime().newFixnum(cal.get(Calendar.SECOND));
     }
 
     public RubyInteger min() {
         return getRuntime().newFixnum(cal.get(Calendar.MINUTE));
     }
 
     public RubyInteger hour() {
         return getRuntime().newFixnum(cal.get(Calendar.HOUR_OF_DAY));
     }
 
     public RubyInteger mday() {
         return getRuntime().newFixnum(cal.get(Calendar.DAY_OF_MONTH));
     }
 
     public RubyInteger month() {
         return getRuntime().newFixnum(cal.get(Calendar.MONTH) + 1);
     }
 
     public RubyInteger year() {
         return getRuntime().newFixnum(cal.get(Calendar.YEAR));
     }
 
     public RubyInteger wday() {
         return getRuntime().newFixnum(cal.get(Calendar.DAY_OF_WEEK) - 1);
     }
 
     public RubyInteger yday() {
         return getRuntime().newFixnum(cal.get(Calendar.DAY_OF_YEAR));
     }
 
     public RubyInteger gmt_offset() {
         return getRuntime().newFixnum((int)(cal.get(Calendar.ZONE_OFFSET)/1000));
     }
     
     public RubyBoolean isdst() {
         return getRuntime().newBoolean(cal.getTimeZone().inDaylightTime(cal.getTime()));
     }
 
     public RubyString zone() {
         return getRuntime().newString(cal.getTimeZone().getID());
     }
 
     public void setJavaCalendar(Calendar cal) {
         this.cal = cal;
     }
 
     public Date getJavaDate() {
         return this.cal.getTime();
     }
 
     public RubyFixnum hash() {
     	// modified to match how hash is calculated in 1.8.2
         return getRuntime().newFixnum((int)(((cal.getTimeInMillis() / 1000) ^ microseconds()) << 1) >> 1);
     }    
 
     public RubyString dump(final IRubyObject[] args, Block unusedBlock) {
         if (args.length > 1) {
             throw getRuntime().newArgumentError(0, 1);
         }
         
         RubyString str = (RubyString) mdump(new IRubyObject[] { this });
         str.setInstanceVariables(this.getInstanceVariables());
         return str;
     }    
 
     public RubyObject mdump(final IRubyObject[] args) {
         RubyTime obj = (RubyTime)args[0];
         Calendar calendar = obj.gmtime().cal;
         byte dumpValue[] = new byte[8];
         int pe = 
             0x1                                 << 31 |
             (calendar.get(Calendar.YEAR)-1900)  << 14 |
             calendar.get(Calendar.MONTH)        << 10 |
             calendar.get(Calendar.DAY_OF_MONTH) << 5  |
             calendar.get(Calendar.HOUR_OF_DAY);
         int se =
             calendar.get(Calendar.MINUTE)       << 26 |
             calendar.get(Calendar.SECOND)       << 20 |
             calendar.get(Calendar.MILLISECOND);
 
         for(int i = 0; i < 4; i++) {
             dumpValue[i] = (byte)(pe & 0xFF);
             pe >>>= 8;
         }
         for(int i = 4; i < 8 ;i++) {
             dumpValue[i] = (byte)(se & 0xFF);
             se >>>= 8;
         }
         return RubyString.newString(obj.getRuntime(), new ByteList(dumpValue,false));
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         return this;
     }
     
     /* Time class methods */
     
     public static IRubyObject s_new(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyTime time = new RubyTime(runtime, (RubyClass) recv);
         GregorianCalendar cal = new GregorianCalendar();
         cal.setTime(new Date());
         time.setJavaCalendar(cal);
         time.callInit(args,block);
         return time;
     }
 
     public static IRubyObject new_at(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int len = Arity.checkArgumentCount(runtime, args, 1, 2);
 
         Calendar cal = Calendar.getInstance(); 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, cal);
 
         if (args[0] instanceof RubyTime) {
             ((RubyTime) args[0]).updateCal(cal);
         } else {
             long seconds = RubyNumeric.num2long(args[0]);
             long millisecs = 0;
             long microsecs = 0;
             if (len > 1) {
                 long tmp = RubyNumeric.num2long(args[1]);
                 millisecs = tmp / 1000;
                 microsecs = tmp % 1000;
             }
             else {
                 // In the case of two arguments, MRI will discard the portion of
                 // the first argument after a decimal point (i.e., "floor").
                 // However in the case of a single argument, any portion after
                 // the decimal point is honored.
                 if (args[0] instanceof RubyFloat) {
                     double dbl = ((RubyFloat) args[0]).getDoubleValue();
                     long micro = (long) ((dbl - seconds) * 1000000);
                     millisecs = micro / 1000;
                     microsecs = micro % 1000;
                 }
             }
             time.setUSec(microsecs);
             cal.setTimeInMillis(seconds * 1000 + millisecs);
         }
 
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
 
         return time;
     }
 
     public static RubyTime new_local(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, false);
     }
 
     public static RubyTime new_utc(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, true);
     }
 
     public static RubyTime s_load(IRubyObject recv, IRubyObject from, Block block) {
         return s_mload(recv, (RubyTime)(((RubyClass)recv).allocate()), from);
     }
 
     protected static RubyTime s_mload(IRubyObject recv, RubyTime time, IRubyObject from) {
         Ruby runtime = recv.getRuntime();
         Calendar calendar = Calendar.getInstance();
         calendar.clear();
         calendar.setTimeZone(TimeZone.getTimeZone(RubyTime.UTC));
         byte[] fromAsBytes = null;
         fromAsBytes = from.convertToString().getBytes();
         if(fromAsBytes.length != 8) {
             throw runtime.newTypeError("marshaled time format differ");
         }
         int p=0;
         int s=0;
         for (int i = 0; i < 4; i++) {
             p |= ((int)fromAsBytes[i] & 0xFF) << (8 * i);
         }
         for (int i = 4; i < 8; i++) {
             s |= ((int)fromAsBytes[i] & 0xFF) << (8 * (i - 4));
         }
         if ((p & (1<<31)) == 0) {
             calendar.setTimeInMillis(p * 1000L + s);
         } else {
             p &= ~(1<<31);
             calendar.set(Calendar.YEAR, ((p >>> 14) & 0xFFFF) + 1900);
             calendar.set(Calendar.MONTH, ((p >>> 10) & 0xF));
             calendar.set(Calendar.DAY_OF_MONTH, ((p >>> 5)  & 0x1F));
             calendar.set(Calendar.HOUR_OF_DAY, (p & 0x1F));
             calendar.set(Calendar.MINUTE, ((s >>> 26) & 0x3F));
             calendar.set(Calendar.SECOND, ((s >>> 20) & 0x3F));
             calendar.set(Calendar.MILLISECOND, (s & 0xFFFFF));
         }
         time.setJavaCalendar(calendar);
         return time;
     }
     
     private static final String[] months = {"jan", "feb", "mar", "apr", "may", "jun",
                                             "jul", "aug", "sep", "oct", "nov", "dec"};
     private static final long[] time_min = {1, 0, 0, 0, 0};
     private static final long[] time_max = {31, 23, 59, 60, Long.MAX_VALUE};
 
     private static RubyTime createTime(IRubyObject recv, IRubyObject[] args, boolean gmt) {
         Ruby runtime = recv.getRuntime();
         int len = 6;
         
         if (args.length == 10) {
             args = new IRubyObject[] { args[5], args[4], args[3], args[2], args[1], args[0] };
         } else {
             // MRI accepts additional wday argument which appears to be ignored.
             len = Arity.checkArgumentCount(runtime, args, 1, 8);
         }
         ThreadContext tc = runtime.getCurrentContext();
         
         if(args[0] instanceof RubyString) {
             args[0] = RubyNumeric.str2inum(runtime, (RubyString) args[0], 10, false);
         }
         
         int year = (int) RubyNumeric.num2long(args[0]);
         int month = 0;
         
         if (len > 1) {
             if (!args[1].isNil()) {
                 if (args[1] instanceof RubyString) {
                     month = -1;
                     for (int i = 0; i < 12; i++) {
                         if (months[i].equalsIgnoreCase(args[1].toString())) {
                             month = i;
                         }
                     }
                     if (month == -1) {
                         try {
                             month = Integer.parseInt(args[1].toString()) - 1;
                         } catch (NumberFormatException nfExcptn) {
                             throw runtime.newArgumentError("Argument out of range.");
                         }
                     }
                 } else {
                     month = (int)RubyNumeric.num2long(args[1]) - 1;
                 }
             }
             if (0 > month || month > 11) {
                 throw runtime.newArgumentError("Argument out of range.");
             }
         }
 
         int[] int_args = { 1, 0, 0, 0, 0 };
 
         for (int i = 0; len > i + 2; i++) {
             if (!args[i + 2].isNil()) {
                 if(!(args[i+2] instanceof RubyNumeric)) {
                     args[i+2] = args[i+2].callMethod(tc,"to_i");
                 }
                 int_args[i] = (int)RubyNumeric.num2long(args[i + 2]);
                 if (time_min[i] > int_args[i] || int_args[i] > time_max[i]) {
                     throw runtime.newArgumentError("Argument out of range.");
                 }
             }
         }
         
         if (year < 100) year += 2000;
         
         Calendar cal;
         if (gmt) {
             cal = Calendar.getInstance(TimeZone.getTimeZone(RubyTime.UTC)); 
         } else {
             cal = Calendar.getInstance(RubyTime.getLocalTimeZone(runtime));
         }
         cal.set(year, month, int_args[0], int_args[1], int_args[2], int_args[3]);
         cal.set(Calendar.MILLISECOND, int_args[4] / 1000);
         
         if (cal.getTimeInMillis() / 1000 < -0x80000000) {
             throw runtime.newArgumentError("time out of range");
         }
         
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, cal);
         
         time.setUSec(int_args[4] % 1000);
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
 
         return time;
     }
 }
diff --git a/src/org/jruby/runtime/MethodIndex.java b/src/org/jruby/runtime/MethodIndex.java
index 1073e5bce5..5302f233aa 100644
--- a/src/org/jruby/runtime/MethodIndex.java
+++ b/src/org/jruby/runtime/MethodIndex.java
@@ -1,119 +1,123 @@
 /*
  * MethodIndex.java
  *
  * Created on January 1, 2007, 7:39 PM
  *
  * To change this template, choose Tools | Template Manager
  * and open the template in the editor.
  */
 
 package org.jruby.runtime;
 
 import java.util.HashMap;
 import java.util.Map;
 
 /**
  *
  * @author headius
  */
 public class MethodIndex {
     public static final int NO_INDEX = 0;
     public static final int OP_PLUS = 1;
     public static final int OP_MINUS = 2;
     public static final int OP_LT = 3;
     public static final int AREF = 4;
     public static final int ASET = 5;
     public static final int POP = 6;
     public static final int PUSH = 7;
     public static final int NIL_P = 8;
     public static final int EQUALEQUAL = 9;
     public static final int UNSHIFT = 10;
     public static final int OP_GE = 11;
     public static final int OP_LSHIFT = 12;
     public static final int EMPTY_P = 13;
     public static final int TO_S = 14;
     public static final int TO_I = 15;
     public static final int AT = 16;
     public static final int TO_STR = 17;
     public static final int TO_ARY = 18;
     public static final int TO_INT = 19;
     public static final int TO_F = 20;
     public static final int TO_SYM = 21;
     public static final int TO_A = 22;
     public static final int HASH = 23;
     public static final int OP_GT = 24;
     public static final int OP_TIMES = 25;
     public static final int OP_LE = 26;
     public static final int OP_SPACESHIP = 27;
     public static final int LENGTH = 28;
     public static final int OP_MATCH = 29;
     public static final int OP_EQQ = 30;
     public static final int LAST = 31;
     public static final int SHIFT = 32;
-    public static final int MAX_METHODS = 33;
+    public static final int EQL_P = 33;
+    public static final int TO_HASH = 34;
+    public static final int MAX_METHODS = 35;
     
     public static final String[] NAMES = new String[MAX_METHODS];
     public static final Map NUMBERS = new HashMap();
     
     static {
         NAMES[NO_INDEX] = "";
         NAMES[OP_PLUS] = "+";
         NAMES[OP_MINUS] = "-";
         NAMES[OP_LT] = "<";
         NAMES[AREF] = "[]";
         NAMES[ASET] = "[]=";
         NAMES[POP] = "pop";
         NAMES[PUSH] = "push";
         NAMES[NIL_P] = "nil?";
         NAMES[EQUALEQUAL] = "==";
         NAMES[UNSHIFT] = "unshift";
         NAMES[OP_GE] = ">=";
         NAMES[OP_LSHIFT] = "<<";
         NAMES[EMPTY_P] = "empty?";
         NAMES[TO_S] = "to_s";
         NAMES[TO_I] = "to_i";
         NAMES[AT] = "at";
         NAMES[TO_STR] = "to_str";
         NAMES[TO_ARY] = "to_ary";
         NAMES[TO_INT] = "to_int";
         NAMES[TO_F] = "to_f";
         NAMES[TO_SYM] = "to_sym";
         NAMES[TO_A] = "to_a";
         NAMES[HASH] = "hash";
         NAMES[OP_GT] = ">";
         NAMES[OP_TIMES] = "*";
         NAMES[OP_LE] = "<=";
         NAMES[OP_SPACESHIP] = "<=>";
         NAMES[LENGTH] = "length";
         NAMES[OP_MATCH] = "=~";
         NAMES[OP_EQQ] = "===";
         NAMES[LAST] = "last";
         NAMES[SHIFT] = "shift";
+        NAMES[EQL_P] = "eql?";
+        NAMES[TO_HASH] = "to_hash";
         
         for (int i = 0; i < MAX_METHODS; i++) {
             NUMBERS.put(NAMES[i], new Integer(i));
         }
     }
     
     /** Creates a new instance of MethodIndex */
     public MethodIndex() {
     }
     
     public static int getIndex(String methodName) {
         // fast lookup for the length 1 messages
         switch (methodName.length()) {
         case 1:
             switch (methodName.charAt(0)) {
             case '+': return OP_PLUS;
             case '-': return OP_MINUS;
             case '<': return OP_LT;
             case '>': return OP_GT;
             case '*': return OP_TIMES;
             default: return NO_INDEX;
             }
         default:
             if (NUMBERS.containsKey(methodName)) return ((Integer)NUMBERS.get(methodName)).intValue();
             return NO_INDEX;
         }
     }
 }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index 25fbcb9cfb..0506c2d1f8 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,548 +1,550 @@
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
 import org.jruby.RubyHash;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
     /**
      *
      */
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * Return the ClassIndex value for the native type this object was
      * constructed from. Particularly useful for determining marshalling
      * format. All instances of subclasses of Hash, for example
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
     
     /**
      *
      * @return
      */
     Map getInstanceVariables();
     
     /**
      *
      * 
      * @param instanceVariables 
      */
     void setInstanceVariables(Map instanceVariables);
     
     /**
      *
      * @return
      */
     Map getInstanceVariablesSnapshot();
     
     /**
      *
      * @param context
      * @param rubyclass
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name, IRubyObject[] args, CallType callType, Block block);
     /**
      *
      * @param context
      * @param rubyclass
      * @param methodIndex
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param arg
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject arg);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param args
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject[] args);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param args
      * @param callType
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject[] args, CallType callType);
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param callType
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType);
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType, Block block);
     // Used by the compiler, to allow visibility checks
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param caller
      * @param callType
      * @param block
      * @return
      */
     IRubyObject compilerCallMethod(ThreadContext context, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param args
      * @param caller
      * @param callType
      * @param block
      * @return
      */
     IRubyObject compilerCallMethodWithIndex(ThreadContext context, int methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
     /**
      *
      * @param context
      * @param args
      * @param block
      * @return
      */
     IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block);
     /**
      *
      * @param context
      * @param string
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string);
     /**
      *
      * @param context
      * @param string
      * @return
      */
     IRubyObject callMethod(ThreadContext context, int methodIndex, String string);
     /**
      *
      * @param context
      * @param string
      * @param aBlock
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string, Block aBlock);
     /**
      *
      * @param context
      * @param string
      * @param arg
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string, IRubyObject arg);
     /**
      *
      * @param context
      * @param method
      * @param rubyArgs
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs);
     /**
      *
      * @param context
      * @param method
      * @param rubyArgs
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs, Block block);
     
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
     
     /**
      *
      * @return
      */
     boolean isTrue();
     
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
     
     /**
      * RubyMethod setTaint.
      * @param b
      */
     void setTaint(boolean b);
     
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
     
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
     
     /**
      *
      * @return
      */
     boolean isImmediate();
     
     /**
      * RubyMethod isKindOf.
      * @param rubyClass
      * @return boolean
      */
     boolean isKindOf(RubyModule rubyClass);
     
     /**
      * Infect this object using the taint of another object
      * @param obj
      * @return
      */
     IRubyObject infectBy(IRubyObject obj);
     
     /**
      * RubyMethod getRubyClass.
      * @return
      */
     RubyClass getMetaClass();
     
     /**
      *
      * @param metaClass
      */
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
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
     
     /**
      * RubyMethod getRuntime.
      * @return
      */
     Ruby getRuntime();
     
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
     
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
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(ThreadContext context, IRubyObject evalString, String file);
     
     /**
      * Convert the object into a symbol name if possible.
      *
      * @return String the symbol name
      */
     String asSymbol();
     
     /** rb_obj_as_string
      * @return
      */
     RubyString asString();
     
     /**
      * Methods which perform to_xxx if the object has such a method
      * @return
      */
     RubyArray convertToArray();
     /**
      *
      * @return
      */
     RubyHash convertToHash();    
     /**
     *
     * @return
     */    
     RubyFloat convertToFloat();
     /**
      *
      * @return
      */
     RubyInteger convertToInteger();
     /**
      *
      * @return
      */
     RubyString convertToString();
     
     /**
      * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
      *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
      * @return the converted value
      */
     IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raiseOnError);
     
     /**
      * Higher level conversion utility similiar to convertToType but it can throw an
      * additional TypeError during conversion (MRI: rb_check_convert_type).
      *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @return the converted value
      */
     IRubyObject convertToTypeWithCheck(RubyClass targetType, int convertMethodIndex, String convertMethod);
    
     /**
      * 
      * @param targetType 
      * @param convertMethod 
      * @param raiseOnMissingMethod 
      * @param raiseOnWrongTypeResult 
      * @param allowNilThrough 
      * @return 
      */
     public IRubyObject convertToType(RubyClass targetType, int convertMethodIndex, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough);
     
     /**
      *
      * @return
      */
     IRubyObject anyToString();
     
     /**
      *
      * @return
      */
     IRubyObject checkStringType();
     
     /**
      *
      * @return
      */
     IRubyObject checkArrayType();
 
     /**
      * RubyMethod dup.
      * @return
      */
     IRubyObject dup();
     
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
     
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
     
     
     /**
      *
      * @return
      */
     boolean isSingleton();
     
     /**
      *
      * @return
      */
     Iterator instanceVariableNames();
     
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
     
     /**
      *
      * @return
      */
     RubyFixnum id();
     
-    /**
-     *
-     * @param context
-     * @param other
-     * @return
-     */
+    
+    public IRubyObject equal(IRubyObject other); 
+
     IRubyObject equalInternal(final ThreadContext context, final IRubyObject other);
-        
+
+
+    public boolean eql(IRubyObject other);
+
+    public boolean eqlInternal(final ThreadContext context, final IRubyObject other);
+
     public void addFinalizer(RubyProc finalizer);
 
     public void removeFinalizers();
 }
diff --git a/src/org/jruby/util/ByteList.java b/src/org/jruby/util/ByteList.java
index 707060cc51..5b56bc7fbe 100644
--- a/src/org/jruby/util/ByteList.java
+++ b/src/org/jruby/util/ByteList.java
@@ -1,537 +1,540 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
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
 package org.jruby.util;
 
 import java.io.Serializable;
 
 
 /**
  *
  * @author headius
  */
 public final class ByteList implements Comparable, CharSequence, Serializable {
     private static final long serialVersionUID = -1286166947275543731L;
 
     public static final byte[] NULL_ARRAY = new byte[0];
 
     public byte[] bytes;
     public int begin;
     public int realSize;
 
     int hash;
     boolean validHash = false;
     String stringValue;
 
     private static final int DEFAULT_SIZE = 4;
     private static final double FACTOR = 1.5;
 
     /** Creates a new instance of ByteList */
     public ByteList() {
         this(DEFAULT_SIZE);
     }
 
     public ByteList(int size) {
         bytes = new byte[size];
         realSize = 0;
     }
 
     public ByteList(byte[] wrap) {
         this(wrap,true);
     }
 
     public ByteList(byte[] wrap, boolean copy) {
         if (wrap == null) throw new NullPointerException("Invalid argument: constructing with null array");
         if(copy) {
             bytes = (byte[])wrap.clone();
         } else {
             bytes = wrap;
         }
         realSize = wrap.length;
     }
 
     public ByteList(ByteList wrap) {
         this(wrap.bytes, wrap.begin, wrap.realSize);
     }
 
     public ByteList(byte[] wrap, int index, int len) {
         this(wrap,index,len,true);
     }
 
     public ByteList(byte[] wrap, int index, int len, boolean copy) {
         if (wrap == null) throw new NullPointerException("Invalid argument: constructing with null array");
         if(copy || index != 0) {
             bytes = new byte[len];
             System.arraycopy(wrap, index, bytes, 0, len);
         } else {
             bytes = wrap;
         }
         realSize = len;
     }
 
     public ByteList(ByteList wrap, int index, int len) {
         this(wrap.bytes, wrap.begin + index, len);
     }
 
     private ByteList(boolean flag) {
     }
 
     public void delete(int start, int len) {
         realSize-=len;
         System.arraycopy(bytes,start+len,bytes,start,realSize);
     }
 
     public ByteList append(byte b) {
         grow(1);
         bytes[realSize++] = b;
         return this;
     }
 
     public ByteList append(int b) {
         append((byte)b);
         return this;
     }
 
     public Object clone() {
         return dup();
     }
 
     public ByteList dup() {
         ByteList dup = new ByteList(false);
         dup.bytes = new byte[realSize];
         System.arraycopy(bytes, begin, dup.bytes, 0, realSize);
         dup.realSize = realSize;
         dup.begin = 0;
 
         dup.validHash = validHash;
         dup.hash = hash;
         dup.stringValue = stringValue;
 
         return dup;        
     }
 
     public ByteList makeShared(int index, int len) {
         ByteList shared = new ByteList(false);        
         shared.bytes = bytes;
         shared.realSize = len;        
         shared.begin = begin + index;
         return shared;
     }
 
     public void view(int index, int len) {
         realSize = len;
         begin = begin + index;
     }
 
     public void unshare() {
         byte[] newBytes = new byte[realSize];
         System.arraycopy(bytes, begin, newBytes, 0, realSize);
         bytes = newBytes;
         begin = 0;
     }
 
     public void invalidate() {
         validHash = false;
         stringValue = null;
     }
 
     public void prepend(byte b) {
         grow(1);
         System.arraycopy(bytes, 0, bytes, 1, realSize);
         bytes[0] = b;
         realSize++;
     }
 
     public void append(byte[] moreBytes) {
         grow(moreBytes.length);
         System.arraycopy(moreBytes, 0, bytes, realSize, moreBytes.length);
         realSize += moreBytes.length;
     }
 
     public void append(ByteList moreBytes) {
         append(moreBytes.bytes, moreBytes.begin, moreBytes.realSize);
     }
 
     public void append(ByteList moreBytes, int index, int len) {
         append(moreBytes.bytes, moreBytes.begin + index, len);
     }
 
     public void append(byte[] moreBytes, int start, int len) {
         grow(len);
         System.arraycopy(moreBytes, start, bytes, realSize, len);
         realSize += len;
     }
 
     public int length() {
         return realSize;
     }
 
     public void length(int newLength) {
         grow(newLength - realSize);
         realSize = newLength;
     }
 
     public int get(int index) {
         if (index >= realSize) throw new IndexOutOfBoundsException();
         return bytes[begin + index];
     }
 
     public void set(int index, int b) {
         if (index >= realSize) throw new IndexOutOfBoundsException();
         bytes[begin + index] = (byte)b;
     }
 
     public void replace(byte[] newBytes) {
         if (newBytes == null) throw new NullPointerException("Invalid argument: replacing with null array");
         this.bytes = newBytes;
         realSize = newBytes.length;
     }
 
     /**
      * Unsafe version of replace(int,int,ByteList). The contract is that these
      * unsafe versions will not make sure thet beg and len indices are correct.
      */
     public void unsafeReplace(int beg, int len, ByteList nbytes) {
         unsafeReplace(beg, len, nbytes.bytes, nbytes.begin, nbytes.realSize);
     }
 
     /**
      * Unsafe version of replace(int,int,byte[]). The contract is that these
      * unsafe versions will not make sure thet beg and len indices are correct.
      */
     public void unsafeReplace(int beg, int len, byte[] buf) {
         unsafeReplace(beg, len, buf, 0, buf.length);
     }
 
     /**
      * Unsafe version of replace(int,int,byte[],int,int). The contract is that these
      * unsafe versions will not make sure thet beg and len indices are correct.
      */
     public void unsafeReplace(int beg, int len, byte[] nbytes, int index, int count) {
         grow(count - len);
         int newSize = realSize + count - len;
         System.arraycopy(bytes,beg+len,bytes,beg+count,realSize - (len+beg));
         System.arraycopy(nbytes,index,bytes,beg,count);
         realSize = newSize;
     }
 
     public void replace(int beg, int len, ByteList nbytes) {
         replace(beg, len, nbytes.bytes, nbytes.begin, nbytes.realSize);
     }
 
     public void replace(int beg, int len, byte[] buf) {
         replace(beg, len, buf, 0, buf.length);
     }
 
     public void replace(int beg, int len, byte[] nbytes, int index, int count) {
         if (len - beg > realSize) throw new IndexOutOfBoundsException();
         unsafeReplace(beg,len,nbytes,index,count);
     }
 
     public void insert(int index, int b) {
         if (index >= realSize) throw new IndexOutOfBoundsException();
         grow(1);
         System.arraycopy(bytes,index,bytes,index+1,realSize-index);
         bytes[index] = (byte)b;
         realSize++;
     }
 
     public int indexOf(int c) {
         return indexOf(c, 0);
     }
 
     public int indexOf(final int c, int pos) {
         // not sure if this is checked elsewhere,
         // didn't see it in RubyString. RubyString does
         // cast to char, so c will be >= 0.
         if (c > 255)
             return -1;
         final byte b = (byte)(c&0xFF);
         final int size = begin + realSize;
         final byte[] buf = bytes;
         pos += begin;
         for ( ; pos < size && buf[pos] != b ; pos++ ) ;
         return pos < size ? pos - begin : -1;
     }
 
     public int indexOf(ByteList find) {
         return indexOf(find, 0);
     }
 
     public int indexOf(final ByteList find, int pos) {
         final int len = find.realSize;
         if (len == 0) return -1;
 
         final byte first = find.bytes[find.begin];
         final byte[] buf = bytes;
         final int max = realSize - len + 1;
         for ( ; pos < max ; pos++ ) {
             for ( ; pos < max && buf[begin + pos] != first; pos++ ) ;
             if (pos == max)
                 return -1;
             int index = len;
             // TODO: forward/backward scan as in #equals
             for ( ; --index >= 0 && buf[begin + index + pos] == find.bytes[find.begin + index]; ) ;
             if (index < 0)
                 return pos;
         }
         return -1;
     }
 
     public int lastIndexOf(int c) {
         return lastIndexOf(c, realSize - 1);
     }
 
     public int lastIndexOf(final int c, int pos) {
         // not sure if this is checked elsewhere,
         // didn't see it in RubyString. RubyString does
         // cast to char, so c will be >= 0.
         if (c > 255)
             return -1;
         final byte b = (byte)(c&0xFF);
         final int size = begin + realSize;
         pos += begin;
         final byte[] buf = bytes;
         if (pos >= size) {
             pos = size;
         } else {
             pos++;
         }
         for ( ; --pos >= begin && buf[pos] != b ; ) ;
         return pos - begin;
     }
 
     public int lastIndexOf(ByteList find) {
         return lastIndexOf(find, realSize - 1);
     }
 
     public int lastIndexOf(final ByteList find, int pos) {
         final int len = find.realSize;
         if (len == 0) return -1;
 
         final byte first = find.bytes[find.begin];
         final byte[] buf = bytes;
         pos = Math.min(pos,realSize-len);
         for ( ; pos >= 0 ; pos-- ) {
             for ( ; pos >= 0 && buf[begin + pos] != first; pos-- ) ;
             if (pos < 0)
                 return -1;
             int index = len;
             // TODO: forward/backward scan as in #equals
             for ( ; --index >= 0 && buf[begin + index + pos] == find.bytes[find.begin + index]; ) ;
             if (index < 0)
                 return pos;
         }
         return -1;
     }
 
     public boolean equals(Object other) {
-        if (other == this) return true;
-        if (other instanceof ByteList) {
-            ByteList b = (ByteList) other;
+        if (other instanceof ByteList) return equal((ByteList)other);
+        return false;
+    }
+    
+    public boolean equal(ByteList other) {
+        if (other == this) return true; 
+        if (validHash && other.validHash && hash != other.hash) return false;
             int first;
             int last;
             byte[] buf;
-            if ((last = realSize) == b.realSize) {
+        if ((last = realSize) == other.realSize) {
                 // scanning from front and back simultaneously, meeting in
                 // the middle. the object is to get a mismatch as quickly as
                 // possible. alternatives might be: scan from the middle outward
                 // (not great because it won't pick up common variations at the
                 // ends until late) or sample odd bytes forward and even bytes
                 // backward (I like this one, but it's more expensive for
                 // strings that are equal; see sample_equals below).
                 for (buf = bytes, first = -1; 
-                    --last > first && buf[begin + last] == b.bytes[b.begin + last] &&
-                    ++first < last && buf[begin + first] == b.bytes[b.begin + first] ; ) ;
+                --last > first && buf[begin + last] == other.bytes[other.begin + last] &&
+                ++first < last && buf[begin + first] == other.bytes[other.begin + first] ; ) ;
                 return first >= last;
             }
-        }
         return false;
     }
 
     // an alternative to the new version of equals, should
     // detect inequality faster (in many cases), but is slow
     // in the case of equal values (all bytes visited), due to
     // using n+=2, n-=2 vs. ++n, --n while iterating over the array.
     public boolean sample_equals(Object other) {
         if (other == this) return true;
         if (other instanceof ByteList) {
             ByteList b = (ByteList) other;
             int first;
             int last;
             int size;
             byte[] buf;
             if ((size = realSize) == b.realSize) {
                 // scanning from front and back simultaneously, sampling odd
                 // bytes on the forward iteration and even bytes on the 
                 // reverse iteration. the object is to get a mismatch as quickly
                 // as possible. 
                 for (buf = bytes, first = -1, last = (size + 1) & ~1 ;
                     (last -= 2) >= 0 && buf[begin + last] == b.bytes[b.begin + last] &&
                     (first += 2) < size && buf[begin + first] == b.bytes[b.begin + first] ; ) ;
                 return last < 0 || first == size;
             }
         }
         return false;
     }
 
     /**
      * This comparison matches MRI comparison of Strings (rb_str_cmp).
      * I wish we had memcmp right now...
      */
     public int compareTo(Object other) {
         return cmp((ByteList)other);
     }
 
     public int cmp(final ByteList other) {
         if (other == this || bytes == other.bytes) return 0;
         final int size = realSize;
         final int len =  Math.min(size,other.realSize);
         int offset = -1;
         // a bit of VM/JIT weirdness here: though in most cases
         // performance is improved if array references are kept in
         // a local variable (saves an instruction per access, as I
         // [slightly] understand it), in some cases, when two (or more?) 
         // arrays are being accessed, the member reference is actually
         // faster.  this is one of those cases...
         for (  ; ++offset < len && bytes[begin + offset] == other.bytes[other.begin + offset]; ) ;
         if (offset < len) {
             return (bytes[begin + offset]&0xFF) > (other.bytes[other.begin + offset]&0xFF) ? 1 : -1;
         }
         return size == other.realSize ? 0 : size == len ? -1 : 1;
     }
 
    /**
      * Returns the internal byte array. This is unsafe unless you know what you're
      * doing. But it can improve performance for byte-array operations that
      * won't change the array.
      *
      * @return the internal byte array
      */
     public byte[] unsafeBytes() {
         return bytes;  
     }
 
     public byte[] bytes() {
         byte[] newBytes = new byte[realSize];
         System.arraycopy(bytes, begin, newBytes, 0, realSize);
         return newBytes;
     }
 
     public int begin() {
         return begin;
     }
 
     private void grow(int increaseRequested) {
         if (increaseRequested < 0) {
             return;
         }
         int newSize = realSize + increaseRequested;
         if (bytes.length < newSize) {
             byte[] newBytes = new byte[(int) (newSize * FACTOR)];
             System.arraycopy(bytes,0,newBytes,0,realSize);
             bytes = newBytes;
         }
     }
 
     public int hashCode() {
         if (validHash) return hash;
 
         int key = 0;
         int index = begin;
         final int end = begin + realSize; 
         while (index < end) {
             // equivalent of: key = key * 65599 + byte;
             key = ((key << 16) + (key << 6) - key) + (int)(bytes[index++]); // & 0xFF ? 
         }
         key = key + (key >> 5);
         validHash = true;
         return hash = key;
     }
 
     /**
      * Remembers toString value, which is expensive for StringBuffer.
      */    
     public String toString() {
         if (stringValue == null) stringValue = new String(plain(bytes, begin, realSize));
         return stringValue;
     }
 
     public static ByteList create(CharSequence s) {
         return new ByteList(plain(s),false);
     }
 
     public static byte[] plain(CharSequence s) {
         if(s instanceof String) {
             try {
                 return ((String)s).getBytes("ISO8859-1");
             } catch(Exception e) {
                 //FALLTHROUGH
             }
         }
         byte[] bytes = new byte[s.length()];
         for (int i = 0; i < bytes.length; i++) {
             bytes[i] = (byte) s.charAt(i);
         }
         return bytes;
     }
 
     public static byte[] plain(char[] s) {
         byte[] bytes = new byte[s.length];
         for (int i = 0; i < s.length; i++) {
             bytes[i] = (byte) s[i];
         }
         return bytes;
     }
 
     public static char[] plain(byte[] b, int start, int length) {
         char[] chars = new char[length];
         for (int i = 0; i < length; i++) {
             chars[i] = (char) (b[start + i] & 0xFF);
         }
         return chars;
     }
 
     public static char[] plain(byte[] b) {
         char[] chars = new char[b.length];
         for (int i = 0; i < b.length; i++) {
             chars[i] = (char) (b[i] & 0xFF);
         }
         return chars;
     }
 
     public char charAt(int ix) {
         return (char)(this.bytes[begin + ix] & 0xFF);
     }
 
     public CharSequence subSequence(int start, int end) {
         return new ByteList(this, start, end - start);
     }
 }
diff --git a/test/test_hash.rb b/test/test_hash.rb
index fba0982468..601fbf5fe4 100644
--- a/test/test_hash.rb
+++ b/test/test_hash.rb
@@ -1,9 +1,59 @@
 require 'test/unit'
 
 class TestHash < Test::Unit::TestCase
   def test_clone_copies_default_initializer
     hash = Hash.new { |h, k| h[k] = 0 }
     clone = hash.clone
     assert_equal 0, clone[:test]
   end
+
+  class Eql
+    def initialize(hash); @hash=hash; end
+    def eql?(other); true; end
+    def hash; @hash; end
+  end
+
+  class Ee
+    def initialize(hash); @hash=hash; end
+    def ==(other); true; end
+    def hash; @hash; end
+  end
+
+  class Equal
+    def initialize(hash); @hash=hash; end
+    def equal?(other); true; end
+    def hash; @hash; end
+  end
+
+  def test_lookup_with_eql_and_same_hash_should_work
+    eql1 = Eql.new(5)
+    eql2 = Eql.new(5)
+
+    hash = {eql1 => "bar"}
+    assert_equal("bar", hash[eql1])
+    assert_equal("bar", hash[eql2])
+  end
+
+  def test_lookup_with_eql_and_different_hash_should_not_work
+    eql1 = Eql.new(5)
+    eql2 = Eql.new(6)
+
+    hash = {eql1 => "bar"}
+    assert_equal("bar", hash[eql1])
+    assert_nil(hash[eql2])
+  end
+
+  def test_lookup_with_ee_and_same_hash_should_not_work
+    ee1 = Ee.new(5)
+    ee2 = Ee.new(5)
+    hash = {ee1 => 'bar'}
+    assert_nil(hash[ee2])
+  end
+
+  def test_lookup_with_equal_and_same_hash_should_not_work
+    equal1 = Equal.new(5)
+    equal2 = Equal.new(5)
+    hash = {equal1 => 'bar'}
+    assert_nil(hash[equal2])
+  end
 end
