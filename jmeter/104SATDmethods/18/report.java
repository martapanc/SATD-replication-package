File path: src/components/org/apache/jmeter/visualizers/BeanShellListener.java
Comment: TODO - remove UnsharedComponent ? Probably does not make sense for a TestBean.
Initial commit id: 3ed0e7c56
Final commit id: ed97da695
   Bugs between [       0]:

   Bugs after [       2]:
24c8763e3 Bug 60564 - Migrating LogKit to SLF4J - check log level in non-error logging if method invoked in params Contributed by Woonsan Ko This closes #268 Bugzilla Id: 60564
2c2756776 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Contributed by Woonsan Ko This closes #266 Bugzilla Id: 60564

Start block index: 34
End block index: 77
public class BeanShellListener extends BeanShellTestElement
    implements Cloneable, SampleListener, TestBean, UnsharedComponent  {
	// TODO - remove UnsharedComponent ? Probably does not make sense for a TestBean.

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = 4;

    // can be specified in jmeter.properties
    private static final String INIT_FILE = "beanshell.listener.init"; //$NON-NLS-1$

    protected String getInitFileProperty() {
        return INIT_FILE;
    }

	public void sampleOccurred(SampleEvent se) {
        final BeanShellInterpreter bshInterpreter = getBeanShellInterpreter();
		if (bshInterpreter == null) {
            log.error("BeanShell not found");
            return;
        }

        JMeterContext jmctx = JMeterContextService.getContext();
        JMeterVariables vars = jmctx.getVariables();
        SampleResult samp=se.getResult();
        try {
            // Add variables for access to context and variables
            bshInterpreter.set("ctx", jmctx);//$NON-NLS-1$
            bshInterpreter.set("vars", vars);//$NON-NLS-1$
            bshInterpreter.set("sampleEvent", se);//$NON-NLS-1$
            bshInterpreter.set("sampleResult", samp);//$NON-NLS-1$
            processFileOrScript(bshInterpreter);
        } catch (JMeterException e) {
            log.warn("Problem in BeanShell script "+e);
        }
	}

	public void sampleStarted(SampleEvent e) {
	}

	public void sampleStopped(SampleEvent e) {
	}

}

*********************** Method when SATD was removed **************************

public class BeanShellListener extends BeanShellTestElement
    implements Cloneable, SampleListener, TestBean, Visualizer, UnsharedComponent  {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = 4;

    // can be specified in jmeter.properties
    private static final String INIT_FILE = "beanshell.listener.init"; //$NON-NLS-1$

    @Override
    protected String getInitFileProperty() {
        return INIT_FILE;
    }

    @Override
    public void sampleOccurred(SampleEvent se) {
        final BeanShellInterpreter bshInterpreter = getBeanShellInterpreter();
        if (bshInterpreter == null) {
            log.error("BeanShell not found");
            return;
        }

        SampleResult samp=se.getResult();
        try {
            bshInterpreter.set("sampleEvent", se);//$NON-NLS-1$
            bshInterpreter.set("sampleResult", samp);//$NON-NLS-1$
            processFileOrScript(bshInterpreter);
        } catch (JMeterException e) {
            log.warn("Problem in BeanShell script "+e);
        }
    }

    @Override
    public void sampleStarted(SampleEvent e) {
        // NOOP
    }

    @Override
    public void sampleStopped(SampleEvent e) {
        // NOOP
    }

    @Override
    public void add(SampleResult sample) {
        // NOOP
    }

    @Override
    public boolean isStats() { // Needed by Visualizer interface
        return false;
    }

}
