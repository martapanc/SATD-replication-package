File path: proposal/myrmidon/src/ant1compat/org/apache/tools/ant/OriginalAnt1Task.java
Comment: XXX: (Jon Skeet) The comment "if it hasn't been done already" may
Initial commit id: 9b85c069
Final commit id: 1bc1b473
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 238
End block index: 257
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
