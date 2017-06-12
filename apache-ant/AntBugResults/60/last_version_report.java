    public int executeInVM(CommandlineJava commandline) throws BuildException {
        jdepend.textui.JDepend jdepend;

        if ("xml".equals(format)) {
            jdepend = new jdepend.xmlui.JDepend();
        } else {
            jdepend = new jdepend.textui.JDepend();
        }

        FileWriter fw = null;
        PrintWriter pw = null;
        if (getOutputFile() != null) {
            try {
                fw = new FileWriter(getOutputFile().getPath()); //NOSONAR
            } catch (IOException e) {
                String msg = "JDepend Failed when creating the output file: "
                    + e.getMessage();
                log(msg);
                throw new BuildException(msg);
            }
            pw = new PrintWriter(fw);
            jdepend.setWriter(pw);
            log("Output to be stored in " + getOutputFile().getPath());
        }

        try {
            getWorkingPath().ifPresent(path -> {
                for (String filepath : path.list()) {
                    File f = new File(filepath);
                    // not necessary as JDepend would fail, but why loose
                    // some time?
                    if (!f.exists()) {
                        String msg = "\""
                            + f.getPath()
                            + "\" does not represent a valid"
                            + " file or directory. JDepend would fail.";
                        log(msg);
                        throw new BuildException(msg);
                    }
                    try {
                        jdepend.addDirectory(f.getPath());
                    } catch (IOException e) {
                        String msg =
                            "JDepend Failed when adding a class directory: "
                            + e.getMessage();
                        log(msg);
                        throw new BuildException(msg);
                    }
                }
            });

            // This bit turns <exclude> child tags into patters to ignore
            String[] patterns = defaultPatterns.getExcludePatterns(getProject());
            if (patterns != null && patterns.length > 0) {
                if (setFilter != null) {
                    List<String> v = new ArrayList<>();
                    Collections.addAll(v, patterns);
                    try {
                        Object o = packageFilterC.newInstance(v);
                        setFilter.invoke(jdepend, o);
                    } catch (Throwable e) {
                        log("excludes will be ignored as JDepend doesn't like me: "
                            + e.getMessage(), Project.MSG_WARN);
                    }
                } else {
                    log("Sorry, your version of JDepend doesn't support excludes",
                        Project.MSG_WARN);
                }
            }

            jdepend.analyze();
            if (pw != null && pw.checkError()) {
                throw new IOException(
                    "Encountered an error writing JDepend output");
            }
        } catch (IOException ex) {
            throw new BuildException(ex);
        } finally {
            FileUtils.close(pw);
            FileUtils.close(fw);
        }
        return SUCCESS;
    }
