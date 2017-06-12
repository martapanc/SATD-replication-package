File path: src/reports/org/apache/jmeter/report/gui/tree/ReportTreeModel.java
Comment: TODO can this ever happen?
Initial commit id: 4663f8f1
Final commit id: faf5bc05
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 88
End block index: 112
    public ReportTreeNode addComponent(TestElement component,
            ReportTreeNode node) throws IllegalUserActionException {
        if (node.getUserObject() instanceof AbstractConfigGui) {
            throw new IllegalUserActionException(
                    "This node cannot hold sub-elements");
        }
        ReportGuiPackage.getInstance().updateCurrentNode();
        JMeterGUIComponent guicomp = ReportGuiPackage.getInstance().getGui(component);
        guicomp.configure(component);
        guicomp.modifyTestElement(component);
        ReportGuiPackage.getInstance().getCurrentGui(); // put the gui object back
        // to the way it was.
        ReportTreeNode newNode = new ReportTreeNode(component, this);

        // This check the state of the TestElement and if returns false it
        // disable the loaded node
        try {
            newNode.setEnabled(component.isEnabled());
        } catch (Exception e) { // TODO can this ever happen?
            newNode.setEnabled(true);
        }

        this.insertNodeInto(newNode, node, node.getChildCount());
        return newNode;
    }
