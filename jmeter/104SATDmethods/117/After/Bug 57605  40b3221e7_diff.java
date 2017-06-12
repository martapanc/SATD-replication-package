diff --git a/src/components/org/apache/jmeter/control/IncludeController.java b/src/components/org/apache/jmeter/control/IncludeController.java
index 1cb1f134a..437f6713a 100644
--- a/src/components/org/apache/jmeter/control/IncludeController.java
+++ b/src/components/org/apache/jmeter/control/IncludeController.java
@@ -1,218 +1,210 @@
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
 
 import java.io.File;
-import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
-import java.io.InputStream;
 import java.util.Iterator;
 import java.util.LinkedList;
 
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
-import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public class IncludeController extends GenericController implements ReplaceableController {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String INCLUDE_PATH = "IncludeController.includepath"; //$NON-NLS-1$
 
     private static  final String prefix =
         JMeterUtils.getPropDefault(
                 "includecontroller.prefix", //$NON-NLS-1$
                 ""); //$NON-NLS-1$
 
     private HashTree subtree = null;
     private TestElement sub = null;
 
     /**
      * No-arg constructor
      *
      * @see java.lang.Object#Object()
      */
     public IncludeController() {
         super();
     }
 
     @Override
     public Object clone() {
         // TODO - fix so that this is only called once per test, instead of at every clone
         // Perhaps save previous filename, and only load if it has changed?
         this.resolveReplacementSubTree(null);
         IncludeController clone = (IncludeController) super.clone();
         clone.setIncludePath(this.getIncludePath());
         if (this.subtree != null) {
             if (this.subtree.size() == 1) {
                 Iterator<Object> itr = this.subtree.keySet().iterator();
                 while (itr.hasNext()) {
                     this.sub = (TestElement) itr.next();
                 }
             }
             clone.subtree = (HashTree)this.subtree.clone();
             clone.sub = this.sub==null ? null : (TestElement) this.sub.clone();
         }
         return clone;
     }
 
     /**
      * In the event an user wants to include an external JMX test plan
      * the GUI would call this.
      * @param jmxfile The path to the JMX test plan to include
      */
     public void setIncludePath(String jmxfile) {
         this.setProperty(INCLUDE_PATH,jmxfile);
     }
 
     /**
      * return the JMX file path.
      * @return the JMX file path
      */
     public String getIncludePath() {
         return this.getPropertyAsString(INCLUDE_PATH);
     }
 
     /**
      * The way ReplaceableController works is clone is called first,
      * followed by replace(HashTree) and finally getReplacement().
      */
     @Override
     public HashTree getReplacementSubTree() {
         return subtree;
     }
 
     public TestElement getReplacementElement() {
         return sub;
     }
 
     @Override
     public void resolveReplacementSubTree(JMeterTreeNode context) {
         this.subtree = this.loadIncludedElements();
     }
 
     /**
      * load the included elements using SaveService
      *
      * @return tree with loaded elements
      */
     protected HashTree loadIncludedElements() {
         // only try to load the JMX test plan if there is one
         final String includePath = getIncludePath();
-        InputStream reader = null;
         HashTree tree = null;
         if (includePath != null && includePath.length() > 0) {
             try {
                 String fileName=prefix+includePath;
                 File file = new File(fileName);
                 final String absolutePath = file.getAbsolutePath();
                 log.info("loadIncludedElements -- try to load included module: "+absolutePath);
                 if(!file.exists() && !file.isAbsolute()){
                     log.info("loadIncludedElements -failed for: "+absolutePath);
                     file = new File(FileServer.getFileServer().getBaseDir(), includePath);
                     log.info("loadIncludedElements -Attempting to read it from: "+absolutePath);
                     if(!file.exists()){
                         log.error("loadIncludedElements -failed for: "+absolutePath);
                         throw new IOException("loadIncludedElements -failed for: "+absolutePath);
                     }
                 }
                 
-                reader = new FileInputStream(file);
-                tree = SaveService.loadTree(reader);
+                tree = SaveService.loadTree(file);
                 // filter the tree for a TestFragment.
                 tree = getProperBranch(tree);
                 removeDisabledItems(tree);
                 return tree;
             } catch (NoClassDefFoundError ex) // Allow for missing optional jars
             {
                 String msg = ex.getMessage();
                 if (msg == null) {
                     msg = "Missing jar file - see log for details";
                 }
                 log.warn("Missing jar file", ex);
                 JMeterUtils.reportErrorToUser(msg);
             } catch (FileNotFoundException ex) {
                 String msg = ex.getMessage();
                 JMeterUtils.reportErrorToUser(msg);
                 log.warn(msg);
             } catch (Exception ex) {
                 String msg = ex.getMessage();
                 if (msg == null) {
                     msg = "Unexpected error - see log for details";
                 }
                 JMeterUtils.reportErrorToUser(msg);
                 log.warn("Unexpected error", ex);
             }
-            finally{
-                JOrphanUtils.closeQuietly(reader);
-            }
         }
         return tree;
     }
 
     /**
      * Extract from tree (included test plan) all Test Elements located in a Test Fragment
      * @param tree HashTree included Test Plan
      * @return HashTree Subset within Test Fragment or Empty HashTree
      */
     private HashTree getProperBranch(HashTree tree) {
         Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
         while (iter.hasNext()) {
             TestElement item = (TestElement) iter.next();
 
             //if we found a TestPlan, then we are on our way to the TestFragment
             if (item instanceof TestPlan)
             {
                 return getProperBranch(tree.getTree(item));
             }
 
             if (item instanceof TestFragmentController)
             {
                 return tree.getTree(item);
             }
         }
         log.warn("No Test Fragment was found in included Test Plan, returning empty HashTree");
         return new HashTree();
     }
 
 
     private void removeDisabledItems(HashTree tree) {
         Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
         while (iter.hasNext()) {
             TestElement item = (TestElement) iter.next();
             if (!item.isEnabled()) {
                 //log.info("Removing "+item.toString());
                 tree.remove(item);
             } else {
                 //log.info("Keeping "+item.toString());
                 removeDisabledItems(tree.getTree(item));// Recursive call
             }
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/JMeter.java b/src/core/org/apache/jmeter/JMeter.java
index 07b3099d3..e2b56f06f 100644
--- a/src/core/org/apache/jmeter/JMeter.java
+++ b/src/core/org/apache/jmeter/JMeter.java
@@ -1,1167 +1,1157 @@
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
 import java.lang.Thread.UncaughtExceptionHandler;
 import java.net.Authenticator;
 import java.net.DatagramPacket;
 import java.net.DatagramSocket;
 import java.net.InetAddress;
 import java.net.MalformedURLException;
 import java.net.SocketException;
 import java.text.SimpleDateFormat;
 import java.util.Collection;
 import java.util.Date;
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import java.util.Properties;
 import java.util.StringTokenizer;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import javax.swing.JTree;
 import javax.swing.UIManager;
 import javax.swing.tree.TreePath;
 
 import org.apache.commons.cli.avalon.CLArgsParser;
 import org.apache.commons.cli.avalon.CLOption;
 import org.apache.commons.cli.avalon.CLOptionDescriptor;
 import org.apache.commons.cli.avalon.CLUtil;
 import org.apache.jmeter.control.ReplaceableController;
 import org.apache.jmeter.engine.ClientJMeterEngine;
+import org.apache.jmeter.engine.DistributedRunner;
 import org.apache.jmeter.engine.JMeterEngine;
 import org.apache.jmeter.engine.RemoteJMeterEngineImpl;
 import org.apache.jmeter.engine.StandardJMeterEngine;
-import org.apache.jmeter.engine.DistributedRunner;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.MainFrame;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.Load;
 import org.apache.jmeter.gui.action.LoadRecentProject;
 import org.apache.jmeter.gui.action.LookAndFeelCommand;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FocusRequester;
 import org.apache.jmeter.plugin.JMeterPlugin;
 import org.apache.jmeter.plugin.PluginManager;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.reporters.Summariser;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.RemoteThreadsListenerTestElement;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellServer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.SearchByClass;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassTools;
 import org.apache.jorphan.util.HeapDumper;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * Main JMeter class; processes options and starts the GUI, non-GUI or server as appropriate.
  */
 public class JMeter implements JMeterPlugin {
     private static final Logger log = LoggingManager.getLoggerForClass();
     
     public static final int UDP_PORT_DEFAULT = 4445; // needed for ShutdownClient
 
     public static final String HTTP_PROXY_PASS = "http.proxyPass"; // $NON-NLS-1$
 
     public static final String HTTP_PROXY_USER = "http.proxyUser"; // $NON-NLS-1$
 
     public static final String JMETER_NON_GUI = "JMeter.NonGui"; // $NON-NLS-1$
 
     // If the -t flag is to "LAST", then the last loaded file (if any) is used
     private static final String USE_LAST_JMX = "LAST";
     // If the -j  or -l flag is set to LAST or LAST.log|LAST.jtl, then the last loaded file name is used to
     // generate the log file name by removing .JMX and replacing it with .log|.jtl
 
     private static final int PROXY_PASSWORD     = 'a';// $NON-NLS-1$
     private static final int JMETER_HOME_OPT    = 'd';// $NON-NLS-1$
     private static final int HELP_OPT           = 'h';// $NON-NLS-1$
     // jmeter.log
     private static final int JMLOGFILE_OPT      = 'j';// $NON-NLS-1$
     // sample result log file
     private static final int LOGFILE_OPT        = 'l';// $NON-NLS-1$
     private static final int NONGUI_OPT         = 'n';// $NON-NLS-1$
     private static final int PROPFILE_OPT       = 'p';// $NON-NLS-1$
     private static final int PROPFILE2_OPT      = 'q';// $NON-NLS-1$
     private static final int REMOTE_OPT         = 'r';// $NON-NLS-1$
     private static final int SERVER_OPT         = 's';// $NON-NLS-1$
     private static final int TESTFILE_OPT       = 't';// $NON-NLS-1$
     private static final int PROXY_USERNAME     = 'u';// $NON-NLS-1$
     private static final int VERSION_OPT        = 'v';// $NON-NLS-1$
 
     private static final int SYSTEM_PROPERTY    = 'D';// $NON-NLS-1$
     private static final int JMETER_GLOBAL_PROP = 'G';// $NON-NLS-1$
     private static final int PROXY_HOST         = 'H';// $NON-NLS-1$
     private static final int JMETER_PROPERTY    = 'J';// $NON-NLS-1$
     private static final int LOGLEVEL           = 'L';// $NON-NLS-1$
     private static final int NONPROXY_HOSTS     = 'N';// $NON-NLS-1$
     private static final int PROXY_PORT         = 'P';// $NON-NLS-1$
     private static final int REMOTE_OPT_PARAM   = 'R';// $NON-NLS-1$
     private static final int SYSTEM_PROPFILE    = 'S';// $NON-NLS-1$
     private static final int REMOTE_STOP        = 'X';// $NON-NLS-1$
 
 
 
     /**
      * Define the understood options. Each CLOptionDescriptor contains:
      * <ul>
      * <li>The "long" version of the option. Eg, "help" means that "--help"
      * will be recognised.</li>
      * <li>The option flags, governing the option's argument(s).</li>
      * <li>The "short" version of the option. Eg, 'h' means that "-h" will be
      * recognised.</li>
      * <li>A description of the option.</li>
      * </ul>
      */
     private static final CLOptionDescriptor[] options = new CLOptionDescriptor[] {
             new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED, HELP_OPT,
                     "print usage information and exit"),
             new CLOptionDescriptor("version", CLOptionDescriptor.ARGUMENT_DISALLOWED, VERSION_OPT,
                     "print the version information and exit"),
             new CLOptionDescriptor("propfile", CLOptionDescriptor.ARGUMENT_REQUIRED, PROPFILE_OPT,
                     "the jmeter property file to use"),
             new CLOptionDescriptor("addprop", CLOptionDescriptor.ARGUMENT_REQUIRED
                     | CLOptionDescriptor.DUPLICATES_ALLOWED, PROPFILE2_OPT,
                     "additional JMeter property file(s)"),
             new CLOptionDescriptor("testfile", CLOptionDescriptor.ARGUMENT_REQUIRED, TESTFILE_OPT,
                     "the jmeter test(.jmx) file to run"),
             new CLOptionDescriptor("logfile", CLOptionDescriptor.ARGUMENT_REQUIRED, LOGFILE_OPT,
                     "the file to log samples to"),
             new CLOptionDescriptor("jmeterlogfile", CLOptionDescriptor.ARGUMENT_REQUIRED, JMLOGFILE_OPT,
                     "jmeter run log file (jmeter.log)"),
             new CLOptionDescriptor("nongui", CLOptionDescriptor.ARGUMENT_DISALLOWED, NONGUI_OPT,
                     "run JMeter in nongui mode"),
             new CLOptionDescriptor("server", CLOptionDescriptor.ARGUMENT_DISALLOWED, SERVER_OPT,
                     "run the JMeter server"),
             new CLOptionDescriptor("proxyHost", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_HOST,
                     "Set a proxy server for JMeter to use"),
             new CLOptionDescriptor("proxyPort", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_PORT,
                     "Set proxy server port for JMeter to use"),
             new CLOptionDescriptor("nonProxyHosts", CLOptionDescriptor.ARGUMENT_REQUIRED, NONPROXY_HOSTS,
                     "Set nonproxy host list (e.g. *.apache.org|localhost)"),
             new CLOptionDescriptor("username", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_USERNAME,
                     "Set username for proxy server that JMeter is to use"),
             new CLOptionDescriptor("password", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_PASSWORD,
                     "Set password for proxy server that JMeter is to use"),
             new CLOptionDescriptor("jmeterproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_PROPERTY,
                     "Define additional JMeter properties"),
             new CLOptionDescriptor("globalproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_GLOBAL_PROP,
                     "Define Global properties (sent to servers)\n\t\te.g. -Gport=123 or -Gglobal.properties"),
             new CLOptionDescriptor("systemproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, SYSTEM_PROPERTY,
                     "Define additional system properties"),
             new CLOptionDescriptor("systemPropertyFile", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENT_REQUIRED, SYSTEM_PROPFILE,
                     "additional system property file(s)"),
             new CLOptionDescriptor("loglevel", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, LOGLEVEL,
                     "[category=]level e.g. jorphan=INFO or jmeter.util=DEBUG"),
             new CLOptionDescriptor("runremote", CLOptionDescriptor.ARGUMENT_DISALLOWED, REMOTE_OPT,
                     "Start remote servers (as defined in remote_hosts)"),
             new CLOptionDescriptor("remotestart", CLOptionDescriptor.ARGUMENT_REQUIRED, REMOTE_OPT_PARAM,
                     "Start these remote servers (overrides remote_hosts)"),
             new CLOptionDescriptor("homedir", CLOptionDescriptor.ARGUMENT_REQUIRED, JMETER_HOME_OPT,
                     "the jmeter home directory to use"),
             new CLOptionDescriptor("remoteexit", CLOptionDescriptor.ARGUMENT_DISALLOWED, REMOTE_STOP,
             "Exit the remote servers at end of test (non-GUI)"),
                     };
 
     public JMeter() {
     }
 
     // Hack to allow automated tests to find when test has ended
     //transient boolean testEnded = false;
 
     private JMeter parent;
 
     private Properties remoteProps; // Properties to be sent to remote servers
 
     private boolean remoteStop; // should remote engines be stopped at end of non-GUI test?
 
     /**
      * Starts up JMeter in GUI mode
      */
     private void startGui(String testFile) {
         String jMeterLaf = LookAndFeelCommand.getJMeterLaf();
         try {
             UIManager.setLookAndFeel(jMeterLaf);
         } catch (Exception ex) {
             log.warn("Could not set LAF to:"+jMeterLaf, ex);
         }
 
         PluginManager.install(this, true);
 
         JMeterTreeModel treeModel = new JMeterTreeModel();
         JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
         treeLis.setActionHandler(ActionRouter.getInstance());
         // NOTUSED: GuiPackage guiPack =
         GuiPackage.getInstance(treeLis, treeModel);
         MainFrame main = new MainFrame(treeModel, treeLis);
         ComponentUtil.centerComponentInWindow(main, 80);
         main.setVisible(true);
         ActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, ActionNames.ADD_ALL));
         if (testFile != null) {
-            FileInputStream reader = null;
             try {
                 File f = new File(testFile);
                 log.info("Loading file: " + f);
                 FileServer.getFileServer().setBaseForScript(f);
 
-                reader = new FileInputStream(f);
-                HashTree tree = SaveService.loadTree(reader);
+                HashTree tree = SaveService.loadTree(f);
 
                 GuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());
 
                 Load.insertLoadedTree(1, tree);
             } catch (ConversionException e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(SaveService.CEtoString(e));
             } catch (Exception e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(e.toString());
-            } finally {
-                JOrphanUtils.closeQuietly(reader);
             }
         } else {
             JTree jTree = GuiPackage.getInstance().getMainFrame().getTree();
             TreePath path = jTree.getPathForRow(0);
             jTree.setSelectionPath(path);
             FocusRequester.requestFocus(jTree);
         }
     }
 
     /**
      * Takes the command line arguments and uses them to determine how to
      * startup JMeter.
      * 
      * Called reflectively by {@link NewDriver#main(String[])}
      * @param args The arguments for JMeter
      */
     public void start(String[] args) {
 
         CLArgsParser parser = new CLArgsParser(args, options);
         String error = parser.getErrorString();
         if (error == null){// Check option combinations
             boolean gui = parser.getArgumentById(NONGUI_OPT)==null;
             boolean nonGuiOnly = parser.getArgumentById(REMOTE_OPT)!=null
                                || parser.getArgumentById(REMOTE_OPT_PARAM)!=null
                                || parser.getArgumentById(REMOTE_STOP)!=null;
             if (gui && nonGuiOnly) {
                 error = "-r and -R and -X are only valid in non-GUI mode";
             }
         }
         if (null != error) {
             System.err.println("Error: " + error);
             System.out.println("Usage");
             System.out.println(CLUtil.describeOptions(options).toString());
             return;
         }
         try {
             initializeProperties(parser); // Also initialises JMeter logging
 
             /*
              * The following is needed for HTTPClient.
              * (originally tried doing this in HTTPSampler2,
              * but it appears that it was done too late when running in GUI mode)
              * Set the commons logging default to Avalon Logkit, if not already defined
              */
             if (System.getProperty("org.apache.commons.logging.Log") == null) { // $NON-NLS-1$
                 System.setProperty("org.apache.commons.logging.Log" // $NON-NLS-1$
                         , "org.apache.commons.logging.impl.LogKitLogger"); // $NON-NLS-1$
             }
 
             Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {                
                 @Override
                 public void uncaughtException(Thread t, Throwable e) {
                     if (!(e instanceof ThreadDeath)) {
                         log.error("Uncaught exception: ", e);
                         System.err.println("Uncaught Exception " + e + ". See log file for details.");
                     }
                 }
             });
 
             log.info(JMeterUtils.getJMeterCopyright());
             log.info("Version " + JMeterUtils.getJMeterVersion());
             logProperty("java.version"); //$NON-NLS-1$
             logProperty("java.vm.name"); //$NON-NLS-1$
             logProperty("os.name"); //$NON-NLS-1$
             logProperty("os.arch"); //$NON-NLS-1$
             logProperty("os.version"); //$NON-NLS-1$
             logProperty("file.encoding"); // $NON-NLS-1$
             log.info("Default Locale=" + Locale.getDefault().getDisplayName());
             log.info("JMeter  Locale=" + JMeterUtils.getLocale().getDisplayName());
             log.info("JMeterHome="     + JMeterUtils.getJMeterHome());
             logProperty("user.dir","  ="); //$NON-NLS-1$
             log.info("PWD       ="+new File(".").getCanonicalPath());//$NON-NLS-1$
             log.info("IP: "+JMeterUtils.getLocalHostIP()
                     +" Name: "+JMeterUtils.getLocalHostName()
                     +" FullName: "+JMeterUtils.getLocalHostFullName());
             setProxy(parser);
 
             updateClassLoader();
             if (log.isDebugEnabled())
             {
                 String jcp=System.getProperty("java.class.path");// $NON-NLS-1$
                 String bits[] =jcp.split(File.pathSeparator);
                 log.debug("ClassPath");
                 for(String bit : bits){
                     log.debug(bit);
                 }
                 log.debug(jcp);
             }
 
             // Set some (hopefully!) useful properties
             long now=System.currentTimeMillis();
             JMeterUtils.setProperty("START.MS",Long.toString(now));// $NON-NLS-1$
             Date today=new Date(now); // so it agrees with above
             // TODO perhaps should share code with __time() function for this...
             JMeterUtils.setProperty("START.YMD",new SimpleDateFormat("yyyyMMdd").format(today));// $NON-NLS-1$ $NON-NLS-2$
             JMeterUtils.setProperty("START.HMS",new SimpleDateFormat("HHmmss").format(today));// $NON-NLS-1$ $NON-NLS-2$
 
             if (parser.getArgumentById(VERSION_OPT) != null) {
                 System.out.println(JMeterUtils.getJMeterCopyright());
                 System.out.println("Version " + JMeterUtils.getJMeterVersion());
             } else if (parser.getArgumentById(HELP_OPT) != null) {
                 System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));// $NON-NLS-1$
             } else if (parser.getArgumentById(SERVER_OPT) != null) {
                 // Start the server
                 try {
                     RemoteJMeterEngineImpl.startServer(JMeterUtils.getPropDefault("server_port", 0)); // $NON-NLS-1$
                 } catch (Exception ex) {
                     System.err.println("Server failed to start: "+ex);
                     log.error("Giving up, as server failed with:", ex);
                     throw ex;
                 }
                 startOptionalServers();
             } else {
                 String testFile=null;
                 CLOption testFileOpt = parser.getArgumentById(TESTFILE_OPT);
                 if (testFileOpt != null){
                     testFile = testFileOpt.getArgument();
                     if (USE_LAST_JMX.equals(testFile)) {
                         testFile = LoadRecentProject.getRecentFile(0);// most recent
                     }
                 }
                 if (parser.getArgumentById(NONGUI_OPT) == null) {
                     startGui(testFile);
                     startOptionalServers();
                 } else {
                     CLOption rem=parser.getArgumentById(REMOTE_OPT_PARAM);
                     if (rem==null) { rem=parser.getArgumentById(REMOTE_OPT); }
                     CLOption jtl = parser.getArgumentById(LOGFILE_OPT);
                     String jtlFile = null;
                     if (jtl != null){
                         jtlFile=processLAST(jtl.getArgument(), ".jtl"); // $NON-NLS-1$
                     }
                     startNonGui(testFile, jtlFile, rem);
                     startOptionalServers();
                 }
             }
         } catch (IllegalUserActionException e) {
             System.out.println(e.getMessage());
             System.out.println("Incorrect Usage");
             System.out.println(CLUtil.describeOptions(options).toString());
         } catch (Throwable e) {
             log.fatalError("An error occurred: ",e);
             System.out.println("An error occurred: " + e.getMessage());
             System.exit(1); // TODO - could this be return?
         }
     }
 
     // Update classloader if necessary
     private void updateClassLoader() {
             updatePath("search_paths",";", true); //$NON-NLS-1$//$NON-NLS-2$
             updatePath("user.classpath",File.pathSeparator, true);//$NON-NLS-1$
             updatePath("plugin_dependency_paths",";", false);//$NON-NLS-1$
     }
 
     private void updatePath(String property, String sep, boolean cp) {
         String userpath= JMeterUtils.getPropDefault(property,"");// $NON-NLS-1$
         if (userpath.length() <= 0) { return; }
         log.info(property+"="+userpath); //$NON-NLS-1$
         StringTokenizer tok = new StringTokenizer(userpath, sep);
         while(tok.hasMoreTokens()) {
             String path=tok.nextToken();
             File f=new File(path);
             if (!f.canRead() && !f.isDirectory()) {
                 log.warn("Can't read "+path);
             } else {
                 if (cp) {
                     log.info("Adding to classpath and loader: "+path);
                     try {
                         NewDriver.addPath(path);
                     } catch (MalformedURLException e) {
                         log.warn("Error adding: "+path+" "+e.getLocalizedMessage());
                     }
                 } else {
                     log.info("Adding to loader: "+path);
                     NewDriver.addURL(path);
                 }
             }
         }
     }
 
     /**
      *
      */
     private void startOptionalServers() {
         int bshport = JMeterUtils.getPropDefault("beanshell.server.port", 0);// $NON-NLS-1$
         String bshfile = JMeterUtils.getPropDefault("beanshell.server.file", "");// $NON-NLS-1$ $NON-NLS-2$
         if (bshport > 0) {
             log.info("Starting Beanshell server (" + bshport + "," + bshfile + ")");
             Runnable t = new BeanShellServer(bshport, bshfile);
             t.run();
         }
 
         // Should we run a beanshell script on startup?
         String bshinit = JMeterUtils.getProperty("beanshell.init.file");// $NON-NLS-1$
         if (bshinit != null){
             log.info("Run Beanshell on file: "+bshinit);
             try {
                 BeanShellInterpreter bsi = new BeanShellInterpreter();//bshinit,log);
                 bsi.source(bshinit);
             } catch (ClassNotFoundException e) {
                 log.warn("Could not start Beanshell: "+e.getLocalizedMessage());
             } catch (JMeterException e) {
                 log.warn("Could not process Beanshell file: "+e.getLocalizedMessage());
             }
         }
 
         int mirrorPort=JMeterUtils.getPropDefault("mirror.server.port", 0);// $NON-NLS-1$
         if (mirrorPort > 0){
             log.info("Starting Mirror server (" + mirrorPort + ")");
             try {
                 Object instance = ClassTools.construct(
                         "org.apache.jmeter.protocol.http.control.HttpMirrorControl",// $NON-NLS-1$
                         mirrorPort);
                 ClassTools.invoke(instance,"startHttpMirror");
             } catch (JMeterException e) {
                 log.warn("Could not start Mirror server",e);
             }
         }
     }
 
     /**
      * Sets a proxy server for the JVM if the command line arguments are
      * specified.
      */
     private void setProxy(CLArgsParser parser) throws IllegalUserActionException {
         if (parser.getArgumentById(PROXY_USERNAME) != null) {
             Properties jmeterProps = JMeterUtils.getJMeterProperties();
             if (parser.getArgumentById(PROXY_PASSWORD) != null) {
                 String u, p;
                 Authenticator.setDefault(new ProxyAuthenticator(u = parser.getArgumentById(PROXY_USERNAME)
                         .getArgument(), p = parser.getArgumentById(PROXY_PASSWORD).getArgument()));
                 log.info("Set Proxy login: " + u + "/" + p);
                 jmeterProps.setProperty(HTTP_PROXY_USER, u);//for Httpclient
                 jmeterProps.setProperty(HTTP_PROXY_PASS, p);//for Httpclient
             } else {
                 String u;
                 Authenticator.setDefault(new ProxyAuthenticator(u = parser.getArgumentById(PROXY_USERNAME)
                         .getArgument(), ""));
                 log.info("Set Proxy login: " + u);
                 jmeterProps.setProperty(HTTP_PROXY_USER, u);
             }
         }
         if (parser.getArgumentById(PROXY_HOST) != null && parser.getArgumentById(PROXY_PORT) != null) {
             String h = parser.getArgumentById(PROXY_HOST).getArgument();
             String p = parser.getArgumentById(PROXY_PORT).getArgument();
             System.setProperty("http.proxyHost",  h );// $NON-NLS-1$
             System.setProperty("https.proxyHost", h);// $NON-NLS-1$
             System.setProperty("http.proxyPort",  p);// $NON-NLS-1$
             System.setProperty("https.proxyPort", p);// $NON-NLS-1$
             log.info("Set http[s].proxyHost: " + h + " Port: " + p);
         } else if (parser.getArgumentById(PROXY_HOST) != null || parser.getArgumentById(PROXY_PORT) != null) {
             throw new IllegalUserActionException(JMeterUtils.getResString("proxy_cl_error"));// $NON-NLS-1$
         }
 
         if (parser.getArgumentById(NONPROXY_HOSTS) != null) {
             String n = parser.getArgumentById(NONPROXY_HOSTS).getArgument();
             System.setProperty("http.nonProxyHosts",  n );// $NON-NLS-1$
             System.setProperty("https.nonProxyHosts", n );// $NON-NLS-1$
             log.info("Set http[s].nonProxyHosts: "+n);
         }
     }
 
     private void initializeProperties(CLArgsParser parser) {
         if (parser.getArgumentById(PROPFILE_OPT) != null) {
             JMeterUtils.loadJMeterProperties(parser.getArgumentById(PROPFILE_OPT).getArgument());
         } else {
             JMeterUtils.loadJMeterProperties(NewDriver.getJMeterDir() + File.separator
                     + "bin" + File.separator // $NON-NLS-1$
                     + "jmeter.properties");// $NON-NLS-1$
         }
 
         if (parser.getArgumentById(JMLOGFILE_OPT) != null){
             String jmlogfile=parser.getArgumentById(JMLOGFILE_OPT).getArgument();
             jmlogfile = processLAST(jmlogfile, ".log");// $NON-NLS-1$
             JMeterUtils.setProperty(LoggingManager.LOG_FILE,jmlogfile);
         }
 
         JMeterUtils.initLogging();
         JMeterUtils.initLocale();
         // Bug 33845 - allow direct override of Home dir
         if (parser.getArgumentById(JMETER_HOME_OPT) == null) {
             JMeterUtils.setJMeterHome(NewDriver.getJMeterDir());
         } else {
             JMeterUtils.setJMeterHome(parser.getArgumentById(JMETER_HOME_OPT).getArgument());
         }
 
         Properties jmeterProps = JMeterUtils.getJMeterProperties();
         remoteProps = new Properties();
 
         // Add local JMeter properties, if the file is found
         String userProp = JMeterUtils.getPropDefault("user.properties",""); //$NON-NLS-1$
         if (userProp.length() > 0){ //$NON-NLS-1$
             FileInputStream fis=null;
             try {
                 File file = JMeterUtils.findFile(userProp);
                 if (file.canRead()){
                     log.info("Loading user properties from: "+file.getCanonicalPath());
                     fis = new FileInputStream(file);
                     Properties tmp = new Properties();
                     tmp.load(fis);
                     jmeterProps.putAll(tmp);
                     LoggingManager.setLoggingLevels(tmp);//Do what would be done earlier
                 }
             } catch (IOException e) {
                 log.warn("Error loading user property file: " + userProp, e);
             } finally {
                 JOrphanUtils.closeQuietly(fis);
             }
         }
 
         // Add local system properties, if the file is found
         String sysProp = JMeterUtils.getPropDefault("system.properties",""); //$NON-NLS-1$
         if (sysProp.length() > 0){
             FileInputStream fis=null;
             try {
                 File file = JMeterUtils.findFile(sysProp);
                 if (file.canRead()){
                     log.info("Loading system properties from: "+file.getCanonicalPath());
                     fis = new FileInputStream(file);
                     System.getProperties().load(fis);
                 }
             } catch (IOException e) {
                 log.warn("Error loading system property file: " + sysProp, e);
             } finally {
                 JOrphanUtils.closeQuietly(fis);
             }
         }
 
         // Process command line property definitions
         // These can potentially occur multiple times
 
         List<CLOption> clOptions = parser.getArguments();
         int size = clOptions.size();
 
         for (int i = 0; i < size; i++) {
             CLOption option = clOptions.get(i);
             String name = option.getArgument(0);
             String value = option.getArgument(1);
             FileInputStream fis = null;
 
             switch (option.getDescriptor().getId()) {
 
             // Should not have any text arguments
             case CLOption.TEXT_ARGUMENT:
                 throw new IllegalArgumentException("Unknown arg: "+option.getArgument());
 
             case PROPFILE2_OPT: // Bug 33920 - allow multiple props
                 try {
                     fis = new FileInputStream(new File(name));
                     Properties tmp = new Properties();
                     tmp.load(fis);
                     jmeterProps.putAll(tmp);
                     LoggingManager.setLoggingLevels(tmp);//Do what would be done earlier
                 } catch (FileNotFoundException e) {
                     log.warn("Can't find additional property file: " + name, e);
                 } catch (IOException e) {
                     log.warn("Error loading additional property file: " + name, e);
                 } finally {
                     JOrphanUtils.closeQuietly(fis);
                 }
                 break;
             case SYSTEM_PROPFILE:
                 log.info("Setting System properties from file: " + name);
                 try {
                     fis = new FileInputStream(new File(name));
                     System.getProperties().load(fis);
                 } catch (IOException e) {
                     log.warn("Cannot find system property file "+e.getLocalizedMessage());
                 } finally {
                     JOrphanUtils.closeQuietly(fis);
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
             case JMETER_GLOBAL_PROP:
                 if (value.length() > 0) { // Set it
                     log.info("Setting Global property: " + name + "=" + value);
                     remoteProps.setProperty(name, value);
                 } else {
                     File propFile = new File(name);
                     if (propFile.canRead()) {
                         log.info("Setting Global properties from the file "+name);
                         try {
                             fis = new FileInputStream(propFile);
                             remoteProps.load(fis);
                         } catch (FileNotFoundException e) {
                             log.warn("Could not find properties file: "+e.getLocalizedMessage());
                         } catch (IOException e) {
                             log.warn("Could not load properties file: "+e.getLocalizedMessage());
                         } finally {
                             JOrphanUtils.closeQuietly(fis);
                         }
                     }
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
             case REMOTE_STOP:
                 remoteStop = true;
                 break;
             default:
                 // ignored
                 break;
             }
         }
 
         String sample_variables = (String) jmeterProps.get(SampleEvent.SAMPLE_VARIABLES);
         if (sample_variables != null){
             remoteProps.put(SampleEvent.SAMPLE_VARIABLES, sample_variables);
         }
         jmeterProps.put("jmeter.version", JMeterUtils.getJMeterVersion());
     }
 
     /*
      * Checks for LAST or LASTsuffix.
      * Returns the LAST name with .JMX replaced by suffix.
      */
     private String processLAST(String jmlogfile, String suffix) {
         if (USE_LAST_JMX.equals(jmlogfile) || USE_LAST_JMX.concat(suffix).equals(jmlogfile)){
             String last = LoadRecentProject.getRecentFile(0);// most recent
             final String JMX_SUFFIX = ".JMX"; // $NON-NLS-1$
             if (last.toUpperCase(Locale.ENGLISH).endsWith(JMX_SUFFIX)){
                 jmlogfile=last.substring(0, last.length() - JMX_SUFFIX.length()).concat(suffix);
             }
         }
         return jmlogfile;
     }
 
     private void startNonGui(String testFile, String logFile, CLOption remoteStart)
             throws IllegalUserActionException {
         // add a system property so samplers can check to see if JMeter
         // is running in NonGui mode
         System.setProperty(JMETER_NON_GUI, "true");// $NON-NLS-1$
         JMeter driver = new JMeter();// TODO - why does it create a new instance?
         driver.remoteProps = this.remoteProps;
         driver.remoteStop = this.remoteStop;
         driver.parent = this;
         PluginManager.install(this, false);
 
         String remote_hosts_string = null;
         if (remoteStart != null) {
             remote_hosts_string = remoteStart.getArgument();
             if (remote_hosts_string == null) {
                 remote_hosts_string = JMeterUtils.getPropDefault(
                         "remote_hosts", //$NON-NLS-1$
                         "127.0.0.1");//$NON-NLS-1$
             }
         }
         if (testFile == null) {
             throw new IllegalUserActionException("Non-GUI runs require a test plan");
         }
         driver.runNonGui(testFile, logFile, remoteStart != null, remote_hosts_string);
     }
 
     // run test in batch mode
     private void runNonGui(String testFile, String logFile, boolean remoteStart, String remote_hosts_string) {
-        FileInputStream reader = null;
         try {
             File f = new File(testFile);
             if (!f.exists() || !f.isFile()) {
                 println("Could not open " + testFile);
                 return;
             }
             FileServer.getFileServer().setBaseForScript(f);
 
-            reader = new FileInputStream(f);
-            log.info("Loading file: " + f);
-
-            HashTree tree = SaveService.loadTree(reader);
+            HashTree tree = SaveService.loadTree(f);
 
             @SuppressWarnings("deprecation") // Deliberate use of deprecated ctor
             JMeterTreeModel treeModel = new JMeterTreeModel(new Object());// Create non-GUI version to avoid headless problems
             JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
             treeModel.addSubTree(tree, root);
 
             // Hack to resolve ModuleControllers in non GUI mode
             SearchByClass<ReplaceableController> replaceableControllers = new SearchByClass<ReplaceableController>(ReplaceableController.class);
             tree.traverse(replaceableControllers);
             Collection<ReplaceableController> replaceableControllersRes = replaceableControllers.getSearchResults();
             for (Iterator<ReplaceableController> iter = replaceableControllersRes.iterator(); iter.hasNext();) {
                 ReplaceableController replaceableController = iter.next();
                 replaceableController.resolveReplacementSubTree(root);
             }
 
             // Remove the disabled items
             // For GUI runs this is done in Start.java
             convertSubTree(tree);
 
             Summariser summer = null;
             String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");//$NON-NLS-1$
             if (summariserName.length() > 0) {
                 log.info("Creating summariser <" + summariserName + ">");
                 println("Creating summariser <" + summariserName + ">");
                 summer = new Summariser(summariserName);
             }
 
             if (logFile != null) {
                 ResultCollector logger = new ResultCollector(summer);
                 logger.setFilename(logFile);
                 tree.add(tree.getArray()[0], logger);
             }
             else {
                 // only add Summariser if it can not be shared with the ResultCollector
                 if (summer != null) {
                     tree.add(tree.getArray()[0], summer);
                 }
             }
             // Used for remote notification of threads start/stop,see BUG 54152
             // Summariser uses this feature to compute correctly number of threads 
             // when NON GUI mode is used
             tree.add(tree.getArray()[0], new RemoteThreadsListenerTestElement());
 
             List<JMeterEngine> engines = new LinkedList<JMeterEngine>();
             tree.add(tree.getArray()[0], new ListenToTest(parent, (remoteStart && remoteStop) ? engines : null));
             println("Created the tree successfully using "+testFile);
             if (!remoteStart) {
                 JMeterEngine engine = new StandardJMeterEngine();
                 engine.configure(tree);
                 long now=System.currentTimeMillis();
                 println("Starting the test @ "+new Date(now)+" ("+now+")");
                 engine.runTest();
                 engines.add(engine);
             } else {
                 java.util.StringTokenizer st = new java.util.StringTokenizer(remote_hosts_string, ",");//$NON-NLS-1$
                 List<String> hosts = new LinkedList<String>();
                 while (st.hasMoreElements()) {
                     hosts.add((String) st.nextElement());
                 }
                 
                 DistributedRunner distributedRunner=new DistributedRunner(this.remoteProps);
                 distributedRunner.setStdout(System.out);
                 distributedRunner.setStdErr(System.err);
                 distributedRunner.init(hosts, tree);
                 distributedRunner.start();
             }
             startUdpDdaemon(engines);
         } catch (Exception e) {
             System.out.println("Error in NonGUIDriver " + e.toString());
             log.error("Error in NonGUIDriver", e);
-        } finally {
-            JOrphanUtils.closeQuietly(reader);
         }
     }
 
     /**
      * Refactored from AbstractAction.java
      *
      * @param tree The {@link HashTree} to convert
      */
     public static void convertSubTree(HashTree tree) {
         LinkedList<Object> copyList = new LinkedList<Object>(tree.list());
         for (Object o  : copyList) {
             if (o instanceof TestElement) {
                 TestElement item = (TestElement) o;
                 if (item.isEnabled()) {
                     if (item instanceof ReplaceableController) {
                         ReplaceableController rc = ensureReplaceableControllerIsLoaded(item);
 
                         HashTree subTree = tree.getTree(item);
                         if (subTree != null) {
                             HashTree replacementTree = rc.getReplacementSubTree();
                             if (replacementTree != null) {
                                 convertSubTree(replacementTree);
                                 tree.replaceKey(item, rc);
                                 tree.set(rc, replacementTree);
                             }
                         } 
                     } else { // not Replaceable Controller
                         convertSubTree(tree.getTree(item));
                     }
                 } else { // Not enabled
                     tree.remove(item);
                 }
             } else { // Not a TestElement
                 JMeterTreeNode item = (JMeterTreeNode) o;
                 if (item.isEnabled()) {
                     // Replacement only needs to occur when starting the engine
                     // @see StandardJMeterEngine.run()
                     if (item.getUserObject() instanceof ReplaceableController) {
                         TestElement controllerAsItem = item.getTestElement();
                         ReplaceableController rc = ensureReplaceableControllerIsLoaded(controllerAsItem);
 
                         HashTree subTree = tree.getTree(item);
                         
                         if (subTree != null) {
                             HashTree replacementTree = rc.getReplacementSubTree();
                             if (replacementTree != null) {
                                 convertSubTree(replacementTree);
                                 tree.replaceKey(item, rc);
                                 tree.set(rc, replacementTree);
                             }
                         }
                     } else { // Not a ReplaceableController
                         convertSubTree(tree.getTree(item));
                         TestElement testElement = item.getTestElement();
                         tree.replaceKey(item, testElement);
                     }
                  } else { // Not enabled
                     tree.remove(item);
                 }
             }
         }
     }
 
     /**
      * Ensures the {@link ReplaceableController} is loaded
      * @param item {@link TestElement}
      * @return {@link ReplaceableController} loaded
      */
     private static ReplaceableController ensureReplaceableControllerIsLoaded(
             TestElement item) {
         ReplaceableController rc;
         // TODO this bit of code needs to be tidied up
         // Unfortunately ModuleController is in components, not core
         if (item.getClass().getName().equals("org.apache.jmeter.control.ModuleController")){ // Bug 47165
             rc = (ReplaceableController) item;
         } else {
             // HACK: force the controller to load its tree
             rc = (ReplaceableController) item.clone();
         }
         return rc;
     }
 
     /*
      * Listen to test and handle tidyup after non-GUI test completes.
      * If running a remote test, then after waiting a few seconds for listeners to finish files,
      * it calls ClientJMeterEngine.tidyRMI() to deal with the Naming Timer Thread.
      */
     private static class ListenToTest implements TestStateListener, Runnable, Remoteable {
         private final AtomicInteger started = new AtomicInteger(0); // keep track of remote tests
 
         //NOT YET USED private JMeter _parent;
 
         private final List<JMeterEngine> engines;
 
         /**
          * @param unused JMeter unused for now
          * @param engines List<JMeterEngine>
          */
         public ListenToTest(JMeter unused, List<JMeterEngine> engines) {
             //_parent = unused;
             this.engines=engines;
         }
 
         @Override
         public void testEnded(String host) {
             long now=System.currentTimeMillis();
             log.info("Finished remote host: " + host + " ("+now+")");
             if (started.decrementAndGet() <= 0) {
                 Thread stopSoon = new Thread(this);
                 stopSoon.start();
             }
         }
 
         @Override
         public void testEnded() {
             long now = System.currentTimeMillis();
             println("Tidying up ...    @ "+new Date(now)+" ("+now+")");
             println("... end of run");
             checkForRemainingThreads();
         }
 
         @Override
         public void testStarted(String host) {
             started.incrementAndGet();
             long now=System.currentTimeMillis();
             log.info("Started remote host:  " + host + " ("+now+")");
         }
 
         @Override
         public void testStarted() {
             long now=System.currentTimeMillis();
             log.info(JMeterUtils.getResString("running_test")+" ("+now+")");//$NON-NLS-1$
         }
 
         /**
          * This is a hack to allow listeners a chance to close their files. Must
          * implement a queue for sample responses tied to the engine, and the
          * engine won't deliver testEnded signal till all sample responses have
          * been delivered. Should also improve performance of remote JMeter
          * testing.
          */
         @Override
         public void run() {
             long now = System.currentTimeMillis();
             println("Tidying up remote @ "+new Date(now)+" ("+now+")");
             if (engines!=null){ // it will be null unless remoteStop = true
                 println("Exitting remote servers");
                 for (JMeterEngine e : engines){
                     e.exit();
                 }
             }
             try {
                 TimeUnit.SECONDS.sleep(5); // Allow listeners to close files
             } catch (InterruptedException ignored) {
             }
             ClientJMeterEngine.tidyRMI(log);
             println("... end of run");
             checkForRemainingThreads();
         }
 
         /**
          * Runs daemon thread which waits a short while; 
          * if JVM does not exit, lists remaining non-daemon threads on stdout.
          */
         private void checkForRemainingThreads() {
             // This cannot be a JMeter class variable, because properties
             // are not initialised until later.
             final int REMAIN_THREAD_PAUSE = 
                     JMeterUtils.getPropDefault("jmeter.exit.check.pause", 2000); // $NON-NLS-1$ 
             
             if (REMAIN_THREAD_PAUSE > 0) {
                 Thread daemon = new Thread(){
                     @Override
                     public void run(){
                         try {
                             TimeUnit.MILLISECONDS.sleep(REMAIN_THREAD_PAUSE); // Allow enough time for JVM to exit
                         } catch (InterruptedException ignored) {
                         }
                         // This is a daemon thread, which should only reach here if there are other
                         // non-daemon threads still active
                         System.out.println("The JVM should have exitted but did not.");
                         System.out.println("The following non-daemon threads are still running (DestroyJavaVM is OK):");
                         JOrphanUtils.displayThreads(false);
                     }
     
                 };
                 daemon.setDaemon(true);
                 daemon.start();
             } else if(REMAIN_THREAD_PAUSE<=0) {
                 if(log.isDebugEnabled()) {
                     log.debug("jmeter.exit.check.pause is <= 0, JMeter won't check for unterminated non-daemon threads");
                 }
             }
         }
 
     }
 
     private static void println(String str) {
         System.out.println(str);
     }
 
     private static final String[][] DEFAULT_ICONS = {
         { "org.apache.jmeter.control.gui.TestPlanGui",               "org/apache/jmeter/images/beaker.gif" },     //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.timers.gui.AbstractTimerGui",           "org/apache/jmeter/images/timer.gif" },      //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.threads.gui.ThreadGroupGui",            "org/apache/jmeter/images/thread.gif" },     //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.visualizers.gui.AbstractListenerGui",   "org/apache/jmeter/images/meter.png" },      //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.config.gui.AbstractConfigGui",          "org/apache/jmeter/images/testtubes.png" },  //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.processor.gui.AbstractPreProcessorGui", "org/apache/jmeter/images/leafnode.gif"},    //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.processor.gui.AbstractPostProcessorGui","org/apache/jmeter/images/leafnodeflip.gif"},//$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.control.gui.AbstractControllerGui",     "org/apache/jmeter/images/knob.gif" },       //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.control.gui.WorkBenchGui",              "org/apache/jmeter/images/clipboard.gif" },  //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.samplers.gui.AbstractSamplerGui",       "org/apache/jmeter/images/pipet.png" },      //$NON-NLS-1$ $NON-NLS-2$
         { "org.apache.jmeter.assertions.gui.AbstractAssertionGui",   "org/apache/jmeter/images/question.gif"}     //$NON-NLS-1$ $NON-NLS-2$
     };
 
     @Override
     public String[][] getIconMappings() {
         final String defaultIconProp = "org/apache/jmeter/images/icon.properties"; //$NON-NLS-1$
         String iconProp = JMeterUtils.getPropDefault("jmeter.icons", defaultIconProp);//$NON-NLS-1$
         Properties p = JMeterUtils.loadProperties(iconProp);
         if (p == null && !iconProp.equals(defaultIconProp)) {
             log.info(iconProp + " not found - using " + defaultIconProp);
             iconProp = defaultIconProp;
             p = JMeterUtils.loadProperties(iconProp);
         }
         if (p == null) {
             log.info(iconProp + " not found - using inbuilt icon set");
             return DEFAULT_ICONS;
         }
         log.info("Loaded icon properties from " + iconProp);
         String[][] iconlist = new String[p.size()][3];
         Enumeration<?> pe = p.keys();
         int i = 0;
         while (pe.hasMoreElements()) {
             String key = (String) pe.nextElement();
             String icons[] = JOrphanUtils.split(p.getProperty(key), " ");//$NON-NLS-1$
             iconlist[i][0] = key;
             iconlist[i][1] = icons[0];
             if (icons.length > 1) {
                 iconlist[i][2] = icons[1];
             }
             i++;
         }
         return iconlist;
     }
 
     @Override
     public String[][] getResourceBundles() {
         return new String[0][];
     }
 
     /**
      * Check if JMeter is running in non-GUI mode.
      *
      * @return true if JMeter is running in non-GUI mode.
      */
     public static boolean isNonGUI(){
         return "true".equals(System.getProperty(JMeter.JMETER_NON_GUI)); //$NON-NLS-1$
     }
 
     private void logProperty(String prop){
         log.info(prop+"="+System.getProperty(prop));//$NON-NLS-1$
     }
     private void logProperty(String prop,String separator){
         log.info(prop+separator+System.getProperty(prop));//$NON-NLS-1$
     }
 
     private static void startUdpDdaemon(final List<JMeterEngine> engines) {
         int port = JMeterUtils.getPropDefault("jmeterengine.nongui.port", UDP_PORT_DEFAULT); // $NON-NLS-1$
         int maxPort = JMeterUtils.getPropDefault("jmeterengine.nongui.maxport", 4455); // $NON-NLS-1$
         if (port > 1000){
             final DatagramSocket socket = getSocket(port, maxPort);
             if (socket != null) {
                 Thread waiter = new Thread("UDP Listener"){
                     @Override
                     public void run() {
                         waitForSignals(engines, socket);
                     }
                 };
                 waiter.setDaemon(true);
                 waiter.start();
             } else {
                 System.out.println("Failed to create UDP port");
             }
         }
     }
 
     private static void waitForSignals(final List<JMeterEngine> engines, DatagramSocket socket) {
         byte[] buf = new byte[80];
         System.out.println("Waiting for possible shutdown message on port "+socket.getLocalPort());
         DatagramPacket request = new DatagramPacket(buf, buf.length);
         try {
             while(true) {
                 socket.receive(request);
                 InetAddress address = request.getAddress();
                 // Only accept commands from the local host
                 if (address.isLoopbackAddress()){
                     String command = new String(request.getData(), request.getOffset(), request.getLength(),"ASCII");
                     System.out.println("Command: "+command+" received from "+address);
                     log.info("Command: "+command+" received from "+address);
                     if (command.equals("StopTestNow")){
                         for(JMeterEngine engine : engines) {
                             engine.stopTest(true);
                         }
                     } else if (command.equals("Shutdown")) {
                         for(JMeterEngine engine : engines) {
                             engine.stopTest(false);
                         }
                     } else if (command.equals("HeapDump")) {
                         HeapDumper.dumpHeap();
                     } else {
                         System.out.println("Command: "+command+" not recognised ");
                     }
                 }
             }
         } catch (Exception e) {
             System.out.println(e);
         } finally {
             socket.close();
         }
     }
 
     private static DatagramSocket getSocket(int udpPort, int udpPortMax) {
         DatagramSocket socket = null;
         int i = udpPort;
         while (i<= udpPortMax) {
             try {
                 socket = new DatagramSocket(i);
                 break;
             } catch (SocketException e) {
                 i++;
             }            
         }
 
         return socket;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Load.java b/src/core/org/apache/jmeter/gui/action/Load.java
index d1fb57af8..730bfa290 100644
--- a/src/core/org/apache/jmeter/gui/action/Load.java
+++ b/src/core/org/apache/jmeter/gui/action/Load.java
@@ -1,253 +1,246 @@
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
-import java.io.FileInputStream;
 import java.io.IOException;
-import java.io.InputStream;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 import javax.swing.JTree;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.FocusRequester;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
-import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * Handles the Open (load a new file) and Merge commands.
  *
  */
 public class Load implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final boolean expandTree = JMeterUtils.getPropDefault("onload.expandtree", false); //$NON-NLS-1$
 
     private static final Set<String> commands = new HashSet<String>();
 
     static {
         commands.add(ActionNames.OPEN);
         commands.add(ActionNames.MERGE);
     }
 
     public Load() {
         super();
     }
 
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     @Override
     public void doAction(final ActionEvent e) {
         final JFileChooser chooser = FileDialoger.promptToOpenFile(new String[] { ".jmx" }); //$NON-NLS-1$
         if (chooser == null) {
             return;
         }
         final File selectedFile = chooser.getSelectedFile();
         if (selectedFile != null) {
             final boolean merging = e.getActionCommand().equals(ActionNames.MERGE);
             // We must ask the user if it is ok to close current project
             if (!merging) { // i.e. it is OPEN
                 if (!Close.performAction(e)) {
                     return;
                 }
             }
             loadProjectFile(e, selectedFile, merging);
         }
     }
 
     /**
      * Loads or merges a file into the current GUI, reporting any errors to the user.
      * If the file is a complete test plan, sets the GUI test plan file name
      *
      * @param e the event that triggered the action
      * @param f the file to load
      * @param merging if true, then try to merge the file into the current GUI.
      */
     static void loadProjectFile(final ActionEvent e, final File f, final boolean merging) {
         loadProjectFile(e, f, merging, true);
     }
 
     /**
      * Loads or merges a file into the current GUI, reporting any errors to the user.
      * If the file is a complete test plan, sets the GUI test plan file name
      *
      * @param e the event that triggered the action
      * @param f the file to load
      * @param merging if true, then try to merge the file into the current GUI.
      * @param setDetails if true, then set the file details (if not merging)
      */
     static void loadProjectFile(final ActionEvent e, final File f, final boolean merging, final boolean setDetails) {
         ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.STOP_THREAD));
 
         final GuiPackage guiPackage = GuiPackage.getInstance();
         if (f != null) {
-            InputStream reader = null;
             try {
                 if (merging) {
                     log.info("Merging file: " + f);
                 } else {
                     log.info("Loading file: " + f);
                     // TODO should this be done even if not a full test plan?
                     // and what if load fails?
                     if (setDetails) {
                         FileServer.getFileServer().setBaseForScript(f);
                     }
                 }
-                reader = new FileInputStream(f);
-                final HashTree tree = SaveService.loadTree(reader);
+                final HashTree tree = SaveService.loadTree(f);
                 final boolean isTestPlan = insertLoadedTree(e.getID(), tree, merging);
 
                 // don't change name if merging
                 if (!merging && isTestPlan && setDetails) {
                     // TODO should setBaseForScript be called here rather than
                     // above?
                     guiPackage.setTestPlanFile(f.getAbsolutePath());
                 }
             } catch (NoClassDefFoundError ex) {// Allow for missing optional jars
                 reportError("Missing jar file", ex, true);
             } catch (ConversionException ex) {
                 log.warn("Could not convert file "+ex);
                 JMeterUtils.reportErrorToUser(SaveService.CEtoString(ex));
             } catch (IOException ex) {
                 reportError("Error reading file: ", ex, false);
             } catch (Exception ex) {
                 reportError("Unexpected error", ex, true);
-            } finally {
-                JOrphanUtils.closeQuietly(reader);
             }
             FileDialoger.setLastJFCDirectory(f.getParentFile().getAbsolutePath());
             guiPackage.updateCurrentGui();
             guiPackage.getMainFrame().repaint();
         }
     }
 
     /**
      * Inserts (or merges) the tree into the GUI.
      * Does not check if the previous tree has been saved.
      * Clears the existing GUI test plan if we are inserting a complete plan.
      * @param id the id for the ActionEvent that is created
      * @param tree the tree to load
      * @param merging true if the tree is to be merged; false if it is to replace the existing tree
      * @return true if the loaded tree was a full test plan
      * @throws IllegalUserActionException if the tree cannot be merged at the selected position or the tree is empty
      */
     // Does not appear to be used externally; called by #loadProjectFile()
     public static boolean insertLoadedTree(final int id, final HashTree tree, final boolean merging) throws IllegalUserActionException {
         // convertTree(tree);
         if (tree == null) {
             throw new IllegalUserActionException("Empty TestPlan or error reading test plan - see log file");
         }
         final boolean isTestPlan = tree.getArray()[0] instanceof TestPlan;
 
         // If we are loading a new test plan, initialize the tree with the testplan node we are loading
         final GuiPackage guiInstance = GuiPackage.getInstance();
         if(isTestPlan && !merging) {
             // Why does this not call guiInstance.clearTestPlan() ?
             // Is there a reason for not clearing everything?
             guiInstance.clearTestPlan((TestElement)tree.getArray()[0]);
         }
 
         if (merging){ // Check if target of merge is reasonable
             final TestElement te = (TestElement)tree.getArray()[0];
             if (!(te instanceof WorkBench || te instanceof TestPlan)){// These are handled specially by addToTree
                 final boolean ok = MenuFactory.canAddTo(guiInstance.getCurrentNode(), te);
                 if (!ok){
                     String name = te.getName();
                     String className = te.getClass().getName();
                     className = className.substring(className.lastIndexOf('.')+1);
                     throw new IllegalUserActionException("Can't merge "+name+" ("+className+") here");
                 }
             }
         }
         final HashTree newTree = guiInstance.addSubTree(tree);
         guiInstance.updateCurrentGui();
         guiInstance.getMainFrame().getTree().setSelectionPath(
                 new TreePath(((JMeterTreeNode) newTree.getArray()[0]).getPath()));
         final HashTree subTree = guiInstance.getCurrentSubTree();
         // Send different event wether we are merging a test plan into another test plan,
         // or loading a testplan from scratch
         ActionEvent actionEvent =
             new ActionEvent(subTree.get(subTree.getArray()[subTree.size() - 1]), id,
                     merging ? ActionNames.SUB_TREE_MERGED : ActionNames.SUB_TREE_LOADED);
 
         ActionRouter.getInstance().actionPerformed(actionEvent);
         final JTree jTree = guiInstance.getMainFrame().getTree();
         if (expandTree && !merging) { // don't automatically expand when merging
             for(int i = 0; i < jTree.getRowCount(); i++) {
                 jTree.expandRow(i);
             }
         } else {
             jTree.expandRow(0);
         }
         jTree.setSelectionPath(jTree.getPathForRow(1));
         FocusRequester.requestFocus(jTree);
         return isTestPlan;
     }
 
     /**
      * Inserts the tree into the GUI.
      * Does not check if the previous tree has been saved.
      * Clears the existing GUI test plan if we are inserting a complete plan.
      * @param id the id for the ActionEvent that is created
      * @param tree the tree to load
      * @return true if the loaded tree was a full test plan
      * @throws IllegalUserActionException if the tree cannot be merged at the selected position or the tree is empty
      */
     // Called by JMeter#startGui()
     public static boolean insertLoadedTree(final int id, final HashTree tree) throws IllegalUserActionException {
         return insertLoadedTree(id, tree, false);
     }
 
     // Helper method to simplify code
     private static void reportError(final String reason, final Throwable ex, final boolean stackTrace) {
         if (stackTrace) {
             log.warn(reason, ex);
         } else {
             log.warn(reason + ex);
         }
         String msg = ex.getMessage();
         if (msg == null) {
             msg = "Unexpected error - see log for details";
         }
         JMeterUtils.reportErrorToUser(msg);
     }
 
 }
diff --git a/src/core/org/apache/jmeter/save/SaveService.java b/src/core/org/apache/jmeter/save/SaveService.java
index 137a74e26..8de3846ec 100644
--- a/src/core/org/apache/jmeter/save/SaveService.java
+++ b/src/core/org/apache/jmeter/save/SaveService.java
@@ -1,640 +1,645 @@
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
 
 package org.apache.jmeter.save;
 
 import java.io.BufferedInputStream;
+import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.Writer;
 import java.lang.reflect.InvocationTargetException;
 import java.nio.charset.Charset;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.apache.commons.lang3.builder.ToStringBuilder;
 import org.apache.jmeter.reporters.ResultCollectorHelper;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.NameUpdater;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.XStream;
 import com.thoughtworks.xstream.converters.ConversionException;
 import com.thoughtworks.xstream.converters.Converter;
 import com.thoughtworks.xstream.converters.DataHolder;
 import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
 import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
 import com.thoughtworks.xstream.io.xml.XppDriver;
 import com.thoughtworks.xstream.mapper.CannotResolveClassException;
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.mapper.MapperWrapper;
 
 /**
  * Handles setting up XStream serialisation.
  * The class reads alias definitions from saveservice.properties.
  *
  */
 public class SaveService {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Names of DataHolder entries for JTL processing
     public static final String SAMPLE_EVENT_OBJECT = "SampleEvent"; // $NON-NLS-1$
     public static final String RESULTCOLLECTOR_HELPER_OBJECT = "ResultCollectorHelper"; // $NON-NLS-1$
 
     // Names of DataHolder entries for JMX processing
     public static final String TEST_CLASS_NAME = "TestClassName"; // $NON-NLS-1$
 
     private static final class XStreamWrapper extends XStream {
         private XStreamWrapper(ReflectionProvider reflectionProvider) {
             super(reflectionProvider);
         }
 
         // Override wrapMapper in order to insert the Wrapper in the chain
         @Override
         protected MapperWrapper wrapMapper(MapperWrapper next) {
             // Provide our own aliasing using strings rather than classes
             return new MapperWrapper(next){
             // Translate alias to classname and then delegate to wrapped class
             @Override
             public Class<?> realClass(String alias) {
                 String fullName = aliasToClass(alias);
                 if (fullName != null) {
                     fullName = NameUpdater.getCurrentName(fullName);
                 }
                 return super.realClass(fullName == null ? alias : fullName);
             }
             // Translate to alias and then delegate to wrapped class
             @Override
             public String serializedClass(@SuppressWarnings("rawtypes") // superclass does not use types 
                     Class type) {
                 if (type == null) {
                     return super.serializedClass(null); // was type, but that caused FindBugs warning
                 }
                 String alias = classToAlias(type.getName());
                 return alias == null ? super.serializedClass(type) : alias ;
                 }
             };
         }
     }
 
     private static final XStream JMXSAVER = new XStreamWrapper(new PureJavaReflectionProvider());
     private static final XStream JTLSAVER = new XStreamWrapper(new PureJavaReflectionProvider());
     static {
         JTLSAVER.setMode(XStream.NO_REFERENCES); // This is needed to stop XStream keeping copies of each class
     }
 
     // The XML header, with placeholder for encoding, since that is controlled by property
     private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"<ph>\"?>"; // $NON-NLS-1$
 
     // Default file name
     private static final String SAVESERVICE_PROPERTIES_FILE = "/bin/saveservice.properties"; // $NON-NLS-1$
 
     // Property name used to define file name
     private static final String SAVESERVICE_PROPERTIES = "saveservice_properties"; // $NON-NLS-1$
 
     // Define file format property names
     private static final String FILE_FORMAT = "file_format"; // $NON-NLS-1$
     private static final String FILE_FORMAT_TESTPLAN = "file_format.testplan"; // $NON-NLS-1$
     private static final String FILE_FORMAT_TESTLOG = "file_format.testlog"; // $NON-NLS-1$
 
     // Define file format versions
     private static final String VERSION_2_2 = "2.2";  // $NON-NLS-1$
 
     // Default to overall format, and then to version 2.2
     public static final String TESTPLAN_FORMAT
         = JMeterUtils.getPropDefault(FILE_FORMAT_TESTPLAN
         , JMeterUtils.getPropDefault(FILE_FORMAT, VERSION_2_2));
 
     public static final String TESTLOG_FORMAT
         = JMeterUtils.getPropDefault(FILE_FORMAT_TESTLOG
         , JMeterUtils.getPropDefault(FILE_FORMAT, VERSION_2_2));
 
     private static boolean validateFormat(String format){
         if ("2.2".equals(format)) return true;
         if ("2.1".equals(format)) return true;
         return false;
     }
 
     static{
         if (!validateFormat(TESTPLAN_FORMAT)){
             log.error("Invalid test plan format: "+TESTPLAN_FORMAT);
         }
         if (!validateFormat(TESTLOG_FORMAT)){
             log.error("Invalid test log format: "+TESTLOG_FORMAT);
         }
     }
 
     /** New XStream format - more compressed class names */
     public static final boolean IS_TESTPLAN_FORMAT_22
         = VERSION_2_2.equals(TESTPLAN_FORMAT);
 
     // Holds the mappings from the saveservice properties file
     // Key: alias Entry: full class name
     // There may be multiple aliases which map to the same class
     private static final Properties aliasToClass = new Properties();
 
     // Holds the reverse mappings
     // Key: full class name Entry: primary alias
     private static final Properties classToAlias = new Properties();
 
     // Version information for test plan header
     // This is written to JMX files by ScriptWrapperConverter
     // Also to JTL files by ResultCollector
     private static final String VERSION = "1.2"; // $NON-NLS-1$
 
     // This is written to JMX files by ScriptWrapperConverter
     private static String propertiesVersion = "";// read from properties file; written to JMX files
     
     // Must match _version property value in saveservice.properties
     // used to ensure saveservice.properties and SaveService are updated simultaneously
     private static final String PROPVERSION = "2.8";// Expected version $NON-NLS-1$
 
     // Internal information only
     private static String fileVersion = ""; // read from saveservice.properties file// $NON-NLS-1$
     // Must match Revision id value in saveservice.properties, 
     // used to ensure saveservice.properties and SaveService are updated simultaneously
     private static final String FILEVERSION = "1656252"; // Expected value $NON-NLS-1$
     private static String fileEncoding = ""; // read from properties file// $NON-NLS-1$
 
     static {
         log.info("Testplan (JMX) version: "+TESTPLAN_FORMAT+". Testlog (JTL) version: "+TESTLOG_FORMAT);
         initProps();
         checkVersions();
     }
 
     // Helper method to simplify alias creation from properties
     private static void makeAlias(String aliasList, String clazz) {
         String aliases[]=aliasList.split(","); // Can have multiple aliases for same target classname
         String alias=aliases[0];
         for (String a : aliases){
             Object old = aliasToClass.setProperty(a,clazz);
             if (old != null){
                 log.error("Duplicate class detected for "+alias+": "+clazz+" & "+old);                
             }
         }
         Object oldval=classToAlias.setProperty(clazz,alias);
         if (oldval != null) {
             log.error("Duplicate alias detected for "+clazz+": "+alias+" & "+oldval);
         }
     }
 
     public static Properties loadProperties() throws IOException{
         Properties nameMap = new Properties();
         FileInputStream fis = null;
         try {
             fis = new FileInputStream(JMeterUtils.getJMeterHome()
                          + JMeterUtils.getPropDefault(SAVESERVICE_PROPERTIES, SAVESERVICE_PROPERTIES_FILE));
             nameMap.load(fis);
         } finally {
             JOrphanUtils.closeQuietly(fis);
         }
         return nameMap;
     }
     private static void initProps() {
         // Load the alias properties
         try {
             Properties nameMap = loadProperties();
             // now create the aliases
             for (Map.Entry<Object, Object> me : nameMap.entrySet()) {
                 String key = (String) me.getKey();
                 String val = (String) me.getValue();
                 if (!key.startsWith("_")) { // $NON-NLS-1$
                     makeAlias(key, val);
                 } else {
                     // process special keys
                     if (key.equalsIgnoreCase("_version")) { // $NON-NLS-1$
                         propertiesVersion = val;
                         log.info("Using SaveService properties version " + propertiesVersion);
                     } else if (key.equalsIgnoreCase("_file_version")) { // $NON-NLS-1$
                             fileVersion = extractVersion(val);
                             log.info("Using SaveService properties file version " + fileVersion);
                     } else if (key.equalsIgnoreCase("_file_encoding")) { // $NON-NLS-1$
                         fileEncoding = val;
                         log.info("Using SaveService properties file encoding " + fileEncoding);
                     } else {
                         key = key.substring(1);// Remove the leading "_"
                         try {
                             final String trimmedValue = val.trim();
                             if (trimmedValue.equals("collection") // $NON-NLS-1$
                              || trimmedValue.equals("mapping")) { // $NON-NLS-1$
                                 registerConverter(key, JMXSAVER, true);
                                 registerConverter(key, JTLSAVER, true);
                             } else {
                                 registerConverter(key, JMXSAVER, false);
                                 registerConverter(key, JTLSAVER, false);
                             }
                         } catch (IllegalAccessException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         } catch (InstantiationException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         } catch (ClassNotFoundException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         } catch (IllegalArgumentException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         } catch (SecurityException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         } catch (InvocationTargetException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         } catch (NoSuchMethodException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         }
                     }
                 }
             }
         } catch (IOException e) {
             log.fatalError("Bad saveservice properties file", e);
             throw new JMeterError("JMeter requires the saveservice properties file to continue");
         }
     }
 
     /**
      * Register converter.
      * @param key
      * @param jmxsaver
      * @param useMapper
      *
      * @throws InstantiationException
      * @throws IllegalAccessException
      * @throws InvocationTargetException
      * @throws NoSuchMethodException
      * @throws ClassNotFoundException
      */
     private static void registerConverter(String key, XStream jmxsaver, boolean useMapper)
             throws InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException,
             ClassNotFoundException {
         if (useMapper){
             jmxsaver.registerConverter((Converter) Class.forName(key).getConstructor(
                     new Class[] { Mapper.class }).newInstance(
                             new Object[] { jmxsaver.getMapper() }));
         } else {
             jmxsaver.registerConverter((Converter) Class.forName(key).newInstance());
         }
     }
 
     // For converters to use
     public static String aliasToClass(String s){
         String r = aliasToClass.getProperty(s);
         return r == null ? s : r;
     }
 
     // For converters to use
     public static String classToAlias(String s){
         String r = classToAlias.getProperty(s);
         return r == null ? s : r;
     }
 
     // Called by Save function
     public static void saveTree(HashTree tree, OutputStream out) throws IOException {
         // Get the OutputWriter to use
         OutputStreamWriter outputStreamWriter = getOutputStreamWriter(out);
         writeXmlHeader(outputStreamWriter);
         // Use deprecated method, to avoid duplicating code
         ScriptWrapper wrapper = new ScriptWrapper();
         wrapper.testPlan = tree;
         JMXSAVER.toXML(wrapper, outputStreamWriter);
         outputStreamWriter.write('\n');// Ensure terminated properly
         outputStreamWriter.close();
     }
 
     // Used by Test code
     public static void saveElement(Object el, OutputStream out) throws IOException {
         // Get the OutputWriter to use
         OutputStreamWriter outputStreamWriter = getOutputStreamWriter(out);
         writeXmlHeader(outputStreamWriter);
         // Use deprecated method, to avoid duplicating code
         JMXSAVER.toXML(el, outputStreamWriter);
         outputStreamWriter.close();
     }
 
     // Used by Test code
     public static Object loadElement(InputStream in) throws IOException {
         // Get the InputReader to use
         InputStreamReader inputStreamReader = getInputStreamReader(in);
         // Use deprecated method, to avoid duplicating code
         Object element = JMXSAVER.fromXML(inputStreamReader);
         inputStreamReader.close();
         return element;
     }
 
     /**
      * Save a sampleResult to an XML output file using XStream.
      *
      * @param evt sampleResult wrapped in a sampleEvent
      * @param writer output stream which must be created using {@link #getFileEncoding(String)}
      * @throws IOException when writing data to output fails
      */
     // Used by ResultCollector.sampleOccurred(SampleEvent event)
     public synchronized static void saveSampleResult(SampleEvent evt, Writer writer) throws IOException {
         DataHolder dh = JTLSAVER.newDataHolder();
         dh.put(SAMPLE_EVENT_OBJECT, evt);
         // This is effectively the same as saver.toXML(Object, Writer) except we get to provide the DataHolder
         // Don't know why there is no method for this in the XStream class
         try {
             JTLSAVER.marshal(evt.getResult(), new XppDriver().createWriter(writer), dh);
         } catch(RuntimeException e) {
             throw new IllegalArgumentException("Failed marshalling:"+(evt.getResult() != null ? showDebuggingInfo(evt.getResult()) : "null"), e);
         }
         writer.write('\n');
     }
 
     /**
      * 
      * @param result SampleResult
      * @return String debugging information
      */
     private static String showDebuggingInfo(SampleResult result) {
         try {
             return "class:"+result.getClass()+",content:"+ToStringBuilder.reflectionToString(result);
         } catch(Exception e) {
             return "Exception occured creating debug from event, message:"+e.getMessage();
         }
     }
 
     /**
      * @param elem test element
      * @param writer output stream which must be created using {@link #getFileEncoding(String)}
      * @throws IOException when writing data to output fails
      */
     // Used by ResultCollector#recordStats()
     public synchronized static void saveTestElement(TestElement elem, Writer writer) throws IOException {
         JMXSAVER.toXML(elem, writer); // TODO should this be JTLSAVER? Only seems to be called by MonitorHealthVisualzer
         writer.write('\n');
     }
 
     private static boolean versionsOK = true;
 
     // Extract version digits from String of the form #Revision: n.mm #
     // (where # is actually $ above)
     private static final String REVPFX = "$Revision: ";
     private static final String REVSFX = " $"; // $NON-NLS-1$
 
     private static String extractVersion(String rev) {
         if (rev.length() > REVPFX.length() + REVSFX.length()) {
             return rev.substring(REVPFX.length(), rev.length() - REVSFX.length());
         }
         return rev;
     }
 
 //  private static void checkVersion(Class clazz, String expected) {
 //
 //      String actual = "*NONE*"; // $NON-NLS-1$
 //      try {
 //          actual = (String) clazz.getMethod("getVersion", null).invoke(null, null);
 //          actual = extractVersion(actual);
 //      } catch (Exception ignored) {
 //          // Not needed
 //      }
 //      if (0 != actual.compareTo(expected)) {
 //          versionsOK = false;
 //          log.warn("Version mismatch: expected '" + expected + "' found '" + actual + "' in " + clazz.getName());
 //      }
 //  }
 
     // Routines for TestSaveService
     static boolean checkPropertyVersion(){
         return SaveService.PROPVERSION.equals(SaveService.propertiesVersion);
     }
 
     static boolean checkFileVersion(){
         return SaveService.FILEVERSION.equals(SaveService.fileVersion);
     }
 
     // Allow test code to check for spurious class references
     static List<String> checkClasses(){
         final ClassLoader classLoader = SaveService.class.getClassLoader();
         List<String> missingClasses = new ArrayList<String>();
         //boolean OK = true;
         for (Object clazz : classToAlias.keySet()) {
             String name = (String) clazz;
             if (!NameUpdater.isMapped(name)) {// don't bother checking class is present if it is to be updated
                 try {
                     Class.forName(name, false, classLoader);
                 } catch (ClassNotFoundException e) {
                         log.error("Unexpected entry in saveservice.properties; class does not exist and is not upgraded: "+name);              
                         missingClasses.add(name);
                 }
             }
         }
         return missingClasses;
     }
 
     static boolean checkVersions() {
         versionsOK = true;
         // Disable converter version checks as they are more of a nuisance than helpful
 //      checkVersion(BooleanPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(HashTreeConverter.class, "514283"); // $NON-NLS-1$
 //      checkVersion(IntegerPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(LongPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(MultiPropertyConverter.class, "514283"); // $NON-NLS-1$
 //      checkVersion(SampleResultConverter.class, "571992"); // $NON-NLS-1$
 //
 //        // Not built until later, so need to use this method:
 //        try {
 //            checkVersion(
 //                    Class.forName("org.apache.jmeter.protocol.http.util.HTTPResultConverter"), // $NON-NLS-1$
 //                    "514283"); // $NON-NLS-1$
 //        } catch (ClassNotFoundException e) {
 //            versionsOK = false;
 //            log.warn(e.getLocalizedMessage());
 //        }
 //      checkVersion(StringPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(TestElementConverter.class, "549987"); // $NON-NLS-1$
 //      checkVersion(TestElementPropertyConverter.class, "549987"); // $NON-NLS-1$
 //      checkVersion(ScriptWrapperConverter.class, "514283"); // $NON-NLS-1$
 //      checkVersion(TestResultWrapperConverter.class, "514283"); // $NON-NLS-1$
 //        checkVersion(SampleSaveConfigurationConverter.class,"549936"); // $NON-NLS-1$
 
         if (!PROPVERSION.equalsIgnoreCase(propertiesVersion)) {
             log.warn("Bad _version - expected " + PROPVERSION + ", found " + propertiesVersion + ".");
         }
 //        if (!FILEVERSION.equalsIgnoreCase(fileVersion)) {
 //            log.warn("Bad _file_version - expected " + FILEVERSION + ", found " + fileVersion +".");
 //        }
         if (versionsOK) {
             log.info("All converter versions present and correct");
         }
         return versionsOK;
     }
 
     /**
      * Read results from JTL file.
      *
      * @param reader of the file
      * @param resultCollectorHelper helper class to enable TestResultWrapperConverter to deliver the samples
      * @throws IOException if an I/O error occurs
      */
     public static void loadTestResults(InputStream reader, ResultCollectorHelper resultCollectorHelper) throws IOException {
         // Get the InputReader to use
         InputStreamReader inputStreamReader = getInputStreamReader(reader);
         DataHolder dh = JTLSAVER.newDataHolder();
         dh.put(RESULTCOLLECTOR_HELPER_OBJECT, resultCollectorHelper); // Allow TestResultWrapper to feed back the samples
         // This is effectively the same as saver.fromXML(InputStream) except we get to provide the DataHolder
         // Don't know why there is no method for this in the XStream class
         JTLSAVER.unmarshal(new XppDriver().createReader(reader), null, dh);
         inputStreamReader.close();
     }
 
     /**
      * Load a Test tree (JMX file)
-     * @param reader on the JMX file
+     * @param file the JMX file
      * @return the loaded tree
      * @throws IOException if there is a problem reading the file or processing it
      */
-    public static HashTree loadTree(InputStream reader) throws IOException {
-        if (!reader.markSupported()) {
-            reader = new BufferedInputStream(reader);
-        }
-        reader.mark(Integer.MAX_VALUE);
-        ScriptWrapper wrapper = null;
+    public static HashTree loadTree(File file) throws IOException {
+        log.info("Loading file: " + file);
+        InputStream reader = null;
         try {
-            // Get the InputReader to use
-            InputStreamReader inputStreamReader = getInputStreamReader(reader);
-            wrapper = (ScriptWrapper) JMXSAVER.fromXML(inputStreamReader);
-            inputStreamReader.close();
-            if (wrapper == null){
-                log.error("Problem loading XML: see above.");
-                return null;
+            reader = new FileInputStream(file);
+            if (!reader.markSupported()) {
+                reader = new BufferedInputStream(reader);
             }
-            return wrapper.testPlan;
-        } catch (CannotResolveClassException e) {
-            if (e.getMessage().startsWith("node")) {
-                log.info("Problem loading XML, trying Avalon format");
-                reader.reset();
-                return OldSaveService.loadSubTree(reader);                
+            reader.mark(Integer.MAX_VALUE);
+            ScriptWrapper wrapper = null;
+            try {
+                // Get the InputReader to use
+                InputStreamReader inputStreamReader = getInputStreamReader(reader);
+                wrapper = (ScriptWrapper) JMXSAVER.fromXML(inputStreamReader);
+                inputStreamReader.close();
+                if (wrapper == null){
+                    log.error("Problem loading XML: see above.");
+                    return null;
+                }
+                return wrapper.testPlan;
+            } catch (CannotResolveClassException e) {
+                if (e.getMessage().startsWith("node")) {
+                    log.info("Problem loading XML, trying Avalon format");
+                    reader.reset();
+                    return OldSaveService.loadSubTree(reader);                
+                }
+                throw new IllegalArgumentException("Problem loading XML from:'"+file.getAbsolutePath()+"', cannot determine class for element: " + e, e);
+            } catch (NoClassDefFoundError e) {
+                throw new IllegalArgumentException("Problem loading XML from:'"+file.getAbsolutePath()+"', missing class "+e , e);
+            } catch (ConversionException e) {
+                throw new IllegalArgumentException("Problem loading XML from:'"+file.getAbsolutePath()+"', conversion error "+e , e);
             }
-            log.warn("Problem loading XML, cannot determine class for element: " + e.getLocalizedMessage());
-            return null;
-        } catch (NoClassDefFoundError e) {
-            log.error("Missing class "+e);
-            return null;
-        } catch (ConversionException e) {
-            log.error("Conversion error "+e);
-            return null;
+        } finally {
+            JOrphanUtils.closeQuietly(reader);
         }
     }
 
     private static InputStreamReader getInputStreamReader(InputStream inStream) {
         // Check if we have a encoding to use from properties
         Charset charset = getFileEncodingCharset();
         if(charset != null) {
             return new InputStreamReader(inStream, charset);
         }
         else {
             // We use the default character set encoding of the JRE
             return new InputStreamReader(inStream);
         }
     }
 
     private static OutputStreamWriter getOutputStreamWriter(OutputStream outStream) {
         // Check if we have a encoding to use from properties
         Charset charset = getFileEncodingCharset();
         if(charset != null) {
             return new OutputStreamWriter(outStream, charset);
         }
         else {
             // We use the default character set encoding of the JRE
             return new OutputStreamWriter(outStream);
         }
     }
 
     /**
      * Returns the file Encoding specified in saveservice.properties or the default
      * @param dflt value to return if file encoding was not provided
      *
      * @return file encoding or default
      */
     // Used by ResultCollector when creating output files
     public static String getFileEncoding(String dflt){
         if(fileEncoding != null && fileEncoding.length() > 0) {
             return fileEncoding;
         }
         else {
             return dflt;
         }
     }
 
     private static Charset getFileEncodingCharset() {
         // Check if we have a encoding to use from properties
         if(fileEncoding != null && fileEncoding.length() > 0) {
             return Charset.forName(fileEncoding);
         }
         else {
             // We use the default character set encoding of the JRE
             return null;
         }
     }
 
     private static void writeXmlHeader(OutputStreamWriter writer) throws IOException {
         // Write XML header if we have the charset to use for encoding
         Charset charset = getFileEncodingCharset();
         if(charset != null) {
             // We do not use getEncoding method of Writer, since that returns
             // the historical name
             String header = XML_HEADER.replaceAll("<ph>", charset.name());
             writer.write(header);
             writer.write('\n');
         }
     }
 
 //  Normal output
 //  ---- Debugging information ----
 //  required-type       : org.apache.jorphan.collections.ListedHashTree
 //  cause-message       : WebServiceSampler : WebServiceSampler
 //  class               : org.apache.jmeter.save.ScriptWrapper
 //  message             : WebServiceSampler : WebServiceSampler
 //  line number         : 929
 //  path                : /jmeterTestPlan/hashTree/hashTree/hashTree[4]/hashTree[5]/WebServiceSampler
 //  cause-exception     : com.thoughtworks.xstream.alias.CannotResolveClassException
 //  -------------------------------
 
     /**
      * Simplify getMessage() output from XStream ConversionException
      * @param ce - ConversionException to analyse
      * @return string with details of error
      */
     public static String CEtoString(ConversionException ce){
         String msg =
             "XStream ConversionException at line: " + ce.get("line number")
             + "\n" + ce.get("message")
             + "\nPerhaps a missing jar? See log file.";
         return msg;
     }
 
     public static String getPropertiesVersion() {
         return propertiesVersion;
     }
 
     public static String getVERSION() {
         return VERSION;
     }
 }
diff --git a/test/src/org/apache/jmeter/gui/action/TestLoad.java b/test/src/org/apache/jmeter/gui/action/TestLoad.java
index c3d167d89..cbb66134f 100644
--- a/test/src/org/apache/jmeter/gui/action/TestLoad.java
+++ b/test/src/org/apache/jmeter/gui/action/TestLoad.java
@@ -1,119 +1,112 @@
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
 
 import java.io.File;
-import java.io.FileInputStream;
 import java.io.FilenameFilter;
 import java.util.HashSet;
 import java.util.Set;
 
 import junit.framework.TestSuite;
 
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jorphan.collections.HashTree;
 
 /**
  * 
  * Test JMX files to check that they can be loaded OK.
  */
 public class TestLoad extends JMeterTestCase {
 
     private static final String basedir = new File(System.getProperty("user.dir")).getParent();
     private static final File testfiledir = new File(basedir,"bin/testfiles");
     private static final File demofiledir = new File(basedir,"xdocs/demos");
     
     private static final Set<String> notTestPlan = new HashSet<String>();// not full test plans
     
     static{
         notTestPlan.add("load_bug_list.jmx");// used by TestAnchorModifier
         notTestPlan.add("Load_JMeter_Page.jmx");// used by TestAnchorModifier
         notTestPlan.add("ProxyServerTestPlan.jmx");// used by TestSaveService
     }
 
     private static final FilenameFilter jmxFilter = new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
             return name.endsWith(".jmx");
         }
     };
 
     private final File testFile;
     private final String parent;
     
     public TestLoad(String name) {
         super(name);
         testFile=null;
         parent=null;
     }
 
     public TestLoad(String name, File file, String dir) {
         super(name);
         testFile=file;
         parent=dir;
     }
 
     public static TestSuite suite(){
         TestSuite suite=new TestSuite("Load Test");
         //suite.addTest(new TestLoad("checkGuiPackage"));
         scanFiles(suite,testfiledir);
         scanFiles(suite,demofiledir);
         return suite;
     }
 
     private static void scanFiles(TestSuite suite, File parent) {
         File testFiles[]=parent.listFiles(jmxFilter);
         String dir = parent.getName();
         for (int i=0; i<testFiles.length; i++){
             suite.addTest(new TestLoad("checkTestFile",testFiles[i],dir));
         }
     }
     
     public void checkTestFile() throws Exception{
         HashTree tree = null;
         try {
             tree =getTree(testFile);
         } catch (Exception e) {
             fail(parent+": "+ testFile.getName()+" caused "+e);
         }
         assertTree(tree);
     }
     
     private void assertTree(HashTree tree) throws Exception {
         assertNotNull(parent+": "+ testFile.getName()+" caused null tree: ",tree);
         final Object object = tree.getArray()[0];
         final String name = testFile.getName();
         
         if (! (object instanceof org.apache.jmeter.testelement.TestPlan) && !notTestPlan.contains(name)){
             fail(parent+ ": " +name+" tree should be TestPlan, but is "+object.getClass().getName());
         }
     }
 
     private HashTree getTree(File f) throws Exception {
-        FileInputStream fis = new FileInputStream(f);
-        HashTree tree = null;
-        try {
-            tree = SaveService.loadTree(fis);
-        } finally {
-            fis.close();
-        }
+        HashTree tree = SaveService.loadTree(f);
         return tree;
     }
 }
diff --git a/test/src/org/apache/jmeter/protocol/http/modifier/TestAnchorModifier.java b/test/src/org/apache/jmeter/protocol/http/modifier/TestAnchorModifier.java
index 2d294578e..155eb9e1b 100644
--- a/test/src/org/apache/jmeter/protocol/http/modifier/TestAnchorModifier.java
+++ b/test/src/org/apache/jmeter/protocol/http/modifier/TestAnchorModifier.java
@@ -1,343 +1,343 @@
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
 
-import java.io.FileInputStream;
+import java.io.File;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLEncoder;
 
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.protocol.http.sampler.HTTPNullSampler;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.io.TextFile;
 
 public class TestAnchorModifier extends JMeterTestCase {
         private AnchorModifier parser = new AnchorModifier();
         public TestAnchorModifier(String name) {
             super(name);
         }
 
         private JMeterContext jmctx = null;
 
         @Override
         public void setUp() {
             jmctx = JMeterContextService.getContext();
             parser.setThreadContext(jmctx);
         }
 
         public void testProcessingHTMLFile(String HTMLFileName) throws Exception {
-            HTTPSamplerBase config = (HTTPSamplerBase) SaveService.loadTree(
-                    new FileInputStream(System.getProperty("user.dir") + "/testfiles/load_bug_list.jmx")).getArray()[0];
+            File file = new File(System.getProperty("user.dir") + "/testfiles/load_bug_list.jmx");
+            HTTPSamplerBase config = (HTTPSamplerBase) SaveService.loadTree(file).getArray()[0];
             config.setRunningVersion(true);
             HTTPSampleResult result = new HTTPSampleResult();
-            HTTPSamplerBase context = (HTTPSamplerBase) SaveService.loadTree(
-                    new FileInputStream(System.getProperty("user.dir") + "/testfiles/Load_JMeter_Page.jmx")).getArray()[0];
+            file = new File(System.getProperty("user.dir") + "/testfiles/Load_JMeter_Page.jmx");
+            HTTPSamplerBase context = (HTTPSamplerBase) SaveService.loadTree(file).getArray()[0];
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             result.setResponseData(new TextFile(System.getProperty("user.dir") + HTMLFileName).getText(), null);
             result.setSampleLabel(context.toString());
             result.setSamplerData(context.toString());
             result.setURL(new URL("http://issues.apache.org/fakepage.html"));
             jmctx.setPreviousResult(result);
             AnchorModifier modifier = new AnchorModifier();
             modifier.setThreadContext(jmctx);
             modifier.process();
             assertEquals("http://issues.apache.org/bugzilla/buglist.cgi?"
                     + "bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED"
                     + "&email1=&emailtype1=substring&emailassigned_to1=1"
                     + "&email2=&emailtype2=substring&emailreporter2=1" + "&bugidtype=include&bug_id=&changedin=&votes="
                     + "&chfieldfrom=&chfieldto=Now&chfieldvalue="
                     + "&product=JMeter&short_desc=&short_desc_type=substring"
                     + "&long_desc=&long_desc_type=substring&bug_file_loc=" + "&bug_file_loc_type=substring&keywords="
                     + "&keywords_type=anywords" + "&field0-0-0=noop&type0-0-0=noop&value0-0-0="
                     + "&cmdtype=doit&order=Reuse+same+sort+as+last+time", config.toString());
             config.recoverRunningVersion();
             assertEquals("http://issues.apache.org/bugzilla/buglist.cgi?"
                     + "bug_status=.*&bug_status=.*&bug_status=.*&email1="
                     + "&emailtype1=substring&emailassigned_to1=1&email2=" + "&emailtype2=substring&emailreporter2=1"
                     + "&bugidtype=include&bug_id=&changedin=&votes=" + "&chfieldfrom=&chfieldto=Now&chfieldvalue="
                     + "&product=JMeter&short_desc=&short_desc_type=substring"
                     + "&long_desc=&long_desc_type=substring&bug_file_loc=" + "&bug_file_loc_type=substring&keywords="
                     + "&keywords_type=anywords&field0-0-0=noop" + "&type0-0-0=noop&value0-0-0=&cmdtype=doit"
                     + "&order=Reuse+same+sort+as+last+time", config.toString());
         }
 
         public void testModifySampler() throws Exception {
             testProcessingHTMLFile("/testfiles/jmeter_home_page.html");
         }
 
         public void testModifySamplerWithRelativeLink() throws Exception {
             testProcessingHTMLFile("/testfiles/jmeter_home_page_with_relative_links.html");
         }
 
         public void testModifySamplerWithBaseHRef() throws Exception {
             testProcessingHTMLFile("/testfiles/jmeter_home_page_with_base_href.html");
         }
 
         public void testSimpleParse() throws Exception {
             HTTPSamplerBase config = makeUrlConfig(".*/index\\.html");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"index.html\">Goto index page</a></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setSamplerData(context.toString());
             result.setURL(context.getUrl());
             jmctx.setPreviousResult(result);
             parser.process();
             assertEquals("http://www.apache.org/subdir/index.html", config.getUrl().toString());
         }
         
         // Test https works too
         public void testSimpleParse1() throws Exception {
             HTTPSamplerBase config = makeUrlConfig(".*/index\\.html");
             config.setProtocol(HTTPConstants.PROTOCOL_HTTPS);
             config.setPort(HTTPConstants.DEFAULT_HTTPS_PORT);
             HTTPSamplerBase context = makeContext("https://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"index.html\">Goto index page</a></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setSamplerData(context.toString());
             result.setURL(context.getUrl());
             jmctx.setPreviousResult(result);
             parser.process();
             assertEquals("https://www.apache.org/subdir/index.html", config.getUrl().toString());
         }
 
         public void testSimpleParse2() throws Exception {
             HTTPSamplerBase config = makeUrlConfig("/index\\.html");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"/index.html\">Goto index page</a>" + "hfdfjiudfjdfjkjfkdjf"
                     + "<b>bold text</b><a href=lowerdir/index.html>lower</a>" + "</body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             String newUrl = config.getUrl().toString();
             assertTrue("http://www.apache.org/index.html".equals(newUrl)
                     || "http://www.apache.org/subdir/lowerdir/index.html".equals(newUrl));
         }
 
         public void testSimpleParse3() throws Exception {
             HTTPSamplerBase config = makeUrlConfig(".*index.*");
             config.getArguments().addArgument("param1", "value1");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"/home/index.html?param1=value1\">" + "Goto index page</a></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             String newUrl = config.getUrl().toString();
             assertEquals("http://www.apache.org/home/index.html?param1=value1", newUrl);
         }
 
         public void testSimpleParse4() throws Exception {
             HTTPSamplerBase config = makeUrlConfig("/subdir/index\\..*");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<A HREF=\"index.html\">Goto index page</A></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             String newUrl = config.getUrl().toString();
             assertEquals("http://www.apache.org/subdir/index.html", newUrl);
         }
 
         public void testSimpleParse5() throws Exception {
             HTTPSamplerBase config = makeUrlConfig("/subdir/index\\.h.*");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/one/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"../index.html\">Goto index page</a></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             String newUrl = config.getUrl().toString();
             assertEquals("http://www.apache.org/subdir/index.html", newUrl);
         }
 
         public void testFailSimpleParse1() throws Exception {
             HTTPSamplerBase config = makeUrlConfig(".*index.*?param2=.+1");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"/home/index.html?param1=value1\">" + "Goto index page</a></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             String newUrl = config.getUrl().toString();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             assertEquals(newUrl, config.getUrl().toString());
         }
 
         public void testFailSimpleParse3() throws Exception {
             HTTPSamplerBase config = makeUrlConfig("/home/index.html");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"/home/index.html?param1=value1\">" + "Goto index page</a></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             String newUrl = config.getUrl().toString();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             assertEquals(newUrl + "?param1=value1", config.getUrl().toString());
         }
 
         public void testFailSimpleParse2() throws Exception {
             HTTPSamplerBase config = makeUrlConfig(".*login\\.html");
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<a href=\"/home/index.html?param1=value1\">" + "Goto index page</a></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setPreviousResult(result);
             parser.process();
             String newUrl = config.getUrl().toString();
             assertTrue(!"http://www.apache.org/home/index.html?param1=value1".equals(newUrl));
             assertEquals(config.getUrl().toString(), newUrl);
         }
 
         public void testSimpleFormParse() throws Exception {
             HTTPSamplerBase config = makeUrlConfig(".*index.html");
             config.addArgument("test", "g.*");
             config.setMethod(HTTPConstants.POST);
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<form action=\"index.html\" method=\"POST\">" + "<input type=\"checkbox\" name=\"test\""
                     + " value=\"goto\">Goto index page</form></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             assertEquals("http://www.apache.org/subdir/index.html", config.getUrl().toString());
             assertEquals("test=goto", config.getQueryString());
         }
 
         public void testBadCharParse() throws Exception {
             HTTPSamplerBase config = makeUrlConfig(".*index.html");
             config.addArgument("te$st", "g.*");
             config.setMethod(HTTPConstants.POST);
             HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
             String responseText = "<html><head><title>Test page</title></head><body>"
                     + "<form action=\"index.html\" method=\"POST\">" + "<input type=\"checkbox\" name=\"te$st\""
                     + " value=\"goto\">Goto index page</form></body></html>";
             HTTPSampleResult result = new HTTPSampleResult();
             result.setResponseData(responseText, null);
             result.setSampleLabel(context.toString());
             result.setURL(context.getUrl());
             jmctx.setCurrentSampler(context);
             jmctx.setCurrentSampler(config);
             jmctx.setPreviousResult(result);
             parser.process();
             assertEquals("http://www.apache.org/subdir/index.html", config.getUrl().toString());
             assertEquals("te%24st=goto", config.getQueryString());
         }
 
         public void testSpecialCharParse() throws Exception {
         String specialChars = "-_.!~*'()%25";// These are some of the special characters
         String htmlEncodedFixture = URLEncoder.encode(specialChars, "UTF-8");
         
         HTTPSamplerBase config = makeUrlConfig(".*index.html");
         config.addArgument("test", ".*");
         config.setMethod(HTTPConstants.POST);
         HTTPSamplerBase context = makeContext("http://www.apache.org/subdir/previous.html");
         String responseText = "<html><head><title>Test page</title></head><body>"
             + "<form action=\"index.html\" method=\"POST\">" + "<input type=\"hidden\" name=\"test\""
             + " value=\"" + htmlEncodedFixture + "\">Goto index page</form></body></html>";
         
         HTTPSampleResult result = new HTTPSampleResult();
         result.setResponseData(responseText, null);
         result.setSampleLabel(context.toString());
         result.setURL(context.getUrl());
         jmctx.setCurrentSampler(context);
         jmctx.setCurrentSampler(config);
         jmctx.setPreviousResult(result);
         parser.process();
         assertEquals("http://www.apache.org/subdir/index.html", config.getUrl().toString());
         assertEquals("test=" + htmlEncodedFixture, config.getQueryString());
       }
 
         
         private HTTPSamplerBase makeContext(String url) throws MalformedURLException {
             URL u = new URL(url);
             HTTPSamplerBase context = new HTTPNullSampler();
             context.setDomain(u.getHost());
             context.setPath(u.getPath());
             context.setPort(u.getPort());
             context.setProtocol(u.getProtocol());
             context.parseArguments(u.getQuery());
             return context;
         }
 
         private HTTPSamplerBase makeUrlConfig(String path) {
             HTTPSamplerBase config = new HTTPNullSampler();
             config.setDomain("www.apache.org");
             config.setMethod(HTTPConstants.GET);
             config.setPath(path);
             config.setPort(HTTPConstants.DEFAULT_HTTP_PORT);
             config.setProtocol(HTTPConstants.PROTOCOL_HTTP);
             return config;
         }
 }
diff --git a/test/src/org/apache/jmeter/save/TestSaveService.java b/test/src/org/apache/jmeter/save/TestSaveService.java
index a2024ad93..f18ede41b 100644
--- a/test/src/org/apache/jmeter/save/TestSaveService.java
+++ b/test/src/org/apache/jmeter/save/TestSaveService.java
@@ -1,214 +1,201 @@
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
 
 package org.apache.jmeter.save;
 
 import java.io.BufferedReader;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
-import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileReader;
-import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.util.List;
 
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 
 public class TestSaveService extends JMeterTestCase {
     
     // testLoadAndSave test files
     private static final String[] FILES = new String[] {
         "AssertionTestPlan.jmx",
         "AuthManagerTestPlan.jmx",
         "HeaderManagerTestPlan.jmx",
         "InterleaveTestPlan2.jmx", 
         "InterleaveTestPlan.jmx",
         "LoopTestPlan.jmx",
         "Modification Manager.jmx",
         "OnceOnlyTestPlan.jmx",
         "proxy.jmx",
         "ProxyServerTestPlan.jmx",
         "SimpleTestPlan.jmx",
         "GuiTest.jmx", 
         "GuiTest231.jmx",
         "GenTest27.jmx",
         "GenTest210.jmx",
         };
 
     // Test files for testLoadAndSave; output will generally be different in size but same number of lines
     private static final String[] FILES_LINES = new String[] {
         "GuiTest231_original.jmx",
         "GenTest25.jmx", // GraphAccumVisualizer obsolete, BSFSamplerGui now a TestBean
         "GenTest251.jmx", // GraphAccumVisualizer obsolete, BSFSamplerGui now a TestBean
         "GenTest26.jmx", // GraphAccumVisualizer now obsolete
         "GenTest27_original.jmx", // CTT changed to use intProp for mode
     };
 
     // Test files for testLoad; output will generally be different in size and line count
     private static final String[] FILES_LOAD_ONLY = new String[] {
         "GuiTest_original.jmx", 
         "GenTest22.jmx",
         "GenTest231.jmx",
         "GenTest24.jmx",
         };
 
     private static final boolean saveOut = JMeterUtils.getPropDefault("testsaveservice.saveout", false);
 
     public TestSaveService(String name) {
         super(name);
     }
     public void testPropfile() throws Exception {
         assertTrue("Property Version mismatch, ensure you update SaveService#PROPVERSION field with _version property value from saveservice.properties", SaveService.checkPropertyVersion());            
         assertTrue("Property File Version mismatch, ensure you update SaveService#FILEVERSION field with revision id of saveservice.properties", SaveService.checkFileVersion());
     }
     
     public void testVersions() throws Exception {
         assertTrue("Unexpected version found", SaveService.checkVersions());
     }
 
     public void testLoadAndSave() throws Exception {
         boolean failed = false; // Did a test fail?
 
         for (int i = 0; i < FILES.length; i++) {
             final String fileName = FILES[i];
             final File testFile = findTestFile("testfiles/" + fileName);
             failed |= loadAndSave(testFile, fileName, true);
         }
         for (int i = 0; i < FILES_LINES.length; i++) {
             final String fileName = FILES[i];
             final File testFile = findTestFile("testfiles/" + fileName);
             failed |= loadAndSave(testFile, fileName, false);
         }
         if (failed) // TODO make these separate tests?
         {
             fail("One or more failures detected");
         }
     }
 
     private boolean loadAndSave(File testFile, String fileName, boolean checkSize) throws Exception {
         
         boolean failed = false;
 
         int [] orig = readFile(new BufferedReader(new FileReader(testFile)));
 
-        InputStream in = null;
-        HashTree tree = null;
-        try {
-            in = new FileInputStream(testFile);
-            tree = SaveService.loadTree(in);
-        } finally {
-            if(in != null) {
-                in.close();
-            }
-        }
+        HashTree tree = SaveService.loadTree(testFile);
 
         ByteArrayOutputStream out = new ByteArrayOutputStream(1000000);
         try {
             SaveService.saveTree(tree, out);
         } finally {
             out.close(); // Make sure all the data is flushed out
         }
 
         ByteArrayInputStream ins = new ByteArrayInputStream(out.toByteArray());
         
         int [] output = readFile(new BufferedReader(new InputStreamReader(ins)));
         // We only check the length of the result. Comparing the
         // actual result (out.toByteArray==original) will usually
         // fail, because the order of the properties within each
         // test element may change. Comparing the lengths should be
         // enough to detect most problem cases...
         if ((checkSize && (orig[0] != output[0] ))|| orig[1] != output[1]) {
             failed = true;
             System.out.println();
             System.out.println("Loading file testfiles/" + fileName + " and "
                     + "saving it back changes its size from " + orig[0] + " to " + output[0] + ".");
             if (orig[1] != output[1]) {
                 System.out.println("Number of lines changes from " + orig[1] + " to " + output[1]);
             }
             if (saveOut) {
                 final File outFile = findTestFile("testfiles/" + fileName + ".out");
                 System.out.println("Write " + outFile);
                 FileOutputStream outf = null;
                 try {
                     outf = new FileOutputStream(outFile);
                     outf.write(out.toByteArray());
                 } finally {
                     if(outf != null) {
                         outf.close();
                     }
                 }
                 System.out.println("Wrote " + outFile);
             }
         }
 
         // Note this test will fail if a property is added or
         // removed to any of the components used in the test
         // files. The way to solve this is to appropriately change
         // the test file.
         return failed;
     }
     
     /**
      * Calculate size and line count ignoring EOL and 
      * "jmeterTestPlan" element which may vary because of 
      * different attributes/attribute lengths.
      */
     private int[] readFile(BufferedReader br) throws Exception {
         try {
             int length=0;
             int lines=0;
             String line;
             while((line=br.readLine()) != null) {
                 lines++;
                 if (!line.startsWith("<jmeterTestPlan")) {
                     length += line.length();
                 }
             }
             return new int []{length, lines};
         } finally {
             br.close();
         }
     }
 
     public void testLoad() throws Exception {
         for (int i = 0; i < FILES_LOAD_ONLY.length; i++) {
-            InputStream in = null;
+            File file = findTestFile("testfiles/" + FILES_LOAD_ONLY[i]);
             try {
-                in = new FileInputStream(findTestFile("testfiles/" + FILES_LOAD_ONLY[i]));
-                HashTree tree =SaveService.loadTree(in);
+                HashTree tree =SaveService.loadTree(file);
                 assertNotNull(tree);
-            } finally {
-                if(in != null) {
-                    in.close();
-                }
+            } catch(IllegalArgumentException ex) {
+                fail("Exception loading "+file.getAbsolutePath());
             }
+            
         }
 
     }
 
     public void testClasses(){
         List<String> missingClasses = SaveService.checkClasses();
         if(missingClasses.size()>0) {
             fail("One or more classes not found:"+missingClasses);
         }
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index fab4866e2..49d186b14 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,277 +1,278 @@
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
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.13 =================== -->
 
 <h1>Version 2.13</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <!-- <ch_category>Improvements</ch_category> -->
 <!-- <ch_title>Sample title</ch_title>
 <p>
 <ul>
 <li><bugzilla>48799</bugzilla> - Add time to establish connection to available sample metrics. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bugzilla>57500</bugzilla> - Introduce retry behavior for distributed testing. Implemented by Andrey Pokhilko and Dzimitry Kashlach and contributed by BlazeMeter Ltd.</li>
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 <!--  =================== Known bugs =================== -->
 
 
 <ch_section>Known bugs</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show 0 (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(...) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477</bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error: <i>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</i>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <b>jdk.certpath.disabledAlgorithms</b> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <pre>JAVA_HOME/jre/lib/security/java.security</pre>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>Since 2.13, Aggregate Graph, Summary Report and Aggregate Report now export percentages to %, before they exported the decimal value which differed from what was shown in GUI</li>
     <li>Third party plugins may be impacted by fix of <bugzilla>57586</bugzilla>, ensure that your subclass of HttpTestSampleGui implements ItemListener if you relied on parent class doing so.</li>
 </ul>
 
 <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bug>57385</bug>Getting empty thread name in xml result for HTTP requests with "Follow Redirects" set. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57579</bug>NullPointerException error is raised on main sample if "RETURN_NO_SAMPLE" is used (default) and "Use Cache-Control / Expires header..." is checked in HTTP Cache Manager</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bug>57447</bug>Use only the user listed DNS Servers, when "use custom DNS resolver" option is enabled.</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>57262</bug>Aggregate Report, Aggregate Graph and Summary Report export : headers use keys instead of labels</li>
 <li><bug>57346</bug>Summariser : The + (difference) reports show wrong elapsed time and throughput</li>
 <li><bug>57449</bug>Distributed Testing: Stripped modes do not strip responses from SubResults (affects load tests that use Download of embedded resources). Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57562</bug>View Results Tree CSS/JQuery Tester : Nothing happens when there is an error in syntax and an exception occurs in jmeter.log</li>
 <li><bug>57514</bug>Aggregate Graph, Summary Report and Aggregate Report show wrong percentage reporting in saved file</li>
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
 <li><bug>57365</bug>Selected LAF is not correctly setup due to call of UIManager.setLookAndFeel too late. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57364</bug>Options &lt; Look And Feel does not update all windows LAF. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57394</bug>When constructing an instance with ClassTools#construct(String, int) the integer was ignored and the default constructor was used instead.</li>
 <li><bug>57440</bug>OutOfMemoryError after introduction of JSyntaxTextArea in LoggerPanel due to disableUndo not being taken into account.</li>
 <li><bug>57569</bug>FileServer.reserveFile - inconsistent behaviour when hasHeader is true</li>
 <li><bug>57555</bug>Cannot use JMeter 2.12 as a maven dependency. Contributed by Pascal Schumacher (pascal.schumacher at t-systems.com)</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
 <li><bug>25430</bug>HTTP(S) Test Script Recorder : Make it populate HTTP Authorisation Manager. Partly based on a patch from Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><bug>57381</bug>HTTP(S) Test Script Recorder should display an error if Target Controller references a Recording Controller and no Recording Controller exists. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57488</bug>Performance : Improve SSLContext reset for Two-way SSL Authentication</li>
 <li><bug>57565</bug>SamplerCreator : Add method to allow implementations to add children to created sampler</li>
 <li><bug>57606</bug>HTTPSamplerBase#errorResult changes the sample label on exception </li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57322</bug>JDBC Test elements: add ResultHandler to deal with ResultSets(cursors) returned by callable statements. Contributed by Yngvi &amp;THORN;&amp;oacute;r Sigurj&amp;oacute;nsson (blitzkopf at gmail.com)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bug>57561</bug>Module controller UI : Replace combobox by tree. Contributed by Maciej Franek (maciej.franek at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>55932</bug>Create a Async BackendListener to allow easy plug of new listener (Graphite, JDBC, Console,...)</li>
 <li><bug>57246</bug>BackendListener : Create a Graphite implementation</li>
 <li><bug>57217</bug>Aggregate graph and Aggregate report improvements (3 configurable percentiles, same data in both, factor out code). Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
 <li><bug>57537</bug>BackendListener : Allow implementations to drop samples</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>54453</bug>Performance enhancements : Replace Random by ThreadLocalRandom in __Random function</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>57518</bug>Icons for toolbar with several sizes</li>
+<li><bug>57605</bug>When there is an error loading Test Plan, SaveService.loadTree returns null leading to NPE in callers</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to jsoup-1.8.3.jar (from 1.7.3)</li>
 <li>Updated to tika-core and tika-parsers 1.7 (from 1.6)</li>
 <li><bug>57276</bug>RMIC no longer needed since Java 5</li>
 <li><bug>57310</bug>Replace System.getProperty("file.separator") with File.separator throughout (Also "path.separator" with File.pathSeparator)</li>
 <li><bug>57389</bug>Fix potential NPE in converters</li>
 <li><bug>57417</bug>Remove unused method isTemporary from NullProperty. This was a leftover from a refactoring done in 2003.</li>
 <li><bug>57418</bug>Remove unused constructor from Workbench</li>
 <li><bug>57419</bug>Remove unused interface ModelListener.</li>
 <li><bug>57466</bug>IncludeController : Remove an unneeded set creation. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li>Added property loggerpanel.usejsyntaxtext to disable the use of JSyntaxTextArea for the Console Logger (in case of memory or other issues)</li>
 <li><bug>57586</bug>HttpTestSampleGui: Remove interface ItemListener implementation</li>
 </ul>
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Yngvi &amp;THORN;&amp;oacute;r Sigurj&amp;oacute;nsson (blitzkopf at gmail.com)</li>
 <li>Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><a href="http://blazemeter.com">BlazeMeter Ltd.</a></li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li>Pascal Schumacher (pascal.schumacher at t-systems.com)</li>
 <li>Maciej Franek (maciej.franek at gmail.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Chaitanya Bhatt (bhatt.chaitanya@gmail.com) for his thorough testing of new BackendListener and Graphite Client implementation.</li>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
 </section> 
 </body> 
 </document>
