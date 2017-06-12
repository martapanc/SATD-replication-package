diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java b/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
index 5f522a6d3..213b984fd 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
@@ -1,1076 +1,1070 @@
 /*
  * Copyright  2000-2004 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 package org.apache.tools.ant.taskdefs.optional.net;
 
 import org.apache.commons.net.ftp.FTPClient;
 import org.apache.commons.net.ftp.FTPFile;
 import org.apache.commons.net.ftp.FTPReply;
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.HashSet;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.Vector;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Delete;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.selectors.SelectorUtils;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Basic FTP client. Performs the following actions:
  * <ul>
  *   <li> <strong>send</strong> - send files to a remote server. This is the
  *   default action.</li>
  *   <li> <strong>get</strong> - retrieve files from a remote server.</li>
  *   <li> <strong>del</strong> - delete files from a remote server.</li>
  *   <li> <strong>list</strong> - create a file listing.</li>
  *   <li> <strong>chmod</strong> - change unix file permissions.</li>
  *   <li> <strong>rmdir</strong> - remove directories, if empty, from a
  *   remote server.</li>
  * </ul>
  * <strong>Note:</strong> Some FTP servers - notably the Solaris server - seem
  * to hold data ports open after a "retr" operation, allowing them to timeout
  * instead of shutting them down cleanly. This happens in active or passive
  * mode, and the ports will remain open even after ending the FTP session. FTP
  * "send" operations seem to close ports immediately. This behavior may cause
  * problems on some systems when downloading large sets of files.
  *
- * @author Roger Vaughn <a href="mailto:rvaughn@seaconinc.com">
- *      rvaughn@seaconinc.com</a>
- * @author Glenn McAllister <a href="mailto:glennm@ca.ibm.com">
- *      glennm@ca.ibm.com</a>
- * @author Magesh Umasankar
- * @author <a href="mailto:kadams@gfs.com">Kyle Adams</a>
  * @since Ant 1.3
  */
 public class FTP
      extends Task {
     protected static final int SEND_FILES = 0;
     protected static final int GET_FILES = 1;
     protected static final int DEL_FILES = 2;
     protected static final int LIST_FILES = 3;
     protected static final int MK_DIR = 4;
     protected static final int CHMOD = 5;
     protected static final int RM_DIR = 6;
     /** return code of ftp - not implemented in commons-net version 1.0 */
     private static final int CODE_521 = 521;
     /** Default port for FTP */
     public static final int DEFAULT_FTP_PORT = 21;
 
     private String remotedir;
     private String server;
     private String userid;
     private String password;
     private File listing;
     private boolean binary = true;
     private boolean passive = false;
     private boolean verbose = false;
     private boolean newerOnly = false;
     private long timeDiffMillis = 0;
     private boolean timeDiffAuto = false;
     private int action = SEND_FILES;
     private Vector filesets = new Vector();
     private Vector dirCache = new Vector();
     private int transferred = 0;
     private String remoteFileSep = "/";
     private int port = DEFAULT_FTP_PORT;
     private boolean skipFailedTransfers = false;
     private int skipped = 0;
     private boolean ignoreNoncriticalErrors = false;
     private boolean preserveLastModified = false;
     private String chmod = null;
     private String umask = null;
     private FileUtils fileUtils = FileUtils.newFileUtils();
 
     protected static final String[] ACTION_STRS = {
         "sending",
         "getting",
         "deleting",
         "listing",
         "making directory",
         "chmod",
         "removing"
         };
 
     protected static final String[] COMPLETED_ACTION_STRS = {
         "sent",
         "retrieved",
         "deleted",
         "listed",
         "created directory",
         "mode changed",
         "removed"
         };
 
     protected static final String[] ACTION_TARGET_STRS = {
         "files",
         "files",
         "files",
         "files",
         "directory",
         "files",
         "directories"
         };
 
 
     /**
      * internal class allowing to read the contents of a remote file system
      * using the FTP protocol
      * used in particular for ftp get operations
      * differences with DirectoryScanner
      * "" (the root of the fileset) is never included in the included directories
      * followSymlinks defaults to false
      */
     protected class FTPDirectoryScanner extends DirectoryScanner {
         protected FTPClient ftp = null;
         private String rootPath = null;
         /**
          * since ant 1.6
          * this flag should be set to true on UNIX and can save scanning time
          */
         private boolean remoteSystemCaseSensitive = false;
         private boolean remoteSensitivityChecked = false;
 
         /**
          * constructor
          * @param ftp  ftpclient object
          */
         public FTPDirectoryScanner(FTPClient ftp) {
             super();
             this.ftp = ftp;
             this.setFollowSymlinks(false);
         }
 
 
         /**
          * scans the remote directory,
          * storing internally the included files, directories, ...
          */
         public void scan() {
             if (includes == null) {
                 // No includes supplied, so set it to 'matches all'
                 includes = new String[1];
                 includes[0] = "**";
             }
             if (excludes == null) {
                 excludes = new String[0];
             }
 
             filesIncluded = new Vector();
             filesNotIncluded = new Vector();
             filesExcluded = new Vector();
             dirsIncluded = new Vector();
             dirsNotIncluded = new Vector();
             dirsExcluded = new Vector();
 
             try {
                 String cwd = ftp.printWorkingDirectory();
                 // always start from the current ftp working dir
 
                 checkIncludePatterns();
                 clearCaches();
                 ftp.changeWorkingDirectory(cwd);
             } catch (IOException e) {
                 throw new BuildException("Unable to scan FTP server: ", e);
             }
         }
 
 
         /**
          * this routine is actually checking all the include patterns in
          * order to avoid scanning everything under base dir
          * @since ant1.6
          */
         private void checkIncludePatterns() {
             Hashtable newroots = new Hashtable();
             // put in the newroots vector the include patterns without
             // wildcard tokens
             for (int icounter = 0; icounter < includes.length; icounter++) {
                 String newpattern =
                     SelectorUtils.rtrimWildcardTokens(includes[icounter]);
                 newroots.put(newpattern, includes[icounter]);
             }
             if (remotedir == null) {
                 try {
                     remotedir = ftp.printWorkingDirectory();
                 } catch (IOException e) {
                     throw new BuildException("could not read current ftp directory",
                         getLocation());
                 }
             }
             AntFTPFile baseFTPFile = new AntFTPRootFile(ftp, remotedir);
             rootPath = baseFTPFile.getAbsolutePath();
             // construct it
             if (newroots.containsKey("")) {
                 // we are going to scan everything anyway
                 scandir(rootPath, "", true);
             } else {
                 // only scan directories that can include matched files or
                 // directories
                 Enumeration enum2 = newroots.keys();
 
                 while (enum2.hasMoreElements()) {
                     String currentelement = (String) enum2.nextElement();
                     String originalpattern = (String) newroots.get(currentelement);
                     AntFTPFile myfile = new AntFTPFile(baseFTPFile, currentelement);
                     boolean isOK = true;
                     boolean traversesSymlinks = false;
                     String path = null;
 
                     if (myfile.exists()) {
                         if (remoteSensitivityChecked
                             && remoteSystemCaseSensitive && isFollowSymlinks()) {
                             // cool case,
                             //we do not need to scan all the subdirs in the relative path
                             path = myfile.getFastRelativePath();
                         } else {
                             // may be on a case insensitive file system.  We want
                             // the results to show what's really on the disk, so
                             // we need to double check.
                             try {
                                 path = myfile.getRelativePath();
                                 traversesSymlinks = myfile.isTraverseSymlinks();
                             }  catch (IOException be) {
                                 throw new BuildException(be, getLocation());
                             } catch (BuildException be) {
                                 isOK = false;
 
                             }
                         }
                     } else {
                         isOK = false;
                     }
                     if (isOK) {
                         currentelement = path.replace(remoteFileSep.charAt(0), File.separatorChar);
                         if (!isFollowSymlinks()
                             && traversesSymlinks) {
                             continue;
                         }
 
                         if (myfile.isDirectory()) {
                             if (isIncluded(currentelement)
                                 && currentelement.length() > 0) {
                                 accountForIncludedDir(currentelement, myfile, true);
                             }  else {
                                 if (currentelement.length() > 0) {
                                     if (currentelement.charAt(currentelement
                                                               .length() - 1)
                                         != File.separatorChar) {
                                         currentelement =
                                             currentelement + File.separatorChar;
                                     }
                                 }
                                 scandir(myfile.getAbsolutePath(), currentelement, true);
                             }
                         } else {
                             if (isCaseSensitive
                                 && originalpattern.equals(currentelement)) {
                                 accountForIncludedFile(currentelement);
                             } else if (!isCaseSensitive
                                        && originalpattern
                                        .equalsIgnoreCase(currentelement)) {
                                 accountForIncludedFile(currentelement);
                             }
                         }
                     }
                 }
             }
         }
         /**
          * scans a particular directory
          * @param dir directory to scan
          * @param vpath  relative path to the base directory of the remote fileset
          * always ended with a File.separator
          * @param fast seems to be always true in practice
          */
         protected void scandir(String dir, String vpath, boolean fast) {
             // avoid double scanning of directories, can only happen in fast mode
             if (fast && hasBeenScanned(vpath)) {
                 return;
             }
             try {
                 if (!ftp.changeWorkingDirectory(dir)) {
                     return;
                 }
                 String completePath = null;
                 if (!vpath.equals("")) {
                     completePath = rootPath + remoteFileSep
                         + vpath.replace(File.separatorChar, remoteFileSep.charAt(0));
                 } else {
                     completePath = rootPath;
                 }
                 FTPFile[] newfiles = listFiles(completePath, false);
 
                 if (newfiles == null) {
                     ftp.changeToParentDirectory();
                     return;
                 }
                 for (int i = 0; i < newfiles.length; i++) {
                     FTPFile file = newfiles[i];
                     if (!file.getName().equals(".")
                          && !file.getName().equals("..")) {
                         if (isFunctioningAsDirectory(ftp, dir, file)) {
                             String name = vpath + file.getName();
                             boolean slowScanAllowed = true;
                             if (!isFollowSymlinks() && file.isSymbolicLink()) {
                                 dirsExcluded.addElement(name);
                                 slowScanAllowed = false;
                             } else if (isIncluded(name)) {
                                 accountForIncludedDir(name,
                                     new AntFTPFile(ftp, file, completePath) , fast);
                             } else {
                                 dirsNotIncluded.addElement(name);
                                 if (fast && couldHoldIncluded(name)) {
                                     scandir(file.getName(),
                                             name + File.separator, fast);
                                 }
                             }
                             if (!fast && slowScanAllowed) {
                                 scandir(file.getName(),
                                         name + File.separator, fast);
                             }
                         } else {
                             String name = vpath + file.getName();
                             if (!isFollowSymlinks() && file.isSymbolicLink()) {
                                 filesExcluded.addElement(name);
                             } else if (isFunctioningAsFile(ftp, dir, file)) {
                                 accountForIncludedFile(name);
                             }
                         }
                     }
                 }
                 ftp.changeToParentDirectory();
             } catch (IOException e) {
                 throw new BuildException("Error while communicating with FTP "
                      + "server: ", e);
             }
         }
         /**
          * process included file
          * @param name  path of the file relative to the directory of the fileset
          */
         private void accountForIncludedFile(String name) {
             if (!filesIncluded.contains(name)
                 && !filesExcluded.contains(name)) {
 
                 if (isIncluded(name)) {
                     if (!isExcluded(name)) {
                         filesIncluded.addElement(name);
                     } else {
                         filesExcluded.addElement(name);
                     }
                 } else {
                     filesNotIncluded.addElement(name);
                 }
             }
         }
 
         /**
          *
          * @param name path of the directory relative to the directory of
          * the fileset
          * @param file directory as file
          * @param fast
          */
         private void accountForIncludedDir(String name, AntFTPFile file, boolean fast) {
             if (!dirsIncluded.contains(name)
                 && !dirsExcluded.contains(name)) {
 
                 if (!isExcluded(name)) {
                     if (fast) {
                         if (file.isSymbolicLink()) {
                             try {
                                 file.getClient().changeWorkingDirectory(file.curpwd);
                             } catch (IOException ioe) {
                                 throw new BuildException("could not change directory to curpwd");
                             }
                             scandir(file.getLink(),
                                 name + File.separator, fast);
                         } else {
                             try {
                                 file.getClient().changeWorkingDirectory(file.curpwd);
                             } catch (IOException ioe) {
                                 throw new BuildException("could not change directory to curpwd");
                             }
                             scandir(file.getName(),
                                 name + File.separator, fast);
                         }
                     }
                     dirsIncluded.addElement(name);
                 } else {
                     dirsExcluded.addElement(name);
                     if (fast && couldHoldIncluded(name)) {
                         try {
                             file.getClient().changeWorkingDirectory(file.curpwd);
                         } catch (IOException ioe) {
                             throw new BuildException("could not change directory to curpwd");
                         }
                         scandir(file.getName(),
                                 name + File.separator, fast);
                     }
                 }
             }
         }
         /**
          * temporary table to speed up the various scanning methods below
          *
          * @since Ant 1.6
          */
         private Map fileListMap = new HashMap();
         /**
          * List of all scanned directories.
          *
          * @since Ant 1.6
          */
         private Set scannedDirs = new HashSet();
 
         /**
          * Has the directory with the given path relative to the base
          * directory already been scanned?
          *
          * <p>Registers the given directory as scanned as a side effect.</p>
          *
          * @since Ant 1.6
          */
         private boolean hasBeenScanned(String vpath) {
             return !scannedDirs.add(vpath);
         }
 
         /**
          * Clear internal caches.
          *
          * @since Ant 1.6
          */
         private void clearCaches() {
             fileListMap.clear();
             scannedDirs.clear();
         }
         /**
          * list the files present in one directory.
          * @param directory full path on the remote side
          * @param changedir if true change to directory directory before listing
          * @return array of FTPFile
          */
         public FTPFile[] listFiles(String directory, boolean changedir) {
             //getProject().log("listing files in directory " + directory, Project.MSG_DEBUG);
             String currentPath = directory;
             if (changedir) {
                 try {
                     boolean result = ftp.changeWorkingDirectory(directory);
                     if (!result) {
                         return null;
                     }
                     currentPath = ftp.printWorkingDirectory();
                 } catch (IOException ioe) {
                     throw new BuildException(ioe, getLocation());
                 }
             }
             if (fileListMap.containsKey(currentPath)) {
                 getProject().log("filelist map used in listing files", Project.MSG_DEBUG);
                 return ((FTPFile[]) fileListMap.get(currentPath));
             }
             FTPFile[] result = null;
             try {
                 result = ftp.listFiles();
             } catch (IOException ioe) {
                 throw new BuildException(ioe, getLocation());
             }
             fileListMap.put(currentPath, result);
             if (!remoteSensitivityChecked) {
                 checkRemoteSensitivity(result, directory);
             }
             return result;
         }
         /**
          * cd into one directory and
          * list the files present in one directory.
          * @param directory full path on the remote side
          * @return array of FTPFile
          */
         public FTPFile[] listFiles(String directory) {
             return listFiles(directory, true);
         }
         private void checkRemoteSensitivity(FTPFile[] array, String directory) {
             if (array == null) {
                 return;
             }
             boolean candidateFound = false;
             String target = null;
             for (int icounter = 0; icounter < array.length; icounter++) {
                 if (array[icounter].isDirectory()) {
                     if (!array[icounter].getName().equals(".")
                         && !array[icounter].getName().equals("..")) {
                         candidateFound = true;
                         target = fiddleName(array[icounter].getName());
                         getProject().log("will try to cd to "
                             + target + " where a directory called " + array[icounter].getName()
                             + " exists", Project.MSG_DEBUG);
                         for (int pcounter = 0; pcounter < array.length; pcounter++) {
                             if (array[pcounter].getName().equals(target) && pcounter != icounter) {
                                 candidateFound = false;
                             }
                         }
                         if (candidateFound) {
                             break;
                         }
                     }
                 }
             }
             if (candidateFound) {
                 try {
                     getProject().log("testing case sensitivity, attempting to cd to "
                         + target, Project.MSG_DEBUG);
                     remoteSystemCaseSensitive  = !ftp.changeWorkingDirectory(target);
                 } catch (IOException ioe) {
                     remoteSystemCaseSensitive = true;
                 } finally {
                     try {
                         ftp.changeWorkingDirectory(directory);
                     } catch (IOException ioe) {
                         throw new BuildException(ioe, getLocation());
                     }
                 }
                 getProject().log("remote system is case sensitive : " + remoteSystemCaseSensitive,
                     Project.MSG_VERBOSE);
                 remoteSensitivityChecked = true;
             }
         }
         private String fiddleName(String origin) {
             StringBuffer result = new StringBuffer();
             for (int icounter = 0; icounter < origin.length(); icounter++) {
                 if (Character.isLowerCase(origin.charAt(icounter))) {
                     result.append(Character.toUpperCase(origin.charAt(icounter)));
                 } else if (Character.isUpperCase(origin.charAt(icounter))) {
                     result.append(Character.toLowerCase(origin.charAt(icounter)));
                 } else {
                     result.append(origin.charAt(icounter));
                 }
             }
             return result.toString();
         }
         /**
          * an AntFTPFile is a representation of a remote file
          * @since Ant 1.6
          */
         protected class AntFTPFile {
             /**
              * ftp client
              */
             private FTPClient client;
             /**
              * parent directory of the file
              */
             private String curpwd;
             /**
              * the file itself
              */
             private FTPFile ftpFile;
             /**
              *
              */
             private AntFTPFile parent = null;
             private boolean relativePathCalculated = false;
             private boolean traversesSymlinks = false;
             private String relativePath = "";
             /**
              * constructor
              * @param client ftp client variable
              * @param ftpFile the file
              * @param curpwd absolute remote path where the file is found
              */
             public AntFTPFile(FTPClient client, FTPFile ftpFile, String curpwd) {
                 this.client = client;
                 this.ftpFile = ftpFile;
                 this.curpwd = curpwd;
             }
             /**
              * other constructor
              * @param parent the parent file
              * @param path  a relative path to the parent file
              */
             public AntFTPFile(AntFTPFile parent, String path) {
                 this.parent = parent;
                 this.client = parent.client;
                 Vector pathElements = SelectorUtils.tokenizePath(path);
                 try {
                     boolean result = this.client.changeWorkingDirectory(parent.getAbsolutePath());
                     //this should not happen, except if parent has been deleted by another process
                     if (!result) {
                         return;
                     }
                     this.curpwd = parent.getAbsolutePath();
                 } catch (IOException ioe) {
                     throw new BuildException("could not change working dir to "
                     + parent.curpwd);
                 }
                 for (int fcount = 0; fcount < pathElements.size() - 1; fcount++) {
                     String currentPathElement = (String) pathElements.elementAt(fcount);
                     try {
                         boolean result = this.client.changeWorkingDirectory(currentPathElement);
                         if (!result && !isCaseSensitive()
                             && (remoteSystemCaseSensitive || !remoteSensitivityChecked)) {
                            currentPathElement = findPathElementCaseUnsensitive(this.curpwd,
                                currentPathElement);
                             if (currentPathElement == null) {
                                 return;
                             }
                         } else if (!result) {
                             return;
                         }
                         this.curpwd = this.curpwd + remoteFileSep
                             + currentPathElement;
                     } catch (IOException ioe) {
                         throw new BuildException("could not change working dir to "
                         + (String) pathElements.elementAt(fcount)
                             + " from " + this.curpwd);
                     }
 
                 }
                 String lastpathelement = (String) pathElements.elementAt(pathElements.size() - 1);
                 FTPFile [] theFiles = listFiles(this.curpwd);
                 this.ftpFile = getFile(theFiles, lastpathelement);
             }
             /**
              * find a file in a directory in case unsensitive way
              * @param parentPath        where we are
              * @param soughtPathElement what is being sought
              * @return                  the first file found or null if not found
              */
             private String findPathElementCaseUnsensitive(String parentPath,
                                String soughtPathElement) {
                 // we are already in the right path, so the second parameter
                 // is false
                 FTPFile[] theFiles = listFiles(parentPath, false);
                 if (theFiles == null) {
                     return null;
                 }
                 for (int icounter = 0; icounter < theFiles.length; icounter++) {
                     if (theFiles[icounter].getName().equalsIgnoreCase(soughtPathElement)) {
                         return theFiles[icounter].getName();
                     }
                 }
                 return null;
             }
             /**
              * find out if the file exists
              * @return  true if the file exists
              */
             public boolean exists() {
                 return (ftpFile != null);
             }
             /**
              * if the file is a symbolic link, find out to what it is pointing
              * @return the target of the symbolic link
              */
             public String getLink() {
                 return ftpFile.getLink();
             }
             /**
              * get the name of the file
              * @return the name of the file
              */
             public String getName() {
                 return ftpFile.getName();
             }
             /**
              * find out the absolute path of the file
              * @return absolute path as string
              */
             public String getAbsolutePath() {
                 return curpwd + remoteFileSep + ftpFile.getName();
             }
             /**
              * find out the relative path assuming that the path used to construct
              * this AntFTPFile was spelled properly with regards to case.
              * This is OK on a case sensitive system such as UNIX
              * @return relative path
              */
             public String getFastRelativePath() {
                 String absPath = getAbsolutePath();
                 if (absPath.indexOf(rootPath + remoteFileSep) == 0) {
                     return absPath.substring(rootPath.length() + remoteFileSep.length());
                 }
                 return null;
             }
             /**
              * find out the relative path to the rootPath of the enclosing scanner.
              * this relative path is spelled exactly like on disk,
              * for instance if the AntFTPFile has been instantiated as ALPHA,
              * but the file is really called alpha, this method will return alpha.
              * If a symbolic link is encountered, it is followed, but the name of the link
              * rather than the name of the target is returned.
              * (ie does not behave like File.getCanonicalPath())
              * @return                relative path, separated by remoteFileSep
              * @throws IOException    if a change directory fails, ...
              * @throws BuildException if one of the components of the relative path cannot
              * be found.
              */
             public String getRelativePath() throws IOException, BuildException {
                 if (!relativePathCalculated) {
                     if (parent != null) {
                         traversesSymlinks = parent.isTraverseSymlinks();
                         relativePath = getRelativePath(parent.getAbsolutePath(),
                             parent.getRelativePath());
                     } else {
                         relativePath = getRelativePath(rootPath, "");
                         relativePathCalculated = true;
                     }
                 }
                 return relativePath;
             }
             /**
              * get thge relative path of this file
              * @param currentPath          base path
              * @param currentRelativePath  relative path of the base path with regards to remote dir
              * @return relative path
              */
             private String getRelativePath(String currentPath, String currentRelativePath) {
                 Vector pathElements = SelectorUtils.tokenizePath(getAbsolutePath(), remoteFileSep);
                 Vector pathElements2 = SelectorUtils.tokenizePath(currentPath, remoteFileSep);
                 String relPath = currentRelativePath;
                 for (int pcount = pathElements2.size(); pcount < pathElements.size(); pcount++) {
                     String currentElement = (String) pathElements.elementAt(pcount);
                     FTPFile[] theFiles = listFiles(currentPath);
                     FTPFile theFile = null;
                     if (theFiles != null) {
                         theFile = getFile(theFiles, currentElement);
                     }
                     if (theFile == null) {
                         throw new BuildException("could not find " + currentElement
                             + " from " + currentPath);
                     } else {
                         traversesSymlinks = traversesSymlinks || theFile.isSymbolicLink();
                         if (!relPath.equals("")) {
                             relPath = relPath + remoteFileSep;
                         }
                         relPath = relPath + theFile.getName();
                         currentPath = currentPath + remoteFileSep + theFile.getName();
                     }
                 }
                 return relPath;
             }
             /**
              * find a file matching a string in an array of FTPFile.
              * This method will find "alpha" when requested for "ALPHA"
              * if and only if the caseSensitive attribute is set to false.
              * When caseSensitive is set to true, only the exact match is returned.
              * @param theFiles  array of files
              * @param lastpathelement  the file name being sought
              * @return null if the file cannot be found, otherwise return the matching file.
              */
             public FTPFile getFile(FTPFile[] theFiles, String lastpathelement) {
                 if (theFiles == null) {
                     return null;
                 }
                 for (int fcount = 0; fcount < theFiles.length; fcount++) {
                      if (theFiles[fcount].getName().equals(lastpathelement)) {
                          return theFiles[fcount];
                      } else if (!isCaseSensitive()
                          && theFiles[fcount].getName().equalsIgnoreCase(lastpathelement)) {
                          return theFiles[fcount];
                      }
                 }
                 return null;
             }
             /**
              * tell if a file is a directory.
              * note that it will return false for symbolic links pointing to directories.
              * @return <code>true</code> for directories
              */
             public boolean isDirectory() {
                 return ftpFile.isDirectory();
             }
             /**
              * tell if a file is a symbolic link
              * @return <code>true</code> for symbolic links
              */
             public boolean isSymbolicLink() {
                 return ftpFile.isSymbolicLink();
             }
             /**
              * return the attached FTP client object.
              * Warning : this instance is really shared with the enclosing class.
              * @return  FTP client
              */
             protected FTPClient getClient() {
                 return client;
             }
 
             /**
              * sets the current path of an AntFTPFile
              * @param curpwd the current path one wants to set
              */
             protected void setCurpwd(String curpwd) {
                 this.curpwd = curpwd;
             }
             /**
              * returns the path of the directory containing the AntFTPFile.
              * of the full path of the file itself in case of AntFTPRootFile
              * @return parent directory of the AntFTPFile
              */
             public String getCurpwd() {
                 return curpwd;
             }
             /**
              * find out if a symbolic link is encountered in the relative path of this file
              * from rootPath.
              * @return <code>true</code> if a symbolic link is encountered in the relative path.
              * @throws IOException if one of the change directory or directory listing operations
              * fails
              * @throws BuildException if a path component in the relative path cannot be found.
              */
             public boolean isTraverseSymlinks() throws IOException, BuildException {
                 if (!relativePathCalculated) {
                     // getRelativePath also finds about symlinks
                     String relpath = getRelativePath();
                 }
                 return traversesSymlinks;
             }
         }
         /**
          * special class to represent the remote directory itself
          * @since Ant 1.6
          */
         protected class AntFTPRootFile extends AntFTPFile {
              private String remotedir;
             /**
              * constructor
              * @param aclient FTP client
              * @param remotedir remote directory
              */
              public AntFTPRootFile(FTPClient aclient, String remotedir) {
                  super(aclient, null, remotedir);
                  this.remotedir = remotedir;
                  try {
                      this.getClient().changeWorkingDirectory(this.remotedir);
                      this.setCurpwd(this.getClient().printWorkingDirectory());
                  } catch (IOException ioe) {
                      throw new BuildException(ioe, getLocation());
                  }
              }
             /**
              * find the absolute path
              * @return absolute path
              */
             public String getAbsolutePath() {
                 return this.getCurpwd();
             }
             /**
              * find out the relative path to root
              * @return empty string
              * @throws BuildException actually never
              * @throws IOException  actually never
              */
             public String getRelativePath() throws BuildException, IOException {
                  return "";
             }
         }
     }
     /**
      * check FTPFiles to check whether they function as directories too
      * the FTPFile API seem to make directory and symbolic links incompatible
      * we want to find out if we can cd to a symbolic link
      * @param dir  the parent directory of the file to test
      * @param file the file to test
      * @return true if it is possible to cd to this directory
      * @since ant 1.6
      */
     private boolean isFunctioningAsDirectory(FTPClient ftp, String dir, FTPFile file) {
         boolean result = false;
         String currentWorkingDir = null;
         if (file.isDirectory()) {
             return true;
         } else if (file.isFile()) {
             return false;
         }
         try {
             currentWorkingDir = ftp.printWorkingDirectory();
         } catch (IOException ioe) {
             getProject().log("could not find current working directory " + dir
                 + " while checking a symlink",
                 Project.MSG_DEBUG);
         }
         if (currentWorkingDir != null) {
             try {
                 result = ftp.changeWorkingDirectory(file.getLink());
             } catch (IOException ioe) {
                 getProject().log("could not cd to " + file.getLink() + " while checking a symlink",
                     Project.MSG_DEBUG);
             }
             if (result) {
                 boolean comeback = false;
                 try {
                     comeback = ftp.changeWorkingDirectory(currentWorkingDir);
                 } catch (IOException ioe) {
                     getProject().log("could not cd back to " + dir + " while checking a symlink",
                         Project.MSG_ERR);
                 } finally {
                     if (!comeback) {
                         throw new BuildException("could not cd back to " + dir
                             + " while checking a symlink");
                     }
                 }
             }
         }
         return result;
     }
     /**
      * check FTPFiles to check whether they function as directories too
      * the FTPFile API seem to make directory and symbolic links incompatible
      * we want to find out if we can cd to a symbolic link
      * @param dir  the parent directory of the file to test
      * @param file the file to test
      * @return true if it is possible to cd to this directory
      * @since ant 1.6
      */
     private boolean isFunctioningAsFile(FTPClient ftp, String dir, FTPFile file) {
         if (file.isDirectory()) {
             return false;
         } else if (file.isFile()) {
             return true;
         }
         return !isFunctioningAsDirectory(ftp, dir, file);
     }
     /**
      * Sets the remote directory where files will be placed. This may be a
      * relative or absolute path, and must be in the path syntax expected by
      * the remote server. No correction of path syntax will be performed.
      *
      * @param dir the remote directory name.
      */
     public void setRemotedir(String dir) {
         this.remotedir = dir;
     }
 
 
     /**
      * Sets the FTP server to send files to.
      *
      * @param server the remote server name.
      */
     public void setServer(String server) {
         this.server = server;
     }
 
 
     /**
      * Sets the FTP port used by the remote server.
      *
      * @param port the port on which the remote server is listening.
      */
     public void setPort(int port) {
         this.port = port;
     }
 
 
     /**
      * Sets the login user id to use on the specified server.
      *
      * @param userid remote system userid.
      */
     public void setUserid(String userid) {
         this.userid = userid;
     }
 
 
     /**
      * Sets the login password for the given user id.
      *
      * @param password the password on the remote system.
      */
     public void setPassword(String password) {
         this.password = password;
     }
 
 
     /**
      * If true, uses binary mode, otherwise text mode (default is binary).
      *
      * @param binary if true use binary mode in transfers.
      */
     public void setBinary(boolean binary) {
         this.binary = binary;
     }
 
 
     /**
      * Specifies whether to use passive mode. Set to true if you are behind a
      * firewall and cannot connect without it. Passive mode is disabled by
      * default.
      *
      * @param passive true is passive mode should be used.
      */
     public void setPassive(boolean passive) {
         this.passive = passive;
     }
 
 
     /**
      * Set to true to receive notification about each file as it is
      * transferred.
      *
      * @param verbose true if verbose notifications are required.
      */
     public void setVerbose(boolean verbose) {
         this.verbose = verbose;
     }
 
 
     /**
      * A synonym for <tt>depends</tt>. Set to true to transmit only new
      * or changed files.
      *
      * See the related attributes timediffmillis and timediffauto.
      *
      * @param newer if true only transfer newer files.
      */
     public void setNewer(boolean newer) {
         this.newerOnly = newer;
     }
 
     /**
      * number of milliseconds to add to the time on the remote machine
      * to get the time on the local machine.
      *
      * use in conjunction with <code>newer</code>
      *
      * @param timeDiffMillis number of milliseconds
      *
      * @since ant 1.6
      */
     public void setTimeDiffMillis(long timeDiffMillis) {
         this.timeDiffMillis = timeDiffMillis;
     }
