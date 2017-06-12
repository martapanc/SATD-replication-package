diff --git a/src/core/org/apache/jmeter/gui/action/SearchTreeCommand.java b/src/core/org/apache/jmeter/gui/action/SearchTreeCommand.java
index 262463539..f2b09e2b7 100644
--- a/src/core/org/apache/jmeter/gui/action/SearchTreeCommand.java
+++ b/src/core/org/apache/jmeter/gui/action/SearchTreeCommand.java
@@ -1,90 +1,96 @@
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
 import java.util.Iterator;
+import java.util.List;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Search nodes for a text
  * TODO Enhance search dialog to select kind of nodes ....
  */
 public class SearchTreeCommand extends AbstractAction {
     private Logger logger = LoggingManager.getLoggerForClass();
+
     private static final Set<String> commands = new HashSet<String>();
 
     static {
         commands.add(ActionNames.SEARCH_TREE);
     }
 
     /**
      * @see Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         String wordToSearch = JOptionPane.showInputDialog(
                 GuiPackage.getInstance().getMainFrame(),
                 JMeterUtils.getResString("search_word"),  // $NON-NLS-1$
                 JMeterUtils.getResString("search_tree_title"),  // $NON-NLS-1$
                 JOptionPane.QUESTION_MESSAGE);
         GuiPackage guiPackage = GuiPackage.getInstance();
         JMeterTreeModel jMeterTreeModel = guiPackage.getTreeModel();
         Iterator<?> iter = jMeterTreeModel.getNodesOfType(Searchable.class).iterator();
+        Set<JMeterTreeNode> nodes = new HashSet<JMeterTreeNode>();
         while (iter.hasNext()) {
             try {
                 JMeterTreeNode jMeterTreeNode = (JMeterTreeNode) iter.next();
                 if (jMeterTreeNode.getUserObject() instanceof Searchable){
                     Searchable searchable = (Searchable) jMeterTreeNode.getUserObject();
                     
                     boolean result = searchable.searchContent(wordToSearch);
+                    jMeterTreeNode.setMarkedBySearch(false);   
                     if(result) {
-                        jMeterTreeNode.setMarkedBySearch(true);
-                    }
-                    else {
-                        jMeterTreeNode.setMarkedBySearch(false);   
+                        List<JMeterTreeNode> matchingNodes = jMeterTreeNode.getPathToThreadGroup();
+                        nodes.addAll(matchingNodes);
                     }
                 }
             } catch (Exception ex) {
                 logger.error("Error occured searching for word:"+ wordToSearch, ex);
             }
         }
+        for (Iterator<JMeterTreeNode> iterator = nodes.iterator(); iterator.hasNext();) {
+            JMeterTreeNode jMeterTreeNode = iterator.next();
+            jMeterTreeNode.setMarkedBySearch(true);
+        }
         GuiPackage.getInstance().getMainFrame().repaint();
     }
 
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
index 905a5d342..3eb13d0f0 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeNode.java
@@ -1,175 +1,201 @@
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
+import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Enumeration;
+import java.util.List;
 
 import javax.swing.ImageIcon;
 import javax.swing.JPopupMenu;
 import javax.swing.tree.DefaultMutableTreeNode;
+import javax.swing.tree.TreeNode;
 
 import org.apache.jmeter.gui.GUIFactory;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class JMeterTreeNode extends DefaultMutableTreeNode implements NamedTreeNode {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
+    private static final int TEST_PLAN_LEVEL = 2;
+
     private final JMeterTreeModel treeModel;
 
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
         return ((AbstractTestElement) getTestElement()).getPropertyAsBoolean(TestElement.ENABLED);
     }
 
     public void setEnabled(boolean enabled) {
         getTestElement().setProperty(new BooleanProperty(TestElement.ENABLED, enabled));
         treeModel.nodeChanged(this);
     }
     
     /**
+     * Return nodes to level 2
+     * @return {@link List}<JMeterTreeNode>
+     */
+    public List<JMeterTreeNode> getPathToThreadGroup() {
+        List<JMeterTreeNode> nodes = new ArrayList<JMeterTreeNode>();
+        if(treeModel != null) {
+            TreeNode[] nodesToRoot = treeModel.getPathToRoot(this);
+            for (int i = 0; i < nodesToRoot.length; i++) {
+                JMeterTreeNode jMeterTreeNode = (JMeterTreeNode) nodesToRoot[i];
+                int level = jMeterTreeNode.getLevel();
+                if(level<TEST_PLAN_LEVEL) {
+                    continue;
+                } else {
+                    nodes.add(jMeterTreeNode);
+                }
+            }
+        }
+        return nodes;
+    }
+    
+    /**
      * Tag Node as result of a search
      * @return
      */
     public void setMarkedBySearch(boolean tagged) {
         this.markedBySearch = tagged;
         treeModel.nodeChanged(this);
     }
     
     /**
      * Node is markedBySearch by a search
      * @return
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
     public void setName(String name) {
         ((TestElement) getUserObject()).setName(name);
     }
 
     /** {@inheritDoc} */
     public String getName() {
         return ((TestElement) getUserObject()).getName();
     }
 
     /** {@inheritDoc} */
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
