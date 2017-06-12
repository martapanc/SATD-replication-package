File path: src/main/org/apache/tools/ant/helper/ProjectHelper2.java
Comment: XXX ignore attributes in a different NS ( maybe store them ? )
Initial commit id: 72b95057
Final commit id: 13000c1a
   Bugs between [      18]:
51ce8fac7 fix for Target rewriting for nested "include" only works when "as" is specified, Bugzilla PR 54940
3ef6daa0c according to the javadocs of JarURLConnection the separator is !/ not ! - this allows dealing with jars in directories that contain a ! in their name, as long as it is not at the end of the directory name.  PR 50007
bd52e7b9b allow targets to deal with missing extension points.  PR 49473.  Submitted by Danny Yates.
13941782f extension-point doesn't work with import.  PR 48804
2c468c212 don't warn about duplicate project names if importing the same URL twice.  PR 49162
6a02f4483 use the configured ProjectHelper to parse antlib descriptors, if possible,  PR 42208.
583cfae0e Do not set ant.file.{projectname} when the project name is not set in the <project> tag. (as discussed) Bugzilla report: 39920
dfaca18fc set target's name before the depends attribute - leads to more meaningful errors.  PR 37655
3d66f4ef9 Allways create qualified targets in imported build files PR: 28444
0a36bf20e check for same targets in a source file PR: 34566
0e1abe82d Set the location for the default target (the location will be the project tag) PR: 32267 Reported by: Yves Martin
1c48e016c Fix for execution of top level tasks getting delayed by targets. PR: 31487
476678d4a DynamicConfiguratorNS Initial code for dynamicConfiguratorNS. Change from patch the qualified name is given and not the prefix. PR: 28426
33ac6bc3c Add a Location member to oata.Target, with the appropriate settor & gettor. PR:  28599
8a5186ec7 fix if build file name has ../ or ./ PR:  26765 Reported by: Ian E. Gorman
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
0c43380c1 remove some deprecated methods and 1.1 holdovers Bugzilla: 22326
f3e3462d8 Add antlib xml functionality This is patch 7204 the fourth patch of the antlib + ns enhancement. The issues reported by Stephan will need be addressed, so the implementation may change before ant 1.6 is released. PR: 19897
   Bugs after [       1]:
6ea9dc102 When loading resources from jars it seems to be advisable to disable caching.  PR 54473.  Combining patches by René Krell and Antoine Levy-Lambert.

Start block index: 549
End block index: 617
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
