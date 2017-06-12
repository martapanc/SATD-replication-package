diff --git a/src/core/org/apache/jmeter/engine/TreeCloner.java b/src/core/org/apache/jmeter/engine/TreeCloner.java
index 207061bd6..63edadb52 100644
--- a/src/core/org/apache/jmeter/engine/TreeCloner.java
+++ b/src/core/org/apache/jmeter/engine/TreeCloner.java
@@ -1,83 +1,90 @@
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
 
 package org.apache.jmeter.engine;
 
 import java.util.LinkedList;
 
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.collections.ListedHashTree;
 
 /**
  * Clones the test tree,  skipping test elements that implement {@link NoThreadClone} by default.
  */
 public class TreeCloner implements HashTreeTraverser {
 
     private final ListedHashTree newTree;
 
     private final LinkedList<Object> objects = new LinkedList<Object>();
 
     private final boolean honourNoThreadClone;
 
     /**
      * Clone the test tree, honouring NoThreadClone markers.
      * 
      */
     public TreeCloner() {
         this(true);
     }
 
     /**
      * Clone the test tree.
      * 
      * @param honourNoThreadClone set false to clone NoThreadClone nodes as well
      */
     public TreeCloner(boolean honourNoThreadClone) {
         newTree = new ListedHashTree();
         this.honourNoThreadClone = honourNoThreadClone;
     }
 
     public void addNode(Object node, HashTree subTree) {
-        
         if ( (node instanceof TestElement) // Check can cast for clone
            // Don't clone NoThreadClone unless honourNoThreadClone == false
           && (!honourNoThreadClone || !(node instanceof NoThreadClone))
         ) {
             node = ((TestElement) node).clone();
             newTree.add(objects, node);
         } else {
             newTree.add(objects, node);
         }
+        addLast(node);
+    }
+    
+    /**
+     * add node to objects LinkedList
+     * @param node Object
+     */
+    protected final void addLast(Object node) {
         objects.addLast(node);
     }
 
     public void subtractNode() {
         objects.removeLast();
     }
 
     public ListedHashTree getClonedTree() {
         return newTree;
     }
 
     public void processPath() {
     }
 
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/engine/TreeClonerNoTimer.java b/src/core/org/apache/jmeter/engine/TreeClonerNoTimer.java
new file mode 100644
index 000000000..e01eb9997
--- /dev/null
+++ b/src/core/org/apache/jmeter/engine/TreeClonerNoTimer.java
@@ -0,0 +1,59 @@
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
+package org.apache.jmeter.engine;
+
+import org.apache.jmeter.timers.Timer;
+import org.apache.jorphan.collections.HashTree;
+import org.apache.jorphan.logging.LoggingManager;
+import org.apache.log.Logger;
+
+/**
+ * Clones the test tree,  skipping test elements that implement {@link Timer} by default.
+ */
+public class TreeClonerNoTimer extends TreeCloner{
+    private Logger logger = LoggingManager.getLoggerForClass();
+    
+    /**
+     * {@inheritDoc}
+     */
+    public TreeClonerNoTimer() {
+        super();
+    }
+
+    /**
+     * {@inheritDoc}
+     */
+    public TreeClonerNoTimer(boolean honourNoThreadClone) {
+        super(honourNoThreadClone);
+    }
+
+    /**
+     * {@inheritDoc}
+     */
+    public void addNode(Object node, HashTree subTree) {
+        if(!(node instanceof Timer)) {
+            super.addNode(node, subTree);
+        } else {
+            if(logger.isDebugEnabled()) {
+                logger.debug("Ignoring timer node:"+ node);
+            }
+            addLast(node);
+        }
+    }
+}
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/gui/action/ActionNames.java b/src/core/org/apache/jmeter/gui/action/ActionNames.java
index 600adf7df..2d0011eb3 100644
--- a/src/core/org/apache/jmeter/gui/action/ActionNames.java
+++ b/src/core/org/apache/jmeter/gui/action/ActionNames.java
@@ -1,93 +1,94 @@
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
 
 /*
  * Collect all the action names together in one place.
  * This helps to ensure that there are no duplicates
  *
  *
  */
 public class ActionNames {
 
     public static final String ABOUT            = "about"; // $NON-NLS-1$
     public static final String ACTION_SHUTDOWN  = "shutdown"; // $NON-NLS-1$
     public static final String ACTION_START     = "start"; // $NON-NLS-1$
+    public static final String ACTION_START_NO_TIMERS = "start_no_timers"; // $NON-NLS-1$
     public static final String ACTION_STOP      = "stop"; // $NON-NLS-1$
     public static final String ADD              = "Add"; // $NON-NLS-1$
     public static final String ADD_ALL          = "add_all"; // $NON-NLS-1$
     public static final String ADD_PARENT       = "Add Parent"; // $NON-NLS-1$
     public static final String ANALYZE_FILE     = "Analyze File"; // $NON-NLS-1$
     public static final String CHANGE_LANGUAGE  = "change_language"; // $NON-NLS-1$
     public static final String CHECK_DIRTY      = "check_dirty"; // $NON-NLS-1$
     public static final String CHECK_REMOVE     = "check_remove"; // $NON-NLS-1$
     public final static String CLEAR            = "action.clear"; // $NON-NLS-1$
     public final static String CLEAR_ALL        = "action.clear_all"; // $NON-NLS-1$
     public static final String CLOSE            = "close"; // $NON-NLS-1$
     public static final String COLLAPSE_ALL     = "collapse all"; // $NON-NLS-1$
     public static final String COPY             = "Copy"; // $NON-NLS-1$
     public final static String CUT              = "Cut"; // $NON-NLS-1$
     public static final String DEBUG_ON         = "debug_on"; // $NON-NLS-1$
     public static final String DEBUG_OFF        = "debug_off"; // $NON-NLS-1$
     public static final String DISABLE          = "disable"; // $NON-NLS-1$
     public final static String DRAG_ADD         = "drag_n_drop.add";//$NON-NLS-1$
     public static final String EDIT             = "edit"; // $NON-NLS-1$
     public static final String ENABLE           = "enable"; // $NON-NLS-1$
     public static final String EXIT             = "exit"; // $NON-NLS-1$
     public static final String EXPAND_ALL       = "expand all"; // $NON-NLS-1$
     public static final String FUNCTIONS        = "functions"; // $NON-NLS-1$
     public final static String HELP             = "help"; // $NON-NLS-1$
     public final static String INSERT_AFTER     = "drag_n_drop.insert_after";//$NON-NLS-1$
     public final static String INSERT_BEFORE    = "drag_n_drop.insert_before";//$NON-NLS-1$
     public static final String LAF_PREFIX       = "laf:"; // Look and Feel prefix
     public static final String MERGE            = "merge"; // $NON-NLS-1$
     public static final String OPEN             = "open"; // $NON-NLS-1$
     public static final String OPEN_RECENT      = "open_recent"; // $NON-NLS-1$
     public static final String PASTE            = "Paste"; // $NON-NLS-1$
     public static final String REMOTE_EXIT      = "remote_exit"; // $NON-NLS-1$
     public static final String REMOTE_EXIT_ALL  = "remote_exit_all"; // $NON-NLS-1$
     public static final String REMOTE_SHUT      = "remote_shut"; // $NON-NLS-1$
     public static final String REMOTE_SHUT_ALL  = "remote_shut_all"; // $NON-NLS-1$
     public static final String REMOTE_START     = "remote_start"; // $NON-NLS-1$
     public static final String REMOTE_START_ALL = "remote_start_all"; // $NON-NLS-1$
     public static final String REMOTE_STOP      = "remote_stop"; // $NON-NLS-1$
     public static final String REMOTE_STOP_ALL  = "remote_stop_all"; // $NON-NLS-1$
     public static final String REMOVE           = "remove"; // $NON-NLS-1$
     public static final String RESET_GUI        = "reset_gui"; // $NON-NLS-1$
     public static final String REVERT_PROJECT   = "revert_project"; // $NON-NLS-1$
     public final static String SAVE             = "save"; // $NON-NLS-1$
     public final static String SAVE_ALL_AS      = "save_all_as";  // $NON-NLS-1$
     public final static String SAVE_AS          = "save_as"; // $NON-NLS-1$
     public static final String SAVE_GRAPHICS    = "save_graphics"; // $NON-NLS-1$
     public static final String SAVE_GRAPHICS_ALL= "save_graphics_all"; // $NON-NLS-1$
     public static final String SSL_MANAGER      = "sslManager"; // $NON-NLS-1$
     public static final String STOP_THREAD      = "stop_thread"; // $NON-NLS-1$
     public static final String SUB_TREE_LOADED  = "sub_tree_loaded"; // $NON-NLS-1$
     public static final String SUB_TREE_MERGED  = "sub_tree_merged"; // $NON-NLS-1$
     public static final String SUB_TREE_SAVED   = "sub_tree_saved"; // $NON-NLS-1$
     public static final String TOGGLE           = "toggle"; // $NON-NLS-1$ enable/disable
     public static final String WHAT_CLASS       = "what_class"; // $NON-NLS-1$
     public static final String SEARCH_TREE      = "search_tree"; // $NON-NLS-1$
     public static final String SEARCH_RESET      = "search_reset"; // $NON-NLS-1$
 
     // Prevent instantiation
     private ActionNames(){
 
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Start.java b/src/core/org/apache/jmeter/gui/action/Start.java
index 561bc7fc6..9676c52e9 100644
--- a/src/core/org/apache/jmeter/gui/action/Start.java
+++ b/src/core/org/apache/jmeter/gui/action/Start.java
@@ -1,107 +1,133 @@
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
 
 import org.apache.jmeter.JMeter;
 import org.apache.jmeter.engine.JMeterEngineException;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.TreeCloner;
+import org.apache.jmeter.engine.TreeClonerNoTimer;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.testelement.TestPlan;
+import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class Start extends AbstractAction {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> commands = new HashSet<String>();
 
     static {
         commands.add(ActionNames.ACTION_START);
+        commands.add(ActionNames.ACTION_START_NO_TIMERS);
         commands.add(ActionNames.ACTION_STOP);
         commands.add(ActionNames.ACTION_SHUTDOWN);
     }
 
     private StandardJMeterEngine engine;
 
     /**
      * Constructor for the Start object.
      */
     public Start() {
     }
 
     /**
      * Gets the ActionNames attribute of the Start object.
      *
      * @return the ActionNames value
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doAction(ActionEvent e) {
         if (e.getActionCommand().equals(ActionNames.ACTION_START)) {
             popupShouldSave(e);
-            startEngine();
+            startEngine(false);
+        } else if (e.getActionCommand().equals(ActionNames.ACTION_START_NO_TIMERS)) {
+            popupShouldSave(e);
+            startEngine(true);
         } else if (e.getActionCommand().equals(ActionNames.ACTION_STOP)) {
             if (engine != null) {
                 log.info("Stopping test");
                 GuiPackage.getInstance().getMainFrame().showStoppingMessage("");
                 engine.stopTest();
             }
         } else if (e.getActionCommand().equals(ActionNames.ACTION_SHUTDOWN)) {
             if (engine != null) {
                 log.info("Shutting test down");
                 GuiPackage.getInstance().getMainFrame().showStoppingMessage("");
                 engine.askThreadsToStop();
             }
         }
     }
 
-    private void startEngine() {
+    /**
+     * Start JMeter engine
+     * @param noTimer ignore timers 
+     */
+    private void startEngine(boolean ignoreTimer) {
         GuiPackage gui = GuiPackage.getInstance();
         HashTree testTree = gui.getTreeModel().getTestPlan();
         JMeter.convertSubTree(testTree);
         testTree.add(testTree.getArray()[0], gui.getMainFrame());
         log.debug("test plan before cloning is running version: "
                 + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
-        TreeCloner cloner = new TreeCloner(false);
-        testTree.traverse(cloner);
+        TreeCloner cloner = cloneTree(testTree, ignoreTimer);
         engine = new StandardJMeterEngine();
         engine.configure(cloner.getClonedTree());
         try {
             engine.runTest();
         } catch (JMeterEngineException e) {
             JOptionPane.showMessageDialog(gui.getMainFrame(), e.getMessage(), JMeterUtils
                     .getResString("Error Occurred"), JOptionPane.ERROR_MESSAGE);
         }
         log.debug("test plan after cloning and running test is running version: "
                 + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
     }
+    
+    /**
+     * Create a Cloner that ignores {@link Timer} if removeTimers is true
+     * @param testTree {@link HashTree}
+     * @param removeTimers boolean remove timers 
+     * @return {@link TreeCloner}
+     */
+    private TreeCloner cloneTree(HashTree testTree, boolean removeTimers) {
+        TreeCloner cloner = null;
+        if(!removeTimers) {
+            cloner = new TreeCloner(false);     
+        } else {
+            cloner = new TreeClonerNoTimer(false);
+        }
+        testTree.traverse(cloner);
+        return cloner;
+    }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index a0e8a6285..5ac2ae77b 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,728 +1,734 @@
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
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.Component;
 import java.awt.event.KeyEvent;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import javax.swing.JComponent;
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.KeyStroke;
 import javax.swing.MenuElement;
 import javax.swing.UIManager;
 
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.action.LoadRecentProject;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class JMeterMenuBar extends JMenuBar implements LocaleChangeListener {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private JMenu fileMenu;
 
     private JMenuItem file_save_as;
 
     private JMenuItem file_selection_as;
 
     private JMenuItem file_revert;
 
     private JMenuItem file_load;
 
     private List<JComponent> file_load_recent_files;
 
     private JMenuItem file_merge;
 
     private JMenuItem file_exit;
 
     private JMenuItem file_close;
 
     private JMenu editMenu;
 
     private JMenu edit_add;
 
     // JMenu edit_add_submenu;
     private JMenuItem edit_remove; // TODO - should this be created?
 
     private JMenu runMenu;
 
     private JMenuItem run_start;
 
+    private JMenuItem run_start_no_timers;
+
     private JMenu remote_start;
 
     private JMenuItem remote_start_all;
 
     private Collection<JMenuItem> remote_engine_start;
 
     private JMenuItem run_stop;
 
     private JMenuItem run_shut;
 
     private JMenu remote_stop;
 
     private JMenu remote_shut;
 
     private JMenuItem remote_stop_all;
 
     private JMenuItem remote_shut_all;
 
     private Collection<JMenuItem> remote_engine_stop;
 
     private Collection<JMenuItem> remote_engine_shut;
 
     private JMenuItem run_clear;
 
     private JMenuItem run_clearAll;
 
     // JMenu reportMenu;
     // JMenuItem analyze;
     private JMenu optionsMenu;
 
     private JMenu lafMenu;
 
     private JMenuItem sslManager;
 
     private JMenu helpMenu;
 
     private JMenuItem help_about;
 
     private String[] remoteHosts;
 
     private JMenu remote_exit;
 
     private JMenuItem remote_exit_all;
 
     private Collection<JMenuItem> remote_engine_exit;
 
     private JMenu searchMenu;
 
     public JMeterMenuBar() {
         // List for recent files menu items
         file_load_recent_files = new LinkedList<JComponent>();
         // Lists for remote engines menu items
         remote_engine_start = new LinkedList<JMenuItem>();
         remote_engine_stop = new LinkedList<JMenuItem>();
         remote_engine_shut = new LinkedList<JMenuItem>();
         remote_engine_exit = new LinkedList<JMenuItem>();
         remoteHosts = JOrphanUtils.split(JMeterUtils.getPropDefault("remote_hosts", ""), ","); //$NON-NLS-1$
         if (remoteHosts.length == 1 && remoteHosts[0].equals("")) {
             remoteHosts = new String[0];
         }
         this.getRemoteItems();
         createMenuBar();
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     public void setFileSaveEnabled(boolean enabled) {
         if(file_save_as != null) {
             file_save_as.setEnabled(enabled);
         }
     }
 
     public void setFileLoadEnabled(boolean enabled) {
         if (file_load != null) {
             file_load.setEnabled(enabled);
         }
         if (file_merge != null) {
             file_merge.setEnabled(enabled);
         }
     }
 
     public void setFileRevertEnabled(boolean enabled) {
         if(file_revert != null) {
             file_revert.setEnabled(enabled);
         }
     }
 
     public void setProjectFileLoaded(String file) {
         if(file_load_recent_files != null && file != null) {
             LoadRecentProject.updateRecentFileMenuItems(file_load_recent_files, file);
         }
     }
 
     public void setEditEnabled(boolean enabled) {
         if (editMenu != null) {
             editMenu.setEnabled(enabled);
         }
     }
 
     // Does not appear to be used; called by MainFrame#setEditAddMenu() but that is not called
     public void setEditAddMenu(JMenu menu) {
         // If the Add menu already exists, remove it.
         if (edit_add != null) {
             editMenu.remove(edit_add);
         }
         // Insert the Add menu as the first menu item in the Edit menu.
         edit_add = menu;
         editMenu.insert(edit_add, 0);
     }
 
     // Called by MainFrame#setEditMenu() which is called by EditCommand#doAction and GuiPackage#localeChanged
     public void setEditMenu(JPopupMenu menu) {
         if (menu != null) {
             editMenu.removeAll();
             Component[] comps = menu.getComponents();
             for (int i = 0; i < comps.length; i++) {
                 editMenu.add(comps[i]);
             }
             editMenu.setEnabled(true);
         } else {
             editMenu.setEnabled(false);
         }
     }
 
     public void setEditAddEnabled(boolean enabled) {
         // There was a NPE being thrown without the null check here.. JKB
         if (edit_add != null) {
             edit_add.setEnabled(enabled);
         }
         // If we are enabling the Edit-->Add menu item, then we also need to
         // enable the Edit menu. The Edit menu may already be enabled, but
         // there's no harm it trying to enable it again.
         if (enabled) {
             setEditEnabled(true);
         } else {
             // If we are disabling the Edit-->Add menu item and the
             // Edit-->Remove menu item is disabled, then we also need to
             // disable the Edit menu.
             // The Java Look and Feel Guidelines say to disable a menu if all
             // menu items are disabled.
             if (!edit_remove.isEnabled()) {
                 editMenu.setEnabled(false);
             }
         }
     }
 
     public void setEditRemoveEnabled(boolean enabled) {
         edit_remove.setEnabled(enabled);
         // If we are enabling the Edit-->Remove menu item, then we also need to
         // enable the Edit menu. The Edit menu may already be enabled, but
         // there's no harm it trying to enable it again.
         if (enabled) {
             setEditEnabled(true);
         } else {
             // If we are disabling the Edit-->Remove menu item and the
             // Edit-->Add menu item is disabled, then we also need to disable
             // the Edit menu.
             // The Java Look and Feel Guidelines say to disable a menu if all
             // menu items are disabled.
             if (!edit_add.isEnabled()) {
                 editMenu.setEnabled(false);
             }
         }
     }
 
     /**
      * Creates the MenuBar for this application. I believe in my heart that this
      * should be defined in a file somewhere, but that is for later.
      */
     public void createMenuBar() {
         makeFileMenu();
         makeEditMenu();
         makeRunMenu();
         makeOptionsMenu();
         makeHelpMenu();
         makeSearchMenu();
         this.add(fileMenu);
         this.add(editMenu);
         this.add(searchMenu);
         this.add(runMenu);
         this.add(optionsMenu);
         this.add(helpMenu);
     }
 
     private void makeHelpMenu() {
         // HELP MENU
         helpMenu = makeMenuRes("help",'H'); //$NON-NLS-1$
 
         JMenuItem contextHelp = makeMenuItemRes("help", 'H', ActionNames.HELP, KeyStrokes.HELP); //$NON-NLS-1$
 
         JMenuItem whatClass = makeMenuItemRes("help_node", 'W', ActionNames.WHAT_CLASS, KeyStrokes.WHAT_CLASS);//$NON-NLS-1$
 
         JMenuItem setDebug = makeMenuItemRes("debug_on", ActionNames.DEBUG_ON, KeyStrokes.DEBUG_ON);//$NON-NLS-1$
 
         JMenuItem resetDebug = makeMenuItemRes("debug_off", ActionNames.DEBUG_OFF, KeyStrokes.DEBUG_OFF);//$NON-NLS-1$
 
         help_about = makeMenuItemRes("about", 'A', ActionNames.ABOUT); //$NON-NLS-1$
 
         helpMenu.add(contextHelp);
         helpMenu.addSeparator();
         helpMenu.add(whatClass);
         helpMenu.add(setDebug);
         helpMenu.add(resetDebug);
         helpMenu.addSeparator();
         helpMenu.add(help_about);
     }
 
     private void makeOptionsMenu() {
         // OPTIONS MENU
         optionsMenu = makeMenuRes("option",'O'); //$NON-NLS-1$
         JMenuItem functionHelper = makeMenuItemRes("function_dialog_menu_item", 'F', ActionNames.FUNCTIONS, KeyStrokes.FUNCTIONS); //$NON-NLS-1$
 
         lafMenu = makeMenuRes("appearance",'L'); //$NON-NLS-1$
         UIManager.LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
         for (int i = 0; i < lafs.length; ++i) {
             JMenuItem laf = new JMenuItem(lafs[i].getName());
             laf.addActionListener(ActionRouter.getInstance());
             laf.setActionCommand(ActionNames.LAF_PREFIX + lafs[i].getClassName());
             lafMenu.add(laf);
         }
         optionsMenu.add(functionHelper);
         optionsMenu.add(lafMenu);
         if (SSLManager.isSSLSupported()) {
             sslManager = makeMenuItemRes("sslmanager", 'S', ActionNames.SSL_MANAGER, KeyStrokes.SSL_MANAGER); //$NON-NLS-1$
             optionsMenu.add(sslManager);
         }
         optionsMenu.add(makeLanguageMenu());
 
         JMenuItem collapse = makeMenuItemRes("menu_collapse_all", ActionNames.COLLAPSE_ALL, KeyStrokes.COLLAPSE_ALL); //$NON-NLS-1$
         optionsMenu.add(collapse);
 
         JMenuItem expand = makeMenuItemRes("menu_expand_all", ActionNames.EXPAND_ALL, KeyStrokes.EXPAND_ALL); //$NON-NLS-1$
         optionsMenu.add(expand);
     }
 
     private static class LangMenuHelper{
         final ActionRouter actionRouter = ActionRouter.getInstance();
         final JMenu languageMenu;
 
         LangMenuHelper(JMenu _languageMenu){
             languageMenu = _languageMenu;
         }
 
         /**
          * Create a language entry from the locale name.
          *
          * @param locale - must also be a valid resource name
          */
         void addLang(String locale){
             String localeString = JMeterUtils.getLocaleString(locale);
             JMenuItem language = new JMenuItem(localeString);
             language.addActionListener(actionRouter);
             language.setActionCommand(ActionNames.CHANGE_LANGUAGE);
             language.setName(locale); // This is used by the ChangeLanguage class to define the Locale
             languageMenu.add(language);
         }
 
    }
 
     /**
      * Generate the list of supported languages.
      *
      * @return list of languages
      */
     // Also used by org.apache.jmeter.resources.PackageTest
     public static String[] getLanguages(){
         List<String> lang = new ArrayList<String>(20);
         lang.add(Locale.ENGLISH.toString()); // en
         lang.add(Locale.FRENCH.toString()); // fr
         lang.add(Locale.GERMAN.toString()); // de
         lang.add("no"); // $NON-NLS-1$
         lang.add("pl"); // $NON-NLS-1$
         lang.add("pt_BR"); // $NON-NLS-1$
         lang.add("es"); // $NON-NLS-1$
         lang.add("tr"); // $NON-NLS-1$
         lang.add(Locale.JAPANESE.toString()); // ja
         lang.add(Locale.SIMPLIFIED_CHINESE.toString()); // zh_CN
         lang.add(Locale.TRADITIONAL_CHINESE.toString()); // zh_TW
         final String addedLocales = JMeterUtils.getProperty("locales.add");
         if (addedLocales != null){
             String [] addLanguages =addedLocales.split(","); // $NON-NLS-1$
             for(int i=0; i < addLanguages.length; i++){
                 log.info("Adding locale "+addLanguages[i]);
                 lang.add(addLanguages[i]);
             }
         }
         return lang.toArray(new String[lang.size()]);
     }
 
     static JMenu makeLanguageMenu() {
         final JMenu languageMenu = makeMenuRes("choose_language",'C'); //$NON-NLS-1$
 
         LangMenuHelper langMenu = new LangMenuHelper(languageMenu);
 
         /*
          * Note: the item name is used by ChangeLanguage to create a Locale for
          * that language, so need to ensure that the language strings are valid
          * If they exist, use the Locale language constants.
          * Also, need to ensure that the names are valid resource entries too.
          */
 
         String lang[] = getLanguages();
         for(int i=0; i < lang.length; i++ ){
             langMenu.addLang(lang[i]);
         }
         return languageMenu;
     }
 
     private void makeRunMenu() {
         // RUN MENU
         runMenu = makeMenuRes("run",'R'); //$NON-NLS-1$
 
         run_start = makeMenuItemRes("start", 'S', ActionNames.ACTION_START, KeyStrokes.ACTION_START); //$NON-NLS-1$
 
+        run_start_no_timers = makeMenuItemRes("start_no_timers", ActionNames.ACTION_START_NO_TIMERS); //$NON-NLS-1$
+        
         run_stop = makeMenuItemRes("stop", 'T', ActionNames.ACTION_STOP, KeyStrokes.ACTION_STOP); //$NON-NLS-1$
         run_stop.setEnabled(false);
 
         run_shut = makeMenuItemRes("shutdown", 'Y', ActionNames.ACTION_SHUTDOWN, KeyStrokes.ACTION_SHUTDOWN); //$NON-NLS-1$
         run_shut.setEnabled(false);
 
         run_clear = makeMenuItemRes("clear", 'C', ActionNames.CLEAR, KeyStrokes.CLEAR); //$NON-NLS-1$
 
         run_clearAll = makeMenuItemRes("clear_all", 'a', ActionNames.CLEAR_ALL, KeyStrokes.CLEAR_ALL); //$NON-NLS-1$
 
         runMenu.add(run_start);
+        runMenu.add(run_start_no_timers);
         if (remote_start != null) {
             runMenu.add(remote_start);
         }
         remote_start_all = makeMenuItemRes("remote_start_all", ActionNames.REMOTE_START_ALL, KeyStrokes.REMOTE_START_ALL); //$NON-NLS-1$
 
         runMenu.add(remote_start_all);
         runMenu.add(run_stop);
         runMenu.add(run_shut);
         if (remote_stop != null) {
             runMenu.add(remote_stop);
         }
         remote_stop_all = makeMenuItemRes("remote_stop_all", 'X', ActionNames.REMOTE_STOP_ALL, KeyStrokes.REMOTE_STOP_ALL); //$NON-NLS-1$
         runMenu.add(remote_stop_all);
 
         if (remote_shut != null) {
             runMenu.add(remote_shut);
         }
         remote_shut_all = makeMenuItemRes("remote_shut_all", 'X', ActionNames.REMOTE_SHUT_ALL, KeyStrokes.REMOTE_SHUT_ALL); //$NON-NLS-1$
         runMenu.add(remote_shut_all);
 
         if (remote_exit != null) {
             runMenu.add(remote_exit);
         }
         remote_exit_all = makeMenuItemRes("remote_exit_all", ActionNames.REMOTE_EXIT_ALL); //$NON-NLS-1$
         runMenu.add(remote_exit_all);
 
         runMenu.addSeparator();
         runMenu.add(run_clear);
         runMenu.add(run_clearAll);
     }
 
     private void makeEditMenu() {
         // EDIT MENU
         editMenu = makeMenuRes("edit",'E'); //$NON-NLS-1$
 
         // From the Java Look and Feel Guidelines: If all items in a menu
         // are disabled, then disable the menu. Makes sense.
         editMenu.setEnabled(false);
     }
 
     private void makeFileMenu() {
         // FILE MENU
         fileMenu = makeMenuRes("file",'F'); //$NON-NLS-1$
 
         JMenuItem file_save = makeMenuItemRes("save", 'S', ActionNames.SAVE, KeyStrokes.SAVE); //$NON-NLS-1$
         file_save.setEnabled(true);
 
         file_save_as = makeMenuItemRes("save_all_as", 'A', ActionNames.SAVE_ALL_AS, KeyStrokes.SAVE_ALL_AS); //$NON-NLS-1$
         file_save_as.setEnabled(true);
 
         file_selection_as = makeMenuItemRes("save_as", ActionNames.SAVE_AS); //$NON-NLS-1$
         file_selection_as.setEnabled(true);
 
         file_revert = makeMenuItemRes("revert_project", 'R', ActionNames.REVERT_PROJECT); //$NON-NLS-1$
         file_revert.setEnabled(false);
 
         file_load = makeMenuItemRes("menu_open", 'O', ActionNames.OPEN, KeyStrokes.OPEN); //$NON-NLS-1$
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         file_load.setEnabled(false);
 
         file_close = makeMenuItemRes("menu_close", 'C', ActionNames.CLOSE, KeyStrokes.CLOSE); //$NON-NLS-1$
 
         file_exit = makeMenuItemRes("exit", 'X', ActionNames.EXIT, KeyStrokes.EXIT); //$NON-NLS-1$
 
         file_merge = makeMenuItemRes("menu_merge", 'M', ActionNames.MERGE); //$NON-NLS-1$
         // file_merge.setAccelerator(
         // KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         file_merge.setEnabled(false);
 
         fileMenu.add(file_close);
         fileMenu.add(file_load);
         fileMenu.add(file_merge);
         fileMenu.addSeparator();
         fileMenu.add(file_save);
         fileMenu.add(file_save_as);
         fileMenu.add(file_selection_as);
         fileMenu.add(file_revert);
         fileMenu.addSeparator();
         // Add the recent files, which will also add a separator that is
         // visible when needed
         file_load_recent_files = LoadRecentProject.getRecentFileMenuItems();
         for(Iterator<JComponent> i = file_load_recent_files.iterator(); i.hasNext();) {
             fileMenu.add(i.next());
         }
         fileMenu.add(file_exit);
     }
 
     private void makeSearchMenu() {
         // Search MENU
         searchMenu = makeMenuRes("menu_search"); //$NON-NLS-1$
 
         JMenuItem search = makeMenuItemRes("menu_search", ActionNames.SEARCH_TREE); //$NON-NLS-1$
         searchMenu.add(search);
         searchMenu.setEnabled(true);
 
         JMenuItem searchReset = makeMenuItemRes("menu_search_reset", ActionNames.SEARCH_RESET); //$NON-NLS-1$
         searchMenu.add(searchReset);
         searchMenu.setEnabled(true);
     }
     
     public void setRunning(boolean running, String host) {
         log.info("setRunning(" + running + "," + host + ")");
 
         Iterator<JMenuItem> iter = remote_engine_start.iterator();
         Iterator<JMenuItem> iter2 = remote_engine_stop.iterator();
         Iterator<JMenuItem> iter3 = remote_engine_exit.iterator();
         Iterator<JMenuItem> iter4 = remote_engine_shut.iterator();
         while (iter.hasNext() && iter2.hasNext() && iter3.hasNext() &&iter4.hasNext()) {
             JMenuItem start = iter.next();
             JMenuItem stop = iter2.next();
             JMenuItem exit = iter3.next();
             JMenuItem shut = iter4.next();
             if (start.getText().equals(host)) {
                 log.debug("Found start host: " + start.getText());
                 start.setEnabled(!running);
             }
             if (stop.getText().equals(host)) {
                 log.debug("Found stop  host: " + stop.getText());
                 stop.setEnabled(running);
             }
             if (exit.getText().equals(host)) {
                 log.debug("Found exit  host: " + exit.getText());
                 exit.setEnabled(true);
             }
             if (shut.getText().equals(host)) {
                 log.debug("Found exit  host: " + exit.getText());
                 shut.setEnabled(running);
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void setEnabled(boolean enable) {
         run_start.setEnabled(!enable);
+        run_start_no_timers.setEnabled(!enable);
         run_stop.setEnabled(enable);
         run_shut.setEnabled(enable);
     }
 
     private void getRemoteItems() {
         if (remoteHosts.length > 0) {
             remote_start = makeMenuRes("remote_start"); //$NON-NLS-1$
             remote_stop = makeMenuRes("remote_stop"); //$NON-NLS-1$
             remote_shut = makeMenuRes("remote_shut"); //$NON-NLS-1$
             remote_exit = makeMenuRes("remote_exit"); //$NON-NLS-1$
 
             for (int i = 0; i < remoteHosts.length; i++) {
                 remoteHosts[i] = remoteHosts[i].trim();
 
                 JMenuItem item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_START);
                 remote_engine_start.add(item);
                 remote_start.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_STOP);
                 item.setEnabled(false);
                 remote_engine_stop.add(item);
                 remote_stop.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_SHUT);
                 item.setEnabled(false);
                 remote_engine_shut.add(item);
                 remote_shut.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i],ActionNames.REMOTE_EXIT);
                 item.setEnabled(false);
                 remote_engine_exit.add(item);
                 remote_exit.add(item);
             }
         }
     }
 
     /** {@inheritDoc} */
     public void localeChanged(LocaleChangeEvent event) {
         updateMenuElement(fileMenu);
         updateMenuElement(editMenu);
         updateMenuElement(runMenu);
         updateMenuElement(optionsMenu);
         updateMenuElement(helpMenu);
     }
 
     /**
      * <p>Refreshes all texts in the menu and all submenus to a new locale.</p>
      *
      * <p>Assumes that the item name is set to the resource key, so the resource can be retrieved.
      * Certain action types do not follow this rule, @see JMeterMenuBar#isNotResource(String)</p>
      *
      * The Language Change event assumes that the name is the same as the locale name,
      * so this additionally means that all supported locales must be defined as resources.
      *
      */
     private void updateMenuElement(MenuElement menu) {
         Component component = menu.getComponent();
         final String compName = component.getName();
         if (compName != null) {
             if (component instanceof JMenu) {
                 final JMenu jMenu = (JMenu) component;
                 if (isResource(jMenu.getActionCommand())){
                     jMenu.setText(JMeterUtils.getResString(compName));
                 }
             } else {
                 final JMenuItem jMenuItem = (JMenuItem) component;
                 if (isResource(jMenuItem.getActionCommand())){
                     jMenuItem.setText(JMeterUtils.getResString(compName));
                 } else if  (ActionNames.CHANGE_LANGUAGE.equals(jMenuItem.getActionCommand())){
                     jMenuItem.setText(JMeterUtils.getLocaleString(compName));
                 }
             }
         }
 
         MenuElement[] subelements = menu.getSubElements();
 
         for (int i = 0; i < subelements.length; i++) {
             updateMenuElement(subelements[i]);
         }
     }
 
     /**
      * Return true if component name is a resource.<br/>
      * i.e it is not a hostname:<br/>
      *
      * <tt>ActionNames.REMOTE_START</tt><br/>
      * <tt>ActionNames.REMOTE_STOP</tt><br/>
      * <tt>ActionNames.REMOTE_EXIT</tt><br/>
      *
      * nor a filename:<br/>
      * <tt>ActionNames.OPEN_RECENT</tt>
      *
      * nor a look and feel prefix:<br/>
      * <tt>ActionNames.LAF_PREFIX</tt>
      */
     private static boolean isResource(String actionCommand) {
         if (ActionNames.CHANGE_LANGUAGE.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.ADD.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_START.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_STOP.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_SHUT.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.REMOTE_EXIT.equals(actionCommand)){//
             return false;
         }
         if (ActionNames.OPEN_RECENT.equals(actionCommand)){//
             return false;
         }
         if (actionCommand != null && actionCommand.startsWith(ActionNames.LAF_PREFIX)){
             return false;
         }
         return true;
     }
 
     /**
      * Make a menu from a resource string.
      * @param resource used to name menu and set text.
      * @return the menu
      */
     private static JMenu makeMenuRes(String resource) {
         JMenu menu = new JMenu(JMeterUtils.getResString(resource));
         menu.setName(resource);
         return menu;
     }
 
     /**
      * Make a menu from a resource string and set its mnemonic.
      *
      * @param resource
      * @param mnemonic
      * @return the menu
      */
     private static JMenu makeMenuRes(String resource, int mnemonic){
         JMenu menu = makeMenuRes(resource);
         menu.setMnemonic(mnemonic);
         return menu;
     }
 
     /**
      * Make a menuItem using a fixed label which is also used as the item name.
      * This is used for items such as recent files and hostnames which are not resources
      * @param label (this is not used as a resource key)
      * @param actionCommand
      * @return the menu item
      */
     private static JMenuItem makeMenuItemNoRes(String label, String actionCommand) {
         JMenuItem menuItem = new JMenuItem(label);
         menuItem.setName(label);
         menuItem.setActionCommand(actionCommand);
         menuItem.addActionListener(ActionRouter.getInstance());
         return menuItem;
     }
 
     private static JMenuItem makeMenuItemRes(String resource, String actionCommand) {
         return makeMenuItemRes(resource, KeyEvent.VK_UNDEFINED, actionCommand, null);
     }
 
     private static JMenuItem makeMenuItemRes(String resource, String actionCommand, KeyStroke keyStroke) {
         return makeMenuItemRes(resource, KeyEvent.VK_UNDEFINED, actionCommand, keyStroke);
     }
 
     private static JMenuItem makeMenuItemRes(String resource, int mnemonic, String actionCommand) {
         return makeMenuItemRes(resource, mnemonic, actionCommand, null);
     }
 
     private static JMenuItem makeMenuItemRes(String resource, int mnemonic, String actionCommand, KeyStroke keyStroke){
         JMenuItem menuItem = new JMenuItem(JMeterUtils.getResString(resource), mnemonic);
         menuItem.setName(resource);
         menuItem.setActionCommand(actionCommand);
         menuItem.setAccelerator(keyStroke);
         menuItem.addActionListener(ActionRouter.getInstance());
         return menuItem;
     }
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index 2eb0f198a..8a31b4538 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1100 +1,1102 @@
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
 add=Add
 add_as_child=Add as Child
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 aggregate_graph=Statistical Graphs
 aggregate_graph_column=Column
 aggregate_graph_display=Display Graph
 aggregate_graph_height=Height
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label
 aggregate_graph_ms=Milliseconds
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_use_group_name=Include group name in label?
 aggregate_graph_user_title=Title for Graph
 aggregate_graph_width=Width
 aggregate_report=Aggregate Report
 aggregate_report_90=90%
 aggregate_report_90%_line=90% Line
 aggregate_report_bandwidth=KB/sec
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
 assertion_resp_field=Response Field to Test
 assertion_resp_size_field=Response Size Field to Test
 assertion_substring=Substring
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 bind=Thread Bind
 browse=Browse...
 bouncy_castle_unavailable_message=The jars for bouncy castle are unavailable, please add them to your classpath.
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
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 char_value=Unicode character number (decimal or 0xhex)
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cache_per_iter=Clear cache each iteration?
 clear_cookies_per_iter=Clear cookies each iteration?
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
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
 configure_wsdl=Configure
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_manager_policy=Cookie Policy
 cookie_manager_title=HTTP Cookie Manager
 cookies_stored=User-Defined Cookies
 copy=Copy
 corba_config_title=CORBA Sampler Config
 corba_input_data_file=Input Data File\:
 corba_methods=Choose method to invoke\:
 corba_name_server=Name Server\:
 corba_port=Port Number\:
 corba_request_data=Input Data
 corba_sample_title=CORBA Sampler
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 countlim=Size limit
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
 delete=Delete
 delete_parameter=Delete Variable
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 disable=Disable
 distribution_graph_title=Distribution Graph (alpha)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 domain=Domain
 done=Done
 down=Down
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Enable
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
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
 expiration=Expiration
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
 foreach_controller_title=ForEach Controller
 foreach_input=Input variable prefix
 foreach_output=Output variable name
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
 get_xml_from_file=File with SOAP XML Data (overrides above text)
 get_xml_from_random=Message(s) Folder
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_in_transaction_controllers=Put each group in a new transaction controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 help=Help
 help_node=What's this node?
 html_assertion_file=Write JTidy report to file
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_parameter_mask=HTML Parameter Mask
 http_implementation=Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
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
 jms_client_caption=Receiver client uses MessageConsumer.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
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
 jms_error_msg=Object message should read from an external file. Text input is currently selected, please remember to change it.
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
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Random File
 jms_read_response=Read Response
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title=JMS Default Request
 jms_send_queue=JNDI name Request queue
 jms_stop_between_samples=Stop between samples?
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use MessageConsumer.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_message=Text Message
 jms_timeout=Timeout (milliseconds)
 jms_topic=Destination
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File
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
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
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
 junit_create_instance_per_sample=Create a new instance per sample
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
 load_wsdl=Load WSDL
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
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_port=Server Port (optional):
 mail_reader_server=Server Host:
 mail_reader_server_type=Protocol (e.g. pop3, imaps):
 mail_reader_storemime=Store the message using MIME (raw)
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_attributes_panel=Mailing attributes
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\:
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 memory_cache=Memory Cache
 menu_assertions=Assertions
 menu_close=Close
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_generative_controller=Sampler
 menu_threads=Threads (Users)
 menu_fragments=Test Fragment
 menu_listener=Listener
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
 menu_timer=Timer
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
 monitor_health_title=Monitor Results
 monitor_is_title=Use as Monitor
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
 no=Norwegian
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
 port=Port\:
 post_thread_group_title=tearDown Thread Group
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip={0}\: {1}
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
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_daemon_bind_error=Could not create proxy - port in use. Choose another port.
 proxy_daemon_error=Could not create proxy - see log for details
 proxy_headers=Capture HTTP Headers
 proxy_httpsspoofing=Attempt HTTPS Spoofing
 proxy_httpsspoofing_match=Only spoof URLs matching:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP Proxy Server
 pt_br=Portugese (Brazilian)
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 random_string_length=Random string length
 random_string_chars_to_use=Chars to use for random string generation
 read_response_message=Read response is not checked. To see the response, please check the box in the sampler.
 read_response_note=If read response is unchecked, the sampler will not read the response
 read_response_note2=or set the SampleResult. This improves performance, but it means
 read_response_note3=the response content won't be logged.
 read_soap_response=Read SOAP Response
 realm=Realm
 record_controller_title=Recording Controller
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_source=Response Field to check
 regex_src_body=Body
 regex_src_body_unescaped=Body (unescaped)
 regex_src_hdrs=Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search previous sample - or variable.
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 regexfunc_param_7=Input variable name containing the text to be parsed ([previous sample])
 regexp_tester_button_test=Test
 regexp_tester_field=Regular expression\:
 regexp_render_no_text=Data response result isn't text.
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
 request_data=Request Data
 reset_gui=Reset Gui
 response_save_as_md5=Save response as MD5 hash?
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_errors=Save Failed Responses only
 resultsaver_prefix=Filename prefix\:
 resultsaver_skipautonumber=Don't add number to prefix
 resultsaver_skipsuffix=Don't add suffix
 resultsaver_success=Save Successful Responses only
 resultsaver_title=Save Responses to a file
 resultsaver_variable=Variable Name:
 retobj=Return object
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
 root=Root
 root_title=Root
 run=Run
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
 sampler_on_error_start_next_loop=Start Next Loop
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_test_now=Stop Test Now
 sampler_on_error_stop_thread=Stop Thread
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save byte count
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
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplecount=Save Sample and Error Counts
 save_samplerdata=Save Sampler Data (XML)
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search_base=Search base
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
 search_word=Search Word
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send Files With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=File Path\:
 send_file_mime_label=MIME Type\:
 send_file_param_name_label=Parameter Name\:
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
 smime_assertion_not_signed=Message not signed
 smime_assertion_message_position=Execute assertion on message at position
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
 smtp_enforcestarttls_tooltip=<html><b>Enforces</b> the server to use StartTLS.<br />If not selected and the SMTP-Server doesn't support StartTLS, <br />a normal SMTP-Connection will be used as fallback instead. <br /><i>Please note</i> that this checkbox creates a file in \"/tmp/\", <br />so this will cause problems under windows.</html>
 smtp_from=Address From:
 smtp_mail_settings=Mail settings
 smtp_message=Message:
 smtp_message_settings=Message settings
 smtp_messagesize=Calculate message size
 smtp_password=Password:
 smtp_plainbody=Send plain body (i.e. not multipart/mixed)
 smtp_replyto=Address Reply-To:
 smtp_sampler_title=SMTP Sampler
 smtp_security_settings=Security settings
 smtp_server_port=Port:
 smtp_server=Server:
 smtp_server_settings=Server settings
 smtp_subject=Subject:
 smtp_suppresssubj=Suppress Subject Header
 smtp_to=Address To:
 smtp_timestamp=Include timestamp in subject
 smtp_trustall=Trust all certificates
 smtp_trustall_tooltip=<html><b>Enforces</b> JMeter to trust all certificates, whatever CA it comes from.</html>
 smtp_truststore=Local truststore:
 smtp_truststore_tooltip=<html>The pathname of the truststore.<br />Relative paths are resolved against the current directory.<br />Failing that, against the directory containing the test script (JMX file)</html>
 smtp_useauth=Use Auth
 smtp_usetruststore=Use local truststore
 smtp_usetruststore_tooltip=<html>Allows JMeter to use a local truststore.</html>
 smtp_usenone=Use no security features
 smtp_username=Username:
 smtp_usessl=Use SSL
 smtp_usestarttls=Use StartTLS
 smtp_header_add=Add Header
 smtp_header_remove=Remove
 smtp_header_name=Header Name
 smtp_header_value=Header Value
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_title=SOAP/XML-RPC Request
 soap_send_action=Send SOAPAction: 
 spline_visualizer_average=Average
 spline_visualizer_incoming=Incoming
 spline_visualizer_maximum=Maximum
 spline_visualizer_minimum=Minimum
 spline_visualizer_title=Spline Visualizer
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
+start_no_timers=Start no pauses
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads.  Please be patient.
 stopping_test_failed=One or more test threads won't exit; see log file.
 stopping_test_title=Stopping Test
 string_from_file_encoding=File encoding if not the platform default (opt)
 string_from_file_file_name=Enter path (absolute or relative) to file
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 table_visualizer_bytes=Bytes
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
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
 template_field=Template\:
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
 test_action_pause=Pause
 test_action_stop=Stop
 test_action_stop_now=Stop Now
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
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
 test_fragment_title=Test Fragment
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
 toggle=Toggle
 tr=Turkish
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 transaction_controller_include_timers=Include timer duration in generated sample
 unbind=Thread Unbind
 unescape_html_string=String to unescape
 unescape_string=String containing Java escapes
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
 up=Up
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
 use_expires=Use Cache-Control/Expires header when processing GET requests
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for POST
 use_multipart_mode_browser=Browser-compatible headers
 use_recording_controller=Use Recording Controller
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 value=Value
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_assertion_error=Assertion error: 
 view_results_assertion_failure=Assertion failure: 
 view_results_assertion_failure_message=Assertion failure message: 
 view_results_autoscroll=Scroll automatically?
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_load_time=Load time: 
 view_results_render=Render: 
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML (download resources)
 view_results_render_json=JSON
 view_results_render_text=Text
 view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_too_large_message=Response too large to be displayed. Size: 
+view_results_response_partial_message=Start of message:
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
 view_results_search_pane=Search pane
 view_results_size_in_bytes=Size in bytes: 
 view_results_size_body_in_bytes=Body size in bytes: 
 view_results_size_headers_in_bytes=Headers size in bytes: 
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
 view_results_table_request_raw_nodata=No data to display
 view_results_table_request_params_key=Parameter name
 view_results_table_request_params_value=Value
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Raw
 view_results_table_result_tab_parsed=Parsed
 view_results_table_result_tab_raw=Raw
 view_results_thread_name=Thread Name: 
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_proxy_server_title=Proxy Server
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_testing2_source_ip=Source IP address:
 web_testing2_title=HTTP Request HTTPClient
 web_testing_concurrent_download=Use concurrent pool. Size:
 web_testing_embedded_url_pattern=Embedded URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources from HTML Files
 web_testing_title=HTTP Request
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
 webservice_configuration_wizard=WSDL helper
 webservice_get_xml_from_random_title=Use random messages SOAP
 webservice_methods=Web Methods
 webservice_message_soap=WebService message
 webservice_proxy_host=Proxy Host
 webservice_proxy_note=If Use HTTP Proxy is checked, but no host or port are provided, the sampler
 webservice_proxy_note2=will look at command line options. If no proxy host or port are provided by
 webservice_proxy_note3=either, it will fail silently.
 webservice_proxy_port=Proxy Port
 webservice_sampler_title=WebService(SOAP) Request
 webservice_soap_action=SOAPAction
 webservice_timeout=Timeout:
 webservice_use_proxy=Use HTTP Proxy
 while_controller_label=Condition (function or variable)
 while_controller_title=While Controller
 workbench_title=WorkBench
 wsdl_helper_error=The WSDL was not valid, please double check the url.
 wsdl_url=WSDL URL
 wsdl_url_error=The WSDL was emtpy.
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
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
diff --git a/src/core/org/apache/jmeter/resources/messages_es.properties b/src/core/org/apache/jmeter/resources/messages_es.properties
index 51827201f..e337b49bf 100644
--- a/src/core/org/apache/jmeter/resources/messages_es.properties
+++ b/src/core/org/apache/jmeter/resources/messages_es.properties
@@ -1,1071 +1,1073 @@
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
 about=Acerca de Apache JMeter
 add=A\u00F1adir
 add_as_child=A\u00F1adir como hijo
 add_parameter=A\u00F1adir Variable
 add_pattern=A\u00F1adir Patr\u00F3n\:
 add_test=A\u00F1adir Test
 add_user=A\u00F1adir Usuario
 add_value=A\u00F1adir Valor
 addtest=A\u00F1adir test
 aggregate_graph=Gr\u00E1ficos estad\u00EDsticos
 aggregate_graph_column=Columna
 aggregate_graph_display=Mostrar gr\u00E1fico
 aggregate_graph_height=Altura
 aggregate_graph_max_length_xaxis_label=Longitud m\u00E1xima de la etiqueta del eje x
 aggregate_graph_ms=Milisegundos
 aggregate_graph_response_time=Tiempo de respuesta
 aggregate_graph_save=Guardar gr\u00E1fico
 aggregate_graph_save_table=Guardar la tabla de datos
 aggregate_graph_save_table_header=Guardar la cabecera de la tabla
 aggregate_graph_title=Gr\u00E1fico
 aggregate_graph_use_group_name=\u00BFIncluir el nombre del grupo en la etiqueta?
 aggregate_graph_user_title=T\u00EDtulo del gr\u00E1fico
 aggregate_graph_width=Anchura
 aggregate_report=Informe Agregado
 aggregate_report_90=90%
 aggregate_report_90%_line=Linea de 90%
 aggregate_report_bandwidth=Kb/sec
 aggregate_report_count=\# Muestras
 aggregate_report_error=Error
 aggregate_report_error%=% Error
 aggregate_report_max=M\u00E1x
 aggregate_report_median=Mediana
 aggregate_report_min=M\u00EDn
 aggregate_report_rate=Rendimiento
 aggregate_report_stddev=Desv. Est\u00E1ndar
 aggregate_report_total_label=Total
 ajp_sampler_title=AJP/1.3 Muestreador
 als_message=Nota\: El Parser de Access Log tiene un dise\u00F1o gen\u00E9rico y le permite incorporar
 als_message2=su propio parser. Para hacer esto, implemente "LogParser", y a\u00F1ada el jar al
 als_message3=directorio /lib e introduzca la clases en el muestreador.
 analyze=Analizar Archivo de Datos...
 anchor_modifier_title=Parseador de Enlaces HTML
 appearance=Apariencia
 argument_must_not_be_negative=\u00A1El Argumento no puede ser negativo\!
 assertion_assume_success=Ignorar el Estado
 assertion_code_resp=C\u00F3digo de Respuesta
 assertion_contains=Contiene
 assertion_equals=igual
 assertion_headers=Cabeceras de la respuesta
 assertion_matches=Coincide
 assertion_message_resp=Mensaje de Respuesta
 assertion_not=No
 assertion_pattern_match_rules=Reglas de Coincidencia de Patrones
 assertion_patterns_to_test=Patr\u00F3n a Probar
 assertion_resp_field=Campo de Respuesta a Probar
 assertion_substring=Substring
 assertion_text_resp=Respuesta Textual
 assertion_textarea_label=Aserciones\:
 assertion_title=Aserci\u00F3n de Respuesta
 assertion_url_samp=URL Muestreada
 assertion_visualizer_title=Resultados de la Aserci\u00F3n
 attribute=Atributo
 attrs=Atributos
 auth_base_url=URL Base
 auth_manager_title=Gestor de Autorizaci\u00F3n HTTP
 auths_stored=Autorizaciones Almacenadas en el Gestor de Autorizaci\u00F3n
 average=Media
 average_bytes=Media de Bytes
 bind=Enlace a Hilo
 bouncy_castle_unavailable_message=Los jars para bouncy castle no est\u00E1n disponibles, por favor a\u00F1adalos a su classpath.
 browse=Navegar...
 bsf_sampler_title=Muestreador BSF
 bsf_script=Script a lanzar (variables\: log, Label, FileName, Parameters, args[], SampleResult (aka prev), sampler, ctx, vars, props, OUT)
 bsf_script_file=Archivo de Script a lanzar
 bsf_script_language=Lenguaje de Script\:
 bsf_script_parameters=Par\u00E1metros a pasar al script/archivo\:
 bsh_assertion_script=Script (ver abajo para las variables que est\u00E1n definidas)
 bsh_assertion_script_variables=Lectura/Escritura\: Failure, FailureMessage, SampleResult, log.\nS\u00F3lo Lectura\: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData
 bsh_assertion_title=Aserci\u00F3n BeanShell
 bsh_function_expression=Expresi\u00F3n a evaluar
 bsh_sampler_title=Muestreador BeanShell
 bsh_script=Script (ver abajo para las variables que est\u00E1n definidas)
 bsh_script_file=Archivo de script
 bsh_script_parameters=Par\u00E1metros (-> Par\u00E1metros String y String[]bsh.args)
 bsh_script_reset_interpreter=Resetear el int\u00E9rprete bsh antes de cada llamada
 bsh_script_variables=Las siguientes variables est\u00E1n definidas para el script\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Estoy ocupado probando, por favor pare el test antes de cambiar la configuraci\u00F3n
 cache_manager_title=Gestionador de la Cach\u00E9 HTTP
 cache_session_id=\u00BFIdentificador de la sesi\u00F3n de cach\u00E9?
 cancel=Cancelar
 cancel_exit_to_save=\u00BFHay elementos de prueba que no han sido salvados. \u00BFQuiere salvar antes de salir?
 cancel_new_to_save=\u00BFHay elementos del test que no han sido salvados. \u00BFQuiere salvar antes de limpiar el plan de pruebas?
 cancel_revert_project=Hay elementos del test que no han sido guardados. \u00BFDesea revertir a una versi\u00F3n guardada previamente del plan de test?
 char_value=N\u00FAmero del car\u00E1cter Unicode (decimal or 0xhex)
 choose_function=Elija una funci\u00F3n
 choose_language=Elija lenguaje
 clear=Limpiar
 clear_all=Limpiar Todo
 clear_cache_per_iter=\u00BFLimpiar la cach\u00E9 en cada iteraci\u00F3n?
 clear_cookies_per_iter=\u00BFLimpiar las cookies en cada iteraci\u00F3n?
 column_delete_disallowed=Borrar esta columna no est\u00E1 permitido
 column_number=N\u00FAmero de columna del archivo CSV | siguiente | *alias
 compare=Comparar
 comparefilt=Filtro de Comparaci\u00F3n
 comparison_differ_content=Las respuestas difieren en el contenido
 comparison_differ_time=Las respuestas difieren en el tiempo de respuesta en m\u00E1s de 
 comparison_invalid_node=Nodo inv\u00E1lido
 comparison_regex_string=Expresi\u00F3n regular
 comparison_regex_substitution=Sustituci\u00F3n
 comparison_response_time=Tiempo de respuesta\:
 comparison_unit=ms
 comparison_visualizer_title=Visualizador de la aserci\u00F3n de comparaci\u00F3n
 config_element=Elemento de Configuraci\u00F3n
 config_save_settings=Configurar
 configure_wsdl=Configurar
 constant_throughput_timer_memo=A\u00F1ade un retardo entre muestras para obtener un rendimiento constante
 constant_timer_delay=Retardo de Hilo (en milisegundos)\:
 constant_timer_memo=A\u00F1ade un retardo constante entre muestras
 constant_timer_title=Temporizador Constante
 content_encoding=Codificac\u00EDon del contenido\:
 controller=Controlador
 cookie_manager_policy=Pol\u00EDtica de Cookies
 cookie_manager_title=Gestor de Cookies HTTP
 cookies_stored=Cookies almacenadas en el Gestor de Cookies
 copy=Copiar
 corba_config_title=Configuraci\u00F3n de Muestreador CORBA
 corba_input_data_file=Archivo de Datos de Entrada\:
 corba_methods=Elija m\u00E9todo a invocar\:
 corba_name_server=Nombre del Servidor\:
 corba_port=Puerto\:
 corba_request_data=Datos de Entrada
 corba_sample_title=Muestreador CORBA
 counter_config_title=Contador
 counter_per_user=Contador independiente para cada usuario
 countlim=L\u00EDmite de tama\u00F1o
 csvread_file_file_name=Archivo CSV del que obtener valores | *alias
 cut=Cortar
 cut_paste_function=Funci\u00F3n de cadena para copiar y pegar
 database_conn_pool_max_usage=Uso m\u00E1ximo para cada Conexi\u00F3n
 database_conn_pool_props=Pool de Conexiones a Base de Datos
 database_conn_pool_size=N\u00FAmero de Conexiones en el Pool
 database_conn_pool_title=Valores por defecto del Pool de Conexiones JDBC
 database_driver_class=Clase del Driver\:
 database_login_title=Valores por defecto para el Login a JDBC
 database_sql_query_string=Query String de SQL\:
 database_sql_query_title=Valores por defecto de Query SQL JDBC
 database_testing_title=Petici\u00F3n JDBC
 database_url=URL JDBC\:
 database_url_jdbc_props=Driver JDBC y URL a Base de Datos
 ddn=DN 
 de=Alem\u00E1n
 debug_off=Deshabilitar depuraci\u00F3n
 debug_on=Habilitar depuraci\u00F3n
 default_parameters=Valores por defecto
 default_value_field=Valor por defecto\:
 delay=Retardo de arranque (segundos)
 delete=Borrar
 delete_parameter=Borrar Variable
 delete_test=Borrar Test
 delete_user=Borrar Usuario
 deltest=Test de borrado
 deref=Alias para desreferenciar
 disable=Deshabilitar
 distribution_graph_title=Gr\u00E1fico de Distribuci\u00F3n (alfa)
 distribution_note1=El gr\u00E1fico se actualiza cada 10 muestras
 dn=DN
 domain=Dominio
 done=Hecho
 duration=Duraci\u00F3n (segundos)
 duration_assertion_duration_test=Duraci\u00F3n a asegurar
 duration_assertion_failure=La operaci\u00F3n dur\u00F3 demasiado\: tard\u00F3 {0} milisegundos, cuando no deber\u00EDa haber tardado m\u00E1s de {1} milisegundos.
 duration_assertion_input_error=Por favor, introduzca un entero positivo v\u00E1lido.
 duration_assertion_label=Duraci\u00F3n en milisegundos\:
 duration_assertion_title=Aserci\u00F3n de Duraci\u00F3n
 edit=Editar
 email_results_title=Resultados del Email
 en=Ingl\u00E9s
 enable=Habilitar
 encode?=\u00BFCodificar?
 encoded_value=Valor de URL Codificada
 endtime=Tiempo de Finalizaci\u00F3n
 entry_dn=Introduzca DN
 entrydn=Introduzca DN
 error_loading_help=Error cargando p\u00E1gina de ayuda
 error_occurred=Error
 error_title=Error
 es=Espa\u00F1ol
 escape_html_string=Texto de escapado
 eval_name_param=Texto que contien variables y referencias de funci\u00F3n
 evalvar_name_param=Nombre de la variable
 example_data=Dato de muestra
 example_title=Muestreador de ejemplo
 exit=Salir
 expiration=Expiraci\u00F3n
 field_name=Nombre de campo
 file=Archivo
 file_already_in_use=Ese archivo est\u00E1 en uso
 file_visualizer_append=A\u00F1adir a archivo de datos existente
 file_visualizer_auto_flush=Limpiar autom\u00E1ticamente despu\u00E9s de cada muestra de datos
 file_visualizer_browse=Navegar...
 file_visualizer_close=Cerrar
 file_visualizer_file_options=Opciones de Archivo
 file_visualizer_filename=Nombre de archivo
 file_visualizer_flush=Limpiar
 file_visualizer_missing_filename=No se ha especificado nombre de archivo de salida.
 file_visualizer_open=Abrir
 file_visualizer_output_file=Escribir todos los datos a Archivo
 file_visualizer_submit_data=Incluir Datos Enviados
 file_visualizer_title=Informe de Archivo
 file_visualizer_verbose=Salida Verbosa
 filename=Nombre de Archivo
 follow_redirects=Seguir Redirecciones
 follow_redirects_auto=Redirigir Autom\u00E1ticamente
 foreach_controller_title=Controlador ForEach
 foreach_input=Prefijo de variable de entrada
 foreach_output=Nombre de variable de salida
 foreach_use_separator=\u00BFA\u00F1adir "_" antes de n\u00FAmero?
 format=Formato del n\u00FAmero
 fr=Franc\u00E9s
 ftp_binary_mode=\u00BFUsar modo binario?
 ftp_get=get(RETR)
 ftp_local_file=Fichero local\:
 ftp_local_file_contents=Contenidos del fichero local\:
 ftp_put=put(STOR)
 ftp_remote_file=Fichero remoto\:
 ftp_sample_title=Valores por defecto para petici\u00F3n FTP
 ftp_save_response_data=\u00BFGuardar fichero en la respuesta?
 ftp_testing_title=Petici\u00F3n FTP
 function_dialog_menu_item=Di\u00E1logo de Ayuda de Funci\u00F3n
 function_helper_title=Ayuda de Funci\u00F3n
 function_name_param=Nombre de funci\u00F3n. Usado para almacenar valores a utilizar en cualquier sitio del plan de prueba.
 function_name_paropt=Nombre de variable donde almacenar el resultado (opcional)
 function_params=Par\u00E1metros de Funci\u00F3n
 functional_mode=Modo de Prueba Funcional
 functional_mode_explanation=Seleccione modo de prueba funcional solo si necesita archivar los datos recibidos del servidor para cada petici\u00F3n.\nSeleccionar esta opci\u00F3n impacta en el rendimiento considerablemente. 
 gaussian_timer_delay=Desplazamiento para Retardo Constante (en milisegundos)\:
 gaussian_timer_memo=A\u00F1ade un retardo aleatorio con distribuci\u00F3n gaussiana.
 gaussian_timer_range=Desviaci\u00F3n (en milisegundos)\:
 gaussian_timer_title=Temporizador Aleatorio Gaussiano
 generate=Generar
 generator=Nombre de la clase Generadora
 generator_cnf_msg=No pude encontrar la clase generadora. Por favor aseg\u00FArese de que puso el archivo jar en el directorio /lib
 generator_illegal_msg=No pude acceder a la clase generadora debido a una "IllegalAcessException".
 generator_instantiate_msg=No pude crear una instancia del parser generador. Por favor aseg\u00FArese de que el generador implementa la interfaz Generator.
 get_xml_from_file=Archivo con datos SOAP XML (sobreescribe el texto anterior)
 get_xml_from_random=Carpeta de Mensaje
 graph_choose_graphs=Gr\u00E1ficos a Mostrar
 graph_full_results_title=Resultados de Gr\u00E1fico Completo
 graph_results_average=Media
 graph_results_data=Datos
 graph_results_deviation=Desviaci\u00F3n
 graph_results_latest_sample=\u00DAltima Muestra
 graph_results_median=Mediana
 graph_results_ms=ms
 graph_results_no_samples=No. de Muestras
 graph_results_throughput=Rendimiento
 graph_results_title=Gr\u00E1fico de Resultados
 grouping_add_separators=A\u00F1adir separadores entre grupos
 grouping_in_controllers=Poner cada grupo en un nuevo controlador
 grouping_in_transaction_controllers=Poner cada grupo en un nuevo controlador de transacciones
 grouping_mode=Agrupaci\u00F3n\:
 grouping_no_groups=No agrupar muestreadores
 grouping_store_first_only=Almacenar el primer muestreador de cada grupo solamente
 header_manager_title=Gestor de Cabecera HTTP
 headers_stored=Cabeceras Almacenadas en el Gestor de Cabeceras
 help=Ayuda
 help_node=\u00BFQu\u00E9 es este nodo?
 html_assertion_file=Escribir el reporte JTidy en fichero
 html_assertion_label=Aserci\u00F3n HTML
 html_assertion_title=Aserci\u00F3n HTML
 html_parameter_mask=M\u00E1scara de Par\u00E1metro HTML
 http_implementation=Implementaci\u00F3n HTTP\:
 http_response_code=c\u00F3digo de respuesta HTTP
 http_url_rewriting_modifier_title=Modificador de re-escritura HTTP URL
 http_user_parameter_modifier=Modificador de Par\u00E1metro de Usuario HTTP
 httpmirror_title=Servidor espejo HTTP
 id_prefix=Prefijo ID
 id_suffix=Sufijo ID
 if_controller_evaluate_all=\u00BFEvaluar para todos los hijos?
 if_controller_expression=\u00BFInterpretar la condici\u00F3n como una variable de expresi\u00F3n?
 if_controller_label=Condici\u00F3n
 if_controller_title=Controlador If
 ignore_subcontrollers=Ignorar bloques sub-controladores
 include_controller=Incluir Controlador
 include_equals=\u00BFIncluir Equals?
 include_path=Incluir Plan de Pruebas
 increment=Incrementar
 infinite=Sin f\u00EDn
 initial_context_factory=Factor\u00EDa Initial Context
 insert_after=Insertar Despu\u00E9s
 insert_before=Insertar Antes
 insert_parent=Insertar Padre
 interleave_control_title=Controlador Interleave
 intsum_param_1=Primer int a a\u00F1adir.
 intsum_param_2=Segundo int a a\u00F1adir - m\u00E1s ints pueden ser insertados a\u00F1adiendo m\u00E1s argumentos
 invalid_data=Dato inv\u00E1lido
 invalid_mail=Error al enviar el e-mail
 invalid_mail_address=Una o m\u00E1s direcciones de e-mail inv\u00E1lidas
 invalid_mail_server=Problema contactantdo el servidor de e-mail (mire los logs de JMeter)
 invalid_variables=Variables inv\u00E1lidas
 iteration_counter_arg_1=TRUE, para que cada usuario su propio contador, FALSE para tener un contador global
 iterator_num=Contador del bucle\:
 ja=Japon\u00E9s
 jar_file=Ficheros .jar
 java_request=Petici\u00F3n Java
 java_request_defaults=Valores por defecto para Petici\u00F3n Java
 javascript_expression=Expresi\u00F3n JavaScript a evaluar
 jexl_expression=Expresi\u00F3n JEXL a evaluar
 jms_auth_required=Requerido
 jms_client_caption=El cliente Receive utiliza TopicSubscriber.receive() para escuchar un mensaje.
 jms_client_caption2=MessageListener utiliza la interfaz onMessage(Message) para escuchar nuevos mensajes
 jms_client_type=Cliente
 jms_communication_style=Estilo de Comunicaci\u00F3n
 jms_concrete_connection_factory=Factor\u00EDa de Connection Concreto
 jms_config=Fuente del mensaje
 jms_config_title=Configuraci\u00F3n JMS
 jms_connection_factory=Factor\u00EDa de Connection
 jms_correlation_title=Usar campos alternativos para la correlaci\u00F3n de mensajes
 jms_dest_setup=Configuraci\u00F3n
 jms_dest_setup_dynamic=En cada muestra
 jms_dest_setup_static=Al arranque
 jms_error_msg=Object message deber\u00EDa leer de un archivo externo. Entrad de texto est\u00E1 seleccionada actu\u00E1lmente, recuerde cambiarlo.
 jms_file=Archivo
 jms_initial_context_factory=Factor\u00EDa de Initial Context
 jms_itertions=N\u00FAmero de muestras a agregar
 jms_jndi_defaults_title=Configuraci\u00F3n por defecto de JNDI
 jms_jndi_props=Propiedades JNDI
 jms_map_message=Mensaje Map
 jms_message_title=Propiedades de Mensaje
 jms_message_type=Tipo de Mensaje
 jms_msg_content=Contenido
 jms_object_message=Mensaje Object
 jms_point_to_point=JMS Punto-a-Punto
 jms_props=Propiedades JMS
 jms_provider_url=URL Proveedor
 jms_publisher=Publicador JMS
 jms_pwd=Contrase\u00F1a
 jms_queue=Cola
 jms_queue_connection_factory=Factor\u00EDa de QueueConnection
 jms_queueing=Recursos JMS
 jms_random_file=Archivo Aleatorio
 jms_read_response=Respuesta Le\u00EDda
 jms_receive_queue=Nombre JNDI cola Recepci\u00F3n
 jms_request=S\u00F3lo Petici\u00F3n
 jms_requestreply=Respuesta a Petici\u00F3n
 jms_sample_title=Petici\u00F3n JMS por defecto
 jms_send_queue=Nombre JNDI Cola Petici\u00F3n
 jms_stop_between_samples=\u00BFParar entre muestras?
 jms_subscriber_on_message=Utilizar MessageListener.onMessage()
 jms_subscriber_receive=Utilizar TopicSubscriber.receive()
 jms_subscriber_title=Suscriptor JMS
 jms_testing_title=Petici\u00F3n Mensajer\u00EDa
 jms_text_message=Mensaje Texto
 jms_timeout=Timeout (milisegundos)
 jms_topic=T\u00F3pico
 jms_use_auth=\u00BFUsar Autorizaci\u00F3n?
 jms_use_file=Desde archivo
 jms_use_non_persistent_delivery=\u00BFUsar modo de entrega no persistente?
 jms_use_properties_file=Utilizar archivo jndi.properties
 jms_use_random_file=Archivo Aleatorio
 jms_use_req_msgid_as_correlid=Usar el identificador del mensaje Request
 jms_use_res_msgid_as_correlid=Usar el identificador del mensaje Response
 jms_use_text=\u00C1rea de Texto
 jms_user=Usuario
 jndi_config_title=Configuraci\u00F3n JNDI
 jndi_lookup_name=Interfaz Remota
 jndi_lookup_title=Configuraci\u00F3n del Lookup JNDI
 jndi_method_button_invoke=Invocar
 jndi_method_button_reflect=Reflejar
 jndi_method_home_name=Nombre de M\u00E9todo Home
 jndi_method_home_parms=Par\u00E1metros de M\u00E9todo Home
 jndi_method_name=Configuraci\u00F3n de M\u00E9todo
 jndi_method_remote_interface_list=Interfaces Remotas
 jndi_method_remote_name=Nombre de M\u00E9todo Remoto
 jndi_method_remote_parms=Par\u00E1metros de M\u00E9todo Remoto
 jndi_method_title=Configuraci\u00F3n de M\u00E9todo Remoto
 jndi_testing_title=Petici\u00F3n JNDI
 jndi_url_jndi_props=Propiedades JNDI
 junit_append_error=A\u00F1adir errores de aserci\u00F3n
 junit_append_exception=A\u00F1adir excepciones de ejecuci\u00F3n
 junit_constructor_error=Imposible crear una instancia de la clase
 junit_constructor_string=Etiqueta del constructor de String
 junit_do_setup_teardown=No llamar a setUp y tearDown
 junit_error_code=C\u00F3digo de error
 junit_error_default_code=9999
 junit_error_default_msg=Ocurri\u00F3 un error no esperado
 junit_error_msg=Mensaje de error
 junit_failure_code=Codigo de fallo
 junit_failure_default_code=0001
 junit_failure_default_msg=Test fall\u00F3
 junit_failure_msg=Mensaje de fallo
 junit_junit4=Buscar anotaciones JUnit 4 (en el caso de JUnit 3)
 junit_pkg_filter=Filtro de paquetes
 junit_request=Petici\u00F3n JUnit
 junit_request_defaults=Valores por defecto de la petici\u00F3n JUnit
 junit_success_code=C\u00F3digo de \u00E9xito
 junit_success_default_code=1000
 junit_success_default_msg=Test satisfactorio
 junit_success_msg=Mensaje de \u00E9xito
 junit_test_config=Par\u00E1metros del test JUnit
 junit_test_method=M\u00E9todo de Test
 ldap_argument_list=Lista de LDAPArgument
 ldap_connto=Timeout de conexi\u00F3n (en milisegundos)
 ldap_parse_results=\u00BFParsear los resultados de la b\u00FAsqueda?
 ldap_sample_title=Valores por defecto Petici\u00F3n LDAP
 ldap_search_baseobject=Realizar la b\u00FAsqueda 'baseobject'
 ldap_search_onelevel=Realizar la b\u00FAsqueda 'onelevel'
 ldap_search_subtree=Realizar la b\u00FAsqueda 'subtree'
 ldap_secure=\u00BFUsar el Protocolo LDAP Seguro?
 ldap_testing_title=Petici\u00F3n LDAP
 ldapext_sample_title=Valores por defecto Petici\u00F3n Extendidad LDAP
 ldapext_testing_title=Petici\u00F3n Extendida LDAP
 library=Librer\u00EDa
 load=Cargar
 load_wsdl=Cargar WSDL
 log_errors_only=Escribir en Log S\u00F3lo Errores
 log_file=Ubicaci\u00F3n del archivo de logs
 log_function_comment=Comentario adicional (opcional)
 log_function_level=Nivel de Log (por defecto INFO) o OUT o ERR
 log_function_string=Texto a escribir en log
 log_function_string_ret=Texto a ser escrito en log  (y retornado)
 log_function_throwable=Texto para 'Throwable' (Opcional) 
 log_only=Log/Mostrar s\u00F3lo\:
 log_parser=Nombre de la clase Parser de Log
 log_parser_cnf_msg=No pude encontrar la clase. Por favor, aseg\u00FArese de colocar el archivo jar en el directorio /lib.
 log_parser_illegal_msg=No pude acceder a la clase debido a una "IllegalAcessException".
 log_parser_instantiate_msg=No pude crear una instancia del parser de log. Por favor aseg\u00FArese de que el parser implementar la interfaz LogParser.
 log_sampler=Muestreador de Log de Acceso de Tomcat
 log_success_only=\u00C9xitos
 logic_controller_title=Controlador Simple
 login_config=Configuraci\u00F3n de Login
 login_config_element=Elemento de Configuraci\u00F3n de Login
 longsum_param_1=Primer 'long' a a\u00F1adir
 longsum_param_2=Segundo 'long' a a\u00F1adir - m\u00E1s 'longs' pueden ser sumados a\u00F1adiendo m\u00E1s argumentos.
 loop_controller_title=Controlador Bucle
 looping_control=Control de Bucles
 lower_bound=L\u00EDmite inferior
 mail_reader_account=Usuario\:
 mail_reader_all_messages=Todo
 mail_reader_delete=Borrar archivos del servidor
 mail_reader_folder=Carpeta\:
 mail_reader_num_messages=N\u00FAmero de mensajes a recuperar\:
 mail_reader_password=Contrase\u00F1a\:
 mail_reader_port=Puerto del servidor (opcional)\:
 mail_reader_server=Servidor\:
 mail_reader_server_type=Tipo de Servidor\:
 mail_reader_storemime=Almacenar el mensaje usando MIME(raw)
 mail_reader_title=Muestreador Lector de Correo
 mail_sent=Mail enviado con \u00E9xito
 mailer_attributes_panel=Atributos de Mailing
 mailer_error=No pude enviar mail. Por favor, corrija las entradas incorrectas.
 mailer_visualizer_title=Visualizador de Mailer
 match_num_field=Coincidencia No. (0 para Aleatorio)\:
 max=M\u00E1ximo
 maximum_param=El valor m\u00E1ximo permitido para un rango de valores
 md5hex_assertion_failure=Error validando MD5\: obtuve {0} pero deber\u00EDa haber obtenido {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex a Comprobar
 md5hex_assertion_title=Aserci\u00F3n MD5Hex
 memory_cache=Cach\u00E9 en Memoria
 menu_assertions=Aserciones
 menu_close=Cerrar
 menu_collapse_all=Colapsar todo
 menu_config_element=Elemento de Configuraci\u00F3n
 menu_edit=Editar
 menu_expand_all=Expandir todo
 menu_fragments=Fragmento de Prueba
 menu_generative_controller=Muestreador
 menu_listener=Receptor
 menu_logic_controller=Controlador L\u00F3gico
 menu_merge=Mezclar
 menu_modifiers=Modificadores
 menu_non_test_elements=Elementos NoDePrueba
 menu_open=Abrir
 menu_post_processors=Post Procesadores
 menu_pre_processors=Pre Procesadores
 menu_response_based_modifiers=Modificadores Basados en Respuesta
 menu_tables=Tabla
 menu_threads=Hilos (Usuarios)
 menu_timer=Temporizador
 metadata=MetaDatos
 method=M\u00E9todo\:
 mimetype=Tipo MIME
 minimum_param=El valor m\u00EDnimo admitido para un rango de valores
 minute=minuto
 modddn=Nombre de entrada antiguo
 modification_controller_title=Controlador de Modificaci\u00F3n
 modification_manager_title=Gestor de Modificaci\u00F3n
 modify_test=Prueba de Modificaci\u00F3n
 modtest=Prueba de Modificaci\u00F3n
 module_controller_module_to_run=M\u00F3dulo a ejecutar
 module_controller_title=Controlador de M\u00F3dulo
 module_controller_warning=No pudo encontrar el m\u00F3dulo\:
 monitor_equation_active=Activo\: (ocupado/m\u00E1x) > 25%
 monitor_equation_dead=Muerto\: no hay respuesta
 monitor_equation_healthy=Sano. (ocupado/m\u00E1x) < 25%
 monitor_equation_load=Carga\: ((ocupado/m\u00E1x) * 50) + ((memoria usada/memoria m\u00E1x) * 50)
 monitor_equation_warning=Aviso\: (ocupado/m\u00E1x) > 67%
 monitor_health_tab_title=Salud
 monitor_health_title=Resultados del Monitor
 monitor_is_title=Utilizar como Monitor
 monitor_label_left_bottom=0 %
 monitor_label_left_middle=50 %
 monitor_label_left_top=100 %
 monitor_label_prefix=Prefijo de conexi\u00F3n
 monitor_label_right_active=Activo
 monitor_label_right_dead=Muerto
 monitor_label_right_healthy=Sano
 monitor_label_right_warning=Aviso
 monitor_legend_health=Salud
 monitor_legend_load=Carga
 monitor_legend_memory_per=Memoria % (usada/total)
 monitor_legend_thread_per=Hilo % (ocupado/m\u00E1x)
 monitor_load_factor_mem=50
 monitor_load_factor_thread=50
 monitor_performance_servers=Servidores
 monitor_performance_tab_title=Rendimiento
 monitor_performance_title=Gr\u00E1fico de Rendimiento
 name=Nombre\:
 new=Nuevo
 newdn=Nuevo distinghuised name
 no=Noruego
 number_of_threads=N\u00FAmero de Hilos
 obsolete_test_element=Este elemento de test es obsoleto
 once_only_controller_title=Controlador Only Once
 opcode=opCode
 open=Abrir...
 option=Opciones
 optional_tasks=Tareas Opcionales
 paramtable=Enviar Par\u00E1metros Con la Petici\u00F3n\:
 password=Contrase\u00F1a
 paste=Pegar
 paste_insert=Pegar como Inserci\u00F3n
 path=Ruta\:
 path_extension_choice=Extensi\u00F3n de Path (utilice ";" como separador)
 path_extension_dont_use_equals=No utilice el signo igual en la extensi\u00F3n del path (compatibilidad con Intershop Enfinity)
 path_extension_dont_use_questionmark=No utilice el signo interrogaci\u00F3n en la extensi\u00F3n del path (compatibilidad con Intershop Enfinity)
 patterns_to_exclude=URL Patrones a Excluir
 patterns_to_include=URL Patrones a Incluir
 pkcs12_desc=Clave PKCS (*.p12)
 pl=Polaco
 port=Puerto\:
 post_thread_group_title=Tirar abajo grupo de Hilos
 property_as_field_label={0}\:
 property_default_param=Valor por defecto
 property_edit=Editar
 property_editor.value_is_invalid_message=El texto que acaba de introducir no es un valor v\u00E1lido para esta propiedad. La propiedad ser\u00E1 devuelta a su valor anterior.
 property_editor.value_is_invalid_title=Entrada inv\u00E1lida
 property_name_param=Nombre de propiedad
 property_returnvalue_param=\u00BFRetornar el valor original de la propiedad (falso, por defecto)?
 property_tool_tip={0}\: {1}
 property_undefined=No definido
 property_value_param=Valor de propiedad
 property_visualiser_title=Visualizador de propiedades
 protocol=Protocolo\:
 protocol_java_border=Clase java
 protocol_java_classname=Nombre de clase\:
 protocol_java_config_tile=Muestra de Configure Java
 protocol_java_test_title=Test Java
 provider_url=URL Proveedor
 proxy_assertions=A\u00F1adir Aserciones
 proxy_cl_error=Si est\u00E1 especificando un servidor proxy, el puerto y el host deben ser provistos.
 proxy_content_type_exclude=Excluir\:
 proxy_content_type_filter=Filtro de tipo de contenido
 proxy_content_type_include=Incluir\:
 proxy_daemon_bind_error=No pudo crear el proxy - puerto en uso. Escoger otro puerto.
 proxy_daemon_error=No pudo crear el proxy - ver traza para m\u00E1s detalles
 proxy_headers=Capturar Cabeceras HTTP
 proxy_httpsspoofing=Intentar usurpado HTTPS (HTTPS spoofing)
 proxy_httpsspoofing_match=Filtro de URLs para usurpaci\u00F3n HTTPS (HTTPS spoofing) \:
 proxy_regex=Coincidencia Regex
 proxy_sampler_settings=Par\u00E1metros muestra HTTP
 proxy_sampler_type=Tipo\:
 proxy_separators=A\u00F1adir Separadores
 proxy_target=Controlador Objetivo\:
 proxy_test_plan_content=Contenido del plan de pruebas
 proxy_title=Servidor Proxy HTTP
 pt_br=Portugu\u00E9s (Brasile\u00F1o)
 ramp_up=Periodo de Subida (en segundos)\:
 random_control_title=Controlador Aleatorio
 random_order_control_title=Controlador Orden Aleatorio
 read_response_message=La lectura de respuesta no est\u00E1 activada. Para ver la respuesta, por favor marque la caja en el sampler.
 read_response_note=Si "leer respuesta" est\u00E1 desactivado, el muestreador no leer\u00E1 la respuesta
 read_response_note2=ni establecer\u00E1 el "SampleResult". Esto mejora el rendimiento, pero significa
 read_response_note3=que el contenido de respuesta no ser\u00E1 logado.
 read_soap_response=Leer Respuesta SOAP
 realm=Dominio (realm)
 record_controller_title=Controlador Grabaci\u00F3n
 ref_name_field=Nombre de Referencia\:
 regex_extractor_title=Extractor de Expresiones Regulares
 regex_field=Expresi\u00F3n Regular\:
 regex_source=Campo de Respuesta a comprobar
 regex_src_body=Cuerpo
 regex_src_body_unescaped=Cuerpo (No escapado)
 regex_src_hdrs=Cabeceras
 regex_src_url=URL
 regexfunc_param_1=Expresi\u00F3n regular usada para buscar resultados en la peticiones previas
 regexfunc_param_2=Plantilla para la cadena de sustituci\u00F3n, utilizando grupos de la expresi\u00F3n regular. El formato es $[grupo]$.\nEjemplo $1$.
 regexfunc_param_3=Qu\u00E9 coincidencia utilizar. Un entero 1 o mayor, RAND para indicar a JMeter que utilice un n\u00FAmero aleatorio, un floar o ALL para indicar que todas las coincidencias deber\u00EDan ser utilizadas
 regexfunc_param_4=Texto intermedio. Si se selecciona ALL, the texto intermedio ser\u00E1 utilizado para generar los resultados
 regexfunc_param_5=Texto por Defecto. Utilizado en lugar de la plantilla si la expresi\u00F3n regular no encuentra coincidencias. 
 regexfunc_param_7=Nombre de la variable de entrada que contiene el texto a ser parseado ([muestra anterior])
 regexp_render_no_text=El dato de respuesta del resultado no es texto.
 regexp_tester_button_test=Test
 regexp_tester_field=Expresi\u00F3n regular\:
 regexp_tester_title=Testeador de RegExp
 remote_error_init=Error inicializando el servidor remoto
 remote_error_starting=Error arrancando el servidor remoto
 remote_exit=Salir Remoto
 remote_exit_all=Salir de Todo Remoto
 remote_shut=Apagar remoto
 remote_shut_all=Apagar todo remoto
 remote_start=Arrancar Remoto
 remote_start_all=Arrancar Todo Remoto
 remote_stop=Parar Remoto
 remote_stop_all=Parar Todo Remoto
 remove=Borrar
 rename=Renombrar entrada
 report=Informe
 report_bar_chart=Gr\u00E1fico de barras
 report_bar_graph_url=URL
 report_base_directory=Directorio base
 report_chart_caption=Leyenda del gr\u00E1fico
 report_chart_x_axis=Eje X
 report_chart_x_axis_label=Etiqueta para el eje X
 report_chart_y_axis=Eje Y
 report_chart_y_axis_label=Etiqueta para el eje Y
 report_line_graph=Gr\u00E1fico de l\u00EDneas
 report_line_graph_urls=Incluir URL
 report_output_directory=Directorio de salida para el informe
 report_page=P\u00E1gina del informe
 report_page_element=Elemento de p\u00E1gina
 report_page_footer=Pie de p\u00E1gina
 report_page_header=Cabecera de p\u00E1gina
 report_page_index=Crear \u00EDndice de p\u00E1gina
 report_page_intro=P\u00E1gina de introducci\u00F3n
 report_page_style_url=URL de la hoja de estilos
 report_page_title=T\u00EDtulo de p\u00E1gina
 report_pie_chart=Gr\u00E1fico de tarta
 report_plan=Esquema del reporte
 report_select=Seleccionar
 report_summary=Resumen de informe
 report_table=Tabla de informe
 report_writer=Escritor del reporte
 report_writer_html=Escritor HTML del reporte
 request_data=Pedir Datos
 reset_gui=Resetear GUI
 response_save_as_md5=\u00BFGuardar la respuesta como MD5 hash?
 restart=Rearranque
 resultaction_title=Manejador de Acci\u00F3n para Status de Resultados 
 resultsaver_errors=Guardar Respuestas Fallidas Solamente
 resultsaver_prefix=Prefijo de nombre de archivo\:
 resultsaver_skipautonumber=No a\u00F1adir n\u00FAmero al prefijo
 resultsaver_skipsuffix=No a\u00F1adir sufijo
 resultsaver_success=Guardar s\u00F3lo respuestas satisfactorias
 resultsaver_title=Guardar respuestas en archivo
 resultsaver_variable=Nombre de variable\:
 retobj=Devolver objeto
 reuseconnection=Reusar conexi\u00F3n
 revert_project=Revertir
 revert_project?=\u00BFRevertir proyecto?
 root=Ra\u00EDz
 root_title=Ra\u00EDz
 run=Lanzar
 running_test=Test lanzado
 runtime_controller_title=Controlador Tiempo de Ejecuci\u00F3n
 runtime_seconds=Tiempo de ejecuci\u00F3n (segundos)
 sample_result_save_configuration=Guardar Configuraci\u00F3n de Resultado de Muestra
 sample_scope=Aplicar a\:
 sample_scope_all=Muestra principal y submuestras
 sample_scope_children=S\u00F3lo submuestras
 sample_scope_parent=S\u00F3lo muestra principal
 sample_scope_variable=Variable JMeter
 sampler_label=Etiqueta
 sampler_on_error_action=Acci\u00F3n a tomar despu\u00E9s de un error de Muestreador
 sampler_on_error_continue=Continuar
 sampler_on_error_start_next_loop=Comenzar siguiente iteraci\u00F3n
 sampler_on_error_stop_test=Parar Test
 sampler_on_error_stop_test_now=Parar test ahora
 sampler_on_error_stop_thread=Parar Hilo
 save=Guardar
 save?=\u00BFGuardar?
 save_all_as=Guardar Plan de Pruebas como
 save_as=Guardar selecci\u00F3n como...
 save_as_error=\u00A1M\u00E1s de un item seleccionado\!
 save_as_image=Guardar como imagen
 save_as_image_all=Guardar la pantalla como imagen
 save_assertionresultsfailuremessage=Guardar Mensaje de Fallo de Resultados de Aserci\u00F3n
 save_assertions=Guardar Resultados de Aserci\u00F3n
 save_asxml=Guardar Como XML
 save_bytes=Guardar conteo de bytes
 save_code=Guardar C\u00F3digo de Respuesta
 save_datatype=Guardar Tipo de Datos
 save_encoding=Guardar Codificaci\u00F3n
 save_fieldnames=Guardar Nombre de Campo
 save_filename=Guardar el nombre del fichero de respuesta
 save_graphics=Guardar Gr\u00E1ficos
 save_hostname=Guardar el nombre de host
 save_idletime=Guardar tiempo inactivo
 save_label=Guardar Etiqueta
 save_latency=Guardar Latencia
 save_message=Guardar Mensaje de Respuesta
 save_overwrite_existing_file=El fichero seleccionado ya existe, \u00BFquiere sobreescribirlo?
 save_requestheaders=Guardar Cabeceras de Petici\u00F3n
 save_responsedata=Guardar Datos de Respuesta
 save_responseheaders=Guardar Cabeceras de Respuesta
 save_samplecount=Guardar muestra y conteo de error
 save_samplerdata=Guardar Datos de Muestreador
 save_subresults=Guardar Sub Resultados
 save_success=Guardado Correctamente
 save_threadcounts=Guardar conteos hilos activos
 save_threadname=Guardar Nombre de Hilo
 save_time=Guardar Tiempo
 save_timestamp=Guardar Etiqueta de Tiempo
 save_url=Guardar URL
 sbind=Conexi\u00F3n/Desconexi\u00F3n Simple
 scheduler=Planificador
 scheduler_configuration=Configuraci\u00F3n del Planificador
 scope=\u00C1mbito
 search_base=Base de B\u00FAsqueda
 search_filter=Filtro de B\u00FAsqueda
 search_test=Prueba de B\u00FAsqueda
 search_text_button_close=Cerrar
 search_text_button_find=Encontrar
 search_text_button_next=Encontrar siguiente
 search_text_chkbox_case=Sensible a may\u00FAsculas
 search_text_chkbox_regexp=Expresi\u00F3n regular
 search_text_field=Buscar\:
 search_text_msg_not_found=Texto no encontrado
 search_text_title_not_found=No encontrado
 searchbase=Base de B\u00FAsqueda
 searchfilter=Filtro de B\u00FAsqueda
 searchtest=Prueba de B\u00FAsqueda
 second=segundo
 secure=Seguro
 send_file=Enviar un archivo Con la Petici\u00F3n
 send_file_browse=Navegar...
 send_file_filename_label=Nombre de Archivo\:
 send_file_mime_label=Tipo MIME\:
 send_file_param_name_label=Nombre de Par\u00E1metro\:
 server=Nombre de Servidor o IP\:
 servername=Nombre de Servidor\:
 session_argument_name=Nombre de Argumento de Sesi\u00F3n
 setup_thread_group_title=Montar grupo de Hilos
 should_save=Deber\u00EDa guardar el plan de pruebas antes de lanzarlo. Si est\u00E1 utilizando archivos de datos (ie, para DCV o _StringFromFile), entonces es especialmente importante que primero guarde su script de prueba.
 shutdown=Interrumpir
 simple_config_element=Elemento de Configuraci\u00F3n Simple
 simple_data_writer_title=Escritor de Datos Simple
 size_assertion_comparator_error_equal=siendo igual a
 size_assertion_comparator_error_greater=siendo mayor que
 size_assertion_comparator_error_greaterequal=siendo mayor o igual a
 size_assertion_comparator_error_less=siendo menor que
 size_assertion_comparator_error_lessequal=siendo menor o igual que
 size_assertion_comparator_error_notequal=no siendo igual a
 size_assertion_comparator_label=Tipo de Comparaci\u00F3n
 size_assertion_failure=El resultado tuvo el tama\u00F1o incorrecto\: fu\u00E9 {0} bytes, pero deber\u00EDa haber sido {1} {2} bytes.
 size_assertion_input_error=Por favor, introduzca un entero positivo v\u00E1lido.
 size_assertion_label=Tama\u00F1o en bytes\:
 size_assertion_size_test=Tama\u00F1o a Comprobar
 size_assertion_title=Aserci\u00F3n de Tama\u00F1o
 smime_assertion_issuer_dn=Nombre \u00FAnico del emisor\:
 smime_assertion_message_position=Ejecutar aserci\u00F3n sobre el mensaje a partir de la posici\u00F3n
 smime_assertion_not_signed=Mensaje no firmado
 smime_assertion_signature=Firma
 smime_assertion_signer=Cerficado del firmante
 smime_assertion_signer_by_file=Certificado
 smime_assertion_signer_constraints=Chequear valores
 smime_assertion_signer_dn=Nombre \u00FAnico del firmante
 smime_assertion_signer_email=Direcci\u00F3n de correo del firmante
 smime_assertion_signer_no_check=No chequear
 smime_assertion_signer_serial=N\u00FAmero de serie
 smime_assertion_title=Aserci\u00F3n SMIME
 smime_assertion_verify_signature=Verificar firma
 smtp_additional_settings=Par\u00E1metros adicionales
 smtp_attach_file=Adjuntar fichero(s)\:
 smtp_attach_file_tooltip=Separar ficheros con ";"
 smtp_auth_settings=Par\u00E1metros de autentificaci\u00F3n
 smtp_bcc=Direcciones en copia oculta (BCC)\:
 smtp_cc=Direcciones en copia(CC)\:
 smtp_default_port=(Por defecto\: SMTP\:25, SSL\:465, StartTLS\:587)
 smtp_eml=Enviar .eml\:
 smtp_enabledebug=\u00BFActivar las trazas de depuraci\u00F3n?
 smtp_enforcestarttls=Imponer StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Forza</b> al servidor a usar StartTLS.<br />Si no es seleccionado el servidor SMTP no soporta  StartTLS, <br />una conexi\u00F3n normal SMTP ser\u00E1 usada como reserva. <br /><i>Por favor advierta</i> que este objeto crea un fichero en "/tmp/", <br />so Esto causar\u00E1 problemas bajo Windows.</html>
 smtp_from=Direcci\u00F3n Desde\:
 smtp_header_add=A\u00F1adir cabecera
 smtp_header_name=Nombre de cabecera
 smtp_header_remove=Suprimir
 smtp_header_value=Valor de cabecera
 smtp_mail_settings=Par\u00E1metros del correo
 smtp_message=Mensaje\:
 smtp_message_settings=Par\u00E1metros del mensaje\:
 smtp_messagesize=Calcular tama\u00F1o del mensaje
 smtp_password=Contrase\u00F1a\:
 smtp_plainbody=Enviar texto plano(i.e. no multipart/mixed)
 smtp_replyto=Direcci\u00F3n Responder-a\:
 smtp_sampler_title=Muestra SMTP
 smtp_security_settings=Par\u00E1metros de seguridad
 smtp_server=Servidor\:
 smtp_server_port=Puerto\:
 smtp_server_settings=Par\u00E1metros del servidor
 smtp_subject=Asunto\:
 smtp_suppresssubj=Suprimir la cabecera del asunto
 smtp_timestamp=Incluir timestamp en el asunto
 smtp_to=Direcci\u00F3n A\:
 smtp_trustall=Verificar todos  los certificados
 smtp_trustall_tooltip=<html><b>Fuerza</b> a JMeter a verificar todos los certificados, que vienen del CA.</html>
 smtp_truststore=Almacenamiento local de confianza\:
 smtp_truststore_tooltip=<html>Nombre de la ruta del almacenamiento local de confianza.<br />Rutas relativas son resueltas contra el directorio actual.<br />Si esto falla, contra el directorio que contiene el script de test (JMX file)</html>
 smtp_useauth=Usar autentificaci\u00F3n
 smtp_usenone=No usar funcionalidades de seguridad
 smtp_username=Nombre de usuario\:
 smtp_usessl=Usar SSL
 smtp_usestarttls=Usar StartTLS
 smtp_usetruststore=Usar almacenamiento local de confianza
 smtp_usetruststore_tooltip=<html>Permite a JMeter usar un almacenamiento de confianza local.</html>
 soap_action=Acci\u00F3n Soap
 soap_data_title=Datos Soap/XML-RPC
 soap_sampler_title=Petici\u00F3n Soap/XML-RPC
 soap_send_action=Enviar SOAPAction\:
 spline_visualizer_average=Media
 spline_visualizer_incoming=Entrando
 spline_visualizer_maximum=M\u00E1ximo
 spline_visualizer_minimum=M\u00EDnimo
 spline_visualizer_title=Visualizador Spline
 spline_visualizer_waitingmessage=Esperando muestras
 split_function_separator=Texto para separar. Por defecto es , (coma)
 split_function_string=Texto a separar
 ssl_alias_prompt=Por favor, introduzca su alias favorito
 ssl_alias_select=Seleccione su alias para la prueba
 ssl_alias_title=Alias de Cliente
 ssl_error_title=Problema con el KeyStore
 ssl_pass_prompt=Por favor, introduzca su contrase\u00F1a
 ssl_pass_title=Contrase\u00F1a de KeyStore
 ssl_port=Puerto SSL
 sslmanager=Gestor SSL
 start=Arrancar
+start_no_timers=Inicio no se detiene
 starttime=Tiempo de Arranque
 stop=Parar
 stopping_test=Parando todos los hilos. Por favor, sea paciente.
 stopping_test_failed=Uno o m\u00E1s hilos de test no saldr\u00E1n; ver fichero de log.
 stopping_test_title=Parando la Prueba
 string_from_file_encoding=Codificaci\u00F3n, si no el por defecto de la plataforma (opcional) 
 string_from_file_file_name=Introduzca ruta completa al archivo
 string_from_file_seq_final=N\u00FAmero final de secuencia de archivo
 string_from_file_seq_start=N\u00FAmero inicial de secuencia de archivo
 summariser_title=Generar Resumen de Resultados
 summary_report=Reporte resumen
 switch_controller_label=Conmutar Valor
 switch_controller_title=Conmutar Controlador
 table_visualizer_bytes=Bytes
 table_visualizer_sample_num=Muestra \#
 table_visualizer_sample_time=Tiempo de Muestra (ms)
 table_visualizer_start_time=Tiempo de comienzo
 table_visualizer_status=Estado
 table_visualizer_success=\u00C9xito
 table_visualizer_thread_name=Nombre del hilo
 table_visualizer_warning=Alerta
 tcp_classname=Nombre de clase TCPClient\:
 tcp_config_title=Configuraci\u00F3n de Muestreador TCP
 tcp_nodelay=Establecer SinRetardo
 tcp_port=Puerto\:
 tcp_request_data=Texto a enviar
 tcp_sample_title=Muestreador TCP
 tcp_timeout=Timeout (milisegundos)
 template_field=Plantilla\:
 test=Prueba
 test_action_action=Acci\u00F3n
 test_action_duration=Duraci\u00F3n
 test_action_pause=Pausa
 test_action_stop=Parar
 test_action_stop_now=Parar ahora
 test_action_target=Objetivo
 test_action_target_test=Todos los Hilos
 test_action_target_thread=Hilo Actual
 test_action_title=Acci\u00F3n de Prueba
 test_configuration=Configuraci\u00F3n de Pruebas
 test_fragment_title=Fragmento de Prueba
 test_plan=Plan de Pruebas
 test_plan_classpath_browse=A\u00F1adir directorio o jar al classpath
 testconfiguration=Configuraci\u00F3n de Pruebas
 testplan.serialized=Lanza cada Grupo de Hilos separadamente (i.e. lanza un grupo antes de lanzar el siguiente)
 testplan_comments=Comentarios
 testt=Prueba
 textbox_cancel=Cancelar
 textbox_close=Cerrar
 textbox_save_close=Guardar y cerrar
 textbox_title_edit=Editar texto
 textbox_title_view=Ver texto
 textbox_tooltip_cell=Doble click para ver/editar
 thread_delay_properties=Propiedades de Retardo de Hilos
 thread_group_title=Grupo de Hilos
 thread_properties=Propiedades de Hilo
 threadgroup=Grupo de Hilos
 throughput_control_bynumber_label=Ejecuciones Totales
 throughput_control_bypercent_label=Porcentaje de Ejecuciones
 throughput_control_perthread_label=Por Usuario
 throughput_control_title=Controlador Throughput
 throughput_control_tplabel=Rendimiento
 time_format=Cadena de formateo para SimpleDateFormat(opcional)
 timelim=L\u00EDmite de Tiempo
 tr=Turco
 transaction_controller_include_timers=Incluir duraci\u00F3n del temporizador en la muestra generada
 transaction_controller_parent=Generar muestra padre
 transaction_controller_title=Controlador Transaction
 unbind=Desligar Hilo
 unescape_html_string=Cadena de texto para quitar caracteres de escapado
 unescape_string=Cadena de texto contiene caracteres Java de escapado
 uniform_timer_delay=Desplazamiento de Retraso Constante (en milisegundos)\:
 uniform_timer_memo=A\u00F1ade un retardo aleatorio con una distribuci\u00F3n uniforme
 uniform_timer_range=M\u00E1ximo retardo Aleatorio (en milisegundos)
 uniform_timer_title=Temporizador Aleatorio Uniforme
 update_per_iter=Actualizar Una Vez Por Iteraci\u00F3n
 upload=Subida de Archivo
 upper_bound=L\u00EDmite Superior
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocolo\:
 url_config_title=Valores por Defecto para Petici\u00F3n HTTP
 url_full_config_title=Muestra UrlFull
 url_multipart_config_title=Valores por Defecto para Petici\u00F3n HTTP Multipart
 use_expires=Usar cabecera 'Cache-Control/Expires' cuando se procesan peticiones GET
 use_keepalive=Utilizar KeepAlive
 use_multipart_for_http_post=Usar 'multipart/form-data' para HTTP POST
 use_multipart_mode_browser=Cabeceras compatibles con navegadores
 use_recording_controller=Utilizar Controlador Recording
 user=Usuario
 user_defined_test=Prueba Definida por el Usuario
 user_defined_variables=Variables definidas por el Usuario
 user_param_mod_help_note=(No cambie esto. En su lugar, modifique el archivo con ese nombre en el directorio /bin de JMeter)
 user_parameters_table=Par\u00E1metros
 user_parameters_title=Par\u00E1metros de Usuario
 userdn=Nombre de Usuario
 username=Nombre de Usuario
 userpw=Contrase\u00F1a
 value=Valor
 var_name=Nombre de Referencia
 variable_name_param=Nombre de variable(puede incluir variables y referencias a funci\u00F3n)
 view_graph_tree_title=Ver \u00C1rbol Gr\u00E1fico
 view_results_assertion_error=Error de aserci\u00F3n\:
 view_results_assertion_failure=Fallo de aserci\u00F3n\:
 view_results_assertion_failure_message=Mensaje de fallo de aserci\u00F3n\:
 view_results_desc=Muestra los resultados de texto del muestreo en forma de \u00E1rbol
 view_results_error_count=Conteo de error\:
 view_results_fields=campos\:
 view_results_in_table=Ver Resultados en \u00C1rbol
 view_results_latency=Latencia\:
 view_results_load_time=Tiempo de carga\:
 view_results_render=Renderizador\:
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML(descargar elementos embebidos)
 view_results_render_json=JSON
 view_results_render_text=Texto
 view_results_render_xml=XML
 view_results_request_headers=Cabeceras de petici\u00F3n\:
 view_results_response_code=C\u00F3digo de respuesta\:
 view_results_response_headers=Cabeceras de respuesta\:
 view_results_response_message=Mensaje de respuesta\:
 view_results_response_too_large_message=Respuesta muy larga a ser mostrada. Tama\u00F1o\:
+view_results_response_partial_message=Principio del mensaje:
 view_results_sample_count=Conteo de muestra\:
 view_results_sample_start=Comienzo de muestra\:
 view_results_search_pane=Panel de b\u00FAsqueda
 view_results_size_in_bytes=Tama\u00F1o en bytes\:
 view_results_tab_assertion=Resultado de la aserci\u00F3n
 view_results_tab_request=Petici\u00F3n
 view_results_tab_response=Datos de Respuesta
 view_results_tab_sampler=Resultado del Muestreador
 view_results_table_fields_key=Campo adicional
 view_results_table_fields_value=Valor
 view_results_table_headers_key=Cabecera de respuesta
 view_results_table_headers_value=Valor
 view_results_table_request_headers_key=Cabecera de petici\u00F3n
 view_results_table_request_headers_value=Valor
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=M\u00E1quina
 view_results_table_request_http_method=M\u00E9todo
 view_results_table_request_http_nohttp=No muestra HTTP
 view_results_table_request_http_path=Ruta
 view_results_table_request_http_port=Puerto
 view_results_table_request_http_protocol=Protocolo
 view_results_table_request_params_key=Nombre de par\u00E1metro
 view_results_table_request_params_value=Valor
 view_results_table_request_raw_nodata=No mostrar datos
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=En bruto
 view_results_table_result_tab_parsed=Parseado
 view_results_table_result_tab_raw=En bruto
 view_results_thread_name=Nombre del hilo\:
 view_results_title=Ver Resultados
 view_results_tree_title=Ver \u00C1rbol de Resultados
 warning=\u00A1Atenci\u00F3n\!
 web_proxy_server_title=Servidor Proxy
 web_request=Petici\u00F3n HTTP
 web_server=Servidor Web
 web_server_client=Implementaci\u00F3n del Cliente\:
 web_server_domain=Nombre de Servidor o IP\:
 web_server_port=Puerto\:
 web_server_timeout_connect=Conexi\u00F3n\:
 web_server_timeout_response=Respuesta\:
 web_server_timeout_title=Timeout (milisegundos)
 web_testing2_source_ip=Direcci\u00F3n IP fuente\:
 web_testing2_title=Petici\u00F3n HTTP HttpClient
 web_testing_embedded_url_pattern=Las URLs embebidas deben coincidir a\:
 web_testing_retrieve_images=Recuperar Todos los Recursos Empotrados de Archivos HTML
 web_testing_title=Petici\u00F3n HTTP
 webservice_proxy_host=Host Proxy
 webservice_proxy_note=Si est\u00E1 seleccionado "Utilizar Proxy HTTP", pero no se proporciona host o puerto, el muestreador
 webservice_proxy_note2=buscar\u00E1 opciones en la l\u00EDnea de comandos. Si no se proporcionan host o puerto
 webservice_proxy_note3=all\u00ED, finalmente fallar\u00E1 silenciosamente.
 webservice_proxy_port=Puerto Proxy
 webservice_sampler_title=Petici\u00F3n WebService(SOAP)
 webservice_soap_action=Acci\u00F3n SOAP
 webservice_timeout=Timeout\:
 webservice_use_proxy=Utilizar Proxy HTTP
 while_controller_label=Condici\u00F3n (funci\u00F3n o variable)
 while_controller_title=Controlador While
 workbench_title=Banco de Trabajo
 wsdl_helper_error=El WSDL no es v\u00E1lido, por favor compruebe la url.
 wsdl_url=URL del WSDL
 wsdl_url_error=El WSDL est\u00E1 vacio.
 xml_assertion_title=Aserci\u00F3n XML
 xml_download_dtds=Recuperar DTDs externos
 xml_namespace_button=Utilizar NameSpaces
 xml_tolerant_button=Parser XML/HTML Tolerante
 xml_validate_button=Validar XML
 xml_whitespace_button=Ignorar Espacios
 xmlschema_assertion_label=Nombre de Archivo\:
 xmlschema_assertion_title=Aserci\u00F3n de Esquema XML
 xpath_assertion_button=Validar
 xpath_assertion_check=Comprobar Expresi\u00F3n XPath
 xpath_assertion_error=Error en XPath
 xpath_assertion_failed=Expresi\u00F3n XPath Inv\u00E1lida
 xpath_assertion_label=XPath
 xpath_assertion_negate=True si nada coincide
 xpath_assertion_option=Opciones para parsear XML
 xpath_assertion_test=Aserci\u00F3n XPath
 xpath_assertion_tidy=Prueba y ordena la entrada
 xpath_assertion_title=Aserci\u00F3n XPath
 xpath_assertion_valid=Expresi\u00F3n XPath V\u00E1lida
 xpath_assertion_validation=Validar el XML contra el DTD
 xpath_assertion_whitespace=Ignorar espacios
 xpath_expression=Expresi\u00F3n XPath contra la que comparar
 xpath_extractor_fragment=\u00BFRetornar el fragmento XPATH en el caso de contenido de texto?
 xpath_extractor_query=Consulta XPath\:
 xpath_extractor_title=Extractor XPath
 xpath_file_file_name=Archivo XML del que obtener valores
 xpath_tidy_quiet=Silencioso
 xpath_tidy_report_errors=Reportar los errores
 xpath_tidy_show_warnings=Mostrar advertencias
 you_must_enter_a_valid_number=Debe introducir un n\u00FAmero v\u00E1lido
 zh_cn=Chino (Simplificado)
 zh_tw=Chino (Tradicional)
diff --git a/src/core/org/apache/jmeter/resources/messages_fr.properties b/src/core/org/apache/jmeter/resources/messages_fr.properties
index 21c5d9856..e968088c4 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,1002 +1,1004 @@
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
 add=Ajouter
 add_as_child=Ajouter en tant qu'enfant
 add_parameter=Ajouter un param\u00E8tre
 add_pattern=Ajouter un motif \:
 add_test=Ajout
 add_user=Ajouter un utilisateur
 add_value=Ajouter valeur
 addtest=Ajout
 aggregate_graph=Graphique des statistiques
 aggregate_graph_column=Colonne
 aggregate_graph_display=G\u00E9n\u00E9rer le graphique
 aggregate_graph_height=Hauteur \:
 aggregate_graph_max_length_xaxis_label=Longueur maximum du libell\u00E9 de l'axe des abscisses \:
 aggregate_graph_ms=Millisecondes
 aggregate_graph_response_time=Temps de r\u00E9ponse
 aggregate_graph_save=Enregistrer le graphique
 aggregate_graph_save_table=Enregistrer le tableau de donn\u00E9es
 aggregate_graph_save_table_header=Inclure l'ent\u00EAte du tableau
 aggregate_graph_title=Graphique agr\u00E9g\u00E9
 aggregate_graph_use_group_name=Ajouter le nom du groupe aux libell\u00E9s
 aggregate_graph_user_title=Titre du graphique \:
 aggregate_graph_width=Largeur \:
 aggregate_report=Rapport agr\u00E9g\u00E9
 aggregate_report_90%_line=90e centile
 aggregate_report_bandwidth=Ko/sec
 aggregate_report_count=\# Echantillons
 aggregate_report_error=Erreur
 aggregate_report_error%=% Erreur
 aggregate_report_median=M\u00E9diane
 aggregate_report_rate=D\u00E9bit
 aggregate_report_stddev=Ecart type
 ajp_sampler_title=Requ\u00EAte AJP/1.3
 als_message=Note \: Le parseur de log d'acc\u00E8s est g\u00E9n\u00E9rique et vous permet de se brancher \u00E0 
 als_message2=votre propre parseur. Pour se faire, impl\u00E9menter le LogParser, ajouter le jar au 
 als_message3=r\u00E9pertoire /lib et entrer la classe (fichier .class) dans l'\u00E9chantillon (sampler).
 analyze=En train d'analyser le fichier de donn\u00E9es
 anchor_modifier_title=Analyseur de lien HTML
 appearance=Apparence
 argument_must_not_be_negative=L'argument ne peut pas \u00EAtre n\u00E9gatif \!
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
 assertion_resp_field=Section de r\u00E9ponse \u00E0 tester
 assertion_resp_size_field=Taille \u00E0 v\u00E9rifier sur
 assertion_substring=Contient (texte brut)
 assertion_text_resp=Texte de r\u00E9ponse
 assertion_textarea_label=Assertions \:
 assertion_title=Assertion R\u00E9ponse
 assertion_url_samp=URL Echantillon
 assertion_visualizer_title=R\u00E9cepteur d'assertions
 attribute=Attribut \:
 attrs=Attributs
 auth_base_url=URL de base
 auth_manager_title=Gestionnaire d'autorisation HTTP
 auths_stored=Autorisations stock\u00E9es
 average=Moyenne
 average_bytes=Moy. octets
 bind=Connexion de l'unit\u00E9
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
 bsh_script=Script (voir la suite pour les variables qui sont d\u00E9finies)
 bsh_script_file=Fichier script \:
 bsh_script_parameters=Param\u00E8tres  (-> String Parameters et String []bsh.args)
 bsh_script_reset_interpreter=R\u00E9initialiser l'interpr\u00E9teur bsh avant chaque appel
 bsh_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Je suis occup\u00E9 \u00E0 tester, veuillez arr\u00EAter le test avant de changer le param\u00E8trage
 cache_manager_title=Gestionnaire de cache HTTP
 cache_session_id=Identifiant de session de cache ?
 cancel=Annuler
 cancel_exit_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de sortir ?
 cancel_new_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de nettoyer le plan de test ?
 cancel_revert_project=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Annuler les changements et revenir \u00E0 la derni\u00E8re sauvegarde du plan de test ?
 choose_function=Choisir une fonction
 choose_language=Choisir une langue
 clear=Nettoyer
 clear_all=Nettoyer tout
 clear_cache_per_iter=Nettoyer le cache \u00E0 chaque it\u00E9ration ?
 clear_cookies_per_iter=Nettoyer les cookies \u00E0 chaque it\u00E9ration ?
 column_delete_disallowed=Supprimer cette colonne n'est pas possible
 column_number=Num\u00E9ro de colonne du fichier CSV | next | *alias
 compare=Comparaison
 comparefilt=Filtre de comparaison
 comparison_differ_content=Le contenu des r\u00E9ponses est diff\u00E9rent.
 comparison_differ_time=La diff\u00E9rence du temps de r\u00E9ponse diff\u00E8re de plus de 
 comparison_invalid_node=Noeud invalide 
 comparison_regex_string=Expression r\u00E9guli\u00E8re
 comparison_response_time=Temps de r\u00E9ponse \: 
 comparison_visualizer_title=R\u00E9cepteur d'assertions de comparaison
 config_element=El\u00E9ment de configuration
 config_save_settings=Configurer
 configure_wsdl=Configurer
 constant_throughput_timer_memo=Ajouter un d\u00E9lai entre les \u00E9chantillions pour obtenir un d\u00E9bit constant
 constant_timer_delay=D\u00E9lai d'attente (en millisecondes) \:
 constant_timer_memo=Ajouter un d\u00E9lai fixe entre les \u00E9chantillions de test
 constant_timer_title=Compteur de temps fixe
 content_encoding=Encodage contenu \:
 controller=Contr\u00F4leur
 cookie_manager_policy=Politique des cookies
 cookie_manager_title=Gestionnaire de cookies HTTP
 cookies_stored=Cookies stock\u00E9s
 copy=Copier
 corba_port=Num\u00E9ro de port \:
 counter_config_title=Compteur
 counter_per_user=Suivre le compteur ind\u00E9pendamment pour chaque unit\u00E9 de test
 countlim=Limiter le nombre d'\u00E9l\u00E9ments retourn\u00E9s \u00E0
 csvread_file_file_name=Fichier CSV pour obtenir les valeurs de | *alias
 cut=Couper
 cut_paste_function=Fonction de copier/coller de cha\u00EEne de caract\u00E8re
 database_sql_query_string=Requ\u00EAte SQL \:
 database_sql_query_title=Requ\u00EAte SQL JDBC par d\u00E9faut
 ddn=DN \:
 de=Allemand
 debug_off=D\u00E9sactiver le d\u00E9bogage
 debug_on=Activer le d\u00E9bogage
 default_parameters=Param\u00E8tres par d\u00E9faut
 default_value_field=Valeur par d\u00E9faut \:
 delay=D\u00E9lai avant d\u00E9marrage (secondes) \:
 delete=Supprimer
 delete_parameter=Supprimer le param\u00E8tre
 delete_test=Suppression
 delete_user=Supprimer l'utilisateur
 deltest=Suppression
 deref=D\u00E9r\u00E9f\u00E9rencement des alias
 disable=D\u00E9sactiver
 distribution_graph_title=Graphique de distribution (alpha)
 distribution_note1=Ce graphique se mettra \u00E0 jour tous les 10 \u00E9chantillons
 dn=Racine DN \:
 domain=Domaine \:
 done=Fait
 down=Descendre
 duration=Dur\u00E9e (secondes) \:
 duration_assertion_duration_test=Dur\u00E9e maximale \u00E0 v\u00E9rifier
 duration_assertion_failure=L''op\u00E9ration a dur\u00E9e trop longtemps\: cela a pris {0} millisecondes, mais n''aurait pas d\u00FB durer plus de {1} millisecondes.
 duration_assertion_input_error=Veuillez entrer un entier positif valide.
 duration_assertion_label=Dur\u00E9e en millisecondes \:
 duration_assertion_title=Assertion Dur\u00E9e
 edit=Editer
 email_results_title=R\u00E9sultat d'email
 en=Anglais
 enable=Activer
 encode?=Encodage
 encoded_value=Valeur de l'URL encod\u00E9e
 endtime=Date et heure de fin \:
 entry_dn=Entr\u00E9e DN \:
 entrydn=Entr\u00E9e DN
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
 file_visualizer_output_file=Ecrire les donn\u00E9es dans un fichier
 file_visualizer_submit_data=Inclure les donn\u00E9es envoy\u00E9es
 file_visualizer_title=Rapporteur de fichier
 file_visualizer_verbose=Sortie verbeuse
 filename=Nom de fichier \: 
 follow_redirects=Suivre les redirect.
 follow_redirects_auto=Rediriger automat.
 foreach_controller_title=Contr\u00F4leur Pour chaque (ForEach)
 foreach_input=Pr\u00E9fixe de la variable d'entr\u00E9e \:
 foreach_output=Nom de la variable de sortie \:
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
 gaussian_timer_range=D\u00E9calage (en millisecondes) \:
 gaussian_timer_title=Compteur de temps al\u00E9atoire gaussien
 generate=G\u00E9n\u00E9rer
 generator=Nom de la classe g\u00E9n\u00E9ratrice
 generator_cnf_msg=N'a pas p\u00FB trouver la classe g\u00E9n\u00E9ratrice. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 generator_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classes g\u00E9n\u00E9ratrice \u00E0 cause d'une IllegalAccessException.
 generator_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur g\u00E9n\u00E9rateur. Assurez-vous que le g\u00E9n\u00E9rateur impl\u00E9mente l'interface Generator.
 get_xml_from_file=Fichier avec les donn\u00E9es XML SOAP (remplace le texte ci-dessus)
 get_xml_from_random=R\u00E9pertoire contenant les fichier(s) \:
 graph_choose_graphs=Graphique \u00E0 afficher
 graph_full_results_title=Graphique de r\u00E9sultats complets
 graph_results_average=Moyenne
 graph_results_data=Donn\u00E9es
 graph_results_deviation=Ecart type
 graph_results_latest_sample=Dernier \u00E9chantillon
 graph_results_median=M\u00E9diane
 graph_results_no_samples=Nombre d'\u00E9chantillons
 graph_results_throughput=D\u00E9bit
 graph_results_title=Graphique de r\u00E9sultats
 grouping_add_separators=Ajouter des s\u00E9parateurs entre les groupes
 grouping_in_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur
 grouping_in_transaction_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur de transaction
 grouping_mode=Grouper \:
 grouping_no_groups=Ne pas grouper les \u00E9chantillons
 grouping_store_first_only=Stocker le 1er \u00E9chantillon pour chaque groupe uniquement
 header_manager_title=Gestionnaire d'ent\u00EAtes HTTP
 headers_stored=Ent\u00EAtes stock\u00E9es
 help=Aide
 help_node=Quel est ce noeud ?
 html_assertion_file=Ecrire un rapport JTidy dans un fichier
 html_assertion_label=Assertion HTML
 html_assertion_title=Assertion HTML
 html_parameter_mask=Masque de param\u00E8tre HTML
 http_implementation=Impl\u00E9mentation \:
 http_response_code=Code de r\u00E9ponse HTTP
 http_url_rewriting_modifier_title=Transcripteur d'URL HTTP
 http_user_parameter_modifier=Modificateur de param\u00E8tre utilisateur HTTP
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
 jms_client_caption=Le client r\u00E9cepteur utilise MessageConsumer.receive () pour \u00E9couter les messages.
 jms_client_caption2=MessageListener utilise l'interface onMessage(Message) pour \u00E9couter les nouveaux messages.
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
 jms_error_msg=L'objet du message peut \u00EAtre lu depuis un fichier externe. L'entr\u00E9e par texte est actuellement s\u00E9lectionn\u00E9e, ne pas oublier de la changer
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
 jms_props=Propri\u00E9t\u00E9s JMS
 jms_provider_url=URL du fournisseur
 jms_publisher=Requ\u00EAte JMS Publication
 jms_pwd=Mot de passe
 jms_queue=File
 jms_queue_connection_factory=Fabrique QueueConnection
 jms_queueing=Ressources JMS
 jms_random_file=Fichier al\u00E9atoire
 jms_read_response=Lire la r\u00E9ponse
 jms_receive_queue=Nom JNDI de la file d'attente Receive 
 jms_request=Requ\u00EAte seule
 jms_requestreply=Requ\u00EAte R\u00E9ponse
 jms_sample_title=Requ\u00EAte JMS par d\u00E9faut
 jms_send_queue=Nom JNDI de la file d'attente Request
 jms_stop_between_samples=Arr\u00EAter entre les \u00E9chantillons ?
 jms_subscriber_on_message=Utiliser MessageListener.onMessage()
 jms_subscriber_receive=Utiliser MessageConsumer.receive()
 jms_subscriber_title=Requ\u00EAte JMS Abonnement
 jms_testing_title=Messagerie Request
 jms_text_message=Message texte
 jms_timeout=D\u00E9lai (millisecondes)
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
 load_wsdl=Charger WSDL
 log_errors_only=Erreurs
 log_file=Emplacement du fichier de journal (log)
 log_function_comment=Commentaire (facultatif)
 log_function_level=Niveau de journalisation (INFO par d\u00E9faut), OUT ou ERR
 log_function_string=Cha\u00EEne \u00E0 tracer
 log_function_string_ret=Cha\u00EEne \u00E0 tracer (et \u00E0 retourner)
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
 loop_controller_title=Contr\u00F4leur Boucle
 looping_control=Contr\u00F4le de boucle
 lower_bound=Borne Inf\u00E9rieure
 mail_reader_account=Nom utilisateur \:
 mail_reader_all_messages=Tous
 mail_reader_delete=Supprimer les messages du serveur
 mail_reader_folder=Dossier \:
 mail_reader_num_messages=Nombre de message \u00E0 r\u00E9cup\u00E9rer \:
 mail_reader_password=Mot de passe \:
 mail_reader_port=Port (optionnel) \:
 mail_reader_server=Serveur \:
 mail_reader_server_type=Protocole (ex. pop3, imaps) \:
 mail_reader_storemime=Stocker le message en utilisant MIME (brut)
 mail_reader_title=Echantillon Lecteur d'email
 mail_sent=Email envoy\u00E9 avec succ\u00E8s
 mailer_attributes_panel=Attributs de courrier
 mailer_error=N'a pas p\u00FB envoyer l'email. Veuillez corriger les erreurs de saisie.
 mailer_visualizer_title=Visualiseur de courrier
 match_num_field=R\u00E9cup\u00E9rer la Ni\u00E8me corresp. (0 \: Al\u00E9atoire) \:
 max=Maximum \:
 maximum_param=La valeur maximum autoris\u00E9e pour un \u00E9cart de valeurs
 md5hex_assertion_failure=Erreur de v\u00E9rification de la somme MD5 \: obtenu {0} mais aurait d\u00FB \u00EAtre {1}
 md5hex_assertion_md5hex_test=MD5Hex \u00E0 v\u00E9rifier
 md5hex_assertion_title=Assertion MD5Hex
 memory_cache=Cache de m\u00E9moire
 menu_close=Fermer
 menu_collapse_all=R\u00E9duire tout
 menu_config_element=Configurations
 menu_edit=Editer
 menu_expand_all=Etendre tout
 menu_fragments=Fragment d'\u00E9l\u00E9ments
 menu_generative_controller=Echantillons
 menu_listener=R\u00E9cepteurs
 menu_logic_controller=Contr\u00F4leurs Logiques
 menu_merge=Fusionner...
 menu_modifiers=Modificateurs
 menu_non_test_elements=El\u00E9ments hors test
 menu_open=Ouvrir...
 menu_post_processors=Post-Processeurs
 menu_pre_processors=Pr\u00E9-Processeurs
 menu_response_based_modifiers=Modificateurs bas\u00E9s sur la r\u00E9ponse
 menu_search_reset=Effacer la recherche
 menu_search=Rechercher
 menu_threads=Moteurs d'utilisateurs
 menu_timer=Compteurs de temps
 metadata=M\u00E9ta-donn\u00E9es
 method=M\u00E9thode \:
 mimetype=Type MIME
 minimum_param=La valeur minimale autoris\u00E9e pour l'\u00E9cart de valeurs
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
 monitor_health_title=Moniteur de connecteurs
 monitor_is_title=Utiliser comme moniteur
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
 monitor_performance_title=Graphique de performance
 name=Nom \:
 new=Nouveau
 newdn=Nouveau DN
 no=Norv\u00E9gien
 number_of_threads=Nombre d'unit\u00E9s (utilisateurs) \:
 obsolete_test_element=Cet \u00E9l\u00E9ment de test est obsol\u00E8te
 once_only_controller_title=Contr\u00F4leur Ex\u00E9cution unique
 opcode=Code d'op\u00E9ration
 open=Ouvrir...
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
 pl=Polonais
 port=Port \:
 post_thread_group_title=Groupe d'unit\u00E9s de fin
 property_default_param=Valeur par d\u00E9faut
 property_edit=Editer
 property_editor.value_is_invalid_message=Le texte que vous venez d'entrer n'a pas une valeur valide pour cette propri\u00E9t\u00E9.\nLa propri\u00E9t\u00E9 va revenir \u00E0 sa valeur pr\u00E9c\u00E9dente.
 property_editor.value_is_invalid_title=Texte saisi invalide
 property_name_param=Nom de la propri\u00E9t\u00E9
 property_returnvalue_param=Revenir \u00E0 la valeur originale de la propri\u00E9t\u00E9 (d\u00E9faut non) ?
 property_undefined=Non d\u00E9fini
 property_value_param=Valeur de propri\u00E9t\u00E9
 property_visualiser_title=Afficheur de propri\u00E9t\u00E9s
 protocol=Protocole [http] \:
 protocol_java_border=Classe Java
 protocol_java_classname=Nom de classe \:
 protocol_java_config_tile=Configurer \u00E9chantillon Java
 protocol_java_test_title=Test Java
 proxy_assertions=Ajouter une Assertion R\u00E9ponse
 proxy_cl_error=Si un serveur proxy est sp\u00E9cifi\u00E9, h\u00F4te et port doivent \u00EAtre donn\u00E9
 proxy_content_type_exclude=Exclure \:
 proxy_content_type_filter=Filtre de type de contenu
 proxy_content_type_include=Inclure \:
 proxy_daemon_bind_error=Impossible de lancer le serveur proxy, le port est d\u00E9j\u00E0 utilis\u00E9. Choisissez un autre port.
 proxy_daemon_error=Impossible de lancer le serveur proxy, voir le journal pour plus de d\u00E9tails
 proxy_headers=Capturer les ent\u00EAtes HTTP
 proxy_httpsspoofing=Tenter d'usurper le HTTPS
 proxy_httpsspoofing_match=Filtre d'URL pour usurpation HTTPS (regexp) \:
 proxy_regex=Correspondance des variables par regex ?
 proxy_sampler_settings=Param\u00E8tres Echantillon HTTP
 proxy_sampler_type=Type \:
 proxy_separators=Ajouter des s\u00E9parateurs
 proxy_target=Contr\u00F4leur Cible \:
 proxy_test_plan_content=Param\u00E8tres du plan de test
 proxy_title=Serveur Proxy HTTP
 pt_br=Portugais (Br\u00E9sil)
 ramp_up=Dur\u00E9e de mont\u00E9e en charge (en secondes) \:
 random_control_title=Contr\u00F4leur Al\u00E9atoire
 random_order_control_title=Contr\u00F4leur d'Ordre al\u00E9atoire
 random_string_chars_to_use=Caract\u00E8res \u00E0 utiliser pour la g\u00E9n\u00E9ration de la cha\u00EEne al\u00E9atoire
 random_string_length=Longueur de cha\u00EEne al\u00E9atoire
 read_response_message='Lire la r\u00E9ponse SOAP' n'est pas coch\u00E9. Pour voir la r\u00E9ponse, cocher la case dans la requ\u00EAte WebService svp.
 read_response_note=Si 'Lire la r\u00E9ponse SOAP' n'est pas coch\u00E9, la requ\u00EAte WebService ne lira pas la r\u00E9ponse.
 read_response_note2=et ne remplira pas l'objet SampleResult. Cela am\u00E9liore les performances, mais signifie que 
 read_response_note3=le contenu de la r\u00E9ponse ne sera pas tra\u00E7\u00E9.
 read_soap_response=Lire la r\u00E9ponse SOAP
 realm=Univers (realm)
 record_controller_title=Contr\u00F4leur Enregistreur
 ref_name_field=Nom de r\u00E9f\u00E9rence \:
 regex_extractor_title=Extracteur Expression r\u00E9guli\u00E8re
 regex_field=Expression r\u00E9guli\u00E8re \:
 regex_source=Port\u00E9e
 regex_src_body=Corps
 regex_src_body_unescaped=Corps (non \u00E9chapp\u00E9)
 regex_src_hdrs=Ent\u00EAtes
 regexfunc_param_1=Expression r\u00E9guli\u00E8re utilis\u00E9e pour chercher les r\u00E9sultats de la requ\u00EAte pr\u00E9c\u00E9dente.
 regexfunc_param_2=Canevas pour la ch\u00EEne de caract\u00E8re de remplacement, utilisant des groupes d'expressions r\u00E9guli\u00E8res. Le format est  $[group]$.  Exemple $1$.
 regexfunc_param_3=Quelle correspondance utiliser. Un entier 1 ou plus grand, RAND pour indiquer que JMeter doit choisir al\u00E9atoirement , A d\u00E9cimal, ou ALL indique que toutes les correspondances doivent \u00EAtre utilis\u00E9es
 regexfunc_param_4=Entre le texte. Si ALL est s\u00E9lectionn\u00E9, l'entre-texte sera utilis\u00E9 pour g\u00E9n\u00E9rer les r\u00E9sultats ([""])
 regexfunc_param_5=Text par d\u00E9faut. Utilis\u00E9 \u00E0 la place du canevas si l'expression r\u00E9guli\u00E8re ne trouve pas de correspondance
 regexp_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 regexp_tester_button_test=Tester
 regexp_tester_field=Expression r\u00E9guli\u00E8re \:
 regexp_tester_title=Testeur de RegExp
 remote_exit=Sortie distante
 remote_exit_all=Sortie distante de tous
 remote_shut=Extinction \u00E0 distance
 remote_shut_all=Extinction \u00E0 distance de tous
 remote_start=D\u00E9marrage distant
 remote_start_all=D\u00E9marrage distant de tous
 remote_stop=Arr\u00EAt distant
 remote_stop_all=Arr\u00EAt distant de tous
 remove=Supprimer
 rename=Renommer une entr\u00E9e
 report=Rapport
 request_data=Donn\u00E9e requ\u00EAte
 reset_gui=R\u00E9initialiser l'\u00E9l\u00E9ment
 response_save_as_md5=R\u00E9ponse en empreinte MD5
 restart=Red\u00E9marrer
 resultaction_title=Op\u00E9rateur R\u00E9sultats Action
 resultsaver_errors=Enregistrer seulement les r\u00E9ponses en \u00E9checs
 resultsaver_prefix=Pr\u00E9fixe du nom de fichier \: 
 resultsaver_skipautonumber=Ne pas ajouter de nombre au pr\u00E9fixe
 resultsaver_skipsuffix=Ne pas ajouter de suffixe
 resultsaver_success=Enregistrer seulement les r\u00E9ponses en succ\u00E8s
 resultsaver_title=Sauvegarder les r\u00E9ponses vers un fichier
 resultsaver_variable=Nom de variable \:
 retobj=Retourner les objets
 reuseconnection=R\u00E9-utiliser la connexion
 revert_project=Annuler les changements
 revert_project?=Annuler les changements sur le projet ?
 root=Racine
 root_title=Racine
 run=Lancer
 running_test=Lancer test
 runtime_controller_title=Contr\u00F4leur Dur\u00E9e d'ex\u00E9cution
 runtime_seconds=Temps d'ex\u00E9cution (secondes) \:
 sample_scope=Appliquer sur
 sample_scope_all=L'\u00E9chantillon et ses ressources li\u00E9es
 sample_scope_children=Les ressources li\u00E9es
 sample_scope_parent=L'\u00E9chantillon
 sample_scope_variable=Une variable \:
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
 save_assertionresultsfailuremessage=Messages d'erreur des assertions
 save_assertions=R\u00E9sultats des assertions (XML)
 save_asxml=Enregistrer au format XML
 save_bytes=Nombre d'octets
 save_code=Code de r\u00E9ponse HTTP
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
 save_subresults=Sous r\u00E9sultats (XML)
 save_success=Succ\u00E8s
 save_threadcounts=Nombre d'unit\u00E9s actives
 save_threadname=Nom d'unit\u00E9
 save_time=Temps \u00E9coul\u00E9
 save_timestamp=Horodatage
 save_url=URL
 sbind=Simple connexion/d\u00E9connexion
 scheduler=Programmateur de d\u00E9marrage
 scheduler_configuration=Configuration du programmateur
 scope=Port\u00E9e
 search_base=Base de recherche
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
 search_word=Rechercher le mot
 searchbase=Base de recherche
 searchfilter=Filtre de recherche
 searchtest=Recherche
 second=seconde
 secure=S\u00E9curis\u00E9 \:
 send_file=Envoyer un fichier avec la requ\u00EAte \:
 send_file_browse=Parcourir...
 send_file_filename_label=Chemin du fichier \: 
 send_file_mime_label=Type MIME \:
 send_file_param_name_label=Nom du param\u00E8tre \:
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
 smtp_server_port=Port \:
 smtp_server_settings=Param\u00E8tres du serveur
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
 soap_sampler_title=Requ\u00EAte SOAP/XML-RPC
 soap_send_action=Envoyer l'action SOAP \:
 spline_visualizer_average=Moyenne \:
 spline_visualizer_incoming=Entr\u00E9e \:
 spline_visualizer_maximum=Maximum \:
 spline_visualizer_minimum=Minimum \:
 spline_visualizer_title=Moniteur de courbe (spline)
 spline_visualizer_waitingmessage=En attente de r\u00E9sultats d'\u00E9chantillons
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
+start_no_timers=Lancer sans pauses
 starttime=Date et heure de d\u00E9marrage \:
 stop=Arr\u00EAter
 stopping_test=En train d'\u00E9teindre toutes les unit\u00E9s de tests. Soyez patient, merci.
 stopping_test_failed=Au moins une unit\u00E9 non arr\u00EAt\u00E9e; voir le journal.
 stopping_test_title=En train d'arr\u00EAter le test
 string_from_file_encoding=Encodage du fichier (optionnel)
 string_from_file_file_name=Entrer le chemin (absolu ou relatif) du fichier
 string_from_file_seq_final=Nombre final de s\u00E9quence de fichier
 string_from_file_seq_start=D\u00E9marer le nombre de s\u00E9quence de fichier
 summariser_title=G\u00E9n\u00E9rer les resultats consolid\u00E9s
 summary_report=Rapport consolid\u00E9
 switch_controller_label=Aller vers le num\u00E9ro d'\u00E9l\u00E9ment (ou nom) subordonn\u00E9 \:
 switch_controller_title=Contr\u00F4leur Aller \u00E0
 table_visualizer_bytes=Octets
 table_visualizer_sample_num=Echantillon \#
 table_visualizer_sample_time=Temps (ms)
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
 template_field=Canevas \:
 test_action_action=Action \:
 test_action_duration=Dur\u00E9e (millisecondes) \:
 test_action_pause=Mettre en pause
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
 testplan.serialized=Lancer les groupes d'unit\u00E9s en s\u00E9rie (c'est-\u00E0-dire \: lance un groupe \u00E0 la fois)
 testplan_comments=Commentaires \:
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
 toggle=Permuter
 tr=Turc
 transaction_controller_include_timers=Inclure la dur\u00E9e des compteurs de temps dans le calcul du temps
 transaction_controller_parent=G\u00E9n\u00E9rer en \u00E9chantillon parent
 transaction_controller_title=Contr\u00F4leur Transaction
 unbind=D\u00E9connexion de l'unit\u00E9
 uniform_timer_delay=D\u00E9lai de d\u00E9calage constant (en millisecondes) \:
 uniform_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution uniforme
 uniform_timer_range=D\u00E9lai al\u00E9atoire maximum (en millisecondes) \:
 uniform_timer_title=Compteur de temps al\u00E9atoire uniforme
 up=Monter
 update_per_iter=Mettre \u00E0 jour une fois par it\u00E9ration
 upload=Fichier \u00E0 uploader
 upper_bound=Borne sup\u00E9rieure
 url_config_protocol=Protocole \:
 url_config_title=Param\u00E8tres HTTP par d\u00E9faut
 url_full_config_title=Echantillon d'URL complet
 url_multipart_config_title=Requ\u00EAte HTTP Multipart par d\u00E9faut
 use_expires=Utiliser les ent\u00EAtes Cache-Control/Expires lors du traitement des requ\u00EAtes GET
 use_keepalive=Connexion persist.
 use_multipart_for_http_post=Multipart/form-data
 use_multipart_mode_browser=Ent\u00EAtes compat. navigateur
 use_recording_controller=Utiliser un contr\u00F4leur enregistreur
 user=Utilisateur
 user_defined_test=Test d\u00E9fini par l'utilisateur
 user_defined_variables=Variables pr\u00E9-d\u00E9finies
 user_param_mod_help_note=(Ne pas changer. A la place, modifier le fichier de ce nom dans le r\u00E9pertoire /bin de JMeter)
 user_parameters_table=Param\u00E8tres
 user_parameters_title=Param\u00E8tres Utilisateur
 userdn=Identifiant
 username=Nom d'utilisateur \:
 userpw=Mot de passe
 value=Valeur \:
 var_name=Nom de r\u00E9f\u00E9rence \:
 variable_name_param=Nom de variable (peut inclure une r\u00E9f\u00E9rence de variable ou fonction)
 view_graph_tree_title=Voir le graphique en arbre
 view_results_assertion_error=Erreur d'assertion \: 
 view_results_assertion_failure=Echec d'assertion \: 
 view_results_assertion_failure_message=Message d'\u00E9chec d'assertion \: 
 view_results_autoscroll=D\u00E9filement automatique ?
 view_results_desc=Affiche les r\u00E9sultats d'un \u00E9chantillon dans un arbre de r\u00E9sultats
 view_results_error_count=Compteur erreur\: 
 view_results_fields=champs \:
 view_results_in_table=Tableau de r\u00E9sultats
 view_results_latency=Latence \: 
 view_results_load_time=Temps de r\u00E9ponse \: 
 view_results_render=Rendu \: 
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML et ressources
 view_results_render_json=JSON
 view_results_render_text=Texte brut
 view_results_render_xml=XML
 view_results_request_headers=Ent\u00EAtes de requ\u00EAte \:
 view_results_response_code=Code de retour \: 
 view_results_response_headers=Ent\u00EAtes de r\u00E9ponse \:
 view_results_response_message=Message de retour \: 
 view_results_response_too_large_message=R\u00E9ponse d\u00E9passant la taille maximale d'affichage. Taille \: 
+view_results_response_partial_message=D\u00E9but du message:
 view_results_sample_count=Compteur \u00E9chantillon \: 
 view_results_sample_start=Date d\u00E9but \u00E9chantillon \: 
 view_results_search_pane=Volet recherche 
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
 view_results_table_request_http_path=Cheming
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
 web_proxy_server_title=Requ\u00EAte via un serveur proxy
 web_request=Requ\u00EAte HTTP
 web_server=Serveur web
 web_server_client=Impl\u00E9mentation client \:
 web_server_domain=Nom ou adresse IP \:
 web_server_port=Port \:
 web_server_timeout_connect=Connexion \:
 web_server_timeout_response=R\u00E9ponse \:
 web_server_timeout_title=D\u00E9lai expiration (ms)
 web_testing2_source_ip=Adresse IP source \:
 web_testing2_title=Requ\u00EAte HTTP HTTPClient
 web_testing_concurrent_download=Utiliser pool unit\u00E9. Nbre \:
 web_testing_embedded_url_pattern=Les URL \u00E0 inclure doivent correspondre \u00E0 \:
 web_testing_retrieve_images=R\u00E9cup\u00E9rer les ressources incluses
 web_testing_title=Requ\u00EAte HTTP
 webservice_configuration_wizard=Assistant de configuration WSDL
 webservice_get_xml_from_random_title=Utiliser al\u00E9atoirement des messages SOAP
 webservice_message_soap=Message WebService
 webservice_methods=M\u00E9thode(s) WebService \:
 webservice_proxy_host=H\u00F4te proxy
 webservice_proxy_note=Si 'utiliser un proxy HTTP' est coch\u00E9e, mais qu'aucun h\u00F4te ou port est fournit, l'\u00E9chantillon
 webservice_proxy_note2=regardera les options de ligne de commandes. Si aucun h\u00F4te ou port du proxy sont fournit 
 webservice_proxy_note3=non plus, il \u00E9chouera silencieusement.
 webservice_proxy_port=Port proxy
 webservice_sampler_title=Requ\u00EAte WebService (SOAP)
 webservice_soap_action=Action SOAP \:
 webservice_timeout=D\u00E9lai expiration \:
 webservice_use_proxy=Utiliser un proxy HTTP
 while_controller_label=Condition (fonction ou variable) \:
 while_controller_title=Contr\u00F4leur Tant Que
 workbench_title=Plan de travail
 wsdl_helper_error=Le WSDL n'est pas valide, veuillez rev\u00E9rifier l'URL.
 wsdl_url_error=Le WSDL est vide.
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
 xpath_assertion_negate=Vrai si aucune correspondance trouv\u00E9e
 xpath_assertion_option=Options d'analyse XML
 xpath_assertion_test=V\u00E9rificateur XPath
 xpath_assertion_tidy=Essayer et nettoyer l'entr\u00E9e
 xpath_assertion_title=Assertion XPath
 xpath_assertion_valid=Expression XPath valide
 xpath_assertion_validation=Valider le code XML \u00E0 travers le fichier DTD
 xpath_assertion_whitespace=Ignorer les espaces
 xpath_extractor_fragment=Retourner le fragment XPath entier au lieu du contenu
 xpath_extractor_query=Requ\u00EAte XPath \:
 xpath_extractor_title=Extracteur XPath
 xpath_tidy_quiet=Silencieux
 xpath_tidy_report_errors=Rapporter les erreurs
 xpath_tidy_show_warnings=Afficher les alertes
 you_must_enter_a_valid_number=Vous devez entrer un nombre valide
 zh_cn=Chinois (simplifi\u00E9)
 zh_tw=Chinois (traditionnel)
diff --git a/src/core/org/apache/jmeter/resources/messages_pt_BR.properties b/src/core/org/apache/jmeter/resources/messages_pt_BR.properties
index c38f7c542..f5769c980 100644
--- a/src/core/org/apache/jmeter/resources/messages_pt_BR.properties
+++ b/src/core/org/apache/jmeter/resources/messages_pt_BR.properties
@@ -1,907 +1,909 @@
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
 
 about=Sobre Apache JMeter
 add=Adicionar
 add_as_child=Adicionar como filho
 add_parameter=Adicionar Var\u00E1vel
 add_pattern=Adicionar Padr\u00E3o\:
 add_test=Adicionar Teste
 add_user=Adicionar Usu\u00E1rio
 add_value=Adicionar Valor
 addtest=Adicionar teste
 aggregate_graph=Gr\u00E1ficos Estat\u00EDsticos
 aggregate_graph_column=Coluna
 aggregate_graph_display=Exibir Gr\u00E1fico
 aggregate_graph_height=Altura
 aggregate_graph_max_length_xaxis_label=Largura m\u00E1xima do r\u00F3tulo do eixo x
 aggregate_graph_ms=Milisegundos
 aggregate_graph_response_time=Tempo de Tesposta
 aggregate_graph_save=Salvar Gr\u00E1fico
 aggregate_graph_save_table=Salvar Dados da Tabela
 aggregate_graph_save_table_header=Salvar Cabe\u00E7alho da Tabela
 aggregate_graph_title=Gr\u00E1fico Agregado
 aggregate_graph_use_group_name=Incluir nome do grupo no r\u00F3tulo?
 aggregate_graph_user_title=T\u00EDtulo para o Gr\u00E1fico
 aggregate_graph_width=Largura
 aggregate_report=Relat\u00F3rio Agregado
 aggregate_report_90%_line=Linha de 90%
 aggregate_report_bandwidth=KB/s
 aggregate_report_count=\# Amostras
 aggregate_report_error=Erro
 aggregate_report_error%=% de Erro
 aggregate_report_max=M\u00E1x.
 aggregate_report_median=Mediana
 aggregate_report_min=M\u00EDn.
 aggregate_report_rate=Vaz\u00E3o
 aggregate_report_stddev=Desvio Padr\u00E3o
 ajp_sampler_title=Testador AJP/1.3
 als_message=Nota\: O Processador de Logs de Acesso \u00E9 gen\u00E9rico em seu projeto e permite que voc\u00EA o especialize
 als_message2=seu pr\u00F3prio processador. Para tanto, implementar o LogParser, e adicionar o arquivo jar
 als_message3=diret\u00F3rio /lib e entre com a classe no testador
 analyze=Analizar Arquivo de Dados...
 anchor_modifier_title=Processador de Links HTML
 appearance=Apar\u00EAncia
 argument_must_not_be_negative=O Argumento n\u00E3o pode ser negativo\!
 assertion_assume_success=Ignorar estado
 assertion_code_resp=C\u00F3digo de Resposta
 assertion_contains=Cont\u00E9m
 assertion_equals=Igual
 assertion_headers=Cabe\u00E7alhos da Resposta
 assertion_matches=Combina
 assertion_message_resp=Mensagem da Resposta
 assertion_not=N\u00E3o
 assertion_pattern_match_rules=Regras para Combina\u00E7\u00E3o de Padr\u00F5es
 assertion_patterns_to_test=Padr\u00F5es a serem Testados
 assertion_resp_field=Testar que Campo da Resposta
 assertion_text_resp=Resposta de Texto
 assertion_textarea_label=Asser\u00E7\u00F5es\:
 assertion_title=Asser\u00E7\u00F5es de Resposta
 assertion_url_samp=URL Amostrada
 assertion_visualizer_title=Resultados de Asser\u00E7\u00E3o
 attribute=Atributo
 attrs=Atributos
 auth_base_url=URL Base
 auth_manager_title=Gerenciador de Autoriza\u00E7\u00E3o HTTP
 auths_stored=Autoriza\u00E7\u00F5es Armazenadas no Gerenciador de Autoriza\u00E7\u00E3o
 average=M\u00E9dia
 average_bytes=M\u00E9dia de Bytes
 bind=Liga\u00E7\u00E3o do Usu\u00E1rio Virtual
 browse=Procurar...
 bsf_sampler_title=Testador BSF
 bsf_script=Script a ser executado (vari\u00E1veis\: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Arquivo de script a ser executado
 bsf_script_language=Linguagem de scripting\:
 bsf_script_parameters=Par\u00E2metros a serem passados ao script/arquivo\:
 bsh_assertion_script=Script (veja abaixo quais vari\u00E1veis que est\u00E3o definidas)
 bsh_assertion_script_variables=As seguintes vari\u00E1veis est\u00E3o definidas para o script\:\nEscrita/Leitura\: Failure, FailureMessage, SampleResult, vars, props, log.\nSomente Leitura\: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=Asser\u00E7\u00E3o BeanShell
 bsh_function_expression=Express\u00E3o a ser avaliada
 bsh_sampler_title=Testador BeanShell
 bsh_script=Script (veja abaixo quais vari\u00E1veis est\u00E3o definidas)
 bsh_script_file=Arquivo de script
 bsh_script_parameters=Par\u00E2metros (\=> String Parameters e String []bsh.args)
 bsh_script_reset_interpreter=Reiniciar bsh.Interpreter antes de cada chamada
 bsh_script_variables=As seguintes vari\u00E1veis est\u00E3o definidas para o script\:\nSampleResult, RespondeCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Eu estou ocupado testando, por favor pare o teste antes de alterar as configura\u00E7\u00F5es
 cache_manager_title=Gerenciador de Cache HTTP
 cache_session_id=Fazer cache do ID da sess\u00E3o?
 cancel=Cancelar
 cancel_exit_to_save=Existem itens n\u00E3o salvos. Voc\u00EA deseja salvar antes de sair?
 cancel_new_to_save=Existem itens n\u00E3o salvos. Voc\u00EA deseja salvar antes de limpar o plano de teste?
 cancel_revert_project=Existem itens n\u00E3o salvos. Voc\u00EA deseja reverter para o plano de teste salvo previamente?
 char_value=N\u00FAmero unicode do caracter (decimal ou 0xhex)
 choose_function=Escolher Fun\u00E7\u00E3o
 choose_language=Escolher Linguagem
 clear=Limpar
 clear_all=Limpar Tudo
 clear_cache_per_iter=Limpar cache a cada itera\u00E7\u00E3o?
 clear_cookies_per_iter=Limpar cookies a cada itera\u00E7\u00E3o?
 column_delete_disallowed=N\u00E3o \u00E9 permitida a exclus\u00E3o desta coluna.
 column_number=N\u00FAmero da coluna no arquivo CSV | pr\u00F3x | *apelido
 compare=Comparar
 comparefilt=Filtro de compara\u00E7\u00E3o
 config_element=Elemento de Configura\u00E7\u00E3o
 config_save_settings=Configurar
 configure_wsdl=Configurar
 constant_throughput_timer_memo=Adicionar um atraso entre amostragens para obter vaz\u00E3o constante
 constant_timer_delay=Atraso do usu\u00E1rio virtual (em milisegundos)
 constant_timer_memo=Adicionar um atraso constante entre amostragens
 constant_timer_title=Temporizador Constante
 content_encoding=Codifica\u00E7\u00E3o do conte\u00FAdo\:
 controller=Controlador
 cookie_manager_policy=Pol\u00EDtica de Cookie
 cookie_manager_title=Gerenciador de Cookie HTTP
 cookies_stored=Cookies Definidos pelo Usu\u00E1rio
 copy=Copiar
 corba_config_title=Configura\u00E7\u00E3o do Testador CORBA
 corba_input_data_file=Arquivo de Dados de Entrada
 corba_methods=Escolher m\u00E9todo a ser invocado\:
 corba_name_server=Nome do Servidor\:
 corba_port=N\u00FAmero da Porta\:
 corba_request_data=Dados de Entrada
 corba_sample_title=Testador CORBA
 counter_config_title=Contador
 counter_per_user=Realiza contagem independentemente para cada usu\u00E1rio
 countlim=Tamanho limite
 csvread_file_file_name=Arquivo CSV de onde os valores ser\u00E3o obtidos | *apelido
 cut=Recortar
 cut_paste_function=Copiar e colar texto da fun\u00E7\u00E3o
 database_conn_pool_max_usage=Uso M\u00E1ximo Para Cada Conex\u00E3o\:
 database_conn_pool_props=Grupo de Conex\u00F5es com o Banco de Dados
 database_conn_pool_size=N\u00FAmero de Conex\u00F5es no Grupo de Conex\u00F5es
 database_conn_pool_title=Padr\u00F5es JDBC do Grupo de Conex\u00F5es ao Banco de Dados
 database_driver_class=Classe do Driver\:
 database_login_title=Padr\u00F5es JDBC para Acesso ao Banco de Dados
 database_sql_query_string=Consulta SQL\:
 database_sql_query_title=Padr\u00F5es JDBC para Consultas SQL
 database_testing_title=Requisi\u00E7\u00E3o JDBC
 database_url=URL JDBC\:
 database_url_jdbc_props=URL do Banco de Dados e Driver JDBC
 de=Alem\u00E3o
 debug_off=Desabilitar debug
 debug_on=Habilitar debug
 default_parameters=Par\u00E2metros Padr\u00E3o
 default_value_field=Valor Padr\u00E3o\:
 delay=Atraso para in\u00EDcio (segundos)
 delete=Excluir
 delete_parameter=Excluir Vari\u00E1vel
 delete_test=Excluir Teste
 delete_user=Excluir Usu\u00E1rio
 deltest=Teste de exclus\u00E3o
 deref=Dereferenciar apelidos
 disable=Desabilitar
 distribution_graph_title=Gr\u00E1fico de Distribui\u00E7\u00E3o (alfa)
 distribution_note1=O gr\u00E1fico ser\u00E1 atualizado a cada 10 amostras
 domain=Dom\u00EDnio
 done=Pronto
 duration=Dura\u00E7\u00E3o (segundos)
 duration_assertion_duration_test=Dura\u00E7\u00E3o para Avaliar
 duration_assertion_failure=A opera\u00E7\u00E3o tomou muito tempo\: levou {0} milisegundos, mas n\u00E3o deveria ter levado mais do que {1} milisegundos
 duration_assertion_input_error=Favor entrar com um inteiro positivo v\u00E1lido.
 duration_assertion_label=Dura\u00E7\u00E3o em milisegundos\:
 duration_assertion_title=Asser\u00E7\u00E3o de Dura\u00E7\u00E3o
 edit=Editar
 email_results_title=Enviar Resultados por Email
 en=Ingl\u00EAs
 enable=Habilitar
 encode?=Codificar?
 encoded_value=Valor da URL Codificada
 endtime=Tempo de T\u00E9rmino
 entry_dn=Entrada DN
 entrydn=Entrada DN
 error_loading_help=Erro ao carregar p\u00E1gina de ajuda
 error_occurred=Um erro ocorreu
 error_title=Erro
 es=Espanhol
 escape_html_string=String a ser escapada
 eval_name_param=Texto contendo refer\u00EAncias de fun\u00E7\u00F5es e vari\u00E1veis
 evalvar_name_param=Nome da vari\u00E1vel
 example_data=Dados da amostra
 example_title=Testador de Exemplo
 exit=Sair
 expiration=Expira\u00E7\u00E3o
 field_name=Nome do campo
 file=Arquivo
 file_already_in_use=Este arquivo j\u00E1 est\u00E1 em uso
 file_visualizer_append=Adicionar a um Arquivo de Dados Existente
 file_visualizer_auto_flush=Automaticamente descarregar dados (flush) ap\u00F3s cada amostra de dados
 file_visualizer_browse=Procurar...
 file_visualizer_close=Fechar
 file_visualizer_file_options=Op\u00E7\u00F5es do Arquivo
 file_visualizer_filename=Nome do arquivo
 file_visualizer_flush=Descarregar (flush)
 file_visualizer_missing_filename=N\u00E3o foi especificado nenhum nome de arquivo de sa\u00EDda.
 file_visualizer_open=Abrir
 file_visualizer_output_file=Escrever resultados para arquivo / Ler a partir do arquivo
 file_visualizer_submit_data=Incluir Dados Enviados
 file_visualizer_title=Relat\u00F3rios de Arquivo
 file_visualizer_verbose=Sa\u00EDda detalhada (verbose)
 filename=Nome do arquivo
 follow_redirects=Seguir redire\u00E7\u00F5es
 follow_redirects_auto=Redirecionar automaticamente
 foreach_controller_title=Controlador ParaCada (ForEach)
 foreach_input=Prefixo da vari\u00E1vel de entrada
 foreach_output=Nome da vari\u00E1vel de sa\u00EDda
 foreach_use_separator=Adicionar "_" antes do n\u00FAmero?
 format=Formato do n\u00FAmero
 fr=Franc\u00EAs
 ftp_binary_mode=Usar modo bin\u00E1rio?
 ftp_local_file=Arquivo local\:
 ftp_local_file_contents=Conte\u00FAdo do Arquivo Local\:
 ftp_remote_file=Arquivo remoto\:
 ftp_sample_title=Padr\u00F5es para Requisi\u00E7ao FTP
 ftp_save_response_data=Salvar Arquivos na Resposta?
 ftp_testing_title=Requisi\u00E7\u00E3o FTP
 function_dialog_menu_item=Di\u00E1logo de Fun\u00E7\u00E3o de Ajuda
 function_helper_title=Fun\u00E7\u00E3o de Ajuda
 function_name_param=Nome da vari\u00E1vel na qual ser\u00E1 armazenado o resultado (requerido)
 function_name_paropt=Nome da vari\u00E1vel na qual ser\u00E1 armazenado o resultado (opcional)
 function_params=Par\u00E2metros da Fun\u00E7\u00E3o
 functional_mode=Modo de Teste Funcional (ex\: salvar dados das respostas e dados dos testadores)
 functional_mode_explanation=Selecionando Modo de Teste Funcional pode afetar o desempenho de modo adverso.
 gaussian_timer_delay=Offset do Atraso Constante (em milisegundos)\:
 gaussian_timer_memo=Adiciona um atraso aleat\u00F3rio atrav\u00E9s de uma distribui\u00E7\u00E3o gaussiana.
 gaussian_timer_range=Desvio (em milisegundos)\:
 gaussian_timer_title=Temporizador Aleat\u00F3rio Gaussiano
 generate=Gerar
 generator=Nome da classe Geradora
 generator_cnf_msg=N\u00E3o foi poss\u00EDvel encontrar a classe geradora. Verifique se voc\u00EA colocou o jar correto no diret\u00F3rio  /lib.
 generator_illegal_msg=N\u00E3o foi poss\u00EDvel acessar a classe geradora devido a uma IllegalAccessException.
 generator_instantiate_msg=N\u00E3o foi poss\u00EDvel criar uma inst\u00E2ncia do processador gerador. Verifique se o gerador implementa a interface Generator.
 get_xml_from_file=Arquivo com dados XML SOAP (substitui o texto acima)
 get_xml_from_random=Diret\u00F3rio das Mensagens
 graph_choose_graphs=Gr\u00E1ficos a serem Exibidos
 graph_full_results_title=Gr\u00E1fico de Resultados Completos
 graph_results_average=M\u00E9dia
 graph_results_data=Dados
 graph_results_deviation=Desvio
 graph_results_latest_sample=\u00DAltima Amostra
 graph_results_median=Mediana
 graph_results_no_samples=N\u00FAm. de Amostras
 graph_results_throughput=Vaz\u00E3o
 graph_results_title=Gr\u00E1fico de Resultados
 grouping_add_separators=Adicionar separadores entre grupos
 grouping_in_controllers=Colocar cada grupo em um novo controlador
 grouping_mode=Agrupamento\:
 grouping_no_groups=N\u00E3o agrupar testadores
 grouping_store_first_only=Armazenar primeiro testador de cada grupo apenas
 header_manager_title=Gerenciador de Cabe\u00E7alhos HTTP
 headers_stored=Cabe\u00E7alhos Armazenados no Gerenciador de Cabe\u00E7alhos
 help=Ajuda
 help_node=O que \u00E9 este n\u00F3?
 html_assertion_file=Escrever relat\u00F3rio do JTidy em arquivo
 html_assertion_label=Asser\u00E7\u00E3o HTML
 html_assertion_title=Asser\u00E7\u00E3o HTML
 html_parameter_mask=M\u00E1scara de Par\u00E2metro HTML
 http_implementation=Implementa\u00E7\u00E3o\:
 http_response_code=C\u00F3digo da Resposta HTTP
 http_url_rewriting_modifier_title=Modificador de Re-escrita de URL HTTP
 http_user_parameter_modifier=Modificador de Par\u00E2metros HTTP do Usu\u00E1rio
 httpmirror_title=Servidor Espelho HTTP
 id_prefix=Prefixo do ID
 id_suffix=Sufixo do ID
 if_controller_evaluate_all=Avaliar para todos os filhos?
 if_controller_expression=Interpretar Condi\u00E7\u00E3o como Express\u00E3o de Vari\u00E1vel?
 if_controller_label=Condi\u00E7\u00E3o (padr\u00E3o\: Javascript)
 if_controller_title=Controlador Se
 ignore_subcontrollers=Ignorar blocos de sub-controladores
 include_controller=Controlador de Inclus\u00E3o
 include_equals=Incluir Igual?
 include_path=Incluir Plano de Teste
 increment=Incremento
 infinite=Infinito
 initial_context_factory=F\u00E1brica de Contexto Inicial
 insert_after=Inserir Depois
 insert_before=Inserir Antes
 insert_parent=Inserir como Pai
 interleave_control_title=Controlador de Intercala\u00E7\u00E3o
 intsum_param_1=Primeiro inteiro para adicionar.
 intsum_param_2=Segundo inteiro a ser adicionado - posteriormente inteiros podem ser somados atrav\u00E9s da adi\u00E7\u00E3o de novos argumentos.
 invalid_data=Dados inv\u00E1lidos
 invalid_mail=Ocorreu um erro ao enviar o e-mail
 invalid_mail_address=Foram detectados um ou mais endere\u00E7os de email inv\u00E1lidos
 invalid_mail_server=Houve um problema ao contactar o servidor de email (veja arquivo de log do JMeter)
 invalid_variables=Vari\u00E1veis inv\u00E1lidas
 iteration_counter_arg_1=TRUE, para que cada usu\u00E1rio tenha seu pr\u00F3prio contador, FALSE para um contador global
 iterator_num=Contador de Itera\u00E7\u00E3o
 ja=Japon\u00EAs
 jar_file=Arquivos Jar
 java_request=Requisi\u00E7\u00E3o Java
 java_request_defaults=Padr\u00F5es de Requisi\u00E7\u00E3o Java
 javascript_expression=Express\u00E3o JavaScript a ser avaliada
 jexl_expression=Express\u00E3o JEXL a ser avaliada
 jms_auth_required=Requerido
 jms_client_caption=Cliente recebedor usa TopicSubscriber.receive() para aguardar uma mensagem.
 jms_client_caption2=MessageListener usa a interface onMessage(Message) para aguardar novas mensagens.
 jms_client_type=Cliente
 jms_communication_style=Estilo de comunica\u00E7\u00E3o
 jms_concrete_connection_factory=F\u00E1brica Concreta de Conex\u00E3o
 jms_config=Configura\u00E7\u00E3o
 jms_config_title=Configura\u00E7\u00E3o JMS
 jms_connection_factory=F\u00E1brica de Conex\u00E3o
 jms_error_msg=Mensagens deveriam ser lidas de um arquivo externo. Entrada de texto est\u00E1 selecionado agora, favor lembrar de alter\u00E1-la.
 jms_file=Arquivo
 jms_initial_context_factory=F\u00E1brica de Contexto Inicial
 jms_itertions=N\u00FAmeros de amostras para agregar
 jms_jndi_defaults_title=Configura\u00E7\u00E3o Padr\u00E3o de JNDI
 jms_jndi_props=Propriedades JNDI
 jms_message_title=Propriedades das mensagens
 jms_message_type=Tipo das Mensagens
 jms_msg_content=Conte\u00FAdo
 jms_object_message=Mensagens de Objetos
 jms_point_to_point=JMS Ponto a Ponto
 jms_props=Propriedades JMS
 jms_provider_url=URL do Provedor
 jms_publisher=Publicador JMS
 jms_pwd=Senha
 jms_queue=Fila
 jms_queue_connection_factory=F\u00E1brica de Conex\u00F5es em Fila
 jms_queueing=Recursos JMS
 jms_random_file=Arquivo Aleat\u00F3rio
 jms_read_response=Resposta Lida
 jms_receive_queue=Nome da Fila de Recebimento JNDI
 jms_request=Somente Requisitar
 jms_requestreply=Requisitar e Responder
 jms_sample_title=Padr\u00F5es de Requisi\u00E7\u00E3o JMS
 jms_send_queue=Fila de Requisi\u00E7\u00E3o de nomes JNDI
 jms_subscriber_on_message=Utilizar MessageListener.onMessage()
 jms_subscriber_receive=Usar TopicSubscriber.receive()
 jms_subscriber_title=Assinante JMS
 jms_testing_title=Requisi\u00E7\u00E3o de Mensagens
 jms_text_message=Mensagem de Texto
 jms_timeout=Tempo limite (timeout) em milisegundos
 jms_topic=T\u00F3pico
 jms_use_auth=Usar Autoriza\u00E7\u00E3o?
 jms_use_file=Do Arquivo
 jms_use_non_persistent_delivery=Utilizar modo de entrega n\u00E3o persistente
 jms_use_properties_file=Utilizar aquivo jndi.properties
 jms_use_random_file=Arquivo Aleat\u00F3rio
 jms_use_req_msgid_as_correlid=Utilizar ID de Mensagem de Requisi\u00E7\u00E3o como ID de Correla\u00E7\u00E3o
 jms_use_text=\u00C1rea de texto
 jms_user=Usu\u00E1rio
 jndi_config_title=Configura\u00E7\u00E3o JNDI
 jndi_lookup_name=Interface Remota
 jndi_lookup_title=Configura\u00E7\u00E3o de Lookup JNDI
 jndi_method_button_invoke=Invocar
 jndi_method_button_reflect=Refletir
 jndi_method_home_name=Nome do M\u00E9todo Home
 jndi_method_home_parms=Par\u00E2metros do M\u00E9todo Home
 jndi_method_name=Configura\u00E7\u00E3o do M\u00E9todo
 jndi_method_remote_interface_list=Interfaces Remotas
 jndi_method_remote_name=Nome do M\u00E9todo Remoto
 jndi_method_remote_parms=Par\u00E2metros do M\u00E9todo Remoto
 jndi_method_title=Configura\u00E7\u00E3o do M\u00E9todo Remoto
 jndi_testing_title=Requisi\u00E7\u00E3o JNDI
 jndi_url_jndi_props=Propriedades JNDI
 junit_append_error=Adicionar erros de asser\u00E7\u00E3o
 junit_append_exception=Adicionar exce\u00E7\u00F5es de tempo de execu\u00E7\u00E3o
 junit_constructor_error=N\u00E3o foi poss\u00EDvel criar uma inst\u00E2ncia da classe
 junit_constructor_string=R\u00F3tulo String do Construtor
 junit_do_setup_teardown=N\u00E3o chamar setUp e tearDown
 junit_error_code=C\u00F3digo de Erro
 junit_error_default_msg=Houve um erro inesperado
 junit_error_msg=Mensagem de Erro
 junit_failure_code=C\u00F3digo da Falha
 junit_failure_default_msg=O teste falhou
 junit_failure_msg=Mensagns de Falha
 junit_pkg_filter=Filtro de Pacote
 junit_request=Requisi\u00E7\u00E3o JUnit
 junit_request_defaults=Padr\u00F5es de Requisi\u00E7\u00E3o JUnit
 junit_success_code=C\u00F3digo de Sucesso
 junit_success_default_msg=Teste com sucesso
 junit_success_msg=Mensagem de Sucesso
 junit_test_config=Par\u00E2metros de Teste JUnit
 junit_test_method=M\u00E9todo de Teste
 ldap_argument_list=Lista de Argumentos LDAP
 ldap_connto=Tempo limite de Conex\u00E3o (timeout) em milisegundos
 ldap_parse_results=Processar os resultados da busca?
 ldap_sample_title=Padr\u00F5es de Requisi\u00E7\u00E3o LDAP
 ldap_search_baseobject=Realizar busca b\u00E1sica de objetos
 ldap_search_onelevel=Realizar busca de um n\u00EDvel
 ldap_search_subtree=Realizar busca em sub-\u00E1rvore
 ldap_secure=Utilizar Protocolo LDAP Seguro?
 ldap_testing_title=Requisi\u00E7\u00E3o LDAP
 ldapext_sample_title=Padr\u00F5es de Requisi\u00E7\u00E3o LDAP Estendidas
 ldapext_testing_title=Requisi\u00E7\u00E3o LDAP Estendida
 library=Biblioteca
 load=Carregar
 load_wsdl=Carregar WSDL
 log_errors_only=Erros
 log_file=Localiza\u00E7\u00E3o do arquivo de log
 log_function_comment=Coment\u00E1rios adicionais (opcional)
 log_function_level=N\u00EDvel de log (padr\u00E3o INFO) ou OUT ou ERR
 log_function_string=String a ser logada
 log_function_string_ret=String a ser logada (e retornada)
 log_function_throwable=Texto a ser lan\u00E7ado (opcional)
 log_only=Apenas Logar/Exibir
 log_parser=Nome da Classe Processadora de Logs
 log_parser_cnf_msg=N\u00E3o foi poss\u00EDvel encontrar a classe. Verifique se o jar se encontra no diret\u00F3rio /lib.
 log_parser_illegal_msg=N\u00E3o foi poss\u00EDvel acessar a classe devido a IllegalAccessException.
 log_parser_instantiate_msg=N\u00E3o foi poss\u00EDvel criar uma inst\u00E2ncia do processador de logs. Verifique se o processador implementa a interface LogParser.
 log_sampler=Testador de Log de Acessp do Tomcat
 log_success_only=Sucessos
 logic_controller_title=Controlador Simples
 login_config=Configura\u00E7\u00E3o de Login
 login_config_element=Elemento de Configura\u00E7\u00E3o de Login
 longsum_param_1=Primeiro long a ser adicionado
 longsum_param_2=Segundo long a ser adicionado - adicionalmente novos longs podem ser somados atrav\u00E9s da adi\u00E7\u00E3o de novos argumentos
 loop_controller_title=Controlador de Itera\u00E7\u00E3o
 looping_control=Controle de Itera\u00E7\u00E3o
 lower_bound=Limite Inferior
 mail_reader_account=Nome do usu\u00E1rio\:
 mail_reader_all_messages=Todos
 mail_reader_delete=Excluir mensagens do servidor
 mail_reader_folder=Diret\u00F3rio\:
 mail_reader_num_messages=N\u00FAmero de mensagens a serem recuperadas\:
 mail_reader_password=Senha\:
 mail_reader_server=Servidor\:
 mail_reader_server_type=Tipo do Servidor\:
 mail_reader_storemime=Armazenar as mensagens utilizando MIME
 mail_reader_title=Testador Leitor de Emails
 mail_sent=Email enviado com sucesso
 mailer_attributes_panel=Atributos de envio de emails
 mailer_error=N\u00E3o foi poss\u00EDvel enviar email. Favor verificar as configura\u00E7\u00F5es.
 mailer_visualizer_title=Vizualizador de Email
 match_num_field=N\u00FAmero para Combina\u00E7\u00E3o (0 para aleat\u00F3rio)
 max=M\u00E1ximo
 maximum_param=O valor m\u00E1ximo permitido para um intervalo de valores
 md5hex_assertion_failure=Erro avaliando soma MD5\: encontrado {0} mas deveria haver {1}
 md5hex_assertion_md5hex_test=MD5Hex para Asser\u00E7\u00E3o
 md5hex_assertion_title=Asser\u00E7\u00E3o MD5Hex
 memory_cache=Cache em Mem\u00F3ria
 menu_assertions=Asser\u00E7\u00F5es
 menu_close=Fechar
 menu_collapse_all=Fechar Todos
 menu_config_element=Elemento de Configura\u00E7\u00E3o
 menu_edit=Editar
 menu_expand_all=Expandir Todos
 menu_generative_controller=Testador
 menu_listener=Ouvinte
 menu_logic_controller=Controlador L\u00F3gico
 menu_merge=Mesclar
 menu_modifiers=Modificadores
 menu_non_test_elements=Elementos que n\u00E3o s\u00E3o de Teste
 menu_open=Abrir
 menu_post_processors=P\u00F3s-Processadores
 menu_pre_processors=Pr\u00E9-Processadores
 menu_response_based_modifiers=Modificadores Baseados na Resposta
 menu_timer=Temporizador
 metadata=Metadados
 method=M\u00E9todo\:
 minimum_param=Valor m\u00EDnimo permitido para um intervalo de valores
 minute=minuto
 modddn=Velho nome da entrada
 modification_controller_title=Controlador de Modifica\u00E7\u00E3o
 modification_manager_title=Gerenciador de Modifica\u00E7\u00E3o
 modify_test=Modificar Teste
 modtest=Teste de Modifica\u00E7\u00E3o
 module_controller_module_to_run=M\u00F3dulo a ser executado
 module_controller_title=Controlador de M\u00F3dulo
 module_controller_warning=N\u00E3o foi poss\u00EDvel encontrar o m\u00F3dulo\:
 monitor_equation_active=Ativo\: (ocupado / max) > 25%
 monitor_equation_dead=Morto\: sem resposta
 monitor_equation_healthy=Saud\u00E1vel\: (ocupado / max) < 25%
 monitor_equation_load=Carga\: ((ocupado / max) * 50) + ((mem\u00F3ria utilizada / max mem\u00F3ria) * 50)
 monitor_equation_warning=Alerta\: (ocupado / max) > 67%
 monitor_health_tab_title=Sa\u00FAde
 monitor_health_title=Monitorar Resultados
 monitor_is_title=Usar como Monitor
 monitor_label_prefix=Prefixo de Conex\u00E3o
 monitor_label_right_active=Ativo
 monitor_label_right_dead=Morto
 monitor_label_right_healthy=Saud\u00E1vel
 monitor_label_right_warning=Alerta
 monitor_legend_health=Sa\u00FAde
 monitor_legend_load=Carga
 monitor_legend_memory_per=Mem\u00F3ria % (usada / total)
 monitor_legend_thread_per=Thread % (ocupado / max)
 monitor_performance_servers=Servidores
 monitor_performance_tab_title=Desempenho
 monitor_performance_title=Gr\u00E1fico de Desempenho
 name=Nome\:
 new=Novo
 newdn=Novo nome distingu\u00EDvel
 no=Noruegu\u00EAs
 number_of_threads=N\u00FAmero de Usu\u00E1rios Virtuais (threads)\:
 obsolete_test_element=O elemento de teste est\u00E1 obsoleto.
 once_only_controller_title=Controlador de Uma \u00DAnica Vez
 open=Abrir...
 option=Op\u00E7\u00F5es
 optional_tasks=Tarefas Opcionais
 paramtable=Enviar Par\u00E2metros Com a Requisi\u00E7\u00E3o
 password=Senha
 paste=Colar
 paste_insert=Colar como Inser\u00E7\u00E3o
 path=Caminho\:
 path_extension_choice=Extens\u00F5es do Caminho (usar ";" como separador)
 path_extension_dont_use_equals=N\u00E3o usar igual nas extens\u00F5es do caminho (compatibilidae com Intershop Enfinity)
 path_extension_dont_use_questionmark=N\u00E3o utilizar s\u00EDmbolo de interroga\u00E7\u00E3o nas extens\u00F5es do caminho (Compatibilidade com Intershop Enfinity)
 patterns_to_exclude=Padr\u00F5es de URL a serem exclu\u00EDdos
 patterns_to_include=Padr\u00F5es de URL a serem inclu\u00EDdos
 pl=Polon\u00EAs
 port=Porta\:
 property_default_param=Valor padr\u00E3o
 property_edit=Editar
 property_editor.value_is_invalid_message=O texto informado n\u00E3o \u00E9 um valor v\u00E1lido para esta propriedade.\nA propriedade ser\u00E1 revertida para o seu valor pr\u00E9vio.
 property_editor.value_is_invalid_title=Entrada inv\u00E1lida.
 property_name_param=Nome da propriedade
 property_returnvalue_param=Retornar Valor Original da Propriedade (padr\u00E3o\: false)?
 property_undefined=Indefinido
 property_value_param=Valor da propriedade
 property_visualiser_title=Exibi\u00E7\u00E3o da Propriedade
 protocol=Protocolo [http]\:
 protocol_java_border=Classe Java
 protocol_java_classname=Nome da classe\:
 protocol_java_config_tile=Configurar amostra Java
 protocol_java_test_title=Teste Java
 provider_url=URL do Provedor
 proxy_assertions=Adicionar Asser\u00E7\u00F5es
 proxy_cl_error=Se estiver especificando um servidor proxy, nome do servidor e porta precisam ser informados
 proxy_content_type_exclude=Excluir\:
 proxy_content_type_filter=Filtro de tipo de conte\u00FAdo\:
 proxy_content_type_include=Incluir\:
 proxy_daemon_bind_error=N\u00E3o foi poss\u00EDvel criar o proxy - porta em uso\: Escolha outra porta.
 proxy_daemon_error=N\u00E3o foi poss\u00EDvel criar o proxy - veja log para detalhes
 proxy_headers=Capturar Cabe\u00E7alhos HTTP
 proxy_httpsspoofing=Tentar ataque  HTTPS (spoofing)
 proxy_httpsspoofing_match=String de combina\u00E7\u00E3o de URL opcional\:
 proxy_regex=Combina\u00E7\u00E3o de express\u00E3o regular
 proxy_sampler_settings=Configura\u00E7\u00F5es do Testador HTTP
 proxy_sampler_type=Tipo\:
 proxy_separators=Adicionar Separadores
 proxy_target=Controlador alvo\:
 proxy_test_plan_content=Conte\u00FAdo do Plano de Teste
 proxy_title=Servidor HTTP Proxy
 pt_br=Portugu\u00EAs (Brasileiro)
 ramp_up=Tempo de inicializa\u00E7\u00E3o (em segundos)
 random_control_title=Controlador Aleat\u00F3rio
 random_order_control_title=Controlador de Ordem Aleat\u00F3ria
 read_response_message=N\u00E3o est\u00E1 configurado para ler a resposta. Para ver a resposta, favor marcar esta op\u00E7\u00E3o no testador.
 read_response_note=Se ler resposta est\u00E1 desmarcado, o testador n\u00E3o ir\u00E1 ler a resposta
 read_response_note2=ou configurar o SampleResult. Isto melhora o desempenho, mas significa que
 read_response_note3=o conte\u00FAdo da resposta n\u00E3o ser\u00E1 logado.
 read_soap_response=Ler Respostas SOAP
 realm=Reino (realm)
 record_controller_title=Controlador de Grava\u00E7\u00E3o
 ref_name_field=Nome de Refer\u00EAncia\:
 regex_extractor_title=Extractor de Express\u00E3o Regular
 regex_field=Express\u00E3o Regular
 regex_source=Campo da Resposta a ser verificado
 regex_src_body=Corpo (body)
 regex_src_body_unescaped=Corpo (body) - n\u00E3o escapado
 regex_src_hdrs=Cabe\u00E7alhos (headers)
 regexfunc_param_1=Express\u00E3o regular usada para buscar amostras anteriores - ou vari\u00E1vel.
 regexfunc_param_2=Modelo (template) para substitui\u00E7\u00E3o de string, usando grupos de express\u00F5es reuglares. Formato \u00E9 ${grupo}$. Exemplo $1$.
 regexfunc_param_3=Que combina\u00E7\u00E3o usar. Um inteiro 1 ou maior, RAND indica que JMeter deve escolher aleatoriamente, um ponto flutuante (float) ou ALL indicando todas as combina\u00E7\u00F5es devem ser usadas ([1])
 regexfunc_param_4=Entre texto. Se ALL est\u00E1 selecionado, o que estiver ENTRE o texto informado ser\u00E1 utilizado para gerar os resultados ([""])
 regexfunc_param_5=Texto padr\u00E3o. Usado no lugar do modelo (template) se a express\u00E3o regular n\u00E3o econtrar nenhuma combina\u00E7\u00E3o ([""])
 regexfunc_param_7=Nome da vari\u00E1vel de entrada contidas no texto a ser processado ([amostra anterior]) 
 remote_error_init=Erro inicializando o servidor remoto
 remote_error_starting=Erro inicializando o servidor remoto
 remote_exit=Sa\u00EDda Remota
 remote_exit_all=Sair de Todos Remotos
 remote_start=Inicializar Remoto
 remote_start_all=Inicializar Todos Remotos
 remote_stop=Parar Remoto
 remote_stop_all=Parar Todos Remotos
 remove=Remover
 rename=Renomear entrada
 report=Relat\u00F3rio
 report_bar_chart=Gr\u00E1fico de Barras
 report_base_directory=Diret\u00F3rio Base
 report_chart_caption=Legenda do Gr\u00E1fico
 report_chart_x_axis=Eixo X
 report_chart_x_axis_label=R\u00F3tulo do Eixo X
 report_chart_y_axis=Eixo Y
 report_chart_y_axis_label=R\u00F3tulo do Eixo Y
 report_line_graph=Gr\u00E1fico de Linha
 report_line_graph_urls=Incluir URLs
 report_output_directory=Diret\u00F3rio de Sa\u00EDda do Relat\u00F3rio
 report_page=P\u00E1gina do Relat\u00F3rio
 report_page_element=Elemento da P\u00E1gina
 report_page_footer=Rodap\u00E9 da P\u00E1gina
 report_page_header=Cabe\u00E7alho da P\u00E1gina
 report_page_index=Criar \u00CDndice de P\u00E1ginas
 report_page_intro=P\u00E1gina de Introdu\u00E7\u00E3o
 report_page_style_url=URL da folha de estilos (stylesheet)
 report_page_title=T\u00EDtulo da P\u00E1gina
 report_pie_chart=Gr\u00E1fico de Pizza
 report_plan=Plano de Relat\u00F3rio
 report_select=Selecionar
 report_summary=Sum\u00E1rio do Relat\u00F3rio
 report_table=Tabela do Relat\u00F3rio
 report_writer=Escritor de Relat\u00F3rio
 report_writer_html=Escritor de Relat\u00F3rio HTML
 request_data=Requisitar Dados
 reset_gui=Reiniciar GUI
 response_save_as_md5=Salvar respostas como chave MD5?
 restart=Reiniciar
 resultaction_title=Manuseador de A\u00E7\u00F5es de Estados do Resultado
 resultsaver_errors=Somente Salvar Respostas que Falharam
 resultsaver_prefix=Prefixo do nome do arquivo
 resultsaver_skipautonumber=N\u00E3o adicionar n\u00FAmeros ao prefixo
 resultsaver_success=Somente Salvar Respostas de Sucesso
 resultsaver_title=Salvar respostas para arquivo
 resultsaver_variable=Nome da Vari\u00E1vel\:
 retobj=Objeto de retorno
 reuseconnection=Reusar conex\u00E3o
 revert_project=Reverter
 revert_project?=Reverter projeto?
 root=Raiz
 root_title=Raiz
 run=Executar
 running_test=Testes executando
 runtime_controller_title=Controlador de Tempo de Execu\u00E7\u00E3o
 runtime_seconds=Tempo de execu\u00E7\u00E3o (segundos)
 sample_result_save_configuration=Configura\u00E7\u00E3o de Salvar Resultados da Amostra
 sample_scope=Quais amostras testar
 sample_scope_all=Amostras principais e sub-amostras
 sample_scope_children=Somente Sub-amostras
 sample_scope_parent=Somente Amostras principais
 sampler_label=R\u00F3tulo
 sampler_on_error_action=A\u00E7\u00E3o a ser tomada depois de erro do testador
 sampler_on_error_continue=Continuar
 sampler_on_error_stop_test=Interromper Teste
 sampler_on_error_stop_test_now=Interrompe Teste Agora
 sampler_on_error_stop_thread=Interromper Usu\u00E1rio Virtual
 save=Salvar
 save?=Salvar?
 save_all_as=Salvar Plano de Teste como
 save_as=Salvar Sele\u00E7\u00E3o Como...
 save_as_error=Mais de um item selecionado\!
 save_as_image=Salvar N\u00F3 como Imagem
 save_as_image_all=Salvar Tela Como Imagem
 save_assertionresultsfailuremessage=Salvar Mensagens de Falha de Asser\u00E7\u00E3o
 save_assertions=Salvar Resultados de Asser\u00E7\u00F5es (XML)
 save_asxml=Salvar como XML
 save_bytes=Salvar quantidade de bytes
 save_code=Salvar C\u00F3digo da Resposta
 save_datatype=Salvar Tipo de Dados
 save_encoding=Salvar Codifica\u00E7\u00E3o
 save_fieldnames=Salvar Nomes dos Campos (CSV)
 save_filename=Nome do Arquivo para Salvar Respostas
 save_graphics=Salvar Gr\u00E1fico
 save_hostname=Salvar Nome do Host
 save_label=Salvar R\u00F3tulo
 save_latency=Salvar Lat\u00EAncia
 save_message=Salvar Mensagens das Respostas
 save_overwrite_existing_file=O arquivo selecionado j\u00E1 existe, voc\u00EA quer substitu\u00ED-lo?
 save_requestheaders=Salvar Cabe\u00E7alhos das Requisi\u00E7\u00F5es (XML)
 save_responsedata=Salvar Dados das Respostas (XML)
 save_responseheaders=Salvar Cabe\u00E7alhos das Respostas (XML)
 save_samplecount=Salvar Amostra e Contador de Erros
 save_samplerdata=Salvar Dados do Testador (XML)
 save_subresults=Salvar sub resultados (XML)
 save_success=Salvar Sucessos
 save_threadcounts=Salvar Contador de Usu\u00E1rios Virtuais Ativos
 save_threadname=Salvar Nome do Usu\u00E1rio Virtual
 save_time=Salvar Tempo Decorrido
 save_timestamp=Salvar Data e Hora
 save_url=Salvar URL
 sbind=Ligar/Desligar \u00FAnico (single bind/unbind)
 scheduler=Agendador
 scheduler_configuration=Configura\u00E7\u00E3o do Agendador
 scope=Escopo
 search_base=Base de busca
 search_filter=Filtro de busca
 search_test=Teste de busca
 searchbase=Base de busca
 searchfilter=Filtro de Busca
 searchtest=Teste de Busca
 second=segundo
 secure=Seguro
 send_file=Enviar Arquivos com a Requisi\u00E7\u00E3o
 send_file_browse=Procurar...
 send_file_filename_label=Caminho do Arquivo\:
 send_file_param_name_label=Nome do Par\u00E2metro\:
 server=Nome do servidor ou IP\:
 servername=Nome do servidor\:
 session_argument_name=Nome do Argumento de Sess\u00E3o
 should_save=Voc\u00EA deveria salvar seu plano de teste antes de execut\u00E1-lo.\nSe voc\u00EA est\u00E1 usando suporte a arquivos de dados (ex\: Conjunto de Dados CSV ou _StringFromFile),\nent\u00E3o \u00E9 particularmente importante salvar seu script de teste.\nVoc\u00EA quer salvar seu plano de teste primeiro?
 shutdown=Desligar
 simple_config_element=Elemento de Configura\u00E7\u00E3o Simples
 simple_data_writer_title=Escritor de Dados Simples
 size_assertion_comparator_error_equal=igual a
 size_assertion_comparator_error_greater=maior que
 size_assertion_comparator_error_greaterequal=maior ou igual que
 size_assertion_comparator_error_less=menor que
 size_assertion_comparator_error_lessequal=menor ou igual que
 size_assertion_comparator_error_notequal=diferente de
 size_assertion_comparator_label=Tipo de Compara\u00E7\u00E3o
 size_assertion_failure=O resultado estava com tamanho errado\: Tinha {0} bytes, mas deveria ter {1} {2} bytes
 size_assertion_input_error=Favor entrar um inteiro positivo v\u00E1lido.
 size_assertion_label=Tamanho em bytes\:
 size_assertion_size_test=Tamanho para Asser\u00E7\u00E3o
 size_assertion_title=Asser\u00E7\u00E3o de Tamanho
 soap_action=A\u00E7\u00E3o SOAP
 soap_data_title=Dados Soap/XML-RPC
 soap_sampler_title=Requisi\u00E7\u00E3o SOAP/XML-RPC
 soap_send_action=Enviar a\u00E7\u00E3o SOAP\:
 spline_visualizer_average=M\u00E9dia
 spline_visualizer_incoming=Recebidos
 spline_visualizer_maximum=M\u00E1ximo
 spline_visualizer_minimum=M\u00EDnimo
 spline_visualizer_title=Visualizador Spline
 spline_visualizer_waitingmessage=Aguardando amostras
 split_function_separator=Separador. Padr\u00E3o \u00E9 , (v\u00EDrgula)
 split_function_string=String a ser separada
 ssl_alias_prompt=Favor digitar seu apelido preferido
 ssl_alias_select=Selecione seu apelido para o teste
 ssl_alias_title=Apelido do Cliente
 ssl_error_title=Problema com Armazenamento de Chave
 ssl_pass_prompt=Favor digitar sua senha
 ssl_pass_title=Senha do Armaz\u00E9m de Chaves (KeyStore)
 ssl_port=Porta SSL
 sslmanager=Gerenciador SSL
 start=Iniciar
+start_no_timers=Iniciar sem pausas
 starttime=Tempo de In\u00EDcio
 stop=Interromper
 stopping_test=Interrompendo todos os usu\u00E1rios virtuais. Por favor, seja paciente.
 stopping_test_failed=Um ou mais usu\u00E1rios virtuais n\u00E3o pararam; veja arquivo de log.
 stopping_test_title=Interrompendo Teste
 string_from_file_file_name=Entre com o caminho completo do arquivo
 string_from_file_seq_final=N\u00FAmero de sequ\u00EAncia final do arquivo (opcional)
 string_from_file_seq_start=Numero de sequ\u00EAncia inicial do arquivo (opcional)
 summariser_title=Gerar Sum\u00E1rio de Resultados
 summary_report=Relat\u00F3rio de Sum\u00E1rio
 switch_controller_label=Valor de Sele\u00E7\u00E3o
 switch_controller_title=Controlador de Sele\u00E7\u00E3o
 table_visualizer_sample_num=Amostra \#
 table_visualizer_sample_time=Tempo da amostra (ms)
 table_visualizer_start_time=Tempo de in\u00EDcio
 table_visualizer_status=Estado
 table_visualizer_success=Sucesso
 table_visualizer_thread_name=Nome do Usu\u00E1rio Virtual
 table_visualizer_warning=Alerta
 tcp_classname=Nome da classe TCPClient\:
 tcp_config_title=Configura\u00E7\u00E3o do Testador TCP
 tcp_nodelay=Alterar "Sem Atrasos"
 tcp_port=N\u00FAmero da Porta\:
 tcp_request_data=Texto para envio
 tcp_sample_title=Testador TCP
 tcp_timeout=Tempo limite (ms)
 template_field=Modelo\:
 test=Teste
 test_action_action=A\u00E7\u00E3o
 test_action_duration=Dura\u00E7\u00E3o (ms)
 test_action_pause=Pausar
 test_action_stop=Encerrar
 test_action_stop_now=Encerrar Agora
 test_action_target=Alvo
 test_action_target_test=Todos Usu\u00E1ros Virtuais
 test_action_target_thread=Usu\u00E1rio Virtual Atual
 test_action_title=A\u00E7\u00E3o de Teste
 test_configuration=Configura\u00E7\u00E3o do Teste
 test_plan=Plano de Teste
 test_plan_classpath_browse=Adiconar diret\u00F3rio ou jar ao caminho de classes (classpath)
 testconfiguration=Configura\u00E7\u00E3o de Teste
 testplan.serialized=Executar Grupos de Usu\u00E1rios consecutivamente (ex\: executar um grupo de cada vez)
 testplan_comments=Coment\u00E1rios\:
 testt=Teste
 thread_delay_properties=Propriedades de Atraso do Usu\u00E1rio Virtual
 thread_group_title=Grupo de Usu\u00E1rios
 thread_properties=Propriedades do Usu\u00E1rio Virtual
 threadgroup=Grupo de Usu\u00E1rios
 throughput_control_bynumber_label=Total de Execu\u00E7\u00F5es
 throughput_control_bypercent_label=Percentagem de Execu\u00E7\u00F5es
 throughput_control_perthread_label=Por Usu\u00E1rio
 throughput_control_title=Controlador de Vaz\u00E3o
 throughput_control_tplabel=Vaz\u00E3o
 time_format=Formato de string para o SimpleDateFormat (opcional)
 timelim=Tempo limite
 tr=Turco
 transaction_controller_parent=Gerar amostras do pai
 transaction_controller_title=Controlador de Transa\u00E7\u00E3o
 unbind=Liberar Usu\u00E1rio Virtual
 unescape_html_string=String a ser escapada
 unescape_string=String contendo escapes de Java
 uniform_timer_delay=Limite de Atraso Constante (em ms)
 uniform_timer_memo=Adiciona um atraso aleat\u00F3rio com uma distribui\u00E7\u00E3o uniforme
 uniform_timer_range=Atraso M\u00E1ximo Aleat\u00F3rio (ms)
 uniform_timer_title=Temporizador Aleat\u00F3rio Uniforme
 update_per_iter=Atualizar uma \u00FAnica vez por itera\u00E7\u00E3o
 upload=Subir Arquivo
 upper_bound=Limite Superior
 url_config_protocol=Protocolo\:
 url_config_title=Padr\u00F5es de Requisi\u00E7\u00E3o HTTP
 url_full_config_title=Amostra de URL Completa
 url_multipart_config_title=Padr\u00F5es de Requisi\u00E7\u00E3o HTTP Multiparte
 use_keepalive=Usar Manter Ativo (KeepAlive)
 use_multipart_for_http_post=Usar multipart/form-data para HTTP POST
 use_recording_controller=Usar Controlador de Grava\u00E7\u00E3o
 user=Usu\u00E1rio
 user_defined_test=Teste Definido pelo Usu\u00E1rio
 user_defined_variables=Vari\u00E1veis Definidas Pelo Usu\u00E1rio
 user_param_mod_help_note=(N\u00E3o modifique isto. Modifique o arquivo com aquele nome no diret\u00F3rio /bin do JMeter)
 user_parameters_table=Par\u00E2metros
 user_parameters_title=Par\u00E2metros do Usu\u00E1rio
 userdn=Nome do Usu\u00E1rio
 username=Nome do Usu\u00E1rio
 userpw=Senha
 value=Valor
 var_name=Nome de Refer\u00EAncia
 variable_name_param=Nome da vari\u00E1vel (pode incluir refer\u00EAncias de vari\u00E1veis e fun\u00E7\u00F5es)
 view_graph_tree_title=Ver Gr\u00E1fico de \u00C1rvore
 view_results_assertion_error=Erro de asser\u00E7\u00E3o\:
 view_results_assertion_failure=Falha de asser\u00E7\u00E3o\:
 view_results_assertion_failure_message=Mensagem de falha de asser\u00E7\u00E3o\:
 view_results_desc=Exibe os resultados de amostragem na forma de \u00E1rvore
 view_results_error_count=Contador de Erros\:
 view_results_fields=campos\:
 view_results_in_table=Ver Resultados em Tabela
 view_results_latency=Lat\u00EAncia\:
 view_results_load_time=Tempo de Carga\:
 view_results_render_html=Renderizar HTML
 view_results_render_json=Renderizar JSON
 view_results_render_text=Exibir Texto
 view_results_render_xml=Renderizar XML
 view_results_request_headers=Cabe\u00E7alhos das Requisi\u00E7\u00F5es
 view_results_response_code=C\u00F3digo de Resposta\:
 view_results_response_headers=Cabe\u00E7alhos da Resposta\:
 view_results_response_message=Mensagem de resposta\:
 view_results_response_too_large_message=Resposta muito grande para ser exibida. Tamanho\:
+view_results_response_partial_message=In\u00EDcio da mensagem:
 view_results_sample_count=Contagem de amostras\:
 view_results_sample_start=In\u00EDcio da Amostra\:
 view_results_size_in_bytes=Tamanho em bytes\:
 view_results_tab_assertion=Resultados da asser\u00E7\u00E3o
 view_results_tab_request=Requisi\u00E7\u00E3o
 view_results_tab_response=Dados da resposta
 view_results_tab_sampler=Resultados do testador
 view_results_thread_name=Nome do Usu\u00E1rio Virtual\:
 view_results_title=Ver Resultados
 view_results_tree_title=Ver \u00C1rvore de Resultados
 warning=Alerta\!
 web_request=Requisi\u00E7\u00E3o HTTP
 web_server=Servidor Web
 web_server_client=Implementa\u00E7\u00E3o do Cliente\:
 web_server_domain=Nome do Servidor ou IP\:
 web_server_port=N\u00FAmero da Porta\:
 web_server_timeout_connect=Conectar\:
 web_server_timeout_response=Resposta\:
 web_server_timeout_title=Tempo limite (ms)
 web_testing2_title=Cliente HTTP de Requisi\u00E7\u00E3o HTTP
 web_testing_embedded_url_pattern=URLs embutidas precisam combinar\:
 web_testing_retrieve_images=Recuperar todos recursos embutidos a partir de arquivos HTML
 web_testing_title=Requisi\u00E7\u00E3o HTTP
 webservice_proxy_note=Se Usar Proxy HTTP estiver marcado, mas nenhum host ou porta s\u00E3o fornecidos, o testador
 webservice_proxy_note2=ir\u00E1 analizar as op\u00E7\u00F5es de linha de comando. Se nenhum host proxy ou portas forem providos
 webservice_proxy_note3=tamb\u00E9m, ele ir\u00E1 falhar em modo silencioso.
 webservice_proxy_port=Porta do Proxy
 webservice_sampler_title=Requisi\u00E7\u00E3o (SOAP) Requisi\u00E7\u00E3o
 webservice_soap_action=A\u00E7\u00E3o SOAP
 webservice_timeout=Tempo limite\:
 webservice_use_proxy=Usar Proxy HTTP
 while_controller_label=Condi\u00E7\u00F5e (fun\u00E7\u00E3o ou vari\u00E1vel)
 while_controller_title=Controlador de Enquanto
 workbench_title=\u00C1rea de Trabalho
 wsdl_helper_error=O WSDL \u00E9 inv\u00E1lido, favor verificar a url.
 wsdl_url_error=O WSDL estava vazio.
 xml_assertion_title=Asser\u00E7\u00E3o XML
 xml_namespace_button=Usar Espa\u00E7o de Nome (namespace)
 xml_tolerant_button=Processador de XML/HTML Tolerante
 xml_validate_button=Validar XML
 xml_whitespace_button=Ignorar espa\u00E7os em branco
 xmlschema_assertion_label=Nome do arquivo\:
 xmlschema_assertion_title=Asser\u00E7\u00E3o de Esquema de XML
 xpath_assertion_button=Validar
 xpath_assertion_check=Verificar Express\u00E3o XPath
 xpath_assertion_error=Erro com XPath
 xpath_assertion_failed=Express\u00F5es XPath Inv\u00E1lidas
 xpath_assertion_negate=Verdadeiro se nada combina
 xpath_assertion_option=Op\u00E7\u00F5es de Processamento de XML
 xpath_assertion_test=Asser\u00E7\u00E3o XPath
 xpath_assertion_tidy=Tentar e melhorar entrada
 xpath_assertion_title=Asser\u00E7\u00E3o XPath
 xpath_assertion_valid=Express\u00E3o XPath V\u00E1lida
 xpath_assertion_validation=Validar XML de acordo com DTD
 xpath_assertion_whitespace=Ignorar espa\u00E7os em branco
 xpath_expression=Express\u00F5es XPath que ser\u00E3o combinadas
 xpath_extractor_query=Consulta XPath
 xpath_extractor_title=Extractor XPath
 xpath_file_file_name=Arquivo XML de onde os valores ser\u00E3o extra\u00EDdos
 xpath_tidy_quiet=Quieto
 xpath_tidy_report_errors=Reportar erros
 xpath_tidy_show_warnings=Exibir alertas
 you_must_enter_a_valid_number=Voc\u00EA precisa entrar com um n\u00FAmero v\u00E1lido
 zh_cn=Chin\u00EAs (Simplificado)
 zh_tw=Chin\u00EAs (Tradicional)
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 0b8f5a7f9..691866d97 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,182 +1,183 @@
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
 	<author email="dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.5.2 =================== -->
 
 <h1>Version 2.5.2</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>The If Controller may cause an infinite loop if the condition is always false from the first iteration. 
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>
 JMeter versions since 2.1 failed to create a container sample when loading embedded resources.
 This has been corrected; can still revert to the Bug 51939 behaviour by setting the following property:
 <code>httpsampler.separate.container=false</code>
 </p>
 <p>
 Mirror server now uses default port 8081, was 8080 before 2.5.1.
 </p>
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 51932 - CacheManager does not handle cache-control header with any attributes after max-age</li>
 <li>Bug 51918 - GZIP compressed traffic produces errors, when multiple connections allowed</li>
 <li>Bug 51939 - Should generate new parent sample if necessary when retrieving embedded resources</li>
 <li>Bug 51942 - Synchronisation issue on CacheManager when Concurrent Download is used</li>
 <li>Bug 51957 - Concurrent get can hang if a task does not complete</li>
 <li>Bug 51925 - Calling Stop on Test leaks executor threads when concurrent download of resources is on</li>
 <li>Bug 51980 - HtmlParserHTMLParser double-counts images used in links</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 51996 - JMS Initial Context leak newly created Context when Multiple Thread enter InitialContextFactory#lookupContext at the same time</li>
 <li>Bug 51691 - Authorization does not work for JMS Publisher and JMS Subscriber</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
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
 <li>Bug 51937 - JMeter does not handle missing TestPlan entry well</li>
 <li>Bug 51988 - CSV Data Set Configuration does not resolve default delimiter for header parsing when variables field is empty</li>
 <li>Bug 52003 - View Results Tree "Scroll automatically" does not scroll properly in case nodes are expanded</li>
 <li>Bug 27112 - User Parameters should use scrollbars</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>Bug 51981 - Better support for file: protocol in HTTP sampler</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52022 - In View Results Tree rather than showing just a message if the results are to big, show as much of the result as are configured</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 52006 - Create a function RandomString to generate random Strings</li>
 <li>Bug 52016 - It would be useful to support Jexl2</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 51892 - Default mirror port should be different from default proxy port</li>
 <li>Bug 51817 - Moving variables up and down in User Defined Variables control</li>
 <li>Bug 51876 - Functionnality to search in Samplers TreeView</li>
+<li>Bug 52019 - Add menu option to Start a test ignoring Pause Timers</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>fixes to build.xml: support scripts; localise re-usable property names</li>
 <li>Bug 51923 - Counter function bug or documentation issue ? (fixed docs)</li>
 <li>Update velocity.jar to 1.7 (from 1.6.2)</li>
 <li>Bug 51954 - Generated documents include &lt;/br&gt; entries which cause extra blank lines </li>
 </ul>
 
 </section> 
 </body> 
 </document>
