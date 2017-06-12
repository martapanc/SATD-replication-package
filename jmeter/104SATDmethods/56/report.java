File path: src/core/org/apache/jmeter/gui/action/EditCommand.java
Comment: TODO: I believe the following code (to the end of the method) is
Initial commit id: e2d97b33a
Final commit id: 1139c450e
   Bugs between [       1]:
d6b7c4fd0 Bug 51822 - (part 1) save 1 invocation of GuiPackage#getCurrentGui
   Bugs after [       0]:


Start block index: 25
End block index: 47
    public void doAction(ActionEvent e)
    {
        GuiPackage guiPackage = GuiPackage.getInstance();
        guiPackage.getMainFrame().setMainPanel(
            (javax.swing.JComponent) guiPackage.getCurrentGui());
        guiPackage.getMainFrame().setEditMenu(
            ((JMeterGUIComponent) guiPackage
                .getTreeListener()
                .getCurrentNode())
                .createPopupMenu());
        // TODO: I believe the following code (to the end of the method) is obsolete,
        // since NamePanel no longer seems to be the GUI for any component:
        if (!(guiPackage.getCurrentGui() instanceof NamePanel))
        {
            guiPackage.getMainFrame().setFileLoadEnabled(true);
            guiPackage.getMainFrame().setFileSaveEnabled(true);
        }
        else
        {
            guiPackage.getMainFrame().setFileLoadEnabled(false);
            guiPackage.getMainFrame().setFileSaveEnabled(false);
        }
    }

*********************** Method when SATD was removed **************************

@Override
public void doAction(ActionEvent e) {
    GuiPackage guiPackage = GuiPackage.getInstance();
    JMeterGUIComponent currentGui = guiPackage.getCurrentGui();
    guiPackage.getMainFrame().setMainPanel((javax.swing.JComponent) currentGui);
    guiPackage.getMainFrame().setEditMenu(guiPackage.getTreeListener().getCurrentNode().createPopupMenu());
    guiPackage.getMainFrame().setFileLoadEnabled(true);
    guiPackage.getMainFrame().setFileSaveEnabled(true);
}
