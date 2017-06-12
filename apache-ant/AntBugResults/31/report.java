File path: src/main/org/apache/tools/ant/taskdefs/optional/jdepend/JDependTask.java
Comment: This is deprecated - use classespath in the future
Initial commit id: 9c51b355
Final commit id: b7d1e9bd
   Bugs between [       5]:
ff41336fc provide a Map based method to access environment variables and use that.  Don't use System.getenv() on OpenVMS.  PR 49366
e160d8323 JDependTask did not close an output file PR: 28557 Obtained from: Jeff Badorek
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
98c3a0ea1 [PATCH] Misspelling: s/occured/occurred/g PR: 27282 Obtained from: Jesse Glick
eba9a3c2d Fix NPE, PR: 24344
   Bugs after [       0]:


Start block index: 466
End block index: 528
    public int executeAsForked(CommandlineJava commandline,
                               ExecuteWatchdog watchdog) throws BuildException {
        // if not set, auto-create the ClassPath from the project
        createClasspath();

        // not sure whether this test is needed but cost nothing to put.
        // hope it will be reviewed by anybody competent
        if (getClasspath().toString().length() > 0) {
            createJvmarg(commandline).setValue("-classpath");
            createJvmarg(commandline).setValue(getClasspath().toString());
        }

        if (getOutputFile() != null) {
            // having a space between the file and its path causes commandline 
            // to add quotes around the argument thus making JDepend not taking 
            // it into account. Thus we split it in two
            commandline.createArgument().setValue("-file");
            commandline.createArgument().setValue(_outputFile.getPath());
            // we have to find a cleaner way to put this output
        }

        // This is deprecated - use classespath in the future
        String[] sourcesPath = getSourcespath().list();
        for (int i = 0; i < sourcesPath.length; i++) {
            File f = new File(sourcesPath[i]);

            // not necessary as JDepend would fail, but why loose some time?
            if (!f.exists() || !f.isDirectory()) {
                throw new BuildException("\"" + f.getPath() + "\" does not " 
                    + "represent a valid directory. JDepend would fail.");
            }
            commandline.createArgument().setValue(f.getPath());
        }

        // This is the new way - use classespath - code is the same for now
        String[] classesPath = getClassespath().list();
        for (int i = 0; i < classesPath.length; i++) {
            File f = new File(classesPath[i]);
            // not necessary as JDepend would fail, but why loose some time?
            if (!f.exists() || !f.isDirectory()) {
                throw new BuildException("\"" + f.getPath() + "\" does not "
                        + "represent a valid directory. JDepend would fail.");
            }
            commandline.createArgument().setValue(f.getPath());
        }

        Execute execute = new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN), watchdog);
        execute.setCommandline(commandline.getCommandline());
        if (getDir() != null) {
            execute.setWorkingDirectory(getDir());
            execute.setAntRun(getProject());
        }

        if (getOutputFile() != null) {
            log("Output to be stored in " + getOutputFile().getPath());
        }
        log(commandline.describeCommand(), Project.MSG_VERBOSE);
        try {
            return execute.execute();
        } catch (IOException e) {
            throw new BuildException("Process fork failed.", e, getLocation());
        }
    }
