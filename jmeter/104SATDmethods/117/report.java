File path: src/core/org/apache/jmeter/gui/action/Load.java
Comment: TODO should setBaseForScript be called here rather than above?
Initial commit id: 4a5192431
Final commit id: e2fcfe848
   Bugs between [       1]:
571eb813f Bug 52707 - Make Open File dialog use last opened file folder as start folder Bugzilla Id: 52707
   Bugs after [       3]:
c6a82dfad Bug 60564 - Migrating LogKit to SLF4J - core/gui package (2/2) Contributed by Woonsan Ko This closes #272 Bugzilla Id: 60564
c93177faa Bug 60266 - Usability/ UX : It should not be possible to close/exit/Revert/Load/Load a recent project or create from template a JMeter plan or open a new one if a test is running Bugzilla Id: 60266
40b3221e7 Bug 57605 - When there is an error loading Test Plan, SaveService.loadTree returns null leading to NPE in callers Bugzilla Id: 57605

Start block index: 103
End block index: 142
    static void loadProjectFile(final ActionEvent e, final File f, final boolean merging) {
        ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.STOP_THREAD));

        final GuiPackage guiPackage = GuiPackage.getInstance();
        if (f != null) {
            InputStream reader = null;
            try {
                    if (merging) {
                        log.info("Merging file: " + f);
                    } else {
                        log.info("Loading file: " + f);
                        // TODO should this be done even if not a full test plan?
                        // and what if load fails?
                        FileServer.getFileServer().setBaseForScript(f);
                    }
                    reader = new FileInputStream(f);
                    final HashTree tree = SaveService.loadTree(reader);
                    final boolean isTestPlan = insertLoadedTree(e.getID(), tree, merging);

                    // don't change name if merging
                    if (!merging && isTestPlan) {
                        // TODO should setBaseForScript be called here rather than above?
                        guiPackage.setTestPlanFile(f.getAbsolutePath());
                    }
            } catch (NoClassDefFoundError ex) {// Allow for missing optional jars
                reportError("Missing jar file", ex, true);
            } catch (ConversionException ex) {
                log.warn("Could not convert file "+ex);
                JMeterUtils.reportErrorToUser(SaveService.CEtoString(ex));
            } catch (IOException ex) {
                reportError("Error reading file: ", ex, false);
            } catch (Exception ex) {
                reportError("Unexpected error", ex, true);
            } finally {
                JOrphanUtils.closeQuietly(reader);
            }
            guiPackage.updateCurrentGui();
            guiPackage.getMainFrame().repaint();
        }
    }

*********************** Method when SATD was removed **************************

static void loadProjectFile(final ActionEvent e, final File f, final boolean merging, final boolean setDetails) {
    ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.STOP_THREAD));

    final GuiPackage guiPackage = GuiPackage.getInstance();
    if (f != null) {
        InputStream reader = null;
        try {
            if (merging) {
                log.info("Merging file: " + f);
            } else {
                log.info("Loading file: " + f);
                // TODO should this be done even if not a full test plan?
                // and what if load fails?
                if (setDetails) {
                    FileServer.getFileServer().setBaseForScript(f);
                }
            }
            reader = new FileInputStream(f);
            final HashTree tree = SaveService.loadTree(reader);
            final boolean isTestPlan = insertLoadedTree(e.getID(), tree, merging);

            // don't change name if merging
            if (!merging && isTestPlan && setDetails) {
                // TODO should setBaseForScript be called here rather than
                // above?
                guiPackage.setTestPlanFile(f.getAbsolutePath());
            }
        } catch (NoClassDefFoundError ex) {// Allow for missing optional jars
            reportError("Missing jar file", ex, true);
        } catch (ConversionException ex) {
            log.warn("Could not convert file "+ex);
            JMeterUtils.reportErrorToUser(SaveService.CEtoString(ex));
        } catch (IOException ex) {
            reportError("Error reading file: ", ex, false);
        } catch (Exception ex) {
            reportError("Unexpected error", ex, true);
        } finally {
            JOrphanUtils.closeQuietly(reader);
        }
        FileDialoger.setLastJFCDirectory(f.getParentFile().getAbsolutePath());
        guiPackage.updateCurrentGui();
        guiPackage.getMainFrame().repaint();
    }
}
