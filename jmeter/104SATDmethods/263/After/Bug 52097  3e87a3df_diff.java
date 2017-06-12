diff --git a/src/core/org/apache/jmeter/gui/action/Save.java b/src/core/org/apache/jmeter/gui/action/Save.java
index 86ac7d7fe..7fe3c95c1 100644
--- a/src/core/org/apache/jmeter/gui/action/Save.java
+++ b/src/core/org/apache/jmeter/gui/action/Save.java
@@ -1,165 +1,165 @@
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
-            JFileChooser chooser = FileDialoger.promptToSaveFile(GuiPackage.getInstance().getTreeListener()
+            JFileChooser chooser = FileDialoger.promptToSaveFile(updateFile == null ? GuiPackage.getInstance().getTreeListener()
                     .getCurrentNode().getName()
-                    + JMX_FILE_EXTENSION);
+                    + JMX_FILE_EXTENSION : updateFile);
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
             ostream = new FileOutputStream(updateFile);
             SaveService.saveTree(subTree, ostream);
         } catch (Throwable ex) {
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
diff --git a/src/core/org/apache/jmeter/gui/util/FileDialoger.java b/src/core/org/apache/jmeter/gui/util/FileDialoger.java
index d9b0699ef..cf7353bb8 100644
--- a/src/core/org/apache/jmeter/gui/util/FileDialoger.java
+++ b/src/core/org/apache/jmeter/gui/util/FileDialoger.java
@@ -1,147 +1,149 @@
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
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.JMeterFileFilter;
 
 /**
  * Class implementing a file open dialogue
  */
 public final class FileDialoger {
     /**
      * The last directory visited by the user while choosing Files.
      */
     private static String lastJFCDirectory = null;
 
     private static JFileChooser jfc = new JFileChooser();
 
     /**
      * Prevent instantiation of utility class.
      */
     private FileDialoger() {
     }
 
     /**
      * Prompts the user to choose a file from their filesystems for our own
      * devious uses. This method maintains the last directory the user visited
      * before dismissing the dialog. This does NOT imply they actually chose a
      * file from that directory, only that they closed the dialog there. It is
      * the caller's responsibility to check to see if the selected file is
      * non-null.
      *
      * @return the JFileChooser that interacted with the user, after they are
      *         finished using it - null if no file was chosen
      */
     public static JFileChooser promptToOpenFile(String[] exts) {
         // JFileChooser jfc = null;
 
         if (lastJFCDirectory == null) {
             String start = System.getProperty("user.dir", ""); //$NON-NLS-1$//$NON-NLS-2$
 
             if (start.length() > 0) {
                 jfc.setCurrentDirectory(new File(start));
             }
         }
         clearFileFilters();
         if(exts != null && exts.length > 0) {
             JMeterFileFilter currentFilter = new JMeterFileFilter(exts);
             jfc.addChoosableFileFilter(currentFilter);
             jfc.setAcceptAllFileFilterUsed(true);
             jfc.setFileFilter(currentFilter);
         }
         int retVal = jfc.showOpenDialog(GuiPackage.getInstance().getMainFrame());
         lastJFCDirectory = jfc.getCurrentDirectory().getAbsolutePath();
 
         if (retVal == JFileChooser.APPROVE_OPTION) {
             return jfc;
         }
         return null;
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
      * @return the JFileChooser that interacted with the user, after they are
      *         finished using it - null if no file was chosen
      * @see #promptToOpenFile()
      */
     public static JFileChooser promptToSaveFile(String filename) {
         return promptToSaveFile(filename, null);
     }
 
     /**
      * Get a JFileChooser with a new FileFilter.
      *
      * @param filename file name
      * @param extensions list of extensions
      * @return the FileChooser - null if no file was chosen
      */
     public static JFileChooser promptToSaveFile(String filename, String[] extensions) {
         if (lastJFCDirectory == null) {
             String start = System.getProperty("user.dir", "");//$NON-NLS-1$//$NON-NLS-2$
             if (start.length() > 0) {
                 jfc = new JFileChooser(new File(start));
             }
             lastJFCDirectory = jfc.getCurrentDirectory().getAbsolutePath();
         }
         String ext = ".jmx";//$NON-NLS-1$
         if (filename != null) {
-            jfc.setSelectedFile(new File(lastJFCDirectory, filename));
+            jfc.setSelectedFile(filename.lastIndexOf(System.getProperty("file.separator")) > 0 ?
+                    new File(filename) :
+                    new File(lastJFCDirectory, filename));
             int i = -1;
             if ((i = filename.lastIndexOf(".")) > -1) {//$NON-NLS-1$
                 ext = filename.substring(i);
             }
         }
         clearFileFilters();
         if (extensions != null) {
             jfc.addChoosableFileFilter(new JMeterFileFilter(extensions));
         } else {
             jfc.addChoosableFileFilter(new JMeterFileFilter(new String[] { ext }));
         }
 
         int retVal = jfc.showSaveDialog(GuiPackage.getInstance().getMainFrame());
         lastJFCDirectory = jfc.getCurrentDirectory().getAbsolutePath();
         if (retVal == JFileChooser.APPROVE_OPTION) {
             return jfc;
         }
         return null;
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 065061c3f..c84aad150 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,203 +1,204 @@
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
 
 
 <!--  =================== 2.5.2 =================== -->
 
 <h1>Version 2.5.2</h1>
 
 <h2>Summary of main changes</h2>
 
 <ul>
 </ul>
 
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>The If Controller may cause an infinite loop if the condition is always false from the first iteration. 
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <p>Start next Loop option in Thread Group is broken, see Bugs (51868, 51866, 51865).</p>
 
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
 <li>Bug 51937 - JMeter does not handle missing TestPlan entry well</li>
 <li>Bug 51988 - CSV Data Set Configuration does not resolve default delimiter for header parsing when variables field is empty</li>
 <li>Bug 52003 - View Results Tree "Scroll automatically" does not scroll properly in case nodes are expanded</li>
 <li>Bug 27112 - User Parameters should use scrollbars</li>
 <li>Bug 52029 - Command-line shutdown only gets sent to last engine that was started</li>
 <li>Bug 52093 - Toolbar ToolTips don't switch language</li>
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
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52022 - In View Results Tree rather than showing just a message if the results are to big, show as much of the result as are configured</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 52006 - Create a function RandomString to generate random Strings</li>
 <li>Bug 52016 - It would be useful to support Jexl2</li>
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
+<li>Bug 52097 - Save As should point to same folder that was used to open a file if MRU list is used</li>
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
