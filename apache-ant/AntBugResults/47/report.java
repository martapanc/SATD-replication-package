File path: src/main/org/apache/tools/ant/taskdefs/Javac.java
Comment: first: developer could use a little help
Initial commit id: ba0d3f2a
Final commit id: 2f3fc4ce
   Bugs between [       0]:

   Bugs after [      10]:
5ecf92bf5 add an option to suppress the artifical package-info.class files created by <javac>.  PR 52096
28c651a95 Empty package-info.class is created in wrong directory if no destdir is specified.  PR 51947
4a07176ab support sources with extensions other than .java in javac.  Submitted by Andrew Eisenberg.  PR 48829.
c5a945fe3 Allow users to specify a classpath when using a custom adapter in javac, rmic, javah or native2ascii.  PR 11143
130b9317e support source/target on gcj.  PR 46617.  Based on patch by Paweł Zuzelski
f6af37217 PR 37546: Use alternative names for the compiler argument line. (If modern is specified in javac and javac1.5 is used, and for javac1.5 a command line is specified, but no command line is specified for modern, the javac1.5 command line will be used, if no javac has been specified and javac 1.4 is used and a comman line is specified for modern, the command line specified for modern will be used, etc.)
a245b4e9e compiler attributes is used to determine command line arguments even in the fork case, this warning is misleading.  PR: 31664
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
cc9f4f71f Be a little more defensive in a protected method of a non-final public class, PR 26737
f0c61ad2f <javac>'s executable attribute can now also be used to specify the executable for jikes, jvc, sj or gcj. PR: 13814

Start block index: 380
End block index: 453
    protected Path getCompileClasspath(boolean addRuntime) {
        Path classpath = new Path(project);

        // add dest dir to classpath so that previously compiled and
        // untouched classes are on classpath

        if (destDir != null) {
            classpath.setLocation(destDir);
        }

        // Compine the build classpath with the system classpath, in an 
        // order determined by the value of build.classpath

        if (compileClasspath == null) {
            classpath.addExisting(Path.systemClasspath);
        } else {
            String order = project.getProperty("build.sysclasspath");
            if (order == null) order="first";

            if (order.equals("only")) {
                // only: the developer knows what (s)he is doing
                classpath.addExisting(Path.systemClasspath);

            } else if (order.equals("last")) {
                // last: don't trust the developer
                classpath.addExisting(compileClasspath);
                classpath.addExisting(Path.systemClasspath);

            } else if (order.equals("ignore")) {
                // ignore: don't trust anyone
                classpath.addExisting(compileClasspath);
                addRuntime = true;

            } else {
                // first: developer could use a little help
                classpath.addExisting(Path.systemClasspath);
                classpath.addExisting(compileClasspath);
            }
        }

        // optionally add the runtime classes

        if (addRuntime) {
            if (System.getProperty("java.vendor").toLowerCase().indexOf("microsoft") >= 0) {
                // Pull in *.zip from packages directory
                FileSet msZipFiles = new FileSet();
                msZipFiles.setDir(new File(System.getProperty("java.home") + File.separator + "Packages"));
                msZipFiles.setIncludes("*.ZIP");
                classpath.addFileset(msZipFiles);
            }
            else if (Project.getJavaVersion() == Project.JAVA_1_1) {
                classpath.addExisting(new Path(null,
                                                System.getProperty("java.home")
                                                + File.separator + "lib"
                                                + File.separator 
                                                + "classes.zip"));
            } else {
                // JDK > 1.1 seems to set java.home to the JRE directory.
                classpath.addExisting(new Path(null,
                                                System.getProperty("java.home")
                                                + File.separator + "lib"
                                                + File.separator + "rt.jar"));
                // Just keep the old version as well and let addExistingToPath
                // sort it out.
                classpath.addExisting(new Path(null,
                                                System.getProperty("java.home")
                                                + File.separator +"jre"
                                                + File.separator + "lib"
                                                + File.separator + "rt.jar"));
            }
        }
            
        return classpath;
    }
