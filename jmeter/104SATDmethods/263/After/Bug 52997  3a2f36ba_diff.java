diff --git a/src/core/org/apache/jmeter/gui/action/Save.java b/src/core/org/apache/jmeter/gui/action/Save.java
index 7fe3c95c1..8cff5dc73 100644
--- a/src/core/org/apache/jmeter/gui/action/Save.java
+++ b/src/core/org/apache/jmeter/gui/action/Save.java
@@ -1,165 +1,170 @@
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
 import java.io.File;
 import java.io.FileOutputStream;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 import javax.swing.JOptionPane;
 
 import org.apache.commons.io.FilenameUtils;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Save the current test plan; implements:
  * Save
  * Save TestPlan As
  * Save (Selection) As
  */
 public class Save implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String JMX_FILE_EXTENSION = ".jmx"; // $NON-NLS-1$
 
     private static final Set<String> commands = new HashSet<String>();
 
     static {
         commands.add(ActionNames.SAVE_AS); // Save (Selection) As
         commands.add(ActionNames.SAVE_ALL_AS); // Save TestPlan As
         commands.add(ActionNames.SAVE); // Save
     }
 
     /**
      * Constructor for the Save object.
      */
     public Save() {
     }
 
     /**
      * Gets the ActionNames attribute of the Save object.
      *
      * @return the ActionNames value
      */
     public Set<String> getActionNames() {
         return commands;
     }
 
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         HashTree subTree = null;
         if (!commands.contains(e.getActionCommand())) {
             throw new IllegalUserActionException("Invalid user command:" + e.getActionCommand());
         }
         if (e.getActionCommand().equals(ActionNames.SAVE_AS)) {
             JMeterTreeNode[] nodes = GuiPackage.getInstance().getTreeListener().getSelectedNodes();
             if (nodes.length > 1){
                 JMeterUtils.reportErrorToUser(
                         JMeterUtils.getResString("save_as_error"), // $NON-NLS-1$
                         JMeterUtils.getResString("save_as")); // $NON-NLS-1$
                 return;
             }
             subTree = GuiPackage.getInstance().getCurrentSubTree();
         } else {
             subTree = GuiPackage.getInstance().getTreeModel().getTestPlan();
         }
 
         String updateFile = GuiPackage.getInstance().getTestPlanFile();
         if (!ActionNames.SAVE.equals(e.getActionCommand()) || updateFile == null) {
             JFileChooser chooser = FileDialoger.promptToSaveFile(updateFile == null ? GuiPackage.getInstance().getTreeListener()
                     .getCurrentNode().getName()
                     + JMX_FILE_EXTENSION : updateFile);
             if (chooser == null) {
                 return;
             }
             updateFile = chooser.getSelectedFile().getAbsolutePath();
             // Make sure the file ends with proper extension
             if(FilenameUtils.getExtension(updateFile).equals("")) {
                 updateFile = updateFile + JMX_FILE_EXTENSION;
             }
             // Check if the user is trying to save to an existing file
             File f = new File(updateFile);
             if(f.exists()) {
                 int response = JOptionPane.showConfirmDialog(GuiPackage.getInstance().getMainFrame(),
                         JMeterUtils.getResString("save_overwrite_existing_file"), // $NON-NLS-1$
                         JMeterUtils.getResString("save?"),  // $NON-NLS-1$
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.QUESTION_MESSAGE);
                 if (response == JOptionPane.CLOSED_OPTION || response == JOptionPane.NO_OPTION) {
                     return ; // Do not save, user does not want to overwrite
                 }
             }
 
             if (!e.getActionCommand().equals(ActionNames.SAVE_AS)) {
                 GuiPackage.getInstance().setTestPlanFile(updateFile);
             }
         }
         // TODO: doesn't putting this here mark the tree as
         // saved even though a failure may occur later?
 
         ActionRouter.getInstance().doActionNow(new ActionEvent(subTree, e.getID(), ActionNames.SUB_TREE_SAVED));
         try {
             convertSubTree(subTree);
         } catch (Exception err) {
         }
         FileOutputStream ostream = null;
         try {
+            File outFile = new File(updateFile);
+            if(!outFile.canWrite()) {
+                throw new IllegalUserActionException("File cannot be written: " + outFile.getAbsolutePath());
+            }
             ostream = new FileOutputStream(updateFile);
             SaveService.saveTree(subTree, ostream);
         } catch (Throwable ex) {
+            GuiPackage.getInstance().setDirty(true);
             GuiPackage.getInstance().setTestPlanFile(null);
             log.error("", ex);
             if (ex instanceof Error){
                 throw (Error) ex;
             }
             if (ex instanceof RuntimeException){
                 throw (RuntimeException) ex;
             }
             throw new IllegalUserActionException("Couldn't save test plan to file: " + updateFile);
         } finally {
             JOrphanUtils.closeQuietly(ostream);
         }
         GuiPackage.getInstance().updateCurrentGui();
     }
 
     // package protected to allow access from test code
     void convertSubTree(HashTree tree) {
         Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
         while (iter.hasNext()) {
             JMeterTreeNode item = (JMeterTreeNode) iter.next();
             convertSubTree(tree.getTree(item));
             TestElement testElement = item.getTestElement(); // requires JMeterTreeNode
             tree.replace(item, testElement);
         }
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index aaaac6c05..1b6d6af4b 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,222 +1,223 @@
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
 
 
 <!--  =================== 2.7 =================== -->
 
 <h1>Version 2.7</h1>
 
 <h2>New and Noteworthy</h2>
 
 <p>
 Plugin writers : New interface org.apache.jmeter.engine.util.ConfigMergabilityIndicator has been introduced to tell wether a ConfigTestElement can be merged in Sampler(see Bug 53042):<br/>
 public boolean applies(ConfigTestElement configElement);
 </p>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode (see Bug 50898). 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 The workaround is to use different names for IncludeControllers
 </p>
 
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
 When doing replacement of User Defined Variables, Proxy will not substitute partial values anymore when "Regexp matching" is used. It will use Perl 5 word matching ("\b")
 </p>
 
 <p>
 In User Defined Variables, Test Plan, HTTP Sampler Arguments Table, Java Request Defaults, JMS Sampler and Publisher, LDAP Request Defaults and LDAP Extended Request Defaults, rows with
 empty Name and Value are no more saved.
 </p>
 
 <p>
 JMeter now expands the Test Plan tree to the testplan level and no further and selects the root of the tree. Furthermore default value of onload.expandtree is false.
 </p>
 
 <p>
 Graph Full Results Listener has been removed.
 </p>
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 52613 - Using Raw Post Body option, text gets encoded</li>
 <li>Bug 52781 - Content-Disposition header garbled even if browser compatible headers is checked (HC4) </li>
 <li>Bug 52796 - MonitorHandler fails to clear variables when starting a new parse</li>
 <li>Bug 52871 - Multiple Certificates not working with HTTP Client 4</li>
 <li>Bug 52885 - Proxy : Recording issues with HTTPS, cookies starting with secure are partly truncated</li>
 <li>Bug 52886 - Proxy : Recording issues with HTTPS when spoofing is on, secure cookies are not always changed</li>
 <li>Bug 52897 - HTTPSampler : Using PUT method with HTTPClient4 and empty Content Encoding and sending files leads to NullPointerException</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 51737 - TCPSampler : Packet gets converted/corrupted</li>
 <li>Bug 52868 - BSF language list should be sorted</li>
 <li>Bug 52869 - JSR223 language list currently uses BSF list which is wrong</li>
 <li>Bug 52932 - JDBC Sampler : Sampler is not marked in error in an Exception which is not of class IOException, SQLException, IOException occurs</li>
 <li>Bug 52916 - JDBC Exception if there is an empty user defined variable</li>
 <li>Bug 52937 - Webservice Sampler : Clear Soap Documents Cache at end of Test </li>
 <li>Bug 53027 - Jmeter starts throwing exceptions while using SMTP Sample in a test plan with HTTP Cookie Mngr or HTTP Request Defaults</li>
 <li>Bug 53072 - JDBC PREPARED SELECT statements should return results in variables like non prepared SELECT</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 52968 - Option Start Next Loop in Thread Group does not mark parent Transaction Sampler in error when an error occurs</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 43450 - Listeners/Savers assume SampleResult count is always 1; fixed Generate Summary Results</li>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 <li>Bug 52848 - NullPointer in "XPath Assertion"</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>Bug 52551 - Function Helper Dialog does not switch language correctly</li>
 <li>Bug 52552 - Help reference only works in English</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 52639 - JSplitPane divider for log panel should be hidden if log is not activated</li>
 <li>Bug 52672 - Change Controller action deletes all but one child samplers</li>
 <li>Bug 52694 - Deadlock in GUI related to non AWT Threads updating GUI</li>
 <li>Bug 52678 - Proxy : When doing replacement of UserDefinedVariables, partial values should not be substituted</li>
 <li>Bug 52728 - CSV Data Set Config element cannot coexist with BSF Sampler in same Thread Plan</li>
 <li>Bug 52762 - Problem with multiples certificates: first index not used until indexes are restarted</li>
 <li>Bug 52741 - TestBeanGUI default values do not work at second time or later</li>
 <li>Bug 52783 - oro.patterncache.size property never used due to early init</li>
 <li>Bug 52789 - Proxy with Regexp Matching can fail with NullPointerException in Value Replacement if value is null</li>
 <li>Bug 52645 - Recording with Proxy leads to OutOfMemory</li>
 <li>Bug 52679 - User Parameters columns narrow</li>
 <li>Bug 52843 - Sample headerSize and bodySize not being accumulated for subsamples</li>
 <li>Bug 52967 - The function __P() couldn't use default value when running with remote server in GUI mode.</li>
 <li>Bug 50799 - Having a non-HTTP sampler in a http test plan prevents multiple header managers from working</li>
+<li>Bug 52997 - Jmeter should not exit without saving Test Plan if saving before exit fails</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Bug 52775 - JMS Publisher : Add Non Persistent Delivery option</li>
 <li>Bug 52810 - Enable setting JMS Properties through JMS Publisher sampler</li>
 <li>Bug 52938 - Webservice Sampler : Add a jmeter property soap.document_cache to control size of Document Cache</li>
 <li>Bug 52939 - Webservice Sampler : Make MaintainSession configurable</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52603 - MailerVisualizer : Enable SSL , TLS and Authentication</li>
 <li>Bug 52698 - Remove Graph Full Results Listener</li>
 <li>Bug 53070 - Change Aggregate graph to Clustered Bar chart, add more columns (median, 90% line, min, max) and options, fixed some bugs</li>
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
 <li>Bug 45839 - Test Action : Allow premature exit from a loop</li>
 <li>Bug 52614 - MailerModel.sendMail has strange way to calculate debug setting</li>
 <li>Bug 52782 - Add a detail button on parameters table to show detail of a Row</li>
 <li>Bug 52674 - Proxy : Add a Sampler Creator to allow plugging HTTP based samplers using potentially non textual POST Body (AMF, Silverlight...) and customizing them for others</li>
 <li>Bug 52934 - GUI : Open Test plan with the tree expanded to the testplan level and no further and select the root of the tree</li>
 <li>Bug 52941 - Improvements of HTML report design generated by JMeter Ant task extra</li>
 <li>Bug 53042 - Introduce a new method in Sampler interface to allow Sampler to decide wether a config element applies to Sampler</li>
 <li>Bug 52771 - Documentation : Added RSS feed on JMeter Home page under link "Subscribe to What's New"</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Upgraded to rhino 1.7R3 (was js-1.7R2.jar). 
 Note: the Maven coordinates for the jar were changed from rhino:js to org.mozilla:rhino.
 This does not affect JMeter directly, but might cause problems if using JMeter in a Maven project
 with other code that depends on an earlier version of the Rhino Javascript jar.
 </li>
 <li>Bug 52675 - Refactor Proxy and HttpRequestHdr to allow Sampler Creation by Proxy</li>
 <li>Bug 52680 - Mention version in which function was introduced</li>
 <li>Bug 52788 - HttpRequestHdr : Optimize code to avoid useless work</li>
 <li>JMeter Ant (ant-jmeter-1.1.1.jar) task was upgraded from 1.0.9 to 1.1.1</li>
 <li>Updated to commons-io 2.2 (from 2.1)</li>
 <li>Bug 53129 - Upgrade XStream from 1.3.1 to 1.4.2</li>
 </ul>
 
 </section> 
 </body> 
 </document>
