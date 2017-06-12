diff --git a/src/etc/testcases/taskdefs/sync.xml b/src/etc/testcases/taskdefs/sync.xml
index 36fbe88b5..67602b136 100644
--- a/src/etc/testcases/taskdefs/sync.xml
+++ b/src/etc/testcases/taskdefs/sync.xml
@@ -1,64 +1,78 @@
 <?xml version="1.0"?>
 <project name="sync-test" default="not-me">
   <property name="scratch" location="synctest"/>
 
   <target name="not-me">
     <fail>This file must be used from a test case</fail>
   </target>
 
   <target name="cleanup">
     <delete dir="${scratch}"/>
   </target>
 
   <target name="setup">
     <property name="src" location="${scratch}/source"/>
     <property name="dest" location="${scratch}/target"/>
     <mkdir dir="${src}"/>
     <mkdir dir="${dest}"/>
   </target>
 
   <target name="simplecopy" depends="setup">
     <mkdir dir="${src}/a/b/c"/>
     <touch file="${src}/a/b/c/d"/>
     <sync todir="${dest}">
       <fileset dir="${src}"/>
     </sync>
   </target>
 
   <target name="copyandremove" depends="setup">
     <mkdir dir="${src}/a/b/c"/>
     <touch file="${src}/a/b/c/d"/>
     <mkdir dir="${dest}/e"/>
     <touch file="${dest}/e/f"/>
     <sync todir="${dest}">
       <fileset dir="${src}"/>
     </sync>
   </target>
 
   <target name="emptycopy" depends="setup">
     <mkdir dir="${src}/a/b/c"/>
     <touch file="${src}/a/b/c/d"/>
     <sync todir="${dest}">
       <fileset dir="${src}" excludes="**/d"/>
     </sync>
   </target>
 
   <target name="emptydircopy" depends="setup">
     <mkdir dir="${src}/a/b/c"/>
     <touch file="${src}/a/b/c/d"/>
     <sync todir="${dest}"
          includeemptydirs="true">
       <fileset dir="${src}" excludes="**/d"/>
     </sync>
   </target>
 
   <target name="emptydircopyandremove" depends="setup">
     <mkdir dir="${src}/a/b/c"/>
     <touch file="${src}/a/b/c/d"/>
     <mkdir dir="${dest}/e/f"/>
     <sync todir="${dest}"
          includeemptydirs="true">
       <fileset dir="${src}" excludes="**/d"/>
     </sync>
   </target>
+
+  <target name="copynoremove" depends="setup">
+    <mkdir dir="${src}/a/b/c"/>
+    <touch file="${src}/a/b/c/d"/>
+    <mkdir dir="${dest}/e"/>
+    <touch file="${dest}/e/f"/>
+    <sync todir="${dest}">
+      <fileset dir="${src}"/>
+      <deletefromtarget>
+        <exclude name="e/f"/>
+      </deletefromtarget>
+    </sync>
+  </target>
+
 </project>
\ No newline at end of file
diff --git a/src/main/org/apache/tools/ant/DirectoryScanner.java b/src/main/org/apache/tools/ant/DirectoryScanner.java
index 7ae98a7f4..039421ad8 100644
--- a/src/main/org/apache/tools/ant/DirectoryScanner.java
+++ b/src/main/org/apache/tools/ant/DirectoryScanner.java
@@ -1,1542 +1,1579 @@
 /*
  * Copyright  2000-2005 The Apache Software Foundation
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
 import java.util.ArrayList;
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
 
     /** Helper. */
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
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
 
     /** Whether or not everything tested so far has been included. */
     protected boolean everythingIncluded = true;
 
     /**
      * Set of all include patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to lower case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.7
      */
     private Set includeNonPatterns = new HashSet();
 
     /**
      * Set of all include patterns that are full file names and don't
      * contain any wildcards.
      *
      * <p>If this instance is not case sensitive, the file names get
      * turned to lower case.</p>
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.7
      */
     private Set excludeNonPatterns = new HashSet();
 
     /**
      * Array of all include patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.7
      */
     private String[] includePatterns;
 
     /**
      * Array of all exclude patterns that contain wildcards.
      *
      * <p>Gets lazily initialized on the first invocation of
      * isIncluded or isExcluded and cleared at the end of the scan
      * method (cleared in clearCaches, actually).</p>
      *
      * @since Ant 1.7
      */
     private String[] excludePatterns;
 
     /**
      * Have the non-pattern sets and pattern arrays for in- and
      * excludes been initialized?
      *
      * @since Ant 1.7
      */
     private boolean areNonPatternSetsReady = false;
 
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
-                String pattern;
-                pattern = includes[i].replace('/', File.separatorChar).replace(
-                        '\\', File.separatorChar);
-                if (pattern.endsWith(File.separator)) {
-                    pattern += "**";
-                }
-                this.includes[i] = pattern;
+                this.includes[i] = normalizePattern(includes[i]);
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
-                String pattern;
-                pattern = excludes[i].replace('/', File.separatorChar).replace(
-                        '\\', File.separatorChar);
-                if (pattern.endsWith(File.separator)) {
-                    pattern += "**";
+                this.excludes[i] = normalizePattern(excludes[i]);
+            }
+        }
+    }
+
+    /**
+     * Adds to the list of exclude patterns to use. All '/' and '\'
+     * characters are replaced by <code>File.separatorChar</code>, so
+     * the separator used need not match
+     * <code>File.separatorChar</code>.
+     * <p>
+     * When a pattern ends with a '/' or '\', "**" is appended.
+     *
+     * @param excludes A list of exclude patterns.
+     *                 May be <code>null</code>, in which case the
+     *                 exclude patterns don't get changed at all.
+     *
+     * @since Ant 1.7
+     */
+    public void addExcludes(String[] excludes) {
+        if (excludes != null) {
+            if (this.excludes != null) {
+                String[] tmp = new String[excludes.length 
+                                          + this.excludes.length];
+                System.arraycopy(this.excludes, 0, tmp, 0, 
+                                 this.excludes.length);
+                for (int i = 0; i < excludes.length; i++) {
+                    tmp[this.excludes.length + i] = 
+                        normalizePattern(excludes[i]);
                 }
-                this.excludes[i] = pattern;
+                this.excludes = tmp;
+            } else {
+                setExcludes(excludes);
             }
         }
     }
 
+    /**
+     * All '/' and '\' characters are replaced by
+     * <code>File.separatorChar</code>, so the separator used need not
+     * match <code>File.separatorChar</code>.
+     *
+     * <p> When a pattern ends with a '/' or '\', "**" is appended.
+     *
+     * @since Ant 1.7
+     */
+    private static String normalizePattern(String p) {
+        String pattern = p.replace('/', File.separatorChar)
+            .replace('\\', File.separatorChar);
+        if (pattern.endsWith(File.separator)) {
+            pattern += "**";
+        }
+        return pattern;
+    }
 
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
 
                 if ((myfile == null || !myfile.exists()) && !isCaseSensitive()) {
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
                         if (isCaseSensitive()
                             && originalpattern.equals(currentelement)) {
                             accountForIncludedFile(currentelement, myfile);
                         } else if (!isCaseSensitive()
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
      * Process included file.
      * @param name  path of the file relative to the directory of the FileSet.
      * @param file  included File.
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
      * Process included directory.
      * @param name path of the directory relative to the directory of
      *             the FileSet.
      * @param file directory as File.
      * @param fast whether to perform fast scans.
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
         ensureNonPatternSetsReady();
 
         if ((isCaseSensitive() && includeNonPatterns.contains(name))
             ||
             (!isCaseSensitive() 
              && includeNonPatterns.contains(name.toUpperCase()))) {
                 return true;
         }
 
         for (int i = 0; i < includePatterns.length; i++) {
             if (matchPath(includePatterns[i], name, isCaseSensitive())) {
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
             if (matchPatternStart(includes[i], name, isCaseSensitive())) {
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
         ensureNonPatternSetsReady();
 
         if ((isCaseSensitive() && excludeNonPatterns.contains(name))
             ||
             (!isCaseSensitive() 
              && excludeNonPatterns.contains(name.toUpperCase()))) {
                 return true;
         }
 
         for (int i = 0; i < excludePatterns.length; i++) {
             if (matchPath(excludePatterns[i], name, isCaseSensitive())) {
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
         if (filesIncluded == null) {
             throw new IllegalStateException();
         }
         String[] files = new String[filesIncluded.size()];
         filesIncluded.copyInto(files);
         Arrays.sort(files);
         return files;
     }
 
     /**
      * Return the count of included files.
      * @return <CODE>int</CODE>.
      * @since Ant 1.6.3
      */
     public int getIncludedFilesCount() {
         if (filesIncluded == null) {
             throw new IllegalStateException();
         }
         return filesIncluded.size();
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
         if (dirsIncluded == null) {
             throw new IllegalStateException();
         }
         String[] directories = new String[dirsIncluded.size()];
         dirsIncluded.copyInto(directories);
         Arrays.sort(directories);
         return directories;
     }
 
     /**
      * Return the count of included directories.
      * @return <CODE>int</CODE>.
      * @since Ant 1.6.3
      */
     public int getIncludedDirsCount() {
         if (dirsIncluded == null) {
             throw new IllegalStateException();
         }
         return dirsIncluded.size();
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
     private synchronized void clearCaches() {
         fileListMap.clear();
         scannedDirs.clear();
         includeNonPatterns.clear();
         excludeNonPatterns.clear();
         includePatterns = excludePatterns = null;
         areNonPatternSetsReady = false;
     }
 
     /**
      * Ensure that the in|exclude &quot;patterns&quot;
      * have been properly divided up.
      *
      * @since Ant 1.7
      */
     private synchronized void ensureNonPatternSetsReady() {
         if (!areNonPatternSetsReady) {
             includePatterns = fillNonPatternSet(includeNonPatterns, includes);
             excludePatterns = fillNonPatternSet(excludeNonPatterns, excludes);
             areNonPatternSetsReady = true;
         }
     }
 
     /**
      * Adds all patterns that are not real patterns (do not contain
      * wildcards) to the set and returns the real patterns.
      *
      * @since Ant 1.7
      */
     private String[] fillNonPatternSet(Set set, String[] patterns) {
         ArrayList al = new ArrayList(patterns.length);
         for (int i = 0; i < patterns.length; i++) {
             if (!SelectorUtils.hasWildcards(patterns[i])) {
                 set.add(isCaseSensitive() ? patterns[i]
                     : patterns[i].toUpperCase());
             } else {
                 al.add(patterns[i]);
             }
         }
         return set.size() == 0 ? patterns
             : (String[]) al.toArray(new String[al.size()]);
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Sync.java b/src/main/org/apache/tools/ant/taskdefs/Sync.java
index 45b6acdd0..f3ecf01b1 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Sync.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Sync.java
@@ -1,348 +1,402 @@
 /*
  * Copyright  2003-2005 The Apache Software Foundation
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
 
 /*
  * This code is based on code Copyright (c) 2002, Landmark Graphics
  * Corp that has been kindly donated to the Apache Software
  * Foundation.
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
+import org.apache.tools.ant.types.AbstractFileSet;
 import org.apache.tools.ant.types.FileSet;
 
 /**
  * Synchronize a local target directory from the files defined
  * in one or more filesets.
  *
  * <p>Uses a &lt;copy&gt; task internally, but forbidding the use of
  * mappers and filter chains. Files of the destination directory not
  * present in any of the source fileset are removed.</p>
  *
  * @version $Revision$
  * @since Ant 1.6
  *
  * revised by <a href="mailto:daniel.armbrust@mayo.edu">Dan Armbrust</a>
  * to remove orphaned directories.
  *
  * @ant.task category="filesystem"
  */
 public class Sync extends Task {
 
     // Same as regular <copy> task... see at end-of-file!
     private MyCopy myCopy;
 
+    // Similar to a fileset, but doesn't allow dir attribute to be set
+    private SyncTarget syncTarget;
+
     // Override Task#init
     /**
      * @see Task#init()
      */
     public void init()
         throws BuildException {
         // Instantiate it
         myCopy = new MyCopy();
         configureTask(myCopy);
 
         // Default config of <mycopy> for our purposes.
         myCopy.setFiltering(false);
         myCopy.setIncludeEmptyDirs(false);
         myCopy.setPreserveLastModified(true);
     }
 
     private void configureTask(Task helper) {
         helper.setProject(getProject());
         helper.setTaskName(getTaskName());
         helper.setOwningTarget(getOwningTarget());
         helper.init();
     }
 
     // Override Task#execute
     /**
      * @see Task#execute()
      */
     public void execute()
         throws BuildException {
         // The destination of the files to copy
         File toDir = myCopy.getToDir();
 
         // The complete list of files to copy
         Set allFiles = myCopy.nonOrphans;
 
         // If the destination directory didn't already exist,
         // or was empty, then no previous file removal is necessary!
         boolean noRemovalNecessary = !toDir.exists() || toDir.list().length < 1;
 
         // Copy all the necessary out-of-date files
         log("PASS#1: Copying files to " + toDir, Project.MSG_DEBUG);
         myCopy.execute();
 
         // Do we need to perform further processing?
         if (noRemovalNecessary) {
             log("NO removing necessary in " + toDir, Project.MSG_DEBUG);
             return; // nope ;-)
         }
 
         // Get rid of all files not listed in the source filesets.
         log("PASS#2: Removing orphan files from " + toDir, Project.MSG_DEBUG);
         int[] removedFileCount = removeOrphanFiles(allFiles, toDir);
         logRemovedCount(removedFileCount[0], "dangling director", "y", "ies");
         logRemovedCount(removedFileCount[1], "dangling file", "", "s");
 
         // Get rid of empty directories on the destination side
         if (!myCopy.getIncludeEmptyDirs()) {
             log("PASS#3: Removing empty directories from " + toDir,
                 Project.MSG_DEBUG);
             int removedDirCount = removeEmptyDirectories(toDir, false);
             logRemovedCount(removedDirCount, "empty director", "y", "ies");
         }
     }
 
     private void logRemovedCount(int count, String prefix,
                                  String singularSuffix, String pluralSuffix) {
         File toDir = myCopy.getToDir();
 
         String what = (prefix == null) ? "" : prefix;
         what += (count < 2) ? singularSuffix : pluralSuffix;
 
         if (count > 0) {
             log("Removed " + count + " " + what + " from " + toDir,
                 Project.MSG_INFO);
         } else {
             log("NO " + what + " to remove from " + toDir,
                 Project.MSG_VERBOSE);
         }
     }
 
     /**
      * Removes all files and folders not found as keys of a table
      * (used as a set!).
      *
      * <p>If the provided file is a directory, it is recursively
      * scanned for orphaned files which will be removed as well.</p>
      *
      * <p>If the directory is an orphan, it will also be removed.</p>
      *
      * @param  nonOrphans the table of all non-orphan <code>File</code>s.
      * @param  file the initial file or directory to scan or test.
      * @return the number of orphaned files and directories actually removed.
      * Position 0 of the array is the number of orphaned directories.
      * Position 1 of the array is the number or orphaned files.
      */
     private int[] removeOrphanFiles(Set nonOrphans, File toDir) {
         int[] removedCount = new int[] {0, 0};
-        DirectoryScanner ds = new DirectoryScanner();
-        ds.setBasedir(toDir);
         String[] excls =
             (String[]) nonOrphans.toArray(new String[nonOrphans.size() + 1]);
         // want to keep toDir itself
         excls[nonOrphans.size()] = "";
-        ds.setExcludes(excls);
+
+        DirectoryScanner ds = null;
+        if (syncTarget != null) {
+            syncTarget.setTargetDir(toDir);
+            ds = syncTarget.getDirectoryScanner(getProject());
+        } else {
+            ds = new DirectoryScanner();
+            ds.setBasedir(toDir);
+        }
+        ds.addExcludes(excls);
+
         ds.scan();
         String[] files = ds.getIncludedFiles();
         for (int i = 0; i < files.length; i++) {
             File f = new File(toDir, files[i]);
             log("Removing orphan file: " + f, Project.MSG_DEBUG);
             f.delete();
             ++removedCount[1];
         }
         String[] dirs = ds.getIncludedDirectories();
         // ds returns the directories as it has visited them.
         // iterating through the array backwards means we are deleting
         // leaves before their parent nodes - thus making sure (well,
         // more likely) that the directories are empty when we try to
         // delete them.
         for (int i = dirs.length - 1; i >= 0; --i) {
             File f = new File(toDir, dirs[i]);
             log("Removing orphan directory: " + f, Project.MSG_DEBUG);
             f.delete();
             ++removedCount[0];
         }
         return removedCount;
     }
 
     /**
      * Removes all empty directories from a directory.
      *
      * <p><em>Note that a directory that contains only empty
      * directories, directly or not, will be removed!</em></p>
      *
      * <p>Recurses depth-first to find the leaf directories
      * which are empty and removes them, then unwinds the
      * recursion stack, removing directories which have
      * become empty themselves, etc...</p>
      *
      * @param  dir the root directory to scan for empty directories.
      * @param  removeIfEmpty whether to remove the root directory
      *         itself if it becomes empty.
      * @return the number of empty directories actually removed.
      */
     private int removeEmptyDirectories(File dir, boolean removeIfEmpty) {
         int removedCount = 0;
         if (dir.isDirectory()) {
             File[] children = dir.listFiles();
             for (int i = 0; i < children.length; ++i) {
                 File file = children[i];
                 // Test here again to avoid method call for non-directories!
                 if (file.isDirectory()) {
                     removedCount += removeEmptyDirectories(file, true);
                 }
             }
             if (children.length > 0) {
                 // This directory may have become empty...
                 // We need to re-query its children list!
                 children = dir.listFiles();
             }
             if (children.length < 1 && removeIfEmpty) {
                 log("Removing empty directory: " + dir, Project.MSG_DEBUG);
                 dir.delete();
                 ++removedCount;
             }
         }
         return removedCount;
     }
 
 
     //
     // Various copy attributes/subelements of <copy> passed thru to <mycopy>
     //
 
     /**
      * Sets the destination directory.
      * @param destDir the destination directory
      */
     public void setTodir(File destDir) {
         myCopy.setTodir(destDir);
     }
 
     /**
      * Used to force listing of all names of copied files.
      * @param verbose if true force listing of all names of copied files.
      */
     public void setVerbose(boolean verbose) {
         myCopy.setVerbose(verbose);
     }
 
     /**
      * Overwrite any existing destination file(s).
      * @param overwrite if true overwrite any existing destination file(s).
      */
     public void setOverwrite(boolean overwrite) {
         myCopy.setOverwrite(overwrite);
     }
 
     /**
      * Used to copy empty directories.
      * @param includeEmpty If true copy empty directories.
      */
     public void setIncludeEmptyDirs(boolean includeEmpty) {
         myCopy.setIncludeEmptyDirs(includeEmpty);
     }
 
     /**
      * If false, note errors to the output but keep going.
      * @param failonerror true or false
      */
     public void setFailOnError(boolean failonerror) {
         myCopy.setFailOnError(failonerror);
     }
 
     /**
      * Adds a set of files to copy.
      * @param set a fileset
      */
     public void addFileset(FileSet set) {
         myCopy.addFileset(set);
     }
 
     /**
      * The number of milliseconds leeway to give before deciding a
      * target is out of date.
      *
      * <p>Default is 0 milliseconds, or 2 seconds on DOS systems.</p>
      * @param granularity a <code>long</code> value
      * @since Ant 1.6.2
      */
     public void setGranularity(long granularity) {
         myCopy.setGranularity(granularity);
     }
 
     /**
+     * A container for patterns and selectors that can be used to
+     * specify files that should be kept in the target even if they
+     * are not present in any source directory.
+     *
+     * <p>You must not invoke this method more than once.</p>
+     *
+     * @since Ant 1.7
+     */
+    public void addDeleteFromTarget(SyncTarget s) {
+        if (syncTarget != null) {
+            throw new BuildException("you must not specify multiple "
+                                     + "deletefromtaget elements.");
+        }
+        syncTarget = s;
+    }
+
+    /**
      * Subclass Copy in order to access it's file/dir maps.
      */
     public static class MyCopy extends Copy {
 
         // List of files that must be copied, irrelevant from the
         // fact that they are newer or not than the destination.
         private Set nonOrphans = new HashSet();
 
         /** Constructor for MyCopy. */
         public MyCopy() {
         }
 
         /**
          * @see Copy#scan(File, File, String[], String[])
          */
         protected void scan(File fromDir, File toDir, String[] files,
                             String[] dirs) {
             assertTrue("No mapper", mapperElement == null);
 
             super.scan(fromDir, toDir, files, dirs);
 
             for (int i = 0; i < files.length; ++i) {
                 nonOrphans.add(files[i]);
             }
             for (int i = 0; i < dirs.length; ++i) {
                 nonOrphans.add(dirs[i]);
             }
         }
 
         /**
          * Get the destination directory.
          * @return the destination directory
          */
         public File getToDir() {
             return destDir;
         }
 
         /**
          * Get the includeEmptyDirs attribute.
          * @return true if emptyDirs are to be included
          */
         public boolean getIncludeEmptyDirs() {
             return includeEmpty;
         }
 
     }
 
     /**
+     * Inner class used to hold exclude patterns and selectors to save
+     * stuff that happens to live in the target directory but should
+     * not get removed.
+     *
+     * @since Ant 1.7
+     */
+    public static class SyncTarget extends AbstractFileSet {
+
+        public SyncTarget() {
+            super();
+            setDefaultexcludes(false);
+        }
+
+        public void setDir(File dir) throws BuildException {
+            throw new BuildException("synctarget doesn't support the dir "
+                                     + "attribute");
+        }
+
+        private void setTargetDir(File dir) throws BuildException {
+            super.setDir(dir);
+        }
+
+    }
+
+    /**
      * Pseudo-assert method.
      */
     private static void assertTrue(String message, boolean condition) {
         if (!condition) {
             throw new BuildException("Assertion Error: " + message);
         }
     }
 
 }
diff --git a/src/testcases/org/apache/tools/ant/taskdefs/SyncTest.java b/src/testcases/org/apache/tools/ant/taskdefs/SyncTest.java
index 7f6fd80a1..a42ce87d2 100644
--- a/src/testcases/org/apache/tools/ant/taskdefs/SyncTest.java
+++ b/src/testcases/org/apache/tools/ant/taskdefs/SyncTest.java
@@ -1,95 +1,104 @@
 /*
- * Copyright  2004 The Apache Software Foundation
+ * Copyright 2004-2005 The Apache Software Foundation
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
 
 import org.apache.tools.ant.BuildFileTest;
 import java.io.File;
 
 public class SyncTest extends BuildFileTest {
 
     public SyncTest(String name) {
         super(name);
     }
 
     public void setUp() {
         configureProject("src/etc/testcases/taskdefs/sync.xml");
     }
 
     public void tearDown() {
         executeTarget("cleanup");
     }
 
     public void testSimpleCopy() {
         executeTarget("simplecopy");
         String d = getProject().getProperty("dest") + "/a/b/c/d";
         assertFileIsPresent(d);
         assertTrue(getFullLog().indexOf("dangling") == -1);
     }
 
     public void testEmptyCopy() {
         executeTarget("emptycopy");
         String d = getProject().getProperty("dest") + "/a/b/c/d";
         assertFileIsNotPresent(d);
         String c = getProject().getProperty("dest") + "/a/b/c";
         assertFileIsNotPresent(c);
         assertTrue(getFullLog().indexOf("dangling") == -1);
     }
 
     public void testEmptyDirCopy() {
         executeTarget("emptydircopy");
         String d = getProject().getProperty("dest") + "/a/b/c/d";
         assertFileIsNotPresent(d);
         String c = getProject().getProperty("dest") + "/a/b/c";
         assertFileIsPresent(c);
         assertTrue(getFullLog().indexOf("dangling") == -1);
     }
 
     public void testCopyAndRemove() {
         executeTarget("copyandremove");
         String d = getProject().getProperty("dest") + "/a/b/c/d";
         assertFileIsPresent(d);
         String f = getProject().getProperty("dest") + "/e/f";
         assertFileIsNotPresent(f);
         assertTrue(getFullLog().indexOf("Removing orphan file:") > -1);
         assertDebuglogContaining("Removed 1 dangling file from");
         assertDebuglogContaining("Removed 1 dangling directory from");
     }
 
     public void testEmptyDirCopyAndRemove() {
         executeTarget("emptydircopyandremove");
         String d = getProject().getProperty("dest") + "/a/b/c/d";
         assertFileIsNotPresent(d);
         String c = getProject().getProperty("dest") + "/a/b/c";
         assertFileIsPresent(c);
         String f = getProject().getProperty("dest") + "/e/f";
         assertFileIsNotPresent(f);
         assertTrue(getFullLog().indexOf("Removing orphan directory:") > -1);
         assertDebuglogContaining("NO dangling file to remove from");
         assertDebuglogContaining("Removed 2 dangling directories from");
     }
 
+    public void testCopyNoRemove() {
+        executeTarget("copynoremove");
+        String d = getProject().getProperty("dest") + "/a/b/c/d";
+        assertFileIsPresent(d);
+        String f = getProject().getProperty("dest") + "/e/f";
+        assertFileIsPresent(f);
+        assertTrue(getFullLog().indexOf("Removing orphan file:") == -1);
+    }
+
     public void assertFileIsPresent(String f) {
         assertTrue("Expected file " + f,
                    getProject().resolveFile(f).exists());
     }
 
     public void assertFileIsNotPresent(String f) {
         assertTrue("Didn't expect file " + f,
                    !getProject().resolveFile(f).exists());
     }
 }
\ No newline at end of file
