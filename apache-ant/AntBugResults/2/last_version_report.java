    public void maybeConfigure() throws BuildException {
        if (!invalid) {
            if (wrapper != null) {
                wrapper.maybeConfigure(getProject());
            }
        } else {
            getReplacement();
        }
    }
