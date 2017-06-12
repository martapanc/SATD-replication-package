diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index d880e7b42..edb82c3af 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -1,1391 +1,1399 @@
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
 
 package org.apache.tools.ant;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Map;
 import java.util.Set;
 import java.util.Vector;
 
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceFactory;
 import org.apache.tools.ant.types.selectors.FileSelector;
 import org.apache.tools.ant.types.selectors.SelectorScanner;
 import org.apache.tools.ant.types.selectors.SelectorUtils;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Class for scanning a directory for files/directories which match certain
  * criteria.
  * <p>
  * These criteria consist of selectors and patterns which have been specified.
  * With the selectors you can select which files you want to have included.
  * Files which are not selected are excluded. With patterns you can include
  * or exclude files based on their filename.
  * <p>
  * The idea is simple. A given directory is recursively scanned for all files
  * and directories. Each file/directory is matched against a set of selectors,
  * including special support for matching against filenames with include and
  * and exclude patterns. Only files/directories which match at least one
  * pattern of the include pattern list or other file selector, and don't match
  * any pattern of the exclude pattern list or fail to match against a required
  * selector will be placed in the list of files/directories found.
  * <p>
  * When no list of include patterns is supplied, "**" will be used, which
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
  * @author Arnout J. Kuiper
  * <a href="mailto:ajkuiper@wxs.nl">ajkuiper@wxs.nl</a>
  * @author Magesh Umasankar
  * @author <a href="mailto:bruce@callenish.com">Bruce Atherton</a>
  * @author <a href="mailto:levylambert@tiscali-dsl.de">Antoine Levy-Lambert</a>
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
     private static final FileUtils fileUtils = FileUtils.newFileUtils();
 
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
                         String path = fileUtils.removeLeadingPath(canonBase,
                                                                   canonFile);
                         if (!path.equals(currentelement) || ON_VMS) {
                             myfile = findFile(basedir, currentelement);
                             if (myfile != null) {
                                 currentelement =
                                     fileUtils.removeLeadingPath(basedir,
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
                         currentelement = fileUtils.removeLeadingPath(basedir,
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
+        if (dir == null) {
+            throw new BuildException("dir must not be null.");
+        } else if (!dir.exists()) {
+            throw new BuildException(dir + " doesn't exists.");
+        } else if (!dir.isDirectory()) {
+            throw new BuildException(dir + " is not a directory.");
+        }
+
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
                     if (fileUtils.isSymbolicLink(dir, newfiles[i])) {
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
         File f = fileUtils.resolveFile(basedir, name);
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
                 if (fileUtils.isSymbolicLink(base, current)) {
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
diff --git a/src/main/org/apache/tools/ant/taskdefs/Javac.java b/src/main/org/apache/tools/ant/taskdefs/Javac.java
index 1e815b12f..7953508f5 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Javac.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Javac.java
@@ -1,968 +1,968 @@
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
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
 import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.GlobPatternMapper;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.util.SourceFileScanner;
 import org.apache.tools.ant.util.facade.FacadeTaskHelper;
 
 /**
  * Compiles Java source files. This task can take the following
  * arguments:
  * <ul>
  * <li>sourcedir
  * <li>destdir
  * <li>deprecation
  * <li>classpath
  * <li>bootclasspath
  * <li>extdirs
  * <li>optimize
  * <li>debug
  * <li>encoding
  * <li>target
  * <li>depend
  * <li>verbose
  * <li>failonerror
  * <li>includeantruntime
  * <li>includejavaruntime
  * <li>source
  * <li>compiler
  * </ul>
  * Of these arguments, the <b>sourcedir</b> and <b>destdir</b> are required.
  * <p>
  * When this task executes, it will recursively scan the sourcedir and
  * destdir looking for Java source files to compile. This task makes its
  * compile decision based on timestamp.
  *
  * @author James Davidson <a href="mailto:duncan@x180.com">duncan@x180.com</a>
  * @author Robin Green
  *         <a href="mailto:greenrd@hotmail.com">greenrd@hotmail.com</a>
  * @author Stefan Bodewig
  * @author <a href="mailto:jayglanville@home.com">J D Glanville</a>
  *
  * @version $Revision$
  *
  * @since Ant 1.1
  *
  * @ant.task category="java"
  */
 
 public class Javac extends MatchingTask {
 
     private static final String FAIL_MSG
         = "Compile failed; see the compiler error output for details.";
 
     private Path src;
     private File destDir;
     private Path compileClasspath;
     private Path compileSourcepath;
     private String encoding;
     private boolean debug = false;
     private boolean optimize = false;
     private boolean deprecation = false;
     private boolean depend = false;
     private boolean verbose = false;
     private String target;
     private Path bootclasspath;
     private Path extdirs;
     private boolean includeAntRuntime = true;
     private boolean includeJavaRuntime = false;
     private boolean fork = false;
     private String forkedExecutable = null;
     private boolean nowarn = false;
     private String memoryInitialSize;
     private String memoryMaximumSize;
     private FacadeTaskHelper facade = null;
 
     protected boolean failOnError = true;
     protected boolean listFiles = false;
     protected File[] compileList = new File[0];
 
     private String source;
     private String debugLevel;
     private File tmpDir;
 
     /**
      * Javac task for compilation of Java files.
      */
     public Javac() {
         if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)) {
             facade = new FacadeTaskHelper("javac1.1");
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)) {
             facade = new FacadeTaskHelper("javac1.2");
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) {
             facade = new FacadeTaskHelper("javac1.3");
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_4)) {
             facade = new FacadeTaskHelper("javac1.4");
         } else if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_5)) {
             facade = new FacadeTaskHelper("javac1.5");
         } else {
             facade = new FacadeTaskHelper("classic");
         }
     }
 
     /**
      * Get the value of debugLevel.
      * @return value of debugLevel.
      */
     public String getDebugLevel() {
         return debugLevel;
     }
 
     /**
      * Keyword list to be appended to the -g command-line switch.
      *
      * This will be ignored by all implementations except modern
      * and classic(ver >= 1.2). Legal values are none or a
      * comma-separated list of the following keywords: lines, vars,
      * and source. If debuglevel is not specified, by default, :none
      * will be appended to -g. If debug is not turned on, this attribute
      * will be ignored.
      *
      * @param v  Value to assign to debugLevel.
      */
     public void setDebugLevel(String  v) {
         this.debugLevel = v;
     }
 
     /**
      * Get the value of source.
      * @return value of source.
      */
     public String getSource() {
         return source;
     }
 
     /**
      * Value of the -source command-line switch; will be ignored
      * by all implementations except modern and jikes.
      *
      * If you use this attribute together with jikes, you must
      * make sure that your version of jikes supports the -source switch.
      * Legal values are 1.3, 1.4 and 1.5 - by default, no -source argument
      * will be used at all.
      *
      * @param v  Value to assign to source.
      */
     public void setSource(String  v) {
         this.source = v;
     }
 
     /**
      * Adds a path for source compilation.
      *
      * @return a nested src element.
      */
     public Path createSrc() {
         if (src == null) {
             src = new Path(getProject());
         }
         return src.createPath();
     }
 
     /**
      * Recreate src.
      *
      * @return a nested src element.
      */
     protected Path recreateSrc() {
         src = null;
         return createSrc();
     }
 
     /**
      * Set the source directories to find the source Java files.
      * @param srcDir the source directories as a path
      */
     public void setSrcdir(Path srcDir) {
         if (src == null) {
             src = srcDir;
         } else {
             src.append(srcDir);
         }
     }
 
     /**
      * Gets the source dirs to find the source java files.
      * @return the source directories as a path
      */
     public Path getSrcdir() {
         return src;
     }
 
     /**
      * Set the destination directory into which the Java source
      * files should be compiled.
      * @param destDir the destination director
      */
     public void setDestdir(File destDir) {
         this.destDir = destDir;
     }
 
     /**
      * Gets the destination directory into which the java source files
      * should be compiled.
      * @return the destination directory
      */
     public File getDestdir() {
         return destDir;
     }
 
     /**
      * Set the sourcepath to be used for this compilation.
      * @param sourcepath the source path
      */
     public void setSourcepath(Path sourcepath) {
         if (compileSourcepath == null) {
             compileSourcepath = sourcepath;
         } else {
             compileSourcepath.append(sourcepath);
         }
     }
 
     /**
      * Gets the sourcepath to be used for this compilation.
      * @return the source path
      */
     public Path getSourcepath() {
         return compileSourcepath;
     }
 
     /**
      * Adds a path to sourcepath.
      * @return a sourcepath to be configured
      */
     public Path createSourcepath() {
         if (compileSourcepath == null) {
             compileSourcepath = new Path(getProject());
         }
         return compileSourcepath.createPath();
     }
 
     /**
      * Adds a reference to a source path defined elsewhere.
      * @param r a reference to a source path
      */
     public void setSourcepathRef(Reference r) {
         createSourcepath().setRefid(r);
     }
 
     /**
      * Set the classpath to be used for this compilation.
      *
      * @param classpath an Ant Path object containing the compilation classpath.
      */
     public void setClasspath(Path classpath) {
         if (compileClasspath == null) {
             compileClasspath = classpath;
         } else {
             compileClasspath.append(classpath);
         }
     }
 
     /**
      * Gets the classpath to be used for this compilation.
      * @return the class path
      */
     public Path getClasspath() {
         return compileClasspath;
     }
 
     /**
      * Adds a path to the classpath.
      * @return a class path to be configured
      */
     public Path createClasspath() {
         if (compileClasspath == null) {
             compileClasspath = new Path(getProject());
         }
         return compileClasspath.createPath();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      * @param r a reference to a classpath
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * Sets the bootclasspath that will be used to compile the classes
      * against.
      * @param bootclasspath a path to use as a boot class path (may be more
      *                      than one)
      */
     public void setBootclasspath(Path bootclasspath) {
         if (this.bootclasspath == null) {
             this.bootclasspath = bootclasspath;
         } else {
             this.bootclasspath.append(bootclasspath);
         }
     }
 
     /**
      * Gets the bootclasspath that will be used to compile the classes
      * against.
      * @return the boot path
      */
     public Path getBootclasspath() {
         return bootclasspath;
     }
 
     /**
      * Adds a path to the bootclasspath.
      * @return a path to be configured
      */
     public Path createBootclasspath() {
         if (bootclasspath == null) {
             bootclasspath = new Path(getProject());
         }
         return bootclasspath.createPath();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      * @param r a reference to a classpath
      */
     public void setBootClasspathRef(Reference r) {
         createBootclasspath().setRefid(r);
     }
 
     /**
      * Sets the extension directories that will be used during the
      * compilation.
      * @param extdirs a path
      */
     public void setExtdirs(Path extdirs) {
         if (this.extdirs == null) {
             this.extdirs = extdirs;
         } else {
             this.extdirs.append(extdirs);
         }
     }
 
     /**
      * Gets the extension directories that will be used during the
      * compilation.
      * @return the extension directories as a path
      */
     public Path getExtdirs() {
         return extdirs;
     }
 
     /**
      * Adds a path to extdirs.
      * @return a path to be configured
      */
     public Path createExtdirs() {
         if (extdirs == null) {
             extdirs = new Path(getProject());
         }
         return extdirs.createPath();
     }
 
     /**
      * If true, list the source files being handed off to the compiler.
      * @param list if true list the source files
      */
     public void setListfiles(boolean list) {
         listFiles = list;
     }
 
     /**
      * Get the listfiles flag.
      * @return the listfiles flag
      */
     public boolean getListfiles() {
         return listFiles;
     }
 
     /**
      * Indicates whether the build will continue
      * even if there are compilation errors; defaults to true.
      * @param fail if true halt the build on failure
      */
     public void setFailonerror(boolean fail) {
         failOnError = fail;
     }
 
     /**
      * @ant.attribute ignore="true"
      * @param proceed inverse of failoferror
      */
     public void setProceed(boolean proceed) {
         failOnError = !proceed;
     }
 
     /**
      * Gets the failonerror flag.
      * @return the failonerror flag
      */
     public boolean getFailonerror() {
         return failOnError;
     }
 
     /**
      * Indicates whether source should be
      * compiled with deprecation information; defaults to off.
      * @param deprecation if true turn on deprecation information
      */
     public void setDeprecation(boolean deprecation) {
         this.deprecation = deprecation;
     }
 
     /**
      * Gets the deprecation flag.
      * @return the deprecation flag
      */
     public boolean getDeprecation() {
         return deprecation;
     }
 
     /**
      * The initial size of the memory for the underlying VM
      * if javac is run externally; ignored otherwise.
      * Defaults to the standard VM memory setting.
      * (Examples: 83886080, 81920k, or 80m)
      * @param memoryInitialSize string to pass to VM
      */
     public void setMemoryInitialSize(String memoryInitialSize) {
         this.memoryInitialSize = memoryInitialSize;
     }
 
     /**
      * Gets the memoryInitialSize flag.
      * @return the memoryInitialSize flag
      */
     public String getMemoryInitialSize() {
         return memoryInitialSize;
     }
 
     /**
      * The maximum size of the memory for the underlying VM
      * if javac is run externally; ignored otherwise.
      * Defaults to the standard VM memory setting.
      * (Examples: 83886080, 81920k, or 80m)
      * @param memoryMaximumSize string to pass to VM
      */
     public void setMemoryMaximumSize(String memoryMaximumSize) {
         this.memoryMaximumSize = memoryMaximumSize;
     }
 
     /**
      * Gets the memoryMaximumSize flag.
      * @return the memoryMaximumSize flag
      */
     public String getMemoryMaximumSize() {
         return memoryMaximumSize;
     }
 
     /**
      * Set the Java source file encoding name.
      * @param encoding the source file encoding
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * Gets the java source file encoding name.
      * @return the source file encoding name
      */
     public String getEncoding() {
         return encoding;
     }
 
     /**
      * Indicates whether source should be compiled
      * with debug information; defaults to off.
      * @param debug if true compile with debug information
      */
     public void setDebug(boolean debug) {
         this.debug = debug;
     }
 
     /**
      * Gets the debug flag.
      * @return the debug flag
      */
     public boolean getDebug() {
         return debug;
     }
 
     /**
      * If true, compiles with optimization enabled.
      * @param optimize if true compile with optimization enabled
      */
     public void setOptimize(boolean optimize) {
         this.optimize = optimize;
     }
 
     /**
      * Gets the optimize flag.
      * @return the optimize flag
      */
     public boolean getOptimize() {
         return optimize;
     }
 
     /**
      * Enables dependency-tracking for compilers
      * that support this (jikes and classic).
      * @param depend if true enable dependency-tracking
      */
     public void setDepend(boolean depend) {
         this.depend = depend;
     }
 
     /**
      * Gets the depend flag.
      * @return the depend flag
      */
     public boolean getDepend() {
         return depend;
     }
 
     /**
      * If true, asks the compiler for verbose output.
      * @param verbose if true, asks the compiler for verbose output
      */
     public void setVerbose(boolean verbose) {
         this.verbose = verbose;
     }
 
     /**
      * Gets the verbose flag.
      * @return the verbose flag
      */
     public boolean getVerbose() {
         return verbose;
     }
 
     /**
      * Sets the target VM that the classes will be compiled for. Valid
      * values depend on the compiler, for jdk 1.4 the valid values are
-     * "1.1", "1.2", "1.3" and "1.4".
+     * "1.1", "1.2", "1.3", "1.4" and "1.5".
      * @param target the target VM
      */
     public void setTarget(String target) {
         this.target = target;
     }
 
     /**
      * Gets the target VM that the classes will be compiled for.
      * @return the target VM
      */
     public String getTarget() {
         return target;
     }
 
     /**
      * If true, includes Ant's own classpath in the classpath.
      * @param include if true, includes Ant's own classpath in the classpath
      */
     public void setIncludeantruntime(boolean include) {
         includeAntRuntime = include;
     }
 
     /**
      * Gets whether or not the ant classpath is to be included in the classpath.
      * @return whether or not the ant classpath is to be included in the classpath
      */
     public boolean getIncludeantruntime() {
         return includeAntRuntime;
     }
 
     /**
      * If true, includes the Java runtime libraries in the classpath.
      * @param include if true, includes the Java runtime libraries in the classpath
      */
     public void setIncludejavaruntime(boolean include) {
         includeJavaRuntime = include;
     }
 
     /**
      * Gets whether or not the java runtime should be included in this
      * task's classpath.
      * @return the includejavaruntime attribute
      */
     public boolean getIncludejavaruntime() {
         return includeJavaRuntime;
     }
 
     /**
      * If true, forks the javac compiler.
      *
      * @param f "true|false|on|off|yes|no"
      */
     public void setFork(boolean f) {
         fork = f;
     }
 
     /**
      * Sets the name of the javac executable.
      *
      * <p>Ignored unless fork is true or extJavac has been specified
      * as the compiler.</p>
      * @param forkExec the name of the executable
      */
     public void setExecutable(String forkExec) {
         forkedExecutable = forkExec;
     }
 
     /**
      * The value of the executable attribute, if any.
      *
      * @since Ant 1.6
      * @return the name of the java executable
      */
     public String getExecutable() {
         return forkedExecutable;
     }
 
     /**
      * Is this a forked invocation of JDK's javac?
      * @return true if this is a forked invocation
      */
     public boolean isForkedJavac() {
         return fork || "extJavac".equals(getCompiler());
     }
 
     /**
      * The name of the javac executable to use in fork-mode.
      *
      * <p>This is either the name specified with the executable
      * attribute or the full path of the javac compiler of the VM Ant
      * is currently running in - guessed by Ant.</p>
      *
      * <p>You should <strong>not</strong> invoke this method if you
      * want to get the value of the executable command - use {@link
      * #getExecutable getExecutable} for this.</p>
      * @return the name of the javac executable
      */
     public String getJavacExecutable() {
         if (forkedExecutable == null && isForkedJavac()) {
             forkedExecutable = getSystemJavac();
         } else if (forkedExecutable != null && !isForkedJavac()) {
             forkedExecutable = null;
         }
         return forkedExecutable;
     }
 
     /**
      * If true, enables the -nowarn option.
      * @param flag if true, enable the -nowarn option
      */
     public void setNowarn(boolean flag) {
         this.nowarn = flag;
     }
 
     /**
      * Should the -nowarn option be used.
      * @return true if the -nowarn option should be used
      */
     public boolean getNowarn() {
         return nowarn;
     }
 
     /**
      * Adds an implementation specific command-line argument.
      * @return a ImplementationSpecificArgument to be configured
      */
     public ImplementationSpecificArgument createCompilerArg() {
         ImplementationSpecificArgument arg =
             new ImplementationSpecificArgument();
         facade.addImplementationArgument(arg);
         return arg;
     }
 
     /**
      * Get the additional implementation specific command line arguments.
      * @return array of command line arguments, guaranteed to be non-null.
      */
     public String[] getCurrentCompilerArgs() {
         String chosen = facade.getExplicitChoice();
         // make sure facade knows about magic properties and fork setting
         facade.setImplementation(getCompiler());
         try {
             return facade.getArgs();
         } finally {
             facade.setImplementation(chosen);
         }
     }
 
     /**
      * Where Ant should place temporary files.
      *
      * @since Ant 1.6
      * @param tmpDir the temporary directory
      */
     public void setTempdir(File tmpDir) {
         this.tmpDir = tmpDir;
     }
 
     /**
      * Where Ant should place temporary files.
      *
      * @since Ant 1.6
      * @return the temporary directory
      */
     public File getTempdir() {
         return tmpDir;
     }
 
     /**
      * Executes the task.
      * @exception BuildException if an error occurs
      */
     public void execute() throws BuildException {
         checkParameters();
         resetFileLists();
 
         // scan source directories and dest directory to build up
         // compile lists
         String[] list = src.list();
         for (int i = 0; i < list.length; i++) {
             File srcDir = getProject().resolveFile(list[i]);
             if (!srcDir.exists()) {
                 throw new BuildException("srcdir \""
                                          + srcDir.getPath()
                                          + "\" does not exist!", getLocation());
             }
 
             DirectoryScanner ds = this.getDirectoryScanner(srcDir);
             String[] files = ds.getIncludedFiles();
 
             scanDir(srcDir, destDir != null ? destDir : srcDir, files);
         }
 
         compile();
     }
 
     /**
      * Clear the list of files to be compiled and copied..
      */
     protected void resetFileLists() {
         compileList = new File[0];
     }
 
     /**
      * Scans the directory looking for source files to be compiled.
      * The results are returned in the class variable compileList
      *
      * @param srcDir   The source directory
      * @param destDir  The destination directory
      * @param files    An array of filenames
      */
     protected void scanDir(File srcDir, File destDir, String[] files) {
         GlobPatternMapper m = new GlobPatternMapper();
         m.setFrom("*.java");
         m.setTo("*.class");
         SourceFileScanner sfs = new SourceFileScanner(this);
         File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, m);
 
         if (newFiles.length > 0) {
             File[] newCompileList
                 = new File[compileList.length + newFiles.length];
             System.arraycopy(compileList, 0, newCompileList, 0,
                     compileList.length);
             System.arraycopy(newFiles, 0, newCompileList,
                     compileList.length, newFiles.length);
             compileList = newCompileList;
         }
     }
 
     /**
      * Gets the list of files to be compiled.
      * @return the list of files as an array
      */
     public File[] getFileList() {
         return compileList;
     }
 
     /**
      * Is the compiler implementation a jdk compiler
      *
      * @param compilerImpl the name of the compiler implementation
      * @return true if compilerImpl is "modern", "classic", "javac1.1",
      *                 "javac1.2", "javac1.3", "javac1.4" or "javac1.5".
      */
     protected boolean isJdkCompiler(String compilerImpl) {
         return "modern".equals(compilerImpl)
             || "classic".equals(compilerImpl)
             || "javac1.1".equals(compilerImpl)
             || "javac1.2".equals(compilerImpl)
             || "javac1.3".equals(compilerImpl)
             || "javac1.4".equals(compilerImpl)
             || "javac1.5".equals(compilerImpl);
     }
 
     /**
      * @return the executable name of the java compiler
      */
     protected String getSystemJavac() {
         return JavaEnvUtils.getJdkExecutable("javac");
     }
 
     /**
      * Choose the implementation for this particular task.
      * @param compiler the name of the compiler
      * @since Ant 1.5
      */
     public void setCompiler(String compiler) {
         facade.setImplementation(compiler);
     }
 
     /**
      * The implementation for this particular task.
      *
      * <p>Defaults to the build.compiler property but can be overridden
      * via the compiler and fork attributes.</p>
      *
      * <p>If fork has been set to true, the result will be extJavac
      * and not classic or java1.2 - no matter what the compiler
      * attribute looks like.</p>
      *
      * @see #getCompilerVersion
      *
      * @since Ant 1.5
      */
     public String getCompiler() {
         String compilerImpl = getCompilerVersion();
         if (fork) {
             if (isJdkCompiler(compilerImpl)) {
                 if (facade.hasBeenSet()) {
                     log("Since fork is true, ignoring compiler setting.",
                         Project.MSG_WARN);
                 }
                 compilerImpl = "extJavac";
             } else {
                 log("Since compiler setting isn't classic or modern,"
                     + "ignoring fork setting.", Project.MSG_WARN);
             }
         }
         return compilerImpl;
     }
 
     /**
      * The implementation for this particular task.
      *
      * <p>Defaults to the build.compiler property but can be overridden
      * via the compiler attribute.</p>
      *
      * <p>This method does not take the fork attribute into
      * account.</p>
      *
      * @see #getCompiler
      *
      * @since Ant 1.5
      */
     public String getCompilerVersion() {
         facade.setMagicValue(getProject().getProperty("build.compiler"));
         return facade.getImplementation();
     }
 
     /**
      * Check that all required attributes have been set and nothing
      * silly has been entered.
      *
      * @since Ant 1.5
      * @exception BuildException if an error occurs
      */
     protected void checkParameters() throws BuildException {
         if (src == null) {
             throw new BuildException("srcdir attribute must be set!",
                                      getLocation());
         }
         if (src.size() == 0) {
             throw new BuildException("srcdir attribute must be set!",
                                      getLocation());
         }
 
         if (destDir != null && !destDir.isDirectory()) {
             throw new BuildException("destination directory \""
                                      + destDir
                                      + "\" does not exist "
                                      + "or is not a directory", getLocation());
         }
     }
 
     /**
      * Perform the compilation.
      *
      * @since Ant 1.5
      */
     protected void compile() {
         String compilerImpl = getCompiler();
 
         if (compileList.length > 0) {
             log("Compiling " + compileList.length + " source file"
                 + (compileList.length == 1 ? "" : "s")
                 + (destDir != null ? " to " + destDir : ""));
 
             if (listFiles) {
                 for (int i = 0; i < compileList.length; i++) {
                   String filename = compileList[i].getAbsolutePath();
                   log(filename);
                 }
             }
 
             CompilerAdapter adapter =
                 CompilerAdapterFactory.getCompiler(compilerImpl, this);
 
             // now we need to populate the compiler adapter
             adapter.setJavac(this);
 
             // finally, lets execute the compiler!!
             if (!adapter.execute()) {
                 if (failOnError) {
                     throw new BuildException(FAIL_MSG, getLocation());
                 } else {
                     log(FAIL_MSG, Project.MSG_ERR);
                 }
             }
         }
     }
 
     /**
      * Adds an "compiler" attribute to Commandline$Attribute used to
      * filter command line attributes based on the current
      * implementation.
      */
     public class ImplementationSpecificArgument extends
         org.apache.tools.ant.util.facade.ImplementationSpecificArgument {
 
         /**
          * @param impl the name of the compiler
          */
         public void setCompiler(String impl) {
             super.setImplementation(impl);
         }
     }
 
 }