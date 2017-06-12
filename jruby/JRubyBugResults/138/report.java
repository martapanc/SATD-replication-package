File path: src/org/jruby/compiler/ir/IR_ExecutionScope.java
Comment: SS FIXME: Do we need to check if l is same as whatever popped?
Initial commit id: dc0b3a04
Final commit id: 8dff5b2a
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 64
End block index: 64
    public void endLoop(IR_Loop l) { _loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */ }
