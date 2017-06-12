diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index 3d2ebd826..813ceca9b 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,836 +1,836 @@
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
 import java.io.IOException;
 import java.lang.reflect.Modifier;
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
 import org.apache.jmeter.gui.plugin.MenuCreator;
 import org.apache.jmeter.gui.plugin.MenuCreator.MENU_LOCATION;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public class JMeterMenuBar extends JMenuBar implements LocaleChangeListener {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private JMenu fileMenu;
 
     private JMenuItem fileSaveAs;
 
     private JMenuItem fileSelectionAs;
 
     private JMenuItem fileSelectionAsTestFragment;
 
     private JMenuItem fileRevert;
 
     private JMenuItem fileLoad;
 
     private JMenuItem templates;
 
     private List<JComponent> fileLoadRecentFiles;
 
     private JMenuItem fileMerge;
 
     private JMenuItem fileExit;
 
-    private JMenuItem fileClose;
+    private JMenuItem fileNew;
 
     private JMenu editMenu;
 
     private JMenu editAdd;
 
     private JMenu runMenu;
 
     private JMenuItem runStart;
 
     private JMenuItem runStartNoTimers;
 
     private JMenu remoteStart;
 
     private JMenuItem remoteStartAll;
 
     private Collection<JMenuItem> remoteEngineStart;
 
     private JMenuItem runStop;
 
     private JMenuItem runShut;
 
     private JMenu remoteStop;
 
     private JMenu remoteShut;
 
     private JMenuItem remoteStopAll;
 
     private JMenuItem remoteShutAll;
 
     private Collection<JMenuItem> remoteEngineStop;
 
     private Collection<JMenuItem> remoteEngineShut;
 
     private JMenuItem runClear;
 
     private JMenuItem runClearAll;
 
     private JMenu optionsMenu;
 
     private JMenu lafMenu;
 
     private JMenuItem sslManager;
 
     private JMenu helpMenu;
 
     private JMenuItem helpAbout;
 
     private String[] remoteHosts;
 
     private JMenu remoteExit;
 
     private JMenuItem remoteExitAll;
 
     private Collection<JMenuItem> remoteEngineExit;
 
     private JMenu searchMenu;
 
     private ArrayList<MenuCreator> menuCreators;
 
     public static final String SYSTEM_LAF = "System"; // $NON-NLS-1$
 
     public static final String CROSS_PLATFORM_LAF = "CrossPlatform"; // $NON-NLS-1$
 
     public JMeterMenuBar() {
         // List for recent files menu items
         fileLoadRecentFiles = new LinkedList<>();
         // Lists for remote engines menu items
         remoteEngineStart = new LinkedList<>();
         remoteEngineStop = new LinkedList<>();
         remoteEngineShut = new LinkedList<>();
         remoteEngineExit = new LinkedList<>();
         remoteHosts = JOrphanUtils.split(JMeterUtils.getPropDefault("remote_hosts", ""), ","); //$NON-NLS-1$
         if (remoteHosts.length == 1 && remoteHosts[0].isEmpty()) {
             remoteHosts = new String[0];
         }
         this.getRemoteItems();
         createMenuBar();
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     public void setFileSaveEnabled(boolean enabled) {
         if(fileSaveAs != null) {
             fileSaveAs.setEnabled(enabled);
         }
     }
 
     public void setFileLoadEnabled(boolean enabled) {
         if (fileLoad != null) {
             fileLoad.setEnabled(enabled);
         }
         if (fileMerge != null) {
             fileMerge.setEnabled(enabled);
         }
     }
 
     public void setFileRevertEnabled(boolean enabled) {
         if(fileRevert != null) {
             fileRevert.setEnabled(enabled);
         }
     }
 
     public void setProjectFileLoaded(String file) {
         if(fileLoadRecentFiles != null && file != null) {
             LoadRecentProject.updateRecentFileMenuItems(fileLoadRecentFiles, file);
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
         if (editAdd != null) {
             editMenu.remove(editAdd);
         }
         // Insert the Add menu as the first menu item in the Edit menu.
         editAdd = menu;
         editMenu.insert(editAdd, 0);
     }
 
     // Called by MainFrame#setEditMenu() which is called by EditCommand#doAction and GuiPackage#localeChanged
     public void setEditMenu(JPopupMenu menu) {
         if (menu != null) {
             editMenu.removeAll();
             Component[] comps = menu.getComponents();
             for (Component comp : comps) {
                 editMenu.add(comp);
             }
             editMenu.setEnabled(true);
         } else {
             editMenu.setEnabled(false);
         }
     }
 
     public void setEditAddEnabled(boolean enabled) {
         // There was a NPE being thrown without the null check here.. JKB
         if (editAdd != null) {
             editAdd.setEnabled(enabled);
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
         this.menuCreators = new ArrayList<>();
         try {
             List<String> listClasses = ClassFinder.findClassesThatExtend(
                     JMeterUtils.getSearchPaths(),
                     new Class[] {MenuCreator.class });
             for (String strClassName : listClasses) {
                 try {
                     if(log.isDebugEnabled()) {
                         log.debug("Loading menu creator class: "+ strClassName);
                     }
                     Class<?> commandClass = Class.forName(strClassName);
                     if (!Modifier.isAbstract(commandClass.getModifiers())) {
                         if(log.isDebugEnabled()) {
                             log.debug("Instantiating: "+ commandClass.getName());
                         }
                         MenuCreator creator = (MenuCreator) commandClass.newInstance();
                         menuCreators.add(creator);
                     }
                 } catch (Exception e) {
                     log.error("Exception registering "+MenuCreator.class.getName() + " with implementation:"+strClassName, e);
                 }
             }
         } catch (IOException e) {
             log.error("Exception finding implementations of "+MenuCreator.class, e);
         }
 
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
         for (MenuCreator menuCreator : menuCreators) {
             JMenu[] topLevelMenus = menuCreator.getTopLevelMenus();
             for (JMenu topLevelMenu : topLevelMenus) {
                 this.add(topLevelMenu);
             }
         }
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
 
         helpAbout = makeMenuItemRes("about", 'A', ActionNames.ABOUT); //$NON-NLS-1$
 
         helpMenu.add(contextHelp);
         helpMenu.addSeparator();
         helpMenu.add(whatClass);
         helpMenu.add(setDebug);
         helpMenu.add(resetDebug);
         helpMenu.add(heapDump);
 
         addPluginsMenuItems(helpMenu, menuCreators, MENU_LOCATION.HELP);
 
         helpMenu.addSeparator();
         helpMenu.add(helpAbout);
     }
 
     private void makeOptionsMenu() {
         // OPTIONS MENU
         optionsMenu = makeMenuRes("option",'O'); //$NON-NLS-1$
         JMenuItem functionHelper = makeMenuItemRes("function_dialog_menu_item", 'F', ActionNames.FUNCTIONS, KeyStrokes.FUNCTIONS); //$NON-NLS-1$
 
         lafMenu = makeMenuRes("appearance",'L'); //$NON-NLS-1$
         for (LookAndFeelInfo laf : getAllLAFs()) {
             JMenuItem menuItem = new JMenuItem(laf.getName());
             menuItem.addActionListener(ActionRouter.getInstance());
             menuItem.setActionCommand(ActionNames.LAF_PREFIX + laf.getClassName());
             menuItem.setToolTipText(laf.getClassName()); // show the classname to the user
             lafMenu.add(menuItem);
         }
         optionsMenu.add(functionHelper);
         optionsMenu.add(lafMenu);
 
         JCheckBoxMenuItem menuLoggerPanel = makeCheckBoxMenuItemRes("menu_logger_panel", ActionNames.LOGGER_PANEL_ENABLE_DISABLE); //$NON-NLS-1$
         GuiPackage guiInstance = GuiPackage.getInstance();
         if (guiInstance != null) { //avoid error in ant task tests (good way?)
             guiInstance.setMenuItemLoggerPanel(menuLoggerPanel);
         }
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
 
         addPluginsMenuItems(optionsMenu, menuCreators, MENU_LOCATION.OPTIONS);
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
         List<String> lang = new ArrayList<>(20);
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
 
         runStart = makeMenuItemRes("start", 'S', ActionNames.ACTION_START, KeyStrokes.ACTION_START); //$NON-NLS-1$
 
         runStartNoTimers = makeMenuItemRes("start_no_timers", ActionNames.ACTION_START_NO_TIMERS); //$NON-NLS-1$
         
         runStop = makeMenuItemRes("stop", 'T', ActionNames.ACTION_STOP, KeyStrokes.ACTION_STOP); //$NON-NLS-1$
         runStop.setEnabled(false);
 
         runShut = makeMenuItemRes("shutdown", 'Y', ActionNames.ACTION_SHUTDOWN, KeyStrokes.ACTION_SHUTDOWN); //$NON-NLS-1$
         runShut.setEnabled(false);
 
         runClear = makeMenuItemRes("clear", 'C', ActionNames.CLEAR, KeyStrokes.CLEAR); //$NON-NLS-1$
 
         runClearAll = makeMenuItemRes("clear_all", 'a', ActionNames.CLEAR_ALL, KeyStrokes.CLEAR_ALL); //$NON-NLS-1$
 
         runMenu.add(runStart);
         runMenu.add(runStartNoTimers);
         if (remoteStart != null) {
             runMenu.add(remoteStart);
         }
         remoteStartAll = makeMenuItemRes("remote_start_all", ActionNames.REMOTE_START_ALL, KeyStrokes.REMOTE_START_ALL); //$NON-NLS-1$
 
         runMenu.add(remoteStartAll);
         runMenu.add(runStop);
         runMenu.add(runShut);
         if (remoteStop != null) {
             runMenu.add(remoteStop);
         }
         remoteStopAll = makeMenuItemRes("remote_stop_all", 'X', ActionNames.REMOTE_STOP_ALL, KeyStrokes.REMOTE_STOP_ALL); //$NON-NLS-1$
         runMenu.add(remoteStopAll);
 
         if (remoteShut != null) {
             runMenu.add(remoteShut);
         }
         remoteShutAll = makeMenuItemRes("remote_shut_all", 'X', ActionNames.REMOTE_SHUT_ALL, KeyStrokes.REMOTE_SHUT_ALL); //$NON-NLS-1$
         runMenu.add(remoteShutAll);
 
         if (remoteExit != null) {
             runMenu.add(remoteExit);
         }
         remoteExitAll = makeMenuItemRes("remote_exit_all", ActionNames.REMOTE_EXIT_ALL); //$NON-NLS-1$
         runMenu.add(remoteExitAll);
 
         runMenu.addSeparator();
         runMenu.add(runClear);
         runMenu.add(runClearAll);
 
         addPluginsMenuItems(runMenu, menuCreators, MENU_LOCATION.RUN);
     }
 
     private void makeEditMenu() {
         // EDIT MENU
         editMenu = makeMenuRes("edit",'E'); //$NON-NLS-1$
 
         // From the Java Look and Feel Guidelines: If all items in a menu
         // are disabled, then disable the menu. Makes sense.
         editMenu.setEnabled(false);
 
         addPluginsMenuItems(editMenu, menuCreators, MENU_LOCATION.EDIT);
     }
 
     private void makeFileMenu() {
         // FILE MENU
         fileMenu = makeMenuRes("file",'F'); //$NON-NLS-1$
 
         JMenuItem fileSave = makeMenuItemRes("save", 'S', ActionNames.SAVE, KeyStrokes.SAVE); //$NON-NLS-1$
         fileSave.setEnabled(true);
 
         fileSaveAs = makeMenuItemRes("save_all_as", 'A', ActionNames.SAVE_ALL_AS, KeyStrokes.SAVE_ALL_AS); //$NON-NLS-1$
         fileSaveAs.setEnabled(true);
 
         fileSelectionAs = makeMenuItemRes("save_as", ActionNames.SAVE_AS); //$NON-NLS-1$
         fileSelectionAs.setEnabled(true);
 
         fileSelectionAsTestFragment = makeMenuItemRes("save_as_test_fragment", ActionNames.SAVE_AS_TEST_FRAGMENT); //$NON-NLS-1$
         fileSelectionAsTestFragment.setEnabled(true);
 
         fileRevert = makeMenuItemRes("revert_project", 'R', ActionNames.REVERT_PROJECT); //$NON-NLS-1$
         fileRevert.setEnabled(false);
 
         fileLoad = makeMenuItemRes("menu_open", 'O', ActionNames.OPEN, KeyStrokes.OPEN); //$NON-NLS-1$
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         fileLoad.setEnabled(false);
 
         templates = makeMenuItemRes("template_menu", 'T', ActionNames.TEMPLATES); //$NON-NLS-1$
         templates.setEnabled(true);
 
-        fileClose = makeMenuItemRes("menu_close", 'C', ActionNames.CLOSE, KeyStrokes.CLOSE); //$NON-NLS-1$
+        fileNew = makeMenuItemRes("new", 'N', ActionNames.CLOSE, KeyStrokes.CLOSE); //$NON-NLS-1$
 
         fileExit = makeMenuItemRes("exit", 'X', ActionNames.EXIT, KeyStrokes.EXIT); //$NON-NLS-1$
 
         fileMerge = makeMenuItemRes("menu_merge", 'M', ActionNames.MERGE); //$NON-NLS-1$
         fileMerge.setEnabled(false);
 
-        fileMenu.add(fileClose);
-        fileMenu.add(fileLoad);
+        fileMenu.add(fileNew);
         fileMenu.add(templates);
+        fileMenu.add(fileLoad);
         fileMenu.add(fileMerge);
         fileMenu.addSeparator();
         fileMenu.add(fileSave);
         fileMenu.add(fileSaveAs);
         fileMenu.add(fileSelectionAs);
         fileMenu.add(fileSelectionAsTestFragment);
         fileMenu.add(fileRevert);
         fileMenu.addSeparator();
         // Add the recent files, which will also add a separator that is
         // visible when needed
         fileLoadRecentFiles = LoadRecentProject.getRecentFileMenuItems();
         for(JComponent jc : fileLoadRecentFiles){
             fileMenu.add(jc);
         }
 
         addPluginsMenuItems(fileMenu, menuCreators, MENU_LOCATION.FILE);
 
         fileMenu.add(fileExit);
     }
 
     private void makeSearchMenu() {
         // Search MENU
         searchMenu = makeMenuRes("menu_search"); //$NON-NLS-1$
 
         JMenuItem search = makeMenuItemRes("menu_search", 'F', ActionNames.SEARCH_TREE, KeyStrokes.SEARCH_TREE); //$NON-NLS-1$
         searchMenu.add(search);
         search.setEnabled(true);
 
         JMenuItem searchReset = makeMenuItemRes("menu_search_reset", ActionNames.SEARCH_RESET); //$NON-NLS-1$
         searchMenu.add(searchReset);
         searchReset.setEnabled(true);
 
         addPluginsMenuItems(searchMenu, menuCreators, MENU_LOCATION.SEARCH);
     }
 
     /**
      * @param menu
      * @param menuCreators
      * @param location
      */
     private void addPluginsMenuItems(JMenu menu, List<MenuCreator> menuCreators, MENU_LOCATION location) {
         boolean addedSeparator = false;
         for (MenuCreator menuCreator : menuCreators) {
             JMenuItem[] menuItems = menuCreator.getMenuItemsAtLocation(location);
             for (JMenuItem jMenuItem : menuItems) {
                 if(!addedSeparator) {
                     menu.addSeparator();
                     addedSeparator = true;
                 }
                 menu.add(jMenuItem);
             }
         }
     }
     
     public void setRunning(boolean running, String host) {
         log.info("setRunning(" + running + "," + host + ")");
         if(org.apache.jmeter.gui.MainFrame.LOCAL.equals(host)) {
             return;
         }
         Iterator<JMenuItem> iter = remoteEngineStart.iterator();
         Iterator<JMenuItem> iter2 = remoteEngineStop.iterator();
         Iterator<JMenuItem> iter3 = remoteEngineExit.iterator();
         Iterator<JMenuItem> iter4 = remoteEngineShut.iterator();
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
         runStart.setEnabled(!enable);
         runStartNoTimers.setEnabled(!enable);
         runStop.setEnabled(enable);
         runShut.setEnabled(enable);
     }
 
     private void getRemoteItems() {
         if (remoteHosts.length > 0) {
             remoteStart = makeMenuRes("remote_start"); //$NON-NLS-1$
             remoteStop = makeMenuRes("remote_stop"); //$NON-NLS-1$
             remoteShut = makeMenuRes("remote_shut"); //$NON-NLS-1$
             remoteExit = makeMenuRes("remote_exit"); //$NON-NLS-1$
 
             for (int i = 0; i < remoteHosts.length; i++) {
                 remoteHosts[i] = remoteHosts[i].trim();
 
                 JMenuItem item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_START);
                 remoteEngineStart.add(item);
                 remoteStart.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_STOP);
                 item.setEnabled(false);
                 remoteEngineStop.add(item);
                 remoteStop.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i], ActionNames.REMOTE_SHUT);
                 item.setEnabled(false);
                 remoteEngineShut.add(item);
                 remoteShut.add(item);
 
                 item = makeMenuItemNoRes(remoteHosts[i],ActionNames.REMOTE_EXIT);
                 item.setEnabled(false);
                 remoteEngineExit.add(item);
                 remoteExit.add(item);
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
         for (MenuCreator creator : menuCreators) {
             creator.localeChanged();
         }
     }
 
     /**
      * Get a list of all installed LAFs plus CrossPlatform and System.
      * 
      * @return The list of available {@link LookAndFeelInfo}s
      */
     // This is also used by LookAndFeelCommand
     public static LookAndFeelInfo[] getAllLAFs() {
         UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
         int i = lafs.length;
         UIManager.LookAndFeelInfo[] lafsAll = new UIManager.LookAndFeelInfo[i+2];
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
             for (MenuCreator menuCreator : menuCreators) {
                 if(menuCreator.localeChanged(menu)) {
                     return;
                 }
             }
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
 
         for (MenuElement subElement : menu.getSubElements()) {
             updateMenuElement(subElement);
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
diff --git a/src/core/org/apache/jmeter/images/toolbar/22x22/document-close-4.png b/src/core/org/apache/jmeter/images/toolbar/22x22/document-close-4.png
deleted file mode 100644
index c1b275b79..000000000
Binary files a/src/core/org/apache/jmeter/images/toolbar/22x22/document-close-4.png and /dev/null differ
diff --git a/src/core/org/apache/jmeter/images/toolbar/32x32/document-close-4.png b/src/core/org/apache/jmeter/images/toolbar/32x32/document-close-4.png
deleted file mode 100644
index db97a95b4..000000000
Binary files a/src/core/org/apache/jmeter/images/toolbar/32x32/document-close-4.png and /dev/null differ
diff --git a/src/core/org/apache/jmeter/images/toolbar/48x48/document-close-4.png b/src/core/org/apache/jmeter/images/toolbar/48x48/document-close-4.png
deleted file mode 100644
index 018b5db83..000000000
Binary files a/src/core/org/apache/jmeter/images/toolbar/48x48/document-close-4.png and /dev/null differ
diff --git a/src/core/org/apache/jmeter/images/toolbar/icons-toolbar.properties b/src/core/org/apache/jmeter/images/toolbar/icons-toolbar.properties
index ddc9f0119..858b57589 100644
--- a/src/core/org/apache/jmeter/images/toolbar/icons-toolbar.properties
+++ b/src/core/org/apache/jmeter/images/toolbar/icons-toolbar.properties
@@ -1,49 +1,48 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #   http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
 # Icons order. Keys separate by comma. Use a pipe | to have a space between two icons.
 toolbar=new,templates,open,close,save,save_as_testplan,|,undo,redo,cut,copy,paste,|,expand,collapse,toggle,|,test_start,test_start_notimers,test_stop,test_shutdown,|,test_start_remote_all,test_stop_remote_all,test_shutdown_remote_all,|,test_clear,test_clear_all,|,search,search_reset,|,function_helper,help
 
 # Icon / action definition file.
 # Key:      button names
 # Value:    I18N key in messages.properties, ActionNames key field, icon path, optionally followed by comma and then the pressed icon name
 # Special keyword "<SIZE>" for the different size of the same icon (Available sizes are: 22x22, 32x32, 48x48)
 new=new,CLOSE,org/apache/jmeter/images/toolbar/<SIZE>/document-new-4.png
 templates=template_menu,TEMPLATES,org/apache/jmeter/images/toolbar/<SIZE>/applications-office.png
 open=menu_open,OPEN,org/apache/jmeter/images/toolbar/<SIZE>/document-open-2.png
-close=menu_close,CLOSE,org/apache/jmeter/images/toolbar/<SIZE>/document-close-4.png
 save=save,SAVE,org/apache/jmeter/images/toolbar/<SIZE>/document-save-5.png
 save_as_testplan=save_as,SAVE_AS,org/apache/jmeter/images/toolbar/<SIZE>/document-save-as-5.png
 cut=cut,CUT,org/apache/jmeter/images/toolbar/<SIZE>/edit-cut-4.png
 copy=copy,COPY,org/apache/jmeter/images/toolbar/<SIZE>/edit-copy-4.png
 paste=paste,PASTE,org/apache/jmeter/images/toolbar/<SIZE>/edit-paste-4.png
 test_start=start,ACTION_START,org/apache/jmeter/images/toolbar/<SIZE>/arrow-right-3.png
 test_start_notimers=start_no_timers,ACTION_START_NO_TIMERS,org/apache/jmeter/images/toolbar/<SIZE>/arrow-right-3-notimer.png
 test_stop=stop,ACTION_STOP,org/apache/jmeter/images/toolbar/<SIZE>/road-sign-us-stop.png
 test_shutdown=shutdown,ACTION_SHUTDOWN,org/apache/jmeter/images/toolbar/<SIZE>/process-stop-7.png
 test_start_remote_all=remote_start_all,REMOTE_START_ALL,org/apache/jmeter/images/toolbar/<SIZE>/arrow-right-3-startremoteall.png
 test_stop_remote_all=remote_stop_all,REMOTE_STOP_ALL,org/apache/jmeter/images/toolbar/<SIZE>/road-sign-us-stop-stopremoteall.png
 test_shutdown_remote_all=remote_shut_all,REMOTE_SHUT_ALL,org/apache/jmeter/images/toolbar/<SIZE>/process-stop-7-shutdownremoteall.png
 test_clear=clear,CLEAR,org/apache/jmeter/images/toolbar/<SIZE>/run-build-clean.png
 test_clear_all=clear_all,CLEAR_ALL,org/apache/jmeter/images/toolbar/<SIZE>/run-build-prune.png
 toggle=toggle,TOGGLE,org/apache/jmeter/images/toolbar/<SIZE>/color-picker-toggle.png
 expand=menu_expand_all,EXPAND_ALL,org/apache/jmeter/images/toolbar/<SIZE>/list-add-3.png
 collapse=menu_collapse_all,COLLAPSE_ALL,org/apache/jmeter/images/toolbar/<SIZE>/list-remove-3.png
 search=menu_search,SEARCH_TREE,org/apache/jmeter/images/toolbar/<SIZE>/edit-find-7.png
 search_reset=menu_search_reset,SEARCH_RESET,org/apache/jmeter/images/toolbar/<SIZE>/edit-clear-3.png
 function_helper=function_dialog_menu_item,FUNCTIONS,org/apache/jmeter/images/toolbar/<SIZE>/documentation.png
 help=help,HELP,org/apache/jmeter/images/toolbar/<SIZE>/help-contents-5.png
 undo=undo,UNDO,org/apache/jmeter/images/toolbar/<SIZE>/edit-undo-7.png
 redo=redo,REDO,org/apache/jmeter/images/toolbar/<SIZE>/edit-redo-7.png
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index dbcacdd1d..a592bc64b 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1351 +1,1350 @@
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
 add_think_times=Add Think Times to children
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
 apply_naming=Apply Naming Policy
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
 assertion_or=Or
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
 jms_error_reconnect_on_codes=Reconnect on error codes (regex)
 jms_error_pause_between=Pause between errors (ms)
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
 jsonpp_match_numbers=Match No. (0 for Random)
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
 action_check_message=A Test is currently running, stop or shutdown test to execute this command
 action_check_title=Test Running
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
-menu_close=Close
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
 reportgenerator_top5_error_count=#Errors
 reportgenerator_top5_error_label=Error
 reportgenerator_top5_label=Sample
 reportgenerator_top5_sample_count=#Samples
 reportgenerator_top5_total=Total
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
 reportgenerator_summary_statistics_max=Max (ms)
 reportgenerator_summary_statistics_mean=Average response time (ms)
 reportgenerator_summary_statistics_min=Min (ms)
 reportgenerator_summary_statistics_percentile_fmt=%dth pct (ms)
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
 search_replace_all=Replace All
 search_test=Search Test
 search_text_button_close=Close
 search_text_button_find=Find
 search_text_button_next=Find next
 search_text_chkbox_case=Case sensitive
 search_text_chkbox_regexp=Regular exp.
 search_text_field=Search: 
 search_text_msg_not_found=Text not found
 search_text_replace=Replace by
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
 view_results_render_browser=Browser
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
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/resources/messages_de.properties b/src/core/org/apache/jmeter/resources/messages_de.properties
index 757344cfc..8dc0b292d 100644
--- a/src/core/org/apache/jmeter/resources/messages_de.properties
+++ b/src/core/org/apache/jmeter/resources/messages_de.properties
@@ -1,542 +1,541 @@
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
 
 about=\u00DCber Apache JMeter
 add=Hinzuf\u00FCgen
 add_as_child=Als ein Kind hinzuf\u00FCgen
 add_parameter=Variable hinzuf\u00FCgen
 add_pattern=Muster hinzuf\u00FCgen\:
 add_test=Test hinzuf\u00FCgen
 add_user=Benutzer hinzuf\u00FCgen
 add_value=Wert hinzuf\u00FCgen
 addtest=Test hinzuf\u00FCgen
 aggregate_graph=Statistischer Graph
 aggregate_graph_column=Spalte
 aggregate_graph_display=Graphen anzeigen
 aggregate_graph_height=H\u00F6he
 aggregate_graph_max_length_xaxis_label=Maximale L\u00E4nge des x-Achsen Bezeichners
 aggregate_graph_ms=Millisekunden
 aggregate_graph_response_time=Reaktionszeit
 aggregate_graph_save=Graphen speichern
 aggregate_graph_save_table=Tabellen Daten speichern
 aggregate_graph_save_table_header=Tabellen Kopf speichern
 aggregate_graph_title=Graph
 aggregate_graph_user_title=Titel f\u00FCr den Graphen
 aggregate_graph_width=Breite
 aggregate_report=Report
 aggregate_report_bandwidth=KB/sek
 aggregate_report_count=Anz. der Proben
 aggregate_report_error=Fehler
 aggregate_report_error%=% Fehler
 aggregate_report_median=Mittel
 aggregate_report_rate=Durchsatz
 aggregate_report_total_label=Gesamt
 als_message=Hinweis\: Der Zugriff-Log Parser ist allgmein gehalten. Es ist m\u00F6glich ein Plugin zu erstellen.
 als_message2=Eigener Parser. Implementieren sie hierzu "LogParser" und f\u00FCgen sie es als .jar hinzu
 analyze=Analysiere Daten Datei...
 appearance=Aussehen (Look & Feel)
 argument_must_not_be_negative=Der Wert darf nicht negativ sein\!
 assertion_assume_success=Status ignorieren
 assertion_code_resp=Antwort-Code (Response-Code)
 assertion_contains=Enth\u00E4lt
 assertion_equals=Gleicht
 assertion_headers=Antwort-Header (Response-Header)
 assertion_matches=Entsprechungen
 assertion_message_resp=Antwort-Message
 assertion_not=Nicht
 assertion_pattern_match_rules=Regeln f\u00FCr passende Muster
 assertion_patterns_to_test=Zu testende(s) Muster
 assertion_resp_field=Zu testendes Antwort-Feld (Response-Feld)
 assertion_substring=Teilzeichenkette (Substring)
 assertion_text_resp=Text-Antwort (Text-Response)
 assertion_textarea_label=Behauptungen\:
 assertion_title=Versicherte Anwort
 assertion_url_samp=URL gesampled
 assertion_visualizer_title=Versicherungs Erebnis
 attribute=Attribut
 attrs=Attribute
 auth_manager_title=HTTP Authorisierungs Manager
 auths_stored=Im Authorization Manager gespeicherte Authorisierungen
 average=Durchschnitt
 average_bytes=Durchschnittliche Bytes
 browse=Datei laden...
 bsf_script=Auszuf\u00FChrendes Script (Variablen\: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Auszuf\u00FChrendes Script
 bsf_script_language=Scriptsprache
 bsf_script_parameters=An das Script bzw. die Script-Datei zu \u00FCbergebende Parameter
 bsh_assertion_script=Script (untenstehende Variablen sind definiert)
 bsh_assertion_title=BeanShell Behauptung
 bsh_function_expression=Auszuwertender Ausdruck
 bsh_script=Script (untenstehende Variablen sind definiert)
 bsh_script_file=Script-Datei
 bsh_script_variables=Folgende Variablen wurden f\u00FCr das Script definiert\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Ich bin mit dem Testen besch\u00E4ftigt, bitte stoppen sie den Test bevor sie die Einstellungen \u00E4ndern.
 cache_session_id=Session ID zwischenspeichern?
 cancel=Abbrechen
 cancel_exit_to_save=Es gibt Tests die noch nicht gespeichert wurden. M\u00F6chten sie diese vor dem Beenden speichern?
 cancel_new_to_save=Es gibt Tests die noch nicht gespeichert wurden. M\u00F6chten sie diese vor dem Bereinigen speichern?
 cancel_revert_project=Es gibt Tests die noch nicht gespeichert wurden. M\u00F6chten sie zum zuletzt gespeicherten Testplan zur\u00FCck gehen?
 choose_function=W\u00E4hlen Sie eine Funktion
 choose_language=W\u00E4hlen sie eine Sprache
 clear=L\u00F6schen
 clear_all=Alle L\u00F6schen
 clear_cookies_per_iter=Cookies bei jedem Durchgang l\u00F6schen?
 column_delete_disallowed=Das l\u00F6schen dieser Spalte ist nicht erlaubt
 column_number=Spaltennummer der CSV Datei file | next | *alias
 compare=Vergleichen
 comparefilt=Vergleichsfilter
 config_element=Konfigurations Element
 config_save_settings=Konfigurieren
 constant_throughput_timer_memo=Geben sie eine Pause zwischen den Proben an um einen konstanten Durchsatz zu gew\u00E4hrleisten
 constant_timer_delay=Thread-Pause (in Millisekunden)
 constant_timer_memo=Geben sie eine Pause zwischen den Proben an
 constant_timer_title=Konstanter Timer
 content_encoding=Content Kodierung\:
 cookie_manager_policy=Cookie Richtlinie
 cookies_stored=Anzahl der gespeicherten Cookies im Cookie Manager
 copy=Kopieren
 counter_config_title=Z\u00E4hler (Counter)
 counter_per_user=Z\u00E4hler (Counter) f\u00FCr jeden Benutzer einzeln f\u00FChren
 countlim=Gr\u00F6\u00DFen-Beschr\u00E4nkung
 csvread_file_file_name=CVS Datei aus der die Werte gelesen werden | *alias
 cut=Ausschneiden
 cut_paste_function=Kopieren und Einf\u00FCgen des Funktions Strings
 database_conn_pool_max_usage=Maximale Auslastung jeder Verbindung\:
 database_conn_pool_props=Datenbank Verbindungs-Pool\:
 database_conn_pool_size=Anzahl der Verbindungen im Pool\:
 database_conn_pool_title=Vorgaben zum JDBC Datenbank Verbindungs Pool
 database_driver_class=Treiber-Klasse\:
 database_login_title=JDBC Datenbank Login Vorgabe
 database_sql_query_string=SQL Abfrage\:
 database_sql_query_title=Vorgaben zur JDBC SQL Abfrage
 database_testing_title=JDBC Anfrage
 database_url_jdbc_props=Database URL und JDBC Treiber
 de=Deutsch
 debug_off=Debugging deaktivieren
 debug_on=Debugging aktivieren
 default_parameters=Standard Parameter
 default_value_field=Vorgabe-Wert\:
 delay=Pause zu Beginn (Sekunden)
 delete=L\u00F6schen
 delete_parameter=L\u00F6sche Variable
 delete_test=Test L\u00F6schen
 delete_user=Benutzer L\u00F6schen
 deltest=L\u00F6sch-Test
 deref=Dereferenzierungs Aliasse
 disable=Deaktivieren
 distribution_graph_title=Verteilungs-Graph (Alpha)
 distribution_note1=Der Graph wird mit jeder 10. Probe aktualisiert
 done=Fertig
 duration=Dauer (Sekunden)
 duration_assertion_duration_test=Dauer der Aufrechterhaltung
 duration_assertion_failure=Die Operation dauerte zu lang\: es wurden {0} Millisekunden ben\u00F6tigt, h\u00E4tte aber maximal {1} Millisekunden dauern d\u00FCrfen.
 duration_assertion_input_error=Geben Sie bitte einen g\u00FCltigen, positive Ganzzahl-Wert ein.
 duration_assertion_label=Dauer in Millisekunden
 duration_assertion_title=Aufrechterhaltungs-Dauer
 edit=Bearbeiten
 email_results_title=Ergebnisse per eMail verschicken
 en=Englisch
 enable=Aktivieren
 encode?=Encodieren?
 encoded_value=URL-Encodierter Wert
 endtime=End-Zeitpunkt
 entry_dn=Ausgangs DN
 entrydn=Ausgangs DN
 error_loading_help=Fehler beim laden der Hilfe-Seite
 error_occurred=Es ist ein Fehler aufgetreten
 error_title=Fehler
 es=Spanisch
 eval_name_param=Ein Ausdruck der eine Variable und Funktions-Referenz enth\u00E4lt
 evalvar_name_param=Variablenname
 example_data=Beispieldaten
 example_title=Beispiel Proben
 exit=Beenden
 expiration=Verfall
 field_name=Feldname
 file=Datei
 file_already_in_use=Die Datei ist bereits ge\u00F6ffnet
 file_visualizer_append=An eine existierende Daten-Datei anh\u00E4ngen 
 file_visualizer_auto_flush=Nach jeder Daten-Probe bereinigen (Flush)
 file_visualizer_browse=Datei laden...
 file_visualizer_close=Schliessen
 file_visualizer_file_options=Datei Optionen
 file_visualizer_filename=Dateinamen eingeben, oder eine existierende Datei ausw\u00E4hlen.
 file_visualizer_flush=Bereinigen
 file_visualizer_missing_filename=Kein Ausgabe Dateiname angegeben.
 file_visualizer_open=\u00D6ffnen
 file_visualizer_output_file=Schreibe alle Daten in eine Datei
 file_visualizer_submit_data=Einschliesslich \u00FCbermittelter Daten
 file_visualizer_title=Datei Reporter
 file_visualizer_verbose=Umfangreiche Ausgabe (Verbose)
 filename=Dateiname
 follow_redirects=Folge Redirects
 follow_redirects_auto=Automatisch Redirects folgen
 foreach_input=Prefix der Eingabe-Variable
 foreach_output=Name der Ausgabe-Variable
 foreach_use_separator=Trennzeichen "_" vor jeder Nummer einf\u00FCgen?
 format=Zahlenformat
 fr=Franz\u00F6sisch
 ftp_binary_mode=Bin\u00E4r-Modus verwenden?
 ftp_local_file=Lokale Datei\:
 ftp_remote_file=Entfernte Datei
 ftp_sample_title=Vorgaben zum FTP Request
 ftp_save_response_data=Datei in Antwort speichern?
 ftp_testing_title=FTP Anfrage
 function_dialog_menu_item=Funktions Hilfe-Dialog
 function_helper_title=Funktions Hilfe
 function_name_param=Name der Variablen, in der die Ergebnisse abgelegt werden sollen (ben\u00F6tigt)
 function_name_paropt=Name der Variablen, in der die Ergebnisse abgelegt werden sollen (optional)
 function_params=Funktions Parameter
 functional_mode=Funktionaler Test Mode
 functional_mode_explanation=Diese Funktion f\u00FChrt zu betr\u00E4chtlichen Performanceverlusten.
 gaussian_timer_delay=Konstante Pause (in Millisekunden)
 gaussian_timer_memo=Zus\u00E4tzliche Pause zur Gauss'schen Verteilung
 gaussian_timer_range=Abweichung (in Millisekunden)
 gaussian_timer_title=Gauss'scher Zufalls-Zeitgeber
 generate=Generiere
 generator=Name der Erzeuger-Klasse
 generator_cnf_msg=Kann die Erzeuger-Klasse (Generator) nicht finden. Vergewissern sie sich, dass die das .jar Archiv in das /lib Verzeichnis gelegt haben.
 generator_illegal_msg=Konnte wegen einer "IllegalAcessException" nicht auf die Erzeuger-Klasse (Generator) zugreifen.
 generator_instantiate_msg=Konnte keine Instanz der Erzeuger-Klasse ertsllen. Stellen sie sicher, dass der Erzeuger das "Generator"-Interface implementiert\!
 graph_choose_graphs=Anzuzeigende Graphen
 graph_full_results_title=Vollst\u00E4ndige Ergebnisse
 graph_results_average=Durchschnitt
 graph_results_data=Daten
 graph_results_deviation=Abweichung
 graph_results_latest_sample=Letzte Probe
 graph_results_median=Mittel
 graph_results_ms=Millisekunden (ms)
 graph_results_no_samples=Anzahl der Proben
 graph_results_throughput=Durchsatz
 graph_results_title=Ergebnisse
 grouping_add_separators=Zwischen den Gruppen Trennzeichen einf\u00FCgen
 grouping_in_controllers=Jede Gruppe in einen neuen Controller legen
 grouping_mode=Gruppierung
 grouping_no_groups=Sampler nicht gruppieren
 grouping_store_first_only=Nur den ersten Sampler jeder Gruppe speichern
 headers_stored=Gespeicherte Header im Header Manager
 help=Hilfe
 help_node=Wof\u00FCr ist das?
 html_assertion_file=Schreibe JTidy Bericht in eine Datei
 html_assertion_label=HTML Bericht
 html_assertion_title=Titel des HTML Bericht
 http_url_rewriting_modifier_title=HTTP URL Re-writing Bezeichner
 http_user_parameter_modifier=HTTP User Parameter Bezeichner
 httpmirror_title=HTTP Spiegel
 if_controller_evaluate_all=F\u00FCr alle Unterelemente auswerten?
 if_controller_label=Bedingung (Javascript)
 if_controller_title=If-Controller
 ignore_subcontrollers=Ignoriere Sub-Controller Bl\u00F6cke
 include_controller=Controller einschlie\u00DFen
 include_equals=Gleichheitszeichen mit einbeziehen?
 include_path=Test-Plan mit einbeziehen?
 increment=Zunahme
 infinite=endlos Wiederholen
 insert_after=Dahinter einf\u00FCgen
 insert_before=Davor einf\u00FCgen
 insert_parent=Dar\u00FCber Einf\u00FCgen
 interleave_control_title=Controller \u00FCberlagern
 intsum_param_1=Erster Ganzzahl Wert (int)
 intsum_param_2=Zweiter Ganzzahl Wert (int). Weitere Werte k\u00F6nnen durch Angabe weiterer Argumente addiert werden.
 invalid_data=Ung\u00FCltige Daten
 invalid_mail=Fehler beim senden der E-Mail
 invalid_mail_address=Eine oder mehrere fehlerhafte E-Mail Adressen gefunden
 invalid_mail_server=Probleme beim Verbinden mit dem Mail-Server (siehe JMeter Log-Datei)
 invalid_variables=Ung\u00FCltige Variablen
 iteration_counter_arg_1="TRUE" damit jeder Benutzer einen eingenen Z\u00E4hler hat. "FALSE" f\u00FCr einen globalen Z\u00E4hler.
 iterator_num=Anzahl der Wiederholungen\:
 jar_file=.jar Dateien
 java_request=Java Anfrage (Request)
 java_request_defaults=Java Anfrage (Request) Vorgabe
 javascript_expression=Zu evaluierender JavaScript Ausdruck
 jexl_expression=Auszuwertender JEXL Ausdruck
 jms_auth_required=Ben\u00F6tigt
 jndi_config_title=JNDI Konfiguration
 jndi_url_jndi_props=JNDI Eigenschaften
 load=Laden
 log_errors_only=Fehler
 log_file=Ort der Log-Datei
 log_function_comment=Zus\u00E4tzliche Kommentare (optional)
 log_function_level=Log-Level "INFO" (Vorgabe), "OUT" oder "ERR"
 log_function_string=Zu loggende Zeichenkette
 log_function_string_ret=Zu loggende (und zur\u00FCckzugebende) Zeichenkette
 log_function_throwable="Throwable" Test (optional)
 log_only=Nur Loggen/Anzeigen\:
 log_parser=Name der Log-Parser Klasse
 log_parser_cnf_msg=Kann die Klasse nicht finden. Vergewissern sie sich, dass die das .jar Archiv in das /lib Verzeichnis gelegt haben.
 log_parser_illegal_msg=Konnte aufgrund einer "IllegalAcessException" nicht auf die Klasse zugreifen.
 log_parser_instantiate_msg=Konnte keine Instanz der Log-Parsers erstellen. Stellen sie sicher, dass der Erzeuger das "LogParser"-Interface implementiert\!
 log_sampler=Tomcat Zugriffs-Log Sampler
 log_success_only=Erfolge
 logic_controller_title=Einfacher Controller
 login_config=Login Konfiguration
 login_config_element=Login Konfigurations Element
 longsum_param_1=Erster long-Wert
 longsum_param_2=Zweiter long-Wert. Durch weitere Parameter k\u00F6nnen zus\u00E4tzliche long-Werte hinzugef\u00FCgt werden
 loop_controller_title=Schleifen-Controller (Loop Controller)
 looping_control=Wiederholungs-Control
 lower_bound=Untere Grenze
 mail_reader_account=Benutzername\:
 mail_reader_all_messages=Alle
 mail_reader_delete=Nachrichten vom Server l\u00F6schen
 mail_reader_folder=Verzeichnis\:
 mail_reader_num_messages=Anzahl der zu ladenen Nachrichten\:
 mail_reader_password=Passwort\:
 mail_reader_server_type=Server-Typ\:
 mail_sent=Mail erfolgreich gesendet
 mailer_attributes_panel=Mail Eigenschaften
 mailer_error=Konnte die Mail nicht senden. Bitte korrigieren Sie jede fehlerhafte Eingabe.
 mailer_visualizer_title=Mailer-Visualisierung
 maximum_param=Der maximale Wert welcher f\u00FCr einen Wertebereich erlaubt ist
 md5hex_assertion_failure=Fehler beim \u00FCberpr\u00FCfen der MD5 Summe\: {0} erhalten, sollte {1} sein
 md5hex_assertion_md5hex_test=Zu pr\u00FCfender MD5 Hex String
 md5hex_assertion_title=MD5 Hex \u00DCberpr\u00FCfung
 mechanism=Mechanismus
 menu_assertions=\u00DCberpr\u00FCfung
-menu_close=Schlie\u00DFen
 menu_collapse_all=Alle schlie\u00DFen
 menu_config_element=Konfigurations Element
 menu_edit=Editieren
 menu_expand_all=Alle \u00F6ffnen
 menu_logic_controller=Logik-Controller
 menu_merge=Zusammenf\u00FCgen
 menu_modifiers=Modifizierer
 menu_non_test_elements=Nicht-Test Elemente
 menu_open=\u00D6ffnen
 menu_post_processors=Post-Processors
 menu_pre_processors=Pre-Processors
 menu_response_based_modifiers=Antwort-Basierter Modifizierer
 menu_timer=Zeitgeber (Timer)
 method=Methode\:
 minimum_param=Der minimale Wert welcher f\u00FCr einen Wertebereich erlaubt ist
 minute=Minute
 modddn=Alter Name
 modification_controller_title=Modifikations-Controller
 modification_manager_title=Modifikations-Manager
 modify_test=Test \u00E4ndern
 modtest=\u00C4nderungs-Test
 module_controller_module_to_run=Auszurufendes Modul
 module_controller_title=Modul-Controller
 module_controller_warning=Konnte Modul nicht finden\:
 new=Neu
 newdn=Neuer DN (distinguished name)
 no=Norwegisch
 number_of_threads=Anzahl von Threads\:
 obsolete_test_element=Dieser Test-Abschnitt ist hinf\u00E4lig
 once_only_controller_title=Einmal-Controller
 opcode=OPcode
 open=\u00D6ffnen
 option=Optionen
 optional_tasks=Optionale Aufgaben
 paramtable=Parameter die mit dem Request gesendet werden\:
 password=Passwort
 paste=Einf\u00FCgen
 paste_insert=Als Eintrag einf\u00FCgen
 path=Pfad\:
 path_extension_choice=Pfad-Erweiterung (benutze ";" als Trennzeichen)
 path_extension_dont_use_equals=Keine Gleichheitszeichen in Pfad-Erweiterung benutzen (Intershop Enfinity compatibility)
 path_extension_dont_use_questionmark=Keine Fragezeichen in Pfad-Erweiterung benutzen (Intershop Enfinity compatibility)
 patterns_to_exclude=Auszuschlie\u00DFende URL-Muster
 patterns_to_include=Einzuschlie\u00DFende URL-Muster
 property_default_param=Vorgabe-Wert
 property_edit=Bearbeiten
 property_editor.value_is_invalid_message=Der eingegebene Text ist f\u00FCr diese Eigenschaft ung\u00FCltig.\nDer Wert wird auf seinen vorherigen Wert zur\u00FCck gesetzt.
 property_editor.value_is_invalid_title=Ung\u00FCltige Eingabe
 property_name_param=Name der Eigenschaft
 property_returnvalue_param=Urspr\u00FCnglichen Wert der Eigenschaft zur\u00FCckgeben? Vorgabe\: false
 property_undefined=undefiniert
 property_value_param=Wert der Eigeschaft
 property_visualiser_title=Eigenschaften
 protocol=Protokoll [http]\:
 protocol_java_border=Java-Klasse
 protocol_java_classname=Klassenname (classname)\:
 protocol_java_config_tile=Java Sample Konfigurieren
 protocol_java_test_title=Java Tests
 proxy_assertions=Versicherungen hinzuf\u00FCgen
 proxy_cl_error=Wenn Sie einen Proxy Server spezifizieren, m\u00FCssen Sie den Host und Port angeben
 proxy_content_type_exclude=Ausschlie\u00DFen\:
 proxy_content_type_filter=Content-Type Filter
 proxy_content_type_include=Einschlie\u00DFen\:
 proxy_headers=HTTP-Header \u00FCberwachen
 proxy_regex=RegEx Muster
 proxy_sampler_settings=HTTP Sampler Einstellungen
 proxy_sampler_type=Typ\:
 proxy_separators=F\u00FCgen sie Trennzeichen hinzu
 proxy_target=Ziel-Controller (Target-Controller)\:
 proxy_test_plan_content=Test-Plan Inhalt\:
 random_control_title=Zufalls-Controller
 random_order_control_title=Zufalls-Reihenfolgen-Controller
 realm=Bereich
 regexfunc_param_1=Regul\u00E4re Ausdr\u00FCcke zum Suchen in den Results der vorherigen Requests
 regexfunc_param_2=Beispiel f\u00FCr Ersetzungs Strings, benuzte Gruppen von den regul\u00E4ren Ausdr\u00FCcken
 regexfunc_param_3=Which match to use.  Einen Integer 1 oder gr\u00F6sser, RAND damit JMeter eine zuf\u00E4llige Auswahl trifft, eine Fliesskommazahl, oder ALL wenn alle Treffer benutzt werden
 regexfunc_param_4=Zwischen Text.  Wenn ALL ausgew\u00E4hlt ist, wird der zwischen Test benutzt um das Ergebnis zu generieren
 regexfunc_param_5=Standard Text.  Wird benutzt anstatt der Vorlage, falls der Regul\u00E4re Ausdruck keine Treffer findet
 remove=Entfernen
 report_bar_chart=Balken-Diagramm
 report_base_directory=Basis-Verzeichnis
 report_chart_caption=Diagramm-Titel
 report_chart_x_axis=X-Achse
 report_chart_x_axis_label=Bezeichner f\u00FCr die X-Achse
 report_chart_y_axis=Y-Achse
 report_chart_y_axis_label=Bezeichner f\u00FCr die Y-Achse
 report_line_graph=Linien-Diagramm
 report_line_graph_urls=URLs einbeziehen
 report_output_directory=Ausgabe-Verzeichnis f\u00FCr den Bericht
 report_page=Bericht-Seite
 report_page_element=Seitenelement
 report_page_footer=Seitenfu\u00DF
 report_page_header=Seitenkopf
 report_page_index=Seiten-Index erstellen
 report_page_intro=Seiteneinleitung
 report_page_style_url=Stylesheet URL
 report_page_title=Seitentitel
 report_pie_chart=Torten-Diagramm
 report_select=Ausw\u00E4hlen
 report_summary=Bericht-Zusammenfassung
 report_writer=Bericht-Schreiber
 report_writer_html=HTML Bericht-Schreiber
 request_data=Request Daten
 reset_gui=GUI zur\u00FCcksetzen
 restart=Neu starten
 revert_project=Zur\u00FCcksetzen
 revert_project?=Projekt zur\u00FCck setzten?
 root=Wurzel
 root_title=Wurzel
 run=Start
 running_test=Test starten
 sampler_on_error_action=Aktion die bei einem Sampler-Fehler ausgef\u00FChrt werden soll
 sampler_on_error_continue=Fortfahren
 sampler_on_error_stop_test=Test Anhalten
 sampler_on_error_stop_thread=Thread Anhalten
 save=Speichern
 save?=Speichern?
 save_all_as=Test-Plan speichern unter
 save_as=Speichern unter
 save_as_error=Mehr als ein Element ausgew\u00E4hlt\!
 save_as_image=Als Bild speichern
 save_as_image_all=Bildschirm als Bild speichern
 save_assertionresultsfailuremessage=Speichere Meldungen der Versicherungs-Fehler
 save_assertions=Speichere Versicherungs-Ergebnisse (XML)
 save_asxml=Speichere als XML
 save_bytes=Speichere anzahl der Bytes
 save_code=Speichere Response-Code
 save_datatype=Speichere Daten-Typ
 save_encoding=Speichere Kodierung
 save_fieldnames=Speichere Feld-Namen (CSV)
 save_filename=Speichere Response-Dateiname
 save_graphics=Speichere Graphen
 save_hostname=Speichere Hostenamen
 save_label=Speichere Bezeichner
 save_latency=Speichere Latenz
 save_message=Speichere Response-Nachricht
 save_overwrite_existing_file=Die ausgew\u00E4hlte Datei existiert bereits, m\u00F6chten sie sie \u00FCberschreiben?
 save_requestheaders=Speichere Request-Header (XML)
 save_responsedata=Speichere Response-Daten (XML)
 save_responseheaders=Speichere Response-Header (XML)
 save_samplecount=Speichere Proben und Fehler Anzahl
 save_samplerdata=Speichere Sampler-Daten (XML)
 save_subresults=Speichere Unter-Ergebnisse (XML)
 save_success=Speichere Erfolge
 save_threadcounts=Speichere aktive Thread Anzahl
 save_threadname=Speichere Thread-Name
 save_time=Speichere ben\u00F6tigte Zeit
 save_timestamp=Speichere Zeitstempel
 save_url=Speichere URL
 scheduler_configuration=Scheduler Konfiguration
 scope=G\u00FCltigkeitsbereich (Scope)
 second=Sekunde
 secure=Sicher
 send_file=Datei mit dem Request senden\:
 send_file_browse=Datei ausw\u00E4hlen...
 send_file_filename_label=Dateiname\:
 send_file_param_name_label=Wert des "name"-Attributes\:
 server=Server Name oder IP\:
 should_save=Wenn sie supportete Daten Dateien (z.B. CSV) benutzen ist es wichtig zuerst das Test-Script zu speichern.\nM\u00F6chten sie den Test-Plan speichern?
 shutdown=Beenden
 size_assertion_comparator_error_equal=gleich
 size_assertion_comparator_error_greater=gr\u00F6\u00DFer als
 size_assertion_comparator_error_greaterequal=gr\u00F6\u00DFer oder gleich
 size_assertion_comparator_error_less=kleiner als
 size_assertion_comparator_error_lessequal=kleiner oder gleich
 size_assertion_comparator_error_notequal=nicht gleich
 size_assertion_comparator_label=Art des Vergleichs
 size_assertion_failure=Das Ergebnis hatte die falsche Gr\u00F6\u00DFe ({0} Byte). Es h\u00E4tte {1} {2} Byte sein m\u00FCssen.
 size_assertion_input_error=Bitte geben sie einen g\u00FCltigen Ganzzahl-Wert ein.
 size_assertion_label=Gr\u00F6\u00DFe in Byte\:
 size_assertion_size_test=Gr\u00F6\u00DFe versichern
 size_assertion_title=Gr\u00F6\u00DFen Versicherung
 soap_action=SOAP Aktion
 soap_data_title=SOAP/XML-RPC Daten
 soap_sampler_title=SOAP/XML-RPC Anfrage
 soap_send_action=Sende SOAP Aktion\:
 ssl_alias_prompt=Bitte geben Sie Ihren bevorzugten Alias ein
 ssl_alias_select=W\u00E4hlen Sie Ihren Alias f\u00FCr den Test
 ssl_error_title=Problem beim Schl\u00FCssel Speichern
 ssl_pass_prompt=Bitte geben Sie Ihr Passwort ein
 ssl_pass_title=Schl\u00FCssel Speicher Passwort
 starttime=Startzeit
 stopping_test=Stoppe alle Tests. Bitte warten ...
 stopping_test_title=Stoppe den Test
 string_from_file_file_name=Geben sie den vollst\u00E4ndingen Datei-Pfad ein
 string_from_file_seq_final=Letzte Datei-Sequenznummer (optional)
 string_from_file_seq_start=Erste Datei-Sequenznummer (optional)
 table_visualizer_sample_num=Proben Anzahl
 table_visualizer_sample_time=Proben-Zeit (ms)
 table_visualizer_start_time=Startzeit
 table_visualizer_success=Erfolgreich
 table_visualizer_thread_name=Thread-Name
 table_visualizer_warning=Warnung
 tcp_config_title=TCP Sampler Konfiguration
 tcp_nodelay=Setzte "NoDelay"
 tcp_port=Port-Nummer\:
 tcp_request_data=Text senden
 tcp_timeout=Timeout (Millisekunden)\:
 test_action_duration=Dauer (Millisekunden)
 test_action_target=Ziel
 test_action_target_test=Alle Threads
 test_action_target_thread=Aktueller Thread
 test_action_title=Test-Aktion
 test_configuration=Test-Konfiguration
 test_plan=Testplan
 test_plan_classpath_browse=F\u00FCgen sie das Verzeichnis oder .jar zum classpath hinzu
 testconfiguration=Konfiguration Testen
 testplan.serialized=Thread-Gruppen nacheinander starten
 testplan_comments=Kommentare\:
 thread_delay_properties=Thread-Pause Eigenschaften
 thread_group_title=Thread Gruppe
 thread_properties=Thread-Eigenschaften
 threadgroup=Thread-Gruppe
 throughput_control_bynumber_label=Ausf\u00FChrungen (Gesamt)
 throughput_control_bypercent_label=Ausf\u00FChrungen (Prozent)
 throughput_control_perthread_label=pro Benutzer
 throughput_control_title=Durchsatz-Controller
 throughput_control_tplabel=Durchsatz
 time_format=Format-Zeichenkette des "SimpleDateFormat" (optional)
 timelim=Zeit-Limit
 tr=T\u00FCrkisch
 upload=Datei hochladen
 upper_bound=obere Grenze
 url_config_title=HTTP Request Default Einstellungen
 use_keepalive=Benutze KeepAlive
 user_defined_variables=Benutzer definierte Variablen
 user_param_mod_help_note=(\u00C4ndern Sie dies nicht. Stattdessen, bitte die Datei mit dem Namen in JMeter's /bin Ordner \u00E4ndern.)
 username=Benutzername
 value=Wert
 view_results_in_table=Zeige Ergebnisse in der Tabelle
 warning=Warnung\!
 web_server_domain=Server Name oder IP\:
 web_testing_retrieve_images=Hole alle Bilder und Java Applets (nur HTML Dateien)
 you_must_enter_a_valid_number=Sie m\u00FCssen ein g\u00FCltige Nummer eingeben
diff --git a/src/core/org/apache/jmeter/resources/messages_es.properties b/src/core/org/apache/jmeter/resources/messages_es.properties
index cdd85d403..aa5bdd5cb 100644
--- a/src/core/org/apache/jmeter/resources/messages_es.properties
+++ b/src/core/org/apache/jmeter/resources/messages_es.properties
@@ -1,1008 +1,1007 @@
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
 delayed_start=Retrasar la creaci\u00F3n de Hilos hasta que se necesiten
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
 jms_receive_queue=Nombre JNDI cola Recepci\u00F3n
 jms_request=S\u00F3lo Petici\u00F3n
 jms_requestreply=Respuesta a Petici\u00F3n
 jms_sample_title=Petici\u00F3n JMS por defecto
 jms_send_queue=Nombre JNDI Cola Petici\u00F3n
 jms_stop_between_samples=\u00BFParar entre muestras?
 jms_store_response=Guarde la respuesta
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
 menu_assertions=Aserciones
-menu_close=Cerrar
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
 start_no_timers=Inicio no se detiene
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
 transaction_controller_include_timers=Incluir la duraci\u00F3n de temporizador y pre-post procesadores en la muestra generada
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
 view_results_response_partial_message=Principio del mensaje:
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
 web_testing2_title=Petici\u00F3n HTTP HttpClient
 web_testing_embedded_url_pattern=Las URLs embebidas deben coincidir a\:
 web_testing_retrieve_images=Recuperar Todos los Recursos Empotrados de Archivos HTML
 web_testing_source_ip=Direcci\u00F3n IP fuente\:
 web_testing_title=Petici\u00F3n HTTP
 while_controller_label=Condici\u00F3n (funci\u00F3n o variable)
 while_controller_title=Controlador While
 workbench_title=Banco de Trabajo
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
index be7aea4c7..21535b999 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,1341 +1,1340 @@
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
 add_think_times=Add Think Times to children
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
 apply_naming=Appliquer Convention Nommage
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
 assertion_or=Ou
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
 jms_error_reconnect_on_codes=Se reconnecter pour les codes d'erreurs (regex)
 jms_error_pause_between=Temporisation entre erreurs (ms)
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
 jsonpp_default_values=Valeur par d\u00E9fault
 jsonpp_error_number_arguments_mismatch_error=D\u00E9calage entre nombre de variables, expressions et valeurs par d\u00E9faut
 jsonpp_json_path_expressions=Expressions JSON Path
 jsonpp_match_numbers=R\u00E9cup\u00E9rer la Ni\u00E8me corresp. (0 \: Al\u00E9atoire)
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
 action_check_message=Un test est en cours, arr\u00EAtez le avant d''utiliser cette commande
 action_check_title=Test en cours
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
-menu_close=Fermer
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
 reportgenerator_top5_error_count=#Erreurs
 reportgenerator_top5_error_label=Erreur
 reportgenerator_top5_label=Echantillon
 reportgenerator_top5_sample_count=#Echantillons
 reportgenerator_top5_total=Total
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
 reportgenerator_summary_statistics_max=Max (ms)
 reportgenerator_summary_statistics_mean=Temps moyen (ms)
 reportgenerator_summary_statistics_min=Min (ms)
 reportgenerator_summary_statistics_percentile_fmt=%d%% centile (ms)
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
 save_as=Enregistrer la s\u00E9lection sous...
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
 search_replace_all=Tout remplacer
 search_test=Recherche
 search_text_button_close=Fermer
 search_text_button_find=Rechercher
 search_text_button_next=Suivant
 search_text_chkbox_case=Consid\u00E9rer la casse
 search_text_chkbox_regexp=Exp. reguli\u00E8re
 search_text_field=Rechercher \:
 search_text_msg_not_found=Texte non trouv\u00E9
 search_text_replace=Remplacer par
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
 view_results_render_browser=Navigateur
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
diff --git a/src/core/org/apache/jmeter/resources/messages_ja.properties b/src/core/org/apache/jmeter/resources/messages_ja.properties
index 1d2d86a78..1421234ab 100644
--- a/src/core/org/apache/jmeter/resources/messages_ja.properties
+++ b/src/core/org/apache/jmeter/resources/messages_ja.properties
@@ -1,445 +1,444 @@
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
 about=Apache JMeter \u306B\u3064\u3044\u3066
 add=\u8FFD\u52A0
 add_as_child=\u5B50\u3068\u3057\u3066\u8FFD\u52A0
 add_parameter=\u5909\u6570\u306E\u8FFD\u52A0
 add_pattern=\u30D1\u30BF\u30FC\u30F3\u8FFD\u52A0\:
 add_test=\u30C6\u30B9\u30C8\u306E\u8FFD\u52A0
 add_user=\u30E6\u30FC\u30B6\u30FC\u306E\u8FFD\u52A0
 add_value=\u5024\u306E\u8FFD\u52A0
 aggregate_report=\u7D71\u8A08\u30EC\u30DD\u30FC\u30C8
 aggregate_report_total_label=\u5408\u8A08
 als_message=\u6CE8\u610F\: \u30A2\u30AF\u30BB\u30B9\u30ED\u30B0\u30D1\u30FC\u30B5\u306F\u6C4E\u7528\u7684\u306B\u8A2D\u8A08\u3055\u308C\u3066\u3044\u308B\u306E\u3067\u3001\u72EC\u81EA\u30D1\u30FC\u30B5\u3092
 als_message2=\u30D7\u30E9\u30B0\u30A4\u30F3\u53EF\u80FD\u3067\u3059\u3002\u305D\u306E\u305F\u3081\u306B\u306F\u3001LogParser\u3092\u5B9F\u88C5\u3057\u3066/lib\u30C7\u30A3\u30EC\u30AF\u30C8\u30EA\u306B
 als_message3=jar\u30D5\u30A1\u30A4\u30EB\u3092\u8FFD\u52A0\u3057\u3001\u30B5\u30F3\u30D7\u30E9\u30FC\u3067\u30AF\u30E9\u30B9\u3092\u5165\u529B\u3057\u3066\u304F\u3060\u3055\u3044\u3002
 analyze=\u30C7\u30FC\u30BF\u30D5\u30A1\u30A4\u30EB\u3092\u5206\u6790...
 anchor_modifier_title=HTML \u30EA\u30F3\u30AF\u30D1\u30FC\u30B5
 appearance=\u30EB\u30C3\u30AF&\u30D5\u30A3\u30FC\u30EB
 argument_must_not_be_negative=\u5F15\u6570\u306F\u8CA0\u306E\u5024\u3067\u306A\u3051\u308C\u3070\u306A\u308A\u307E\u305B\u3093\uFF01
 assertion_code_resp=\u5FDC\u7B54\u30B3\u30FC\u30C9
 assertion_contains=\u542B\u3080
 assertion_matches=\u4E00\u81F4\u3059\u308B
 assertion_message_resp=\u5FDC\u7B54\u30E1\u30C3\u30BB\u30FC\u30B8
 assertion_not=\u5426\u5B9A
 assertion_pattern_match_rules=\u30D1\u30BF\u30FC\u30F3\u30DE\u30C3\u30C1\u30F3\u30B0\u30EB\u30FC\u30EB
 assertion_patterns_to_test=\u30C6\u30B9\u30C8\u30D1\u30BF\u30FC\u30F3
 assertion_resp_field=\u30C6\u30B9\u30C8\u3059\u308B\u30EC\u30B9\u30DD\u30F3\u30B9\u30D5\u30A3\u30FC\u30EB\u30C9
 assertion_text_resp=\u30C6\u30AD\u30B9\u30C8\u306E\u30EC\u30B9\u30DD\u30F3\u30B9
 assertion_textarea_label=\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3\:
 assertion_title=\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3
 assertion_url_samp=\u30B5\u30F3\u30D7\u30EA\u30F3\u30B0\u3055\u308C\u305F URL
 assertion_visualizer_title=\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3 \u7D50\u679C
 auth_base_url=\u57FA\u5E95URL
 auth_manager_title=HTTP \u8A8D\u8A3C\u30DE\u30CD\u30FC\u30B8\u30E3
 auths_stored=\u8A8D\u8A3C\u30DE\u30CD\u30FC\u30B8\u30E3\u306B\u4FDD\u5B58\u3055\u308C\u3066\u3044\u308B\u8A8D\u8A3C
 browse=\u53C2\u7167...
 bsf_sampler_title=BSF\u30B5\u30F3\u30D7\u30E9\u30FC
 bsf_script=\u5B9F\u884C\u3059\u308B\u30B9\u30AF\u30EA\u30D7\u30C8
 bsf_script_file=\u5B9F\u884C\u3059\u308B\u30B9\u30AF\u30EA\u30D7\u30C8\u30D5\u30A1\u30A4\u30EB
 bsf_script_language=\u30B9\u30AF\u30EA\u30D7\u30C8\u8A00\u8A9E\:
 bsf_script_parameters=\u30B9\u30AF\u30EA\u30D7\u30C8/\u30D5\u30A1\u30A4\u30EB\u3078\u6E21\u3059\u30D1\u30E9\u30E1\u30FC\u30BF\:
 bsh_assertion_script=\u30B9\u30AF\u30EA\u30D7\u30C8(Response[Data|Code|Message|Headers], RequestHeaders, Sample[Label|rData], Result, Failure[Message])
 bsh_assertion_title=BeanShell\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3
 bsh_function_expression=\u8A55\u4FA1\u5BFE\u8C61\u306E\u5F0F
 bsh_sampler_title=BeanShell\u30B5\u30F3\u30D7\u30E9\u30FC
 bsh_script=\u30B9\u30AF\u30EA\u30D7\u30C8 (variables\: SampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName)
 bsh_script_file=\u30B9\u30AF\u30EA\u30D7\u30C8\u30D5\u30A1\u30A4\u30EB
 bsh_script_parameters=\u30D1\u30E9\u30E1\u30FC\u30BF\uFF08-> String Parameters and String []bsh.args\uFF09
 busy_testing=\u73FE\u5728\u30C6\u30B9\u30C8\u4E2D\u3067\u3059\u3002\u8A2D\u5B9A\u5909\u66F4\u306E\u524D\u306B\u30C6\u30B9\u30C8\u3092\u505C\u6B62\u3057\u3066\u304F\u3060\u3055\u3044\u3002
 cancel=\u30AD\u30E3\u30F3\u30BB\u30EB
 cancel_exit_to_save=\u4FDD\u5B58\u3055\u308C\u3066\u3044\u306A\u3044\u30C6\u30B9\u30C8\u9805\u76EE\u304C\u3042\u308A\u307E\u3059\u3002\u7D42\u4E86\u3059\u308B\u524D\u306B\u4FDD\u5B58\u3057\u307E\u3059\u304B\uFF1F
 cancel_new_to_save=\u4FDD\u5B58\u3055\u308C\u3066\u3044\u306A\u3044\u30C6\u30B9\u30C8\u9805\u76EE\u304C\u3042\u308A\u307E\u3059\u3002\u30C6\u30B9\u30C8\u8A08\u753B\u3092\u6D88\u53BB\u3059\u308B\u524D\u306B\u4FDD\u5B58\u3057\u307E\u3059\u304B\uFF1F
 choose_function=\u95A2\u6570\u306E\u9078\u629E
 choose_language=\u8A00\u8A9E\u306E\u9078\u629E
 clear=\u6D88\u53BB
 clear_all=\u5168\u3066\u6D88\u53BB
 clear_cookies_per_iter=\u7E70\u308A\u8FD4\u3057\u3054\u3068\u306B\u30AF\u30C3\u30AD\u30FC\u3092\u7834\u68C4\u3057\u307E\u3059\u304B\uFF1F
 column_delete_disallowed=\u3053\u306E\u30AB\u30E9\u30E0\u306E\u524A\u9664\u6A29\u9650\u304C\u3042\u308A\u307E\u305B\u3093
 column_number=CSV\u30D5\u30A1\u30A4\u30EB\u306E\u30AB\u30E9\u30E0\u756A\u53F7
 config_element=\u8A2D\u5B9A\u30A8\u30EC\u30E1\u30F3\u30C8
 constant_throughput_timer_memo=\u4E00\u5B9A\u306E\u30B9\u30EB\u30FC\u30D7\u30C3\u30C8\u306B\u5230\u9054\u3057\u305F\u3089\u30B5\u30F3\u30D7\u30EA\u30F3\u30B0\u9593\u306B\u9045\u5EF6\u3092\u8FFD\u52A0
 constant_timer_delay=\u30B9\u30EC\u30C3\u30C9\u9045\u5EF6\u6642\u9593 (\u30DF\u30EA\u79D2)\:
 constant_timer_memo=\u30B5\u30F3\u30D7\u30EA\u30F3\u30B0\u9593\u306B\u4E00\u5B9A\u306E\u9045\u5EF6\u3092\u8FFD\u52A0
 constant_timer_title=\u5B9A\u6570\u30BF\u30A4\u30DE
 controller=\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 cookie_manager_title=HTTP \u30AF\u30C3\u30AD\u30FC\u30DE\u30CD\u30FC\u30B8\u30E3
 cookies_stored=\u30AF\u30C3\u30AD\u30FC\u30DE\u30CD\u30FC\u30B8\u30E3\u306B\u4FDD\u5B58\u3055\u308C\u3066\u3044\u308B\u30AF\u30C3\u30AD\u30FC
 copy=\u30B3\u30D4\u30FC
 counter_config_title=\u30AB\u30A6\u30F3\u30BF
 counter_per_user=\u5404\u30E6\u30FC\u30B6\u72EC\u7ACB\u306E\u30C8\u30E9\u30C3\u30AF\u30AB\u30A6\u30F3\u30BF
 csvread_file_file_name=\u5024\u3092\u8AAD\u307F\u8FBC\u3080CSV\u30D5\u30A1\u30A4\u30EB
 cut=\u30AB\u30C3\u30C8
 cut_paste_function=\u751F\u6210\u3055\u308C\u305F\u95A2\u6570\u6587\u5B57\u5217\u3092\u30B3\u30D4\u30FC\u3057\u8CBC\u308A\u4ED8\u3051\u3066\u304F\u3060\u3055\u3044\u3002
 database_sql_query_string=SQL \u30AF\u30A8\u30EA\u30FC\u6587\u5B57\u5217\:
 database_sql_query_title=JDBC SQL \u30AF\u30A8\u30EA\u30FC\u521D\u671F\u5024\u8A2D\u5B9A
 de=\u30C9\u30A4\u30C4\u8A9E
 default_parameters=\u30C7\u30D5\u30A9\u30EB\u30C8\u30D1\u30E9\u30E1\u30FC\u30BF
 default_value_field=\u521D\u671F\u5024\uFF1A
 delay=\u8D77\u52D5\u9045\u5EF6\uFF08\u79D2\uFF09
 delete=\u524A\u9664
 delete_parameter=\u5909\u6570\u306E\u524A\u9664
 delete_test=\u30C6\u30B9\u30C8\u306E\u524A\u9664
 delete_user=\u30E6\u30FC\u30B6\u30FC\u306E\u524A\u9664
 disable=\u7121\u52B9
 dn=\uFF24\uFF2E
 domain=\u30C9\u30E1\u30A4\u30F3
 duration=\u6301\u7D9A\u6642\u9593\uFF08\u79D2\uFF09
 duration_assertion_duration_test=\u30A2\u30B5\u30FC\u30C8\u306E\u6301\u7D9A
 duration_assertion_failure=\u64CD\u4F5C\u306B\u6642\u9593\u304C\u304B\u304B\u308A\u3059\u304E\u307E\u3057\u305F\:{0}\u30DF\u30EA\u79D2\u304B\u304B\u308A\u307E\u3057\u305F\u304C\u3001{1}\u30DF\u30EA\u79D2\u3088\u308A\u3082\u9577\u304F\u304B\u304B\u308B\u3079\u304D\u3067\u306F\u3042\u308A\u307E\u305B\u3093\u3002
 duration_assertion_input_error=\u59A5\u5F53\u306A\u6B63\u306E\u6574\u6570\u3092\u5165\u529B\u3057\u3066\u304F\u3060\u3055\u3044\u3002
 duration_assertion_label=\u6301\u7D9A\u6642\u9593(\u30DF\u30EA\u79D2)
 duration_assertion_title=\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3\u306E\u6301\u7D9A
 edit=\u7DE8\u96C6
 email_results_title=\u7D50\u679C\u3092\u30E1\u30FC\u30EB\u3067\u9001\u4FE1
 en=\u82F1\u8A9E
 enable=\u6709\u52B9
 encoded_value=URL\u30A8\u30F3\u30B3\u30FC\u30C9\u5024
 endtime=\u7D42\u4E86\u6642\u523B
 entry_dn=\u30A8\u30F3\u30C8\u30EADN
 error_loading_help=\u30D8\u30EB\u30D7\u30DA\u30FC\u30B8\u30ED\u30FC\u30C9\u4E2D\u306E\u30A8\u30E9\u30FC
 error_occurred=\u30A8\u30E9\u30FC\u304C\u767A\u751F
 example_data=\u30B5\u30F3\u30D7\u30EB\u30C7\u30FC\u30BF
 example_title=Example\u30B5\u30F3\u30D7\u30E9\u30FC
 exit=\u7D42\u4E86
 expiration=\u671F\u9650
 field_name=\u30D5\u30A3\u30FC\u30EB\u30C9\u540D
 file=\u30D5\u30A1\u30A4\u30EB
 file_already_in_use=\u305D\u306E\u30D5\u30A1\u30A4\u30EB\u306F\u3059\u3067\u306B\u4F7F\u7528\u4E2D\u3067\u3059\u3002
 file_visualizer_append=\u65E2\u306B\u5B58\u5728\u3059\u308B\u30C7\u30FC\u30BF\u30D5\u30A1\u30A4\u30EB\u3078\u8FFD\u52A0
 file_visualizer_auto_flush=\u5404\u30C7\u30FC\u30BF\u3092\u30B5\u30F3\u30D7\u30EA\u30F3\u30B0\u3057\u305F\u3042\u3068\u306B\u81EA\u52D5\u7684\u306B\u66F8\u51FA\u3057
 file_visualizer_browse=\u53C2\u7167...
 file_visualizer_close=\u9589\u3058\u308B
 file_visualizer_file_options=\u30D5\u30A1\u30A4\u30EB\u30AA\u30D7\u30B7\u30E7\u30F3
 file_visualizer_filename=\u30D5\u30A1\u30A4\u30EB\u540D
 file_visualizer_flush=\u66F8\u51FA\u3057
 file_visualizer_missing_filename=\u51FA\u529B\u30D5\u30A1\u30A4\u30EB\u304C\u6307\u5B9A\u3055\u308C\u3066\u3044\u307E\u305B\u3093\u3002
 file_visualizer_open=\u958B\u304F
 file_visualizer_output_file=\u5168\u3066\u306E\u30C7\u30FC\u30BF\u3092\u30D5\u30A1\u30A4\u30EB\u306B\u51FA\u529B
 file_visualizer_submit_data=\u9001\u4FE1\u30C7\u30FC\u30BF\u3092\u542B\u307E\u305B\u308B
 file_visualizer_title=\u30D5\u30A1\u30A4\u30EB\u30EC\u30DD\u30FC\u30BF
 file_visualizer_verbose=\u8A73\u7D30\u306A\u30E1\u30C3\u30BB\u30FC\u30B8\u3092\u51FA\u529B
 filename=\u30D5\u30A1\u30A4\u30EB\u540D
 follow_redirects=\u30EA\u30C0\u30A4\u30EC\u30AF\u30C8\u306B\u5BFE\u5FDC
 follow_redirects_auto=\u81EA\u52D5\u30EA\u30C0\u30A4\u30EC\u30AF\u30C8
 foreach_controller_title=ForEach\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 foreach_input=Input\u5909\u6570\u540D\u63A5\u982D\u8F9E
 foreach_output=Output\u5909\u6570\u540D
 ftp_sample_title=FTP \u30EA\u30AF\u30A8\u30B9\u30C8\u521D\u671F\u5024\u8A2D\u5B9A
 ftp_testing_title=FTP \u30EA\u30AF\u30A8\u30B9\u30C8
 function_dialog_menu_item=\u95A2\u6570\u30D8\u30EB\u30D1\u30FC\u30C0\u30A4\u30A2\u30ED\u30B0
 function_helper_title=\u95A2\u6570\u30D8\u30EB\u30D1\u30FC
 function_name_param=\u95A2\u6570\u540D\u3002\u30C6\u30B9\u30C8\u8A08\u753B\u3067\u4F7F\u7528\u3059\u308B\u5024\u3092\u4FDD\u6301\u3059\u308B\u306E\u306B\u4F7F\u7528\u3055\u308C\u307E\u3059\u3002
 function_params=\u95A2\u6570\u30D1\u30E9\u30E1\u30FC\u30BF
 functional_mode=Functional \u30C6\u30B9\u30C8\u30E2\u30FC\u30C9
 functional_mode_explanation=\u5404\u30EA\u30AF\u30A8\u30B9\u30C8\u306B\u5BFE\u3059\u308B\u30B5\u30FC\u30D0\u30FC\u306E\u5FDC\u7B54\u3092\n\u30D5\u30A1\u30A4\u30EB\u3078\u66F8\u304D\u8FBC\u307F\u305F\u3044\u5834\u5408\u306E\u307FFunctional \u30C6\u30B9\u30C8\u30E2\u30FC\u30C9\u3092\u9078\u629E\u3057\u3066\u4E0B\u3055\u3044\u3002\n\n\u3053\u306E\u30AA\u30D7\u30B7\u30E7\u30F3\u3092\u9078\u3093\u3060\u3068\u304D\u306E\u6027\u80FD\u306B\u5BFE\u3059\u308B\u5F71\u97FF\u306B\u7559\u610F\u3057\u3066\u304F\u3060\u3055\u3044\u3002
 gaussian_timer_delay=\u9045\u5EF6\u6642\u9593\u30AA\u30D5\u30BB\u30C3\u30C8\u5B9A\u6570 (\u30DF\u30EA\u79D2)\:
 gaussian_timer_memo=\u30AC\u30A6\u30B9\u5206\u5E03\u306B\u3088\u308B\u30E9\u30F3\u30C0\u30E0\u306A\u9045\u5EF6\u3092\u8FFD\u52A0
 gaussian_timer_range=\u504F\u5DEE (\u30DF\u30EA\u79D2)\:
 gaussian_timer_title=\u30AC\u30A6\u30B9\u4E71\u6570\u30BF\u30A4\u30DE
 generate=\u751F\u6210
 generator=\u751F\u6210\u30AF\u30E9\u30B9\u540D
 generator_cnf_msg=\u751F\u6210\u30AF\u30E9\u30B9\u304C\u898B\u3064\u304B\u308A\u307E\u305B\u3093\u3002/lib\u30C7\u30A3\u30EC\u30AF\u30C8\u30EA\u306B\u751F\u6210\u30AF\u30E9\u30B9\u3092\u542B\u3080jar\u30D5\u30A1\u30A4\u30EB\u304C\u3042\u308B\u3053\u3068\u3092\u78BA\u8A8D\u3057\u3066\u4E0B\u3055\u3044\u3002
 generator_illegal_msg=IllegalAcessException\u306B\u3088\u308A\u751F\u6210\u30AF\u30E9\u30B9\u3078\u30A2\u30AF\u30BB\u30B9\u3067\u304D\u307E\u305B\u3093\u3067\u3057\u305F\u3002
 generator_instantiate_msg=\u751F\u6210\u30D1\u30FC\u30B5\u306E\u30A4\u30F3\u30B9\u30BF\u30F3\u30B9\u3092\u4F5C\u6210\u3067\u304D\u307E\u305B\u3093\u3067\u3057\u305F\u3002Generator\u30A4\u30F3\u30BF\u30D5\u30A7\u30FC\u30B9\u3092\u5B9F\u88C5\u3059\u308B\u751F\u6210\u30AF\u30E9\u30B9\u3092\u78BA\u8A8D\u3057\u3066\u4E0B\u3055\u3044\u3002
 graph_choose_graphs=\u8868\u793A\u3059\u308B\u30B0\u30E9\u30D5
 graph_full_results_title=\u7D50\u679C\u3092\u30B0\u30E9\u30D5\u8868\u793A(\u8A73\u7D30)
 graph_results_average=\u5E73\u5747
 graph_results_data=\u30C7\u30FC\u30BF
 graph_results_deviation=\u504F\u5DEE
 graph_results_latest_sample=\u6700\u65B0\u306E\u30B5\u30F3\u30D7\u30EB
 graph_results_median=\u4E2D\u592E\u5024
 graph_results_ms=\u30DF\u30EA\u79D2(ms)
 graph_results_no_samples=\u30B5\u30F3\u30D7\u30EB\u6570
 graph_results_throughput=\u30B9\u30EB\u30FC\u30D7\u30C3\u30C8
 graph_results_title=\u30B0\u30E9\u30D5\u8868\u793A
 grouping_add_separators=\u30B0\u30EB\u30FC\u30D7\u9593\u306B\u30BB\u30D1\u30EC\u30FC\u30BF\u3092\u8FFD\u52A0
 grouping_in_controllers=\u65B0\u898F\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9\u3078\u5404\u30B0\u30EB\u30FC\u30D7\u3092\u7F6E\u304F
 grouping_mode=\u30B0\u30EB\u30FC\u30D7\u306B\u3059\u308B\:
 grouping_no_groups=\u30B5\u30F3\u30D7\u30E9\u30FC\u3092\u30B0\u30EB\u30FC\u30D7\u306B\u3057\u306A\u3044
 grouping_store_first_only=\u5404\u30B0\u30EB\u30FC\u30D7\u306E\u6700\u521D\u306E\u30B5\u30F3\u30D7\u30E9\u30FC\u3060\u3051\u4FDD\u5B58
 header_manager_title=HTTP \u30D8\u30C3\u30C0\u30DE\u30CD\u30FC\u30B8\u30E3
 headers_stored=\u30D8\u30C3\u30C0\u30FC\u30DE\u30CD\u30FC\u30B8\u30E3\u306B\u4FDD\u5B58\u3055\u308C\u3066\u3044\u308B\u30D8\u30C3\u30C0
 help=\u30D8\u30EB\u30D7
 http_response_code=HTTP\u5FDC\u7B54\u30B3\u30FC\u30C9
 http_url_rewriting_modifier_title=HTTP URL-Rewriting \u4FEE\u98FE\u5B50
 http_user_parameter_modifier=HTTP\u30E6\u30FC\u30B6\u30FC\u30D1\u30E9\u30E1\u30FC\u30BF\u306E\u5909\u66F4
 id_prefix=ID\u63A5\u982D\u8F9E
 id_suffix=ID \u63A5\u5C3E\u8F9E
 if_controller_label=\u6761\u4EF6
 if_controller_title=If \u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 ignore_subcontrollers=\u30B5\u30D6\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9\u30D6\u30ED\u30C3\u30AF\u3092\u7121\u8996
 include_equals=\u7B49\u53F7\u542B\u3080\uFF1F
 increment=\u5897\u5206
 infinite=\u7121\u9650\u30EB\u30FC\u30D7
 insert_after=\u5F8C\u3078\u633F\u5165
 insert_before=\u524D\u3078\u633F\u5165
 insert_parent=\u4E0A\u306E\u968E\u5C64\u306B\u633F\u5165
 interleave_control_title=\u30A4\u30F3\u30BF\u30EA\u30FC\u30D6\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 intsum_param_1=\u6700\u521D\u306B\u8FFD\u52A0\u3059\u308Bint
 intsum_param_2=\uFF12\u56DE\u76EE\u306B\u8FFD\u52A0\u3059\u308Bint - \u3055\u3089\u306A\u308B\u5F15\u6570\u3092\u8FFD\u52A0\u3057\u3066\u5408\u8A08\u304C\u8A08\u7B97\u3055\u308C\u308B
 invalid_data=\u9069\u5207\u3067\u306A\u3044\u30C7\u30FC\u30BF
 invalid_mail_server=\u4E0D\u660E\u306A\u30E1\u30FC\u30EB\u30B5\u30FC\u30D0\u30FC\u3067\u3059\u3002
 iteration_counter_arg_1=TRUE, \u30E6\u30FC\u30B6\u30FC\u6BCE\u306B\u30AB\u30A6\u30F3\u30BF\u30FC\u3092\u6301\u3064\u3053\u3068\u304C\u3067\u304D\u308B, FALSE \u30B0\u30ED\u30FC\u30D0\u30EB\u30AB\u30A6\u30F3\u30BF\u30FC\u3068\u306A\u308B
 iterator_num=\u30EB\u30FC\u30D7\u56DE\u6570\:
 java_request=Java \u30EA\u30AF\u30A8\u30B9\u30C8
 java_request_defaults=Java \u30EA\u30AF\u30A8\u30B9\u30C8\u521D\u671F\u5024\u8A2D\u5B9A
 jndi_config_title=JNDI \u8A2D\u5B9A
 jndi_lookup_name=\u30EA\u30E2\u30FC\u30C8\u30A4\u30F3\u30BF\u30D5\u30A7\u30FC\u30B9
 jndi_lookup_title=JNDI \u30EB\u30C3\u30AF\u30A2\u30C3\u30D7\u8A2D\u5B9A
 jndi_method_button_invoke=\u547C\u3073\u51FA\u3057
 jndi_method_button_reflect=\u30EA\u30D5\u30EC\u30AF\u30C8
 jndi_method_home_name=\u30DB\u30FC\u30E0\u30E1\u30BD\u30C3\u30C9\u540D
 jndi_method_home_parms=\u30DB\u30FC\u30E0\u30E1\u30BD\u30C3\u30C9\u306E\u5F15\u6570
 jndi_method_name=\u30E1\u30BD\u30C3\u30C9\u8A2D\u5B9A
 jndi_method_remote_interface_list=\u30EA\u30E2\u30FC\u30C8\u30A4\u30F3\u30BF\u30D5\u30A7\u30FC\u30B9
 jndi_method_remote_name=\u30EA\u30E2\u30FC\u30C8\u30E1\u30BD\u30C3\u30C9\u540D
 jndi_method_remote_parms=\u30EA\u30E2\u30FC\u30C8\u30E1\u30BD\u30C3\u30C9\u306E\u5F15\u6570
 jndi_method_title=\u30EA\u30E2\u30FC\u30C8\u30E1\u30BD\u30C3\u30C9\u8A2D\u5B9A
 jndi_testing_title=JNDI \u30EA\u30AF\u30A8\u30B9\u30C8
 jndi_url_jndi_props=JNDI \u30D7\u30ED\u30D1\u30C6\u30A3
 ja=\u65E5\u672C\u8A9E
 ldap_sample_title=LDAP\u30EA\u30AF\u30A8\u30B9\u30C8\u521D\u671F\u5024\u8A2D\u5B9A
 ldap_testing_title=LDAP\u30EA\u30AF\u30A8\u30B9\u30C8
 load=\u8AAD\u8FBC
 log_errors_only=\u30ED\u30B0\u30A8\u30E9\u30FC\u306E\u307F
 log_file=\u30ED\u30B0\u30D5\u30A1\u30A4\u30EB\u306E\u5834\u6240
 log_parser=Log\u30D1\u30FC\u30B5\u30AF\u30E9\u30B9\u540D
 log_parser_cnf_msg=\u30AF\u30E9\u30B9\u304C\u898B\u3064\u304B\u308A\u307E\u305B\u3093\u3002/lib\u30C7\u30A3\u30EC\u30AF\u30C8\u30EA\u306B\u30AF\u30E9\u30B9\u3092\u542B\u3080jar\u30D5\u30A1\u30A4\u30EB\u304C\u3042\u308B\u3053\u3068\u3092\u78BA\u8A8D\u3057\u3066\u4E0B\u3055\u3044\u3002
 log_parser_illegal_msg=IllegalAcessException\u306B\u3088\u308A\u30AF\u30E9\u30B9\u3078\u30A2\u30AF\u30BB\u30B9\u3067\u304D\u307E\u305B\u3093\u3067\u3057\u305F\u3002
 log_parser_instantiate_msg=\u30ED\u30B0\u30D1\u30FC\u30B5\u306E\u30A4\u30F3\u30B9\u30BF\u30F3\u30B9\u3092\u4F5C\u6210\u3067\u304D\u307E\u305B\u3093\u3067\u3057\u305F\u3002LogParser\u30A4\u30F3\u30BF\u30D5\u30A7\u30FC\u30B9\u3092\u5B9F\u88C5\u3059\u308B\u30D1\u30FC\u30B5\u30AF\u30E9\u30B9\u3092\u78BA\u8A8D\u3057\u3066\u4E0B\u3055\u3044\u3002
 log_sampler=Tomcat\u30A2\u30AF\u30BB\u30B9\u30ED\u30B0\u30B5\u30F3\u30D7\u30E9\u30FC
 logic_controller_title=\u30B7\u30F3\u30D7\u30EB\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 login_config=\u30ED\u30B0\u30A4\u30F3\u8A2D\u5B9A
 login_config_element=\u30ED\u30B0\u30A4\u30F3\u8A2D\u5B9A\u30A8\u30EC\u30E1\u30F3\u30C8
 loop_controller_title=\u30EB\u30FC\u30D7\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 looping_control=\u30EB\u30FC\u30D7\u30B3\u30F3\u30C8\u30ED\u30FC\u30EB
 lower_bound=\u4E0B\u9650
 mailer_attributes_panel=\u30E1\u30FC\u30EB\u306E\u5C5E\u6027
 mailer_error=\u30E1\u30FC\u30EB\u3092\u9001\u4FE1\u3067\u304D\u307E\u305B\u3093\u3067\u3057\u305F\u3002\u5165\u529B\u5185\u5BB9\u306B\u9593\u9055\u3044\u304C\u306A\u3044\u304B\u78BA\u8A8D\u3057\u3066\u304F\u3060\u3055\u3044\u3002
 mailer_visualizer_title=\u30E1\u30FC\u30E9\u30FC\u30D3\u30B8\u30E5\u30A2\u30E9\u30A4\u30B6
 match_num_field=\u4E00\u81F4\u756A\u53F7\uFF080\u304B\u3089\u4E71\u6570\uFF09\uFF1A
 max=\u6700\u5927\u5024
 maximum_param=\u5024\u57DF\u306E\u6700\u5927\u5024
 md5hex_assertion_failure=MD5 sum \u30A2\u30B5\u30FC\u30C8\u30A8\u30E9\u30FC \:  \u7D50\u679C\u306F {0} \u3067\u3057\u305F\u304C\u3001{1}\u3067\u306A\u3051\u308C\u3070\u306A\u308A\u307E\u305B\u3093\u3002
 md5hex_assertion_md5hex_test=\u30A2\u30B5\u30FC\u30C8\u5BFE\u8C61\u306EMD5Hex
 md5hex_assertion_title=MD5Hex\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3
 menu_assertions=\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3
-menu_close=\u9589\u3058\u308B
 menu_config_element=\u8A2D\u5B9A\u30A8\u30EC\u30E1\u30F3\u30C8
 menu_edit=\u7DE8\u96C6
 menu_generative_controller=\u30B5\u30F3\u30D7\u30E9\u30FC
 menu_listener=\u30EA\u30B9\u30CA\u30FC
 menu_logic_controller=\u30ED\u30B8\u30C3\u30AF\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 menu_merge=\u4F75\u5408\uFF08\u30DE\u30FC\u30B8\uFF09
 menu_modifiers=\u4FEE\u98FE\u5B50
 menu_non_test_elements=Non-Test\u30A8\u30EC\u30E1\u30F3\u30C8
 menu_open=\u958B\u304F
 menu_post_processors=\u5F8C\u51E6\u7406
 menu_pre_processors=\u524D\u51E6\u7406
 menu_response_based_modifiers=\u30EC\u30B9\u30DD\u30F3\u30B9\u57FA\u6E96\u306E\u4FEE\u98FE\u5B50
 menu_timer=\u30BF\u30A4\u30DE
 metadata=\u30E1\u30BF\u30C7\u30FC\u30BF
 method=\u30E1\u30BD\u30C3\u30C9\:
 mimetype=Mime\u30BF\u30A4\u30D7
 minimum_param=\u5024\u57DF\u306E\u6700\u5C0F\u5024
 minute=\u5206
 modification_controller_title=\u5909\u66F4\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 modification_manager_title=\u5909\u66F4\u30DE\u30CD\u30FC\u30B8\u30E3
 modify_test=\u30C6\u30B9\u30C8\u306E\u5909\u66F4
 module_controller_title=\u30E2\u30B8\u30E5\u30FC\u30EB\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 name=\u540D\u524D\:
 new=\u65B0\u898F
 no=\u30CE\u30EB\u30A6\u30A7\u30FC\u8A9E
 number_of_threads=\u30B9\u30EC\u30C3\u30C9\u6570\:
 once_only_controller_title=\u4E00\u5EA6\u3060\u3051\u5B9F\u884C\u3055\u308C\u308B\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 open=\u958B\u304F...
 option=\u30AA\u30D7\u30B7\u30E7\u30F3
 optional_tasks=\u30AA\u30D7\u30B7\u30E7\u30F3\u30BF\u30B9\u30AF
 paramtable=\u30EA\u30AF\u30A8\u30B9\u30C8\u3067\u9001\u308B\u30D1\u30E9\u30E1\u30FC\u30BF\:
 password=\u30D1\u30B9\u30EF\u30FC\u30C9
 paste=\u30DA\u30FC\u30B9\u30C8
 paste_insert=\u633F\u5165\u3068\u3057\u3066\u30DA\u30FC\u30B9\u30C8
 path=\u30D1\u30B9\:
 path_extension_choice=\u30D1\u30B9\u306E\u62E1\u5F35(\u533A\u5207\u308A\u306B\u306F";"\u3092\u4F7F\u3063\u3066\u304F\u3060\u3055\u3044)
 path_extension_dont_use_equals=\u30D1\u30B9\u306E\u62E1\u5F35\u306B\u7B49\u53F7\u3092\u4F7F\u308F\u306A\u3044\uFF08Intershop Enfinity \u4E92\u63DB\u306E\u305F\u3081\uFF09
 patterns_to_exclude=\u9664\u5916\u3059\u308B\u30D1\u30BF\u30FC\u30F3
 patterns_to_include=\u633F\u5165\u3059\u308B\u30D1\u30BF\u30FC\u30F3
 port=\u30DD\u30FC\u30C8\:
 property_default_param=\u521D\u671F\u5024
 property_edit=\u7DE8\u96C6
 property_editor.value_is_invalid_message=\u5165\u529B\u3055\u308C\u305F\u30C6\u30AD\u30B9\u30C8\u306F\u3053\u306E\u30D7\u30ED\u30D1\u30C6\u30A3\u306B\u9069\u3057\u3066\u3044\u307E\u305B\u3093\u3002\n\u5143\u306E\u5024\u306B\u623B\u3057\u307E\u3059\u3002
 property_editor.value_is_invalid_title=\u9069\u5207\u3067\u306A\u3044\u5165\u529B
 property_name_param=\u30D7\u30ED\u30D1\u30C6\u30A3\u540D
 property_undefined=\u5B9A\u7FA9\u3055\u308C\u3066\u3044\u306A\u3044
 protocol=\u30D7\u30ED\u30C8\u30B3\u30EB\:
 protocol_java_border=Java \u30AF\u30E9\u30B9
 protocol_java_classname=\u30AF\u30E9\u30B9\u540D\:
 protocol_java_config_tile=Java \u30B5\u30F3\u30D7\u30EB\u306E\u8A2D\u5B9A
 protocol_java_test_title=Java \u30C6\u30B9\u30C8
 proxy_assertions=\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3\u306E\u8FFD\u52A0
 proxy_cl_error=\u30D7\u30ED\u30AD\u30B7\u30FC\u30B5\u30FC\u30D0\u30FC\u304C\u6307\u5B9A\u3055\u308C\u3066\u3044\u308B\u5834\u5408\u306F\u3001\u30DB\u30B9\u30C8\u540D\u3068\u30DD\u30FC\u30C8\u3082\u6307\u5B9A\u3057\u3066\u304F\u3060\u3055\u3044\u3002
 proxy_headers=HTTP\u30D8\u30C3\u30C0\u306E\u53D6\u308A\u8FBC\u307F
 proxy_separators=\u30BB\u30D1\u30EC\u30FC\u30BF\u306E\u8FFD\u52A0
 proxy_target=\u5BFE\u8C61\u3068\u306A\u308B\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9\:
 proxy_title=HTTP \u30D7\u30ED\u30AD\u30B7\u30B5\u30FC\u30D0
 ramp_up=Ramp-Up \u671F\u9593 (\u79D2)\:
 random_control_title=\u4E71\u6570\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 random_order_control_title=\u30E9\u30F3\u30C0\u30E0\u9806\u5E8F\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 record_controller_title=\u8A18\u9332\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 ref_name_field=\u53C2\u7167\u540D\uFF1A
 regex_extractor_title=\u6B63\u898F\u8868\u73FE\u62BD\u51FA
 regex_field=\u6B63\u898F\u8868\u73FE\uFF1A
 regexfunc_param_1=\u76F4\u524D\u306E\u30EA\u30AF\u30A8\u30B9\u30C8\u7D50\u679C\u304B\u3089\u691C\u7D22\u3059\u308B\u305F\u3081\u306E\u6B63\u898F\u8868\u73FE\u3067\u3059\u3002
 regexfunc_param_2=\u6587\u5B57\u5217\u3092\u7F6E\u63DB\u3059\u308B\u305F\u3081\u306E\u30C6\u30F3\u30D7\u30EC\u30FC\u30C8\u3067\u3001\u6B63\u898F\u8868\u73FE\u306E\u30B0\u30EB\u30FC\u30D7\u5316\u3092\u4F7F\u7528\u3067\u304D\u307E\u3059\u3002\u66F8\u5F0F\u306F$[group]$\u3002\u4F8B\uFF09 $1$\u3002
 regexfunc_param_3=\u30DE\u30C3\u30C1\u30F3\u30B0\u3067\u4F7F\u7528\u3057\u307E\u3059\u30021\u4EE5\u4E0A\u306E\u6574\u6570\u3001RAND(JMeter\u304C\u30E9\u30F3\u30C0\u30E0\u306B\u9078\u629E\u3059\u308B)\u3001\u6D6E\u52D5\u5C0F\u6570\u70B9\u3001ALL(\u5168\u3066\u306B\u4E00\u81F4\u3059\u308B)\u3001\u306E\u3044\u305A\u308C\u304B\u3092\u6307\u5B9A\u3067\u304D\u307E\u3059\u3002
 regexfunc_param_4=\u30C6\u30AD\u30B9\u30C8\u306E\u7BC4\u56F2\u3067\u3059\u3002ALL\u304C\u9078\u629E\u3055\u308C\u305F\u5834\u5408\u3001\u7D50\u679C\u3092\u751F\u6210\u3059\u308B\u305F\u3081\u306B\u4F7F\u308F\u308C\u307E\u3059\u3002
 regexfunc_param_5=\u521D\u671F\u30C6\u30AD\u30B9\u30C8\u3067\u3059\u3002\u6B63\u898F\u8868\u73FE\u3068\u4E00\u81F4\u3059\u308B\u6587\u5B57\u5217\u304C\u306A\u304B\u3063\u305F\u5834\u5408\u306B\u30C6\u30F3\u30D7\u30EC\u30FC\u30C8\u306E\u4EE3\u308F\u308A\u3068\u3057\u3066\u4F7F\u7528\u3055\u308C\u307E\u3059\u3002
 remote_exit=\u7D42\u4E86(\u30EA\u30E2\u30FC\u30C8)
 remote_exit_all=\u5168\u3066\u7D42\u4E86(\u30EA\u30E2\u30FC\u30C8)
 remote_start=\u958B\u59CB(\u30EA\u30E2\u30FC\u30C8)
 remote_start_all=\u5168\u3066\u958B\u59CB(\u30EA\u30E2\u30FC\u30C8)
 remote_stop=\u505C\u6B62(\u30EA\u30E2\u30FC\u30C8)
 remote_stop_all=\u5168\u3066\u505C\u6B62(\u30EA\u30E2\u30FC\u30C8)
 remove=\u524A\u9664
 report=\u30EC\u30DD\u30FC\u30C8
 request_data=\u30EA\u30AF\u30A8\u30B9\u30C8\u30C7\u30FC\u30BF
 restart=\u30EA\u30B9\u30BF\u30FC\u30C8
 resultaction_title=\u30A2\u30AF\u30B7\u30E7\u30F3\u30CF\u30F3\u30C9\u30E9\u306E\u7D42\u4E86\u72B6\u614B
 resultsaver_prefix=\u30D5\u30A1\u30A4\u30EB\u540D\u306E\u63A5\u982D\u8F9E\:
 resultsaver_title=\u5FDC\u7B54\u3092\u30D5\u30A1\u30A4\u30EB\u3078\u4FDD\u5B58
 root=\u30EB\u30FC\u30C8
 root_title=\u30EB\u30FC\u30C8
 run=\u5B9F\u884C
 running_test=\u30C6\u30B9\u30C8\u5B9F\u884C\u4E2D
 sampler_on_error_action=\u30B5\u30F3\u30D7\u30E9\u30FC\u30A8\u30E9\u30FC\u5F8C\u306E\u30A2\u30AF\u30B7\u30E7\u30F3
 sampler_on_error_continue=\u7D9A\u884C
 sampler_on_error_stop_test=\u30C6\u30B9\u30C8\u505C\u6B62
 sampler_on_error_stop_thread=\u30B9\u30EC\u30C3\u30C9\u505C\u6B62
 save=\u30C6\u30B9\u30C8\u8A08\u753B\u3092\u4FDD\u5B58
 save?=\u4FDD\u5B58?
 save_all_as=\u30C6\u30B9\u30C8\u8A08\u753B\u306B\u540D\u524D\u3092\u3064\u3051\u3066\u4FDD\u5B58
 save_as=\u5225\u540D\u3067\u4FDD\u5B58...
 scheduler=\u30B9\u30B1\u30B8\u30E5\u30FC\u30E9
 scheduler_configuration=\u30B9\u30B1\u30B8\u30E5\u30FC\u30E9\u8A2D\u5B9A
 search_base=\u691C\u7D22\u57FA\u6E96
 search_filter=\u691C\u7D22\u30D5\u30A3\u30EB\u30BF
 search_test=\u30C6\u30B9\u30C8\u306E\u691C\u7D22
 second=\u79D2
 secure=\u30BB\u30AD\u30E5\u30A2
 send_file=\u30EA\u30AF\u30A8\u30B9\u30C8\u3068\u4E00\u7DD2\u306B\u9001\u4FE1\u3055\u308C\u308B\u30D5\u30A1\u30A4\u30EB\:
 send_file_browse=\u53C2\u7167...
 send_file_filename_label=\u30D5\u30A1\u30A4\u30EB\u540D\:
 send_file_mime_label=MIME \u30BF\u30A4\u30D7\:
 send_file_param_name_label=\u30D1\u30E9\u30E1\u30FC\u30BF\u540D\:
 server=\u30B5\u30FC\u30D0\u540D\u307E\u305F\u306F IP\:
 servername=\u30B5\u30FC\u30D0\u540D\uFF1A
 session_argument_name=\u30BB\u30C3\u30B7\u30E7\u30F3\u5F15\u6570\u540D
 shutdown=\u30B7\u30E3\u30C3\u30C8\u30C0\u30A6\u30F3
 simple_config_element=\u30B7\u30F3\u30D7\u30EB\u8A2D\u5B9A\u30A8\u30EC\u30E1\u30F3\u30C8
 simple_data_writer_title=\u30B7\u30F3\u30D7\u30EB\u30C7\u30FC\u30BF\u30E9\u30A4\u30BF
 size_assertion_comparator_error_equal=\u7B49\u3057\u3044
 size_assertion_comparator_error_greater=\u5927\u306A\u308A\u5C0F
 size_assertion_comparator_error_greaterequal=\u4EE5\u4E0A
 size_assertion_comparator_error_less=\u5C0F\u306A\u308A\u5927
 size_assertion_comparator_error_lessequal=\u4EE5\u4E0B
 size_assertion_comparator_error_notequal=\u7B49\u3057\u304F\u306A\u3044
 size_assertion_comparator_label=\u6BD4\u8F03\u306E\u578B
 size_assertion_failure=\u7D50\u679C\u306F\u6B63\u3057\u304F\u306A\u3044\u30B5\u30A4\u30BA\u3067\u3059\u3002\:{0}\u30D0\u30A4\u30C8\u3067\u3059\u304C\u3001{1}\u30D0\u30A4\u30C8\u304B{2}\u30D0\u30A4\u30C8\u3067\u306A\u3051\u308C\u3070\u306A\u308A\u307E\u305B\u3093\u3002
 size_assertion_input_error=\u9069\u5207\u306A\u6B63\u306E\u6574\u6570\u3092\u5165\u529B\u3057\u3066\u304F\u3060\u3055\u3044\u3002
 size_assertion_label=\u30D0\u30A4\u30C8\u30B5\u30A4\u30BA\:
 size_assertion_size_test=\u30A2\u30B5\u30FC\u30C8\u306E\u30B5\u30A4\u30BA
 size_assertion_title=\u30B5\u30A4\u30BA\u30A2\u30B5\u30FC\u30B7\u30E7\u30F3
 soap_action=Soap\u30A2\u30AF\u30B7\u30E7\u30F3
 soap_data_title=Soap/XML-RPC \u30C7\u30FC\u30BF
 soap_sampler_title=Soap/XML-RPC\u30EA\u30AF\u30A8\u30B9\u30C8 
 ssl_alias_prompt=\u5B9A\u7FA9\u6E08\u307F\u306E\u30A8\u30A4\u30EA\u30A2\u30B9\u3092\u5165\u529B\u3057\u3066\u4E0B\u3055\u3044\u3002
 ssl_alias_select=\u30C6\u30B9\u30C8\u3059\u308B\u30A8\u30A4\u30EA\u30A2\u30B9\u3092\u9078\u629E\u3057\u3066\u4E0B\u3055\u3044\u3002
 ssl_alias_title=\u30AF\u30E9\u30A4\u30A2\u30F3\u30C8\u30A8\u30A4\u30EA\u30A2\u30B9
 ssl_error_title=\u30AD\u30FC\u30B9\u30C8\u30A2\u30A8\u30E9\u30FC
 ssl_pass_prompt=\u30D1\u30B9\u30EF\u30FC\u30C9\u3092\u5165\u529B\u3057\u3066\u4E0B\u3055\u3044\u3002
 ssl_pass_title=\u30AD\u30FC\u30B9\u30C8\u30A2\u30D1\u30B9\u30EF\u30FC\u30C9
 ssl_port=SSL\u30DD\u30FC\u30C8
 sslmanager=SSL \u30DE\u30CD\u30FC\u30B8\u30E3
 start=\u958B\u59CB
 starttime=\u958B\u59CB\u6642\u523B
 stop=\u505C\u6B62
 stopping_test=\u3059\u3079\u3066\u306E\u30C6\u30B9\u30C8\u7528\u30B9\u30EC\u30C3\u30C9\u3092\u505C\u6B62\u4E2D\u3067\u3059\u3002\u3057\u3070\u3089\u304F\u304A\u5F85\u3061\u304F\u3060\u3055\u3044\u3002
 stopping_test_title=\u30C6\u30B9\u30C8\u306E\u505C\u6B62\u4E2D
 string_from_file_file_name=\u30D5\u30A1\u30A4\u30EB\u306E\u30D5\u30EB\u30D1\u30B9\u3092\u5165\u529B\u3057\u3066\u304F\u3060\u3055\u3044
 string_from_file_seq_final=\u6700\u7D42\u30D5\u30A1\u30A4\u30EB\u30B7\u30FC\u30B1\u30F3\u30B9\u756A\u53F7
 string_from_file_seq_start=\u958B\u59CB\u30D5\u30A1\u30A4\u30EB\u30B7\u30FC\u30B1\u30F3\u30B9\u756A\u53F7
 summariser_title=\u7D50\u679C\u306E\u6982\u8981\u3092\u751F\u6210
 tcp_config_title=TCP\u30B5\u30F3\u30D7\u30E9\u30FC\u8A2D\u5B9A
 tcp_nodelay=\u9045\u5EF6\u306A\u3057\u3092\u8A2D\u5B9A
 tcp_port=\u30DD\u30FC\u30C8\u756A\u53F7\:
 tcp_request_data=\u9001\u4FE1\u3059\u308B\u30C6\u30AD\u30B9\u30C8
 tcp_sample_title=TCP\u30B5\u30F3\u30D7\u30E9\u30FC
 tcp_timeout=\u30BF\u30A4\u30E0\u30A2\u30A6\u30C8\:
 template_field=\u30C6\u30F3\u30D7\u30EC\u30FC\u30C8\uFF1A
 test=\u30C6\u30B9\u30C8
 test_configuration=\u30C6\u30B9\u30C8\u8A2D\u5B9A
 test_plan=\u30C6\u30B9\u30C8\u8A08\u753B
 testplan.serialized=\u5404\u30B9\u30EC\u30C3\u30C9\u30B0\u30EB\u30FC\u30D7\u3092\u5225\u3005\u306B\u5B9F\u884C
 testplan_comments=\u30B3\u30E1\u30F3\u30C8\:
 thread_delay_properties=\u30B9\u30EC\u30C3\u30C9\u9045\u5EF6\u6642\u9593\u30D7\u30ED\u30D1\u30C6\u30A3
 thread_group_title=\u30B9\u30EC\u30C3\u30C9\u30B0\u30EB\u30FC\u30D7
 thread_properties=\u30B9\u30EC\u30C3\u30C9\u30D7\u30ED\u30D1\u30C6\u30A3
 threadgroup=\u30B9\u30EC\u30C3\u30C9\u30B0\u30EB\u30FC\u30D7
 throughput_control_bynumber_label=\u5168\u4F53\u5B9F\u884C
 throughput_control_bypercent_label=\u30D1\u30FC\u30BB\u30F3\u30C8\u5B9F\u884C
 throughput_control_perthread_label=\u30E6\u30FC\u30B6\u30FC\u3054\u3068
 throughput_control_title=\u30B9\u30EB\u30FC\u30D7\u30C3\u30C8\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 throughput_control_tplabel=\u30B9\u30EB\u30FC\u30D7\u30C3\u30C8
 transaction_controller_title=\u30C8\u30E9\u30F3\u30B6\u30AF\u30B7\u30E7\u30F3\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9
 uniform_timer_delay=\u9045\u5EF6\u6642\u9593\u30AA\u30D5\u30BB\u30C3\u30C8\u5B9A\u6570 (\u30DF\u30EA\u79D2)\:
 uniform_timer_memo=\u4E00\u69D8\u5206\u5E03\u306B\u3088\u308B\u30E9\u30F3\u30C0\u30E0\u306A\u9045\u5EF6\u3092\u8FFD\u52A0
 uniform_timer_range=\u6700\u5927\u9045\u5EF6\u6642\u9593 (\u30DF\u30EA\u79D2)\:
 uniform_timer_title=\u4E00\u69D8\u4E71\u6570\u30BF\u30A4\u30DE
 update_per_iter=\u7E70\u308A\u8FD4\u3057\u3054\u3068\u306B\u66F4\u65B0
 upload=\u30D5\u30A1\u30A4\u30EB\u30A2\u30C3\u30D7\u30ED\u30FC\u30C9
 upper_bound=\u4E0A\u9650
 url_config_protocol=\u30D7\u30ED\u30C8\u30B3\u30EB\:
 url_config_title=HTTP \u30EA\u30AF\u30A8\u30B9\u30C8\u521D\u671F\u5024\u8A2D\u5B9A
 url_full_config_title=UrlFull \u30B5\u30F3\u30D7\u30EB
 url_multipart_config_title=HTTP\u30DE\u30EB\u30C1\u30D1\u30FC\u30C8\u30EA\u30AF\u30A8\u30B9\u30C8\u521D\u671F\u5024\u8A2D\u5B9A
 use_keepalive=KeepAlive \u3092\u6709\u52B9\u306B\u3059\u308B
 use_recording_controller=\u8A18\u9332\u30B3\u30F3\u30C8\u30ED\u30FC\u30E9\u306E\u4F7F\u7528
 user=\u30E6\u30FC\u30B6\u30FC
 user_defined_test=\u30E6\u30FC\u30B6\u30FC\u5B9A\u7FA9\u30C6\u30B9\u30C8
 user_defined_variables=\u30E6\u30FC\u30B6\u30FC\u5B9A\u7FA9\u5909\u6570
 user_param_mod_help_note=(\u5909\u66F4\u3057\u306A\u3044\u3067\u304F\u3060\u3055\u3044\u3002\u5909\u66F4\u3059\u308B\u5834\u5408\u306F\u3001JMeter\u306E/bin\u30C7\u30A3\u30EC\u30AF\u30C8\u30EA\u306B\u3042\u308B\u540C\u540D\u306E\u30D5\u30A1\u30A4\u30EB\u3092\u5909\u66F4\u3057\u3066\u304F\u3060\u3055\u3044\u3002)
 user_parameters_table=\u30D1\u30E9\u30E1\u30FC\u30BF
 user_parameters_title=\u30E6\u30FC\u30B6\u30FC\u30D1\u30E9\u30E1\u30FC\u30BF
 username=\u30E6\u30FC\u30B6\u30FC\u540D
 value=\u5024
 var_name=\u53C2\u7167\u540D
 view_graph_tree_title=\u7D50\u679C\u3092\u30B0\u30E9\u30D5\u3068\u30C4\u30EA\u30FC\u3067\u8868\u793A
 view_results_in_table=\u7D50\u679C\u3092\u8868\u3067\u8868\u793A
 view_results_tab_request=\u30EA\u30AF\u30A8\u30B9\u30C8
 view_results_tab_response=\u5FDC\u7B54\u30C7\u30FC\u30BF
 view_results_title=\u7D50\u679C\u8868\u793A
 view_results_tree_title=\u7D50\u679C\u3092\u30C4\u30EA\u30FC\u3067\u8868\u793A
 web_request=HTTP \u30EA\u30AF\u30A8\u30B9\u30C8
 web_server=Web \u30B5\u30FC\u30D0
 web_server_domain=\u30B5\u30FC\u30D0\u540D\u307E\u305F\u306F IP\:
 web_server_port=\u30DD\u30FC\u30C8\u756A\u53F7\:
 web_testing_retrieve_images=\u5168\u3066\u306E\u30A4\u30E1\u30FC\u30B8\u3068\u30A2\u30D7\u30EC\u30C3\u30C8\u3092\u7E70\u308A\u8FD4\u3057\u30C0\u30A6\u30F3\u30ED\u30FC\u30C9\u3059\u308B(HTML \u30D5\u30A1\u30A4\u30EB\u306E\u307F)
 web_testing_title=HTTP \u30EA\u30AF\u30A8\u30B9\u30C8
 workbench_title=\u30EF\u30FC\u30AF\u30D9\u30F3\u30C1
 xml_assertion_title=XML \u30A2\u30B5\u30FC\u30B7\u30E7\u30F3
 you_must_enter_a_valid_number=\u9069\u5207\u306A\u6570\u5024\u3092\u5165\u529B\u3057\u3066\u304F\u3060\u3055\u3044
diff --git a/src/core/org/apache/jmeter/resources/messages_pl.properties b/src/core/org/apache/jmeter/resources/messages_pl.properties
index 4e60e1f30..f9e27aaa8 100644
--- a/src/core/org/apache/jmeter/resources/messages_pl.properties
+++ b/src/core/org/apache/jmeter/resources/messages_pl.properties
@@ -1,237 +1,236 @@
 #Generated by ResourceBundle Editor (http://eclipse-rbe.sourceforge.net)
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
 #=> All keys in this file must also be lower case or they won't match
 #
 about=O programie Apache JMeter
 add=Dodaj
 add_parameter=Dodaj parametr
 add_pattern=Dodaj wzorzec:
 add_test=Dodaj test
 add_user=Dodaj u\u017Cytkownika
 add_value=Dodaj warto\u015B\u0107
 addtest=Dodaj test
 aggregate_graph=Wykresy statystyczne
 aggregate_graph_column=Kolumna
 aggregate_graph_display=Wy\u015Bwietl wykres
 aggregate_graph_height=Wysoko\u015B\u0107
 aggregate_graph_max_length_xaxis_label=Maksymalna wysoko\u015B\u0107 etykiety osi OX
 aggregate_graph_ms=Milisekund
 aggregate_graph_response_time=Czas odpowiedzi
 aggregate_graph_save=Zapisz wykres
 aggregate_graph_save_table=Zapisz dane z tabeli
 aggregate_graph_save_table_header=Zapisz nag\u0142\u00F3wek tabeli
 aggregate_graph_title=Wykres skumulowany
 aggregate_graph_user_title=Tytu\u0142 wykresu
 aggregate_graph_width=Szeroko\u015B\u0107
 aggregate_report=Dane zagregowane
 aggregate_report_bandwidth=KB/sek
 aggregate_report_count=Liczba pr\u00F3bek
 aggregate_report_error=B\u0142\u0105d
 aggregate_report_error%=% b\u0142\u0119d\u00F3w
 aggregate_report_median=Mediana
 aggregate_report_rate=Przepustowo\u015B\u0107
 aggregate_report_stddev=Odch. std.
 aggregate_report_total_label=RAZEM
 ajp_sampler_title=Pr\u00F3bnik AJP/1.3
 analyze=Analizuj plik z danymi...
 anchor_modifier_title=Parser link\u00F3w HTML
 argument_must_not_be_negative=Parametr musi by\u0107 nieujemny!
 assertion_assume_success=Ignoruj status
 assertion_code_resp=Kod odpowiedzi
 assertion_contains=Zawiera
 assertion_equals=R\u00F3wna si\u0119
 assertion_headers=Nag\u0142\u00F3wki odpowiedzi
 assertion_matches=Pasuje do
 assertion_message_resp=Tre\u015B\u0107 odpowiedzi
 assertion_not=Nie
 assertion_text_resp=Tekst odpowiedzi
 assertion_textarea_label=Asercje:
 attribute=Atrybut
 attrs=Atrybuty
 average=\u015Arednia
 average_bytes=bit\u00F3w \u015Brednio
 browse=Przegl\u0105daj...
 bsf_sampler_title=Pr\u00F3bnik BSF
 bsf_script_language=J\u0119zyk skryptowy:
 bsf_script_parameters=Parametry do przekazania do skryptu/pliku:
 bsh_assertion_script=Skrypt (see below for variables that are defined)
 bsh_script=Skrypt (see below for variables that are defined)
 bsh_script_file=Plik ze skryptem
 cancel=Anuluj
 choose_function=Wybierz funkcj\u0119
 choose_language=Wybierz j\u0119zyk
 clear=Wyczy\u015B\u0107
 clear_all=Wyczy\u015B\u0107 wszystko
 clear_cache_per_iter=Czy\u015Bci\u0107 cache po ka\u017Cdej iteracji?
 column_delete_disallowed=Tej kolumny nie mo\u017Cna usuwa\u0107
 compare=Por\u00F3wnaj
 config_element=Element konfiguruj\u0105cy
 config_save_settings=Konfiguruj
 controller=Kontroler
 copy=Kopiuj
 counter_config_title=Licznik
 counter_per_user=Osobny licznik dla ka\u017Cdego u\u017Cytkownika
 countlim=Limit rozmiaru
 cut=Wytnij
 de=Niemiecki
 debug_off=Wy\u0142\u0105cz debugowanie
 debug_on=W\u0142\u0105cz debugowanie
 default_parameters=Parametry domy\u015Blne
 default_value_field=Domy\u015Blna warto\u015B\u0107:
 delay=Uruchom w ci\u0105gu (sekund)
 delete=Usu\u0144
 delete_parameter=Usu\u0144 parametr
 delete_test=Usu\u0144 test
 delete_user=Usu\u0144 u\u017Cytkownika
 deltest=Test usuwania
 disable=Wy\u0142\u0105cz
 distribution_graph_title=Rozk\u0142ad funkcji g\u0119sto\u015Bci (alpha)
 distribution_note1=Wykres uaktualnia si\u0119 automatycznie co 10 pr\u00F3bek
 domain=Domena
 done=Gotowe
 duration=Czas trwania (sekund)
 edit=Edytuj
 en=Angielski
 enable=W\u0142\u0105cz
 endtime=Czas zako\u0144czenia
 error_loading_help=Wyst\u0105pi\u0142 b\u0142\u0105d przy pr\u00F3bie wy\u015Bwietlenia strony z pomoc\u0105
 error_occurred=Wyst\u0105pi\u0142 b\u0142\u0105d
 error_title=B\u0142\u0105d
 es=Hiszpa\u0144ski
 exit=Edycja
 file=Plik
 file_visualizer_close=Zamknij
 file_visualizer_filename=Nazwa pliku
 file_visualizer_flush=Zapisz
 file_visualizer_open=Otw\u00f3rz
 file_visualizer_output_file=Zapisuj/Czytaj wyniki z pliku
 filename=Nazwa pliku
 functional_mode=Tryb test\u00F3w funkcjonalnych (i.e. zapisuj dane wej\u015bciowe i wyj\u015bciowe pr\u00f3bnika)
 functional_mode_explanation=W\u0142\u0105czenie trybu test\u00F3w funkcjonalnych mo\u017Ce wyra\u017Anie obni\u017Cy\u0107 wydajno\u015B\u0107.
 help=Pomoc
 help_node=Co to za w\u0119ze\u0142?
 iterator_num=Liczba powt\u00F3rze\u0144:
 ja=Japo\u0144ski
 jms_client_type=Klient
 jms_config=Konfiguracja
 jms_config_title=Konfiguracja JMS
 jms_message_type=Typ wiadomo\u015Bci
 jms_pwd=Has\u0142o
 jms_queue=Kolejka
 jms_user=U\u017Cytkownik
 load=Wczytaj
 log_errors_only=B\u0142\u0119dy
 log_file=Po\u0142o\u017Cenie pliku log\u00F3w
 log_function_comment=Dodatkowy komentarz (opcjonalnie)
 log_function_level=Szczeg\u00F3\u0142owo\u015B\u0107 logowania (domy\u015Blnie INFO) lub OUT lub ERR
 log_only=Zapisuj do loga/Wy\u015bwietlaj tylko:
 log_success_only=Sukcesy
 mail_reader_account=U\u017Cytkownik:
 mail_reader_password=Has\u0142o:
 mail_reader_server=Serwer:
 mail_reader_server_type=Typ serwera:
 mail_sent=Wiadomo\u015B\u0107 zosta\u0142a wys\u0142ana
 max=Maksimum
 menu_assertions=Assercje
-menu_close=Zamknij
 menu_collapse_all=Zwi\u0144 wszystko
 menu_config_element=Element konfiguruj\u0105cy
 menu_edit=Edytuj
 menu_expand_all=Rozwi\u0144 wszystko
 menu_listener=S\u0142uchacze
 menu_merge=Scal
 menu_open=Otw\u00F3rz
 menu_post_processors=Post Procesory
 menu_pre_processors=Pre Procesory
 name=Nazwa:
 number_of_threads=Liczba w\u0105tk\u00F3w (u\u017Cytkownik\u00F3w):
 open=Otw\u00F3rz...
 option=Opcje
 password=Has\u0142o
 paste=Wklej
 pl=Polski
 property_default_param=Warto\u015B\u0107 domy\u015Blna
 property_edit=Edytuj
 property_undefined=Niezdefiniowano
 property_value_param=Warto\u015B\u0107 parametru
 proxy_assertions=Dodaj asercje
 proxy_headers=Przechwytuj nag\u0142\u00F3wki HTTP
 proxy_sampler_type=Typ:
 proxy_title=Serwer proxy HTTP
 ramp_up=Uruchom w ci\u0105gu (sekund):
 regex_field=Wyra\u017Cenie regularne:
 remove=Usu\u0144
 report_chart_x_axis=O\u015B X
 report_chart_x_axis_label=Etykieta osi X
 report_chart_y_axis=O\u015B Y
 report_chart_y_axis_label=Etykieta osi Y
 report_line_graph=Wykres liniowy
 report_page_title=Tytu\u0142 strony
 reset_gui=Resetuj Gui
 resultsaver_prefix=Prefiks do nazwy pliku:
 revert_project=Cofnij
 run=Uruchom
 sample_scope=Kt\u00F3re pr\u00F3bki testowa\u0107
 sampler_label=Etykieta
 sampler_on_error_action=Co robi\u0107 je\u015Bli Pr\u00F3bnik zg\u0142osi b\u0142\u0105d?
 sampler_on_error_continue=Kontunuuj
 sampler_on_error_stop_test=Przerwij test
 sampler_on_error_stop_test_now=Natychmiast przerwij test
 sampler_on_error_stop_thread=Przerwij w\u0105tek
 save=Zapisz
 save?=Zapisa\u0107?
 save_all_as=Zapisz plan test\u00F3w jako
 save_as=Zapisz zaznaczenie jako...
 save_as_image=Zapisz w\u0119ze\u0142 jako obrazek
 save_as_image_all=Zapisz ekran jako obrazek
 save_asxml=Zapisz jako XML
 save_bytes=Zapisz liczb\u0119 bit\u00F3w
 save_code=Zapisz kod odpowiedzi
 save_datatype=Zapisz typ danych
 save_encoding=Zapisz stron\u0119 kodow\u0105
 save_fieldnames=Zapisz nazwy kolumn (CSV)
 scheduler=Kalendarz
 scheduler_configuration=Konfiguracja kalendarza
 scope=Zakres
 second=sekund
 send_file_browse=Przegl\u0105daj ...
 send_file_filename_label=\u015acie\u017cka do pliku:
 ssl_pass_prompt=Prosz\u0119 wpisz has\u0142o
 template_field=Szablon:
 test_action_duration=Czas trwania (milisekund)
 test_action_pause=Pauza
 test_plan=Plan test\u00F3w
 test_plan_classpath_browse=Dodaj katalog lub jara do classpatha
 testplan.serialized=Uruchamiaj grupy w\u0105tk\u00F3w jedna po drugiej (tzn. jedn\u0105 na raz)
 testplan_comments=Uwagi:
 thread_group_title=Grupa w\u0105tk\u00F3w
 thread_properties=W\u0105tki
 threadgroup=Grupa w\u0105tk\u00F3w
 user_defined_test=Test zdefiniowany przez u\u017Cytkownika
 user_defined_variables=Zmienne zdefiniowane przez u\u017Cytkownika
 user_parameters_table=Parametry
 user_parameters_title=Parametry u\u017Cytkownika
 userdn=U\u017Cytkownik
 username=U\u017Cytkownik
 userpw=Has\u0142o
 value=Warto\u015B\u0107:
 workbench_title=Brudnopis
 xpath_tidy_show_warnings=Pokazuj ostrze\u017Cenia
 you_must_enter_a_valid_number=Tu trzeba wpisa\u0107 liczb\u0119
 zh_cn=Chi\u0144ski (Uproszczony)
 zh_tw=Chi\u0144ski (Tradycyjny)
diff --git a/src/core/org/apache/jmeter/resources/messages_pt_BR.properties b/src/core/org/apache/jmeter/resources/messages_pt_BR.properties
index 199a064df..799d44f0b 100644
--- a/src/core/org/apache/jmeter/resources/messages_pt_BR.properties
+++ b/src/core/org/apache/jmeter/resources/messages_pt_BR.properties
@@ -1,852 +1,851 @@
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
 jms_receive_queue=Nome da Fila de Recebimento JNDI
 jms_request=Somente Requisitar
 jms_requestreply=Requisitar e Responder
 jms_sample_title=Padr\u00F5es de Requisi\u00E7\u00E3o JMS
 jms_send_queue=Fila de Requisi\u00E7\u00E3o de nomes JNDI
 jms_store_response=Armazenar a resposta
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
 menu_assertions=Asser\u00E7\u00F5es
-menu_close=Fechar
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
 start_no_timers=Iniciar sem pausas
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
 transaction_controller_include_timers=Incluem dura\u221a\u00df\u221a\u00a3o do temporizador e pr\u221a\u00a9-p\u221a\u2265s processadores em amostra gerada
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
 view_results_response_partial_message=In\u00EDcio da mensagem:
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
 while_controller_label=Condi\u00E7\u00F5e (fun\u00E7\u00E3o ou vari\u00E1vel)
 while_controller_title=Controlador de Enquanto
 workbench_title=\u00C1rea de Trabalho
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
diff --git a/src/core/org/apache/jmeter/resources/messages_tr.properties b/src/core/org/apache/jmeter/resources/messages_tr.properties
index 75e6a617c..0ced5c670 100644
--- a/src/core/org/apache/jmeter/resources/messages_tr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_tr.properties
@@ -1,790 +1,789 @@
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
 about=Apache JMeter Hakk\u0131nda
 add=Ekle
 add_as_child=\u00C7ocuk Olarak Ekle
 add_parameter=De\u011Fi\u015Fken Olarak Ekle
 add_pattern=Desen Ekle\:
 add_test=Test Ekle
 add_user=Kullan\u0131c\u0131 Ekle
 add_value=De\u011Fer Ekle
 addtest=Test ekle
 aggregate_graph=\u0130statiksel Grafikler
 aggregate_graph_column=Kolon
 aggregate_graph_display=Grafik G\u00F6ster
 aggregate_graph_height=Y\u00FCkseklik
 aggregate_graph_max_length_xaxis_label=X-ekseni etiketinin maximum uzunlu\u011Fu
 aggregate_graph_ms=Milisaniyeler
 aggregate_graph_response_time=Cevap Zaman\u0131
 aggregate_graph_save=Grafi\u011Fi kaydet
 aggregate_graph_save_table=Tablo Verisini Kaydet
 aggregate_graph_save_table_header=Tablo Ba\u015Fl\u0131\u011F\u0131n\u0131 Kaydet
 aggregate_graph_title=Toplu Grafik
 aggregate_graph_user_title=Grafik Ba\u015Fl\u0131\u011F\u0131
 aggregate_graph_width=Geni\u015Flik
 aggregate_report=Toplu Rapor
 aggregate_report_bandwidth=KB/sn
 aggregate_report_count=\# \u00D6rnek
 aggregate_report_error=Hata
 aggregate_report_error%=Hata %
 aggregate_report_max=En \u00C7ok
 aggregate_report_median=Ortalama
 aggregate_report_min=En Az
 aggregate_report_rate=Transfer Oran\u0131
 aggregate_report_total_label=TOPLAM
 als_message=Not\: Eri\u015Fim Logu Ayr\u0131\u015Ft\u0131r\u0131c\u0131s\u0131 eklentiye izin veren genel-ge\u00E7er bir tasar\u0131ma sahiptir
 als_message2=\u00F6zg\u00FCn ayr\u0131\u015Ft\u0131r\u0131c\u0131. Bu \u015Fekilde yapmak i\u00E7in, LogParser'i ger\u00E7ekle, jar'\u0131 \u015Furaya ekle\: 
 als_message3=/lib dizini ve \u00F6rnekleyicideki s\u0131n\u0131f\u0131 gir.
 analyze=Veri Dosyas\u0131n\u0131 Analiz Et...
 anchor_modifier_title=HTML Ba\u011Flant\u0131s\u0131 Ayr\u0131\u015Ft\u0131r\u0131c\u0131s\u0131
 appearance=Temalar
 argument_must_not_be_negative=Ba\u011F\u0131ms\u0131z de\u011Fi\u015Fken negatif olmamal\u0131\!
 assertion_assume_success=Durumu Yoksay
 assertion_code_resp=Cevap Kodu
 assertion_contains=\u0130\u00E7erir
 assertion_equals=E\u015Fittir
 assertion_headers=Cevap Ba\u015Fl\u0131klar\u0131
 assertion_matches=\u00D6rt\u00FC\u015F\u00FCr
 assertion_message_resp=Cevap Mesaj\u0131
 assertion_pattern_match_rules=Desen \u00D6rt\u00FC\u015Fme Kurallar\u0131
 assertion_patterns_to_test=Test Edilecek Desenler
 assertion_resp_field=Test Edilecek Cevap Alan\u0131
 assertion_text_resp=Metin Cevap
 assertion_textarea_label=Do\u011Frulamalar\:
 assertion_title=Cevap Do\u011Frulamas\u0131
 assertion_url_samp=URL \u00D6rneklendi
 assertion_visualizer_title=Do\u011Frulama Sonu\u00E7lar\u0131
 attribute=\u00D6znitelik
 attrs=\u00D6znitelikler
 auth_base_url=Temel URL
 auth_manager_title=HTTP Yetkilendirme Y\u00F6neticisi
 auths_stored=Yetkilendirme Y\u00F6neticisinde Tutulan Yetkilendirmeler
 average=Ortalama
 average_bytes=Ort. Byte
 bind=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Ba\u011Flamas\u0131
 browse=G\u00F6zat...
 bsf_sampler_title=BSF \u00D6rnekleyicisi
 bsf_script=\u00C7al\u0131\u015Ft\u0131r\u0131lacak betik (de\u011Fi\u015Fkenler\: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=\u00C7al\u0131\u015Ft\u0131r\u0131lacak betik dosyas\u0131
 bsf_script_language=Betik dili\:
 bsf_script_parameters=Beti\u011Fe veya betik dosyas\u0131na ge\u00E7ilecek parametreler
 bsh_assertion_script=Betik (tan\u0131ml\u0131 de\u011Fi\u015Fkenler i\u00E7in a\u015Fa\u011F\u0131ya bak\u0131n)
 bsh_assertion_script_variables=Betik i\u00E7in \u015Fu de\u011Fi\u015Fkenler tan\u0131mlanm\u0131\u015Ft\u0131r\:\nOkuma/Yazma\: Failure, FailureMessage, SampleResult, vars, props, log.\nSalt Okunur\: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=BeanShell Do\u011Frulamas\u0131
 bsh_function_expression=De\u011Ferlendirilecek ifade
 bsh_sampler_title=BeanShell \u00D6rnekleyici
 bsh_script=Betik (tan\u0131ml\u0131 de\u011Fi\u015Fkenler i\u00E7in a\u015Fa\u011F\u0131ya bak\u0131n)
 bsh_script_file=Betik Dosyas\u0131
 bsh_script_parameters=Parametreler (-> Dizgi (String) Parametreler ve String []bsh.args)
 bsh_script_variables=Betik i\u00E7in \u015Fu de\u011Fi\u015Fkenler tan\u0131ml\u0131d\u0131r\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Testle me\u015Fgul\u00FCm, l\u00FCtfen ayarlar\u0131 de\u011Fi\u015Ftirmeden \u00F6nce testi durdurun
 cache_session_id=\u00D6nbellek oturum Id'si?
 cancel=\u0130ptal
 cancel_exit_to_save=Kaydedilmemi\u015F test maddeleri var. \u00C7\u0131kmadan \u00F6nce kaydetmek ister misiniz?
 cancel_new_to_save=Kaydedilmemi\u015F test maddeleri var. Testi temizlemeden \u00F6nce kaydetmek ister misin?
 cancel_revert_project=Kaydedilmemi\u015F test maddeleri var. Daha \u00F6nce kaydedilmi\u015F olan test plan\u0131na d\u00F6nmek ister misiniz?
 choose_function=Fonksiyon se\u00E7in
 choose_language=Dil se\u00E7in
 clear=Temizle
 clear_all=Hepsini Temizle
 clear_cookies_per_iter=Her tekrar i\u00E7in \u00E7erezleri temizle?
 column_delete_disallowed=Bu kolonu silmek i\u00E7in izin yok
 column_number=CSV dosyas\u0131 i\u00E7in kolon numaras\u0131 | ileri | *takma ad
 compare=Kar\u015F\u0131la\u015Ft\u0131r
 comparefilt=Filtreyi kar\u015F\u0131la\u015Ft\u0131r
 config_element=Ayar Eleman\u0131
 config_save_settings=Ayarla
 constant_throughput_timer_memo=Sabit bir transfer oran\u0131 elde etmek i\u00E7in \u00F6rneklemeler aras\u0131na gecikme ekle
 constant_timer_delay=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Gecikmesi (milisaniyeler)
 constant_timer_memo=\u00D6rneklemeler aras\u0131na sabit bir gecikme ekle
 constant_timer_title=Sabit Zamanlay\u0131c\u0131
 content_encoding=\u0130\u00E7erik kodlamas\u0131\:
 controller=Denet\u00E7i
 cookie_manager_policy=\u00C7erez Politikas\u0131
 cookie_manager_title=HTTP \u00C7erez Y\u00F6neticisi
 cookies_stored=\u00C7erez Y\u00F6neticisinde Tutulan \u00C7erezler
 copy=Kopyala
 counter_config_title=Saya\u00E7
 counter_per_user=Sayac\u0131 her kullan\u0131c\u0131 i\u00E7in ba\u011F\u0131ms\u0131z \u00E7al\u0131\u015Ft\u0131r
 countlim=Boyut s\u0131n\u0131r\u0131
 csvread_file_file_name=De\u011Ferlerin okunaca\u011F\u0131 CSV dosyas\u0131 | *k\u0131saltma
 cut=Kes
 cut_paste_function=Fonksiyon metnini kopyala ve yap\u0131\u015Ft\u0131r
 database_conn_pool_max_usage=Her Ba\u011Flant\u0131 i\u00E7in En Fazla Kullan\u0131m\:
 database_conn_pool_props=Veritaban\u0131 Ba\u011Flant\u0131s\u0131 Havuzu
 database_conn_pool_size=Havuzdaki Ba\u011Flant\u0131 Say\u0131s\u0131
 database_conn_pool_title=JDBC Veritaban Ba\u011Flant\u0131s\u0131 Havuzu \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 database_driver_class=S\u00FCr\u00FCc\u00FC S\u0131n\u0131f\u0131\:
 database_login_title=JDBC Veritaban\u0131 Giri\u015Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 database_sql_query_string=SQL Sorgusu Metni\:
 database_sql_query_title=JDBC SQL Sorgusu \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 database_testing_title=JDBC \u0130ste\u011Fi
 database_url=JDBC Adresi\:
 database_url_jdbc_props=Veritaban\u0131 Adresi ve JDBC S\u00FCr\u00FCc\u00FCs\u00FC
 de=Alman
 debug_off=Hata ay\u0131klamas\u0131n\u0131 saf d\u0131\u015F\u0131 b\u0131rak
 debug_on=Hata ay\u0131klamas\u0131n\u0131 etkinle\u015Ftir
 default_parameters=\u00D6ntan\u0131ml\u0131 Parametreler
 default_value_field=\u00D6ntan\u0131ml\u0131 De\u011Fer\:
 delay=Ba\u015Flang\u0131\u00E7 gecikmesi (saniye)
 delete=Sil
 delete_parameter=De\u011Fi\u015Fkeni Sil
 delete_test=Testi Sil
 delete_user=Kullan\u0131c\u0131y\u0131 Sil
 deltest=Testi sil
 deref=K\u0131saltmalar\u0131 g\u00F6ster
 disable=Safd\u0131\u015F\u0131 b\u0131rak
 distribution_graph_title=Da\u011F\u0131t\u0131m Grafi\u011Fi (alfa)
 distribution_note1=Grafik 10 \u00F6rnekte bir g\u00FCncellenecek
 domain=Etki Alan\u0131
 done=Bitti
 duration=S\u00FCre (saniye)
 duration_assertion_duration_test=Do\u011Frulama S\u00FCresi
 duration_assertion_failure=\u0130\u015Flem \u00E7ok uzun s\u00FCrd\u00FC\: {0} milisaniye s\u00FCrmesi gerekirken, {1}  milisaniyeden fazla s\u00FCrd\u00FC.
 duration_assertion_input_error=Pozitif bir tamsay\u0131 giriniz.
 duration_assertion_label=Milisaniye olarak s\u00FCre\:
 duration_assertion_title=S\u00FCre Do\u011Frulamas\u0131
 edit=D\u00FCzenle
 email_results_title=E-posta Sonu\u00E7lar\u0131
 en=\u0130ngilizce
 enable=Etkinle\u015Ftir
 encode?=Kodlama?
 encoded_value=URL Kodlanm\u0131\u015F De\u011Fer
 endtime=Biti\u015F Zaman\u0131
 entry_dn=Giri\u015F DN'i
 entrydn=Giri\u015F DN'i
 error_loading_help=Yard\u0131m sayfas\u0131n\u0131 y\u00FCklerken hata
 error_occurred=Hata Olu\u015Ftu
 error_title=Hata
 es=\u0130spanyolca
 eval_name_param=De\u011Fi\u015Fken ve fonksiyon referanslar\u0131 i\u00E7eren metin
 evalvar_name_param=De\u011Fi\u015Fken ismi
 example_data=\u00D6rnek Veri
 example_title=\u00D6rnekleyici \u00F6rne\u011Fi
 exit=\u00C7\u0131k\u0131\u015F
 expiration=S\u00FCre dolumu
 field_name=Alan ismi
 file=Dosya
 file_already_in_use=Bu dosya zaten kullan\u0131l\u0131yor
 file_visualizer_append=Varolan Veri Dosyas\u0131na Ekle
 file_visualizer_auto_flush=Her \u00D6rnekten Sonra Otomatik Olarak Temizle
 file_visualizer_browse=G\u00F6zat...
 file_visualizer_close=Kapat
 file_visualizer_file_options=Dosya Se\u00E7enekleri
 file_visualizer_filename=Dosya ismi
 file_visualizer_flush=Temizle
 file_visualizer_missing_filename=\u00C7\u0131kt\u0131 dosyas\u0131 belirtilmedi.
 file_visualizer_open=A\u00E7
 file_visualizer_output_file=Sonu\u00E7lar\u0131 dosyaya yaz / Dosyadan oku
 file_visualizer_submit_data=Girilen Veriyi Ekle
 file_visualizer_title=Dosya Raprolay\u0131c\u0131
 file_visualizer_verbose=\u00C7\u0131kt\u0131y\u0131 Detayland\u0131r
 filename=Dosya \u0130smi
 follow_redirects=Y\u00F6nlendirmeleri \u0130zle
 follow_redirects_auto=Otomatik Olarak Y\u00F6nlendir
 foreach_controller_title=ForEach Denet\u00E7isi
 foreach_input=Giri\u015F de\u011Fi\u015Fkeni \u00F6neki
 foreach_output=\u00C7\u0131k\u0131\u015F de\u011Fi\u015Fkeni ismi
 foreach_use_separator=Numara \u00F6n\u00FCne "_" ekle ?
 format=Numara bi\u00E7imi
 fr=Frans\u0131zca
 ftp_binary_mode=\u0130kili kipi kullan ?
 ftp_local_file=Yerel Dosya\:
 ftp_remote_file=Uzak Dosya\:
 ftp_sample_title=FTP \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 ftp_save_response_data=Cevab\u0131 Dosyaya Kaydet ?
 ftp_testing_title=FTP \u0130ste\u011Fi
 function_dialog_menu_item=Fonksiyon Yard\u0131m\u0131 Diyalo\u011Fu
 function_helper_title=Fonksiyon Yard\u0131m\u0131
 function_name_param=Sonucu tutacak de\u011F\u015Fkenin ismi (gerekli)
 function_name_paropt=Sonucu tutacak de\u011Fi\u015Fkenin ismi (iste\u011Fe ba\u011Fl\u0131)
 function_params=Fonksiyon Parametresi
 functional_mode=Fonksiyonel Test Kipi (\u00F6r\: Cevap Verisini ve \u00D6rnekleyici Verisini kaydet)
 functional_mode_explanation=Ba\u015Far\u0131m\u0131 olumsuz etkileyecek olmas\u0131na ra\u011Fmen Fonksiyonel Test Kipi se\u00E7iliyor.
 gaussian_timer_delay=Sabit Gecikme S\u0131n\u0131r\u0131 (milisaniye)
 gaussian_timer_memo=Gauss da\u011F\u0131l\u0131m\u0131na g\u00F6re rastgele bir gecikme ekler
 gaussian_timer_range=Sapma (milisaniye)
 gaussian_timer_title=Gauss Rastgele Zamanlay\u0131c\u0131
 generate=\u00DCret
 generator=\u00DCretici S\u0131n\u0131f\u0131n \u0130smi
 generator_cnf_msg=\u00DCretici s\u0131n\u0131f\u0131 bulamad\u0131. L\u00FCtfen jar dosyas\u0131n\u0131 /lib dizini alt\u0131na yerle\u015Ftirdi\u011Finizden emin olun.
 generator_illegal_msg=IllegalAccessException nedeniyle \u00FCretici s\u0131n\u0131fa eri\u015Femedi.
 generator_instantiate_msg=\u00DCretici ayr\u0131\u015Ft\u0131r\u0131c\u0131 i\u00E7in \u00F6rnek yaratamad\u0131. L\u00FCtfen \u00FCreticinin "Generator" arabirimini ger\u00E7ekledi\u011Finden emin olun.
 graph_choose_graphs=G\u00F6sterilecek Grafikler
 graph_full_results_title=Grafik Tam Sonu\u00E7lar\u0131
 graph_results_average=Ortalama
 graph_results_data=Veri
 graph_results_deviation=Sapma
 graph_results_latest_sample=Son \u00D6rnek
 graph_results_median=Orta
 graph_results_no_samples=\u00D6rnek Say\u0131s\u0131
 graph_results_throughput=Transfer Oran\u0131
 graph_results_title=Grafik Sonu\u00E7lar\u0131
 grouping_add_separators=Gruplar aras\u0131na ayra\u00E7 ekle
 grouping_in_controllers=Her grubu yeni bir denet\u00E7iye koy
 grouping_mode=Gruplama\:
 grouping_no_groups=\u00D6rnekleyicileri gruplama
 grouping_store_first_only=Her grubun sadece 1. \u00F6rnekleyicilerini tut
 header_manager_title=HTTP Ba\u015Fl\u0131k Y\u00F6neticisi
 headers_stored=Ba\u015Fl\u0131k Y\u00F6neticisinde Tutulan Ba\u015Fl\u0131klar
 help=Yard\u0131m
 help_node=Bu d\u00FC\u011F\u00FCm nedir?
 html_assertion_file=JTidy raporunu dosyaya yaz
 html_assertion_label=HTML Do\u011Frulama
 html_assertion_title=HTML Do\u011Frulama
 http_implementation=Uygulamas\u0131\:
 http_response_code=HTTP cevap kodu
 http_url_rewriting_modifier_title=HTTP URL Yeniden Yazma Niteleyicisi
 http_user_parameter_modifier=HTTP Kullan\u0131c\u0131 Parametresi Niteleyicisi
 httpmirror_title=HTTP Ayna Sunucusu
 id_prefix=ID \u00D6neki
 id_suffix=ID Soneki
 if_controller_evaluate_all=T\u00FCm \u00E7ocuklar i\u00E7in hesapla?
 if_controller_label=Durum (Javascript)
 if_controller_title=If Denet\u00E7isi
 ignore_subcontrollers=Alt denet\u00E7i bloklar\u0131n\u0131 yoksay
 include_controller=\u0130\u00E7erme Denet\u00E7isi
 include_equals=E\u015Fleniyorsa i\u00E7er?
 include_path=Test Plan\u0131n\u0131 \u0130\u00E7er
 increment=Artt\u0131r
 infinite=Her zaman
 initial_context_factory=\u0130lk Ba\u011Flam Fabrikas\u0131
 insert_after=Arkas\u0131na Ekle
 insert_before=\u00D6n\u00FCne Ekle
 insert_parent=Ebeveynine Ekle
 interleave_control_title=Aral\u0131k Denet\u00E7isi
 intsum_param_1=Eklenecek ilk tamsay\u0131.
 intsum_param_2=Eklenecek ikinci tamsay\u0131 - sonraki tamsay\u0131lar di\u011Fer argumanlar\u0131 ekleyerek toplanabilir.
 invalid_data=Ge\u00E7ersiz veri
 invalid_mail=E-posta g\u00F6nderirken hata olu\u015Ftu
 invalid_mail_address=Bir veya daha fazla ge\u00E7ersiz e-posta adresi tespit edildi
 invalid_mail_server=E-posta sunucusuna ba\u011Flan\u0131rken problem (JMeter log dosyas\u0131na bak\u0131n\u0131z)
 invalid_variables=Ge\u00E7ersiz de\u011Fi\u015Fkenler
 iteration_counter_arg_1=TRUE, her kullan\u0131c\u0131n\u0131n kendi sayac\u0131na sahip olmas\u0131 i\u00E7in, FALSE genel-ge\u00E7er saya\u00E7 i\u00E7in
 iterator_num=D\u00F6ng\u00FC Say\u0131s\u0131\:
 ja=Japonca
 jar_file=Jar Dosyalar\u0131
 java_request=Java \u0130ste\u011Fi
 java_request_defaults=Java \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 javascript_expression=De\u011Ferlendirilecek javascript ifadesi
 jexl_expression=De\u011Ferlendirilecek JEXL ifadesi
 jms_auth_required=Gerekli
 jms_client_caption=Mesaj dinlemek i\u00E7in TopicSubscriber.receive() kullanan istemciyi al.
 jms_client_caption2=MessageListener yeni mesajlar\u0131 dinlemek i\u00E7in onMessage(Message)'\u0131 kullan\u0131r.
 jms_client_type=\u0130stemci
 jms_communication_style=\u0130leti\u015Fim \u015Eekli
 jms_concrete_connection_factory=Somut Ba\u011Flant\u0131 Fabrikas\u0131
 jms_config=Ayarlar
 jms_config_title=JMS Ayar\u0131
 jms_connection_factory=Ba\u011Flant\u0131 Fabrikas\u0131
 jms_file=Dosya
 jms_initial_context_factory=Ba\u015Flang\u0131\u00E7 Ba\u011Flam Fabrikas\u0131
 jms_itertions=Toplanacak istek say\u0131s\u0131
 jms_jndi_defaults_title=JNDI \u00D6ntan\u0131ml\u0131 Ayarlar\u0131
 jms_jndi_props=JDNI \u00D6zellikleri
 jms_message_title=Mesaj \u00F6zellikleri
 jms_message_type=Mesaj Tipi
 jms_msg_content=\u0130\u00E7erik
 jms_object_message=Nesne Mesaj\u0131
 jms_point_to_point=JMS U\u00E7tan Uca
 jms_props=JMS \u00D6zellikleri
 jms_provider_url=Sa\u011Flay\u0131c\u0131 Adresi (URL)
 jms_publisher=JMS Yay\u0131nc\u0131s\u0131
 jms_pwd=\u015Eifre
 jms_queue=S\u0131ra
 jms_queue_connection_factory=QueueConnection Fabrikas\u0131
 jms_queueing=JMS Kaynaklar\u0131
 jms_random_file=Rastgele Dosyas\u0131
 jms_receive_queue=S\u0131ray\u0131 alan JNDI ismi 
 jms_request=Sadece \u0130stek
 jms_requestreply=\u0130stek Cevap
 jms_sample_title=JMS \u00D6ntan\u0131ml\u0131 \u0130ste\u011Fi 
 jms_send_queue=S\u0131ray\u0131 alan JNDI ismi
 jms_subscriber_on_message=MessageListener.onMessage()'\u0131 kullan
 jms_subscriber_receive=TopicSubscriber.receive()'\u0131 kullan
 jms_subscriber_title=JMS Abonesi
 jms_testing_title=Mesajla\u015Fma \u0130ste\u011Fi
 jms_text_message=Metin Mesaj\u0131
 jms_timeout=Zaman A\u015F\u0131m\u0131 (milisaniye)
 jms_topic=Konu
 jms_use_file=Dosyadan
 jms_use_non_persistent_delivery=S\u00FCrekli olmayan da\u011F\u0131t\u0131m kipini kullan?
 jms_use_properties_file=jndi.properties dosyas\u0131n\u0131 kullan
 jms_use_random_file=Rastgele Dosyas\u0131
 jms_use_text=Metin alan\u0131
 jms_user=Kullan\u0131c\u0131
 jndi_config_title=JNDI Ayar\u0131
 jndi_lookup_name=Uzak Arabirim
 jndi_lookup_title=JNDI Arama Ayar\u0131
 jndi_method_button_invoke=\u00C7a\u011F\u0131r
 jndi_method_button_reflect=Yans\u0131t
 jndi_method_home_name=Yerel Metod \u0130smi
 jndi_method_home_parms=Yerel Metod Parametreleri
 jndi_method_name=Metod Ayar\u0131
 jndi_method_remote_interface_list=Uzak Arabirimler
 jndi_method_remote_name=Uzak Metod \u0130smi
 jndi_method_remote_parms=Uzak Metod Parametreleri
 jndi_method_title=Uzak Metod Ayar\u0131
 jndi_testing_title=JNDI \u0130ste\u011Fi
 jndi_url_jndi_props=JNDI \u00D6zellikleri
 junit_append_error=Do\u011Frulama hatalar\u0131n\u0131 ekle
 junit_append_exception=\u00C7al\u0131\u015Fma zaman\u0131 istisnalar\u0131n\u0131 ekle
 junit_constructor_error=S\u0131n\u0131f \u00F6rne\u011Fi yarat\u0131lamad\u0131
 junit_constructor_string=Yap\u0131c\u0131 Metin Etiketi
 junit_do_setup_teardown=setUp ve tearDown'u \u00E7a\u011F\u0131rma
 junit_error_code=Hata Kodu
 junit_error_default_msg=Beklenmedik hata olu\u015Ftu
 junit_error_msg=Hata Mesaj\u0131
 junit_failure_code=Ba\u015Far\u0131s\u0131zl\u0131k Kodu 
 junit_failure_default_msg=Test ba\u015Far\u0131s\u0131z oldu.
 junit_failure_msg=Ba\u015Far\u0131s\u0131zl\u0131k Mesaj\u0131
 junit_pkg_filter=Paket Filtresi
 junit_request=JUnit \u0130ste\u011Fi
 junit_request_defaults=JUnit \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 junit_success_code=Ba\u015Far\u0131 Kodu
 junit_success_default_msg=Test ba\u015Far\u0131s\u0131z
 junit_success_msg=Ba\u015Far\u0131 Mesaj\u0131
 junit_test_config=JUnit Test Parametreleri
 junit_test_method=Test Metodu
 ldap_argument_list=LDAP Arg\u00FCman Listesi
 ldap_connto=Ba\u011Flant\u0131 zaman a\u015F\u0131m\u0131 (milisaniye)
 ldap_parse_results=Arama sonu\u00E7lar\u0131n\u0131 ayr\u0131\u015Ft\u0131r ?
 ldap_sample_title=LDAP \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 Ayarlar\u0131
 ldap_search_baseobject=Temel-nesne aramas\u0131 ger\u00E7ekle\u015Ftir
 ldap_search_onelevel=Tek-seviye aramas\u0131 ger\u00E7ekle\u015Ftir
 ldap_search_subtree=Alt-a\u011Fa\u00E7 aramas\u0131 ger\u00E7ekle\u015Ftir
 ldap_secure=G\u00FCvenli LDAP Protokulu kullan ?
 ldap_testing_title=LDAP \u0130ste\u011Fi
 ldapext_sample_title=LDAP Geli\u015Fmi\u015F \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 ldapext_testing_title=LDAP Geli\u015Fmi\u015F \u0130ste\u011Fi
 load=Y\u00FCkle
 log_errors_only=Hatalar
 log_file=Log Dosyas\u0131 Yolu
 log_function_comment=Ek yorum (iste\u011Fe ba\u011Fl\u0131)
 log_function_level=Log seviyesi (\u00F6ntan\u0131ml\u0131 INFO) ya OUT ya da ERR
 log_function_string=Loglanacak metin
 log_function_string_ret=Loglanacak (ve d\u00F6n\u00FClecek) metin
 log_function_throwable=At\u0131lacak metin (iste\u011Fe ba\u011Fl\u0131)
 log_only=Sadece Log/G\u00F6r\u00FCnt\u00FCleme\:
 log_parser=Log Ayr\u0131\u015Ft\u0131r\u0131c\u0131s\u0131 S\u0131n\u0131f\u0131n\u0131n \u0130smi
 log_parser_cnf_msg=S\u0131n\u0131f\u0131 bulamad\u0131. L\u00FCtfen jar dosyas\u0131n\u0131 /lib dizini alt\u0131na yerle\u015Ftirdi\u011Finizden emin olun.
 log_parser_illegal_msg=IllegalAcessException nedeniyle s\u0131n\u0131fa eri\u015Femedi.
 log_parser_instantiate_msg=Log ayr\u0131\u015Ft\u0131r\u0131c\u0131 \u00F6rne\u011Fi yaratamad\u0131. L\u00FCtfen ayr\u0131\u015Ft\u0131r\u0131c\u0131n\u0131n LogParser arabirimini ger\u00E7ekledi\u011Finden emin olun.
 log_sampler=Tomcat Eri\u015Fimi Log \u00D6rnekleyicisi
 log_success_only=Ba\u015Far\u0131lar
 logic_controller_title=Basit Denet\u00E7i
 login_config=Kullan\u0131c\u0131 Giri\u015Fi Ayar\u0131
 login_config_element=Kullan\u0131c\u0131 Giri\u015Fi Eleman\u0131
 longsum_param_1=Eklenek ilk b\u00FCy\u00FCk say\u0131 (long)
 longsum_param_2=Eklenecek ikinci b\u00FCy\u00FCk say\u0131 (long) - di\u011Fer b\u00FCy\u00FCk say\u0131lar di\u011Fer arg\u00FCmanlar\u0131n toplanmas\u0131yla elde edilebilir.
 loop_controller_title=D\u00F6ng\u00FC Denet\u00E7isi
 looping_control=D\u00F6ng\u00FC Kontrol\u00FC
 lower_bound=A\u015Fa\u011F\u0131 S\u0131n\u0131r
 mail_reader_account=Kullan\u0131c\u0131 ismi\:
 mail_reader_all_messages=Hepsi
 mail_reader_delete=Sunucudan mesajlar\u0131 sil
 mail_reader_folder=Klas\u00F6r\:
 mail_reader_num_messages=\u00C7ekilecek mesajlar\u0131n say\u0131s\u0131\:
 mail_reader_password=\u015Eifre\:
 mail_reader_server=Sunucu\:
 mail_reader_server_type=Sunucu Tipi\:
 mail_reader_title=Eposta Okuyucu \u00D6rnekleyicisi
 mail_sent=Eposta ba\u015Far\u0131yla g\u00F6nderildi
 mailer_attributes_panel=Eposta \u00F6znitelikleri
 mailer_error=Eposta g\u00F6nderilemedi. L\u00FCtfen sorunlu girdileri d\u00FCzeltin.
 mailer_visualizer_title=Eposta G\u00F6r\u00FCnt\u00FCleyicisi
 match_num_field=E\u015Fle\u015Fme Numaras\u0131. (Rastgele i\u00E7in 0)
 max=En Fazla
 maximum_param=\u0130zin verilen de\u011Fer aral\u0131\u011F\u0131 i\u00E7in en b\u00FCy\u00FCk de\u011Fer
 md5hex_assertion_failure=MD5 toplam\u0131 do\u011Frulamas\u0131 hatas\u0131\: {1} beklenirken {0} al\u0131nd\u0131
 md5hex_assertion_label=MDBHex
 md5hex_assertion_md5hex_test=Do\u011Frulanacak MD5Hex
 md5hex_assertion_title=MD5Hex Do\u011Frulamas\u0131
 menu_assertions=Do\u011Frulamalar
-menu_close=Kapat
 menu_collapse_all=Hepsini Kapat
 menu_config_element=Ayar Eleman\u0131
 menu_edit=D\u00FCzenle
 menu_expand_all=Hepsini A\u00E7
 menu_generative_controller=\u00D6rnekleyici
 menu_listener=Dinleyici
 menu_logic_controller=Mant\u0131k Denet\u00E7isi
 menu_merge=Birle\u015Ftir
 menu_modifiers=Niteleyiciler
 menu_non_test_elements=Test-d\u0131\u015F\u0131 Elemanlar
 menu_open=A\u00E7
 menu_post_processors=Test Sonras\u0131 \u0130\u015Flemciler
 menu_pre_processors=Test \u00D6ncesi \u0130\u015Flemciler
 menu_response_based_modifiers=Cevap Temelli Niteleyiciler
 menu_timer=Zamanlay\u0131c\u0131
 metadata=Veri hakk\u0131nda veri (metadata)
 method=Metod\:
 mimetype=Mime tipi
 minimum_param=\u0130zin verilen de\u011Fer aral\u0131\u011F\u0131 i\u00E7in en k\u00FC\u00E7\u00FCk de\u011Fer
 minute=dakika
 modddn=Eski girdi ismi
 modification_controller_title=De\u011Fi\u015Fiklik Denet\u00E7isi
 modification_manager_title=De\u011Fi\u015Fiklik Y\u00F6neticisi
 modify_test=Testi De\u011Fi\u015Ftir
 modtest=De\u011Fi\u015Fiklik testi
 module_controller_module_to_run=\u00C7al\u0131\u015Ft\u0131r\u0131lacak Birim
 module_controller_title=Birim Denet\u00E7isi
 module_controller_warning=Birim bulunamad\u0131\:
 name=\u0130sim\:
 new=Yeni
 newdn=Yeni ay\u0131rt edici isim
 no=Norve\u00E7ce
 number_of_threads=\u0130\u015F par\u00E7ac\u0131\u011F\u0131 say\u0131s\u0131
 obsolete_test_element=Test eleman\u0131 belirsiz
 once_only_controller_title=Bir Kerelik Denet\u00E7i
 open=A\u00E7...
 option=Se\u00E7enekler
 optional_tasks=\u0130ste\u011Fe Ba\u011Fl\u0131 G\u00F6revler
 paramtable=\u0130stekle parametreleri g\u00F6nder\:
 password=\u015Eifre
 paste=Yap\u0131\u015Ft\u0131r
 paste_insert=Ekleme Olarak Yap\u0131\u015Ft\u0131r
 path=Yol\:
 path_extension_choice=Yol Uzatmas\u0131 (ayra\u00E7 olarak ";" kullan)
 path_extension_dont_use_equals=Yol uzatmas\u0131nda e\u015Fitlik kullanmay\u0131n (Intershop Enfinity uyumlulu\u011Fu)
 path_extension_dont_use_questionmark=Yol uzatmas\u0131nda soru i\u015Fareti kullanmay\u0131n (Intershop Enfinity uyumlulu\u011Fu)
 patterns_to_exclude=Hari\u00E7 Tutulacak URL Desenleri
 patterns_to_include=Dahil Edilecek URL Desenleri
 pkcs12_desc=PKCS 12 Anahtar (*.p12)
 property_default_param=\u00D6ntan\u0131ml\u0131 de\u011Fer
 property_edit=D\u00FCzenle
 property_editor.value_is_invalid_message=Girdi\u011Finiz metin bu \u00F6zellik i\u00E7in ge\u00E7erli de\u011Fil.\n\u00D6zellik \u00F6nceki de\u011Ferine geri d\u00F6nd\u00FCr\u00FClecek.
 property_editor.value_is_invalid_title=Ge\u00E7ersiz girdi
 property_name_param=\u00D6zellik ismi
 property_returnvalue_param=\u00D6zelli\u011Fin orjinal de\u011Ferini d\u00F6n (\u00F6ntan\u0131ml\u0131 false)?
 property_undefined=Tan\u0131ms\u0131z
 property_value_param=\u00D6zelli\u011Fin de\u011Feri
 property_visualiser_title=\u00D6zellik G\u00F6r\u00FCnt\u00FCleme
 protocol=Protokol [http]\:
 protocol_java_border=Java s\u0131n\u0131f\u0131
 protocol_java_classname=S\u0131n\u0131f ismi\:
 protocol_java_config_tile=Java \u00D6rne\u011Fi Ayarla
 protocol_java_test_title=Java Testi
 provider_url=Sa\u011Flay\u0131c\u0131 Adresi (URL)
 proxy_assertions=Do\u011Frulamalar\u0131 Ekle
 proxy_cl_error=Vekil sunucu belirtiliyorsa, sunucu ve port verilmeli
 proxy_content_type_exclude=Hari\u00E7 tut\:
 proxy_content_type_filter=\u0130\u00E7erik-tipi filtresi
 proxy_content_type_include=\u0130\u00E7eren\:
 proxy_headers=HTTP Ba\u015Fl\u0131klar\u0131n\u0131 Yakala
 proxy_regex=D\u00FCzenli ifade e\u015Fle\u015Fmesi
 proxy_sampler_settings=HTTP \u00D6rnekleyici Ayarlar\u0131
 proxy_sampler_type=Tip\:
 proxy_separators=Ayra\u00E7lar\u0131 Ekle
 proxy_target=Hedef Denet\u00E7isi\:
 proxy_test_plan_content=Test plan\u0131 i\u00E7eri\u011Fi
 proxy_title=HTTP Vekil Sunucusu
 ramp_up=Rampa S\u00FCresi (saniyeler)\:
 random_control_title=Rastgele Denet\u00E7isi
 random_order_control_title=Rastgele S\u0131ra Denet\u00E7isi
 realm=Alan (Realm)
 record_controller_title=Kaydetme Denet\u00E7isi
 ref_name_field=Referans \u0130smi\:
 regex_extractor_title=D\u00FCzenli \u0130fade \u00C7\u0131kar\u0131c\u0131
 regex_field=D\u00FCzenli \u0130fade\:
 regex_source=Se\u00E7ilecek Cevap Alanlar\u0131
 regex_src_body=G\u00F6vde
 regex_src_hdrs=Ba\u015Fl\u0131klar
 regex_src_url=Adres (URL)
 regexfunc_param_1=\u00D6nceki istekte sonu\u00E7lar\u0131 aramak i\u00E7in kullan\u0131lan d\u00FCzenli ifade
 regexfunc_param_2=De\u011Fi\u015Ftirme metni i\u00E7in, d\u00FCzenli ifadeden gruplar\u0131 kullanan \u015Fablon.\nBi\u00E7im $[group]$.  \u00D6rnek $1$.
 regexfunc_param_3=Hangi e\u015Fle\u015Fme kullan\u0131lacak. 1 ya da 1'den b\u00FCy\u00FCk bir tamsay\u0131, rastgele se\u00E7im i\u00E7in RAND, b\u00FCy\u00FCk say\u0131 (float) i\u00E7in A, t\u00FCm e\u015Fle\u015Fmelerin kullan\u0131lmas\u0131 i\u00E7in  ALL ([1])
 regexfunc_param_4=Metinler aras\u0131nda. E\u011Fer ALL se\u00E7iliyse, aradaki metin sonu\u00E7lar\u0131 yaratmak i\u00E7in kullan\u0131lacak ([""])
 regexfunc_param_5=\u00D6ntan\u0131ml\u0131 metin. E\u011Fer d\u00FCzenli ifade bir e\u015Fle\u015Fme yakalayamazsa, yerine kullan\u0131lacak \u015Fablon ([""])
 remote_error_init=Uzak sunucuyu s\u0131f\u0131rlarken hata
 remote_error_starting=Uzak sunucuyu ba\u015Flat\u0131rken hata
 remote_exit=Uzakta \u00C7\u0131k
 remote_exit_all=Uzakta Hepsinden \u00C7\u0131k 
 remote_start=Uzakta Ba\u015Flat
 remote_start_all=Uzakta Hepsini Ba\u015Flat
 remote_stop=Uzakta Durdur
 remote_stop_all=Uzakta Hepsini Durdur
 remove=Kald\u0131r
 rename=Girdiyi yeniden adland\u0131r
 report=Rapor
 report_bar_chart=Bar Grafi\u011Fi
 report_bar_graph_url=Adres (URL)
 report_base_directory=Temel Dizin
 report_chart_caption=Grafik Ba\u015Fl\u0131\u011F\u0131
 report_chart_x_axis=X Ekseni
 report_chart_x_axis_label=X Ekseni Etiketi
 report_chart_y_axis=Y Ekseni
 report_chart_y_axis_label=Y Ekseni Etiketi
 report_line_graph=\u00C7izgi Grafik
 report_line_graph_urls=\u0130\u00E7erilen Adresler (URLler)
 report_output_directory=Rapor i\u00E7in \u00C7\u0131kt\u0131 Dizini
 report_page=Rapor Sayfas\u0131
 report_page_element=Sayfa Eleman\u0131
 report_page_footer=Sayfa Altl\u0131\u011F\u0131
 report_page_header=Sayfa Ba\u015Fl\u0131\u011F\u0131
 report_page_index=Sayfa \u0130ndeksi Yarat
 report_page_intro=Sayfa Giri\u015Fi
 report_page_style_url=CSS Adresi (URL)
 report_page_title=Sayfa Ba\u015Fl\u0131\u011F\u0131
 report_pie_chart=Elma Grafi\u011Fi
 report_plan=Rapor Plan\u0131
 report_select=Se\u00E7
 report_summary=Rapor \u00D6zeti
 report_table=Rapor Tablosu
 report_writer=Rapor Yaz\u0131c\u0131
 report_writer_html=HTML Raporu Yaz\u0131c\u0131
 request_data=\u0130stek Verisi
 reset_gui=Aray\u00FCz\u00FC S\u0131f\u0131rla
 restart=Ba\u015Ftan ba\u015Flat
 resultaction_title=Sonu\u00E7 Durumu Eylem \u0130\u015Fleyici
 resultsaver_errors=Sadece Ba\u015Far\u0131s\u0131z Cevaplar\u0131 Kaydet
 resultsaver_prefix=Dosya ismi \u00F6neki\:
 resultsaver_title=Cevaplar\u0131 dosyaya kaydet
 retobj=Nesne d\u00F6n
 reuseconnection=Ba\u011Flant\u0131y\u0131 tekrar kullan
 revert_project=Geri d\u00F6nd\u00FCr
 revert_project?=Projeyi geri d\u00F6nd\u00FCr?
 root=K\u00F6k
 root_title=K\u00F6k
 run=\u00C7al\u0131\u015Ft\u0131r
 running_test=Testi \u00E7al\u0131\u015Ft\u0131r
 runtime_controller_title=\u00C7al\u0131\u015Fma Zaman\u0131 Denet\u00E7isi
 runtime_seconds=\u00C7al\u0131\u015Fma Zaman\u0131 (saniyeler)
 sample_result_save_configuration=\u00D6rnek Sonu\u00E7 Kaydetme Ayar\u0131
 sampler_label=Etiket
 sampler_on_error_action=\u00D6rnekleyici hatas\u0131ndan sonra yap\u0131lacak hareket
 sampler_on_error_continue=Devam et
 sampler_on_error_stop_test=Testi Durdur
 sampler_on_error_stop_thread=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Durdur
 save=Kaydet
 save?=Kaydet?
 save_all_as=Test Plan\u0131 olarak Kaydet
 save_as=Se\u00E7imi Farkl\u0131 Kaydet...
 save_as_error=Birden fazla madde se\u00E7ili\!
 save_as_image=D\u00FC\u011F\u00FCm\u00FC Resim olarak Kaydet
 save_as_image_all=Ekran\u0131 Resim olarak Kaydet
 save_assertionresultsfailuremessage=Do\u011Frulama Ba\u015Far\u0131s\u0131zl\u0131\u011F\u0131 Mesaj\u0131n\u0131 Kaydet
 save_assertions=Do\u011Frulama Sonu\u00E7lar\u0131n\u0131 Kaydet (XML)
 save_asxml=XML olarak Kaydet
 save_bytes=Bayt say\u0131s\u0131n\u0131 kaydet
 save_code=Cevap Kodunu Kaydet
 save_datatype=Data Tipini Kaydet
 save_encoding=Kodlamay\u0131 Kaydet
 save_fieldnames=Alan \u0130simlerini Kaydet (CSV)
 save_filename=Cevap Dosya \u0130smini Kaydet
 save_graphics=Grafi\u011Fi Kaydet
 save_hostname=Sunucu \u0130smini Kaydet
 save_label=Etiketi Kaydet
 save_latency=Gecikme S\u00FCresi Kaydet
 save_message=Cevap Mesaj\u0131n\u0131 Kaydet
 save_overwrite_existing_file=Se\u00E7ili dosya zaten mevcut, \u00FCst\u00FCne yazmak ister misiniz?
 save_requestheaders=\u0130stek Ba\u015Fl\u0131klar\u0131n\u0131 Kaydet (XML)
 save_responsedata=Cevap Verisini Kaydet (XML)
 save_responseheaders=Cevap Ba\u015Fl\u0131klar\u0131n\u0131 Kaydet (XML)
 save_samplecount=\u00D6rnek ve Hata Say\u0131s\u0131n\u0131 Kaydet
 save_samplerdata=\u00D6rnekleyici Verisini Kaydet (XML)
 save_subresults=Alt Sonu\u00E7lar\u0131 Kaydet (XML)\n
 save_success=Ba\u015Far\u0131y\u0131 Kaydet
 save_threadcounts=Aktif \u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Say\u0131s\u0131n\u0131 Kaydet
 save_threadname=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 \u0130smini Kaydet
 save_time=Ge\u00E7en Zaman\u0131 Kaydet
 save_timestamp=Tarih Bilgisini Kaydet
 save_url=URL Adresini Kaydet
 sbind=Yaln\u0131z ba\u011Fla/ba\u011Flama
 scheduler=Planla
 scheduler_configuration=Planlama Ayar\u0131
 scope=Kapsam
 search_base=Ara\u015Ft\u0131rma temeli
 search_filter=Ara\u015Ft\u0131rma Filtresi
 search_test=Ara\u015Ft\u0131rma Testi
 searchbase=Ara\u015Ft\u0131rma temeli
 searchfilter=Ara\u015Ft\u0131rma Filtresi
 searchtest=Ara\u015Ft\u0131rma testi
 second=saniye
 secure=G\u00FCvenli
 send_file=\u0130stekle Beraber Dosya G\u00F6nder\:
 send_file_browse=G\u00F6zat...
 send_file_filename_label=Dosya Yolu\:
 send_file_mime_label=MIME Tipi\:
 send_file_param_name_label=Parametre \u0130smi\:
 server=Sunucu \u0130smi veya IP\:
 servername=Sunucu \u0130smi \:
 session_argument_name=Oturum Arg\u00FCman\u0131 \u0130smi
 should_save=Testi \u00E7al\u0131\u015Ft\u0131rmadan \u00F6nce test plan\u0131n\u0131 kaydetmeniz tavsiye edilir.\nE\u011Fer destek veri dosyalar\u0131 kullan\u0131yorsan\u0131z (\u00F6r\: CSV Veri K\u00FCmesi ya da _StringFromFile), \u00F6ncelikle test beti\u011Fini kaydetmeniz \u00F6nemlidir.\n\u00D6ncelikle test plan\u0131n\u0131 kaydetmek istiyor musunuz?
 shutdown=Kapat
 simple_config_element=Basit Ayar Eleman\u0131
 simple_data_writer_title=Basit Veri Yaz\u0131c\u0131
 size_assertion_comparator_error_equal=e\u015Fittir
 size_assertion_comparator_error_greater=b\u00FCy\u00FCkt\u00FCr
 size_assertion_comparator_error_greaterequal=b\u00FCy\u00FCkt\u00FCr ya da e\u015Fittir
 size_assertion_comparator_error_less=k\u00FC\u00E7\u00FCkt\u00FCr
 size_assertion_comparator_error_lessequal=k\u00FC\u00E7\u00FCkt\u00FCr ya da e\u015Fittir
 size_assertion_comparator_error_notequal=e\u015Fit de\u011Fildir
 size_assertion_comparator_label=Kar\u015F\u0131la\u015Ft\u0131rma Tipi
 size_assertion_failure=Sonu\u00E7 boyutunda yanl\u0131\u015Fl\u0131k\: {0} bayt, halbuki {1} {2} bayt olmas\u0131 bekleniyordu.
 size_assertion_input_error=L\u00FCtfen ge\u00E7erli pozitif bir tamsay\u0131 girin.
 size_assertion_label=Boyut (bayt)\:
 size_assertion_size_test=Do\u011Frulanacak Boyut
 size_assertion_title=Boyut Do\u011Frulamas\u0131
 soap_data_title=Soap/XML-RPC Verisi
 soap_sampler_title=SOAP/XML-RPC \u0130ste\u011Fi
 soap_send_action=SOAPAction g\u00F6nder\: 
 ssl_alias_prompt=L\u00FCtfen tercih etti\u011Finiz k\u0131saltmay\u0131 girin
 ssl_alias_select=Test k\u0131saltman\u0131z\u0131 se\u00E7iniz
 ssl_alias_title=\u0130stemci K\u0131saltmas\u0131
 ssl_error_title=Anahtar Kayd\u0131 Problemi
 ssl_pass_prompt=L\u00FCtfen \u015Fifrenizi girin
 ssl_pass_title=Anahtar Kayd\u0131 Problemi
 ssl_port=SSL Portu
 sslmanager=SSL Y\u00F6neticisi
 start=Ba\u015Flat
 starttime=Ba\u015Flama Zaman\u0131
 stop=Durdur
 stopping_test=T\u00FCm i\u015F par\u00E7ac\u0131klar\u0131n\u0131 kapat\u0131yor. L\u00FCtfen sab\u0131rl\u0131 olun.
 stopping_test_title=Testleri Durduruyor
 string_from_file_file_name=Dosya tam yolunu girin
 string_from_file_seq_final=Dosya s\u0131ra numaras\u0131n\u0131 sonland\u0131r (iste\u011Fe ba\u011Fl\u0131)
 string_from_file_seq_start=Dosya s\u0131ra numaras\u0131n\u0131 ba\u015Flat (iste\u011Fe ba\u011Fl\u0131)
 summariser_title=\u00D6zet Sonu\u00E7lar Olu\u015Ftur
 summary_report=\u00D6zet Rapor
 switch_controller_label=Dallanma (Switch) De\u011Feri
 switch_controller_title=Dallanma (Switch) Denet\u00E7isi
 table_visualizer_bytes=Bayt
 table_visualizer_sample_num=\u00D6rnek \#
 table_visualizer_sample_time=\u00D6rnek Zaman\u0131(ms)
 table_visualizer_start_time=Ba\u015Flama Zaman\u0131
 table_visualizer_status=Durum
 table_visualizer_success=Ba\u015Far\u0131
 table_visualizer_thread_name=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 \u0130smi
 table_visualizer_warning=Uyar\u0131
 tcp_config_title=TCP \u00D6rnekleyici Ayar\u0131
 tcp_nodelay=Hi\u00E7 Gecikmeye Ayarla
 tcp_port=Port Numaras\u0131\:
 tcp_request_data=G\u00F6nderilecek metin
 tcp_sample_title=TCP \u00D6rnekleyici
 tcp_timeout=Zaman A\u015F\u0131m\u0131 (milisaniye)
 template_field=\u015Eablon\:
 test_action_action=Hareket
 test_action_duration=S\u00FCre (milisaniye)
 test_action_pause=Duraklat
 test_action_stop=Durdur
 test_action_target=Hedef
 test_action_target_test=T\u00FCm \u0130\u015F Par\u00E7ac\u0131klar\u0131
 test_action_target_thread=\u015Eu Anki \u0130\u015F Par\u00E7ac\u0131\u011F\u0131
 test_action_title=Test Hareketi
 test_configuration=Test Ayar\u0131
 test_plan=Test Plan\u0131
 test_plan_classpath_browse=S\u0131n\u0131f yoluna (classpath) dizin veya jar ekle
 testconfiguration=Test Ayar\u0131
 testplan.serialized=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Gruplar\u0131n\u0131 Ard\u0131\u015F\u0131k \u00C7al\u0131\u015Ft\u0131r (\u00F6r\: her defas\u0131nda bir grup \u00E7al\u0131\u015Ft\u0131r)
 testplan_comments=Yorumlar\:
 thread_delay_properties=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Gecikme \u00D6zellikleri
 thread_group_title=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Grubu
 thread_properties=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 \u00D6zellikleri
 threadgroup=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131 Grubu
 throughput_control_bynumber_label=Toplam Y\u00FCr\u00FCtme
 throughput_control_bypercent_label=Y\u00FCr\u00FCtme Y\u00FCzdesi
 throughput_control_perthread_label=Kullan\u0131c\u0131 Ba\u015F\u0131na
 throughput_control_title=Transfer Oran\u0131 Denet\u00E7isi
 throughput_control_tplabel=Transfer Oran\u0131
 time_format=Metni SimpleDateFormat i\u00E7in bi\u00E7imlendir (iste\u011Fe ba\u011Fl\u0131)
 timelim=Zaman s\u0131n\u0131r\u0131
 tr=T\u00FCrk\u00E7e
 transaction_controller_parent=Ebeveyn \u00F6rnek olu\u015Ftur
 transaction_controller_title=\u0130\u015Flem (transaction) Denet\u00E7isi
 unbind=\u0130\u015F Par\u00E7ac\u0131\u011F\u0131n\u0131 B\u0131rak
 uniform_timer_delay=Sabit Gecikme S\u0131n\u0131r\u0131 (milisaniye)\:
 uniform_timer_memo=Tek bi\u00E7imli da\u011F\u0131l\u0131mla rastgele gecikme ekler
 uniform_timer_range=Maksimum Rastgele Gecikme (milisaniye)\:
 uniform_timer_title=Tek Bi\u00E7imli Rastgele Zamanlay\u0131c\u0131
 update_per_iter=Her Tekrar i\u00E7in Bir Defa Yenile
 upload=Dosya Y\u00FCkleme
 upper_bound=\u00DCst S\u0131n\u0131r
 url=Adres (URL)
 url_config_protocol=Protokol\:
 url_config_title=HTTP \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 url_full_config_title=UrlFull \u00D6rne\u011Fi
 url_multipart_config_title=HTTP Multipart \u0130ste\u011Fi \u00D6ntan\u0131ml\u0131 De\u011Ferleri
 use_keepalive=Canl\u0131Tut (KeepAlive) kullan
 use_multipart_for_http_post=HTTP POST i\u00E7in multipart/form-data kullan
 use_recording_controller=Kaydetme Denet\u00E7isi Kullan
 user=Kullan\u0131c\u0131
 user_defined_test=Kullan\u0131c\u0131 Tan\u0131ml\u0131 Test
 user_defined_variables=Kullan\u0131c\u0131 Tan\u0131ml\u0131 De\u011Fi\u015Fkenler
 user_param_mod_help_note=(Buray\u0131 de\u011Fi\u015Ftirme. Onun yerine, JMeter'in /bin dizinindeki dosya ismini d\u00FCzenle)
 user_parameters_table=Parametreler
 user_parameters_title=Kullan\u0131c\u0131 Parametreleri
 userdn=Kullan\u0131c\u0131 ismi
 username=Kullan\u0131c\u0131 ismi
 userpw=\u015Eifre
 value=De\u011Fer
 var_name=Referans \u0130smi
 variable_name_param=De\u011Fi\u015Fken ismi (de\u011Fi\u015Fken ve fonksiyon referanslar\u0131 i\u00E7erebilir)
 view_graph_tree_title=Grafik A\u011Fac\u0131n\u0131 G\u00F6ster
 view_results_in_table=Sonu\u00E7 Tablosunu G\u00F6ster
 view_results_render_html=HTML i\u015Fle
 view_results_render_json=JSON i\u015Fle
 view_results_render_text=Metin G\u00F6ster
 view_results_render_xml=XML \u0130\u015Fle
 view_results_tab_assertion=Do\u011Frulama sonucu
 view_results_tab_request=\u0130stek
 view_results_tab_response=Cevap verisi
 view_results_tab_sampler=\u00D6rnekleyici sonucu
 view_results_title=Sonu\u00E7lar\u0131 G\u00F6ster
 view_results_tree_title=Sonu\u00E7lar\u0131 G\u00F6sterme A\u011Fac\u0131
 warning=Uyar\u0131\!
 web_request=HTTP \u0130ste\u011Fi
 web_server=A\u011F Sunucusu
 web_server_client=\u0130stemci uygulamas\u0131\:
 web_server_domain=Sunucu \u0130smi veya IP\:
 web_server_port=Port Numaras\u0131\:
 web_testing2_title=HTTP \u0130ste\u011Fi HTTPClient
 web_testing_embedded_url_pattern=G\u00F6m\u00FCl\u00FC Adresler (URL) \u00F6rt\u00FC\u015Fmeli\:
 web_testing_retrieve_images=HTML Dosyalardan T\u00FCm G\u00F6m\u00FCl\u00FC Kaynaklar\u0131 Al
 web_testing_title=HTTP \u0130ste\u011Fi
 while_controller_label=Ko\u015Ful (fonksiyon veya de\u011Fi\u015Fken)
 while_controller_title=While Denet\u00E7isi
 workbench_title=Tezgah
 xml_assertion_title=XML Do\u011Frulamas\u0131
 xml_namespace_button=Namespace kullan
 xml_tolerant_button=Ho\u015Fg\u00F6r\u00FClen XML/HTML Ayr\u0131\u015Ft\u0131r\u0131c\u0131
 xml_validate_button=XML'i do\u011Frula
 xml_whitespace_button=G\u00F6r\u00FCnmeyen Karakterleri Yoksay
 xmlschema_assertion_label=Dosya \u0130smi\:
 xmlschema_assertion_title=XML \u015Eemas\u0131 Do\u011Frulamas\u0131
 xpath_assertion_button=Do\u011Frula
 xpath_assertion_check=XPath \u0130fadesini Kontrol Et
 xpath_assertion_error=XPath'te Hata
 xpath_assertion_failed=Ge\u00E7ersiz XPath \u0130fadesi
 xpath_assertion_negate=E\u011Fer e\u015Fle\u015Fen yoksa True
 xpath_assertion_option=XML Ayr\u0131\u015Ft\u0131r\u0131c\u0131 Se\u00E7enekleri
 xpath_assertion_test=XPath Do\u011Frulamas\u0131
 xpath_assertion_tidy=Dene ve girdiyi d\u00FCzenle
 xpath_assertion_title=XPath Do\u011Frulamas\u0131
 xpath_assertion_valid=Ge\u00E7erli XPath \u0130fadesi
 xpath_assertion_validation=XML'i DTD'ye g\u00F6re kontrol et
 xpath_assertion_whitespace=G\u00F6r\u00FCnmeyen Karakterleri Yoksay
 xpath_expression=Kar\u015F\u0131la\u015Ft\u0131r\u0131lacak XPath ifadesi
 xpath_extractor_query=XPath sorgusu\:
 xpath_extractor_title=XPath \u00C7\u0131kar\u0131c\u0131
 xpath_file_file_name=De\u011Ferlerin okunaca\u011F\u0131 XML dosyas\u0131
 xpath_tidy_quiet=Sessiz
 xpath_tidy_report_errors=Hatalar\u0131 raporla
 xpath_tidy_show_warnings=Uyar\u0131lar\u0131 g\u00F6ster
 you_must_enter_a_valid_number=Ge\u00E7erli bir rakam girmelisiniz
 zh_cn=\u00C7ince (Basitle\u015Ftirilmi\u015F)
 zh_tw=\u00C7ince (Geleneksel)
diff --git a/src/core/org/apache/jmeter/resources/messages_zh_CN.properties b/src/core/org/apache/jmeter/resources/messages_zh_CN.properties
index 07120e347..f37222053 100644
--- a/src/core/org/apache/jmeter/resources/messages_zh_CN.properties
+++ b/src/core/org/apache/jmeter/resources/messages_zh_CN.properties
@@ -1,405 +1,404 @@
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
 
 about=\u5173\u4E8EApache JMeter
 add=\u6DFB\u52A0
 add_as_child=\u6DFB\u52A0\u5B50\u8282\u70B9
 add_parameter=\u6DFB\u52A0\u53D8\u91CF
 add_pattern=\u6DFB\u52A0\u6A21\u5F0F\uFF1A
 add_test=\u6DFB\u52A0\u6D4B\u8BD5
 add_user=\u6DFB\u52A0\u7528\u6237
 add_value=\u6DFB\u52A0\u6570\u503C
 aggregate_report=\u805A\u5408\u62A5\u544A
 aggregate_report_total_label=\u603B\u4F53
 als_message=\u6CE8\u610F\uFF1A\u8BBF\u95EE\u65E5\u5FD7\u89E3\u6790\u5668\uFF08Access Log Parser\uFF09\u662F\u901A\u7528\u7684\u5E76\u5141\u8BB8\u5B9A\u4E49\u63D2\u4EF6
 als_message2=\u81EA\u5B9A\u4E49\u7684\u89E3\u6790\u5668\u3002\u8981\u8FD9\u4E48\u505A\uFF0C\u5B9E\u73B0LogParser\uFF0C\u6DFB\u52A0jar\u5230
 als_message3=/lib\u76EE\u5F55\u5E76\u5728sampler\u4E2D\u8F93\u5165\u7C7B\u540D\u79F0\u3002
 analyze=\u5206\u6790\u6570\u636E\u6587\u4EF6...
 anchor_modifier_title=HTML\u94FE\u63A5\u89E3\u6790\u5668
 appearance=\u5916\u89C2
 argument_must_not_be_negative=\u53C2\u6570\u4E0D\u5141\u8BB8\u662F\u8D1F\u503C\uFF01
 assertion_code_resp=\u54CD\u5E94\u4EE3\u7801
 assertion_contains=\u5305\u62EC
 assertion_matches=\u5339\u914D
 assertion_message_resp=\u54CD\u5E94\u4FE1\u606F
 assertion_not=\u5426
 assertion_or=\u6216\u8005
 assertion_pattern_match_rules=\u6A21\u5F0F\u5339\u914D\u89C4\u5219
 assertion_patterns_to_test=\u8981\u6D4B\u8BD5\u7684\u6A21\u5F0F
 assertion_resp_field=\u8981\u6D4B\u8BD5\u7684\u54CD\u5E94\u5B57\u6BB5
 assertion_text_resp=\u54CD\u5E94\u6587\u672C
 assertion_textarea_label=\u65AD\u8A00\uFF1A
 assertion_title=\u54CD\u5E94\u65AD\u8A00
 assertion_url_samp=URL\u6837\u672C
 assertion_visualizer_title=\u65AD\u8A00\u7ED3\u679C
 auth_base_url=\u57FA\u7840URL
 auth_manager_title=HTTP\u6388\u6743\u7BA1\u7406\u5668
 auths_stored=\u5B58\u50A8\u5728\u6388\u6743\u7BA1\u7406\u5668\u4E2D\u7684\u6388\u6743
 browse=\u6D4F\u89C8...
 bsf_sampler_title=BSF \u53D6\u6837\u5668
 bsf_script=\u8981\u8FD0\u884C\u7684\u811A\u672C
 bsf_script_file=\u8981\u8FD0\u884C\u7684\u811A\u672C\u6587\u4EF6
 bsf_script_language=\u811A\u672C\u8BED\u8A00\uFF1A
 bsf_script_parameters=\u4F20\u9012\u7ED9\u811A\u672C/\u6587\u4EF6\u7684\u53C2\u6570\uFF1A
 bsh_assertion_title=BeanShell\u65AD\u8A00
 bsh_function_expression=\u8868\u8FBE\u5F0F\u6C42\u503C
 bsh_script_file=\u811A\u672C\u6587\u4EF6
 bsh_script_parameters=\u53C2\u6570\uFF08-> String Parameters \u548C String [ ]bash.args\uFF09
 busy_testing=\u6B63\u5728\u6D4B\u8BD5\uFF0C\u8BF7\u5728\u4FEE\u6539\u8BBE\u7F6E\u524D\u505C\u6B62\u6D4B\u8BD5
 cancel=\u53D6\u6D88
 cancel_exit_to_save=\u6D4B\u8BD5\u6761\u76EE\u672A\u5B58\u50A8\u3002\u4F60\u60F3\u5728\u9000\u51FA\u524D\u5B58\u50A8\u5417\uFF1F
 cancel_new_to_save=\u6D4B\u8BD5\u6761\u76EE\u672A\u5B58\u50A8\u3002\u4F60\u60F3\u5728\u6E05\u7A7A\u6D4B\u8BD5\u8BA1\u5212\u524D\u5B58\u50A8\u5417\uFF1F
 choose_function=\u9009\u62E9\u4E00\u4E2A\u529F\u80FD
 choose_language=\u9009\u62E9\u8BED\u8A00
 clear=\u6E05\u9664
 clear_all=\u6E05\u9664\u5168\u90E8
 clear_cookies_per_iter=\u6BCF\u6B21\u53CD\u590D\u6E05\u9664Cookies \uFF1F
 column_delete_disallowed=\u4E0D\u5141\u8BB8\u5220\u9664\u6B64\u5217
 column_number=CSV\u6587\u4EF6\u5217\u53F7| next| *alias
 constant_throughput_timer_memo=\u5728\u53D6\u6837\u95F4\u6DFB\u52A0\u5EF6\u8FDF\u6765\u83B7\u5F97\u56FA\u5B9A\u7684\u541E\u5410\u91CF
 constant_timer_delay=\u7EBF\u7A0B\u5EF6\u8FDF\uFF08\u6BEB\u79D2\uFF09\uFF1A
 constant_timer_memo=\u5728\u53D6\u6837\u95F4\u6DFB\u52A0\u56FA\u5B9A\u5EF6\u8FDF
 constant_timer_title=\u56FA\u5B9A\u5B9A\u65F6\u5668
 controller=\u63A7\u5236\u5668
 cookie_manager_title=HTTP Cookie \u7BA1\u7406\u5668
 cookies_stored=\u5B58\u50A8\u5728Cookie\u7BA1\u7406\u5668\u4E2D\u7684Cookie
 copy=\u590D\u5236
 counter_config_title=\u8BA1\u6570\u5668
 counter_per_user=\u4E0E\u6BCF\u7528\u6237\u72EC\u7ACB\u7684\u8DDF\u8E2A\u8BA1\u6570\u5668
 cut=\u526A\u5207
 cut_paste_function=\u62F7\u8D1D\u5E76\u7C98\u8D34\u51FD\u6570\u5B57\u7B26\u4E32
 database_sql_query_string=SQL\u67E5\u8BE2\u5B57\u7B26\u4E32\uFF1A
 database_sql_query_title=JDBC SQL \u67E5\u8BE2\u7F3A\u7701\u503C
 de=\u5FB7\u8BED
 default_parameters=\u7F3A\u7701\u53C2\u6570
 default_value_field=\u7F3A\u7701\u503C\uFF1A
 delay=\u542F\u52A8\u5EF6\u8FDF\uFF08\u79D2\uFF09
 delete=\u5220\u9664
 delete_parameter=\u5220\u9664\u53D8\u91CF
 delete_test=\u5220\u9664\u6D4B\u8BD5
 delete_user=\u5220\u9664\u7528\u6237
 disable=\u7981\u7528
 domain=\u57DF
 duration=\u6301\u7EED\u65F6\u95F4\uFF08\u79D2\uFF09
 duration_assertion_duration_test=\u65AD\u8A00\u6301\u7EED\u65F6\u95F4
 duration_assertion_failure=\u64CD\u4F5C\u6301\u7EED\u592A\u957F\u65F6\u95F4\uFF1A\u4ED6\u82B1\u8D39\u4E86{0}\u6BEB\u79D2\uFF0C\u4F46\u4E0D\u5E94\u8BE5\u8D85\u8FC7{1}\u6BEB\u79D2\u3002
 duration_assertion_input_error=\u8BF7\u8F93\u5165\u4E00\u4E2A\u6709\u6548\u7684\u6B63\u6574\u6570\u3002
 duration_assertion_label=\u6301\u7EED\u65F6\u95F4\uFF08\u6BEB\u79D2\uFF09\uFF1A
 duration_assertion_title=\u65AD\u8A00\u6301\u7EED\u65F6\u95F4
 edit=\u7F16\u8F91
 email_results_title=\u7535\u5B50\u90AE\u4EF6\u7ED3\u679C
 en=\u82F1\u8BED
 enable=\u542F\u7528
 encode?=\u7F16\u7801\uFF1F
 encoded_value=URL\u7F16\u7801\u540E\u7684\u503C
 endtime=\u7ED3\u675F\u65F6\u95F4
 entry_dn=\u5165\u53E3DN
 error_loading_help=\u52A0\u8F7D\u5E2E\u52A9\u9875\u9762\u51FA\u9519
 error_occurred=\u53D1\u751F\u9519\u8BEF
 example_data=\u6837\u672C\u6570\u636E
 example_title=\u793A\u4F8B\u53D6\u6837\u5668
 exit=\u9000\u51FA
 expiration=\u8FC7\u671F
 field_name=\u5B57\u6BB5\u540D\u6210
 file=\u6587\u4EF6
 file_already_in_use=\u6587\u4EF6\u6B63\u5728\u4F7F\u7528
 file_visualizer_append=\u6DFB\u52A0\u5230\u5DF2\u7ECF\u5B58\u5728\u7684\u6570\u636E\u6587\u4EF6
 file_visualizer_auto_flush=\u5728\u6BCF\u6B21\u6570\u636E\u53D6\u6837\u540E\u81EA\u52A8\u66F4\u65B0
 file_visualizer_browse=\u6D4F\u89C8...
 file_visualizer_close=\u5173\u95ED
 file_visualizer_file_options=\u6587\u4EF6\u64CD\u4F5C
 file_visualizer_filename=\u6587\u4EF6\u540D
 file_visualizer_flush=\u66F4\u65B0
 file_visualizer_missing_filename=\u6CA1\u6709\u6307\u5B9A\u8F93\u51FA\u6587\u4EF6\u540D\u3002
 file_visualizer_open=\u6253\u5F00
 file_visualizer_output_file=\u6240\u6709\u6570\u636E\u5199\u5165\u4E00\u4E2A\u6587\u4EF6
 file_visualizer_submit_data=\u5305\u62EC\u88AB\u63D0\u4EA4\u7684\u6570\u636E
 file_visualizer_title=\u6587\u4EF6\u62A5\u544A\u5668
 file_visualizer_verbose=\u8BE6\u7EC6\u7684\u8F93\u51FA
 filename=\u6587\u4EF6\u540D\u79F0
 follow_redirects=\u8DDF\u968F\u91CD\u5B9A\u5411
 follow_redirects_auto=\u81ea\u52a8\u91cd\u5b9a\u5411
 foreach_controller_title=ForEach\u63A7\u5236\u5668
 foreach_input=\u8F93\u5165\u53D8\u91CF\u524D\u7F00
 foreach_output=\u8F93\u51FA\u53D8\u91CF\u540D\u79F0
 ftp_sample_title=FTP\u8BF7\u6C42\u7F3A\u7701\u503C
 ftp_testing_title=FTP\u8BF7\u6C42
 function_dialog_menu_item=\u51FD\u6570\u52A9\u624B\u5BF9\u8BDD\u6846
 function_helper_title=\u51FD\u6570\u52A9\u624B
 function_name_param=\u51FD\u6570\u540D\u79F0\u3002\u7528\u4E8E\u5B58\u50A8\u5728\u6D4B\u8BD5\u8BA1\u5212\u4E2D\u5176\u4ED6\u7684\u65B9\u5F0F\u4F7F\u7528\u7684\u503C\u3002
 function_params=\u51FD\u6570\u53C2\u6570
 functional_mode=\u51FD\u6570\u6D4B\u8BD5\u6A21\u5F0F
 functional_mode_explanation=\u53EA\u6709\u5F53\u4F60\u9700\u8981\u8BB0\u5F55\u6BCF\u4E2A\u8BF7\u6C42\u4ECE\u670D\u52A1\u5668\u53D6\u5F97\u7684\u6570\u636E\u5230\u6587\u4EF6\u65F6\n\u624D\u9700\u8981\u9009\u62E9\u51FD\u6570\u6D4B\u8BD5\u6A21\u5F0F\u3002\n\n\u9009\u62E9\u8FD9\u4E2A\u9009\u9879\u5F88\u5F71\u54CD\u6027\u80FD\u3002\n
 gaussian_timer_delay=\u56FA\u5B9A\u5EF6\u8FDF\u504F\u79FB\uFF08\u6BEB\u79D2\uFF09\uFF1A
 gaussian_timer_memo=\u6DFB\u52A0\u4E00\u4E2A\u968F\u673A\u7684\u9AD8\u65AF\u5206\u5E03\u5EF6\u8FDF
 gaussian_timer_range=\u504F\u5DEE\uFF08\u6BEB\u79D2\uFF09\uFF1A
 gaussian_timer_title=\u9AD8\u65AF\u968F\u673A\u5B9A\u65F6\u5668
 generate=\u751F\u6210
 generator=\u751F\u6210\u5668\u7C7B\u540D\u79F0
 generator_cnf_msg=\u4E0D\u80FD\u627E\u5230\u751F\u6210\u5668\u7C7B\u3002\u8BF7\u786E\u5B9A\u4F60\u5C06jar\u6587\u4EF6\u653E\u7F6E\u5728/lib\u76EE\u5F55\u4E2D\u3002
 generator_illegal_msg=\u7531\u4E8EIllegalAcessException\uFF0C\u4E0D\u80FD\u8BBF\u95EE\u751F\u6210\u5668\u7C7B\u3002
 generator_instantiate_msg=\u4E0D\u80FD\u521B\u5EFA\u751F\u6210\u5668\u89E3\u6790\u5668\u7684\u5B9E\u4F8B\u3002\u8BF7\u786E\u4FDD\u751F\u6210\u5668\u5B9E\u73B0\u4E86Generator\u63A5\u53E3\u3002
 graph_choose_graphs=\u8981\u663E\u793A\u7684\u56FE\u5F62
 graph_full_results_title=\u56FE\u5F62\u7ED3\u679C
 graph_results_average=\u5E73\u5747
 graph_results_data=\u6570\u636E
 graph_results_deviation=\u504F\u79BB
 graph_results_latest_sample=\u6700\u65B0\u6837\u672C
 graph_results_median=\u4E2D\u503C
 graph_results_no_samples=\u6837\u672C\u6570\u76EE
 graph_results_throughput=\u541E\u5410\u91CF
 graph_results_title=\u56FE\u5F62\u7ED3\u679C
 grouping_add_separators=\u5728\u7EC4\u95F4\u6DFB\u52A0\u5206\u9694
 grouping_in_controllers=\u6BCF\u4E2A\u7EC4\u653E\u5165\u4E00\u4E2A\u65B0\u7684\u63A7\u5236\u5668
 grouping_mode=\u5206\u7EC4\uFF1A
 grouping_no_groups=\u4E0D\u5BF9\u6837\u672C\u5206\u7EC4
 grouping_store_first_only=\u53EA\u5B58\u50A8\u6BCF\u4E2A\u7EC4\u7684\u7B2C\u4E00\u4E2A\u6837\u672C
 header_manager_title=HTTP\u4FE1\u606F\u5934\u7BA1\u7406\u5668
 headers_stored=\u4FE1\u606F\u5934\u5B58\u50A8\u5728\u4FE1\u606F\u5934\u7BA1\u7406\u5668\u4E2D
 help=\u5E2E\u52A9
 http_response_code=HTTP\u578B\u5E94\u4EE3\u7801
 http_url_rewriting_modifier_title=HTTP URL \u91CD\u5199\u4FEE\u9970\u7B26
 http_user_parameter_modifier=HTTP \u7528\u6237\u53C2\u6570\u4FEE\u9970\u7B26
 id_prefix=ID\u524D\u7F00
 id_suffix=ID\u540E\u7F00
 if_controller_label=\u6761\u4EF6
 if_controller_title=\u5982\u679C\uFF08If\uFF09\u63A7\u5236\u5668
 ignore_subcontrollers=\u5FFD\u7565\u8D44\u63A7\u5236\u5668\u5757
 include_equals=\u5305\u542B\u7B49\u4E8E\uFF1F
 increment=\u9012\u589E
 infinite=\u6C38\u8FDC
 insert_after=\u4E4B\u540E\u63D2\u5165
 insert_before=\u4E4B\u524D\u63D2\u5165
 insert_parent=\u63D2\u5165\u4E0A\u7EA7
 interleave_control_title=\u4EA4\u66FF\u63A7\u5236\u5668
 intsum_param_1=\u8981\u6DFB\u52A0\u7684\u7B2C\u4E00\u4E2A\u6574\u6570\u3002
 intsum_param_2=\u8981\u6DFB\u52A0\u7684\u7B2C\u4E8C\u4E2A\u6574\u6570\u2014\u2014\u66F4\u591A\u7684\u6574\u6570\u53EF\u4EE5\u901A\u8FC7\u6DFB\u52A0\u66F4\u591A\u7684\u53C2\u6570\u6765\u6C42\u548C\u3002
 invalid_data=\u65E0\u6548\u6570\u636E
 invalid_mail_server=\u90AE\u4EF6\u670D\u52A1\u5668\u4E0D\u53EF\u77E5\u3002
 iteration_counter_arg_1=TRUE\uFF0C\u6BCF\u4E2A\u7528\u6237\u6709\u81EA\u5DF1\u7684\u8BA1\u6570\u5668\uFF1BFALSE\uFF0C\u4F7F\u7528\u5168\u5C40\u8BA1\u6570\u5668
 iterator_num=\u5FAA\u73AF\u6B21\u6570
 java_request=Java\u8BF7\u6C42
 java_request_defaults=Java\u8BF7\u6C42\u9ED8\u8BA4\u503C
 jndi_config_title=JNDI\u914D\u7F6E
 jndi_lookup_name=\u8FDC\u7A0B\u63A5\u53E3
 jndi_lookup_title=JNDI\u67E5\u8BE2\u914D\u7F6E
 jndi_method_button_invoke=\u8C03\u7528
 jndi_method_button_reflect=\u53CD\u5C04
 jndi_method_home_name=\u672C\u5730\u65B9\u6CD5\u540D\u79F0
 jndi_method_home_parms=\u672C\u5730\u65B9\u6CD5\u53C2\u6570
 jndi_method_name=\u65B9\u6CD5\u914D\u7F6E
 jndi_method_remote_interface_list=\u8FDC\u7A0B\u63A5\u53E3
 jndi_method_remote_name=\u8FDC\u7A0B\u65B9\u6CD5\u540D\u79F0
 jndi_method_remote_parms=\u8FDC\u7A0B\u65B9\u6CD5\u53C2\u6570
 jndi_method_title=\u8FDC\u7A0B\u65B9\u6CD5\u914D\u7F6E
 jndi_testing_title=JNDI\u8BF7\u6C42
 jndi_url_jndi_props=JNDI\u5C5E\u6027
 ja=\u65E5\u8BED
 ldap_sample_title=LDAP\u8BF7\u6C42\u9ED8\u8BA4\u503C
 ldap_testing_title=LDAP\u8BF7\u6C42
 load=\u8F7D\u5165
 log_errors_only=\u4EC5\u65E5\u5FD7\u9519\u8BEF
 log_file=\u65E5\u5FD7\u6587\u4EF6\u4F4D\u7F6E
 log_parser=\u65E5\u5FD7\u89E3\u6790\u5668\u7C7B\u540D
 log_parser_cnf_msg=\u627E\u4E0D\u5230\u7C7B\u3002\u786E\u8BA4\u4F60\u5C06jar\u6587\u4EF6\u653E\u5728\u4E86/lib\u76EE\u5F55\u4E2D\u3002
 log_parser_illegal_msg=\u56E0\u4E3AIllegalAccessException\u4E0D\u80FD\u8BBF\u95EE\u7C7B\u3002
 log_parser_instantiate_msg=\u4E0D\u80FD\u521B\u5EFA\u65E5\u5FD7\u89E3\u6790\u5668\u5B9E\u4F8B\u3002\u786E\u8BA4\u89E3\u6790\u5668\u5B9E\u73B0\u4E86LogParser\u63A5\u53E3\u3002
 log_sampler=Tomcat\u8BBF\u95EE\u65E5\u5FD7\u53D6\u6837\u5668
 logic_controller_title=\u7B80\u5355\u63A7\u5236\u5668
 login_config=\u767B\u9646\u914D\u7F6E
 login_config_element=\u767B\u9646\u914D\u7F6E\u5143\u4EF6/\u7D20
 loop_controller_title=\u5FAA\u73AF\u63A7\u5236\u5668
 looping_control=\u5FAA\u73AF\u63A7\u5236
 lower_bound=\u8F83\u4F4E\u8303\u56F4
 mailer_attributes_panel=\u90AE\u4EF6\u5C5E\u6027
 mailer_error=\u4E0D\u80FD\u53D1\u9001\u90AE\u4EF6\u3002\u8BF7\u4FEE\u6B63\u9519\u8BEF\u3002
 mailer_visualizer_title=\u90AE\u4EF6\u89C2\u5BDF\u4EEA
 match_num_field=\u5339\u914D\u6570\u5B57\uFF080\u4EE3\u8868\u968F\u673A\uFF09\uFF1A
 max=\u6700\u5927\u503C
 maximum_param=\u4E00\u4E2A\u8303\u56F4\u5185\u5141\u8BB8\u7684\u6700\u5927\u503C
 md5hex_assertion_failure=MD5\u603B\u5408\u65AD\u8A00\u9519\u8BEF\uFF1A\u5F97\u5230\u4E86{0}\uFF0C\u4F46\u5E94\u8BE5\u662F{1}
 md5hex_assertion_md5hex_test=\u8981\u65AD\u8A00\u7684MD5Hex
 md5hex_assertion_title=MD5Hex\u65AD\u8A00
 menu_assertions=\u65AD\u8A00
-menu_close=\u5173\u95ED
 menu_config_element=\u914D\u7F6E\u5143\u4EF6
 menu_edit=\u7F16\u8F91
 menu_listener=\u76D1\u542C\u5668
 menu_logic_controller=\u903B\u8F91\u63A7\u5236\u5668
 menu_merge=\u5408\u5E76
 menu_modifiers=\u4FEE\u9970\u7B26
 menu_non_test_elements=\u975E\u6D4B\u8BD5\u5143\u4EF6
 menu_open=\u6253\u5F00
 menu_post_processors=\u540E\u7F6E\u5904\u7406\u5668
 menu_pre_processors=\u524D\u7F6E\u5904\u7406\u5668
 menu_response_based_modifiers=\u57FA\u4E8E\u76F8\u5E94\u7684\u4FEE\u9970\u7B26
 menu_timer=\u5B9A\u65F6\u5668
 metadata=\u539F\u6570\u636E
 method=\u65B9\u6CD5\uFF1A
 mimetype=MIME\u7C7B\u578B
 minimum_param=\u4E00\u4E2A\u8303\u56F4\u5185\u7684\u6700\u5C0F\u503C
 minute=\u5206\u949F
 modification_controller_title=\u4FEE\u6B63\u63A7\u5236\u5668
 modification_manager_title=\u4FEE\u6B63\u7BA1\u7406\u5668
 modify_test=\u4FEE\u6539\u6D4B\u8BD5
 module_controller_title=\u6A21\u5757\u63A7\u5236\u5668
 name=\u540D\u79F0\uFF1A
 new=\u65B0\u5EFA
 no=\u632A\u5A01\u8BED
 number_of_threads=\u7EBF\u7A0B\u6570\uFF1A
 once_only_controller_title=\u4EC5\u4E00\u6B21\u63A7\u5236\u5668
 open=\u6253\u5F00...
 option=\u9009\u9879
 optional_tasks=\u5176\u4ED6\u4EFB\u52A1
 paramtable=\u540C\u8BF7\u6C42\u4E00\u8D77\u53D1\u9001\u53C2\u6570\uFF1A
 password=\u5BC6\u7801
 paste=\u7C98\u8D34
 paste_insert=\u4F5C\u4E3A\u63D2\u5165\u7C98\u8D34
 path=\u8DEF\u5F84\uFF1A
 path_extension_choice=\u8DEF\u5F84\u6269\u5C55\uFF08\u4F7F\u7528";"\u4F5C\u5206\u9694\u7B26\uFF09
 patterns_to_exclude=\u6392\u9664\u6A21\u5F0F
 patterns_to_include=\u5305\u542B\u6A21\u5F0F
 port=\u7AEF\u53E3\uFF1A
 property_default_param=\u9ED8\u8BA4\u503C
 property_edit=\u7F16\u8F91
 property_editor.value_is_invalid_title=\u65E0\u6548\u8F93\u5165
 property_name_param=\u5C5E\u6027\u540D\u79F0
 property_undefined=\u672A\u5B9A\u4E49
 protocol=\u534F\u8BAE\uFF1A
 protocol_java_border=Java\u7C7B
 protocol_java_classname=\u7C7B\u540D\u79F0\uFF1A
 protocol_java_config_tile=\u914D\u7F6EJava\u6837\u672C
 protocol_java_test_title=Java\u6D4B\u8BD5
 proxy_assertions=\u6DFB\u52A0\u65AD\u8A00
 proxy_cl_error=\u5982\u679C\u6307\u5B9A\u4EE3\u7406\u670D\u52A1\u5668\uFF0C\u4E3B\u673A\u548C\u7AEF\u53E3\u5FC5\u987B\u6307\u5B9A
 proxy_headers=\u8BB0\u5F55HTTP\u4FE1\u606F\u5934
 proxy_separators=\u6DFB\u52A0\u5206\u9694\u7B26
 proxy_target=\u76EE\u6807\u63A7\u5236\u5668\uFF1A
 proxy_title=HTTP\u4EE3\u7406\u670D\u52A1\u5668
 random_control_title=\u968F\u673A\u63A7\u5236\u5668
 random_order_control_title=\u968F\u673A\u987A\u5E8F\u63A7\u5236\u5668
 record_controller_title=\u5F55\u5236\u63A7\u5236\u5668
 ref_name_field=\u5F15\u7528\u540D\u79F0\uFF1A
 regex_extractor_title=\u6B63\u5219\u8868\u8FBE\u5F0F\u63D0\u53D6\u5668
 regex_field=\u6B63\u5219\u8868\u8FBE\u5F0F\uFF1A
 regex_source=\u8981\u68C0\u67E5\u7684\u54CD\u5E94\u5B57\u6BB5
 regex_src_body=\u4E3B\u4F53
 regex_src_hdrs=\u4FE1\u606F\u5934
 regexfunc_param_1=\u7528\u4E8E\u4ECE\u524D\u4E00\u4E2A\u8BF7\u6C42\u641C\u7D22\u7ED3\u679C\u7684\u6B63\u5219\u8868\u8FBE\u5F0F
 remote_exit=\u8FDC\u7A0B\u9000\u51FA
 remote_exit_all=\u8FDC\u7A0B\u5168\u90E8\u9000\u51FA
 remote_start=\u8FDC\u7A0B\u542F\u52A8
 remote_start_all=\u8FDC\u7A0B\u5168\u90E8\u542F\u52A8
 remote_stop=\u8FDC\u7A0B\u505C\u6B62
 remote_stop_all=\u8FDC\u7A0B\u5168\u90E8\u505C\u6B62
 remove=\u5220\u9664
 report=\u62A5\u544A
 request_data=\u8BF7\u6C42\u6570\u636E
 restart=\u91CD\u542F
 resultsaver_prefix=\u6587\u4EF6\u540D\u79F0\u524D\u7F00\uFF1A
 resultsaver_title=\u4FDD\u5B58\u54CD\u5E94\u5230\u6587\u4EF6
 root=\u6839
 root_title=\u6839
 run=\u8FD0\u884C
 running_test=\u6B63\u5728\u8FD0\u884C\u7684\u6D4B\u8BD5
 sampler_on_error_action=\u5728\u53D6\u6837\u5668\u9519\u8BEF\u540E\u8981\u6267\u884C\u7684\u52A8\u4F5C
 sampler_on_error_continue=\u7EE7\u7EED
 sampler_on_error_stop_test=\u505C\u6B62\u6D4B\u8BD5
 sampler_on_error_stop_thread=\u505C\u6B62\u7EBF\u7A0B
 save=\u4FDD\u5B58\u6D4B\u8BD5\u8BA1\u5212
 save?=\u4FDD\u5B58\uFF1F
 save_all_as=\u4FDD\u5B58\u6D4B\u8BD5\u8BA1\u5212\u4E3A
 save_as=\u9009\u4E2D\u90E8\u5206\u4FDD\u5B58\u4E3A...
 scheduler=\u8C03\u5EA6\u5668
 scheduler_configuration=\u8C03\u5EA6\u5668\u914D\u7F6E
 search_filter=\u641C\u7D22\u8FC7\u6EE4\u5668
 search_test=\u641C\u7D22\u6D4B\u8BD5
 secure=\u5B89\u5168
 send_file=\u540C\u8BF7\u6C42\u4E00\u8D77\u53D1\u9001\u6587\u4EF6\uFF1A
 send_file_browse=\u6D4F\u89C8...
 send_file_filename_label=\u6587\u4EF6\u540D\u79F0\uFF1A
 send_file_mime_label=MIME\u7C7B\u578B\uFF1A
 send_file_param_name_label=\u53C2\u6570\u540D\u79F0\uFF1A
 server=\u670D\u52A1\u5668\u540D\u79F0\u6216IP\uFF1A
 servername=\u670D\u52A1\u5668\u540D\u79F0\uFF1A
 session_argument_name=\u4F1A\u8BDD\u53C2\u6570\u540D\u79F0\uFF1A
 shutdown=\u5173\u95ED
 simple_config_element=\u7B80\u5355\u914D\u7F6E\u5143\u4EF6
 size_assertion_comparator_label=\u6BD4\u8F83\u7C7B\u578B
 size_assertion_input_error=\u8BF7\u8F93\u5165\u4E00\u4E2A\u6709\u6548\u7684\u6B63\u6574\u6570\u3002
 size_assertion_label=\u5B57\u8282\u5927\u5C0F\uFF1A
 soap_action=Soap\u52A8\u4F5C
 ssl_alias_prompt=\u8BF7\u8F93\u5165\u9996\u9009\u7684\u522B\u540D
 ssl_alias_select=\u4E3A\u6D4B\u8BD5\u9009\u62E9\u4F60\u7684\u522B\u540D
 ssl_alias_title=\u5BA2\u6237\u7AEF\u522B\u540D
 ssl_pass_prompt=\u8BF7\u8F93\u5165\u4F60\u7684\u5BC6\u7801
 ssl_port=SSL\u7AEF\u53E3
 sslmanager=SSL\u7BA1\u7406\u5668
 start=\u542F\u52A8
 starttime=\u542F\u52A8\u65F6\u95F4
 stop=\u505C\u6B62
 stopping_test=\u505C\u6B62\u5168\u90E8\u6D4B\u8BD5\u7EBF\u7A0B\u3002\u8BF7\u8010\u5FC3\u7B49\u5F85\u3002
 stopping_test_title=\u6B63\u5728\u505C\u6B62\u6D4B\u8BD5
 string_from_file_file_name=\u8F93\u5165\u6587\u4EF6\u7684\u5168\u8DEF\u5F84
 summariser_title=\u751F\u6210\u6982\u8981\u7ED3\u679C
 tcp_config_title=TCP\u53D6\u6837\u5668\u914D\u7F6E
 tcp_nodelay=\u8BBE\u7F6E\u65E0\u5EF6\u8FDF
 tcp_port=\u7AEF\u53E3\u53F7\uFF1A
 tcp_request_data=\u8981\u53D1\u9001\u7684\u6587\u672C
 tcp_sample_title=TCP\u53D6\u6837\u5668
 tcp_timeout=\u8D85\u65F6\uFF1A
 template_field=\u6A21\u677F\uFF1A
 test=\u6D4B\u8BD5
 test_configuration=\u6D4B\u8BD5\u914D\u7F6E
 test_plan=\u6D4B\u8BD5\u8BA1\u5212
 testplan.serialized=\u72EC\u7ACB\u8FD0\u884C\u6BCF\u4E2A\u7EBF\u7A0B\u7EC4\uFF08\u4F8B\u5982\u5728\u4E00\u4E2A\u7EC4\u8FD0\u884C\u7ED3\u675F\u540E\u542F\u52A8\u4E0B\u4E00\u4E2A\uFF09
 testplan_comments=\u6CE8\u91CA\uFF1A
 thread_delay_properties=\u7EBF\u7A0B\u5EF6\u8FDF\u5C5E\u6027
 thread_group_title=\u7EBF\u7A0B\u7EC4
 thread_properties=\u7EBF\u7A0B\u5C5E\u6027
 threadgroup=\u7EBF\u7A0B\u7EC4
 throughput_control_title=\u541E\u5410\u91CF\u63A7\u5236\u5668
 throughput_control_tplabel=\u541E\u5410\u91CF
 transaction_controller_title=\u4E8B\u52A1\u63A7\u5236\u5668
 update_per_iter=\u6BCF\u6B21\u8DCC\u4EE3\u66F4\u65B0\u4E00\u6B21
 upload=\u6587\u4EF6\u4E0A\u8F7D
 upper_bound=\u4E0A\u9650
 url_config_protocol=\u534F\u8BAE\uFF1A
 url_config_title=HTTP\u8BF7\u6C42\u9ED8\u8BA4\u503C
 use_recording_controller=\u4F7F\u7528\u5F55\u5236\u63A7\u5236\u5668
 user=\u7528\u6237
 user_defined_test=\u7528\u6237\u5B9A\u4E49\u7684\u6D4B\u8BD5
 user_defined_variables=\u7528\u6237\u5B9A\u4E49\u7684\u53D8\u91CF
 user_parameters_table=\u53C2\u6570
 user_parameters_title=\u7528\u6237\u53C2\u6570
 username=\u7528\u6237\u540D
 value=\u503C
 var_name=\u5F15\u7528\u540D\u79F0
 view_graph_tree_title=\u5BDF\u770B\u7ED3\u679C\u6811
 view_results_in_table=\u7528\u8868\u683C\u5BDF\u770B\u7ED3\u679C
 view_results_tab_request=\u8BF7\u6C42
 view_results_tab_response=\u54CD\u5E94\u6570\u636E
 view_results_tab_sampler=\u53D6\u6837\u5668\u7ED3\u679C
 view_results_title=\u5BDF\u770B\u7ED3\u679C
 view_results_tree_title=\u5BDF\u770B\u7ED3\u679C\u6811
 web_request=HTTP\u8BF7\u6C42
 web_server=Web\u670D\u52A1\u5668
 web_server_domain=\u670D\u52A1\u5668\u540D\u79F0\u6216IP\uFF1A
 web_server_port=\u7AEF\u53E3\u53F7\uFF1A
 web_testing_retrieve_images=\u4ECEHTML\u6587\u4EF6\u83B7\u53D6\u6240\u6709\u5185\u542B\u7684\u8D44\u6E90
 web_testing_title=HTTP\u8BF7\u6C42
 workbench_title=\u5DE5\u4F5C\u53F0
 xml_assertion_title=XML\u65AD\u8A00
 you_must_enter_a_valid_number=\u5FC5\u987B\u8F93\u5165\u6709\u6548\u7684\u6570\u5B57
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/resources/messages_zh_TW.properties b/src/core/org/apache/jmeter/resources/messages_zh_TW.properties
index cbe83250f..e401e5709 100644
--- a/src/core/org/apache/jmeter/resources/messages_zh_TW.properties
+++ b/src/core/org/apache/jmeter/resources/messages_zh_TW.properties
@@ -1,607 +1,606 @@
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
 about=\u95DC\u65BC Apache JMeter
 add=\u65B0\u589E
 add_as_child=\u65B0\u589E\u70BA\u5B50\u5143\u4EF6
 add_parameter=\u65B0\u589E\u53C3\u6578
 add_pattern=\u65B0\u589E\u6A23\u5F0F\:
 add_test=\u65B0\u589E\u6E2C\u8A66
 add_user=\u65B0\u589E\u4F7F\u7528\u8005
 add_value=\u65B0\u589E\u503C
 addtest=\u65B0\u589E\u6E2C\u8A66
 aggregate_report=\u5F59\u6574\u5831\u544A
 aggregate_report_bandwidth=\u6BCF\u79D2\u4EDF\u4F4D\u5143\u7D44
 aggregate_report_count=\u53D6\u6A23\u6578
 aggregate_report_error%=\u932F\u8AA4\u7387
 aggregate_report_max=\u6700\u5927\u503C
 aggregate_report_median=\u4E2D\u9593\u503C
 aggregate_report_min=\u6700\u5C0F\u503C
 aggregate_report_rate=\u8655\u7406\u91CF
 aggregate_report_total_label=\u7E3D\u8A08
 als_message=\u8A3B\uFF1AAccess Log Parser \u8A2D\u8A08\u6210\u901A\u7528\uFF0C\u4F60\u53EF\u4EE5\u81EA\u884C\u52A0\u5F37\u529F\u80FD
 als_message2=\u8981\u5BE6\u4F5C LogParser\uFF0C\u4E26\u5C07 jar \u653E\u5728
 als_message3=/lib \u76EE\u9304\uFF0C\u7136\u5F8C\u5728\u53D6\u6A23\u4E2D\u8F38\u5165 class \u540D\u7A31
 analyze=\u5206\u6790\u8CC7\u6599\u6A94\u6848\u4E2D...
 anchor_modifier_title=HTML \u93C8\u7D50\u5256\u6790\u5668
 argument_must_not_be_negative=\u53C3\u6578\u4E0D\u53EF\u4EE5\u70BA\u8CA0\u503C\uFF01
 assertion_assume_success=\u5FFD\u7565\u72C0\u614B
 assertion_code_resp=\u56DE\u8986\u4EE3\u78BC
 assertion_contains=\u5305\u542B
 assertion_matches=\u76F8\u7B26
 assertion_message_resp=\u56DE\u8986\u8A0A\u606F
 assertion_not=\u975E
 assertion_pattern_match_rules=\u6A23\u5F0F\u6BD4\u5C0D\u898F\u5247
 assertion_patterns_to_test=\u6E2C\u8A66\u7528\u6A23\u5F0F
 assertion_resp_field=\u9808\u6AA2\u67E5\u7684\u56DE\u8986\u6B04\u4F4D
 assertion_text_resp=\u56DE\u8986\u6587\u5B57
 assertion_textarea_label=\u9A57\u8B49\uFF1A
 assertion_title=\u9A57\u8B49\u56DE\u8986
 assertion_url_samp=\u53D6\u6A23\u7684 URL
 assertion_visualizer_title=\u9A57\u8B49\u7D50\u679C
 attribute=\u5C6C\u6027
 attrs=\u5C6C\u6027
 auth_manager_title=HTTP \u6388\u6B0A\u7BA1\u7406\u54E1
 auths_stored=\u6388\u6B0A\u7BA1\u7406\u54E1\u4E2D\u8A18\u8F09\u7684\u6388\u6B0A\u8CC7\u6599
 average=\u5E73\u5747\u503C
 bind=\u57F7\u884C\u7DD2\u9023\u7D50
 browse=\u700F\u89BD...
 bsf_sampler_title=BSF \u53D6\u6A23
 bsf_script=\u8173\u672C
 bsf_script_file=\u8173\u672C\u6A94
 bsf_script_language=\u8173\u672C\u8A9E\u8A00\uFF1A
 bsf_script_parameters=\u50B3\u7D66\u8173\u672C(\u6A94\u6848)\u7684\u53C3\u6578\uFF1A
 bsh_assertion_script=\u8173\u672C
 bsh_assertion_script_variables=\u56DE\u8986[\u8CC7\u6599|\u4EE3\u78BC|\u8A0A\u606F|\u8868\u982D], \u8981\u6C42\u8868\u982D, \u53D6\u6A23\u6A19\u984C, \u53D6\u6A23\u8CC7\u6599
 bsh_assertion_title=BeanShell \u9A57\u8B49
 bsh_function_expression=\u88AB\u9A57\u8B49\u7684\u8868\u793A\u5F0F
 bsh_sampler_title=BeanShell \u53D6\u6A23
 bsh_script=\u8173\u672C(\u8B8A\u6578\uFF1A\u53D6\u6A23\u7D50\u679C,\u56DE\u8986\u4EE3\u78BC,\u56DE\u8986\u8A0A\u606F,\u662F\u5426\u6210\u529F,\u6A19\u984C,\u6A94\u540D)
 bsh_script_file=\u8173\u672C\u6A94\u6848
 bsh_script_parameters=\u53C3\u6578(->\u5B57\u4E32\u53C3\u6578\u548C String []bsh.args)
 busy_testing=\u6211\u6B63\u5FD9\u8457\u6E2C\u5462, \u8981\u6539\u8A2D\u5B9A\u503C\u8ACB\u5148\u505C\u6B62\u6E2C\u8A66
 cancel=\u53D6\u6D88
 cancel_exit_to_save=\u5C1A\u672A\u5132\u5B58, \u5148\u5132\u5B58\u518D\u96E2\u958B\u597D\u55CE\uFF1F
 cancel_new_to_save=\u5C1A\u672A\u5132\u5B58, \u5148\u5132\u5B58\u518D\u6E05\u9664\u597D\u55CE\uFF1F
 choose_function=\u9078\u64C7\u4E00\u500B\u529F\u80FD
 choose_language=\u9078\u64C7\u4E00\u7A2E\u8A9E\u8A00
 clear=\u6E05\u9664
 clear_all=\u5168\u90E8\u6E05\u9664
 clear_cookies_per_iter=\u6BCF\u56DE\u5408\u90FD\u5148\u6E05\u9664 Cookies\uFF1F
 column_delete_disallowed=\u6B64\u6B04\u4F4D\u4E0D\u5141\u8A31\u522A\u9664
 compare=\u6BD4\u8F03
 comparefilt=\u6BD4\u8F03\u904E\u6FFE\u5668
 config_element=\u8A2D\u5B9A
 config_save_settings=\u8A2D\u5B9A
 constant_throughput_timer_memo=\u70BA\u4F7F\u8655\u7406\u91CF\u70BA\u56FA\u5B9A\u503C, \u5728\u53D6\u6A23\u9593\u52A0\u5165\u5EF6\u9072\u6642\u9593
 constant_timer_delay=\u5EF6\u9072\u6642\u9593(\u55AE\u4F4D\u662F\u5343\u5206\u4E4B\u4E00\u79D2)
 constant_timer_memo=\u5728\u53D6\u6A23\u9593\u52A0\u5165\u56FA\u5B9A\u7684\u5EF6\u9072\u6642\u9593
 constant_timer_title=\u56FA\u5B9A\u503C\u8A08\u6642\u5668
 controller=\u63A7\u5236\u5668
 cookie_manager_title=HTTP Cookie \u7BA1\u7406\u54E1
 cookies_stored=Cookie \u7BA1\u7406\u54E1\u4E2D\u8A18\u9304\u7684 Cookies
 copy=\u8907\u88FD
 counter_config_title=\u8A08\u6578\u5668
 counter_per_user=\u6BCF\u500B\u4F7F\u7528\u8005\u7684\u8A08\u6578\u5668
 countlim=\u5927\u5C0F\u9650\u5236
 cut=\u526A\u4E0B
 cut_paste_function=\u8907\u88FD\u548C\u8CBC\u4E0A\u5B57\u4E32
 database_conn_pool_max_usage=\u6BCF\u500B\u9023\u7DDA\u6700\u5927\u4F7F\u7528\u91CF\uFF1A
 database_conn_pool_props=\u8CC7\u6599\u5EAB\u9023\u7DDA\u6C60
 database_conn_pool_size=\u8CC7\u6599\u5EAB\u9023\u7DDA\u6C60\u4E2D\u7684\u9023\u7DDA\u6578\uFF1A
 database_conn_pool_title=JDBC \u8CC7\u6599\u5EAB\u9023\u7DDA\u6C60\u9810\u8A2D\u503C
 database_driver_class=\u9A45\u52D5\u7A0B\u5F0F Class \uFF1A
 database_login_title=JDBC \u8CC7\u6599\u5EAB\u767B\u5165\u9810\u8A2D\u503C
 database_sql_query_string=SQL \u654D\u8FF0\uFF1A
 database_sql_query_title=JDBC SQL \u654D\u8FF0\u9810\u8A2D\u503C
 database_testing_title=JDBC \u8981\u6C42
 database_url=JDBC URL\uFF1A
 database_url_jdbc_props=\u8CC7\u6599\u5EAB URL \u548C JDBC \u9A45\u52D5\u7A0B\u5F0F
 de=\u5FB7\u570B
 default_parameters=\u9810\u8A2D\u53C3\u6578
 default_value_field=\u9810\u8A2D\u503C\uFF1A
 delay=\u555F\u52D5\u5EF6\u9072\u6642\u9593(\u79D2)
 delete=\u522A\u9664
 delete_parameter=\u522A\u9664\u503C
 delete_test=\u522A\u9664\u6E2C\u8A66
 delete_user=\u522A\u9664\u4F7F\u7528\u8005
 deltest=\u522A\u9664\u6E2C\u8A66
 deref=\u4E0D\u5F15\u7528\u5225\u540D
 disable=\u4E0D\u81F4\u80FD
 distribution_graph_title=\u5206\u6563\u5716\u5F62(alpha)
 distribution_note1=\u672C\u5716\u65BC\u6BCF10\u500B\u53D6\u6A23\u9032\u884C\u66F4\u65B0
 domain=\u7DB2\u57DF
 done=\u5B8C\u6210
 duration=\u671F\u9593
 duration_assertion_duration_test=\u88AB\u9A57\u8B49\u7684\u671F\u9593
 duration_assertion_failure=\u6B64\u52D5\u4F5C\u592A\u4E45\u4E86,\u61C9\u8A72\u5728{1}\u5FAE\u79D2\u4E4B\u5167\u5B8C\u6210,\u537B\u82B1\u4E86{0}\u5FAE\u79D2
 duration_assertion_input_error=\u8ACB\u8F38\u5165\u5408\u6CD5\u6B63\u6574\u6578\u503C
 duration_assertion_label=\u671F\u9593(\u5FAE\u79D2)
 duration_assertion_title=\u9A57\u8B49\u671F\u9593
 edit=\u7DE8\u8F2F
 email_results_title=\u96FB\u90F5\u7D50\u679C
 en=\u82F1\u6587
 enable=\u555F\u52D5
 encode?=\u7DE8\u78BC\uFF1F
 encoded_value=URL \u7DE8\u78BC\u503C
 endtime=\u7D50\u675F\u6642\u9593
 entry_dn=\u9032\u5165 DN
 entrydn=\u9032\u5165 DN
 error_loading_help=\u8F09\u5165\u8F14\u52A9\u8AAA\u660E\u5931\u6557
 error_occurred=\u767C\u751F\u932F\u8AA4
 example_data=\u8CC7\u6599\u7BC4\u4F8B
 example_title=\u53D6\u6A23\u7BC4\u4F8B
 exit=\u96E2\u958B
 expiration=\u5230\u671F
 field_name=\u6B04\u4F4D\u540D\u7A31
 file=\u6A94\u6848
 file_already_in_use=\u6A94\u6848\u5DF2\u5728\u4F7F\u7528\u4E2D
 file_visualizer_append=\u52A0\u5165\u65E2\u6709\u7684\u6A94\u6848
 file_visualizer_auto_flush=\u53D6\u5F97\u6BCF\u8CC7\u6599\u5F8C\u81EA\u52D5 Flush
 file_visualizer_browse=\u700F\u89BD
 file_visualizer_close=\u95DC\u9589
 file_visualizer_file_options=\u6A94\u6848\u9078\u9805
 file_visualizer_filename=\u6A94\u540D
 file_visualizer_missing_filename=\u6A94\u540D\u672A\u6307\u5B9A
 file_visualizer_open=\u958B\u555F
 file_visualizer_output_file=\u5C07\u5168\u90E8\u8CC7\u6599\u5BEB\u6210\u6A94\u6848
 file_visualizer_submit_data=\u5305\u542B\u5DF2\u50B3\u9001\u8CC7\u6599
 file_visualizer_title=\u6A94\u6848\u5831\u544A\u54E1
 file_visualizer_verbose=\u700F\u89BD\u8F38\u51FA
 filename=\u6A94\u540D
 follow_redirects=\u8DDF\u96A8\u91CD\u5C0E
 follow_redirects_auto=\u81EA\u52D5\u8DDF\u96A8\u91CD\u5C0E
 foreach_controller_title=ForEach \u63A7\u5236\u5668
 foreach_input=\u8B8A\u6578\u524D\u7F6E\u5B57\u4E32
 foreach_output=\u8F38\u51FA\u8B8A\u6578\u540D\u7A31
 foreach_use_separator=\u5728\u7DE8\u865F\u524D\u52A0\u4E0A\u5E95\u7DDA\u7B26\u865F\uFF1F
 fr=\u6CD5\u6587
 ftp_sample_title=FTP \u8981\u6C42\u9810\u8A2D\u503C
 ftp_testing_title=FTP \u8981\u6C42
 function_dialog_menu_item=\u529F\u80FD\u8F14\u52A9\u8AAA\u660E\u5C0D\u8A71
 function_helper_title=\u529F\u80FD\u8F14\u52A9\u8AAA\u660E
 function_name_param=\u529F\u80FD\u540D\u7A31. \u6703\u51FA\u73FE\u5728\u6574\u4EFD\u6E2C\u8A66\u8A08\u756B\u4E2D
 function_params=\u529F\u80FD\u53C3\u6578
 functional_mode=\u529F\u80FD\u6027\u6E2C\u8A66\u6A21\u5F0F
 functional_mode_explanation=\u53EA\u6709\u5728\u9700\u8981\u5C07\u6240\u6709\u56DE\u8986\u90FD\u5B58\u6210\u6A94\u6848\u6642\u624D\u9078\u7528\u9019\u7A2E\u6A21\u5F0F\n\n\u9078\u7528\u9019\u7A2E\u6A21\u5F0F\u5C0D\u6548\u80FD\u6703\u6709\u986F\u8457\u5F71\u97FF
 gaussian_timer_delay=\u5B9A\u503C\u5EF6\u9072\u5DEE(\u5FAE\u79D2)
 gaussian_timer_memo=\u4EE5\u9AD8\u65AF\u5206\u914D\u6CD5\u52A0\u5165\u96A8\u6A5F\u5EF6\u9072\u6642\u9593
 gaussian_timer_range=\u96E2\u5DEE(\u5FAE\u79D2)
 gaussian_timer_title=\u9AD8\u65AF\u96A8\u6A5F\u8A08\u6642\u5668
 generate=\u7522\u751F
 generator=\u9AD8\u65AF\u6CD5 class \u540D\u7A31
 generator_cnf_msg=\u627E\u4E0D\u5230\u7522\u751F\u5668 class. \u8ACB\u78BA\u8A8D jar \u6A94\u653E\u5728 /lib \u76EE\u9304
 generator_illegal_msg=\u7121\u6CD5\u5B58\u53D6\u7522\u751F\u5668 class (iIllegalAcessException)
 generator_instantiate_msg=\u7121\u6CD5\u4F7F\u7528\u7522\u751F\u5668, \u8ACB\u78BA\u8A8D\u5DF2\u5BE6\u4F5C Generator \u4ECB\u9762
 graph_choose_graphs=\u8981\u986F\u793A\u7684\u5716\u5F62
 graph_full_results_title=\u5B8C\u6574\u7D50\u679C\u5716\u5F62
 graph_results_average=\u5E73\u5747
 graph_results_data=\u8CC7\u6599
 graph_results_deviation=\u8B8A\u7570\u5DEE
 graph_results_latest_sample=\u6700\u8FD1\u7684\u53D6\u6A23
 graph_results_median=\u4E2D\u9593\u503C
 graph_results_ms=\u5FAE\u79D2
 graph_results_no_samples=\u53D6\u6A23\u7684\u7DE8\u865F
 graph_results_throughput=\u8655\u7406\u91CF
 graph_results_title=\u7D50\u679C\u5716\u5F62
 grouping_add_separators=\u7FA4\u7D44\u9593\u7684\u5206\u9694\u7DDA
 grouping_in_controllers=\u6BCF\u500B\u7FA4\u7D44\u5206\u653E\u81F3\u4E0D\u540C\u63A7\u5236\u5668
 grouping_mode=\u7FA4\u7D44\uFF1A
 grouping_no_groups=\u4E0D\u8981\u5C07\u53D6\u6A23\u5206\u7FA4\u7D44
 grouping_store_first_only=\u50C5\u5132\u5B58\u6BCF\u500B\u7FA4\u7D44\u7684\u7B2C\u4E00\u500B\u53D6\u6A23
 header_manager_title=HTTP \u6A19\u982D\u7BA1\u7406\u54E1
 headers_stored=\u6A19\u982D\u7BA1\u7406\u54E1\u4E2D\u5132\u5B58\u7684\u6A19\u982D\u8CC7\u6599
 help=\u8F14\u52A9\u8AAA\u660E
 html_assertion_label=HTML \u9A57\u8B49
 html_assertion_title=HTML \u9A57\u8B49
 http_response_code=HTTP \u56DE\u61C9\u4EE3\u78BC
 http_url_rewriting_modifier_title=HTTP URL \u91CD\u5C0E\u4FEE\u98FE\u8A5E
 http_user_parameter_modifier=HTTP \u4F7F\u7528\u8005\u53C3\u6578\u4FEE\u98FE\u8A5E
 id_prefix=ID \u524D\u7F6E\u5B57\u4E32
 id_suffix=ID \u5F8C\u7F6E\u5B57\u4E32
 if_controller_label=\u689D\u4EF6
 if_controller_title=\u82E5...\u63A7\u5236\u5668
 ignore_subcontrollers=\u5FFD\u7565\u5B50\u63A7\u5236\u5668\u5167\u5BB9
 include_equals=\u5305\u542B\u76F8\u7B49\uFF1F
 increment=\u589E\u91CF
 infinite=\u6C38\u4E45
 insert_after=\u52A0\u5728\u4E4B\u5F8C
 insert_before=\u52A0\u5728\u4E4B\u524D
 insert_parent=\u52A0\u5230\u4E0A\u4E00\u968E\u5C64
 interleave_control_title=\u4EA4\u932F\u63A7\u5236\u5668
 intsum_param_1=\u7B2C\u4E00\u500B\u6574\u6578\u53C3\u6578
 intsum_param_2=\u7B2C\u4E8C\u500B\u6574\u6578\u53C3\u6578\u2500\u5176\u4ED6\u7684\u6574\u6578\u53EF\u4EE5\u7531\u65B0\u589E\u7684\u53C3\u6578\u52A0\u7E3D
 invalid_data=\u7121\u6548\u8CC7\u6599
 invalid_mail=\u50B3\u9001\u96FB\u90F5\u6642\u767C\u751F\u932F\u8AA4
 invalid_mail_address=\u5075\u6E2C\u5230\u4E00\u500B\u4EE5\u4E0A\u7684\u7121\u6548\u96FB\u90F5\u5730\u5740
 invalid_mail_server=\u7121\u6CD5\u9023\u4E0A\u96FB\u90F5\u4F3A\u670D\u5668(\u8A73\u898BJMeter\u6B77\u7A0B\u6A94)
 iteration_counter_arg_1=\u6BCF\u500B\u4F7F\u7528\u8005\u4F7F\u7528\u4E0D\u540C\u8A08\u6578\u5668(TRUE)\u6216\u5171\u7528\u4E00\u500B\u5168\u57DF\u8A08\u6578\u5668(FALSE)
 iterator_num=\u8FF4\u5708\u6B21\u6578\uFF1A
 java_request=Java \u8981\u6C42
 java_request_defaults=Java \u8981\u6C42\u9810\u8A2D\u503C
 jms_auth_required=\u5FC5\u8981
 jms_client_caption=\u63A5\u6536\u7AEF\u900F\u904ETopicSubscriber.receive()\u63A5\u807D\u8A0A\u606F
 jms_client_caption2=MessageListener\u900F\u904EonMessage(Message\u4ECB\u9762\u63A5\u807D\u8A0A\u606F
 jms_client_type=\u7528\u6236\u7AEF
 jms_communication_style=\u6E9D\u901A\u6A21\u5F0F
 jms_concrete_connection_factory=\u5805\u56FA\u9023\u7DDA\u5DE5\u5EE0
 jms_config=\u8A2D\u7F6E
 jms_config_title=JMS \u8A2D\u7F6E
 jms_connection_factory=\u9023\u7DDA\u5DE5\u5EE0
 jms_file=\u6A94\u6848
 jms_initial_context_factory=JNDI \u521D\u59CB\u672C\u6587\u5DE5\u5EE0
 jms_itertions=\u8981\u7D2F\u8A08\u7684\u53D6\u6A23\u6578
 jms_jndi_defaults_title=JNDI \u9810\u8A2D\u914D\u7F6E
 jms_jndi_props=JNDI \u5C6C\u6027
 jms_message_title=\u8A0A\u606F
 jms_message_type=\u8A0A\u606F\u7A2E\u985E
 jms_msg_content=\u8A0A\u606F\u5167\u5BB9
 jms_object_message=\u7269\u4EF6\u8A0A\u606F
 jms_props=JMS \u5C6C\u6027
 jms_provider_url=\u63D0\u4F9B\u8005 URL
 jms_publisher=JMS \u767C\u4F48\u8005
 jms_pwd=\u5BC6\u78BC
 jms_queue=\u4F47\u5217
 jms_queue_connection_factory=\u4F47\u5217\u9023\u7DDA\u5DE5\u5EE0
 jms_queueing=JMS \u8CC7\u6E90
 jms_random_file=\u96A8\u6A5F\u6A94\u6848
 jms_receive_queue=\u63A5\u6536\u4F47\u5217
 jms_request=\u55AE\u5411\u8981\u6C42
 jms_requestreply=\u8981\u6C42\u4E14\u56DE\u8986
 jms_sample_title=JMS \u9810\u8A2D\u8981\u6C42
 jms_send_queue=\u50B3\u9001\u4F47\u5217
 jms_subscriber_on_message=\u4F7F\u7528 MessageListener.onMessage()
 jms_subscriber_receive=\u4F7F\u7528 TopicSubscriber.receive()
 jms_subscriber_title=JMS \u8A02\u95B1\u8005
 jms_testing_title=\u8981\u6C42\u8A0A\u606F
 jms_text_message=\u6587\u5B57\u8A0A\u606F
 jms_timeout=\u903E\u6642
 jms_topic=\u984C\u76EE
 jms_use_file=\u5F9E\u6A94\u6848
 jms_use_properties_file=\u4F7F\u7528 jndi.properties \u6A94
 jms_use_random_file=\u96A8\u6A5F\u6A94\u6848
 jms_use_text=\u6587\u5B57\u5340\u57DF
 jms_user=\u4F7F\u7528\u8005
 jndi_config_title=JNDI \u914D\u7F6E
 jndi_lookup_name=\u9060\u7AEF\u4ECB\u9762
 jndi_lookup_title=JNDI \u5C0B\u67E5\u914D\u7F6E
 jndi_method_button_invoke=\u8D77\u52D5
 jndi_method_button_reflect=\u53CD\u6620
 jndi_method_home_name=\u672C\u7AEF\u65B9\u6CD5
 jndi_method_home_parms=\u672C\u7AEF\u65B9\u6CD5\u53C3\u6578
 jndi_method_name=\u65B9\u6CD5\u914D\u7F6E
 jndi_method_remote_interface_list=\u9060\u7AEF\u4ECB\u9762
 jndi_method_remote_name=\u9060\u7AEF\u65B9\u6CD5\u540D\u7A31
 jndi_method_remote_parms=\u9060\u7AEF\u65B9\u6CD5\u53C3\u6578
 jndi_method_title=\u9060\u7AEF\u65B9\u6CD5\u914D\u7F6E
 jndi_testing_title=JNDI \u8981\u6C42
 jndi_url_jndi_props=JNDI \u5C6C\u6027
 ja=\u65E5\u6587
 ldap_argument_list=LDAP\u53C3\u6578\u5217\u8868
 ldap_sample_title=LDAP \u8981\u6C42\u9810\u8A2D\u503C
 ldap_testing_title=LDAP \u8981\u6C42
 ldapext_sample_title=LDAP \u5EF6\u4F38\u8981\u6C42\u9810\u8A2D\u503C
 ldapext_testing_title=LDAP \u5EF6\u4F38\u8981\u6C42
 load=\u8F09\u5165
 log_errors_only=\u53EA\u8A18\u9304\u932F\u8AA4
 log_file=\u6B77\u7A0B\u6A94\u4F4D\u7F6E
 log_parser=\u6B77\u7A0B\u6A94\u5256\u6790\u7A0B\u5F0F
 log_parser_cnf_msg=\u627E\u4E0D\u5230\u8A72 class, \u8ACB\u78BA\u5B9A jar \u6A94\u653E\u5728 /lib \u76EE\u9304\u4E0B
 log_parser_illegal_msg=\u7121\u6CD5\u5B58\u53D6 class (IllegalAcessException)
 log_parser_instantiate_msg=\u7121\u6CD5\u5EFA\u7ACB log parser. \u8ACB\u5B9A\u6709\u5BE6\u4F5C LogParser \u4ECB\u9762
 log_sampler=Tomcat \u5B58\u53D6\u8A18\u9304\u5256\u6790\u5668
 logic_controller_title=\u7C21\u6613\u63A7\u5236\u5668
 login_config=\u767B\u5165\u914D\u7F6E
 login_config_element=\u767B\u5165\u914D\u7F6E\u5143\u7D20
 loop_controller_title=\u8FF4\u5708\u63A7\u5236\u5668
 looping_control=\u8FF4\u5708\u63A7\u5236
 lower_bound=\u4F4E\u9650
 mail_reader_account=\u4F7F\u7528\u8005
 mail_reader_all_messages=\u5168\u90E8
 mail_reader_delete=\u5F9E\u4F3A\u670D\u5668\u522A\u9664
 mail_reader_folder=\u8CC7\u6599\u593E
 mail_reader_num_messages=\u5F85\u63A5\u6536\u8A0A\u606F\u6578
 mail_reader_password=\u5BC6\u78BC
 mail_reader_server=\u4F3A\u670D\u5668
 mail_reader_server_type=\u4F3A\u670D\u5668\u7A2E\u985E
 mail_reader_title=\u90F5\u4EF6\u8B80\u53D6\u8005\u53D6\u6A23
 mail_sent=\u90F5\u4EF6\u50B3\u9001\u6210\u529F
 mailer_attributes_panel=\u90F5\u5BC4\u5C6C\u6027
 mailer_error=\u7121\u6CD5\u5BC4\u51FA. \u8ACB\u66F4\u6B63\u932F\u8AA4\u503C
 mailer_visualizer_title=\u90F5\u4EF6\u8996\u89BA\u5316
 match_num_field=\u7B26\u5408\u6578\u5B57(0\u8868\u793A\u96A8\u6A5F)
 max=\u6700\u5927\u503C
 maximum_param=\u5141\u8A31\u7BC4\u570D\u4E2D\u7684\u6700\u5927\u503C
 md5hex_assertion_failure=\u9A57\u8B49 MD5 \u932F\u8AA4,\u61C9\u8A72\u662F{1}\u537B\u5F97\u5230{0}
 md5hex_assertion_md5hex_test=\u88AB\u9A57\u8B49\u7684 MD5Hex
 md5hex_assertion_title=MD5Hex \u9A57\u8B49
 menu_assertions=\u9A57\u8B49
-menu_close=\u95DC\u9589
 menu_config_element=\u8A2D\u5B9A\u5143\u7D20
 menu_edit=\u7DE8\u8F2F
 menu_generative_controller=\u53D6\u6A23
 menu_listener=\u63A5\u807D
 menu_logic_controller=\u908F\u8F2F\u63A7\u5236\u5668
 menu_merge=\u5408\u4F75
 menu_modifiers=\u4FEE\u98FE\u5143
 menu_non_test_elements=\u975E\u6E2C\u8A66\u5143\u7D20
 menu_open=\u958B\u555F
 menu_post_processors=\u5F8C\u7F6E\u8655\u7406\u5668
 menu_pre_processors=\u524D\u7F6E\u8655\u7406\u5668
 menu_response_based_modifiers=\u4EE5\u56DE\u8986\u70BA\u57FA\u6E96\u7684\u4FEE\u98FE\u5143
 menu_timer=\u8A08\u6642\u5668
 metadata=\u8CC7\u6599\u5B9A\u7FA9
 method=\u65B9\u6CD5
 mimetype=MIME\u7A2E\u985E
 minimum_param=\u5141\u8A31\u7BC4\u570D\u4E2D\u7684\u6700\u5C0F\u503C
 minute=\u5206\u9418
 modddn=\u820A\u8F38\u5165\u540D\u7A31
 modification_controller_title=\u4FEE\u98FE\u63A7\u5236\u5668
 modification_manager_title=\u4FEE\u98FE\u7BA1\u7406\u54E1
 modify_test=\u4FEE\u98FE\u6E2C\u8A66
 modtest=\u4FEE\u98FE\u6E2C\u8A66
 module_controller_title=\u6A21\u7D44\u63A7\u5236\u5668
 name=\u540D\u7A31
 new=\u65B0
 newdn=\u65B0\u7684\u8B58\u5225\u540D\u7A31
 no=\u632A\u5A01
 number_of_threads=\u57F7\u884C\u7DD2\u6578\u91CF
 once_only_controller_title=\u53EA\u6709\u4E00\u6B21\u63A7\u5236\u5668
 open=\u958B\u555F...
 option=\u9078\u9805
 optional_tasks=\u9078\u64C7\u6027\u5DE5\u4F5C
 paramtable=\u9001\u51FA\u542B\u53C3\u6578\u7684\u8981\u6C42
 password=\u5BC6\u78BC
 paste=\u8CBC\u4E0A
 paste_insert=\u8CBC\u4E0A(\u63D2\u5165)
 path=\u8DEF\u5F91
 path_extension_choice=\u5EF6\u4F38\u8DEF\u5F91(\u4F7F\u7528\u5206\u865F\u505A\u70BA\u5206\u9694\u865F)
 path_extension_dont_use_equals=\u4E0D\u8981\u5728\u5EF6\u4F38\u8DEF\u5F91\u4E2D\u4F7F\u7528\u7B49\u865F(Intershop Enfinity compatibility)
 path_extension_dont_use_questionmark=\u4E0D\u8981\u5728\u5EF6\u4F38\u8DEF\u5F91\u4E2D\u4F7F\u7528\u554F\u865F(Intershop Enfinity compatibility)
 patterns_to_exclude=\u9664\u5916\u7684\u578B\u5F0F
 patterns_to_include=\u8981\u5305\u542B\u7684\u578B\u5F0F
 port=\u7AEF\u53E3
 property_default_param=\u9810\u8A2D\u503C
 property_edit=\u7DE8\u8F2F
 property_editor.value_is_invalid_message=\u7531\u65BC\u4F60\u7684\u8F38\u5165\u503C\u4E0D\u5408\u6CD5.\u81EA\u52D5\u56DE\u5FA9\u5230\u539F\u503C
 property_editor.value_is_invalid_title=\u7121\u6548\u8F38\u5165
 property_name_param=\u5C6C\u6027\u540D\u7A31
 property_undefined=\u672A\u5B9A\u7FA9
 protocol=\u5354\u5B9A
 protocol_java_config_tile=\u8A2D\u5B9A Java \u7BC4\u4F8B
 protocol_java_test_title=Java \u6E2C\u8A66
 proxy_assertions=\u589E\u52A0\u9A57\u8B49
 proxy_cl_error=\u82E5\u8981\u6307\u5B9A\u4EE3\u7406\u4F3A\u670D\u5668,\u9808\u63D0\u4F9B\u4E3B\u6A5F\u540D\u7A31\u548C\u7AEF\u53E3
 proxy_headers=\u622A\u53D6 HTTP \u8868\u982D
 proxy_separators=\u589E\u52A0\u5206\u9694
 proxy_target=\u76EE\u6A19\u63A7\u5236\u5668
 proxy_title=HTTP \u4EE3\u7406\u4F3A\u670D\u5668
 ramp_up=\u555F\u52D5\u5EF6\u9072(\u79D2)
 random_control_title=\u96A8\u6A5F\u63A7\u5236\u5668
 random_order_control_title=\u96A8\u6A5F\u9806\u5E8F\u63A7\u5236\u5668
 record_controller_title=\u9304\u88FD\u63A7\u5236\u5668
 ref_name_field=\u53C3\u7167\u540D\u7A31
 regex_extractor_title=\u6B63\u898F\u8868\u793A\u5F0F\u5256\u6790\u5668
 regex_field=\u6B63\u898F\u8868\u793A\u5F0F
 regex_source=\u6B63\u898F\u8868\u793A\u5F0F\u6B04\u4F4D
 regex_src_body=\u672C\u6587
 regex_src_hdrs=\u8868\u982D
 remote_exit=\u9060\u7AEF\u96E2\u958B
 remote_exit_all=\u9060\u7AEF\u96E2\u958B\u5168\u90E8
 remote_start=\u9060\u7AEF\u555F\u52D5
 remote_start_all=\u9060\u7AEF\u555F\u52D5\u5168\u90E8
 remote_stop=\u9060\u7AEF\u505C\u6B62
 remote_stop_all=\u9060\u7AEF\u505C\u6B62\u5168\u90E8
 remove=\u79FB\u9664
 rename=\u66F4\u540D
 report=\u5831\u544A
 request_data=\u8981\u6C42\u8CC7\u6599
 restart=\u91CD\u65B0\u555F\u52D5
 resultaction_title=\u7D50\u679C\u72C0\u614B\u52D5\u4F5C\u8655\u7406\u5668
 resultsaver_errors=\u53EA\u5132\u5B58\u5931\u6557\u7684\u56DE\u8986
 resultsaver_prefix=\u6A94\u540D\u524D\u7F6E\u5B57\u4E32
 resultsaver_title=\u5C07\u56DE\u8986\u5B58\u5230\u6A94\u6848
 retobj=\u50B3\u56DE\u7269\u4EF6
 root=\u6839
 root_title=\u6839
 run=\u57F7\u884C
 running_test=\u57F7\u884C\u6E2C\u8A66
 runtime_controller_title=\u57F7\u884C\u6642\u671F\u63A7\u5236\u5668
 runtime_seconds=\u57F7\u884C\u6642\u671F(\u79D2)
 sample_result_save_configuration=\u53D6\u6A23\u7D50\u679C\u5132\u5B58\u914D\u7F6E
 sampler_on_error_action=\u53D6\u6A23\u932F\u8AA4\u5F8C\u63A1\u53D6\u7684\u52D5\u4F5C
 sampler_on_error_continue=\u7E7C\u7E8C
 sampler_on_error_stop_test=\u505C\u6B62\u6E2C\u8A66
 sampler_on_error_stop_thread=\u505C\u6B62\u57F7\u884C\u7DD2
 save=\u5132\u5B58\u6E2C\u8A66\u8A08\u756B
 save?=\u5132\u5B58\uFF1F
 save_all_as=\u5C07\u6E2C\u8A66\u8A08\u756B\u5132\u5B58\u6210...
 save_as=\u5132\u5B58\u6210...
 save_as_image=\u5132\u5B58\u6210\u5716\u5F62
 save_assertionresultsfailuremessage=\u5132\u5B58\u9A57\u8B49\u7D50\u679C\u5931\u6557\u8A0A\u606F
 save_assertions=\u5132\u5B58\u9A57\u8B49\u7D50\u679C
 save_asxml=\u5132\u5B58\u6210 XML
 save_code=\u5132\u5B58\u56DE\u8986\u4EE3\u78BC
 save_datatype=\u5132\u5B58\u8CC7\u6599\u578B\u614B
 save_encoding=\u5132\u5B58\u7DE8\u78BC
 save_fieldnames=\u5132\u5B58\u6B04\u4F4D\u540D\u7A31
 save_graphics=\u5132\u5B58\u5716\u5F62
 save_label=\u5132\u5B58\u6A19\u984C
 save_latency=\u5132\u5B58 Latency
 save_message=\u5132\u5B58\u56DE\u8986\u8A0A\u606F
 save_requestheaders=\u5132\u5B58\u8981\u6C42\u8868\u982D
 save_responsedata=\u5132\u5B58\u56DE\u8986\u8CC7\u6599
 save_responseheaders=\u5132\u5B58\u56DE\u8986\u8868\u982D
 save_samplerdata=\u5132\u5B58\u53D6\u6A23\u8CC7\u6599
 save_subresults=\u5132\u5B58\u5B50\u7D50\u679C
 save_success=\u5132\u5B58\u6210\u529F
 save_threadname=\u5132\u5B58\u57F7\u884C\u7DD2\u540D\u7A31
 save_time=\u5132\u5B58\u6642\u9593
 save_timestamp=\u5132\u5B58\u6642\u9593\u6233\u8A18
 sbind=\u55AE\u4E00\u7E6B\u7D50/\u4E0D\u7E6B\u7D50
 scheduler=\u5B9A\u6642\u5668
 scheduler_configuration=\u5B9A\u6642\u5668\u914D\u7F6E
 scope=\u7BC4\u570D
 search_base=\u641C\u5C0B\u57FA\u6E96
 search_filter=\u641C\u5C0B\u904E\u6FFE\u689D\u4EF6
 search_test=\u641C\u5C0B\u6E2C\u8A66
 searchbase=\u641C\u5C0B\u57FA\u6E96
 searchfilter=\u641C\u5C0B\u904E\u6FFE\u689D\u4EF6
 searchtest=\u641C\u5C0B\u6E2C\u8A66
 second=\u79D2
 secure=\u5B89\u5168
 send_file=\u8207\u8981\u6C42\u4E00\u540C\u50B3\u9001\u6A94\u6848
 send_file_browse=\u700F\u89BD...
 send_file_filename_label=\u6A94\u540D
 send_file_mime_label=MIME \u578B\u5F0F
 send_file_param_name_label=\u53C3\u6578\u540D\u7A31
 server=\u4F3A\u670D\u5668\u540D\u7A31\u6216 IP
 servername=\u4F3A\u670D\u5668\u540D\u7A31
 session_argument_name=\u9023\u7DDA\u968E\u6BB5\u53C3\u6578\u540D\u7A31
 should_save=\u57F7\u884C\u6E2C\u8A66\u524D\u8981\u5148\u5C07\u6E2C\u8A66\u8173\u672C\u5B58\u6A94. \u5C24\u5176\u662F\u7576\u4F60\u4F7F\u7528 CSV Data Set \u6216 _StringFromFile \u6642
 shutdown=\u95DC\u9589
 simple_config_element=\u7C21\u6613\u8A2D\u7F6E\u5143\u7D20
 simple_data_writer_title=\u7C21\u6613\u8CC7\u6599\u5BEB\u4F5C\u8005
 size_assertion_comparator_error_equal=\u7B49\u65BC
 size_assertion_comparator_error_greater=\u5927\u65BC
 size_assertion_comparator_error_greaterequal=\u5927\u65BC\u6216\u7B49\u65BC
 size_assertion_comparator_error_less=\u5C0F\u65BC
 size_assertion_comparator_error_lessequal=\u5C0F\u65BC\u6216\u7B49\u65BC
 size_assertion_comparator_error_notequal=\u4E0D\u7B49\u65BC
 size_assertion_comparator_label=\u6BD4\u8F03\u7684\u985E\u5225
 size_assertion_failure=\u5927\u5C0F\u932F\u8AA4, \u61C9\u8A72\u6709 {1}{2}\u4F4D\u5143\u7D44, \u537B\u6709 {0} \u4F4D\u5143\u7D44
 size_assertion_input_error=\u8ACB\u8F38\u5165\u6709\u6548\u6B63\u6574\u6578
 size_assertion_label=\u5927\u5C0F(\u4F4D\u5143\u7D44)
 size_assertion_size_test=\u9A57\u8B49\u5927\u5C0F
 size_assertion_title=\u9A57\u8B49\u5927\u5C0F
 soap_action=Soap \u52D5\u4F5C
 soap_data_title=Soap/XML-RPC \u8CC7\u6599
 soap_sampler_title=SOAP/XML-RPC \u8981\u6C42
 ssl_alias_prompt=\u8ACB\u8F38\u5165\u9810\u9078\u7684 alias
 ssl_alias_select=\u8ACB\u9078\u64C7\u6E2C\u8A66\u8981\u7528\u7684 alias
 ssl_error_title=KeyStore \u554F\u984C
 ssl_pass_prompt=\u8ACB\u8F38\u5165\u5BC6\u78BC
 ssl_pass_title=KeyStore \u5BC6\u78BC
 ssl_port=SSL \u7AEF\u53E3
 sslmanager=SSL \u7BA1\u7406\u54E1
 start=\u958B\u59CB
 starttime=\u958B\u59CB\u6642\u9593
 stop=\u505C\u6B62
 stopping_test=\u6B63\u5728\u505C\u6B62\u6240\u6709\u6E2C\u8A66\u7DD2. \u8ACB\u8010\u5FC3\u7B49\u5F85
 stopping_test_title=\u505C\u6B62\u6E2C\u8A66
 string_from_file_file_name=\u8F38\u5165\u6A94\u6848\u5B8C\u6574\u8DEF\u5F91
 string_from_file_seq_final=\u6A94\u6848\u5E8F\u865F(\u7D50\u675F)
 string_from_file_seq_start=\u6A94\u6848\u5E8F\u865F(\u958B\u59CB)
 summariser_title=\u7522\u751F\u7E3D\u8A08\u7D50\u679C
 switch_controller_label=\u5207\u63DB\u503C
 switch_controller_title=\u5207\u63DB\u63A7\u5236\u5668
 table_visualizer_bytes=\u4F4D\u5143\u7D44
 table_visualizer_sample_num=\u53D6\u6A23\u7DE8\u865F \#
 table_visualizer_sample_time=\u53D6\u6A23\u6642\u9593(\u5FAE\u79D2)
 tcp_config_title=TCP \u53D6\u6A23\u8A2D\u5B9A
 tcp_nodelay=\u8A2D\u70BA\u4E0D\u5EF6\u9072
 tcp_port=\u7AEF\u53E3\u865F\u78BC
 tcp_request_data=\u6B32\u50B3\u9001\u6587\u5B57
 tcp_sample_title=TCP \u53D6\u6A23
 tcp_timeout=\u903E\u6642(\u5FAE\u79D2)
 template_field=\u7BC4\u672C
 test=\u6E2C\u8A66
 testconfiguration=\u6E2C\u8A66\u914D\u7F6E
 test_action_action=\u52D5\u4F5C
 test_action_duration=\u671F\u9593
 test_action_pause=\u66AB\u505C
 test_action_stop=\u505C\u6B62
 test_action_target=\u6A19\u7684
 test_action_target_test=\u6240\u6709\u57F7\u884C\u7DD2
 test_action_target_thread=\u76EE\u524D\u57F7\u884C\u7DD2
 test_action_title=\u6E2C\u8A66\u52D5\u4F5C
 test_configuration=\u6E2C\u8A66\u914D\u7F6E
 test_plan=\u6E2C\u8A66\u8A08\u756B
 testplan.serialized=\u4F9D\u5E8F\u57F7\u884C\u57F7\u884C\u7DD2\u7FA4\u7D44,\u57F7\u884C\u5B8C\u4E00\u500B\u624D\u6703\u57F7\u884C\u4E0B\u4E00\u500B
 testplan_comments=\u5099\u8A3B
 testt=\u6E2C\u8A66
 thread_delay_properties=\u57F7\u884C\u7DD2\u5EF6\u9072\u5C6C\u6027
 thread_group_title=\u57F7\u884C\u7DD2\u7FA4\u7D44
 thread_properties=\u57F7\u884C\u7DD2\u5C6C\u6027
 threadgroup=\u57F7\u884C\u7DD2\u7FA4\u7D44
 throughput_control_bynumber_label=\u7E3D\u57F7\u884C\u6578
 throughput_control_bypercent_label=\u767E\u5206\u6BD4\u57F7\u884C
 throughput_control_perthread_label=\u6BCF\u500B\u4F7F\u7528\u8005
 throughput_control_title=\u8655\u7406\u91CF\u63A7\u5236\u5668
 throughput_control_tplabel=\u8655\u7406\u91CF
 timelim=\u6642\u9593\u9650\u5236
 transaction_controller_title=\u4EA4\u6613\u63A7\u5236\u5668
 unbind=\u672A\u7E6B\u7D50\u57F7\u884C\u7DD2
 uniform_timer_delay=\u5E38\u6578\u5EF6\u9072\u5DEE(\u5FAE\u79D2)
 uniform_timer_memo=\u52A0\u5165\u4E00\u81F4\u5206\u4F48\u7684\u96A8\u6A5F\u5EF6\u9072
 uniform_timer_range=\u96A8\u6A5F\u5EF6\u9072\u6700\u5927\u503C(\u5FAE\u79D2)
 uniform_timer_title=\u4E00\u81F4\u96A8\u6A5F\u8A08\u6642\u5668
 update_per_iter=\u6BCF\u56DE\u5408\u8B8A\u66F4\u4E00\u6B21
 upload=\u6A94\u6848\u4E0A\u50B3
 upper_bound=\u4E0A\u9650
 url_config_protocol=\u5354\u5B9A
 url_config_title=HTTP \u8981\u6C42\u9810\u8A2D\u503C
 url_full_config_title=UrlFull \u7BC4\u4F8B
 url_multipart_config_title=HTTP Multipart \u8981\u6C42\u9810\u8A2D\u503C
 use_recording_controller=\u4F7F\u7528\u9304\u88FD\u63A7\u5236\u5668
 user=\u4F7F\u7528\u8005
 user_defined_test=\u4F7F\u7528\u8005\u81EA\u8A02\u6E2C\u8A66
 user_defined_variables=\u4F7F\u7528\u8005\u81EA\u8A02\u8B8A\u6578
 user_param_mod_help_note=(\u4E0D\u8981\u8B8A\u66F4\u9019\u88E1, \u8981\u6539\u5C31\u6539\u5728 /bin \u76EE\u9304\u4E0B\u540C\u540D\u7684\u6A94\u6848\u5167\u5BB9)
 user_parameters_table=\u53C3\u6578
 user_parameters_title=\u4F7F\u7528\u8005\u53C3\u6578
 userdn=\u4F7F\u7528\u8005\u540D\u7A31
 username=\u4F7F\u7528\u8005\u540D\u7A31
 userpw=\u5BC6\u78BC
 value=\u503C
 var_name=\u53C3\u7167\u540D\u7A31
 view_graph_tree_title=\u6AA2\u8996\u5716\u5F62\u6A39
 view_results_in_table=\u6AA2\u8996\u8868\u683C\u5F0F\u7D50\u679C
 view_results_tab_request=\u8981\u6C42
 view_results_tab_response=\u56DE\u8986\u8CC7\u6599
 view_results_tab_sampler=\u53D6\u6A23\u7D50\u679C
 view_results_title=\u6AA2\u8996\u7D50\u679C
 view_results_tree_title=\u6AA2\u8996\u7D50\u679C\u6A39
 warning=\u8B66\u544A\uFF01
 web_request=HTTP \u8981\u6C42
 web_server=Web \u4F3A\u670D\u5668
 web_server_domain=\u4E3B\u6A5F\u540D\u7A31\u6216 IP
 web_server_port=\u7AEF\u53E3\u865F\u78BC
 web_testing_retrieve_images=\u53D6\u56DE\u6240\u6709\u5D4C\u5165 HTML \u7684\u8CC7\u6E90
 web_testing_title=HTTP \u8981\u6C42
 while_controller_label=\u689D\u4EF6 (blank/LAST \u6216 true)
 while_controller_title=\u7576.. \u63A7\u5236\u5668
 workbench_title=\u5DE5\u4F5C\u53F0
 xml_assertion_title=XML \u9A57\u8B49
 xml_namespace_button=\u4F7F\u7528\u540D\u7A31\u7A7A\u9593
 xml_tolerant_button=Tolerant XML/HTML \u5256\u6790\u5668
 xml_validate_button=\u9A57\u8B49 XML
 xml_whitespace_button=\u5FFD\u7565\u767D\u7A7A\u5B57\u5143
 xpath_assertion_button=\u9A57\u8B49
 xpath_assertion_check=\u6AA2\u67E5 XPath \u654D\u8FF0
 xpath_assertion_error=XPath \u932F\u8AA4
 xpath_assertion_failed=\u7121\u6548 XPath \u654D\u8FF0
 xpath_assertion_negate=\u5982\u679C\u5168\u4E0D\u7B26\u5408\u5247\u70BA True
 xpath_assertion_option=\u5256\u6790 XML \u9078\u9805
 xpath_assertion_test=\u9A57\u8B49 XPath
 xpath_assertion_tidy=\u8A66\u8457\u4E26\u5C07\u8F38\u5165\u8CC7\u6599 tidy up
 xpath_assertion_title=\u9A57\u8B49 XPath
 xpath_assertion_valid=\u5408\u6CD5 XPath \u654D\u8FF0
 xpath_assertion_validation=\u4EE5 DTD \u6AA2\u67E5 XML
 xpath_assertion_whitespace=\u5FFD\u7565\u767D\u7A7A\u5B57\u5143
 xpath_expression=\u8981\u6BD4\u5C0D\u7528\u7684 XPath \u654D\u8FF0
 xpath_file_file_name=\u53D6\u503C\u4F86\u6E90\u7684 XML \u6A94\u540D
 you_must_enter_a_valid_number=\u5FC5\u9808\u8F38\u5165\u4E00\u500B\u5408\u6CD5\u6578\u5B57
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 0a2948d4e..8c8a2a39d 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,314 +1,316 @@
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
 
 
 <!--  =================== 3.2 =================== -->
 
 <h1>Version 3.2</h1>
 <p>
 Summary
 </p>
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Known problems and workarounds">Known problems and workarounds</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_section>IMPORTANT CHANGE</ch_section>
 <p>
 JMeter now requires Java 8. Ensure you use most up to date version.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>PDF Documentations have been migrated to HTML user manual</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>60585</bug>JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors. Based on <pr>240</pr> from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
     <li><bug>60542</bug>View Results Tree : Allow Upper Panel to be collapsed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a <pr>245</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60590</bug>BackendListener : Add Influxdb BackendListenerClient implementation to JMeter. Partly based on <pr>246</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60591</bug>BackendListener : Add a time boxed sampling. Based on a <pr>237</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
     <li><bug>58943</bug>Create a Better Think Time experience. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60602</bug>XPath Extractor : Add Match No. to allow extraction randomly, by index or all matches</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
     <li>Improve translation "save_as" in French. Based on a <pr>252</pr> by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60514</bug>Ability to apply a naming convention on Children of a Transaction Controller. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60593</bug>Switch to G1 GC algorithm</li>
     <li><bug>60595</bug>Add a SplashScreen at the start of JMeter GUI. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
+    <li><bug>55258</bug>Drop "Close" icon from toolbar and add "New" to menu. Partly based on contribution from Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to dnsjava-2.1.8.jar (from 2.1.7)</li>
     <li>Updated to groovy 2.4.8 (from 2.4.7)</li>
     <li>Updated to httpcore 4.4.6 (from 4.4.5)</li>
     <li>Updated to jodd 3.8.1 (from 3.8.1.jar)</li>
     <li>Updated to jsoup-1.10.2 (from 1.10.1)</li>
     <li>Updated to ph-css 5.0.3 (from 4.1.6)</li>
     <li>Updated to ph-commons 8.6.0 (from 6.2.4)</li>
     <li>Updated to slf4j-api 1.7.22 (from 1.7.21)</li>
     <li>Updated to asm 5.2 (from 5.1)</li>
     <li>Converted the old pdf tutorials to xml.</li>
     <li><pr>255</pr>Utilised Java 8 (and 7) features to tidy up code. Contributed by Graham Russell (graham at ham1.co.uk)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
     <li><bug>60575</bug>HTTP GET Requests could have a content-type header without a body.</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
     <li><bug>60607</bug>DNS Cache Manager configuration is ignored</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of TestHTTPMirrorThread#testSleep(). Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
     <li><bug>60621</bug>The "report-template" folder is missing from ApacheJMeter_config-3.1.jar in maven central</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Jerome Loisel (loisel.jerome at gmail.com)</li>
 <li>Liu XP (liu_xp2003 at sina.com)</li>
 <li>Qi Chen (qi.chensh at ele.me)</li>
 <li>(gavin at 16degrees.com.au)</li>
 <li>Thomas Schapitz (ts-nospam12 at online.de)</li>
 <li>Murdecai777 (https://github.com/Murdecai777)</li>
 <li>Logan Mauzaize (logan.mauzaize at gmail.com)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
+<li>Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
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
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
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
 The fix is to use JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later (but be aware that Java 9 is not certified yet for JMeter).
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
