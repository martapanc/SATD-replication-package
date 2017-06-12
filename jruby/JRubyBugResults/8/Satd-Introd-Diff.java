diff --git a/src/org/jruby/ast/WhenOneArgNode.java b/src/org/jruby/ast/WhenOneArgNode.java
new file mode 100644
index 0000000000..507f7ceac0
--- /dev/null
+++ b/src/org/jruby/ast/WhenOneArgNode.java
@@ -0,0 +1,59 @@
+/*
+ * To change this template, choose Tools | Templates
+ * and open the template in the editor.
+ */
+
+package org.jruby.ast;
+
+import org.jruby.Ruby;
+import org.jruby.ast.types.IEqlNode;
+import org.jruby.lexer.yacc.ISourcePosition;
+import org.jruby.runtime.Block;
+import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.builtin.IRubyObject;
+
+/**
+ *
+ * @author enebo
+ */
+public class WhenOneArgNode extends WhenNode {
+    public WhenOneArgNode(ISourcePosition position, Node expressionNode, Node bodyNode, Node nextCase) {
+        super(position, expressionNode, bodyNode, nextCase);
+    }
+
+    @Override
+    public NodeType getHomogeneity() {
+        return expressionNodes.getNodeType();
+    }
+
+    // FIXME: Can get optimized for IEqlNode
+    private IRubyObject whenNoTest(ThreadContext context, Ruby runtime, IRubyObject self, Block aBlock) {
+        if (expressionNodes.interpret(runtime, context, self, aBlock).isTrue()) {
+            return bodyNode.interpret(runtime, context, self, aBlock);
+        }
+
+        return null;
+    }
+
+    private IRubyObject whenSlowTest(IRubyObject test, ThreadContext context, Ruby runtime, IRubyObject self, Block aBlock) {
+        IRubyObject expression = expressionNodes.interpret(runtime, context, self, aBlock);
+        if (eqq.call(context, self, expression, test).isTrue()) {
+                return bodyNode.interpret(runtime, context, self, aBlock);
+        }
+
+        return null;
+    }
+
+    @Override
+    public IRubyObject when(IRubyObject test, ThreadContext context, Ruby runtime, IRubyObject self, Block aBlock) {
+        // No actual test, so do 'when' if when expression is not nil
+        if (test == null) return whenNoTest(context, runtime, self, aBlock);
+        if (!(expressionNodes instanceof IEqlNode)) return whenSlowTest(test, context, runtime, self, aBlock);
+
+        if (((IEqlNode) expressionNodes).eql(test, context, runtime, self, aBlock)) {
+            return bodyNode.interpret(runtime, context, self, aBlock);
+        }
+
+        return null;
+    }
+}
