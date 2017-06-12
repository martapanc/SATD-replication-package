    private boolean isParentFirst(String resourceName) {
        // default to the global setting and then see
        // if this class belongs to a package which has been
        // designated to use a specific loader first
        // (this one or the parent one)

        // TODO - shouldn't this always return false in isolated mode?

        boolean useParentFirst = parentFirst;

        for (Enumeration<String> e = systemPackages.elements(); e.hasMoreElements();) {
            String packageName = e.nextElement();
            if (resourceName.startsWith(packageName)) {
                useParentFirst = true;
                break;
            }
        }
        for (Enumeration<String> e = loaderPackages.elements(); e.hasMoreElements();) {
            String packageName = e.nextElement();
            if (resourceName.startsWith(packageName)) {
                useParentFirst = false;
                break;
            }
        }
        return useParentFirst;
    }
