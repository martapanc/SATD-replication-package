File path: src/main/org/apache/tools/ant/Main.java
Comment: XXX: (Jon Skeet) Any reason for writing a message and then using a bare
Initial commit id: f181fda3
Final commit id: 13000c1a
   Bugs between [      13]:
5f20b9914 microoptimizations.  PR 50716
f67b8649e reinstate prefix handling of property task at the expense of even more state in a thread-unfriendly class and with a new attribute on two tasks to make everybody happy.  PR 49373.
3f4cb68de resolve properties defined via -propertyfile against each other.  PR 18732
df121a6cf PR 47830 : implementation of the ProjectHelperRepository to make Ant able to choose a ProjectHelper, and some doc about it
99529fd6c At least try to log the real reason of an error.  PR 26086
94da71c76 change command line parser so that version is only printed once - and version as well as diagnostics methods know the current loglevel.  The former is PR 45695, the later useful for 45692.
7ea420955 normalize build file name. In essence, turn relative file names given on the command line into absolute ones.  Submitted by: Justin Vallon.  PR: 44323
b31c3d79f Add java1.5 system proxy support, as per bug 36174. I do not see any evidence of this working on my kde/linux system; I will check out and test on Windows.Other tests on other platforms welcome.
eaa5e6610 Make -projecthelp output display even in -quiet mode. PR: 17471
a38273bbf ant -p swallows (configuration) errors silently. PR:  27732
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
2a2141bcb Restore System.in, PR: 22066
b7c2f5f64 Add make's keep-going feature into ANT. PR: 21144 Obtained from: Alexey Solofnenko
   Bugs after [       2]:
47a3b5144 PR 56747 Document which options exit in help output, submitted by Ville Skyttä
834d0d120 configurable User-Agent for <get>. PR 55489. Submitted by André-John Mas

Start block index: 617
End block index: 651
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
