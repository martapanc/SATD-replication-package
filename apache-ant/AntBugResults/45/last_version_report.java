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
            classpath.addExisting(compileClasspath.concatSystemClasspath());
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
