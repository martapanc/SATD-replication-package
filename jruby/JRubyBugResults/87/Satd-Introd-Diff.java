diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 0d13ce8f54..55947df333 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1734 +1,2326 @@
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
-import java.util.ArrayList;
+import java.util.Arrays;
 import java.util.Collection;
-import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Set;
-import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.JavaUtil;
-import org.jruby.javasupport.util.ConversionIterator;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
+import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
+import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
-import org.jruby.runtime.builtin.meta.ArrayMetaClass;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Pack;
-import org.jruby.util.collections.IdentitySet;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  */
 public class RubyArray extends RubyObject implements List {
-    private List list;
-    private boolean tmpLock;
+
+    public static RubyClass createArrayClass(IRuby runtime) {
+        RubyClass arrayc = runtime.defineClass("Array", runtime.getObject(), ARRAY_ALLOCATOR);
+        arrayc.index = ClassIndex.ARRAY;
+        CallbackFactory callbackFactory = runtime.callbackFactory(RubyArray.class);
+
+        arrayc.includeModule(runtime.getModule("Enumerable"));
+        arrayc.getMetaClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("create"));
+
+        arrayc.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
+        arrayc.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s")); 
+        arrayc.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
+        arrayc.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
+        arrayc.defineFastMethod("to_ary", callbackFactory.getFastMethod("to_ary"));
+        arrayc.defineFastMethod("frozen?", callbackFactory.getFastMethod("frozen"));
+
+        arrayc.defineFastMethod("==", callbackFactory.getFastMethod("op_equal", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("eql?", callbackFactory.getFastMethod("eql", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
+
+        arrayc.defineFastMethod("[]", callbackFactory.getFastOptMethod("aref"));
+        arrayc.defineFastMethod("[]=", callbackFactory.getFastOptMethod("aset"));
+        arrayc.defineFastMethod("at", callbackFactory.getFastMethod("at", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineMethod("fetch", callbackFactory.getOptMethod("fetch"));
+        arrayc.defineFastMethod("first", callbackFactory.getFastOptMethod("first"));
+        arrayc.defineFastMethod("last", callbackFactory.getFastOptMethod("last"));
+        arrayc.defineFastMethod("concat", callbackFactory.getFastMethod("concat", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("<<", callbackFactory.getFastMethod("append", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("push", callbackFactory.getFastOptMethod("push_m"));
+        arrayc.defineFastMethod("pop", callbackFactory.getFastMethod("pop"));
+        arrayc.defineFastMethod("shift", callbackFactory.getFastMethod("shift"));
+        arrayc.defineFastMethod("unshift", callbackFactory.getFastOptMethod("unshift_m"));
+        arrayc.defineFastMethod("insert", callbackFactory.getFastOptMethod("insert"));
+        arrayc.defineMethod("each", callbackFactory.getMethod("each"));
+        arrayc.defineMethod("each_index", callbackFactory.getMethod("each_index"));
+        arrayc.defineMethod("reverse_each", callbackFactory.getMethod("reverse_each"));
+        arrayc.defineFastMethod("length", callbackFactory.getFastMethod("length"));
+        arrayc.defineAlias("size", "length");
+        arrayc.defineFastMethod("empty?", callbackFactory.getFastMethod("empty_p"));
+        arrayc.defineFastMethod("index", callbackFactory.getFastMethod("index", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("rindex", callbackFactory.getFastMethod("rindex", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("indexes", callbackFactory.getFastOptMethod("indexes"));
+        arrayc.defineFastMethod("indices", callbackFactory.getFastOptMethod("indexes"));
+        arrayc.defineFastMethod("join", callbackFactory.getFastOptMethod("join_m"));
+        arrayc.defineFastMethod("reverse", callbackFactory.getFastMethod("reverse"));
+        arrayc.defineFastMethod("reverse!", callbackFactory.getFastMethod("reverse_bang"));
+        arrayc.defineMethod("sort", callbackFactory.getMethod("sort"));
+        arrayc.defineMethod("sort!", callbackFactory.getMethod("sort_bang"));
+        arrayc.defineMethod("collect", callbackFactory.getMethod("collect"));
+        arrayc.defineMethod("collect!", callbackFactory.getMethod("collect_bang"));
+        arrayc.defineMethod("map", callbackFactory.getMethod("collect"));
+        arrayc.defineMethod("map!", callbackFactory.getMethod("collect_bang"));
+        arrayc.defineMethod("select", callbackFactory.getMethod("select"));
+        arrayc.defineFastMethod("values_at", callbackFactory.getFastOptMethod("values_at"));
+        arrayc.defineMethod("delete", callbackFactory.getMethod("delete", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("delete_at", callbackFactory.getFastMethod("delete_at", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineMethod("delete_if", callbackFactory.getMethod("delete_if"));
+        arrayc.defineMethod("reject", callbackFactory.getMethod("reject"));
+        arrayc.defineMethod("reject!", callbackFactory.getMethod("reject_bang"));
+        arrayc.defineMethod("zip", callbackFactory.getOptMethod("zip"));
+        arrayc.defineFastMethod("transpose", callbackFactory.getFastMethod("transpose"));
+        arrayc.defineFastMethod("replace", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("clear", callbackFactory.getFastMethod("rb_clear"));
+        arrayc.defineMethod("fill", callbackFactory.getOptMethod("fill"));
+        arrayc.defineFastMethod("include?", callbackFactory.getFastMethod("include_p", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", RubyKernel.IRUBY_OBJECT));
+
+        arrayc.defineFastMethod("slice", callbackFactory.getFastOptMethod("aref"));
+        arrayc.defineFastMethod("slice!", callbackFactory.getFastOptMethod("slice_bang"));
+
+        arrayc.defineFastMethod("assoc", callbackFactory.getFastMethod("assoc", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("rassoc", callbackFactory.getFastMethod("rassoc", RubyKernel.IRUBY_OBJECT));
+
+        arrayc.defineFastMethod("+", callbackFactory.getFastMethod("op_plus", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("*", callbackFactory.getFastMethod("op_times", RubyKernel.IRUBY_OBJECT));
+
+        arrayc.defineFastMethod("-", callbackFactory.getFastMethod("op_diff", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("&", callbackFactory.getFastMethod("op_and", RubyKernel.IRUBY_OBJECT));
+        arrayc.defineFastMethod("|", callbackFactory.getFastMethod("op_or", RubyKernel.IRUBY_OBJECT));
+
+        arrayc.defineFastMethod("uniq", callbackFactory.getFastMethod("uniq"));
+        arrayc.defineFastMethod("uniq!", callbackFactory.getFastMethod("uniq_bang"));
+        arrayc.defineFastMethod("compact", callbackFactory.getFastMethod("compact"));
+        arrayc.defineFastMethod("compact!", callbackFactory.getFastMethod("compact_bang"));
+
+        arrayc.defineFastMethod("flatten", callbackFactory.getFastMethod("flatten"));
+        arrayc.defineFastMethod("flatten!", callbackFactory.getFastMethod("flatten_bang"));
+
+        arrayc.defineFastMethod("nitems", callbackFactory.getFastMethod("nitems"));
+
+        arrayc.defineFastMethod("pack", callbackFactory.getFastMethod("pack", RubyKernel.IRUBY_OBJECT));
+
+        return arrayc;
+    }
+
+    private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(IRuby runtime, RubyClass klass) {
+            return new RubyArray(runtime, klass);
+        }
+    };
+    
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
 
-    private RubyArray(IRuby runtime, List list) {
-        super(runtime, runtime.getClass("Array"));
-        this.list = list;
-    }
-    
-    /**
-     * "Light" version which isn't entered into ObjectSpace. For internal utility purposes.
-     */
-    private RubyArray(IRuby runtime) {
-        super(runtime, runtime.getClass("Array"), false);
-        this.list = new ArrayList(6);
-    }
-    
-    public RubyArray(IRuby runtime, RubyClass klass) {
-        super(runtime, klass);
-        this.list = new ArrayList(16);
-    }
-
-    public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
-            IRubyObject[] args, CallType callType, Block block) {
+    public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue,
+            String name, IRubyObject[] args, CallType callType, Block block) {
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
-                return push(args);
+            return push_m(args);
             case NIL_P_SWITCHVALUE:
                 Arity.noArguments().checkArity(context.getRuntime(), args);
                 return nil_p();
             case EQUALEQUAL_SWITCHVALUE:
                 Arity.singleArgument().checkArity(context.getRuntime(), args);
-                return array_op_equal(args[0]);
+            return op_equal(args[0]);
             case UNSHIFT_SWITCHVALUE:
                 Arity.optional().checkArity(context.getRuntime(), args);
-                return unshift(args);
+            return unshift_m(args);
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
+    }    
+
+    /** rb_ary_s_create
+     * 
+     */
+    public static IRubyObject create(IRubyObject klass, IRubyObject[] args, Block block) {
+        RubyArray arr = (RubyArray) ((RubyClass) klass).allocate();
+        arr.callInit(IRubyObject.NULL_ARRAY, block);
+    
+        if (args.length > 0) {
+            arr.alloc(args.length);
+            System.arraycopy(args, 0, arr.values, 0, args.length);
+            arr.realLength = args.length;
+        }
+        return arr;
+    }
+
+    /** rb_ary_new2
+     *
+     */
+    public static final RubyArray newArray(final IRuby runtime, final long len) {
+        return new RubyArray(runtime, len);
+    }
+
+    /** rb_ary_new
+     *
+     */
+    public static final RubyArray newArray(final IRuby runtime) {
+        return new RubyArray(runtime, ARRAY_DEFAULT_SIZE);
+    }
+
+    /** rb_ary_new
+     *
+     */
+    public static final RubyArray newArrayLight(final IRuby runtime) {
+        /* Ruby arrays default to holding 16 elements, so we create an
+         * ArrayList of the same size if we're not told otherwise
+         */
+        RubyArray arr = new RubyArray(runtime, false);
+        arr.alloc(ARRAY_DEFAULT_SIZE);
+        return arr;
+    }
+
+    public static RubyArray newArray(IRuby runtime, IRubyObject obj) {
+        return new RubyArray(runtime, new IRubyObject[] { obj });
+    }
+
+    /** rb_assoc_new
+     *
+     */
+    public static RubyArray newArray(IRuby runtime, IRubyObject car, IRubyObject cdr) {
+        return new RubyArray(runtime, new IRubyObject[] { car, cdr });
+    }
+
+    /** rb_ary_new4, rb_ary_new3
+     *   
+     */
+    public static RubyArray newArray(IRuby runtime, IRubyObject[] args) {
+        RubyArray arr = new RubyArray(runtime, args.length);
+        System.arraycopy(args, 0, arr.values, 0, args.length);
+        arr.realLength = args.length;
+        return arr;
     }
     
+    public static RubyArray newArrayNoCopy(IRuby runtime, IRubyObject[] args) {
+        return new RubyArray(runtime, args);
+    }
+
+    public static RubyArray newArray(IRuby runtime, List list) {
+        RubyArray arr = new RubyArray(runtime, list.size());
+        list.toArray(arr.values);
+        arr.realLength = arr.values.length;
+        return arr;
+    }
+
+    public static final int ARRAY_DEFAULT_SIZE = 16;    
+
+    private IRubyObject[] values;
+    private boolean tmpLock = false;
+    private boolean shared = false;
+
+    private int begin = 0;
+    private int realLength = 0;
+
+    /* 
+     * plain internal array assignment
+     */
+    public RubyArray(IRuby runtime, IRubyObject[]vals){
+        super(runtime, runtime.getArray());
+        values = vals;
+        realLength = vals.length;
+    }
+    
+    /* rb_ary_new2
+     * just allocates the internal array
+     */
+    private RubyArray(IRuby runtime, long length) {
+        super(runtime, runtime.getArray());
+        checkLength(length);
+        alloc((int) length);
+    }
+
+    /* rb_ary_new3, rb_ary_new4
+     * allocates the internal array of size length and copies the 'length' elements
+     */
+    public RubyArray(IRuby runtime, long length, IRubyObject[] vals) {
+        super(runtime, runtime.getArray());
+        checkLength(length);
+        int ilength = (int) length;
+        alloc(ilength);
+        if (ilength > 0 && vals.length > 0) System.arraycopy(vals, 0, values, 0, ilength);
+
+        realLength = ilength;
+    }
+
+    /* rb_ary_new3, rb_ary_new4, with begin
+     * allocates the internal array of size length and copies the 'length' elements from 'vals' starting from 'beg'
+     */
+    private RubyArray(IRuby runtime, int beg, long length, IRubyObject[] vals) {
+        super(runtime, runtime.getArray());
+        checkLength(length);
+        int ilength = (int) length;
+        alloc(ilength);
+        if (ilength > 0 && vals.length > 0) System.arraycopy(vals, beg, values, 0, ilength);
+
+        realLength = ilength;
+    }
+
+    /* NEWOBJ and OBJSETUP equivalent
+     * fastest one, for shared arrays, optional objectspace
+     */
+    private RubyArray(IRuby runtime, boolean objectSpace) {
+        super(runtime, runtime.getArray(), objectSpace);
+    }
+
+    private RubyArray(IRuby runtime) {
+        super(runtime, runtime.getArray());
+        alloc(ARRAY_DEFAULT_SIZE);
+    }
+
+    public RubyArray(IRuby runtime, RubyClass klass) {
+        super(runtime, klass);
+        alloc(ARRAY_DEFAULT_SIZE);
+    }
+
+    private final IRubyObject[] reserve(int length) {
+        return new IRubyObject[length];
+    }
+
+    private final void alloc(int length) {
+        values = new IRubyObject[length];
+    }
+
+    private final void realloc(int newLength) {
+        IRubyObject[] reallocated = new IRubyObject[newLength];
+        System.arraycopy(values, 0, reallocated, 0, newLength > realLength ? realLength : newLength);
+        values = reallocated;
+    }
+
+    private final void checkLength(long length) {
+        if (length < 0) {
+            throw getRuntime().newArgumentError("negative array size (or size too big)");
+        }
+
+        if (length >= Integer.MAX_VALUE) {
+            throw getRuntime().newArgumentError("array size too big");
+        }
+    }
+
     public int getNativeTypeIndex() {
         return ClassIndex.ARRAY;
     }
 
     /** Getter for property list.
      * @return Value of property list.
      */
     public List getList() {
-        return list;
+        return Arrays.asList(toJavaArray()); 
+    }
+
+    public int getLength() {
+        return realLength;
     }
 
     public IRubyObject[] toJavaArray() {
-        return (IRubyObject[])list.toArray(new IRubyObject[getLength()]);
+        IRubyObject[] copy = reserve(realLength);
+        System.arraycopy(values, begin, copy, 0, realLength);
+        return copy;
     }
     
-    public RubyArray convertToArray() {
-    	return this;
-    }
+    public IRubyObject[] toJavaArrayUnsafe() {
+        return !shared ? values : toJavaArray();
+    }    
 
-    /** Getter for property tmpLock.
-     * @return Value of property tmpLock.
+    /** rb_ary_make_shared
+     *
      */
-    public boolean isTmpLock() {
-        return tmpLock;
+    private final RubyArray makeShared(int beg, int len){
+        RubyArray sharedArray = new RubyArray(getRuntime(), true);
+        shared = true;
+        sharedArray.values = values;
+        sharedArray.shared = true;
+        sharedArray.begin = beg;
+        sharedArray.realLength = len;
+        return sharedArray;        
     }
 
-    /** Setter for property tmpLock.
-     * @param tmpLock New value of property tmpLock.
+    /** rb_ary_modify_check
+     *
      */
-    public void setTmpLock(boolean tmpLock) {
-        this.tmpLock = tmpLock;
+    private final void modifyCheck() {
+        testFrozen("array");
+
+        if (tmpLock) {
+            throw getRuntime().newTypeError("can't modify array during iteration");
+        }
+        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
+            throw getRuntime().newSecurityError("Insecure: can't modify array");
+        }
     }
 
-    public int getLength() {
-        return list.size();
+    /** rb_ary_modify
+     *
+     */
+    private final void modify() {
+        modifyCheck();
+        if (shared) {
+            IRubyObject[] vals = reserve(realLength);
+            shared = false;
+            System.arraycopy(values, begin, vals, 0, realLength);
+            begin = 0;            
+            values = vals;
+        }
     }
 
-    public boolean includes(IRubyObject item) {
-        ThreadContext context = getRuntime().getCurrentContext();
-        for (int i = 0, n = getLength(); i < n; i++) {
-            if (item.callMethod(context, "==", entry(i)).isTrue()) {
-                return true;
+    /*  ================
+     *  Instance Methods
+     *  ================ 
+     */
+
+    /** rb_ary_initialize
+     * 
+     */
+    public IRubyObject initialize(IRubyObject[] args, Block block) {
+        int argc = checkArgumentCount(args, 0, 2);
+        IRuby runtime = getRuntime();
+
+        if (argc == 0) {
+            realLength = 0;
+            if (block.isGiven()) runtime.getWarnings().warn("given block not used");
+
+    	    return this;
+    	}
+
+        if (argc == 1 && !(args[0] instanceof RubyFixnum)) {
+            IRubyObject val = args[0].checkArrayType();
+            if (!val.isNil()) {
+                replace(val);
+                return this;
             }
         }
-        return false;
+
+        long len = RubyNumeric.num2long(args[0]);
+
+        if (len < 0) throw runtime.newArgumentError("negative array size");
+
+        if (len >= Integer.MAX_VALUE) throw runtime.newArgumentError("array size too big");
+
+        int ilen = (int) len;
+
+        modify();
+
+        if (ilen > values.length) values = reserve(ilen);
+
+        if (block.isGiven()) {
+            if (argc == 2) {
+                runtime.getWarnings().warn("block supersedes default value argument");
+            }
+
+            ThreadContext context = runtime.getCurrentContext();
+            for (int i = 0; i < ilen; i++) {
+                store(i, context.yield(new RubyFixnum(runtime, i), block));
+                realLength = i + 1;
+            }
+        } else {
+            Arrays.fill(values, 0, ilen, (argc == 2) ? args[1] : runtime.getNil());
+            realLength = ilen;
+        }
+    	return this;
     }
 
-    public RubyFixnum hash() {
-        return getRuntime().newFixnum(list.hashCode());
+    /** rb_ary_replace
+     *
+     */
+    public IRubyObject replace(IRubyObject orig) {
+        modifyCheck();
+
+        RubyArray origArr = orig.convertToArray();
+
+        if (this == orig) return this;
+
+        origArr.shared = true;
+        values = origArr.values;
+        realLength = origArr.realLength;
+        begin = origArr.begin;
+        shared = true;
+
+        return this;
     }
 
-    /** rb_ary_modify
+    /** rb_ary_to_s
      *
      */
-    public void modify() {
-    	testFrozen("Array");
-        if (isTmpLock()) {
-            throw getRuntime().newTypeError("can't modify array during sort");
-        }
-        if (isTaint() && getRuntime().getSafeLevel() >= 4) {
-            throw getRuntime().newSecurityError("Insecure: can't modify array");
-        }
+    public IRubyObject to_s() {
+        if (realLength == 0) return getRuntime().newString("");
+
+        return join(getRuntime().getGlobalVariables().get("$,"));
+    }
+
+    public boolean includes(IRubyObject item) {
+        ThreadContext context = getRuntime().getCurrentContext();
+        int begin = this.begin;
+        
+        for (int i = begin; i < begin + realLength; i++) {
+            if (item.callMethod(context, "==", values[i]).isTrue()) return true;
+    	}
+        
+        return false;
     }
 
-    /* if list's size is not at least 'toLength', add nil's until it is */
-    private void autoExpand(long toLength) {
-        //list.ensureCapacity((int) toLength);
-        for (int i = getLength(); i < toLength; i++) {
-            list.add(getRuntime().getNil());
+    /** rb_ary_hash
+     * 
+     */
+    public RubyFixnum hash() {
+        int h = realLength;
+
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        int begin = this.begin;
+        for (int i = begin; i < begin + realLength; i++) {
+            h = (h << 1) | (h < 0 ? 1 : 0);
+            h ^= RubyNumeric.num2long(values[i].callMethod(context, "hash"));
         }
+
+        return runtime.newFixnum(h);
     }
 
     /** rb_ary_store
      *
      */
-    private IRubyObject store(long index, IRubyObject value) {
-        modify();
+    private final IRubyObject store(long index, IRubyObject value) {
         if (index < 0) {
-            index += getLength();
+            index += realLength;
             if (index < 0) {
-                throw getRuntime().newIndexError("index " + (index - getLength()) + " out of array");
+                throw getRuntime().newIndexError("index " + (index - realLength) + " out of array");
             }
         }
-        autoExpand(index + 1);
-        list.set((int) index, value);
-        return value;
-    }
 
-    public IRubyObject entry(long offset) {
-    	return entry(offset, false);
-    }
-    
-    /** rb_ary_entry
-     *
-     */
-    public IRubyObject entry(long offset, boolean throwException) {
-        if (getLength() == 0) {
-        	if (throwException) {
-        		throw getRuntime().newIndexError("index " + offset + " out of array");
-        	} 
-        	return getRuntime().getNil();
-        }
-        if (offset < 0) {
-            offset += getLength();
+        modify();
+
+        if (index >= values.length) {
+            long newLength = values.length / 2;
+
+            if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
+
+            newLength += index;
+            if (newLength >= Integer.MAX_VALUE) {
+                throw getRuntime().newArgumentError("index too big");
+            }
+            realloc((int) newLength);
         }
-        if (offset < 0 || getLength() <= offset) {
-        	if (throwException) {
-        		throw getRuntime().newIndexError("index " + offset + " out of array");
-        	} 
-            return getRuntime().getNil();
+        if (index > realLength) {
+            Arrays.fill(values, realLength, (int) index + 1, getRuntime().getNil());
         }
-        return (IRubyObject) list.get((int) offset);
-    }
-    
-    public IRubyObject fetch(IRubyObject[] args, Block block) {
-    	checkArgumentCount(args, 1, 2);
-
-    	RubyInteger index = args[0].convertToInteger();
-    	try {
-    		return entry(index.getLongValue(), true);
-    	} catch (RaiseException e) {
-    		// FIXME: use constant or method for IndexError lookup?
-    		RubyException raisedException = e.getException();
-            
-    		if (raisedException.isKindOf(getRuntime().getClassFromPath("IndexError"))) {
-	    		if (args.length > 1) return args[1];
-	    		if (block.isGiven()) return getRuntime().getCurrentContext().yield(index, block); 
-    		}
-    		
-    		throw e;
-    	}
-    }
-    
-    public IRubyObject insert(IRubyObject[] args) {
-    	checkArgumentCount(args, 1, -1);
-    	// ruby does not bother to bounds check index, if no elements are to be added.
-    	if (args.length == 1) {
-    	    return this;
-    	}
-    	
-    	// too negative of an offset will throw an IndexError
-    	long offset = args[0].convertToInteger().getLongValue();
-    	if (offset < 0 && getLength() + offset < -1) {
-    		throw getRuntime().newIndexError("index " + 
-    				(getLength() + offset) + " out of array");
-    	}
-    	
-    	// An offset larger than the current length will pad with nils
-    	// to length
-    	if (offset > getLength()) {
-    		long difference = offset - getLength();
-    		IRubyObject nil = getRuntime().getNil();
-    		for (long i = 0; i < difference; i++) {
-    			list.add(nil);
-    		}
-    	}
-    	
-    	if (offset < 0) {
-    		offset += getLength() + 1;
-    	}
-    	
-    	for (int i = 1; i < args.length; i++) {
-    		list.add((int) (offset + i - 1), args[i]);
-    	}
-    	
-    	return this;
+        
+        if (index >= realLength) realLength = (int) index + 1;
+
+        values[(int) index] = value;
+        return value;
     }
 
-    public RubyArray transpose() {
-    	RubyArray newArray = getRuntime().newArray();
-    	int length = getLength();
-    	
-    	if (length == 0) {
-    		return newArray;
-    	}
+    /** rb_ary_elt - faster
+     *
+     */
+    private final IRubyObject elt(long offset) {
+        if (realLength == 0 || offset < 0 || offset >= realLength) return getRuntime().getNil();
 
-    	for (int i = 0; i < length; i++) {
-    	    if (!(entry(i) instanceof RubyArray)) {
-    		    throw getRuntime().newTypeError("Some error");
-    	    }
-    	}
-    	
-    	int width = ((RubyArray) entry(0)).getLength();
-
-		for (int j = 0; j < width; j++) {
-    		RubyArray columnArray = getRuntime().newArray(length);
-    		
-			for (int i = 0; i < length; i++) {
-				try {
-				    columnArray.append((IRubyObject) ((RubyArray) entry(i)).list.get(j));
-				} catch (IndexOutOfBoundsException e) {
-					throw getRuntime().newIndexError("element size differ (" + i +
-							" should be " + width + ")");
-				}
-    		}
-			
-			newArray.append(columnArray);
-    	}
-    	
-    	return newArray;
+        return values[begin + (int) offset];
     }
 
-    public IRubyObject values_at(IRubyObject[] args) {
-    	RubyArray newArray = getRuntime().newArray();
-
-    	for (int i = 0; i < args.length; i++) {
-    		IRubyObject o = aref(new IRubyObject[] {args[i]});
-    		if (args[i] instanceof RubyRange) {
-    			if (o instanceof RubyArray) {
-    				for (Iterator j = ((RubyArray) o).getList().iterator(); j.hasNext();) {
-    					newArray.append((IRubyObject) j.next());
-    				}
-    			}
-    		} else {
-    			newArray.append(o);    			
-    		}
-    	}
-    	return newArray;
-    }
-    
-    /** rb_ary_unshift
+    /** rb_ary_entry
      *
      */
-    public RubyArray unshift(IRubyObject item) {
-        modify();
-        list.add(0, item);
-        return this;
+    public final IRubyObject entry(long offset) {
+        if (offset < 0) offset += realLength;
+
+        return elt(offset);
     }
 
-    /** rb_ary_subseq
+    /** rb_ary_fetch
      *
      */
-    public IRubyObject subseq(long beg, long len) {
-        int length = getLength();
+    public IRubyObject fetch(IRubyObject[] args, Block block) {
+        checkArgumentCount(args, 1, 2);
+        IRubyObject pos = args[0];
 
-        if (beg > length || beg < 0 || len < 0) {
-            return getRuntime().getNil();
+        if (block.isGiven() && args.length == 2) {
+            getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
-        if (beg + len > length) {
-            len = length - beg;
+        long index = RubyNumeric.num2long(pos);
+
+        if (index < 0) index += realLength;
+
+        if (index < 0 || index >= realLength) {
+            if (block.isGiven()) return getRuntime().getCurrentContext().yield(pos, block);
+
+            if (args.length == 1) {
+                throw getRuntime().newIndexError("index " + index + " out of array");
+            }
+            
+            return args[1];
         }
-        return len <= 0 ? getRuntime().newArray(0) :
-        	getRuntime().newArray( 
-        			new ArrayList(list.subList((int)beg, (int) (len + beg))));
+        
+        return values[begin + (int) index];
     }
 
-    /** rb_ary_replace
-     *	@todo change the algorythm to make it efficient
-     *			there should be no need to do any deletion or addition
-     *			when the replacing object is an array of the same length
-     *			and in any case we should minimize them, they are costly
+    /** rb_ary_to_ary
+     * 
      */
-    public void replace(long beg, long len, IRubyObject repl) {
-        int length = getLength();
+    private static RubyArray aryToAry(IRubyObject obj) {
+        if (obj instanceof RubyArray) return (RubyArray) obj;
+
+        if (obj.respondsTo("to_ary")) return obj.convertToArray();
+
+        RubyArray arr = new RubyArray(obj.getRuntime(), false); // possibly should not in object space
+        arr.alloc(1);
+        arr.values[0] = obj;
+        arr.realLength = 1;
+        return arr;
+    }
+
+    /** rb_ary_splice
+     * 
+     */
+    private final void splice(long beg, long len, IRubyObject rpl) {
+        long rlen;
+
+        if (len < 0) throw getRuntime().newIndexError("negative length (" + len + ")");
 
-        if (len < 0) {
-            throw getRuntime().newIndexError("Negative array length: " + len);
-        }
         if (beg < 0) {
-            beg += length;
+            beg += realLength;
+            if (beg < 0) {
+                beg -= realLength;
+                throw getRuntime().newIndexError("index " + beg + " out of array");
+            }
         }
-        if (beg < 0) {
-            throw getRuntime().newIndexError("Index out of bounds: " + beg);
+        
+        if (beg + len > realLength) len = realLength - beg;
+
+        RubyArray rplArr;
+        if (rpl == null || rpl.isNil()) {
+            rplArr = null;
+            rlen = 0;
+        } else {
+            rplArr = aryToAry(rpl);
+            rlen = rplArr.realLength;
         }
 
         modify();
 
-        for (int i = 0; beg < getLength() && i < len; i++) {
-            list.remove((int) beg);
-        }
-        autoExpand(beg);
-        if (repl instanceof RubyArray) {
-            List repList = ((RubyArray) repl).getList();
-            //list.ensureCapacity(getLength() + repList.size());
-            list.addAll((int) beg, new ArrayList(repList));
-        } else if (!repl.isNil()) {
-            list.add((int) beg, repl);
+        if (beg >= realLength) {
+            len = beg + rlen;
+            
+            if (len >= values.length) {
+                int tryNewLength = values.length + values.length / 2;
+                
+                realloc(len > tryNewLength ? (int)len : tryNewLength);
+            }
+            
+            Arrays.fill(values, realLength, (int) beg, getRuntime().getNil());
+            if (rlen > 0) {
+                System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, (int) rlen);
+            }
+            realLength = (int) len;
+        } else {
+            long alen;
+
+            if (beg + len > realLength) len = realLength - beg;
+
+            alen = realLength + rlen - len;
+            if (alen >= values.length) {
+                int tryNewLength = values.length + values.length / 2;
+                realloc(alen > tryNewLength ? (int)alen : tryNewLength);
+            }
+            
+            if (len != rlen) {
+                System.arraycopy(values, (int) (beg + len), values, (int) (beg + rlen), realLength - (int) (beg + len));
+                realLength = (int) alen;
+            }
+            
+            if (rlen > 0) {
+                System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, (int) rlen);
+            }
         }
     }
 
-    private boolean flatten(List array) {
-        return flatten(array, new IdentitySet(), null, -1);
-    }
+    /** rb_ary_insert
+     * 
+     */
+    public IRubyObject insert(IRubyObject[] args) {
+        if (args.length == 1) return this;
 
-    private boolean flatten(List array, IdentitySet visited, List toModify, int index) {
-        if (visited.contains(array)) {
-            throw getRuntime().newArgumentError("tried to flatten recursive array");
-        }
-        visited.add(array);
-        boolean isModified = false;
-        for (int i = array.size() - 1; i >= 0; i--) {
-            Object elem = array.get(i);
-            if (elem instanceof RubyArray) {
-                if (toModify == null) { // This is the array to flatten
-                    array.remove(i);
-                    flatten(((RubyArray) elem).getList(), visited, array, i);
-                } else { // Sub-array, recurse
-                    flatten(((RubyArray) elem).getList(), visited, toModify, index);
-                }
-                isModified = true;
-            } else if (toModify != null) { // Add sub-list element to flattened array
-                toModify.add(index, elem);
-            }
+        if (args.length < 1) {
+            throw getRuntime().newArgumentError("wrong number of arguments (at least 1)");
         }
-        visited.remove(array);
-        return isModified;
+
+        long pos = RubyNumeric.num2long(args[0]);
+
+        if (pos == -1) pos = realLength;
+        if (pos < 0) pos++;
+
+        splice(pos, 0, new RubyArray(getRuntime(), 1, args.length - 1, args)); // rb_ary_new4
+        
+        return this;
     }
 
-    //
-    // Methods of the Array Class (rb_ary_*):
-    //
+    public final IRubyObject dup() {
+        return aryDup();
+    }
 
-    /** rb_ary_new2
-     *
+    /** rb_ary_dup
+     * 
      */
-    public static final RubyArray newArray(final IRuby runtime, final long len) {
-        return new RubyArray(runtime, new ArrayList((int) len));
+    private final RubyArray aryDup() {
+        RubyArray dup = new RubyArray(getRuntime(), realLength);
+        dup.setTaint(isTaint()); // from DUP_SETUP
+        // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
+        System.arraycopy(values, begin, dup.values, 0, realLength);
+        dup.realLength = realLength;
+        return dup;
     }
 
-    /** rb_ary_new
-     *
+    /** rb_ary_transpose
+     * 
      */
-    public static final RubyArray newArray(final IRuby runtime) {
-        /* Ruby arrays default to holding 16 elements, so we create an
-         * ArrayList of the same size if we're not told otherwise
-         */
-    	
-        return new RubyArray(runtime, new ArrayList(16));
+    public RubyArray transpose() {
+        RubyArray tmp, result = null;
+
+        int alen = realLength;
+        if (alen == 0) return aryDup();
+    
+        IRuby runtime = getRuntime();
+        int elen = -1;
+        int end = begin + alen;
+        for (int i = begin; i < end; i++) {
+            tmp = elt(i).convertToArray();
+            if (elen < 0) {
+                elen = tmp.realLength;
+                result = new RubyArray(runtime, elen);
+                for (int j = 0; j < elen; j++) {
+                    result.store(j, new RubyArray(runtime, alen));
+                }
+            } else if (elen != tmp.realLength) {
+                throw runtime.newIndexError("element size differs (" + tmp.realLength
+                        + " should be " + elen + ")");
+            }
+            for (int j = 0; j < elen; j++) {
+                ((RubyArray) result.elt(j)).store(i - begin, tmp.elt(j));
+            }
+        }
+        return result;
     }
 
-    /** rb_ary_new
-     *
+    /** rb_values_at (internal)
+     * 
      */
-    public static final RubyArray newArrayLight(final IRuby runtime) {
-        /* Ruby arrays default to holding 16 elements, so we create an
-         * ArrayList of the same size if we're not told otherwise
-         */
-    	
-        return new RubyArray(runtime);
+    private final IRubyObject values_at(long olen, IRubyObject[] args) {
+        RubyArray result = new RubyArray(getRuntime(), args.length);
+
+        for (int i = 0; i < args.length; i++) {
+            if (args[i] instanceof RubyFixnum) {
+                result.append(entry(RubyNumeric.fix2long(args[i])));
+                continue;
+            }
+
+            long beglen[];
+            if (!(args[i] instanceof RubyRange)) {
+            } else if ((beglen = ((RubyRange) args[i]).begLen(olen, 0)) == null) {
+                continue;
+            } else {
+                int beg = (int) beglen[0];
+                int len = (int) beglen[1];
+                int end = begin + len;
+                for (int j = begin; j < end; j++) {
+                    result.append(entry(j + beg));
+                }
+                continue;
+            }
+            result.append(entry(RubyNumeric.num2long(args[i])));
+        }
+
+        return result;
     }
 
-    /**
-     *
+    /** rb_values_at
+     * 
      */
-    public static RubyArray newArray(IRuby runtime, IRubyObject obj) {
-        ArrayList list = new ArrayList(1);
-        list.add(obj);
-        return new RubyArray(runtime, list);
+    public IRubyObject values_at(IRubyObject[] args) {
+        return values_at(realLength, args);
     }
 
-    /** rb_assoc_new
+    /** rb_ary_subseq
      *
      */
-    public static RubyArray newArray(IRuby runtime, IRubyObject car, IRubyObject cdr) {
-        ArrayList list = new ArrayList(2);
-        list.add(car);
-        list.add(cdr);
-        return new RubyArray(runtime, list);
-    }
-
-    public static final RubyArray newArray(final IRuby runtime, final List list) {
-        return new RubyArray(runtime, list);
-    }
+    public IRubyObject subseq(long beg, long len) {
+        if (beg > realLength || beg < 0 || len < 0) return getRuntime().getNil();
 
-    public static RubyArray newArray(IRuby runtime, IRubyObject[] args) {
-        final ArrayList list = new ArrayList(args.length);
-        for (int i = 0; i < args.length; i++) {
-            list.add(args[i]);
+        if (beg + len > realLength) {
+            len = realLength - beg;
+            
+            if (len < 0) len = 0;
         }
-        return new RubyArray(runtime, list);
+        
+        // MRI does klass = rb_obj_class(ary); here, what for ?
+        if (len == 0) return new RubyArray(getRuntime(), 0);
+
+        return makeShared(begin + (int) beg, (int) len);
     }
 
     /** rb_ary_length
      *
      */
     public RubyFixnum length() {
-        return getRuntime().newFixnum(getLength());
+        return getRuntime().newFixnum(realLength);
     }
 
-    /** rb_ary_push_m
+    /** rb_ary_push - specialized rb_ary_store 
      *
      */
-    public RubyArray push(IRubyObject[] items) {
+    public final RubyArray append(IRubyObject item) {
         modify();
-        boolean tainted = false;
-        for (int i = 0; i < items.length; i++) {
-            tainted |= items[i].isTaint();
-            list.add(items[i]);
+        
+        if (realLength == Integer.MAX_VALUE) throw getRuntime().newArgumentError("index too big");
+
+        if (realLength == values.length){
+            long newLength = values.length + values.length / 2;
+            if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
+
+            realloc((int) newLength);
         }
-        setTaint(isTaint() || tainted);
+        values[realLength++] = item;
         return this;
     }
 
-    public RubyArray append(IRubyObject value) {
-        modify();
-        list.add(value);
-        infectBy(value);
+    /** rb_ary_push_m
+     *
+     */
+    public RubyArray push_m(IRubyObject[] items) {
+        for (int i = 0; i < items.length; i++) {
+            append(items[i]);
+        }
+        
         return this;
     }
 
     /** rb_ary_pop
      *
      */
     public IRubyObject pop() {
-        modify();
-        int length = getLength();
-        return length == 0 ? getRuntime().getNil() : 
-        	(IRubyObject) list.remove(length - 1);
+        modifyCheck();
+        
+        if (realLength == 0) return getRuntime().getNil();
+
+        if (!shared) {
+            int index = begin + --realLength;
+            IRubyObject obj = values[index];
+            values[index] = null;
+            return obj;
+        } 
+
+        return values[begin + --realLength];
     }
 
     /** rb_ary_shift
      *
      */
     public IRubyObject shift() {
-        modify();
-        return getLength() == 0 ? getRuntime().getNil() : 
-        	(IRubyObject) list.remove(0);
+        modifyCheck();
+
+        if (realLength == 0) return getRuntime().getNil();
+
+        IRubyObject obj = values[begin];
+
+        if (!shared) shared = true;
+
+        begin++;
+        realLength--;
+
+        return obj;
     }
 
-    /** rb_ary_unshift_m
+    /** rb_ary_unshift
      *
      */
-    public RubyArray unshift(IRubyObject[] items) {
+    public RubyArray unshift(IRubyObject item) {
         modify();
-        boolean taint = false;
-        for (int i = 0; i < items.length; i++) {
-            taint |= items[i].isTaint();
-            list.add(i, items[i]);
+
+        if (realLength == values.length) {
+            long newLength = values.length / 2;
+            if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
+
+            newLength += values.length;
+            realloc((int) newLength);
         }
-        setTaint(isTaint() || taint);
+        System.arraycopy(values, 0, values, 1, realLength);
+
+        realLength++;
+        values[0] = item;
+
+        return this;
+    }
+
+    /** rb_ary_unshift_m
+     *
+     */
+    public RubyArray unshift_m(IRubyObject[] items) {
+        long len = realLength;
+
+        if (items.length == 0) return this;
+
+        store(len + items.length - 1, getRuntime().getNil());
+
+        // it's safe to use zeroes here since modified by store()
+        System.arraycopy(values, 0, values, items.length, (int) len);
+        System.arraycopy(items, 0, values, 0, items.length);
+        
         return this;
     }
 
+    /** rb_ary_includes
+     * 
+     */
     public RubyBoolean include_p(IRubyObject item) {
         return getRuntime().newBoolean(includes(item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     public RubyBoolean frozen() {
-        return getRuntime().newBoolean(isFrozen() || isTmpLock());
-    }
-
-    /** rb_ary_initialize
-     */
-    public IRubyObject initialize(IRubyObject[] args, Block block) {
-        int argc = checkArgumentCount(args, 0, 2);
-        RubyArray arrayInitializer = null;
-        long len = 0;
-        if (argc > 0) {
-        	if (args[0] instanceof RubyArray) {
-        		arrayInitializer = (RubyArray)args[0];
-        	} else {
-        		len = convertToLong(args[0]);
-        	}
-        }
-
-        modify();
-
-        // Array initializer is provided
-        if (arrayInitializer != null) {
-        	list = new ArrayList(arrayInitializer.list);
-        	return this;
-        }
-        
-        // otherwise, continue with Array.new(fixnum, obj)
-        if (len < 0) throw getRuntime().newArgumentError("negative array size");
-        if (len > Integer.MAX_VALUE) throw getRuntime().newArgumentError("array size too big");
-
-        list = new ArrayList((int) len);
-        if (len > 0) {
-        	if (block.isGiven()) {
-                ThreadContext tc = getRuntime().getCurrentContext();
-                
-        		// handle block-based array initialization
-                for (int i = 0; i < len; i++) {
-                    list.add(tc.yield(new RubyFixnum(getRuntime(), i), block));
-                }
-        	} else {
-        		IRubyObject obj = (argc == 2) ? args[1] : getRuntime().getNil();
-        		list.addAll(Collections.nCopies((int)len, obj));
-        	}
-        }
-        return this;
+        return getRuntime().newBoolean(isFrozen() || tmpLock);
     }
 
     /** rb_ary_aref
      */
     public IRubyObject aref(IRubyObject[] args) {
-        int argc = checkArgumentCount(args, 1, 2);
-        if (argc == 2) {
-            long beg = RubyNumeric.fix2long(args[0]);
-            long len = RubyNumeric.fix2long(args[1]);
-            if (beg < 0) {
-                beg += getLength();
+        long beg, len;
+        if (args.length == 2) {
+            if (args[0] instanceof RubySymbol) {
+                throw getRuntime().newTypeError("Symbol as array index");
             }
+            beg = RubyNumeric.num2long(args[0]);
+            len = RubyNumeric.num2long(args[1]);
+
+            if (beg < 0) beg += realLength;
+
             return subseq(beg, len);
         }
-        if (args[0] instanceof RubyFixnum) {
-            return entry(RubyNumeric.fix2long(args[0]));
-        }
-        if (args[0] instanceof RubyBignum) {
-            throw getRuntime().newIndexError("index too big");
-        }
-        if (args[0] instanceof RubyRange) {
-            long[] begLen = ((RubyRange) args[0]).getBeginLength(getLength(), true, false);
 
-            return begLen == null ? getRuntime().getNil() : subseq(begLen[0], begLen[1]);
-        }
-        if(args[0] instanceof RubySymbol) {
-            throw getRuntime().newTypeError("Symbol as array index");
+        if (args.length != 1) checkArgumentCount(args, 1, 1);
+
+        IRubyObject arg = args[0];
+
+        if (arg instanceof RubyFixnum) return entry(RubyNumeric.fix2long(arg));
+        if (arg instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
+
+        long[] beglen;
+        if (!(arg instanceof RubyRange)) {
+        } else if ((beglen = ((RubyRange) arg).begLen(realLength, 0)) == null) {
+            return getRuntime().getNil();
+        } else {
+            beg = beglen[0];
+            len = beglen[1];
+            return subseq(beg, len);
         }
-        return entry(args[0].convertToInteger().getLongValue());
+
+        return entry(RubyNumeric.num2long(arg));
     }
 
     /** rb_ary_aset
      *
      */
     public IRubyObject aset(IRubyObject[] args) {
-        int argc = checkArgumentCount(args, 2, 3);
-        if (argc == 3) {
-            long beg = args[0].convertToInteger().getLongValue();
-            long len = args[1].convertToInteger().getLongValue();
-            replace(beg, len, args[2]);
+        if (args.length == 3) {
+            if (args[0] instanceof RubySymbol) {
+                throw getRuntime().newTypeError("Symbol as array index");
+            }
+            if (args[1] instanceof RubySymbol) {
+                throw getRuntime().newTypeError("Symbol as subarray length");
+            }
+            splice(RubyNumeric.num2long(args[0]), RubyNumeric.num2long(args[1]), args[2]);
             return args[2];
         }
-        if (args[0] instanceof RubyRange) {
-            long[] begLen = ((RubyRange) args[0]).getBeginLength(getLength(), false, true);
-            replace(begLen[0], begLen[1], args[1]);
+
+        if (args.length != 2) {
+            throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + 
+                    " for 2)");
+        }
+        
+        if (args[0] instanceof RubyFixnum) {
+            store(RubyNumeric.fix2long(args[0]), args[1]);
             return args[1];
         }
-        if (args[0] instanceof RubyBignum) {
-            throw getRuntime().newIndexError("Index too large");
+        
+        if (args[0] instanceof RubySymbol) {
+            throw getRuntime().newTypeError("Symbol as array index");
+        }
+
+        if (args[0] instanceof RubyRange) {
+            long[] beglen = ((RubyRange) args[0]).begLen(realLength, 1);
+            splice(beglen[0], beglen[1], args[1]);
+            return args[1];
         }
-        return store(args[0].convertToInteger().getLongValue(), args[1]);
+        
+        store(RubyNumeric.num2long(args[0]), args[1]);
+        return args[1];
     }
 
     /** rb_ary_at
      *
      */
     public IRubyObject at(IRubyObject pos) {
-        return entry(convertToLong(pos));
+        return entry(RubyNumeric.num2long(pos));
     }
 
-	private long convertToLong(IRubyObject pos) {
-		if (pos instanceof RubyNumeric) {
-			return ((RubyNumeric) pos).getLongValue();
-		}
-		throw getRuntime().newTypeError("cannot convert " + pos.getType().getBaseName() + " to Integer");
-	}
-
 	/** rb_ary_concat
      *
      */
     public RubyArray concat(IRubyObject obj) {
-        modify();
-        RubyArray other = obj.convertToArray();
-        list.addAll(other.getList());
-        infectBy(other);
+        RubyArray ary = obj.convertToArray();
+        
+        if (ary.realLength > 0) splice(realLength, 0, ary);
+
         return this;
     }
 
     /** rb_ary_inspect
      *
      */
     public IRubyObject inspect() {
-        if(!getRuntime().registerInspecting(this)) {
-            return getRuntime().newString("[...]");
-        }
+        if (realLength == 0) return getRuntime().newString("[]");
+
+        if (!getRuntime().registerInspecting(this)) return getRuntime().newString("[...]");
+
+        RubyString s;
         try {
-            int length = getLength();
+            StringBuffer buffer = new StringBuffer("[");
+            IRuby runtime = getRuntime();
+            ThreadContext context = runtime.getCurrentContext();
+            boolean tainted = isTaint();
+            for (int i = 0; i < realLength; i++) {
+                s = RubyString.objAsString(values[begin + i].callMethod(context, "inspect"));
+                
+                if (s.isTaint()) tainted = true;
 
-            if (length == 0) {
-                return getRuntime().newString("[]");
-            }
-            RubyString result = getRuntime().newString("[");
-            RubyString separator = getRuntime().newString(", ");
-            ThreadContext context = getRuntime().getCurrentContext();
-            for (int i = 0; i < length; i++) {
-                if (i > 0) {
-                    result.append(separator);
-                }
-                result.append(entry(i).callMethod(context, "inspect"));
+                if (i > 0) buffer.append(", ");
+
+                buffer.append(s.toString());
             }
-            result.cat((byte)']');
-            return result;
+            buffer.append("]");
+            if (tainted) setTaint(true);
+
+            return runtime.newString(buffer.toString());
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_first
      *
      */
     public IRubyObject first(IRubyObject[] args) {
-    	checkArgumentCount(args, 0, 1);
-
     	if (args.length == 0) {
-    		return getLength() == 0 ? getRuntime().getNil() : entry(0);
-    	}
-    	
-    	// TODO: See if enough integer-only conversions to make this
-    	// convenience function (which could replace RubyNumeric#fix2long).
-    	if (!(args[0] instanceof RubyInteger)) {
-            throw getRuntime().newTypeError("Cannot convert " + 
-            		args[0].getType() + " into Integer");
-    	}
-    	
-    	long length = ((RubyInteger)args[0]).getLongValue();
-    	
-    	if (length < 0) {
-    		throw getRuntime().newArgumentError(
-    				"negative array size (or size too big)");
+            if (realLength == 0) return getRuntime().getNil();
+
+            return values[begin];
+        } 
+            
+        checkArgumentCount(args, 0, 1);
+        int n = (int)RubyNumeric.num2long(args[0]);
+        if (n > realLength) {
+            n = realLength;
+        } else if (n < 0) {
+            throw getRuntime().newArgumentError("negative array size (or size too big)");
     	}
     	
-    	return subseq(0, length);
+        return makeShared(begin, n);
     }
 
     /** rb_ary_last
      *
      */
     public IRubyObject last(IRubyObject[] args) {
-        int count = checkArgumentCount(args, 0, 1);
-    	int length = getLength();
-    	
-    	int listSize = list.size();
-    	int sublistSize = 0;
-    	int startIndex = 0;
-    		
-    	switch (count) {
-        case 0:
-            return length == 0 ? getRuntime().getNil() : entry(length - 1);
-        case 1:
-            sublistSize = RubyNumeric.fix2int(args[0]);
-            if (sublistSize == 0) {
-                return getRuntime().newArray();
-            }
-            if (sublistSize < 0) {
-                throw getRuntime().newArgumentError("negative array size (or size too big)");
-            }
+        if (args.length == 0) {
+            if (realLength == 0) return getRuntime().getNil();
+
+            return values[begin + realLength - 1];
+        } 
+            
+        checkArgumentCount(args, 0, 1);
 
-            startIndex = (sublistSize > listSize) ? 0 : listSize - sublistSize;
-            return getRuntime().newArray(list.subList(startIndex, listSize));
-        default:
-            assert false;
-        	return null;
+        int n = (int)RubyNumeric.num2long(args[0]);
+        if (n > realLength) {
+            n = realLength;
+        } else if (n < 0) {
+            throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
+
+        return makeShared(begin + realLength - n, n);
     }
 
     /** rb_ary_each
      *
      */
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
-        for (int i = 0; i < getLength(); i++) {
-            context.yield(entry(i), block);
+        for (int i = begin; i < begin + realLength; i++) {
+            context.yield(values[i], block);
         }
         return this;
     }
 
     /** rb_ary_each_index
      *
      */
     public IRubyObject each_index(Block block) {
-        ThreadContext context = getRuntime().getCurrentContext();
-        for (int i = 0, len = getLength(); i < len; i++) {
-            context.yield(getRuntime().newFixnum(i), block);
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        for (int i = 0; i < realLength; i++) {
+            context.yield(runtime.newFixnum(i), block);
         }
         return this;
     }
 
     /** rb_ary_reverse_each
      *
      */
     public IRubyObject reverse_each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
-        for (long i = getLength(); i > 0; i--) {
-            context.yield(entry(i - 1), block);
+        
+        int len = realLength;
+        
+        while(len-- > 0) {
+            context.yield(values[begin + len], block);
+            
+            if (realLength < len) len = realLength;
         }
+        
         return this;
     }
 
+    private final IRubyObject inspectJoin(IRubyObject sep) {
+        IRubyObject result = join(sep);
+        getRuntime().unregisterInspecting(this);
+        return result;
+    }
+
     /** rb_ary_join
      *
      */
-    public RubyString join(RubyString sep) {
-        StringBuffer buf = new StringBuffer();
-        int length = getLength();
-        if (length == 0) {
-            getRuntime().newString("");
-        }
+    public RubyString join(IRubyObject sep) {
+        if (realLength == 0) return getRuntime().newString("");
+
         boolean taint = isTaint() || sep.isTaint();
-        RubyString str;
-        IRubyObject tmp = null;
-        for (long i = 0; i < length; i++) {
-            tmp = entry(i);
-            taint |= tmp.isTaint();
+
+        long len = 1;
+        for (int i = begin; i < begin + realLength; i++) {            
+            IRubyObject tmp = values[i].checkStringType();
+            len += tmp.isNil() ? 10 : ((RubyString) tmp).getByteList().length();
+        }
+
+        if (!sep.isNil()) len += sep.convertToString().getByteList().length() * (realLength - 1);
+
+        StringBuffer buf = new StringBuffer((int) len);
+        IRuby runtime = getRuntime();
+        for (int i = begin; i < begin + realLength; i++) {
+            IRubyObject tmp = values[i];
             if (tmp instanceof RubyString) {
                 // do nothing
             } else if (tmp instanceof RubyArray) {
-                tmp = ((RubyArray) tmp).to_s_join(sep);
+                if (!runtime.registerInspecting(tmp)) {
+                    tmp = runtime.newString("[...]");
+                } else {
+                    tmp = ((RubyArray) tmp).inspectJoin(sep);
+                }
             } else {
                 tmp = RubyString.objAsString(tmp);
             }
-            
-            if (i > 0 && !sep.isNil()) {
-                buf.append(sep.toString());
-            }
-            buf.append(((RubyString)tmp).toString());
+
+            if (i > begin && !sep.isNil()) buf.append(sep.toString());
+
+            buf.append(((RubyString) tmp).toString());
+            taint |= tmp.isTaint();
         }
-        str = RubyString.newString(getRuntime(), buf.toString());
-        str.setTaint(taint);
-        return str;
+
+        RubyString result = RubyString.newString(runtime, buf.toString());
+        
+        if (taint) result.setTaint(taint);
+
+        return result;
     }
 
     /** rb_ary_join_m
      *
      */
-    public RubyString join(IRubyObject[] args) {
+    public RubyString join_m(IRubyObject[] args) {
         int argc = checkArgumentCount(args, 0, 1);
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
-        return join(sep.isNil() ? getRuntime().newString("") : RubyString.stringValue(sep));
-    }
-
-    /** rb_ary_to_s
-     *
-     */
-    public IRubyObject to_s() {
-        if(!getRuntime().registerInspecting(this)) {
-            return getRuntime().newString("[...]");
-        }
-        try {
-            IRubyObject separatorObject = getRuntime().getGlobalVariables().get("$,");
-            RubyString separator;
-            if (separatorObject.isNil()) {
-                separator = getRuntime().newString("");
-            } else {
-                separator = RubyString.stringValue(separatorObject);
-            }
-            return join(separator);
-        } finally {
-            getRuntime().unregisterInspecting(this);
-        }
-    }
-
-    private IRubyObject to_s_join(RubyString sep) {
-        if(!getRuntime().registerInspecting(this)) {
-            return getRuntime().newString("[...]");
-        }
-        try {
-            return join(sep);
-        } finally {
-            getRuntime().unregisterInspecting(this);
-        }
+        
+        return join(sep);
     }
 
     /** rb_ary_to_a
      *
      */
     public RubyArray to_a() {
         return this;
     }
-    
+
     public IRubyObject to_ary() {
     	return this;
     }
 
+    public RubyArray convertToArray() {
+        return this;
+    }
+
     /** rb_ary_equal
      *
      */
-    public IRubyObject array_op_equal(IRubyObject obj) {
-        if (this == obj) {
-            return getRuntime().getTrue();
-        }
+    public IRubyObject op_equal(IRubyObject obj) {
+        if (this == obj) return getRuntime().getTrue();
 
-        RubyArray ary;
-        
         if (!(obj instanceof RubyArray)) {
-            if (obj.respondsTo("to_ary")) {
-                return obj.callMethod(getRuntime().getCurrentContext(), "==", this);
-            } else {
+            if (!obj.respondsTo("to_ary")) {
                 return getRuntime().getFalse();
+            } else {
+                return obj.callMethod(getRuntime().getCurrentContext(), "==", this);
             }
-        } else {
-        	ary = (RubyArray) obj;
         }
-        
-        int length = getLength();
 
-        if (length != ary.getLength()) {
-            return getRuntime().getFalse();
-        }
+        RubyArray ary = (RubyArray) obj;
+        if (realLength != ary.realLength) return getRuntime().getFalse();
 
-        for (long i = 0; i < length; i++) {
-            if (!entry(i).callMethod(getRuntime().getCurrentContext(), "==", ary.entry(i)).isTrue()) {
-                return getRuntime().getFalse();
-            }
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        for (long i = 0; i < realLength; i++) {
+            if (!elt(i).callMethod(context, "==", ary.elt(i)).isTrue()) return runtime.getFalse();
         }
-        return getRuntime().getTrue();
+        return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     public RubyBoolean eql(IRubyObject obj) {
-        if (!(obj instanceof RubyArray)) {
-            return getRuntime().getFalse();
-        }
-        int length = getLength();
+        if (this == obj) return getRuntime().getTrue();
+        if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
-        
-        if (length != ary.getLength()) {
-            return getRuntime().getFalse();
-        }
-        
-        ThreadContext context = getRuntime().getCurrentContext();
 
-        for (long i = 0; i < length; i++) {
-            if (!entry(i).callMethod(context, "eql?", ary.entry(i)).isTrue()) {
-                return getRuntime().getFalse();
-            }
+        if (realLength != ary.realLength) return getRuntime().getFalse();
+
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        for (long i = 0; i < realLength; i++) {
+            if (!elt(i).callMethod(context, "eql?", ary.elt(i)).isTrue()) return runtime.getFalse();
         }
-        return getRuntime().getTrue();
+        return runtime.getTrue();
     }
 
     /** rb_ary_compact_bang
      *
      */
     public IRubyObject compact_bang() {
         modify();
-        boolean isChanged = false;
-        for (int i = getLength() - 1; i >= 0; i--) {
-            if (entry(i).isNil()) {
-                list.remove(i);
-                isChanged = true;
+
+        int p = 0;
+        int t = 0;
+        int end = p + realLength;
+
+        while (t < end) {
+            if (values[t].isNil()) {
+                t++;
+            } else {
+                values[p++] = values[t++];
             }
         }
-        return isChanged ? (IRubyObject) this : (IRubyObject) getRuntime().getNil();
+
+        if (realLength == p) return getRuntime().getNil();
+
+        realloc(p);
+        realLength = p;
+        return this;
     }
 
     /** rb_ary_compact
      *
      */
     public IRubyObject compact() {
-        RubyArray ary = (RubyArray) dup();
+        RubyArray ary = aryDup();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     public IRubyObject empty_p() {
-        return getLength() == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
+        return realLength == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     public IRubyObject rb_clear() {
-        modify();
-        list.clear();
+        modifyCheck();
+
+        if (ARRAY_DEFAULT_SIZE * 2 < values.length || shared) alloc(ARRAY_DEFAULT_SIZE * 2);
+        
+        begin = 0;
+        shared = false;
+        
+        realLength = 0;
         return this;
     }
 
     /** rb_ary_fill
      *
      */
     public IRubyObject fill(IRubyObject[] args, Block block) {
-        int beg = 0;
-        int len = getLength();
-        int argc;
-        IRubyObject filObj;
-        IRubyObject begObj;
-        IRubyObject lenObj;
-        IRuby runtime = getRuntime();
+        IRubyObject item = null;
+        IRubyObject begObj = null;
+        IRubyObject lenObj = null;
+        int argc = args.length;
+
         if (block.isGiven()) {
-        	argc = checkArgumentCount(args, 0, 2);
-        	filObj = null;
+            checkArgumentCount(args, 0, 2);
+            item = null;
         	begObj = argc > 0 ? args[0] : null;
         	lenObj = argc > 1 ? args[1] : null;
         	argc++;
         } else {
-        	argc = checkArgumentCount(args, 1, 3);
-        	filObj = args[0];
+            checkArgumentCount(args, 1, 3);
+            item = args[0];
         	begObj = argc > 1 ? args[1] : null;
         	lenObj = argc > 2 ? args[2] : null;
         }
+
+        long beg = 0, end = 0, len = 0;
         switch (argc) {
-            case 1 :
+        case 1:
+            beg = 0;
+            len = realLength;
+            break;
+        case 2:
+            if (begObj instanceof RubyRange) {
+                long[] beglen = ((RubyRange) begObj).begLen(realLength, 1);
+                beg = (int) beglen[0];
+                len = (int) beglen[1];
                 break;
-            case 2 :
-                if (begObj instanceof RubyRange) {
-                    long[] begLen = ((RubyRange) begObj).getBeginLength(len, false, true);
-                    beg = (int) begLen[0];
-                    len = (int) begLen[1];
-                    break;
-                }
-                /* fall through */
-            default :
-                beg = begObj.isNil() ? beg : RubyNumeric.fix2int(begObj);
-                if (beg < 0 && (beg += len) < 0) {
-                    throw runtime.newIndexError("Negative array index");
-                }
-                len -= beg;
-                if (argc == 3 && !lenObj.isNil()) {
-                    len = RubyNumeric.fix2int(lenObj);
-                }
+            }
+            /* fall through */
+        case 3:
+            beg = begObj.isNil() ? 0 : RubyNumeric.num2long(begObj);
+            if (beg < 0) {
+                beg = realLength + beg;
+                if (beg < 0) beg = 0;
+            }
+            len = (lenObj == null || lenObj.isNil()) ? realLength - beg : RubyNumeric.num2long(lenObj);
+            break;
         }
 
         modify();
-        autoExpand(beg + len);
-        
-        ThreadContext tc = runtime.getCurrentContext();
 
-        for (int i = beg; i < beg + len; i++) {
-        	if (filObj == null) {
-        		list.set(i, tc.yield(runtime.newFixnum(i), block));
-        	} else {
-        		list.set(i, filObj);
-        	}
+        end = beg + len;
+        if (end > realLength) {
+            if (end >= values.length) realloc((int) end);
+
+            Arrays.fill(values, realLength, (int) end, getRuntime().getNil());
+            realLength = (int) end;
+        }
+
+        if (block.isGiven()) {
+            IRuby runtime = getRuntime();
+            ThreadContext context = runtime.getCurrentContext();
+            for (int i = (int) beg; i < (int) end; i++) {
+                IRubyObject v = context.yield(runtime.newFixnum(i), block);
+                if (i >= realLength) break;
+
+                values[i] = v;
+            }
+        } else {
+            Arrays.fill(values, (int) beg, (int) (beg + len), item);
         }
+        
         return this;
     }
 
     /** rb_ary_index
      *
      */
     public IRubyObject index(IRubyObject obj) {
-        ThreadContext context = getRuntime().getCurrentContext();
-        int len = getLength();
-        for (int i = 0; i < len; i++) {
-            if (entry(i).callMethod(context, "==", obj).isTrue()) {
-                return getRuntime().newFixnum(i);
-            }
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        for (int i = begin; i < begin + realLength; i++) {
+            if (values[i].callMethod(context, "==", obj).isTrue()) return runtime.newFixnum(i - begin);
         }
-        return getRuntime().getNil();
+
+        return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     public IRubyObject rindex(IRubyObject obj) {
-        ThreadContext context = getRuntime().getCurrentContext();
-        for (int i = getLength() - 1; i >= 0; i--) {
-            if (entry(i).callMethod(context, "==", obj).isTrue()) {
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+
+        int i = realLength;
+
+        while (i-- > 0) {
+            if (i > realLength) {
+                i = realLength;
+                continue;
+            }
+            if (values[begin + i].callMethod(context, "==", obj).isTrue()) {
                 return getRuntime().newFixnum(i);
             }
         }
-        return getRuntime().getNil();
+
+        return runtime.getNil();
     }
 
-    public IRubyObject indices(IRubyObject[] args) {
-        IRubyObject[] result = new IRubyObject[args.length];
-        boolean taint = false;
+    /** rb_ary_indexes
+     * 
+     */
+    public IRubyObject indexes(IRubyObject[] args) {
+        getRuntime().getWarnings().warn("Array#indexes is deprecated; use Array#values_at");
+
+        RubyArray ary = new RubyArray(getRuntime(), args.length);
+
+        IRubyObject[] arefArgs = new IRubyObject[1];
         for (int i = 0; i < args.length; i++) {
-            result[i] = entry(RubyNumeric.fix2int(args[i]));
-            taint |= result[i].isTaint();
+            arefArgs[0] = args[i];
+            ary.append(aref(arefArgs));
         }
-        // TODO: Why was is calling create, which used to skip array initialization?
-        IRubyObject ary = ArrayMetaClass.create(getMetaClass(), result, Block.NULL_BLOCK);
-        ary.setTaint(taint);
+
         return ary;
     }
 
     /** rb_ary_reverse_bang
      *
      */
     public IRubyObject reverse_bang() {
         modify();
-        Collections.reverse(list);
+
+        int p1, p2;
+        IRubyObject tmp;
+        if (realLength > 1) {
+            p1 = 0;
+            p2 = p1 + realLength - 1;
+
+            while (p1 < p2) {
+                tmp = values[p1];
+                values[p1++] = values[p2];
+                values[p2--] = tmp;
+            }
+        }
         return this;
     }
 
     /** rb_ary_reverse_m
      *
      */
     public IRubyObject reverse() {
-        RubyArray result = (RubyArray) dup();
-        result.reverse_bang();
-        return result;
+        return aryDup().reverse_bang();
     }
 
     /** rb_ary_collect
      *
      */
     public RubyArray collect(Block block) {
-        if (!block.isGiven()) return (RubyArray) dup();
+        if (!block.isGiven()) return new RubyArray(getRuntime(), realLength, values);
 
-        ThreadContext tc = getRuntime().getCurrentContext();
-        ArrayList ary = new ArrayList();
-        for (int i = 0, len = getLength(); i < len; i++) {
-            ary.add(tc.yield(entry(i), block));
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        RubyArray collect = new RubyArray(runtime, realLength);
+        
+        for (int i = begin; i < begin + realLength; i++) {
+            collect.append(context.yield(values[i], block));
         }
-        return new RubyArray(getRuntime(), ary);
+        
+        return collect;
     }
 
     /** rb_ary_collect_bang
      *
      */
     public RubyArray collect_bang(Block block) {
         modify();
         ThreadContext context = getRuntime().getCurrentContext();
-        for (int i = 0, len = getLength(); i < len; i++) {
-            list.set(i, context.yield(entry(i), block));
+        for (int i = 0, len = realLength; i < len; i++) {
+            store(i, context.yield(values[begin + i], block));
+        }
+        return this;
+    }
+
+    /** rb_ary_select
+     *
+     */
+    public RubyArray select(Block block) {
+        IRuby runtime = getRuntime();
+        RubyArray result = new RubyArray(runtime, realLength);
+
+        ThreadContext context = runtime.getCurrentContext();
+        if (shared) {
+            for (int i = begin; i < begin + realLength; i++) {
+                if (context.yield(values[i], block).isTrue()) result.append(elt(i - begin));
+            }
+        } else {
+            for (int i = 0; i < realLength; i++) {
+                if (context.yield(values[i], block).isTrue()) result.append(elt(i));
+            }
         }
-        return this;
+        return result;
     }
 
     /** rb_ary_delete
      *
      */
-    public IRubyObject delete(IRubyObject obj, Block block) {
-        modify();
-        ThreadContext tc = getRuntime().getCurrentContext();
-        IRubyObject result = getRuntime().getNil();
-        for (int i = getLength() - 1; i >= 0; i--) {
-            if (obj.callMethod(tc, "==", entry(i)).isTrue()) {
-                result = (IRubyObject) list.remove(i);
-            }
+    public IRubyObject delete(IRubyObject item, Block block) {
+        int i2 = 0;
+
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        for (int i1 = 0; i1 < realLength; i1++) {
+            IRubyObject e = values[begin + i1];
+            if (e.callMethod(context, "==", item).isTrue()) continue;
+            if (i1 != i2) store(i2, e);
+            i2++;
         }
-        if (result.isNil() && block.isGiven()) {
-            result = tc.yield(entry(0), block);
+        
+        if (realLength == i2) {
+            if (block.isGiven()) return context.yield(item, block);
+
+            return runtime.getNil();
         }
-        return result;
+
+        modify();
+
+        if (realLength > i2) {
+            realLength = i2;
+            if (i2 * 2 < values.length && values.length > ARRAY_DEFAULT_SIZE) realloc(i2 * 2);
+        }
+        return item;
     }
 
     /** rb_ary_delete_at
      *
      */
-    public IRubyObject delete_at(IRubyObject obj) {
+    private final IRubyObject delete_at(int pos) {
+        int len = realLength;
+
+        if (pos >= len) return getRuntime().getNil();
+
+        if (pos < 0) pos += len;
+
+        if (pos < 0) return getRuntime().getNil();
+
         modify();
-        int pos = (int) obj.convertToInteger().getLongValue();
-        int len = getLength();
-        if (pos >= len) {
-            return getRuntime().getNil();
-        }
-        
-        return pos < 0 && (pos += len) < 0 ?
-            getRuntime().getNil() : (IRubyObject) list.remove(pos);
+
+        IRubyObject obj = values[pos];
+        System.arraycopy(values, pos + 1, values, pos, len - (pos + 1));
+
+        realLength--;
+
+        return obj;
+    }
+
+    /** rb_ary_delete_at_m
+     * 
+     */
+    public IRubyObject delete_at(IRubyObject obj) {
+        return delete_at((int) RubyNumeric.num2long(obj));
+    }
+
+    /** rb_ary_reject_bang
+     * 
+     */
+    public IRubyObject reject(Block block) {
+        RubyArray ary = aryDup();
+        ary.reject_bang(block);
+        return ary;
     }
 
     /** rb_ary_reject_bang
      *
      */
     public IRubyObject reject_bang(Block block) {
+        int i2 = 0;
         modify();
-        IRubyObject retVal = getRuntime().getNil();
+
         ThreadContext context = getRuntime().getCurrentContext();
-        for (int i = getLength() - 1; i >= 0; i--) {
-            if (context.yield(entry(i), block).isTrue()) {
-                retVal = (IRubyObject) list.remove(i);
-            }
+        for (int i1 = 0; i1 < realLength; i1++) {
+            IRubyObject v = values[i1];
+            if (context.yield(v, block).isTrue()) continue;
+
+            if (i1 != i2) store(i2, v);
+            i2++;
         }
-        return retVal.isNil() ? (IRubyObject) retVal : (IRubyObject) this;
+        if (realLength == i2) return getRuntime().getNil();
+
+        if (i2 < realLength) realLength = i2;
+
+        return this;
     }
 
     /** rb_ary_delete_if
      *
      */
     public IRubyObject delete_if(Block block) {
         reject_bang(block);
         return this;
     }
 
-    /** rb_ary_replace
-     *
+    /** rb_ary_zip
+     * 
      */
-    public IRubyObject replace(IRubyObject other) {
-        replace(0, getLength(), other.convertToArray());
-        return this;
+    public IRubyObject zip(IRubyObject[] args, Block block) {
+        for (int i = 0; i < args.length; i++) {
+            args[i] = args[i].convertToArray();
+        }
+
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        if (block.isGiven()) {
+            for (int i = 0; i < realLength; i++) {
+                RubyArray tmp = new RubyArray(runtime, args.length + 1);
+                tmp.append(elt(i));
+                for (int j = 0; j < args.length; j++) {
+                    tmp.append(((RubyArray) args[j]).elt(i));
+                }
+                context.yield(tmp, block);
+            }
+            return runtime.getNil();
+        }
+        
+        int len = realLength;
+        RubyArray result = new RubyArray(runtime, len);
+        for (int i = 0; i < len; i++) {
+            RubyArray tmp = new RubyArray(runtime, args.length + 1);
+            tmp.append(elt(i));
+            for (int j = 0; j < args.length; j++) {
+                tmp.append(((RubyArray) args[j]).elt(i));
+            }
+            result.append(tmp);
+        }
+        return result;
     }
 
     /** rb_ary_cmp
      *
      */
-    public IRubyObject op_cmp(IRubyObject other) {
-        RubyArray ary = other.convertToArray();
-        int otherLen = ary.getLength();
-        int len = getLength();
-        int minCommon = Math.min(len, otherLen);
-        ThreadContext context = getRuntime().getCurrentContext();
-        RubyClass fixnumClass = getRuntime().getClass("Fixnum");
-        for (int i = 0; i < minCommon; i++) {
-        	IRubyObject result = entry(i).callMethod(context, "<=>", ary.entry(i));
-            if (! result.isKindOf(fixnumClass) || RubyFixnum.fix2int(result) != 0) {
-                return result;
-            }
-        }
-        if (len != otherLen) {
-            return len < otherLen ? RubyFixnum.minus_one(getRuntime()) : RubyFixnum.one(getRuntime());
+    public IRubyObject op_cmp(IRubyObject obj) {
+        RubyArray ary2 = obj.convertToArray();
+
+        int len = realLength;
+
+        if (len > ary2.realLength) len = ary2.realLength;
+
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+        for (int i = 0; i < len; i++) {
+            IRubyObject v = elt(i).callMethod(context, "<=>", ary2.elt(i));
+            if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
         }
-        return RubyFixnum.zero(getRuntime());
+        len = realLength - ary2.realLength;
+
+        if (len == 0) return RubyFixnum.zero(runtime);
+        if (len > 0) return RubyFixnum.one(runtime);
+
+        return RubyFixnum.minus_one(runtime);
     }
 
     /** rb_ary_slice_bang
      *
      */
     public IRubyObject slice_bang(IRubyObject[] args) {
-        int argc = checkArgumentCount(args, 1, 2);
-        IRubyObject result = aref(args);
-        if (argc == 2) {
-            long beg = RubyNumeric.fix2long(args[0]);
-            long len = RubyNumeric.fix2long(args[1]);
-            replace(beg, len, getRuntime().getNil());
-        } else if (args[0] instanceof RubyFixnum && RubyNumeric.fix2long(args[0]) < getLength()) {
-            replace(RubyNumeric.fix2long(args[0]), 1, getRuntime().getNil());
-        } else if (args[0] instanceof RubyRange) {
-            long[] begLen = ((RubyRange) args[0]).getBeginLength(getLength(), false, true);
-            replace(begLen[0], begLen[1], getRuntime().getNil());
+        if (checkArgumentCount(args, 1, 2) == 2) {
+            int pos = (int) RubyNumeric.num2long(args[0]);
+            int len = (int) RubyNumeric.num2long(args[1]);
+            
+            if (pos < 0) pos = realLength + pos;
+
+            args[1] = subseq(pos, len);
+            splice(pos, len, null);
+            
+            return args[1];
         }
-        return result;
+        
+        IRubyObject arg = args[0];
+        if (arg instanceof RubyRange) {
+            long[] beglen = ((RubyRange) arg).begLen(realLength, 1);
+            int pos = (int) beglen[0];
+            int len = (int) beglen[1];
+
+            if (pos < 0) {
+                pos = realLength + pos;
+            }
+            arg = subseq(pos, len);
+            splice(pos, len, null);
+            return arg;
+        }
+
+        return delete_at((int) RubyNumeric.num2long(args[0]));
     }
 
     /** rb_ary_assoc
      *
      */
-    public IRubyObject assoc(IRubyObject arg) {
-        for (int i = 0, len = getLength(); i < len; i++) {
-            if (!(entry(i) instanceof RubyArray && ((RubyArray) entry(i)).getLength() > 0)) {
-                continue;
-            }
-            RubyArray ary = (RubyArray) entry(i);
-            if (arg.callMethod(getRuntime().getCurrentContext(), "==", ary.entry(0)).isTrue()) {
-                return ary;
+    public IRubyObject assoc(IRubyObject key) {
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+
+        for (int i = begin; i < begin + realLength; i++) {
+            IRubyObject v = values[i];
+            if (v instanceof RubyArray && ((RubyArray) v).realLength > 0
+                    && ((RubyArray) v).values[0].callMethod(context, "==", key).isTrue()) {
+                return v;
             }
         }
-        return getRuntime().getNil();
+        return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
-    public IRubyObject rassoc(IRubyObject arg) {
-        ThreadContext context = getRuntime().getCurrentContext();
-        
-        for (int i = 0, len = getLength(); i < len; i++) {
-            if (!(entry(i) instanceof RubyArray && ((RubyArray) entry(i)).getLength() > 1)) {
-                continue;
+    public IRubyObject rassoc(IRubyObject value) {
+        IRuby runtime = getRuntime();
+        ThreadContext context = runtime.getCurrentContext();
+
+        for (int i = begin; i < begin + realLength; i++) {
+            IRubyObject v = values[i];
+            if (v instanceof RubyArray && ((RubyArray) v).realLength > 1
+                    && ((RubyArray) v).values[1].callMethod(context, "==", value).isTrue()) {
+                return v;
             }
-            RubyArray ary = (RubyArray) entry(i);
-            if (arg.callMethod(context, "==", ary.entry(1)).isTrue()) {
-                return ary;
+        }
+
+        return runtime.getNil();
+    }
+
+    /** flatten
+     * 
+     */
+    private final long flatten(long index, RubyArray ary2, RubyArray memo) {
+        long i = index;
+        long n;
+        long lim = index + ary2.realLength;
+
+        IRubyObject id = ary2.id();
+
+        if (memo.includes(id)) throw getRuntime().newArgumentError("tried to flatten recursive array");
+
+        memo.append(id);
+        splice(index, 1, ary2);
+        while (i < lim) {
+            IRubyObject tmp = elt(i).checkArrayType();
+            if (!tmp.isNil()) {
+                n = flatten(i, (RubyArray) tmp, memo);
+                i += n;
+                lim += n;
             }
+            i++;
         }
-        return getRuntime().getNil();
+        memo.pop();
+        return lim - index - 1; /* returns number of increased items */
     }
 
     /** rb_ary_flatten_bang
      *
      */
     public IRubyObject flatten_bang() {
-        modify();
-        return flatten(list) ? this : getRuntime().getNil();
+        int i = 0;
+        boolean mod = false;
+        IRubyObject memo = getRuntime().getNil();
+
+        while (i < realLength) {
+            IRubyObject ary2 = values[begin + i];
+            IRubyObject tmp = ary2.checkArrayType();
+            if (!tmp.isNil()) {
+                if (memo.isNil()) memo = new RubyArray(getRuntime());
+
+                i += flatten(i, (RubyArray) tmp, (RubyArray) memo);
+                mod = true;
+            }
+            i++;
+        }
+        if (!mod) return getRuntime().getNil();
+
+        return this;
     }
 
     /** rb_ary_flatten
      *
      */
     public IRubyObject flatten() {
-        RubyArray rubyArray = (RubyArray) dup();
-        rubyArray.flatten_bang();
-        return rubyArray;
+        RubyArray ary = aryDup();
+        ary.flatten_bang();
+        return ary;
     }
 
     /** rb_ary_nitems
      *
      */
     public IRubyObject nitems() {
-        int count = 0;
-        for (int i = 0, len = getLength(); i < len; i++) {
-            count += entry(i).isNil() ? 0 : 1;
+        int n = 0;
+
+        for (int i = begin; i < begin + realLength; i++) {
+            if (!values[i].isNil()) n++;
         }
-        return getRuntime().newFixnum(count);
+        
+        return getRuntime().newFixnum(n++);
     }
 
     /** rb_ary_plus
      *
      */
-    public IRubyObject op_plus(IRubyObject other) {
-        List otherList = other.convertToArray().getList();
-        List newList = new ArrayList(getLength() + otherList.size());
-        newList.addAll(list);
-        newList.addAll(otherList);
-        return new RubyArray(getRuntime(), newList);
+    public IRubyObject op_plus(IRubyObject obj) {
+        RubyArray y = obj.convertToArray();
+        int len = realLength + y.realLength;
+        RubyArray z = new RubyArray(getRuntime(), len);
+        System.arraycopy(values, begin, z.values, 0, realLength);
+        System.arraycopy(y.values, y.begin, z.values, realLength, y.realLength);
+        z.realLength = len;
+        return z;
     }
 
     /** rb_ary_times
      *
      */
-    public IRubyObject op_times(IRubyObject arg) {
-        if (arg instanceof RubyString) {
-            return join((RubyString) arg);
-        }
+    public IRubyObject op_times(IRubyObject times) {
+        IRubyObject tmp = times.checkStringType();
+
+        if (!tmp.isNil()) return join(tmp);
+
+        long len = RubyNumeric.num2long(times);
+        if (len == 0) return new RubyArray(getRuntime(), 0);
+        if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
-        int len = (int) arg.convertToInteger().getLongValue();
-        if (len < 0) {
-            throw getRuntime().newArgumentError("negative argument");
+        if (Long.MAX_VALUE / len < realLength) {
+            throw getRuntime().newArgumentError("argument too big");
         }
-        ArrayList newList = new ArrayList(getLength() * len);
-        for (int i = 0; i < len; i++) {
-            newList.addAll(list);
+
+        len *= realLength;
+
+        RubyArray ary2 = new RubyArray(getRuntime(), len);
+        ary2.realLength = (int) len;
+
+        for (int i = 0; i < len; i += realLength) {
+            System.arraycopy(values, begin, ary2.values, i, realLength);
         }
-        return new RubyArray(getRuntime(), newList);
+
+        ary2.infectBy(this);
+
+        return ary2;
     }
 
-    private static ArrayList uniq(List oldList) {
-        ArrayList newList = new ArrayList(oldList.size());
-        Set passed = new HashSet(oldList.size());
+    /** ary_make_hash
+     * 
+     */
+    private final Set makeSet(RubyArray ary2) {
+        final Set set = new HashSet();
+        int begin = this.begin;
+        for (int i = begin; i < begin + realLength; i++) {
+            set.add(values[i]);
+        }
 
-        for (Iterator iter = oldList.iterator(); iter.hasNext();) {
-            Object item = iter.next();
-            if (! passed.contains(item)) {
-                passed.add(item);
-                newList.add(item);
+        if (ary2 != null) {
+            begin = ary2.begin;            
+            for (int i = begin; i < begin + ary2.realLength; i++) {
+                set.add(ary2.values[i]);
             }
         }
-        newList.trimToSize();
-        return newList;
+        return set;
     }
 
     /** rb_ary_uniq_bang
      *
      */
     public IRubyObject uniq_bang() {
-        modify();
-        ArrayList newList = uniq(list);
-        if (newList.equals(list)) {
-            return getRuntime().getNil();
+        Set set = makeSet(null);
+
+        if (realLength == set.size()) return getRuntime().getNil();
+
+        int j = 0;
+        for (int i = 0; i < realLength; i++) {
+            IRubyObject v = elt(i);
+            if (set.remove(v)) store(j++, v);
         }
-        list = newList;
+        realLength = j;
         return this;
     }
 
     /** rb_ary_uniq
      *
      */
     public IRubyObject uniq() {
-        return new RubyArray(getRuntime(), uniq(list));
+        RubyArray ary = aryDup();
+        ary.uniq_bang();
+        return ary;
     }
 
     /** rb_ary_diff
      *
      */
     public IRubyObject op_diff(IRubyObject other) {
-        List ary1 = new ArrayList(list);
-        List ary2 = other.convertToArray().getList();
-        int len2 = ary2.size();
-        ThreadContext context = getRuntime().getCurrentContext();
-        
-        for (int i = ary1.size() - 1; i >= 0; i--) {
-            IRubyObject obj = (IRubyObject) ary1.get(i);
-            for (int j = 0; j < len2; j++) {
-                if (obj.callMethod(context, "==", (IRubyObject) ary2.get(j)).isTrue()) {
-                    ary1.remove(i);
-                    break;
-                }
-            }
+        Set set = other.convertToArray().makeSet(null);
+        RubyArray ary3 = new RubyArray(getRuntime());
+
+        int begin = this.begin;
+        for (int i = begin; i < begin + realLength; i++) {
+            if (set.contains(values[i])) continue;
+
+            ary3.append(elt(i - begin));
         }
-        return new RubyArray(getRuntime(), ary1);
+
+        return ary3;
     }
 
     /** rb_ary_and
      *
      */
     public IRubyObject op_and(IRubyObject other) {
-    	RubyClass arrayClass = getRuntime().getClass("Array");
-    	
-    	// & only works with array types
-    	if (!other.isKindOf(arrayClass)) {
-    		throw getRuntime().newTypeError(other, arrayClass);
-    	}
-        List ary1 = uniq(list);
-        int len1 = ary1.size();
-        List ary2 = other.convertToArray().getList();
-        int len2 = ary2.size();
-        ArrayList ary3 = new ArrayList(len1);
-        ThreadContext context = getRuntime().getCurrentContext();
-        
-        for (int i = 0; i < len1; i++) {
-            IRubyObject obj = (IRubyObject) ary1.get(i);
-            for (int j = 0; j < len2; j++) {
-                if (obj.callMethod(context, "eql?", (IRubyObject) ary2.get(j)).isTrue()) {
-                    ary3.add(obj);
-                    break;
-                }
-            }
+        RubyArray ary2 = other.convertToArray();
+        Set set = ary2.makeSet(null);
+        RubyArray ary3 = new RubyArray(getRuntime(), 
+                realLength < ary2.realLength ? realLength : ary2.realLength);
+
+        for (int i = 0; i < realLength; i++) {
+            IRubyObject v = elt(i);
+            if (set.remove(v)) ary3.append(v);
         }
-        ary3.trimToSize();
-        return new RubyArray(getRuntime(), ary3);
+
+        return ary3;
     }
 
     /** rb_ary_or
      *
      */
     public IRubyObject op_or(IRubyObject other) {
-        List newArray = new ArrayList(list);
-        
-        newArray.addAll(other.convertToArray().getList());
-        
-        return new RubyArray(getRuntime(), uniq(newArray));
+        RubyArray ary2 = other.convertToArray();
+        Set set = makeSet(ary2);
+
+        RubyArray ary3 = new RubyArray(getRuntime(), realLength + ary2.realLength);
+
+        for (int i = 0; i < realLength; i++) {
+            IRubyObject v = elt(i);
+            if (set.remove(v)) ary3.append(v);
+        }
+        for (int i = 0; i < ary2.realLength; i++) {
+            IRubyObject v = ary2.elt(i);
+            if (set.remove(v)) ary3.append(v);
+        }
+        return ary3;
     }
 
     /** rb_ary_sort
      *
      */
     public RubyArray sort(Block block) {
-        RubyArray rubyArray = (RubyArray) dup();
-        rubyArray.sort_bang(block);
-        return rubyArray;
+        RubyArray ary = aryDup();
+        ary.sort_bang(block);
+        return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
-    public IRubyObject sort_bang(Block block) {
+    public RubyArray sort_bang(Block block) {
         modify();
-        setTmpLock(true);
+        if (realLength > 1) {
+            tmpLock = true;
+            try {
+                if (block.isGiven()) {
+                    Arrays.sort(values, 0, realLength, new BlockComparator(block));
+                } else {
+                    Arrays.sort(values, 0, realLength, new DefaultComparator());
+                }
+            } finally {
+                tmpLock = false;
+            }
+        }
+        return this;
+    }
 
-        Comparator comparator;
-        if (block.isGiven()) {
-            comparator = new BlockComparator(block);
-        } else {
-            comparator = new DefaultComparator();
+    final class BlockComparator implements Comparator {
+        private Block block;
+
+        public BlockComparator(Block block) {
+            this.block = block;
         }
-        Collections.sort(list, comparator);
 
-        setTmpLock(false);
-        return this;
+        public int compare(Object o1, Object o2) {
+            ThreadContext context = getRuntime().getCurrentContext();
+            IRubyObject obj1 = (IRubyObject) o1;
+            IRubyObject obj2 = (IRubyObject) o2;
+            IRubyObject ret = block.yield(context, getRuntime().newArray(obj1, obj2), null, null, true);
+            int n = RubyComparable.cmpint(ret, obj1, obj2);
+            //TODO: ary_sort_check should be done here
+            return n;
+        }
+    }
+
+    final class DefaultComparator implements Comparator {
+        public int compare(Object o1, Object o2) {
+            if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
+                long a = ((RubyFixnum) o1).getLongValue();
+                long b = ((RubyFixnum) o2).getLongValue();
+                if (a > b) return 1;
+                if (a < b) return -1;
+                return 0;
+            }
+            if (o1 instanceof RubyString && o2 instanceof RubyString) {
+                return ((RubyString) o1).cmp((RubyString) o2);
+            }
+
+            IRubyObject obj1 = (IRubyObject) o1;
+            IRubyObject obj2 = (IRubyObject) o2;
+
+            IRubyObject ret = obj1.callMethod(obj1.getRuntime().getCurrentContext(), "<=>", obj2);
+            int n = RubyComparable.cmpint(ret, obj1, obj2);
+            //TODO: ary_sort_check should be done here
+            return n;
+        }
     }
 
     public static void marshalTo(RubyArray array, MarshalStream output) throws IOException {
         output.writeInt(array.getList().size());
-        for (Iterator iter = array.getList().iterator(); iter.hasNext(); ) {
+        for (Iterator iter = array.getList().iterator(); iter.hasNext();) {
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
-	RubyString iFmt = RubyString.objAsString(obj);
-        return Pack.pack(getRuntime(), this.list, iFmt.getBytes());
-    }
-
-    class BlockComparator implements Comparator {
-        private Block block;
-        
-        public BlockComparator(Block block) {
-            this.block = block;
-        }
-        
-        public int compare(Object o1, Object o2) {
-            ThreadContext context = getRuntime().getCurrentContext();
-            IRubyObject result = block.yield(context, getRuntime().newArray((IRubyObject) o1, (IRubyObject) o2), null, null, true);
-            return (int) ((RubyNumeric) result).getLongValue();
-        }
-    }
-
-    static class DefaultComparator implements Comparator {
-        public int compare(Object o1, Object o2) {
-            IRubyObject obj1 = (IRubyObject) o1;
-            IRubyObject obj2 = (IRubyObject) o2;
-            if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
-            	long diff = RubyNumeric.fix2long(obj1) - RubyNumeric.fix2long(obj2);
-
-            	return diff < 0 ? -1 : diff > 0 ? 1 : 0;
-            }
-
-            if (o1 instanceof RubyString && o2 instanceof RubyString) {
-                return RubyNumeric.fix2int(
-                        ((RubyString)o1).op_cmp((RubyString)o2));
-            }
-
-            return RubyNumeric.fix2int(obj1.callMethod(obj1.getRuntime().getCurrentContext(), "<=>", obj2));
-        }
+        RubyString iFmt = RubyString.objAsString(obj);
+        return Pack.pack(getRuntime(), Arrays.asList(toJavaArray()), iFmt.getBytes());
     }
 
-
     public Class getJavaClass() {
         return List.class;
     }
-    
+
     // Satisfy java.util.List interface (for Java integration)
-    
+
 	public int size() {
-		return list.size();
+        return realLength;
 	}
 
 	public boolean isEmpty() {
-		return list.isEmpty();
+        return realLength == 0;
 	}
 
 	public boolean contains(Object element) {
-		return list.contains(JavaUtil.convertJavaToRuby(getRuntime(), element));
-	}
-
-	public Iterator iterator() {
-		return new ConversionIterator(list.iterator());
+        return indexOf(element) != -1;
 	}
 
 	public Object[] toArray() {
-		Object[] array = new Object[getLength()];
-		Iterator iter = iterator();
-		
-		for (int i = 0; iter.hasNext(); i++) {
-			array[i] = iter.next();
-		}
-
+        Object[] array = new Object[realLength];
+        for (int i = begin; i < realLength; i++) {
+            array[i - begin] = JavaUtil.convertRubyToJava(values[i]);
+        }
 		return array;
 	}
 
 	public Object[] toArray(final Object[] arg) {
         Object[] array = arg;
-        int length = getLength();
-            
-        if(array.length < length) {
+        if (array.length < realLength) {
             Class type = array.getClass().getComponentType();
-            array = (Object[])Array.newInstance(type, length);
+            array = (Object[]) Array.newInstance(type, realLength);
         }
+        int length = realLength - begin;
 
-        Iterator iter = iterator();
-        for (int i = 0; iter.hasNext(); i++) {
-            array[i] = iter.next();
+        for (int i = 0; i < length; i++) {
+           array[i] = JavaUtil.convertRubyToJava(values[i + begin]); 
         }
-        
         return array;
 	}
 
 	public boolean add(Object element) {
-		return list.add(JavaUtil.convertJavaToRuby(getRuntime(), element));
+        append(JavaUtil.convertJavaToRuby(getRuntime(), element));
+        return true;
 	}
 
 	public boolean remove(Object element) {
-		return list.remove(JavaUtil.convertJavaToRuby(getRuntime(), element));
+        IRubyObject deleted = delete(JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
+        return deleted.isNil() ? false : true; // TODO: is this correct ?
 	}
 
 	public boolean containsAll(Collection c) {
 		for (Iterator iter = c.iterator(); iter.hasNext();) {
-			if (indexOf(iter.next()) == -1) {
-				return false;
-			}
+			if (indexOf(iter.next()) == -1) return false;
 		}
-
+        
 		return true;
 	}
 
 	public boolean addAll(Collection c) {
-		for (Iterator iter = c.iterator(); iter.hasNext(); ) {
+        for (Iterator iter = c.iterator(); iter.hasNext();) {
 			add(iter.next());
 		}
-
 		return !c.isEmpty();
 	}
 
 	public boolean addAll(int index, Collection c) {
 		Iterator iter = c.iterator();
 		for (int i = index; iter.hasNext(); i++) {
 			add(i, iter.next());
 		}
-
 		return !c.isEmpty();
 	}
 
 	public boolean removeAll(Collection c) {
-		boolean changed = false;
-		
+        boolean listChanged = false;
 		for (Iterator iter = c.iterator(); iter.hasNext();) {
 			if (remove(iter.next())) {
-				changed = true;
+                listChanged = true;
 			}
 		}
-
-		return changed;
+        return listChanged;
 	}
 
 	public boolean retainAll(Collection c) {
 		boolean listChanged = false;
-		
+
 		for (Iterator iter = iterator(); iter.hasNext();) {
 			Object element = iter.next();
 			if (!c.contains(element)) {
 				remove(element);
 				listChanged = true;
 			}
 		}
-
 		return listChanged;
 	}
 
 	public Object get(int index) {
-		return JavaUtil.convertRubyToJava((IRubyObject) list.get(index), Object.class);
+        return JavaUtil.convertRubyToJava((IRubyObject) elt(index), Object.class);
 	}
 
 	public Object set(int index, Object element) {
-		return list.set(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
+        return store(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
 	}
 
+    // TODO: make more efficient by not creating IRubyArray[]
 	public void add(int index, Object element) {
-		list.add(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
+        insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element) });
 	}
 
 	public Object remove(int index) {
-		return JavaUtil.convertRubyToJava((IRubyObject) list.remove(index), Object.class);
+        return JavaUtil.convertRubyToJava(delete_at(index), Object.class);
 	}
 
 	public int indexOf(Object element) {
-		return list.indexOf(JavaUtil.convertJavaToRuby(getRuntime(), element));
-	}
+        int begin = this.begin;
+        
+        if (element == null) {
+            for (int i = begin; i < begin + realLength; i++) {
+                if (values[i] == null) return i;
+            }
+        } else {
+            IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
+            
+            for (int i = begin; i < begin + realLength; i++) {
+                if (convertedElement.equals(values[i])) return i;
+            }
+        }
+        return -1;
+    }
 
 	public int lastIndexOf(Object element) {
-		return list.lastIndexOf(JavaUtil.convertJavaToRuby(getRuntime(), element));
-	}
+        int begin = this.begin;
 
-	public ListIterator listIterator() {
-		return new ConversionListIterator(list.listIterator());
-	}
+        if (element == null) {
+            for (int i = begin + realLength - 1; i >= begin; i--) {
+                if (values[i] == null) return i;
+            }
+        } else {
+            IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
+            
+            for (int i = begin + realLength - 1; i >= begin; i--) {
+                if (convertedElement.equals(values[i])) return i;
+            }
+        }
+        
+        return -1;
+    }
 
-	public ListIterator listIterator(int index) {
-		return new ConversionListIterator(list.listIterator(index));
-	}
+    public class RubyArrayConversionIterator implements Iterator {
+        protected int index = 0;
+        protected int last = -1;
 
-	// TODO: list.subList(from, to).clear() is supposed to clear the sublist from the list.
-	// How can we support this operation?
-	public List subList(int fromIndex, int toIndex) {
-		if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
-			throw new IndexOutOfBoundsException();
+        public boolean hasNext() {
+            return index < realLength;
+        }
 
-		}
-		IRubyObject subList = subseq(fromIndex, toIndex - fromIndex + 1);
-		
-		return subList.isNil() ? null : (List) subList;  
-	}
+        public Object next() {
+            IRubyObject element = elt(index);
+            last = index++;
+            return JavaUtil.convertRubyToJava(element, Object.class);
+        }
 
-	public void clear() {
-		list.clear();
-	}
+        public void remove() {
+            if (last == -1) throw new IllegalStateException();
 
-	class ConversionListIterator implements ListIterator {
-		private ListIterator iterator;
+            delete_at(last);
+            if (last < index) index--;
 
-		public ConversionListIterator(ListIterator iterator) {
-			this.iterator = iterator;
-		}
+            last = -1;
+	
+        }
+    }
 
-		public boolean hasNext() {
-			return iterator.hasNext();
-		}
+    public Iterator iterator() {
+        return new RubyArrayConversionIterator();
+    }
 
-		public Object next() {
-			return JavaUtil.convertRubyToJava((IRubyObject) iterator.next(), Object.class);
+    final class RubyArrayConversionListIterator extends RubyArrayConversionIterator implements ListIterator {
+        public RubyArrayConversionListIterator() {
+        }
+
+        public RubyArrayConversionListIterator(int index) {
+            this.index = index;
 		}
 
 		public boolean hasPrevious() {
-			return iterator.hasPrevious();
+            return index >= 0;
 		}
 
 		public Object previous() {
-			return JavaUtil.convertRubyToJava((IRubyObject) iterator.previous(), Object.class);
+            return JavaUtil.convertRubyToJava((IRubyObject) elt(last = --index), Object.class);
 		}
 
 		public int nextIndex() {
-			return iterator.nextIndex();
+            return index;
 		}
 
 		public int previousIndex() {
-			return iterator.previousIndex();
+            return index - 1;
 		}
 
-		public void remove() {
-			iterator.remove();
-		}
+        public void set(Object obj) {
+            if (last == -1) throw new IllegalStateException();
 
-		public void set(Object arg0) {
-			// TODO Auto-generated method stub
-		}
+            store(last, JavaUtil.convertJavaToRuby(getRuntime(), obj));
+        }
 
-		public void add(Object arg0) {
-			// TODO Auto-generated method stub
+        public void add(Object obj) {
+            insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToRuby(getRuntime(), obj) });
+            last = -1;
 		}
+    }
+
+    public ListIterator listIterator() {
+        return new RubyArrayConversionListIterator();
+    }
+
+    public ListIterator listIterator(int index) {
+        return new RubyArrayConversionListIterator(index);
 	}
+
+    // TODO: list.subList(from, to).clear() is supposed to clear the sublist from the list.
+    // How can we support this operation?
+    public List subList(int fromIndex, int toIndex) {
+        if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
+            throw new IndexOutOfBoundsException();
+        }
+        
+        IRubyObject subList = subseq(fromIndex, toIndex - fromIndex + 1);
+
+        return subList.isNil() ? null : (List) subList;
+    }
+
+    public void clear() {
+        rb_clear();
+    }
 }
