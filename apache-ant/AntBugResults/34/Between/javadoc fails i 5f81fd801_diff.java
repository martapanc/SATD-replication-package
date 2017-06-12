diff --git a/src/main/org/apache/tools/ant/taskdefs/Javadoc.java b/src/main/org/apache/tools/ant/taskdefs/Javadoc.java
index dae34ac13..d830098ee 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javadoc.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javadoc.java
@@ -1246,1217 +1246,1233 @@ public class Javadoc extends Task {
         }
 
         /**
          * Get the linkOffline attribute.
          * @return the linkOffline attribute.
          */
         public boolean isLinkOffline() {
             return offline;
         }
 
         /**
          * Sets whether Ant should resolve the link attribute relative
          * to the current basedir.
          * @param resolve a <code>boolean</code> value
          */
         public void setResolveLink(boolean resolve) {
             this.resolveLink = resolve;
         }
 
         /**
          * should Ant resolve the link attribute relative to the
          * current basedir?
          * @return the resolveLink attribute.
          */
         public boolean shouldResolveLink() {
             return resolveLink;
         }
 
     }
 
     /**
      * Creates and adds a -tag argument. This is used to specify
      * custom tags. This argument is only available for Javadoc 1.4,
      * and will generate a verbose message (and then be ignored)
      * when run on Java versions below 1.4.
      * @return tag argument to be configured
      */
     public TagArgument createTag() {
         TagArgument ta = new TagArgument();
         tags.addElement (ta);
         return ta;
     }
 
     /**
      * Scope element verbose names. (Defined here as fields
      * cannot be static in inner classes.) The first letter
      * from each element is used to build up the scope string.
      */
     static final String[] SCOPE_ELEMENTS = {
         "overview", "packages", "types", "constructors",
         "methods", "fields"
     };
 
     /**
      * Class representing a -tag argument.
      */
     public class TagArgument extends FileSet {
         /** Name of the tag. */
         private String name = null;
         /** Whether or not the tag is enabled. */
         private boolean enabled = true;
         /**
          * Scope string of the tag. This will form the middle
          * argument of the -tag parameter when the tag is enabled
          * (with an X prepended for and is parsed from human-readable form.
          */
         private String scope = "a";
 
         /** Sole constructor. */
         public TagArgument () {
             //empty
         }
 
         /**
          * Sets the name of the tag.
          *
          * @param name The name of the tag.
          *             Must not be <code>null</code> or empty.
          */
         public void setName (String name) {
             this.name = name;
         }
 
         /**
          * Sets the scope of the tag. This is in comma-separated
          * form, with each element being one of "all" (the default),
          * "overview", "packages", "types", "constructors", "methods",
          * "fields". The elements are treated in a case-insensitive
          * manner.
          *
          * @param verboseScope The scope of the tag.
          *                     Must not be <code>null</code>,
          *                     should not be empty.
          *
          * @exception BuildException if all is specified along with
          * other elements, if any elements are repeated, if no
          * elements are specified, or if any unrecognised elements are
          * specified.
          */
         public void setScope (String verboseScope) throws BuildException {
             verboseScope = verboseScope.toLowerCase(Locale.US);
 
             boolean[] elements = new boolean[SCOPE_ELEMENTS.length];
 
             boolean gotAll = false;
             boolean gotNotAll = false;
 
             // Go through the tokens one at a time, updating the
             // elements array and issuing warnings where appropriate.
             StringTokenizer tok = new StringTokenizer (verboseScope, ",");
             while (tok.hasMoreTokens()) {
                 String next = tok.nextToken().trim();
                 if (next.equals("all")) {
                     if (gotAll) {
                         getProject().log ("Repeated tag scope element: all",
                                           Project.MSG_VERBOSE);
                     }
                     gotAll = true;
                 } else {
                     int i;
                     for (i = 0; i < SCOPE_ELEMENTS.length; i++) {
                         if (next.equals (SCOPE_ELEMENTS[i])) {
                             break;
                         }
                     }
                     if (i == SCOPE_ELEMENTS.length) {
                         throw new BuildException ("Unrecognised scope element: "
                                                   + next);
                     } else {
                         if (elements[i]) {
                             getProject().log ("Repeated tag scope element: "
                                               + next, Project.MSG_VERBOSE);
                         }
                         elements[i] = true;
                         gotNotAll = true;
                     }
                 }
             }
 
             if (gotNotAll && gotAll) {
                 throw new BuildException ("Mixture of \"all\" and other scope "
                                           + "elements in tag parameter.");
             }
             if (!gotNotAll && !gotAll) {
                 throw new BuildException ("No scope elements specified in tag "
                                           + "parameter.");
             }
             if (gotAll) {
                 this.scope = "a";
             } else {
                 StringBuffer buff = new StringBuffer (elements.length);
                 for (int i = 0; i < elements.length; i++) {
                     if (elements[i]) {
                         buff.append (SCOPE_ELEMENTS[i].charAt(0));
                     }
                 }
                 this.scope = buff.toString();
             }
         }
 
         /**
          * Sets whether or not the tag is enabled.
          *
          * @param enabled Whether or not this tag is enabled.
          */
         public void setEnabled (boolean enabled) {
             this.enabled = enabled;
         }
 
         /**
          * Returns the -tag parameter this argument represented.
          * @return the -tag parameter as a string
          * @exception BuildException if either the name or description
          *                           is <code>null</code> or empty.
          */
         public String getParameter() throws BuildException {
             if (name == null || name.equals("")) {
                 throw new BuildException ("No name specified for custom tag.");
             }
             if (getDescription() != null) {
                 return name + ":" + (enabled ? "" : "X")
                     + scope + ":" + getDescription();
             } else if (!enabled || !"a".equals(scope)) {
                 return name + ":" + (enabled ? "" : "X") + scope;
             } else {
                 return name;
             }
         }
     }
 
     /**
      * Separates packages on the overview page into whatever
      * groups you specify, one group per table.
      * @return a group argument to be configured
      */
     public GroupArgument createGroup() {
         GroupArgument ga = new GroupArgument();
         groups.addElement(ga);
         return ga;
     }
 
 
     /**
      * A class corresponding to the group nested element.
      */
     public class GroupArgument {
         private Html title;
         private Vector packages = new Vector();
 
         /** Constructor for GroupArgument */
         public GroupArgument() {
             //empty
         }
 
         /**
          * Set the title attribute using a string.
          * @param src a <code>String</code> value
          */
         public void setTitle(String src) {
             Html h = new Html();
             h.addText(src);
             addTitle(h);
         }
         /**
          * Set the title attribute using a nested Html value.
          * @param text a <code>Html</code> value
          */
         public void addTitle(Html text) {
             title = text;
         }
 
         /**
          * Get the title.
          * @return the title
          */
         public String getTitle() {
             return title != null ? title.getText() : null;
         }
 
         /**
          * Set the packages to Javadoc on.
          * @param src a comma separated list of packages
          */
         public void setPackages(String src) {
             StringTokenizer tok = new StringTokenizer(src, ",");
             while (tok.hasMoreTokens()) {
                 String p = tok.nextToken();
                 PackageName pn = new PackageName();
                 pn.setName(p);
                 addPackage(pn);
             }
         }
         /**
          * Add a package nested element.
          * @param pn a nested element specifing the package.
          */
         public void addPackage(PackageName pn) {
             packages.addElement(pn);
         }
 
         /**
          * Get the packages as a collon separated list.
          * @return the packages as a string
          */
         public String getPackages() {
             StringBuffer p = new StringBuffer();
             for (int i = 0; i < packages.size(); i++) {
                 if (i > 0) {
                     p.append(":");
                 }
                 p.append(packages.elementAt(i).toString());
             }
             return p.toString();
         }
     }
 
     /**
      * Charset for cross-platform viewing of generated documentation.
      * @param src the name of the charset
      */
     public void setCharset(String src) {
         this.addArgIfNotEmpty("-charset", src);
     }
 
     /**
      * Should the build process fail if Javadoc fails (as indicated by
      * a non zero return code)?
      *
      * <p>Default is false.</p>
      * @param b a <code>boolean</code> value
      */
     public void setFailonerror(boolean b) {
         failOnError = b;
     }
 
     /**
      * Enables the -source switch, will be ignored if Javadoc is not
      * the 1.4 version.
      * @param source a <code>String</code> value
      * @since Ant 1.5
      */
     public void setSource(String source) {
         this.source = source;
     }
 
     /**
      * Sets the actual executable command to invoke, instead of the binary
      * <code>javadoc</code> found in Ant's JDK.
      * @param executable the command to invoke.
      * @since Ant 1.6.3
      */
     public void setExecutable(String executable) {
         this.executable = executable;
     }
 
     /**
      * Adds a packageset.
      *
      * <p>All included directories will be translated into package
      * names be converting the directory separator into dots.</p>
      * @param packageSet a directory set
      * @since 1.5
      */
     public void addPackageset(DirSet packageSet) {
         packageSets.addElement(packageSet);
     }
 
     /**
      * Adds a fileset.
      *
      * <p>All included files will be added as sourcefiles.  The task
      * will automatically add
      * <code>includes=&quot;**&#47;*.java&quot;</code> to the
      * fileset.</p>
      * @param fs a file set
      * @since 1.5
      */
     public void addFileset(FileSet fs) {
         createSourceFiles().add(fs);
     }
 
     /**
      * Adds a container for resource collections.
      *
      * <p>All included files will be added as sourcefiles.</p>
      * @return the source files to configure.
      * @since 1.7
      */
     public ResourceCollectionContainer createSourceFiles() {
         return nestedSourceFiles;
     }
 
     /**
      * Enables the -linksource switch, will be ignored if Javadoc is not
      * the 1.4 version. Default is false
      * @param b a <code>String</code> value
      * @since Ant 1.6
      */
     public void setLinksource(boolean b) {
         this.linksource = b;
     }
 
     /**
      * Enables the -linksource switch, will be ignored if Javadoc is not
      * the 1.4 version. Default is false
      * @param b a <code>String</code> value
      * @since Ant 1.6
      */
     public void setBreakiterator(boolean b) {
         this.breakiterator = b;
     }
 
     /**
      * Enables the -noqualifier switch, will be ignored if Javadoc is not
      * the 1.4 version.
      * @param noqualifier the parameter to the -noqualifier switch
      * @since Ant 1.6
      */
     public void setNoqualifier(String noqualifier) {
         this.noqualifier = noqualifier;
     }
 
     /**
      * If set to true, Ant will also accept packages that only hold
      * package.html files but no Java sources.
      * @param b a <code>boolean</code> value.
      * @since Ant 1.6.3
      */
     public void setIncludeNoSourcePackages(boolean b) {
         this.includeNoSourcePackages = b;
     }
 
     /**
      * Execute the task.
      * @throws BuildException on error
      */
     public void execute() throws BuildException {
         checkTaskName();
 
         Vector packagesToDoc = new Vector();
         Path sourceDirs = new Path(getProject());
 
         checkPackageAndSourcePath();
 
         if (sourcePath != null) {
             sourceDirs.addExisting(sourcePath);
         }
 
         parsePackages(packagesToDoc, sourceDirs);
         checkPackages(packagesToDoc, sourceDirs);
 
         Vector sourceFilesToDoc = (Vector) sourceFiles.clone();
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
 
         // Javadoc 1.4 parameters
         if (JAVADOC_4 || executable != null) {
             doJava14(toExecute);
             if (breakiterator && (doclet == null || JAVADOC_5)) {
                 toExecute.createArgument().setValue("-breakiterator");
             }
         } else {
             doNotJava14();
         }
         // Javadoc 1.2/1.3 parameters:
         if (!JAVADOC_4 || executable != null) {
             if (old) {
                 toExecute.createArgument().setValue("-1.1");
             }
         } else {
             if (old) {
                 log("Javadoc 1.4 doesn't support the -1.1 switch anymore",
                     Project.MSG_WARN);
             }
         }
         // If using an external file, write the command line options to it
         if (useExternalFile && JAVADOC_4) {
             writeExternalArgs(toExecute);
         }
 
         File tmpList = null;
         PrintWriter srcListWriter = null;
 
         try {
             /**
              * Write sourcefiles and package names to a temporary file
              * if requested.
              */
             if (useExternalFile) {
                 tmpList = FILE_UTILS.createTempFile("javadoc", "", null, true, true);
                 toExecute.createArgument()
                     .setValue("@" + tmpList.getAbsolutePath());
                 srcListWriter = new PrintWriter(
                     new FileWriter(tmpList.getAbsolutePath(),
                                    true));
             }
 
             doSourceAndPackageNames(
                 toExecute, packagesToDoc, sourceFilesToDoc,
                 useExternalFile, tmpList, srcListWriter);
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
 
     private void checkTaskName() {
         if ("javadoc2".equals(getTaskType())) {
             log("Warning: the task name <javadoc2> is deprecated."
                 + " Use <javadoc> instead.",
                 Project.MSG_WARN);
         }
     }
 
     private void checkPackageAndSourcePath() {
         if (packageList != null && sourcePath == null) {
             String msg = "sourcePath attribute must be set when "
                 + "specifying packagelist.";
             throw new BuildException(msg);
         }
     }
 
     private void checkPackages(Vector packagesToDoc, Path sourceDirs) {
         if (packagesToDoc.size() != 0 && sourceDirs.size() == 0) {
             String msg = "sourcePath attribute must be set when "
                 + "specifying package names.";
             throw new BuildException(msg);
         }
     }
 
     private void checkPackagesToDoc(
         Vector packagesToDoc, Vector sourceFilesToDoc) {
         if (packageList == null && packagesToDoc.size() == 0
             && sourceFilesToDoc.size() == 0) {
             throw new BuildException("No source files and no packages have "
                                      + "been specified.");
         }
     }
 
     private void doSourcePath(Commandline toExecute, Path sourceDirs) {
         if (sourceDirs.size() > 0) {
             toExecute.createArgument().setValue("-sourcepath");
             toExecute.createArgument().setPath(sourceDirs);
         }
     }
 
     private void generalJavadocArguments(Commandline toExecute) {
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
 
         if (version && doclet == null) {
             toExecute.createArgument().setValue("-version");
         }
         if (author && doclet == null) {
             toExecute.createArgument().setValue("-author");
         }
 
         if (doclet == null && destDir == null) {
             throw new BuildException("destdir attribute must be set!");
         }
     }
 
     private void doDoclet(Commandline toExecute) {
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
     }
 
     private void writeExternalArgs(Commandline toExecute) {
         // If using an external file, write the command line options to it
         File optionsTmpFile = null;
         PrintWriter optionsListWriter = null;
         try {
             optionsTmpFile = FILE_UTILS.createTempFile(
                 "javadocOptions", "", null, true, true);
             String[] listOpt = toExecute.getArguments();
             toExecute.clearArgs();
             toExecute.createArgument().setValue(
                 "@" + optionsTmpFile.getAbsolutePath());
             optionsListWriter = new PrintWriter(
                 new FileWriter(optionsTmpFile.getAbsolutePath(), true));
             for (int i = 0; i < listOpt.length; i++) {
                 String string = listOpt[i];
                 if (string.startsWith("-J-")) {
                     toExecute.createArgument().setValue(string);
                 } else  {
                     if (string.startsWith("-")) {
                         optionsListWriter.print(string);
                         optionsListWriter.print(" ");
                     } else {
                         optionsListWriter.println(quoteString(string));
                     }
                 }
             }
             optionsListWriter.close();
         } catch (IOException ex) {
             if (optionsTmpFile != null) {
                 optionsTmpFile.delete();
             }
             throw new BuildException(
                 "Error creating or writing temporary file for javadoc options",
                 ex, getLocation());
         } finally {
             FileUtils.close(optionsListWriter);
         }
     }
 
     private void doBootPath(Commandline toExecute) {
         Path bcp = new Path(getProject());
         if (bootclasspath != null) {
             bcp.append(bootclasspath);
         }
         bcp = bcp.concatSystemBootClasspath("ignore");
         if (bcp.size() > 0) {
             toExecute.createArgument().setValue("-bootclasspath");
             toExecute.createArgument().setPath(bcp);
         }
     }
 
     private void doLinks(Commandline toExecute) {
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
                     URL packageListURL = la.getPackagelistURL();
                     if (packageListLocation == null
                         && packageListURL == null) {
                         throw new BuildException("The package list"
                                                  + " location for link "
                                                  + la.getHref()
                                                  + " must be provided "
                                                  + "because the link is "
                                                  + "offline");
                     }
                     if (packageListLocation != null) {
                         File packageListFile =
                             new File(packageListLocation, "package-list");
                         if (packageListFile.exists()) {
                             try {
                                 packageListURL =
                                     FILE_UTILS.getFileURL(packageListLocation);
                             } catch (MalformedURLException ex) {
                                 log("Warning: Package list location was "
                                     + "invalid " + packageListLocation,
                                     Project.MSG_WARN);
                             }
                         } else {
                             log("Warning: No package list was found at "
                                 + packageListLocation, Project.MSG_VERBOSE);
                         }
                     }
                     if (packageListURL != null) {
                         toExecute.createArgument().setValue("-linkoffline");
                         toExecute.createArgument().setValue(link);
                         toExecute.createArgument()
                             .setValue(packageListURL.toExternalForm());
                     }
                 } else {
                     toExecute.createArgument().setValue("-link");
                     toExecute.createArgument().setValue(link);
                 }
             }
         }
     }
 
     private void doGroup(Commandline toExecute) {
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
     }
 
     // add the group arguments
     private void doGroups(Commandline toExecute) {
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
     }
 
     // Do java1.4 arguments
     private void doJava14(Commandline toExecute) {
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
                             throw new BuildException(
                                 "Couldn't read "
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
 
         String sourceArg = source != null ? source
             : getProject().getProperty(MagicNames.BUILD_JAVAC_SOURCE);
         if (sourceArg != null) {
             toExecute.createArgument().setValue("-source");
             toExecute.createArgument().setValue(sourceArg);
         }
 
         if (linksource && doclet == null) {
             toExecute.createArgument().setValue("-linksource");
         }
         if (noqualifier != null && doclet == null) {
             toExecute.createArgument().setValue("-noqualifier");
             toExecute.createArgument().setValue(noqualifier);
         }
     }
 
     private void doNotJava14() {
         // Not 1.4+.
         if (!tags.isEmpty()) {
             log("-tag and -taglet options not supported on Javadoc < 1.4",
                 Project.MSG_VERBOSE);
         }
         if (source != null) {
             log("-source option not supported on Javadoc < 1.4",
                 Project.MSG_VERBOSE);
         }
         if (linksource) {
             log("-linksource option not supported on Javadoc < 1.4",
                 Project.MSG_VERBOSE);
         }
         if (breakiterator) {
             log("-breakiterator option not supported on Javadoc < 1.4",
                 Project.MSG_VERBOSE);
         }
         if (noqualifier != null) {
             log("-noqualifier option not supported on Javadoc < 1.4",
                 Project.MSG_VERBOSE);
         }
     }
 
     private void doSourceAndPackageNames(
         Commandline toExecute,
         Vector packagesToDoc,
         Vector sourceFilesToDoc,
         boolean useExternalFile,
         File    tmpList,
         PrintWriter srcListWriter)
         throws IOException {
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
                 // XXX what is the following doing?
                 //     should it run if !javadoc4 && executable != null?
                 if (JAVADOC_4 && sourceFileName.indexOf(" ") > -1) {
                     String name = sourceFileName;
                     if (File.separatorChar == '\\') {
                         name = sourceFileName.replace(File.separatorChar, '/');
                     }
                     srcListWriter.println("\"" + name + "\"");
                 } else {
                     srcListWriter.println(sourceFileName);
                 }
             } else {
                 toExecute.createArgument().setValue(sourceFileName);
             }
         }
     }
 
     /**
      * Quote a string to place in a @ file.
      * @param str the string to quote
      * @return the quoted string, if there is no need to quote the string,
      *         return the original string.
      */
     private String quoteString(final String str) {
         if (!containsWhitespace(str)
             && str.indexOf('\'') == -1
             && str.indexOf('"') == -1) {
             return str;
         }
         if (str.indexOf('\'') == -1) {
             return quoteString(str, '\'');
         } else {
             return quoteString(str, '"');
         }
     }
 
     private boolean containsWhitespace(final String s) {
         final int len = s.length();
         for (int i = 0; i < len; i++) {
             if (Character.isWhitespace(s.charAt(i))) {
                 return true;
             }
         }
         return false;
     }
 
-    private String quoteString(String str, final char delim) {
+    private String quoteString(final String str, final char delim) {
         StringBuffer buf = new StringBuffer(str.length() * 2);
         buf.append(delim);
-        if (str.indexOf('\\') != -1) {
-            str = replace(str, '\\', "\\\\");
-        }
-        if (str.indexOf(delim) != -1) {
-            str = replace(str, delim, "\\" + delim);
-        }
-        buf.append(str);
-        buf.append(delim);
-        return buf.toString();
-    }
-
-    private String replace(String str, char fromChar, String toString) {
-        StringBuffer buf = new StringBuffer(str.length() * 2);
-        for (int i = 0; i < str.length(); ++i) {
-            char ch = str.charAt(i);
-            if (ch == fromChar) {
-                buf.append(toString);
+        final int len = str.length();
+        boolean lastCharWasCR = false;
+        for (int i = 0; i < len; i++) {
+            char c = str.charAt(i);
+            if (c == delim) { // can't put the non-constant delim into a case
+                buf.append('\\').append(c);
+                lastCharWasCR = false;
             } else {
-                buf.append(ch);
+                switch (c) {
+                case '\\':
+                    buf.append("\\\\");
+                    lastCharWasCR = false;
+                    break;
+                case '\r':
+                    // insert a line continuation marker
+                    buf.append("\\\r");
+                    lastCharWasCR = true;
+                    break;
+                case '\n':
+                    // insert a line continuation marker unless this
+                    // is a \r\n sequence in which case \r already has
+                    // created the marker
+                    if (!lastCharWasCR) {
+                        buf.append("\\\n");
+                    } else {
+                        buf.append("\n");
+                    }
+                    lastCharWasCR = false;
+                    break;
+                default:
+                    buf.append(c);
+                    lastCharWasCR = false;
+                    break;
+                }
             }
         }
+        buf.append(delim);
         return buf.toString();
     }
 
     /**
      * Add the files matched by the nested source files to the Vector
      * as SourceFile instances.
      *
      * @since 1.7
      */
     private void addSourceFiles(Vector sf) {
         Iterator e = nestedSourceFiles.iterator();
         while (e.hasNext()) {
             ResourceCollection rc = (ResourceCollection) e.next();
             if (!rc.isFilesystemOnly()) {
                 throw new BuildException("only file system based resources are"
                                          + " supported by javadoc");
             }
             if (rc instanceof FileSet) {
                 FileSet fs = (FileSet) rc;
                 if (!fs.hasPatterns() && !fs.hasSelectors()) {
                     FileSet fs2 = (FileSet) fs.clone();
                     fs2.createInclude().setName("**/*.java");
                     if (includeNoSourcePackages) {
                         fs2.createInclude().setName("**/package.html");
                     }
                     rc = fs2;
                 }
             }
             Iterator iter = rc.iterator();
             while (iter.hasNext()) {
                 sf.addElement(new SourceFile(((FileProvider) iter.next())
                                              .getFile()));
             }
         }
     }
 
     /**
      * Add the directories matched by the nested dirsets to the Vector
      * and the base directories of the dirsets to the Path.  It also
      * handles the packages and excludepackages attributes and
      * elements.
      *
      * @since 1.5
      */
     private void parsePackages(Vector pn, Path sp) {
         Vector addedPackages = new Vector();
         Vector dirSets = (Vector) packageSets.clone();
 
         // for each sourcePath entry, add a directoryset with includes
         // taken from packagenames attribute and nested package
         // elements and excludes taken from excludepackages attribute
         // and nested excludepackage elements
         if (sourcePath != null) {
             PatternSet ps = new PatternSet();
             if (packageNames.size() > 0) {
                 Enumeration e = packageNames.elements();
                 while (e.hasMoreElements()) {
                     PackageName p = (PackageName) e.nextElement();
                     String pkg = p.getName().replace('.', '/');
                     if (pkg.endsWith("*")) {
                         pkg += "*";
                     }
                     ps.createInclude().setName(pkg);
                 }
             } else {
                 ps.createInclude().setName("**");
             }
 
             Enumeration e = excludePackageNames.elements();
             while (e.hasMoreElements()) {
                 PackageName p = (PackageName) e.nextElement();
                 String pkg = p.getName().replace('.', '/');
                 if (pkg.endsWith("*")) {
                     pkg += "*";
                 }
                 ps.createExclude().setName(pkg);
             }
 
 
             String[] pathElements = sourcePath.list();
             for (int i = 0; i < pathElements.length; i++) {
                 File dir = new File(pathElements[i]);
                 if (dir.isDirectory()) {
                     DirSet ds = new DirSet();
                     ds.setDefaultexcludes(useDefaultExcludes);
                     ds.setDir(dir);
                     ds.createPatternSet().addConfiguredPatternset(ps);
                     dirSets.addElement(ds);
                 } else {
                     log("Skipping " + pathElements[i]
                         + " since it is no directory.", Project.MSG_WARN);
                 }
             }
         }
 
         Enumeration e = dirSets.elements();
         while (e.hasMoreElements()) {
             DirSet ds = (DirSet) e.nextElement();
             File baseDir = ds.getDir(getProject());
             log("scanning " + baseDir + " for packages.", Project.MSG_DEBUG);
             DirectoryScanner dsc = ds.getDirectoryScanner(getProject());
             String[] dirs = dsc.getIncludedDirectories();
             boolean containsPackages = false;
             for (int i = 0; i < dirs.length; i++) {
                 // are there any java files in this directory?
                 File pd = new File(baseDir, dirs[i]);
                 String[] files = pd.list(new FilenameFilter () {
                         public boolean accept(File dir1, String name) {
                             return name.endsWith(".java")
                                 || (includeNoSourcePackages
                                     && name.equals("package.html"));
                         }
                     });
 
                 if (files.length > 0) {
                     if ("".equals(dirs[i])) {
                         log(baseDir
                             + " contains source files in the default package,"
                             + " you must specify them as source files"
                             + " not packages.",
                             Project.MSG_WARN);
                     } else {
                         containsPackages = true;
                         String packageName =
                             dirs[i].replace(File.separatorChar, '.');
                         if (!addedPackages.contains(packageName)) {
                             addedPackages.addElement(packageName);
                             pn.addElement(packageName);
                         }
                     }
                 }
             }
             if (containsPackages) {
                 // We don't need to care for duplicates here,
                 // Path.list does it for us.
                 sp.createPathElement().setLocation(baseDir);
             } else {
                 log(baseDir + " doesn\'t contain any packages, dropping it.",
                     Project.MSG_VERBOSE);
             }
         }
     }
 
     private class JavadocOutputStream extends LogOutputStream {
         JavadocOutputStream(int level) {
             super(Javadoc.this, level);
         }
 
         //
         // Override the logging of output in order to filter out Generating
         // messages.  Generating messages are set to a priority of VERBOSE
         // unless they appear after what could be an informational message.
         //
         private String queuedLine = null;
         protected void processLine(String line, int messageLevel) {
             if (messageLevel == Project.MSG_INFO
                 && line.startsWith("Generating ")) {
                 if (queuedLine != null) {
                     super.processLine(queuedLine, Project.MSG_VERBOSE);
                 }
                 queuedLine = line;
             } else {
                 if (queuedLine != null) {
                     if (line.startsWith("Building ")) {
                         super.processLine(queuedLine, Project.MSG_VERBOSE);
                     } else {
                         super.processLine(queuedLine, Project.MSG_INFO);
                     }
                     queuedLine = null;
                 }
                 super.processLine(line, messageLevel);
             }
         }
 
 
         protected void logFlush() {
             if (queuedLine != null) {
                 super.processLine(queuedLine, Project.MSG_VERBOSE);
                 queuedLine = null;
             }
         }
     }
 
     /**
      * Convenience method to expand properties.
      * @param content the string to expand
      * @return the converted string
      */
     protected String expand(String content) {
         return getProject().replaceProperties(content);
     }
 
 }
