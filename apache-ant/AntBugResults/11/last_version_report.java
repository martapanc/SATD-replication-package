public void onStartElement(String uri, String tag, String qname, Attributes attrs,
                           AntXMLContext context) throws SAXParseException {
    String baseDir = null;
    boolean nameAttributeSet = false;

    Project project = context.getProject();
    // Set the location of the implicit target associated with the project tag
    context.getImplicitTarget().setLocation(new Location(context.getLocator()));

    /** TODO I really don't like this - the XML processor is still
     * too 'involved' in the processing. A better solution (IMO)
     * would be to create UE for Project and Target too, and
     * then process the tree and have Project/Target deal with
     * its attributes ( similar with Description ).
     *
     * If we eventually switch to ( or add support for ) DOM,
     * things will work smoothly - UE can be avoided almost completely
     * ( it could still be created on demand, for backward compatibility )
     */

    for (int i = 0; i < attrs.getLength(); i++) {
        String attrUri = attrs.getURI(i);
        if (attrUri != null && !attrUri.equals("") && !attrUri.equals(uri)) {
            continue; // Ignore attributes from unknown uris
        }
        String key = attrs.getLocalName(i);
        String value = attrs.getValue(i);

        if (key.equals("default")) {
            if (value != null && !value.equals("")) {
                if (!context.isIgnoringProjectTag()) {
                    project.setDefault(value);
                }
            }
        } else if (key.equals("name")) {
            if (value != null) {
                context.setCurrentProjectName(value);
                nameAttributeSet = true;
                if (!context.isIgnoringProjectTag()) {
                    project.setName(value);
                    project.addReference(value, project);
                } else if (isInIncludeMode()) {
                    if (!"".equals(value) && getCurrentTargetPrefix()!= null && getCurrentTargetPrefix().endsWith(ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX))  {
                        String newTargetPrefix = getCurrentTargetPrefix().replace(ProjectHelper.USE_PROJECT_NAME_AS_TARGET_PREFIX, value);
                        // help nested include tasks
                        setCurrentTargetPrefix(newTargetPrefix);
                    }
                }
            }
        } else if (key.equals("id")) {
            if (value != null) {
                // What's the difference between id and name ?
                if (!context.isIgnoringProjectTag()) {
                    project.addReference(value, project);
                }
            }
        } else if (key.equals("basedir")) {
            if (!context.isIgnoringProjectTag()) {
                baseDir = value;
            }
        } else {
            // TODO ignore attributes in a different NS ( maybe store them ? )
            throw new SAXParseException("Unexpected attribute \"" + attrs.getQName(i)
                                        + "\"", context.getLocator());
        }
    }

    // TODO Move to Project ( so it is shared by all helpers )
    String antFileProp =
        MagicNames.ANT_FILE + "." + context.getCurrentProjectName();
    String dup = project.getProperty(antFileProp);
    String typeProp =
        MagicNames.ANT_FILE_TYPE + "." + context.getCurrentProjectName();
    String dupType = project.getProperty(typeProp);
    if (dup != null && nameAttributeSet) {
        Object dupFile = null;
        Object contextFile = null;
        if (MagicNames.ANT_FILE_TYPE_URL.equals(dupType)) {
            try {
                dupFile = new URL(dup);
            } catch (java.net.MalformedURLException mue) {
                throw new BuildException("failed to parse "
                                         + dup + " as URL while looking"
                                         + " at a duplicate project"
                                         + " name.", mue);
            }
            contextFile = context.getBuildFileURL();
        } else {
            dupFile = new File(dup);
            contextFile = context.getBuildFile();
        }

        if (context.isIgnoringProjectTag() && !dupFile.equals(contextFile)) {
            project.log("Duplicated project name in import. Project "
                        + context.getCurrentProjectName() + " defined first in " + dup
                        + " and again in " + contextFile, Project.MSG_WARN);
        }
    }
    if (nameAttributeSet) {
        if (context.getBuildFile() != null) {
            project.setUserProperty(antFileProp,
                                    context.getBuildFile().toString());
            project.setUserProperty(typeProp,
                                    MagicNames.ANT_FILE_TYPE_FILE);
        } else if (context.getBuildFileURL() != null) {
            project.setUserProperty(antFileProp,
                                    context.getBuildFileURL().toString());
            project.setUserProperty(typeProp,
                                    MagicNames.ANT_FILE_TYPE_URL);
        }
    }
    if (context.isIgnoringProjectTag()) {
        // no further processing
        return;
    }
    // set explicitly before starting ?
    if (project.getProperty("basedir") != null) {
        project.setBasedir(project.getProperty("basedir"));
    } else {
        // Default for baseDir is the location of the build file.
        if (baseDir == null) {
            project.setBasedir(context.getBuildFileParent().getAbsolutePath());
        } else {
            // check whether the user has specified an absolute path
            if ((new File(baseDir)).isAbsolute()) {
                project.setBasedir(baseDir);
            } else {
                project.setBaseDir(FILE_UTILS.resolveFile(context.getBuildFileParent(),
                                                          baseDir));
            }
        }
    }
    project.addTarget("", context.getImplicitTarget());
    context.setCurrentTarget(context.getImplicitTarget());
}
