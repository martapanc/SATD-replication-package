diff --git a/src/core/org/apache/jmeter/gui/GuiPackage.java b/src/core/org/apache/jmeter/gui/GuiPackage.java
index c7ae92e29..fbbb96ccd 100644
--- a/src/core/org/apache/jmeter/gui/GuiPackage.java
+++ b/src/core/org/apache/jmeter/gui/GuiPackage.java
@@ -1,732 +1,736 @@
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
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.TestBeanGUI;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
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
 public final class GuiPackage implements LocaleChangeListener {
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
      * Private constructor to permit instantiation only from within this class.
      * Use {@link #getInstance()} to retrieve a singleton instance.
      */
     private GuiPackage(JMeterTreeModel treeModel, JMeterTreeListener treeListener) {
         this.treeModel = treeModel;
         this.treeListener = treeListener;
         JMeterUtils.addLocaleChangeListener(this);
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
             throw new RuntimeException(e.toString()); // Probably a missing
                                                         // jar
         } catch (ClassNotFoundException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString()); // Programming error:
                                                         // bail out.
         } catch (InstantiationException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString()); // Programming error:
                                                         // bail out.
         } catch (IllegalAccessException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString()); // Programming error:
                                                         // bail out.
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
+                // When Switching to another language occurs, Introspector clears its internal caches
+                // and GUI classes information gets lost, so we initialize the GUI classes here
+                // TODO Find a better place for this
+                ((TestBeanGUI)comp).setupGuiClasses();
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
                 comp.modifyTestElement(el);
                 currentNode.nameChanged(); // Bug 50221 - ensure label is updated
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
         return treeModel.addSubTree(subTree, treeListener.getCurrentNode());
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
             popup.requestFocus();
         }
     }
 
     /**
      * {@inheritDoc}
      */
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
     }
 
     /**
      * Clears the test plan element and associated object
      *
      * @param element to clear
      */
     public void clearTestPlan(TestElement element) {
         getTreeModel().clearTestPlan(element);
         removeNode(element);
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
      * @return List<IStoppable> Copy of IStoppable
      */
     public List<Stoppable> getStoppables() {
         ArrayList<Stoppable> list = new ArrayList<Stoppable>();
         list.addAll(stoppables);
         return list;
     }
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
index 84cbadd69..10b0c16b1 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
@@ -1,447 +1,467 @@
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
+import java.util.ArrayList;
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
 
     private transient final BeanInfo beanInfo;
 
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
 
     /**
      * @deprecated Dummy for JUnit test purposes only
      */
     @Deprecated
     public TestBeanGUI() {
         log.warn("Constructor only for use in testing");// $NON-NLS-1$
         testBeanClass = null;
         customizerClass = null;
         beanInfo = null;
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
         for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
             String name = desc.getName();
             Object value = propertyMap.get(name);
             log.debug("Modify " + name + " to " + value);
             if (value == null) {
                 if (GenericTestBeanCustomizer.notNull(desc)) { // cannot be null
                     setPropertyInElement(element, name, desc.getValue(GenericTestBeanCustomizer.DEFAULT));
                 } else {
                     element.removeProperty(name);
                 }
             } else {
                 setPropertyInElement(element, name, value);
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
 
-        int matches = 0; // How many classes can we assign from?
+        int matches = setupGuiClasses(menuCategories);
+        if (matches == 0) {
+            log.error("Could not assign GUI class to " + testBeanClass.getName());
+        } else if (matches > 1) {// may be impossible, but no harm in
+                                    // checking ...
+            log.error("More than 1 GUI class found for " + testBeanClass.getName());
+        }
+        return menuCategories;
+    }
+    
+    /**
+     * Setup GUI class
+     * @return number of matches
+     */
+    public int setupGuiClasses() {
+    	return setupGuiClasses(new ArrayList<String>());
+    }
+    
+    /**
+     * Setup GUI class
+     * @param menuCategories List<String> menu categories
+     * @return number of matches
+     */
+    private int setupGuiClasses(List<String> menuCategories ) {
+    	int matches = 0;// How many classes can we assign from?
         // TODO: there must be a nicer way...
-        if (Assertion.class.isAssignableFrom(testBeanClass)) {
+        BeanDescriptor bd = beanInfo.getBeanDescriptor();
+    	if (Assertion.class.isAssignableFrom(testBeanClass)) {
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
-            matches++;
             menuCategories.add(MenuFactory.PRE_PROCESSORS);
             bd.setValue(TestElement.GUI_CLASS, AbstractPreProcessorGui.class.getName());
+            matches++;
         }
         if (Sampler.class.isAssignableFrom(testBeanClass)) {
-            matches++;
             menuCategories.add(MenuFactory.SAMPLERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractSamplerGui.class.getName());
+            matches++;
         }
         if (Timer.class.isAssignableFrom(testBeanClass)) {
-            matches++;
             menuCategories.add(MenuFactory.TIMERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractTimerGui.class.getName());
+            matches++;
         }
-        if (matches == 0) {
-            log.error("Could not assign GUI class to " + testBeanClass.getName());
-        } else if (matches > 1) {// may be impossible, but no harm in
-                                    // checking ...
-            log.error("More than 1 GUI class found for " + testBeanClass.getName());
-        }
-        return menuCategories;
+        return matches;
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
 
     public boolean isHidden() {
         return beanInfo.getBeanDescriptor().isHidden();
     }
 
     public boolean isExpert() {
         return beanInfo.getBeanDescriptor().isExpert();
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index da98c091c..cd142eb57 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,248 +1,249 @@
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
 The Include Controller has some problems in non-GUI mode (see Bugs 40671, 41286, 44973, 50898). 
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
 <p>
 Sample Sender implementations know resolve their configuration on Client side since 2.5.2.
 This behaviour can be changed with property sample_sender_client_configured (set it to false).
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
 <li>Bug 52221 - Nullpointer Exception with use Retrieve Embedded Resource without HTTP Cache Manager</li>
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
 <li>The CRLF example for the char function was wrong; CRLF=(0xD,0xA), not (0xC,0xA)</li>
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
 <li>Bug 52215 - Confusing synchronization in StatVisualizer, SummaryReport ,Summariser and issue in StatGraphVisualizer</li>
 <li>Bug 52216 - TableVisualizer : currentData field is badly synchronized</li>
 <li>Bug 52217 - ViewResultsFullVisualizer : Synchronization issues on root and treeModel</li>
 <li>Bug 43294 - XPath Extractor namespace problems</li>
 <li>Bug 52224 - TestBeanHelper does not support NOT_UNDEFINED == Boolean.FALSE</li>
+<li>Bug 52279 - Switching to another language loses icons in Tree and logs error Can't obtain GUI class from ...</li>
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
 <li>Bug 52087 - TCPClient interface does not allow for partial reads</li>
 <li>Bug 52115 - SOAP/XML-RPC should not send a POST request when file to send is not found</li>
 <li>Bug 40750 - TCPSampler : Behaviour when sockets are closed by remote host</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52022 - In View Results Tree rather than showing just a message if the results are to big, show as much of the result as are configured</li>
 <li>Bug 52201 - Add option to TableVisualiser to display child samples instead of parent </li>
 <li>Bug 52214 - Save Responses to a file - improve naming algorithm</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 52128 - Add JDBC pre- and post-processor</li>
 <li>Bug 52183 - SyncTimer could be improved (performance+reliability)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 52006 - Create a function RandomString to generate random Strings</li>
 <li>Bug 52016 - It would be useful to support Jexl2</li>
 <li>__char() function now supports octal values</li>
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
 <li>Bug 52160 - Don't display TestBeanGui items which are flagged as hidden</li>
 <li>Bug 51886 - SampleSender configuration resolved partly on client and partly on server</li>
 <li>Bug 52161 - Enable plugins to add own translation rules in addition to upgrade.properties.
 Loads any additional properties found in META-INF/resources/org.apache.jmeter.nameupdater.properties files</li>
 <li>Bug 42538 - Add "duplicate node" in context menu</li>
 <li>Bug 46921 - Add Ability to Change Controller elements</li>
 <li>Bug 52240 - TestBeans should support Boolean, Integer and Long</li>
 <li>Bug 52241 - GenericTestBeanCustomizer assumes that the default value is the empty string</li>
 <li>Bug 52242 - FileEditor does not allow output to be saved in a File </li>
 <li>Bug 51093 - when loading a selection previously stored by "Save Selection As", show the file name in the blue window bar</li>
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
