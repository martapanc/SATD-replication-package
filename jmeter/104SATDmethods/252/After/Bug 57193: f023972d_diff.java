diff --git a/src/core/org/apache/jmeter/config/gui/RowDetailDialog.java b/src/core/org/apache/jmeter/config/gui/RowDetailDialog.java
index fb18ee685..d40d39841 100644
--- a/src/core/org/apache/jmeter/config/gui/RowDetailDialog.java
+++ b/src/core/org/apache/jmeter/config/gui/RowDetailDialog.java
@@ -1,269 +1,269 @@
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
 
 package org.apache.jmeter.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.swing.AbstractAction;
 import javax.swing.Action;
 import javax.swing.ActionMap;
 import javax.swing.BorderFactory;
 import javax.swing.BoxLayout;
 import javax.swing.InputMap;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JRootPane;
 import javax.swing.JTextField;
 import javax.swing.event.DocumentEvent;
 import javax.swing.event.DocumentListener;
 
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.util.JSyntaxTextArea;
 import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.gui.ObjectTableModel;
 
 /**
  * Show detail of a Row
  */
 public class RowDetailDialog extends JDialog implements ActionListener, DocumentListener {
 
     private static final long serialVersionUID = 6578889215615435475L;
 
     /** Command for moving a row up in the table. */
     private static final String NEXT = "next"; // $NON-NLS-1$
 
     /** Command for moving a row down in the table. */
     private static final String PREVIOUS = "previous"; // $NON-NLS-1$
 
     /** Command for CANCEL. */
     private static final String CLOSE = "close"; // $NON-NLS-1$
 
     private static final String UPDATE = "update"; // $NON-NLS-1$
 
     private JLabel nameLabel;
 
     private JTextField nameTF;
 
     private JLabel valueLabel;
 
     private JSyntaxTextArea valueTA;
 
     private JButton nextButton;
 
     private JButton previousButton;
     
     private JButton closeButton;
 
     private ObjectTableModel tableModel;
 
     private int selectedRow;
     
     private boolean textChanged = true; // change to false after the first insert
 
 
     public RowDetailDialog() {
         super();
     }
 
     public RowDetailDialog(ObjectTableModel tableModel, int selectedRow) {
         super((JFrame) null, JMeterUtils.getResString("detail"), true); //$NON-NLS-1$
         this.tableModel = tableModel;
         this.selectedRow = selectedRow;
         init();
     }
 
     @Override
     protected JRootPane createRootPane() {
         JRootPane rootPane = new JRootPane();
         // Hide Window on ESC
         Action escapeAction = new AbstractAction("ESCAPE") {
             /**
              *
              */
             private static final long serialVersionUID = -8699034338969407625L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 setVisible(false);
             }
         };
         // Do update on Enter
         Action enterAction = new AbstractAction("ENTER") {
             /**
              *
              */
             private static final long serialVersionUID = -1529005452976176873L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 doUpdate(actionEvent);
                 setVisible(false);
             }
         };
         ActionMap actionMap = rootPane.getActionMap();
         actionMap.put(escapeAction.getValue(Action.NAME), escapeAction);
         actionMap.put(enterAction.getValue(Action.NAME), enterAction);
         InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         inputMap.put(KeyStrokes.ESC, escapeAction.getValue(Action.NAME));
         inputMap.put(KeyStrokes.ENTER, enterAction.getValue(Action.NAME));
         return rootPane;
     }
 
     private void init() {
         this.getContentPane().setLayout(new BorderLayout(10,10));
 
         nameLabel = new JLabel(JMeterUtils.getResString("name")); //$NON-NLS-1$
         nameTF = new JTextField(JMeterUtils.getResString("name"), 20); //$NON-NLS-1$
         nameTF.getDocument().addDocumentListener(this);
         JPanel namePane = new JPanel(new BorderLayout());
         namePane.add(nameLabel, BorderLayout.WEST);
         namePane.add(nameTF, BorderLayout.CENTER);
 
         valueLabel = new JLabel(JMeterUtils.getResString("value")); //$NON-NLS-1$
         valueTA = new JSyntaxTextArea(30, 80);
         valueTA.getDocument().addDocumentListener(this);
         setValues(selectedRow);
         JPanel valuePane = new JPanel(new BorderLayout());
         valuePane.add(valueLabel, BorderLayout.NORTH);
         JTextScrollPane jTextScrollPane = new JTextScrollPane(valueTA);
         valuePane.add(jTextScrollPane, BorderLayout.CENTER);
 
         JPanel detailPanel = new JPanel(new BorderLayout());
         detailPanel.add(namePane, BorderLayout.NORTH);
         detailPanel.add(valuePane, BorderLayout.CENTER);
 
         JPanel mainPanel = new JPanel();
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(7, 3, 3, 3));
         mainPanel.add(detailPanel, BorderLayout.CENTER);
 
         JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
 
         JButton updateButton = new JButton(JMeterUtils.getResString("update")); //$NON-NLS-1$
         updateButton.setActionCommand(UPDATE);
         updateButton.addActionListener(this);
         closeButton = new JButton(JMeterUtils.getResString("close")); //$NON-NLS-1$
         closeButton.setActionCommand(CLOSE);
         closeButton.addActionListener(this);
         nextButton = new JButton(JMeterUtils.getResString("next")); //$NON-NLS-1$
         nextButton.setActionCommand(NEXT);
         nextButton.addActionListener(this);
         nextButton.setEnabled(selectedRow < tableModel.getRowCount()-1);
         previousButton = new JButton(JMeterUtils.getResString("previous")); //$NON-NLS-1$
         previousButton.setActionCommand(PREVIOUS);
         previousButton.addActionListener(this);
         previousButton.setEnabled(selectedRow > 0);
 
         buttonsPanel.add(updateButton);
         buttonsPanel.add(previousButton);
         buttonsPanel.add(nextButton);
         buttonsPanel.add(closeButton);
         mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
         this.getContentPane().add(mainPanel);
         nameTF.requestFocusInWindow();
 
         this.pack();
         ComponentUtil.centerComponentInWindow(this);
     }
 
     /**
      * Do search
      * @param e {@link ActionEvent}
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
         if(action.equals(CLOSE)) {
             this.setVisible(false);
         }
         else if(action.equals(NEXT)) {
             selectedRow++;
             previousButton.setEnabled(true);
             nextButton.setEnabled(selectedRow < tableModel.getRowCount()-1);
             setValues(selectedRow);
         }
         else if(action.equals(PREVIOUS)) {
             selectedRow--;
             nextButton.setEnabled(true);
             previousButton.setEnabled(selectedRow > 0);
             setValues(selectedRow);
         }
         else if(action.equals(UPDATE)) {
             doUpdate(e);
         }
     }
 
     /**
      * Set TextField and TA values from model
      * @param selectedRow Selected row
      */
     private void setValues(int selectedRow) {
         nameTF.setText((String)tableModel.getValueAt(selectedRow, 0));
         valueTA.setInitialText((String)tableModel.getValueAt(selectedRow, 1));
         valueTA.setCaretPosition(0);
         textChanged = false;
     }
 
     /**
      * Update model values
-     * @param actionEvent
+     * @param actionEvent the event that led to this call
      */
     protected void doUpdate(ActionEvent actionEvent) {
         tableModel.setValueAt(nameTF.getText(), selectedRow, 0);
         tableModel.setValueAt(valueTA.getText(), selectedRow, 1);
         // Change Cancel label to Close
         closeButton.setText(JMeterUtils.getResString("close")); //$NON-NLS-1$
         textChanged = false;
     }
 
     /**
      * Change the label of Close button to Cancel (after the first text changes)
      */
     private void changeLabelButton() {
         if (!textChanged) {
             closeButton.setText(JMeterUtils.getResString("cancel")); //$NON-NLS-1$
             textChanged = true;
         }
     }
 
     @Override
     public void insertUpdate(DocumentEvent e) {
         changeLabelButton();
     }
 
     @Override
     public void removeUpdate(DocumentEvent e) {
         changeLabelButton();
     }
 
     @Override
     public void changedUpdate(DocumentEvent e) {
         changeLabelButton();
     }
     
 }
diff --git a/src/core/org/apache/jmeter/control/ReplaceableController.java b/src/core/org/apache/jmeter/control/ReplaceableController.java
index e0a8d8eea..e3a4cbba4 100644
--- a/src/core/org/apache/jmeter/control/ReplaceableController.java
+++ b/src/core/org/apache/jmeter/control/ReplaceableController.java
@@ -1,48 +1,48 @@
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
 
 package org.apache.jmeter.control;
 
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jorphan.collections.HashTree;
 
 /**
  * This interface represents a controller that gets replaced during the
  * compilation phase of test execution in an arbitrary way.
  *
  */
 public interface ReplaceableController {
 
     /**
      * Used to replace the test execution tree (usually by adding the
      * subelements of the TestElement that is replacing the
      * ReplaceableController.
      * 
      * @return The replaced sub tree
      *
      * @see org.apache.jorphan.collections.HashTree
      */
     HashTree getReplacementSubTree();
 
     /**
      * Compute the replacement tree.
      *
-     * @param context
+     * @param context the starting point of the replacement
      */
     void resolveReplacementSubTree(JMeterTreeNode context);
 }
diff --git a/src/core/org/apache/jmeter/gui/AbstractScopedJMeterGuiComponent.java b/src/core/org/apache/jmeter/gui/AbstractScopedJMeterGuiComponent.java
index 0023af1e5..83617a817 100644
--- a/src/core/org/apache/jmeter/gui/AbstractScopedJMeterGuiComponent.java
+++ b/src/core/org/apache/jmeter/gui/AbstractScopedJMeterGuiComponent.java
@@ -1,139 +1,143 @@
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
 
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.testelement.AbstractScopedTestElement;
 import org.apache.jmeter.util.ScopePanel;
 
 
 public abstract class AbstractScopedJMeterGuiComponent extends AbstractJMeterGuiComponent {
 
     private static final long serialVersionUID = 240L;
 
     private ScopePanel scopePanel;
 
     @Override
     public void clearGui(){
         super.clearGui();
         if (scopePanel != null) {
             scopePanel.clearGui();
         }
     }
     /**
      * When a user right-clicks on the component in the test tree, or selects
      * the edit menu when the component is selected, the component will be asked
      * to return a JPopupMenu that provides all the options available to the
      * user from this component.
      * <p>
      * This implementation returns menu items appropriate for most assertion
      * components.
      *
      * @return a JPopupMenu appropriate for the component.
      */
     @Override
     public JPopupMenu createPopupMenu() {
         return MenuFactory.getDefaultAssertionMenu();
     }
 
     /**
      * Create the scope settings panel.
      *
      * @return the scope settings panel
      */
     protected JPanel createScopePanel() {
         return createScopePanel(false);
     }
 
     /**
      * Create the scope settings panel.
      * @param enableVariable set true to enable the variable panel
      * @return the scope settings panel
      */
     protected JPanel createScopePanel(boolean enableVariable) {
         return createScopePanel(enableVariable, true, true);
     }
     
     /**
      * Create the scope settings panel.
      * @param enableVariable set true to enable the variable panel
      * @param enableParentAndSubsamples set true to enable the parent and sub-samples
      * @param enableSubsamplesOnly set true to enable the sub-samples only
      * @return the scope settings panel
      */
     protected JPanel createScopePanel(boolean enableVariable, boolean enableParentAndSubsamples, boolean enableSubsamplesOnly) {
         scopePanel = new ScopePanel(enableVariable, enableParentAndSubsamples, enableSubsamplesOnly);
         return scopePanel;
     }
 
     /**
      * Save the scope settings in the test element.
      *
      * @param testElement
+     *            the test element to save the settings into
      */
     protected void saveScopeSettings(AbstractScopedTestElement testElement) {
         if (scopePanel.isScopeParent()){
             testElement.setScopeParent();
         } else if (scopePanel.isScopeChildren()){
             testElement.setScopeChildren();
         } else if (scopePanel.isScopeAll()) {
             testElement.setScopeAll();
         } else if (scopePanel.isScopeVariable()) {
             testElement.setScopeVariable(scopePanel.getVariable());
         } else {
             throw new IllegalArgumentException("Unexpected scope panel state");
         }
     }
 
     /**
      * Show the scope settings from the test element.
      *
      * @param testElement
+     *            the test element from which the settings should be shown
      */
     protected void showScopeSettings(AbstractScopedTestElement testElement) {
         showScopeSettings(testElement, false);
     }
     
     /**
      * Show the scope settings from the test element with variable scope
      *
      * @param testElement
+     *            the test element from which the settings should be shown
      * @param enableVariableButton
+     *            set true to enable the variable panel
      */
     protected void showScopeSettings(AbstractScopedTestElement testElement,
             boolean enableVariableButton) {
         String scope = testElement.fetchScope();
         if (testElement.isScopeParent(scope)) {
                 scopePanel.setScopeParent(enableVariableButton);
         } else if (testElement.isScopeChildren(scope)){
             scopePanel.setScopeChildren(enableVariableButton);
         } else if (testElement.isScopeAll(scope)){
             scopePanel.setScopeAll(enableVariableButton);
         } else if (testElement.isScopeVariable(scope)){
             scopePanel.setScopeVariable(testElement.getVariableName());
         } else {
             throw new IllegalArgumentException("Invalid scope: "+scope);
         }
     }
 
 
 }
diff --git a/src/core/org/apache/jmeter/gui/Searchable.java b/src/core/org/apache/jmeter/gui/Searchable.java
index 8a7430b20..f3d99252f 100644
--- a/src/core/org/apache/jmeter/gui/Searchable.java
+++ b/src/core/org/apache/jmeter/gui/Searchable.java
@@ -1,33 +1,40 @@
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
- * Interface for nodes that are searchable
+ * Interface for nodes that are searchable.
+ * <p>
+ * A {@link Searchable} component will get asked for tokens, that should be used
+ * in a search. These tokens will then be matched against a user given search
+ * string.
  */
 public interface Searchable {
     /**
+     * Get a list of all tokens that should be visible to searching
+     *
      * @return List of searchable tokens
      * @throws Exception
+     *             when something fails while getting the searchable tokens
      */
     List<String> getSearchableTokens()
         throws Exception;
 }
diff --git a/src/core/org/apache/jmeter/gui/UndoHistory.java b/src/core/org/apache/jmeter/gui/UndoHistory.java
index e2814abaf..2965355f7 100644
--- a/src/core/org/apache/jmeter/gui/UndoHistory.java
+++ b/src/core/org/apache/jmeter/gui/UndoHistory.java
@@ -1,370 +1,370 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.swing.JTree;
 import javax.swing.event.TreeModelEvent;
 import javax.swing.event.TreeModelListener;
 
 import org.apache.jmeter.gui.action.UndoCommand;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * This class serves storing Test Tree state and navigating through it
  * to give the undo/redo ability for test plan changes
  * 
  * @since 2.12
  */
 public class UndoHistory implements TreeModelListener, Serializable {
     /**
      * 
      */
     private static final long serialVersionUID = -974269825492906010L;
     
     /**
      * Interface to be implemented by components interested in UndoHistory
      */
     public interface HistoryListener {
         void notifyChangeInHistory(UndoHistory history);
     }
 
     /**
      * Avoid storing too many elements
      *
-     * @param <T>
+     * @param <T> Class that should be held in this container
      */
     private static class LimitedArrayList<T> extends ArrayList<T> {
         /**
          *
          */
         private static final long serialVersionUID = -6574380490156356507L;
         private int limit;
 
         public LimitedArrayList(int limit) {
             this.limit = limit;
         }
 
         @Override
         public boolean add(T item) {
             if (this.size() + 1 > limit) {
                 this.remove(0);
             }
             return super.add(item);
         }
     }
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * temporary storage for GUI tree expansion state
      */
     private ArrayList<Integer> savedExpanded = new ArrayList<Integer>();
 
     /**
      * temporary storage for GUI tree selected row
      */
     private int savedSelected = 0;
 
     private static final int INITIAL_POS = -1;
     private int position = INITIAL_POS;
 
     private static final int HISTORY_SIZE = JMeterUtils.getPropDefault("undo.history.size", 0);
 
     private List<UndoHistoryItem> history = new LimitedArrayList<UndoHistoryItem>(HISTORY_SIZE);
 
     /**
      * flag to prevent recursive actions
      */
     private boolean working = false;
 
     /**
      * History listeners
      */
     private List<HistoryListener> listeners = new ArrayList<UndoHistory.HistoryListener>();
 
     public UndoHistory() {
     }
 
     /**
      * Clears the undo history
      */
     public void clear() {
         if (working) {
             return;
         }
         log.debug("Clearing undo history");
         history.clear();
         position = INITIAL_POS;
         notifyListeners();
     }
 
     /**
      * Add tree model copy to the history
      * <p>
      * This method relies on the rule that the record in history made AFTER
      * change has been made to test plan
      *
      * @param treeModel JMeterTreeModel
      * @param comment   String
      */
     public void add(JMeterTreeModel treeModel, String comment) {
         if(!isEnabled()) {
             log.debug("undo.history.size is set to 0, undo/redo feature is disabled");
             return;
         }
 
         // don't add element if we are in the middle of undo/redo or a big loading
         if (working) {
             log.debug("Not adding history because of noop");
             return;
         }
 
         JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
         if (root.getChildCount() < 1) {
             log.debug("Not adding history because of no children");
             return;
         }
 
         String name = root.getName();
 
         log.debug("Adding history element " + name + ": " + comment);
 
         working = true;
         // get test plan tree
         HashTree tree = treeModel.getCurrentSubTree((JMeterTreeNode) treeModel.getRoot());
         // first clone to not convert original tree
         tree = (HashTree) tree.getTree(tree.getArray()[0]).clone();
 
         position++;
         while (history.size() > position) {
             log.debug("Removing further record, position: " + position + ", size: " + history.size());
             history.remove(history.size() - 1);
         }
 
         // cloning is required because we need to immute stored data
         HashTree copy = UndoCommand.convertAndCloneSubTree(tree);
 
         history.add(new UndoHistoryItem(copy, comment));
 
         log.debug("Added history element, position: " + position + ", size: " + history.size());
         working = false;
         notifyListeners();
     }
 
     /**
      * Goes through undo history, changing GUI
      *
      * @param offset        the direction to go to, usually -1 for undo or 1 for redo
      * @param acceptorModel TreeModel to accept the changes
      */
     public void moveInHistory(int offset, JMeterTreeModel acceptorModel) {
         log.debug("Moving history from position " + position + " with step " + offset + ", size is " + history.size());
         if (offset < 0 && !canUndo()) {
             log.warn("Can't undo, we're already on the last record");
             return;
         }
 
         if (offset > 0 && !canRedo()) {
             log.warn("Can't redo, we're already on the first record");
             return;
         }
 
         if (history.isEmpty()) {
             log.warn("Can't proceed, the history is empty");
             return;
         }
 
         position += offset;
 
         final GuiPackage guiInstance = GuiPackage.getInstance();
 
         // save tree expansion and selection state before changing the tree
         saveTreeState(guiInstance);
 
         // load the tree
         loadHistoricalTree(acceptorModel, guiInstance);
 
         // load tree UI state
         restoreTreeState(guiInstance);
 
         log.debug("Current position " + position + ", size is " + history.size());
 
         // refresh the all ui
         guiInstance.updateCurrentGui();
         guiInstance.getMainFrame().repaint();
         notifyListeners();
     }
 
     /**
      * Load the undo item into acceptorModel tree
      *
      * @param acceptorModel tree to accept the data
-     * @param guiInstance
+     * @param guiInstance {@link GuiPackage} to be used
      */
     private void loadHistoricalTree(JMeterTreeModel acceptorModel, GuiPackage guiInstance) {
         HashTree newModel = history.get(position).getTree();
         acceptorModel.removeTreeModelListener(this);
         working = true;
         try {
             guiInstance.getTreeModel().clearTestPlan();
             guiInstance.addSubTree(newModel);
         } catch (Exception ex) {
             log.error("Failed to load from history", ex);
         }
         acceptorModel.addTreeModelListener(this);
         working = false;
     }
 
     /**
      * @return true if remaing items
      */
     public boolean canRedo() {
         return position < history.size() - 1;
     }
 
     /**
      * @return true if not at first element
      */
     public boolean canUndo() {
         return position > INITIAL_POS + 1;
     }
 
     /**
      * Record the changes in the node as the undo step
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeNodesChanged(TreeModelEvent tme) {
         String name = ((JMeterTreeNode) tme.getTreePath().getLastPathComponent()).getName();
         log.debug("Nodes changed " + name);
         final JMeterTreeModel sender = (JMeterTreeModel) tme.getSource();
         add(sender, "Node changed " + name);
     }
 
     /**
      * Record adding nodes as the undo step
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeNodesInserted(TreeModelEvent tme) {
         String name = ((JMeterTreeNode) tme.getTreePath().getLastPathComponent()).getName();
         log.debug("Nodes inserted " + name);
         final JMeterTreeModel sender = (JMeterTreeModel) tme.getSource();
         add(sender, "Add " + name);
     }
 
     /**
      * Record deleting nodes as the undo step
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeNodesRemoved(TreeModelEvent tme) {
         String name = ((JMeterTreeNode) tme.getTreePath().getLastPathComponent()).getName();
         log.debug("Nodes removed: " + name);
         add((JMeterTreeModel) tme.getSource(), "Remove " + name);
     }
 
     /**
      * Record some other change
      *
      * @param tme {@link TreeModelEvent} with event details
      */
     @Override
     public void treeStructureChanged(TreeModelEvent tme) {
         log.debug("Nodes struct changed");
         add((JMeterTreeModel) tme.getSource(), "Complex Change");
     }
 
     /**
      * Save tree expanded and selected state
      *
-     * @param guiPackage
+     * @param guiPackage {@link GuiPackage} to be used
      */
     private void saveTreeState(GuiPackage guiPackage) {
         savedExpanded.clear();
 
         MainFrame mainframe = guiPackage.getMainFrame();
         if (mainframe != null) {
             final JTree tree = mainframe.getTree();
             savedSelected = tree.getMinSelectionRow();
 
             for (int rowN = 0; rowN < tree.getRowCount(); rowN++) {
                 if (tree.isExpanded(rowN)) {
                     savedExpanded.add(rowN);
                 }
             }
         }
     }
 
     /**
      * Restore tree expanded and selected state
      *
-     * @param guiPackage
+     * @param guiInstance GuiPackage to be used
      */
     private void restoreTreeState(GuiPackage guiInstance) {
         final JTree tree = guiInstance.getMainFrame().getTree();
 
         if (savedExpanded.size() > 0) {
             for (int rowN : savedExpanded) {
                 tree.expandRow(rowN);
             }
         } else {
             tree.expandRow(0);
         }
         tree.setSelectionRow(savedSelected);
     }
     
     /**
      * 
      * @return true if history is enabled
      */
     boolean isEnabled() {
         return HISTORY_SIZE > 0;
     }
     
     /**
      * Register HistoryListener 
      * @param listener to add to our listeners
      */
     public void registerHistoryListener(HistoryListener listener) {
         listeners.add(listener);
     }
     
     /**
      * Notify listener
      */
     private void notifyListeners() {
         for (HistoryListener listener : listeners) {
             listener.notifyChangeInHistory(this);
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/gui/action/AbstractAction.java b/src/core/org/apache/jmeter/gui/action/AbstractAction.java
index 92eb959ee..d73291403 100644
--- a/src/core/org/apache/jmeter/gui/action/AbstractAction.java
+++ b/src/core/org/apache/jmeter/gui/action/AbstractAction.java
@@ -1,62 +1,62 @@
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
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public abstract class AbstractAction implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * @see Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
     }
 
     /**
      * @see Command#getActionNames()
      */
     @Override
     abstract public Set<String> getActionNames();
 
     /**
-     * @param e
+     * @param e the event that led to the call of this method
      */
     protected void popupShouldSave(ActionEvent e) {
         log.debug("popupShouldSave");
         if (GuiPackage.getInstance().getTestPlanFile() == null) {
             if (JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                     JMeterUtils.getResString("should_save"),  //$NON-NLS-1$
                     JMeterUtils.getResString("warning"),  //$NON-NLS-1$
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                 ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(),ActionNames.SAVE));
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java b/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java
index c4d031f1f..123c7a2fe 100644
--- a/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/AsynchSampleSender.java
@@ -1,166 +1,168 @@
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
 
 package org.apache.jmeter.samplers;
 
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.ArrayBlockingQueue;
 import java.util.concurrent.BlockingQueue;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.log.Logger;
 
 /**
  * Sends samples in a separate Thread and in Batch mode
  */
 public class AsynchSampleSender extends AbstractSampleSender implements Serializable {
 
     private static final long serialVersionUID = 251L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Create unique object as marker for end of queue
     private transient static final SampleEvent FINAL_EVENT = new SampleEvent();
 
     private static final int DEFAULT_QUEUE_SIZE = 100;
     
     private static final int serverConfiguredCapacity = JMeterUtils.getPropDefault("asynch.batch.queue.size", DEFAULT_QUEUE_SIZE); // $NON-NLS-1$
     
     private final int clientConfiguredCapacity = JMeterUtils.getPropDefault("asynch.batch.queue.size", DEFAULT_QUEUE_SIZE); // $NON-NLS-1$
 
     // created by client 
     private final RemoteSampleListener listener;
 
     private transient BlockingQueue<SampleEvent> queue; // created by server in readResolve method
     
     private transient long queueWaits; // how many times we had to wait to queue a sample
     
     private transient long queueWaitTime; // how long we had to wait (nanoSeconds)
 
     /**
      * Processed by the RMI server code.
-     * @throws ObjectStreamException  
+     *
+     * @return this
+     * @throws ObjectStreamException never
      */
     private Object readResolve() throws ObjectStreamException{
         int capacity = getCapacity();
         log.info("Using batch queue size (asynch.batch.queue.size): " + capacity); // server log file
         queue = new ArrayBlockingQueue<SampleEvent>(capacity);        
         Worker worker = new Worker(queue, listener);
         worker.setDaemon(true);
         worker.start();
         return this;
     }
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public AsynchSampleSender(){
         this(null);
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
 
     // Created by SampleSenderFactory
     protected AsynchSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         log.info("Using Asynch Remote Sampler for this test run, queue size "+getCapacity());  // client log file
     }
 
     /**
      * @return capacity
      */
     private int getCapacity() {
         return isClientConfigured() ? 
                 clientConfiguredCapacity : serverConfiguredCapacity;
     }
     
     @Override
     public void testEnded(String host) {
         log.debug("Test Ended on " + host);
         try {
             listener.testEnded(host);
             queue.put(FINAL_EVENT);
         } catch (Exception ex) {
             log.warn("testEnded(host)"+ex);
         }
         if (queueWaits > 0) {
             log.info("QueueWaits: "+queueWaits+"; QueueWaitTime: "+queueWaitTime+" (nanoseconds)");            
         }
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         try {
             if (!queue.offer(e)){ // we failed to add the element first time
                 queueWaits++;
                 long t1 = System.nanoTime();
                 queue.put(e);
                 long t2 = System.nanoTime();
                 queueWaitTime += t2-t1;
             }
         } catch (Exception err) {
             log.error("sampleOccurred; failed to queue the sample", err);
         }
     }
 
     private static class Worker extends Thread {
         
         private final BlockingQueue<SampleEvent> queue;
         
         private final RemoteSampleListener listener;
         
         private Worker(BlockingQueue<SampleEvent> q, RemoteSampleListener l){
             queue = q;
             listener = l;
         }
 
         @Override
         public void run() {
             try {
                 boolean eof = false;
                 while (!eof) {
                     List<SampleEvent> l = new ArrayList<SampleEvent>();
                     SampleEvent e = queue.take();
                     while (!(eof = (e == FINAL_EVENT)) && e != null) { // try to process as many as possible
                         l.add(e);
                         e = queue.poll(); // returns null if nothing on queue currently
                     }
                     int size = l.size();
                     if (size > 0) {
                         try {
                             listener.processBatch(l);
                         } catch (RemoteException err) {
                             if (err.getCause() instanceof java.net.ConnectException){
                                 throw new JMeterError("Could not return sample",err);
                             }
                             log.error("Failed to return sample", err);
                         }
                     }
                 }
             } catch (InterruptedException e) {
             }
             log.debug("Worker ended");
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/BatchSampleSender.java b/src/core/org/apache/jmeter/samplers/BatchSampleSender.java
index 1d89f0af7..547bfa85a 100644
--- a/src/core/org/apache/jmeter/samplers/BatchSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/BatchSampleSender.java
@@ -1,209 +1,212 @@
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
 
 package org.apache.jmeter.samplers;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.log.Logger;
 import org.apache.jorphan.logging.LoggingManager;
 
 import java.util.List;
 import java.util.ArrayList;
 import java.rmi.RemoteException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 
 /**
  * Implements batch reporting for remote testing.
  *
  */
 public class BatchSampleSender extends AbstractSampleSender implements Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final int DEFAULT_NUM_SAMPLE_THRESHOLD = 100;
 
     private static final long DEFAULT_TIME_THRESHOLD = 60000L;
 
     // Static fields are resolved on the server
     private static final int NUM_SAMPLES_THRESHOLD = 
         JMeterUtils.getPropDefault("num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD); // $NON-NLS-1$
 
     private static final long TIME_THRESHOLD_MS =
         JMeterUtils.getPropDefault("time_threshold", DEFAULT_TIME_THRESHOLD); // $NON-NLS-1$
 
     // instance fields are copied from the client instance
     private final int clientConfiguredNumSamplesThreshold = 
             JMeterUtils.getPropDefault("num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD); // $NON-NLS-1$
 
     private final long clientConfiguredTimeThresholdMs =
             JMeterUtils.getPropDefault("time_threshold", DEFAULT_TIME_THRESHOLD); // $NON-NLS-1$
 
     private final RemoteSampleListener listener;
 
     private final List<SampleEvent> sampleStore = new ArrayList<SampleEvent>();
 
     // Server-only work item
     private transient long batchSendTime = -1;
 
     // Configuration items, set up by readResolve
     private transient volatile int numSamplesThreshold;
 
     private transient volatile long timeThresholdMs;
 
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public BatchSampleSender(){
         this(null);
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
     /**
      * Constructor
      *
      * @param listener
      *            that the List of sample events will be sent to.
      */
     // protected added: Bug 50008 - allow BatchSampleSender to be subclassed
     protected BatchSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         if (isClientConfigured()) {
             log.info("Using batching (client settings) for this run."
                     + " Thresholds: num=" + clientConfiguredNumSamplesThreshold
                     + ", time=" + clientConfiguredTimeThresholdMs);
         } else {
             log.info("Using batching (server settings) for this run.");
         }
     }
 
    /**
     * @return the listener
     */
     // added: Bug 50008 - allow BatchSampleSender to be subclassed
    protected RemoteSampleListener getListener() {
        return listener;
    }
 
    /**
     * @return the sampleStore
     */
    // added: Bug 50008 - allow BatchSampleSender to be subclassed
    protected List<SampleEvent> getSampleStore() {
        return sampleStore;
    }
 
     /**
      * Checks if any sample events are still present in the sampleStore and
      * sends them to the listener. Informs the listener of the testended.
      *
      * @param host
      *            the host that the test has ended on.
      */
     @Override
     public void testEnded(String host) {
         log.info("Test Ended on " + host);
         try {
             if (sampleStore.size() != 0) {
                 listener.processBatch(sampleStore);
                 sampleStore.clear();
             }
             listener.testEnded(host);
         } catch (RemoteException err) {
             log.error("testEnded(host)", err);
         }
     }
 
     /**
      * Stores sample events untill either a time or sample threshold is
      * breached. Both thresholds are reset if one fires. If only one threshold
      * is set it becomes the only value checked against. When a threhold is
      * breached the list of sample events is sent to a listener where the event
      * are fired locally.
      *
      * @param e
      *            a Sample Event
      */
     @Override
     public void sampleOccurred(SampleEvent e) {
         List<SampleEvent> clonedStore = null;
         synchronized (sampleStore) {
             sampleStore.add(e);
             final int sampleCount = sampleStore.size();
 
             boolean sendNow = false;            
             if (numSamplesThreshold != -1) {
                 if (sampleCount >= numSamplesThreshold) {
                     sendNow = true;
                 }
             }
 
             long now = 0;
             if (timeThresholdMs != -1) {
                 now = System.currentTimeMillis();
                 // Checking for and creating initial timestamp to check against
                 if (batchSendTime == -1) {
                     this.batchSendTime = now + timeThresholdMs;
                 }
                 if (batchSendTime < now && sampleCount > 0) {
                     sendNow = true;
                 }
             }
 
             if (sendNow){
                 @SuppressWarnings("unchecked") // OK because sampleStore is of type ArrayList<SampleEvent>
                 final ArrayList<SampleEvent> clone = (ArrayList<SampleEvent>)((ArrayList<SampleEvent>)sampleStore).clone();
                 clonedStore = clone;
                 sampleStore.clear();
                 if (timeThresholdMs != -1) {
                     this.batchSendTime = now + timeThresholdMs;
                 }
             }
         } // synchronized(sampleStore)
         
         if (clonedStore != null){
             try {
                 log.debug("Firing sample");
                 listener.processBatch(clonedStore);
                 clonedStore.clear();
             } catch (RemoteException err) {
                 log.error("sampleOccurred", err);
             }  
         }
     }
     
     /**
      * Processed by the RMI server code; acts as testStarted().
-     * @throws ObjectStreamException  
+     *
+     * @return this
+     * @throws ObjectStreamException
+     *             never
      */
     private Object readResolve() throws ObjectStreamException{
         if (isClientConfigured()) {
             numSamplesThreshold = clientConfiguredNumSamplesThreshold;
             timeThresholdMs = clientConfiguredTimeThresholdMs;
         } else {
             numSamplesThreshold =  NUM_SAMPLES_THRESHOLD;
             timeThresholdMs = TIME_THRESHOLD_MS;
         }
         log.info("Using batching for this run."
                 + " Thresholds: num=" + numSamplesThreshold
                 + ", time=" + timeThresholdMs); 
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java b/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
index 0015dd989..0de60ae7c 100644
--- a/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/DataStrippingSampleSender.java
@@ -1,104 +1,107 @@
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
 
 package org.apache.jmeter.samplers;
 
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * The standard remote sample reporting should be more friendly to the main purpose of
  * remote testing - which is scalability.  To increase scalability, this class strips out the
  * response data before sending.
  *
  *
  */
 public class DataStrippingSampleSender extends AbstractSampleSender implements Serializable {
 
     private static final long serialVersionUID = -5556040298982085715L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final RemoteSampleListener listener;
     private final SampleSender decoratedSender;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public DataStrippingSampleSender(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
         listener = null;
         decoratedSender = null;
     }
 
     DataStrippingSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         decoratedSender = null;
         log.info("Using DataStrippingSampleSender for this run");
     }
 
     DataStrippingSampleSender(SampleSender decorate)
     {
         this.decoratedSender = decorate;
         this.listener = null;
         log.info("Using DataStrippingSampleSender for this run");
     }
 
     @Override
     public void testEnded(String host) {
         log.info("Test Ended on " + host);
         if(decoratedSender != null) decoratedSender.testEnded(host);
     }
 
     @Override
     public void sampleOccurred(SampleEvent event) {
         //Strip the response data before writing, but only for a successful request.
         SampleResult result = event.getResult();
         if(result.isSuccessful()) {
             // Compute bytes before stripping
             result.setBytes(result.getBytes());
             result.setResponseData(new byte[0]);
         }
         if(decoratedSender == null)
         {
             try {
                 listener.sampleOccurred(event);
             } catch (RemoteException e) {
                 log.error("Error sending sample result over network ",e);
             }
         }
         else
         {
             decoratedSender.sampleOccurred(event);
         }
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
-     * @throws ObjectStreamException  
+     *
+     * @return this
+     * @throws ObjectStreamException
+     *             never
      */
     private Object readResolve() throws ObjectStreamException{
         log.info("Using DataStrippingSampleSender for this run");
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java b/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java
index 2b850a36e..24f836bb2 100644
--- a/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/DiskStoreSampleSender.java
@@ -1,172 +1,175 @@
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
 
 package org.apache.jmeter.samplers;
 
 import org.apache.log.Logger;
 import org.apache.commons.io.IOUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 
 import java.io.EOFException;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.OutputStream;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.TimeUnit;
 
 /**
  * Version of HoldSampleSender that stores the samples on disk as a serialised stream.
  */
 
 public class DiskStoreSampleSender extends AbstractSampleSender implements Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 252L;
 
     private final RemoteSampleListener listener;
 
     private transient volatile ObjectOutputStream oos;
     private transient volatile File temporaryFile;
     private transient volatile ExecutorService singleExecutor;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public DiskStoreSampleSender(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
         listener = null;
     }
 
     DiskStoreSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         log.info("Using DiskStoreSampleSender for this test run"); // client log file
     }
 
     @Override
     public void testEnded(String host) {
         log.info("Test Ended on " + host);
         singleExecutor.submit(new Runnable(){
             @Override
             public void run() {
                 try {
                     oos.close(); // ensure output is flushed
                 } catch (IOException e) {
                     log.error("Failed to close data file ", e);
                 }                
             }});
         singleExecutor.shutdown(); // finish processing samples
         try {
             if (!singleExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                 log.error("Executor did not terminate in a timely fashion");
             }
         } catch (InterruptedException e1) {
             log.error("Executor did not terminate in a timely fashion", e1);
         }
         ObjectInputStream ois = null;
         try {
             ois = new ObjectInputStream(new FileInputStream(temporaryFile));
             Object obj = null;
             while((obj = ois.readObject()) != null) {
                 if (obj instanceof SampleEvent) {
                     try {
                         listener.sampleOccurred((SampleEvent) obj);
                     } catch (RemoteException err) {
                         if (err.getCause() instanceof java.net.ConnectException){
                             throw new JMeterError("Could not return sample",err);
                         }
                         log.error("returning sample", err);
                     }
                 } else {
                     log.error("Unexpected object type found in data file "+obj.getClass().getName());
                 }
             }                    
         } catch (EOFException err) {
             // expected
         } catch (IOException err) {
             log.error("returning sample", err);
         } catch (ClassNotFoundException err) {
             log.error("returning sample", err);
         } finally {
             try {
                 listener.testEnded(host);
             } catch (RemoteException e) {
                 log.error("returning sample", e);
             }
             IOUtils.closeQuietly(ois);
             if(!temporaryFile.delete()) {
                 log.warn("Could not delete file:"+temporaryFile.getAbsolutePath());
             }
         }
     }
 
     @Override
     public void sampleOccurred(final SampleEvent e) {
         // sampleOccurred is called from multiple threads; not safe to write from multiple threads.
         // also decouples the file IO from sample generation
         singleExecutor.submit(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         oos.writeObject(e);
                     } catch (IOException err) {
                         log.error("sampleOccurred", err);
                     }                
                 }
             
             }
         );
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
-     * @throws ObjectStreamException  
+     *
+     * @return this
+     * @throws ObjectStreamException
+     *             never
      */
     // TODO should errors be thrown back through RMI?
     private Object readResolve() throws ObjectStreamException{
         log.info("Using DiskStoreSampleSender for this test run"); // server log file
         singleExecutor = Executors.newSingleThreadExecutor();
         try {
             temporaryFile = File.createTempFile("SerialisedSampleSender", ".ser");
             temporaryFile.deleteOnExit();
             singleExecutor.submit(new Runnable(){
                 @Override
                 public void run() {
                     OutputStream anOutputStream;
                     try {
                         anOutputStream = new FileOutputStream(temporaryFile);
                         oos = new ObjectOutputStream(anOutputStream);
                     } catch (IOException e) {
                         log.error("Failed to create output Stream", e);
                     }
                 }});
         } catch (IOException e) {
             log.error("Failed to create output file", e);
         }
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/HoldSampleSender.java b/src/core/org/apache/jmeter/samplers/HoldSampleSender.java
index b8bbee6b6..96026c81b 100644
--- a/src/core/org/apache/jmeter/samplers/HoldSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/HoldSampleSender.java
@@ -1,93 +1,96 @@
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
 
 package org.apache.jmeter.samplers;
 
 import org.apache.log.Logger;
 import org.apache.jorphan.logging.LoggingManager;
 
 import java.util.List;
 import java.util.ArrayList;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 
 /**
  * Lars-Erik Helander provided the idea (and original implementation) for the
  * caching functionality (sampleStore).
  */
 
 public class HoldSampleSender extends AbstractSampleSender implements Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private final List<SampleEvent> sampleStore = new ArrayList<SampleEvent>();
 
     private final RemoteSampleListener listener;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public HoldSampleSender(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
         listener = null;
     }
 
     HoldSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         log.warn("Using HoldSampleSender for this test run, ensure you have configured enough memory (-Xmx) for your test"); // client        
     }
 
     @Override
     public void testEnded(String host) {
         log.info("Test Ended on " + host);
         try {
             for (SampleEvent se : sampleStore) {
                 listener.sampleOccurred(se);
             }
             listener.testEnded(host);
             sampleStore.clear();
         } catch (Throwable ex) {
             log.error("testEnded(host)", ex);
             if (ex instanceof Error){
                 throw (Error) ex;
             }
             if (ex instanceof RuntimeException){
                 throw (RuntimeException) ex;
             }
         }
 
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         synchronized (sampleStore) {
             sampleStore.add(e);
         }
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
-     * @throws ObjectStreamException  
+     *
+     * @return this
+     * @throws ObjectStreamException
+     *             never
      */
     private Object readResolve() throws ObjectStreamException{
         log.warn("Using HoldSampleSender for this test run, ensure you have configured enough memory (-Xmx) for your test"); // server        
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/RemoteSampleListener.java b/src/core/org/apache/jmeter/samplers/RemoteSampleListener.java
index 64f15a01d..95a3d5132 100644
--- a/src/core/org/apache/jmeter/samplers/RemoteSampleListener.java
+++ b/src/core/org/apache/jmeter/samplers/RemoteSampleListener.java
@@ -1,82 +1,82 @@
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
 
 package org.apache.jmeter.samplers;
 
 import java.rmi.RemoteException;
 import java.util.List;
 
 /**
  * Allows notification on events occurring during the sampling process.
  * Specifically, when sampling is started, when a specific sample is obtained,
  * and when sampling is stopped.
  *
  * @version $Revision$
  */
 public interface RemoteSampleListener extends java.rmi.Remote {
     void testStarted() throws RemoteException;
 
     void testStarted(String host) throws RemoteException;
 
     void testEnded() throws RemoteException;
 
     void testEnded(String host) throws RemoteException;
 
     // Not currently needed by any Remoteable classes
     // Anyway, would probably be too expensive in terms of network traffic
     // 
     // void testIterationStart(LoopIterationEvent event);
     
     /**
      * This method is called remotely and fires a list of samples events
      * received locally. The function is to reduce network load when using
      * remote testing.
      *
      * @param samples
      *            the list of sample events to be fired locally.
-     * @throws RemoteException
+     * @throws RemoteException when calling the remote method fails
      */
     void processBatch(List<SampleEvent> samples) throws RemoteException;
 
     /**
      * A sample has started and stopped.
      * 
      * @param e
      *            the event with data about the completed sample
-     * @throws RemoteException
+     * @throws RemoteException when calling the remote method fails
      */
     void sampleOccurred(SampleEvent e) throws RemoteException;
 
     /**
      * A sample has started.
      * 
      * @param e
      *            the event with data about the started sample
-     * @throws RemoteException
+     * @throws RemoteException when calling the remote method fails
      */
     void sampleStarted(SampleEvent e) throws RemoteException;
 
     /**
      * A sample has stopped.
      * 
      * @param e
      *            the event with data about the stopped sample
-     * @throws RemoteException
+     * @throws RemoteException when calling the remote method fails
      */
     void sampleStopped(SampleEvent e) throws RemoteException;
 }
diff --git a/src/core/org/apache/jmeter/samplers/StandardSampleSender.java b/src/core/org/apache/jmeter/samplers/StandardSampleSender.java
index 51d34b239..3af8cd925 100644
--- a/src/core/org/apache/jmeter/samplers/StandardSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/StandardSampleSender.java
@@ -1,84 +1,87 @@
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
 
 package org.apache.jmeter.samplers;
 
 import org.apache.log.Logger;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 
 import java.rmi.RemoteException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 
 /**
  * Default behaviour for remote testing.
  */
 
 public class StandardSampleSender extends AbstractSampleSender implements Serializable {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final RemoteSampleListener listener;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public StandardSampleSender(){
         this.listener = null;
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
 
     StandardSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         log.info("Using StandardSampleSender for this test run");        
     }
 
     @Override
     public void testEnded(String host) {
         log.info("Test Ended on " + host);
         try {
             listener.testEnded(host);
         } catch (RemoteException ex) {
             log.warn("testEnded(host)"+ex);
         }
     }
 
     @Override
     public void sampleOccurred(SampleEvent e) {
         try {
             listener.sampleOccurred(e);
         } catch (RemoteException err) {
             if (err.getCause() instanceof java.net.ConnectException){
                 throw new JMeterError("Could not return sample",err);
             }
             log.error("sampleOccurred", err);
         }
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
-     * @throws ObjectStreamException  
+     *
+     * @return this
+     * @throws ObjectStreamException
+     *             never
      */
     private Object readResolve() throws ObjectStreamException{
         log.info("Using StandardSampleSender for this test run");        
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java b/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java
index 8b2e9e664..f3153e92e 100644
--- a/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java
+++ b/src/core/org/apache/jmeter/samplers/StatisticalSampleSender.java
@@ -1,225 +1,226 @@
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
 
 package org.apache.jmeter.samplers;
 
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.rmi.RemoteException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 /**
  * Implements batch reporting for remote testing.
  *
  */
 public class StatisticalSampleSender extends AbstractSampleSender implements Serializable {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final int DEFAULT_NUM_SAMPLE_THRESHOLD = 100;
 
     private static final long DEFAULT_TIME_THRESHOLD = 60000L;
 
     // Static fields are set by the server when the class is constructed
 
     private static final int NUM_SAMPLES_THRESHOLD = JMeterUtils.getPropDefault(
             "num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD);
 
     private static final long TIME_THRESHOLD_MS = JMeterUtils.getPropDefault("time_threshold",
             DEFAULT_TIME_THRESHOLD);
 
     // should the samples be aggregated on thread name or thread group (default) ?
     private static boolean KEY_ON_THREADNAME = JMeterUtils.getPropDefault("key_on_threadname", false);
 
     // Instance fields are constructed by the client when the instance is create in the test plan
     // and the field values are then transferred to the server copy by RMI serialisation/deserialisation
 
     private final int clientConfiguredNumSamplesThreshold = JMeterUtils.getPropDefault(
             "num_sample_threshold", DEFAULT_NUM_SAMPLE_THRESHOLD);
 
     private final long clientConfiguredTimeThresholdMs = JMeterUtils.getPropDefault("time_threshold",
             DEFAULT_TIME_THRESHOLD);
 
     // should the samples be aggregated on thread name or thread group (default) ?
     private final boolean clientConfiguredKeyOnThreadName = JMeterUtils.getPropDefault("key_on_threadname", false);
 
     private final RemoteSampleListener listener;
 
     private final List<SampleEvent> sampleStore = new ArrayList<SampleEvent>();
 
     //@GuardedBy("sampleStore") TODO perhaps use ConcurrentHashMap ?
     private final Map<String, StatisticalSampleResult> sampleTable = new HashMap<String, StatisticalSampleResult>();
 
     // Settings; readResolve sets these from the server/client values as appropriate
     // TODO would be nice to make these final; not 100% sure volatile is needed as not changed after creation
     private transient volatile int numSamplesThreshold;
 
     private transient volatile long timeThresholdMs;
 
     private transient volatile boolean keyOnThreadName;
 
 
     // variables maintained by server code
     // @GuardedBy("sampleStore")
     private transient int sampleCount; // maintain separate count of samples for speed
 
     private transient long batchSendTime = -1; // @GuardedBy("sampleStore")
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public StatisticalSampleSender(){
         this(null);
         log.warn("Constructor only intended for use in testing");
     }
 
     /**
      * Constructor, only called by client code.
      *
      * @param listener that the List of sample events will be sent to.
      */
     StatisticalSampleSender(RemoteSampleListener listener) {
         this.listener = listener;
         if (isClientConfigured()) {
             log.info("Using StatisticalSampleSender (client settings) for this run."
                     + " Thresholds: num=" + clientConfiguredNumSamplesThreshold
                     + ", time=" + clientConfiguredTimeThresholdMs
                     + ". Key uses ThreadName: " + clientConfiguredKeyOnThreadName);
         } else {
             log.info("Using StatisticalSampleSender (server settings) for this run.");
         }
     }
 
     /**
      * Checks if any sample events are still present in the sampleStore and
      * sends them to the listener. Informs the listener that the test ended.
      *
      * @param host the hostname that the test has ended on.
      */
     @Override
     public void testEnded(String host) {
         log.info("Test Ended on " + host);
         try {
             if (sampleStore.size() != 0) {
                 sendBatch();
             }
             listener.testEnded(host);
         } catch (RemoteException err) {
             log.warn("testEnded(hostname)", err);
         }
     }
 
     /**
      * Stores sample events until either a time or sample threshold is
      * breached. Both thresholds are reset if one fires. If only one threshold
      * is set it becomes the only value checked against. When a threshold is
      * breached the list of sample events is sent to a listener where the event
      * are fired locally.
      *
      * @param e a Sample Event
      */
     @Override
     public void sampleOccurred(SampleEvent e) {
         synchronized (sampleStore) {
             // Locate the statistical sample collector
             String key = StatisticalSampleResult.getKey(e, keyOnThreadName);
             StatisticalSampleResult statResult = sampleTable.get(key);
             if (statResult == null) {
                 statResult = new StatisticalSampleResult(e.getResult());
                 // store the new statistical result collector
                 sampleTable.put(key, statResult);
                 // add a new wrapper sampleevent
                 sampleStore
                         .add(new SampleEvent(statResult, e.getThreadGroup()));
             }
             statResult.add(e.getResult());
             sampleCount++;
             boolean sendNow = false;
             if (numSamplesThreshold != -1) {
                 if (sampleCount >= numSamplesThreshold) {
                     sendNow = true;
                 }
             }
 
             long now = 0;
             if (timeThresholdMs != -1) {
                 now = System.currentTimeMillis();
                 // Checking for and creating initial timestamp to check against
                 if (batchSendTime == -1) {
                     this.batchSendTime = now + timeThresholdMs;
                 }
                 if (batchSendTime < now) {
                     sendNow = true;
                 }
             }
             if (sendNow) {
                 try {
                     if (log.isDebugEnabled()) {
                         log.debug("Firing sample");
                     }
                     sendBatch();
                     if (timeThresholdMs != -1) {
                         this.batchSendTime = now + timeThresholdMs;
                     }
                 } catch (RemoteException err) {
                     log.warn("sampleOccurred", err);
                 }
             }
         } // synchronized(sampleStore)
     }
 
     private void sendBatch() throws RemoteException {
         if (sampleStore.size() > 0) {
             listener.processBatch(sampleStore);
             sampleStore.clear();
             sampleTable.clear();
             sampleCount = 0;
         }
     }
 
     /**
      * Processed by the RMI server code; acts as testStarted().
-     * @throws ObjectStreamException
+     * @return this
+     * @throws ObjectStreamException never
      */
     private Object readResolve() throws ObjectStreamException{
         if (isClientConfigured()) {
             numSamplesThreshold = clientConfiguredNumSamplesThreshold;
             timeThresholdMs = clientConfiguredTimeThresholdMs;
             keyOnThreadName = clientConfiguredKeyOnThreadName;
         } else {
             numSamplesThreshold = NUM_SAMPLES_THRESHOLD;
             timeThresholdMs = TIME_THRESHOLD_MS;
             keyOnThreadName = KEY_ON_THREADNAME;
         }
         log.info("Using StatisticalSampleSender for this run."
                 + (isClientConfigured() ? " Client config: " : " Server config: ")
                 + " Thresholds: num=" + numSamplesThreshold
                 + ", time=" + timeThresholdMs
                 + ". Key uses ThreadName: " + keyOnThreadName);
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java b/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java
index abadfefb6..0b7389002 100644
--- a/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java
+++ b/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java
@@ -1,298 +1,301 @@
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
  */
 package org.apache.jmeter.testbeans;
 
 import java.awt.Image;
 import java.beans.BeanDescriptor;
 import java.beans.BeanInfo;
 import java.beans.EventSetDescriptor;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.MethodDescriptor;
 import java.beans.PropertyDescriptor;
 import java.beans.SimpleBeanInfo;
 import java.util.MissingResourceException;
 import java.util.ResourceBundle;
 
 import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
 import org.apache.jmeter.testbeans.gui.TypeEditor;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Support class for test bean beanInfo objects. It will help using the
  * introspector to get most of the information, to then modify it at will.
  * <p>
  * To use, subclass it, create a subclass with a parameter-less constructor
  * that:
  * <ol>
  * <li>Calls super(beanClass)
  * <li>Modifies the property descriptors, bean descriptor, etc. at will.
  * </ol>
  * <p>
  * Even before any such modifications, a resource bundle named xxxResources
  * (where xxx is the fully qualified bean class name) will be obtained if
  * available and used to localize the following:
  * <ul>
  * <li>Bean's display name -- from property <b>displayName</b>.
  * <li>Properties' display names -- from properties <b><i>propertyName</i>.displayName</b>.
  * <li>Properties' short descriptions -- from properties <b><i>propertyName</i>.shortDescription</b>.
  * </ul>
  * <p>
  * The resource bundle will be stored as the bean descriptor's "resourceBundle"
  * attribute, so that it can be used for further localization. TestBeanGUI, for
  * example, uses it to obtain the group's display names from properties <b><i>groupName</i>.displayName</b>.
  *
  * @version $Revision$
  */
 public abstract class BeanInfoSupport extends SimpleBeanInfo {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Some known attribute names, just for convenience:
     public static final String TAGS = GenericTestBeanCustomizer.TAGS;
 
     /** Whether the field must be defined (i.e. is required);  Boolean, defaults to FALSE */
     public static final String NOT_UNDEFINED = GenericTestBeanCustomizer.NOT_UNDEFINED;
 
     /** Whether the field disallows JMeter expressions; Boolean, default FALSE */
     public static final String NOT_EXPRESSION = GenericTestBeanCustomizer.NOT_EXPRESSION;
 
     /** Whether the field disallows constant values different from the provided tags; Boolean, default FALSE */
     public static final String NOT_OTHER = GenericTestBeanCustomizer.NOT_OTHER;
 
     /** If specified, create a multi-line editor */
     public static final String MULTILINE = GenericTestBeanCustomizer.MULTILINE;
 
     /** Default value, must be provided if {@link #NOT_UNDEFINED} is TRUE */
     public static final String DEFAULT = GenericTestBeanCustomizer.DEFAULT;
 
     /** Pointer to the resource bundle, if any (will generally be null) */
     public static final String RESOURCE_BUNDLE = GenericTestBeanCustomizer.RESOURCE_BUNDLE;
 
     /** TextEditor property */
     public static final String TEXT_LANGUAGE = GenericTestBeanCustomizer.TEXT_LANGUAGE;
 
     /** The BeanInfo for our class as obtained by the introspector. */
     private final BeanInfo rootBeanInfo;
 
     /** The descriptor for our class */
     private final BeanDescriptor beanDescriptor;
 
     /** The icons for this bean. */
     private final Image[] icons = new Image[5];
 
     /** The class for which we're providing the bean info. */
     private final Class<?> beanClass;
 
     /**
      * Construct a BeanInfo for the given class.
+     *
+     * @param beanClass
+     *            class for which to construct a BeanInfo
      */
     protected BeanInfoSupport(Class<? extends TestBean> beanClass) {
         this.beanClass= beanClass;
 
         try {
             rootBeanInfo = Introspector.getBeanInfo(beanClass, Introspector.IGNORE_IMMEDIATE_BEANINFO);
         } catch (IntrospectionException e) {
             throw new Error("Can't introspect "+beanClass, e); // Programming error: bail out.
         }
 
         // N.B. JVMs other than Sun may return different instances each time
         // so we cache the value here (and avoid having to fetch it every time)
         beanDescriptor = rootBeanInfo.getBeanDescriptor();
 
         try {
             ResourceBundle resourceBundle = ResourceBundle.getBundle(
                     beanClass.getName() + "Resources",  // $NON-NLS-1$
                     JMeterUtils.getLocale());
 
             // Store the resource bundle as an attribute of the BeanDescriptor:
             getBeanDescriptor().setValue(RESOURCE_BUNDLE, resourceBundle);
             final String dnKey = "displayName";
             // Localize the bean name
             if (resourceBundle.containsKey(dnKey)) { // $NON-NLS-1$
                 getBeanDescriptor().setDisplayName(resourceBundle.getString(dnKey)); // $NON-NLS-1$
             } else {
                 log.debug("Localized display name not available for bean " + beanClass);                    
             }
             // Localize the property names and descriptions:
             PropertyDescriptor[] properties = getPropertyDescriptors();
             for (PropertyDescriptor property : properties) {
                 String name = property.getName();
                 final String propDnKey = name + ".displayName";
                 if(resourceBundle.containsKey(propDnKey)) {
                     property.setDisplayName(resourceBundle.getString(propDnKey)); // $NON-NLS-1$
                 } else {
                     log.debug("Localized display name not available for property " + name + " in " + beanClass);
                 }
                 final String propSdKey = name + ".shortDescription";
                 if(resourceBundle.containsKey(propSdKey)) {
                     property.setShortDescription(resourceBundle.getString(propSdKey));
                 } else {
                     log.debug("Localized short description not available for property " + name + " in " + beanClass);
                 }
             }
         } catch (MissingResourceException e) {
             log.warn("Localized strings not available for bean " + beanClass, e);
         } catch (Exception e) {
             log.warn("Something bad happened when loading bean info for bean " + beanClass, e);
         }
     }
 
     /**
      * Get the property descriptor for the property of the given name.
      *
      * @param name
      *            property name
      * @return descriptor for a property of that name, or null if there's none
      */
     protected PropertyDescriptor property(String name) {
         for (PropertyDescriptor propdesc : getPropertyDescriptors()) {
             if (propdesc.getName().equals(name)) {
                 return propdesc;
             }
         }
         log.error("Cannot find property: " + name + " in class " + beanClass);
         return null;
     }
 
     /**
      * Get the property descriptor for the property of the given name.
      * Sets the GUITYPE to the provided editor.
      *
      * @param name
      *            property name
      * @param editor the TypeEditor enum that describes the property editor
      *
      * @return descriptor for a property of that name, or null if there's none
      */
     protected PropertyDescriptor property(String name, TypeEditor editor) {
         PropertyDescriptor property = property(name);
         if (property != null) {
             property.setValue(GenericTestBeanCustomizer.GUITYPE, editor);
         }
         return property;
     }
 
     /**
      * Get the property descriptor for the property of the given name.
      * Sets the GUITYPE to the provided enum.
      *
      * @param name
      *            property name
      * @param enumClass the enum class that is to be used by the editor
      * @return descriptor for a property of that name, or null if there's none
      */
     protected PropertyDescriptor property(final String name, 
             final Class<? extends Enum<?>> enumClass) {
         PropertyDescriptor property = property(name);
         if (property != null) {
             property.setValue(GenericTestBeanCustomizer.GUITYPE, enumClass);
             // we also provide the resource bundle
             property.setValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE, getBeanDescriptor().getValue(RESOURCE_BUNDLE));
         }
         return property;
     }
 
     /**
      * Set the bean's 16x16 colour icon.
      *
      * @param resourceName
      *            A pathname relative to the directory holding the class file of
      *            the current class.
      */
     protected void setIcon(String resourceName) {
         icons[ICON_COLOR_16x16] = loadImage(resourceName);
     }
 
     /** Number of groups created so far by createPropertyGroup. */
     private int numCreatedGroups = 0;
 
     /**
      * Utility method to group and order properties.
      * <p>
      * It will assign the given group name to each of the named properties, and
      * set their order attribute so that they are shown in the given order.
      * <p>
      * The created groups will get order 1, 2, 3,... in the order in which they
      * are created.
      *
      * @param group
      *            name of the group
      * @param names
      *            property names in the desired order
      */
     protected void createPropertyGroup(String group, String[] names) {
         for (int i = 0; i < names.length; i++) { // i is used below
             log.debug("Getting property for: " + names[i]);
             PropertyDescriptor p = property(names[i]);
             p.setValue(GenericTestBeanCustomizer.GROUP, group);
             p.setValue(GenericTestBeanCustomizer.ORDER, Integer.valueOf(i));
         }
         numCreatedGroups++;
         getBeanDescriptor().setValue(GenericTestBeanCustomizer.ORDER(group), Integer.valueOf(numCreatedGroups));
     }
 
     /** {@inheritDoc} */
     @Override
     public BeanInfo[] getAdditionalBeanInfo() {
         return rootBeanInfo.getAdditionalBeanInfo();
     }
 
     /** {@inheritDoc} */
     @Override
     public BeanDescriptor getBeanDescriptor() {
         return beanDescriptor;
     }
 
     /** {@inheritDoc} */
     @Override
     public int getDefaultEventIndex() {
         return rootBeanInfo.getDefaultEventIndex();
     }
 
     /** {@inheritDoc} */
     @Override
     public int getDefaultPropertyIndex() {
         return rootBeanInfo.getDefaultPropertyIndex();
     }
 
     /** {@inheritDoc} */
     @Override
     public EventSetDescriptor[] getEventSetDescriptors() {
         return rootBeanInfo.getEventSetDescriptors();
     }
 
     /** {@inheritDoc} */
     @Override
     public Image getIcon(int iconKind) {
         return icons[iconKind];
     }
 
     /** {@inheritDoc} */
     @Override
     public MethodDescriptor[] getMethodDescriptors() {
         return rootBeanInfo.getMethodDescriptors();
     }
 
     /** {@inheritDoc} */
     @Override
     public PropertyDescriptor[] getPropertyDescriptors() {
         return rootBeanInfo.getPropertyDescriptors();
     }
 }
diff --git a/src/core/org/apache/jmeter/testelement/TestElement.java b/src/core/org/apache/jmeter/testelement/TestElement.java
index 7b174827a..a3b1b4ba3 100644
--- a/src/core/org/apache/jmeter/testelement/TestElement.java
+++ b/src/core/org/apache/jmeter/testelement/TestElement.java
@@ -1,336 +1,337 @@
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
 
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContext;
 
 public interface TestElement extends Cloneable {
     String NAME = "TestElement.name"; //$NON-NLS-1$
 
     String GUI_CLASS = "TestElement.gui_class"; //$NON-NLS-1$
 
     String ENABLED = "TestElement.enabled"; //$NON-NLS-1$
 
     String TEST_CLASS = "TestElement.test_class"; //$NON-NLS-1$
 
     // Needed by AbstractTestElement.
     // Also TestElementConverter and TestElementPropertyConverter for handling empty comments
     String COMMENTS = "TestPlan.comments"; //$NON-NLS-1$
     // N.B. Comments originally only applied to Test Plans, hence the name - which can now not be easily changed
 
     void addTestElement(TestElement child);
     
     /**
      * This method should clear any test element properties that are merged
      * by {@link #addTestElement(TestElement)}.
      */
     void clearTestElementChildren();
 
     void setProperty(String key, String value);
 
     void setProperty(String key, String value, String dflt);
 
     void setProperty(String key, boolean value);
 
     void setProperty(String key, boolean value, boolean dflt);
 
     void setProperty(String key, int value);
 
     void setProperty(String key, int value, int dflt);
 
     void setProperty(String name, long value);
 
     void setProperty(String name, long value, long dflt);
 
     /**
      * Check if ENABLED property is present and true ; defaults to true
      *
      * @return true if element is enabled
      */
     boolean isEnabled();
 
     /**
      * Set the enabled status of the test element
      * @param enabled the status to set
      */
     void setEnabled(boolean enabled);
 
     /**
      * Returns true or false whether the element is the running version.
      * 
      * @return <code>true</code> if the element is the running version
      */
     boolean isRunningVersion();
 
     /**
      * Test whether a given property is only a temporary resident of the
      * TestElement
      *
      * @param property
-     * @return boolean
+     *            the property to be tested
+     * @return <code>true</code> if property is temporary
      */
     boolean isTemporary(JMeterProperty property);
 
     /**
      * Indicate that the given property should be only a temporary property in
      * the TestElement
      *
      * @param property
      *            void
      */
     void setTemporary(JMeterProperty property);
 
     /**
      * Return a property as a boolean value.
      *
      * @param key
      *            the name of the property to get
      * @return the value of the property
      */
     boolean getPropertyAsBoolean(String key);
 
     /**
      * Return a property as a boolean value or a default value if no property
      * could be found.
      *
      * @param key
      *            the name of the property to get
      * @param defaultValue
      *            the default value to use
      * @return the value of the property, or <code>defaultValue</code> if no
      *         property could be found
      */
     boolean getPropertyAsBoolean(String key, boolean defaultValue);
 
     /**
      * Return a property as a long value.
      *
      * @param key
      *            the name of the property to get
      * @return the value of the property
      */
     long getPropertyAsLong(String key);
 
     /**
      * Return a property as a long value or a default value if no property
      * could be found.
      *
      * @param key
      *            the name of the property to get
      * @param defaultValue
      *            the default value to use
      * @return the value of the property, or <code>defaultValue</code> if no
      *         property could be found
      */
     long getPropertyAsLong(String key, long defaultValue);
 
     /**
      * Return a property as an int value.
      *
      * @param key
      *            the name of the property to get
      * @return the value of the property
      */
     int getPropertyAsInt(String key);
     
     /**
      * Return a property as an int value or a default value if no property
      * could be found.
      *
      * @param key
      *            the name of the property to get
      * @param defaultValue
      *            the default value to use
      * @return the value of the property, or <code>defaultValue</code> if no
      *         property could be found
      */
     int getPropertyAsInt(String key, int defaultValue);
 
     /**
      * Return a property as a float value.
      *
      * @param key
      *            the name of the property to get
      * @return the value of the property
      */
     float getPropertyAsFloat(String key);
 
     /**
      * Return a property as a double value.
      *
      * @param key
      *            the name of the property to get
      * @return the value of the property
      */
     double getPropertyAsDouble(String key);
 
     /**
      * Make the test element the running version, or make it no longer the
      * running version. This tells the test element that it's current state must
      * be retrievable by a call to recoverRunningVersion(). It is kind of like
      * making the TestElement Read- Only, but not as strict. Changes can be made
      * and the element can be modified, but the state of the element at the time
      * of the call to setRunningVersion() must be recoverable.
      *
      * @param run
      *            flag whether this element should be the running version
      */
     void setRunningVersion(boolean run);
 
     /**
      * Tells the test element to return to the state it was in when
      * setRunningVersion(true) was called.
      */
     void recoverRunningVersion();
 
     /**
      * Clear the TestElement of all data.
      */
     void clear();
     // TODO - yet another ambiguous name - does it need changing?
     // See also: Clearable, JMeterGUIComponent
 
     /**
      * Return a property as a string value.
      *
      * @param key
      *            the name of the property to get
      * @return the value of the property
      */
     String getPropertyAsString(String key);
 
     /**
      * Return a property as an string value or a default value if no property
      * could be found.
      *
      * @param key
      *            the name of the property to get
      * @param defaultValue
      *            the default value to use
      * @return the value of the property, or <code>defaultValue</code> if no
      *         property could be found
      */
     String getPropertyAsString(String key, String defaultValue);
 
     /**
      * Sets and overwrites a property in the TestElement. This call will be
      * ignored if the TestElement is currently a "running version".
      *
      * @param property
      *            the property to be set
      */
     void setProperty(JMeterProperty property);
 
     /**
      * Given the name of the property, returns the appropriate property from
      * JMeter. If it is null, a NullProperty object will be returned.
      *
      * @param propName
      *            the name of the property to get
      * @return {@link JMeterProperty} stored under the name, or
      *         {@link NullProperty} if no property can be found
      */
     JMeterProperty getProperty(String propName);
 
     /**
      * Get a Property Iterator for the TestElements properties.
      *
      * @return PropertyIterator
      */
     PropertyIterator propertyIterator();
 
     /**
      * Remove property stored under the <code>key</code>
      *
      * @param key
      *            name of the property to be removed
      */
     void removeProperty(String key);
 
     // lifecycle methods
 
     Object clone();
 
     /**
      * Convenient way to traverse a test element.
      *
      * @param traverser
      *            The traverser that is notified of the contained elements
      */
     void traverse(TestElementTraverser traverser);
 
     /**
      * @return Returns the threadContext.
      */
     JMeterContext getThreadContext();
 
     /**
      * @param threadContext
      *            The threadContext to set.
      */
     void setThreadContext(JMeterContext threadContext);
 
     /**
      * @return Returns the threadName.
      */
     String getThreadName();
 
     /**
      * @param threadName
      *            The threadName to set.
      */
     void setThreadName(String threadName);
 
     /**
      * Called by Remove to determine if it is safe to remove the element. The
      * element can either clean itself up, and return true, or the element can
      * return false.
      *
      * @return true if safe to remove the element
      */
     boolean canRemove();
 
     /**
      * Get the name of this test element
      * @return name of this element
      */
     String getName();
 
     /**
      * @param name
      *            of this element
      */
     void setName(String name);
 
     /**
      * @return comment associated with this element
      */
     String getComment();
 
     /**
      * Associates a comment with this element
      *
      * @param comment
      *            to be associated
      */
     void setComment(String comment);
 }
diff --git a/src/core/org/apache/jmeter/testelement/TestIterationListener.java b/src/core/org/apache/jmeter/testelement/TestIterationListener.java
index 87c9164b0..225e9c3d1 100644
--- a/src/core/org/apache/jmeter/testelement/TestIterationListener.java
+++ b/src/core/org/apache/jmeter/testelement/TestIterationListener.java
@@ -1,35 +1,35 @@
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
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 
 public interface TestIterationListener {
 
     /**
      * Each time through a Thread Group's test script, an iteration event is
      * fired for each thread.
      *
      * This will be after the test elements have been cloned, so in general
      * the instance will not be the same as the ones the start/end methods call.
      *
-     * @param event
+     * @param event the iteration event
      */
     void testIterationStart(LoopIterationEvent event);
 }
diff --git a/src/core/org/apache/jmeter/testelement/property/NumberProperty.java b/src/core/org/apache/jmeter/testelement/property/NumberProperty.java
index d6c537e4f..92b076696 100644
--- a/src/core/org/apache/jmeter/testelement/property/NumberProperty.java
+++ b/src/core/org/apache/jmeter/testelement/property/NumberProperty.java
@@ -1,72 +1,80 @@
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
 
 /*
  * Created on May 5, 2003
  */
 package org.apache.jmeter.testelement.property;
 
 public abstract class NumberProperty extends AbstractProperty {
     private static final long serialVersionUID = 240L;
 
     public NumberProperty() {
         super();
     }
 
     public NumberProperty(String name) {
         super(name);
     }
 
     /**
      * Set the value of the property with a Number object.
+     *
+     * @param n the value to set
      */
     protected abstract void setNumberValue(Number n);
 
     /**
      * Set the value of the property with a String object.
+     *
+     * @param n
+     *            the number to set as a string representation
+     * @throws NumberFormatException
+     *             if the number <code>n</code> can not be converted to a
+     *             {@link Number}
      */
     protected abstract void setNumberValue(String n) throws NumberFormatException;
 
     @Override
     public void setObjectValue(Object v) {
         if (v instanceof Number) {
             setNumberValue((Number) v);
         } else {
             try {
                 setNumberValue(v.toString());
             } catch (RuntimeException e) {
             }
         }
     }
 
     /**
      * @see Comparable#compareTo(Object)
      */
     @Override
     public int compareTo(JMeterProperty arg0) {
         double compareValue = getDoubleValue() - arg0.getDoubleValue();
 
         if (compareValue < 0) {
             return -1;
         } else if (compareValue == 0) {
             return 0;
         } else {
             return 1;
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/RemoteThreadsListener.java b/src/core/org/apache/jmeter/threads/RemoteThreadsListener.java
index 80c37d66b..0a34d84c0 100644
--- a/src/core/org/apache/jmeter/threads/RemoteThreadsListener.java
+++ b/src/core/org/apache/jmeter/threads/RemoteThreadsListener.java
@@ -1,40 +1,40 @@
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
 
 import java.rmi.RemoteException;
 
 /**
  * RMI Interface that allows notification of remote start/end of threads
  * @since 2.10
  */
 public interface RemoteThreadsListener extends java.rmi.Remote {
     
     /**
      * @see org.apache.jmeter.testelement.ThreadListener#threadStarted()
-     * @throws RemoteException
+     * @throws RemoteException when remote calling of the method fails
      */
     void threadStarted() throws RemoteException;
 
     /**
      * @see org.apache.jmeter.testelement.ThreadListener#threadFinished()
-     * @throws RemoteException
+     * @throws RemoteException when remote calling of the method fails
      */
     void threadFinished() throws RemoteException;
 }
diff --git a/src/core/org/apache/jmeter/threads/TestCompilerHelper.java b/src/core/org/apache/jmeter/threads/TestCompilerHelper.java
index c8b237657..a7d66b25c 100644
--- a/src/core/org/apache/jmeter/threads/TestCompilerHelper.java
+++ b/src/core/org/apache/jmeter/threads/TestCompilerHelper.java
@@ -1,47 +1,48 @@
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
 
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * Bug 53796 - TestCompiler uses static Set which can grow huge
  * 
  * This interface is a means to allow the pair data to be saved with the parent
  * instance, thus allowing it to be garbage collected when the thread completes.
  * 
  * This uses a bit more memory, as each controller test element includes the data
  * structure to contain the child element. However, there is no need to store the
  * parent element. 
  * 
  * @since 2.8
  */
 public interface TestCompilerHelper {
 
     /**
      * Add child test element only if it has not already been added.
      * <p>
      * Only for use by TestCompiler.
      * 
      * @param child
-     * @return true if the child was added
+     *            the {@link TestElement} to be added
+     * @return <code>true</code> if the child was added
      */
     boolean addTestElementOnce(TestElement child);
     
 }
diff --git a/src/core/org/apache/jmeter/util/JSR223TestElement.java b/src/core/org/apache/jmeter/util/JSR223TestElement.java
index 9576071be..389fb20bd 100644
--- a/src/core/org/apache/jmeter/util/JSR223TestElement.java
+++ b/src/core/org/apache/jmeter/util/JSR223TestElement.java
@@ -1,262 +1,262 @@
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
 
 package org.apache.jmeter.util;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.Map;
 import java.util.Properties;
 
 import javax.script.Bindings;
 import javax.script.Compilable;
 import javax.script.CompiledScript;
 import javax.script.ScriptEngine;
 import javax.script.ScriptEngineManager;
 import javax.script.ScriptException;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public abstract class JSR223TestElement extends ScriptingTestElement
     implements Serializable, TestStateListener
 {
     /**
      * Initialization On Demand Holder pattern
      */
     private static class LazyHolder {
         public static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
     }
  
     /**
      * @return ScriptEngineManager singleton
      */
     public static ScriptEngineManager getInstance() {
             return LazyHolder.INSTANCE;
     }
     
     private static final long serialVersionUID = 233L;
 
     private String cacheKey = ""; // If not empty then script in ScriptText will be compiled and cached
 
     /**
      * Cache of compiled scripts
      */
     @SuppressWarnings("unchecked") // LRUMap does not support generics (yet)
     private static final Map<String, CompiledScript> compiledScriptsCache = 
             Collections.synchronizedMap(
                     new LRUMap(JMeterUtils.getPropDefault("jsr223.compiled_scripts_cache_size", 100)));
 
     public JSR223TestElement() {
         super();
     }
 
     protected ScriptEngine getScriptEngine() throws ScriptException {
         final String lang = getScriptLanguage();
 
         ScriptEngine scriptEngine = getInstance().getEngineByName(lang);
         if (scriptEngine == null) {
             throw new ScriptException("Cannot find engine named: '"+lang+"', ensure you set language field in JSR223 Test Element:"+getName());
         }
 
         return scriptEngine;
     }
 
     /**
      * Populate variables to be passed to scripts
      * @param bindings Bindings
      */
     protected void populateBindings(Bindings bindings) {
         final String label = getName();
         final String fileName = getFilename();
         final String scriptParameters = getParameters();
         // Use actual class name for log
         final Logger logger = LoggingManager.getLoggerForShortName(getClass().getName());
         bindings.put("log", logger); // $NON-NLS-1$ (this name is fixed)
         bindings.put("Label", label); // $NON-NLS-1$ (this name is fixed)
         bindings.put("FileName", fileName); // $NON-NLS-1$ (this name is fixed)
         bindings.put("Parameters", scriptParameters); // $NON-NLS-1$ (this name is fixed)
         String [] args=JOrphanUtils.split(scriptParameters, " ");//$NON-NLS-1$
         bindings.put("args", args); // $NON-NLS-1$ (this name is fixed)
         // Add variables for access to context and variables
         JMeterContext jmctx = JMeterContextService.getContext();
         bindings.put("ctx", jmctx); // $NON-NLS-1$ (this name is fixed)
         JMeterVariables vars = jmctx.getVariables();
         bindings.put("vars", vars); // $NON-NLS-1$ (this name is fixed)
         Properties props = JMeterUtils.getJMeterProperties();
         bindings.put("props", props); // $NON-NLS-1$ (this name is fixed)
         // For use in debugging:
         bindings.put("OUT", System.out); // $NON-NLS-1$ (this name is fixed)
 
         // Most subclasses will need these:
         Sampler sampler = jmctx.getCurrentSampler();
         bindings.put("sampler", sampler); // $NON-NLS-1$ (this name is fixed)
         SampleResult prev = jmctx.getPreviousResult();
         bindings.put("prev", prev); // $NON-NLS-1$ (this name is fixed)
     }
 
 
     /**
      * This method will run inline script or file script with special behaviour for file script:
      * - If ScriptEngine implements Compilable script will be compiled and cached
      * - If not if will be run
      * @param scriptEngine ScriptEngine
      * @param bindings {@link Bindings} might be null
      * @return Object returned by script
-     * @throws IOException
-     * @throws ScriptException
+     * @throws IOException when reading the script fails
+     * @throws ScriptException when compiling or evaluation of the script fails
      */
     protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings) throws IOException, ScriptException {
         if (bindings == null) {
             bindings = scriptEngine.createBindings();
         }
         populateBindings(bindings);
         File scriptFile = new File(getFilename()); 
         // Hack: bsh-2.0b5.jar BshScriptEngine implements Compilable but throws "java.lang.Error: unimplemented"
         boolean supportsCompilable = scriptEngine instanceof Compilable 
                 && !(scriptEngine.getClass().getName().equals("bsh.engine.BshScriptEngine")); // $NON-NLS-1$
         if (!StringUtils.isEmpty(getFilename())) {
             if (scriptFile.exists() && scriptFile.canRead()) {
                 BufferedReader fileReader = null;
                 try {
                     if (supportsCompilable) {
                         String cacheKey = 
                                 getScriptLanguage()+"#"+ // $NON-NLS-1$
                                 scriptFile.getAbsolutePath()+"#"+  // $NON-NLS-1$
                                         scriptFile.lastModified();
                         CompiledScript compiledScript = 
                                 compiledScriptsCache.get(cacheKey);
                         if (compiledScript==null) {
                             synchronized (compiledScriptsCache) {
                                 compiledScript = 
                                         compiledScriptsCache.get(cacheKey);
                                 if (compiledScript==null) {
                                     // TODO Charset ?
                                     fileReader = new BufferedReader(new FileReader(scriptFile), 
                                             (int)scriptFile.length()); 
                                     compiledScript = 
                                             ((Compilable) scriptEngine).compile(fileReader);
                                     compiledScriptsCache.put(cacheKey, compiledScript);
                                 }
                             }
                         }
                         return compiledScript.eval(bindings);
                     } else {
                         // TODO Charset ?
                         fileReader = new BufferedReader(new FileReader(scriptFile), 
                                 (int)scriptFile.length()); 
                         return scriptEngine.eval(fileReader, bindings);                    
                     }
                 } finally {
                     IOUtils.closeQuietly(fileReader);
                 }
             }  else {
                 throw new ScriptException("Script file '"+scriptFile.getAbsolutePath()+"' does not exist or is unreadable for element:"+getName());
             }
         } else if (!StringUtils.isEmpty(getScript())){
             if (supportsCompilable && !StringUtils.isEmpty(cacheKey)) {
                 CompiledScript compiledScript = 
                         compiledScriptsCache.get(cacheKey);
                 if (compiledScript==null) {
                     synchronized (compiledScriptsCache) {
                         compiledScript = 
                                 compiledScriptsCache.get(cacheKey);
                         if (compiledScript==null) {
                             compiledScript = 
                                     ((Compilable) scriptEngine).compile(getScript());
                             compiledScriptsCache.put(cacheKey, compiledScript);
                         }
                     }
                 }
                 return compiledScript.eval(bindings);
             } else {
                 return scriptEngine.eval(getScript(), bindings);
             }
         } else {
             throw new ScriptException("Both script file and script text are empty for element:"+getName());            
         }
     }
 
 
     /**
      * @return the cacheKey
      */
     public String getCacheKey() {
         return cacheKey;
     }
 
     /**
      * @param cacheKey the cacheKey to set
      */
     public void setCacheKey(String cacheKey) {
         this.cacheKey = cacheKey;
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted()
      */
     @Override
     public void testStarted() {
         // NOOP
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted(java.lang.String)
      */
     @Override
     public void testStarted(String host) {
         // NOOP   
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded()
      */
     @Override
     public void testEnded() {
         testEnded("");
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded(java.lang.String)
      */
     @Override
     public void testEnded(String host) {
         compiledScriptsCache.clear();
     }
     public String getScriptLanguage() {
         return scriptLanguage;
     }
 
     public void setScriptLanguage(String s) {
         scriptLanguage = s;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/SSLManager.java b/src/core/org/apache/jmeter/util/SSLManager.java
index de27ae2ad..4c348ba8f 100644
--- a/src/core/org/apache/jmeter/util/SSLManager.java
+++ b/src/core/org/apache/jmeter/util/SSLManager.java
@@ -1,308 +1,313 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.InputStream;
 import java.net.HttpURLConnection;
 import java.security.KeyStore;
 import java.security.Provider;
 import java.security.Security;
 import java.util.Locale;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.keystore.JmeterKeyStore;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * The SSLManager handles the KeyStore information for JMeter. Basically, it
  * handles all the logic for loading and initializing all the JSSE parameters
  * and selecting the alias to authenticate against if it is available.
  * SSLManager will try to automatically select the client certificate for you,
  * but if it can't make a decision, it will pop open a dialog asking you for
  * more information.
  *
  * TODO? - N.B. does not currently allow the selection of a client certificate.
  *
  */
 public abstract class SSLManager {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String SSL_TRUST_STORE = "javax.net.ssl.trustStore";// $NON-NLS-1$
 
     private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword"; // $NON-NLS-1$
 
     public static final String JAVAX_NET_SSL_KEY_STORE = "javax.net.ssl.keyStore"; // $NON-NLS-1$
 
     private static final String JAVAX_NET_SSL_KEY_STORE_TYPE = "javax.net.ssl.keyStoreType"; // $NON-NLS-1$
 
     private static final String PKCS12 = "pkcs12"; // $NON-NLS-1$
 
     /** Singleton instance of the manager */
     //@GuardedBy("this")
     private static SSLManager manager;
 
     private static final boolean isSSLSupported = true;
 
     /** Cache the KeyStore instance */
     private volatile JmeterKeyStore keyStore;
 
     /** Cache the TrustStore instance - null if no truststore name was provided */
     private KeyStore trustStore = null;
     // Have we yet tried to load the truststore?
     private volatile boolean truststore_loaded=false;
 
     /** Have the password available */
     protected String defaultpw = System.getProperty(KEY_STORE_PASSWORD);
 
     private int keystoreAliasStartIndex;
 
     private int keystoreAliasEndIndex;
 
     private String clientCertAliasVarName;
 
     /**
      * Resets the SSLManager so that we can create a new one with a new keystore
      */
     public static synchronized void reset() {
         SSLManager.manager = null;
     }
 
     public abstract void setContext(HttpURLConnection conn);
 
     /**
      * Default implementation of setting the Provider
+     *
+     * @param provider
+     *            the provider to use
      */
     protected void setProvider(Provider provider) {
         if (null != provider) {
             Security.addProvider(provider);
         }
     }
     
     /**
      * Opens and initializes the KeyStore. If the password for the KeyStore is
      * not set, this method will prompt you to enter it. Unfortunately, there is
      * no PasswordEntryField available from JOptionPane.
+     *
+     * @return the configured {@link JmeterKeyStore}
      */
     protected synchronized JmeterKeyStore getKeyStore() {
         if (null == this.keyStore) {
             String fileName = System.getProperty(JAVAX_NET_SSL_KEY_STORE,""); // empty if not provided
             String fileType = System.getProperty(JAVAX_NET_SSL_KEY_STORE_TYPE, // use the system property to determine the type
                     fileName.toLowerCase(Locale.ENGLISH).endsWith(".p12") ? PKCS12 : "JKS"); // otherwise use the name
             log.info("JmeterKeyStore Location: " + fileName + " type " + fileType);
             try {
                 this.keyStore = JmeterKeyStore.getInstance(fileType, keystoreAliasStartIndex, keystoreAliasEndIndex, clientCertAliasVarName);
                 log.info("KeyStore created OK");
             } catch (Exception e) {
                 this.keyStore = null;
                 throw new RuntimeException("Could not create keystore: "+e.getMessage(), e);
             }
             InputStream fileInputStream = null;
             try {
                 File initStore = new File(fileName);
 
                 if (fileName.length() >0 && initStore.exists()) {
                     fileInputStream = new BufferedInputStream(new FileInputStream(initStore));
                     this.keyStore.load(fileInputStream, getPassword());
                     if (log.isInfoEnabled()) {
                         log.info("Total of " + keyStore.getAliasCount() + " aliases loaded OK from keystore");
                     }
                 } else {
                     log.warn("Keystore file not found, loading empty keystore");
                     this.defaultpw = ""; // Ensure not null
                     this.keyStore.load(null, "");
                 }
             } catch (Exception e) {
                 log.error("Problem loading keystore: " +e.getMessage(), e);
             } finally {
                 JOrphanUtils.closeQuietly(fileInputStream);
             }
 
             log.debug("JmeterKeyStore type: " + this.keyStore.getClass().toString());
         }
 
         return this.keyStore;
     }
 
     /*
      * The password can be defined as a property; this dialogue is provided to allow it
      * to be entered at run-time.
      *
      * However, this does not gain much, as the dialogue does not (yet) support hidden input ...
      *
     */
     private String getPassword() {
         String password = this.defaultpw;
         if (null == password) {
             final GuiPackage guiInstance = GuiPackage.getInstance();
             if (guiInstance != null) {
                 synchronized (this) { // TODO is sync really needed?
                     this.defaultpw = JOptionPane.showInputDialog(
                             guiInstance.getMainFrame(),
                             JMeterUtils.getResString("ssl_pass_prompt"),  // $NON-NLS-1$
                             JMeterUtils.getResString("ssl_pass_title"),  // $NON-NLS-1$
                             JOptionPane.QUESTION_MESSAGE);
                     System.setProperty(KEY_STORE_PASSWORD, this.defaultpw);
                     password = this.defaultpw;
                 }
             } else {
                 log.warn("No password provided, and no GUI present so cannot prompt");
             }
         }
         return password;
     }
 
     /**
      * Opens and initializes the TrustStore.
      *
      * There are 3 possibilities:
      * - no truststore name provided, in which case the default Java truststore should be used
      * - truststore name is provided, and loads OK
      * - truststore name is provided, but is not found or does not load OK, in which case an empty
      * truststore is created
      *
      * If the KeyStore object cannot be created, then this is currently treated the same
      * as if no truststore name was provided.
      *
      * @return truststore
      * - null: use Java truststore
      * - otherwise, the truststore, which may be empty if the file could not be loaded.
      *
      */
     protected KeyStore getTrustStore() {
         if (!truststore_loaded) {
 
             truststore_loaded=true;// we've tried ...
 
             String fileName = System.getProperty(SSL_TRUST_STORE);
             if (fileName == null) {
                 return null;
             }
             log.info("TrustStore Location: " + fileName);
 
             try {
                 this.trustStore = KeyStore.getInstance("JKS");
                 log.info("TrustStore created OK, Type: JKS");
             } catch (Exception e) {
                 this.trustStore = null;
                 throw new RuntimeException("Problem creating truststore: "+e.getMessage(), e);
             }
 
             InputStream fileInputStream = null;
             try {
                 File initStore = new File(fileName);
 
                 if (initStore.exists()) {
                     fileInputStream = new BufferedInputStream(new FileInputStream(initStore));
                     this.trustStore.load(fileInputStream, null);
                     log.info("Truststore loaded OK from file");
                 } else {
                     log.info("Truststore file not found, loading empty truststore");
                     this.trustStore.load(null, null);
                 }
             } catch (Exception e) {
                 throw new RuntimeException("Can't load TrustStore: " + e.getMessage(), e);
             } finally {
                 JOrphanUtils.closeQuietly(fileInputStream);
             }
         }
 
         return this.trustStore;
     }
 
     /**
      * Protected Constructor to remove the possibility of directly instantiating
      * this object. Create the SSLContext, and wrap all the X509KeyManagers with
      * our X509KeyManager so that we can choose our alias.
      */
     protected SSLManager() {
     }
 
     /**
      * Static accessor for the SSLManager object. The SSLManager is a singleton.
      *
      * @return the singleton {@link SSLManager}
      */
     public static final synchronized SSLManager getInstance() {
         if (null == SSLManager.manager) {
             SSLManager.manager = new JsseSSLManager(null);
 //          if (SSLManager.isSSLSupported) {
 //              String classname = null;
 //              classname = "org.apache.jmeter.util.JsseSSLManager"; // $NON-NLS-1$
 //
 //              try {
 //                  Class clazz = Class.forName(classname);
 //                  Constructor con = clazz.getConstructor(new Class[] { Provider.class });
 //                  SSLManager.manager = (SSLManager) con.newInstance(new Object[] { SSLManager.sslProvider });
 //              } catch (Exception e) {
 //                  log.error("Could not create SSLManager instance", e); // $NON-NLS-1$
 //                  SSLManager.isSSLSupported = false;
 //                  return null;
 //              }
 //          }
         }
 
         return SSLManager.manager;
     }
 
     /**
      * Test whether SSL is supported or not.
      *
      * @return flag whether SSL is supported
      */
     public static final boolean isSSLSupported() {
         return SSLManager.isSSLSupported;
     }
 
     /**
      * Configure Keystore
      * 
      * @param preload
      *            flag whether the keystore should be opened within this method,
      *            or the opening should be delayed
      * @param startIndex
      *            first index to consider for a key
      * @param endIndex
      *            last index to consider for a key
      * @param clientCertAliasVarName
      *            name of the default key, if empty the first key will be used
      *            as default key
      */
     public void configureKeystore(boolean preload, int startIndex, int endIndex, String clientCertAliasVarName) {
         this.keystoreAliasStartIndex = startIndex;
         this.keystoreAliasEndIndex = endIndex;
         this.clientCertAliasVarName = clientCertAliasVarName;
         if(preload) {
             keyStore = getKeyStore();
         }
     }
 
     /**
      * Destroy Keystore
      */
     public void destroyKeystore() {
         keyStore=null;
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/collections/HashTree.java b/src/jorphan/org/apache/jorphan/collections/HashTree.java
index b915c6752..833539854 100644
--- a/src/jorphan/org/apache/jorphan/collections/HashTree.java
+++ b/src/jorphan/org/apache/jorphan/collections/HashTree.java
@@ -1,1087 +1,1103 @@
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
 
 package org.apache.jorphan.collections;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 /**
  * This class is used to create a tree structure of objects. Each element in the
  * tree is also a key to the next node down in the tree. It provides many ways
  * to add objects and branches, as well as many ways to retrieve.
  * <p>
  * HashTree implements the Map interface for convenience reasons. The main
  * difference between a Map and a HashTree is that the HashTree organizes the
  * data into a recursive tree structure, and provides the means to manipulate
  * that structure.
  * <p>
  * Of special interest is the {@link #traverse(HashTreeTraverser)} method, which
  * provides an expedient way to traverse any HashTree by implementing the
  * {@link HashTreeTraverser} interface in order to perform some operation on the
  * tree, or to extract information from the tree.
  *
  * @see HashTreeTraverser
  * @see SearchByClass
  */
 public class HashTree implements Serializable, Map<Object, HashTree>, Cloneable {
 
     private static final long serialVersionUID = 240L;
 
     // Used for the RuntimeException to short-circuit the traversal
     private static final String FOUND = "found"; // $NON-NLS-1$
 
     // N.B. The keys can be either JMeterTreeNode or TestElement
     protected final Map<Object, HashTree> data;
 
     /**
      * Creates an empty new HashTree.
      */
     public HashTree() {
         this(null, null);
     }
 
     /**
      * Allow subclasses to provide their own Map.
      * @param _map {@link Map} to use
      */
     protected HashTree(Map<Object, HashTree> _map) {
         this(_map, null);
     }
 
     /**
      * Creates a new HashTree and adds the given object as a top-level node.
      *
      * @param key
      *            name of the new top-level node
      */
     public HashTree(Object key) {
         this(new HashMap<Object, HashTree>(), key);
     }
     
     /**
-     * Uses the new HashTree if not null and adds the given object as a top-level node if not null
+     * Uses the new HashTree if not null and adds the given object as a
+     * top-level node if not null
+     *
      * @param _map
+     *            the map to be used. If <code>null</code> a new {@link HashMap}
+     *            will be created
      * @param key
+     *            the object to be used as the key for the root node (may be
+     *            <code>null</code>, in which case no root node will be created)
      */
     private HashTree(Map<Object, HashTree> _map, Object key) {
         if(_map != null) {
             data = _map;
         } else {
             data = new HashMap<Object, HashTree>();
         }
         if(key != null) {
             data.put(key, new HashTree());
         }
     }
 
     /**
      * The Map given must also be a HashTree, otherwise an
      * UnsupportedOperationException is thrown. If it is a HashTree, this is
      * like calling the add(HashTree) method.
      *
      * @see #add(HashTree)
      * @see java.util.Map#putAll(Map)
      */
     @Override
     public void putAll(Map<? extends Object, ? extends HashTree> map) {
         if (map instanceof HashTree) {
             this.add((HashTree) map);
         } else {
             throw new UnsupportedOperationException("can only putAll other HashTree objects");
         }
     }
 
     /**
      * Exists to satisfy the Map interface.
      *
      * @see java.util.Map#entrySet()
      */
     @Override
     public Set<Entry<Object, HashTree>> entrySet() {
         return data.entrySet();
     }
 
     /**
      * Implemented as required by the Map interface, but is not very useful
      * here. All 'values' in a HashTree are HashTree's themselves.
      *
      * @param value
      *            Object to be tested as a value.
      * @return True if the HashTree contains the value, false otherwise.
      * @see java.util.Map#containsValue(Object)
      */
     @Override
     public boolean containsValue(Object value) {
         return data.containsValue(value);
     }
 
     /**
      * This is the same as calling HashTree.add(key,value).
      *
      * @param key
      *            to use
      * @param value
      *            to store against key
      * @see java.util.Map#put(Object, Object)
      */
     @Override
     public HashTree put(Object key, HashTree value) {
         HashTree previous = data.get(key);
         add(key, value);
         return previous;
     }
 
     /**
      * Clears the HashTree of all contents.
      *
      * @see java.util.Map#clear()
      */
     @Override
     public void clear() {
         data.clear();
     }
 
     /**
      * Returns a collection of all the sub-trees of the current tree.
      *
      * @see java.util.Map#values()
      */
     @Override
     public Collection<HashTree> values() {
         return data.values();
     }
 
     /**
      * Adds a key as a node at the current level and then adds the given
      * HashTree to that new node.
      *
      * @param key
      *            key to create in this tree
      * @param subTree
      *            sub tree to add to the node created for the first argument.
      */
     public void add(Object key, HashTree subTree) {
         add(key).add(subTree);
     }
 
     /**
      * Adds all the nodes and branches of the given tree to this tree. Is like
      * merging two trees. Duplicates are ignored.
      *
      * @param newTree the tree to be added
      */
     public void add(HashTree newTree) {
         for (Object item : newTree.list()) {
             add(item).add(newTree.getTree(item));
         }
     }
 
     /**
      * Creates a new HashTree and adds all the objects in the given collection
      * as top-level nodes in the tree.
      *
      * @param keys
      *            a collection of objects to be added to the created HashTree.
      */
     public HashTree(Collection<?> keys) {
         data = new HashMap<Object, HashTree>();
         for (Object o : keys) {
             data.put(o, new HashTree());
         }
     }
 
     /**
      * Creates a new HashTree and adds all the objects in the given array as
      * top-level nodes in the tree.
      *
      * @param keys
      *            array with names for the new top-level nodes
      */
     public HashTree(Object[] keys) {
         data = new HashMap<Object, HashTree>();
         for (int x = 0; x < keys.length; x++) {
             data.put(keys[x], new HashTree());
         }
     }
 
     /**
      * If the HashTree contains the given object as a key at the top level, then
      * a true result is returned, otherwise false.
      *
      * @param o
      *            Object to be tested as a key.
      * @return True if the HashTree contains the key, false otherwise.
      * @see java.util.Map#containsKey(Object)
      */
     @Override
     public boolean containsKey(Object o) {
         return data.containsKey(o);
     }
 
     /**
      * If the HashTree is empty, true is returned, false otherwise.
      *
      * @return True if HashTree is empty, false otherwise.
      */
     @Override
     public boolean isEmpty() {
         return data.isEmpty();
     }
 
     /**
      * Sets a key and it's value in the HashTree. It actually sets up a key, and
      * then creates a node for the key and sets the value to the new node, as a
      * key. Any previous nodes that existed under the given key are lost.
      *
      * @param key
      *            key to be set up
      * @param value
      *            value to be set up as a key in the secondary node
      */
     public void set(Object key, Object value) {
         data.put(key, createNewTree(value));
     }
 
     /**
      * Sets a key into the current tree and assigns it a HashTree as its
      * subtree. Any previous entries under the given key are removed.
      *
      * @param key
      *            key to be set up
      * @param t
      *            HashTree that the key maps to
      */
     public void set(Object key, HashTree t) {
         data.put(key, t);
     }
 
     /**
      * Sets a key and its values in the HashTree. It sets up a key in the
      * current node, and then creates a node for that key, and sets all the
      * values in the array as keys in the new node. Any keys previously held
      * under the given key are lost.
      *
      * @param key
      *            Key to be set up
      * @param values
      *            Array of objects to be added as keys in the secondary node
      */
     public void set(Object key, Object[] values) {
         data.put(key, createNewTree(Arrays.asList(values)));
     }
 
     /**
      * Sets a key and its values in the HashTree. It sets up a key in the
      * current node, and then creates a node for that key, and set all the
      * values in the array as keys in the new node. Any keys previously held
      * under the given key are removed.
      *
      * @param key
      *            key to be set up
      * @param values
      *            Collection of objects to be added as keys in the secondary
      *            node
      */
     public void set(Object key, Collection<?> values) {
         data.put(key, createNewTree(values));
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key array as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the array.
      * Continues recursing in this manner until the end of the first array is
      * reached, at which point all the values of the second array are set as
      * keys to the bottom-most node. All previous keys of that bottom-most node
      * are removed.
      *
      * @param treePath
      *            array of keys to put into HashTree
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void set(Object[] treePath, Object[] values) {
         if (treePath != null && values != null) {
             set(Arrays.asList(treePath), Arrays.asList(values));
         }
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key array as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the array.
      * Continues recursing in this manner until the end of the first array is
      * reached, at which point all the values of the Collection of values are
      * set as keys to the bottom-most node. Any keys previously held by the
      * bottom-most node are lost.
      *
      * @param treePath
      *            array of keys to put into HashTree
      * @param values
      *            Collection of values to be added as keys to bottom-most node
      */
     public void set(Object[] treePath, Collection<?> values) {
         if (treePath != null) {
             set(Arrays.asList(treePath), values);
         }
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key list as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the list.
      * Continues recursing in this manner until the end of the first list is
      * reached, at which point all the values of the array of values are set as
      * keys to the bottom-most node. Any previously existing keys of that bottom
      * node are removed.
      *
      * @param treePath
      *            collection of keys to put into HashTree
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void set(Collection<?> treePath, Object[] values) {
         HashTree tree = addTreePath(treePath);
         tree.set(Arrays.asList(values));
     }
 
     /**
      * Sets the nodes of the current tree to be the objects of the given
      * collection. Any nodes previously in the tree are removed.
      *
      * @param values
      *            Collection of objects to set as nodes.
      */
     public void set(Collection<?> values) {
         clear();
         this.add(values);
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key list as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the list.
      * Continues recursing in this manner until the end of the first list is
      * reached, at which point all the values of the Collection of values are
      * set as keys to the bottom-most node. Any previously existing keys of that
      * bottom node are lost.
      *
      * @param treePath
      *            list of keys to put into HashTree
      * @param values
      *            collection of values to be added as keys to bottom-most node
      */
     public void set(Collection<?> treePath, Collection<?> values) {
         HashTree tree = addTreePath(treePath);
         tree.set(values);
     }
 
     /**
      * Adds an key into the HashTree at the current level. If a HashTree exists
      * for the key already, no new tree will be added
      *
      * @param key
      *            key to be added to HashTree
      * @return newly generated tree, if no tree was found for the given key;
      *         existing key otherwise
      */
     public HashTree add(Object key) {
         if (!data.containsKey(key)) {
             HashTree newTree = createNewTree();
             data.put(key, newTree);
             return newTree;
         }
         return getTree(key);
     }
 
     /**
      * Adds all the given objects as nodes at the current level.
      *
      * @param keys
      *            Array of Keys to be added to HashTree.
      */
     public void add(Object[] keys) {
         for (int x = 0; x < keys.length; x++) {
             add(keys[x]);
         }
     }
 
     /**
      * Adds a bunch of keys into the HashTree at the current level.
      *
      * @param keys
      *            Collection of Keys to be added to HashTree.
      */
     public void add(Collection<?> keys) {
         for (Object o : keys) {
             add(o);
         }
     }
 
     /**
      * Adds a key and it's value in the HashTree. The first argument becomes a
      * node at the current level, and the second argument becomes a node of it.
      *
      * @param key
      *            key to be added
      * @param value
      *            value to be added as a key in the secondary node
      * @return HashTree for which <code>value</code> is the key
      */
     public HashTree add(Object key, Object value) {
         return add(key).add(value);
     }
 
     /**
      * Adds a key and it's values in the HashTree. The first argument becomes a
      * node at the current level, and adds all the values in the array to the
      * new node.
      *
      * @param key
      *            key to be added
      * @param values
      *            array of objects to be added as keys in the secondary node
      */
     public void add(Object key, Object[] values) {
         add(key).add(values);
     }
 
     /**
      * Adds a key as a node at the current level and then adds all the objects
      * in the second argument as nodes of the new node.
      *
      * @param key
      *            key to be added
      * @param values
      *            Collection of objects to be added as keys in the secondary
      *            node
      */
     public void add(Object key, Collection<?> values) {
         add(key).add(values);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is an array that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            an array of objects representing a path
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void add(Object[] treePath, Object[] values) {
         if (treePath != null) {
             add(Arrays.asList(treePath), Arrays.asList(values));
         }
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is an array that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            an array of objects representing a path
      * @param values
      *            collection of values to be added as keys to bottom-most node
      */
     public void add(Object[] treePath, Collection<?> values) {
         if (treePath != null) {
             add(Arrays.asList(treePath), values);
         }
     }
 
     public HashTree add(Object[] treePath, Object value) {
         return add(Arrays.asList(treePath), value);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a List that represents a path to a specific node in the tree.
      * If the path doesn't already exist, it is created (the objects are added
      * along the way). At the path, all the objects in the second argument are
      * added as nodes.
      *
      * @param treePath
      *            a list of objects representing a path
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void add(Collection<?> treePath, Object[] values) {
         HashTree tree = addTreePath(treePath);
         tree.add(Arrays.asList(values));
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a List that represents a path to a specific node in the tree.
      * If the path doesn't already exist, it is created (the objects are added
      * along the way). At the path, the object in the second argument is added
      * as a node.
      *
      * @param treePath
      *            a list of objects representing a path
      * @param value
      *            Object to add as a node to bottom-most node
      * @return HashTree for which <code>value</code> is the key
      */
     public HashTree add(Collection<?> treePath, Object value) {
         HashTree tree = addTreePath(treePath);
         return tree.add(value);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a SortedSet that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            a SortedSet of objects representing a path
      * @param values
      *            Collection of values to be added as keys to bottom-most node
      */
     public void add(Collection<?> treePath, Collection<?> values) {
         HashTree tree = addTreePath(treePath);
         tree.add(values);
     }
 
     protected HashTree addTreePath(Collection<?> treePath) {
         HashTree tree = this;
         for (Object temp : treePath) {
             tree = tree.add(temp);
         }
         return tree;
     }
 
     /**
      * Gets the HashTree mapped to the given key.
      *
      * @param key
      *            Key used to find appropriate HashTree()
      * @return the HashTree for <code>key</code>
      */
     public HashTree getTree(Object key) {
         return data.get(key);
     }
 
     /**
      * Returns the HashTree object associated with the given key. Same as
      * calling {@link #getTree(Object)}.
      *
      * @see java.util.Map#get(Object)
      */
     @Override
     public HashTree get(Object key) {
         return getTree(key);
     }
 
     /**
      * Gets the HashTree object mapped to the last key in the array by recursing
      * through the HashTree structure one key at a time.
      *
      * @param treePath
      *            array of keys.
      * @return HashTree at the end of the recursion.
      */
     public HashTree getTree(Object[] treePath) {
         if (treePath != null) {
             return getTree(Arrays.asList(treePath));
         }
         return this;
     }
 
     /**
      * Create a clone of this HashTree. This is not a deep clone (ie, the
      * contents of the tree are not cloned).
      *
      */
     @Override
     public Object clone() {
         HashTree newTree = new HashTree();
         cloneTree(newTree);
         return newTree;
     }
 
     protected void cloneTree(HashTree newTree) {
         for (Object key : list()) {
             newTree.set(key, (HashTree) getTree(key).clone());
         }
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
      * @return HashTree
      */
     protected HashTree createNewTree() {
         return new HashTree();
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
      * @param key
      *            object to use as the key for the top level
      *
      * @return newly created {@link HashTree}
      */
     protected HashTree createNewTree(Object key) {
         return new HashTree(key);
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
      * @param values objects to be added to the new {@link HashTree}
      *
      * @return newly created {@link HashTree}
      */
     protected HashTree createNewTree(Collection<?> values) {
         return new HashTree(values);
     }
 
     /**
      * Gets the HashTree object mapped to the last key in the SortedSet by
      * recursing through the HashTree structure one key at a time.
      *
      * @param treePath
      *            Collection of keys
      * @return HashTree at the end of the recursion
      */
     public HashTree getTree(Collection<?> treePath) {
         return getTreePath(treePath);
     }
 
     /**
      * Gets a Collection of all keys in the current HashTree node. If the
      * HashTree represented a file system, this would be like getting a
      * collection of all the files in the current folder.
      *
      * @return Set of all keys in this HashTree
      */
     public Collection<Object> list() {
         return data.keySet();
     }
 
     /**
      * Gets a Set of all keys in the HashTree mapped to the given key of the
      * current HashTree object (in other words, one level down. If the HashTree
      * represented a file system, this would like getting a list of all files in
      * a sub-directory (of the current directory) specified by the key argument.
      *
      * @param key
      *            key used to find HashTree to get list of
      * @return Set of all keys in found HashTree.
      */
     public Collection<?> list(Object key) {
         HashTree temp = data.get(key);
         if (temp != null) {
             return temp.list();
         }
         return new HashSet<Object>();
     }
 
     /**
      * Removes the entire branch specified by the given key.
      *
      * @see java.util.Map#remove(Object)
      */
     @Override
     public HashTree remove(Object key) {
         return data.remove(key);
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * array of keys, and returns the Set of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            Array of keys used to recurse into HashTree structure
      * @return Set of all keys found in end HashTree
      */
     public Collection<?> list(Object[] treePath) { // TODO not used?
         if (treePath != null) {
             return list(Arrays.asList(treePath));
         }
         return list();
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * List of keys, and returns the Set of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            List of keys used to recurse into HashTree structure
      * @return Set of all keys found in end HashTree
      */
     public Collection<?> list(Collection<?> treePath) {
         HashTree tree = getTreePath(treePath);
         if (tree != null) {
             return tree.list();
         }
         return new HashSet<Object>();
     }
 
     /**
      * Finds the given current key, and replaces it with the given new key. Any
      * tree structure found under the original key is moved to the new key.
      *
      * @param currentKey name of the key to be replaced
      * @param newKey name of the new key
      */
     public void replaceKey(Object currentKey, Object newKey) {
         HashTree tree = getTree(currentKey);
         data.remove(currentKey);
         data.put(newKey, tree);
     }
 
     /**
      * Gets an array of all keys in the current HashTree node. If the HashTree
      * represented a file system, this would be like getting an array of all the
      * files in the current folder.
      *
      * @return array of all keys in this HashTree.
      */
     public Object[] getArray() {
         return data.keySet().toArray();
     }
 
     /**
      * Gets an array of all keys in the HashTree mapped to the given key of the
      * current HashTree object (in other words, one level down). If the HashTree
      * represented a file system, this would like getting a list of all files in
      * a sub-directory (of the current directory) specified by the key argument.
      *
      * @param key
      *            key used to find HashTree to get list of
      * @return array of all keys in found HashTree
      */
     public Object[] getArray(Object key) {
         HashTree t = getTree(key);
         if (t != null) {
             return t.getArray();
         }
         return null;
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * array of keys, and returns an array of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            array of keys used to recurse into HashTree structure
      * @return array of all keys found in end HashTree
      */
     public Object[] getArray(Object[] treePath) {
         if (treePath != null) {
             return getArray(Arrays.asList(treePath));
         }
         return getArray();
     }
 
     /**
      * Recurses down into the HashTree structure using each subsequent key in the
      * treePath argument, and returns an array of keys of the HashTree object at
      * the end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            list of keys used to recurse into HashTree structure
      * @return array of all keys found in end HashTree
      */
     public Object[] getArray(Collection<?> treePath) {
         HashTree tree = getTreePath(treePath);
         return (tree != null) ? tree.getArray() : null;
     }
 
     protected HashTree getTreePath(Collection<?> treePath) {
         HashTree tree = this;
         Iterator<?> iter = treePath.iterator();
         while (iter.hasNext()) {
             if (tree == null) {
                 return null;
             }
             Object temp = iter.next();
             tree = tree.getTree(temp);
         }
         return tree;
     }
 
     /**
      * Returns a hashcode for this HashTree.
      *
      * @see java.lang.Object#hashCode()
      */
     @Override
     public int hashCode() {
         return data.hashCode() * 7;
     }
 
     /**
      * Compares all objects in the tree and verifies that the two trees contain
      * the same objects at the same tree levels. Returns true if they do, false
      * otherwise.
      *
      * @param o
      *            Object to be compared against
      * @see java.lang.Object#equals(Object)
      */
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof HashTree)) {
             return false;
         }
         HashTree oo = (HashTree) o;
         if (oo.size() != this.size()) {
             return false;
         }
         return data.equals(oo.data);
     }
 
     /**
      * Returns a Set of all the keys in the top-level of this HashTree.
      *
      * @see java.util.Map#keySet()
      */
     @Override
     public Set<Object> keySet() {
         return data.keySet();
     }
 
     /**
      * Searches the HashTree structure for the given key. If it finds the key,
      * it returns the HashTree mapped to the key. If it finds nothing, it
      * returns null.
      *
      * @param key
      *            Key to search for
      * @return HashTree mapped to key, if found, otherwise <code>null</code>
      */
     public HashTree search(Object key) {// TODO does not appear to be used
         HashTree result = getTree(key);
         if (result != null) {
             return result;
         }
         TreeSearcher searcher = new TreeSearcher(key);
         try {
             traverse(searcher);
         } catch (RuntimeException e) {
             if (!e.getMessage().equals(FOUND)){
                 throw e;
             }
             // do nothing - means object is found
         }
         return searcher.getResult();
     }
 
     /**
      * Method readObject.
+     *
+     * @param ois
+     *            the stream to read the objects from
+     * @throws ClassNotFoundException
+     *             when the class for the deserialization can not be found
+     * @throws IOException
+     *             when I/O error occurs
      */
     private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
         ois.defaultReadObject();
     }
 
     private void writeObject(ObjectOutputStream oos) throws IOException {
         oos.defaultWriteObject();
     }
 
     /**
      * Returns the number of top-level entries in the HashTree.
      *
      * @see java.util.Map#size()
      */
     @Override
     public int size() {
         return data.size();
     }
 
     /**
      * Allows any implementation of the HashTreeTraverser interface to easily
      * traverse (depth-first) all the nodes of the HashTree. The Traverser
      * implementation will be given notification of each node visited.
      *
      * @see HashTreeTraverser
      * @param visitor
      *            the visitor that wants to traverse the tree
      */
     public void traverse(HashTreeTraverser visitor) {
         for (Object item : list()) {
             visitor.addNode(item, getTree(item));
             getTree(item).traverseInto(visitor);
         }
     }
 
     /**
      * The recursive method that accomplishes the tree-traversal and performs
      * the callbacks to the HashTreeTraverser.
+     *
+     * @param visitor
+     *            the {@link HashTreeTraverser} to be notified
      */
     private void traverseInto(HashTreeTraverser visitor) {
 
         if (list().size() == 0) {
             visitor.processPath();
         } else {
             for (Object item : list()) {
                 final HashTree treeItem = getTree(item);
                 visitor.addNode(item, treeItem);
                 treeItem.traverseInto(visitor);
             }
         }
         visitor.subtractNode();
     }
 
     /**
      * Generate a printable representation of the tree.
      *
      * @return a representation of the tree
      */
     @Override
     public String toString() {
         ConvertToString converter = new ConvertToString();
         try {
             traverse(converter);
         } catch (Exception e) { // Just in case
             converter.reportError(e);
         }
         return converter.toString();
     }
 
     private static class TreeSearcher implements HashTreeTraverser {
 
         private final Object target;
 
         private HashTree result;
 
         public TreeSearcher(Object t) {
             target = t;
         }
 
         public HashTree getResult() {
             return result;
         }
 
         /** {@inheritDoc} */
         @Override
         public void addNode(Object node, HashTree subTree) {
             result = subTree.getTree(target);
             if (result != null) {
                 // short circuit traversal when found
                 throw new RuntimeException(FOUND);
             }
         }
 
         /** {@inheritDoc} */
         @Override
         public void processPath() {
             // Not used
         }
 
         /** {@inheritDoc} */
         @Override
         public void subtractNode() {
             // Not used
         }
     }
 
     private static class ConvertToString implements HashTreeTraverser {
         private final StringBuilder string = new StringBuilder(getClass().getName() + "{");
 
         private final StringBuilder spaces = new StringBuilder();
 
         private int depth = 0;
 
         @Override
         public void addNode(Object key, HashTree subTree) {
             depth++;
             string.append("\n").append(getSpaces()).append(key);
             string.append(" {");
         }
 
         @Override
         public void subtractNode() {
             string.append("\n" + getSpaces() + "}");
             depth--;
         }
 
         @Override
         public void processPath() {
         }
 
         @Override
         public String toString() {
             string.append("\n}");
             return string.toString();
         }
 
         void reportError(Throwable t){
             string.append("Error: ").append(t.toString());
         }
 
         private String getSpaces() {
             if (spaces.length() < depth * 2) {
                 while (spaces.length() < depth * 2) {
                     spaces.append("  ");
                 }
             } else if (spaces.length() > depth * 2) {
                 spaces.setLength(depth * 2);
             }
             return spaces.toString();
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/gui/TreeTableModel.java b/src/jorphan/org/apache/jorphan/gui/TreeTableModel.java
index 760e507e8..6c789a2ae 100644
--- a/src/jorphan/org/apache/jorphan/gui/TreeTableModel.java
+++ b/src/jorphan/org/apache/jorphan/gui/TreeTableModel.java
@@ -1,56 +1,66 @@
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
 
 package org.apache.jorphan.gui;
 
 import javax.swing.table.TableModel;
 
 /**
  *
  * This is a basic interface for TreeTableModel that extends TableModel.
  * It's pretty minimal and isn't as full featured at other implementations.
  */
 public interface TreeTableModel extends TableModel {
 
     /**
-     * The method is similar to getValueAt(int,int). Instead of int,
-     * the row is an object.
+     * The method is similar to getValueAt(int,int). Instead of int, the row is
+     * an object.
+     *
      * @param node
+     *            the node which value is to be fetched
      * @param col
+     *            the column of the node
      * @return the value at the column
      */
     Object getValueAt(Object node, int col);
 
     /**
-     * the method is similar to isCellEditable(int,int). Instead of int,
-     * the row is an object.
+     * the method is similar to isCellEditable(int,int). Instead of int, the row
+     * is an object.
+     *
      * @param node
+     *            the node which value is to be fetched
      * @param col
-     * @return if cell is editable
+     *            the column of the node
+     * @return <code>true</code> if cell is editable
      */
     boolean isCellEditable(Object node, int col);
 
     /**
-     * the method is similar to isCellEditable(int,int). Instead of int,
-     * the row is an object.
+     * the method is similar to isCellEditable(int,int). Instead of int, the row
+     * is an object.
+     *
      * @param val
+     *            the value to be set
      * @param node
+     *            the node which value is to be set
      * @param column
+     *            the column of the node
      */
     void setValueAt(Object val, Object node, int column);
 }
diff --git a/src/jorphan/org/apache/jorphan/test/UnitTestManager.java b/src/jorphan/org/apache/jorphan/test/UnitTestManager.java
index 0af60f6ad..5d7fc9115 100644
--- a/src/jorphan/org/apache/jorphan/test/UnitTestManager.java
+++ b/src/jorphan/org/apache/jorphan/test/UnitTestManager.java
@@ -1,40 +1,41 @@
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
 
 package org.apache.jorphan.test;
 
 /**
  * Implement this interface to work with the AllTests class. This interface
  * allows AllTests to pass a configuration file to your application before
  * running the junit unit tests.
  * <p>
  * N.B. This interface must be in the main src/ tree (not test/) because it is
  * implemented by JMeterUtils
  * </p>
  * see JUnit class: org.apache.jorphan.test.AllTests
  */
 public interface UnitTestManager {
     /**
      * Your implementation will be handed the filename that was provided to
      * AllTests as a configuration file. It can hold whatever properties you
      * need to configure your system prior to the unit tests running.
      *
      * @param filename
+     *            path to the configuration file
      */
     void initializeProperties(String filename);
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java
index dfe80e66e..1995fb2ea 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreator.java
@@ -1,84 +1,84 @@
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
      * @param pageEncodings Map of page encodings
      * @param formEncodings Map of form encodings
      * @return {@link HTTPSamplerBase}
      */
     HTTPSamplerBase createSampler(HttpRequestHdr request,
             Map<String, String> pageEncodings, Map<String, String> formEncodings);
 
     /**
      * Populate sampler from request
      * @param sampler {@link HTTPSamplerBase}
      * @param request {@link HttpRequestHdr}
      * @param pageEncodings Map of page encodings
      * @param formEncodings Map of form encodings
-     * @throws Exception
+     * @throws Exception when something fails
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
      * @param pageEncodings Map of page encodings
      * @param formEncodings Map of form encodings
      * @return {@link HTTPSamplerBase}
-     * @throws Exception
+     * @throws Exception when something fails
      * @since 2.9
      */
     HTTPSamplerBase createAndPopulateSampler(HttpRequestHdr request,
             Map<String, String> pageEncodings, Map<String, String> formEncodings)
                     throws Exception;
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBaseConverter.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBaseConverter.java
index 6edff5200..718e5aa6a 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBaseConverter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBaseConverter.java
@@ -1,75 +1,77 @@
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
 
 /*
  * Created on Sep 14, 2004
  *
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import org.apache.jmeter.save.converters.TestElementConverter;
 
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.mapper.Mapper;
 
 /**
  * Class for XStream conversion of HTTPResult
  *
  */
 public class HTTPSamplerBaseConverter extends TestElementConverter {
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
+     *
+     * @return the version of this component
      */
     public static String getVersion() {
         return "$Revision$";  //$NON-NLS-1$
     }
 
     public HTTPSamplerBaseConverter(Mapper arg0) {
         super(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not support types
         return HTTPSamplerBase.class.isAssignableFrom(arg0);
     }
 
     /**
      * Override TestElementConverter; convert HTTPSamplerBase to merge
      * the two means of providing file names into a single list.
      * 
      * {@inheritDoc} 
      */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         final HTTPSamplerBase httpSampler = (HTTPSamplerBase) super.unmarshal(reader, context);
         // Help convert existing JMX files which use HTTPSampler[2] nodes
         String nodeName = reader.getNodeName();
         if (nodeName.equals(HTTPSamplerFactory.HTTP_SAMPLER_JAVA)){
             httpSampler.setImplementation(HTTPSamplerFactory.IMPL_JAVA);
         }
         if (nodeName.equals(HTTPSamplerFactory.HTTP_SAMPLER_APACHE)){
             httpSampler.setImplementation(HTTPSamplerFactory.IMPL_HTTP_CLIENT3_1);
         }
         httpSampler.mergeFileProperties();
         return httpSampler;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java
index d6dd06175..7aec21112 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java
@@ -1,131 +1,133 @@
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
 
 /*
  * Created on Sep 14, 2004
  *
  */
 package org.apache.jmeter.protocol.http.util;
 
 import java.net.URL;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.converters.SampleResultConverter;
 
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 
 /**
  * Class for XStream conversion of HTTPResult
  *
  */
 public class HTTPResultConverter extends SampleResultConverter {
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
+     *
+     * @return the version of this component
      */
     public static String getVersion() {
         return "$Revision$";  //$NON-NLS-1$
     }
 
     /**
      * @param arg0 the mapper
      */
     public HTTPResultConverter(Mapper arg0) {
         super(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not support types
         return HTTPSampleResult.class.equals(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
         HTTPSampleResult res = (HTTPSampleResult) obj;
         SampleSaveConfiguration save = res.getSaveConfig();
         setAttributes(writer, context, res, save);
         saveAssertions(writer, context, res, save);
         saveSubResults(writer, context, res, save);
         saveResponseHeaders(writer, context, res, save);
         saveRequestHeaders(writer, context, res, save);
         saveResponseData(writer, context, res, save);
         saveSamplerData(writer, context, res, save);
     }
 
     private void saveSamplerData(HierarchicalStreamWriter writer, MarshallingContext context, HTTPSampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveSamplerData(res)) {
             writeString(writer, TAG_COOKIES, res.getCookies());
             writeString(writer, TAG_METHOD, res.getHTTPMethod());
             writeString(writer, TAG_QUERY_STRING, res.getQueryString());
             writeString(writer, TAG_REDIRECT_LOCATION, res.getRedirectLocation());
         }
         if (save.saveUrl()) {
             writeItem(res.getURL(), context, writer);
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         HTTPSampleResult res = (HTTPSampleResult) createCollection(context.getRequiredType());
         retrieveAttributes(reader, context, res);
         while (reader.hasMoreChildren()) {
             reader.moveDown();
             Object subItem = readItem(reader, context, res);
             if (!retrieveItem(reader, context, res, subItem)) {
                 retrieveHTTPItem(reader, res, subItem);
             }
             reader.moveUp();
         }
 
         // If we have a file, but no data, then read the file
         String resultFileName = res.getResultFileName();
         if (resultFileName.length()>0
         &&  res.getResponseData().length == 0) {
             readFile(resultFileName,res);
         }
         return res;
     }
 
     private void retrieveHTTPItem(HierarchicalStreamReader reader, 
             HTTPSampleResult res, Object subItem) {
         if (subItem instanceof URL) {
             res.setURL((URL) subItem);
         } else {
             String nodeName = reader.getNodeName();
             if (nodeName.equals(TAG_COOKIES)) {
                 res.setCookies((String) subItem);
             } else if (nodeName.equals(TAG_METHOD)) {
                 res.setHTTPMethod((String) subItem);
             } else if (nodeName.equals(TAG_QUERY_STRING)) {
                 res.setQueryString((String) subItem);
             } else if (nodeName.equals(TAG_REDIRECT_LOCATION)) {
                 res.setRedirectLocation((String) subItem);
             }
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/JMeterReport.java b/src/reports/org/apache/jmeter/JMeterReport.java
index f0f4d0e02..da21c6724 100644
--- a/src/reports/org/apache/jmeter/JMeterReport.java
+++ b/src/reports/org/apache/jmeter/JMeterReport.java
@@ -1,406 +1,406 @@
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
 package org.apache.jmeter;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.Properties;
 
 import org.apache.commons.cli.avalon.CLArgsParser;
 import org.apache.commons.cli.avalon.CLOption;
 import org.apache.commons.cli.avalon.CLOptionDescriptor;
 import org.apache.commons.cli.avalon.CLUtil;
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.gui.ReportGui;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.plugin.JMeterPlugin;
 import org.apache.jmeter.plugin.PluginManager;
 import org.apache.jmeter.report.gui.ReportPageGui;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.report.gui.action.ReportCheckDirty;
 import org.apache.jmeter.report.gui.action.ReportLoad;
 import org.apache.jmeter.report.gui.tree.ReportTreeListener;
 import org.apache.jmeter.report.gui.tree.ReportTreeModel;
 import org.apache.jmeter.report.writers.gui.HTMLReportWriterGui;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  *
  * JMeterReport is the main class for the reporting component. For now,
  * the plan is to make the reporting component a separate GUI, which
  * can run in GUI or console mode. The purpose of the GUI is to design
  * reports, which can then be run. One of the primary goals of the
  * reporting component is to make it so the reports can be run in an
  * automated process.
  * The report GUI is different than the main JMeter GUI in several ways.
  * <ul>
  *   <li> the gui is not multi-threaded</li>
  *   <li> the gui uses different components</li>
  *   <li> the gui is focused on designing reports from the jtl logs
  * generated during a test run</li>
  * </ul>
  * The class follows the same design as JMeter.java. This should keep
  * things consistent and make it easier to maintain.
  */
 public class JMeterReport implements JMeterPlugin {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final int PROPFILE_OPT = 'p';
 
     private static final int PROPFILE2_OPT = 'q'; // Bug 33920 - additional
                                                     // prop files
 
     private static final int TESTFILE_OPT = 't';
 
     private static final int LOGFILE_OPT = 'l';
 
     private static final int NONGUI_OPT = 'n';
 
     private static final int HELP_OPT = 'h';
 
     private static final int VERSION_OPT = 'v';
 
     private static final int SERVER_OPT = 's';
 
     private static final int JMETER_PROPERTY = 'J';
 
     private static final int SYSTEM_PROPERTY = 'D';
 
     private static final int LOGLEVEL = 'L';
 
     private static final int REMOTE_OPT = 'r';
 
     private static final int JMETER_HOME_OPT = 'd';
 
     private static final CLOptionDescriptor[] options = new CLOptionDescriptor[] {
             new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED, HELP_OPT,
                     "print usage information and exit"),
             new CLOptionDescriptor("version", CLOptionDescriptor.ARGUMENT_DISALLOWED, VERSION_OPT,
                     "print the version information and exit"),
             new CLOptionDescriptor("propfile", CLOptionDescriptor.ARGUMENT_REQUIRED, PROPFILE_OPT,
                     "the jmeter property file to use"),
             new CLOptionDescriptor("addprop", CLOptionDescriptor.ARGUMENT_REQUIRED
                     | CLOptionDescriptor.DUPLICATES_ALLOWED, // Bug 33920 -
                                                                 // allow
                                                                 // multiple
                                                                 // props
                     PROPFILE2_OPT, "additional property file(s)"),
             new CLOptionDescriptor("testfile", CLOptionDescriptor.ARGUMENT_REQUIRED, TESTFILE_OPT,
                     "the jmeter test(.jmx) file to run"),
             new CLOptionDescriptor("logfile", CLOptionDescriptor.ARGUMENT_REQUIRED, LOGFILE_OPT,
                     "the file to log samples to"),
             new CLOptionDescriptor("nongui", CLOptionDescriptor.ARGUMENT_DISALLOWED, NONGUI_OPT,
                     "run JMeter in nongui mode"),
             new CLOptionDescriptor("server", CLOptionDescriptor.ARGUMENT_DISALLOWED, SERVER_OPT,
                     "run the JMeter server"),
             new CLOptionDescriptor("jmeterproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_PROPERTY, "Define additional JMeter properties"),
             new CLOptionDescriptor("systemproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, SYSTEM_PROPERTY, "Define additional JMeter properties"),
             new CLOptionDescriptor("loglevel", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, LOGLEVEL,
                     "Define loglevel: [category=]level e.g. jorphan=INFO or " + "jmeter.util=DEBUG"),
             new CLOptionDescriptor("runremote", CLOptionDescriptor.ARGUMENT_DISALLOWED, REMOTE_OPT,
                     "Start remote servers from non-gui mode"),
             new CLOptionDescriptor("homedir", CLOptionDescriptor.ARGUMENT_REQUIRED, JMETER_HOME_OPT,
                     "the jmeter home directory to use"), };
 
     /**
      *
      */
     public JMeterReport() {
         super();
     }
 
     /**
      * The default icons for the report GUI.
      */
     private static final String[][] DEFAULT_ICONS = {
             { AbstractListenerGui.class.getName(), "org/apache/jmeter/images/meter.png" },
             { AbstractConfigGui.class.getName(), "org/apache/jmeter/images/testtubes.png" },
             { HTMLReportWriterGui.class.getName(), "org/apache/jmeter/images/new/pencil.png" },
             { ReportPageGui.class.getName(), "org/apache/jmeter/images/new/scroll.png" },
             { ReportGui.class.getName(), "org/apache/jmeter/images/new/book.png" }
     };
 
     /** {@inheritDoc} */
     @Override
     public String[][] getIconMappings() {
         String iconProp = JMeterUtils.getPropDefault("jmeter.icons", "org/apache/jmeter/images/icon.properties");
         Properties p = JMeterUtils.loadProperties(iconProp);
         if (p == null) {
             log.info(iconProp + " not found - using default icon set");
             return DEFAULT_ICONS;
         }
         log.info("Loaded icon properties from " + iconProp);
         String[][] iconlist = new String[p.size()][3];
         Enumeration<Object> pe = p.keys();
         int i = 0;
         while (pe.hasMoreElements()) {
             String key = (String) pe.nextElement();
             String icons[] = JOrphanUtils.split(p.getProperty(key), " ");
             iconlist[i][0] = key;
             iconlist[i][1] = icons[0];
             if (icons.length > 1){
                 iconlist[i][2] = icons[1];
             }
             i++;
         }
         return iconlist;
     }
 
     /** {@inheritDoc} */
     @Override
     public String[][] getResourceBundles() {
         return new String[0][];
     }
 
     public void startNonGui(CLOption testFile, CLOption logFile){
         System.setProperty(JMeter.JMETER_NON_GUI, "true");
         PluginManager.install(this, false);
     }
 
     public void startGui(CLOption testFile) {
         PluginManager.install(this, true);
         ReportTreeModel treeModel = new ReportTreeModel();
         ReportTreeListener treeLis = new ReportTreeListener(treeModel);
         treeLis.setActionHandler(ReportActionRouter.getInstance());
         ReportGuiPackage.getInstance(treeLis, treeModel);
         org.apache.jmeter.gui.ReportMainFrame main =
             new org.apache.jmeter.gui.ReportMainFrame(
                 treeModel, treeLis);
         ComponentUtil.centerComponentInWindow(main, 80);
         main.setVisible(true);
 
         ReportActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, ReportCheckDirty.ADD_ALL));
         if (testFile != null) {
             FileInputStream reader = null;
             try {
                 File f = new File(testFile.getArgument());
                 log.info("Loading file: " + f);
                 reader = new FileInputStream(f);
                 HashTree tree = SaveService.loadTree(reader);
 
                 ReportGuiPackage.getInstance().setReportPlanFile(f.getAbsolutePath());
 
                 new ReportLoad().insertLoadedTree(1, tree);
             } catch (Exception e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(e.toString());
             }
             finally{
                 JOrphanUtils.closeQuietly(reader);
             }
         }
     }
 
 //    private void run(String testFile, String logFile, boolean remoteStart) {
 //        FileInputStream reader = null;
 //        try {
 //            File f = new File(testFile);
 //            if (!f.exists() || !f.isFile()) {
 //                System.out.println("Could not open " + testFile);
 //                return;
 //            }
 //            FileServer.getFileServer().setBasedir(f.getAbsolutePath());
 //
 //            reader = new FileInputStream(f);
 //            log.info("Loading file: " + f);
 //
 //            HashTree tree = SaveService.loadTree(reader);
 //
 //            // Remove the disabled items
 //            // For GUI runs this is done in Start.java
 //            convertSubTree(tree);
 //
 //            if (logFile != null) {
 //                ResultCollector logger = new ResultCollector();
 //                logger.setFilename(logFile);
 //                tree.add(tree.getArray()[0], logger);
 //            }
 //            String summariserName = JMeterUtils.getPropDefault(
 //                    "summariser.name", "");//$NON-NLS-1$
 //            if (summariserName.length() > 0) {
 //                log.info("Creating summariser <" + summariserName + ">");
 //                System.out.println("Creating summariser <" + summariserName + ">");
 //                Summariser summer = new Summariser(summariserName);
 //                tree.add(tree.getArray()[0], summer);
 //            }
 //            tree.add(tree.getArray()[0], new ListenToTest(parent));
 //            System.out.println("Created the tree successfully");
 //            /**
 //            JMeterEngine engine = null;
 //            if (!remoteStart) {
 //                engine = new StandardJMeterEngine();
 //                engine.configure(tree);
 //                System.out.println("Starting the test");
 //                engine.runTest();
 //            } else {
 //                String remote_hosts_string = JMeterUtils.getPropDefault(
 //                        "remote_hosts", "127.0.0.1");
 //                java.util.StringTokenizer st = new java.util.StringTokenizer(
 //                        remote_hosts_string, ",");
 //                List engines = new LinkedList();
 //                while (st.hasMoreElements()) {
 //                    String el = (String) st.nextElement();
 //                    System.out.println("Configuring remote engine for " + el);
 //                    // engines.add(doRemoteInit(el.trim(), tree));
 //                }
 //                System.out.println("Starting remote engines");
 //                Iterator iter = engines.iterator();
 //                while (iter.hasNext()) {
 //                    engine = (JMeterEngine) iter.next();
 //                    engine.runTest();
 //                }
 //                System.out.println("Remote engines have been started");
 //            }
 //            **/
 //        } catch (Exception e) {
 //            System.out.println("Error in NonGUIDriver " + e.toString());
 //            log.error("", e);
 //        }
 //        finally{
 //            JOrphanUtils.closeQuietly(reader);
 //        }
 //    }
 
 
     /**
      *
-     * @param args
+     * @param args the arguments to parse
      */
     public void start(String[] args) {
         CLArgsParser parser = new CLArgsParser(args, options);
         if (null != parser.getErrorString()) {
             System.err.println("Error: " + parser.getErrorString());
             System.out.println("Usage");
             System.out.println(CLUtil.describeOptions(options).toString());
             return;
         }
         try {
             initializeProperties(parser);
             log.info("Version " + JMeterUtils.getJMeterVersion());
             log.info("java.version=" + System.getProperty("java.version"));
             log.info(JMeterUtils.getJMeterCopyright());
             if (parser.getArgumentById(VERSION_OPT) != null) {
                 System.out.println(JMeterUtils.getJMeterCopyright());
                 System.out.println("Version " + JMeterUtils.getJMeterVersion());
             } else if (parser.getArgumentById(HELP_OPT) != null) {
                 System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));
             } else if (parser.getArgumentById(NONGUI_OPT) == null) {
                 startGui(parser.getArgumentById(TESTFILE_OPT));
             } else {
                 startNonGui(parser.getArgumentById(TESTFILE_OPT), parser.getArgumentById(LOGFILE_OPT));
             }
         } catch (Exception e) {
             e.printStackTrace();
             System.out.println("An error occurred: " + e.getMessage());
             System.exit(-1);
         }
     }
 
     private void initializeProperties(CLArgsParser parser) {
         if (parser.getArgumentById(PROPFILE_OPT) != null) {
             JMeterUtils.getProperties(parser.getArgumentById(PROPFILE_OPT).getArgument());
         } else {
             JMeterUtils.getProperties(NewDriver.getJMeterDir() + File.separator + "bin" + File.separator
                     + "jmeter.properties");
         }
 
         // Bug 33845 - allow direct override of Home dir
         if (parser.getArgumentById(JMETER_HOME_OPT) == null) {
             JMeterUtils.setJMeterHome(NewDriver.getJMeterDir());
         } else {
             JMeterUtils.setJMeterHome(parser.getArgumentById(JMETER_HOME_OPT).getArgument());
         }
 
         // Process command line property definitions (can occur multiple times)
 
         Properties jmeterProps = JMeterUtils.getJMeterProperties();
         List<CLOption> clOptions = parser.getArguments();
         int size = clOptions.size();
 
         for (int i = 0; i < size; i++) {
             CLOption option = clOptions.get(i);
             String name = option.getArgument(0);
             String value = option.getArgument(1);
 
             switch (option.getDescriptor().getId()) {
             case PROPFILE2_OPT: // Bug 33920 - allow multiple props
                 File f = new File(name);
                 FileInputStream inStream = null;
                 try {
                     inStream = new FileInputStream(f);
                     jmeterProps.load(inStream);
                 } catch (FileNotFoundException e) {
                     log.warn("Can't find additional property file: " + name, e);
                 } catch (IOException e) {
                     log.warn("Error loading additional property file: " + name, e);
                 } finally {
                     IOUtils.closeQuietly(inStream);
                 }
                 break;
             case SYSTEM_PROPERTY:
                 if (value.length() > 0) { // Set it
                     log.info("Setting System property: " + name + "=" + value);
                     System.getProperties().setProperty(name, value);
                 } else { // Reset it
                     log.warn("Removing System property: " + name);
                     System.getProperties().remove(name);
                 }
                 break;
             case JMETER_PROPERTY:
                 if (value.length() > 0) { // Set it
                     log.info("Setting JMeter property: " + name + "=" + value);
                     jmeterProps.setProperty(name, value);
                 } else { // Reset it
                     log.warn("Removing JMeter property: " + name);
                     jmeterProps.remove(name);
                 }
                 break;
             case LOGLEVEL:
                 if (value.length() > 0) { // Set category
                     log.info("LogLevel: " + name + "=" + value);
                     LoggingManager.setPriority(value, name);
                 } else { // Set root level
                     log.warn("LogLevel: " + name);
                     LoggingManager.setPriority(name);
                 }
                 break;
             default:
                 // ignored
                 break;
             }
         }
 
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java b/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java
index ffeff1c6d..f4fa52eb9 100644
--- a/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java
+++ b/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java
@@ -1,618 +1,616 @@
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
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.swing.JPopupMenu;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.report.engine.ValueReplacer;
 import org.apache.jmeter.report.gui.tree.ReportTreeListener;
 import org.apache.jmeter.report.gui.tree.ReportTreeModel;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.TestBeanGUI;
 import org.apache.jmeter.testelement.ReportPlan;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * ReportGuiPackage is based on GuiPackage, but with changes for
  * the reporting tool. Because of how the gui components work, it
  * was safer to just make a new class, rather than braking existing
  * JMeter gui code.
  *
  */
 public final class ReportGuiPackage implements LocaleChangeListener {
     /** Logging. */
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Object LOCK = new Object();
 
     /** Singleton instance. */
     private static volatile ReportGuiPackage guiPack;
 
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
     private ReportTreeNode currentNode = null;
 
     private boolean currentNodeUpdated = false;
 
     /** The model for JMeter's test tree. */
     private ReportTreeModel treeModel;
 
     /** The listener for JMeter's test tree. */
     private ReportTreeListener treeListener;
 
     /** The main JMeter frame. */
     private ReportMainFrame mainFrame;
 
     /**
      * Private constructor to permit instantiation only from within this class.
      * Use {@link #getInstance()} to retrieve a singleton instance.
      */
     private ReportGuiPackage() {
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     /**
      * Retrieve the singleton GuiPackage instance.
      *
      * @return the GuiPackage instance
      */
     public static ReportGuiPackage getInstance() {
         if (guiPack == null){
             log.error("ReportGuiPackage is null");
         }
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
     public static ReportGuiPackage getInstance(ReportTreeListener listener, ReportTreeModel treeModel) {
         if (guiPack == null) {
             synchronized (LOCK) {
                 if(guiPack== null) {
                     guiPack = new ReportGuiPackage();
                     guiPack.setTreeListener(listener);
                     guiPack.setTreeModel(treeModel);
                 }
             }
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
             if (testClassName.equals("")) {
                 testClass = node.getClass();
             } else {
                 testClass = Class.forName(testClassName);
             }
             Class<?> guiClass = null;
             if (!guiClassName.equals("")) {
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
     public ReportTreeNode getNodeOf(TestElement userObject) {
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
             throw new RuntimeException(e.toString(), e); // Probably a missing
                                                         // jar
         } catch (ClassNotFoundException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error:
                                                         // bail out.
         } catch (InstantiationException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error:
                                                         // bail out.
         } catch (IllegalAccessException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error:
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
-     * @throws ClassNotFoundException
-     *             if the specified GUI class cannot be found
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
                 comp.modifyTestElement(el);
             }
             if (currentNode != treeListener.getCurrentNode()) {
                 currentNodeUpdated = true;
             }
             currentNode = treeListener.getCurrentNode();
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
         }
     }
 
     public ReportTreeNode getCurrentNode() {
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
     public ReportTreeModel getTreeModel() {
         return treeModel;
     }
 
     /**
      * Set the model for JMeter's test tree.
      *
      * @param newTreeModel
      *            the new JMeter tree model
      */
     public void setTreeModel(ReportTreeModel newTreeModel) {
         treeModel = newTreeModel;
     }
 
     /**
      * Get a ValueReplacer for the test tree.
      *
      * @return a ValueReplacer configured for the test tree
      */
     public ValueReplacer getReplacer() {
         return new ValueReplacer((ReportPlan) ((ReportTreeNode) getTreeModel().getReportPlan().getArray()[0])
                 .getTestElement());
     }
 
     /**
      * Set the main JMeter frame.
      *
      * @param newMainFrame
      *            the new JMeter main frame
      */
     public void setMainFrame(ReportMainFrame newMainFrame) {
         this.mainFrame = newMainFrame;
     }
 
     /**
      * Get the main JMeter frame.
      *
      * @return the main JMeter frame
      */
     public ReportMainFrame getMainFrame() {
         return this.mainFrame;
     }
 
     /**
      * Set the listener for JMeter's test tree.
      *
      * @param newTreeListener
      *            the new JMeter test tree listener
      */
     public void setTreeListener(ReportTreeListener newTreeListener) {
         treeListener = newTreeListener;
     }
 
     /**
      * Get the listener for JMeter's test tree.
      *
      * @return the JMeter test tree listener
      */
     public ReportTreeListener getTreeListener() {
         return treeListener;
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
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.util.LocaleChangeListener#localeChanged(org.apache.jmeter.util.LocaleChangeEvent)
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
         ReportMainFrame mf = getMainFrame(); // Fetch once
         if (mf == null) // Probably caused by unit testing on headless system
         {
             log.warn("Mainframe is null");
         } else {
             mf.setMainPanel((javax.swing.JComponent) getCurrentGui());
             mf.setEditMenu(getTreeListener().getCurrentNode().createPopupMenu());
         }
     }
 
     private String reportPlanFile;
 
     /**
      * Sets the filepath of the current test plan. It's shown in the main frame
      * title and used on saving.
      *
-     * @param f
+     * @param f the path of the file to save the test plan
      */
     public void setReportPlanFile(String f) {
         reportPlanFile = f;
         ReportGuiPackage.getInstance().getMainFrame().setExtendedFrameTitle(reportPlanFile);
         try {
             FileServer.getFileServer().setBasedir(reportPlanFile);
         } catch (IllegalStateException e1) {
             log.error("Failure setting file server's base dir", e1);
         }
     }
 
     public String getReportPlanFile() {
         return reportPlanFile;
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java b/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java
index 8ebf222e2..7ee3dd86a 100644
--- a/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java
+++ b/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java
@@ -1,158 +1,161 @@
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
 
 import java.awt.Color;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.util.JMeterUtils;
 
 public class DirectoryPanel extends HorizontalPanel implements ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final String ACTION_BROWSE = "browse"; // $NON-NSL-1$
 
     private final JTextField filename = new JTextField(20);
 
     private final JButton browse = new JButton(JMeterUtils.getResString("browse")); // $NON-NLS-1$
 
     private final List<ChangeListener> listeners = new LinkedList<ChangeListener>();
 
     private final String title;
     
     private final Color background;
 
     /**
      * Constructor for the FilePanel object. No {@link ChangeListener} is registered
      * and an empty title is assumed. 
      */
     public DirectoryPanel() {
         this("", null); // $NON-NLS-1$
     }
 
     /**
-     * Constructor for the FilePanel object. No {@link ChangeListener} is registered.
+     * Constructor for the FilePanel object. No {@link ChangeListener} is
+     * registered.
+     *
      * @param title
+     *            the title of this component
      */
     public DirectoryPanel(String title) {
         this(title, null);
     }
 
     /**
      * Constructor for the FilePanel object. No {@link ChangeListener} is registered.
      * @param title The title of the panel
      * @param bk The {@link Color} of the background of this panel
      */
     public DirectoryPanel(String title, Color bk) {
         this.title = title;
         this.background = bk;
         init();
     }
     /**
      * Constructor for the FilePanel object.
      * @param l The {@link ChangeListener} to which we report events
      * @param title The title of the panel
      */
     public DirectoryPanel(ChangeListener l, String title) {
         this(title);
         listeners.add(l);
     }
 
     /**
      * Add a {@link ChangeListener} to this panel
      * @param l The {@link ChangeListener} to add
      */
     public void addChangeListener(ChangeListener l) {
         listeners.add(l);
     }
 
     private void init() {
         setBackground(this.background);
         setBorder(BorderFactory.createTitledBorder(title));
         add(Box.createHorizontalStrut(5));
         add(filename);
         add(Box.createHorizontalStrut(5));
         filename.addActionListener(this);
         add(browse);
         browse.setActionCommand(ACTION_BROWSE);
         browse.addActionListener(this);
     }
 
     /**
      * If the gui needs to enable/disable the FilePanel, call the method.
      *
      * @param enable specifies whether the FilePanel should be enabled or disabled
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
         Iterator<ChangeListener> iter = listeners.iterator();
         while (iter.hasNext()) {
             iter.next().stateChanged(new ChangeEvent(this));
         }
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals(ACTION_BROWSE)) {
             JFileChooser chooser = DirectoryDialoger.promptToOpenFile();
             if (chooser.getSelectedFile() != null) {
                 filename.setText(chooser.getSelectedFile().getPath());
                 fireFileChanged();
             }
         } else {
             fireFileChanged();
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/util/ReportFileDialoger.java b/src/reports/org/apache/jmeter/gui/util/ReportFileDialoger.java
index 2944dbcaf..5d096bbe5 100644
--- a/src/reports/org/apache/jmeter/gui/util/ReportFileDialoger.java
+++ b/src/reports/org/apache/jmeter/gui/util/ReportFileDialoger.java
@@ -1,141 +1,148 @@
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
 
 import java.io.File;
 
 import javax.swing.JFileChooser;
 import javax.swing.filechooser.FileFilter;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.JMeterFileFilter;
 
 public final class ReportFileDialoger {
     /**
      * The last directory visited by the user while choosing Files.
      */
     private static String lastJFCDirectory = null;
 
     private static JFileChooser jfc = new JFileChooser();
 
     /**
      * Prevent instantiation of utility class.
      */
     private ReportFileDialoger() {
     }
 
     /**
      * Prompts the user to choose a file from their filesystems for our own
      * devious uses. This method maintains the last directory the user visited
      * before dismissing the dialog. This does NOT imply they actually chose a
      * file from that directory, only that they closed the dialog there. It is
      * the caller's responsibility to check to see if the selected file is
      * non-null.
      *
+     * @param exts
+     *            non <code>null</code> array of allowed file extensions. If
+     *            empty, all extensions are allowed
      * @return the JFileChooser that interacted with the user, after they are
      *         finished using it (accept or otherwise).
      */
     public static JFileChooser promptToOpenFile(String[] exts) {
         // JFileChooser jfc = null;
 
         if (lastJFCDirectory == null) {
             String start = System.getProperty("user.dir", "");
 
             if (!start.equals("")) {
                 jfc.setCurrentDirectory(new File(start));
             }
         }
         clearFileFilters();
         jfc.addChoosableFileFilter(new JMeterFileFilter(exts));
         int retVal = jfc.showOpenDialog(ReportGuiPackage.getInstance().getMainFrame());
         lastJFCDirectory = jfc.getCurrentDirectory().getAbsolutePath();
 
         if (retVal == JFileChooser.APPROVE_OPTION) {
             return jfc;
         } else {
             return null;
         }
     }
 
     private static void clearFileFilters() {
         FileFilter[] filters = jfc.getChoosableFileFilters();
         for (int x = 0; x < filters.length; x++) {
             jfc.removeChoosableFileFilter(filters[x]);
         }
     }
 
     public static JFileChooser promptToOpenFile() {
         return promptToOpenFile(new String[0]);
     }
 
     /**
      * Prompts the user to choose a file from their filesystems for our own
      * devious uses. This method maintains the last directory the user visited
      * before dismissing the dialog. This does NOT imply they actually chose a
      * file from that directory, only that they closed the dialog there. It is
      * the caller's responsibility to check to see if the selected file is
      * non-null.
      *
+     * @param filename name of selected file
      * @return the JFileChooser that interacted with the user, after they are
      *         finished using it (accept or otherwise).
      * @see #promptToOpenFile()
      */
     public static JFileChooser promptToSaveFile(String filename) {
         return promptToSaveFile(filename, null);
     }
 
     /**
      * Get a JFileChooser with a new FileFilter.
      *
      * @param filename
+     *            name of a selected file
      * @param extensions
+     *            list of extensions to allow. If <code>null</code>, files with
+     *            <em>.jmx</em> extension will be allowed only
      * @return JFileChooser
      */
     public static JFileChooser promptToSaveFile(String filename, String[] extensions) {
         if (lastJFCDirectory == null) {
             String start = System.getProperty("user.dir", "");
             if (!start.equals("")) {
                 jfc = new JFileChooser(new File(start));
             }
             lastJFCDirectory = jfc.getCurrentDirectory().getAbsolutePath();
         }
         String ext = ".jmx";
         if (filename != null) {
             jfc.setSelectedFile(new File(lastJFCDirectory, filename));
             int i = -1;
             if ((i = filename.lastIndexOf('.')) > -1) {
                 ext = filename.substring(i);
             }
         }
         clearFileFilters();
         if (extensions != null) {
             jfc.addChoosableFileFilter(new JMeterFileFilter(extensions));
         } else {
             jfc.addChoosableFileFilter(new JMeterFileFilter(new String[] { ext }));
         }
 
         int retVal = jfc.showSaveDialog(ReportGuiPackage.getInstance().getMainFrame());
         lastJFCDirectory = jfc.getCurrentDirectory().getAbsolutePath();
         if (retVal == JFileChooser.APPROVE_OPTION) {
             return jfc;
         } else {
             return null;
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java b/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java
index 0105112e3..cfd8654ad 100644
--- a/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java
+++ b/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java
@@ -1,144 +1,149 @@
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
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.util.JMeterUtils;
 
 public class ReportFilePanel extends HorizontalPanel implements ActionListener {
     private static final long serialVersionUID = 240L;
 
     private final JTextField filename = new JTextField(20);
 
     private final JLabel label = new JLabel(JMeterUtils.getResString("file_visualizer_filename")); // $NON-NLS-1$
 
     private final JButton browse = new JButton(JMeterUtils.getResString("browse")); // $NON-NLS-1$
 
     private final List<ChangeListener> listeners = new LinkedList<ChangeListener>();
 
     private final String title;
 
     private final String filetype;
 
     /**
      * Constructor for the FilePanel object.
      */
     public ReportFilePanel() {
         this(""); // $NON-NLS-1$
     }
 
     public ReportFilePanel(String title) {
         this(title, null);
     }
 
     public ReportFilePanel(String title, String filetype) {
         this.title = title;
         this.filetype = filetype;
         init();
     }
 
     /**
      * Constructor for the FilePanel object.
+     *
+     * @param l
+     *            {@link ChangeListener} to be notified of changes
+     * @param title
+     *            the title of this component
      */
     public ReportFilePanel(ChangeListener l, String title) {
         this(title);
         listeners.add(l);
     }
 
     public void addChangeListener(ChangeListener l) {
         listeners.add(l);
     }
 
     private void init() {
         setBorder(BorderFactory.createTitledBorder(title));
         add(label);
         add(Box.createHorizontalStrut(5));
         add(filename);
         add(Box.createHorizontalStrut(5));
         filename.addActionListener(this);
         add(browse);
         browse.setActionCommand("browse");
         browse.addActionListener(this);
 
     }
 
     /**
      * If the gui needs to enable/disable the FilePanel, call the method.
      *
-     * @param enable
+     * @param enable flag whether to enable the FilePanel
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
         Iterator<ChangeListener> iter = listeners.iterator();
         while (iter.hasNext()) {
             iter.next().stateChanged(new ChangeEvent(this));
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals("browse")) {
             JFileChooser chooser = ReportFileDialoger.promptToOpenFile(new String[] { filetype });
             if (chooser != null && chooser.getSelectedFile() != null) {
                 filename.setText(chooser.getSelectedFile().getPath());
                 fireFileChanged();
             }
         } else {
             fireFileChanged();
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/testelement/JTLData.java b/src/reports/org/apache/jmeter/testelement/JTLData.java
index 32a3b4bd0..768dbddcd 100644
--- a/src/reports/org/apache/jmeter/testelement/JTLData.java
+++ b/src/reports/org/apache/jmeter/testelement/JTLData.java
@@ -1,235 +1,238 @@
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
 
 import java.io.File;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.apache.jmeter.report.DataSet;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.visualizers.SamplingStatCalculator;
 
 /**
  *
  * The purpose of TableData is to contain the results of a single .jtl file.
  * It is equivalent to what the AggregateListener table. A HashMap is used
  * to store the data. The URL is the key and the value is SamplingStatCalculator
  */
 public class JTLData implements Serializable, DataSet {
 
     private static final long serialVersionUID = 240L;
 
     private final HashMap<String, SamplingStatCalculator> data = new HashMap<String, SamplingStatCalculator>();
     private String jtl_file = null;
     private long startTimestamp = 0;
     private long endTimestamp = 0;
     private transient File inputFile = null;
 
     /**
      *
      */
     public JTLData() {
         super();
     }
 
     /**
      * Return a Set of the URLs
      * @return set of URLs
      */
     @Override
     public Set<?> getURLs() {
         return this.data.keySet();
     }
 
     /**
      * Return a Set of the values
      * @return values
      */
     @Override
     public Set<SamplingStatCalculator>  getStats() {
         return (Set<SamplingStatCalculator>) this.data.values();
     }
 
     /**
-     * The purpose of the method is to make it convienant to pass a list
-     * of the URLs and return a list of the SamplingStatCalculators. If
-     * no URLs match, the list is empty.
-     * TODO - this method seems to be wrong - it does not agree with the Javadoc
-     * The SamplingStatCalculators will be returned in the same sequence
-     * as the url list.
+     * The purpose of the method is to make it convienant to pass a list of the
+     * URLs and return a list of the SamplingStatCalculators. If no URLs match,
+     * the list is empty.
+     * <p>
+     * TODO - this method seems to be wrong - it does not
+     * agree with the Javadoc The SamplingStatCalculators will be returned in
+     * the same sequence as the url list.
+     *
      * @param urls
+     *            the URLs for which to get statistics
      * @return array list of non-null entries (may be empty)
      */
     @SuppressWarnings({ "rawtypes", "unchecked" }) // Method is broken anyway
     @Override
     public List getStats(List urls) {
         ArrayList items = new ArrayList();
         Iterator itr = urls.iterator();
         if (itr.hasNext()) {
             SamplingStatCalculator row = (SamplingStatCalculator)itr.next();
             if (row != null) {
                 items.add(row);
             }
         }
         return items;
     }
 
     @Override
     public void setDataSource(String absolutePath) {
         this.jtl_file = absolutePath;
     }
 
     @Override
     public String getDataSource() {
         return this.jtl_file;
     }
 
     @Override
     public String getDataSourceName() {
         if (inputFile == null) {
             inputFile = new File(getDataSource());
         }
         return inputFile.getName().substring(0,inputFile.getName().length() - 4);
     }
 
     @Override
     public void setStartTimestamp(long stamp) {
         this.startTimestamp = stamp;
     }
 
     @Override
     public long getStartTimestamp() {
         return this.startTimestamp;
     }
 
     @Override
     public void setEndTimestamp(long stamp) {
         this.endTimestamp = stamp;
     }
 
     @Override
     public long getEndTimestamp() {
         return this.endTimestamp;
     }
 
     /**
      * The date we use for the result is the start timestamp. The
      * reasoning is that a test may run for a long time, but it
      * is most likely scheduled to run using CRON on unix or
      * scheduled task in windows.
      * @return start time
      */
     @Override
     public Date getDate() {
         return new Date(this.startTimestamp);
     }
 
     @Override
     public String getMonthDayDate() {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(this.startTimestamp);
         return String.valueOf(cal.get(Calendar.MONTH)) + " - " +
         String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
     }
 
     @Override
     public String getMonthDayYearDate() {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(this.startTimestamp);
         return String.valueOf(cal.get(Calendar.MONTH)) + " - " +
             String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + " - " +
             String.valueOf(cal.get(Calendar.YEAR));
     }
 
     /**
      * The method will SamplingStatCalculator for the given URL. If the URL
      * doesn't exist, the method returns null.
-     * @param url
+     * @param url the URL for which to get statistics
      * @return data for this URL
      */
     @Override
     public SamplingStatCalculator getStatistics(String url) {
         if (this.data.containsKey(url)) {
             return this.data.get(url);
         } else {
             return null;
         }
     }
 
     /**
      * The implementation loads a single .jtl file and cleans up the
      * ResultCollector.
      */
     @Override
     public void loadData() {
         if (this.getDataSource() != null) {
             ResultCollector rc = new ResultCollector();
             rc.setFilename(this.getDataSource());
             rc.setListener(this);
             rc.loadExistingFile();
             // we clean up the ResultCollector to make sure there's
             // no slow leaks
             rc.clear();
             rc.setListener(null);
         }
     }
 
     /**
      * the implementation will set the start timestamp if the HashMap
      * is empty. otherwise it will set the end timestamp using the
      * end time
      */
     @Override
     public void add(SampleResult sample) {
         if (data.size() == 0) {
             this.startTimestamp = sample.getStartTime();
         } else {
             this.endTimestamp = sample.getEndTime();
         }
         // now add the samples to the HashMap
         String url = sample.getSampleLabel();
         if (url == null) {
             url = sample.getURL().toString();
         }
         SamplingStatCalculator row = data.get(url);
         if (row == null) {
             row = new SamplingStatCalculator(url);
             // just like the aggregate listener, we use the sample label to represent
             // a row. in this case, we use it as a key.
             this.data.put(url,row);
         }
         row.addSample(sample);
     }
 
     /**
      * By default, the method always returns true. Subclasses can over
      * ride the implementation.
      */
     @Override
     public boolean isStats() {
         return true;
     }
 }
