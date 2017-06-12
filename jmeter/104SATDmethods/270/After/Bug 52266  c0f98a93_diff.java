diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index 89b9875eb..d336fc570 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,407 +1,410 @@
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
     public void add(final SampleResult sample) {
         JMeterUtils.runSafe(new Runnable() {
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
     public synchronized void clearData() {
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
-        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
+        DefaultMutableTreeNode node = null;
+        synchronized (this) {
+            node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
+        }
 
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
     public void itemStateChanged(ItemEvent e) {
         // NOOP state is held by component
     }
 }
diff --git a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElement.java b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElement.java
index 0620b0f22..8be4aeff2 100644
--- a/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElement.java
+++ b/src/protocol/jdbc/org/apache/jmeter/protocol/jdbc/config/DataSourceElement.java
@@ -1,490 +1,492 @@
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
 package org.apache.jmeter.protocol.jdbc.config;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.avalon.excalibur.datasource.DataSourceComponent;
 import org.apache.avalon.excalibur.datasource.ResourceLimitingJdbcDataSource;
 import org.apache.avalon.framework.configuration.Configuration;
 import org.apache.avalon.framework.configuration.ConfigurationException;
 import org.apache.avalon.framework.configuration.DefaultConfiguration;
 import org.apache.avalon.framework.logger.LogKitLogger;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class DataSourceElement extends AbstractTestElement
     implements ConfigElement, TestStateListener, TestBean
     {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 233L;
 
     private transient String dataSource, driver, dbUrl, username, password, checkQuery, poolMax, connectionAge, timeout,
             trimInterval,transactionIsolation;
 
     private transient boolean keepAlive, autocommit;
 
     /*
      *  The datasource is set up by testStarted and cleared by testEnded.
      *  These are called from different threads, so access must be synchronized.
      *  The same instance is called in each case.
     */
     private transient ResourceLimitingJdbcDataSource excaliburSource;
 
     // Keep a record of the pre-thread pools so that they can be disposed of at the end of a test
     private transient Set<ResourceLimitingJdbcDataSource> perThreadPoolSet;
 
     public DataSourceElement() {
     }
 
     public void testEnded() {
         synchronized (this) {
             if (excaliburSource != null) {
                 excaliburSource.dispose();
             }
             excaliburSource = null;
         }
         if (perThreadPoolSet != null) {// in case
             for(ResourceLimitingJdbcDataSource dsc : perThreadPoolSet){
                 log.debug("Disposing pool: "+dsc.getInstrumentableName()+" @"+System.identityHashCode(dsc));
                 dsc.dispose();
             }
             perThreadPoolSet=null;
         }
     }
 
     public void testEnded(String host) {
         testEnded();
     }
 
     @SuppressWarnings("deprecation") // call to TestBeanHelper.prepare() is intentional
     public void testStarted() {
         this.setRunningVersion(true);
         TestBeanHelper.prepare(this);
         JMeterVariables variables = getThreadContext().getVariables();
         String poolName = getDataSource();
         if (variables.getObject(poolName) != null) {
             log.error("JDBC data source already defined for: "+poolName);
         } else {
             String maxPool = getPoolMax();
             perThreadPoolSet = Collections.synchronizedSet(new HashSet<ResourceLimitingJdbcDataSource>());
             if (maxPool.equals("0")){ // i.e. if we want per thread pooling
                 variables.putObject(poolName, new DataSourceComponentImpl()); // pool will be created later
             } else {
                 ResourceLimitingJdbcDataSource src=initPool(maxPool);
                 synchronized(this){
                     excaliburSource = src;
                     variables.putObject(poolName, new DataSourceComponentImpl(excaliburSource));
                 }
             }
         }
     }
 
     public void testStarted(String host) {
         testStarted();
     }
 
     @Override
     public Object clone() {
         DataSourceElement el = (DataSourceElement) super.clone();
-        el.excaliburSource = excaliburSource;
-        el.perThreadPoolSet = perThreadPoolSet;
+        synchronized (this) {
+            el.excaliburSource = excaliburSource;
+            el.perThreadPoolSet = perThreadPoolSet;            
+        }
         return el;
     }
 
     /*
      * Utility routine to get the connection from the pool.
      * Purpose:
      * - allows JDBCSampler to be entirely independent of the pooling classes
      * - allows the pool storage mechanism to be changed if necessary
      */
     public static Connection getConnection(String poolName) throws SQLException{
         DataSourceComponent pool = (DataSourceComponent)
             JMeterContextService.getContext().getVariables().getObject(poolName);
         if (pool == null) {
             throw new SQLException("No pool found named: '" + poolName + "'");
         }
         return pool.getConnection();
     }
 
     /*
      * Set up the DataSource - maxPool is a parameter, so the same code can
      * also be used for setting up the per-thread pools.
     */
     private ResourceLimitingJdbcDataSource initPool(String maxPool) {
         ResourceLimitingJdbcDataSource source = null;
         source = new ResourceLimitingJdbcDataSource();
         DefaultConfiguration config = new DefaultConfiguration("rl-jdbc"); // $NON-NLS-1$
 
         if (log.isDebugEnabled()) {
             StringBuilder sb = new StringBuilder(40);
             sb.append("MaxPool: ");
             sb.append(maxPool);
             sb.append(" Timeout: ");
             sb.append(getTimeout());
             sb.append(" TrimInt: ");
             sb.append(getTrimInterval());
             sb.append(" Auto-Commit: ");
             sb.append(isAutocommit());
             log.debug(sb.toString());
         }
         DefaultConfiguration poolController = new DefaultConfiguration("pool-controller"); // $NON-NLS-1$
         poolController.setAttribute("max", maxPool); // $NON-NLS-1$
         poolController.setAttribute("max-strict", "true"); // $NON-NLS-1$ $NON-NLS-2$
         poolController.setAttribute("blocking", "true"); // $NON-NLS-1$ $NON-NLS-2$
         poolController.setAttribute("timeout", getTimeout()); // $NON-NLS-1$
         poolController.setAttribute("trim-interval", getTrimInterval()); // $NON-NLS-1$
         config.addChild(poolController);
 
         DefaultConfiguration autoCommit = new DefaultConfiguration("auto-commit"); // $NON-NLS-1$
         autoCommit.setValue(String.valueOf(isAutocommit()));
         config.addChild(autoCommit);
 
         if (log.isDebugEnabled()) {
             StringBuilder sb = new StringBuilder(40);
             sb.append("KeepAlive: ");
             sb.append(isKeepAlive());
             sb.append(" Age: ");
             sb.append(getConnectionAge());
             sb.append(" CheckQuery: ");
             sb.append(getCheckQuery());
             log.debug(sb.toString());
         }
         DefaultConfiguration cfgKeepAlive = new DefaultConfiguration("keep-alive"); // $NON-NLS-1$
         cfgKeepAlive.setAttribute("disable", String.valueOf(!isKeepAlive())); // $NON-NLS-1$
         cfgKeepAlive.setAttribute("age", getConnectionAge()); // $NON-NLS-1$
         cfgKeepAlive.setValue(getCheckQuery());
         poolController.addChild(cfgKeepAlive);
 
         String _username = getUsername();
         if (log.isDebugEnabled()) {
             StringBuilder sb = new StringBuilder(40);
             sb.append("Driver: ");
             sb.append(getDriver());
             sb.append(" DbUrl: ");
             sb.append(getDbUrl());
             sb.append(" User: ");
             sb.append(_username);
             log.debug(sb.toString());
         }
         DefaultConfiguration cfgDriver = new DefaultConfiguration("driver"); // $NON-NLS-1$
         cfgDriver.setValue(getDriver());
         config.addChild(cfgDriver);
         DefaultConfiguration cfgDbUrl = new DefaultConfiguration("dburl"); // $NON-NLS-1$
         cfgDbUrl.setValue(getDbUrl());
         config.addChild(cfgDbUrl);
 
         if (_username.length() > 0){
             DefaultConfiguration cfgUsername = new DefaultConfiguration("user"); // $NON-NLS-1$
             cfgUsername.setValue(_username);
             config.addChild(cfgUsername);
             DefaultConfiguration cfgPassword = new DefaultConfiguration("password"); // $NON-NLS-1$
             cfgPassword.setValue(getPassword());
             config.addChild(cfgPassword);
         }
 
         // log is required to ensure errors are available
         source.enableLogging(new LogKitLogger(log));
         try {
             source.configure(config);
             source.setInstrumentableName(getDataSource());
         } catch (ConfigurationException e) {
             log.error("Could not configure datasource for pool: "+getDataSource(),e);
         }
         return source;
     }
 
     // used to hold per-thread singleton connection pools
     private static final ThreadLocal<Map<String, ResourceLimitingJdbcDataSource>> perThreadPoolMap =
         new ThreadLocal<Map<String, ResourceLimitingJdbcDataSource>>(){
         @Override
         protected Map<String, ResourceLimitingJdbcDataSource> initialValue() {
             return new HashMap<String, ResourceLimitingJdbcDataSource>();
         }
     };
 
     /*
      * Wrapper class to allow getConnection() to be implemented for both shared
      * and per-thread pools.
      *
      */
     private class DataSourceComponentImpl implements DataSourceComponent{
 
         private final ResourceLimitingJdbcDataSource sharedDSC;
 
         DataSourceComponentImpl(){
             sharedDSC=null;
         }
 
         DataSourceComponentImpl(ResourceLimitingJdbcDataSource p_dsc){
             sharedDSC=p_dsc;
         }
 
         public Connection getConnection() throws SQLException {
             Connection conn = null;
             ResourceLimitingJdbcDataSource dsc = null;
             if (sharedDSC != null){ // i.e. shared pool
                 dsc = sharedDSC;
             } else {
                 Map<String, ResourceLimitingJdbcDataSource> poolMap = perThreadPoolMap.get();
                 dsc = poolMap.get(getDataSource());
                 if (dsc == null){
                     dsc = initPool("1");
                     poolMap.put(getDataSource(),dsc);
                     log.debug("Storing pool: "+dsc.getInstrumentableName()+" @"+System.identityHashCode(dsc));
                     perThreadPoolSet.add(dsc);
                 }
             }
             if (dsc != null) {
                 conn=dsc.getConnection();
                 int transactionIsolation = DataSourceElementBeanInfo.getTransactionIsolationMode(getTransactionIsolation());
                 if (transactionIsolation >= 0 && conn.getTransactionIsolation() != transactionIsolation) {
                     try {
                         // make sure setting the new isolation mode is done in an auto committed transaction
                         conn.setTransactionIsolation(transactionIsolation);
                         log.debug("Setting transaction isolation: " + transactionIsolation + " @"
                                 + System.identityHashCode(dsc));
                     } catch (SQLException ex) {
                         log.error("Could not set transaction isolation: " + transactionIsolation + " @"
                                 + System.identityHashCode(dsc));
                     }   
                 }
             }
             return conn;
         }
 
         public void configure(Configuration arg0) throws ConfigurationException {
         }
 
     }
 
     public void addConfigElement(ConfigElement config) {
     }
 
     public boolean expectsModification() {
         return false;
     }
 
     /**
      * @return Returns the checkQuery.
      */
     public String getCheckQuery() {
         return checkQuery;
     }
 
     /**
      * @param checkQuery
      *            The checkQuery to set.
      */
     public void setCheckQuery(String checkQuery) {
         this.checkQuery = checkQuery;
     }
 
     /**
      * @return Returns the connectionAge.
      */
     public String getConnectionAge() {
         return connectionAge;
     }
 
     /**
      * @param connectionAge
      *            The connectionAge to set.
      */
     public void setConnectionAge(String connectionAge) {
         this.connectionAge = connectionAge;
     }
 
     /**
      * @return Returns the poolname.
      */
     public String getDataSource() {
         return dataSource;
     }
 
     /**
      * @param dataSource
      *            The poolname to set.
      */
     public void setDataSource(String dataSource) {
         this.dataSource = dataSource;
     }
 
     /**
      * @return Returns the dbUrl.
      */
     public String getDbUrl() {
         return dbUrl;
     }
 
     /**
      * @param dbUrl
      *            The dbUrl to set.
      */
     public void setDbUrl(String dbUrl) {
         this.dbUrl = dbUrl;
     }
 
     /**
      * @return Returns the driver.
      */
     public String getDriver() {
         return driver;
     }
 
     /**
      * @param driver
      *            The driver to set.
      */
     public void setDriver(String driver) {
         this.driver = driver;
     }
 
     /**
      * @return Returns the password.
      */
     public String getPassword() {
         return password;
     }
 
     /**
      * @param password
      *            The password to set.
      */
     public void setPassword(String password) {
         this.password = password;
     }
 
     /**
      * @return Returns the poolMax.
      */
     public String getPoolMax() {
         return poolMax;
     }
 
     /**
      * @param poolMax
      *            The poolMax to set.
      */
     public void setPoolMax(String poolMax) {
         this.poolMax = poolMax;
     }
 
     /**
      * @return Returns the timeout.
      */
     public String getTimeout() {
         return timeout;
     }
 
     /**
      * @param timeout
      *            The timeout to set.
      */
     public void setTimeout(String timeout) {
         this.timeout = timeout;
     }
 
     /**
      * @return Returns the trimInterval.
      */
     public String getTrimInterval() {
         return trimInterval;
     }
 
     /**
      * @param trimInterval
      *            The trimInterval to set.
      */
     public void setTrimInterval(String trimInterval) {
         this.trimInterval = trimInterval;
     }
 
     /**
      * @return Returns the username.
      */
     public String getUsername() {
         return username;
     }
 
     /**
      * @param username
      *            The username to set.
      */
     public void setUsername(String username) {
         this.username = username;
     }
 
     /**
      * @return Returns the autocommit.
      */
     public boolean isAutocommit() {
         return autocommit;
     }
 
     /**
      * @param autocommit
      *            The autocommit to set.
      */
     public void setAutocommit(boolean autocommit) {
         this.autocommit = autocommit;
     }
 
     /**
      * @return Returns the keepAlive.
      */
     public boolean isKeepAlive() {
         return keepAlive;
     }
 
     /**
      * @param keepAlive
      *            The keepAlive to set.
      */
     public void setKeepAlive(boolean keepAlive) {
         this.keepAlive = keepAlive;
     }
 
     /**
      * @return the transaction isolation level
      */
     public String getTransactionIsolation() {
         return transactionIsolation;
     }
 
     /**
      * @param transactionIsolation The transaction isolation level to set. <code>NULL</code> to
      * use the default of the driver.
      */
     public void setTransactionIsolation(String transactionIsolation) {
         this.transactionIsolation = transactionIsolation;
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 35aaa6d96..3f3b642a8 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,189 +1,190 @@
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
 
 
 <!--  =================== 2.8 =================== -->
 
 <h1>Version 2.8</h1>
 
 <h2>New and Noteworthy</h2>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see Bug 52496).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>
 When using CacheManager, JMeter now caches responses for GET queries provided header Cache-Control is different from "no-cache" as described in specification.
 Furthermore it doesn't put anymore in Cache deprecated entries for "no-cache" responses. See <bugzilla>53521</bugzilla> and <bugzilla>53522</bugzilla> 
 </p>
 
 <p>
 A major change has occured on JSR223 Test Elements, previously variables set up before script execution where stored in ScriptEngineManager which was created once per execution, 
 now ScriptEngineManager is a singleton shared by all JSR223 elements and only ScriptEngine is created once per execution, variables set up before script execution are now stored 
 in Bindings created on each execution, see <bugzilla>53365</bugzilla>.
 </p>
 
 <p>
 JSR223 Test Elements using Script file are now Compiled if ScriptEngine supports this feature, see <bugzilla>53520</bugzilla>.
 </p>
 
 <p>
 Shortcut for Function Helper Dialog is now CTRL+F1 (CMD + F1 for Mac OS), CTRL+F (CMD+F1 for Mac OS) now opens Search Dialog.
 </p>
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>53521</bugzilla> - Cache Manager should cache content with Cache-control=private</li>
 <li><bugzilla>53522</bugzilla> - Cache Manager should not store at all response with header "no-cache" and store other types of Cache-Control having max-age value</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>53348</bugzilla> - JMeter JMS Point-to-Point Request-Response sampler doesn't work when Request-queue and Receive-queue are different</li>
 <li><bugzilla>53357</bugzilla> - JMS Point to Point reports too high response times in Request Response Mode</li>
 <li><bugzilla>53440</bugzilla> - SSL connection leads to ArrayStoreException on JDK 6 with some KeyManagerFactory SPI</li>
 <li><bugzilla>53511</bugzilla> - access log sampler SessionFilter throws NullPointerException - cookie manager not initialized properly</li>
 <li><bugzilla>53715</bugzilla> - JMeter does not load WSDL</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>53742</bugzilla> - When jmeter.save.saveservice.sample_count is set to true, elapsed time read by listener is always equal to 0</li>
 <li><bugzilla>53774</bugzilla> - RequestViewRaw does not show headers unless samplerData is non-null</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>51512</bugzilla> - Cookies aren't inserted into HTTP request with IPv6 Host header</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>53365</bugzilla> - JSR223TestElement should cache ScriptEngineManager</li>
 <li><bugzilla>53520</bugzilla> - JSR223 Elements : Use Compilable interface to improve performances on File scripts</li>
 <li><bugzilla>53501</bugzilla> - Synchronization timer blocks test end.</li>
 <li><bugzilla>53750</bugzilla> - TestCompiler saves unnecessary entries in pairing collection</li>
+<li><bugzilla>52266</bugzilla> - Code:Inconsistent synchronization</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li><bugzilla>53675</bugzilla> - Add PATCH verb to HTTP sampler</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>55310</bugzilla> - TestAction should implement Interruptible</li>
 <li><bugzilla>53318</bugzilla> - Add Embedded URL Filter to HTTP Request Defaults Control </li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>53566</bugzilla> - Don't log partial responses to the jmeter log</li>
 <li><bugzilla>53716</bugzilla> - Small improvements in aggregate graph: legend at left or right is now on 1 column (instead of 1 large line), no border to the reference's square color, reduce width on some fields</li>
 <li><bugzilla>53718</bugzilla> - Add a new visualizer 'Response Time Graph' to draw a line graph showing the evolution of response time for a test</li>
 <li><bugzilla>53738</bugzilla> - Keep track of number of threads started and finished</li>
 <li><bugzilla>53753</bugzilla> -  Summariser: no point displaying fractional time in most cases</li>
 <li><bugzilla>53749</bugzilla> - TestListener interface could perhaps be split up. 
 This should reduce per-thread memory requirements and processing, 
 as only test elements that actually use testIterationStart functionality now need to be handled.</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>53755</bugzilla> - Adding a HttpClient 4 cookie implementation in JMeter. 
 Cookie Manager has now the default HC3.1 implementation and a new choice HC4 implementation (compliant with IPv6 address)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>53364</bugzilla> - Sort list of Functions in Function Helper Dialog</li>
 <li><bugzilla>53418</bugzilla> - New Option "Delay thread creation until needed" that will create and start threads when needed instead of creating them on Test startup</li>
 <li><bugzilla>42245</bugzilla> - Show clear passwords in HTTP Authorization Manager</li>
 <li><bugzilla>53616</bugzilla> - Display 'Apache JMeter' title in app title bar in Gnome 3</li>
 <li><bugzilla>53759</bugzilla> - ClientJMeterEngine perfoms unnecessary traverse using SearchByClass(TestListener)</li>
 <li><bugzilla>52601</bugzilla> - CTRL + F for the new Find feature</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li><bugzilla>53311</bugzilla> - JMeterUtils#runSafe should not throw Error when interrupted</li>
 <li>Updated to commons-net-3.1 (from 3.0.1)</li>
 <li>Updated to HttpComponents Core 4.2.1 (from 4.1.4) and HttpComponents Client 4.2.1 (from 4.1.3)</li>
 <li><bugzilla>53765</bugzilla> - Switch to commons-lang3-3.1</li>
 </ul>
 
 </section> 
 </body> 
 </document>
\ No newline at end of file
