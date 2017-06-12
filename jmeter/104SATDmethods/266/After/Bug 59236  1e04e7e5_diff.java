diff --git a/src/core/org/apache/jmeter/gui/GuiPackage.java b/src/core/org/apache/jmeter/gui/GuiPackage.java
index e63de7b9b..db83f1a22 100644
--- a/src/core/org/apache/jmeter/gui/GuiPackage.java
+++ b/src/core/org/apache/jmeter/gui/GuiPackage.java
@@ -1,875 +1,854 @@
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
     private Map<TestElement, JMeterGUIComponent> nodesToGui = new HashMap<>();
 
     /**
      * Map from Class to JMeterGUIComponent, mapping the Class of a GUI
      * component to an instance of that component.
      */
     private Map<Class<?>, JMeterGUIComponent> guis = new HashMap<>();
 
     /**
      * Map from Class to TestBeanGUI, mapping the Class of a TestBean to an
      * instance of TestBeanGUI to be used to edit such components.
      */
     private Map<Class<?>, JMeterGUIComponent> testBeanGUIs = new HashMap<>();
 
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
 
-    /** The menu item toolbar. */
-    private JCheckBoxMenuItem menuToolBar;
-
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
             if (testClassName.isEmpty()) {
                 testClass = node.getClass();
             } else {
                 testClass = Class.forName(testClassName);
             }
             Class<?> guiClass = null;
             if (!guiClassName.isEmpty()) {
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
         } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
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
                 int before = 0;
                 int after = 0;
                 final boolean historyEnabled = undoHistory.isEnabled();
                 if(historyEnabled) {
                     before = getTestElementCheckSum(el);
                 }
                 comp.modifyTestElement(el);
                 if(historyEnabled) {
                     after = getTestElementCheckSum(el);
                 }
                 if (!historyEnabled || (before != after)) {
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
 
-    /**
-     * Set the menu item toolbar.
-     *
-     * @param newMenuToolBar
-     *            the new menu item toolbar
-     */
-    public void setMenuItemToolbar(JCheckBoxMenuItem newMenuToolBar) {
-        menuToolBar = newMenuToolBar;
-    }
-
-    /**
-     * Get the menu item  toolbar.
-     *
-     * @return the menu item toolbar
-     */
-    public JCheckBoxMenuItem getMenuItemToolbar() {
-        return menuToolBar;
-    }
 
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
         guis = new HashMap<>();
         nodesToGui = new HashMap<>();
         testBeanGUIs = new HashMap<>();
 
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
      *            The filepath of the current test plan
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
      * 
      * @param stoppable
      *            The {@link Stoppable} to be registered
      */
     public void register(Stoppable stoppable) {
         stoppables.add(stoppable);
     }
 
     /**
      *
      * @return copy of list of {@link Stoppable}s
      */
     public List<Stoppable> getStoppables() {
         List<Stoppable> list = new ArrayList<>();
         list.addAll(stoppables);
         return list;
     }
 
     /**
      * Set the menu item LoggerPanel.
      * @param menuItemLoggerPanel The menu item LoggerPanel
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
                 String stringValue = obj.getStringValue();
                 if(stringValue != null) {
                     ret ^= stringValue.hashCode();
                 } else {
                     if(log.isDebugEnabled()) {
                         log.debug("obj.getStringValue() returned null for test element:"
                                 +el.getName()+" at property:"+obj.getName());
                     }
                 }
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
index d0682566b..45c565d77 100644
--- a/src/core/org/apache/jmeter/gui/MainFrame.java
+++ b/src/core/org/apache/jmeter/gui/MainFrame.java
@@ -1,900 +1,899 @@
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
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Cursor;
 import java.awt.Dimension;
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
 
 import javax.swing.AbstractAction;
 import javax.swing.Action;
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.DropMode;
 import javax.swing.ImageIcon;
 import javax.swing.InputMap;
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
 import javax.swing.KeyStroke;
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
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.action.LoadDraggedFile;
 import org.apache.jmeter.gui.tree.JMeterCellRenderer;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.tree.JMeterTreeTransferHandler;
 import org.apache.jmeter.gui.util.EscapeDialog;
 import org.apache.jmeter.gui.util.JMeterMenuBar;
 import org.apache.jmeter.gui.util.JMeterToolBar;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
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
 
     // Allow display/hide LoggerPanel
     private static final boolean DISPLAY_LOGGER_PANEL =
             JMeterUtils.getPropDefault("jmeter.loggerpanel.display", false); // $NON-NLS-1$
 
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
 
     private final String iconSize = JMeterUtils.getPropDefault(JMeterToolBar.TOOLBAR_ICON_SIZE, JMeterToolBar.DEFAULT_TOOLBAR_ICON_SIZE); 
 
     /** An image which is displayed when a test is running. */
     private final ImageIcon runningIcon = JMeterUtils.getImage("status/" + iconSize +"/user-online-2.png");// $NON-NLS-1$
 
     /** An image which is displayed when a test is not currently running. */
     private final ImageIcon stoppedIcon = JMeterUtils.getImage("status/" + iconSize +"/user-offline-2.png");// $NON-NLS-1$
     
     /** An image which is displayed to indicate FATAL, ERROR or WARNING. */
     private final ImageIcon warningIcon = JMeterUtils.getImage("status/" + iconSize +"/pictogram-din-w000-general.png");// $NON-NLS-1$
 
     /** The button used to display the running/stopped image. */
     private JButton runningIndicator;
 
     /** The set of currently running hosts. */
     private final Set<String> hosts = new HashSet<>();
 
     /** A message dialog shown while JMeter threads are stopping. */
     private JDialog stoppingMessage;
 
     private JLabel totalThreads;
     private JLabel activeThreads;
 
     private JMeterToolBar toolbar;
 
     /**
      * Label at top right showing test duration
      */
     private JLabel testTimeDuration;
 
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
     
     private javax.swing.Timer computeTestDurationTimer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
         
         @Override
         public void actionPerformed(ActionEvent e) {
             computeTestDuration();
         }
     });
 
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
         runningIndicator.setFocusable(false);
         runningIndicator.setBorderPainted(false);
         runningIndicator.setContentAreaFilled(false);
         runningIndicator.setMargin(new Insets(0, 0, 0, 0));
 
         testTimeDuration = new JLabel("00:00:00"); //$NON-NLS-1$
         testTimeDuration.setToolTipText(JMeterUtils.getResString("duration_tooltip")); //$NON-NLS-1$
         testTimeDuration.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
 
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
 
     protected void computeTestDuration() {
         long startTime = JMeterContextService.getTestStartTime();
         if (startTime > 0) {
             long elapsedSec = (System.currentTimeMillis()-startTime + 500) / 1000; // rounded seconds
             testTimeDuration.setText(JOrphanUtils.formatDuration(elapsedSec));
         }
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
                 for (MenuElement element : menuElement) {
                     JMenu menu = (JMenu) element;
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
         if (!StringUtils.isEmpty(host)) {
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
                 if (stoppingMessage != null) { // TODO - how can this be null?
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
         computeTestDurationTimer.start();
         runningIndicator.setIcon(runningIcon);
         activeThreads.setText("0"); // $NON-NLS-1$
         totalThreads.setText("0"); // $NON-NLS-1$
         menuBar.setRunning(true, host);
         if (LOCAL.equals(host)) {
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
             computeTestDurationTimer.stop();
         }
         menuBar.setRunning(false, host);
         if (LOCAL.equals(host)) {
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
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
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
         errorsAndFatalsCounterLogTarget = new ErrorsAndFatalsCounterLogTarget();
         LoggingManager.addLogTargetToRootLogger(new LogTarget[]{
                 logPanel,
                 errorsAndFatalsCounterLogTarget
         });
 
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
         this.toolbar = JMeterToolBar.createToolbar(true);
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setMainToolbar(toolbar);
-        guiInstance.getMenuItemToolbar().getModel().setSelected(true);
         toolPanel.add(toolbar);
 
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(Box.createGlue());
 
         toolPanel.add(testTimeDuration);
         toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
 
         toolPanel.add(errorsOrFatalsLabel);
         toolPanel.add(warnIndicator);
         toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
 
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
 
         addQuickComponentHotkeys(treevar);
 
         return treevar;
     }
 
     private void addQuickComponentHotkeys(JTree treevar) {
         Action quickComponent = new AbstractAction("Quick Component") {
             private static final long serialVersionUID = 1L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 String propname = "gui.quick_" + actionEvent.getActionCommand();
                 String comp = JMeterUtils.getProperty(propname);
                 log.debug("Event " + propname + ": " + comp);
 
                 if (comp == null) {
                     log.warn("No component set through property: " + propname);
                     return;
                 }
 
                 GuiPackage guiPackage = GuiPackage.getInstance();
                 try {
                     guiPackage.updateCurrentNode();
                     TestElement testElement = guiPackage.createTestElement(SaveService.aliasToClass(comp));
                     JMeterTreeNode parentNode = guiPackage.getCurrentNode();
                     while (!MenuFactory.canAddTo(parentNode, testElement)) {
                         parentNode = (JMeterTreeNode) parentNode.getParent();
                     }
                     if (parentNode.getParent() == null) {
                         log.debug("Cannot add element on very top level");
                     } else {
                         JMeterTreeNode node = guiPackage.getTreeModel().addComponent(testElement, parentNode);
                         guiPackage.getMainFrame().getTree().setSelectionPath(new TreePath(node.getPath()));
                     }
                 } catch (Exception err) {
                     log.warn("Failed to perform quick component add: " + comp, err); // $NON-NLS-1$
                 }
             }
         };
 
         InputMap inputMap = treevar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         KeyStroke[] keyStrokes = new KeyStroke[]{KeyStrokes.CTRL_0,
                 KeyStrokes.CTRL_1, KeyStrokes.CTRL_2, KeyStrokes.CTRL_3,
                 KeyStrokes.CTRL_4, KeyStrokes.CTRL_5, KeyStrokes.CTRL_6,
                 KeyStrokes.CTRL_7, KeyStrokes.CTRL_8, KeyStrokes.CTRL_9,};
         for (int n = 0; n < keyStrokes.length; n++) {
             treevar.getActionMap().put(ActionNames.QUICK_COMPONENT + String.valueOf(n), quickComponent);
             inputMap.put(keyStrokes[n], ActionNames.QUICK_COMPONENT + String.valueOf(n));
         }
     }
 
     /**
      * Create the tree cell renderer used to draw the nodes in the test tree.
      *
      * @return a renderer to draw the test tree nodes
      */
     private TreeCellRenderer getCellRenderer() {
         DefaultTreeCellRenderer rend = new JMeterCellRenderer();
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
             for (DataFlavor flavor : flavors) {
                 // Check for file lists specifically
                 if (flavor.isFlavorJavaFileListType()) {
                     dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                     try {
                         openJmxFilesFromDragAndDrop(tr);
                     } finally {
                         dtde.dropComplete(true);
                     }
                     return;
                 }
             }
         } catch (UnsupportedFlavorException | IOException e) {
             log.warn("Dnd failed" , e);
         }
 
     }
 
     public boolean openJmxFilesFromDragAndDrop(Transferable tr) throws UnsupportedFlavorException, IOException {
         @SuppressWarnings("unchecked")
         List<File> files = (List<File>)
                 tr.getTransferData(DataFlavor.javaFileListFlavor);
         if (files.isEmpty()) {
             return false;
         }
         File file = files.get(0);
         if (!file.getName().endsWith(".jmx")) {
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
                         errorsOrFatalsLabel.setForeground(Color.RED);
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
                     errorsOrFatalsLabel.setForeground(Color.BLACK);
                     errorsOrFatalsLabel.setText(Integer.toString(errorOrFatal.get()));
                 }
             });
         }
     }
 
 
     @Override
     public void clearData() {
         logPanel.clear();
         errorsAndFatalsCounterLogTarget.clearData();
     }
 
     /**
      * Handles click on warnIndicator
      */
     @Override
     public void actionPerformed(ActionEvent event) {
         if (event.getSource() == warnIndicator) {
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
             } catch (NoSuchFieldException | IllegalAccessException nsfe) {
                 log.warn("Error awt title: " + nsfe); // $NON-NLS-1$
             }
        }
     }
 
     /**
      * Update Undo/Redo icons state
      * 
      * @param canUndo
      *            Flag whether the undo button should be enabled
      * @param canRedo
      *            Flag whether the redo button should be enabled
      */
     public void updateUndoRedoIcons(boolean canUndo, boolean canRedo) {
         toolbar.updateUndoRedoIcons(canUndo, canRedo);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/ToolBar.java b/src/core/org/apache/jmeter/gui/action/ToolBar.java
deleted file mode 100644
index ca6a82358..000000000
--- a/src/core/org/apache/jmeter/gui/action/ToolBar.java
+++ /dev/null
@@ -1,69 +0,0 @@
-/*
- * Licensed to the Apache Software Foundation (ASF) under one or more
- * contributor license agreements.  See the NOTICE file distributed with
- * this work for additional information regarding copyright ownership.
- * The ASF licenses this file to You under the Apache License, Version 2.0
- * (the "License"); you may not use this file except in compliance with
- * the License.  You may obtain a copy of the License at
- *
- *   http://www.apache.org/licenses/LICENSE-2.0
- *
- * Unless required by applicable law or agreed to in writing, software
- * distributed under the License is distributed on an "AS IS" BASIS,
- * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- * See the License for the specific language governing permissions and
- * limitations under the License.
- *
- */
-
-package org.apache.jmeter.gui.action;
-
-import java.awt.event.ActionEvent;
-import java.util.HashSet;
-import java.util.Set;
-
-import org.apache.jmeter.gui.GuiPackage;
-
-/**
- * Hide / unhide toolbar.
- *
- */
-public class ToolBar implements Command {
-
-    private static final Set<String> commands = new HashSet<>();
-
-    static {
-        commands.add(ActionNames.TOOLBAR);
-    }
-
-    /**
-     * Constructor for object.
-     */
-    public ToolBar() {
-    }
-
-    /**
-     * Gets the ActionNames attribute of the action
-     *
-     * @return the ActionNames value
-     */
-    @Override
-    public Set<String> getActionNames() {
-        return commands;
-    }
-
-    /**
-     * This method performs the actual command processing.
-     *
-     * @param e
-     *            the generic UI action event
-     */
-    @Override
-    public void doAction(ActionEvent e) {
-        if (ActionNames.TOOLBAR.equals(e.getActionCommand())) {
-            GuiPackage guiInstance = GuiPackage.getInstance();
-            final boolean isSelected = guiInstance.getMenuItemToolbar().getModel().isSelected();
-            guiInstance.getMainToolbar().setVisible(isSelected);
-        }
-    }
-}
diff --git a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
index ed83eb0b7..619c1eed9 100644
--- a/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
+++ b/src/core/org/apache/jmeter/gui/util/JMeterMenuBar.java
@@ -1,843 +1,840 @@
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
 
     private JMenuItem fileClose;
 
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
 
-        JCheckBoxMenuItem menuToolBar = makeCheckBoxMenuItemRes("menu_toolbar", ActionNames.TOOLBAR); //$NON-NLS-1$
         JCheckBoxMenuItem menuLoggerPanel = makeCheckBoxMenuItemRes("menu_logger_panel", ActionNames.LOGGER_PANEL_ENABLE_DISABLE); //$NON-NLS-1$
         GuiPackage guiInstance = GuiPackage.getInstance();
         if (guiInstance != null) { //avoid error in ant task tests (good way?)
-            guiInstance.setMenuItemToolbar(menuToolBar);
             guiInstance.setMenuItemLoggerPanel(menuLoggerPanel);
         }
-        optionsMenu.add(menuToolBar);
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
 
         fileClose = makeMenuItemRes("menu_close", 'C', ActionNames.CLOSE, KeyStrokes.CLOSE); //$NON-NLS-1$
 
         fileExit = makeMenuItemRes("exit", 'X', ActionNames.EXIT, KeyStrokes.EXIT); //$NON-NLS-1$
 
         fileMerge = makeMenuItemRes("menu_merge", 'M', ActionNames.MERGE); //$NON-NLS-1$
         // file_merge.setAccelerator(
         // KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         fileMerge.setEnabled(false);
 
         fileMenu.add(fileClose);
         fileMenu.add(fileLoad);
         fileMenu.add(templates);
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
