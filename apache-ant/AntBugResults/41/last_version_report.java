    public void execute() {
        try {
            // Gump friendly - don't mess with the core loader if only classpath
            if ("only".equals(getProject().getProperty("build.sysclasspath"))
                && (name == null || SYSTEM_LOADER_REF.equals(name))) {
                log("Changing the system loader is disabled "
                    + "by build.sysclasspath=only", Project.MSG_WARN);
                return;
            }

            String loaderName = (name == null) ? SYSTEM_LOADER_REF : name;

            Object obj = getProject().getReference(loaderName);
            if (reset) {
                // Are any other references held ? Can we 'close' the loader
                // so it removes the locks on jars ?
                obj = null; // a new one will be created.
            }

            // TODO maybe use reflection to addPathElement (other patterns ?)
            if (obj != null && !(obj instanceof AntClassLoader)) {
                log("Referenced object is not an AntClassLoader",
                        Project.MSG_ERR);
                return;
            }

            AntClassLoader acl = (AntClassLoader) obj;
            boolean existingLoader = acl != null;

            if (acl == null) {
                // Construct a new class loader
                Object parent = null;
                if (parentName != null) {
                    parent = getProject().getReference(parentName);
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
                getProject().log("Setting parent loader " + name + " "
                    + parent + " " + parentFirst, Project.MSG_DEBUG);

                // The param is "parentFirst"
                acl = AntClassLoader.newAntClassLoader((ClassLoader) parent,
                         getProject(), classpath, parentFirst);

                getProject().addReference(loaderName, acl);

                if (name == null) {
                    // This allows the core loader to load optional tasks
                    // without delegating
                    acl.addLoaderPackageRoot("org.apache.tools.ant.taskdefs.optional");
                    getProject().setCoreLoader(acl);
                }
            }

            if (existingLoader && classpath != null) {
                String[] list = classpath.list();
                for (int i = 0; i < list.length; i++) {
                    File f = new File(list[i]);
                    if (f.exists()) {
                        log("Adding to class loader " +  acl + " " + f.getAbsolutePath(),
                                Project.MSG_DEBUG);
                        acl.addPathElement(f.getAbsolutePath());
                    }
                }
            }

            // TODO add exceptions

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
