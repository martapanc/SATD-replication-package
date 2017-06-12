File path: src/components/org/apache/jmeter/visualizers/BeanShellListener.java
Comment: N.B. Needs to implement Visualizer so that TestBeanGUI can find the correct GUI class
Initial commit id: 224d6f6c
Final commit id: 67abdd71
   Bugs between [       0]:

   Bugs after [       2]:
24c8763e3 Bug 60564 - Migrating LogKit to SLF4J - check log level in non-error logging if method invoked in params Contributed by Woonsan Ko This closes #268 Bugzilla Id: 60564
2c2756776 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Contributed by Woonsan Ko This closes #266 Bugzilla Id: 60564

Start block index: 36
End block index: 39
	// N.B. Needs to implement Visualizer so that TestBeanGUI can find the correct GUI class
	// TODO - remove UnsharedComponent ? Probably does not make sense for a TestBean.
	
    private static final Logger log = LoggingManager.getLoggerForClass();
