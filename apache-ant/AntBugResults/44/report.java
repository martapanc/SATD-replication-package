File path: src/main/org/apache/tools/ant/types/Path.java
Comment: XXX is this code still necessary? is there any 1.2+ port?
Initial commit id: 126daf8b
Final commit id: 13000c1a
   Bugs between [       2]:
3ac732f3f Support wildcards in CLASSPATH.  PR 46842.  Based on patch by Mike Murray.
abe974363 Optionally enable caching for <path>.  Should help in situations like PR 45696
   Bugs after [       1]:
1348ebb91 Eclipse tries to write to systemClasspath - PR 60582

Start block index: 633
End block index: 709
    public void addJavaRuntime() {
        if (JavaEnvUtils.isKaffe()) {
            // newer versions of Kaffe (1.1.1+) won't have this,
            // but this will be sorted by FileSet anyway.
            File kaffeShare = new File(System.getProperty("java.home")
                                       + File.separator + "share"
                                       + File.separator + "kaffe");
            if (kaffeShare.isDirectory()) {
                FileSet kaffeJarFiles = new FileSet();
                kaffeJarFiles.setDir(kaffeShare);
                kaffeJarFiles.setIncludes("*.jar");
                addFileset(kaffeJarFiles);
            }
        } else if ("GNU libgcj".equals(System.getProperty("java.vm.name"))) {
            addExisting(systemBootClasspath);
        }

        if (System.getProperty("java.vendor").toLowerCase(Locale.US).indexOf("microsoft") >= 0) {
            // XXX is this code still necessary? is there any 1.2+ port?
            // Pull in *.zip from packages directory
            FileSet msZipFiles = new FileSet();
            msZipFiles.setDir(new File(System.getProperty("java.home")
                + File.separator + "Packages"));
            msZipFiles.setIncludes("*.ZIP");
            addFileset(msZipFiles);
        } else {
            // JDK 1.2+ seems to set java.home to the JRE directory.
            addExisting(new Path(null,
                                 System.getProperty("java.home")
                                 + File.separator + "lib"
                                 + File.separator + "rt.jar"));
            // Just keep the old version as well and let addExisting
            // sort it out.
            addExisting(new Path(null,
                                 System.getProperty("java.home")
                                 + File.separator + "jre"
                                 + File.separator + "lib"
                                 + File.separator + "rt.jar"));

            // Sun's and Apple's 1.4 have JCE and JSSE in separate jars.
            String[] secJars = {"jce", "jsse"};
            for (int i = 0; i < secJars.length; i++) {
                addExisting(new Path(null,
                                     System.getProperty("java.home")
                                     + File.separator + "lib"
                                     + File.separator + secJars[i] + ".jar"));
                addExisting(new Path(null,
                                     System.getProperty("java.home")
                                     + File.separator + ".."
                                     + File.separator + "Classes"
                                     + File.separator + secJars[i] + ".jar"));
            }

            // IBM's 1.4 has rt.jar split into 4 smaller jars and a combined
            // JCE/JSSE in security.jar.
            String[] ibmJars
                = {"core", "graphics", "security", "server", "xml"};
            for (int i = 0; i < ibmJars.length; i++) {
                addExisting(new Path(null,
                                     System.getProperty("java.home")
                                     + File.separator + "lib"
                                     + File.separator + ibmJars[i] + ".jar"));
            }

            // Added for MacOS X
            addExisting(new Path(null,
                                 System.getProperty("java.home")
                                 + File.separator + ".."
                                 + File.separator + "Classes"
                                 + File.separator + "classes.jar"));
            addExisting(new Path(null,
                                 System.getProperty("java.home")
                                 + File.separator + ".."
                                 + File.separator + "Classes"
                                 + File.separator + "ui.jar"));
        }
    }
