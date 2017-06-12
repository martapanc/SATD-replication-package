File path: src/org/jruby/compiler/ir/IR_Builder.java
Comment: / XXX: const lookup can trigger const_missing
Initial commit id: dfa3f863
Final commit id: a7b4c64e
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 1127
End block index: 1134
    public void buildConst(Node node, IR_BuilderContext m, boolean expr) {
        ConstNode constNode = (ConstNode) node;

        m.retrieveConstant(constNode.getName());
        // TODO: don't require pop
        if (!expr) m.consumeCurrentValue();
        // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
    }
