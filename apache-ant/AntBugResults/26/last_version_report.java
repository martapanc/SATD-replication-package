    public void execute() throws BuildException {

        String savedCommand = getCommand();

        if (this.getCommand() == null && vecCommandlines.size() == 0) {
            // re-implement legacy behaviour:
            this.setCommand(AbstractCvsTask.DEFAULT_COMMAND);
        }

        String c = this.getCommand();
        Commandline cloned = null;
        if (c != null) {
            cloned = (Commandline) cmd.clone();
            cloned.createArgument(true).setLine(c);
            this.addConfiguredCommandline(cloned, true);
        }

        try {
            final int size = vecCommandlines.size();
            for (int i = 0; i < size; i++) {
                this.runCommand((Commandline) vecCommandlines.elementAt(i));
            }
        } finally {
            if (cloned != null) {
                removeCommandline(cloned);
            }
            setCommand(savedCommand);
            FileUtils.close(outputStream);
            FileUtils.close(errorStream);
        }
    }
