diff --git a/src/core/org/apache/jmeter/gui/action/AbstractActionWithNoRunningTest.java b/src/core/org/apache/jmeter/gui/action/AbstractActionWithNoRunningTest.java
new file mode 100644
index 000000000..c60a8c987
--- /dev/null
+++ b/src/core/org/apache/jmeter/gui/action/AbstractActionWithNoRunningTest.java
@@ -0,0 +1,54 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+
+package org.apache.jmeter.gui.action;
+
+import java.awt.event.ActionEvent;
+
+import javax.swing.JOptionPane;
+
+import org.apache.jmeter.exceptions.IllegalUserActionException;
+import org.apache.jmeter.gui.GuiPackage;
+import org.apache.jmeter.util.JMeterUtils;
+
+/**
+ * {@link AbstractAction} implementation that check no test is running 
+ * before calling {@link AbstractActionWithNoRunningTest#doActionAfterCheck(ActionEvent)}
+ * @since 3.1
+ */
+public abstract class AbstractActionWithNoRunningTest extends AbstractAction {
+
+    @Override
+    public final void doAction(ActionEvent e) throws IllegalUserActionException {
+        if (JMeterUtils.isTestRunning()) {
+            JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),
+                    JMeterUtils.getResString("action_check_message"),  //$NON-NLS-1$
+                    JMeterUtils.getResString("action_check_title"),  //$NON-NLS-1$
+                    JOptionPane.WARNING_MESSAGE);
+            return;
+        }
+        doActionAfterCheck(e);
+    }
+
+    /**
+     * Called to handle {@link ActionEvent} only if no test is running
+     * @param e {@link ActionEvent}
+     * @throws IllegalUserActionException
+     */
+    protected abstract void doActionAfterCheck(ActionEvent e) throws IllegalUserActionException;
+}
diff --git a/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java b/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java
index bdc3950cd..f3124e071 100644
--- a/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java
+++ b/src/core/org/apache/jmeter/gui/action/ChangeLanguage.java
@@ -1,84 +1,73 @@
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
 
 import java.awt.Component;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Locale;
 import java.util.Set;
 
-import javax.swing.JOptionPane;
-
-import org.apache.jmeter.gui.GuiPackage;
-import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.log.Logger;
 
 /**
- * 
+ * Change language
  */
-public class ChangeLanguage extends AbstractAction {
+public class ChangeLanguage extends AbstractActionWithNoRunningTest {
     private static final Set<String> commands = new HashSet<>();
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     static {
         commands.add(ActionNames.CHANGE_LANGUAGE);
     }
 
     /**
-     * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
+     * @see org.apache.jmeter.gui.action.AbstractActionWithNoRunningTest#doActionAfterCheck(ActionEvent)
      */
     @Override
-    public void doAction(ActionEvent e) {
-        if (JMeterContextService.getTestStartTime()>0) {
-            JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(),
-                    JMeterUtils.getResString("language_change_test_running"),  //$NON-NLS-1$
-                    JMeterUtils.getResString("language_change_title"),  //$NON-NLS-1$
-                    JOptionPane.WARNING_MESSAGE);
-            return;
-        }
+    public void doActionAfterCheck(ActionEvent e) {
         String locale = ((Component) e.getSource()).getName();
         Locale loc;
 
         int sep = locale.indexOf('_');
         if (sep > 0) {
             loc = new Locale(locale.substring(0, sep), locale.substring(sep + 1));
         } else {
             loc = new Locale(locale, "");
         }
         log.debug("Changing locale to " + loc.toString());
         try {
             JMeterUtils.setLocale(loc);
         } catch (JMeterError err) {
             JMeterUtils.reportErrorToUser(err.toString());
         }
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Close.java b/src/core/org/apache/jmeter/gui/action/Close.java
index b087fbb62..dc0dd7f3d 100644
--- a/src/core/org/apache/jmeter/gui/action/Close.java
+++ b/src/core/org/apache/jmeter/gui/action/Close.java
@@ -1,113 +1,113 @@
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
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 import javax.swing.JTree;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.util.FocusRequester;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * This command clears the existing test plan, allowing the creation of a New
  * test plan.
  *
  */
-public class Close extends AbstractAction {
+public class Close extends AbstractActionWithNoRunningTest {
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.CLOSE);
     }
 
     /**
      * Constructor for the Close object.
      */
     public Close() {
     }
 
     /**
      * Gets the ActionNames attribute of the Close object.
      *
      * @return the ActionNames value
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     /**
      * This method performs the actual command processing.
      *
      * @param e
      *            the generic UI action event
      */
     @Override
-    public void doAction(ActionEvent e) {
+    public void doActionAfterCheck(ActionEvent e) {
         performAction(e);
     }
 
     /**
      * Helper routine to allow action to be shared by LOAD.
      *
      * @param e event
      * @return true if Close was not cancelled
      */
     static boolean performAction(ActionEvent e){
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.CHECK_DIRTY));
         GuiPackage guiPackage = GuiPackage.getInstance();
         if (guiPackage.isDirty()) {
             int response;
             if ((response = JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                     JMeterUtils.getResString("cancel_new_to_save"), // $NON-NLS-1$
                     JMeterUtils.getResString("save?"),  // $NON-NLS-1$
                     JOptionPane.YES_NO_CANCEL_OPTION,
                     JOptionPane.QUESTION_MESSAGE)) == JOptionPane.YES_OPTION) {
                 ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.SAVE));
                 // the user might cancel the file chooser dialog
                 // in this case we should not close the test plan
                 if (guiPackage.isDirty()) {
                     return false;
                 }
             }
             if (response == JOptionPane.CLOSED_OPTION || response == JOptionPane.CANCEL_OPTION) {
                 return false; // Don't clear the plan
             }
         }
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.STOP_THREAD));
         closeProject(e);
         return true;
     }
 
     static void closeProject(ActionEvent e) {
         GuiPackage guiPackage = GuiPackage.getInstance();
 
         guiPackage.clearTestPlan();
         JTree tree = guiPackage.getTreeListener().getJTree();
         tree.setSelectionRow(0);
         FocusRequester.requestFocus(tree);
         ActionRouter.getInstance().actionPerformed(new ActionEvent(e.getSource(), e.getID(), ActionNames.ADD_ALL));
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/ExitCommand.java b/src/core/org/apache/jmeter/gui/action/ExitCommand.java
index f0d6f79d4..3f33a4ac9 100644
--- a/src/core/org/apache/jmeter/gui/action/ExitCommand.java
+++ b/src/core/org/apache/jmeter/gui/action/ExitCommand.java
@@ -1,80 +1,80 @@
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
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.JMeterUtils;
 
-public class ExitCommand extends AbstractAction {
+public class ExitCommand extends AbstractActionWithNoRunningTest {
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.EXIT);
     }
 
     /**
      * Constructor for the ExitCommand object
      */
     public ExitCommand() {
     }
 
     /**
      * Gets the ActionNames attribute of the ExitCommand object
      *
      * @return The ActionNames value
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     /**
      * Description of the Method
      *
      * @param e
      *            Description of Parameter
      */
     @Override
-    public void doAction(ActionEvent e) {
+    public void doActionAfterCheck(ActionEvent e) {
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.CHECK_DIRTY));
         if (GuiPackage.getInstance().isDirty()) {
             int chosenOption = JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(), JMeterUtils
                     .getResString("cancel_exit_to_save"), // $NON-NLS-1$
                     JMeterUtils.getResString("save?"), // $NON-NLS-1$
                     JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (chosenOption == JOptionPane.NO_OPTION) {
                 System.exit(0);
             } else if (chosenOption == JOptionPane.YES_OPTION) {
                 ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.SAVE));
                 if (!GuiPackage.getInstance().isDirty()) {
                     System.exit(0);
                 }
             }
         } else {
             System.exit(0);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Load.java b/src/core/org/apache/jmeter/gui/action/Load.java
index 15f26af82..dc00a80a2 100644
--- a/src/core/org/apache/jmeter/gui/action/Load.java
+++ b/src/core/org/apache/jmeter/gui/action/Load.java
@@ -1,246 +1,246 @@
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
 import java.io.IOException;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 import javax.swing.JTree;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.FocusRequester;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * Handles the Open (load a new file) and Merge commands.
  *
  */
-public class Load extends AbstractAction {
+public class Load extends AbstractActionWithNoRunningTest {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final boolean expandTree = JMeterUtils.getPropDefault("onload.expandtree", false); //$NON-NLS-1$
 
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.OPEN);
         commands.add(ActionNames.MERGE);
     }
 
     public Load() {
         super();
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
-    public void doAction(final ActionEvent e) {
+    public void doActionAfterCheck(final ActionEvent e) {
         final JFileChooser chooser = FileDialoger.promptToOpenFile(new String[] { ".jmx" }); //$NON-NLS-1$
         if (chooser == null) {
             return;
         }
         final File selectedFile = chooser.getSelectedFile();
         if (selectedFile != null) {
             final boolean merging = e.getActionCommand().equals(ActionNames.MERGE);
             // We must ask the user if it is ok to close current project
             if (!merging) { // i.e. it is OPEN
                 if (!Close.performAction(e)) {
                     return;
                 }
             }
             loadProjectFile(e, selectedFile, merging);
         }
     }
 
     /**
      * Loads or merges a file into the current GUI, reporting any errors to the user.
      * If the file is a complete test plan, sets the GUI test plan file name
      *
      * @param e the event that triggered the action
      * @param f the file to load
      * @param merging if true, then try to merge the file into the current GUI.
      */
     static void loadProjectFile(final ActionEvent e, final File f, final boolean merging) {
         loadProjectFile(e, f, merging, true);
     }
 
     /**
      * Loads or merges a file into the current GUI, reporting any errors to the user.
      * If the file is a complete test plan, sets the GUI test plan file name
      *
      * @param e the event that triggered the action
      * @param f the file to load
      * @param merging if true, then try to merge the file into the current GUI.
      * @param setDetails if true, then set the file details (if not merging)
      */
     static void loadProjectFile(final ActionEvent e, final File f, final boolean merging, final boolean setDetails) {
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.STOP_THREAD));
 
         final GuiPackage guiPackage = GuiPackage.getInstance();
         if (f != null) {
             try {
                 if (merging) {
                     log.info("Merging file: " + f);
                 } else {
                     log.info("Loading file: " + f);
                     // TODO should this be done even if not a full test plan?
                     // and what if load fails?
                     if (setDetails) {
                         FileServer.getFileServer().setBaseForScript(f);
                     }
                 }
                 final HashTree tree = SaveService.loadTree(f);
                 final boolean isTestPlan = insertLoadedTree(e.getID(), tree, merging);
 
                 // don't change name if merging
                 if (!merging && isTestPlan && setDetails) {
                     // TODO should setBaseForScript be called here rather than
                     // above?
                     guiPackage.setTestPlanFile(f.getAbsolutePath());
                 }
             } catch (NoClassDefFoundError ex) {// Allow for missing optional jars
                 reportError("Missing jar file", ex, true);
             } catch (ConversionException ex) {
                 log.warn("Could not convert file "+ex);
                 JMeterUtils.reportErrorToUser(SaveService.CEtoString(ex));
             } catch (IOException ex) {
                 reportError("Error reading file: ", ex, false);
             } catch (Exception ex) {
                 reportError("Unexpected error", ex, true);
             }
             FileDialoger.setLastJFCDirectory(f.getParentFile().getAbsolutePath());
             guiPackage.updateCurrentGui();
             guiPackage.getMainFrame().repaint();
         }
     }
 
     /**
      * Inserts (or merges) the tree into the GUI.
      * Does not check if the previous tree has been saved.
      * Clears the existing GUI test plan if we are inserting a complete plan.
      * @param id the id for the ActionEvent that is created
      * @param tree the tree to load
      * @param merging true if the tree is to be merged; false if it is to replace the existing tree
      * @return true if the loaded tree was a full test plan
      * @throws IllegalUserActionException if the tree cannot be merged at the selected position or the tree is empty
      */
     // Does not appear to be used externally; called by #loadProjectFile()
     public static boolean insertLoadedTree(final int id, final HashTree tree, final boolean merging) throws IllegalUserActionException {
         // convertTree(tree);
         if (tree == null) {
             throw new IllegalUserActionException("Empty TestPlan or error reading test plan - see log file");
         }
         final boolean isTestPlan = tree.getArray()[0] instanceof TestPlan;
 
         // If we are loading a new test plan, initialize the tree with the testplan node we are loading
         final GuiPackage guiInstance = GuiPackage.getInstance();
         if(isTestPlan && !merging) {
             // Why does this not call guiInstance.clearTestPlan() ?
             // Is there a reason for not clearing everything?
             guiInstance.clearTestPlan((TestElement)tree.getArray()[0]);
         }
 
         if (merging){ // Check if target of merge is reasonable
             final TestElement te = (TestElement)tree.getArray()[0];
             if (!(te instanceof WorkBench || te instanceof TestPlan)){// These are handled specially by addToTree
                 final boolean ok = MenuFactory.canAddTo(guiInstance.getCurrentNode(), te);
                 if (!ok){
                     String name = te.getName();
                     String className = te.getClass().getName();
                     className = className.substring(className.lastIndexOf('.')+1);
                     throw new IllegalUserActionException("Can't merge "+name+" ("+className+") here");
                 }
             }
         }
         final HashTree newTree = guiInstance.addSubTree(tree);
         guiInstance.updateCurrentGui();
         guiInstance.getMainFrame().getTree().setSelectionPath(
                 new TreePath(((JMeterTreeNode) newTree.getArray()[0]).getPath()));
         final HashTree subTree = guiInstance.getCurrentSubTree();
         // Send different event wether we are merging a test plan into another test plan,
         // or loading a testplan from scratch
         ActionEvent actionEvent =
             new ActionEvent(subTree.get(subTree.getArray()[subTree.size() - 1]), id,
                     merging ? ActionNames.SUB_TREE_MERGED : ActionNames.SUB_TREE_LOADED);
 
         ActionRouter.getInstance().actionPerformed(actionEvent);
         final JTree jTree = guiInstance.getMainFrame().getTree();
         if (expandTree && !merging) { // don't automatically expand when merging
             for(int i = 0; i < jTree.getRowCount(); i++) {
                 jTree.expandRow(i);
             }
         } else {
             jTree.expandRow(0);
         }
         jTree.setSelectionPath(jTree.getPathForRow(1));
         FocusRequester.requestFocus(jTree);
         return isTestPlan;
     }
 
     /**
      * Inserts the tree into the GUI.
      * Does not check if the previous tree has been saved.
      * Clears the existing GUI test plan if we are inserting a complete plan.
      * @param id the id for the ActionEvent that is created
      * @param tree the tree to load
      * @return true if the loaded tree was a full test plan
      * @throws IllegalUserActionException if the tree cannot be merged at the selected position or the tree is empty
      */
     // Called by JMeter#startGui()
     public static boolean insertLoadedTree(final int id, final HashTree tree) throws IllegalUserActionException {
         return insertLoadedTree(id, tree, false);
     }
 
     // Helper method to simplify code
     private static void reportError(final String reason, final Throwable ex, final boolean stackTrace) {
         if (stackTrace) {
             log.warn(reason, ex);
         } else {
             log.warn(reason + ex);
         }
         String msg = ex.getMessage();
         if (msg == null) {
             msg = "Unexpected error - see log for details";
         }
         JMeterUtils.reportErrorToUser(msg);
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/action/LoadRecentProject.java b/src/core/org/apache/jmeter/gui/action/LoadRecentProject.java
index 516aa87a1..433d38385 100644
--- a/src/core/org/apache/jmeter/gui/action/LoadRecentProject.java
+++ b/src/core/org/apache/jmeter/gui/action/LoadRecentProject.java
@@ -1,260 +1,260 @@
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
 import java.awt.event.KeyEvent;
 import java.io.File;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 import java.util.prefs.Preferences;
 
 import javax.swing.JComponent;
 import javax.swing.JMenuItem;
 import javax.swing.JSeparator;
 
 /**
  * Handles the loading of recent files, and also the content and
  * visibility of menu items for loading the recent files
  */
 public class LoadRecentProject extends Load {
     /** Prefix for the user preference key */
     private static final String USER_PREFS_KEY = "recent_file_"; //$NON-NLS-1$
     /** The number of menu items used for recent files */
     private static final int NUMBER_OF_MENU_ITEMS = 9;
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.OPEN_RECENT);
     }
 
     private static final Preferences prefs = Preferences.userNodeForPackage(LoadRecentProject.class);
     // Note: Windows user preferences are stored relative to: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
 
     public LoadRecentProject() {
         super();
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
-    public void doAction(ActionEvent e) {
+    public void doActionAfterCheck(ActionEvent e) {
         // We must ask the user if it is ok to close current project
         if (!Close.performAction(e)) {
             return;
         }
         // Load the file for this recent file command
         loadProjectFile(e, getRecentFile(e), false);
     }
 
     /**
      * Get the recent file for the menu item
      */
     private File getRecentFile(ActionEvent e) {
         JMenuItem menuItem = (JMenuItem)e.getSource();
         // Get the preference for the recent files
         return new File(getRecentFile(Integer.parseInt(menuItem.getName())));
     }
 
     /**
      * Get the menu items to add to the menu bar, to get recent file functionality
      *
      * @return a List of JMenuItem and a JSeparator, representing recent files
      */
     public static List<JComponent> getRecentFileMenuItems() {
         LinkedList<JComponent> menuItems = new LinkedList<>();
         // Get the preference for the recent files
         for(int i = 0; i < NUMBER_OF_MENU_ITEMS; i++) {
             // Create the menu item
             JMenuItem recentFile = new JMenuItem();
             // Use the index as the name, used when processing the action
             recentFile.setName(Integer.toString(i));
             recentFile.addActionListener(ActionRouter.getInstance());
             recentFile.setActionCommand(ActionNames.OPEN_RECENT);
             // Set the KeyStroke to use
             int shortKey = getShortcutKey(i);
             if(shortKey >= 0) {
                 recentFile.setMnemonic(shortKey);
             }
             // Add the menu item
             menuItems.add(recentFile);
         }
         // Add separator as the last item
         JSeparator separator = new JSeparator();
         separator.setVisible(false);
         menuItems.add(separator);
 
         // Update menu items to reflect recent files
         updateMenuItems(menuItems);
 
         return menuItems;
     }
 
     /**
      * Update the content and visibility of the menu items for recent files
      *
      * @param menuItems the JMenuItem and JSeparator to update
      * @param loadedFileName the file name of the project file that has just
      * been loaded
      */
     public static void updateRecentFileMenuItems(List<JComponent> menuItems, String loadedFileName) {
         // Get the preference for the recent files
 
         LinkedList<String> newRecentFiles = new LinkedList<>();
         // Check if the new file is already in the recent list
         boolean alreadyExists = false;
         for(int i = 0; i < NUMBER_OF_MENU_ITEMS; i++) {
             String recentFilePath = getRecentFile(i);
             if(!loadedFileName.equals(recentFilePath)) {
                 newRecentFiles.add(recentFilePath);
             }
             else {
                 alreadyExists = true;
             }
         }
         // Add the new file at the start of the list
         newRecentFiles.add(0, loadedFileName);
         // Remove the last item from the list if it was a brand new file
         if(!alreadyExists) {
             newRecentFiles.removeLast();
         }
         // Store the recent files
         for(int i = 0; i < NUMBER_OF_MENU_ITEMS; i++) {
             String fileName = newRecentFiles.get(i);
             if(fileName != null) {
                 setRecentFile(i, fileName);
             }
         }
         // Update menu items to reflect recent files
         updateMenuItems(menuItems);
     }
 
     /**
      * Set the content and visibility of menu items and menu separator,
      * based on the recent file stored user preferences.
      */
     private static void updateMenuItems(List<JComponent> menuItems) {
         // Assume no recent files
         boolean someRecentFiles = false;
         // Update the menu items
         for(int i = 0; i < NUMBER_OF_MENU_ITEMS; i++) {
             // Get the menu item
             JMenuItem recentFile = (JMenuItem)menuItems.get(i);
 
             // Find and set the file for this recent file command
             String recentFilePath = getRecentFile(i);
             if(recentFilePath != null) {
                 File file = new File(recentFilePath);
                 StringBuilder sb = new StringBuilder(60);
                 if (i<9) {
                     sb.append(i+1).append(" "); //$NON-NLS-1$
                 }
                 sb.append(getMenuItemDisplayName(file));
                 recentFile.setText(sb.toString());
                 recentFile.setToolTipText(recentFilePath);
                 recentFile.setEnabled(true);
                 recentFile.setVisible(true);
                 // At least one recent file menu item is visible
                 someRecentFiles = true;
             }
             else {
                 recentFile.setEnabled(false);
                 recentFile.setVisible(false);
             }
         }
         // If there are some recent files, we must make the separator visisble
         // The separator is the last item in the list
         JSeparator separator = (JSeparator)menuItems.get(menuItems.size() - 1);
         separator.setVisible(someRecentFiles);
     }
 
     /**
      * Get the name to display in the menu item, it will chop the file name
      * if it is too long to display in the menu bar
      */
     private static String getMenuItemDisplayName(File file) {
         // Limit the length of the menu text if needed
         final int maxLength = 40;
         String menuText = file.getName();
         if(menuText.length() > maxLength) {
             menuText = "..." + menuText.substring(menuText.length() - maxLength, menuText.length()); //$NON-NLS-1$
         }
         return menuText;
     }
 
     /**
      * Get the KeyEvent to use as shortcut key for menu item
      */
     private static int getShortcutKey(int index) {
         int shortKey = -1;
         switch(index+1) {
             case 1:
                 shortKey = KeyEvent.VK_1;
                 break;
             case 2:
                 shortKey = KeyEvent.VK_2;
                 break;
             case 3:
                 shortKey = KeyEvent.VK_3;
                 break;
             case 4:
                 shortKey = KeyEvent.VK_4;
                 break;
             case 5:
                 shortKey = KeyEvent.VK_5;
                 break;
             case 6:
                 shortKey = KeyEvent.VK_6;
                 break;
             case 7:
                 shortKey = KeyEvent.VK_7;
                 break;
             case 8:
                 shortKey = KeyEvent.VK_8;
                 break;
             case 9:
                 shortKey = KeyEvent.VK_9;
                 break;
             default:
                 break;
         }
         return shortKey;
     }
 
     /**
      * Get the full path to the recent file where index 0 is the most recent
      * @param index the index of the recent file
      * @return full path to the recent file at <code>index</code>
      */
     public static String getRecentFile(int index) {
         return prefs.get(USER_PREFS_KEY + index, null);
     }
 
     /**
      * Set the full path to the recent file where index 0 is the most recent
      */
     private static void setRecentFile(int index, String fileName) {
         prefs.put(USER_PREFS_KEY + index, fileName);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Move.java b/src/core/org/apache/jmeter/gui/action/Move.java
index 4ac238d3c..bd8e2d0c4 100644
--- a/src/core/org/apache/jmeter/gui/action/Move.java
+++ b/src/core/org/apache/jmeter/gui/action/Move.java
@@ -1,156 +1,160 @@
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
 
 import java.awt.Toolkit;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JTree;
 import javax.swing.tree.TreeNode;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 
+/**
+ * Move a node up/down/left/right 
+ *
+ */
 public class Move extends AbstractAction {
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.MOVE_DOWN);
         commands.add(ActionNames.MOVE_UP);
         commands.add(ActionNames.MOVE_LEFT);
         commands.add(ActionNames.MOVE_RIGHT);
     }
 
     /**
      * @see Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         JMeterTreeListener treeListener = GuiPackage.getInstance()
                 .getTreeListener();
 
         if (treeListener.getSelectedNodes().length != 1) {
             // we can only move a single node
             return;
         }
 
         JMeterTreeNode currentNode = treeListener.getCurrentNode();
         JMeterTreeNode parentNode = getParentNode(currentNode);
 
         if (parentNode != null) {
             String action = e.getActionCommand();
             int index = parentNode.getIndex(currentNode);
 
             if (ActionNames.MOVE_UP.equals(action)) {
                 if (index > 0) {
                     // we stay within the same parent node
                     int newIndx = index - 1;
                     moveAndSelectNode(currentNode, parentNode, newIndx);
                 }
             } else if (ActionNames.MOVE_DOWN.equals(action)) {
                 if (index < parentNode.getChildCount() - 1) {
                     // we stay within the same parent node
                     int newIndx = index + 1;
                     moveAndSelectNode(currentNode, parentNode, newIndx);
                 }
             } else if (ActionNames.MOVE_LEFT.equals(action)) {
                 JMeterTreeNode parentParentNode = getParentNode(parentNode);
                 // move to the parent
                 if (parentParentNode != null
                         && canAddTo(parentParentNode, currentNode)) {
                     moveAndSelectNode(currentNode, parentParentNode,
                             parentParentNode.getIndex(parentNode));
                 }
             } else if (ActionNames.MOVE_RIGHT.equals(action)) {
                 JMeterTreeNode after = (JMeterTreeNode) parentNode
                         .getChildAfter(currentNode);
                 if (after != null && canAddTo(after, currentNode)) {
                     // move as a child of the next sibling
                     moveAndSelectNode(currentNode, after, 0);
                 }
                 // Commented as per sebb 
                 // http://mail-archives.apache.org/mod_mbox/jmeter-dev/201307.mbox/%3CCAOGo0VZ0z3GMbfsq_gSB%2Bp7nTUqLng6Gy2ecvYbD8_AKb-Dt5w%40mail.gmail.com%3E
                 /*
                 else {
                     // move as a sibling of the parent
                     JMeterTreeNode parentParentNode = getParentNode(parentNode);
                     after = (JMeterTreeNode) parentParentNode
                             .getChildAfter(parentNode);
                     if (after != null
                             && canAddTo(parentParentNode, currentNode)) {
                         moveAndSelectNode(currentNode, parentParentNode,
                                 parentParentNode.getIndex(after));
                     }
                 }
                 */
             }
         }
 
         GuiPackage.getInstance().getMainFrame().repaint();
     }
 
     private JMeterTreeNode getParentNode(JMeterTreeNode currentNode) {
         JMeterTreeNode parentNode = (JMeterTreeNode) currentNode.getParent();
         TestElement te = currentNode.getTestElement();
         if (te instanceof TestPlan || te instanceof WorkBench) {
             parentNode = null; // So elements can only be added as children
         }
         return parentNode;
     }
 
     private static boolean canAddTo(JMeterTreeNode parentNode,
             JMeterTreeNode node) {
         boolean ok = MenuFactory.canAddTo(parentNode,
                 new JMeterTreeNode[] { node });
         if (!ok) {
             Toolkit.getDefaultToolkit().beep();
         }
         return ok;
     }
 
     private static void moveAndSelectNode(JMeterTreeNode currentNode,
             JMeterTreeNode parentNode, int newIndx) {
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.getTreeModel().removeNodeFromParent(currentNode);
         guiInstance.getTreeModel().insertNodeInto(currentNode, parentNode,
                 newIndx);
 
         // select the node
         TreeNode[] nodes = guiInstance.getTreeModel()
                 .getPathToRoot(currentNode);
         JTree jTree = guiInstance.getMainFrame().getTree();
         jTree.setSelectionPath(new TreePath(nodes));
     }
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/RevertProject.java b/src/core/org/apache/jmeter/gui/action/RevertProject.java
index b457d50e2..455e4089e 100644
--- a/src/core/org/apache/jmeter/gui/action/RevertProject.java
+++ b/src/core/org/apache/jmeter/gui/action/RevertProject.java
@@ -1,78 +1,78 @@
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
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Handles the Revert Project command.
  *
  */
-public class RevertProject extends AbstractAction {
+public class RevertProject extends AbstractActionWithNoRunningTest {
     private static final Set<String> commands = new HashSet<>();
 
     static {
         commands.add(ActionNames.REVERT_PROJECT);
     }
 
     public RevertProject() {
         super();
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
-    public void doAction(ActionEvent e) {
+    public void doActionAfterCheck(ActionEvent e) {
         // Get the file name of the current project
         String projectFile = GuiPackage.getInstance().getTestPlanFile();
         // Check if the user has loaded any file
         if(projectFile == null) {
             return;
         }
 
         // Check if the user wants to drop any changes
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.CHECK_DIRTY));
         GuiPackage guiPackage = GuiPackage.getInstance();
         if (guiPackage.isDirty()) {
             // Check if the user wants to revert
             int response = JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                     JMeterUtils.getResString("cancel_revert_project"), // $NON-NLS-1$
                     JMeterUtils.getResString("revert_project?"),  // $NON-NLS-1$
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.QUESTION_MESSAGE);
             if(response == JOptionPane.YES_OPTION) {
                 // Close the current project
                 Close.closeProject(e);
                 // Reload the project
                 Load.loadProjectFile(e, new File(projectFile), false);
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/TemplatesCommand.java b/src/core/org/apache/jmeter/gui/action/TemplatesCommand.java
index 005b52c94..e72f3f5ed 100644
--- a/src/core/org/apache/jmeter/gui/action/TemplatesCommand.java
+++ b/src/core/org/apache/jmeter/gui/action/TemplatesCommand.java
@@ -1,58 +1,58 @@
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
 import java.util.HashSet;
 import java.util.Set;
 
 /**
  * Open Templates 
  * @since 2.10
  */
-public class TemplatesCommand extends AbstractAction {
+public class TemplatesCommand extends AbstractActionWithNoRunningTest {
 
     private static final Set<String> commands = new HashSet<>();
 
     // Ensure the dialog is only created when it is first needed
     // In turn this avoids scanning the templates until first needed
     static class IODH {
         private static final SelectTemplatesDialog dialog = new SelectTemplatesDialog();        
     }
 
     static {
         commands.add(ActionNames.TEMPLATES);
     }
 
     /**
-     * @see Command#doAction(ActionEvent)
+     * @see org.apache.jmeter.gui.action.AbstractActionWithNoRunningTest#doActionAfterCheck(ActionEvent)
      */
     @Override
-    public void doAction(ActionEvent e) {
+    public void doActionAfterCheck(ActionEvent e) {
         IODH.dialog.setVisible(true);
     }
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index faca43900..e2efb6971 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1369 +1,1369 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 # Warning: JMeterUtils.getResString() replaces space with '_'
 # and converts keys to lowercase before lookup
 # => All keys in this file must also be lower case or they won't match
 #
 
 # Please add new entries in alphabetical order
 
 about=About Apache JMeter
 active_threads_tooltip=Running threads
 add=Add
 add_as_child=Add as Child
 add_from_clipboard=Add from Clipboard
 add_from_suggested_excludes=Add suggested Excludes
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 aggregate_graph=Statistical Graphs
 aggregate_graph_choose_color=Choose color
 aggregate_graph_choose_foreground_color=Foreground color
 aggregate_graph_color_bar=Color\:
 aggregate_graph_column=Column\:
 aggregate_graph_column_selection=Column label selection\:
 aggregate_graph_column_settings=Column settings
 aggregate_graph_columns_to_display=Columns to display\:
 aggregate_graph_dimension=Graph size
 aggregate_graph_display=Display Graph
 aggregate_graph_draw_outlines=Draw outlines bar?
 aggregate_graph_dynamic_size=Dynamic graph size
 aggregate_graph_font=Font\:
 aggregate_graph_height=Height\:
 aggregate_graph_increment_scale=Increment scale\:
 aggregate_graph_legend=Legend
 aggregate_graph_legend.placement.bottom=Bottom
 aggregate_graph_legend.placement.left=Left
 aggregate_graph_legend.placement.right=Right
 aggregate_graph_legend.placement.top=Top
 aggregate_graph_legend_placement=Placement\:
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label\:
 aggregate_graph_ms=Milliseconds
 aggregate_graph_no_values_to_graph=No values to graph
 aggregate_graph_number_grouping=Show number grouping?
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_size=Size\:
 aggregate_graph_style=Style\:
 aggregate_graph_sync_with_name=Synchronize with name
 aggregate_graph_tab_graph=Graph
 aggregate_graph_tab_settings=Settings
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_title_group=Title
 aggregate_graph_use_group_name=Include group name in label?
 aggregate_graph_user_title=Graph title\:
 aggregate_graph_value_font=Value font\:
 aggregate_graph_value_labels_vertical=Value labels vertical?
 aggregate_graph_width=Width\:
 aggregate_graph_xaxis_group=X Axis
 aggregate_graph_yaxis_group=Y Axis (milli-seconds)
 aggregate_graph_yaxis_max_value=Scale maximum value\:
 aggregate_report=Aggregate Report
 aggregate_report_xx_pct1_line={0}% Line
 aggregate_report_xx_pct2_line={0}% Line
 aggregate_report_xx_pct3_line={0}% Line
 aggregate_report_90=90%
 aggregate_report_bandwidth=Received KB/sec
 aggregate_report_sent_bytes_per_sec=Sent KB/sec
 aggregate_report_count=# Samples
 aggregate_report_error=Error
 aggregate_report_error%=Error %
 aggregate_report_max=Max
 aggregate_report_median=Median
 aggregate_report_min=Min
 aggregate_report_rate=Throughput
 aggregate_report_stddev=Std. Dev.
 aggregate_report_total_label=TOTAL
 ajp_sampler_title=AJP/1.3 Sampler
 als_message=Note\: The Access Log Parser is generic in design and allows you to plugin
 als_message2=your own parser. To do so, implement the LogParser, add the jar to the
 als_message3=/lib directory and enter the class in the sampler.
 analyze=Analyze Data File...
 anchor_modifier_title=HTML Link Parser
 appearance=Look and Feel
 argument_must_not_be_negative=The Argument must not be negative\!
 arguments_panel_title=Command parameters
 assertion_assume_success=Ignore Status
 assertion_body_resp=Response Body
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_network_size=Full Response
 assertion_not=Not
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_regex_empty_default_value=Use empty default value
 assertion_resp_field=Response Field to Test
 assertion_resp_size_field=Response Size Field to Test
 assertion_substring=Substring
 assertion_text_document=Document (text)
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attribute_field=Attribute\:
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_clear_per_iter=Clear auth on each iteration?
 auth_manager_options=Options
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 backend_listener=Backend Listener
 backend_listener_classname=Backend Listener implementation
 backend_listener_paramtable=Parameters
 backend_listener_queue_size=Async Queue size
 bind=Thread Bind
 bouncy_castle_unavailable_message=The jars for bouncy castle are unavailable, please add them to your classpath.
 browse=Browse...
 bsf_sampler_title=BSF Sampler
 bsf_script=Script to run (variables: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Script file to run
 bsf_script_language=Scripting language\:
 bsf_script_parameters=Parameters to pass to script/file\:
 bsh_assertion_script=Script (see below for variables that are defined)
 bsh_assertion_script_variables=The following variables are defined for the script:\nRead/Write: Failure, FailureMessage, SampleResult, vars, props, log.\nReadOnly: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=BeanShell Assertion
 bsh_function_expression=Expression to evaluate
 bsh_sampler_title=BeanShell Sampler
 bsh_script=Script (see below for variables that are defined)
 bsh_script_file=Script file
 bsh_script_parameters=Parameters (-> String Parameters and String []bsh.args)
 bsh_script_reset_interpreter=Reset bsh.Interpreter before each call
 bsh_script_variables=The following variables are defined for the script\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=I'm busy testing, please stop the test before changing settings
 cache_manager_size=Max Number of elements in cache
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_from_template=There are test items that have not been saved.  Do you wish to save before creating a test plan from selected template?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 change_parent=Change Controller
 char_value=Unicode character number (decimal or 0xhex)
 check_return_code_title=Check Return Code
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cache_each_iteration=Clear cache each iteration
 clear_cache_per_iter=Clear cache each iteration?
 clear_cookies_per_iter=Clear cookies each iteration?
 clipboard_node_read_error=An error occurred while copying node
 close=Close
 closeconnection=Close connection
 collapse_tooltip=Click to open / collapse
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
 command_config_box_title=Command to Execute
 command_config_std_streams_title=Standard streams (files)
 command_field_title=Command:
 compare=Compare
 comparefilt=Compare filter
 comparison_differ_content=Responses differ in content
 comparison_differ_time=Responses differ in response time by more than 
 comparison_invalid_node=Invalid Node 
 comparison_regex_string=Regex String
 comparison_regex_substitution=Substitution
 comparison_response_time=Response Time: 
 comparison_unit=\ ms
 comparison_visualizer_title=Comparison Assertion Visualizer
 config_element=Config Element
 config_save_settings=Configure
 confirm=Confirm
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_implementation_choose=Implementation:
 cookie_manager_policy=Cookie Policy:
 cookie_manager_title=HTTP Cookie Manager
 cookie_options=Options
 cookies_stored=User-Defined Cookies
 copy=Copy
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 counter_reset_per_tg_iteration=Reset counter on each Thread Group Iteration
 countlim=Size limit
 critical_section_controller_label=Lock name
 critical_section_controller_title=Critical Section Controller
 cssjquery_attribute=Attribute\:
 cssjquery_empty_default_value=Use empty default value
 cssjquery_tester_error=An error occured evaluating expression:{0}, error:{1}
 cssjquery_impl=CSS/JQuery implementation\:
 cssjquery_render_no_text=Data response result isn't text.
 cssjquery_tester_button_test=Test
 cssjquery_tester_field=Selector\:
 cssjquery_tester_title=CSS/JQuery Tester
 csvread_file_file_name=CSV file to get values from | *alias
 cut=Cut
 cut_paste_function=Copy and paste function string
 database_conn_pool_max_usage=Max Usage For Each Connection\:
 database_conn_pool_props=Database Connection Pool
 database_conn_pool_size=Number of Connections in Pool\:
 database_conn_pool_title=JDBC Database Connection Pool Defaults
 database_driver_class=Driver Class\:
 database_login_title=JDBC Database Login Defaults
 database_sql_query_string=SQL Query String\:
 database_sql_query_title=JDBC SQL Query Defaults
 database_testing_title=JDBC Request
 database_url=JDBC URL\:
 database_url_jdbc_props=Database URL and JDBC Driver
 ddn=DN
 de=German
 debug_off=Disable debug
 debug_on=Enable debug
 default_parameters=Default Parameters
 default_value_field=Default Value\:
 delay=Startup delay (seconds)
 delayed_start=Delay Thread creation until needed
 delete=Delete
 delete_parameter=Delete Variable
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 description=Description
 detail=Detail
 directory_field_title=Working directory:
 disable=Disable
 distribution_graph_title=Distribution Graph (DEPRECATED)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 dns_cache_manager_title=DNS Cache Manager
 dns_hostname_or_ip=Hostname or IP address
 dns_servers=DNS Servers
 domain=Domain
 done=Done
 down=Down
 duplicate=Duplicate
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 duration_tooltip=Elapsed time of current running Test
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Enable
 encode=URL Encode
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
 environment_panel_title=Environment Variables
 eolbyte=End of line(EOL) byte value: 
 error_indicator_tooltip=Show the number of errors in log, click to open Log Viewer panel
 error_loading_help=Error loading help page
 error_occurred=Error Occurred
 error_title=Error
 es=Spanish
 escape_html_string=String to escape
 eval_name_param=Text containing variable and function references
 evalvar_name_param=Name of variable
 example_data=Sample Data
 example_title=Example Sampler
 exit=Exit
 find_target_element=Find target element
 expected_return_code_title=Expected Return Code: 
 expiration=Expiration
 expression_field=CSS/JQuery expression\:
 field_name=Field name
 file=File
 file_already_in_use=That file is already in use
 file_visualizer_append=Append to Existing Data File
 file_visualizer_auto_flush=Automatically Flush After Each Data Sample
 file_visualizer_browse=Browse...
 file_visualizer_close=Close
 file_visualizer_file_options=File Options
 file_visualizer_filename=Filename
 file_visualizer_flush=Flush
 file_visualizer_missing_filename=No output filename specified.
 file_visualizer_open=Open
 file_visualizer_output_file=Write results to file / Read from file
 file_visualizer_submit_data=Include Submitted Data
 file_visualizer_title=File Reporter
 file_visualizer_verbose=Verbose Output
 filename=File Name
 follow_redirects=Follow Redirects
 follow_redirects_auto=Redirect Automatically
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Bold
 fontstyle.italic=Italic
 fontstyle.normal=Normal
 foreach_controller_title=ForEach Controller
 foreach_end_index=End index for loop (inclusive)
 foreach_input=Input variable prefix
 foreach_output=Output variable name
 foreach_start_index=Start index for loop (exclusive)
 foreach_use_separator=Add "_" before number ?
 format=Number format
 fr=French
 ftp_binary_mode=Use Binary mode ?
 ftp_get=get(RETR)
 ftp_local_file=Local File:
 ftp_local_file_contents=Local File Contents:
 ftp_put=put(STOR)
 ftp_remote_file=Remote File:
 ftp_sample_title=FTP Request Defaults
 ftp_save_response_data=Save File in Response ?
 ftp_testing_title=FTP Request
 function_dialog_menu_item=Function Helper Dialog
 function_helper_title=Function Helper
 function_name_param=Name of variable in which to store the result (required)
 function_name_paropt=Name of variable in which to store the result (optional)
 function_params=Function Parameters
 functional_mode=Functional Test Mode (i.e. save Response Data and Sampler Data)
 functional_mode_explanation=Selecting Functional Test Mode may adversely affect performance.
 gaussian_timer_delay=Constant Delay Offset (in milliseconds)\:
 gaussian_timer_memo=Adds a random delay with a gaussian distribution
 gaussian_timer_range=Deviation (in milliseconds)\:
 gaussian_timer_title=Gaussian Random Timer
 generate=Generate
 generator=Name of Generator class
 generator_cnf_msg=Could not find the generator class. Please make sure you place your jar file in the /lib directory.
 generator_illegal_msg=Could not access the generator class due to IllegalAccessException.
 generator_instantiate_msg=Could not create an instance of the generator parser. Please make sure the generator implements Generator interface.
 graph_apply_filter=Apply filter
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_pointshape_circle=Circle
 graph_pointshape_diamond=Diamond
 graph_pointshape_none=None
 graph_pointshape_square=Square
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms):
 graph_resp_time_interval_reload=Apply interval
 graph_resp_time_not_enough_data=Unable to graph, not enough data
 graph_resp_time_series_selection=Sampler label selection:
 graph_resp_time_settings_line=Line settings
 graph_resp_time_settings_pane=Graph settings
 graph_resp_time_shape_label=Shape point:
 graph_resp_time_stroke_width=Stroke width:
 graph_resp_time_title=Response Time Graph
 graph_resp_time_title_label=Graph title:
 graph_resp_time_xaxis_time_format=Time format (SimpleDateFormat):
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 groovy_function_expression=Expression to evaluate
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_in_transaction_controllers=Put each group in a new transaction controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 heap_dump=Create a heap dump
 help=Help
 help_node=What's this node?
 html_assertion_file=Write JTidy report to file
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_extractor_title=CSS/JQuery Extractor
 html_extractor_type=CSS/JQuery Extractor Implementation
 http_implementation=Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
 httpmirror_max_pool_size=Max number of Threads:
 httpmirror_max_queue_size=Max queue size:
 httpmirror_settings=Settings
 httpmirror_title=HTTP Mirror Server
 id_prefix=ID Prefix
 id_suffix=ID Suffix
 if_controller_evaluate_all=Evaluate for all children?
 if_controller_expression=Interpret Condition as Variable Expression?
 if_controller_label=Condition (default Javascript)
 if_controller_title=If Controller
 ignore_subcontrollers=Ignore sub-controller blocks
 include_controller=Include Controller
 include_equals=Include Equals?
 include_path=Include Test Plan
 increment=Increment
 infinite=Forever
 initial_context_factory=Initial Context Factory
 insert_after=Insert After
 insert_before=Insert Before
 insert_parent=Insert Parent
 interleave_control_title=Interleave Controller
 interleave_accross_threads=Interleave accross threads
 intsum_param_1=First int to add.
 intsum_param_2=Second int to add - further ints can be summed by adding further arguments.
 invalid_data=Invalid data
 invalid_mail=Error occurred sending the e-mail
 invalid_mail_address=One or more invalid e-mail addresses detected
 invalid_mail_server=Problem contacting the e-mail server (see JMeter log file)
 invalid_variables=Invalid variables
 iteration_counter_arg_1=TRUE, for each user to have own counter, FALSE for a global counter
 iterator_num=Loop Count\:
 ja=Japanese
 jar_file=Jar Files
 java_request=Java Request
 java_request_defaults=Java Request Defaults
 javascript_expression=JavaScript expression to evaluate
 jexl_expression=JEXL expression to evaluate
 jms_auth_required=Required
 jms_bytes_message=Bytes Message
 jms_client_caption=Receiver client uses MessageConsumer.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_id=Client ID
 jms_client_type=Client
 jms_communication_style=Communication style
 jms_concrete_connection_factory=Concrete Connection Factory
 jms_config=Message source
 jms_config_title=JMS Configuration
 jms_connection_factory=Connection Factory
 jms_correlation_title=Use alternate fields for message correlation
 jms_dest_setup=Setup
 jms_dest_setup_dynamic=Each sample
 jms_dest_setup_static=At startup
 jms_durable_subscription_id=Durable Subscription ID
 jms_expiration=Expiration (ms)
 jms_file=File
 jms_initial_context_factory=Initial Context Factory
 jms_itertions=Number of samples to aggregate
 jms_jndi_defaults_title=JNDI Default Configuration
 jms_jndi_props=JNDI Properties
 jms_map_message=Map Message
 jms_message_title=Message properties
 jms_message_type=Message Type
 jms_msg_content=Content
 jms_object_message=Object Message
 jms_point_to_point=JMS Point-to-Point
 jms_priority=Priority (0-9)
 jms_properties=JMS Properties
 jms_properties_name=Name
 jms_properties_title=JMS Properties
 jms_properties_type=Class of value
 jms_properties_value=Value
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Path of folder containing random files suffixed with .dat for bytes messages, .txt or .obj for text and Object messages
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title=JMS Default Request
 jms_selector=JMS Selector
 jms_send_queue=JNDI name Request queue
 jms_separator=Separator
 jms_stop_between_samples=Stop between samples?
 jms_store_response=Store Response
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use MessageConsumer.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_area=Text Message or Object Message serialized to XML by XStream
 jms_text_message=Text Message
 jms_timeout=Timeout (ms)
 jms_topic=Destination
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File from folder specified below
 jms_use_req_msgid_as_correlid=Use Request Message Id
 jms_use_res_msgid_as_correlid=Use Response Message Id
 jms_use_text=Textarea
 jms_user=User
 jndi_config_title=JNDI Configuration
 jndi_lookup_name=Remote Interface
 jndi_lookup_title=JNDI Lookup Configuration
 jndi_method_button_invoke=Invoke
 jndi_method_button_reflect=Reflect
 jndi_method_home_name=Home Method Name
 jndi_method_home_parms=Home Method Parameters
 jndi_method_name=Method Configuration
 jndi_method_remote_interface_list=Remote Interfaces
 jndi_method_remote_name=Remote Method Name
 jndi_method_remote_parms=Remote Method Parameters
 jndi_method_title=Remote Method Configuration
 jndi_testing_title=JNDI Request
 jndi_url_jndi_props=JNDI Properties
 jsonpath_renderer=JSON Path Tester
 jsonpath_tester_title=JSON Path Tester
 jsonpath_tester_field=JSON Path Expression
 jsonpath_tester_button_test=Test
 jsonpath_render_no_text=No Text
 json_post_processor_title=JSON Extractor
 jsonpp_variable_names=Variable names
 jsonpp_json_path_expressions=JSON Path expressions
 jsonpp_default_values=Default Values
 jsonpp_match_numbers=Match Numbers
 jsonpp_compute_concat=Compute concatenation var (suffix _ALL)
 jsonpp_error_number_arguments_mismatch_error=Mismatch between number of variables, json expressions and default values
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
 junit_create_instance_per_sample=Create a new instance per sample
 junit_do_setup_teardown=Do not call setUp and tearDown
 junit_error_code=Error Code
 junit_error_default_code=9999
 junit_error_default_msg=An unexpected error occured
 junit_error_msg=Error Message
 junit_failure_code=Failure Code
 junit_failure_default_code=0001
 junit_failure_default_msg=Test failed
 junit_failure_msg=Failure Message
 junit_junit4=Search for JUnit 4 annotations (instead of JUnit 3)
 junit_pkg_filter=Package Filter
 junit_request=JUnit Request
 junit_request_defaults=JUnit Request Defaults
 junit_success_code=Success Code
 junit_success_default_code=1000
 junit_success_default_msg=Test successful
 junit_success_msg=Success Message
 junit_test_config=JUnit Test Parameters
 junit_test_method=Test Method
-language_change_test_running=A Test is currently running, stop or shutdown test to change language
-language_change_title=Test Running
+action_check_message=A Test is currently running, stop or shutdown test to execute this command
+action_check_title=Test Running
 ldap_argument_list=LDAPArgument List
 ldap_connto=Connection timeout (in milliseconds)
 ldap_parse_results=Parse the search results ?
 ldap_sample_title=LDAP Request Defaults
 ldap_search_baseobject=Perform baseobject search
 ldap_search_onelevel=Perform onelevel search
 ldap_search_subtree=Perform subtree search
 ldap_secure=Use Secure LDAP Protocol ?
 ldap_testing_title=LDAP Request
 ldapext_sample_title=LDAP Extended Request Defaults
 ldapext_testing_title=LDAP Extended Request
 library=Library
 load=Load
 log_errors_only=Errors
 log_file=Location of log File
 log_function_comment=Additional comment (optional)
 log_function_level=Log level (default INFO) or OUT or ERR
 log_function_string=String to be logged
 log_function_string_ret=String to be logged (and returned)
 log_function_throwable=Throwable text (optional)
 log_only=Log/Display Only:
 log_parser=Name of Log Parser class
 log_parser_cnf_msg=Could not find the class. Please make sure you place your jar file in the /lib directory.
 log_parser_illegal_msg=Could not access the class due to IllegalAccessException.
 log_parser_instantiate_msg=Could not create an instance of the log parser. Please make sure the parser implements LogParser interface.
 log_sampler=Tomcat Access Log Sampler
 log_success_only=Successes
 logic_controller_title=Simple Controller
 login_config=Login Configuration
 login_config_element=Login Config Element
 longsum_param_1=First long to add
 longsum_param_2=Second long to add - further longs can be summed by adding further arguments.
 loop_controller_title=Loop Controller
 looping_control=Looping Control
 lower_bound=Lower Bound
 mail_reader_account=Username:
 mail_reader_all_messages=All
 mail_reader_delete=Delete messages from the server
 mail_reader_folder=Folder:
 mail_reader_header_only=Fetch headers only
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_port=Server Port (optional):
 mail_reader_server=Server Host:
 mail_reader_server_type=Protocol (e.g. pop3, imaps):
 mail_reader_storemime=Store the message using MIME (raw)
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_addressees=Addressee(s): 
 mailer_attributes_panel=Mailing attributes
 mailer_connection_security=Connection security: 
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_failure_limit=Failure Limit: 
 mailer_failure_subject=Failure Subject: 
 mailer_failures=Failures: 
 mailer_from=From: 
 mailer_host=Host: 
 mailer_login=Login: 
 mailer_msg_title_error=Error
 mailer_msg_title_information=Information
 mailer_password=Password: 
 mailer_port=Port: 
 mailer_string=E-Mail Notification
 mailer_success_limit=Success Limit: 
 mailer_success_subject=Success Subject: 
 mailer_test_mail=Test Mail
 mailer_title_message=Message
 mailer_title_settings=Mailer settings
 mailer_title_smtpserver=SMTP server
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\: 
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 mechanism=Mechanism
 menu_assertions=Assertions
 menu_close=Close
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_fragments=Test Fragment
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logger_panel=Log Viewer 
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_search=Search
 menu_search_reset=Reset Search
 menu_tables=Table
 menu_threads=Threads (Users)
 menu_timer=Timer
 menu_toolbar=Toolbar
 metadata=MetaData
 method=Method\:
 mimetype=Mimetype
 minimum_param=The minimum value allowed for a range of values
 minute=minute
 modddn=Old entry name
 modification_controller_title=Modification Controller
 modification_manager_title=Modification Manager
 modify_test=Modify Test
 modtest=Modification test
 module_controller_module_to_run=Module To Run 
 module_controller_title=Module Controller
 module_controller_warning=Could not find module: 
 monitor_equation_active=Active:  (busy/max) > 25%
 monitor_equation_dead=Dead:  no response
 monitor_equation_healthy=Healthy:  (busy/max) < 25%
 monitor_equation_load=Load:  ( (busy / max) * 50) + ( (used memory / max memory) * 50)
 monitor_equation_warning=Warning:  (busy/max) > 67%
 monitor_health_tab_title=Health
 monitor_health_title=Monitor Results (DEPRECATED)
 monitor_is_title=Use as Monitor (DEPRECATED)
 monitor_label_left_bottom=0 %
 monitor_label_left_middle=50 %
 monitor_label_left_top=100 %
 monitor_label_prefix=Connection Prefix
 monitor_label_right_active=Active
 monitor_label_right_dead=Dead
 monitor_label_right_healthy=Healthy
 monitor_label_right_warning=Warning
 monitor_legend_health=Health
 monitor_legend_load=Load
 monitor_legend_memory_per=Memory % (used/total)
 monitor_legend_thread_per=Thread % (busy/max)
 monitor_load_factor_mem=50
 monitor_load_factor_thread=50
 monitor_performance_servers=Servers
 monitor_performance_tab_title=Performance
 monitor_performance_title=Performance Graph
 name=Name\:
 new=New
 newdn=New distinguished name
 next=Next
 no=Norwegian
 notify_child_listeners_fr=Notify Child Listeners of filtered samplers
 number_of_threads=Number of Threads (users)\:
 obsolete_test_element=This test element is obsolete
 once_only_controller_title=Once Only Controller
 opcode=opCode
 open=Open...
 option=Options
 optional_tasks=Optional Tasks
 paramtable=Send Parameters With the Request\:
 password=Password
 paste=Paste
 paste_insert=Paste As Insert
 path=Path\:
 path_extension_choice=Path Extension (use ";" as separator)
 path_extension_dont_use_equals=Do not use equals in path extension (Intershop Enfinity compatibility)
 path_extension_dont_use_questionmark=Do not use questionmark in path extension (Intershop Enfinity compatibility)
 patterns_to_exclude=URL Patterns to Exclude
 patterns_to_include=URL Patterns to Include
 pkcs12_desc=PKCS 12 Key (*.p12)
 pl=Polish
 poisson_timer_delay=Constant Delay Offset (in milliseconds)\:
 poisson_timer_memo=Adds a random delay with a poisson distribution
 poisson_timer_range=Lambda (in milliseconds)\:
 poisson_timer_title=Poisson Random Timer
 port=Port\:
 post_as_parameters=Parameters
 post_body=Body Data
 post_body_raw=Body Data
 post_files_upload=Files Upload
 post_thread_group_title=tearDown Thread Group
 previous=Previous
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip=<html>{0}</html>
 property_undefined=Undefined
 property_value_param=Value of property
 property_visualiser_title=Property Display
 protocol=Protocol [http]\:
 protocol_java_border=Java class
 protocol_java_classname=Classname\:
 protocol_java_config_tile=Configure Java Sample
 protocol_java_test_title=Java Testing
 provider_url=Provider URL
 proxy_assertions=Add Assertions
 proxy_cl_error=If specifying a proxy server, host and port must be given
 proxy_cl_wrong_target_cl=Target Controller is configured to "Use Recording Controller" but no such controller exists, \nensure you add a Recording Controller as child of Thread Group node to start recording correctly
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_daemon_bind_error=Could not create script recorder - port in use. Choose another port.
 proxy_daemon_error=Could not create script recorder - see log for details
 proxy_daemon_error_from_clipboard=from clipboard
 proxy_daemon_error_not_retrieve=Could not add retrieve
 proxy_daemon_error_read_args=Could not add read arguments from clipboard\:
 proxy_daemon_msg_check_details=Please check the details below when installing the certificate in the browser
 proxy_daemon_msg_created_in_bin=created in JMeter bin directory
 proxy_daemon_msg_install_as_in_doc=You can install it following instructions in Component Reference documentation (see Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Root CA certificate\:
 proxy_domains=HTTPS Domains \:
 proxy_domains_dynamic_mode_tooltip=List of domain names for HTTPS url, ex. jmeter.apache.org or wildcard domain like *.apache.org. Use comma as separator. 
 proxy_domains_dynamic_mode_tooltip_java6=To activate this field, use a Java 7+ runtime environment
 proxy_general_settings=Global Settings
 proxy_headers=Capture HTTP Headers
 proxy_prefix_http_sampler_name=Prefix\:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_settings_port_error_digits=Only digits allowed
 proxy_settings_port_error_invalid_data=Invalid data
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP(S) Test Script Recorder
 pt_br=Portugese (Brazilian)
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 random_multi_result_source_variable=Source Variable(s) (use | as separator)
 random_multi_result_target_variable=Target Variable
 random_string_chars_to_use=Chars to use for random string generation
 random_string_length=Random string length
 realm=Realm
 record_controller_clear_samples=Clear all the recorded samples
 record_controller_title=Recording Controller
 redo=Redo
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_params_names_field=Parameter names regexp group number
 regex_params_ref_name_field=Regular Expression Reference Name
 regex_params_title=RegEx User Parameters
 regex_params_values_field=Parameter values regex group number
 regex_source=Field to check
 regex_src_body=Body
 regex_src_body_as_document=Body as a Document
 regex_src_body_unescaped=Body (unescaped)
 regex_src_hdrs=Response Headers
 regex_src_hdrs_req=Request Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search previous sample - or variable.
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 regexfunc_param_7=Input variable name containing the text to be parsed ([previous sample])
 regexp_render_no_text=Data response result isn't text.
 regexp_tester_button_test=Test
 regexp_tester_field=Regular expression\:
 regexp_tester_title=RegExp Tester
 remote_error_init=Error initialising remote server
 remote_error_starting=Error starting remote server
 remote_exit=Remote Exit
 remote_exit_all=Remote Exit All
 remote_shut=Remote Shutdown
 remote_shut_all=Remote Shutdown All
 remote_start=Remote Start
 remote_start_all=Remote Start All
 remote_stop=Remote Stop
 remote_stop_all=Remote Stop All
 remove=Remove
 remove_confirm_msg=Are you sure you want remove the selected element(s)?
 remove_confirm_title=Confirm remove?
 rename=Rename entry
 report=Report
 report_bar_chart=Bar Chart
 report_bar_graph_url=URL
 report_base_directory=Base Directory
 report_chart_caption=Chart Caption
 report_chart_x_axis=X Axis
 report_chart_x_axis_label=Label for X Axis
 report_chart_y_axis=Y Axis
 report_chart_y_axis_label=Label for Y Axis
 report_line_graph=Line Graph
 report_line_graph_urls=Include URLs
 report_output_directory=Output Directory for Report
 report_page=Report Page
 report_page_element=Page Element
 report_page_footer=Page Footer
 report_page_header=Page Header
 report_page_index=Create Page Index
 report_page_intro=Page Introduction
 report_page_style_url=Stylesheet url
 report_page_title=Page Title
 report_pie_chart=Pie Chart
 report_plan=Report Plan
 report_select=Select
 report_summary=Report Summary
 report_table=Report Table
 report_writer=Report Writer
 report_writer_html=HTML Report Writer
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Label
 reportgenerator_summary_apdex_satisfied=T (Toleration threshold)  
 reportgenerator_summary_apdex_tolerated=F (Frustration threshold)
 reportgenerator_summary_errors_count=Number of errors
 reportgenerator_summary_errors_rate_all=% in all samples
 reportgenerator_summary_errors_rate_error=% in errors
 reportgenerator_summary_errors_type=Type of error
 reportgenerator_summary_statistics_count=#Samples
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=Error %
 reportgenerator_summary_statistics_kbytes=Received KB/sec
 reportgenerator_summary_statistics_sent_kbytes=Sent KB/sec
 reportgenerator_summary_statistics_label=Label
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Average response time
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%dth pct
 reportgenerator_summary_statistics_throughput=Throughput
 reportgenerator_summary_total=Total
 request_data=Request Data
 reset=Reset
 reset_gui=Reset Gui
 response_save_as_md5=Save response as MD5 hash?
 response_time_distribution_satisfied_label=Requests having \\nresponse time <= {0}ms
 response_time_distribution_tolerated_label= Requests having \\nresponse time > {0}ms and <= {1}ms
 response_time_distribution_untolerated_label=Requests having \\nresponse time > {0}ms
 response_time_distribution_failed_label=Requests in error
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_addtimestamp=Add timestamp
 resultsaver_errors=Save Failed Responses only
 resultsaver_numberpadlen=Minimum Length of sequence number
 resultsaver_prefix=Filename prefix\:
 resultsaver_skipautonumber=Don't add number to prefix
 resultsaver_skipsuffix=Don't add suffix
 resultsaver_success=Save Successful Responses only
 resultsaver_title=Save Responses to a file
 resultsaver_variable=Variable Name:
 retobj=Return object
 return_code_config_box_title=Return Code Configuration
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
 root=Root
 root_title=Root
 run=Run
 run_threadgroup=Start
 run_threadgroup_no_timers=Start no pauses
 running_test=Running test
 runtime_controller_title=Runtime Controller
 runtime_seconds=Runtime (seconds)
 sample_result_save_configuration=Sample Result Save Configuration
 sample_scope=Apply to:
 sample_scope_all=Main sample and sub-samples
 sample_scope_children=Sub-samples only
 sample_scope_parent=Main sample only
 sample_scope_variable=JMeter Variable
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_start_next_loop=Start Next Thread Loop
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_test_now=Stop Test Now
 sampler_on_error_stop_thread=Stop Thread
 sample_timeout_memo=Interrupt the sampler if it times out
 sample_timeout_timeout=Sample timeout (in milliseconds)\:
 sample_timeout_title=Sample Timeout
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_as_test_fragment=Save as Test Fragment
 save_as_test_fragment_error=One of the selected nodes cannot be put inside a Test Fragment
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save received byte count
 save_code=Save Response Code
 save_datatype=Save Data Type
 save_encoding=Save Encoding
 save_fieldnames=Save Field Names (CSV)
 save_filename=Save Response Filename
 save_graphics=Save Graph
 save_hostname=Save Hostname
 save_idletime=Save Idle Time
 save_label=Save Label
 save_latency=Save Latency
 save_connecttime=Save Connect Time
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplecount=Save Sample and Error Counts
 save_samplerdata=Save Sampler Data (XML)
 save_sentbytes=Save sent byte count
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 save_workbench=Save WorkBench
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search=Search
 search_base=Search base
 search_expand=Search & Expand
 search_filter=Search Filter
 search_test=Search Test
 search_text_button_close=Close
 search_text_button_find=Find
 search_text_button_next=Find next
 search_text_chkbox_case=Case sensitive
 search_text_chkbox_regexp=Regular exp.
 search_text_field=Search: 
 search_text_msg_not_found=Text not found
 search_text_title_not_found=Not found
 search_tree_title=Search Tree
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send Files With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=File Path
 send_file_mime_label=MIME Type
 send_file_param_name_label=Parameter Name
 server=Server Name or IP\:
 servername=Servername \:
 session_argument_name=Session Argument Name
 setup_thread_group_title=setUp Thread Group
 should_save=You should save your test plan before running it.  \nIf you are using supporting data files (ie, for CSV Data Set or _StringFromFile), \nthen it is particularly important to first save your test script. \nDo you want to save your test plan first?
 shutdown=Shutdown
 simple_config_element=Simple Config Element
 simple_data_writer_title=Simple Data Writer
 size_assertion_comparator_error_equal=been equal to
 size_assertion_comparator_error_greater=been greater than
 size_assertion_comparator_error_greaterequal=been greater or equal to
 size_assertion_comparator_error_less=been less than
 size_assertion_comparator_error_lessequal=been less than or equal to
 size_assertion_comparator_error_notequal=not been equal to
 size_assertion_comparator_label=Type of Comparison
 size_assertion_failure=The result was the wrong size\: It was {0} bytes, but should have {1} {2} bytes.
 size_assertion_input_error=Please enter a valid positive integer.
 size_assertion_label=Size in bytes\:
 size_assertion_size_test=Size to Assert
 size_assertion_title=Size Assertion
 smime_assertion_issuer_dn=Issuer distinguished name
 smime_assertion_message_position=Execute assertion on message at position
 smime_assertion_not_signed=Message not signed
 smime_assertion_signature=Signature
 smime_assertion_signer=Signer certificate
 smime_assertion_signer_by_file=Certificate file
 smime_assertion_signer_constraints=Check values
 smime_assertion_signer_dn=Signer distinguished name
 smime_assertion_signer_email=Signer email address
 smime_assertion_signer_no_check=No check
 smime_assertion_signer_serial=Serial Number
 smime_assertion_title=SMIME Assertion
 smime_assertion_verify_signature=Verify signature
 smtp_additional_settings=Additional Settings
 smtp_attach_file=Attach file(s):
 smtp_attach_file_tooltip=Separate multiple files with ";"
 smtp_auth_settings=Auth settings
 smtp_bcc=Address To BCC:
 smtp_cc=Address To CC:
 smtp_default_port=(Defaults: SMTP:25, SSL:465, StartTLS:587)
 smtp_eml=Send .eml:
 smtp_enabledebug=Enable debug logging?
 smtp_enforcestarttls=Enforce StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Enforces</b> the server to use StartTLS.<br />If not selected and the SMTP-Server doesn't support StartTLS, <br />a normal SMTP-Connection will be used as fallback instead. <br /><i>Please note</i> that this checkbox creates a file in "/tmp/", <br />so this will cause problems under windows.</html>
 smtp_from=Address From:
 smtp_header_add=Add Header
 smtp_header_name=Header Name
 smtp_header_remove=Remove
 smtp_header_value=Header Value
 smtp_mail_settings=Mail settings
 smtp_message=Message:
 smtp_message_settings=Message settings
 smtp_messagesize=Calculate message size
 smtp_password=Password:
 smtp_plainbody=Send plain body (i.e. not multipart/mixed)
 smtp_replyto=Address Reply-To:
 smtp_sampler_title=SMTP Sampler
 smtp_security_settings=Security settings
 smtp_server=Server:
 smtp_server_connection_timeout=Connection timeout:
 smtp_server_port=Port:
 smtp_server_settings=Server settings
 smtp_server_timeout=Read timeout:
 smtp_server_timeouts_settings=Timeouts (milliseconds)
 smtp_subject=Subject:
 smtp_suppresssubj=Suppress Subject Header
 smtp_timestamp=Include timestamp in subject
 smtp_to=Address To:
 smtp_trustall=Trust all certificates
 smtp_trustall_tooltip=<html><b>Enforces</b> JMeter to trust all certificates, whatever CA it comes from.</html>
 smtp_truststore=Local truststore:
 smtp_truststore_tooltip=<html>The pathname of the truststore.<br />Relative paths are resolved against the current directory.<br />Failing that, against the directory containing the test script (JMX file)</html>
 smtp_useauth=Use Auth
 smtp_usenone=Use no security features
 smtp_username=Username:
 smtp_usessl=Use SSL
 smtp_usestarttls=Use StartTLS
 smtp_usetruststore=Use local truststore
 smtp_usetruststore_tooltip=<html>Allows JMeter to use a local truststore.</html>
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_file_invalid=Filename references a missing or unreadable file\:
 soap_sampler_title=SOAP/XML-RPC Request
 soap_send_action=Send SOAPAction: 
 solinger=SO_LINGER:
 spline_visualizer_average=Average
 spline_visualizer_incoming=Incoming
 spline_visualizer_maximum=Maximum
 spline_visualizer_minimum=Minimum
 spline_visualizer_title=Spline Visualizer (DEPRECATED)
 spline_visualizer_waitingmessage=Waiting for samples
 split_function_separator=String to split on. Default is , (comma).
 split_function_string=String to split
 ssl_alias_prompt=Please type your preferred alias
 ssl_alias_select=Select your alias for the test
 ssl_alias_title=Client Alias
 ssl_error_title=Key Store Problem
 ssl_pass_prompt=Please type your password
 ssl_pass_title=KeyStore Password
 ssl_port=SSL Port
 sslmanager=SSL Manager
 start=Start
 start_no_timers=Start no pauses
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads. You can see number of active threads in the upper right corner of GUI. Please be patient. 
 stopping_test_failed=One or more test threads won't exit; see log file.
 stopping_test_host=Host
 stopping_test_title=Stopping Test
 string_from_file_encoding=File encoding if not the platform default (opt)
 string_from_file_file_name=Enter path (absolute or relative) to file
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 system_sampler_stderr=Standard error (stderr):
 system_sampler_stdin=Standard input (stdin):
 system_sampler_stdout=Standard output (stdout):
 system_sampler_title=OS Process Sampler
 table_visualizer_bytes=Bytes
 table_visualizer_latency=Latency
 table_visualizer_connect=Connect Time(ms)
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
 table_visualizer_sent_bytes=Sent Bytes
 table_visualizer_start_time=Start Time
 table_visualizer_status=Status
 table_visualizer_success=Success
 table_visualizer_thread_name=Thread Name
 table_visualizer_warning=Warning
 target_server=Target Server
 tcp_classname=TCPClient classname\:
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 teardown_on_shutdown=Run tearDown Thread Groups after shutdown of main threads
 template_choose=Select Template
 template_create_from=Create
 template_field=Template\:
 template_load?=Load template ?
 template_menu=Templates...
 template_merge_from=Merge
 template_reload=Reload templates
 template_title=Templates
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
 test_action_pause=Pause
 test_action_restart_next_loop=Go to next loop iteration
 test_action_stop=Stop
 test_action_stop_now=Stop Now
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
 test_fragment_title=Test Fragment
 test_plan=Test Plan
 test_plan_classpath_browse=Add directory or jar to classpath
 testconfiguration=Test Configuration
 testplan.serialized=Run Thread Groups consecutively (i.e. run groups one at a time)
 testplan_comments=Comments\:
 testt=Test
 textbox_cancel=Cancel
 textbox_close=Close
 textbox_save_close=Save & Close
 textbox_title_edit=Edit text
 textbox_title_view=View text
 textbox_tooltip_cell=Double click to view/edit
 thread_delay_properties=Thread Delay Properties
 thread_group_title=Thread Group
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
 timeout_config_box_title=Timeout configuration
 timeout_title=Timeout (ms)
 toggle=Toggle
 toolbar_icon_set_not_found=The file description of toolbar icon set is not found. See logs.
 total_threads_tooltip=Total number of threads to run
 tr=Turkish
 transaction_controller_include_timers=Include duration of timer and pre-post processors in generated sample
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 transform_into_variable=Replace values with variables
 unbind=Thread Unbind
 undo=Undo
 unescape_html_string=String to unescape
 unescape_string=String containing Java escapes
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
 up=Up
 update=Update
 update_per_iter=Update Once Per Iteration
 upload=File Upload
 upper_bound=Upper Bound
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocol\:
 url_config_title=HTTP Request Defaults
 url_full_config_title=UrlFull Sample
 url_multipart_config_title=HTTP Multipart Request Defaults
 urldecode_string=String with URL encoded chars to decode
 urlencode_string=String to encode in URL encoded chars
 use_custom_dns_resolver=Use custom DNS resolver
 use_expires=Use Cache-Control/Expires header when processing GET requests
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for POST
 use_multipart_mode_browser=Browser-compatible headers
 use_recording_controller=Use Recording Controller
 use_system_dns_resolver=Use system DNS resolver
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 validate_threadgroup=Validate
 value=Value
 value_to_quote_meta=Value to escape from ORO Regexp meta chars
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_assertion_error=Assertion error: 
 view_results_assertion_failure=Assertion failure: 
 view_results_assertion_failure_message=Assertion failure message: 
 view_results_autoscroll=Scroll automatically?
 view_results_childsamples=Child samples?
 view_results_datatype=Data type ("text"|"bin"|""): 
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_connect_time=Connect Time: 
 view_results_load_time=Load time: 
 view_results_render=Render: 
 view_results_render_document=Document
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML (download resources)
 view_results_render_html_formatted=HTML Source Formatted
 view_results_render_json=JSON
 view_results_render_text=Text
 view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_missing_tika=Missing tika-app.jar in classpath. Unable to convert to plain text this kind of document.\nDownload the tika-app-x.x.jar file from http://tika.apache.org/download.html\nAnd put the file in <JMeter>/lib directory.
 view_results_response_partial_message=Start of message:
 view_results_response_too_large_message=Response too large to be displayed. Size: 
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
 view_results_search_pane=Search pane
 view_results_sent_bytes=Sent bytes:
 view_results_size_body_in_bytes=Body size in bytes: 
 view_results_size_headers_in_bytes=Headers size in bytes: 
 view_results_size_in_bytes=Size in bytes: 
 view_results_tab_assertion=Assertion result
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
 view_results_table_fields_key=Additional field
 view_results_table_fields_value=Value
 view_results_table_headers_key=Response header
 view_results_table_headers_value=Value
 view_results_table_request_headers_key=Request header
 view_results_table_request_headers_value=Value
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=Host
 view_results_table_request_http_method=Method
 view_results_table_request_http_nohttp=No HTTP Sample
 view_results_table_request_http_path=Path
 view_results_table_request_http_port=Port
 view_results_table_request_http_protocol=Protocol
 view_results_table_request_params_key=Parameter name
 view_results_table_request_params_value=Value
 view_results_table_request_raw_nodata=No data to display
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Raw
 view_results_table_result_tab_parsed=Parsed
 view_results_table_result_tab_raw=Raw
 view_results_thread_name=Thread Name: 
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_cannot_convert_parameters_to_raw=Cannot convert parameters to Body Data \nbecause one of the parameters has a name
 web_cannot_switch_tab=You cannot switch because data cannot be converted\n to target Tab data, empty data to switch
 web_parameters_lost_message=Switching to Body Data will convert the parameters.\nParameter table will be cleared when you select\nanother node or save the test plan.\nOK to proceeed?
 web_proxy_server_title=Proxy Server
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
 web_testing2_title=HTTP Request HTTPClient
 web_testing_basic=Basic
 web_testing_advanced=Advanced
 web_testing_concurrent_download=Parallel downloads. Number:
 web_testing_embedded_url_pattern=URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources
 web_testing_retrieve_title=Embedded Resources from HTML Files
 web_testing_source_ip=Source address
 web_testing_source_ip_device=Device
 web_testing_source_ip_device_ipv4=Device IPv4
 web_testing_source_ip_device_ipv6=Device IPv6
 web_testing_source_ip_hostname=IP/Hostname
 web_testing_title=HTTP Request
 while_controller_label=Condition (function or variable)
 while_controller_title=While Controller
 workbench_title=WorkBench
 xml_assertion_title=XML Assertion
 xml_download_dtds=Fetch external DTDs
 xml_namespace_button=Use Namespaces
 xml_tolerant_button=Use Tidy (tolerant parser)
 xml_validate_button=Validate XML
 xml_whitespace_button=Ignore Whitespace
 xmlschema_assertion_label=File Name:
 xmlschema_assertion_title=XML Schema Assertion
 xpath_assertion_button=Validate
 xpath_assertion_check=Check XPath Expression
 xpath_assertion_error=Error with XPath
 xpath_assertion_failed=Invalid XPath Expression
 xpath_assertion_label=XPath
 xpath_assertion_negate=True if nothing matches
 xpath_assertion_option=XML Parsing Options
 xpath_assertion_test=XPath Assertion 
 xpath_assertion_tidy=Try and tidy up the input
 xpath_assertion_title=XPath Assertion
 xpath_assertion_valid=Valid XPath Expression
 xpath_assertion_validation=Validate the XML against the DTD
 xpath_assertion_whitespace=Ignore whitespace
 xpath_expression=XPath expression to match against
 xpath_extractor_fragment=Return entire XPath fragment instead of text content?
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_file_file_name=XML file to get values from 
 xpath_tester=XPath Tester
 xpath_tester_button_test=Test
 xpath_tester_field=XPath expression
 xpath_tester_fragment=Return entire XPath fragment instead of text content?
 xpath_tester_no_text=Data response result isn't text.
 xpath_tester_title=XPath Tester
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
diff --git a/src/core/org/apache/jmeter/resources/messages_fr.properties b/src/core/org/apache/jmeter/resources/messages_fr.properties
index 3dbd2f10a..947e095ab 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,1354 +1,1354 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 #Stored by I18NEdit, may be edited!
 about=A propos de JMeter
 active_threads_tooltip=Unit\u00E9s actives
 add=Ajouter
 add_as_child=Ajouter en tant qu'enfant
 add_from_clipboard=Ajouter depuis Presse-papier
 add_from_suggested_excludes=Ajouter exclusions propos\u00E9es
 add_parameter=Ajouter un param\u00E8tre
 add_pattern=Ajouter un motif \:
 add_test=Ajout
 add_user=Ajouter un utilisateur
 add_value=Ajouter valeur
 addtest=Ajout
 aggregate_graph=Graphique des statistiques
 aggregate_graph_choose_color=Choisir couleur
 aggregate_graph_choose_foreground_color=Couleur valeur
 aggregate_graph_color_bar=Couleur \:
 aggregate_graph_column=Colonne
 aggregate_graph_column_selection=S\u00E9lection de colonnes par libell\u00E9 \:
 aggregate_graph_column_settings=Param\u00E8tres colonne
 aggregate_graph_columns_to_display=Colonnes \u00E0 afficher \:
 aggregate_graph_dimension=Taille graphique
 aggregate_graph_display=G\u00E9n\u00E9rer le graphique
 aggregate_graph_draw_outlines=Bordure de barre ?
 aggregate_graph_dynamic_size=Taille de graphique dynamique
 aggregate_graph_font=Police \:
 aggregate_graph_height=Hauteur \:
 aggregate_graph_increment_scale=Intervalle \u00E9chelle \:
 aggregate_graph_legend=L\u00E9gende
 aggregate_graph_legend.placement.bottom=Bas
 aggregate_graph_legend.placement.left=Gauche
 aggregate_graph_legend.placement.right=Droite
 aggregate_graph_legend.placement.top=Haut
 aggregate_graph_legend_placement=Position \:
 aggregate_graph_max_length_xaxis_label=Longueur maximum du libell\u00E9 de l'axe des abscisses \:
 aggregate_graph_ms=Millisecondes
 aggregate_graph_no_values_to_graph=Pas de valeurs pour le graphique
 aggregate_graph_number_grouping=S\u00E9parateur de milliers ?
 aggregate_graph_response_time=Temps de r\u00E9ponse
 aggregate_graph_save=Enregistrer le graphique
 aggregate_graph_save_table=Enregistrer le tableau de donn\u00E9es
 aggregate_graph_save_table_header=Inclure l'ent\u00EAte du tableau
 aggregate_graph_size=Taille \:
 aggregate_graph_style=Style \:
 aggregate_graph_sync_with_name=Synchroniser avec nom
 aggregate_graph_tab_graph=Graphique
 aggregate_graph_tab_settings=Param\u00E8tres
 aggregate_graph_title=Graphique agr\u00E9g\u00E9
 aggregate_graph_title_group=Titre
 aggregate_graph_use_group_name=Ajouter le nom du groupe aux libell\u00E9s
 aggregate_graph_user_title=Titre du graphique \:
 aggregate_graph_value_font=Police de la valeur \:
 aggregate_graph_value_labels_vertical=Libell\u00E9 de valeurs vertical ?
 aggregate_graph_width=Largeur \:
 aggregate_graph_xaxis_group=Abscisses
 aggregate_graph_yaxis_group=Ordonn\u00E9es (milli-secondes)
 aggregate_graph_yaxis_max_value=Echelle maximum \:
 aggregate_report=Rapport agr\u00E9g\u00E9
 aggregate_report_bandwidth=Ko/sec re\u00e7us
 aggregate_report_sent_bytes_per_sec=KB/sec \u00E9mis
 aggregate_report_count=\# Echantillons
 aggregate_report_error=Erreur
 aggregate_report_error%=% Erreur
 aggregate_report_max=Max
 aggregate_report_median=M\u00E9diane
 aggregate_report_min=Min
 aggregate_report_rate=D\u00E9bit
 aggregate_report_stddev=Ecart type
 aggregate_report_total_label=TOTAL
 aggregate_report_xx_pct1_line={0}% centile
 aggregate_report_xx_pct2_line={0}% centile
 aggregate_report_xx_pct3_line={0}% centile
 ajp_sampler_title=Requ\u00EAte AJP/1.3
 als_message=Note \: Le parseur de log d'acc\u00E8s est g\u00E9n\u00E9rique et vous permet de se brancher \u00E0 
 als_message2=votre propre parseur. Pour se faire, impl\u00E9menter le LogParser, ajouter le jar au 
 als_message3=r\u00E9pertoire /lib et entrer la classe (fichier .class) dans l'\u00E9chantillon (sampler).
 analyze=En train d'analyser le fichier de donn\u00E9es
 anchor_modifier_title=Analyseur de lien HTML
 appearance=Apparence
 argument_must_not_be_negative=L'argument ne peut pas \u00EAtre n\u00E9gatif \!
 arguments_panel_title=Param\u00E8tres de commande
 assertion_assume_success=Ignorer le statut
 assertion_body_resp=Corps de r\u00E9ponse
 assertion_code_resp=Code de r\u00E9ponse
 assertion_contains=Contient (exp. r\u00E9guli\u00E8re)
 assertion_equals=Est \u00E9gale \u00E0 (texte brut)
 assertion_headers=Ent\u00EAtes de r\u00E9ponse
 assertion_matches=Correspond \u00E0 (exp. r\u00E9guli\u00E8re)
 assertion_message_resp=Message de r\u00E9ponse
 assertion_network_size=R\u00E9ponse compl\u00E8te
 assertion_not=Inverser
 assertion_pattern_match_rules=Type de correspondance du motif
 assertion_patterns_to_test=Motifs \u00E0 tester
 assertion_regex_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 assertion_resp_field=Section de r\u00E9ponse \u00E0 tester
 assertion_resp_size_field=Taille \u00E0 v\u00E9rifier sur
 assertion_substring=Contient (texte brut)
 assertion_text_document=Document (texte)
 assertion_text_resp=Texte de r\u00E9ponse
 assertion_textarea_label=Assertions \:
 assertion_title=Assertion R\u00E9ponse
 assertion_url_samp=URL Echantillon
 assertion_visualizer_title=R\u00E9cepteur d'assertions
 attribute=Attribut \:
 attribute_field=Attribut \:
 attrs=Attributs
 auth_base_url=URL de base
 auth_manager_clear_per_iter=R\u00E9authentifier \u00E0 chaque it\u00E9ration ?
 auth_manager_options=Options
 auth_manager_title=Gestionnaire d'autorisation HTTP
 auths_stored=Autorisations stock\u00E9es
 average=Moyenne
 average_bytes=Moy. octets
 backend_listener=R\u00E9cepteur asynchrone
 backend_listener_classname=Impl\u00E9mentation du r\u00E9cepteur asynchrone
 backend_listener_paramtable=Param\u00E8tres
 backend_listener_queue_size=Taille de la queue
 bind=Connexion de l'unit\u00E9
 bouncy_castle_unavailable_message=Les jars de bouncycastle sont indisponibles, ajoutez les au classpath.
 browse=Parcourir...
 bsf_sampler_title=Echantillon BSF
 bsf_script=Script \u00E0 lancer (variables\: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Fichier script \u00E0 lancer \:
 bsf_script_language=Langage de script \:
 bsf_script_parameters=Param\u00E8tres \u00E0 passer au script/fichier \:
 bsh_assertion_script=Script (IO\: Failure[Message], Response. IN\: Response[Data|Code|Message|Headers], RequestHeaders, Sample[Label|rData])
 bsh_assertion_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nEn lecture/\u00E9criture \: Failure, FailureMessage, SampleResult, vars, props, log.\nEn lecture seule \: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=Assertion BeanShell
 bsh_function_expression=Expression \u00E0 \u00E9valuer
 bsh_sampler_title=Echantillon BeanShell
 bsh_script=Script (voir ci-dessous pour les variables qui sont d\u00E9finies)
 bsh_script_file=Fichier script \:
 bsh_script_parameters=Param\u00E8tres  (-> String Parameters et String []bsh.args)
 bsh_script_reset_interpreter=R\u00E9initialiser l'interpr\u00E9teur bsh avant chaque appel
 bsh_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Je suis occup\u00E9 \u00E0 tester, veuillez arr\u00EAter le test avant de changer le param\u00E8trage
 cache_manager_size=Nombre maximum d'\u00E9l\u00E9ments dans le cache
 cache_manager_title=Gestionnaire de cache HTTP
 cache_session_id=Identifiant de session de cache ?
 cancel=Annuler
 cancel_exit_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de sortir ?
 cancel_new_from_template=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de charger le mod\u00E8le ?
 cancel_new_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de nettoyer le plan de test ?
 cancel_revert_project=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Annuler les changements et revenir \u00E0 la derni\u00E8re sauvegarde du plan de test ?
 change_parent=Changer le contr\u00F4leur
 char_value=Caract\u00E8re num\u00E9rique Unicode (d\u00E9cimal or 0xhex)
 check_return_code_title=V\u00E9rifier le code retour
 choose_function=Choisir une fonction
 choose_language=Choisir une langue
 clear=Nettoyer
 clear_all=Nettoyer tout
 clear_cache_each_iteration=Vider le cache \u00E0 chaque it\u00E9ration ?
 clear_cache_per_iter=Nettoyer le cache \u00E0 chaque it\u00E9ration ?
 clear_cookies_per_iter=Nettoyer les cookies \u00E0 chaque it\u00E9ration ?
 clipboard_node_read_error=Une erreur est survenue lors de la copie du noeud
 close=Fermer
 closeconnection=Fermer la connexion
 collapse_tooltip=Cliquer pour ouvrir / r\u00E9duire
 column_delete_disallowed=Supprimer cette colonne n'est pas possible
 column_number=Num\u00E9ro de colonne du fichier CSV | next | *alias
 command_config_box_title=Commande \u00E0 ex\u00E9cuter
 command_config_std_streams_title=Flux standard (fichiers)
 command_field_title=Commande \:
 compare=Comparaison
 comparefilt=Filtre de comparaison
 comparison_differ_content=Le contenu des r\u00E9ponses est diff\u00E9rent.
 comparison_differ_time=La diff\u00E9rence du temps de r\u00E9ponse diff\u00E8re de plus de 
 comparison_invalid_node=Noeud invalide 
 comparison_regex_string=Expression r\u00E9guli\u00E8re
 comparison_regex_substitution=Substitution
 comparison_response_time=Temps de r\u00E9ponse \: 
 comparison_unit=ms
 comparison_visualizer_title=R\u00E9cepteur d'assertions de comparaison
 config_element=El\u00E9ment de configuration
 config_save_settings=Configurer
 confirm=Confirmer
 constant_throughput_timer_memo=Ajouter un d\u00E9lai entre les \u00E9chantillions pour obtenir un d\u00E9bit constant
 constant_timer_delay=D\u00E9lai d'attente (en millisecondes) \:
 constant_timer_memo=Ajouter un d\u00E9lai fixe entre les \u00E9chantillions de test
 constant_timer_title=Compteur de temps fixe
 content_encoding=Encodage contenu \:
 controller=Contr\u00F4leur
 cookie_implementation_choose=Impl\u00E9mentation \:
 cookie_manager_policy=Politique des cookies \:
 cookie_manager_title=Gestionnaire de cookies HTTP
 cookie_options=Options
 cookies_stored=Cookies stock\u00E9s
 copy=Copier
 counter_config_title=Compteur
 counter_per_user=Suivre le compteur ind\u00E9pendamment pour chaque unit\u00E9 de test
 counter_reset_per_tg_iteration=R\u00E9initialiser le compteur \u00E0 chaque it\u00E9ration du groupe d'unit\u00E9s
 countlim=Limiter le nombre d'\u00E9l\u00E9ments retourn\u00E9s \u00E0
 critical_section_controller_label=Nom du verrou
 critical_section_controller_title=Contr\u00F4leur Section critique
 cssjquery_attribute=Attribut
 cssjquery_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 cssjquery_impl=Impl\u00E9mentation CSS/JQuery\:
 cssjquery_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 cssjquery_tester_button_test=Tester
 cssjquery_tester_error=Une erreur s''est produite lors de l''\u00E9valuation de l''expression\:{0}, erreur\:{1}
 cssjquery_tester_field=S\u00E9lecteur\:
 cssjquery_tester_title=Testeur CSS/JQuery
 csvread_file_file_name=Fichier CSV pour obtenir les valeurs de | *alias
 cut=Couper
 cut_paste_function=Fonction de copier/coller de cha\u00EEne de caract\u00E8re
 database_conn_pool_max_usage=Utilisation max pour chaque connexion\:
 database_conn_pool_props=Pool de connexions \u221A\u2020 la base de donn\u221A\u00A9es
 database_conn_pool_size=Nombre de Connexions dans le Pool\:
 database_conn_pool_title=Valeurs par d\u00E9faut du Pool de connexions JDBC
 database_driver_class=Classe du Driver\:
 database_login_title=Valeurs par d\u00E9faut de la base de donn\u221A\u00A9es JDBC
 database_sql_query_string=Requ\u00EAte SQL \:
 database_sql_query_title=Requ\u00EAte SQL JDBC par d\u00E9faut
 database_testing_title=Requ\u221A\u2122te JDBC
 database_url=URL JDBC\:
 database_url_jdbc_props=URL et driver JDBC de la base de donn\u221A\u00A9es
 ddn=DN \:
 de=Allemand
 debug_off=D\u00E9sactiver le d\u00E9bogage
 debug_on=Activer le d\u00E9bogage
 default_parameters=Param\u00E8tres par d\u00E9faut
 default_value_field=Valeur par d\u00E9faut \:
 delay=D\u00E9lai avant d\u00E9marrage (secondes) \:
 delayed_start=Cr\u00E9er les unit\u00E9s seulement quand n\u00E9cessaire
 delete=Supprimer
 delete_parameter=Supprimer le param\u00E8tre
 delete_test=Suppression
 delete_user=Supprimer l'utilisateur
 deltest=Suppression
 deref=D\u00E9r\u00E9f\u00E9rencement des alias
 description=Description
 detail=D\u00E9tail
 directory_field_title=R\u00E9pertoire d'ex\u00E9cution \:
 disable=D\u00E9sactiver
 distribution_graph_title=Graphique de distribution (DEPRECATED)
 distribution_note1=Ce graphique se mettra \u00E0 jour tous les 10 \u00E9chantillons
 dn=Racine DN \:
 dns_cache_manager_title=Gestionnaire de cache DNS
 dns_hostname_or_ip=Nom de machine ou adresse IP
 dns_servers=Serveurs DNS
 domain=Domaine \:
 done=Fait
 down=Descendre
 duplicate=Dupliquer
 duration=Dur\u00E9e (secondes) \:
 duration_assertion_duration_test=Dur\u00E9e maximale \u00E0 v\u00E9rifier
 duration_assertion_failure=L''op\u00E9ration a dur\u00E9e trop longtemps\: cela a pris {0} millisecondes, mais n''aurait pas d\u00FB durer plus de {1} millisecondes.
 duration_assertion_input_error=Veuillez entrer un entier positif valide.
 duration_assertion_label=Dur\u00E9e en millisecondes \:
 duration_assertion_title=Assertion Dur\u00E9e
 duration_tooltip=Temps pass\u00E9 depuis le d\u00E9but du test en cours
 edit=Editer
 email_results_title=R\u00E9sultat d'email
 en=Anglais
 enable=Activer
 encode=URL Encoder
 encode?=Encodage
 encoded_value=Valeur de l'URL encod\u00E9e
 endtime=Date et heure de fin \:
 entry_dn=Entr\u00E9e DN \:
 entrydn=Entr\u00E9e DN
 environment_panel_title=Variables d'environnement
 eolbyte=Valeur byte de l'indicateur de fin de ligne (EOL)\: 
 error_indicator_tooltip=Affiche le nombre d'erreurs dans le journal(log), cliquer pour afficher la console.
 error_loading_help=Erreur au chargement de la page d'aide
 error_occurred=Une erreur est survenue
 error_title=Erreur
 es=Espagnol
 escape_html_string=Cha\u00EEne d'\u00E9chappement
 eval_name_param=Variable contenant du texte et r\u00E9f\u00E9rences de fonctions
 evalvar_name_param=Nom de variable
 example_data=Exemple de donn\u00E9e
 example_title=Echantillon exemple
 exit=Quitter
 expected_return_code_title=Code retour attendu \: 
 expiration=Expiration
 expression_field=Expression CSS/JQuery \:
 field_name=Nom du champ
 file=Fichier
 file_already_in_use=Ce fichier est d\u00E9j\u00E0 utilis\u00E9
 file_visualizer_append=Concat\u00E9ner au fichier de donn\u00E9es existant
 file_visualizer_auto_flush=Vider automatiquement apr\u00E8s chaque echantillon de donn\u00E9es
 file_visualizer_browse=Parcourir...
 file_visualizer_close=Fermer
 file_visualizer_file_options=Options de fichier
 file_visualizer_filename=Nom du fichier \: 
 file_visualizer_flush=Vider
 file_visualizer_missing_filename=Aucun fichier de sortie sp\u00E9cifi\u00E9.
 file_visualizer_open=Ouvrir...
 file_visualizer_output_file=\u00C9crire les r\u00E9sultats dans un fichier ou lire les r\u00E9sultats depuis un fichier CSV / JTL
 file_visualizer_submit_data=Inclure les donn\u00E9es envoy\u00E9es
 file_visualizer_title=Rapporteur de fichier
 file_visualizer_verbose=Sortie verbeuse
 filename=Nom de fichier \: 
 find_target_element=Trouver l'\u00E9l\u00E9ment cible
 follow_redirects=Suivre les redirect.
 follow_redirects_auto=Rediriger automat.
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Gras
 fontstyle.italic=Italique
 fontstyle.normal=Normal
 foreach_controller_title=Contr\u00F4leur Pour chaque (ForEach)
 foreach_end_index=Indice de fin de la boucle (inclus)
 foreach_input=Pr\u00E9fixe de la variable d'entr\u00E9e \:
 foreach_output=Nom de la variable de sortie \:
 foreach_start_index=Indice de d\u00E9but de la boucle(exclus)
 foreach_use_separator=Ajouter un soulign\u00E9 "_" avant le nombre ?
 format=Format du nombre \:
 fr=Fran\u00E7ais
 ftp_binary_mode=Utiliser le mode binaire ?
 ftp_get=R\u00E9cup\u00E9rer (get)
 ftp_local_file=Fichier local \:
 ftp_local_file_contents=Contenus fichier local \:
 ftp_put=D\u00E9poser (put)
 ftp_remote_file=Fichier distant \:
 ftp_sample_title=Param\u00E8tres FTP par d\u00E9faut
 ftp_save_response_data=Enregistrer le fichier dans la r\u00E9ponse ?
 ftp_testing_title=Requ\u00EAte FTP
 function_dialog_menu_item=Assistant de fonctions
 function_helper_title=Assistant de fonctions
 function_name_param=Nom de la fonction. Utilis\u00E9 pour stocker les valeurs \u00E0 utiliser ailleurs dans la plan de test
 function_name_paropt=Nom de variable dans laquelle le r\u00E9sultat sera stock\u00E9 (optionnel)
 function_params=Param\u00E8tres de la fonction
 functional_mode=Mode de test fonctionnel
 functional_mode_explanation=S\u00E9lectionner le mode de test fonctionnel uniquement si vous avez besoin\nd'enregistrer les donn\u00E9es re\u00E7ues du serveur dans un fichier \u00E0 chaque requ\u00EAte. \n\nS\u00E9lectionner cette option affecte consid\u00E9rablement les performances.
 gaussian_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 gaussian (en millisecondes) \:
 gaussian_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution gaussienne
 gaussian_timer_range=D\u00E9viation (en millisecondes) \:
 gaussian_timer_title=Compteur de temps al\u00E9atoire gaussien
 generate=G\u00E9n\u00E9rer
 generator=Nom de la classe g\u00E9n\u00E9ratrice
 generator_cnf_msg=N'a pas p\u00FB trouver la classe g\u00E9n\u00E9ratrice. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 generator_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classes g\u00E9n\u00E9ratrice \u00E0 cause d'une IllegalAccessException.
 generator_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur g\u00E9n\u00E9rateur. Assurez-vous que le g\u00E9n\u00E9rateur impl\u00E9mente l'interface Generator.
 graph_apply_filter=Appliquer le filtre
 graph_choose_graphs=Graphique \u00E0 afficher
 graph_full_results_title=Graphique de r\u00E9sultats complets
 graph_pointshape_circle=Cercle
 graph_pointshape_diamond=Diamant
 graph_pointshape_none=Aucun
 graph_pointshape_square=Carr\u00E9
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms) \:
 graph_resp_time_interval_reload=Appliquer l'interval
 graph_resp_time_not_enough_data=Impossible de dessiner le graphique, pas assez de donn\u00E9es
 graph_resp_time_series_selection=S\u00E9lection des \u00E9chantillons par libell\u00E9 \:
 graph_resp_time_settings_line=Param\u00E9tres de la courbe
 graph_resp_time_settings_pane=Param\u00E9tres du graphique
 graph_resp_time_shape_label=Forme de la jonction \:
 graph_resp_time_stroke_width=Largeur de ligne \:
 graph_resp_time_title=Graphique \u00E9volution temps de r\u00E9ponses
 graph_resp_time_title_label=Titre du graphique \:  
 graph_resp_time_xaxis_time_format=Formatage heure (SimpleDateFormat) \:
 graph_results_average=Moyenne
 graph_results_data=Donn\u00E9es
 graph_results_deviation=Ecart type
 graph_results_latest_sample=Dernier \u00E9chantillon
 graph_results_median=M\u00E9diane
 graph_results_ms=ms
 graph_results_no_samples=Nombre d'\u00E9chantillons
 graph_results_throughput=D\u00E9bit
 graph_results_title=Graphique de r\u00E9sultats
 groovy_function_expression=Expression \u00E0 \u00E9valuer
 grouping_add_separators=Ajouter des s\u00E9parateurs entre les groupes
 grouping_in_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur
 grouping_in_transaction_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur de transaction
 grouping_mode=Grouper \:
 grouping_no_groups=Ne pas grouper les \u00E9chantillons
 grouping_store_first_only=Stocker le 1er \u00E9chantillon pour chaque groupe uniquement
 header_manager_title=Gestionnaire d'ent\u00EAtes HTTP
 headers_stored=Ent\u00EAtes stock\u00E9es
 heap_dump=Cr\u00E9er une image disque de la m\u00E9moire (heap dump)
 help=Aide
 help_node=Quel est ce noeud ?
 html_assertion_file=Ecrire un rapport JTidy dans un fichier
 html_assertion_label=Assertion HTML
 html_assertion_title=Assertion HTML
 html_extractor_title=Extracteur CSS/JQuery
 html_extractor_type=Impl\u00E9mentation de l'extracteur CSS/JQuery
 http_implementation=Impl\u00E9mentation \:
 http_response_code=Code de r\u00E9ponse HTTP
 http_url_rewriting_modifier_title=Transcripteur d'URL HTTP
 http_user_parameter_modifier=Modificateur de param\u00E8tre utilisateur HTTP
 httpmirror_max_pool_size=Taille maximum du pool d'unit\u00E9s \:
 httpmirror_max_queue_size=Taille maximum de la file d'attente \:
 httpmirror_settings=Param\u00E8tres
 httpmirror_title=Serveur HTTP miroir
 id_prefix=Pr\u00E9fixe d'ID
 id_suffix=Suffixe d'ID
 if_controller_evaluate_all=Evaluer pour tous les fils ?
 if_controller_expression=Interpr\u00E9ter la condition comme une expression
 if_controller_label=Condition (d\u00E9faut Javascript) \:
 if_controller_title=Contr\u00F4leur Si (If)
 ignore_subcontrollers=Ignorer les sous-blocs de contr\u00F4leurs
 include_controller=Contr\u00F4leur Inclusion
 include_equals=Inclure \u00E9gale ?
 include_path=Plan de test \u00E0 inclure
 increment=Incr\u00E9ment \:
 infinite=Infini
 initial_context_factory=Fabrique de contexte initiale
 insert_after=Ins\u00E9rer apr\u00E8s
 insert_before=Ins\u00E9rer avant
 insert_parent=Ins\u00E9rer en tant que parent
 interleave_control_title=Contr\u00F4leur Interleave
 interleave_accross_threads=Alterne en prenant en compte toutes les unit\u00E9s
 intsum_param_1=Premier entier \u00E0 ajouter
 intsum_param_2=Deuxi\u00E8me entier \u00E0 ajouter - les entier(s) suivants peuvent \u00EAtre ajout\u00E9(s) avec les arguments suivants.
 invalid_data=Donn\u00E9e invalide
 invalid_mail=Une erreur est survenue lors de l'envoi de l'email
 invalid_mail_address=Une ou plusieurs adresse(s) invalide(s) ont \u00E9t\u00E9 d\u00E9tect\u00E9e(s)
 invalid_mail_server=Le serveur de mail est inconnu (voir le fichier de journalisation JMeter)
 invalid_variables=Variables invalides
 iteration_counter_arg_1=TRUE, pour que chaque utilisateur ait son propre compteur, FALSE pour un compteur global
 iterator_num=Nombre d'it\u00E9rations \:
 ja=Japonais
 jar_file=Fichiers .jar
 java_request=Requ\u00EAte Java
 java_request_defaults=Requ\u00EAte Java par d\u00E9faut
 javascript_expression=Expression JavaScript \u00E0 \u00E9valuer
 jexl_expression=Expression JEXL \u00E0 \u00E9valuer
 jms_auth_required=Obligatoire
 jms_bytes_message=Message binaire
 jms_client_caption=Le client r\u00E9cepteur utilise MessageConsumer.receive () pour \u00E9couter les messages.
 jms_client_caption2=MessageListener utilise l'interface onMessage(Message) pour \u00E9couter les nouveaux messages.
 jms_client_id=ID du Client
 jms_client_type=Client
 jms_communication_style=Type de communication \: 
 jms_concrete_connection_factory=Fabrique de connexion 
 jms_config=Source du message \:
 jms_config_title=Configuration JMS
 jms_connection_factory=Fabrique de connexion
 jms_correlation_title=Champs alternatifs pour la correspondance de message
 jms_dest_setup=Evaluer
 jms_dest_setup_dynamic=A chaque \u00E9chantillon
 jms_dest_setup_static=Au d\u00E9marrage
 jms_durable_subscription_id=ID d'abonnement durable
 jms_expiration=Expiration (ms)
 jms_file=Fichier
 jms_initial_context_factory=Fabrique de connexion initiale
 jms_itertions=Nombre d'\u00E9chantillons \u00E0 agr\u00E9ger
 jms_jndi_defaults_title=Configuration JNDI par d\u00E9faut
 jms_jndi_props=Propri\u00E9t\u00E9s JNDI
 jms_map_message=Message Map
 jms_message_title=Propri\u00E9t\u00E9s du message
 jms_message_type=Type de message \: 
 jms_msg_content=Contenu
 jms_object_message=Message Object
 jms_point_to_point=Requ\u00EAte JMS Point-\u00E0-point
 jms_priority=Priorit\u00E9 (0-9)
 jms_properties=Propri\u00E9t\u00E9s JMS
 jms_properties_name=Nom
 jms_properties_title=Propri\u00E9t\u00E9s JMS
 jms_properties_type=Classe de la Valeur
 jms_properties_value=Valeur
 jms_props=Propri\u00E9t\u00E9s JMS
 jms_provider_url=URL du fournisseur
 jms_publisher=Requ\u00EAte JMS Publication
 jms_pwd=Mot de passe
 jms_queue=File
 jms_queue_connection_factory=Fabrique QueueConnection
 jms_queueing=Ressources JMS
 jms_random_file=Dossier contenant des fichiers al\u00E9atoires (suffix\u00E9s par .dat pour un message binaire, .txt ou .obj pour un message texte ou un objet)
 jms_receive_queue=Nom JNDI de la file d'attente Receive 
 jms_request=Requ\u00EAte seule
 jms_requestreply=Requ\u00EAte R\u00E9ponse
 jms_sample_title=Requ\u00EAte JMS par d\u00E9faut
 jms_selector=S\u00E9lecteur JMS
 jms_send_queue=Nom JNDI de la file d'attente Request
 jms_separator=S\u00E9parateur
 jms_stop_between_samples=Arr\u00EAter entre les \u00E9chantillons ?
 jms_store_response=Stocker la r\u00E9ponse
 jms_subscriber_on_message=Utiliser MessageListener.onMessage()
 jms_subscriber_receive=Utiliser MessageConsumer.receive()
 jms_subscriber_title=Requ\u00EAte JMS Abonnement
 jms_testing_title=Messagerie Request
 jms_text_area=Message texte ou Message Objet s\u00E9rialis\u00E9 en XML par XStream
 jms_text_message=Message texte
 jms_timeout=D\u00E9lai (ms)
 jms_topic=Destination
 jms_use_auth=Utiliser l'authentification ?
 jms_use_file=Depuis un fichier
 jms_use_non_persistent_delivery=Utiliser un mode de livraison non persistant ?
 jms_use_properties_file=Utiliser le fichier jndi.properties
 jms_use_random_file=Fichier al\u00E9atoire
 jms_use_req_msgid_as_correlid=Utiliser l'ID du message Request
 jms_use_res_msgid_as_correlid=Utiliser l'ID du message Response
 jms_use_text=Zone de texte (ci-dessous)
 jms_user=Utilisateur
 jndi_config_title=Configuration JNDI
 jndi_lookup_name=Interface remote
 jndi_lookup_title=Configuration Lookup JNDI 
 jndi_method_button_invoke=Invoquer
 jndi_method_button_reflect=R\u00E9flection
 jndi_method_home_name=Nom de la m\u00E9thode home
 jndi_method_home_parms=Param\u00E8tres de la m\u00E9thode home
 jndi_method_name=Configuration m\u00E9thode
 jndi_method_remote_interface_list=Interfaces remote
 jndi_method_remote_name=Nom m\u00E9thodes remote
 jndi_method_remote_parms=Param\u00E8tres m\u00E9thode remote
 jndi_method_title=Configuration m\u00E9thode remote
 jndi_testing_title=Requ\u00EAte JNDI
 jndi_url_jndi_props=Propri\u00E9t\u00E9s JNDI
 json_post_processor_title=Extracteur JSON
 jsonpath_render_no_text=Pas de Texte
 jsonpath_renderer=Testeur JSON Path
 jsonpath_tester_button_test=Tester
 jsonpath_tester_field=Expression JSON Path
 jsonpath_tester_title=Testeur JSON Path
 jsonpp_compute_concat=Calculer la variable de concat\u00E9nation (suffix _ALL)
 jsonpp_default_values=Valeure par d\u00E9fault
 jsonpp_error_number_arguments_mismatch_error=D\u00E9calage entre nombre de variables, expressions et valeurs par d\u00E9faut
 jsonpp_json_path_expressions=Expressions JSON Path
 jsonpp_match_numbers=Nombre de correspondances
 jsonpp_variable_names=Noms des variables
 junit_append_error=Concat\u00E9ner les erreurs d'assertion
 junit_append_exception=Concat\u00E9ner les exceptions d'ex\u00E9cution
 junit_constructor_error=Impossible de cr\u00E9er une instance de la classe
 junit_constructor_string=Libell\u00E9 de cha\u00EEne Constructeur
 junit_create_instance_per_sample=Cr\u00E9er une nouvelle instance pour chaque \u00E9chantillon
 junit_do_setup_teardown=Ne pas appeler setUp et tearDown
 junit_error_code=Code d'erreur
 junit_error_default_msg=Une erreur inattendue est survenue
 junit_error_msg=Message d'erreur
 junit_failure_code=Code d'\u00E9chec
 junit_failure_default_msg=Test \u00E9chou\u00E9
 junit_failure_msg=Message d'\u00E9chec
 junit_junit4=Rechercher les annotations JUnit 4 (au lieu de JUnit 3)
 junit_pkg_filter=Filtre de paquets
 junit_request=Requ\u00EAte JUnit
 junit_request_defaults=Requ\u00EAte par d\u00E9faut JUnit
 junit_success_code=Code de succ\u00E8s
 junit_success_default_msg=Test r\u00E9ussi
 junit_success_msg=Message de succ\u00E8s
 junit_test_config=Param\u00E8tres Test JUnit
 junit_test_method=M\u00E9thode de test
-language_change_test_running=Un test est en cours, arr\u00EAtez le avant de changer la langue
-language_change_title=Test en cours
+action_check_message=Un test est en cours, arr\u00EAtez le avant d''utiliser cette commande
+action_check_title=Test en cours
 ldap_argument_list=Liste d'arguments LDAP
 ldap_connto=D\u00E9lai d'attente de connexion (millisecondes)
 ldap_parse_results=Examiner les r\u00E9sultats de recherche ?
 ldap_sample_title=Requ\u00EAte LDAP par d\u00E9faut
 ldap_search_baseobject=Effectuer une recherche 'baseobject'
 ldap_search_onelevel=Effectuer une recherche 'onelevel'
 ldap_search_subtree=Effectuer une recherche 'subtree'
 ldap_secure=Utiliser le protocole LDAP s\u00E9curis\u00E9 (ldaps) ?
 ldap_testing_title=Requ\u00EAte LDAP
 ldapext_sample_title=Requ\u00EAte LDAP \u00E9tendue par d\u00E9faut
 ldapext_testing_title=Requ\u00EAte LDAP \u00E9tendue
 library=Librairie
 load=Charger
 log_errors_only=Erreurs
 log_file=Emplacement du fichier de journal (log)
 log_function_comment=Commentaire (facultatif)
 log_function_level=Niveau de journalisation (INFO par d\u00E9faut), OUT ou ERR
 log_function_string=Cha\u00EEne \u00E0 tracer
 log_function_string_ret=Cha\u00EEne \u00E0 tracer (et \u00E0 retourner)
 log_function_throwable=Texte de l'exception Throwable (optionnel)
 log_only=Uniquement \:
 log_parser=Nom de la classe de parseur des journaux (log)
 log_parser_cnf_msg=N'a pas p\u00FB trouver cette classe. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 log_parser_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classe \u00E0 cause d'une exception IllegalAccessException.
 log_parser_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur de log. Assurez-vous que le parseur impl\u00E9mente l'interface LogParser.
 log_sampler=Echantillon Journaux d'acc\u00E8s Tomcat
 log_success_only=Succ\u00E8s
 logic_controller_title=Contr\u00F4leur Simple
 login_config=Configuration Identification
 login_config_element=Configuration Identification 
 longsum_param_1=Premier long \u221A\u2020 ajouter
 longsum_param_2=Second long \u221A\u2020 ajouter - les autres longs pourront \u221A\u2122tre cumul\u221A\u00A9s en ajoutant d'autres arguments.
 loop_controller_title=Contr\u00F4leur Boucle
 looping_control=Contr\u00F4le de boucle
 lower_bound=Borne Inf\u00E9rieure
 mail_reader_account=Nom utilisateur \:
 mail_reader_all_messages=Tous
 mail_reader_delete=Supprimer les messages du serveur
 mail_reader_folder=Dossier \:
 mail_reader_header_only=R\u00E9cup\u00E9rer seulement les ent\u00EAtes
 mail_reader_num_messages=Nombre de message \u00E0 r\u00E9cup\u00E9rer \:
 mail_reader_password=Mot de passe \:
 mail_reader_port=Port (optionnel) \:
 mail_reader_server=Serveur \:
 mail_reader_server_type=Protocole (ex. pop3, imaps) \:
 mail_reader_storemime=Stocker le message en utilisant MIME (brut)
 mail_reader_title=Echantillon Lecteur d'email
 mail_sent=Email envoy\u00E9 avec succ\u00E8s
 mailer_addressees=Destinataire(s) \: 
 mailer_attributes_panel=Attributs de courrier
 mailer_connection_security=S\u00E9curit\u00E9 connexion \: 
 mailer_error=N'a pas p\u00FB envoyer l'email. Veuillez corriger les erreurs de saisie.
 mailer_failure_limit=Limite d'\u00E9chec \: 
 mailer_failure_subject=Sujet Echec \: 
 mailer_failures=Nombre d'\u00E9checs \: 
 mailer_from=Exp\u00E9diteur \: 
 mailer_host=Serveur \: 
 mailer_login=Identifiant \: 
 mailer_msg_title_error=Erreur
 mailer_msg_title_information=Information
 mailer_password=Mot de passe \: 
 mailer_port=Port \: 
 mailer_string=Notification d'email
 mailer_success_limit=Limite de succ\u00E8s \: 
 mailer_success_subject=Sujet Succ\u00E8s \: 
 mailer_test_mail=Tester email
 mailer_title_message=Message
 mailer_title_settings=Param\u00E8tres
 mailer_title_smtpserver=Serveur SMTP
 mailer_visualizer_title=R\u00E9cepteur Notification Email
 match_num_field=R\u00E9cup\u00E9rer la Ni\u00E8me corresp. (0 \: Al\u00E9atoire) \: 
 max=Maximum \:
 maximum_param=La valeur maximum autoris\u00E9e pour un \u00E9cart de valeurs
 md5hex_assertion_failure=Erreur de v\u00E9rification de la somme MD5 \: obtenu {0} mais aurait d\u00FB \u00EAtre {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex \u00E0 v\u00E9rifier
 md5hex_assertion_title=Assertion MD5Hex
 mechanism=M\u00E9canisme
 menu_assertions=Assertions
 menu_close=Fermer
 menu_collapse_all=R\u00E9duire tout
 menu_config_element=Configurations
 menu_edit=Editer
 menu_expand_all=Etendre tout
 menu_fragments=Fragment d'\u00E9l\u00E9ments
 menu_generative_controller=Echantillons
 menu_listener=R\u00E9cepteurs
 menu_logger_panel=Afficher la console 
 menu_logic_controller=Contr\u00F4leurs Logiques
 menu_merge=Fusionner...
 menu_modifiers=Modificateurs
 menu_non_test_elements=El\u00E9ments hors test
 menu_open=Ouvrir...
 menu_post_processors=Post-Processeurs
 menu_pre_processors=Pr\u00E9-Processeurs
 menu_response_based_modifiers=Modificateurs bas\u00E9s sur la r\u00E9ponse
 menu_search=Rechercher
 menu_search_reset=Effacer la recherche
 menu_tables=Table
 menu_threads=Moteurs d'utilisateurs
 menu_timer=Compteurs de temps
 menu_toolbar=Barre d'outils
 metadata=M\u00E9ta-donn\u00E9es
 method=M\u00E9thode \:
 mimetype=Type MIME
 minimum_param=La valeur minimale autoris\u00E9e pour l'\u00E9cart de valeurs
 minute=minute
 modddn=Ancienne valeur
 modification_controller_title=Contr\u00F4leur Modification
 modification_manager_title=Gestionnaire Modification
 modify_test=Modification
 modtest=Modification
 module_controller_module_to_run=Module \u00E0 ex\u00E9cuter \:
 module_controller_title=Contr\u00F4leur Module
 module_controller_warning=Ne peut pas trouver le module \:
 monitor_equation_active=Activit\u00E9 \:  (occup\u00E9e/max) > 25%
 monitor_equation_dead=Mort \: pas de r\u00E9ponse
 monitor_equation_healthy=Sant\u00E9 \:  (occup\u00E9e/max) < 25%
 monitor_equation_load=Charge \:  ((occup\u00E9e / max) * 50) + ((m\u00E9moire utilis\u00E9e / m\u00E9moire maximum) * 50)
 monitor_equation_warning=Attention \:  (occup\u00E9/max) > 67%
 monitor_health_tab_title=Sant\u00E9
 monitor_health_title=Moniteur de connecteurs (DEPRECATED)
 monitor_is_title=Utiliser comme moniteur (DEPRECATED)
 monitor_label_prefix=Pr\u00E9fixe de connecteur \:
 monitor_label_right_active=Actif
 monitor_label_right_dead=Mort
 monitor_label_right_healthy=Sant\u00E9
 monitor_label_right_warning=Attention
 monitor_legend_health=Sant\u00E9
 monitor_legend_load=Charge
 monitor_legend_memory_per=M\u00E9moire % (utilis\u00E9e/total)
 monitor_legend_thread_per=Unit\u00E9 % (occup\u00E9/max)
 monitor_performance_servers=Serveurs
 monitor_performance_tab_title=Performance
 monitor_performance_title=Graphique de performance
 name=Nom \:
 new=Nouveau
 newdn=Nouveau DN
 next=Suivant
 no=Norv\u00E9gien
 notify_child_listeners_fr=Notifier les r\u00E9cepteurs fils des \u00E9chantillons filtr\u00E9s
 number_of_threads=Nombre d'unit\u00E9s (utilisateurs) \:
 obsolete_test_element=Cet \u00E9l\u00E9ment de test est obsol\u00E8te
 once_only_controller_title=Contr\u00F4leur Ex\u00E9cution unique
 opcode=Code d'op\u00E9ration
 open=Ouvrir...
 option=Options
 optional_tasks=T\u00E2ches optionnelles
 paramtable=Envoyer les param\u00E8tres avec la requ\u00EAte \:
 password=Mot de passe \:
 paste=Coller
 paste_insert=Coller ins\u00E9rer
 path=Chemin \:
 path_extension_choice=Extension de chemin (utiliser ";" comme separateur)
 path_extension_dont_use_equals=Ne pas utiliser \u00E9gale dans l'extension de chemin (Compatibilit\u00E9 Intershop Enfinity)
 path_extension_dont_use_questionmark=Ne pas utiliser le point d'interrogation dans l'extension du chemin (Compatiblit\u00E9 Intershop Enfinity)
 patterns_to_exclude=URL \: motifs \u00E0 exclure
 patterns_to_include=URL \: motifs \u00E0 inclure
 pkcs12_desc=Clef PKCS 12 (*.p12)
 pl=Polonais
 poisson_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 sur la loi de poisson (en millisecondes) \:
 poisson_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution de type Poisson
 poisson_timer_range=D\u00E9viation (en millisecondes) \:
 poisson_timer_title=Compteur de temps al\u00E9atoire selon la loi de Poisson 
 port=Port \:
 post_as_parameters=Param\u00E8tres
 post_body=Corps de la requ\u00EAte
 post_body_raw=Donn\u00E9es de la requ\u00EAte
 post_files_upload=T\u00E9l\u00E9chargement de fichiers
 post_thread_group_title=Groupe d'unit\u00E9s de fin
 previous=Pr\u00E9c\u00E9dent
 property_as_field_label={0}\:
 property_default_param=Valeur par d\u00E9faut
 property_edit=Editer
 property_editor.value_is_invalid_message=Le texte que vous venez d'entrer n'a pas une valeur valide pour cette propri\u00E9t\u00E9.\nLa propri\u00E9t\u00E9 va revenir \u00E0 sa valeur pr\u00E9c\u00E9dente.
 property_editor.value_is_invalid_title=Texte saisi invalide
 property_name_param=Nom de la propri\u00E9t\u00E9
 property_returnvalue_param=Revenir \u00E0 la valeur originale de la propri\u00E9t\u00E9 (d\u00E9faut non) ?
 property_tool_tip=<html>{0}</html>
 property_undefined=Non d\u00E9fini
 property_value_param=Valeur de propri\u00E9t\u00E9
 property_visualiser_title=Afficheur de propri\u00E9t\u00E9s
 protocol=Protocole [http] \:
 protocol_java_border=Classe Java
 protocol_java_classname=Nom de classe \:
 protocol_java_config_tile=Configurer \u00E9chantillon Java
 protocol_java_test_title=Test Java
 provider_url=Provider URL
 proxy_assertions=Ajouter une Assertion R\u00E9ponse
 proxy_cl_error=Si un serveur proxy est sp\u00E9cifi\u00E9, h\u00F4te et port doivent \u00EAtre donn\u00E9
 proxy_cl_wrong_target_cl=Le contr\u00F4leur cible est configur\u00E9 en mode "Utiliser un contr\u00F4leur enregistreur" \nmais aucun contr\u00F4leur de ce type n'existe, assurez vous de l'ajouter comme fils \nde Groupe d'unit\u00E9s afin de pouvoir d\u00E9marrer l'enregisteur
 proxy_content_type_exclude=Exclure \:
 proxy_content_type_filter=Filtre de type de contenu
 proxy_content_type_include=Inclure \:
 proxy_daemon_bind_error=Impossible de lancer le serveur proxy, le port est d\u00E9j\u00E0 utilis\u00E9. Choisissez un autre port.
 proxy_daemon_error=Impossible de lancer le serveur proxy, voir le journal pour plus de d\u00E9tails
 proxy_daemon_error_from_clipboard=depuis le presse-papier
 proxy_daemon_error_not_retrieve=Impossible d'ajouter
 proxy_daemon_error_read_args=Impossible de lire les arguments depuis le presse-papiers \:
 proxy_daemon_msg_check_details=Svp, v\u00E9rifier les d\u00E9tails ci-dessous lors de l'installation du certificat dans le navigateur
 proxy_daemon_msg_created_in_bin=cr\u00E9\u00E9 dans le r\u00E9pertoire bin de JMeter
 proxy_daemon_msg_install_as_in_doc=Vous pouvez l'installer en suivant les instructions de la documentation Component Reference (voir Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Certificat AC ra\u00E7ine \:
 proxy_domains=Domaines HTTPS \:
 proxy_domains_dynamic_mode_tooltip=Liste de noms de domaine pour les url HTTPS, ex. jmeter.apache.org ou les domaines wildcard comme *.apache.org. Utiliser la virgule comme s\u00E9parateur. 
 proxy_domains_dynamic_mode_tooltip_java6=Pour activer ce champ, utiliser un environnement d'ex\u00E9cution Java 7+
 proxy_general_settings=Param\u00E8tres g\u00E9n\u00E9raux
 proxy_headers=Capturer les ent\u00EAtes HTTP
 proxy_prefix_http_sampler_name=Pr\u00E9fixe \:
 proxy_regex=Correspondance des variables par regex ?
 proxy_sampler_settings=Param\u00E8tres Echantillon HTTP
 proxy_sampler_type=Type \:
 proxy_separators=Ajouter des s\u00E9parateurs
 proxy_settings_port_error_digits=Seuls les chiffres sont autoris\u00E9s.
 proxy_settings_port_error_invalid_data=Donn\u00E9es invalides
 proxy_target=Contr\u00F4leur Cible \:
 proxy_test_plan_content=Param\u00E8tres du plan de test
 proxy_title=Enregistreur script de test HTTP(S)
 pt_br=Portugais (Br\u00E9sil)
 ramp_up=Dur\u00E9e de mont\u00E9e en charge (en secondes) \:
 random_control_title=Contr\u00F4leur Al\u00E9atoire
 random_order_control_title=Contr\u00F4leur d'Ordre al\u00E9atoire
 random_multi_result_source_variable=Variable(s) source (separateur |)
 random_multi_result_target_variable=Variable cible
 random_string_chars_to_use=Caract\u00E8res \u00E0 utiliser pour la g\u00E9n\u00E9ration de la cha\u00EEne al\u00E9atoire
 random_string_length=Longueur de cha\u00EEne al\u00E9atoire
 realm=Univers (realm)
 record_controller_clear_samples=Supprimer tous les \u00E9chantillons
 record_controller_title=Contr\u00F4leur Enregistreur
 redo=R\u00E9tablir
 ref_name_field=Nom de r\u00E9f\u00E9rence \:
 regex_extractor_title=Extracteur Expression r\u00E9guli\u00E8re
 regex_field=Expression r\u00E9guli\u00E8re \:
 regex_params_names_field=Num\u00E9ro du groupe de la Regex pour les noms des param\u00E8tres
 regex_params_ref_name_field=Nom de la r\u00E9f\u00E9rence de la Regex
 regex_params_title=Param\u00E8tres utilisateurs bas\u00E9s sur RegEx
 regex_params_values_field=Num\u00E9ro du groupe de la Regex pour les valeurs des param\u00E8tres
 regex_source=Port\u00E9e
 regex_src_body=Corps
 regex_src_body_as_document=Corps en tant que Document
 regex_src_body_unescaped=Corps (non \u00E9chapp\u00E9)
 regex_src_hdrs=Ent\u00EAtes (R\u00E9ponse)
 regex_src_hdrs_req=Ent\u00EAtes (Requ\u00EAte)
 regex_src_url=URL
 regexfunc_param_1=Expression r\u00E9guli\u00E8re utilis\u00E9e pour chercher les r\u00E9sultats de la requ\u00EAte pr\u00E9c\u00E9dente.
 regexfunc_param_2=Canevas pour la ch\u00EEne de caract\u00E8re de remplacement, utilisant des groupes d'expressions r\u00E9guli\u00E8res. Le format est  $[group]$.  Exemple $1$.
 regexfunc_param_3=Quelle correspondance utiliser. Un entier 1 ou plus grand, RAND pour indiquer que JMeter doit choisir al\u00E9atoirement , A d\u00E9cimal, ou ALL indique que toutes les correspondances doivent \u00EAtre utilis\u00E9es
 regexfunc_param_4=Entre le texte. Si ALL est s\u00E9lectionn\u00E9, l'entre-texte sera utilis\u00E9 pour g\u00E9n\u00E9rer les r\u00E9sultats ([""])
 regexfunc_param_5=Text par d\u00E9faut. Utilis\u00E9 \u00E0 la place du canevas si l'expression r\u00E9guli\u00E8re ne trouve pas de correspondance
 regexfunc_param_7=Variable en entr\u221A\u00A9e contenant le texte \u221A\u2020 parser ([\u221A\u00A9chantillon pr\u221A\u00A9c\u221A\u00A9dent])
 regexp_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 regexp_tester_button_test=Tester
 regexp_tester_field=Expression r\u00E9guli\u00E8re \:
 regexp_tester_title=Testeur de RegExp
 remote_error_init=Erreur lors de l'initialisation du serveur distant
 remote_error_starting=Erreur lors du d\u221A\u00A9marrage du serveur distant
 remote_exit=Sortie distante
 remote_exit_all=Sortie distante de tous
 remote_shut=Extinction \u00E0 distance
 remote_shut_all=Extinction \u00E0 distance de tous
 remote_start=D\u00E9marrage distant
 remote_start_all=D\u00E9marrage distant de tous
 remote_stop=Arr\u00EAt distant
 remote_stop_all=Arr\u00EAt distant de tous
 remove=Supprimer
 remove_confirm_msg=Etes-vous s\u00FBr de vouloir supprimer ce(s) \u00E9l\u00E9ment(s) ?
 remove_confirm_title=Confirmer la suppression ?
 rename=Renommer une entr\u00E9e
 report=Rapport
 report_bar_chart=Graphique \u221A\u2020 barres
 report_bar_graph_url=URL
 report_base_directory=R\u221A\u00A9pertoire de Base
 report_chart_caption=L\u221A\u00A9gende du graph
 report_chart_x_axis=Axe X
 report_chart_x_axis_label=Libell\u221A\u00A9 de l'Axe X
 report_chart_y_axis=Axe Y
 report_chart_y_axis_label=Libell\u221A\u00A9 de l'Axe Y
 report_line_graph=Graphique Lin\u221A\u00A9aire
 report_line_graph_urls=Inclure les URLs
 report_output_directory=R\u221A\u00A9pertoire de sortie du rapport
 report_page=Page de Rapport
 report_page_element=Page Element
 report_page_footer=Pied de page
 report_page_header=Ent\u221A\u2122te de Page
 report_page_index=Cr\u221A\u00A9er la Page d'Index
 report_page_intro=Page d'Introduction
 report_page_style_url=Url de la feuille de style
 report_page_title=Titre de la Page
 report_pie_chart=Camembert
 report_plan=Plan du rapport
 report_select=Selectionner
 report_summary=Rapport r\u221A\u00A9sum\u221A\u00A9
 report_table=Table du Rapport
 report_writer=R\u221A\u00A9dacteur du Rapport
 report_writer_html=R\u221A\u00A9dacteur de rapport HTML
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Libell\u00E9
 reportgenerator_summary_apdex_satisfied=T (Seuil de tol\u00E9rance)
 reportgenerator_summary_apdex_tolerated=F (Seuil de frustration)
 reportgenerator_summary_errors_count=Nombre d'erreurs
 reportgenerator_summary_errors_rate_all=% de tous les \u00E9chantillons
 reportgenerator_summary_errors_rate_error=% des erreurs
 reportgenerator_summary_errors_type=Type d'erreur
 reportgenerator_summary_statistics_count=\#Echantillons
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=% Erreur
 reportgenerator_summary_statistics_kbytes=Ko re\u00e7ues / sec
 reportgenerator_summary_statistics_sent_kbytes=Ko envoy\u00e9s / sec
 reportgenerator_summary_statistics_label=Libell\u00E9
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Temps moyen
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%d%% centile
 reportgenerator_summary_statistics_throughput=D\u00E9bit
 reportgenerator_summary_total=Total
 request_data=Donn\u00E9e requ\u00EAte
 reset=R\u00E9initialiser
 reset_gui=R\u00E9initialiser l'\u00E9l\u00E9ment
 response_save_as_md5=R\u00E9ponse en empreinte MD5
 response_time_distribution_satisfied_label=Requ\u00EAtes \\ntemps de r\u00E9ponse <= {0}ms
 response_time_distribution_tolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms et <= {1}ms
 response_time_distribution_untolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms
 response_time_distribution_failed_label=Requ\u00EAtes en erreur
 restart=Red\u00E9marrer
 resultaction_title=Op\u00E9rateur R\u00E9sultats Action
 resultsaver_addtimestamp=Ajouter un timestamp
 resultsaver_errors=Enregistrer seulement les r\u00E9ponses en \u00E9checs
 resultsaver_numberpadlen=Taille minimale du num\u00E9ro de s\u00E9quence
 resultsaver_prefix=Pr\u00E9fixe du nom de fichier \: 
 resultsaver_skipautonumber=Ne pas ajouter de nombre au pr\u00E9fixe
 resultsaver_skipsuffix=Ne pas ajouter de suffixe
 resultsaver_success=Enregistrer seulement les r\u00E9ponses en succ\u00E8s
 resultsaver_title=Sauvegarder les r\u00E9ponses vers un fichier
 resultsaver_variable=Nom de variable \:
 retobj=Retourner les objets
 return_code_config_box_title=Configuration du code retour
 reuseconnection=R\u00E9-utiliser la connexion
 revert_project=Annuler les changements
 revert_project?=Annuler les changements sur le projet ?
 root=Racine
 root_title=Racine
 run=Lancer
 run_threadgroup=Lancer
 run_threadgroup_no_timers=Lancer sans pauses
 running_test=Lancer test
 runtime_controller_title=Contr\u00F4leur Dur\u00E9e d'ex\u00E9cution
 runtime_seconds=Temps d'ex\u00E9cution (secondes) \:
 sample_result_save_configuration=Sauvegarder la configuration de la sauvegarde des \u00E9chantillons
 sample_scope=Appliquer sur
 sample_scope_all=L'\u00E9chantillon et ses ressources li\u00E9es
 sample_scope_children=Les ressources li\u00E9es
 sample_scope_parent=L'\u00E9chantillon
 sample_scope_variable=Une variable \:
 sample_timeout_memo=Interrompre l'\u00E9chantillon si le d\u00E9lai est d\u00E9pass\u00E9
 sample_timeout_timeout=D\u00E9lai d'attente avant interruption (en millisecondes) \: 
 sample_timeout_title=Compteur Interruption
 sampler_label=Libell\u00E9
 sampler_on_error_action=Action \u00E0 suivre apr\u00E8s une erreur d'\u00E9chantillon
 sampler_on_error_continue=Continuer
 sampler_on_error_start_next_loop=D\u00E9marrer it\u00E9ration suivante
 sampler_on_error_stop_test=Arr\u00EAter le test
 sampler_on_error_stop_test_now=Arr\u00EAter le test imm\u00E9diatement
 sampler_on_error_stop_thread=Arr\u00EAter l'unit\u00E9
 save=Enregistrer le plan de test
 save?=Enregistrer ?
 save_all_as=Enregistrer le plan de test sous...
 save_as=Enregistrer sous...
 save_as_error=Au moins un \u00E9l\u00E9ment doit \u00EAtre s\u00E9lectionn\u00E9 \!
 save_as_image=Enregistrer en tant qu'image sous...
 save_as_image_all=Enregistrer l'\u00E9cran en tant qu'image...
 save_as_test_fragment=Enregistrer comme Fragment de Test
 save_as_test_fragment_error=Au moins un \u00E9l\u00E9ment ne peut pas \u00EAtre plac\u00E9 sous un Fragment de Test
 save_assertionresultsfailuremessage=Messages d'erreur des assertions
 save_assertions=R\u00E9sultats des assertions (XML)
 save_asxml=Enregistrer au format XML
 save_bytes=Nombre d'octets re\u00e7us
 save_code=Code de r\u00E9ponse HTTP
 save_connecttime=Temps \u00E9tablissement connexion
 save_datatype=Type de donn\u00E9es
 save_encoding=Encodage
 save_fieldnames=Libell\u00E9 des colonnes (CSV)
 save_filename=Nom de fichier de r\u00E9ponse
 save_graphics=Enregistrer le graphique
 save_hostname=Nom d'h\u00F4te
 save_idletime=Temps d'inactivit\u00E9
 save_label=Libell\u00E9
 save_latency=Latence
 save_message=Message de r\u00E9ponse
 save_overwrite_existing_file=Le fichier s\u00E9lectionn\u00E9 existe d\u00E9j\u00E0, voulez-vous l'\u00E9craser ?
 save_requestheaders=Ent\u00EAtes de requ\u00EAte (XML)
 save_responsedata=Donn\u00E9es de r\u00E9ponse (XML)
 save_responseheaders=Ent\u00EAtes de r\u00E9ponse (XML)
 save_samplecount=Nombre d'\u00E9chantillon et d'erreur
 save_samplerdata=Donn\u00E9es d'\u00E9chantillon (XML)
 save_sentbytes=Nombre d'octets envoy\u00E9s
 save_subresults=Sous r\u00E9sultats (XML)
 save_success=Succ\u00E8s
 save_threadcounts=Nombre d'unit\u00E9s actives
 save_threadname=Nom d'unit\u00E9
 save_time=Temps \u00E9coul\u00E9
 save_timestamp=Horodatage
 save_url=URL
 save_workbench=Sauvegarder le plan de travail
 sbind=Simple connexion/d\u00E9connexion
 scheduler=Programmateur de d\u00E9marrage
 scheduler_configuration=Configuration du programmateur
 scope=Port\u00E9e
 search=Rechercher
 search_base=Base de recherche
 search_expand=Rechercher & D\u00E9plier
 search_filter=Filtre de recherche
 search_test=Recherche
 search_text_button_close=Fermer
 search_text_button_find=Rechercher
 search_text_button_next=Suivant
 search_text_chkbox_case=Consid\u00E9rer la casse
 search_text_chkbox_regexp=Exp. reguli\u00E8re
 search_text_field=Rechercher \:
 search_text_msg_not_found=Texte non trouv\u00E9
 search_text_title_not_found=Pas trouv\u00E9
 search_tree_title=Rechercher dans l'arbre
 searchbase=Base de recherche
 searchfilter=Filtre de recherche
 searchtest=Recherche
 second=seconde
 secure=S\u00E9curis\u00E9 \:
 send_file=Envoyer un fichier avec la requ\u00EAte \:
 send_file_browse=Parcourir...
 send_file_filename_label=Chemin du fichier
 send_file_mime_label=Type MIME
 send_file_param_name_label=Nom du param\u00E8tre
 server=Nom ou IP du serveur \:
 servername=Nom du serveur \:
 session_argument_name=Nom des arguments de la session
 setup_thread_group_title=Groupe d'unit\u00E9s de d\u00E9but
 should_save=Vous devez enregistrer le plan de test avant de le lancer.  \nSi vous utilisez des fichiers de donn\u00E9es (i.e. Source de donn\u00E9es CSV ou la fonction _StringFromFile), \nalors c'est particuli\u00E8rement important d'enregistrer d'abord votre script de test. \nVoulez-vous enregistrer maintenant votre plan de test ?
 shutdown=Eteindre
 simple_config_element=Configuration Simple
 simple_data_writer_title=Enregistreur de donn\u00E9es
 size_assertion_comparator_error_equal=est \u00E9gale \u00E0
 size_assertion_comparator_error_greater=est plus grand que
 size_assertion_comparator_error_greaterequal=est plus grand ou \u00E9gale \u00E0
 size_assertion_comparator_error_less=est inf\u00E9rieur \u00E0
 size_assertion_comparator_error_lessequal=est inf\u00E9rieur ou \u00E9gale \u00E0
 size_assertion_comparator_error_notequal=n'est pas \u00E9gale \u00E0
 size_assertion_comparator_label=Type de comparaison
 size_assertion_failure=Le r\u00E9sultat n''a pas la bonne taille \: il \u00E9tait de {0} octet(s), mais aurait d\u00FB \u00EAtre de {1} {2} octet(s).
 size_assertion_input_error=Entrer un entier positif valide svp.
 size_assertion_label=Taille en octets \:
 size_assertion_size_test=Taille \u00E0 v\u00E9rifier
 size_assertion_title=Assertion Taille
 smime_assertion_issuer_dn=Nom unique de l'\u00E9metteur \: 
 smime_assertion_message_position=V\u00E9rifier l'assertion sur le message \u00E0 partir de la position
 smime_assertion_not_signed=Message non sign\u00E9
 smime_assertion_signature=Signature
 smime_assertion_signer=Certificat signataire
 smime_assertion_signer_by_file=Fichier du certificat \: 
 smime_assertion_signer_constraints=V\u00E9rifier les valeurs \:
 smime_assertion_signer_dn=Nom unique du signataire \: 
 smime_assertion_signer_email=Adresse courriel du signataire \: 
 smime_assertion_signer_no_check=Pas de v\u00E9rification
 smime_assertion_signer_serial=Num\u00E9ro de s\u00E9rie \: 
 smime_assertion_title=Assertion SMIME
 smime_assertion_verify_signature=V\u00E9rifier la signature
 smtp_additional_settings=Param\u00E8tres suppl\u00E9mentaires
 smtp_attach_file=Fichier(s) attach\u00E9(s) \:
 smtp_attach_file_tooltip=S\u00E9parer les fichiers par le point-virgule ";"
 smtp_auth_settings=Param\u00E8tres d'authentification
 smtp_bcc=Adresse en copie cach\u00E9e (Bcc) \:
 smtp_cc=Adresse en copie (CC) \:
 smtp_default_port=(D\u00E9fauts \: SMTP \: 25, SSL \: 465, StartTLS \: 587)
 smtp_eml=Envoyer un message .eml \:
 smtp_enabledebug=Activer les traces de d\u00E9bogage ?
 smtp_enforcestarttls=Forcer le StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Force</b> le serveur a utiliser StartTLS.<br />Si il n'est pas s\u00E9lectionn\u00E9 et que le serveur SMTP ne supporte pas StartTLS, <br />une connexion SMTP normale sera utilis\u00E9e \u00E0 la place. <br /><i>Merci de noter</i> que la case \u00E0 cocher cr\u00E9\u00E9e un fichier dans /tmp/, <br />donc cela peut poser des probl\u00E8mes sous Windows.</html>
 smtp_from=Adresse exp\u00E9diteur (From) \:
 smtp_header_add=Ajouter une ent\u00EAte
 smtp_header_name=Nom d'ent\u00EAte
 smtp_header_remove=Supprimer
 smtp_header_value=Valeur d'ent\u00EAte
 smtp_mail_settings=Param\u00E8tres du courriel
 smtp_message=Message \:
 smtp_message_settings=Param\u00E8tres du message
 smtp_messagesize=Calculer la taille du message
 smtp_password=Mot de passe \:
 smtp_plainbody=Envoyer le message en texte (i.e. sans multipart/mixed)
 smtp_replyto=Adresse de r\u00E9ponse (Reply-To) \:
 smtp_sampler_title=Requ\u00EAte SMTP
 smtp_security_settings=Param\u00E8tres de s\u00E9curit\u00E9
 smtp_server=Serveur \:
 smtp_server_connection_timeout=D\u00E9lai d'attente de connexion \:
 smtp_server_port=Port \:
 smtp_server_settings=Param\u00E8tres du serveur
 smtp_server_timeout=D\u00E9lai d'attente de r\u00E9ponse \:
 smtp_server_timeouts_settings=D\u00E9lais d'attente (milli-secondes)
 smtp_subject=Sujet \:
 smtp_suppresssubj=Supprimer l'ent\u00EAte Sujet (Subject)
 smtp_timestamp=Ajouter un horodatage dans le sujet
 smtp_to=Adresse destinataire (To) \:
 smtp_trustall=Faire confiance \u00E0 tous les certificats
 smtp_trustall_tooltip=<html><b>Forcer</b> JMeter \u00E0 faire confiance \u00E0 tous les certificats, quelque soit l'autorit\u00E9 de certification du certificat.</html>
 smtp_truststore=Coffre de cl\u00E9s local \:
 smtp_truststore_tooltip=<html>Le chemin du coffre de confiance.<br />Les chemins relatifs sont d\u00E9termin\u00E9s \u00E0 partir du r\u00E9pertoire courant.<br />En cas d'\u00E9chec, c'est le r\u00E9pertoire contenant le script JMX qui est utilis\u00E9.</html>
 smtp_useauth=Utiliser l'authentification
 smtp_usenone=Pas de fonctionnalit\u00E9 de s\u00E9curit\u00E9
 smtp_username=Identifiant \:
 smtp_usessl=Utiliser SSL
 smtp_usestarttls=Utiliser StartTLS
 smtp_usetruststore=Utiliser le coffre de confiance local
 smtp_usetruststore_tooltip=<html>Autoriser JMeter \u00E0 utiliser le coffre de confiance local.</html>
 soap_action=Action Soap
 soap_data_title=Donn\u00E9es Soap/XML-RPC
 soap_sampler_file_invalid=Le nom de fichier r\u00E9f\u00E9rence un fichier absent ou sans droits de lecture\:
 soap_sampler_title=Requ\u00EAte SOAP/XML-RPC
 soap_send_action=Envoyer l'action SOAP \:
 solinger=SO_LINGER\:
 spline_visualizer_average=Moyenne \:
 spline_visualizer_incoming=Entr\u00E9e \:
 spline_visualizer_maximum=Maximum \:
 spline_visualizer_minimum=Minimum \:
 spline_visualizer_title=Moniteur de courbe (spline)  (DEPRECATED)
 spline_visualizer_waitingmessage=En attente de r\u00E9sultats d'\u00E9chantillons
 split_function_separator=S\u00E9parateur utilis\u00E9 pour scinder le texte. Par d\u00E9faut , (virgule) est utilis\u00E9.
 split_function_string=Texte \u00E0 scinder
 ssl_alias_prompt=Veuillez entrer votre alias pr\u00E9f\u00E9r\u00E9
 ssl_alias_select=S\u00E9lectionner votre alias pour le test
 ssl_alias_title=Alias du client
 ssl_error_title=Probl\u00E8me de KeyStore
 ssl_pass_prompt=Entrer votre mot de passe
 ssl_pass_title=Mot de passe KeyStore
 ssl_port=Port SSL
 sslmanager=Gestionnaire SSL
 start=Lancer
 start_no_timers=Lancer sans pauses
 starttime=Date et heure de d\u00E9marrage \:
 stop=Arr\u00EAter
 stopping_test=Arr\u00EAt de toutes les unit\u00E9s de tests en cours. Le nombre d'unit\u00E9s actives est visible dans le coin haut droit de l'interface. Soyez patient, merci. 
 stopping_test_failed=Au moins une unit\u00E9 non arr\u00EAt\u00E9e; voir le journal.
 stopping_test_host=H\u00F4te
 stopping_test_title=En train d'arr\u00EAter le test
 string_from_file_encoding=Encodage du fichier (optionnel)
 string_from_file_file_name=Entrer le chemin (absolu ou relatif) du fichier
 string_from_file_seq_final=Nombre final de s\u00E9quence de fichier
 string_from_file_seq_start=D\u00E9marer le nombre de s\u00E9quence de fichier
 summariser_title=G\u00E9n\u00E9rer les resultats consolid\u00E9s
 summary_report=Rapport consolid\u00E9
 switch_controller_label=Aller vers le num\u00E9ro d'\u00E9l\u00E9ment (ou nom) subordonn\u00E9 \:
 switch_controller_title=Contr\u00F4leur Aller \u00E0
 system_sampler_stderr=Erreur standard (stderr) \:
 system_sampler_stdin=Entr\u00E9e standard (stdin) \:
 system_sampler_stdout=Sortie standard (stdout) \:
 system_sampler_title=Appel de processus syst\u00E8me
 table_visualizer_bytes=Octets
 table_visualizer_connect=\u00C9tabl. Conn.(ms)
 table_visualizer_latency=Latence
 table_visualizer_sample_num=Echantillon \#
 table_visualizer_sample_time=Temps (ms)
 table_visualizer_sent_bytes=Octets envoy\u00E9s
 table_visualizer_start_time=Heure d\u00E9but
 table_visualizer_status=Statut
 table_visualizer_success=Succ\u00E8s
 table_visualizer_thread_name=Nom d'unit\u00E9
 table_visualizer_warning=Alerte
 target_server=Serveur cible
 tcp_classname=Nom de classe TCPClient \:
 tcp_config_title=Param\u00E8tres TCP par d\u00E9faut
 tcp_nodelay=D\u00E9finir aucun d\u00E9lai (NoDelay)
 tcp_port=Num\u00E9ro de port \:
 tcp_request_data=Texte \u00E0 envoyer \:
 tcp_sample_title=Requ\u00EAte TCP
 tcp_timeout=Expiration (millisecondes) \:
 teardown_on_shutdown=Ex\u00E9cuter le Groupe d'unit\u00E9s de fin m\u00EAme apr\u00E8s un arr\u00EAt manuel des Groupes d'unit\u00E9s principaux
 template_choose=Choisir le mod\u00E8le
 template_create_from=Cr\u00E9er
 template_field=Canevas \:
 template_load?=Charger le mod\u00E8le ?
 template_menu=Mod\u00E8les...
 template_merge_from=Fusionner
 template_reload=Recharger les mod\u00E8les
 template_title=Mod\u00E8les
 test=Test
 test_action_action=Action \:
 test_action_duration=Dur\u00E9e (millisecondes) \:
 test_action_pause=Mettre en pause
 test_action_restart_next_loop=Passer \u00E0 l'it\u00E9ration suivante de la boucle
 test_action_stop=Arr\u00EAter
 test_action_stop_now=Arr\u00EAter imm\u00E9diatement
 test_action_target=Cible \:
 test_action_target_test=Toutes les unit\u00E9s
 test_action_target_thread=Unit\u00E9 courante
 test_action_title=Action test
 test_configuration=Type de test
 test_fragment_title=Fragment d'\u00E9l\u00E9ments
 test_plan=Plan de test
 test_plan_classpath_browse=Ajouter un r\u00E9pertoire ou un fichier 'jar' au 'classpath'
 testconfiguration=Tester la configuration
 testplan.serialized=Lancer les groupes d'unit\u00E9s en s\u00E9rie (c'est-\u00E0-dire \: lance un groupe \u00E0 la fois)
 testplan_comments=Commentaires \:
 testt=Test
 textbox_cancel=Annuler
 textbox_close=Fermer
 textbox_save_close=Enregistrer & Fermer
 textbox_title_edit=Editer texte
 textbox_title_view=Voir texte
 textbox_tooltip_cell=Double clic pour voir/editer
 thread_delay_properties=Propri\u00E9t\u00E9s de temporisation de l'unit\u00E9
 thread_group_title=Groupe d'unit\u00E9s
 thread_properties=Propri\u00E9t\u00E9s du groupe d'unit\u00E9s
 threadgroup=Groupe d'unit\u00E9s
 throughput_control_bynumber_label=Ex\u00E9cutions totales
 throughput_control_bypercent_label=Pourcentage d'ex\u00E9cution
 throughput_control_perthread_label=Par utilisateur
 throughput_control_title=Contr\u00F4leur D\u00E9bit
 throughput_control_tplabel=D\u00E9bit \:
 time_format=Chaine de formatage sur le mod\u00E8le SimpleDateFormat (optionnel)
 timelim=Limiter le temps de r\u00E9ponses \u00E0 (ms)
 timeout_config_box_title=Configuration du d\u00E9lai d'expiration
 timeout_title=D\u00E9lai expiration (ms)
 toggle=Permuter
 toolbar_icon_set_not_found=Le fichier de description des ic\u00F4nes de la barre d'outils n'est pas trouv\u00E9. Voir les journaux.
 total_threads_tooltip=Nombre total d'Unit\u00E9s \u00E0 lancer
 tr=Turc
 transaction_controller_include_timers=Inclure la dur\u00E9e des compteurs de temps et pre/post processeurs dans le calcul du temps
 transaction_controller_parent=G\u00E9n\u00E9rer en \u00E9chantillon parent
 transaction_controller_title=Contr\u00F4leur Transaction
 transform_into_variable=Remplacer les valeurs par des variables
 unbind=D\u00E9connexion de l'unit\u00E9
 undo=Annuler
 unescape_html_string=Cha\u00EEne \u00E0 \u00E9chapper
 unescape_string=Cha\u00EEne de caract\u00E8res contenant des\u00E9chappements Java
 uniform_timer_delay=D\u00E9lai de d\u00E9calage constant (en millisecondes) \:
 uniform_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution uniforme
 uniform_timer_range=D\u00E9viation al\u00E9atoire maximum (en millisecondes) \:
 uniform_timer_title=Compteur de temps al\u00E9atoire uniforme
 up=Monter
 update=Mettre \u00E0 jour
 update_per_iter=Mettre \u00E0 jour une fois par it\u00E9ration
 upload=Fichier \u00E0 uploader
 upper_bound=Borne sup\u00E9rieure
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocole \:
 url_config_title=Param\u00E8tres HTTP par d\u00E9faut
 url_full_config_title=Echantillon d'URL complet
 url_multipart_config_title=Requ\u00EAte HTTP Multipart par d\u00E9faut
 urldecode_string=Cha\u00EEne de style URL \u00E0 d\u00E9coder
 urlencode_string=Cha\u00EEne de caract\u00E8res \u00E0 encoder en style URL
 use_custom_dns_resolver=Utiliser un r\u00E9solveur DNS personnalis\u00E9
 use_expires=Utiliser les ent\u00EAtes Cache-Control/Expires lors du traitement des requ\u00EAtes GET
 use_keepalive=Connexion persist.
 use_multipart_for_http_post=Multipart/form-data
 use_multipart_mode_browser=Ent\u00EAtes compat. navigateur
 use_recording_controller=Utiliser un contr\u00F4leur enregistreur
 use_system_dns_resolver=Utiliser le r\u00E9solveur DNS syst\u00E8me (JVM)
 user=Utilisateur
 user_defined_test=Test d\u00E9fini par l'utilisateur
 user_defined_variables=Variables pr\u00E9-d\u00E9finies
 user_param_mod_help_note=(Ne pas changer. A la place, modifier le fichier de ce nom dans le r\u00E9pertoire /bin de JMeter)
 user_parameters_table=Param\u00E8tres
 user_parameters_title=Param\u00E8tres Utilisateur
 userdn=Identifiant
 username=Nom d'utilisateur \:
 userpw=Mot de passe
 validate_threadgroup=Valider
 value=Valeur \:
 value_to_quote_meta=Valeur \u00E0 \u00E9chapper des caract\u00E8res sp\u00E9ciaux utilis\u00E8s par ORO Regexp
 var_name=Nom de r\u00E9f\u00E9rence \:
 variable_name_param=Nom de variable (peut inclure une r\u00E9f\u00E9rence de variable ou fonction)
 view_graph_tree_title=Voir le graphique en arbre
 view_results_assertion_error=Erreur d'assertion \: 
 view_results_assertion_failure=Echec d'assertion \: 
 view_results_assertion_failure_message=Message d'\u00E9chec d'assertion \: 
 view_results_autoscroll=D\u00E9filement automatique ?
 view_results_childsamples=Echantillons enfants?
 view_results_connect_time=Temps \u00E9tablissement connexion \: 
 view_results_datatype=Type de donn\u00E9es ("text"|"bin"|"")\: 
 view_results_desc=Affiche les r\u00E9sultats d'un \u00E9chantillon dans un arbre de r\u00E9sultats
 view_results_error_count=Compteur erreur\: 
 view_results_fields=champs \:
 view_results_in_table=Tableau de r\u00E9sultats
 view_results_latency=Latence \: 
 view_results_load_time=Temps de r\u00E9ponse \: 
 view_results_render=Rendu \: 
 view_results_render_document=Document
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML et ressources
 view_results_render_html_formatted=Code source HTML Format\u00E9
 view_results_render_json=JSON
 view_results_render_text=Texte brut
 view_results_render_xml=XML
 view_results_request_headers=Ent\u00EAtes de requ\u00EAte \:
 view_results_response_code=Code de retour \: 
 view_results_response_headers=Ent\u00EAtes de r\u00E9ponse \:
 view_results_response_message=Message de retour \: 
 view_results_response_missing_tika=Manque l'archive tika-app.jar dans le classpath. Impossible de convertir en texte ce type de document.\nT\u00E9l\u00E9charger le fichier tika-app-x.x.jar depuis http\://tika.apache.org/download.html\nPuis ajouter ce fichier dans le r\u00E9pertoire <JMeter>/lib
 view_results_response_partial_message=D\u00E9but du message\:
 view_results_response_too_large_message=R\u00E9ponse d\u00E9passant la taille maximale d'affichage. Taille \: 
 view_results_sample_count=Compteur \u00E9chantillon \: 
 view_results_sample_start=Date d\u00E9but \u00E9chantillon \: 
 view_results_search_pane=Volet recherche 
 view_results_sent_bytes=Octets envoy\u00E9s:
 view_results_size_body_in_bytes=Taille du corps en octets \: 
 view_results_size_headers_in_bytes=Taille de l'ent\u00EAte en octets \: 
 view_results_size_in_bytes=Taille en octets \: 
 view_results_tab_assertion=R\u00E9sultats d'assertion
 view_results_tab_request=Requ\u00EAte
 view_results_tab_response=Donn\u00E9es de r\u00E9ponse
 view_results_tab_sampler=R\u00E9sultat de l'\u00E9chantillon
 view_results_table_fields_key=Champ suppl\u00E9mentaire
 view_results_table_fields_value=Valeur
 view_results_table_headers_key=Ent\u00EAtes de r\u00E9ponse
 view_results_table_headers_value=Valeur
 view_results_table_request_headers_key=Ent\u00EAtes de requ\u00EAte
 view_results_table_request_headers_value=Valeur
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=H\u00F4te
 view_results_table_request_http_method=M\u00E9thode
 view_results_table_request_http_nohttp=N'est pas un \u00E9chantillon HTTP
 view_results_table_request_http_path=Chemin
 view_results_table_request_http_port=Port
 view_results_table_request_http_protocol=Protocole
 view_results_table_request_params_key=Nom de param\u00E8tre
 view_results_table_request_params_value=Valeur
 view_results_table_request_raw_nodata=Pas de donn\u00E9es \u00E0 afficher
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Brut
 view_results_table_result_tab_parsed=D\u00E9cod\u00E9
 view_results_table_result_tab_raw=Brut
 view_results_thread_name=Nom d'unit\u00E9 \: 
 view_results_title=Voir les r\u00E9sultats
 view_results_tree_title=Arbre de r\u00E9sultats
 warning=Attention \!
 web_cannot_convert_parameters_to_raw=Ne peut pas convertir les param\u00E8tres en Donn\u00E9es POST brutes\ncar l'un des param\u00E8tres a un nom.
 web_cannot_switch_tab=Vous ne pouvez pas basculer car ces donn\u00E9es ne peuvent \u00EAtre converties.\nVider les donn\u00E9es pour basculer.
 web_parameters_lost_message=Basculer vers les Donn\u00E9es POST brutes va convertir en format brut\net perdre le format tabulaire quand vous s\u00E9lectionnerez un autre noeud\nou \u00E0 la sauvegarde du plan de test, \u00EAtes-vous s\u00FBr ?
 web_proxy_server_title=Requ\u00EAte via un serveur proxy
 web_request=Requ\u00EAte HTTP
 web_server=Serveur web
 web_server_client=Impl\u00E9mentation client \:
 web_server_domain=Nom ou adresse IP \:
 web_server_port=Port \:
 web_server_timeout_connect=Connexion \:
 web_server_timeout_response=R\u00E9ponse \:
 web_server_timeout_title=D\u00E9lai expiration (ms)
 web_testing2_title=Requ\u00EAte HTTP HTTPClient
 web_testing_advanced=Avanc\u00E9e
 web_testing_basic=Basique
 web_testing_concurrent_download=T\u00E9l\u00E9chargements en parall\u00E8le. Nombre \:
 web_testing_embedded_url_pattern=Les URL \u00E0 inclure doivent correspondre \u00E0 \:
 web_testing_retrieve_images=R\u00E9cup\u00E9rer les ressources incluses
 web_testing_retrieve_title=Ressources incluses dans les pages HTML
 web_testing_source_ip=Adresse source
 web_testing_source_ip_device=Interface
 web_testing_source_ip_device_ipv4=Interface IPv4
 web_testing_source_ip_device_ipv6=Interface IPv6
 web_testing_source_ip_hostname=IP/Nom d'h\u00F4te
 web_testing_title=Requ\u00EAte HTTP
 while_controller_label=Condition (fonction ou variable) \:
 while_controller_title=Contr\u00F4leur Tant Que
 workbench_title=Plan de travail
 xml_assertion_title=Assertion XML
 xml_download_dtds=R\u00E9cup\u00E9rer les DTD externes
 xml_namespace_button=Utiliser les espaces de noms
 xml_tolerant_button=Utiliser Tidy (analyseur tol\u00E9rant)
 xml_validate_button=Validation XML
 xml_whitespace_button=Ignorer les espaces
 xmlschema_assertion_label=Nom de fichier \: 
 xmlschema_assertion_title=Assertion Sch\u00E9ma XML
 xpath_assertion_button=Valider
 xpath_assertion_check=V\u00E9rifier l'expression XPath
 xpath_assertion_error=Erreur avec XPath
 xpath_assertion_failed=Expression XPath invalide
 xpath_assertion_label=XPath
 xpath_assertion_negate=Vrai si aucune correspondance trouv\u00E9e
 xpath_assertion_option=Options d'analyse XML
 xpath_assertion_test=V\u00E9rificateur XPath
 xpath_assertion_tidy=Essayer et nettoyer l'entr\u00E9e
 xpath_assertion_title=Assertion XPath
 xpath_assertion_valid=Expression XPath valide
 xpath_assertion_validation=Valider le code XML \u00E0 travers le fichier DTD
 xpath_assertion_whitespace=Ignorer les espaces
 xpath_expression=Expression XPath de correspondance
 xpath_extractor_fragment=Retourner le fragment XPath entier au lieu du contenu
 xpath_extractor_query=Requ\u00EAte XPath \:
 xpath_extractor_title=Extracteur XPath
 xpath_file_file_name=Fichier XML contenant les valeurs
 xpath_tester=Testeur XPath
 xpath_tester_button_test=Tester
 xpath_tester_field=Expression XPath
 xpath_tester_fragment=Retourner le fragment XPath entier au lieu du contenu ?
 xpath_tester_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 xpath_tester_title=Testeur XPath
 xpath_tidy_quiet=Silencieux
 xpath_tidy_report_errors=Rapporter les erreurs
 xpath_tidy_show_warnings=Afficher les alertes
 you_must_enter_a_valid_number=Vous devez entrer un nombre valide
 zh_cn=Chinois (simplifi\u00E9)
 zh_tw=Chinois (traditionnel)
diff --git a/src/core/org/apache/jmeter/threads/gui/AbstractThreadGroupGui.java b/src/core/org/apache/jmeter/threads/gui/AbstractThreadGroupGui.java
index e1f442212..b946108a9 100644
--- a/src/core/org/apache/jmeter/threads/gui/AbstractThreadGroupGui.java
+++ b/src/core/org/apache/jmeter/threads/gui/AbstractThreadGroupGui.java
@@ -1,211 +1,211 @@
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
 
 package org.apache.jmeter.threads.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.util.Arrays;
 import java.util.Collection;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.ButtonGroup;
 import javax.swing.JMenuItem;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.JRadioButton;
 
 import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 
 public abstract class AbstractThreadGroupGui extends AbstractJMeterGuiComponent {
     private static final long serialVersionUID = 240L;
 
     // Sampler error action buttons
     private JRadioButton continueBox;
 
     private JRadioButton startNextLoop;
 
     private JRadioButton stopThrdBox;
 
     private JRadioButton stopTestBox;
 
     private JRadioButton stopTestNowBox;
 
     public AbstractThreadGroupGui(){
         super();
         init();
         initGui();
     }
 
     @Override
     public Collection<String> getMenuCategories() {
         return Arrays.asList(MenuFactory.THREADS);
     }
 
     @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         pop.add(MenuFactory.makeMenus(new String[] {
                 MenuFactory.CONTROLLERS,
                 MenuFactory.CONFIG_ELEMENTS,
                 MenuFactory.TIMERS,
                 MenuFactory.PRE_PROCESSORS,
                 MenuFactory.SAMPLERS,
                 MenuFactory.POST_PROCESSORS,
                 MenuFactory.ASSERTIONS,
                 MenuFactory.LISTENERS,
                 },
                 JMeterUtils.getResString("add"), // $NON-NLS-1$
                 ActionNames.ADD));
         
         if(this.isEnabled() && 
                 // Check test is not started already
-                JMeterContextService.getTestStartTime()==0) {
+                !JMeterUtils.isTestRunning()) {
             pop.addSeparator();
             JMenuItem runTg = new JMenuItem(JMeterUtils.getResString("run_threadgroup"));
             runTg.setName("run_threadgroup");
             runTg.addActionListener(ActionRouter.getInstance());
             runTg.setActionCommand(ActionNames.RUN_TG);
             pop.add(runTg);
     
             JMenuItem runTgNotimers = new JMenuItem(JMeterUtils.getResString("run_threadgroup_no_timers"));
             runTgNotimers.setName("run_threadgroup_no_timers");
             runTgNotimers.addActionListener(ActionRouter.getInstance());
             runTgNotimers.setActionCommand(ActionNames.RUN_TG_NO_TIMERS);
             pop.add(runTgNotimers);
 
             JMenuItem validateTg = new JMenuItem(JMeterUtils.getResString("validate_threadgroup"));
             validateTg.setName("validate_threadgroup");
             validateTg.addActionListener(ActionRouter.getInstance());
             validateTg.setActionCommand(ActionNames.VALIDATE_TG);
             pop.add(validateTg);
         }
         
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop, false);
         return pop;
     }
 
     @Override
     public Dimension getPreferredSize() {
         return getMinimumSize();
     }
 
     @Override
     public void clearGui(){
         super.clearGui();
         initGui();
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
 
         Box box = Box.createVerticalBox();
         box.add(makeTitlePanel());
         box.add(createOnErrorPanel());
         add(box, BorderLayout.NORTH);
     }
     
     private void initGui() {
         continueBox.setSelected(true);
     }
 
     private JPanel createOnErrorPanel() {
         JPanel panel = new JPanel();
         panel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("sampler_on_error_action"))); // $NON-NLS-1$
 
         ButtonGroup group = new ButtonGroup();
 
         continueBox = new JRadioButton(JMeterUtils.getResString("sampler_on_error_continue")); // $NON-NLS-1$
         group.add(continueBox);
         panel.add(continueBox);
 
         startNextLoop = new JRadioButton(JMeterUtils.getResString("sampler_on_error_start_next_loop")); // $NON-NLS-1$
         group.add(startNextLoop);
         panel.add(startNextLoop);
 
         stopThrdBox = new JRadioButton(JMeterUtils.getResString("sampler_on_error_stop_thread")); // $NON-NLS-1$
         group.add(stopThrdBox);
         panel.add(stopThrdBox);
 
         stopTestBox = new JRadioButton(JMeterUtils.getResString("sampler_on_error_stop_test")); // $NON-NLS-1$
         group.add(stopTestBox);
         panel.add(stopTestBox);
 
         stopTestNowBox = new JRadioButton(JMeterUtils.getResString("sampler_on_error_stop_test_now")); // $NON-NLS-1$
         group.add(stopTestNowBox);
         panel.add(stopTestNowBox);
 
         return panel;
     }
 
     private void setSampleErrorBoxes(AbstractThreadGroup te) {
         if (te.getOnErrorStopTest()) {
             stopTestBox.setSelected(true);
         } else if (te.getOnErrorStopTestNow()) {
             stopTestNowBox.setSelected(true);
         } else if (te.getOnErrorStopThread()) {
             stopThrdBox.setSelected(true);
         } else if (te.getOnErrorStartNextLoop()) {
             startNextLoop.setSelected(true);
         } else {
             continueBox.setSelected(true);
         }
     }
 
     private String onSampleError() {
         if (stopTestBox.isSelected()) {
             return AbstractThreadGroup.ON_SAMPLE_ERROR_STOPTEST;
         }
         if (stopTestNowBox.isSelected()) {
             return AbstractThreadGroup.ON_SAMPLE_ERROR_STOPTEST_NOW;
         }
         if (stopThrdBox.isSelected()) {
             return AbstractThreadGroup.ON_SAMPLE_ERROR_STOPTHREAD;
         }
         if (startNextLoop.isSelected()) {
             return AbstractThreadGroup.ON_SAMPLE_ERROR_START_NEXT_LOOP;
         }
 
         // Defaults to continue
         return AbstractThreadGroup.ON_SAMPLE_ERROR_CONTINUE;
     }
 
    @Override
     public void configure(TestElement tg) {
         super.configure(tg);
         setSampleErrorBoxes((AbstractThreadGroup) tg);
     }
     
    @Override
     protected void configureTestElement(TestElement tg) {
         super.configureTestElement(tg);
         tg.setProperty(new StringProperty(AbstractThreadGroup.ON_SAMPLE_ERROR, onSampleError()));
     }
 
 }
diff --git a/src/core/org/apache/jmeter/util/JMeterUtils.java b/src/core/org/apache/jmeter/util/JMeterUtils.java
index 4bb760c5a..e1c7b3043 100644
--- a/src/core/org/apache/jmeter/util/JMeterUtils.java
+++ b/src/core/org/apache/jmeter/util/JMeterUtils.java
@@ -1,1450 +1,1458 @@
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
 
 package org.apache.jmeter.util;
 
 import java.awt.Dimension;
 import java.awt.HeadlessException;
 import java.awt.event.ActionListener;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.lang.reflect.InvocationTargetException;
 import java.net.InetAddress;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.MissingResourceException;
 import java.util.Properties;
 import java.util.ResourceBundle;
 import java.util.Vector;
 import java.util.concurrent.ThreadLocalRandom;
 
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JComboBox;
 import javax.swing.JOptionPane;
 import javax.swing.JTable;
 import javax.swing.SwingUtilities;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.gui.GuiPackage;
+import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.test.UnitTestManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.PatternCacheLRU;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.xml.sax.XMLReader;
 
 /**
  * This class contains the static utility methods used by JMeter.
  *
  */
 public class JMeterUtils implements UnitTestManager {
     private static final Logger log = LoggingManager.getLoggerForClass();
     
     // Note: cannot use a static variable here, because that would be processed before the JMeter properties
     // have been defined (Bug 52783)
     private static class LazyPatternCacheHolder {
         public static final PatternCacheLRU INSTANCE = new PatternCacheLRU(
                 getPropDefault("oro.patterncache.size",1000), // $NON-NLS-1$
                 new Perl5Compiler());
     }
 
     private static final String EXPERT_MODE_PROPERTY = "jmeter.expertMode"; // $NON-NLS-1$
     
     private static final String ENGLISH_LANGUAGE = Locale.ENGLISH.getLanguage();
 
     private static volatile Properties appProperties;
 
     private static final Vector<LocaleChangeListener> localeChangeListeners = new Vector<>();
 
     private static volatile Locale locale;
 
     private static volatile ResourceBundle resources;
 
     // What host am I running on?
 
     //@GuardedBy("this")
     private static String localHostIP = null;
     //@GuardedBy("this")
     private static String localHostName = null;
     //@GuardedBy("this")
     private static String localHostFullName = null;
 
     private static volatile boolean ignoreResorces = false; // Special flag for use in debugging resources
 
     private static final ThreadLocal<Perl5Matcher> localMatcher = new ThreadLocal<Perl5Matcher>() {
         @Override
         protected Perl5Matcher initialValue() {
             return new Perl5Matcher();
         }
     };
 
     /**
      * Gets Perl5Matcher for this thread.
      * @return the {@link Perl5Matcher} for this thread
      */
     public static Perl5Matcher getMatcher() {
         return localMatcher.get();
     }
 
     /**
      * This method is used by the init method to load the property file that may
      * even reside in the user space, or in the classpath under
      * org.apache.jmeter.jmeter.properties.
      *
      * The method also initialises logging and sets up the default Locale
      *
      * TODO - perhaps remove?
      * [still used
      *
      * @param file
      *            the file to load
      * @return the Properties from the file
      * @see #getJMeterProperties()
      * @see #loadJMeterProperties(String)
      * @see #initLogging()
      * @see #initLocale()
      */
     public static Properties getProperties(String file) {
         loadJMeterProperties(file);
         initLogging();
         initLocale();
         return appProperties;
     }
 
     /**
      * Initialise JMeter logging
      */
     public static void initLogging() {
         LoggingManager.initializeLogging(appProperties);
     }
 
     /**
      * Initialise the JMeter Locale
      */
     public static void initLocale() {
         String loc = appProperties.getProperty("language"); // $NON-NLS-1$
         if (loc != null) {
             String []parts = JOrphanUtils.split(loc,"_");// $NON-NLS-1$
             if (parts.length==2) {
                 setLocale(new Locale(parts[0], parts[1]));
             } else {
                 setLocale(new Locale(loc, "")); // $NON-NLS-1$
             }
 
         } else {
             setLocale(Locale.getDefault());
         }
     }
 
 
     /**
      * Load the JMeter properties file; if not found, then
      * default to "org/apache/jmeter/jmeter.properties" from the classpath
      *
      * <p>
      * c.f. loadProperties
      *
      * @param file Name of the file from which the JMeter properties should be loaded
      */
     public static void loadJMeterProperties(String file) {
         Properties p = new Properties(System.getProperties());
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 is =
                     ClassLoader.getSystemResourceAsStream("org/apache/jmeter/jmeter.properties"); // $NON-NLS-1$
                 if (is == null) {
                     throw new RuntimeException("Could not read JMeter properties file:"+file);
                 }
                 p.load(is);
             } catch (IOException ex) {
                 // JMeter.fail("Could not read internal resource. " +
                 // "Archive is broken.");
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         appProperties = p;
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @return the Properties from the file, may be null (e.g. file not found)
      */
     public static Properties loadProperties(String file) {
         return loadProperties(file, null);
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @param defaultProps a set of default properties
      * @return the Properties from the file; if it could not be processed, the defaultProps are returned.
      */
     public static Properties loadProperties(String file, Properties defaultProps) {
         Properties p = new Properties(defaultProps);
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 final URL resource = JMeterUtils.class.getClassLoader().getResource(file);
                 if (resource == null) {
                     log.warn("Cannot find " + file);
                     return defaultProps;
                 }
                 is = resource.openStream();
                 if (is == null) {
                     log.warn("Cannot open " + file);
                     return defaultProps;
                 }
                 p.load(is);
             } catch (IOException ex) {
                 log.warn("Error reading " + file + " " + ex.toString());
                 return defaultProps;
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         return p;
     }
 
     public static PatternCacheLRU getPatternCache() {
         return LazyPatternCacheHolder.INSTANCE;
     }
 
     /**
      * Get a compiled expression from the pattern cache (READ_ONLY).
      *
      * @param expression regular expression to be looked up
      * @return compiled pattern
      *
      * @throws MalformedCachePatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression) throws MalformedCachePatternException {
         return getPattern(expression, Perl5Compiler.READ_ONLY_MASK);
     }
 
     /**
      * Get a compiled expression from the pattern cache.
      *
      * @param expression RE
      * @param options e.g. {@link Perl5Compiler#READ_ONLY_MASK READ_ONLY_MASK}
      * @return compiled pattern
      *
      * @throws MalformedCachePatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression, int options) throws MalformedCachePatternException {
         return LazyPatternCacheHolder.INSTANCE.getPattern(expression, options);
     }
 
     @Override
     public void initializeProperties(String file) {
         System.out.println("Initializing Properties: " + file);
         getProperties(file);
     }
 
     /**
      * Convenience method for
      * {@link ClassFinder#findClassesThatExtend(String[], Class[], boolean)}
      * with the option to include inner classes in the search set to false
      * and the path list is derived from JMeterUtils.getSearchPaths().
      *
      * @param superClass - single class to search for
      * @return List of Strings containing discovered class names.
      * @throws IOException when the used {@link ClassFinder} throws one while searching for the class
      */
     public static List<String> findClassesThatExtend(Class<?> superClass)
         throws IOException {
         return ClassFinder.findClassesThatExtend(getSearchPaths(), new Class[]{superClass}, false);
     }
 
     /**
      * Generate a list of paths to search.
      * The output array always starts with
      * JMETER_HOME/lib/ext
      * and is followed by any paths obtained from the "search_paths" JMeter property.
      * 
      * @return array of path strings
      */
     public static String[] getSearchPaths() {
         String p = JMeterUtils.getPropDefault("search_paths", null); // $NON-NLS-1$
         String[] result = new String[1];
 
         if (p != null) {
             String[] paths = p.split(";"); // $NON-NLS-1$
             result = new String[paths.length + 1];
             System.arraycopy(paths, 0, result, 1, paths.length);
         }
         result[0] = getJMeterHome() + "/lib/ext"; // $NON-NLS-1$
         return result;
     }
 
     /**
      * Provide random numbers
      *
      * @param r -
      *            the upper bound (exclusive)
      * @return a random <code>int</code>
      */
     public static int getRandomInt(int r) {
         return ThreadLocalRandom.current().nextInt(r);
     }
 
     /**
      * Changes the current locale: re-reads resource strings and notifies
      * listeners.
      *
      * @param loc -
      *            new locale
      */
     public static void setLocale(Locale loc) {
         log.info("Setting Locale to " + loc.toString());
         /*
          * See bug 29920. getBundle() defaults to the property file for the
          * default Locale before it defaults to the base property file, so we
          * need to change the default Locale to ensure the base property file is
          * found.
          */
         Locale def = null;
         boolean isDefault = false; // Are we the default language?
         if (loc.getLanguage().equals(ENGLISH_LANGUAGE)) {
             isDefault = true;
             def = Locale.getDefault();
             // Don't change locale from en_GB to en
             if (!def.getLanguage().equals(ENGLISH_LANGUAGE)) {
                 Locale.setDefault(Locale.ENGLISH);
             } else {
                 def = null; // no need to reset Locale
             }
         }
         if (loc.toString().equals("ignoreResources")){ // $NON-NLS-1$
             log.warn("Resource bundles will be ignored");
             ignoreResorces = true;
             // Keep existing settings
         } else {
             ignoreResorces = false;
             ResourceBundle resBund = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", loc); // $NON-NLS-1$
             resources = resBund;
             locale = loc;
             final Locale resBundLocale = resBund.getLocale();
             if (isDefault || resBundLocale.equals(loc)) {// language change worked
             // Check if we at least found the correct language:
             } else if (resBundLocale.getLanguage().equals(loc.getLanguage())) {
                 log.info("Could not find resources for '"+loc.toString()+"', using '"+resBundLocale.toString()+"'");
             } else {
                 log.error("Could not find resources for '"+loc.toString()+"'");
             }
         }
         notifyLocaleChangeListeners();
         /*
          * Reset Locale if necessary so other locales are properly handled
          */
         if (def != null) {
             Locale.setDefault(def);
         }
     }
 
     /**
      * Gets the current locale.
      *
      * @return current locale
      */
     public static Locale getLocale() {
         return locale;
     }
 
     public static void addLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.add(listener);
     }
 
     public static void removeLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.remove(listener);
     }
 
     /**
      * Notify all listeners interested in locale changes.
      *
      */
     private static void notifyLocaleChangeListeners() {
         LocaleChangeEvent event = new LocaleChangeEvent(JMeterUtils.class, locale);
         @SuppressWarnings("unchecked") // clone will produce correct type
         // TODO but why do we need to clone the list?
         // ANS: to avoid possible ConcurrentUpdateException when unsubscribing
         // Could perhaps avoid need to clone by using a modern concurrent list
         Vector<LocaleChangeListener> listeners = (Vector<LocaleChangeListener>) localeChangeListeners.clone();
         for (LocaleChangeListener listener : listeners) {
             listener.localeChanged(event);
         }
     }
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      */
     public static String getResString(String key) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]"); // $NON-NLS-1$
     }
     
     /**
      * Gets the resource string for this key in Locale.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param forcedLocale Force a particular locale
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      * @since 2.7
      */
     public static String getResString(String key, Locale forcedLocale) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]", // $NON-NLS-1$
                 forcedLocale); 
     }
 
     public static final String RES_KEY_PFX = "[res_key="; // $NON-NLS-1$
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param defaultValue -
      *            the default value
      *
      * @return the resource string if the key is found; otherwise, return the
      *         default
      * @deprecated Only intended for use in development; use
      *             getResString(String) normally
      */
     @Deprecated
     public static String getResString(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue);
     }
 
     /*
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue, null);
     }
     /*
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue, Locale forcedLocale) {
         if (key == null) {
             return null;
         }
         // Resource keys cannot contain spaces, and are forced to lower case
         String resKey = key.replace(' ', '_'); // $NON-NLS-1$ // $NON-NLS-2$
         resKey = resKey.toLowerCase(java.util.Locale.ENGLISH);
         String resString = null;
         try {
             ResourceBundle bundle = resources;
             if(forcedLocale != null) {
                 bundle = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", forcedLocale); // $NON-NLS-1$
             }
             if (bundle.containsKey(resKey)) {
                 resString = bundle.getString(resKey);
             } else {
                 log.warn("ERROR! Resource string not found: [" + resKey + "]");
                 resString = defaultValue;                
             }
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "["+key+"]";
             }
         } catch (MissingResourceException mre) {
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "[?"+key+"?]";
             }
             log.warn("ERROR! Resource string not found: [" + resKey + "]", mre);
             resString = defaultValue;
         }
         return resString;
     }
 
     /**
      * To get I18N label from properties file
      * 
      * @param key
      *            in messages.properties
      * @return I18N label without (if exists) last colon ':' and spaces
      */
     public static String getParsedLabel(String key) {
         String value = JMeterUtils.getResString(key);
         return value.replaceFirst("(?m)\\s*?:\\s*$", ""); // $NON-NLS-1$ $NON-NLS-2$
     }
     
     /**
      * Get the locale name as a resource.
      * Does not log an error if the resource does not exist.
      * This is needed to support additional locales, as they won't be in existing messages files.
      *
      * @param locale name
      * @return the locale display name as defined in the current Locale or the original string if not present
      */
     public static String getLocaleString(String locale){
         // All keys in messages.properties are lowercase (historical reasons?)
         String resKey = locale.toLowerCase(java.util.Locale.ENGLISH);
         if (resources.containsKey(resKey)) {
             return resources.getString(resKey);
         }
         return locale;
     }
     /**
      * This gets the currently defined appProperties. It can only be called
      * after the {@link #getProperties(String)} or {@link #loadJMeterProperties(String)} 
      * method has been called.
      *
      * @return The JMeterProperties value, 
      *         may be null if {@link #loadJMeterProperties(String)} has not been called
      * @see #getProperties(String)
      * @see #loadJMeterProperties(String)
      */
     public static Properties getJMeterProperties() {
         return appProperties;
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images.&lt;name&gt;
      *
      * @param name
      *            Description of Parameter
      * @return The Image value
      */
     public static ImageIcon getImage(String name) {
         try {
             URL url = JMeterUtils.class.getClassLoader().getResource(
                     "org/apache/jmeter/images/" + name.trim());
             if(url != null) {
                 return new ImageIcon(url); // $NON-NLS-1$
             } else {
                 log.warn("no icon for " + name);
                 return null;                
             }
         } catch (NoClassDefFoundError | InternalError e) {// Can be returned by headless hosts
             log.info("no icon for " + name + " " + e.getMessage());
             return null;
         }
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images.<em>&lt;name&gt;</em>, and also sets the description
      * of the image, which is useful if the icon is going to be placed
      * on the clipboard.
      *
      * @param name
      *            the name of the image
      * @param description
      *            the description of the image
      * @return The Image value
      */
     public static ImageIcon getImage(String name, String description) {
         ImageIcon icon = getImage(name);
         if(icon != null) {
             icon.setDescription(description);
         }
         return icon;
     }
 
     public static String getResourceFileAsText(String name) {
         BufferedReader fileReader = null;
         try {
             String lineEnd = System.getProperty("line.separator"); // $NON-NLS-1$
             InputStream is = JMeterUtils.class.getClassLoader().getResourceAsStream(name);
             if(is != null) {
                 fileReader = new BufferedReader(new InputStreamReader(is));
                 StringBuilder text = new StringBuilder();
                 String line;
                 while ((line = fileReader.readLine()) != null) {
                     text.append(line);
                     text.append(lineEnd);
                 }
                 // Done by finally block: fileReader.close();
                 return text.toString();
             } else {
                 return ""; // $NON-NLS-1$                
             }
         } catch (IOException e) {
             return ""; // $NON-NLS-1$
         } finally {
             IOUtils.closeQuietly(fileReader);
         }
     }
 
     /**
      * Creates the vector of Timers plugins.
      *
      * @param properties
      *            Description of Parameter
      * @return The Timers value
      * @deprecated (3.0) not used + pre-java 1.2 collection
      */
     @Deprecated
     public static Vector<Object> getTimers(Properties properties) {
         return instantiate(getVector(properties, "timer."), // $NON-NLS-1$
                 "org.apache.jmeter.timers.Timer"); // $NON-NLS-1$
     }
 
     /**
      * Creates the vector of visualizer plugins.
      *
      * @param properties
      *            Description of Parameter
      * @return The Visualizers value
      * @deprecated (3.0) not used + pre-java 1.2 collection
      */
     @Deprecated
     public static Vector<Object> getVisualizers(Properties properties) {
         return instantiate(getVector(properties, "visualizer."), // $NON-NLS-1$
                 "org.apache.jmeter.visualizers.Visualizer"); // $NON-NLS-1$
     }
 
     /**
      * Creates a vector of SampleController plugins.
      *
      * @param properties
      *            The properties with information about the samplers
      * @return The Controllers value
      * @deprecated (3.0) not used + pre-java 1.2 collection
      */
     // TODO - does not appear to be called directly
     @Deprecated
     public static Vector<Object> getControllers(Properties properties) {
         String name = "controller."; // $NON-NLS-1$
         Vector<Object> v = new Vector<>();
         Enumeration<?> names = properties.keys();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(name)) {
                 Object o = instantiate(properties.getProperty(prop),
                         "org.apache.jmeter.control.SamplerController"); // $NON-NLS-1$
                 v.addElement(o);
             }
         }
         return v;
     }
 
     /**
      * Create a string of class names for a particular SamplerController
      *
      * @param properties
      *            The properties with info about the samples.
      * @param name
      *            The name of the sampler controller.
      * @return The TestSamples value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static String[] getTestSamples(Properties properties, String name) {
         Vector<String> vector = getVector(properties, name + ".testsample"); // $NON-NLS-1$
         return vector.toArray(new String[vector.size()]);
     }
 
     /**
      * Create an instance of an org.xml.sax.Parser based on the default props.
      *
      * @return The XMLParser value
      * @deprecated (3.0) was only called by UserParameterXMLParser.getXMLParameters which has been removed in 3.0
      */
     @Deprecated
     public static XMLReader getXMLParser() {
         final String parserName = getPropDefault("xml.parser", // $NON-NLS-1$
                 "org.apache.xerces.parsers.SAXParser");  // $NON-NLS-1$
         return (XMLReader) instantiate(parserName,
                 "org.xml.sax.XMLReader"); // $NON-NLS-1$
     }
 
     /**
      * Creates the vector of alias strings.
      * <p>
      * The properties will be filtered by all values starting with
      * <code>alias.</code>. The matching entries will be used for the new
      * {@link Hashtable} while the prefix <code>alias.</code> will be stripped
      * of the keys.
      *
      * @param properties
      *            the input values
      * @return The Alias value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static Hashtable<String, String> getAlias(Properties properties) {
         return getHashtable(properties, "alias."); // $NON-NLS-1$
     }
 
     /**
      * Creates a vector of strings for all the properties that start with a
      * common prefix.
      *
      * @param properties
      *            Description of Parameter
      * @param name
      *            Description of Parameter
      * @return The Vector value
      */
     public static Vector<String> getVector(Properties properties, String name) {
         Vector<String> v = new Vector<>();
         Enumeration<?> names = properties.keys();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(name)) {
                 v.addElement(properties.getProperty(prop));
             }
         }
         return v;
     }
 
     /**
      * Creates a table of strings for all the properties that start with a
      * common prefix.
      * <p>
      * So if you have {@link Properties} <code>prop</code> with two entries, say
      * <ul>
      * <li>this.test</li>
      * <li>that.something</li>
      * </ul>
      * And would call this method with a <code>prefix</code> <em>this</em>, the
      * result would be a new {@link Hashtable} with one entry, which key would
      * be <em>test</em>.
      *
      * @param properties
      *            input to search
      * @param prefix
      *            to match against properties
      * @return a Hashtable where the keys are the original matching keys with
      *         the prefix removed
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static Hashtable<String, String> getHashtable(Properties properties, String prefix) {
         Hashtable<String, String> t = new Hashtable<>();
         Enumeration<?> names = properties.keys();
         final int length = prefix.length();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(prefix)) {
                 t.put(prop.substring(length), properties.getProperty(prop));
             }
         }
         return t;
     }
 
     /**
      * Get a int value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static int getPropDefault(String propName, int defaultVal) {
         int ans;
         try {
             ans = Integer.parseInt(appProperties.getProperty(propName, Integer.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching int property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a boolean value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static boolean getPropDefault(String propName, boolean defaultVal) {
         boolean ans;
         try {
             String strVal = appProperties.getProperty(propName, Boolean.toString(defaultVal)).trim();
             if (strVal.equalsIgnoreCase("true") || strVal.equalsIgnoreCase("t")) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = true;
             } else if (strVal.equalsIgnoreCase("false") || strVal.equalsIgnoreCase("f")) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = false;
             } else {
                 ans = Integer.parseInt(strVal) == 1;
             }
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching boolean property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a long value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static long getPropDefault(String propName, long defaultVal) {
         long ans;
         try {
             ans = Long.parseLong(appProperties.getProperty(propName, Long.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching long property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
     
     /**
      * Get a float value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static float getPropDefault(String propName, float defaultVal) {
         float ans;
         try {
             ans = Float.parseFloat(appProperties.getProperty(propName, Float.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching float property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a String value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value applying a trim on it
      */
     public static String getPropDefault(String propName, String defaultVal) {
         String ans = defaultVal;
         try 
         {
             String value = appProperties.getProperty(propName, defaultVal);
             if(value != null) {
                 ans = value.trim();
             }
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching String property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
     
     /**
      * Get the value of a JMeter property.
      *
      * @param propName
      *            the name of the property.
      * @return the value of the JMeter property, or null if not defined
      */
     public static String getProperty(String propName) {
         String ans = null;
         try {
             ans = appProperties.getProperty(propName);
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching String property:'"+propName+"'");
             ans = null;
         }
         return ans;
     }
 
     /**
      * Set a String value
      *
      * @param propName
      *            the name of the property.
      * @param propValue
      *            the value of the property
      * @return the previous value of the property
      */
     public static Object setProperty(String propName, String propValue) {
         return appProperties.setProperty(propName, propValue);
     }
 
     /**
      * Sets the selection of the JComboBox to the Object 'name' from the list in
      * namVec.
      * NOTUSED?
      * @param properties not used at the moment
      * @param combo {@link JComboBox} to work on
      * @param namVec List of names, which are displayed in <code>combo</code>
      * @param name Name, that is to be selected. It has to be in <code>namVec</code>
      */
     @Deprecated
     public static void selJComboBoxItem(Properties properties, JComboBox<?> combo, Vector<?> namVec, String name) {
         int idx = namVec.indexOf(name);
         combo.setSelectedIndex(idx);
         // Redisplay.
         combo.updateUI();
     }
 
     /**
      * Instatiate an object and guarantee its class.
      *
      * @param className
      *            The name of the class to instantiate.
      * @param impls
      *            The name of the class it must be an instance of
      * @return an instance of the class, or null if instantiation failed or the class did not implement/extend as required
      * @deprecated (3.0) not used out of this class
      */
     // TODO probably not needed
     @Deprecated
     public static Object instantiate(String className, String impls) {
         if (className != null) {
             className = className.trim();
         }
 
         if (impls != null) {
             impls = impls.trim();
         }
 
         try {
             Class<?> c = Class.forName(impls);
             try {
                 Class<?> o = Class.forName(className);
                 Object res = o.newInstance();
                 if (c.isInstance(res)) {
                     return res;
                 }
                 throw new IllegalArgumentException(className + " is not an instance of " + impls);
             } catch (ClassNotFoundException e) {
                 log.error("Error loading class " + className + ": class is not found");
             } catch (IllegalAccessException e) {
                 log.error("Error loading class " + className + ": does not have access");
             } catch (InstantiationException e) {
                 log.error("Error loading class " + className + ": could not instantiate");
             } catch (NoClassDefFoundError e) {
                 log.error("Error loading class " + className + ": couldn't find class " + e.getMessage());
             }
         } catch (ClassNotFoundException e) {
             log.error("Error loading class " + impls + ": was not found.");
         }
         return null;
     }
 
     /**
      * Instantiate a vector of classes
      *
      * @param v
      *            Description of Parameter
      * @param className
      *            Description of Parameter
      * @return Description of the Returned Value
      * @deprecated (3.0) not used out of this class
      */
     @Deprecated
     public static Vector<Object> instantiate(Vector<String> v, String className) {
         Vector<Object> i = new Vector<>();
         try {
             Class<?> c = Class.forName(className);
             Enumeration<String> elements = v.elements();
             while (elements.hasMoreElements()) {
                 String name = elements.nextElement();
                 try {
                     Object o = Class.forName(name).newInstance();
                     if (c.isInstance(o)) {
                         i.addElement(o);
                     }
                 } catch (ClassNotFoundException e) {
                     log.error("Error loading class " + name + ": class is not found");
                 } catch (IllegalAccessException e) {
                     log.error("Error loading class " + name + ": does not have access");
                 } catch (InstantiationException e) {
                     log.error("Error loading class " + name + ": could not instantiate");
                 } catch (NoClassDefFoundError e) {
                     log.error("Error loading class " + name + ": couldn't find class " + e.getMessage());
                 }
             }
         } catch (ClassNotFoundException e) {
             log.error("Error loading class " + className + ": class is not found");
         }
         return i;
     }
 
     /**
      * Create a button with the netscape style
      *
      * @param name
      *            Description of Parameter
      * @param listener
      *            Description of Parameter
      * @return Description of the Returned Value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static JButton createButton(String name, ActionListener listener) {
         JButton button = new JButton(getImage(name + ".on.gif")); // $NON-NLS-1$
         button.setDisabledIcon(getImage(name + ".off.gif")); // $NON-NLS-1$
         button.setRolloverIcon(getImage(name + ".over.gif")); // $NON-NLS-1$
         button.setPressedIcon(getImage(name + ".down.gif")); // $NON-NLS-1$
         button.setActionCommand(name);
         button.addActionListener(listener);
         button.setRolloverEnabled(true);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(false);
         button.setPreferredSize(new Dimension(24, 24));
         return button;
     }
 
     /**
      * Create a button with the netscape style
      *
      * @param name
      *            Description of Parameter
      * @param listener
      *            Description of Parameter
      * @return Description of the Returned Value
      * @deprecated (3.0) not used
      */
     @Deprecated
     public static JButton createSimpleButton(String name, ActionListener listener) {
         JButton button = new JButton(getImage(name + ".gif")); // $NON-NLS-1$
         button.setActionCommand(name);
         button.addActionListener(listener);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(false);
         button.setPreferredSize(new Dimension(25, 25));
         return button;
     }
 
 
     /**
      * Report an error through a dialog box.
      * Title defaults to "error_title" resource string
      * @param errorMsg - the error message.
      */
     public static void reportErrorToUser(String errorMsg) {
         reportErrorToUser(errorMsg, JMeterUtils.getResString("error_title")); // $NON-NLS-1$
     }
 
     /**
      * Report an error through a dialog box.
      *
      * @param errorMsg - the error message.
      * @param titleMsg - title string
      */
     public static void reportErrorToUser(String errorMsg, String titleMsg) {
         if (errorMsg == null) {
             errorMsg = "Unknown error - see log file";
             log.warn("Unknown error", new Throwable("errorMsg == null"));
         }
         GuiPackage instance = GuiPackage.getInstance();
         if (instance == null) {
             System.out.println(errorMsg);
             return; // Done
         }
         try {
             JOptionPane.showMessageDialog(instance.getMainFrame(),
                     errorMsg,
                     titleMsg,
                     JOptionPane.ERROR_MESSAGE);
         } catch (HeadlessException e) {
             log.warn("reportErrorToUser(\"" + errorMsg + "\") caused", e);
         }
     }
 
     /**
      * Finds a string in an array of strings and returns the
      *
      * @param array
      *            Array of strings.
      * @param value
      *            String to compare to array values.
      * @return Index of value in array, or -1 if not in array.
      * @deprecated (3.0) not used
      */
     //TODO - move to JOrphanUtils?
     @Deprecated
     public static int findInArray(String[] array, String value) {
         int count = -1;
         int index = -1;
         if (array != null && value != null) {
             while (++count < array.length) {
                 if (array[count] != null && array[count].equals(value)) {
                     index = count;
                     break;
                 }
             }
         }
         return index;
     }
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             }
             if (count + 1 < splittee.length && splittee[count + 1] != null) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
 
     // End Method
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @param def
      *            Default value to replace null values in array.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar, String def) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             } else {
                 retVal.append(def);
             }
             if (count + 1 < splittee.length) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
+    
+    /**
+     * @return true if test is running
+     */
+    public static boolean isTestRunning() {
+        return JMeterContextService.getTestStartTime()>0;
+    }
 
     /**
      * Get the JMeter home directory - does not include the trailing separator.
      *
      * @return the home directory
      */
     public static String getJMeterHome() {
         return jmDir;
     }
 
     /**
      * Get the JMeter bin directory - does not include the trailing separator.
      *
      * @return the bin directory
      */
     public static String getJMeterBinDir() {
         return jmBin;
     }
 
     public static void setJMeterHome(String home) {
         jmDir = home;
         jmBin = jmDir + File.separator + "bin"; // $NON-NLS-1$
     }
 
     // TODO needs to be synch? Probably not changed after threads have started
     private static String jmDir; // JMeter Home directory (excludes trailing separator)
     private static String jmBin; // JMeter bin directory (excludes trailing separator)
 
 
     /**
      * Gets the JMeter Version.
      *
      * @return the JMeter version string
      */
     public static String getJMeterVersion() {
         return JMeterVersion.getVERSION();
     }
 
     /**
      * Gets the JMeter copyright.
      *
      * @return the JMeter copyright string
      */
     public static String getJMeterCopyright() {
         return JMeterVersion.getCopyRight();
     }
 
     /**
      * Determine whether we are in 'expert' mode. Certain features may be hidden
      * from user's view unless in expert mode.
      *
      * @return true iif we're in expert mode
      */
     public static boolean isExpertMode() {
         return JMeterUtils.getPropDefault(EXPERT_MODE_PROPERTY, false);
     }
 
     /**
      * Find a file in the current directory or in the JMeter bin directory.
      *
      * @param fileName the name of the file to find
      * @return File object
      */
     public static File findFile(String fileName){
         File f =new File(fileName);
         if (!f.exists()){
             f=new File(getJMeterBinDir(),fileName);
         }
         return f;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostAddress()
      *
      * @return String representation of local IP address
      */
     public static synchronized String getLocalHostIP(){
         if (localHostIP == null) {
             getLocalHostDetails();
         }
         return localHostIP;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostName()
      *
      * @return local host name
      */
     public static synchronized String getLocalHostName(){
         if (localHostName == null) {
             getLocalHostDetails();
         }
         return localHostName;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getCanonicalHostName()
      *
      * @return local host name in canonical form
      */
     public static synchronized String getLocalHostFullName(){
         if (localHostFullName == null) {
             getLocalHostDetails();
         }
         return localHostFullName;
     }
 
     private static void getLocalHostDetails(){
         InetAddress localHost=null;
         try {
             localHost = InetAddress.getLocalHost();
         } catch (UnknownHostException e1) {
             log.error("Unable to get local host IP address.", e1);
             return; // TODO - perhaps this should be a fatal error?
         }
         localHostIP=localHost.getHostAddress();
         localHostName=localHost.getHostName();
         localHostFullName=localHost.getCanonicalHostName();
     }
     
     /**
      * Split line into name/value pairs and remove colon ':'
      * 
      * @param headers
      *            multi-line string headers
      * @return a map name/value for each header
      */
     public static LinkedHashMap<String, String> parseHeaders(String headers) {
         LinkedHashMap<String, String> linkedHeaders = new LinkedHashMap<>();
         String[] list = headers.split("\n"); // $NON-NLS-1$
         for (String header : list) {
             int colon = header.indexOf(':'); // $NON-NLS-1$
             if (colon <= 0) {
                 linkedHeaders.put(header, ""); // Empty value // $NON-NLS-1$
             } else {
                 linkedHeaders.put(header.substring(0, colon).trim(), header
                         .substring(colon + 1).trim());
             }
         }
         return linkedHeaders;
     }
 
     /**
      * Run the runnable in AWT Thread if current thread is not AWT thread
      * otherwise runs call {@link SwingUtilities#invokeAndWait(Runnable)}
      * @param runnable {@link Runnable}
      */
     public static void runSafe(Runnable runnable) {
         runSafe(true, runnable);
     }
 
     /**
      * Run the runnable in AWT Thread if current thread is not AWT thread
      * otherwise runs call {@link SwingUtilities#invokeAndWait(Runnable)}
      * @param synchronous flag, whether we will wait for the AWT Thread to finish its job.
      * @param runnable {@link Runnable}
      */
     public static void runSafe(boolean synchronous, Runnable runnable) {
         if(SwingUtilities.isEventDispatchThread()) {
             runnable.run();
         } else {
             if (synchronous) {
                 try {
                     SwingUtilities.invokeAndWait(runnable);
                 } catch (InterruptedException e) {
                     log.warn("Interrupted in thread "
                             + Thread.currentThread().getName(), e);
                 } catch (InvocationTargetException e) {
                     throw new Error(e);
                 }
             } else {
                 SwingUtilities.invokeLater(runnable);
             }
         }
     }
 
     /**
      * Help GC by triggering GC and finalization
      */
     public static void helpGC() {
         System.gc();
         System.runFinalization();
     }
 
     /**
      * Hack to make matcher clean the two internal buffers it keeps in memory which size is equivalent to 
      * the unzipped page size
      * @param matcher {@link Perl5Matcher}
      * @param pattern Pattern
      */
     public static void clearMatcherMemory(Perl5Matcher matcher, Pattern pattern) {
         try {
             if (pattern != null) {
                 matcher.matches("", pattern); // $NON-NLS-1$
             }
         } catch (Exception e) {
             // NOOP
         }
     }
 
     /**
      * Provide info, whether we run in HiDPI mode
      * @return {@code true} if we run in HiDPI mode, {@code false} otherwise
      */
     public static boolean getHiDPIMode() {
         return JMeterUtils.getPropDefault("jmeter.hidpi.mode", false);  // $NON-NLS-1$
     }
 
     /**
      * Provide info about the HiDPI scale factor
      * @return the factor by which we should scale elements for HiDPI mode
      */
     public static double getHiDPIScaleFactor() {
         return Double.parseDouble(JMeterUtils.getPropDefault("jmeter.hidpi.scale.factor", "1.0"));  // $NON-NLS-1$  $NON-NLS-2$
     }
 
     /**
      * Apply HiDPI mode management to {@link JTable}
      * @param table the {@link JTable} which should be adapted for HiDPI mode
      */
     public static void applyHiDPI(JTable table) {
         if (JMeterUtils.getHiDPIMode()) {
             table.setRowHeight((int) Math.round(table.getRowHeight() * JMeterUtils.getHiDPIScaleFactor()));
         }
     }
 
     /**
      * Return delimiterValue handling the TAB case
      * @param delimiterValue Delimited value 
      * @return String delimited modified to handle correctly tab
      * @throws JMeterError if delimiterValue has a length different from 1
      */
     public static String getDelimiter(String delimiterValue) {
         if (delimiterValue.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
             delimiterValue="\t";
         }
 
         if (delimiterValue.length() != 1){
             throw new JMeterError("Delimiter '"+delimiterValue+"' must be of length 1.");
         }
         return delimiterValue;
     }
 
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 5b993069b..876cffd8d 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,381 +1,382 @@
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
 <!ENTITY rarr     "&#x02192;" >
 <!ENTITY vellip   "&#x022EE;" >
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
 
 
 <!--  =================== 3.1 =================== -->
 
 <h1>Version 3.1</h1>
 
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
 
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>A cache for CSS Parsing of URLs has been introduced in this version, it is enabled by default. It is controlled by property <code>css.parser.cache.size</code>. It can be disabled by setting its value to <code>0</code>. See <bugzilla>59885</bugzilla></li>
     <li>ThroughputController defaults have changed. Now defaults are Percent Executions which is global and no more per user. See <bugzilla>60023</bugzilla></li>
     <li>Since version 3.1, HTML report ignores empty <code>Transaction Controller</code> (possibly generated by <code>If Controller</code> or <code>Throughput Controller</code>) when computing metrics. This provides more accurate metrics</li>
     <li>Since version 3.1, Summariser ignores SampleResults generated by <code>Transaction Controller</code> when computing the live statistics, see <bugzilla>60109</bugzilla></li>
     <li>Since version 3.1, when using Stripped modes (by default <code>StrippedBatch</code> is used), response will be stripped also for failing SampleResults, you can revert this to previous behaviour by setting <code>sample_sender_strip_also_on_error=false</code> in <code>user.properties</code>, see <bugzilla>60137</bugzilla></li>
     <li>Since version 3.1, <code>jmeter.save.saveservice.connect_time</code> property value is <code>true</code>, meaning CSV file for results will contain an additional column containing connection time, see <bugzilla>60106</bugzilla></li>
     <li>Since version 3.1, Random Timer subclasses (Gaussian Random Timer, Uniform Random Timer and Poisson Random Timer) implement interface <code><a href="./api/org/apache/jmeter/timers/ModifiableTimer.html">org.apache.jmeter.timers.ModifiableTimer</a></code></li>
     <li>Since version 3.1, if you don't select any language in JSR223 Test Elements, Apache Groovy language will be used. See <bugzilla>59945</bugzilla></li>
     <li>Since version 3.1, CSV DataSet now trims variable names to avoid issues due to spaces between variables names when configuring CSV DataSet. This should not have any impact for you unless you use space at the begining or end of your variable names. See <bugzilla>60221</bugzilla></li>
     <li>Since version 3.1, HTTP Request is able when using HttpClient4 (default) implementation to handle responses bigger than <code>2147483647</code>. To allow this 2 properties have been introduced:
     <ul>
         <li><code>httpsampler.max_bytes_to_store_per_request</code> (defaults to 10MB)will control what is held in memory, by default JMeter will only keep in memory the first 10MB of a response. If you have responses larger than this value and use assertions that are after the first 10MB, then you must increase this value</li>
         <li><code>httpsampler.max_buffer_size</code> will control the buffer used to read the data. Previously JMeter used a buffer equal to Content-Length header which could lead to failures and make JMeter less resistant to faulty applications, but note this may impact response times and give slightly different results
          than previous versions if your application returned a Content-Length header higher than current default value (65KB) </li>
     </ul>
     See <bugzilla>53039</bugzilla></li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <ul>
     <li><bug>60222</bug>Remove deprecated elements Distribution Graph, Spline Visualizer</li>
     <li><bug>60224</bug>Deprecate <code><a href="./usermanual/component_reference.html#Monitor_Results_(DEPRECATED)">Monitor Results</a></code> listener. It will be dropped in next version</li>
     <li><bug>60225</bug>Drop deprecated <code>__jexl</code> function, jexl support in BSF and dependency on <code>commons-jexl-1.1.jar</code> This function can be easily replaced with <code><a href="./usermanual/functions.html#__jexl3">__jexl3</a></code> function</li>
     <li><bug>60268</bug>Drop org.apache.jmeter.gui.action.Analyze and deprecate org.apache.jmeter.reporters.FileReporter (will be removed in next version)</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59882</bug>Reduce memory allocations for better throughput. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com) through <pr>217</pr> and <pr>228</pr></li>
     <li><bug>59885</bug>Optimize css parsing for embedded resources download by introducing a cache. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com) through <pr>219</pr></li>
     <li><bug>60092</bug>Add shortened version of the PUT body to sampler result.</li>
     <li><bug>60229</bug>Add a new metric : sent_bytes. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>53039</bug>HTTP Request : Be able to handle responses which size exceeds <code>2147483647</code> bytes</li>
     <li><bug>60265</bug> - HTTP Request : In Files Upload Tab you cannot resize columns</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><pr>211</pr>Differentiate the timing for JDBC Sampler. Use latency and connect time. Contributed by Thomas Peyrard (thomas.peyrard at murex.com)</li>
     <li><bug>59620</bug>Fix button action in "JMS Publisher &rarr; Random File from folder specified below" to allow to select a directory</li>
     <li><bug>60066</bug>Handle CLOBs and BLOBs and limit them if necessary when storing them in result sampler.</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>59351</bug>Improve log/error/message for IncludeController. Partly contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>60023</bug>ThroughputController : Make "Percent Executions" and global the default values. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60082</bug>Validation mode : Be able to force Throughput Controller to run as if it was set to 100%</li>
     <li><bug>59349</bug>Trim spaces in input filename in IncludeController.</li>
     <li><bug>60081</bug>Interleave Controller : Add an option to alternate across threads</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>59953</bug>GraphiteBackendListener : Add Average metric. Partly contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>59975</bug>View Results Tree : Text renderer annoyingly scrolls down when content is bulky. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60109</bug>Summariser : Make it ignore TC generated SampleResult in its summary computations</li>
     <li><bug>59948</bug>Add a formatted and sane HTML source code render to View Results Tree</li>
     <li><bug>60252</bug>Add sent kbytes/s to Aggregate Report and Summary report</li>
     <li><bug>60267</bug>UX : In View Results Tree it should be possible to close the Configure popup by typing escape. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>59845</bug>Log messages about JSON Path mismatches at <code>debug</code> level instead of <code>error</code>.</li>
     <li><pr>212</pr>Allow multiple selection and delete in HTTP Authorization Manager. Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59816</bug><pr>213</pr>Allow multiple selection and delete in HTTP Header Manager. Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59967</bug>CSS/JQuery Extractor : Allow empty default value. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59974</bug>Response Assertion : Add button "<code>Add from clipboard</code>". Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60050</bug>CSV Data Set : Make it clear in the logs when a thread will exit due to this configuration</li>
     <li><bug>59962</bug>Cache Manager does not update expires date when response code is <code>304</code>.</li>
     <li><bug>60018</bug>Timer : Add a factor to apply on pauses. Partly based on a patch by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60203</bug>Use more available space for textarea in XPath Assertion.</li>
     <li><bug>60220</bug>Rename JSON Path Post Processor to JSON Extractor</li>
     <li><bug>60221</bug>CSV DataSet : trim variable names</li>
     <li><bug>59329</bug>Trim spaces in input filename in CSVDataSet.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>59963</bug>New function <code>__RandomFromMultipleVars</code>: Ability to compute a random value from values of one or more variables. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59991</bug>New function <code>__groovy</code> to evaluate Groovy Script. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
     <li><pr>214</pr>Add spanish translation for delayed starting of threads. Contributed by Asier Lostalé (asier.lostale at openbravo.com).</li>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>59954</bug>Web Report/Dashboard : Add average metric</li>
     <li><bug>59956</bug>Web Report / Dashboard : Add ability to generate a graph for a range of data</li>
     <li><bug>60065</bug>Report / Dashboard : Improve Dashboard Error Summary by adding response message to "Type of error". Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60079</bug>Report / Dashboard : Add a new "Response Time Overview" graph</li>
     <li><bug>60080</bug>Report / Dashboard : Add a new "Connect Time Over Time " graph. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60091</bug>Report / Dashboard : Have a new report containing min/max and percentiles graphs.</li>
     <li><bug>60108</bug>Report / Dashboard : In Requests Summary rounding is too aggressive</li>
     <li><bug>60098</bug>Report / Dashboard : Reduce default value for "<code>jmeter.reportgenerator.statistic_window</code>" to reduce memory impact</li>
     <li><bug>60115</bug>Add date format property for start/end date filter into Report generator</li>
     <li><bug>60171</bug>Report / Dashboard : Active Threads Over Time should stack lines to give the total amount of threads running</li>
     <li><bug>60250</bug>Report / Dashboard : Need to Add Sent KB/s in Statistics Report of HTML Dashboard</li>
 </ul>
 <h3>General</h3>
 <ul>
     <li><bug>59803</bug>Use <code>isValid()</code> method from JDBC driver, if no <code>validationQuery</code>
     is given in JDBC Connection Configuration.</li>
     <li><bug>57493</bug>Create a documentation page for properties</li>
     <li><bug>59924</bug>The log level of <em>XXX</em> package is set to <code>DEBUG</code> if <code>log_level.<em>XXXX</em></code> property value contains spaces, same for <code>__log</code> function</li>
     <li><bug>59777</bug>Extract SLF4J binding into its own jar and make it a JMeter lib.
     <note>If you get a warning about multiple SLF4J bindings on startup. Remove either the Apache JMeter provided binding
           <code>lib/ApacheJMeter_slf4j_logkit.jar</code>, or all of the other reported bindings.
           For more information you can have a look at <a href="http://www.slf4j.org/codes.html#multiple_bindings">SLF4Js own info page.</a></note>
     </li>
     <li><bug>60085</bug>Remove cache for prepared statements, as it didn't work with the current JDBC pool implementation and current JDBC drivers should support caching of prepared statements themselves.</li>
     <li><bug>60137</bug>In Distributed testing when using StrippedXXXX modes strip response also on error</li>
     <li><bug>60106</bug>Settings defaults : Switch "<code>jmeter.save.saveservice.connect_time</code>" to true (after 3.0)</li>
     <li><pr>229</pr> tiny memory allocation improvements. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59945</bug>For all JSR223 elements, if script language has not been chosen on the UI, the script will be interpreted as a groovy script.</li>
+    <li><bug>60266</bug>Usability/ UX : It should not be possible to close/exit/Revert/Load/Load a recent project or create from template a JMeter plan or open a new one if a test is running</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li>Updated to jsoup-1.9.2 (from 1.8.3)</li>
     <li>Updated to ph-css 4.1.5 (from 4.1.4)</li>
     <li>Updated to tika-core and tika-parsers 1.13 (from 1.12)</li>
     <li>Updated to commons-io 2.5 (from 2.4)</li>
     <li>Updated to commons-net 3.5 (from 3.4)</li>
     <li>Updated to groovy 2.4.7 (from 2.4.6)</li>
     <li>Updated to httpcore 4.4.5 (from 4.4.4)</li>
     <li>Updated to slf4j-api 1.7.21 (from 1.7.13)</li>
     <li>Updated to rsyntaxtextarea-2.6.0 (from 2.5.8)</li>
     <li>Updated to xstream 1.4.9 (from 1.4.8)</li>
     <li>Updated to jodd 3.7.1 (from 3.6.7.jar)</li>
     <li>Updated to xmlgraphics-commons 2.1 (from 2.0.1)</li>
     <li><pr>215</pr>Reduce duplicated code by using the newly added method <code>GuiUtils#cancelEditing</code>.
     Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>218</pr>Misc cleanup. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>216</pr>Re-use pattern when possible. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>58888</bug>HTTP(S) Test Script Recorder (ProxyControl) does not add TestElement's returned by <code>SamplerCreator#createChildren()</code></li>
     <li><bug>59902</bug>Https handshake failure when setting <code>httpclient.socket.https.cps</code> property</li>
     <li><bug>60084</bug>JMeter 3.0 embedded resource URL is silently encoded</li>
  </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>59113</bug>JDBC Connection Configuration : Transaction Isolation level not correctly set if constant used instead of numerical</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>59712</bug>Display original query in RequestView when decoding fails. Based on a patch by
          Teemu Vesala (teemu.vesala at qentinel.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>59964</bug>JSR223 Test Element : Cache compiled script if available is not correctly reset. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>59609</bug>Format extracted JSON Objects in JSON Post Processor correctly as JSON.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>59400</bug>Get rid of UnmarshalException on stopping when <code>-X</code> option is used.</li>
     <li><bug>59607</bug>JMeter crashes when reading large test plan (greater than 2g). Based on fix by Felix Draxler (felix.draxler at sap.com)</li>
     <li><bug>59621</bug>Error count in report dashboard is one off.</li>
     <li><bug>59657</bug>Only set font in JSyntaxTextArea, when property <code>jsyntaxtextarea.font.family</code> is set.</li>
     <li><bug>59720</bug>Batch test file comparisons fail on Windows as XML files are generated as EOL=LF</li>
     <li>Code cleanups. Patches by Graham Russell (graham at ham1.co.uk)</li>
     <li><bug>59722</bug>Use StandardCharsets to reduce the possibility of misspelling Charset names.</li>
     <li><bug>59723</bug>Use <code>jmeter.properties</code> for testing whenever possible</li>
     <li><bug>59726</bug>Unit test to check that CSV header text and sample format don't change unexpectedly</li>
     <li><bug>59889</bug>Change encoding to UTF-8 in reports for dashboard.</li>
     <li><bug>60053</bug>In Non GUI mode, a Stacktrace is shown at end of test while report is being generated</li>
     <li><bug>60049</bug>When using Timers with high delays or Constant Throughput Timer with low throughput, Scheduler may take a lot of time to exit, same for Shutdown test </li>
     <li><bug>60089</bug>Report / Dashboard : Bytes throughput Over Time has reversed Sent and Received bytes. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60090</bug>Report / Dashboard : Empty Transaction Controller should not count in metrics</li>
     <li><bug>60103</bug>Report / Dashboard : Requests summary includes Transaction Controller leading to wrong percentage</li>
     <li><bug>60105</bug>Report / Dashboard : Report requires Transaction Controller "<code>generate parent sample</code>" option to be checked , fix related issues</li>
     <li><bug>60107</bug>Report / Dashboard : In StatisticSummary, TransactionController SampleResult makes Total line wrong</li>
     <li><bug>60110</bug>Report / Dashboard : In Response Time Percentiles, slider is useless</li>
     <li><bug>60135</bug>Report / Dashboard : Active Threads Over Time should be in OverTime section</li>
     <li><bug>60125</bug>Report / Dashboard : Dashboard cannot be generated if the default delimiter is <code>\t</code>. Based on a report from Tamas Szabadi (tamas.szabadi at rightside.co)</li>
     <li><bug>59439</bug>Report / Dashboard : AbstractOverTimeGraphConsumer.createGroupInfos() should be abstract</li>
     <li><bug>59918</bug>Ant generated HTML report is broken (extras folder)</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Felix Draxler (felix.draxler at sap.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Teemu Vesala (teemu.vesala at qentinel.com)</li>
 <li>Asier Lostalé (asier.lostale at openbravo.com)</li>
 <li>Thomas Peyrard (thomas.peyrard at murex.com)</li>
 <li>Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Tamas Szabadi (tamas.szabadi at rightside.co)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <source>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </source>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
