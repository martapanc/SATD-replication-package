diff --git a/lib/ruby/1.8/openssl/digest.rb b/lib/ruby/1.8/openssl/digest.rb
index 6f2c998ff6..ac7dd3c129 100644
--- a/lib/ruby/1.8/openssl/digest.rb
+++ b/lib/ruby/1.8/openssl/digest.rb
@@ -1,49 +1,48 @@
 =begin
 = $RCSfile: digest.rb,v $ -- Ruby-space predefined Digest subclasses
 
 = Info
   'OpenSSL for Ruby 2' project
   Copyright (C) 2002  Michal Rokos <m.rokos@sh.cvut.cz>
   All rights reserved.
 
 = Licence
   This program is licenced under the same licence as Ruby.
   (See the file 'LICENCE'.)
 
 = Version
   $Id: digest.rb,v 1.1.2.2 2006/06/20 11:18:15 gotoyuzo Exp $
 =end
 
 ##
 # Should we care what if somebody require this file directly?
 #require 'openssl'
 
 module OpenSSL
   module Digest
 
     alg = %w(DSS DSS1 MD2 MD4 MD5 MDC2 RIPEMD160 SHA SHA1)
     if OPENSSL_VERSION_NUMBER > 0x00908000
       alg += %w(SHA224 SHA256 SHA384 SHA512)
     end
-
     alg.each{|name|
       klass = Class.new(Digest){
         define_method(:initialize){|*data|
           if data.length > 1
             raise ArgumentError,
               "wrong number of arguments (#{data.length} for 1)"
           end
           super(name, data.first)
         }
       }
       singleton = (class <<klass; self; end)
       singleton.class_eval{
         define_method(:digest){|data| Digest.digest(name, data) }
         define_method(:hexdigest){|data| Digest.hexdigest(name, data) }
       }
       const_set(name, klass)
     }
 
   end # Digest
 end # OpenSSL
 
diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index c2c7a0f60a..ba43628b7a 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1418 +1,1422 @@
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
 
     /*
      * just allocates the internal array, with optional objectspace
      */
     private RubyArray(Ruby runtime, long length, boolean objectSpace) {
         super(runtime, runtime.getArray(), objectSpace);
         checkLength(length);
         alloc((int) length);
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
 
+    public IRubyObject[] toJavaArrayMaybeUnsafe() {
+        return (!shared && begin == 0 && values.length == realLength) ? values : toJavaArray();
+    }    
+
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
 
         if (ARRAY_DEFAULT_SIZE * 2 < values.length || shared) alloc(ARRAY_DEFAULT_SIZE * 2);
         
         begin = 0;
         shared = false;
         
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
             checkArgumentCount(args, 0, 2);
             item = null;
         	begObj = argc > 0 ? args[0] : null;
         	lenObj = argc > 1 ? args[1] : null;
         	argc++;
         } else {
             checkArgumentCount(args, 1, 3);
             item = args[0];
         	begObj = argc > 1 ? args[1] : null;
         	lenObj = argc > 2 ? args[2] : null;
         }
 
         long beg = 0, end = 0, len = 0;
diff --git a/src/org/jruby/RubyDigest.java b/src/org/jruby/RubyDigest.java
index 2c1c1a3841..c6f5b6aed7 100644
--- a/src/org/jruby/RubyDigest.java
+++ b/src/org/jruby/RubyDigest.java
@@ -1,235 +1,246 @@
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
  * Copyright (C) 2006, 2007 Ola Bini <ola@ologix.com>
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
 
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
+import java.security.NoSuchProviderException;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class RubyDigest {
     public static void createDigest(Ruby runtime) {
+        java.security.Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(),2);
+
         RubyModule mDigest = runtime.defineModule("Digest");
         RubyClass cDigestBase = mDigest.defineClassUnder("Base",runtime.getObject(), Base.BASE_ALLOCATOR);
 
         CallbackFactory basecb = runtime.callbackFactory(Base.class);
         
         cDigestBase.getMetaClass().defineFastMethod("new",basecb.getFastOptSingletonMethod("newInstance"));
         cDigestBase.getMetaClass().defineFastMethod("digest",basecb.getFastSingletonMethod("s_digest",RubyKernel.IRUBY_OBJECT));
         cDigestBase.getMetaClass().defineFastMethod("hexdigest",basecb.getFastSingletonMethod("s_hexdigest",RubyKernel.IRUBY_OBJECT));
 
         cDigestBase.defineMethod("initialize",basecb.getOptMethod("initialize"));
         cDigestBase.defineFastMethod("initialize_copy",basecb.getFastMethod("initialize_copy",RubyKernel.IRUBY_OBJECT));
         cDigestBase.defineFastMethod("clone",basecb.getFastMethod("rbClone"));
         cDigestBase.defineFastMethod("update",basecb.getFastMethod("update",RubyKernel.IRUBY_OBJECT));
         cDigestBase.defineFastMethod("<<",basecb.getFastMethod("update",RubyKernel.IRUBY_OBJECT));
         cDigestBase.defineFastMethod("digest",basecb.getFastMethod("digest"));
         cDigestBase.defineFastMethod("hexdigest",basecb.getFastMethod("hexdigest"));
         cDigestBase.defineFastMethod("to_s",basecb.getFastMethod("hexdigest"));
         cDigestBase.defineFastMethod("==",basecb.getFastMethod("eq",RubyKernel.IRUBY_OBJECT));
     }
 
     public static void createDigestMD5(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_MD5 = mDigest.defineClassUnder("MD5",cDigestBase,cDigestBase.getAllocator());
         cDigest_MD5.setClassVar("metadata",runtime.newString("MD5"));
     }
 
     public static void createDigestRMD160(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_RMD160 = mDigest.defineClassUnder("RMD160",cDigestBase,cDigestBase.getAllocator());
         cDigest_RMD160.setClassVar("metadata",runtime.newString("RIPEMD160"));
     }
 
     public static void createDigestSHA1(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_SHA1 = mDigest.defineClassUnder("SHA1",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA1.setClassVar("metadata",runtime.newString("SHA1"));
     }
 
     public static void createDigestSHA2(Ruby runtime) {
         try {
             MessageDigest.getInstance("SHA-256");
         } catch(NoSuchAlgorithmException e) {
             throw runtime.newLoadError("SHA2 not supported");
         }
         runtime.getLoadService().require("digest.so");
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_SHA2_256 = mDigest.defineClassUnder("SHA256",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_256.setClassVar("metadata",runtime.newString("SHA-256"));
         RubyClass cDigest_SHA2_384 = mDigest.defineClassUnder("SHA384",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_384.setClassVar("metadata",runtime.newString("SHA-384"));
         RubyClass cDigest_SHA2_512 = mDigest.defineClassUnder("SHA512",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_512.setClassVar("metadata",runtime.newString("SHA-512"));
     }
 
     public static class Base extends RubyObject {
         protected static ObjectAllocator BASE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new Base(runtime, klass);
             }
         };
         
         public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args) {
             if(recv == recv.getRuntime().getModule("Digest").getClass("Base")) {
                 throw recv.getRuntime().newNotImplementedError("Digest::Base is an abstract class");
             }
 
             if(!((RubyClass)recv).isClassVarDefined("metadata")) {
                 throw recv.getRuntime().newNotImplementedError("the " + recv + "() function is unimplemented on this machine");
             }
 
             RubyClass klass = (RubyClass)recv;
             
             Base result = (Base) klass.allocate();
             try {
                 result.setAlgorithm(((RubyClass)recv).getClassVar("metadata"));
             } catch(NoSuchAlgorithmException e) {
                 throw recv.getRuntime().newNotImplementedError("the " + recv + "() function is unimplemented on this machine");
+            } catch(NoSuchProviderException e) {
+                throw recv.getRuntime().newNotImplementedError("the " + recv + "() function is unimplemented on this machine");
             }
             result.callInit(args, Block.NULL_BLOCK);
             return result;
         }
         public static IRubyObject s_digest(IRubyObject recv, IRubyObject str) {
             String name = ((RubyClass)recv).getClassVar("metadata").toString();
             try {
-                MessageDigest md = MessageDigest.getInstance(name);
+                MessageDigest md = MessageDigest.getInstance(name,"BC");
                 return RubyString.newString(recv.getRuntime(), md.digest(str.convertToString().getBytes()));
             } catch(NoSuchAlgorithmException e) {
                 throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
+            } catch(NoSuchProviderException e) {
+                throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
             }
         }
         public static IRubyObject s_hexdigest(IRubyObject recv, IRubyObject str) {
             String name = ((RubyClass)recv).getClassVar("metadata").toString();
             try {
-                MessageDigest md = MessageDigest.getInstance(name);
+                MessageDigest md = MessageDigest.getInstance(name,"BC");
                 return RubyString.newString(recv.getRuntime(), ByteList.plain(toHex(md.digest(str.convertToString().getBytes()))));
             } catch(NoSuchAlgorithmException e) {
                 throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
+            } catch(NoSuchProviderException e) {
+                throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
             }
         }
 
         private MessageDigest algo;
         private StringBuffer data;
 
         public Base(Ruby runtime, RubyClass type) {
             super(runtime,type);
             data = new StringBuffer();
         }
         
         public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
             if(args.length > 0 && !args[0].isNil()) {
                 update(args[0]);
             }
             return this;
         }
 
         public IRubyObject initialize_copy(IRubyObject obj, Block unusedBlock) {
             if(this == obj) {
                 return this;
             }
             ((RubyObject)obj).checkFrozen();
 
             data = new StringBuffer(((Base)obj).data.toString());
             String name = ((Base)obj).algo.getAlgorithm();
             try {
-                algo = MessageDigest.getInstance(name);
+                algo = MessageDigest.getInstance(name,"BC");
             } catch(NoSuchAlgorithmException e) {
                 throw getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
+            } catch(NoSuchProviderException e) {
+                throw getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
             }
             return this;
         }
 
         public IRubyObject update(IRubyObject obj) {
             data.append(obj);
             algo.update(obj.convertToString().getBytes());
             return this;
         }
 
         public IRubyObject digest() {
             algo.reset();
             return RubyString.newString(getRuntime(), algo.digest(ByteList.plain(data)));
         }
 
         public IRubyObject hexdigest() {
             algo.reset();
             return RubyString.newString(getRuntime(), ByteList.plain(toHex(algo.digest(ByteList.plain(data)))));
         }
 
         public IRubyObject eq(IRubyObject oth) {
             boolean ret = this == oth;
             if(!ret && oth instanceof Base) {
                 Base b = (Base)oth;
                 ret = this.algo.getAlgorithm().equals(b.algo.getAlgorithm()) &&
                     this.digest().equals(b.digest());
             }
 
             return ret ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         public IRubyObject rbClone() {
             IRubyObject clone = new Base(getRuntime(),getMetaClass().getRealClass());
             clone.setMetaClass(getMetaClass().getSingletonClassClone());
             clone.setTaint(this.isTaint());
             clone.initCopy(this);
             clone.setFrozen(isFrozen());
             return clone;
         }
 
-        private void setAlgorithm(IRubyObject algo) throws NoSuchAlgorithmException {
-            this.algo = MessageDigest.getInstance(algo.toString());
+        private void setAlgorithm(IRubyObject algo) throws NoSuchAlgorithmException, NoSuchProviderException {
+            this.algo = MessageDigest.getInstance(algo.toString(),"BC");
         }
 
         private static String toHex(byte[] val) {
             StringBuffer out = new StringBuffer();
             for(int i=0,j=val.length;i<j;i++) {
                 String ve = Integer.toString((((int)((char)val[i])) & 0xFF),16);
                 if(ve.length() == 1) {
                     ve = "0" + ve;
                 }
                 out.append(ve);
             }
             return out.toString();
         }
     }
 }// RubyDigest
diff --git a/src/org/jruby/RubyDir.java b/src/org/jruby/RubyDir.java
index 34f4cca355..9d69a80fe3 100644
--- a/src/org/jruby/RubyDir.java
+++ b/src/org/jruby/RubyDir.java
@@ -1,458 +1,457 @@
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
 
 import java.io.File;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.Glob;
 import org.jruby.util.JRubyFile;
 
 /**
  * .The Ruby built-in class Dir.
  *
  * @author  jvoegele
  */
 public class RubyDir extends RubyObject {
 	// What we passed to the constructor for method 'path'
     private RubyString    path;
     protected JRubyFile      dir;
     private   String[]  snapshot;   // snapshot of contents of directory
     private   int       pos;        // current position in directory
     private boolean isOpen = true;
 
     public RubyDir(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     private static ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyDir(runtime, klass);
         }
     };
 
     public static RubyClass createDirClass(Ruby runtime) {
         RubyClass dirClass = runtime.defineClass("Dir", runtime.getObject(), DIR_ALLOCATOR);
 
         dirClass.includeModule(runtime.getModule("Enumerable"));
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyDir.class);
 
         dirClass.getMetaClass().defineMethod("glob", callbackFactory.getSingletonMethod("glob", RubyString.class));
         dirClass.getMetaClass().defineFastMethod("entries", callbackFactory.getFastSingletonMethod("entries", RubyString.class));
         dirClass.getMetaClass().defineMethod("[]", callbackFactory.getSingletonMethod("glob", RubyString.class));
         // dirClass.defineAlias("[]", "glob");
         dirClass.getMetaClass().defineMethod("chdir", callbackFactory.getOptSingletonMethod("chdir"));
         dirClass.getMetaClass().defineFastMethod("chroot", callbackFactory.getFastSingletonMethod("chroot", RubyString.class));
         //dirClass.defineSingletonMethod("delete", callbackFactory.getSingletonMethod(RubyDir.class, "delete", RubyString.class));
         dirClass.getMetaClass().defineMethod("foreach", callbackFactory.getSingletonMethod("foreach", RubyString.class));
         dirClass.getMetaClass().defineFastMethod("getwd", callbackFactory.getFastSingletonMethod("getwd"));
         dirClass.getMetaClass().defineFastMethod("pwd", callbackFactory.getFastSingletonMethod("getwd"));
         // dirClass.defineAlias("pwd", "getwd");
         dirClass.getMetaClass().defineFastMethod("mkdir", callbackFactory.getFastOptSingletonMethod("mkdir"));
         dirClass.getMetaClass().defineMethod("open", callbackFactory.getSingletonMethod("open", RubyString.class));
         dirClass.getMetaClass().defineFastMethod("rmdir", callbackFactory.getFastSingletonMethod("rmdir", RubyString.class));
         dirClass.getMetaClass().defineFastMethod("unlink", callbackFactory.getFastSingletonMethod("rmdir", RubyString.class));
         dirClass.getMetaClass().defineFastMethod("delete", callbackFactory.getFastSingletonMethod("rmdir", RubyString.class));
         // dirClass.defineAlias("unlink", "rmdir");
         // dirClass.defineAlias("delete", "rmdir");
 
         dirClass.defineFastMethod("close", callbackFactory.getFastMethod("close"));
         dirClass.defineMethod("each", callbackFactory.getMethod("each"));
         dirClass.defineFastMethod("entries", callbackFactory.getFastMethod("entries"));
         dirClass.defineFastMethod("path", callbackFactory.getFastMethod("path"));
         dirClass.defineFastMethod("tell", callbackFactory.getFastMethod("tell"));
         dirClass.defineAlias("pos", "tell");
         dirClass.defineFastMethod("seek", callbackFactory.getFastMethod("seek", RubyFixnum.class));
         dirClass.defineFastMethod("pos=", callbackFactory.getFastMethod("setPos", RubyFixnum.class));
         dirClass.defineFastMethod("read", callbackFactory.getFastMethod("read"));
         dirClass.defineFastMethod("rewind", callbackFactory.getFastMethod("rewind"));
         dirClass.defineMethod("initialize", callbackFactory.getMethod("initialize", RubyString.class));
 
         return dirClass;
     }
 
     /**
      * Creates a new <code>Dir</code>.  This method takes a snapshot of the
      * contents of the directory at creation time, so changes to the contents
      * of the directory will not be reflected during the lifetime of the
      * <code>Dir</code> object returned, so a new <code>Dir</code> instance
      * must be created to reflect changes to the underlying file system.
      */
     public IRubyObject initialize(RubyString newPath, Block unusedBlock) {
         newPath.checkSafeString();
-
         dir = JRubyFile.create(getRuntime().getCurrentDirectory(),newPath.toString());
         if (!dir.isDirectory()) {
             dir = null;
             throw getRuntime().newErrnoENOENTError(newPath.toString() + " is not a directory");
         }
         path = newPath;
 		List snapshotList = new ArrayList();
 		snapshotList.add(".");
 		snapshotList.add("..");
 		snapshotList.addAll(getContents(dir));
 		snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
 		pos = 0;
 
         return this;
     }
 
 // ----- Ruby Class Methods ----------------------------------------------------
 
     /**
      * Returns an array of filenames matching the specified wildcard pattern
      * <code>pat</code>. If a block is given, the array is iterated internally
      * with each filename is passed to the block in turn. In this case, Nil is
      * returned.  
      */
     public static IRubyObject glob(IRubyObject recv, RubyString pat, Block block) {
         String pattern = pat.toString();
         String[] files = new Glob(recv.getRuntime().getCurrentDirectory(), pattern).getNames();
         if (block.isGiven()) {
             ThreadContext context = recv.getRuntime().getCurrentContext();
             
             for (int i = 0; i < files.length; i++) {
                 context.yield(JavaUtil.convertJavaToRuby(recv.getRuntime(), files[i]), block);
             }
             return recv.getRuntime().getNil();
         }            
         return recv.getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(recv.getRuntime(), files));
     }
 
     /**
      * @return all entries for this Dir
      */
     public RubyArray entries() {
         return getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(getRuntime(), snapshot));
     }
     
     /**
      * Returns an array containing all of the filenames in the given directory.
      */
     public static RubyArray entries(IRubyObject recv, RubyString path) {
         final JRubyFile directory = JRubyFile.create(recv.getRuntime().getCurrentDirectory(),path.toString());
         
         if (!directory.isDirectory()) {
             throw recv.getRuntime().newErrnoENOENTError("No such directory");
         }
         List fileList = getContents(directory);
 		fileList.add(0,".");
 		fileList.add(1,"..");
         Object[] files = fileList.toArray();
         return recv.getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(recv.getRuntime(), files));
     }
 
     /** Changes the current directory to <code>path</code> */
     public static IRubyObject chdir(IRubyObject recv, IRubyObject[] args, Block block) {
         recv.checkArgumentCount(args, 0, 1);
         RubyString path = args.length == 1 ? 
             (RubyString) args[0].convertToString() : getHomeDirectoryPath(recv); 
         JRubyFile dir = getDir(recv.getRuntime(), path.toString(), true);
         String realPath = null;
         String oldCwd = recv.getRuntime().getCurrentDirectory();
         
         // We get canonical path to try and flatten the path out.
         // a dir '/subdir/..' should return as '/'
         // cnutter: Do we want to flatten path out?
         try {
             realPath = dir.getCanonicalPath();
         } catch (IOException e) {
             realPath = dir.getAbsolutePath();
         }
         
         IRubyObject result = null;
         if (block.isGiven()) {
         	// FIXME: Don't allow multiple threads to do this at once
             recv.getRuntime().setCurrentDirectory(realPath);
             try {
                 result = recv.getRuntime().getCurrentContext().yield(path, block);
             } finally {
                 recv.getRuntime().setCurrentDirectory(oldCwd);
             }
         } else {
         	recv.getRuntime().setCurrentDirectory(realPath);
         	result = recv.getRuntime().newFixnum(0);
         }
         
         return result;
     }
 
     /**
      * Changes the root directory (only allowed by super user).  Not available
      * on all platforms.
      */
     public static IRubyObject chroot(IRubyObject recv, RubyString path) {
         throw recv.getRuntime().newNotImplementedError("chroot not implemented: chroot is non-portable and is not supported.");
     }
 
     /**
      * Deletes the directory specified by <code>path</code>.  The directory must
      * be empty.
      */
     public static IRubyObject rmdir(IRubyObject recv, RubyString path) {
         JRubyFile directory = getDir(recv.getRuntime(), path.toString(), true);
         
         if (!directory.delete()) {
             throw recv.getRuntime().newSystemCallError("No such directory");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     /**
      * Executes the block once for each file in the directory specified by
      * <code>path</code>.
      */
     public static IRubyObject foreach(IRubyObject recv, RubyString path, Block block) {
         path.checkSafeString();
 
         RubyClass dirClass = recv.getRuntime().getClass("Dir");
         RubyDir dir = (RubyDir) dirClass.newInstance(new IRubyObject[] { path }, block);
         
         dir.each(block);
         return recv.getRuntime().getNil();
     }
 
     /** Returns the current directory. */
     public static RubyString getwd(IRubyObject recv) {
         return recv.getRuntime().newString(recv.getRuntime().getCurrentDirectory());
     }
 
     /**
      * Creates the directory specified by <code>path</code>.  Note that the
      * <code>mode</code> parameter is provided only to support existing Ruby
      * code, and is ignored.
      */
     public static IRubyObject mkdir(IRubyObject recv, IRubyObject[] args) {
         if (args.length < 1) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
         if (args.length > 2) {
             throw recv.getRuntime().newArgumentError(args.length, 2);
         }
 
         args[0].checkSafeString();
         String path = args[0].toString();
 
         File newDir = getDir(recv.getRuntime(), path, false);
         if (File.separatorChar == '\\') {
             newDir = new File(newDir.getPath());
         }
         
         return newDir.mkdirs() ? RubyFixnum.zero(recv.getRuntime()) :
             RubyFixnum.one(recv.getRuntime());
     }
 
     /**
      * Returns a new directory object for <code>path</code>.  If a block is
      * provided, a new directory object is passed to the block, which closes the
      * directory object before terminating.
      */
     public static IRubyObject open(IRubyObject recv, RubyString path, Block block) {
         RubyDir directory = 
             (RubyDir) recv.getRuntime().getClass("Dir").newInstance(
                     new IRubyObject[] { path }, Block.NULL_BLOCK);
 
         if (!block.isGiven()) return directory;
         
         try {
             recv.getRuntime().getCurrentContext().yield(directory, block);
         } finally {
             directory.close();
         }
             
         return recv.getRuntime().getNil();
     }
 
 // ----- Ruby Instance Methods -------------------------------------------------
 
     /**
      * Closes the directory stream.
      */
     public IRubyObject close() {
         // Make sure any read()s after close fail.
         isOpen = false;
 
         return getRuntime().getNil();
     }
 
     /**
      * Executes the block once for each entry in the directory.
      */
     public IRubyObject each(Block block) {
         String[] contents = snapshot;
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i=0; i<contents.length; i++) {
             context.yield(getRuntime().newString(contents[i]), block);
         }
         return this;
     }
 
     /**
      * Returns the current position in the directory.
      */
     public RubyInteger tell() {
         return getRuntime().newFixnum(pos);
     }
 
     /**
      * Moves to a position <code>d</code>.  <code>pos</code> must be a value
      * returned by <code>tell</code> or 0.
      */
     public IRubyObject seek(RubyFixnum newPos) {
         setPos(newPos);
         return this;
     }
     
     public IRubyObject setPos(RubyFixnum newPos) {
         this.pos = (int) newPos.getLongValue();
         return newPos;
     }
 
     public IRubyObject path() {
         if (!isOpen) {
             throw getRuntime().newIOError("closed directory");
         }
         
         return path;
     }
 
     /** Returns the next entry from this directory. */
     public IRubyObject read() {
 	if (!isOpen) {
 	    throw getRuntime().newIOError("Directory already closed");
 	}
 
         if (pos >= snapshot.length) {
             return getRuntime().getNil();
         }
         RubyString result = getRuntime().newString(snapshot[pos]);
         pos++;
         return result;
     }
 
     /** Moves position in this directory to the first entry. */
     public IRubyObject rewind() {
         pos = 0;
         return getRuntime().newFixnum(pos);
     }
 
 // ----- Helper Methods --------------------------------------------------------
 
     /** Returns a Java <code>File</code> object for the specified path.  If
      * <code>path</code> is not a directory, throws <code>IOError</code>.
      *
      * @param   path path for which to return the <code>File</code> object.
      * @param   mustExist is true the directory must exist.  If false it must not.
      * @throws  IOError if <code>path</code> is not a directory.
      */
     protected static JRubyFile getDir(final Ruby runtime, final String path, final boolean mustExist) {
         JRubyFile result = JRubyFile.create(runtime.getCurrentDirectory(),path);
         boolean isDirectory = result.isDirectory();
         
         if (mustExist && !isDirectory) {
             throw runtime.newErrnoENOENTError(path + " is not a directory");
         } else if (!mustExist && isDirectory) {
             throw runtime.newErrnoEEXISTError("File exists - " + path); 
         }
 
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Java Strings.
      */
     protected static List getContents(File directory) {
         String[] contents = directory.list();
         List result = new ArrayList();
 
         // If an IO exception occurs (something odd, but possible)
         // A directory may return null.
         if (contents != null) {
             for (int i=0; i<contents.length; i++) {
                 result.add(contents[i]);
             }
         }
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Ruby Strings.
      */
     protected static List getContents(File directory, Ruby runtime) {
         List result = new ArrayList();
         String[] contents = directory.list();
         
         for (int i = 0; i < contents.length; i++) {
             result.add(runtime.newString(contents[i]));
         }
         return result;
     }
 	
 	/*
 	 * Poor mans find home directory.  I am not sure how windows ruby behaves with '~foo', but
 	 * this mostly will work on any unix/linux/cygwin system.  When someone wants to extend this
 	 * to include the windows way, we should consider moving this to an external ruby file.
 	 */
 	public static IRubyObject getHomeDirectoryPath(IRubyObject recv, String user) {
 		// TODO: Having a return where I set user inside readlines created a JumpException.  It seems that
 		// evalScript should catch that and return?
 		return recv.getRuntime().evalScript("File.open('/etc/passwd') do |f| f.readlines.each do" +
 				"|l| f = l.split(':'); return f[5] if f[0] == '" + user + "'; end; end; nil");
 	}
 	
 	public static RubyString getHomeDirectoryPath(IRubyObject recv) {
 		RubyHash hash = (RubyHash) recv.getRuntime().getObject().getConstant("ENV_JAVA");
 		IRubyObject home = hash.aref(recv.getRuntime().newString("user.home"));
 		
 		if (home == null || home.isNil()) {
 			home = hash.aref(recv.getRuntime().newString("LOGDIR"));
 		}
 		
 		if (home == null || home.isNil()) {
 			throw recv.getRuntime().newArgumentError("user.home/LOGDIR not set");
 		}
 		
 		return (RubyString) home;
 	}
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 9701d16dc1..91841b1754 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1673 +1,1673 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.SinglyLinkedList;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyModule extends RubyObject {
     private static final String CVAR_TAINT_ERROR =
         "Insecure: can't modify class variable";
     private static final String CVAR_FREEZE_ERROR = "class/module";
 
     // superClass may be null.
     private RubyClass superClass;
 
     public int index;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     //public RubyModule parentModule;
 
     // CRef...to eventually replace parentModule
     public SinglyLinkedList cref;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     private String classId;
 
     // All methods and all CACHED methods for the module.  The cached methods will be removed
     // when appropriate (e.g. when method is removed by source class or a new method is added
     // with same name by one of its subclasses).
     private Map methods = new HashMap();
 
     protected RubyModule(Ruby runtime, RubyClass metaClass, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass);
 
         this.superClass = superClass;
 
         setBaseName(name);
 
         // If no parent is passed in, it is safe to assume Object.
         if (parentCRef == null) {
             if (runtime.getObject() != null) {
                 parentCRef = runtime.getObject().getCRef();
             }
         }
         this.cref = new SinglyLinkedList(this, parentCRef);
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         if (cref.getNext() == null) {
             return null;
         }
 
         return (RubyModule)cref.getNext().getValue();
     }
 
     public void setParent(RubyModule p) {
         cref.setNext(p.getCRef());
     }
 
     public Map getMethods() {
         return methods;
     }
 
     public boolean isModule() {
         return true;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
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
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             }
         }
 
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             result.insert(0, "::").insert(0, p.getBaseName());
         }
 
         return result.toString();
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
 
     /**
      * Search this and parent modules for the named variable.
      * 
      * @param name The variable to search for
      * @return The module in which that variable is found, or null if not found
      */
     private RubyModule getModuleWithInstanceVar(String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getInstanceVariable(name) != null) {
                 return p;
             }
         }
         return null;
     }
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module == null) {
             module = this;
         }
 
         return module.setInstanceVariable(name, value, CVAR_TAINT_ERROR, CVAR_FREEZE_ERROR);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module != null) {
             IRubyObject variable = module.getInstanceVariable(name);
 
             return variable == null ? getRuntime().getNil() : variable;
         }
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
     /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         return getModuleWithInstanceVar(name) != null;
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      * 
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      * @see RubyObject#setInstanceVariable(String, IRubyObject, String, String)
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
         if(getConstantAt(name) != null) {
             getRuntime().getWarnings().warn("already initialized constant " + name);
         }
 
         IRubyObject result = setInstanceVariable(name, value, "Insecure: can't set constant",
                 "class/module");
 
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
             /*
             module.setParent(this);
             */
         }
         return result;
     }
 
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module = getConstantAt(name);
 
         return  (module instanceof RubyClass) ? (RubyClass) module : null;
     }
 
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
      * @param name The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     public IRubyObject const_missing(IRubyObject name, Block block) {
         /* Uninitialized constant */
         if (this != getRuntime().getObject()) {
             throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asSymbol(), "" + getName() + "::" + name.asSymbol());
         }
 
         throw getRuntime().newNameError("uninitialized constant " + name.asSymbol(), name.asSymbol());
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         RubyModule p, c;
         boolean changed = false;
         boolean skip = false;
 
         c = this;
         while (module != null) {
             if (getNonIncludedClass() == module.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
 
             boolean superclassSeen = false;
             for (p = getSuperClass(); p != null; p = p.getSuperClass()) {
                 if (p instanceof IncludedModuleWrapper) {
                     if (p.getNonIncludedClass() == module.getNonIncludedClass()) {
                         if (!superclassSeen) {
                             c = p;
                         }
                         skip = true;
                         break;
                     }
                 } else {
                     superclassSeen = true;
                 }
             }
             if (!skip) {
                 // In the current logic, if we get here we know that module is not an 
                 // IncludedModuleWrapper, so there's no need to fish out the delegate. But just 
                 // in case the logic should change later, let's do it anyway:
                 c.setSuperClass(new IncludedModuleWrapper(getRuntime(), c.getSuperClass(),
                         module.getNonIncludedClass()));
                 c = c.getSuperClass();
                 changed = true;
             }
 
             module = module.getSuperClass();
             skip = false;
         }
 
         if (changed) {
             // MRI seems to blow away its cache completely after an include; is
             // what we're doing here really safe?
             List methodNames = new ArrayList(((RubyModule) arg).getMethods().keySet());
             for (Iterator iter = methodNames.iterator();
                  iter.hasNext();) {
                 String methodName = (String) iter.next();
                 getRuntime().getCacheMap().remove(methodName, searchMethod(methodName));
             }
         }
 
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(String name) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn("undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = getInstanceVariable("__attached__");
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(runtime.getCurrentContext(), "singleton_method_undefined", getRuntime().newSymbol(name));
         }else{
             callMethod(runtime.getCurrentContext(), "method_undefined", getRuntime().newSymbol(name));
     }
     }
 
     private void addCachedMethod(String name, DynamicMethod method) {
         // Included modules modify the original 'included' modules class.  Since multiple
         // classes can include the same module, we cannot cache in the original included module.
         if (!isIncluded()) {
             getMethods().put(name, method);
             getRuntime().getCacheMap().add(method, this);
         }
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             DynamicMethod existingMethod = (DynamicMethod) getMethods().remove(name);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(name, existingMethod);
             }
 
             getMethods().put(name, method);
         }
     }
 
     public void removeCachedMethod(String name) {
         getMethods().remove(name);
     }
 
     public void removeMethod(String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             getRuntime().getCacheMap().remove(name, method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(getRuntime().getCurrentContext(), "singleton_method_removed", getRuntime().newSymbol(name));
         }else{
             callMethod(getRuntime().getCurrentContext(), "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 if (method != null) {
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
 
                     return method;
                 }
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     private IRubyObject getConstantInner(String name, boolean exclude) {
         IRubyObject objectClass = getRuntime().getObject();
         boolean retryForModule = false;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 IRubyObject constant = p.getConstantAt(name);
 
                 if (constant == null) {
                     if (getRuntime().getLoadService().autoload(p.getName() + "::" + name) != null) {
                         continue;
                     }
                 }
                 if (constant != null) {
                     if (exclude && p == objectClass && this != objectClass) {
                         getRuntime().getWarnings().warn("toplevel constant " + name +
                                 " referenced by " + getName() + "::" + name);
                     }
 
                     return constant;
                 }
                 p = p.getSuperClass();
             }
 
             if (!exclude && !retryForModule && getClass().equals(RubyModule.class)) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(), "const_missing", RubySymbol.newSymbol(getRuntime(), name));
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstantInner(name, false);
     }
 
     public IRubyObject getConstantFrom(String name) {
         return getConstantInner(name, true);
     }
 
     public IRubyObject getConstantAt(String name) {
         return getInstanceVariable(name);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         getRuntime().getCacheMap().remove(name, searchMethod(name));
         getMethods().put(name, new AliasMethod(method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
         IRubyObject type = getConstantAt(name);
         ObjectAllocator allocator = superClazz == null ? getRuntime().getObject().getAllocator() : superClazz.getAllocator();
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         } 
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (superClazz != null && ((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newTypeError("superclass mismatch for class " + name);
         }
 
         return (RubyClass) type;
     }
 
     /** rb_define_class_under
      *
      */
     public RubyClass defineClassUnder(String name, RubyClass superClazz, ObjectAllocator allocator) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         }
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newNameError(name + " is already defined.", name);
         }
 
         return (RubyClass) type;
     }
 
     public RubyModule defineModuleUnder(String name) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineModuleUnder(name, cref);
         }
 
         if (!(type instanceof RubyModule)) {
             throw getRuntime().newTypeError(name + " is not a module.");
         }
 
         return (RubyModule) type;
     }
 
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (this == getRuntime().getClass("Class")) {
             getRuntime().secure(4);
         }
 
         if (!IdUtil.isConstant(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         if (!IdUtil.isClassVariable(name.asSymbol())) {
             throw getRuntime().newNameError("wrong class variable name " + name.asSymbol(), name.asSymbol());
         }
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject value = removeInstanceVariable(name.asSymbol());
 
         if (value != null) {
             return value;
         }
 
         if (isClassVarDefined(name.asSymbol())) {
             throw cannotRemoveError(name.asSymbol());
         }
 
         throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName(), name.asSymbol());
     }
 
     private void addAccessor(String name, boolean readable, boolean writeable) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = tc.getCurrentVisibility();
         if (attributeScope.isPrivate()) {
             //FIXME warning
         } else if (attributeScope.isModuleFunction()) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = "@" + name;
         final Ruby runtime = getRuntime();
         ThreadContext context = getRuntime().getCurrentContext();
         if (readable) {
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     checkArgumentCount(args, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
         if (writeable) {
             name = name + "=";
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     IRubyObject[] fargs = runtime.getCurrentContext().getFrameArgs();
 
                     if (fargs.length != 1) {
                         throw runtime.newArgumentError("wrong # of arguments(" + fargs.length + "for 1)");
                     }
 
                     return self.setInstanceVariable(variableName, fargs[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asSymbol(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 addMethod(name, new FullFunctionCallbackMethod(this, new Callback() {
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         ThreadContext tc = self.getRuntime().getCurrentContext();
                         return tc.callSuper(tc.getFrameArgs(), block);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 }, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility().isPrivate());
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(method.getImplementationClass(), name, this, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(method.getImplementationClass(), name, this, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
-    // What is argument 1 for in this method?
+    // What is argument 1 for in this method? A Method or Proc object /OB
     public IRubyObject define_method(IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asSymbol();
         DynamicMethod newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility.isModuleFunction()) visibility = Visibility.PRIVATE;
 
         if (args.length == 1 || args[1].isKindOf(getRuntime().getClass("Proc"))) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (args.length == 1) ? getRuntime().newProc(false, block) : (RubyProc)args[1];
             body = proc;
 
             proc.getBlock().isLambda = true;
             proc.getBlock().getFrame().setLastClass(this);
             proc.getBlock().getFrame().setLastFunc(name);
 
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args[1].isKindOf(getRuntime().getClass("Method"))) {
             RubyMethod method = (RubyMethod)args[1];
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(null), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (tc.getPreviousVisibility().isModuleFunction()) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(context, "singleton_method_added", symbol);
         }else{
         callMethod(context, "method_added", symbol);
         }
 
         return body;
     }
 
     public IRubyObject executeUnder(Callback method, IRubyObject[] args, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         context.preExecuteUnder(this, block);
 
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     // Methods of the Module Class (rb_mod_*):
 
     public static RubyModule newModule(Ruby runtime, String name) {
         return newModule(runtime, runtime.getClass("Module"), name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name) {
         return newModule(runtime, type, name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, String name, SinglyLinkedList parentCRef) {
         return newModule(runtime, runtime.getClass("Module"), name, parentCRef);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name, SinglyLinkedList parentCRef) {
         RubyModule module = new RubyModule(runtime, type, null, parentCRef, name);
         
         return module;
     }
 
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     /** rb_mod_class_variables
      *
      */
     public RubyArray class_variables() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             for (Iterator iter = p.instanceVariableNames(); iter.hasNext();) {
                 String id = (String) iter.next();
                 if (IdUtil.isClassVariable(id)) {
                     RubyString kval = getRuntime().newString(id);
                     if (!ary.includes(kval)) {
                         ary.append(kval);
                     }
                 }
             }
         }
         return ary;
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             DynamicMethod method = (DynamicMethod) entry.getValue();
 
             // Do not clone cached methods
             if (method.getImplementationClass() == realType) {
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = (DynamicMethod)method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.getMethods().put(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     protected IRubyObject doClone() {
         return RubyModule.newModule(getRuntime(), null, cref.getNext());
     }
 
     /** rb_mod_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original) {
         assert original instanceof RubyModule;
         
         RubyModule originalModule = (RubyModule)original;
         
         super.initialize_copy(originalModule);
         
         if (!getMetaClass().isSingleton()) {
             setMetaClass(originalModule.getSingletonClassClone());
         }
 
         setSuperClass(originalModule.getSuperClass());
         
         if (originalModule.instanceVariables != null){
             setInstanceVariables(new HashMap(originalModule.getInstanceVariables()));
         }
         
         // no __classpath__ and __classid__ stuff in JRuby here (yet?)        
 
         originalModule.cloneMethods(this);
         
         return this;        
     }
 
     /** rb_mod_included_modules
      *
      */
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List getAncestorList() {
         ArrayList list = new ArrayList();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     /** rb_mod_to_s
      *
      */
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = getInstanceVariable("__attached__");
             StringBuffer buffer = new StringBuffer("#<Class:");
             if(attached instanceof RubyClass || attached instanceof RubyModule){
                 buffer.append(attached.inspect());
             }else{
                 buffer.append(attached.anyToString());
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(obj.isKindOf(this));
     }
 
     /** rb_mod_le
     *
     */
    public IRubyObject op_le(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        if (isKindOfModule((RubyModule)obj)) {
            return getRuntime().getTrue();
        } else if (((RubyModule)obj).isKindOfModule(this)) {
            return getRuntime().getFalse();
        }
 
        return getRuntime().getNil();
    }
 
    /** rb_mod_lt
     *
     */
    public IRubyObject op_lt(IRubyObject obj) {
     return obj == this ? getRuntime().getFalse() : op_le(obj);
    }
 
    /** rb_mod_ge
     *
     */
    public IRubyObject op_ge(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        return ((RubyModule) obj).op_le(this);
    }
 
    /** rb_mod_gt
     *
     */
    public IRubyObject op_gt(IRubyObject obj) {
        return this == obj ? getRuntime().getFalse() : op_ge(obj);
    }
 
    /** rb_mod_cmp
     *
     */
    public IRubyObject op_cmp(IRubyObject obj) {
        if (this == obj) {
            return getRuntime().newFixnum(0);
        }
 
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError(
                "<=> requires Class or Module (" + getMetaClass().getName() + " given)");
        }
 
        RubyModule module = (RubyModule)obj;
 
        if (module.isKindOfModule(this)) {
            return getRuntime().newFixnum(1);
        } else if (this.isKindOfModule(module)) {
            return getRuntime().newFixnum(-1);
        }
 
        return getRuntime().getNil();
    }
 
    public boolean isKindOfModule(RubyModule type) {
        for (RubyModule p = this; p != null; p = p.getSuperClass()) {
            if (p.isSame(type)) {
                return true;
            }
        }
 
        return false;
    }
 
    public boolean isSame(RubyModule module) {
        return this == module;
    }
 
     /** rb_mod_initialize
      *
      */
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (block.isGiven()) block.yield(getRuntime().getCurrentContext(), null, this, this, false);
         
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr
      *
      */
     public IRubyObject attr(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         addAccessor(args[0].asSymbol(), true, writeable);
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_reader
      *
      */
     public IRubyObject attr_reader(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, false);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     public IRubyObject attr_writer(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), false, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_attr_accessor
      *
      */
     public IRubyObject attr_accessor(IRubyObject[] args) {
         for (int i = 0; i < args.length; i++) {
             addAccessor(args[i].asSymbol(), true, true);
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_const_get
      *
      */
     public IRubyObject const_get(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return getConstant(name);
     }
 
     /** rb_mod_const_set
      *
      */
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return setConstant(name, value);
     }
 
     /** rb_mod_const_defined
      *
      */
     public RubyBoolean const_defined(IRubyObject symbol) {
         String name = symbol.asSymbol();
 
         if (!IdUtil.isConstant(name)) {
             throw wrongConstantNameError(name);
         }
 
         return getRuntime().newBoolean(getConstantAt(name) != null);
     }
 
     private RaiseException wrongConstantNameError(String name) {
         return getRuntime().newNameError("wrong constant name " + name, name);
     }
 
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         RubyArray ary = getRuntime().newArray();
         HashMap undefinedMethods = new HashMap();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
                 String methodName = (String) entry.getKey();
 
                 if (method.isUndefined()) {
                     undefinedMethods.put(methodName, Boolean.TRUE);
                     continue;
                 }
                 if (method.getImplementationClass() == realType &&
                     method.getVisibility().is(visibility) && undefinedMethods.get(methodName) == null) {
                     RubyString name = getRuntime().newString(methodName);
 
                     if (!ary.includes(name)) {
                         ary.append(name);
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
 
         return ary;
     }
 
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC_PROTECTED);
     }
 
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PUBLIC);
     }
 
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asSymbol(), false);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PROTECTED);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, Visibility.PRIVATE);
     }
 
     /** rb_mod_constants
      *
      */
     public RubyArray constants() {
         ArrayList constantNames = new ArrayList();
         RubyModule objectClass = getRuntime().getObject();
 
         if (getRuntime().getClass("Module") == this) {
             for (Iterator vars = objectClass.instanceVariableNames();
                  vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
 
             return getRuntime().newArray(constantNames);
         } else if (getRuntime().getObject() == this) {
             for (Iterator vars = instanceVariableNames(); vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
 
             return getRuntime().newArray(constantNames);
         }
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (objectClass == p) {
                 continue;
             }
 
             for (Iterator vars = p.instanceVariableNames(); vars.hasNext();) {
                 String name = (String) vars.next();
                 if (IdUtil.isConstant(name)) {
                     constantNames.add(getRuntime().newString(name));
                 }
             }
         }
 
         return getRuntime().newArray(constantNames);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject remove_class_variable(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isClassVariable(id)) {
             throw getRuntime().newNameError("wrong class variable name " + id, id);
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject variable = removeInstanceVariable(id);
         if (variable != null) {
             return variable;
         }
 
         if (isClassVarDefined(id)) {
             throw cannotRemoveError(id);
         }
         throw getRuntime().newNameError("class variable " + id + " not defined for " + getName(), id);
     }
 
     private RaiseException cannotRemoveError(String id) {
         return getRuntime().newNameError("cannot remove " + id + " for " + getName(), id);
     }
 
     public IRubyObject remove_const(IRubyObject name) {
         String id = name.asSymbol();
 
         if (!IdUtil.isConstant(id)) {
             throw wrongConstantNameError(id);
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject variable = getInstanceVariable(id);
         if (variable != null) {
             return removeInstanceVariable(id);
         }
 
         if (isClassVarDefined(id)) {
             throw cannotRemoveError(id);
         }
         throw getRuntime().newNameError("constant " + id + " not defined for " + getName(), id);
     }
 
     /** rb_mod_append_features
      *
      */
     // TODO: Proper argument check (conversion?)
     public RubyModule append_features(IRubyObject module) {
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     public IRubyObject extend_object(IRubyObject obj) {
         obj.extendObject(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     public IRubyObject included(IRubyObject other) {
         return getRuntime().getNil();
     }
 
     public IRubyObject extended(IRubyObject other, Block block) {
         return getRuntime().getNil();
     }
 
     private void setVisibility(IRubyObject[] args, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             // Note: we change current frames visibility here because the methods which call
             // this method are all "fast" (e.g. they do not created their own frame).
             getRuntime().getCurrentContext().setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
 
     /** rb_mod_public
      *
      */
     public RubyModule rbPublic(IRubyObject[] args) {
         setVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     public RubyModule rbProtected(IRubyObject[] args) {
         setVisibility(args, Visibility.PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     public RubyModule rbPrivate(IRubyObject[] args) {
         setVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     public RubyModule module_function(IRubyObject[] args) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
             context.setCurrentVisibility(Visibility.MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, Visibility.PRIVATE);
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asSymbol();
                 DynamicMethod method = searchMethod(name);
                 assert !method.isUndefined() : "undefined method '" + name + "'";
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, Visibility.PUBLIC));
                 callMethod(context, "singleton_method_added", RubySymbol.newSymbol(getRuntime(), name));
             }
         }
         return this;
     }
 
     public IRubyObject method_added(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
 
     public IRubyObject method_removed(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
 
     public IRubyObject method_undefined(IRubyObject nothing, Block block) {
         return getRuntime().getNil();
     }
     
     public RubyBoolean method_defined(IRubyObject symbol) {
         return isMethodBound(symbol.asSymbol(), true) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PUBLIC);
         return this;
     }
 
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, Visibility.PRIVATE);
         return this;
     }
 
     public RubyModule alias_method(IRubyObject newId, IRubyObject oldId) {
         defineAlias(newId.asSymbol(), oldId.asSymbol());
         return this;
     }
 
     public RubyModule undef_method(IRubyObject name) {
         undef(name.asSymbol());
         return this;
     }
 
     public IRubyObject module_eval(IRubyObject[] args, Block block) {
         return specificEval(this, args, block);
     }
 
     public RubyModule remove_method(IRubyObject[] args) {
         for(int i=0;i<args.length;i++) {
             removeMethod(args[i].asSymbol());
         }
         return this;
     }
 
     public static void marshalTo(RubyModule module, MarshalStream output) throws java.io.IOException {
         output.writeString(module.name().toString());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         Ruby runtime = input.getRuntime();
         RubyModule result = runtime.getClassFromPath(name);
         if (result == null) {
             throw runtime.newNameError("uninitialized constant " + name, name);
         }
         input.registerLinkTarget(result);
         return result;
     }
 
     public SinglyLinkedList getCRef() {
         return cref;
     }
 }
diff --git a/src/org/jruby/ast/util/ArgsUtil.java b/src/org/jruby/ast/util/ArgsUtil.java
index 6137d53180..a1e17d1348 100644
--- a/src/org/jruby/ast/util/ArgsUtil.java
+++ b/src/org/jruby/ast/util/ArgsUtil.java
@@ -1,102 +1,102 @@
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
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.ast.util;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author  jpetersen
  */
 public final class ArgsUtil {
     
     public static IRubyObject[] convertToJavaArray(IRubyObject value) {
         if (value == null) {
         	return IRubyObject.NULL_ARRAY;
         }
         
         if (value instanceof RubyArray) {
-            return ((RubyArray) value).toJavaArray();
+            return ((RubyArray)value).toJavaArrayMaybeUnsafe();
         }
         
         return new IRubyObject[] { value };
     }
 
     /**
      * This name may be a bit misleading, since this also attempts to coerce
      * array behavior using to_ary.
      * 
      * @param runtime The JRuby runtime
      * @param value The value to convert
      * @param coerce Whether to coerce using to_ary or just wrap with an array
      */
     public static RubyArray convertToRubyArray(Ruby runtime, IRubyObject value, boolean coerce) {
         if (value == null) {
             return runtime.newArray(0);
         }
         
         if (!coerce) {
             // don't attempt to coerce to array, just wrap and return
             return runtime.newArray(value);
         }
         
         // FIXME: I don't like this, but all consumers of this method do the same cast.
         IRubyObject newValue = value.convertToType("Array", "to_ary", false);
 
         if (newValue.isNil()) {
             return runtime.newArray(value);
         }
         
         // empirically it appears that to_ary coersions always return array or nil, so this
         // should always be an array by now.
         return (RubyArray)newValue;
     }
     
     /**
      * Remove first element from array
      * 
      * @param array to have first element "popped" off
      * @return all but first element of the supplied array
      */
     public static IRubyObject[] popArray(IRubyObject[] array) {
     	if (array == null || array.length == 0) {
     		return IRubyObject.NULL_ARRAY;
     	}
     	
     	IRubyObject[] newArray = new IRubyObject[array.length - 1];
     	System.arraycopy(array, 1, newArray, 0, array.length - 1);
     	
     	return newArray;
     }
 }
diff --git a/src/org/jruby/ext/openssl/Digest.java b/src/org/jruby/ext/openssl/Digest.java
index 792a51ca86..c62bcece58 100644
--- a/src/org/jruby/ext/openssl/Digest.java
+++ b/src/org/jruby/ext/openssl/Digest.java
@@ -1,227 +1,212 @@
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
  * Copyright (C) 2006, 2007 Ola Bini <ola@ologix.com>
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
 package org.jruby.ext.openssl;
 
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class Digest extends RubyObject {
     private static ObjectAllocator DIGEST_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new Digest(runtime, klass);
         }
     };
     
     public static void createDigest(Ruby runtime, RubyModule ossl) {
         RubyModule mDigest = ossl.defineModuleUnder("Digest");
         RubyClass cDigest = mDigest.defineClassUnder("Digest",runtime.getObject(),DIGEST_ALLOCATOR);
         RubyClass openSSLError = ossl.getClass("OpenSSLError");
         mDigest.defineClassUnder("DigestError",openSSLError,openSSLError.getAllocator());
 
         CallbackFactory digestcb = runtime.callbackFactory(Digest.class);
 
-        cDigest.getMetaClass().defineMethod("new",digestcb.getOptSingletonMethod("newInstance"));
+        //        cDigest.getMetaClass().defineMethod("new",digestcb.getOptSingletonMethod("newInstance"));
         cDigest.getMetaClass().defineFastMethod("digest",digestcb.getFastSingletonMethod("s_digest",IRubyObject.class,IRubyObject.class));
         cDigest.getMetaClass().defineFastMethod("hexdigest",digestcb.getFastSingletonMethod("s_hexdigest",IRubyObject.class,IRubyObject.class));
         cDigest.defineMethod("initialize",digestcb.getOptMethod("initialize"));
         cDigest.defineFastMethod("initialize_copy",digestcb.getFastMethod("initialize_copy",IRubyObject.class));
-        cDigest.defineFastMethod("clone",digestcb.getFastMethod("rbClone"));
         cDigest.defineFastMethod("update",digestcb.getFastMethod("update",IRubyObject.class));
         cDigest.defineFastMethod("<<",digestcb.getFastMethod("update",IRubyObject.class));
         cDigest.defineFastMethod("digest",digestcb.getFastMethod("digest"));
         cDigest.defineFastMethod("hexdigest",digestcb.getFastMethod("hexdigest"));
         cDigest.defineFastMethod("inspect",digestcb.getFastMethod("hexdigest"));
         cDigest.defineFastMethod("to_s",digestcb.getFastMethod("hexdigest"));
         cDigest.defineFastMethod("==",digestcb.getFastMethod("eq",IRubyObject.class));
         cDigest.defineFastMethod("reset",digestcb.getFastMethod("reset"));
         cDigest.defineFastMethod("name",digestcb.getFastMethod("name"));
         cDigest.defineFastMethod("size",digestcb.getFastMethod("size"));
     }
 
     private static String transformDigest(String inp) {
         String[] sp = inp.split("::");
         if(sp.length > 1) { // We only want Digest names from the last part of class name
             inp = sp[sp.length-1];
         }
 
         if("DSS".equalsIgnoreCase(inp)) {
             return "SHA";
         } else if("DSS1".equalsIgnoreCase(inp)) {
             return "SHA1";
         }
         return inp;
     }
 
-    public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
-        Digest result = (Digest) ((RubyClass) recv).allocate();
-        if(!(recv.toString().equals("OpenSSL::Digest::Digest"))) {
-            try {
-                result.name = recv.toString();
-                result.md = MessageDigest.getInstance(transformDigest(recv.toString()));
-            } catch(NoSuchAlgorithmException e) {
-                throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + recv.toString() + ")");
-            }
-        }
-        result.callInit(args, block);
-        return result;
-    }
-
     public static IRubyObject s_digest(IRubyObject recv, IRubyObject str, IRubyObject data) {
         String name = str.toString();
         try {
             MessageDigest md = MessageDigest.getInstance(transformDigest(name));
             return RubyString.newString(recv.getRuntime(), md.digest(data.convertToString().getBytes()));
         } catch(NoSuchAlgorithmException e) {
             throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
         }
     }
 
     public static IRubyObject s_hexdigest(IRubyObject recv, IRubyObject str, IRubyObject data) {
         String name = str.toString();
         try {
             MessageDigest md = MessageDigest.getInstance(transformDigest(name));
             return RubyString.newString(recv.getRuntime(), ByteList.plain(Utils.toHex(md.digest(data.convertToString().getBytes()))));
         } catch(NoSuchAlgorithmException e) {
             throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
         }
     }
 
     public Digest(Ruby runtime, RubyClass type) {
         super(runtime,type);
         data = new StringBuffer();
+
+        if(!(type.toString().equals("OpenSSL::Digest::Digest"))) {
+            try {
+                name = type.toString();
+                md = MessageDigest.getInstance(transformDigest(type.toString()));
+            } catch(NoSuchAlgorithmException e) {
+                throw runtime.newNotImplementedError("Unsupported digest algorithm (" + type.toString() + ")");
+            }
+        }
     }
 
     private MessageDigest md;
     private StringBuffer data;
     private String name;
 
-    public IRubyObject initialize(IRubyObject[] args) {
+    public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         IRubyObject type;
         IRubyObject data = getRuntime().getNil();
         if(checkArgumentCount(args,1,2) == 2) {
             data = args[1];
         }
         type = args[0];
 
         name = type.toString();
         try {
             md = MessageDigest.getInstance(transformDigest(name));
         } catch(NoSuchAlgorithmException e) {
             throw getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
         }
         if(!data.isNil()) {
             update(data);
         }
         return this;
     }
 
     public IRubyObject initialize_copy(IRubyObject obj) {
         if(this == obj) {
             return this;
         }
         checkFrozen();
         data = new StringBuffer(((Digest)obj).data.toString());
         name = ((Digest)obj).md.getAlgorithm();
         try {
             md = MessageDigest.getInstance(transformDigest(name));
         } catch(NoSuchAlgorithmException e) {
             throw getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
         }
 
         return this;
     }
 
     public IRubyObject update(IRubyObject obj) {
         data.append(obj);
         md.update(obj.convertToString().getBytes());
         return this;
     }
 
     public IRubyObject reset() {
         md.reset();
         data = new StringBuffer();
         return this;
     }
 
     public IRubyObject digest() {
         md.reset();
         return RubyString.newString(getRuntime(), md.digest(ByteList.plain(data)));
     }
 
     public IRubyObject name() {
         return getRuntime().newString(name);
     }
 
     public IRubyObject size() {
         return getRuntime().newFixnum(md.getDigestLength());
     }
 
     public IRubyObject hexdigest() {
         md.reset();
         return RubyString.newString(getRuntime(), ByteList.plain(Utils.toHex(md.digest(ByteList.plain(data)))));
     }
 
     public IRubyObject eq(IRubyObject oth) {
         boolean ret = this == oth;
         if(!ret && oth instanceof Digest) {
             Digest b = (Digest)oth;
             ret = this.md.getAlgorithm().equals(b.md.getAlgorithm()) &&
                 this.digest().equals(b.digest());
         }
 
         return ret ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
-    public IRubyObject rbClone() {
-        IRubyObject clone = new Digest(getRuntime(),getMetaClass().getRealClass());
-        clone.setMetaClass(getMetaClass().getSingletonClassClone());
-        clone.setTaint(this.isTaint());
-        clone.initCopy(this);
-        clone.setFrozen(isFrozen());
-        return clone;
-    }
-
     String getAlgorithm() {
         return this.md.getAlgorithm();
     }
 }
 
diff --git a/src/org/jruby/ext/openssl/OpenSSLReal.java b/src/org/jruby/ext/openssl/OpenSSLReal.java
index 8eced27005..db22437ba6 100644
--- a/src/org/jruby/ext/openssl/OpenSSLReal.java
+++ b/src/org/jruby/ext/openssl/OpenSSLReal.java
@@ -1,65 +1,67 @@
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
 package org.jruby.ext.openssl;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class OpenSSLReal {
     public static void createOpenSSL(Ruby runtime) {
+        java.security.Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(),2);
+
         RubyModule ossl = runtime.defineModule("OpenSSL");
         RubyClass standardError = runtime.getClass("StandardError");
         ossl.defineClassUnder("OpenSSLError",standardError,standardError.getAllocator());
 
         ASN1.createASN1(runtime, ossl);
         Digest.createDigest(runtime, ossl);
         Cipher.createCipher(runtime, ossl);
         Random.createRandom(runtime, ossl);
         PKey.createPKey(runtime,ossl);
         HMAC.createHMAC(runtime,ossl);
         X509.createX509(runtime,ossl);
         Config.createConfig(runtime,ossl);
         NetscapeSPKI.createNetscapeSPKI(runtime,ossl);
         PKCS7.createPKCS7(runtime,ossl);
         SSL.createSSL(runtime,ossl);
 
         ossl.setConstant("VERSION",runtime.newString("1.0.0"));
         ossl.setConstant("OPENSSL_VERSION",runtime.newString("OpenSSL 0.9.8b 04 May 2006 (Java fake)"));
         
         try {
             java.security.MessageDigest.getInstance("SHA224");
             ossl.setConstant("OPENSSL_VERSION_NUMBER",runtime.newFixnum(9469999));
         } catch(java.security.NoSuchAlgorithmException e) {
             ossl.setConstant("OPENSSL_VERSION_NUMBER",runtime.newFixnum(9469952));
         }
     }
 }// OpenSSLReal
diff --git a/src/org/jruby/runtime/Block.java b/src/org/jruby/runtime/Block.java
index 0d3757bc8b..20e7a57df0 100644
--- a/src/org/jruby/runtime/Block.java
+++ b/src/org/jruby/runtime/Block.java
@@ -1,372 +1,372 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeTypes;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class Block {
     /**
      * All Block variables should either refer to a real block or this NULL_BLOCK.
      */
     public static Block NULL_BLOCK = new Block() {
         public boolean isGiven() {
             return false;
         }
         
         public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
                 RubyModule klass, boolean aValue) {
             throw context.getRuntime().newLocalJumpError("yield called out of block");
         }
         
         public Block cloneBlock() {
             return this;
         }
     };
 
     /**
      * 'self' at point when the block is defined
      */
     private IRubyObject self;
 
     /**
      * body of the block (wrapped in an ICallable)
      */
     private ICallable method;
 
     /**
      * AST Node representing the parameter (VARiable) list to the block.
      */
     private Node varNode;
     
     /**
      * frame of method which defined this block
      */
     private Frame frame;
     private SinglyLinkedList cref;
     private Visibility visibility;
     private RubyModule klass;
     
     /**
      * A reference to all variable values (and names) that are in-scope for this block.
      */
     private DynamicScope dynamicScope;
     
     /**
      * The Proc that this block is associated with.  When we reference blocks via variable
      * reference they are converted to Proc objects.  We store a reference of the associated
      * Proc object for easy conversion.  
      */
     private RubyProc proc = null;
     
     public boolean isLambda = false;
 
     public static Block createBlock(ThreadContext context, Node varNode, DynamicScope dynamicScope,
             ICallable method, IRubyObject self) {
         return new Block(varNode,
                          method,
                          self,
                          context.getCurrentFrame(),
                          context.peekCRef(),
                          context.getCurrentFrame().getVisibility(),
                          context.getRubyClass(),
                          dynamicScope);
     }
     
     protected Block() {
         this(null, null, null, null, null, null, null, null);
     }
 
     public Block(Node varNode, ICallable method, IRubyObject self, Frame frame,
         SinglyLinkedList cref, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope) {
     	
         //assert method != null;
 
         this.varNode = varNode;
         this.method = method;
         this.self = self;
         this.frame = frame;
         this.visibility = visibility;
         this.klass = klass;
         this.cref = cref;
         this.dynamicScope = dynamicScope;
     }
     
     public static Block createBinding(RubyModule wrapper, Frame frame, DynamicScope dynamicScope) {
         ThreadContext context = frame.getSelf().getRuntime().getCurrentContext();
         
         // We create one extra dynamicScope on a binding so that when we 'eval "b=1", binding' the
         // 'b' will get put into this new dynamic scope.  The original scope does not see the new
         // 'b' and successive evals with this binding will.  I take it having the ability to have 
         // succesive binding evals be able to share same scope makes sense from a programmers 
         // perspective.   One crappy outcome of this design is it requires Dynamic and Static 
         // scopes to be mutable for this one case.
         
         // Note: In Ruby 1.9 all of this logic can go away since they will require explicit
         // bindings for evals.
         
         // We only define one special dynamic scope per 'logical' binding.  So all bindings for
         // the same scope should share the same dynamic scope.  This allows multiple evals with
         // different different bindings in the same scope to see the same stuff.
         DynamicScope extraScope = dynamicScope.getBindingScope();
         
         // No binding scope so we should create one
         if (extraScope == null) {
             // If the next scope out has the same binding scope as this scope it means
             // we are evaling within an eval and in that case we should be sharing the same
             // binding scope.
             DynamicScope parent = dynamicScope.getNextCapturedScope(); 
             if (parent != null && parent.getBindingScope() == dynamicScope) {
                 extraScope = dynamicScope;
             } else {
                 extraScope = new DynamicScope(new BlockStaticScope(dynamicScope.getStaticScope()), dynamicScope);
                 dynamicScope.setBindingScope(extraScope);
             }
         } 
         
         // FIXME: Ruby also saves wrapper, which we do not
         return new Block(null, null, frame.getSelf(), frame, context.peekCRef(), frame.getVisibility(), 
                 context.getBindingRubyClass(), extraScope);
     }
 
     public IRubyObject call(ThreadContext context, IRubyObject[] args, IRubyObject replacementSelf) {
         Block newBlock = this.cloneBlock();
             
         if (replacementSelf != null) newBlock.self = replacementSelf; 
 
         return newBlock.yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true);
     }
     
     protected void pre(ThreadContext context, RubyModule klass) {
         context.preYieldSpecificBlock(this, klass);
     }
     
     protected void post(ThreadContext context) {
         context.postYield(this);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue) {
         if (klass == null) {
             self = this.self;
             frame.setSelf(self);
         }
         
         pre(context, klass);
 
         try {
             IRubyObject[] args = getBlockArgs(context, value, self, aValue);
         
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return method.call(context, self, args, NULL_BLOCK);
                 } catch (JumpException je) {
                     if (je.getJumpType() == JumpException.JumpType.RedoJump) {
                         // do nothing, allow loop to redo
                     } else {
                         if (je.getJumpType() == JumpException.JumpType.BreakJump && je.getTarget() == null) {
                             je.setTarget(this);                            
                         }                        
                         throw je;
                     }
                 }
             }
             
         } catch (JumpException je) {
             // A 'next' is like a local return from the block, ending this call or yield.
         	if (je.getJumpType() == JumpException.JumpType.NextJump) return (IRubyObject) je.getValue();
 
             throw je;
         } finally {
             post(context);
         }
     }
 
     private IRubyObject[] getBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self, boolean valueIsArray) {
         //FIXME: block arg handling is mucked up in strange ways and NEED to
         // be fixed. Especially with regard to Enumerable. See RubyEnumerable#eachToList too.
         if (varNode == null) {
             return new IRubyObject[]{value};
         }
         
         Ruby runtime = self.getRuntime();
         
         switch (varNode.nodeId) {
             case NodeTypes.ZEROARGNODE:
                 break;
             case NodeTypes.MULTIPLEASGNNODE:
                 if (!valueIsArray) {
                     value = ArgsUtil.convertToRubyArray(runtime, value, ((MultipleAsgnNode)varNode).getHeadNode() != null);
                 }
 
                 value = AssignmentVisitor.multiAssign(context, self, (MultipleAsgnNode)varNode, (RubyArray)value, false);
                 break;
             default:
                 if (valueIsArray) {
                     int length = arrayLength(value);
 
                     switch (length) {
                         case 0:
                             value = runtime.getNil();
                             break;
                         case 1:
                             value = ((RubyArray)value).first(IRubyObject.NULL_ARRAY);
                             break;
                         default:
                             runtime.getWarnings().warn("multiple values for a block parameter (" + length + " for 1)");
                     }
                 } else if (value == null) { 
                     runtime.getWarnings().warn("multiple values for a block parameter (0 for 1)");
                 }
 
                 AssignmentVisitor.assign(context, self, varNode, value, Block.NULL_BLOCK, false);
         }
         return ArgsUtil.convertToJavaArray(value);
     }
     
     private int arrayLength(IRubyObject node) {
         return node instanceof RubyArray ? ((RubyArray)node).getLength() : 0;
     }
 
     public Block cloneBlock() {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
-        Block newBlock = new Block(varNode, method, self, frame, cref, visibility, klass, 
+        Block newBlock = new Block(varNode, method, self, frame.duplicate(), cref, visibility, klass, 
                 dynamicScope.cloneScope());
         
         newBlock.isLambda = isLambda;
 
         return newBlock;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return method.getArity();
     }
 
     public Visibility getVisibility() {
         return visibility;
     }
 
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
 
     public SinglyLinkedList getCRef() {
         return cref;
     }
 
     /**
      * Retrieve the proc object associated with this block
      * 
      * @return the proc or null if this has no proc associated with it
      */
     public RubyProc getProcObject() {
     	return proc;
     }
     
     /**
      * Set the proc object associated with this block
      * 
      * @param procObject
      */
     public void setProcObject(RubyProc procObject) {
     	this.proc = procObject;
     }
 
     /**
      * Gets the dynamicVariables that are local to this block.   Parent dynamic scopes are also
      * accessible via the current dynamic scope.
      * 
      * @return Returns all relevent variable scoping information
      */
     public DynamicScope getDynamicScope() {
         return dynamicScope;
     }
 
     /**
      * Gets the frame.
      * 
      * @return Returns a RubyFrame
      */
     public Frame getFrame() {
         return frame;
     }
 
     /**
      * Gets the klass.
      * @return Returns a RubyModule
      */
     public RubyModule getKlass() {
         return klass;
     }
     
     /**
      * Is the current block a real yield'able block instead a null one
      * 
      * @return true if this is a valid block or false otherwise
      */
     public boolean isGiven() {
         return true;
     }
 }
