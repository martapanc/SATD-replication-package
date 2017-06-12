diff --git a/.gitignore b/.gitignore
index 1b632e65ef..b5e76c1b3f 100644
--- a/.gitignore
+++ b/.gitignore
@@ -1,66 +1,67 @@
 !.gitignore
 *.gz
 *.zip
 *.md5
 *.sha1
 jruby-complete.jar
 bin/*
 !bin/gem
 !bin/update_rubygems
 build
 build.eclipse
 build.properties
 target
 dist
 lib/jruby*.jar
 lib/native
 lib/ruby/gems/1.8/source_cache
 lib/ruby/gems/1.8/doc
 lib/ruby/gems/1.8/gems
 lib/ruby/gems/1.8/specifications
 lib/ruby/gems/1.8/cache
 lib/ruby/gems/1.8/bin
 lib/ruby/gems/1.9/source_cache
 lib/ruby/gems/1.9/doc
 lib/ruby/gems/1.9/gems
 lib/ruby/gems/1.9/specifications
 lib/ruby/gems/1.9/cache
 lib/ruby/gems/1.9/bin
 lib/ruby/site_ruby/1.8/rubygems/defaults/jruby_native.rb
 nbproject/private
 share
 latest_source_cache
 src_gen
 docs/api
 spec/ruby
 test/rails
 test/prawn
 gem/lib/*.jar
 gem/lib/jruby-jars/version.rb
 gem/pkg
 *.orig
 *.rej
 *~
 tool/nailgun/Makefile
 tool/nailgun/ng
 tool/nailgun/config.log
 tool/nailgun/config.status
 jruby-findbugs.html
 .debug.properties
 test/testapp/testapp
 glob_test
 spaces test
 .DS_Store
 lib/ruby/cext/
 *.so
 *.bundle
 *.[oa]
 dev_null
 .redcar
 /.externalToolBuilders/
 /.settings/
 .metadata
 .idea/workspace.xml
 shared.iml
 build_graph.png
-/.idea/
\ No newline at end of file
+/.idea/
+/.rbx/
\ No newline at end of file
diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index b3bc436ce1..e9f9c38bbe 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,2484 +1,2490 @@
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
+import org.jcodings.Encoding;
+import org.jcodings.specific.USASCIIEncoding;
 
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
 import org.jruby.runtime.MethodIndex;
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
+    public static final int DEFAULT_INSPECT_STR_SIZE = 10;
 
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
 
     public RArray getRArray() {
         return (RArray)getMetaClass().getRealClass().getNativeHandleAccessorForRead().get(this);
     }
     
     public final void setRArray(RArray rarray) {
         setRArray(getMetaClass().getRealClass().getNativeHandleAccessorForWrite().getIndex(), rarray);
     }
 
     private void setRArray(int index, RArray value) {
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
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
-        ByteList buffer = new ByteList();
-        buffer.append('[');
+        Encoding encoding = context.runtime.getDefaultInternalEncoding();
+        if (encoding == null) encoding = USASCIIEncoding.INSTANCE;
+        RubyString str = RubyString.newStringLight(context.runtime, DEFAULT_INSPECT_STR_SIZE, encoding);
+        str.cat((byte)'[');
         boolean tainted = isTaint();
         boolean untrust = isUntrusted();
 
         for (int i = 0; i < realLength; i++) {
-            if (i > 0) buffer.append(',').append(' ');
+            if (i > 0) str.cat((byte)',').cat((byte)' ');
 
-            RubyString str = inspect(context, safeArrayRef(values, begin + i));
-            if (str.isTaint()) tainted = true;
-            if (str.isUntrusted()) untrust = true;
-            buffer.append(str.getByteList());
+            RubyString str2 = inspect(context, safeArrayRef(values, begin + i));
+            if (str2.isTaint()) tainted = true;
+            if (str2.isUntrusted()) untrust = true;
+            
+            // safe for both 1.9 and 1.8
+            str.cat19(str2);
         }
-        buffer.append(']');
+        str.cat((byte)']');
 
-        RubyString str = getRuntime().newString(buffer);
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
 
     // 1.9 MRI: join0
     private RubyString joinStrings(RubyString sep, int max, RubyString result) {
         try {
             for(int i = begin; i < max; i++) {
                 if (i > begin && sep != null) result.append19(sep);
                 result.append19(values[i]);
             }
         } catch (ArrayIndexOutOfBoundsException e) {
             concurrentModification();
         }
         
         return result;
     }
 
     // 1.9 MRI: join1
     private RubyString joinAny(ThreadContext context, IRubyObject obj, RubyString sep, 
             int i, RubyString result) {
         RubyClass stringClass = context.getRuntime().getString();
         RubyClass arrayClass = context.getRuntime().getArray();
 
         for (; i < begin + realLength; i++) {
             if (i > begin && sep != null) result.append19(sep);
 
             IRubyObject val = safeArrayRef(values, i);
 
             if (val instanceof RubyString) {
                 result.append19(val);
             } else if (val instanceof RubyArray) {
                 obj = val;
                 recursiveJoin(context, obj, sep, result, val);
             } else {
                 IRubyObject tmp = val.checkStringType19();
                 if (tmp.isNil()) tmp = TypeConverter.convertToTypeWithCheck(val, stringClass, "to_s");
 
                 if (!tmp.isNil()) {
                     result.append19(tmp);
                 } else {
                     tmp = TypeConverter.convertToTypeWithCheck(val, arrayClass, "to_a");
                     if(!tmp.isNil()) {
                         obj = val;
                         recursiveJoin(context, obj, sep, result, tmp);
                     } else {
                         result.append19(RubyString.objAsString(context, val));
                     }
                 }
             }
         }
         
         return result;
     }
 
     private void recursiveJoin(final ThreadContext context, final IRubyObject outValue,
             final RubyString sep, final RubyString result, final IRubyObject ary) {
         final Ruby runtime = context.getRuntime();
         
         if (ary == this) throw runtime.newArgumentError("recursive array join");
             
         runtime.execRecursive(new Ruby.RecursiveFunction() {
             public IRubyObject call(IRubyObject obj, boolean recur) {
                 if (recur) throw runtime.newArgumentError("recursive array join");
                             
                 ((RubyArray) ary).joinAny(context, outValue, sep, 0, result);
                 
                 return runtime.getNil();
             }}, outValue);
     }
 
     /** rb_ary_join
      *
      */
     @JRubyMethod(name = "join", compat = RUBY1_9)
     public IRubyObject join19(ThreadContext context, IRubyObject sep) {
         final Ruby runtime = context.getRuntime();
         if (realLength == 0) return RubyString.newEmptyString(runtime);
 
         int len = 1;
         RubyString sepString = null;
         if (!sep.isNil()) {
             sepString = sep.convertToString();
             len += sepString.size() * (realLength - 1);
         }
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject val = safeArrayRef(values, i);
             IRubyObject tmp = val.checkStringType19();
             if (tmp.isNil() || tmp != val) {
                 len += ((begin + realLength) - i) * 10;
                 RubyString result = (RubyString) RubyString.newStringLight(runtime, len).infectBy(this);
 
                 return joinAny(context, this, sepString, i, joinStrings(sepString, i, result));
             }
 
             len += ((RubyString) tmp).getByteList().length();
         }
 
         return joinStrings(sepString, begin + realLength, 
                 (RubyString) RubyString.newStringLight(runtime, len).infectBy(this));
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
         return RecursiveComparator.compare(context, MethodIndex.OP_EQUAL, this, obj);
     }
 
     public RubyBoolean compare(ThreadContext context, int method, IRubyObject other) {
 
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
             if (!invokedynamic(context, elt(i), method, ary.elt(i)).isTrue()) {
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
         return RecursiveComparator.compare(context, MethodIndex.EQL, this, obj);
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
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index b0337514e6..57cd112ae9 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1732 +1,1732 @@
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
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
-import org.jruby.util.ByteList;
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
+    public static final int DEFAULT_INSPECT_STR_SIZE = 20;
 
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
-        final ByteList buffer = new ByteList();
-        buffer.append('{');
+        final RubyString str = RubyString.newStringLight(context.runtime, DEFAULT_INSPECT_STR_SIZE);
+        str.cat((byte)'{');
         final boolean[] firstEntry = new boolean[1];
 
         firstEntry[0] = true;
         visitAll(new Visitor() {
             public void visit(IRubyObject key, IRubyObject value) {
-                if (!firstEntry[0]) buffer.append(',').append(' ');
+                if (!firstEntry[0]) str.cat((byte)',').cat((byte)' ');
 
-                buffer.append(inspect(context, key).getByteList());
-                buffer.append('=').append('>');
-                buffer.append(inspect(context, value).getByteList());
+                str.cat19(inspect(context, key));
+                str.cat((byte)'=').cat((byte)'>');
+                str.cat19(inspect(context, value));
                 firstEntry[0] = false;
             }
         });
-        buffer.append('}');
-        return getRuntime().newString(buffer);
+        str.cat((byte)'}');
+        return str;
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
 
     public RubyBoolean compare(final ThreadContext context, final int method, IRubyObject other) {
 
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
 
                     if (!invokedynamic(context, value, method, value2).isTrue()) {
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
         return RecursiveComparator.compare(context, MethodIndex.OP_EQUAL, this, other);
     }
 
     /** rb_hash_eql
      * 
      */
     @JRubyMethod(name = "eql?")
     public IRubyObject op_eql19(final ThreadContext context, IRubyObject other) {
         return RecursiveComparator.compare(context, MethodIndex.EQL, this, other);
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
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index bb3da54a55..5f168c1371 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -104,2004 +104,2007 @@ import org.jruby.util.string.JavaCrypt;
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.OP_EQUAL;
 import static org.jruby.runtime.MethodIndex.OP_CMP;
 
 /**
  * Implementation of Ruby String class
  * 
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="String", include={"Enumerable", "Comparable"})
 public class RubyString extends RubyObject implements EncodingCapable {
 
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
 
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.setReifiedClass(RubyString.class);
         stringClass.kindOf = new RubyModule.KindOf() {
             @Override
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyString;
                 }
             };
 
         stringClass.includeModule(runtime.getComparable());
         if (!runtime.is1_9()) stringClass.includeModule(runtime.getEnumerable());
         stringClass.defineAnnotatedMethods(RubyString.class);
 
         return stringClass;
     }
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyString.newEmptyString(runtime, klass);
         }
     };
 
     public Encoding getEncoding() {
         return value.getEncoding();
     }
 
     public void setEncoding(Encoding encoding) {
         value.setEncoding(encoding);
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
 
     public final RString getRString() {
         return (RString)getMetaClass().getRealClass().getNativeHandleAccessorForRead().get(this);
     }
     
     public final void setRString(RString rstring) {
         setRString(getMetaClass().getRealClass().getNativeHandleAccessorForWrite().getIndex(), rstring);
     }
 
     private void setRString(int index, RString value) {
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
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
 
     final Encoding checkEncoding(RubyString other) {
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
 
     final int strLength() {
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
         Ruby runtime = getRuntime();
         if (getMetaClass() != runtime.getString() || getMetaClass() != other.getMetaClass()) return super.eql(other);
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
         byte[] bytes = RubyEncoding.encodeUTF8(value);
         this.value = new ByteList(bytes, false);
         this.value.setEncoding(UTF8);
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
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, ByteList.EMPTY_BYTELIST);
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
         int index = enc.getIndex();
         EmptyByteListHolder bytes;
         if (index < EMPTY_BYTELISTS.length && (bytes = EMPTY_BYTELISTS[index]) != null) {
             return bytes;
         }
         return prepareEmptyByteList(enc);
     }
 
     private static EmptyByteListHolder prepareEmptyByteList(Encoding enc) {
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
         Encoding encoding = runtime.is1_9() ? getEncoding() : UTF8;
         Charset charset = encoding.getCharset();
 
         // charset is not defined for this encoding in jcodings db.  Try letting Java resolve this.
         if (charset == null) {
             try {
                 return new String(value.getUnsafeBytes(), value.begin(), value.length(), encoding.toString());
             } catch (UnsupportedEncodingException uee) {
                 return value.toString();
             }
         }
 
         return RubyEncoding.decode(value.getUnsafeBytes(), value.begin(), value.length(), charset);
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
         final RubyString shared;
         Encoding enc = value.getEncoding();
         RubyClass meta = getType();
 
         if (len == 0) {
             shared = newEmptyString(runtime, meta, enc);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
             shared.copyCodeRangeForSubstr(this, enc); // no need to assign encoding, same bytelist shared
         }
         shared.infectBy(this);
         return shared;
     }
 
     public final void modifyCheck() {
         frozenCheck();
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
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
 
         value.replace(bytes);
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
             return context.getRuntime().newFixnum(op_cmp((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     @JRubyMethod(name = "<=>", compat = RUBY1_9)
     public IRubyObject op_cmp19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp19((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     private IRubyObject op_cmpCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
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
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             return value.equal(((RubyString)other).value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     @JRubyMethod(name = "==", compat = RUBY1_9)
     public IRubyObject op_equal19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return isComparableWith(otherString) && value.equal(otherString.value) ? runtime.getTrue() : runtime.getFalse();    
         }
         return op_equalCommon(context, other);
     }
 
     private IRubyObject op_equalCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (!other.respondsTo("to_str")) return runtime.getFalse();
         return invokedynamic(context, other, OP_EQUAL, this).isTrue() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_8)
     public IRubyObject op_plus(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         RubyString resultStr = newString(context.getRuntime(), addByteLists(value, str.value));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         Encoding enc = checkEncoding(str);
         RubyString resultStr = newStringNoCopy(context.getRuntime(), addByteLists(value, str.value),
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
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_8)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         return multiplyByteList(context, other);
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_9)
     public IRubyObject op_mul19(ThreadContext context, IRubyObject other) {
         RubyString result = multiplyByteList(context, other);
         result.value.setEncoding(value.getEncoding());
         result.copyCodeRange(this);
         return result;
     }
 
     private RubyString multiplyByteList(ThreadContext context, IRubyObject arg) {
         int len = RubyNumeric.num2int(arg);
         if (len < 0) throw context.getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.getRealSize()) {
             throw context.getRuntime().newArgumentError("argument too big");
         }
 
         ByteList bytes = new ByteList(len *= value.getRealSize());
         if (len > 0) {
             bytes.setRealSize(len);
             int n = value.getRealSize();
             System.arraycopy(value.getUnsafeBytes(), value.getBegin(), bytes.getUnsafeBytes(), 0, n);
             while (n <= len >> 1) {
                 System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, n);
                 n <<= 1;
             }
             System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, len - n);
         }
         RubyString result = new RubyString(context.getRuntime(), getMetaClass(), bytes);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "%", required = 1)
     public IRubyObject op_format(ThreadContext context, IRubyObject arg) {
         return opFormatCommon(context, arg, context.getRuntime().getInstanceConfig().getCompatVersion());
     }
 
     private IRubyObject opFormatCommon(ThreadContext context, IRubyObject arg, CompatVersion compat) {
         IRubyObject tmp = arg.checkArrayType();
         if (tmp.isNil()) tmp = arg;
 
-        // FIXME: Should we make this work with platform's locale,
-        // or continue hardcoding US?
         ByteList out = new ByteList(value.getRealSize());
+        out.setEncoding(value.getEncoding());
+        
         boolean tainted;
+        
+        // FIXME: Should we make this work with platform's locale,
+        // or continue hardcoding US?
         switch (compat) {
         case RUBY1_8:
             tainted = Sprintf.sprintf(out, Locale.US, value, tmp);
             break;
         case RUBY1_9:
             tainted = Sprintf.sprintf1_9(out, Locale.US, value, tmp);
             break;
         default:
             throw new RuntimeException("invalid compat version for sprintf: " + compat);
         }
         RubyString str = newString(context.getRuntime(), out);
 
         str.setTaint(tainted || isTaint());
         return str;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         Ruby runtime = getRuntime();
         return RubyFixnum.newFixnum(runtime, strHashCode(runtime));
     }
 
     @Override
     public int hashCode() {
         return strHashCode(getRuntime());
     }
 
     private int strHashCode(Ruby runtime) {
         if (runtime.is1_9()) {
             return value.hashCode() ^ (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         } else {
             return value.hashCode();
         }
     }
 
     @Override
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             if (((RubyString) other).value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(ThreadContext context, IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
         IRubyObject str = obj.callMethod(context, "to_s");
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
         if (obj.isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public final int op_cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     public final int op_cmp19(RubyString other) {
         int ret = value.cmp(other.value);
         if (ret == 0 && !isComparableWith(other)) {
             return value.getEncoding().getIndex() > other.value.getEncoding().getIndex() ? 1 : -1;
         }
         return ret;
     }
 
     /** rb_to_id
      *
      */
     @Override
     public String asJavaString() {
         return toString();
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     public final RubyString cat(byte[] str) {
         modify(value.getRealSize() + str.length);
         System.arraycopy(str, 0, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.length);
         value.setRealSize(value.getRealSize() + str.length);
         return this;
     }
 
     public final RubyString cat(byte[] str, int beg, int len) {
         modify(value.getRealSize() + len);
         System.arraycopy(str, beg, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         return this;
     }
 
     // // rb_str_buf_append
     public final RubyString cat19(RubyString str) {
         ByteList other = str.value;
         int otherCr = cat(other.getUnsafeBytes(), other.getBegin(), other.getRealSize(),
                 other.getEncoding(), str.getCodeRange());
         infectBy(str);
         str.setCodeRange(otherCr);
         return this;
     }
 
     public final RubyString cat(ByteList str) {
         modify(value.getRealSize() + str.getRealSize());
         System.arraycopy(str.getUnsafeBytes(), str.getBegin(), value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.getRealSize());
         value.setRealSize(value.getRealSize() + str.getRealSize());
         return this;
     }
 
     public final RubyString cat(byte ch) {
         modify(value.getRealSize() + 1);
         value.getUnsafeBytes()[value.getBegin() + value.getRealSize()] = ch;
         value.setRealSize(value.getRealSize() + 1);
         return this;
     }
 
     public final RubyString cat(int ch) {
         return cat((byte)ch);
     }
 
     public final RubyString cat(int code, Encoding enc) {
         int n = codeLength(getRuntime(), enc, code);
         modify(value.getRealSize() + n);
         enc.codeToMbc(code, value.getUnsafeBytes(), value.getBegin() + value.getRealSize());
         value.setRealSize(value.getRealSize() + n);
         return this;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc, int cr) {
         modify(value.getRealSize() + len);
         int toCr = getCodeRange();
         Encoding toEnc = value.getEncoding();
         int cr2 = cr;
 
         if (toEnc == enc) {
             if (toCr == CR_UNKNOWN || (toEnc == ASCIIEncoding.INSTANCE && toCr != CR_7BIT)) { 
                 cr = CR_UNKNOWN;
             } else if (cr == CR_UNKNOWN) {
                 cr = codeRangeScan(enc, bytes, p, len);
             }
         } else {
             if (!toEnc.isAsciiCompatible() || !enc.isAsciiCompatible()) {
                 if (len == 0) return toCr;
                 if (value.getRealSize() == 0) {
                     System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
                     value.setRealSize(value.getRealSize() + len);
                     setEncodingAndCodeRange(enc, cr);
                     return cr;
                 }
                 throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
             }
             if (cr == CR_UNKNOWN) cr = codeRangeScan(enc, bytes, p, len);
             if (toCr == CR_UNKNOWN) {
                 if (toEnc == ASCIIEncoding.INSTANCE || cr != CR_7BIT) toCr = scanForCodeRange(); 
             }
         }
         if (cr2 != 0) cr2 = cr;
 
         if (toEnc != enc && toCr != CR_7BIT && cr != CR_7BIT) {        
             throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
         }
 
         final int resCr;
         final Encoding resEnc;
         if (toCr == CR_UNKNOWN) {
             resEnc = toEnc;
             resCr = CR_UNKNOWN;
         } else if (toCr == CR_7BIT) {
             if (cr == CR_7BIT) {
                 resEnc = toEnc != ASCIIEncoding.INSTANCE ? toEnc : enc;
                 resCr = CR_7BIT;
             } else {
                 resEnc = enc;
                 resCr = cr;
             }
         } else if (toCr == CR_VALID) {
             resEnc = toEnc;
             if (cr == CR_7BIT || cr == CR_VALID) {
                 resCr = toCr;
             } else {
                 resCr = cr;
             }
         } else {
             resEnc = toEnc;
             resCr = len > 0 ? CR_UNKNOWN : toCr;
         }
 
         if (len < 0) throw getRuntime().newArgumentError("negative string size (or size too big)");            
 
         System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         setEncodingAndCodeRange(resEnc, resCr);
 
         return cr2;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc) {
         return cat(bytes, p, len, enc, CR_UNKNOWN);
     }
 
     public final RubyString catAscii(byte[]bytes, int p, int len) {
         Encoding enc = value.getEncoding();
         if (enc.isAsciiCompatible()) {
             cat(bytes, p, len, enc, CR_7BIT);
         } else {
             byte buf[] = new byte[enc.maxLength()];
             int end = p + len;
             while (p < end) {
                 int c = bytes[p];
                 int cl = codeLength(getRuntime(), enc, c);
                 enc.codeToMbc(c, buf, 0);
                 cat(buf, 0, cl, enc, CR_VALID);
                 p++;
             }
         }
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_8)
     public IRubyObject replace(IRubyObject other) {
         if (this == other) return this;
         replaceCommon(other);
         return this;
     }
 
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_9)
     public RubyString replace19(IRubyObject other) {
         modifyCheck();
         if (this == other) return this;        
         setCodeRange(replaceCommon(other).getCodeRange()); // encoding doesn't have to be copied.
         return this;
     }
 
     private RubyString replaceCommon(IRubyObject other) {
         modifyCheck();
         RubyString otherStr = other.convertToString();
         otherStr.shareLevel = shareLevel = SHARE_LEVEL_BYTELIST;
         value = otherStr.value;
         infectBy(otherStr);
         return otherStr;
     }
 
     @JRubyMethod(name = "clear", compat = RUBY1_9)
     public RubyString clear() {
         modifyCheck();
         Encoding enc = value.getEncoding();
 
         EmptyByteListHolder holder = getEmptyByteList(enc); 
         value = holder.bytes;
         shareLevel = SHARE_LEVEL_BYTELIST;
         setCodeRange(holder.cr);
         return this;
     }
 
     @JRubyMethod(name = "reverse", compat = RUBY1_8)
     public IRubyObject reverse(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         for (int i = 0; i <= len >> 1; i++) {
             obytes[i] = bytes[p + len - i - 1];
             obytes[len - i - 1] = bytes[p + i];
         }
 
         return new RubyString(runtime, getMetaClass(), new ByteList(obytes, false)).infectBy(this);
     }
 
     @JRubyMethod(name = "reverse", compat = RUBY1_9)
     public IRubyObject reverse19(ThreadContext context) {        
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         boolean single = true;
         Encoding enc = value.getEncoding();
         // this really needs to be inlined here
         if (singleByteOptimizable(enc)) {
             for (int i = 0; i <= len >> 1; i++) {
                 obytes[i] = bytes[p + len - i - 1];
                 obytes[len - i - 1] = bytes[p + i];
             }
         } else {
             int end = p + len;
             int op = len;
             while (p < end) {
                 int cl = StringSupport.length(enc, bytes, p, end);
                 if (cl > 1 || (bytes[p] & 0x80) != 0) {
                     single = false;
                     op -= cl;
                     System.arraycopy(bytes, p, obytes, op, cl);
                     p += cl;
                 } else {
                     obytes[--op] = bytes[p++];
                 }
             }
         }
 
         RubyString result = new RubyString(runtime, getMetaClass(), new ByteList(obytes, false));
 
         if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
         Encoding encoding = value.getEncoding();
         result.value.setEncoding(encoding);
         result.copyCodeRangeForSubstr(this, encoding);
         return result.infectBy(this);
     }
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_8)
     public RubyString reverse_bang(ThreadContext context) {
         if (value.getRealSize() > 1) {
             modify();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
             for (int i = 0; i < len >> 1; i++) {
                 byte b = bytes[p + i];
                 bytes[p + i] = bytes[p + len - i - 1];
                 bytes[p + len - i - 1] = b;
             }
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_9)
     public RubyString reverse_bang19(ThreadContext context) {
         modifyCheck();
         if (value.getRealSize() > 1) {
             modifyAndKeepCodeRange();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
             
             Encoding enc = value.getEncoding();
             // this really needs to be inlined here
             if (singleByteOptimizable(enc)) {
                 for (int i = 0; i < len >> 1; i++) {
                     byte b = bytes[p + i];
                     bytes[p + i] = bytes[p + len - i - 1];
                     bytes[p + len - i - 1] = b;
                 }
             } else {
                 int end = p + len;
                 int op = len;
                 byte[]obytes = new byte[len];
                 boolean single = true;
                 while (p < end) {
                     int cl = StringSupport.length(enc, bytes, p, end);
                     if (cl > 1 || (bytes[p] & 0x80) != 0) {
                         single = false;
                         op -= cl;
                         System.arraycopy(bytes, p, obytes, op, cl);
                         p += cl;
                     } else {
                         obytes[--op] = bytes[p++];
                     }
                 }
                 value.setUnsafeBytes(obytes);
                 if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
             }
         }
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newStringShared(recv.getRuntime(), ByteList.EMPTY_BYTELIST);
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0) {
         replace(arg0);
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     @Override
     public IRubyObject initialize19(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     @Override
     public IRubyObject initialize19(ThreadContext context, IRubyObject arg0) {
         replace19(arg0);
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         return RubyFixnum.newFixnum(context.getRuntime(), value.caseInsensitiveCmp(other.convertToString().value));
     }
 
     @JRubyMethod(name = "casecmp", compat = RUBY1_9)
     public IRubyObject casecmp19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         RubyString otherStr = other.convertToString();
         Encoding enc = isCompatibleWith(otherStr);
         if (enc == null) return runtime.getNil();
         
         if (singleByteOptimizable() && otherStr.singleByteOptimizable()) {
             return RubyFixnum.newFixnum(runtime, value.caseInsensitiveCmp(otherStr.value));
         } else {
             return multiByteCasecmp(runtime, enc, value, otherStr.value);
         }
     }
 
     private IRubyObject multiByteCasecmp(Ruby runtime, Encoding enc, ByteList value, ByteList otherValue) {
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
         byte[]obytes = otherValue.getUnsafeBytes();
         int op = otherValue.getBegin();
         int oend = op + otherValue.getRealSize();
 
         while (p < end && op < oend) {
             final int c, oc;
             if (enc.isAsciiCompatible()) {
                 c = bytes[p] & 0xff;
                 oc = obytes[op] & 0xff;
             } else {
                 c = StringSupport.preciseCodePoint(enc, bytes, p, end);
                 oc = StringSupport.preciseCodePoint(enc, obytes, op, oend);                
             }
 
             int cl, ocl;
             if (Encoding.isAscii(c) && Encoding.isAscii(oc)) {
                 byte uc = AsciiTables.ToUpperCaseTable[c];
                 byte uoc = AsciiTables.ToUpperCaseTable[oc];
                 if (uc != uoc) {
                     return uc < uoc ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
                 }
                 cl = ocl = 1;
             } else {
                 cl = StringSupport.length(enc, bytes, p, end);
                 ocl = StringSupport.length(enc, obytes, op, oend);
                 // TODO: opt for 2 and 3 ?
                 int ret = StringSupport.caseCmp(bytes, p, obytes, op, cl < ocl ? cl : ocl);
                 if (ret != 0) return ret < 0 ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
                 if (cl != ocl) return cl < ocl ? RubyFixnum.minus_one(runtime) : RubyFixnum.one(runtime);
             }
 
             p += cl;
             op += ocl;
         }
         if (end - p == oend - op) return RubyFixnum.zero(runtime);
         return end - p > oend - op ? RubyFixnum.one(runtime) : RubyFixnum.minus_one(runtime);
     }
 
     /** rb_str_match
      *
      */
     @JRubyMethod(name = "=~", compat = RUBY1_8, writes = BACKREF)
     @Override
     public IRubyObject op_match(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match(context, this);
         if (other instanceof RubyString) throw context.getRuntime().newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
 
     @JRubyMethod(name = "=~", compat = RUBY1_9, writes = BACKREF)
     @Override
     public IRubyObject op_match19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match19(context, this);
         if (other instanceof RubyString) throw context.getRuntime().newTypeError("type mismatch: String given");
         return other.callMethod(context, "=~", this);
     }
     /**
      * String#match(pattern)
      *
      * rb_str_match_m
      *
      * @param pattern Regexp or String
      */
     @JRubyMethod(compat = RUBY1_8, reads = BACKREF)
     public IRubyObject match(ThreadContext context, IRubyObject pattern) {
         return getPattern(pattern).callMethod(context, "match", this);
     }
 
     @JRubyMethod(name = "match", compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject pattern, Block block) {
         IRubyObject result = getPattern(pattern).callMethod(context, "match", this);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     @JRubyMethod(name = "match", required = 2, rest = true, compat = RUBY1_9, reads = BACKREF)
     public IRubyObject match19(ThreadContext context, IRubyObject[]args, Block block) {
         RubyRegexp pattern = getPattern(args[0]);
         args[0] = this;
         IRubyObject result = pattern.callMethod(context, "match", args);
         return block.isGiven() && !result.isNil() ? block.yield(context, result) : result;
     }
 
     /** rb_str_capitalize / rb_str_capitalize_bang
      *
      */
     @JRubyMethod(name = "capitalize", compat = RUBY1_8)
     public IRubyObject capitalize(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.capitalize_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_8)
     public IRubyObject capitalize_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modify();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
         
         int c = bytes[s] & 0xff;
         if (ASCII.isLower(c)) {
             bytes[s] = AsciiTables.ToUpperCaseTable[c];
             modify = true;
         }
 
         while (++s < end) {
             c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             }
         }
 
         return modify ? this : runtime.getNil();
     }
 
     @JRubyMethod(name = "capitalize", compat = RUBY1_9)
     public IRubyObject capitalize19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.capitalize_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "capitalize!", compat = RUBY1_9)
     public IRubyObject capitalize_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         boolean modify = false;
 
         int c = codePoint(runtime, enc, bytes, s, end);
         if (enc.isLower(c)) {
             enc.codeToMbc(toUpper(enc, c), bytes, s);
             modify = true;
         }
 
         s += codeLength(runtime, enc, c);
         while (s < end) {
             c = codePoint(runtime, enc, bytes, s, end);
             if (enc.isUpper(c)) {
                 enc.codeToMbc(toLower(enc, c), bytes, s);
                 modify = true;
             }
             s += codeLength(runtime, enc, c);
         }
 
         return modify ? this : runtime.getNil();
     }
 
     @JRubyMethod(name = ">=", compat = RUBY1_8)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) >= 0);
         return RubyComparable.op_ge(context, this, other);
     }
 
     @JRubyMethod(name = ">=", compat = RUBY1_9)
     public IRubyObject op_ge19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) >= 0);
         return RubyComparable.op_ge(context, this, other);
     }
 
     @JRubyMethod(name = ">", compat = RUBY1_8)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) > 0);
         return RubyComparable.op_gt(context, this, other);
     }
 
     @JRubyMethod(name = ">", compat = RUBY1_9)
     public IRubyObject op_gt19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) > 0);
         return RubyComparable.op_gt(context, this, other);
     }
 
     @JRubyMethod(name = "<=", compat = RUBY1_8)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) <= 0);
         return RubyComparable.op_le(context, this, other);
     }
 
     @JRubyMethod(name = "<=", compat = RUBY1_9)
     public IRubyObject op_le19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) <= 0);
         return RubyComparable.op_le(context, this, other);
     }
 
     @JRubyMethod(name = "<", compat = RUBY1_8)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp((RubyString) other) < 0);
         return RubyComparable.op_lt(context, this, other);
     }
 
     @JRubyMethod(name = "<", compat = RUBY1_9)
     public IRubyObject op_lt19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) return context.getRuntime().newBoolean(op_cmp19((RubyString) other) < 0);
         return RubyComparable.op_lt(context, this, other);
     }
 
     @JRubyMethod(name = "eql?", compat = RUBY1_8)
     public IRubyObject str_eql_p(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof RubyString && value.equal(((RubyString)other).value)) return runtime.getTrue();
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "eql?", compat = RUBY1_9)
     public IRubyObject str_eql_p19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             if (isComparableWith(otherString) && value.equal(otherString.value)) return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     /** rb_str_upcase / rb_str_upcase_bang
      *
      */
     @JRubyMethod(name = "upcase", compat = RUBY1_8)
     public RubyString upcase(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.upcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "upcase!", compat = RUBY1_8)
     public IRubyObject upcase_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
         modify();
         return singleByteUpcase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "upcase", compat = RUBY1_9)
     public RubyString upcase19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.upcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "upcase!", compat = RUBY1_9)
     public IRubyObject upcase_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteUpcase(runtime, bytes, s, end);
         } else {
             return multiByteUpcase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteUpcase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isLower(c)) {
                 bytes[s] = AsciiTables.ToUpperCaseTable[c];
                 modify = true;
             }
             s++;
         }
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteUpcase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         int c;
         while (s < end) {
             if (enc.isAsciiCompatible() && Encoding.isAscii(c = bytes[s] & 0xff)) {
                 if (ASCII.isLower(c)) {
                     bytes[s] = AsciiTables.ToUpperCaseTable[c];
                     modify = true;
                 }
                 s++;
             } else {
                 c = codePoint(runtime, enc, bytes, s, end);
                 if (enc.isLower(c)) {
                     enc.codeToMbc(toUpper(enc, c), bytes, s);
                     modify = true;
                 }
                 s += codeLength(runtime, enc, c);
             }
         }
         return modify ? this : runtime.getNil();        
     }
 
     /** rb_str_downcase / rb_str_downcase_bang
      *
      */
     @JRubyMethod(name = "downcase", compat = RUBY1_8)
     public RubyString downcase(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.downcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "downcase!", compat = RUBY1_8)
     public IRubyObject downcase_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modify();
         return singleByteDowncase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "downcase", compat = RUBY1_9)
     public RubyString downcase19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.downcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "downcase!", compat = RUBY1_9)
     public IRubyObject downcase_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
 
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();
         }
 
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteDowncase(runtime, bytes, s, end);
         } else {
             return multiByteDowncase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteDowncase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             }
             s++;
         }
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteDowncase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         int c;
         while (s < end) {
             if (enc.isAsciiCompatible() && Encoding.isAscii(c = bytes[s] & 0xff)) {
                 if (ASCII.isUpper(c)) {
                     bytes[s] = AsciiTables.ToLowerCaseTable[c];
                     modify = true;
                 }
                 s++;
             } else {
                 c = codePoint(runtime, enc, bytes, s, end);
                 if (enc.isUpper(c)) {
                     enc.codeToMbc(toLower(enc, c), bytes, s);
                     modify = true;
                 }
                 s += codeLength(runtime, enc, c);
             }
         }
         return modify ? this : runtime.getNil();        
     }
 
 
     /** rb_str_swapcase / rb_str_swapcase_bang
      *
      */
     @JRubyMethod(name = "swapcase", compat = RUBY1_8)
     public RubyString swapcase(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.swapcase_bang(context);
         return str;
     }
 
     @JRubyMethod(name = "swapcase!", compat = RUBY1_8)
     public IRubyObject swapcase_bang(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();        
         }
         modify();
         return singleByteSwapcase(runtime, value.getUnsafeBytes(), value.getBegin(), value.getBegin() + value.getRealSize());
     }
 
     @JRubyMethod(name = "swapcase", compat = RUBY1_9)
     public RubyString swapcase19(ThreadContext context) {
         RubyString str = strDup(context.getRuntime());
         str.swapcase_bang19(context);
         return str;
     }
 
     @JRubyMethod(name = "swapcase!", compat = RUBY1_9)
     public IRubyObject swapcase_bang19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         Encoding enc = checkDummyEncoding();
         if (value.getRealSize() == 0) {
             modifyCheck();
             return runtime.getNil();        
         }
         modifyAndKeepCodeRange();
 
         int s = value.getBegin();
         int end = s + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
 
         if (singleByteOptimizable(enc)) {
             return singleByteSwapcase(runtime, bytes, s, end);
         } else {
             return multiByteSwapcase(runtime, enc, bytes, s, end);
         }
     }
 
     private IRubyObject singleByteSwapcase(Ruby runtime, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = bytes[s] & 0xff;
             if (ASCII.isUpper(c)) {
                 bytes[s] = AsciiTables.ToLowerCaseTable[c];
                 modify = true;
             } else if (ASCII.isLower(c)) {
                 bytes[s] = AsciiTables.ToUpperCaseTable[c];
                 modify = true;
             }
             s++;
         }
 
         return modify ? this : runtime.getNil();
     }
 
     private IRubyObject multiByteSwapcase(Ruby runtime, Encoding enc, byte[]bytes, int s, int end) {
         boolean modify = false;
         while (s < end) {
             int c = codePoint(runtime, enc, bytes, s, end);
             if (enc.isUpper(c)) {
                 enc.codeToMbc(toLower(enc, c), bytes, s);
                 modify = true;
             } else if (enc.isLower(c)) {
                 enc.codeToMbc(toUpper(enc, c), bytes, s);
                 modify = true;
             }
             s += codeLength(runtime, enc, c);
         }
 
         return modify ? this : runtime.getNil();
     }
 
     /** rb_str_dump
      *
      */
     @JRubyMethod(name = "dump", compat = RUBY1_8)
     public IRubyObject dump() {
         return dumpCommon(false);
     }
 
     @JRubyMethod(name = "dump", compat = RUBY1_9)
     public IRubyObject dump19() {
         return dumpCommon(true);
     }
 
     private IRubyObject dumpCommon(boolean is1_9) {
         Ruby runtime = getRuntime();
         ByteList buf = null;
         Encoding enc = value.getEncoding();
 
         int p = value.getBegin();
         int end = p + value.getRealSize();
         byte[]bytes = value.getUnsafeBytes();
         
         int len = 2;
         while (p < end) {
             int c = bytes[p++] & 0xff;
 
             switch (c) {
             case '"':case '\\':case '\n':case '\r':case '\t':case '\f':
             case '\013': case '\010': case '\007': case '\033':
                 len += 2;
                 break;
             case '#':
                 len += isEVStr(bytes, p, end) ? 2 : 1;
                 break;
             default:
                 if (ASCII.isPrint(c)) {
                     len++;
                 } else {
                     if (is1_9 && enc instanceof UTF8Encoding) {
                         int n = StringSupport.preciseLength(enc, bytes, p - 1, end) - 1;
                         if (n > 0) {
                             if (buf == null) buf = new ByteList();
                             int cc = codePoint(runtime, enc, bytes, p - 1, end);
                             Sprintf.sprintf(runtime, buf, "%x", cc);
                             len += buf.getRealSize() + 4;
                             buf.setRealSize(0);
                             p += n;
                             break;
                         }
                     }
                     len += 4;
                 }
                 break;
             }
         }
 
         if (is1_9 && !enc.isAsciiCompatible()) {
             len += ".force_encoding(\"".length() + enc.getName().length + "\")".length();
         }
 
         ByteList outBytes = new ByteList(len);
         byte out[] = outBytes.getUnsafeBytes();
         int q = 0;
         p = value.getBegin();
         end = p + value.getRealSize();
 
         out[q++] = '"';
         while (p < end) {
             int c = bytes[p++] & 0xff;
             if (c == '"' || c == '\\') {
