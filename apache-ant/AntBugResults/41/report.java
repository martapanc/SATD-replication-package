File path: src/main/org/apache/tools/ant/taskdefs/Classloader.java
Comment: XXX maybe use reflection to addPathElement (other patterns ?)
Initial commit id: 6ee5317c
Final commit id: 13000c1a
   Bugs between [       5]:
d8922d6d2 deprecate reverse attribute.  PR 45845.
e0d63af0c only add classpath elements to existing loaders, not to a freshly created one.  PR 45847.
b6aa5cb51 make log more usefull.  PR 45841.
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
3396e7c32 Remove direct call to deprecated project, location, tasktype Task field, replaced by an accessor way into tasks. Remove too some unused variable declaration and some unused imports. PR: 22515 Submitted by: Emmanuel Feller ( Emmanuel dot Feller at free dot fr)
   Bugs after [       0]:


Start block index: 179
End block index: 258
    public void execute() {
        try {
            // Gump friendly - don't mess with the core loader if only classpath
            if ("only".equals(project.getProperty("build.sysclasspath"))
                && (name == null || SYSTEM_LOADER_REF.equals(name))) {
                log("Changing the system loader is disabled "
                    + "by build.sysclasspath=only", Project.MSG_WARN);
                return;
            }

            String loaderName = (name == null) ? SYSTEM_LOADER_REF : name;

            Object obj = project.getReference(loaderName);
            if (reset) {
                // Are any other references held ? Can we 'close' the loader
                // so it removes the locks on jars ?
                obj = null; // a new one will be created.
            }

            // XXX maybe use reflection to addPathElement (other patterns ?)
            if (obj != null && !(obj instanceof AntClassLoader)) {
                log("Referenced object is not an AntClassLoader",
                        Project.MSG_ERR);
                return;
            }

            AntClassLoader acl = (AntClassLoader) obj;

            if (acl == null) {
                // Construct a new class loader
                Object parent = null;
                if (parentName != null) {
                    parent = project.getReference(parentName);
                    if (!(parent instanceof ClassLoader)) {
                        parent = null;
                    }
                }
                // TODO: allow user to request the system or no parent
                if (parent == null) {
                    parent = this.getClass().getClassLoader();
                }

                if (name == null) {
                    // The core loader must be reverse
                    //reverse=true;
                }
                project.log("Setting parent loader " + name + " "
                    + parent + " " + parentFirst, Project.MSG_DEBUG);

                // The param is "parentFirst"
                acl = new AntClassLoader((ClassLoader) parent,
                        project, classpath, parentFirst);

                project.addReference(loaderName, acl);

                if (name == null) {
                    // This allows the core loader to load optional tasks
                    // without delegating
                    acl.addLoaderPackageRoot("org.apache.tools.ant.taskdefs.optional");
                    project.setCoreLoader(acl);
                }
            }
            if (classpath != null) {
                String[] list = classpath.list();
                for (int i = 0; i < list.length; i++) {
                    File f = new File(list[i]);
                    if (f.exists()) {
                        acl.addPathElement(f.getAbsolutePath());
                        log("Adding to class loader " +  acl + " " + f.getAbsolutePath(),
                                Project.MSG_DEBUG);
                    }
                }
            }

            // XXX add exceptions

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
