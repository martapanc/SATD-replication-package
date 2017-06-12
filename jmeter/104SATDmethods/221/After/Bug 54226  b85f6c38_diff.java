diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index 524e4b3f2..8b27aa982 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,417 +1,427 @@
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
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 import java.io.IOException;
 import java.util.Collections;
 import java.util.List;
 
 import javax.swing.ComboBoxModel;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.ImageIcon;
 import javax.swing.JCheckBox;
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
 
+import org.apache.commons.lang3.StringUtils;
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
 implements ActionListener, TreeSelectionListener, Clearable, ItemListener {
 
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
 
     private JCheckBox autoScrollCB;
 
     /**
      * Constructor
      */
     public ViewResultsFullVisualizer() {
         super();
         init();
     }
 
     /** {@inheritDoc} */
     @Override
     public void add(final SampleResult sample) {
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                 updateGui(sample);
             }
         });
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
         if (autoScrollCB.isSelected() && root.getChildCount() > 1) {
             jTree.scrollPathToVisible(new TreePath(new Object[] { root,
                     treeModel.getChild(root, root.getChildCount() - 1) }));
         }
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
     @Override
     public synchronized void clearData() {
         while (root.getChildCount() > 0) {
             // the child to be removed will always be 0 'cos as the nodes are
             // removed the nth node will become (n-1)th
             treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
         }
         resultsRender.clearData();
     }
 
     /** {@inheritDoc} */
     @Override
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
     @Override
     public void valueChanged(TreeSelectionEvent e) {
         lastSelectionEvent = e;
         DefaultMutableTreeNode node = null;
         synchronized (this) {
             node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
         }
 
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
-                if ((SampleResult.TEXT).equals(sampleResult.getDataType())){
+                if (isTextDataType(sampleResult)){
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
 
+    /**
+     * @param sampleResult SampleResult
+     * @return true if sampleResult is text or has empty content type
+     */
+    protected static boolean isTextDataType(SampleResult sampleResult) {
+        return (SampleResult.TEXT).equals(sampleResult.getDataType())
+                || StringUtils.isEmpty(sampleResult.getDataType());
+    }
+
     private synchronized Component createLeftPanel() {
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
 
         VerticalPanel leftPane = new VerticalPanel();
         leftPane.add(treePane, BorderLayout.CENTER);
         VerticalPanel leftDownPane = new VerticalPanel();
         leftDownPane.add(createComboRender(), BorderLayout.NORTH);
         autoScrollCB = new JCheckBox(JMeterUtils.getResString("view_results_autoscroll"));
         autoScrollCB.setSelected(false);
         autoScrollCB.addItemListener(this);
         leftDownPane.add(autoScrollCB, BorderLayout.SOUTH);
         leftPane.add(leftDownPane, BorderLayout.SOUTH);
         return leftPane;
     }
 
     /**
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
     @Override
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
-        if ((SampleResult.TEXT).equals(res.getDataType())) {
+        if (isTextDataType(res)) {
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
                 StringBuilder builder = new StringBuilder(MAX_DISPLAY_SIZE+100);
                 builder.append(JMeterUtils.getResString("view_results_response_too_large_message")) //$NON-NLS-1$
                     .append(len).append(" > Max: ").append(MAX_DISPLAY_SIZE)
                     .append(", ").append(JMeterUtils.getResString("view_results_response_partial_message"))
                     .append("\n").append(res.getResponseDataAsString().substring(0, MAX_DISPLAY_SIZE)).append("\n...");
                 response = builder.toString();
             } else {
                 response = res.getResponseDataAsString();
             }
         }
         return response;
     }
 
     private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
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
 
     /**
      * Handler for Checkbox
      */
     @Override
     public void itemStateChanged(ItemEvent e) {
         // NOOP state is held by component
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index fae639fe0..3e4524e0f 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,224 +1,226 @@
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
 
 
 <!--  =================== 2.9 =================== -->
 
 <h1>Version 2.9</h1>
 
 <h2>New and Noteworthy</h2>
 
 <h3>Core Improvements:</h3>
 <h4>Webservice sampler now adds to request the headers that are set through Header Manager</h4>
 
 <h3>GUI and ergonomy Improvements:</h3>
 <h4>Allow copy from clipboard to HeaderPanel, headers are supposed to be separated by new line and have the following form name:value</h4>
 
 <h4>Proxy now has a button to add a set of default exclusions for URL patterns, this list can be configured through property : proxy.excludes.suggested</h4>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <p>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </p>
 
 <p>
 Changing language can break part of the configuration of the following elements (see <bugzilla>53679</bugzilla>):
 <ul>
     <li>CSV Data Set Config (sharing mode will be lost)</li>
     <li>Constant Throughput Timer (Calculate throughput based on will be lost)</li>
 </ul>
 </p>
 
 <p>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
 </p>
 
 <p>
 Note that there is a bug in Java on some Linux systems that manifests
 itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation.
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>Webservice sampler now adds to request the headers that are set through Header Manager, these were previously ignored</p>
 
 <p>jdbcsampler.cachesize property has been removed, it previously limited the size of a per connection cache of Map &lt; String, PreparedStatement &gt; , it also limited the size of this
 map which held the PreparedStatement for SQL queries. This limitation provoked a bug <bugzilla>53995</bugzilla>. 
 It has been removed so now size of these 2 maps is not limited anymore. This change changes behaviour as starting from this version no PreparedStatement will be closed during the test.</p>
 
 <p>Starting with this version, there are some important changes on JSR223 Test Elements:
 <ul>
     <li>JSR223 Test Elements that have an invalid filename (not existing or unreadable) will make test fail instead of making the element silently work</li>
     <li>In JSR223 Test Elements: responseCodeOk, responseMessageOK and successful are set before script is executed, if responseData is set it will not be overriden anymore by a toString() on script return value</li>
 </ul>
 </p>
 
+<p>View Results Tree now considers response with missing content type as text.</p>
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Don't log spurious warning messages when using concurrent pool embedded downloads with Cache Manager or CookieManager</li>
 <li><bugzilla>54057</bugzilla>- Proxy option to set user and password at startup (-u and -a) not working with HTTPClient 4</li>
 <li><bugzilla>54187</bugzilla> - Request tab does not show headers if request fails</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>53997</bugzilla> - LDAP Extended Request: Escape ampersand (&amp;), left angle bracket (&lt;) 
 and right angle bracket (&gt;) in search filter tag in XML response data</li>
 <li><bugzilla>53995</bugzilla> - AbstractJDBCTestElement shares PreparedStatement between multi-threads</li>
 <li><bugzilla>54119</bugzilla> - HTTP 307 response is not redirected</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54088</bugzilla> - The type video/f4m is text, not binary</li>
 <li><bugzilla>54166</bugzilla> - ViewResultsTree could not render the HTML response: handle failure to parse HTML</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54058</bugzilla> - In HTTP Request Defaults, the value of field "Embedded URLs must match: is not saved if the check box "Retrieve All  Embedded Resources" is not checked.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>53975</bugzilla> - Variables replacement doesn't work with option "Delay thread creation until needed"</li>
 <li><bugzilla>54055</bugzilla> - View Results tree: = signs are stripped from parameter values at HTTP tab</li>
 <li><bugzilla>54129</bugzilla> - Search Feature does not find text although existing in elements </li>
 <li><bugzilla>54023</bugzilla> - Unable to start JMeter from a root directory and if the full path of JMeter installation contains one or more spaces (Unix/linux)</li>
 <li><bugzilla>54172</bugzilla> - Duplicate shortcut key not working and CTRL+C / CTRL+V / CTRL+V do not cancel default event</li>
 <li><bugzilla>54057</bugzilla> - Proxy option to set user and password at startup (-u and -a) not working with HTTPClient 4</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li><bugzilla>54185</bugzilla> - Allow query strings in paths that start with HTTP or HTTPS</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>54004</bugzilla> - Webservice Sampler : Allow adding headers to request with Header Manager</li>
 <li><bugzilla>54106</bugzilla> - JSR223TestElement should check for file existence when a filename is set instead of using Text Area content </li>
 <li><bugzilla>54107</bugzilla> - JSR223TestElement : Enable compilation and caching of Script Text</li>
 <li><bugzilla>54109</bugzilla> - JSR223TestElement : SampleResult properties should be set before entering script to allow user setting different code</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54131</bugzilla> - ForEach Controller : Add start and end index for looping over variables</li>
 <li><bugzilla>54132</bugzilla> - Module Controller GUI : Improve rendering of referenced controller</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54200</bugzilla> - Add support of several document types (like Apache OpenOffice's files, MS Office's files, PDF's files, etc.) 
 to the elements View Results Tree, Assertion Response and Regular Expression Extractor (in using Apache Tika)</li>
+<li><bugzilla>54226</bugzilla> - View Results Tree : Show response even when server does not return ContentType header</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>54189</bugzilla> - Add a function to quote ORO regexp meta characters</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54005</bugzilla> - HTTP Mirror Server : Add special headers "X-" to control Response status and response content</li>
 <li><bugzilla>53875</bugzilla> - Include suggested defaults for URL filters on HTTP Proxy</li>
 <li><bugzilla>54031</bugzilla> - Add tooltip to running/total threads indicator </li>
 <li>Webservice (SOAP) Request has been deprecated</li>
 <li><bugzilla>54161</bugzilla> - Proxy : be able to create binary sampler for x-www-form-urlencoded POST request</li>
 <li><bugzilla>54154</bugzilla> - HTTP Proxy Server should not force user to select the type of Sampler in HTTP Sampler Settings</li>
 <li><bugzilla>54165</bugzilla> - Proxy Server: Improve rendering of target controller</li>
 <li><bugzilla>46677</bugzilla> - Copying Test Elements between test plans</li>
 <li><bugzilla>54204</bugzilla> - Result Status Action Handler : Add start next thread loop option</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li><bugzilla>53956</bugzilla> - Add ability to paste (a list of values) from clipboard for Header Manager</li>
 <li>Updated to HttpComponents Client 4.2.2 (from 4.2.1)</li>
 <li><bugzilla>54110</bugzilla> - BSFTestElement and JSR223TestElement should use shared super-class for common fields</li>
 <li><bugzilla>54199</bugzilla> - Move to Java 6</li>
 <li>Upgraded to rhino 1.7R4</li>
 </ul>
 
 </section> 
 </body> 
 </document>
\ No newline at end of file
