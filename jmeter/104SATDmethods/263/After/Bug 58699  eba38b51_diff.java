diff --git a/src/core/org/apache/jmeter/gui/action/CheckDirty.java b/src/core/org/apache/jmeter/gui/action/CheckDirty.java
index c7b314882..93bab22a8 100644
--- a/src/core/org/apache/jmeter/gui/action/CheckDirty.java
+++ b/src/core/org/apache/jmeter/gui/action/CheckDirty.java
@@ -1,170 +1,191 @@
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
 import java.awt.event.ActionListener;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
+import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Check if the TestPlan has been changed since it was last saved
  *
  */
 public class CheckDirty extends AbstractAction implements HashTreeTraverser, ActionListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final Map<JMeterTreeNode, TestElement> previousGuiItems;
 
     private boolean checkMode = false;
 
     private boolean removeMode = false;
 
     private boolean dirty = false;
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.CHECK_DIRTY);
         commands.add(ActionNames.SUB_TREE_SAVED);
         commands.add(ActionNames.SUB_TREE_MERGED);
         commands.add(ActionNames.SUB_TREE_LOADED);
         commands.add(ActionNames.ADD_ALL);
         commands.add(ActionNames.CHECK_REMOVE);
     }
 
     public CheckDirty() {
         previousGuiItems = new HashMap<>();
         ActionRouter.getInstance().addPreActionListener(ExitCommand.class, this);
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals(ActionNames.EXIT)) {
             doAction(e);
         }
     }
 
     /**
      * @see Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         String action = e.getActionCommand();
         if (action.equals(ActionNames.SUB_TREE_SAVED)) {
             HashTree subTree = (HashTree) e.getSource();
             subTree.traverse(this);
         } else if (action.equals(ActionNames.SUB_TREE_LOADED)) {
             ListedHashTree addTree = (ListedHashTree) e.getSource();
             addTree.traverse(this);
         } else if (action.equals(ActionNames.ADD_ALL)) {
             previousGuiItems.clear();
             GuiPackage.getInstance().getTreeModel().getTestPlan().traverse(this);
+            if (isWorkbenchSaveable()) {
+                GuiPackage.getInstance().getTreeModel().getWorkBench().traverse(this);
+            }
         } else if (action.equals(ActionNames.CHECK_REMOVE)) {
             GuiPackage guiPackage = GuiPackage.getInstance();
             JMeterTreeNode[] nodes = guiPackage.getTreeListener().getSelectedNodes();
             removeMode = true;
             try {
                 for (int i = nodes.length - 1; i >= 0; i--) {
                     guiPackage.getTreeModel().getCurrentSubTree(nodes[i]).traverse(this);
                 }
             } finally {
                 removeMode = false;
             }
         }
+        
         // If we are merging in another test plan, we know the test plan is dirty now
         if(action.equals(ActionNames.SUB_TREE_MERGED)) {
             dirty = true;
         }
         else {
             dirty = false;
             checkMode = true;
             try {
                 HashTree wholeTree = GuiPackage.getInstance().getTreeModel().getTestPlan();
                 wholeTree.traverse(this);
+                
+                // check the workbench for modification
+                if(!dirty) {
+                    if (isWorkbenchSaveable()) {
+                        HashTree workbench = GuiPackage.getInstance().getTreeModel().getWorkBench();
+                        workbench.traverse(this);
+                    }
+                }
             } finally {
                 checkMode = false;
             }
         }
         GuiPackage.getInstance().setDirty(dirty);
     }
 
     /**
+     * check if the workbench should be saved
+     */
+    private boolean isWorkbenchSaveable() {
+        JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot()).getChildAt(1);
+        return ((WorkBench) workbenchNode.getUserObject()).getSaveWorkBench();
+    }
+
+    /**
      * The tree traverses itself depth-first, calling addNode for each
      * object it encounters as it goes.
      */
     @Override
     public void addNode(Object node, HashTree subTree) {
         log.debug("Node is class:" + node.getClass());
         JMeterTreeNode treeNode = (JMeterTreeNode) node;
         if (checkMode) {
             // Only check if we have not found any differences so far
             if(!dirty) {
                 if (previousGuiItems.containsKey(treeNode)) {
                     if (!previousGuiItems.get(treeNode).equals(treeNode.getTestElement())) {
                         dirty = true;
                     }
                 } else {
                     dirty = true;
                 }
             }
         } else if (removeMode) {
             previousGuiItems.remove(treeNode);
         } else {
             previousGuiItems.put(treeNode, (TestElement) treeNode.getTestElement().clone());
         }
     }
 
     /**
      * Indicates traversal has moved up a step, and the visitor should remove
      * the top node from it's stack structure.
      */
     @Override
     public void subtractNode() {
     }
 
     /**
      * Process path is called when a leaf is reached. If a visitor wishes to
      * generate Lists of path elements to each leaf, it should keep a Stack data
      * structure of nodes passed to it with addNode, and removing top items for
      * every subtractNode() call.
      */
     @Override
     public void processPath() {
     }
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Save.java b/src/core/org/apache/jmeter/gui/action/Save.java
index 6ab6be178..a8b6deef9 100644
--- a/src/core/org/apache/jmeter/gui/action/Save.java
+++ b/src/core/org/apache/jmeter/gui/action/Save.java
@@ -1,444 +1,454 @@
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
 import java.io.IOException;
 import java.text.DecimalFormat;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import javax.swing.JFileChooser;
 import javax.swing.JOptionPane;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.io.FilenameUtils;
 import org.apache.commons.io.filefilter.FileFilterUtils;
 import org.apache.commons.io.filefilter.IOFileFilter;
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
 
     private static final List<File> EMPTY_FILE_LIST = Collections.emptyList();
     
     private static final String JMX_BACKUP_ON_SAVE = "jmeter.gui.action.save.backup_on_save"; // $NON-NLS-1$
 
     private static final String JMX_BACKUP_DIRECTORY = "jmeter.gui.action.save.backup_directory"; // $NON-NLS-1$
     
     private static final String JMX_BACKUP_MAX_HOURS = "jmeter.gui.action.save.keep_backup_max_hours"; // $NON-NLS-1$
     
     private static final String JMX_BACKUP_MAX_COUNT = "jmeter.gui.action.save.keep_backup_max_count"; // $NON-NLS-1$
     
     public static final String JMX_FILE_EXTENSION = ".jmx"; // $NON-NLS-1$
 
     private static final String DEFAULT_BACKUP_DIRECTORY = JMeterUtils.getJMeterHome() + "/backups"; //$NON-NLS-1$
     
     // Whether we should keep backups for save JMX files. Default is to enable backup
     private static final boolean BACKUP_ENABLED = JMeterUtils.getPropDefault(JMX_BACKUP_ON_SAVE, true);
     
     // Path to the backup directory
     private static final String BACKUP_DIRECTORY = JMeterUtils.getPropDefault(JMX_BACKUP_DIRECTORY, DEFAULT_BACKUP_DIRECTORY);
     
     // Backup files expiration in hours. Default is to never expire (zero value).
     private static final int BACKUP_MAX_HOURS = JMeterUtils.getPropDefault(JMX_BACKUP_MAX_HOURS, 0);
     
     // Max number of backup files. Default is to limit to 10 backups max.
     private static final int BACKUP_MAX_COUNT = JMeterUtils.getPropDefault(JMX_BACKUP_MAX_COUNT, 10);
 
     // NumberFormat to format version number in backup file names
     private static final DecimalFormat BACKUP_VERSION_FORMATER = new DecimalFormat("000000"); //$NON-NLS-1$
     
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.SAVE_AS); // Save (Selection) As
         commands.add(ActionNames.SAVE_AS_TEST_FRAGMENT); // Save as Test Fragment
         commands.add(ActionNames.SAVE_ALL_AS); // Save TestPlan As
         commands.add(ActionNames.SAVE); // Save
     }
 
     /**
      * Constructor for the Save object.
      */
-    public Save() {
-    }
+    public Save() {}
 
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
-            JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot()).getChildAt(1);
-            if (((WorkBench)workbenchNode.getUserObject()).getSaveWorkBench()) {
+            if (isWorkbenchSaveable()) {
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
         
         // backup existing file according to jmeter/user.properties settings
         List<File> expiredBackupFiles = EMPTY_FILE_LIST;
         File fileToBackup = new File(updateFile);
         try {
             expiredBackupFiles = createBackupFile(fileToBackup);
         } catch (Exception ex) {
             log.error("Failed to create a backup for " + fileToBackup.getName(), ex); //$NON-NLS-1$
         }
         
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
+                if (isWorkbenchSaveable()) {
+                    HashTree workbench = GuiPackage.getInstance().getTreeModel().getWorkBench();
+                    subTree.add(workbench);
+                }
                 ActionRouter.getInstance().doActionNow(new ActionEvent(subTree, e.getID(), ActionNames.SUB_TREE_SAVED));
             }
             
             // delete expired backups : here everything went right so we can
             // proceed to deletion
             for (File expiredBackupFile : expiredBackupFiles) {
                 try {
                     FileUtils.deleteQuietly(expiredBackupFile);
                 } catch (Exception ex) {
                     log.warn("Failed to delete backup file " + expiredBackupFile.getName()); //$NON-NLS-1$
                 }
             }
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
     
     /**
      * <p>
      * Create a backup copy of the specified file whose name will be
      * <code>{baseName}-{version}.jmx</code><br>
      * Where :<br>
      * <code>{baseName}</code> is the name of the file to backup without its
      * <code>.jmx</code> extension. For a file named <code>testplan.jmx</code>
      * it would then be <code>testplan</code><br>
      * <code>{version}</code> is the version number automatically incremented
      * after the higher version number of pre-existing backup files. <br>
      * <br>
      * Example: <code>testplan-000028.jmx</code> <br>
      * <br>
      * If <code>jmeter.gui.action.save.backup_directory</code> is <b>not</b>
      * set, then backup files will be created in
      * <code>${JMETER_HOME}/backups</code>
      * </p>
      * <p>
      * Backup process is controlled by the following jmeter/user properties :<br>
      * <table border=1>
      * <tr>
      * <th align=left>Property</th>
      * <th align=left>Type/Value</th>
      * <th align=left>Description</th>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.backup_on_save</code></td>
      * <td><code>true|false</code></td>
      * <td>Enables / Disables backup</td>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.backup_directory</code></td>
      * <td><code>/path/to/backup/directory</code></td>
      * <td>Set the directory path where backups will be stored upon save. If not
      * set then backups will be created in <code>${JMETER_HOME}/backups</code><br>
      * If that directory does not exist, it will be created</td>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.keep_backup_max_hours</code></td>
      * <td><code>integer</code></td>
      * <td>Maximum number of hours to preserve backup files. Backup files whose
      * age exceeds that limit should be deleted and will be added to this method
      * returned list</td>
      * </tr>
      * <tr>
      * <td><code>jmeter.gui.action.save.keep_backup_max_count</code></td>
      * <td><code>integer</code></td>
      * <td>Max number of backup files to be preserved. Exceeding backup files
      * should be deleted and will be added to this method returned list. Only
      * the most recent files will be preserved.</td>
      * </tr>
      * </table>
      * </p>
      * 
      * @param fileToBackup
      *            The file to create a backup from
      * @return A list of expired backup files selected according to the above
      *         properties and that should be deleted after the save operation
      *         has performed successfully
      */
     private List<File> createBackupFile(File fileToBackup) {
         if (!BACKUP_ENABLED || !fileToBackup.exists()) {
             return EMPTY_FILE_LIST;
         }
         char versionSeparator = '-'; //$NON-NLS-1$
         String baseName = fileToBackup.getName();
         // remove .jmx extension if any
         baseName = baseName.endsWith(JMX_FILE_EXTENSION) ? baseName.substring(0, baseName.length() - JMX_FILE_EXTENSION.length()) : baseName;
         // get a file to the backup directory
         File backupDir = new File(BACKUP_DIRECTORY);
         backupDir.mkdirs();
         if (!backupDir.isDirectory()) {
             log.error("Could not backup file ! Backup directory does not exist, is not a directory or could not be created ! <" + backupDir.getAbsolutePath() + ">"); //$NON-NLS-1$ //$NON-NLS-2$
         }
 
         // select files matching
         // {baseName}{versionSeparator}{version}{jmxExtension}
         // where {version} is a 6 digits number
         String backupPatternRegex = Pattern.quote(baseName + versionSeparator) + "([\\d]{6})" + Pattern.quote(JMX_FILE_EXTENSION); //$NON-NLS-1$
         Pattern backupPattern = Pattern.compile(backupPatternRegex);
         // create a file filter that select files matching a given regex pattern
         IOFileFilter patternFileFilter = new PrivatePatternFileFilter(backupPattern);
         // get all backup files in the backup directory
         List<File> backupFiles = new ArrayList<>(FileUtils.listFiles(backupDir, patternFileFilter, null));
         // find the highest version number among existing backup files (this
         // should be the more recent backup)
         int lastVersionNumber = 0;
         for (File backupFile : backupFiles) {
             Matcher matcher = backupPattern.matcher(backupFile.getName());
             if (matcher.find() && matcher.groupCount() > 0) {
                 // parse version number from the backup file name
                 // should never fail as it matches the regex
                 int version = Integer.parseInt(matcher.group(1));
                 lastVersionNumber = Math.max(lastVersionNumber, version);
             }
         }
         // find expired backup files
         List<File> expiredFiles = new ArrayList<>();
         if (BACKUP_MAX_HOURS > 0) {
             Calendar cal = Calendar.getInstance();
             cal.add(Calendar.HOUR_OF_DAY, -BACKUP_MAX_HOURS);
             long expiryDate = cal.getTime().getTime();
             // select expired files that should be deleted
             IOFileFilter expiredFileFilter = FileFilterUtils.ageFileFilter(expiryDate, true);
             expiredFiles.addAll(FileFilterUtils.filterList(expiredFileFilter, backupFiles));
         }
         // sort backups from by their last modified time
         Collections.sort(backupFiles, new Comparator<File>() {
             @Override
             public int compare(File o1, File o2) {
                 long diff = o1.lastModified() - o2.lastModified();
                 // convert the long to an int in order to comply with the method
                 // contract
                 return diff < 0 ? -1 : diff > 0 ? 1 : 0;
             }
         });
         // backup name is of the form
         // {baseName}{versionSeparator}{version}{jmxExtension}
         String backupName = baseName + versionSeparator + BACKUP_VERSION_FORMATER.format(lastVersionNumber + 1) + JMX_FILE_EXTENSION;
         File backupFile = new File(backupDir, backupName);
         // create file backup
         try {
             FileUtils.copyFile(fileToBackup, backupFile);
         } catch (IOException e) {
             log.error("Failed to backup file :" + fileToBackup.getAbsolutePath(), e); //$NON-NLS-1$
             return EMPTY_FILE_LIST;
         }
         // add the fresh new backup file (list is still sorted here)
         backupFiles.add(backupFile);
         // unless max backups is not set, ensure that we don't keep more backups
         // than required
         if (BACKUP_MAX_COUNT > 0 && backupFiles.size() > BACKUP_MAX_COUNT) {
             // keep the most recent files in the limit of the specified max
             // count
             expiredFiles.addAll(backupFiles.subList(0, backupFiles.size() - BACKUP_MAX_COUNT));
         }
         return expiredFiles;
     }
+    
+    /**
+     * check if the workbench should be saved
+     */
+    private boolean isWorkbenchSaveable() {
+        JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot()).getChildAt(1);
+        return ((WorkBench) workbenchNode.getUserObject()).getSaveWorkBench();
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
         Iterator<Object> iter = new LinkedList<>(tree.list()).iterator();
         while (iter.hasNext()) {
             JMeterTreeNode item = (JMeterTreeNode) iter.next();
             convertSubTree(tree.getTree(item));
             TestElement testElement = item.getTestElement(); // requires JMeterTreeNode
             tree.replaceKey(item, testElement);
         }
     }
     
     private static class PrivatePatternFileFilter implements IOFileFilter {
         
         private Pattern pattern;
         
         public PrivatePatternFileFilter(Pattern pattern) {
             if(pattern == null) {
                 throw new IllegalArgumentException("pattern cannot be null !"); //$NON-NLS-1$
             }
             this.pattern = pattern;
         }
         
         @Override
         public boolean accept(File dir, String fileName) {
             return pattern.matcher(fileName).matches();
         }
         
         @Override
         public boolean accept(File file) {
             return accept(file.getParentFile(), file.getName());
         }
     }
     
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
index d340313eb..7e80cc240 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
@@ -1,281 +1,275 @@
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
 
 package org.apache.jmeter.gui.tree;
 
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.tree.DefaultTreeModel;
 
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.gui.TestPlanGui;
 import org.apache.jmeter.control.gui.WorkBenchGui;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
 
 public class JMeterTreeModel extends DefaultTreeModel {
 
     private static final long serialVersionUID = 240L;
 
     public JMeterTreeModel(TestElement tp, TestElement wb) {
         super(new JMeterTreeNode(wb, null));
         initTree(tp,wb);
     }
 
     public JMeterTreeModel() {
         this(new TestPlanGui().createTestElement(),new WorkBenchGui().createTestElement());
-//        super(new JMeterTreeNode(new WorkBenchGui().createTestElement(), null));
-//        TestElement tp = new TestPlanGui().createTestElement();
-//        initTree(tp);
     }
 
     /**
      * Hack to allow TreeModel to be used in non-GUI and headless mode.
      *
      * @deprecated - only for use by JMeter class!
      * @param o - dummy
      */
     @Deprecated
     public JMeterTreeModel(Object o) {
         this(new TestPlan(),new WorkBench());
-//      super(new JMeterTreeNode(new WorkBench(), null));
-//      TestElement tp = new TestPlan();
-//      initTree(tp, new WorkBench());
     }
 
     /**
      * Returns a list of tree nodes that hold objects of the given class type.
      * If none are found, an empty list is returned.
      * @param type The type of nodes, which are to be collected
      * @return a list of tree nodes of the given <code>type</code>, or an empty list
      */
     public List<JMeterTreeNode> getNodesOfType(Class<?> type) {
         List<JMeterTreeNode> nodeList = new LinkedList<>();
         traverseAndFind(type, (JMeterTreeNode) this.getRoot(), nodeList);
         return nodeList;
     }
 
     /**
      * Get the node for a given TestElement object.
      * @param userObject The object to be found in this tree
      * @return the node corresponding to the <code>userObject</code>
      */
     public JMeterTreeNode getNodeOf(TestElement userObject) {
         return traverseAndFind(userObject, (JMeterTreeNode) getRoot());
     }
 
     /**
      * Adds the sub tree at the given node. Returns a boolean indicating whether
      * the added sub tree was a full test plan.
      * 
      * @param subTree
      *            The {@link HashTree} which is to be inserted into
      *            <code>current</code>
      * @param current
      *            The node in which the <code>subTree</code> is to be inserted.
      *            Will be overridden, when an instance of {@link TestPlan} or
      *            {@link WorkBench} is found in the subtree.
      * @return newly created sub tree now found at <code>current</code>
      * @throws IllegalUserActionException
      *             when <code>current</code> is not an instance of
      *             {@link AbstractConfigGui} and no instance of {@link TestPlan}
      *             or {@link WorkBench} could be found in the
      *             <code>subTree</code>
      */
     public HashTree addSubTree(HashTree subTree, JMeterTreeNode current) throws IllegalUserActionException {
         Iterator<Object> iter = subTree.list().iterator();
         while (iter.hasNext()) {
             TestElement item = (TestElement) iter.next();
             if (item instanceof TestPlan) {
                 TestPlan tp = (TestPlan) item;
                 current = (JMeterTreeNode) ((JMeterTreeNode) getRoot()).getChildAt(0);
                 final TestPlan userObject = (TestPlan) current.getUserObject();
                 userObject.addTestElement(item);
                 userObject.setName(item.getName());
                 userObject.setFunctionalMode(tp.isFunctionalMode());
                 userObject.setSerialized(tp.isSerialized());
                 addSubTree(subTree.getTree(item), current);
             } else if (item instanceof WorkBench) {
                 current = (JMeterTreeNode) ((JMeterTreeNode) getRoot()).getChildAt(1);
                 final TestElement testElement = ((TestElement) current.getUserObject());
                 testElement.addTestElement(item);
                 testElement.setName(item.getName());
                 addSubTree(subTree.getTree(item), current);
             } else {
                 addSubTree(subTree.getTree(item), addComponent(item, current));
             }
         }
         return getCurrentSubTree(current);
     }
 
     /**
      * Add a {@link TestElement} to a {@link JMeterTreeNode}
      * @param component The {@link TestElement} to be used as data for the newly created note
      * @param node The {@link JMeterTreeNode} into which the newly created node is to be inserted
      * @return new {@link JMeterTreeNode} for the given <code>component</code>
      * @throws IllegalUserActionException
      *             when the user object for the <code>node</code> is not an instance
      *             of {@link AbstractConfigGui}
      */
     public JMeterTreeNode addComponent(TestElement component, JMeterTreeNode node) throws IllegalUserActionException {
         if (node.getUserObject() instanceof AbstractConfigGui) {
             throw new IllegalUserActionException("This node cannot hold sub-elements");
         }
 
         GuiPackage guiPackage = GuiPackage.getInstance();
         if (guiPackage != null) {
             // The node can be added in non GUI mode at startup
             guiPackage.updateCurrentNode();
             JMeterGUIComponent guicomp = guiPackage.getGui(component);
             guicomp.configure(component);
             guicomp.modifyTestElement(component);
             guiPackage.getCurrentGui(); // put the gui object back
                                         // to the way it was.
         }
         JMeterTreeNode newNode = new JMeterTreeNode(component, this);
 
         // This check the state of the TestElement and if returns false it
         // disable the loaded node
         try {
             newNode.setEnabled(component.isEnabled());
         } catch (Exception e) { // TODO - can this ever happen?
             newNode.setEnabled(true);
         }
 
         this.insertNodeInto(newNode, node, node.getChildCount());
         return newNode;
     }
 
     public void removeNodeFromParent(JMeterTreeNode node) {
         if (!(node.getUserObject() instanceof TestPlan) && !(node.getUserObject() instanceof WorkBench)) {
             super.removeNodeFromParent(node);
         }
     }
 
     private void traverseAndFind(Class<?> type, JMeterTreeNode node, List<JMeterTreeNode> nodeList) {
         if (type.isInstance(node.getUserObject())) {
             nodeList.add(node);
         }
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             traverseAndFind(type, child, nodeList);
         }
     }
 
     private JMeterTreeNode traverseAndFind(TestElement userObject, JMeterTreeNode node) {
         if (userObject == node.getUserObject()) {
             return node;
         }
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             JMeterTreeNode result = traverseAndFind(userObject, child);
             if (result != null) {
                 return result;
             }
         }
         return null;
     }
 
     /**
      * Get the current sub tree for a {@link JMeterTreeNode}
      * @param node The {@link JMeterTreeNode} from which the sub tree is to be taken 
      * @return newly copied sub tree
      */
     public HashTree getCurrentSubTree(JMeterTreeNode node) {
         ListedHashTree hashTree = new ListedHashTree(node);
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             hashTree.add(node, getCurrentSubTree(child));
         }
         return hashTree;
     }
 
     /**
      * Get the {@link TestPlan} from the root of this tree
      * @return The {@link TestPlan} found at the root of this tree
      */
     public HashTree getTestPlan() {
         return getCurrentSubTree((JMeterTreeNode) ((JMeterTreeNode) this.getRoot()).getChildAt(0));
     }
 
     /**
      * Get the {@link WorkBench} from the root of this tree
      * @return The {@link WorkBench} found at the root of this tree
      */
     public HashTree getWorkBench() {
         return getCurrentSubTree((JMeterTreeNode) ((JMeterTreeNode) this.getRoot()).getChildAt(1));
     }
 
     /**
      * Clear the test plan, and use default node for test plan and workbench.
      *
      * N.B. Should only be called by {@link GuiPackage#clearTestPlan()}
      */
     public void clearTestPlan() {
         TestElement tp = new TestPlanGui().createTestElement();
         clearTestPlan(tp);
     }
 
     /**
      * Clear the test plan, and use specified node for test plan and default node for workbench
      *
      * N.B. Should only be called by {@link GuiPackage#clearTestPlan(TestElement)}
      *
      * @param testPlan the node to use as the testplan top node
      */
     public void clearTestPlan(TestElement testPlan) {
         // Remove the workbench and testplan nodes
         int children = getChildCount(getRoot());
         while (children > 0) {
             JMeterTreeNode child = (JMeterTreeNode)getChild(getRoot(), 0);
             super.removeNodeFromParent(child);
             children = getChildCount(getRoot());
         }
         // Init the tree
         initTree(testPlan,new WorkBenchGui().createTestElement()); // Assumes this is only called from GUI mode
     }
 
     /**
      * Initialize the model with nodes for testplan and workbench.
      *
      * @param tp the element to use as testplan
      * @param wb the element to use as workbench
      */
     private void initTree(TestElement tp, TestElement wb) {
         // Insert the test plan node
         insertNodeInto(new JMeterTreeNode(tp, this), (JMeterTreeNode) getRoot(), 0);
         // Insert the workbench node
         insertNodeInto(new JMeterTreeNode(wb, this), (JMeterTreeNode) getRoot(), 1);
         // Let others know that the tree content has changed.
         // This should not be necessary, but without it, nodes are not shown when the user
         // uses the Close menu item
         nodeStructureChanged((JMeterTreeNode)getRoot());
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index e03d477b1..5ff00aa88 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,320 +1,321 @@
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
 <!ENTITY hellip   "&#x02026;" >
 ]>
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
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
     <li>Since version 2.14, you can use Nashorn Engine (default javascript engine is Rhino) under Java8 for Elements that use Javascript Engine (__javaScript, IfController). If you want to use it, use property <code>javascript.use_rhino=false</code>, see <bugzilla>58406</bugzilla>.
     Note in future versions, we will switch to Nashorn by default, so users are encouraged to report any issue related to broken code when using Nashorn instead of Rhino.
     </li>
     <li>Since version 2.14, JMS Publisher will reload contents of file if Message source is "From File" and the ""Filename" field changes (through variables usage for example)</li>
     <li>org.apache.jmeter.gui.util.ButtonPanel has been removed, if you use it in your 3rd party plugin or custom development ensure you update your code. see <bugzill>58687</bugzill></li>   
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
     <li><bug>58300</bug> Make existing Java Samplers implement Interruptible</li>
     <li><bug>58160</bug>JMS Publisher : reload file content if file name changes. Based partly on a patch contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58406</bug>IfController : Allow use of Nashorn Engine if available for JavaScript evaluation</li>
     <li><bug>58281</bug>RandomOrderController : Improve randomization algorithm performance. Contributed by Graham Russell (jmeter at ham1.co.uk)</li> 
     <li><bug>58675</bug>Module controller : error message can easily be missed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58673</bug>Module controller : when the target element is disabled the default jtree icons are displayed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58674</bug>Module controller : it should not be possible to select more than one node in the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58680</bug>Module Controller : ui enhancement. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
 <li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li><bug>58681</bug>GraphiteBackendListener : Don't send data if no sampling occured</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
   <li><bug>58303</bug>Change usage of bouncycastle api in SMIMEAssertion to get rid of deprecation warnings.</li>
   <li><bug>58515</bug>New JSON related components : JSON-PATH Extractor and JSON-PATH Renderer in View Results Tree. Donated by Ubik Load Pack (support at ubikloadpack.com).</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>58477</bug> __javaScript function : Allow use of Nashorn engine for Java8 and later versions</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (<keycombo><keysym>Ctrl</keysym><keysym>1</keysym></keycombo> &hellip;
     <keycombo><keysym>Ctrl</keysym><keysym>9</keysym></keycombo>) to quick add elements into test plan.
     Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 <li><bug>58465</bug>JMS Read response field is badly named and documented</li>
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>$Revision$</code> to sha1 sum of the file itself.</li>
 <li><bug>58677</bug>TestSaveService#testLoadAndSave use the wrong set of files. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58679</bug>Replace the xpp pull parser in xstream with a java6+ standard solution. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58689</bug>Add shortcuts to expand / collapse a part of the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58696</bug>Create Ant task to setup Eclipse project</li>
 <li><bug>58653</bug>New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results. Developed by Ubik-Ingenierie and contributed by Decathlon S.A. and Ubik-Ingenierie/UbikLoadPack</li>
+<li><bug>58699</bug>Workbench changes neither saved nor prompted for saving upon close. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to tika-core and tika-parsers 1.11 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7 (from 1.7R5)</li>
 <li>Updated to jodd-3.6.6.jar (from 3.6.4)</li>
 <li>Updated to jsoup-1.8.2 (from 1.8.1)</li>
 <li>Updated to rsyntaxtextarea-2.5.7 (from 2.5.6)</li>
 <li>Updated to slf4j-1.7.12 (from 1.7.10)</li>
 <li>Updated to xmlgraphics-commons-2.0.1 (from 1.5)</li>
 <li><bug>57981</bug>Require a minimum of Java 7. Partly contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li><bug>58684</bug>JMeterColor does not need to extend java.awt.Color. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58687</bug>ButtonPanel should die. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com).</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
     <li><bug>58137</bug>JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them).</li>
     <li><bug>58201</bug>Make usage of port in the host header more consistent across the different http samplers.</li>
     <li><bug>58453</bug>HTTP Test Script Recorder : NullPointerException when disabling Capture HTTP Headers </li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug> SampleResultConverter should note that it cannot record non-TEXT data</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>58079</bug>Do not cache HTTP samples that have a Vary header when using a HTTP CacheManager.</li>
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
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13 (Fixed group ids for Commons Pool and Math)</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
     <li><bug>58293</bug>SOAP/XML-RPC Sampler file browser generates NullPointerException</li>
     <li><bug>58685</bug>JDatefield : Make the modification of the date with up/down arrow work.Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58693</bug>Fix "Cannot nest output folder 'jmeter/build/components' inside output folder 'jmeter/build' when setting up eclipse</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
 <li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li>Samoht-fr (https://github.com/Samoht-fr)</li>
 <li>Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><a href="http://www.decathlon.com">Decathlon S.A.</a></li>
 <li><a href="http://www.ubik-ingenierie.com">Ubik-Ingenierie S.A.S.</a></li>
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
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
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
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
