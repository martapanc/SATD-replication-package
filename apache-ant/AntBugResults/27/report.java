File path: src/main/org/apache/tools/ant/taskdefs/Cvs.java
Comment: XXX: we should use JCVS (www.ice.com/JCVS) instead of
Initial commit id: bce31805
Final commit id: 5c39c186
   Bugs between [       0]:

   Bugs after [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof

Start block index: 73
End block index: 119
    public void execute() throws BuildException {

	// XXX: we should use JCVS (www.ice.com/JCVS) instead of command line
	// execution so that we don't rely on having native CVS stuff around (SM)
	
	try {
	    String ant=project.getProperty("ant.home");
	    if(ant==null) throw new BuildException("Needs ant.home");

	    StringBuffer sb=new StringBuffer();
	    sb.append(ant).append("/bin/antRun ").append(dest);
	    sb.append(" cvs -d ").append( cvsRoot ).append(" checkout ");
	    if(tag!=null)
		sb.append("-r ").append(tag).append(" ");

	    sb.append( pack );
	    String command=sb.toString();

            project.log(command, "cvs", Project.MSG_WARN);


	    // exec command on system runtime
	    Process proc = Runtime.getRuntime().exec( command);
	    
	    // ignore response
	    InputStreamReader isr=new InputStreamReader(proc.getInputStream());
	    BufferedReader din = new BufferedReader(isr);

	    // pipe CVS output to STDOUT
	    String line;
	    while((line = din.readLine()) != null) {
		project.log(line, "cvs", Project.MSG_WARN);
		//System.out.println(line);
	    }
	    
	    proc.waitFor();
	    int err = proc.exitValue();
	    if (err != 0) {
	       throw new BuildException( "Error " + err + "in " + command);
	    }
	    
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    throw new BuildException("Error checking out: " + pack );
	} catch (InterruptedException ex) {
	}
    }
