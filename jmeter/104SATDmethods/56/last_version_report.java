    public void doAction(ActionEvent e) {
        GuiPackage guiPackage = GuiPackage.getInstance();
        JMeterGUIComponent currentGui = guiPackage.getCurrentGui();
        guiPackage.getMainFrame().setMainPanel((javax.swing.JComponent) currentGui);
        guiPackage.getMainFrame().setEditMenu(guiPackage.getTreeListener().getCurrentNode().createPopupMenu());
        guiPackage.getMainFrame().setFileLoadEnabled(true);
        guiPackage.getMainFrame().setFileSaveEnabled(true);
    }
