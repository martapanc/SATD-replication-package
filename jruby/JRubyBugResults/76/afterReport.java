76/report.java
Satd-method: 
********************************************
********************************************
76/After/13da9cb5b  Fix JRUBY-6367. diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/src/org/jruby/RubyMethod.java
+++ b/src/org/jruby/RubyMethod.java
-            IRubyObject self, Block unusedBlock) {
+            IRubyObject self, Block block) {
-            return ((RubyMethod) arg1).call(context, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
+            return ((RubyMethod) arg1).call(context, IRubyObject.NULL_ARRAY, block);
-            return ((RubyMethod) arg1).call(context, ((RubyArray) blockArg).toJavaArray(), Block.NULL_BLOCK);
+            return ((RubyMethod) arg1).call(context, ((RubyArray) blockArg).toJavaArray(), block);
-        return ((RubyMethod) arg1).call(context, new IRubyObject[] { blockArg }, Block.NULL_BLOCK);
+        return ((RubyMethod) arg1).call(context, new IRubyObject[] { blockArg }, block);
--- a/src/org/jruby/runtime/MethodBlock.java
+++ b/src/org/jruby/runtime/MethodBlock.java
-        return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
+        return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type, Block.NULL_BLOCK);
+    }
+
+    @Override
+    public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type, Block block) {
+        return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type, block);
+    @Override
+    @Override
+    public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type, Block block) {
+        return yield(context, value, null, null, false, binding, type, block);
+    }
+
+    @Override
+    public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self,
+                             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
+        return yield(context, value, self, klass, aValue, binding, type, Block.NULL_BLOCK);
+    }
+
+    @Override
-            RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
+            RubyModule klass, boolean aValue, Binding binding, Block.Type type, Block block) {
-                    return callback(value, method, self, Block.NULL_BLOCK);
+                    return callback(value, method, self, block);

Lines added containing method: 27. Lines removed containing method: 9. Tot = 36
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
