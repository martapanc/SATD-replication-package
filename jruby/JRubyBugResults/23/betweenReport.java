23/report.java
Satd-method: 
********************************************
********************************************
23/Between/dd7f8b74b  Fix #2205: All (except t diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
+++ b/core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
-                                             RubyModule implClass, Visibility visibility) {
+                                             RubyModule implClass) {
-            context.setCurrentVisibility(visibility);
+            // Only the top-level script scope has PRIVATE visibility.
+            // This is already handled as part of Interpreter.execute above.
+            // Everything else is PUBLIC by default.
+            context.setCurrentVisibility(Visibility.PUBLIC);
-            InterpreterContext interpreterContext, Visibility visibility, RubyModule implClass,
+            InterpreterContext interpreterContext, RubyModule implClass,
-                        processBookKeepingOp(context, instr, operation, name, args, self, block, implClass, visibility);
+                        processBookKeepingOp(context, instr, operation, name, args, self, block, implClass);
-            return interpret(context, self, ic, null, clazz, name, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK, null);
+            return interpret(context, self, ic, clazz, name, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK, null);
-            return interpret(context, self, ic, null, clazz, name, args, block, blockType);
+            return interpret(context, self, ic, clazz, name, args, block, blockType);
-            return interpret(context, self, ic, null, null, name, args, block, blockType);
+            return interpret(context, self, ic, null, name, args, block, blockType);
-            return interpret(context, self, ic, method.getVisibility(), method.getImplementationClass().getMethodLocation(), name, args, block, null);
+            return interpret(context, self, ic, method.getImplementationClass().getMethodLocation(), name, args, block, null);

Lines added containing method: 12. Lines removed containing method: 9. Tot = 21
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
