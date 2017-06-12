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
SATD id: 208		Size: 4
    	// N.B. Needs to implement Visualizer so that TestBeanGUI can find the correct GUI class
    	// TODO - remove UnsharedComponent ? Probably does not make sense for a TestBean.

        private static final Logger log = LoggingManager.getLoggerForClass();
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
SATD id: 239		Size: 2
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
