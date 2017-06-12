diff --git a/src/core/org/apache/jmeter/JMeter.java b/src/core/org/apache/jmeter/JMeter.java
index 02b2b6656..5306b7b6c 100644
--- a/src/core/org/apache/jmeter/JMeter.java
+++ b/src/core/org/apache/jmeter/JMeter.java
@@ -1,1330 +1,1336 @@
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
 import java.io.InputStream;
 import java.net.Authenticator;
 import java.net.DatagramPacket;
 import java.net.DatagramSocket;
 import java.net.InetAddress;
 import java.net.MalformedURLException;
 import java.net.SocketException;
 import java.nio.charset.StandardCharsets;
 import java.text.SimpleDateFormat;
 import java.util.Collection;
 import java.util.Date;
 import java.util.Enumeration;
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
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.control.ReplaceableController;
 import org.apache.jmeter.engine.ClientJMeterEngine;
 import org.apache.jmeter.engine.DistributedRunner;
 import org.apache.jmeter.engine.JMeterEngine;
 import org.apache.jmeter.engine.RemoteJMeterEngineImpl;
 import org.apache.jmeter.engine.StandardJMeterEngine;
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
 import org.apache.jmeter.report.config.ConfigurationException;
 import org.apache.jmeter.report.dashboard.GenerationException;
 import org.apache.jmeter.report.dashboard.ReportGenerator;
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
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassTools;
 import org.apache.jorphan.util.HeapDumper;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
 import org.apache.logging.log4j.Level;
 import org.apache.logging.log4j.core.config.Configurator;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * Main JMeter class; processes options and starts the GUI, non-GUI or server as appropriate.
  */
 public class JMeter implements JMeterPlugin {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMeter.class);
     
     public static final int UDP_PORT_DEFAULT = 4445; // needed for ShutdownClient
 
     public static final String HTTP_PROXY_PASS = "http.proxyPass"; // $NON-NLS-1$
 
     public static final String HTTP_PROXY_USER = "http.proxyUser"; // $NON-NLS-1$
 
     public static final String JMETER_NON_GUI = "JMeter.NonGui"; // $NON-NLS-1$
     
     public static final String JMETER_REPORT_OUTPUT_DIR_PROPERTY = 
             "jmeter.reportgenerator.outputdir"; //$NON-NLS-1$
 
     // Icons size in the JMeter tree
     public static final String TREE_ICON_SIZE = "jmeter.tree.icons.size"; //$NON-NLS-1$
 
     public static final String DEFAULT_TREE_ICON_SIZE = "19x19"; //$NON-NLS-1$
 
     protected static final String KEY_SIZE = "<SIZE>"; //$NON-NLS-1$
 
     // If the -t flag is to "LAST", then the last loaded file (if any) is used
     private static final String USE_LAST_JMX = "LAST";
     // If the -j  or -l flag is set to LAST or LAST.log|LAST.jtl, then the last loaded file name is used to
     // generate the log file name by removing .JMX and replacing it with .log|.jtl
 
     private static final int PROXY_PASSWORD     = 'a';// $NON-NLS-1$
     private static final int JMETER_HOME_OPT    = 'd';// $NON-NLS-1$
     private static final int HELP_OPT           = 'h';// $NON-NLS-1$
     private static final int OPTIONS_OPT        = '?';// $NON-NLS-1$
     // logging configuration file
     private static final int JMLOGCONF_OPT      = 'i';// $NON-NLS-1$
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
     private static final int REPORT_GENERATING_OPT  = 'g';// $NON-NLS-1$
     private static final int REPORT_AT_END_OPT      = 'e';// $NON-NLS-1$
     private static final int REPORT_OUTPUT_FOLDER_OPT      = 'o';// $NON-NLS-1$
     
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
     
     private static final String JMX_SUFFIX = ".JMX"; // $NON-NLS-1$
 
     private static final String PACKAGE_PREFIX = "org.apache."; //$NON_NLS-1$
 
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
     private static final CLOptionDescriptor D_OPTIONS_OPT =
             new CLOptionDescriptor("?", CLOptionDescriptor.ARGUMENT_DISALLOWED, OPTIONS_OPT,
                 "print command line options and exit");
     private static final CLOptionDescriptor D_HELP_OPT =
             new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED, HELP_OPT,
                     "print usage information and exit");
     private static final CLOptionDescriptor D_VERSION_OPT =
             new CLOptionDescriptor("version", CLOptionDescriptor.ARGUMENT_DISALLOWED, VERSION_OPT,
                     "print the version information and exit");
     private static final CLOptionDescriptor D_PROPFILE_OPT =
             new CLOptionDescriptor("propfile", CLOptionDescriptor.ARGUMENT_REQUIRED, PROPFILE_OPT,
                     "the jmeter property file to use");
     private static final CLOptionDescriptor D_PROPFILE2_OPT =
             new CLOptionDescriptor("addprop", CLOptionDescriptor.ARGUMENT_REQUIRED
                     | CLOptionDescriptor.DUPLICATES_ALLOWED, PROPFILE2_OPT,
                     "additional JMeter property file(s)");
     private static final CLOptionDescriptor D_TESTFILE_OPT =
             new CLOptionDescriptor("testfile", CLOptionDescriptor.ARGUMENT_REQUIRED, TESTFILE_OPT,
                     "the jmeter test(.jmx) file to run");
     private static final CLOptionDescriptor D_LOGFILE_OPT =
             new CLOptionDescriptor("logfile", CLOptionDescriptor.ARGUMENT_REQUIRED, LOGFILE_OPT,
                     "the file to log samples to");
     private static final CLOptionDescriptor D_JMLOGCONF_OPT =
             new CLOptionDescriptor("jmeterlogconf", CLOptionDescriptor.ARGUMENT_REQUIRED, JMLOGCONF_OPT,
                     "jmeter logging configuration file (log4j2.xml)");
     private static final CLOptionDescriptor D_JMLOGFILE_OPT =
             new CLOptionDescriptor("jmeterlogfile", CLOptionDescriptor.ARGUMENT_REQUIRED, JMLOGFILE_OPT,
                     "jmeter run log file (jmeter.log)");
     private static final CLOptionDescriptor D_NONGUI_OPT =
             new CLOptionDescriptor("nongui", CLOptionDescriptor.ARGUMENT_DISALLOWED, NONGUI_OPT,
                     "run JMeter in nongui mode");
     private static final CLOptionDescriptor D_SERVER_OPT =
             new CLOptionDescriptor("server", CLOptionDescriptor.ARGUMENT_DISALLOWED, SERVER_OPT,
                     "run the JMeter server");
     private static final CLOptionDescriptor D_PROXY_HOST =
             new CLOptionDescriptor("proxyHost", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_HOST,
                     "Set a proxy server for JMeter to use");
     private static final CLOptionDescriptor D_PROXY_PORT =
             new CLOptionDescriptor("proxyPort", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_PORT,
                     "Set proxy server port for JMeter to use");
     private static final CLOptionDescriptor D_NONPROXY_HOSTS =
             new CLOptionDescriptor("nonProxyHosts", CLOptionDescriptor.ARGUMENT_REQUIRED, NONPROXY_HOSTS,
                     "Set nonproxy host list (e.g. *.apache.org|localhost)");
     private static final CLOptionDescriptor D_PROXY_USERNAME =
             new CLOptionDescriptor("username", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_USERNAME,
                     "Set username for proxy server that JMeter is to use");
     private static final CLOptionDescriptor D_PROXY_PASSWORD =
             new CLOptionDescriptor("password", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_PASSWORD,
                     "Set password for proxy server that JMeter is to use");
     private static final CLOptionDescriptor D_JMETER_PROPERTY =
             new CLOptionDescriptor("jmeterproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_PROPERTY,
                     "Define additional JMeter properties");
     private static final CLOptionDescriptor D_JMETER_GLOBAL_PROP =
             new CLOptionDescriptor("globalproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_GLOBAL_PROP,
                     "Define Global properties (sent to servers)\n\t\te.g. -Gport=123 or -Gglobal.properties");
     private static final CLOptionDescriptor D_SYSTEM_PROPERTY =
             new CLOptionDescriptor("systemproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, SYSTEM_PROPERTY,
                     "Define additional system properties");
     private static final CLOptionDescriptor D_SYSTEM_PROPFILE =
             new CLOptionDescriptor("systemPropertyFile", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENT_REQUIRED, SYSTEM_PROPFILE,
                     "additional system property file(s)");
     private static final CLOptionDescriptor D_LOGLEVEL =
             new CLOptionDescriptor("loglevel", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, LOGLEVEL,
                     "[category=]level e.g. jorphan=INFO or jmeter.util=DEBUG");
     private static final CLOptionDescriptor D_REMOTE_OPT =
             new CLOptionDescriptor("runremote", CLOptionDescriptor.ARGUMENT_DISALLOWED, REMOTE_OPT,
                     "Start remote servers (as defined in remote_hosts)");
     private static final CLOptionDescriptor D_REMOTE_OPT_PARAM =
             new CLOptionDescriptor("remotestart", CLOptionDescriptor.ARGUMENT_REQUIRED, REMOTE_OPT_PARAM,
                     "Start these remote servers (overrides remote_hosts)");
     private static final CLOptionDescriptor D_JMETER_HOME_OPT =
             new CLOptionDescriptor("homedir", CLOptionDescriptor.ARGUMENT_REQUIRED, JMETER_HOME_OPT,
                     "the jmeter home directory to use");
     private static final CLOptionDescriptor D_REMOTE_STOP =
             new CLOptionDescriptor("remoteexit", CLOptionDescriptor.ARGUMENT_DISALLOWED, REMOTE_STOP,
                     "Exit the remote servers at end of test (non-GUI)");
     private static final CLOptionDescriptor D_REPORT_GENERATING_OPT =
             new CLOptionDescriptor("reportonly",
                     CLOptionDescriptor.ARGUMENT_REQUIRED, REPORT_GENERATING_OPT,
                     "generate report dashboard only, from a test results file",
                     new CLOptionDescriptor[]{ D_NONGUI_OPT, D_REMOTE_OPT, D_REMOTE_OPT_PARAM, D_LOGFILE_OPT }); // disallowed
     private static final CLOptionDescriptor D_REPORT_AT_END_OPT =
             new CLOptionDescriptor("reportatendofloadtests",
                     CLOptionDescriptor.ARGUMENT_DISALLOWED, REPORT_AT_END_OPT,
                     "generate report dashboard after load test");
     private static final CLOptionDescriptor D_REPORT_OUTPUT_FOLDER_OPT =
             new CLOptionDescriptor("reportoutputfolder",
                     CLOptionDescriptor.ARGUMENT_REQUIRED, REPORT_OUTPUT_FOLDER_OPT,
                     "output folder for report dashboard");
 
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
     
     private static final CLOptionDescriptor[] options = new CLOptionDescriptor[] {
             D_OPTIONS_OPT,
             D_HELP_OPT,
             D_VERSION_OPT,
             D_PROPFILE_OPT,
             D_PROPFILE2_OPT,
             D_TESTFILE_OPT,
             D_LOGFILE_OPT,
             D_JMLOGCONF_OPT,
             D_JMLOGFILE_OPT,
             D_NONGUI_OPT,
             D_SERVER_OPT,
             D_PROXY_HOST,
             D_PROXY_PORT,
             D_NONPROXY_HOSTS,
             D_PROXY_USERNAME,
             D_PROXY_PASSWORD,
             D_JMETER_PROPERTY,
             D_JMETER_GLOBAL_PROP,
             D_SYSTEM_PROPERTY,
             D_SYSTEM_PROPFILE,
             D_LOGLEVEL,
             D_REMOTE_OPT,
             D_REMOTE_OPT_PARAM,
             D_JMETER_HOME_OPT,
             D_REMOTE_STOP,
             D_REPORT_GENERATING_OPT,
             D_REPORT_AT_END_OPT,
             D_REPORT_OUTPUT_FOLDER_OPT,
     };
     
     /** Properties to be sent to remote servers */
     private Properties remoteProps; 
 
     /** should remote engines be stopped at end of non-GUI test? */
     private boolean remoteStop; 
 
     public JMeter() {
         super();
     }
 
 
     /**
      * Starts up JMeter in GUI mode
      */
     private void startGui(String testFile) {
         System.out.println("================================================================================");//NOSONAR
         System.out.println("Don't use GUI mode for load testing, only for Test creation and Test debugging !");//NOSONAR
         System.out.println("For load testing, use NON GUI Mode:");//NOSONAR
         System.out.println("   jmeter -n -t [jmx file] -l [results file] -e -o [Path to output folder]");//NOSONAR
         System.out.println("& adapt Java Heap to your test requirements:");//NOSONAR
         System.out.println("   Modify HEAP=\"-Xms512m -Xmx512m\" in the JMeter batch file");//NOSONAR
         System.out.println("================================================================================");//NOSONAR
         
         SplashScreen splash = new SplashScreen();
         splash.showScreen();
         String jMeterLaf = LookAndFeelCommand.getJMeterLaf();
         try {
             UIManager.setLookAndFeel(jMeterLaf);
         } catch (Exception ex) {
-            log.warn("Could not set LAF to:"+jMeterLaf, ex);
+            log.warn("Could not set LAF to: {}", jMeterLaf, ex);
         }
         splash.setProgress(10);
         JMeterUtils.applyHiDPIOnFonts();
         PluginManager.install(this, true);
 
         JMeterTreeModel treeModel = new JMeterTreeModel();
         splash.setProgress(30);
         JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
         final ActionRouter instance = ActionRouter.getInstance();
         instance.populateCommandMap();
         splash.setProgress(60);
         treeLis.setActionHandler(instance);
         GuiPackage.initInstance(treeLis, treeModel);
         splash.setProgress(80);
         MainFrame main = new MainFrame(treeModel, treeLis);
         splash.setProgress(100);
         ComponentUtil.centerComponentInWindow(main, 80);
         main.setVisible(true);
         instance.actionPerformed(new ActionEvent(main, 1, ActionNames.ADD_ALL));
         if (testFile != null) {
             try {
                 File f = new File(testFile);
-                log.info("Loading file: " + f);
+                log.info("Loading file: {}", f);
                 FileServer.getFileServer().setBaseForScript(f);
 
                 HashTree tree = SaveService.loadTree(f);
 
                 GuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());
 
                 Load.insertLoadedTree(1, tree);
             } catch (ConversionException e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(SaveService.CEtoString(e));
             } catch (Exception e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(e.toString());
             }
         } else {
             JTree jTree = GuiPackage.getInstance().getMainFrame().getTree();
             TreePath path = jTree.getPathForRow(0);
             jTree.setSelectionPath(path);
             FocusRequester.requestFocus(jTree);
         }
         splash.setProgress(100);
         splash.close();
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
             System.err.println("Error: " + error);//NOSONAR
             System.out.println("Usage");//NOSONAR
             System.out.println(CLUtil.describeOptions(options).toString());//NOSONAR
             // repeat the error so no need to scroll back past the usage to see it
             System.out.println("Error: " + error);//NOSONAR
             return;
         }
         try {
             initializeProperties(parser); // Also initialises JMeter logging
 
             Thread.setDefaultUncaughtExceptionHandler(
                     (Thread t, Throwable e) -> {
                     if (!(e instanceof ThreadDeath)) {
                         log.error("Uncaught exception: ", e);
                         System.err.println("Uncaught Exception " + e + ". See log file for details.");//NOSONAR
                     }
             });
 
-            log.info(JMeterUtils.getJMeterCopyright());
-            log.info("Version " + JMeterUtils.getJMeterVersion());
-            logProperty("java.version"); //$NON-NLS-1$
-            logProperty("java.vm.name"); //$NON-NLS-1$
-            logProperty("os.name"); //$NON-NLS-1$
-            logProperty("os.arch"); //$NON-NLS-1$
-            logProperty("os.version"); //$NON-NLS-1$
-            logProperty("file.encoding"); // $NON-NLS-1$
-            log.info("Max memory     ="+ Runtime.getRuntime().maxMemory());
-            log.info("Available Processors ="+ Runtime.getRuntime().availableProcessors());
-            log.info("Default Locale=" + Locale.getDefault().getDisplayName());
-            log.info("JMeter  Locale=" + JMeterUtils.getLocale().getDisplayName());
-            log.info("JMeterHome="     + JMeterUtils.getJMeterHome());
-            logProperty("user.dir","  ="); //$NON-NLS-1$
-            log.info("PWD       ="+new File(".").getCanonicalPath());//$NON-NLS-1$
-            log.info("IP: "+JMeterUtils.getLocalHostIP()
-                    +" Name: "+JMeterUtils.getLocalHostName()
-                    +" FullName: "+JMeterUtils.getLocalHostFullName());
+            if (log.isInfoEnabled()) {
+                log.info(JMeterUtils.getJMeterCopyright());
+                log.info("Version {}", JMeterUtils.getJMeterVersion());
+                log.info("java.version={}", System.getProperty("java.version"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("java.vm.name={}", System.getProperty("java.vm.name"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("os.name={}", System.getProperty("os.name"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("os.arch={}", System.getProperty("os.arch"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("os.version={}", System.getProperty("os.version"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("file.encoding={}", System.getProperty("file.encoding"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("Max memory     ={}", Runtime.getRuntime().maxMemory());
+                log.info("Available Processors ={}", Runtime.getRuntime().availableProcessors());
+                log.info("Default Locale={}", Locale.getDefault().getDisplayName());
+                log.info("JMeter  Locale={}", JMeterUtils.getLocale().getDisplayName());
+                log.info("JMeterHome={}", JMeterUtils.getJMeterHome());
+                log.info("user.dir  ={}", System.getProperty("user.dir"));//$NON-NLS-1$ //$NON-NLS-2$
+                log.info("PWD       ={}", new File(".").getCanonicalPath());//$NON-NLS-1$
+                log.info("IP: {} Name: {} FullName: {}", JMeterUtils.getLocalHostIP(), JMeterUtils.getLocalHostName(),
+                        JMeterUtils.getLocalHostFullName());
+            }
             setProxy(parser);
 
             updateClassLoader();
             if (log.isDebugEnabled())
             {
                 String jcp=System.getProperty("java.class.path");// $NON-NLS-1$
                 String[] bits = jcp.split(File.pathSeparator);
                 log.debug("ClassPath");
                 for(String bit : bits){
                     log.debug(bit);
                 }
             }
 
             // Set some (hopefully!) useful properties
             long now=System.currentTimeMillis();
             JMeterUtils.setProperty("START.MS",Long.toString(now));// $NON-NLS-1$
             Date today=new Date(now); // so it agrees with above
             JMeterUtils.setProperty("START.YMD",new SimpleDateFormat("yyyyMMdd").format(today));// $NON-NLS-1$ $NON-NLS-2$
             JMeterUtils.setProperty("START.HMS",new SimpleDateFormat("HHmmss").format(today));// $NON-NLS-1$ $NON-NLS-2$
 
             if (parser.getArgumentById(VERSION_OPT) != null) {
                 displayAsciiArt();
             } else if (parser.getArgumentById(HELP_OPT) != null) {
                 displayAsciiArt();
                 System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));//NOSONAR $NON-NLS-1$
             } else if (parser.getArgumentById(OPTIONS_OPT) != null) {
                 displayAsciiArt();
                 System.out.println(CLUtil.describeOptions(options).toString());//NOSONAR
             } else if (parser.getArgumentById(SERVER_OPT) != null) {
                 // Start the server
                 try {
                     RemoteJMeterEngineImpl.startServer(JMeterUtils.getPropDefault("server_port", 0)); // $NON-NLS-1$
                     startOptionalServers();
                 } catch (Exception ex) {
                     System.err.println("Server failed to start: "+ex);//NOSONAR
                     log.error("Giving up, as server failed with:", ex);
                     throw ex;
                 }
             } else {
                 String testFile=null;
                 CLOption testFileOpt = parser.getArgumentById(TESTFILE_OPT);
                 if (testFileOpt != null){
                     testFile = testFileOpt.getArgument();
                     if (USE_LAST_JMX.equals(testFile)) {
                         testFile = LoadRecentProject.getRecentFile(0);// most recent
                     }
                 }
                 CLOption testReportOpt = parser.getArgumentById(REPORT_GENERATING_OPT);
                 if (testReportOpt != null) { // generate report from existing file
                     String reportFile = testReportOpt.getArgument();
                     extractAndSetReportOutputFolder(parser);
                     ReportGenerator generator = new ReportGenerator(reportFile, null);
                     generator.generate();
                 } else if (parser.getArgumentById(NONGUI_OPT) == null) { // not non-GUI => GUI
                     startGui(testFile);
                     startOptionalServers();
                 } else { // NON-GUI must be true
                     extractAndSetReportOutputFolder(parser);
                     
                     CLOption rem = parser.getArgumentById(REMOTE_OPT_PARAM);
                     if (rem == null) {
                         rem = parser.getArgumentById(REMOTE_OPT);
                     }
                     CLOption jtl = parser.getArgumentById(LOGFILE_OPT);
                     String jtlFile = null;
                     if (jtl != null) {
                         jtlFile = processLAST(jtl.getArgument(), ".jtl"); // $NON-NLS-1$
                     }
                     CLOption reportAtEndOpt = parser.getArgumentById(REPORT_AT_END_OPT);
                     if(reportAtEndOpt != null && jtlFile == null) {
                         throw new IllegalUserActionException(
                                 "Option -"+ ((char)REPORT_AT_END_OPT)+" requires -"+((char)LOGFILE_OPT )+ " option");
                     }
                     startNonGui(testFile, jtlFile, rem, reportAtEndOpt != null);
                     startOptionalServers();
                 }
             }
         } catch (IllegalUserActionException e) {// NOSONAR
             System.out.println("Incorrect Usage:"+e.getMessage());//NOSONAR
             System.out.println(CLUtil.describeOptions(options).toString());//NOSONAR
         } catch (Throwable e) { // NOSONAR
-            log.fatalError("An error occurred: ",e);
+            log.error("An error occurred: ", e);
             System.out.println("An error occurred: " + e.getMessage());//NOSONAR
             // FIXME Should we exit here ? If we are called by Maven or Jenkins
             System.exit(1);
         }
     }
 
     /**
      * Extract option JMeter#REPORT_OUTPUT_FOLDER_OPT and if defined sets property 
      * {@link JMeter#JMETER_REPORT_OUTPUT_DIR_PROPERTY} after checking folder can
      * be safely written to
      * @param parser {@link CLArgsParser}
      * @throws IllegalArgumentException
      */
     private void extractAndSetReportOutputFolder(CLArgsParser parser) {
         CLOption reportOutputFolderOpt = parser
                 .getArgumentById(REPORT_OUTPUT_FOLDER_OPT);
         if(reportOutputFolderOpt != null) {
             String reportOutputFolder = parser.getArgumentById(REPORT_OUTPUT_FOLDER_OPT).getArgument();
             File reportOutputFolderAsFile = new File(reportOutputFolder);
 
             JOrphanUtils.canSafelyWriteToFolder(reportOutputFolderAsFile);
-            log.info("Setting property '"+JMETER_REPORT_OUTPUT_DIR_PROPERTY+"' to:'"+reportOutputFolderAsFile.getAbsolutePath()+"'");
-            JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, 
-                    reportOutputFolderAsFile.getAbsolutePath());                        
+            final String reportOutputFolderAbsPath = reportOutputFolderAsFile.getAbsolutePath();
+            log.info("Setting property '{}' to:'{}'", JMETER_REPORT_OUTPUT_DIR_PROPERTY, reportOutputFolderAbsPath);
+            JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, reportOutputFolderAbsPath);
         }
     }
 
     /**
      * Displays as ASCII Art Apache JMeter version + Copyright notice
      */
     private void displayAsciiArt() {
         try (InputStream inputStream = JMeter.class.getResourceAsStream("jmeter_as_ascii_art.txt")) {
             if(inputStream != null) {
                 String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                 System.out.println(text);//NOSONAR
             }
         } catch (Exception e1) { //NOSONAR No logging here
             System.out.println(JMeterUtils.getJMeterCopyright());//NOSONAR
             System.out.println("Version " + JMeterUtils.getJMeterVersion());//NOSONAR
         }
     }
 
     // Update classloader if necessary
     private void updateClassLoader() throws MalformedURLException {
         updatePath("search_paths",";", true); //$NON-NLS-1$//$NON-NLS-2$
         updatePath("user.classpath",File.pathSeparator, true);//$NON-NLS-1$
         updatePath("plugin_dependency_paths",";", false);//$NON-NLS-1$
     }
 
     private void updatePath(String property, String sep, boolean cp) throws MalformedURLException {
         String userpath= JMeterUtils.getPropDefault(property,"");// $NON-NLS-1$
         if (userpath.length() <= 0) { 
             return; 
         }
-        log.info(property+"="+userpath); //$NON-NLS-1$
+        log.info("{}={}", property, userpath); //$NON-NLS-1$
         StringTokenizer tok = new StringTokenizer(userpath, sep);
         while(tok.hasMoreTokens()) {
             String path=tok.nextToken();
             File f=new File(path);
             if (!f.canRead() && !f.isDirectory()) {
-                log.warn("Can't read "+path);
+                log.warn("Can't read {}", path);
             } else {
                 if (cp) {
-                    log.info("Adding to classpath and loader: "+path);
+                    log.info("Adding to classpath and loader: {}", path);
                     NewDriver.addPath(path);
                 } else {
-                    log.info("Adding to loader: "+path);
+                    log.info("Adding to loader: {}", path);
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
-            log.info("Starting Beanshell server (" + bshport + "," + bshfile + ")");
+            log.info("Starting Beanshell server ({},{})", bshport, bshfile);
             Runnable t = new BeanShellServer(bshport, bshfile);
             t.run(); // NOSONAR we just evaluate some code here
         }
 
         // Should we run a beanshell script on startup?
         String bshinit = JMeterUtils.getProperty("beanshell.init.file");// $NON-NLS-1$
         if (bshinit != null){
-            log.info("Run Beanshell on file: "+bshinit);
+            log.info("Run Beanshell on file: {}", bshinit);
             try {
                 BeanShellInterpreter bsi = new BeanShellInterpreter();
                 bsi.source(bshinit);
             } catch (ClassNotFoundException e) {
-                log.warn("Could not start Beanshell: "+e.getLocalizedMessage());
+                if (log.isWarnEnabled()) {
+                    log.warn("Could not start Beanshell: {}", e.getLocalizedMessage());
+                }
             } catch (JMeterException e) {
-                log.warn("Could not process Beanshell file: "+e.getLocalizedMessage());
+                if (log.isWarnEnabled()) {
+                    log.warn("Could not process Beanshell file: {}", e.getLocalizedMessage());
+                }
             }
         }
 
         int mirrorPort=JMeterUtils.getPropDefault("mirror.server.port", 0);// $NON-NLS-1$
         if (mirrorPort > 0){
-            log.info("Starting Mirror server (" + mirrorPort + ")");
+            log.info("Starting Mirror server ({})", mirrorPort);
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
                 String u = parser.getArgumentById(PROXY_USERNAME).getArgument();
                 String p = parser.getArgumentById(PROXY_PASSWORD).getArgument();
                 Authenticator.setDefault(new ProxyAuthenticator(u, p));
-                log.info("Set Proxy login: " + u + "/" + p);
+                log.info("Set Proxy login: {}/{}", u, p);
                 jmeterProps.setProperty(HTTP_PROXY_USER, u);//for Httpclient
                 jmeterProps.setProperty(HTTP_PROXY_PASS, p);//for Httpclient
             } else {
                 String u = parser.getArgumentById(PROXY_USERNAME).getArgument();
                 Authenticator.setDefault(new ProxyAuthenticator(u, ""));
-                log.info("Set Proxy login: " + u);
+                log.info("Set Proxy login: {}", u);
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
-            log.info("Set http[s].proxyHost: " + h + " Port: " + p);
+            log.info("Set http[s].proxyHost: {} Port: {}", h, p);
         } else if (parser.getArgumentById(PROXY_HOST) != null || parser.getArgumentById(PROXY_PORT) != null) {
             throw new IllegalUserActionException(JMeterUtils.getResString("proxy_cl_error"));// $NON-NLS-1$
         }
 
         if (parser.getArgumentById(NONPROXY_HOSTS) != null) {
             String n = parser.getArgumentById(NONPROXY_HOSTS).getArgument();
             System.setProperty("http.nonProxyHosts",  n );// $NON-NLS-1$
             System.setProperty("https.nonProxyHosts", n );// $NON-NLS-1$
-            log.info("Set http[s].nonProxyHosts: "+n);
+            log.info("Set http[s].nonProxyHosts: {}", n);
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
             File file = JMeterUtils.findFile(userProp);
             if (file.canRead()){
                 try (FileInputStream fis = new FileInputStream(file)){
-                    log.info("Loading user properties from: "+file.getCanonicalPath());
+                    log.info("Loading user properties from: {}", file);
                     Properties tmp = new Properties();
                     tmp.load(fis);
                     jmeterProps.putAll(tmp);
                 } catch (IOException e) {
-                    log.warn("Error loading user property file: " + userProp, e);
+                    log.warn("Error loading user property file: {}", userProp, e);
                 }
             }
         }
 
         // Add local system properties, if the file is found
         String sysProp = JMeterUtils.getPropDefault("system.properties",""); //$NON-NLS-1$
         if (sysProp.length() > 0){
             File file = JMeterUtils.findFile(sysProp);
             if (file.canRead()) {
                 try (FileInputStream fis = new FileInputStream(file)){
-                    log.info("Loading system properties from: "+file.getCanonicalPath());
+                    log.info("Loading system properties from: {}", file);
                     System.getProperties().load(fis);
                 } catch (IOException e) {
-                    log.warn("Error loading system property file: " + sysProp, e);
+                    log.warn("Error loading system property file: {}", sysProp, e);
                 } 
             }
         }
 
         // Process command line property definitions
         // These can potentially occur multiple times
 
         List<CLOption> clOptions = parser.getArguments();
         for (CLOption option : clOptions) {
             String name = option.getArgument(0);
             String value = option.getArgument(1);
             
             switch (option.getDescriptor().getId()) {
 
             // Should not have any text arguments
             case CLOption.TEXT_ARGUMENT:
                 throw new IllegalArgumentException("Unknown arg: " + option.getArgument());
 
             case PROPFILE2_OPT: // Bug 33920 - allow multiple props
                 try (FileInputStream fis = new FileInputStream(new File(name))){
                     Properties tmp = new Properties();
                     tmp.load(fis);
                     jmeterProps.putAll(tmp);
                 } catch (FileNotFoundException e) { // NOSONAR
-                    log.warn("Can't find additional property file: " + name, e);
+                    log.warn("Can't find additional property file: {}", name, e);
                 } catch (IOException e) { // NOSONAR
-                    log.warn("Error loading additional property file: " + name, e);
+                    log.warn("Error loading additional property file: {}", name, e);
                 }
                 break;
             case SYSTEM_PROPFILE:
-                log.info("Setting System properties from file: " + name);
+                log.info("Setting System properties from file: {}", name);
                 try (FileInputStream fis = new FileInputStream(new File(name))){
                     System.getProperties().load(fis);
                 } catch (IOException e) { // NOSONAR
-                    log.warn("Cannot find system property file " + e.getLocalizedMessage());
+                    if (log.isWarnEnabled()) {
+                        log.warn("Cannot find system property file. {}", e.getLocalizedMessage());
+                    }
                 }
                 break;
             case SYSTEM_PROPERTY:
                 if (value.length() > 0) { // Set it
-                    log.info("Setting System property: " + name + "=" + value);
+                    log.info("Setting System property: {}={}", name, value);
                     System.getProperties().setProperty(name, value);
                 } else { // Reset it
-                    log.warn("Removing System property: " + name);
+                    log.warn("Removing System property: {}", name);
                     System.getProperties().remove(name);
                 }
                 break;
             case JMETER_PROPERTY:
                 if (value.length() > 0) { // Set it
-                    log.info("Setting JMeter property: " + name + "=" + value);
+                    log.info("Setting JMeter property: {}={}", name, value);
                     jmeterProps.setProperty(name, value);
                 } else { // Reset it
-                    log.warn("Removing JMeter property: " + name);
+                    log.warn("Removing JMeter property: {}", name);
                     jmeterProps.remove(name);
                 }
                 break;
             case JMETER_GLOBAL_PROP:
                 if (value.length() > 0) { // Set it
-                    log.info("Setting Global property: " + name + "=" + value);
+                    log.info("Setting Global property: {}={}", name, value);
                     remoteProps.setProperty(name, value);
                 } else {
                     File propFile = new File(name);
                     if (propFile.canRead()) {
-                        log.info("Setting Global properties from the file " + name);
+                        log.info("Setting Global properties from the file {}", name);
                         try (FileInputStream fis = new FileInputStream(propFile)){
                             remoteProps.load(fis);
                         } catch (FileNotFoundException e) { // NOSONAR
-                            log.warn("Could not find properties file: " + e.getLocalizedMessage());
+                            if (log.isWarnEnabled()) {
+                                log.warn("Could not find properties file: {}", e.getLocalizedMessage());
+                            }
                         } catch (IOException e) { // NOSONAR
-                            log.warn("Could not load properties file: " + e.getLocalizedMessage());
+                            if (log.isWarnEnabled()) {
+                                log.warn("Could not load properties file: {}", e.getLocalizedMessage());
+                            }
                         } 
                     }
                 }
                 break;
             case LOGLEVEL:
                 if (value.length() > 0) { // Set category
-                    log.info("LogLevel: " + name + "=" + value);
+                    log.info("LogLevel: {}={}", name, value);
                     final Level logLevel = Level.getLevel(value);
                     if (logLevel != null) {
                         String loggerName = name;
                         if (name.startsWith("jmeter") || name.startsWith("jorphan")) {
                             loggerName = PACKAGE_PREFIX + name;
                         }
                         Configurator.setAllLevels(loggerName, logLevel);
                     } else {
-                        log.warn("Invalid log level, '" + value + "' for '" + name + "'.");
+                        log.warn("Invalid log level, '{}' for '{}'.", value, name);
                     }
                 } else { // Set root level
-                    log.warn("LogLevel: " + name);
+                    log.warn("LogLevel: {}", name);
                     final Level logLevel = Level.getLevel(name);
                     if (logLevel != null) {
                         Configurator.setRootLevel(logLevel);
                     } else {
-                        log.warn("Invalid log level, '" + name + "' for the root logger.");
+                        log.warn("Invalid log level, '{}', for the root logger.", name);
                     }
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
 
         String sampleVariables = (String) jmeterProps.get(SampleEvent.SAMPLE_VARIABLES);
         if (sampleVariables != null){
             remoteProps.put(SampleEvent.SAMPLE_VARIABLES, sampleVariables);
         }
         jmeterProps.put("jmeter.version", JMeterUtils.getJMeterVersion());
     }
 
     /*
      * Checks for LAST or LASTsuffix.
      * Returns the LAST name with .JMX replaced by suffix.
      */
     private String processLAST(final String jmlogfile, final String suffix) {
         if (USE_LAST_JMX.equals(jmlogfile) || USE_LAST_JMX.concat(suffix).equals(jmlogfile)){
             String last = LoadRecentProject.getRecentFile(0);// most recent
             if (last.toUpperCase(Locale.ENGLISH).endsWith(JMX_SUFFIX)){
                 return last.substring(0, last.length() - JMX_SUFFIX.length()).concat(suffix);
             }
         }
         return jmlogfile;
     }
 
     private void startNonGui(String testFile, String logFile, CLOption remoteStart, boolean generateReportDashboard)
             throws IllegalUserActionException, ConfigurationException {
         // add a system property so samplers can check to see if JMeter
         // is running in NonGui mode
         System.setProperty(JMETER_NON_GUI, "true");// $NON-NLS-1$
         JMeter driver = new JMeter();// TODO - why does it create a new instance?
         driver.remoteProps = this.remoteProps;
         driver.remoteStop = this.remoteStop;
         PluginManager.install(this, false);
 
         String remoteHostsString = null;
         if (remoteStart != null) {
             remoteHostsString = remoteStart.getArgument();
             if (remoteHostsString == null) {
                 remoteHostsString = JMeterUtils.getPropDefault(
                         "remote_hosts", //$NON-NLS-1$
                         "127.0.0.1");//NOSONAR $NON-NLS-1$ 
             }
         }
         if (testFile == null) {
             throw new IllegalUserActionException("Non-GUI runs require a test plan");
         }
         driver.runNonGui(testFile, logFile, remoteStart != null, remoteHostsString, generateReportDashboard);
     }
 
     // run test in batch mode
     private void runNonGui(String testFile, String logFile, boolean remoteStart, String remoteHostsString, boolean generateReportDashboard) {
         try {
             File f = new File(testFile);
             if (!f.exists() || !f.isFile()) {
                 println("Could not open " + testFile);
                 return;
             }
             FileServer.getFileServer().setBaseForScript(f);
 
             HashTree tree = SaveService.loadTree(f);
 
             @SuppressWarnings("deprecation") // Deliberate use of deprecated ctor
             JMeterTreeModel treeModel = new JMeterTreeModel(new Object());// Create non-GUI version to avoid headless problems
             JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
             treeModel.addSubTree(tree, root);
 
             // Hack to resolve ModuleControllers in non GUI mode
             SearchByClass<ReplaceableController> replaceableControllers =
                     new SearchByClass<>(ReplaceableController.class);
             tree.traverse(replaceableControllers);
             Collection<ReplaceableController> replaceableControllersRes = replaceableControllers.getSearchResults();
             for (ReplaceableController replaceableController : replaceableControllersRes) {
                 replaceableController.resolveReplacementSubTree(root);
             }
 
             // Remove the disabled items
             // For GUI runs this is done in Start.java
             convertSubTree(tree);
 
             Summariser summer = null;
             String summariserName = JMeterUtils.getPropDefault("summariser.name", "");//$NON-NLS-1$
             if (summariserName.length() > 0) {
-                log.info("Creating summariser <" + summariserName + ">");
+                log.info("Creating summariser <{}>", summariserName);
                 println("Creating summariser <" + summariserName + ">");
                 summer = new Summariser(summariserName);
             }
             ReportGenerator reportGenerator = null;
             if (logFile != null) {
                 ResultCollector logger = new ResultCollector(summer);
                 logger.setFilename(logFile);
                 tree.add(tree.getArray()[0], logger);
                 if(generateReportDashboard) {
                     reportGenerator = new ReportGenerator(logFile, logger);
                 }
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
 
             List<JMeterEngine> engines = new LinkedList<>();
             tree.add(tree.getArray()[0], new ListenToTest(remoteStart && remoteStop ? engines : null, reportGenerator));
             println("Created the tree successfully using "+testFile);
             if (!remoteStart) {
                 JMeterEngine engine = new StandardJMeterEngine();
                 engine.configure(tree);
                 long now=System.currentTimeMillis();
                 println("Starting the test @ "+new Date(now)+" ("+now+")");
                 engine.runTest();
                 engines.add(engine);
             } else {
                 java.util.StringTokenizer st = new java.util.StringTokenizer(remoteHostsString, ",");//$NON-NLS-1$
                 List<String> hosts = new LinkedList<>();
                 while (st.hasMoreElements()) {
                     hosts.add((String) st.nextElement());
                 }
                 
                 DistributedRunner distributedRunner=new DistributedRunner(this.remoteProps);
                 distributedRunner.setStdout(System.out); // NOSONAR
                 distributedRunner.setStdErr(System.err); // NOSONAR
                 distributedRunner.init(hosts, tree);
                 engines.addAll(distributedRunner.getEngines());
                 distributedRunner.start();
             }
             startUdpDdaemon(engines);
         } catch (Exception e) {
             System.out.println("Error in NonGUIDriver " + e.toString());//NOSONAR
             log.error("Error in NonGUIDriver", e);
         }
     }
 
     /**
      * Remove disabled elements
      * Replace the ReplaceableController with the target subtree
      *
      * @param tree The {@link HashTree} to convert
      */
     public static void convertSubTree(HashTree tree) {
         LinkedList<Object> copyList = new LinkedList<>(tree.list());
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
         if ("org.apache.jmeter.control.ModuleController".equals(item.getClass().getName())){ // NOSONAR (comparison is intentional) Bug 47165
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
 
         private final List<JMeterEngine> engines;
 
         private final ReportGenerator reportGenerator;
 
         /**
          * @param engines List<JMeterEngine>
          * @param reportGenerator {@link ReportGenerator}
          */
         public ListenToTest(List<JMeterEngine> engines, ReportGenerator reportGenerator) {
             this.engines=engines;
             this.reportGenerator = reportGenerator;
         }
 
         @Override
         // N.B. this is called by a daemon RMI thread from the remote host
         public void testEnded(String host) {
-            long now=System.currentTimeMillis();
-            log.info("Finished remote host: " + host + " ("+now+")");
+            final long now=System.currentTimeMillis();
+            log.info("Finished remote host: {} ({})", host, now);
             if (started.decrementAndGet() <= 0) {
                 Thread stopSoon = new Thread(this);
                 // the calling thread is a daemon; this thread must not be
                 // see Bug 59391
                 stopSoon.setDaemon(false); 
                 stopSoon.start();
             }
         }
 
         @Override
         public void testEnded() {
             long now = System.currentTimeMillis();
             println("Tidying up ...    @ "+new Date(now)+" ("+now+")");
             try {
                 generateReport();
             } catch (Exception e) {
                 System.err.println("Error generating the report: "+e);//NOSONAR
                 log.error("Error generating the report",e);
             }
             checkForRemainingThreads();
             println("... end of run");
         }
 
         @Override
         public void testStarted(String host) {
             started.incrementAndGet();
-            long now=System.currentTimeMillis();
-            log.info("Started remote host:  " + host + " ("+now+")");
+            final long now=System.currentTimeMillis();
+            log.info("Started remote host:  {} ({})", host, now);
         }
 
         @Override
         public void testStarted() {
-            long now=System.currentTimeMillis();
-            log.info(JMeterUtils.getResString("running_test")+" ("+now+")");//$NON-NLS-1$
+            if (log.isInfoEnabled()) {
+                final long now = System.currentTimeMillis();
+                log.info("{} ({})", JMeterUtils.getResString("running_test"), now);//$NON-NLS-1$
+            }
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
                 println("Exiting remote servers");
                 for (JMeterEngine e : engines){
                     e.exit();
                 }
             }
             try {
                 TimeUnit.SECONDS.sleep(5); // Allow listeners to close files
             } catch (InterruptedException ignored) {
                 Thread.currentThread().interrupt();
             }
             ClientJMeterEngine.tidyRMI(log);
             try {
                 generateReport();
             } catch (Exception e) {
                 System.err.println("Error generating the report: "+e);//NOSONAR
                 log.error("Error generating the report",e);
             }
             checkForRemainingThreads();
             println("... end of run");
         }
 
         /**
          * Generate report
          */
         private void generateReport() {
             if(reportGenerator != null) {
                 try {
                     log.info("Generating Dashboard");
                     reportGenerator.generate();
                     log.info("Dashboard generated");
                 } catch (GenerationException ex) {
-                    log.error("Error generating dashboard:"+ex.getMessage(), ex);
+                    log.error("Error generating dashboard: {}", ex, ex);
                 }
             }
         }
 
         /**
          * Runs daemon thread which waits a short while; 
          * if JVM does not exit, lists remaining non-daemon threads on stdout.
          */
         private void checkForRemainingThreads() {
             // This cannot be a JMeter class variable, because properties
             // are not initialised until later.
             final int pauseToCheckForRemainingThreads = 
                     JMeterUtils.getPropDefault("jmeter.exit.check.pause", 2000); // $NON-NLS-1$ 
             
             if (pauseToCheckForRemainingThreads > 0) {
                 Thread daemon = new Thread(){
                     @Override
                     public void run(){
                         try {
                             TimeUnit.MILLISECONDS.sleep(pauseToCheckForRemainingThreads); // Allow enough time for JVM to exit
                         } catch (InterruptedException ignored) {
                             Thread.currentThread().interrupt();
                         }
                         // This is a daemon thread, which should only reach here if there are other
                         // non-daemon threads still active
                         System.out.println("The JVM should have exited but did not.");//NOSONAR
                         System.out.println("The following non-daemon threads are still running (DestroyJavaVM is OK):");//NOSONAR
                         JOrphanUtils.displayThreads(false);
                     }
     
                 };
                 daemon.setDaemon(true);
                 daemon.start();
-            } else if(pauseToCheckForRemainingThreads<=0 && log.isDebugEnabled()) {
+            } else if (pauseToCheckForRemainingThreads<=0) {
                 log.debug("jmeter.exit.check.pause is <= 0, JMeter won't check for unterminated non-daemon threads");
             }
         }
     }
 
     private static void println(String str) {
         System.out.println(str);//NOSONAR
     }
 
     @Override
     public String[][] getIconMappings() {
         final String defaultIconProp = "org/apache/jmeter/images/icon.properties"; //$NON-NLS-1$
         final String iconSize = JMeterUtils.getPropDefault(TREE_ICON_SIZE, DEFAULT_TREE_ICON_SIZE); 
         String iconProp = JMeterUtils.getPropDefault("jmeter.icons", defaultIconProp);//$NON-NLS-1$
         Properties p = JMeterUtils.loadProperties(iconProp);
         if (p == null && !iconProp.equals(defaultIconProp)) {
-            log.info(iconProp + " not found - using " + defaultIconProp);
+            log.info("{} not found - using {}", iconProp, defaultIconProp);
             iconProp = defaultIconProp;
             p = JMeterUtils.loadProperties(iconProp);
         }
         if (p == null) {
-            log.info(iconProp + " not found - using inbuilt icon set");
+            log.info("{} not found - using inbuilt icon set", iconProp);
             return DEFAULT_ICONS;
         }
-        log.info("Loaded icon properties from " + iconProp);
+        log.info("Loaded icon properties from {}", iconProp);
         String[][] iconlist = new String[p.size()][3];
         Enumeration<?> pe = p.keys();
         int i = 0;
         while (pe.hasMoreElements()) {
             String key = (String) pe.nextElement();
             String[] icons = JOrphanUtils.split(p.getProperty(key), " ");//$NON-NLS-1$
             iconlist[i][0] = key;
             iconlist[i][1] = icons[0].replace(KEY_SIZE, iconSize);
             if (icons.length > 1) {
                 iconlist[i][2] = icons[1].replace(KEY_SIZE, iconSize);
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
 
-    private void logProperty(String prop){
-        log.info(prop+"="+System.getProperty(prop));//$NON-NLS-1$
-    }
-    private void logProperty(String prop,String separator){
-        log.info(prop+separator+System.getProperty(prop));//$NON-NLS-1$
-    }
-
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
                 System.out.println("Failed to create UDP port");//NOSONAR
             }
         }
     }
 
     private static void waitForSignals(final List<JMeterEngine> engines, DatagramSocket socket) {
         byte[] buf = new byte[80];
         System.out.println("Waiting for possible Shutdown/StopTestNow/Heapdump message on port "+socket.getLocalPort());//NOSONAR
         DatagramPacket request = new DatagramPacket(buf, buf.length);
         try {
             while(true) {
                 socket.receive(request);
                 InetAddress address = request.getAddress();
                 // Only accept commands from the local host
                 if (address.isLoopbackAddress()){
                     String command = new String(request.getData(), request.getOffset(), request.getLength(),"ASCII");
                     System.out.println("Command: "+command+" received from "+address);//NOSONAR
-                    log.info("Command: "+command+" received from "+address);
+                    log.info("Command: {} received from {}", command, address);
                     switch(command) {
                         case "StopTestNow" :
                             for(JMeterEngine engine : engines) {
                                 engine.stopTest(true);
                             }
                             break;
                         case "Shutdown" :
                             for(JMeterEngine engine : engines) {
                                 engine.stopTest(false);
                             }
                             break;
                         case "HeapDump" :
                             HeapDumper.dumpHeap();
                             break;
                         default:
                             System.out.println("Command: "+command+" not recognised ");//NOSONAR                            
                     }
                 }
             }
         } catch (Exception e) {
             System.out.println(e);//NOSONAR
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
             } catch (SocketException e) { // NOSONAR
                 i++;
             }            
         }
 
         return socket;
     }
 }
diff --git a/src/core/org/apache/jmeter/control/GenericController.java b/src/core/org/apache/jmeter/control/GenericController.java
index c4fa6ed6a..0e00dddcb 100644
--- a/src/core/org/apache/jmeter/control/GenericController.java
+++ b/src/core/org/apache/jmeter/control/GenericController.java
@@ -1,418 +1,416 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.threads.TestCompilerHelper;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * <p>
  * This class is the basis for all the controllers.
  * It also implements SimpleController.
  * </p>
  * <p>
  * The main entry point is next(), which is called by JMeterThread as follows:
  * </p>
  * <p>
  * <code>while (running &amp;&amp; (sampler = controller.next()) != null)</code>
  * </p>
  */
 public class GenericController extends AbstractTestElement implements Controller, Serializable, TestCompilerHelper {
 
-    private static final long serialVersionUID = 234L;
+    private static final long serialVersionUID = 235L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(GenericController.class);
 
     private transient LinkedList<LoopIterationListener> iterationListeners = new LinkedList<>();
 
     // Only create the map if it is required
     private transient ConcurrentMap<TestElement, Object> children = new ConcurrentHashMap<>();
 
     private static final Object DUMMY = new Object();
 
     // May be replaced by RandomOrderController
     protected transient List<TestElement> subControllersAndSamplers = new ArrayList<>();
 
     /**
      * Index of current sub controller or sampler
      */
     protected transient int current;
 
     /**
      * TODO document this
      */
     private transient int iterCount;
     
     /**
      * Controller has ended
      */
     private transient boolean done;
     
     /**
      * First sampler or sub-controller
      */
     private transient boolean first;
 
     /**
      * Creates a Generic Controller
      */
     public GenericController() {
     }
 
     @Override
     public void initialize() {
         resetCurrent();
         resetIterCount();
         done = false; // TODO should this use setDone()?
         first = true; // TODO should this use setFirst()?        
         initializeSubControllers();
     }
 
     /**
      * (re)Initializes sub controllers
      * See Bug 50032
      */
     protected void initializeSubControllers() {
         for (TestElement te : subControllersAndSamplers) {
             if(te instanceof GenericController) {
                 ((Controller) te).initialize();
             }
         }
     }
 
     /**
      * Resets the controller (called after execution of last child of controller):
      * <ul>
      * <li>resetCurrent() (i.e. current=0)</li>
      * <li>increment iteration count</li>
      * <li>sets first=true</li>
      * <li>recoverRunningVersion() to set the controller back to the initial state</li>
      * </ul>
      *
      */
     protected void reInitialize() {
         resetCurrent();
         incrementIterCount();
         setFirst(true);
         recoverRunningVersion();
     }
 
     /**
      * <p>
      * Determines the next sampler to be processed.
      * </p>
      *
      * <p>
      * If {@link #isDone()} is <code>true</code>, returns null.
      * </p>
      *
      * <p>
      * Gets the list element using current pointer.
      * If this is <code>null</code>, calls {@link #nextIsNull()}.
      * </p>
      *
      * <p>
      * If the list element is a {@link Sampler}, calls {@link #nextIsASampler(Sampler)},
      * otherwise calls {@link #nextIsAController(Controller)}
      * </p>
      *
      * <p>
      * If any of the called methods throws {@link NextIsNullException}, returns <code>null</code>,
      * otherwise the value obtained above is returned.
      * </p>
      *
      * @return the next sampler or <code>null</code>
      */
     @Override
     public Sampler next() {
         fireIterEvents();
-        if (log.isDebugEnabled()) {
-            log.debug("Calling next on: " + this.getClass().getName());
-        }
+        log.debug("Calling next on: {}", GenericController.class);
         if (isDone()) {
             return null;
         }
         Sampler returnValue = null;
         try {
             TestElement currentElement = getCurrentElement();
             setCurrentElement(currentElement);
             if (currentElement == null) {
                 returnValue = nextIsNull();
             } else {
                 if (currentElement instanceof Sampler) {
                     returnValue = nextIsASampler((Sampler) currentElement);
                 } else { // must be a controller
                     returnValue = nextIsAController((Controller) currentElement);
                 }
             }
         } catch (NextIsNullException e) {
             // NOOP
         }
         return returnValue;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#isDone()
      */
     @Override
     public boolean isDone() {
         return done;
     }
 
     protected void setDone(boolean done) {
         this.done = done;
     }
 
     /**
      * @return true if it's the controller is returning the first of its children
      */
     protected boolean isFirst() {
         return first;
     }
 
     /**
      * If b is true, it means first is reset which means Controller has executed all its children 
      * @param b The flag, whether first is reseted
      */
     public void setFirst(boolean b) {
         first = b;
     }
 
     /**
      * Called by {@link #next()} if the element is a Controller, and returns the
      * next sampler from the controller. If this is <code>null</code>, then
      * updates the current pointer and makes recursive call to {@link #next()}.
      * 
      * @param controller the current <em>next</em> element
      * @return the next sampler
      * @throws NextIsNullException when the end of the list has already been reached
      */
     protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
         Sampler sampler = controller.next();
         if (sampler == null) {
             currentReturnedNull(controller);
             sampler = next();
         }
         return sampler;
     }
 
     /**
      * Increment the current pointer and return the element. Called by
      * {@link #next()} if the element is a sampler. (May be overriden by
      * sub-classes).
      *
      * @param element
      *            the current <em>next</em> element
      * @return input element
      * @throws NextIsNullException when the end of the list has already been reached
      */
     protected Sampler nextIsASampler(Sampler element) throws NextIsNullException {
         incrementCurrent();
         return element;
     }
 
     /**
      * Called by {@link #next()} when {@link #getCurrentElement()} returns <code>null</code>.
      * Reinitialises the controller.
      *
      * @return null (always, for this class)
      * @throws NextIsNullException when the end of the list has already been reached
      */
     protected Sampler nextIsNull() throws NextIsNullException {
         reInitialize();
         return null;
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void triggerEndOfLoop() {
         reInitialize();
     }
     
     /**
      * If the controller is done, remove it from the list,
      * otherwise increment to next entry in list.
      *
      * @param c controller
      */
     protected void currentReturnedNull(Controller c) {
         if (c.isDone()) {
             removeCurrentElement();
         } else {
             incrementCurrent();
         }
     }
 
     /**
      * Gets the SubControllers attribute of the GenericController object
      *
      * @return the SubControllers value
      */
     protected List<TestElement> getSubControllers() {
         return subControllersAndSamplers;
     }
 
     private void addElement(TestElement child) {
         subControllersAndSamplers.add(child);
     }
 
     /**
      * Empty implementation - does nothing.
      *
      * @param currentElement
      *            the current element
      * @throws NextIsNullException
      *             when the list has been completed already
      */
     protected void setCurrentElement(TestElement currentElement) throws NextIsNullException {
     }
 
     /**
      * <p>
      * Gets the element indicated by the <code>current</code> index, if one exists,
      * from the <code>subControllersAndSamplers</code> list.
      * </p>
      * <p>
      * If the <code>subControllersAndSamplers</code> list is empty,
      * then set done = true, and throw NextIsNullException.
      * </p>
      * @return the current element - or null if current index too large
      * @throws NextIsNullException if list is empty
      */
     protected TestElement getCurrentElement() throws NextIsNullException {
         if (current < subControllersAndSamplers.size()) {
             return subControllersAndSamplers.get(current);
         }
         if (subControllersAndSamplers.size() == 0) {
             setDone(true);
             throw new NextIsNullException();
         }
         return null;
     }
 
     protected void removeCurrentElement() {
         subControllersAndSamplers.remove(current);
     }
 
     /**
      * Increments the current pointer; called by currentReturnedNull to move the
      * controller on to its next child.
      */
     protected void incrementCurrent() {
         current++;
     }
 
     protected void resetCurrent() {
         current = 0;
     }
 
     @Override
     public void addTestElement(TestElement child) {
         if (child instanceof Controller || child instanceof Sampler) {
             addElement(child);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public final boolean addTestElementOnce(TestElement child){
         if (children.putIfAbsent(child, DUMMY) == null) {
             addTestElement(child);
             return true;
         }
         return false;
     }
 
     @Override
     public void addIterationListener(LoopIterationListener lis) {
         /*
          * A little hack - add each listener to the start of the list - this
          * ensures that the thread running the show is the first listener and
          * can modify certain values before other listeners are called.
          */
         iterationListeners.addFirst(lis);
     }
     
     /**
      * Remove listener
      */
     @Override
     public void removeIterationListener(LoopIterationListener iterationListener) {
         for (Iterator<LoopIterationListener> iterator = iterationListeners.iterator(); iterator.hasNext();) {
             LoopIterationListener listener = iterator.next();
             if(listener == iterationListener)
             {
                 iterator.remove();
                 break; // can only match once
             }
         }
     }
 
     protected void fireIterEvents() {
         if (isFirst()) {
             fireIterationStart();
             first = false; // TODO - should this use setFirst() ?
         }
     }
 
     protected void fireIterationStart() {
         LoopIterationEvent event = new LoopIterationEvent(this, getIterCount());
         for (LoopIterationListener item : iterationListeners) {
             item.iterationStart(event);
         }
     }
 
     protected int getIterCount() {
         return iterCount;
     }
 
     protected void incrementIterCount() {
         iterCount++;
     }
 
     protected void resetIterCount() {
         iterCount = 0;
     }
     
     protected Object readResolve(){
         iterationListeners = new LinkedList<>();
         children = new ConcurrentHashMap<>();
         subControllersAndSamplers = new ArrayList<>();
 
         return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/control/IfController.java b/src/core/org/apache/jmeter/control/IfController.java
index cea9dafd7..e6852edda 100644
--- a/src/core/org/apache/jmeter/control/IfController.java
+++ b/src/core/org/apache/jmeter/control/IfController.java
@@ -1,290 +1,286 @@
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
 
 import java.io.Serializable;
 
 import javax.script.ScriptContext;
 import javax.script.ScriptEngine;
 import javax.script.ScriptEngineManager;
 import javax.script.SimpleScriptContext;
 
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.mozilla.javascript.Context;
 import org.mozilla.javascript.Scriptable;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  *
  *
  * This is a Conditional Controller; it will execute the set of statements
  * (samplers/controllers, etc) while the 'condition' is true.
  * <p>
  * In a programming world - this is equivalent of :
  * <pre>
  * if (condition) {
  *          statements ....
  *          }
  * </pre>
  * In JMeter you may have :
  * <pre> 
  * Thread-Group (set to loop a number of times or indefinitely,
  *    ... Samplers ... (e.g. Counter )
  *    ... Other Controllers ....
  *    ... IfController ( condition set to something like - ${counter} &lt; 10)
  *       ... statements to perform if condition is true
  *       ...
  *    ... Other Controllers /Samplers }
  * </pre>
  */
 
 // for unit test code @see TestIfController
 
 public class IfController extends GenericController implements Serializable, ThreadListener {
 
-    private static final Logger logger = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(IfController.class);
 
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
 
     private static final String NASHORN_ENGINE_NAME = "nashorn"; //$NON-NLS-1$
 
     private static final String CONDITION = "IfController.condition"; //$NON-NLS-1$
 
     private static final String EVALUATE_ALL = "IfController.evaluateAll"; //$NON-NLS-1$
 
     private static final String USE_EXPRESSION = "IfController.useExpression"; //$NON-NLS-1$
     
     private static final String USE_RHINO_ENGINE_PROPERTY = "javascript.use_rhino"; //$NON-NLS-1$
 
     private static final boolean USE_RHINO_ENGINE = 
             JMeterUtils.getPropDefault(USE_RHINO_ENGINE_PROPERTY, false) ||
             getInstance().getEngineByName(NASHORN_ENGINE_NAME) == null;
 
     
     private static final ThreadLocal<ScriptEngine> NASHORN_ENGINE = new ThreadLocal<ScriptEngine>() {
 
         @Override
         protected ScriptEngine initialValue() {
             return getInstance().getEngineByName("nashorn");//$NON-NLS-N$
         }
     
     };
 
     private interface JsEvaluator {
         boolean evaluate(String testElementName, String condition);
     }
     
     private static class RhinoJsEngine implements JsEvaluator {
         @Override
         public boolean evaluate(String testElementName, String condition) {
             boolean result = false;
             // now evaluate the condition using JavaScript
             Context cx = Context.enter();
             try {
                 Scriptable scope = cx.initStandardObjects(null);
                 Object cxResultObject = cx.evaluateString(scope, condition
                 /** * conditionString ** */
                 , "<cmd>", 1, null);
                 result = computeResultFromString(condition, Context.toString(cxResultObject));
             } catch (Exception e) {
-                logger.error(testElementName+": error while processing "+ "[" + condition + "]\n", e);
+                log.error("{}: error while processing "+ "[{}]", testElementName, condition, e);
             } finally {
                 Context.exit();
             }
             return result;
         }
     }
     
     private static class NashornJsEngine implements JsEvaluator {
         @Override
         public boolean evaluate(String testElementName, String condition) {
             try {
                 ScriptContext newContext = new SimpleScriptContext();
                 newContext.setBindings(NASHORN_ENGINE.get().createBindings(), ScriptContext.ENGINE_SCOPE);
                 Object o = NASHORN_ENGINE.get().eval(condition, newContext);
                 return computeResultFromString(condition, o.toString());
             } catch (Exception ex) {
-                logger.error(testElementName+": error while processing "+ "[" + condition + "]\n", ex);
+                log.error("{}: error while processing [{}]", testElementName, condition, ex);
             }
             return false;
         }
     }
         
     private static JsEvaluator JAVASCRIPT_EVALUATOR = USE_RHINO_ENGINE ? new RhinoJsEngine() : new NashornJsEngine();
     
     /**
      * Initialization On Demand Holder pattern
      */
     private static class LazyHolder {
         public static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
     }
  
     /**
      * @return ScriptEngineManager singleton
      */
     private static ScriptEngineManager getInstance() {
             return LazyHolder.INSTANCE;
     }
     /**
      * constructor
      */
     public IfController() {
         super();
     }
 
     /**
      * constructor
      * @param condition The condition for this controller
      */
     public IfController(String condition) {
         super();
         this.setCondition(condition);
     }
 
     /**
      * Condition Accessor - this is gonna be like <code>${count} &lt; 10</code>
      * @param condition The condition for this controller
      */
     public void setCondition(String condition) {
         setProperty(new StringProperty(CONDITION, condition));
     }
 
     /**
      * Condition Accessor - this is gonna be like <code>${count} &lt; 10</code>
      * @return the condition associated with this controller
      */
     public String getCondition() {
         return getPropertyAsString(CONDITION);
     }
 
     /**
      * evaluate the condition clause log error if bad condition
      */
     private boolean evaluateCondition(String cond) {
-        if(logger.isDebugEnabled()) {
-            logger.debug("    getCondition() : [" + cond + "]");
-        }
+        log.debug("    getCondition() : [{}]", cond);
         return JAVASCRIPT_EVALUATOR.evaluate(getName(), cond);
     }
 
     /**
      * @param condition
      * @param resultStr
      * @return boolean
      * @throws Exception
      */
     private static boolean computeResultFromString(
             String condition, String resultStr) throws Exception {
         boolean result;
         switch(resultStr) {
             case "false":
                 result = false;
                 break;
             case "true":
                 result = true;
                 break;
             default:
                 throw new Exception(" BAD CONDITION :: " + condition + " :: expected true or false");
         }
-        if (logger.isDebugEnabled()) {
-            logger.debug("    >> evaluate Condition -  [ " + condition + "] results is  [" + result + "]");
-        }
+        log.debug("    >> evaluate Condition -  [{}] results is  [{}]", condition, result);
         return result;
     }
     
     
     private static boolean evaluateExpression(String cond) {
         return cond.equalsIgnoreCase("true"); // $NON-NLS-1$
     }
 
     @Override
     public boolean isDone() {
         // bug 26672 : the isDone result should always be false and not based on the expession evaluation
         // if an IfController ever gets evaluated to false it gets removed from the test tree. 
         // The problem is that the condition might get evaluated to true the next iteration, 
         // which we don't get the opportunity for
         return false;
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next() {
         // We should only evalute the condition if it is the first
         // time ( first "iteration" ) we are called.
         // For subsequent calls, we are inside the IfControllerGroup,
         // so then we just pass the control to the next item inside the if control
         boolean result = true;
         if(isEvaluateAll() || isFirst()) {
             result = isUseExpression() ? 
                     evaluateExpression(getCondition())
                     :
                     evaluateCondition(getCondition());
         }
 
         if (result) {
             return super.next();
         }
         // If-test is false, need to re-initialize indexes
         try {
             initializeSubControllers();
             return nextIsNull();
         } catch (NextIsNullException e1) {
             return null;
         }
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void triggerEndOfLoop() {
         super.initializeSubControllers();
         super.triggerEndOfLoop();
     }
 
     public boolean isEvaluateAll() {
         return getPropertyAsBoolean(EVALUATE_ALL,false);
     }
 
     public void setEvaluateAll(boolean b) {
         setProperty(EVALUATE_ALL,b);
     }
 
     public boolean isUseExpression() {
         return getPropertyAsBoolean(USE_EXPRESSION, false);
     }
 
     public void setUseExpression(boolean selected) {
         setProperty(USE_EXPRESSION, selected, false);
     }
     
     @Override
     public void threadStarted() {}
     
     @Override
     public void threadFinished() {
        NASHORN_ENGINE.remove();
     }
 }
diff --git a/src/core/org/apache/jmeter/control/TransactionController.java b/src/core/org/apache/jmeter/control/TransactionController.java
index 895900477..dcc94e551 100644
--- a/src/core/org/apache/jmeter/control/TransactionController.java
+++ b/src/core/org/apache/jmeter/control/TransactionController.java
@@ -1,344 +1,344 @@
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
 
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterThread;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.threads.ListenerNotifier;
 import org.apache.jmeter.threads.SamplePackage;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Transaction Controller to measure transaction times
  *
  * There are two different modes for the controller:
  * - generate additional total sample after nested samples (as in JMeter 2.2)
  * - generate parent sampler containing the nested samples
  *
  */
 public class TransactionController extends GenericController implements SampleListener, Controller, Serializable {
     /**
      * Used to identify Transaction Controller Parent Sampler
      */
     static final String NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX = "Number of samples in transaction : ";
 
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
     
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     private static final String GENERATE_PARENT_SAMPLE = "TransactionController.parent";// $NON-NLS-1$
 
     private static final String INCLUDE_TIMERS = "TransactionController.includeTimers";// $NON-NLS-1$
     
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
 
     private static final boolean DEFAULT_VALUE_FOR_INCLUDE_TIMERS = true; // default true for compatibility
 
     /**
      * Only used in parent Mode
      */
     private transient TransactionSampler transactionSampler;
     
     /**
      * Only used in NON parent Mode
      */
     private transient ListenerNotifier lnf;
 
     /**
      * Only used in NON parent Mode
      */
     private transient SampleResult res;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int calls;
     
     /**
      * Only used in NON parent Mode
      */
     private transient int noFailingSamples;
 
     /**
      * Cumulated pause time to excluse timer and post/pre processor times
      * Only used in NON parent Mode
      */
     private transient long pauseTime;
 
     /**
      * Previous end time
      * Only used in NON parent Mode
      */
     private transient long prevEndTime;
 
     /**
      * Creates a Transaction Controller
      */
     public TransactionController() {
         lnf = new ListenerNotifier();
     }
 
     @Override
     protected Object readResolve(){
         super.readResolve();
         lnf = new ListenerNotifier();
         return this;
     }
 
     /**
      * @param generateParent flag whether a parent sample should be generated.
      */
     public void setGenerateParentSample(boolean generateParent) {
         setProperty(new BooleanProperty(GENERATE_PARENT_SAMPLE, generateParent));
     }
 
     /**
      * @return {@code true} if a parent sample will be generated
      */
     public boolean isGenerateParentSample() {
         return getPropertyAsBoolean(GENERATE_PARENT_SAMPLE);
     }
 
     /**
      * @see org.apache.jmeter.control.Controller#next()
      */
     @Override
     public Sampler next(){
         if (isGenerateParentSample()){
             return nextWithTransactionSampler();
         }
         return nextWithoutTransactionSampler();
     }
 
 ///////////////// Transaction Controller - parent ////////////////
 
     private Sampler nextWithTransactionSampler() {
         // Check if transaction is done
         if(transactionSampler != null && transactionSampler.isTransactionDone()) {
             if (log.isDebugEnabled()) {
-                log.debug("End of transaction " + getName());
+                log.debug("End of transaction {}", getName());
             }
             // This transaction is done
             transactionSampler = null;
             return null;
         }
 
         // Check if it is the start of a new transaction
         if (isFirst()) // must be the start of the subtree
         {
             if (log.isDebugEnabled()) {
-                log.debug("Start of transaction " + getName());
+                log.debug("Start of transaction {}", getName());
             }
             transactionSampler = new TransactionSampler(this, getName());
         }
 
         // Sample the children of the transaction
         Sampler subSampler = super.next();
         transactionSampler.setSubSampler(subSampler);
         // If we do not get any sub samplers, the transaction is done
         if (subSampler == null) {
             transactionSampler.setTransactionDone();
         }
         return transactionSampler;
     }
 
     @Override
     protected Sampler nextIsAController(Controller controller) throws NextIsNullException {
         if (!isGenerateParentSample()) {
             return super.nextIsAController(controller);
         }
         Sampler returnValue;
         Sampler sampler = controller.next();
         if (sampler == null) {
             currentReturnedNull(controller);
             // We need to call the super.next, instead of this.next, which is done in GenericController,
             // because if we call this.next(), it will return the TransactionSampler, and we do not want that.
             // We need to get the next real sampler or controller
             returnValue = super.next();
         } else {
             returnValue = sampler;
         }
         return returnValue;
     }
 
 ////////////////////// Transaction Controller - additional sample //////////////////////////////
 
     private Sampler nextWithoutTransactionSampler() {
         if (isFirst()) // must be the start of the subtree
         {
             calls = 0;
             noFailingSamples = 0;
             res = new SampleResult();
             res.setSampleLabel(getName());
             // Assume success
             res.setSuccessful(true);
             res.sampleStart();
             prevEndTime = res.getStartTime();//???
             pauseTime = 0;
         }
         boolean isLast = current==super.subControllersAndSamplers.size();
         Sampler returnValue = super.next();
         if (returnValue == null && isLast) // Must be the end of the controller
         {
             if (res != null) {
                 // See BUG 55816
                 if (!isIncludeTimers()) {
                     long processingTimeOfLastChild = res.currentTimeInMillis() - prevEndTime;
                     pauseTime += processingTimeOfLastChild;
                 }
                 res.setIdleTime(pauseTime+res.getIdleTime());
                 res.sampleEnd();
                 res.setResponseMessage(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX + calls + ", number of failing samples : " + noFailingSamples);
                 if(res.isSuccessful()) {
                     res.setResponseCodeOK();
                 }
                 notifyListeners();
             }
         }
         else {
             // We have sampled one of our children
             calls++;
         }
 
         return returnValue;
     }
     
     /**
      * @param res {@link SampleResult}
      * @return true if res is the ParentSampler transactions
      */
     public static boolean isFromTransactionController(SampleResult res) {
         return res.getResponseMessage() != null && 
                 res.getResponseMessage().startsWith(
                         TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX);
     }
 
     /**
      * @see org.apache.jmeter.control.GenericController#triggerEndOfLoop()
      */
     @Override
     public void triggerEndOfLoop() {
         if(!isGenerateParentSample()) {
             if (res != null) {
                 res.setIdleTime(pauseTime + res.getIdleTime());
                 res.sampleEnd();
                 res.setSuccessful(TRUE.equals(JMeterContextService.getContext().getVariables().get(JMeterThread.LAST_SAMPLE_OK)));
                 res.setResponseMessage(TransactionController.NUMBER_OF_SAMPLES_IN_TRANSACTION_PREFIX + calls + ", number of failing samples : " + noFailingSamples);
                 notifyListeners();
             }
         } else {
             Sampler subSampler = transactionSampler.getSubSampler();
             // See Bug 56811
             // triggerEndOfLoop is called when error occurs to end Main Loop
             // in this case normal workflow doesn't happen, so we need 
             // to notify the childs of TransactionController and 
             // update them with SubSamplerResult
             if(subSampler instanceof TransactionSampler) {
                 TransactionSampler tc = (TransactionSampler) subSampler;
                 transactionSampler.addSubSamplerResult(tc.getTransactionResult());
             }
             transactionSampler.setTransactionDone();
             // This transaction is done
             transactionSampler = null;
         }
         super.triggerEndOfLoop();
     }
 
     /**
      * Create additional SampleEvent in NON Parent Mode
      */
     protected void notifyListeners() {
         // TODO could these be done earlier (or just once?)
         JMeterContext threadContext = getThreadContext();
         JMeterVariables threadVars = threadContext.getVariables();
         SamplePackage pack = (SamplePackage) threadVars.getObject(JMeterThread.PACKAGE_OBJECT);
         if (pack == null) {
             // If child of TransactionController is a ThroughputController and TPC does
             // not sample its children, then we will have this
             // TODO Should this be at warn level ?
             log.warn("Could not fetch SamplePackage");
         } else {
             SampleEvent event = new SampleEvent(res, threadContext.getThreadGroup().getName(),threadVars, true);
             // We must set res to null now, before sending the event for the transaction,
             // so that we can ignore that event in our sampleOccured method
             res = null;
             lnf.notifyListeners(event, pack.getSampleListeners());
         }
     }
 
     @Override
     public void sampleOccurred(SampleEvent se) {
         if (!isGenerateParentSample()) {
             // Check if we are still sampling our children
             if(res != null && !se.isTransactionSampleEvent()) {
                 SampleResult sampleResult = se.getResult();
                 res.setThreadName(sampleResult.getThreadName());
                 res.setBytes(res.getBytesAsLong() + sampleResult.getBytesAsLong());
                 res.setSentBytes(res.getSentBytes() + sampleResult.getSentBytes());
                 if (!isIncludeTimers()) {// Accumulate waiting time for later
                     pauseTime += sampleResult.getEndTime() - sampleResult.getTime() - prevEndTime;
                     prevEndTime = sampleResult.getEndTime();
                 }
                 if(!sampleResult.isSuccessful()) {
                     res.setSuccessful(false);
                     noFailingSamples++;
                 }
                 res.setAllThreads(sampleResult.getAllThreads());
                 res.setGroupThreads(sampleResult.getGroupThreads());
                 res.setLatency(res.getLatency() + sampleResult.getLatency());
                 res.setConnectTime(res.getConnectTime() + sampleResult.getConnectTime());
             }
         }
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
     }
 
     /**
      * Whether to include timers and pre/post processor time in overall sample.
      * @param includeTimers Flag whether timers and pre/post processor should be included in overall sample
      */
     public void setIncludeTimers(boolean includeTimers) {
         setProperty(INCLUDE_TIMERS, includeTimers, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 
     /**
      * Whether to include timer and pre/post processor time in overall sample.
      *
      * @return boolean (defaults to true for backwards compatibility)
      */
     public boolean isIncludeTimers() {
         return getPropertyAsBoolean(INCLUDE_TIMERS, DEFAULT_VALUE_FOR_INCLUDE_TIMERS);
     }
 }
diff --git a/src/core/org/apache/jmeter/control/WhileController.java b/src/core/org/apache/jmeter/control/WhileController.java
index f980c946d..d54e8ecf1 100644
--- a/src/core/org/apache/jmeter/control/WhileController.java
+++ b/src/core/org/apache/jmeter/control/WhileController.java
@@ -1,132 +1,126 @@
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
 
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterThread;
 import org.apache.jmeter.threads.JMeterVariables;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 // @see TestWhileController for unit tests
 
 public class WhileController extends GenericController implements Serializable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(WhileController.class);
 
-    private static final long serialVersionUID = 232L;
+    private static final long serialVersionUID = 233L;
 
     private static final String CONDITION = "WhileController.condition"; // $NON-NLS-1$
 
     public WhileController() {
     }
 
     /*
      * Evaluate the condition, which can be:
      * blank or LAST = was the last sampler OK?
      * otherwise, evaluate the condition to see if it is not "false"
      * If blank, only evaluate at the end of the loop
      *
      * Must only be called at start and end of loop
      *
      * @param loopEnd - are we at loop end?
      * @return true means OK to continue
      */
     private boolean endOfLoop(boolean loopEnd) {
         String cnd = getCondition().trim();
-        if(log.isDebugEnabled()) {
-            log.debug("Condition string:" + cnd+".");
-        }
+        log.debug("Condition string: '{}'", cnd);
         boolean res;
         // If blank, only check previous sample when at end of loop
         if ((loopEnd && cnd.length() == 0) || "LAST".equalsIgnoreCase(cnd)) {// $NON-NLS-1$
             JMeterVariables threadVars = JMeterContextService.getContext().getVariables();
             res = "false".equalsIgnoreCase(threadVars.get(JMeterThread.LAST_SAMPLE_OK));// $NON-NLS-1$
         } else {
             // cnd may be null if next() called us
             res = "false".equalsIgnoreCase(cnd);// $NON-NLS-1$
         }
-        if(log.isDebugEnabled()) {
-            log.debug("Condition value: " + res);
-        }
+        log.debug("Condition value: '{}'", res);
         return res;
     }
 
     /**
      * Only called at End of Loop
      * <p>
      * {@inheritDoc}
      */
     @Override
     protected Sampler nextIsNull() throws NextIsNullException {
         reInitialize();
         if (endOfLoop(true)){
             return null;
         }
         return next();
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void triggerEndOfLoop() {
         super.triggerEndOfLoop();
         endOfLoop(true);
     }
 
     /**
      * This skips controller entirely if the condition is false on first entry.
      * <p>
      * {@inheritDoc}
      */
     @Override
     public Sampler next(){
         if (isFirst()){
             if (endOfLoop(false)){
                 return null;
             }
         }
         return super.next();
     }
 
     /**
      * @param string
      *            the condition to save
      */
     public void setCondition(String string) {
-        if(log.isDebugEnabled()) {
-            log.debug("setCondition(" + string + ")");
-        }
+        log.debug("setCondition({})", string);
         setProperty(new StringProperty(CONDITION, string));
     }
 
     /**
      * @return the condition
      */
     public String getCondition() {
         JMeterProperty prop=getProperty(CONDITION);
         prop.recoverRunningVersion(this);
         return prop.getStringValue();
     }
 }
diff --git a/src/core/org/apache/jmeter/engine/ClientJMeterEngine.java b/src/core/org/apache/jmeter/engine/ClientJMeterEngine.java
index 37f913ad0..3e0c2d09a 100644
--- a/src/core/org/apache/jmeter/engine/ClientJMeterEngine.java
+++ b/src/core/org/apache/jmeter/engine/ClientJMeterEngine.java
@@ -1,204 +1,204 @@
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
 
 package org.apache.jmeter.engine;
 
 import java.io.File;
 import java.net.MalformedURLException;
 import java.rmi.Naming;
 import java.rmi.NotBoundException;
 import java.rmi.Remote;
 import java.rmi.RemoteException;
 import java.rmi.server.RemoteObject;
 import java.util.Properties;
 
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Class to run remote tests from the client JMeter and collect remote samples
  */
 public class ClientJMeterEngine implements JMeterEngine {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ClientJMeterEngine.class);
 
     private static final Object LOCK = new Object();
 
     private RemoteJMeterEngine remote;
 
     private HashTree test;
 
     private final String host;
 
     private static RemoteJMeterEngine getEngine(String h) throws MalformedURLException, RemoteException,
             NotBoundException {
        final String name = "//" + h + "/" + RemoteJMeterEngineImpl.JMETER_ENGINE_RMI_NAME; // $NON-NLS-1$ $NON-NLS-2$
        Remote remobj = Naming.lookup(name);
        if (remobj instanceof RemoteJMeterEngine){
            final RemoteJMeterEngine rje = (RemoteJMeterEngine) remobj;
            if (remobj instanceof RemoteObject){
                RemoteObject robj = (RemoteObject) remobj;
                System.out.println("Using remote object: "+robj.getRef().remoteToString());
            }
            return rje;
        }
        throw new RemoteException("Could not find "+name);
     }
 
     public ClientJMeterEngine(String host) throws MalformedURLException, NotBoundException, RemoteException {
         this.remote = getEngine(host);
         this.host = host;
     }
 
     /** {@inheritDoc} */
     @Override
     public void configure(HashTree testTree) {
         TreeCloner cloner = new TreeCloner(false);
         testTree.traverse(cloner);
         test = cloner.getClonedTree();
     }
 
     /** {@inheritDoc} */
     @Override
     public void stopTest(boolean now) {
         log.info("about to "+(now ? "stop" : "shutdown")+" remote test on "+host);
         try {
             remote.rstopTest(now);
         } catch (Exception ex) {
             log.error("", ex); // $NON-NLS-1$
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void reset() {
         try {
             try {
                 remote.rreset();
             } catch (java.rmi.ConnectException e) {
                 log.info("Retry reset after: "+e);
                 remote = getEngine(host);
                 remote.rreset();
             }
         } catch (Exception ex) {
             log.error("Failed to reset remote engine", ex); // $NON-NLS-1$
         }
     }
 
     @Override
     public void runTest() throws JMeterEngineException {
         log.info("running clientengine run method");
         
         // See https://bz.apache.org/bugzilla/show_bug.cgi?id=55510
         JMeterContextService.clearTotalThreads();
         HashTree testTree = test;
 
         synchronized(testTree) {
             testTree.traverse(new PreCompiler(true));  // limit the changes to client only test elements
             testTree.traverse(new TurnElementsOn());
             testTree.traverse(new ConvertListeners());
         }
 
         String methodName="unknown";
         try {
             JMeterContextService.startTest();
             /*
              * Add fix for Deadlocks, see:
              * 
              * See https://bz.apache.org/bugzilla/show_bug.cgi?id=48350
             */
             File baseDirRelative = FileServer.getFileServer().getBaseDirRelative();
             String scriptName = FileServer.getFileServer().getScriptName();
             synchronized(LOCK)
             {
                 methodName="rconfigure()"; // NOSONAR Used for tracing
                 remote.rconfigure(testTree, host, baseDirRelative, scriptName);
             }
             log.info("sent test to " + host + " basedir='"+baseDirRelative+"'"); // $NON-NLS-1$
             if(savep == null) {
                 savep = new Properties();
             }
             log.info("Sending properties "+savep);
             try {
                 methodName="rsetProperties()";// NOSONAR Used for tracing
                 remote.rsetProperties(savep);
             } catch (RemoteException e) {
                 log.warn("Could not set properties: " + e.toString());
             }
             methodName="rrunTest()";
             remote.rrunTest();
             log.info("sent run command to "+ host);
         } catch (IllegalStateException ex) {
             log.error("Error in "+methodName+" method "+ex); // $NON-NLS-1$ $NON-NLS-2$
             tidyRMI(log);
             throw ex; // Don't wrap this error - display it as is
         } catch (Exception ex) {
             log.error("Error in "+methodName+" method "+ex); // $NON-NLS-1$ $NON-NLS-2$
             tidyRMI(log);
             throw new JMeterEngineException("Error in "+methodName+" method "+ex, ex); // $NON-NLS-1$ $NON-NLS-2$
         }
     }
 
     /**
      * Tidy up RMI access to allow JMeter client to exit.
      * Currently just interrups the "RMI Reaper" thread.
      * @param logger where to log the information
      */
     public static void tidyRMI(Logger logger) {
         String reaperRE = JMeterUtils.getPropDefault("rmi.thread.name", "^RMI Reaper$");
         for(Thread t : Thread.getAllStackTraces().keySet()){
             String name = t.getName();
             if (name.matches(reaperRE)) {
                 logger.info("Interrupting "+name);
                 t.interrupt();
             }
         }
     }
 
     /** {@inheritDoc} */
     // Called by JMeter ListenToTest if remoteStop is true
     @Override
     public void exit() {
         log.info("about to exit remote server on "+host);
         try {
             remote.rexit();
         } catch (RemoteException e) {
             log.warn("Could not perform remote exit: " + e.toString());
         }
     }
 
     private Properties savep;
     /** {@inheritDoc} */
     @Override
     public void setProperties(Properties p) {
         savep = p;
         // Sent later
     }
 
     @Override
     public boolean isActive() {
         return true;
     }
 
     public String getHost() {
         return host;
     }
 }
diff --git a/src/core/org/apache/jmeter/engine/StandardJMeterEngine.java b/src/core/org/apache/jmeter/engine/StandardJMeterEngine.java
index 47048404d..a406e7552 100644
--- a/src/core/org/apache/jmeter/engine/StandardJMeterEngine.java
+++ b/src/core/org/apache/jmeter/engine/StandardJMeterEngine.java
@@ -1,597 +1,597 @@
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
 
 package org.apache.jmeter.engine;
 
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Properties;
 import java.util.concurrent.CopyOnWriteArrayList;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.JMeter;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.ListenerNotifier;
 import org.apache.jmeter.threads.PostThreadGroup;
 import org.apache.jmeter.threads.SetupThreadGroup;
 import org.apache.jmeter.threads.TestCompiler;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.collections.SearchByClass;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Runs JMeter tests, either directly for local GUI and non-GUI invocations, 
  * or started by {@link RemoteJMeterEngineImpl} when running in server mode.
  */
 public class StandardJMeterEngine implements JMeterEngine, Runnable {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StandardJMeterEngine.class);
 
     // Should we exit at end of the test? (only applies to server, because host is non-null)
     private static final boolean EXIT_AFTER_TEST =
         JMeterUtils.getPropDefault("server.exitaftertest", false);  // $NON-NLS-1$
 
     // Allow engine and threads to be stopped from outside a thread
     // e.g. from beanshell server
     // Assumes that there is only one instance of the engine
     // at any one time so it is not guaranteed to work ...
     private static volatile StandardJMeterEngine engine;
 
     /*
      * Allow functions etc to register for testStopped notification.
      * Only used by the function parser so far.
      * The list is merged with the testListeners and then cleared.
      */
     private static final List<TestStateListener> testList = new ArrayList<>();
 
     /** Whether to call System.exit(0) in exit after stopping RMI */
     private static final boolean REMOTE_SYSTEM_EXIT = JMeterUtils.getPropDefault("jmeterengine.remote.system.exit", false);
 
     /** Whether to call System.exit(1) if threads won't stop */
     private static final boolean SYSTEM_EXIT_ON_STOP_FAIL = JMeterUtils.getPropDefault("jmeterengine.stopfail.system.exit", true);
     
     /** Whether to call System.exit(0) unconditionally at end of non-GUI test */
     private static final boolean SYSTEM_EXIT_FORCED = JMeterUtils.getPropDefault("jmeterengine.force.system.exit", false);
 
     /** Flag to show whether test is running. Set to false to stop creating more threads. */
     private volatile boolean running = false;
 
     /** Flag to show whether test was shutdown gracefully. */
     private volatile boolean shutdown = false;
 
     /** Flag to show whether engine is active. Set to false at end of test. */
     private volatile boolean active = false;
 
     /** Thread Groups run sequentially */
     private volatile boolean serialized = false;
 
     /** tearDown Thread Groups run after shutdown of main threads */
     private volatile boolean tearDownOnShutdown = false;
 
     private HashTree test;
 
     private final String host;
 
     // The list of current thread groups; may be setUp, main, or tearDown.
     private final List<AbstractThreadGroup> groups = new CopyOnWriteArrayList<>();
 
     public StandardJMeterEngine() {
         this(null);
     }
 
     public StandardJMeterEngine(String host) {
         this.host = host;
         // Hack to allow external control
         initSingletonEngine(this);
     }
     /**
      * Set the shared engine
      * @param standardJMeterEngine 
      */
     private static void initSingletonEngine(StandardJMeterEngine standardJMeterEngine) {
         StandardJMeterEngine.engine = standardJMeterEngine; 
     }
     
     /**
      * set the shared engine to null
      */
     private static void resetSingletonEngine() {
         StandardJMeterEngine.engine = null;
     }
 
     public static void stopEngineNow() {
         if (engine != null) {// May be null if called from Unit test
             engine.stopTest(true);
         }
     }
 
     public static void stopEngine() {
         if (engine != null) { // May be null if called from Unit test
             engine.stopTest(false);
         }
     }
 
     public static synchronized void register(TestStateListener tl) {
         testList.add(tl);
     }
 
     public static boolean stopThread(String threadName) {
         return stopThread(threadName, false);
     }
 
     public static boolean stopThreadNow(String threadName) {
         return stopThread(threadName, true);
     }
 
     private static boolean stopThread(String threadName, boolean now) {
         if (engine == null) {
             return false;// e.g. not yet started
         }
         boolean wasStopped = false;
         // ConcurrentHashMap does not need synch. here
         for (AbstractThreadGroup threadGroup : engine.groups) {
             wasStopped = wasStopped || threadGroup.stopThread(threadName, now);
         }
         return wasStopped;
     }
 
     // End of code to allow engine to be controlled remotely
 
     @Override
     public void configure(HashTree testTree) {
         // Is testplan serialised?
         SearchByClass<TestPlan> testPlan = new SearchByClass<>(TestPlan.class);
         testTree.traverse(testPlan);
         Object[] plan = testPlan.getSearchResults().toArray();
         if (plan.length == 0) {
             throw new RuntimeException("Could not find the TestPlan class!");
         }
         TestPlan tp = (TestPlan) plan[0];
         serialized = tp.isSerialized();
         tearDownOnShutdown = tp.isTearDownOnShutdown();
         active = true;
         test = testTree;
     }
 
     @Override
     public void runTest() throws JMeterEngineException {
         if (host != null){
             long now=System.currentTimeMillis();
             System.out.println("Starting the test on host " + host + " @ "+new Date(now)+" ("+now+")"); // NOSONAR Intentional
         }
         try {
             Thread runningThread = new Thread(this, "StandardJMeterEngine");
             runningThread.start();
         } catch (Exception err) {
             stopTest();
             throw new JMeterEngineException(err);
         }
     }
 
     private void removeThreadGroups(List<?> elements) {
         Iterator<?> iter = elements.iterator();
         while (iter.hasNext()) { // Can't use for loop here because we remove elements
             Object item = iter.next();
             if (item instanceof AbstractThreadGroup) {
                 iter.remove();
             } else if (!(item instanceof TestElement)) {
                 iter.remove();
             }
         }
     }
 
     private void notifyTestListenersOfStart(SearchByClass<TestStateListener> testListeners) {
         for (TestStateListener tl : testListeners.getSearchResults()) {
             if (tl instanceof TestBean) {
                 TestBeanHelper.prepare((TestElement) tl);
             }
             if (host == null) {
                 tl.testStarted();
             } else {
                 tl.testStarted(host);
             }
         }
     }
 
     private void notifyTestListenersOfEnd(SearchByClass<TestStateListener> testListeners) {
         log.info("Notifying test listeners of end of test");
         for (TestStateListener tl : testListeners.getSearchResults()) {
             try {
                 if (host == null) {
                     tl.testEnded();
                 } else {
                     tl.testEnded(host);
                 }
             } catch (Exception e) {
                 log.warn("Error encountered during shutdown of "+tl.toString(),e);
             }
         }
         if (host != null) {
             log.info("Test has ended on host "+host);
             long now=System.currentTimeMillis();
             System.out.println("Finished the test on host " + host + " @ "+new Date(now)+" ("+now+")" // NOSONAR Intentional
             +(EXIT_AFTER_TEST ? " - exit requested." : ""));
             if (EXIT_AFTER_TEST){
                 exit();
             }
         }
         active=false;
     }
 
     @Override
     public void reset() {
         if (running) {
             stopTest();
         }
     }
 
     public synchronized void stopTest() {
         stopTest(true);
     }
 
     @Override
     public synchronized void stopTest(boolean now) {
         shutdown = !now;
         Thread stopThread = new Thread(new StopTest(now));
         stopThread.start();
     }
 
     private class StopTest implements Runnable {
         private final boolean now;
 
         private StopTest(boolean b) {
             now = b;
         }
         
         /**
          * For each current thread group, invoke:
          * <ul> 
          * <li>{@link AbstractThreadGroup#stop()} - set stop flag</li>
          * </ul> 
          */
         private void stopAllThreadGroups() {
             // ConcurrentHashMap does not need synch. here
             for (AbstractThreadGroup threadGroup : groups) {
                 threadGroup.stop();
             }
         }
         
         /**
          * For each thread group, invoke {@link AbstractThreadGroup#tellThreadsToStop()}
          */
         private void tellThreadGroupsToStop() {
             // ConcurrentHashMap does not need protecting
             for (AbstractThreadGroup threadGroup : groups) {
                 threadGroup.tellThreadsToStop();
             }
         }
         
         /**
          * @return boolean true if all threads of all Threead Groups stopped
          */
         private boolean verifyThreadsStopped() {
             boolean stoppedAll = true;
             // ConcurrentHashMap does not need synch. here
             for (AbstractThreadGroup threadGroup : groups) {
                 stoppedAll = stoppedAll && threadGroup.verifyThreadsStopped();
             }
             return stoppedAll;
         }
 
         /**
          * @return total of active threads in all Thread Groups
          */
         private int countStillActiveThreads() {
             int reminingThreads= 0;
             for (AbstractThreadGroup threadGroup : groups) {
                 reminingThreads += threadGroup.numberOfActiveThreads();
             }            
             return reminingThreads; 
         }
         
         @Override
         public void run() {
             running = false;
             resetSingletonEngine();
             if (now) {
                 tellThreadGroupsToStop();
                 pause(10L * countStillActiveThreads());
                 boolean stopped = verifyThreadsStopped();
                 if (!stopped) {  // we totally failed to stop the test
                     if (JMeter.isNonGUI()) {
                         // TODO should we call test listeners? That might hang too ...
-                        log.fatalError(JMeterUtils.getResString("stopping_test_failed")); //$NON-NLS-1$
+                        log.error(JMeterUtils.getResString("stopping_test_failed")); //$NON-NLS-1$
                         if (SYSTEM_EXIT_ON_STOP_FAIL) { // default is true
-                            log.fatalError("Exiting");
+                            log.error("Exiting");
                             System.out.println("Fatal error, could not stop test, exiting"); // NOSONAR Intentional
                             System.exit(1); // NOSONAR Intentional
                         } else {
                             System.out.println("Fatal error, could not stop test"); // NOSONAR Intentional                            
                         }
                     } else {
                         JMeterUtils.reportErrorToUser(
                                 JMeterUtils.getResString("stopping_test_failed"), //$NON-NLS-1$
                                 JMeterUtils.getResString("stopping_test_title")); //$NON-NLS-1$
                     }
                 } // else will be done by threadFinished()
             } else {
                 stopAllThreadGroups();
             }
         }
     }
 
     @Override
     public void run() {
         log.info("Running the test!");
         running = true;
 
         /*
          * Ensure that the sample variables are correctly initialised for each run.
          */
         SampleEvent.initSampleVariables();
 
         JMeterContextService.startTest();
         try {
             PreCompiler compiler = new PreCompiler();
             test.traverse(compiler);
         } catch (RuntimeException e) {
             log.error("Error occurred compiling the tree:",e);
             JMeterUtils.reportErrorToUser("Error occurred compiling the tree: - see log file", e);
             return; // no point continuing
         }
         /**
          * Notification of test listeners needs to happen after function
          * replacement, but before setting RunningVersion to true.
          */
         SearchByClass<TestStateListener> testListeners = new SearchByClass<>(TestStateListener.class); // TL - S&E
         test.traverse(testListeners);
 
         // Merge in any additional test listeners
         // currently only used by the function parser
         testListeners.getSearchResults().addAll(testList);
         testList.clear(); // no longer needed
 
         test.traverse(new TurnElementsOn());
         notifyTestListenersOfStart(testListeners);
 
         List<?> testLevelElements = new LinkedList<>(test.list(test.getArray()[0]));
         removeThreadGroups(testLevelElements);
 
         SearchByClass<SetupThreadGroup> setupSearcher = new SearchByClass<>(SetupThreadGroup.class);
         SearchByClass<AbstractThreadGroup> searcher = new SearchByClass<>(AbstractThreadGroup.class);
         SearchByClass<PostThreadGroup> postSearcher = new SearchByClass<>(PostThreadGroup.class);
 
         test.traverse(setupSearcher);
         test.traverse(searcher);
         test.traverse(postSearcher);
         
         TestCompiler.initialize();
         // for each thread group, generate threads
         // hand each thread the sampler controller
         // and the listeners, and the timer
         Iterator<SetupThreadGroup> setupIter = setupSearcher.getSearchResults().iterator();
         Iterator<AbstractThreadGroup> iter = searcher.getSearchResults().iterator();
         Iterator<PostThreadGroup> postIter = postSearcher.getSearchResults().iterator();
 
         ListenerNotifier notifier = new ListenerNotifier();
 
         int groupCount = 0;
         JMeterContextService.clearTotalThreads();
         
         if (setupIter.hasNext()) {
             log.info("Starting setUp thread groups");
             while (running && setupIter.hasNext()) {//for each setup thread group
                 AbstractThreadGroup group = setupIter.next();
                 groupCount++;
                 String groupName = group.getName();
                 log.info("Starting setUp ThreadGroup: " + groupCount + " : " + groupName);
                 startThreadGroup(group, groupCount, setupSearcher, testLevelElements, notifier);
                 if (serialized && setupIter.hasNext()) {
                     log.info("Waiting for setup thread group: "+groupName+" to finish before starting next setup group");
                     group.waitThreadsStopped();
                 }
             }    
             log.info("Waiting for all setup thread groups to exit");
             //wait for all Setup Threads To Exit
             waitThreadsStopped();
             log.info("All Setup Threads have ended");
             groupCount=0;
             JMeterContextService.clearTotalThreads();
         }
 
         groups.clear(); // The groups have all completed now                
 
         /*
          * Here's where the test really starts. Run a Full GC now: it's no harm
          * at all (just delays test start by a tiny amount) and hitting one too
          * early in the test can impair results for short tests.
          */
         JMeterUtils.helpGC();
         
         JMeterContextService.getContext().setSamplingStarted(true);
         boolean mainGroups = running; // still running at this point, i.e. setUp was not cancelled
         while (running && iter.hasNext()) {// for each thread group
             AbstractThreadGroup group = iter.next();
             //ignore Setup and Post here.  We could have filtered the searcher. but then
             //future Thread Group objects wouldn't execute.
             if (group instanceof SetupThreadGroup ||
                     group instanceof PostThreadGroup) {
                 continue;
             }
             groupCount++;
             String groupName = group.getName();
             log.info("Starting ThreadGroup: " + groupCount + " : " + groupName);
             startThreadGroup(group, groupCount, searcher, testLevelElements, notifier);
             if (serialized && iter.hasNext()) {
                 log.info("Waiting for thread group: "+groupName+" to finish before starting next group");
                 group.waitThreadsStopped();
             }
         } // end of thread groups
         if (groupCount == 0){ // No TGs found
             log.info("No enabled thread groups found");
         } else {
             if (running) {
                 log.info("All thread groups have been started");
             } else {
                 log.info("Test stopped - no more thread groups will be started");
             }
         }
 
         //wait for all Test Threads To Exit
         waitThreadsStopped();
         groups.clear(); // The groups have all completed now            
 
         if (postIter.hasNext()){
             groupCount = 0;
             JMeterContextService.clearTotalThreads();
             log.info("Starting tearDown thread groups");
             if (mainGroups && !running) { // i.e. shutdown/stopped during main thread groups
                 running = shutdown && tearDownOnShutdown; // re-enable for tearDown if necessary
             }
             while (running && postIter.hasNext()) {//for each setup thread group
                 AbstractThreadGroup group = postIter.next();
                 groupCount++;
                 String groupName = group.getName();
                 log.info("Starting tearDown ThreadGroup: " + groupCount + " : " + groupName);
                 startThreadGroup(group, groupCount, postSearcher, testLevelElements, notifier);
                 if (serialized && postIter.hasNext()) {
                     log.info("Waiting for post thread group: "+groupName+" to finish before starting next post group");
                     group.waitThreadsStopped();
                 }
             }
             waitThreadsStopped(); // wait for Post threads to stop
         }
 
         notifyTestListenersOfEnd(testListeners);
         JMeterContextService.endTest();
         if (JMeter.isNonGUI() && SYSTEM_EXIT_FORCED) {
             log.info("Forced JVM shutdown requested at end of test");
             System.exit(0); // NOSONAR Intentional
         }
     }
     
     private void startThreadGroup(AbstractThreadGroup group, int groupCount, SearchByClass<?> searcher, List<?> testLevelElements, ListenerNotifier notifier)
     {
         try {
             int numThreads = group.getNumThreads();
             JMeterContextService.addTotalThreads(numThreads);
             boolean onErrorStopTest = group.getOnErrorStopTest();
             boolean onErrorStopTestNow = group.getOnErrorStopTestNow();
             boolean onErrorStopThread = group.getOnErrorStopThread();
             boolean onErrorStartNextLoop = group.getOnErrorStartNextLoop();
             String groupName = group.getName();
             log.info("Starting " + numThreads + " threads for group " + groupName + ".");
     
             if (onErrorStopTest) {
                 log.info("Test will stop on error");
             } else if (onErrorStopTestNow) {
                 log.info("Test will stop abruptly on error");
             } else if (onErrorStopThread) {
                 log.info("Thread will stop on error");
             } else if (onErrorStartNextLoop) {
                 log.info("Thread will start next loop on error");
             } else {
                 log.info("Thread will continue on error");
             }
             ListedHashTree threadGroupTree = (ListedHashTree) searcher.getSubTree(group);
             threadGroupTree.add(group, testLevelElements);
     
             groups.add(group);
             group.start(groupCount, notifier, threadGroupTree, this);
         } catch (JMeterStopTestException ex) { // NOSONAR Reported by log
             JMeterUtils.reportErrorToUser("Error occurred starting thread group :" + group.getName()+ ", error message:"+ex.getMessage()
                 +", \r\nsee log file for more details", ex);
             return; // no point continuing
         }
     }
 
     /**
      * Wait for Group Threads to stop
      */
     private void waitThreadsStopped() {
         // ConcurrentHashMap does not need synch. here
         for (AbstractThreadGroup threadGroup : groups) {
             threadGroup.waitThreadsStopped();
         }
     }
 
     public void askThreadsToStop() {
         if (engine != null) { // Will be null if StopTest thread has started
             engine.stopTest(false);
         }
     }
 
     /** 
      * Remote exit
      * Called by RemoteJMeterEngineImpl.rexit()
      * and by notifyTestListenersOfEnd() iff exitAfterTest is true;
      * in turn that is called by the run() method and the StopTest class
      * also called
      */
     @Override
     public void exit() {
         ClientJMeterEngine.tidyRMI(log); // This should be enough to allow server to exit.
         if (REMOTE_SYSTEM_EXIT) { // default is false
             log.warn("About to run System.exit(0) on "+host);
             // Needs to be run in a separate thread to allow RMI call to return OK
             Thread t = new Thread() {
                 @Override
                 public void run() {
                     pause(1000); // Allow RMI to complete
                     log.info("Bye from "+host);
                     System.out.println("Bye from "+host); // NOSONAR Intentional
                     System.exit(0); // NOSONAR Intentional
                 }
             };
             t.start();
         }
     }
 
     private void pause(long ms){
         try {
             TimeUnit.MILLISECONDS.sleep(ms);
         } catch (InterruptedException e) {
             Thread.currentThread().interrupt();
         }
     }
 
     @Override
     public void setProperties(Properties p) {
         log.info("Applying properties "+p);
         JMeterUtils.getJMeterProperties().putAll(p);
     }
     
     @Override
     public boolean isActive() {
         return active;
     }
 }
