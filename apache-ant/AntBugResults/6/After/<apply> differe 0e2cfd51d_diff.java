diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index 3f6e214fe..275414c58 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -57,1338 +57,1358 @@ import org.apache.tools.ant.util.FileUtils;
  * means that everything will be matched. When no list of exclude patterns is
  * supplied, an empty list is used, such that nothing will be excluded. When
  * no selectors are supplied, none are applied.
  * <p>
  * The filename pattern matching is done as follows:
  * The name to be matched is split up in path segments. A path segment is the
  * name of a directory or file, which is bounded by
  * <code>File.separator</code> ('/' under UNIX, '\' under Windows).
  * For example, "abc/def/ghi/xyz.java" is split up in the segments "abc",
  * "def","ghi" and "xyz.java".
  * The same is done for the pattern against which should be matched.
  * <p>
  * The segments of the name and the pattern are then matched against each
  * other. When '**' is used for a path segment in the pattern, it matches
  * zero or more path segments of the name.
  * <p>
  * There is a special case regarding the use of <code>File.separator</code>s
  * at the beginning of the pattern and the string to match:<br>
  * When a pattern starts with a <code>File.separator</code>, the string
  * to match must also start with a <code>File.separator</code>.
  * When a pattern does not start with a <code>File.separator</code>, the
  * string to match may not start with a <code>File.separator</code>.
  * When one of these rules is not obeyed, the string will not
  * match.
  * <p>
  * When a name path segment is matched against a pattern path segment, the
  * following special characters can be used:<br>
  * '*' matches zero or more characters<br>
  * '?' matches one character.
  * <p>
  * Examples:
  * <p>
  * "**\*.class" matches all .class files/dirs in a directory tree.
  * <p>
  * "test\a??.java" matches all files/dirs which start with an 'a', then two
  * more characters and then ".java", in a directory called test.
  * <p>
  * "**" matches everything in a directory tree.
  * <p>
  * "**\test\**\XYZ*" matches all files/dirs which start with "XYZ" and where
  * there is a parent directory called test (e.g. "abc\test\def\ghi\XYZ123").
  * <p>
  * Case sensitivity may be turned off if necessary. By default, it is
  * turned on.
  * <p>
  * Example of usage:
  * <pre>
  *   String[] includes = {"**\\*.class"};
  *   String[] excludes = {"modules\\*\\**"};
  *   ds.setIncludes(includes);
  *   ds.setExcludes(excludes);
  *   ds.setBasedir(new File("test"));
  *   ds.setCaseSensitive(true);
  *   ds.scan();
  *
  *   System.out.println("FILES:");
  *   String[] files = ds.getIncludedFiles();
  *   for (int i = 0; i < files.length; i++) {
  *     System.out.println(files[i]);
  *   }
  * </pre>
  * This will scan a directory called test for .class files, but excludes all
  * files in all proper subdirectories of a directory called "modules"
  *
  */
 public class DirectoryScanner
        implements FileScanner, SelectorScanner, ResourceFactory {
 
     /** Is OpenVMS the operating system we're running on? */
     private static final boolean ON_VMS = Os.isFamily("openvms");
 
     /**
      * Patterns which should be excluded by default.
      *
      * <p>Note that you can now add patterns to the list of default
      * excludes.  Added patterns will not become part of this array
      * that has only been kept around for backwards compatibility
      * reasons.</p>
      *
      * @deprecated use the {@link #getDefaultExcludes
      * getDefaultExcludes} method instead.
      */
     protected static final String[] DEFAULTEXCLUDES = {
         // Miscellaneous typical temporary files
         "**/*~",
         "**/#*#",
         "**/.#*",
         "**/%*%",
         "**/._*",
 
         // CVS
         "**/CVS",
         "**/CVS/**",
         "**/.cvsignore",
 
         // SCCS
         "**/SCCS",
         "**/SCCS/**",
 
         // Visual SourceSafe
         "**/vssver.scc",
 
         // Subversion
         "**/.svn",
         "**/.svn/**",
 
         // Mac
         "**/.DS_Store"
     };
 
     /**
      * Patterns which should be excluded by default.
      *
      * @see #addDefaultExcludes()
      */
     private static Vector defaultExcludes = new Vector();
     static {
         resetDefaultExcludes();
     }
 
     /** The base directory to be scanned. */
     protected File basedir;
 
     /** The patterns for the files to be included. */
     protected String[] includes;
 
     /** The patterns for the files to be excluded. */
     protected String[] excludes;
 
     /** Selectors that will filter which files are in our candidate list. */
     protected FileSelector[] selectors = null;
 
     /** The files which matched at least one include and no excludes
      *  and were selected.
      */
     protected Vector filesIncluded;
 
     /** The files which did not match any includes or selectors. */
     protected Vector filesNotIncluded;
 
     /**
      * The files which matched at least one include and at least
      * one exclude.
      */
     protected Vector filesExcluded;
 
     /** The directories which matched at least one include and no excludes
      *  and were selected.
      */
     protected Vector dirsIncluded;
 
     /** The directories which were found and did not match any includes. */
     protected Vector dirsNotIncluded;
 
     /**
      * The directories which matched at least one include and at least one
      * exclude.
      */
     protected Vector dirsExcluded;
 
     /** The files which matched at least one include and no excludes and
      *  which a selector discarded.
      */
     protected Vector filesDeselected;
 
     /** The directories which matched at least one include and no excludes
      *  but which a selector discarded.
      */
     protected Vector dirsDeselected;
 
     /** Whether or not our results were built by a slow scan. */
     protected boolean haveSlowResults = false;
 
     /**
      * Whether or not the file system should be treated as a case sensitive
      * one.
      */
     protected boolean isCaseSensitive = true;
 
     /**
      * Whether or not symbolic links should be followed.
      *
      * @since Ant 1.5
      */
     private boolean followSymlinks = true;
 
     /** Helper. */
     private static final FileUtils FILE_UTILS = FileUtils.newFileUtils();
 
     /** Whether or not everything tested so far has been included. */
     protected boolean everythingIncluded = true;
 
     /**
      * Sole constructor.
      */
     public DirectoryScanner() {
     }
 
     /**
      * Tests whether or not a given path matches the start of a given
      * pattern up to the first "**".
      * <p>
      * This is not a general purpose test and should only be used if you
      * can live with false positives. For example, <code>pattern=**\a</code>
      * and <code>str=b</code> will yield <code>true</code>.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      *
      * @return whether or not a given path matches the start of a given
      * pattern up to the first "**".
      */
     protected static boolean matchPatternStart(String pattern, String str) {
         return SelectorUtils.matchPatternStart(pattern, str);
     }
     /**
      * Tests whether or not a given path matches the start of a given
      * pattern up to the first "**".
      * <p>
      * This is not a general purpose test and should only be used if you
      * can live with false positives. For example, <code>pattern=**\a</code>
      * and <code>str=b</code> will yield <code>true</code>.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      * @param isCaseSensitive Whether or not matching should be performed
      *                        case sensitively.
      *
      * @return whether or not a given path matches the start of a given
      * pattern up to the first "**".
      */
     protected static boolean matchPatternStart(String pattern, String str,
                                                boolean isCaseSensitive) {
         return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
     }
 
     /**
      * Tests whether or not a given path matches a given pattern.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      *
      * @return <code>true</code> if the pattern matches against the string,
      *         or <code>false</code> otherwise.
      */
     protected static boolean matchPath(String pattern, String str) {
         return SelectorUtils.matchPath(pattern, str);
     }
 
     /**
      * Tests whether or not a given path matches a given pattern.
      *
      * @param pattern The pattern to match against. Must not be
      *                <code>null</code>.
      * @param str     The path to match, as a String. Must not be
      *                <code>null</code>.
      * @param isCaseSensitive Whether or not matching should be performed
      *                        case sensitively.
      *
      * @return <code>true</code> if the pattern matches against the string,
      *         or <code>false</code> otherwise.
      */
     protected static boolean matchPath(String pattern, String str,
                                        boolean isCaseSensitive) {
         return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
     }
 
     /**
      * Tests whether or not a string matches against a pattern.
      * The pattern may contain two special characters:<br>
      * '*' means zero or more characters<br>
      * '?' means one and only one character
      *
      * @param pattern The pattern to match against.
      *                Must not be <code>null</code>.
      * @param str     The string which must be matched against the pattern.
      *                Must not be <code>null</code>.
      *
      * @return <code>true</code> if the string matches against the pattern,
      *         or <code>false</code> otherwise.
      */
     public static boolean match(String pattern, String str) {
         return SelectorUtils.match(pattern, str);
     }
 
     /**
      * Tests whether or not a string matches against a pattern.
      * The pattern may contain two special characters:<br>
      * '*' means zero or more characters<br>
      * '?' means one and only one character
      *
      * @param pattern The pattern to match against.
      *                Must not be <code>null</code>.
      * @param str     The string which must be matched against the pattern.
      *                Must not be <code>null</code>.
      * @param isCaseSensitive Whether or not matching should be performed
      *                        case sensitively.
      *
      *
      * @return <code>true</code> if the string matches against the pattern,
      *         or <code>false</code> otherwise.
      */
     protected static boolean match(String pattern, String str,
                                    boolean isCaseSensitive) {
         return SelectorUtils.match(pattern, str, isCaseSensitive);
     }
 
 
     /**
      * Get the list of patterns that should be excluded by default.
      *
      * @return An array of <code>String</code> based on the current
      *         contents of the <code>defaultExcludes</code>
      *         <code>Vector</code>.
      *
      * @since Ant 1.6
      */
     public static String[] getDefaultExcludes() {
         return (String[]) defaultExcludes.toArray(new String[defaultExcludes
                                                              .size()]);
     }
 
     /**
      * Add a pattern to the default excludes unless it is already a
      * default exclude.
      *
      * @param s   A string to add as an exclude pattern.
      * @return    <code>true</code> if the string was added
      *            <code>false</code> if it already
      *            existed.
      *
      * @since Ant 1.6
      */
     public static boolean addDefaultExclude(String s) {
         if (defaultExcludes.indexOf(s) == -1) {
             defaultExcludes.add(s);
             return true;
         }
         return false;
     }
 
     /**
      * Remove a string if it is a default exclude.
      *
      * @param s   The string to attempt to remove.
      * @return    <code>true</code> if <code>s</code> was a default
      *            exclude (and thus was removed),
      *            <code>false</code> if <code>s</code> was not
      *            in the default excludes list to begin with
      *
      * @since Ant 1.6
      */
     public static boolean removeDefaultExclude(String s) {
         return defaultExcludes.remove(s);
     }
 
     /**
      *  Go back to the hard wired default exclude patterns
      *
      * @since Ant 1.6
      */
     public static void resetDefaultExcludes() {
         defaultExcludes = new Vector();
 
         for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
             defaultExcludes.add(DEFAULTEXCLUDES[i]);
         }
     }
 
     /**
      * Sets the base directory to be scanned. This is the directory which is
      * scanned recursively. All '/' and '\' characters are replaced by
      * <code>File.separatorChar</code>, so the separator used need not match
      * <code>File.separatorChar</code>.
      *
      * @param basedir The base directory to scan.
      *                Must not be <code>null</code>.
      */
     public void setBasedir(String basedir) {
         setBasedir(new File(basedir.replace('/', File.separatorChar).replace(
                 '\\', File.separatorChar)));
     }
 
     /**
      * Sets the base directory to be scanned. This is the directory which is
      * scanned recursively.
      *
      * @param basedir The base directory for scanning.
      *                Should not be <code>null</code>.
      */
     public void setBasedir(File basedir) {
         this.basedir = basedir;
     }
 
     /**
      * Returns the base directory to be scanned.
      * This is the directory which is scanned recursively.
      *
      * @return the base directory to be scanned
      */
     public File getBasedir() {
         return basedir;
     }
 
     /**
      * Find out whether include exclude patterns are matched in a
      * case sensitive way
      * @return whether or not the scanning is case sensitive
      * @since ant 1.6
      */
     public boolean isCaseSensitive() {
         return isCaseSensitive;
     }
     /**
      * Sets whether or not include and exclude patterns are matched
      * in a case sensitive way
      *
      * @param isCaseSensitive whether or not the file system should be
      *                        regarded as a case sensitive one
      */
     public void setCaseSensitive(boolean isCaseSensitive) {
         this.isCaseSensitive = isCaseSensitive;
     }
 
     /**
      * gets whether or not a DirectoryScanner follows symbolic links
      *
      * @return flag indicating whether symbolic links should be followed
      *
      * @since ant 1.6
      */
     public boolean isFollowSymlinks() {
         return followSymlinks;
     }
 
     /**
      * Sets whether or not symbolic links should be followed.
      *
      * @param followSymlinks whether or not symbolic links should be followed
      */
     public void setFollowSymlinks(boolean followSymlinks) {
         this.followSymlinks = followSymlinks;
     }
 
     /**
      * Sets the list of include patterns to use. All '/' and '\' characters
      * are replaced by <code>File.separatorChar</code>, so the separator used
      * need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param includes A list of include patterns.
      *                 May be <code>null</code>, indicating that all files
      *                 should be included. If a non-<code>null</code>
      *                 list is given, all elements must be
      * non-<code>null</code>.
      */
     public void setIncludes(String[] includes) {
         if (includes == null) {
             this.includes = null;
         } else {
             this.includes = new String[includes.length];
             for (int i = 0; i < includes.length; i++) {
                 String pattern;
                 pattern = includes[i].replace('/', File.separatorChar).replace(
                         '\\', File.separatorChar);
                 if (pattern.endsWith(File.separator)) {
                     pattern += "**";
                 }
                 this.includes[i] = pattern;
             }
         }
     }
 
 
     /**
      * Sets the list of exclude patterns to use. All '/' and '\' characters
      * are replaced by <code>File.separatorChar</code>, so the separator used
      * need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param excludes A list of exclude patterns.
      *                 May be <code>null</code>, indicating that no files
      *                 should be excluded. If a non-<code>null</code> list is
      *                 given, all elements must be non-<code>null</code>.
      */
     public void setExcludes(String[] excludes) {
         if (excludes == null) {
             this.excludes = null;
         } else {
             this.excludes = new String[excludes.length];
             for (int i = 0; i < excludes.length; i++) {
                 String pattern;
                 pattern = excludes[i].replace('/', File.separatorChar).replace(
                         '\\', File.separatorChar);
                 if (pattern.endsWith(File.separator)) {
                     pattern += "**";
                 }
                 this.excludes[i] = pattern;
             }
         }
     }
 
 
     /**
      * Sets the selectors that will select the filelist.
      *
      * @param selectors specifies the selectors to be invoked on a scan
      */
     public void setSelectors(FileSelector[] selectors) {
         this.selectors = selectors;
     }
 
 
     /**
      * Returns whether or not the scanner has included all the files or
      * directories it has come across so far.
      *
      * @return <code>true</code> if all files and directories which have
      *         been found so far have been included.
      */
     public boolean isEverythingIncluded() {
         return everythingIncluded;
     }
 
     /**
      * Scans the base directory for files which match at least one include
      * pattern and don't match any exclude patterns. If there are selectors
      * then the files must pass muster there, as well.
      *
      * @exception IllegalStateException if the base directory was set
      *            incorrectly (i.e. if it is <code>null</code>, doesn't exist,
      *            or isn't a directory).
      */
     public void scan() throws IllegalStateException {
         if (basedir == null) {
             throw new IllegalStateException("No basedir set");
         }
         if (!basedir.exists()) {
             throw new IllegalStateException("basedir " + basedir
                                             + " does not exist");
         }
         if (!basedir.isDirectory()) {
             throw new IllegalStateException("basedir " + basedir
                                             + " is not a directory");
         }
 
         if (includes == null) {
             // No includes supplied, so set it to 'matches all'
             includes = new String[1];
             includes[0] = "**";
         }
         if (excludes == null) {
             excludes = new String[0];
         }
 
         filesIncluded    = new Vector();
         filesNotIncluded = new Vector();
         filesExcluded    = new Vector();
         filesDeselected  = new Vector();
         dirsIncluded     = new Vector();
         dirsNotIncluded  = new Vector();
         dirsExcluded     = new Vector();
         dirsDeselected   = new Vector();
 
         if (isIncluded("")) {
             if (!isExcluded("")) {
                 if (isSelected("", basedir)) {
                     dirsIncluded.addElement("");
                 } else {
                     dirsDeselected.addElement("");
                 }
             } else {
                 dirsExcluded.addElement("");
             }
         } else {
             dirsNotIncluded.addElement("");
         }
         checkIncludePatterns();
         clearCaches();
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
 
         if (newroots.containsKey("")) {
             // we are going to scan everything anyway
             scandir(basedir, "", true);
         } else {
             // only scan directories that can include matched files or
             // directories
             Enumeration enum2 = newroots.keys();
 
             File canonBase = null;
             try {
                 canonBase = basedir.getCanonicalFile();
             } catch (IOException ex) {
                 throw new BuildException(ex);
             }
 
             while (enum2.hasMoreElements()) {
                 String currentelement = (String) enum2.nextElement();
                 String originalpattern = (String) newroots.get(currentelement);
                 File myfile = new File(basedir, currentelement);
 
                 if (myfile.exists()) {
                     // may be on a case insensitive file system.  We want
                     // the results to show what's really on the disk, so
                     // we need to double check.
                     try {
                         File canonFile = myfile.getCanonicalFile();
                         String path = FILE_UTILS.removeLeadingPath(canonBase,
                                                                   canonFile);
                         if (!path.equals(currentelement) || ON_VMS) {
                             myfile = findFile(basedir, currentelement);
                             if (myfile != null) {
                                 currentelement =
                                     FILE_UTILS.removeLeadingPath(basedir,
                                                                  myfile);
                             }
                         }
                     } catch (IOException ex) {
                         throw new BuildException(ex);
                     }
                 }
 
                 if ((myfile == null || !myfile.exists()) && !isCaseSensitive) {
                     File f = findFileCaseInsensitive(basedir, currentelement);
                     if (f.exists()) {
                         // adapt currentelement to the case we've
                         // actually found
                         currentelement = FILE_UTILS.removeLeadingPath(basedir,
                                                                      f);
                         myfile = f;
                     }
                 }
 
                 if (myfile != null && myfile.exists()) {
                     if (!followSymlinks
                         && isSymlink(basedir, currentelement)) {
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
                             scandir(myfile, currentelement, true);
                         }
                     } else {
                         if (isCaseSensitive
                             && originalpattern.equals(currentelement)) {
                             accountForIncludedFile(currentelement, myfile);
                         } else if (!isCaseSensitive
                                    && originalpattern
                                    .equalsIgnoreCase(currentelement)) {
                             accountForIncludedFile(currentelement, myfile);
                         }
                     }
                 }
             }
         }
     }
 
     /**
      * Top level invocation for a slow scan. A slow scan builds up a full
      * list of excluded/included files/directories, whereas a fast scan
      * will only have full results for included files, as it ignores
      * directories which can't possibly hold any included files/directories.
      * <p>
      * Returns immediately if a slow scan has already been completed.
      */
     protected void slowScan() {
         if (haveSlowResults) {
             return;
         }
 
         String[] excl = new String[dirsExcluded.size()];
         dirsExcluded.copyInto(excl);
 
         String[] notIncl = new String[dirsNotIncluded.size()];
         dirsNotIncluded.copyInto(notIncl);
 
         for (int i = 0; i < excl.length; i++) {
             if (!couldHoldIncluded(excl[i])) {
                 scandir(new File(basedir, excl[i]),
                         excl[i] + File.separator, false);
             }
         }
 
         for (int i = 0; i < notIncl.length; i++) {
             if (!couldHoldIncluded(notIncl[i])) {
                 scandir(new File(basedir, notIncl[i]),
                         notIncl[i] + File.separator, false);
             }
         }
 
         haveSlowResults  = true;
     }
 
     /**
      * Scans the given directory for files and directories. Found files and
      * directories are placed in their respective collections, based on the
      * matching of includes, excludes, and the selectors.  When a directory
      * is found, it is scanned recursively.
      *
      * @param dir   The directory to scan. Must not be <code>null</code>.
      * @param vpath The path relative to the base directory (needed to
      *              prevent problems with an absolute path when using
      *              dir). Must not be <code>null</code>.
      * @param fast  Whether or not this call is part of a fast scan.
      *
      * @see #filesIncluded
      * @see #filesNotIncluded
      * @see #filesExcluded
      * @see #dirsIncluded
      * @see #dirsNotIncluded
      * @see #dirsExcluded
      * @see #slowScan
      */
     protected void scandir(File dir, String vpath, boolean fast) {
         if (dir == null) {
             throw new BuildException("dir must not be null.");
         } else if (!dir.exists()) {
             throw new BuildException(dir + " doesn't exists.");
         } else if (!dir.isDirectory()) {
             throw new BuildException(dir + " is not a directory.");
         }
 
         // avoid double scanning of directories, can only happen in fast mode
         if (fast && hasBeenScanned(vpath)) {
             return;
         }
         String[] newfiles = dir.list();
 
         if (newfiles == null) {
             /*
              * two reasons are mentioned in the API docs for File.list
              * (1) dir is not a directory. This is impossible as
              *     we wouldn't get here in this case.
              * (2) an IO error occurred (why doesn't it throw an exception
              *     then???)
              */
             throw new BuildException("IO error scanning directory "
                                      + dir.getAbsolutePath());
         }
 
         if (!followSymlinks) {
             Vector noLinks = new Vector();
             for (int i = 0; i < newfiles.length; i++) {
                 try {
                     if (FILE_UTILS.isSymbolicLink(dir, newfiles[i])) {
                         String name = vpath + newfiles[i];
                         File   file = new File(dir, newfiles[i]);
                         if (file.isDirectory()) {
                             dirsExcluded.addElement(name);
                         } else {
                             filesExcluded.addElement(name);
                         }
                     } else {
                         noLinks.addElement(newfiles[i]);
                     }
                 } catch (IOException ioe) {
                     String msg = "IOException caught while checking "
                         + "for links, couldn't get canonical path!";
                     // will be caught and redirected to Ant's logging system
                     System.err.println(msg);
                     noLinks.addElement(newfiles[i]);
                 }
             }
             newfiles = new String[noLinks.size()];
             noLinks.copyInto(newfiles);
         }
 
         for (int i = 0; i < newfiles.length; i++) {
             String name = vpath + newfiles[i];
             File   file = new File(dir, newfiles[i]);
             if (file.isDirectory()) {
                 if (isIncluded(name)) {
                     accountForIncludedDir(name, file, fast);
                 } else {
                     everythingIncluded = false;
                     dirsNotIncluded.addElement(name);
                     if (fast && couldHoldIncluded(name)) {
                         scandir(file, name + File.separator, fast);
                     }
                 }
                 if (!fast) {
                     scandir(file, name + File.separator, fast);
                 }
             } else if (file.isFile()) {
                 if (isIncluded(name)) {
                     accountForIncludedFile(name, file);
                 } else {
                     everythingIncluded = false;
                     filesNotIncluded.addElement(name);
                 }
             }
         }
     }
     /**
      * process included file
      * @param name  path of the file relative to the directory of the fileset
      * @param file  included file
      */
     private void accountForIncludedFile(String name, File file) {
         if (!filesIncluded.contains(name)
             && !filesExcluded.contains(name)
             && !filesDeselected.contains(name)) {
 
             if (!isExcluded(name)) {
                 if (isSelected(name, file)) {
                     filesIncluded.addElement(name);
                 } else {
                     everythingIncluded = false;
                     filesDeselected.addElement(name);
                 }
             } else {
                 everythingIncluded = false;
                 filesExcluded.addElement(name);
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
     private void accountForIncludedDir(String name, File file, boolean fast) {
         if (!dirsIncluded.contains(name)
             && !dirsExcluded.contains(name)
             && !dirsDeselected.contains(name)) {
 
             if (!isExcluded(name)) {
                 if (isSelected(name, file)) {
                     dirsIncluded.addElement(name);
                     if (fast) {
                         scandir(file, name + File.separator, fast);
                     }
                 } else {
                     everythingIncluded = false;
                     dirsDeselected.addElement(name);
                     if (fast && couldHoldIncluded(name)) {
                         scandir(file, name + File.separator, fast);
                     }
                 }
 
             } else {
                 everythingIncluded = false;
                 dirsExcluded.addElement(name);
                 if (fast && couldHoldIncluded(name)) {
                     scandir(file, name + File.separator, fast);
                 }
             }
         }
     }
     /**
      * Tests whether or not a name matches against at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         include pattern, or <code>false</code> otherwise.
      */
     protected boolean isIncluded(String name) {
         for (int i = 0; i < includes.length; i++) {
             if (matchPath(includes[i], name, isCaseSensitive)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Tests whether or not a name matches the start of at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against the start of at
      *         least one include pattern, or <code>false</code> otherwise.
      */
     protected boolean couldHoldIncluded(String name) {
         for (int i = 0; i < includes.length; i++) {
             if (matchPatternStart(includes[i], name, isCaseSensitive)) {
                 if (isMorePowerfulThanExcludes(name, includes[i])) {
                     return true;
                 }
             }
         }
         return false;
     }
 
     /**
      *  find out whether one particular include pattern is more powerful
      *  than all the excludes
      *  note : the power comparison is based on the length of the include pattern
      *  and of the exclude patterns without the wildcards
      *  ideally the comparison should be done based on the depth
      *  of the match, that is to say how many file separators have been matched
      *  before the first ** or the end of the pattern
      *
      *  IMPORTANT : this function should return false "with care"
      *
      *  @param name the relative path that one want to test
      *  @param includepattern  one include pattern
      *  @return true if there is no exclude pattern more powerful than this include pattern
      *  @since ant1.6
      */
     private boolean isMorePowerfulThanExcludes(String name, String includepattern) {
         String soughtexclude = name + File.separator + "**";
         for (int counter = 0; counter < excludes.length; counter++) {
             if (excludes[counter].equals(soughtexclude))  {
                 return false;
             }
         }
         return true;
     }
     /**
      * Tests whether or not a name matches against at least one exclude
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         exclude pattern, or <code>false</code> otherwise.
      */
     protected boolean isExcluded(String name) {
         for (int i = 0; i < excludes.length; i++) {
             if (matchPath(excludes[i], name, isCaseSensitive)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Tests whether a name should be selected.
      *
      * @param name the filename to check for selecting
      * @param file the java.io.File object for this filename
      * @return <code>false</code> when the selectors says that the file
      *         should not be selected, <code>true</code> otherwise.
      */
     protected boolean isSelected(String name, File file) {
         if (selectors != null) {
             for (int i = 0; i < selectors.length; i++) {
                 if (!selectors[i].isSelected(basedir, name, file)) {
                     return false;
                 }
             }
         }
         return true;
     }
 
     /**
      * Returns the names of the files which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and none of the exclude patterns.
      */
     public String[] getIncludedFiles() {
         String[] files = new String[filesIncluded.size()];
         filesIncluded.copyInto(files);
         Arrays.sort(files);
         return files;
     }
 
     /**
+     * Return the count of included files.
+     * @return <CODE>int</CODE>.
+     * @since Ant 1.6.3
+     */
+    public int getIncludedFilesCount() {
+        if (filesIncluded == null) throw new IllegalStateException();
+        return filesIncluded.size();
+    }
+
+    /**
      * Returns the names of the files which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched none of the include
      *         patterns.
      *
      * @see #slowScan
      */
     public String[] getNotIncludedFiles() {
         slowScan();
         String[] files = new String[filesNotIncluded.size()];
         filesNotIncluded.copyInto(files);
         return files;
     }
 
     /**
      * Returns the names of the files which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public String[] getExcludedFiles() {
         slowScan();
         String[] files = new String[filesExcluded.size()];
         filesExcluded.copyInto(files);
         return files;
     }
 
     /**
      * <p>Returns the names of the files which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the files which were deselected.
      *
      * @see #slowScan
      */
     public String[] getDeselectedFiles() {
         slowScan();
         String[] files = new String[filesDeselected.size()];
         filesDeselected.copyInto(files);
         return files;
     }
 
     /**
      * Returns the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      */
     public String[] getIncludedDirectories() {
         String[] directories = new String[dirsIncluded.size()];
         dirsIncluded.copyInto(directories);
         Arrays.sort(directories);
         return directories;
     }
 
     /**
+     * Return the count of included directories.
+     * @return <CODE>int</CODE>.
+     * @since Ant 1.6.3
+     */
+    public int getIncludedDirsCount() {
+        if (dirsIncluded == null) throw new IllegalStateException();
+        return dirsIncluded.size();
+    }
+
+    /**
      * Returns the names of the directories which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched none of the include
      * patterns.
      *
      * @see #slowScan
      */
     public String[] getNotIncludedDirectories() {
         slowScan();
         String[] directories = new String[dirsNotIncluded.size()];
         dirsNotIncluded.copyInto(directories);
         return directories;
     }
 
     /**
      * Returns the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public String[] getExcludedDirectories() {
         slowScan();
         String[] directories = new String[dirsExcluded.size()];
         dirsExcluded.copyInto(directories);
         return directories;
     }
 
     /**
      * <p>Returns the names of the directories which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the directories which were deselected.
      *
      * @see #slowScan
      */
     public String[] getDeselectedDirectories() {
         slowScan();
         String[] directories = new String[dirsDeselected.size()];
         dirsDeselected.copyInto(directories);
         return directories;
     }
 
     /**
      * Adds default exclusions to the current exclusions set.
      */
     public void addDefaultExcludes() {
         int excludesLength = excludes == null ? 0 : excludes.length;
         String[] newExcludes;
         newExcludes = new String[excludesLength + defaultExcludes.size()];
         if (excludesLength > 0) {
             System.arraycopy(excludes, 0, newExcludes, 0, excludesLength);
         }
         String[] defaultExcludesTemp = getDefaultExcludes();
         for (int i = 0; i < defaultExcludesTemp.length; i++) {
             newExcludes[i + excludesLength] =
                 defaultExcludesTemp[i].replace('/', File.separatorChar)
                 .replace('\\', File.separatorChar);
         }
         excludes = newExcludes;
     }
 
     /**
      * Get the named resource
      * @param name path name of the file relative to the dir attribute.
      *
      * @return the resource with the given name.
      * @since Ant 1.5.2
      */
     public Resource getResource(String name) {
         File f = FILE_UTILS.resolveFile(basedir, name);
         return new Resource(name, f.exists(), f.lastModified(),
                             f.isDirectory());
     }
 
     /**
      * temporary table to speed up the various scanning methods below
      *
      * @since Ant 1.6
      */
     private Map fileListMap = new HashMap();
 
     /**
      * Returns a cached result of list performed on file, if
      * available.  Invokes the method and caches the result otherwise.
      *
      * @since Ant 1.6
      */
     private String[] list(File file) {
         String[] files = (String[]) fileListMap.get(file);
         if (files == null) {
             files = file.list();
             if (files != null) {
                 fileListMap.put(file, files);
             }
         }
         return files;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in a case
      * insensitive manner in order to find a file that matches the
      * given name.
      *
      * @return File object that points to the file in question.  if it
      * hasn't been found it will simply be <code>new File(base,
      * path)</code>.
      *
      * @since Ant 1.6
      */
     private File findFileCaseInsensitive(File base, String path) {
         File f = findFileCaseInsensitive(base,
                                          SelectorUtils.tokenizePath(path));
         return  f == null ? new File(base, path) : f;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in a case
      * insensitive manner in order to find a file that matches the
      * given stack of names.
      *
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6
      */
     private File findFileCaseInsensitive(File base, Vector pathElements) {
         if (pathElements.size() == 0) {
             return base;
         } else {
             if (!base.isDirectory()) {
                 return null;
             }
             String[] files = list(base);
             if (files == null) {
                 throw new BuildException("IO error scanning directory "
                                          + base.getAbsolutePath());
             }
             String current = (String) pathElements.remove(0);
             for (int i = 0; i < files.length; i++) {
                 if (files[i].equals(current)) {
                     base = new File(base, files[i]);
                     return findFileCaseInsensitive(base, pathElements);
                 }
             }
             for (int i = 0; i < files.length; i++) {
                 if (files[i].equalsIgnoreCase(current)) {
                     base = new File(base, files[i]);
                     return findFileCaseInsensitive(base, pathElements);
                 }
             }
         }
         return null;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given name.
      *
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6
      */
     private File findFile(File base, String path) {
         return findFile(base, SelectorUtils.tokenizePath(path));
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given stack of names.
      *
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6
      */
     private File findFile(File base, Vector pathElements) {
         if (pathElements.size() == 0) {
             return base;
         } else {
             if (!base.isDirectory()) {
                 return null;
             }
             String[] files = list(base);
             if (files == null) {
                 throw new BuildException("IO error scanning directory "
                                          + base.getAbsolutePath());
             }
             String current = (String) pathElements.remove(0);
             for (int i = 0; i < files.length; i++) {
                 if (files[i].equals(current)) {
                     base = new File(base, files[i]);
                     return findFile(base, pathElements);
                 }
             }
         }
         return null;
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, String path) {
         return isSymlink(base, SelectorUtils.tokenizePath(path));
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, Vector pathElements) {
         if (pathElements.size() > 0) {
             String current = (String) pathElements.remove(0);
             try {
                 if (FILE_UTILS.isSymbolicLink(base, current)) {
                     return true;
                 } else {
                     base = new File(base, current);
                     return isSymlink(base, pathElements);
                 }
             } catch (IOException ioe) {
                 String msg = "IOException caught while checking "
                     + "for links, couldn't get canonical path!";
                 // will be caught and redirected to Ant's logging system
                 System.err.println(msg);
                 return false;
             }
         }
         return false;
     }
 
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
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/ExecuteOn.java b/src/main/org/apache/tools/ant/taskdefs/ExecuteOn.java
index 75b3a4835..9126d1e4d 100644
--- a/src/main/org/apache/tools/ant/taskdefs/ExecuteOn.java
+++ b/src/main/org/apache/tools/ant/taskdefs/ExecuteOn.java
@@ -1,684 +1,669 @@
 /*
  * Copyright  2000-2004 The Apache Software Foundation.
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
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Hashtable;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.AbstractFileSet;
 import org.apache.tools.ant.types.DirSet;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileList;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.Mapper;
 import org.apache.tools.ant.util.FileNameMapper;
 import org.apache.tools.ant.util.SourceFileScanner;
 
 /**
  * Executes a given command, supplying a set of files as arguments.
  *
  * @since Ant 1.2
  *
  * @ant.task category="control" name="apply"
  */
 public class ExecuteOn extends ExecTask {
 
-    private class ExtendedDirectoryScanner extends DirectoryScanner {
-        public int getIncludedFilesCount() {
-            if (filesIncluded == null) throw new IllegalStateException();
-            return filesIncluded.size();
-        }
-
-        public int getIncludedDirsCount() {
-            if (dirsIncluded == null) throw new IllegalStateException();
-            return dirsIncluded.size();
-        }
-    }
-
     protected Vector filesets = new Vector(); // contains AbstractFileSet
                                               // (both DirSet and FileSet)
     private Vector filelists = new Vector();
     private boolean relative = false;
     private boolean parallel = false;
     private boolean forwardSlash = false;
     protected String type = "file";
     protected Commandline.Marker srcFilePos = null;
     private boolean skipEmpty = false;
     protected Commandline.Marker targetFilePos = null;
     protected Mapper mapperElement = null;
     protected FileNameMapper mapper = null;
     protected File destDir = null;
     private int maxParallel = -1;
     private boolean addSourceFile = true;
     private boolean verbose = false;
     private boolean ignoreMissing = true;
 
     /**
      * Has &lt;srcfile&gt; been specified before &lt;targetfile&gt;
      */
     protected boolean srcIsFirst = true;
 
     /**
      * Source files to operate upon.
      */
     public void addFileset(FileSet set) {
         filesets.addElement(set);
     }
 
     /**
      * Adds directories to operate on.
      *
      * @param  set the DirSet to add.
      *
      * @since Ant 1.6
      */
     public void addDirset(DirSet set) {
         filesets.addElement(set);
     }
     /**
      * Source files to operate upon.
      */
     public void addFilelist(FileList list) {
         filelists.addElement(list);
     }
 
     /**
      * Whether the filenames should be passed on the command line as
      * absolute or relative pathnames. Paths are relative to the base
      * directory of the corresponding fileset for source files or the
      * dest attribute for target files.
      */
     public void setRelative(boolean relative) {
         this.relative = relative;
     }
 
 
     /**
      * If true, run the command only once, appending all files as arguments.
      * If false, command will be executed once for every file. Defaults to false.
      */
     public void setParallel(boolean parallel) {
         this.parallel = parallel;
     }
 
     /**
      * Whether the command works only on files, directories or both?
      */
     public void setType(FileDirBoth type) {
         this.type = type.getValue();
     }
 
     /**
      * If no source files have been found or are newer than their
      * corresponding target files, do not run the command.
      */
     public void setSkipEmptyFilesets(boolean skip) {
         skipEmpty = skip;
     }
 
     /**
      * The directory where target files are to be placed.
      */
     public void setDest(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * The source and target file names on Windows and OS/2 must use
      * forward slash as file separator.
      */
     public void setForwardslash(boolean forwardSlash) {
         this.forwardSlash = forwardSlash;
     }
 
     /**
      * Limit the command line length by passing at maximum this many
      * sourcefiles at once to the command.
      *
      * <p>Set to &lt;= 0 for unlimited - this is the default.</p>
      *
      * @since Ant 1.6
      */
     public void setMaxParallel(int max) {
         maxParallel = max;
     }
 
     /**
      * Whether to send the source file name on the command line.
      *
      * <p>Defaults to <code>true</code>.
      *
      * @since Ant 1.6
      */
     public void setAddsourcefile(boolean b) {
         addSourceFile = b;
     }
 
     /**
      * Whether to print a verbose summary after execution.
      *
      * @since Ant 1.6
      */
     public void setVerbose(boolean b) {
         verbose = b;
     }
 
     /**
      * Whether to ignore nonexistent files from filelists.
      *
      * @since Ant 1.6.2
      */
     public void setIgnoremissing(boolean b) {
         ignoreMissing = b;
     }
 
     /**
      * Marker that indicates where the name of the source file should
      * be put on the command line.
      */
     public Commandline.Marker createSrcfile() {
         if (srcFilePos != null) {
             throw new BuildException(getTaskType() + " doesn\'t support multiple "
                                      + "srcfile elements.", getLocation());
         }
         srcFilePos = cmdl.createMarker();
         return srcFilePos;
     }
 
     /**
      * Marker that indicates where the name of the target file should
      * be put on the command line.
      */
     public Commandline.Marker createTargetfile() {
         if (targetFilePos != null) {
             throw new BuildException(getTaskType() + " doesn\'t support multiple "
                                      + "targetfile elements.", getLocation());
         }
         targetFilePos = cmdl.createMarker();
         srcIsFirst = (srcFilePos != null);
         return targetFilePos;
     }
 
     /**
      * Mapper to use for mapping source files to target files.
      */
     public Mapper createMapper() throws BuildException {
         if (mapperElement != null) {
             throw new BuildException("Cannot define more than one mapper",
                                      getLocation());
         }
         mapperElement = new Mapper(getProject());
         return mapperElement;
     }
 
     /**
      * @todo using taskName here is brittle, as a user could override it.
      *       this should probably be modified to use the classname instead.
      */
     protected void checkConfiguration() {
         if ("execon".equals(getTaskName())) {
             log("!! execon is deprecated. Use apply instead. !!");
         }
 
         super.checkConfiguration();
         if (filesets.size() == 0 && filelists.size() == 0) {
             throw new BuildException("no filesets and no filelists specified",
                                      getLocation());
         }
 
         if (targetFilePos != null || mapperElement != null
             || destDir != null) {
 
             if (mapperElement == null) {
                 throw new BuildException("no mapper specified", getLocation());
             }
             if (destDir == null) {
                 throw new BuildException("no dest attribute specified",
                                          getLocation());
             }
             mapper = mapperElement.getImplementation();
         }
     }
 
     protected ExecuteStreamHandler createHandler() throws BuildException {
         //if we have a RedirectorElement, return a decoy
         return (redirectorElement == null)
             ? super.createHandler() : new PumpStreamHandler();
     }
 
     protected void setupRedirector() {
         super.setupRedirector();
         redirector.setAppendProperties(true);
     }
 
     protected void runExec(Execute exe) throws BuildException {
         int totalFiles = 0;
         int totalDirs = 0;
         boolean haveExecuted = false;
         try {
 
             Vector fileNames = new Vector();
             Vector baseDirs = new Vector();
             for (int i = 0; i < filesets.size(); i++) {
                 String currentType = type;
                 AbstractFileSet fs = (AbstractFileSet) filesets.elementAt(i);
                 if (fs instanceof DirSet) {
                     if (!"dir".equals(type)) {
                         log("Found a nested dirset but type is " + type + ". "
                             + "Temporarily switching to type=\"dir\" on the"
                             + " assumption that you really did mean"
                             + " <dirset> not <fileset>.", Project.MSG_DEBUG);
                         currentType = "dir";
                     }
                 }
                 File base = fs.getDir(getProject());
 
-                ExtendedDirectoryScanner ds = new ExtendedDirectoryScanner();
-                fs.setupDirectoryScanner(ds, getProject());
-                ds.setFollowSymlinks(fs.isFollowSymlinks());
-                ds.scan();
+                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
 
                 if (!"dir".equals(currentType)) {
                     String[] s = getFiles(base, ds);
                     for (int j = 0; j < s.length; j++) {
                         totalFiles++;
                         fileNames.addElement(s[j]);
                         baseDirs.addElement(base);
                     }
                 }
 
                 if (!"file".equals(currentType)) {
                     String[] s = getDirs(base, ds);
                     for (int j = 0; j < s.length; j++) {
                         totalDirs++;
                         fileNames.addElement(s[j]);
                         baseDirs.addElement(base);
                     }
                 }
 
                 if (fileNames.size() == 0 && skipEmpty) {
                     int includedCount
                         = ((!"dir".equals(currentType))
                         ? ds.getIncludedFilesCount() : 0)
                         + ((!"file".equals(currentType))
                         ? ds.getIncludedDirsCount() : 0);
 
                     log("Skipping fileset for directory " + base + ". It is "
                         + ((includedCount > 0) ? "up to date." : "empty."),
                         Project.MSG_INFO);
                     continue;
                 }
 
                 if (!parallel) {
                     String[] s = new String[fileNames.size()];
                     fileNames.copyInto(s);
                     for (int j = 0; j < s.length; j++) {
                         String[] command = getCommandline(s[j], base);
                         log(Commandline.describeCommand(command),
                             Project.MSG_VERBOSE);
                         exe.setCommandline(command);
 
                         if (redirectorElement != null) {
                             setupRedirector();
                             redirectorElement.configure(redirector, s[j]);
                         }
 
                         if (redirectorElement != null || haveExecuted) {
                             // need to reset the stream handler to restart
                             // reading of pipes;
                             // go ahead and do it always w/ nested redirectors
                             exe.setStreamHandler(redirector.createHandler());
                         }
                         runExecute(exe);
                         haveExecuted = true;
                     }
                     fileNames.removeAllElements();
                     baseDirs.removeAllElements();
                 }
             }
 
             for (int i = 0; i < filelists.size(); i++) {
                 FileList list = (FileList) filelists.elementAt(i);
                 File base = list.getDir(getProject());
                 String[] names = getFilesAndDirs(list);
 
                 for (int j = 0; j < names.length; j++) {
                     File f = new File(base, names[j]);
                     if ((!ignoreMissing) || (f.isFile() && !"dir".equals(type))
                         || (f.isDirectory() && !"file".equals(type))) {
 
                         if (ignoreMissing || f.isFile()) {
                             totalFiles++;
                         } else {
                             totalDirs++;
                         }
 
                         fileNames.addElement(names[j]);
                         baseDirs.addElement(base);
                     }
                 }
 
                 if (fileNames.size() == 0 && skipEmpty) {
-                    ExtendedDirectoryScanner ds = new ExtendedDirectoryScanner();
+                    DirectoryScanner ds = new DirectoryScanner();
                     ds.setBasedir(base);
                     ds.setIncludes(list.getFiles(getProject()));
                     ds.scan();
                     int includedCount
                         = ds.getIncludedFilesCount() + ds.getIncludedDirsCount();
 
                     log("Skipping filelist for directory " + base + ". It is "
                         + ((includedCount > 0) ? "up to date." : "empty."),
                         Project.MSG_INFO);
                     continue;
                 }
 
                 if (!parallel) {
                     String[] s = new String[fileNames.size()];
                     fileNames.copyInto(s);
                     for (int j = 0; j < s.length; j++) {
                         String[] command = getCommandline(s[j], base);
                         log(Commandline.describeCommand(command),
                             Project.MSG_VERBOSE);
                         exe.setCommandline(command);
 
                         if (redirectorElement != null) {
                             setupRedirector();
                             redirectorElement.configure(redirector, s[j]);
                         }
 
                         if (redirectorElement != null || haveExecuted) {
                             // need to reset the stream handler to restart
                             // reading of pipes;
                             // go ahead and do it always w/ nested redirectors
                             exe.setStreamHandler(redirector.createHandler());
                         }
                         runExecute(exe);
                         haveExecuted = true;
                     }
                     fileNames.removeAllElements();
                     baseDirs.removeAllElements();
                 }
             }
 
             if (parallel && (fileNames.size() > 0 || !skipEmpty)) {
                 runParallel(exe, fileNames, baseDirs);
                 haveExecuted = true;
             }
 
             if (haveExecuted) {
                 log("Applied " + cmdl.getExecutable() + " to "
                     + totalFiles + " file"
                     + (totalFiles != 1 ? "s" : "") + " and "
                     + totalDirs + " director"
                     + (totalDirs != 1 ? "ies" : "y") + ".",
                     verbose ? Project.MSG_INFO : Project.MSG_VERBOSE);
             }
 
         } catch (IOException e) {
             throw new BuildException("Execute failed: " + e, e, getLocation());
         } finally {
             // close the output file if required
             logFlush();
             redirector.setAppendProperties(false);
             redirector.setProperties();
         }
     }
 
     /**
      * Construct the command line for parallel execution.
      *
      * @param srcFiles The filenames to add to the commandline
      * @param baseDirs filenames are relative to this dir
      */
     protected String[] getCommandline(String[] srcFiles, File[] baseDirs) {
         final char fileSeparator = File.separatorChar;
         Vector targets = new Vector();
         if (targetFilePos != null) {
             Hashtable addedFiles = new Hashtable();
             for (int i = 0; i < srcFiles.length; i++) {
                 String[] subTargets = mapper.mapFileName(srcFiles[i]);
                 if (subTargets != null) {
                     for (int j = 0; j < subTargets.length; j++) {
                         String name = null;
                         if (!relative) {
                             name = (new File(destDir, subTargets[j])).getAbsolutePath();
                         } else {
                             name = subTargets[j];
                         }
                         if (forwardSlash && fileSeparator != '/') {
                             name = name.replace(fileSeparator, '/');
                         }
                         if (!addedFiles.contains(name)) {
                             targets.addElement(name);
                             addedFiles.put(name, name);
                         }
                     }
                 }
             }
         }
         String[] targetFiles = new String[targets.size()];
         targets.copyInto(targetFiles);
 
         if (!addSourceFile) {
             srcFiles = new String[0];
         }
 
         String[] orig = cmdl.getCommandline();
         String[] result
             = new String[orig.length + srcFiles.length + targetFiles.length];
 
         int srcIndex = orig.length;
         if (srcFilePos != null) {
             srcIndex = srcFilePos.getPosition();
         }
 
         if (targetFilePos != null) {
             int targetIndex = targetFilePos.getPosition();
 
             if (srcIndex < targetIndex
                 || (srcIndex == targetIndex && srcIsFirst)) {
 
                 // 0 --> srcIndex
                 System.arraycopy(orig, 0, result, 0, srcIndex);
 
                 // srcIndex --> targetIndex
                 System.arraycopy(orig, srcIndex, result,
                                  srcIndex + srcFiles.length,
                                  targetIndex - srcIndex);
 
                 // targets are already absolute file names
                 System.arraycopy(targetFiles, 0, result,
                                  targetIndex + srcFiles.length,
                                  targetFiles.length);
 
                 // targetIndex --> end
                 System.arraycopy(orig, targetIndex, result,
                     targetIndex + srcFiles.length + targetFiles.length,
                     orig.length - targetIndex);
             } else {
                 // 0 --> targetIndex
                 System.arraycopy(orig, 0, result, 0, targetIndex);
 
                 // targets are already absolute file names
                 System.arraycopy(targetFiles, 0, result,
                                  targetIndex,
                                  targetFiles.length);
 
                 // targetIndex --> srcIndex
                 System.arraycopy(orig, targetIndex, result,
                                  targetIndex + targetFiles.length,
                                  srcIndex - targetIndex);
 
                 // srcIndex --> end
                 System.arraycopy(orig, srcIndex, result,
                     srcIndex + srcFiles.length + targetFiles.length,
                     orig.length - srcIndex);
                 srcIndex += targetFiles.length;
             }
 
         } else { // no targetFilePos
 
             // 0 --> srcIndex
             System.arraycopy(orig, 0, result, 0, srcIndex);
             // srcIndex --> end
             System.arraycopy(orig, srcIndex, result,
                              srcIndex + srcFiles.length,
                              orig.length - srcIndex);
 
         }
 
         // fill in source file names
         for (int i = 0; i < srcFiles.length; i++) {
             if (!relative) {
                 result[srcIndex + i] =
                     (new File(baseDirs[i], srcFiles[i])).getAbsolutePath();
             } else {
                 result[srcIndex + i] = srcFiles[i];
             }
             if (forwardSlash && fileSeparator != '/') {
                 result[srcIndex + i] =
                     result[srcIndex + i].replace(fileSeparator, '/');
             }
         }
         return result;
     }
 
     /**
      * Construct the command line for serial execution.
      *
      * @param srcFile The filename to add to the commandline
      * @param baseDir filename is relative to this dir
      */
     protected String[] getCommandline(String srcFile, File baseDir) {
         return getCommandline(new String[] {srcFile}, new File[] {baseDir});
     }
 
     /**
      * Return the list of files from this DirectoryScanner that should
      * be included on the command line.
      */
     protected String[] getFiles(File baseDir, DirectoryScanner ds) {
         if (mapper != null) {
             SourceFileScanner sfs = new SourceFileScanner(this);
             return sfs.restrict(ds.getIncludedFiles(), baseDir, destDir,
                                 mapper);
         } else {
             return ds.getIncludedFiles();
         }
     }
 
     /**
      * Return the list of Directories from this DirectoryScanner that
      * should be included on the command line.
      */
     protected String[] getDirs(File baseDir, DirectoryScanner ds) {
         if (mapper != null) {
             SourceFileScanner sfs = new SourceFileScanner(this);
             return sfs.restrict(ds.getIncludedDirectories(), baseDir, destDir,
                                 mapper);
         } else {
             return ds.getIncludedDirectories();
         }
     }
 
     /**
      * Return the list of files or directories from this FileList that
      * should be included on the command line.
      *
      * @since Ant 1.6.2
      */
     protected String[] getFilesAndDirs(FileList list) {
         if (mapper != null) {
             SourceFileScanner sfs = new SourceFileScanner(this);
             return sfs.restrict(list.getFiles(getProject()),
                                 list.getDir(getProject()), destDir,
                                 mapper);
         } else {
             return list.getFiles(getProject());
         }
     }
 
     /**
      * Runs the command in "parallel" mode, making sure that at most
      * maxParallel sourcefiles get passed on the command line.
      *
      * @since Ant 1.6
      */
     protected void runParallel(Execute exe, Vector fileNames,
                                Vector baseDirs)
         throws IOException, BuildException {
         String[] s = new String[fileNames.size()];
         fileNames.copyInto(s);
         File[] b = new File[baseDirs.size()];
         baseDirs.copyInto(b);
 
         if (maxParallel <= 0
             || s.length == 0 /* this is skipEmpty == false */) {
             String[] command = getCommandline(s, b);
             log(Commandline.describeCommand(command), Project.MSG_VERBOSE);
             exe.setCommandline(command);
             runExecute(exe);
         } else {
             int stillToDo = fileNames.size();
             int currentOffset = 0;
             while (stillToDo > 0) {
                 int currentAmount = Math.min(stillToDo, maxParallel);
                 String[] cs = new String[currentAmount];
                 System.arraycopy(s, currentOffset, cs, 0, currentAmount);
                 File[] cb = new File[currentAmount];
                 System.arraycopy(b, currentOffset, cb, 0, currentAmount);
                 String[] command = getCommandline(cs, cb);
                 log(Commandline.describeCommand(command), Project.MSG_VERBOSE);
                 exe.setCommandline(command);
                 if (redirectorElement != null) {
                     setupRedirector();
                     redirectorElement.configure(redirector, null);
                 }
 
                 if (redirectorElement != null || currentOffset > 0) {
                     // need to reset the stream handler to restart
                     // reading of pipes;
                     // go ahead and do it always w/ nested redirectors
                     exe.setStreamHandler(redirector.createHandler());
                 }
                 runExecute(exe);
 
                 stillToDo -= currentAmount;
                 currentOffset += currentAmount;
             }
         }
     }
 
     /**
      * Enumerated attribute with the values "file", "dir" and "both"
      * for the type attribute.
      */
     public static class FileDirBoth extends EnumeratedAttribute {
         /**
          * @see EnumeratedAttribute#getValues
          */
         public String[] getValues() {
             return new String[] {"file", "dir", "both"};
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/types/optional/depend/DependScanner.java b/src/main/org/apache/tools/ant/types/optional/depend/DependScanner.java
index 8fc39cd11..4964e83dc 100644
--- a/src/main/org/apache/tools/ant/types/optional/depend/DependScanner.java
+++ b/src/main/org/apache/tools/ant/types/optional/depend/DependScanner.java
@@ -1,193 +1,204 @@
 /*
  * Copyright  2001-2002,2004 The Apache Software Foundation
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
 package org.apache.tools.ant.types.optional.depend;
 
 import java.io.File;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.util.depend.DependencyAnalyzer;
 
 
 /**
  * An interface used to describe the actions required by any type of
  * directory scanner.
  *
  */
 public class DependScanner extends DirectoryScanner {
     /**
      * The name of the analyzer to use by default.
      */
     public static final String DEFAULT_ANALYZER_CLASS
         = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
 
     /**
      * The root classes to drive the search for dependent classes
      */
     private Vector rootClasses;
 
     /**
      * The names of the classes to include in the fileset
      */
     private Vector included;
 
     /**
      * The parent scanner which gives the basic set of files. Only files which
      * are in this set and which can be reached from a root class will end
      * up being included in the result set
      */
     private DirectoryScanner parentScanner;
 
     /**
      * Create a DependScanner, using the given scanner to provide the basic
      * set of files from which class files come.
      *
      * @param parentScanner the DirectoryScanner which returns the files from
      *        which class files must come.
      */
     public DependScanner(DirectoryScanner parentScanner) {
         this.parentScanner = parentScanner;
     }
 
     /**
      * Sets the root classes to be used to drive the scan.
      *
      * @param rootClasses the rootClasses to be used for this scan
      */
     public void setRootClasses(Vector rootClasses) {
         this.rootClasses = rootClasses;
     }
 
     /**
      * Get the names of the class files, baseClass depends on
      *
      * @return the names of the files
      */
     public String[] getIncludedFiles() {
         int count = included.size();
         String[] files = new String[count];
         for (int i = 0; i < count; i++) {
             files[i] = (String) included.elementAt(i);
         }
         return files;
     }
 
+    //inherit doc
+    public int getIncludedFilesCount() {
+        if (included == null) throw new IllegalStateException();
+        return included.size();
+    }
+
     /**
      * Scans the base directory for files that baseClass depends on
      *
      * @exception IllegalStateException when basedir was set incorrecly
      */
     public void scan() throws IllegalStateException {
         included = new Vector();
         String analyzerClassName = DEFAULT_ANALYZER_CLASS;
         DependencyAnalyzer analyzer = null;
         try {
             Class analyzerClass = Class.forName(analyzerClassName);
             analyzer = (DependencyAnalyzer) analyzerClass.newInstance();
         } catch (Exception e) {
             throw new BuildException("Unable to load dependency analyzer: "
                 + analyzerClassName, e);
         }
         analyzer.addClassPath(new Path(null, basedir.getPath()));
 
         for (Enumeration e = rootClasses.elements(); e.hasMoreElements();) {
             String rootClass = (String) e.nextElement();
             analyzer.addRootClass(rootClass);
         }
 
         Enumeration e = analyzer.getClassDependencies();
 
         String[] parentFiles = parentScanner.getIncludedFiles();
         Hashtable parentSet = new Hashtable();
         for (int i = 0; i < parentFiles.length; ++i) {
             parentSet.put(parentFiles[i], parentFiles[i]);
         }
 
         while (e.hasMoreElements()) {
             String classname = (String) e.nextElement();
             String filename = classname.replace('.', File.separatorChar);
             filename = filename + ".class";
             File depFile = new File(basedir, filename);
             if (depFile.exists() && parentSet.containsKey(filename)) {
                 // This is included
                 included.addElement(filename);
             }
         }
     }
 
     /**
      * @see DirectoryScanner#addDefaultExcludes
      */
     public void addDefaultExcludes() {
     }
 
     /**
      * @see DirectoryScanner#getExcludedDirectories
      */
     public String[] getExcludedDirectories() {
         return null;
     }
 
     /**
      * @see DirectoryScanner#getExcludedFiles
      */
     public String[] getExcludedFiles() {
         return null;
     }
 
     /**
      * @see DirectoryScanner#getIncludedDirectories
      */
     public String[] getIncludedDirectories() {
         return new String[0];
     }
 
+    //inherit doc
+    public int getIncludedDirsCount() {
+        return 0;
+    }
+
     /**
      * @see DirectoryScanner#getNotIncludedDirectories
      */
     public String[] getNotIncludedDirectories() {
         return null;
     }
 
     /**
      * @see DirectoryScanner#getNotIncludedFiles
      */
     public String[] getNotIncludedFiles() {
         return null;
     }
 
     /**
      * @see DirectoryScanner#setExcludes
      */
     public void setExcludes(String[] excludes) {
     }
 
     /**
      * @see DirectoryScanner#setIncludes
      */
     public void setIncludes(String[] includes) {
     }
 
     /**
      * @see DirectoryScanner#setCaseSensitive
      */
     public void setCaseSensitive(boolean isCaseSensitive) {
     }
 }
