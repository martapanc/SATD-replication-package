diff --git a/src/core/org/apache/jmeter/gui/GuiPackage.java b/src/core/org/apache/jmeter/gui/GuiPackage.java
index 73831fb29..d79463546 100644
--- a/src/core/org/apache/jmeter/gui/GuiPackage.java
+++ b/src/core/org/apache/jmeter/gui/GuiPackage.java
@@ -1,864 +1,867 @@
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
      *            the fully qualified class name of the GUI component which will
      *            be created if it doesn't already exist
      * @param testClass
      *            the fully qualified class name of the test elements which have
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
+     *            The filepath of the current test plan
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
+     * 
      * @param stoppable
+     *            The {@link Stoppable} to be registered
      */
     public void register(Stoppable stoppable) {
         stoppables.add(stoppable);
     }
 
     /**
      *
      * @return copy of list of {@link Stoppable}s
      */
     public List<Stoppable> getStoppables() {
         ArrayList<Stoppable> list = new ArrayList<Stoppable>();
         list.addAll(stoppables);
         return list;
     }
 
     /**
      * Set the menu item LoggerPanel.
-     * @param menuItemLoggerPanel
+     * @param menuItemLoggerPanel The menu item LoggerPanel
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
diff --git a/src/core/org/apache/jmeter/gui/MainFrame.java b/src/core/org/apache/jmeter/gui/MainFrame.java
index d56ceaa0f..2b4cdba2b 100644
--- a/src/core/org/apache/jmeter/gui/MainFrame.java
+++ b/src/core/org/apache/jmeter/gui/MainFrame.java
@@ -1,825 +1,828 @@
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
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.Cursor;
 import java.awt.Dimension;
 import java.awt.Font;
 import java.awt.Insets;
 import java.awt.Toolkit;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.Transferable;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.awt.dnd.DnDConstants;
 import java.awt.dnd.DropTarget;
 import java.awt.dnd.DropTargetDragEvent;
 import java.awt.dnd.DropTargetDropEvent;
 import java.awt.dnd.DropTargetEvent;
 import java.awt.dnd.DropTargetListener;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.MouseEvent;
 import java.awt.event.WindowAdapter;
 import java.awt.event.WindowEvent;
 import java.io.File;
 import java.io.IOException;
 import java.lang.reflect.Field;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.DropMode;
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTree;
 import javax.swing.MenuElement;
 import javax.swing.SwingUtilities;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.TreeCellRenderer;
 import javax.swing.tree.TreeModel;
 import javax.swing.tree.TreePath;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.LoadDraggedFile;
 import org.apache.jmeter.gui.tree.JMeterCellRenderer;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeTransferHandler;
 import org.apache.jmeter.gui.util.EscapeDialog;
 import org.apache.jmeter.gui.util.JMeterMenuBar;
 import org.apache.jmeter.gui.util.JMeterToolBar;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.LogEvent;
 import org.apache.log.LogTarget;
 import org.apache.log.Logger;
 import org.apache.log.Priority;
 
 /**
  * The main JMeter frame, containing the menu bar, test tree, and an area for
  * JMeter component GUIs.
  *
  */
 public class MainFrame extends JFrame implements TestStateListener, Remoteable, DropTargetListener, Clearable, ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     // This is used to keep track of local (non-remote) tests
     // The name is chosen to be an unlikely host-name
     public static final String LOCAL = "*local*"; // $NON-NLS-1$
 
     // The application name
     private static final String DEFAULT_APP_NAME = "Apache JMeter"; // $NON-NLS-1$
 
     // The default title for the Menu bar
     private static final String DEFAULT_TITLE = DEFAULT_APP_NAME +
             " (" + JMeterUtils.getJMeterVersion() + ")"; // $NON-NLS-1$ $NON-NLS-2$
 
     // Allow display/hide toolbar
     private static final boolean DISPLAY_TOOLBAR =
             JMeterUtils.getPropDefault("jmeter.toolbar.display", true); // $NON-NLS-1$
 
     // Allow display/hide LoggerPanel
     private static final boolean DISPLAY_LOGGER_PANEL =
             JMeterUtils.getPropDefault("jmeter.loggerpanel.display", false); // $NON-NLS-1$
 
     // Allow display/hide Log Error/Fatal counter
     private static final boolean DISPLAY_ERROR_FATAL_COUNTER =
             JMeterUtils.getPropDefault("jmeter.errorscounter.display", true); // $NON-NLS-1$
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** The menu bar. */
     private JMeterMenuBar menuBar;
 
     /** The main panel where components display their GUIs. */
     private JScrollPane mainPanel;
 
     /** The panel where the test tree is shown. */
     private JScrollPane treePanel;
 
     /** The LOG panel. */
     private LoggerPanel logPanel;
 
     /** The test tree. */
     private JTree tree;
 
     /** An image which is displayed when a test is running. */
     private final ImageIcon runningIcon = JMeterUtils.getImage("thread.enabled.gif");// $NON-NLS-1$
 
     /** An image which is displayed when a test is not currently running. */
     private final ImageIcon stoppedIcon = JMeterUtils.getImage("thread.disabled.gif");// $NON-NLS-1$
 
     /** An image which is displayed to indicate FATAL, ERROR or WARNING. */
     private final ImageIcon warningIcon = JMeterUtils.getImage("warning.png");// $NON-NLS-1$
 
     /** The button used to display the running/stopped image. */
     private JButton runningIndicator;
 
     /** The set of currently running hosts. */
     private final Set<String> hosts = new HashSet<String>();
 
     /** A message dialog shown while JMeter threads are stopping. */
     private JDialog stoppingMessage;
 
     private JLabel totalThreads;
     private JLabel activeThreads;
 
     private JMeterToolBar toolbar;
 
     /**
      * Indicator for Log errors and Fatals
      */
     private JButton warnIndicator;
     /**
      * Counter
      */
     private JLabel errorsOrFatalsLabel;
     /**
      * LogTarget that receives ERROR or FATAL
      */
     private transient ErrorsAndFatalsCounterLogTarget errorsAndFatalsCounterLogTarget;
 
     /**
      * Create a new JMeter frame.
      *
      * @param treeModel
      *            the model for the test tree
      * @param treeListener
      *            the listener for the test tree
      */
     public MainFrame(TreeModel treeModel, JMeterTreeListener treeListener) {
 
         // TODO: Make the running indicator its own class instead of a JButton
         runningIndicator = new JButton(stoppedIcon);
         runningIndicator.setMargin(new Insets(0, 0, 0, 0));
         runningIndicator.setBorder(BorderFactory.createEmptyBorder());
 
         totalThreads = new JLabel("0"); // $NON-NLS-1$
         totalThreads.setToolTipText(JMeterUtils.getResString("total_threads_tooltip")); // $NON-NLS-1$
         activeThreads = new JLabel("0"); // $NON-NLS-1$
         activeThreads.setToolTipText(JMeterUtils.getResString("active_threads_tooltip")); // $NON-NLS-1$
 
         warnIndicator = new JButton(warningIcon);
         warnIndicator.setMargin(new Insets(0, 0, 0, 0));
         // Transparent JButton with no border
         warnIndicator.setOpaque(false);
         warnIndicator.setContentAreaFilled(false);
         warnIndicator.setBorderPainted(false);
         warnIndicator.setCursor(new Cursor(Cursor.HAND_CURSOR));
         
         warnIndicator.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
         warnIndicator.addActionListener(this);
         errorsOrFatalsLabel = new JLabel("0"); // $NON-NLS-1$
         errorsOrFatalsLabel.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
 
         tree = makeTree(treeModel, treeListener);
 
         GuiPackage.getInstance().setMainFrame(this);
         init();
         initTopLevelDndHandler();
         setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
     }
 
     /**
      * Default constructor for the JMeter frame. This constructor will not
      * properly initialize the tree, so don't use it.
      *
      * @deprecated Do not use - only needed for JUnit tests
      */
     @Deprecated
     public MainFrame() {
     }
 
     // MenuBar related methods
     // TODO: Do we really need to have all these menubar methods duplicated
     // here? Perhaps we can make the menu bar accessible through GuiPackage?
 
     /**
      * Specify whether or not the File|Load menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileLoadEnabled(boolean enabled) {
         menuBar.setFileLoadEnabled(enabled);
     }
 
     /**
      * Specify whether or not the File|Save menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileSaveEnabled(boolean enabled) {
         menuBar.setFileSaveEnabled(enabled);
     }
 
     /**
      * Specify whether or not the File|Revert item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileRevertEnabled(boolean enabled) {
         menuBar.setFileRevertEnabled(enabled);
     }
 
     /**
      * Specify the project file that was just loaded
      *
      * @param file - the full path to the file that was loaded
      */
     public void setProjectFileLoaded(String file) {
         menuBar.setProjectFileLoaded(file);
     }
 
     /**
      * Set the menu that should be used for the Edit menu.
      *
      * @param menu
      *            the new Edit menu
      */
     public void setEditMenu(JPopupMenu menu) {
         menuBar.setEditMenu(menu);
     }
 
     /**
      * Specify whether or not the Edit menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setEditEnabled(boolean enabled) {
         menuBar.setEditEnabled(enabled);
     }
 
     /**
      * Set the menu that should be used for the Edit|Add menu.
      *
      * @param menu
      *            the new Edit|Add menu
      */
     public void setEditAddMenu(JMenu menu) {
         menuBar.setEditAddMenu(menu);
     }
 
     /**
      * Specify whether or not the Edit|Add menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setEditAddEnabled(boolean enabled) {
         menuBar.setEditAddEnabled(enabled);
     }
 
     /**
      * Close the currently selected menu.
      */
     public void closeMenu() {
         if (menuBar.isSelected()) {
             MenuElement[] menuElement = menuBar.getSubElements();
             if (menuElement != null) {
                 for (int i = 0; i < menuElement.length; i++) {
                     JMenu menu = (JMenu) menuElement[i];
                     if (menu.isSelected()) {
                         menu.setPopupMenuVisible(false);
                         menu.setSelected(false);
                         break;
                     }
                 }
             }
         }
     }
 
     /**
      * Show a dialog indicating that JMeter threads are stopping on a particular
      * host.
      *
      * @param host
      *            the host where JMeter threads are stopping
      */
     public void showStoppingMessage(String host) {
         if (stoppingMessage != null){
             stoppingMessage.dispose();
         }
         stoppingMessage = new EscapeDialog(this, JMeterUtils.getResString("stopping_test_title"), true); //$NON-NLS-1$
         String label = JMeterUtils.getResString("stopping_test"); //$NON-NLS-1
         if(!StringUtils.isEmpty(host)) {
             label = label + JMeterUtils.getResString("stopping_test_host")+ ": " + host;
         }
         JLabel stopLabel = new JLabel(label); //$NON-NLS-1$$NON-NLS-2$
         stopLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         stoppingMessage.getContentPane().add(stopLabel);
         stoppingMessage.pack();
         ComponentUtil.centerComponentInComponent(this, stoppingMessage);
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 if (stoppingMessage != null) {// TODO - how can this be null?
                     stoppingMessage.setVisible(true);
                 }
             }
         });
     }
 
     public void updateCounts() {
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 activeThreads.setText(Integer.toString(JMeterContextService.getNumberOfThreads()));
                 totalThreads.setText(Integer.toString(JMeterContextService.getTotalThreads()));
             }
         });
     }
 
     public void setMainPanel(JComponent comp) {
         mainPanel.setViewportView(comp);
     }
 
     public JTree getTree() {
         return tree;
     }
 
     // TestStateListener implementation
 
     /**
      * Called when a test is started on the local system. This implementation
      * sets the running indicator and ensures that the menubar is enabled and in
      * the running state.
      */
     @Override
     public void testStarted() {
         testStarted(LOCAL);
         menuBar.setEnabled(true);
     }
 
     /**
      * Called when a test is started on a specific host. This implementation
      * sets the running indicator and ensures that the menubar is in the running
      * state.
      *
      * @param host
      *            the host where the test is starting
      */
     @Override
     public void testStarted(String host) {
         hosts.add(host);
         runningIndicator.setIcon(runningIcon);
         activeThreads.setText("0"); // $NON-NLS-1$
         totalThreads.setText("0"); // $NON-NLS-1$
         menuBar.setRunning(true, host);
         if(LOCAL.equals(host)) {
             toolbar.setLocalTestStarted(true);
         } else {
             toolbar.setRemoteTestStarted(true);
         }
     }
 
     /**
      * Called when a test is ended on the local system. This implementation
      * disables the menubar, stops the running indicator, and closes the
      * stopping message dialog.
      */
     @Override
     public void testEnded() {
         testEnded(LOCAL);
         menuBar.setEnabled(false);
     }
 
     /**
      * Called when a test is ended on the remote system. This implementation
      * stops the running indicator and closes the stopping message dialog.
      *
      * @param host
      *            the host where the test is ending
      */
     @Override
     public void testEnded(String host) {
         hosts.remove(host);
         if (hosts.size() == 0) {
             runningIndicator.setIcon(stoppedIcon);
             JMeterContextService.endTest();
         }
         menuBar.setRunning(false, host);
         if(LOCAL.equals(host)) {
             toolbar.setLocalTestStarted(false);
         } else {
             toolbar.setRemoteTestStarted(false);
         }
         if (stoppingMessage != null) {
             stoppingMessage.dispose();
             stoppingMessage = null;
         }
     }
 
     /**
      * Create the GUI components and layout.
      */
     private void init() {
         menuBar = new JMeterMenuBar();
         setJMenuBar(menuBar);
         JPanel all = new JPanel(new BorderLayout());
         all.add(createToolBar(), BorderLayout.NORTH);
 
         JSplitPane treeAndMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
 
         treePanel = createTreePanel();
         treeAndMain.setLeftComponent(treePanel);
 
         JSplitPane topAndDown = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         topAndDown.setOneTouchExpandable(true);
         topAndDown.setDividerLocation(0.8);
         topAndDown.setResizeWeight(.8);
         topAndDown.setContinuousLayout(true);
         topAndDown.setBorder(null); // see bug jdk 4131528
         if (!DISPLAY_LOGGER_PANEL) {
             topAndDown.setDividerSize(0);
         }
         mainPanel = createMainPanel();
 
         logPanel = createLoggerPanel();
         if (DISPLAY_ERROR_FATAL_COUNTER) {
             errorsAndFatalsCounterLogTarget = new ErrorsAndFatalsCounterLogTarget();
             LoggingManager.addLogTargetToRootLogger(new LogTarget[]{
                 logPanel,
                 errorsAndFatalsCounterLogTarget
                  });
         } else {
             LoggingManager.addLogTargetToRootLogger(new LogTarget[]{
                     logPanel
                      });
         }
 
         topAndDown.setTopComponent(mainPanel);
         topAndDown.setBottomComponent(logPanel);
 
         treeAndMain.setRightComponent(topAndDown);
 
         treeAndMain.setResizeWeight(.2);
         treeAndMain.setContinuousLayout(true);
         all.add(treeAndMain, BorderLayout.CENTER);
 
         getContentPane().add(all);
 
         tree.setSelectionRow(1);
         addWindowListener(new WindowHappenings());
         // Building is complete, register as listener
         GuiPackage.getInstance().registerAsListener();
         setTitle(DEFAULT_TITLE);
         setIconImage(JMeterUtils.getImage("icon-apache.png").getImage());// $NON-NLS-1$
         setWindowTitle(); // define AWT WM_CLASS string
     }
 
 
     /**
      * Support for Test Plan Dnd
      * see BUG 52281 (when JDK6 will be minimum JDK target)
      */
     public void initTopLevelDndHandler() {
         new DropTarget(this, this);
     }
 
     public void setExtendedFrameTitle(String fname) {
         // file New operation may set to null, so just return app name
         if (fname == null) {
             setTitle(DEFAULT_TITLE);
             return;
         }
 
         // allow for windows / chars in filename
         String temp = fname.replace('\\', '/'); // $NON-NLS-1$ // $NON-NLS-2$
         String simpleName = temp.substring(temp.lastIndexOf('/') + 1);// $NON-NLS-1$
         setTitle(simpleName + " (" + fname + ") - " + DEFAULT_TITLE); // $NON-NLS-1$ // $NON-NLS-2$
     }
 
     /**
      * Create the JMeter tool bar pane containing the running indicator.
      *
      * @return a panel containing the running indicator
      */
     private Component createToolBar() {
         Box toolPanel = new Box(BoxLayout.X_AXIS);
         // add the toolbar
         this.toolbar = JMeterToolBar.createToolbar(DISPLAY_TOOLBAR);
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setMainToolbar(toolbar);
         guiInstance.getMenuItemToolbar().getModel().setSelected(DISPLAY_TOOLBAR);
         toolPanel.add(toolbar);
 
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(Box.createGlue());
 
         if (DISPLAY_ERROR_FATAL_COUNTER) {
             toolPanel.add(errorsOrFatalsLabel);
             toolPanel.add(warnIndicator);
             toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
         }
         toolPanel.add(activeThreads);
         toolPanel.add(new JLabel(" / "));
         toolPanel.add(totalThreads);
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(runningIndicator);
         return toolPanel;
     }
 
     /**
      * Create the panel where the GUI representation of the test tree is
      * displayed. The tree should already be created before calling this method.
      *
      * @return a scroll pane containing the test tree GUI
      */
     private JScrollPane createTreePanel() {
         JScrollPane treeP = new JScrollPane(tree);
         treeP.setMinimumSize(new Dimension(100, 0));
         return treeP;
     }
 
     /**
      * Create the main panel where components can display their GUIs.
      *
      * @return the main scroll pane
      */
     private JScrollPane createMainPanel() {
         return new JScrollPane();
     }
 
     /**
      * Create at the down of the left a Console for Log events
      * @return {@link LoggerPanel}
      */
     private LoggerPanel createLoggerPanel() {
         LoggerPanel loggerPanel = new LoggerPanel();
         loggerPanel.setMinimumSize(new Dimension(0, 100));
         loggerPanel.setPreferredSize(new Dimension(0, 150));
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setLoggerPanel(loggerPanel);
         guiInstance.getMenuItemLoggerPanel().getModel().setSelected(DISPLAY_LOGGER_PANEL);
         loggerPanel.setVisible(DISPLAY_LOGGER_PANEL);
         return loggerPanel;
     }
 
     /**
      * Create and initialize the GUI representation of the test tree.
      *
      * @param treeModel
      *            the test tree model
      * @param treeListener
      *            the test tree listener
      *
      * @return the initialized test tree GUI
      */
     private JTree makeTree(TreeModel treeModel, JMeterTreeListener treeListener) {
         JTree treevar = new JTree(treeModel) {
             private static final long serialVersionUID = 240L;
 
             @Override
             public String getToolTipText(MouseEvent event) {
                 TreePath path = this.getPathForLocation(event.getX(), event.getY());
                 if (path != null) {
                     Object treeNode = path.getLastPathComponent();
                     if (treeNode instanceof DefaultMutableTreeNode) {
                         Object testElement = ((DefaultMutableTreeNode) treeNode).getUserObject();
                         if (testElement instanceof TestElement) {
                             String comment = ((TestElement) testElement).getComment();
                             if (comment != null && comment.length() > 0) {
                                 return comment;
                                 }
                             }
                         }
                     }
                 return null;
                 }
             };
         treevar.setToolTipText("");
         treevar.setCellRenderer(getCellRenderer());
         treevar.setRootVisible(false);
         treevar.setShowsRootHandles(true);
 
         treeListener.setJTree(treevar);
         treevar.addTreeSelectionListener(treeListener);
         treevar.addMouseListener(treeListener);
         treevar.addKeyListener(treeListener);
         
         // enable drag&drop, install a custom transfer handler
         treevar.setDragEnabled(true);
         treevar.setDropMode(DropMode.ON_OR_INSERT);
         treevar.setTransferHandler(new JMeterTreeTransferHandler());
 
         return treevar;
     }
 
     /**
      * Create the tree cell renderer used to draw the nodes in the test tree.
      *
      * @return a renderer to draw the test tree nodes
      */
     private TreeCellRenderer getCellRenderer() {
         DefaultTreeCellRenderer rend = new JMeterCellRenderer();
         rend.setFont(new Font("Dialog", Font.PLAIN, 11));
         return rend;
     }
 
     /**
      * A window adapter used to detect when the main JMeter frame is being
      * closed.
      */
     private static class WindowHappenings extends WindowAdapter {
         /**
          * Called when the main JMeter frame is being closed. Sends a
          * notification so that JMeter can react appropriately.
          *
          * @param event
          *            the WindowEvent to handle
          */
         @Override
         public void windowClosing(WindowEvent event) {
             ActionRouter.getInstance().actionPerformed(new ActionEvent(this, event.getID(), ActionNames.EXIT));
         }
     }
 
     @Override
     public void dragEnter(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     @Override
     public void dragExit(DropTargetEvent dte) {
         // NOOP
     }
 
     @Override
     public void dragOver(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     /**
      * Handler of Top level Dnd
      */
     @Override
     public void drop(DropTargetDropEvent dtde) {
         try {
             Transferable tr = dtde.getTransferable();
             DataFlavor[] flavors = tr.getTransferDataFlavors();
             for (int i = 0; i < flavors.length; i++) {
                 // Check for file lists specifically
                 if (flavors[i].isFlavorJavaFileListType()) {
                     dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                     try {
                         openJmxFilesFromDragAndDrop(tr);
                     } finally {
                         dtde.dropComplete(true);
                     }
                     return;
                 }
             }
         } catch (UnsupportedFlavorException e) {
             log.warn("Dnd failed" , e);
         } catch (IOException e) {
             log.warn("Dnd failed" , e);
         }
 
     }
 
     public boolean openJmxFilesFromDragAndDrop(Transferable tr) throws UnsupportedFlavorException, IOException {
         @SuppressWarnings("unchecked")
         List<File> files = (List<File>)
                 tr.getTransferData(DataFlavor.javaFileListFlavor);
         if(files.isEmpty()) {
             return false;
         }
         File file = files.get(0);
         if(!file.getName().endsWith(".jmx")) {
             log.warn("Importing file:" + file.getName()+ "from DnD failed because file extension does not end with .jmx");
             return false;
         }
 
         ActionEvent fakeEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ActionNames.OPEN);
         LoadDraggedFile.loadProject(fakeEvent, file);
         
         return true;
     }
 
     @Override
     public void dropActionChanged(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     /**
      *
      */
     public final class ErrorsAndFatalsCounterLogTarget implements LogTarget, Clearable {
         public AtomicInteger errorOrFatal = new AtomicInteger(0);
 
         @Override
         public void processEvent(LogEvent event) {
             if(event.getPriority().equals(Priority.ERROR) ||
                     event.getPriority().equals(Priority.FATAL_ERROR)) {
                 final int newValue = errorOrFatal.incrementAndGet();
                 SwingUtilities.invokeLater(new Runnable() {
                     @Override
                     public void run() {
                         errorsOrFatalsLabel.setText(Integer.toString(newValue));
                     }
                 });
             }
         }
 
         @Override
         public void clearData() {
             errorOrFatal.set(0);
             SwingUtilities.invokeLater(new Runnable() {
                 @Override
                 public void run() {
                     errorsOrFatalsLabel.setText(Integer.toString(errorOrFatal.get()));
                 }
             });
         }
     }
 
 
     @Override
     public void clearData() {
         logPanel.clear();
         if(DISPLAY_ERROR_FATAL_COUNTER) {
             errorsAndFatalsCounterLogTarget.clearData();
         }
     }
 
     /**
      * Handles click on warnIndicator
      */
     @Override
     public void actionPerformed(ActionEvent event) {
         if(event.getSource()==warnIndicator) {
             ActionRouter.getInstance().doActionNow(new ActionEvent(event.getSource(), event.getID(), ActionNames.LOGGER_PANEL_ENABLE_DISABLE));
         }
     }
 
     /**
      * Define AWT window title (WM_CLASS string) (useful on Gnome 3 / Linux)
      */
     private void setWindowTitle() {
         Class<?> xtoolkit = Toolkit.getDefaultToolkit().getClass();
         if (xtoolkit.getName().equals("sun.awt.X11.XToolkit")) { // $NON-NLS-1$
             try {
                 final Field awtAppClassName = xtoolkit.getDeclaredField("awtAppClassName"); // $NON-NLS-1$
                 awtAppClassName.setAccessible(true);
                 awtAppClassName.set(null, DEFAULT_APP_NAME);
             } catch (NoSuchFieldException nsfe) {
                 log.warn("Error awt title: " + nsfe); // $NON-NLS-1$
             } catch (IllegalAccessException iae) {
                 log.warn("Error awt title: " + iae); // $NON-NLS-1$
             }
        }
     }
 
     /**
      * Update Undo/Redo icons state
+     * 
      * @param canUndo
+     *            Flag whether the undo button should be enabled
      * @param canRedo
+     *            Flag whether the redo button should be enabled
      */
     public void updateUndoRedoIcons(boolean canUndo, boolean canRedo) {
         toolbar.updateUndoRedoIcons(canUndo, canRedo);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/ActionRouter.java b/src/core/org/apache/jmeter/gui/action/ActionRouter.java
index e6fd05759..6e6d5ed50 100644
--- a/src/core/org/apache/jmeter/gui/action/ActionRouter.java
+++ b/src/core/org/apache/jmeter/gui/action/ActionRouter.java
@@ -1,306 +1,338 @@
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
 
+    /**
+     * Get the set of {@link Command}s registered under the name
+     * <code>actionName</code>
+     * 
+     * @param actionName
+     *            The name the {@link Command}s were registered
+     * @return a set with all registered {@link Command}s for
+     *         <code>actionName</code>
+     */
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
 
+    /**
+     * Get the {@link Command} registered under the name <code>actionName</code>,
+     * that is of {@link Class} <code>actionClass</code>
+     * 
+     * @param actionName
+     *            The name the {@link Command}s were registered
+     * @param actionClass
+     *            The class the {@link Command}s should be equal to
+     * @return The registered {@link Command} for <code>actionName</code>, or
+     *         <code>null</code> if none could be found
+     */
     public Command getAction(String actionName, Class<?> actionClass) {
         for (Command com : commands.get(actionName)) {
             if (com.getClass().equals(actionClass)) {
                 return com;
             }
         }
         return null;
     }
 
+    /**
+     * Get the {@link Command} registered under the name <code>actionName</code>
+     * , which class names are equal to <code>className</code>
+     * 
+     * @param actionName
+     *            The name the {@link Command}s were registered
+     * @param className
+     *            The name of the class the {@link Command}s should be equal to
+     * @return The {@link Command} for <code>actionName</code> or
+     *         <code>null</code> if none could be found
+     */
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
+     *            The {@link ActionListener} to be registered
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
-     * @param listener
+     * @param listener The {@link ActionListener} that should be deregistered
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
                 for (ActionListener listener : listeners) {
                     listener.actionPerformed(e);
                 }
             }
         }
     }
 
     protected void postActionPerformed(Class<? extends Command> action, ActionEvent e) {
         if (action != null) {
             Set<ActionListener> listenerSet = postActionListeners.get(action.getName());
             if (listenerSet != null && listenerSet.size() > 0) {
                 ActionListener[] listeners = listenerSet.toArray(new ActionListener[listenerSet.size()]);
                 for (ActionListener listener : listeners) {
                     listener.actionPerformed(e);
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
                     null, // contains - classname should contain this string
                     // Ignore the classes which are specific to the reporting tool
                     "org.apache.jmeter.report.gui", // $NON-NLS-1$ // notContains - classname should not contain this string
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
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java
index 7c9aa3fa2..43bc43665 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeListener.java
@@ -1,237 +1,247 @@
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
 
 import java.awt.Container;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.InputEvent;
 import java.awt.event.KeyEvent;
 import java.awt.event.KeyListener;
 import java.awt.event.MouseEvent;
 import java.awt.event.MouseListener;
 
 import javax.swing.JPopupMenu;
 import javax.swing.JTree;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.MainFrame;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class JMeterTreeListener implements TreeSelectionListener, MouseListener, KeyListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private TreePath currentPath;
 
     private ActionListener actionHandler;
 
     private JMeterTreeModel model;
 
     private JTree tree;
 
     /**
      * Constructor for the JMeterTreeListener object.
+     * 
+     * @param model
+     *            The {@link JMeterTreeModel} for this listener
      */
     public JMeterTreeListener(JMeterTreeModel model) {
         this.model = model;
     }
 
+    /**
+     * Constructor for the {@link JMeterTreeListener} object
+     */
     public JMeterTreeListener() {
     }
 
+    /**
+     * Set the {@link JMeterTreeModel} for this listener
+     * @param m The {@link JMeterTreeModel} to be used
+     */
     public void setModel(JMeterTreeModel m) {
         model = m;
     }
 
     /**
      * Sets the ActionHandler attribute of the JMeterTreeListener object.
      *
      * @param ah
      *            the new ActionHandler value
      */
     public void setActionHandler(ActionListener ah) {
         actionHandler = ah;
     }
 
     /**
      * Sets the JTree attribute of the JMeterTreeListener object.
      *
      * @param tree
      *            the new JTree value
      */
     public void setJTree(JTree tree) {
         this.tree = tree;
     }
 
     /**
      * Sets the EndWindow attribute of the JMeterTreeListener object.
      *
      * @param window
      *            the new EndWindow value
      */
     public void setEndWindow(Container window) {
         // endWindow = window;
     }
 
     /**
      * Gets the JTree attribute of the JMeterTreeListener object.
      *
      * @return tree the current JTree value.
      */
     public JTree getJTree() {
         return tree;
     }
 
     /**
      * Gets the CurrentNode attribute of the JMeterTreeListener object.
      *
      * @return the CurrentNode value
      */
     public JMeterTreeNode getCurrentNode() {
         if (currentPath != null) {
             if (currentPath.getLastPathComponent() != null) {
                 return (JMeterTreeNode) currentPath.getLastPathComponent();
             }
             return (JMeterTreeNode) currentPath.getParentPath().getLastPathComponent();
         }
         return (JMeterTreeNode) model.getRoot();
     }
 
     public JMeterTreeNode[] getSelectedNodes() {
         TreePath[] paths = tree.getSelectionPaths();
         if (paths == null) {
             return new JMeterTreeNode[] { getCurrentNode() };
         }
         JMeterTreeNode[] nodes = new JMeterTreeNode[paths.length];
         for (int i = 0; i < paths.length; i++) {
             nodes[i] = (JMeterTreeNode) paths[i].getLastPathComponent();
         }
 
         return nodes;
     }
 
     public TreePath removedSelectedNode() {
         currentPath = currentPath.getParentPath();
         return currentPath;
     }
 
     @Override
     public void valueChanged(TreeSelectionEvent e) {
         log.debug("value changed, updating currentPath");
         currentPath = e.getNewLeadSelectionPath();
         // Call requestFocusInWindow to ensure current component loses focus and
         // all values are correctly saved
         // see https://issues.apache.org/bugzilla/show_bug.cgi?id=55103
         // see https://issues.apache.org/bugzilla/show_bug.cgi?id=55459
         tree.requestFocusInWindow();
         actionHandler.actionPerformed(new ActionEvent(this, 3333, ActionNames.EDIT)); // $NON-NLS-1$
     }
 
     @Override
     public void mouseClicked(MouseEvent ev) {
     }
 
     @Override
     public void mouseReleased(MouseEvent e) {
         GuiPackage.getInstance().getMainFrame().repaint();
     }
 
     @Override
     public void mouseEntered(MouseEvent e) {
     }
 
     @Override
     public void mousePressed(MouseEvent e) {
         // Get the Main Frame.
         MainFrame mainFrame = GuiPackage.getInstance().getMainFrame();
         // Close any Main Menu that is open
         mainFrame.closeMenu();
         int selRow = tree.getRowForLocation(e.getX(), e.getY());
         if (tree.getPathForLocation(e.getX(), e.getY()) != null) {
             log.debug("mouse pressed, updating currentPath");
             currentPath = tree.getPathForLocation(e.getX(), e.getY());
         }
         if (selRow != -1) {
             if (isRightClick(e)) {
                 if (tree.getSelectionCount() < 2) {
                     tree.setSelectionPath(currentPath);
                 }
                 log.debug("About to display pop-up");
                 displayPopUp(e);
             }
         }
     }
 
     @Override
     public void mouseExited(MouseEvent ev) {
     }
 
     @Override
     public void keyPressed(KeyEvent e) {
         String actionName = null;
 
         if (KeyStrokes.matches(e, KeyStrokes.COPY)) {
             actionName = ActionNames.COPY;
         } else if (KeyStrokes.matches(e, KeyStrokes.PASTE)) {
             actionName = ActionNames.PASTE;
         } else if (KeyStrokes.matches(e, KeyStrokes.CUT)) {
             actionName = ActionNames.CUT;
         } else if (KeyStrokes.matches(e, KeyStrokes.DUPLICATE)) {
             actionName = ActionNames.DUPLICATE;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_UP_ARROW)) {
             actionName = ActionNames.MOVE_UP;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_DOWN_ARROW)) {
             actionName = ActionNames.MOVE_DOWN;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_LEFT_ARROW)) {
             actionName = ActionNames.MOVE_LEFT;
         } else if (KeyStrokes.matches(e, KeyStrokes.ALT_RIGHT_ARROW)) {
             actionName = ActionNames.MOVE_RIGHT;
         } 
         
         if (actionName != null) {
             final ActionRouter actionRouter = ActionRouter.getInstance();
             actionRouter.doActionNow(new ActionEvent(e.getSource(), e.getID(), actionName));
             e.consume();
         }
     }
 
     @Override
     public void keyReleased(KeyEvent e) {
     }
 
     @Override
     public void keyTyped(KeyEvent e) {
     }
 
     private boolean isRightClick(MouseEvent e) {
         return e.isPopupTrigger() || (InputEvent.BUTTON2_MASK & e.getModifiers()) > 0 || (InputEvent.BUTTON3_MASK == e.getModifiers());
     }
 
     private void displayPopUp(MouseEvent e) {
         JPopupMenu pop = getCurrentNode().createPopupMenu();
         GuiPackage.getInstance().displayPopUp(e, pop);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/FilePanelEntry.java b/src/core/org/apache/jmeter/gui/util/FilePanelEntry.java
index 3e236093b..2597ac9fb 100644
--- a/src/core/org/apache/jmeter/gui/util/FilePanelEntry.java
+++ b/src/core/org/apache/jmeter/gui/util/FilePanelEntry.java
@@ -1,151 +1,151 @@
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
 
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.util.JMeterUtils;
 
 public class FilePanelEntry extends HorizontalPanel implements ActionListener {
     private static final long serialVersionUID = 280L;
 
     private final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 10); //$NON-NLS-1$
 
     private final JTextField filename = new JTextField(10);
 
     private final JLabel label;
 
     private final JButton browse = new JButton(JMeterUtils.getResString("browse")); //$NON-NLS-1$
 
     private static final String ACTION_BROWSE = "browse"; //$NON-NLS-1$
 
     private final List<ChangeListener> listeners = new LinkedList<ChangeListener>();
 
     private final String[] filetypes;
 
     // Mainly needed for unit test Serialisable tests
     public FilePanelEntry() {
         this(JMeterUtils.getResString("file_visualizer_filename")); //$NON-NLS-1$
     }
 
     public FilePanelEntry(String label) {
         this(label, (ChangeListener) null);
     }
 
     public FilePanelEntry(String label, String ... exts) {
         this(label, (ChangeListener) null, exts);
     }
 
     public FilePanelEntry(String label, ChangeListener listener, String ... exts) {
         this.label = new JLabel(label);
         if (listener != null) {
             listeners.add(listener);
         }
         if (exts != null) {
             this.filetypes = new String[exts.length];
             System.arraycopy(exts, 0, this.filetypes, 0, exts.length);
         } else {
             this.filetypes = null;
         }
         init();
     }
 
     public final void addChangeListener(ChangeListener l) {
         listeners.add(l);
     }
 
     private void init() {
         add(label);
         add(filename);
         filename.addActionListener(this);
         browse.setFont(FONT_SMALL);
         add(browse);
         browse.setActionCommand(ACTION_BROWSE);
         browse.addActionListener(this);
 
     }
 
     public void clearGui(){
         filename.setText(""); // $NON-NLS-1$
     }
 
     /**
      * If the gui needs to enable/disable the FilePanel, call the method.
      *
-     * @param enable
+     * @param enable The Flag whether the {@link FilePanel} should be enabled
      */
     public void enableFile(boolean enable) {
         browse.setEnabled(enable);
         filename.setEnabled(enable);
     }
 
     /**
      * Gets the filename attribute of the FilePanel object.
      *
      * @return the filename value
      */
     public String getFilename() {
         return filename.getText();
     }
 
     /**
      * Sets the filename attribute of the FilePanel object.
      *
      * @param f
      *            the new filename value
      */
     public void setFilename(String f) {
         filename.setText(f);
     }
 
     private void fireFileChanged() {
         for (ChangeListener cl : listeners) {
             cl.stateChanged(new ChangeEvent(this));
         }
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals(ACTION_BROWSE)) {
             JFileChooser chooser;
             if(filetypes == null || filetypes.length == 0){
                 chooser = FileDialoger.promptToOpenFile(filename.getText());
             } else {
                 chooser = FileDialoger.promptToOpenFile(filetypes, filename.getText());
             }
             if (chooser != null && chooser.getSelectedFile() != null) {
                 filename.setText(chooser.getSelectedFile().getPath());
                 fireFileChanged();
             }
         } else {
             fireFileChanged();
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JDateField.java b/src/core/org/apache/jmeter/gui/util/JDateField.java
index af7bf7706..f4405369e 100644
--- a/src/core/org/apache/jmeter/gui/util/JDateField.java
+++ b/src/core/org/apache/jmeter/gui/util/JDateField.java
@@ -1,203 +1,211 @@
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
 
 import java.awt.event.FocusEvent;
 import java.awt.event.FocusListener;
 import java.awt.event.KeyAdapter;
 import java.awt.event.KeyEvent;
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 
 import javax.swing.JTextField;
 
 /**
  * This is Date mask control. Using this control we can pop up our date in the
  * text field. And this control is Devloped basically for JDK1.3 and lower
  * version support. This control is similer to JSpinner control this is
  * available in JDK1.4 and above only.
  * <p>
  * This will set the date "yyyy/MM/dd HH:mm:ss" in this format only.
  * </p>
  *
  */
 public class JDateField extends JTextField {
 
     private static final long serialVersionUID = 240L;
 
     // Datefields are not thread-safe
     private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // $NON-NLS-1$
 
     /*
      * The following array must agree with dateFormat
      *
      * It is used to translate the positions in the buffer to the values used by
      * the Calendar class for the field id.
      *
      * Current format: MM/DD/YYYY HH:MM:SS 01234567890123456789 ^buffer
      * positions
      */
     private static final int fieldPositions[] = {
             Calendar.YEAR, // Y
             Calendar.YEAR, // Y
             Calendar.YEAR, // Y
             Calendar.YEAR, // Y
             Calendar.YEAR, // sp
             Calendar.MONTH, // M
             Calendar.MONTH, // M
             Calendar.MONTH, // /
             Calendar.DAY_OF_MONTH, // D
             Calendar.DAY_OF_MONTH, // D
             Calendar.DAY_OF_MONTH, // /
             Calendar.HOUR_OF_DAY, // H
             Calendar.HOUR_OF_DAY, // H
             Calendar.HOUR_OF_DAY, // :
             Calendar.MINUTE, // M
             Calendar.MINUTE, // M
             Calendar.MINUTE, // :
             Calendar.SECOND, // S
             Calendar.SECOND, // S
             Calendar.SECOND // end
     };
 
     /**
      * Create a DateField with the specified date.
+     * 
+     * @param date
+     *            The {@link Date} to be used
      */
     public JDateField(Date date) {
         super(20);
         this.addKeyListener(new KeyFocus());
         this.addFocusListener(new FocusClass());
         String myString = dateFormat.format(date);
         setText(myString);
     }
 
     // Dummy constructor to allo JUnit tests to work
     public JDateField() {
         this(new Date());
     }
 
     /**
      * Set the date to the Date mask control.
+     * 
+     * @param date
+     *            The {@link Date} to be set
      */
     public void setDate(Date date) {
         setText(dateFormat.format(date));
     }
 
     /**
      * Get the date from the Date mask control.
+     * 
+     * @return The currently set date
      */
     public Date getDate() {
         try {
             return dateFormat.parse(getText());
         } catch (ParseException e) {
             return new Date();
         } catch (Exception e) {
             // DateFormat.parse has some bugs (up to JDK 1.4.2) by which it
             // throws unchecked exceptions. E.g. see:
             // http://developer.java.sun.com/developer/bugParade/bugs/4699765.html
             //
             // To avoid problems with such situations, we'll catch all
             // exceptions here and act just as for ParseException above:
             return new Date();
         }
     }
 
     /*
      * Convert position in buffer to Calendar type Assumes that pos >=0 (which
      * is true for getCaretPosition())
      */
     private static int posToField(int pos) {
         if (pos >= fieldPositions.length) { // if beyond the end
             pos = fieldPositions.length - 1; // then set to the end
         }
         return fieldPositions[pos];
     }
 
     /**
      * Converts a date/time to a calendar using the defined format
      */
     private Calendar parseDate(String datetime) {
         Calendar c = Calendar.getInstance();
         try {
             Date dat = dateFormat.parse(datetime);
             c.setTime(dat);
         } catch (ParseException e) {
             // Do nothing; the current time will be returned
         }
         return c;
     }
 
     /*
      * Update the current field. The addend is only expected to be +1/-1, but
      * other values will work. N.B. the roll() method only supports changes by a
      * single unit - up or down
      */
     private void update(int addend, boolean shifted) {
         Calendar c = parseDate(getText());
         int pos = getCaretPosition();
         int field = posToField(pos);
         if (shifted) {
             c.roll(field, true);
         } else {
             c.add(field, addend);
         }
         String newDate = dateFormat.format(c.getTime());
         setText(newDate);
         if (pos > newDate.length()) {
             pos = newDate.length();
         }
         setCaretPosition(pos);// Restore position
 
     }
 
     class KeyFocus extends KeyAdapter {
         KeyFocus() {
         }
 
         @Override
         public void keyPressed(KeyEvent e) {
             if (e.getKeyCode() == KeyEvent.VK_UP) {
                 update(1, e.isShiftDown());
             } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                 update(-1, e.isShiftDown());
             }
         }
     }
 
     class FocusClass implements FocusListener {
         FocusClass() {
         }
 
         @Override
         public void focusGained(FocusEvent e) {
         }
 
         @Override
         public void focusLost(FocusEvent e) {
             try {
                 dateFormat.parse(getText());
             } catch (ParseException e1) {
                 requestFocusInWindow();
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JLabeledRadioI18N.java b/src/core/org/apache/jmeter/gui/util/JLabeledRadioI18N.java
index c909232e8..0f3edfab4 100644
--- a/src/core/org/apache/jmeter/gui/util/JLabeledRadioI18N.java
+++ b/src/core/org/apache/jmeter/gui/util/JLabeledRadioI18N.java
@@ -1,226 +1,226 @@
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
 
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.AbstractButton;
 import javax.swing.ButtonGroup;
 import javax.swing.ButtonModel;
 import javax.swing.JComponent;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledField;
 
 /**
  * JLabeledRadioI18N creates a set of Radio buttons with a label.
  * This is a version of the original JLabelledRadio class (now removed), but modified
  * to accept resource names rather than language strings.
  *
  */
 public class JLabeledRadioI18N extends JPanel implements JLabeledField, ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private final JLabel mLabel = new JLabel();
 
     private final ButtonGroup bGroup = new ButtonGroup();
 
     private final ArrayList<ChangeListener> mChangeListeners = new ArrayList<ChangeListener>(3);
 
     /**
      *
      * @param label_resouce text resource name for group label
      * @param item_resources list of resource names for individual buttons
      * @param selectedItem button to be selected (if not null)
      */
     public JLabeledRadioI18N(String label_resouce, String[] item_resources, String selectedItem) {
         setLabel(label_resouce);
         init(item_resources, selectedItem);
     }
 
     /**
      * @deprecated - only for use in testing
      */
     @Deprecated
     public JLabeledRadioI18N() {
         super();
     }
 
     /**
      * Method is responsible for creating the JRadioButtons and adding them to
      * the ButtonGroup.
      *
      * The resource name is used as the action command for the button model,
      * and the resource value is used to set the button label.
      *
      * @param resouces list of resource names
      * @param selected initially selected resource (if not null)
      *
      */
     private void init(String[] resouces, String selected) {
         this.add(mLabel);
         initButtonGroup(resouces, selected);
     }
 
     /**
      * Method is responsible for creating the JRadioButtons and adding them to
      * the ButtonGroup.
      *
      * The resource name is used as the action command for the button model,
      * and the resource value is used to set the button label.
      *
      * @param resouces list of resource names
      * @param selected initially selected resource (if not null)
      *
      */
     private void initButtonGroup(String[] resouces, String selected) {
         for (int idx = 0; idx < resouces.length; idx++) {
             JRadioButton btn = new JRadioButton(JMeterUtils.getResString(resouces[idx]));
             btn.setActionCommand(resouces[idx]);
             btn.addActionListener(this);
             // add the button to the button group
             this.bGroup.add(btn);
             // add the button
             this.add(btn);
             if (selected != null && selected.equals(resouces[idx])) {
                 btn.setSelected(true);
             }
         }
     }
     
     /**
      * Method is responsible for removing current JRadioButtons of ButtonGroup and
      * add creating the JRadioButtons and adding them to
      * the ButtonGroup.
      *
      * The resource name is used as the action command for the button model,
      * and the resource value is used to set the button label.
      *
      * @param resouces list of resource names
      * @param selected initially selected resource (if not null)
      *
      */
     public void resetButtons(String[] resouces, String selected) {
         Enumeration<AbstractButton> buttons = bGroup.getElements();
         List<AbstractButton> buttonsToRemove = new ArrayList<AbstractButton>(this.bGroup.getButtonCount());
         while (buttons.hasMoreElements()) {
             AbstractButton abstractButton = buttons
                     .nextElement();
             buttonsToRemove.add(abstractButton);
         }
         for (AbstractButton abstractButton : buttonsToRemove) {
             abstractButton.removeActionListener(this);
             bGroup.remove(abstractButton);
         }
         for (AbstractButton abstractButton : buttonsToRemove) {
             this.remove(abstractButton);
         }
         initButtonGroup(resouces, selected);
     }
 
     /**
      * The implementation will get the resource name from the selected radio button
      * in the JButtonGroup.
      */
     @Override
     public String getText() {
         return this.bGroup.getSelection().getActionCommand();
     }
 
     /**
      * The implementation will iterate through the radio buttons and find the
      * match. It then sets it to selected and sets all other radio buttons as
      * not selected.
      * @param resourcename name of resource whose button is to be selected
      */
     @Override
     public void setText(String resourcename) {
         Enumeration<AbstractButton> en = this.bGroup.getElements();
         while (en.hasMoreElements()) {
             ButtonModel model = en.nextElement().getModel();
             if (model.getActionCommand().equals(resourcename)) {
                 this.bGroup.setSelected(model, true);
             } else {
                 this.bGroup.setSelected(model, false);
             }
         }
     }
 
     /**
      * Set the group label from the resource name.
      *
-     * @param label_resource
+     * @param label_resource The text to be looked up and set
      */
     @Override
     public final void setLabel(String label_resource) {
         this.mLabel.setText(JMeterUtils.getResString(label_resource));
     }
 
     /** {@inheritDoc} */
     @Override
     public void addChangeListener(ChangeListener pChangeListener) {
         this.mChangeListeners.add(pChangeListener);
     }
 
     /**
      * Notify all registered change listeners that the text in the text field
      * has changed.
      */
     private void notifyChangeListeners() {
         ChangeEvent ce = new ChangeEvent(this);
         for (int index = 0; index < mChangeListeners.size(); index++) {
             mChangeListeners.get(index).stateChanged(ce);
         }
     }
 
     /**
      * Method will return all the label and JRadioButtons. ButtonGroup is
      * excluded from the list.
      */
     @Override
     public List<JComponent> getComponentList() {
         List<JComponent> comps = new LinkedList<JComponent>();
         comps.add(mLabel);
         Enumeration<AbstractButton> en = this.bGroup.getElements();
         while (en.hasMoreElements()) {
             comps.add(en.nextElement());
         }
         return comps;
     }
 
     /**
      * When a radio button is clicked, an ActionEvent is triggered.
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         this.notifyChangeListeners();
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index 3f895d0cd..6f06969d3 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,847 +1,849 @@
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
 
     private JMenuItem file_save_as;
 
     private JMenuItem file_selection_as;
 
     private JMenuItem file_selection_as_test_fragment;
 
     private JMenuItem file_revert;
 
     private JMenuItem file_load;
 
     private JMenuItem templates;
 
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
 
     private ArrayList<MenuCreator> menuCreators;
 
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
         this.menuCreators = new ArrayList<MenuCreator>();
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
         for (Iterator<MenuCreator> iterator = menuCreators.iterator(); iterator.hasNext();) {
             MenuCreator menuCreator = iterator.next();
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
 
         help_about = makeMenuItemRes("about", 'A', ActionNames.ABOUT); //$NON-NLS-1$
 
         helpMenu.add(contextHelp);
         helpMenu.addSeparator();
         helpMenu.add(whatClass);
         helpMenu.add(setDebug);
         helpMenu.add(resetDebug);
         helpMenu.add(heapDump);
 
         addPluginsMenuItems(helpMenu, menuCreators, MENU_LOCATION.HELP);
 
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
 
         JMenuItem file_save = makeMenuItemRes("save", 'S', ActionNames.SAVE, KeyStrokes.SAVE); //$NON-NLS-1$
         file_save.setEnabled(true);
 
         file_save_as = makeMenuItemRes("save_all_as", 'A', ActionNames.SAVE_ALL_AS, KeyStrokes.SAVE_ALL_AS); //$NON-NLS-1$
         file_save_as.setEnabled(true);
 
         file_selection_as = makeMenuItemRes("save_as", ActionNames.SAVE_AS); //$NON-NLS-1$
         file_selection_as.setEnabled(true);
 
         file_selection_as_test_fragment = makeMenuItemRes("save_as_test_fragment", ActionNames.SAVE_AS_TEST_FRAGMENT); //$NON-NLS-1$
         file_selection_as_test_fragment.setEnabled(true);
 
         file_revert = makeMenuItemRes("revert_project", 'R', ActionNames.REVERT_PROJECT); //$NON-NLS-1$
         file_revert.setEnabled(false);
 
         file_load = makeMenuItemRes("menu_open", 'O', ActionNames.OPEN, KeyStrokes.OPEN); //$NON-NLS-1$
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         file_load.setEnabled(false);
 
         templates = makeMenuItemRes("template_menu", 'T', ActionNames.TEMPLATES); //$NON-NLS-1$
         templates.setEnabled(true);
 
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
         fileMenu.add(templates);
         fileMenu.add(file_merge);
         fileMenu.addSeparator();
         fileMenu.add(file_save);
         fileMenu.add(file_save_as);
         fileMenu.add(file_selection_as);
         fileMenu.add(file_selection_as_test_fragment);
         fileMenu.add(file_revert);
         fileMenu.addSeparator();
         // Add the recent files, which will also add a separator that is
         // visible when needed
         file_load_recent_files = LoadRecentProject.getRecentFileMenuItems();
         for(JComponent jc : file_load_recent_files){
             fileMenu.add(jc);
         }
 
         addPluginsMenuItems(fileMenu, menuCreators, MENU_LOCATION.FILE);
 
         fileMenu.add(file_exit);
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
         for (MenuCreator creator : menuCreators) {
             creator.localeChanged();
         }
     }
 
     /**
      * Get a list of all installed LAFs plus CrossPlatform and System.
+     * 
+     * @return The list of available {@link LookAndFeelInfo}s
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
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java b/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
index bd02691d1..2bec82d1b 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterToolBar.java
@@ -1,275 +1,289 @@
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
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.Component;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JOptionPane;
 import javax.swing.JToolBar;
 
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * The JMeter main toolbar class
  *
  */
 public class JMeterToolBar extends JToolBar implements LocaleChangeListener {
     
     /**
      * 
      */
     private static final long serialVersionUID = -4591210341986068907L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String TOOLBAR_ENTRY_SEP = ",";  //$NON-NLS-1$
 
     private static final String TOOLBAR_PROP_NAME = "toolbar"; //$NON-NLS-1$
 
     // protected fields: JMeterToolBar class can be use to create another toolbar (plugin, etc.)    
     protected static final String DEFAULT_TOOLBAR_PROPERTY_FILE = "org/apache/jmeter/images/toolbar/icons-toolbar.properties"; //$NON-NLS-1$
 
     protected static final String USER_DEFINED_TOOLBAR_PROPERTY_FILE = "jmeter.toolbar.icons"; //$NON-NLS-1$
     
     private static final String TOOLBAR_LIST = "jmeter.toolbar";
     
     /**
      * Create the default JMeter toolbar
-     * @return the JMeter toolbar
+     * 
+     * @param visible
+     *            Flag whether toolbar should be visible
+     * @return the newly created {@link JMeterToolBar}
      */
     public static JMeterToolBar createToolbar(boolean visible) {
         JMeterToolBar toolBar = new JMeterToolBar();
         toolBar.setFloatable(false);
         toolBar.setVisible(visible);
 
         setupToolbarContent(toolBar);
         JMeterUtils.addLocaleChangeListener(toolBar);
         // implicit return empty toolbar if icons == null
         return toolBar;
     }
 
     /**
      * Setup toolbar content
      * @param toolBar {@link JMeterToolBar}
      */
     private static void setupToolbarContent(JMeterToolBar toolBar) {
         List<IconToolbarBean> icons = getIconMappings();
         if (icons != null) {
             for (IconToolbarBean iconToolbarBean : icons) {
                 if (iconToolbarBean == null) {
                     toolBar.addSeparator();
                 } else {
                     toolBar.add(makeButtonItemRes(iconToolbarBean));
                 }
             }
             toolBar.initButtonsState();
         }
     }
     
     /**
      * Generate a button component from icon bean
      * @param iconBean contains I18N key, ActionNames, icon path, optional icon path pressed
      * @return a button for toolbar
      */
     private static JButton makeButtonItemRes(IconToolbarBean iconBean) {
         final URL imageURL = JMeterUtils.class.getClassLoader().getResource(iconBean.getIconPath());
         JButton button = new JButton(new ImageIcon(imageURL));
         button.setToolTipText(JMeterUtils.getResString(iconBean.getI18nKey()));
         final URL imageURLPressed = JMeterUtils.class.getClassLoader().getResource(iconBean.getIconPathPressed());
         button.setPressedIcon(new ImageIcon(imageURLPressed));
         button.addActionListener(ActionRouter.getInstance());
         button.setActionCommand(iconBean.getActionNameResolve());
         return button;
     }
     
     /**
      * Parse icon set file.
      * @return List of icons/action definition
      */
     private static List<IconToolbarBean> getIconMappings() {
         // Get the standard toolbar properties
         Properties defaultProps = JMeterUtils.loadProperties(DEFAULT_TOOLBAR_PROPERTY_FILE);
         if (defaultProps == null) {
             JOptionPane.showMessageDialog(null, 
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
             return null;
         }
         Properties p;
         String userProp = JMeterUtils.getProperty(USER_DEFINED_TOOLBAR_PROPERTY_FILE); 
         if (userProp != null){
             p = JMeterUtils.loadProperties(userProp, defaultProps);
         } else {
             p=defaultProps;
         }
 
         String order = JMeterUtils.getPropDefault(TOOLBAR_LIST, p.getProperty(TOOLBAR_PROP_NAME));
 
         if (order == null) {
             log.warn("Could not find toolbar definition list");
             JOptionPane.showMessageDialog(null, 
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
             return null;
         }
 
         String[] oList = order.split(TOOLBAR_ENTRY_SEP);
 
         List<IconToolbarBean> listIcons = new ArrayList<IconToolbarBean>();
         for (String key : oList) {
             log.debug("Toolbar icon key: " + key); //$NON-NLS-1$
             String trimmed = key.trim();
             if (trimmed.equals("|")) { //$NON-NLS-1$
                 listIcons.add(null);
             } else {
                 String property = p.getProperty(trimmed);
                 if (property == null) {
                     log.warn("No definition for toolbar entry: " + key);
                 } else {
                     try {
                         IconToolbarBean itb = new IconToolbarBean(property);
                         listIcons.add(itb);
                     } catch (IllegalArgumentException e) {
                         // already reported by IconToolbarBean
                     }
                 }
             }
         }
         return listIcons;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void localeChanged(LocaleChangeEvent event) {
         Map<String, Boolean> currentButtonStates = getCurrentButtonsStates();
         this.removeAll();
         setupToolbarContent(this);
         updateButtons(currentButtonStates);
     }
 
     /**
      * 
      * @return Current state (enabled/disabled) of Toolbar button
      */
     private Map<String, Boolean> getCurrentButtonsStates() {
         Component[] components = getComponents();
         Map<String, Boolean> buttonStates = 
                 new HashMap<String, Boolean>(components.length);
         for (int i = 0; i < components.length; i++) {
             if(components[i]instanceof JButton) {
                 JButton button = (JButton) components[i];
                 buttonStates.put(button.getActionCommand(),  
                         Boolean.valueOf(button.isEnabled()));
             }
         }
         return buttonStates;
     }
 
     /**
      * Init the state of buttons
      */
     public void initButtonsState() {
         final boolean started = false;
         Map<String, Boolean> buttonStates = new HashMap<String, Boolean>();
         buttonStates.put(ActionNames.ACTION_START,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_START_NO_TIMERS,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_STOP,Boolean.valueOf(started));
         buttonStates.put(ActionNames.ACTION_SHUTDOWN,Boolean.valueOf(started));
         buttonStates.put(ActionNames.UNDO, Boolean.FALSE);
         buttonStates.put(ActionNames.REDO, Boolean.FALSE);
         buttonStates.put(ActionNames.REMOTE_START_ALL,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.REMOTE_STOP_ALL,Boolean.valueOf(started));
         buttonStates.put(ActionNames.REMOTE_SHUT_ALL,Boolean.valueOf(started));
         updateButtons(buttonStates);
     }
     
     
     /**
      * Change state of buttons on local test
+     * 
      * @param started
+     *            Flag whether local test is started
      */
     public void setLocalTestStarted(boolean started) {
         Map<String, Boolean> buttonStates = new HashMap<String, Boolean>(3);
         buttonStates.put(ActionNames.ACTION_START,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_START_NO_TIMERS,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.ACTION_STOP,Boolean.valueOf(started));
         buttonStates.put(ActionNames.ACTION_SHUTDOWN,Boolean.valueOf(started));
         updateButtons(buttonStates);
     }
     
     /**
      * Change state of buttons on remote test
+     * 
      * @param started
+     *            Flag whether the test is started
      */
     public void setRemoteTestStarted(boolean started) {
         Map<String, Boolean> buttonStates = new HashMap<String, Boolean>(3);
         buttonStates.put(ActionNames.REMOTE_START_ALL,Boolean.valueOf(!started));
         buttonStates.put(ActionNames.REMOTE_STOP_ALL,Boolean.valueOf(started));
         buttonStates.put(ActionNames.REMOTE_SHUT_ALL,Boolean.valueOf(started));
         updateButtons(buttonStates);
     }
 
     /**
      * Change state of buttons after undo or redo
+     * 
      * @param canUndo
+     *            Flag whether the button corresponding to
+     *            {@link ActionNames#UNDO} should be enabled
      * @param canRedo
+     *            Flag whether the button corresponding to
+     *            {@link ActionNames#REDO} should be enabled
      */
     public void updateUndoRedoIcons(boolean canUndo, boolean canRedo) {
         Map<String, Boolean> buttonStates = new HashMap<String, Boolean>(2);
         buttonStates.put(ActionNames.UNDO, Boolean.valueOf(canUndo));
         buttonStates.put(ActionNames.REDO, Boolean.valueOf(canRedo));
         updateButtons(buttonStates);
     }
 
     /**
+     * Set buttons to a given state
      * 
-     * @param buttonStates Map<String, Boolean>
+     * @param buttonStates
+     *            {@link Map} of button names and their states
      */
     private void updateButtons(Map<String, Boolean> buttonStates) {
         Component[] components = getComponents();
         for (int i = 0; i < components.length; i++) {
             if(components[i]instanceof JButton) {
                 JButton button = (JButton) components[i];
                 Boolean enabled = buttonStates.get(button.getActionCommand());
                 if(enabled != null) {
                     button.setEnabled(enabled.booleanValue());
                 }
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java b/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java
index 4e6c1669d..4c656aa21 100644
--- a/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java
+++ b/src/core/org/apache/jmeter/gui/util/JSyntaxTextArea.java
@@ -1,133 +1,142 @@
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
 
 import java.util.Properties;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
 import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
 import org.fife.ui.rtextarea.RUndoManager;
 
 /**
  * Utility class to handle RSyntaxTextArea code
  */
 public class JSyntaxTextArea extends RSyntaxTextArea {
 
     private static final long serialVersionUID = 210L;
 
     private final Properties languageProperties = JMeterUtils.loadProperties("org/apache/jmeter/gui/util/textarea.properties"); //$NON-NLS-1$;
 
     private final boolean disableUndo;
 
     private static final boolean WRAP_STYLE_WORD = JMeterUtils.getPropDefault("jsyntaxtextarea.wrapstyleword", true);
     private static final boolean LINE_WRAP       = JMeterUtils.getPropDefault("jsyntaxtextarea.linewrap", true);
     private static final boolean CODE_FOLDING    = JMeterUtils.getPropDefault("jsyntaxtextarea.codefolding", true);
     private static final int MAX_UNDOS           = JMeterUtils.getPropDefault("jsyntaxtextarea.maxundos", 50);
 
     @Deprecated
     public JSyntaxTextArea() {
         // For use by test code only
         this(30, 50, false);
     }
 
     /**
-     * Creates the default syntax highlighting text area.
-     * The following are set:
+     * Creates the default syntax highlighting text area. The following are set:
      * <ul>
      * <li>setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA)</li>
      * <li>setCodeFoldingEnabled(true)</li>
      * <li>setAntiAliasingEnabled(true)</li>
      * <li>setLineWrap(true)</li>
      * <li>setWrapStyleWord(true)</li>
      * </ul>
+     * 
      * @param rows
+     *            The number of rows for the text area
      * @param cols
+     *            The number of columns for the text area
      */
     public JSyntaxTextArea(int rows, int cols) {
         this(rows, cols, false);
     }
 
     /**
-     * Creates the default syntax highlighting text area.
-     * The following are set:
+     * Creates the default syntax highlighting text area. The following are set:
      * <ul>
      * <li>setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA)</li>
      * <li>setCodeFoldingEnabled(true)</li>
      * <li>setAntiAliasingEnabled(true)</li>
      * <li>setLineWrap(true)</li>
      * <li>setWrapStyleWord(true)</li>
      * </ul>
+     * 
      * @param rows
+     *            The number of rows for the text area
      * @param cols
-     * @param disableUndo true to disable undo manager, defaults to false
+     *            The number of columns for the text area
+     * @param disableUndo
+     *            true to disable undo manager, defaults to false
      */
     public JSyntaxTextArea(int rows, int cols, boolean disableUndo) {
         super(rows, cols);
         super.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
         super.setCodeFoldingEnabled(CODE_FOLDING);
         super.setAntiAliasingEnabled(true);
         super.setLineWrap(LINE_WRAP);
         super.setWrapStyleWord(WRAP_STYLE_WORD);
         this.disableUndo = disableUndo;
     }
 
     /**
      * Sets the language of the text area.
+     * 
      * @param language
+     *            The language to be set
      */
     public void setLanguage(String language) {
         if(language == null) {
           // TODO: Log a message?
           // But how to find the name of the offending GUI element in the case of a TestBean?
           super.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
         } else {
           final String style = languageProperties.getProperty(language);
           if (style == null) {
               super.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
           } else {
               super.setSyntaxEditingStyle(style);
           }
         }
     }
 
     /**
      * Override UndoManager to allow disabling if feature causes issues
      * See https://github.com/bobbylight/RSyntaxTextArea/issues/19
      */
     @Override
     protected RUndoManager createUndoManager() {
         RUndoManager undoManager = super.createUndoManager();
         if(disableUndo) {
             undoManager.setLimit(0);
         } else {
             undoManager.setLimit(MAX_UNDOS);
         }
         return undoManager;
     }
 
     /**
      * Sets initial text resetting undo history
+     * 
      * @param string
+     *            The initial text to be set
      */
     public void setInitialText(String string) {
         setText(string);
         discardAllEdits();
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/MenuFactory.java b/src/core/org/apache/jmeter/gui/util/MenuFactory.java
index 313278a40..feecbd007 100644
--- a/src/core/org/apache/jmeter/gui/util/MenuFactory.java
+++ b/src/core/org/apache/jmeter/gui/util/MenuFactory.java
@@ -1,739 +1,743 @@
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
 import java.awt.HeadlessException;
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Map.Entry;
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
 import org.apache.jorphan.gui.GuiUtils;
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
         menu.add(makeMenuItemRes("duplicate", ActionNames.DUPLICATE, KeyStrokes.DUPLICATE));  //$NON-NLS-1$
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
 
     public static void addFileMenu(JPopupMenu pop) {
         addFileMenu(pop, true);
     }
 
     /**
      * @param menu JPopupMenu
      * @param addSaveTestFragmentMenu Add Save as Test Fragment menu if true 
      */
     public static void addFileMenu(JPopupMenu menu, boolean addSaveTestFragmentMenu) {
         // the undo/redo as a standard goes first in Edit menus
         // maybe there's better place for them in JMeter?
         addUndoItems(menu);
 
         addSeparator(menu);
         menu.add(makeMenuItemRes("open", ActionNames.OPEN));// $NON-NLS-1$
         menu.add(makeMenuItemRes("menu_merge", ActionNames.MERGE));// $NON-NLS-1$
         menu.add(makeMenuItemRes("save_as", ActionNames.SAVE_AS));// $NON-NLS-1$
         if(addSaveTestFragmentMenu) {
             menu.add(makeMenuItemRes("save_as_test_fragment", ActionNames.SAVE_AS_TEST_FRAGMENT));// $NON-NLS-1$
         }
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
 
     /**
      * Add undo / redo
      * @param menu JPopupMenu
      */
     private static void addUndoItems(JPopupMenu menu) {
         addSeparator(menu);
 
         JMenuItem undo = makeMenuItemRes("undo", ActionNames.UNDO); //$NON-NLS-1$
         undo.setEnabled(GuiPackage.getInstance().canUndo());
         menu.add(undo);
 
         JMenuItem redo = makeMenuItemRes("redo", ActionNames.REDO); //$NON-NLS-1$
         // TODO: we could even show some hints on action being undone here if this will be required (by passing those hints into history  records)
         redo.setEnabled(GuiPackage.getInstance().canRedo());
         menu.add(redo);
     }
 
 
     public static JMenu makeMenus(String[] categories, String label, String actionCommand) {
         JMenu addMenu = new JMenu(label);
         for (int i = 0; i < categories.length; i++) {
             addMenu.add(makeMenu(categories[i], actionCommand));
         }
         GuiUtils.makeScrollableMenu(addMenu);
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
         pop.add(makeMenus(MENU_PARENT_CONTROLLER,
                 JMeterUtils.getResString("change_parent"),// $NON-NLS-1$
                 ActionNames.CHANGE_PARENT));
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
-     * @param menuName
+     * @param menuName The name of the newly created menu
      * @return the menu
      */
     public static JMenu makeMenu(Collection<MenuInfo> menuInfo, String actionCommand, String menuName) {
         JMenu menu = new JMenu(menuName);
         for (MenuInfo info : menuInfo) {
             menu.add(makeMenuItem(info, actionCommand));
         }
         GuiUtils.makeScrollableMenu(menu);
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
 
                 boolean hideBean = false; // Should the TestBean be hidden?
 
                 JMeterGUIComponent item;
                 try {
                     Class<?> c = Class.forName(name);
                     if (TestBean.class.isAssignableFrom(c)) {
                         TestBeanGUI tbgui = new TestBeanGUI(c);
                         hideBean = tbgui.isHidden() || (tbgui.isExpert() && !JMeterUtils.isExpertMode());
                         item = tbgui;
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
                         if (!(e instanceof HeadlessException)) { // Allow headless testing
                             throw (RuntimeException) e;
                         }
                     }
                     continue;
                 }
                 if (hideBean || elementsToSkip.contains(item.getStaticLabel())) {
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
-     * @param element - top-level test element to be added
-     *
+     *            The {@link JMeterTreeNode} to test, if a new element can be
+     *            added to it
+     * @param element
+     *            - top-level test element to be added
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
-     * @param nodes - array of nodes that are to be added
-     *
+     *            The {@link JMeterTreeNode} to test, if <code>nodes[]</code>
+     *            can be added to it
+     * @param nodes
+     *            - array of nodes that are to be added
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
             if (parent instanceof TestPlan) {
                 return true;
             }
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
         private final boolean caseBlind;
         MenuInfoComparator(boolean caseBlind){
             this.caseBlind = caseBlind;
         }
         @Override
         public int compare(MenuInfo o1, MenuInfo o2) {
             String lab1 = o1.getLabel();
             String lab2 = o2.getLabel();
             if (caseBlind) {
                 return lab1.toLowerCase(Locale.ENGLISH).compareTo(lab2.toLowerCase(Locale.ENGLISH));
             }
             return lab1.compareTo(lab2);
         }
     }
 
     /**
      * Sort loaded menus; all but THREADS are sorted case-blind.
      * [This is so Thread Group appears before setUp and tearDown]
      */
     private static void sortPluginMenus() {
         for(Entry<String, List<MenuInfo>> me : menuMap.entrySet()){
             Collections.sort(me.getValue(), new MenuInfoComparator(!me.getKey().equals(THREADS)));
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/TextAreaTableCellEditor.java b/src/core/org/apache/jmeter/gui/util/TextAreaTableCellEditor.java
index fd1202a17..7b5e04e72 100644
--- a/src/core/org/apache/jmeter/gui/util/TextAreaTableCellEditor.java
+++ b/src/core/org/apache/jmeter/gui/util/TextAreaTableCellEditor.java
@@ -1,327 +1,328 @@
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
 import java.awt.event.ActionEvent;
 import java.awt.event.FocusEvent;
 import java.awt.event.FocusListener;
 import java.awt.event.ItemEvent;
 import java.awt.event.MouseEvent;
 import java.io.Serializable;
 import java.util.EventObject;
 
 import javax.swing.AbstractCellEditor;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.JTextArea;
 import javax.swing.JTree;
 import javax.swing.table.TableCellEditor;
 import javax.swing.tree.TreeCellEditor;
 
 public class TextAreaTableCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
     private static final long serialVersionUID = 240L;
 
     //
     // Instance Variables
     //
 
     /** The Swing component being edited. */
     protected JTextArea editorComponent;
 
     /**
      * The delegate class which handles all methods sent from the
      * <code>CellEditor</code>.
      */
     protected EditorDelegate delegate;
 
     /**
      * An integer specifying the number of clicks needed to start editing. Even
      * if <code>clickCountToStart</code> is defined as zero, it will not
      * initiate until a click occurs.
      */
     protected int clickCountToStart = 1;
 
     //
     // Constructors
     //
 
     /**
      * Constructs a <code>TableCellEditor</code> that uses a text field.
      */
     public TextAreaTableCellEditor() {
         editorComponent = new JTextArea();
         editorComponent.setRows(3);
         this.clickCountToStart = 2;
         delegate = new EditorDelegate() {
             private static final long serialVersionUID = 240L;
 
             @Override
             public void setValue(Object value) {
                 editorComponent.setText((value != null) ? value.toString() : "");
             }
 
             @Override
             public Object getCellEditorValue() {
                 return editorComponent.getText();
             }
         };
         editorComponent.addFocusListener(delegate);
     }
 
     /**
      * Returns a reference to the editor component.
      *
      * @return the editor <code>Component</code>
      */
     public Component getComponent() {
         return editorComponent;
     }
 
     //
     // Modifying
     //
 
     /**
      * Specifies the number of clicks needed to start editing.
      *
      * @param count
      *            an int specifying the number of clicks needed to start editing
      * @see #getClickCountToStart
      */
     public void setClickCountToStart(int count) {
         clickCountToStart = count;
     }
 
     /**
      * Returns the number of clicks needed to start editing.
      *
      * @return the number of clicks needed to start editing
      */
     public int getClickCountToStart() {
         return clickCountToStart;
     }
 
     //
     // Override the implementations of the superclass, forwarding all methods
     // from the CellEditor interface to our delegate.
     //
 
     /**
      * Forwards the message from the <code>CellEditor</code> to the
      * <code>delegate</code>.
      *
      * @see EditorDelegate#getCellEditorValue
      */
     @Override
     public Object getCellEditorValue() {
         return delegate.getCellEditorValue();
     }
 
     /**
      * Forwards the message from the <code>CellEditor</code> to the
      * <code>delegate</code>.
      *
      * @see EditorDelegate#isCellEditable(EventObject)
      */
     @Override
     public boolean isCellEditable(EventObject anEvent) {
         return delegate.isCellEditable(anEvent);
     }
 
     /**
      * Forwards the message from the <code>CellEditor</code> to the
      * <code>delegate</code>.
      *
      * @see EditorDelegate#shouldSelectCell(EventObject)
      */
     @Override
     public boolean shouldSelectCell(EventObject anEvent) {
         return delegate.shouldSelectCell(anEvent);
     }
 
     /**
      * Forwards the message from the <code>CellEditor</code> to the
      * <code>delegate</code>.
      *
      * @see EditorDelegate#stopCellEditing
      */
     @Override
     public boolean stopCellEditing() {
         return delegate.stopCellEditing();
     }
 
     /**
      * Forwards the message from the <code>CellEditor</code> to the
      * <code>delegate</code>.
      *
      * @see EditorDelegate#cancelCellEditing
      */
     @Override
     public void cancelCellEditing() {
         delegate.cancelCellEditing();
     }
 
     //
     // Implementing the TreeCellEditor Interface
     //
 
     /** Implements the <code>TreeCellEditor</code> interface. */
     @Override
     public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
             boolean leaf, int row) {
         String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, false);
 
         delegate.setValue(stringValue);
         return new JScrollPane(editorComponent);
     }
 
     //
     // Implementing the CellEditor Interface
     //
     /** Implements the <code>TableCellEditor</code> interface. */
     @Override
     public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
         delegate.setValue(value);
         return new JScrollPane(editorComponent);
     }
 
     //
     // Protected EditorDelegate class
     //
 
     /**
      * The protected <code>EditorDelegate</code> class.
      */
     protected class EditorDelegate implements FocusListener, Serializable {
         private static final long serialVersionUID = 240L;
 
         /** The value of this cell. */
         protected Object value;
 
         /**
          * Returns the value of this cell.
          *
          * @return the value of this cell
          */
         public Object getCellEditorValue() {
             return value;
         }
 
         /**
          * Sets the value of this cell.
          *
          * @param value
          *            the new value of this cell
          */
         public void setValue(Object value) {
             this.value = value;
         }
 
         /**
          * Returns true if <code>anEvent</code> is <b>not</b> a
          * <code>MouseEvent</code>. Otherwise, it returns true if the
          * necessary number of clicks have occurred, and returns false
          * otherwise.
          *
          * @param anEvent
          *            the event
          * @return true if cell is ready for editing, false otherwise
          * @see #setClickCountToStart(int)
          * @see #shouldSelectCell
          */
         public boolean isCellEditable(EventObject anEvent) {
             if (anEvent instanceof MouseEvent) {
                 return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
             }
             return true;
         }
 
         /**
          * Returns true to indicate that the editing cell may be selected.
          *
          * @param anEvent
          *            the event
          * @return true
          * @see #isCellEditable
          */
         public boolean shouldSelectCell(EventObject anEvent) {
             return true;
         }
 
         /**
          * Returns true to indicate that editing has begun.
          *
          * @param anEvent
          *            the event
+         * @return always <code>true</code>
          */
         public boolean startCellEditing(EventObject anEvent) {
             return true;
         }
 
         /**
          * Stops editing and returns true to indicate that editing has stopped.
          * This method calls <code>fireEditingStopped</code>.
          *
          * @return true
          */
         public boolean stopCellEditing() {
             fireEditingStopped();
             return true;
         }
 
         /**
          * Cancels editing. This method calls <code>fireEditingCanceled</code>.
          */
         public void cancelCellEditing() {
             fireEditingCanceled();
         }
 
         /**
          * When an action is performed, editing is ended.
          *
          * @param e
          *            the action event
          * @see #stopCellEditing
          */
         public void actionPerformed(ActionEvent e) {
             TextAreaTableCellEditor.this.stopCellEditing();
         }
 
         /**
          * When an item's state changes, editing is ended.
          *
          * @param e
          *            the action event
          * @see #stopCellEditing
          */
         public void itemStateChanged(ItemEvent e) {
             TextAreaTableCellEditor.this.stopCellEditing();
         }
 
         @Override
         public void focusLost(FocusEvent ev) {
             TextAreaTableCellEditor.this.stopCellEditing();
         }
 
         @Override
         public void focusGained(FocusEvent ev) {
         }
     }
 }
