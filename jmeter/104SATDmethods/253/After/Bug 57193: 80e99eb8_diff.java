diff --git a/src/components/org/apache/jmeter/extractor/Extractor.java b/src/components/org/apache/jmeter/extractor/Extractor.java
index e26224b4f..ab1e24d96 100644
--- a/src/components/org/apache/jmeter/extractor/Extractor.java
+++ b/src/components/org/apache/jmeter/extractor/Extractor.java
@@ -1,48 +1,48 @@
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
  *
  */
 
 package org.apache.jmeter.extractor;
 
 import java.io.Serializable;
 import java.util.List;
 
 /**
  * CSS/JQuery based extractor for HTML pages
  * @since 2.9
  */
 public interface Extractor extends Serializable {
     /**
      * 
      * @param expression Expression used for extraction of nodes
      * @param attribute Attribute name to return 
      * @param matchNumber Match number
      * @param inputString Page or excerpt
-     * @param result List<String> results
+     * @param result List of results
      * @param found current matches found
      * @param cacheKey If not null, the implementation is encouraged to cache parsing result and use this key as part of cache key
      * @return match found updated
      */
     int extract(
             String expression,
             String attribute,
             int matchNumber, 
             String inputString, 
             List<String> result,
             int found,
             String cacheKey);
 }
diff --git a/src/core/org/apache/jmeter/gui/GuiPackage.java b/src/core/org/apache/jmeter/gui/GuiPackage.java
index 0010a253a..8a238f1db 100644
--- a/src/core/org/apache/jmeter/gui/GuiPackage.java
+++ b/src/core/org/apache/jmeter/gui/GuiPackage.java
@@ -1,864 +1,864 @@
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
 
 package org.apache.jmeter.gui;
 
 import java.awt.Component;
 import java.awt.event.MouseEvent;
 import java.beans.Introspector;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import javax.swing.JCheckBoxMenuItem;
 import javax.swing.JOptionPane;
 import javax.swing.JPopupMenu;
 import javax.swing.JToolBar;
 import javax.swing.SwingUtilities;
 
 import org.apache.jmeter.engine.util.ValueReplacer;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.UndoHistory.HistoryListener;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.JMeterToolBar;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.TestBeanGUI;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * GuiPackage is a static class that provides convenient access to information
  * about the current state of JMeter's GUI. Any GUI class can grab a handle to
  * GuiPackage by calling the static method {@link #getInstance()} and then use
  * it to query the GUI about it's state. When actions, for instance, need to
  * affect the GUI, they typically use GuiPackage to get access to different
  * parts of the GUI.
  *
  */
 public final class GuiPackage implements LocaleChangeListener, HistoryListener {
     /** Logging. */
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** Singleton instance. */
     private static GuiPackage guiPack;
 
     /**
      * Flag indicating whether or not parts of the tree have changed since they
      * were last saved.
      */
     private boolean dirty = false;
 
     /**
      * Map from TestElement to JMeterGUIComponent, mapping the nodes in the tree
      * to their corresponding GUI components.
      */
     private Map<TestElement, JMeterGUIComponent> nodesToGui = new HashMap<TestElement, JMeterGUIComponent>();
 
     /**
      * Map from Class to JMeterGUIComponent, mapping the Class of a GUI
      * component to an instance of that component.
      */
     private Map<Class<?>, JMeterGUIComponent> guis = new HashMap<Class<?>, JMeterGUIComponent>();
 
     /**
      * Map from Class to TestBeanGUI, mapping the Class of a TestBean to an
      * instance of TestBeanGUI to be used to edit such components.
      */
     private Map<Class<?>, JMeterGUIComponent> testBeanGUIs = new HashMap<Class<?>, JMeterGUIComponent>();
 
     /** The currently selected node in the tree. */
     private JMeterTreeNode currentNode = null;
 
     private boolean currentNodeUpdated = false;
 
     /** The model for JMeter's test tree. */
     private final JMeterTreeModel treeModel;
 
     /** The listener for JMeter's test tree. */
     private final JMeterTreeListener treeListener;
 
     /** The main JMeter frame. */
     private MainFrame mainFrame;
 
     /** The main JMeter toolbar. */
     private JToolBar toolbar;
 
     /** The menu item toolbar. */
     private JCheckBoxMenuItem menuToolBar;
 
     /**
      * The LoggerPanel menu item
      */
     private JCheckBoxMenuItem menuItemLoggerPanel;
 
     /**
      * Logger Panel reference
      */
     private LoggerPanel loggerPanel;
 
     /**
      * History for tree states
      */
     private UndoHistory undoHistory = new UndoHistory();
 
     /**
      * Private constructor to permit instantiation only from within this class.
      * Use {@link #getInstance()} to retrieve a singleton instance.
      */
     private GuiPackage(JMeterTreeModel treeModel, JMeterTreeListener treeListener) {
         this.treeModel = treeModel;
         if(undoHistory.isEnabled()) {
             this.treeModel.addTreeModelListener(undoHistory);
         }
         this.treeListener = treeListener;
     }
 
     /**
      * Retrieve the singleton GuiPackage instance.
      *
      * @return the GuiPackage instance (may be null, e.g in non-Gui mode)
      */
     public static GuiPackage getInstance() {
         return guiPack;
     }
     
     /**
      * Register as listener of:
      * - UndoHistory
      * - Locale Changes
      */
     public void registerAsListener() {
         if(undoHistory.isEnabled()) {
             this.undoHistory.registerHistoryListener(this);
         }
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     /**
      * When GuiPackage is requested for the first time, it should be given
      * handles to JMeter's Tree Listener and TreeModel.
      *
      * @param listener
      *            the TreeListener for JMeter's test tree
      * @param treeModel
      *            the model for JMeter's test tree
      *
      * @return GuiPackage
      */
     public static GuiPackage getInstance(JMeterTreeListener listener, JMeterTreeModel treeModel) {
         if (guiPack == null) {
             guiPack = new GuiPackage(treeModel, listener);
             guiPack.undoHistory.add(treeModel, "Created");
         }
         return guiPack;
     }
 
     /**
      * Get a JMeterGUIComponent for the specified test element. If the GUI has
      * already been created, that instance will be returned. Otherwise, if a GUI
      * component of the same type has been created, and the component is not
      * marked as an {@link UnsharedComponent}, that shared component will be
      * returned. Otherwise, a new instance of the component will be created. The
      * TestElement's GUI_CLASS property will be used to determine the
      * appropriate type of GUI component to use.
      *
      * @param node
      *            the test element which this GUI is being created for
      *
      * @return the GUI component corresponding to the specified test element
      */
     public JMeterGUIComponent getGui(TestElement node) {
         String testClassName = node.getPropertyAsString(TestElement.TEST_CLASS);
         String guiClassName = node.getPropertyAsString(TestElement.GUI_CLASS);
         try {
             Class<?> testClass;
             if (testClassName.equals("")) { // $NON-NLS-1$
                 testClass = node.getClass();
             } else {
                 testClass = Class.forName(testClassName);
             }
             Class<?> guiClass = null;
             if (!guiClassName.equals("")) { // $NON-NLS-1$
                 guiClass = Class.forName(guiClassName);
             }
             return getGui(node, guiClass, testClass);
         } catch (ClassNotFoundException e) {
             log.error("Could not get GUI for " + node, e);
             return null;
         }
     }
 
     /**
      * Get a JMeterGUIComponent for the specified test element. If the GUI has
      * already been created, that instance will be returned. Otherwise, if a GUI
      * component of the same type has been created, and the component is not
      * marked as an {@link UnsharedComponent}, that shared component will be
      * returned. Otherwise, a new instance of the component will be created.
      *
      * @param node
      *            the test element which this GUI is being created for
      * @param guiClass
      *            the fully qualifed class name of the GUI component which will
      *            be created if it doesn't already exist
      * @param testClass
      *            the fully qualifed class name of the test elements which have
      *            to be edited by the returned GUI component
      *
      * @return the GUI component corresponding to the specified test element
      */
     public JMeterGUIComponent getGui(TestElement node, Class<?> guiClass, Class<?> testClass) {
         try {
             JMeterGUIComponent comp = nodesToGui.get(node);
             if (comp == null) {
                 comp = getGuiFromCache(guiClass, testClass);
                 nodesToGui.put(node, comp);
             }
             log.debug("Gui retrieved = " + comp);
             return comp;
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
             return null;
         }
     }
 
     /**
      * Remove a test element from the tree. This removes the reference to any
      * associated GUI component.
      *
      * @param node
      *            the test element being removed
      */
     public void removeNode(TestElement node) {
         nodesToGui.remove(node);
     }
 
     /**
      * Convenience method for grabbing the gui for the current node.
      *
      * @return the GUI component associated with the currently selected node
      */
     public JMeterGUIComponent getCurrentGui() {
         try {
             updateCurrentNode();
             TestElement curNode = treeListener.getCurrentNode().getTestElement();
             JMeterGUIComponent comp = getGui(curNode);
             comp.clearGui();
             log.debug("Updating gui to new node");
             comp.configure(curNode);
             currentNodeUpdated = false;
             return comp;
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
             return null;
         }
     }
 
     /**
      * Find the JMeterTreeNode for a certain TestElement object.
      *
      * @param userObject
      *            the test element to search for
      * @return the tree node associated with the test element
      */
     public JMeterTreeNode getNodeOf(TestElement userObject) {
         return treeModel.getNodeOf(userObject);
     }
 
     /**
      * Create a TestElement corresponding to the specified GUI class.
      *
      * @param guiClass
      *            the fully qualified class name of the GUI component or a
      *            TestBean class for TestBeanGUIs.
      * @param testClass
      *            the fully qualified class name of the test elements edited by
      *            this GUI component.
      * @return the test element corresponding to the specified GUI class.
      */
     public TestElement createTestElement(Class<?> guiClass, Class<?> testClass) {
         try {
             JMeterGUIComponent comp = getGuiFromCache(guiClass, testClass);
             comp.clearGui();
             TestElement node = comp.createTestElement();
             nodesToGui.put(node, comp);
             return node;
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
             return null;
         }
     }
 
     /**
      * Create a TestElement for a GUI or TestBean class.
      * <p>
      * This is a utility method to help actions do with one single String
      * parameter.
      *
      * @param objClass
      *            the fully qualified class name of the GUI component or of the
      *            TestBean subclass for which a TestBeanGUI is wanted.
      * @return the test element corresponding to the specified GUI class.
      */
     public TestElement createTestElement(String objClass) {
         JMeterGUIComponent comp;
         Class<?> c;
         try {
             c = Class.forName(objClass);
             if (TestBean.class.isAssignableFrom(c)) {
                 comp = getGuiFromCache(TestBeanGUI.class, c);
             } else {
                 comp = getGuiFromCache(c, null);
             }
             comp.clearGui();
             TestElement node = comp.createTestElement();
             nodesToGui.put(node, comp);
             return node;
         } catch (NoClassDefFoundError e) {
             log.error("Problem retrieving gui for " + objClass, e);
             String msg="Cannot find class: "+e.getMessage();
             JOptionPane.showMessageDialog(null,
                     msg,
                     "Missing jar? See log file." ,
                     JOptionPane.ERROR_MESSAGE);
             throw new RuntimeException(e.toString(), e); // Probably a missing jar
         } catch (ClassNotFoundException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error: bail out.
         } catch (InstantiationException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error: bail out.
         } catch (IllegalAccessException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error: bail out.
         }
     }
 
     /**
      * Get an instance of the specified JMeterGUIComponent class. If an instance
      * of the GUI class has previously been created and it is not marked as an
      * {@link UnsharedComponent}, that shared instance will be returned.
      * Otherwise, a new instance of the component will be created, and shared
      * components will be cached for future retrieval.
      *
      * @param guiClass
      *            the fully qualified class name of the GUI component. This
      *            class must implement JMeterGUIComponent.
      * @param testClass
      *            the fully qualified class name of the test elements edited by
      *            this GUI component. This class must implement TestElement.
      * @return an instance of the specified class
      *
      * @throws InstantiationException
      *             if an instance of the object cannot be created
      * @throws IllegalAccessException
      *             if access rights do not allow the default constructor to be
      *             called
      * @throws ClassNotFoundException
      *             if the specified GUI class cannot be found
      */
     private JMeterGUIComponent getGuiFromCache(Class<?> guiClass, Class<?> testClass) throws InstantiationException,
             IllegalAccessException {
         JMeterGUIComponent comp;
         if (guiClass == TestBeanGUI.class) {
             comp = testBeanGUIs.get(testClass);
             if (comp == null) {
                 comp = new TestBeanGUI(testClass);
                 testBeanGUIs.put(testClass, comp);
             }
         } else {
             comp = guis.get(guiClass);
             if (comp == null) {
                 comp = (JMeterGUIComponent) guiClass.newInstance();
                 if (!(comp instanceof UnsharedComponent)) {
                     guis.put(guiClass, comp);
                 }
             }
         }
         return comp;
     }
 
     /**
      * Update the GUI for the currently selected node. The GUI component is
      * configured to reflect the settings in the current tree node.
      *
      */
     public void updateCurrentGui() {
         updateCurrentNode();
         currentNode = treeListener.getCurrentNode();
         TestElement element = currentNode.getTestElement();
         JMeterGUIComponent comp = getGui(element);
         comp.configure(element);
         currentNodeUpdated = false;
     }
 
     /**
      * This method should be called in order for GuiPackage to change the
      * current node. This will save any changes made to the earlier node before
      * choosing the new node.
      */
     public void updateCurrentNode() {
         try {
             if (currentNode != null && !currentNodeUpdated) {
                 log.debug("Updating current node " + currentNode.getName());
                 JMeterGUIComponent comp = getGui(currentNode.getTestElement());
                 TestElement el = currentNode.getTestElement();
                 int before = getTestElementCheckSum(el);
                 comp.modifyTestElement(el);
                 int after = getTestElementCheckSum(el);
                 if (before != after) {
                     currentNode.nameChanged(); // Bug 50221 - ensure label is updated
                 }
             }
             // The current node is now updated
             currentNodeUpdated = true;
             currentNode = treeListener.getCurrentNode();
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
         }
     }
 
     public JMeterTreeNode getCurrentNode() {
         return treeListener.getCurrentNode();
     }
 
     public TestElement getCurrentElement() {
         return getCurrentNode().getTestElement();
     }
 
     /**
      * The dirty property is a flag that indicates whether there are parts of
      * JMeter's test tree that the user has not saved since last modification.
      * Various (@link Command actions) set this property when components are
      * modified/created/saved.
      *
      * @param dirty
      *            the new value of the dirty flag
      */
     public void setDirty(boolean dirty) {
         this.dirty = dirty;
     }
 
     /**
      * Retrieves the state of the 'dirty' property, a flag that indicates if
      * there are test tree components that have been modified since they were
      * last saved.
      *
      * @return true if some tree components have been modified since they were
      *         last saved, false otherwise
      */
     public boolean isDirty() {
         return dirty;
     }
 
     /**
      * Add a subtree to the currently selected node.
      *
      * @param subTree
      *            the subtree to add.
      *
      * @return the resulting subtree starting with the currently selected node
      *
      * @throws IllegalUserActionException
      *             if a subtree cannot be added to the currently selected node
      */
     public HashTree addSubTree(HashTree subTree) throws IllegalUserActionException {
         HashTree hashTree = treeModel.addSubTree(subTree, treeListener.getCurrentNode());
         undoHistory.clear();
         undoHistory.add(this.treeModel, "Loaded tree");
         return hashTree;
     }
 
     /**
      * Get the currently selected subtree.
      *
      * @return the subtree of the currently selected node
      */
     public HashTree getCurrentSubTree() {
         return treeModel.getCurrentSubTree(treeListener.getCurrentNode());
     }
 
     /**
      * Get the model for JMeter's test tree.
      *
      * @return the JMeter tree model
      */
     /*
      * TODO consider removing this method, and providing method wrappers instead.
      * This would allow the Gui package to do any additional clearups if required,
      * as has been done with clearTestPlan()
     */
     public JMeterTreeModel getTreeModel() {
         return treeModel;
     }
 
     /**
      * Get a ValueReplacer for the test tree.
      *
      * @return a ValueReplacer configured for the test tree
      */
     public ValueReplacer getReplacer() {
         return new ValueReplacer((TestPlan) ((JMeterTreeNode) getTreeModel().getTestPlan().getArray()[0])
                 .getTestElement());
     }
 
     /**
      * Set the main JMeter frame.
      *
      * @param newMainFrame
      *            the new JMeter main frame
      */
     public void setMainFrame(MainFrame newMainFrame) {
         mainFrame = newMainFrame;
     }
 
     /**
      * Get the main JMeter frame.
      *
      * @return the main JMeter frame
      */
     public MainFrame getMainFrame() {
         return mainFrame;
     }
 
     /**
      * Get the listener for JMeter's test tree.
      *
      * @return the JMeter test tree listener
      */
     public JMeterTreeListener getTreeListener() {
         return treeListener;
     }
 
     /**
      * Set the main JMeter toolbar.
      *
      * @param newToolbar
      *            the new JMeter main toolbar
      */
     public void setMainToolbar(JToolBar newToolbar) {
         toolbar = newToolbar;
     }
 
     /**
      * Get the main JMeter toolbar.
      *
      * @return the main JMeter toolbar
      */
     public JToolBar getMainToolbar() {
         return toolbar;
     }
 
     /**
      * Set the menu item toolbar.
      *
      * @param newMenuToolBar
      *            the new menu item toolbar
      */
     public void setMenuItemToolbar(JCheckBoxMenuItem newMenuToolBar) {
         menuToolBar = newMenuToolBar;
     }
 
     /**
      * Get the menu item  toolbar.
      *
      * @return the menu item toolbar
      */
     public JCheckBoxMenuItem getMenuItemToolbar() {
         return menuToolBar;
     }
 
     /**
      * Display the specified popup menu with the source component and location
      * from the specified mouse event.
      *
      * @param e
      *            the mouse event causing this popup to be displayed
      * @param popup
      *            the popup menu to display
      */
     public void displayPopUp(MouseEvent e, JPopupMenu popup) {
         displayPopUp((Component) e.getSource(), e, popup);
     }
 
     /**
      * Display the specified popup menu at the location specified by a mouse
      * event with the specified source component.
      *
      * @param invoker
      *            the source component
      * @param e
      *            the mouse event causing this popup to be displayed
      * @param popup
      *            the popup menu to display
      */
     public void displayPopUp(Component invoker, MouseEvent e, JPopupMenu popup) {
         if (popup != null) {
             log.debug("Showing pop up for " + invoker + " at x,y = " + e.getX() + "," + e.getY());
 
             popup.pack();
             popup.show(invoker, e.getX(), e.getY());
             popup.setVisible(true);
             popup.requestFocusInWindow();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void localeChanged(LocaleChangeEvent event) {
         // FIrst make sure we save the content of the current GUI (since we
         // will flush it away):
         updateCurrentNode();
 
         // Forget about all GUIs we've created so far: we'll need to re-created
         // them all!
         guis = new HashMap<Class<?>, JMeterGUIComponent>();
         nodesToGui = new HashMap<TestElement, JMeterGUIComponent>();
         testBeanGUIs = new HashMap<Class<?>, JMeterGUIComponent>();
 
         // BeanInfo objects also contain locale-sensitive data -- flush them
         // away:
         Introspector.flushCaches();
 
         // Now put the current GUI in place. [This code was copied from the
         // EditCommand action -- we can't just trigger the action because that
         // would populate the current node with the contents of the new GUI --
         // which is empty.]
         MainFrame mf = getMainFrame(); // Fetch once
         if (mf == null) // Probably caused by unit testing on headless system
         {
             log.warn("Mainframe is null");
         } else {
             mf.setMainPanel((javax.swing.JComponent) getCurrentGui());
             mf.setEditMenu(getTreeListener().getCurrentNode().createPopupMenu());
         }
     }
 
     private String testPlanFile;
 
     private final List<Stoppable> stoppables = Collections.synchronizedList(new ArrayList<Stoppable>());
 
     /**
      * Sets the filepath of the current test plan. It's shown in the main frame
      * title and used on saving.
      *
      * @param f
      */
     public void setTestPlanFile(String f) {
         testPlanFile = f;
         getMainFrame().setExtendedFrameTitle(testPlanFile);
         // Enable file revert action if a file is used
         getMainFrame().setFileRevertEnabled(f != null);
         getMainFrame().setProjectFileLoaded(f);
 
         try {
             FileServer.getFileServer().setBasedir(testPlanFile);
         } catch (IllegalStateException e1) {
             log.error("Failure setting file server's base dir", e1);
         }
     }
 
     public String getTestPlanFile() {
         return testPlanFile;
     }
 
     /**
      * Clears the test plan and associated objects.
      * Clears the test plan file name.
      */
     public void clearTestPlan() {
         getTreeModel().clearTestPlan();
         nodesToGui.clear();
         setTestPlanFile(null);
         undoHistory.clear();
         undoHistory.add(this.treeModel, "Initial Tree");
     }
 
     /**
      * Clears the test plan element and associated object
      *
      * @param element to clear
      */
     public void clearTestPlan(TestElement element) {
         getTreeModel().clearTestPlan(element);
         removeNode(element);
         undoHistory.clear();
         undoHistory.add(this.treeModel, "Initial Tree");
     }
 
     public static void showErrorMessage(final String message, final String title){
         showMessage(message,title,JOptionPane.ERROR_MESSAGE);
     }
 
     public static void showInfoMessage(final String message, final String title){
         showMessage(message,title,JOptionPane.INFORMATION_MESSAGE);
     }
 
     public static void showWarningMessage(final String message, final String title){
         showMessage(message,title,JOptionPane.WARNING_MESSAGE);
     }
 
     public static void showMessage(final String message, final String title, final int type){
         if (guiPack == null) {
             return ;
         }
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 JOptionPane.showMessageDialog(null,message,title,type);
             }
         });
 
     }
 
     /**
      * Unregister stoppable
      * @param stoppable Stoppable
      */
     public void unregister(Stoppable stoppable) {
         for (Iterator<Stoppable> iterator = stoppables .iterator(); iterator.hasNext();) {
             Stoppable stopable = iterator.next();
             if(stopable == stoppable)
             {
                 iterator.remove();
             }
         }
     }
 
     /**
      * Register process to stop on reload
      * @param stoppable
      */
     public void register(Stoppable stoppable) {
         stoppables.add(stoppable);
     }
 
     /**
      *
-     * @return List<IStoppable> Copy of IStoppable
+     * @return copy of list of {@link Stoppable}s
      */
     public List<Stoppable> getStoppables() {
         ArrayList<Stoppable> list = new ArrayList<Stoppable>();
         list.addAll(stoppables);
         return list;
     }
 
     /**
      * Set the menu item LoggerPanel.
      * @param menuItemLoggerPanel
      */
     public void setMenuItemLoggerPanel(JCheckBoxMenuItem menuItemLoggerPanel) {
         this.menuItemLoggerPanel = menuItemLoggerPanel;
     }
 
     /**
      * Get the menu item LoggerPanel.
      *
      * @return the menu item LoggerPanel
      */
     public JCheckBoxMenuItem getMenuItemLoggerPanel() {
         return menuItemLoggerPanel;
     }
 
     /**
      * @param loggerPanel LoggerPanel
      */
     public void setLoggerPanel(LoggerPanel loggerPanel) {
         this.loggerPanel = loggerPanel;
     }
 
     /**
      * @return the loggerPanel
      */
     public LoggerPanel getLoggerPanel() {
         return loggerPanel;
     }
 
     /**
      * Navigate back and forward through undo history
      *
      * @param offset int
      */
     public void goInHistory(int offset) {
         undoHistory.moveInHistory(offset, this.treeModel);
     }
 
     /**
      * @return true if history contains redo item
      */
     public boolean canRedo() {
         return undoHistory.canRedo();
     }
 
     /**
      * @return true if history contains undo item
      */
     public boolean canUndo() {
         return undoHistory.canUndo();
     }
 
     /**
      * Compute checksum of TestElement to detect changes
      * the method calculates properties checksum to detect testelement
      * modifications
      * TODO would be better to override hashCode for TestElement, but I decided not to touch it
      *
      * @param el {@link TestElement}
      * @return int checksum
      */
     private int getTestElementCheckSum(TestElement el) {
         int ret = el.getClass().hashCode();
         PropertyIterator it = el.propertyIterator();
         while (it.hasNext()) {
             JMeterProperty obj = it.next();
             if (obj instanceof TestElementProperty) {
                 ret ^= getTestElementCheckSum(((TestElementProperty) obj)
                         .getElement());
             } else {
                 ret ^= obj.getName().hashCode();
                 ret ^= obj.getStringValue().hashCode();
             }
         }
         return ret;
     }
 
     /**
      * Called when history changes, it updates toolbar
      */
     @Override
     public void notifyChangeInHistory(UndoHistory history) {
         ((JMeterToolBar)toolbar).updateUndoRedoIcons(history.canUndo(), history.canRedo());
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/Searchable.java b/src/core/org/apache/jmeter/gui/Searchable.java
index f44dd23db..8a7430b20 100644
--- a/src/core/org/apache/jmeter/gui/Searchable.java
+++ b/src/core/org/apache/jmeter/gui/Searchable.java
@@ -1,33 +1,33 @@
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
 
 package org.apache.jmeter.gui;
 
 import java.util.List;
 
 /**
  * Interface for nodes that are searchable
  */
 public interface Searchable {
     /**
-     * @return List<String> of searchable tokens
+     * @return List of searchable tokens
      * @throws Exception
      */
     List<String> getSearchableTokens()
         throws Exception;
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Searcher.java b/src/core/org/apache/jmeter/gui/action/Searcher.java
index 52fab6fb5..5ad2ec735 100644
--- a/src/core/org/apache/jmeter/gui/action/Searcher.java
+++ b/src/core/org/apache/jmeter/gui/action/Searcher.java
@@ -1,34 +1,34 @@
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
 
 import java.util.List;
 
 /**
  * Search algorithm
  */
 public interface Searcher {
 
     /**
      * Implements the search
-     * @param textTokens List<String> content to be searched
+     * @param textTokens List of content to be searched
      * @return true if search on textTokens is successful
      */
     boolean search(List<String> textTokens);
 }
diff --git a/src/core/org/apache/jmeter/gui/action/UndoCommand.java b/src/core/org/apache/jmeter/gui/action/UndoCommand.java
index 3f346b4a0..8f464d350 100644
--- a/src/core/org/apache/jmeter/gui/action/UndoCommand.java
+++ b/src/core/org/apache/jmeter/gui/action/UndoCommand.java
@@ -1,80 +1,81 @@
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
 
 import org.apache.jmeter.engine.TreeCloner;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jorphan.collections.HashTree;
 
 /**
  * Menu command to serve Undo/Redo
  * @since 2.12
  */
 public class UndoCommand implements Command {
 
     private static final Set<String> commands = new HashSet<String>();
 
     static {
         commands.add(ActionNames.UNDO);
         commands.add(ActionNames.REDO);
     }
 
     @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         GuiPackage guiPackage = GuiPackage.getInstance();
         final String command = e.getActionCommand();
 
         if (command.equals(ActionNames.UNDO)) {
             guiPackage.goInHistory(-1);
         } else if (command.equals(ActionNames.REDO)) {
             guiPackage.goInHistory(1);
         } else {
             throw new IllegalArgumentException("Wrong action called: " + command);
         }
     }
 
     /**
-     * @return Set<String>
+     * @return Set of all action names
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     /**
      * wrapper to use package-visible method
      * and clone tree for saving
      *
      * @param tree to be converted and cloned
+     * @return converted and cloned tree
      */
     public static HashTree convertAndCloneSubTree(HashTree tree) {
         Save executor = new Save();
         executor.convertSubTree(tree);
 
         // convert before clone
         TreeCloner cloner = new TreeCloner(false);
         tree.traverse(cloner);
         return cloner.getClonedTree();
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
index 02fda110a..89c14ba18 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
@@ -1,202 +1,202 @@
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
 
 import java.awt.Image;
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Enumeration;
 import java.util.List;
 
 import javax.swing.ImageIcon;
 import javax.swing.JPopupMenu;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.TreeNode;
 
 import org.apache.jmeter.gui.GUIFactory;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class JMeterTreeNode extends DefaultMutableTreeNode implements NamedTreeNode {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final int TEST_PLAN_LEVEL = 1;
 
     // See Bug 54648
     private transient JMeterTreeModel treeModel;
 
     private boolean markedBySearch;
 
     public JMeterTreeNode() {// Allow serializable test to work
         // TODO: is the serializable test necessary now that JMeterTreeNode is
         // no longer a GUI component?
         this(null, null);
     }
 
     public JMeterTreeNode(TestElement userObj, JMeterTreeModel treeModel) {
         super(userObj);
         this.treeModel = treeModel;
     }
 
     public boolean isEnabled() {
         return getTestElement().isEnabled();
     }
 
     public void setEnabled(boolean enabled) {
         getTestElement().setEnabled(enabled);
         treeModel.nodeChanged(this);
     }
     
     /**
      * Return nodes to level 2
-     * @return {@link List}<JMeterTreeNode>
+     * @return {@link List} of {@link JMeterTreeNode}s
      */
     public List<JMeterTreeNode> getPathToThreadGroup() {
         List<JMeterTreeNode> nodes = new ArrayList<JMeterTreeNode>();
         if(treeModel != null) {
             TreeNode[] nodesToRoot = treeModel.getPathToRoot(this);
             for (TreeNode node : nodesToRoot) {
                 JMeterTreeNode jMeterTreeNode = (JMeterTreeNode) node;
                 int level = jMeterTreeNode.getLevel();
                 if(level<TEST_PLAN_LEVEL) {
                     continue;
                 } else {
                     nodes.add(jMeterTreeNode);
                 }
             }
         }
         return nodes;
     }
     
     /**
      * Tag Node as result of a search
      */
     public void setMarkedBySearch(boolean tagged) {
         this.markedBySearch = tagged;
         treeModel.nodeChanged(this);
     }
     
     /**
      * Node is markedBySearch by a search
      * @return true if marked by search
      */
     public boolean isMarkedBySearch() {
         return this.markedBySearch;
     }
 
     public ImageIcon getIcon() {
         return getIcon(true);
     }
 
     public ImageIcon getIcon(boolean enabled) {
         TestElement testElement = getTestElement();
         try {
             if (testElement instanceof TestBean) {
                 Class<?> testClass = testElement.getClass();
                 try {
                     Image img = Introspector.getBeanInfo(testClass).getIcon(BeanInfo.ICON_COLOR_16x16);
                     // If icon has not been defined, then use GUI_CLASS property
                     if (img == null) {
                         Object clazz = Introspector.getBeanInfo(testClass).getBeanDescriptor()
                                 .getValue(TestElement.GUI_CLASS);
                         if (clazz == null) {
                             log.warn("getIcon(): Can't obtain GUI class from " + testClass.getName());
                             return null;
                         }
                         return GUIFactory.getIcon(Class.forName((String) clazz), enabled);
                     }
                     return new ImageIcon(img);
                 } catch (IntrospectionException e1) {
                     log.error("Can't obtain icon for class "+testElement, e1);
                     throw new org.apache.jorphan.util.JMeterError(e1);
                 }
             }
             return GUIFactory.getIcon(Class.forName(testElement.getPropertyAsString(TestElement.GUI_CLASS)),
                         enabled);
         } catch (ClassNotFoundException e) {
             log.warn("Can't get icon for class " + testElement, e);
             return null;
         }
     }
 
     public Collection<String> getMenuCategories() {
         try {
             return GuiPackage.getInstance().getGui(getTestElement()).getMenuCategories();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public JPopupMenu createPopupMenu() {
         try {
             return GuiPackage.getInstance().getGui(getTestElement()).createPopupMenu();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public TestElement getTestElement() {
         return (TestElement) getUserObject();
     }
 
     public String getStaticLabel() {
         return GuiPackage.getInstance().getGui((TestElement) getUserObject()).getStaticLabel();
     }
 
     public String getDocAnchor() {
         return GuiPackage.getInstance().getGui((TestElement) getUserObject()).getDocAnchor();
     }
 
     /** {@inheritDoc} */
     @Override
     public void setName(String name) {
         ((TestElement) getUserObject()).setName(name);
     }
 
     /** {@inheritDoc} */
     @Override
     public String getName() {
         return ((TestElement) getUserObject()).getName();
     }
 
     /** {@inheritDoc} */
     @Override
     public void nameChanged() {
         if (treeModel != null) { // may be null during startup
             treeModel.nodeChanged(this);
         }
     }
 
     // Override in order to provide type safety
     @Override
     @SuppressWarnings("unchecked")
     public Enumeration<JMeterTreeNode> children() {
         return super.children();
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java b/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java
index 0539529d1..9418bc5b4 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java
@@ -1,311 +1,313 @@
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
 
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.FocusEvent;
 import java.awt.event.FocusListener;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditorSupport;
 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 
 import javax.swing.CellEditor;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.event.TableModelEvent;
 import javax.swing.event.TableModelListener;
 
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.log.Logger;
 
 /**
  * Table editor for TestBean GUI properties.
  * Currently only works for:
- * - property type Collection<String>, where there is a single header entry
+ * <ul>
+ * <li>property type Collection of {@link String}s, where there is a single header entry</li>
+ * </ul>
  */
 public class TableEditor extends PropertyEditorSupport implements FocusListener,TestBeanPropertyEditor,TableModelListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** 
      * attribute name for class name of a table row;
      * value must be java.lang.String, or a class which supports set and get/is methods for the property name.
      */
     public static final String CLASSNAME = "tableObject.classname"; // $NON-NLS-1$
 
     /** 
      * attribute name for table headers, value must be a String array.
      * If {@link #CLASSNAME} is java.lang.String, there must be only a single entry.
      */
     public static final String HEADERS = "table.headers"; // $NON-NLS-1$
 
     /** attribute name for property names within the {@link #CLASSNAME}, value must be String array */
     public static final String OBJECT_PROPERTIES = "tableObject.properties"; // $NON-NLS-1$
 
     private JTable table;
     private ObjectTableModel model;
     private Class<?> clazz;
     private PropertyDescriptor descriptor;
     private final JButton addButton,removeButton,clearButton;
 
     public TableEditor() {
         addButton = new JButton(JMeterUtils.getResString("add")); // $NON-NLS-1$
         addButton.addActionListener(new AddListener());
         removeButton = new JButton(JMeterUtils.getResString("remove")); // $NON-NLS-1$
         removeButton.addActionListener(new RemoveListener());
         clearButton = new JButton(JMeterUtils.getResString("clear")); // $NON-NLS-1$
         clearButton.addActionListener(new ClearListener());
     }
 
     @Override
     public String getAsText() {
         return null;
     }
 
     @Override
     public Component getCustomEditor() {
         JComponent pane = makePanel();
         pane.doLayout();
         pane.validate();
         return pane;
     }
 
     private JComponent makePanel()
     {
         JPanel p = new JPanel(new BorderLayout());
         JScrollPane scroller = new JScrollPane(table);
         scroller.setPreferredSize(scroller.getMinimumSize());
         p.add(scroller,BorderLayout.CENTER);
         JPanel south = new JPanel();
         south.add(addButton);
         south.add(removeButton);
         south.add(clearButton);
         p.add(south,BorderLayout.SOUTH);
         return p;
     }
 
     @Override
     public Object getValue() {
         return model.getObjectList();
     }
 
     @Override
     public void setAsText(String text) throws IllegalArgumentException {
         //not interested in this method.
     }
 
     @Override
     public void setValue(Object value) {
         if(value != null)
         {
             model.setRows(convertCollection((Collection<?>)value));
         }
         else model.clearData();
         this.firePropertyChange();
     }
 
     private Collection<Object> convertCollection(Collection<?> values)
     {
         List<Object> l = new LinkedList<Object>();
         for(Object obj : values)
         {
             if(obj instanceof TestElementProperty)
             {
                 l.add(((TestElementProperty)obj).getElement());
             }
             else
             {
                 l.add(obj);
             }
         }
         return l;
     }
 
     @Override
     public boolean supportsCustomEditor() {
         return true;
     }
 
     /**
      * For the table editor, the CLASSNAME attribute must simply be the name of the class of object it will hold
      * where each row holds one object.
      */
     @Override
     public void setDescriptor(PropertyDescriptor descriptor) {
         this.descriptor = descriptor;
         String value = (String)descriptor.getValue(CLASSNAME);
         if (value == null) {
             throw new RuntimeException("The Table Editor requires the CLASSNAME atttribute be set - the name of the object to represent a row");
         }
         try {
             clazz = Class.forName(value);
             initializeModel();
         } catch (ClassNotFoundException e) {
             throw new RuntimeException("Could not find the CLASSNAME class "+ value, e);
         }
     }
 
     void initializeModel()
     {
         Object hdrs = descriptor.getValue(HEADERS);
         if (!(hdrs instanceof String[])){
             throw new RuntimeException("attribute HEADERS must be a String array");            
         }
         if(clazz == String.class)
         {
             model = new ObjectTableModel((String[])hdrs,new Functor[0],new Functor[0],new Class[]{String.class});
         }
         else
         {
             Object value = descriptor.getValue(OBJECT_PROPERTIES);
             if (!(value instanceof String[])) {
                 throw new RuntimeException("attribute OBJECT_PROPERTIES must be a String array");
             }
             String[] props = (String[])value;
             Functor[] writers = new Functor[props.length];
             Functor[] readers = new Functor[props.length];
             Class<?>[] editors = new Class[props.length];
             int count = 0;
             for(String propName : props)
             {
                 propName = propName.substring(0,1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
                 writers[count] = createWriter(clazz,propName);
                 readers[count] = createReader(clazz,propName);
                 editors[count] = getArgForWriter(clazz,propName);
                 count++;
             }
             model = new ObjectTableModel((String[])hdrs,readers,writers,editors);
         }
         model.addTableModelListener(this);
         table = new JTable(model);
         table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         table.addFocusListener(this);
     }
 
     Functor createWriter(Class<?> c,String propName)
     {
         String setter = "set" + propName; // $NON-NLS-1$
         return new Functor(setter);
     }
 
     Functor createReader(Class<?> c,String propName)
     {
         String getter = "get" + propName; // $NON-NLS-1$
         try
         {
             c.getMethod(getter,new Class[0]);
             return new Functor(getter);
         }
         catch(Exception e) { return new Functor("is" + propName); }
     }
 
     Class<?> getArgForWriter(Class<?> c,String propName)
     {
         String setter = "set" + propName; // $NON-NLS-1$
         for(Method m : c.getMethods())
         {
             if(m.getName().equals(setter))
             {
                 return m.getParameterTypes()[0];
             }
         }
         return null;
     }
 
     @Override
     public void tableChanged(TableModelEvent e) {
         this.firePropertyChange();
     }
 
     @Override
     public void focusGained(FocusEvent e) {
 
     }
 
     @Override
     public void focusLost(FocusEvent e) {
         final int editingRow = table.getEditingRow();
         final int editingColumn = table.getEditingColumn();
         CellEditor ce = null;
         if (editingRow != -1 && editingColumn != -1){
             ce = table.getCellEditor(editingRow,editingColumn);
         }
         Component editor = table.getEditorComponent();
         if(ce != null && (editor == null || editor != e.getOppositeComponent()))
         {
             ce.stopCellEditing();
         }
         else if(editor != null)
         {
             editor.addFocusListener(this);
         }
         this.firePropertyChange();
     }
 
     private class AddListener implements ActionListener
     {
         @Override
         public void actionPerformed(ActionEvent e)
         {
             try
             {
                 model.addRow(clazz.newInstance());
             }catch(Exception err)
             {
                 log.error("The class type given to TableEditor was not instantiable. ",err);
             }
         }
     }
 
     private class RemoveListener implements ActionListener
     {
         @Override
         public void actionPerformed(ActionEvent e)
         {
             int row = table.getSelectedRow();
             if (row >= 0) {
                 model.removeRow(row);
             }
         }
     }
 
     private class ClearListener implements ActionListener
     {
         @Override
         public void actionPerformed(ActionEvent e)
         {
             model.clearData();
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
index ed0e5952f..403312e4f 100644
--- a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
+++ b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
@@ -1,658 +1,658 @@
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
 
 package org.apache.jmeter.testelement;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.testelement.property.MapProperty;
 import org.apache.jmeter.testelement.property.MultiProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.PropertyIteratorImpl;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  */
 public abstract class AbstractTestElement implements TestElement, Serializable, Searchable {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final Map<String, JMeterProperty> propMap =
         Collections.synchronizedMap(new LinkedHashMap<String, JMeterProperty>());
 
     /**
      * Holds properties added when isRunningVersion is true
      */
     private transient Set<JMeterProperty> temporaryProperties;
 
     private transient boolean runningVersion = false;
 
     // Thread-specific variables saved here to save recalculation
     private transient JMeterContext threadContext = null;
 
     private transient String threadName = null;
 
     @Override
     public Object clone() {
         try {
             TestElement clonedElement = this.getClass().newInstance();
 
             PropertyIterator iter = propertyIterator();
             while (iter.hasNext()) {
                 clonedElement.setProperty(iter.next().clone());
             }
             clonedElement.setRunningVersion(runningVersion);
             return clonedElement;
         } catch (InstantiationException e) {
             throw new AssertionError(e); // clone should never return null
         } catch (IllegalAccessException e) {
             throw new AssertionError(e); // clone should never return null
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void clear() {
         propMap.clear();
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Default implementation - does nothing
      */
     @Override
     public void clearTestElementChildren(){
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void removeProperty(String key) {
         propMap.remove(key);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean equals(Object o) {
         if (o instanceof AbstractTestElement) {
             return ((AbstractTestElement) o).propMap.equals(propMap);
         } else {
             return false;
         }
     }
 
     // TODO temporary hack to avoid unnecessary bug reports for subclasses
 
     /**
      * {@inheritDoc}
      */
     @Override
     public int hashCode(){
         return System.identityHashCode(this);
     }
 
     /*
      * URGENT: TODO - sort out equals and hashCode() - at present equal
      * instances can/will have different hashcodes - problem is, when a proper
      * hashcode is used, tests stop working, e.g. listener data disappears when
      * switching views... This presumably means that instances currently
      * regarded as equal, aren't really equal.
      *
      * @see java.lang.Object#hashCode()
      */
     // This would be sensible, but does not work:
     // public int hashCode()
     // {
     // return propMap.hashCode();
     // }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void addTestElement(TestElement el) {
         mergeIn(el);
     }
 
     @Override
     public void setName(String name) {
         setProperty(TestElement.NAME, name);
     }
 
     @Override
     public String getName() {
         return getPropertyAsString(TestElement.NAME);
     }
 
     @Override
     public void setComment(String comment){
         setProperty(new StringProperty(TestElement.COMMENTS, comment));
     }
 
     @Override
     public String getComment(){
         return getProperty(TestElement.COMMENTS).getStringValue();
     }
 
     /**
      * Get the named property. If it doesn't exist, a new NullProperty object is
      * created with the same name and returned.
      */
     @Override
     public JMeterProperty getProperty(String key) {
         JMeterProperty prop = propMap.get(key);
         if (prop == null) {
             prop = new NullProperty(key);
         }
         return prop;
     }
 
     @Override
     public void traverse(TestElementTraverser traverser) {
         PropertyIterator iter = propertyIterator();
         traverser.startTestElement(this);
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
         traverser.endTestElement(this);
     }
 
     protected void traverseProperty(TestElementTraverser traverser, JMeterProperty value) {
         traverser.startProperty(value);
         if (value instanceof TestElementProperty) {
             ((TestElement) value.getObjectValue()).traverse(traverser);
         } else if (value instanceof CollectionProperty) {
             traverseCollection((CollectionProperty) value, traverser);
         } else if (value instanceof MapProperty) {
             traverseMap((MapProperty) value, traverser);
         }
         traverser.endProperty(value);
     }
 
     protected void traverseMap(MapProperty map, TestElementTraverser traverser) {
         PropertyIterator iter = map.valueIterator();
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
     }
 
     protected void traverseCollection(CollectionProperty col, TestElementTraverser traverser) {
         PropertyIterator iter = col.iterator();
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
     }
 
     @Override
     public int getPropertyAsInt(String key) {
         return getProperty(key).getIntValue();
     }
 
     @Override
     public int getPropertyAsInt(String key, int defaultValue) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultValue : jmp.getIntValue();
     }
 
     @Override
     public boolean getPropertyAsBoolean(String key) {
         return getProperty(key).getBooleanValue();
     }
 
     @Override
     public boolean getPropertyAsBoolean(String key, boolean defaultVal) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultVal : jmp.getBooleanValue();
     }
 
     @Override
     public float getPropertyAsFloat(String key) {
         return getProperty(key).getFloatValue();
     }
 
     @Override
     public long getPropertyAsLong(String key) {
         return getProperty(key).getLongValue();
     }
 
     @Override
     public long getPropertyAsLong(String key, long defaultValue) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultValue : jmp.getLongValue();
     }
 
     @Override
     public double getPropertyAsDouble(String key) {
         return getProperty(key).getDoubleValue();
     }
 
     @Override
     public String getPropertyAsString(String key) {
         return getProperty(key).getStringValue();
     }
 
     @Override
     public String getPropertyAsString(String key, String defaultValue) {
         JMeterProperty jmp = getProperty(key);
         return jmp instanceof NullProperty ? defaultValue : jmp.getStringValue();
     }
 
     /**
      * Add property to test element
      * @param property {@link JMeterProperty} to add to current Test Element
      * @param clone clone property
      */
     protected void addProperty(JMeterProperty property, boolean clone) {
         JMeterProperty propertyToPut = property;
         if(clone) {
             propertyToPut = property.clone();
         }
         if (isRunningVersion()) {
             setTemporary(propertyToPut);
         } else {
             clearTemporary(property);
         }
         JMeterProperty prop = getProperty(property.getName());
 
         if (prop instanceof NullProperty || (prop instanceof StringProperty && prop.getStringValue().equals(""))) {
             propMap.put(property.getName(), propertyToPut);
         } else {
             prop.mergeIn(propertyToPut);
         }
     }
 
     /**
      * Add property to test element without cloning it
      * @param property {@link JMeterProperty}
      */
     protected void addProperty(JMeterProperty property) {
         addProperty(property, false);
     }
 
     /**
      * Remove property from temporaryProperties
      * @param property {@link JMeterProperty}
      */
     protected void clearTemporary(JMeterProperty property) {
         if (temporaryProperties != null) {
             temporaryProperties.remove(property);
         }
     }
 
     /**
      * Log the properties of the test element
      *
      * @see TestElement#setProperty(JMeterProperty)
      */
     protected void logProperties() {
         if (log.isDebugEnabled()) {
             PropertyIterator iter = propertyIterator();
             while (iter.hasNext()) {
                 JMeterProperty prop = iter.next();
                 log.debug("Property " + prop.getName() + " is temp? " + isTemporary(prop) + " and is a "
                         + prop.getObjectValue());
             }
         }
     }
 
     @Override
     public void setProperty(JMeterProperty property) {
         if (isRunningVersion()) {
             if (getProperty(property.getName()) instanceof NullProperty) {
                 addProperty(property);
             } else {
                 getProperty(property.getName()).setObjectValue(property.getObjectValue());
             }
         } else {
             propMap.put(property.getName(), property);
         }
     }
 
     @Override
     public void setProperty(String name, String value) {
         setProperty(new StringProperty(name, value));
     }
 
     /**
      * Create a String property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, String value, String dflt) {
         if (dflt.equals(value)) {
             removeProperty(name);
         } else {
             setProperty(new StringProperty(name, value));
         }
     }
 
     @Override
     public void setProperty(String name, boolean value) {
         setProperty(new BooleanProperty(name, value));
     }
 
     /**
      * Create a boolean property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, boolean value, boolean dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new BooleanProperty(name, value));
         }
     }
 
     @Override
     public void setProperty(String name, int value) {
         setProperty(new IntegerProperty(name, value));
     }
 
     /**
      * Create an int property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, int value, int dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new IntegerProperty(name, value));
         }
     }
     
     @Override
     public void setProperty(String name, long value) {
         setProperty(new LongProperty(name, value));
     }
     
     /**
      * Create a long property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, long value, long dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new LongProperty(name, value));
         }
     }
 
     @Override
     public PropertyIterator propertyIterator() {
         return new PropertyIteratorImpl(propMap.values());
     }
 
     /**
      * Add to this the properties of element (by reference)
      * @param element {@link TestElement}
      */
     protected void mergeIn(TestElement element) {
         PropertyIterator iter = element.propertyIterator();
         while (iter.hasNext()) {
             JMeterProperty prop = iter.next();
             addProperty(prop, false);
         }
     }
 
     /**
      * Returns the runningVersion.
      */
     @Override
     public boolean isRunningVersion() {
         return runningVersion;
     }
 
     /**
      * Sets the runningVersion.
      *
      * @param runningVersion
      *            the runningVersion to set
      */
     @Override
     public void setRunningVersion(boolean runningVersion) {
         this.runningVersion = runningVersion;
         PropertyIterator iter = propertyIterator();
         while (iter.hasNext()) {
             iter.next().setRunningVersion(runningVersion);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void recoverRunningVersion() {
         Iterator<Map.Entry<String, JMeterProperty>>  iter = propMap.entrySet().iterator();
         while (iter.hasNext()) {
             Map.Entry<String, JMeterProperty> entry = iter.next();
             JMeterProperty prop = entry.getValue();
             if (isTemporary(prop)) {
                 iter.remove();
                 clearTemporary(prop);
             } else {
                 prop.recoverRunningVersion(this);
             }
         }
         emptyTemporary();
     }
 
     /**
      * Clears temporaryProperties
      */
     protected void emptyTemporary() {
         if (temporaryProperties != null) {
             temporaryProperties.clear();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean isTemporary(JMeterProperty property) {
         if (temporaryProperties == null) {
             return false;
         } else {
             return temporaryProperties.contains(property);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setTemporary(JMeterProperty property) {
         if (temporaryProperties == null) {
             temporaryProperties = new LinkedHashSet<JMeterProperty>();
         }
         temporaryProperties.add(property);
         if (property instanceof MultiProperty) {
             PropertyIterator iter = ((MultiProperty) property).iterator();
             while (iter.hasNext()) {
                 setTemporary(iter.next());
             }
         }
     }
 
     /**
      * @return Returns the threadContext.
      */
     @Override
     public JMeterContext getThreadContext() {
         if (threadContext == null) {
             /*
              * Only samplers have the thread context set up by JMeterThread at
              * present, so suppress the warning for now
              */
             // log.warn("ThreadContext was not set up - should only happen in
             // JUnit testing..."
             // ,new Throwable("Debug"));
             threadContext = JMeterContextService.getContext();
         }
         return threadContext;
     }
 
     /**
      * @param inthreadContext
      *            The threadContext to set.
      */
     @Override
     public void setThreadContext(JMeterContext inthreadContext) {
         if (threadContext != null) {
             if (inthreadContext != threadContext) {
                 throw new RuntimeException("Attempting to reset the thread context");
             }
         }
         this.threadContext = inthreadContext;
     }
 
     /**
      * @return Returns the threadName.
      */
     @Override
     public String getThreadName() {
         return threadName;
     }
 
     /**
      * @param inthreadName
      *            The threadName to set.
      */
     @Override
     public void setThreadName(String inthreadName) {
         if (threadName != null) {
             if (!threadName.equals(inthreadName)) {
                 throw new RuntimeException("Attempting to reset the thread name");
             }
         }
         this.threadName = inthreadName;
     }
 
     public AbstractTestElement() {
         super();
     }
 
     /**
      * {@inheritDoc}
      */
     // Default implementation
     @Override
     public boolean canRemove() {
         return true;
     }
 
     // Moved from JMeter class
     @Override
     public boolean isEnabled() {
         return getProperty(TestElement.ENABLED) instanceof NullProperty || getPropertyAsBoolean(TestElement.ENABLED);
     }
 
     @Override
     public void setEnabled(boolean enabled) {
         setProperty(new BooleanProperty(TestElement.ENABLED, enabled));
     }
 
     /** 
      * {@inheritDoc}}
      */
     @Override
     public List<String> getSearchableTokens() {
         List<String> result = new ArrayList<String>(25);
         PropertyIterator iterator = propertyIterator();
         while(iterator.hasNext()) {
             JMeterProperty jMeterProperty = iterator.next();    
             result.add(jMeterProperty.getName());
             result.add(jMeterProperty.getStringValue());
         }
         return result;
     }
     
     /**
      * Add to result the values of propertyNames
-     * @param result List<String> values of propertyNames
-     * @param propertyNames Set<String> properties to extract
+     * @param result List of values of propertyNames
+     * @param propertyNames Set of names of properties to extract
      */
     protected final void addPropertiesValues(List<String> result, Set<String> propertyNames) {
         PropertyIterator iterator = propertyIterator();
         while(iterator.hasNext()) {
             JMeterProperty jMeterProperty = iterator.next();
             if(propertyNames.contains(jMeterProperty.getName())) {
                 result.add(jMeterProperty.getStringValue());
             }
         }
     } 
 }
diff --git a/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java b/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java
index 14d2e2c22..0cec79ec4 100644
--- a/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java
+++ b/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java
@@ -1,103 +1,103 @@
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
 
 package org.apache.jmeter.threads;
 
 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HashTreeTraverser implementation that stores in a Stack all 
  * the Test Elements on the path to a particular node.
  */
 public class FindTestElementsUpToRootTraverser implements HashTreeTraverser {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final LinkedList<TestElement> stack = new LinkedList<TestElement>();
 
     /**
      * Node to find in TestTree
      */
     private final Object nodeToFind;
     /**
      * Once we find the node in the Tree we stop recording nodes
      */
     private boolean stopRecording = false;
 
     /**
      * @param nodeToFind Node to find
      */
     public FindTestElementsUpToRootTraverser(Object nodeToFind) {
         this.nodeToFind = nodeToFind;
     }
 
     /** {@inheritDoc} */
     @Override
     public void addNode(Object node, HashTree subTree) {
         if(stopRecording) {
             return;
         }
         if(node == nodeToFind) {
             this.stopRecording = true;
         }
         stack.addLast((TestElement) node);        
     }
 
     /** {@inheritDoc} */
     @Override
     public void subtractNode() {
         if(stopRecording) {
             return;
         }
         if(log.isDebugEnabled()) {
             log.debug("Subtracting node, stack size = " + stack.size());
         }
         stack.removeLast();        
     }
 
     /** {@inheritDoc} */
     @Override
     public void processPath() {
         //NOOP
     }
 
     /**
      * Returns all controllers that where in Tree down to nodeToFind in reverse order (from leaf to root)
-     * @return List<Controller>
+     * @return List of {@link Controller}
      */
     public List<Controller> getControllersToRoot() {
         List<Controller> result = new ArrayList<Controller>(stack.size());
         LinkedList<TestElement> stackLocalCopy = new LinkedList<TestElement>(stack);
         while(stackLocalCopy.size()>0) {
             TestElement te = stackLocalCopy.getLast();
             if(te instanceof Controller) {
                 result.add((Controller)te);
             }
             stackLocalCopy.removeLast();
         }
         return result;
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/SamplePackage.java b/src/core/org/apache/jmeter/threads/SamplePackage.java
index 8839e4347..c8f71a928 100644
--- a/src/core/org/apache/jmeter/threads/SamplePackage.java
+++ b/src/core/org/apache/jmeter/threads/SamplePackage.java
@@ -1,230 +1,230 @@
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
 
 package org.apache.jmeter.threads;
 
 import java.util.List;
 
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.timers.Timer;
 
 /**
  * Packages methods related to sample handling.<br>
  * A SamplePackage contains all elements associated to a Sampler:
  * <ul>
  *  <li>SampleListener(s)</li>
  *  <li>Timer(s)</li>
  *  <li>Assertion(s)</li>
  *  <li>PreProcessor(s)</li>
  *  <li>PostProcessor(s)</li>
  *  <li>ConfigTestElement(s)</li>
  *  <li>Controller(s)</li>
  * </ul>
  */
 public class SamplePackage {
 
     private final List<SampleListener> sampleListeners;
 
     private final List<Timer> timers;
 
     private final List<Assertion> assertions;
 
     private final List<PostProcessor> postProcessors;
 
     private final List<PreProcessor> preProcessors;
 
     private final List<ConfigTestElement> configs;
 
     private final List<Controller> controllers;
 
     private Sampler sampler;
 
     public SamplePackage(
             List<ConfigTestElement> configs,
             List<SampleListener> listeners,
             List<Timer> timers,
             List<Assertion> assertions, 
             List<PostProcessor> postProcessors, 
             List<PreProcessor> preProcessors,
             List<Controller> controllers) {
         this.configs = configs;
         this.sampleListeners = listeners;
         this.timers = timers;
         this.assertions = assertions;
         this.postProcessors = postProcessors;
         this.preProcessors = preProcessors;
         this.controllers = controllers;
     }
 
     /**
      * Make the SamplePackage the running version, or make it no longer the
      * running version. This tells to each element of the SamplePackage that it's current state must
      * be retrievable by a call to recoverRunningVersion(). 
      * @param running boolean
      * @see TestElement#setRunningVersion(boolean)
      */
     public void setRunningVersion(boolean running) {
         setRunningVersion(configs, running);
         setRunningVersion(sampleListeners, running);
         setRunningVersion(assertions, running);
         setRunningVersion(timers, running);
         setRunningVersion(postProcessors, running);
         setRunningVersion(preProcessors, running);
         setRunningVersion(controllers, running);
         sampler.setRunningVersion(running);
     }
 
     private void setRunningVersion(List<?> list, boolean running) {
         @SuppressWarnings("unchecked") // all implementations extend TestElement
         List<TestElement> telist = (List<TestElement>)list;
         for (TestElement te : telist) {
             te.setRunningVersion(running);
         }
     }
 
     private void recoverRunningVersion(List<?> list) {
         @SuppressWarnings("unchecked") // All implementations extend TestElement
         List<TestElement> telist = (List<TestElement>)list;
         for (TestElement te : telist) {
             te.recoverRunningVersion();
         }
     }
 
     /**
      * Recover each member of SamplePackage to the state before the call of setRunningVersion(true)
      * @see TestElement#recoverRunningVersion()
      */
     public void recoverRunningVersion() {
         recoverRunningVersion(configs);
         recoverRunningVersion(sampleListeners);
         recoverRunningVersion(assertions);
         recoverRunningVersion(timers);
         recoverRunningVersion(postProcessors);
         recoverRunningVersion(preProcessors);
         recoverRunningVersion(controllers);
         sampler.recoverRunningVersion();
     }
 
     /**
-     * @return List<SampleListener>
+     * @return List of {@link SampleListener}s
      */
     public List<SampleListener> getSampleListeners() {
         return sampleListeners;
     }
 
     /**
      * Add Sample Listener
      * @param listener {@link SampleListener}
      */
     public void addSampleListener(SampleListener listener) {
         sampleListeners.add(listener);
     }
 
     /**
-     * @return List<Timer>
+     * @return List of {@link Timer}s
      */
     public List<Timer> getTimers() {
         return timers;
     }
 
     
     /**
      * Add Post processor
      * @param ex {@link PostProcessor}
      */
     public void addPostProcessor(PostProcessor ex) {
         postProcessors.add(ex);
     }
 
     /**
      * Add Pre processor
      * @param pre {@link PreProcessor}
      */
     public void addPreProcessor(PreProcessor pre) {
         preProcessors.add(pre);
     }
 
     /**
      * Add Timer
      * @param timer {@link Timer}
      */
     public void addTimer(Timer timer) {
         timers.add(timer);
     }
 
     /**
      * Add Assertion
      * @param asser {@link Assertion}
      */
     public void addAssertion(Assertion asser) {
         assertions.add(asser);
     }
 
     /**
-     * @return List<Assertion>
+     * @return List of {@link Assertion}
      */
     public List<Assertion> getAssertions() {
         return assertions;
     }
 
     /**
-     * @return List<PostProcessor>
+     * @return List of {@link PostProcessor}s
      */
     public List<PostProcessor> getPostProcessors() {
         return postProcessors;
     }
 
     /**
      * @return {@link Sampler}
      */
     public Sampler getSampler() {
         return sampler;
     }
 
     /**
      * @param s {@link Sampler}
      */
     public void setSampler(Sampler s) {
         sampler = s;
     }
 
     /**
      * Returns the preProcessors.
-     * @return List<PreProcessor>
+     * @return List of {@link PreProcessor}
      */
     public List<PreProcessor> getPreProcessors() {
         return preProcessors;
     }
 
     /**
      * Returns the configs.
      *
-     * @return List
+     * @return List of {@link ConfigTestElement}
      */
     public List<ConfigTestElement> getConfigs() {
         return configs;
     }
 
 }
diff --git a/src/core/org/apache/jmeter/util/XPathUtil.java b/src/core/org/apache/jmeter/util/XPathUtil.java
index e8cb988ff..ea26c7bd1 100644
--- a/src/core/org/apache/jmeter/util/XPathUtil.java
+++ b/src/core/org/apache/jmeter/util/XPathUtil.java
@@ -1,451 +1,451 @@
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
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintWriter;
 import java.io.StringReader;
 import java.io.StringWriter;
 import java.util.List;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.OutputKeys;
 import javax.xml.transform.Source;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.sax.SAXSource;
 import javax.xml.transform.stream.StreamResult;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.xml.utils.PrefixResolver;
 import org.apache.xpath.XPathAPI;
 import org.apache.xpath.objects.XObject;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 /**
  * This class provides a few utility methods for dealing with XML/XPath.
  */
 public class XPathUtil {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private XPathUtil() {
         super();
     }
 
     //@GuardedBy("this")
     private static DocumentBuilderFactory documentBuilderFactory;
 
     /**
      * Returns a suitable document builder factory.
      * Caches the factory in case the next caller wants the same options.
      *
      * @param validate should the parser validate documents?
      * @param whitespace should the parser eliminate whitespace in element content?
      * @param namespace should the parser be namespace aware?
      *
      * @return javax.xml.parsers.DocumentBuilderFactory
      */
     private static synchronized DocumentBuilderFactory makeDocumentBuilderFactory(boolean validate, boolean whitespace,
             boolean namespace) {
         if (XPathUtil.documentBuilderFactory == null || documentBuilderFactory.isValidating() != validate
                 || documentBuilderFactory.isNamespaceAware() != namespace
                 || documentBuilderFactory.isIgnoringElementContentWhitespace() != whitespace) {
             // configure the document builder factory
             documentBuilderFactory = DocumentBuilderFactory.newInstance();
             documentBuilderFactory.setValidating(validate);
             documentBuilderFactory.setNamespaceAware(namespace);
             documentBuilderFactory.setIgnoringElementContentWhitespace(whitespace);
         }
         return XPathUtil.documentBuilderFactory;
     }
 
     /**
      * Create a DocumentBuilder using the makeDocumentFactory func.
      *
      * @param validate should the parser validate documents?
      * @param whitespace should the parser eliminate whitespace in element content?
      * @param namespace should the parser be namespace aware?
      * @param downloadDTDs if true, parser should attempt to resolve external entities
      * @return document builder
      * @throws ParserConfigurationException
      */
     public static DocumentBuilder makeDocumentBuilder(boolean validate, boolean whitespace, boolean namespace, boolean downloadDTDs)
             throws ParserConfigurationException {
         DocumentBuilder builder = makeDocumentBuilderFactory(validate, whitespace, namespace).newDocumentBuilder();
         builder.setErrorHandler(new MyErrorHandler(validate, false));
         if (!downloadDTDs){
             EntityResolver er = new EntityResolver(){
                 @Override
                 public InputSource resolveEntity(String publicId, String systemId)
                         throws SAXException, IOException {
                     return new InputSource(new ByteArrayInputStream(new byte[]{}));
                 }
             };
             builder.setEntityResolver(er);
         }
         return builder;
     }
 
     /**
      * Utility function to get new Document
      *
      * @param stream - Document Input stream
      * @param validate - Validate Document (not Tidy)
      * @param whitespace - Element Whitespace (not Tidy)
      * @param namespace - Is Namespace aware. (not Tidy)
      * @param tolerant - Is tolerant - i.e. use the Tidy parser
      * @param quiet - set Tidy quiet
      * @param showWarnings - set Tidy warnings
      * @param report_errors - throw TidyException if Tidy detects an error
      * @param isXml - is document already XML (Tidy only)
      * @param downloadDTDs - if true, try to download external DTDs
      * @return document
      * @throws ParserConfigurationException
      * @throws SAXException
      * @throws IOException
      * @throws TidyException
      */
     public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
             boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors, boolean isXml, boolean downloadDTDs)
             throws ParserConfigurationException, SAXException, IOException, TidyException {
         return makeDocument(stream, validate, whitespace, namespace,
                 tolerant, quiet, showWarnings, report_errors, isXml, downloadDTDs, null);
     }
 
     /**
      * Utility function to get new Document
      *
      * @param stream - Document Input stream
      * @param validate - Validate Document (not Tidy)
      * @param whitespace - Element Whitespace (not Tidy)
      * @param namespace - Is Namespace aware. (not Tidy)
      * @param tolerant - Is tolerant - i.e. use the Tidy parser
      * @param quiet - set Tidy quiet
      * @param showWarnings - set Tidy warnings
      * @param report_errors - throw TidyException if Tidy detects an error
      * @param isXml - is document already XML (Tidy only)
      * @param downloadDTDs - if true, try to download external DTDs
      * @param tidyOut OutputStream for Tidy pretty-printing
      * @return document
      * @throws ParserConfigurationException
      * @throws SAXException
      * @throws IOException
      * @throws TidyException
      */
     public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
             boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors, boolean isXml, boolean downloadDTDs, 
             OutputStream tidyOut)
             throws ParserConfigurationException, SAXException, IOException, TidyException {
         Document doc;
         if (tolerant) {
             doc = tidyDoc(stream, quiet, showWarnings, report_errors, isXml, tidyOut);
         } else {
             doc = makeDocumentBuilder(validate, whitespace, namespace, downloadDTDs).parse(stream);
         }
         return doc;
     }
 
     /**
      * Create a document using Tidy
      *
      * @param stream - input
      * @param quiet - set Tidy quiet?
      * @param showWarnings - show Tidy warnings?
      * @param report_errors - log errors and throw TidyException?
      * @param isXML - treat document as XML?
      * @param out OutputStream, null if no output required
      * @return the document
      *
      * @throws TidyException if a ParseError is detected and report_errors is true
      */
     private static Document tidyDoc(InputStream stream, boolean quiet, boolean showWarnings, boolean report_errors,
             boolean isXML, OutputStream out) throws TidyException {
         StringWriter sw = new StringWriter();
         Tidy tidy = makeTidyParser(quiet, showWarnings, isXML, sw);
         Document doc = tidy.parseDOM(stream, out);
         doc.normalize();
         if (tidy.getParseErrors() > 0) {
             if (report_errors) {
                 log.error("TidyException: " + sw.toString());
                 throw new TidyException(tidy.getParseErrors(),tidy.getParseWarnings());
             }
             log.warn("Tidy errors: " + sw.toString());
         }
         return doc;
     }
 
     /**
      * Create a Tidy parser with the specified settings.
      *
      * @param quiet - set the Tidy quiet flag?
      * @param showWarnings - show Tidy warnings?
      * @param isXml - treat the content as XML?
      * @param stringWriter - if non-null, use this for Tidy errorOutput
      * @return the Tidy parser
      */
     public static Tidy makeTidyParser(boolean quiet, boolean showWarnings, boolean isXml, StringWriter stringWriter) {
         Tidy tidy = new Tidy();
         tidy.setInputEncoding("UTF8");
         tidy.setOutputEncoding("UTF8");
         tidy.setQuiet(quiet);
         tidy.setShowWarnings(showWarnings);
         tidy.setMakeClean(true);
         tidy.setXmlTags(isXml);
         if (stringWriter != null) {
             tidy.setErrout(new PrintWriter(stringWriter));
         }
         return tidy;
     }
 
     static class MyErrorHandler implements ErrorHandler {
         private final boolean val, tol;
 
         private final String type;
 
         MyErrorHandler(boolean validate, boolean tolerate) {
             val = validate;
             tol = tolerate;
             type = "Val=" + val + " Tol=" + tol;
         }
 
         @Override
         public void warning(SAXParseException ex) throws SAXException {
             log.info("Type=" + type + " " + ex);
             if (val && !tol){
                 throw new SAXException(ex);
             }
         }
 
         @Override
         public void error(SAXParseException ex) throws SAXException {
             log.warn("Type=" + type + " " + ex);
             if (val && !tol) {
                 throw new SAXException(ex);
             }
         }
 
         @Override
         public void fatalError(SAXParseException ex) throws SAXException {
             log.error("Type=" + type + " " + ex);
             if (val && !tol) {
                 throw new SAXException(ex);
             }
         }
     }
     
     /**
      * Return value for node
      * @param node Node
      * @return String
      */
     private static String getValueForNode(Node node) {
         StringWriter sw = new StringWriter();
         try {
             Transformer t = TransformerFactory.newInstance().newTransformer();
             t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
             t.transform(new DOMSource(node), new StreamResult(sw));
         } catch (TransformerException e) {
             sw.write(e.getMessageAndLocation());
         }
         return sw.toString();
     }
 
     /**
      * Extract NodeList using expression
      * @param document {@link Document}
      * @param xPathExpression XPath expression
      * @return {@link NodeList}
      * @throws TransformerException 
      */
     public static NodeList selectNodeList(Document document, String xPathExpression) throws TransformerException {
         XObject xObject = XPathAPI.eval(document, xPathExpression, getPrefixResolver(document));
         return xObject.nodelist();
     }
 
     /**
      * Put in matchStrings results of evaluation
      * @param document XML document
      * @param xPathQuery XPath Query
-     * @param matchStrings List<String> that will be filled
+     * @param matchStrings List of strings that will be filled
      * @param fragment return fragment
      * @throws TransformerException
      */
     public static void putValuesForXPathInList(Document document, 
             String xPathQuery,
             List<String> matchStrings, boolean fragment) throws TransformerException {
         String val = null;
         XObject xObject = XPathAPI.eval(document, xPathQuery, getPrefixResolver(document));
         final int objectType = xObject.getType();
         if (objectType == XObject.CLASS_NODESET) {
             NodeList matches = xObject.nodelist();
             int length = matches.getLength();
             for (int i = 0 ; i < length; i++) {
                 Node match = matches.item(i);
                 if ( match instanceof Element){
                     if (fragment){
                         val = getValueForNode(match);
                     } else {
                         // elements have empty nodeValue, but we are usually interested in their content
                         final Node firstChild = match.getFirstChild();
                         if (firstChild != null) {
                             val = firstChild.getNodeValue();
                         } else {
                             val = match.getNodeValue(); // TODO is this correct?
                         }
                     }
                 } else {
                    val = match.getNodeValue();
                 }
                 matchStrings.add(val);
             }
         } else if (objectType == XObject.CLASS_NULL
                 || objectType == XObject.CLASS_UNKNOWN
                 || objectType == XObject.CLASS_UNRESOLVEDVARIABLE) {
             log.warn("Unexpected object type: "+xObject.getTypeString()+" returned for: "+xPathQuery);
         } else {
             val = xObject.toString();
             matchStrings.add(val);
       }
     }
 
     /**
      * 
      * @param document XML Document
      * @return {@link PrefixResolver}
      */
     private static PrefixResolver getPrefixResolver(Document document) {
         PropertiesBasedPrefixResolver propertiesBasedPrefixResolver =
                 new PropertiesBasedPrefixResolver(document.getDocumentElement());
         return propertiesBasedPrefixResolver;
     }
 
     /**
      * Validate xpathString is a valid XPath expression
      * @param document XML Document
      * @param xpathString XPATH String
      * @throws TransformerException if expression fails to evaluate
      */
     public static void validateXPath(Document document, String xpathString) throws TransformerException {
         if (XPathAPI.eval(document, xpathString, getPrefixResolver(document)) == null) {
             // We really should never get here
             // because eval will throw an exception
             // if xpath is invalid, but whatever, better
             // safe
             throw new IllegalArgumentException("xpath eval of '" + xpathString + "' was null");
         }
     }
 
     /**
      * Fills result
      * @param result {@link AssertionResult}
      * @param doc XML Document
      * @param xPathExpression XPath expression
      * @param isNegated
      */
     public static void computeAssertionResult(AssertionResult result,
             Document doc, 
             String xPathExpression,
             boolean isNegated) {
         try {
             XObject xObject = XPathAPI.eval(doc, xPathExpression, getPrefixResolver(doc));
             switch (xObject.getType()) {
                 case XObject.CLASS_NODESET:
                     NodeList nodeList = xObject.nodelist();
                     if (nodeList == null || nodeList.getLength() == 0) {
                         if (log.isDebugEnabled()) {
                             log.debug(new StringBuilder("nodeList null no match  ").append(xPathExpression).toString());
                         }
                         result.setFailure(!isNegated);
                         result.setFailureMessage("No Nodes Matched " + xPathExpression);
                         return;
                     }
                     if (log.isDebugEnabled()) {
                         log.debug("nodeList length " + nodeList.getLength());
                         if (!isNegated) {
                             for (int i = 0; i < nodeList.getLength(); i++){
                                 log.debug(new StringBuilder("nodeList[").append(i).append("] ").append(nodeList.item(i)).toString());
                             }
                         }
                     }
                     result.setFailure(isNegated);
                     if (isNegated) {
                         result.setFailureMessage("Specified XPath was found... Turn off negate if this is not desired");
                     }
                     return;
                 case XObject.CLASS_BOOLEAN:
                     if (!xObject.bool()){
                         result.setFailure(!isNegated);
                         result.setFailureMessage("No Nodes Matched " + xPathExpression);
                     }
                     return;
                 default:
                     result.setFailure(true);
                     result.setFailureMessage("Cannot understand: " + xPathExpression);
                     return;
             }
         } catch (TransformerException e) {
             result.setError(true);
             result.setFailureMessage(
                     new StringBuilder("TransformerException: ")
                     .append(e.getMessage())
                     .append(" for:")
                     .append(xPathExpression)
                     .toString());
         }
     }
     
     /**
      * Formats XML
      * @param xml
      * @return String formatted XML
      */
     public static final String formatXml(String xml){
         try {
             Transformer serializer= TransformerFactory.newInstance().newTransformer();
             serializer.setOutputProperty(OutputKeys.INDENT, "yes");
             serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
             Source xmlSource=new SAXSource(new InputSource(new StringReader(xml)));
             StringWriter stringWriter = new StringWriter();
             StreamResult res =  new StreamResult(stringWriter);            
             serializer.transform(xmlSource, res);
             return stringWriter.toString();
         } catch (Exception e) {
             return xml;
         }
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
index 5f6e85b8e..f3e8abcb0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
@@ -1,423 +1,424 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.StringReader;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Map;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.parsers.SAXParser;
 import javax.xml.parsers.SAXParserFactory;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.http.config.MultipartUrlConfig;
 import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.sampler.PostWriter;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 import org.xml.sax.XMLReader;
 import org.xml.sax.helpers.DefaultHandler;
 
 /**
  * Default implementation that handles classical HTTP textual + Multipart requests
  */
 public class DefaultSamplerCreator extends AbstractSamplerCreator {
     private static final Logger log = LoggingManager.getLoggerForClass();
  
     /**
      * 
      */
     public DefaultSamplerCreator() {
     }
 
     /**
      * @see org.apache.jmeter.protocol.http.proxy.SamplerCreator#getManagedContentTypes()
      */
     @Override
     public String[] getManagedContentTypes() {
         return new String[0];
     }
 
     /**
      * 
      * @see org.apache.jmeter.protocol.http.proxy.SamplerCreator#createSampler(org.apache.jmeter.protocol.http.proxy.HttpRequestHdr, java.util.Map, java.util.Map)
      */
     @Override
     public HTTPSamplerBase createSampler(HttpRequestHdr request,
             Map<String, String> pageEncodings, Map<String, String> formEncodings) {
         // Instantiate the sampler
         HTTPSamplerBase sampler = HTTPSamplerFactory.newInstance(request.getHttpSamplerName());
 
         sampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
 
         // Defaults
         sampler.setFollowRedirects(false);
         sampler.setUseKeepAlive(true);
 
         if (log.isDebugEnabled()) {
             log.debug("getSampler: sampler path = " + sampler.getPath());
         }
         return sampler;
     }
 
     /**
      * @see org.apache.jmeter.protocol.http.proxy.SamplerCreator#populateSampler(org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase, org.apache.jmeter.protocol.http.proxy.HttpRequestHdr, java.util.Map, java.util.Map)
      */
     @Override
     public final void populateSampler(HTTPSamplerBase sampler,
             HttpRequestHdr request, Map<String, String> pageEncodings,
             Map<String, String> formEncodings) throws Exception{
         computeFromHeader(sampler, request, pageEncodings, formEncodings);
 
         computeFromPostBody(sampler, request);
         if (log.isDebugEnabled()) {
             log.debug("sampler path = " + sampler.getPath());
         }
         Arguments arguments = sampler.getArguments();
         if(arguments.getArgumentCount() == 1 && arguments.getArgument(0).getName().length()==0) {
             sampler.setPostBodyRaw(true);
         }
     }
 
     /**
      * Compute sampler informations from Request Header
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
-     * @param pageEncodings Map<String, String>
-     * @param formEncodings Map<String, String>
+     * @param pageEncodings Map of page encodings
+     * @param formEncodings Map of form encodings
      * @throws Exception
      */
     protected void computeFromHeader(HTTPSamplerBase sampler,
             HttpRequestHdr request, Map<String, String> pageEncodings,
             Map<String, String> formEncodings) throws Exception {
         computeDomain(sampler, request);
         
         computeMethod(sampler, request);
         
         computePort(sampler, request);
         
         computeProtocol(sampler, request);
 
         computeContentEncoding(sampler, request,
                 pageEncodings, formEncodings);
 
         computePath(sampler, request);
         
         computeSamplerName(sampler, request);
     }
 
     /**
      * Compute sampler informations from Request Header
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      * @throws Exception
      */
     protected void computeFromPostBody(HTTPSamplerBase sampler,
             HttpRequestHdr request) throws Exception {
         // If it was a HTTP GET request, then all parameters in the URL
         // has been handled by the sampler.setPath above, so we just need
         // to do parse the rest of the request if it is not a GET request
         if((!HTTPConstants.CONNECT.equals(request.getMethod())) && (!HTTPConstants.GET.equals(request.getMethod()))) {
             // Check if it was a multipart http post request
             final String contentType = request.getContentType();
             MultipartUrlConfig urlConfig = request.getMultipartConfig(contentType);
             String contentEncoding = sampler.getContentEncoding();
             // Get the post data using the content encoding of the request
             String postData = null;
             if (log.isDebugEnabled()) {
                 if(!StringUtils.isEmpty(contentEncoding)) {
                     log.debug("Using encoding " + contentEncoding + " for request body");
                 }
                 else {
                     log.debug("No encoding found, using JRE default encoding for request body");
                 }
             }
             
             
             if (!StringUtils.isEmpty(contentEncoding)) {
                 postData = new String(request.getRawPostData(), contentEncoding);
             } else {
                 // Use default encoding
                 postData = new String(request.getRawPostData(), PostWriter.ENCODING);
             }
             
             if (urlConfig != null) {
                 urlConfig.parseArguments(postData);
                 // Tell the sampler to do a multipart post
                 sampler.setDoMultipartPost(true);
                 // Remove the header for content-type and content-length, since
                 // those values will most likely be incorrect when the sampler
                 // performs the multipart request, because the boundary string
                 // will change
                 request.getHeaderManager().removeHeaderNamed(HttpRequestHdr.CONTENT_TYPE);
                 request.getHeaderManager().removeHeaderNamed(HttpRequestHdr.CONTENT_LENGTH);
 
                 // Set the form data
                 sampler.setArguments(urlConfig.getArguments());
                 // Set the file uploads
                 sampler.setHTTPFiles(urlConfig.getHTTPFileArgs().asArray());
                 sampler.setDoBrowserCompatibleMultipart(true); // we are parsing browser input here
             // used when postData is pure xml (eg. an xml-rpc call) or for PUT
             } else if (postData.trim().startsWith("<?") 
                     || HTTPConstants.PUT.equals(sampler.getMethod())
                     || isPotentialXml(postData)) {
                 sampler.addNonEncodedArgument("", postData, "");
             } else if (contentType == null || 
                     (contentType.startsWith(HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED) && 
                             !isBinaryContent(contentType))) {
                 // It is the most common post request, with parameter name and values
                 // We also assume this if no content type is present, to be most backwards compatible,
                 // but maybe we should only parse arguments if the content type is as expected
                 sampler.parseArguments(postData.trim(), contentEncoding); //standard name=value postData
             } else if (postData.length() > 0) {
                 if (isBinaryContent(contentType)) {
                     try {
                         File tempDir = new File(getBinaryDirectory());
                         File out = File.createTempFile(request.getMethod(), getBinaryFileSuffix(), tempDir);
                         FileUtils.writeByteArrayToFile(out,request.getRawPostData());
                         HTTPFileArg [] files = {new HTTPFileArg(out.getPath(),"",contentType)};
                         sampler.setHTTPFiles(files);
                     } catch (IOException e) {
                         log.warn("Could not create binary file: "+e);
                     }
                 } else {
                     // Just put the whole postbody as the value of a parameter
                     sampler.addNonEncodedArgument("", postData, ""); //used when postData is pure xml (ex. an xml-rpc call)
                 }
             }
         }
     }
 
     /**
      * Tries parsing to see if content is xml
      * @param postData String
      * @return boolean
      */
     private static final boolean isPotentialXml(String postData) {
         try {
             SAXParserFactory spf = SAXParserFactory.newInstance();
             SAXParser saxParser = spf.newSAXParser();
             XMLReader xmlReader = saxParser.getXMLReader();
             ErrorDetectionHandler detectionHandler =
                     new ErrorDetectionHandler();
             xmlReader.setContentHandler(detectionHandler);
             xmlReader.setErrorHandler(detectionHandler);
             xmlReader.parse(new InputSource(new StringReader(postData)));
             return !detectionHandler.isErrorDetected();
         } catch (ParserConfigurationException e) {
             return false;
         } catch (SAXException e) {
             return false;
         } catch (IOException e) {
             return false;
         }
     }
     
     private static final class ErrorDetectionHandler extends DefaultHandler {
         private boolean errorDetected = false;
         public ErrorDetectionHandler() {
             super();
         }
         /* (non-Javadoc)
          * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
          */
         @Override
         public void error(SAXParseException e) throws SAXException {
             this.errorDetected = true;
         }
 
         /* (non-Javadoc)
          * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
          */
         @Override
         public void fatalError(SAXParseException e) throws SAXException {
             this.errorDetected = true;
         }
         /**
          * @return the errorDetected
          */
         public boolean isErrorDetected() {
             return errorDetected;
         }
     }
     /**
      * Compute sampler name
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeSamplerName(HTTPSamplerBase sampler,
             HttpRequestHdr request) {
         if (!HTTPConstants.CONNECT.equals(request.getMethod()) && isNumberRequests()) {
             incrementRequestNumber();
             sampler.setName(getRequestNumber() + " " + sampler.getPath());
         } else {
             sampler.setName(sampler.getPath());
         }
     }
 
     /**
      * Set path on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computePath(HTTPSamplerBase sampler, HttpRequestHdr request) {
         if(sampler.getContentEncoding() != null) {
             sampler.setPath(request.getPath(), sampler.getContentEncoding());
         }
         else {
             // Although the spec says UTF-8 should be used for encoding URL parameters,
             // most browser use ISO-8859-1 for default if encoding is not known.
             // We use null for contentEncoding, then the url parameters will be added
             // with the value in the URL, and the "encode?" flag set to false
             sampler.setPath(request.getPath(), null);
         }
         if (log.isDebugEnabled()) {
             log.debug("Proxy: setting path: " + sampler.getPath());
         }
     }
 
     /**
      * Compute content encoding
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
-     * @param pageEncodings Map<String, String>
-     * @param formEncodings Map<String, String>
-     * @throws MalformedURLException
+     * @param pageEncodings Map of page encodings
+     * @param formEncodings Map of form encodings
+     * @throws MalformedURLException when no {@link URL} could be built from
+     *         <code>sampler</code> and <code>request</code>
      */
     protected void computeContentEncoding(HTTPSamplerBase sampler,
             HttpRequestHdr request, Map<String, String> pageEncodings,
             Map<String, String> formEncodings) throws MalformedURLException {
         URL pageUrl = null;
         if(sampler.isProtocolDefaultPort()) {
             pageUrl = new URL(sampler.getProtocol(), sampler.getDomain(), request.getPath());
         }
         else {
             pageUrl = new URL(sampler.getProtocol(), sampler.getDomain(), 
                     sampler.getPort(), request.getPath());
         }
         String urlWithoutQuery = request.getUrlWithoutQuery(pageUrl);
 
 
         String contentEncoding = computeContentEncoding(request, pageEncodings,
                 formEncodings, urlWithoutQuery);
         
         // Set the content encoding
         if(!StringUtils.isEmpty(contentEncoding)) {
             sampler.setContentEncoding(contentEncoding);
         } 
     }
     
     /**
      * Computes content encoding from request and if not found uses pageEncoding 
      * and formEncoding to see if URL was previously computed with a content type
      * @param request {@link HttpRequestHdr}
-     * @param pageEncodings Map<String, String>
-     * @param formEncodings Map<String, String>
+     * @param pageEncodings Map of page encodings
+     * @param formEncodings Map of form encodings
      * @return String content encoding
      */
     protected String computeContentEncoding(HttpRequestHdr request,
             Map<String, String> pageEncodings,
             Map<String, String> formEncodings, String urlWithoutQuery) {
         // Check if the request itself tells us what the encoding is
         String contentEncoding = null;
         String requestContentEncoding = ConversionUtils.getEncodingFromContentType(
                 request.getContentType());
         if(requestContentEncoding != null) {
             contentEncoding = requestContentEncoding;
         }
         else {
             // Check if we know the encoding of the page
             if (pageEncodings != null) {
                 synchronized (pageEncodings) {
                     contentEncoding = pageEncodings.get(urlWithoutQuery);
                 }
             }
             // Check if we know the encoding of the form
             if (formEncodings != null) {
                 synchronized (formEncodings) {
                     String formEncoding = formEncodings.get(urlWithoutQuery);
                     // Form encoding has priority over page encoding
                     if (formEncoding != null) {
                         contentEncoding = formEncoding;
                     }
                 }
             }
         }
         return contentEncoding;
     }
 
     /**
      * Set protocol on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeProtocol(HTTPSamplerBase sampler,
             HttpRequestHdr request) {
         sampler.setProtocol(request.getProtocol(sampler));
     }
 
     /**
      * Set Port on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computePort(HTTPSamplerBase sampler, HttpRequestHdr request) {
         sampler.setPort(request.serverPort());
         if (log.isDebugEnabled()) {
             log.debug("Proxy: setting port: " + sampler.getPort());
         }
     }
 
     /**
      * Set method on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeMethod(HTTPSamplerBase sampler, HttpRequestHdr request) {
         sampler.setMethod(request.getMethod());
         log.debug("Proxy: setting method: " + sampler.getMethod());
     }
 
     /**
      * Set domain on sampler
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      */
     protected void computeDomain(HTTPSamplerBase sampler, HttpRequestHdr request) {
         sampler.setDomain(request.serverName());
         if (log.isDebugEnabled()) {
             log.debug("Proxy: setting server: " + sampler.getDomain());
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java
index 71d707203..dfe80e66e 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java
@@ -1,83 +1,84 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.util.Map;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * Factory of sampler
  */
 public interface SamplerCreator {
     
     /**
      * @return String[] array of Content types managed by Factory
      */
     String[] getManagedContentTypes();
 
     /**
      * Create HTTPSamplerBase
      * @param request {@link HttpRequestHdr}
-     * @param pageEncodings Map<String, String>
-     * @param formEncodings Map<String, String>
+     * @param pageEncodings Map of page encodings
+     * @param formEncodings Map of form encodings
      * @return {@link HTTPSamplerBase}
      */
     HTTPSamplerBase createSampler(HttpRequestHdr request,
             Map<String, String> pageEncodings, Map<String, String> formEncodings);
 
     /**
      * Populate sampler from request
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
-     * @param pageEncodings Map<String, String>
-     * @param formEncodings Map<String, String>
+     * @param pageEncodings Map of page encodings
+     * @param formEncodings Map of form encodings
      * @throws Exception
      */
     void populateSampler(HTTPSamplerBase sampler,
             HttpRequestHdr request, Map<String, String> pageEncodings,
             Map<String, String> formEncodings)
                     throws Exception;
 
     /**
      * Post process sampler 
      * Called after sampling 
      * @param sampler HTTPSamplerBase
      * @param result SampleResult
      * @since 2.9
      */
     void postProcessSampler(HTTPSamplerBase sampler, SampleResult result);
 
     /**
      * Default implementation calls:
      * <ol>
      *  <li>{@link SamplerCreator}{@link #createSampler(HttpRequestHdr, Map, Map)}</li>
      *  <li>{@link SamplerCreator}{@link #populateSampler(HTTPSamplerBase, HttpRequestHdr, Map, Map)}</li>
      * </ol>
      * @param request {@link HttpRequestHdr}
-     * @param pageEncodings Map<String, String>
-     * @param formEncodings Map<String, String>
+     * @param pageEncodings Map of page encodings
+     * @param formEncodings Map of form encodings
      * @return {@link HTTPSamplerBase}
+     * @throws Exception
      * @since 2.9
      */
     HTTPSamplerBase createAndPopulateSampler(HttpRequestHdr request,
             Map<String, String> pageEncodings, Map<String, String> formEncodings)
                     throws Exception;
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreatorFactory.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreatorFactory.java
index 7f3f06c02..bea80defc 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreatorFactory.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreatorFactory.java
@@ -1,104 +1,104 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.IOException;
 import java.lang.reflect.Modifier;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.log.Logger;
 
 /**
  * {@link SamplerCreator} factory
  */
 public class SamplerCreatorFactory {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final SamplerCreator DEFAULT_SAMPLER_CREATOR = new DefaultSamplerCreator();
 
     private final Map<String, SamplerCreator> samplerCreatorMap = new HashMap<String, SamplerCreator>();
 
     /**
      * 
      */
     public SamplerCreatorFactory() {
         init();
     }
     
     /**
      * Initialize factory from classpath
      */
     private void init() {
         try {
             List<String> listClasses = ClassFinder.findClassesThatExtend(
                     JMeterUtils.getSearchPaths(), 
                     new Class[] {SamplerCreator.class }); 
             for (String strClassName : listClasses) {
                 try {
                     if(log.isDebugEnabled()) {
                         log.debug("Loading class: "+ strClassName);
                     }
                     Class<?> commandClass = Class.forName(strClassName);
                     if (!Modifier.isAbstract(commandClass.getModifiers())) {
                         if(log.isDebugEnabled()) {
                             log.debug("Instantiating: "+ commandClass.getName());
                         }
                             SamplerCreator creator = (SamplerCreator) commandClass.newInstance();
                             String[] contentTypes = creator.getManagedContentTypes();
                             for (String contentType : contentTypes) {
                                 if(log.isDebugEnabled()) {
                                     log.debug("Registering samplerCreator "+commandClass.getName()+" for content type:"+contentType);
                                 }
                                 SamplerCreator oldSamplerCreator = samplerCreatorMap.put(contentType, creator);
                                 if(oldSamplerCreator!=null) {
                                     log.warn("A sampler creator was already registered for:"+contentType+", class:"+oldSamplerCreator.getClass()
                                             + ", it will be replaced");
                                 }
                             }                        
                     }
                 } catch (Exception e) {
                     log.error("Exception registering "+SamplerCreator.class.getName() + " with implementation:"+strClassName, e);
                 }
             }
         } catch (IOException e) {
             log.error("Exception finding implementations of "+SamplerCreator.class, e);
         }
     }
 
     /**
      * Gets {@link SamplerCreator} for content type, if none is found returns {@link DefaultSamplerCreator}
-     * @param request {@link HttpRequestHdr}
-     * @param pageEncodings Map<String, String> pageEncodings
-     * @param formEncodings  Map<String, String> formEncodings
-     * @return SamplerCreator
+     * @param request {@link HttpRequestHdr} from which the content type should be used
+     * @param pageEncodings Map of pageEncodings
+     * @param formEncodings  Map of formEncodings
+     * @return SamplerCreator for the content type of the <code>request</code>, or {@link DefaultSamplerCreator} when none is found
      */
     public SamplerCreator getSamplerCreator(HttpRequestHdr request,
             Map<String, String> pageEncodings, Map<String, String> formEncodings) {
         SamplerCreator creator = samplerCreatorMap.get(request.getContentType());
         if(creator == null) {
             return DEFAULT_SAMPLER_CREATOR;
         }
         return creator;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
index 4447b5d95..bd7dcb5b2 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
@@ -1,571 +1,571 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.StringTokenizer;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 // For JUnit tests, @see TestTCLogParser
 
 /**
  * Description:<br>
  * <br>
  * Currently the parser only handles GET/POST requests. It's easy enough to add
  * support for other request methods by changing checkMethod. The is a complete
  * rewrite of a tool I wrote for myself earlier. The older algorithm was basic
  * and did not provide the same level of flexibility I want, so I wrote a new
  * one using a totally new algorithm. This implementation reads one line at a
  * time using BufferedReader. When it gets to the end of the file and the
  * sampler needs to get more requests, the parser will re-initialize the
  * BufferedReader. The implementation uses StringTokenizer to create tokens.
  * <p>
  * The parse algorithm is the following:
  * <p>
  * <ol>
  * <li> cleans the entry by looking for backslash "\"
  * <li> looks to see if GET or POST is in the line
  * <li> tokenizes using quotes "
  * <li> finds the token with the request method
  * <li> gets the string of the token and tokenizes it using space
  * <li> finds the first token beginning with slash character
  * <li> tokenizes the string using question mark "?"
  * <li> get the path from the first token
  * <li> returns the second token and checks it for parameters
  * <li> tokenizes the string using ampersand "&amp;"
  * <li> parses each token to name/value pairs
  * </ol>
  * <p>
  * Extending this class is fairly simple. Most access logs use the same format
  * starting from the request method. Therefore, changing the implementation of
  * cleanURL(string) method should be sufficient to support new log formats.
  * Tomcat uses common log format, so any webserver that uses the format should
  * work with this parser. Servers that are known to use non standard formats are
  * IIS and Netscape.
  * <p>
  *
  */
 
 public class TCLogParser implements LogParser {
     protected static final Logger log = LoggingManager.getLoggerForClass();
 
     /*
      * TODO should these fields be public?
      * They don't appear to be used externally.
      * 
      * Also, are they any different from HTTPConstants.GET etc. ?
      * In some cases they seem to be used as the method name from the Tomcat log.
      * However the RMETHOD field is used as the value for HTTPSamplerBase.METHOD,
      * for which HTTPConstants is most approriate.
      */
     public static final String GET = "GET";
 
     public static final String POST = "POST";
 
     public static final String HEAD = "HEAD";
 
     /** protected members * */
     protected String RMETHOD = null;
 
     /**
      * The path to the access log file
      */
     protected String URL_PATH = null;
 
     protected boolean useFILE = true;
 
     protected File SOURCE = null;
 
     protected String FILENAME = null;
 
     protected BufferedReader READER = null;
 
     /**
      * Handles to supporting classes
      */
     protected Filter FILTER = null;
 
     /**
      * by default, we probably should decode the parameter values
      */
     protected boolean decode = true;
 
     // TODO downcase UPPER case non-final variables
 
     /**
      *
      */
     public TCLogParser() {
         super();
     }
 
     /**
      * @param source
      */
     public TCLogParser(String source) {
         setSourceFile(source);
     }
 
     /**
      * by default decode is set to true. if the parameters shouldn't be
      * decoded, call the method with false
      * @param decodeparams
      */
     public void setDecodeParameterValues(boolean decodeparams) {
         this.decode = decodeparams;
     }
 
     /**
      * decode the parameter values is to true by default
      * @return  if paramter values should be decoded
      */
     public boolean decodeParameterValue() {
         return this.decode;
     }
 
     /**
      * Calls this method to set whether or not to use the path in the log. We
      * may want to provide the ability to filter the log file later on. By
      * default, the parser uses the file in the log.
      *
      * @param file
      */
     public void setUseParsedFile(boolean file) {
         this.useFILE = file;
     }
 
     /**
      * Use the filter to include/exclude files in the access logs. This is
      * provided as a convienance and reduce the need to spend hours cleaning up
      * log files.
      *
      * @param filter
      */
     @Override
     public void setFilter(Filter filter) {
         FILTER = filter;
     }
 
     /**
      * Sets the source file.
      *
      * @param source
      */
     @Override
     public void setSourceFile(String source) {
         this.FILENAME = source;
     }
 
     /**
      * parse the entire file.
      *
      * @return boolean success/failure
      */
     public int parse(TestElement el, int parseCount) {
         if (this.SOURCE == null) {
             this.SOURCE = new File(this.FILENAME);
         }
         try {
             if (this.READER == null) {
                 this.READER = getReader(this.SOURCE);
             }
             return parse(this.READER, el, parseCount);
         } catch (Exception exception) {
             log.error("Problem creating samples", exception);
         }
         return -1;// indicate that an error occured
     }
 
     private static BufferedReader getReader(File file) throws IOException {
         if (! isGZIP(file)) {
             return new BufferedReader(new FileReader(file));
         }
         GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
         return new BufferedReader(new InputStreamReader(in));
     }
 
     private static boolean isGZIP(File file) throws IOException {
         FileInputStream in = new FileInputStream(file);
         try {
             return in.read() == (GZIPInputStream.GZIP_MAGIC & 0xFF)
                 && in.read() == (GZIPInputStream.GZIP_MAGIC >> 8);
         } finally {
             in.close();
         }
     }
 
     /**
      * parse a set number of lines from the access log. Keep in mind the number
      * of lines parsed will depend the filter and number of lines in the log.
      * The method returns the actual number of lines parsed.
      *
      * @param count
      * @return lines parsed
      */
     @Override
     public int parseAndConfigure(int count, TestElement el) {
         return this.parse(el, count);
     }
 
     /**
      * The method is responsible for reading each line, and breaking out of the
      * while loop if a set number of lines is given.
      *
      * @param breader
      */
     protected int parse(BufferedReader breader, TestElement el, int parseCount) {
         int actualCount = 0;
         String line = null;
         try {
             // read one line at a time using
             // BufferedReader
             line = breader.readLine();
             while (line != null) {
                 if (line.length() > 0) {
                     actualCount += this.parseLine(line, el);
                 }
                 // we check the count to see if we have exceeded
                 // the number of lines to parse. There's no way
                 // to know where to stop in the file. Therefore
                 // we use break to escape the while loop when
                 // we've reached the count.
                 if (parseCount != -1 && actualCount >= parseCount) {
                     break;
                 }
                 line = breader.readLine();
             }
             if (line == null) {
                 breader.close();
                 this.READER = null;
                 // this.READER = new BufferedReader(new
                 // FileReader(this.SOURCE));
                 // parse(this.READER,el);
             }
         } catch (IOException ioe) {
             log.error("Error reading log file", ioe);
         }
         return actualCount;
     }
 
     /**
      * parseLine calls the other parse methods to parse the given text.
      *
      * @param line
      */
     protected int parseLine(String line, TestElement el) {
         int count = 0;
         // we clean the line to get
         // rid of extra stuff
         String cleanedLine = this.cleanURL(line);
         log.debug("parsing line: " + line);
         // now we set request method
         el.setProperty(HTTPSamplerBase.METHOD, RMETHOD);
         if (FILTER != null) {
             log.debug("filter is not null");
             if (!FILTER.isFiltered(line,el)) {
                 log.debug("line was not filtered");
                 // increment the current count
                 count++;
                 // we filter the line first, before we try
                 // to separate the URL into file and
                 // parameters.
                 line = FILTER.filter(cleanedLine);
                 if (line != null) {
                     createUrl(line, el);
                 }
             } else {
                 log.debug("Line was filtered");
             }
         } else {
             log.debug("filter was null");
             // increment the current count
             count++;
             // in the case when the filter is not set, we
             // parse all the lines
             createUrl(cleanedLine, el);
         }
         return count;
     }
 
     /**
      * @param line
      */
     private void createUrl(String line, TestElement el) {
         String paramString = null;
         // check the URL for "?" symbol
         paramString = this.stripFile(line, el);
         if (paramString != null) {
             this.checkParamFormat(line);
             // now that we have stripped the file, we can parse the parameters
             this.convertStringToJMRequest(paramString, el);
         }
     }
 
     /**
      * The method cleans the URL using the following algorithm.
      * <ol>
      * <li> check for double quotes
      * <li> check the request method
      * <li> tokenize using double quotes
      * <li> find first token containing request method
      * <li> tokenize string using space
      * <li> find first token that begins with "/"
      * </ol>
      * Example Tomcat log entry:
      * <p>
      * 127.0.0.1 - - [08/Jan/2003:07:03:54 -0500] "GET /addrbook/ HTTP/1.1" 200
      * 1981
      * <p>
      *
      * @param entry
      * @return cleaned url
      */
     public String cleanURL(String entry) {
         String url = entry;
         // if the string contains atleast one double
         // quote and checkMethod is true, go ahead
         // and tokenize the string.
         if (entry.indexOf('"') > -1 && checkMethod(entry)) {
             StringTokenizer tokens = null;
             // we tokenize using double quotes. this means
             // for tomcat we should have 3 tokens if there
             // isn't any additional information in the logs
             tokens = this.tokenize(entry, "\"");
             while (tokens.hasMoreTokens()) {
                 String toke = tokens.nextToken();
                 // if checkMethod on the token is true
                 // we tokenzie it using space and escape
                 // the while loop. Only the first matching
                 // token will be used
                 if (checkMethod(toke)) {
                     StringTokenizer token2 = this.tokenize(toke, " ");
                     while (token2.hasMoreTokens()) {
                         String t = (String) token2.nextElement();
                         if (t.equalsIgnoreCase(GET)) {
                             RMETHOD = GET;
                         } else if (t.equalsIgnoreCase(POST)) {
                             RMETHOD = POST;
                         } else if (t.equalsIgnoreCase(HEAD)) {
                             RMETHOD = HEAD;
                         }
                         // there should only be one token
                         // that starts with slash character
                         if (t.startsWith("/")) {
                             url = t;
                             break;
                         }
                     }
                     break;
                 }
             }
             return url;
         }
         // we return the original string
         return url;
     }
 
     /**
      * The method checks for POST and GET methods currently. The other methods
      * aren't supported yet.
      *
      * @param text
      * @return if method is supported
      */
     public boolean checkMethod(String text) {
         if (text.indexOf("GET") > -1) {
             this.RMETHOD = GET;
             return true;
         } else if (text.indexOf("POST") > -1) {
             this.RMETHOD = POST;
             return true;
         } else if (text.indexOf("HEAD") > -1) {
             this.RMETHOD = HEAD;
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * Tokenize the URL into two tokens. If the URL has more than one "?", the
      * parse may fail. Only the first two tokens are used. The first token is
      * automatically parsed and set at URL_PATH.
      *
      * @param url
      * @return String parameters
      */
     public String stripFile(String url, TestElement el) {
         if (url.indexOf('?') > -1) {
             StringTokenizer tokens = this.tokenize(url, "?");
             this.URL_PATH = tokens.nextToken();
             el.setProperty(HTTPSamplerBase.PATH, URL_PATH);
             return tokens.hasMoreTokens() ? tokens.nextToken() : null;
         }
         el.setProperty(HTTPSamplerBase.PATH, url);
         return null;
     }
 
     /**
      * Checks the string to make sure it has /path/file?name=value format. If
      * the string doesn't have "?", it will return false.
      *
      * @param url
      * @return boolean
      */
     public boolean checkURL(String url) {
         if (url.indexOf('?') > -1) {
             return true;
         }
         return false;
     }
 
     /**
      * Checks the string to see if it contains "&amp;" and "=". If it does, return
      * true, so that it can be parsed.
      *
      * @param text
      * @return boolean
      */
     public boolean checkParamFormat(String text) {
         if (text.indexOf('&') > -1 && text.indexOf('=') > -1) {
             return true;
         }
         return false;
     }
 
     /**
      * Convert a single line into XML
      *
      * @param text
      */
     public void convertStringToJMRequest(String text, TestElement el) {
         ((HTTPSamplerBase) el).parseArguments(text);
     }
 
     /**
      * Parse the string parameters into NVPair[] array. Once they are parsed, it
      * is returned. The method uses parseOneParameter(string) to convert each
      * pair.
      *
      * @param stringparams
      */
     public NVPair[] convertStringtoNVPair(String stringparams) {
         List<String> vparams = this.parseParameters(stringparams);
         NVPair[] nvparams = new NVPair[vparams.size()];
         // convert the Parameters
         for (int idx = 0; idx < nvparams.length; idx++) {
             nvparams[idx] = this.parseOneParameter(vparams.get(idx));
         }
         return nvparams;
     }
 
     /**
      * Method expects name and value to be separated by an equal sign "=". The
      * method uses StringTokenizer to make a NVPair object. If there happens to
      * be more than one "=" sign, the others are ignored. The chance of a string
      * containing more than one is unlikely and would not conform to HTTP spec.
      * I should double check the protocol spec to make sure this is accurate.
      *
      * @param parameter
      *            to be parsed
      * @return NVPair
      */
     protected NVPair parseOneParameter(String parameter) {
         String name = ""; // avoid possible NPE when trimming the name
         String value = null;
         try {
             StringTokenizer param = this.tokenize(parameter, "=");
             name = param.nextToken();
             value = param.nextToken();
         } catch (Exception e) {
             // do nothing. it's naive, but since
             // the utility is meant to parse access
             // logs the formatting should be correct
         }
         if (value == null) {
             value = "";
         } else {
             if (decode) {
                 try {
                     value = URLDecoder.decode(value,"UTF-8");
                 } catch (UnsupportedEncodingException e) {
                     log.warn(e.getMessage());
                 }
             }
         }
         return new NVPair(name.trim(), value.trim());
     }
 
     /**
      * Method uses StringTokenizer to convert the string into single pairs. The
      * string should conform to HTTP protocol spec, which means the name/value
      * pairs are separated by the ampersand symbol "&amp;". Someone could write the
      * querystrings by hand, but that would be round about and go against the
      * purpose of this utility.
      *
      * @param parameters
-     * @return List<String>
+     * @return List of name/value pairs
      */
     protected List<String> parseParameters(String parameters) {
         List<String> parsedParams = new ArrayList<String>();
         StringTokenizer paramtokens = this.tokenize(parameters, "&");
         while (paramtokens.hasMoreElements()) {
             parsedParams.add(paramtokens.nextToken());
         }
         return parsedParams;
     }
 
     /**
      * Parses the line using java.util.StringTokenizer.
      *
      * @param line
      *            line to be parsed
      * @param delim
      *            delimiter
      * @return StringTokenizer
      */
     public StringTokenizer tokenize(String line, String delim) {
         return new StringTokenizer(line, delim);
     }
 
     @Override
     public void close() {
         try {
             this.READER.close();
             this.READER = null;
             this.SOURCE = null;
         } catch (IOException e) {
             // do nothing
         }
     }
 }
diff --git a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java
index 3769892ec..4abd804e7 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/protocol/SendMailCommand.java
@@ -1,828 +1,828 @@
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
 
 package org.apache.jmeter.protocol.smtp.sampler.protocol;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import javax.activation.DataHandler;
 import javax.activation.FileDataSource;
 import javax.mail.BodyPart;
 import javax.mail.Message;
 import javax.mail.MessagingException;
 import javax.mail.Multipart;
 import javax.mail.Session;
 import javax.mail.Transport;
 import javax.mail.internet.InternetAddress;
 import javax.mail.internet.MimeBodyPart;
 import javax.mail.internet.MimeMessage;
 import javax.mail.internet.MimeMultipart;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * This class performs all tasks necessary to send a message (build message,
  * prepare connection, send message). Provides getter-/setter-methods for an
  * SmtpSampler-object to configure transport and message settings. The
  * send-mail-command itself is started by the SmtpSampler-object.
  */
 public class SendMailCommand {
 
     // local vars
     private static final Logger logger = LoggingManager.getLoggerForClass();
 
     // Use the actual class so the name must be correct.
     private static final String TRUST_ALL_SOCKET_FACTORY = TrustAllSSLSocketFactory.class.getName();
 
     private boolean useSSL = false;
     private boolean useStartTLS = false;
     private boolean trustAllCerts = false;
     private boolean enforceStartTLS = false;
     private boolean sendEmlMessage = false;
     private boolean enableDebug;
     private String smtpServer;
     private String smtpPort;
     private String sender;
     private List<InternetAddress> replyTo;
     private String emlMessage;
     private List<InternetAddress> receiverTo;
     private List<InternetAddress> receiverCC;
     private List<InternetAddress> receiverBCC;
     private CollectionProperty headerFields;
     private String subject = "";
 
     private boolean useAuthentication = false;
     private String username;
     private String password;
 
     private boolean useLocalTrustStore;
     private String trustStoreToUse;
 
     private List<File> attachments;
 
     private String mailBody;
 
     private String timeOut; // Socket read timeout value in milliseconds. This timeout is implemented by java.net.Socket.
     private String connectionTimeOut; // Socket connection timeout value in milliseconds. This timeout is implemented by java.net.Socket.
 
     // case we are measuring real time of spedition
     private boolean synchronousMode;
 
     private Session session;
 
     private StringBuilder serverResponse = new StringBuilder(); // TODO this is not populated currently
 
     /** send plain body, i.e. not multipart/mixed */
     private boolean plainBody;
 
     /**
      * Standard-Constructor
      */
     public SendMailCommand() {
         headerFields = new CollectionProperty();
         attachments = new ArrayList<File>();
     }
 
     /**
      * Prepares message prior to be sent via execute()-method, i.e. sets
      * properties such as protocol, authentication, etc.
      *
      * @return Message-object to be sent to execute()-method
      * @throws MessagingException
      * @throws IOException
      */
     public Message prepareMessage() throws MessagingException, IOException {
 
         Properties props = new Properties();
 
         String protocol = getProtocol();
 
         // set properties using JAF
         props.setProperty("mail." + protocol + ".host", smtpServer);
         props.setProperty("mail." + protocol + ".port", getPort());
         props.setProperty("mail." + protocol + ".auth", Boolean.toString(useAuthentication));
         
         // set timeout
         props.setProperty("mail." + protocol + ".timeout", getTimeout());
         props.setProperty("mail." + protocol + ".connectiontimeout", getConnectionTimeout());
 
         if (enableDebug) {
             props.setProperty("mail.debug","true");
         }
 
         if (useStartTLS) {
             props.setProperty("mail.smtp.starttls.enable", "true");
             if (enforceStartTLS){
                 // Requires JavaMail 1.4.2+
                 props.setProperty("mail.smtp.starttls.require", "true");
             }
         }
 
         if (trustAllCerts) {
             if (useSSL) {
                 props.setProperty("mail.smtps.ssl.socketFactory.class", TRUST_ALL_SOCKET_FACTORY);
                 props.setProperty("mail.smtps.ssl.socketFactory.fallback", "false");
             } else if (useStartTLS) {
                 props.setProperty("mail.smtp.ssl.socketFactory.class", TRUST_ALL_SOCKET_FACTORY);
                 props.setProperty("mail.smtp.ssl.socketFactory.fallback", "false");
             }
         } else if (useLocalTrustStore){
             File truststore = new File(trustStoreToUse);
             logger.info("load local truststore - try to load truststore from: "+truststore.getAbsolutePath());
             if(!truststore.exists()){
                 logger.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath());
                 truststore = new File(FileServer.getFileServer().getBaseDir(), trustStoreToUse);
                 logger.info("load local truststore -Attempting to read truststore from:  "+truststore.getAbsolutePath());
                 if(!truststore.exists()){
                     logger.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath() + ". Local truststore not available, aborting execution.");
                     throw new IOException("Local truststore file not found. Also not available under : " + truststore.getAbsolutePath());
                 }
             }
             if (useSSL) {
                 // Requires JavaMail 1.4.2+
                 props.put("mail.smtps.ssl.socketFactory", new LocalTrustStoreSSLSocketFactory(truststore));
                 props.put("mail.smtps.ssl.socketFactory.fallback", "false");
             } else if (useStartTLS) {
                 // Requires JavaMail 1.4.2+
                 props.put("mail.smtp.ssl.socketFactory", new LocalTrustStoreSSLSocketFactory(truststore));
                 props.put("mail.smtp.ssl.socketFactory.fallback", "false");
             }
         }
 
         session = Session.getInstance(props, null);
         
         Message message;
 
         if (sendEmlMessage) {
             message = new MimeMessage(session, new BufferedInputStream(new FileInputStream(emlMessage)));
         } else {
             message = new MimeMessage(session);
             // handle body and attachments
             Multipart multipart = new MimeMultipart();
             final int attachmentCount = attachments.size();
             if (plainBody && 
                (attachmentCount == 0 ||  (mailBody.length() == 0 && attachmentCount == 1))) {
                 if (attachmentCount == 1) { // i.e. mailBody is empty
                     File first = attachments.get(0);
                     InputStream is = null;
                     try {
                         is = new BufferedInputStream(new FileInputStream(first));
                         message.setText(IOUtils.toString(is));
                     } finally {
                         IOUtils.closeQuietly(is);
                     }
                 } else {
                     message.setText(mailBody);
                 }
             } else {
                 BodyPart body = new MimeBodyPart();
                 body.setText(mailBody);
                 multipart.addBodyPart(body);
                 for (File f : attachments) {
                     BodyPart attach = new MimeBodyPart();
                     attach.setFileName(f.getName());
                     attach.setDataHandler(new DataHandler(new FileDataSource(f.getAbsolutePath())));
                     multipart.addBodyPart(attach);
                 }
                 message.setContent(multipart);
             }
         }
 
         // set from field and subject
         if (null != sender) {
             message.setFrom(new InternetAddress(sender));
         }
 
         if (null != replyTo) {
             InternetAddress[] to = new InternetAddress[replyTo.size()];
             message.setReplyTo(replyTo.toArray(to));
         }
 
         if(null != subject) {
             message.setSubject(subject);
         }
         
         if (receiverTo != null) {
             InternetAddress[] to = new InternetAddress[receiverTo.size()];
             receiverTo.toArray(to);
             message.setRecipients(Message.RecipientType.TO, to);
         }
 
         if (receiverCC != null) {
             InternetAddress[] cc = new InternetAddress[receiverCC.size()];
             receiverCC.toArray(cc);
             message.setRecipients(Message.RecipientType.CC, cc);
         }
 
         if (receiverBCC != null) {
             InternetAddress[] bcc = new InternetAddress[receiverBCC.size()];
             receiverBCC.toArray(bcc);
             message.setRecipients(Message.RecipientType.BCC, bcc);
         }
 
         for (int i = 0; i < headerFields.size(); i++) {
             Argument argument = (Argument)((TestElementProperty)headerFields.get(i)).getObjectValue();
             message.setHeader(argument.getName(), argument.getValue());
         }
 
         message.saveChanges();
         return message;
     }
 
     /**
      * Sends message to mailserver, waiting for delivery if using synchronous mode.
      *
      * @param message
      *            Message previously prepared by prepareMessage()
      * @throws MessagingException
      * @throws IOException
      * @throws InterruptedException
      */
     public void execute(Message message) throws MessagingException, IOException, InterruptedException {
 
         Transport tr = session.getTransport(getProtocol());
         SynchronousTransportListener listener = null;
 
         if (synchronousMode) {
             listener = new SynchronousTransportListener();
             tr.addTransportListener(listener);
         }
 
         if (useAuthentication) {
             tr.connect(smtpServer, username, password);
         } else {
             tr.connect();
         }
 
         tr.sendMessage(message, message.getAllRecipients());
 
         if (listener != null /*synchronousMode==true*/) {
             listener.attend(); // listener cannot be null here
         }
 
         tr.close();
         logger.debug("transport closed");
 
         logger.debug("message sent");
         return;
     }
 
     /**
      * Processes prepareMessage() and execute()
      *
      * @throws InterruptedException 
      * @throws IOException 
      * @throws MessagingException 
      */
     public void execute() throws MessagingException, IOException, InterruptedException {
         execute(prepareMessage());
     }
 
     /**
      * Returns FQDN or IP of SMTP-server to be used to send message - standard
      * getter
      *
      * @return FQDN or IP of SMTP-server
      */
     public String getSmtpServer() {
         return smtpServer;
     }
 
     /**
      * Sets FQDN or IP of SMTP-server to be used to send message - to be called
      * by SmtpSampler-object
      *
      * @param smtpServer
      *            FQDN or IP of SMTP-server
      */
     public void setSmtpServer(String smtpServer) {
         this.smtpServer = smtpServer;
     }
 
     /**
      * Returns sender-address for current message - standard getter
      *
      * @return sender-address
      */
     public String getSender() {
         return sender;
     }
 
     /**
      * Sets the sender-address for the current message - to be called by
      * SmtpSampler-object
      *
      * @param sender
      *            Sender-address for current message
      */
     public void setSender(String sender) {
         this.sender = sender;
     }
 
     /**
      * Returns subject for current message - standard getter
      *
      * @return Subject of current message
      */
     public String getSubject() {
         return subject;
     }
 
     /**
      * Sets subject for current message - called by SmtpSampler-object
      *
      * @param subject
      *            Subject for message of current message - may be null
      */
     public void setSubject(String subject) {
         this.subject = subject;
     }
 
     /**
      * Returns username to authenticate at the mailserver - standard getter
      *
      * @return Username for mailserver
      */
     public String getUsername() {
         return username;
     }
 
     /**
      * Sets username to authenticate at the mailserver - to be called by
      * SmtpSampler-object
      *
      * @param username
      *            Username for mailserver
      */
     public void setUsername(String username) {
         this.username = username;
     }
 
     /**
      * Returns password to authenticate at the mailserver - standard getter
      *
      * @return Password for mailserver
      */
     public String getPassword() {
         return password;
     }
 
     /**
      * Sets password to authenticate at the mailserver - to be called by
      * SmtpSampler-object
      *
      * @param password
      *            Password for mailserver
      */
     public void setPassword(String password) {
         this.password = password;
     }
 
     /**
      * Sets receivers of current message ("to") - to be called by
      * SmtpSampler-object
      *
      * @param receiverTo
-     *            List of receivers <InternetAddress>
+     *            List of receivers
      */
     public void setReceiverTo(List<InternetAddress> receiverTo) {
         this.receiverTo = receiverTo;
     }
 
     /**
-     * Returns receivers of current message <InternetAddress> ("cc") - standard
+     * Returns receivers of current message as {@link InternetAddress} ("cc") - standard
      * getter
      *
      * @return List of receivers
      */
     public List<InternetAddress> getReceiverCC() {
         return receiverCC;
     }
 
     /**
      * Sets receivers of current message ("cc") - to be called by
      * SmtpSampler-object
      *
      * @param receiverCC
-     *            List of receivers <InternetAddress>
+     *            List of receivers
      */
     public void setReceiverCC(List<InternetAddress> receiverCC) {
         this.receiverCC = receiverCC;
     }
 
     /**
-     * Returns receivers of current message <InternetAddress> ("bcc") - standard
+     * Returns receivers of current message as {@link InternetAddress} ("bcc") - standard
      * getter
      *
      * @return List of receivers
      */
     public List<InternetAddress> getReceiverBCC() {
         return receiverBCC;
     }
 
     /**
      * Sets receivers of current message ("bcc") - to be called by
      * SmtpSampler-object
      *
      * @param receiverBCC
-     *            List of receivers <InternetAddress>
+     *            List of receivers
      */
     public void setReceiverBCC(List<InternetAddress> receiverBCC) {
         this.receiverBCC = receiverBCC;
     }
 
     /**
      * Returns if authentication is used to access the mailserver - standard
      * getter
      *
      * @return True if authentication is used to access mailserver
      */
     public boolean isUseAuthentication() {
         return useAuthentication;
     }
 
     /**
      * Sets if authentication should be used to access the mailserver - to be
      * called by SmtpSampler-object
      *
      * @param useAuthentication
      *            Should authentication be used to access mailserver?
      */
     public void setUseAuthentication(boolean useAuthentication) {
         this.useAuthentication = useAuthentication;
     }
 
     /**
      * Returns if SSL is used to send message - standard getter
      *
      * @return True if SSL is used to transmit message
      */
     public boolean getUseSSL() {
         return useSSL;
     }
 
     /**
      * Sets SSL to secure the delivery channel for the message - to be called by
      * SmtpSampler-object
      *
      * @param useSSL
      *            Should StartTLS be used to secure SMTP-connection?
      */
     public void setUseSSL(boolean useSSL) {
         this.useSSL = useSSL;
     }
 
     /**
      * Returns if StartTLS is used to transmit message - standard getter
      *
      * @return True if StartTLS is used to transmit message
      */
     public boolean getUseStartTLS() {
         return useStartTLS;
     }
 
     /**
      * Sets StartTLS to secure the delivery channel for the message - to be
      * called by SmtpSampler-object
      *
      * @param useStartTLS
      *            Should StartTLS be used to secure SMTP-connection?
      */
     public void setUseStartTLS(boolean useStartTLS) {
         this.useStartTLS = useStartTLS;
     }
 
     /**
      * Returns port to be used for SMTP-connection (standard 25 or 465) -
      * standard getter
      *
      * @return Port to be used for SMTP-connection
      */
     public String getSmtpPort() {
         return smtpPort;
     }
 
     /**
      * Sets port to be used for SMTP-connection (standard 25 or 465) - to be
      * called by SmtpSampler-object
      *
      * @param smtpPort
      *            Port to be used for SMTP-connection
      */
     public void setSmtpPort(String smtpPort) {
         this.smtpPort = smtpPort;
     }
 
     /**
      * Returns if sampler should trust all certificates - standard getter
      *
      * @return True if all Certificates are trusted
      */
     public boolean isTrustAllCerts() {
         return trustAllCerts;
     }
 
     /**
      * Determines if SMTP-sampler should trust all certificates, no matter what
      * CA - to be called by SmtpSampler-object
      *
      * @param trustAllCerts
      *            Should all certificates be trusted?
      */
     public void setTrustAllCerts(boolean trustAllCerts) {
         this.trustAllCerts = trustAllCerts;
     }
 
     /**
      * Instructs object to enforce StartTLS and not to fallback to plain
      * SMTP-connection - to be called by SmtpSampler-object
      *
      * @param enforceStartTLS
      *            Should StartTLS be enforced?
      */
     public void setEnforceStartTLS(boolean enforceStartTLS) {
         this.enforceStartTLS = enforceStartTLS;
     }
 
     /**
      * Returns if StartTLS is enforced to secure the connection, i.e. no
      * fallback is used (plain SMTP) - standard getter
      *
      * @return True if StartTLS is enforced
      */
     public boolean isEnforceStartTLS() {
         return enforceStartTLS;
     }
 
     /**
      * Returns headers for current message - standard getter
      *
      * @return CollectionProperty of headers for current message
      */
     public CollectionProperty getHeaders() {
         return headerFields;
     }
 
     /**
      * Sets headers for current message
      *
      * @param headerFields
      *            CollectionProperty of headers for current message
      */
     public void setHeaderFields(CollectionProperty headerFields) {
         this.headerFields = headerFields;
     }
 
     /**
      * Adds a header-part to current HashMap of headers - to be called by
      * SmtpSampler-object
      *
      * @param headerName
      *            Key for current header
      * @param headerValue
      *            Value for current header
      */
     public void addHeader(String headerName, String headerValue) {
         if (this.headerFields == null){
             this.headerFields = new CollectionProperty();
         }
         Argument argument = new Argument(headerName, headerValue);
         this.headerFields.addItem(argument);
     }
 
     /**
      * Deletes all current headers in HashMap
      */
     public void clearHeaders() {
         if (this.headerFields == null){
             this.headerFields = new CollectionProperty();
         }else{
             this.headerFields.clear();
         }
     }
 
     /**
      * Returns all attachment for current message - standard getter
      *
      * @return List of attachments for current message
      */
     public List<File> getAttachments() {
         return attachments;
     }
 
     /**
      * Adds attachments to current message
      *
      * @param attachments
      *            List of files to be added as attachments to current message
      */
     public void setAttachments(List<File> attachments) {
         this.attachments = attachments;
     }
 
     /**
      * Adds an attachment to current message - to be called by
      * SmtpSampler-object
      *
      * @param attachment
      *            File-object to be added as attachment to current message
      */
     public void addAttachment(File attachment) {
         this.attachments.add(attachment);
     }
 
     /**
      * Clear all attachments for current message
      */
     public void clearAttachments() {
         this.attachments.clear();
     }
 
     /**
      * Returns if synchronous-mode is used for current message (i.e. time for
      * delivery, ... is measured) - standard getter
      *
      * @return True if synchronous-mode is used
      */
     public boolean isSynchronousMode() {
         return synchronousMode;
     }
 
     /**
      * Sets the use of synchronous-mode (i.e. time for delivery, ... is
      * measured) - to be called by SmtpSampler-object
      *
      * @param synchronousMode
      *            Should synchronous-mode be used?
      */
     public void setSynchronousMode(boolean synchronousMode) {
         this.synchronousMode = synchronousMode;
     }
 
     /**
      * Returns which protocol should be used to transport message (smtps for
      * SSL-secured connections or smtp for plain SMTP / StartTLS)
      *
      * @return Protocol that is used to transport message
      */
     private String getProtocol() {
         return (useSSL) ? "smtps" : "smtp";
     }
 
     /**
      * Returns port to be used for SMTP-connection - returns the
      * default port for the protocol if no port has been supplied.
      *
      * @return Port to be used for SMTP-connection
      */
     private String getPort() {
         String port = smtpPort.trim();
         if (port.length() > 0) { // OK, it has been supplied
             return port;
         }
         if (useSSL){
             return "465";
         }
         if (useStartTLS) {
             return "587";
         }
         return "25";
     }
 
     /**
      * @param timeOut the timeOut to set
      */
     public void setTimeOut(String timeOut) {
         this.timeOut = timeOut;
     }
 
     /**
      * Returns timeout for the SMTP-connection - returns the
      * default timeout if no value has been supplied.
      *
      * @return Timeout to be set for SMTP-connection
      */
     public String getTimeout() {
         String timeout = timeOut.trim();
         if (timeout.length() > 0) { // OK, it has been supplied
             return timeout;
         }
         return "0"; // Default is infinite timeout (value 0).
     }
 
     /**
      * @param connectionTimeOut the connectionTimeOut to set
      */
     public void setConnectionTimeOut(String connectionTimeOut) {
         this.connectionTimeOut = connectionTimeOut;
     }
 
     /**
      * Returns connection timeout for the SMTP-connection - returns the
      * default connection timeout if no value has been supplied.
      *
      * @return Connection timeout to be set for SMTP-connection
      */
     public String getConnectionTimeout() {
         String connectionTimeout = connectionTimeOut.trim();
         if (connectionTimeout.length() > 0) { // OK, it has been supplied
             return connectionTimeout;
         }
         return "0"; // Default is infinite timeout (value 0).
     }
 
     /**
      * Assigns the object to use a local truststore for SSL / StartTLS - to be
      * called by SmtpSampler-object
      *
      * @param useLocalTrustStore
      *            Should a local truststore be used?
      */
     public void setUseLocalTrustStore(boolean useLocalTrustStore) {
         this.useLocalTrustStore = useLocalTrustStore;
     }
 
     /**
      * Sets the path to the local truststore to be used for SSL / StartTLS - to
      * be called by SmtpSampler-object
      *
      * @param trustStoreToUse
      *            Path to local truststore
      */
     public void setTrustStoreToUse(String trustStoreToUse) {
         this.trustStoreToUse = trustStoreToUse;
     }
 
     public void setUseEmlMessage(boolean sendEmlMessage) {
         this.sendEmlMessage = sendEmlMessage;
     }
 
     /**
      * Sets eml-message to be sent
      *
      * @param emlMessage
      *            path to eml-message
      */
     public void setEmlMessage(String emlMessage) {
         this.emlMessage = emlMessage;
     }
 
     /**
      * Set the mail body.
      *
      * @param body
      */
     public void setMailBody(String body){
         mailBody = body;
     }
     
     /**
      * Set whether to send a plain body (i.e. not multipart/mixed)
      *
      * @param plainBody <code>true</code> if sending a plain body (i.e. not multipart/mixed)
      */
     public void setPlainBody(boolean plainBody){
         this.plainBody = plainBody;
     }
 
     public String getServerResponse() {
         return this.serverResponse.toString();
     }
 
     public void setEnableDebug(boolean selected) {
         enableDebug = selected;
 
     }
 
     public void setReplyTo(List<InternetAddress> replyTo) {
         this.replyTo = replyTo;
     }
 }
