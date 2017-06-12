diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java b/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
index 6eaf9ede2..88f81ecff 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
@@ -1450,1273 +1450,1273 @@ public class FTP
      * the default value of null (which signifies "autodetect") will be kept.
      * @see org.apache.commons.net.ftp.FTPClientConfig
      */
     public void setSystemTypeKey(FTPSystemType systemKey) {
         if (systemKey != null && !systemKey.getValue().equals("")) {
             this.systemTypeKey = systemKey;
             configurationHasBeenSet();
         }
     }
 
     /**
      * Sets the defaultDateFormatConfig attribute.
      * @param defaultDateFormat configuration to be set, unless it is
      * null or empty string, in which case ignored.
      * @see org.apache.commons.net.ftp.FTPClientConfig
      */
     public void setDefaultDateFormatConfig(String defaultDateFormat) {
         if (defaultDateFormat != null && !defaultDateFormat.equals("")) {
             this.defaultDateFormatConfig = defaultDateFormat;
             configurationHasBeenSet();
         }
     }
 
     /**
      * Sets the recentDateFormatConfig attribute.
      * @param recentDateFormat configuration to be set, unless it is
      * null or empty string, in which case ignored.
      * @see org.apache.commons.net.ftp.FTPClientConfig
      */
     public void setRecentDateFormatConfig(String recentDateFormat) {
         if (recentDateFormat != null && !recentDateFormat.equals("")) {
             this.recentDateFormatConfig = recentDateFormat;
             configurationHasBeenSet();
         }
     }
 
     /**
      * Sets the serverLanguageCode attribute.
      * @param serverLanguageCode configuration to be set, unless it is
      * null or empty string, in which case ignored.
      * @see org.apache.commons.net.ftp.FTPClientConfig
      */
     public void setServerLanguageCodeConfig(LanguageCode serverLanguageCode) {
         if (serverLanguageCode != null && !"".equals(serverLanguageCode.getValue())) {
             this.serverLanguageCodeConfig = serverLanguageCode;
             configurationHasBeenSet();
         }
     }
 
     /**
      * Sets the serverTimeZoneConfig attribute.
      * @param serverTimeZoneId configuration to be set, unless it is
      * null or empty string, in which case ignored.
      * @see org.apache.commons.net.ftp.FTPClientConfig
      */
     public void setServerTimeZoneConfig(String serverTimeZoneId) {
         if (serverTimeZoneId != null && !serverTimeZoneId.equals("")) {
             this.serverTimeZoneConfig = serverTimeZoneId;
             configurationHasBeenSet();
         }
     }
 
     /**
      * Sets the shortMonthNamesConfig attribute
      *
      * @param shortMonthNames configuration to be set, unless it is
      * null or empty string, in which case ignored.
      * @see org.apache.commons.net.ftp.FTPClientConfig
      */
     public void setShortMonthNamesConfig(String shortMonthNames) {
         if (shortMonthNames != null && !shortMonthNames.equals("")) {
             this.shortMonthNamesConfig = shortMonthNames;
             configurationHasBeenSet();
         }
     }
 
 
 
     /**
      * Defines how many times to retry executing FTP command before giving up.
      * Default is 0 - try once and if failure then give up.
      *
      * @param retriesAllowed number of retries to allow.  -1 means
      * keep trying forever. "forever" may also be specified as a
      * synonym for -1.
      */
     public void setRetriesAllowed(String retriesAllowed) {
         if ("FOREVER".equalsIgnoreCase(retriesAllowed)) {
             this.retriesAllowed = Retryable.RETRY_FOREVER;
         } else {
             try {
                 int retries = Integer.parseInt(retriesAllowed);
                 if (retries < Retryable.RETRY_FOREVER) {
                     throw new BuildException(
                                              "Invalid value for retriesAllowed attribute: "
                                              + retriesAllowed);
 
                 }
                 this.retriesAllowed = retries;
             } catch (NumberFormatException px) {
                 throw new BuildException(
                                          "Invalid value for retriesAllowed attribute: "
                                          + retriesAllowed);
 
             }
 
         }
     }
     /**
      * @return Returns the systemTypeKey.
      */
     String getSystemTypeKey() {
         return systemTypeKey.getValue();
     }
     /**
      * @return Returns the defaultDateFormatConfig.
      */
     String getDefaultDateFormatConfig() {
         return defaultDateFormatConfig;
     }
     /**
      * @return Returns the recentDateFormatConfig.
      */
     String getRecentDateFormatConfig() {
         return recentDateFormatConfig;
     }
     /**
      * @return Returns the serverLanguageCodeConfig.
      */
     String getServerLanguageCodeConfig() {
         return serverLanguageCodeConfig.getValue();
     }
     /**
      * @return Returns the serverTimeZoneConfig.
      */
     String getServerTimeZoneConfig() {
         return serverTimeZoneConfig;
     }
     /**
      * @return Returns the shortMonthNamesConfig.
      */
     String getShortMonthNamesConfig() {
         return shortMonthNamesConfig;
     }
     /**
      * @return Returns the timestampGranularity.
      */
     Granularity getTimestampGranularity() {
         return timestampGranularity;
     }
     /**
      * Sets the timestampGranularity attribute
      * @param timestampGranularity The timestampGranularity to set.
      */
     public void setTimestampGranularity(Granularity timestampGranularity) {
         if (null == timestampGranularity || "".equals(timestampGranularity.getValue())) {
             return;
         }
         this.timestampGranularity = timestampGranularity;
     }
     /**
      * Sets the siteCommand attribute.  This attribute
      * names the command that will be executed if the action
      * is "site".
      * @param siteCommand The siteCommand to set.
      */
     public void setSiteCommand(String siteCommand) {
         this.siteCommand = siteCommand;
     }
     /**
      * Sets the initialSiteCommand attribute.  This attribute
      * names a site command that will be executed immediately
      * after connection.
      * @param initialCommand The initialSiteCommand to set.
      */
     public void setInitialSiteCommand(String initialCommand) {
         this.initialSiteCommand = initialCommand;
     }
 
     /**
      * Whether to verify that data and control connections are
      * connected to the same remote host.
      *
      * @since Ant 1.8.0
      */
     public void setEnableRemoteVerification(boolean b) {
         enableRemoteVerification = b;
     }
 
     /**
      * Checks to see that all required parameters are set.
      *
      * @throws BuildException if the configuration is not valid.
      */
     protected void checkAttributes() throws BuildException {
         if (server == null) {
             throw new BuildException("server attribute must be set!");
         }
         if (userid == null) {
             throw new BuildException("userid attribute must be set!");
         }
         if (password == null) {
             throw new BuildException("password attribute must be set!");
         }
 
         if ((action == LIST_FILES) && (listing == null)) {
             throw new BuildException("listing attribute must be set for list "
                                      + "action!");
         }
 
         if (action == MK_DIR && remotedir == null) {
             throw new BuildException("remotedir attribute must be set for "
                                      + "mkdir action!");
         }
 
         if (action == CHMOD && chmod == null) {
             throw new BuildException("chmod attribute must be set for chmod "
                                      + "action!");
         }
         if (action == SITE_CMD && siteCommand == null) {
             throw new BuildException("sitecommand attribute must be set for site "
                                      + "action!");
         }
 
 
         if (this.isConfigurationSet) {
             try {
                 Class.forName("org.apache.commons.net.ftp.FTPClientConfig");
             } catch (ClassNotFoundException e) {
                 throw new BuildException(
                                          "commons-net.jar >= 1.4.0 is required for at least one"
                                          + " of the attributes specified.");
             }
         }
     }
 
     /**
      * Executable a retryable object.
      * @param h the retry hander.
      * @param r the object that should be retried until it succeeds
      *          or the number of retrys is reached.
      * @param descr a description of the command that is being run.
      * @throws IOException if there is a problem.
      */
     protected void executeRetryable(RetryHandler h, Retryable r, String descr)
         throws IOException {
         h.execute(r, descr);
     }
 
 
     /**
      * For each file in the fileset, do the appropriate action: send, get,
      * delete, or list.
      *
      * @param ftp the FTPClient instance used to perform FTP actions
      * @param fs the fileset on which the actions are performed.
      *
      * @return the number of files to be transferred.
      *
      * @throws IOException if there is a problem reading a file
      * @throws BuildException if there is a problem in the configuration.
      */
     protected int transferFiles(final FTPClient ftp, FileSet fs)
         throws IOException, BuildException {
         DirectoryScanner ds;
         if (action == SEND_FILES) {
             ds = fs.getDirectoryScanner(getProject());
         } else {
             ds = new FTPDirectoryScanner(ftp);
             fs.setupDirectoryScanner(ds, getProject());
             ds.setFollowSymlinks(fs.isFollowSymlinks());
             ds.scan();
         }
 
         String[] dsfiles = null;
         if (action == RM_DIR) {
             dsfiles = ds.getIncludedDirectories();
         } else {
             dsfiles = ds.getIncludedFiles();
         }
         String dir = null;
 
         if ((ds.getBasedir() == null)
             && ((action == SEND_FILES) || (action == GET_FILES))) {
             throw new BuildException("the dir attribute must be set for send "
                                      + "and get actions");
         } else {
             if ((action == SEND_FILES) || (action == GET_FILES)) {
                 dir = ds.getBasedir().getAbsolutePath();
             }
         }
 
         // If we are doing a listing, we need the output stream created now.
         BufferedWriter bw = null;
 
         try {
             if (action == LIST_FILES) {
                 File pd = listing.getParentFile();
 
                 if (!pd.exists()) {
                     pd.mkdirs();
                 }
                 bw = new BufferedWriter(new FileWriter(listing));
             }
             RetryHandler h = new RetryHandler(this.retriesAllowed, this);
             if (action == RM_DIR) {
                 // to remove directories, start by the end of the list
                 // the trunk does not let itself be removed before the leaves
                 for (int i = dsfiles.length - 1; i >= 0; i--) {
                     final String dsfile = dsfiles[i];
                     executeRetryable(h, new Retryable() {
                             public void execute() throws IOException {
                                 rmDir(ftp, dsfile);
                             }
                         }, dsfile);
                 }
             } else {
                 final BufferedWriter fbw = bw;
                 final String fdir = dir;
                 if (this.newerOnly) {
                     this.granularityMillis =
                         this.timestampGranularity.getMilliseconds(action);
                 }
                 for (int i = 0; i < dsfiles.length; i++) {
                     final String dsfile = dsfiles[i];
                     executeRetryable(h, new Retryable() {
                             public void execute() throws IOException {
                                 switch (action) {
                                 case SEND_FILES:
                                     sendFile(ftp, fdir, dsfile);
                                     break;
                                 case GET_FILES:
                                     getFile(ftp, fdir, dsfile);
                                     break;
                                 case DEL_FILES:
                                     delFile(ftp, dsfile);
                                     break;
                                 case LIST_FILES:
                                     listFile(ftp, fbw, dsfile);
                                     break;
                                 case CHMOD:
                                     doSiteCommand(ftp, "chmod " + chmod
                                                   + " " + resolveFile(dsfile));
                                     transferred++;
                                     break;
                                 default:
                                     throw new BuildException("unknown ftp action " + action);
                                 }
                             }
                         }, dsfile);
                 }
             }
         } finally {
             if (bw != null) {
                 bw.close();
             }
         }
 
         return dsfiles.length;
     }
 
 
     /**
      * Sends all files specified by the configured filesets to the remote
      * server.
      *
      * @param ftp the FTPClient instance used to perform FTP actions
      *
      * @throws IOException if there is a problem reading a file
      * @throws BuildException if there is a problem in the configuration.
      */
     protected void transferFiles(FTPClient ftp)
         throws IOException, BuildException {
         transferred = 0;
         skipped = 0;
 
         if (filesets.size() == 0) {
             throw new BuildException("at least one fileset must be specified.");
         } else {
             // get files from filesets
             for (int i = 0; i < filesets.size(); i++) {
                 FileSet fs = (FileSet) filesets.elementAt(i);
 
                 if (fs != null) {
                     transferFiles(ftp, fs);
                 }
             }
         }
 
         log(transferred + " " + ACTION_TARGET_STRS[action] + " "
             + COMPLETED_ACTION_STRS[action]);
         if (skipped != 0) {
             log(skipped + " " + ACTION_TARGET_STRS[action]
                 + " were not successfully " + COMPLETED_ACTION_STRS[action]);
         }
     }
 
 
     /**
      * Correct a file path to correspond to the remote host requirements. This
      * implementation currently assumes that the remote end can handle
      * Unix-style paths with forward-slash separators. This can be overridden
      * with the <code>separator</code> task parameter. No attempt is made to
      * determine what syntax is appropriate for the remote host.
      *
      * @param file the remote file name to be resolved
      *
      * @return the filename as it will appear on the server.
      */
     protected String resolveFile(String file) {
         return file.replace(System.getProperty("file.separator").charAt(0),
                             remoteFileSep.charAt(0));
     }
 
 
     /**
      * Creates all parent directories specified in a complete relative
      * pathname. Attempts to create existing directories will not cause
      * errors.
      *
      * @param ftp the FTP client instance to use to execute FTP actions on
      *        the remote server.
      * @param filename the name of the file whose parents should be created.
      * @throws IOException under non documented circumstances
      * @throws BuildException if it is impossible to cd to a remote directory
      *
      */
     protected void createParents(FTPClient ftp, String filename)
         throws IOException, BuildException {
 
         File dir = new File(filename);
         if (dirCache.contains(dir)) {
             return;
         }
 
         Vector parents = new Vector();
         String dirname;
 
         while ((dirname = dir.getParent()) != null) {
             File checkDir = new File(dirname);
             if (dirCache.contains(checkDir)) {
                 break;
             }
             dir = checkDir;
             parents.addElement(dir);
         }
 
         // find first non cached dir
         int i = parents.size() - 1;
 
         if (i >= 0) {
             String cwd = ftp.printWorkingDirectory();
             String parent = dir.getParent();
             if (parent != null) {
                 if (!ftp.changeWorkingDirectory(resolveFile(parent))) {
                     throw new BuildException("could not change to "
                                              + "directory: " + ftp.getReplyString());
                 }
             }
 
             while (i >= 0) {
                 dir = (File) parents.elementAt(i--);
                 // check if dir exists by trying to change into it.
                 if (!ftp.changeWorkingDirectory(dir.getName())) {
                     // could not change to it - try to create it
                     log("creating remote directory "
                         + resolveFile(dir.getPath()), Project.MSG_VERBOSE);
                     if (!ftp.makeDirectory(dir.getName())) {
                         handleMkDirFailure(ftp);
                     }
                     if (!ftp.changeWorkingDirectory(dir.getName())) {
                         throw new BuildException("could not change to "
                                                  + "directory: " + ftp.getReplyString());
                     }
                 }
                 dirCache.addElement(dir);
             }
             ftp.changeWorkingDirectory(cwd);
         }
     }
     /**
      * auto find the time difference between local and remote
      * @param ftp handle to ftp client
      * @return number of millis to add to remote time to make it comparable to local time
      * @since ant 1.6
      */
     private long getTimeDiff(FTPClient ftp) {
         long returnValue = 0;
         File tempFile = findFileName(ftp);
         try {
             // create a local temporary file
             FILE_UTILS.createNewFile(tempFile);
             long localTimeStamp = tempFile.lastModified();
             BufferedInputStream instream = new BufferedInputStream(new FileInputStream(tempFile));
             ftp.storeFile(tempFile.getName(), instream);
             instream.close();
             boolean success = FTPReply.isPositiveCompletion(ftp.getReplyCode());
             if (success) {
                 FTPFile [] ftpFiles = ftp.listFiles(tempFile.getName());
                 if (ftpFiles.length == 1) {
                     long remoteTimeStamp = ftpFiles[0].getTimestamp().getTime().getTime();
                     returnValue = localTimeStamp - remoteTimeStamp;
                 }
                 ftp.deleteFile(ftpFiles[0].getName());
             }
             // delegate the deletion of the local temp file to the delete task
             // because of race conditions occuring on Windows
             Delete mydelete = new Delete();
             mydelete.bindToOwner(this);
             mydelete.setFile(tempFile.getCanonicalFile());
             mydelete.execute();
         } catch (Exception e) {
             throw new BuildException(e, getLocation());
         }
         return returnValue;
     }
     /**
      *  find a suitable name for local and remote temporary file
      */
     private File findFileName(FTPClient ftp) {
         FTPFile [] theFiles = null;
         final int maxIterations = 1000;
         for (int counter = 1; counter < maxIterations; counter++) {
             File localFile = FILE_UTILS.createTempFile(
                                                        "ant" + Integer.toString(counter), ".tmp",
                                                        null, false, false);
             String fileName = localFile.getName();
             boolean found = false;
             try {
                 if (theFiles == null) {
                     theFiles = ftp.listFiles();
                 }
                 for (int counter2 = 0; counter2 < theFiles.length; counter2++) {
                     if (theFiles[counter2] != null
                         && theFiles[counter2].getName().equals(fileName)) {
                         found = true;
                         break;
                     }
                 }
             } catch (IOException ioe) {
                 throw new BuildException(ioe, getLocation());
             }
             if (!found) {
                 localFile.deleteOnExit();
                 return localFile;
             }
         }
         return null;
     }
 
     /**
      * Checks to see if the remote file is current as compared with the local
      * file. Returns true if the target file is up to date.
      * @param ftp ftpclient
      * @param localFile local file
      * @param remoteFile remote file
      * @return true if the target file is up to date
      * @throws IOException  in unknown circumstances
      * @throws BuildException if the date of the remote files cannot be found and the action is
      * GET_FILES
      */
     protected boolean isUpToDate(FTPClient ftp, File localFile,
                                  String remoteFile)
         throws IOException, BuildException {
         log("checking date for " + remoteFile, Project.MSG_VERBOSE);
 
         FTPFile[] files = ftp.listFiles(remoteFile);
 
         // For Microsoft's Ftp-Service an Array with length 0 is
         // returned if configured to return listings in "MS-DOS"-Format
         if (files == null || files.length == 0) {
             // If we are sending files, then assume out of date.
             // If we are getting files, then throw an error
 
             if (action == SEND_FILES) {
                 log("Could not date test remote file: " + remoteFile
                     + "assuming out of date.", Project.MSG_VERBOSE);
                 return false;
             } else {
                 throw new BuildException("could not date test remote file: "
                                          + ftp.getReplyString());
             }
         }
 
         long remoteTimestamp = files[0].getTimestamp().getTime().getTime();
         long localTimestamp = localFile.lastModified();
         long adjustedRemoteTimestamp =
             remoteTimestamp + this.timeDiffMillis + this.granularityMillis;
 
         StringBuffer msg;
         synchronized(TIMESTAMP_LOGGING_SDF) {
             msg = new StringBuffer("   [")
                 .append(TIMESTAMP_LOGGING_SDF.format(new Date(localTimestamp)))
                 .append("] local");
         }
         log(msg.toString(), Project.MSG_VERBOSE);
 
         synchronized(TIMESTAMP_LOGGING_SDF) {
             msg = new StringBuffer("   [")
                 .append(TIMESTAMP_LOGGING_SDF.format(new Date(adjustedRemoteTimestamp)))
                 .append("] remote");
         }
         if (remoteTimestamp != adjustedRemoteTimestamp) {
             synchronized(TIMESTAMP_LOGGING_SDF) {
                 msg.append(" - (raw: ")
                     .append(TIMESTAMP_LOGGING_SDF.format(new Date(remoteTimestamp)))
                     .append(")");
             }
         }
         log(msg.toString(), Project.MSG_VERBOSE);
 
 
 
         if (this.action == SEND_FILES) {
             return adjustedRemoteTimestamp >= localTimestamp;
         } else {
             return localTimestamp >= adjustedRemoteTimestamp;
         }
     }
 
 
     /**
      * Sends a site command to the ftp server
      * @param ftp ftp client
      * @param theCMD command to execute
      * @throws IOException  in unknown circumstances
      * @throws BuildException in unknown circumstances
      */
     protected void doSiteCommand(FTPClient ftp, String theCMD)
         throws IOException, BuildException {
         boolean rc;
         String[] myReply = null;
 
         log("Doing Site Command: " + theCMD, Project.MSG_VERBOSE);
 
         rc = ftp.sendSiteCommand(theCMD);
 
         if (!rc) {
             log("Failed to issue Site Command: " + theCMD, Project.MSG_WARN);
         } else {
 
             myReply = ftp.getReplyStrings();
 
             for (int x = 0; x < myReply.length; x++) {
                 if (myReply[x].indexOf("200") == -1) {
                     log(myReply[x], Project.MSG_WARN);
                 }
             }
         }
     }
 
 
     /**
      * Sends a single file to the remote host. <code>filename</code> may
      * contain a relative path specification. When this is the case, <code>sendFile</code>
      * will attempt to create any necessary parent directories before sending
      * the file. The file will then be sent using the entire relative path
      * spec - no attempt is made to change directories. It is anticipated that
      * this may eventually cause problems with some FTP servers, but it
      * simplifies the coding.
      * @param ftp ftp client
      * @param dir base directory of the file to be sent (local)
      * @param filename relative path of the file to be send
      *        locally relative to dir
      *        remotely relative to the remotedir attribute
      * @throws IOException  in unknown circumstances
      * @throws BuildException in unknown circumstances
      */
     protected void sendFile(FTPClient ftp, String dir, String filename)
         throws IOException, BuildException {
         InputStream instream = null;
 
         try {
             // XXX - why not simply new File(dir, filename)?
             File file = getProject().resolveFile(new File(dir, filename).getPath());
 
             if (newerOnly && isUpToDate(ftp, file, resolveFile(filename))) {
                 return;
             }
 
             if (verbose) {
                 log("transferring " + file.getAbsolutePath());
             }
 
             instream = new BufferedInputStream(new FileInputStream(file));
 
             createParents(ftp, filename);
 
             ftp.storeFile(resolveFile(filename), instream);
 
             boolean success = FTPReply.isPositiveCompletion(ftp.getReplyCode());
 
             if (!success) {
                 String s = "could not put file: " + ftp.getReplyString();
 
                 if (skipFailedTransfers) {
                     log(s, Project.MSG_WARN);
                     skipped++;
                 } else {
                     throw new BuildException(s);
                 }
 
             } else {
                 // see if we should issue a chmod command
                 if (chmod != null) {
                     doSiteCommand(ftp, "chmod " + chmod + " " + resolveFile(filename));
                 }
                 log("File " + file.getAbsolutePath() + " copied to " + server,
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
 
 
     /**
      * Delete a file from the remote host.
      * @param ftp ftp client
      * @param filename file to delete
      * @throws IOException  in unknown circumstances
      * @throws BuildException if skipFailedTransfers is set to false
      * and the deletion could not be done
      */
     protected void delFile(FTPClient ftp, String filename)
         throws IOException, BuildException {
         if (verbose) {
             log("deleting " + filename);
         }
 
         if (!ftp.deleteFile(resolveFile(filename))) {
             String s = "could not delete file: " + ftp.getReplyString();
 
             if (skipFailedTransfers) {
                 log(s, Project.MSG_WARN);
                 skipped++;
             } else {
                 throw new BuildException(s);
             }
         } else {
             log("File " + filename + " deleted from " + server,
                 Project.MSG_VERBOSE);
             transferred++;
         }
     }
 
     /**
      * Delete a directory, if empty, from the remote host.
      * @param ftp ftp client
      * @param dirname directory to delete
      * @throws IOException  in unknown circumstances
      * @throws BuildException if skipFailedTransfers is set to false
      * and the deletion could not be done
      */
     protected void rmDir(FTPClient ftp, String dirname)
         throws IOException, BuildException {
         if (verbose) {
             log("removing " + dirname);
         }
 
         if (!ftp.removeDirectory(resolveFile(dirname))) {
             String s = "could not remove directory: " + ftp.getReplyString();
 
             if (skipFailedTransfers) {
                 log(s, Project.MSG_WARN);
                 skipped++;
             } else {
                 throw new BuildException(s);
             }
         } else {
             log("Directory " + dirname + " removed from " + server,
                 Project.MSG_VERBOSE);
             transferred++;
         }
     }
 
 
     /**
      * Retrieve a single file from the remote host. <code>filename</code> may
      * contain a relative path specification. <p>
      *
      * The file will then be retreived using the entire relative path spec -
      * no attempt is made to change directories. It is anticipated that this
      * may eventually cause problems with some FTP servers, but it simplifies
      * the coding.</p>
      * @param ftp the ftp client
      * @param dir local base directory to which the file should go back
      * @param filename relative path of the file based upon the ftp remote directory
      *        and/or the local base directory (dir)
      * @throws IOException  in unknown circumstances
      * @throws BuildException if skipFailedTransfers is false
      * and the file cannot be retrieved.
      */
     protected void getFile(FTPClient ftp, String dir, String filename)
         throws IOException, BuildException {
         OutputStream outstream = null;
         try {
             File file = getProject().resolveFile(new File(dir, filename).getPath());
 
             if (newerOnly && isUpToDate(ftp, file, resolveFile(filename))) {
                 return;
             }
 
             if (verbose) {
                 log("transferring " + filename + " to "
                     + file.getAbsolutePath());
             }
 
             File pdir = file.getParentFile();
 
             if (!pdir.exists()) {
                 pdir.mkdirs();
             }
             outstream = new BufferedOutputStream(new FileOutputStream(file));
             ftp.retrieveFile(resolveFile(filename), outstream);
 
             if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                 String s = "could not get file: " + ftp.getReplyString();
 
                 if (skipFailedTransfers) {
                     log(s, Project.MSG_WARN);
                     skipped++;
                 } else {
                     throw new BuildException(s);
                 }
 
             } else {
                 log("File " + file.getAbsolutePath() + " copied from "
                     + server, Project.MSG_VERBOSE);
                 transferred++;
                 if (preserveLastModified) {
                     outstream.close();
                     outstream = null;
                     FTPFile[] remote = ftp.listFiles(resolveFile(filename));
                     if (remote.length > 0) {
                         FILE_UTILS.setFileLastModified(file,
                                                        remote[0].getTimestamp()
                                                        .getTime().getTime());
                     }
                 }
             }
         } finally {
             if (outstream != null) {
                 try {
                     outstream.close();
                 } catch (IOException ex) {
                     // ignore it
                 }
             }
         }
     }
 
 
     /**
      * List information about a single file from the remote host. <code>filename</code>
      * may contain a relative path specification. <p>
      *
      * The file listing will then be retrieved using the entire relative path
      * spec - no attempt is made to change directories. It is anticipated that
      * this may eventually cause problems with some FTP servers, but it
      * simplifies the coding.</p>
      * @param ftp ftp client
      * @param bw buffered writer
      * @param filename the directory one wants to list
      * @throws IOException  in unknown circumstances
      * @throws BuildException in unknown circumstances
      */
     protected void listFile(FTPClient ftp, BufferedWriter bw, String filename)
         throws IOException, BuildException {
         if (verbose) {
             log("listing " + filename);
         }
         FTPFile[] ftpfiles = ftp.listFiles(resolveFile(filename));
 
         if (ftpfiles != null && ftpfiles.length > 0) {
             bw.write(ftpfiles[0].toString());
             bw.newLine();
             transferred++;
         }
     }
 
 
     /**
      * Create the specified directory on the remote host.
      *
      * @param ftp The FTP client connection
      * @param dir The directory to create (format must be correct for host
      *      type)
      * @throws IOException  in unknown circumstances
      * @throws BuildException if ignoreNoncriticalErrors has not been set to true
      *         and a directory could not be created, for instance because it was
      *         already existing. Precisely, the codes 521, 550 and 553 will trigger
      *         a BuildException
      */
     protected void makeRemoteDir(FTPClient ftp, String dir)
         throws IOException, BuildException {
         String workingDirectory = ftp.printWorkingDirectory();
         if (verbose) {
             if (dir.indexOf("/") == 0 || workingDirectory == null) {
                 log("Creating directory: " + dir + " in /");
             } else {
                 log("Creating directory: " + dir + " in " + workingDirectory);
             }
         }
         if (dir.indexOf("/") == 0) {
             ftp.changeWorkingDirectory("/");
         }
         String subdir = "";
         StringTokenizer st = new StringTokenizer(dir, "/");
         while (st.hasMoreTokens()) {
             subdir = st.nextToken();
             log("Checking " + subdir, Project.MSG_DEBUG);
             if (!ftp.changeWorkingDirectory(subdir)) {
                 if (!ftp.makeDirectory(subdir)) {
                     // codes 521, 550 and 553 can be produced by FTP Servers
                     //  to indicate that an attempt to create a directory has
                     //  failed because the directory already exists.
                     int rc = ftp.getReplyCode();
                     if (!(ignoreNoncriticalErrors
                           && (rc == FTPReply.CODE_550 || rc == FTPReply.CODE_553
                               || rc == CODE_521))) {
                         throw new BuildException("could not create directory: "
                                                  + ftp.getReplyString());
                     }
                     if (verbose) {
                         log("Directory already exists");
                     }
                 } else {
                     if (verbose) {
                         log("Directory created OK");
                     }
                     ftp.changeWorkingDirectory(subdir);
                 }
             }
         }
         if (workingDirectory != null) {
             ftp.changeWorkingDirectory(workingDirectory);
         }
     }
 
     /**
      * look at the response for a failed mkdir action, decide whether
      * it matters or not. If it does, we throw an exception
      * @param ftp current ftp connection
      * @throws BuildException if this is an error to signal
      */
     private void handleMkDirFailure(FTPClient ftp)
         throws BuildException {
         int rc = ftp.getReplyCode();
         if (!(ignoreNoncriticalErrors
               && (rc == FTPReply.CODE_550 || rc == FTPReply.CODE_553 || rc == CODE_521))) {
             throw new BuildException("could not create directory: "
                                      + ftp.getReplyString());
         }
     }
 
     /**
      * Runs the task.
      *
      * @throws BuildException if the task fails or is not configured
      *         correctly.
      */
     public void execute() throws BuildException {
         checkAttributes();
 
         FTPClient ftp = null;
 
         try {
             log("Opening FTP connection to " + server, Project.MSG_VERBOSE);
 
             ftp = new FTPClient();
             if (this.isConfigurationSet) {
                 ftp = FTPConfigurator.configure(ftp, this);
             }
 
             ftp.setRemoteVerificationEnabled(enableRemoteVerification);
             ftp.connect(server, port);
             if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                 throw new BuildException("FTP connection failed: "
                                          + ftp.getReplyString());
             }
 
             log("connected", Project.MSG_VERBOSE);
             log("logging in to FTP server", Project.MSG_VERBOSE);
 
             if ((this.account != null && !ftp.login(userid, password, account))
                 || (this.account == null && !ftp.login(userid, password))) {
                 throw new BuildException("Could not login to FTP server");
             }
 
             log("login succeeded", Project.MSG_VERBOSE);
 
             if (binary) {
-                ftp.setFileType(org.apache.commons.net.ftp.FTP.IMAGE_FILE_TYPE);
+                ftp.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                 if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                     throw new BuildException("could not set transfer type: "
                                              + ftp.getReplyString());
                 }
             } else {
                 ftp.setFileType(org.apache.commons.net.ftp.FTP.ASCII_FILE_TYPE);
                 if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                     throw new BuildException("could not set transfer type: "
                                              + ftp.getReplyString());
                 }
             }
 
             if (passive) {
                 log("entering passive mode", Project.MSG_VERBOSE);
                 ftp.enterLocalPassiveMode();
                 if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                     throw new BuildException("could not enter into passive "
                                              + "mode: " + ftp.getReplyString());
                 }
             }
 
             // If an initial command was configured then send it.
             // Some FTP servers offer different modes of operation,
             // E.G. switching between a UNIX file system mode and
             // a legacy file system.
             if (this.initialSiteCommand != null) {
                 RetryHandler h = new RetryHandler(this.retriesAllowed, this);
                 final FTPClient lftp = ftp;
                 executeRetryable(h, new Retryable() {
                         public void execute() throws IOException {
                             doSiteCommand(lftp, FTP.this.initialSiteCommand);
                         }
                     }, "initial site command: " + this.initialSiteCommand);
             }
 
 
             // For a unix ftp server you can set the default mask for all files
             // created.
 
             if (umask != null) {
                 RetryHandler h = new RetryHandler(this.retriesAllowed, this);
                 final FTPClient lftp = ftp;
                 executeRetryable(h, new Retryable() {
                         public void execute() throws IOException {
                             doSiteCommand(lftp, "umask " + umask);
                         }
                     }, "umask " + umask);
             }
 
             // If the action is MK_DIR, then the specified remote
             // directory is the directory to create.
 
             if (action == MK_DIR) {
                 RetryHandler h = new RetryHandler(this.retriesAllowed, this);
                 final FTPClient lftp = ftp;
                 executeRetryable(h, new Retryable() {
                         public void execute() throws IOException {
                             makeRemoteDir(lftp, remotedir);
                         }
                     }, remotedir);
             } else if (action == SITE_CMD) {
                 RetryHandler h = new RetryHandler(this.retriesAllowed, this);
                 final FTPClient lftp = ftp;
                 executeRetryable(h, new Retryable() {
                         public void execute() throws IOException {
                             doSiteCommand(lftp, FTP.this.siteCommand);
                         }
                     }, "Site Command: " + this.siteCommand);
             } else {
                 if (remotedir != null) {
                     log("changing the remote directory to " + remotedir,
                         Project.MSG_VERBOSE);
                     ftp.changeWorkingDirectory(remotedir);
                     if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                         throw new BuildException("could not change remote "
                                                  + "directory: " + ftp.getReplyString());
                     }
                 }
                 if (newerOnly && timeDiffAuto) {
                     // in this case we want to find how much time span there is between local
                     // and remote
                     timeDiffMillis = getTimeDiff(ftp);
                 }
                 log(ACTION_STRS[action] + " " + ACTION_TARGET_STRS[action]);
                 transferFiles(ftp);
             }
 
         } catch (IOException ex) {
             throw new BuildException("error during FTP transfer: " + ex, ex);
         } finally {
             if (ftp != null && ftp.isConnected()) {
                 try {
                     log("disconnecting", Project.MSG_VERBOSE);
                     ftp.logout();
                     ftp.disconnect();
                 } catch (IOException ex) {
                     // ignore it
                 }
             }
         }
     }
 
 
     /**
      * an action to perform, one of
      * "send", "put", "recv", "get", "del", "delete", "list", "mkdir", "chmod",
      * "rmdir"
      */
     public static class Action extends EnumeratedAttribute {
 
         private static final String[] VALID_ACTIONS = {
             "send", "put", "recv", "get", "del", "delete", "list", "mkdir",
             "chmod", "rmdir", "site"
         };
 
 
         /**
          * Get the valid values
          *
          * @return an array of the valid FTP actions.
          */
         public String[] getValues() {
             return VALID_ACTIONS;
         }
 
 
         /**
          * Get the symbolic equivalent of the action value.
          *
          * @return the SYMBOL representing the given action.
          */
         public int getAction() {
             String actionL = getValue().toLowerCase(Locale.US);
 
             if (actionL.equals("send") || actionL.equals("put")) {
                 return SEND_FILES;
             } else if (actionL.equals("recv") || actionL.equals("get")) {
                 return GET_FILES;
             } else if (actionL.equals("del") || actionL.equals("delete")) {
                 return DEL_FILES;
             } else if (actionL.equals("list")) {
                 return LIST_FILES;
             } else if (actionL.equals("chmod")) {
                 return CHMOD;
             } else if (actionL.equals("mkdir")) {
                 return MK_DIR;
             } else if (actionL.equals("rmdir")) {
                 return RM_DIR;
             } else if (actionL.equals("site")) {
                 return SITE_CMD;
             }
             return SEND_FILES;
         }
     }
     /**
      * represents one of the valid timestamp adjustment values
      * recognized by the <code>timestampGranularity</code> attribute.<p>
 
      * A timestamp adjustment may be used in file transfers for checking
      * uptodateness. MINUTE means to add one minute to the server
      * timestamp.  This is done because FTP servers typically list
      * timestamps HH:mm and client FileSystems typically use HH:mm:ss.
      *
      * The default is to use MINUTE for PUT actions and NONE for GET
      * actions, since GETs have the <code>preserveLastModified</code>
      * option, which takes care of the problem in most use cases where
      * this level of granularity is an issue.
      *
      */
     public static class Granularity extends EnumeratedAttribute {
 
         private static final String[] VALID_GRANULARITIES = {
             "", "MINUTE", "NONE"
         };
 
         /**
          * Get the valid values.
          * @return the list of valid Granularity values
          */
         public String[] getValues() {
             return VALID_GRANULARITIES;
         }
         /**
          * returns the number of milliseconds associated with
          * the attribute, which can vary in some cases depending
          * on the value of the action parameter.
          * @param action SEND_FILES or GET_FILES
          * @return the number of milliseconds associated with
          * the attribute, in the context of the supplied action
          */
         public long getMilliseconds(int action) {
             String granularityU = getValue().toUpperCase(Locale.US);
 
             if ("".equals(granularityU)) {
                 if (action == SEND_FILES) {
                     return GRANULARITY_MINUTE;
                 }
             } else if ("MINUTE".equals(granularityU)) {
                 return GRANULARITY_MINUTE;
             }
             return 0L;
         }
         static final Granularity getDefault() {
             Granularity g = new Granularity();
             g.setValue("");
             return g;
         }
 
     }
     /**
      * one of the valid system type keys recognized by the systemTypeKey
      * attribute.
      */
     public static class FTPSystemType extends EnumeratedAttribute {
 
         private static final String[] VALID_SYSTEM_TYPES = {
             "", "UNIX", "VMS", "WINDOWS", "OS/2", "OS/400",
             "MVS"
         };
 
 
         /**
          * Get the valid values.
          * @return the list of valid system types.
          */
         public String[] getValues() {
             return VALID_SYSTEM_TYPES;
         }
 
         static final FTPSystemType getDefault() {
             FTPSystemType ftpst = new FTPSystemType();
             ftpst.setValue("");
             return ftpst;
         }
     }
     /**
      * Enumerated class for languages.
      */
     public static class LanguageCode extends EnumeratedAttribute {
 
 
         private static final String[] VALID_LANGUAGE_CODES =
             getValidLanguageCodes();
 
         private static String[] getValidLanguageCodes() {
             Collection c = FTPClientConfig.getSupportedLanguageCodes();
             String[] ret = new String[c.size() + 1];
             int i = 0;
             ret[i++] = "";
             for (Iterator it = c.iterator(); it.hasNext(); i++) {
                 ret[i] = (String) it.next();
             }
             return ret;
         }
 
 
         /**
          * Return the value values.
          * @return the list of valid language types.
          */
         public String[] getValues() {
             return VALID_LANGUAGE_CODES;
         }
 
         static final LanguageCode getDefault() {
             LanguageCode lc = new LanguageCode();
             lc.setValue("");
             return lc;
         }
     }
 
 }
