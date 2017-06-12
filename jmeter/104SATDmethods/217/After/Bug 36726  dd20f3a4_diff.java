diff --git a/src/components/org/apache/jmeter/visualizers/SearchTextExtension.java b/src/components/org/apache/jmeter/visualizers/SearchTextExtension.java
new file mode 100644
index 000000000..5be57fe0e
--- /dev/null
+++ b/src/components/org/apache/jmeter/visualizers/SearchTextExtension.java
@@ -0,0 +1,359 @@
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
+ * distributed  under the  License is distributed on an "AS IS" BASIS,
+ * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
+ * implied.
+ * 
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package org.apache.jmeter.visualizers;
+
+import java.awt.Color;
+import java.awt.Dimension;
+import java.awt.Font;
+import java.awt.event.ActionEvent;
+import java.awt.event.ActionListener;
+import java.awt.event.InputEvent;
+import java.awt.event.KeyEvent;
+import java.util.regex.Matcher;
+import java.util.regex.Pattern;
+
+import javax.swing.AbstractAction;
+import javax.swing.ActionMap;
+import javax.swing.BorderFactory;
+import javax.swing.Box;
+import javax.swing.BoxLayout;
+import javax.swing.InputMap;
+import javax.swing.JButton;
+import javax.swing.JCheckBox;
+import javax.swing.JComponent;
+import javax.swing.JEditorPane;
+import javax.swing.JLabel;
+import javax.swing.JOptionPane;
+import javax.swing.JPanel;
+import javax.swing.JTextField;
+import javax.swing.KeyStroke;
+import javax.swing.event.DocumentEvent;
+import javax.swing.event.DocumentListener;
+import javax.swing.text.BadLocationException;
+import javax.swing.text.DefaultHighlighter;
+import javax.swing.text.Document;
+import javax.swing.text.Highlighter;
+
+import org.apache.jmeter.util.JMeterUtils;
+import org.apache.jorphan.logging.LoggingManager;
+import org.apache.log.Logger;
+
+public class SearchTextExtension implements ActionListener, DocumentListener {
+    
+    private static final Logger log = LoggingManager.getLoggerForClass();
+    
+    public static final String SEARCH_TEXT_COMMAND = "search_text"; // $NON-NLS-1$
+    
+    public static final String DIALOG_SEARCH_SHOW_COMMAND = "show_search_dialog"; // $NON-NLS-1$
+    
+    public static final String DIALOG_SEARCH_HIDE_COMMAND = "close_search_dialog"; // $NON-NLS-1$
+
+    private static final String SEARCH_SHOW_COMMAND = "show_search_text"; // $NON-NLS-1$
+
+    // set default command to Text
+    private String command = DIALOG_SEARCH_SHOW_COMMAND;
+    
+    private static int LAST_POSITION_DEFAULT = 0;
+
+    private int lastPosition = LAST_POSITION_DEFAULT;
+    
+    private final static Color HILIT_COLOR = Color.LIGHT_GRAY;
+    
+    private Highlighter selection;
+    
+    private Highlighter.HighlightPainter painter;
+    
+    private JLabel label;
+
+    private JButton findButton;
+
+    private JTextField textToFindField;
+
+    private  JCheckBox caseChkBox;
+
+    private JCheckBox regexpChkBox;
+
+    private String lastTextTofind;
+
+    private boolean newSearch = false;
+    
+    private JEditorPane results;
+    
+    private JPanel searchPanel;
+
+    private boolean enabled = true;
+    
+    private JCheckBox showSearch;
+
+    
+    public void init(JPanel resultsPane) {
+        // when CTRL-T is pressed, (un-)show search dialog box (only in results pane)
+        InputMap im = resultsPane
+                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
+        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 
+                InputEvent.CTRL_MASK), SEARCH_SHOW_COMMAND);
+        ActionMap am = resultsPane.getActionMap();
+        am.put(SEARCH_SHOW_COMMAND, new DisplaySearchAction());
+    }
+    
+    public void setResults(JEditorPane results) {
+        if (this.results != null) {
+            newSearch = true;
+            resetTextToFind();
+        }
+        this.results = results;
+        // prepare highlighter to show text find with search command
+        selection = new DefaultHighlighter();
+        painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
+        results.setHighlighter(selection);
+    }
+
+    /**
+     * Launch find text engine on response text
+     */
+    public void executeAndShowTextFind() {
+        if (results != null && results.getText().length() > 0
+                && this.textToFindField.getText().length() > 0) {
+            String textToFind = textToFindField.getText();
+
+            // new search?
+            if (lastTextTofind != null && !lastTextTofind.equals(textToFind)) {
+                lastPosition = LAST_POSITION_DEFAULT;
+            }
+
+            log.debug("lastPosition=" + lastPosition);
+            Matcher matcher = null;
+            try {
+                Pattern pattern = createPattern(textToFind);
+                Document contentDoc = results.getDocument();
+                String body = contentDoc.getText(lastPosition,
+                        (contentDoc.getLength() - lastPosition));
+                matcher = pattern.matcher(body);
+
+                if ((matcher != null) && (matcher.find())) {
+                    selection.removeAllHighlights();
+                    selection.addHighlight(lastPosition + matcher.start(),
+                            lastPosition + matcher.end(), painter);
+                    results.setCaretPosition(lastPosition + matcher.end());                    
+
+                    // save search position
+                    lastPosition = lastPosition + matcher.end();
+                    findButton.setText(JMeterUtils
+                            .getResString("search_text_button_next"));// $NON-NLS-1$
+                    lastTextTofind = textToFind;
+                    newSearch = true;
+                } else {
+                    // Display not found message and reset search
+                    JOptionPane.showMessageDialog(null, JMeterUtils
+                            .getResString("search_text_msg_not_found"),// $NON-NLS-1$
+                            JMeterUtils.getResString("search_text_title_not_found"), // $NON-NLS-1$
+                            JOptionPane.INFORMATION_MESSAGE);
+                    lastPosition = LAST_POSITION_DEFAULT;
+                    findButton.setText(JMeterUtils
+                            .getResString("search_text_button_find"));// $NON-NLS-1$
+                    results.setCaretPosition(0);
+                }
+            } catch (BadLocationException ble) {
+                log.error("Location exception in text find", ble);// $NON-NLS-1$
+            }
+        }
+    }
+
+    /**
+     * Create the text find task pane
+     * 
+     * @return Text find task pane
+     */
+    public JPanel createSearchTextPanel() {
+        Font font = new Font("SansSerif", Font.PLAIN, 10);
+        
+        // Search field
+        searchPanel = new JPanel();
+        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
+        searchPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
+        label = new JLabel(JMeterUtils.getResString("search_text_field")); // $NON-NLS-1$
+        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
+        searchPanel.add(label);
+        textToFindField = new JTextField(); // $NON-NLS-1$
+        searchPanel.add(textToFindField);
+        searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
+
+        // add listener to intercept texttofind changes and reset search
+        textToFindField.getDocument().addDocumentListener(this);
+
+        // Buttons
+        findButton = new JButton(JMeterUtils
+                .getResString("search_text_button_find")); // $NON-NLS-1$
+        findButton.setFont(font);
+        findButton.setActionCommand(SEARCH_TEXT_COMMAND);
+        findButton.addActionListener(this);
+        searchPanel.add(findButton);
+
+        // checkboxes
+        caseChkBox = new JCheckBox(JMeterUtils
+                .getResString("search_text_chkbox_case"), false); // $NON-NLS-1$
+        caseChkBox.setFont(font);
+        searchPanel.add(caseChkBox);
+        regexpChkBox = new JCheckBox(JMeterUtils
+                .getResString("search_text_chkbox_regexp"), false); // $NON-NLS-1$
+        regexpChkBox.setFont(font);
+        searchPanel.add(regexpChkBox);
+
+        // when Enter is pressed, search start
+        InputMap im = textToFindField
+                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
+        im.put(KeyStroke.getKeyStroke("ENTER"), SEARCH_TEXT_COMMAND);
+        ActionMap am = textToFindField.getActionMap();
+        am.put(SEARCH_TEXT_COMMAND, new EnterAction());
+        
+        return searchPanel;
+    }
+    
+    public JPanel createSearchTextExtensionPane() {
+        JPanel pane = new JPanel();
+        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
+        pane.add(createShowSearchPanel());
+        pane.add(createSearchTextPanel());
+        return pane;
+    }
+    
+    public JPanel createShowSearchPanel() {
+        Font fontBold = new Font("SansSerif", Font.BOLD, 10);
+
+        showSearch = new JCheckBox();
+        showSearch.setFont(fontBold);
+        showSearch.setAction(new DisplaySearchAction());
+        showSearch.setText(JMeterUtils
+                .getResString("view_results_search_pane")); // $NON-NLS-1$
+        
+        JPanel pane = new JPanel();
+        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
+        pane.add(showSearch);
+        
+        return pane;
+    }
+
+
+    /**
+     * Display the response as text or as rendered HTML. Change the text on the
+     * button appropriate to the current display.
+     *
+     * @param e
+     *            the ActionEvent being processed
+     */
+    public void actionPerformed(ActionEvent e) {
+        command = e.getActionCommand();
+
+        // Search text in response data
+        if (command != null 
+                && (command.equals(SearchTextExtension.SEARCH_TEXT_COMMAND))) {
+            executeAndShowTextFind();
+        }
+        // hide search dialog box
+        else if (command != null && command.equals(
+                SearchTextExtension.DIALOG_SEARCH_HIDE_COMMAND)) {
+            hideSearchBox();
+        }
+    }
+
+    private class EnterAction extends AbstractAction {
+        private static final long serialVersionUID = 1L;
+        public void actionPerformed(ActionEvent ev) {
+            executeAndShowTextFind();
+        }
+    }
+
+    private class DisplaySearchAction extends AbstractAction {
+        private static final long serialVersionUID = 1L;
+        public void actionPerformed(ActionEvent ev) {
+            if (searchPanel != null) {
+                resetTextToFind();
+                if (searchPanel.isVisible()) {
+                    searchPanel.setVisible(false);
+                    showSearch.setSelected(false);
+                } else {
+                    searchPanel.setVisible(true);
+                    showSearch.setSelected(true);
+                }
+            }
+        }
+    }
+    
+    // DocumentListener method
+    public void changedUpdate(DocumentEvent e) {
+        // do nothing
+    }
+
+    // DocumentListener method
+    public void insertUpdate(DocumentEvent e) {
+        resetTextToFind();
+    }
+
+    // DocumentListener method
+    public void removeUpdate(DocumentEvent e) {
+        resetTextToFind();
+    }
+
+    public void hideSearchBox() {
+        searchPanel.setVisible(false);
+    }
+    
+    public void resetTextToFind() {
+        if (newSearch) {
+            log.debug("reset pass");
+            // Reset search
+            lastPosition = LAST_POSITION_DEFAULT;
+            lastTextTofind = null;
+            findButton.setText(JMeterUtils
+                    .getResString("search_text_button_find"));// $NON-NLS-1$
+            selection.removeAllHighlights();
+            results.setCaretPosition(0);
+            newSearch = false;
+        }
+    }
+    
+    public boolean isEnabled() {
+        return enabled ;
+    }
+    
+    public void setEnabled(boolean b) {
+        this.enabled = b;
+        label.setEnabled(b);
+        textToFindField.setEnabled(b);
+        findButton.setEnabled(b);
+        caseChkBox.setEnabled(b);
+        regexpChkBox.setEnabled(b);
+    }
+    
+    private Pattern createPattern(String textToFind) {
+        // desactivate or not specials regexp char 
+        String textToFindQ = Pattern.quote(textToFind);
+        if (regexpChkBox.isSelected()) {
+            textToFindQ = textToFind;
+        }
+        Pattern pattern = null;
+        if (caseChkBox.isSelected()) {
+            pattern = Pattern.compile(textToFindQ);
+        } else {
+            pattern = Pattern.compile(textToFindQ, Pattern.CASE_INSENSITIVE);
+        }
+        return pattern;
+    }
+}
diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index b07e8bade..f2ca9227c 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,1024 +1,1076 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Dimension;
+import java.awt.Font;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
+import java.awt.event.InputEvent;
+import java.awt.event.KeyEvent;
 import java.io.ByteArrayInputStream;
 import java.io.StringWriter;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
+import javax.swing.AbstractAction;
+import javax.swing.ActionMap;
 import javax.swing.BorderFactory;
+import javax.swing.Box;
+import javax.swing.BoxLayout;
 import javax.swing.ButtonGroup;
 import javax.swing.Icon;
 import javax.swing.ImageIcon;
+import javax.swing.InputMap;
 import javax.swing.JCheckBox;
+import javax.swing.JComponent;
 import javax.swing.JEditorPane;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextPane;
 import javax.swing.JTree;
+import javax.swing.KeyStroke;
 import javax.swing.ToolTipManager;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.text.BadLocationException;
 import javax.swing.text.ComponentView;
 import javax.swing.text.Document;
 import javax.swing.text.EditorKit;
 import javax.swing.text.Element;
 import javax.swing.text.Style;
 import javax.swing.text.StyleConstants;
 import javax.swing.text.StyledDocument;
 import javax.swing.text.View;
 import javax.swing.text.ViewFactory;
 import javax.swing.text.html.HTML;
 import javax.swing.text.html.HTMLEditorKit;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.DefaultTreeModel;
 import javax.swing.tree.TreePath;
 import javax.swing.tree.TreeSelectionModel;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.XPathUtil;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 import org.xml.sax.SAXException;
 
 /**
  * Allows the tester to view the textual response from sampling an Entry. This
  * also allows to "single step through" the sampling process via a nice
  * "Continue" button.
  *
  * Created 2001/07/25
  */
 public class ViewResultsFullVisualizer extends AbstractVisualizer
         implements ActionListener, TreeSelectionListener, Clearable
     {
 
     private static final long serialVersionUID = 1L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Maximum size that we will display
     private static final int MAX_DISPLAY_SIZE =
         JMeterUtils.getPropDefault("view.results.tree.max_size", 200 * 1024); // $NON-NLS-1$
 
     // N.B. these are not multi-threaded, so don't make it static
     private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // ISO format $NON-NLS-1$
 
     private static final String NL = "\n"; // $NON-NLS-1$
 
     private static final byte[] XML_PFX = "<?xml ".getBytes(); // $NON-NLS-1$
 
     public static final Color SERVER_ERROR_COLOR = Color.red;
 
     public static final Color CLIENT_ERROR_COLOR = Color.blue;
 
     public static final Color REDIRECT_COLOR = Color.green;
 
     private static final String TEXT_HTML = "text/html"; // $NON-NLS-1$
 
     private static final String HTML_COMMAND = "html"; // $NON-NLS-1$
 
     private static final String JSON_COMMAND = "json"; // $NON-NLS-1$
 
     private static final String XML_COMMAND = "xml"; // $NON-NLS-1$
 
     private static final String TEXT_COMMAND = "text"; // $NON-NLS-1$
 
     private static final String STYLE_SERVER_ERROR = "ServerError"; // $NON-NLS-1$
 
     private static final String STYLE_CLIENT_ERROR = "ClientError"; // $NON-NLS-1$
 
     private static final String STYLE_REDIRECT = "Redirect"; // $NON-NLS-1$
 
     private boolean textMode = true;
 
     private static final String ESC_CHAR_REGEX = "\\\\[\"\\\\/bfnrt]|\\\\u[0-9A-Fa-f]{4}"; // $NON-NLS-1$
 
     private static final String NORMAL_CHARACTER_REGEX = "[^\"\\\\]";  // $NON-NLS-1$
 
     private static final String STRING_REGEX = "\"(" + ESC_CHAR_REGEX + "|" + NORMAL_CHARACTER_REGEX + ")*\""; // $NON-NLS-1$
 
     // This 'other value' regex is deliberately weak, even accepting an empty string, to be useful when reporting malformed data.
     private static final String OTHER_VALUE_REGEX = "[^\\{\\[\\]\\}\\,]*"; // $NON-NLS-1$
 
     private static final String VALUE_OR_PAIR_REGEX = "((" + STRING_REGEX + "\\s*:)?\\s*(" + STRING_REGEX + "|" + OTHER_VALUE_REGEX + ")\\s*,?\\s*)"; // $NON-NLS-1$
 
     private static final Pattern VALUE_OR_PAIR_PATTERN = Pattern.compile(VALUE_OR_PAIR_REGEX);
 
     // set default command to Text
     private String command = TEXT_COMMAND;
 
     // Keep copies of the two editors needed
     private static final EditorKit customisedEditor = new LocalHTMLEditorKit();
 
     private static final EditorKit defaultHtmlEditor = JEditorPane.createEditorKitForContentType(TEXT_HTML);
 
     private DefaultMutableTreeNode root;
 
     private DefaultTreeModel treeModel;
 
     private JTextPane stats;
 
     private JEditorPane results;
 
     private JScrollPane resultsScrollPane;
 
     private JPanel resultsPane;
 
     private JLabel imageLabel;
 
     private JTextArea sampleDataField;
 
     private JPanel requestPane;
 
     private JRadioButton textButton;
 
     private JRadioButton htmlButton;
 
     private JRadioButton jsonButton;
 
     private JRadioButton xmlButton;
 
     private JCheckBox downloadAll;
+    
+    private JLabel renderLabel;
 
     private JTree jTree;
 
     private JTabbedPane rightSide;
 
+    private SearchTextExtension searchTextExtension;
+    
+    private JPanel searchPanel = null;
+
     private static final ImageIcon imageSuccess = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                     "icon_success_sml.gif")); //$NON-NLS-1$
 
     private static final ImageIcon imageFailure = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                     "icon_warning_sml.gif")); //$NON-NLS-1$
 
     public ViewResultsFullVisualizer() {
         super();
         log.debug("Start : ViewResultsFullVisualizer1");
         init();
         log.debug("End : ViewResultsFullVisualizer1");
     }
 
     public void add(SampleResult res) {
         updateGui(res);
     }
 
     public String getLabelResource() {
         return "view_results_tree_title"; // $NON-NLS-1$
     }
 
     /**
      * Update the visualizer with new data.
      */
     private synchronized void updateGui(SampleResult res) {
         log.debug("Start : updateGui1");
         if (log.isDebugEnabled()) {
             log.debug("updateGui1 : sample result - " + res);
         }
         // Add sample
         DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(res);
         treeModel.insertNodeInto(currNode, root, root.getChildCount());
         addSubResults(currNode, res);
         // Add any assertion that failed as children of the sample node
         AssertionResult assertionResults[] = res.getAssertionResults();
         int assertionIndex = currNode.getChildCount();
         for (int j = 0; j < assertionResults.length; j++) {
             AssertionResult item = assertionResults[j];
 
             if (item.isFailure() || item.isError()) {
                 DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(item);
                 treeModel.insertNodeInto(assertionNode, currNode, assertionIndex++);
             }
         }
 
         if (root.getChildCount() == 1) {
             jTree.expandPath(new TreePath(root));
         }
         log.debug("End : updateGui1");
     }
 
     private void addSubResults(DefaultMutableTreeNode currNode, SampleResult res) {
         SampleResult[] subResults = res.getSubResults();
 
         int leafIndex = 0;
 
         for (int i = 0; i < subResults.length; i++) {
             SampleResult child = subResults[i];
 
             if (log.isDebugEnabled()) {
                 log.debug("updateGui1 : child sample result - " + child);
             }
             DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(child);
 
             treeModel.insertNodeInto(leafNode, currNode, leafIndex++);
             addSubResults(leafNode, child);
             // Add any assertion that failed as children of the sample node
             AssertionResult assertionResults[] = child.getAssertionResults();
             int assertionIndex = leafNode.getChildCount();
             for (int j = 0; j < assertionResults.length; j++) {
                 AssertionResult item = assertionResults[j];
 
                 if (item.isFailure() || item.isError()) {
                     DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(item);
                     treeModel.insertNodeInto(assertionNode, leafNode, assertionIndex++);
                 }
             }
         }
     }
 
     /**
      * Clears the visualizer.
      */
     public synchronized void clearData() {
         log.debug("Start : clear1");
 
         if (log.isDebugEnabled()) {
             log.debug("clear1 : total child - " + root.getChildCount());
         }
         while (root.getChildCount() > 0) {
             // the child to be removed will always be 0 'cos as the nodes are
             // removed the nth node will become (n-1)th
             treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
         }
 
         results.setText("");// Response Data // $NON-NLS-1$
         sampleDataField.setText("");// Request Data // $NON-NLS-1$
         log.debug("End : clear1");
     }
 
     /**
      * Returns the description of this visualizer.
      *
      * @return description of this visualizer
      */
     @Override
     public String toString() {
         String desc = JMeterUtils.getResString("view_results_desc"); //$NON-NLS-1$
 
         if (log.isDebugEnabled()) {
             log.debug("toString1 : Returning description - " + desc);
         }
         return desc;
     }
 
     /**
      * Sets the right pane to correspond to the selected node of the left tree.
      */
     public void valueChanged(TreeSelectionEvent e) {
         log.debug("Start : valueChanged1");
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
         if (log.isDebugEnabled()) {
             log.debug("valueChanged : selected node - " + node);
         }
 
         StyledDocument statsDoc = stats.getStyledDocument();
         try {
             statsDoc.remove(0, statsDoc.getLength());
             sampleDataField.setText(""); // $NON-NLS-1$
             results.setText(""); // $NON-NLS-1$
             if (node != null) {
                 Object userObject = node.getUserObject();
                 if(userObject instanceof SampleResult) {
                     SampleResult res = (SampleResult) userObject;
 
                     // We are displaying a SampleResult
                     setupTabPaneForSampleResult();
 
                     if (log.isDebugEnabled()) {
                         log.debug("valueChanged1 : sample result - " + res);
                     }
 
                     // load time label
 
                     log.debug("valueChanged1 : load time - " + res.getTime());
                     String sd = res.getSamplerData();
                     if (sd != null) {
                         String rh = res.getRequestHeaders();
                         if (rh != null) {
                             StringBuffer sb = new StringBuffer(sd.length() + rh.length()+20);
                             sb.append(sd);
                             sb.append("\n"); //$NON-NLS-1$
                             sb.append(JMeterUtils.getResString("view_results_request_headers")); //$NON-NLS-1$
                             sb.append("\n"); //$NON-NLS-1$
                             sb.append(rh);
                             sd = sb.toString();
                         }
                         sampleDataField.setText(sd);
                     }
 
                     StringBuffer statsBuff = new StringBuffer(200);
                     statsBuff.append(JMeterUtils.getResString("view_results_thread_name")).append(res.getThreadName()).append(NL); //$NON-NLS-1$
                     String startTime = dateFormat.format(new Date(res.getStartTime()));
                     statsBuff.append(JMeterUtils.getResString("view_results_sample_start")).append(startTime).append(NL); //$NON-NLS-1$
                     statsBuff.append(JMeterUtils.getResString("view_results_load_time")).append(res.getTime()).append(NL); //$NON-NLS-1$
                     statsBuff.append(JMeterUtils.getResString("view_results_latency")).append(res.getLatency()).append(NL); //$NON-NLS-1$
                     statsBuff.append(JMeterUtils.getResString("view_results_size_in_bytes")).append(res.getBytes()).append(NL); //$NON-NLS-1$
                     statsBuff.append(JMeterUtils.getResString("view_results_sample_count")).append(res.getSampleCount()).append(NL); //$NON-NLS-1$
                     statsBuff.append(JMeterUtils.getResString("view_results_error_count")).append(res.getErrorCount()).append(NL); //$NON-NLS-1$
                     statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
                     statsBuff = new StringBuffer(); //reset for reuse
 
                     String responseCode = res.getResponseCode();
                     log.debug("valueChanged1 : response code - " + responseCode);
 
                     int responseLevel = 0;
                     if (responseCode != null) {
                         try {
                             responseLevel = Integer.parseInt(responseCode) / 100;
                         } catch (NumberFormatException numberFormatException) {
                             // no need to change the foreground color
                         }
                     }
 
                     Style style = null;
                     switch (responseLevel) {
                     case 3:
                         style = statsDoc.getStyle(STYLE_REDIRECT);
                         break;
                     case 4:
                         style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
                         break;
                     case 5:
                         style = statsDoc.getStyle(STYLE_SERVER_ERROR);
                         break;
                     }
 
                     statsBuff.append(JMeterUtils.getResString("view_results_response_code")).append(responseCode).append(NL); //$NON-NLS-1$
                     statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), style);
                     statsBuff = new StringBuffer(100); //reset for reuse
 
                     // response message label
                     String responseMsgStr = res.getResponseMessage();
 
                     log.debug("valueChanged1 : response message - " + responseMsgStr);
                     statsBuff.append(JMeterUtils.getResString("view_results_response_message")).append(responseMsgStr).append(NL); //$NON-NLS-1$
 
                     statsBuff.append(NL);
                     statsBuff.append(JMeterUtils.getResString("view_results_response_headers")).append(NL); //$NON-NLS-1$
                     statsBuff.append(res.getResponseHeaders()).append(NL);
                     statsBuff.append(NL);
                     final String samplerClass = res.getClass().getName();
                     statsBuff.append(samplerClass.substring(1+samplerClass.lastIndexOf('.'))).append(" " + JMeterUtils.getResString("view_results_fields")).append(NL); //$NON-NLS-1$
                     statsBuff.append("ContentType: ").append(res.getContentType()).append(NL);
                     statsBuff.append("DataEncoding: ").append(res.getDataEncodingNoDefault()).append(NL);
                     statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
                     statsBuff = null; // Done
 
+                    // Reset search
+                    searchTextExtension.resetTextToFind();
+                    
                     // get the text response and image icon
                     // to determine which is NOT null
                     if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null) is OK
                     {
                         String response = getResponseAsString(res);
                         if (command.equals(TEXT_COMMAND)) {
                             showTextResponse(response);
                         } else if (command.equals(HTML_COMMAND)) {
                             showRenderedResponse(response, res);
                         } else if (command.equals(JSON_COMMAND)) {
                             showRenderJSONResponse(response);
                         } else if (command.equals(XML_COMMAND)) {
                             showRenderXMLResponse(res);
                         }
+                        if (!searchTextExtension.isEnabled()) {
+                            searchTextExtension.setEnabled(true);
+                        }
                     } else {
                         byte[] responseBytes = res.getResponseData();
                         if (responseBytes != null) {
                             showImage(new ImageIcon(responseBytes)); //TODO implement other non-text types
+                            if (searchTextExtension.isEnabled()) {
+                                searchTextExtension.setEnabled(false);
+                            }
                         }
                     }
                 }
                 else if(userObject instanceof AssertionResult) {
                     AssertionResult res = (AssertionResult) userObject;
 
                     // We are displaying an AssertionResult
                     setupTabPaneForAssertionResult();
 
                     if (log.isDebugEnabled()) {
                         log.debug("valueChanged1 : sample result - " + res);
                     }
 
                     StringBuffer statsBuff = new StringBuffer(100);
                     statsBuff.append(JMeterUtils.getResString("view_results_assertion_error")).append(res.isError()).append(NL); //$NON-NLS-1$
                     statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure")).append(res.isFailure()).append(NL); //$NON-NLS-1$
                     statsBuff.append(JMeterUtils.getResString("view_results_assertion_failure_message")).append(res.getFailureMessage()).append(NL); //$NON-NLS-1$
                     statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
                     statsBuff = null;
                 }
             }
         } catch (BadLocationException exc) {
             log.error("Error setting statistics text", exc);
             stats.setText("");
         }
         log.debug("End : valueChanged1");
     }
 
     private void showImage(Icon image) {
         imageLabel.setIcon(image);
         resultsScrollPane.setViewportView(imageLabel);
-        textButton.setEnabled(false);
-        htmlButton.setEnabled(false);
-        jsonButton.setEnabled(false);
-        xmlButton.setEnabled(false);
+        setEnabledButtons(false);
     }
 
     private void showTextResponse(String response) {
         results.setContentType("text/plain"); // $NON-NLS-1$
         results.setText(response == null ? "" : response); // $NON-NLS-1$
         results.setCaretPosition(0);
         resultsScrollPane.setViewportView(results);
 
-        textButton.setEnabled(true);
-        htmlButton.setEnabled(true);
-        jsonButton.setEnabled(true);
-        xmlButton.setEnabled(true);
+        setEnabledButtons(true);
     }
 
     // It might be useful also to make this available in the 'Request' tab, for
     // when posting JSON.
     private static String prettyJSON(String json) {
         StringBuffer pretty = new StringBuffer(json.length() * 2); // Educated guess
 
         final String tab = ":   "; // $NON-NLS-1$
         StringBuffer index = new StringBuffer();
         String nl = ""; // $NON-NLS-1$
 
         Matcher valueOrPair = VALUE_OR_PAIR_PATTERN.matcher(json);
 
         boolean misparse = false;
 
         for (int i = 0; i < json.length(); ) {
             final char currentChar = json.charAt(i);
             if ((currentChar == '{') || (currentChar == '[')) {
                 pretty.append(nl).append(index).append(currentChar);
                 i++;
                 index.append(tab);
                 misparse = false;
             }
             else if ((currentChar == '}') || (currentChar == ']')) {
                 if (index.length() > 0) {
                     index.delete(0, tab.length());
                 }
                 pretty.append(nl).append(index).append(currentChar);
                 i++;
                 int j = i;
                 while ((j < json.length()) && Character.isWhitespace(json.charAt(j))) {
                     j++;
                 }
                 if ((j < json.length()) && (json.charAt(j) == ',')) {
                     pretty.append(","); // $NON-NLS-1$
                     i=j+1;
                 }
                 misparse = false;
             }
             else if (valueOrPair.find(i) && valueOrPair.group().length() > 0) {
                 pretty.append(nl).append(index).append(valueOrPair.group());
                 i=valueOrPair.end();
                 misparse = false;
             }
             else {
                 if (!misparse) {
                     pretty.append(nl).append("- Parse failed from:");
                 }
                 pretty.append(currentChar);
                 i++;
                 misparse = true;
             }
             nl = "\n"; // $NON-NLS-1$
         }
         return pretty.toString();
     }
 
     private void showRenderJSONResponse(String response) {
         results.setContentType("text/plain"); // $NON-NLS-1$
         results.setText(response == null ? "" : prettyJSON(response));
         results.setCaretPosition(0);
         resultsScrollPane.setViewportView(results);
 
-        textButton.setEnabled(true);
-        htmlButton.setEnabled(true);
-        jsonButton.setEnabled(true);
-        xmlButton.setEnabled(true);
+        setEnabledButtons(true);
     }
 
     private void showRenderXMLResponse(SampleResult res) {
         results.setContentType("text/xml"); // $NON-NLS-1$
         results.setCaretPosition(0);
         byte[] source = res.getResponseData();
         final ByteArrayInputStream baIS = new ByteArrayInputStream(source);
         for(int i=0; i<source.length-XML_PFX.length; i++){
             if (JOrphanUtils.startsWith(source, XML_PFX, i)){
                 baIS.skip(i);// Skip the leading bytes (if any)
                 break;
             }
         }
         Component view = results;
 
         // there is also a javax.swing.text.Document class.
         org.w3c.dom.Document document = null;
 
         StringWriter sw = new StringWriter();
         Tidy tidy = XPathUtil.makeTidyParser(true, true, true, sw);
         document = tidy.parseDOM(baIS, null);
         document.normalize();
         if (tidy.getParseErrors() > 0) {
             showErrorMessageDialog(sw.toString(),
                     "Tidy: " + tidy.getParseErrors() + " errors, " + tidy.getParseWarnings() + " warnings",
                     JOptionPane.WARNING_MESSAGE);
         }
 
         JPanel domTreePanel = new DOMTreePanel(document);
         view = domTreePanel;
         resultsScrollPane.setViewportView(view);
-        textButton.setEnabled(true);
-        htmlButton.setEnabled(true);
-        jsonButton.setEnabled(true);
-        xmlButton.setEnabled(true);
+        setEnabledButtons(true);
     }
 
     private static String getResponseAsString(SampleResult res) {
 
         String response = null;
         if ((SampleResult.TEXT).equals(res.getDataType())) {
             // Showing large strings can be VERY costly, so we will avoid
             // doing so if the response
             // data is larger than 200K. TODO: instead, we could delay doing
             // the result.setText
             // call until the user chooses the "Response data" tab. Plus we
             // could warn the user
             // if this happens and revert the choice if he doesn't confirm
             // he's ready to wait.
             int len = res.getResponseData().length;
             if (MAX_DISPLAY_SIZE > 0 && len > MAX_DISPLAY_SIZE) {
                 response = JMeterUtils.getResString("view_results_response_too_large_message") //$NON-NLS-1$
                     + len + " > Max: "+MAX_DISPLAY_SIZE;
                 log.warn(response);
             } else {
                 response = res.getResponseDataAsString();
             }
         }
         return response;
     }
 
     /**
      * Display the response as text or as rendered HTML. Change the text on the
      * button appropriate to the current display.
      *
      * @param e
      *            the ActionEvent being processed
      */
     public void actionPerformed(ActionEvent e) {
         command = e.getActionCommand();
 
         if (command != null
                 && (command.equals(TEXT_COMMAND) || command.equals(HTML_COMMAND)
                  || command.equals(JSON_COMMAND) || command.equals(XML_COMMAND))) {
 
             textMode = command.equals(TEXT_COMMAND);
 
             DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
             if (node == null) {
                 results.setText("");
                 return;
             }
-
+            searchTextExtension.resetTextToFind();
+            
             SampleResult res = (SampleResult) node.getUserObject();
             String response = getResponseAsString(res);
 
             if (command.equals(TEXT_COMMAND)) {
                 showTextResponse(response);
             } else if (command.equals(HTML_COMMAND)) {
                 showRenderedResponse(response, res);
             } else if (command.equals(JSON_COMMAND)) {
                 showRenderJSONResponse(response);
             } else if (command.equals(XML_COMMAND)) {
                 showRenderXMLResponse(res);
             }
         }
     }
 
     private void showRenderedResponse(String response, SampleResult res) {
         if (response == null) {
             results.setText("");
             return;
         }
 
         int htmlIndex = response.indexOf("<HTML"); // could be <HTML lang=""> // $NON-NLS-1$
 
         // Look for a case variation
         if (htmlIndex < 0) {
             htmlIndex = response.indexOf("<html"); // ditto // $NON-NLS-1$
         }
 
         // If we still can't find it, just try using all of the text
         if (htmlIndex < 0) {
             htmlIndex = 0;
         }
 
         String html = response.substring(htmlIndex);
 
         /*
          * To disable downloading and rendering of images and frames, enable the
          * editor-kit. The Stream property can then be
          */
 
         // Must be done before setContentType
         results.setEditorKitForContentType(TEXT_HTML, downloadAll.isSelected() ? defaultHtmlEditor : customisedEditor);
 
         results.setContentType(TEXT_HTML);
 
         if (downloadAll.isSelected()) {
             // Allow JMeter to render frames (and relative images)
             // Must be done after setContentType [Why?]
             results.getDocument().putProperty(Document.StreamDescriptionProperty, res.getURL());
         }
 
         /*
          * Get round problems parsing <META http-equiv='content-type'
          * content='text/html; charset=utf-8'> See
          * http://issues.apache.org/bugzilla/show_bug.cgi?id=23315
          *
          * Is this due to a bug in Java?
          */
         results.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE); // $NON-NLS-1$
 
         results.setText(html);
         results.setCaretPosition(0);
         resultsScrollPane.setViewportView(results);
 
-        textButton.setEnabled(true);
-        htmlButton.setEnabled(true);
-        jsonButton.setEnabled(true);
-        xmlButton.setEnabled(true);
+        setEnabledButtons(true);
     }
 
     private Component createHtmlOrTextPane() {
         ButtonGroup group = new ButtonGroup();
+        Font font = new Font("SansSerif", Font.PLAIN, 10);
+        Font fontBold = new Font("SansSerif", Font.BOLD, 10);
+
+        renderLabel = new JLabel(JMeterUtils
+                .getResString("view_results_render")); // $NON-NLS-1$
+        renderLabel.setFont(fontBold);
 
         textButton = new JRadioButton(JMeterUtils.getResString("view_results_render_text")); // $NON-NLS-1$
+        textButton.setFont(font);
         textButton.setActionCommand(TEXT_COMMAND);
         textButton.addActionListener(this);
         textButton.setSelected(textMode);
         group.add(textButton);
 
         htmlButton = new JRadioButton(JMeterUtils.getResString("view_results_render_html")); // $NON-NLS-1$
+        htmlButton.setFont(font);
         htmlButton.setActionCommand(HTML_COMMAND);
         htmlButton.addActionListener(this);
         htmlButton.setSelected(!textMode);
         group.add(htmlButton);
 
         jsonButton = new JRadioButton(JMeterUtils.getResString("view_results_render_json")); // $NON-NLS-1$
+        jsonButton.setFont(font);
         jsonButton.setActionCommand(JSON_COMMAND);
         jsonButton.addActionListener(this);
         jsonButton.setSelected(!textMode);
         group.add(jsonButton);
 
         xmlButton = new JRadioButton(JMeterUtils.getResString("view_results_render_xml")); // $NON-NLS-1$
+        xmlButton.setFont(font);
         xmlButton.setActionCommand(XML_COMMAND);
         xmlButton.addActionListener(this);
         xmlButton.setSelected(!textMode);
         group.add(xmlButton);
 
         downloadAll = new JCheckBox(JMeterUtils.getResString("view_results_render_embedded")); // $NON-NLS-1$
-
+        downloadAll.setFont(font);
+        
         JPanel pane = new JPanel();
+        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
+        pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
+        pane.add(searchTextExtension.createShowSearchPanel());
+        pane.add(Box.createRigidArea(new Dimension(30, 0)));
+        pane.add(renderLabel);
         pane.add(textButton);
         pane.add(htmlButton);
         pane.add(xmlButton);
         pane.add(jsonButton);
         pane.add(downloadAll);
+        
         return pane;
     }
 
     /**
      * Initialize this visualizer
      */
     private void init() {
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         Component leftSide = createLeftPanel();
         rightSide = new JTabbedPane();
         // Add the common tab
         rightSide.addTab(JMeterUtils.getResString("view_results_tab_sampler"), createResponseMetadataPanel()); // $NON-NLS-1$
         // Create the panels for the other tabs
         requestPane = createRequestPanel();
         resultsPane = createResponseDataPanel();
 
         JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
         add(mainSplit, BorderLayout.CENTER);
+        
+        searchTextExtension.init(resultsPane);
     }
 
     private void setupTabPaneForSampleResult() {
         // Set the title for the first tab
         rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_sampler")); //$NON-NLS-1$
         // Add the other tabs if not present
         if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")) < 0) { // $NON-NLS-1$
             rightSide.addTab(JMeterUtils.getResString("view_results_tab_request"), requestPane); // $NON-NLS-1$
         }
         if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")) < 0) { // $NON-NLS-1$
             rightSide.addTab(JMeterUtils.getResString("view_results_tab_response"), resultsPane); // $NON-NLS-1$
         }
     }
 
     private void setupTabPaneForAssertionResult() {
         // Set the title for the first tab
         rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_assertion")); //$NON-NLS-1$
         // Remove the other tabs if present
         int requestTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")); // $NON-NLS-1$
         if(requestTabIndex >= 0) {
             rightSide.removeTabAt(requestTabIndex);
         }
         int responseTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")); // $NON-NLS-1$
         if(responseTabIndex >= 0) {
             rightSide.removeTabAt(responseTabIndex);
         }
     }
 
     private Component createLeftPanel() {
         SampleResult rootSampleResult = new SampleResult();
         rootSampleResult.setSampleLabel("Root");
         rootSampleResult.setSuccessful(true);
         root = new DefaultMutableTreeNode(rootSampleResult);
 
         treeModel = new DefaultTreeModel(root);
         jTree = new JTree(treeModel);
         jTree.setCellRenderer(new ResultsNodeRenderer());
         jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
         jTree.addTreeSelectionListener(this);
         jTree.setRootVisible(false);
         jTree.setShowsRootHandles(true);
 
         JScrollPane treePane = new JScrollPane(jTree);
         treePane.setPreferredSize(new Dimension(200, 300));
         return treePane;
     }
 
     private Component createResponseMetadataPanel() {
         stats = new JTextPane();
         stats.setEditable(false);
         stats.setBackground(getBackground());
 
         // Add styles to use for different types of status messages
         StyledDocument doc = (StyledDocument) stats.getDocument();
 
         Style style = doc.addStyle(STYLE_REDIRECT, null);
         StyleConstants.setForeground(style, REDIRECT_COLOR);
 
         style = doc.addStyle(STYLE_CLIENT_ERROR, null);
         StyleConstants.setForeground(style, CLIENT_ERROR_COLOR);
 
         style = doc.addStyle(STYLE_SERVER_ERROR, null);
         StyleConstants.setForeground(style, SERVER_ERROR_COLOR);
 
         JScrollPane pane = makeScrollPane(stats);
         pane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
         return pane;
     }
 
     private JPanel createRequestPanel() {
         sampleDataField = new JTextArea();
         sampleDataField.setEditable(false);
         sampleDataField.setLineWrap(true);
         sampleDataField.setWrapStyleWord(true);
 
         JPanel pane = new JPanel(new BorderLayout(0, 5));
         pane.add(makeScrollPane(sampleDataField));
         return pane;
     }
 
     private JPanel createResponseDataPanel() {
         results = new JEditorPane();
         results.setEditable(false);
 
         resultsScrollPane = makeScrollPane(results);
         imageLabel = new JLabel();
 
+        // Add search text extension
+        searchTextExtension = new SearchTextExtension();
+        searchPanel = searchTextExtension.createSearchTextPanel();
+        searchTextExtension.setResults(results);
+        searchPanel.setVisible(false);
+        JPanel panelSouth = new JPanel(new BorderLayout());
+        panelSouth.add(createHtmlOrTextPane(), BorderLayout.CENTER);
+        panelSouth.add(searchPanel, BorderLayout.SOUTH);
+        
         JPanel panel = new JPanel(new BorderLayout());
         panel.add(resultsScrollPane, BorderLayout.CENTER);
-        panel.add(createHtmlOrTextPane(), BorderLayout.SOUTH);
+        panel.add(panelSouth, BorderLayout.SOUTH);
 
         return panel;
     }
 
+    private void setEnabledButtons(boolean b) {
+        renderLabel.setEnabled(b); 
+        textButton.setEnabled(b);
+        htmlButton.setEnabled(b);
+        jsonButton.setEnabled(b);
+        xmlButton.setEnabled(b);
+        if (b && command.equals(HTML_COMMAND)) {
+            downloadAll.setEnabled(b);
+        } else {
+            downloadAll.setEnabled(false);
+        }
+    }
+
     private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
         @Override
         public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                 boolean leaf, int row, boolean focus) {
             super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
             boolean failure = true;
             Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
             if(userObject instanceof SampleResult) {
                 failure = !(((SampleResult) userObject).isSuccessful());
             }
             else if(userObject instanceof AssertionResult) {
                 AssertionResult assertion = (AssertionResult) userObject;
                 failure =  assertion.isError() || assertion.isFailure();
             }
 
             // Set the status for the node
             if (failure) {
                 this.setForeground(Color.red);
                 this.setIcon(imageFailure);
             } else {
                 this.setIcon(imageSuccess);
             }
             return this;
         }
     }
 
     private static class LocalHTMLEditorKit extends HTMLEditorKit {
 
         private static final ViewFactory defaultFactory = new LocalHTMLFactory();
 
         @Override
         public ViewFactory getViewFactory() {
             return defaultFactory;
         }
 
         private static class LocalHTMLFactory extends javax.swing.text.html.HTMLEditorKit.HTMLFactory {
             /*
              * Provide dummy implementations to suppress download and display of
              * related resources: - FRAMEs - IMAGEs TODO create better dummy
              * displays TODO suppress LINK somehow
              */
             @Override
             public View create(Element elem) {
                 Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
                 if (o instanceof HTML.Tag) {
                     HTML.Tag kind = (HTML.Tag) o;
                     if (kind == HTML.Tag.FRAME) {
                         return new ComponentView(elem);
                     } else if (kind == HTML.Tag.IMG) {
                         return new ComponentView(elem);
                     }
                 }
                 return super.create(elem);
             }
         }
     }
 
     /**
      *
      * A Dom tree panel for to display response as tree view author <a
      * href="mailto:d.maung@mdl.com">Dave Maung</a> TODO implement to find any
      * nodes in the tree using TreePath.
      *
      */
     private static class DOMTreePanel extends JPanel {
 
         private JTree domJTree;
 
         public DOMTreePanel(org.w3c.dom.Document document) {
             super(new GridLayout(1, 0));
             try {
                 Node firstElement = getFirstElement(document);
                 DefaultMutableTreeNode top = new XMLDefaultMutableTreeNode(firstElement);
                 domJTree = new JTree(top);
 
                 domJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                 domJTree.setShowsRootHandles(true);
                 JScrollPane domJScrollPane = new JScrollPane(domJTree);
                 domJTree.setAutoscrolls(true);
                 this.add(domJScrollPane);
                 ToolTipManager.sharedInstance().registerComponent(domJTree);
                 domJTree.setCellRenderer(new DomTreeRenderer());
             } catch (SAXException e) {
                 log.warn("", e);
             }
 
         }
 
         /**
          * Skip all DTD nodes, all prolog nodes. They dont support in tree view
          * We let user to insert them however in DOMTreeView, we dont display it
          *
          * @param root
          * @return
          */
         private Node getFirstElement(Node parent) {
             NodeList childNodes = parent.getChildNodes();
             Node toReturn = parent; // Must return a valid node, or may generate an NPE
             for (int i = 0; i < childNodes.getLength(); i++) {
                 Node childNode = childNodes.item(i);
                 toReturn = childNode;
                 if (childNode.getNodeType() == Node.ELEMENT_NODE){
                     break;
                 }
 
             }
             return toReturn;
         }
 
         /**
          * This class is to view as tooltext. This is very useful, when the
          * contents has long string and does not fit in the view. it will also
          * automatically wrap line for each 100 characters since tool tip
          * support html. author <a href="mailto:d.maung@mdl.com">Dave Maung</a>
          */
         private static class DomTreeRenderer extends DefaultTreeCellRenderer {
             @Override
             public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                     boolean leaf, int row, boolean phasFocus) {
                 super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, phasFocus);
 
                 DefaultMutableTreeNode valueTreeNode = (DefaultMutableTreeNode) value;
                 setToolTipText(getHTML(valueTreeNode.toString(), "<br>", 100)); // $NON-NLS-1$
                 return this;
             }
 
             /**
              * get the html
              *
              * @param str
              * @param separator
              * @param maxChar
              * @return
              */
             private String getHTML(String str, String separator, int maxChar) {
                 StringBuffer strBuf = new StringBuffer("<html><body bgcolor=\"yellow\"><b>"); // $NON-NLS-1$
                 char[] chars = str.toCharArray();
                 for (int i = 0; i < chars.length; i++) {
 
                     if (i % maxChar == 0 && i != 0) {
                         strBuf.append(separator);
                     }
                     strBuf.append(encode(chars[i]));
 
                 }
                 strBuf.append("</b></body></html>"); // $NON-NLS-1$
                 return strBuf.toString();
 
             }
 
             private String encode(char c) {
                 String toReturn = String.valueOf(c);
                 switch (c) {
                 case '<': // $NON-NLS-1$
                     toReturn = "&lt;"; // $NON-NLS-1$
                     break;
                 case '>': // $NON-NLS-1$
                     toReturn = "&gt;"; // $NON-NLS-1$
                     break;
                 case '\'': // $NON-NLS-1$
                     toReturn = "&apos;"; // $NON-NLS-1$
                     break;
                 case '\"': // $NON-NLS-1$
                     toReturn = "&quot;"; // $NON-NLS-1$
                     break;
 
                 }
                 return toReturn;
             }
         }
     }
 
     private static void showErrorMessageDialog(String message, String title, int messageType) {
         JOptionPane.showMessageDialog(null, message, title, messageType);
     }
 
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index ca4d2026a..5e3a47950 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,956 +1,966 @@
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
 add=Add
 add_as_child=Add as Child
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 aggregate_graph=Statistical Graphs
 aggregate_graph_column=Column
 aggregate_graph_display=Display Graph
 aggregate_graph_height=Height
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label
 aggregate_graph_ms=Milliseconds
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_use_group_name=Include group name in label?
 aggregate_graph_user_title=Title for Graph
 aggregate_graph_width=Width
 aggregate_report=Aggregate Report
 aggregate_report_90=90%
 aggregate_report_90%_line=90% Line
 aggregate_report_bandwidth=KB/sec
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
 argument_must_not_be_negative=The Argument must not be negative\!
 assertion_assume_success=Ignore Status
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_not=Not
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_resp_field=Response Field to Test
 assertion_scope=Which samples to test
 assertion_scope_all=Main sample and sub-samples
 assertion_scope_children=Sub-samples only
 assertion_scope_parent=Main sample only
 assertion_substring=Substring
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 bind=Thread Bind
 browse=Browse...
 bsf_sampler_title=BSF Sampler
 bsf_script=Script to run (variables: log, Label, FileName, Parameters, args[], SampleResult, ctx, vars, props, OUT)
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
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 char_value=Unicode character number (decimal or 0xhex)
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cache_per_iter=Clear cache each iteration?
 clear_cookies_per_iter=Clear cookies each iteration?
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
 compare=Compare
 comparefilt=Compare filter
 comparison_visualizer_title=Comparison Assertion Visualizer
 config_element=Config Element
 config_save_settings=Configure
 configure_wsdl=Configure
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_manager_policy=Cookie Policy
 cookie_manager_title=HTTP Cookie Manager
 cookies_stored=User-Defined Cookies
 copy=Copy
 corba_config_title=CORBA Sampler Config
 corba_input_data_file=Input Data File\:
 corba_methods=Choose method to invoke\:
 corba_name_server=Name Server\:
 corba_port=Port Number\:
 corba_request_data=Input Data
 corba_sample_title=CORBA Sampler
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 countlim=Size limit
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
 delete=Delete
 delete_parameter=Delete Variable
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 disable=Disable
 distribution_graph_title=Distribution Graph (alpha)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 domain=Domain
 done=Done
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Enable
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
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
 expiration=Expiration
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
 foreach_controller_title=ForEach Controller
 foreach_input=Input variable prefix
 foreach_output=Output variable name
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
 get_xml_from_file=File with SOAP XML Data (overrides above text)
 get_xml_from_random=Message Folder
 get_xml_message=Note\: Parsing XML is CPU intensive. Therefore, do not set the thread count
 get_xml_message2=too high. In general, 10 threads will consume 100% of the CPU on a 900MHz
 get_xml_message3=Pentium 3. On a Pentium 4 2.4GHz cpu, 50 threads is the upper limit. Your
 get_xml_message4=options for increasing the number of clients is to increase the number of
 get_xml_message5=machines or use multi-cpu systems.
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 help=Help
 help_node=What's this node?
 html_assertion_file=Write JTidy report to file
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_parameter_mask=HTML Parameter Mask
 http_implementation=HTTP Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
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
 jms_client_caption=Receiver client uses TopicSubscriber.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_type=Client
 jms_communication_style=Communication style
 jms_concrete_connection_factory=Concrete Connection Factory
 jms_config=Configuration
 jms_config_title=JMS Configuration
 jms_connection_factory=Connection Factory
 jms_error_msg=Object message should read from an external file. Text input is currently selected, please remember to change it.
 jms_file=File
 jms_initial_context_factory=Initial Context Factory
 jms_itertions=Number of samples to aggregate
 jms_jndi_defaults_title=JNDI Default Configuration
 jms_jndi_props=JNDI Properties
 jms_message_title=Message properties
 jms_message_type=Message Type
 jms_msg_content=Content
 jms_object_message=Object Message
 jms_point_to_point=JMS Point-to-Point
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Random File
 jms_read_response=Read Response
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title=JMS Default Request
 jms_send_queue=JNDI name Request queue
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use TopicSubscriber.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_message=Text Message
 jms_timeout=Timeout (milliseconds)
 jms_topic=Topic
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File
 jms_use_req_msgid_as_correlid=Use Request Message Id As Correlation Id
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
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
 junit_do_setup_teardown=Do not call setUp and tearDown
 junit_error_code=Error Code
 junit_error_default_code=9999
 junit_error_default_msg=An unexpected error occured
 junit_error_msg=Error Message
 junit_failure_code=Failure Code
 junit_failure_default_code=0001
 junit_failure_default_msg=Test failed
 junit_failure_msg=Failure Message
 junit_pkg_filter=Package Filter
 junit_request=JUnit Request
 junit_request_defaults=JUnit Request Defaults
 junit_success_code=Success Code
 junit_success_default_code=1000
 junit_success_default_msg=Test successful
 junit_success_msg=Success Message
 junit_test_config=JUnit Test Parameters
 junit_test_method=Test Method
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
 load_wsdl=Load WSDL
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
 mail_reader_imap=IMAP
 mail_reader_imaps=IMAPS
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_pop3=POP3
 mail_reader_pop3s=POP3S
 mail_reader_server=Server:
 mail_reader_server_type=Server Type:
 mail_reader_storemime=Store the message using MIME
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_attributes_panel=Mailing attributes
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\:
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 memory_cache=Memory Cache
 menu_assertions=Assertions
 menu_close=Close
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_tables=Table
 menu_timer=Timer
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
 monitor_equation_active=Active:  (busy/max) > 25%
 monitor_equation_dead=Dead:  no response
 monitor_equation_healthy=Healthy:  (busy/max) < 25%
 monitor_equation_load=Load:  ( (busy / max) * 50) + ( (used memory / max memory) * 50)
 monitor_equation_warning=Warning:  (busy/max) > 67%
 monitor_health_tab_title=Health
 monitor_health_title=Monitor Results
 monitor_is_title=Use as Monitor
 monitor_label_left_bottom=0 %
 monitor_label_left_middle=50 %
 monitor_label_left_top=100 %
 monitor_label_prefix=Connection Prefix
 monitor_label_right_active=Active
 monitor_label_right_dead=Dead
 monitor_label_right_healthy=Healthy
 monitor_label_right_warning=Warning
 monitor_legend_health=Health
 monitor_legend_load=Load
 monitor_legend_memory_per=Memory % (used/total)
 monitor_legend_thread_per=Thread % (busy/max)
 monitor_load_factor_mem=50
 monitor_load_factor_thread=50
 monitor_performance_servers=Servers
 monitor_performance_tab_title=Performance
 monitor_performance_title=Performance Graph
 name=Name\:
 new=New
 newdn=New distinguished name
 no=Norwegian
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
 port=Port\:
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip={0}\: {1}
 property_undefined=Undefined
 property_value_param=Value of property
 property_visualiser_title=Property Display
 protocol=Protocol (default http)\:
 protocol_java_border=Java class
 protocol_java_classname=Classname\:
 protocol_java_config_tile=Configure Java Sample
 protocol_java_test_title=Java Testing
 provider_url=Provider URL
 proxy_assertions=Add Assertions
 proxy_cl_error=If specifying a proxy server, host and port must be given
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_daemon_bind_error=Could not create proxy - port in use. Choose another port.
 proxy_daemon_error=Could not create proxy - see log for details
 proxy_headers=Capture HTTP Headers
 proxy_httpsspoofing=Attempt HTTPS Spoofing
 proxy_httpsspoofing_match=Optional URL match string:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP Proxy Server
 pt_br=Portugese (Brazilian)
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 read_response_message=Read response is not checked. To see the response, please check the box in the sampler.
 read_response_note=If read response is unchecked, the sampler will not read the response
 read_response_note2=or set the SampleResult. This improves performance, but it means
 read_response_note3=the response content won't be logged.
 read_soap_response=Read SOAP Response
 realm=Realm
 record_controller_title=Recording Controller
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_source=Response Field to check
 regex_src_body=Body
 regex_src_body_unescaped=Body (unescaped)
 regex_src_hdrs=Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search previous sample - or variable.
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 regexfunc_param_7=Input variable name containing the text to be parsed ([previous sample])
 remote_error_init=Error initialising remote server
 remote_error_starting=Error starting remote server
 remote_exit=Remote Exit
 remote_exit_all=Remote Exit All
 remote_start=Remote Start
 remote_start_all=Remote Start All
 remote_stop=Remote Stop
 remote_stop_all=Remote Stop All
 remove=Remove
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
 request_data=Request Data
 reset_gui=Reset Gui
 response_save_as_md5=Save response as MD5 hash?
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_errors=Save Failed Responses only
 resultsaver_prefix=Filename prefix\:
 resultsaver_skipautonumber=Don't add number to prefix
 resultsaver_skipsuffix=Don't add suffix
 resultsaver_success=Save Successful Responses only
 resultsaver_title=Save Responses to a file
 resultsaver_variable=Variable Name:
 retobj=Return object
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
 root=Root
 root_title=Root
 run=Run
 running_test=Running test
 runtime_controller_title=Runtime Controller
 runtime_seconds=Runtime (seconds)
 sample_result_save_configuration=Sample Result Save Configuration
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_test_now=Stop Test Now
 sampler_on_error_stop_thread=Stop Thread
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save byte count
 save_code=Save Response Code
 save_datatype=Save Data Type
 save_encoding=Save Encoding
 save_fieldnames=Save Field Names (CSV)
 save_filename=Save Response Filename
 save_graphics=Save Graph
 save_hostname=Save Hostname
 save_label=Save Label
 save_latency=Save Latency
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplecount=Save Sample and Error Counts
 save_samplerdata=Save Sampler Data (XML)
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search_base=Search base
 search_filter=Search Filter
 search_test=Search Test
+search_text_button_close=Close
+search_text_button_find=Find
+search_text_button_next=Find next
+search_text_chkbox_case=Case sensitive
+search_text_chkbox_regexp=Regular exp.
+search_text_field=Search: 
+search_text_msg_not_found=Text not found
+search_text_title_not_found=Not found
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send Files With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=File Path\:
 send_file_mime_label=MIME Type\:
 send_file_param_name_label=Parameter Name\:
 server=Server Name or IP\:
 servername=Servername \:
 session_argument_name=Session Argument Name
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
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_title=SOAP/XML-RPC Request
 soap_send_action=Send SOAPAction: 
 spline_visualizer_average=Average
 spline_visualizer_incoming=Incoming
 spline_visualizer_maximum=Maximum
 spline_visualizer_minimum=Minimum
 spline_visualizer_title=Spline Visualizer
 spline_visualizer_waitingmessage=Waiting for samples
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
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads.  Please be patient.
 stopping_test_failed=One or more test threads won't exit; see log file.
 stopping_test_title=Stopping Test
 string_from_file_file_name=Enter full path to file
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 table_visualizer_bytes=Bytes
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
 table_visualizer_start_time=Start Time
 table_visualizer_status=Status
 table_visualizer_success=Success
 table_visualizer_thread_name=Thread Name
 table_visualizer_warning=Warning
 tcp_classname=TCPClient classname\:
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 template_field=Template\:
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
 test_action_pause=Pause
 test_action_stop=Stop
 test_action_stop_now=Stop Now
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
 test_plan=Test Plan
 test_plan_classpath_browse=Add directory or jar to classpath
 testconfiguration=Test Configuration
 testplan.serialized=Run Thread Groups consecutively (i.e. run groups one at a time)
 testplan_comments=Comments\:
 testt=Test
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
 tr=Turkish
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 unbind=Thread Unbind
 unescape_html_string=String to unescape
 unescape_string=String containing Java escapes
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
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
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for HTTP POST
 use_recording_controller=Use Recording Controller
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 value=Value
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_assertion_error=Assertion error: 
 view_results_assertion_failure=Assertion failure: 
 view_results_assertion_failure_message=Assertion failure message: 
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_load_time=Load time: 
+view_results_render=Render: 
 view_results_render_embedded=Download embedded resources
-view_results_render_html=Render HTML
-view_results_render_json=Render JSON
-view_results_render_text=Show Text
-view_results_render_xml=Render XML
+view_results_render_html=HTML
+view_results_render_json=JSON
+view_results_render_text=Text
+view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_too_large_message=Response too large to be displayed. Size: 
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
+view_results_search_pane=Search pane
 view_results_size_in_bytes=Size in bytes: 
 view_results_tab_assertion=Assertion result
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
 view_results_thread_name=Thread Name: 
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_proxy_server_title=Proxy Server
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_testing2_title=HTTP Request HTTPClient
 web_testing_embedded_url_pattern=Embedded URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources from HTML Files
 web_testing_title=HTTP Request
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
 webservice_proxy_host=Proxy Host
 webservice_proxy_note=If Use HTTP Proxy is checked, but no host or port are provided, the sampler
 webservice_proxy_note2=will look at command line options. If no proxy host or port are provided by
 webservice_proxy_note3=either, it will fail silently.
 webservice_proxy_port=Proxy Port
 webservice_sampler_title=WebService(SOAP) Request
 webservice_soap_action=SOAPAction
 webservice_timeout=Timeout:
 webservice_use_proxy=Use HTTP Proxy
 while_controller_label=Condition (function or variable)
 while_controller_title=While Controller
 workbench_title=WorkBench
 wsdl_helper_error=The WSDL was not valid, please double check the url.
 wsdl_url=WSDL URL
 wsdl_url_error=The WSDL was emtpy.
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
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_file_file_name=XML file to get values from 
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
diff --git a/src/core/org/apache/jmeter/resources/messages_fr.properties b/src/core/org/apache/jmeter/resources/messages_fr.properties
index 6e2537cf1..ae652d1dd 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,796 +1,806 @@
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
 add=Ajouter
 add_as_child=Ajouter en tant qu'enfant
 add_parameter=Ajouter un param\u00E8tre
 add_pattern=Ajouter un motif \:
 add_test=Ajout
 add_user=Ajouter un utilisateur
 add_value=Ajouter valeur
 aggregate_graph=Graphique des statistiques
 aggregate_graph_column=Colonne
 aggregate_graph_display=G\u00E9n\u00E9rer le graphique
 aggregate_graph_height=Hauteur \:
 aggregate_graph_max_length_xaxis_label=Longueur maximum du libell\u00E9 de l'axe des abscisses \:
 aggregate_graph_ms=Millisecondes
 aggregate_graph_response_time=Temps de r\u00E9ponse
 aggregate_graph_save=Enregistrer le graphique
 aggregate_graph_save_table=Enregistrer le tableau de donn\u00E9es
 aggregate_graph_save_table_header=Inclure l'ent\u00EAte du tableau
 aggregate_graph_title=Graphique agr\u00E9g\u00E9
 aggregate_graph_use_group_name=Ajouter le nom du groupe d'unit\u00E9s aux libell\u00E9s des \u00E9chantillons ?
 aggregate_graph_user_title=Titre du graphique \:
 aggregate_graph_width=Largeur \:
 aggregate_report=Rapport agr\u00E9g\u00E9
 aggregate_report_90=90%
 aggregate_report_90%_line=90e centile
 aggregate_report_bandwidth=Ko/sec
 aggregate_report_count=\# Echantillons
 aggregate_report_error=Erreur
 aggregate_report_error%=% Erreur
 aggregate_report_max=Max
 aggregate_report_median=M\u00E9diane
 aggregate_report_min=Min
 aggregate_report_rate=D\u00E9bit
 aggregate_report_stddev=Ecart type
 ajp_sampler_title=Requ\u00EAte AJP/1.3
 als_message=Note \: Le parseur de log d'acc\u00E8s est g\u00E9n\u00E9rique et vous permet de se brancher \u00E0 
 als_message2=votre propre parseur. Pour se faire, impl\u00E9menter le LogParser, ajouter le jar au 
 als_message3=r\u00E9pertoire /lib et entrer la classe (fichier .class) dans l'\u00E9chantillon (sampler).
 analyze=En train d'analyser le fichier de donn\u00E9es
 anchor_modifier_title=Analyseur de lien HTML
 appearance=Apparence
 argument_must_not_be_negative=L'argument ne peut pas \u00EAtre n\u00E9gatif \!
 assertion_assume_success=Ignorer le statut
 assertion_code_resp=Code de r\u00E9ponse
 assertion_contains=Contient
 assertion_equals=Est \u00E9gale \u00E0
 assertion_headers=Ent\u00EAtes de r\u00E9ponse
 assertion_matches=Correspond \u00E0
 assertion_message_resp=Message de r\u00E9ponse
 assertion_not=Inverser
 assertion_pattern_match_rules=Type de correspondance du motif
 assertion_patterns_to_test=Motifs \u00E0 tester
 assertion_resp_field=Section de r\u00E9ponse \u00E0 tester
 assertion_scope=Port\u00E9e de l'assertion
 assertion_scope_all=L'\u00E9chantillon parent et ses sous-\u00E9chantillons
 assertion_scope_children=Seulement les sous-\u00E9chantillons
 assertion_scope_parent=Seulement l'\u00E9chantillon parent
 assertion_substring=Commence par
 assertion_text_resp=Texte de r\u00E9ponse
 assertion_textarea_label=Assertions \:
 assertion_title=Assertion R\u00E9ponse
 assertion_url_samp=URL Echantillon
 assertion_visualizer_title=R\u00E9sultats d'assertion
 auth_base_url=URL de base
 auth_manager_title=Gestionnaire d'autorisation HTTP
 auths_stored=Autorisations stock\u00E9es
 average=Moyenne
 average_bytes=Moy. octets
 bind=Unit\u00E9 li\u00E9e
 browse=Parcourir...
 bsf_sampler_title=Echantillon BSF
 bsf_script=Script \u00E0 lancer (variables\: log, Label, FileName, Parameters, args[], SampleResult, ctx, vars, props, OUT) \:
 bsf_script_file=Fichier script \u00E0 lancer \:
 bsf_script_language=Langage de script \:
 bsf_script_parameters=Param\u00E8tres \u00E0 passer au script/fichier \:
 bsh_assertion_script=Script (IO\: Failure[Message], Response. IN\: Response[Data|Code|Message|Headers], RequestHeaders, Sample[Label|rData])
 bsh_assertion_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nEn lecture/\u00E9criture \: Failure, FailureMessage, SampleResult, vars, props, log.\nEn lecture seule \: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=Assertion BeanShell
 bsh_function_expression=Expression \u00E0 \u00E9valuer
 bsh_sampler_title=Echantillon BeanShell
 bsh_script=Script (voir la suite pour les variables qui sont d\u00E9finies)
 bsh_script_file=Fichier de script \:
 bsh_script_parameters=Param\u00E8tres  (-> String Parameters et String []bsh.args)
 bsh_script_reset_interpreter=R\u00E9initialiser l'interpr\u00E9teur bsh avant chaque appel
 bsh_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Je suis occup\u00E9 \u00E0 tester, veuillez arr\u00EAter le test avant de changer le param\u00E8trage
 cache_manager_title=Gestionnaire de cache HTTP
 cache_session_id=Identifiant de session de cache ?
 cancel=Annuler
 cancel_exit_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de sortir ?
 cancel_new_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de nettoyer le plan de test ?
 cancel_revert_project=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Annuler les changements et revenir \u00E0 la derni\u00E8re sauvegarde du plan de test ?
 choose_function=Choisir une fonction
 choose_language=Choisir une langue
 clear=Nettoyer
 clear_all=Nettoyer tout
 clear_cache_per_iter=Nettoyer le cache \u00E0 chaque it\u00E9ration ?
 clear_cookies_per_iter=Nettoyer les cookies \u00E0 chaque it\u00E9ration ?
 column_delete_disallowed=Supprimer cette colonne n'est pas possible
 column_number=Num\u00E9ro de colonne du fichier CSV | next | *alias
 config_element=El\u00E9ment de configuration
 config_save_settings=Configurer
 configure_wsdl=Configurer
 constant_throughput_timer_memo=Ajouter un d\u00E9lai entre les \u00E9chantillions pour obtenir un d\u00E9bit constant
 constant_timer_delay=D\u00E9lai d'attente (en millisecondes) \:
 constant_timer_memo=Ajouter un d\u00E9lai fixe entre les \u00E9chantillions de test
 constant_timer_title=Compteur de temps fixe
 content_encoding=Encodage du contenu \:
 controller=Contr\u00F4leur
 cookie_manager_policy=Politique des cookies
 cookie_manager_title=Gestionnaire de cookies HTTP
 cookies_stored=Cookies stock\u00E9s
 copy=Copier
 corba_port=Num\u00E9ro de port \:
 counter_config_title=Compteur
 counter_per_user=Suivre le compteur ind\u00E9pendamment pour chaque unit\u00E9 de test
 csvread_file_file_name=Fichier CSV pour obtenir les valeurs de | *alias
 cut=Couper
 cut_paste_function=Fonction de copier/coller de cha\u00EEne de caract\u00E8re
 database_sql_query_string=Requ\u00EAte SQL \:
 database_sql_query_title=Requ\u00EAte SQL JDBC par d\u00E9faut
 ddn=DN \:
 de=Allemand
 debug_off=D\u00E9sactiver le d\u00E9bogage
 debug_on=Activer le d\u00E9bogage
 default_parameters=Param\u00E8tres par d\u00E9faut
 default_value_field=Valeur par d\u00E9faut \:
 delay=D\u00E9lai avant d\u00E9marrage (secondes) \:
 delete=Supprimer
 delete_parameter=Supprimer le param\u00E8tre
 delete_test=Suppression
 delete_user=Supprimer l'utilisateur
 disable=D\u00E9sactiver
 distribution_graph_title=Graphique de distribution (alpha)
 distribution_note1=Ce graphique se mettra \u00E0 jour tous les 10 \u00E9chantillons
 dn=DN \:
 domain=Domaine \:
 duration=Dur\u00E9e (secondes) \:
 duration_assertion_duration_test=Dur\u00E9e maximale \u00E0 v\u00E9rifier
 duration_assertion_failure=L''op\u00E9ration a dur\u00E9e trop longtemps\: cela a pris {0} millisecondes, mais n''aurait pas d\u00FB durer plus de {1} millisecondes.
 duration_assertion_input_error=Veuillez entrer un entier positif valide.
 duration_assertion_label=Dur\u00E9e en millisecondes \:
 duration_assertion_title=Assertion Dur\u00E9e
 edit=Editer
 email_results_title=R\u00E9sultat d'email
 en=Anglais
 enable=Activer
 encode?=Encodage
 encoded_value=Valeur de l'URL encod\u00E9e
 endtime=Date et heure de fin \:
 entry_dn=Entr\u00E9e DN \:
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
 file_visualizer_output_file=Ecrire les donn\u00E9es dans un fichier
 file_visualizer_submit_data=Inclure les donn\u00E9es envoy\u00E9es
 file_visualizer_title=Rapporteur de fichier
 file_visualizer_verbose=Sortie verbeuse
 filename=Nom de fichier \: 
 follow_redirects=Suivre les redirections
 follow_redirects_auto=Rediriger automatiquement
 foreach_controller_title=Contr\u00F4leur Pour chaque (ForEach)
 foreach_input=Pr\u00E9fixe de la variable d'entr\u00E9e \:
 foreach_output=Nom de la variable de sortie \:
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
 function_params=Param\u00E8tres de la fonction
 functional_mode=Mode de test fonctionnel
 functional_mode_explanation=S\u00E9lectionner le mode de test fonctionnel uniquement si vous avez besoin\nd'enregistrer les donn\u00E9es re\u00E7ues du serveur dans un fichier \u00E0 chaque requ\u00EAte. \n\nS\u00E9lectionner cette option affecte consid\u00E9rablement les performances.
 gaussian_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 gaussian (en millisecondes) \:
 gaussian_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution gaussienne
 gaussian_timer_range=D\u00E9calage (en millisecondes) \:
 gaussian_timer_title=Compteur de temps al\u00E9atoire gaussien
 generate=G\u00E9n\u00E9rer
 generator=Nom de la classe g\u00E9n\u00E9ratrice
 generator_cnf_msg=N'a pas p\u00FB trouver la classe g\u00E9n\u00E9ratrice. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 generator_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classes g\u00E9n\u00E9ratrice \u00E0 cause d'une IllegalAccessException.
 generator_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur g\u00E9n\u00E9rateur. Assurez-vous que le g\u00E9n\u00E9rateur impl\u00E9mente l'interface Generator.
 get_xml_from_file=Fichier avec les donn\u00E9es XML SOAP (remplace le texte ci-dessus)
 get_xml_from_random=R\u00E9pertoire de message
 get_xml_message=Note \: l'analyseur XML consomme du temps CPU. Donc, ne mettez pas de valeur au compteur d'unit\u00E9
 get_xml_message2=trop \u00E9l\u00E9v\u00E9. En g\u00E9n\u00E9ral, 10 unit\u00E9s vont consommer 100% du CPU sur un 900MHz
 get_xml_message3=Pentium 3. Sur un Pentium 4 2.4GHz, 50 unit\u00E9s est la limite sup\u00E9rieure. Vos
 get_xml_message4=possibilit\u00E9s pour augmenter le nombre de clients est d'augmenter le nombre de
 get_xml_message5=machines ou utiliser des syst\u00E8mes multi-cpu.
 graph_choose_graphs=Graphique \u00E0 afficher
 graph_full_results_title=Graphique de r\u00E9sultats complets
 graph_results_average=Moyenne
 graph_results_data=Donn\u00E9es
 graph_results_deviation=Ecart type
 graph_results_latest_sample=Dernier \u00E9chantillon
 graph_results_median=M\u00E9diane
 graph_results_no_samples=Nombre d'\u00E9chantillons
 graph_results_throughput=D\u00E9bit
 graph_results_title=Graphique de r\u00E9sultats
 grouping_add_separators=Ajouter des s\u00E9parateurs entre les groupes
 grouping_in_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur
 grouping_mode=Grouper \:
 grouping_no_groups=Ne pas grouper les \u00E9chantillons
 grouping_store_first_only=Stocker le 1er \u00E9chantillon pour chaque groupe uniquement
 header_manager_title=Gestionnaire d'ent\u00EAtes HTTP
 headers_stored=Ent\u00EAtes stock\u00E9es
 help=Aide
 help_node=Quel est ce noeud ?
 html_assertion_file=Ecrire un rapport JTidy dans un fichier
 html_assertion_label=Assertion HTML
 html_assertion_title=Assertion HTML
 html_parameter_mask=Masque de param\u00E8tre HTML
 http_implementation=Impl\u00E9mentation HTTP \:
 http_response_code=Code de r\u00E9ponse HTTP
 http_url_rewriting_modifier_title=Transcripteur d'URL HTTP
 http_user_parameter_modifier=Modificateur de param\u00E8tre utilisateur HTTP
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
 interleave_control_title=Controleur Interleave
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
 jms_timeout=D\u00E9lai (millisecondes)
 jms_use_file=Depuis un fichier
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
 junit_append_error=Concat\u00E9ner les erreurs d'assertion
 junit_append_exception=Concat\u00E9ner les exceptions d'ex\u00E9cution
 junit_constructor_error=Impossible de cr\u00E9er une instance de la classe
 junit_constructor_string=Libell\u00E9 de cha\u00EEne Constructeur
 junit_do_setup_teardown=Ne pas appeler setUp et tearDown
 junit_error_code=Code d'erreur
 junit_error_default_msg=Une erreur inattendue est survenue
 junit_error_msg=Message d'erreur
 junit_failure_code=Code d'\u00E9chec
 junit_failure_default_msg=Test \u00E9chou\u00E9
 junit_failure_msg=Message d'\u00E9chec
 junit_pkg_filter=Filtre de paquets
 junit_request=Requ\u00EAte JUnit
 junit_request_defaults=Requ\u00EAte par d\u00E9faut JUnit
 junit_success_code=Code de succ\u00E8s
 junit_success_default_msg=Test r\u00E9ussi
 junit_success_msg=Message de succ\u00E8s
 junit_test_config=Param\u00E8tres Test JUnit
 junit_test_method=M\u00E9thode de test
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
 load_wsdl=Charger WSDL
 log_errors_only=Erreurs
 log_file=Emplacement du fichier de journal (log)
 log_function_comment=Commentaire (facultatif)
 log_function_level=Niveau de journalisation (INFO par d\u00E9faut), OUT ou ERR
 log_function_string=Cha\u00EEne \u00E0 tracer
 log_function_string_ret=Cha\u00EEne \u00E0 tracer (et \u00E0 retourner)
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
 loop_controller_title=Contr\u00F4leur Boucle
 looping_control=Contr\u00F4le de boucle
 lower_bound=Borne Inf\u00E9rieure
 mail_reader_account=Nom utilisateur \:
 mail_reader_all_messages=Tous
 mail_reader_delete=Supprimer les messages du serveur
 mail_reader_folder=Dossier \:
 mail_reader_num_messages=Nombre de message \u00E0 r\u00E9cup\u00E9rer \:
 mail_reader_password=Mot de passe \:
 mail_reader_server=Serveur \:
 mail_reader_server_type=Type de serveur \:
 mail_reader_storemime=Stocker le message en utilisant MIME
 mail_reader_title=Echantillon Lecteur d'email
 mail_sent=Email envoy\u00E9 avec succ\u00E8s
 mailer_attributes_panel=Attributs de courrier
 mailer_error=N'a pas p\u00FB envoyer l'email. Veuillez corriger les erreurs de saisie.
 mailer_visualizer_title=Visualiseur de courrier
 match_num_field=Correspond au num. (0 pour Al\u00E9atoire) \:
 max=Maximum \:
 maximum_param=La valeur maximum autoris\u00E9e pour un \u00E9cart de valeurs
 md5hex_assertion_failure=Erreur de v\u00E9rification de la somme MD5 \: obtenu {0} mais aurait d\u00FB \u00EAtre {1}
 md5hex_assertion_md5hex_test=MD5Hex \u00E0 v\u00E9rifier
 md5hex_assertion_title=Assertion MD5Hex
 memory_cache=Cache de m\u00E9moire
 menu_assertions=Assertions
 menu_close=Fermer
 menu_collapse_all=R\u00E9duire tout
 menu_config_element=Configurations
 menu_edit=Editer
 menu_expand_all=Etendre tout
 menu_generative_controller=Echantillons
 menu_listener=R\u00E9cepteurs
 menu_logic_controller=Contr\u00F4leurs Logiques
 menu_merge=Fusionner...
 menu_modifiers=Modificateurs
 menu_non_test_elements=El\u00E9ments hors test
 menu_open=Ouvrir...
 menu_post_processors=Post-Processeurs
 menu_pre_processors=Pr\u00E9-Processeurs
 menu_response_based_modifiers=Modificateurs bas\u00E9s sur la r\u00E9ponse
 menu_timer=Compteurs de temps
 metadata=M\u00E9ta-donn\u00E9es
 method=M\u00E9thode \:
 mimetype=Type MIME
 minimum_param=La valeur minimale autoris\u00E9e pour l'\u00E9cart de valeurs
 minute=minute
 modddn=Ancienne valeur
 modification_controller_title=Contr\u00F4leur Modification
 modification_manager_title=Gestionnaire Modification
 modify_test=Modification
 module_controller_module_to_run=Module \u00E0 ex\u00E9cuter \:
 module_controller_title=Contr\u00F4leur Module
 module_controller_warning=Ne peut pas trouver le module \:
 monitor_equation_active=Activit\u00E9 \:  (occup\u00E9e/max) > 25%
 monitor_equation_dead=Mort \: pas de r\u00E9ponse
 monitor_equation_healthy=Sant\u00E9 \:  (occup\u00E9e/max) < 25%
 monitor_equation_load=Charge \:  ((occup\u00E9e / max) * 50) + ((m\u00E9moire utilis\u00E9e / m\u00E9moire maximum) * 50)
 monitor_equation_warning=Attention \:  (occup\u00E9/max) > 67%
 monitor_health_tab_title=Sant\u00E9
 monitor_health_title=Moniteur de connecteurs
 monitor_is_title=Utiliser dans moniteur de connecteurs
 monitor_label_prefix=Pr\u00E9fixe de connecteur \:
 monitor_label_right_active=Actif
 monitor_label_right_dead=Mort
 monitor_label_right_healthy=Sant\u00E9
 monitor_label_right_warning=Attention
 monitor_legend_health=Sant\u00E9
 monitor_legend_load=Charge
 monitor_legend_memory_per=M\u00E9moire % (utilis\u00E9e/total)
 monitor_legend_thread_per=Unit\u00E9 % (occup\u00E9/max)
 monitor_performance_servers=Serveurs
 monitor_performance_tab_title=Performance
 monitor_performance_title=Graphique de performance
 name=Nom \:
 new=Nouveau
 no=Norv\u00E9gien
 number_of_threads=Nombre d'unit\u00E9s (utilisateurs) \:
 obsolete_test_element=Cet \u00E9l\u00E9ment de test est obsol\u00E8te
 once_only_controller_title=Contr\u00F4leur Ex\u00E9cution unique
 open=Ouvrir...
 optional_tasks=T\u00E2ches optionnelles
 paramtable=Envoyer les param\u00E8tres avec la requ\u00EAte \:
 password=Mot de passe \:
 paste=Coller
 paste_insert=Coller ins\u00E9rer
 path=Chemin \:
 path_extension_choice=Extension chamin (utiliser ";" comme separateur)
 path_extension_dont_use_equals=Ne pas utiliser \u00E9gale dans l'extension de chemin (Compatibilit\u00E9 Intershop Enfinity)
 path_extension_dont_use_questionmark=Ne pas utiliser le point d'interrogation dans l'extension du chemin (Compatiblit\u00E9 Intershop Enfinity)
 patterns_to_exclude=URL \: motifs \u00E0 exclure
 patterns_to_include=URL \: motifs \u00E0 inclure
 pl=Polonais
 port=Port \:
 property_default_param=Valeur par d\u00E9faut
 property_edit=Editer
 property_editor.value_is_invalid_message=Le texte que vous venez d'entrer n'a pas une valeur valide pour cette propri\u00E9t\u00E9.\nLa propri\u00E9t\u00E9 va revenir \u00E0 sa valeur pr\u00E9c\u00E9dente.
 property_editor.value_is_invalid_title=Texte saisi invalide
 property_name_param=Nom de la propri\u00E9t\u00E9
 property_returnvalue_param=Revenir \u00E0 la valeur originale de la propri\u00E9t\u00E9 (d\u00E9faut non) ?
 property_undefined=Non d\u00E9fini
 property_value_param=Valeur de propri\u00E9t\u00E9
 property_visualiser_title=Afficheur de propri\u00E9t\u00E9s
 protocol=Protocole (d\u00E9faut http) \:
 protocol_java_border=Classe Java
 protocol_java_classname=Nom de classe \:
 protocol_java_config_tile=Configurer \u00E9chantillon Java
 protocol_java_test_title=Test Java
 proxy_assertions=Ajouter une Assertion R\u00E9ponse
 proxy_cl_error=Si un serveur proxy est sp\u00E9cifi\u00E9, h\u00F4te et port doivent \u00EAtre donn\u00E9
 proxy_content_type_exclude=Exclure \:
 proxy_content_type_filter=Filtre de type de contenu
 proxy_content_type_include=Inclure \:
 proxy_daemon_bind_error=Impossible de lancer le serveur proxy, le port est d\u00E9j\u00E0 utilis\u00E9. Choisissez un autre port.
 proxy_daemon_error=Impossible de lancer le serveur proxy, voir le journal pour plus de d\u00E9tails
 proxy_headers=Capturer les ent\u00EAtes HTTP
 proxy_httpsspoofing=Tenter d'usurper le HTTPS
 proxy_httpsspoofing_match=Filtre d'URL (regexp) \:
 proxy_regex=Correspondance des variables par regex ?
 proxy_sampler_settings=Param\u00E8tres Echantillon HTTP
 proxy_sampler_type=Type \:
 proxy_separators=Ajouter des s\u00E9parateurs
 proxy_target=Contr\u00F4leur Cible \:
 proxy_test_plan_content=Param\u00E8tres du plan de test
 proxy_title=Serveur Proxy HTTP
 pt_br=Portugais (Br\u00E9sil)
 ramp_up=Dur\u00E9e de mont\u00E9e en charge (en secondes) \:
 random_control_title=Contr\u00F4leur Al\u00E9atoire
 random_order_control_title=Contr\u00F4leur d'Ordre al\u00E9atoire
 read_response_message=Lire la r\u00E9ponse n'est pas coch\u00E9. Pour voir la r\u00E9ponse, coch\u00E9 la bo\u00EEte dans l'\u00E9chantillonneur svp.
 read_response_note=Si Lire la r\u00E9ponse n'est pas coch\u00E9, l'\u00E9chantillonneur ne pourra pas lire la r\u00E9ponse.
 read_response_note2=ou set le SampleResult. Cela am\u00E9liore les performances, mais signifie que 
 read_response_note3=le contenu de la r\u00E9ponse ne sera pas logg\u00E9.
 read_soap_response=Lire la r\u00E9ponse SOAP.
 realm=Univers (realm)
 record_controller_title=Contr\u00F4leur Enregistreur
 ref_name_field=Nom de r\u00E9f\u00E9rence \:
 regex_extractor_title=Extracteur Expression r\u00E9guli\u00E8re
 regex_field=Expression r\u00E9guli\u00E8re \:
 regex_source=Champs r\u00E9ponse \u00E0 cocher\:
 regex_src_body=Corps
 regex_src_body_unescaped=Corps (non \u00E9chapp\u00E9)
 regex_src_hdrs=Ent\u00EAtes
 regexfunc_param_1=Expression r\u00E9guli\u00E8re utilis\u00E9e pour chercher les r\u00E9sultats de la requ\u00EAte pr\u00E9c\u00E9dente.
 regexfunc_param_2=Canevas pour la ch\u00EEne de caract\u00E8re de remplacement, utilisant des groupes d'expressions r\u00E9guli\u00E8res. Le format est  $[group]$.  Exemple $1$.
 regexfunc_param_3=Quelle correspondance utiliser. Un entier 1 ou plus grand, RAND pour indiquer que JMeter doit choisir al\u00E9atoirement , A d\u00E9cimal, ou ALL indique que toutes les correspondances doivent \u00EAtre utilis\u00E9es
 regexfunc_param_4=Entre le texte. Si ALL est s\u00E9lectionn\u00E9, l'entre-texte sera utilis\u00E9 pour g\u00E9n\u00E9rer les r\u00E9sultats ([""])
 regexfunc_param_5=Text par d\u00E9faut. Utilis\u00E9 \u00E0 la place du canevas si l'expression r\u00E9guli\u00E8re ne trouve pas de correspondance
 remote_exit=Sortie distante
 remote_exit_all=Sortie distante de tous
 remote_start=D\u00E9marrage distant
 remote_start_all=D\u00E9marrage distant de tous
 remote_stop=Arr\u00EAt distant
 remote_stop_all=Arr\u00EAt distant de tous
 remove=Supprimer
 report=Rapport
 request_data=Donn\u00E9e requ\u00EAte
 reset_gui=R\u00E9initialiser l'\u00E9l\u00E9ment
 response_save_as_md5=R\u00E9ponse en empreinte MD5
 restart=Red\u00E9marrer
 resultaction_title=Op\u00E9rateur R\u00E9sultats Action
 resultsaver_errors=Enregistrer seulement les r\u00E9ponses en \u00E9checs
 resultsaver_prefix=Pr\u00E9fixe du nom de fichier \: 
 resultsaver_skipautonumber=Ne pas ajouter de nombre au pr\u00E9fixe
 resultsaver_skipsuffix=Ne pas ajouter de suffixe
 resultsaver_success=Enregistrer seulement les r\u00E9ponses en succ\u00E8s
 resultsaver_title=Sauvegarder les r\u00E9ponses vers un fichier
 resultsaver_variable=Nom de variable \:
 reuseconnection=R\u00E9-utiliser la connexion
 revert_project=Annuler les changements
 revert_project?=Annuler les changements sur le projet ?
 root=Racine
 root_title=Racine
 run=Lancer
 running_test=Lancer test
 runtime_controller_title=Contr\u00F4leur Dur\u00E9e d'ex\u00E9cution
 runtime_seconds=Temps d'ex\u00E9cution (secondes) \:
 sampler_label=Libell\u00E9
 sampler_on_error_action=Action \u00E0 suivre apr\u00E8s une erreur d'\u00E9chantillon
 sampler_on_error_continue=Continuer
 sampler_on_error_stop_test=Arr\u00EAter le test
 sampler_on_error_stop_test_now=Arr\u00EAter le test imm\u00E9diatement
 sampler_on_error_stop_thread=Arr\u00EAter l'unit\u00E9
 save=Enregistrer le plan de test
 save?=Enregistrer ?
 save_all_as=Enregistrer le plan de test sous...
 save_as=Enregistrer sous...
 save_as_error=Au moins un \u00E9l\u00E9ment doit \u00EAtre s\u00E9lectionn\u00E9 \!
 save_as_image=Enregistrer en tant qu'image sous...
 save_as_image_all=Enregistrer l'\u00E9cran en tant qu'image...
 save_assertionresultsfailuremessage=Messages d'erreur des assertions
 save_assertions=R\u00E9sultats des assertions (XML)
 save_asxml=Enregistrer au format XML
 save_bytes=Nombre d'octets
 save_code=Code de r\u00E9ponse HTTP
 save_datatype=Type de donn\u00E9es
 save_encoding=Encodage
 save_fieldnames=Libell\u00E9 des colonnes (CSV)
 save_filename=Nom de fichier de r\u00E9ponse
 save_graphics=Enregistrer le graphique
 save_hostname=Nom d'h\u00F4te
 save_label=Libell\u00E9
 save_latency=Latence
 save_message=Message de r\u00E9ponse
 save_overwrite_existing_file=Le fichier s\u00E9lectionn\u00E9 existe d\u00E9j\u00E0, voulez-vous l'\u00E9craser ?
 save_requestheaders=Ent\u00EAte de requ\u00EAte (XML)
 save_responsedata=Donn\u00E9es de r\u00E9ponse (XML)
 save_responseheaders=Ent\u00EAte de r\u00E9ponse (XML)
 save_samplecount=Nombre d'\u00E9chantillon et d'erreur
 save_samplerdata=Donn\u00E9es d'\u00E9chantillon (XML)
 save_subresults=Sous r\u00E9sultats (XML)
 save_success=Succ\u00E8s
 save_threadcounts=Nombre d'unit\u00E9s actives
 save_threadname=Nom d'unit\u00E9
 save_time=Temps \u00E9coul\u00E9
 save_timestamp=Horodatage
 save_url=URL
 scheduler=Programmateur de d\u00E9marrage
 scheduler_configuration=Configuration du programmateur
 search_base=Base de recherche
 search_filter=Filtre de recherche
 search_test=Recherche
+search_text_button_close=Fermer
+search_text_button_find=Rechercher
+search_text_button_next=Suivant
+search_text_chkbox_case=Consid\u00E9rer la casse
+search_text_chkbox_regexp=Exp. reguli\u00E8re
+search_text_field=Rechercher \:
+search_text_msg_not_found=Text non trouv\u00E9
+search_text_title_not_found=Pas trouv\u00E9
 second=seconde
 secure=S\u00E9curis\u00E9 \:
 send_file=Envoyer un fichier avec la requ\u00EAte \:
 send_file_browse=Parcourir...
 send_file_filename_label=Chemin du fichier \: 
 send_file_mime_label=Type MIME \:
 send_file_param_name_label=Nom du param\u00E8tre \:
 server=Nom ou IP du serveur \:
 servername=Nom du serveur \:
 session_argument_name=Nom des arguments de la session
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
 soap_action=Action Soap
 soap_data_title=Donn\u00E9es Soap/XML-RPC
 soap_sampler_title=Requ\u00EAte SOAP/XML-RPC
 soap_send_action=Envoyer l'action SOAP \:
 spline_visualizer_average=Moyenne \:
 spline_visualizer_incoming=Entr\u00E9e \:
 spline_visualizer_maximum=Maximum \:
 spline_visualizer_minimum=Minimum \:
 spline_visualizer_title=Moniteur de courbe (spline)
 spline_visualizer_waitingmessage=En attente de r\u00E9sultats d'\u00E9chantillons
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
 starttime=Date et heure de d\u00E9marrage \:
 stop=Arr\u00EAter
 stopping_test=En train d'\u00E9teindre toutes les unit\u00E9s de tests. Soyez patient, merci.
 stopping_test_failed=Au moins une unit\u00E9 non arr\u00EAt\u00E9e; voir le journal.
 stopping_test_title=En train d'arr\u00EAter le test
 string_from_file_file_name=Entrer le chemin complet du fichier
 string_from_file_seq_final=Nombre final de s\u00E9quence de fichier
 string_from_file_seq_start=D\u00E9marer le nombre de s\u00E9quence de fichier
 summariser_title=G\u00E9n\u00E9rer les resultats consolid\u00E9s
 summary_report=Rapport consolid\u00E9
 switch_controller_label=Permuter vers le num\u00E9ro d'\u00E9l\u00E9ment (ou nom) subordonn\u00E9 \:
 switch_controller_title=Contr\u00F4leur R\u00E9gulateur
 table_visualizer_bytes=Octets
 table_visualizer_sample_num=Echantillon \#
 table_visualizer_sample_time=Temps (ms)
 table_visualizer_start_time=Heure d\u00E9but
 table_visualizer_status=Statut
 table_visualizer_success=Succ\u00E8s
 table_visualizer_thread_name=Nom d'unit\u00E9
 table_visualizer_warning=Alerte
 tcp_classname=Nom de classe TCPClient \:
 tcp_config_title=Param\u00E8tres TCP par d\u00E9faut
 tcp_nodelay=D\u00E9finir aucun d\u00E9lai (NoDelay)
 tcp_port=Num\u00E9ro de port \:
 tcp_request_data=Texte \u00E0 envoyer \:
 tcp_sample_title=Requ\u00EAte TCP
 tcp_timeout=Expiration (millisecondes) \:
 template_field=Canevas \:
 test=Test
 test_action_action=Action \:
 test_action_duration=Dur\u00E9e (millisecondes) \:
 test_action_pause=Mettre en pause
 test_action_stop=Arr\u00EAter
 test_action_stop_now=Arr\u00EAter imm\u00E9diatement
 test_action_target=Cible \:
 test_action_target_test=Toutes les unit\u00E9s
 test_action_target_thread=Unit\u00E9 courante
 test_action_title=Action test
 test_configuration=Type de test
 test_plan=Plan de test
 test_plan_classpath_browse=Ajouter un r\u00E9pertoire ou un fichier 'jar' au 'classpath'
 testplan.serialized=Lancer les groupes d'unit\u00E9s en s\u00E9rie (c'est-\u00E0-dire \: lance un groupe \u00E0 la fois)
 testplan_comments=Commentaires \:
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
 timelim=Limite de temps
 tr=Turc
 transaction_controller_parent=G\u00E9n\u00E9rer en \u00E9chantillon parent
 transaction_controller_title=Contr\u00F4leur Transaction
 unbind=D\u00E9lier les unit\u00E9s
 uniform_timer_delay=D\u00E9lai de d\u00E9calage constant (en millisecondes) \:
 uniform_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution uniforme
 uniform_timer_range=D\u00E9lai al\u00E9atoire maximum (en millisecondes) \:
 uniform_timer_title=Compteur de temps al\u00E9atoire uniforme
 update_per_iter=Mettre \u00E0 jour une fois par it\u00E9ration
 upload=Fichier \u00E0 uploader
 upper_bound=Borne sup\u00E9rieure
 url_config_protocol=Protocole \:
 url_config_title=Param\u00E8tres HTTP par d\u00E9faut
 url_full_config_title=Echantillon d'URL complet
 url_multipart_config_title=Requ\u00EAte HTTP Multipart par d\u00E9faut
 use_keepalive=Connexions persistantes
 use_multipart_for_http_post=Multipart/form-data pour POST
 use_recording_controller=Utiliser un contr\u00F4leur enregistreur
 user=Utilisateur
 user_defined_test=Test d\u00E9fini par l'utilisateur
 user_defined_variables=Variables pr\u00E9-d\u00E9finies
 user_param_mod_help_note=(Ne pas changer. A la place, modifier le fichier de ce nom dans le r\u00E9pertoire /bin de JMeter)
 user_parameters_table=Param\u00E8tres
 user_parameters_title=Param\u00E8tres Utilisateur
 userdn=Identifiant
 username=Nom d'utilisateur \:
 userpw=Mot de passe
 value=Valeur \:
 var_name=Nom de r\u00E9f\u00E9rence \:
 variable_name_param=Nom de variable (peut inclure une r\u00E9f\u00E9rence de variable ou fonction)
 view_graph_tree_title=Voir le graphique en arbre
 view_results_assertion_error=Erreur d'assertion \: 
 view_results_assertion_failure=Echec d'assertion \: 
 view_results_assertion_failure_message=Message d'\u00E9chec d'assertion \: 
 view_results_desc=Affiche les r\u00E9sultats d'un \u00E9chantillon dans un arbre de r\u00E9sultats
 view_results_error_count=Compteur erreur\: 
 view_results_fields=champs \:
 view_results_in_table=Tableau de r\u00E9sultats
 view_results_latency=Latence \: 
 view_results_load_time=Temps de r\u00E9ponse \: 
+view_results_render=Rendu \: 
 view_results_render_embedded=T\u00E9l\u00E9charger les ressources incluses
-view_results_render_html=Afficher en HTML
-view_results_render_json=Afficher en JSON
-view_results_render_text=Afficher en texte brut
-view_results_render_xml=Afficher en XML
+view_results_render_html=HTML
+view_results_render_json=JSON
+view_results_render_text=Texte brut
+view_results_render_xml=XML
 view_results_request_headers=Ent\u00EAtes de requ\u00EAte \:
 view_results_response_code=Code HTTP de r\u00E9ponse \: 
 view_results_response_headers=Ent\u00EAtes de r\u00E9ponse \:
 view_results_response_message=Message HTTP de r\u00E9ponse \: 
 view_results_response_too_large_message=R\u00E9ponse d\u00E9passant la taille maximale d'affichage. Taille \: 
 view_results_sample_count=Compteur \u00E9chantillon \: 
 view_results_sample_start=Date d\u00E9but \u00E9chantillon \: 
+view_results_search_pane=Volet recherche 
 view_results_size_in_bytes=Taille en octets \: 
 view_results_tab_assertion=R\u00E9sultats d'assertion
 view_results_tab_request=Requ\u00EAte
 view_results_tab_response=Donn\u00E9es de r\u00E9ponse
 view_results_tab_sampler=R\u00E9sultat de l'\u00E9chantillon
 view_results_thread_name=Nom d'unit\u00E9 \: 
 view_results_title=Voir les r\u00E9sultats
 view_results_tree_title=Arbre de r\u00E9sultats
 warning=Attention \!
 web_request=Requ\u00EAte HTTP
 web_server=Serveur web
 web_server_client=Impl\u00E9mentation client \:
 web_server_domain=Nom ou adresse IP \:
 web_server_port=Port \:
 web_server_timeout_connect=Connexion \:
 web_server_timeout_response=R\u00E9ponse \:
 web_server_timeout_title=D\u00E9lai expiration (ms)
 web_testing2_title=Requ\u00EAte HTTP HTTPClient
 web_testing_embedded_url_pattern=Les URL \u00E0 inclure doivent correspondre \u00E0 \:
 web_testing_retrieve_images=R\u00E9cup\u00E9rer les ressources incluses
 web_testing_title=Requ\u00EAte HTTP
 webservice_proxy_host=H\u00F4te proxy
 webservice_proxy_note=Si 'utiliser le proxy HTTP' est coch\u00E9e, mais qu'aucun h\u00F4te ou port est fournit, l'\u00E9chantillon
 webservice_proxy_note2=regardera les options de ligne de commandes. Si aucun h\u00F4te ou port du proxy sont fournit 
 webservice_proxy_note3=non plus, il \u00E9chouera silencieusement.
 webservice_proxy_port=Port proxy
 webservice_sampler_title=Requ\u00EAte WebService(SOAP) (Code Beta)
 webservice_soap_action=Action SOAP
 webservice_timeout=D\u00E9lai \:
 webservice_use_proxy=Utiliser un proxy HTTP
 while_controller_label=Condition (fonction ou variable) \:
 while_controller_title=Contr\u00F4leur Tant Que
 workbench_title=Plan de travail
 wsdl_helper_error=Le WSDL n'est pas valide, veuillez rev\u00E9rifier l'URL.
 wsdl_url_error=Le WSDL est vide.
 xml_assertion_title=Assertion XML
 xml_namespace_button=Utiliser les espaces de noms
 xml_tolerant_button=Analyseur XML/HTML 'flexible'
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
 xpath_extractor_query=Requ\u00EAte XPath \:
 xpath_extractor_title=Extracteur XPath
 xpath_tidy_quiet=Silencieux
 xpath_tidy_report_errors=Rapporter les erreurs
 xpath_tidy_show_warnings=Afficher les alertes
 you_must_enter_a_valid_number=Vous devez entrer un nombre valide
 zh_cn=Chinois (simplifi\u00E9)
 zh_tw=Chinois (traditionnel)
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index a45dad8c3..af796154e 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,157 +1,158 @@
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
 	<author email="jmeter-dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 <!--  ===================  -->
 
 <h1>Version 2.4</h1>
 
 <h2>Summary of main changes</h2>
 
 <p>
 </p>
 
 
 <!--  ========================= End of summary ===================================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h2>Incompatible changes</h2>
 
 <p>
 The XPath Assertion and XPath Extractor elements no longer fetch external DTDs by default; this can be changed in the GUI.
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 47445 -  Using Proxy with https-spoofing secure cookies need to be unsecured</li>
 <li>Bug 47442 -  Missing replacement of https by http for certain conditions using https-spoofing</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 47420 - LDAP extended request not closing connections during add request</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 47385 - TransactionController should set AllThreads and GroupThreads</li>
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
 <li>Bug 47646 -  NullPointerException in the "Random Variable" element</li>
 </ul>
 
 <!-- ==================================================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>Bug 47622 - enable recording of HTTPS sessions</li>
 <li>Allow Proxy Server to be specified on HTTP Sampler GUI and HTTP Config GUI</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 47398 - SampleEvents are sent twice over RMI in distributed testing and non gui mode</li>
 <li>Added DataStrippingSample sender - supports "Stripped" and "StrippedBatch" modes.</li>
 <li>Added Comparison Assertion Visualizer</li>
+<li>Bug 36726 - add search function to Tree View Listener</li>
 </ul>
 
 <h3>Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 47338 - XPath Extractor forces retrieval of document DTD</li>
 <li>Added Comparison Assertion</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 47565 - [Function] FileToString</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>Bug 47223 - Slow Aggregate Report Performance (StatCalculator)</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Add TestBean Table Editor support</li>
 </ul>
 
 </section> 
 </body> 
 </document>
