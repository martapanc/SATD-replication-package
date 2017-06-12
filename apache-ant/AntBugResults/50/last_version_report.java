    public RegexpMatcher newRegexpMatcher(Project p) throws BuildException {
        String systemDefault = null;
        if (p == null) {
            systemDefault = System.getProperty(MagicNames.REGEXP_IMPL);
        } else {
            systemDefault = p.getProperty(MagicNames.REGEXP_IMPL);
        }

        if (systemDefault != null) {
            return createInstance(systemDefault);
            // TODO     should we silently catch possible exceptions and try to
            //         load a different implementation?
        }

        return new Jdk14RegexpMatcher();
    }
