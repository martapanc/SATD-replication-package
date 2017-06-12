SATD id: 100		Size: 2
    // TODO should the engine be static?
    private static final JexlEngine jexl = new JexlEngine();
*****************************************
*****************************************
SATD id: 107		Size: 117
    public void start(String[] args) {

        CLArgsParser parser = new CLArgsParser(args, options);
        String error = parser.getErrorString();
        if (error == null){// Check option combinations
            boolean gui = parser.getArgumentById(NONGUI_OPT)==null;
            boolean remoteStart = parser.getArgumentById(REMOTE_OPT)!=null
                               || parser.getArgumentById(REMOTE_OPT_PARAM)!=null;
            if (gui && remoteStart) {
                error = "-r and -R are only valid in non-GUI mode";
            }
        }
        if (null != error) {
            System.err.println("Error: " + error);
            System.out.println("Usage");
            System.out.println(CLUtil.describeOptions(options).toString());
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

            log.info(JMeterUtils.getJMeterCopyright());
            log.info("Version " + JMeterUtils.getJMeterVersion());
            logProperty("java.version"); //$NON-NLS-1$
            logProperty("java.vm.name"); //$NON-NLS-1$
            logProperty("os.name"); //$NON-NLS-1$
            logProperty("os.arch"); //$NON-NLS-1$
            logProperty("os.version"); //$NON-NLS-1$
            logProperty("file.encoding"); // $NON-NLS-1$
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
                String bits[] =jcp.split(File.pathSeparator);
                log.debug("ClassPath");
                for(int i = 0; i<bits.length ;i++){
                    log.debug(bits[i]);
                }
                log.debug(jcp);
            }

            // Set some (hopefully!) useful properties
            long now=System.currentTimeMillis();
            JMeterUtils.setProperty("START.MS",Long.toString(now));// $NON-NLS-1$
            Date today=new Date(now); // so it agrees with above
            // TODO perhaps should share code with __time() function for this...
            JMeterUtils.setProperty("START.YMD",new SimpleDateFormat("yyyyMMdd").format(today));// $NON-NLS-1$ $NON-NLS-2$
            JMeterUtils.setProperty("START.HMS",new SimpleDateFormat("HHmmss").format(today));// $NON-NLS-1$ $NON-NLS-2$

            if (parser.getArgumentById(VERSION_OPT) != null) {
                System.out.println(JMeterUtils.getJMeterCopyright());
                System.out.println("Version " + JMeterUtils.getJMeterVersion());
            } else if (parser.getArgumentById(HELP_OPT) != null) {
                System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));// $NON-NLS-1$
            } else if (parser.getArgumentById(SERVER_OPT) != null) {
                // Start the server
                startServer(JMeterUtils.getPropDefault("server_port", 0));// $NON-NLS-1$
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
                if (parser.getArgumentById(NONGUI_OPT) == null) {
                    startGui(testFile);
                    startOptionalServers();
                } else {
                    CLOption rem=parser.getArgumentById(REMOTE_OPT_PARAM);
                    if (rem==null) { rem=parser.getArgumentById(REMOTE_OPT); }
                    CLOption jtl = parser.getArgumentById(LOGFILE_OPT);
                    String jtlFile = null;
                    if (jtl != null){
                        jtlFile=processLAST(jtl.getArgument(), ".jtl"); // $NON-NLS-1$
                    }
                    startNonGui(testFile, jtlFile, rem);
                    startOptionalServers();
                }
            }
        } catch (IllegalUserActionException e) {
            System.out.println(e.getMessage());
            System.out.println("Incorrect Usage");
            System.out.println(CLUtil.describeOptions(options).toString());
        } catch (Throwable e) {
            if (log != null){
                log.fatalError("An error occurred: ",e);
            } else {
                e.printStackTrace();
            }
            System.out.println("An error occurred: " + e.getMessage());
            System.exit(1); // TODO - could this be return?
        }
    }
*****************************************
*****************************************
SATD id: 117		Size: 40
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
*****************************************
*****************************************
SATD id: 12		Size: 102
	public AssertionResult getResult(SampleResult inResponse) {
		log.debug("HTMLAssertions.getResult() called");

		// no error as default
		AssertionResult result = new AssertionResult();

		if (inResponse.getResponseData() == null) {
			return result.setResultForNull();
		}

		result.setFailure(false);

		// create parser
		Tidy tidy = null;
		try {
			log.debug("HTMLAssertions.getResult(): Setup tidy ...");
			log.debug("doctype: " + getDoctype());
			log.debug("errors only: " + isErrorsOnly());
			log.debug("error threshold: " + getErrorThreshold());
			log.debug("warning threshold: " + getWarningThreshold());
			log.debug("html mode: " + isHTML());
			log.debug("xhtml mode: " + isXHTML());
			log.debug("xml mode: " + isXML());
			tidy = new Tidy();
			tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
			tidy.setQuiet(false);
			tidy.setShowWarnings(true);
			tidy.setOnlyErrors(isErrorsOnly());
			tidy.setDocType(getDoctype());
			if (isXHTML()) {
				tidy.setXHTML(true);
			} else if (isXML()) {
				tidy.setXmlTags(true);
			}
			log.debug("err file: " + getFilename());
			tidy.setErrfile(getFilename());

			if (log.isDebugEnabled()) {
				log.debug("getParser : tidy parser created - " + tidy);
			}
			log.debug("HTMLAssertions.getResult(): Tidy instance created!");

		} catch (Exception e) {//TODO replace with proper Exception
			log.error("Unable to instantiate tidy parser", e);
			result.setFailure(true);
			result.setFailureMessage("Unable to instantiate tidy parser");
			// return with an error
			return result;
		}

		/*
		 * Run tidy.
		 */
		try {
			log.debug("HTMLAssertions.getResult(): start parsing with tidy ...");

			StringWriter errbuf = new StringWriter();
			tidy.setErrout(new PrintWriter(errbuf));
			// Node node = tidy.parseDOM(new
			// ByteArrayInputStream(response.getResponseData()), null);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			log.debug("Start : parse");
			Node node = tidy.parse(new ByteArrayInputStream(inResponse.getResponseData()), os);
			if (log.isDebugEnabled()) {
				log.debug("node : " + node);
			}
			log.debug("End   : parse");
			log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
			log.debug("Output: " + os.toString());

			// write output to file
			writeOutput(errbuf.toString());

			// evaluate result
			if ((tidy.getParseErrors() > getErrorThreshold())
					|| (!isErrorsOnly() && (tidy.getParseWarnings() > getWarningThreshold()))) {
				log.debug("HTMLAssertions.getResult(): errors/warnings detected:");
				log.debug(errbuf.toString());
				result.setFailure(true);
				result.setFailureMessage(MessageFormat.format("Tidy Parser errors:   " + tidy.getParseErrors()
						+ " (allowed " + getErrorThreshold() + ") " + "Tidy Parser warnings: "
						+ tidy.getParseWarnings() + " (allowed " + getWarningThreshold() + ")", new Object[0]));
				// return with an error

			} else if ((tidy.getParseErrors() > 0) || (tidy.getParseWarnings() > 0)) {
				// return with no error
				log.debug("HTMLAssertions.getResult(): there were errors/warnings but threshold to high");
				result.setFailure(false);
			} else {
				// return with no error
				log.debug("HTMLAssertions.getResult(): no errors/warnings detected:");
				result.setFailure(false);
			}

		} catch (Exception e) {//TODO replace with proper Exception
			// return with an error
			log.warn("Cannot parse result content", e);
			result.setFailure(true);
			result.setFailureMessage(e.getMessage());
		}
		return result;
	}
*****************************************
*****************************************
SATD id: 123		Size: 6
    public void createTest(BasicAttributes basicattributes, String string)
        throws NamingException
    {
    	//DirContext dc = //TODO perhaps return this?
        dirContext.createSubcontext(string, basicattributes);
    }
*****************************************
*****************************************
SATD id: 124		Size: 2
    public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
    // TODO - change to use URL version? Will this affect test plans?
*****************************************
*****************************************
SATD id: 126		Size: 28
    public JMeterTreeNode addComponent(TestElement component, JMeterTreeNode node) throws IllegalUserActionException {
        if (node.getUserObject() instanceof AbstractConfigGui) {
            throw new IllegalUserActionException("This node cannot hold sub-elements");
        }

        GuiPackage guiPackage = GuiPackage.getInstance();
        if (guiPackage != null) {
            // The node can be added in non GUI mode at startup
            guiPackage.updateCurrentNode();
            JMeterGUIComponent guicomp = guiPackage.getGui(component);
            guicomp.configure(component);
            guicomp.modifyTestElement(component);
            guiPackage.getCurrentGui(); // put the gui object back
                                        // to the way it was.
        }
        JMeterTreeNode newNode = new JMeterTreeNode(component, this);

        // This check the state of the TestElement and if returns false it
        // disable the loaded node
        try {
            newNode.setEnabled(component.isEnabled());
        } catch (Exception e) { // TODO - can this eever happen?
            newNode.setEnabled(true);
        }

        this.insertNodeInto(newNode, node, node.getChildCount());
        return newNode;
    }
*****************************************
*****************************************
SATD id: 136		Size: 4
    // TODO: make static?
	protected String encodeSpaces(String path) {
        return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
	}
*****************************************
*****************************************
SATD id: 139		Size: 1
    public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere 
*****************************************
*****************************************
SATD id: 140		Size: 73
    protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
        Iterator<URL> urls = null;
        try {
            final byte[] responseData = res.getResponseData();
            if (responseData.length > 0){  // Bug 39205
                String parserName = getParserClass(res);
                if(parserName != null)
                {
                    final HTMLParser parser =
                        parserName.length() > 0 ? // we have a name
                        HTMLParser.getParser(parserName)
                        :
                        HTMLParser.getParser(); // we don't; use the default parser
                    urls = parser.getEmbeddedResourceURLs(responseData, res.getURL());
                }
            }
        } catch (HTMLParseException e) {
            // Don't break the world just because this failed:
            res.addSubResult(errorResult(e, res));
            res.setSuccessful(false);
        }

        // Iterate through the URLs and download each image:
        if (urls != null && urls.hasNext()) {
            res = container;

            // Get the URL matcher
            String re=getEmbeddedUrlRE();
            Perl5Matcher localMatcher = null;
            Pattern pattern = null;
            if (re.length()>0){
                try {
                    pattern = JMeterUtils.getPattern(re);
                    localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                } catch (MalformedCachePatternException e) {
                    log.warn("Ignoring embedded URL match string: "+e.getMessage());
                }
            }
            while (urls.hasNext()) {
                Object binURL = urls.next(); // See catch clause below
                try {
                    URL url = (URL) binURL;
                    if (url == null) {
                        log.warn("Null URL detected (should not happen)");
                    } else {
                        String urlstr = url.toString();
                        String urlStrEnc=encodeSpaces(urlstr);
                        if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
                            try {
                                url = new URL(urlStrEnc);
                            } catch (MalformedURLException e) {
                                res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), res));
                                res.setSuccessful(false);
                                continue;
                            }
                        }
                        // I don't think localMatcher can be null here, but check just in case
                        if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                            continue; // we have a pattern and the URL does not match, so skip it
                        }
                        HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
                        res.addSubResult(binRes);
                        res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                    }
                } catch (ClassCastException e) { // TODO can this happen?
                    res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
                    res.setSuccessful(false);
                    continue;
                }
            }
        }
        return res;
    }
*****************************************
*****************************************
SATD id: 141		Size: 140

protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
        Iterator<URL> urls = null;
        try {
            final byte[] responseData = res.getResponseData();
            if (responseData.length > 0){  // Bug 39205
                String parserName = getParserClass(res);
                if(parserName != null)
                {
                    final HTMLParser parser =
                        parserName.length() > 0 ? // we have a name
                        HTMLParser.getParser(parserName)
                        :
                        HTMLParser.getParser(); // we don't; use the default parser
                    urls = parser.getEmbeddedResourceURLs(responseData, res.getURL());
                }
            }
        } catch (HTMLParseException e) {
            // Don't break the world just because this failed:
            res.addSubResult(errorResult(e, res));
            res.setSuccessful(false);
        }

        // Iterate through the URLs and download each image:
        if (urls != null && urls.hasNext()) {
            if (container == null) {
                // TODO needed here because currently done on sample completion in JMeterThread,
                // but that only catches top-level samples.
                res.setThreadName(Thread.currentThread().getName());
                container = new HTTPSampleResult(res);
                container.addRawSubResult(res);
            }
            res = container;

            // Get the URL matcher
            String re=getEmbeddedUrlRE();
            Perl5Matcher localMatcher = null;
            Pattern pattern = null;
            if (re.length()>0){
                try {
                    pattern = JMeterUtils.getPattern(re);
                    localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                } catch (MalformedCachePatternException e) {
                    log.warn("Ignoring embedded URL match string: "+e.getMessage());
                }
            }
            
            // For concurrent get resources
            final List<Callable<HTTPSampleResult>> liste = new ArrayList<Callable<HTTPSampleResult>>();
            
            while (urls.hasNext()) {
                Object binURL = urls.next(); // See catch clause below
                try {
                    URL url = (URL) binURL;
                    if (url == null) {
                        log.warn("Null URL detected (should not happen)");
                    } else {
                        String urlstr = url.toString();
                        String urlStrEnc=encodeSpaces(urlstr);
                        if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
                            try {
                                url = new URL(urlStrEnc);
                            } catch (MalformedURLException e) {
                                res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), res));
                                res.setSuccessful(false);
                                continue;
                            }
                        }
                        // I don't think localMatcher can be null here, but check just in case
                        if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                            continue; // we have a pattern and the URL does not match, so skip it
                        }
                        
                        if (isConcurrentDwn()) {
                            // if concurrent download emb. resources, add to a list for async gets later
                            liste.add(new ASyncSample(url, GET, false, frameDepth + 1));
                        } else {
                            // default: serial download embedded resources
                            HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
                            res.addSubResult(binRes);
                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                        }

                    }
                } catch (ClassCastException e) { // TODO can this happen?
                    res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
                    res.setSuccessful(false);
                    continue;
                }
            }
            
            // IF for download concurrent embedded resources
            if (isConcurrentDwn()) {
                int poolSize = CONCURRENT_POOL_SIZE; // init with default value
                try {
                    poolSize = Integer.parseInt(getConcurrentPool());
                } catch (NumberFormatException nfe) {
                    log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                            + "but pool size value is bad. Use default value");// $NON-NLS-1$
                }
                // Thread pool Executor to get resources 
                // use a LinkedBlockingQueue, note: max pool size doesn't effect
                final ThreadPoolExecutor exec = new ThreadPoolExecutor(
                        poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>());

                boolean tasksCompleted = false;
                try {
                    // sample all resources with threadpool
                    final List<Future<HTTPSampleResult>> retExec = exec.invokeAll(liste);
                    // call normal shutdown (wait ending all tasks)
                    exec.shutdown();
                    // put a timeout if tasks couldn't terminate
                    exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);

                    // add result to main sampleResult
                    for (Future<HTTPSampleResult> future : retExec) {
                        HTTPSampleResult binRes;
                        try {
                            binRes = future.get(1, TimeUnit.MILLISECONDS);
                            res.addSubResult(binRes);
                            res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                        } catch (TimeoutException e) {
                            errorResult(e, res);
                        }
                    }
                    tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
                } catch (InterruptedException ie) {
                    log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
                } catch (ExecutionException ee) {
                    log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                } finally {
                    if (!tasksCompleted) {
                        exec.shutdownNow(); // kill any remaining tasks
                    }
                }
            }
        }
        return res;
    }
*****************************************
*****************************************
SATD id: 158		Size: 48
    private SampleResult sampleWithListener(SampleResult result) {
        StringBuilder buffer = new StringBuilder();
        StringBuilder propBuffer = new StringBuilder();

        int loop = getIterationCount();
        int read = 0;

        long until = 0L;
        long now = System.currentTimeMillis();
        if (timeout > 0) {
            until = timeout + now; 
        }
        while (!interrupted
                && (until == 0 || now < until)
                && read < loop) {
            try {
                Message msg = queue.poll(calculateWait(until, now), TimeUnit.MILLISECONDS);
                if (msg != null) {
                    read++;
                    extractContent(buffer, propBuffer, msg);
                }
            } catch (InterruptedException e) {
                // Ignored
            }
            now = System.currentTimeMillis();
        }
        result.sampleEnd();
       
        if (getReadResponseAsBoolean()) {
            result.setResponseData(buffer.toString().getBytes());
        } else {
            result.setBytes(buffer.toString().getBytes().length);
        }
        result.setResponseHeaders(propBuffer.toString());
        result.setDataType(SampleResult.TEXT);
        if (read == 0) {
            result.setResponseCode("404"); // Not found
            result.setSuccessful(false);
        } else { // TODO set different status if not enough messages found?
            result.setResponseCodeOK();
            result.setSuccessful(true);
        }
        result.setResponseMessage(read + " messages received");
        result.setSamplerData(loop + " messages expected");
        result.setSampleCount(read);

        return result;
    }
*****************************************
*****************************************
SATD id: 162		Size: 4
    //TODO add some real tests now that split() has been removed
    public void test1() throws Exception{
    	
    }
*****************************************
*****************************************
SATD id: 173		Size: 33
	// TODO strings should be resources
	private String getFailText(String stringPattern) {
		String text;
		String what;
		if (ResponseAssertion.RESPONSE_DATA.equals(getTestField())) {
			what = "text";
		} else if (ResponseAssertion.RESPONSE_CODE.equals(getTestField())) {
			what = "code";
		} else if (ResponseAssertion.RESPONSE_MESSAGE.equals(getTestField())) {
			what = "message";
		} else // Assume it is the URL
		{
			what = "URL";
		}
		switch (getTestType()) {
		case CONTAINS:
			text = " expected to contain ";
			break;
		case NOT | CONTAINS:
			text = " expected not to contain ";
			break;
		case MATCH:
			text = " expected to match ";
			break;
		case NOT | MATCH:
			text = " expected not to match ";
			break;
		default:// should never happen...
			text = " expected something using ";
		}

		return "Test failed, " + what + text + "/" + stringPattern + "/";
	}
*****************************************
*****************************************
SATD id: 174		Size: 11
        public void warning(SAXParseException exception)
                throws SAXParseException 
        {

			String msg="warning: "+errorDetails(exception);
			log.debug(msg);
			result.setFailureMessage(msg);
			//result.setError(true); // TODO is this the correct strategy?
            //throw exception; // allow assertion to pass

        }
*****************************************
*****************************************
SATD id: 18		Size: 44
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
*****************************************
*****************************************
SATD id: 201		Size: 1
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
*****************************************
*****************************************
SATD id: 202		Size: 1
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
*****************************************
*****************************************
SATD id: 203		Size: 1
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
*****************************************
*****************************************
SATD id: 204		Size: 1
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
*****************************************
*****************************************
SATD id: 205		Size: 1
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
*****************************************
*****************************************
SATD id: 206		Size: 1
	 private ArrayList mChangeListeners = new ArrayList(3);  // Maybe move to vector if MT problems occur
*****************************************
*****************************************
SATD id: 207		Size: 248
    public SampleResult sample(Entry e) {
        Message message = null;
        SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        boolean isOK = false; // Did sample succeed?
        SendMailCommand instance = new SendMailCommand();
        instance.setSmtpServer(getPropertyAsString(SmtpSampler.SERVER));
        instance.setSmtpPort(getPropertyAsString(SmtpSampler.SERVER_PORT));

        instance.setUseSSL(getPropertyAsBoolean(USE_SSL));
        instance.setUseStartTLS(getPropertyAsBoolean(USE_STARTTLS));
        instance.setTrustAllCerts(getPropertyAsBoolean(SSL_TRUST_ALL_CERTS));
        instance.setEnforceStartTLS(getPropertyAsBoolean(ENFORCE_STARTTLS));

        instance.setUseAuthentication(getPropertyAsBoolean(USE_AUTH));
        instance.setUsername(getPropertyAsString(USERNAME));
        instance.setPassword(getPropertyAsString(PASSWORD));

        instance.setUseLocalTrustStore(getPropertyAsBoolean(USE_LOCAL_TRUSTSTORE));
        instance.setTrustStoreToUse(getPropertyAsString(TRUSTSTORE_TO_USE));
        instance.setEmlMessage(getPropertyAsString(EML_MESSAGE_TO_SEND));
        instance.setUseEmlMessage(getPropertyAsBoolean(USE_EML));

        if (getMailFrom().matches(".*@.*")) {
            instance.setSender(getMailFrom());
        }

        try {
            if (!getPropertyAsBoolean(USE_EML)) { // part is only needed if we
                // don't send an .eml-file

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                List<InternetAddress> receiversTo = new Vector<InternetAddress>();
                if (getPropertyAsString(SmtpSampler.RECEIVER_TO).matches(".*@.*")) {
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_TO))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversTo.add(new InternetAddress(strReceivers[i].trim()));
                    }
                } else {
                    receiversTo.add(new InternetAddress(getMailFrom()));
                }

                instance.setReceiverTo(receiversTo);

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_CC).matches(".*@.*")) {
                    List<InternetAddress> receiversCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_CC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversCC.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverCC(receiversCC);
                }

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_BCC).matches(".*@.*")) {
                    List<InternetAddress> receiversBCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_BCC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversBCC.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverBCC(receiversBCC);
                }

                MailBodyProvider mb = new MailBodyProvider();
                if (getPropertyAsString(MESSAGE) != null
                        && !getPropertyAsString(MESSAGE).equals(""))
                    mb.setBody(getPropertyAsString(MESSAGE));
                instance.setMbProvider(mb);

                if (!getAttachments().equals("")) {
                    String[] attachments = getAttachments().split(FILENAME_SEPARATOR);
                    for (String attachment : attachments) {
                        instance.addAttachment(new File(attachment));
                    }
                }

                instance.setSubject(getPropertyAsString(SUBJECT)
                                + (getPropertyAsBoolean(INCLUDE_TIMESTAMP) ? " <<< current timestamp: "
                                        + new Date().getTime() + " >>>"
                                        : ""));
            } else {

                // send an .eml-file

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_TO).matches(".*@.*")) {
                    List<InternetAddress> receiversTo = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_TO))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversTo.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverTo(receiversTo);
                }

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_CC).matches(".*@.*")) {
                    List<InternetAddress> receiversCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_CC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversCC.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverCC(receiversCC);
                }

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_BCC).matches(
                        ".*@.*")) {
                    List<InternetAddress> receiversBCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_BCC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversBCC.add(new InternetAddress(strReceivers[i]
                                .trim()));
                    }
                    instance.setReceiverBCC(receiversBCC);
                }

                String subj = getPropertyAsString(SUBJECT);
                if (subj.trim().length() > 0) {
                    instance.setSubject(subj
                            + (getPropertyAsBoolean(INCLUDE_TIMESTAMP) ? " <<< current timestamp: "
                                    + new Date().getTime() + " >>>"
                                    : ""));
                }
            }
            // needed for measuring sending time
            instance.setSynchronousMode(true);

            message = instance.prepareMessage();

            if (getPropertyAsBoolean(MESSAGE_SIZE_STATS)) {
                // calculate message size
                CounterOutputStream cs = new CounterOutputStream();
                message.writeTo(cs);
                res.setBytes(cs.getCount());
            } else {
                res.setBytes(-1);
            }

        } catch (AddressException ex) {
            log.warn("Error while preparing message", ex);
            return res;
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        } catch (MessagingException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        // Perform the sampling
        res.sampleStart();

        try {
            instance.execute(message);

            // Set up the sample result details
            res.setSamplerData("To: "
                    + getPropertyAsString(SmtpSampler.RECEIVER_TO) + "\nCC: "
                    + getPropertyAsString(SmtpSampler.RECEIVER_CC) + "\nBCC: "
                    + getPropertyAsString(SmtpSampler.RECEIVER_BCC));
            res.setDataType(SampleResult.TEXT);
            res.setResponseCodeOK();
            /*
             * TODO if(instance.getSMTPStatusCode == 250)
             * res.setResponseMessage("Message successfully sent!"); else
             * res.setResponseMessage(instance.getSMTPStatusCodeIncludingMessage);
             */
            res.setResponseMessage("Message successfully sent!\n"
                    + instance.getServerResponse());
            isOK = true;
        }
        // username / password incorrect
        catch (AuthenticationFailedException afex) {
            log.warn("", afex);
            res.setResponseCode("500");
            res.setResponseMessage("AuthenticationFailedException: authentication failed - wrong username / password!\n"
                            + afex);
        }
        // SSL not supported, startTLS not supported, other messagingException
        catch (MessagingException mex) {
            log.warn("",mex);
            res.setResponseCode("500");
            if (mex.getMessage().matches(
                    ".*Could not connect to SMTP host.*465.*")
                    && mex.getCause().getMessage().matches(
                            ".*Connection timed out.*")) {
                res.setResponseMessage("MessagingException: Probably, SSL is not supported by the SMTP-Server!\n"
                                + mex);
            } else if (mex.getMessage().matches(".*StartTLS failed.*")) {
                res.setResponseMessage("MessagingException: StartTLS not supported by server or initializing failed!\n"
                                + mex);
            } else if (mex.getMessage().matches(".*send command to.*")
                    && mex.getCause().getMessage().matches(
                                    ".*unable to find valid certification path to requested target.*")) {
                res.setResponseMessage("MessagingException: Server certificate not trusted - perhaps you have to restart JMeter!\n"
                                + mex);
            } else {
                res.setResponseMessage("Other MessagingException: "
                        + mex.toString());
            }
        }
        // general exception
        catch (Exception ex) {
            res.setResponseCode("500");
            if (null != ex.getMessage()
                    && ex.getMessage().matches("Failed to build truststore")) {
                res.setResponseMessage("Failed to build truststore - did not try to send mail!");
            } else {
                res.setResponseMessage("Other Exception: " + ex.toString());
            }
        }

        res.sampleEnd();

        try {
            // process the sampler result
            InputStream is = message.getInputStream();
            StringBuffer sb = new StringBuffer();
            byte[] buf = new byte[1024];
            int read = is.read(buf);
            while (read > 0) {
                sb.append(new String(buf, 0, read));
                read = is.read(buf);
            }
            res.setResponseData(sb.toString().getBytes()); // TODO this should really be request data, but there is none
        } catch (IOException ex) {
            log.warn("",ex);
        } catch (MessagingException ex) {
            log.warn("",ex);
        }

        res.setSuccessful(isOK);

        return res;
    }
*****************************************
*****************************************
SATD id: 208		Size: 4
	// N.B. Needs to implement Visualizer so that TestBeanGUI can find the correct GUI class
	// TODO - remove UnsharedComponent ? Probably does not make sense for a TestBean.
	
    private static final Logger log = LoggingManager.getLoggerForClass();
*****************************************
*****************************************
SATD id: 209		Size: 137
	public void valueChanged(TreeSelectionEvent e) {
		log.debug("Start : valueChanged1");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

		if (log.isDebugEnabled()) {
			log.debug("valueChanged : selected node - " + node);
		}

		StyledDocument statsDoc = stats.getStyledDocument();
		try {
			statsDoc.remove(0, statsDoc.getLength());
			sampleDataField.setText(""); // $NON-NLS-1$
			results.setText(""); // $NON-NLS-1$
			if (node != null) {
				Object userObject = node.getUserObject();
				if(userObject instanceof SampleResult) {					
					SampleResult res = (SampleResult) userObject;
					
					// We are displaying a SampleResult
					setupTabPaneForSampleResult();

					if (log.isDebugEnabled()) {
						log.debug("valueChanged1 : sample result - " + res);
					}

					if (res != null) {
						// load time label

						log.debug("valueChanged1 : load time - " + res.getTime());
						String sd = res.getSamplerData();
						if (sd != null) {
							String rh = res.getRequestHeaders();
							if (rh != null) {
								StringBuffer sb = new StringBuffer(sd.length() + rh.length()+20);
								sb.append(sd);
								sb.append("\nRequest Headers:\n");
								sb.append(rh);
								sd = sb.toString();
							}
							sampleDataField.setText(sd);
						}

						StringBuffer statsBuff = new StringBuffer(200);
						statsBuff.append("Thread Name: ").append(res.getThreadName()).append(NL);
						String startTime = dateFormat.format(new Date(res.getStartTime()));
						statsBuff.append("Sample Start: ").append(startTime).append(NL);
						statsBuff.append("Load time: ").append(res.getTime()).append(NL);
						statsBuff.append("Size in bytes: ").append(res.getBytes()).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
						statsBuff = new StringBuffer(); //reset for reuse
						
						String responseCode = res.getResponseCode();
						log.debug("valueChanged1 : response code - " + responseCode);

						int responseLevel = 0;
						if (responseCode != null) {
							try {
								responseLevel = Integer.parseInt(responseCode) / 100;
							} catch (NumberFormatException numberFormatException) {
								// no need to change the foreground color
							}
						}

						Style style = null;
						switch (responseLevel) {
						case 3:
							style = statsDoc.getStyle(STYLE_REDIRECT);
							break;
						case 4:
							style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
							break;
						case 5:
							style = statsDoc.getStyle(STYLE_SERVER_ERROR);
							break;
						}

						statsBuff.append("Response code: ").append(responseCode).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), style);
						statsBuff = new StringBuffer(100); //reset for reuse

						// response message label
						String responseMsgStr = res.getResponseMessage();

						log.debug("valueChanged1 : response message - " + responseMsgStr);
						statsBuff.append("Response message: ").append(responseMsgStr).append(NL);

						statsBuff.append(NL).append("Response headers:").append(NL);
						statsBuff.append(res.getResponseHeaders()).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
						statsBuff = null; // Done

						// get the text response and image icon
						// to determine which is NOT null
						if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null) is OK
						{
							String response = getResponseAsString(res);
							if (command.equals(TEXT_COMMAND)) {
								showTextResponse(response);
							} else if (command.equals(HTML_COMMAND)) {
								showRenderedResponse(response, res);
							} else if (command.equals(XML_COMMAND)) {
								showRenderXMLResponse(response);
							}
						} else {
							byte[] responseBytes = res.getResponseData();
							if (responseBytes != null) {
								showImage(new ImageIcon(responseBytes)); //TODO implement other non-text types
							}
						}
					}
				}
				else if(userObject instanceof AssertionResult) {
					AssertionResult res = (AssertionResult) userObject;
					
					// We are displaying an AssertionResult
					setupTabPaneForAssertionResult();
					
					if (log.isDebugEnabled()) {
						log.debug("valueChanged1 : sample result - " + res);
					}

					if (res != null) {
						StringBuffer statsBuff = new StringBuffer(100);
						statsBuff.append("Assertion error: ").append(res.isError()).append(NL);
						statsBuff.append("Assertion failure: ").append(res.isFailure()).append(NL);
						statsBuff.append("Assertion failure message : ").append(res.getFailureMessage()).append(NL);
						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
						statsBuff = null;
					}
				}
			}
		} catch (BadLocationException exc) {
			log.error("Error setting statistics text", exc);
			stats.setText("");
		}
		log.debug("End : valueChanged1");
	}
*****************************************
*****************************************
SATD id: 21		Size: 83
    public SampleResult sample(Entry entry) {
        SampleResult results = new SampleResult();
        results.setDataType(SampleResult.TEXT);
        results.setSampleLabel(getName());
        
        String command = getCommand();
        Arguments args = getArguments();
        Arguments environment = getEnvironmentVariables();
        boolean checkReturnCode = getCheckReturnCode();
        int expectedReturnCode = getExpectedReturnCode();
        List<String> cmds = new ArrayList<String>(args.getArgumentCount()+1);
        StringBuilder cmdLine = new StringBuilder((null == command) ? "" : command);
        cmds.add(command);
        for (int i=0;i<args.getArgumentCount();i++) {
            Argument arg = args.getArgument(i);
            cmds.add(arg.getPropertyAsString(Argument.VALUE));
            cmdLine.append(" ");
            cmdLine.append(cmds.get(i+1));
        }

        Map<String,String> env = new HashMap<String, String>();
        for (int i=0;i<environment.getArgumentCount();i++) {
            Argument arg = environment.getArgument(i);
            env.put(arg.getName(), arg.getPropertyAsString(Argument.VALUE));
        }
        
        File directory = null;
        if(StringUtils.isEmpty(getDirectory())) {
            directory = new File(FileServer.getDefaultBase());
            if(log.isDebugEnabled()) {
                log.debug("Using default directory:"+directory.getAbsolutePath());
            }
        } else {
            directory = new File(getDirectory());
            if(log.isDebugEnabled()) {
                log.debug("Using configured directory:"+directory.getAbsolutePath());
            }
        }
        
        if(log.isDebugEnabled()) {
            log.debug("Will run :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                    " with environment:"+env);
        }

        results.setSamplerData("Working Directory:"+directory.getAbsolutePath()+
                "\nEnvironment:"+env+
                "\nExecuting:" + cmdLine.toString());
        
        SystemCommand nativeCommand = new SystemCommand(directory, env, getStdin(), getStdout(), getStderr());
        
        try {
            results.sampleStart();
            int returnCode = nativeCommand.run(cmds);
            results.sampleEnd();
            results.setResponseCode(Integer.toString(returnCode)); // TODO is this the best way to do this?
            if(log.isDebugEnabled()) {
                log.debug("Ran :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                        " with execution environment:"+nativeCommand.getExecutionEnvironment()+ " => " + returnCode);
            }

            if (checkReturnCode && (returnCode != expectedReturnCode)) {
                results.setSuccessful(false);
                results.setResponseMessage("Uexpected return code.  Expected ["+expectedReturnCode+"]. Actual ["+returnCode+"].");
            } else {
                results.setSuccessful(true);
                results.setResponseMessage("OK");
            }
        } catch (IOException ioe) {
            results.sampleEnd();
            results.setSuccessful(false);
            // results.setResponseCode("???"); TODO what code should be set here?
            results.setResponseMessage("Exception occured whilst executing System Call: " + ioe);
        } catch (InterruptedException ie) {
            results.sampleEnd();
            results.setSuccessful(false);
            // results.setResponseCode("???"); TODO what code should be set here?
            results.setResponseMessage("System Sampler Interupted whilst executing System Call: " + ie);
        }

        results.setResponseData(nativeCommand.getOutResult().getBytes()); // default charset is deliberate here
            
        return results;
    }
*****************************************
*****************************************
SATD id: 210		Size: 3
    
    //NOT USED protected double[][] data = null;
    
*****************************************
*****************************************
SATD id: 211		Size: 110
	 * A Dom tree panel for to display response as tree view author <a
	 * href="mailto:d.maung@mdl.com">Dave Maung</a> TODO implement to find any
	 * nodes in the tree using TreePath.
	 * 
	 */
	private class DOMTreePanel extends JPanel {

		private JTree domJTree;

		public DOMTreePanel(org.w3c.dom.Document document) {
			super(new GridLayout(1, 0));
			try {
				Node firstElement = getFirstElement(document);
				DefaultMutableTreeNode top = new XMLDefaultMutableTreeNode(firstElement);
				domJTree = new JTree(top);

				domJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				domJTree.setShowsRootHandles(true);
				JScrollPane domJScrollPane = new JScrollPane(domJTree);
				domJTree.setAutoscrolls(true);
				this.add(domJScrollPane);
				ToolTipManager.sharedInstance().registerComponent(domJTree);
				domJTree.setCellRenderer(new DomTreeRenderer());
				this.setPreferredSize(new Dimension(800, 600));
			} catch (SAXException e) {
				log.warn("", e);
			}

		}

		/**
		 * Skip all DTD nodes, all prolog nodes. They dont support in tree view
		 * We let user to insert them however in DOMTreeView, we dont display it
		 * 
		 * @param root
		 * @return
		 */
		private Node getFirstElement(Node parent) {
			NodeList childNodes = parent.getChildNodes();
			Node toReturn = null;
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				toReturn = childNode;
				if (childNode.getNodeType() == Node.ELEMENT_NODE)
					break;

			}
			return toReturn;
		}

		/**
		 * This class is to view as tooltext. This is very useful, when the
		 * contents has long string and does not fit in the view. it will also
		 * automatically wrap line for each 100 characters since tool tip
		 * support html. author <a href="mailto:d.maung@mdl.com">Dave Maung</a>
		 */
		private class DomTreeRenderer extends DefaultTreeCellRenderer {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean phasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, phasFocus);

				DefaultMutableTreeNode valueTreeNode = (DefaultMutableTreeNode) value;
				setToolTipText(getHTML(valueTreeNode.toString(), "<br>", 100));
				return this;
			}

			/**
			 * get the html
			 * 
			 * @param str
			 * @param separator
			 * @param maxChar
			 * @return
			 */
			private String getHTML(String str, String separator, int maxChar) {
				StringBuffer strBuf = new StringBuffer("<html><body bgcolor=\"yellow\"><b>");
				char[] chars = str.toCharArray();
				for (int i = 0; i < chars.length; i++) {

					if (i % maxChar == 0 && i != 0)
						strBuf.append(separator);
					strBuf.append(encode(chars[i]));

				}
				strBuf.append("</b></body></html>");
				return strBuf.toString();

			}

			private String encode(char c) {
				String toReturn = String.valueOf(c);
				switch (c) {
				case '<':
					toReturn = "&lt;";
					break;
				case '>':
					toReturn = "&gt;";
					break;
				case '\'':
					toReturn = "&apos;";
					break;
				case '\"':
					toReturn = "&quot;";
					break;

				}
				return toReturn;
			}
		}
	}
*****************************************
*****************************************
SATD id: 212		Size: 12
    public boolean interrupt() {
        HttpClient client = savedClient;
        if (client != null) {
            savedClient = null;
            // TODO - not sure this is the best method
            final HttpConnectionManager httpConnectionManager = client.getHttpConnectionManager();
            if (httpConnectionManager instanceof SimpleHttpConnectionManager) {// Should be true
                ((SimpleHttpConnectionManager)httpConnectionManager).shutdown();
            }
        }
        return client != null;
    }
*****************************************
*****************************************
SATD id: 213		Size: 12
    public boolean interrupt() {
        HttpClient client = savedClient;
        if (client != null) {
            savedClient = null;
            // TODO - not sure this is the best method
            final HttpConnectionManager httpConnectionManager = client.getHttpConnectionManager();
            if (httpConnectionManager instanceof SimpleHttpConnectionManager) {// Should be true
                ((SimpleHttpConnectionManager)httpConnectionManager).shutdown();
            }
        }
        return client != null;
    }
*****************************************
*****************************************
SATD id: 214		Size: 3
	public JPanel getControlPanel() {// TODO - is this needed?
		return this;
	}
*****************************************
*****************************************
SATD id: 215		Size: 2
    @SuppressWarnings("unchecked") // Method is broken anyway
    public List<SamplingStatCalculator> getStats(List urls);
*****************************************
*****************************************
SATD id: 216		Size: 12
    @SuppressWarnings("unchecked") // Method is broken anyway
    public List getStats(List urls) {
        ArrayList items = new ArrayList();
        Iterator itr = urls.iterator();
        if (itr.hasNext()) {
            SamplingStatCalculator row = (SamplingStatCalculator)itr.next();
            if (row != null) {
                items.add(row);
            }
        }
        return items;
    }
*****************************************
*****************************************
SATD id: 217		Size: 10
		 * Not clear what can cause this
         * ? conflicting versions perhaps
		 */
        public void warning(SAXParseException exception)
                throws SAXParseException 
        {
             msg="warning: "+errorDetails(exception);
			 log.debug(msg);
			 messageType = JOptionPane.WARNING_MESSAGE;
        }
*****************************************
*****************************************
SATD id: 218		Size: 4
	 * Mask values for TEST_TYPE TODO: remove either MATCH or CONTAINS - they
	 * are mutually exckusive
	 */
	private final static int MATCH = 1 << 0;
*****************************************
*****************************************
SATD id: 219		Size: 13
	private JPanel createFilenamePanel()// TODO ought to be a FileChooser ...
	{
		JLabel label = new JLabel(JMeterUtils.getResString("bsf_script_file"));

		filename = new JTextField(10);
		filename.setName(BSFSampler.FILENAME);
		label.setLabelFor(filename);

		JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
		filenamePanel.add(label, BorderLayout.WEST);
		filenamePanel.add(filename, BorderLayout.CENTER);
		return filenamePanel;
	}
*****************************************
*****************************************
SATD id: 220		Size: 13
	private JPanel createFilenamePanel()// TODO ought to be a FileChooser ...
	{
		JLabel label = new JLabel(JMeterUtils.getResString("resultsaver_prefix")); // $NON-NLS-1$

		filename = new JTextField(10);
		filename.setName(ResultSaver.FILENAME);
		label.setLabelFor(filename);

		JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
		filenamePanel.add(label, BorderLayout.WEST);
		filenamePanel.add(filename, BorderLayout.CENTER);
		return filenamePanel;
	}
*****************************************
*****************************************
SATD id: 221		Size: 19
		private static class LocalHTMLFactory extends javax.swing.text.html.HTMLEditorKit.HTMLFactory {
			/*
			 * Provide dummy implementations to suppress download and display of
			 * related resources: - FRAMEs - IMAGEs TODO create better dummy
			 * displays TODO suppress LINK somehow
			 */
			public View create(Element elem) {
				Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
				if (o instanceof HTML.Tag) {
					HTML.Tag kind = (HTML.Tag) o;
					if (kind == HTML.Tag.FRAME) {
						return new ComponentView(elem);
					} else if (kind == HTML.Tag.IMG) {
						return new ComponentView(elem);
					}
				}
				return super.create(elem);
			}
		}
*****************************************
*****************************************
SATD id: 222		Size: 4
    public synchronized static void saveTestElement(TestElement elem, Writer writer) throws IOException {
        JMXSAVER.toXML(elem, writer); // TODO should this be JTLSAVER? Only seems to be called by MonitorHealthVisualzer
        writer.write('\n');
    }
*****************************************
*****************************************
SATD id: 223		Size: 48
    public Sampler next()
    {
		Sampler returnValue = null;
    	if (isFirst()) // must be the start of the subtree
    	{
    		log_debug("+++++++++++++++++++++++++++++");
    		calls = 0;
    		res = new SampleResult();
    		res.sampleStart();
    	}
    	
    	calls++;
    	
    	returnValue = super.next();

        if (returnValue == null) // Must be the end of the controller
        {
			log_debug("-----------------------------"+calls);
        	if (res == null){
        		log_debug("already called");
        	} else {
				res.sampleEnd();
				res.setSuccessful(true);
				res.setSampleLabel(getName());
				res.setResponseCode("200");
				res.setResponseMessage("Called: "+calls);
				res.setThreadName(threadName);
        	
				//TODO could these be done earlier (or just once?)
				threadContext = JMeterContextService.getContext();
				threadVars = threadContext.getVariables();
				
				SamplePackage pack = (SamplePackage)
				              threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
				if (pack == null)
				{
					log.warn("Could not fetch SamplePackage");
				}
				else 
				{
					lnf.notifyListeners(new SampleEvent(res,getName()),pack.getSampleListeners());
				}
				res=null;
        	}
        }

        return returnValue;
    }
*****************************************
*****************************************
SATD id: 224		Size: 48
    public Sampler next()
    {
		Sampler returnValue = null;
    	if (isFirst()) // must be the start of the subtree
    	{
    		log_debug("+++++++++++++++++++++++++++++");
    		calls = 0;
    		res = new SampleResult();
    		res.sampleStart();
    	}
    	
    	calls++;
    	
    	returnValue = super.next();

        if (returnValue == null) // Must be the end of the controller
        {
			log_debug("-----------------------------"+calls);
        	if (res == null){
        		log_debug("already called");
        	} else {
				res.sampleEnd();
				res.setSuccessful(true);
				res.setSampleLabel(getName());
				res.setResponseCode("200");
				res.setResponseMessage("Called: "+calls);
				res.setThreadName(threadName);
        	
				//TODO could these be done earlier (or just once?)
				threadContext = JMeterContextService.getContext();
				threadVars = threadContext.getVariables();
				
				SamplePackage pack = (SamplePackage)
				              threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
				if (pack == null)
				{
					log.warn("Could not fetch SamplePackage");
				}
				else 
				{
					lnf.notifyListeners(new SampleEvent(res,getName()),pack.getSampleListeners());
				}
				res=null;
        	}
        }

        return returnValue;
    }
*****************************************
*****************************************
SATD id: 225		Size: 1
	 * NOTUSED private void initButtonMap() { }
*****************************************
*****************************************
SATD id: 226		Size: 80
	static {
		Properties props = JMeterUtils.getJMeterProperties();

        _subresults      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
        _assertions      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
        _latency         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
        _samplerData     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
        _responseHeaders = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
        _requestHeaders  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
        _encoding        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));

        String dlm = props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER);
        if (dlm.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
        	dlm="\t";
        }
		_delimiter = dlm;

		_fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));

		_dataType = TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));

		_label = TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));

		_code = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));

		_responseData = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));

		_responseDataOnError = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_ON_ERROR_PROP, FALSE));

		_message = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));

		_success = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));

		_threadName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));

		_bytes = TRUE.equalsIgnoreCase(props.getProperty(SAVE_BYTES_PROP, TRUE));

		_url = TRUE.equalsIgnoreCase(props.getProperty(SAVE_URL_PROP, FALSE));

		_fileName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_FILENAME_PROP, FALSE));

		_time = TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));

		_timeStampFormat = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);

		_printMilliseconds = MILLISECONDS.equalsIgnoreCase(_timeStampFormat);

		// Prepare for a pretty date
		if (!_printMilliseconds && !NONE.equalsIgnoreCase(_timeStampFormat) && (_timeStampFormat != null)) {
			_formatter = new SimpleDateFormat(_timeStampFormat);
		} else {
			_formatter = null;
		}

		_timestamp = !NONE.equalsIgnoreCase(_timeStampFormat);// reversed compare allows for null

		_saveAssertionResultsFailureMessage = TRUE.equalsIgnoreCase(props.getProperty(
				ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, FALSE));

		String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
		if (NONE.equals(whichAssertionResults)) {
			_assertionsResultsToSave = SAVE_NO_ASSERTIONS;
		} else if (FIRST.equals(whichAssertionResults)) {
			_assertionsResultsToSave = SAVE_FIRST_ASSERTION;
		} else if (ALL.equals(whichAssertionResults)) {
			_assertionsResultsToSave = SAVE_ALL_ASSERTIONS;
		}

		String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, XML);

		if (XML.equals(howToSave)) {
			_xml = true;
		} else {
			_xml = false;
		}

        _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, FALSE));

        _sampleCount=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
	}
*****************************************
*****************************************
SATD id: 227		Size: 91
    public static SampleResult makeResultFromDelimitedString(String inputLine, SampleSaveConfiguration saveConfig) {
        // Check for a header line
        if (inputLine.startsWith(TIME_STAMP) || inputLine.startsWith(CSV_TIME)){
            return null;
        }
		SampleResult result = null;
		long timeStamp = 0;
		long elapsed = 0;
		StringTokenizer splitter = new StringTokenizer(inputLine, _saveConfig.getDelimiter());
		String text = null;

		try {
			if (saveConfig.printMilliseconds()) {
				text = splitter.nextToken();
				timeStamp = Long.parseLong(text);
			} else if (saveConfig.formatter() != null) {
				text = splitter.nextToken();
				Date stamp = saveConfig.formatter().parse(text);
				timeStamp = stamp.getTime();
			}

			if (saveConfig.saveTime()) {
				text = splitter.nextToken();
				elapsed = Long.parseLong(text);
			}

			result = new SampleResult(timeStamp, elapsed);

			if (saveConfig.saveLabel()) {
				text = splitter.nextToken();
				result.setSampleLabel(text);
			}
			if (saveConfig.saveCode()) {
				text = splitter.nextToken();
				result.setResponseCode(text);
			}

			if (saveConfig.saveMessage()) {
				text = splitter.nextToken();
				result.setResponseMessage(text);
			}

			if (saveConfig.saveThreadName()) {
				text = splitter.nextToken();
				result.setThreadName(text);
			}

			if (saveConfig.saveDataType()) {
				text = splitter.nextToken();
				result.setDataType(text);
			}

			if (saveConfig.saveSuccess()) {
				text = splitter.nextToken();
				result.setSuccessful(Boolean.valueOf(text).booleanValue());
			}

			if (saveConfig.saveAssertionResultsFailureMessage()) {
				text = splitter.nextToken();
                // TODO - should this be restored?
			}
            
            if (saveConfig.saveBytes()) {
                text = splitter.nextToken();
                result.setBytes(Integer.parseInt(text));
            }
        
            if (saveConfig.saveThreadCounts()) {
                text = splitter.nextToken();
                // Not saved, as not part of a result
            }

            if (saveConfig.saveUrl()) {
                text = splitter.nextToken();
                // TODO: should this be restored?
            }
        
            if (saveConfig.saveFileName()) {
                text = splitter.nextToken();
                result.setResultFileName(text);
            }            
            
		} catch (NumberFormatException e) {
			log.warn("Error parsing number " + e);
			throw e;
		} catch (ParseException e) {
			log.warn("Error parsing line " + e);
			throw new RuntimeException(e.toString());
		}
		return result;
	}
*****************************************
*****************************************
SATD id: 228		Size: 7
	public static AssertionResult getAssertionResult(Configuration config) {
		AssertionResult result = new AssertionResult(""); //TODO provide proper name?
		result.setError(config.getAttributeAsBoolean(ERROR, false));
		result.setFailure(config.getAttributeAsBoolean(FAILURE, false));
		result.setFailureMessage(config.getAttribute(FAILURE_MESSAGE, ""));
		return result;
	}
*****************************************
*****************************************
SATD id: 229		Size: 7
    private static AssertionResult getAssertionResult(Configuration config) {
        AssertionResult result = new AssertionResult(""); //TODO provide proper name?
        result.setError(config.getAttributeAsBoolean(ERROR, false));
        result.setFailure(config.getAttributeAsBoolean(FAILURE, false));
        result.setFailureMessage(config.getAttribute(FAILURE_MESSAGE, ""));
        return result;
    }
*****************************************
*****************************************
SATD id: 230		Size: 91
    public static SampleResult makeResultFromDelimitedString(String inputLine, SampleSaveConfiguration saveConfig) {
        // Check for a header line
        if (inputLine.startsWith(TIME_STAMP) || inputLine.startsWith(CSV_TIME)){
            return null;
        }
		SampleResult result = null;
		long timeStamp = 0;
		long elapsed = 0;
		StringTokenizer splitter = new StringTokenizer(inputLine, _saveConfig.getDelimiter());
		String text = null;

		try {
			if (saveConfig.printMilliseconds()) {
				text = splitter.nextToken();
				timeStamp = Long.parseLong(text);
			} else if (saveConfig.formatter() != null) {
				text = splitter.nextToken();
				Date stamp = saveConfig.formatter().parse(text);
				timeStamp = stamp.getTime();
			}

			if (saveConfig.saveTime()) {
				text = splitter.nextToken();
				elapsed = Long.parseLong(text);
			}

			result = new SampleResult(timeStamp, elapsed);

			if (saveConfig.saveLabel()) {
				text = splitter.nextToken();
				result.setSampleLabel(text);
			}
			if (saveConfig.saveCode()) {
				text = splitter.nextToken();
				result.setResponseCode(text);
			}

			if (saveConfig.saveMessage()) {
				text = splitter.nextToken();
				result.setResponseMessage(text);
			}

			if (saveConfig.saveThreadName()) {
				text = splitter.nextToken();
				result.setThreadName(text);
			}

			if (saveConfig.saveDataType()) {
				text = splitter.nextToken();
				result.setDataType(text);
			}

			if (saveConfig.saveSuccess()) {
				text = splitter.nextToken();
				result.setSuccessful(Boolean.valueOf(text).booleanValue());
			}

			if (saveConfig.saveAssertionResultsFailureMessage()) {
				text = splitter.nextToken();
                // TODO - should this be restored?
			}
            
            if (saveConfig.saveBytes()) {
                text = splitter.nextToken();
                result.setBytes(Integer.parseInt(text));
            }
        
            if (saveConfig.saveThreadCounts()) {
                text = splitter.nextToken();
                // Not saved, as not part of a result
            }

            if (saveConfig.saveUrl()) {
                text = splitter.nextToken();
                // TODO: should this be restored?
            }
        
            if (saveConfig.saveFileName()) {
                text = splitter.nextToken();
                result.setResultFileName(text);
            }            
            
		} catch (NumberFormatException e) {
			log.warn("Error parsing number " + e);
			throw e;
		} catch (ParseException e) {
			log.warn("Error parsing line " + e);
			throw new RuntimeException(e.toString());
		}
		return result;
	}
*****************************************
*****************************************
SATD id: 231		Size: 22
    public void saveTIFFWithBatik(String filename, BufferedImage image) {
        File outfile = new File(filename);
        OutputStream fos = createFile(outfile);
        if (fos == null) {
            return;
        }
        TIFFEncodeParam param = new TIFFEncodeParam();
        TIFFImageEncoder encoder = new TIFFImageEncoder(fos, param);
        try {
            encoder.encode(image);
        } catch (IOException e) {
            JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
        // Yuck: TIFFImageEncoder uses Error to report runtime problems
        } catch (Error e) {
            JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
            if (e.getClass() != Error.class){// rethrow other errors
                throw e;
            }
        } finally {
            JOrphanUtils.closeQuietly(fos);
        }
    }
*****************************************
*****************************************
SATD id: 232		Size: 83
    public SampleResult sample(Entry entry) {
        SampleResult results = new SampleResult();
        results.setDataType(SampleResult.TEXT);
        results.setSampleLabel(getName());
        
        String command = getCommand();
        Arguments args = getArguments();
        Arguments environment = getEnvironmentVariables();
        boolean checkReturnCode = getCheckReturnCode();
        int expectedReturnCode = getExpectedReturnCode();
        List<String> cmds = new ArrayList<String>(args.getArgumentCount()+1);
        StringBuilder cmdLine = new StringBuilder((null == command) ? "" : command);
        cmds.add(command);
        for (int i=0;i<args.getArgumentCount();i++) {
            Argument arg = args.getArgument(i);
            cmds.add(arg.getPropertyAsString(Argument.VALUE));
            cmdLine.append(" ");
            cmdLine.append(cmds.get(i+1));
        }

        Map<String,String> env = new HashMap<String, String>();
        for (int i=0;i<environment.getArgumentCount();i++) {
            Argument arg = environment.getArgument(i);
            env.put(arg.getName(), arg.getPropertyAsString(Argument.VALUE));
        }
        
        File directory = null;
        if(StringUtils.isEmpty(getDirectory())) {
            directory = new File(FileServer.getDefaultBase());
            if(log.isDebugEnabled()) {
                log.debug("Using default directory:"+directory.getAbsolutePath());
            }
        } else {
            directory = new File(getDirectory());
            if(log.isDebugEnabled()) {
                log.debug("Using configured directory:"+directory.getAbsolutePath());
            }
        }
        
        if(log.isDebugEnabled()) {
            log.debug("Will run :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                    " with environment:"+env);
        }

        results.setSamplerData("Working Directory:"+directory.getAbsolutePath()+
                "\nEnvironment:"+env+
                "\nExecuting:" + cmdLine.toString());
        
        SystemCommand nativeCommand = new SystemCommand(directory, env, getStdin(), getStdout(), getStderr());
        
        try {
            results.sampleStart();
            int returnCode = nativeCommand.run(cmds);
            results.sampleEnd();
            results.setResponseCode(Integer.toString(returnCode)); // TODO is this the best way to do this?
            if(log.isDebugEnabled()) {
                log.debug("Ran :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                        " with execution environment:"+nativeCommand.getExecutionEnvironment()+ " => " + returnCode);
            }

            if (checkReturnCode && (returnCode != expectedReturnCode)) {
                results.setSuccessful(false);
                results.setResponseMessage("Uexpected return code.  Expected ["+expectedReturnCode+"]. Actual ["+returnCode+"].");
            } else {
                results.setSuccessful(true);
                results.setResponseMessage("OK");
            }
        } catch (IOException ioe) {
            results.sampleEnd();
            results.setSuccessful(false);
            // results.setResponseCode("???"); TODO what code should be set here?
            results.setResponseMessage("Exception occured whilst executing System Call: " + ioe);
        } catch (InterruptedException ie) {
            results.sampleEnd();
            results.setSuccessful(false);
            // results.setResponseCode("???"); TODO what code should be set here?
            results.setResponseMessage("System Sampler Interupted whilst executing System Call: " + ie);
        }

        results.setResponseData(nativeCommand.getOutResult().getBytes()); // default charset is deliberate here
            
        return results;
    }
*****************************************
*****************************************
SATD id: 233		Size: 27
    public void configure(TestElement element)
    {
        super.configure(element);

		for (int i=0; i<editors.length; i++)
		{
			if (editors[i] == null) continue;
			JMeterProperty jprop= element.getProperty(descriptors[i].getName());
			try
			{
				setEditorValue(i, jprop.getObjectValue());
			}
			catch (IllegalArgumentException e)
			{
				// I guess this can happen as a result of a bad
				// file read? In this case, it would be better to replace the
				// incorrect value with anything valid, e.g. the default value
				// for the property.
				// But for the time being, I just prefer to be aware of any
				// problems occuring here, most likely programming errors,
				// so I'll bail out.
				throw new Error("Bad property value.", e);
				// TODO: review this and possibly change to:
				// setEditorValue(i, descriptors[i].getValue("default"); 
			}
		}
    }
*****************************************
*****************************************
SATD id: 234		Size: 29
	protected void initRun() {
		JMeterContextService.incrNumberOfThreads();
		threadGroup.incrNumberOfThreads();
		threadContext = JMeterContextService.getContext();
		threadContext.setVariables(threadVars);
		threadContext.setThreadNum(getThreadNum());
		threadContext.getVariables().put(LAST_SAMPLE_OK, "true");
		threadContext.setThread(this);
		testTree.traverse(compiler);
		// listeners = controller.getListeners();
		if (scheduler) {
			// set the scheduler to start
			startScheduler();
		}
		rampUpDelay();
		log.info("Thread " + Thread.currentThread().getName() + " started");
		/*
		 * Setting SamplingStarted before the contollers are initialised allows
		 * them to access the running values of functions and variables (however
		 * it does not seem to help with the listeners)
		 */
		if (startEarlier)
			threadContext.setSamplingStarted(true);
		controller.initialize();
		controller.addIterationListener(new IterationListener());
		if (!startEarlier)
			threadContext.setSamplingStarted(true);
		threadStarted();
	}
*****************************************
*****************************************
SATD id: 235		Size: 2
    @SuppressWarnings("unchecked") // TODO fix this when there is a real implementation
    public abstract String[][] getTableData(List data);
*****************************************
*****************************************
SATD id: 236		Size: 5
    @SuppressWarnings("unchecked") // TODO fix this when there is a real implementation
    @Override
    public String[][] getTableData(List data) {
        return new String[0][0];
    }
*****************************************
*****************************************
SATD id: 237		Size: 2
    @SuppressWarnings("unchecked") // TODO fix this when there is a real implementation
    String[][] getTableData(List data);
*****************************************
*****************************************
SATD id: 238		Size: 70
    protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings) throws IOException, ScriptException {
        if (bindings == null) {
            bindings = scriptEngine.createBindings();
        }
        populateBindings(bindings);
        File scriptFile = new File(getFilename()); 
        // Hack: bsh-2.0b5.jar BshScriptEngine implements Compilable but throws "java.lang.Error: unimplemented"
        boolean supportsCompilable = scriptEngine instanceof Compilable 
                && !(scriptEngine.getClass().getName().equals("bsh.engine.BshScriptEngine"));
        if (!StringUtils.isEmpty(getFilename())) {
            if (scriptFile.exists() && scriptFile.canRead()) {
                BufferedReader fileReader = null;
                try {
                    if (supportsCompilable) {
                        String cacheKey = 
                                getScriptLanguage()+"#"+
                                scriptFile.getAbsolutePath()+"#"+
                                        scriptFile.lastModified();
                        CompiledScript compiledScript = 
                                compiledScriptsCache.get(cacheKey);
                        if (compiledScript==null) {
                            synchronized (compiledScriptsCache) {
                                compiledScript = 
                                        compiledScriptsCache.get(cacheKey);
                                if (compiledScript==null) {
                                    // TODO Charset ?
                                    fileReader = new BufferedReader(new FileReader(scriptFile), 
                                            (int)scriptFile.length()); 
                                    compiledScript = 
                                            ((Compilable) scriptEngine).compile(fileReader);
                                    compiledScriptsCache.put(cacheKey, compiledScript);
                                }
                            }
                        }
                        return compiledScript.eval(bindings);
                    } else {
                        // TODO Charset ?
                        fileReader = new BufferedReader(new FileReader(scriptFile), 
                                (int)scriptFile.length()); 
                        return scriptEngine.eval(fileReader, bindings);                    
                    }
                } finally {
                    IOUtils.closeQuietly(fileReader);
                }
            }  else {
                throw new ScriptException("Script file '"+scriptFile.getAbsolutePath()+"' does not exist or is unreadable for element:"+getName());
            }
        } else if (!StringUtils.isEmpty(getScript())){
            if (supportsCompilable && !StringUtils.isEmpty(cacheKey)) {
                CompiledScript compiledScript = 
                        compiledScriptsCache.get(cacheKey);
                if (compiledScript==null) {
                    synchronized (compiledScriptsCache) {
                        compiledScript = 
                                compiledScriptsCache.get(cacheKey);
                        if (compiledScript==null) {
                            compiledScript = 
                                    ((Compilable) scriptEngine).compile(getScript());
                            compiledScriptsCache.put(cacheKey, compiledScript);
                        }
                    }
                }
                return compiledScript.eval(bindings);
            } else {
                return scriptEngine.eval(getScript(), bindings);
            }
        } else {
            throw new ScriptException("Both script file and script text are empty for element:"+getName());            
        }
    }
*****************************************
*****************************************
SATD id: 239		Size: 116
   private static final boolean _time,  _timestamp, _success,
   _label, _code, _message, _threadName, _xml, _responseData,
   _dataType, _encoding, _assertions, _latency,
   _subresults,  _samplerData, _fieldNames, _responseHeaders, _requestHeaders;
   
   private static final boolean _saveAssertionResultsFailureMessage;
   private static final String _timeStampFormat;
   private static int _assertionsResultsToSave;
   // TODO turn into method?
   public static final int SAVE_NO_ASSERTIONS = 0;
   public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;
   public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;
   private static final boolean _printMilliseconds;
   private static final SimpleDateFormat _formatter;
   
   /** The string used to separate fields when stored to disk, for example,
   the comma for CSV files. */
   private static final String _delimiter;
   private static final String DEFAULT_DELIMITER = ",";

   /**
    * Read in the properties having to do with saving from a properties file.
    */
   static
   {
	   // TODO - get from properties?
	   _subresults = _encoding = _assertions= _latency = _samplerData = true;
	   _responseHeaders = _requestHeaders = true;
	   
	   
		Properties props = JMeterUtils.getJMeterProperties();

		_delimiter=props.getProperty(DEFAULT_DELIMITER_PROP,DEFAULT_DELIMITER);
		
       _fieldNames =
           TRUE.equalsIgnoreCase(
               props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));

       _dataType =
           TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));

       _label =
           TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));

       _code = // TODO is this correct?
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));

       _responseData =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));

       _message =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));

       _success =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));

       _threadName =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));

       _time =
           TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));

       _timeStampFormat =
           props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);

       _printMilliseconds = MILLISECONDS.equalsIgnoreCase(_timeStampFormat);

       // Prepare for a pretty date
       if (!_printMilliseconds
           && !NONE.equalsIgnoreCase(_timeStampFormat)
           && (_timeStampFormat != null))
       {
           _formatter = new SimpleDateFormat(_timeStampFormat);
       } else {
		   _formatter = null;
       }

	   _timestamp = !_timeStampFormat.equalsIgnoreCase(NONE);
	   
       _saveAssertionResultsFailureMessage =
           TRUE.equalsIgnoreCase(
               props.getProperty(
                   ASSERTION_RESULTS_FAILURE_MESSAGE_PROP,
                   FALSE));

       String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
       if (NONE.equals(whichAssertionResults))
       {
           _assertionsResultsToSave = SAVE_NO_ASSERTIONS;
       }
       else if (FIRST.equals(whichAssertionResults))
       {
           _assertionsResultsToSave = SAVE_FIRST_ASSERTION;
       }
       else if (ALL.equals(whichAssertionResults))
       {
           _assertionsResultsToSave = SAVE_ALL_ASSERTIONS;
       }

       String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, XML);

       if (XML.equals(howToSave))
       {
           _xml=true;
       }
       else
       {
           _xml=false;
       }

   }
*****************************************
*****************************************
SATD id: 240		Size: 38
    private void getValuesForXPath(Document d,String query, JMeterVariables vars, String refName)
     throws TransformerException
    {
        String val = null;
     	XObject xObject = XPathAPI.eval(d, query);
        if (xObject.getType() == XObject.CLASS_NODESET) {
	        NodeList matches = xObject.nodelist();
			int length = matches.getLength();
	        vars.put(concat(refName,MATCH_NR), String.valueOf(length));
	        for (int i = 0 ; i < length; i++) {
	            Node match = matches.item(i);
				if ( match instanceof Element){
				// elements have empty nodeValue, but we are usually interested in their content
				   final Node firstChild = match.getFirstChild();
				   if (firstChild != null) {
					   val = firstChild.getNodeValue();
				   } else {
					   val = match.getNodeValue(); // TODO is this correct?
				   }
				} else {				
				   val = match.getNodeValue();
				}
	            if ( val!=null){
	                if (i==0) {// Treat 1st match specially
	                    vars.put(refName,val);                    
	                }
	                vars.put(concat(refName,String.valueOf(i+1)),val);
	            }
			}
	        vars.remove(concat(refName,String.valueOf(length+1)));
     	} else {
	        val = xObject.toString();
	        vars.put(concat(refName, MATCH_NR), "1");
	        vars.put(refName, val);
	        vars.put(concat(refName, "1"), val);
	        vars.remove(concat(refName, "2"));
	    }
    }
*****************************************
*****************************************
SATD id: 241		Size: 47
		private static void filetest(HTMLParser p, String file, String url, String resultFile, Collection c,
				boolean orderMatters) // Does the order matter?
				throws Exception {
			String parserName = p.getClass().getName().substring("org.apache.jmeter.protocol.http.parser".length());
			log.debug("file   " + file);
			File f = findTestFile(file);
			byte[] buffer = new byte[(int) f.length()];
			int len = new FileInputStream(f).read(buffer);
			assertEquals(len, buffer.length);
			Iterator result;
			if (c == null) {
				result = p.getEmbeddedResourceURLs(buffer, new URL(url));
			} else {
				result = p.getEmbeddedResourceURLs(buffer, new URL(url), c);
			}
			/*
			 * TODO: Exact ordering is only required for some tests; change the
			 * comparison to do a set compare where necessary.
			 */
			Iterator expected;
			if (orderMatters) {
				expected = getFile(resultFile).iterator();
			} else {
				// Convert both to Sets
				expected = new TreeSet(getFile(resultFile)).iterator();
				TreeSet temp = new TreeSet(new Comparator() {
					public int compare(Object o1, Object o2) {
						return (o1.toString().compareTo(o2.toString()));
					}
				});
				while (result.hasNext()) {
					temp.add(result.next());
				}
				result = temp.iterator();
			}

			while (expected.hasNext()) {
				Object next = expected.next();
				assertTrue(parserName + "::Expecting another result " + next, result.hasNext());
				try {
					assertEquals(parserName + "(" + file + ")", next, ((URL) result.next()).toString());
				} catch (ClassCastException e) {
					fail(parserName + "::Expected URL, but got " + e.toString());
				}
			}
			assertFalse(parserName + "::Should have reached the end of the results", result.hasNext());
		}
*****************************************
*****************************************
SATD id: 242		Size: 48
        private static void filetest(HTMLParser p, String file, String url, String resultFile, Collection c,
                boolean orderMatters) // Does the order matter?
                throws Exception {
            String parserName = p.getClass().getName().substring("org.apache.jmeter.protocol.http.parser.".length());
            String fname = file.substring(file.indexOf("/")+1);
            log.debug("file   " + file);
            File f = findTestFile(file);
            byte[] buffer = new byte[(int) f.length()];
            int len = new FileInputStream(f).read(buffer);
            assertEquals(len, buffer.length);
            Iterator result;
            if (c == null) {
                result = p.getEmbeddedResourceURLs(buffer, new URL(url));
            } else {
                result = p.getEmbeddedResourceURLs(buffer, new URL(url), c);
            }
            /*
             * TODO: Exact ordering is only required for some tests; change the
             * comparison to do a set compare where necessary.
             */
            Iterator expected;
            if (orderMatters) {
                expected = getFile(resultFile).iterator();
            } else {
                // Convert both to Sets
                expected = new TreeSet(getFile(resultFile)).iterator();
                TreeSet temp = new TreeSet(new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return (o1.toString().compareTo(o2.toString()));
                    }
                });
                while (result.hasNext()) {
                    temp.add(result.next());
                }
                result = temp.iterator();
            }

            while (expected.hasNext()) {
                Object next = expected.next();
                assertTrue(fname+"::"+parserName + "::Expecting another result " + next, result.hasNext());
                try {
                    assertEquals(fname+"::"+parserName + "(next)", next, ((URL) result.next()).toString());
                } catch (ClassCastException e) {
                    fail(fname+"::"+parserName + "::Expected URL, but got " + e.toString());
                }
            }
            assertFalse(fname+"::"+parserName + "::Should have reached the end of the results", result.hasNext());
        }
*****************************************
*****************************************
SATD id: 243		Size: 2
    // TODO field always true, what for ?
    private final boolean drawYgrid = true;
*****************************************
*****************************************
SATD id: 244		Size: 5
	// not currently used
    public MultipartUrlConfigGui(boolean value) {
        super(value);
        init();
    }
*****************************************
*****************************************
SATD id: 245		Size: 127
    protected static SampleResult parseForImages(SampleResult res, HTTPSampler sampler)
    {
        URL baseUrl;

        String displayName = res.getSampleLabel();

        try
        {
            baseUrl = sampler.getUrl();
            if(log.isDebugEnabled())
            {
                log.debug("baseUrl - " + baseUrl.toString());
            }
        }
        catch(MalformedURLException mfue)
        {
            log.error("Error creating URL '" + displayName + "'");
            log.error("MalformedURLException - " + mfue);
            res.setResponseData(mfue.toString().getBytes());
            res.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
            res.setResponseMessage(HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
            res.setSuccessful(false);
            return res;
        }
        
        // This is used to ignore duplicated binary files.
        // Using a LinkedHashSet to avoid unnecessary overhead in iterating
        // the elements in the set later on. As a side-effect, this will keep
        // them roughly in order, which should be a better model of browser
        // behaviour.
        Set uniqueRLs = new LinkedHashSet();
        
        // Look for unique RLs to be sampled.
        Perl5Matcher matcher = (Perl5Matcher) localMatcher.get();
        PatternMatcherInput input = (PatternMatcherInput) localInput.get();
        // TODO: find a way to avoid the cost of creating a String here --
        // probably a new PatternMatcherInput working on a byte[] would do
        // better.
        input.setInput(new String(res.getResponseData()));
        while (matcher.contains(input, pattern)) {
            MatchResult match= matcher.getMatch();
            String s;
            if (log.isDebugEnabled()) log.debug("match groups "+match.groups());
            // Check for a BASE HREF:
            s= match.group(1);
            if (s!=null) {
                try {
                    baseUrl= new URL(baseUrl, s);
                    log.debug("new baseUrl from - "+s+" - " + baseUrl.toString());
                }
                catch(MalformedURLException mfue)
                {
                    log.error("Error creating base URL from BASE HREF '" + displayName + "'");
                    log.error("MalformedURLException - " + mfue);
                    res.setResponseData(mfue.toString().getBytes());
                    res.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
                    res.setResponseMessage(HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
                    res.setSuccessful(false);
                    return res;
                }
            }
            for (int g= 2; g < match.groups(); g++) {
                s= match.group(g);
                if (log.isDebugEnabled()) log.debug("group "+g+" - "+match.group(g));
                if (s!=null) uniqueRLs.add(s);
            }
        }

        // Iterate through the RLs and download each image:
        Iterator rls= uniqueRLs.iterator();
        while (rls.hasNext()) {
            String binUrlStr= (String)rls.next();
            SampleResult binRes = new SampleResult();
            
            // set the baseUrl and binUrl so that if error occurs
            // due to MalformedException then at least the values will be
            // visible to the user to aid correction
            binRes.setSampleLabel(baseUrl + "," + binUrlStr);

            URL binUrl;
            try {
                binUrl= new URL(baseUrl, binUrlStr);
            }
            catch(MalformedURLException mfue)
            {
                log.error("Error creating URL '" + baseUrl +
                          " , " + binUrlStr + "'");
                log.error("MalformedURLException - " + mfue);
                binRes.setResponseData(mfue.toString().getBytes());
                binRes.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
                binRes.setResponseMessage(
                    HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
                binRes.setSuccessful(false);
                res.addSubResult(binRes);
                break;
            }
            if(log.isDebugEnabled())
            {
                log.debug("Binary url - " + binUrlStr);
                log.debug("Full Binary url - " + binUrl);
            }
            binRes.setSampleLabel(binUrl.toString());
            try
            {
                HTTPSamplerFull.loadBinary(binUrl, binRes, sampler);
            }
            catch(Exception ioe)
            {
                log.error("Error reading from URL - " + ioe);
                binRes.setResponseData(ioe.toString().getBytes());
                binRes.setResponseCode(HTTPSampler.NON_HTTP_RESPONSE_CODE);
                binRes.setResponseMessage(
                    HTTPSampler.NON_HTTP_RESPONSE_MESSAGE);
                binRes.setSuccessful(false);
            }
            log.debug("Adding result");
            res.addSubResult(binRes);
            res.setTime(res.getTime() + binRes.getTime());
        }

        // Okay, we're all done now
        if(log.isDebugEnabled())
        {
            log.debug("Total time - " + res.getTime());
        }
        return res;
    }
*****************************************
*****************************************
SATD id: 246		Size: 18
    private static final String PROXY_PASS =
        JMeterUtils.getPropDefault(JMeter.HTTP_PROXY_PASS,""); // $NON-NLS-1$

    private static final String ENCODING = "UTF-8"; // $NON-NLS-1$ TODO should this be a variable?

    /*
     * Random class for generating random numbers.
     */
    private final Random RANDOM = new Random();

    private String fileContents = null;

    /**
     * Set the path where XML messages are stored for random selection.
     */
    public void setXmlPathLoc(String path) {
        setProperty(XML_PATH_LOC, path);
    }
*****************************************
*****************************************
SATD id: 247		Size: 117
    protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {

        String urlStr = url.toString();

        log.debug("Start : sample" + urlStr);
        log.debug("method" + method);

        PostMethod httpMethod;
        httpMethod = new PostMethod(urlStr);

        HTTPSampleResult res = new HTTPSampleResult();
        res.setMonitor(isMonitor());

        res.setSampleLabel(urlStr); // May be replaced later
        res.setHTTPMethod(method);
        res.sampleStart(); // Count the retries as well in the time
        HttpClient client = null;
        InputStream instream = null;
        try {
            setPostHeaders(httpMethod);
            client = setupConnection(url, httpMethod, res);

            res.setQueryString(getQueryString());
            sendPostData(httpMethod);

            int statusCode = client.executeMethod(httpMethod);

            // Request sent. Now get the response:
            instream = httpMethod.getResponseBodyAsStream();

            if (instream != null) {// will be null for HEAD

                org.apache.commons.httpclient.Header responseHeader = httpMethod.getResponseHeader(TRANSFER_ENCODING);
                if (responseHeader != null && ENCODING_GZIP.equals(responseHeader.getValue())) {
                    instream = new GZIPInputStream(instream);
                }

                //int contentLength = httpMethod.getResponseContentLength();Not visible ...
                //TODO size ouststream according to actual content length
                ByteArrayOutputStream outstream = new ByteArrayOutputStream(4 * 1024);
                //contentLength > 0 ? contentLength : DEFAULT_INITIAL_BUFFER_SIZE);
                byte[] buffer = new byte[4096];
                int len;
                boolean first = true;// first response
                while ((len = instream.read(buffer)) > 0) {
                    if (first) { // save the latency
                        res.latencyEnd();
                        first = false;
                    }
                    outstream.write(buffer, 0, len);
                }

                res.setResponseData(outstream.toByteArray());
                outstream.close();

            }

            res.sampleEnd();
            // Done with the sampling proper.

            // Now collect the results into the HTTPSampleResult:

            res.setSampleLabel(httpMethod.getURI().toString());
            // Pick up Actual path (after redirects)

            res.setResponseCode(Integer.toString(statusCode));
            res.setSuccessful(isSuccessCode(statusCode));

            res.setResponseMessage(httpMethod.getStatusText());

            String ct = null;
            org.apache.commons.httpclient.Header h
                    = httpMethod.getResponseHeader(HEADER_CONTENT_TYPE);
            if (h != null)// Can be missing, e.g. on redirect
            {
                ct = h.getValue();
                res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                res.setEncodingAndType(ct);
            }

            res.setResponseHeaders(getResponseHeaders(httpMethod));
            if (res.isRedirect()) {
                res.setRedirectLocation(httpMethod.getResponseHeader(HEADER_LOCATION).getValue());
            }

            // If we redirected automatically, the URL may have changed
            if (getAutoRedirects()) {
                res.setURL(new URL(httpMethod.getURI().toString()));
            }

            // Store any cookies received in the cookie manager:
            saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());

            // Follow redirects and download page resources if appropriate:
            res = resultProcessing(areFollowingRedirect, frameDepth, res);

            log.debug("End : sample");
            if (httpMethod != null)
                httpMethod.releaseConnection();
            return res;
        } catch (IllegalArgumentException e)// e.g. some kinds of invalid URL
        {
            res.sampleEnd();
            HTTPSampleResult err = errorResult(e, res);
            err.setSampleLabel("Error: " + url.toString());
            return err;
        } catch (IOException e) {
            res.sampleEnd();
            HTTPSampleResult err = errorResult(e, res);
            err.setSampleLabel("Error: " + url.toString());
            return err;
        } finally {
            JOrphanUtils.closeQuietly(instream);
            if (httpMethod != null)
                httpMethod.releaseConnection();
        }
    }
*****************************************
*****************************************
SATD id: 248		Size: 5
    // Does not appear to be used
	public void initialize() {
		variables.clear();
        preloadVariables();
	}
*****************************************
*****************************************
SATD id: 249		Size: 16
// Does not appear to be used
    public void configureSampler(HTTPSampler sampler)
//    {
//        sampler.setArguments((Arguments) argsPanel.createTestElement());
//        sampler.setDomain(domain.getText());
//        sampler.setProtocol(protocol.getText());
//        sampler.setPath(path.getText());
//        sampler.setFollowRedirects(followRedirects.isSelected());
//        sampler.setDelegateRedirects(autoRedirects.isSelected());
//        sampler.setUseKeepAlive(useKeepAlive.isSelected());
//        if (port.getText().length() > 0)
//        {
//            sampler.setPort(Integer.parseInt(port.getText()));
//        }
//        sampler.setMethod((post.isSelected() ? "POST" : "GET"));
//    }
*****************************************
*****************************************
SATD id: 250		Size: 5
// Does not appear to be used.
// Remove as replaceAll() requires 1.4
//	public static String insertSpaceBreaks(String v, String insertion) {
//		return v.trim().replaceAll("\\s+", insertion);
//	}
*****************************************
*****************************************
SATD id: 251		Size: 31
	public void startGui(CLOption testFile) {

		PluginManager.install(this, true);
		JMeterTreeModel treeModel = new JMeterTreeModel();
		JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
		treeLis.setActionHandler(ActionRouter.getInstance());
		// NOTUSED: GuiPackage guiPack =
		GuiPackage.getInstance(treeLis, treeModel);
		org.apache.jmeter.gui.MainFrame main = new org.apache.jmeter.gui.MainFrame(ActionRouter.getInstance(),
				treeModel, treeLis);
		main.setTitle("Apache JMeter");
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
*****************************************
*****************************************
SATD id: 252		Size: 31
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
*****************************************
*****************************************
SATD id: 253		Size: 4
    public JMeterTreeNode(){// Allow serializable test to work
    		// TODO: is the serializable test necessary now that JMeterTreeNode is no longer a GUI component?
    	this(null,null);
    }
*****************************************
*****************************************
SATD id: 254		Size: 27
	public String setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager) {
		StringBuffer hdrs = new StringBuffer(100);
		if (headerManager != null) {
			CollectionProperty headers = headerManager.getHeaders();
			if (headers != null) {
				PropertyIterator i = headers.iterator();
				while (i.hasNext()) {
					org.apache.jmeter.protocol.http.control.Header header 
                    = (org.apache.jmeter.protocol.http.control.Header) 
                       i.next().getObjectValue();
					String n = header.getName();
					// Don't allow override of Content-Length
					// This helps with SoapSampler hack too
					// TODO - what other headers are not allowed?
					if (! HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
						String v = header.getValue();
						method.addRequestHeader(n, v);
						hdrs.append(n);
						hdrs.append(": "); // $NON-NLS-1$
						hdrs.append(v);
						hdrs.append("\n"); // $NON-NLS-1$
					}
				}
			}
		}
		return hdrs.toString();
	}
*****************************************
*****************************************
SATD id: 255		Size: 25
    private void setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager, CacheManager cacheManager) {
        // Set all the headers from the HeaderManager
        if (headerManager != null) {
            CollectionProperty headers = headerManager.getHeaders();
            if (headers != null) {
                PropertyIterator i = headers.iterator();
                while (i.hasNext()) {
                    org.apache.jmeter.protocol.http.control.Header header
                    = (org.apache.jmeter.protocol.http.control.Header)
                       i.next().getObjectValue();
                    String n = header.getName();
                    // Don't allow override of Content-Length
                    // This helps with SoapSampler hack too
                    // TODO - what other headers are not allowed?
                    if (! HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                        String v = header.getValue();
                        method.addRequestHeader(n, v);
                    }
                }
            }
        }
        if (cacheManager != null){
            cacheManager.setHeaders(u, method);
        }
    }
*****************************************
*****************************************
SATD id: 256		Size: 1
    private static final String ENCODING = "utf-8"; //$NON-NLS-1$ TODO should this be variable?
*****************************************
*****************************************
SATD id: 257		Size: 226
    private String sendPostData(PostMethod post) throws IOException {
        // Buffer to hold the post body, except file content
        StringBuilder postedBody = new StringBuilder(1000);
        HTTPFileArg files[] = getHTTPFiles();
        // Check if we should do a multipart/form-data or an
        // application/x-www-form-urlencoded post request
        if(getUseMultipartForPost()) {
            // If a content encoding is specified, we use that as the
            // encoding of any parameter values
            String contentEncoding = getContentEncoding();
            if(contentEncoding != null && contentEncoding.length() == 0) {
                contentEncoding = null;
            }

            // We don't know how many entries will be skipped
            ArrayList<PartBase> partlist = new ArrayList<PartBase>();
            // Create the parts
            // Add any parameters
            PropertyIterator args = getArguments().iterator();
            while (args.hasNext()) {
               HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
               String parameterName = arg.getName();
               if (arg.isSkippable(parameterName)){
                   continue;
               }
               partlist.add(new StringPart(arg.getName(), arg.getValue(), contentEncoding));
            }

            // Add any files
            for (int i=0; i < files.length; i++) {
                HTTPFileArg file = files[i];
                File inputFile = new File(file.getPath());
                // We do not know the char set of the file to be uploaded, so we set it to null
                ViewableFilePart filePart = new ViewableFilePart(file.getParamName(), inputFile, file.getMimeType(), null);
                filePart.setCharSet(null); // We do not know what the char set of the file is
                partlist.add(filePart);
            }

            // Set the multipart for the post
            int partNo = partlist.size();
            Part[] parts = partlist.toArray(new Part[partNo]);
            MultipartRequestEntity multiPart = new MultipartRequestEntity(parts, post.getParams());
            post.setRequestEntity(multiPart);

            // Set the content type
            String multiPartContentType = multiPart.getContentType();
            post.setRequestHeader(HEADER_CONTENT_TYPE, multiPartContentType);

            // If the Multipart is repeatable, we can send it first to
            // our own stream, without the actual file content, so we can return it
            if(multiPart.isRepeatable()) {
                // For all the file multiparts, we must tell it to not include
                // the actual file content
                for(int i = 0; i < partNo; i++) {
                    if(parts[i] instanceof ViewableFilePart) {
                        ((ViewableFilePart) parts[i]).setHideFileData(true); // .sendMultipartWithoutFileContent(bos);
                    }
                }
                // Write the request to our own stream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                multiPart.writeRequest(bos);
                bos.flush();
                // We get the posted bytes using the encoding used to create it
                postedBody.append(new String(bos.toByteArray(),
                        contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                        : contentEncoding));
                bos.close();

                // For all the file multiparts, we must revert the hiding of
                // the actual file content
                for(int i = 0; i < partNo; i++) {
                    if(parts[i] instanceof ViewableFilePart) {
                        ((ViewableFilePart) parts[i]).setHideFileData(false);
                    }
                }
            }
            else {
                postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
            }
        }
        else {
            // Check if the header manager had a content type header
            // This allows the user to specify his own content-type for a POST request
            Header contentTypeHeader = post.getRequestHeader(HEADER_CONTENT_TYPE);
            boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
            // If there are no arguments, we can send a file as the body of the request
            // TODO: needs a multiple file upload scenerio
            if(!hasArguments() && getSendFileAsPostBody()) {
                // If getSendFileAsPostBody returned true, it's sure that file is not null
                HTTPFileArg file = files[0];
                if(!hasContentTypeHeader) {
                    // Allow the mimetype of the file to control the content type
                    if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                    }
                    else {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                    }
                }

                FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(file.getPath()),null);
                post.setRequestEntity(fileRequestEntity);

                // We just add placeholder text for file content
                postedBody.append("<actual file content, not shown here>");
            }
            else {
                // In a post request which is not multipart, we only support
                // parameters, no file upload is allowed

                // If a content encoding is specified, we set it as http parameter, so that
                // the post body will be encoded in the specified content encoding
                String contentEncoding = getContentEncoding();
                boolean haveContentEncoding = false;
                if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                    post.getParams().setContentCharset(contentEncoding);
                    haveContentEncoding = true;
                } else if (contentEncoding != null && contentEncoding.trim().length() == 0){
                    contentEncoding=null;
                }

                // If none of the arguments have a name specified, we
                // just send all the values as the post body
                if(getSendParameterValuesAsPostBody()) {
                    // Allow the mimetype of the file to control the content type
                    // This is not obvious in GUI if you are not uploading any files,
                    // but just sending the content of nameless parameters
                    // TODO: needs a multiple file upload scenerio
                    if(!hasContentTypeHeader) {
                        HTTPFileArg file = files.length > 0? files[0] : null;
                        if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                            post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                        }
                        else {
                             // TODO - is this the correct default?
                            post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                        }
                    }

                    // Just append all the parameter values, and use that as the post body
                    StringBuilder postBody = new StringBuilder();
                    PropertyIterator args = getArguments().iterator();
                    while (args.hasNext()) {
                        HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                        String value;
                        if (haveContentEncoding){
                            value = arg.getEncodedValue(contentEncoding);
                        } else {
                            value = arg.getEncodedValue();
                        }
                        postBody.append(value);
                    }
                    StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                    post.setRequestEntity(requestEntity);
                }
                else {
                    // It is a normal post request, with parameter names and values

                    // Set the content type
                    if(!hasContentTypeHeader) {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                    }
                    // Add the parameters
                    PropertyIterator args = getArguments().iterator();
                    while (args.hasNext()) {
                        HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                        // The HTTPClient always urlencodes both name and value,
                        // so if the argument is already encoded, we have to decode
                        // it before adding it to the post request
                        String parameterName = arg.getName();
                        if (arg.isSkippable(parameterName)){
                            continue;
                        }
                        String parameterValue = arg.getValue();
                        if(!arg.isAlwaysEncoded()) {
                            // The value is already encoded by the user
                            // Must decode the value now, so that when the
                            // httpclient encodes it, we end up with the same value
                            // as the user had entered.
                            String urlContentEncoding = contentEncoding;
                            if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
                                // Use the default encoding for urls
                                urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                            }
                            parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                            parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                        }
                        // Add the parameter, httpclient will urlencode it
                        post.addParameter(parameterName, parameterValue);
                    }

/*
//                    // Alternative implementation, to make sure that HTTPSampler and HTTPSampler2
//                    // sends the same post body.
//
//                    // Only include the content char set in the content-type header if it is not
//                    // an APPLICATION_X_WWW_FORM_URLENCODED content type
//                    String contentCharSet = null;
//                    if(!post.getRequestHeader(HEADER_CONTENT_TYPE).getValue().equals(APPLICATION_X_WWW_FORM_URLENCODED)) {
//                        contentCharSet = post.getRequestCharSet();
//                    }
//                    StringRequestEntity requestEntity = new StringRequestEntity(getQueryString(contentEncoding), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentCharSet);
//                    post.setRequestEntity(requestEntity);
*/
                }

                // If the request entity is repeatable, we can send it first to
                // our own stream, so we can return it
                if(post.getRequestEntity().isRepeatable()) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    post.getRequestEntity().writeRequest(bos);
                    bos.flush();
                    // We get the posted bytes using the encoding used to create it
                    postedBody.append(new String(bos.toByteArray(),post.getRequestCharSet()));
                    bos.close();
                }
                else {
                    postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                }
            }
        }
        // Set the content length
        post.setRequestHeader(HEADER_CONTENT_LENGTH, Long.toString(post.getRequestEntity().getContentLength()));

        return postedBody.toString();
    }
*****************************************
*****************************************
SATD id: 258		Size: 222
	private String sendPostData(PostMethod post) throws IOException {
        // Buffer to hold the post body, expect file content
        StringBuffer postedBody = new StringBuffer(1000);
        
        // Check if we should do a multipart/form-data or an
        // application/x-www-form-urlencoded post request
        if(getUseMultipartForPost()) {
            // If a content encoding is specified, we use that es the
            // encoding of any parameter values
            String contentEncoding = getContentEncoding();
            if(contentEncoding != null && contentEncoding.length() == 0) {
                contentEncoding = null;
            }
            
            // Check how many parts we need, one for each parameter and file
            int noParts = getArguments().getArgumentCount();
            if(hasUploadableFiles())
            {
                noParts++;
            }

            // Create the parts
            Part[] parts = new Part[noParts];
            int partNo = 0;
            // Add any parameters
            PropertyIterator args = getArguments().iterator();
            while (args.hasNext()) {
               HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
               String parameterName = arg.getName();
               if (parameterName.length()==0){
                   continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
               }
               parts[partNo++] = new StringPart(arg.getName(), arg.getValue(), contentEncoding);
            }
            
            // Add any files
            if(hasUploadableFiles()) {
                File inputFile = new File(getFilename());
                // We do not know the char set of the file to be uploaded, so we set it to null
                ViewableFilePart filePart = new ViewableFilePart(getFileField(), inputFile, getMimetype(), null);
                filePart.setCharSet(null); // We do not know what the char set of the file is
                parts[partNo++] = filePart;
            }
            
            // Set the multipart for the post
            MultipartRequestEntity multiPart = new MultipartRequestEntity(parts, post.getParams());
            post.setRequestEntity(multiPart);

            // Set the content type
            String multiPartContentType = multiPart.getContentType();
            post.setRequestHeader(HEADER_CONTENT_TYPE, multiPartContentType);

            // If the Multipart is repeatable, we can send it first to
            // our own stream, without the actual file content, so we can return it
            if(multiPart.isRepeatable()) {
            	// For all the file multiparts, we must tell it to not include
            	// the actual file content
                for(int i = 0; i < partNo; i++) {
                	if(parts[i] instanceof ViewableFilePart) {
                		((ViewableFilePart) parts[i]).setHideFileData(true); // .sendMultipartWithoutFileContent(bos);
                    }
                }
                // Write the request to our own stream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                multiPart.writeRequest(bos);
                bos.flush();
                // We get the posted bytes as UTF-8, since java is using UTF-8
                postedBody.append(new String(bos.toByteArray() , "UTF-8")); // $NON-NLS-1$
                bos.close();

            	// For all the file multiparts, we must revert the hiding of
            	// the actual file content
                for(int i = 0; i < partNo; i++) {
                	if(parts[i] instanceof ViewableFilePart) {
                		((ViewableFilePart) parts[i]).setHideFileData(false);
                    }
                }
            }
            else {
                postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
            }
        }
        else {
            // Check if the header manager had a content type header
            // This allows the user to specify his own content-type for a POST request
            Header contentTypeHeader = post.getRequestHeader(HEADER_CONTENT_TYPE);
            boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0; 

            // If there are no arguments, we can send a file as the body of the request
            if(!hasArguments() && getSendFileAsPostBody()) {
                if(!hasContentTypeHeader) {
                    // Allow the mimetype of the file to control the content type
                    if(getMimetype() != null && getMimetype().length() > 0) {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, getMimetype());
                    }
                    else {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                    }
                }
                
                FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(getFilename()),null); 
                post.setRequestEntity(fileRequestEntity);
                
                // We just add placeholder text for file content
                postedBody.append("<actual file content, not shown here>");
            }
            else {
                // In a post request which is not multipart, we only support
                // parameters, no file upload is allowed

                // If a content encoding is specified, we set it as http parameter, so that
                // the post body will be encoded in the specified content encoding
                final String contentEncoding = getContentEncoding();
                boolean haveContentEncoding = false;
                if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                    post.getParams().setContentCharset(contentEncoding);
                    haveContentEncoding = true;
                }
                
                // If none of the arguments have a name specified, we
                // just send all the values as the post body
                if(getSendParameterValuesAsPostBody()) {
                    // Allow the mimetype of the file to control the content type
                    // This is not obvious in GUI if you are not uploading any files,
                    // but just sending the content of nameless parameters
                    if(!hasContentTypeHeader) {
                        if(getMimetype() != null && getMimetype().length() > 0) {
                            post.setRequestHeader(HEADER_CONTENT_TYPE, getMimetype());
                        }
                        else {
                        	 // TODO - is this the correct default?
                            post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                        }
                    }
                    
                    // Just append all the non-empty parameter values, and use that as the post body
                    StringBuffer postBody = new StringBuffer();
                    PropertyIterator args = getArguments().iterator();
                    while (args.hasNext()) {
                        HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                        String value;
                        if (haveContentEncoding){
                        	value = arg.getEncodedValue(contentEncoding);
                        } else {
                        	value = arg.getEncodedValue();
                        }
						postBody.append(value);
                    }
                    StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), post.getRequestCharSet());
                    post.setRequestEntity(requestEntity);
                }
                else {
                    // It is a normal post request, with parameter names and values
                    
                    // Set the content type
                    if(!hasContentTypeHeader) {
                        post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                    }
                    // Add the parameters
                    PropertyIterator args = getArguments().iterator();
                    while (args.hasNext()) {
                        HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                        // The HTTPClient always urlencodes both name and value,
                        // so if the argument is already encoded, we have to decode
                        // it before adding it to the post request
                        String parameterName = arg.getName();
                        if (parameterName.length()==0){
                            continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
                        }
                        String parameterValue = arg.getValue();
                        if(!arg.isAlwaysEncoded()) {
                            // The value is already encoded by the user
                            // Must decode the value now, so that when the
                            // httpclient encodes it, we end up with the same value
                            // as the user had entered.
                            String urlContentEncoding = contentEncoding;
                            if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
                                // Use the default encoding for urls 
                                urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                            }
                            parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                            parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                        }
                        // Add the parameter, httpclient will urlencode it
                        post.addParameter(parameterName, parameterValue);
                    }
                    
/*
//                    // Alternative implementation, to make sure that HTTPSampler and HTTPSampler2
//                    // sends the same post body.
//                     
//                    // Only include the content char set in the content-type header if it is not
//                    // an APPLICATION_X_WWW_FORM_URLENCODED content type
//                    String contentCharSet = null;
//                    if(!post.getRequestHeader(HEADER_CONTENT_TYPE).getValue().equals(APPLICATION_X_WWW_FORM_URLENCODED)) {
//                        contentCharSet = post.getRequestCharSet();
//                    }
//                    StringRequestEntity requestEntity = new StringRequestEntity(getQueryString(contentEncoding), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentCharSet);
//                    post.setRequestEntity(requestEntity);
*/                    
                }
                
                // If the request entity is repeatable, we can send it first to
                // our own stream, so we can return it
                if(post.getRequestEntity().isRepeatable()) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    post.getRequestEntity().writeRequest(bos);
                    bos.flush();
                    // We get the posted bytes as UTF-8, since java is using UTF-8
                    postedBody.append(new String(bos.toByteArray() , "UTF-8")); // $NON-NLS-1$
                    bos.close();
                }
                else {
                    postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                }
            }
        }
        // Set the content length
        post.setRequestHeader(HEADER_CONTENT_LENGTH, Long.toString(post.getRequestEntity().getContentLength()));
        
        return postedBody.toString();
	}
*****************************************
*****************************************
SATD id: 259		Size: 70
	public static void main(String[] args) {
		if (args.length == 3) {
			int parser = 0;
			String file = null;
			int loops = 1000;
			if (args[0] != null) {
				if (!args[0].equals("jaxb")) {
					parser = 1;
				}
			}
			if (args[1] != null) {
				file = args[1];
			}
			if (args[2] != null) {
				loops = Integer.parseInt(args[2]);
			}

			java.io.File infile = new java.io.File(file);
			java.io.FileInputStream fis = null;
			java.io.InputStreamReader isr = null;
			StringBuffer buf = new StringBuffer();
			try {
				fis = new java.io.FileInputStream(infile);
				isr = new java.io.InputStreamReader(fis);
				java.io.BufferedReader br = new java.io.BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					buf.append(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			long start = 0;
			long end = 0;
			String contents = buf.toString().trim();
			System.out.println("start test: " + loops + " iterations");
			System.out.println("content:");
			System.out.println(contents);

			if (parser == 0) {
				/**
				 * try { JAXBContext jxbc = new
				 * org.apache.jorphan.tomcat.manager.ObjectFactory();
				 * Unmarshaller mar = jxbc.createUnmarshaller();
				 * 
				 * start = System.currentTimeMillis(); for (int idx=0; idx <
				 * loops; idx++){ StreamSource ss = new StreamSource( new
				 * ByteArrayInputStream(contents.getBytes())); Object ld =
				 * mar.unmarshal(ss); } end = System.currentTimeMillis();
				 * System.out.println("elapsed Time: " + (end - start)); } catch
				 * (JAXBException e){ }
				 */
			} else {
				org.apache.jmeter.monitor.model.ObjectFactory of = org.apache.jmeter.monitor.model.ObjectFactory
						.getInstance();
				start = System.currentTimeMillis();
				for (int idx = 0; idx < loops; idx++) {
					// NOTUSED org.apache.jmeter.monitor.model.Status st =
					of.parseBytes(contents.getBytes());
				}
				end = System.currentTimeMillis();
				System.out.println("elapsed Time: " + (end - start));
			}

		} else {
			System.out.println("missing paramters:");
			System.out.println("parser file iterations");
			System.out.println("example: jaxb status.xml 1000");
		}
	}
*****************************************
*****************************************
SATD id: 260		Size: 70
	public static void main(String[] args) {
		if (args.length == 3) {
			int parser = 0;
			String file = null;
			int loops = 1000;
			if (args[0] != null) {
				if (!args[0].equals("jaxb")) {
					parser = 1;
				}
			}
			if (args[1] != null) {
				file = args[1];
			}
			if (args[2] != null) {
				loops = Integer.parseInt(args[2]);
			}

			java.io.File infile = new java.io.File(file);
			java.io.FileInputStream fis = null;
			java.io.InputStreamReader isr = null;
			StringBuffer buf = new StringBuffer();
			try {
				fis = new java.io.FileInputStream(infile);
				isr = new java.io.InputStreamReader(fis);
				java.io.BufferedReader br = new java.io.BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					buf.append(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			long start = 0;
			long end = 0;
			String contents = buf.toString().trim();
			System.out.println("start test: " + loops + " iterations");
			System.out.println("content:");
			System.out.println(contents);

			if (parser == 0) {
				/**
				 * try { JAXBContext jxbc = new
				 * org.apache.jorphan.tomcat.manager.ObjectFactory();
				 * Unmarshaller mar = jxbc.createUnmarshaller();
				 * 
				 * start = System.currentTimeMillis(); for (int idx=0; idx <
				 * loops; idx++){ StreamSource ss = new StreamSource( new
				 * ByteArrayInputStream(contents.getBytes())); Object ld =
				 * mar.unmarshal(ss); } end = System.currentTimeMillis();
				 * System.out.println("elapsed Time: " + (end - start)); } catch
				 * (JAXBException e){ }
				 */
			} else {
				org.apache.jmeter.monitor.model.ObjectFactory of = org.apache.jmeter.monitor.model.ObjectFactory
						.getInstance();
				start = System.currentTimeMillis();
				for (int idx = 0; idx < loops; idx++) {
					// NOTUSED org.apache.jmeter.monitor.model.Status st =
					of.parseBytes(contents.getBytes());
				}
				end = System.currentTimeMillis();
				System.out.println("elapsed Time: " + (end - start));
			}

		} else {
			System.out.println("missing paramters:");
			System.out.println("parser file iterations");
			System.out.println("example: jaxb status.xml 1000");
		}
	}
*****************************************
*****************************************
SATD id: 261		Size: 12
	// TODO: Do we really need to have all these menubar methods duplicated
	// here? Perhaps we can make the menu bar accessible through GuiPackage?

	/**
	 * Specify whether or not the File|Load menu item should be enabled.
	 * 
	 * @param enabled
	 *            true if the menu item should be enabled, false otherwise
	 */
	public void setFileLoadEnabled(boolean enabled) {
        super.setFileLoadEnabled(enabled);
	}
*****************************************
*****************************************
SATD id: 262		Size: 79
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
*****************************************
*****************************************
SATD id: 263		Size: 1
	// NOTUSED private String chosenFile;
*****************************************
*****************************************
SATD id: 264		Size: 83
    public SampleResult sample(Entry entry) {
        SampleResult results = new SampleResult();
        results.setDataType(SampleResult.TEXT);
        results.setSampleLabel(getName());
        
        String command = getCommand();
        Arguments args = getArguments();
        Arguments environment = getEnvironmentVariables();
        boolean checkReturnCode = getCheckReturnCode();
        int expectedReturnCode = getExpectedReturnCode();
        List<String> cmds = new ArrayList<String>(args.getArgumentCount()+1);
        StringBuilder cmdLine = new StringBuilder((null == command) ? "" : command);
        cmds.add(command);
        for (int i=0;i<args.getArgumentCount();i++) {
            Argument arg = args.getArgument(i);
            cmds.add(arg.getPropertyAsString(Argument.VALUE));
            cmdLine.append(" ");
            cmdLine.append(cmds.get(i+1));
        }

        Map<String,String> env = new HashMap<String, String>();
        for (int i=0;i<environment.getArgumentCount();i++) {
            Argument arg = environment.getArgument(i);
            env.put(arg.getName(), arg.getPropertyAsString(Argument.VALUE));
        }
        
        File directory = null;
        if(StringUtils.isEmpty(getDirectory())) {
            directory = new File(FileServer.getDefaultBase());
            if(log.isDebugEnabled()) {
                log.debug("Using default directory:"+directory.getAbsolutePath());
            }
        } else {
            directory = new File(getDirectory());
            if(log.isDebugEnabled()) {
                log.debug("Using configured directory:"+directory.getAbsolutePath());
            }
        }
        
        if(log.isDebugEnabled()) {
            log.debug("Will run :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                    " with environment:"+env);
        }

        results.setSamplerData("Working Directory:"+directory.getAbsolutePath()+
                "\nEnvironment:"+env+
                "\nExecuting:" + cmdLine.toString());
        
        SystemCommand nativeCommand = new SystemCommand(directory, env, getStdin(), getStdout(), getStderr());
        
        try {
            results.sampleStart();
            int returnCode = nativeCommand.run(cmds);
            results.sampleEnd();
            results.setResponseCode(Integer.toString(returnCode)); // TODO is this the best way to do this?
            if(log.isDebugEnabled()) {
                log.debug("Ran :"+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                        " with execution environment:"+nativeCommand.getExecutionEnvironment()+ " => " + returnCode);
            }

            if (checkReturnCode && (returnCode != expectedReturnCode)) {
                results.setSuccessful(false);
                results.setResponseMessage("Uexpected return code.  Expected ["+expectedReturnCode+"]. Actual ["+returnCode+"].");
            } else {
                results.setSuccessful(true);
                results.setResponseMessage("OK");
            }
        } catch (IOException ioe) {
            results.sampleEnd();
            results.setSuccessful(false);
            // results.setResponseCode("???"); TODO what code should be set here?
            results.setResponseMessage("Exception occured whilst executing System Call: " + ioe);
        } catch (InterruptedException ie) {
            results.sampleEnd();
            results.setSuccessful(false);
            // results.setResponseCode("???"); TODO what code should be set here?
            results.setResponseMessage("System Sampler Interupted whilst executing System Call: " + ie);
        }

        results.setResponseData(nativeCommand.getOutResult().getBytes()); // default charset is deliberate here
            
        return results;
    }
*****************************************
*****************************************
SATD id: 265		Size: 1
	// NOT USED transient protected ThreadGroup THREADGROUP = null;
*****************************************
*****************************************
SATD id: 266		Size: 1
	private JMenuItem run_shut; // all the others could be private too?
*****************************************
*****************************************
SATD id: 267		Size: 25
    public ReportTreeNode addComponent(TestElement component,
            ReportTreeNode node) throws IllegalUserActionException {
        if (node.getUserObject() instanceof AbstractConfigGui) {
            throw new IllegalUserActionException(
                    "This node cannot hold sub-elements");
        }
        ReportGuiPackage.getInstance().updateCurrentNode();
        JMeterGUIComponent guicomp = ReportGuiPackage.getInstance().getGui(component);
        guicomp.configure(component);
        guicomp.modifyTestElement(component);
        ReportGuiPackage.getInstance().getCurrentGui(); // put the gui object back
        // to the way it was.
        ReportTreeNode newNode = new ReportTreeNode(component, this);

        // This check the state of the TestElement and if returns false it
        // disable the loaded node
        try {
            newNode.setEnabled(component.isEnabled());
        } catch (Exception e) { // TODO can this ever happen?
            newNode.setEnabled(true);
        }

        this.insertNodeInto(newNode, node, node.getChildCount());
        return newNode;
    }
*****************************************
*****************************************
SATD id: 268		Size: 18
    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl,
            URLCollection coll, String encoding) throws HTMLParseException {
        try {
            String contents = new String(html,encoding); 
            LagartoParser lagartoParser = new LagartoParser(contents);
            JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll);
            lagartoParser.parse(tagVisitor);
            return coll.iterator();
        } catch (LagartoException e) {
            // TODO is it the best way ? https://issues.apache.org/bugzilla/show_bug.cgi?id=55634
            if(log.isDebugEnabled()) {
                log.debug("Error extracting embedded resource URLs from:'"+baseUrl+"', probably not text content, message:"+e.getMessage());
            }
            return Collections.<URL>emptyList().iterator();
        } catch (Exception e) {
            throw new HTMLParseException(e);
        }
    }
*****************************************
*****************************************
SATD id: 269		Size: 98
    private void init()
    {
		// TODO: add support for Bean Customizers

        setLayout(new BorderLayout(0, 5));

        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints cl= new GridBagConstraints(); // for labels
		cl.gridx= 0;
		cl.anchor= GridBagConstraints.LINE_END;
		cl.insets= new Insets(0, 1, 0, 1);

		GridBagConstraints ce= new GridBagConstraints(); // for editors
		ce.fill= GridBagConstraints.BOTH;
		ce.gridx= 1;
		ce.weightx= 1.0;
		ce.insets= new Insets(0, 1, 0, 1);
		
		GridBagConstraints cp= new GridBagConstraints(); // for panels
		cp.fill= GridBagConstraints.BOTH;
		cp.gridx= 1;
		cp.gridy= GridBagConstraints.RELATIVE;
		cp.gridwidth= 2;
		cp.weightx= 1.0;

		JPanel currentPanel= mainPanel;
		String currentGroup= "";
		int y=0;
		
        for (int i=0; i<editors.length; i++)
        {
            if (editors[i] == null) continue;

			if (log.isDebugEnabled())
			{
				log.debug("Laying property "+descriptors[i].getName());
			}
			
			String g= group(descriptors[i]);
			if (! currentGroup.equals(g))
			{
				if (currentPanel != mainPanel)
				{
					mainPanel.add(currentPanel, cp);
				}
				currentGroup= g;
				currentPanel= new JPanel(new GridBagLayout()); 
				currentPanel.setBorder(
					BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(),
						groupDisplayName(g)));
				cp.weighty= 0.0;
				y= 0;
			}

			Component customEditor= editors[i].getCustomEditor();

			boolean multiLineEditor= false;
			if (customEditor.getPreferredSize().height > 50)
			{
				// TODO: the above works in the current situation, but it's
				// just a hack. How to get each editor to report whether it
				// wants to grow bigger? Whether the property label should
				// be at the left or at the top of the editor? ...?
				multiLineEditor= true;
			}
			
			JLabel label= createLabel(descriptors[i]);
			label.setLabelFor(customEditor);

			cl.gridy= y;
			cl.gridwidth= multiLineEditor ? 2 : 1;
			cl.anchor= multiLineEditor 
				? GridBagConstraints.CENTER
				: GridBagConstraints.LINE_END;
            currentPanel.add(label, cl);

			ce.gridx= multiLineEditor ? 0 : 1;
			ce.gridy= multiLineEditor ? ++y : y;
			ce.gridwidth= multiLineEditor ? 2 : 1;
			ce.weighty= multiLineEditor ? 1.0 : 0.0;

			cp.weighty+= ce.weighty;

            currentPanel.add(customEditor, ce);

            y++;
        }
		if (currentPanel != mainPanel)
		{
			mainPanel.add(currentPanel, cp);
		}
        add(mainPanel, BorderLayout.CENTER);
    }
*****************************************
*****************************************
SATD id: 270		Size: 54
	// It might be useful also to make this available in the 'Request' tab, for
	// when posting JSON.
	private static String prettyJSON(String json) {
		StringBuffer pretty = new StringBuffer(json.length() * 2); // Educated guess

		final String tab = ":   "; // $NON-NLS-1$
		StringBuffer index = new StringBuffer();
		String nl = ""; // $NON-NLS-1$

		Matcher valueOrPair = VALUE_OR_PAIR_PATTERN.matcher(json);

		boolean misparse = false;

		for (int i = 0; i < json.length(); ) {
			final char currentChar = json.charAt(i);
			if ((currentChar == '{') || (currentChar == '[')) {
				pretty.append(nl).append(index).append(currentChar);
				i++;
				index.append(tab);
				misparse = false;
			}
			else if ((currentChar == '}') || (currentChar == ']')) {
				if (index.length() > 0) {
					index.delete(0, tab.length());
				}
				pretty.append(nl).append(index).append(currentChar);
				i++;
				int j = i;
				while ((j < json.length()) && Character.isWhitespace(json.charAt(j))) {
					j++;
				}
				if ((j < json.length()) && (json.charAt(j) == ',')) {
					pretty.append(","); // $NON-NLS-1$
					i=j+1;
				}
				misparse = false;
			}
			else if (valueOrPair.find(i) && valueOrPair.group().length() > 0) {
				pretty.append(nl).append(index).append(valueOrPair.group());
				i=valueOrPair.end();
				misparse = false;
			}
			else {
				if (!misparse) {
					pretty.append(nl).append("- Parse failed from:");
				}
				pretty.append(currentChar);
				i++;
				misparse = true;
			}
			nl = "\n"; // $NON-NLS-1$
		}
		return pretty.toString();
	}
*****************************************
*****************************************
SATD id: 30		Size: 23
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
*****************************************
*****************************************
SATD id: 33		Size: 126
    public SampleResult sample(Entry e) {
        SampleResult parent = new SampleResult();
        boolean isOK = false; // Did sample succeed?
        boolean deleteMessages = getDeleteMessages();

        parent.setSampleLabel(getName());
        
        String samplerString = toString();
        parent.setSamplerData(samplerString);

        /*
         * Perform the sampling
         */
        parent.sampleStart(); // Start timing
        try {
            // Create empty properties
            Properties props = new Properties();

            // Get session
            Session session = Session.getDefaultInstance(props, null);

            // Get the store
            Store store = session.getStore(getServerType());
            store.connect(getServer(), getPortAsInt(), getUserName(), getPassword());

            // Get folder
            Folder folder = store.getFolder(getFolder());
            if (deleteMessages) {
                folder.open(Folder.READ_WRITE);
            } else {
                folder.open(Folder.READ_ONLY);
            }

            // Get directory
            Message messages[] = folder.getMessages();
            StringBuilder pdata = new StringBuilder();
            pdata.append(messages.length);
            pdata.append(" messages found\n");
            parent.setResponseData(pdata.toString(),null);
            parent.setDataType(SampleResult.TEXT);
            parent.setContentType("text/plain"); // $NON-NLS-1$

            int n = getNumMessages();
            if (n == ALL_MESSAGES || n > messages.length) {
                n = messages.length;
            }

            parent.setSampleCount(n); // TODO is this sensible?
            
            for (int i = 0; i < n; i++) {
                StringBuilder cdata = new StringBuilder();
                SampleResult child = new SampleResult();
                child.sampleStart();
                Message message = messages[i];
                
                cdata.append("Message "); // $NON-NLS-1$
                cdata.append(message.getMessageNumber());
                child.setSampleLabel(cdata.toString());
                child.setSamplerData(cdata.toString());
                cdata.setLength(0);

                final String contentType = message.getContentType();
                child.setContentType(contentType);// Store the content-type

                if (isStoreMimeMessage()) {
                    // Don't save headers - they are already in the raw message
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    message.writeTo(bout);
                    child.setResponseData(bout.toByteArray()); // Save raw message
                    child.setDataType(SampleResult.TEXT);
                    child.setDataEncoding("iso-8859-1"); // RFC 822 uses ascii
                    child.setEncodingAndType(contentType);// Parse the content-type
                } else {
                    child.setEncodingAndType(contentType);// Parse the content-type
                    @SuppressWarnings("unchecked") // Javadoc for the API says this is OK
                    Enumeration<Header> hdrs = message.getAllHeaders();
                    while(hdrs.hasMoreElements()){
                        Header hdr = hdrs.nextElement();
                        String value = hdr.getValue();
                        try {
                            value = MimeUtility.decodeText(value);
                        } catch (UnsupportedEncodingException uce) {
                            // ignored
                        }
                        cdata.append(hdr.getName()).append(": ").append(value).append("\n");
                    }
                    child.setResponseHeaders(cdata.toString());
                    cdata.setLength(0);
                    appendMessageData(child, message);
                }

                if (deleteMessages) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
                child.setResponseOK();
                child.sampleEnd();
                parent.addSubResult(child);
            }

            // Close connection
            folder.close(true);
            store.close();

            parent.setResponseCodeOK();
            parent.setResponseMessageOK();
            isOK = true;
        } catch (NoClassDefFoundError ex) {
            log.debug("",ex);// No need to log normally, as we set the status
            parent.setResponseCode("500"); // $NON-NLS-1$
            parent.setResponseMessage(ex.toString());
        } catch (MessagingException ex) {
            log.debug("", ex);// No need to log normally, as we set the status
            parent.setResponseCode("500"); // $NON-NLS-1$
            parent.setResponseMessage(ex.toString());
        } catch (IOException ex) {
            log.debug("", ex);// No need to log normally, as we set the status
            parent.setResponseCode("500"); // $NON-NLS-1$
            parent.setResponseMessage(ex.toString());
        }

        if (parent.getEndTime()==0){// not been set by any child samples
            parent.sampleEnd();
        }
        parent.setSuccessful(isOK);
        return parent;
    }
*****************************************
*****************************************
SATD id: 34		Size: 102
	public AssertionResult getResult(SampleResult inResponse) {
		log.debug("HTMLAssertions.getResult() called");

		// no error as default
		AssertionResult result = new AssertionResult();

		if (inResponse.getResponseData() == null) {
			return result.setResultForNull();
		}

		result.setFailure(false);

		// create parser
		Tidy tidy = null;
		try {
			log.debug("HTMLAssertions.getResult(): Setup tidy ...");
			log.debug("doctype: " + getDoctype());
			log.debug("errors only: " + isErrorsOnly());
			log.debug("error threshold: " + getErrorThreshold());
			log.debug("warning threshold: " + getWarningThreshold());
			log.debug("html mode: " + isHTML());
			log.debug("xhtml mode: " + isXHTML());
			log.debug("xml mode: " + isXML());
			tidy = new Tidy();
			tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
			tidy.setQuiet(false);
			tidy.setShowWarnings(true);
			tidy.setOnlyErrors(isErrorsOnly());
			tidy.setDocType(getDoctype());
			if (isXHTML()) {
				tidy.setXHTML(true);
			} else if (isXML()) {
				tidy.setXmlTags(true);
			}
			log.debug("err file: " + getFilename());
			tidy.setErrfile(getFilename());

			if (log.isDebugEnabled()) {
				log.debug("getParser : tidy parser created - " + tidy);
			}
			log.debug("HTMLAssertions.getResult(): Tidy instance created!");

		} catch (Exception e) {//TODO replace with proper Exception
			log.error("Unable to instantiate tidy parser", e);
			result.setFailure(true);
			result.setFailureMessage("Unable to instantiate tidy parser");
			// return with an error
			return result;
		}

		/*
		 * Run tidy.
		 */
		try {
			log.debug("HTMLAssertions.getResult(): start parsing with tidy ...");

			StringWriter errbuf = new StringWriter();
			tidy.setErrout(new PrintWriter(errbuf));
			// Node node = tidy.parseDOM(new
			// ByteArrayInputStream(response.getResponseData()), null);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			log.debug("Start : parse");
			Node node = tidy.parse(new ByteArrayInputStream(inResponse.getResponseData()), os);
			if (log.isDebugEnabled()) {
				log.debug("node : " + node);
			}
			log.debug("End   : parse");
			log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
			log.debug("Output: " + os.toString());

			// write output to file
			writeOutput(errbuf.toString());

			// evaluate result
			if ((tidy.getParseErrors() > getErrorThreshold())
					|| (!isErrorsOnly() && (tidy.getParseWarnings() > getWarningThreshold()))) {
				log.debug("HTMLAssertions.getResult(): errors/warnings detected:");
				log.debug(errbuf.toString());
				result.setFailure(true);
				result.setFailureMessage(MessageFormat.format("Tidy Parser errors:   " + tidy.getParseErrors()
						+ " (allowed " + getErrorThreshold() + ") " + "Tidy Parser warnings: "
						+ tidy.getParseWarnings() + " (allowed " + getWarningThreshold() + ")", new Object[0]));
				// return with an error

			} else if ((tidy.getParseErrors() > 0) || (tidy.getParseWarnings() > 0)) {
				// return with no error
				log.debug("HTMLAssertions.getResult(): there were errors/warnings but threshold to high");
				result.setFailure(false);
			} else {
				// return with no error
				log.debug("HTMLAssertions.getResult(): no errors/warnings detected:");
				result.setFailure(false);
			}

		} catch (Exception e) {//TODO replace with proper Exception
			// return with an error
			log.warn("Cannot parse result content", e);
			result.setFailure(true);
			result.setFailureMessage(e.getMessage());
		}
		return result;
	}
*****************************************
*****************************************
SATD id: 38		Size: 8
	public synchronized void clear() {// TODO: should this be clearData()?
		failureCount = 0;
		successCount = 0;
		siteDown = false;
		successMsgSent = false;
		failureMsgSent = false;
		notifyChangeListeners();
	}
*****************************************
*****************************************
SATD id: 41		Size: 63
	public void start(String[] args) {

		CLArgsParser parser = new CLArgsParser(args, options);
		if (null != parser.getErrorString()) {
			System.err.println("Error: " + parser.getErrorString());
			System.out.println("Usage");
			System.out.println(CLUtil.describeOptions(options).toString());
			return;
		}
		try {
			initializeProperties(parser);
			setProxy(parser);
			log.info("Version " + JMeterUtils.getJMeterVersion());
			log.info("java.version=" + System.getProperty("java.version"));// $NON-NLS-1$ $NON-NLS-2$
			log.info("os.name=" + System.getProperty("os.name"));// $NON-NLS-1$ $NON-NLS-2$
			log.info("os.arch=" + System.getProperty("os.arch"));// $NON-NLS-1$ $NON-NLS-2$
			log.info("os.version=" + System.getProperty("os.version"));// $NON-NLS-1$ $NON-NLS-2$
			log.info("Default Locale=" + Locale.getDefault().getDisplayName());// $NON-NLS-1$
            log.info("JMeter  Locale=" + JMeterUtils.getLocale().getDisplayName());// $NON-NLS-1$
			log.info(JMeterUtils.getJMeterCopyright());
			log.info("JMeterHome="+JMeterUtils.getJMeterHome());// $NON-NLS-1$
            
            updateClassLoader();
            if (log.isDebugEnabled())
            {
                String jcp=System.getProperty("java.class.path");// $NON-NLS-1$
                log.debug(jcp);
            }

            // Set some (hopefully!) useful properties
            long now=System.currentTimeMillis();
            JMeterUtils.setProperty("START.MS",Long.toString(now));
            Date today=new Date(now); // so it agrees with above
            // TODO perhaps should share code with __time() function for this...
            JMeterUtils.setProperty("START.YMD",new SimpleDateFormat("yyyyMMdd").format(today));
            JMeterUtils.setProperty("START.HMS",new SimpleDateFormat("HHmmss").format(today));
            
			if (parser.getArgumentById(VERSION_OPT) != null) {
				System.out.println(JMeterUtils.getJMeterCopyright());
				System.out.println("Version " + JMeterUtils.getJMeterVersion());
			} else if (parser.getArgumentById(HELP_OPT) != null) {
				System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));// $NON-NLS-1$
			} else if (parser.getArgumentById(SERVER_OPT) != null) {
				startServer(JMeterUtils.getPropDefault("server_port", 0));// $NON-NLS-1$
				startBSH();
			} else if (parser.getArgumentById(NONGUI_OPT) == null) {
				startGui(parser.getArgumentById(TESTFILE_OPT));
				startBSH();
			} else {
				startNonGui(parser.getArgumentById(TESTFILE_OPT), parser.getArgumentById(LOGFILE_OPT), parser
						.getArgumentById(REMOTE_OPT));
				startBSH();
			}
		} catch (IllegalUserActionException e) {
			System.out.println(e.getMessage());
			System.out.println("Incorrect Usage");
			System.out.println(CLUtil.describeOptions(options).toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error occurred: " + e.getMessage());
			System.exit(-1);
		}
	}
*****************************************
*****************************************
SATD id: 5		Size: 23
	public void setValue(Object value) {
		if (value == null || ! (value instanceof Double)) {
			setText("#N/A"); // TODO: should this just call super()?
			return;
		}
		double rate = ((Double) value).doubleValue();
		if (rate == Double.MAX_VALUE){
			setText("#N/A"); // TODO: should this just call super()?
			return;
		}
		
	    String unit = "sec";

	    if (rate < 1.0) {
	        rate *= 60.0;
	        unit = "min";
	    }
	    if (rate < 1.0) {
	        rate *= 60.0;
	        unit = "hour";
	    }			
	    setText(formatter.format(rate) + "/" + unit);
	}
*****************************************
*****************************************
SATD id: 56		Size: 23
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
*****************************************
*****************************************
SATD id: 58		Size: 158
package org.apache.jmeter.reporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Save Result responseData to a set of files
 * TODO - perhaps save other items such as headers?
 * 
 * This is mainly intended for validation tests
 * 
 * @author sebb AT apache DOT org
 * @version $Revision$ Last updated: $Date$
 */
public class ResultSaver
    extends AbstractTestElement
    implements Serializable,
    SampleListener,
    Clearable
{
	private static final Logger log = LoggingManager.getLoggerForClass();
    
    // File name sequence number 
    private static long sequenceNumber = 0;    
    
	public static final String FILENAME = "FileSaver.filename";

    private static synchronized long nextNumber(){
    	return ++sequenceNumber;
    }
    /*
     * Constructor is initially called once for each occurrence in the test plan
     * For GUI, several more instances are created
     * Then clear is called at start of test
     * Called several times during test startup
     * The name will not necessarily have been set at this point.
     */
	public ResultSaver(){
		super();
		//log.debug(Thread.currentThread().getName());
		//System.out.println(">> "+me+"        "+this.getName()+" "+Thread.currentThread().getName());		
	}

    /*
     * Constructor for use during startup
     * (intended for non-GUI use)
     * @param name of summariser
     */
    public ResultSaver(String name){
    	this();
    	setName(name);
    }
    
    /*
     * This is called once for each occurrence in the test plan, before the start of the test.
     * The super.clear() method clears the name (and all other properties),
     * so it is called last.
     */
	public void clear()
	{
		//System.out.println("-- "+me+this.getName()+" "+Thread.currentThread().getName());
		super.clear();
		sequenceNumber=0; //TODO is this the right thing to do?
	}
	
	/**
	 * Contains the items needed to collect stats for a summariser
	 * 
	 * @author sebb AT apache DOT org
	 * @version $revision$ Last updated: $date$
	 */
	private static class Totals{

	}
	
	/**
	 * Accumulates the sample in two SampleResult objects
	 * - one for running totals, and the other for deltas
	 * 
	 * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleOccurred(SampleEvent e) {
		SampleResult s = e.getResult();
		saveSample(s);
		SampleResult []sr = s.getSubResults();
		if (sr != null){
			for (int i = 0; i < sr.length; i++){
				saveSample(sr[i]);
			}

		}
    }

	/**
	 * @param s SampleResult to save
	 */
	private void saveSample(SampleResult s) {
		nextNumber();
		String fileName=makeFileName(s.getContentType());
		log.debug("Saving "+s.getSampleLabel()+" in "+fileName);
		//System.out.println(fileName);
		File out = new File(fileName);
		FileOutputStream pw=null;
		try {
			pw = new FileOutputStream(out);
			pw.write(s.getResponseData());
			pw.close();
		} catch (FileNotFoundException e1) {
			log.error("Error creating sample file for "+s.getSampleLabel(),e1);
		} catch (IOException e1) {
			log.error("Error saving sample "+s.getSampleLabel(),e1);
		}
	}
	/**
	 * @return fileName composed of fixed prefix, a number,
	 * and a suffix derived from the contentType
	 */
	private String makeFileName(String contentType) {
		String suffix;
		int i = contentType.indexOf("/");
		if (i == -1){
			suffix="unknown";
		} else {
			suffix=contentType.substring(i+1);
		}
		return getFilename()+sequenceNumber+"."+suffix;
	}
	/* (non-Javadoc)
	 * @see org.apache.jmeter.samplers.SampleListener#sampleStarted(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleStarted(SampleEvent e) 
	{
		// not used
	}

	/* (non-Javadoc)
	 * @see org.apache.jmeter.samplers.SampleListener#sampleStopped(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleStopped(SampleEvent e)
	{
		// not used
	}
	private String getFilename()
	{
		return getPropertyAsString(FILENAME);
	}
}
*****************************************
*****************************************
SATD id: 69		Size: 6
	public void clear()
	{
		//System.out.println("-- "+me+this.getName()+" "+Thread.currentThread().getName());
		super.clear();
		sequenceNumber=0; //TODO is this the right thing to do?
	}
*****************************************
*****************************************
SATD id: 75		Size: 7

	/**
	 * Clear the TestElement of all data.
	 */
	public void clear();
	// TODO - yet another ambiguous name - does it need changing?
	// See also: Clearable, JMeterGUIComponent
*****************************************
*****************************************
SATD id: 76		Size: 7
    private void pause(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Is this silent exception intended
        }
    }
*****************************************
*****************************************
SATD id: 78		Size: 77
    public void run()
    {
        try
        {
            threadContext = JMeterContextService.getContext();
            threadContext.setVariables(threadVars);
            threadContext.setThreadNum(getThreadNum());
            testTree.traverse(compiler);
            running = true;
            //listeners = controller.getListeners();

            if (scheduler)
            {
                //set the scheduler to start
                startScheduler();
            }

			rampUpDelay();
            
            log.info("Thread " + Thread.currentThread().getName() + " started");
            controller.initialize();
            controller.addIterationListener(new IterationListener());
            threadContext.setSamplingStarted(true);
            while (running)
            {
                Sampler sam;
                while (running && (sam=controller.next())!=null)
                {
                    try
                    {
                        threadContext.setCurrentSampler(sam);
                        SamplePackage pack = compiler.configureSampler(sam);
                        
                        //Hack: save the package for any transaction controllers
                        threadContext.getVariables().putObject(PACKAGE_OBJECT,pack);
                        
                        delay(pack.getTimers());
                        Sampler sampler= pack.getSampler();
                        if (sampler instanceof TestBean) ((TestBean)sampler).prepare();               
                        SampleResult result = sampler.sample(null); // TODO: remove this useless Entry parameter
                        result.setThreadName(threadName);
                        threadContext.setPreviousResult(result);
                        runPostProcessors(pack.getPostProcessors());
                        checkAssertions(pack.getAssertions(), result);
                        notifyListeners(pack.getSampleListeners(), result);
                        compiler.done(pack);
                        if (result.isStopThread() || (!result.isSuccessful() && onErrorStopThread)){
                        	stopThread();
                        }
                        if (result.isStopTest() || (!result.isSuccessful() && onErrorStopTest)){
                        	stopTest();
                        }
                        if (scheduler)
                        {
                            //checks the scheduler to stop the iteration
                            stopScheduler();
                        }

                    }
                    catch (Exception e)
                    {
                        log.error("", e);
                    }
                }
                if (controller.isDone())
                {
                    running = false;
                }
            }
        }
        finally
        {
            threadContext.clear();
            log.info("Thread " + threadName + " is done");
            monitor.threadFinished(this);
        }
    }
*****************************************
*****************************************
SATD id: 82		Size: 20
    //TODO - does not appear to be called directly
    public static Vector getControllers(Properties properties)
    {
        String name = "controller.";
        Vector v = new Vector();
        Enumeration names = properties.keys();
        while (names.hasMoreElements())
        {
            String prop = (String) names.nextElement();
            if (prop.startsWith(name))
            {
                Object o =
                    instantiate(
                        properties.getProperty(prop),
                        "org.apache.jmeter.control.SamplerController");
                v.addElement(o);
            }
        }
        return v;
    }
*****************************************
*****************************************
SATD id: 84		Size: 16
        public void run(){
            System.out.println("Reading responses from server ...");
            int x = 0;
            try {
                while ((x = is.read()) > -1) {
                    char c = (char) x;
                    System.out.print(c);
                }
            } catch (IOException e) {
                // TODO Why empty block ?
            } finally {
                System.out.println("... disconnected from server.");
                JOrphanUtils.closeQuietly(is);
            }

        }
*****************************************
*****************************************
SATD id: 87		Size: 15
    // TODO only called by UserParameterXMLParser.getXMLParameters which is a deprecated class
    public static XMLReader getXMLParser() {
        XMLReader reader = null;
        final String parserName = getPropDefault("xml.parser", // $NON-NLS-1$
                "org.apache.xerces.parsers.SAXParser");  // $NON-NLS-1$
        try {
            reader = (XMLReader) instantiate(parserName,
                    "org.xml.sax.XMLReader"); // $NON-NLS-1$
            // reader = xmlFactory.newSAXParser().getXMLReader();
        } catch (Exception e) {
            reader = (XMLReader) instantiate(parserName, // $NON-NLS-1$
                    "org.xml.sax.XMLReader"); // $NON-NLS-1$
        }
        return reader;
    }
*****************************************
*****************************************
SATD id: 88		Size: 33
    // TODO probably not needed
    public static Object instantiate(String className, String impls) {
        if (className != null) {
            className = className.trim();
        }

        if (impls != null) {
            impls = impls.trim();
        }

        try {
            Class<?> c = Class.forName(impls);
            try {
                Class<?> o = Class.forName(className);
                Object res = o.newInstance();
                if (c.isInstance(res)) {
                    return res;
                }
                throw new IllegalArgumentException(className + " is not an instance of " + impls);
            } catch (ClassNotFoundException e) {
                log.error("Error loading class " + className + ": class is not found");
            } catch (IllegalAccessException e) {
                log.error("Error loading class " + className + ": does not have access");
            } catch (InstantiationException e) {
                log.error("Error loading class " + className + ": could not instantiate");
            } catch (NoClassDefFoundError e) {
                log.error("Error loading class " + className + ": couldn't find class " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            log.error("Error loading class " + impls + ": was not found.");
        }
        return null;
    }
*****************************************
*****************************************
SATD id: 99		Size: 22

    // TODO Should this method be synchronized ? all other function execute are
    /** {@inheritDoc} */
    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {
        // return JMeterContextService.getContext().getCurrentSampler().getName();
        String name = "";
        if (currentSampler != null) { // will be null if function is used on TestPlan
            name = currentSampler.getName();
        }
        if (values.length > 0){
            JMeterVariables vars = getVariables();
            if (vars != null) {// May be null if function is used on TestPlan
                String varName = ((CompoundVariable) values[0]).execute().trim();
                if (varName.length() > 0) {
                    vars.put(varName, name);
                }
            }
        }
        return name;
    }
*****************************************
*****************************************
