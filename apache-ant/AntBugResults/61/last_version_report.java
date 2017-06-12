    protected void sendFile(FTPClient ftp, String dir, String filename)
        throws IOException, BuildException {
        InputStream instream = null;

        try {
            // TODO - why not simply new File(dir, filename)?
            File file = task.getProject().resolveFile(new File(dir, filename).getPath());

            if (task.isNewer() && isUpToDate(ftp, file, resolveFile(filename))) {
                return;
            }

            if (task.isVerbose()) {
                task.log("transferring " + file.getAbsolutePath());
            }

            instream = new BufferedInputStream(new FileInputStream(file));

            createParents(ftp, filename);

            ftp.storeFile(resolveFile(filename), instream);

            boolean success = FTPReply.isPositiveCompletion(ftp.getReplyCode());

            if (!success) {
                String s = "could not put file: " + ftp.getReplyString();

                if (task.isSkipFailedTransfers()) {
                    task.log(s, Project.MSG_WARN);
                    skipped++;
                } else {
                    throw new BuildException(s);
                }

            } else {
                // see if we should issue a chmod command
                if (task.getChmod() != null) {
                    doSiteCommand(ftp, "chmod " + task.getChmod() + " "
                                  + resolveFile(filename));
                }
                task.log("File " + file.getAbsolutePath() + " copied to " + task.getServer(),
                         Project.MSG_VERBOSE);
                transferred++;
            }
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (IOException ex) {
                    // ignore it
                }
            }
        }
    }
