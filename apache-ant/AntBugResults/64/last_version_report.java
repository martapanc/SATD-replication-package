    public void execute() throws BuildException {
        if (!destinationDirectory.isDirectory()) {
            throw new BuildException("destination directory "
                + destinationDirectory.getPath() + " is not valid");
        }

        if (!sourceDirectory.isDirectory()) {
            throw new BuildException("src directory "
                + sourceDirectory.getPath() + " is not valid");
        }

        if (destinationPackage == null) {
            throw new BuildException("package attribute must be present.",
                                     getLocation());
        }


        pathToPackage
            = this.destinationPackage.replace('.', File.separatorChar);
        // get all the files in the sourceDirectory
        DirectoryScanner ds = super.getDirectoryScanner(sourceDirectory);

        //use the systemclasspath as well, to include the ant jar
        if (compileClasspath == null) {
            compileClasspath = new Path(getProject());
        }

        compileClasspath = compileClasspath.concatSystemClasspath();
        String[] files = ds.getIncludedFiles();

        //Weblogic.jspc calls System.exit() ... have to fork
        // Therefore, takes loads of time
        // Can pass directories at a time (*.jsp) but easily runs out of
        // memory on hefty dirs (even on  a Sun)
        Java helperTask = new Java(this);
        helperTask.setFork(true);
        helperTask.setClassname("weblogic.jspc");
        helperTask.setTaskName(getTaskName());
        // CheckStyle:MagicNumber OFF
        String[] args = new String[12];
        // CheckStyle:MagicNumber ON

        File jspFile = null;
        String parents = "";
        int j = 0;
        //XXX  this array stuff is a remnant of prev trials.. gotta remove.
        args[j++] = "-d";
        args[j++] = destinationDirectory.getAbsolutePath().trim();
        args[j++] = "-docroot";
        args[j++] = sourceDirectory.getAbsolutePath().trim();
        args[j++] = "-keepgenerated";
        //Call compiler as class... dont want to fork again
        //Use classic compiler -- can be parameterised?
        args[j++] =  "-compilerclass";
        args[j++] = "sun.tools.javac.Main";
        //Weblogic jspc does not seem to work unless u explicitly set this...
        // Does not take the classpath from the env....
        // Am i missing something about the Java task??
        args[j++] = "-classpath";
        args[j++] = compileClasspath.toString();

        this.scanDir(files);
        log("Compiling " + filesToDo.size() + " JSP files");

        for (int i = 0; i < filesToDo.size(); i++) {
            //XXX
            // All this to get package according to weblogic standards
            // Can be written better... this is too hacky!
            // Careful.. similar code in scanDir , but slightly different!!
            String filename = (String) filesToDo.elementAt(i);
            jspFile = new File(filename);
            args[j] = "-package";
            parents = jspFile.getParent();
            if ((parents != null)  && (!("").equals(parents))) {
                parents =  this.replaceString(parents, File.separator, "_.");
                args[j + 1] = destinationPackage + "." + "_" + parents;
            } else {
                args[j + 1] = destinationPackage;
            }


            args[j + 2] =  sourceDirectory + File.separator + filename;
            helperTask.clearArgs();

            // CheckStyle:MagicNumber OFF
            for (int x = 0; x < j + 3; x++) {
                helperTask.createArg().setValue(args[x]);
            }
            // CheckStyle:MagicNumber ON

            helperTask.setClasspath(compileClasspath);
            if (helperTask.executeJava() != 0) {
                log(filename + " failed to compile", Project.MSG_WARN);
            }
        }
    }
