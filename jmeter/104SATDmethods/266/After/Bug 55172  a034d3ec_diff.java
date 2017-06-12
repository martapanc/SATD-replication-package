diff --git a/src/core/org/apache/jmeter/gui/action/ActionRouter.java b/src/core/org/apache/jmeter/gui/action/ActionRouter.java
index bc6e06e24..4ce9326ea 100644
--- a/src/core/org/apache/jmeter/gui/action/ActionRouter.java
+++ b/src/core/org/apache/jmeter/gui/action/ActionRouter.java
@@ -1,307 +1,307 @@
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
 
 import java.awt.HeadlessException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import javax.swing.SwingUtilities;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.log.Logger;
 
 public final class ActionRouter implements ActionListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Object LOCK = new Object();
 
     private static volatile ActionRouter router;
 
     private Map<String, Set<Command>> commands = new HashMap<String, Set<Command>>();
 
     private final Map<String, HashSet<ActionListener>> preActionListeners =
         new HashMap<String, HashSet<ActionListener>>();
 
     private final Map<String, HashSet<ActionListener>> postActionListeners =
         new HashMap<String, HashSet<ActionListener>>();
 
     private ActionRouter() {
     }
 
     @Override
     public void actionPerformed(final ActionEvent e) {
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 performAction(e);
             }
 
         });
     }
 
     private void performAction(final ActionEvent e) {
         String actionCommand = e.getActionCommand();
         try {
             try {
                 GuiPackage.getInstance().updateCurrentGui();
             } catch (Exception err){
                 log.error("performAction(" + actionCommand + ") updateCurrentGui() on" + e.toString() + " caused", err);
                 JMeterUtils.reportErrorToUser("Problem updating GUI - see log file for details");
             }
             for (Command c : commands.get(actionCommand)) {
                 try {
                     preActionPerformed(c.getClass(), e);
                     c.doAction(e);
                     postActionPerformed(c.getClass(), e);
                 } catch (IllegalUserActionException err) {
                     String msg = err.getMessage();
                     if (msg == null) {
                         msg = err.toString();
                     }
                     Throwable t = err.getCause();
                     if (t != null) {
                         String cause = t.getMessage();
                         if (cause == null) {
                             cause = t.toString();
                         }
                         msg = msg + "\n" + cause;
                     }
                     JMeterUtils.reportErrorToUser(msg);
                 } catch (Exception err) {
                     log.error("Error processing "+c.toString(), err);
                 }
             }
         } catch (NullPointerException er) {
             log.error("performAction(" + actionCommand + ") " + e.toString() + " caused", er);
             JMeterUtils.reportErrorToUser("Sorry, this feature (" + actionCommand + ") not yet implemented");
         }
     }
 
     /**
      * To execute an action immediately in the current thread.
      *
      * @param e
      *            the action to execute
      */
     public void doActionNow(ActionEvent e) {
         performAction(e);
     }
 
     public Set<Command> getAction(String actionName) {
         Set<Command> set = new HashSet<Command>();
         for (Command c : commands.get(actionName)) {
             try {
                 set.add(c);
             } catch (Exception err) {
                 log.error("Could not add Command", err);
             }
         }
         return set;
     }
 
     public Command getAction(String actionName, Class<?> actionClass) {
         for (Command com : commands.get(actionName)) {
             if (com.getClass().equals(actionClass)) {
                 return com;
             }
         }
         return null;
     }
 
     public Command getAction(String actionName, String className) {
         for (Command com : commands.get(actionName)) {
             if (com.getClass().getName().equals(className)) {
                 return com;
             }
         }
         return null;
     }
 
     /**
      * Allows an ActionListener to receive notification of a command being
      * executed prior to the actual execution of the command.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener
      *            the ActionListener to receive the notifications
      */
     public void addPreActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = preActionListeners.get(action.getName());
             if (set == null) {
                 set = new HashSet<ActionListener>();
             }
             set.add(listener);
             preActionListeners.put(action.getName(), set);
         }
     }
 
     /**
      * Allows an ActionListener to be removed from receiving notifications of a
      * command being executed prior to the actual execution of the command.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener
      *            the ActionListener to receive the notifications
      */
     public void removePreActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = preActionListeners.get(action.getName());
             if (set != null) {
                 set.remove(listener);
                 preActionListeners.put(action.getName(), set);
             }
         }
     }
 
     /**
      * Allows an ActionListener to receive notification of a command being
      * executed after the command has executed.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener
      */
     public void addPostActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = postActionListeners.get(action.getName());
             if (set == null) {
                 set = new HashSet<ActionListener>();
             }
             set.add(listener);
             postActionListeners.put(action.getName(), set);
         }
     }
 
     /**
      * Allows an ActionListener to be removed from receiving notifications of a
      * command being executed after the command has executed.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.gui.action.Command.
      * @param listener
      */
     public void removePostActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = postActionListeners.get(action.getName());
             if (set != null) {
                 set.remove(listener);
                 postActionListeners.put(action.getName(), set);
             }
         }
     }
 
     protected void preActionPerformed(Class<? extends Command> action, ActionEvent e) {
         if (action != null) {
             Set<ActionListener> listenerSet = preActionListeners.get(action.getName());
             if (listenerSet != null && listenerSet.size() > 0) {
                 ActionListener[] listeners = listenerSet.toArray(new ActionListener[listenerSet.size()]);
                 for (int i = 0; i < listeners.length; i++) {
                     listeners[i].actionPerformed(e);
                 }
             }
         }
     }
 
     protected void postActionPerformed(Class<? extends Command> action, ActionEvent e) {
         if (action != null) {
             Set<ActionListener> listenerSet = postActionListeners.get(action.getName());
             if (listenerSet != null && listenerSet.size() > 0) {
                 ActionListener[] listeners = listenerSet.toArray(new ActionListener[listenerSet.size()]);
                 for (int i = 0; i < listeners.length; i++) {
                     listeners[i].actionPerformed(e);
                 }
             }
         }
     }
 
     private void populateCommandMap() {
         try {
             List<String> listClasses = ClassFinder.findClassesThatExtend(
                     JMeterUtils.getSearchPaths(), // strPathsOrJars - pathnames or jarfiles to search for classes
                     // classNames - required parent class(es) or annotations
                     new Class[] {Class.forName("org.apache.jmeter.gui.action.Command") }, // $NON-NLS-1$
                     false, // innerClasses - should we include inner classes?
                     // contains - classname should contain this string
                     // This was added in r325814 as part of changes for the reporting tool
-                    "org.apache.jmeter.gui",  // $NON-NLS-1$
-                    null, // notContains - classname should not contain this string
+                    null, // contains - classname should contain this string
+                    "org.apache.jmeter.report.gui", // $NON-NLS-1$ // notContains - classname should not contain this string
                     false); // annotations - true if classnames are annotations
             commands = new HashMap<String, Set<Command>>(listClasses.size());
             if (listClasses.isEmpty()) {
                 log.fatalError("!!!!!Uh-oh, didn't find any action handlers!!!!!");
                 throw new JMeterError("No action handlers found - check JMeterHome and libraries");
             }
             for (String strClassName : listClasses) {
                 Class<?> commandClass = Class.forName(strClassName);
                 Command command = (Command) commandClass.newInstance();
                 for (String commandName : command.getActionNames()) {
                     Set<Command> commandObjects = commands.get(commandName);
                     if (commandObjects == null) {
                         commandObjects = new HashSet<Command>();
                         commands.put(commandName, commandObjects);
                     }
                     commandObjects.add(command);
                 }
             }
         } catch (HeadlessException e){
             log.warn(e.toString());
         } catch (Exception e) {
             log.error("exception finding action handlers", e);
         }
     }
 
     /**
      * Gets the Instance attribute of the ActionRouter class
      *
      * @return The Instance value
      */
     public static ActionRouter getInstance() {
         if (router == null) {
             synchronized (LOCK) {
                 if(router == null) {
                     router = new ActionRouter();
                     router.populateCommandMap();
                 }
             }
         }
         return router;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/plugin/MenuCreator.java b/src/core/org/apache/jmeter/gui/plugin/MenuCreator.java
new file mode 100644
index 000000000..26d6d2103
--- /dev/null
+++ b/src/core/org/apache/jmeter/gui/plugin/MenuCreator.java
@@ -0,0 +1,60 @@
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
+package org.apache.jmeter.gui.plugin;
+
+import javax.swing.JMenu;
+import javax.swing.JMenuItem;
+import javax.swing.MenuElement;
+
+/**
+ * @since 2.10
+ */
+public interface MenuCreator {
+    public enum MENU_LOCATION {
+        FILE,
+        EDIT,
+        RUN,
+        OPTIONS,
+        HELP,
+        SEARCH
+    }
+    
+    /**
+     * MenuItems to be added in location menu
+     * @param location in top menu
+     * @return array of {@link JMenuItem}
+     */
+    JMenuItem[] getMenuItemsAtLocation(MENU_LOCATION location);
+
+    /**
+     * @return array of JMenu to be put as top level menu between Options and Help
+     */
+    JMenu[] getTopLevelMenus();
+
+    /**
+     * @param menu MenuElement
+     * @return true if menu was concerned by Locale change
+     */
+    boolean localeChanged(MenuElement menu);
+
+    /**
+     * Update Top Level menu on Locale Change
+     */
+    void localeChanged();
+}
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index 855fdd7d4..dbf8609fe 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,758 +1,839 @@
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
+import java.io.IOException;
+import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 
 import javax.swing.JCheckBoxMenuItem;
 import javax.swing.JComponent;
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.KeyStroke;
 import javax.swing.MenuElement;
 import javax.swing.UIManager;
 import javax.swing.UIManager.LookAndFeelInfo;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.action.LoadRecentProject;
+import org.apache.jmeter.gui.plugin.MenuCreator;
+import org.apache.jmeter.gui.plugin.MenuCreator.MENU_LOCATION;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public class JMeterMenuBar extends JMenuBar implements LocaleChangeListener {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private JMenu fileMenu;
 
     private JMenuItem file_save_as;
 
     private JMenuItem file_selection_as;
 
     private JMenuItem file_revert;
 
     private JMenuItem file_load;
 
     private JMenuItem create_from_template;
 
     private List<JComponent> file_load_recent_files;
 
     private JMenuItem file_merge;
 
     private JMenuItem file_exit;
 
     private JMenuItem file_close;
 
     private JMenu editMenu;
 
     private JMenu edit_add;
 
     private JMenu runMenu;
 
     private JMenuItem run_start;
 
     private JMenuItem run_start_no_timers;
 
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
 
+    private ArrayList<MenuCreator> menuCreators;
+
     public static final String SYSTEM_LAF = "System"; // $NON-NLS-1$
 
     public static final String CROSS_PLATFORM_LAF = "CrossPlatform"; // $NON-NLS-1$
 
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
         setEditEnabled(enabled);
     }
 
     /**
      * Creates the MenuBar for this application. I believe in my heart that this
      * should be defined in a file somewhere, but that is for later.
      */
     public void createMenuBar() {
+        this.menuCreators = new ArrayList<MenuCreator>();
+        try {
+            List<String> listClasses = ClassFinder.findClassesThatExtend(
+                    JMeterUtils.getSearchPaths(), 
+                    new Class[] {MenuCreator.class }); 
+            for (String strClassName : listClasses) {
+                try {
+                    if(log.isDebugEnabled()) {
+                        log.debug("Loading menu creator class: "+ strClassName);
+                    }
+                    Class<?> commandClass = Class.forName(strClassName);
+                    if (!Modifier.isAbstract(commandClass.getModifiers())) {
+                        if(log.isDebugEnabled()) {
+                            log.debug("Instantiating: "+ commandClass.getName());
+                        }
+                        MenuCreator creator = (MenuCreator) commandClass.newInstance();
+                        menuCreators.add(creator);                  
+                    }
+                } catch (Exception e) {
+                    log.error("Exception registering "+MenuCreator.class.getName() + " with implementation:"+strClassName, e);
+                }
+            }
+        } catch (IOException e) {
+            log.error("Exception finding implementations of "+MenuCreator.class, e);
+        }
+
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
+        for (Iterator iterator = menuCreators.iterator(); iterator.hasNext();) {
+            MenuCreator menuCreator = (MenuCreator) iterator.next();
+            JMenu[] topLevelMenus = menuCreator.getTopLevelMenus();
+            for (JMenu topLevelMenu : topLevelMenus) {
+                this.add(topLevelMenu);                
+            }
+        }
         this.add(helpMenu);
     }
 
     private void makeHelpMenu() {
         // HELP MENU
         helpMenu = makeMenuRes("help",'H'); //$NON-NLS-1$
 
         JMenuItem contextHelp = makeMenuItemRes("help", 'H', ActionNames.HELP, KeyStrokes.HELP); //$NON-NLS-1$
 
         JMenuItem whatClass = makeMenuItemRes("help_node", 'W', ActionNames.WHAT_CLASS, KeyStrokes.WHAT_CLASS);//$NON-NLS-1$
 
         JMenuItem setDebug = makeMenuItemRes("debug_on", ActionNames.DEBUG_ON, KeyStrokes.DEBUG_ON);//$NON-NLS-1$
 
         JMenuItem resetDebug = makeMenuItemRes("debug_off", ActionNames.DEBUG_OFF, KeyStrokes.DEBUG_OFF);//$NON-NLS-1$
 
         JMenuItem heapDump = makeMenuItemRes("heap_dump", ActionNames.HEAP_DUMP);//$NON-NLS-1$
 
         help_about = makeMenuItemRes("about", 'A', ActionNames.ABOUT); //$NON-NLS-1$
 
         helpMenu.add(contextHelp);
         helpMenu.addSeparator();
         helpMenu.add(whatClass);
         helpMenu.add(setDebug);
         helpMenu.add(resetDebug);
         helpMenu.add(heapDump);
+
+        addPluginsMenuItems(helpMenu, menuCreators, MENU_LOCATION.HELP);
+
         helpMenu.addSeparator();
         helpMenu.add(help_about);
     }
 
     private void makeOptionsMenu() {
         // OPTIONS MENU
         optionsMenu = makeMenuRes("option",'O'); //$NON-NLS-1$
         JMenuItem functionHelper = makeMenuItemRes("function_dialog_menu_item", 'F', ActionNames.FUNCTIONS, KeyStrokes.FUNCTIONS); //$NON-NLS-1$
 
         lafMenu = makeMenuRes("appearance",'L'); //$NON-NLS-1$
         UIManager.LookAndFeelInfo lafs[] = getAllLAFs();
         for (int i = 0; i < lafs.length; ++i) {
             JMenuItem laf = new JMenuItem(lafs[i].getName());
             laf.addActionListener(ActionRouter.getInstance());
             laf.setActionCommand(ActionNames.LAF_PREFIX + lafs[i].getClassName());
             laf.setToolTipText(lafs[i].getClassName()); // show the classname to the user
             lafMenu.add(laf);
         }
         optionsMenu.add(functionHelper);
         optionsMenu.add(lafMenu);
 
         JCheckBoxMenuItem menuToolBar = makeCheckBoxMenuItemRes("menu_toolbar", ActionNames.TOOLBAR); //$NON-NLS-1$
         JCheckBoxMenuItem menuLoggerPanel = makeCheckBoxMenuItemRes("menu_logger_panel", ActionNames.LOGGER_PANEL_ENABLE_DISABLE); //$NON-NLS-1$
         GuiPackage guiInstance = GuiPackage.getInstance();
         if (guiInstance != null) { //avoid error in ant task tests (good way?)
             guiInstance.setMenuItemToolbar(menuToolBar);
             guiInstance.setMenuItemLoggerPanel(menuLoggerPanel);
         }
         optionsMenu.add(menuToolBar);
         optionsMenu.add(menuLoggerPanel);
         
         if (SSLManager.isSSLSupported()) {
             sslManager = makeMenuItemRes("sslmanager", 'S', ActionNames.SSL_MANAGER, KeyStrokes.SSL_MANAGER); //$NON-NLS-1$
             optionsMenu.add(sslManager);
         }
         optionsMenu.add(makeLanguageMenu());
 
         JMenuItem collapse = makeMenuItemRes("menu_collapse_all", ActionNames.COLLAPSE_ALL, KeyStrokes.COLLAPSE_ALL); //$NON-NLS-1$
         optionsMenu.add(collapse);
 
         JMenuItem expand = makeMenuItemRes("menu_expand_all", ActionNames.EXPAND_ALL, KeyStrokes.EXPAND_ALL); //$NON-NLS-1$
         optionsMenu.add(expand);
+
+        addPluginsMenuItems(optionsMenu, menuCreators, MENU_LOCATION.OPTIONS);
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
             for(String newLang : addLanguages){
                 log.info("Adding locale "+newLang);
                 lang.add(newLang);
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
 
         for(String lang : getLanguages()){
             langMenu.addLang(lang);
         }
         return languageMenu;
     }
 
     private void makeRunMenu() {
         // RUN MENU
         runMenu = makeMenuRes("run",'R'); //$NON-NLS-1$
 
         run_start = makeMenuItemRes("start", 'S', ActionNames.ACTION_START, KeyStrokes.ACTION_START); //$NON-NLS-1$
 
         run_start_no_timers = makeMenuItemRes("start_no_timers", ActionNames.ACTION_START_NO_TIMERS); //$NON-NLS-1$
         
         run_stop = makeMenuItemRes("stop", 'T', ActionNames.ACTION_STOP, KeyStrokes.ACTION_STOP); //$NON-NLS-1$
         run_stop.setEnabled(false);
 
         run_shut = makeMenuItemRes("shutdown", 'Y', ActionNames.ACTION_SHUTDOWN, KeyStrokes.ACTION_SHUTDOWN); //$NON-NLS-1$
         run_shut.setEnabled(false);
 
         run_clear = makeMenuItemRes("clear", 'C', ActionNames.CLEAR, KeyStrokes.CLEAR); //$NON-NLS-1$
 
         run_clearAll = makeMenuItemRes("clear_all", 'a', ActionNames.CLEAR_ALL, KeyStrokes.CLEAR_ALL); //$NON-NLS-1$
 
         runMenu.add(run_start);
         runMenu.add(run_start_no_timers);
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
+
+        addPluginsMenuItems(runMenu, menuCreators, MENU_LOCATION.RUN);
     }
 
     private void makeEditMenu() {
         // EDIT MENU
         editMenu = makeMenuRes("edit",'E'); //$NON-NLS-1$
 
         // From the Java Look and Feel Guidelines: If all items in a menu
         // are disabled, then disable the menu. Makes sense.
         editMenu.setEnabled(false);
+
+        addPluginsMenuItems(editMenu, menuCreators, MENU_LOCATION.EDIT);
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
 
         create_from_template = makeMenuItemRes("create_from_template", 'T', ActionNames.CREATE_FROM_TEMPLATE); //$NON-NLS-1$
         create_from_template.setEnabled(true);
 
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
         fileMenu.add(create_from_template);
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
         for(JComponent jc : file_load_recent_files){
             fileMenu.add(jc);
         }
+
+        addPluginsMenuItems(fileMenu, menuCreators, MENU_LOCATION.FILE);
+
         fileMenu.add(file_exit);
     }
 
     private void makeSearchMenu() {
         // Search MENU
         searchMenu = makeMenuRes("menu_search"); //$NON-NLS-1$
 
         JMenuItem search = makeMenuItemRes("menu_search", 'F', ActionNames.SEARCH_TREE, KeyStrokes.SEARCH_TREE); //$NON-NLS-1$
         searchMenu.add(search);
-        searchMenu.setEnabled(true);
+        search.setEnabled(true);
 
         JMenuItem searchReset = makeMenuItemRes("menu_search_reset", ActionNames.SEARCH_RESET); //$NON-NLS-1$
         searchMenu.add(searchReset);
-        searchMenu.setEnabled(true);
+        searchReset.setEnabled(true);
+
+        addPluginsMenuItems(searchMenu, menuCreators, MENU_LOCATION.SEARCH);
+    }
+
+    /**
+     * @param menu 
+     * @param menuCreators
+     * @param location
+     */
+    protected void addPluginsMenuItems(JMenu menu, List<MenuCreator> menuCreators, MENU_LOCATION location) {
+        boolean addedSeparator = false;
+        for (MenuCreator menuCreator : menuCreators) {
+            JMenuItem[] menuItems = menuCreator.getMenuItemsAtLocation(location);
+            for (JMenuItem jMenuItem : menuItems) {
+                if(!addedSeparator) {
+                    menu.addSeparator();
+                    addedSeparator = true;
+                }
+                menu.add(jMenuItem);
+            }
+        }
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
         run_start_no_timers.setEnabled(!enable);
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
     @Override
     public void localeChanged(LocaleChangeEvent event) {
         updateMenuElement(fileMenu);
         updateMenuElement(editMenu);
         updateMenuElement(searchMenu);
         updateMenuElement(runMenu);
         updateMenuElement(optionsMenu);
         updateMenuElement(helpMenu);
+        for (MenuCreator creator : menuCreators) {
+            creator.localeChanged();
+        }
     }
 
     /**
      * Get a list of all installed LAFs plus CrossPlatform and System.
      */
     // This is also used by LookAndFeelCommand
     public static LookAndFeelInfo[] getAllLAFs() {
         UIManager.LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
         int i = lafs.length;
         UIManager.LookAndFeelInfo lafsAll[] = new UIManager.LookAndFeelInfo[i+2];
         System.arraycopy(lafs, 0, lafsAll, 0, i);
         lafsAll[i++]=new UIManager.LookAndFeelInfo(CROSS_PLATFORM_LAF,UIManager.getCrossPlatformLookAndFeelClassName());
         lafsAll[i++]=new UIManager.LookAndFeelInfo(SYSTEM_LAF,UIManager.getSystemLookAndFeelClassName());
         return lafsAll;
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
+            for (MenuCreator menuCreator : menuCreators) {
+                if(menuCreator.localeChanged(menu)) {
+                    return;
+                }
+            }
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
     
     private static JCheckBoxMenuItem makeCheckBoxMenuItemRes(String resource, String actionCommand) {
         return makeCheckBoxMenuItemRes(resource, actionCommand, null);
     }
 
     private static JCheckBoxMenuItem makeCheckBoxMenuItemRes(String resource, 
             String actionCommand, KeyStroke keyStroke){
         JCheckBoxMenuItem cbkMenuItem = new JCheckBoxMenuItem(JMeterUtils.getResString(resource));
         cbkMenuItem.setName(resource);
         cbkMenuItem.setActionCommand(actionCommand);
         cbkMenuItem.setAccelerator(keyStroke);
         cbkMenuItem.addActionListener(ActionRouter.getInstance());
         return cbkMenuItem;
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 41c261cfd..aa776b7c1 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,251 +1,252 @@
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
 
 
 <!--  =================== 2.10 =================== -->
 
 <h1>Version 2.10</h1>
 
 <h2>New and Noteworthy</h2>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <p>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </p>
 
 <p>
 Changing language can break part of the configuration of the following elements (see <bugzilla>53679</bugzilla>):
 <ul>
     <li>CSV Data Set Config (sharing mode will be lost)</li>
     <li>Constant Throughput Timer (Calculate throughput based on will be lost)</li>
 </ul>
 </p>
 
 <p>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
 </p>
 
 <p>
 Note that there is a bug in Java on some Linux systems that manifests
 itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation.
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>SMTP Sampler now uses eml file subject if subject field is empty</p>
 
 <p>With this version autoFlush has been turned off on PrintWriter in charge of writing test results. 
 This results in improved throughput for intensive tests but can result in more test data loss in case
 of JMeter crash (very rare). To revert to previous behaviour set jmeter.save.saveservice.autoflush property to true. </p>
 
 <p>
 Shortcut for Function Helper Dialog is now CTRL+SHIFT+F1 (CMD + SHIFT + F1 for Mac OS).
 The original key sequence (Ctrl+F1) did not work in some locations (it is consumed by the Java Swing ToolTipManager).
 It was therefore necessary to change the shortcut.
 </p>
 
 <p>
 Webservice (SOAP) Request has been removed by default from GUI as Element is deprecated (use HTTP Sampler with Raw Post body), if you need to show it, see property not_in_menu in jmeter.properties
 </p>
 
 <p>
 Transaction Controller now sets Response Code of Generated Parent Sampler (if Generate Parent Sampler is checked) to response code of first failing child in case of failure of one of the children, in previous versions Response Code was empty.
 </p>
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>54627</bugzilla> - JMeter Proxy GUI: Type of sampler settings takes the whole screen with when there are samplers with long name</li>
 <li><bugzilla>54629</bugzilla> - HTMLParser does not extract &lt;object&gt; tag urls</li>
 <li><bugzilla>55023</bugzilla> - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput</li>
 <li><bugzilla>55092</bugzilla> - Log message "WARN - jmeter.protocol.http.sampler.HTTPSamplerBase: Null URL detected (should not happen)" displayed when embedded resource URL is malformed</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>54913</bugzilla> - JMSPublisherGui incorrectly restore its state</li>
 <li><bugzilla>55027</bugzilla> - Test Action regression, duration value are not record (nightly build)</li>
 <li><bugzilla>55163</bugzilla> - BeanShellTestElement faiils to quote string when calling testStarted(String)/testEnded(String)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54467</bugzilla> - Loop controller Controller check conditions each request</li>
 <li><bugzilla>54985</bugzilla> - Make Transaction Controller set Response Code of Generated Parent Sampler to response code of first failing child in case of failure of one of its children</li>
 <li><bugzilla>54950</bugzilla> - ModuleController : Changes to referenced Module are not taken into account if changes occur after first run and referenced node is disabled</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54589</bugzilla> - View Results Tree have a lot of Garbage characters if html page uses double-byte charset</li>
 <li><bugzilla>54753</bugzilla> - StringIndexOutOfBoundsException at SampleResult.getSampleLabel() if key_on_threadname=false when using Statistical mode</li>
 <li><bugzilla>54865</bugzilla> - ArrayIndexOutOfBoundsException if "sample_variable" is set in client but not server</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54540</bugzilla> - "HTML Parameter Mask" are not marked deprecated in the IHM</li>
 <li><bugzilla>54575</bugzilla> - CSS/JQuery Extractor : Choosing JODD Implementation always uses JSOUP</li>
 <li><bugzilla>54901</bugzilla> - Response Assertion GUI behaves weirdly</li>
 <li><bugzilla>54924</bugzilla> - XMLAssertion uses JMeter JVM file.encoding instead of response encoding and does not clean threadlocal variable</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54504</bugzilla> - Resource string not found: [clipboard_node_read_error]</li>
 <li><bugzilla>54538</bugzilla> - GUI: context menu is too big</li>
 <li><bugzilla>54847</bugzilla> - Cut &amp; Paste is broken with tree multi-selection</li>
 <li><bugzilla>54870</bugzilla> - Tree D&amp;D may lost leaf nodes (affected nightly build)</li>
 <li><bugzilla>55056</bugzilla> - wasted work in Data.append()</li>
 <li><bugzilla>55129</bugzilla> -  Change Javadoc generation per CVE-2013-1571, VU#225657</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>HTTP Request: Small user interaction improvements in Row parameter Detail Box</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>54788</bugzilla> - JMS Point-to-Point Sampler - GUI enhancements to increase readability and ease of use</li>
 <li><bugzilla>54798</bugzilla> - Using subject from EML-file for SMTP Sampler</li>
 <li><bugzilla>54759</bugzilla> - SSLPeerUnverifiedException using HTTPS , property documented</li>
 <li><bugzilla>54896</bugzilla> - JUnit sampler gives only “failed to create an instance of the class” message with constructor problems</li>
 <li><bugzilla>55084</bugzilla> - Add timeout support for JDBC Request</li>
 <li><bugzilla>55161</bugzilla> - Useless processing in SoapSampler.setPostHeaders</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54532</bugzilla> - Improve Response Time Graph Y axis scale with huge values or small values (&lt; 1000ms). Add a new field to define increment scale</li>
 <li><bugzilla>54576</bugzilla> - View Results Tree : Add a CSS/JQuery Tester</li>
 <li><bugzilla>54777</bugzilla> - Improve Performance of default ResultCollector</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>54991</bugzilla> - Add functions to encode/decode URL encoded chars (__urlencode and __urldecode)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54584</bugzilla> - MongoDB plugin</li>
 <li><bugzilla>54669</bugzilla> - Add flag forcing non-GUI JVM to exit after test</li>
 <li><bugzilla>42428</bugzilla> - Workbench not saved with Test Plan</li>
 <li><bugzilla>54825</bugzilla> - Add shortcuts to move elements in the tree</li>
 <li><bugzilla>54834</bugzilla> - Improve Drag &amp; Drop in the jmeter tree</li>
 <li><bugzilla>54839</bugzilla> - Set the application name on Mac</li>
 <li><bugzilla>54841</bugzilla> - Correctly handle the quit shortcut on Mac Os (CMD-Q)</li>
 <li><bugzilla>54844</bugzilla> - Set the application icon on Mac Os</li>
 <li><bugzilla>54834</bugzilla> - Improve Drag &amp; Drop in the jmeter tree</li>
 <li><bugzilla>54864</bugzilla> - Enable multi selection drag &amp; drop in the tree without having to start dragging before releasing Shift or Control </li>
 <li><bugzilla>54945</bugzilla> - Add Shutdown Hook to enable trapping kill or CTRL+C signals</li>
 <li><bugzilla>54990</bugzilla> - Download large files avoiding outOfMemory</li>
 <li><bugzilla>55085</bugzilla> - UX Improvement : Ability to create New Test Plan from Templates</li>
+<li><bugzilla>55172</bugzilla> - Provide plugins a way to add Top Menu and menu items</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Updated to jsoup-1.7.2</li>
 <li><bugzilla>54776</bugzilla> - Update the dependency on Bouncy Castle to 1.48</li>
 <li>Updated to commons-logging 1.1.2 (from 1.1.1)</li>
 <li>Updated to HttpComponents Client 4.2.5 (from 4.2.3)</li>
 <li>Updated to HttpComponents Core 4.2.4 (from 4.2.3)</li>
 <li>Updated to commons-codec 1.8 (from 1.6)</li>
 <li>Updated to commons-io 2.4 (from 2.2)</li>
 <li>Updated to commons-logging 1.1.3 (from 1.1.1)</li>
 <li>Updated to commons-net 3.3 (from 3.1)</li>
 <li>Updated to jdom-1.1.3 (from 1.1.2)</li>
 <li>Updated to jodd-lagarto and jodd-core 3.4.4 (from 3.4.1)</li>
 <li>Updated to junit 4.11 (from 4.10)</li>
 <li>Updated to slf4j-api 1.7.5 (from 1.7.2)</li>
 <li>Updated to xmlgraphics-commons 1.5 (from 1.3.1)</li>
 <li>Updated to xstream 1.4.4 (from 1.4.2)</li>
 <li><bugzilla>54912</bugzilla> - JMeterTreeListener should use constants</li>
 <li><bugzilla>55065</bugzilla> - Useless processing in Spline3.converge()</li>
 <li><bugzilla>55064</bugzilla> - Useless processing in ReportTreeListener.isValidDragAction()</li>
 <li><bugzilla>54903</bugzilla> - Remove the dependency on the Activation Framework</li>
 </ul>
 
 </section> 
 </body> 
 </document>
