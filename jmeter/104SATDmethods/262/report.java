File path: src/reports/org/apache/jmeter/report/gui/action/ReportStart.java
Comment: ME BROKEN CODE
Initial commit id: 8c8bf88c
Final commit id: faf5bc05
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 26
End block index: 104
/**
 * FIXME BROKEN CODE
 */
public class ReportStart extends AbstractAction {
    //private static final Logger log = LoggingManager.getLoggerForClass();

    private static final Set<String> commands = new HashSet<String>();
    static {
        commands.add(ActionNames.ACTION_START);
        commands.add(ActionNames.ACTION_STOP);
        commands.add(ActionNames.ACTION_SHUTDOWN);
    }
    // FIXME Due to startEngine being commented engine will always be null
    //private StandardJMeterEngine engine;

    /**
     * Constructor for the Start object.
     */
    public ReportStart() {
    }

    /**
     * Gets the ActionNames attribute of the Start object.
     *
     * @return the ActionNames value
     */
    @Override
    public Set<String> getActionNames() {
        return commands;
    }

    @Override
    public void doAction(ActionEvent e) {
        if (e.getActionCommand().equals(ActionNames.ACTION_START)) {
            popupShouldSave(e);
            startEngine();
        } else if (e.getActionCommand().equals(ActionNames.ACTION_STOP)) {
        	// FIXME engine is always null
//            if (engine != null) {
//                ReportGuiPackage.getInstance().getMainFrame().showStoppingMessage("");
//                engine.stopTest();
//                engine = null;
//            }
        } else if (e.getActionCommand().equals(ActionNames.ACTION_SHUTDOWN)) {
        	// FIXME engine is always null
//            if (engine != null) {
//                ReportGuiPackage.getInstance().getMainFrame().showStoppingMessage("");
//                engine.askThreadsToStop();
//                engine = null;
//            }
        }
    }

    protected void startEngine() {
        /**
         * this will need to be changed
        ReportGuiPackage gui = ReportGuiPackage.getInstance();
        engine = new StandardJMeterEngine();
        HashTree testTree = gui.getTreeModel().getTestPlan();
        convertSubTree(testTree);
        DisabledComponentRemover remover = new DisabledComponentRemover(testTree);
        testTree.traverse(remover);
        testTree.add(testTree.getArray()[0], gui.getMainFrame());
        log.debug("test plan before cloning is running version: "
                + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
        TreeCloner cloner = new TreeCloner(false);
        testTree.traverse(cloner);
        engine.configure(cloner.getClonedTree());
        try {
            engine.runTest();
        } catch (JMeterEngineException e) {
            JOptionPane.showMessageDialog(gui.getMainFrame(), e.getMessage(), JMeterUtils
                    .getResString("Error Occurred"), JOptionPane.ERROR_MESSAGE);
        }
        log.debug("test plan after cloning and running test is running version: "
                + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
         */
    }
}
