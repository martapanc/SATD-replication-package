diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index 3074161c9..dd89e49e3 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -1,1747 +1,1839 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
+import java.util.Stack;
 import java.util.Vector;
 
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceFactory;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.selectors.FileSelector;
 import org.apache.tools.ant.types.selectors.PathPattern;
 import org.apache.tools.ant.types.selectors.SelectorScanner;
 import org.apache.tools.ant.types.selectors.SelectorUtils;
+import org.apache.tools.ant.util.CollectionUtils;
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
      * @deprecated since 1.6.x.
      *             Use the {@link #getDefaultExcludes getDefaultExcludes}
      *             method instead.
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
 
+    public static final int MAX_LEVELS_OF_SYMLINKS = 1;
+
     /** Helper. */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /** iterations for case-sensitive scanning. */
     private static final boolean[] CS_SCAN_ONLY = new boolean[] {true};
 
     /** iterations for non-case-sensitive scanning. */
     private static final boolean[] CS_THEN_NON_CS = new boolean[] {true, false};
 
     /**
      * Patterns which should be excluded by default.
      *
      * @see #addDefaultExcludes()
      */
     private static Vector defaultExcludes = new Vector();
     static {
         resetDefaultExcludes();
     }
 
     // CheckStyle:VisibilityModifier OFF - bc
 
     /** The base directory to be scanned. */
     protected File basedir;
 
     /** The patterns for the files to be included. */
     protected String[] includes;
 
     /** The patterns for the files to be excluded. */
     protected String[] excludes;
 
     /** Selectors that will filter which files are in our candidate list. */
     protected FileSelector[] selectors = null;
 
     /**
      * The files which matched at least one include and no excludes
      * and were selected.
      */
     protected Vector filesIncluded;
 
     /** The files which did not match any includes or selectors. */
     protected Vector filesNotIncluded;
 
     /**
      * The files which matched at least one include and at least
      * one exclude.
      */
     protected Vector filesExcluded;
 
     /**
      * The directories which matched at least one include and no excludes
      * and were selected.
      */
     protected Vector dirsIncluded;
 
     /** The directories which were found and did not match any includes. */
     protected Vector dirsNotIncluded;
 
     /**
      * The directories which matched at least one include and at least one
      * exclude.
      */
     protected Vector dirsExcluded;
 
     /**
      * The files which matched at least one include and no excludes and
      * which a selector discarded.
      */
     protected Vector filesDeselected;
 
     /**
      * The directories which matched at least one include and no excludes
      * but which a selector discarded.
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
      * Whether a missing base directory is an error.
      * @since Ant 1.7.1
      */
     protected boolean errorOnMissingDir = true;
 
     /**
      * Whether or not symbolic links should be followed.
      *
      * @since Ant 1.5
      */
     private boolean followSymlinks = true;
 
     /** Whether or not everything tested so far has been included. */
     protected boolean everythingIncluded = true;
 
     // CheckStyle:VisibilityModifier ON
 
     /**
      * Temporary table to speed up the various scanning methods.
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
      * Set of all include patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to upper case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.6.3
      */
     private Set includeNonPatterns = new HashSet();
 
     /**
      * Set of all include patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to upper case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.6.3
      */
     private Set excludeNonPatterns = new HashSet();
 
     /**
      * Array of all include patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      */
     private PathPattern[] includePatterns;
 
     /**
      * Array of all exclude patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      */
     private PathPattern[] excludePatterns;
 
     /**
      * Have the non-pattern sets and pattern arrays for in- and
      * excludes been initialized?
      *
      * @since Ant 1.6.3
      */
     private boolean areNonPatternSetsReady = false;
 
     /**
      * Scanning flag.
      *
      * @since Ant 1.6.3
      */
     private boolean scanning = false;
 
     /**
      * Scanning lock.
      *
      * @since Ant 1.6.3
      */
     private Object scanLock = new Object();
 
     /**
      * Slow scanning flag.
      *
      * @since Ant 1.6.3
      */
     private boolean slowScanning = false;
 
     /**
      * Slow scanning lock.
      *
      * @since Ant 1.6.3
      */
     private Object slowScanLock = new Object();
 
     /**
      * Exception thrown during scan.
      *
      * @since Ant 1.6.3
      */
     private IllegalStateException illegal = null;
 
     /**
+     * The maximum number of times a symbolic link may be followed
+     * during a scan.
+     *
+     * @since Ant 1.8.0
+     */
+    private int maxLevelsOfSymlinks = MAX_LEVELS_OF_SYMLINKS;
+
+    /**
      * Sole constructor.
      */
     public DirectoryScanner() {
     }
 
     /**
      * Test whether or not a given path matches the start of a given
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
      * Test whether or not a given path matches the start of a given
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
      * Test whether or not a given path matches a given pattern.
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
      * Test whether or not a given path matches a given pattern.
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
      * Test whether or not a string matches against a pattern.
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
      * Test whether or not a string matches against a pattern.
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
      * @return    <code>true</code> if the string was added;
      *            <code>false</code> if it already existed.
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
      *            exclude (and thus was removed);
      *            <code>false</code> if <code>s</code> was not
      *            in the default excludes list to begin with.
      *
      * @since Ant 1.6
      */
     public static boolean removeDefaultExclude(String s) {
         return defaultExcludes.remove(s);
     }
 
     /**
      * Go back to the hardwired default exclude patterns.
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
      * Set the base directory to be scanned. This is the directory which is
      * scanned recursively. All '/' and '\' characters are replaced by
      * <code>File.separatorChar</code>, so the separator used need not match
      * <code>File.separatorChar</code>.
      *
      * @param basedir The base directory to scan.
      */
     public void setBasedir(String basedir) {
         setBasedir(basedir == null ? (File) null
             : new File(basedir.replace('/', File.separatorChar).replace(
             '\\', File.separatorChar)));
     }
 
     /**
      * Set the base directory to be scanned. This is the directory which is
      * scanned recursively.
      *
      * @param basedir The base directory for scanning.
      */
     public synchronized void setBasedir(File basedir) {
         this.basedir = basedir;
     }
 
     /**
      * Return the base directory to be scanned.
      * This is the directory which is scanned recursively.
      *
      * @return the base directory to be scanned.
      */
     public synchronized File getBasedir() {
         return basedir;
     }
 
     /**
      * Find out whether include exclude patterns are matched in a
      * case sensitive way.
      * @return whether or not the scanning is case sensitive.
      * @since Ant 1.6
      */
     public synchronized boolean isCaseSensitive() {
         return isCaseSensitive;
     }
 
     /**
      * Set whether or not include and exclude patterns are matched
      * in a case sensitive way.
      *
      * @param isCaseSensitive whether or not the file system should be
      *                        regarded as a case sensitive one.
      */
     public synchronized void setCaseSensitive(boolean isCaseSensitive) {
         this.isCaseSensitive = isCaseSensitive;
     }
 
     /**
      * Sets whether or not a missing base directory is an error
      *
      * @param errorOnMissingDir whether or not a missing base directory
      *                        is an error
      * @since Ant 1.7.1
      */
     public void setErrorOnMissingDir(boolean errorOnMissingDir) {
         this.errorOnMissingDir = errorOnMissingDir;
     }
 
     /**
      * Get whether or not a DirectoryScanner follows symbolic links.
      *
      * @return flag indicating whether symbolic links should be followed.
      *
      * @since Ant 1.6
      */
     public synchronized boolean isFollowSymlinks() {
         return followSymlinks;
     }
 
     /**
      * Set whether or not symbolic links should be followed.
      *
      * @param followSymlinks whether or not symbolic links should be followed.
      */
     public synchronized void setFollowSymlinks(boolean followSymlinks) {
         this.followSymlinks = followSymlinks;
     }
 
     /**
+     * The maximum number of times a symbolic link may be followed
+     * during a scan.
+     *
+     * @since Ant 1.8.0
+     */
+    public void setMaxLevelsOfSymlinks(int max) {
+        maxLevelsOfSymlinks = max;
+    }
+
+    /**
      * Set the list of include patterns to use. All '/' and '\' characters
      * are replaced by <code>File.separatorChar</code>, so the separator used
      * need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param includes A list of include patterns.
      *                 May be <code>null</code>, indicating that all files
      *                 should be included. If a non-<code>null</code>
      *                 list is given, all elements must be
      *                 non-<code>null</code>.
      */
     public synchronized void setIncludes(String[] includes) {
         if (includes == null) {
             this.includes = null;
         } else {
             this.includes = new String[includes.length];
             for (int i = 0; i < includes.length; i++) {
                 this.includes[i] = normalizePattern(includes[i]);
             }
         }
     }
 
     /**
      * Set the list of exclude patterns to use. All '/' and '\' characters
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
     public synchronized void setExcludes(String[] excludes) {
         if (excludes == null) {
             this.excludes = null;
         } else {
             this.excludes = new String[excludes.length];
             for (int i = 0; i < excludes.length; i++) {
                 this.excludes[i] = normalizePattern(excludes[i]);
             }
         }
     }
 
     /**
      * Add to the list of exclude patterns to use. All '/' and '\'
      * characters are replaced by <code>File.separatorChar</code>, so
      * the separator used need not match <code>File.separatorChar</code>.
      * <p>
      * When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @param excludes A list of exclude patterns.
      *                 May be <code>null</code>, in which case the
      *                 exclude patterns don't get changed at all.
      *
      * @since Ant 1.6.3
      */
     public synchronized void addExcludes(String[] excludes) {
         if (excludes != null && excludes.length > 0) {
             if (this.excludes != null && this.excludes.length > 0) {
                 String[] tmp = new String[excludes.length
                                           + this.excludes.length];
                 System.arraycopy(this.excludes, 0, tmp, 0,
                                  this.excludes.length);
                 for (int i = 0; i < excludes.length; i++) {
                     tmp[this.excludes.length + i] =
                         normalizePattern(excludes[i]);
                 }
                 this.excludes = tmp;
             } else {
                 setExcludes(excludes);
             }
         }
     }
 
     /**
      * All '/' and '\' characters are replaced by
      * <code>File.separatorChar</code>, so the separator used need not
      * match <code>File.separatorChar</code>.
      *
      * <p> When a pattern ends with a '/' or '\', "**" is appended.
      *
      * @since Ant 1.6.3
      */
     private static String normalizePattern(String p) {
         String pattern = p.replace('/', File.separatorChar)
             .replace('\\', File.separatorChar);
         if (pattern.endsWith(File.separator)) {
             pattern += "**";
         }
         return pattern;
     }
 
     /**
      * Set the selectors that will select the filelist.
      *
      * @param selectors specifies the selectors to be invoked on a scan.
      */
     public synchronized void setSelectors(FileSelector[] selectors) {
         this.selectors = selectors;
     }
 
     /**
      * Return whether or not the scanner has included all the files or
      * directories it has come across so far.
      *
      * @return <code>true</code> if all files and directories which have
      *         been found so far have been included.
      */
     public synchronized boolean isEverythingIncluded() {
         return everythingIncluded;
     }
 
     /**
      * Scan for files which match at least one include pattern and don't match
      * any exclude patterns. If there are selectors then the files must pass
      * muster there, as well.  Scans under basedir, if set; otherwise the
      * include patterns without leading wildcards specify the absolute paths of
      * the files that may be included.
      *
      * @exception IllegalStateException if the base directory was set
      *            incorrectly (i.e. if it doesn't exist or isn't a directory).
      */
     public void scan() throws IllegalStateException {
         synchronized (scanLock) {
             if (scanning) {
                 while (scanning) {
                     try {
                         scanLock.wait();
                     } catch (InterruptedException e) {
                         continue;
                     }
                 }
                 if (illegal != null) {
                     throw illegal;
                 }
                 return;
             }
             scanning = true;
         }
         File savedBase = basedir;
         try {
             synchronized (this) {
                 illegal = null;
                 clearResults();
 
                 // set in/excludes to reasonable defaults if needed:
                 boolean nullIncludes = (includes == null);
                 includes = nullIncludes ? new String[] {"**"} : includes;
                 boolean nullExcludes = (excludes == null);
                 excludes = nullExcludes ? new String[0] : excludes;
 
                 if (basedir != null && !followSymlinks
                     && FILE_UTILS.isSymbolicLink(basedir.getParentFile(),
                                                  basedir.getName())) {
                     basedir = null;
                 }
 
                 if (basedir == null) {
                     // if no basedir and no includes, nothing to do:
                     if (nullIncludes) {
                         return;
                     }
                 } else {
                     if (!basedir.exists()) {
                         if (errorOnMissingDir) {
                             illegal = new IllegalStateException(
                                 "basedir " + basedir + " does not exist.");
                         } else {
                             // Nothing to do - basedir does not exist
                             return;
                         }
                     } else if (!basedir.isDirectory()) {
                         illegal = new IllegalStateException("basedir "
                                                             + basedir
                                                             + " is not a"
                                                             + " directory.");
                     }
                     if (illegal != null) {
                         throw illegal;
                     }
                 }
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
                 includes = nullIncludes ? null : includes;
                 excludes = nullExcludes ? null : excludes;
             }
         } catch (IOException ex) {
             throw new BuildException(ex);
         } finally {
             basedir = savedBase;
             synchronized (scanLock) {
                 scanning = false;
                 scanLock.notifyAll();
             }
         }
     }
 
     /**
      * This routine is actually checking all the include patterns in
      * order to avoid scanning everything under base dir.
      * @since Ant 1.6
      */
     private void checkIncludePatterns() {
         Map newroots = new HashMap();
         // put in the newroots map the include patterns without
         // wildcard tokens
         for (int i = 0; i < includes.length; i++) {
             if (FileUtils.isAbsolutePath(includes[i])) {
                 //skip abs. paths not under basedir, if set:
                 if (basedir != null
                     && !SelectorUtils.matchPatternStart(includes[i],
                     basedir.getAbsolutePath(), isCaseSensitive())) {
                     continue;
                 }
             } else if (basedir == null) {
                 //skip non-abs. paths if basedir == null:
                 continue;
             }
             newroots.put(SelectorUtils.rtrimWildcardTokens(
                 includes[i]), includes[i]);
         }
         if (newroots.containsKey("") && basedir != null) {
             // we are going to scan everything anyway
             scandir(basedir, "", true);
         } else {
             // only scan directories that can include matched files or
             // directories
             Iterator it = newroots.entrySet().iterator();
 
             File canonBase = null;
             if (basedir != null) {
                 try {
                     canonBase = basedir.getCanonicalFile();
                 } catch (IOException ex) {
                     throw new BuildException(ex);
                 }
             }
             while (it.hasNext()) {
                 Map.Entry entry = (Map.Entry) it.next();
                 String currentelement = (String) entry.getKey();
                 if (basedir == null
                     && !FileUtils.isAbsolutePath(currentelement)) {
                     continue;
                 }
                 String originalpattern = (String) entry.getValue();
                 File myfile = new File(basedir, currentelement);
 
                 if (myfile.exists()) {
                     // may be on a case insensitive file system.  We want
                     // the results to show what's really on the disk, so
                     // we need to double check.
                     try {
                         String path = (basedir == null)
                             ? myfile.getCanonicalPath()
                             : FILE_UTILS.removeLeadingPath(canonBase,
                             myfile.getCanonicalFile());
                         if (!path.equals(currentelement) || ON_VMS) {
                             myfile = findFile(basedir, currentelement, true);
                             if (myfile != null && basedir != null) {
                                 currentelement = FILE_UTILS.removeLeadingPath(
                                     basedir, myfile);
                             }
                         }
                     } catch (IOException ex) {
                         throw new BuildException(ex);
                     }
                 }
                 if ((myfile == null || !myfile.exists()) && !isCaseSensitive()) {
                     File f = findFile(basedir, currentelement, false);
                     if (f != null && f.exists()) {
                         // adapt currentelement to the case we've
                         // actually found
                         currentelement = (basedir == null)
                             ? f.getAbsolutePath()
                             : FILE_UTILS.removeLeadingPath(basedir, f);
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
                         boolean included = isCaseSensitive()
                             ? originalpattern.equals(currentelement)
                             : originalpattern.equalsIgnoreCase(currentelement);
                         if (included) {
                             accountForIncludedFile(currentelement, myfile);
                         }
                     }
                 }
             }
         }
     }
 
     /**
      * Clear the result caches for a scan.
      */
     protected synchronized void clearResults() {
         filesIncluded    = new Vector();
         filesNotIncluded = new Vector();
         filesExcluded    = new Vector();
         filesDeselected  = new Vector();
         dirsIncluded     = new Vector();
         dirsNotIncluded  = new Vector();
         dirsExcluded     = new Vector();
         dirsDeselected   = new Vector();
         everythingIncluded = (basedir != null);
         scannedDirs.clear();
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
         synchronized (slowScanLock) {
             if (haveSlowResults) {
                 return;
             }
             if (slowScanning) {
                 while (slowScanning) {
                     try {
                         slowScanLock.wait();
                     } catch (InterruptedException e) {
                         // Empty
                     }
                 }
                 return;
             }
             slowScanning = true;
         }
         try {
             synchronized (this) {
 
                 // set in/excludes to reasonable defaults if needed:
                 boolean nullIncludes = (includes == null);
                 includes = nullIncludes ? new String[] {"**"} : includes;
                 boolean nullExcludes = (excludes == null);
                 excludes = nullExcludes ? new String[0] : excludes;
 
                 String[] excl = new String[dirsExcluded.size()];
                 dirsExcluded.copyInto(excl);
 
                 String[] notIncl = new String[dirsNotIncluded.size()];
                 dirsNotIncluded.copyInto(notIncl);
 
                 processSlowScan(excl);
                 processSlowScan(notIncl);
                 clearCaches();
                 includes = nullIncludes ? null : includes;
                 excludes = nullExcludes ? null : excludes;
             }
         } finally {
             synchronized (slowScanLock) {
                 haveSlowResults = true;
                 slowScanning = false;
                 slowScanLock.notifyAll();
             }
         }
     }
 
     private void processSlowScan(String[] arr) {
         for (int i = 0; i < arr.length; i++) {
             if (!couldHoldIncluded(arr[i])) {
                 scandir(new File(basedir, arr[i]),
                         arr[i] + File.separator, false);
             }
         }
     }
 
     /**
      * Scan the given directory for files and directories. Found files and
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
         }
         String[] newfiles = list(dir);
         if (newfiles == null) {
             if (!dir.exists()) {
                 throw new BuildException(dir + " doesn't exist.");
             } else if (!dir.isDirectory()) {
                 throw new BuildException(dir + " is not a directory.");
             } else {
                 throw new BuildException("IO error scanning directory '"
                                          + dir.getAbsolutePath() + "'");
             }
         }
-        scandir(dir, vpath, fast, newfiles);
+        scandir(dir, vpath, fast, newfiles, new Stack());
     }
 
     private void scandir(File dir, String vpath, boolean fast,
-                         String[] newfiles) {
+                         String[] newfiles, Stack directoryNamesFollowed) {
         // avoid double scanning of directories, can only happen in fast mode
         if (fast && hasBeenScanned(vpath)) {
             return;
         }
         if (!followSymlinks) {
             Vector noLinks = new Vector();
             for (int i = 0; i < newfiles.length; i++) {
                 try {
                     if (FILE_UTILS.isSymbolicLink(dir, newfiles[i])) {
                         String name = vpath + newfiles[i];
                         File file = new File(dir, newfiles[i]);
                         (file.isDirectory()
                             ? dirsExcluded : filesExcluded).addElement(name);
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
             newfiles = (String[]) (noLinks.toArray(new String[noLinks.size()]));
+        } else {
+            directoryNamesFollowed.push(dir.getName());
         }
+
         for (int i = 0; i < newfiles.length; i++) {
             String name = vpath + newfiles[i];
             File file = new File(dir, newfiles[i]);
             String[] children = list(file);
             if (children == null) { // probably file
                 if (isIncluded(name)) {
                     accountForIncludedFile(name, file);
                 } else {
                     everythingIncluded = false;
                     filesNotIncluded.addElement(name);
                 }
             } else { // dir
+
+                if (followSymlinks
+                    && causesIllegalSymlinkLoop(newfiles[i], dir,
+                                                directoryNamesFollowed)) {
+                    // will be caught and redirected to Ant's logging system
+                    System.err.println("skipping symbolic link "
+                                       + file.getAbsolutePath()
+                                       + " -- too many levels of symbolic"
+                                       + " links.");
+                    continue;
+                }
+
                 if (isIncluded(name)) {
-                    accountForIncludedDir(name, file, fast, children);
+                    accountForIncludedDir(name, file, fast, children,
+                                          directoryNamesFollowed);
                 } else {
                     everythingIncluded = false;
                     dirsNotIncluded.addElement(name);
                     if (fast && couldHoldIncluded(name)) {
-                        scandir(file, name + File.separator, fast, children);
+                        scandir(file, name + File.separator, fast, children,
+                                directoryNamesFollowed);
                     }
                 }
                 if (!fast) {
-                    scandir(file, name + File.separator, fast, children);
+                    scandir(file, name + File.separator, fast, children,
+                            directoryNamesFollowed);
                 }
             }
         }
+
+        if (followSymlinks) {
+            directoryNamesFollowed.pop();
+        }
     }
 
     /**
      * Process included file.
      * @param name  path of the file relative to the directory of the FileSet.
      * @param file  included File.
      */
     private void accountForIncludedFile(String name, File file) {
         processIncluded(name, file, filesIncluded, filesExcluded,
                         filesDeselected);
     }
 
     /**
      * Process included directory.
      * @param name path of the directory relative to the directory of
      *             the FileSet.
      * @param file directory as File.
      * @param fast whether to perform fast scans.
      */
     private void accountForIncludedDir(String name, File file, boolean fast) {
         processIncluded(name, file, dirsIncluded, dirsExcluded, dirsDeselected);
         if (fast && couldHoldIncluded(name) && !contentsExcluded(name)) {
             scandir(file, name + File.separator, fast);
         }
     }
 
     private void accountForIncludedDir(String name, File file, boolean fast,
-                                       String[] children) {
+                                       String[] children,
+                                       Stack directoryNamesFollowed) {
         processIncluded(name, file, dirsIncluded, dirsExcluded, dirsDeselected);
         if (fast && couldHoldIncluded(name) && !contentsExcluded(name)) {
-            scandir(file, name + File.separator, fast, children);
+            scandir(file, name + File.separator, fast, children,
+                    directoryNamesFollowed);
         }
     }
 
     private void processIncluded(String name, File file, Vector inc,
                                  Vector exc, Vector des) {
 
         if (inc.contains(name) || exc.contains(name) || des.contains(name)) {
             return;
         }
 
         boolean included = false;
         if (isExcluded(name)) {
             exc.add(name);
         } else if (isSelected(name, file)) {
             included = true;
             inc.add(name);
         } else {
             des.add(name);
         }
         everythingIncluded &= included;
     }
 
     /**
      * Test whether or not a name matches against at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         include pattern, or <code>false</code> otherwise.
      */
     protected boolean isIncluded(String name) {
         ensureNonPatternSetsReady();
 
         if (isCaseSensitive()
             ? includeNonPatterns.contains(name)
             : includeNonPatterns.contains(name.toUpperCase())) {
             return true;
         }
         for (int i = 0; i < includePatterns.length; i++) {
             if (includePatterns[i].matchPath(name, isCaseSensitive())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Test whether or not a name matches the start of at least one include
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against the start of at
      *         least one include pattern, or <code>false</code> otherwise.
      */
     protected boolean couldHoldIncluded(String name) {
         for (int i = 0; i < includes.length; i++) {
             if (matchPatternStart(includes[i], name, isCaseSensitive())
                 && isMorePowerfulThanExcludes(name, includes[i])
                 && isDeeper(includes[i], name)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Verify that a pattern specifies files deeper
      * than the level of the specified file.
      * @param pattern the pattern to check.
      * @param name the name to check.
      * @return whether the pattern is deeper than the name.
      * @since Ant 1.6.3
      */
     private boolean isDeeper(String pattern, String name) {
         Vector p = SelectorUtils.tokenizePath(pattern);
         if (!p.contains("**")) {
             Vector n = SelectorUtils.tokenizePath(name);
             return p.size() > n.size();
         }
         return true;
     }
 
     /**
      *  Find out whether one particular include pattern is more powerful
      *  than all the excludes.
      *  Note:  the power comparison is based on the length of the include pattern
      *  and of the exclude patterns without the wildcards.
      *  Ideally the comparison should be done based on the depth
      *  of the match; that is to say how many file separators have been matched
      *  before the first ** or the end of the pattern.
      *
      *  IMPORTANT : this function should return false "with care".
      *
      *  @param name the relative path to test.
      *  @param includepattern one include pattern.
      *  @return true if there is no exclude pattern more powerful than
      *  this include pattern.
      *  @since Ant 1.6
      */
     private boolean isMorePowerfulThanExcludes(String name,
                                                String includepattern) {
         String soughtexclude = name + File.separator + "**";
         for (int counter = 0; counter < excludes.length; counter++) {
             if (excludes[counter].equals(soughtexclude))  {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * Test whether all contents of the specified directory must be excluded.
      * @param name the directory name to check.
      * @return whether all the specified directory's contents are excluded.
      */
     private boolean contentsExcluded(String name) {
         name = (name.endsWith(File.separator)) ? name : name + File.separator;
         for (int i = 0; i < excludes.length; i++) {
             String e = excludes[i];
             if (e.endsWith("**") && SelectorUtils.matchPath(
                 e.substring(0, e.length() - 2), name, isCaseSensitive())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Test whether or not a name matches against at least one exclude
      * pattern.
      *
      * @param name The name to match. Must not be <code>null</code>.
      * @return <code>true</code> when the name matches against at least one
      *         exclude pattern, or <code>false</code> otherwise.
      */
     protected boolean isExcluded(String name) {
         ensureNonPatternSetsReady();
 
         if (isCaseSensitive()
             ? excludeNonPatterns.contains(name)
             : excludeNonPatterns.contains(name.toUpperCase())) {
             return true;
         }
         for (int i = 0; i < excludePatterns.length; i++) {
             if (excludePatterns[i].matchPath(name, isCaseSensitive())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Test whether a file should be selected.
      *
      * @param name the filename to check for selecting.
      * @param file the java.io.File object for this filename.
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
      * Return the names of the files which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and none of the exclude patterns.
      */
     public String[] getIncludedFiles() {
         String[] files;
         synchronized (this) {
             if (filesIncluded == null) {
                 throw new IllegalStateException("Must call scan() first");
             }
             files = new String[filesIncluded.size()];
             filesIncluded.copyInto(files);
         }
         Arrays.sort(files);
         return files;
     }
 
     /**
      * Return the count of included files.
      * @return <code>int</code>.
      * @since Ant 1.6.3
      */
     public synchronized int getIncludedFilesCount() {
         if (filesIncluded == null) {
             throw new IllegalStateException("Must call scan() first");
         }
         return filesIncluded.size();
     }
 
     /**
      * Return the names of the files which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched none of the include
      *         patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getNotIncludedFiles() {
         slowScan();
         String[] files = new String[filesNotIncluded.size()];
         filesNotIncluded.copyInto(files);
         return files;
     }
 
     /**
      * Return the names of the files which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the files which matched at least one of the
      *         include patterns and at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getExcludedFiles() {
         slowScan();
         String[] files = new String[filesExcluded.size()];
         filesExcluded.copyInto(files);
         return files;
     }
 
     /**
      * <p>Return the names of the files which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the files which were deselected.
      *
      * @see #slowScan
      */
     public synchronized String[] getDeselectedFiles() {
         slowScan();
         String[] files = new String[filesDeselected.size()];
         filesDeselected.copyInto(files);
         return files;
     }
 
     /**
      * Return the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      * The names are relative to the base directory.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and none of the exclude patterns.
      */
     public String[] getIncludedDirectories() {
         String[] directories;
         synchronized (this) {
             if (dirsIncluded == null) {
                 throw new IllegalStateException("Must call scan() first");
             }
             directories = new String[dirsIncluded.size()];
             dirsIncluded.copyInto(directories);
         }
         Arrays.sort(directories);
         return directories;
     }
 
     /**
      * Return the count of included directories.
      * @return <code>int</code>.
      * @since Ant 1.6.3
      */
     public synchronized int getIncludedDirsCount() {
         if (dirsIncluded == null) {
             throw new IllegalStateException("Must call scan() first");
         }
         return dirsIncluded.size();
     }
 
     /**
      * Return the names of the directories which matched none of the include
      * patterns. The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched none of the include
      * patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getNotIncludedDirectories() {
         slowScan();
         String[] directories = new String[dirsNotIncluded.size()];
         dirsNotIncluded.copyInto(directories);
         return directories;
     }
 
     /**
      * Return the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      * The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.
      *
      * @return the names of the directories which matched at least one of the
      * include patterns and at least one of the exclude patterns.
      *
      * @see #slowScan
      */
     public synchronized String[] getExcludedDirectories() {
         slowScan();
         String[] directories = new String[dirsExcluded.size()];
         dirsExcluded.copyInto(directories);
         return directories;
     }
 
     /**
      * <p>Return the names of the directories which were selected out and
      * therefore not ultimately included.</p>
      *
      * <p>The names are relative to the base directory. This involves
      * performing a slow scan if one has not already been completed.</p>
      *
      * @return the names of the directories which were deselected.
      *
      * @see #slowScan
      */
     public synchronized String[] getDeselectedDirectories() {
         slowScan();
         String[] directories = new String[dirsDeselected.size()];
         dirsDeselected.copyInto(directories);
         return directories;
     }
 
     /**
      * Add default exclusions to the current exclusions set.
      */
     public synchronized void addDefaultExcludes() {
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
      * Get the named resource.
      * @param name path name of the file relative to the dir attribute.
      *
      * @return the resource with the given name.
      * @since Ant 1.5.2
      */
     public synchronized Resource getResource(String name) {
         return new FileResource(basedir, name);
     }
 
     private static final String[] NULL_FILE_LIST = new String[0];
 
     /**
      * Return a cached result of list performed on file, if
      * available.  Invokes the method and caches the result otherwise.
      *
      * @param file File (dir) to list.
      * @since Ant 1.6
      */
     private String[] list(File file) {
         String[] files = (String[]) fileListMap.get(file);
         if (files == null) {
             files = file.list();
             if (files != null) {
                 fileListMap.put(file, files);
             } else {
                 fileListMap.put(file, NULL_FILE_LIST);
             }
         } else if (files == NULL_FILE_LIST) {
             files = null;
         }
         return files;
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given name.
      *
      * @param base base File (dir).
      * @param path file path.
      * @param cs whether to scan case-sensitively.
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6.3
      */
     private File findFile(File base, String path, boolean cs) {
         if (FileUtils.isAbsolutePath(path)) {
             if (base == null) {
                 String[] s = FILE_UTILS.dissect(path);
                 base = new File(s[0]);
                 path = s[1];
             } else {
                 File f = FILE_UTILS.normalize(path);
                 String s = FILE_UTILS.removeLeadingPath(base, f);
                 if (s.equals(f.getAbsolutePath())) {
                     //removing base from path yields no change; path
                     //not child of base
                     return null;
                 }
                 path = s;
             }
         }
         return findFile(base, SelectorUtils.tokenizePath(path), cs);
     }
 
     /**
      * From <code>base</code> traverse the filesystem in order to find
      * a file that matches the given stack of names.
      *
      * @param base base File (dir).
      * @param pathElements Vector of path elements (dirs...file).
      * @param cs whether to scan case-sensitively.
      * @return File object that points to the file in question or null.
      *
      * @since Ant 1.6.3
      */
     private File findFile(File base, Vector pathElements, boolean cs) {
         if (pathElements.size() == 0) {
             return base;
         }
         String current = (String) pathElements.remove(0);
         if (base == null) {
             return findFile(new File(current), pathElements, cs);
         }
         if (!base.isDirectory()) {
             return null;
         }
         String[] files = list(base);
         if (files == null) {
             throw new BuildException("IO error scanning directory "
                                      + base.getAbsolutePath());
         }
         boolean[] matchCase = cs ? CS_SCAN_ONLY : CS_THEN_NON_CS;
         for (int i = 0; i < matchCase.length; i++) {
             for (int j = 0; j < files.length; j++) {
                 if (matchCase[i] ? files[j].equals(current)
                                  : files[j].equalsIgnoreCase(current)) {
                     return findFile(new File(base, files[j]), pathElements, cs);
                 }
             }
         }
         return null;
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @param base base File (dir).
      * @param path file path.
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, String path) {
         return isSymlink(base, SelectorUtils.tokenizePath(path));
     }
 
     /**
      * Do we have to traverse a symlink when trying to reach path from
      * basedir?
      * @param base base File (dir).
      * @param pathElements Vector of path elements (dirs...file).
      * @since Ant 1.6
      */
     private boolean isSymlink(File base, Vector pathElements) {
         if (pathElements.size() > 0) {
             String current = (String) pathElements.remove(0);
             try {
                 return FILE_UTILS.isSymbolicLink(base, current)
                     || isSymlink(new File(base, current), pathElements);
             } catch (IOException ioe) {
                 String msg = "IOException caught while checking "
                     + "for links, couldn't get canonical path!";
                 // will be caught and redirected to Ant's logging system
                 System.err.println(msg);
             }
         }
         return false;
     }
 
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
      * This method is of interest for testing purposes.  The returned
      * Set is live and should not be modified.
      * @return the Set of relative directory names that have been scanned.
      */
     /* package-private */ Set getScannedDirs() {
         return scannedDirs;
     }
 
     /**
      * Clear internal caches.
      *
      * @since Ant 1.6
      */
     private synchronized void clearCaches() {
         fileListMap.clear();
         includeNonPatterns.clear();
         excludeNonPatterns.clear();
         includePatterns = null;
         excludePatterns = null;
         areNonPatternSetsReady = false;
     }
 
     /**
      * Ensure that the in|exclude &quot;patterns&quot;
      * have been properly divided up.
      *
      * @since Ant 1.6.3
      */
     private synchronized void ensureNonPatternSetsReady() {
         if (!areNonPatternSetsReady) {
             includePatterns = fillNonPatternSet(includeNonPatterns, includes);
             excludePatterns = fillNonPatternSet(excludeNonPatterns, excludes);
             areNonPatternSetsReady = true;
         }
     }
 
     /**
      * Add all patterns that are not real patterns (do not contain
      * wildcards) to the set and returns the real patterns.
      *
      * @param set Set to populate.
      * @param patterns String[] of patterns.
      * @since Ant 1.6.3
      */
     private PathPattern[] fillNonPatternSet(Set set, String[] patterns) {
         ArrayList al = new ArrayList(patterns.length);
         for (int i = 0; i < patterns.length; i++) {
             if (!SelectorUtils.hasWildcards(patterns[i])) {
                 set.add(isCaseSensitive() ? patterns[i]
                     : patterns[i].toUpperCase());
             } else {
                 al.add(new PathPattern(patterns[i]));
             }
         }
         return (PathPattern[]) al.toArray(new PathPattern[al.size()]);
     }
 
+    /**
+     * Would following the given directory cause a loop of symbolic
+     * links deeper than allowed?
+     *
+     * <p>Can only happen if the given directory has been seen at
+     * least more often than allowed during the current scan and it is
+     * a symbolic link and enough other occurences of the same name
+     * higher up are symbolic links that point to the same place.</p>
+     *
+     * @since Ant 1.8.0
+     */
+    private boolean causesIllegalSymlinkLoop(String dirName, File parent,
+                                             Stack directoryNamesFollowed) {
+        try {
+            if (CollectionUtils.frequency(directoryNamesFollowed, dirName)
+                   >= maxLevelsOfSymlinks
+                && FILE_UTILS.isSymbolicLink(parent, dirName)) {
+
+                Stack s = (Stack) directoryNamesFollowed.clone();
+                ArrayList files = new ArrayList();
+                File f = FILE_UTILS.resolveFile(parent, dirName);
+                String target = f.getCanonicalPath();
+                files.add(target);
+
+                String relPath = "";
+                while (!s.empty()) {
+                    relPath += "../";
+                    String dir = (String) s.pop();
+                    if (dirName.equals(dir)) {
+                        f = FILE_UTILS.resolveFile(parent, relPath + dir);
+                        files.add(f.getCanonicalPath());
+                        if (CollectionUtils.frequency(files, target)
+                            > maxLevelsOfSymlinks) {
+                            return true;
+                        }
+                    }
+                }
+
+            }
+            return false;
+        } catch (IOException ex) {
+            throw new BuildException("Caught error while checking for"
+                                     + " symbolic links", ex);
+        }
+    }
+
 }
diff --git a/src/main/org/apache/tools/ant/types/AbstractFileSet.java b/src/main/org/apache/tools/ant/types/AbstractFileSet.java
index 4e1dee9da..e1a743d68 100644
--- a/src/main/org/apache/tools/ant/types/AbstractFileSet.java
+++ b/src/main/org/apache/tools/ant/types/AbstractFileSet.java
@@ -1,838 +1,851 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 package org.apache.tools.ant.types;
 
 import java.io.File;
 import java.util.Vector;
 import java.util.Enumeration;
 
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.FileScanner;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.types.selectors.OrSelector;
 import org.apache.tools.ant.types.selectors.AndSelector;
 import org.apache.tools.ant.types.selectors.NotSelector;
 import org.apache.tools.ant.types.selectors.DateSelector;
 import org.apache.tools.ant.types.selectors.FileSelector;
 import org.apache.tools.ant.types.selectors.NoneSelector;
 import org.apache.tools.ant.types.selectors.SizeSelector;
 import org.apache.tools.ant.types.selectors.TypeSelector;
 import org.apache.tools.ant.types.selectors.DepthSelector;
 import org.apache.tools.ant.types.selectors.DependSelector;
 import org.apache.tools.ant.types.selectors.ExtendSelector;
 import org.apache.tools.ant.types.selectors.SelectSelector;
 import org.apache.tools.ant.types.selectors.PresentSelector;
 import org.apache.tools.ant.types.selectors.SelectorScanner;
 import org.apache.tools.ant.types.selectors.ContainsSelector;
 import org.apache.tools.ant.types.selectors.FilenameSelector;
 import org.apache.tools.ant.types.selectors.MajoritySelector;
 import org.apache.tools.ant.types.selectors.DifferentSelector;
 import org.apache.tools.ant.types.selectors.SelectorContainer;
 import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
 import org.apache.tools.ant.types.selectors.ReadableSelector;
 import org.apache.tools.ant.types.selectors.WritableSelector;
 import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;
 
 /**
  * Class that holds an implicit patternset and supports nested
  * patternsets and creates a DirectoryScanner using these patterns.
  *
  * <p>Common base class for DirSet and FileSet.</p>
  *
  */
 public abstract class AbstractFileSet extends DataType
     implements Cloneable, SelectorContainer {
 
     private PatternSet defaultPatterns = new PatternSet();
     private Vector additionalPatterns = new Vector();
     private Vector selectors = new Vector();
 
     private File dir;
     private boolean useDefaultExcludes = true;
     private boolean caseSensitive = true;
     private boolean followSymlinks = true;
     private boolean errorOnMissingDir = true;
+    private int maxLevelsOfSymlinks = DirectoryScanner.MAX_LEVELS_OF_SYMLINKS;
 
     /* cached DirectoryScanner instance for our own Project only */
     private DirectoryScanner directoryScanner = null;
 
     /**
      * Construct a new <code>AbstractFileSet</code>.
      */
     public AbstractFileSet() {
         super();
     }
 
     /**
      * Construct a new <code>AbstractFileSet</code>, shallowly cloned
      * from the specified <code>AbstractFileSet</code>.
      * @param fileset the <code>AbstractFileSet</code> to use as a template.
      */
     protected AbstractFileSet(AbstractFileSet fileset) {
         this.dir = fileset.dir;
         this.defaultPatterns = fileset.defaultPatterns;
         this.additionalPatterns = fileset.additionalPatterns;
         this.selectors = fileset.selectors;
         this.useDefaultExcludes = fileset.useDefaultExcludes;
         this.caseSensitive = fileset.caseSensitive;
         this.followSymlinks = fileset.followSymlinks;
         this.errorOnMissingDir = fileset.errorOnMissingDir;
+        this.maxLevelsOfSymlinks = fileset.maxLevelsOfSymlinks;
         setProject(fileset.getProject());
     }
 
     /**
      * Makes this instance in effect a reference to another instance.
      *
      * <p>You must not set another attribute or nest elements inside
      * this element if you make it a reference.</p>
      * @param r the <code>Reference</code> to use.
      * @throws BuildException on error
      */
     public void setRefid(Reference r) throws BuildException {
         if (dir != null || defaultPatterns.hasPatterns(getProject())) {
             throw tooManyAttributes();
         }
         if (!additionalPatterns.isEmpty()) {
             throw noChildrenAllowed();
         }
         if (!selectors.isEmpty()) {
             throw noChildrenAllowed();
         }
         super.setRefid(r);
     }
 
     /**
      * Sets the base-directory for this instance.
      * @param dir the directory's <code>File</code> instance.
      * @throws BuildException on error
      */
     public synchronized void setDir(File dir) throws BuildException {
         if (isReference()) {
             throw tooManyAttributes();
         }
         this.dir = dir;
         directoryScanner = null;
     }
 
     /**
      * Retrieves the base-directory for this instance.
      * @return <code>File</code>.
      */
     public File getDir() {
         return getDir(getProject());
     }
 
     /**
      * Retrieves the base-directory for this instance.
      * @param p the <code>Project</code> against which the
      *          reference is resolved, if set.
      * @return <code>File</code>.
      */
     public synchronized File getDir(Project p) {
         return (isReference()) ? getRef(p).getDir(p) : dir;
     }
 
     /**
      * Creates a nested patternset.
      * @return <code>PatternSet</code>.
      */
     public synchronized PatternSet createPatternSet() {
         if (isReference()) {
             throw noChildrenAllowed();
         }
         PatternSet patterns = new PatternSet();
         additionalPatterns.addElement(patterns);
         directoryScanner = null;
         return patterns;
     }
 
     /**
      * Add a name entry to the include list.
      * @return <code>PatternSet.NameEntry</code>.
      */
     public synchronized PatternSet.NameEntry createInclude() {
         if (isReference()) {
             throw noChildrenAllowed();
         }
         directoryScanner = null;
         return defaultPatterns.createInclude();
     }
 
     /**
      * Add a name entry to the include files list.
      * @return <code>PatternSet.NameEntry</code>.
      */
     public synchronized PatternSet.NameEntry createIncludesFile() {
         if (isReference()) {
             throw noChildrenAllowed();
         }
         directoryScanner = null;
         return defaultPatterns.createIncludesFile();
     }
 
     /**
      * Add a name entry to the exclude list.
      * @return <code>PatternSet.NameEntry</code>.
      */
     public synchronized PatternSet.NameEntry createExclude() {
         if (isReference()) {
             throw noChildrenAllowed();
         }
         directoryScanner = null;
         return defaultPatterns.createExclude();
     }
 
     /**
      * Add a name entry to the excludes files list.
      * @return <code>PatternSet.NameEntry</code>.
      */
     public synchronized PatternSet.NameEntry createExcludesFile() {
         if (isReference()) {
             throw noChildrenAllowed();
         }
         directoryScanner = null;
         return defaultPatterns.createExcludesFile();
     }
 
     /**
      * Creates a single file fileset.
      * @param file the single <code>File</code> included in this
      *             <code>AbstractFileSet</code>.
      */
     public synchronized void setFile(File file) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         setDir(file.getParentFile());
         createInclude().setName(file.getName());
     }
 
     /**
      * Appends <code>includes</code> to the current list of include
      * patterns.
      *
      * <p>Patterns may be separated by a comma or a space.</p>
      *
      * @param includes the <code>String</code> containing the include patterns.
      */
     public synchronized void setIncludes(String includes) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         defaultPatterns.setIncludes(includes);
         directoryScanner = null;
     }
 
     /**
      * Appends <code>includes</code> to the current list of include
      * patterns.
      *
      * @param includes array containing the include patterns.
      * @since Ant 1.7
      */
     public synchronized void appendIncludes(String[] includes) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         if (includes != null) {
             for (int i = 0; i < includes.length; i++) {
                 defaultPatterns.createInclude().setName(includes[i]);
             }
             directoryScanner = null;
         }
     }
 
     /**
      * Appends <code>excludes</code> to the current list of exclude
      * patterns.
      *
      * <p>Patterns may be separated by a comma or a space.</p>
      *
      * @param excludes the <code>String</code> containing the exclude patterns.
      */
     public synchronized void setExcludes(String excludes) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         defaultPatterns.setExcludes(excludes);
         directoryScanner = null;
     }
 
     /**
      * Appends <code>excludes</code> to the current list of include
      * patterns.
      *
      * @param excludes array containing the exclude patterns.
      * @since Ant 1.7
      */
     public synchronized void appendExcludes(String[] excludes) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         if (excludes != null) {
             for (int i = 0; i < excludes.length; i++) {
                 defaultPatterns.createExclude().setName(excludes[i]);
             }
             directoryScanner = null;
         }
     }
 
     /**
      * Sets the <code>File</code> containing the includes patterns.
      *
      * @param incl <code>File</code> instance.
      * @throws BuildException on error
      */
     public synchronized void setIncludesfile(File incl) throws BuildException {
         if (isReference()) {
             throw tooManyAttributes();
         }
         defaultPatterns.setIncludesfile(incl);
         directoryScanner = null;
     }
 
     /**
      * Sets the <code>File</code> containing the excludes patterns.
      *
      * @param excl <code>File</code> instance.
      * @throws BuildException on error
      */
     public synchronized void setExcludesfile(File excl) throws BuildException {
         if (isReference()) {
             throw tooManyAttributes();
         }
         defaultPatterns.setExcludesfile(excl);
         directoryScanner = null;
     }
 
     /**
      * Sets whether default exclusions should be used or not.
      *
      * @param useDefaultExcludes <code>boolean</code>.
      */
     public synchronized void setDefaultexcludes(boolean useDefaultExcludes) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         this.useDefaultExcludes = useDefaultExcludes;
         directoryScanner = null;
     }
 
     /**
      * Whether default exclusions should be used or not.
      * @return the default exclusions value.
      * @since Ant 1.6.3
      */
     public synchronized boolean getDefaultexcludes() {
         return (isReference())
             ? getRef(getProject()).getDefaultexcludes() : useDefaultExcludes;
     }
 
     /**
      * Sets case sensitivity of the file system.
      *
      * @param caseSensitive <code>boolean</code>.
      */
     public synchronized void setCaseSensitive(boolean caseSensitive) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         this.caseSensitive = caseSensitive;
         directoryScanner = null;
     }
 
     /**
      * Find out if the fileset is case sensitive.
      *
      * @return <code>boolean</code> indicating whether the fileset is
      * case sensitive.
      *
      * @since Ant 1.7
      */
     public synchronized boolean isCaseSensitive() {
         return (isReference())
             ? getRef(getProject()).isCaseSensitive() : caseSensitive;
     }
 
     /**
      * Sets whether or not symbolic links should be followed.
      *
      * @param followSymlinks whether or not symbolic links should be followed.
      */
     public synchronized void setFollowSymlinks(boolean followSymlinks) {
         if (isReference()) {
             throw tooManyAttributes();
         }
         this.followSymlinks = followSymlinks;
         directoryScanner = null;
     }
 
     /**
      * Find out if the fileset wants to follow symbolic links.
      *
      * @return <code>boolean</code> indicating whether symbolic links
      *         should be followed.
      *
      * @since Ant 1.6
      */
     public synchronized boolean isFollowSymlinks() {
         return (isReference())
             ? getRef(getProject()).isFollowSymlinks() : followSymlinks;
     }
 
     /**
+     * The maximum number of times a symbolic link may be followed
+     * during a scan.
+     *
+     * @since Ant 1.8.0
+     */
+    public void setMaxLevelsOfSymlinks(int max) {
+        maxLevelsOfSymlinks = max;
+    }
+
+    /**
      * Sets whether an error is thrown if a directory does not exist.
      *
      * @param errorOnMissingDir true if missing directories cause errors,
      *                        false if not.
      */
      public void setErrorOnMissingDir(boolean errorOnMissingDir) {
          this.errorOnMissingDir = errorOnMissingDir;
      }
 
     /**
      * Returns the directory scanner needed to access the files to process.
      * @return a <code>DirectoryScanner</code> instance.
      */
     public DirectoryScanner getDirectoryScanner() {
         return getDirectoryScanner(getProject());
     }
 
     /**
      * Returns the directory scanner needed to access the files to process.
      * @param p the Project against which the DirectoryScanner should be configured.
      * @return a <code>DirectoryScanner</code> instance.
      */
     public DirectoryScanner getDirectoryScanner(Project p) {
         if (isReference()) {
             return getRef(p).getDirectoryScanner(p);
         }
         DirectoryScanner ds = null;
         synchronized (this) {
             if (directoryScanner != null && p == getProject()) {
                 ds = directoryScanner;
             } else {
                 if (dir == null) {
                     throw new BuildException("No directory specified for "
                                              + getDataTypeName() + ".");
                 }
                 if (!dir.exists() && errorOnMissingDir) {
                     throw new BuildException(dir.getAbsolutePath()
                                              + " does not exist.");
                 }
                 if (!dir.isDirectory() && dir.exists()) {
                     throw new BuildException(dir.getAbsolutePath()
                                              + " is not a directory.");
                 }
                 ds = new DirectoryScanner();
                 setupDirectoryScanner(ds, p);
                 ds.setFollowSymlinks(followSymlinks);
                 ds.setErrorOnMissingDir(errorOnMissingDir);
+                ds.setMaxLevelsOfSymlinks(maxLevelsOfSymlinks);
                 directoryScanner = (p == getProject()) ? ds : directoryScanner;
             }
         }
         ds.scan();
         return ds;
     }
 
     /**
      * Set up the specified directory scanner against this
      * AbstractFileSet's Project.
      * @param ds a <code>FileScanner</code> instance.
      */
     public void setupDirectoryScanner(FileScanner ds) {
         setupDirectoryScanner(ds, getProject());
     }
 
     /**
      * Set up the specified directory scanner against the specified project.
      * @param ds a <code>FileScanner</code> instance.
      * @param p an Ant <code>Project</code> instance.
      */
     public synchronized void setupDirectoryScanner(FileScanner ds, Project p) {
         if (isReference()) {
             getRef(p).setupDirectoryScanner(ds, p);
             return;
         }
         if (ds == null) {
             throw new IllegalArgumentException("ds cannot be null");
         }
         ds.setBasedir(dir);
 
         PatternSet ps = mergePatterns(p);
         p.log(getDataTypeName() + ": Setup scanner in dir " + dir
             + " with " + ps, Project.MSG_DEBUG);
 
         ds.setIncludes(ps.getIncludePatterns(p));
         ds.setExcludes(ps.getExcludePatterns(p));
         if (ds instanceof SelectorScanner) {
             SelectorScanner ss = (SelectorScanner) ds;
             ss.setSelectors(getSelectors(p));
         }
         if (useDefaultExcludes) {
             ds.addDefaultExcludes();
         }
         ds.setCaseSensitive(caseSensitive);
     }
 
     /**
      * Performs the check for circular references and returns the
      * referenced FileSet.
      * @param p the current project
      * @return the referenced FileSet
      */
     protected AbstractFileSet getRef(Project p) {
         return (AbstractFileSet) getCheckedRef(p);
     }
 
     // SelectorContainer methods
 
     /**
      * Indicates whether there are any selectors here.
      *
      * @return whether any selectors are in this container.
      */
     public synchronized boolean hasSelectors() {
         return (isReference() && getProject() != null)
             ? getRef(getProject()).hasSelectors() : !(selectors.isEmpty());
     }
 
     /**
      * Indicates whether there are any patterns here.
      *
      * @return whether any patterns are in this container.
      */
     public synchronized boolean hasPatterns() {
         if (isReference() && getProject() != null) {
             return getRef(getProject()).hasPatterns();
         }
         if (defaultPatterns.hasPatterns(getProject())) {
             return true;
         }
         Enumeration e = additionalPatterns.elements();
         while (e.hasMoreElements()) {
             PatternSet ps = (PatternSet) e.nextElement();
             if (ps.hasPatterns(getProject())) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Gives the count of the number of selectors in this container.
      *
      * @return the number of selectors in this container as an <code>int</code>.
      */
     public synchronized int selectorCount() {
         return (isReference() && getProject() != null)
             ? getRef(getProject()).selectorCount() : selectors.size();
     }
 
     /**
      * Returns the set of selectors as an array.
      * @param p the current project
      * @return a <code>FileSelector[]</code> of the selectors in this container.
      */
     public synchronized FileSelector[] getSelectors(Project p) {
         return (isReference())
             ? getRef(p).getSelectors(p) : (FileSelector[]) (selectors.toArray(
             new FileSelector[selectors.size()]));
     }
 
     /**
      * Returns an enumerator for accessing the set of selectors.
      *
      * @return an <code>Enumeration</code> of selectors.
      */
     public synchronized Enumeration selectorElements() {
         return (isReference() && getProject() != null)
             ? getRef(getProject()).selectorElements() : selectors.elements();
     }
 
     /**
      * Add a new selector into this container.
      *
      * @param selector the new <code>FileSelector</code> to add.
      */
     public synchronized void appendSelector(FileSelector selector) {
         if (isReference()) {
             throw noChildrenAllowed();
         }
         selectors.addElement(selector);
         directoryScanner = null;
     }
 
     /* Methods below all add specific selectors */
 
     /**
      * Add a "Select" selector entry on the selector list.
      * @param selector the <code>SelectSelector</code> to add.
      */
     public void addSelector(SelectSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add an "And" selector entry on the selector list.
      * @param selector the <code>AndSelector</code> to add.
      */
     public void addAnd(AndSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add an "Or" selector entry on the selector list.
      * @param selector the <code>OrSelector</code> to add.
      */
     public void addOr(OrSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a "Not" selector entry on the selector list.
      * @param selector the <code>NotSelector</code> to add.
      */
     public void addNot(NotSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a "None" selector entry on the selector list.
      * @param selector the <code>NoneSelector</code> to add.
      */
     public void addNone(NoneSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a majority selector entry on the selector list.
      * @param selector the <code>MajoritySelector</code> to add.
      */
     public void addMajority(MajoritySelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a selector date entry on the selector list.
      * @param selector the <code>DateSelector</code> to add.
      */
     public void addDate(DateSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a selector size entry on the selector list.
      * @param selector the <code>SizeSelector</code> to add.
      */
     public void addSize(SizeSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a DifferentSelector entry on the selector list.
      * @param selector the <code>DifferentSelector</code> to add.
      */
     public void addDifferent(DifferentSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a selector filename entry on the selector list.
      * @param selector the <code>FilenameSelector</code> to add.
      */
     public void addFilename(FilenameSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a selector type entry on the selector list.
      * @param selector the <code>TypeSelector</code> to add.
      */
     public void addType(TypeSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add an extended selector entry on the selector list.
      * @param selector the <code>ExtendSelector</code> to add.
      */
     public void addCustom(ExtendSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a contains selector entry on the selector list.
      * @param selector the <code>ContainsSelector</code> to add.
      */
     public void addContains(ContainsSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a present selector entry on the selector list.
      * @param selector the <code>PresentSelector</code> to add.
      */
     public void addPresent(PresentSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a depth selector entry on the selector list.
      * @param selector the <code>DepthSelector</code> to add.
      */
     public void addDepth(DepthSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a depends selector entry on the selector list.
      * @param selector the <code>DependSelector</code> to add.
      */
     public void addDepend(DependSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add a regular expression selector entry on the selector list.
      * @param selector the <code>ContainsRegexpSelector</code> to add.
      */
     public void addContainsRegexp(ContainsRegexpSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Add the modified selector.
      * @param selector the <code>ModifiedSelector</code> to add.
      * @since ant 1.6
      */
     public void addModified(ModifiedSelector selector) {
         appendSelector(selector);
     }
 
     public void addReadable(ReadableSelector r) {
         appendSelector(r);
     }
 
     public void addWritable(WritableSelector w) {
         appendSelector(w);
     }
 
     /**
      * Add an arbitary selector.
      * @param selector the <code>FileSelector</code> to add.
      * @since Ant 1.6
      */
     public void add(FileSelector selector) {
         appendSelector(selector);
     }
 
     /**
      * Returns included files as a list of semicolon-separated filenames.
      *
      * @return a <code>String</code> of included filenames.
      */
     public String toString() {
         DirectoryScanner ds = getDirectoryScanner(getProject());
         String[] files = ds.getIncludedFiles();
         StringBuffer sb = new StringBuffer();
 
         for (int i = 0; i < files.length; i++) {
             if (i > 0) {
                 sb.append(';');
             }
             sb.append(files[i]);
         }
         return sb.toString();
     }
 
     /**
      * Creates a deep clone of this instance, except for the nested
      * selectors (the list of selectors is a shallow clone of this
      * instance's list).
      * @return the cloned object
      * @since Ant 1.6
      */
     public synchronized Object clone() {
         if (isReference()) {
             return (getRef(getProject())).clone();
         } else {
             try {
                 AbstractFileSet fs = (AbstractFileSet) super.clone();
                 fs.defaultPatterns = (PatternSet) defaultPatterns.clone();
                 fs.additionalPatterns = new Vector(additionalPatterns.size());
                 Enumeration e = additionalPatterns.elements();
                 while (e.hasMoreElements()) {
                     fs.additionalPatterns
                         .addElement(((PatternSet) e.nextElement()).clone());
                 }
                 fs.selectors = new Vector(selectors);
                 return fs;
             } catch (CloneNotSupportedException e) {
                 throw new BuildException(e);
             }
         }
     }
 
     /**
      * Get the merged include patterns for this AbstractFileSet.
      * @param p the project to use.
      * @return the include patterns of the default pattern set and all
      * nested patternsets.
      *
      * @since Ant 1.7
      */
     public String[] mergeIncludes(Project p) {
         return mergePatterns(p).getIncludePatterns(p);
     }
 
     /**
      * Get the merged exclude patterns for this AbstractFileSet.
      * @param p the project to use.
      * @return the exclude patterns of the default pattern set and all
      * nested patternsets.
      *
      * @since Ant 1.7
      */
     public String[] mergeExcludes(Project p) {
         return mergePatterns(p).getExcludePatterns(p);
     }
 
     /**
      * Get the merged patterns for this AbstractFileSet.
      * @param p the project to use.
      * @return the default patternset merged with the additional sets
      * in a new PatternSet instance.
      *
      * @since Ant 1.7
      */
     public synchronized PatternSet mergePatterns(Project p) {
         if (isReference()) {
             return getRef(p).mergePatterns(p);
         }
         PatternSet ps = (PatternSet) defaultPatterns.clone();
         final int count = additionalPatterns.size();
         for (int i = 0; i < count; i++) {
             Object o = additionalPatterns.elementAt(i);
             ps.append((PatternSet) o, p);
         }
         return ps;
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/util/CollectionUtils.java b/src/main/org/apache/tools/ant/util/CollectionUtils.java
index 375548357..bb5be8ca6 100644
--- a/src/main/org/apache/tools/ant/util/CollectionUtils.java
+++ b/src/main/org/apache/tools/ant/util/CollectionUtils.java
@@ -1,225 +1,245 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 package org.apache.tools.ant.util;
 
 import java.util.Collection;
 import java.util.Vector;
 import java.util.Iterator;
 import java.util.Dictionary;
 import java.util.Enumeration;
 import java.util.NoSuchElementException;
 
 // CheckStyle:HideUtilityClassConstructorCheck OFF - bc
 
 /**
  * A set of helper methods related to collection manipulation.
  *
  * @since Ant 1.5
  */
 public class CollectionUtils {
 
     /**
      * Please use Vector.equals() or List.equals().
      * @param v1 the first vector.
      * @param v2 the second vector.
      * @return true if the vectors are equal.
      * @since Ant 1.5
      * @deprecated since 1.6.x.
      */
     public static boolean equals(Vector v1, Vector v2) {
         if (v1 == v2) {
             return true;
         }
 
         if (v1 == null || v2 == null) {
             return false;
         }
 
         return v1.equals(v2);
     }
 
     /**
      * Dictionary does not have an equals.
      * Please use  Map.equals().
      *
      * <p>Follows the equals contract of Java 2's Map.</p>
      * @param d1 the first directory.
      * @param d2 the second directory.
      * @return true if the directories are equal.
      * @since Ant 1.5
      * @deprecated since 1.6.x.
      */
     public static boolean equals(Dictionary d1, Dictionary d2) {
         if (d1 == d2) {
             return true;
         }
 
         if (d1 == null || d2 == null) {
             return false;
         }
 
         if (d1.size() != d2.size()) {
             return false;
         }
 
         Enumeration e1 = d1.keys();
         while (e1.hasMoreElements()) {
             Object key = e1.nextElement();
             Object value1 = d1.get(key);
             Object value2 = d2.get(key);
             if (value2 == null || !value1.equals(value2)) {
                 return false;
             }
         }
 
         // don't need the opposite check as the Dictionaries have the
         // same size, so we've also covered all keys of d2 already.
 
         return true;
     }
 
     /**
      * Creates a comma separated list of all values held in the given
      * collection.
      *
      * @since Ant 1.8.0
      */
     public static String flattenToString(Collection c) {
         Iterator iter = c.iterator();
         boolean first = true;
         StringBuffer sb = new StringBuffer();
         while (iter.hasNext()) {
             if (!first) {
                 sb.append(",");
             }
             sb.append(String.valueOf(iter.next()));
             first = false;
         }
         return sb.toString();
     }
 
     /**
      * Dictionary does not know the putAll method. Please use Map.putAll().
      * @param m1 the to directory.
      * @param m2 the from directory.
      * @since Ant 1.6
      * @deprecated since 1.6.x.
      */
     public static void putAll(Dictionary m1, Dictionary m2) {
         for (Enumeration it = m2.keys(); it.hasMoreElements();) {
             Object key = it.nextElement();
             m1.put(key, m2.get(key));
         }
     }
 
     /**
      * An empty enumeration.
      * @since Ant 1.6
      */
     public static final class EmptyEnumeration implements Enumeration {
         /** Constructor for the EmptyEnumeration */
         public EmptyEnumeration() {
         }
 
         /**
          * @return false always.
          */
         public boolean hasMoreElements() {
             return false;
         }
 
         /**
          * @return nothing.
          * @throws NoSuchElementException always.
          */
         public Object nextElement() throws NoSuchElementException {
             throw new NoSuchElementException();
         }
     }
 
     /**
      * Append one enumeration to another.
      * Elements are evaluated lazily.
      * @param e1 the first enumeration.
      * @param e2 the subsequent enumeration.
      * @return an enumeration representing e1 followed by e2.
      * @since Ant 1.6.3
      */
     public static Enumeration append(Enumeration e1, Enumeration e2) {
         return new CompoundEnumeration(e1, e2);
     }
 
     /**
      * Adapt the specified Iterator to the Enumeration interface.
      * @param iter the Iterator to adapt.
      * @return an Enumeration.
      */
     public static Enumeration asEnumeration(final Iterator iter) {
         return new Enumeration() {
             public boolean hasMoreElements() {
                 return iter.hasNext();
             }
             public Object nextElement() {
                 return iter.next();
             }
         };
     }
 
     /**
      * Adapt the specified Enumeration to the Iterator interface.
      * @param e the Enumeration to adapt.
      * @return an Iterator.
      */
     public static Iterator asIterator(final Enumeration e) {
         return new Iterator() {
             public boolean hasNext() {
                 return e.hasMoreElements();
             }
             public Object next() {
                 return e.nextElement();
             }
             public void remove() {
                 throw new UnsupportedOperationException();
             }
         };
     }
 
     private static final class CompoundEnumeration implements Enumeration {
 
         private final Enumeration e1, e2;
 
         public CompoundEnumeration(Enumeration e1, Enumeration e2) {
             this.e1 = e1;
             this.e2 = e2;
         }
 
         public boolean hasMoreElements() {
             return e1.hasMoreElements() || e2.hasMoreElements();
         }
 
         public Object nextElement() throws NoSuchElementException {
             if (e1.hasMoreElements()) {
                 return e1.nextElement();
             } else {
                 return e2.nextElement();
             }
         }
 
     }
 
+    /**
+     * Counts how often the given Object occurs in the given
+     * collection using equals() for comparison.
+     *
+     * @since Ant 1.8.0
+     */
+    public static int frequency(Collection c, Object o) {
+        // same as Collections.frequency introduced with JDK 1.5
+        int freq = 0;
+        if (c != null) {
+            for (Iterator i = c.iterator(); i.hasNext(); ) {
+                Object test = i.next();
+                if (o == null ? test == null : o.equals(test)) {
+                    freq++;
+                }
+            }
+        }
+        return freq;
+    }
+            
 }
diff --git a/src/tests/antunit/core/dirscanner-symlinks-test.xml b/src/tests/antunit/core/dirscanner-symlinks-test.xml
index 551338ebe..0645946af 100644
--- a/src/tests/antunit/core/dirscanner-symlinks-test.xml
+++ b/src/tests/antunit/core/dirscanner-symlinks-test.xml
@@ -1,166 +1,200 @@
 <?xml version="1.0"?>
 <!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 -->
 <project xmlns:au="antlib:org.apache.ant.antunit" default="antunit">
 
   <import file="../antunit-base.xml"/>
 
   <target name="setUp">
     <property name="base" location="${input}/base"/>
     <mkdir dir="${base}"/>
   </target>
 
   <target name="checkOs">
     <condition property="unix"><os family="unix"/></condition>
   </target>
 
   <macrodef name="assertDirIsEmpty">
     <attribute name="dir" default="${output}"/>
     <sequential>
       <local name="resources"/>
       <resourcecount property="resources">
         <fileset dir="@{dir}"/>
       </resourcecount>
       <au:assertEquals expected="0" actual="${resources}"/>
     </sequential>
   </macrodef>
 
   <target name="testSymlinkToSiblingFollow"
           depends="checkOs, setUp, -sibling"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true"/>
     </copy>
     <au:assertFileExists file="${output}/B/file.txt"/>
   </target>
 
   <target name="testSymlinkToSiblingNoFollow"
           depends="checkOs, setUp, -sibling"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <au:assertFileDoesntExist file="${output}/B/file.txt"/>
   </target>
 
   <target name="testBasedirIsSymlinkFollow"
           depends="checkOs, setUp, -basedir-as-symlink"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true"/>
     </copy>
     <au:assertFileExists file="${output}/file.txt"/>
   </target>
 
   <target name="testBasedirIsSymlinkNoFollow"
           depends="checkOs, setUp, -basedir-as-symlink"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <au:assertFileDoesntExist file="${output}/file.txt"/>
   </target>
 
-  <target name="INFINITEtestLinkToParentFollow"
+  <target name="testLinkToParentFollow"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
-      <fileset dir="${base}" followsymlinks="true"/>
+      <fileset dir="${base}" followsymlinks="true" maxLevelsOfSymlinks="1"/>
+    </copy>
+    <symlink action="delete" link="${base}/A"/>
+    <au:assertFileExists file="${output}/A/B/file.txt"/>
+    <au:assertFileDoesntExist file="${output}/A/base/A/B/file.txt"/>
+  </target>
+
+  <target name="testLinkToParentFollowMax2"
+          depends="checkOs, setUp, -link-to-parent"
+          if="unix">
+    <copy todir="${output}">
+      <fileset dir="${base}" followsymlinks="true" maxLevelsOfSymlinks="2"/>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileExists file="${output}/A/B/file.txt"/>
+    <au:assertFileExists file="${output}/A/base/A/B/file.txt"/>
   </target>
 
   <target name="testLinkToParentFollowWithInclude"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true">
         <include name="A/B/*"/>
       </fileset>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileExists file="${output}/A/B/file.txt"/>
   </target>
 
   <!-- supposed to fail? -->
   <target name="testLinkToParentFollowWithIncludeMultiFollow"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true">
         <include name="A/base/A/B/*"/>
       </fileset>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileExists file="${output}/A/base/A/B/file.txt"/>
   </target>
 
   <target name="testLinkToParentNoFollow"
           depends="checkOs, setUp, -link-to-parent"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <symlink action="delete" link="${base}/A"/>
     <au:assertFileDoesntExist file="${output}/A/B/file.txt"/>
   </target>
 
-  <target name="INFINITE testSillyLoopFollow"
+  <target name="testSillyLoopFollow"
           depends="checkOs, setUp, -silly-loop"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="true"/>
     </copy>
     <symlink action="delete" link="${base}"/>
     <assertDirIsEmpty/>
   </target>
 
   <target name="testSillyLoopNoFollow"
           depends="checkOs, setUp, -silly-loop"
           if="unix">
     <copy todir="${output}">
       <fileset dir="${base}" followsymlinks="false"/>
     </copy>
     <symlink action="delete" link="${base}"/>
     <au:assertFileDoesntExist file="${output}"/>
   </target>
 
+  <target name="testRepeatedName"
+          depends="setUp">
+    <mkdir dir="${base}/A/A/A/A"/>
+    <touch file="${base}/A/A/A/A/file.txt"/>
+    <copy todir="${output}">
+      <fileset dir="${base}" followsymlinks="true" maxLevelsOfSymlinks="1"/>
+    </copy>
+    <au:assertFileExists file="${output}/A/A/A/A/file.txt"/>
+  </target>
+
+  <target name="testRepeatedNameWithLinkButNoLoop"
+          depends="checkOs, setUp"
+          if="unix">
+    <mkdir dir="${base}/A/A/A/B"/>
+    <touch file="${base}/A/A/A/B/file.txt"/>
+    <symlink link="${base}/A/A/A/A" resource="${base}/A/A/A/B"/>
+    <copy todir="${output}">
+      <fileset dir="${base}" followsymlinks="true" maxLevelsOfSymlinks="1"/>
+    </copy>
+    <au:assertFileExists file="${output}/A/A/A/A/file.txt"/>
+  </target>
+
   <target name="-sibling" if="unix">
     <mkdir dir="${base}/A"/>
     <touch file="${base}/A/file.txt"/>
     <symlink link="${base}/B" resource="${base}/A"/>
   </target>
 
   <target name="-basedir-as-symlink" if="unix">
     <delete dir="${base}"/>
     <mkdir dir="${input}/realdir"/>
     <touch file="${input}/realdir/file.txt"/>
     <symlink link="${base}" resource="${input}/realdir"/>
   </target>    
 
   <target name="-link-to-parent" if="unix">
     <mkdir dir="${input}/B"/>
     <touch file="${input}/B/file.txt"/>
     <symlink link="${base}/A" resource="${input}"/>
   </target>
 
   <target name="-silly-loop" if="unix">
     <delete dir="${base}"/>
     <symlink link="${base}" resource="${input}"/>
   </target>
 </project>
