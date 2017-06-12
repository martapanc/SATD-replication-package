File path: src/main/org/apache/tools/ant/taskdefs/optional/net/FTPTaskMirrorImpl.java
Comment: XXX - why not simply new File(dir
Initial commit id: d7241318
Final commit id: 13000c1a
   Bugs between [       2]:
5f20b9914 microoptimizations.  PR 50716
969aeca23 merge fix for PR 49296
   Bugs after [       1]:
43844a7e6 PR 56748 Spelling fixes, submitted by Ville Skyttä

Start block index: 1503
End block index: 1556
    protected void sendFile(FTPClient ftp, String dir, String filename)
        throws IOException, BuildException {
        InputStream instream = null;

        try {
            // XXX - why not simply new File(dir, filename)?
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
