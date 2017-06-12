diff --git a/src/core/org/apache/jmeter/gui/action/Save.java b/src/core/org/apache/jmeter/gui/action/Save.java
index b33f4f072..d16331977 100644
--- a/src/core/org/apache/jmeter/gui/action/Save.java
+++ b/src/core/org/apache/jmeter/gui/action/Save.java
@@ -1,224 +1,444 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileOutputStream;
+import java.io.IOException;
+import java.text.DecimalFormat;
+import java.util.ArrayList;
+import java.util.Calendar;
+import java.util.Collections;
+import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
+import java.util.List;
 import java.util.Set;
+import java.util.regex.Matcher;
+import java.util.regex.Pattern;
 
 import javax.swing.JFileChooser;
 import javax.swing.JOptionPane;
 
+import org.apache.commons.io.FileUtils;
 import org.apache.commons.io.FilenameUtils;
+import org.apache.commons.io.filefilter.FileFilterUtils;
+import org.apache.commons.io.filefilter.IOFileFilter;
 import org.apache.jmeter.control.gui.TestFragmentControllerGui;
 import org.apache.jmeter.engine.TreeCloner;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Save the current test plan; implements:
  * Save
  * Save TestPlan As
  * Save (Selection) As
  */
 public class Save implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
+    private static final List<File> EMPTY_FILE_LIST = Collections.emptyList();
+    
+    private static final String JMX_BACKUP_ON_SAVE = "jmeter.gui.action.save.backup_on_save"; // $NON-NLS-1$
+
+    private static final String JMX_BACKUP_DIRECTORY = "jmeter.gui.action.save.backup_directory"; // $NON-NLS-1$
+    
+    private static final String JMX_BACKUP_MAX_HOURS = "jmeter.gui.action.save.keep_backup_max_hours"; // $NON-NLS-1$
+    
+    private static final String JMX_BACKUP_MAX_COUNT = "jmeter.gui.action.save.keep_backup_max_count"; // $NON-NLS-1$
+    
     public static final String JMX_FILE_EXTENSION = ".jmx"; // $NON-NLS-1$
 
+    private static final String DEFAULT_BACKUP_DIRECTORY = JMeterUtils.getJMeterHome() + "/backups"; //$NON-NLS-1$
+    
+    // Whether we should keep backups for save JMX files. Default is to enable backup
+    private static final boolean BACKUP_ENABLED = JMeterUtils.getPropDefault(JMX_BACKUP_ON_SAVE, true);
+    
+    // Path to the backup directory
+    private static final String BACKUP_DIRECTORY = JMeterUtils.getPropDefault(JMX_BACKUP_DIRECTORY, DEFAULT_BACKUP_DIRECTORY);
+    
+    // Backup files expiration in hours. Default is to never expire (zero value).
+    private static final int BACKUP_MAX_HOURS = JMeterUtils.getPropDefault(JMX_BACKUP_MAX_HOURS, 0);
+    
+    // Max number of backup files. Default is to limit to 10 backups max.
+    private static final int BACKUP_MAX_COUNT = JMeterUtils.getPropDefault(JMX_BACKUP_MAX_COUNT, 10);
+
+    // NumberFormat to format version number in backup file names
+    private static final DecimalFormat BACKUP_VERSION_FORMATER = new DecimalFormat("000000"); //$NON-NLS-1$
+    
     private static final Set<String> commands = new HashSet<String>();
 
     static {
         commands.add(ActionNames.SAVE_AS); // Save (Selection) As
         commands.add(ActionNames.SAVE_AS_TEST_FRAGMENT); // Save as Test Fragment
         commands.add(ActionNames.SAVE_ALL_AS); // Save TestPlan As
         commands.add(ActionNames.SAVE); // Save
     }
 
     /**
      * Constructor for the Save object.
      */
     public Save() {
     }
 
     /**
      * Gets the ActionNames attribute of the Save object.
      *
      * @return the ActionNames value
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         HashTree subTree = null;
         boolean fullSave = false; // are we saving the whole tree?
         if (!commands.contains(e.getActionCommand())) {
             throw new IllegalUserActionException("Invalid user command:" + e.getActionCommand());
         }
         if (e.getActionCommand().equals(ActionNames.SAVE_AS)) {
             JMeterTreeNode[] nodes = GuiPackage.getInstance().getTreeListener().getSelectedNodes();
             if (nodes.length > 1){
                 JMeterUtils.reportErrorToUser(
                         JMeterUtils.getResString("save_as_error"), // $NON-NLS-1$
                         JMeterUtils.getResString("save_as")); // $NON-NLS-1$
                 return;
             }
             subTree = GuiPackage.getInstance().getCurrentSubTree();
         } 
         else if (e.getActionCommand().equals(ActionNames.SAVE_AS_TEST_FRAGMENT)) {
             JMeterTreeNode[] nodes = GuiPackage.getInstance().getTreeListener().getSelectedNodes();
             if(checkAcceptableForTestFragment(nodes)) {                
                 subTree = GuiPackage.getInstance().getCurrentSubTree();
                 // Create Test Fragment node
                 TestElement element = GuiPackage.getInstance().createTestElement(TestFragmentControllerGui.class.getName());
                 HashTree hashTree = new ListedHashTree();
                 HashTree tfTree = hashTree.add(new JMeterTreeNode(element, null));
                 for (int i = 0; i < nodes.length; i++) {
                     // Clone deeply current node
                     TreeCloner cloner = new TreeCloner(false);
                     GuiPackage.getInstance().getTreeModel().getCurrentSubTree(nodes[i]).traverse(cloner);
                     // Add clone to tfTree
                     tfTree.add(cloner.getClonedTree());
                 }
                                 
                 subTree = hashTree;
                 
             } else {
                 JMeterUtils.reportErrorToUser(
                         JMeterUtils.getResString("save_as_test_fragment_error"), // $NON-NLS-1$
                         JMeterUtils.getResString("save_as_test_fragment")); // $NON-NLS-1$
                 return;
             }
         } else {
             fullSave = true;
             HashTree testPlan = GuiPackage.getInstance().getTreeModel().getTestPlan();
             // If saveWorkBench 
             JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot()).getChildAt(1);
             if (((WorkBench)workbenchNode.getUserObject()).getSaveWorkBench()) {
                 HashTree workbench = GuiPackage.getInstance().getTreeModel().getWorkBench();
                 testPlan.add(workbench);
             }
             subTree = testPlan;
         }
 
         String updateFile = GuiPackage.getInstance().getTestPlanFile();
         if (!ActionNames.SAVE.equals(e.getActionCommand()) || updateFile == null) {
             JFileChooser chooser = FileDialoger.promptToSaveFile(updateFile == null ? GuiPackage.getInstance().getTreeListener()
                     .getCurrentNode().getName()
                     + JMX_FILE_EXTENSION : updateFile);
             if (chooser == null) {
                 return;
             }
             updateFile = chooser.getSelectedFile().getAbsolutePath();
             // Make sure the file ends with proper extension
             if(FilenameUtils.getExtension(updateFile).equals("")) {
                 updateFile = updateFile + JMX_FILE_EXTENSION;
             }
             // Check if the user is trying to save to an existing file
             File f = new File(updateFile);
             if(f.exists()) {
                 int response = JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                         JMeterUtils.getResString("save_overwrite_existing_file"), // $NON-NLS-1$
                         JMeterUtils.getResString("save?"),  // $NON-NLS-1$
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.QUESTION_MESSAGE);
                 if (response == JOptionPane.CLOSED_OPTION || response == JOptionPane.NO_OPTION) {
                     return ; // Do not save, user does not want to overwrite
                 }
             }
 
             if (!e.getActionCommand().equals(ActionNames.SAVE_AS)) {
                 GuiPackage.getInstance().setTestPlanFile(updateFile);
             }
         }
-
+        
+        // backup existing file according to jmeter/user.properties settings
+        List<File> expiredBackupFiles = EMPTY_FILE_LIST;
+        File fileToBackup = new File(updateFile);
+        try {
+            expiredBackupFiles = createBackupFile(fileToBackup);
+        } catch (Exception ex) {
+            log.error("Failed to create a backup for " + fileToBackup.getName(), ex); //$NON-NLS-1$
+        }
+        
         try {
             convertSubTree(subTree);
         } catch (Exception err) {
             log.warn("Error converting subtree "+err);
         }
 
         FileOutputStream ostream = null;
         try {
             ostream = new FileOutputStream(updateFile);
             SaveService.saveTree(subTree, ostream);
             if (fullSave) { // Only update the stored copy of the tree for a full save
                 subTree = GuiPackage.getInstance().getTreeModel().getTestPlan(); // refetch, because convertSubTree affects it
                 ActionRouter.getInstance().doActionNow(new ActionEvent(subTree, e.getID(), ActionNames.SUB_TREE_SAVED));
             }
+            
+            // delete expired backups : here everything went right so we can
+            // proceed to deletion
+            for (File expiredBackupFile : expiredBackupFiles) {
+                try {
+                    FileUtils.deleteQuietly(expiredBackupFile);
+                } catch (Exception ex) {
+                    log.warn("Failed to delete backup file " + expiredBackupFile.getName()); //$NON-NLS-1$
+                }
+            }
         } catch (Throwable ex) {
             log.error("Error saving tree:", ex);
             if (ex instanceof Error){
                 throw (Error) ex;
             }
             if (ex instanceof RuntimeException){
                 throw (RuntimeException) ex;
             }
             throw new IllegalUserActionException("Couldn't save test plan to file: " + updateFile, ex);
         } finally {
             JOrphanUtils.closeQuietly(ostream);
         }
         GuiPackage.getInstance().updateCurrentGui();
     }
+    
+    /**
+     * <p>
+     * Create a backup copy of the specified file whose name will be
+     * <code>{baseName}-{version}.jmx</code><br>
+     * Where :<br>
+     * <code>{baseName}</code> is the name of the file to backup without its
+     * <code>.jmx</code> extension. For a file named <code>testplan.jmx</code>
+     * it would then be <code>testplan</code><br>
+     * <code>{version}</code> is the version number automatically incremented
+     * after the higher version number of pre-existing backup files. <br>
+     * <br>
+     * Example: <code>testplan-000028.jmx</code> <br>
+     * <br>
+     * If <code>jmeter.gui.action.save.backup_directory</code> is <b>not</b>
+     * set, then backup files will be created in
+     * <code>${JMETER_HOME}/backups</code>
+     * </p>
+     * <p>
+     * Backup process is controlled by the following jmeter/user properties :<br>
+     * <table border=1>
+     * <tr>
+     * <th align=left>Property</th>
+     * <th align=left>Type/Value</th>
+     * <th align=left>Description</th>
+     * </tr>
+     * <tr>
+     * <td><code>jmeter.gui.action.save.backup_on_save</code></td>
+     * <td><code>true|false</code></td>
+     * <td>Enables / Disables backup</td>
+     * </tr>
+     * <tr>
+     * <td><code>jmeter.gui.action.save.backup_directory</code></td>
+     * <td><code>/path/to/backup/directory</code></td>
+     * <td>Set the directory path where backups will be stored upon save. If not
+     * set then backups will be created in <code>${JMETER_HOME}/backups</code><br>
+     * If that directory does not exist, it will be created</td>
+     * </tr>
+     * <tr>
+     * <td><code>jmeter.gui.action.save.keep_backup_max_hours</code></td>
+     * <td><code>integer</code></td>
+     * <td>Maximum number of hours to preserve backup files. Backup files whose
+     * age exceeds that limit should be deleted and will be added to this method
+     * returned list</td>
+     * </tr>
+     * <tr>
+     * <td><code>jmeter.gui.action.save.keep_backup_max_count</code></td>
+     * <td><code>integer</code></td>
+     * <td>Max number of backup files to be preserved. Exceeding backup files
+     * should be deleted and will be added to this method returned list. Only
+     * the most recent files will be preserved.</td>
+     * </tr>
+     * </table>
+     * </p>
+     * 
+     * @param fileToBackup
+     *            The file to create a backup from
+     * @return A list of expired backup files selected according to the above
+     *         properties and that should be deleted after the save operation
+     *         has performed successfully
+     */
+    private List<File> createBackupFile(File fileToBackup) {
+        if (!BACKUP_ENABLED) {
+            return EMPTY_FILE_LIST;
+        }
+        char versionSeparator = '-'; //$NON-NLS-1$
+        String baseName = fileToBackup.getName();
+        // remove .jmx extension if any
+        baseName = baseName.endsWith(JMX_FILE_EXTENSION) ? baseName.substring(0, baseName.length() - JMX_FILE_EXTENSION.length()) : baseName;
+        // get a file to the backup directory
+        File backupDir = new File(BACKUP_DIRECTORY);
+        backupDir.mkdirs();
+        if (!backupDir.isDirectory()) {
+            log.error("Could not backup file ! Backup directory does not exist, is not a directory or could not be created ! <" + backupDir.getAbsolutePath() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
+        }
+
+        // select files matching
+        // {baseName}{versionSeparator}{version}{jmxExtension}
+        // where {version} is a 6 digits number
+        String backupPatternRegex = Pattern.quote(baseName + versionSeparator) + "([\\d]{6})" + Pattern.quote(JMX_FILE_EXTENSION); //$NON-NLS-1$
+        Pattern backupPattern = Pattern.compile(backupPatternRegex);
+        // create a file filter that select files matching a given regex pattern
+        IOFileFilter patternFileFilter = new PrivatePatternFileFilter(backupPattern);
+        // get all backup files in the backup directory
+        List<File> backupFiles = new ArrayList<File>(FileUtils.listFiles(backupDir, patternFileFilter, null));
+        // find the highest version number among existing backup files (this
+        // should be the more recent backup)
+        int lastVersionNumber = 0;
+        for (File backupFile : backupFiles) {
+            Matcher matcher = backupPattern.matcher(backupFile.getName());
+            if (matcher.find() && matcher.groupCount() > 0) {
+                // parse version number from the backup file name
+                // should never fail as it matches the regex
+                int version = Integer.parseInt(matcher.group(1));
+                lastVersionNumber = Math.max(lastVersionNumber, version);
+            }
+        }
+        // find expired backup files
+        List<File> expiredFiles = new ArrayList<File>();
+        if (BACKUP_MAX_HOURS > 0) {
+            Calendar cal = Calendar.getInstance();
+            cal.add(Calendar.HOUR_OF_DAY, -BACKUP_MAX_HOURS);
+            long expiryDate = cal.getTime().getTime();
+            // select expired files that should be deleted
+            IOFileFilter expiredFileFilter = FileFilterUtils.ageFileFilter(expiryDate, true);
+            expiredFiles.addAll(FileFilterUtils.filterList(expiredFileFilter, backupFiles));
+        }
+        // sort backups from by their last modified time
+        Collections.sort(backupFiles, new Comparator<File>() {
+            @Override
+            public int compare(File o1, File o2) {
+                long diff = o1.lastModified() - o2.lastModified();
+                // convert the long to an int in order to comply with the method
+                // contract
+                return diff < 0 ? -1 : diff > 0 ? 1 : 0;
+            }
+        });
+        // backup name is of the form
+        // {baseName}{versionSeparator}{version}{jmxExtension}
+        String backupName = baseName + versionSeparator + BACKUP_VERSION_FORMATER.format(lastVersionNumber + 1) + JMX_FILE_EXTENSION;
+        File backupFile = new File(backupDir, backupName);
+        // create file backup
+        try {
+            FileUtils.copyFile(fileToBackup, backupFile);
+        } catch (IOException e) {
+            log.error("Failed to backup file :" + fileToBackup.getAbsolutePath(), e); //$NON-NLS-1$
+            return EMPTY_FILE_LIST;
+        }
+        // add the fresh new backup file (list is still sorted here)
+        backupFiles.add(backupFile);
+        // unless max backups is not set, ensure that we don't keep more backups
+        // than required
+        if (BACKUP_MAX_COUNT > 0 && backupFiles.size() > BACKUP_MAX_COUNT) {
+            // keep the most recent files in the limit of the specified max
+            // count
+            expiredFiles.addAll(backupFiles.subList(0, backupFiles.size() - BACKUP_MAX_COUNT));
+        }
+        return expiredFiles;
+    }
 
     /**
      * Check nodes does not contain a node of type TestPlan or ThreadGroup
      * @param nodes
      */
     private static final boolean checkAcceptableForTestFragment(JMeterTreeNode[] nodes) {
         for (int i = 0; i < nodes.length; i++) {
             Object userObject = nodes[i].getUserObject();
             if(userObject instanceof org.apache.jmeter.threads.ThreadGroup ||
                     userObject instanceof TestPlan) {
                 return false;
             }
         }
         return true;
     }
 
     // package protected to allow access from test code
     void convertSubTree(HashTree tree) {
         Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
         while (iter.hasNext()) {
             JMeterTreeNode item = (JMeterTreeNode) iter.next();
             convertSubTree(tree.getTree(item));
             TestElement testElement = item.getTestElement(); // requires JMeterTreeNode
             tree.replaceKey(item, testElement);
         }
     }
+    
+    private static class PrivatePatternFileFilter implements IOFileFilter {
+        
+        private Pattern pattern;
+        
+        public PrivatePatternFileFilter(Pattern pattern) {
+            if(pattern == null) {
+                throw new IllegalArgumentException("pattern cannot be null !"); //$NON-NLS-1$
+            }
+            this.pattern = pattern;
+        }
+        
+        @Override
+        public boolean accept(File dir, String fileName) {
+            return pattern.matcher(fileName).matches();
+        }
+        
+        @Override
+        public boolean accept(File file) {
+            return accept(file.getParentFile(), file.getName());
+        }
+    }
+    
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 98b6ed216..336b06c36 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,239 +1,241 @@
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
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.14 =================== -->
 
 <h1>Version 2.14</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <!-- <ch_category>Improvements</ch_category> -->
 <!-- <ch_title>Sample title</ch_title>
 <p>
 <ul>
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
+<li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to tika-core and tika-parsers 1.8 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
+<li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs =================== -->
  
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show 0 (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(...) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477</bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error: <i>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</i>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <b>jdk.certpath.disabledAlgorithms</b> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <pre>JAVA_HOME/jre/lib/security/java.security</pre>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 </ul>
  
 </section> 
 </body> 
 </document>
diff --git a/xdocs/usermanual/hints_and_tips.xml b/xdocs/usermanual/hints_and_tips.xml
index 8f44e6943..62c4ce47e 100644
--- a/xdocs/usermanual/hints_and_tips.xml
+++ b/xdocs/usermanual/hints_and_tips.xml
@@ -1,115 +1,141 @@
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
 <!DOCTYPE document
 [
 <!ENTITY sect-num '22'>
 ]>
 <document prev="regular_expressions.html" next="glossary.html" id="$Id: $">
 
 <properties>
   <title>User's Manual: Hints and Tips</title>
 </properties>
 
 <body>
 
 <section name="&sect-num;. Hints and Tips" anchor="hints">
 <p>
 This section is a collection of various hints and tips that have been suggested by various questions on the JMeter User list.
 If you don't find what you are looking for here, please check the <a href="http://wiki.apache.org/jmeter">JMeter Wiki</a>.
 Also, try search the JMeter User list; someone may well have already provided a solution.
 </p>
 <subsection name="&sect-num;.1 Passing variables between threads" anchor="variable_and_threads">
 <p>
 JMeter variables have thread scope. This is deliberate, so that threads can act independently.
 However sometimes there is a need to pass variables between different threads, in the same or different Thread Groups.
 </p>
 <p>
 One way to do this is to use a property instead. 
 Properties are shared between all JMeter threads, so if one thread <a href="functions.html#__setProperty">sets a property</a>,
 another thread can <a href="functions.html#__P">read</a> the updated value.
 </p>
 <p>
 If there is a lot of information that needs to be passed between threads, then consider using a file.
 For example you could use the <a href="component_reference.html#Save_Responses_to_a_file">Save Responses to a file</a>
 listener or perhaps a BeanShell PostProcessor in one thread, and read the file using the HTTP Sampler "file:" protocol,
 and extract the information using a PostProcessor or BeanShell element.
 </p>
 <p>
 If you can derive the data before starting the test, then it may well be better to store it in a file,
 read it using CSV Dataset.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.2 Enabling Debug logging" anchor="debug_logging">
 <p>
 Most test elements include debug logging. If running a test plan from the GUI, 
 select the test element and use the Help Menu to enable or disable logging.
 The Help Menu also has an option to display the GUI and test element class names.
 You can use these to determine the correct property setting to change the logging level.
 </p>
 
 <p>
 It is sometimes very useful to see Log messages to debug dynamic scripting languages like BeanShell or
 groovy used in JMeter.
 Since version 2.6, you can view log messages directly in JMeter GUI, to do so:</p>
 <ul>
 <li>use menu Options &gt; Log Viewer, a log console will appear at the bottom of the interface</li>
 <li>Or click on the Warning icon in the upper right corner of GUI</li>
 </ul>
 By default this log console is disabled, you can enable it by changing in <code>jmeter.properties</code>:
 <source>jmeter.loggerpanel.display=true</source>
 
 To avoid using too much memory, this components limits the number of characters used by this panel:
 
 <source>jmeter.loggerpanel.maxlength=80000</source>
 </subsection>
 
 <subsection name="&sect-num;.3 Searching" anchor="searching">
 <p>
 It is sometimes hard to find in a Test Plan tree and elements using a variable or containing a certain URL or parameter.
 A new feature is now available since 2.6, you can access it in Menu Search.
 It provides search with following options:
 </p>
 <dl>
 <dt><code>Case sensitive</code></dt><dd>Makes search case sensitive</dd>
 <dt><code>Regular exp.</code></dt><dd>Is text to search a regexp, if so Regexp will be searched in Tree of components, example "<code>\btest\b</code>"
 will match any component that contains test in searchable elements of the component</dd>
 </dl>
 
 <figure width="663" height="300" image="searching/raw-search.png">Figure 1 - Search raw text in TreeView</figure>
 <figure width="667" height="319" image="searching/raw-search-result.png">Figure 2 - Result in TreeView</figure>
 <figure width="642" height="307" image="searching/regexp-search.png">Figure 3 - Search Regexp in TreeView (in this example we search whole word)</figure>
 <figure width="596" height="328" image="searching/regexp-search-result.png">Figure 4 - Result in TreeView</figure>
 
 </subsection>
 
 <subsection name="&sect-num;.4 Toolbar icons size" anchor="toolbar">
 <description>
     <p>
 You can change the size of icons in the toolbar using the property <source>jmeter.toolbar.icons.size</source> with these
 values: <code>22x22</code> (default size), <code>32x32</code> or <code>48x48</code>.
     </p>
 </description>
 <figure width="296" height="95" image="icons-22x22.jpg">Icons with the size <code>22x22</code>.</figure>
 <figure width="300" height="106" image="icons-32x32.jpg">Icons with the size <code>32x32</code>.</figure>
 <figure width="365" height="120" image="icons-48x48.jpg">Icons with the size <code>48x48</code>.</figure>
 </subsection>
+
+<subsection name="&sect-num;.5 Autosave process configuration" anchor="autosave">
+<description>
+    <p>Since JMeter 2.14, JMeter automatically saves up to 10 backups of every saved jmx files. When enabled, just before the .jmx is saved,
+    it will be backed up to the ${JMETER_HOME}/backups subfolder. Backup files are named after the saved jmx file and assigned a
+    version number that is automatically incremented, ex: test-plan-000001.jmx, test-plan-000002.jmx, test-plan-000003.jmx, etc.
+    To control auto-backup, add the following properties to user.properties.
+    To enable/disable auto-backup, set the following property to true/false (default is true): 
+    <source>jmeter.gui.action.save.backup_on_save=false</source>
+    The backup directory can also be set to a different location. Setting the following property to the path of the desired directory
+    will cause backup files to be stored inside instead of the ${JMETER_HOME}/backups folder. If the specified directory does not exist
+    it will be created. Leaving this property unset will cause the ${JMETER_HOME}/backups folder to be used.
+    <source>jmeter.gui.action.save.backup_directory=/path/to/backups/dir</source>
+    You can also configure the maximum time (in hours) that backup files should be preserved since the most recent save time.
+    By default a zero expiration time is set which instructs JMeter to preserve backup files for ever.
+    Use the following property to control max preservation time :
+    <source>jmeter.gui.action.save.keep_backup_max_hours=0</source>
+    You can set the maximum number of backup files that should be preserved. By default 10 backups will be kept.
+    Setting this to zero will cause the backups to never being deleted (unless keep_backup_max_hours is set to a non nul value)
+    Maximum backup files selection is processed _after_ time expiration selection, so even if you set 1 year as the expiry time, only the max_count
+    most recent backups files will be kept.
+    <source>jmeter.gui.action.save.keep_backup_max_count=10</source>
+    </p>
+</description>
+</subsection>
+
 </section>
 
 </body>
 </document>
