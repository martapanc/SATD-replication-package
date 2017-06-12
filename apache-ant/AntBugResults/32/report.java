File path: src/main/org/apache/tools/ant/taskdefs/optional/jsp/WLJspc.java
Comment: XX  this array stuff is a remnant of prev trials.. gotta remove.
Initial commit id: 6c26371a
Final commit id: 13000c1a
   Bugs between [       3]:
5f20b9914 microoptimizations.  PR 50716
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
6650efb10 Next try for PR: 12844 (Stochern im Nebel)
   Bugs after [       0]:


Start block index: 130
End block index: 225
    public void execute() throws BuildException {
        if (!destinationDirectory.isDirectory()) {
            throw new BuildException("destination directory " + destinationDirectory.getPath() + 
                                     " is not valid");
        }
                               
        if (!sourceDirectory.isDirectory()) {
            throw new BuildException("src directory " + sourceDirectory.getPath() + 
                                     " is not valid");
        }

        if (destinationPackage == null) {
            throw new BuildException("package attribute must be present.", location);
        }
        
        
        String systemClassPath = System.getProperty("java.class.path");
        
        pathToPackage = this.destinationPackage.replace('.',File.separatorChar);
        // get all the files in the sourceDirectory
        DirectoryScanner ds = super.getDirectoryScanner(sourceDirectory);

        //use the systemclasspath as well, to include the ant jar
        if (compileClasspath == null) {
            compileClasspath = new Path(project);
        }
        
        compileClasspath.append(Path.systemClasspath);
        String[] files = ds.getIncludedFiles();
        
        //Weblogic.jspc calls System.exit() ... have to fork
        // Therefore, takes loads of time 
        // Can pass directories at a time (*.jsp) but easily runs out of memory on hefty dirs 
        // (even on  a Sun)
        Java helperTask = (Java)project.createTask("java");
        helperTask.setFork(true);
        helperTask.setClassname("weblogic.jspc");
        helperTask.setTaskName(getTaskName());
        String[] args = new String[12];
        
        File jspFile = null;
        String parents = "";
        String arg = "";
        int j=0;
        //XXX  this array stuff is a remnant of prev trials.. gotta remove. 
        args[j++] = "-d";
        args[j++] = destinationDirectory.getAbsolutePath().trim(); 
        args[j++] = "-docroot";
        args[j++] = sourceDirectory.getAbsolutePath().trim();
        args[j++] = "-keepgenerated";  //TODO: Parameterise ??
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
        log("Compiling "+filesToDo.size() + " JSP files");
            
        for (int i=0;i<filesToDo.size();i++) {
            //XXX
            // All this to get package according to weblogic standards
            // Can be written better... this is too hacky! 
            // Careful.. similar code in scanDir , but slightly different!!
            jspFile = new File((String) filesToDo.elementAt(i));
            args[j] = "-package";
            parents = jspFile.getParent();
            if ((parents != null)  && (!("").equals(parents))) {
                parents =  this.replaceString(parents,File.separator,"_.");
                args[j+1] = destinationPackage +"."+ "_"+parents;   
            }else {
                args[j+1] = destinationPackage;
            }
            
            
            args[j+2] =  sourceDirectory+File.separator+(String) filesToDo.elementAt(i);
            arg="";
            
            for (int x=0;x<12;x++) {
                arg += " "+ args[x];
            }
            
            System.out.println("arg = " + arg);
            
            helperTask.clearArgs();
            helperTask.setArgs(arg);
            helperTask.setClasspath(compileClasspath);
            if (helperTask.executeJava() != 0) {                         
                log(files[i] + " failed to compile",Project.MSG_WARN) ;
            }
        }
    }
