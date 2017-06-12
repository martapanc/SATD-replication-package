    public void execute() throws BuildException {
        checkTaskName();

        Vector<String> packagesToDoc = new Vector<String>();
        Path sourceDirs = new Path(getProject());

        checkPackageAndSourcePath();

        if (sourcePath != null) {
            sourceDirs.addExisting(sourcePath);
        }

        parsePackages(packagesToDoc, sourceDirs);
        checkPackages(packagesToDoc, sourceDirs);

        @SuppressWarnings("unchecked")
        Vector<SourceFile> sourceFilesToDoc = (Vector<SourceFile>) sourceFiles.clone();
        addSourceFiles(sourceFilesToDoc);

        checkPackagesToDoc(packagesToDoc, sourceFilesToDoc);

        log("Generating Javadoc", Project.MSG_INFO);

        Commandline toExecute = (Commandline) cmd.clone();
        if (executable != null) {
            toExecute.setExecutable(executable);
        } else {
            toExecute.setExecutable(JavaEnvUtils.getJdkExecutable("javadoc"));
        }

        //  Javadoc arguments
        generalJavadocArguments(toExecute);  // general Javadoc arguments
        doSourcePath(toExecute, sourceDirs); // sourcepath
        doDoclet(toExecute);   // arguments for default doclet
        doBootPath(toExecute); // bootpath
        doLinks(toExecute);    // links arguments
        doGroup(toExecute);    // group attribute
        doGroups(toExecute);  // groups attribute
        doDocFilesSubDirs(toExecute); // docfilessubdir attribute

        doJava14(toExecute);
        if (breakiterator && (doclet == null || JAVADOC_5)) {
            toExecute.createArgument().setValue("-breakiterator");
        }
        // If using an external file, write the command line options to it
        if (useExternalFile) {
            writeExternalArgs(toExecute);
        }

        File tmpList = null;
        FileWriter wr = null;
        try {
            /**
             * Write sourcefiles and package names to a temporary file
             * if requested.
             */
            BufferedWriter srcListWriter = null;
            if (useExternalFile) {
                tmpList = FILE_UTILS.createTempFile("javadoc", "", null, true, true);
                toExecute.createArgument()
                    .setValue("@" + tmpList.getAbsolutePath());
                wr = new FileWriter(tmpList.getAbsolutePath(), true);
                srcListWriter = new BufferedWriter(wr);
            }

            doSourceAndPackageNames(
                toExecute, packagesToDoc, sourceFilesToDoc,
                useExternalFile, tmpList, srcListWriter);

            if (useExternalFile) {
                srcListWriter.flush();
            }
        } catch (IOException e) {
            tmpList.delete();
            throw new BuildException("Error creating temporary file",
                                     e, getLocation());
        } finally {
            FileUtils.close(wr);
        }

        if (packageList != null) {
            toExecute.createArgument().setValue("@" + packageList);
        }
        log(toExecute.describeCommand(), Project.MSG_VERBOSE);

        log("Javadoc execution", Project.MSG_INFO);

        JavadocOutputStream out = new JavadocOutputStream(Project.MSG_INFO);
        JavadocOutputStream err = new JavadocOutputStream(Project.MSG_WARN);
        Execute exe = new Execute(new PumpStreamHandler(out, err));
        exe.setAntRun(getProject());

        /*
         * No reason to change the working directory as all filenames and
         * path components have been resolved already.
         *
         * Avoid problems with command line length in some environments.
         */
        exe.setWorkingDirectory(null);
        try {
            exe.setCommandline(toExecute.getCommandline());
            int ret = exe.execute();
            if (ret != 0 && failOnError) {
                throw new BuildException("Javadoc returned " + ret,
                                         getLocation());
            }
            postProcessGeneratedJavadocs();
        } catch (IOException e) {
            throw new BuildException("Javadoc failed: " + e, e, getLocation());
        } finally {
            if (tmpList != null) {
                tmpList.delete();
                tmpList = null;
            }

            out.logFlush();
            err.logFlush();
            try {
                out.close();
                err.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
