diff --git a/src/core/org/apache/jmeter/JMeter.java b/src/core/org/apache/jmeter/JMeter.java
index 3affdafab..feed8c751 100644
--- a/src/core/org/apache/jmeter/JMeter.java
+++ b/src/core/org/apache/jmeter/JMeter.java
@@ -1,1251 +1,1251 @@
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
 import org.apache.jorphan.reflect.ClassTools;
 import org.apache.jorphan.util.HeapDumper;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.logging.log4j.Level;
 import org.apache.logging.log4j.core.config.Configurator;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * Main JMeter class; processes options and starts the GUI, non-GUI or server as appropriate.
  */
 public class JMeter implements JMeterPlugin {
     private static final Logger log = LoggerFactory.getLogger(JMeter.class);
     
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
     private static final int FORCE_DELETE_RESULT_FILE      = 'f';// $NON-NLS-1$
     
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
-                    "[category=]level e.g. jorphan=INFO or jmeter.util=DEBUG");
+                    "[category=]level e.g. jorphan=INFO, jmeter.util=DEBUG or com.example.foo=WARN");
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
      private static final CLOptionDescriptor D_FORCE_DELETE_RESULT_FILE =
             new CLOptionDescriptor("forceDeleteResultFile",
                     CLOptionDescriptor.ARGUMENT_DISALLOWED, FORCE_DELETE_RESULT_FILE,
                     "force delete existing results files before start the test");
 
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
             D_FORCE_DELETE_RESULT_FILE,
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
 
     /** should delete result file before start ? */
     private boolean deleteResultFile = false; 
     
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
             log.warn("Could not set LAF to: {}", jMeterLaf, ex);
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
                 log.info("Loading file: {}", f);
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
 
             if (log.isInfoEnabled()) {
                 log.info(JMeterUtils.getJMeterCopyright());
                 log.info("Version {}", JMeterUtils.getJMeterVersion());
                 log.info("java.version={}", System.getProperty("java.version"));//$NON-NLS-1$ //$NON-NLS-2$
                 log.info("java.vm.name={}", System.getProperty("java.vm.name"));//$NON-NLS-1$ //$NON-NLS-2$
                 log.info("os.name={}", System.getProperty("os.name"));//$NON-NLS-1$ //$NON-NLS-2$
                 log.info("os.arch={}", System.getProperty("os.arch"));//$NON-NLS-1$ //$NON-NLS-2$
                 log.info("os.version={}", System.getProperty("os.version"));//$NON-NLS-1$ //$NON-NLS-2$
                 log.info("file.encoding={}", System.getProperty("file.encoding"));//$NON-NLS-1$ //$NON-NLS-2$
                 log.info("Max memory     ={}", Runtime.getRuntime().maxMemory());
                 log.info("Available Processors ={}", Runtime.getRuntime().availableProcessors());
                 log.info("Default Locale={}", Locale.getDefault().getDisplayName());
                 log.info("JMeter  Locale={}", JMeterUtils.getLocale().getDisplayName());
                 log.info("JMeterHome={}", JMeterUtils.getJMeterHome());
                 log.info("user.dir  ={}", System.getProperty("user.dir"));//$NON-NLS-1$ //$NON-NLS-2$
                 log.info("PWD       ={}", new File(".").getCanonicalPath());//$NON-NLS-1$
                 log.info("IP: {} Name: {} FullName: {}", JMeterUtils.getLocalHostIP(), JMeterUtils.getLocalHostName(),
                         JMeterUtils.getLocalHostFullName());
             }
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
             log.error("An error occurred: ", e);
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
             final String reportOutputFolderAbsPath = reportOutputFolderAsFile.getAbsolutePath();
             log.info("Setting property '{}' to:'{}'", JMETER_REPORT_OUTPUT_DIR_PROPERTY, reportOutputFolderAbsPath);
             JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, reportOutputFolderAbsPath);
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
         log.info("{}={}", property, userpath); //$NON-NLS-1$
         StringTokenizer tok = new StringTokenizer(userpath, sep);
         while(tok.hasMoreTokens()) {
             String path=tok.nextToken();
             File f=new File(path);
             if (!f.canRead() && !f.isDirectory()) {
                 log.warn("Can't read {}", path);
             } else {
                 if (cp) {
                     log.info("Adding to classpath and loader: {}", path);
                     NewDriver.addPath(path);
                 } else {
                     log.info("Adding to loader: {}", path);
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
             log.info("Starting Beanshell server ({},{})", bshport, bshfile);
             Runnable t = new BeanShellServer(bshport, bshfile);
             t.run(); // NOSONAR we just evaluate some code here
         }
 
         // Should we run a beanshell script on startup?
         String bshinit = JMeterUtils.getProperty("beanshell.init.file");// $NON-NLS-1$
         if (bshinit != null){
             log.info("Run Beanshell on file: {}", bshinit);
             try {
                 BeanShellInterpreter bsi = new BeanShellInterpreter();
                 bsi.source(bshinit);
             } catch (ClassNotFoundException e) {
                 if (log.isWarnEnabled()) {
                     log.warn("Could not start Beanshell: {}", e.getLocalizedMessage());
                 }
             } catch (JMeterException e) {
                 if (log.isWarnEnabled()) {
                     log.warn("Could not process Beanshell file: {}", e.getLocalizedMessage());
                 }
             }
         }
 
         int mirrorPort=JMeterUtils.getPropDefault("mirror.server.port", 0);// $NON-NLS-1$
         if (mirrorPort > 0){
             log.info("Starting Mirror server ({})", mirrorPort);
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
                 log.info("Set Proxy login: {}/{}", u, p);
                 jmeterProps.setProperty(HTTP_PROXY_USER, u);//for Httpclient
                 jmeterProps.setProperty(HTTP_PROXY_PASS, p);//for Httpclient
             } else {
                 String u = parser.getArgumentById(PROXY_USERNAME).getArgument();
                 Authenticator.setDefault(new ProxyAuthenticator(u, ""));
                 log.info("Set Proxy login: {}", u);
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
             log.info("Set http[s].proxyHost: {} Port: {}", h, p);
         } else if (parser.getArgumentById(PROXY_HOST) != null || parser.getArgumentById(PROXY_PORT) != null) {
             throw new IllegalUserActionException(JMeterUtils.getResString("proxy_cl_error"));// $NON-NLS-1$
         }
 
         if (parser.getArgumentById(NONPROXY_HOSTS) != null) {
             String n = parser.getArgumentById(NONPROXY_HOSTS).getArgument();
             System.setProperty("http.nonProxyHosts",  n );// $NON-NLS-1$
             System.setProperty("https.nonProxyHosts", n );// $NON-NLS-1$
             log.info("Set http[s].nonProxyHosts: {}", n);
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
                     log.info("Loading user properties from: {}", file);
                     Properties tmp = new Properties();
                     tmp.load(fis);
                     jmeterProps.putAll(tmp);
                 } catch (IOException e) {
                     log.warn("Error loading user property file: {}", userProp, e);
                 }
             }
         }
 
         // Add local system properties, if the file is found
         String sysProp = JMeterUtils.getPropDefault("system.properties",""); //$NON-NLS-1$
         if (sysProp.length() > 0){
             File file = JMeterUtils.findFile(sysProp);
             if (file.canRead()) {
                 try (FileInputStream fis = new FileInputStream(file)){
                     log.info("Loading system properties from: {}", file);
                     System.getProperties().load(fis);
                 } catch (IOException e) {
                     log.warn("Error loading system property file: {}", sysProp, e);
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
                     log.warn("Can't find additional property file: {}", name, e);
                 } catch (IOException e) { // NOSONAR
                     log.warn("Error loading additional property file: {}", name, e);
                 }
                 break;
             case SYSTEM_PROPFILE:
                 log.info("Setting System properties from file: {}", name);
                 try (FileInputStream fis = new FileInputStream(new File(name))){
                     System.getProperties().load(fis);
                 } catch (IOException e) { // NOSONAR
                     if (log.isWarnEnabled()) {
                         log.warn("Cannot find system property file. {}", e.getLocalizedMessage());
                     }
                 }
                 break;
             case SYSTEM_PROPERTY:
                 if (value.length() > 0) { // Set it
                     log.info("Setting System property: {}={}", name, value);
                     System.getProperties().setProperty(name, value);
                 } else { // Reset it
                     log.warn("Removing System property: {}", name);
                     System.getProperties().remove(name);
                 }
                 break;
             case JMETER_PROPERTY:
                 if (value.length() > 0) { // Set it
                     log.info("Setting JMeter property: {}={}", name, value);
                     jmeterProps.setProperty(name, value);
                 } else { // Reset it
                     log.warn("Removing JMeter property: {}", name);
                     jmeterProps.remove(name);
                 }
                 break;
             case JMETER_GLOBAL_PROP:
                 if (value.length() > 0) { // Set it
                     log.info("Setting Global property: {}={}", name, value);
                     remoteProps.setProperty(name, value);
                 } else {
                     File propFile = new File(name);
                     if (propFile.canRead()) {
                         log.info("Setting Global properties from the file {}", name);
                         try (FileInputStream fis = new FileInputStream(propFile)){
                             remoteProps.load(fis);
                         } catch (FileNotFoundException e) { // NOSONAR
                             if (log.isWarnEnabled()) {
                                 log.warn("Could not find properties file: {}", e.getLocalizedMessage());
                             }
                         } catch (IOException e) { // NOSONAR
                             if (log.isWarnEnabled()) {
                                 log.warn("Could not load properties file: {}", e.getLocalizedMessage());
                             }
                         } 
                     }
                 }
                 break;
             case LOGLEVEL:
                 if (value.length() > 0) { // Set category
                     log.info("LogLevel: {}={}", name, value);
                     final Level logLevel = Level.getLevel(value);
                     if (logLevel != null) {
                         String loggerName = name;
                         if (name.startsWith("jmeter") || name.startsWith("jorphan")) {
                             loggerName = PACKAGE_PREFIX + name;
                         }
                         Configurator.setAllLevels(loggerName, logLevel);
                     } else {
                         log.warn("Invalid log level, '{}' for '{}'.", value, name);
                     }
                 } else { // Set root level
                     log.warn("LogLevel: {}", name);
                     final Level logLevel = Level.getLevel(name);
                     if (logLevel != null) {
                         Configurator.setRootLevel(logLevel);
                     } else {
                         log.warn("Invalid log level, '{}', for the root logger.", name);
                     }
                 }
                 break;
             case REMOTE_STOP:
                 remoteStop = true;
                 break;
             case FORCE_DELETE_RESULT_FILE:
                 deleteResultFile = true;
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
         driver.deleteResultFile = this.deleteResultFile;
         
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
             
             if (deleteResultFile) {
                 SearchByClass<ResultCollector> resultListeners = new SearchByClass<>(ResultCollector.class);
                 tree.traverse(resultListeners);
                 Iterator<ResultCollector> irc = resultListeners.getSearchResults().iterator();
                 while (irc.hasNext()) {
                     ResultCollector rc = irc.next();
                     File resultFile = new File(rc.getFilename());
                     if (resultFile.exists()) {
                         resultFile.delete();
                     }
                 }
             }
 
             Summariser summer = null;
             String summariserName = JMeterUtils.getPropDefault("summariser.name", "");//$NON-NLS-1$
             if (summariserName.length() > 0) {
                 log.info("Creating summariser <{}>", summariserName);
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
             final long now=System.currentTimeMillis();
             log.info("Finished remote host: {} ({})", host, now);
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
             final long now=System.currentTimeMillis();
             log.info("Started remote host:  {} ({})", host, now);
         }
 
         @Override
         public void testStarted() {
             if (log.isInfoEnabled()) {
                 final long now = System.currentTimeMillis();
                 log.info("{} ({})", JMeterUtils.getResString("running_test"), now);//$NON-NLS-1$
             }
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
                     log.error("Error generating dashboard: {}", ex, ex);
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
             } else if (pauseToCheckForRemainingThreads<=0) {
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
diff --git a/xdocs/usermanual/get-started.xml b/xdocs/usermanual/get-started.xml
index 5c1f6bc09..a47c241f8 100644
--- a/xdocs/usermanual/get-started.xml
+++ b/xdocs/usermanual/get-started.xml
@@ -1,682 +1,779 @@
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
 
 <!DOCTYPE document[
 <!ENTITY sect-num '1'>
 <!ENTITY hellip   "&#x02026;" >
 ]>
 
 <document next="build-test-plan.html" id="$Id$">
 
 <properties>
   <title>User's Manual: Getting Started</title>
 </properties>
 <body>
 
 <section name="&sect-num;. Getting Started" anchor="get_started">
 <section name="&sect-num;.0 Overview" anchor="overview">
 When using JMeter you will usually follow this process:
 <subsection name="&sect-num;.0.1 Test plan building" anchor="test_plan_building">
 <p>To do that, you will <a href="get-started.html#running">run JMeter in GUI Mode.</a><br/>
 Then you can either choose to record the application from a browser, or native application.
 You can use for that the menu <menuchoice><guimenuitem>File</guimenuitem><guimenuitem>Templates...</guimenuitem><guimenuitem>Recording</guimenuitem></menuchoice><br/>
 </p>
 <p>
 Note you can also manually build your plan. Ensure you read this <a href="test_plan.html">documentation</a> to understand major concepts.
 </p>
 You will also debug it using one of these options:
 <ul>
     <li><menuchoice><guimenuitem>Run</guimenuitem><guimenuitem>Start no pauses</guimenuitem></menuchoice></li>
     <li><menuchoice><guimenuitem>Run</guimenuitem><guimenuitem>Start</guimenuitem></menuchoice></li>
     <li><menuchoice><guimenuitem>Validate</guimenuitem></menuchoice> on <a href="component_reference.html#Thread_Group">Thread Group</a></li>
 </ul>
 <p>
 and <a href="component_reference.html#View_Results_Tree">View Results Tree</a> renderers or Testers (CSS/JQUERY, JSON, Regexp, XPath).<br/>
 Ensure you follow <a href="best-practices.html" >best-practices</a> when building your Test Plan.
 </p>
 </subsection>
 <subsection name="&sect-num;.0.2 Load Test running" anchor="load_test_running">
 <p>Once your Test Plan is ready, you can start your Load Test.
 The first step is to configure the injectors that will run JMeter, this as for any other Load Testing tool includes:
 <ul>
     <li>Correct machine sizing in terms of CPU, memory and network</li>
     <li>OS Tuning</li>
     <li>Java setup: Ensure you install the latest version of Java supported by JMeter</li>
     <li><b>Correct sizing of Java Heap</b>. By default JMeter runs with a heap of 512MB, this might not be enough for your test and depends on your test plan and number of threads you want to run</li>
 </ul> 
 
 Once everything is ready, you will use Command-line mode (called <a href="#non_gui">Non-GUI mode</a>) to run it for the Load Test. 
 <note>Don't run load test using GUI mode !</note><br/>
 Using Non-GUI mode, you can generate a CSV (or XML) file containing results and have JMeter <a href="generating-dashboard.html">generate an HTML report</a> at end of Load Test.
 JMeter will by default provide a summary of load test while it's running. <br/>
 You can also have <a href="realtime-results.html" >real-time results</a> during your test using <a href="component_reference.html#Backend_Listener">Backend Listener</a>.
 </p>
 </subsection>
 <subsection name="&sect-num;.0.3 Load Test analysis" anchor="load_test_analysis">
 Once your Load Test is finished, you can use the HTML report to analyze your load test. <br/>
 </subsection>
 </section>
 
 <subsection name="&sect-num;.0.4 Let's start" anchor="lets_start">
 <p>The easiest way to begin using JMeter is to first
 <a href="http://jmeter.apache.org/download_jmeter.cgi">download the latest production release</a> and install it.
 The release contains all of the files you need to build and run most types of tests,
 e.g. Web (HTTP/HTTPS), FTP, JDBC, LDAP, Java, JUnit and more.</p>
 <p>If you want to perform JDBC testing,
 then you will, of course, need the appropriate JDBC driver from your vendor.  JMeter does not come with
 any JDBC drivers.</p>
 <p>
 JMeter includes the JMS API jar, but does not include a JMS client implementation.
 If you want to run JMS tests, you will need to download the appropriate jars from the JMS provider.
 </p>
 <note>
 See the <a href="#classpath">JMeter Classpath</a> section for details on installing additional jars.
 </note>
 <p>Next, start JMeter and go through the <a href="build-test-plan.html">Building a Test Plan</a> section
 of the User Guide to familiarize yourself with JMeter basics (for example, adding and removing elements).</p>
 <p>Finally, go through the appropriate section on how to build a specific type of Test Plan.
 For example, if you are interested in testing a Web application, then see the section
 <a href="build-web-test-plan.html">Building a Web Test Plan</a>.
 The other specific Test Plan sections are:
 <ul>
 <li><a href="build-adv-web-test-plan.html">Advanced Web Test Plan</a></li>
 <li><a href="build-db-test-plan.html">JDBC</a></li>
 <li><a href="build-ftp-test-plan.html">FTP</a></li>
 <li><a href="build-jms-point-to-point-test-plan.html">JMS Point-to-Point</a></li>
 <li><a href="build-jms-topic-test-plan.html">JMS Topic</a></li>
 <li><a href="build-ldap-test-plan.html">LDAP</a></li>
 <li><a href="build-ldapext-test-plan.html">LDAP Extended</a></li>
 <li><a href="build-ws-test-plan.html">WebServices (SOAP)</a></li>
 </ul>
 </p>
 <p>Once you are comfortable with building and running JMeter Test Plans, you can look into the
 various configuration elements (timers, listeners, assertions, and others) which give you more control
 over your Test Plans.</p>
 </subsection>
 </section>
 
 <section name="&sect-num;.1 Requirements" anchor="requirements">
 <p>JMeter requires that your computing environment meets some minimum requirements.</p>
 
 <subsection name="&sect-num;.1.1 Java Version" anchor="java_versions">
 <note>JMeter requires a fully compliant JVM 8, we advise that you install latest minor version of those major versions.
 Java 9 is not tested completely as of JMeter 3.2.
 </note>
 <p>Because JMeter uses only standard Java APIs, please do not file bug reports if your JRE fails to run
 JMeter because of JRE implementation issues.</p>
 </subsection>
 
 <subsection name="&sect-num;.1.2 Operating Systems" anchor="os">
 <p>JMeter is a 100% Java application and should run correctly on any system
 that has a compliant Java implementation.</p>
 <p>Operating systems tested with JMeter can be viewed on  
 <a href="http://wiki.apache.org/jmeter/JMeterAndOperatingSystemsTested">this page</a>
 on JMeter wiki.</p>
 <p>Even if your OS is not listed on the wiki page, JMeter should run on it provided that the JVM is compliant.</p>
 </subsection>
 </section>
 
 <section name="&sect-num;.2 Optional" anchor="optional">
 <p>If you plan on doing JMeter development, then you will need one or more optional packages listed below.</p>
 
 
 <subsection name="&sect-num;.2.1 Java Compiler" anchor="opt_compiler">
 <p>If you want to build the JMeter source or develop JMeter plugins, then you will need a fully compliant JDK 8 or higher.</p>
 </subsection>
 
 <subsection name="&sect-num;.2.2 SAX XML Parser" anchor="opt_sax">
 <p>JMeter comes with Apache's <a href="http://xml.apache.org/">Xerces XML parser</a>. You have the option of telling JMeter
 to use a different XML parser. To do so, include the classes for the third-party parser in JMeter's <a href="#classpath">classpath</a>,
 and update the <a href="#configuring_jmeter">jmeter.properties</a> file with the full classname of the parser
 implementation.</p>
 </subsection>
 
 <subsection name="&sect-num;.2.3 Email Support" anchor="opt_email">
 <p>JMeter has extensive Email capabilities. 
 It can send email based on test results, and has a POP3(S)/IMAP(S) sampler.
 It also has an SMTP(S) sampler.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.2.4 SSL Encryption" anchor="opt_ssl">
 <p>To test a web server using SSL encryption (HTTPS), JMeter requires that an
 implementation of SSL be provided, as is the case with Sun Java 1.4 and above.
 If your version of Java does not include SSL support, then it is possible to add an external implementation.
 Include the necessary encryption packages in JMeter's <a href="#classpath">classpath</a>.  
 Also, update <a href="#configuring_jmeter"><code>system.properties</code></a> to register the SSL Provider.</p>
 <p>
 JMeter HTTP defaults to protocol level TLS. This can be changed by editing the JMeter property 
 <code>https.default.protocol</code> in <code>jmeter.properties</code> or <code>user.properties</code>.
 </p>
 <p><b>The JMeter HTTP samplers are configured to accept all certificates,
 whether trusted or not, regardless of validity periods, etc.</b>
 This is to allow the maximum flexibility in testing servers.</p>
 <p>If the server requires a client certificate, this can be provided.</p>
 <p>There is also the <complink name="SSL Manager"/>, for greater control of certificates.</p>
 <note>The JMeter proxy server (see below) supports recording HTTPS (SSL)</note>
 <p>
 The SMTP sampler can optionally use a local trust store or trust all certificates.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.2.5 JDBC Driver" anchor="opt_jdbc">
 <p>You will need to add your database vendor's JDBC driver to the <a href="#classpath">classpath</a> if you want to do JDBC testing.
 Make sure the file is a jar file, not a zip.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.2.6 JMS client" anchor="opt_jms">
 <p>
 JMeter now includes the JMS API from Apache Geronimo, so you just need to add the appropriate JMS Client implementation
 jar(s) from the JMS provider. Please refer to their documentation for details.
 There may also be some information on the <a href="http://wiki.apache.org/jmeter/">JMeter Wiki</a>.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.2.7 Libraries for ActiveMQ JMS" anchor="libraries_activemq">
 <p>
 You will need to add the jar <code>activemq-all-X.X.X.jar</code> to your classpath, e.g. by storing it in the <code>lib/</code> directory.
 </p>
 <p>
 See <a href="http://activemq.apache.org/initial-configuration.html">ActiveMQ initial configuration page</a>
 for details.
 </p>
 </subsection>
 
 <note>
 See the <a href="#classpath">JMeter Classpath</a> section for more details on installing additional jars.
 </note>
 </section>
 
 <section name="&sect-num;.3 Installation" anchor="install">
 
 <p>We recommend that most users run the <a href="http://jmeter.apache.org/download_jmeter.cgi">latest release</a>.</p>
 <p>To install a release build, simply unzip the zip/tar file into the directory
 where you want JMeter to be installed.  Provided that you have a JRE/JDK correctly installed
 and the <code>JAVA_HOME</code> environment variable set, there is nothing more for you to do.</p>
 <note>
 There can be problems (especially with client-server mode) if the directory path contains any spaces.
 </note>
 <p>
 The installation directory structure should look something like this (where <code>X.Y</code> is version number):
 <source>
 apache-jmeter-X.Y
 apache-jmeter-X.Y/bin
 apache-jmeter-X.Y/docs
 apache-jmeter-X.Y/extras
 apache-jmeter-X.Y/lib/
 apache-jmeter-X.Y/lib/ext
 apache-jmeter-X.Y/lib/junit
 apache-jmeter-X.Y/licenses
 apache-jmeter-X.Y/printable_docs
 </source>
 You can rename the parent directory (i.e. <code>apache-jmeter-X.Y</code>) if you want, but do not change any of the sub-directory names.
 </p>
 </section>
 
 <section name="&sect-num;.4 Running JMeter" anchor="running">
 <br/>
 <p>To run JMeter, run the <code>jmeter.bat</code> (for Windows) or <code>jmeter</code> (for Unix) file.
 These files are found in the bin directory.
 After a short time, the JMeter GUI should appear.
 <note>GUI mode should only be used for creating the test script, NON GUI mode must be used for load testing</note>
 </p>
 
 <p>
 There are some additional scripts in the bin directory that you may find useful.
 Windows script files (the .CMD files require Win2K or later):
 </p>
 <dl>
 <dt><code>jmeter.bat</code></dt><dd>run JMeter (in GUI mode by default)</dd>
 <dt><code>jmeterw.cmd</code></dt><dd>run JMeter without the windows shell console (in GUI mode by default)</dd>
 <dt><code>jmeter-n.cmd</code></dt><dd>drop a JMX file on this to run a non-GUI test</dd>
 <dt><code>jmeter-n-r.cmd</code></dt><dd>drop a JMX file on this to run a non-GUI test remotely</dd>
 <dt><code>jmeter-t.cmd</code></dt><dd>drop a JMX file on this to load it in GUI mode</dd>
 <dt><code>jmeter-server.bat</code></dt><dd>start JMeter in server mode</dd>
 <dt><code>mirror-server.cmd</code></dt><dd>runs the JMeter Mirror Server in non-GUI mode</dd>
 <dt><code>shutdown.cmd</code></dt><dd>Run the Shutdown client to stop a non-GUI instance gracefully</dd>
 <dt><code>stoptest.cmd</code></dt><dd>Run the Shutdown client to stop a non-GUI instance abruptly</dd>
 </dl>
 <note>The special name <code>LAST</code> can be used with <code>jmeter-n.cmd</code>, <code>jmeter-t.cmd</code> and <code>jmeter-n-r.cmd</code>
 and means the last test plan that was run interactively.</note>
 
 <p>
 The environment variable <code>JVM_ARGS</code> can be used to override JVM settings in the <code>jmeter.bat</code> script.
 For example:
 </p>
 <source>
 set JVM_ARGS="-Xms1024m -Xmx1024m -Dpropname=propvalue"
 jmeter -t test.jmx &hellip;
 </source>
 
 <p>
 Un*x script files; should work on most Linux/Unix systems:
 </p>
 <dl>
 <dt><code>jmeter</code></dt><dd>run JMeter (in GUI mode by default). Defines some JVM settings which may not work for all JVMs.</dd>
 <dt><code>jmeter-server</code></dt><dd>start JMeter in server mode (calls jmeter script with appropriate parameters)</dd>
 <dt><code>jmeter.sh</code></dt><dd>very basic JMeter script (You may need to adapt JVM options like memory settings).</dd>
 <dt><code>mirror-server.sh</code></dt><dd>runs the JMeter Mirror Server in non-GUI mode</dd>
 <dt><code>shutdown.sh</code></dt><dd>Run the Shutdown client to stop a non-GUI instance gracefully</dd>
 <dt><code>stoptest.sh</code></dt><dd>Run the Shutdown client to stop a non-GUI instance abruptly</dd>
 </dl>
 <p>
 It may be necessary to edit the jmeter shell script if some of the JVM options are not supported
 by the JVM you are using.
 The <code>JVM_ARGS</code> environment variable can be used to override or set additional JVM options, for example:
 </p>
 <source>
 JVM_ARGS="-Xms1024m -Xmx1024m" jmeter -t test.jmx [etc.]
 </source>
 <p>
 will override the HEAP settings in the script.
 </p>
 <subsection name="&sect-num;.4.1 JMeter's Classpath" anchor="classpath">
 <p>JMeter automatically finds classes from jars in the following directories:</p>
 <dl>
 <dt><code>JMETER_HOME/lib</code></dt><dd>used for utility jars</dd>
 <dt><code>JMETER_HOME/lib/ext</code></dt><dd>used for JMeter components and plugins</dd>
 </dl>
 <p>If you have developed new JMeter components,
 then you should jar them and copy the jar into JMeter's <code>lib/ext</code> directory.
 JMeter will automatically find JMeter components in any jars found here.
 Do not use <code>lib/ext</code> for utility jars or dependency jars used by the plugins;
 it is only intended for JMeter components and plugins.
 </p>
 <p>If you don't want to put JMeter plugin jars in the <code>lib/ext</code> directory,
 then define the property <code>search_paths</code> in <code>jmeter.properties</code>.
 </p>
 <p>Utility and dependency jars (libraries etc) can be placed in the <code>lib</code> directory.</p>
 <p>If you don't want to put such jars in the <code>lib</code> directory,
 then define the property <code>user.classpath</code> or <code>plugin_dependency_paths</code>
 in <code>jmeter.properties</code>. See below for an explanation of the differences.
 </p>
 <p>
 Other jars (such as JDBC, JMS implementations and any other support libraries needed by the JMeter code)
 should be placed in the <code>lib</code> directory - not the <code>lib/ext</code> directory,
 or added to <code>user.classpath</code>.</p>
 <note>JMeter will only find <code>.jar</code> files, not <code>.zip</code>.</note>
 <p>You can also install utility Jar files in <code>$JAVA_HOME/jre/lib/ext</code>, or you can set the
 property <code>user.classpath</code> in <code>jmeter.properties</code></p>
 <p>Note that setting the <code>CLASSPATH</code> environment variable will have no effect.
 This is because JMeter is started with "<code>java -jar</code>",
 and the java command silently ignores the <code>CLASSPATH</code> variable, and the <code>-classpath</code>/<code>-cp</code>
 options when <code>-jar</code> is used.</p>
 <note>This occurs with all Java programs, not just JMeter.</note>
 </subsection>
 
 <subsection name="&sect-num;.4.2 Create Test Plan from Template" anchor="template">
 <p>You have the ability to create a new Test Plan from existing template.</p>
 <p>To do so you use the menu
 <menuchoice>
   <guimenuitem>File</guimenuitem>
   <guimenuitem>Templates&hellip;</guimenuitem>
 </menuchoice> or Templates icon:
 <figure image="template_menu.png">Templates icon item</figure>
 </p>
 <p>A popup appears, you can then choose a template among the list:
 <figure image="template_wizard.png">Templates popup</figure>
 </p>
 <p>A documentation for each template explains what to do once test plan is created from template.</p>
 </subsection>
 
 <subsection name="&sect-num;.4.3 Using JMeter behind a proxy" anchor="proxy_server">
 <p>If you are testing from behind a firewall/proxy server, you may need to provide JMeter with
 the firewall/proxy server hostname and port number.  To do so, run the <code>jmeter[.bat]</code> file
 from a command line with the following parameters:</p>
 <dl>
 <dt><code>-H</code></dt><dd>[proxy server hostname or ip address]</dd>
 <dt><code>-P</code></dt><dd>[proxy server port]</dd>
 <dt><code>-N</code></dt><dd>[nonproxy hosts] (e.g. <code>*.apache.org|localhost</code>)</dd>
 <dt><code>-u</code></dt><dd>[username for proxy authentication - if required]</dd>
 <dt><code>-a</code></dt><dd>[password for proxy authentication - if required]</dd>
 </dl>
 <b>Example</b>:
 <source>jmeter -H my.proxy.server -P 8000 -u username -a password -N localhost</source>
 <p>You can also use <code>--proxyHost</code>, <code>--proxyPort</code>, <code>--username</code>, and <code>--password</code> as parameter names</p>
 <note>
 Parameters provided on a command-line may be visible to other users on the system.
 </note>
 <p>
 If the proxy host and port are provided, then JMeter sets the following System properties:
 </p>
 <ul>
 <li><code>http.proxyHost</code></li>
 <li><code>http.proxyPort</code></li>
 <li><code>https.proxyHost</code></li>
 <li><code>https.proxyPort</code></li>
 </ul>
 If a nonproxy host list is provided, then JMeter sets the following System properties:
 <ul>
 <li><code>http.nonProxyHosts</code></li>
 <li><code>https.nonProxyHosts</code></li>
 </ul>
 <p>
 So if you don't wish to set both http and https proxies, 
 you can define the relevant properties in <code>system.properties</code> instead of using the command-line parameters.
 </p>
 <p>
 Proxy Settings can also be defined in a Test Plan, using either the <complink name="HTTP Request Defaults"/>
 configuration or the <complink name="HTTP Request"/> sampler elements.
 </p>
 <note>JMeter also has its own in-built Proxy Server, the <complink name="HTTP(S) Test Script Recorder">HTTP(S) Test Script Recorder</complink>.
 This is only used for recording HTTP or HTTPS browser sessions.
 This is not to be confused with the proxy settings described above, which are used when JMeter makes HTTP or HTTPS requests itself.</note>
 </subsection>
 
 <subsection name="&sect-num;.4.4 Non-GUI Mode (Command Line mode)" anchor="non_gui">
 <p>For load testing, you must run JMeter in this mode (Without the GUI) to get the optimal results from it. To do so, use
 the following command options:</p>
 <dl>
 <dt><code>-n</code></dt><dd>This specifies JMeter is to run in non-gui mode</dd>
 <dt><code>-t</code></dt><dd>[name of JMX file that contains the Test Plan].</dd>
 <dt><code>-l</code></dt><dd>[name of JTL file to log sample results to].</dd>
 <dt><code>-j</code></dt><dd>[name of JMeter run log file].</dd>
 <dt><code>-r</code></dt><dd>Run the test in the servers specified by the JMeter property "<code>remote_hosts</code>"</dd>
 <dt><code>-R</code></dt><dd>[list of remote servers] Run the test in the specified remote servers</dd>
 <dt><code>-g</code></dt><dd>[path to CSV file] generate report dashboard only</dd>
 <dt><code>-e</code></dt><dd>generate report dashboard after load test</dd>
 <dt><code>-o</code></dt><dd>output folder where to generate the report dashboard after load test. Folder must not exist or be empty</dd>
 </dl>
 <p>The script also lets you specify the optional firewall/proxy server information:</p>
 <dl>
 <dt><code>-H</code></dt><dd>[proxy server hostname or ip address]</dd>
 <dt><code>-P</code></dt><dd>[proxy server port]</dd>
 </dl>
 <b>Example</b>
 <source>jmeter -n -t my_test.jmx -l log.jtl -H my.proxy.server -P 8000</source>
 <p>
 If the property <code>jmeterengine.stopfail.system.exit</code> is set to <code>true</code> (default is <code>false</code>),
 then JMeter will invoke <code>System.exit(1)</code> if it cannot stop all threads.
 Normally this is not necessary.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.4.5 Server Mode" anchor="server">
 <p>For <a href="remote-test.html">distributed testing</a>, run JMeter in server mode on the remote node(s), and then control the server(s) from the GUI.
 You can also use non-GUI mode to run remote tests.
 To start the server(s), run <code>jmeter-server[.bat]</code> on each server host.</p>
 <p>The script also lets you specify the optional firewall/proxy server information:</p>
 <dl>
 <dt><code>-H</code></dt><dd>[proxy server hostname or ip address]</dd>
 <dt><code>-P</code></dt><dd>[proxy server port]</dd>
 </dl>
 <b>Example</b>:
 <source>jmeter-server -H my.proxy.server -P 8000</source>
 <p>If you want the server to exit after a single test has been run, then define the JMeter property <code>server.exitaftertest=true</code>.
 </p>
 <p>To run the test from the client in non-GUI mode, use the following command:</p>
 <source>
 jmeter -n -t testplan.jmx -r [-Gprop=val] [-Gglobal.properties] [-X]
 </source>
 where:
 <dl>
 <dt><code>-G</code></dt><dd>is used to define JMeter properties to be set in the servers</dd>
 <dt><code>-X</code></dt><dd>means exit the servers at the end of the test</dd>
 <dt><code>-Rserver1,server2</code></dt><dd>can be used instead of <code>-r</code> to provide a list of servers to start.
 Overrides <code>remote_hosts</code>, but does not define the property.</dd>
 </dl>
 <p>
 If the property <code>jmeterengine.remote.system.exit</code> is set to <code>true</code> (default is <code>false</code>),
 then JMeter will invoke <code>System.exit(0)</code> after stopping RMI at the end of a test.
 Normally this is not necessary.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.4.6 Overriding Properties Via The Command Line" anchor="override">
-<p>Java system properties, JMeter properties, and logging properties can be overridden directly on the command line
+<p>Java system properties and JMeter properties can be overridden directly on the command lin
 (instead of modifying <code>jmeter.properties</code>).
 To do so, use the following options:</p>
 <dl>
 <dt><code>-D[prop_name]=[value]</code></dt><dd>defines a java system property value.</dd>
 <dt><code>-J[prop_name]=[value]</code></dt><dd>defines a local JMeter property.</dd>
 <dt><code>-G[prop_name]=[value]</code></dt><dd>defines a JMeter property to be sent to all remote servers.</dd>
 <dt><code>-G[propertyfile]</code></dt><dd>defines a file containing JMeter properties to be sent to all remote servers.</dd>
-<dt><code>-L[category]=[priority]</code></dt><dd>overrides a logging setting, setting a particular category to the given priority level.</dd>
 </dl>
-<p>The <code>-L</code> flag can also be used without the category name to set the root logging level.</p>
 <p><b>Examples</b>:
 </p>
 <source>
 jmeter -Duser.dir=/home/mstover/jmeter_stuff \
-    -Jremote_hosts=127.0.0.1 -Ljmeter.engine=DEBUG
+    -Jremote_hosts=127.0.0.1
 </source>
 <source>jmeter -LDEBUG</source>
 <note>
     The command line properties are processed early in startup, but after the logging system has been set up.
 </note>
 </subsection>
 <subsection name="&sect-num;.4.7 Logging and error messages" anchor="logging">
     <note>
+    Since 3.2, JMeter logging is not configured through properties file(s) such as <code>jmeter.properties</code> any more,
+    but it is configured through a <a href="http://logging.apache.org/log4j/2.x/" target="_blank">Apache Log4j 2</a> configuration file
+    (<code>log4j2.xml</code> in the directory from which JMeter was launched, by default) instead.
+    Also, every code including JMeter and plugins MUST use <a href="https://www.slf4j.org/" target="_blank">SLF4J</a> library
+    to leave logs since 3.2.
+    </note>
+    <note>
+        Also, since 3.2, every code including JMeter and plugins MUST use <a href="https://www.slf4j.org/" target="_blank">SLF4J</a> library
+        to leave logs. For detail, please see <a href="https://www.slf4j.org/manual.html" target="_blank">SLF4J user manual</a>.
+    </note>
+    <p>
+        Here is an example <code>log4j2.xml</code> file which defines two log appenders and loggers for each category.
+    </p>
+<source><![CDATA[<Configuration status="WARN" packages="org.apache.jmeter.gui.logging">
+
+  <Appenders>
+
+    <!-- The main log file appender to jmeter.log in the directory from which JMeter was launched, by default. -->
+    <File name="jmeter-log" fileName="${sys:jmeter.logfile:-jmeter.log}" append="false">
+      <PatternLayout>
+        <pattern>%d %p %c{1.}: %m%n</pattern>
+      </PatternLayout>
+    </File>
+
+    <!-- Log appender for GUI Log Viewer. See below. -->
+    <GuiLogEvent name="gui-log-event">
+      <PatternLayout>
+        <pattern>%d %p %c{1.}: %m%n</pattern>
+      </PatternLayout>
+    </GuiLogEvent>
+
+  </Appenders>
+
+  <Loggers>
+
+    <!-- Root logger -->
+    <Root level="info">
+      <AppenderRef ref="jmeter-log" />
+      <AppenderRef ref="gui-log-event" />
+    </Root>
+
+    <!-- SNIP -->
+
+    <!--
+      # Apache HttpClient logging examples
+    -->
+    <!-- # Enable header wire + context logging - Best for Debugging -->
+    <!--
+    <Logger name="org.apache.http" level="debug" />
+    <Logger name="org.apache.http.wire" level="error" />
+    -->
+
+    <!-- SNIP -->
+
+  </Loggers>
+
+</Configuration>]]></source>
+    <p>
+        So, if you want to change the log level for <code>org.apache.http</code> category to debug level for instance,
+        you can simply add (or uncomment) the following logger element in <code>log4j2.xml</code> file before launching JMeter.
+    </p>
+<source><![CDATA[  <Loggers>
+    <!-- SNIP -->
+    <Logger name="org.apache.http" level="debug" />
+    <!-- SNIP -->
+  </Loggers>]]></source>
+    <p>
+        For more detail on how to configure <code>log4j2.xml</code> file,
+        please see <a href="http://logging.apache.org/log4j/2.x/manual/configuration.html" target="_blank">Apache Log4j 2 Configuration</a> page.
+    </p>
+    <p>
+        Log level for specific categories or root logger can be overridden directly on the command line (instead of modifying <code>log4j2.xml</code>) as well.
+        To do so, use the following options:
+    </p>
+    <dl>
+        <dt>
+            <code>-L[category]=[priority]</code>
+        </dt>
+        <dd>
+            Overrides a logging setting, setting a particular category to the given priority level.
+            Since 3.2, it is recommended to use a full category name (e.g, <code>org.apache.jmeter</code> or <code>com.example.foo</code>),
+            but if the category name starts with either <code>jmeter</code> or <code>jorphan</code>, <code>org.apache.</code>
+            will be prepended internally to the category name input to construct a full category name (i.e, <code>org.apache.jmeter</code> or <code>org.apache.jorphan</code>) for backward compatibility.
+        </dd>
+    </dl>
+    <p>
+        <b>Examples</b>:
+    </p>
+    <source>jmeter -Ljmeter.engine=DEBUG</source>
+    <source>jmeter -Lorg.apache.jmeter.engine=DEBUG</source>
+    <source>jmeter -Lcom.example.foo=DEBUG</source>
+    <source>jmeter -LDEBUG</source>
+    <note>
     JMeter does not generally use pop-up dialog boxes for errors, as these would interfere with
     running tests. Nor does it report any error for a mis-spelt variable or function; instead the
     reference is just used as is. See <a href="functions.html">Functions and Variables for more information</a>.
     </note>
     <p>If JMeter detects an error during a test, a message will be written to the log file.
-        The log file name is defined in the <code>jmeter.properties</code> file (or using the <code>-j</code> option, see below).
+        The log file name is defined in the <code>log4j2.xml</code> file (or using the <span class="code">-j</span> option, see below).
         It defaults to <code>jmeter.log</code>, and will be found in the directory from which JMeter was launched.
         </p>
         <p>
         The menu <menuchoice><guimenuitem>Options</guimenuitem><guimenuitem>Log Viewer</guimenuitem></menuchoice>
         displays the log file in a bottom pane on main JMeter window.
         </p>
         <p>
         In the GUI mode, the number of error/fatal messages logged in the log file is displayed at top-right.
         </p>
         <figure image="log_errors_counter.png">Error/fatal counter</figure>
         <p>
         The command-line option <code>-j jmeterlogfile</code> allow to process
         after the initial properties file is read,
         and before any further properties are processed.
         It therefore allows the default of <code>jmeter.log</code> to be overridden.
         The jmeter scripts that take a test plan name as a parameter (e.g. <code>jmeter-n.cmd</code>) have been updated
         to define the log file using the test plan name,
         e.g. for the test plan <code>Test27.jmx</code> the log file is set to <code>Test27.log</code>.
         </p>
         <p>When running on Windows, the file may appear as just <b>jmeter</b> unless you have set Windows to show file extensions.
         [Which you should do anyway, to make it easier to detect viruses and other nasties that pretend to be text files &hellip;]
         </p>
         <p>As well as recording errors, the <code>jmeter.log</code> file records some information about the test run. For example:</p>
 <source>
-10/17/2003 12:19:20 PM INFO  - jmeter.JMeter: Version 1.9.20031002 
-10/17/2003 12:19:45 PM INFO  - jmeter.gui.action.Load: Loading file: c:\mytestfiles\BSH.jmx 
-10/17/2003 12:19:52 PM INFO  - jmeter.engine.StandardJMeterEngine: Running the test! 
-10/17/2003 12:19:52 PM INFO  - jmeter.engine.StandardJMeterEngine: Starting 1 threads for group BSH. Ramp up = 1. 
-10/17/2003 12:19:52 PM INFO  - jmeter.engine.StandardJMeterEngine: Continue on error 
-10/17/2003 12:19:52 PM INFO  - jmeter.threads.JMeterThread: Thread BSH1-1 started 
-10/17/2003 12:19:52 PM INFO  - jmeter.threads.JMeterThread: Thread BSH1-1 is done 
-10/17/2003 12:19:52 PM INFO  - jmeter.engine.StandardJMeterEngine: Test has ended
+2017-03-01 12:19:20,314 INFO o.a.j.JMeter: Version 3.2.20170301
+2017-03-01 12:19:45,314 INFO o.a.j.g.a.Load: Loading file: c:\mytestfiles\BSH.jmx 
+2017-03-01 12:19:52,328 INFO o.a.j.e.StandardJMeterEngine: Running the test! 
+2017-03-01 12:19:52,384 INFO o.a.j.e.StandardJMeterEngine: Starting 1 threads for group BSH. Ramp up = 1. 
+2017-03-01 12:19:52,485 INFO o.a.j.e.StandardJMeterEngine: Continue on error 
+2017-03-01 12:19:52,589 INFO o.a.j.t.JMeterThread: Thread BSH1-1 started 
+2017-03-01 12:19:52,590 INFO o.a.j.t.JMeterThread: Thread BSH1-1 is done 
+2017-03-01 12:19:52,691 INFO o.a.j.e.StandardJMeterEngine: Test has ended
 </source>
 <p>The log file can be helpful in determining the cause of an error,
     as JMeter does not interrupt a test to display an error dialogue.</p>
 </subsection>
 <subsection name="&sect-num;.4.8 Full list of command-line options" anchor="options">
 <p>Invoking JMeter as "<code>jmeter -?</code>" will print a list of all the command-line options. 
 These are shown below.</p>
 <source>
+    --?
+        print command line options and exit
     -h, --help
         print usage information and exit
     -v, --version
         print the version information and exit
     -p, --propfile &lt;argument&gt;
         the jmeter property file to use
     -q, --addprop &lt;argument&gt;
         additional JMeter property file(s)
     -t, --testfile &lt;argument&gt;
         the jmeter test(.jmx) file to run
     -l, --logfile &lt;argument&gt;
         the file to log samples to
+    -i, --jmeterlogconf &lt;argument&gt;
+        jmeter logging configuration file (log4j2.xml)
     -j, --jmeterlogfile &lt;argument&gt;
         jmeter run log file (jmeter.log)
     -n, --nongui
         run JMeter in nongui mode
     -s, --server
         run the JMeter server
     -H, --proxyHost &lt;argument&gt;
         Set a proxy server for JMeter to use
     -P, --proxyPort &lt;argument&gt;
         Set proxy server port for JMeter to use
     -N, --nonProxyHosts &lt;argument&gt;
         Set nonproxy host list (e.g. *.apache.org|localhost)
     -u, --username &lt;argument&gt;
         Set username for proxy server that JMeter is to use
     -a, --password &lt;argument&gt;
         Set password for proxy server that JMeter is to use
     -J, --jmeterproperty &lt;argument&gt;=&lt;value&gt;
         Define additional JMeter properties
     -G, --globalproperty &lt;argument&gt;=&lt;value&gt;
         Define Global properties (sent to servers)
         e.g. -Gport=123
          or -Gglobal.properties
     -D, --systemproperty &lt;argument&gt;=&lt;value&gt;
         Define additional system properties
     -S, --systemPropertyFile &lt;argument&gt;
         additional system property file(s)
+    -f, --forceDeleteResultFile
+        force delete existing results files before start the test
     -L, --loglevel &lt;argument&gt;=&lt;value&gt;
-        [category=]level e.g. jorphan=INFO or jmeter.util=DEBUG
+        [category=]level e.g. jorphan=INFO, jmeter.util=DEBUG or com.example.foo=WARN
     -r, --runremote
         Start remote servers (as defined in remote_hosts)
     -R, --remotestart &lt;argument&gt;
         Start these remote servers (overrides remote_hosts)
     -d, --homedir &lt;argument&gt;
         the jmeter home directory to use
     -X, --remoteexit
         Exit the remote servers at end of test (non-GUI)
     -g, --reportonly &lt;argument&gt;
-        generate report dashboard only
+        generate report dashboard only, from a test results file
     -e, --reportatendofloadtests
         generate report dashboard after load test
-    -o, --reportoutputfolder
+    -o, --reportoutputfolder &lt;argument&gt;
         output folder for report dashboard
 </source>
 <p>
 Note: the JMeter log file name is formatted as a SimpleDateFormat (applied to the current date) 
 if it contains paired single-quotes, .e.g. '<code>jmeter_'yyyyMMddHHmmss'.log</code>'
 </p>
 <p>
 If the special name <code>LAST</code> is used for the <code>-t</code>, <code>-j</code> or <code>-l</code> flags,
 then JMeter takes that to mean the last test plan
 that was run in interactive mode.
 </p>
 </subsection>
 
 <subsection name="&sect-num;.4.9 non-GUI shutdown" anchor="shutdown">
 <p>
 Prior to version 2.5.1, JMeter invoked <code>System.exit()</code> when a non-GUI test completed.
 This caused problems for applications that invoke JMeter directly, so JMeter no longer invokes <code>System.exit()</code>
 for a normal test completion. [Some fatal errors may still invoke <code>System.exit()</code>]
 JMeter will exit all the non-daemon threads it starts, but it is possible that some non-daemon threads
 may still remain; these will prevent the JVM from exiting.
 To detect this situation, JMeter starts a new daemon thread just before it exits.
 This daemon thread waits a short while; if it returns from the wait, then clearly the
 JVM has not been able to exit, and the thread prints a message to say why.
 </p>
 <p>
 The property <code>jmeter.exit.check.pause</code> can be used to override the default pause of 2000ms (2secs).
 If set to <code>0</code>, then JMeter does not start the daemon thread.
 </p>
 </subsection>
 
 </section>
 
 
 <section name="&sect-num;.5 Configuring JMeter" anchor="configuring_jmeter">
 <p>If you wish to modify the properties with which JMeter runs you need to
   either modify the <code>user.properties</code> in the <code>/bin</code> directory or create
   your own copy of the <code>jmeter.properties</code> and specify it in the command line.
   </p>
   <note>
   Note: You can define additional JMeter properties in the file defined by the
   JMeter property <code>user.properties</code> which has the default value <code>user.properties</code>.
   The file will be automatically loaded if it is found in the current directory
   or if it is found in the JMeter bin directory.
   Similarly, <code>system.properties</code> is used to update system properties.
   </note>
   <properties>
     <property name="ssl.provider">You can specify the class for your SSL
     implementation if you don't want to use the built-in Java implementation.
     </property>
     <property name="xml.parser">You can specify an implementation as your XML
     parser. The default value is: <code>org.apache.xerces.parsers.SAXParser</code></property>
   <property name="remote_hosts">Comma-delimited list of remote JMeter hosts (or <code>host:port</code> if required).
     If you are running JMeter in a distributed environment, list the machines where
   you have JMeter remote servers running.  This will allow you to control those
     servers from this machine's GUI</property>
   <property name="not_in_menu">A list of components you do not want to see in
     JMeter's menus.  As JMeter has more and more components added, you may wish to
   customize your JMeter to show only those components you are interested in.
   You may list their classname or their class label (the string that appears
   in JMeter's UI) here, and they will no longer appear in the menus.</property>
   <property name="search_paths">
   List of paths (separated by <code>;</code>) that JMeter will search for JMeter plugin classes,
   for example additional samplers. A path item can either be a jar file or a directory.
   Any jar file in such a directory will be automatically included in <code>search_paths</code>,
   jar files in sub directories are ignored.
   The given value is in addition to any jars found in the <code>lib/ext</code> directory.
   </property>
   <property name="user.classpath">
   List of paths that JMeter will search for utility and plugin dependency classes.
   Use your platform path separator to separate multiple paths.
   A path item can either be a jar file or a directory.
   Any jar file in such a directory will be automatically included in <code>user.classpath</code>,
   jar files in sub directories are ignored.
   The given value is in addition to any jars found in the lib directory.
   All entries will be added to the class path of the system class loader
   and also to the path of the JMeter internal loader.
   </property>
   <property name="plugin_dependency_paths">
   List of paths (separated by <code>;</code>) that JMeter will search for utility
   and plugin dependency classes.
   A path item can either be a jar file or a directory.
   Any jar file in such a directory will be automatically included in <code>plugin_dependency_paths</code>,
   jar files in sub directories are ignored.
   The given value is in addition to any jars found in the <code>lib</code> directory
   or given by the <code>user.classpath</code> property.
   All entries will be added to the path of the JMeter internal loader only.
   For plugin dependencies using <code>plugin_dependency_paths</code> should be preferred over
   <code>user.classpath</code>.
   </property>
   <property name="user.properties">
   Name of file containing additional JMeter properties.
   These are added after the initial property file, but before the <code>-q</code> and <code>-J</code> options are processed.
   </property>
   <property name="system.properties">
   Name of file containing additional system properties.
   These are added before the <code>-S</code> and <code>-D</code> options are processed.
   </property>
   </properties>
   <p>
   The command line options and properties files are processed in the following order:
   <ol>
   <li><code>-p propfile</code></li>
   <li><code>jmeter.properties</code> (or the file from the <code>-p</code> option) is then loaded</li>
   <li><code>-j logfile</code></li>
   <li>Logging is initialised</li>
   <li><code>user.properties</code> is loaded</li>
   <li><code>system.properties</code> is loaded</li>
   <li>all other command-line options are processed</li>
   </ol>
   </p>
 <p><b>
 See also the comments in the <code>jmeter.properties</code>, <code>user.properties</code> and <code>system.properties</code> files for further information on other settings you can change.
 </b></p>
 </section>
 
 </body>
 </document>
 
