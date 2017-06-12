File path: src/main/org/apache/tools/ant/taskdefs/AbstractCvsTask.java
Comment: XXX: we should use JCVS (www.ice.com/JCVS) instead of
Initial commit id: 5c39c186
Final commit id: 13000c1a
   Bugs between [       7]:
5f20b9914 microoptimizations.  PR 50716
d85c862e9 support modules with space in their names in <cvs> and <cvschangelog>, will be supported in <cvstagdiff> once PR 35301 is fixed.  PR 38220.
0140d366b Make port attribute work for some "non-standard" CVS clients.  submitted by Johann Herunter.  PR 30124.
228efb0ef Pr 43330, suppress printing of cvs password in case it is given on the command line.
236873d6e Prevent AbstractCvsTask from closing its output and error streams prematurely PR: 30097 Submitted by: Will Wang (telerice at yahoo dot com)
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
5ec7e1db1 Add a new cvsversion task Modify CvsChangeLog to accept a branch as the tag attribute ChangeLogTask.java now inherits from AbstractCvsTask PR: 13510
   Bugs after [       0]:


Start block index: 224
End block index: 339
    public void execute() throws BuildException {
        
        // XXX: we should use JCVS (www.ice.com/JCVS) instead of command line
        // execution so that we don't rely on having native CVS stuff around (SM)
        
        // We can't do it ourselves as jCVS is GPLed, a third party task
        // outside of jakarta repositories would be possible though (SB).
        
        Commandline toExecute = new Commandline();
        
        toExecute.setExecutable("cvs");
        if (cvsRoot != null) {
            toExecute.createArgument().setValue("-d");
            toExecute.createArgument().setValue(cvsRoot);
        }
        if (noexec) {
            toExecute.createArgument().setValue("-n");
        }
        if (quiet) {
            toExecute.createArgument().setValue("-q");
        }
        
        toExecute.createArgument().setLine(command);
        
        //
        // get the other arguments.
        //
        toExecute.addArguments(cmd.getCommandline());
        
        if (cvsPackage != null) {
            toExecute.createArgument().setLine(cvsPackage);
        }
        
        Environment env = new Environment();
        
        if(port>0){
            Environment.Variable var = new Environment.Variable();
            var.setKey("CVS_CLIENT_PORT");
            var.setValue(String.valueOf(port));
            env.addVariable(var);
        }
        
        /**
         * Need a better cross platform integration with <cvspass>, so use the same filename.
         */
        /* But currently we cannot because 'cvs log' is not working with a pass file.
        if(passFile == null){
         
            File defaultPassFile = new File(System.getProperty("user.home") + File.separatorChar + ".cvspass");
            
            if(defaultPassFile.exists())
                this.setPassfile(defaultPassFile);
        }
         */
        
        if(passFile!=null){
            Environment.Variable var = new Environment.Variable();
            var.setKey("CVS_PASSFILE");
            var.setValue(String.valueOf(passFile));
            env.addVariable(var);
            log("Using cvs passfile: " + String.valueOf(passFile), Project.MSG_INFO);
        }
        
        if(cvsRsh!=null){
            Environment.Variable var = new Environment.Variable();
            var.setKey("CVS_RSH");
            var.setValue(String.valueOf(cvsRsh));
            env.addVariable(var);
        }
        
        
        //
        // Just call the getExecuteStreamHandler() and let it handle
        //     the semantics of instantiation or retrieval.
        //
        Execute exe = new Execute(getExecuteStreamHandler(), null);
        
        exe.setAntRun(project);
        if (dest == null) {
            dest = project.getBaseDir();
        }

        exe.setWorkingDirectory(dest);
        exe.setCommandline(toExecute.getCommandline());
        exe.setEnvironment(env.getVariables());

        try {
            log("Executing: " + executeToString(exe), Project.MSG_DEBUG);
            
            int retCode = exe.execute();
            /*Throw an exception if cvs exited with error. (Iulian)*/
            if(failOnError && retCode != 0) {
                throw new BuildException("cvs exited with error code "+ retCode);
            }
        } 
        catch (IOException e) {
            throw new BuildException(e, location);
        } 
        finally {
            //
            // condition used to be if(output == null) outputStream.close().  This is
            //      not appropriate.  Check if the stream itself is not null, then close().
            //
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }
            
            if (errorStream != null) {
                try {
                    errorStream.close();
                } catch (IOException e) {}
            }
        }
    }
