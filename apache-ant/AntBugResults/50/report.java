File path: src/main/org/apache/tools/ant/util/regexp/RegexpMatcherFactory.java
Comment: XXX     should we silently catch possible exceptions and try to
Initial commit id: ffea0a9f
Final commit id: 13000c1a
   Bugs between [       2]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
3396e7c32 Remove direct call to deprecated project, location, tasktype Task field, replaced by an accessor way into tasks. Remove too some unused variable declaration and some unused imports. PR: 22515 Submitted by: Emmanuel Feller ( Emmanuel dot Feller at free dot fr)
   Bugs after [       1]:
43844a7e6 PR 56748 Spelling fixes, submitted by Ville Skyttä

Start block index: 86
End block index: 114
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
