File path: src/org/jruby/compiler/ir/IRBuilder.java
Comment: / TODO: is this right?
Initial commit id: f9c3ccc4
Final commit id: 9071ce26
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 2718
End block index: 2735
    public IRScope buildRoot(Node node) {
        // Top-level script!
        IRScript script = new IRScript("__file__", node.getPosition().getFile());
        IRClass  rootClass = script.dummyClass;
        IRMethod rootMethod = rootClass.getRootMethod();

        // Debug info: record file name
        rootMethod.addInstr(new FilenameInstr(node.getPosition().getFile()));

        // add a "self" recv here
        // TODO: is this right?
        rootMethod.addInstr(new ReceiveArgumentInstruction(rootClass.getSelf(), 0));

        RootNode rootNode = (RootNode) node;
        build(rootNode.getBodyNode(), rootMethod);

        return script;
    }
