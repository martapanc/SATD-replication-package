File path: src/main/org/apache/tools/ant/taskdefs/Javac.java
Comment: erhaps we shouldn't use properties for these
Initial commit id: bce31805
Final commit id: 4ca5683c
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

Start block index: 465
End block index: 572
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
