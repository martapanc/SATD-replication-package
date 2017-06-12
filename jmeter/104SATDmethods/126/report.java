File path: src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
Comment: TODO - can this eever happen?
Initial commit id: 4663f8f1a
Final commit id: 8cf39ed85
   Bugs between [       1]:
8cf39ed85 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
   Bugs after [       2]:
efc2f7569 Bug 60744 - GUI elements are not cleaned up when reused during load of Test Plan which can lead them to be partially initialized with a previous state for a new Test Element Bugzilla Id: 60744
eba38b513 Bug 58699 : Workbench changes neither saved nor prompted for saving upon close #resolve #45 Bugzilla Id: 58699

Start block index: 116
End block index: 143
    public JMeterTreeNode addComponent(TestElement component, JMeterTreeNode node) throws IllegalUserActionException {
        if (node.getUserObject() instanceof AbstractConfigGui) {
            throw new IllegalUserActionException("This node cannot hold sub-elements");
        }

        GuiPackage guiPackage = GuiPackage.getInstance();
        if (guiPackage != null) {
            // The node can be added in non GUI mode at startup
            guiPackage.updateCurrentNode();
            JMeterGUIComponent guicomp = guiPackage.getGui(component);
            guicomp.configure(component);
            guicomp.modifyTestElement(component);
            guiPackage.getCurrentGui(); // put the gui object back
                                        // to the way it was.
        }
        JMeterTreeNode newNode = new JMeterTreeNode(component, this);

        // This check the state of the TestElement and if returns false it
        // disable the loaded node
        try {
            newNode.setEnabled(component.isEnabled());
        } catch (Exception e) { // TODO - can this eever happen?
            newNode.setEnabled(true);
        }

        this.insertNodeInto(newNode, node, node.getChildCount());
        return newNode;
    }

*********************** Method when SATD was removed **************************

public JMeterTreeNode addComponent(TestElement component, JMeterTreeNode node) throws IllegalUserActionException {
    if (node.getUserObject() instanceof AbstractConfigGui) {
        throw new IllegalUserActionException("This node cannot hold sub-elements");
    }

    GuiPackage guiPackage = GuiPackage.getInstance();
    if (guiPackage != null) {
        // The node can be added in non GUI mode at startup
        guiPackage.updateCurrentNode();
        JMeterGUIComponent guicomp = guiPackage.getGui(component);
        guicomp.configure(component);
        guicomp.modifyTestElement(component);
        guiPackage.getCurrentGui(); // put the gui object back
                                    // to the way it was.
    }
    JMeterTreeNode newNode = new JMeterTreeNode(component, this);

    // This check the state of the TestElement and if returns false it
    // disable the loaded node
    try {
        newNode.setEnabled(component.isEnabled());
    } catch (Exception e) { // TODO - can this ever happen?
        newNode.setEnabled(true);
    }

    this.insertNodeInto(newNode, node, node.getChildCount());
    return newNode;
}
