diff --git a/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java b/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
index e0e287f3b..2906c19e2 100644
--- a/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
@@ -1,114 +1,115 @@
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
 import java.awt.Dimension;
 
 import javax.swing.Box;
 import javax.swing.JLabel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 
 public class AssertionVisualizer extends AbstractVisualizer implements Clearable {
 
     private static final long serialVersionUID = 240L;
 
     private JTextArea textArea;
 
     public AssertionVisualizer() {
         init();
         setName(getStaticLabel());
     }
 
     public String getLabelResource() {
         return "assertion_visualizer_title"; // $NON-NLS-1$
     }
 
     public void add(SampleResult sample) {
         StringBuilder sb = new StringBuilder(100);
         sb.append(sample.getSampleLabel());
         sb.append(getAssertionResult(sample));
         sb.append("\n"); // $NON-NLS-1$
         synchronized (textArea) {
             textArea.append(sb.toString());
+            textArea.setCaretPosition(textArea.getText().length());
         }
     }
 
     public void clearData() {
         textArea.setText(""); // $NON-NLS-1$
     }
 
     private String getAssertionResult(SampleResult res) {
         if (res != null) {
             StringBuilder display = new StringBuilder();
             AssertionResult assertionResults[] = res.getAssertionResults();
             for (int i = 0; i < assertionResults.length; i++) {
                 AssertionResult item = assertionResults[i];
 
                 if (item.isFailure() || item.isError()) {
                     display.append("\n\t"); // $NON-NLS-1$
                     display.append(item.getName() != null ? item.getName() + " : " : "");// $NON-NLS-1$
                     display.append(item.getFailureMessage());
                 }
             }
             return display.toString();
         }
         return "";
     }
 
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         this.setBorder(margin);
 
         // NAME
         this.add(makeTitlePanel(), BorderLayout.NORTH);
 
         // TEXTAREA LABEL
         JLabel textAreaLabel =
             new JLabel(JMeterUtils.getResString("assertion_textarea_label")); // $NON-NLS-1$
         Box mainPanel = Box.createVerticalBox();
         mainPanel.add(textAreaLabel);
 
         // TEXTAREA
         textArea = new JTextArea();
         textArea.setEditable(false);
         textArea.setLineWrap(false);
         JScrollPane areaScrollPane = new JScrollPane(textArea);
 
         areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
         areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 
         areaScrollPane.setPreferredSize(new Dimension(mainPanel.getWidth(),mainPanel.getHeight()));
         mainPanel.add(areaScrollPane);
         this.add(mainPanel, BorderLayout.CENTER);
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index 05b49968b..c845810a0 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,382 +1,413 @@
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
 
 /**
  *
  */
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
+import java.awt.event.ItemEvent;
+import java.awt.event.ItemListener;
 import java.io.IOException;
 import java.util.Collections;
 import java.util.List;
 
 import javax.swing.ComboBoxModel;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.ImageIcon;
+import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTree;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.DefaultTreeModel;
 import javax.swing.tree.TreePath;
 import javax.swing.tree.TreeSelectionModel;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Base for ViewResults
  *
  */
 public class ViewResultsFullVisualizer extends AbstractVisualizer
-implements ActionListener, TreeSelectionListener, Clearable {
+implements ActionListener, TreeSelectionListener, Clearable, ItemListener {
 
     private static final long serialVersionUID = 7338676747296593842L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final Color SERVER_ERROR_COLOR = Color.red;
 
     public static final Color CLIENT_ERROR_COLOR = Color.blue;
 
     public static final Color REDIRECT_COLOR = Color.green;
 
     private  JSplitPane mainSplit;
 
     private DefaultMutableTreeNode root;
 
     private DefaultTreeModel treeModel;
 
     private JTree jTree;
 
     private Component leftSide;
 
     private JTabbedPane rightSide;
 
     private JComboBox selectRenderPanel;
 
     private int selectedTab;
 
     protected static final String COMBO_CHANGE_COMMAND = "change_combo"; // $NON-NLS-1$
 
     private static final ImageIcon imageSuccess = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                     "icon_success_sml.gif")); //$NON-NLS-1$
 
     private static final ImageIcon imageFailure = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                     "icon_warning_sml.gif")); //$NON-NLS-1$
 
     // Maximum size that we will display
     private static final int MAX_DISPLAY_SIZE =
         JMeterUtils.getPropDefault("view.results.tree.max_size", 200 * 1024); // $NON-NLS-1$
 
     private ResultRenderer resultsRender = null;
 
     private TreeSelectionEvent lastSelectionEvent;
 
+    private JCheckBox autoScrollCB;
+
     /**
      * Constructor
      */
     public ViewResultsFullVisualizer() {
         super();
         init();
     }
 
     /** {@inheritDoc} */
     public void add(SampleResult sample) {
         updateGui(sample);
     }
 
     /**
      * Update the visualizer with new data.
      */
     private synchronized void updateGui(SampleResult res) {
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
+        if (autoScrollCB.isSelected() && root.getChildCount() > 1) {
+            jTree.scrollRowToVisible(root.getChildCount() - 1);
+        }
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
 
     /** {@inheritDoc} */
     public void clearData() {
         while (root.getChildCount() > 0) {
             // the child to be removed will always be 0 'cos as the nodes are
             // removed the nth node will become (n-1)th
             treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
         }
         resultsRender.clearData();
     }
 
     /** {@inheritDoc} */
     public String getLabelResource() {
         return "view_results_tree_title"; // $NON-NLS-1$
     }
 
     /**
      * Initialize this visualizer
      */
     protected void init() {
         log.debug("init() - pass");
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         leftSide = createLeftPanel();
         // Prepare the common tab
         rightSide = new JTabbedPane();
 
         // Create the split pane
         mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
         add(mainSplit, BorderLayout.CENTER);
         // init right side with first render
         resultsRender.setRightSide(rightSide);
         resultsRender.init();
     }
 
     /** {@inheritDoc} */
     public void valueChanged(TreeSelectionEvent e) {
         lastSelectionEvent = e;
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
         if (node != null) {
             // to restore last tab used
             if (rightSide.getTabCount() > selectedTab) {
                 resultsRender.setLastSelectedTab(rightSide.getSelectedIndex());
             }
             Object userObject = node.getUserObject();
             resultsRender.setSamplerResult(userObject);
             resultsRender.setupTabPane(); // Processes Assertions
             // display a SampleResult
             if (userObject instanceof SampleResult) {
                 SampleResult sampleResult = (SampleResult) userObject;
                 if ((SampleResult.TEXT).equals(sampleResult.getDataType())){
                     resultsRender.renderResult(sampleResult);
                 } else {
                     byte[] responseBytes = sampleResult.getResponseData();
                     if (responseBytes != null) {
                         resultsRender.renderImage(sampleResult);
                     }
                 }
             }
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
-
         JScrollPane treePane = new JScrollPane(jTree);
         treePane.setPreferredSize(new Dimension(200, 300));
 
         VerticalPanel leftPane = new VerticalPanel();
         leftPane.add(treePane, BorderLayout.CENTER);
-        leftPane.add(createComboRender(), BorderLayout.SOUTH);
-
+        VerticalPanel leftDownPane = new VerticalPanel();
+        leftDownPane.add(createComboRender(), BorderLayout.NORTH);
+        autoScrollCB = createASCheckBox("view_results_autoscroll", Color.BLACK);
+        leftDownPane.add(autoScrollCB, BorderLayout.SOUTH);
+        leftPane.add(leftDownPane, BorderLayout.SOUTH);
         return leftPane;
     }
 
     /**
+     * Create auto scroll checkbox
+     * 
+     * @param labelResourceName
+     * @param color
+     * @return {@link JCheckBox}
+     */
+    private JCheckBox createASCheckBox(String labelResourceName, Color color) {
+        JCheckBox checkBox = new JCheckBox(
+                JMeterUtils.getResString(labelResourceName));
+        checkBox.setSelected(false);
+        checkBox.addItemListener(this);
+        checkBox.setForeground(color);
+        return checkBox;
+    }
+
+    /**
      * Create the drop-down list to changer render
      * @return List of all render (implement ResultsRender)
      */
     private Component createComboRender() {
         ComboBoxModel nodesModel = new DefaultComboBoxModel();
         // drop-down list for renderer
         selectRenderPanel = new JComboBox(nodesModel);
         selectRenderPanel.setActionCommand(COMBO_CHANGE_COMMAND);
         selectRenderPanel.addActionListener(this);
 
         // if no results render in jmeter.properties, load Standard (default)
         List<String> classesToAdd = Collections.<String>emptyList();
         try {
             classesToAdd = JMeterUtils.findClassesThatExtend(ResultRenderer.class);
         } catch (IOException e1) {
             // ignored
         }
         String textRenderer = JMeterUtils.getResString("view_results_render_text"); // $NON-NLS-1$
         Object textObject = null;
         for (String clazz : classesToAdd) {
             try {
                 // Instantiate render classes
                 final ResultRenderer renderer = (ResultRenderer) Class.forName(clazz).newInstance();
                 if (textRenderer.equals(renderer.toString())){
                     textObject=renderer;
                 }
                 renderer.setBackgroundColor(getBackground());
                 selectRenderPanel.addItem(renderer);
             } catch (Exception e) {
                 log.warn("Error in load result render:" + clazz, e);
             }
         }
         nodesModel.setSelectedItem(textObject); // preset to "Text" option
         return selectRenderPanel;
     }
 
     /** {@inheritDoc} */
     public void actionPerformed(ActionEvent event) {
         String command = event.getActionCommand();
         if (COMBO_CHANGE_COMMAND.equals(command)) {
             JComboBox jcb = (JComboBox) event.getSource();
 
             if (jcb != null) {
                 resultsRender = (ResultRenderer) jcb.getSelectedItem();
                 if (rightSide != null) {
                     // to restore last selected tab (better user-friendly)
                     selectedTab = rightSide.getSelectedIndex();
                     // Remove old right side
                     mainSplit.remove(rightSide);
 
                     // create and add a new right side
                     rightSide = new JTabbedPane();
                     mainSplit.add(rightSide);
                     resultsRender.setRightSide(rightSide);
                     resultsRender.setLastSelectedTab(selectedTab);
                     log.debug("selectedTab=" + selectedTab);
                     resultsRender.init();
                     // To display current sampler result before change
                     this.valueChanged(lastSelectionEvent);
                 }
             }
         }
     }
 
     public static String getResponseAsString(SampleResult res) {
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
 
     private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
-
         private static final long serialVersionUID = 4159626601097711565L;
 
         @Override
         public Component getTreeCellRendererComponent(JTree tree, Object value,
                 boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
             super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
             boolean failure = true;
             Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
             if (userObject instanceof SampleResult) {
                 failure = !(((SampleResult) userObject).isSuccessful());
             } else if (userObject instanceof AssertionResult) {
                 AssertionResult assertion = (AssertionResult) userObject;
                 failure = assertion.isError() || assertion.isFailure();
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
 
+    /**
+     * Handler for Checkbox
+     */
+    public void itemStateChanged(ItemEvent e) {
+        // NOOP state is held by component
+    }
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index 9096df5a5..e5fcb01ee 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1087 +1,1088 @@
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
 assertion_body_resp=Response Body
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_network_size=Full Response
 assertion_not=Not
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_resp_field=Response Field to Test
 assertion_resp_size_field=Response Size Field to Test
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
 bouncy_castle_unavailable_message=The jars for bouncy castle are unavailable, please add them to your classpath.
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
 grouping_in_transaction_controllers=Put each group in a new transaction controller
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
 http_implementation=Implementation:
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
 jms_client_caption=Receiver client uses MessageConsumer.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
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
 jms_error_msg=Object message should read from an external file. Text input is currently selected, please remember to change it.
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
 jms_stop_between_samples=Stop between samples?
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use MessageConsumer.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_message=Text Message
 jms_timeout=Timeout (milliseconds)
 jms_topic=Destination
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File
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
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_port=Server Port (optional):
 mail_reader_server=Server Host:
 mail_reader_server_type=Protocol (e.g. pop3, imaps):
 mail_reader_storemime=Store the message using MIME (raw)
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
 menu_threads=Threads (Users)
 menu_fragments=Test Fragment
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
 post_thread_group_title=tearDown Thread Group
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
 protocol=Protocol [http]\:
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
 proxy_httpsspoofing_match=Only spoof URLs matching:
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
 regexp_tester_button_test=Test
 regexp_tester_field=Regular expression\:
 regexp_render_no_text=Data response result isn't text.
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
 sample_scope=Apply to:
 sample_scope_all=Main sample and sub-samples
 sample_scope_children=Sub-samples only
 sample_scope_parent=Main sample only
 sample_scope_variable=JMeter Variable
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_start_next_loop=Start Next Loop
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
 save_idletime=Save Idle Time
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
 search_text_button_close=Close
 search_text_button_find=Find
 search_text_button_next=Find next
 search_text_chkbox_case=Case sensitive
 search_text_chkbox_regexp=Regular exp.
 search_text_field=Search: 
 search_text_msg_not_found=Text not found
 search_text_title_not_found=Not found
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
 smime_assertion_not_signed=Message not signed
 smime_assertion_message_position=Execute assertion on message at position
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
 smtp_enforcestarttls_tooltip=<html><b>Enforces</b> the server to use StartTLS.<br />If not selected and the SMTP-Server doesn't support StartTLS, <br />a normal SMTP-Connection will be used as fallback instead. <br /><i>Please note</i> that this checkbox creates a file in \"/tmp/\", <br />so this will cause problems under windows.</html>
 smtp_from=Address From:
 smtp_mail_settings=Mail settings
 smtp_message=Message:
 smtp_message_settings=Message settings
 smtp_messagesize=Calculate message size
 smtp_password=Password:
 smtp_plainbody=Send plain body (i.e. not multipart/mixed)
 smtp_replyto=Address Reply-To:
 smtp_sampler_title=SMTP Sampler
 smtp_security_settings=Security settings
 smtp_server_port=Port:
 smtp_server=Server:
 smtp_server_settings=Server settings
 smtp_subject=Subject:
 smtp_suppresssubj=Suppress Subject Header
 smtp_to=Address To:
 smtp_timestamp=Include timestamp in subject
 smtp_trustall=Trust all certificates
 smtp_trustall_tooltip=<html><b>Enforces</b> JMeter to trust all certificates, whatever CA it comes from.</html>
 smtp_truststore=Local truststore:
 smtp_truststore_tooltip=<html>The pathname of the truststore.<br />Relative paths are resolved against the current directory.<br />Failing that, against the directory containing the test script (JMX file)</html>
 smtp_useauth=Use Auth
 smtp_usetruststore=Use local truststore
 smtp_usetruststore_tooltip=<html>Allows JMeter to use a local truststore.</html>
 smtp_usenone=Use no security features
 smtp_username=Username:
 smtp_usessl=Use SSL
 smtp_usestarttls=Use StartTLS
 smtp_header_add=Add Header
 smtp_header_remove=Remove
 smtp_header_name=Header Name
 smtp_header_value=Header Value
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
 string_from_file_encoding=File encoding if not the platform default (opt)
 string_from_file_file_name=Enter path (absolute or relative) to file
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
 target_server=Target Server
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
 textbox_cancel=Cancel
 textbox_close=Close
 textbox_save_close=Save & Close
 textbox_title_edit=Edit text
 textbox_title_view=View text
 textbox_tooltip_cell=Double click to view/edit
 thread_delay_properties=Thread Delay Properties
 thread_group_title=Thread Group
 test_fragment_title=Test Fragment
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
 toggle=Toggle
 tr=Turkish
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 transaction_controller_include_timers=Include timer duration in generated sample
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
 use_expires=Use Cache-Control/Expires header when processing GET requests
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for POST
 use_multipart_mode_browser=Browser-compatible headers
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
+view_results_autoscroll=Scroll automatically?
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_load_time=Load time: 
 view_results_render=Render: 
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML (download embedded resources)
 view_results_render_json=JSON
 view_results_render_text=Text
 view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_too_large_message=Response too large to be displayed. Size: 
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
 view_results_search_pane=Search pane
 view_results_size_in_bytes=Size in bytes: 
 view_results_size_body_in_bytes=Body size in bytes: 
 view_results_size_headers_in_bytes=Headers size in bytes: 
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
 view_results_table_request_raw_nodata=No data to display
 view_results_table_request_params_key=Parameter name
 view_results_table_request_params_value=Value
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Raw
 view_results_table_result_tab_parsed=Parsed
 view_results_table_result_tab_raw=Raw
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
 web_testing2_source_ip=Source IP address:
 web_testing2_title=HTTP Request HTTPClient
 web_testing_concurrent_download=Use concurrent pool. Size:
 web_testing_embedded_url_pattern=Embedded URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources from HTML Files
 web_testing_title=HTTP Request
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
 webservice_methods=Web Methods
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
 xpath_extractor_fragment=Return entire XPath fragment instead of text content?
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_file_file_name=XML file to get values from 
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 236aef323..82d12af90 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,165 +1,166 @@
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
 
 <!--  ===================  -->
 
 <h1>Version 2.5.1</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  ========================= End of summary ===================================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>If Controller generates additional samples for children Transaction Controller inside when condition is false.</p>
 
 <p>If Controller may make that JMeter starts a infinite running when the If condition is always
 false from the first iteration.</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h2>Incompatible changes</h2>
 
 <p>
 The HttpClient4 sampler as implemented in version 2.5 used a retry count of 3.
 As this can hide server errors, JMeter now sets the retry count to 0 to prevent any automatic retries.
 This can be overridden by setting the JMeter property <b>httpclient4.retrycount</b>.
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Fix HttpClient 4 sampler so it reuses HttpClient instances and connections where possible.</li>
 <li>Bug 51750 - Retrieve all embedded resources doesn't follow IFRAME</li>
 <li>Change the default so the HttpClient 4 sampler does not retry</li>
 <li>Bug 51752 - HTTP Cache is broken when using "Retrieve all embedded resources" with concurrent pool</li>
 <li>Bug 39219 - HTTP Server: You can't stop it after File->Open</li>
 <li>Bug 51775 - Port number duplicates in Host header when capturing by HttpClient (3.1 and 4.x)</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 50424 - Web Methods drop down list box inconsistent</li>
 <li>Bug 43293 - Java Request fields not cleared when creating new sampler</li>
 <li>Bug 51830 - Webservice Soap Request triggers too many popups when Webservice WSDL URL is down</li>
 <li>WebService(SOAP) request - add a connect timeout to get the wsdl used to populate Web Methods when server doesn't response</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>If Controller - Fixed a regression introduce by bug 50032 (see bug 50618 too)</li>
 <li>If Controller - Catches a StackOverflowError when a condition returns always false (after at least one iteration with return true) See bug 50618</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 48943 - Functions are invoked additional times when used in combination with a Config Element</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>WebService(SOAP) request - add I18N for some labels</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 51831 - Cannot disable UDP server or change the maximum UDP port</li>
 <li>Bug 51821 - Add short-cut for Enabling / Disabling (sub)tree or branches in test plan.</li>
 </ul>
 
 <!-- ==================================================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
+<li>Bug 42246 - Need for a 'auto-scroll' option in "View Results Tree" and "Assertion Results"</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
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
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 </ul>
 
 </section> 
 </body> 
 </document>
