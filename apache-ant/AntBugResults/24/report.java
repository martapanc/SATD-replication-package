File path: src/main/org/apache/tools/ant/ProjectHelper.java
Comment: Temporary - until we figure a better API
Initial commit id: 8d139ecb
Final commit id: a6d4ce25
   Bugs between [       6]:
6a02f4483 use the configured ProjectHelper to parse antlib descriptors, if possible,  PR 42208.
aa5fccfbe remove deprecated tag from ProjectHelper#configureProject() it has been used for a long time, and one cannot just replace it with the non-static parse method, without setting the "ant.projectHelper" reference. PR: 32668 Reported by:  Nell Gawor
476678d4a DynamicConfiguratorNS Initial code for dynamicConfiguratorNS. Change from patch the qualified name is given and not the prefix. PR: 28426
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
98c3a0ea1 [PATCH] Misspelling: s/occured/occurred/g PR: 27282 Obtained from: Jesse Glick
92ff24a7e add original exception to new buildexception for projectHelper#addLocationToBuildException( PR: 25777 Obtained from: Jesse Glick
   Bugs after [       5]:
51ce8fac7 fix for Target rewriting for nested "include" only works when "as" is specified, Bugzilla PR 54940
46c940785 adding if and unless namespaces allowing to put conditions on all tasks and nested elements. Code written by Peter Reilly. Bugzilla PR 43362.
bd52e7b9b allow targets to deal with missing extension points.  PR 49473.  Submitted by Danny Yates.
13941782f extension-point doesn't work with import.  PR 48804
df121a6cf PR 47830 : implementation of the ProjectHelperRepository to make Ant able to choose a ProjectHelper, and some doc about it

Start block index: 138
End block index: 144
    // Temporary - until we figure a better API
    /** EXPERIMENTAL WILL_CHANGE
     *
     */
//    public Hashtable getProcessedFiles() {
//        return processedFiles;
//    }
