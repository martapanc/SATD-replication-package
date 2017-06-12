diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/gui/AuthPanel.java b/src/protocol/http/org/apache/jmeter/protocol/http/gui/AuthPanel.java
index ec5048d06..9773d6442 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/gui/AuthPanel.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/gui/AuthPanel.java
@@ -1,455 +1,455 @@
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
 
 package org.apache.jmeter.protocol.http.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 
 import javax.swing.BorderFactory;
 import javax.swing.DefaultCellEditor;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JFileChooser;
 import javax.swing.JPanel;
 import javax.swing.JPasswordField;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.AbstractTableModel;
 import javax.swing.table.TableCellRenderer;
 import javax.swing.table.TableColumn;
 
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.AuthManager.Mechanism;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.layout.VerticalLayout;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * Handles input for determining if authentication services are required for a
  * Sampler. It also understands how to get AuthManagers for the files that the
  * user selects.
  */
 public class AuthPanel extends AbstractConfigGui implements ActionListener {
-    private static final long serialVersionUID = -378312656300713636L;
+    private static final long serialVersionUID = -378312656300713635L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AuthPanel.class);
 
     private static final String ADD_COMMAND = "Add"; //$NON-NLS-1$
 
     private static final String DELETE_COMMAND = "Delete"; //$NON-NLS-1$
 
     private static final String LOAD_COMMAND = "Load"; //$NON-NLS-1$
 
     private static final String SAVE_COMMAND = "Save"; //$NON-NLS-1$
 
     private InnerTableModel tableModel;
 
     private JCheckBox clearEachIteration;
 
     /**
      * A table to show the authentication information.
      */
     private JTable authTable;
 
     private JButton addButton;
 
     private JButton deleteButton;
 
     private JButton loadButton;
 
     private JButton saveButton;
 
     /**
      * Default Constructor.
      */
     public AuthPanel() {
         tableModel = new InnerTableModel();
         init();
     }
 
     @Override
     public TestElement createTestElement() {
         AuthManager authMan = tableModel.manager;
         configureTestElement(authMan);
         authMan.setClearEachIteration(clearEachIteration.isSelected());
         return (TestElement) authMan.clone();
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement el) {
         GuiUtils.stopTableEditing(authTable);
         AuthManager authManager = (AuthManager) el;
         authManager.clear();
         authManager.addTestElement((TestElement) tableModel.manager.clone());
         authManager.setClearEachIteration(clearEachIteration.isSelected());
         configureTestElement(el);
     }
 
     /**
      * Implements JMeterGUIComponent.clear
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         tableModel.clearData();
         deleteButton.setEnabled(false);
         saveButton.setEnabled(false);
         clearEachIteration.setSelected(false);
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         tableModel.manager.clear();
         tableModel.manager.addTestElement((AuthManager) el.clone());
         clearEachIteration.setSelected(((AuthManager) el).getClearEachIteration());
         checkButtonsStatus();
     }
 
     @Override
     public String getLabelResource() {
         return "auth_manager_title"; //$NON-NLS-1$
     }
 
     /**
      * Shows the main authentication panel for this object.
      */
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout());
         setBorder(makeBorder());
 
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         northPanel.add(makeTitlePanel());
 
         JPanel optionsPane = new JPanel();
         optionsPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("auth_manager_options"))); // $NON-NLS-1$
         optionsPane.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         clearEachIteration = 
                 new JCheckBox(JMeterUtils.getResString("auth_manager_clear_per_iter"), false); //$NON-NLS-1$
         optionsPane.add(clearEachIteration);
         northPanel.add(optionsPane);
         add(northPanel, BorderLayout.NORTH);
 
         
         add(createAuthTablePanel(), BorderLayout.CENTER);
     }
 
     /**
      * Remove the currently selected argument from the table.
      */
     protected void deleteRows() {
         // If a table cell is being edited, we must cancel the editing
         // before deleting the row.
         GuiUtils.cancelEditing(authTable);
 
         int[] rowsSelected = authTable.getSelectedRows();
         int anchorSelection = authTable.getSelectionModel().getAnchorSelectionIndex();
         authTable.clearSelection();
         if (rowsSelected.length > 0) {
             for (int i = rowsSelected.length - 1; i >= 0; i--) {
                 tableModel.removeRow(rowsSelected[i]);
             }
             tableModel.fireTableDataChanged();
 
             // Table still contains one or more rows, so highlight (select)
             // the appropriate one.
             if (tableModel.getRowCount() > 0) {
                 if (anchorSelection >= tableModel.getRowCount()) {
                     anchorSelection = tableModel.getRowCount() - 1;
                 }
                 authTable.setRowSelectionInterval(anchorSelection, anchorSelection);
             }
 
             checkButtonsStatus();
         }
     }
 
 
     private void checkButtonsStatus() {
         // Disable DELETE if there are no rows in the table to delete.
         if (tableModel.getRowCount() == 0) {
             deleteButton.setEnabled(false);
             saveButton.setEnabled(false);
         } else {
             deleteButton.setEnabled(true);
             saveButton.setEnabled(true);
         }
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
 
         if (action.equals(DELETE_COMMAND)) {
             deleteRows();
         }
         else if (action.equals(ADD_COMMAND)) {
             // If a table cell is being edited, we should accept the current
             // value and stop the editing before adding a new row.
             GuiUtils.stopTableEditing(authTable);
 
             tableModel.addNewRow();
             tableModel.fireTableDataChanged();
 
             checkButtonsStatus();
 
             // Highlight (select) the appropriate row.
             int rowToSelect = tableModel.getRowCount() - 1;
             authTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         } else if (action.equals(LOAD_COMMAND)) {
             try {
                 final String [] _txt={".txt"}; //$NON-NLS-1$
                 final JFileChooser dialog = FileDialoger.promptToOpenFile(_txt);
                 if (dialog != null) {
                     tableModel.manager.addFile(dialog.getSelectedFile().getAbsolutePath());
                     tableModel.fireTableDataChanged();
 
                     checkButtonsStatus();
                 }
             } catch (IOException ex) {
                 log.error("Error loading auth data", ex);
             }
         } else if (action.equals(SAVE_COMMAND)) {
             try {
                 final JFileChooser chooser = FileDialoger.promptToSaveFile("auth.txt"); //$NON-NLS-1$
                 if (chooser != null) {
                     tableModel.manager.save(chooser.getSelectedFile().getAbsolutePath());
                 }
             } catch (IOException ex) {
                 JMeterUtils.reportErrorToUser(ex.getMessage(), "Error saving auth data");
             }
         }
     }
 
     public JPanel createAuthTablePanel() {
         // create the JTable that holds auth per row
         authTable = new JTable(tableModel);
         JMeterUtils.applyHiDPI(authTable);
         authTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         authTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         authTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
 
         TableColumn passwordColumn = authTable.getColumnModel().getColumn(AuthManager.COL_PASSWORD);
         passwordColumn.setCellRenderer(new PasswordCellRenderer());
         
         TableColumn mechanismColumn = authTable.getColumnModel().getColumn(AuthManager.COL_MECHANISM);
         mechanismColumn.setCellEditor(new MechanismCellEditor());
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("auths_stored"))); //$NON-NLS-1$
         panel.add(new JScrollPane(authTable));
         panel.add(createButtonPanel(), BorderLayout.SOUTH);
         return panel;
     }
 
     private JButton createButton(String resName, char mnemonic, String command, boolean enabled) {
         JButton button = new JButton(JMeterUtils.getResString(resName));
         button.setMnemonic(mnemonic);
         button.setActionCommand(command);
         button.setEnabled(enabled);
         button.addActionListener(this);
         return button;
     }
 
     private JPanel createButtonPanel() {
         boolean tableEmpty = tableModel.getRowCount() == 0;
 
         addButton = createButton("add", 'A', ADD_COMMAND, true); //$NON-NLS-1$
         deleteButton = createButton("delete", 'D', DELETE_COMMAND, !tableEmpty); //$NON-NLS-1$
         loadButton = createButton("load", 'L', LOAD_COMMAND, true); //$NON-NLS-1$
         saveButton = createButton("save", 'S', SAVE_COMMAND, !tableEmpty); //$NON-NLS-1$
 
         // Button Panel
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addButton);
         buttonPanel.add(deleteButton);
         buttonPanel.add(loadButton);
         buttonPanel.add(saveButton);
         return buttonPanel;
     }
 
     private static class InnerTableModel extends AbstractTableModel {
         private static final long serialVersionUID = 4638155137475747946L;
         final AuthManager manager;
 
         public InnerTableModel() {
             manager = new AuthManager();
         }
 
         public void clearData() {
             manager.clear();
             fireTableDataChanged();
         }
 
         public void removeRow(int row) {
             manager.remove(row);
         }
 
         public void addNewRow() {
             manager.addAuth();
         }
 
         @Override
         public boolean isCellEditable(int row, int column) {
             // all table cells are editable
             return true;
         }
 
         @Override
         public Class<?> getColumnClass(int column) {
             return getValueAt(0, column).getClass();
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public int getRowCount() {
             return manager.getAuthObjects().size();
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public int getColumnCount() {
             return manager.getColumnCount();
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public String getColumnName(int column) {
             return manager.getColumnName(column);
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public Object getValueAt(int row, int column) {
             Authorization auth = manager.getAuthObjectAt(row);
 
             switch (column){
                 case AuthManager.COL_URL:
                     return auth.getURL();
                 case AuthManager.COL_USERNAME:
                     return auth.getUser();
                 case AuthManager.COL_PASSWORD:
                     return auth.getPass();
                 case AuthManager.COL_DOMAIN:
                     return auth.getDomain();
                 case AuthManager.COL_REALM:
                     return auth.getRealm();
                 case AuthManager.COL_MECHANISM:
                     return auth.getMechanism();
                 default:
                     return null;
             }
         }
 
         @Override
         public void setValueAt(Object value, int row, int column) {
             Authorization auth = manager.getAuthObjectAt(row);
             log.debug("Setting auth value: " + value);
             switch (column){
                 case AuthManager.COL_URL:
                     auth.setURL((String) value);
                     break;
                 case AuthManager.COL_USERNAME:
                     auth.setUser((String) value);
                     break;
                 case AuthManager.COL_PASSWORD:
                     auth.setPass((String) value);
                     break;
                 case AuthManager.COL_DOMAIN:
                     auth.setDomain((String) value);
                     break;
                 case AuthManager.COL_REALM:
                     auth.setRealm((String) value);
                     break;
                 case AuthManager.COL_MECHANISM:
                     auth.setMechanism((Mechanism) value);
                     break;
                 default:
                     break;
             }
         }
     }
     
     private static class MechanismCellEditor extends DefaultCellEditor {
 
         private static final long serialVersionUID = 6085773573067229265L;
         
         public MechanismCellEditor() {
             super(new JComboBox<>(Mechanism.values()));
         }
     }
 
     private static class PasswordCellRenderer extends JPasswordField implements TableCellRenderer {
         private static final long serialVersionUID = 5169856333827579927L;
         private Border myBorder;
 
         public PasswordCellRenderer() {
             super();
             myBorder = new EmptyBorder(1, 2, 1, 2);
             setOpaque(true);
             setBorder(myBorder);
         }
 
         @Override
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                 boolean hasFocus, int row, int column) {
             setText((String) value);
 
             setBackground(isSelected && !hasFocus ? table.getSelectionBackground() : table.getBackground());
             setForeground(isSelected && !hasFocus ? table.getSelectionForeground() : table.getForeground());
 
             setFont(table.getFont());
 
             return this;
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java b/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java
index 97b4fd7b1..caebb95c6 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/gui/CookiePanel.java
@@ -1,454 +1,454 @@
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
 
 package org.apache.jmeter.protocol.http.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.ComboBoxModel;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JFileChooser;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 
 import org.apache.commons.lang3.ClassUtils;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.PowerTableModel;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieHandler;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HC4CookieHandler;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.layout.VerticalLayout;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * This is the GUI for Cookie Manager
  *
  * Allows the user to specify if she needs cookie services, and give parameters
  * for this service.
  *
  */
 public class CookiePanel extends AbstractConfigGui implements ActionListener {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CookiePanel.class);
 
     //++ Action command names
     private static final String ADD_COMMAND = "Add"; //$NON-NLS-1$
 
     private static final String DELETE_COMMAND = "Delete"; //$NON-NLS-1$
 
     private static final String LOAD_COMMAND = "Load"; //$NON-NLS-1$
 
     private static final String SAVE_COMMAND = "Save"; //$NON-NLS-1$
 
     private static final String HANDLER_COMMAND = "Handler"; // $NON-NLS-1$
     //--
 
     /**
      * The default implementation that is used when creating a new CookieManager
      */
     private static final String DEFAULT_IMPLEMENTATION = HC4CookieHandler.class.getName();
 
     /**
      * The default policy that is used when creating a new CookieManager
      */
     private static final String DEFAULT_POLICY = HC4CookieHandler.DEFAULT_POLICY_NAME;
 
     private JTable cookieTable;
 
     private PowerTableModel tableModel;
 
     private JCheckBox clearEachIteration;
 
     private JComboBox<String> selectHandlerPanel;
 
     private HashMap<String, String> handlerMap = new HashMap<>();
 
     private static final String[] COLUMN_RESOURCE_NAMES = {
         "name",   //$NON-NLS-1$
         "value",  //$NON-NLS-1$
         "domain", //$NON-NLS-1$
         "path",   //$NON-NLS-1$
         "secure", //$NON-NLS-1$
         // removed expiration because it's just an annoyance for static cookies
     };
 
     private static final Class<?>[] columnClasses = {
         String.class,
         String.class,
         String.class,
         String.class,
         Boolean.class, };
 
     private JButton addButton;
 
     private JButton deleteButton;
 
     private JButton loadButton;
 
     private JButton saveButton;
 
     private JLabeledChoice policy;
 
     /**
      * Default constructor.
      */
     public CookiePanel() {
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "cookie_manager_title"; //$NON-NLS-1$
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
 
         if (action.equals(DELETE_COMMAND)) {
             if (tableModel.getRowCount() > 0) {
                 // If a table cell is being edited, we must cancel the editing
                 // before deleting the row.
                 GuiUtils.cancelEditing(cookieTable);
 
                 int rowSelected = cookieTable.getSelectedRow();
 
                 if (rowSelected != -1) {
                     tableModel.removeRow(rowSelected);
                     tableModel.fireTableDataChanged();
 
                     // Disable the DELETE and SAVE buttons if no rows remaining
                     // after delete.
                     if (tableModel.getRowCount() == 0) {
                         deleteButton.setEnabled(false);
                         saveButton.setEnabled(false);
                     }
 
                     // Table still contains one or more rows, so highlight
                     // (select) the appropriate one.
                     else {
                         int rowToSelect = rowSelected;
 
                         if (rowSelected >= tableModel.getRowCount()) {
                             rowToSelect = rowSelected - 1;
                         }
 
                         cookieTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                     }
                 }
             }
         } else if (action.equals(ADD_COMMAND)) {
             // If a table cell is being edited, we should accept the current
             // value and stop the editing before adding a new row.
             GuiUtils.stopTableEditing(cookieTable);
 
             tableModel.addNewRow();
             tableModel.fireTableDataChanged();
 
             // Enable the DELETE and SAVE buttons if they are currently
             // disabled.
             if (!deleteButton.isEnabled()) {
                 deleteButton.setEnabled(true);
             }
             if (!saveButton.isEnabled()) {
                 saveButton.setEnabled(true);
             }
 
             // Highlight (select) the appropriate row.
             int rowToSelect = tableModel.getRowCount() - 1;
             cookieTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         } else if (action.equals(LOAD_COMMAND)) {
             try {
                 final String [] _txt={".txt"}; //$NON-NLS-1$
                 final JFileChooser chooser = FileDialoger.promptToOpenFile(_txt);
                 if (chooser != null) {
                     CookieManager manager = new CookieManager();
                     manager.addFile(chooser.getSelectedFile().getAbsolutePath());
                     for (int i = 0; i < manager.getCookieCount() ; i++){
                         addCookieToTable(manager.get(i));
                     }
                     tableModel.fireTableDataChanged();
 
                     if (tableModel.getRowCount() > 0) {
                         deleteButton.setEnabled(true);
                         saveButton.setEnabled(true);
                     }
                 }
             } catch (IOException ex) {
                 log.error("", ex);
             }
         } else if (action.equals(SAVE_COMMAND)) {
             try {
                 final JFileChooser chooser = FileDialoger.promptToSaveFile("cookies.txt"); //$NON-NLS-1$
                 if (chooser != null) {
                     ((CookieManager) createTestElement()).save(chooser.getSelectedFile().getAbsolutePath());
                 }
             } catch (IOException ex) {
                 JMeterUtils.reportErrorToUser(ex.getMessage(), "Error saving cookies");
             }
         } else if (action.equals(HANDLER_COMMAND)) {
             String cookieHandlerClass = handlerMap.get(selectHandlerPanel.getSelectedItem());
             CookieHandler handlerImpl = getCookieHandler(cookieHandlerClass);
             policy.setValues(handlerImpl.getPolicies());
             policy.setText(handlerImpl.getDefaultPolicy());
          }
     }
 
     /**
      * @param cookieHandlerClass CookieHandler class name
      * @return {@link CookieHandler}
      */
     private static CookieHandler getCookieHandler(String cookieHandlerClass) {
         try {
             return (CookieHandler) 
                     ClassUtils.getClass(cookieHandlerClass).newInstance();
         } catch (Exception e) {
             log.error("Error creating implementation:"+cookieHandlerClass+ ", will default to:"+DEFAULT_IMPLEMENTATION, e);
             return getCookieHandler(DEFAULT_IMPLEMENTATION);
         }
     }
 
     /**
      * @param className CookieHandler class name
      * @return cookie policies
      */
     private static String[] getPolicies(String className) {
         try {
             return getCookieHandler(className).getPolicies();
         } catch (Exception e) {
             log.error("Error getting cookie policies from implementation:"+className, e);
             return getPolicies(DEFAULT_IMPLEMENTATION);
         }
     }
 
     private void addCookieToTable(Cookie cookie) {
         tableModel.addRow(new Object[] { cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(),
                 Boolean.valueOf(cookie.getSecure()) });
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement cm) {
         GuiUtils.stopTableEditing(cookieTable);
         cm.clear();
         configureTestElement(cm);
         if (cm instanceof CookieManager) {
             CookieManager cookieManager = (CookieManager) cm;
             for (int i = 0; i < tableModel.getRowCount(); i++) {
                 Cookie cookie = createCookie(tableModel.getRowData(i));
                 cookieManager.add(cookie);
             }
             cookieManager.setClearEachIteration(clearEachIteration.isSelected());
             cookieManager.setCookiePolicy(policy.getText());
             cookieManager.setImplementation(handlerMap.get(selectHandlerPanel.getSelectedItem()));
         }
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         tableModel.clearData();
         clearEachIteration.setSelected(false);
         selectHandlerPanel.setSelectedItem(DEFAULT_IMPLEMENTATION
                 .substring(DEFAULT_IMPLEMENTATION.lastIndexOf('.') + 1));
         policy.setText(DEFAULT_POLICY);
         deleteButton.setEnabled(false);
         saveButton.setEnabled(false);
     }
 
     private Cookie createCookie(Object[] rowData) {
         Cookie cookie = new Cookie(
                 (String) rowData[0],
                 (String) rowData[1],
                 (String) rowData[2],
                 (String) rowData[3],
                 ((Boolean) rowData[4]).booleanValue(),
                 0); // Non-expiring
         return cookie;
     }
 
     private void populateTable(CookieManager manager) {
         tableModel.clearData();
         for (JMeterProperty jMeterProperty : manager.getCookies()) {
             addCookieToTable((Cookie) jMeterProperty.getObjectValue());
         }
     }
 
     @Override
     public TestElement createTestElement() {
         CookieManager cookieManager = new CookieManager();
         modifyTestElement(cookieManager);
         return cookieManager;
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
 
         CookieManager cookieManager = (CookieManager) el;
         populateTable(cookieManager);
         clearEachIteration.setSelected((cookieManager).getClearEachIteration());
         String fullImpl = cookieManager.getImplementation();
         selectHandlerPanel.setSelectedItem(fullImpl.substring(fullImpl.lastIndexOf('.') + 1));
         // must set policy after setting handler (which may change the policy)
         policy.setText(cookieManager.getPolicy());
     }
 
     /**
      * Shows the main cookie configuration panel.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         tableModel = new PowerTableModel(COLUMN_RESOURCE_NAMES, columnClasses);
         clearEachIteration = 
             new JCheckBox(JMeterUtils.getResString("clear_cookies_per_iter"), false); //$NON-NLS-1$
         policy = new JLabeledChoice(
                 JMeterUtils.getResString("cookie_manager_policy"), //$NON-NLS-1$
                 getPolicies(DEFAULT_IMPLEMENTATION));
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         northPanel.add(makeTitlePanel());
         JPanel optionsPane = new JPanel();
         optionsPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("cookie_options"))); // $NON-NLS-1$
         optionsPane.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         optionsPane.add(clearEachIteration);
         JPanel policyTypePane = new JPanel();
         policyTypePane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         policyTypePane.add(GuiUtils.createLabelCombo(
                 JMeterUtils.getResString("cookie_implementation_choose"), createComboHandler())); // $NON-NLS-1$
         policyTypePane.add(policy);
         optionsPane.add(policyTypePane);
         northPanel.add(optionsPane);
         add(northPanel, BorderLayout.NORTH);
         add(createCookieTablePanel(), BorderLayout.CENTER);
     }
 
     public JPanel createCookieTablePanel() {
         // create the JTable that holds one cookie per row
         cookieTable = new JTable(tableModel);
         JMeterUtils.applyHiDPI(cookieTable);
         cookieTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         cookieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         cookieTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
 
         JPanel buttonPanel = createButtonPanel();
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("cookies_stored"))); //$NON-NLS-1$
 
         panel.add(new JScrollPane(cookieTable), BorderLayout.CENTER);
         panel.add(buttonPanel, BorderLayout.SOUTH);
         return panel;
     }
 
     private JButton createButton(String resName, char mnemonic, String command, boolean enabled) {
         JButton button = new JButton(JMeterUtils.getResString(resName));
         button.setMnemonic(mnemonic);
         button.setActionCommand(command);
         button.setEnabled(enabled);
         button.addActionListener(this);
         return button;
     }
 
     private JPanel createButtonPanel() {
         boolean tableEmpty = tableModel.getRowCount() == 0;
 
         addButton = createButton("add", 'A', ADD_COMMAND, true); //$NON-NLS-1$
         deleteButton = createButton("delete", 'D', DELETE_COMMAND, !tableEmpty); //$NON-NLS-1$
         loadButton = createButton("load", 'L', LOAD_COMMAND, true); //$NON-NLS-1$
         saveButton = createButton("save", 'S', SAVE_COMMAND, !tableEmpty); //$NON-NLS-1$
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addButton);
         buttonPanel.add(deleteButton);
         buttonPanel.add(loadButton);
         buttonPanel.add(saveButton);
         return buttonPanel;
     }
     
     /**
      * Create the drop-down list to changer render
      * @return List of all render (implement ResultsRender)
      */
     private JComboBox<String> createComboHandler() {
         ComboBoxModel<String> nodesModel = new DefaultComboBoxModel<>();
         // drop-down list for renderer
         selectHandlerPanel = new JComboBox<>(nodesModel);
         selectHandlerPanel.setActionCommand(HANDLER_COMMAND);
         selectHandlerPanel.addActionListener(this);
 
         // if no results render in jmeter.properties, load Standard (default)
         List<String> classesToAdd = Collections.emptyList();
         try {
             classesToAdd = JMeterUtils.findClassesThatExtend(CookieHandler.class);
         } catch (IOException e1) {
             // ignored
         }
         String tmpName = null;
         for (String clazz : classesToAdd) {
             String shortClazz = clazz.substring(clazz.lastIndexOf('.') + 1);
             if (DEFAULT_IMPLEMENTATION.equals(clazz)) {
                 tmpName = shortClazz;
             }
             handlerMap.put(shortClazz, clazz);
         }
         // Only add to selectHandlerPanel after handlerMap has been initialized
         for (String shortClazz : handlerMap.keySet()) {
             selectHandlerPanel.addItem(shortClazz);
         }
         nodesModel.setSelectedItem(tmpName); // preset to default impl
         return selectHandlerPanel;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/gui/HeaderPanel.java b/src/protocol/http/org/apache/jmeter/protocol/http/gui/HeaderPanel.java
index 27c1c082d..4e6d215ef 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/gui/HeaderPanel.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/gui/HeaderPanel.java
@@ -1,397 +1,397 @@
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
 
 package org.apache.jmeter.protocol.http.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 
 import javax.swing.BorderFactory;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.table.AbstractTableModel;
 
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * Allows the user to specify if she needs HTTP header services, and give
  * parameters for this service.
  *
  */
 public class HeaderPanel extends AbstractConfigGui implements ActionListener
 {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HeaderPanel.class);
 
     private static final String ADD_COMMAND = "Add"; // $NON-NLS-1$
 
     private static final String DELETE_COMMAND = "Delete"; // $NON-NLS-1$
 
     private static final String LOAD_COMMAND = "Load"; // $NON-NLS-1$
 
     private static final String SAVE_COMMAND = "Save"; // $NON-NLS-1$
 
     /** Command for adding rows from the clipboard */
     private static final String ADD_FROM_CLIPBOARD = "addFromClipboard"; // $NON-NLS-1$
 
     private final InnerTableModel tableModel;
 
     private final HeaderManager headerManager;
 
     private JTable headerTable;
 
     private JButton deleteButton;
 
     private JButton saveButton;
 
     public HeaderPanel() {
         headerManager = new HeaderManager();
         tableModel = new InnerTableModel(headerManager);
         init();
     }
 
     @Override
     public TestElement createTestElement() {
         configureTestElement(headerManager);
         return (TestElement) headerManager.clone();
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement el) {
         GuiUtils.stopTableEditing(headerTable);
         el.clear();
         el.addTestElement(headerManager);
         configureTestElement(el);
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
 
         tableModel.clearData();
         deleteButton.setEnabled(false);
         saveButton.setEnabled(false);
     }
 
     @Override
     public void configure(TestElement el) {
         headerManager.clear();
         super.configure(el);
         headerManager.addTestElement(el);
         checkButtonsStatus();
     }
 
     @Override
     public String getLabelResource() {
         return "header_manager_title"; // $NON-NLS-1$
     }
 
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout());
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
         add(createHeaderTablePanel(), BorderLayout.CENTER);
     }
 
     private void checkButtonsStatus() {
         if (tableModel.getRowCount() == 0) {
             deleteButton.setEnabled(false);
             saveButton.setEnabled(false);
         } else {
             deleteButton.setEnabled(true);
             saveButton.setEnabled(true);
         }
     }
 
     /**
      * Remove the currently selected rows from the table.
      */
     protected void deleteRows() {
         // If a table cell is being edited, we must cancel the editing
         // before deleting the row.
         GuiUtils.cancelEditing(headerTable);
 
         int[] rowsSelected = headerTable.getSelectedRows();
         int anchorSelection = headerTable.getSelectionModel().getAnchorSelectionIndex();
         headerTable.clearSelection();
         if (rowsSelected.length > 0) {
             for (int i = rowsSelected.length - 1; i >= 0; i--) {
                 tableModel.removeRow(rowsSelected[i]);
             }
             tableModel.fireTableDataChanged();
 
             // Table still contains one or more rows, so highlight (select)
             // the appropriate one.
             if (tableModel.getRowCount() > 0) {
                 if (anchorSelection >= tableModel.getRowCount()) {
                     anchorSelection = tableModel.getRowCount() - 1;
                 }
                 headerTable.setRowSelectionInterval(anchorSelection, anchorSelection);
             }
 
             checkButtonsStatus();
         }
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
 
         if (action.equals(DELETE_COMMAND)) {
             deleteRows();
         } else if (action.equals(ADD_COMMAND)) {
             // If a table cell is being edited, we should accept the current
             // value and stop the editing before adding a new row.
             GuiUtils.stopTableEditing(headerTable);
 
             tableModel.addNewRow();
             tableModel.fireTableDataChanged();
 
             // Enable the DELETE and SAVE buttons if they are currently disabled.
             checkButtonsStatus();
 
             // Highlight (select) the appropriate row.
             int rowToSelect = tableModel.getRowCount() - 1;
             headerTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         } else if (action.equals(LOAD_COMMAND)) {
             try {
                 final JFileChooser chooser = FileDialoger.promptToOpenFile();
                 if (chooser != null) {
                     headerManager.addFile(chooser.getSelectedFile().getAbsolutePath());
                     tableModel.fireTableDataChanged();
 
                     checkButtonsStatus();
                 }
             } catch (IOException ex) {
                 log.error("Could not load headers", ex);
             }
         } else if (action.equals(SAVE_COMMAND)) {
             try {
                 final JFileChooser chooser = FileDialoger.promptToSaveFile(null);
                 if (chooser != null) {
                     headerManager.save(chooser.getSelectedFile().getAbsolutePath());
                 }
             } catch (IOException ex) {
                 JMeterUtils.reportErrorToUser(ex.getMessage(), "Error saving headers");
             }
         } else if (action.equals(ADD_FROM_CLIPBOARD)) {
             addFromClipboard();
         }
     }
 
     /**
      * Add values from the clipboard.
      * The clipboard is first split into lines, and the lines are then split on ':' or '\t'
      * to produce the header name and value.
      * Lines without a ':' are tested with '\t' and ignored if not found.
      */
     protected void addFromClipboard() {
         GuiUtils.stopTableEditing(this.headerTable);
         int rowCount = headerTable.getRowCount();
         try {
             String clipboardContent = GuiUtils.getPastedText();
             if(clipboardContent == null) {
                 return;
             }
             String[] clipboardLines = clipboardContent.split("\n"); // $NON-NLS-1$
             for (String clipboardLine : clipboardLines) {
                 int index = clipboardLine.indexOf(":"); // $NON-NLS-1$
                 if(index < 0) {
                     // when pasting from another header panel the values are separated with '\t'
                     index = clipboardLine.indexOf("\t");
                 }
                 if (index > 0) {
                     Header header = new Header(clipboardLine.substring(0, index), clipboardLine.substring(index+1));
                     headerManager.add(header);
                 }
             }
             tableModel.fireTableDataChanged();
             if (headerTable.getRowCount() > rowCount) {
                 // Highlight (select) the appropriate rows.
                 int rowToSelect = tableModel.getRowCount() - 1;
                 headerTable.setRowSelectionInterval(rowCount, rowToSelect);
             }
 
             checkButtonsStatus();
         } catch (IOException ioe) {
             JOptionPane.showMessageDialog(this,
                     "Could not add read headers from clipboard:\n" + ioe.getLocalizedMessage(), "Error",
                     JOptionPane.ERROR_MESSAGE);
         } catch (UnsupportedFlavorException ufe) {
             JOptionPane.showMessageDialog(this,
                     "Could not add retrieved " + DataFlavor.stringFlavor.getHumanPresentableName()
                             + " from clipboard" + ufe.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         }
     }
 
     public JPanel createHeaderTablePanel() {
         // create the JTable that holds header per row
         headerTable = new JTable(tableModel);
         JMeterUtils.applyHiDPI(headerTable);
         headerTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         headerTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         headerTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("headers_stored"))); // $NON-NLS-1$
         panel.add(new JScrollPane(headerTable), BorderLayout.CENTER);
         panel.add(createButtonPanel(), BorderLayout.SOUTH);
         return panel;
     }
 
     private JButton createButton(String resName, char mnemonic, String command, boolean enabled) {
         JButton button = new JButton(JMeterUtils.getResString(resName));
         button.setMnemonic(mnemonic);
         button.setActionCommand(command);
         button.setEnabled(enabled);
         button.addActionListener(this);
         return button;
     }
 
     private JPanel createButtonPanel() {
         boolean tableEmpty = tableModel.getRowCount() == 0;
 
         JButton addButton = createButton("add", 'A', ADD_COMMAND, true); // $NON-NLS-1$
         deleteButton = createButton("delete", 'D', DELETE_COMMAND, !tableEmpty); // $NON-NLS-1$
         JButton loadButton = createButton("load", 'L', LOAD_COMMAND, true); // $NON-NLS-1$
         saveButton = createButton("save", 'S', SAVE_COMMAND, !tableEmpty); // $NON-NLS-1$
         JButton addFromClipboard = createButton("add_from_clipboard", 'C', ADD_FROM_CLIPBOARD, true); // $NON-NLS-1$
         
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addButton);
         buttonPanel.add(addFromClipboard);
         buttonPanel.add(deleteButton);
         buttonPanel.add(loadButton);
         buttonPanel.add(saveButton);
         return buttonPanel;
     }
 
     private static class InnerTableModel extends AbstractTableModel {
         private static final long serialVersionUID = 240L;
 
         private HeaderManager manager;
 
         public InnerTableModel(HeaderManager man) {
             manager = man;
         }
 
         public void clearData() {
             manager.clear();
             fireTableDataChanged();
         }
 
         public void removeRow(int row) {
             manager.remove(row);
         }
 
         public void addNewRow() {
             manager.add();
         }
 
         @Override
         public boolean isCellEditable(int row, int column) {
             // all table cells are editable
             return true;
         }
 
         @Override
         public Class<?> getColumnClass(int column) {
             return getValueAt(0, column).getClass();
         }
 
         @Override
         public int getRowCount() {
             return manager.getHeaders().size();
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public int getColumnCount() {
             return manager.getColumnCount();
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public String getColumnName(int column) {
             return manager.getColumnName(column);
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public Object getValueAt(int row, int column) {
             Header head = manager.getHeader(row);
             if (column == 0) {
                 return head.getName();
             } else if (column == 1) {
                 return head.getValue();
             }
             return null;
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public void setValueAt(Object value, int row, int column) {
             Header header = manager.getHeader(row);
 
             if (column == 0) {
                 header.setName((String) value);
             } else if (column == 1) {
                 header.setValue((String) value);
             }
         }
 
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/modifier/AnchorModifier.java b/src/protocol/http/org/apache/jmeter/protocol/http/modifier/AnchorModifier.java
index e2e657027..b6db1301f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/modifier/AnchorModifier.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/modifier/AnchorModifier.java
@@ -1,231 +1,231 @@
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
 
 package org.apache.jmeter.protocol.http.modifier;
 
 import java.io.Serializable;
 import java.net.MalformedURLException;
 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.concurrent.ThreadLocalRandom;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.protocol.http.parser.HtmlParsingUtils;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContext;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.w3c.dom.Document;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 
 // For Unit tests, @see TestAnchorModifier
 
 public class AnchorModifier extends AbstractTestElement implements PreProcessor, Serializable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AnchorModifier.class);
 
     private static final long serialVersionUID = 240L;
 
     public AnchorModifier() {
     }
 
     /**
      * Modifies an Entry object based on HTML response text.
      */
     @Override
     public void process() {
         JMeterContext context = getThreadContext();
         Sampler sam = context.getCurrentSampler();
         SampleResult res = context.getPreviousResult();
         HTTPSamplerBase sampler;
         HTTPSampleResult result;
         if (!(sam instanceof HTTPSamplerBase) || !(res instanceof HTTPSampleResult)) {
             log.info("Can't apply HTML Link Parser when the previous" + " sampler run is not an HTTP Request.");
             return;
         } else {
             sampler = (HTTPSamplerBase) sam;
             result = (HTTPSampleResult) res;
         }
         List<HTTPSamplerBase> potentialLinks = new ArrayList<>();
         String responseText = result.getResponseDataAsString();
         int index = responseText.indexOf('<'); // $NON-NLS-1$
         if (index == -1) {
             index = 0;
         }
         if (log.isDebugEnabled()) {
             log.debug("Check for matches against: "+sampler.toString());
         }
         Document html = (Document) HtmlParsingUtils.getDOM(responseText.substring(index));
         addAnchorUrls(html, result, sampler, potentialLinks);
         addFormUrls(html, result, sampler, potentialLinks);
         addFramesetUrls(html, result, sampler, potentialLinks);
         if (!potentialLinks.isEmpty()) {
             HTTPSamplerBase url = potentialLinks.get(ThreadLocalRandom.current().nextInt(potentialLinks.size()));
             if (log.isDebugEnabled()) {
                 log.debug("Selected: "+url.toString());
             }
             sampler.setDomain(url.getDomain());
             sampler.setPath(url.getPath());
             if (url.getMethod().equals(HTTPConstants.POST)) {
                 for (JMeterProperty jMeterProperty : sampler.getArguments()) {
                     Argument arg = (Argument) jMeterProperty.getObjectValue();
                     modifyArgument(arg, url.getArguments());
                 }
             } else {
                 sampler.setArguments(url.getArguments());
                 // config.parseArguments(url.getQueryString());
             }
             sampler.setProtocol(url.getProtocol());
         } else {
             log.debug("No matches found");
         }
     }
 
     private void modifyArgument(Argument arg, Arguments args) {
         if (log.isDebugEnabled()) {
             log.debug("Modifying argument: " + arg);
         }
         List<Argument> possibleReplacements = new ArrayList<>();
         PropertyIterator iter = args.iterator();
         Argument replacementArg;
         while (iter.hasNext()) {
             replacementArg = (Argument) iter.next().getObjectValue();
             try {
                 if (HtmlParsingUtils.isArgumentMatched(replacementArg, arg)) {
                     possibleReplacements.add(replacementArg);
                 }
             } catch (Exception ex) {
                 log.error("Problem adding Argument", ex);
             }
         }
 
         if (!possibleReplacements.isEmpty()) {
             replacementArg = possibleReplacements.get(ThreadLocalRandom.current().nextInt(possibleReplacements.size()));
             arg.setName(replacementArg.getName());
             arg.setValue(replacementArg.getValue());
             if (log.isDebugEnabled()) {
                 log.debug("Just set argument to values: " + arg.getName() + " = " + arg.getValue());
             }
             args.removeArgument(replacementArg);
         }
     }
 
     public void addConfigElement(ConfigElement config) {
     }
 
     private void addFormUrls(Document html, HTTPSampleResult result, HTTPSamplerBase config, 
             List<HTTPSamplerBase> potentialLinks) {
         NodeList rootList = html.getChildNodes();
         List<HTTPSamplerBase> urls = new LinkedList<>();
         for (int x = 0; x < rootList.getLength(); x++) {
             urls.addAll(HtmlParsingUtils.createURLFromForm(rootList.item(x), result.getURL()));
         }
         for (HTTPSamplerBase newUrl : urls) {
             newUrl.setMethod(HTTPConstants.POST);
             if (log.isDebugEnabled()) {
                 log.debug("Potential Form match: " + newUrl.toString());
             }
             if (HtmlParsingUtils.isAnchorMatched(newUrl, config)) {
                 log.debug("Matched!");
                 potentialLinks.add(newUrl);
             }
         }
     }
 
     private void addAnchorUrls(Document html, HTTPSampleResult result, HTTPSamplerBase config, 
             List<HTTPSamplerBase> potentialLinks) {
         String base = "";
         NodeList baseList = html.getElementsByTagName("base"); // $NON-NLS-1$
         if (baseList.getLength() > 0) {
             base = baseList.item(0).getAttributes().getNamedItem("href").getNodeValue(); // $NON-NLS-1$
         }
         NodeList nodeList = html.getElementsByTagName("a"); // $NON-NLS-1$
         for (int i = 0; i < nodeList.getLength(); i++) {
             Node tempNode = nodeList.item(i);
             NamedNodeMap nnm = tempNode.getAttributes();
             Node namedItem = nnm.getNamedItem("href"); // $NON-NLS-1$
             if (namedItem == null) {
                 continue;
             }
             String hrefStr = namedItem.getNodeValue();
             if (hrefStr.startsWith("javascript:")) { // $NON-NLS-1$
                 continue; // No point trying these
             }
             try {
                 HTTPSamplerBase newUrl = HtmlParsingUtils.createUrlFromAnchor(hrefStr, ConversionUtils.makeRelativeURL(result.getURL(), base));
                 newUrl.setMethod(HTTPConstants.GET);
                 if (log.isDebugEnabled()) {
                     log.debug("Potential <a href> match: " + newUrl);
                 }
                 if (HtmlParsingUtils.isAnchorMatched(newUrl, config)) {
                     log.debug("Matched!");
                     potentialLinks.add(newUrl);
                 }
             } catch (MalformedURLException e) {
                 log.warn("Bad URL "+e);
             }
         }
     }
 
     private void addFramesetUrls(Document html, HTTPSampleResult result,
        HTTPSamplerBase config, List<HTTPSamplerBase> potentialLinks) {
        String base = "";
        NodeList baseList = html.getElementsByTagName("base"); // $NON-NLS-1$
        if (baseList.getLength() > 0) {
            base = baseList.item(0).getAttributes().getNamedItem("href") // $NON-NLS-1$
                    .getNodeValue();
        }
        NodeList nodeList = html.getElementsByTagName("frame"); // $NON-NLS-1$
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            NamedNodeMap nnm = tempNode.getAttributes();
            Node namedItem = nnm.getNamedItem("src"); // $NON-NLS-1$
            if (namedItem == null) {
                continue;
            }
            String hrefStr = namedItem.getNodeValue();
            try {
                HTTPSamplerBase newUrl = HtmlParsingUtils.createUrlFromAnchor(
                        hrefStr, ConversionUtils.makeRelativeURL(result.getURL(), base));
                newUrl.setMethod(HTTPConstants.GET);
                if (log.isDebugEnabled()) {
                    log.debug("Potential <frame src> match: " + newUrl);
                }
                if (HtmlParsingUtils.isAnchorMatched(newUrl, config)) {
                    log.debug("Matched!");
                    potentialLinks.add(newUrl);
                }
            } catch (MalformedURLException e) {
                log.warn("Bad URL "+e);
            }
        }
    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/modifier/RegExUserParameters.java b/src/protocol/http/org/apache/jmeter/protocol/http/modifier/RegExUserParameters.java
index 282701d3b..66ce0aa3f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/modifier/RegExUserParameters.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/modifier/RegExUserParameters.java
@@ -1,146 +1,146 @@
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
 
 package org.apache.jmeter.protocol.http.modifier;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.threads.JMeterVariables;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * This component allows you to specify reference name of a regular expression that extracts names and values of HTTP request parameters. 
  * Regular expression group numbers must be specified for parameter's name and also for parameter's value.
  * Replacement will only occur for parameters in the Sampler that uses this RegEx User Parameters which name matches
  */
 public class RegExUserParameters extends AbstractTestElement implements Serializable, PreProcessor {
     private static final String REGEX_GROUP_SUFFIX = "_g";
 
     private static final String MATCH_NR = "matchNr";
 
     /**
      * 
      */
-    private static final long serialVersionUID = 5486502839185386122L;
+    private static final long serialVersionUID = 5486502839185386121L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RegExUserParameters.class);
 
     public static final String REG_EX_REF_NAME = "RegExUserParameters.regex_ref_name";// $NON-NLS-1$
 
     public static final String REG_EX_PARAM_NAMES_GR_NR = "RegExUserParameters.param_names_gr_nr";// $NON-NLS-1$
 
     public static final String REG_EX_PARAM_VALUES_GR_NR = "RegExUserParameters.param_values_gr_nr";// $NON-NLS-1$
 
     @Override
     public void process() {
         if (log.isDebugEnabled()) {
             log.debug(Thread.currentThread().getName() + " Running up named: " + getName());//$NON-NLS-1$
         }
         Sampler entry = getThreadContext().getCurrentSampler();
         if (!(entry instanceof HTTPSamplerBase)) {
             return;
         }
 
         Map<String, String> paramMap = buildParamsMap();
         if(paramMap == null || paramMap.isEmpty()){
             log.info("RegExUserParameters element:"+getName()+" => Referenced RegExp was not found, no parameter will be changed");
             return;
         }
 
         HTTPSamplerBase sampler = (HTTPSamplerBase) entry;
         for (JMeterProperty jMeterProperty : sampler.getArguments()) {
             Argument arg = (Argument) jMeterProperty.getObjectValue();
             String oldValue = arg.getValue();
             // if parameter name exists in http request
             // then change its value with value obtained with regular expression
             String val = paramMap.get(arg.getName());
             if (val != null) {
                 arg.setValue(val);
             }
             if (log.isDebugEnabled()) {
                 log.debug("RegExUserParameters element:" + getName() + " => changed parameter: " + arg.getName() + " = " + arg.getValue() + ", was:" + oldValue);
             }
         }
     }
 
     private Map<String, String> buildParamsMap(){
         String regExRefName = getRegExRefName()+"_";
         String grNames = getRegParamNamesGrNr();
         String grValues = getRegExParamValuesGrNr();
         JMeterVariables jmvars = getThreadContext().getVariables();
         // verify if regex groups exists
         if(jmvars.get(regExRefName + MATCH_NR) == null
                 || jmvars.get(regExRefName + 1 + REGEX_GROUP_SUFFIX + grNames) == null 
                 || jmvars.get(regExRefName + 1 + REGEX_GROUP_SUFFIX + grValues) == null){
             return null;
         }
         int n = Integer.parseInt(jmvars.get(regExRefName + MATCH_NR));
         Map<String, String> map = new HashMap<>(n);
         for(int i=1; i<=n; i++){
             map.put(jmvars.get(regExRefName + i + REGEX_GROUP_SUFFIX + grNames), 
                     jmvars.get(regExRefName + i + REGEX_GROUP_SUFFIX + grValues));
         }
         return map;
     }
 
     /**
      * A new instance is created for each thread group, and the
      * clone() method is then called to create copies for each thread in a
      * thread group.
      * 
      * @see java.lang.Object#clone()
      */
     @Override
     public Object clone() {
         RegExUserParameters up = (RegExUserParameters) super.clone();
         return up;
     }
     
     public void setRegExRefName(String str) {
         setProperty(REG_EX_REF_NAME, str);
     }
 
     public String getRegExRefName() {
         return getPropertyAsString(REG_EX_REF_NAME);
     }
 
     public void setRegExParamNamesGrNr(String str) {
         setProperty(REG_EX_PARAM_NAMES_GR_NR, str);
     }
 
     public String getRegParamNamesGrNr() {
         return getPropertyAsString(REG_EX_PARAM_NAMES_GR_NR);
     }
 
     public void setRegExParamValuesGrNr(String str) {
         setProperty(REG_EX_PARAM_VALUES_GR_NR, str);
     }
 
     public String getRegExParamValuesGrNr() {
         return getPropertyAsString(REG_EX_PARAM_VALUES_GR_NR);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/BaseParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/BaseParser.java
index bb93879a9..f9c715620 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/BaseParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/BaseParser.java
@@ -1,100 +1,100 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * BaseParser is the base class for {@link LinkExtractorParser}
  * It is advised to make subclasses reusable accross parsing, so {@link BaseParser}{@link #isReusable()} returns true by default
  * @since 3.0
  */
 public abstract class BaseParser implements LinkExtractorParser {
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger LOG = LoggerFactory.getLogger(BaseParser.class);
     // Cache of parsers - parsers must be re-usable
     private static final ConcurrentMap<String, LinkExtractorParser> PARSERS = new ConcurrentHashMap<>(5);
 
     /**
      * Constructor for BaseParser
      */
     public BaseParser() {
     }
 
     /**
      * Factory method of parsers. Instances might get cached, when
      * {@link LinkExtractorParser#isReusable()} on the newly created instance
      * equals {@code true}.
      * 
      * @param parserClassName
      *            name of the class that should be used to create new parsers
      * @return a possibly cached instance of the wanted
      *         {@link LinkExtractorParser}
      * @throws LinkExtractorParseException
      *             when a new instance could not be instantiated
      */
     public static LinkExtractorParser getParser(String parserClassName) 
             throws LinkExtractorParseException {
 
         // Is there a cached parser?
         LinkExtractorParser parser = PARSERS.get(parserClassName);
         if (parser != null) {
             LOG.debug("Fetched " + parserClassName);
             return parser;
         }
 
         try {
             Object clazz = Class.forName(parserClassName).newInstance();
             if (clazz instanceof LinkExtractorParser) {
                 parser = (LinkExtractorParser) clazz;
             } else {
                 throw new LinkExtractorParseException(new ClassCastException(parserClassName));
             }
         } catch (InstantiationException | ClassNotFoundException
                 | IllegalAccessException e) {
             throw new LinkExtractorParseException(e);
         }
         LOG.info("Created " + parserClassName);
         if (parser.isReusable()) {
             LinkExtractorParser currentParser = PARSERS.putIfAbsent(
                     parserClassName, parser);// cache the parser if not alread
                                              // done by another thread
             if (currentParser != null) {
                 return currentParser;
             }
         }
 
         return parser;
     }
     
     /**
      * Parsers should over-ride this method if the parser class is re-usable, in
      * which case the class will be cached for the next getParser() call.
      *
      * @return {@code true} if the Parser is reusable
      */
     @Override
     public boolean isReusable() {
         return true;
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/CSSParseExceptionCallback.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/CSSParseExceptionCallback.java
index 889a57755..06c7debd0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/CSSParseExceptionCallback.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/CSSParseExceptionCallback.java
@@ -1,58 +1,58 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.URL;
 
 import org.apache.commons.lang3.Validate;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 import com.helger.css.handler.ICSSParseExceptionCallback;
 import com.helger.css.parser.ParseException;
 import com.helger.css.reader.errorhandler.LoggingCSSParseErrorHandler;
 
 public class CSSParseExceptionCallback implements ICSSParseExceptionCallback {
 
     private static final long serialVersionUID = -4277276398858139449L;
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger LOG = LoggerFactory.getLogger(CSSParseExceptionCallback.class);
     private static final boolean IGNORE_UNRECOVERABLE_PARSING_ERROR = JMeterUtils
             .getPropDefault(
                     "httpsampler.ignore_failed_embedded_resource", false); //$NON-NLS-1$
 
     private final URL baseUrl;
 
     public CSSParseExceptionCallback(URL baseUrl) {
         this.baseUrl = Validate.notNull(baseUrl);
     }
 
     @Override
     public void onException(ParseException ex) {
         final String message = "Failed to parse CSS: " + baseUrl + ", "
                 + LoggingCSSParseErrorHandler.createLoggingStringParseError(ex);
         if (IGNORE_UNRECOVERABLE_PARSING_ERROR) {
             LOG.warn(message);
         } else {
             throw new IllegalArgumentException(message);
         }
 
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParser.java
index ceaeb28bb..34886fbaf 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParser.java
@@ -1,96 +1,96 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.URL;
 import java.nio.charset.Charset;
 import java.util.Collections;
 import java.util.Iterator;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.commons.lang3.Validate;
 import org.apache.commons.lang3.tuple.ImmutableTriple;
 import org.apache.commons.lang3.tuple.Triple;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 import com.github.benmanes.caffeine.cache.Caffeine;
 import com.github.benmanes.caffeine.cache.LoadingCache;
 
 /**
  * CSS Parser used to extract from CSS files external urls
  *
  * @since 3.0
  */
 public class CssParser implements LinkExtractorParser {
     private static final URLCollection EMPTY_URL_COLLECTION = new URLCollection(Collections.emptyList());
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger LOG = LoggerFactory.getLogger(CssParser.class);
 
     /**
      *
      */
     private static final LoadingCache<Triple<String, URL, Charset>, URLCollection> CSS_URL_CACHE;
     static {
         final int cacheSize = JMeterUtils.getPropDefault(
                 "css.parser.cache.size", 400);
         CSS_URL_CACHE = Caffeine.newBuilder().maximumSize(cacheSize)
                 .build(new CssParserCacheLoader());
     }
 
     /**
      *
      * @see org.apache.jmeter.protocol.http.parser.LinkExtractorParser#getEmbeddedResourceURLs
      *      (java.lang.String, byte[], java.net.URL, java.lang.String)
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] data,
             final URL baseUrl, String encoding)
             throws LinkExtractorParseException {
         try {
             final String cssContent = new String(data, encoding);
             final Charset charset = Charset.forName(encoding);
             final Triple<String, URL, Charset> triple = ImmutableTriple.of(
                     cssContent, baseUrl, charset);
             final URLCollection urlCollection = orDefault(CSS_URL_CACHE.get(triple), EMPTY_URL_COLLECTION);
 
             if (LOG.isDebugEnabled()) {
                 LOG.debug("Parsed:" + baseUrl + ", got:" + StringUtils.join(urlCollection, ","));
             }
 
             return urlCollection.iterator();
         } catch (Exception e) {
             throw new LinkExtractorParseException(e);
         }
     }
 
     private URLCollection orDefault(URLCollection urlCollection,
             URLCollection defaultValue) {
         if (urlCollection == null) {
             return Validate.notNull(defaultValue);
         }
         return urlCollection;
     }
 
     @Override
     public boolean isReusable() {
         return true;
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParserCacheLoader.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParserCacheLoader.java
index 7da595b4d..e1e937563 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParserCacheLoader.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/CssParserCacheLoader.java
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.URL;
 import java.nio.charset.Charset;
 import java.util.ArrayList;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.commons.lang3.tuple.Triple;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 import com.github.benmanes.caffeine.cache.CacheLoader;
 import com.helger.css.ECSSVersion;
 import com.helger.css.decl.CSSDeclaration;
 import com.helger.css.decl.CSSExpressionMemberTermURI;
 import com.helger.css.decl.CSSImportRule;
 import com.helger.css.decl.CascadingStyleSheet;
 import com.helger.css.decl.ICSSTopLevelRule;
 import com.helger.css.decl.visit.CSSVisitor;
 import com.helger.css.decl.visit.DefaultCSSUrlVisitor;
 import com.helger.css.reader.CSSReader;
 import com.helger.css.reader.CSSReaderSettings;
 import com.helger.css.reader.errorhandler.DoNothingCSSInterpretErrorHandler;
 import com.helger.css.reader.errorhandler.LoggingCSSParseErrorHandler;
 
 public class CssParserCacheLoader implements
         CacheLoader<Triple<String, URL, Charset>, URLCollection> {
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger LOG = LoggerFactory.getLogger(CssParserCacheLoader.class);
     private static final boolean IGNORE_ALL_CSS_ERRORS = JMeterUtils
             .getPropDefault("css.parser.ignore_all_css_errors", true);
 
     @Override
     public URLCollection load(Triple<String, URL, Charset> triple)
             throws Exception {
         final String cssContent = triple.getLeft();
         final URL baseUrl = triple.getMiddle();
         final Charset charset = triple.getRight();
         final CSSReaderSettings readerSettings = new CSSReaderSettings()
                 .setBrowserCompliantMode(true)
                 .setFallbackCharset(charset)
                 .setCSSVersion(ECSSVersion.CSS30)
                 .setCustomErrorHandler(new LoggingCSSParseErrorHandler())
                 .setCustomExceptionHandler(
                         new CSSParseExceptionCallback(baseUrl));
         if (IGNORE_ALL_CSS_ERRORS) {
             readerSettings
                     .setInterpretErrorHandler(new DoNothingCSSInterpretErrorHandler());
         }
         final CascadingStyleSheet aCSS = CSSReader.readFromStringReader(
                 cssContent, readerSettings);
 
         final URLCollection urls = new URLCollection(new ArrayList<>());
 
         if (aCSS == null) {
             LOG.warn("Failed parsing CSS: " + baseUrl
                     + ", got null CascadingStyleSheet");
             return urls;
         }
 
         CSSVisitor.visitCSSUrl(aCSS, new DefaultCSSUrlVisitor() {
             @Override
             public void onImport(CSSImportRule rule) {
                 final String location = rule.getLocationString();
                 if (!StringUtils.isEmpty(location)) {
                     urls.addURL(location, baseUrl);
                 }
             }
 
             // Call for URLs outside of URLs
             @Override
             public void onUrlDeclaration(final ICSSTopLevelRule aTopLevelRule,
                     final CSSDeclaration aDeclaration,
                     final CSSExpressionMemberTermURI aURITerm) {
                 // NOOP
                 // Browser fetch such urls only when CSS rule matches
                 // so we disable this code
             }
         });
 
         return urls;
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index a8a651329..86e934f23 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,220 +1,220 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.commons.lang3.StringUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * {@link HTMLParser} subclasses can parse HTML content to obtain URLs.
  *
  */
 public abstract class HTMLParser extends BaseParser {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HTMLParser.class);
 
     protected static final String ATT_BACKGROUND    = "background";// $NON-NLS-1$
     protected static final String ATT_CODE          = "code";// $NON-NLS-1$
     protected static final String ATT_CODEBASE      = "codebase";// $NON-NLS-1$
     protected static final String ATT_DATA          = "data";// $NON-NLS-1$
     protected static final String ATT_HREF          = "href";// $NON-NLS-1$
     protected static final String ATT_REL           = "rel";// $NON-NLS-1$
     protected static final String ATT_SRC           = "src";// $NON-NLS-1$
     protected static final String ATT_STYLE         = "style";// $NON-NLS-1$
     protected static final String ATT_TYPE          = "type";// $NON-NLS-1$
     protected static final String ATT_IS_IMAGE      = "image";// $NON-NLS-1$
     protected static final String TAG_APPLET        = "applet";// $NON-NLS-1$
     protected static final String TAG_BASE          = "base";// $NON-NLS-1$
     protected static final String TAG_BGSOUND       = "bgsound";// $NON-NLS-1$
     protected static final String TAG_BODY          = "body";// $NON-NLS-1$
     protected static final String TAG_EMBED         = "embed";// $NON-NLS-1$
     protected static final String TAG_FRAME         = "frame";// $NON-NLS-1$
     protected static final String TAG_IFRAME        = "iframe";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
     protected static final String TAG_OBJECT        = "object";// $NON-NLS-1$
     protected static final String TAG_SCRIPT        = "script";// $NON-NLS-1$
     protected static final String STYLESHEET        = "stylesheet";// $NON-NLS-1$
 
     protected static final String IE_UA             = "MSIE ([0-9]+.[0-9]+)";// $NON-NLS-1$
     protected static final Pattern IE_UA_PATTERN    = Pattern.compile(IE_UA);
     private   static final float IE_10                = 10.0f;
 
     public static final String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
     public static final String DEFAULT_PARSER =
         "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser"; // $NON-NLS-1$
 
     /**
      * Protected constructor to prevent instantiation except from within
      * subclasses.
      */
     protected HTMLParser() {
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * URLs should not appear twice in the returned iterator.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
      * @param userAgent
      *            User Agent
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(
             String userAgent, byte[] html, URL baseUrl, String encoding) throws HTMLParseException {
         // The Set is used to ignore duplicated binary files.
         // Using a LinkedHashSet to avoid unnecessary overhead in iterating
         // the elements in the set later on. As a side-effect, this will keep
         // them roughly in order, which should be a better model of browser
         // behaviour.
 
         Collection<URLString> col = new LinkedHashSet<>();
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(col),encoding);
 
         // An additional note on using HashSets to store URLs: I just
         // discovered that obtaining the hashCode of a java.net.URL implies
         // a domain-name resolution process. This means significant delays
         // can occur, even more so if the domain name is not resolvable.
         // Whether this can be a problem in practical situations I can't tell,
         // but
         // thought I'd keep a note just in case...
         // BTW, note that using a List and removing duplicates via scan
         // would not help, since URL.equals requires name resolution too.
         // The above problem has now been addressed with the URLString and
         // URLCollection classes.
 
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * All URLs should be added to the Collection.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
      * <p>
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            URLCollection
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public abstract Iterator<URL> getEmbeddedResourceURLs(
             String userAgent, byte[] html, URL baseUrl, URLCollection coll, String encoding)
             throws HTMLParseException;
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            Collection - will contain URLString objects, not URLs
      * @param encoding Charset
      * @return an Iterator for the resource URLs
      * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public Iterator<URL> getEmbeddedResourceURLs(
             String userAgent, byte[] html, URL baseUrl, Collection<URLString> coll, String encoding) 
                     throws HTMLParseException {
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(coll), encoding);
     }
     
     /**
      * 
      * @param ieVersion Float IE version
      * @return true if IE version &lt; IE v10
      */
     protected final boolean isEnableConditionalComments(Float ieVersion) {
         // Conditional comment have been dropped in IE10
         // http://msdn.microsoft.com/en-us/library/ie/hh801214%28v=vs.85%29.aspx
         return ieVersion != null && ieVersion.floatValue() < IE_10;
     }
     
     /**
      * 
      * @param userAgent User Agent
      * @return version null if not IE or the version after MSIE
      */
     protected Float extractIEVersion(String userAgent) {
         if (StringUtils.isEmpty(userAgent)) {
             log.info("userAgent is null");
             return null;
         }
         Matcher matcher = IE_UA_PATTERN.matcher(userAgent);
         String ieVersion = null;
         if (matcher.find()) {
             if (matcher.groupCount() > 0) {
                 ieVersion = matcher.group(1);
             } else {
                 ieVersion = matcher.group();
             }
         }
         if (ieVersion != null) {
             return Float.valueOf(ieVersion);
         } else {
             return null;
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
index 20023eec1..bcb3f13f9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
@@ -1,401 +1,401 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.io.ByteArrayInputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.StandardCharsets;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.apache.oro.text.PatternCacheLRU;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.w3c.dom.Document;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 
 // For Junit tests @see TestHtmlParsingUtils
 
 public final class HtmlParsingUtils {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HtmlParsingUtils.class);
 
     /**
      * Private constructor to prevent instantiation.
      */
     private HtmlParsingUtils() {
     }
 
     /**
      * Check if anchor matches by checking against:
      * - protocol
      * - domain
      * - path
      * - parameter names
      *
      * @param newLink target to match
      * @param config pattern to match against
      *
      * @return true if target URL matches pattern URL
      */
     public static boolean isAnchorMatched(HTTPSamplerBase newLink, HTTPSamplerBase config)
     {
         String query = null;
         try {
             query = URLDecoder.decode(newLink.getQueryString(), StandardCharsets.UTF_8.name());
         } catch (UnsupportedEncodingException e) {
             // UTF-8 unsupported? You must be joking!
             log.error("UTF-8 encoding not supported!");
             throw new Error("Should not happen: " + e.toString(), e);
         }
 
         final Arguments arguments = config.getArguments();
 
         final Perl5Matcher matcher = JMeterUtils.getMatcher();
         final PatternCacheLRU patternCache = JMeterUtils.getPatternCache();
 
         if (!isEqualOrMatches(newLink.getProtocol(), config.getProtocol(), matcher, patternCache)){
             return false;
         }
 
         final String domain = config.getDomain();
         if (domain != null && domain.length() > 0) {
             if (!isEqualOrMatches(newLink.getDomain(), domain, matcher, patternCache)){
                 return false;
             }
         }
 
         final String path = config.getPath();
         if (!newLink.getPath().equals(path)
                 && !matcher.matches(newLink.getPath(), patternCache.getPattern("[/]*" + path, // $NON-NLS-1$
                         Perl5Compiler.READ_ONLY_MASK))) {
             return false;
         }
 
         for (JMeterProperty argument : arguments) {
             Argument item = (Argument) argument.getObjectValue();
             final String name = item.getName();
             if (!query.contains(name + "=")) { // $NON-NLS-1$
                 if (!(matcher.contains(query, patternCache.getPattern(name, Perl5Compiler.READ_ONLY_MASK)))) {
                     return false;
                 }
             }
         }
 
         return true;
     }
 
     /**
      * Arguments match if the input name matches the corresponding pattern name
      * and the input value matches the pattern value, where the matching is done
      * first using String equals, and then Regular Expression matching if the equals test fails.
      *
      * @param arg - input Argument
      * @param patternArg - pattern to match against
      * @return true if both name and value match
      */
     public static boolean isArgumentMatched(Argument arg, Argument patternArg) {
         final Perl5Matcher matcher = JMeterUtils.getMatcher();
         final PatternCacheLRU patternCache = JMeterUtils.getPatternCache();
         return
             isEqualOrMatches(arg.getName(), patternArg.getName(), matcher, patternCache)
         &&
             isEqualOrMatches(arg.getValue(), patternArg.getValue(), matcher, patternCache);
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails.
      *
      * @param arg input string
      * @param pat pattern string
      * @param matcher Perl5Matcher
      * @param cache PatternCache
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatches(String arg, String pat, Perl5Matcher matcher, PatternCacheLRU cache){
         return
             arg.equals(pat)
             ||
             matcher.matches(arg,cache.getPattern(pat,Perl5Compiler.READ_ONLY_MASK));
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails
      * using case-insenssitive matching.
      *
      * @param arg input string
      * @param pat pattern string
      * @param matcher Perl5Matcher
      * @param cache PatternCache
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatchesCaseBlind(String arg, String pat, Perl5Matcher matcher, PatternCacheLRU cache){
         return
             arg.equalsIgnoreCase(pat)
             ||
             matcher.matches(arg,cache.getPattern(pat,Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK));
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails
      * using case-insensitive matching.
      *
      * @param arg input string
      * @param pat pattern string
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatches(String arg, String pat){
         return isEqualOrMatches(arg, pat, JMeterUtils.getMatcher(), JMeterUtils.getPatternCache());
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails
      * using case-insensitive matching.
      *
      * @param arg input string
      * @param pat pattern string
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatchesCaseBlind(String arg, String pat){
         return isEqualOrMatchesCaseBlind(arg, pat, JMeterUtils.getMatcher(), JMeterUtils.getPatternCache());
     }
 
     /**
      * Returns <code>tidy</code> as HTML parser.
      *
      * @return a <code>tidy</code> HTML parser
      */
     public static Tidy getParser() {
         log.debug("Start : getParser1");
         Tidy tidy = new Tidy();
         tidy.setInputEncoding(StandardCharsets.UTF_8.name());
         tidy.setOutputEncoding(StandardCharsets.UTF_8.name());
         tidy.setQuiet(true);
         tidy.setShowWarnings(false);
 
         if (log.isDebugEnabled()) {
             log.debug("getParser1 : tidy parser created - " + tidy);
         }
 
         log.debug("End : getParser1");
 
         return tidy;
     }
 
     /**
      * Returns a node representing a whole xml given an xml document.
      *
      * @param text
      *            an xml document
      * @return a node representing a whole xml
      */
     public static Node getDOM(String text) {
         log.debug("Start : getDOM1");
 
         Node node = getParser()
                 .parseDOM(
                         new ByteArrayInputStream(
                                 text.getBytes(StandardCharsets.UTF_8)), null);
 
         if (log.isDebugEnabled()) {
             log.debug("node : " + node);
         }
 
         log.debug("End : getDOM1");
 
         return node;
 
     }
 
     public static Document createEmptyDoc() {
         return Tidy.createEmptyDocument();
     }
 
     /**
      * Create a new Sampler based on an HREF string plus a contextual URL
      * object. Given that an HREF string might be of three possible forms, some
      * processing is required.
      *
      * @param parsedUrlString
      *            the url from the href
      * @param context
      *            the context in which the href was found. This is used to
      *            extract url information that might be missing in
      *            <code>parsedUrlString</code>
      * @return sampler with filled in information about the fully parsed url
      * @throws MalformedURLException
      *             when the given url (<code>parsedUrlString</code> plus
      *             <code>context</code> is malformed)
      */
     public static HTTPSamplerBase createUrlFromAnchor(String parsedUrlString, URL context) throws MalformedURLException {
         if (log.isDebugEnabled()) {
             log.debug("Creating URL from Anchor: " + parsedUrlString + ", base: " + context);
         }
         URL url = ConversionUtils.makeRelativeURL(context, parsedUrlString);
         HTTPSamplerBase sampler =HTTPSamplerFactory.newInstance();
         sampler.setDomain(url.getHost());
         sampler.setProtocol(url.getProtocol());
         sampler.setPort(url.getPort());
         sampler.setPath(url.getPath());
         sampler.parseArguments(url.getQuery());
 
         return sampler;
     }
 
     public static List<HTTPSamplerBase> createURLFromForm(Node doc, URL context) {
         String selectName = null;
         LinkedList<HTTPSamplerBase> urlConfigs = new LinkedList<>();
         recurseForm(doc, urlConfigs, context, selectName, false);
         /*
          * NamedNodeMap atts = formNode.getAttributes();
          * if(atts.getNamedItem("action") == null) { throw new
          * MalformedURLException(); } String action =
          * atts.getNamedItem("action").getNodeValue(); UrlConfig url =
          * createUrlFromAnchor(action, context); recurseForm(doc, url,
          * selectName,true,formStart);
          */
         return urlConfigs;
     }
 
     // N.B. Since the tags are extracted from an HTML Form, any values must already have been encoded
     private static boolean recurseForm(Node tempNode, LinkedList<HTTPSamplerBase> urlConfigs, URL context, String selectName,
             boolean inForm) {
         NamedNodeMap nodeAtts = tempNode.getAttributes();
         String tag = tempNode.getNodeName();
         try {
             if (inForm) {
                 HTTPSamplerBase url = urlConfigs.getLast();
                 if (tag.equalsIgnoreCase("form")) { // $NON-NLS-1$
                     try {
                         urlConfigs.add(createFormUrlConfig(tempNode, context));
                     } catch (MalformedURLException e) {
                         inForm = false;
                     }
                 } else if (tag.equalsIgnoreCase("input")) { // $NON-NLS-1$
                     url.addEncodedArgument(getAttributeValue(nodeAtts, "name"),  // $NON-NLS-1$
                             getAttributeValue(nodeAtts, "value")); // $NON-NLS-1$
                 } else if (tag.equalsIgnoreCase("textarea")) { // $NON-NLS-1$
                     try {
                         url.addEncodedArgument(getAttributeValue(nodeAtts, "name"),  // $NON-NLS-1$
                                 tempNode.getFirstChild().getNodeValue());
                     } catch (NullPointerException e) {
                         url.addArgument(getAttributeValue(nodeAtts, "name"), ""); // $NON-NLS-1$
                     }
                 } else if (tag.equalsIgnoreCase("select")) { // $NON-NLS-1$
                     selectName = getAttributeValue(nodeAtts, "name"); // $NON-NLS-1$
                 } else if (tag.equalsIgnoreCase("option")) { // $NON-NLS-1$
                     String value = getAttributeValue(nodeAtts, "value"); // $NON-NLS-1$
                     if (value == null) {
                         try {
                             value = tempNode.getFirstChild().getNodeValue();
                         } catch (NullPointerException e) {
                             value = ""; // $NON-NLS-1$
                         }
                     }
                     url.addEncodedArgument(selectName, value);
                 }
             } else if (tag.equalsIgnoreCase("form")) { // $NON-NLS-1$
                 try {
                     urlConfigs.add(createFormUrlConfig(tempNode, context));
                     inForm = true;
                 } catch (MalformedURLException e) {
                     inForm = false;
                 }
             }
         } catch (Exception ex) {
             log.warn("Some bad HTML " + printNode(tempNode), ex);
         }
         NodeList childNodes = tempNode.getChildNodes();
         for (int x = 0; x < childNodes.getLength(); x++) {
             inForm = recurseForm(childNodes.item(x), urlConfigs, context, selectName, inForm);
         }
         return inForm;
     }
 
     private static String getAttributeValue(NamedNodeMap att, String attName) {
         try {
             return att.getNamedItem(attName).getNodeValue();
         } catch (Exception ex) {
             return ""; // $NON-NLS-1$
         }
     }
 
     private static String printNode(Node node) {
         StringBuilder buf = new StringBuilder();
         buf.append('<'); // $NON-NLS-1$
         buf.append(node.getNodeName());
         NamedNodeMap atts = node.getAttributes();
         for (int x = 0; x < atts.getLength(); x++) {
             buf.append(' '); // $NON-NLS-1$
             buf.append(atts.item(x).getNodeName());
             buf.append("=\""); // $NON-NLS-1$
             buf.append(atts.item(x).getNodeValue());
             buf.append("\""); // $NON-NLS-1$
         }
 
         buf.append('>'); // $NON-NLS-1$
 
         return buf.toString();
     }
 
     private static HTTPSamplerBase createFormUrlConfig(Node tempNode, URL context) throws MalformedURLException {
         NamedNodeMap atts = tempNode.getAttributes();
         if (atts.getNamedItem("action") == null) { // $NON-NLS-1$
             throw new MalformedURLException();
         }
         String action = atts.getNamedItem("action").getNodeValue(); // $NON-NLS-1$
         return createUrlFromAnchor(action, context);
     }
 
     public static void extractStyleURLs(final URL baseUrl, final URLCollection urls, String styleTagStr) {
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         Pattern pattern = JMeterUtils.getPatternCache().getPattern(
                 "URL\\(\\s*('|\")(.*)('|\")\\s*\\)", // $NON-NLS-1$
                 Perl5Compiler.CASE_INSENSITIVE_MASK | Perl5Compiler.SINGLELINE_MASK | Perl5Compiler.READ_ONLY_MASK);
         PatternMatcherInput input = null;
         input = new PatternMatcherInput(styleTagStr);
         while (matcher.contains(input, pattern)) {
             MatchResult match = matcher.getMatch();
             // The value is in the second group
             String styleUrl = match.group(2);
             urls.addURL(styleUrl, baseUrl);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
index c81b53f0f..d740b4f7d 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JTidyHTMLParser.java
@@ -1,244 +1,244 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.io.ByteArrayInputStream;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.nio.charset.StandardCharsets;
 import java.util.Iterator;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.w3c.dom.Document;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 import org.xml.sax.SAXException;
 
 /**
  * HtmlParser implementation using JTidy.
  *
  */
 class JTidyHTMLParser extends HTMLParser {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JTidyHTMLParser.class);
 
     protected JTidyHTMLParser() {
         super();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
         Document dom = null;
         try {
             dom = (Document) getDOM(html, encoding);
         } catch (SAXException se) {
             throw new HTMLParseException(se);
         }
 
         // Now parse the DOM tree
 
         scanNodes(dom, urls, baseUrl);
 
         return urls.iterator();
     }
 
     /**
      * Scan nodes recursively, looking for embedded resources
      *
      * @param node -
      *            initial node
      * @param urls -
      *            container for URLs
      * @param baseUrl -
      *            used to create absolute URLs
      *
      * @return new base URL
      */
     private URL scanNodes(Node node, URLCollection urls, URL baseUrl) throws HTMLParseException {
         if (node == null) {
             return baseUrl;
         }
 
         String name = node.getNodeName();
 
         int type = node.getNodeType();
 
         switch (type) {
 
         case Node.DOCUMENT_NODE:
             scanNodes(((Document) node).getDocumentElement(), urls, baseUrl);
             break;
 
         case Node.ELEMENT_NODE:
 
             NamedNodeMap attrs = node.getAttributes();
             if (name.equalsIgnoreCase(TAG_BASE)) {
                 String tmp = getValue(attrs, ATT_HREF);
                 if (tmp != null) {
                     try {
                         baseUrl = ConversionUtils.makeRelativeURL(baseUrl, tmp);
                     } catch (MalformedURLException e) {
                         throw new HTMLParseException(e);
                     }
                 }
                 break;
             }
 
             if (name.equalsIgnoreCase(TAG_IMAGE) || name.equalsIgnoreCase(TAG_EMBED)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
 
             if (name.equalsIgnoreCase(TAG_APPLET)) {
                 urls.addURL(getValue(attrs, "code"), baseUrl);
                 break;
             }
             
             if (name.equalsIgnoreCase(TAG_OBJECT)) {
                 String data = getValue(attrs, "codebase");
                 if(!StringUtils.isEmpty(data)) {
                     urls.addURL(data, baseUrl);                    
                 }
                 
                 data = getValue(attrs, "data");
                 if(!StringUtils.isEmpty(data)) {
                     urls.addURL(data, baseUrl);                    
                 }
                 break;
             }
             
             if (name.equalsIgnoreCase(TAG_INPUT)) {
                 String src = getValue(attrs, ATT_SRC);
                 String typ = getValue(attrs, ATT_TYPE);
                 if ((src != null) && (typ.equalsIgnoreCase(ATT_IS_IMAGE))) {
                     urls.addURL(src, baseUrl);
                 }
                 break;
             }
             if (name.equalsIgnoreCase(TAG_LINK) && getValue(attrs, ATT_REL).equalsIgnoreCase(STYLESHEET)) {
                 urls.addURL(getValue(attrs, ATT_HREF), baseUrl);
                 break;
             }
             if (name.equalsIgnoreCase(TAG_SCRIPT)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
             if (name.equalsIgnoreCase(TAG_FRAME)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
             if (name.equalsIgnoreCase(TAG_IFRAME)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
             String back = getValue(attrs, ATT_BACKGROUND);
             if (back != null) {
                 urls.addURL(back, baseUrl);
             }
             if (name.equalsIgnoreCase(TAG_BGSOUND)) {
                 urls.addURL(getValue(attrs, ATT_SRC), baseUrl);
                 break;
             }
 
             String style = getValue(attrs, ATT_STYLE);
             if (style != null) {
                 HtmlParsingUtils.extractStyleURLs(baseUrl, urls, style);
             }
 
             NodeList children = node.getChildNodes();
             if (children != null) {
                 int len = children.getLength();
                 for (int i = 0; i < len; i++) {
                     baseUrl = scanNodes(children.item(i), urls, baseUrl);
                 }
             }
 
             break;
 
         // case Node.TEXT_NODE:
         // break;
 
         default:
             // ignored
             break;
         }
 
         return baseUrl;
 
     }
 
     /*
      * Helper method to get an attribute value, if it exists @param attrs list
      * of attributs @param attname attribute name @return
      */
     private String getValue(NamedNodeMap attrs, String attname) {
         String v = null;
         Node n = attrs.getNamedItem(attname);
         if (n != null) {
             v = n.getNodeValue();
         }
         return v;
     }
 
     /**
      * Returns <code>tidy</code> as HTML parser.
      *
      * @return a <code>tidy</code> HTML parser
      */
     private static Tidy getTidyParser(String encoding) {
         log.debug("Start : getParser");
         Tidy tidy = new Tidy();
         tidy.setInputEncoding(encoding);
         tidy.setOutputEncoding(StandardCharsets.UTF_8.name());
         tidy.setQuiet(true);
         tidy.setShowWarnings(false);
         if (log.isDebugEnabled()) {
             log.debug("getParser : tidy parser created - " + tidy);
         }
         log.debug("End   : getParser");
         return tidy;
     }
 
     /**
      * Returns a node representing a whole xml given an xml document.
      *
      * @param text
      *            an xml document (as a byte array)
      * @return a node representing a whole xml
      *
      * @throws SAXException
      *             indicates an error parsing the xml document
      */
     private static Node getDOM(byte[] text, String encoding) throws SAXException {
         log.debug("Start : getDOM");
         Node node = getTidyParser(encoding).parseDOM(new ByteArrayInputStream(text), null);
         if (log.isDebugEnabled()) {
             log.debug("node : " + node);
         }
         log.debug("End   : getDOM");
         return node;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
index f01cc8de1..e3c21575e 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
@@ -1,153 +1,150 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Iterator;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
-//import org.apache.jorphan.logging.LoggingManager;
-//import org.apache.log.Logger;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.Node;
 import org.jsoup.select.NodeTraversor;
 import org.jsoup.select.NodeVisitor;
 
 /**
  * Parser based on JSOUP
  * @since 2.10
  * TODO Factor out common code between {@link LagartoBasedHtmlParser} and this one (adapter pattern)
  */
 public class JsoupBasedHtmlParser extends HTMLParser {
-//    private static final Logger log = LoggingManager.getLoggerForClass();
 
     /*
      * A dummy class to pass the pointer of URL.
      */
     private static class URLPointer {
         private URLPointer(URL newUrl) {
             url = newUrl;
         }
         private URL url;
     }
 
     private static final class JMeterNodeVisitor implements NodeVisitor {
 
         private URLCollection urls;
         private URLPointer baseUrl;
 
         /**
          * @param baseUrl base url to extract possibly missing information from urls found in <code>urls</code>
          * @param urls collection of urls to consider
          */
         public JMeterNodeVisitor(final URLPointer baseUrl, URLCollection urls) {
             this.urls = urls;
             this.baseUrl = baseUrl;
         }
 
         private void extractAttribute(Element tag, String attributeName) {
             String url = tag.attr(attributeName);
             if (!StringUtils.isEmpty(url)) {
                 urls.addURL(url, baseUrl.url);
             }
         }
 
         @Override
         public void head(Node node, int depth) {
             if (!(node instanceof Element)) {
                 return;
             }
             Element tag = (Element) node;
             String tagName = tag.tagName().toLowerCase();
             if (tagName.equals(TAG_BODY)) {
                 extractAttribute(tag, ATT_BACKGROUND);
             } else if (tagName.equals(TAG_SCRIPT)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_BASE)) {
                 String baseref = tag.attr(ATT_HREF);
                 try {
                     if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
                     {
                         baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref);
                     }
                 } catch (MalformedURLException e1) {
                     throw new RuntimeException(e1);
                 }
             } else if (tagName.equals(TAG_IMAGE)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_APPLET)) {
                 extractAttribute(tag, ATT_CODE);
             } else if (tagName.equals(TAG_OBJECT)) {
                 extractAttribute(tag, ATT_CODEBASE);
                 extractAttribute(tag, ATT_DATA);
             } else if (tagName.equals(TAG_INPUT)) {
                 // we check the input tag type for image
                 if (ATT_IS_IMAGE.equalsIgnoreCase(tag.attr(ATT_TYPE))) {
                     // then we need to download the binary
                     extractAttribute(tag, ATT_SRC);
                 }
                 // Bug 51750
             } else if (tagName.equals(TAG_FRAME) || tagName.equals(TAG_IFRAME)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_EMBED)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_BGSOUND)){
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_LINK)) {
                 // Putting the string first means it works even if the attribute is null
                 if (STYLESHEET.equalsIgnoreCase(tag.attr(ATT_REL))) {
                     extractAttribute(tag, ATT_HREF);
                 }
             } else {
                 extractAttribute(tag, ATT_BACKGROUND);
             }
 
 
             // Now look for URLs in the STYLE attribute
             String styleTagStr = tag.attr(ATT_STYLE);
             if(styleTagStr != null) {
                 HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr);
             }
         }
 
         @Override
         public void tail(Node arg0, int arg1) {
             // Noop
         }
     }
 
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
             // TODO Handle conditional comments for IE
             String contents = new String(html,encoding);
             Document doc = Jsoup.parse(contents);
             JMeterNodeVisitor nodeVisitor = new JMeterNodeVisitor(new URLPointer(baseUrl), coll);
             new NodeTraversor(nodeVisitor).traverse(doc);
             return coll.iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
index 2a4de1fcd..951cbdc65 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
@@ -1,232 +1,231 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.Stack;
 
+import org.apache.commons.lang3.StringUtils;
+import org.apache.jmeter.protocol.http.util.ConversionUtils;
+import org.slf4j.Logger;
+
 import jodd.lagarto.EmptyTagVisitor;
 import jodd.lagarto.LagartoException;
 import jodd.lagarto.LagartoParser;
 import jodd.lagarto.LagartoParserConfig;
 import jodd.lagarto.Tag;
 import jodd.lagarto.TagType;
 import jodd.lagarto.TagUtil;
 import jodd.lagarto.dom.HtmlCCommentExpressionMatcher;
 import jodd.log.LoggerFactory;
 import jodd.log.impl.Slf4jLoggerFactory;
 
-import org.apache.commons.lang3.StringUtils;
-import org.apache.jmeter.protocol.http.util.ConversionUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
-
 /**
  * Parser based on Lagarto
  * @since 2.10
  */
 public class LagartoBasedHtmlParser extends HTMLParser {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = org.slf4j.LoggerFactory.getLogger(LagartoBasedHtmlParser.class);
     static {
         LoggerFactory.setLoggerFactory(new Slf4jLoggerFactory());
     }
 
     /*
      * A dummy class to pass the pointer of URL.
      */
     private static class URLPointer {
         private URLPointer(URL newUrl) {
             url = newUrl;
         }
         private URL url;
     }
     
     private static final class JMeterTagVisitor extends EmptyTagVisitor {
         private HtmlCCommentExpressionMatcher htmlCCommentExpressionMatcher;
         private URLCollection urls;
         private URLPointer baseUrl;
         private Float ieVersion;
         private Stack<Boolean> enabled = new Stack<>();
 
         /**
          * @param baseUrl base url to add possibly missing information to urls found in <code>urls</code>
          * @param urls collection of urls to consider
          * @param ieVersion version number of IE to emulate
          */
         public JMeterTagVisitor(final URLPointer baseUrl, URLCollection urls, Float ieVersion) {
             this.urls = urls;
             this.baseUrl = baseUrl;
             this.ieVersion = ieVersion;
         }
 
         private void extractAttribute(Tag tag, String attributeName) {
             CharSequence url = tag.getAttributeValue(attributeName);
             if (!StringUtils.isEmpty(url)) {
                 urls.addURL(url.toString(), baseUrl.url);
             }
         }
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#script(jodd.lagarto.Tag,
          * java.lang.CharSequence)
          */
         @Override
         public void script(Tag tag, CharSequence body) {
             if (!enabled.peek().booleanValue()) {
                 return;
             }
             extractAttribute(tag, ATT_SRC);
         }
 
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#tag(jodd.lagarto.Tag)
          */
         @Override
         public void tag(Tag tag) {
             if (!enabled.peek().booleanValue()) {
                 return;
             }
             TagType tagType = tag.getType();
             switch (tagType) {
             case START:
             case SELF_CLOSING:
                 if (tag.nameEquals(TAG_BODY)) {
                     extractAttribute(tag, ATT_BACKGROUND);
                 } else if (tag.nameEquals(TAG_BASE)) {
                     CharSequence baseref = tag.getAttributeValue(ATT_HREF);
                     try {
                         if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
                         {
                             baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref.toString());
                         }
                     } catch (MalformedURLException e1) {
                         throw new RuntimeException(e1);
                     }
                 } else if (tag.nameEquals(TAG_IMAGE)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_APPLET)) {
                     extractAttribute(tag, ATT_CODE);
                 } else if (tag.nameEquals(TAG_OBJECT)) {
                     extractAttribute(tag, ATT_CODEBASE);                
                     extractAttribute(tag, ATT_DATA);                 
                 } else if (tag.nameEquals(TAG_INPUT)) {
                     // we check the input tag type for image
                     CharSequence type = tag.getAttributeValue(ATT_TYPE);
                     if (type != null && TagUtil.equalsIgnoreCase(ATT_IS_IMAGE, type)) {
                         // then we need to download the binary
                         extractAttribute(tag, ATT_SRC);
                     }
                 } else if (tag.nameEquals(TAG_SCRIPT)) {
                     extractAttribute(tag, ATT_SRC);
                     // Bug 51750
                 } else if (tag.nameEquals(TAG_FRAME) || tag.nameEquals(TAG_IFRAME)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_EMBED)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_BGSOUND)){
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_LINK)) {
                     CharSequence relAttribute = tag.getAttributeValue(ATT_REL);
                     // Putting the string first means it works even if the attribute is null
                     if (relAttribute != null && TagUtil.equalsIgnoreCase(STYLESHEET,relAttribute)) {
                         extractAttribute(tag, ATT_HREF);
                     }
                 } else {
                     extractAttribute(tag, ATT_BACKGROUND);
                 }
     
     
                 // Now look for URLs in the STYLE attribute
                 CharSequence styleTagStr = tag.getAttributeValue(ATT_STYLE);
                 if(!StringUtils.isEmpty(styleTagStr)) {
                     HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr.toString());
                 }
                 break;
             case END:
                 break;
             default:
                 throw new IllegalStateException("Unexpected tagType " + tagType);
             }
         }
 
         /* (non-Javadoc)
          * @see jodd.lagarto.EmptyTagVisitor#condComment(java.lang.CharSequence, boolean, boolean, boolean)
          */
         @Override
         public void condComment(CharSequence expression, boolean isStartingTag,
                 boolean isHidden, boolean isHiddenEndTag) {
             // See http://css-tricks.com/how-to-create-an-ie-only-stylesheet/
             if(!isStartingTag) {
                 enabled.pop();
             } else {
                 if (htmlCCommentExpressionMatcher == null) {
                     htmlCCommentExpressionMatcher = new HtmlCCommentExpressionMatcher();
                 }
                 String expressionString = expression.toString().trim();
                 enabled.push(Boolean.valueOf(htmlCCommentExpressionMatcher.match(ieVersion.floatValue(),
                         expressionString)));
             }
         }
 
         /* (non-Javadoc)
          * @see jodd.lagarto.EmptyTagVisitor#start()
          */
         @Override
         public void start() {
             super.start();
             enabled.clear();
             enabled.push(Boolean.TRUE);
         }
     }
 
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
             Float ieVersion = extractIEVersion(userAgent);
             
             String contents = new String(html,encoding); 
             // As per Jodd javadocs, emitStrings should be false for visitor for better performances
             LagartoParser lagartoParser = new LagartoParser(contents, false);
             LagartoParserConfig<LagartoParserConfig<?>> config = new LagartoParserConfig<>();
             config.setCaseSensitive(false);
             // Conditional comments only apply for IE < 10
             config.setEnableConditionalComments(isEnableConditionalComments(ieVersion));
             
             lagartoParser.setConfig(config);
             JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll, ieVersion);
             lagartoParser.parse(tagVisitor);
             return coll.iterator();
         } catch (LagartoException e) {
             // TODO is it the best way ? https://bz.apache.org/bugzilla/show_bug.cgi?id=55634
             if(log.isDebugEnabled()) {
                 log.debug("Error extracting embedded resource URLs from:'"+baseUrl+"', probably not text content, message:"+e.getMessage());
             }
             return Collections.<URL>emptyList().iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
index 54400a6cc..d29e8c81d 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
@@ -1,191 +1,187 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Iterator;
 
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
-//NOTE: Also looked at using Java 1.4 regexp instead of ORO. The change was
-//trivial. Performance did not improve -- at least not significantly.
-//Finally decided for ORO following advise from Stefan Bodewig (message
-//to jmeter-dev dated 25 Nov 2003 8:52 CET) [Jordi]
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * HtmlParser implementation using regular expressions.
  * <p>
  * This class will find RLs specified in the following ways (where <b>url</b>
  * represents the RL being found:
  * <ul>
  * <li>&lt;img src=<b>url</b> ... &gt;
  * <li>&lt;script src=<b>url</b> ... &gt;
  * <li>&lt;applet code=<b>url</b> ... &gt;
  * <li>&lt;input type=image src=<b>url</b> ... &gt;
  * <li>&lt;body background=<b>url</b> ... &gt;
  * <li>&lt;table background=<b>url</b> ... &gt;
  * <li>&lt;td background=<b>url</b> ... &gt;
  * <li>&lt;tr background=<b>url</b> ... &gt;
  * <li>&lt;applet ... codebase=<b>url</b> ... &gt;
  * <li>&lt;embed src=<b>url</b> ... &gt;
  * <li>&lt;embed codebase=<b>url</b> ... &gt;
  * <li>&lt;object codebase=<b>url</b> ... &gt;
  * <li>&lt;link rel=stylesheet href=<b>url</b>... gt;
  * <li>&lt;bgsound src=<b>url</b> ... &gt;
  * <li>&lt;frame src=<b>url</b> ... &gt;
  * </ul>
  *
  * <p>
  * This class will take into account the following construct:
  * <ul>
  * <li>&lt;base href=<b>url</b>&gt;
  * </ul>
  *
  * <p>
  * But not the following:
  * <ul>
  * <li>&lt; ... codebase=<b>url</b> ... &gt;
  * </ul>
  *
  */
 class RegexpHTMLParser extends HTMLParser {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RegexpHTMLParser.class);
 
     /**
      * Regexp fragment matching a tag attribute's value (including the equals
      * sign and any spaces before it). Note it matches unquoted values, which to
      * my understanding, are not conformant to any of the HTML specifications,
      * but are still quite common in the web and all browsers seem to understand
      * them.
      */
     private static final String VALUE = "\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'\\s>\\\\][^\\s>]*)(?=[\\s>]))";
 
     // Note there's 3 capturing groups per value
 
     /**
      * Regexp fragment matching the separation between two tag attributes.
      */
     private static final String SEP = "\\s(?:[^>]*\\s)?";
 
     /**
      * Regular expression used against the HTML code to find the URIs of images,
      * etc.:
      */
     private static final String REGEXP =
               "<(?:" + "!--.*?-->"
             + "|BASE" + SEP + "HREF" + VALUE
             + "|(?:IMG|SCRIPT|FRAME|IFRAME|BGSOUND)" + SEP + "SRC" + VALUE
             + "|APPLET" + SEP + "CODE(?:BASE)?" + VALUE
             + "|(?:EMBED|OBJECT)" + SEP + "(?:SRC|CODEBASE|DATA)" + VALUE
             + "|(?:BODY|TABLE|TR|TD)" + SEP + "BACKGROUND" + VALUE
             + "|[^<]+?STYLE\\s*=['\"].*?URL\\(\\s*['\"](.+?)['\"]\\s*\\)"
             + "|INPUT(?:" + SEP + "(?:SRC" + VALUE
             + "|TYPE\\s*=\\s*(?:\"image\"|'image'|image(?=[\\s>])))){2,}"
             + "|LINK(?:" + SEP + "(?:HREF" + VALUE
             + "|REL\\s*=\\s*(?:\"stylesheet\"|'stylesheet'|stylesheet(?=[\\s>])))){2,}" + ")";
 
     // Number of capturing groups possibly containing Base HREFs:
     private static final int NUM_BASE_GROUPS = 3;
 
     /**
      * Thread-local input:
      */
     private static final ThreadLocal<PatternMatcherInput> localInput =
             ThreadLocal.withInitial(() -> new PatternMatcherInput(new char[0]));
 
     /**
      * Make sure to compile the regular expression upon instantiation:
      */
     protected RegexpHTMLParser() {
         super();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
         Pattern pattern= null;
         Perl5Matcher matcher = null;
         try {
             matcher = JMeterUtils.getMatcher();
             PatternMatcherInput input = localInput.get();
             // TODO: find a way to avoid the cost of creating a String here --
             // probably a new PatternMatcherInput working on a byte[] would do
             // better.
             input.setInput(new String(html, encoding)); 
             pattern=JMeterUtils.getPatternCache().getPattern(
                     REGEXP,
                     Perl5Compiler.CASE_INSENSITIVE_MASK
                     | Perl5Compiler.SINGLELINE_MASK
                     | Perl5Compiler.READ_ONLY_MASK);
 
             while (matcher.contains(input, pattern)) {
                 MatchResult match = matcher.getMatch();
                 String s;
                 if (log.isDebugEnabled()) {
                     log.debug("match groups " + match.groups() + " " + match.toString());
                 }
                 // Check for a BASE HREF:
                 for (int g = 1; g <= NUM_BASE_GROUPS && g <= match.groups(); g++) {
                     s = match.group(g);
                     if (s != null) {
                         if (log.isDebugEnabled()) {
                             log.debug("new baseUrl: " + s + " - " + baseUrl.toString());
                         }
                         try {
                             baseUrl = ConversionUtils.makeRelativeURL(baseUrl, s);
                         } catch (MalformedURLException e) {
                             // Doesn't even look like a URL?
                             // Maybe it isn't: Ignore the exception.
                             if (log.isDebugEnabled()) {
                                 log.debug("Can't build base URL from RL " + s + " in page " + baseUrl, e);
                             }
                         }
                     }
                 }
                 for (int g = NUM_BASE_GROUPS + 1; g <= match.groups(); g++) {
                     s = match.group(g);
                     if (s != null) {
                         if (log.isDebugEnabled()) {
                             log.debug("group " + g + " - " + match.group(g));
                         }
                         urls.addURL(s, baseUrl);
                     }
                 }
             }
             return urls.iterator();
         } catch (UnsupportedEncodingException
                 | MalformedCachePatternException e) {
             throw new HTMLParseException(e.getMessage(), e);
         } finally {
             JMeterUtils.clearMatcherMemory(matcher, pattern);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
index 21ca5aa25..315479a2a 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
@@ -1,136 +1,136 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.commons.lang3.Validate;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 /**
  * Collection class designed for handling URLs
  * <p>
  * Before a URL is added to the collection, it is wrapped in a URLString class.
  * The iterator unwraps the URL before return.
  * <p>
  * N.B. Designed for use by HTMLParser, so is not a full implementation - e.g.
  * does not support remove()
  *
  */
 public class URLCollection implements Iterable<URL> {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(URLCollection.class);
     private final Collection<URLString> coll;
 
     /**
      * Creates a new URLCollection from an existing Collection
      *
      * @param c collection to start with (Must not be {@code null})
      */
     public URLCollection(Collection<URLString> c) {
         coll = Validate.notNull(c);
     }
 
     /**
      * Adds the URL to the Collection, first wrapping it in the URLString class
      *
      * @param u
      *            URL to add
      * @return boolean condition returned by the add() method of the underlying
      *         collection
      */
     public boolean add(URL u) {
         return coll.add(new URLString(u));
     }
 
     /**
      * Convenience method for adding URLs to the collection. If the url
      * parameter is <code>null</code>, empty or URL is malformed, nothing is
      * done
      *
      * @param url
      *            String, may be null or empty
      * @param baseUrl
      *            base for <code>url</code> to add information, which might be
      *            missing in <code>url</code>
      * @return boolean condition returned by the add() method of the underlying
      *         collection
      */
     public boolean addURL(String url, URL baseUrl) {
         if (url == null || url.length() == 0) {
             return false;
         }
         //url.replace('+',' ');
         url=StringEscapeUtils.unescapeXml(url);
         boolean b = false;
         try {
             b = this.add(ConversionUtils.makeRelativeURL(baseUrl, url));
         } catch (MalformedURLException mfue) {
             // No WARN message to avoid performance impact
             if(log.isDebugEnabled()) {
                 log.debug("Error occured building relative url for:"+url+", message:"+mfue.getMessage());
             }
             // No point in adding the URL as String as it will result in null 
             // returned during iteration, see URLString
             // See https://bz.apache.org/bugzilla/show_bug.cgi?id=55092
             return false;
         }
         return b;
     }
 
     @Override
     public Iterator<URL> iterator() {
         return new UrlIterator(coll.iterator());
     }
 
     /*
      * Private iterator used to unwrap the URL from the URLString class
      *
      */
     private static class UrlIterator implements Iterator<URL> {
         private final Iterator<URLString> iter;
 
         UrlIterator(Iterator<URLString> i) {
             iter = i;
         }
 
         @Override
         public boolean hasNext() {
             return iter.hasNext();
         }
 
         /*
          * Unwraps the URLString class to return the URL
          */
         @Override
         public URL next() {
             return iter.next().getURL();
         }
 
         @Override
         public void remove() {
             throw new UnsupportedOperationException();
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java
index 5db329085..733570167 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java
@@ -1,163 +1,163 @@
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
 import java.io.InterruptedIOException;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.net.SocketException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.jmeter.gui.Stoppable;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 /**
  * Web daemon thread. Creates main socket on port 8080 and listens on it
  * forever. For each client request, creates a Proxy thread to handle the
  * request.
  *
  */
 public class Daemon extends Thread implements Stoppable {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Daemon.class);
 
     /**
      * The time (in milliseconds) to wait when accepting a client connection.
      * The accept will be retried until the Daemon is told to stop. So this
      * interval is the longest time that the Daemon will have to wait after
      * being told to stop.
      */
     private static final int ACCEPT_TIMEOUT = 1000;
 
     /** The port to listen on. */
     private final int daemonPort;
 
     private final ServerSocket mainSocket;
 
     /** True if the Daemon is currently running. */
     private volatile boolean running;
 
     /** The target which will receive the generated JMeter test components. */
     private final ProxyControl target;
 
     /**
      * The proxy class which will be used to handle individual requests. This
      * class must be the {@link Proxy} class or a subclass.
      */
     private final Class<? extends Proxy> proxyClass;
 
     /**
      * Create a new Daemon with the specified port and target.
      *
      * @param port
      *            the port to listen on.
      * @param target
      *            the target which will receive the generated JMeter test
      *            components.
      * @throws IOException if an I/O error occurs opening the socket
      * @throws IllegalArgumentException if <code>port</code> is outside the allowed range from <code>0</code> to <code>65535</code>
      * @throws SocketException when something is wrong on the underlying protocol layer
      */
     public Daemon(int port, ProxyControl target) throws IOException {
         this(port, target, Proxy.class);
     }
 
     /**
      * Create a new Daemon with the specified port and target, using the
      * specified class to handle individual requests.
      *
      * @param port
      *            the port to listen on.
      * @param target
      *            the target which will receive the generated JMeter test
      *            components.
      * @param proxyClass
      *            the proxy class to use to handle individual requests. This
      *            class must be the {@link Proxy} class or a subclass.
      * @throws IOException if an I/O error occurs opening the socket
      * @throws IllegalArgumentException if <code>port</code> is outside the allowed range from <code>0</code> to <code>65535</code>
      * @throws SocketException when something is wrong on the underlying protocol layer
      */
     public Daemon(int port, ProxyControl target, Class<? extends Proxy> proxyClass) throws IOException {
         super("HTTP Proxy Daemon");
         this.target = target;
         this.daemonPort = port;
         this.proxyClass = proxyClass;
         log.info("Creating Daemon Socket on port: " + daemonPort);
         mainSocket = new ServerSocket(daemonPort);
         mainSocket.setSoTimeout(ACCEPT_TIMEOUT);
     }
 
     /**
      * Listen on the daemon port and handle incoming requests. This method will
      * not exit until {@link #stopServer()} is called or an error occurs.
      */
     @Override
     public void run() {
         running = true;
         log.info("Test Script Recorder up and running!");
 
         // Maps to contain page and form encodings
         // TODO - do these really need to be shared between all Proxy instances?
         Map<String, String> pageEncodings = Collections.synchronizedMap(new HashMap<String, String>());
         Map<String, String> formEncodings = Collections.synchronizedMap(new HashMap<String, String>());
 
         try {
             while (running) {
                 try {
                     // Listen on main socket
                     Socket clientSocket = mainSocket.accept();
                     if (running) {
                         // Pass request to new proxy thread
                         Proxy thd = proxyClass.newInstance();
                         thd.configure(clientSocket, target, pageEncodings, formEncodings);
                         thd.start();
                     }
                 } catch (InterruptedIOException ignored) {
                     // Timeout occurred. Ignore, and keep looping until we're
                     // told to stop running.
                 }
             }
             log.info("HTTP(S) Test Script Recorder stopped");
         } catch (Exception e) {
             log.warn("HTTP(S) Test Script Recorder stopped", e);
         } finally {
             JOrphanUtils.closeQuietly(mainSocket);
         }
 
         // Clear maps
         pageEncodings = null;
         formEncodings = null;
     }
 
     /**
      * Stop the proxy daemon. The daemon may not stop immediately.
      *
      * see #ACCEPT_TIMEOUT
      */
     @Override
     public void stopServer() {
         running = false;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
index c548e4c0c..5c9fab5b4 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/DefaultSamplerCreator.java
@@ -1,421 +1,421 @@
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 import org.xml.sax.XMLReader;
 import org.xml.sax.helpers.DefaultHandler;
 
 /**
  * Default implementation that handles classical HTTP textual + Multipart requests
  */
 public class DefaultSamplerCreator extends AbstractSamplerCreator {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(DefaultSamplerCreator.class);
  
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
      * @param pageEncodings Map of page encodings
      * @param formEncodings Map of form encodings
      * @throws Exception when something fails
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
      * @throws Exception when something fails
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
     private static boolean isPotentialXml(String postData) {
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
         } catch (ParserConfigurationException | SAXException | IOException e) {
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
      * @param pageEncodings Map of page encodings
      * @param formEncodings Map of form encodings
      * @throws MalformedURLException when no {@link URL} could be built from
      *         <code>sampler</code> and <code>request</code>
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
      * @param pageEncodings Map of page encodings
      * @param formEncodings Map of form encodings
      * @param urlWithoutQuery the request URL without the query parameters
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java
index e2b0949cd..779cb3a2e 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java
@@ -1,86 +1,86 @@
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
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 
 /**
  * A parser for html, to find the form tags, and their accept-charset value
  */
 // made public see Bug 49976
 public class FormCharSetFinder {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(FormCharSetFinder.class);
 
     public FormCharSetFinder() {
         super();
     }
 
     /**
      * Add form action urls and their corresponding encodings for all forms on the page
      *
      * @param html the html to parse for form encodings
      * @param formEncodings the Map where form encodings should be added
      * @param pageEncoding the encoding used for the whole page
      * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public void addFormActionsAndCharSet(String html, Map<String, String> formEncodings, String pageEncoding)
             throws HTMLParseException {
         if (log.isDebugEnabled()) {
             log.debug("Parsing html of: " + html);
         }
 
         Document document = Jsoup.parse(html);
         Elements forms = document.select("form");
         for (Element element : forms) {
             String action = element.attr("action");
             if( !(StringUtils.isEmpty(action)) ) {
                 // We use the page encoding where the form resides, as the
                 // default encoding for the form
                 String formCharSet = pageEncoding;
                 String acceptCharSet = element.attr("accept-charset");
                 // Check if we found an accept-charset attribute on the form
                 if(acceptCharSet != null) {
                     String[] charSets = JOrphanUtils.split(acceptCharSet, ",");
                     // Just use the first one of the possible many charsets
                     if(charSets.length > 0) {
                         formCharSet = charSets[0].trim();
                         if(formCharSet.length() == 0) {
                             formCharSet = null;
                         }
                     }
                 }
                 if(formCharSet != null) {
                     synchronized (formEncodings) {
                         formEncodings.put(action, formCharSet);
                     }
                 }
             }      
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
index 1a332000a..e5b287877 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
@@ -1,442 +1,442 @@
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
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.StringTokenizer;
 
 import org.apache.commons.lang3.CharUtils;
 import org.apache.jmeter.protocol.http.config.MultipartUrlConfig;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.gui.HeaderPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 //For unit tests, @see TestHttpRequestHdr
 
 /**
  * The headers of the client HTTP request.
  *
  */
 public class HttpRequestHdr {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HttpRequestHdr.class);
 
     private static final String HTTP = "http"; // $NON-NLS-1$
     private static final String HTTPS = "https"; // $NON-NLS-1$
     private static final String PROXY_CONNECTION = "proxy-connection"; // $NON-NLS-1$
     public static final String CONTENT_TYPE = "content-type"; // $NON-NLS-1$
     public static final String CONTENT_LENGTH = "content-length"; // $NON-NLS-1$
 
 
     /**
      * Http Request method, uppercased, e.g. GET or POST.
      */
     private String method = ""; // $NON-NLS-1$
 
     /** CONNECT url. */
     private String paramHttps = ""; // $NON-NLS-1$
 
     /**
      * The requested url. The universal resource locator that hopefully uniquely
      * describes the object or service the client is requesting.
      */
     private String url = ""; // $NON-NLS-1$
 
     /**
      * Version of http being used. Such as HTTP/1.0.
      */
     private String version = ""; // NOTREAD // $NON-NLS-1$
 
     private byte[] rawPostData;
 
     private final Map<String, Header> headers = new HashMap<>();
 
     private final String httpSamplerName;
 
     private HeaderManager headerManager;
 
     private String firstLine; // saved copy of first line for error reports
 
     public HttpRequestHdr() {
         this.httpSamplerName = ""; // $NON-NLS-1$
         this.firstLine = "" ; // $NON-NLS-1$
     }
 
     /**
      * @param httpSamplerName the http sampler name
      */
     public HttpRequestHdr(String httpSamplerName) {
         this.httpSamplerName = httpSamplerName;
     }
 
     /**
      * Parses a http header from a stream.
      *
      * @param in
      *            the stream to parse.
      * @return array of bytes from client.
      * @throws IOException when reading the input stream fails
      */
     public byte[] parse(InputStream in) throws IOException {
         boolean inHeaders = true;
         int readLength = 0;
         int dataLength = 0;
         boolean firstLine = true;
         ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
         ByteArrayOutputStream line = new ByteArrayOutputStream();
         int x;
         while ((inHeaders || readLength < dataLength) && ((x = in.read()) != -1)) {
             line.write(x);
             clientRequest.write(x);
             if (firstLine && !CharUtils.isAscii((char) x)){// includes \n
                 throw new IllegalArgumentException("Only ASCII supported in headers (perhaps SSL was used?)");
             }
             if (inHeaders && (byte) x == (byte) '\n') { // $NON-NLS-1$
                 if (line.size() < 3) {
                     inHeaders = false;
                     firstLine = false; // cannot be first line either
                 }
                 final String reqLine = line.toString();
                 if (firstLine) {
                     parseFirstLine(reqLine);
                     firstLine = false;
                 } else {
                     // parse other header lines, looking for Content-Length
                     final int contentLen = parseLine(reqLine);
                     if (contentLen > 0) {
                         dataLength = contentLen; // Save the last valid content length one
                     }
                 }
                 if (log.isDebugEnabled()){
                     log.debug("Client Request Line: '" + reqLine.replaceFirst("\r\n$", "<CRLF>") + "'");
                 }
                 line.reset();
             } else if (!inHeaders) {
                 readLength++;
             }
         }
         // Keep the raw post data
         rawPostData = line.toByteArray();
 
         if (log.isDebugEnabled()){
             log.debug("rawPostData in default JRE encoding: " + new String(rawPostData)); // TODO - charset?
             log.debug("Request: '" + clientRequest.toString().replaceAll("\r\n", "<CRLF>") + "'");
         }
         return clientRequest.toByteArray();
     }
 
     private void parseFirstLine(String firstLine) {
         this.firstLine = firstLine;
         if (log.isDebugEnabled()) {
             log.debug("browser request: " + firstLine.replaceFirst("\r\n$", "<CRLF>"));
         }
         StringTokenizer tz = new StringTokenizer(firstLine);
         method = getToken(tz).toUpperCase(java.util.Locale.ENGLISH);
         url = getToken(tz);
         version = getToken(tz);
         if (log.isDebugEnabled()) {
             log.debug("parsed method:   " + method);
             log.debug("parsed url/host: " + url); // will be host:port for CONNECT
             log.debug("parsed version:  " + version);
         }
         // SSL connection
         if (getMethod().startsWith(HTTPConstants.CONNECT)) {
             paramHttps = url;
             return; // Don't try to adjust the host name
         }
         /* The next line looks odd, but proxied HTTP requests look like:
          * GET http://www.apache.org/foundation/ HTTP/1.1
          * i.e. url starts with "http:", not "/"
          * whereas HTTPS proxy requests look like:
          * CONNECT www.google.co.uk:443 HTTP/1.1
          * followed by
          * GET /?gws_rd=cr HTTP/1.1
          */
         if (url.startsWith("/")) { // it must be a proxied HTTPS request
             url = HTTPS + "://" + paramHttps + url; // $NON-NLS-1$
         }
         // JAVA Impl accepts URLs with unsafe characters so don't do anything
         if(HTTPSamplerFactory.IMPL_JAVA.equals(httpSamplerName)) {
             log.debug("First Line url: " + url);
             return;
         }
         try {
             // See Bug 54482
             URI testCleanUri = new URI(url);
             if(log.isDebugEnabled()) {
                 log.debug("Successfully built URI from url:"+url+" => " + testCleanUri.toString());
             }
         } catch (URISyntaxException e) {
             log.warn("Url '" + url + "' contains unsafe characters, will escape it, message:"+e.getMessage());
             try {
                 String escapedUrl = ConversionUtils.escapeIllegalURLCharacters(url);
                 if(log.isDebugEnabled()) {
                     log.debug("Successfully escaped url:'"+url +"' to:'"+escapedUrl+"'");
                 }
                 url = escapedUrl;
             } catch (Exception e1) {
                 log.error("Error escaping URL:'"+url+"', message:"+e1.getMessage());
             }
         }
         log.debug("First Line url: " + url);
     }
 
     /*
      * Split line into name/value pairs and store in headers if relevant
      * If name = "content-length", then return value as int, else return 0
      */
     private int parseLine(String nextLine) {
         int colon = nextLine.indexOf(':');
         if (colon <= 0){
             return 0; // Nothing to do
         }
         String name = nextLine.substring(0, colon).trim();
         String value = nextLine.substring(colon+1).trim();
         headers.put(name.toLowerCase(java.util.Locale.ENGLISH), new Header(name, value));
         if (name.equalsIgnoreCase(CONTENT_LENGTH)) {
             return Integer.parseInt(value);
         }
         return 0;
     }
 
     private HeaderManager createHeaderManager() {
         HeaderManager manager = new HeaderManager();
         for (Map.Entry<String, Header> entry : headers.entrySet()) {
             final String key = entry.getKey();
             if (!key.equals(PROXY_CONNECTION)
              && !key.equals(CONTENT_LENGTH)
              && !key.equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION)) {
                 manager.add(entry.getValue());
             }
         }
         manager.setName(JMeterUtils.getResString("header_manager_title")); // $NON-NLS-1$
         manager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
         manager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
         return manager;
     }
 
     public HeaderManager getHeaderManager() {
         if(headerManager == null) {
             headerManager = createHeaderManager();
         }
         return headerManager;
     }
 
     public String getContentType() {
         Header contentTypeHeader = headers.get(CONTENT_TYPE);
         if (contentTypeHeader != null) {
             return contentTypeHeader.getValue();
         }
         return null;
     }
 
     private boolean isMultipart(String contentType) {
         if (contentType != null && contentType.startsWith(HTTPConstants.MULTIPART_FORM_DATA)) {
             return true;
         }
         return false;
     }
 
     public MultipartUrlConfig getMultipartConfig(String contentType) {
         if(isMultipart(contentType)) {
             // Get the boundary string for the multiparts from the content type
             String boundaryString = contentType.substring(contentType.toLowerCase(java.util.Locale.ENGLISH).indexOf("boundary=") + "boundary=".length());
             return new MultipartUrlConfig(boundaryString);
         }
         return null;
     }
 
     //
     // Parsing Methods
     //
 
     /**
      * Find the //server.name from an url.
      *
      * @return server's internet name
      */
     public String serverName() {
         // chop to "server.name:x/thing"
         String str = url;
         int i = str.indexOf("//"); // $NON-NLS-1$
         if (i > 0) {
             str = str.substring(i + 2);
         }
         // chop to server.name:xx
         i = str.indexOf('/'); // $NON-NLS-1$
         if (0 < i) {
             str = str.substring(0, i);
         }
         // chop to server.name
         i = str.lastIndexOf(':'); // $NON-NLS-1$
         if (0 < i) {
             str = str.substring(0, i);
         }
         // Handle IPv6 urls
         if(str.startsWith("[")&& str.endsWith("]")) {
             return str.substring(1, str.length()-1);
         }
         return str;
     }
 
     // TODO replace repeated substr() above and below with more efficient method.
 
     /**
      * Find the :PORT from http://server.ect:PORT/some/file.xxx
      *
      * @return server's port (or UNSPECIFIED if not found)
      */
     public int serverPort() {
         String str = url;
         // chop to "server.name:x/thing"
         int i = str.indexOf("//");
         if (i > 0) {
             str = str.substring(i + 2);
         }
         // chop to server.name:xx
         i = str.indexOf('/');
         if (0 < i) {
             str = str.substring(0, i);
         }
         // chop to server.name
         i = str.lastIndexOf(':');
         if (0 < i) {
             return Integer.parseInt(str.substring(i + 1).trim());
         }
         return HTTPSamplerBase.UNSPECIFIED_PORT;
     }
 
     /**
      * Find the /some/file.xxxx from http://server.ect:PORT/some/file.xxx
      *
      * @return the path
      */
     public String getPath() {
         String str = url;
         int i = str.indexOf("//");
         if (i > 0) {
             str = str.substring(i + 2);
         }
         i = str.indexOf('/');
         if (i < 0) {
             return "";
         }
         return str.substring(i);
     }
 
     /**
      * Returns the url string extracted from the first line of the client request.
      *
      * @return the url
      */
     public String getUrl(){
         return url;
     }
 
     /**
      * Returns the method string extracted from the first line of the client request.
      *
      * @return the method (will always be upper case)
      */
     public String getMethod(){
         return method;
     }
 
     public String getFirstLine() {
         return firstLine;
     }
 
     /**
      * Returns the next token in a string.
      *
      * @param tk
      *            String that is partially tokenized.
      * @return The remainder
      */
     private String getToken(StringTokenizer tk) {
         if (tk.hasMoreTokens()) {
             return tk.nextToken();
         }
         return "";// $NON-NLS-1$
     }
 
 
     public String getUrlWithoutQuery(URL _url) {
         String fullUrl = _url.toString();
         String urlWithoutQuery = fullUrl;
         String query = _url.getQuery();
         if(query != null) {
             // Get rid of the query and the ?
             urlWithoutQuery = urlWithoutQuery.substring(0, urlWithoutQuery.length() - query.length() - 1);
         }
         return urlWithoutQuery;
     }
 
     /**
      * @return the httpSamplerName
      */
     public String getHttpSamplerName() {
         return httpSamplerName;
     }
 
     /**
      * @return byte[] Raw post data
      */
     public byte[] getRawPostData() {
         return rawPostData;
     }
 
     /**
      * @param sampler {@link HTTPSamplerBase}
      * @return String Protocol (http or https)
      */
     public String getProtocol(HTTPSamplerBase sampler) {
         if (url.contains("//")) {
             String protocol = url.substring(0, url.indexOf(':'));
             if (log.isDebugEnabled()) {
                 log.debug("Proxy: setting protocol to : " + protocol);
             }
             return protocol;
         } else if (sampler.getPort() == HTTPConstants.DEFAULT_HTTPS_PORT) {
             if (log.isDebugEnabled()) {
                 log.debug("Proxy: setting protocol to https");
             }
             return HTTPS;
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Proxy setting default protocol to: http");
             }
             return HTTP;
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java
index 6d563f73d..45af445d9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Proxy.java
@@ -1,627 +1,627 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.net.Socket;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.nio.charset.IllegalCharsetNameException;
 import java.security.GeneralSecurityException;
 import java.security.KeyStore;
 import java.security.KeyStoreException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import javax.net.ssl.KeyManager;
 import javax.net.ssl.KeyManagerFactory;
 import javax.net.ssl.SSLContext;
 import javax.net.ssl.SSLSocket;
 import javax.net.ssl.SSLSocketFactory;
 
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 /**
  * Thread to handle one client request. Gets the request from the client and
  * passes it on to the server, then sends the response back to the client.
  * Information about the request and response is stored so it can be used in a
  * JMeter test plan.
  *
  */
 public class Proxy extends Thread {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Proxy.class);
 
     private static final byte[] CRLF_BYTES = { 0x0d, 0x0a };
     private static final String CRLF_STRING = "\r\n";
 
     private static final String NEW_LINE = "\n"; // $NON-NLS-1$
 
     private static final String[] HEADERS_TO_REMOVE;
 
     // Allow list of headers to be overridden
     private static final String PROXY_HEADERS_REMOVE = "proxy.headers.remove"; // $NON-NLS-1$
 
     private static final String PROXY_HEADERS_REMOVE_DEFAULT = "If-Modified-Since,If-None-Match,Host"; // $NON-NLS-1$
 
     private static final String PROXY_HEADERS_REMOVE_SEPARATOR = ","; // $NON-NLS-1$
 
     private static final String KEYMANAGERFACTORY =
         JMeterUtils.getPropDefault("proxy.cert.factory", "SunX509"); // $NON-NLS-1$ $NON-NLS-2$
 
     private static final String SSLCONTEXT_PROTOCOL =
         JMeterUtils.getPropDefault("proxy.ssl.protocol", "TLS"); // $NON-NLS-1$ $NON-NLS-2$
 
     // HashMap to save ssl connection between Jmeter proxy and browser
     private static final HashMap<String, SSLSocketFactory> HOST2SSL_SOCK_FAC = new HashMap<>();
 
     private static final SamplerCreatorFactory SAMPLERFACTORY = new SamplerCreatorFactory();
 
     static {
         String removeList = JMeterUtils.getPropDefault(PROXY_HEADERS_REMOVE,PROXY_HEADERS_REMOVE_DEFAULT);
         HEADERS_TO_REMOVE = JOrphanUtils.split(removeList,PROXY_HEADERS_REMOVE_SEPARATOR);
         log.info("Proxy will remove the headers: "+removeList);
     }
 
     // Use with SSL connection
     private OutputStream outStreamClient = null;
 
     /** Socket to client. */
     private Socket clientSocket = null;
 
     /** Target to receive the generated sampler. */
     private ProxyControl target;
 
     /** Whether or not to capture the HTTP headers. */
     private boolean captureHttpHeaders;
 
     /** Reference to Daemon's Map of url string to page character encoding of that page */
     private Map<String, String> pageEncodings;
     /** Reference to Daemon's Map of url string to character encoding for the form */
     private Map<String, String> formEncodings;
 
     private String port; // For identifying log messages
 
     private KeyStore keyStore; // keystore for SSL keys; fixed at config except for dynamic host key generation
 
     private String keyPassword;
 
     /**
      * Default constructor - used by newInstance call in Daemon
      */
     public Proxy() {
         port = "";
     }
 
     /**
      * Configure the Proxy.
      * Intended to be called directly after construction.
      * Should not be called after it has been passed to a new thread,
      * otherwise the variables may not be published correctly.
      *
      * @param _clientSocket
      *            the socket connection to the client
      * @param _target
      *            the ProxyControl which will receive the generated sampler
      * @param _pageEncodings
      *            reference to the Map of Deamon, with mappings from page urls to encoding used
      * @param _formEncodings
      *            reference to the Map of Deamon, with mappings from form action urls to encoding used
      */
     void configure(Socket _clientSocket, ProxyControl _target, Map<String, String> _pageEncodings, Map<String, String> _formEncodings) {
         this.target = _target;
         this.clientSocket = _clientSocket;
         this.captureHttpHeaders = _target.getCaptureHttpHeaders();
         this.pageEncodings = _pageEncodings;
         this.formEncodings = _formEncodings;
         this.port = "["+ clientSocket.getPort() + "] ";
         this.keyStore = _target.getKeyStore();
         this.keyPassword = _target.getKeyPassword();
     }
 
     /**
      * Main processing method for the Proxy object
      */
     @Override
     public void run() {
         // Check which HTTPSampler class we should use
         String httpSamplerName = target.getSamplerTypeName();
 
         HttpRequestHdr request = new HttpRequestHdr(httpSamplerName);
         SampleResult result = null;
         HeaderManager headers = null;
         HTTPSamplerBase sampler = null;
         final boolean isDebug = log.isDebugEnabled();
         if (isDebug) {
             log.debug(port + "====================================================================");
         }
         SamplerCreator samplerCreator = null;
         try {
             // Now, parse initial request (in case it is a CONNECT request)
             byte[] ba = request.parse(new BufferedInputStream(clientSocket.getInputStream()));
             if (ba.length == 0) {
                 if (isDebug) {
                     log.debug(port + "Empty request, ignored");
                 }
                 throw new JMeterException(); // hack to skip processing
             }
             if (isDebug) {
                 log.debug(port + "Initial request: " + new String(ba));
             }
             outStreamClient = clientSocket.getOutputStream();
 
             if ((request.getMethod().startsWith(HTTPConstants.CONNECT)) && (outStreamClient != null)) {
                 if (isDebug) {
                     log.debug(port + "Method CONNECT => SSL");
                 }
                 // write a OK reponse to browser, to engage SSL exchange
                 outStreamClient.write(("HTTP/1.0 200 OK\r\n\r\n").getBytes(SampleResult.DEFAULT_HTTP_ENCODING)); // $NON-NLS-1$
                 outStreamClient.flush();
                // With ssl request, url is host:port (without https:// or path)
                 String[] param = request.getUrl().split(":");  // $NON-NLS-1$
                 if (param.length == 2) {
                     if (isDebug) {
                         log.debug(port + "Start to negotiate SSL connection, host: " + param[0]);
                     }
                     clientSocket = startSSL(clientSocket, param[0]);
                 } else {
                     // Should not happen, but if it does we don't want to continue 
                     log.error("In SSL request, unable to find host and port in CONNECT request: " + request.getUrl());
                     throw new JMeterException(); // hack to skip processing
                 }
                 // Re-parse (now it's the http request over SSL)
                 try {
                     ba = request.parse(new BufferedInputStream(clientSocket.getInputStream()));
                 } catch (IOException ioe) { // most likely this is because of a certificate error
                     // param.length is 2 here
                     final String url = " for '"+ param[0] +"'";
                     log.warn(port + "Problem with SSL certificate"+url+"? Ensure browser is set to accept the JMeter proxy cert: " + ioe.getMessage());
                     // won't work: writeErrorToClient(HttpReplyHdr.formInternalError());
                     result = generateErrorResult(result, request, ioe, "\n**ensure browser is set to accept the JMeter proxy certificate**"); // Generate result (if nec.) and populate it
                     throw new JMeterException(); // hack to skip processing
                 }
                 if (isDebug) {
                     log.debug(port + "Reparse: " + new String(ba));
                 }
                 if (ba.length == 0) {
                     log.warn(port + "Empty response to http over SSL. Probably waiting for user to authorize the certificate for " + request.getUrl());
                     throw new JMeterException(); // hack to skip processing
                 }
             }
 
             samplerCreator = SAMPLERFACTORY.getSamplerCreator(request, pageEncodings, formEncodings);
             sampler = samplerCreator.createAndPopulateSampler(request, pageEncodings, formEncodings);
 
             /*
              * Create a Header Manager to ensure that the browsers headers are
              * captured and sent to the server
              */
             headers = request.getHeaderManager();
             sampler.setHeaderManager(headers);
 
             sampler.threadStarted(); // Needed for HTTPSampler2
             if (isDebug) {
                 log.debug(port + "Execute sample: " + sampler.getMethod() + " " + sampler.getUrl());
             }
             result = sampler.sample();
 
             // Find the page encoding and possibly encodings for forms in the page
             // in the response from the web server
             String pageEncoding = addPageEncoding(result);
             addFormEncodings(result, pageEncoding);
 
             writeToClient(result, new BufferedOutputStream(clientSocket.getOutputStream()));
             samplerCreator.postProcessSampler(sampler, result);
         } catch (JMeterException jme) {
             // ignored, already processed
         } catch (UnknownHostException uhe) {
             log.warn(port + "Server Not Found.", uhe);
             writeErrorToClient(HttpReplyHdr.formServerNotFound());
             result = generateErrorResult(result, request, uhe); // Generate result (if nec.) and populate it
         } catch (IllegalArgumentException e) {
             log.error(port + "Not implemented (probably used https)", e);
             writeErrorToClient(HttpReplyHdr.formNotImplemented("Probably used https instead of http. " +
                     "To record https requests, see " +
                     "<a href=\"http://jmeter.apache.org/usermanual/component_reference.html#HTTP(S)_Test_Script_Recorder\">HTTP(S) Test Script Recorder documentation</a>"));
             result = generateErrorResult(result, request, e); // Generate result (if nec.) and populate it
         } catch (Exception e) {
             log.error(port + "Exception when processing sample", e);
             writeErrorToClient(HttpReplyHdr.formInternalError());
             result = generateErrorResult(result, request, e); // Generate result (if nec.) and populate it
         } finally {
             if(sampler != null && isDebug) {
                 log.debug(port + "Will deliver sample " + sampler.getName());
             }
             /*
              * We don't want to store any cookies in the generated test plan
              */
             if (headers != null) {
                 headers.removeHeaderNamed(HTTPConstants.HEADER_COOKIE);// Always remove cookies
                 // See https://bz.apache.org/bugzilla/show_bug.cgi?id=25430
                 // HEADER_AUTHORIZATION won't be removed, it will be used
                 // for creating Authorization Manager
                 // Remove additional headers
                 for(String hdr : HEADERS_TO_REMOVE){
                     headers.removeHeaderNamed(hdr);
                 }
             }
             if(result != null) // deliverSampler allows sampler to be null, but result must not be null
             {
                 List<TestElement> children = new ArrayList<>();
                 if(captureHttpHeaders) {
                     children.add(headers);
                 }
                 if(samplerCreator != null) {
                     children.addAll(samplerCreator.createChildren(sampler, result));
                 } 
                 target.deliverSampler(sampler,
                          children
                                 .toArray(new TestElement[children.size()]),
                         result);
             }
             try {
                 clientSocket.close();
             } catch (Exception e) {
                 log.error(port + "Failed to close client socket", e);
             }
             if(sampler != null) {
                 sampler.threadFinished(); // Needed for HTTPSampler2
             }
         }
     }
 
     /**
      * Get SSL connection from hashmap, creating it if necessary.
      *
      * @param host
      * @return a ssl socket factory, or null if keystore could not be opened/processed
      */
     private SSLSocketFactory getSSLSocketFactory(String host) {
         if (keyStore == null) {
             log.error(port + "No keystore available, cannot record SSL");
             return null;
         }
         final String hashAlias;
         final String keyAlias;
         switch(ProxyControl.KEYSTORE_MODE) {
         case DYNAMIC_KEYSTORE:
             try {
                 keyStore = target.getKeyStore(); // pick up any recent changes from other threads
                 String alias = getDomainMatch(keyStore, host);
                 if (alias == null) {
                     hashAlias = host;
                     keyAlias = host;
                     keyStore = target.updateKeyStore(port, keyAlias);
                 } else if (alias.equals(host)) { // the host has a key already
                     hashAlias = host;
                     keyAlias = host;
                 } else { // the host matches a domain; use its key
                     hashAlias = alias;
                     keyAlias = alias;
                 }
             } catch (IOException | GeneralSecurityException e) {
                 log.error(port + "Problem with keystore", e);
                 return null;
             }
             break;
         case JMETER_KEYSTORE:
             hashAlias = keyAlias = ProxyControl.JMETER_SERVER_ALIAS;
             break;
         case USER_KEYSTORE:
             hashAlias = keyAlias = ProxyControl.CERT_ALIAS;
             break;
         default:
             throw new IllegalStateException("Impossible case: " + ProxyControl.KEYSTORE_MODE);
         }
         synchronized (HOST2SSL_SOCK_FAC) {
             final SSLSocketFactory sslSocketFactory = HOST2SSL_SOCK_FAC.get(hashAlias);
             if (sslSocketFactory != null) {
                 if (log.isDebugEnabled()) {
                     log.debug(port + "Good, already in map, host=" + host + " using alias " + hashAlias);
                 }
                 return sslSocketFactory;
             }
             try {
                 SSLContext sslcontext = SSLContext.getInstance(SSLCONTEXT_PROTOCOL);
                 sslcontext.init(getWrappedKeyManagers(keyAlias), null, null);
                 SSLSocketFactory sslFactory = sslcontext.getSocketFactory();
                 HOST2SSL_SOCK_FAC.put(hashAlias, sslFactory);
                 log.info(port + "KeyStore for SSL loaded OK and put host '" + host + "' in map with key ("+hashAlias+")");
                 return sslFactory;
             } catch (GeneralSecurityException e) {
                 log.error(port + "Problem with SSL certificate", e);
             } catch (IOException e) {
                 log.error(port + "Problem with keystore", e);
             }
             return null;
         }
     }
 
     /**
      * Get matching alias for a host from keyStore that may contain domain aliases.
      * Assumes domains must have at least 2 parts (apache.org);
      * does not check if TLD requires more (google.co.uk).
      * Note that DNS wildcards only apply to a single level, i.e.
      * podling.incubator.apache.org matches *.incubator.apache.org
      * but does not match *.apache.org
      *
      * @param keyStore the KeyStore to search
      * @param host the hostname to match
      * @return the keystore entry or {@code null} if no match found
      * @throws KeyStoreException 
      */
     private String getDomainMatch(KeyStore keyStore, String host) throws KeyStoreException {
         if (keyStore.containsAlias(host)) {
             return host;
         }
         String[] parts = host.split("\\."); // get the component parts
         // Assume domains must have at least 2 parts, e.g. apache.org
         // Replace the first part with "*" 
         StringBuilder sb = new StringBuilder("*"); // $NON-NLS-1$
         for(int j = 1; j < parts.length ; j++) { // Skip the first part
             sb.append('.');
             sb.append(parts[j]);
         }
         String alias = sb.toString();
         if (keyStore.containsAlias(alias)) {
             return alias;
         }
         return null;
     }
 
     /**
      * Return the key managers, wrapped to return a specific alias
      */
     private KeyManager[] getWrappedKeyManagers(final String keyAlias)
             throws GeneralSecurityException, IOException {
         if (!keyStore.containsAlias(keyAlias)) {
             throw new IOException("Keystore does not contain alias " + keyAlias);
         }
         KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEYMANAGERFACTORY);
         kmf.init(keyStore, keyPassword.toCharArray());
         final KeyManager[] keyManagers = kmf.getKeyManagers();
         // Check if alias is suitable here, rather than waiting for connection to fail
         final int keyManagerCount = keyManagers.length;
         final KeyManager[] wrappedKeyManagers = new KeyManager[keyManagerCount];
         for (int i =0; i < keyManagerCount; i++) {
             wrappedKeyManagers[i] = new ServerAliasKeyManager(keyManagers[i], keyAlias);
         }
         return wrappedKeyManagers;
     }
 
     /**
      * Negotiate a SSL connection.
      *
      * @param sock socket in
      * @param host
      * @return a new client socket over ssl
      * @throws IOException if negotiation failed
      */
     private Socket startSSL(Socket sock, String host) throws IOException {
         SSLSocketFactory sslFactory = getSSLSocketFactory(host);
         SSLSocket secureSocket;
         if (sslFactory != null) {
             try {
                 secureSocket = (SSLSocket) sslFactory.createSocket(sock,
                         sock.getInetAddress().getHostName(), sock.getPort(), true);
                 secureSocket.setUseClientMode(false);
                 if (log.isDebugEnabled()){
                     log.debug(port + "SSL transaction ok with cipher: " + secureSocket.getSession().getCipherSuite());
                 }
                 return secureSocket;
             } catch (IOException e) {
                 log.error(port + "Error in SSL socket negotiation: ", e);
                 throw e;
             }
         } else {
             log.warn(port + "Unable to negotiate SSL transaction, no keystore?");
             throw new IOException("Unable to negotiate SSL transaction, no keystore?");
         }
     }
 
     private SampleResult generateErrorResult(SampleResult result, HttpRequestHdr request, Exception e) {
         return generateErrorResult(result, request, e, "");
     }
 
     private static SampleResult generateErrorResult(SampleResult result, HttpRequestHdr request, Exception e, String msg) {
         if (result == null) {
             result = new SampleResult();
             ByteArrayOutputStream text = new ByteArrayOutputStream(200);
             e.printStackTrace(new PrintStream(text)); // NOSONAR we store the Stacktrace in the result
             result.setResponseData(text.toByteArray());
             result.setSamplerData(request.getFirstLine());
             result.setSampleLabel(request.getUrl());
         }
         result.setSuccessful(false);
         result.setResponseMessage(e.getMessage()+msg);
         return result;
     }
 
     /**
      * Write output to the output stream, then flush and close the stream.
      *
      * @param res
      *            the SampleResult to write
      * @param out
      *            the output stream to write to
      * @throws IOException
      *             if an IOException occurs while writing
      */
     private void writeToClient(SampleResult res, OutputStream out) throws IOException {
         try {
             String responseHeaders = messageResponseHeaders(res);
             out.write(responseHeaders.getBytes(SampleResult.DEFAULT_HTTP_ENCODING));
             out.write(CRLF_BYTES);
             out.write(res.getResponseData());
             out.flush();
             if (log.isDebugEnabled()) {
                 log.debug(port + "Done writing to client");
             }
         } catch (IOException e) {
             log.error("", e);
             throw e;
         } finally {
             try {
                 out.close();
             } catch (Exception ex) {
                 log.warn(port + "Error while closing socket", ex);
             }
         }
     }
 
     /**
      * In the event the content was gzipped and unpacked, the content-encoding
      * header must be removed and the content-length header should be corrected.
      *
      * The Transfer-Encoding header is also removed.
      * If the protocol was changed to HTTPS then change any Location header back to http
      * @param res - response
      *
      * @return updated headers to be sent to client
      */
     private String messageResponseHeaders(SampleResult res) {
         String headers = res.getResponseHeaders();
         String[] headerLines = headers.split(NEW_LINE, 0); // drop empty trailing content
         int contentLengthIndex = -1;
         boolean fixContentLength = false;
         for (int i = 0; i < headerLines.length; i++) {
             String line = headerLines[i];
             String[] parts = line.split(":\\s+", 2); // $NON-NLS-1$
             if (parts.length == 2) {
                 if (HTTPConstants.TRANSFER_ENCODING.equalsIgnoreCase(parts[0])) {
                     headerLines[i] = null; // We don't want this passed on to browser
                     continue;
                 }
                 if (HTTPConstants.HEADER_CONTENT_ENCODING.equalsIgnoreCase(parts[0])
                     && (HTTPConstants.ENCODING_GZIP.equalsIgnoreCase(parts[1])
                             || HTTPConstants.ENCODING_DEFLATE.equalsIgnoreCase(parts[1])
                             // TODO BROTLI not supported by HC4, so no uncompression would occur, add it once available
                             // || HTTPConstants.ENCODING_BROTLI.equalsIgnoreCase(parts[1]) 
                             )
                 ){
                     headerLines[i] = null; // We don't want this passed on to browser
                     fixContentLength = true;
                     continue;
                 }
                 if (HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(parts[0])){
                     contentLengthIndex = i;
                 }
             }
         }
         if (fixContentLength && contentLengthIndex>=0){// Fix the content length
             headerLines[contentLengthIndex] =
                     HTTPConstants.HEADER_CONTENT_LENGTH + ": " + res.getResponseData().length;
         }
         StringBuilder sb = new StringBuilder(headers.length());
         for (String line : headerLines) {
             if (line != null) {
                 sb.append(line).append(CRLF_STRING);
             }
         }
         return sb.toString();
     }
 
     /**
      * Write an error message to the client. The message should be the full HTTP
      * response.
      *
      * @param message
      *            the message to write
      */
     private void writeErrorToClient(String message) {
         try {
             OutputStream sockOut = clientSocket.getOutputStream();
             DataOutputStream out = new DataOutputStream(sockOut);
             out.writeBytes(message);
             out.flush();
         } catch (Exception e) {
             log.warn(port + "Exception while writing error", e);
         }
     }
 
     /**
      * Add the page encoding of the sample result to the Map with page encodings
      *
      * @param result the sample result to check
      * @return the page encoding found for the sample result, or null
      */
     private String addPageEncoding(SampleResult result) {
         String pageEncoding = null;
         try {
             pageEncoding = ConversionUtils.getEncodingFromContentType(result.getContentType());
         } catch(IllegalCharsetNameException ex) {
             log.warn("Unsupported charset detected in contentType:'"+result.getContentType()+"', will continue processing with default charset", ex);
         }
         if (pageEncoding != null) {
             String urlWithoutQuery = getUrlWithoutQuery(result.getURL());
             pageEncodings.put(urlWithoutQuery, pageEncoding);
         }
         return pageEncoding;
     }
 
     /**
      * Add the form encodings for all forms in the sample result
      *
      * @param result the sample result to check
      * @param pageEncoding the encoding used for the sample result page
      */
     private void addFormEncodings(SampleResult result, String pageEncoding) {
         FormCharSetFinder finder = new FormCharSetFinder();
         if (!result.getContentType().startsWith("text/")){ // TODO perhaps make more specific than this?
             return; // no point parsing anything else, e.g. GIF ...
         }
         try {
             finder.addFormActionsAndCharSet(result.getResponseDataAsString(), formEncodings, pageEncoding);
         }
         catch (HTMLParseException parseException) {
             if (log.isDebugEnabled()) {
                 log.debug(port + "Unable to parse response, could not find any form character set encodings");
             }
         }
     }
 
     private String getUrlWithoutQuery(URL url) {
         String fullUrl = url.toString();
         String urlWithoutQuery = fullUrl;
         String query = url.getQuery();
         if(query != null) {
             // Get rid of the query and the ?
             urlWithoutQuery = urlWithoutQuery.substring(0, urlWithoutQuery.length() - query.length() - 1);
         }
         return urlWithoutQuery;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
index 72ddab5ec..a355f50e0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
@@ -1,1112 +1,1112 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.InvocationTargetException;
 import java.net.MalformedURLException;
 import java.security.GeneralSecurityException;
 import java.security.KeyStore;
 import java.security.UnrecoverableKeyException;
 import java.security.cert.X509Certificate;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Date;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.prefs.Preferences;
 
 import org.apache.commons.codec.binary.Base64;
 import org.apache.commons.codec.digest.DigestUtils;
 import org.apache.commons.lang3.RandomStringUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.commons.lang3.time.DateUtils;
 import org.apache.http.conn.ssl.AbstractVerifier;
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.assertions.ResponseAssertion;
 import org.apache.jmeter.assertions.gui.AssertionGui;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.control.GenericController;
 import org.apache.jmeter.control.TransactionController;
 import org.apache.jmeter.control.gui.LogicControllerGui;
 import org.apache.jmeter.control.gui.TransactionControllerGui;
 import org.apache.jmeter.engine.util.ValueReplacer;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.functions.InvalidVariableException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.control.RecordingController;
 import org.apache.jmeter.protocol.http.gui.AuthPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jorphan.exec.KeyToolUtils;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 
 
 /**
  * Class handles storing of generated samples, etc
  * For unit tests, see TestProxyControl
  */
 public class ProxyControl extends GenericController {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ProxyControl.class);
 
     private static final long serialVersionUID = 240L;
 
     private static final String ASSERTION_GUI = AssertionGui.class.getName();
 
 
     private static final String TRANSACTION_CONTROLLER_GUI = TransactionControllerGui.class.getName();
 
     private static final String LOGIC_CONTROLLER_GUI = LogicControllerGui.class.getName();
 
     private static final String AUTH_PANEL = AuthPanel.class.getName();
 
     private static final String AUTH_MANAGER = AuthManager.class.getName();
 
     public static final int DEFAULT_PORT = 8888;
 
     // and as a string
     public static final String DEFAULT_PORT_S =
         Integer.toString(DEFAULT_PORT);// Used by GUI
 
     //+ JMX file attributes
     private static final String PORT = "ProxyControlGui.port"; // $NON-NLS-1$
 
     private static final String DOMAINS = "ProxyControlGui.domains"; // $NON-NLS-1$
 
     private static final String EXCLUDE_LIST = "ProxyControlGui.exclude_list"; // $NON-NLS-1$
 
     private static final String INCLUDE_LIST = "ProxyControlGui.include_list"; // $NON-NLS-1$
 
     private static final String CAPTURE_HTTP_HEADERS = "ProxyControlGui.capture_http_headers"; // $NON-NLS-1$
 
     private static final String ADD_ASSERTIONS = "ProxyControlGui.add_assertion"; // $NON-NLS-1$
 
     private static final String GROUPING_MODE = "ProxyControlGui.grouping_mode"; // $NON-NLS-1$
 
     private static final String SAMPLER_TYPE_NAME = "ProxyControlGui.sampler_type_name"; // $NON-NLS-1$
 
     private static final String SAMPLER_REDIRECT_AUTOMATICALLY = "ProxyControlGui.sampler_redirect_automatically"; // $NON-NLS-1$
 
     private static final String SAMPLER_FOLLOW_REDIRECTS = "ProxyControlGui.sampler_follow_redirects"; // $NON-NLS-1$
 
     private static final String USE_KEEPALIVE = "ProxyControlGui.use_keepalive"; // $NON-NLS-1$
 
     private static final String SAMPLER_DOWNLOAD_IMAGES = "ProxyControlGui.sampler_download_images"; // $NON-NLS-1$
     
     private static final String PREFIX_HTTP_SAMPLER_NAME = "ProxyControlGui.proxy_prefix_http_sampler_name"; // $NON-NLS-1$
 
     private static final String REGEX_MATCH = "ProxyControlGui.regex_match"; // $NON-NLS-1$
 
     private static final String CONTENT_TYPE_EXCLUDE = "ProxyControlGui.content_type_exclude"; // $NON-NLS-1$
 
     private static final String CONTENT_TYPE_INCLUDE = "ProxyControlGui.content_type_include"; // $NON-NLS-1$
 
     private static final String NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED = "ProxyControlGui.notify_child_sl_filtered"; // $NON-NLS-1$
 
     private static final String BASIC_AUTH = "Basic"; // $NON-NLS-1$
 
     private static final String DIGEST_AUTH = "Digest"; // $NON-NLS-1$
 
     //- JMX file attributes
 
     // Must agree with the order of entries in the drop-down
     // created in ProxyControlGui.createGroupingPanel()
     private static final int GROUPING_ADD_SEPARATORS = 1;
     private static final int GROUPING_IN_SIMPLE_CONTROLLERS = 2;
     private static final int GROUPING_STORE_FIRST_ONLY = 3;
     private static final int GROUPING_IN_TRANSACTION_CONTROLLERS = 4;
 
     // Original numeric order (we now use strings)
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_JAVA = "0";
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_HC3_1 = "1";
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_HC4 = "2";
 
     private static final long SAMPLE_GAP =
         JMeterUtils.getPropDefault("proxy.pause", 5000); // $NON-NLS-1$
     // Detect if user has pressed a new link
 
     // for ssl connection
     private static final String KEYSTORE_TYPE =
         JMeterUtils.getPropDefault("proxy.cert.type", "JKS"); // $NON-NLS-1$ $NON-NLS-2$
 
     // Proxy configuration SSL
     private static final String CERT_DIRECTORY =
         JMeterUtils.getPropDefault("proxy.cert.directory", JMeterUtils.getJMeterBinDir()); // $NON-NLS-1$
 
     private static final String CERT_FILE_DEFAULT = "proxyserver.jks";// $NON-NLS-1$
 
     private static final String CERT_FILE =
         JMeterUtils.getPropDefault("proxy.cert.file", CERT_FILE_DEFAULT); // $NON-NLS-1$
 
     private static final File CERT_PATH = new File(CERT_DIRECTORY, CERT_FILE);
 
     private static final String CERT_PATH_ABS = CERT_PATH.getAbsolutePath();
 
     private static final String DEFAULT_PASSWORD = "password"; // $NON-NLS-1$ NOSONAR only default password, if user has not defined one
 
     /**
      * Keys for user preferences
      */
     private static final String USER_PASSWORD_KEY = "proxy_cert_password"; // NOSONAR not a hardcoded password
 
     /**
      * Note: Windows user preferences are stored relative to: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
      */
     private static final Preferences PREFERENCES = Preferences.userNodeForPackage(ProxyControl.class);
 
     /**
      * Whether to use dymanic key generation (if supported)
      */
     private static final boolean USE_DYNAMIC_KEYS = JMeterUtils.getPropDefault("proxy.cert.dynamic_keys", true); // $NON-NLS-1$
 
     // The alias to be used if dynamic host names are not possible
     static final String JMETER_SERVER_ALIAS = ":jmeter:"; // $NON-NLS-1$
 
     static final int CERT_VALIDITY = JMeterUtils.getPropDefault("proxy.cert.validity", 7); // $NON-NLS-1$
 
     // If this is defined, it is assumed to be the alias of a user-supplied certificate; overrides dynamic mode
     static final String CERT_ALIAS = JMeterUtils.getProperty("proxy.cert.alias"); // $NON-NLS-1$
 
     public enum KeystoreMode {
         USER_KEYSTORE,   // user-provided keystore
         JMETER_KEYSTORE, // keystore generated by JMeter; single entry
         DYNAMIC_KEYSTORE, // keystore generated by JMeter; dynamic entries
         NONE             // cannot use keystore
     }
 
     static final KeystoreMode KEYSTORE_MODE;
 
     static {
         if (CERT_ALIAS != null) {
             KEYSTORE_MODE = KeystoreMode.USER_KEYSTORE;
             log.info("HTTP(S) Test Script Recorder will use the keystore '"+ CERT_PATH_ABS + "' with the alias: '" + CERT_ALIAS + "'");
         } else {
             if (!KeyToolUtils.haveKeytool()) {
                 KEYSTORE_MODE = KeystoreMode.NONE;
             } else if (USE_DYNAMIC_KEYS) {
                 KEYSTORE_MODE = KeystoreMode.DYNAMIC_KEYSTORE;
                 log.info("HTTP(S) Test Script Recorder SSL Proxy will use keys that support embedded 3rd party resources in file " + CERT_PATH_ABS);
             } else {
                 KEYSTORE_MODE = KeystoreMode.JMETER_KEYSTORE;
                 log.warn("HTTP(S) Test Script Recorder SSL Proxy will use keys that may not work for embedded resources in file " + CERT_PATH_ABS);
             }
         }
     }
 
     /**
      * Whether to use the redirect disabling feature (can be switched off if it does not work)
      */
     private static final boolean ATTEMPT_REDIRECT_DISABLING =
             JMeterUtils.getPropDefault("proxy.redirect.disabling", true); // $NON-NLS-1$
 
     /**
      * Although this field is mutable, it is only accessed within the synchronized method deliverSampler()
      */
     private static String LAST_REDIRECT = null;
     
     /*
      * TODO this assumes that the redirected response will always immediately follow the original response.
      * This may not always be true.
      * Is there a better way to do this?
      */
     private transient Daemon server;
 
     private long lastTime = 0;// When was the last sample seen?
 
     private transient KeyStore keyStore;
 
     private volatile boolean addAssertions = false;
 
     private volatile int groupingMode = 0;
 
     private volatile boolean samplerRedirectAutomatically = false;
 
     private volatile boolean samplerFollowRedirects = false;
 
     private volatile boolean useKeepAlive = false;
 
     private volatile boolean samplerDownloadImages = false;
 
     private volatile boolean notifyChildSamplerListenersOfFilteredSamples = true;
 
     private volatile boolean regexMatch = false;// Should we match using regexes?
     
     private Set<Class<?>> addableInterfaces = new HashSet<>(
             Arrays.asList(Visualizer.class, ConfigElement.class,
                     Assertion.class, Timer.class, PreProcessor.class,
                     PostProcessor.class));
 
     /**
      * Tree node where the samples should be stored.
      * <p>
      * This property is not persistent.
      */
     private JMeterTreeNode target;
 
     private String storePassword;
 
     private String keyPassword;
 
     private JMeterTreeModel nonGuiTreeModel;
 
     public ProxyControl() {
         setPort(DEFAULT_PORT);
         setExcludeList(new HashSet<>());
         setIncludeList(new HashSet<>());
         setCaptureHttpHeaders(true); // maintain original behaviour
     }
 
     /**
      * Set a {@link JMeterTreeModel} to be used by the ProxyControl, when used
      * in a non-GUI environment, where the {@link JMeterTreeModel} can't be
      * acquired through {@link GuiPackage#getTreeModel()}
      *
      * @param treeModel
      *            the {@link JMeterTreeModel} to be used, or {@code null} when
      *            the GUI model should be used
      */
     public void setNonGuiTreeModel(JMeterTreeModel treeModel) {
         this.nonGuiTreeModel = treeModel;
     }
 
     public void setPort(int port) {
         this.setProperty(new IntegerProperty(PORT, port));
     }
 
     public void setPort(String port) {
         setProperty(PORT, port);
     }
 
     public void setSslDomains(String domains) {
         setProperty(DOMAINS, domains, "");
     }
 
     public String getSslDomains() {
         return getPropertyAsString(DOMAINS,"");
     }
 
     public void setCaptureHttpHeaders(boolean capture) {
         setProperty(new BooleanProperty(CAPTURE_HTTP_HEADERS, capture));
     }
 
     public void setGroupingMode(int grouping) {
         this.groupingMode = grouping;
         setProperty(new IntegerProperty(GROUPING_MODE, grouping));
     }
 
     public void setAssertions(boolean b) {
         addAssertions = b;
         setProperty(new BooleanProperty(ADD_ASSERTIONS, b));
     }
 
     public void setSamplerTypeName(String samplerTypeName) {
         setProperty(new StringProperty(SAMPLER_TYPE_NAME, samplerTypeName));
     }
     public void setSamplerRedirectAutomatically(boolean b) {
         samplerRedirectAutomatically = b;
         setProperty(new BooleanProperty(SAMPLER_REDIRECT_AUTOMATICALLY, b));
     }
 
     public void setSamplerFollowRedirects(boolean b) {
         samplerFollowRedirects = b;
         setProperty(new BooleanProperty(SAMPLER_FOLLOW_REDIRECTS, b));
     }
 
     /**
      * @param b flag whether keep alive should be used
      */
     public void setUseKeepAlive(boolean b) {
         useKeepAlive = b;
         setProperty(new BooleanProperty(USE_KEEPALIVE, b));
     }
 
     public void setSamplerDownloadImages(boolean b) {
         samplerDownloadImages = b;
         setProperty(new BooleanProperty(SAMPLER_DOWNLOAD_IMAGES, b));
     }
 
     public void setPrefixHTTPSampleName(String prefixHTTPSampleName) {
         setProperty(PREFIX_HTTP_SAMPLER_NAME, prefixHTTPSampleName);
     }
 
     public void setNotifyChildSamplerListenerOfFilteredSamplers(boolean b) {
         notifyChildSamplerListenersOfFilteredSamples = b;
         setProperty(new BooleanProperty(NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED, b));
     }
 
     public void setIncludeList(Collection<String> list) {
         setProperty(new CollectionProperty(INCLUDE_LIST, new HashSet<>(list)));
     }
 
     public void setExcludeList(Collection<String> list) {
         setProperty(new CollectionProperty(EXCLUDE_LIST, new HashSet<>(list)));
     }
 
     /**
      * @param b flag whether regex matching should be used
      */
     public void setRegexMatch(boolean b) {
         regexMatch = b;
         setProperty(new BooleanProperty(REGEX_MATCH, b));
     }
 
     public void setContentTypeExclude(String contentTypeExclude) {
         setProperty(new StringProperty(CONTENT_TYPE_EXCLUDE, contentTypeExclude));
     }
 
     public void setContentTypeInclude(String contentTypeInclude) {
         setProperty(new StringProperty(CONTENT_TYPE_INCLUDE, contentTypeInclude));
     }
 
     public boolean getAssertions() {
         return getPropertyAsBoolean(ADD_ASSERTIONS);
     }
 
     public int getGroupingMode() {
         return getPropertyAsInt(GROUPING_MODE);
     }
 
     public int getPort() {
         return getPropertyAsInt(PORT);
     }
 
     public String getPortString() {
         return getPropertyAsString(PORT);
     }
 
     public int getDefaultPort() {
         return DEFAULT_PORT;
     }
 
     public boolean getCaptureHttpHeaders() {
         return getPropertyAsBoolean(CAPTURE_HTTP_HEADERS);
     }
 
     public String getSamplerTypeName() {
         // Convert the old numeric types - just in case someone wants to reload the workbench
         String type = getPropertyAsString(SAMPLER_TYPE_NAME);
         if (SAMPLER_TYPE_HTTP_SAMPLER_JAVA.equals(type)){
             type = HTTPSamplerFactory.IMPL_JAVA;
         } else if (SAMPLER_TYPE_HTTP_SAMPLER_HC3_1.equals(type)){
             type = HTTPSamplerFactory.IMPL_HTTP_CLIENT3_1;
         } else if (SAMPLER_TYPE_HTTP_SAMPLER_HC4.equals(type)){
             type = HTTPSamplerFactory.IMPL_HTTP_CLIENT4;
         }
         return type;
     }
 
     public boolean getSamplerRedirectAutomatically() {
         return getPropertyAsBoolean(SAMPLER_REDIRECT_AUTOMATICALLY, false);
     }
 
     public boolean getSamplerFollowRedirects() {
         return getPropertyAsBoolean(SAMPLER_FOLLOW_REDIRECTS, true);
     }
 
     public boolean getUseKeepalive() {
         return getPropertyAsBoolean(USE_KEEPALIVE, true);
     }
 
     public boolean getSamplerDownloadImages() {
         return getPropertyAsBoolean(SAMPLER_DOWNLOAD_IMAGES, false);
     }
 
     public String getPrefixHTTPSampleName() {
         return getPropertyAsString(PREFIX_HTTP_SAMPLER_NAME);
     }
 
     public boolean getNotifyChildSamplerListenerOfFilteredSamplers() {
         return getPropertyAsBoolean(NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED, true);
     }
 
     public boolean getRegexMatch() {
         return getPropertyAsBoolean(REGEX_MATCH, false);
     }
 
     public String getContentTypeExclude() {
         return getPropertyAsString(CONTENT_TYPE_EXCLUDE);
     }
 
     public String getContentTypeInclude() {
         return getPropertyAsString(CONTENT_TYPE_INCLUDE);
     }
 
     /**
      * @return the {@link JMeterTreeModel} used when run in non-GUI mode, or {@code null} when run in GUI mode
      */
     public JMeterTreeModel getNonGuiTreeModel() {
         return nonGuiTreeModel;
     }
 
     public void addConfigElement(ConfigElement config) {
         // NOOP
     }
 
     public void startProxy() throws IOException {
         try {
             initKeyStore();
         } catch (GeneralSecurityException e) {
             log.error("Could not initialise key store", e);
             throw new IOException("Could not create keystore", e);
         } catch (IOException e) { // make sure we log the error
             log.error("Could not initialise key store", e);
             throw e;
         }
         notifyTestListenersOfStart();
         try {
             server = new Daemon(getPort(), this);
             server.start();
             if (GuiPackage.getInstance() != null) {
                 GuiPackage.getInstance().register(server);
             }
         } catch (IOException e) {
             log.error("Could not create Proxy daemon", e);
             throw e;
         }
     }
 
     public void addExcludedPattern(String pattern) {
         getExcludePatterns().addItem(pattern);
     }
 
     public CollectionProperty getExcludePatterns() {
         return (CollectionProperty) getProperty(EXCLUDE_LIST);
     }
 
     public void addIncludedPattern(String pattern) {
         getIncludePatterns().addItem(pattern);
     }
 
     public CollectionProperty getIncludePatterns() {
         return (CollectionProperty) getProperty(INCLUDE_LIST);
     }
 
     public void clearExcludedPatterns() {
         getExcludePatterns().clear();
     }
 
     public void clearIncludedPatterns() {
         getIncludePatterns().clear();
     }
 
     /**
      * @return the target controller node
      */
     public JMeterTreeNode getTarget() {
         return target;
     }
 
     /**
      * Sets the target node where the samples generated by the proxy have to be
      * stored.
      * 
      * @param target target node to store generated samples
      */
     public void setTarget(JMeterTreeNode target) {
         this.target = target;
     }
 
     /**
      * Receives the recorded sampler from the proxy server for placing in the
      * test tree; this is skipped if the sampler is null (e.g. for recording SSL errors)
      * Always sends the result to any registered sample listeners.
      *
      * @param sampler the sampler, may be null
      * @param testElements the test elements to be added (e.g. header namager) under the Sampler
      * @param result the sample result, not null
      * TODO param serverResponse to be added to allow saving of the
      * server's response while recording.
      */
     public synchronized void deliverSampler(final HTTPSamplerBase sampler, final TestElement[] testElements, final SampleResult result) {
         boolean notifySampleListeners = true;
         if (sampler != null) {
             if (ATTEMPT_REDIRECT_DISABLING && (samplerRedirectAutomatically || samplerFollowRedirects)
                     && result instanceof HTTPSampleResult) {
                 final HTTPSampleResult httpSampleResult = (HTTPSampleResult) result;
                 final String urlAsString = httpSampleResult.getUrlAsString();
                 if (urlAsString.equals(LAST_REDIRECT)) { // the url matches the last redirect
                     sampler.setEnabled(false);
                     sampler.setComment("Detected a redirect from the previous sample");
                 } else { // this is not the result of a redirect
                     LAST_REDIRECT = null; // so break the chain
                 }
                 if (httpSampleResult.isRedirect()) { // Save Location so resulting sample can be disabled
                     if (LAST_REDIRECT == null) {
                         sampler.setComment("Detected the start of a redirect chain");
                     }
                     LAST_REDIRECT = httpSampleResult.getRedirectLocation();
                 } else {
                     LAST_REDIRECT = null;
                 }
             }
             if (filterContentType(result) && filterUrl(sampler)) {
                 JMeterTreeNode myTarget = findTargetControllerNode();
                 @SuppressWarnings("unchecked") // OK, because find only returns correct element types
                 Collection<ConfigTestElement> defaultConfigurations = (Collection<ConfigTestElement>) findApplicableElements(myTarget, ConfigTestElement.class, false);
                 @SuppressWarnings("unchecked") // OK, because find only returns correct element types
                 Collection<Arguments> userDefinedVariables = (Collection<Arguments>) findApplicableElements(myTarget, Arguments.class, true);
 
                 removeValuesFromSampler(sampler, defaultConfigurations);
                 replaceValues(sampler, testElements, userDefinedVariables);
                 sampler.setAutoRedirects(samplerRedirectAutomatically);
                 sampler.setFollowRedirects(samplerFollowRedirects);
                 sampler.setUseKeepAlive(useKeepAlive);
                 sampler.setImageParser(samplerDownloadImages);
                 String prefix = getPrefixHTTPSampleName();
                 if(!StringUtils.isEmpty(prefix)) {
                     sampler.setName(prefix + sampler.getName());
                     result.setSampleLabel(prefix + result.getSampleLabel());
                 }
                 Authorization authorization = createAuthorization(testElements, sampler);
                 if (authorization != null) {
                     setAuthorization(authorization, myTarget);
                 }
                 placeSampler(sampler, testElements, myTarget);
             } else {
                 if(log.isDebugEnabled()) {
                     log.debug("Sample excluded based on url or content-type: " + result.getUrlAsString() + " - " + result.getContentType());
                 }
                 notifySampleListeners = notifyChildSamplerListenersOfFilteredSamples;
                 result.setSampleLabel("["+result.getSampleLabel()+"]");
             }
         }
         if(notifySampleListeners) {
             // SampleEvent is not passed JMeterVariables, because they don't make sense for Proxy Recording
             notifySampleListeners(new SampleEvent(result, "WorkBench"));
         } else {
             log.debug("Sample not delivered to Child Sampler Listener based on url or content-type: " + result.getUrlAsString() + " - " + result.getContentType());
         }
     }
 
     /**
      * Detect Header manager in subConfigs,
      * Find(if any) Authorization header
      * Construct Authentication object
      * Removes Authorization if present 
      *
      * @param subConfigs {@link TestElement}[]
      * @param sampler {@link HTTPSamplerBase}
      * @return {@link Authorization}
      */
     private Authorization createAuthorization(final TestElement[] testElements, HTTPSamplerBase sampler) {
         Header authHeader;
         Authorization authorization = null;
         // Iterate over subconfig elements searching for HeaderManager
         for (TestElement te : testElements) {
             if (te instanceof HeaderManager) {
                 @SuppressWarnings("unchecked") // headers should only contain the correct classes
                 List<TestElementProperty> headers = (ArrayList<TestElementProperty>) ((HeaderManager) te).getHeaders().getObjectValue();
                 for (Iterator<?> iterator = headers.iterator(); iterator.hasNext();) {
                     TestElementProperty tep = (TestElementProperty) iterator
                             .next();
                     if (tep.getName().equals(HTTPConstants.HEADER_AUTHORIZATION)) {
                         //Construct Authorization object from HEADER_AUTHORIZATION
                         authHeader = (Header) tep.getObjectValue();
                         String[] authHeaderContent = authHeader.getValue().split(" ");//$NON-NLS-1$
                         String authType;
                         String authCredentialsBase64;
                         if(authHeaderContent.length>=2) {
                             authType = authHeaderContent[0];
                             authCredentialsBase64 = authHeaderContent[1];
                             authorization=new Authorization();
                             try {
                                 authorization.setURL(sampler.getUrl().toExternalForm());
                             } catch (MalformedURLException e) {
                                 log.error("Error filling url on authorization, message:"+e.getMessage(), e);
                                 authorization.setURL("${AUTH_BASE_URL}");//$NON-NLS-1$
                             }
                             // if HEADER_AUTHORIZATION contains "Basic"
                             // then set Mechanism.BASIC_DIGEST, otherwise Mechanism.KERBEROS
                             authorization.setMechanism(
                                     authType.equals(BASIC_AUTH)||authType.equals(DIGEST_AUTH)?
                                     AuthManager.Mechanism.BASIC_DIGEST:
                                     AuthManager.Mechanism.KERBEROS);
                             if(BASIC_AUTH.equals(authType)) {
                                 String authCred= new String(Base64.decodeBase64(authCredentialsBase64));
                                 String[] loginPassword = authCred.split(":"); //$NON-NLS-1$
                                 authorization.setUser(loginPassword[0]);
                                 authorization.setPass(loginPassword[1]);
                             } else {
                                 // Digest or Kerberos
                                 authorization.setUser("${AUTH_LOGIN}");//$NON-NLS-1$
                                 authorization.setPass("${AUTH_PASSWORD}");//$NON-NLS-1$
                                 
                             }
                         }
                         // remove HEADER_AUTHORIZATION from HeaderManager 
                         // because it's useless after creating Authorization object
                         iterator.remove();
                     }
                 }
             }
         }
         return authorization;
     }
 
     public void stopProxy() {
         if (server != null) {
             server.stopServer();
             if (GuiPackage.getInstance() != null) {
                 GuiPackage.getInstance().unregister(server);
             }
             try {
                 server.join(1000); // wait for server to stop
             } catch (InterruptedException e) {
                 //NOOP
                 Thread.currentThread().interrupt();
             }
             notifyTestListenersOfEnd();
             server = null;
         }
     }
 
     public String[] getCertificateDetails() {
         if (isDynamicMode()) {
             try {
                 X509Certificate caCert = (X509Certificate) keyStore.getCertificate(KeyToolUtils.getRootCAalias());
                 if (caCert == null) {
                     return new String[]{"Could not find certificate"};
                 }
                 return new String[]
                         {
                         caCert.getSubjectX500Principal().toString(),
                         "Fingerprint(SHA1): " + JOrphanUtils.baToHexString(DigestUtils.sha1(caCert.getEncoded()), ' '),
                         "Created: "+ caCert.getNotBefore().toString()
                         };
             } catch (GeneralSecurityException e) {
                 log.error("Problem reading root CA from keystore", e);
                 return new String[]{"Problem with root certificate", e.getMessage()};
             }
         }
         return new String[0]; // should not happen
     }
     // Package protected to allow test case access
     boolean filterUrl(HTTPSamplerBase sampler) {
         String domain = sampler.getDomain();
         if (domain == null || domain.length() == 0) {
             return false;
         }
 
         String url = generateMatchUrl(sampler);
         CollectionProperty includePatterns = getIncludePatterns();
         if (includePatterns.size() > 0 && !matchesPatterns(url, includePatterns)) {
             return false;
         }
 
         CollectionProperty excludePatterns = getExcludePatterns();
         if (excludePatterns.size() > 0 && matchesPatterns(url, excludePatterns)) {
             return false;
         }
 
         return true;
     }
 
     // Package protected to allow test case access
     /**
      * Filter the response based on the content type.
      * If no include nor exclude filter is specified, the result will be included
      *
      * @param result the sample result to check
      * @return <code>true</code> means result will be kept
      */
     boolean filterContentType(SampleResult result) {
         String includeExp = getContentTypeInclude();
         String excludeExp = getContentTypeExclude();
         // If no expressions are specified, we let the sample pass
         if((includeExp == null || includeExp.length() == 0) &&
                 (excludeExp == null || excludeExp.length() == 0)
                 )
         {
             return true;
         }
 
         // Check that we have a content type
         String sampleContentType = result.getContentType();
         if(sampleContentType == null || sampleContentType.length() == 0) {
             if(log.isDebugEnabled()) {
                 log.debug("No Content-type found for : " + result.getUrlAsString());
             }
 
             return true;
         }
 
         if(log.isDebugEnabled()) {
             log.debug("Content-type to filter : " + sampleContentType);
         }
 
         // Check if the include pattern is matched
         boolean matched = testPattern(includeExp, sampleContentType, true);
         if(!matched) {
             return false;
         }
 
         // Check if the exclude pattern is matched
         matched = testPattern(excludeExp, sampleContentType, false);
         if(!matched) {
             return false;
         }
 
         return true;
     }
 
     /**
      * Returns true if matching pattern was different from expectedToMatch
      * @param expression Expression to match
      * @param sampleContentType
      * @return boolean true if Matching expression
      */
     private boolean testPattern(String expression, String sampleContentType, boolean expectedToMatch) {
         if(expression != null && expression.length() > 0) {
             if(log.isDebugEnabled()) {
                 log.debug("Testing Expression : " + expression + " on sampleContentType:"+sampleContentType+", expected to match:"+expectedToMatch);
             }
 
             Pattern pattern = null;
             try {
                 pattern = JMeterUtils.getPatternCache().getPattern(expression, Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
                 if(JMeterUtils.getMatcher().contains(sampleContentType, pattern) != expectedToMatch) {
                     return false;
                 }
             } catch (MalformedCachePatternException e) {
                 log.warn("Skipped invalid content pattern: " + expression, e);
             }
         }
         return true;
     }
 
     /**
      * Find if there is any AuthManager in JMeterTreeModel
      * If there is no one, create and add it to tree
      * Add authorization object to AuthManager
      * @param authorization {@link Authorization}
      * @param target {@link JMeterTreeNode}
      */
     private void setAuthorization(Authorization authorization, JMeterTreeNode target) {
         JMeterTreeModel jmeterTreeModel = getJmeterTreeModel();
         List<JMeterTreeNode> authManagerNodes = jmeterTreeModel.getNodesOfType(AuthManager.class);
         if (authManagerNodes.isEmpty()) {
             try {
                 log.debug("Creating HTTP Authentication manager for authorization:"+authorization);
                 AuthManager authManager = newAuthorizationManager(authorization);
                 jmeterTreeModel.addComponent(authManager, target);
             } catch (IllegalUserActionException e) {
                 log.error("Failed to add Authorization Manager to target node:" + target.getName(), e);
             }
         } else{
             AuthManager authManager=(AuthManager)authManagerNodes.get(0).getTestElement();
             authManager.addAuth(authorization);
         }
     }
 
     private JMeterTreeModel getJmeterTreeModel() {
         if (this.nonGuiTreeModel == null) {
             return GuiPackage.getInstance().getTreeModel();
         }
         return this.nonGuiTreeModel;
     }
 
     /**
      * Helper method to add a Response Assertion
      * Called from AWT Event thread
      */
     private void addAssertion(JMeterTreeModel model, JMeterTreeNode node) throws IllegalUserActionException {
         ResponseAssertion ra = new ResponseAssertion();
         ra.setProperty(TestElement.GUI_CLASS, ASSERTION_GUI);
         ra.setName(JMeterUtils.getResString("assertion_title")); // $NON-NLS-1$
         ra.setTestFieldResponseData();
         model.addComponent(ra, node);
     }
 
     /**
      * Construct AuthManager
      * @param authorization
      * @return AuthManager
      * @throws IllegalUserActionException
      */
     private AuthManager newAuthorizationManager(Authorization authorization) throws IllegalUserActionException {
         AuthManager authManager = new AuthManager();
         authManager.setProperty(TestElement.GUI_CLASS, AUTH_PANEL);
         authManager.setProperty(TestElement.TEST_CLASS, AUTH_MANAGER);
         authManager.setName("HTTP Authorization Manager");
         authManager.addAuth(authorization);
         return authManager;
     }
 
     /**
      * Helper method to add a Divider
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      */
     private void addDivider(final JMeterTreeModel model, final JMeterTreeNode node) {
         final GenericController sc = new GenericController();
         sc.setProperty(TestElement.GUI_CLASS, LOGIC_CONTROLLER_GUI);
         sc.setName("-------------------"); // $NON-NLS-1$
         safelyAddComponent(model, node, sc);
     }
 
     private void safelyAddComponent(
             final JMeterTreeModel model,
             final JMeterTreeNode node,
             final GenericController controller) {
         JMeterUtils.runSafe(true, () -> {
             try {
                 model.addComponent(controller, node);
             } catch (IllegalUserActionException e) {
                 log.error("Program error", e);
                 throw new Error(e);
             }
         });
     }
 
     /**
      * Helper method to add a Simple Controller to contain the samplers.
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      * @param model
      *            Test component tree model
      * @param node
      *            Node in the tree where we will add the Controller
      * @param name
      *            A name for the Controller
      * @throws InvocationTargetException
      * @throws InterruptedException
      */
     private void addSimpleController(final JMeterTreeModel model, final JMeterTreeNode node, String name)
             throws InterruptedException, InvocationTargetException {
         final GenericController sc = new GenericController();
         sc.setProperty(TestElement.GUI_CLASS, LOGIC_CONTROLLER_GUI);
         sc.setName(name);
         safelyAddComponent(model, node, sc);
     }
 
     /**
      * Helper method to add a Transaction Controller to contain the samplers.
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      * @param model
      *            Test component tree model
      * @param node
      *            Node in the tree where we will add the Controller
      * @param name
      *            A name for the Controller
      * @throws InvocationTargetException
      * @throws InterruptedException
      */
     private void addTransactionController(final JMeterTreeModel model, final JMeterTreeNode node, String name)
             throws InterruptedException, InvocationTargetException {
         final TransactionController sc = new TransactionController();
         sc.setIncludeTimers(false);
         sc.setProperty(TestElement.GUI_CLASS, TRANSACTION_CONTROLLER_GUI);
         sc.setName(name);
         safelyAddComponent(model, node, sc);
     }
     /**
      * Helper method to replicate any timers found within the Proxy Controller
      * into the provided sampler, while replacing any occurrences of string _T_
      * in the timer's configuration with the provided deltaT.
      * Called from AWT Event thread
      * @param model
      *            Test component tree model
      * @param node
      *            Sampler node in where we will add the timers
      * @param deltaT
      *            Time interval from the previous request
      */
     private void addTimers(JMeterTreeModel model, JMeterTreeNode node, long deltaT) {
         TestPlan variables = new TestPlan();
         variables.addParameter("T", Long.toString(deltaT)); // $NON-NLS-1$
         ValueReplacer replacer = new ValueReplacer(variables);
         JMeterTreeNode mySelf = model.getNodeOf(this);
         Enumeration<JMeterTreeNode> children = mySelf.children();
         while (children.hasMoreElements()) {
             JMeterTreeNode templateNode = children.nextElement();
             if (templateNode.isEnabled()) {
                 TestElement template = templateNode.getTestElement();
                 if (template instanceof Timer) {
                     TestElement timer = (TestElement) template.clone();
                     try {
                         timer.setComment("Recorded:"+Long.toString(deltaT)+"ms");
                         replacer.undoReverseReplace(timer);
                         model.addComponent(timer, node);
                     } catch (InvalidVariableException
                             | IllegalUserActionException e) {
                         // Not 100% sure, but I believe this can't happen, so
                         // I'll log and throw an error:
                         log.error("Program error", e);
                         throw new Error(e);
                     }
                 }
             }
         }
     }
 
     /**
      * Finds the first enabled node of a given type in the tree.
      *
      * @param type
      *            class of the node to be found
      *
      * @return the first node of the given type in the test component tree, or
      *         <code>null</code> if none was found.
      */
     private JMeterTreeNode findFirstNodeOfType(Class<?> type) {
         JMeterTreeModel treeModel = getJmeterTreeModel();
         List<JMeterTreeNode> nodes = treeModel.getNodesOfType(type);
         for (JMeterTreeNode node : nodes) {
             if (node.isEnabled()) {
                 return node;
             }
         }
         return null;
     }
 
     /**
      * Finds the controller where samplers have to be stored, that is:
      * <ul>
      * <li>The controller specified by the <code>target</code> property.
      * <li>If none was specified, the first RecordingController in the tree.
      * <li>If none is found, the first AbstractThreadGroup in the tree.
      * <li>If none is found, the Workspace.
      * </ul>
      *
      * @return the tree node for the controller where the proxy must store the
      *         generated samplers.
      */
     public JMeterTreeNode findTargetControllerNode() {
         JMeterTreeNode myTarget = getTarget();
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(RecordingController.class);
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(AbstractThreadGroup.class);
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(WorkBench.class);
         if (myTarget != null) {
             return myTarget;
         }
         log.error("Program error: test script recording target not found.");
         return null;
     }
 
     /**
      * Finds all configuration objects of the given class applicable to the
      * recorded samplers, that is:
      * <ul>
      * <li>All such elements directly within the HTTP(S) Test Script Recorder (these have
      * the highest priority).
      * <li>All such elements directly within the target controller (higher
      * priority) or directly within any containing controller (lower priority),
      * including the Test Plan itself (lowest priority).
      * </ul>
      *
      * @param myTarget
      *            tree node for the recording target controller.
      * @param myClass
      *            Class of the elements to be found.
      * @param ascending
      *            true if returned elements should be ordered in ascending
      *            priority, false if they should be in descending priority.
      *
      * @return a collection of applicable objects of the given class.
      */
     // TODO - could be converted to generic class?
     private Collection<?> findApplicableElements(JMeterTreeNode myTarget, Class<? extends TestElement> myClass, boolean ascending) {
         JMeterTreeModel treeModel = getJmeterTreeModel();
         LinkedList<TestElement> elements = new LinkedList<>();
 
         // Look for elements directly within the HTTP proxy:
         Enumeration<?> kids = treeModel.getNodeOf(this).children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement element = (TestElement) subNode.getUserObject();
                 if (myClass.isInstance(element)) {
                     if (ascending) {
                         elements.addFirst(element);
                     } else {
                         elements.add(element);
                     }
                 }
             }
         }
 
         // Look for arguments elements in the target controller or higher up:
         for (JMeterTreeNode controller = myTarget; controller != null; controller = (JMeterTreeNode) controller
                 .getParent()) {
             kids = controller.children();
             while (kids.hasMoreElements()) {
                 JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
                 if (subNode.isEnabled()) {
                     TestElement element = (TestElement) subNode.getUserObject();
                     if (myClass.isInstance(element)) {
                         log.debug("Applicable: " + element.getName());
                         if (ascending) {
                             elements.addFirst(element);
                         } else {
                             elements.add(element);
                         }
                     }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreatorFactory.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/SamplerCreatorFactory.java
index 2f1915603..34ef8cd66 100644
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
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.reflect.ClassFinder;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 /**
  * {@link SamplerCreator} factory
  */
 public class SamplerCreatorFactory {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SamplerCreatorFactory.class);
 
     private static final SamplerCreator DEFAULT_SAMPLER_CREATOR = new DefaultSamplerCreator();
 
     private final Map<String, SamplerCreator> samplerCreatorMap = new HashMap<>();
 
     /**
      * 
      */
     public SamplerCreatorFactory() {
         init();
     }
     
     /**
      * Initialize factory from classpath
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
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
      * @param request {@link HttpRequestHdr} from which the content type should be used
      * @param pageEncodings Map of pageEncodings
      * @param formEncodings  Map of formEncodings
      * @return SamplerCreator for the content type of the <code>request</code>, or {@link DefaultSamplerCreator} when none is found
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/gui/ProxyControlGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/gui/ProxyControlGui.java
index fa225e998..19ae940f8 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/gui/ProxyControlGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/gui/ProxyControlGui.java
@@ -1,1004 +1,1004 @@
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
 
 package org.apache.jmeter.protocol.http.proxy.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.Cursor;
 import java.awt.Dimension;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 import java.awt.event.KeyEvent;
 import java.awt.event.KeyListener;
 import java.io.IOException;
 import java.net.BindException;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.JScrollBar;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.JTextField;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.gui.LogicControllerGui;
 import org.apache.jmeter.control.gui.TreeNodeWrapper;
 import org.apache.jmeter.engine.util.ValueReplacer;
 import org.apache.jmeter.functions.InvalidVariableException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.UnsharedComponent;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.gui.util.PowerTableModel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.control.RecordingController;
 import org.apache.jmeter.protocol.http.proxy.ProxyControl;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.exec.KeyToolUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.LoggerFactory;
+import org.slf4j.Logger;
 
 public class ProxyControlGui extends LogicControllerGui implements JMeterGUIComponent, ActionListener, ItemListener,
         KeyListener, UnsharedComponent {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ProxyControlGui.class);
 
     private static final long serialVersionUID = 232L;
 
     private static final String NEW_LINE = "\n";  // $NON-NLS-1$
 
     private static final String SPACE = " ";  // $NON-NLS-1$
 
     /**
      * This choice means don't explicitly set Implementation and rely on default, see Bug 54154
      */
     private static final String USE_DEFAULT_HTTP_IMPL = ""; // $NON-NLS-1$
 
     private static final String SUGGESTED_EXCLUSIONS =
             JMeterUtils.getPropDefault("proxy.excludes.suggested", "(?i).*\\.(bmp|css|js|gif|ico|jpe?g|png|swf|woff)"); // $NON-NLS-1$
 
     private JTextField portField;
 
     private JLabeledTextField sslDomains;
 
     /**
      * Used to indicate that HTTP request headers should be captured. The
      * default is to capture the HTTP request headers, which are specific to
      * particular browser settings.
      */
     private JCheckBox httpHeaders;
 
     /**
      * Whether to group requests together based on inactivity separation periods --
      * and how to handle such grouping afterwards.
      */
     private JComboBox<String> groupingMode;
 
     /**
      * Add an Assertion to the first sample of each set
      */
     private JCheckBox addAssertions;
 
     /**
      * Set/clear the Use Keep-Alive box on the samplers (default is true)
      */
     private JCheckBox useKeepAlive;
 
     /*
      * Use regexes to match the source data
      */
     private JCheckBox regexMatch;
 
     /**
      * The list of sampler type names to choose from
      */
     private JComboBox<String> samplerTypeName;
 
     /**
      * Set/clear the Redirect automatically box on the samplers (default is false)
      */
     private JCheckBox samplerRedirectAutomatically;
 
     /**
      * Set/clear the Follow-redirects box on the samplers (default is true)
      */
     private JCheckBox samplerFollowRedirects;
 
     /**
      * Set/clear the Download images box on the samplers (default is false)
      */
     private JCheckBox samplerDownloadImages;
 
     /**
      * Add a prefix to HTTP sample name recorded
      */
     private JTextField prefixHTTPSampleName;
 
     /**
      * Regular expression to include results based on content type
      */
     private JTextField contentTypeInclude;
 
     /**
      * Regular expression to exclude results based on content type
      */
     private JTextField contentTypeExclude;
 
     /**
      * List of available target controllers
      */
     private JComboBox<Object> targetNodes;
     
     /**
      * Notify child Listener of Filtered Samplers
      */
     private JCheckBox notifyChildSamplerListenerOfFilteredSamplersCB;
 
     private DefaultComboBoxModel<Object> targetNodesModel;
 
     private ProxyControl model;
 
     private JTable excludeTable;
 
     private PowerTableModel excludeModel;
 
     private JTable includeTable;
 
     private PowerTableModel includeModel;
 
     private static final String CHANGE_TARGET = "change_target"; // $NON-NLS-1$
 
     private JButton stop;
     private JButton start;
     private JButton restart;
 
     //+ action names
     private static final String STOP = "stop"; // $NON-NLS-1$
 
     private static final String START = "start"; // $NON-NLS-1$
 
     private static final String RESTART = "restart"; // $NON-NLS-1$
 
     // This is applied to fields that should cause a restart when changed
     private static final String ENABLE_RESTART = "enable_restart"; // $NON-NLS-1$
 
     private static final String ADD_INCLUDE = "add_include"; // $NON-NLS-1$
 
     private static final String ADD_EXCLUDE = "add_exclude"; // $NON-NLS-1$
 
     private static final String DELETE_INCLUDE = "delete_include"; // $NON-NLS-1$
 
     private static final String DELETE_EXCLUDE = "delete_exclude"; // $NON-NLS-1$
 
     private static final String ADD_TO_INCLUDE_FROM_CLIPBOARD = "include_clipboard"; // $NON-NLS-1$
 
     private static final String ADD_TO_EXCLUDE_FROM_CLIPBOARD = "exclude_clipboard"; // $NON-NLS-1$
 
     private static final String ADD_SUGGESTED_EXCLUDES = "exclude_suggested";
 
     private static final String PREFIX_HTTP_SAMPLER_NAME = "proxy_prefix_http_sampler_name"; // $NON-NLS-1$
     //- action names
 
     // Resource names for column headers
     private static final String INCLUDE_COL = "patterns_to_include"; // $NON-NLS-1$
 
     private static final String EXCLUDE_COL = "patterns_to_exclude"; // $NON-NLS-1$
 
     // Used by itemListener
     private static final String PORTFIELD = "portField"; // $NON-NLS-1$
 
     public ProxyControlGui() {
         super();
         log.debug("Creating ProxyControlGui");
         init();
     }
 
     /** {@inheritDoc} */
     @Override
     public TestElement createTestElement() {
         model = makeProxyControl();
         log.debug("creating/configuring model = " + model);
         modifyTestElement(model);
         return model;
     }
 
     protected ProxyControl makeProxyControl() {
         ProxyControl local = new ProxyControl();
         return local;
     }
 
     /** {@inheritDoc} */
     @Override
     public void modifyTestElement(TestElement el) {
         GuiUtils.stopTableEditing(excludeTable);
         GuiUtils.stopTableEditing(includeTable);
         configureTestElement(el);
         if (el instanceof ProxyControl) {
             model = (ProxyControl) el;
             model.setPort(portField.getText());
             model.setSslDomains(sslDomains.getText());
             setIncludeListInProxyControl(model);
             setExcludeListInProxyControl(model);
             model.setCaptureHttpHeaders(httpHeaders.isSelected());
             model.setGroupingMode(groupingMode.getSelectedIndex());
             model.setAssertions(addAssertions.isSelected());
             if(samplerTypeName.getSelectedIndex()< HTTPSamplerFactory.getImplementations().length) {
                 model.setSamplerTypeName(HTTPSamplerFactory.getImplementations()[samplerTypeName.getSelectedIndex()]);
             } else {
                 model.setSamplerTypeName(USE_DEFAULT_HTTP_IMPL);
             }
             model.setSamplerRedirectAutomatically(samplerRedirectAutomatically.isSelected());
             model.setSamplerFollowRedirects(samplerFollowRedirects.isSelected());
             model.setUseKeepAlive(useKeepAlive.isSelected());
             model.setSamplerDownloadImages(samplerDownloadImages.isSelected());
             model.setPrefixHTTPSampleName(prefixHTTPSampleName.getText());
             model.setNotifyChildSamplerListenerOfFilteredSamplers(notifyChildSamplerListenerOfFilteredSamplersCB.isSelected());
             model.setRegexMatch(regexMatch.isSelected());
             model.setContentTypeInclude(contentTypeInclude.getText());
             model.setContentTypeExclude(contentTypeExclude.getText());
             TreeNodeWrapper nw = (TreeNodeWrapper) targetNodes.getSelectedItem();
             if (nw == null) {
                 model.setTarget(null);
             } else {
                 model.setTarget(nw.getTreeNode());
             }
         }
     }
 
     protected void setIncludeListInProxyControl(ProxyControl element) {
         List<String> includeList = getDataList(includeModel, INCLUDE_COL);
         element.setIncludeList(includeList);
     }
 
     protected void setExcludeListInProxyControl(ProxyControl element) {
         List<String> excludeList = getDataList(excludeModel, EXCLUDE_COL);
         element.setExcludeList(excludeList);
     }
 
     private List<String> getDataList(PowerTableModel pModel, String colName) {
         String[] dataArray = pModel.getData().getColumn(colName);
         List<String> list = new LinkedList<>();
         for (String data : dataArray) {
             list.add(data);
         }
         return list;
     }
 
     /** {@inheritDoc} */
     @Override
     public String getLabelResource() {
         return "proxy_title"; // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public Collection<String> getMenuCategories() {
         return Arrays.asList(MenuFactory.NON_TEST_ELEMENTS);
     }
 
     /** {@inheritDoc} */
     @Override
     public void configure(TestElement element) {
         log.debug("Configuring gui with " + element);
         super.configure(element);
         model = (ProxyControl) element;
         portField.setText(model.getPortString());
         sslDomains.setText(model.getSslDomains());
         httpHeaders.setSelected(model.getCaptureHttpHeaders());
         groupingMode.setSelectedIndex(model.getGroupingMode());
         addAssertions.setSelected(model.getAssertions());
         samplerTypeName.setSelectedItem(model.getSamplerTypeName());
         samplerRedirectAutomatically.setSelected(model.getSamplerRedirectAutomatically());
         samplerFollowRedirects.setSelected(model.getSamplerFollowRedirects());
         useKeepAlive.setSelected(model.getUseKeepalive());
         samplerDownloadImages.setSelected(model.getSamplerDownloadImages());
         prefixHTTPSampleName.setText(model.getPrefixHTTPSampleName());
         notifyChildSamplerListenerOfFilteredSamplersCB.setSelected(model.getNotifyChildSamplerListenerOfFilteredSamplers());
         regexMatch.setSelected(model.getRegexMatch());
         contentTypeInclude.setText(model.getContentTypeInclude());
         contentTypeExclude.setText(model.getContentTypeExclude());
 
         reinitializeTargetCombo();// Set up list of potential targets and
                                     // enable listener
 
         populateTable(includeModel, model.getIncludePatterns().iterator());
         populateTable(excludeModel, model.getExcludePatterns().iterator());
         repaint();
     }
 
     private void populateTable(PowerTableModel pModel, PropertyIterator iter) {
         pModel.clearData();
         while (iter.hasNext()) {
             pModel.addRow(new Object[] { iter.next().getStringValue() });
         }
         pModel.fireTableDataChanged();
     }
 
     /*
      * Handles groupingMode. actionPerfomed is not suitable, as that seems to be
      * activated whenever the Proxy is selected in the Test Plan
      * Also handles samplerTypeName
      */
     /** {@inheritDoc} */
     @Override
     public void itemStateChanged(ItemEvent e) {
         // System.err.println(e.paramString());
         enableRestart();
     }
 
     /** {@inheritDoc} */
     @Override
     public void actionPerformed(ActionEvent action) {
         String command = action.getActionCommand();
 
         // Prevent both redirect types from being selected
         final Object source = action.getSource();
         if (source.equals(samplerFollowRedirects) && samplerFollowRedirects.isSelected()) {
             samplerRedirectAutomatically.setSelected(false);
         } else if (source.equals(samplerRedirectAutomatically) && samplerRedirectAutomatically.isSelected()) {
             samplerFollowRedirects.setSelected(false);
         }
 
         // System.err.println(action.paramString()+" "+command+ "
         // "+action.getModifiers());
 
         if (command.equals(STOP)) {
             model.stopProxy();
             stop.setEnabled(false);
             start.setEnabled(true);
             restart.setEnabled(false);
         } else if (command.equals(START)) {
             startProxy();
         } else if (command.equals(RESTART)) {
             model.stopProxy();
             startProxy();
         } else if (command.equals(ENABLE_RESTART)){
             enableRestart();
         } else if (command.equals(ADD_EXCLUDE)) {
             excludeModel.addNewRow();
             excludeModel.fireTableDataChanged();
             enableRestart();
         } else if (command.equals(ADD_INCLUDE)) {
             includeModel.addNewRow();
             includeModel.fireTableDataChanged();
             enableRestart();
         } else if (command.equals(DELETE_EXCLUDE)) {
             excludeModel.removeRow(excludeTable.getSelectedRow());
             excludeModel.fireTableDataChanged();
             enableRestart();
         } else if (command.equals(DELETE_INCLUDE)) {
             includeModel.removeRow(includeTable.getSelectedRow());
             includeModel.fireTableDataChanged();
             enableRestart();
         } else if (command.equals(CHANGE_TARGET)) {
             log.debug("Change target " + targetNodes.getSelectedItem());
             log.debug("In model " + model);
             TreeNodeWrapper nw = (TreeNodeWrapper) targetNodes.getSelectedItem();
             model.setTarget(nw.getTreeNode());
             enableRestart();
         } else if (command.equals(ADD_TO_INCLUDE_FROM_CLIPBOARD)) {
             addFromClipboard(includeTable);
             includeModel.fireTableDataChanged();
             enableRestart();
         } else if (command.equals(ADD_TO_EXCLUDE_FROM_CLIPBOARD)) {
             addFromClipboard(excludeTable);
             excludeModel.fireTableDataChanged();
             enableRestart();
         } else if (command.equals(ADD_SUGGESTED_EXCLUDES)) {
             addSuggestedExcludes(excludeTable);
             excludeModel.fireTableDataChanged();
             enableRestart();
         }
     }
 
     /**
      * Add suggested excludes to exclude table
      * @param table {@link JTable}
      */
     protected void addSuggestedExcludes(JTable table) {
         GuiUtils.stopTableEditing(table);
         int rowCount = table.getRowCount();
         PowerTableModel model = null;
         String[] exclusions = SUGGESTED_EXCLUSIONS.split(";"); // $NON-NLS-1$
         if (exclusions.length>0) {
             model = (PowerTableModel) table.getModel();
             if(model != null) {
                 for (String clipboardLine : exclusions) {
                     model.addRow(new Object[] {clipboardLine});
                 }
                 if (table.getRowCount() > rowCount) {
                     // Highlight (select) the appropriate rows.
                     int rowToSelect = model.getRowCount() - 1;
                     table.setRowSelectionInterval(rowCount, rowToSelect);
                 }
             }
         }
     }
 
     /**
      * Add values from the clipboard to table
      * @param table {@link JTable}
      */
     protected void addFromClipboard(JTable table) {
         GuiUtils.stopTableEditing(table);
         int rowCount = table.getRowCount();
         PowerTableModel model = null;
         try {
             String clipboardContent = GuiUtils.getPastedText();
             if (clipboardContent != null) {
                 String[] clipboardLines = clipboardContent.split(NEW_LINE);
                 for (String clipboardLine : clipboardLines) {
                     model = (PowerTableModel) table.getModel();
                     model.addRow(new Object[] {clipboardLine});
                 }
                 if (table.getRowCount() > rowCount) {
                     if(model != null) {
                         // Highlight (select) the appropriate rows.
                         int rowToSelect = model.getRowCount() - 1;
                         table.setRowSelectionInterval(rowCount, rowToSelect);
                     }
                 }
             }
         } catch (IOException ioe) {
             JOptionPane.showMessageDialog(this,
                     JMeterUtils.getResString("proxy_daemon_error_read_args") // $NON-NLS-1$
                     + "\n" + ioe.getLocalizedMessage(), JMeterUtils.getResString("error_title"),  // $NON-NLS-1$  $NON-NLS-2$
                     JOptionPane.ERROR_MESSAGE);
         } catch (UnsupportedFlavorException ufe) {
             JOptionPane.showMessageDialog(this,
                     JMeterUtils.getResString("proxy_daemon_error_not_retrieve") + SPACE // $NON-NLS-1$
                         + DataFlavor.stringFlavor.getHumanPresentableName() + SPACE
                         + JMeterUtils.getResString("proxy_daemon_error_from_clipboard") // $NON-NLS-1$
                         + ufe.getLocalizedMessage(), JMeterUtils.getResString("error_title"),  // $NON-NLS-1$
                         JOptionPane.ERROR_MESSAGE);
         }
     }
 
     private void startProxy() {
         ValueReplacer replacer = GuiPackage.getInstance().getReplacer();
         modifyTestElement(model);
         TreeNodeWrapper treeNodeWrapper = (TreeNodeWrapper)targetNodesModel.getSelectedItem();
         if (JMeterUtils.getResString("use_recording_controller").equals(treeNodeWrapper.getLabel())) {
             JMeterTreeNode targetNode = model.findTargetControllerNode();
             if(targetNode == null || !(targetNode.getTestElement() instanceof RecordingController)) {
                 JOptionPane.showMessageDialog(this,
                         JMeterUtils.getResString("proxy_cl_wrong_target_cl"), // $NON-NLS-1$
                         JMeterUtils.getResString("error_title"), // $NON-NLS-1$
                         JOptionPane.ERROR_MESSAGE);
                 return;
             }
         }
         // Proxy can take some while to start up; show a wating cursor
         Cursor cursor = getCursor();
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         // TODO somehow show progress
         try {
             replacer.replaceValues(model);
             model.startProxy();
             start.setEnabled(false);
             stop.setEnabled(true);
             restart.setEnabled(false);
             if (ProxyControl.isDynamicMode()) {
                 String[] details = model.getCertificateDetails();
                 StringBuilder sb = new StringBuilder();
                 sb.append(JMeterUtils.getResString("proxy_daemon_msg_rootca_cert"))  // $NON-NLS-1$
                         .append(SPACE).append(KeyToolUtils.ROOT_CACERT_CRT_PFX)
                         .append(SPACE).append(JMeterUtils.getResString("proxy_daemon_msg_created_in_bin"));
                 sb.append(NEW_LINE).append(JMeterUtils.getResString("proxy_daemon_msg_install_as_in_doc")); // $NON-NLS-1$
                 sb.append(NEW_LINE).append(JMeterUtils.getResString("proxy_daemon_msg_check_details")) // $NON-NLS-1$
                     .append(NEW_LINE).append(NEW_LINE);
                 for(String detail : details) {
                     sb.append(detail).append(NEW_LINE);
                 }
                 JOptionPane.showMessageDialog(this,
                     sb.toString(),
                     JMeterUtils.getResString("proxy_daemon_msg_rootca_cert") + SPACE // $NON-NLS-1$
                     + KeyToolUtils.ROOT_CACERT_CRT_PFX + SPACE
                     + JMeterUtils.getResString("proxy_daemon_msg_created_in_bin"), // $NON-NLS-1$
                     JOptionPane.INFORMATION_MESSAGE);
             }
         } catch (InvalidVariableException e) {
             JOptionPane.showMessageDialog(this,
                     JMeterUtils.getResString("invalid_variables")+": "+e.getMessage(), // $NON-NLS-1$ $NON-NLS-2$
                     JMeterUtils.getResString("error_title"), // $NON-NLS-1$
                     JOptionPane.ERROR_MESSAGE);
         } catch (BindException e) {
             JOptionPane.showMessageDialog(this,
                     JMeterUtils.getResString("proxy_daemon_bind_error")+": "+e.getMessage(), // $NON-NLS-1$ $NON-NLS-2$
                     JMeterUtils.getResString("error_title"), // $NON-NLS-1$
                     JOptionPane.ERROR_MESSAGE);
         } catch (IOException e) {
             JOptionPane.showMessageDialog(this,
                     JMeterUtils.getResString("proxy_daemon_error")+": "+e.getMessage(), // $NON-NLS-1$ $NON-NLS-2$
                     JMeterUtils.getResString("error_title"), // $NON-NLS-1$
                     JOptionPane.ERROR_MESSAGE);
         } finally {
             setCursor(cursor);
         }
     }
 
     private void enableRestart() {
         if (stop.isEnabled()) {
             // System.err.println("Enable Restart");
             restart.setEnabled(true);
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void keyPressed(KeyEvent e) {
     }
 
     /** {@inheritDoc} */
     @Override
     public void keyTyped(KeyEvent e) {
     }
 
     /** {@inheritDoc} */
     @Override
     public void keyReleased(KeyEvent e) {
         String fieldName = e.getComponent().getName();
 
         if (fieldName.equals(PORTFIELD)) {
             try {
                 Integer.parseInt(portField.getText());
             } catch (NumberFormatException nfe) {
                 int length = portField.getText().length();
                 if (length > 0) {
                     JOptionPane.showMessageDialog(this, 
                             JMeterUtils.getResString("proxy_settings_port_error_digits"), // $NON-NLS-1$
                             JMeterUtils.getResString("proxy_settings_port_error_invalid_data"), // $NON-NLS-1$
                             JOptionPane.WARNING_MESSAGE);
                     // Drop the last character:
                     portField.setText(portField.getText().substring(0, length-1));
                 }
             }
             enableRestart();
         } else if (fieldName.equals(ENABLE_RESTART)){
             enableRestart();
         } else if(fieldName.equals(PREFIX_HTTP_SAMPLER_NAME)) {
             model.setPrefixHTTPSampleName(prefixHTTPSampleName.getText());
         }
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel mainPanel = new JPanel(new BorderLayout());
 
         Box myBox = Box.createVerticalBox();
         myBox.add(createPortPanel());
         myBox.add(Box.createVerticalStrut(5));
         myBox.add(createTestPlanContentPanel());
         myBox.add(Box.createVerticalStrut(5));
         myBox.add(createHTTPSamplerPanel());
         myBox.add(Box.createVerticalStrut(5));
         myBox.add(createContentTypePanel());
         myBox.add(Box.createVerticalStrut(5));
         mainPanel.add(myBox, BorderLayout.NORTH);
 
         Box includeExcludePanel = Box.createVerticalBox();
         includeExcludePanel.add(createIncludePanel());
         includeExcludePanel.add(createExcludePanel());
         includeExcludePanel.add(createNotifyListenersPanel());
         mainPanel.add(includeExcludePanel, BorderLayout.CENTER);
 
         mainPanel.add(createControls(), BorderLayout.SOUTH);
 
         add(mainPanel, BorderLayout.CENTER);
     }
 
     private JPanel createControls() {
         start = new JButton(JMeterUtils.getResString("start")); // $NON-NLS-1$
         start.addActionListener(this);
         start.setActionCommand(START);
         start.setEnabled(true);
 
         stop = new JButton(JMeterUtils.getResString("stop")); // $NON-NLS-1$
         stop.addActionListener(this);
         stop.setActionCommand(STOP);
         stop.setEnabled(false);
 
         restart = new JButton(JMeterUtils.getResString("restart")); // $NON-NLS-1$
         restart.addActionListener(this);
         restart.setActionCommand(RESTART);
         restart.setEnabled(false);
 
         JPanel panel = new JPanel();
         panel.add(start);
         panel.add(stop);
         panel.add(restart);
         return panel;
     }
 
     private JPanel createPortPanel() {
         portField = new JTextField(ProxyControl.DEFAULT_PORT_S, 5);
         portField.setName(PORTFIELD);
         portField.addKeyListener(this);
 
         JLabel label = new JLabel(JMeterUtils.getResString("port")); // $NON-NLS-1$
         label.setLabelFor(portField);
 
         JPanel gPane = new JPanel(new BorderLayout());
         gPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("proxy_general_settings"))); // $NON-NLS-1$
 
         HorizontalPanel panel = new HorizontalPanel();
         panel.add(label);
         panel.add(portField);
         panel.add(Box.createHorizontalStrut(10));
 
         gPane.add(panel, BorderLayout.WEST);
 
         sslDomains = new JLabeledTextField(JMeterUtils.getResString("proxy_domains")); // $NON-NLS-1$
         sslDomains.setEnabled(ProxyControl.isDynamicMode());
         if (ProxyControl.isDynamicMode()) {
             sslDomains.setToolTipText(JMeterUtils.getResString("proxy_domains_dynamic_mode_tooltip"));
         } else {
             sslDomains.setToolTipText(JMeterUtils.getResString("proxy_domains_dynamic_mode_tooltip_java6"));
         }
         gPane.add(sslDomains, BorderLayout.CENTER);
         return gPane;
     }
 
     private JPanel createTestPlanContentPanel() {
         httpHeaders = new JCheckBox(JMeterUtils.getResString("proxy_headers")); // $NON-NLS-1$
         httpHeaders.setSelected(true); // maintain original default
         httpHeaders.addActionListener(this);
         httpHeaders.setActionCommand(ENABLE_RESTART);
 
         addAssertions = new JCheckBox(JMeterUtils.getResString("proxy_assertions")); // $NON-NLS-1$
         addAssertions.setSelected(false);
         addAssertions.addActionListener(this);
         addAssertions.setActionCommand(ENABLE_RESTART);
 
         regexMatch = new JCheckBox(JMeterUtils.getResString("proxy_regex")); // $NON-NLS-1$
         regexMatch.setSelected(false);
         regexMatch.addActionListener(this);
         regexMatch.setActionCommand(ENABLE_RESTART);
 
         VerticalPanel mainPanel = new VerticalPanel();
         mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("proxy_test_plan_content"))); // $NON-NLS-1$
 
         HorizontalPanel nodeCreationPanel = new HorizontalPanel();
         nodeCreationPanel.add(createGroupingPanel());
         nodeCreationPanel.add(httpHeaders);
         nodeCreationPanel.add(addAssertions);
         nodeCreationPanel.add(regexMatch);
 
         HorizontalPanel targetPanel = new HorizontalPanel();
         targetPanel.add(createTargetPanel());
         mainPanel.add(targetPanel);
         mainPanel.add(nodeCreationPanel);
 
         return mainPanel;
     }
 
     private JPanel createHTTPSamplerPanel() {
         DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
         for (String s : HTTPSamplerFactory.getImplementations()){
             m.addElement(s);
         }
         m.addElement(USE_DEFAULT_HTTP_IMPL);
         samplerTypeName = new JComboBox<>(m);
         samplerTypeName.setPreferredSize(new Dimension(150, 20));
         samplerTypeName.setSelectedItem(USE_DEFAULT_HTTP_IMPL);
         samplerTypeName.addItemListener(this);
         JLabel label2 = new JLabel(JMeterUtils.getResString("proxy_sampler_type")); // $NON-NLS-1$
         label2.setLabelFor(samplerTypeName);
 
         samplerRedirectAutomatically = new JCheckBox(JMeterUtils.getResString("follow_redirects_auto")); // $NON-NLS-1$
         samplerRedirectAutomatically.setSelected(false);
         samplerRedirectAutomatically.addActionListener(this);
         samplerRedirectAutomatically.setActionCommand(ENABLE_RESTART);
 
         samplerFollowRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects")); // $NON-NLS-1$
         samplerFollowRedirects.setSelected(true);
         samplerFollowRedirects.addActionListener(this);
         samplerFollowRedirects.setActionCommand(ENABLE_RESTART);
 
         useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
         useKeepAlive.setSelected(true);
         useKeepAlive.addActionListener(this);
         useKeepAlive.setActionCommand(ENABLE_RESTART);
 
         samplerDownloadImages = new JCheckBox(JMeterUtils.getResString("web_testing_retrieve_images")); // $NON-NLS-1$
         samplerDownloadImages.setSelected(false);
         samplerDownloadImages.addActionListener(this);
         samplerDownloadImages.setActionCommand(ENABLE_RESTART);
 
         prefixHTTPSampleName = new JTextField(4);
         prefixHTTPSampleName.addKeyListener(this);
         prefixHTTPSampleName.setName(PREFIX_HTTP_SAMPLER_NAME);
         // TODO Not sure this is needed
         prefixHTTPSampleName.setActionCommand(ENABLE_RESTART);
         JLabel labelPrefix = new JLabel(JMeterUtils.getResString("proxy_prefix_http_sampler_name")); // $NON-NLS-1$
         labelPrefix.setLabelFor(prefixHTTPSampleName);
 
         HorizontalPanel panel = new HorizontalPanel();
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("proxy_sampler_settings"))); // $NON-NLS-1$
         panel.add(label2);
         panel.add(samplerTypeName);
         panel.add(labelPrefix);
         panel.add(prefixHTTPSampleName);
         panel.add(samplerRedirectAutomatically);
         panel.add(samplerFollowRedirects);
         panel.add(useKeepAlive);
         panel.add(samplerDownloadImages);
 
         return panel;
     }
 
     private JPanel createTargetPanel() {
         targetNodesModel = new DefaultComboBoxModel<>();
         targetNodes = new JComboBox<>(targetNodesModel);
         targetNodes.setPrototypeDisplayValue(""); // $NON-NLS-1$ // Bug 56303 fixed the width of combo list
         JPopupMenu popup = (JPopupMenu) targetNodes.getUI().getAccessibleChild(targetNodes, 0); // get popup element
         JScrollPane scrollPane = findScrollPane(popup);
         if(scrollPane != null) {
             scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL)); // add scroll pane if label element is too long
             scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         }
         targetNodes.setActionCommand(CHANGE_TARGET);
         // Action listener will be added later
 
         JLabel label = new JLabel(JMeterUtils.getResString("proxy_target")); // $NON-NLS-1$
         label.setLabelFor(targetNodes);
 
         HorizontalPanel panel = new HorizontalPanel();
         panel.add(label);
         panel.add(targetNodes);
 
         return panel;
     }
 
     private JScrollPane findScrollPane(JPopupMenu popup) {
         Component[] components = popup.getComponents();
         for (Component component : components) {
             if(component instanceof JScrollPane) {
                 return (JScrollPane) component;
             }
         }
         return null;
     }
 
     private JPanel createGroupingPanel() {
         DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
         // Note: position of these elements in the menu *must* match the
         // corresponding ProxyControl.GROUPING_* values.
         m.addElement(JMeterUtils.getResString("grouping_no_groups")); // $NON-NLS-1$
         m.addElement(JMeterUtils.getResString("grouping_add_separators")); // $NON-NLS-1$
         m.addElement(JMeterUtils.getResString("grouping_in_controllers")); // $NON-NLS-1$
         m.addElement(JMeterUtils.getResString("grouping_store_first_only")); // $NON-NLS-1$
         m.addElement(JMeterUtils.getResString("grouping_in_transaction_controllers")); // $NON-NLS-1$
         groupingMode = new JComboBox<>(m);
         groupingMode.setPreferredSize(new Dimension(150, 20));
         groupingMode.setSelectedIndex(0);
         groupingMode.addItemListener(this);
 
         JLabel label2 = new JLabel(JMeterUtils.getResString("grouping_mode")); // $NON-NLS-1$
         label2.setLabelFor(groupingMode);
 
         HorizontalPanel panel = new HorizontalPanel();
         panel.add(label2);
         panel.add(groupingMode);
 
         return panel;
     }
 
     private JPanel createContentTypePanel() {
         contentTypeInclude = new JTextField(35);
         contentTypeInclude.addKeyListener(this);
         contentTypeInclude.setName(ENABLE_RESTART);
         JLabel labelInclude = new JLabel(JMeterUtils.getResString("proxy_content_type_include")); // $NON-NLS-1$
         labelInclude.setLabelFor(contentTypeInclude);
         // Default value
         contentTypeInclude.setText(JMeterUtils.getProperty("proxy.content_type_include")); // $NON-NLS-1$
 
         contentTypeExclude = new JTextField(35);
         contentTypeExclude.addKeyListener(this);
         contentTypeExclude.setName(ENABLE_RESTART);
         JLabel labelExclude = new JLabel(JMeterUtils.getResString("proxy_content_type_exclude")); // $NON-NLS-1$
         labelExclude.setLabelFor(contentTypeExclude);
         // Default value
         contentTypeExclude.setText(JMeterUtils.getProperty("proxy.content_type_exclude")); // $NON-NLS-1$
 
         HorizontalPanel panel = new HorizontalPanel();
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("proxy_content_type_filter"))); // $NON-NLS-1$
         panel.add(labelInclude);
         panel.add(contentTypeInclude);
         panel.add(labelExclude);
         panel.add(contentTypeExclude);
 
         return panel;
     }
 
     private JPanel createIncludePanel() {
         includeModel = new PowerTableModel(new String[] { INCLUDE_COL }, new Class[] { String.class });
         includeTable = new JTable(includeModel);
         JMeterUtils.applyHiDPI(includeTable);
         includeTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         includeTable.setPreferredScrollableViewportSize(new Dimension(100, 30));
 
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("patterns_to_include"))); // $NON-NLS-1$
 
         panel.add(new JScrollPane(includeTable), BorderLayout.CENTER);
         panel.add(createTableButtonPanel(ADD_INCLUDE, DELETE_INCLUDE, ADD_TO_INCLUDE_FROM_CLIPBOARD, null), BorderLayout.SOUTH);
 
         return panel;
     }
 
     private JPanel createExcludePanel() {
         excludeModel = new PowerTableModel(new String[] { EXCLUDE_COL }, new Class[] { String.class });
         excludeTable = new JTable(excludeModel);
         JMeterUtils.applyHiDPI(excludeTable);
         excludeTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         excludeTable.setPreferredScrollableViewportSize(new Dimension(100, 30));
 
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("patterns_to_exclude"))); // $NON-NLS-1$
 
         panel.add(new JScrollPane(excludeTable), BorderLayout.CENTER);
         panel.add(createTableButtonPanel(ADD_EXCLUDE, DELETE_EXCLUDE, ADD_TO_EXCLUDE_FROM_CLIPBOARD, ADD_SUGGESTED_EXCLUDES), BorderLayout.SOUTH);
 
         return panel;
     }
 
     private JPanel createNotifyListenersPanel() {
         JPanel panel = new JPanel();
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("notify_child_listeners_fr"))); // $NON-NLS-1$
         
         notifyChildSamplerListenerOfFilteredSamplersCB = new JCheckBox(JMeterUtils.getResString("notify_child_listeners_fr")); // $NON-NLS-1$
         notifyChildSamplerListenerOfFilteredSamplersCB.setSelected(true);
         notifyChildSamplerListenerOfFilteredSamplersCB.addActionListener(this);
         notifyChildSamplerListenerOfFilteredSamplersCB.setActionCommand(ENABLE_RESTART);
 
         panel.add(notifyChildSamplerListenerOfFilteredSamplersCB);
         return panel;
     }
 
     private JPanel createTableButtonPanel(String addCommand, String deleteCommand, String copyFromClipboard, String addSuggestedExcludes) {
         JPanel buttonPanel = new JPanel();
 
         JButton addButton = new JButton(JMeterUtils.getResString("add")); // $NON-NLS-1$
         addButton.setActionCommand(addCommand);
         addButton.addActionListener(this);
         buttonPanel.add(addButton);
 
         JButton deleteButton = new JButton(JMeterUtils.getResString("delete")); // $NON-NLS-1$
         deleteButton.setActionCommand(deleteCommand);
         deleteButton.addActionListener(this);
         buttonPanel.add(deleteButton);
 
         /** A button for adding new excludes/includes to the table from the clipboard. */
         JButton addFromClipboard = new JButton(JMeterUtils.getResString("add_from_clipboard")); // $NON-NLS-1$
         addFromClipboard.setActionCommand(copyFromClipboard);
         addFromClipboard.addActionListener(this);
         buttonPanel.add(addFromClipboard);
 
         if(addSuggestedExcludes != null) {
             /** A button for adding suggested excludes. */
             JButton addFromSuggestedExcludes = new JButton(JMeterUtils.getResString("add_from_suggested_excludes")); // $NON-NLS-1$
             addFromSuggestedExcludes.setActionCommand(addSuggestedExcludes);
             addFromSuggestedExcludes.addActionListener(this);
             buttonPanel.add(addFromSuggestedExcludes);
         }
         return buttonPanel;
     }
 
     private void reinitializeTargetCombo() {
         log.debug("Reinitializing target combo");
 
         // Stop action notifications while we shuffle this around:
         targetNodes.removeActionListener(this);
 
         targetNodesModel.removeAllElements();
         GuiPackage gp = GuiPackage.getInstance();
         JMeterTreeNode root;
         if (gp != null) {
             root = (JMeterTreeNode) GuiPackage.getInstance().getTreeModel().getRoot();
             targetNodesModel
                     .addElement(new TreeNodeWrapper(null, JMeterUtils.getResString("use_recording_controller"))); // $NON-NLS-1$
             buildNodesModel(root, "", 0);
         }
         TreeNodeWrapper choice = null;
         for (int i = 0; i < targetNodesModel.getSize(); i++) {
             choice = (TreeNodeWrapper) targetNodesModel.getElementAt(i);
             log.debug("Selecting item " + choice + " for model " + model + " in " + this);
             if (choice.getTreeNode() == model.getTarget()) // .equals caused NPE
             {
                 break;
             }
         }
         // Reinstate action notifications:
         targetNodes.addActionListener(this);
         // Set the current value:
         targetNodesModel.setSelectedItem(choice);
 
         log.debug("Reinitialization complete");
     }
 
     private void buildNodesModel(JMeterTreeNode node, String parentName, int level) {
         String separator = " > ";
         if (node != null) {
             for (int i = 0; i < node.getChildCount(); i++) {
                 StringBuilder name = new StringBuilder();
                 JMeterTreeNode cur = (JMeterTreeNode) node.getChildAt(i);
                 TestElement te = cur.getTestElement();
                 /*
                  * Will never be true. Probably intended to use
                  * org.apache.jmeter.threads.ThreadGroup rather than
                  * java.lang.ThreadGroup However, that does not work correctly;
                  * whereas treating it as a Controller does. if (te instanceof
                  * ThreadGroup) { name.append(parent_name);
                  * name.append(cur.getName()); name.append(seperator);
                  * buildNodesModel(cur, name.toString(), level); } else
                  */
                 if (te instanceof Controller) {
                     name.append(parentName);
                     name.append(cur.getName());
                     TreeNodeWrapper tnw = new TreeNodeWrapper(cur, name.toString());
                     targetNodesModel.addElement(tnw);
                     name.append(separator);
                     buildNodesModel(cur, name.toString(), level + 1);
                 } else if (te instanceof TestPlan || te instanceof WorkBench) {
                     name.append(cur.getName());
                     name.append(separator);
                     buildNodesModel(cur, name.toString(), 0);
                 }
                 // Ignore everything else
             }
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java b/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java
index 54d2e2f9f..6c4e1226f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java
@@ -1,477 +1,477 @@
 /*
 o * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements. See the NOTICE file distributed with this
  * work for additional information regarding copyright ownership. The ASF
  * licenses this file to You under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  * http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.protocol.http.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.io.UnsupportedEncodingException;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.StandardCharsets;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import javax.swing.JPanel;
 import javax.swing.JSplitPane;
 import javax.swing.JTable;
 import javax.swing.table.TableCellRenderer;
 import javax.swing.table.TableColumn;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.TextBoxDialoger.TextBoxDoubleClick;
 import org.apache.jmeter.protocol.http.config.MultipartUrlConfig;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.RequestView;
 import org.apache.jmeter.visualizers.SamplerResultTab.RowResult;
 import org.apache.jmeter.visualizers.SearchTextExtension;
 import org.apache.jmeter.visualizers.SearchTextExtension.ISearchTextExtensionProvider;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RendererUtils;
-import org.apache.jorphan.logging.LoggingManager;
+import org.slf4j.LoggerFactory;
 import org.apache.jorphan.reflect.Functor;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
 
 /**
  * Specializer panel to view a HTTP request parsed
  *
  */
 public class RequestViewHTTP implements RequestView {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RequestViewHTTP.class);
 
     private static final String KEY_LABEL = "view_results_table_request_tab_http"; //$NON-NLS-1$
     
     private static final String CHARSET_DECODE = StandardCharsets.ISO_8859_1.name();
     
     private static final String PARAM_CONCATENATE = "&"; //$NON-NLS-1$
 
     private JPanel paneParsed;
 
     private ObjectTableModel requestModel = null;
 
     private ObjectTableModel paramsModel = null;
 
     private ObjectTableModel headersModel = null;
 
     private static final String[] COLUMNS_REQUEST = new String[] {
             " ", // one space for blank header // $NON-NLS-1$ 
             " " }; // one space for blank header  // $NON-NLS-1$
 
     private static final String[] COLUMNS_PARAMS = new String[] {
             "view_results_table_request_params_key", // $NON-NLS-1$
             "view_results_table_request_params_value" }; // $NON-NLS-1$
 
     private static final String[] COLUMNS_HEADERS = new String[] {
             "view_results_table_request_headers_key", // $NON-NLS-1$
             "view_results_table_request_headers_value" }; // $NON-NLS-1$
 
     private JTable tableRequest = null;
 
     private JTable tableParams = null;
 
     private JTable tableHeaders = null;
 
     // Request headers column renderers
     private static final TableCellRenderer[] RENDERERS_REQUEST = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     // Request headers column renderers
     private static final TableCellRenderer[] RENDERERS_PARAMS = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     // Request headers column renderers
     private static final TableCellRenderer[] RENDERERS_HEADERS = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     private SearchTextExtension searchTextExtension;
 
     /**
      * Pane to view HTTP request sample in view results tree
      */
     public RequestViewHTTP() {
         requestModel = new ObjectTableModel(COLUMNS_REQUEST, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
         paramsModel = new ObjectTableModel(COLUMNS_PARAMS, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
         headersModel = new ObjectTableModel(COLUMNS_HEADERS, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#init()
      */
     @Override
     public void init() {
         paneParsed = new JPanel(new BorderLayout(0, 5));
         paneParsed.add(createRequestPane());
         this.searchTextExtension = new SearchTextExtension();
         this.searchTextExtension.init(paneParsed);
         JPanel searchPanel = this.searchTextExtension.createSearchTextExtensionPane();
         searchPanel.setBorder(null);
         this.searchTextExtension.setSearchProvider(new RequestViewHttpSearchProvider());
         searchPanel.setVisible(true);
         paneParsed.add(searchPanel, BorderLayout.PAGE_END);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#clearData()
      */
     @Override
     public void clearData() {
         requestModel.clearData();
         paramsModel.clearData();
         headersModel.clearData(); // clear results table before filling
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#setSamplerResult(java.lang.Object)
      */
     @Override
     public void setSamplerResult(Object objectResult) {
 
         this.searchTextExtension.resetTextToFind();
         if (objectResult instanceof HTTPSampleResult) {
             HTTPSampleResult sampleResult = (HTTPSampleResult) objectResult;
 
             // Display with same order HTTP protocol
             requestModel.addRow(new RowResult(
                     JMeterUtils.getResString("view_results_table_request_http_method"), //$NON-NLS-1$
                     sampleResult.getHTTPMethod()));
 
             // Parsed request headers
             LinkedHashMap<String, String> lhm = JMeterUtils.parseHeaders(sampleResult.getRequestHeaders());
             for (Entry<String, String> entry : lhm.entrySet()) {
                 headersModel.addRow(new RowResult(entry.getKey(), entry.getValue()));
             }
 
             URL hUrl = sampleResult.getURL();
             if (hUrl != null){ // can be null - e.g. if URL was invalid
                 requestModel.addRow(new RowResult(JMeterUtils
                         .getResString("view_results_table_request_http_protocol"), //$NON-NLS-1$
                         hUrl.getProtocol()));
                 requestModel.addRow(new RowResult(
                         JMeterUtils.getResString("view_results_table_request_http_host"), //$NON-NLS-1$
                         hUrl.getHost()));
                 int port = hUrl.getPort() == -1 ? hUrl.getDefaultPort() : hUrl.getPort();
                 requestModel.addRow(new RowResult(
                         JMeterUtils.getResString("view_results_table_request_http_port"), //$NON-NLS-1$
                         Integer.valueOf(port)));
                 requestModel.addRow(new RowResult(
                         JMeterUtils.getResString("view_results_table_request_http_path"), //$NON-NLS-1$
                         hUrl.getPath()));
     
                 String queryGet = hUrl.getQuery() == null ? "" : hUrl.getQuery(); //$NON-NLS-1$
                 boolean isMultipart = isMultipart(lhm);
 
                 // Concatenate query post if exists
                 String queryPost = sampleResult.getQueryString();
                 if (!isMultipart && StringUtils.isNotBlank(queryPost)) {
                     if (queryGet.length() > 0) {
                         queryGet += PARAM_CONCATENATE;
                     }
                     queryGet += queryPost;
                 }
                 
                 if (StringUtils.isNotBlank(queryGet)) {
                     Set<Entry<String, String[]>> keys = RequestViewHTTP.getQueryMap(queryGet).entrySet();
                     for (Entry<String, String[]> entry : keys) {
                         for (String value : entry.getValue()) {
                             paramsModel.addRow(new RowResult(entry.getKey(), value));
                         }
                     }
                 }
 
                 if(isMultipart && StringUtils.isNotBlank(queryPost)) {
                     String contentType = lhm.get(HTTPConstants.HEADER_CONTENT_TYPE);
                     String boundaryString = extractBoundary(contentType);
                     MultipartUrlConfig urlconfig = new MultipartUrlConfig(boundaryString);
                     urlconfig.parseArguments(queryPost);
                     
                     for(JMeterProperty prop : urlconfig.getArguments()) {
                         Argument arg = (Argument) prop.getObjectValue();
                         paramsModel.addRow(new RowResult(arg.getName(), arg.getValue()));
                     }
                 }
             }
             
             // Display cookie in headers table (same location on http protocol)
             String cookie = sampleResult.getCookies();
             if (cookie != null && cookie.length() > 0) {
                 headersModel.addRow(new RowResult(
                         JMeterUtils.getParsedLabel("view_results_table_request_http_cookie"), //$NON-NLS-1$
                         sampleResult.getCookies()));
             }
 
         }
         else {
             // add a message when no http sample
             requestModel.addRow(new RowResult("", //$NON-NLS-1$
                     JMeterUtils.getResString("view_results_table_request_http_nohttp"))); //$NON-NLS-1$
         }
     }
 
     /**
      * Extract the multipart boundary
      * @param contentType the content type header
      * @return  the boundary string
      */
     private String extractBoundary(String contentType) {
         // Get the boundary string for the multiparts from the content type
         String boundaryString = contentType.substring(contentType.toLowerCase(java.util.Locale.ENGLISH).indexOf("boundary=") + "boundary=".length());
         //TODO check in the RFC if other char can be used as separator
         String[] split = boundaryString.split(";");
         if(split.length > 1) {
             boundaryString = split[0];
         }
         return boundaryString;
     }
     
     /**
      * check if the request is multipart
      * @param headers the http request headers
      * @return true if the request is multipart
      */
     private boolean isMultipart(LinkedHashMap<String, String> headers) {
         String contentType = headers.get(HTTPConstants.HEADER_CONTENT_TYPE);
         if (contentType != null && contentType.startsWith(HTTPConstants.MULTIPART_FORM_DATA)) {
             return true;
         }
         return false;
     }
 
     /**
      * @param query query to parse for param and value pairs
      * @return Map params and values
      */
     //TODO: move to utils class (JMeterUtils?)
     public static Map<String, String[]> getQueryMap(String query) {
 
         Map<String, String[]> map = new HashMap<>();
         String[] params = query.split(PARAM_CONCATENATE);
         for (String param : params) {
             String[] paramSplit = param.split("=");
             String name = decodeQuery(paramSplit[0]);
 
             // hack for SOAP request (generally)
             if (name.trim().startsWith("<?")) { // $NON-NLS-1$
                 map.put(" ", new String[] {query}); //blank name // $NON-NLS-1$
                 return map;
             }
             
             // the post payload is not key=value
             if((param.startsWith("=") && paramSplit.length == 1) || paramSplit.length > 2) {
                 map.put(" ", new String[] {query}); //blank name // $NON-NLS-1$
                 return map;
             }
 
             String value = "";
             if(paramSplit.length>1) {
                 value = decodeQuery(paramSplit[1]);
             }
             
             String[] known = map.get(name);
             if(known == null) {
                 known = new String[] {value};
             }
             else {
                 String[] tmp = new String[known.length+1];
                 tmp[tmp.length-1] = value;
                 System.arraycopy(known, 0, tmp, 0, known.length);
                 known = tmp;
             }
             map.put(name, known);
         }
         
         return map;
     }
 
     /**
      * Decode a query string
      * 
      * @param query
      *            to decode
      * @return the decoded query string, if it can be url-decoded. Otherwise the original
      *            query will be returned.
      */
     public static String decodeQuery(String query) {
         if (query != null && query.length() > 0) {
             try {
                 return URLDecoder.decode(query, CHARSET_DECODE); // better  ISO-8859-1 than UTF-8
             } catch (IllegalArgumentException | UnsupportedEncodingException e) {
                 log.warn(
                         "Error decoding query, maybe your request parameters should be encoded:"
                                 + query, e);
                 return query;
             }
         }
         return "";
     }
 
     @Override
     public JPanel getPanel() {
         return paneParsed;
     }
 
     /**
      * Create a pane with three tables (request, params, headers)
      * 
      * @return Pane to display request data
      */
     private Component createRequestPane() {
         // Set up the 1st table Result with empty headers
         tableRequest = new JTable(requestModel);
         JMeterUtils.applyHiDPI(tableRequest);
         tableRequest.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableRequest.addMouseListener(new TextBoxDoubleClick(tableRequest));
         
         setFirstColumnPreferredAndMaxWidth(tableRequest);
         RendererUtils.applyRenderers(tableRequest, RENDERERS_REQUEST);
 
         // Set up the 2nd table 
         tableParams = new JTable(paramsModel);
         JMeterUtils.applyHiDPI(tableParams);
         tableParams.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableParams.addMouseListener(new TextBoxDoubleClick(tableParams));
         TableColumn column = tableParams.getColumnModel().getColumn(0);
         column.setPreferredWidth(160);
         tableParams.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         RendererUtils.applyRenderers(tableParams, RENDERERS_PARAMS);
 
         // Set up the 3rd table 
         tableHeaders = new JTable(headersModel);
         JMeterUtils.applyHiDPI(tableHeaders);
         tableHeaders.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableHeaders.addMouseListener(new TextBoxDoubleClick(tableHeaders));
         setFirstColumnPreferredAndMaxWidth(tableHeaders);
         tableHeaders.getTableHeader().setDefaultRenderer(
                 new HeaderAsPropertyRenderer());
         RendererUtils.applyRenderers(tableHeaders, RENDERERS_HEADERS);
 
         // Create the split pane
         JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 GuiUtils.makeScrollPane(tableParams),
                 GuiUtils.makeScrollPane(tableHeaders));
         topSplit.setOneTouchExpandable(true);
         topSplit.setResizeWeight(0.50); // set split ratio
         topSplit.setBorder(null); // see bug jdk 4131528
 
         JSplitPane paneParsed = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 GuiUtils.makeScrollPane(tableRequest), topSplit);
         paneParsed.setOneTouchExpandable(true);
         paneParsed.setResizeWeight(0.25); // set split ratio (only 5 lines to display)
         paneParsed.setBorder(null); // see bug jdk 4131528
 
         // Hint to background color on bottom tabs (grey, not blue)
         JPanel panel = new JPanel(new BorderLayout());
         panel.add(paneParsed);
         return panel;
     }
 
     private void setFirstColumnPreferredAndMaxWidth(JTable table) {
         TableColumn column = table.getColumnModel().getColumn(0);
         column.setMaxWidth(300);
         column.setPreferredWidth(160);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#getLabel()
      */
     @Override
     public String getLabel() {
         return JMeterUtils.getResString(KEY_LABEL);
     }
     
     /**
      * Search implementation for the http parameter table
      */
     private class RequestViewHttpSearchProvider implements ISearchTextExtensionProvider {
 
         private int lastPosition = -1;
         
         @Override
         public void resetTextToFind() {
             lastPosition = -1;
             if(tableParams != null) {
                 tableParams.clearSelection();
             }
         }
 
         @Override
         public boolean executeAndShowTextFind(Pattern pattern) {
             boolean found =  false;
             if(tableParams != null) {
                 tableParams.clearSelection();
                 outerloop:
                 for (int i = lastPosition+1; i < tableParams.getRowCount(); i++) {
                     for (int j = 0; j < COLUMNS_PARAMS.length; j++) {
                         Object o = tableParams.getModel().getValueAt(i, j);
                         if(o instanceof String) {
                             Matcher matcher = pattern.matcher((String) o);
                             if ((matcher != null) && (matcher.find())) {
                                 found =  true;
                                 tableParams.setRowSelectionInterval(i, i);
                                 tableParams.scrollRectToVisible(tableParams.getCellRect(i, 0, true));
                                 lastPosition = i;
                                 break outerloop;
                             }
                         }
                     }
                 }
                 
                 if(!found) {
                     resetTextToFind();
                 }
             }
             return found;
         }
         
     }
 }
