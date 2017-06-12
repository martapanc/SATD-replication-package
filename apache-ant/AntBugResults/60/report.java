File path: src/main/org/apache/tools/ant/taskdefs/optional/jdepend/JDependTask.java
Comment: d class files in a directory to use this or jar files
Initial commit id: 9c51b355
Final commit id: b7d1e9bd
   Bugs between [       5]:
ff41336fc provide a Map based method to access environment variables and use that.  Don't use System.getenv() on OpenVMS.  PR 49366
e160d8323 JDependTask did not close an output file PR: 28557 Obtained from: Jeff Badorek
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
98c3a0ea1 [PATCH] Misspelling: s/occured/occurred/g PR: 27282 Obtained from: Jesse Glick
eba9a3c2d Fix NPE, PR: 24344
   Bugs after [       0]:

Start block index: 360
End block index: 454

public int executeInVM(CommandlineJava commandline) throws BuildException {
    jdepend.textui.JDepend jdepend;

    if ("xml".equals(format)) {
        jdepend = new jdepend.xmlui.JDepend();
    } else {
        jdepend = new jdepend.textui.JDepend();
    }

    if (getOutputFile() != null) {
        FileWriter fw;
        try {
            fw = new FileWriter(getOutputFile().getPath());
        } catch (IOException e) {
            String msg = "JDepend Failed when creating the output file: "
                + e.getMessage();
            log(msg);
            throw new BuildException(msg);
        }
        jdepend.setWriter(new PrintWriter(fw));
        log("Output to be stored in " + getOutputFile().getPath());
    }

    if (getClassespath() != null) {
        // This is the new, better way - use classespath instead
        // of sourcespath.  The code is currently the same - you
        // need class files in a directory to use this - jar files
        // coming soon....
        String[] classesPath = getClassespath().list();
        for (int i = 0; i < classesPath.length; i++) {
            File f = new File(classesPath[i]);
            // not necessary as JDepend would fail, but why loose
            // some time?
            if (!f.exists() || !f.isDirectory()) {
                String msg = "\""
                    + f.getPath()
                    + "\" does not represent a valid"
                    + " directory. JDepend would fail.";
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

    } else if (getSourcespath() != null) {

        // This is the old way and is deprecated - classespath is
        // the right way to do this and is above
        String[] sourcesPath = getSourcespath().list();
        for (int i = 0; i < sourcesPath.length; i++) {
            File f = new File(sourcesPath[i]);

            // not necessary as JDepend would fail, but why loose
            // some time?
            if (!f.exists() || !f.isDirectory()) {
                String msg = "\""
                    + f.getPath()
                    + "\" does not represent a valid"
                    + " directory. JDepend would fail.";
                log(msg);
                throw new BuildException(msg);
            }
            try {
                jdepend.addDirectory(f.getPath());
            } catch (IOException e) {
                String msg =
                    "JDepend Failed when adding a source directory: "
                    + e.getMessage();
                log(msg);
                throw new BuildException(msg);
            }
        }
    }

    // This bit turns <exclude> child tags into patters to ignore
    String[] patterns = defaultPatterns.getExcludePatterns(getProject());
    if (patterns != null && patterns.length > 0) {
        Vector v = new Vector();
        for (int i = 0; i < patterns.length; i++) {
            v.addElement(patterns[i]);
        }
        jdepend.setFilter(new jdepend.framework.PackageFilter(v));
    }

    jdepend.analyze();
    return SUCCESS;
}
