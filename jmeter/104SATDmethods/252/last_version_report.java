    public void startGui(CLOption testFile) {
        PluginManager.install(this, true);
        ReportTreeModel treeModel = new ReportTreeModel();
        ReportTreeListener treeLis = new ReportTreeListener(treeModel);
        treeLis.setActionHandler(ReportActionRouter.getInstance());
        ReportGuiPackage.getInstance(treeLis, treeModel);
        org.apache.jmeter.gui.ReportMainFrame main = 
            new org.apache.jmeter.gui.ReportMainFrame(ReportActionRouter.getInstance(),
                treeModel, treeLis);
        main.setTitle("Apache JMeter Report");
        main.setIconImage(JMeterUtils.getImage("jmeter.jpg").getImage());
        ComponentUtil.centerComponentInWindow(main, 80);
        main.show();

        ReportActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, ReportCheckDirty.ADD_ALL));
        if (testFile != null) {
            try {
                File f = new File(testFile.getArgument());
                log.info("Loading file: " + f);
                FileInputStream reader = new FileInputStream(f);
                HashTree tree = SaveService.loadTree(reader);

                ReportGuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());

                new ReportLoad().insertLoadedTree(1, tree);
            } catch (Exception e) {
                log.error("Failure loading test file", e);
                JMeterUtils.reportErrorToUser(e.toString());
            }
        }
    }
