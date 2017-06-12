File path: src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java
Comment: O what code should be set here?
Initial commit id: c6f644ac
Final commit id: 3b46f06a
   Bugs between [       3]:
dd30d6171 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
98e597585 Bug 55403 - Enhancement to OS sampler: Support for timeout Fixed compilation error Bugzilla Id: 55403
8d0f4b0fb Bug 55403 - Enhancement to OS sampler: Support for timeout Bugzilla Id: 55403
   Bugs after [       1]:
20c0659ee Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution

Start block index: 98
End block index: 180
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

*********************** Method when SATD was removed **************************

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult results = new SampleResult();
        results.setDataType(SampleResult.TEXT);
        results.setSampleLabel(getName());

        String command = getCommand();
        Arguments args = getArguments();
        Arguments environment = getEnvironmentVariables();
        boolean checkReturnCode = getCheckReturnCode();
        int expectedReturnCode = getExpectedReturnCode();
        List<String> cmds = new ArrayList<>(args.getArgumentCount() + 1);
        StringBuilder cmdLine = new StringBuilder((null == command) ? "" : command);
        cmds.add(command);
        for (int i=0;i<args.getArgumentCount();i++) {
            Argument arg = args.getArgument(i);
            cmds.add(arg.getPropertyAsString(Argument.VALUE));
            cmdLine.append(" ");
            cmdLine.append(cmds.get(i+1));
        }

        Map<String,String> env = new HashMap<>();
        for (int i=0;i<environment.getArgumentCount();i++) {
            Argument arg = environment.getArgument(i);
            env.put(arg.getName(), arg.getPropertyAsString(Argument.VALUE));
        }

        File directory;
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
            log.debug("Will run : "+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                    " with environment: "+env);
        }

        results.setSamplerData("Working Directory: "+directory.getAbsolutePath()+
                "\nEnvironment: "+env+
                "\nExecuting: " + cmdLine.toString());

        SystemCommand nativeCommand = null;
        try {
            nativeCommand = new SystemCommand(directory, getTimeout(), POLL_INTERVAL, env, getStdin(), getStdout(), getStderr());
            results.sampleStart();
            int returnCode = nativeCommand.run(cmds);
            results.sampleEnd();
            results.setResponseCode(Integer.toString(returnCode));
            if(log.isDebugEnabled()) {
                log.debug("Ran : "+cmdLine + " using working directory: "+directory.getAbsolutePath()+
                        " with execution environment: "+nativeCommand.getExecutionEnvironment()+ " => " + returnCode);
            }

            if (checkReturnCode && (returnCode != expectedReturnCode)) {
                results.setSuccessful(false);
                results.setResponseMessage("Unexpected return code.  Expected ["+expectedReturnCode+"]. Actual ["+returnCode+"].");
            } else {
                results.setSuccessful(true);
                results.setResponseMessage("OK");
            }
        } catch (IOException ioe) {
            results.sampleEnd();
            results.setSuccessful(false);
            results.setResponseCode("500"); //$NON-NLS-1$
            results.setResponseMessage("Exception occurred whilst executing system call: " + ioe);
        } catch (InterruptedException ie) {
            results.sampleEnd();
            results.setSuccessful(false);
            results.setResponseCode("500"); //$NON-NLS-1$
            results.setResponseMessage("System Sampler interrupted whilst executing system call: " + ie);
            Thread.currentThread().interrupt();
        }

        if (nativeCommand != null) {
            results.setResponseData(nativeCommand.getOutResult().getBytes()); // default charset is deliberate here
        }

        return results;
    }
