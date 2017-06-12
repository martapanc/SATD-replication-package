136/report.java
Satd-method: 
********************************************
********************************************
136/Between/04ce842cc  Fixes for JRUBY-2883: Ma diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-        int h = realLength;
+        Ruby runtime = context.getRuntime();
+        if (runtime.isInspecting(this)) return  RubyFixnum.zero(runtime);
-        Ruby runtime = getRuntime();
-        int begin = this.begin;
-        for (int i = begin; i < begin + realLength; i++) {
-            h = (h << 1) | (h < 0 ? 1 : 0);
-            final IRubyObject value;
-            try {
-                value = values[i];
-            } catch (ArrayIndexOutOfBoundsException e) {
-                concurrentModification();
-                continue;
+        try {
+            runtime.registerInspecting(this);
+            int begin = this.begin;
+            int h = realLength;
+            for (int i = begin; i < begin + realLength; i++) {
+                h = (h << 1) | (h < 0 ? 1 : 0);
+                final IRubyObject value;
+                try {
+                    value = values[i];
+                } catch (ArrayIndexOutOfBoundsException e) {
+                    concurrentModification();
+                    continue;
+                }
+                h ^= RubyNumeric.num2long(value.callMethod(context, "hash"));
-            h ^= RubyNumeric.num2long(value.callMethod(context, "hash"));
+            return runtime.newFixnum(h);
+        } finally {
+            runtime.unregisterInspecting(this);
-
-        return runtime.newFixnum(h);
-        if (this == obj) return getRuntime().getTrue();
+        Ruby runtime = context.getRuntime();
+        if (this == obj) return runtime.getTrue();
-                return getRuntime().getFalse();
+                return runtime.getFalse();
-                if (equalInternal(context, obj.callMethod(context, "to_ary"), this)) return getRuntime().getTrue();
-                return getRuntime().getFalse();                
+                if (equalInternal(context, obj.callMethod(context, "to_ary"), this)) return runtime.getTrue();
+                return runtime.getFalse();                
-        if (realLength != ary.realLength) return getRuntime().getFalse();
+        if (realLength != ary.realLength || runtime.isInspecting(this)) return getRuntime().getFalse();
-        Ruby runtime = getRuntime();
-        for (long i = 0; i < realLength; i++) {
-            if (!equalInternal(context, elt(i), ary.elt(i))) return runtime.getFalse();            
+        try {
+            runtime.registerInspecting(this);
+            for (long i = 0; i < realLength; i++) {
+                if (!equalInternal(context, elt(i), ary.elt(i))) return runtime.getFalse();            
+            }
+        } finally {
+            runtime.unregisterInspecting(this);
-        if (this == obj) return getRuntime().getTrue();
-        if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
+        Ruby runtime = context.getRuntime();
+        if (this == obj) return runtime.getTrue();
+        if (!(obj instanceof RubyArray)) return runtime.getFalse();
-        if (realLength != ary.realLength) return getRuntime().getFalse();
+        if (realLength != ary.realLength || runtime.isInspecting(this)) return runtime.getFalse();
-        Ruby runtime = getRuntime();
-        for (int i = 0; i < realLength; i++) {
-            if (!eqlInternal(context, elt(i), ary.elt(i))) return runtime.getFalse();
+        try {
+            runtime.registerInspecting(this);
+            for (int i = 0; i < realLength; i++) {
+                if (!eqlInternal(context, elt(i), ary.elt(i))) return runtime.getFalse();
+            }
+        } finally {
+            runtime.unregisterInspecting(this);
+
+        Ruby runtime = context.getRuntime(); 
-        int len = realLength;
+        if (this == ary2 || runtime.isInspecting(this)) return RubyFixnum.zero(runtime);
-        if (len > ary2.realLength) len = ary2.realLength;
+        try {
+            runtime.registerInspecting(this);
-        Ruby runtime = getRuntime();
-        for (int i = 0; i < len; i++) {
-            IRubyObject v = elt(i).callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ary2.elt(i));
-            if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
+            int len = realLength;
+            if (len > ary2.realLength) len = ary2.realLength;
+
+            for (int i = 0; i < len; i++) {
+                IRubyObject v = elt(i).callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ary2.elt(i));
+                if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
+            }
+        } finally {
+            runtime.unregisterInspecting(this);
-        len = realLength - ary2.realLength;
+
+        int len = realLength - ary2.realLength;

Lines added containing method: 60. Lines removed containing method: 36. Tot = 96
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/0505fb1fc  [1.9] Fix JRUBY-4508, ad diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/spec/tags/1.9/ruby/core/array/rotate_tags.txt
+++ /dev/null
-fails(JRUBY-4508):Array#rotate returns a copy of the array whose first n elements is moved to the last
-fails(JRUBY-4508):Array#rotate returns a copy of the array when the length is one
-fails(JRUBY-4508):Array#rotate returns an empty array when self is empty
-fails(JRUBY-4508):Array#rotate does not return self
-fails(JRUBY-4508):Array#rotate returns subclass instance for Array subclasses
-fails(JRUBY-4508):Array#rotate! moves the first n elements to the last and returns self
-fails(JRUBY-4508):Array#rotate! does nothing and returns self when the length is zero or one
-fails(JRUBY-4508):Array#rotate! returns self
-fails(JRUBY-4508):Array#rotate! raises a RuntimeError on a frozen array
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
+
+    private static void aryReverse(IRubyObject[] _p1, int p1, IRubyObject[] _p2, int p2) {
+        while(p1 < p2) {
+            IRubyObject tmp = _p1[p1];
+            _p1[p1++] = _p2[p2];
+            _p2[p2--] = tmp;
+        }
+    }
+
+    private IRubyObject internalRotateBang(ThreadContext context, int cnt) {
+        modify();
+        
+        if(cnt != 0) {
+            IRubyObject[] ptr = values;
+            int len = realLength;
+            
+            if(len > 0 && (cnt = rotateCount(cnt, len)) > 0) {
+                --len;
+                if(cnt < len) aryReverse(ptr, begin + cnt, ptr, begin + len);
+                if(--cnt > 0) aryReverse(ptr, begin, ptr, begin + cnt);
+                if(len > 0)   aryReverse(ptr, begin, ptr, begin + len);
+                return this;
+            }
+        }
+        
+        return context.getRuntime().getNil();
+    }
+
+    private static int rotateCount(int cnt, int len) {
+        return (cnt < 0) ? (len - (~cnt % len) - 1) : (cnt % len);
+    }
+
+    private IRubyObject internalRotate(ThreadContext context, int cnt) {
+        int len = realLength;
+        RubyArray rotated = aryDup();
+        rotated.modify();
+
+        if(len > 0) {
+            cnt = rotateCount(cnt, len);
+            IRubyObject[] ptr = this.values;
+            IRubyObject[] ptr2 = rotated.values;
+            len -= cnt;
+            System.arraycopy(ptr, begin + cnt, ptr2, 0, len);
+            System.arraycopy(ptr, begin, ptr2, len, cnt);
+        }
+        
+        return rotated;
+    }
+
+    @JRubyMethod(name = "rotate!", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate_bang(ThreadContext context) {
+        internalRotateBang(context, 1);
+        return this;
+    }
+
+    @JRubyMethod(name = "rotate!", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate_bang(ThreadContext context, IRubyObject cnt) {
+        internalRotateBang(context, RubyNumeric.fix2int(cnt));
+        return this;
+    }
+
+    @JRubyMethod(name = "rotate", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate(ThreadContext context) {
+        return internalRotate(context, 1);
+    }
+
+    @JRubyMethod(name = "rotate", compat = CompatVersion.RUBY1_9)
+    public IRubyObject rotate(ThreadContext context, IRubyObject cnt) {
+        return internalRotate(context, RubyNumeric.fix2int(cnt));
+    }
+
+

Lines added containing method: 74. Lines removed containing method: 11. Tot = 85
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/219e0308d  Fix for JRUBY-3387: Arra diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-                if (equalInternal(context, obj.callMethod(context, "to_ary"), this)) return runtime.getTrue();
+                if (equalInternal(context, obj, this)) return runtime.getTrue();

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/2b6aedfc5  Fix for JRUBY-3878: Stri diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
+        if (getRuntime().is1_9()) {
+            // 1.9 seems to just do inspect for to_s now
+            return inspect();
+        }
+        

Lines added containing method: 6. Lines removed containing method: 1. Tot = 7
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/397ae2d50  Fix JRUBY-3148 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-    private long fillBegin(IRubyObject arg) {
-        long beg = arg.isNil() ? 0 : RubyNumeric.num2long(arg);
+    private int fillBegin(IRubyObject arg) {
+        int beg = arg.isNil() ? 0 : RubyNumeric.num2int(arg);
-        int end = beg + len;
+        int end = (int)(beg + len);
-        int end = beg + len;
+        int end = (int)(beg + len);

Lines added containing method: 5. Lines removed containing method: 5. Tot = 10
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/4d034fafe  Fix for JRUBY-4157: fann diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-        arg1 = makeShared((int)pos, (int)len, getMetaClass());
+        arg1 = makeShared(begin + (int)pos, (int)len, getMetaClass());
--- a/test/test_array.rb
+++ b/test/test_array.rb
+
+  # JRUBY-4157
+  def test_shared_ary_slice
+    assert_equal [4,5,6], [1,2,3,4,5,6].slice(1,5).slice!(2,3)
+  end

Lines added containing method: 8. Lines removed containing method: 3. Tot = 11
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/56eeae1a8  Fix for JRUBY-4515: Ruby diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
+import org.jruby.java.addons.ArrayJavaAddons;
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
--- a/src/org/jruby/java/addons/ArrayJavaAddons.java
+++ b/src/org/jruby/java/addons/ArrayJavaAddons.java
+import java.lang.reflect.Array;
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

Lines added containing method: 29. Lines removed containing method: 2. Tot = 31
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/6266374bc  Fix for JRUBY-4053: Acti diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
+        if (!(obj instanceof RubyArray)) {
+            if (!obj.respondsTo("to_ary")) {
+                return context.getRuntime().getFalse();
+            }
+            return RuntimeHelpers.rbEqual(context, obj, this);
+        }
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
+
+    /**
+     * Equivalent to rb_equal in MRI
+     *
+     * @param context
+     * @param a
+     * @param b
+     * @return
+     */
+    public static IRubyObject rbEqual(ThreadContext context, IRubyObject a, IRubyObject b) {
+        Ruby runtime = context.getRuntime();
+        if (a == b) return runtime.getTrue();
+        return a.callMethod(context, "==", b);
+    }
--- a/test/jruby_index
+++ b/test/jruby_index
+test_delegated_array_equals
--- /dev/null
+++ b/test/test_delegated_array_equals.rb
+require 'test/unit'
+
+class TestDelegatedArrayEquals < Test::Unit::TestCase
+  class Foo
+     def initialize
+      @ary = [1,2,3]
+    end
+  
+    def ==(other)
+      @ary == other
+    end
+  
+    def to_ary
+      @ary
+    end
+  end
+  
+  class Foo2
+     def initialize
+      @ary = [1,2,3]
+    end
+  
+    def ==(other)
+      @ary == other
+    end
+  end
+
+  def test_delegated_array_equals
+    a = Foo.new
+    assert_equal(a, a)
+    assert(a == a)
+  end
+
+  def test_badly_delegated_array_equals
+    a = Foo2.new
+    assert_not_equal(a, a)
+    assert(!(a == a))
+  end
+end

Lines added containing method: 64. Lines removed containing method: 4. Tot = 68
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/7b201461f  Fixes for JRUBY-3816: Ob diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-        append(JavaUtil.convertJavaToRuby(getRuntime(), element));
+        append(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
-        IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
+        IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element), Block.NULL_BLOCK);
-        return store(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
+        return store(index, JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element));
-        insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element)});
+        insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element)});
-            IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
+            IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
-            IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
+            IRubyObject convertedElement = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), element);
-            store(last, JavaUtil.convertJavaToRuby(getRuntime(), obj));
+            store(last, JavaUtil.convertJavaToUsableRubyObject(getRuntime(), obj));
-            insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToRuby(getRuntime(), obj) });
+            insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), obj) });
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
-        return internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)) != null;
+        return internalGet(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key)) != null;
-        return hasValue(getRuntime().getCurrentContext(), JavaUtil.convertJavaToRuby(getRuntime(), value));
+        return hasValue(getRuntime().getCurrentContext(), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), value));
-        return JavaUtil.convertRubyToJava(internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)));
+        return JavaUtil.convertRubyToJava(internalGet(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key)));
-        internalPut(JavaUtil.convertJavaToRuby(getRuntime(), key), JavaUtil.convertJavaToRuby(getRuntime(), value));
+        internalPut(JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key), JavaUtil.convertJavaToUsableRubyObject(getRuntime(), value));
-        IRubyObject rubyKey = JavaUtil.convertJavaToRuby(getRuntime(), key);
+        IRubyObject rubyKey = JavaUtil.convertJavaToUsableRubyObject(getRuntime(), key);
-            internalPut(JavaUtil.convertJavaToRuby(runtime, entry.getKey()), JavaUtil.convertJavaToRuby(runtime, entry.getValue()));
+            internalPut(JavaUtil.convertJavaToUsableRubyObject(runtime, entry.getKey()), JavaUtil.convertJavaToUsableRubyObject(runtime, entry.getValue()));
-            IRubyObject value = JavaUtil.convertJavaToRuby(hash.getRuntime(), o);
+            IRubyObject value = JavaUtil.convertJavaToUsableRubyObject(hash.getRuntime(), o);
-            return entry.setValue(JavaUtil.convertJavaToRuby(runtime, o));
+            return entry.setValue(JavaUtil.convertJavaToUsableRubyObject(runtime, o));

Lines added containing method: 18. Lines removed containing method: 18. Tot = 36
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/993f8c99e  A bunch of findbugs fixe diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-    final class DefaultComparator implements Comparator {
+    static final class DefaultComparator implements Comparator {
--- a/src/org/jruby/RubyDigest.java
+++ b/src/org/jruby/RubyDigest.java
-        protected static ObjectAllocator BASE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator BASE_ALLOCATOR = new ObjectAllocator() {
--- a/src/org/jruby/RubyDir.java
+++ b/src/org/jruby/RubyDir.java
-    private static ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
+    private static final ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
+            stream.close();
--- a/src/org/jruby/RubyIconv.java
+++ b/src/org/jruby/RubyIconv.java
-    private static ObjectAllocator ICONV_ALLOCATOR = new ObjectAllocator() {
+    private static final ObjectAllocator ICONV_ALLOCATOR = new ObjectAllocator() {
-        protected static ObjectAllocator ICONV_FAILURE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator ICONV_FAILURE_ALLOCATOR = new ObjectAllocator() {
--- a/src/org/jruby/RubyNameError.java
+++ b/src/org/jruby/RubyNameError.java
-        this(runtime, exceptionClass, exceptionClass.getName().toString());
+        this(runtime, exceptionClass, exceptionClass.getName());
+    @Override
+    @Override
--- a/src/org/jruby/RubyNil.java
+++ b/src/org/jruby/RubyNil.java
-    public static ObjectAllocator NIL_ALLOCATOR = new ObjectAllocator() {
+    public static final ObjectAllocator NIL_ALLOCATOR = new ObjectAllocator() {
--- a/src/org/jruby/RubyNoMethodError.java
+++ b/src/org/jruby/RubyNoMethodError.java
-    private static ObjectAllocator NOMETHODERROR_ALLOCATOR = new ObjectAllocator() {
+    private static final ObjectAllocator NOMETHODERROR_ALLOCATOR = new ObjectAllocator() {
-        super(runtime, exceptionClass, exceptionClass.getName().toString());
+        super(runtime, exceptionClass, exceptionClass.getName());
--- a/src/org/jruby/RubyNumeric.java
+++ b/src/org/jruby/RubyNumeric.java
-    protected static ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
+    protected static final ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
-    public void cleanTerminate(IRubyObject result) {
+    public synchronized void cleanTerminate(IRubyObject result) {
--- a/src/org/jruby/RubyZlib.java
+++ b/src/org/jruby/RubyZlib.java
-        protected static ObjectAllocator INFLATE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator INFLATE_ALLOCATOR = new ObjectAllocator() {
-        protected static ObjectAllocator DEFLATE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator DEFLATE_ALLOCATOR = new ObjectAllocator() {
-        protected static ObjectAllocator GZIPFILE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator GZIPFILE_ALLOCATOR = new ObjectAllocator() {
-        protected static ObjectAllocator GZIPREADER_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator GZIPREADER_ALLOCATOR = new ObjectAllocator() {
-        protected static ObjectAllocator GZIPWRITER_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator GZIPWRITER_ALLOCATOR = new ObjectAllocator() {
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
+        out.close();
-            method.ldc(new Boolean(isExclusive));
+            method.ldc(Boolean.valueOf(isExclusive));
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
-        if (containingClass == runtime.getObject() && name == "initialize") {
+        if (containingClass == runtime.getObject() && name.equals("initialize")) {
-        if (name == "__id__" || name == "__send__") {
+        if (name.equals("__id__") || name.equals("__send__")) {
-        if (name == "initialize" || visibility == Visibility.MODULE_FUNCTION) {
+        if (name.equals("initialize") || visibility == Visibility.MODULE_FUNCTION) {
--- a/src/org/jruby/lexer/yacc/RubyYaccLexer.java
+++ b/src/org/jruby/lexer/yacc/RubyYaccLexer.java
-    private final int str_squote = 0;
-    private final int str_dquote = STR_FUNC_EXPAND;
-    private final int str_xquote = STR_FUNC_EXPAND;
-    private final int str_regexp = STR_FUNC_REGEXP | STR_FUNC_ESCAPE | STR_FUNC_EXPAND;
-    private final int str_ssym   = STR_FUNC_SYMBOL;
-    private final int str_dsym   = STR_FUNC_SYMBOL | STR_FUNC_EXPAND;
+    private static final int str_squote = 0;
+    private static final int str_dquote = STR_FUNC_EXPAND;
+    private static final int str_xquote = STR_FUNC_EXPAND;
+    private static final int str_regexp = STR_FUNC_REGEXP | STR_FUNC_ESCAPE | STR_FUNC_EXPAND;
+    private static final int str_ssym   = STR_FUNC_SYMBOL;
+    private static final int str_dsym   = STR_FUNC_SYMBOL | STR_FUNC_EXPAND;
--- a/src/org/jruby/runtime/MethodFactory.java
+++ b/src/org/jruby/runtime/MethodFactory.java
-                dumpingPath = SafePropertyAccessor.getProperty("jruby.dump_invocations").toString();
+                dumpingPath = SafePropertyAccessor.getProperty("jruby.dump_invocations");
--- a/src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+++ b/src/org/jruby/runtime/callback/InvocationCallbackFactory.java
-        String mname = type.getName() + "Invoker$" + method + "_0";
-        String mnamePath = typePath + "Invoker$" + method + "_0";
+        String mname = type.getName() + "Callback$" + method + "_0";
+        String mnamePath = typePath + "Callback$" + method + "_0";
-        String mname = type.getName() + "Invoker$" + method + "_1";
-        String mnamePath = typePath + "Invoker$" + method + "_1";
+        String mname = type.getName() + "Callback$" + method + "_1";
+        String mnamePath = typePath + "Callback$" + method + "_1";
-        String mname = type.getName() + "Invoker$" + method + "_2";
-        String mnamePath = typePath + "Invoker$" + method + "_2";
+        String mname = type.getName() + "Callback$" + method + "_2";
+        String mnamePath = typePath + "Callback$" + method + "_2";
-        String mname = type.getName() + "Invoker$" + method + "_3";
-        String mnamePath = typePath + "Invoker$" + method + "_3";
+        String mname = type.getName() + "Callback$" + method + "_3";
+        String mnamePath = typePath + "Callback$" + method + "_3";
-        String mname = type.getName() + "Invoker$" + method + "S0";
-        String mnamePath = typePath + "Invoker$" + method + "S0";
+        String mname = type.getName() + "Callback$" + method + "S0";
+        String mnamePath = typePath + "Callback$" + method + "S0";
-        String mname = type.getName() + "Invoker$" + method + "_S1";
-        String mnamePath = typePath + "Invoker$" + method + "_S1";
+        String mname = type.getName() + "Callback$" + method + "_S1";
+        String mnamePath = typePath + "Callback$" + method + "_S1";
-        String mname = type.getName() + "Invoker$" + method + "_S2";
-        String mnamePath = typePath + "Invoker$" + method + "_S2";
+        String mname = type.getName() + "Callback$" + method + "_S2";
+        String mnamePath = typePath + "Callback$" + method + "_S2";
-        String mname = type.getName() + "Invoker$" + method + "_S3";
-        String mnamePath = typePath + "Invoker$" + method + "_S3";
+        String mname = type.getName() + "Callback$" + method + "_S3";
+        String mnamePath = typePath + "Callback$" + method + "_S3";
-        String mname = typeClass.getName() + "Block" + method + "xx1";
-        String mnamePath = typePathString + "Block" + method + "xx1";
+        String mname = typeClass.getName() + "BlockCallback$" + method + "xx1";
+        String mnamePath = typePathString + "BlockCallback$" + method + "xx1";
-        String mname = type.getName() + "Invoker$" + method + "_Sopt";
-        String mnamePath = typePath + "Invoker$" + method + "_Sopt";
+        String mname = type.getName() + "Callback$" + method + "_Sopt";
+        String mnamePath = typePath + "Callback$" + method + "_Sopt";
-        String mname = type.getName() + "Invoker$" + method + "_opt";
-        String mnamePath = typePath + "Invoker$" + method + "_opt";
+        String mname = type.getName() + "Callback$" + method + "_opt";
+        String mnamePath = typePath + "Callback$" + method + "_opt";
-        String mname = type.getName() + "Invoker$" + method + "_F0";
-        String mnamePath = typePath + "Invoker$" + method + "_F0";
+        String mname = type.getName() + "Callback$" + method + "_F0";
+        String mnamePath = typePath + "Callback$" + method + "_F0";
-        String mname = type.getName() + "Invoker$" + method + "_F1";
-        String mnamePath = typePath + "Invoker$" + method + "_F1";
+        String mname = type.getName() + "Callback$" + method + "_F1";
+        String mnamePath = typePath + "Callback$" + method + "_F1";
-        String mname = type.getName() + "Invoker$" + method + "_F2";
-        String mnamePath = typePath + "Invoker$" + method + "_F2";
+        String mname = type.getName() + "Callback$" + method + "_F2";
+        String mnamePath = typePath + "Callback$" + method + "_F2";
-        String mname = type.getName() + "Invoker$" + method + "_F3";
-        String mnamePath = typePath + "Invoker$" + method + "_F3";
+        String mname = type.getName() + "Callback$" + method + "_F3";
+        String mnamePath = typePath + "Callback$" + method + "_F3";
-        String mname = type.getName() + "Invoker$" + method + "_FS0";
-        String mnamePath = typePath + "Invoker$" + method + "_FS0";
+        String mname = type.getName() + "Callback$" + method + "_FS0";
+        String mnamePath = typePath + "Callback$" + method + "_FS0";
-        String mname = type.getName() + "Invoker$" + method + "_FS1";
-        String mnamePath = typePath + "Invoker$" + method + "_FS1";
+        String mname = type.getName() + "Callback$" + method + "_FS1";
+        String mnamePath = typePath + "Callback$" + method + "_FS1";
-        String mname = type.getName() + "Invoker$" + method + "_FS2";
-        String mnamePath = typePath + "Invoker$" + method + "_FS2";
+        String mname = type.getName() + "Callback$" + method + "_FS2";
+        String mnamePath = typePath + "Callback$" + method + "_FS2";
-        String mname = type.getName() + "Invoker$" + method + "_FS3";
-        String mnamePath = typePath + "Invoker$" + method + "_FS3";
+        String mname = type.getName() + "Callback$" + method + "_FS3";
+        String mnamePath = typePath + "Callback$" + method + "_FS3";
-        String mname = type.getName() + "Invoker$" + method + "_Fopt";
-        String mnamePath = typePath + "Invoker$" + method + "_Fopt";
+        String mname = type.getName() + "Callback$" + method + "_Fopt";
+        String mnamePath = typePath + "Callback$" + method + "_Fopt";
-        String mname = type.getName() + "Invoker$" + method + "_FSopt";
-        String mnamePath = typePath + "Invoker$" + method + "_FSopt";
+        String mname = type.getName() + "Callback$" + method + "_FSopt";
+        String mnamePath = typePath + "Callback$" + method + "_FSopt";
-        String className = type.getName() + "Dispatcher_for_" + metaClass.getBaseName();
-        String classPath = typePath + "Dispatcher_for_" + metaClass.getBaseName();
+        String className = type.getName() + "Dispatcher$" + metaClass.getBaseName();
+        String classPath = typePath + "Dispatcher$" + metaClass.getBaseName();

Lines added containing method: 90. Lines removed containing method: 86. Tot = 176
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/a2854314c  Fix for JRUBY-2065: Arra diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-            if (v instanceof RubyArray && ((RubyArray) v).realLength > 0
-                && equalInternal(context, ((RubyArray) v).values[0], key).isTrue()) {
-                return v;
+            if (v instanceof RubyArray) {
+                RubyArray arr = (RubyArray)v;
+                if (arr.realLength > 0 && equalInternal(context, arr.values[arr.begin], key).isTrue()) return arr;
+
-            if (v instanceof RubyArray && ((RubyArray) v).realLength > 1
-                    && equalInternal(context, ((RubyArray) v).values[1], value).isTrue()) {
-                return v;
+            if (v instanceof RubyArray) {
+                RubyArray arr = (RubyArray)v;
+                if (arr.realLength > 1 && equalInternal(context, arr.values[arr.begin + 1], value).isTrue()) return arr;

Lines added containing method: 8. Lines removed containing method: 7. Tot = 15
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/ac5467733  fixes JRUBY-4175: RubySp diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/spec/tags/1.8/ruby/core/array/comparison_tags.txt
+++ /dev/null
-fails(JRUBY-4175):Array#<=> returns nil when the argument is not array-like
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-        Ruby runtime = context.getRuntime(); 
-        RubyArray ary2 = obj.convertToArray();
+        Ruby runtime = context.getRuntime();
+        IRubyObject ary2 = runtime.getNil();
+        boolean isAnArray = (obj instanceof RubyArray) || obj.getMetaClass().getSuperClass() == runtime.getArray();
+
+        if (!isAnArray && !obj.respondsTo("to_ary")) {
+            return ary2;
+        } else if (!isAnArray) {
+            ary2 = obj.callMethod(context, "to_ary");
+        } else {
+            ary2 = obj.convertToArray();
+        }
+        
+        return cmpCommon(context, runtime, (RubyArray) ary2);
+    }
+    private IRubyObject cmpCommon(ThreadContext context, Ruby runtime, RubyArray ary2) {

Lines added containing method: 17. Lines removed containing method: 5. Tot = 22
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/aeef3e6d1  fixes JRUBY-4181: [1.8.7 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-    private IRubyObject slice_internal(long pos, long len, IRubyObject arg0, IRubyObject arg1) {
-        Ruby runtime = getRuntime();
-
+    private IRubyObject slice_internal(long pos, long len, 
+            IRubyObject arg0, IRubyObject arg1, Ruby runtime) {
+
+        Ruby runtime = getRuntime();
-            long[] beglen = ((RubyRange) arg0).begLen(realLength, 1);
+            RubyRange range = (RubyRange) arg0;
+            if (!range.checkBegin(realLength)) {
+                return runtime.getNil();
+            }
+
+            long[] beglen = range.begLen(realLength, 1);
-            return slice_internal(pos, len, arg0, null);
+            return slice_internal(pos, len, arg0, null, runtime);
-        return slice_internal(pos, len, arg0, arg1);
+        return slice_internal(pos, len, arg0, arg1, getRuntime());
--- a/src/org/jruby/RubyRange.java
+++ b/src/org/jruby/RubyRange.java
+    final boolean checkBegin(long length) {
+        long beg = RubyNumeric.num2long(this.begin);
+        if(beg < 0) {
+            beg += length;
+            if(beg < 0) {
+                return false;
+            }
+        } else if(length < beg) {
+            return false;
+        }
+        return true;
+    }
+
--- a/test/externals/ruby1.8/ruby/test_array.rb
+++ b/test/externals/ruby1.8/ruby/test_array.rb
-    # JRUBY-4181
-    # a = @cls[1, 2, 3, 4, 5]
-    # assert_equal(nil, a.slice!(-6..4))
-    # assert_equal(@cls[1, 2, 3, 4, 5], a)
+    a = @cls[1, 2, 3, 4, 5]
+    assert_equal(nil, a.slice!(-6..4))
+    assert_equal(@cls[1, 2, 3, 4, 5], a)

Lines added containing method: 31. Lines removed containing method: 13. Tot = 44
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/b3332e8a4  Fix for JRUBY-3251: Conc diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-                fillNil(reallocated, values.length, newLength, getRuntime());
+                fillNil(reallocated, valuesLength, newLength, getRuntime());

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/bad1f6788  Fixes for JRUBY-1409, on diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
+    private RubyModule procGIDModule;
+    
+    public RubyModule getProcGID() {
+        return procGIDModule;
+    }
+    void setProcGID(RubyModule procGIDModule) {
+        this.procGIDModule = procGIDModule;
+    }
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
-	public int size() {
+    public int size() {
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
-    @JRubyMethod(name = "%", required = 1, alias = "modulo")
+    @JRubyMethod(name = {"%", "modulo"}, required = 1)
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
-    @JRubyMethod(name = {"fileno", "to_i"})
+    @JRubyMethod(name = "fileno", alias = "to_i")
--- a/src/org/jruby/RubyInteger.java
+++ b/src/org/jruby/RubyInteger.java
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
-    @JRubyMethod(name = "succ", alias = "next")
+    @JRubyMethod(name = {"succ", "next"})
--- a/src/org/jruby/RubyJRuby.java
+++ b/src/org/jruby/RubyJRuby.java
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
-    @JRubyMethod(name = "parse", alias = "ast_for", optional = 3, frame = true, module = true)
+    @JRubyMethod(name = {"parse", "ast_for"}, optional = 3, frame = true, module = true)
--- a/src/org/jruby/RubyProcess.java
+++ b/src/org/jruby/RubyProcess.java
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
+        
+        RubyModule process_gid = process.defineModuleUnder("GID");
+        runtime.setProcGID(process_gid);
-        CallbackFactory processCallbackFactory = runtime.callbackFactory(RubyProcess.class);
-        CallbackFactory processUIDCallbackFactory = runtime.callbackFactory(RubyProcess.UID.class);
-        process_uid.defineAnnotatedMethods(UID.class);
+        process_uid.defineAnnotatedMethods(UserAndGroupID.class);
+        process_gid.defineAnnotatedMethods(UserAndGroupID.class);
-    public static class UID {
+    public static class UserAndGroupID {
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
-    @JRubyMethod(name = "length", alias = "size")
+    @JRubyMethod(name = {"length", "size"})
-    @JRubyMethod(name = "concat", alias = "<<", required = 1)
+    @JRubyMethod(name = {"concat", "<<"}, required = 1)
-    @JRubyMethod(name = "[]", alias = "slice", required = 1, optional = 1)
+    @JRubyMethod(name = {"[]", "slice"}, required = 1, optional = 1)
-    @JRubyMethod(name = "succ", alias = "next")
+    @JRubyMethod(name = {"succ", "next"})
-    @JRubyMethod(name = "succ!", alias = "next!")
+    @JRubyMethod(name = {"succ!", "next!"})
-    @JRubyMethod(name = "each_line", required = 0, optional = 1, frame = true, alias = "each")
+    @JRubyMethod(name = {"each_line", "each"}, required = 0, optional = 1, frame = true)
-    @JRubyMethod(name = "to_sym", alias = "intern")
+    @JRubyMethod(name = {"to_sym", "intern"})

Lines added containing method: 46. Lines removed containing method: 30. Tot = 76
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
136/Between/c6aebe391  Fix JRUBY-3612 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
-                    if (runtime.isInspecting(tmp)) {
+                    if (tmp == this || runtime.isInspecting(tmp)) {

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
