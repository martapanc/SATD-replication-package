    public void execute() throws BuildException {
        // test if os match
        String myos = System.getProperty("os.name");
        project.log("Myos = " + myos, Project.MSG_VERBOSE);
        if ((os != null) && (os.indexOf(myos) < 0)){
            // this command will be executed only on the specified OS
            project.log("Not found in " + os, Project.MSG_VERBOSE);
            return;
        }
        
        String ant = project.getProperty("ant.home");
        if (ant == null) throw new BuildException("Property 'ant.home' not found");

        String antRun = project.resolveFile(ant + "/bin/antRun").toString();
        if (myos.toLowerCase().indexOf("windows") >= 0) antRun = antRun + ".bat";
        command = antRun + " " + project.resolveFile(dir) + " " + command;
        
        run(command);
    }
