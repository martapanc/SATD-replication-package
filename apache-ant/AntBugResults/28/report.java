File path: src/main/org/apache/tools/ant/taskdefs/Exec.java
Comment: XXX: we should use JCVS (www.ice.com/JCVS) instead of
Initial commit id: bce31805
Final commit id: fd287b3a
   Bugs between [       0]:

   Bugs after [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof

Start block index: 71
End block index: 128
    public void execute() throws BuildException {
	try {
	    // test if os match
	    String myos=System.getProperty("os.name");
	    project.log("Myos= " + myos, Project.MSG_VERBOSE);
	    if( ( os != null ) && ( os.indexOf(myos) < 0 ) ){
		// this command will be executed only on the specified OS
		project.log("Not found in " + os, Project.MSG_VERBOSE);
		return;
	    }
		
	    // XXX: we should use JCVS (www.ice.com/JCVS) instead of command line
	    // execution so that we don't rely on having native CVS stuff around (SM)
	    
	    String ant=project.getProperty("ant.home");
	    if(ant==null) throw new BuildException("Needs ant.home");
		
	    String antRun = project.resolveFile(ant + "/bin/antRun").toString();
	    if (myos.toLowerCase().indexOf("windows")>=0)
		antRun=antRun+".bat";
	    command=antRun + " " + project.resolveFile(dir) + " " + command;
            project.log(command, Project.MSG_VERBOSE);
		
	    // exec command on system runtime
	    Process proc = Runtime.getRuntime().exec( command);
	    // ignore response
	    InputStreamReader isr=new InputStreamReader(proc.getInputStream());
	    BufferedReader din = new BufferedReader(isr);
	    
	    PrintWriter fos=null;
	    if( out!=null )  {
		fos=new PrintWriter( new FileWriter( out ) );
        	project.log("Output redirected to " + out, Project.MSG_VERBOSE);
	    }

	    // pipe CVS output to STDOUT
	    String line;
	    while((line = din.readLine()) != null) {
		if( fos==null)
		    project.log(line, "exec", Project.MSG_INFO);
		else
		    fos.println(line);
	    }
	    if(fos!=null)
		fos.close();
	    
	    proc.waitFor();
	    int err = proc.exitValue();
	    if (err != 0) {
		project.log("Result: " + err, "exec", Project.MSG_ERR);
	    }
	    
	} catch (IOException ioe) {
	    throw new BuildException("Error exec: " + command );
	} catch (InterruptedException ex) {
	}

    }
