diff --git a/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java b/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
index 2198a54f4..031e38455 100644
--- a/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
+++ b/src/core/org/apache/jmeter/gui/tree/JMeterTreeModel.java
@@ -1,273 +1,274 @@
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
 
 import java.util.Enumeration;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.tree.DefaultTreeModel;
 
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.gui.TestPlanGui;
 import org.apache.jmeter.control.gui.WorkBenchGui;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
 
 public class JMeterTreeModel extends DefaultTreeModel {
 
     private static final long serialVersionUID = 240L;
 
     public JMeterTreeModel(TestElement tp, TestElement wb) {
         super(new JMeterTreeNode(wb, null));
         initTree(tp,wb);
     }
 
     public JMeterTreeModel() {
         this(new TestPlanGui().createTestElement(),new WorkBenchGui().createTestElement());
     }
 
     /**
      * Hack to allow TreeModel to be used in non-GUI and headless mode.
      *
      * @deprecated - only for use by JMeter class!
      * @param o - dummy
      */
     @Deprecated
     public JMeterTreeModel(Object o) {
         this(new TestPlan(),new WorkBench());
     }
 
     /**
      * Returns a list of tree nodes that hold objects of the given class type.
      * If none are found, an empty list is returned.
      * @param type The type of nodes, which are to be collected
      * @return a list of tree nodes of the given <code>type</code>, or an empty list
      */
     public List<JMeterTreeNode> getNodesOfType(Class<?> type) {
         List<JMeterTreeNode> nodeList = new LinkedList<>();
         traverseAndFind(type, (JMeterTreeNode) this.getRoot(), nodeList);
         return nodeList;
     }
 
     /**
      * Get the node for a given TestElement object.
      * @param userObject The object to be found in this tree
      * @return the node corresponding to the <code>userObject</code>
      */
     public JMeterTreeNode getNodeOf(TestElement userObject) {
         return traverseAndFind(userObject, (JMeterTreeNode) getRoot());
     }
 
     /**
      * Adds the sub tree at the given node. Returns a boolean indicating whether
      * the added sub tree was a full test plan.
      * 
      * @param subTree
      *            The {@link HashTree} which is to be inserted into
      *            <code>current</code>
      * @param current
      *            The node in which the <code>subTree</code> is to be inserted.
      *            Will be overridden, when an instance of {@link TestPlan} or
      *            {@link WorkBench} is found in the subtree.
      * @return newly created sub tree now found at <code>current</code>
      * @throws IllegalUserActionException
      *             when <code>current</code> is not an instance of
      *             {@link AbstractConfigGui} and no instance of {@link TestPlan}
      *             or {@link WorkBench} could be found in the
      *             <code>subTree</code>
      */
     public HashTree addSubTree(HashTree subTree, JMeterTreeNode current) throws IllegalUserActionException {
         for (Object o : subTree.list()) {
             TestElement item = (TestElement) o;
             if (item instanceof TestPlan) {
                 TestPlan tp = (TestPlan) item;
                 current = (JMeterTreeNode) ((JMeterTreeNode) getRoot()).getChildAt(0);
                 final TestPlan userObject = (TestPlan) current.getUserObject();
                 userObject.addTestElement(item);
                 userObject.setName(item.getName());
                 userObject.setFunctionalMode(tp.isFunctionalMode());
                 userObject.setSerialized(tp.isSerialized());
                 addSubTree(subTree.getTree(item), current);
             } else if (item instanceof WorkBench) {
                 current = (JMeterTreeNode) ((JMeterTreeNode) getRoot()).getChildAt(1);
                 final TestElement testElement = (TestElement) current.getUserObject();
                 testElement.addTestElement(item);
                 testElement.setName(item.getName());
                 addSubTree(subTree.getTree(item), current);
             } else {
                 addSubTree(subTree.getTree(item), addComponent(item, current));
             }
         }
         return getCurrentSubTree(current);
     }
 
     /**
      * Add a {@link TestElement} to a {@link JMeterTreeNode}
      * @param component The {@link TestElement} to be used as data for the newly created node
      * @param node The {@link JMeterTreeNode} into which the newly created node is to be inserted
      * @return new {@link JMeterTreeNode} for the given <code>component</code>
      * @throws IllegalUserActionException
      *             when the user object for the <code>node</code> is not an instance
      *             of {@link AbstractConfigGui}
      */
     public JMeterTreeNode addComponent(TestElement component, JMeterTreeNode node) throws IllegalUserActionException {
         if (node.getUserObject() instanceof AbstractConfigGui) {
             throw new IllegalUserActionException("This node cannot hold sub-elements");
         }
 
         GuiPackage guiPackage = GuiPackage.getInstance();
         if (guiPackage != null) {
             // The node can be added in non GUI mode at startup
             guiPackage.updateCurrentNode();
             JMeterGUIComponent guicomp = guiPackage.getGui(component);
+            guicomp.clearGui();
             guicomp.configure(component);
             guicomp.modifyTestElement(component);
             guiPackage.getCurrentGui(); // put the gui object back
                                         // to the way it was.
         }
         JMeterTreeNode newNode = new JMeterTreeNode(component, this);
 
         // This check the state of the TestElement and if returns false it
         // disable the loaded node
         try {
             newNode.setEnabled(component.isEnabled());
         } catch (Exception e) { // TODO - can this ever happen?
             newNode.setEnabled(true);
         }
 
         this.insertNodeInto(newNode, node, node.getChildCount());
         return newNode;
     }
 
     public void removeNodeFromParent(JMeterTreeNode node) {
         if (!(node.getUserObject() instanceof TestPlan) && !(node.getUserObject() instanceof WorkBench)) {
             super.removeNodeFromParent(node);
         }
     }
 
     private void traverseAndFind(Class<?> type, JMeterTreeNode node, List<JMeterTreeNode> nodeList) {
         if (type.isInstance(node.getUserObject())) {
             nodeList.add(node);
         }
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             traverseAndFind(type, child, nodeList);
         }
     }
 
     private JMeterTreeNode traverseAndFind(TestElement userObject, JMeterTreeNode node) {
         if (userObject == node.getUserObject()) {
             return node;
         }
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             JMeterTreeNode result = traverseAndFind(userObject, child);
             if (result != null) {
                 return result;
             }
         }
         return null;
     }
 
     /**
      * Get the current sub tree for a {@link JMeterTreeNode}
      * @param node The {@link JMeterTreeNode} from which the sub tree is to be taken 
      * @return newly copied sub tree
      */
     public HashTree getCurrentSubTree(JMeterTreeNode node) {
         ListedHashTree hashTree = new ListedHashTree(node);
         Enumeration<JMeterTreeNode> enumNode = node.children();
         while (enumNode.hasMoreElements()) {
             JMeterTreeNode child = enumNode.nextElement();
             hashTree.add(node, getCurrentSubTree(child));
         }
         return hashTree;
     }
 
     /**
      * Get the {@link TestPlan} from the root of this tree
      * @return The {@link TestPlan} found at the root of this tree
      */
     public HashTree getTestPlan() {
         return getCurrentSubTree((JMeterTreeNode) ((JMeterTreeNode) this.getRoot()).getChildAt(0));
     }
 
     /**
      * Get the {@link WorkBench} from the root of this tree
      * @return The {@link WorkBench} found at the root of this tree
      */
     public HashTree getWorkBench() {
         return getCurrentSubTree((JMeterTreeNode) ((JMeterTreeNode) this.getRoot()).getChildAt(1));
     }
 
     /**
      * Clear the test plan, and use default node for test plan and workbench.
      *
      * N.B. Should only be called by {@link GuiPackage#clearTestPlan()}
      */
     public void clearTestPlan() {
         TestElement tp = new TestPlanGui().createTestElement();
         clearTestPlan(tp);
     }
 
     /**
      * Clear the test plan, and use specified node for test plan and default node for workbench
      *
      * N.B. Should only be called by {@link GuiPackage#clearTestPlan(TestElement)}
      *
      * @param testPlan the node to use as the testplan top node
      */
     public void clearTestPlan(TestElement testPlan) {
         // Remove the workbench and testplan nodes
         int children = getChildCount(getRoot());
         while (children > 0) {
             JMeterTreeNode child = (JMeterTreeNode)getChild(getRoot(), 0);
             super.removeNodeFromParent(child);
             children = getChildCount(getRoot());
         }
         // Init the tree
         initTree(testPlan,new WorkBenchGui().createTestElement()); // Assumes this is only called from GUI mode
     }
 
     /**
      * Initialize the model with nodes for testplan and workbench.
      *
      * @param tp the element to use as testplan
      * @param wb the element to use as workbench
      */
     private void initTree(TestElement tp, TestElement wb) {
         // Insert the test plan node
         insertNodeInto(new JMeterTreeNode(tp, this), (JMeterTreeNode) getRoot(), 0);
         // Insert the workbench node
         insertNodeInto(new JMeterTreeNode(wb, this), (JMeterTreeNode) getRoot(), 1);
         // Let others know that the tree content has changed.
         // This should not be necessary, but without it, nodes are not shown when the user
         // uses the Close menu item
         nodeStructureChanged((JMeterTreeNode)getRoot());
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 557b6fd93..6dd79f92b 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,387 +1,388 @@
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
 <!DOCTYPE document
 [
 <!ENTITY hellip   "&#x02026;" >
 <!ENTITY rarr     "&#x02192;" >
 <!ENTITY vellip   "&#x022EE;" >
 ]>
 <document>
 <properties>
     <author email="dev AT jmeter.apache.org">JMeter developers</author>
     <title>Changes</title>
 </properties>
 <body>
 <section name="Changes">
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 3.2 =================== -->
 
 <h1>Version 3.2</h1>
 <p>
 Summary
 </p>
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Known problems and workarounds">Known problems and workarounds</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_section>IMPORTANT CHANGE</ch_section>
 <p>
 JMeter now requires Java 8. Ensure you use most up to date version.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>PDF Documentations have been migrated to HTML user manual</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>JMeter logging has been migrated to SLF4J and Log4j2, this involves changes in the way configuration is done. JMeter now relies on standard
     <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html">log4j2 configuration</a> in file <code>log4j2.xml</code>
     </li>
     <li>The following jars have been removed after migration from Logkit to SLF4J (see <bugzilla>60589</bugzilla>):
         <ul>
             <li>ApacheJMeter_slf4j_logkit.jar</li>
             <li>avalon-framework-4.1.4.jar</li>
             <li>avalon-framework-4.1.4.jar</li>
             <li>commons-logging-1.2.jar</li>
             <li>excalibur-logger-1.1.jar</li>
             <li>logkit-2.0.jar</li>
         </ul>
     </li>
     <li>The <code>commons-httpclient-3.1.jar</code> has been removed after drop of HC3.1 support(see <bugzilla>60727</bugzilla>)</li>
     <li>JMeter now sets through <code>-Djava.security.egd=file:/dev/urandom</code> the algorithm for secure random</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
     <li>JMeter now uses by default Oracle Nashorn engine instead of Mozilla Rhino for better performances. This should not have an impact unless
     you use some advanced features. You can revert back to Rhino by settings property <code>javascript.use_rhino=true</code>. 
     You can read this <a href="https://wiki.openjdk.java.net/display/Nashorn/Rhino+Migration+Guide">migration guide</a> for more details on Nashorn. See <bugzilla>60672</bugzilla></li>
     <li><bug>60729</bug>The Random Variable Config Element now allows minimum==maximum. Previous versions logged an error when minimum==maximum and did not set the configured variable.</li>
     <li><bug>60730</bug>The JSON PostProcessor now sets the <code>_ALL</code> variable (assuming <code>Compute concatenation var</code> was checked)
     even if the JSON path matches only once. Previous versions did not set the <code>_ALL</code> variable in this case.</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li>SOAP/XML-RPC Request has been removed as part of <bugzilla>60727</bugzilla>. Use HTTP Request element as a replacement. See <a href="./build-ws-test-plan.html" >Building a WebService Test Plan</a></li>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
     <li>Drop unused methods <code>org.apache.jmeter.protocol.http.control.HeaderManager#getSOAPHeader</code>
     and <code>org.apache.jmeter.protocol.http.control.HeaderManager#setSOAPHeader(Object)</code>
     </li>
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
     <li><bug>57242</bug>HTTP Authorization is not pre-emptively set with HttpClient4</li>
     <li><bug>60727</bug>Drop commons-httpclient-3.1 and related elements. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 	<li><bug>60740</bug>Support variable for all JMS messages (bytes, object, ...) and sources (file, folder), based on <pr>241</pr>. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60585</bug>JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors. Based on <pr>240</pr> from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><pr>259</pr>Refactored and reformatted SmtpSampler. Contributed by Graham Russell (graham at ham1.co.uk)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>60672</bug>JavaScript function / IfController : use Nashorn engine by default</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
     <li><bug>60542</bug>View Results Tree : Allow Upper Panel to be collapsed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a <pr>245</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60590</bug>BackendListener : Add Influxdb BackendListenerClient implementation to JMeter. Partly based on <pr>246</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60591</bug>BackendListener : Add a time boxed sampling. Based on a <pr>237</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60678</bug>View Results Tree : Text renderer, search should not popup "Text Not Found"</li>
     <li><bug>60691</bug>View Results Tree : In Renderers (XPath, JSON Path Tester, RegExp Tester and CSS/JQuery Tester) lower panel is sometimes not visible as upper panel is too big and cannot be resized</li>
     <li><bug>60687</bug>Keep GUI responsive when many events are processed by View Results Tree, Summary Table and Log Panel.</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
     <li><bug>58943</bug>Create a Better Think Time experience. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60602</bug>XPath Extractor : Add Match No. to allow extraction randomly, by index or all matches</li>
     <li><bug>60710</bug>XPath Extractor : When content on which assertion applies is not XML, in View Results Tree the extractor is marked in Red and named SAXParseException. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60712</bug>Response Assertion : Improve Renderer of Patterns</li>
     <li><bug>59174</bug>Add a table with static hosts to the DNS Cache Manager. This enables better virtual hosts testing with HttpClient4.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
     <li>Improve translation "save_as" in French. Based on a <pr>252</pr> by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>60637</bug>Improve Statistics table design <figure image="dashboard/report_statistics.png" ></figure></li>
 </ul>
 
 <h3>General</h3>
 <ul>
 	<li><bug>58164</bug>Check if output file exist on all listener before start the loadtest</li>	
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60514</bug>Ability to apply a naming convention on Children of a Transaction Controller. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60711</bug>Improve Delete button behaviour for Assertions / Header Manager / User Parameters GUIs</li>
     <li><bug>60593</bug>Switch to G1 GC algorithm</li>
     <li><bug>60595</bug>Add a SplashScreen at the start of JMeter GUI. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>55258</bug>Drop "Close" icon from toolbar and add "New" to menu. Partly based on contribution from Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
     <li><bug>59995</bug>Allow user to change font size with 2 new menu items and use <code>jmeter.hidpi.scale.factor</code> for scaling fonts. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60654</bug>Validation Feature : Be able to ignore BackendListener. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60646</bug>Workbench : Save it by default</li>
     <li><bug>60684</bug>Thread Group: Validate ended prematurely by Scheduler with 0 or very short duration. Contributed by Andrew Burton (andrewburtonatwh at gmail.com).</li>
     <li><bug>60589</bug>Migrate LogKit to SLF4J - Drop avalon, logkit and excalibur with backward compatibility for 3rd party modules. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60565</bug>Migrate LogKit to SLF4J - Optimize logging statements. e.g, message format args, throwable args, unnecessary if-enabled-logging in simple ones, etc. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60564</bug>Migrate LogKit to SLF4J - Replace logkit loggers with slf4j ones and keep the current logkit binding solution for backward compatibility with plugins. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60664</bug>Add a UI menu to set log level. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><pr>276</pr>Added some translations for polish locale. Contributed by Bartosz Siewniak (barteksiewniak at gmail.com)</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to dnsjava-2.1.8.jar (from 2.1.7)</li>
     <li>Updated to groovy 2.4.8 (from 2.4.7)</li>
     <li>Updated to httpcore 4.4.6 (from 4.4.5)</li>
     <li>Updated to httpclient 4.5.3 (from 4.5.2)</li>
     <li>Updated to jodd 3.8.1 (from 3.8.1.jar)</li>
     <li>Updated to jsoup-1.10.2 (from 1.10.1)</li>
     <li>Updated to ph-css 5.0.3 (from 4.1.6)</li>
     <li>Updated to ph-commons 8.6.0 (from 6.2.4)</li>
     <li>Updated to slf4j-api 1.7.22 (from 1.7.21)</li>
     <li>Updated to asm 5.2 (from 5.1)</li>
     <li>Updated to rsyntaxtextarea-2.6.1 (from 2.6.0)</li>
     <li>Updated to commons-net-3.6 (from 3.5)</li>
     <li>Converted the old pdf tutorials to xml.</li>
     <li><pr>255</pr>Utilised Java 8 (and 7) features to tidy up code. Contributed by Graham Russell (graham at ham1.co.uk)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
     <li><bug>60575</bug>HTTP GET Requests could have a content-type header without a body.</li>
     <li><bug>60682</bug>HTTP Request : Get method may fail on redirect due to Content-Length header being set</li>
     <li><bug>60643</bug>HTTP(S) Test Script Recorder doesn't correctly handle restart or start after stop. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60652</bug>PUT might leak file descriptors.</li>
     <li><bug>60689</bug><code>httpclient4.validate_after_inactivity</code> has no impact leading to usage of potentially stale/closed connections</li>
     <li><bug>60690</bug>Default values for "httpclient4.validate_after_inactivity" and "httpclient4.time_to_live" which are equal to each other makes validation useless</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
     <li><bug>55652</bug>JavaSampler silently resets classname if class can not be found</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60648</bug>GraphiteBackendListener can lose some metrics at end of test if test is very short</li>
     <li><bug>60650</bug>AbstractBackendListenerClient does not reset UserMetric between runs</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
     <li><bug>60607</bug>DNS Cache Manager configuration is ignored</li>
     <li><bug>60729</bug>The Random Variable Config Element should allow minimum==maximum</li>
     <li><bug>60730</bug>The JSON PostProcessor should set the <code>_ALL</code> variable even if the JSON path matches only once.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>60726</bug>Report / Dashboard : Top 5 errors by samplers must not take into account the series filtering</li>
 </ul>
     
 <h3>General</h3>
 <ul>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of TestHTTPMirrorThread#testSleep(). Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
     <li><bug>60621</bug>The "report-template" folder is missing from ApacheJMeter_config-3.1.jar in maven central</li>
+    <li><bug>60744</bug>GUI elements are not cleaned up when reused during load of Test Plan which can lead them to be partially initialized with a previous state for a new Test Element</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Jerome Loisel (loisel.jerome at gmail.com)</li>
 <li>Liu XP (liu_xp2003 at sina.com)</li>
 <li>Qi Chen (qi.chensh at ele.me)</li>
 <li>(gavin at 16degrees.com.au)</li>
 <li>Thomas Schapitz (ts-nospam12 at online.de)</li>
 <li>Murdecai777 (https://github.com/Murdecai777)</li>
 <li>Logan Mauzaize (logan.mauzaize at gmail.com)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
 <li>Andrew Burton (andrewburtonatwh at gmail.com)</li>
 <li>Woonsan Ko (woonsan at apache.org)</li>
 <li>Bartosz Siewniak (barteksiewniak at gmail.com)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 <li>Tuukka Mustonen (tuukka.mustonen at gmail.com) who gave us a lot of useful feedback which helped resolve <bugzilla>60689</bugzilla> and <bugzilla>60690</bugzilla></li>
 <li>Amar Darisa (amar.darisa at gmail.com) who helped us with his feedback on <bugzilla>60682</bugzilla></li>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
 
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads,
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a>
 The fix is to use JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later (but be aware that Java 9 is not certified yet for JMeter).
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a>
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
 
 </section>
 </body>
 </document>
