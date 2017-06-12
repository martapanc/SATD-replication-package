diff --git a/src/core/org/apache/jmeter/gui/util/MenuFactory.java b/src/core/org/apache/jmeter/gui/util/MenuFactory.java
index 11cb221c2..a65741bc2 100644
--- a/src/core/org/apache/jmeter/gui/util/MenuFactory.java
+++ b/src/core/org/apache/jmeter/gui/util/MenuFactory.java
@@ -1,680 +1,684 @@
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
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import javax.swing.JMenu;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.KeyStroke;
 import javax.swing.MenuElement;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.TestBeanGUI;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Printable;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public final class MenuFactory {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /*
      *  Predefined strings for makeMenu().
      *  These are used as menu categories in the menuMap Hashmap,
      *  and also for resource lookup in messages.properties
     */
     public static final String THREADS = "menu_threads"; //$NON-NLS-1$
     
     public static final String FRAGMENTS = "menu_fragments"; //$NON-NLS-1$
 
     public static final String TIMERS = "menu_timer"; //$NON-NLS-1$
 
     public static final String CONTROLLERS = "menu_logic_controller"; //$NON-NLS-1$
 
     public static final String SAMPLERS = "menu_generative_controller"; //$NON-NLS-1$
 
     public static final String CONFIG_ELEMENTS = "menu_config_element"; //$NON-NLS-1$
 
     public static final String POST_PROCESSORS = "menu_post_processors"; //$NON-NLS-1$
 
     public static final String PRE_PROCESSORS = "menu_pre_processors"; //$NON-NLS-1$
 
     public static final String ASSERTIONS = "menu_assertions"; //$NON-NLS-1$
 
     public static final String NON_TEST_ELEMENTS = "menu_non_test_elements"; //$NON-NLS-1$
 
     public static final String LISTENERS = "menu_listener"; //$NON-NLS-1$
 
     private static final Map<String, List<MenuInfo>> menuMap =
         new HashMap<String, List<MenuInfo>>();
 
     private static final Set<String> elementsToSkip = new HashSet<String>();
 
     // MENU_ADD_xxx - controls which items are in the ADD menu
     // MENU_PARENT_xxx - controls which items are in the Insert Parent menu
     private static final String[] MENU_ADD_CONTROLLER = new String[] {
         MenuFactory.CONTROLLERS,
         MenuFactory.CONFIG_ELEMENTS,
         MenuFactory.TIMERS,
         MenuFactory.PRE_PROCESSORS,
         MenuFactory.SAMPLERS,
         MenuFactory.POST_PROCESSORS,
         MenuFactory.ASSERTIONS,
         MenuFactory.LISTENERS,
         };
 
     private static final String[] MENU_PARENT_CONTROLLER = new String[] {
         MenuFactory.CONTROLLERS };
 
     private static final String[] MENU_ADD_SAMPLER = new String[] {
         MenuFactory.CONFIG_ELEMENTS,
         MenuFactory.TIMERS,
         MenuFactory.PRE_PROCESSORS,
         MenuFactory.POST_PROCESSORS,
         MenuFactory.ASSERTIONS,
         MenuFactory.LISTENERS,
         };
 
     private static final String[] MENU_PARENT_SAMPLER = new String[] {
         MenuFactory.CONTROLLERS };
 
     private static final List<MenuInfo> timers, controllers, samplers, threads, 
         fragments,configElements, assertions, listeners, nonTestElements,
         postProcessors, preProcessors;
 
     static {
         threads = new LinkedList<MenuInfo>();
         fragments = new LinkedList<MenuInfo>();
         timers = new LinkedList<MenuInfo>();
         controllers = new LinkedList<MenuInfo>();
         samplers = new LinkedList<MenuInfo>();
         configElements = new LinkedList<MenuInfo>();
         assertions = new LinkedList<MenuInfo>();
         listeners = new LinkedList<MenuInfo>();
         postProcessors = new LinkedList<MenuInfo>();
         preProcessors = new LinkedList<MenuInfo>();
         nonTestElements = new LinkedList<MenuInfo>();
         menuMap.put(THREADS, threads);
         menuMap.put(FRAGMENTS, fragments);
         menuMap.put(TIMERS, timers);
         menuMap.put(ASSERTIONS, assertions);
         menuMap.put(CONFIG_ELEMENTS, configElements);
         menuMap.put(CONTROLLERS, controllers);
         menuMap.put(LISTENERS, listeners);
         menuMap.put(NON_TEST_ELEMENTS, nonTestElements);
         menuMap.put(SAMPLERS, samplers);
         menuMap.put(POST_PROCESSORS, postProcessors);
         menuMap.put(PRE_PROCESSORS, preProcessors);
         try {
             String[] classesToSkip =
                 JOrphanUtils.split(JMeterUtils.getPropDefault("not_in_menu", ""), ","); //$NON-NLS-1$
             for (int i = 0; i < classesToSkip.length; i++) {
                 elementsToSkip.add(classesToSkip[i].trim());
             }
 
             initializeMenus();
             sortPluginMenus();
         } catch (Throwable e) {
             log.error("", e);
             if (e instanceof Error){
                 throw (Error) e;
             }
             if (e instanceof RuntimeException){
                 throw (RuntimeException) e;
             }
         }
     }
 
     /**
      * Private constructor to prevent instantiation.
      */
     private MenuFactory() {
     }
 
     public static void addEditMenu(JPopupMenu menu, boolean removable) {
         addSeparator(menu);
         if (removable) {
             menu.add(makeMenuItemRes("cut", ActionNames.CUT, KeyStrokes.CUT)); //$NON-NLS-1$
         }
         menu.add(makeMenuItemRes("copy", ActionNames.COPY, KeyStrokes.COPY));  //$NON-NLS-1$
         menu.add(makeMenuItemRes("paste", ActionNames.PASTE, KeyStrokes.PASTE)); //$NON-NLS-1$
         menu.add(makeMenuItemRes("reset_gui", ActionNames.RESET_GUI )); //$NON-NLS-1$
         if (removable) {
             menu.add(makeMenuItemRes("remove", ActionNames.REMOVE, KeyStrokes.REMOVE)); //$NON-NLS-1$
         }
     }
 
     public static void addPasteResetMenu(JPopupMenu menu) {
         addSeparator(menu);
         menu.add(makeMenuItemRes("paste", ActionNames.PASTE, KeyStrokes.PASTE)); //$NON-NLS-1$
         menu.add(makeMenuItemRes("reset_gui", ActionNames.RESET_GUI )); //$NON-NLS-1$
     }
 
     public static void addFileMenu(JPopupMenu menu) {
         addSeparator(menu);
         menu.add(makeMenuItemRes("open", ActionNames.OPEN));// $NON-NLS-1$
         menu.add(makeMenuItemRes("menu_merge", ActionNames.MERGE));// $NON-NLS-1$
         menu.add(makeMenuItemRes("save_as", ActionNames.SAVE_AS));// $NON-NLS-1$
 
         addSeparator(menu);
         JMenuItem savePicture = makeMenuItemRes("save_as_image",// $NON-NLS-1$
                 ActionNames.SAVE_GRAPHICS,
                 KeyStrokes.SAVE_GRAPHICS);
         menu.add(savePicture);
         if (!(GuiPackage.getInstance().getCurrentGui() instanceof Printable)) {
             savePicture.setEnabled(false);
         }
 
         JMenuItem savePictureAll = makeMenuItemRes("save_as_image_all",// $NON-NLS-1$
                 ActionNames.SAVE_GRAPHICS_ALL,
                 KeyStrokes.SAVE_GRAPHICS_ALL);
         menu.add(savePictureAll);
 
         addSeparator(menu);
 
         JMenuItem disabled = makeMenuItemRes("disable", ActionNames.DISABLE);// $NON-NLS-1$
         JMenuItem enabled = makeMenuItemRes("enable", ActionNames.ENABLE);// $NON-NLS-1$
         boolean isEnabled = GuiPackage.getInstance().getTreeListener().getCurrentNode().isEnabled();
         if (isEnabled) {
             disabled.setEnabled(true);
             enabled.setEnabled(false);
         } else {
             disabled.setEnabled(false);
             enabled.setEnabled(true);
         }
         menu.add(enabled);
         menu.add(disabled);
         JMenuItem toggle = makeMenuItemRes("toggle", ActionNames.TOGGLE, KeyStrokes.TOGGLE);// $NON-NLS-1$
         menu.add(toggle);
         addSeparator(menu);
         menu.add(makeMenuItemRes("help", ActionNames.HELP));// $NON-NLS-1$
     }
 
     public static JMenu makeMenus(String[] categories, String label, String actionCommand) {
         JMenu addMenu = new JMenu(label);
         for (int i = 0; i < categories.length; i++) {
             addMenu.add(makeMenu(categories[i], actionCommand));
         }
         return addMenu;
     }
 
     public static JPopupMenu getDefaultControllerMenu() {
         JPopupMenu pop = new JPopupMenu();
         pop.add(MenuFactory.makeMenus(MENU_ADD_CONTROLLER,
                 JMeterUtils.getResString("add"),// $NON-NLS-1$
                 ActionNames.ADD));
         pop.add(makeMenus(MENU_PARENT_CONTROLLER,
                 JMeterUtils.getResString("insert_parent"),// $NON-NLS-1$
                 ActionNames.ADD_PARENT));
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultSamplerMenu() {
         JPopupMenu pop = new JPopupMenu();
         pop.add(MenuFactory.makeMenus(MENU_ADD_SAMPLER,
                 JMeterUtils.getResString("add"),// $NON-NLS-1$
                 ActionNames.ADD));
         pop.add(makeMenus(MENU_PARENT_SAMPLER,
                 JMeterUtils.getResString("insert_parent"),// $NON-NLS-1$
                 ActionNames.ADD_PARENT));
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultConfigElementMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultVisualizerMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultTimerMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultAssertionMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultExtractorMenu() {
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     public static JPopupMenu getDefaultMenu() { // if type is unknown
         JPopupMenu pop = new JPopupMenu();
         MenuFactory.addEditMenu(pop, true);
         MenuFactory.addFileMenu(pop);
         return pop;
     }
 
     /**
      * Create a menu from a menu category.
      *
      * @param category - predefined string (used as key for menuMap HashMap and messages.properties lookup)
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu
      */
     public static JMenu makeMenu(String category, String actionCommand) {
         return makeMenu(menuMap.get(category), actionCommand, JMeterUtils.getResString(category));
     }
 
     /**
      * Create a menu from a collection of items.
      *
      * @param menuInfo - collection of MenuInfo items
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @param menuName
      * @return the menu
      */
     public static JMenu makeMenu(Collection<MenuInfo> menuInfo, String actionCommand, String menuName) {
         JMenu menu = new JMenu(menuName);
         for (MenuInfo info : menuInfo) {
             menu.add(makeMenuItem(info, actionCommand));
         }
         return menu;
     }
 
     public static void setEnabled(JMenu menu) {
         if (menu.getSubElements().length == 0) {
             menu.setEnabled(false);
         }
     }
 
     /**
      * Create a single menu item
      *
      * @param label for the MenuItem
      * @param name for the MenuItem
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu item
      */
     public static JMenuItem makeMenuItem(String label, String name, String actionCommand) {
         JMenuItem newMenuChoice = new JMenuItem(label);
         newMenuChoice.setName(name);
         newMenuChoice.addActionListener(ActionRouter.getInstance());
         if (actionCommand != null) {
             newMenuChoice.setActionCommand(actionCommand);
         }
 
         return newMenuChoice;
     }
 
     /**
      * Create a single menu item from the resource name.
      *
      * @param resource for the MenuItem
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu item
      */
     public static JMenuItem makeMenuItemRes(String resource, String actionCommand) {
         JMenuItem newMenuChoice = new JMenuItem(JMeterUtils.getResString(resource));
         newMenuChoice.setName(resource);
         newMenuChoice.addActionListener(ActionRouter.getInstance());
         if (actionCommand != null) {
             newMenuChoice.setActionCommand(actionCommand);
         }
 
         return newMenuChoice;
     }
 
     /**
      * Create a single menu item from a MenuInfo object
      *
      * @param info the MenuInfo object
      * @param actionCommand - predefined string, e.g. ActionNames.ADD
      *     @see org.apache.jmeter.gui.action.ActionNames
      * @return the menu item
      */
     public static Component makeMenuItem(MenuInfo info, String actionCommand) {
         JMenuItem newMenuChoice = new JMenuItem(info.getLabel());
         newMenuChoice.setName(info.getClassName());
         newMenuChoice.addActionListener(ActionRouter.getInstance());
         if (actionCommand != null) {
             newMenuChoice.setActionCommand(actionCommand);
         }
 
         return newMenuChoice;
     }
 
     public static JMenuItem makeMenuItemRes(String resource, String actionCommand, KeyStroke accel) {
         JMenuItem item = makeMenuItemRes(resource, actionCommand);
         item.setAccelerator(accel);
         return item;
     }
 
     public static JMenuItem makeMenuItem(String label, String name, String actionCommand, KeyStroke accel) {
         JMenuItem item = makeMenuItem(label, name, actionCommand);
         item.setAccelerator(accel);
         return item;
     }
 
     private static void initializeMenus() {
         try {
             List<String> guiClasses = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(), new Class[] {
                     JMeterGUIComponent.class, TestBean.class });
             Collections.sort(guiClasses);
             for (String name : guiClasses) {
 
                 /*
                  * JMeterTreeNode and TestBeanGUI are special GUI classes, and
                  * aren't intended to be added to menus
                  *
                  * TODO: find a better way of checking this
                  */
                 if (name.endsWith("JMeterTreeNode") // $NON-NLS-1$
                         || name.endsWith("TestBeanGUI")) {// $NON-NLS-1$
                     continue;// Don't try to instantiate these
                 }
 
                 if (elementsToSkip.contains(name)) { // No point instantiating class
                     log.info("Skipping " + name);
                     continue;
                 }
 
+                boolean hideBean = false; // Should the TestBean be hidden?
+
                 JMeterGUIComponent item;
                 try {
                     Class<?> c = Class.forName(name);
                     if (TestBean.class.isAssignableFrom(c)) {
-                        item = new TestBeanGUI(c);
+                        TestBeanGUI tbgui = new TestBeanGUI(c);
+                        hideBean = tbgui.isHidden() || (tbgui.isExpert() && !JMeterUtils.isExpertMode());
+                        item = tbgui;
                     } else {
                         item = (JMeterGUIComponent) c.newInstance();
                     }
                 } catch (NoClassDefFoundError e) {
                     log.warn("Missing jar? Could not create " + name + ". " + e);
                     continue;
                 } catch (Throwable e) {
                     log.warn("Could not instantiate " + name, e);
                     if (e instanceof Error){
                         throw (Error) e;
                     }
                     if (e instanceof RuntimeException){
                         throw (RuntimeException) e;
                     }
                     continue;
                 }
-                if (elementsToSkip.contains(item.getStaticLabel())) {
+                if (hideBean || elementsToSkip.contains(item.getStaticLabel())) {
                     log.info("Skipping " + name);
                     continue;
                 } else {
                     elementsToSkip.add(name); // Don't add it again
                 }
                 Collection<String> categories = item.getMenuCategories();
                 if (categories == null) {
                     log.debug(name + " participates in no menus.");
                     continue;
                 }
                 if (categories.contains(THREADS)) {
                     threads.add(new MenuInfo(item, name));
                 }
                 if (categories.contains(FRAGMENTS)) {
                     fragments.add(new MenuInfo(item, name));
                 }
                 if (categories.contains(TIMERS)) {
                     timers.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(POST_PROCESSORS)) {
                     postProcessors.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(PRE_PROCESSORS)) {
                     preProcessors.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(CONTROLLERS)) {
                     controllers.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(SAMPLERS)) {
                     samplers.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(NON_TEST_ELEMENTS)) {
                     nonTestElements.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(LISTENERS)) {
                     listeners.add(new MenuInfo(item, name));
                 }
 
                 if (categories.contains(CONFIG_ELEMENTS)) {
                     configElements.add(new MenuInfo(item, name));
                 }
                 if (categories.contains(ASSERTIONS)) {
                     assertions.add(new MenuInfo(item, name));
                 }
 
             }
         } catch (IOException e) {
             log.error("", e);
         }
     }
 
     private static void addSeparator(JPopupMenu menu) {
         MenuElement[] elements = menu.getSubElements();
         if ((elements.length > 0) && !(elements[elements.length - 1] instanceof JPopupMenu.Separator)) {
             menu.addSeparator();
         }
     }
 
     /**
      * Determine whether or not nodes can be added to this parent.
      *
      * Used by Merge
      *
      * @param parentNode
      * @param element - top-level test element to be added
      *
      * @return whether it is OK to add the element to this parent
      */
     public static boolean canAddTo(JMeterTreeNode parentNode, TestElement element) {
         JMeterTreeNode node = new JMeterTreeNode(element, null);
         return canAddTo(parentNode, new JMeterTreeNode[]{node});
     }
 
     /**
      * Determine whether or not nodes can be added to this parent.
      *
      * Used by DragNDrop and Paste.
      *
      * @param parentNode
      * @param nodes - array of nodes that are to be added
      *
      * @return whether it is OK to add the dragged nodes to this parent
      */
     public static boolean canAddTo(JMeterTreeNode parentNode, JMeterTreeNode nodes[]) {
         if (null == parentNode) {
             return false;
         }
         if (foundClass(nodes, new Class[]{WorkBench.class})){// Can't add a Workbench anywhere
             return false;
         }
         if (foundClass(nodes, new Class[]{TestPlan.class})){// Can't add a TestPlan anywhere
             return false;
         }
         TestElement parent = parentNode.getTestElement();
 
         // Force TestFragment to only be pastable under a Test Plan
         if (foundClass(nodes, new Class[]{org.apache.jmeter.control.TestFragmentController.class})){
             if (parent instanceof TestPlan)
                 return true;
             return false;
         }
 
         if (parent instanceof WorkBench) {// allow everything else
             return true;
         }
         if (parent instanceof TestPlan) {
             if (foundClass(nodes,
                      new Class[]{Sampler.class, Controller.class}, // Samplers and Controllers need not apply ...
                      org.apache.jmeter.threads.AbstractThreadGroup.class)  // but AbstractThreadGroup (Controller) is OK
                 ){
                 return false;
             }
             return true;
         }
         // AbstractThreadGroup is only allowed under a TestPlan
         if (foundClass(nodes, new Class[]{org.apache.jmeter.threads.AbstractThreadGroup.class})){
             return false;
         }
         if (parent instanceof Controller) {// Includes thread group; anything goes
             return true;
         }
         if (parent instanceof Sampler) {// Samplers and Controllers need not apply ...
             if (foundClass(nodes, new Class[]{Sampler.class, Controller.class})){
                 return false;
             }
             return true;
         }
         // All other
         return false;
     }
 
     // Is any node an instance of one of the classes?
     private static boolean foundClass(JMeterTreeNode nodes[],Class<?> classes[]){
         for (int i = 0; i < nodes.length; i++) {
             JMeterTreeNode node = nodes[i];
             for (int j=0; j < classes.length; j++) {
                 if (classes[j].isInstance(node.getUserObject())){
                     return true;
                 }
             }
         }
         return false;
     }
 
     // Is any node an instance of one of the classes, but not an exception?
     private static boolean foundClass(JMeterTreeNode nodes[],Class<?> classes[], Class<?> except){
         for (int i = 0; i < nodes.length; i++) {
             JMeterTreeNode node = nodes[i];
             Object userObject = node.getUserObject();
             if (!except.isInstance(userObject)) {
                 for (int j=0; j < classes.length; j++) {
                     if (classes[j].isInstance(userObject)){
                         return true;
                     }
                 }
             }
         }
         return false;
     }
 
     // Methods used for Test cases
     static int menuMap_size() {
         return menuMap.size();
     }
     static int assertions_size() {
         return assertions.size();
     }
     static int configElements_size() {
         return configElements.size();
     }
     static int controllers_size() {
         return controllers.size();
     }
     static int listeners_size() {
         return listeners.size();
     }
     static int nonTestElements_size() {
         return nonTestElements.size();
     }
     static int postProcessors_size() {
         return postProcessors.size();
     }
     static int preProcessors_size() {
         return preProcessors.size();
     }
     static int samplers_size() {
         return samplers.size();
     }
     static int timers_size() {
         return timers.size();
     }
     static int elementsToSkip_size() {
         return elementsToSkip.size();
     }
 
     /**
      * Menu sort helper class
      */
     private static class MenuInfoComparator implements Comparator<MenuInfo>, Serializable {
         private static final long serialVersionUID = 1L;
         public int compare(MenuInfo o1, MenuInfo o2) {
               return o1.getLabel().compareTo(o2.getLabel());
         }
     }
 
     /**
      * Sort loaded menus
      */
     private static void sortPluginMenus() {
        for (List<MenuInfo> menuToSort : menuMap.values()) {
           Collections.sort(menuToSort, new MenuInfoComparator());
        }
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
index e84d436b4..f0abfd75c 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
@@ -1,438 +1,446 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.beans.BeanDescriptor;
 import java.beans.BeanInfo;
 import java.beans.Customizer;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditorManager;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 import javax.swing.JPopupMenu;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.gui.AbstractControllerGui;
 import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
 import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testbeans.BeanInfoSupport;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.AbstractProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.timers.gui.AbstractTimerGui;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * JMeter GUI element editing for TestBean elements.
  * <p>
  * The actual GUI is always a bean customizer: if the bean descriptor provides
  * one, it will be used; otherwise, a GenericTestBeanCustomizer will be created
  * for this purpose.
  * <p>
  * Those customizers deviate from the standards only in that, instead of a bean,
  * they will receive a Map in the setObject call. This will be a property name
  * to value Map. The customizer is also in charge of initializing empty Maps
  * with sensible initial values.
  * <p>
  * If the provided Customizer class implements the SharedCustomizer interface,
  * the same instance of the customizer will be reused for all beans of the type:
  * setObject(map) can then be called multiple times. Otherwise, one separate
  * instance will be used for each element. For efficiency reasons, most
  * customizers should implement SharedCustomizer.
  *
  */
 public class TestBeanGUI extends AbstractJMeterGuiComponent implements JMeterGUIComponent {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final Class<?> testBeanClass;
 
     private transient BeanInfo beanInfo;
 
     private final Class<?> customizerClass;
 
     /**
      * The single customizer if the customizer class implements
      * SharedCustomizer, null otherwise.
      */
     private Customizer customizer = null;
 
     /**
      * TestElement to Customizer map if customizer is null. This is necessary to
      * avoid the cost of creating a new customizer on each edit. The cache size
      * needs to be limited, though, to avoid memory issues when editing very
      * large test plans.
      */
     @SuppressWarnings("unchecked")
     private final Map<TestElement, Customizer> customizers = new LRUMap(20);
 
     /**
      * Index of the customizer in the JPanel's child component list:
      */
     private int customizerIndexInPanel;
 
     /**
      * The property name to value map that the active customizer edits:
      */
     private final Map<String, Object> propertyMap = new HashMap<String, Object>();
 
     /**
      * Whether the GUI components have been created.
      */
     private boolean initialized = false;
 
     static {
         List<String> paths = new LinkedList<String>();
         paths.add("org.apache.jmeter.testbeans.gui");// $NON-NLS-1$
         paths.addAll(Arrays.asList(PropertyEditorManager.getEditorSearchPath()));
         String s = JMeterUtils.getPropDefault("propertyEditorSearchPath", null);// $NON-NLS-1$
         if (s != null) {
             paths.addAll(Arrays.asList(JOrphanUtils.split(s, ",", "")));// $NON-NLS-1$ // $NON-NLS-2$
         }
         PropertyEditorManager.setEditorSearchPath(paths.toArray(new String[0]));
     }
 
     // Dummy for JUnit test
     public TestBeanGUI() {
         log.warn("Constructor only for use in testing");// $NON-NLS-1$
         testBeanClass = null;
         customizerClass = null;
     }
 
     public TestBeanGUI(Class<?> testBeanClass) {
         super();
         log.debug("testing class: " + testBeanClass.getName());
         // A quick verification, just in case:
         if (!TestBean.class.isAssignableFrom(testBeanClass)) {
             Error e = new Error();
             log.error("This should never happen!", e);
             throw e; // Programming error: bail out.
         }
 
         this.testBeanClass = testBeanClass;
 
         // Get the beanInfo:
         try {
             beanInfo = Introspector.getBeanInfo(testBeanClass);
         } catch (IntrospectionException e) {
             log.error("Can't get beanInfo for " + testBeanClass.getName(), e);
             throw new Error(e.toString()); // Programming error. Don't
                                             // continue.
         }
 
         customizerClass = beanInfo.getBeanDescriptor().getCustomizerClass();
 
         // Creation of the customizer and GUI initialization is delayed until
         // the
         // first
         // configure call. We don't need all that just to find out the static
         // label, menu
         // categories, etc!
         initialized = false;
     }
 
     private Customizer createCustomizer() {
         try {
             return (Customizer) customizerClass.newInstance();
         } catch (InstantiationException e) {
             log.error("Could not instantiate customizer of class " + customizerClass, e);
             throw new Error(e.toString());
         } catch (IllegalAccessException e) {
             log.error("Could not instantiate customizer of class " + customizerClass, e);
             throw new Error(e.toString());
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public String getStaticLabel() {
         if (beanInfo == null){
             return "null";// $NON-NLS-1$
         }
         return beanInfo.getBeanDescriptor().getDisplayName();
     }
 
     /**
      * {@inheritDoc}
      */
    public TestElement createTestElement() {
         try {
             TestElement element = (TestElement) testBeanClass.newInstance();
             // configure(element);
             // super.clear(); // set name, enabled.
             modifyTestElement(element); // put the default values back into the
             // new element
             return element;
         } catch (InstantiationException e) {
             log.error("Can't create test element", e);
             throw new Error(e.toString()); // Programming error. Don't
                                             // continue.
         } catch (IllegalAccessException e) {
             log.error("Can't create test element", e);
             throw new Error(e.toString()); // Programming error. Don't
                                             // continue.
         }
     }
 
    /**
     * {@inheritDoc}
     */
     public void modifyTestElement(TestElement element) {
         // Fetch data from screen fields
         if (customizer instanceof GenericTestBeanCustomizer) {
             GenericTestBeanCustomizer gtbc = (GenericTestBeanCustomizer) customizer;
             gtbc.saveGuiFields();
         }
         configureTestElement(element);
 
         // Copy all property values from the map into the element:
         PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
         for (int i = 0; i < props.length; i++) {
             String name = props[i].getName();
             Object value = propertyMap.get(name);
             log.debug("Modify " + name + " to " + value);
             if (value == null) {
                 Object valueNotUnDefined = props[i].getValue(BeanInfoSupport.NOT_UNDEFINED);
                 if (valueNotUnDefined != null && ((Boolean) valueNotUnDefined).booleanValue()) {
                     setPropertyInElement(element, name, props[i].getValue(BeanInfoSupport.DEFAULT));
                 } else {
                     element.removeProperty(name);
                 }
             } else {
                 setPropertyInElement(element, name, propertyMap.get(name));
             }
         }
     }
 
     /**
      * @param element
      * @param name
      */
     private void setPropertyInElement(TestElement element, String name, Object value) {
         JMeterProperty jprop = AbstractProperty.createProperty(value);
         jprop.setName(name);
         element.setProperty(jprop);
     }
 
     /**
      * {@inheritDoc}
      */
     public JPopupMenu createPopupMenu() {
         if (Timer.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultTimerMenu();
         }
         else if(Sampler.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultSamplerMenu();
         }
         else if(ConfigElement.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultConfigElementMenu();
         }
         else if(Assertion.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultAssertionMenu();
         }
         else if(PostProcessor.class.isAssignableFrom(testBeanClass) ||
                 PreProcessor.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultExtractorMenu();
         }
         else if(Visualizer.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultVisualizerMenu();
         }
         else if(Controller.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultControllerMenu();
         }
         else {
             log.warn("Cannot determine PopupMenu for "+testBeanClass.getName());
             return MenuFactory.getDefaultMenu();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void configure(TestElement element) {
         if (!initialized){
             init();
         }
         clearGui();
 
         super.configure(element);
 
         // Copy all property values into the map:
         for (PropertyIterator jprops = element.propertyIterator(); jprops.hasNext();) {
             JMeterProperty jprop = jprops.next();
             propertyMap.put(jprop.getName(), jprop.getObjectValue());
         }
 
         if (customizer != null) {
             customizer.setObject(propertyMap);
         } else {
             if (initialized){
                 remove(customizerIndexInPanel);
             }
             Customizer c = customizers.get(element);
             if (c == null) {
                 c = createCustomizer();
                 c.setObject(propertyMap);
                 customizers.put(element, c);
             }
             add((Component) c, BorderLayout.CENTER);
         }
 
         initialized = true;
     }
 
     /** {@inheritDoc} */
     public Collection<String> getMenuCategories() {
         List<String> menuCategories = new LinkedList<String>();
         BeanDescriptor bd = beanInfo.getBeanDescriptor();
 
         // We don't want to show expert beans in the menus unless we're
         // in expert mode:
         if (bd.isExpert() && !JMeterUtils.isExpertMode()) {
             return null;
         }
 
         int matches = 0; // How many classes can we assign from?
         // TODO: there must be a nicer way...
         if (Assertion.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.ASSERTIONS);
             bd.setValue(TestElement.GUI_CLASS, AbstractAssertionGui.class.getName());
             matches++;
         }
         if (ConfigElement.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.CONFIG_ELEMENTS);
             bd.setValue(TestElement.GUI_CLASS, AbstractConfigGui.class.getName());
             matches++;
         }
         if (Controller.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.CONTROLLERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractControllerGui.class.getName());
             matches++;
         }
         if (Visualizer.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.LISTENERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractVisualizer.class.getName());
             matches++;
         }
         if (PostProcessor.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.POST_PROCESSORS);
             bd.setValue(TestElement.GUI_CLASS, AbstractPostProcessorGui.class.getName());
             matches++;
         }
         if (PreProcessor.class.isAssignableFrom(testBeanClass)) {
             matches++;
             menuCategories.add(MenuFactory.PRE_PROCESSORS);
             bd.setValue(TestElement.GUI_CLASS, AbstractPreProcessorGui.class.getName());
         }
         if (Sampler.class.isAssignableFrom(testBeanClass)) {
             matches++;
             menuCategories.add(MenuFactory.SAMPLERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractSamplerGui.class.getName());
         }
         if (Timer.class.isAssignableFrom(testBeanClass)) {
             matches++;
             menuCategories.add(MenuFactory.TIMERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractTimerGui.class.getName());
         }
         if (matches == 0) {
             log.error("Could not assign GUI class to " + testBeanClass.getName());
         } else if (matches > 1) {// may be impossible, but no harm in
                                     // checking ...
             log.error("More than 1 GUI class found for " + testBeanClass.getName());
         }
         return menuCategories;
     }
 
     private void init() {
         setLayout(new BorderLayout(0, 5));
 
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         customizerIndexInPanel = getComponentCount();
 
         if (customizerClass == null) {
             customizer = new GenericTestBeanCustomizer(beanInfo);
         } else if (SharedCustomizer.class.isAssignableFrom(customizerClass)) {
             customizer = createCustomizer();
         }
 
         if (customizer != null){
             add((Component) customizer, BorderLayout.CENTER);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     public String getLabelResource() {
         // @see getStaticLabel
         return null;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void clearGui() {
         super.clearGui();
         if (customizer instanceof GenericTestBeanCustomizer) {
             GenericTestBeanCustomizer gtbc = (GenericTestBeanCustomizer) customizer;
             gtbc.clearGuiFields();
         }
     }
+
+    public boolean isHidden() {
+        return beanInfo.getBeanDescriptor().isHidden();
+    }
+
+    public boolean isExpert() {
+        return beanInfo.getBeanDescriptor().isExpert();
+    }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index bf1c4bab8..86f428cd9 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,222 +1,223 @@
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
 <p>
 TCP Sampler handles SocketTimeoutException, SocketException and InterruptedIOException differently since 2.5.2, when
 these occurs, Sampler is marked as failed.
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
 <li>Bug 52064 - OutOfMemory Risk in CacheManager</li>
 <li>Bug 51919 - Random ConcurrentModificationException or NoSuchElementException in CookieManager#removeMatchingCookies when using Concurrent Download</li>
 <li>Bug 52126 - HttpClient4 does not clear cookies between iterations</li>
 <li>Bug 52129 - Reported Body Size is wrong when using HTTP Client 4 and Keep Alive connection</li>
 <li>Bug 52137 - Problems with HTTP Cache Manager</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 51996 - JMS Initial Context leak newly created Context when Multiple Thread enter InitialContextFactory#lookupContext at the same time</li>
 <li>Bug 51691 - Authorization does not work for JMS Publisher and JMS Subscriber</li>
 <li>Bug 52036 - Durable Subscription fails with ActiveMQ due to missing clientId field</li>
 <li>Bug 52044 - JMS Subscriber used with many threads leads to javax.naming.NamingException: Something already bound with ActiveMQ</li>
 <li>Bug 52072 - LengthPrefixedBinaryTcpClientImpl may end a sample prematurely</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 51865 - Infinite loop inside thread group does not work properly if "Start next loop after a Sample error" option set</li>
 <li>Bug 51868 - A lot of exceptions in jmeter.log while using option "Start next loop" for thread</li>
 <li>Bug 51866 - Counter under loop doesn't work properly if "Start next loop on error" option set for thread group</li>
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
 <li>Bug 52029 - Command-line shutdown only gets sent to last engine that was started</li>
 <li>Bug 52093 - Toolbar ToolTips don't switch language</li>
 <li>Bug 51733 - SyncTimer is messed up if you a interrupt a test plan</li>
 <li>Bug 52118 - New toolbar : shutdown and stop buttons not disabled when no test is running</li>
 <li>Bug 52125 - StatCalculator.addAll(StatCalculator calc) joins incorrect if there are more samples with the same response time in one of the TreeMap</li>
 <li>Bug 50799 - Having a non-HTTP sampler in a http test plan prevents multiple header managers from working</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>Bug 51981 - Better support for file: protocol in HTTP sampler</li>
 <li>Bug 52033 - Allowing multiple certificates (JKS)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Bug 51419 - JMS Subscriber: ability to use Selectors</li>
 <li>Bug 52088 - JMS Sampler : Add a selector when REQUEST / RESPONSE is chosen</li>
 <li>Bug 52104 - TCP Sampler handles badly errors</li>
 <li>Bug 52115 - SOAP/XML-RPC should not send a POST request when file to send is not found</li>
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
 <li>Bug 52128 - Add JDBC pre- and post-processor</li>
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
 <li>Bug 51876 - Functionality to search in Samplers TreeView</li>
 <li>Bug 52019 - Add menu option to Start a test ignoring Pause Timers</li>
 <li>Bug 52027 - Allow System or CrossPlatform LAF to be set from options menu</li>
 <li>Bug 52037 - Remember user-set LaF over restarts.</li>
 <li>Bug 51861 - Improve HTTP Request GUI to better show parameters without name (GWT RPC requests for example) (UNDER DEVELOPMENT)</li>
 <li>Bug 52040 - Add a toolbar in JMeter main window</li>
 <li>Bug 51816 - Comment Field in User Defined Variables control.</li>
 <li>Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber</li>
 <li>Bug 52103 - Add automatic scrolling option to table visualizer</li>
 <li>Bug 52097 - Save As should point to same folder that was used to open a file if MRU list is used</li>
 <li>Bug 52085 - Allow multiple selection in arguments panel</li>
 <li>Bug 52099 - Allow to set the transaction isolation in the JDBC Connection Configuration</li>
 <li>Bug 52116 - Allow to add (paste) entries from the clipboard to an arguments list</li>
 <li>Bug 51091 - New function returning the name of the current "Test Plan"</li>
+<li>Bug 52160 - Don't display TestBeanGui items which are flagged as hidden</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>fixes to build.xml: support scripts; localise re-usable property names</li>
 <li>Bug 51923 - Counter function bug or documentation issue ? (fixed docs)</li>
 <li>Update velocity.jar to 1.7 (from 1.6.2)</li>
 <li>Bug 51954 - Generated documents include &lt;/br&gt; entries which cause extra blank lines </li>
 <li>Bug 52075 - JMeterProperty.clone() currently returns Object; it should return JMeterProperty</li>
 </ul>
 
 </section> 
 </body> 
 </document>
