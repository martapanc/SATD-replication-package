File path: src/org/jruby/runtime/BlockBody.erb
Comment: / FIXME: This is gross. Don't do this.
Initial commit id: a0ea820f
Final commit id: 45658ca9
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 158
End block index: 173
    public static NodeType getArgumentTypeWackyHack(IterNode iterNode) {
        NodeType argsNodeId = null;
        if (iterNode.getVarNode() != null && iterNode.getVarNode().getNodeType() != NodeType.ZEROARGNODE) {
            // if we have multiple asgn with just *args, need a special type for that
            argsNodeId = iterNode.getVarNode().getNodeType();
            if (argsNodeId == NodeType.MULTIPLEASGNNODE) {
                MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)iterNode.getVarNode();
                if (multipleAsgnNode.getHeadNode() == null && multipleAsgnNode.getArgsNode() != null) {
                    // FIXME: This is gross. Don't do this.
                    argsNodeId = NodeType.SVALUENODE;
                }
            }
        }
        
        return argsNodeId;
    }
