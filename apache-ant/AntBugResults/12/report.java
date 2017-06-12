File path: proposal/embed/src/java/org/apache/tools/ant/helper/ProjectHelperImpl2.java
Comment: XXX ignore attributes in a different NS ( maybe store them ? )
Initial commit id: f3fc0f67
Final commit id: 6ecafdd4
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 567
End block index: 635
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
