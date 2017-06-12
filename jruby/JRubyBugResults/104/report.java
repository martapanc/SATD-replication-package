File path: truffle/src/main/java/org/jruby/truffle/nodes/core/TruffleInteropNodes.java
Comment: / TODO: remove
Initial commit id: 9f228b9a
Final commit id: 5f14842c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 276
End block index: 299
    // TODO: remove maxArgs - hits an assertion if maxArgs is removed - trying argumentsAsArray = true (CS)
    @CoreMethod(names = "execute", isModuleFunction = true, needsSelf = false, required = 1, argumentsAsArray = true)
    public abstract static class ExecuteNode extends CoreMethodNode {

        @Child private ForeignObjectAccessNode node;

        public ExecuteNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }

        public ExecuteNode(ExecuteNode prev) {
            this(prev.getContext(), prev.getSourceSection());
        }

        @Specialization
        public Object executeForeign(VirtualFrame frame, TruffleObject receiver, Object[] arguments) {
            if (node == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                this.node = ForeignObjectAccessNode.getAccess(Execute.create(Receiver.create(), arguments.length));
            }
            return node.executeForeign(frame, receiver, arguments);
        }

    }
