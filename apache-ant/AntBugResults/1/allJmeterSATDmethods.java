SATD id: 1		Size: 17

// FIXME formatters are not thread-safe

    /** input format for dates read in from cvs log */
    private static final SimpleDateFormat INPUT_DATE
        = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    /**
     * New formatter used to parse CVS date/timestamp.
     */
    private static final SimpleDateFormat CVS1129_INPUT_DATE =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        INPUT_DATE.setTimeZone(utc);
        CVS1129_INPUT_DATE.setTimeZone(utc);
    }
*****************************************
*****************************************
SATD id: 10		Size: 37
    private void parseStandardOutput( BufferedReader reader )
        throws IOException
    {
        String line;
        String lower;
        // We assume, that every output, jike does, stands for an error/warning
        // XXX
        // Is this correct?

        // TODO:
        // A warning line, that shows code, which contains a variable
        // error will cause some trouble. The parser should definitely
        // be much better.

        while( ( line = reader.readLine() ) != null )
        {
            lower = line.toLowerCase();
            if( line.trim().equals( "" ) )
                continue;
            if( lower.indexOf( "error" ) != -1 )
                setError( true );
            else if( lower.indexOf( "warning" ) != -1 )
                setError( false );
            else
            {
                // If we don't know the type of the line
                // and we are in emacs mode, it will be
                // an error, because in this mode, jikes won't
                // always print "error", but sometimes other
                // keywords like "Syntax". We should look for
                // all those keywords.
                if( emacsMode )
                    setError( true );
            }
            log( line );
        }
    }
*****************************************
*****************************************
SATD id: 11		Size: 69
        public void onStartElement(String uri, String tag, String qname,
                                   Attributes attrs,
                                   AntXmlContext context)
            throws SAXParseException
        {
            String id = null;
            String baseDir = null;

            Project project=context.getProject();

            for (int i = 0; i < attrs.getLength(); i++) {
                String key = attrs.getQName(i);
                String value = attrs.getValue(i);
                
                if (key.equals("default")) {
                    if ( value != null && !value.equals("")) {
                        if( !context.ignoreProjectTag )
                            project.setDefaultTarget(value);
                    }
                } else if (key.equals("name")) {
                    if (value != null) {
                        context.currentProjectName=value;

                        if( !context.ignoreProjectTag ) {
                            project.setName(value);
                            project.addReference(value, project);
                        } 
                    }
                } else if (key.equals("id")) {
                    if (value != null) {
                        // What's the difference between id and name ?
                        if( !context.ignoreProjectTag ) {
                            project.addReference(value, project);
                        }
                    }
                } else if (key.equals("basedir")) {
                    if( !context.ignoreProjectTag )
                        baseDir = value;
                } else {
                    // XXX ignore attributes in a different NS ( maybe store them ? )
                    throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i) + "\"", context.locator);
                }
            }

            if( context.ignoreProjectTag ) {
                // no further processing
                return;
            }
            // set explicitely before starting ?
            if (project.getProperty("basedir") != null) {
                project.setBasedir(project.getProperty("basedir"));
            } else {
                // Default for baseDir is the location of the build file.
                if (baseDir == null) {
                    project.setBasedir(context.buildFileParent.getAbsolutePath());
                } else {
                    // check whether the user has specified an absolute path
                    if ((new File(baseDir)).isAbsolute()) {
                        project.setBasedir(baseDir);
                    } else {
                        project.setBaseDir(project.resolveFile(baseDir,
                                                               context.buildFileParent));
                    }
                }
            }
            
            project.addTarget("", context.implicitTarget);
            context.currentTarget=context.implicitTarget;
        }
*****************************************
*****************************************
SATD id: 12		Size: 69
        public void onStartElement(String uri, String tag, String qname,
                                   Attributes attrs,
                                   AntXmlContext context)
            throws SAXParseException
        {
            String id = null;
            String baseDir = null;

            Project project=context.getProject();

            for (int i = 0; i < attrs.getLength(); i++) {
                String key = attrs.getQName(i);
                String value = attrs.getValue(i);
                
                if (key.equals("default")) {
                    if ( value != null && !value.equals("")) {
                        if( !context.ignoreProjectTag )
                            project.setDefaultTarget(value);
                    }
                } else if (key.equals("name")) {
                    if (value != null) {
                        context.currentProjectName=value;

                        if( !context.ignoreProjectTag ) {
                            project.setName(value);
                            project.addReference(value, project);
                        } 
                    }
                } else if (key.equals("id")) {
                    if (value != null) {
                        // What's the difference between id and name ?
                        if( !context.ignoreProjectTag ) {
                            project.addReference(value, project);
                        }
                    }
                } else if (key.equals("basedir")) {
                    if( !context.ignoreProjectTag )
                        baseDir = value;
                } else {
                    // XXX ignore attributes in a different NS ( maybe store them ? )
                    throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i) + "\"", context.locator);
                }
            }

            if( context.ignoreProjectTag ) {
                // no further processing
                return;
            }
            // set explicitely before starting ?
            if (project.getProperty("basedir") != null) {
                project.setBasedir(project.getProperty("basedir"));
            } else {
                // Default for baseDir is the location of the build file.
                if (baseDir == null) {
                    project.setBasedir(context.buildFileParent.getAbsolutePath());
                } else {
                    // check whether the user has specified an absolute path
                    if ((new File(baseDir)).isAbsolute()) {
                        project.setBasedir(baseDir);
                    } else {
                        project.setBaseDir(project.resolveFile(baseDir,
                                                               context.buildFileParent));
                    }
                }
            }
            
            project.addTarget("", context.implicitTarget);
            context.currentTarget=context.implicitTarget;
        }
*****************************************
*****************************************
SATD id: 13		Size: 69
        public void onStartElement(String uri, String tag, String qname,
                                   Attributes attrs,
                                   AntXmlContext context)
            throws SAXParseException
        {
            String id = null;
            String baseDir = null;

            Project project=context.project;

            for (int i = 0; i < attrs.getLength(); i++) {
                String key = attrs.getQName(i);
                String value = attrs.getValue(i);
                
                if (key.equals("default")) {
                    if ( value != null && !value.equals("")) {
                        if( !context.ignoreProjectTag )
                            project.setDefaultTarget(value);
                    }
                } else if (key.equals("name")) {
                    if (value != null) {
                        context.currentProjectName=value;

                        if( !context.ignoreProjectTag ) {
                            project.setName(value);
                            project.addReference(value, project);
                        } 
                    }
                } else if (key.equals("id")) {
                    if (value != null) {
                        // What's the difference between id and name ?
                        if( !context.ignoreProjectTag ) {
                            project.addReference(value, project);
                        }
                    }
                } else if (key.equals("basedir")) {
                    if( !context.ignoreProjectTag )
                        baseDir = value;
                } else {
                    // XXX ignore attributes in a different NS ( maybe store them ? )
                    throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i) + "\"", context.locator);
                }
            }

            if( context.ignoreProjectTag ) {
                // no further processing
                return;
            }
            // set explicitely before starting ?
            if (project.getProperty("basedir") != null) {
                project.setBasedir(project.getProperty("basedir"));
            } else {
                // Default for baseDir is the location of the build file.
                if (baseDir == null) {
                    project.setBasedir(context.buildFileParent.getAbsolutePath());
                } else {
                    // check whether the user has specified an absolute path
                    if ((new File(baseDir)).isAbsolute()) {
                        project.setBasedir(baseDir);
                    } else {
                        project.setBaseDir(project.resolveFile(baseDir,
                                                               context.buildFileParent));
                    }
                }
            }
            
            project.addTarget("", context.implicitTarget);
            context.currentTarget=context.implicitTarget;
        }
*****************************************
*****************************************
SATD id: 14		Size: 95
        public void onStartElement(String uri, String tag, String qname,
                                   Attributes attrs,
                                   AntXmlContext context)
            throws SAXParseException
        {
            String id = null;
            String baseDir = null;

            Project project=context.getProject();

            /** XXX I really don't like this - the XML processor is still
             * too 'involved' in the processing. A better solution (IMO)
             * would be to create UE for Project and Target too, and
             * then process the tree and have Project/Target deal with
             * its attributes ( similar with Description ).
             *
             * If we eventually switch to ( or add support for ) DOM,
             * things will work smoothly - UE can be avoided almost completely
             * ( it could still be created on demand, for backward compat )
             */

            for (int i = 0; i < attrs.getLength(); i++) {
                String key = attrs.getQName(i);
                String value = attrs.getValue(i);

                if (key.equals("default")) {
                    if ( value != null && !value.equals("")) {
                        if( !context.ignoreProjectTag )
                            project.setDefaultTarget(value);
                    }
                } else if (key.equals("name")) {
                    if (value != null) {
                        context.currentProjectName=value;

                        if( !context.ignoreProjectTag ) {
                            project.setName(value);
                            project.addReference(value, project);
                        }
                    }
                } else if (key.equals("id")) {
                    if (value != null) {
                        // What's the difference between id and name ?
                        if( !context.ignoreProjectTag ) {
                            project.addReference(value, project);
                        }
                    }
                } else if (key.equals("basedir")) {
                    if( !context.ignoreProjectTag )
                        baseDir = value;
                } else {
                    // XXX ignore attributes in a different NS ( maybe store them ? )
                    throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i) + "\"", context.locator);
                }
            }

            // XXX Move to Project ( so it is shared by all helpers )
            String antFileProp="ant.file." + context.currentProjectName;
            String dup=project.getProperty(antFileProp);
            if( dup!=null ) {
                project.log("Duplicated project name in import. Project "+
                        context.currentProjectName + " defined first in " +
                        dup + " and again in " + context.buildFile,
                        Project.MSG_WARN);
            }

            if( context.buildFile != null ) {
                project.setUserProperty("ant.file."+context.currentProjectName,
                        context.buildFile.toString());
            }

            if( context.ignoreProjectTag ) {
                // no further processing
                return;
            }
            // set explicitely before starting ?
            if (project.getProperty("basedir") != null) {
                project.setBasedir(project.getProperty("basedir"));
            } else {
                // Default for baseDir is the location of the build file.
                if (baseDir == null) {
                    project.setBasedir(context.buildFileParent.getAbsolutePath());
                } else {
                    // check whether the user has specified an absolute path
                    if ((new File(baseDir)).isAbsolute()) {
                        project.setBasedir(baseDir);
                    } else {
                        project.setBaseDir(project.resolveFile(baseDir,
                                                               context.buildFileParent));
                    }
                }
            }

            project.addTarget("", context.implicitTarget);
            context.currentTarget=context.implicitTarget;
        }
*****************************************
*****************************************
SATD id: 15		Size: 57
    private static void ensureProperties (String msg, File inputFile, 
                                          File workingDir, Project project, 
                                          Properties properties) {
        Hashtable xmlproperties = project.getProperties();
        // Every key identified by the Properties must have been loaded.
        Enumeration propertyKeyEnum = properties.propertyNames();
        while(propertyKeyEnum.hasMoreElements()){
            String currentKey = propertyKeyEnum.nextElement().toString();
            String assertMsg = msg + "-" + inputFile.getName() 
                + " Key=" + currentKey;

            String propertyValue = properties.getProperty(currentKey);

            String xmlValue = (String)xmlproperties.get(currentKey);

            if ( propertyValue.indexOf("ID.") == 0 ) {
                // The property is an id's thing -- either a property
                // or a path.  We need to make sure
                // that the object was created with the given id.
                // We don't have an adequate way of testing the actual
                // *value* of the Path object, though...
                String id = currentKey;
                Object obj = project.getReferences().get(id);

                if ( obj == null ) {
                    fail(assertMsg + " Object ID does not exist.");
                }

                // What is the property supposed to be?
                propertyValue = 
                    propertyValue.substring(3, propertyValue.length());
                if (propertyValue.equals("path")) {
                    if (!(obj instanceof Path)) {
                        fail(assertMsg + " Path ID is a " 
                             + obj.getClass().getName());
                    }
                } else {
                    assertEquals(assertMsg, propertyValue, obj.toString());
                }

            } else {

                if (propertyValue.indexOf("FILE.") == 0) {
                    // The property is the name of a file.  We are testing
                    // a location attribute, so we need to resolve the given
                    // file name in the provided folder.
                    String fileName = 
                        propertyValue.substring(5, propertyValue.length());
                    File f = new File(workingDir, fileName);
                    propertyValue = f.getAbsolutePath();
                }

                assertEquals(assertMsg, propertyValue, xmlValue);
            }

        }
    }
*****************************************
*****************************************
SATD id: 16		Size: 9
    protected final String getXSLTCVersion(String procVersionClassName) 
        throws ClassNotFoundException {
        // there's a convenient xsltc class version but data are
        // private so use package information
        Class procVersion = Class.forName(procVersionClassName);
        Package pkg = procVersion.getPackage();
        return pkg.getName() + " " + pkg.getImplementationTitle() 
            + " " + pkg.getImplementationVersion();
    }
*****************************************
*****************************************
SATD id: 17		Size: 34
    public void transform(File infile, File outfile) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(infile);
            fos = new FileOutputStream(outfile);
            StreamSource src = new StreamSource(fis);
            src.setSystemId(getSystemId(infile));
            StreamResult res = new StreamResult(fos);
            // not sure what could be the need of this...
            res.setSystemId(getSystemId(outfile));

            transformer.transform(src, res);
        } finally {
            // make sure to close all handles, otherwise the garbage
            // collector will close them...whenever possible and
            // Windows may complain about not being able to delete files.
            try {
                if (xslStream != null){
                    xslStream.close();
                }
            } catch (IOException ignored){}
            try {
                if (fis != null){
                    fis.close();
                }
            } catch (IOException ignored){}
            try {
                if (fos != null){
                    fos.close();
                }
            } catch (IOException ignored){}
        }
    }
*****************************************
*****************************************
SATD id: 18		Size: 35
    // XXX: (Jon Skeet) Any reason for writing a message and then using a bare 
    // RuntimeException rather than just using a BuildException here? Is it
    // in case the message could end up being written to no loggers (as the loggers
    // could have failed to be created due to this failure)?
    /**
     *  Creates the default build logger for sending build events to the ant log.
     */
    private BuildLogger createLogger() {
        BuildLogger logger = null;
        if (loggerClassname != null) {
            try {
                logger = (BuildLogger)(Class.forName(loggerClassname).newInstance());
            }
            catch (ClassCastException e) {
                System.err.println("The specified logger class " + loggerClassname +
                                         " does not implement the BuildLogger interface");
                throw new RuntimeException();
            }
            catch (Exception e) {
                System.err.println("Unable to instantiate specified logger class " +
                                           loggerClassname + " : " + e.getClass().getName());
                throw new RuntimeException();
            }
        }
        else {
            logger = new DefaultLogger();
        }

        logger.setMessageOutputLevel(msgOutputLevel);
        logger.setOutputPrintStream(out);
        logger.setErrorPrintStream(err);
        logger.setEmacsMode(emacsMode);

        return logger;
    }
*****************************************
*****************************************
SATD id: 19		Size: 35
    // XXX: (Jon Skeet) Any reason for writing a message and then using a bare 
    // RuntimeException rather than just using a BuildException here? Is it
    // in case the message could end up being written to no loggers (as the loggers
    // could have failed to be created due to this failure)?
    /**
     *  Creates the default build logger for sending build events to the ant log.
     */
    private BuildLogger createLogger() {
        BuildLogger logger = null;
        if (loggerClassname != null) {
            try {
                logger = (BuildLogger)(Class.forName(loggerClassname).newInstance());
            }
            catch (ClassCastException e) {
                System.err.println("The specified logger class " + loggerClassname +
                                         " does not implement the BuildLogger interface");
                throw new RuntimeException();
            }
            catch (Exception e) {
                System.err.println("Unable to instantiate specified logger class " +
                                           loggerClassname + " : " + e.getClass().getName());
                throw new RuntimeException();
            }
        }
        else {
            logger = new DefaultLogger();
        }

        logger.setMessageOutputLevel(msgOutputLevel);
        logger.setOutputPrintStream(out);
        logger.setErrorPrintStream(err);
        logger.setEmacsMode(emacsMode);

        return logger;
    }
*****************************************
*****************************************
SATD id: 2		Size: 18
    // XXX: (Jon Skeet) The comment "if it hasn't been done already" may
    // not be strictly true. wrapper.maybeConfigure() won't configure the same
    // attributes/text more than once, but it may well add the children again,
    // unless I've missed something.
    /**
     * Configures this task - if it hasn't been done already.
     * If the task has been invalidated, it is replaced with an 
     * UnknownElement task which uses the new definition in the project.
     */
    public void maybeConfigure() throws BuildException {
        if (!invalid) {
            if (wrapper != null) {
                wrapper.maybeConfigure(project);
            }
        } else {
            getReplacement();
        }
    }
*****************************************
*****************************************
SATD id: 20		Size: 16
    public Object getPropertyHook(String ns, String name, boolean user) {
        if( getNext() != null ) {
            Object o=getNext().getPropertyHook(ns, name, user);
            if( o!= null ) return o;
        }
        // Experimental/Testing, will be removed
        if( name.startsWith( "toString:" )) {
            name=name.substring( "toString:".length());
            Object v=project.getReference( name );
            if( v==null ) return null;
            return v.toString();
        }


        return null;
    }
*****************************************
*****************************************
SATD id: 21		Size: 26
    // This is used to support ant call and similar tasks. It should be
    // deprecated, it is possible to use a better ( more efficient )
    // mechanism to preserve the context.

    // TODO: do we need to delegate ?

    /**
     * Returns a copy of the properties table.
     * @return a hashtable containing all properties
     *         (including user properties).
     */
    public Hashtable getProperties() {
        Hashtable propertiesCopy = new Hashtable();

        Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            Object value = properties.get(name);
            propertiesCopy.put(name, value);
        }

        // There is a better way to save the context. This shouldn't
        // delegate to next, it's for backward compat only.

        return propertiesCopy;
    }
*****************************************
*****************************************
SATD id: 22		Size: 36
    public String substitute(String input, String argument, int options)
        throws BuildException
    {
        // translate \1 to $1 so that the Perl5Substitution will work
        StringBuffer subst = new StringBuffer();
        for (int i=0; i<argument.length(); i++) {
            char c = argument.charAt(i);
            if (c == '\\') {
                if (++i < argument.length()) {
                    c = argument.charAt(i);
                    int value = Character.digit(c, 10);
                    if (value > -1) {
                        subst.append("$").append(value);
                    } else {
                        subst.append(c);
                    }
                } else {
                    // XXX - should throw an exception instead?
                    subst.append('\\');
                }
            } else {
                subst.append(c);
            }
        }
        

        // Do the substitution
        Substitution s = 
            new Perl5Substitution(subst.toString(), 
                                  Perl5Substitution.INTERPOLATE_ALL);
        return Util.substitute(matcher,
                               getCompiledPattern(options),
                               s,
                               input,
                               getSubsOptions(options));
    }
*****************************************
*****************************************
SATD id: 23		Size: 23
    protected String replaceReferences(String source) {
        Vector v = reg.getGroups(source);
        
        result.setLength(0);
        for (int i=0; i<to.length; i++) {
            if (to[i] == '\\') {
                if (++i < to.length) {
                    int value = Character.digit(to[i], 10);
                    if (value > -1) {
                        result.append((String) v.elementAt(value));
                    } else {
                        result.append(to[i]);
                    }
                } else {
                    // XXX - should throw an exception instead?
                    result.append('\\');
                }
            } else {
                result.append(to[i]);
            }
        }
        return result.toString();
    }
*****************************************
*****************************************
SATD id: 24		Size: 7
    // Temporary - until we figure a better API
    /** EXPERIMENTAL WILL_CHANGE
     *
     */
//    public Hashtable getProcessedFiles() {
//        return processedFiles;
//    }
*****************************************
*****************************************
SATD id: 25		Size: 15
    public Hashtable getProperties() {
        Hashtable propertiesCopy = new Hashtable();

        Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            Object value = properties.get(name);
            propertiesCopy.put(name, value);
        }

        // There is a better way to save the context. This shouldn't
        // delegate to next, it's for backward compat only.

        return propertiesCopy;
    }
*****************************************
*****************************************
SATD id: 26		Size: 116
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
*****************************************
*****************************************
SATD id: 27		Size: 47
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
*****************************************
*****************************************
SATD id: 28		Size: 58
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
*****************************************
*****************************************
SATD id: 29		Size: 50
    private void scandir(File dir, String vpath, boolean fast) {
        String[] newfiles = dir.list();

        if (newfiles == null) {
            /*
             * two reasons are mentioned in the API docs for File.list
             * (1) dir is not a directory. This is impossible as
             *     we wouldn't get here in this case.
             * (2) an IO error occurred (why doesn't it throw an exception 
             *     then???)
             */
            throw new BuildException("IO error scanning directory"
                                     + dir.getAbsolutePath());
        }

        for (int i = 0; i < newfiles.length; i++) {
            String name = vpath+newfiles[i];
            File   file = new File(dir,newfiles[i]);
            if (file.isDirectory()) {
                if (isIncluded(name)) {
                    if (!isExcluded(name)) {
                        dirsIncluded.addElement(name);
                        if (fast) {
                            scandir(file, name+File.separator, fast);
                        }
                    } else {
                        dirsExcluded.addElement(name);
                    }
                } else {
                    dirsNotIncluded.addElement(name);
                    if (fast && couldHoldIncluded(name)) {
                        scandir(file, name+File.separator, fast);
                    }
                }
                if (!fast) {
                    scandir(file, name+File.separator, fast);
                }
            } else if (file.isFile()) {
                if (isIncluded(name)) {
                    if (!isExcluded(name)) {
                        filesIncluded.addElement(name);
                    } else {
                        filesExcluded.addElement(name);
                    }
                } else {
                    filesNotIncluded.addElement(name);
                }
            }
        }
    }
*****************************************
*****************************************
SATD id: 3		Size: 20
    // XXX: (Jon Skeet) The comment "if it hasn't been done already" may
    // not be strictly true. wrapper.maybeConfigure() won't configure the same
    // attributes/text more than once, but it may well add the children again,
    // unless I've missed something.
    /**
     * Configures this task - if it hasn't been done already.
     * If the task has been invalidated, it is replaced with an 
     * UnknownElement task which uses the new definition in the project.
     *
     * @exception BuildException if the task cannot be configured.
     */
    public void maybeConfigure() throws BuildException {
        if (!invalid) {
            if (wrapper != null) {
                wrapper.maybeConfigure(project);
            }
        } else {
            getReplacement();
        }
    }
*****************************************
*****************************************
SATD id: 30		Size: 130
    protected ArchiveState getResourcesToAdd(FileSet[] filesets,
                                             File zipFile,
                                             boolean needsUpdate)
        throws BuildException {

        Resource[][] initialResources = grabResources(filesets);
        if (isEmpty(initialResources)) {
            if (needsUpdate && doUpdate) {
                /*
                 * This is a rather hairy case.
                 *
                 * One of our subclasses knows that we need to update the
                 * archive, but at the same time, there are no resources
                 * known to us that would need to be added.  Only the
                 * subclass seems to know what's going on.
                 *
                 * This happens if <jar> detects that the manifest has changed,
                 * for example.  The manifest is not part of any resources
                 * because of our support for inline <manifest>s.
                 *
                 * If we invoke createEmptyZip like Ant 1.5.2 did,
                 * we'll loose all stuff that has been in the original
                 * archive (bugzilla report 17780).
                 */
                return new ArchiveState(true, initialResources);
            }

            if (emptyBehavior.equals("skip")) {
                if (doUpdate) {
                    log(archiveType + " archive " + zipFile 
                        + " not updated because no new files were included.", 
                        Project.MSG_VERBOSE);
                } else {
                    log("Warning: skipping " + archiveType + " archive " 
                        + zipFile + " because no files were included.", 
                        Project.MSG_WARN);
                }
            } else if (emptyBehavior.equals("fail")) {
                throw new BuildException("Cannot create " + archiveType
                                         + " archive " + zipFile +
                                         ": no files were included.", 
                                         getLocation());
            } else {
                // Create.
                createEmptyZip(zipFile);
            }
            return new ArchiveState(needsUpdate, initialResources);
        }

        // initialResources is not empty

        if (!zipFile.exists()) {
            return new ArchiveState(true, initialResources);
        }

        if (needsUpdate && !doUpdate) {
            // we are recreating the archive, need all resources
            return new ArchiveState(true, initialResources);
        }

        Resource[][] newerResources = new Resource[filesets.length][];

        for (int i = 0; i < filesets.length; i++) {
            if (!(fileset instanceof ZipFileSet) 
                || ((ZipFileSet) fileset).getSrc() == null) {
                File base = filesets[i].getDir(getProject());
            
                for (int j = 0; j < initialResources[i].length; j++) {
                    File resourceAsFile = 
                        fileUtils.resolveFile(base, 
                                              initialResources[i][j].getName());
                    if (resourceAsFile.equals(zipFile)) {
                        throw new BuildException("A zip file cannot include "
                                                 + "itself", getLocation());
                    }
                }
            }
        }

        for (int i = 0; i < filesets.length; i++) {
            if (initialResources[i].length == 0) {
                newerResources[i] = new Resource[] {};
                continue;
            }
            
            FileNameMapper myMapper = new IdentityMapper();
            if (filesets[i] instanceof ZipFileSet) {
                ZipFileSet zfs = (ZipFileSet) filesets[i];
                if (zfs.getFullpath() != null
                    && !zfs.getFullpath().equals("") ) {
                    // in this case all files from origin map to
                    // the fullPath attribute of the zipfileset at
                    // destination
                    MergingMapper fm = new MergingMapper();
                    fm.setTo(zfs.getFullpath());
                    myMapper = fm;

                } else if (zfs.getPrefix() != null 
                           && !zfs.getPrefix().equals("")) {
                    GlobPatternMapper gm=new GlobPatternMapper();
                    gm.setFrom("*");
                    String prefix = zfs.getPrefix();
                    if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                        prefix += "/";
                    }
                    gm.setTo(prefix + "*");
                    myMapper = gm;
                }
            }
            newerResources[i] = 
                ResourceUtils.selectOutOfDateSources(this,
                                                     initialResources[i],
                                                     myMapper,
                                                     getZipScanner());
            needsUpdate = needsUpdate || (newerResources[i].length > 0);

            if (needsUpdate && !doUpdate) {
                // we will return initialResources anyway, no reason
                // to scan further.
                break;
            }
        }

        if (needsUpdate && !doUpdate) {
            // we are recreating the archive, need all resources
            return new ArchiveState(true, initialResources);
        }
        
        return new ArchiveState(needsUpdate, newerResources);
    }
*****************************************
*****************************************
SATD id: 31		Size: 63
    public int executeAsForked(CommandlineJava commandline,
                               ExecuteWatchdog watchdog) throws BuildException {
        // if not set, auto-create the ClassPath from the project
        createClasspath();

        // not sure whether this test is needed but cost nothing to put.
        // hope it will be reviewed by anybody competent
        if (getClasspath().toString().length() > 0) {
            createJvmarg(commandline).setValue("-classpath");
            createJvmarg(commandline).setValue(getClasspath().toString());
        }

        if (getOutputFile() != null) {
            // having a space between the file and its path causes commandline 
            // to add quotes around the argument thus making JDepend not taking 
            // it into account. Thus we split it in two
            commandline.createArgument().setValue("-file");
            commandline.createArgument().setValue(_outputFile.getPath());
            // we have to find a cleaner way to put this output
        }

        // This is deprecated - use classespath in the future
        String[] sourcesPath = getSourcespath().list();
        for (int i = 0; i < sourcesPath.length; i++) {
            File f = new File(sourcesPath[i]);

            // not necessary as JDepend would fail, but why loose some time?
            if (!f.exists() || !f.isDirectory()) {
                throw new BuildException("\"" + f.getPath() + "\" does not " 
                    + "represent a valid directory. JDepend would fail.");
            }
            commandline.createArgument().setValue(f.getPath());
        }

        // This is the new way - use classespath - code is the same for now
        String[] classesPath = getClassespath().list();
        for (int i = 0; i < classesPath.length; i++) {
            File f = new File(classesPath[i]);
            // not necessary as JDepend would fail, but why loose some time?
            if (!f.exists() || !f.isDirectory()) {
                throw new BuildException("\"" + f.getPath() + "\" does not "
                        + "represent a valid directory. JDepend would fail.");
            }
            commandline.createArgument().setValue(f.getPath());
        }

        Execute execute = new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN), watchdog);
        execute.setCommandline(commandline.getCommandline());
        if (getDir() != null) {
            execute.setWorkingDirectory(getDir());
            execute.setAntRun(getProject());
        }

        if (getOutputFile() != null) {
            log("Output to be stored in " + getOutputFile().getPath());
        }
        log(commandline.describeCommand(), Project.MSG_VERBOSE);
        try {
            return execute.execute();
        } catch (IOException e) {
            throw new BuildException("Process fork failed.", e, getLocation());
        }
    }
*****************************************
*****************************************
SATD id: 32		Size: 96
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
*****************************************
*****************************************
SATD id: 33		Size: 6
    private void parseEmacsOutput( BufferedReader reader )
        throws IOException
    {
        // This may change, if we add advanced parsing capabilities.
        parseStandardOutput( reader );
    }
*****************************************
*****************************************
SATD id: 34		Size: 470
    public void execute() throws BuildException {
        if ("javadoc2".equals(getTaskType())) {
            log("Warning: the task name <javadoc2> is deprecated. Use <javadoc> instead.", Project.MSG_WARN);
        }

        // Whether *this VM* is 1.4+ (but also check executable != null).
        boolean javadoc4 =
            !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2) &&
            !JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3);

        Vector packagesToDoc = new Vector();
        Path sourceDirs = new Path(getProject());

        if (packageList != null && sourcePath == null) {
            String msg = "sourcePath attribute must be set when "
                + "specifying packagelist.";
            throw new BuildException(msg);
        }

        if (sourcePath != null) {
            sourceDirs.addExisting(sourcePath);
        }

        parsePackages(packagesToDoc, sourceDirs);

        if (packagesToDoc.size() != 0 && sourceDirs.size() == 0) {
            String msg = "sourcePath attribute must be set when "
                + "specifying package names.";
            throw new BuildException(msg);
        }

        Vector sourceFilesToDoc = (Vector) sourceFiles.clone();
        addFileSets(sourceFilesToDoc);

        if (packageList == null && packagesToDoc.size() == 0
            && sourceFilesToDoc.size() == 0) {
            throw new BuildException("No source files and no packages have "
                                     + "been specified.");
        }

        log("Generating Javadoc", Project.MSG_INFO);

        Commandline toExecute = (Commandline) cmd.clone();
        if (executable != null) {
            toExecute.setExecutable(executable);
        } else {
            toExecute.setExecutable(JavaEnvUtils.getJdkExecutable("javadoc"));
        }

        // ------------------------------------------ general javadoc arguments
        if (doctitle != null) {
            toExecute.createArgument().setValue("-doctitle");
            toExecute.createArgument().setValue(expand(doctitle.getText()));
        }
        if (header != null) {
            toExecute.createArgument().setValue("-header");
            toExecute.createArgument().setValue(expand(header.getText()));
        }
        if (footer != null) {
            toExecute.createArgument().setValue("-footer");
            toExecute.createArgument().setValue(expand(footer.getText()));
        }
        if (bottom != null) {
            toExecute.createArgument().setValue("-bottom");
            toExecute.createArgument().setValue(expand(bottom.getText()));
        }

        if (classpath == null) {
            classpath = (new Path(getProject())).concatSystemClasspath("last");
        } else {
            classpath = classpath.concatSystemClasspath("ignore");
        }

        if (classpath.size() > 0) {
            toExecute.createArgument().setValue("-classpath");
            toExecute.createArgument().setPath(classpath);
        }
        if (sourceDirs.size() > 0) {
            toExecute.createArgument().setValue("-sourcepath");
            toExecute.createArgument().setPath(sourceDirs);
        }

        if (version && doclet == null) {
            toExecute.createArgument().setValue("-version");
        }
        if (author && doclet == null) {
            toExecute.createArgument().setValue("-author");
        }

        if (doclet == null && destDir == null) {
            throw new BuildException("destdir attribute must be set!");
        }

        // ---------------------------- javadoc2 arguments for default doclet

        if (doclet != null) {
            if (doclet.getName() == null) {
                throw new BuildException("The doclet name must be "
                                         + "specified.", getLocation());
            } else {
                toExecute.createArgument().setValue("-doclet");
                toExecute.createArgument().setValue(doclet.getName());
                if (doclet.getPath() != null) {
                    Path docletPath
                        = doclet.getPath().concatSystemClasspath("ignore");
                    if (docletPath.size() != 0) {
                        toExecute.createArgument().setValue("-docletpath");
                        toExecute.createArgument().setPath(docletPath);
                    }
                }
                for (Enumeration e = doclet.getParams();
                     e.hasMoreElements();) {
                    DocletParam param = (DocletParam) e.nextElement();
                    if (param.getName() == null) {
                        throw new BuildException("Doclet parameters must "
                                                 + "have a name");
                    }

                    toExecute.createArgument().setValue(param.getName());
                    if (param.getValue() != null) {
                        toExecute.createArgument()
                            .setValue(param.getValue());
                    }
                }
            }
        }
        Path bcp = new Path(getProject());
        if (bootclasspath != null) {
            bcp.append(bootclasspath);
        }
        bcp = bcp.concatSystemBootClasspath("ignore");
        if (bcp.size() > 0) {
            toExecute.createArgument().setValue("-bootclasspath");
            toExecute.createArgument().setPath(bcp);
        }

        // add the links arguments
        if (links.size() != 0) {
            for (Enumeration e = links.elements(); e.hasMoreElements();) {
                LinkArgument la = (LinkArgument) e.nextElement();

                if (la.getHref() == null || la.getHref().length() == 0) {
                    log("No href was given for the link - skipping",
                        Project.MSG_VERBOSE);
                    continue;
                }
                String link = null;
                if (la.shouldResolveLink()) {
                    File hrefAsFile =
                        getProject().resolveFile(la.getHref());
                    if (hrefAsFile.exists()) {
                        try {
                            link = FILE_UTILS.getFileURL(hrefAsFile)
                                .toExternalForm();
                        } catch (MalformedURLException ex) {
                            // should be impossible
                            log("Warning: link location was invalid "
                                + hrefAsFile, Project.MSG_WARN);
                        }
                    }
                }
                if (link == null) {
                    // is the href a valid URL
                    try {
                        URL base = new URL("file://.");
                        new URL(base, la.getHref());
                        link = la.getHref();
                    } catch (MalformedURLException mue) {
                        // ok - just skip
                        log("Link href \"" + la.getHref()
                            + "\" is not a valid url - skipping link",
                            Project.MSG_WARN);
                        continue;
                    }
                }

                if (la.isLinkOffline()) {
                    File packageListLocation = la.getPackagelistLoc();
                    if (packageListLocation == null) {
                        throw new BuildException("The package list"
                                                 + " location for link "
                                                 + la.getHref()
                                                 + " must be provided "
                                                 + "because the link is "
                                                 + "offline");
                    }
                    File packageListFile =
                        new File(packageListLocation, "package-list");
                    if (packageListFile.exists()) {
                        try {
                            String packageListURL =
                                FILE_UTILS.getFileURL(packageListLocation)
                                .toExternalForm();
                            toExecute.createArgument()
                                .setValue("-linkoffline");
                            toExecute.createArgument()
                                .setValue(link);
                            toExecute.createArgument()
                                .setValue(packageListURL);
                        } catch (MalformedURLException ex) {
                            log("Warning: Package list location was "
                                + "invalid " + packageListLocation,
                                Project.MSG_WARN);
                        }
                    } else {
                        log("Warning: No package list was found at "
                            + packageListLocation, Project.MSG_VERBOSE);
                    }
                } else {
                    toExecute.createArgument().setValue("-link");
                    toExecute.createArgument().setValue(link);
                }
            }
        }

        // add the single group arguments
        // Javadoc 1.2 rules:
        //   Multiple -group args allowed.
        //   Each arg includes 3 strings: -group [name] [packagelist].
        //   Elements in [packagelist] are colon-delimited.
        //   An element in [packagelist] may end with the * wildcard.

        // Ant javadoc task rules for group attribute:
        //   Args are comma-delimited.
        //   Each arg is 2 space-delimited strings.
        //   E.g., group="XSLT_Packages org.apache.xalan.xslt*,
        //                XPath_Packages org.apache.xalan.xpath*"
        if (group != null) {
            StringTokenizer tok = new StringTokenizer(group, ",", false);
            while (tok.hasMoreTokens()) {
                String grp = tok.nextToken().trim();
                int space = grp.indexOf(" ");
                if (space > 0) {
                    String name = grp.substring(0, space);
                    String pkgList = grp.substring(space + 1);
                    toExecute.createArgument().setValue("-group");
                    toExecute.createArgument().setValue(name);
                    toExecute.createArgument().setValue(pkgList);
                }
            }
        }

        // add the group arguments
        if (groups.size() != 0) {
            for (Enumeration e = groups.elements(); e.hasMoreElements();) {
                GroupArgument ga = (GroupArgument) e.nextElement();
                String title = ga.getTitle();
                String packages = ga.getPackages();
                if (title == null || packages == null) {
                    throw new BuildException("The title and packages must "
                                             + "be specified for group "
                                             + "elements.");
                }
                toExecute.createArgument().setValue("-group");
                toExecute.createArgument().setValue(expand(title));
                toExecute.createArgument().setValue(packages);
            }
        }

        // JavaDoc 1.4 parameters
        if (javadoc4 || executable != null) {
            for (Enumeration e = tags.elements(); e.hasMoreElements();) {
                Object element = e.nextElement();
                if (element instanceof TagArgument) {
                    TagArgument ta = (TagArgument) element;
                    File tagDir = ta.getDir(getProject());
                    if (tagDir == null) {
                        // The tag element is not used as a fileset,
                        // but specifies the tag directly.
                        toExecute.createArgument().setValue ("-tag");
                        toExecute.createArgument()
                            .setValue (ta.getParameter());
                    } else {
                        // The tag element is used as a
                        // fileset. Parse all the files and create
                        // -tag arguments.
                        DirectoryScanner tagDefScanner =
                            ta.getDirectoryScanner(getProject());
                        String[] files = tagDefScanner.getIncludedFiles();
                        for (int i = 0; i < files.length; i++) {
                            File tagDefFile = new File(tagDir, files[i]);
                            try {
                                BufferedReader in
                                    = new BufferedReader(
                                          new FileReader(tagDefFile)
                                          );
                                String line = null;
                                while ((line = in.readLine()) != null) {
                                    toExecute.createArgument()
                                        .setValue("-tag");
                                    toExecute.createArgument()
                                        .setValue(line);
                                }
                                in.close();
                            } catch (IOException ioe) {
                                throw new BuildException("Couldn't read "
                                    + " tag file from "
                                    + tagDefFile.getAbsolutePath(), ioe);
                            }
                        }
                    }
                } else {
                    ExtensionInfo tagletInfo = (ExtensionInfo) element;
                    toExecute.createArgument().setValue("-taglet");
                    toExecute.createArgument().setValue(tagletInfo
                                                        .getName());
                    if (tagletInfo.getPath() != null) {
                        Path tagletPath = tagletInfo.getPath()
                            .concatSystemClasspath("ignore");
                        if (tagletPath.size() != 0) {
                            toExecute.createArgument()
                                .setValue("-tagletpath");
                            toExecute.createArgument().setPath(tagletPath);
                        }
                    }
                }
            }

            if (source != null) {
                toExecute.createArgument().setValue("-source");
                toExecute.createArgument().setValue(source);
            }

            if (linksource && doclet == null) {
                toExecute.createArgument().setValue("-linksource");
            }
            if (breakiterator && doclet == null) {
                toExecute.createArgument().setValue("-breakiterator");
            }
            if (noqualifier != null && doclet == null) {
                toExecute.createArgument().setValue("-noqualifier");
                toExecute.createArgument().setValue(noqualifier);
            }
        } else {
            // Not 1.4+.
            if (!tags.isEmpty()) {
                log("-tag and -taglet options not supported on Javadoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (source != null) {
                log("-source option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (linksource) {
                log("-linksource option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (breakiterator) {
                log("-breakiterator option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
            if (noqualifier != null) {
                log("-noqualifier option not supported on JavaDoc < 1.4",
                     Project.MSG_VERBOSE);
            }
        }
        // Javadoc 1.2/1.3 parameters:
        if (!javadoc4 || executable != null) {
            if (old) {
                toExecute.createArgument().setValue("-1.1");
            }
        } else {
            if (old) {
                log("Javadoc 1.4 doesn't support the -1.1 switch anymore",
                    Project.MSG_WARN);
            }
        }

        File tmpList = null;
        PrintWriter srcListWriter = null;
        try {

            /**
             * Write sourcefiles and package names to a temporary file
             * if requested.
             */
            if (useExternalFile) {
                if (tmpList == null) {
                    tmpList = FILE_UTILS.createTempFile("javadoc", "", null);
                    tmpList.deleteOnExit();
                    toExecute.createArgument()
                        .setValue("@" + tmpList.getAbsolutePath());
                }
                srcListWriter = new PrintWriter(
                                    new FileWriter(tmpList.getAbsolutePath(),
                                                   true));
            }

            Enumeration e = packagesToDoc.elements();
            while (e.hasMoreElements()) {
                String packageName = (String) e.nextElement();
                if (useExternalFile) {
                    srcListWriter.println(packageName);
                } else {
                    toExecute.createArgument().setValue(packageName);
                }
            }

            e = sourceFilesToDoc.elements();
            while (e.hasMoreElements()) {
                SourceFile sf = (SourceFile) e.nextElement();
                String sourceFileName = sf.getFile().getAbsolutePath();
                if (useExternalFile) {
                    // XXX what is the following doing? should it run if !javadoc4 && executable != null?
                    if (javadoc4 && sourceFileName.indexOf(" ") > -1) {
                        String name =
                            sourceFileName.replace(File.separatorChar, '/');
                        srcListWriter.println("\"" + name + "\"");
                    } else {
                        srcListWriter.println(sourceFileName);
                    }
                } else {
                    toExecute.createArgument().setValue(sourceFileName);
                }
            }

        } catch (IOException e) {
            tmpList.delete();
            throw new BuildException("Error creating temporary file",
                                     e, getLocation());
        } finally {
            if (srcListWriter != null) {
                srcListWriter.close();
            }
        }

        if (packageList != null) {
            toExecute.createArgument().setValue("@" + packageList);
        }
        log(toExecute.describeCommand(), Project.MSG_VERBOSE);

        log("Javadoc execution", Project.MSG_INFO);

        JavadocOutputStream out = new JavadocOutputStream(Project.MSG_INFO);
        JavadocOutputStream err = new JavadocOutputStream(Project.MSG_WARN);
        Execute exe = new Execute(new PumpStreamHandler(out, err));
        exe.setAntRun(getProject());

        /*
         * No reason to change the working directory as all filenames and
         * path components have been resolved already.
         *
         * Avoid problems with command line length in some environments.
         */
        exe.setWorkingDirectory(null);
        try {
            exe.setCommandline(toExecute.getCommandline());
            int ret = exe.execute();
            if (ret != 0 && failOnError) {
                throw new BuildException("Javadoc returned " + ret,
                                         getLocation());
            }
        } catch (IOException e) {
            throw new BuildException("Javadoc failed: " + e, e, getLocation());
        } finally {
            if (tmpList != null) {
                tmpList.delete();
                tmpList = null;
            }

            out.logFlush();
            err.logFlush();
            try {
                out.close();
                err.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
*****************************************
*****************************************
SATD id: 35		Size: 44
    private static String[] getProcEnvCommand() {
        if (Os.isFamily("os/2")) {
            // OS/2 - use same mechanism as Windows 2000
            String[] cmd = {"cmd", "/c", "set" };
            return cmd;
        } else if (Os.isFamily("windows")) {
            // Determine if we're running under XP/2000/NT or 98/95
            if (!Os.isFamily("win9x")) {
                // Windows XP/2000/NT
                String[] cmd = {"cmd", "/c", "set" };
                return cmd;
            } else {
                // Windows 98/95
                String[] cmd = {"command.com", "/c", "set" };
                return cmd;
            }
        } else if (Os.isFamily("z/os") || Os.isFamily("unix")) {
            // On most systems one could use: /bin/sh -c env

            // Some systems have /bin/env, others /usr/bin/env, just try
            String[] cmd = new String[1];
            if (new File("/bin/env").canRead()) {
                cmd[0] = "/bin/env";
            } else if (new File("/usr/bin/env").canRead()) {
                cmd[0] = "/usr/bin/env";
            } else {
                // rely on PATH
                cmd[0] = "env";
            }
            return cmd;
        } else if (Os.isFamily("netware") || Os.isFamily("os/400")) {
            // rely on PATH
            String[] cmd = {"env"};
            return cmd;
        } else if (Os.isFamily("openvms")) {
            String[] cmd = {"show", "logical"};
            return cmd;
        } else {
            // MAC OS 9 and previous
            //TODO: I have no idea how to get it, someone must fix it
            String[] cmd = null;
            return cmd;
        }
    }
*****************************************
*****************************************
SATD id: 36		Size: 64
    //TODO: nothing appears to read this but is set using a public setter.
    private boolean spawn = false;


    /** Controls whether the VM is used to launch commands, where possible. */
    private boolean useVMLauncher = true;

    private static String antWorkingDirectory = System.getProperty("user.dir");
    private static CommandLauncher vmLauncher = null;
    private static CommandLauncher shellLauncher = null;
    private static Vector procEnvironment = null;

    /** Used to destroy processes when the VM exits. */
    private static ProcessDestroyer processDestroyer = new ProcessDestroyer();

    /*
     * Builds a command launcher for the OS and JVM we are running under.
     */
    static {
        // Try using a JDK 1.3 launcher
        try {
            if (!Os.isFamily("os/2")) {
                vmLauncher = new Java13CommandLauncher();
            }
        } catch (NoSuchMethodException exc) {
            // Ignore and keep trying
        }
        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
            // Mac
            shellLauncher = new MacCommandLauncher(new CommandLauncher());
        } else if (Os.isFamily("os/2")) {
            // OS/2
            shellLauncher = new OS2CommandLauncher(new CommandLauncher());
        } else if (Os.isFamily("windows")) {

            CommandLauncher baseLauncher = new CommandLauncher();

            if (!Os.isFamily("win9x")) {
                // Windows XP/2000/NT
                shellLauncher = new WinNTCommandLauncher(baseLauncher);
            } else {
                // Windows 98/95 - need to use an auxiliary script
                shellLauncher
                    = new ScriptCommandLauncher("bin/antRun.bat", baseLauncher);
            }
        } else if (Os.isFamily("netware")) {

            CommandLauncher baseLauncher = new CommandLauncher();

            shellLauncher
                = new PerlScriptCommandLauncher("bin/antRun.pl", baseLauncher);
        } else if (Os.isFamily("openvms")) {
            // OpenVMS
            try {
                shellLauncher = new VmsCommandLauncher();
            } catch (NoSuchMethodException exc) {
            // Ignore and keep trying
            }
        } else {
            // Generic
            shellLauncher = new ScriptCommandLauncher("bin/antRun",
                new CommandLauncher());
        }
    }
*****************************************
*****************************************
SATD id: 37		Size: 12
    protected boolean isNmtoken(String s) {
        final int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            // XXX - we are committing CombiningChar and Extender here
            if (!Character.isLetterOrDigit(c)
                && c != '.' && c != '-' && c != '_' && c != ':') {
                return false;
            }
        }
        return true;
    }
*****************************************
*****************************************
SATD id: 38		Size: 116
    private void printElementDecl(PrintWriter out, String name, Class element) 
        throws BuildException {

        if (visited.containsKey(name)) {
            return;
        }
        visited.put(name, "");

        IntrospectionHelper ih = null;
        try {
            ih = IntrospectionHelper.getHelper(element);
        } catch (Throwable t) {
            /*
             * XXX - failed to load the class properly.
             *
             * should we print a warning here?
             */
            return;
        }

        StringBuffer sb = new StringBuffer("<!ELEMENT ");
        sb.append(name).append(" ");

        if (org.apache.tools.ant.types.Reference.class.equals(element)) {
            sb.append("EMPTY>").append(lSep);
            sb.append("<!ATTLIST ").append(name);
            sb.append(lSep).append("          id ID #IMPLIED");
            sb.append(lSep).append("          refid IDREF #IMPLIED");
            sb.append(">").append(lSep);
            out.println(sb);
            return;
        }

        Vector v = new Vector();
        if (ih.supportsCharacters()) {
            v.addElement("#PCDATA");
        }

        Enumeration enum = ih.getNestedElements();
        while (enum.hasMoreElements()) {
            v.addElement((String) enum.nextElement());
        }

        if (v.isEmpty()) {
            sb.append("EMPTY");
        } else {
            sb.append("(");
            for (int i=0; i<v.size(); i++) {
                if (i != 0) {
                    sb.append(" | ");
                }
                sb.append(v.elementAt(i));
            }
            sb.append(")");
            if (v.size() > 1 || !v.elementAt(0).equals("#PCDATA")) {
                sb.append("*");
            }
        }
        sb.append(">");
        out.println(sb);

        sb.setLength(0);
        sb.append("<!ATTLIST ").append(name);
        sb.append(lSep).append("          id ID #IMPLIED");
        
        enum = ih.getAttributes();
        while (enum.hasMoreElements()) {
            String attrName = (String) enum.nextElement();
            if ("id".equals(attrName)) continue;
            
            sb.append(lSep).append("          ").append(attrName).append(" ");
            Class type = ih.getAttributeType(attrName);
            if (type.equals(java.lang.Boolean.class) || 
                type.equals(java.lang.Boolean.TYPE)) {
                sb.append("%boolean; ");
            } else if (org.apache.tools.ant.types.Reference.class.isAssignableFrom(type)) { 
                sb.append("IDREF ");
            } else if (org.apache.tools.ant.types.EnumeratedAttribute.class.isAssignableFrom(type)) {
                try {
                    EnumeratedAttribute ea = 
                        (EnumeratedAttribute)type.newInstance();
                    String[] values = ea.getValues();
                    if (values == null
                        || values.length == 0
                        || !areNmtokens(values)) {
                        sb.append("CDATA ");
                    } else {
                        sb.append("(");
                        for (int i=0; i < values.length; i++) {
                            if (i != 0) {
                                sb.append(" | ");
                            }
                            sb.append(values[i]);
                        }
                        sb.append(") ");
                    }
                } catch (InstantiationException ie) {
                    sb.append("CDATA ");
                } catch (IllegalAccessException ie) {
                    sb.append("CDATA ");
                }
            } else {
                sb.append("CDATA ");
            }
            sb.append("#IMPLIED");
        }
        sb.append(">").append(lSep);
        out.println(sb);

        for (int i=0; i<v.size(); i++) {
            String nestedName = (String) v.elementAt(i);
            if (!"#PCDATA".equals(nestedName)) {
                printElementDecl(out, nestedName, ih.getElementType(nestedName));
            }
        }
    }
*****************************************
*****************************************
SATD id: 39		Size: 87
    public void execute(Project project) throws BuildException {
        final String classname = javaCommand.getExecutable();

        AntClassLoader loader = null;
        try {
            if (sysProperties != null) {
                sysProperties.setSystem();
            }
            Class target = null;
            if (classpath == null) {
                target = Class.forName(classname);
            } else {
                loader = project.createClassLoader(classpath);
                loader.setParent(project.getCoreLoader());
                loader.setParentFirst(false);
                loader.addJavaLibraries();
                loader.setIsolated(true);
                loader.setThreadContextLoader();
                loader.forceLoadClass(classname);
                target = Class.forName(classname, true, loader);
            }
            main = target.getMethod("main", new Class[] {String[].class});
            if (main == null) {
                throw new BuildException("Could not find main() method in "
                                         + classname);
            }
            if ((main.getModifiers() & Modifier.STATIC) == 0) {
                throw new BuildException("main() method in " + classname
                    + " is not declared static");
            }
            if (timeout == null) {
                run();
            } else {
                thread = new Thread(this, "ExecuteJava");
                Task currentThreadTask
                    = project.getThreadTask(Thread.currentThread());
                // XXX is the following really necessary? it is in the same thread group...
                project.registerThreadTask(thread, currentThreadTask);
                // if we run into a timeout, the run-away thread shall not
                // make the VM run forever - if no timeout occurs, Ant's
                // main thread will still be there to let the new thread
                // finish
                thread.setDaemon(true);
                Watchdog w = new Watchdog(timeout.longValue());
                w.addTimeoutObserver(this);
                synchronized (this) {
                    thread.start();
                    w.start();
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    if (timedOut) {
                        project.log("Timeout: sub-process interrupted",
                                    Project.MSG_WARN);
                    } else {
                        thread = null;
                        w.stop();
                    }
                }
            }
            if (caught != null) {
                throw caught;
            }
        } catch (ClassNotFoundException e) {
            throw new BuildException("Could not find " + classname + "."
                                     + " Make sure you have it in your"
                                     + " classpath");
        } catch (SecurityException e) {
            throw e;
        } catch (ThreadDeath e) {
            // XXX could perhaps also call thread.stop(); not sure if anyone cares
            throw e;
        } catch (Throwable e) {
            throw new BuildException(e);
        } finally {
            if (loader != null) {
                loader.resetThreadContextLoader();
                loader.cleanup();
                loader = null;
            }
            if (sysProperties != null) {
                sysProperties.restoreSystem();
            }
        }
    }
*****************************************
*****************************************
SATD id: 4		Size: 67
    protected void visit(Folder starteamFolder, java.io.File targetFolder ) 
	throws BuildException 
    {
	try {
	    Hashtable localFiles = getLocalFiles(targetFolder);

	    // If we have been told to create the working folders
	    if (createDirs) {
		// Create if it doesn't exist
		if (!targetFolder.exists()) {
		    targetFolder.mkdir();
		}
	    }
	    // For all Files in this folder, we need to check 
	    // if there have been modifications.

	    Item[] files = starteamFolder.getItems("File");
	    for (int i = 0; i < files.length; i++) {
		File eachFile = (File) files[i];
		String filename = eachFile.getName();
		java.io.File localFile = new java.io.File(targetFolder, filename);
		localFiles.remove(localFile.toString());
		
		int fileStatus = (eachFile.getStatus());

		// We try to update the status once to give StarTeam another chance.
		if (fileStatus == Status.MERGE || fileStatus == Status.UNKNOWN) {
		    eachFile.updateStatus(true, true);
		}

		// If the file is current then skip it.
		// If the file doesn't pass the include/exclude tests, skip it. 
		if (fileStatus == Status.CURRENT || !shouldProcess(filename)) {
		    continue;
		}

		// Check out anything else.
		// Just a note: StarTeam has a status for NEW which implies that there 
		// is an item  on your local machine that is not in the repository. 
		// These are the items that show up as NOT IN VIEW in the Starteam GUI.
		// One would think that we would want to perhaps checkin the NEW items
		// (not in all cases! - Steve Cohen 15 Dec 2001)
		// Unfortunately, the sdk doesn't really work, and we can't actually see
		// anything with a status of NEW. That is why we can just check out 
		// everything here without worrying about losing anything.

		log("Checking Out: " + (localFile.toString()), Project.MSG_INFO);
		eachFile.checkoutTo(localFile, Item.LockType.
				    UNCHANGED, true, true, true);
	    }

	    // Now we recursively call this method on all sub folders in this folder.
	    Folder[] subFolders = starteamFolder.getSubFolders();
	    for (int i = 0; i < subFolders.length; i++) {
		localFiles.remove(subFolders[i].getPath());
		visit(subFolders[i], 
		      new java.io.File(targetFolder, subFolders[i].getName()));
	    }

	    // Delete all folders or files that are not in Starteam.
	    if (this.deleteUncontrolled && !localFiles.isEmpty()) {
		delete(localFiles);
	    }
	} catch (IOException e) {
	    throw new BuildException(e);
	}
    }
*****************************************
*****************************************
SATD id: 40		Size: 108
    private void doJikesCompile() throws BuildException {
	project.log("Using jikes compiler",project.MSG_VERBOSE);
	String classpath = getCompileClasspath();
	Vector argList = new Vector();

	if (deprecation == true)
	    argList.addElement("-deprecation");
	
	// We want all output on stdout to make
	// parsing easier
	argList.addElement("-Xstdout");
	
	argList.addElement("-d");
	argList.addElement(destDir.getAbsolutePath());
	argList.addElement("-classpath");
	// Jikes has no option for source-path so we
	// will add it to classpath.
	// XXX is this correct?
	argList.addElement(classpath+File.pathSeparator +
			   srcDir.getAbsolutePath());
	if (debug) {
	    argList.addElement("-g");
	}
	if (optimize) {
	    argList.addElement("-O");
	}

       /**
        * XXX
        * Perhaps we shouldn't use properties for these
        * two options (emacs mode and warnings),
        * but include it in the javac directive?
        */

       /**
        * Jikes has the nice feature to print error
        * messages in a form readable by emacs, so
        * that emcas can directly set the cursor
        * to the place, where the error occured.
        */
       boolean emacsMode = false;
       String emacsProperty = project.getProperty("build.compiler.emacs");
       if (emacsProperty != null &&
           (emacsProperty.equalsIgnoreCase("on") ||
            emacsProperty.equalsIgnoreCase("true"))
           ) {
           emacsMode = true;
       }

       /**
        * Jikes issues more warnings that javac, for
        * example, when you have files in your classpath
        * that don't exist. As this is often the case, these
        * warning can be pretty annoying.
        */
       boolean warnings = true;
       String warningsProperty = project.getProperty("build.compiler.warnings");
       if (warningsProperty != null &&
           (warningsProperty.equalsIgnoreCase("off") ||
            warningsProperty.equalsIgnoreCase("false"))
           ) {
           warnings = false;
       }

       if (emacsMode)
           argList.addElement("+E");

       if (!warnings)
           argList.addElement("-nowarn");
	
	project.log("Compilation args: " + argList.toString(),
		    project.MSG_VERBOSE);
	
	String[] args = new String[argList.size() + compileList.size()];
	int counter = 0; 
	
	for (int i = 0; i < argList.size(); i++) {
	    args[i] = (String)argList.elementAt(i);
	    counter++;
	}

	// XXX
	// should be using system independent line feed!
	
	StringBuffer niceSourceList = new StringBuffer("Files to be compiled:"
						       + "\r\n");

	Enumeration enum = compileList.elements();
	while (enum.hasMoreElements()) {
	    args[counter] = (String)enum.nextElement();
	    niceSourceList.append("    " + args[counter] + "\r\n");
	    counter++;
	}

	project.log(niceSourceList.toString(), project.MSG_VERBOSE);

	// XXX
	// provide the compiler a different message sink - namely our own
	
	JikesOutputParser jop = new JikesOutputParser(project,emacsMode);
	
	Jikes compiler = new Jikes(jop,"jikes");
	compiler.compile(args);
	if (jop.getErrorFlag()) {
	    String msg = "Compile failed, messages should have been provided.";
	    throw new BuildException(msg);
	}
    }
*****************************************
*****************************************
SATD id: 41		Size: 80
    public void execute() {
        try {
            // Gump friendly - don't mess with the core loader if only classpath
            if ("only".equals(project.getProperty("build.sysclasspath"))
                && (name == null || SYSTEM_LOADER_REF.equals(name))) {
                log("Changing the system loader is disabled "
                    + "by build.sysclasspath=only", Project.MSG_WARN);
                return;
            }

            String loaderName = (name == null) ? SYSTEM_LOADER_REF : name;

            Object obj = project.getReference(loaderName);
            if (reset) {
                // Are any other references held ? Can we 'close' the loader
                // so it removes the locks on jars ?
                obj = null; // a new one will be created.
            }

            // XXX maybe use reflection to addPathElement (other patterns ?)
            if (obj != null && !(obj instanceof AntClassLoader)) {
                log("Referenced object is not an AntClassLoader",
                        Project.MSG_ERR);
                return;
            }

            AntClassLoader acl = (AntClassLoader) obj;

            if (acl == null) {
                // Construct a new class loader
                Object parent = null;
                if (parentName != null) {
                    parent = project.getReference(parentName);
                    if (!(parent instanceof ClassLoader)) {
                        parent = null;
                    }
                }
                // TODO: allow user to request the system or no parent
                if (parent == null) {
                    parent = this.getClass().getClassLoader();
                }

                if (name == null) {
                    // The core loader must be reverse
                    //reverse=true;
                }
                project.log("Setting parent loader " + name + " "
                    + parent + " " + parentFirst, Project.MSG_DEBUG);

                // The param is "parentFirst"
                acl = new AntClassLoader((ClassLoader) parent,
                        project, classpath, parentFirst);

                project.addReference(loaderName, acl);

                if (name == null) {
                    // This allows the core loader to load optional tasks
                    // without delegating
                    acl.addLoaderPackageRoot("org.apache.tools.ant.taskdefs.optional");
                    project.setCoreLoader(acl);
                }
            }
            if (classpath != null) {
                String[] list = classpath.list();
                for (int i = 0; i < list.length; i++) {
                    File f = new File(list[i]);
                    if (f.exists()) {
                        acl.addPathElement(f.getAbsolutePath());
                        log("Adding to class loader " +  acl + " " + f.getAbsolutePath(),
                                Project.MSG_DEBUG);
                    }
                }
            }

            // XXX add exceptions

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*****************************************
*****************************************
SATD id: 42		Size: 151
	protected void runItem(Server s, com.starbase.starteam.Project p, View v, Type t, Folder f, Item item)
	{

		// Get descriptors for this item type.
		Property p1 = getPrimaryDescriptor(t);
		Property p2 = getSecondaryDescriptor(t);

		// Time to filter...
		String pName = (String)item.get(p1.getName());
		boolean includeIt = false;
		boolean excludeIt = false;

		// See if it fits any includes.
		if (getIncludes()!=null)
		{
			StringTokenizer inStr = new StringTokenizer(getIncludes(), " ");
			while (inStr.hasMoreTokens())
			{
				if (match(inStr.nextToken(), pName))
				{
					includeIt = true;
				}
			}
		}

		// See if it fits any excludes.
		if (getExcludes()!=null)
		{
			StringTokenizer exStr = new StringTokenizer(getExcludes(), " ");
			while (exStr.hasMoreTokens())
			{
				if (match(exStr.nextToken(), pName))
				{
					excludeIt = true;
				}
			}
		}

		// Don't check it out if
		// (a) It fits no include filters
		// (b) It fits an exclude filter
		if (!includeIt | excludeIt)
		{
			return;
		}

		// VERBOSE MODE ONLY
		if (getVerboseAsBoolean())
		{
			// Show folder only if changed.
			boolean bShowHeader = true;
			if (f != prevFolder)
			{
				// We want to display the folder the same way you would
				// enter it on the command line ... so we remove the 
				// View name (which is also the name of the root folder,
				// and therefore shows up at the start of the path).
				String strFolder = f.getFolderHierarchy();
				int i = strFolder.indexOf(delim);
				if (i >= 0)
				{
					strFolder = strFolder.substring(i+1);
				}
				System.out.println("            Folder: \"" + strFolder + "\"");
				prevFolder = f;
			}
			else
				bShowHeader	= false;

			// If we displayed the project, view, item type, or folder,
			// then show the list of relevant item properties.
			if (bShowHeader)
			{
				System.out.print("                Item");
				System.out.print(",\t" + p1.getDisplayName());
				if (p2 != null)
				{
					System.out.print(",\t" + p2.getDisplayName());
				}
				System.out.println("");
			}

			// Finally, show the Item properties ...

			// Always show the ItemID.
			System.out.print("                " + item.getItemID());

			// Show the primary descriptor.
			// There should always be one.
			System.out.print(",\t" + formatForDisplay(p1, item.get(p1.getName())));

			// Show the secondary descriptor, if there is one.
			// Some item types have one, some don't.
			if (p2 != null)
			{
				System.out.print(",\t" + formatForDisplay(p2, item.get(p2.getName())));
			}

			// Show if the file is locked.
			int locker = item.getLocker();
			if (locker>-1)
			{
				System.out.println(",\tLocked by " + locker);
			}
			else
			{
				System.out.println(",\tNot locked");
			}
		}
		// END VERBOSE ONLY

		// Check it out; also ugly.

		// Change the item to be checked out to a StarTeam File.
		com.starbase.starteam.File remote = (com.starbase.starteam.File)item;

		// Create a variable dirName that contains the name of the StarTeam folder that is the root folder in this view.
		// Get the default path to the current view.
		String dirName = v.getDefaultPath();
		// Settle on "/" as the default path separator for this purpose only.
		dirName = dirName.replace('\\', '/');
		// Take the StarTeam folder name furthest down in the hierarchy.
		dirName = dirName.substring(dirName.lastIndexOf("/", dirName.length() - 2) + 1, dirName.length() - 1);

		// Replace the projectName in the file's absolute path to the viewName.
		// This eventually makes the target of a checkout operation equal to:
		// targetFolder + dirName + [subfolders] + itemName
		StringTokenizer pathTokenizer = new StringTokenizer(item.getParentFolder().getFolderHierarchy(), delim);
		String localName = delim;
		String currentToken = null;
		while (pathTokenizer.hasMoreTokens())
		{
			currentToken = pathTokenizer.nextToken();
			if (currentToken.equals(getProjectName()))
			{
				currentToken = dirName;
			}
			localName += currentToken + delim;
		}
		// Create a reference to the local target file using the format listed above.
		java.io.File local = new java.io.File(getTargetFolder() + localName + item.get(p1.getName()));
		try
		{
			remote.checkoutTo(local, Item.LockType.UNCHANGED, false, true, true);
		}
		catch (Throwable e)
		{
			project.log("    " + e.getMessage());
		}
		checkedOut++;
	}
*****************************************
*****************************************
SATD id: 43		Size: 11
    public synchronized InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        //I can't get my head around this; is encoding treatment needed here?
        return
            //new oata.util.ReaderInputStream(new InputStreamReader(
            new ByteArrayInputStream(getContent().getBytes())
            //, encoding), encoding)
            ;
    }
*****************************************
*****************************************
SATD id: 44		Size: 77
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
*****************************************
*****************************************
SATD id: 45		Size: 74
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
*****************************************
*****************************************
SATD id: 46		Size: 0
*****************************************
*****************************************
SATD id: 47		Size: 74
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
*****************************************
*****************************************
SATD id: 48		Size: 25
    public String substitute(String input, String argument, int options)
        throws BuildException {
        Vector v = matcher.getGroups(input, options);
        
        StringBuffer result = new StringBuffer();
        char[] to = argument.toCharArray();
        for (int i=0; i<to.length; i++) {
            if (to[i] == '\\') {
                if (++i < to.length) {
                    int value = Character.digit(to[i], 10);
                    if (value > -1) {
                        result.append((String) v.elementAt(value));
                    } else {
                        result.append(to[i]);
                    }
                } else {
                    // XXX - should throw an exception instead?
                    result.append('\\');
                }
            } else {
                result.append(to[i]);
            }
        }
        return result.toString();
    }
*****************************************
*****************************************
SATD id: 49		Size: 23
    protected String replaceReferences(String source) {
        Vector v = reg.getGroups(source);
        
        result.setLength(0);
        for (int i=0; i<to.length; i++) {
            if (to[i] == '\\') {
                if (++i < to.length) {
                    int value = Character.digit(to[i], 10);
                    if (value > -1) {
                        result.append((String) v.elementAt(value));
                    } else {
                        result.append(to[i]);
                    }
                } else {
                    // XXX - should throw an exception instead?
                    result.append('\\');
                }
            } else {
                result.append(to[i]);
            }
        }
        return result.toString();
    }
*****************************************
*****************************************
SATD id: 5		Size: 49
    public void testSelectionBehaviour() {
        FilenameSelector s;
        String results;

        try {
            makeBed();

            s = (FilenameSelector)getInstance();
            s.setName("no match possible");
            results = selectionString(s);
            assertEquals("FFFFFFFFFFFF", results);

            s = (FilenameSelector)getInstance();
            s.setName("*.gz");
            results = selectionString(s);
            // This is turned off temporarily. There appears to be a bug
            // in SelectorUtils.matchPattern() where it is recursive on
            // Windows even if no ** is in pattern.
            //assertEquals("FFFTFFFFFFFF", results); // Unix
            // vs
            //assertEquals("FFFTFFFFTFFF", results); // Windows

            s = (FilenameSelector)getInstance();
            s.setName("**/*.gz");
            s.setNegate(true);
            results = selectionString(s);
            assertEquals("TTTFTTTTFTTT", results);

            s = (FilenameSelector)getInstance();
            s.setName("**/*.GZ");
            s.setCasesensitive(false);
            results = selectionString(s);
            assertEquals("FFFTFFFFTFFF", results);

            s = (FilenameSelector)getInstance();
            Parameter param1 = new Parameter();
            param1.setName("name");
            param1.setValue("**/*.bz2");
            Parameter[] params = {param1};
            s.setParameters(params);
            results = selectionString(s);
            assertEquals("FFTFFFFFFTTF", results);

        }
        finally {
            cleanupBed();
        }

    }
*****************************************
*****************************************
SATD id: 50		Size: 29
    public RegexpMatcher newRegexpMatcher(Project p)
        throws BuildException {
        String systemDefault = null;
        if (p == null) {
            systemDefault = System.getProperty("ant.regexp.regexpimpl");
        } else {
            systemDefault = (String) p.getProperties().get("ant.regexp.regexpimpl");
        }
        
        if (systemDefault != null) {
            return createInstance(systemDefault);
            // XXX     should we silently catch possible exceptions and try to 
            //         load a different implementation?
        }

        try {
            return createInstance("org.apache.tools.ant.util.regexp.Jdk14RegexpMatcher");
        } catch (BuildException be) {}
        
        try {
            return createInstance("org.apache.tools.ant.util.regexp.JakartaOroMatcher");
        } catch (BuildException be) {}
        
        try {
            return createInstance("org.apache.tools.ant.util.regexp.JakartaRegexpMatcher");
        } catch (BuildException be) {}

        throw new BuildException("No supported regular expression matcher found");
   }
*****************************************
*****************************************
SATD id: 51		Size: 19
    // Should move to a separate public class - and have API to add
    // listeners, etc.
    private static class AntRefTable extends Hashtable {
        Project project;
        public AntRefTable(Project project) {
            super();
            this.project=project;
        }

        public Object get(Object key) {
            //System.out.println("AntRefTable.get " + key);
            Object o=super.get(key);
            if( o instanceof UnknownElement ) {
                ((UnknownElement)o).maybeConfigure();
                o=((UnknownElement)o).getTask();
            }
            return o;
        }
    }
*****************************************
*****************************************
SATD id: 52		Size: 88
    public void testTokenizer() {
        String[] s = Commandline.translateCommandline("1 2 3");
        assertEquals("Simple case", 3, s.length);
        for (int i=0; i<3; i++) {
            assertEquals(""+(i+1), s[i]);
        }
        
        s = Commandline.translateCommandline("");
        assertEquals("empty string", 0, s.length);

        s = Commandline.translateCommandline(null);
        assertEquals("null", 0, s.length);

        s = Commandline.translateCommandline("1 \'2\' 3");
        assertEquals("Simple case with single quotes", 3, s.length);
        assertEquals("Single quotes have been stripped", "2", s[1]);

        s = Commandline.translateCommandline("1 \"2\" 3");
        assertEquals("Simple case with double quotes", 3, s.length);
        assertEquals("Double quotes have been stripped", "2", s[1]);

        s = Commandline.translateCommandline("1 \"2 3\" 4");
        assertEquals("Case with double quotes and whitespace", 3, s.length);
        assertEquals("Double quotes stripped, space included", "2 3", s[1]);
        
        s = Commandline.translateCommandline("1 \"2\'3\" 4");
        assertEquals("Case with double quotes around single quote", 3, s.length);
        assertEquals("Double quotes stripped, single quote included", "2\'3",
                     s[1]);

        s = Commandline.translateCommandline("1 \'2 3\' 4");
        assertEquals("Case with single quotes and whitespace", 3, s.length);
        assertEquals("Single quotes stripped, space included", "2 3", s[1]);
        
        s = Commandline.translateCommandline("1 \'2\"3\' 4");
        assertEquals("Case with single quotes around double quote", 3, s.length);
        assertEquals("Single quotes stripped, double quote included", "2\"3",
                     s[1]);

        // \ doesn't have a special meaning anymore - this is different from
        // what the Unix sh does but causes a lot of problems on DOS
        // based platforms otherwise
        s = Commandline.translateCommandline("1 2\\ 3 4");
        assertEquals("case with quoted whitespace", 4, s.length);
        assertEquals("backslash included", "2\\", s[1]);

        // "" should become a single empty argument, same for ''
        // PR 5906
        s = Commandline.translateCommandline("\"\" a");
        assertEquals("Doublequoted null arg prepend", 2, s.length);
        assertEquals("Doublequoted null arg prepend", "", s[0]);
        assertEquals("Doublequoted null arg prepend", "a", s[1]);
        s = Commandline.translateCommandline("a \"\"");
        assertEquals("Doublequoted null arg append", 2, s.length);
        assertEquals("Doublequoted null arg append", "a", s[0]);
        assertEquals("Doublequoted null arg append", "", s[1]);
        s = Commandline.translateCommandline("\"\"");
        assertEquals("Doublequoted null arg", 1, s.length);
        assertEquals("Doublequoted null arg", "", s[0]);

        s = Commandline.translateCommandline("\'\' a");
        assertEquals("Singlequoted null arg prepend", 2, s.length);
        assertEquals("Singlequoted null arg prepend", "", s[0]);
        assertEquals("Singlequoted null arg prepend", "a", s[1]);
        s = Commandline.translateCommandline("a \'\'");
        assertEquals("Singlequoted null arg append", 2, s.length);
        assertEquals("Singlequoted null arg append", "a", s[0]);
        assertEquals("Singlequoted null arg append", "", s[1]);
        s = Commandline.translateCommandline("\'\'");
        assertEquals("Singlequoted null arg", 1, s.length);
        assertEquals("Singlequoted null arg", "", s[0]);

        // now to the expected failures
        
        try {
            s = Commandline.translateCommandline("a \'b c");
            fail("unbalanced single quotes undetected");
        } catch (BuildException be) {
            assertEquals("unbalanced quotes in a \'b c", be.getMessage());
        }

        try {
            s = Commandline.translateCommandline("a \"b c");
            fail("unbalanced double quotes undetected");
        } catch (BuildException be) {
            assertEquals("unbalanced quotes in a \"b c", be.getMessage());
        }
    }
*****************************************
*****************************************
SATD id: 53		Size: 8
				public void run(){
					try {
						process.waitFor();
					} catch(InterruptedException e){
						// not very nice but will do the job
						fail("process interrupted in thread");
					}
				}
*****************************************
*****************************************
SATD id: 54		Size: 4
        // provide public visibility
        public String resolveFile(String file) {
            return super.resolveFile(file);
        }
*****************************************
*****************************************
SATD id: 55		Size: 28
    // not used, but public so theoretically must remain for BC?
    public void assertEqualContent(File expect, File result)
        throws AssertionFailedError, IOException {
        if (!result.exists()) {
            fail("Expected file "+result+" doesn\'t exist");
        }

        InputStream inExpect = null;
        InputStream inResult = null;
        try {
            inExpect = new BufferedInputStream(new FileInputStream(expect));
            inResult = new BufferedInputStream(new FileInputStream(result));

            int expectedByte = inExpect.read();
            while (expectedByte != -1) {
                assertEquals(expectedByte, inResult.read());
                expectedByte = inExpect.read();
            }
            assertEquals("End of file", -1, inResult.read());
        } finally {
            if (inResult != null) {
                inResult.close();
            }
            if (inExpect != null) {
                inExpect.close();
            }
        }
    }
*****************************************
*****************************************
SATD id: 56		Size: 3
    private String getKey(String key) {
        return key; // XXX what is this for?
    }
*****************************************
*****************************************
SATD id: 57		Size: 3
    private String getKey(String key) {
        return key; // XXX what is this for?
    }
*****************************************
*****************************************
SATD id: 58		Size: 8
    public void test5() { 
        executeTarget("test5");
        java.io.File f = new java.io.File("src/etc/testcases/taskdefs.tmp");
        if (!f.exists() || !f.isDirectory()) { 
            fail("Copy failed");
        }
        // We keep this, so we have something to delete in later tests :-)
    }
*****************************************
*****************************************
SATD id: 59		Size: 19
    // *************  copied from ConcatTest  *************

    // ------------------------------------------------------
    //   Helper methods - should be in BuildFileTest
    // -----------------------------------------------------

    private String getFileString(String filename)
        throws IOException
    {
        Reader r = null;
        try {
            r = new FileReader(getProject().resolveFile(filename));
            return  FileUtils.newFileUtils().readFully(r);
        }
        finally {
            try {r.close();} catch (Throwable ignore) {}
        }

    }
*****************************************
*****************************************
SATD id: 6		Size: 52
    private static boolean matchPatternStart(String pattern, String str) {
        // When str starts with a File.separator, pattern has to start with a
        // File.separator.
        // When pattern starts with a File.separator, str has to start with a
        // File.separator.
        if (str.startsWith(File.separator) !=
            pattern.startsWith(File.separator)) {
            return false;
        }

        Vector patDirs = new Vector();
        StringTokenizer st = new StringTokenizer(pattern,File.separator);
        while (st.hasMoreTokens()) {
            patDirs.addElement(st.nextToken());
        }

        Vector strDirs = new Vector();
        st = new StringTokenizer(str,File.separator);
        while (st.hasMoreTokens()) {
            strDirs.addElement(st.nextToken());
        }

        int patIdxStart = 0;
        int patIdxEnd   = patDirs.size()-1;
        int strIdxStart = 0;
        int strIdxEnd   = strDirs.size()-1;

        // up to first '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            String patDir = (String)patDirs.elementAt(patIdxStart);
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir,(String)strDirs.elementAt(strIdxStart))) {
                return false;
            }
            patIdxStart++;
            strIdxStart++;
        }

        if (strIdxStart > strIdxEnd) {
            // String is exhausted
            return true;
        } else if (patIdxStart > patIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else {
            // pattern now holds ** while string is not exhausted
            // this will generate false positives but we can live with that.
            return true;
        }
    }
*****************************************
*****************************************
SATD id: 60		Size: 96

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
*****************************************
*****************************************
SATD id: 61		Size: 54
    protected void sendFile(FTPClient ftp, String dir, String filename)
        throws IOException, BuildException {
        InputStream instream = null;

        try {
            // XXX - why not simply new File(dir, filename)?
            File file = task.getProject().resolveFile(new File(dir, filename).getPath());

            if (task.isNewer() && isUpToDate(ftp, file, resolveFile(filename))) {
                return;
            }

            if (task.isVerbose()) {
                task.log("transferring " + file.getAbsolutePath());
            }

            instream = new BufferedInputStream(new FileInputStream(file));

            createParents(ftp, filename);

            ftp.storeFile(resolveFile(filename), instream);

            boolean success = FTPReply.isPositiveCompletion(ftp.getReplyCode());

            if (!success) {
                String s = "could not put file: " + ftp.getReplyString();

                if (task.isSkipFailedTransfers()) {
                    task.log(s, Project.MSG_WARN);
                    skipped++;
                } else {
                    throw new BuildException(s);
                }

            } else {
                // see if we should issue a chmod command
                if (task.getChmod() != null) {
                    doSiteCommand(ftp, "chmod " + task.getChmod() + " "
                                  + resolveFile(filename));
                }
                task.log("File " + file.getAbsolutePath() + " copied to " + task.getServer(),
                         Project.MSG_VERBOSE);
                transferred++;
            }
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (IOException ex) {
                    // ignore it
                }
            }
        }
    }
*****************************************
*****************************************
SATD id: 62		Size: 60
    protected void sendFile(FTPClient ftp, String dir, String filename)
        throws IOException, BuildException
    {
        InputStream instream = null;
        try
        {
            // XXX - why not simply new File(dir, filename)?
            File file = project.resolveFile(new File(dir, filename).getPath());

            if (newerOnly && isUpToDate(ftp, file, resolveFile(filename))) {
                return;
            }

            if (verbose)
            {
                log("transferring " + file.getAbsolutePath());
            }

            instream = new BufferedInputStream(new FileInputStream(file));

            createParents(ftp, filename);

            ftp.storeFile(resolveFile(filename), instream);
            boolean success=FTPReply.isPositiveCompletion(ftp.getReplyCode());
            if (!success)
            {
                String s="could not put file: " + ftp.getReplyString();
                if(skipFailedTransfers==true) {
                    log(s,Project.MSG_WARN);
                    skipped++;
                }
                else {
                    throw new BuildException(s);
                }

            }
            else {
                if (chmod != null) { // see if we should issue a chmod command
                    doSiteCommand(ftp,"chmod " + chmod + " " + filename);
                }
                log("File " + file.getAbsolutePath() + " copied to " + server,
                    Project.MSG_VERBOSE);
                transferred++;
            }
        }
        finally
        {
            if (instream != null)
            {
                try
                {
                    instream.close();
                }
                catch(IOException ex)
                {
                    // ignore it
                }
            }
        }
    }
*****************************************
*****************************************
SATD id: 63		Size: 58
	protected void runType(Server s, com.starbase.starteam.Project p, View v, Type t)
	{

		// This is ugly; checking for the root folder.
		Folder f = v.getRootFolder();
		if (!(getFolderName()==null))
		{
			if (getFolderName().equals("\\") || getFolderName().equals("/"))
			{
				setFolderName(null);
			}
			else
			{
				f = StarTeamFinder.findFolder(v.getRootFolder(), getFolderName());
			}
		}

		if (getVerboseAsBoolean() && !(getFolderName()==null))
		{
			project.log("Found " + getProjectName() + delim + getViewName() + delim + getFolderName() + delim + "\n");
		}

		// For performance reasons, it is important to pre-fetch all the
		// properties we'll need for all the items we'll be searching.

		// We always display the ItemID (OBJECT_ID) and primary descriptor.
		int nProperties = 2;

		// We'll need this item type's primary descriptor.
		Property p1 = getPrimaryDescriptor(t);

		// Does this item type have a secondary descriptor?
		// If so, we'll need it.
		Property p2 = getSecondaryDescriptor(t);
		if (p2 != null)
		{
			nProperties++;
		}

		// Now, build an array of the property names.
		String[] strNames = new String[nProperties];
		int iProperty = 0;
		strNames[iProperty++] = s.getPropertyNames().OBJECT_ID;
		strNames[iProperty++] = p1.getName();
		if (p2 != null)
		{
			strNames[iProperty++] = p2.getName();
		}

		// Pre-fetch the item properties and cache them.
		f.populateNow(t.getName(), strNames, -1);

		// Now, search for items in the selected folder.
		runFolder(s, p, v, t, f);

		// Free up the memory used by the cached items.
		f.discardItems(t.getName(), -1);
	}
*****************************************
*****************************************
SATD id: 64		Size: 96
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
*****************************************
*****************************************
SATD id: 65		Size: 44
    public void writeJar(File jarfile, Hashtable files) throws BuildException{
        JarOutputStream jarStream = null;
        Iterator entryIterator = null;
        String entryName = null;
        File entryFile = null;

        try {
            /* If the jarfile already exists then whack it and recreate it.
             * Should probably think of a more elegant way to handle this
             * so that in case of errors we don't leave people worse off
             * than when we started =)
             */
            if (jarfile.exists()) jarfile.delete();
            jarfile.getParentFile().mkdirs();
            jarfile.createNewFile();
            
            // Create the streams necessary to write the jarfile
            jarStream = new JarOutputStream(new FileOutputStream(jarfile));
            jarStream.setMethod(JarOutputStream.DEFLATED);
            
            // Loop through all the class files found and add them to the jar
            entryIterator = files.keySet().iterator();
            while (entryIterator.hasNext()) {
                entryName = (String) entryIterator.next();
                entryFile = (File) files.get(entryName);
                
                this.log("adding file '" + entryName + "'",
                         Project.MSG_VERBOSE);

                addFileToJar(jarStream,
                             new FileInputStream(entryFile),
                             entryName);
            }
            // All done.  Close the jar stream.
            jarStream.close();
        }
        catch(IOException ioe) {
            String msg = "IOException while processing ejb-jar file '"
                + jarfile.toString()
                + "'. Details: "
                + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
    } // end of writeJar
*****************************************
*****************************************
SATD id: 66		Size: 44
    public void writeJar(File jarfile, Hashtable files) throws BuildException{
        JarOutputStream jarStream = null;
        Iterator entryIterator = null;
        String entryName = null;
        File entryFile = null;

        try {
            /* If the jarfile already exists then whack it and recreate it.
             * Should probably think of a more elegant way to handle this
             * so that in case of errors we don't leave people worse off
             * than when we started =)
             */
            if (jarfile.exists()) jarfile.delete();
            jarfile.getParentFile().mkdirs();
            jarfile.createNewFile();
            
            // Create the streams necessary to write the jarfile
            jarStream = new JarOutputStream(new FileOutputStream(jarfile));
            jarStream.setMethod(JarOutputStream.DEFLATED);
            
            // Loop through all the class files found and add them to the jar
            entryIterator = files.keySet().iterator();
            while (entryIterator.hasNext()) {
                entryName = (String) entryIterator.next();
                entryFile = (File) files.get(entryName);
                
                this.log("adding file '" + entryName + "'",
                         Project.MSG_VERBOSE);

                addFileToJar(jarStream,
                             new FileInputStream(entryFile),
                             entryName);
            }
            // All done.  Close the jar stream.
            jarStream.close();
        }
        catch(IOException ioe) {
            String msg = "IOException while processing ejb-jar file '"
                + jarfile.toString()
                + "'. Details: "
                + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
    } // end of writeJar
*****************************************
*****************************************
SATD id: 67		Size: 87
    public void execute(Project project) throws BuildException {
        final String classname = javaCommand.getExecutable();

        AntClassLoader loader = null;
        try {
            if (sysProperties != null) {
                sysProperties.setSystem();
            }
            Class target = null;
            if (classpath == null) {
                target = Class.forName(classname);
            } else {
                loader = project.createClassLoader(classpath);
                loader.setParent(project.getCoreLoader());
                loader.setParentFirst(false);
                loader.addJavaLibraries();
                loader.setIsolated(true);
                loader.setThreadContextLoader();
                loader.forceLoadClass(classname);
                target = Class.forName(classname, true, loader);
            }
            main = target.getMethod("main", new Class[] {String[].class});
            if (main == null) {
                throw new BuildException("Could not find main() method in "
                                         + classname);
            }
            if ((main.getModifiers() & Modifier.STATIC) == 0) {
                throw new BuildException("main() method in " + classname
                    + " is not declared static");
            }
            if (timeout == null) {
                run();
            } else {
                thread = new Thread(this, "ExecuteJava");
                Task currentThreadTask
                    = project.getThreadTask(Thread.currentThread());
                // XXX is the following really necessary? it is in the same thread group...
                project.registerThreadTask(thread, currentThreadTask);
                // if we run into a timeout, the run-away thread shall not
                // make the VM run forever - if no timeout occurs, Ant's
                // main thread will still be there to let the new thread
                // finish
                thread.setDaemon(true);
                Watchdog w = new Watchdog(timeout.longValue());
                w.addTimeoutObserver(this);
                synchronized (this) {
                    thread.start();
                    w.start();
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    if (timedOut) {
                        project.log("Timeout: sub-process interrupted",
                                    Project.MSG_WARN);
                    } else {
                        thread = null;
                        w.stop();
                    }
                }
            }
            if (caught != null) {
                throw caught;
            }
        } catch (ClassNotFoundException e) {
            throw new BuildException("Could not find " + classname + "."
                                     + " Make sure you have it in your"
                                     + " classpath");
        } catch (SecurityException e) {
            throw e;
        } catch (ThreadDeath e) {
            // XXX could perhaps also call thread.stop(); not sure if anyone cares
            throw e;
        } catch (Throwable e) {
            throw new BuildException(e);
        } finally {
            if (loader != null) {
                loader.resetThreadContextLoader();
                loader.cleanup();
                loader = null;
            }
            if (sysProperties != null) {
                sysProperties.restoreSystem();
            }
        }
    }
*****************************************
*****************************************
SATD id: 68		Size: 95
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
*****************************************
*****************************************
SATD id: 69		Size: 45
    public void doDelayUpdateTest(int kind) {
        // no check for 1<=kind<=3 - only internal use therefore check it
        // while development

        // readable form of parameter kind
        String[] kinds = {"task", "target", "build"};

        // setup the "Ant project"
        MockProject project = new MockProject();
        File base  = new File("base");
        File file1 = new File("file1");
        File file2 = new File("file2");

        // setup the selector
        ModifiedSelector sel = new ModifiedSelector();
        sel.setProject(project);
        sel.setUpdate(true);
        sel.setDelayUpdate(true);
        // sorry - otherwise we will get a ClassCastException because the MockCache
        // is loaded by two different classloader ...
        sel.setClassLoader(this.getClass().getClassLoader());
        sel.addClasspath(testclasses);

        sel.setAlgorithmClass("org.apache.tools.ant.types.selectors.MockAlgorithm");
        sel.setCacheClass("org.apache.tools.ant.types.selectors.MockCache");
        sel.configure();

        // get the cache, so we can check our things
        MockCache cache = (MockCache)sel.getCache();

        // the test
        assertFalse("Cache must not be saved before 1st selection.", cache.saved);
        sel.isSelected(base, "file1", file1);
        assertFalse("Cache must not be saved after 1st selection.", cache.saved);
        sel.isSelected(base, "file2", file2);
        assertFalse("Cache must not be saved after 2nd selection.", cache.saved);
        switch (kind) {
            case 1 : project.fireTaskFinished();   break;
            case 2 : project.fireTargetFinished(); break;
            case 3 : project.fireBuildFinished();  break;
        }
        assertTrue("Cache must be saved after " + kinds[kind-1] + "Finished-Event.", cache.saved);

        // MockCache doesnt create a file - therefore no cleanup needed
    }
*****************************************
*****************************************
SATD id: 7		Size: 8
    private static String getNamespaceURI(Node n) {
        String uri = n.getNamespaceURI();
        if (uri == null) {
            // FIXME: Is "No Namespace is Empty Namespace" really OK?
            uri = "";
        }
        return uri;
    }
*****************************************
*****************************************
SATD id: 70		Size: 27
    private boolean isParentFirst(String resourceName) {
        // default to the global setting and then see
        // if this class belongs to a package which has been
        // designated to use a specific loader first (this one or the parent one)
        
        // XXX - shouldn't this always return false in isolated mode?
        
        boolean useParentFirst = parentFirst;

        for (Enumeration e = systemPackages.elements(); e.hasMoreElements();) {
            String packageName = (String)e.nextElement();
            if (resourceName.startsWith(packageName)) {
                useParentFirst = true;
                break;
            }
        }

        for (Enumeration e = loaderPackages.elements(); e.hasMoreElements();) {
            String packageName = (String)e.nextElement();
            if (resourceName.startsWith(packageName)) {
                useParentFirst = false;
                break;
            }
        }

        return useParentFirst;
    }
*****************************************
*****************************************
SATD id: 8		Size: 80
    private void populateFromCentralDirectory()
        throws IOException {
        positionAtCentralDirectory();

        byte[] cfh = new byte[CFH_LEN];

        byte[] signatureBytes = new byte[4];
        archive.readFully(signatureBytes);
        long sig = ZipLong.getValue(signatureBytes);
        final long cfh_sig = ZipLong.getValue(ZipOutputStream.CFH_SIG);
        while (sig == cfh_sig) {
            archive.readFully(cfh);
            int off = 0;
            ZipEntry ze = new ZipEntry();

            int versionMadeBy = ZipShort.getValue(cfh, off);
            off += 2;
            ze.setPlatform((versionMadeBy >> 8) & 0x0F);

            off += 4; // skip version info and general purpose byte

            ze.setMethod(ZipShort.getValue(cfh, off));
            off += 2;

            // FIXME this is actually not very cpu cycles friendly as we are converting from
            // dos to java while the underlying Sun implementation will convert
            // from java to dos time for internal storage...
            long time = dosToJavaTime(ZipLong.getValue(cfh, off));
            ze.setTime(time);
            off += 4;

            ze.setCrc(ZipLong.getValue(cfh, off));
            off += 4;

            ze.setCompressedSize(ZipLong.getValue(cfh, off));
            off += 4;

            ze.setSize(ZipLong.getValue(cfh, off));
            off += 4;

            int fileNameLen = ZipShort.getValue(cfh, off);
            off += 2;

            int extraLen = ZipShort.getValue(cfh, off);
            off += 2;

            int commentLen = ZipShort.getValue(cfh, off);
            off += 2;

            off += 2; // disk number

            ze.setInternalAttributes(ZipShort.getValue(cfh, off));
            off += 2;

            ze.setExternalAttributes(ZipLong.getValue(cfh, off));
            off += 4;

            byte[] fileName = new byte[fileNameLen];
            archive.readFully(fileName);
            ze.setName(getString(fileName));


            // LFH offset,
            OffsetEntry offset = new OffsetEntry();
            offset.headerOffset = ZipLong.getValue(cfh, off);
            // data offset will be filled later
            entries.put(ze, offset);

            nameMap.put(ze.getName(), ze);

            archive.skipBytes(extraLen);

            byte[] comment = new byte[commentLen];
            archive.readFully(comment);
            ze.setComment(getString(comment));

            archive.readFully(signatureBytes);
            sig = ZipLong.getValue(signatureBytes);
        }
    }
*****************************************
*****************************************
SATD id: 9		Size: 156
    public void execute() throws BuildException {
        sqlCommand = sqlCommand.trim();

        if (srcFile == null && sqlCommand.length()==0 && filesets.isEmpty()) { 
            if (transactions.size() == 0) {
                throw new BuildException("Source file or fileset, transactions or sql statement must be set!", location);
            }
        } else { 
            // deal with the filesets
            for (int i=0; i<filesets.size(); i++) {
                FileSet fs = (FileSet) filesets.elementAt(i);
                DirectoryScanner ds = fs.getDirectoryScanner(project);
                File srcDir = fs.getDir(project);

                String[] srcFiles = ds.getIncludedFiles();

                // Make a transaction for each file
                for ( int j=0 ; j<srcFiles.length ; j++ ) {
                    Transaction t = createTransaction();
                    t.setSrc(new File(srcDir, srcFiles[j]));
                }
            }

            // Make a transaction group for the outer command
            Transaction t = createTransaction();
            t.setSrc(srcFile);
            t.addText(sqlCommand);
        }

        if (driver == null) {
            throw new BuildException("Driver attribute must be set!", location);
        }
        if (userId == null) {
            throw new BuildException("User Id attribute must be set!", location);
        }
        if (password == null) {
            throw new BuildException("Password attribute must be set!", location);
        }
        if (url == null) {
            throw new BuildException("Url attribute must be set!", location);
        }
        if (srcFile != null && !srcFile.exists()) {
            throw new BuildException("Source file does not exist!", location);
        }
        Driver driverInstance = null;
        try {
            Class dc;
            if (classpath != null) {
                // check first that it is not already loaded otherwise
                // consecutive runs seems to end into an OutOfMemoryError
                // or it fails when there is a native library to load
                // several times.
                // this is far from being perfect but should work in most cases.
                synchronized (loaderMap){
                    if (caching){
                        loader = (AntClassLoader)loaderMap.get(driver);
                    }
                    if (loader == null){
                        log( "Loading " + driver + " using AntClassLoader with classpath " + classpath,
                             Project.MSG_VERBOSE );
                        loader = new AntClassLoader(project, classpath);
                        if (caching){
                            loaderMap.put(driver, loader);
                        }
                    } else {
                        log("Loading " + driver + " using a cached AntClassLoader.",
                                Project.MSG_VERBOSE);
                    }
                }
                dc = loader.loadClass(driver);
            }
            else {
                log("Loading " + driver + " using system loader.", Project.MSG_VERBOSE);
                dc = Class.forName(driver);
            }
            driverInstance = (Driver) dc.newInstance();
        }catch(ClassNotFoundException e){
            throw new BuildException("Class Not Found: JDBC driver " + driver + " could not be loaded", location);
        }catch(IllegalAccessException e){
            throw new BuildException("Illegal Access: JDBC driver " + driver + " could not be loaded", location);
        }catch(InstantiationException e) {
            throw new BuildException("Instantiation Exception: JDBC driver " + driver + " could not be loaded", location);
        }

        try{
            log("connecting to " + url, Project.MSG_VERBOSE );
            Properties info = new Properties();
            info.put("user", userId);
            info.put("password", password);
            conn = driverInstance.connect(url, info);

            if (conn == null) {
                // Driver doesn't understand the URL
                throw new SQLException("No suitable Driver for "+url);
            }

            if (!isValidRdbms(conn)) return;

            conn.setAutoCommit(autocommit);

            statement = conn.createStatement();

            
            PrintStream out = System.out;
            try {
                if (output != null) {
                    log("Opening PrintStream to output file " + output, Project.MSG_VERBOSE);
                    out = new PrintStream(new BufferedOutputStream(new FileOutputStream(output)));
                }
                        
                // Process all transactions
                for (Enumeration e = transactions.elements(); 
                     e.hasMoreElements();) {
                       
                    ((Transaction) e.nextElement()).runTransaction(out);
                    if (!autocommit) {
                        log("Commiting transaction", Project.MSG_VERBOSE);
                        conn.commit();
                    }
                }
            }
            finally {
                if (out != null && out != System.out) {
                    out.close();
                }
            }
        } catch(IOException e){
            if (!autocommit && conn != null && onError.equals("abort")) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            throw new BuildException(e, location);
        } catch(SQLException e){
            if (!autocommit && conn != null && onError.equals("abort")) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            throw new BuildException(e, location);
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {}
        }
          
        log(goodSql + " of " + totalSql + 
            " SQL statements executed successfully");
    }
*****************************************
*****************************************
