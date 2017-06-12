    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = 4;

    // can be specified in jmeter.properties
    private static final String INIT_FILE = "beanshell.listener.init"; //$NON-NLS-1$

    @Override
    protected String getInitFileProperty() {
        return INIT_FILE;
    }
