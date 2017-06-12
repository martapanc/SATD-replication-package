File path: src/main/org/apache/tools/ant/AntClassLoader.java
Comment: XXX - shouldn't this always return false in isolated mode?
Initial commit id: 1ef4d3ea
Final commit id: 13000c1a
   Bugs between [      14]:
021d52822 make AntClassLoader$ResourceEnumeration adhere to the Enumeration contract.  PR 51579
b33d33d75 embrace java.util.jar.  PR 48853
3e2cbab6b reduce performance loss in AntClassLoader from 1000% to about 50%.  PR 48853
44b6fcc18 use the same logic as getResource in getResourceAsStream.  PR 44103
329f6d356 report name of corrupt ZIP to System.err,  PR 47593
c8e91147e properly set CodeSource when loading classes.  PR 20174
26f846b83 allow access to parent.  PR 35436
8ebe808f7 override getResources in a new AntClassLoader subclass that will be used consistently when Ant is running on Java5+.  PR 46752
59f1d6794 don't add the same file more than once.  PR 45848.
758a6bcb9 Pr: 42259 inspired on optimization suggested by Tom Brus
44cf7f076 solve problem refering jars specfied by Class-Path attribute in manifest of a ant task jar file, when this ant task jar file is located in a directory with space. Bugzilla Report 37085.
8fdf27262 Impossible to use implicit classpath for <taskdef> when Ant core loader != Java application loader and Path.systemClassPath taken from ${java.class.path} PR: 30161 Submitted by: Jesse Glick (jglick at netbeans dot org)
55c133cec Generate proper file URLs in AntClassLoader#getResource, PR: 28060
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       1]:
9bd753321 deal more gracefully with non-JARs on the CLASSPATH - PR 53964

Start block index: 753
End block index: 779
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
