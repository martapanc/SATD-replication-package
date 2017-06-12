File path: src/main/org/apache/tools/ant/taskdefs/optional/junit/XalanExecutor.java
Comment: there's a convenient xsltc class version but data are
Initial commit id: d891e90f
Final commit id: 69eba07e
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 118
End block index: 126
    protected final String getXSLTCVersion(String procVersionClassName) 
        throws ClassNotFoundException {
        // there's a convenient xsltc class version but data are
        // private so use package information
        Class procVersion = Class.forName(procVersionClassName);
        Package pkg = procVersion.getPackage();
        return pkg.getName() + " " + pkg.getImplementationTitle() 
            + " " + pkg.getImplementationVersion();
    }
