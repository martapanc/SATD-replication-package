File path: src/reports/org/apache/jmeter/JMeterReport.java
Comment: NOTUSED: GuiPackage guiPack =
Initial commit id: 2fd58ada
Final commit id: e2697eb1
   Bugs between [       0]:

   Bugs after [       3]:
f023972db Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
5a32848af Bug 54199 - Move to Java 6 add @Override Bugzilla Id: 54199
e861ae37d Bug 36755 (patch 20073) - consistent closing of file streams

Start block index: 221
End block index: 251
    public void startGui(CLOption testFile) {
        PluginManager.install(this, true);
        JMeterTreeModel treeModel = new JMeterTreeModel();
        JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
        treeLis.setActionHandler(ActionRouter.getInstance());
        // NOTUSED: GuiPackage guiPack =
        GuiPackage.getInstance(treeLis, treeModel);
        org.apache.jmeter.gui.ReportMainFrame main = new org.apache.jmeter.gui.ReportMainFrame(ActionRouter.getInstance(),
                treeModel, treeLis);
        main.setTitle("Apache JMeter Report");
        main.setIconImage(JMeterUtils.getImage("jmeter.jpg").getImage());
        ComponentUtil.centerComponentInWindow(main, 80);
        main.show();
        ActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, CheckDirty.ADD_ALL));
        if (testFile != null) {
            try {
                File f = new File(testFile.getArgument());
                log.info("Loading file: " + f);
                FileInputStream reader = new FileInputStream(f);
                HashTree tree = SaveService.loadTree(reader);

                GuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());

                new Load().insertLoadedTree(1, tree);
            } catch (Exception e) {
                log.error("Failure loading test file", e);
                JMeterUtils.reportErrorToUser(e.toString());
            }
        }
        
    }
