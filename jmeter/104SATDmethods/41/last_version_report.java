    public void start(String[] args) {
        CLArgsParser parser = new CLArgsParser(args, options);
        String error = parser.getErrorString();
        if (error == null){// Check option combinations
            boolean gui = parser.getArgumentById(NONGUI_OPT)==null;
            boolean nonGuiOnly = parser.getArgumentById(REMOTE_OPT)!=null
                               || parser.getArgumentById(REMOTE_OPT_PARAM)!=null
                               || parser.getArgumentById(REMOTE_STOP)!=null;
            if (gui && nonGuiOnly) {
                error = "-r and -R and -X are only valid in non-GUI mode";
            }
        }
        if (null != error) {
            System.err.println("Error: " + error);//NOSONAR
            System.out.println("Usage");//NOSONAR
            System.out.println(CLUtil.describeOptions(options).toString());//NOSONAR
            // repeat the error so no need to scroll back past the usage to see it
            System.out.println("Error: " + error);//NOSONAR
            return;
        }
        try {
            initializeProperties(parser); // Also initialises JMeter logging
            /*
             * The following is needed for HTTPClient.
             * (originally tried doing this in HTTPSampler2,
             * but it appears that it was done too late when running in GUI mode)
             * Set the commons logging default to Avalon Logkit, if not already defined
             */
            if (System.getProperty("org.apache.commons.logging.Log") == null) { // $NON-NLS-1$
                System.setProperty("org.apache.commons.logging.Log" // $NON-NLS-1$
                        , "org.apache.commons.logging.impl.LogKitLogger"); // $NON-NLS-1$
            }

            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {                
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    if (!(e instanceof ThreadDeath)) {
                        log.error("Uncaught exception: ", e);
                        System.err.println("Uncaught Exception " + e + ". See log file for details.");//NOSONAR
                    }
                }
            });

            log.info(JMeterUtils.getJMeterCopyright());
            log.info("Version " + JMeterUtils.getJMeterVersion());
            logProperty("java.version"); //$NON-NLS-1$
            logProperty("java.vm.name"); //$NON-NLS-1$
            logProperty("os.name"); //$NON-NLS-1$
            logProperty("os.arch"); //$NON-NLS-1$
            logProperty("os.version"); //$NON-NLS-1$
            logProperty("file.encoding"); // $NON-NLS-1$
            log.info("Max memory     ="+ Runtime.getRuntime().maxMemory());
            log.info("Available Processors ="+ Runtime.getRuntime().availableProcessors());
            log.info("Default Locale=" + Locale.getDefault().getDisplayName());
            log.info("JMeter  Locale=" + JMeterUtils.getLocale().getDisplayName());
            log.info("JMeterHome="     + JMeterUtils.getJMeterHome());
            logProperty("user.dir","  ="); //$NON-NLS-1$
            log.info("PWD       ="+new File(".").getCanonicalPath());//$NON-NLS-1$
            log.info("IP: "+JMeterUtils.getLocalHostIP()
                    +" Name: "+JMeterUtils.getLocalHostName()
                    +" FullName: "+JMeterUtils.getLocalHostFullName());
            setProxy(parser);

            updateClassLoader();
            if (log.isDebugEnabled())
            {
                String jcp=System.getProperty("java.class.path");// $NON-NLS-1$
                String[] bits = jcp.split(File.pathSeparator);
                log.debug("ClassPath");
                for(String bit : bits){
                    log.debug(bit);
                }
            }

            // Set some (hopefully!) useful properties
            long now=System.currentTimeMillis();
            JMeterUtils.setProperty("START.MS",Long.toString(now));// $NON-NLS-1$
            Date today=new Date(now); // so it agrees with above
            JMeterUtils.setProperty("START.YMD",new SimpleDateFormat("yyyyMMdd").format(today));// $NON-NLS-1$ $NON-NLS-2$
            JMeterUtils.setProperty("START.HMS",new SimpleDateFormat("HHmmss").format(today));// $NON-NLS-1$ $NON-NLS-2$

            if (parser.getArgumentById(VERSION_OPT) != null) {
                displayAsciiArt();
            } else if (parser.getArgumentById(HELP_OPT) != null) {
                displayAsciiArt();
                System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));//NOSONAR $NON-NLS-1$
            } else if (parser.getArgumentById(OPTIONS_OPT) != null) {
                displayAsciiArt();
                System.out.println(CLUtil.describeOptions(options).toString());//NOSONAR
            } else if (parser.getArgumentById(SERVER_OPT) != null) {
                // Start the server
                try {
                    RemoteJMeterEngineImpl.startServer(JMeterUtils.getPropDefault("server_port", 0)); // $NON-NLS-1$
                } catch (Exception ex) {
                    System.err.println("Server failed to start: "+ex);//NOSONAR
                    log.error("Giving up, as server failed with:", ex);
                    throw ex;
                }
                startOptionalServers();
            } else {
                String testFile=null;
                CLOption testFileOpt = parser.getArgumentById(TESTFILE_OPT);
                if (testFileOpt != null){
                    testFile = testFileOpt.getArgument();
                    if (USE_LAST_JMX.equals(testFile)) {
                        testFile = LoadRecentProject.getRecentFile(0);// most recent
                    }
                }
                CLOption testReportOpt = parser.getArgumentById(REPORT_GENERATING_OPT);
                if (testReportOpt != null) { // generate report from existing file
                    String reportFile = testReportOpt.getArgument();
                    extractAndSetReportOutputFolder(parser);
                    ReportGenerator generator = new ReportGenerator(reportFile, null);
                    generator.generate();
                } else if (parser.getArgumentById(NONGUI_OPT) == null) { // not non-GUI => GUI
                    startGui(testFile);
                    startOptionalServers();
                } else { // NON-GUI must be true
                    extractAndSetReportOutputFolder(parser);
                    
                    CLOption rem = parser.getArgumentById(REMOTE_OPT_PARAM);
                    if (rem == null) {
                        rem = parser.getArgumentById(REMOTE_OPT);
                    }
                    CLOption jtl = parser.getArgumentById(LOGFILE_OPT);
                    String jtlFile = null;
                    if (jtl != null) {
                        jtlFile = processLAST(jtl.getArgument(), ".jtl"); // $NON-NLS-1$
                    }
                    CLOption reportAtEndOpt = parser.getArgumentById(REPORT_AT_END_OPT);
                    if(reportAtEndOpt != null && jtlFile == null) {
                        throw new IllegalUserActionException(
                                "Option -"+ ((char)REPORT_AT_END_OPT)+" requires -"+((char)LOGFILE_OPT )+ " option");
                    }
                    startNonGui(testFile, jtlFile, rem, reportAtEndOpt != null);
                    startOptionalServers();
                }
            }
        } catch (IllegalUserActionException e) {// NOSONAR
            System.out.println("Incorrect Usage:"+e.getMessage());//NOSONAR
            System.out.println(CLUtil.describeOptions(options).toString());//NOSONAR
        } catch (Throwable e) { // NOSONAR
            log.fatalError("An error occurred: ",e);
            System.out.println("An error occurred: " + e.getMessage());//NOSONAR
            // FIXME Should we exit here ? If we are called by Maven or Jenkins
            System.exit(1);
        }
    }
