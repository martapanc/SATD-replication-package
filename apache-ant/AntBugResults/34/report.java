File path: src/main/org/apache/tools/ant/taskdefs/Javadoc.java
Comment: XXX what is the following doing?
Initial commit id: 3d670369
Final commit id: 13000c1a
   Bugs between [       6]:
7bc745a28 post-process generated javadocs as workaround for CVE-2013-1571 - based on Maven patch by Uwe Schindler - PR 55132
5f20b9914 microoptimizations.  PR 50716
f1a903928 Add attributes for javadocs -docfilessubdirs and -excludedocfilessubdir CLI args.  PR 34455
5f81fd801 javadoc fails if bottom/gead contain line breaks.  PR 43342.  Based on patch by Robert Streich.
a9831bdca Add a new attribute to javadoc to allow URLs for location of offline links.  PR 28881.
44564adf2 -breakiterator has been promoted, PR 34580
   Bugs after [       3]:
d3f98c2b7  Javadoc.postProcessGeneratedJavadocs() fails for Classes that extend Javadoc - PR 56047
812852292 failOnWarning attribute for javadoc.  Patch by Tim Boemker.  PR 55015
39734fdce NPE in javadoc when no destdir is specified.  PR 55949

Start block index: 1562
End block index: 2031
    public void execute() throws BuildException {
        if ("javadoc2".equals(getTaskType())) {
            log("Warning: the task name <javadoc2> is deprecated. Use <javadoc> instead.", Project.MSG_WARN);
        }

        // Whether *this VM* is 1.4+ (but also check executable != null).
        boolean javadoc4 =
            !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2) &&
            !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3);

        Vector packagesToDoc = new Vector();
        Path sourceDirs = new Path(getProject());

        if (packageList != null && sourcePath == null) {
            String msg = "sourcePath attribute must be set when "
                + "specifying packagelist.";
            throw new BuildException(msg);
        }

        if (sourcePath != null) {
            sourceDirs.addExisting(sourcePath);
        }

        parsePackages(packagesToDoc, sourceDirs);

        if (packagesToDoc.size() != 0 && sourceDirs.size() == 0) {
            String msg = "sourcePath attribute must be set when "
                + "specifying package names.";
            throw new BuildException(msg);
        }

        Vector sourceFilesToDoc = (Vector) sourceFiles.clone();
        addFileSets(sourceFilesToDoc);

        if (packageList == null && packagesToDoc.size() == 0
            && sourceFilesToDoc.size() == 0) {
            throw new BuildException("No source files and no packages have "
                                     + "been specified.");
        }

        log("Generating Javadoc", Project.MSG_INFO);

        Commandline toExecute = (Commandline) cmd.clone();
        if (executable != null) {
            toExecute.setExecutable(executable);
        } else {
            toExecute.setExecutable(JavaEnvUtils.getJdkExecutable("javadoc"));
        }

        // ------------------------------------------ general javadoc arguments
        if (doctitle != null) {
            toExecute.createArgument().setValue("-doctitle");
            toExecute.createArgument().setValue(expand(doctitle.getText()));
        }
        if (header != null) {
            toExecute.createArgument().setValue("-header");
            toExecute.createArgument().setValue(expand(header.getText()));
        }
        if (footer != null) {
            toExecute.createArgument().setValue("-footer");
            toExecute.createArgument().setValue(expand(footer.getText()));
        }
        if (bottom != null) {
            toExecute.createArgument().setValue("-bottom");
            toExecute.createArgument().setValue(expand(bottom.getText()));
        }

        if (classpath == null) {
            classpath = (new Path(getProject())).concatSystemClasspath("last");
        } else {
            classpath = classpath.concatSystemClasspath("ignore");
        }

        if (classpath.size() > 0) {
            toExecute.createArgument().setValue("-classpath");
            toExecute.createArgument().setPath(classpath);
        }
        if (sourceDirs.size() > 0) {
            toExecute.createArgument().setValue("-sourcepath");
            toExecute.createArgument().setPath(sourceDirs);
        }

        if (version && doclet == null) {
            toExecute.createArgument().setValue("-version");
        }
        if (author && doclet == null) {
            toExecute.createArgument().setValue("-author");
        }

        if (doclet == null && destDir == null) {
            throw new BuildException("destdir attribute must be set!");
        }

        // ---------------------------- javadoc2 arguments for default doclet

        if (doclet != null) {
            if (doclet.getName() == null) {
                throw new BuildException("The doclet name must be "
                                         + "specified.", getLocation());
            } else {
                toExecute.createArgument().setValue("-doclet");
                toExecute.createArgument().setValue(doclet.getName());
                if (doclet.getPath() != null) {
                    Path docletPath
                        = doclet.getPath().concatSystemClasspath("ignore");
                    if (docletPath.size() != 0) {
                        toExecute.createArgument().setValue("-docletpath");
                        toExecute.createArgument().setPath(docletPath);
                    }
                }
                for (Enumeration e = doclet.getParams();
                     e.hasMoreElements();) {
                    DocletParam param = (DocletParam) e.nextElement();
                    if (param.getName() == null) {
                        throw new BuildException("Doclet parameters must "
                                                 + "have a name");
                    }

                    toExecute.createArgument().setValue(param.getName());
                    if (param.getValue() != null) {
                        toExecute.createArgument()
                            .setValue(param.getValue());
                    }
                }
            }
        }
        Path bcp = new Path(getProject());
        if (bootclasspath != null) {
            bcp.append(bootclasspath);
        }
        bcp = bcp.concatSystemBootClasspath("ignore");
        if (bcp.size() > 0) {
            toExecute.createArgument().setValue("-bootclasspath");
            toExecute.createArgument().setPath(bcp);
        }

        // add the links arguments
        if (links.size() != 0) {
            for (Enumeration e = links.elements(); e.hasMoreElements();) {
                LinkArgument la = (LinkArgument) e.nextElement();

                if (la.getHref() == null || la.getHref().length() == 0) {
                    log("No href was given for the link - skipping",
                        Project.MSG_VERBOSE);
                    continue;
                }
                String link = null;
                if (la.shouldResolveLink()) {
                    File hrefAsFile =
                        getProject().resolveFile(la.getHref());
                    if (hrefAsFile.exists()) {
                        try {
                            link = FILE_UTILS.getFileURL(hrefAsFile)
                                .toExternalForm();
                        } catch (MalformedURLException ex) {
                            // should be impossible
                            log("Warning: link location was invalid "
                                + hrefAsFile, Project.MSG_WARN);
                        }
                    }
                }
                if (link == null) {
                    // is the href a valid URL
                    try {
                        URL base = new URL("file://.");
                        new URL(base, la.getHref());
                        link = la.getHref();
                    } catch (MalformedURLException mue) {
                        // ok - just skip
                        log("Link href \"" + la.getHref()
                            + "\" is not a valid url - skipping link",
                            Project.MSG_WARN);
                        continue;
                    }
                }

                if (la.isLinkOffline()) {
                    File packageListLocation = la.getPackagelistLoc();
                    if (packageListLocation == null) {
                        throw new BuildException("The package list"
                                                 + " location for link "
                                                 + la.getHref()
                                                 + " must be provided "
                                                 + "because the link is "
                                                 + "offline");
                    }
                    File packageListFile =
                        new File(packageListLocation, "package-list");
                    if (packageListFile.exists()) {
                        try {
                            String packageListURL =
                                FILE_UTILS.getFileURL(packageListLocation)
                                .toExternalForm();
                            toExecute.createArgument()
                                .setValue("-linkoffline");
                            toExecute.createArgument()
                                .setValue(link);
                            toExecute.createArgument()
                                .setValue(packageListURL);
                        } catch (MalformedURLException ex) {
                            log("Warning: Package list location was "
                                + "invalid " + packageListLocation,
                                Project.MSG_WARN);
                        }
                    } else {
                        log("Warning: No package list was found at "
                            + packageListLocation, Project.MSG_VERBOSE);
                    }
                } else {
                    toExecute.createArgument().setValue("-link");
                    toExecute.createArgument().setValue(link);
                }
            }
        }

        // add the single group arguments
        // Javadoc 1.2 rules:
        //   Multiple -group args allowed.
        //   Each arg includes 3 strings: -group [name] [packagelist].
        //   Elements in [packagelist] are colon-delimited.
        //   An element in [packagelist] may end with the * wildcard.

        // Ant javadoc task rules for group attribute:
        //   Args are comma-delimited.
        //   Each arg is 2 space-delimited strings.
        //   E.g., group="XSLT_Packages org.apache.xalan.xslt*,
        //                XPath_Packages org.apache.xalan.xpath*"
        if (group != null) {
            StringTokenizer tok = new StringTokenizer(group, ",", false);
            while (tok.hasMoreTokens()) {
                String grp = tok.nextToken().trim();
                int space = grp.indexOf(" ");
                if (space > 0) {
                    String name = grp.substring(0, space);
                    String pkgList = grp.substring(space + 1);
                    toExecute.createArgument().setValue("-group");
                    toExecute.createArgument().setValue(name);
                    toExecute.createArgument().setValue(pkgList);
                }
            }
        }

        // add the group arguments
        if (groups.size() != 0) {
            for (Enumeration e = groups.elements(); e.hasMoreElements();) {
                GroupArgument ga = (GroupArgument) e.nextElement();
                String title = ga.getTitle();
                String packages = ga.getPackages();
                if (title == null || packages == null) {
                    throw new BuildException("The title and packages must "
                                             + "be specified for group "
                                             + "elements.");
                }
                toExecute.createArgument().setValue("-group");
                toExecute.createArgument().setValue(expand(title));
                toExecute.createArgument().setValue(packages);
            }
        }

        // JavaDoc 1.4 parameters
        if (javadoc4 || executable != null) {
            for (Enumeration e = tags.elements(); e.hasMoreElements();) {
                Object element = e.nextElement();
                if (element instanceof TagArgument) {
                    TagArgument ta = (TagArgument) element;
                    File tagDir = ta.getDir(getProject());
                    if (tagDir == null) {
                        // The tag element is not used as a fileset,
                        // but specifies the tag directly.
                        toExecute.createArgument().setValue ("-tag");
                        toExecute.createArgument()
                            .setValue (ta.getParameter());
                    } else {
                        // The tag element is used as a
                        // fileset. Parse all the files and create
                        // -tag arguments.
                        DirectoryScanner tagDefScanner =
                            ta.getDirectoryScanner(getProject());
                        String[] files = tagDefScanner.getIncludedFiles();
                        for (int i = 0; i < files.length; i++) {
                            File tagDefFile = new File(tagDir, files[i]);
                            try {
                                BufferedReader in
                                    = new BufferedReader(
                                          new FileReader(tagDefFile)
                                          );
                                String line = null;
                                while ((line = in.readLine()) != null) {
                                    toExecute.createArgument()
                                        .setValue("-tag");
                                    toExecute.createArgument()
                                        .setValue(line);
                                }
                                in.close();
                            } catch (IOException ioe) {
                                throw new BuildException("Couldn't read "
                                    + " tag file from "
                                    + tagDefFile.getAbsolutePath(), ioe);
                            }
                        }
                    }
                } else {
                    ExtensionInfo tagletInfo = (ExtensionInfo) element;
                    toExecute.createArgument().setValue("-taglet");
                    toExecute.createArgument().setValue(tagletInfo
                                                        .getName());
                    if (tagletInfo.getPath() != null) {
                        Path tagletPath = tagletInfo.getPath()
                            .concatSystemClasspath("ignore");
                        if (tagletPath.size() != 0) {
                            toExecute.createArgument()
                                .setValue("-tagletpath");
                            toExecute.createArgument().setPath(tagletPath);
                        }
                    }
                }
            }

            if (source != null) {
                toExecute.createArgument().setValue("-source");
                toExecute.createArgument().setValue(source);
            }

            if (linksource && doclet == null) {
                toExecute.createArgument().setValue("-linksource");
            }
            if (breakiterator && doclet == null) {
                toExecute.createArgument().setValue("-breakiterator");
            }
            if (noqualifier != null && doclet == null) {
                toExecute.createArgument().setValue("-noqualifier");
                toExecute.createArgument().setValue(noqualifier);
            }
        } else {
            // Not 1.4+.
            if (!tags.isEmpty()) {
                log("-tag and -taglet options not supported on Javadoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (source != null) {
                log("-source option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (linksource) {
                log("-linksource option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (breakiterator) {
                log("-breakiterator option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (noqualifier != null) {
                log("-noqualifier option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
        }
        // Javadoc 1.2/1.3 parameters:
        if (!javadoc4 || executable != null) {
            if (old) {
                toExecute.createArgument().setValue("-1.1");
            }
        } else {
            if (old) {
                log("Javadoc 1.4 doesn't support the -1.1 switch anymore",
                    Project.MSG_WARN);
            }
        }

        File tmpList = null;
        PrintWriter srcListWriter = null;
        try {

            /**
             * Write sourcefiles and package names to a temporary file
             * if requested.
             */
            if (useExternalFile) {
                if (tmpList == null) {
                    tmpList = FILE_UTILS.createTempFile("javadoc", "", null);
                    tmpList.deleteOnExit();
                    toExecute.createArgument()
                        .setValue("@" + tmpList.getAbsolutePath());
                }
                srcListWriter = new PrintWriter(
                                    new FileWriter(tmpList.getAbsolutePath(),
                                                   true));
            }

            Enumeration e = packagesToDoc.elements();
            while (e.hasMoreElements()) {
                String packageName = (String) e.nextElement();
                if (useExternalFile) {
                    srcListWriter.println(packageName);
                } else {
                    toExecute.createArgument().setValue(packageName);
                }
            }

            e = sourceFilesToDoc.elements();
            while (e.hasMoreElements()) {
                SourceFile sf = (SourceFile) e.nextElement();
                String sourceFileName = sf.getFile().getAbsolutePath();
                if (useExternalFile) {
                    // XXX what is the following doing? should it run if !javadoc4 && executable != null?
                    if (javadoc4 && sourceFileName.indexOf(" ") > -1) {
                        String name =
                            sourceFileName.replace(File.separatorChar, '/');
                        srcListWriter.println("\"" + name + "\"");
                    } else {
                        srcListWriter.println(sourceFileName);
                    }
                } else {
                    toExecute.createArgument().setValue(sourceFileName);
                }
            }

        } catch (IOException e) {
            tmpList.delete();
            throw new BuildException("Error creating temporary file",
                                     e, getLocation());
        } finally {
            if (srcListWriter != null) {
                srcListWriter.close();
            }
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
