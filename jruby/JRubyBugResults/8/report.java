File path: src/org/jruby/ast/WhenOneArgNode.java
Comment: / FIXME: Can get optimized for IEqlNode
Initial commit id: d7361b88
Final commit id: 75f48785
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 28
End block index: 35
    // FIXME: Can get optimized for IEqlNode
    private IRubyObject whenNoTest(ThreadContext context, Ruby runtime, IRubyObject self, Block aBlock) {
        if (expressionNodes.interpret(runtime, context, self, aBlock).isTrue()) {
            return bodyNode.interpret(runtime, context, self, aBlock);
        }

        return null;
    }
