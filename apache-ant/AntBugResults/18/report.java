File path: proposal/sandbox/input/src/main/org/apache/tools/ant/Main.java
Comment: XXX: (Jon Skeet) Any reason for writing a message and then using a bare
Initial commit id: 2ace4090
Final commit id: 991a1c80
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 673
End block index: 707
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
