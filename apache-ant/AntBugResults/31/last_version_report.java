    public int executeAsForked(CommandlineJava commandline,
                               ExecuteWatchdog watchdog) throws BuildException {
        runtimeClasses = new Path(getProject());
        addClasspathEntry("/jdepend/textui/JDepend.class");

        // if not set, auto-create the ClassPath from the project
        createClasspath();

        // not sure whether this test is needed but cost nothing to put.
        // hope it will be reviewed by anybody competent
        if (getClasspath().toString().length() > 0) {
            createJvmarg(commandline).setValue("-classpath");
            createJvmarg(commandline).setValue(getClasspath().toString());
        }

        if (includeRuntime) {
            Map<String, String> env = Execute.getEnvironmentVariables();
            String cp = env.get("CLASSPATH");
            if (cp != null) {
                commandline.createClasspath(getProject()).createPath()
                    .append(new Path(getProject(), cp));
            }
            log("Implicitly adding " + runtimeClasses + " to CLASSPATH",
                Project.MSG_VERBOSE);
            commandline.createClasspath(getProject()).createPath()
                .append(runtimeClasses);
        }

        if (getOutputFile() != null) {
            // having a space between the file and its path causes commandline
            // to add quotes around the argument thus making JDepend not taking
            // it into account. Thus we split it in two
            commandline.createArgument().setValue("-file");
            commandline.createArgument().setValue(outputFile.getPath());
            // we have to find a cleaner way to put this output
        }

        getWorkingPath().ifPresent(path -> {
            for (String filepath : path.list()) {
                File f = new File(filepath);
                
                // not necessary as JDepend would fail, but why loose
                // some time?
                if (!f.exists() || !f.isDirectory()) {
                    throw new BuildException(
                        "\"%s\" does not represent a valid directory. JDepend would fail.",
                        f.getPath());
                }
                commandline.createArgument().setValue(f.getPath());
            }
        });
        Execute execute = new Execute(new LogStreamHandler(this,
            Project.MSG_INFO, Project.MSG_WARN), watchdog);
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
