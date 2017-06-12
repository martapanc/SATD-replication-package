File path: src/main/org/apache/tools/ant/Task.java
Comment: XXX: (Jon Skeet) The comment "if it hasn't been done already" may
Initial commit id: 5afa736b
Final commit id: 13000c1a
   Bugs between [       4]:
5e4cb9c6e Fixed javadoc for Task#handleErrorFlush PR: 31153 Obtained from: Jayant Sai
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
9434671ef removing enum variable PR: 22345 Obtained from: Submitted by: Reviewed by:
987c943d0 Flush output of Java task when finished. Propagate indication of whether line is terminated or not through to project and tasks PR: 16555
   Bugs after [       0]:


Start block index: 238
End block index: 255
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
