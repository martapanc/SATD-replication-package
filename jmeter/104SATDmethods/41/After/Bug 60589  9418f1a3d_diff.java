diff --git a/src/core/org/apache/jmeter/JMeter.java b/src/core/org/apache/jmeter/JMeter.java
index df912a0bf..f995da361 100644
--- a/src/core/org/apache/jmeter/JMeter.java
+++ b/src/core/org/apache/jmeter/JMeter.java
@@ -1,1324 +1,1328 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassTools;
 import org.apache.jorphan.util.HeapDumper;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
+import org.apache.logging.log4j.Level;
+import org.apache.logging.log4j.core.config.Configurator;
 
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
+    // logging configuration file
+    private static final int JMLOGCONF_OPT      = 'i';// $NON-NLS-1$
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
 
+    private static final String PACKAGE_PREFIX = "org.apache."; //$NON_NLS-1$
 
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
+    private static final CLOptionDescriptor D_JMLOGCONF_OPT =
+            new CLOptionDescriptor("jmeterlogconf", CLOptionDescriptor.ARGUMENT_REQUIRED, JMLOGCONF_OPT,
+                    "jmeter logging configuration file (log4j2.xml)");
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
+            D_JMLOGCONF_OPT,
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
         System.out.println("For load testing, use NON GUI Mode (jmeter -n -t [jmx file] -l [results file] -e -o [Path to output folder])");//NOSONAR
         System.out.println("& adapt Java Heap to your test requirements");//NOSONAR
         System.out.println("================================================================================");//NOSONAR
         
         SplashScreen splash = new SplashScreen();
         splash.showScreen();
         String jMeterLaf = LookAndFeelCommand.getJMeterLaf();
         try {
             UIManager.setLookAndFeel(jMeterLaf);
         } catch (Exception ex) {
             log.warn("Could not set LAF to:"+jMeterLaf, ex);
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
                 log.info("Loading file: " + f);
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
-            /*
-             * The following is needed for HTTPClient.
-             * (originally tried doing this in HTTPSampler2,
-             * but it appears that it was done too late when running in GUI mode)
-             * Set the commons logging default to Avalon Logkit, if not already defined
-             */
-            if (System.getProperty("org.apache.commons.logging.Log") == null) { // $NON-NLS-1$
-                System.setProperty("org.apache.commons.logging.Log" // $NON-NLS-1$
-                        , "org.apache.commons.logging.impl.LogKitLogger"); // $NON-NLS-1$
-            }
 
             Thread.setDefaultUncaughtExceptionHandler(
                     (Thread t, Throwable e) -> {
                     if (!(e instanceof ThreadDeath)) {
                         log.error("Uncaught exception: ", e);
                         System.err.println("Uncaught Exception " + e + ". See log file for details.");//NOSONAR
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
             log.info("Max memory     ="+ Runtime.getRuntime().maxMemory());
             log.info("Available Processors ="+ Runtime.getRuntime().availableProcessors());
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
             log.fatalError("An error occurred: ",e);
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
             log.info("Setting property '"+JMETER_REPORT_OUTPUT_DIR_PROPERTY+"' to:'"+reportOutputFolderAsFile.getAbsolutePath()+"'");
             JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, 
                     reportOutputFolderAsFile.getAbsolutePath());                        
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
                     NewDriver.addPath(path);
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
             t.run(); // NOSONAR we just evaluate some code here
         }
 
         // Should we run a beanshell script on startup?
         String bshinit = JMeterUtils.getProperty("beanshell.init.file");// $NON-NLS-1$
         if (bshinit != null){
             log.info("Run Beanshell on file: "+bshinit);
             try {
                 BeanShellInterpreter bsi = new BeanShellInterpreter();
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
                 String u = parser.getArgumentById(PROXY_USERNAME).getArgument();
                 String p = parser.getArgumentById(PROXY_PASSWORD).getArgument();
                 Authenticator.setDefault(new ProxyAuthenticator(u, p));
                 log.info("Set Proxy login: " + u + "/" + p);
                 jmeterProps.setProperty(HTTP_PROXY_USER, u);//for Httpclient
                 jmeterProps.setProperty(HTTP_PROXY_PASS, p);//for Httpclient
             } else {
                 String u = parser.getArgumentById(PROXY_USERNAME).getArgument();
                 Authenticator.setDefault(new ProxyAuthenticator(u, ""));
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
 
-        if (parser.getArgumentById(JMLOGFILE_OPT) != null){
-            String jmlogfile=parser.getArgumentById(JMLOGFILE_OPT).getArgument();
-            jmlogfile = processLAST(jmlogfile, ".log");// $NON-NLS-1$
-            JMeterUtils.setProperty(LoggingManager.LOG_FILE,jmlogfile);
-        }
-
-        JMeterUtils.initLogging();
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
                     log.info("Loading user properties from: "+file.getCanonicalPath());
                     Properties tmp = new Properties();
                     tmp.load(fis);
                     jmeterProps.putAll(tmp);
-                    LoggingManager.setLoggingLevels(tmp);//Do what would be done earlier
                 } catch (IOException e) {
                     log.warn("Error loading user property file: " + userProp, e);
                 }
             }
         }
 
         // Add local system properties, if the file is found
         String sysProp = JMeterUtils.getPropDefault("system.properties",""); //$NON-NLS-1$
         if (sysProp.length() > 0){
             File file = JMeterUtils.findFile(sysProp);
             if (file.canRead()) {
                 try (FileInputStream fis = new FileInputStream(file)){
                     log.info("Loading system properties from: "+file.getCanonicalPath());
                     System.getProperties().load(fis);
                 } catch (IOException e) {
                     log.warn("Error loading system property file: " + sysProp, e);
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
-                    LoggingManager.setLoggingLevels(tmp);//Do what would be done earlier
                 } catch (FileNotFoundException e) { // NOSONAR
                     log.warn("Can't find additional property file: " + name, e);
                 } catch (IOException e) { // NOSONAR
                     log.warn("Error loading additional property file: " + name, e);
                 }
                 break;
             case SYSTEM_PROPFILE:
                 log.info("Setting System properties from file: " + name);
                 try (FileInputStream fis = new FileInputStream(new File(name))){
                     System.getProperties().load(fis);
                 } catch (IOException e) { // NOSONAR
                     log.warn("Cannot find system property file " + e.getLocalizedMessage());
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
                         log.info("Setting Global properties from the file " + name);
                         try (FileInputStream fis = new FileInputStream(propFile)){
                             remoteProps.load(fis);
                         } catch (FileNotFoundException e) { // NOSONAR
                             log.warn("Could not find properties file: " + e.getLocalizedMessage());
                         } catch (IOException e) { // NOSONAR
                             log.warn("Could not load properties file: " + e.getLocalizedMessage());
                         } 
                     }
                 }
                 break;
             case LOGLEVEL:
                 if (value.length() > 0) { // Set category
                     log.info("LogLevel: " + name + "=" + value);
-                    LoggingManager.setPriority(value, name);
+                    final Level logLevel = Level.getLevel(value);
+                    if (logLevel != null) {
+                        String loggerName = name;
+                        if (name.startsWith("jmeter") || name.startsWith("jorphan")) {
+                            loggerName = PACKAGE_PREFIX + name;
+                        }
+                        Configurator.setAllLevels(loggerName, logLevel);
+                    } else {
+                        log.warn("Invalid log level, '" + value + "' for '" + name + "'.");
+                    }
                 } else { // Set root level
                     log.warn("LogLevel: " + name);
-                    LoggingManager.setPriority(name);
+                    final Level logLevel = Level.getLevel(name);
+                    if (logLevel != null) {
+                        Configurator.setRootLevel(logLevel);
+                    } else {
+                        log.warn("Invalid log level, '" + name + "' for the root logger.");
+                    }
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
                 log.info("Creating summariser <" + summariserName + ">");
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
             long now=System.currentTimeMillis();
             log.info("Finished remote host: " + host + " ("+now+")");
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
                     log.error("Error generating dashboard:"+ex.getMessage(), ex);
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
             } else if(pauseToCheckForRemainingThreads<=0 && log.isDebugEnabled()) {
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
                     log.info("Command: "+command+" received from "+address);
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
diff --git a/src/core/org/apache/jmeter/NewDriver.java b/src/core/org/apache/jmeter/NewDriver.java
index 345b449e5..ef2279c44 100644
--- a/src/core/org/apache/jmeter/NewDriver.java
+++ b/src/core/org/apache/jmeter/NewDriver.java
@@ -1,279 +1,381 @@
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
 
 // N.B. this must only use standard Java packages
 import java.io.File;
 import java.io.FilenameFilter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.lang.reflect.Method;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.security.AccessController;
+import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Arrays;
+import java.util.Date;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.StringTokenizer;
 
 /**
  * Main class for JMeter - sets up initial classpath and the loader.
  *
  */
 public final class NewDriver {
 
     private static final String CLASSPATH_SEPARATOR = File.pathSeparator;
 
     private static final String OS_NAME = System.getProperty("os.name");// $NON-NLS-1$
 
     private static final String OS_NAME_LC = OS_NAME.toLowerCase(java.util.Locale.ENGLISH);
 
     private static final String JAVA_CLASS_PATH = "java.class.path";// $NON-NLS-1$
 
     /** The class loader to use for loading JMeter classes. */
     private static final DynamicClassLoader loader;
 
     /** The directory JMeter is installed in. */
     private static final String JMETER_INSTALLATION_DIRECTORY;
 
     private static final List<Exception> EXCEPTIONS_IN_INIT = new ArrayList<>();
 
     static {
         final List<URL> jars = new LinkedList<>();
         final String initial_classpath = System.getProperty(JAVA_CLASS_PATH);
 
         // Find JMeter home dir from the initial classpath
         String tmpDir=null;
         StringTokenizer tok = new StringTokenizer(initial_classpath, File.pathSeparator);
         if (tok.countTokens() == 1
                 || (tok.countTokens()  == 2 // Java on Mac OS can add a second entry to the initial classpath
                     && OS_NAME_LC.startsWith("mac os x")// $NON-NLS-1$
                    )
            ) {
             File jar = new File(tok.nextToken());
             try {
                 tmpDir = jar.getCanonicalFile().getParentFile().getParent();
             } catch (IOException e) {
             }
         } else {// e.g. started from IDE with full classpath
             tmpDir = System.getProperty("jmeter.home","");// Allow override $NON-NLS-1$ $NON-NLS-2$
             if (tmpDir.length() == 0) {
                 File userDir = new File(System.getProperty("user.dir"));// $NON-NLS-1$
                 tmpDir = userDir.getAbsoluteFile().getParent();
             }
         }
         JMETER_INSTALLATION_DIRECTORY=tmpDir;
 
         /*
          * Does the system support UNC paths? If so, may need to fix them up
          * later
          */
         boolean usesUNC = OS_NAME_LC.startsWith("windows");// $NON-NLS-1$
 
         // Add standard jar locations to initial classpath
         StringBuilder classpath = new StringBuilder();
         File[] libDirs = new File[] { new File(JMETER_INSTALLATION_DIRECTORY + File.separator + "lib"),// $NON-NLS-1$ $NON-NLS-2$
                 new File(JMETER_INSTALLATION_DIRECTORY + File.separator + "lib" + File.separator + "ext"),// $NON-NLS-1$ $NON-NLS-2$
                 new File(JMETER_INSTALLATION_DIRECTORY + File.separator + "lib" + File.separator + "junit")};// $NON-NLS-1$ $NON-NLS-2$
         for (File libDir : libDirs) {
             File[] libJars = libDir.listFiles(new FilenameFilter() {
                 @Override
                 public boolean accept(File dir, String name) {// only accept jar files
                     return name.endsWith(".jar");// $NON-NLS-1$
                 }
             });
             if (libJars == null) {
                 new Throwable("Could not access " + libDir).printStackTrace(); // NOSONAR No logging here
                 continue;
             }
             Arrays.sort(libJars); // Bug 50708 Ensure predictable order of jars
             for (File libJar : libJars) {
                 try {
                     String s = libJar.getPath();
 
                     // Fix path to allow the use of UNC URLs
                     if (usesUNC) {
                         if (s.startsWith("\\\\") && !s.startsWith("\\\\\\")) {// $NON-NLS-1$ $NON-NLS-2$
                             s = "\\\\" + s;// $NON-NLS-1$
                         } else if (s.startsWith("//") && !s.startsWith("///")) {// $NON-NLS-1$ $NON-NLS-2$
                             s = "//" + s;// $NON-NLS-1$
                         }
                     } // usesUNC
 
                     jars.add(new File(s).toURI().toURL());// See Java bug 4496398
                     classpath.append(CLASSPATH_SEPARATOR);
                     classpath.append(s);
                 } catch (MalformedURLException e) { // NOSONAR
                     EXCEPTIONS_IN_INIT.add(new Exception("Error adding jar:"+libJar.getAbsolutePath(), e));
                 }
             }
         }
 
         // ClassFinder needs the classpath
         System.setProperty(JAVA_CLASS_PATH, initial_classpath + classpath.toString());
         loader = AccessController.doPrivileged(
                 new java.security.PrivilegedAction<DynamicClassLoader>() {
                     @Override
                     public DynamicClassLoader run() {
                         return new DynamicClassLoader(jars.toArray(new URL[jars.size()]));
                     }
                 }
         );
     }
 
     /**
      * Prevent instantiation.
      */
     private NewDriver() {
     }
 
     /**
      * Generate an array of jar files located in a directory.
      * Jar files located in sub directories will not be added.
      *
      * @param dir to search for the jar files.
      */
     private static File[] listJars(File dir) {
         if (dir.isDirectory()) {
             return dir.listFiles(new FilenameFilter() {
                 @Override
                 public boolean accept(File f, String name) {
                     if (name.endsWith(".jar")) {// $NON-NLS-1$
                         File jar = new File(f, name);
                         return jar.isFile() && jar.canRead();
                     }
                     return false;
                 }
             });
         }
         return new File[0];
     }
 
     /**
      * Add a URL to the loader classpath only; does not update the system classpath.
      *
      * @param path to be added.
      * @throws MalformedURLException 
      */
     public static void addURL(String path) throws MalformedURLException {
         File furl = new File(path);
         loader.addURL(furl.toURI().toURL()); // See Java bug 4496398
         File[] jars = listJars(furl);
         for (File jar : jars) {
             loader.addURL(jar.toURI().toURL()); // See Java bug 4496398
         }
     }
 
     /**
      * Add a URL to the loader classpath only; does not update the system
      * classpath.
      *
      * @param url
      *            The {@link URL} to add to the classpath
      */
     public static void addURL(URL url) {
         loader.addURL(url);
     }
 
     /**
      * Add a directory or jar to the loader and system classpaths.
      *
      * @param path
      *            to add to the loader and system classpath
      * @throws MalformedURLException
      *             if <code>path</code> can not be transformed to a valid
      *             {@link URL}
      */
     public static void addPath(String path) throws MalformedURLException {
         File file = new File(path);
         // Ensure that directory URLs end in "/"
         if (file.isDirectory() && !path.endsWith("/")) {// $NON-NLS-1$
             file = new File(path + "/");// $NON-NLS-1$
         }
         loader.addURL(file.toURI().toURL()); // See Java bug 4496398
         StringBuilder sb = new StringBuilder(System.getProperty(JAVA_CLASS_PATH));
         sb.append(CLASSPATH_SEPARATOR);
         sb.append(path);
         File[] jars = listJars(file);
         for (File jar : jars) {
             loader.addURL(jar.toURI().toURL()); // See Java bug 4496398
             sb.append(CLASSPATH_SEPARATOR);
             sb.append(jar.getPath());
         }
 
         // ClassFinder needs this
         System.setProperty(JAVA_CLASS_PATH,sb.toString());
     }
 
     /**
      * Get the directory where JMeter is installed. This is the absolute path
      * name.
      *
      * @return the directory where JMeter is installed.
      */
     public static String getJMeterDir() {
         return JMETER_INSTALLATION_DIRECTORY;
     }
 
     /**
      * The main program which actually runs JMeter.
      *
      * @param args
      *            the command line arguments
      */
     public static void main(String[] args) {
         if(!EXCEPTIONS_IN_INIT.isEmpty()) {
             System.err.println("Configuration error during init, see exceptions:"+exceptionsToString(EXCEPTIONS_IN_INIT));
         } else {
             Thread.currentThread().setContextClassLoader(loader);
-            if (System.getProperty("log4j.configuration") == null) {// $NON-NLS-1$ $NON-NLS-2$
-                File conf = new File(JMETER_INSTALLATION_DIRECTORY, "bin" + File.separator + "log4j.conf");// $NON-NLS-1$ $NON-NLS-2$
-                System.setProperty("log4j.configuration", "file:" + conf);
-            }
-    
+
+            setLoggingProperties(args);
+
             try {
                 Class<?> initialClass = loader.loadClass("org.apache.jmeter.JMeter");// $NON-NLS-1$
                 Object instance = initialClass.newInstance();
                 Method startup = initialClass.getMethod("start", new Class[] { new String[0].getClass() });// $NON-NLS-1$
                 startup.invoke(instance, new Object[] { args });
             } catch(Throwable e){ // NOSONAR We want to log home directory in case of exception
                 e.printStackTrace(); // NOSONAR No logger at this step
                 System.err.println("JMeter home directory was detected as: "+JMETER_INSTALLATION_DIRECTORY);
             }
         }
     }
 
     /**
      * @param exceptionsInInit List of {@link Exception}
      * @return String
      */
     private static String exceptionsToString(List<Exception> exceptionsInInit) {
         StringBuilder builder = new StringBuilder();
         for (Exception exception : exceptionsInInit) {
             StringWriter stringWriter = new StringWriter();
             PrintWriter printWriter = new PrintWriter(stringWriter);
             exception.printStackTrace(printWriter); // NOSONAR 
             builder.append(stringWriter.toString())
                 .append("\r\n");
         }
         return builder.toString();
     }
+
+    /*
+     * Set logging related system properties.
+     */
+    private static void setLoggingProperties(String[] args) {
+        String jmLogFile = getCommandLineArgument(args, (int) 'j', "jmeterlogfile");// $NON-NLS-1$ $NON-NLS-2$
+
+        if (jmLogFile != null && !jmLogFile.isEmpty()) {
+            jmLogFile = replaceDateFormatInFileName(jmLogFile);
+            System.setProperty("jmeter.logfile", jmLogFile);// $NON-NLS-1$
+        } else if (System.getProperty("jmeter.logfile") == null) {// $NON-NLS-1$
+            System.setProperty("jmeter.logfile", "jmeter.log");// $NON-NLS-1$ $NON-NLS-2$
+        }
+
+        String jmLogConf = getCommandLineArgument(args, (int) 'i', "jmeterlogconf");// $NON-NLS-1$ $NON-NLS-2$
+        File logConfFile = null;
+
+        if (jmLogConf != null && !jmLogConf.isEmpty()) {
+            logConfFile = new File(jmLogConf);
+        } else if (System.getProperty("log4j.configurationFile") == null) {// $NON-NLS-1$
+            logConfFile = new File("log4j2.xml");// $NON-NLS-1$
+            if (!logConfFile.isFile()) {
+                logConfFile = new File(JMETER_INSTALLATION_DIRECTORY, "bin" + File.separator + "log4j2.xml");// $NON-NLS-1$ $NON-NLS-2$
+            }
+        }
+
+        if (logConfFile != null) {
+            System.setProperty("log4j.configurationFile", logConfFile.toURI().toString());// $NON-NLS-1$
+        }
+    }
+
+    /*
+     * Find command line argument option value by the id and name.
+     */
+    private static String getCommandLineArgument(String [] args, int id, String name) {
+        final String shortArgName = "-" + ((char) id);// $NON-NLS-1$
+        final String longArgName = "--" + name;// $NON-NLS-1$
+
+        String value = null;
+
+        for (int i = 0; i < args.length; i++) {
+            if (shortArgName.equals(args[i]) && i < args.length - 1) {
+                if (!args[i + 1].startsWith("-")) {// $NON-NLS-1$
+                    value = args[i + 1];
+                }
+                break;
+            } else if (!shortArgName.equals(args[i]) && args[i].startsWith(shortArgName)) {
+                value = args[i].substring(shortArgName.length());
+                break;
+            } else if (longArgName.equals(args[i])) {
+                if (!args[i + 1].startsWith("-")) {// $NON-NLS-1$
+                    value = args[i + 1];
+                }
+                break;
+            }
+        }
+
+        return value;
+    }
+
+    /*
+     * If the fileName contains at least one set of paired single-quotes, reformat using DateFormat
+     */
+    private static String replaceDateFormatInFileName(String fileName) {
+        try {
+            StringBuilder builder = new StringBuilder();
+
+            final Date date = new Date();
+            int fromIndex = 0;
+            int begin = fileName.indexOf('\'', fromIndex);// $NON-NLS-1$
+            int end;
+
+            String format;
+            SimpleDateFormat dateFormat;
+
+            while (begin != -1) {
+                builder.append(fileName.substring(fromIndex, begin));
+
+                fromIndex = begin + 1;
+                end = fileName.indexOf('\'', fromIndex);// $NON-NLS-1$
+                if (end == -1) {
+                    throw new IllegalArgumentException("Invalid pairs of single-quotes in the file name: " + fileName);// $NON-NLS-1$
+                }
+
+                format = fileName.substring(begin + 1, end);
+                dateFormat = new SimpleDateFormat(format);
+                builder.append(dateFormat.format(date));
+
+                fromIndex = end + 1;
+                begin = fileName.indexOf('\'', fromIndex);// $NON-NLS-1$
+            }
+
+            if (fromIndex < fileName.length() - 1) {
+                builder.append(fileName.substring(fromIndex));
+            }
+
+            return builder.toString();
+        } catch (Exception ignored) {
+        }
+
+        return fileName;
+    }
 }
diff --git a/src/core/org/apache/jmeter/gui/GuiPackage.java b/src/core/org/apache/jmeter/gui/GuiPackage.java
index bcd16c7d5..1c9ae6efb 100644
--- a/src/core/org/apache/jmeter/gui/GuiPackage.java
+++ b/src/core/org/apache/jmeter/gui/GuiPackage.java
@@ -1,884 +1,897 @@
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
 
 package org.apache.jmeter.gui;
 
 import java.awt.Component;
 import java.awt.event.MouseEvent;
 import java.beans.Introspector;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import javax.swing.JCheckBoxMenuItem;
 import javax.swing.JOptionPane;
 import javax.swing.JPopupMenu;
 import javax.swing.JToolBar;
 import javax.swing.SwingUtilities;
 
 import org.apache.jmeter.engine.util.ValueReplacer;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.UndoHistory.HistoryListener;
 import org.apache.jmeter.gui.action.TreeNodeNamingPolicy;
 import org.apache.jmeter.gui.action.impl.DefaultTreeNodeNamingPolicy;
+import org.apache.jmeter.gui.logging.GuiLogEventBus;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.JMeterToolBar;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.TestBeanGUI;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * GuiPackage is a static class that provides convenient access to information
  * about the current state of JMeter's GUI. Any GUI class can grab a handle to
  * GuiPackage by calling the static method {@link #getInstance()} and then use
  * it to query the GUI about it's state. When actions, for instance, need to
  * affect the GUI, they typically use GuiPackage to get access to different
  * parts of the GUI.
  *
  */
 public final class GuiPackage implements LocaleChangeListener, HistoryListener {
     /** Logging. */
     private static final Logger log = LoggingManager.getLoggerForClass();
 
-    private static final String NAMING_POLICY_IMPLEMENTATION = 
-            JMeterUtils.getPropDefault("naming_policy.impl", //$NON-NLS-1$ 
-                    DefaultTreeNodeNamingPolicy.class.getName());
-
     /** Singleton instance. */
     private static GuiPackage guiPack;
 
     /**
      * Flag indicating whether or not parts of the tree have changed since they
      * were last saved.
      */
     private boolean dirty = false;
 
     /**
      * Map from TestElement to JMeterGUIComponent, mapping the nodes in the tree
      * to their corresponding GUI components.
      */
     private Map<TestElement, JMeterGUIComponent> nodesToGui = new HashMap<>();
 
     /**
      * Map from Class to JMeterGUIComponent, mapping the Class of a GUI
      * component to an instance of that component.
      */
     private Map<Class<?>, JMeterGUIComponent> guis = new HashMap<>();
 
     /**
      * Map from Class to TestBeanGUI, mapping the Class of a TestBean to an
      * instance of TestBeanGUI to be used to edit such components.
      */
     private Map<Class<?>, JMeterGUIComponent> testBeanGUIs = new HashMap<>();
 
     /** The currently selected node in the tree. */
     private JMeterTreeNode currentNode = null;
 
     private boolean currentNodeUpdated = false;
 
     /** The model for JMeter's test tree. */
     private final JMeterTreeModel treeModel;
 
     /** The listener for JMeter's test tree. */
     private final JMeterTreeListener treeListener;
 
     /** The main JMeter frame. */
     private MainFrame mainFrame;
 
     /** The main JMeter toolbar. */
     private JToolBar toolbar;
 
     /**
      * The LoggerPanel menu item
      */
     private JCheckBoxMenuItem menuItemLoggerPanel;
 
     /**
      * Logger Panel reference
      */
     private LoggerPanel loggerPanel;
 
     /**
      * History for tree states
      */
     private UndoHistory undoHistory = new UndoHistory();
 
     /**
+     * GUI Logging Event Bus.
+     */
+    private GuiLogEventBus logEventBus = new GuiLogEventBus();
+
+    /**
      * Private constructor to permit instantiation only from within this class.
      * Use {@link #getInstance()} to retrieve a singleton instance.
      */
     private GuiPackage(JMeterTreeModel treeModel, JMeterTreeListener treeListener) {
         this.treeModel = treeModel;
         if(undoHistory.isEnabled()) {
             this.treeModel.addTreeModelListener(undoHistory);
         }
         this.treeListener = treeListener;
     }
 
     /**
      * Retrieve the singleton GuiPackage instance.
      *
      * @return the GuiPackage instance (may be null, e.g in non-Gui mode)
      */
     public static GuiPackage getInstance() {
         return guiPack;
     }
     
     /**
      * Register as listener of:
      * - UndoHistory
      * - Locale Changes
      */
     public void registerAsListener() {
         if(undoHistory.isEnabled()) {
             this.undoHistory.registerHistoryListener(this);
         }
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     /**
      * When GuiPackage is requested for the first time, it should be given
      * handles to JMeter's Tree Listener and TreeModel.
      *
      * @param listener
      *            the TreeListener for JMeter's test tree
      * @param treeModel
      *            the model for JMeter's test tree
      */
     public static void initInstance(JMeterTreeListener listener, JMeterTreeModel treeModel) {
         GuiPackage guiPack = new GuiPackage(treeModel, listener);
         guiPack.undoHistory.add(treeModel, "Created");
         GuiPackage.guiPack = guiPack;
     }
    
 
     /**
      * Get a JMeterGUIComponent for the specified test element. If the GUI has
      * already been created, that instance will be returned. Otherwise, if a GUI
      * component of the same type has been created, and the component is not
      * marked as an {@link UnsharedComponent}, that shared component will be
      * returned. Otherwise, a new instance of the component will be created. The
      * TestElement's GUI_CLASS property will be used to determine the
      * appropriate type of GUI component to use.
      *
      * @param node
      *            the test element which this GUI is being created for
      *
      * @return the GUI component corresponding to the specified test element
      */
     public JMeterGUIComponent getGui(TestElement node) {
         String testClassName = node.getPropertyAsString(TestElement.TEST_CLASS);
         String guiClassName = node.getPropertyAsString(TestElement.GUI_CLASS);
         try {
             Class<?> testClass;
             if (testClassName.isEmpty()) {
                 testClass = node.getClass();
             } else {
                 testClass = Class.forName(testClassName);
             }
             Class<?> guiClass = null;
             if (!guiClassName.isEmpty()) {
                 guiClass = Class.forName(guiClassName);
             }
             return getGui(node, guiClass, testClass);
         } catch (ClassNotFoundException e) {
             log.error("Could not get GUI for " + node, e);
             return null;
         }
     }
 
     /**
      * Get a JMeterGUIComponent for the specified test element. If the GUI has
      * already been created, that instance will be returned. Otherwise, if a GUI
      * component of the same type has been created, and the component is not
      * marked as an {@link UnsharedComponent}, that shared component will be
      * returned. Otherwise, a new instance of the component will be created.
      *
      * @param node
      *            the test element which this GUI is being created for
      * @param guiClass
      *            the fully qualified class name of the GUI component which will
      *            be created if it doesn't already exist
      * @param testClass
      *            the fully qualified class name of the test elements which have
      *            to be edited by the returned GUI component
      *
      * @return the GUI component corresponding to the specified test element
      */
     public JMeterGUIComponent getGui(TestElement node, Class<?> guiClass, Class<?> testClass) {
         try {
             JMeterGUIComponent comp = nodesToGui.get(node);
             if (comp == null) {
                 comp = getGuiFromCache(guiClass, testClass);
                 nodesToGui.put(node, comp);
             }
             log.debug("Gui retrieved = " + comp);
             return comp;
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
             return null;
         }
     }
 
     /**
      * Remove a test element from the tree. This removes the reference to any
      * associated GUI component.
      *
      * @param node
      *            the test element being removed
      */
     public void removeNode(TestElement node) {
         nodesToGui.remove(node);
     }
 
     /**
      * Convenience method for grabbing the gui for the current node.
      *
      * @return the GUI component associated with the currently selected node
      */
     public JMeterGUIComponent getCurrentGui() {
         try {
             updateCurrentNode();
             TestElement curNode = treeListener.getCurrentNode().getTestElement();
             JMeterGUIComponent comp = getGui(curNode);
             comp.clearGui();
             log.debug("Updating gui to new node");
             comp.configure(curNode);
             currentNodeUpdated = false;
             return comp;
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
             return null;
         }
     }
 
     /**
      * Find the JMeterTreeNode for a certain TestElement object.
      *
      * @param userObject
      *            the test element to search for
      * @return the tree node associated with the test element
      */
     public JMeterTreeNode getNodeOf(TestElement userObject) {
         return treeModel.getNodeOf(userObject);
     }
 
     /**
      * Create a TestElement corresponding to the specified GUI class.
      *
      * @param guiClass
      *            the fully qualified class name of the GUI component or a
      *            TestBean class for TestBeanGUIs.
      * @param testClass
      *            the fully qualified class name of the test elements edited by
      *            this GUI component.
      * @return the test element corresponding to the specified GUI class.
      */
     public TestElement createTestElement(Class<?> guiClass, Class<?> testClass) {
         try {
             JMeterGUIComponent comp = getGuiFromCache(guiClass, testClass);
             comp.clearGui();
             TestElement node = comp.createTestElement();
             nodesToGui.put(node, comp);
             return node;
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
             return null;
         }
     }
 
     /**
      * Create a TestElement for a GUI or TestBean class.
      * <p>
      * This is a utility method to help actions do with one single String
      * parameter.
      *
      * @param objClass
      *            the fully qualified class name of the GUI component or of the
      *            TestBean subclass for which a TestBeanGUI is wanted.
      * @return the test element corresponding to the specified GUI class.
      */
     public TestElement createTestElement(String objClass) {
         JMeterGUIComponent comp;
         Class<?> c;
         try {
             c = Class.forName(objClass);
             if (TestBean.class.isAssignableFrom(c)) {
                 comp = getGuiFromCache(TestBeanGUI.class, c);
             } else {
                 comp = getGuiFromCache(c, null);
             }
             comp.clearGui();
             TestElement node = comp.createTestElement();
             nodesToGui.put(node, comp);
             return node;
         } catch (NoClassDefFoundError e) {
             log.error("Problem retrieving gui for " + objClass, e);
             String msg="Cannot find class: "+e.getMessage();
             JOptionPane.showMessageDialog(null,
                     msg,
                     "Missing jar? See log file." ,
                     JOptionPane.ERROR_MESSAGE);
             throw new RuntimeException(e.toString(), e); // Probably a missing jar
         } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error: bail out.
         }
     }
 
     /**
      * Get an instance of the specified JMeterGUIComponent class. If an instance
      * of the GUI class has previously been created and it is not marked as an
      * {@link UnsharedComponent}, that shared instance will be returned.
      * Otherwise, a new instance of the component will be created, and shared
      * components will be cached for future retrieval.
      *
      * @param guiClass
      *            the fully qualified class name of the GUI component. This
      *            class must implement JMeterGUIComponent.
      * @param testClass
      *            the fully qualified class name of the test elements edited by
      *            this GUI component. This class must implement TestElement.
      * @return an instance of the specified class
      *
      * @throws InstantiationException
      *             if an instance of the object cannot be created
      * @throws IllegalAccessException
      *             if access rights do not allow the default constructor to be
      *             called
      */
     private JMeterGUIComponent getGuiFromCache(Class<?> guiClass, Class<?> testClass) throws InstantiationException,
             IllegalAccessException {
         JMeterGUIComponent comp;
         if (guiClass == TestBeanGUI.class) {
             comp = testBeanGUIs.get(testClass);
             if (comp == null) {
                 comp = new TestBeanGUI(testClass);
                 testBeanGUIs.put(testClass, comp);
             }
         } else {
             comp = guis.get(guiClass);
             if (comp == null) {
                 comp = (JMeterGUIComponent) guiClass.newInstance();
                 if (!(comp instanceof UnsharedComponent)) {
                     guis.put(guiClass, comp);
                 }
             }
         }
         return comp;
     }
 
     /**
      * Update the GUI for the currently selected node. The GUI component is
      * configured to reflect the settings in the current tree node.
      */
     public void updateCurrentGui() {
         updateCurrentNode();
         refreshCurrentGui();
     }
 
     /**
      * Refresh GUI from node state. 
      * This method does not update the current node from GUI at the 
      * difference of {@link GuiPackage#updateCurrentGui()}
      */
     public void refreshCurrentGui() {
         currentNode = treeListener.getCurrentNode();
         TestElement element = currentNode.getTestElement();
         JMeterGUIComponent comp = getGui(element);
         comp.configure(element);
         currentNodeUpdated = false;
     }
 
     /**
      * This method should be called in order for GuiPackage to change the
      * current node. This will save any changes made to the earlier node before
      * choosing the new node.
      */
     public void updateCurrentNode() {
         
         try {
             if (currentNode != null && !currentNodeUpdated) {
                 log.debug("Updating current node " + currentNode.getName());
                 JMeterGUIComponent comp = getGui(currentNode.getTestElement());
                 TestElement el = currentNode.getTestElement();
                 int before = 0;
                 int after = 0;
                 final boolean historyEnabled = undoHistory.isEnabled();
                 if(historyEnabled) {
                     before = getTestElementCheckSum(el);
                 }
                 comp.modifyTestElement(el);
                 if(historyEnabled) {
                     after = getTestElementCheckSum(el);
                 }
                 if (!historyEnabled || (before != after)) {
                     currentNode.nameChanged(); // Bug 50221 - ensure label is updated
                 }
             }
             // The current node is now updated
             currentNodeUpdated = true;
             currentNode = treeListener.getCurrentNode();
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
         }
     }
 
     public JMeterTreeNode getCurrentNode() {
         return treeListener.getCurrentNode();
     }
 
     public TestElement getCurrentElement() {
         return getCurrentNode().getTestElement();
     }
 
     /**
      * The dirty property is a flag that indicates whether there are parts of
      * JMeter's test tree that the user has not saved since last modification.
      * Various (@link Command actions) set this property when components are
      * modified/created/saved.
      *
      * @param dirty
      *            the new value of the dirty flag
      */
     public void setDirty(boolean dirty) {
         this.dirty = dirty;
     }
 
     /**
      * Retrieves the state of the 'dirty' property, a flag that indicates if
      * there are test tree components that have been modified since they were
      * last saved.
      *
      * @return true if some tree components have been modified since they were
      *         last saved, false otherwise
      */
     public boolean isDirty() {
         return dirty;
     }
 
     /**
      * Add a subtree to the currently selected node.
      *
      * @param subTree
      *            the subtree to add.
      *
      * @return the resulting subtree starting with the currently selected node
      *
      * @throws IllegalUserActionException
      *             if a subtree cannot be added to the currently selected node
      */
     public HashTree addSubTree(HashTree subTree) throws IllegalUserActionException {
         HashTree hashTree = treeModel.addSubTree(subTree, treeListener.getCurrentNode());
         undoHistory.clear();
         undoHistory.add(this.treeModel, "Loaded tree");
         return hashTree;
     }
 
     /**
      * Get the currently selected subtree.
      *
      * @return the subtree of the currently selected node
      */
     public HashTree getCurrentSubTree() {
         return treeModel.getCurrentSubTree(treeListener.getCurrentNode());
     }
 
     /**
      * Get the model for JMeter's test tree.
      *
      * @return the JMeter tree model
      */
     /*
      * TODO consider removing this method, and providing method wrappers instead.
      * This would allow the Gui package to do any additional clearups if required,
      * as has been done with clearTestPlan()
     */
     public JMeterTreeModel getTreeModel() {
         return treeModel;
     }
 
     /**
      * Get a ValueReplacer for the test tree.
      *
      * @return a ValueReplacer configured for the test tree
      */
     public ValueReplacer getReplacer() {
         return new ValueReplacer((TestPlan) ((JMeterTreeNode) getTreeModel().getTestPlan().getArray()[0])
                 .getTestElement());
     }
 
     /**
      * Set the main JMeter frame.
      *
      * @param newMainFrame
      *            the new JMeter main frame
      */
     public void setMainFrame(MainFrame newMainFrame) {
         mainFrame = newMainFrame;
     }
 
     /**
      * Get the main JMeter frame.
      *
      * @return the main JMeter frame
      */
     public MainFrame getMainFrame() {
         return mainFrame;
     }
 
     /**
      * Get the listener for JMeter's test tree.
      *
      * @return the JMeter test tree listener
      */
     public JMeterTreeListener getTreeListener() {
         return treeListener;
     }
 
     /**
      * Set the main JMeter toolbar.
      *
      * @param newToolbar
      *            the new JMeter main toolbar
      */
     public void setMainToolbar(JToolBar newToolbar) {
         toolbar = newToolbar;
     }
 
     /**
      * Get the main JMeter toolbar.
      *
      * @return the main JMeter toolbar
      */
     public JToolBar getMainToolbar() {
         return toolbar;
     }
 
 
     /**
      * Display the specified popup menu with the source component and location
      * from the specified mouse event.
      *
      * @param e
      *            the mouse event causing this popup to be displayed
      * @param popup
      *            the popup menu to display
      */
     public void displayPopUp(MouseEvent e, JPopupMenu popup) {
         displayPopUp((Component) e.getSource(), e, popup);
     }
 
     /**
      * Display the specified popup menu at the location specified by a mouse
      * event with the specified source component.
      *
      * @param invoker
      *            the source component
      * @param e
      *            the mouse event causing this popup to be displayed
      * @param popup
      *            the popup menu to display
      */
     public void displayPopUp(Component invoker, MouseEvent e, JPopupMenu popup) {
         if (popup != null) {
             log.debug("Showing pop up for " + invoker + " at x,y = " + e.getX() + "," + e.getY());
 
             popup.pack();
             popup.show(invoker, e.getX(), e.getY());
             popup.setVisible(true);
             popup.requestFocusInWindow();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void localeChanged(LocaleChangeEvent event) {
         // FIrst make sure we save the content of the current GUI (since we
         // will flush it away):
         updateCurrentNode();
 
         // Forget about all GUIs we've created so far: we'll need to re-created
         // them all!
         guis = new HashMap<>();
         nodesToGui = new HashMap<>();
         testBeanGUIs = new HashMap<>();
 
         // BeanInfo objects also contain locale-sensitive data -- flush them
         // away:
         Introspector.flushCaches();
 
         // Now put the current GUI in place. [This code was copied from the
         // EditCommand action -- we can't just trigger the action because that
         // would populate the current node with the contents of the new GUI --
         // which is empty.]
         MainFrame mf = getMainFrame(); // Fetch once
         if (mf == null) // Probably caused by unit testing on headless system
         {
             log.warn("Mainframe is null");
         } else {
             mf.setMainPanel((javax.swing.JComponent) getCurrentGui());
             mf.setEditMenu(getTreeListener().getCurrentNode().createPopupMenu());
         }
     }
 
     private String testPlanFile;
 
     private final List<Stoppable> stoppables = Collections.synchronizedList(new ArrayList<Stoppable>());
 
     private TreeNodeNamingPolicy namingPolicy;
 
     /**
      * Sets the filepath of the current test plan. It's shown in the main frame
      * title and used on saving.
      *
      * @param f
      *            The filepath of the current test plan
      */
     public void setTestPlanFile(String f) {
         testPlanFile = f;
         getMainFrame().setExtendedFrameTitle(testPlanFile);
         // Enable file revert action if a file is used
         getMainFrame().setFileRevertEnabled(f != null);
         getMainFrame().setProjectFileLoaded(f);
 
         try {
             FileServer.getFileServer().setBasedir(testPlanFile);
         } catch (IllegalStateException e1) {
             log.error("Failure setting file server's base dir", e1);
         }
     }
 
     public String getTestPlanFile() {
         return testPlanFile;
     }
 
     /**
      * Clears the test plan and associated objects.
      * Clears the test plan file name.
      */
     public void clearTestPlan() {
         getTreeModel().clearTestPlan();
         nodesToGui.clear();
         setTestPlanFile(null);
         undoHistory.clear();
         undoHistory.add(this.treeModel, "Initial Tree");
     }
 
     /**
      * Clears the test plan element and associated object
      *
      * @param element to clear
      */
     public void clearTestPlan(TestElement element) {
         getTreeModel().clearTestPlan(element);
         removeNode(element);
         undoHistory.clear();
         undoHistory.add(this.treeModel, "Initial Tree");
     }
 
     public static void showErrorMessage(final String message, final String title){
         showMessage(message,title,JOptionPane.ERROR_MESSAGE);
     }
 
     public static void showInfoMessage(final String message, final String title){
         showMessage(message,title,JOptionPane.INFORMATION_MESSAGE);
     }
 
     public static void showWarningMessage(final String message, final String title){
         showMessage(message,title,JOptionPane.WARNING_MESSAGE);
     }
 
     public static void showMessage(final String message, final String title, final int type){
         if (guiPack == null) {
             return ;
         }
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 JOptionPane.showMessageDialog(null,message,title,type);
             }
         });
 
     }
 
     /**
      * Unregister stoppable
      * @param stoppable Stoppable
      */
     public void unregister(Stoppable stoppable) {
         for (Iterator<Stoppable> iterator = stoppables .iterator(); iterator.hasNext();) {
             Stoppable stopable = iterator.next();
             if(stopable == stoppable)
             {
                 iterator.remove();
             }
         }
     }
 
     /**
      * Register process to stop on reload
      * 
      * @param stoppable
      *            The {@link Stoppable} to be registered
      */
     public void register(Stoppable stoppable) {
         stoppables.add(stoppable);
     }
 
     /**
      *
      * @return copy of list of {@link Stoppable}s
      */
     public List<Stoppable> getStoppables() {
         List<Stoppable> list = new ArrayList<>();
         list.addAll(stoppables);
         return list;
     }
 
     /**
      * Set the menu item LoggerPanel.
      * @param menuItemLoggerPanel The menu item LoggerPanel
      */
     public void setMenuItemLoggerPanel(JCheckBoxMenuItem menuItemLoggerPanel) {
         this.menuItemLoggerPanel = menuItemLoggerPanel;
     }
 
     /**
      * Get the menu item LoggerPanel.
      *
      * @return the menu item LoggerPanel
      */
     public JCheckBoxMenuItem getMenuItemLoggerPanel() {
         return menuItemLoggerPanel;
     }
 
     /**
      * @param loggerPanel LoggerPanel
      */
     public void setLoggerPanel(LoggerPanel loggerPanel) {
         this.loggerPanel = loggerPanel;
     }
 
     /**
      * @return the loggerPanel
      */
     public LoggerPanel getLoggerPanel() {
         return loggerPanel;
     }
 
     /**
      * Navigate back and forward through undo history
      *
      * @param offset int
      */
     public void goInHistory(int offset) {
         undoHistory.moveInHistory(offset, this.treeModel);
     }
 
     /**
      * @return true if history contains redo item
      */
     public boolean canRedo() {
         return undoHistory.canRedo();
     }
 
     /**
      * @return true if history contains undo item
      */
     public boolean canUndo() {
         return undoHistory.canUndo();
     }
 
     /**
      * Compute checksum of TestElement to detect changes
      * the method calculates properties checksum to detect testelement
      * modifications
      * TODO would be better to override hashCode for TestElement, but I decided not to touch it
      *
      * @param el {@link TestElement}
      * @return int checksum
      */
     private int getTestElementCheckSum(TestElement el) {
         int ret = el.getClass().hashCode();
         PropertyIterator it = el.propertyIterator();
         while (it.hasNext()) {
             JMeterProperty obj = it.next();
             if (obj instanceof TestElementProperty) {
                 ret ^= getTestElementCheckSum(((TestElementProperty) obj)
                         .getElement());
             } else {
                 ret ^= obj.getName().hashCode();
                 String stringValue = obj.getStringValue();
                 if(stringValue != null) {
                     ret ^= stringValue.hashCode();
                 } else {
                     if(log.isDebugEnabled()) {
                         log.debug("obj.getStringValue() returned null for test element:"
                                 +el.getName()+" at property:"+obj.getName());
                     }
                 }
             }
         }
         return ret;
     }
 
     /**
      * Called when history changes, it updates toolbar
      */
     @Override
     public void notifyChangeInHistory(UndoHistory history) {
         ((JMeterToolBar)toolbar).updateUndoRedoIcons(history.canUndo(), history.canRedo());
     }
 
     /**
      * @return {@link TreeNodeNamingPolicy}
      */
     public TreeNodeNamingPolicy getNamingPolicy() {
         if(namingPolicy == null) {
+            final String NAMING_POLICY_IMPLEMENTATION = 
+                    JMeterUtils.getPropDefault("naming_policy.impl", //$NON-NLS-1$ 
+                            DefaultTreeNodeNamingPolicy.class.getName());
+
             try {
                 Class<?> implementationClass = Class.forName(NAMING_POLICY_IMPLEMENTATION);
                 this.namingPolicy = (TreeNodeNamingPolicy) implementationClass.newInstance();
                 
             } catch (Exception ex) {
                 log.error("Failed to create configured naming policy:"+NAMING_POLICY_IMPLEMENTATION+", will use default one", ex);
                 this.namingPolicy = new DefaultTreeNodeNamingPolicy();
             }
         }
         return namingPolicy;
     }
 
+    /**
+     * Return {@link GuiLogEventBus}.
+     * @return {@link GuiLogEventBus}
+     */
+    public GuiLogEventBus getLogEventBus() {
+        return logEventBus;
+    }
 }
diff --git a/src/core/org/apache/jmeter/gui/LoggerPanel.java b/src/core/org/apache/jmeter/gui/LoggerPanel.java
index 2bf84da72..4c77751a8 100644
--- a/src/core/org/apache/jmeter/gui/LoggerPanel.java
+++ b/src/core/org/apache/jmeter/gui/LoggerPanel.java
@@ -1,127 +1,122 @@
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
 
 package org.apache.jmeter.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Insets;
 
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.ScrollPaneConstants;
 import javax.swing.SwingUtilities;
 
+import org.apache.jmeter.gui.logging.GuiLogEventListener;
+import org.apache.jmeter.gui.logging.LogEventObject;
 import org.apache.jmeter.gui.util.JSyntaxTextArea;
 import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.LogEvent;
-import org.apache.log.LogTarget;
-import org.apache.log.format.PatternFormatter;
 import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
 
 /**
  * Panel that shows log events
  */
-public class LoggerPanel extends JPanel implements LogTarget {
+public class LoggerPanel extends JPanel implements GuiLogEventListener {
 
     private static final long serialVersionUID = 6911128494402594429L;
 
     private final JTextArea textArea;
 
-    private final PatternFormatter format;
-
     // Limit length of log content
     private static final int LOGGER_PANEL_MAX_LENGTH =
             JMeterUtils.getPropDefault("jmeter.loggerpanel.maxlength", 80000); // $NON-NLS-1$
     
     // Make panel handle event even if closed
     private static final boolean LOGGER_PANEL_RECEIVE_WHEN_CLOSED =
             JMeterUtils.getPropDefault("jmeter.loggerpanel.enable_when_closed", true); // $NON-NLS-1$
 
     /**
      * Pane for display JMeter log file
      */
     public LoggerPanel() {
         textArea = init();
-        format = new PatternFormatter(LoggingManager.DEFAULT_PATTERN + "\n"); // $NON-NLS-1$
     }
 
     private JTextArea init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         this.setLayout(new BorderLayout());
         final JScrollPane areaScrollPane;
         final JTextArea jTextArea;
 
         if (JMeterUtils.getPropDefault("loggerpanel.usejsyntaxtext", true)) {
             // JSyntax Text Area
             JSyntaxTextArea jSyntaxTextArea = JSyntaxTextArea.getInstance(15, 80, true);
             jSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
             jSyntaxTextArea.setCodeFoldingEnabled(false);
             jSyntaxTextArea.setAntiAliasingEnabled(false);
             jSyntaxTextArea.setEditable(false);
             jSyntaxTextArea.setLineWrap(false);
             jSyntaxTextArea.setLanguage("text");
             jSyntaxTextArea.setMargin(new Insets(2, 2, 2, 2)); // space between borders and text
             areaScrollPane = JTextScrollPane.getInstance(jSyntaxTextArea);
             jTextArea = jSyntaxTextArea;
         } else {
             // Plain text area
             jTextArea =  new JTextArea(15, 80);
             areaScrollPane = new JScrollPane(jTextArea);
         }
 
         areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
         areaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         this.add(areaScrollPane, BorderLayout.CENTER);
         return jTextArea;
     }
 
     /* (non-Javadoc)
-     * @see org.apache.log.LogTarget#processEvent(org.apache.log.LogEvent)
+     * @see org.apache.jmeter.gui.logging.GuiLogEventListener#processLogEvent(org.apache.jmeter.gui.logging.LogEventObject)
      */
     @Override
-    public void processEvent(final LogEvent logEvent) {
+    public void processLogEvent(final LogEventObject logEventObject) {
         if(!LOGGER_PANEL_RECEIVE_WHEN_CLOSED && !GuiPackage.getInstance().getMenuItemLoggerPanel().getModel().isSelected()) {
             return;
         }
-        
+
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 synchronized (textArea) {
-                    textArea.append(format.format(logEvent));
+                    textArea.append(logEventObject.toString());
                     int currentLength = textArea.getText().length();
                     // If LOGGER_PANEL_MAX_LENGTH is 0, it means all log events are kept
                     if(LOGGER_PANEL_MAX_LENGTH != 0 && currentLength> LOGGER_PANEL_MAX_LENGTH) {
                         textArea.setText(textArea.getText().substring(Math.max(0, currentLength-LOGGER_PANEL_MAX_LENGTH), 
                                 currentLength));
                     }
                     textArea.setCaretPosition(textArea.getText().length());
                 }
             }
         });
     }
 
     /**
      * Clear panel content
      */
     public void clear() {
         this.textArea.setText(""); // $NON-NLS-1$
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/MainFrame.java b/src/core/org/apache/jmeter/gui/MainFrame.java
index 079da2075..ba6c23919 100644
--- a/src/core/org/apache/jmeter/gui/MainFrame.java
+++ b/src/core/org/apache/jmeter/gui/MainFrame.java
@@ -1,894 +1,891 @@
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
 
 package org.apache.jmeter.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Cursor;
 import java.awt.Dimension;
 import java.awt.Insets;
 import java.awt.Toolkit;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.Transferable;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.awt.dnd.DnDConstants;
 import java.awt.dnd.DropTarget;
 import java.awt.dnd.DropTargetDragEvent;
 import java.awt.dnd.DropTargetDropEvent;
 import java.awt.dnd.DropTargetEvent;
 import java.awt.dnd.DropTargetListener;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.MouseEvent;
 import java.awt.event.WindowAdapter;
 import java.awt.event.WindowEvent;
 import java.io.File;
 import java.io.IOException;
 import java.lang.reflect.Field;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import javax.swing.AbstractAction;
 import javax.swing.Action;
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.DropMode;
 import javax.swing.ImageIcon;
 import javax.swing.InputMap;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JDialog;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTree;
 import javax.swing.KeyStroke;
 import javax.swing.MenuElement;
 import javax.swing.SwingUtilities;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.TreeCellRenderer;
 import javax.swing.tree.TreeModel;
 import javax.swing.tree.TreePath;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.gui.action.LoadDraggedFile;
+import org.apache.jmeter.gui.logging.GuiLogEventListener;
+import org.apache.jmeter.gui.logging.LogEventObject;
 import org.apache.jmeter.gui.tree.JMeterCellRenderer;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.tree.JMeterTreeTransferHandler;
 import org.apache.jmeter.gui.util.EscapeDialog;
 import org.apache.jmeter.gui.util.JMeterMenuBar;
 import org.apache.jmeter.gui.util.JMeterToolBar;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.LogEvent;
-import org.apache.log.LogTarget;
 import org.apache.log.Logger;
-import org.apache.log.Priority;
 
 /**
  * The main JMeter frame, containing the menu bar, test tree, and an area for
  * JMeter component GUIs.
  *
  */
 public class MainFrame extends JFrame implements TestStateListener, Remoteable, DropTargetListener, Clearable, ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     // This is used to keep track of local (non-remote) tests
     // The name is chosen to be an unlikely host-name
     public static final String LOCAL = "*local*"; // $NON-NLS-1$
 
     // The application name
     private static final String DEFAULT_APP_NAME = "Apache JMeter"; // $NON-NLS-1$
 
     // The default title for the Menu bar
     private static final String DEFAULT_TITLE = DEFAULT_APP_NAME +
             " (" + JMeterUtils.getJMeterVersion() + ")"; // $NON-NLS-1$ $NON-NLS-2$
 
     // Allow display/hide LoggerPanel
     private static final boolean DISPLAY_LOGGER_PANEL =
             JMeterUtils.getPropDefault("jmeter.loggerpanel.display", false); // $NON-NLS-1$
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** The menu bar. */
     private JMeterMenuBar menuBar;
 
     /** The main panel where components display their GUIs. */
     private JScrollPane mainPanel;
 
     /** The panel where the test tree is shown. */
     private JScrollPane treePanel;
 
     /** The LOG panel. */
     private LoggerPanel logPanel;
 
     /** The test tree. */
     private JTree tree;
 
     private final String iconSize = JMeterUtils.getPropDefault(JMeterToolBar.TOOLBAR_ICON_SIZE, JMeterToolBar.DEFAULT_TOOLBAR_ICON_SIZE); 
 
     /** An image which is displayed when a test is running. */
     private final ImageIcon runningIcon = JMeterUtils.getImage("status/" + iconSize +"/user-online-2.png");// $NON-NLS-1$
 
     /** An image which is displayed when a test is not currently running. */
     private final ImageIcon stoppedIcon = JMeterUtils.getImage("status/" + iconSize +"/user-offline-2.png");// $NON-NLS-1$
     
     /** An image which is displayed to indicate FATAL, ERROR or WARNING. */
     private final ImageIcon warningIcon = JMeterUtils.getImage("status/" + iconSize +"/pictogram-din-w000-general.png");// $NON-NLS-1$
 
     /** The button used to display the running/stopped image. */
     private JButton runningIndicator;
 
     /** The set of currently running hosts. */
     private final Set<String> hosts = new HashSet<>();
 
     /** A message dialog shown while JMeter threads are stopping. */
     private JDialog stoppingMessage;
 
     private JLabel totalThreads;
     private JLabel activeThreads;
 
     private JMeterToolBar toolbar;
 
     /**
      * Label at top right showing test duration
      */
     private JLabel testTimeDuration;
 
     /**
      * Indicator for Log errors and Fatals
      */
     private JButton warnIndicator;
     /**
      * Counter
      */
     private JLabel errorsOrFatalsLabel;
     /**
      * LogTarget that receives ERROR or FATAL
      */
     private transient ErrorsAndFatalsCounterLogTarget errorsAndFatalsCounterLogTarget;
     
     private javax.swing.Timer computeTestDurationTimer = new javax.swing.Timer(1000, 
             this::computeTestDuration);
 
     /**
      * Create a new JMeter frame.
      *
      * @param treeModel
      *            the model for the test tree
      * @param treeListener
      *            the listener for the test tree
      */
     public MainFrame(TreeModel treeModel, JMeterTreeListener treeListener) {
 
         // TODO: Make the running indicator its own class instead of a JButton
         runningIndicator = new JButton(stoppedIcon);
         runningIndicator.setFocusable(false);
         runningIndicator.setBorderPainted(false);
         runningIndicator.setContentAreaFilled(false);
         runningIndicator.setMargin(new Insets(0, 0, 0, 0));
 
         testTimeDuration = new JLabel("00:00:00"); //$NON-NLS-1$
         testTimeDuration.setToolTipText(JMeterUtils.getResString("duration_tooltip")); //$NON-NLS-1$
         testTimeDuration.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
 
         totalThreads = new JLabel("0"); // $NON-NLS-1$
         totalThreads.setToolTipText(JMeterUtils.getResString("total_threads_tooltip")); // $NON-NLS-1$
 
         activeThreads = new JLabel("0"); // $NON-NLS-1$
         activeThreads.setToolTipText(JMeterUtils.getResString("active_threads_tooltip")); // $NON-NLS-1$
 
         warnIndicator = new JButton(warningIcon);
         warnIndicator.setMargin(new Insets(0, 0, 0, 0));
         // Transparent JButton with no border
         warnIndicator.setOpaque(false);
         warnIndicator.setContentAreaFilled(false);
         warnIndicator.setBorderPainted(false);
         warnIndicator.setCursor(new Cursor(Cursor.HAND_CURSOR));
         warnIndicator.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
         warnIndicator.addActionListener(this);
 
         errorsOrFatalsLabel = new JLabel("0"); // $NON-NLS-1$
         errorsOrFatalsLabel.setToolTipText(JMeterUtils.getResString("error_indicator_tooltip")); // $NON-NLS-1$
 
         tree = makeTree(treeModel, treeListener);
 
         GuiPackage.getInstance().setMainFrame(this);
         init();
         initTopLevelDndHandler();
         setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
     }
 
     protected void computeTestDuration(ActionEvent evt) {
         long startTime = JMeterContextService.getTestStartTime();
         if (startTime > 0) {
             long elapsedSec = (System.currentTimeMillis()-startTime + 500) / 1000; // rounded seconds
             testTimeDuration.setText(JOrphanUtils.formatDuration(elapsedSec));
         }
     }
 
     /**
      * Default constructor for the JMeter frame. This constructor will not
      * properly initialize the tree, so don't use it.
      *
      * @deprecated Do not use - only needed for JUnit tests
      */
     @Deprecated
     public MainFrame() {
     }
 
     // MenuBar related methods
     // TODO: Do we really need to have all these menubar methods duplicated
     // here? Perhaps we can make the menu bar accessible through GuiPackage?
 
     /**
      * Specify whether or not the File|Load menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileLoadEnabled(boolean enabled) {
         menuBar.setFileLoadEnabled(enabled);
     }
 
     /**
      * Specify whether or not the File|Save menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileSaveEnabled(boolean enabled) {
         menuBar.setFileSaveEnabled(enabled);
     }
 
     /**
      * Specify whether or not the File|Revert item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setFileRevertEnabled(boolean enabled) {
         menuBar.setFileRevertEnabled(enabled);
     }
 
     /**
      * Specify the project file that was just loaded
      *
      * @param file - the full path to the file that was loaded
      */
     public void setProjectFileLoaded(String file) {
         menuBar.setProjectFileLoaded(file);
     }
 
     /**
      * Set the menu that should be used for the Edit menu.
      *
      * @param menu
      *            the new Edit menu
      */
     public void setEditMenu(JPopupMenu menu) {
         menuBar.setEditMenu(menu);
     }
 
     /**
      * Specify whether or not the Edit menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setEditEnabled(boolean enabled) {
         menuBar.setEditEnabled(enabled);
     }
 
     /**
      * Set the menu that should be used for the Edit|Add menu.
      *
      * @param menu
      *            the new Edit|Add menu
      */
     public void setEditAddMenu(JMenu menu) {
         menuBar.setEditAddMenu(menu);
     }
 
     /**
      * Specify whether or not the Edit|Add menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setEditAddEnabled(boolean enabled) {
         menuBar.setEditAddEnabled(enabled);
     }
 
     /**
      * Close the currently selected menu.
      */
     public void closeMenu() {
         if (menuBar.isSelected()) {
             MenuElement[] menuElement = menuBar.getSubElements();
             if (menuElement != null) {
                 for (MenuElement element : menuElement) {
                     JMenu menu = (JMenu) element;
                     if (menu.isSelected()) {
                         menu.setPopupMenuVisible(false);
                         menu.setSelected(false);
                         break;
                     }
                 }
             }
         }
     }
 
     /**
      * Show a dialog indicating that JMeter threads are stopping on a particular
      * host.
      *
      * @param host
      *            the host where JMeter threads are stopping
      */
     public void showStoppingMessage(String host) {
         if (stoppingMessage != null){
             stoppingMessage.dispose();
         }
         stoppingMessage = new EscapeDialog(this, JMeterUtils.getResString("stopping_test_title"), true); //$NON-NLS-1$
         String label = JMeterUtils.getResString("stopping_test"); //$NON-NLS-1
         if (!StringUtils.isEmpty(host)) {
             label = label + JMeterUtils.getResString("stopping_test_host")+ ": " + host;
         }
         JLabel stopLabel = new JLabel(label); //$NON-NLS-1$$NON-NLS-2$
         stopLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         stoppingMessage.getContentPane().add(stopLabel);
         stoppingMessage.pack();
         ComponentUtil.centerComponentInComponent(this, stoppingMessage);
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 if (stoppingMessage != null) { // TODO - how can this be null?
                     stoppingMessage.setVisible(true);
                 }
             }
         });
     }
 
     public void updateCounts() {
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 activeThreads.setText(Integer.toString(JMeterContextService.getNumberOfThreads()));
                 totalThreads.setText(Integer.toString(JMeterContextService.getTotalThreads()));
             }
         });
     }
 
     public void setMainPanel(JComponent comp) {
         mainPanel.setViewportView(comp);
     }
 
     public JTree getTree() {
         return tree;
     }
 
     // TestStateListener implementation
 
     /**
      * Called when a test is started on the local system. This implementation
      * sets the running indicator and ensures that the menubar is enabled and in
      * the running state.
      */
     @Override
     public void testStarted() {
         testStarted(LOCAL);
         menuBar.setEnabled(true);
     }
 
     /**
      * Called when a test is started on a specific host. This implementation
      * sets the running indicator and ensures that the menubar is in the running
      * state.
      *
      * @param host
      *            the host where the test is starting
      */
     @Override
     public void testStarted(String host) {
         hosts.add(host);
         computeTestDurationTimer.start();
         runningIndicator.setIcon(runningIcon);
         activeThreads.setText("0"); // $NON-NLS-1$
         totalThreads.setText("0"); // $NON-NLS-1$
         menuBar.setRunning(true, host);
         if (LOCAL.equals(host)) {
             toolbar.setLocalTestStarted(true);
         } else {
             toolbar.setRemoteTestStarted(true);
         }
     }
 
     /**
      * Called when a test is ended on the local system. This implementation
      * disables the menubar, stops the running indicator, and closes the
      * stopping message dialog.
      */
     @Override
     public void testEnded() {
         testEnded(LOCAL);
         menuBar.setEnabled(false);
     }
 
     /**
      * Called when a test is ended on the remote system. This implementation
      * stops the running indicator and closes the stopping message dialog.
      *
      * @param host
      *            the host where the test is ending
      */
     @Override
     public void testEnded(String host) {
         hosts.remove(host);
         if (hosts.size() == 0) {
             runningIndicator.setIcon(stoppedIcon);
             JMeterContextService.endTest();
             computeTestDurationTimer.stop();
         }
         menuBar.setRunning(false, host);
         if (LOCAL.equals(host)) {
             toolbar.setLocalTestStarted(false);
         } else {
             toolbar.setRemoteTestStarted(false);
         }
         if (stoppingMessage != null) {
             stoppingMessage.dispose();
             stoppingMessage = null;
         }
     }
 
     /**
      * Create the GUI components and layout.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         menuBar = new JMeterMenuBar();
         setJMenuBar(menuBar);
         JPanel all = new JPanel(new BorderLayout());
         all.add(createToolBar(), BorderLayout.NORTH);
 
         JSplitPane treeAndMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
 
         treePanel = createTreePanel();
         treeAndMain.setLeftComponent(treePanel);
 
         JSplitPane topAndDown = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         topAndDown.setOneTouchExpandable(true);
         topAndDown.setDividerLocation(0.8);
         topAndDown.setResizeWeight(.8);
         topAndDown.setContinuousLayout(true);
         topAndDown.setBorder(null); // see bug jdk 4131528
         if (!DISPLAY_LOGGER_PANEL) {
             topAndDown.setDividerSize(0);
         }
         mainPanel = createMainPanel();
 
         logPanel = createLoggerPanel();
         errorsAndFatalsCounterLogTarget = new ErrorsAndFatalsCounterLogTarget();
-        LoggingManager.addLogTargetToRootLogger(new LogTarget[]{
-                logPanel,
-                errorsAndFatalsCounterLogTarget
-        });
+        GuiPackage.getInstance().getLogEventBus().registerEventListener(logPanel);
+        GuiPackage.getInstance().getLogEventBus().registerEventListener(errorsAndFatalsCounterLogTarget);
 
         topAndDown.setTopComponent(mainPanel);
         topAndDown.setBottomComponent(logPanel);
 
         treeAndMain.setRightComponent(topAndDown);
 
         treeAndMain.setResizeWeight(.2);
         treeAndMain.setContinuousLayout(true);
         all.add(treeAndMain, BorderLayout.CENTER);
 
         getContentPane().add(all);
 
         tree.setSelectionRow(1);
         addWindowListener(new WindowHappenings());
         // Building is complete, register as listener
         GuiPackage.getInstance().registerAsListener();
         setTitle(DEFAULT_TITLE);
         setIconImage(JMeterUtils.getImage("icon-apache.png").getImage());// $NON-NLS-1$
         setWindowTitle(); // define AWT WM_CLASS string
     }
 
 
     /**
      * Support for Test Plan Dnd
      * see BUG 52281 (when JDK6 will be minimum JDK target)
      */
     public void initTopLevelDndHandler() {
         new DropTarget(this, this);
     }
 
     public void setExtendedFrameTitle(String fname) {
         // file New operation may set to null, so just return app name
         if (fname == null) {
             setTitle(DEFAULT_TITLE);
             return;
         }
 
         // allow for windows / chars in filename
         String temp = fname.replace('\\', '/'); // $NON-NLS-1$ // $NON-NLS-2$
         String simpleName = temp.substring(temp.lastIndexOf('/') + 1);// $NON-NLS-1$
         setTitle(simpleName + " (" + fname + ") - " + DEFAULT_TITLE); // $NON-NLS-1$ // $NON-NLS-2$
     }
 
     /**
      * Create the JMeter tool bar pane containing the running indicator.
      *
      * @return a panel containing the running indicator
      */
     private Component createToolBar() {
         Box toolPanel = new Box(BoxLayout.X_AXIS);
         // add the toolbar
         this.toolbar = JMeterToolBar.createToolbar(true);
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setMainToolbar(toolbar);
         toolPanel.add(toolbar);
 
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(Box.createGlue());
 
         toolPanel.add(testTimeDuration);
         toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
 
         toolPanel.add(errorsOrFatalsLabel);
         toolPanel.add(warnIndicator);
         toolPanel.add(Box.createRigidArea(new Dimension(20, 15)));
 
         toolPanel.add(activeThreads);
         toolPanel.add(new JLabel(" / "));
         toolPanel.add(totalThreads);
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(runningIndicator);
         return toolPanel;
     }
 
     /**
      * Create the panel where the GUI representation of the test tree is
      * displayed. The tree should already be created before calling this method.
      *
      * @return a scroll pane containing the test tree GUI
      */
     private JScrollPane createTreePanel() {
         JScrollPane treeP = new JScrollPane(tree);
         treeP.setMinimumSize(new Dimension(100, 0));
         return treeP;
     }
 
     /**
      * Create the main panel where components can display their GUIs.
      *
      * @return the main scroll pane
      */
     private JScrollPane createMainPanel() {
         return new JScrollPane();
     }
 
     /**
      * Create at the down of the left a Console for Log events
      * @return {@link LoggerPanel}
      */
     private LoggerPanel createLoggerPanel() {
         LoggerPanel loggerPanel = new LoggerPanel();
         loggerPanel.setMinimumSize(new Dimension(0, 100));
         loggerPanel.setPreferredSize(new Dimension(0, 150));
         GuiPackage guiInstance = GuiPackage.getInstance();
         guiInstance.setLoggerPanel(loggerPanel);
         guiInstance.getMenuItemLoggerPanel().getModel().setSelected(DISPLAY_LOGGER_PANEL);
         loggerPanel.setVisible(DISPLAY_LOGGER_PANEL);
         return loggerPanel;
     }
 
     /**
      * Create and initialize the GUI representation of the test tree.
      *
      * @param treeModel
      *            the test tree model
      * @param treeListener
      *            the test tree listener
      *
      * @return the initialized test tree GUI
      */
     private JTree makeTree(TreeModel treeModel, JMeterTreeListener treeListener) {
         JTree treevar = new JTree(treeModel) {
             private static final long serialVersionUID = 240L;
 
             @Override
             public String getToolTipText(MouseEvent event) {
                 TreePath path = this.getPathForLocation(event.getX(), event.getY());
                 if (path != null) {
                     Object treeNode = path.getLastPathComponent();
                     if (treeNode instanceof DefaultMutableTreeNode) {
                         Object testElement = ((DefaultMutableTreeNode) treeNode).getUserObject();
                         if (testElement instanceof TestElement) {
                             String comment = ((TestElement) testElement).getComment();
                             if (comment != null && comment.length() > 0) {
                                 return comment;
                                 }
                             }
                         }
                     }
                 return null;
                 }
             };
         treevar.setToolTipText("");
         treevar.setCellRenderer(getCellRenderer());
         treevar.setRootVisible(false);
         treevar.setShowsRootHandles(true);
 
         treeListener.setJTree(treevar);
         treevar.addTreeSelectionListener(treeListener);
         treevar.addMouseListener(treeListener);
         treevar.addKeyListener(treeListener);
 
         // enable drag&drop, install a custom transfer handler
         treevar.setDragEnabled(true);
         treevar.setDropMode(DropMode.ON_OR_INSERT);
         treevar.setTransferHandler(new JMeterTreeTransferHandler());
 
         addQuickComponentHotkeys(treevar);
 
         return treevar;
     }
 
     private void addQuickComponentHotkeys(JTree treevar) {
         Action quickComponent = new AbstractAction("Quick Component") {
             private static final long serialVersionUID = 1L;
 
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 String propname = "gui.quick_" + actionEvent.getActionCommand();
                 String comp = JMeterUtils.getProperty(propname);
                 log.debug("Event " + propname + ": " + comp);
 
                 if (comp == null) {
                     log.warn("No component set through property: " + propname);
                     return;
                 }
 
                 GuiPackage guiPackage = GuiPackage.getInstance();
                 try {
                     guiPackage.updateCurrentNode();
                     TestElement testElement = guiPackage.createTestElement(SaveService.aliasToClass(comp));
                     JMeterTreeNode parentNode = guiPackage.getCurrentNode();
                     while (!MenuFactory.canAddTo(parentNode, testElement)) {
                         parentNode = (JMeterTreeNode) parentNode.getParent();
                     }
                     if (parentNode.getParent() == null) {
                         log.debug("Cannot add element on very top level");
                     } else {
                         JMeterTreeNode node = guiPackage.getTreeModel().addComponent(testElement, parentNode);
                         guiPackage.getMainFrame().getTree().setSelectionPath(new TreePath(node.getPath()));
                     }
                 } catch (Exception err) {
                     log.warn("Failed to perform quick component add: " + comp, err); // $NON-NLS-1$
                 }
             }
         };
 
         InputMap inputMap = treevar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         KeyStroke[] keyStrokes = new KeyStroke[]{KeyStrokes.CTRL_0,
                 KeyStrokes.CTRL_1, KeyStrokes.CTRL_2, KeyStrokes.CTRL_3,
                 KeyStrokes.CTRL_4, KeyStrokes.CTRL_5, KeyStrokes.CTRL_6,
                 KeyStrokes.CTRL_7, KeyStrokes.CTRL_8, KeyStrokes.CTRL_9,};
         for (int n = 0; n < keyStrokes.length; n++) {
             treevar.getActionMap().put(ActionNames.QUICK_COMPONENT + String.valueOf(n), quickComponent);
             inputMap.put(keyStrokes[n], ActionNames.QUICK_COMPONENT + String.valueOf(n));
         }
     }
 
     /**
      * Create the tree cell renderer used to draw the nodes in the test tree.
      *
      * @return a renderer to draw the test tree nodes
      */
     private TreeCellRenderer getCellRenderer() {
         DefaultTreeCellRenderer rend = new JMeterCellRenderer();
         return rend;
     }
 
     /**
      * A window adapter used to detect when the main JMeter frame is being
      * closed.
      */
     private static class WindowHappenings extends WindowAdapter {
         /**
          * Called when the main JMeter frame is being closed. Sends a
          * notification so that JMeter can react appropriately.
          *
          * @param event
          *            the WindowEvent to handle
          */
         @Override
         public void windowClosing(WindowEvent event) {
             ActionRouter.getInstance().actionPerformed(new ActionEvent(this, event.getID(), ActionNames.EXIT));
         }
     }
 
     @Override
     public void dragEnter(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     @Override
     public void dragExit(DropTargetEvent dte) {
         // NOOP
     }
 
     @Override
     public void dragOver(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     /**
      * Handler of Top level Dnd
      */
     @Override
     public void drop(DropTargetDropEvent dtde) {
         try {
             Transferable tr = dtde.getTransferable();
             DataFlavor[] flavors = tr.getTransferDataFlavors();
             for (DataFlavor flavor : flavors) {
                 // Check for file lists specifically
                 if (flavor.isFlavorJavaFileListType()) {
                     dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                     try {
                         openJmxFilesFromDragAndDrop(tr);
                     } finally {
                         dtde.dropComplete(true);
                     }
                     return;
                 }
             }
         } catch (UnsupportedFlavorException | IOException e) {
             log.warn("Dnd failed" , e);
         }
 
     }
 
     public boolean openJmxFilesFromDragAndDrop(Transferable tr) throws UnsupportedFlavorException, IOException {
         @SuppressWarnings("unchecked")
         List<File> files = (List<File>)
                 tr.getTransferData(DataFlavor.javaFileListFlavor);
         if (files.isEmpty()) {
             return false;
         }
         File file = files.get(0);
         if (!file.getName().endsWith(".jmx")) {
             log.warn("Importing file:" + file.getName()+ "from DnD failed because file extension does not end with .jmx");
             return false;
         }
 
         ActionEvent fakeEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ActionNames.OPEN);
         LoadDraggedFile.loadProject(fakeEvent, file);
 
         return true;
     }
 
     @Override
     public void dropActionChanged(DropTargetDragEvent dtde) {
         // NOOP
     }
 
     /**
-     *
+     * ErrorsAndFatalsCounterLogTarget.
      */
-    public final class ErrorsAndFatalsCounterLogTarget implements LogTarget, Clearable {
+    public final class ErrorsAndFatalsCounterLogTarget implements GuiLogEventListener, Clearable {
         public AtomicInteger errorOrFatal = new AtomicInteger(0);
 
         @Override
-        public void processEvent(LogEvent event) {
-            if(event.getPriority().equals(Priority.ERROR) ||
-                    event.getPriority().equals(Priority.FATAL_ERROR)) {
+        public void processLogEvent(LogEventObject logEventObject) {
+            if (logEventObject.isMoreSpecificThanError()) {
                 final int newValue = errorOrFatal.incrementAndGet();
                 SwingUtilities.invokeLater(new Runnable() {
                     @Override
                     public void run() {
                         errorsOrFatalsLabel.setForeground(Color.RED);
                         errorsOrFatalsLabel.setText(Integer.toString(newValue));
                     }
                 });
             }
         }
 
         @Override
         public void clearData() {
             errorOrFatal.set(0);
             SwingUtilities.invokeLater(new Runnable() {
                 @Override
                 public void run() {
                     errorsOrFatalsLabel.setForeground(Color.BLACK);
                     errorsOrFatalsLabel.setText(Integer.toString(errorOrFatal.get()));
                 }
             });
         }
+
     }
 
 
     @Override
     public void clearData() {
         logPanel.clear();
         errorsAndFatalsCounterLogTarget.clearData();
     }
 
     /**
      * Handles click on warnIndicator
      */
     @Override
     public void actionPerformed(ActionEvent event) {
         if (event.getSource() == warnIndicator) {
             ActionRouter.getInstance().doActionNow(new ActionEvent(event.getSource(), event.getID(), ActionNames.LOGGER_PANEL_ENABLE_DISABLE));
         }
     }
 
     /**
      * Define AWT window title (WM_CLASS string) (useful on Gnome 3 / Linux)
      */
     private void setWindowTitle() {
         Class<?> xtoolkit = Toolkit.getDefaultToolkit().getClass();
         if (xtoolkit.getName().equals("sun.awt.X11.XToolkit")) { // NOSONAR (we don't want to depend on native LAF) $NON-NLS-1$
             try {
                 final Field awtAppClassName = xtoolkit.getDeclaredField("awtAppClassName"); // $NON-NLS-1$
                 awtAppClassName.setAccessible(true);
                 awtAppClassName.set(null, DEFAULT_APP_NAME);
             } catch (NoSuchFieldException | IllegalAccessException nsfe) {
                 log.warn("Error awt title: " + nsfe); // $NON-NLS-1$
             }
        }
     }
 
     /**
      * Update Undo/Redo icons state
      * 
      * @param canUndo
      *            Flag whether the undo button should be enabled
      * @param canRedo
      *            Flag whether the redo button should be enabled
      */
     public void updateUndoRedoIcons(boolean canUndo, boolean canRedo) {
         toolbar.updateUndoRedoIcons(canUndo, canRedo);
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/action/What.java b/src/core/org/apache/jmeter/gui/action/What.java
index 1dc53c67b..3e931b322 100644
--- a/src/core/org/apache/jmeter/gui/action/What.java
+++ b/src/core/org/apache/jmeter/gui/action/What.java
@@ -1,89 +1,93 @@
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
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.HeapDumper;
 import org.apache.log.Logger;
+import org.apache.logging.log4j.Level;
+import org.apache.logging.log4j.core.config.Configurator;
 
 /**
  *
  * Debug class to show details of the currently selected object
  * Currently shows TestElement and GUI class names
  *
  * Also enables/disables debug for the test element.
  *
  */
 public class What extends AbstractAction {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> commandSet;
 
     static {
         Set<String> commands = new HashSet<>();
         commands.add(ActionNames.WHAT_CLASS);
         commands.add(ActionNames.DEBUG_ON);
         commands.add(ActionNames.DEBUG_OFF);
         commands.add(ActionNames.HEAP_DUMP);
         commandSet = Collections.unmodifiableSet(commands);
     }
 
 
     @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         JMeterTreeNode node= GuiPackage.getInstance().getTreeListener().getCurrentNode();
         TestElement te = (TestElement)node.getUserObject();
         if (ActionNames.WHAT_CLASS.equals(e.getActionCommand())){
             String guiClassName = te.getPropertyAsString(TestElement.GUI_CLASS);
             System.out.println(te.getClass().getName());
             System.out.println(guiClassName);
             log.info("TestElement:"+te.getClass().getName()+", guiClassName:"+guiClassName);
         } else if (ActionNames.DEBUG_ON.equals(e.getActionCommand())){
-            LoggingManager.setPriorityFullName("DEBUG",te.getClass().getName());//$NON-NLS-1$
+            Configurator.setAllLevels(te.getClass().getName(), Level.DEBUG);
+            log.info("Log level set to DEBUG for " + te.getClass().getName());
         } else if (ActionNames.DEBUG_OFF.equals(e.getActionCommand())){
-            LoggingManager.setPriorityFullName("INFO",te.getClass().getName());//$NON-NLS-1$
+            Configurator.setAllLevels(te.getClass().getName(), Level.INFO);
+            log.info("Log level set to INFO for " + te.getClass().getName());
         } else if (ActionNames.HEAP_DUMP.equals(e.getActionCommand())){
             try {
                 String s = HeapDumper.dumpHeap();
                 JOptionPane.showMessageDialog(null, "Created "+s, "HeapDump", JOptionPane.INFORMATION_MESSAGE);
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(null, ex.toString(), "HeapDump", JOptionPane.ERROR_MESSAGE);
             }
         }
     }
 
     /**
      * Provide the list of Action names that are available in this command.
      */
     @Override
     public Set<String> getActionNames() {
         return commandSet;
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/logging/GuiLogEventAppender.java b/src/core/org/apache/jmeter/gui/logging/GuiLogEventAppender.java
new file mode 100644
index 000000000..8ad172387
--- /dev/null
+++ b/src/core/org/apache/jmeter/gui/logging/GuiLogEventAppender.java
@@ -0,0 +1,83 @@
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
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+package org.apache.jmeter.gui.logging;
+
+import java.io.Serializable;
+
+import org.apache.jmeter.gui.GuiPackage;
+import org.apache.logging.log4j.core.Filter;
+import org.apache.logging.log4j.core.Layout;
+import org.apache.logging.log4j.core.LogEvent;
+import org.apache.logging.log4j.core.StringLayout;
+import org.apache.logging.log4j.core.appender.AbstractAppender;
+import org.apache.logging.log4j.core.config.plugins.Plugin;
+import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
+import org.apache.logging.log4j.core.config.plugins.PluginElement;
+import org.apache.logging.log4j.core.config.plugins.PluginFactory;
+import org.apache.logging.log4j.core.layout.PatternLayout;
+
+/**
+ * Posts log events to a {@link GuiLogEventBus}.
+ */
+@Plugin(name = "GuiLogEvent", category = "Core", elementType = "appender", printObject = true)
+public class GuiLogEventAppender extends AbstractAppender {
+
+    protected GuiLogEventAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
+            final boolean ignoreExceptions) {
+        super(name, filter, layout, ignoreExceptions);
+    }
+
+    @Override
+    public void append(LogEvent logEvent) {
+        // Note: GuiPackage class access SHOULD be always successful.
+        //       For example, if it fails to get GuiPackage#getInstance() due to static member initialization failure
+        //       (e.g, accessing JMeterUtils#getPropDefault(...) without initializing application properties),
+        //       the error log can be detected as "Recursive call to appender ..." by Log4j2 LoggerControl,
+        //       resulting in no meaningful error logs in the log appender targets.
+        GuiPackage instance = GuiPackage.getInstance();
+
+        if (instance != null) {
+            final String serializedString = getStringLayout().toSerializable(logEvent);
+
+            if (serializedString != null && !serializedString.isEmpty()) {
+                LogEventObject logEventObject = new LogEventObject(logEvent, serializedString);
+                instance.getLogEventBus().postEvent(logEventObject);
+            }
+        }
+    }
+
+    @PluginFactory
+    public static GuiLogEventAppender createAppender(@PluginAttribute("name") String name,
+            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
+            @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filters") Filter filter) {
+        if (name == null) {
+            LOGGER.error("No name provided for GuiLogEventAppender");
+            return null;
+        }
+
+        if (layout == null) {
+            layout = PatternLayout.createDefaultLayout();
+        }
+
+        return new GuiLogEventAppender(name, filter, layout, ignoreExceptions);
+    }
+
+    public StringLayout getStringLayout() {
+        return (StringLayout) getLayout();
+    }
+}
diff --git a/src/core/org/apache/jmeter/gui/logging/GuiLogEventBus.java b/src/core/org/apache/jmeter/gui/logging/GuiLogEventBus.java
new file mode 100644
index 000000000..a4fc690cb
--- /dev/null
+++ b/src/core/org/apache/jmeter/gui/logging/GuiLogEventBus.java
@@ -0,0 +1,79 @@
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
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+package org.apache.jmeter.gui.logging;
+
+import java.util.Arrays;
+import java.util.LinkedHashSet;
+import java.util.Set;
+
+/**
+ * GUI Log Event Bus.
+ */
+public class GuiLogEventBus {
+
+    /**
+     * Registered GUI log event listeners array.
+     */
+    private GuiLogEventListener [] listeners;
+
+    /**
+     * Default constructor.
+     */
+    public GuiLogEventBus() {
+    }
+
+    /**
+     * Register a GUI log event listener ({@link GuiLogEventListener}).
+     * @param listener a GUI log event listener ({@link GuiLogEventListener})
+     */
+    public void registerEventListener(GuiLogEventListener listener) {
+        if (listeners == null) {
+            listeners = new GuiLogEventListener[] { listener };
+        } else {
+            Set<GuiLogEventListener> set = new LinkedHashSet<>(Arrays.asList(listeners));
+            set.add(listener);
+            GuiLogEventListener [] arr = new GuiLogEventListener[set.size()];
+            listeners = set.toArray(arr);
+        }
+    }
+
+    /**
+     * Unregister a GUI log event listener ({@link GuiLogEventListener}).
+     * @param listener a GUI log event listener ({@link GuiLogEventListener})
+     */
+    public void unregisterEventListener(GuiLogEventListener listener) {
+        if (listeners != null) {
+            Set<GuiLogEventListener> set = new LinkedHashSet<>(Arrays.asList(listeners));
+            set.remove(listener);
+            GuiLogEventListener [] arr = new GuiLogEventListener[set.size()];
+            listeners = set.toArray(arr);
+        }
+    }
+
+    /**
+     * Post a log event object.
+     * @param logEvent log event object
+     */
+    public void postEvent(LogEventObject logEventObject) {
+        if (listeners != null) {
+            for (GuiLogEventListener listener : listeners) {
+                listener.processLogEvent(logEventObject);
+            }
+        }
+    }
+}
diff --git a/src/core/org/apache/jmeter/gui/logging/GuiLogEventListener.java b/src/core/org/apache/jmeter/gui/logging/GuiLogEventListener.java
new file mode 100644
index 000000000..2d066fed0
--- /dev/null
+++ b/src/core/org/apache/jmeter/gui/logging/GuiLogEventListener.java
@@ -0,0 +1,33 @@
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
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+package org.apache.jmeter.gui.logging;
+
+import java.util.EventListener;
+
+/**
+ * GUI Log Event Listener.
+ */
+public interface GuiLogEventListener extends EventListener {
+
+    /**
+     * Process a log event object.
+     * @param logEventObject log event object
+     */
+    public void processLogEvent(LogEventObject logEventObject);
+
+}
diff --git a/src/core/org/apache/jmeter/gui/logging/LogEventObject.java b/src/core/org/apache/jmeter/gui/logging/LogEventObject.java
new file mode 100644
index 000000000..82ba98cd8
--- /dev/null
+++ b/src/core/org/apache/jmeter/gui/logging/LogEventObject.java
@@ -0,0 +1,75 @@
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
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+package org.apache.jmeter.gui.logging;
+
+import java.util.EventObject;
+
+import org.apache.logging.log4j.Level;
+import org.apache.logging.log4j.core.LogEvent;
+
+/**
+ * Log event object.
+ */
+public class LogEventObject extends EventObject {
+
+    private static final long serialVersionUID = 1L;
+
+    private Level level;
+    private String seralizedString;
+
+    public LogEventObject(Object source) {
+        this(source, null);
+    }
+
+    public LogEventObject(Object source, String seralizedString) {
+        super(source);
+        if (source instanceof LogEvent) {
+            level = ((LogEvent) source).getLevel();
+        }
+        this.seralizedString = seralizedString;
+    }
+
+    public boolean isMoreSpecificThanError() {
+        if (level != null) {
+            return level.isMoreSpecificThan(Level.ERROR);
+        }
+        return false;
+    }
+
+    public boolean isMoreSpecificThanWarn() {
+        if (level != null) {
+            return level.isMoreSpecificThan(Level.WARN);
+        }
+        return false;
+    }
+
+    public boolean isMoreSpecificThanInfo() {
+        if (level != null) {
+            return level.isMoreSpecificThan(Level.INFO);
+        }
+        return false;
+    }
+
+    @Override
+    public String toString() {
+        if (seralizedString != null) {
+            return seralizedString;
+        }
+        return super.toString();
+    }
+}
diff --git a/src/core/org/apache/jmeter/samplers/SampleResult.java b/src/core/org/apache/jmeter/samplers/SampleResult.java
index 2f96a10bf..a99f893bf 100644
--- a/src/core/org/apache/jmeter/samplers/SampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/SampleResult.java
@@ -1,1052 +1,1052 @@
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
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.io.UnsupportedEncodingException;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.nio.charset.Charset;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 // For unit tests, @see TestSampleResult
 
 /**
  * This is a nice packaging for the various information returned from taking a
  * sample of an entry.
  *
  */
 public class SampleResult implements Serializable, Cloneable, Searchable {
 
     private static final long serialVersionUID = 241L;
 
     // Needs to be accessible from Test code
-    static final Logger log = LoggingManager.getLoggerForClass();
+    static Logger log = LoggerFactory.getLogger(SampleResult.class);
 
     /**
      * The default encoding to be used if not overridden.
      * The value is ISO-8859-1.
      */
     public static final String DEFAULT_HTTP_ENCODING = StandardCharsets.ISO_8859_1.name();
     
     private static final String OK_CODE = Integer.toString(HttpURLConnection.HTTP_OK);
     private static final String OK_MSG = "OK"; // $NON-NLS-1$
 
 
     // Bug 33196 - encoding ISO-8859-1 is only suitable for Western countries
     // However the suggested System.getProperty("file.encoding") is Cp1252 on
     // Windows
     // So use a new property with the original value as default
     // needs to be accessible from test code
     /**
      * The default encoding to be used to decode the responseData byte array.
      * The value is defined by the property "sampleresult.default.encoding"
      * with a default of DEFAULT_HTTP_ENCODING if that is not defined.
      */
     protected static final String DEFAULT_ENCODING
             = JMeterUtils.getPropDefault("sampleresult.default.encoding", // $NON-NLS-1$
             DEFAULT_HTTP_ENCODING);
 
     /**
      * The default used by {@link #setResponseData(String, String)}
      */
     private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
 
     /**
      * Data type value ({@value}) indicating that the response data is text.
      *
      * @see #getDataType
      * @see #setDataType(java.lang.String)
      */
     public static final String TEXT = "text"; // $NON-NLS-1$
 
     /**
      * Data type value ({@value}) indicating that the response data is binary.
      *
      * @see #getDataType
      * @see #setDataType(java.lang.String)
      */
     public static final String BINARY = "bin"; // $NON-NLS-1$
     
     // List of types that are known to be binary
     private static final String[] BINARY_TYPES = {
         "image/",       //$NON-NLS-1$
         "audio/",       //$NON-NLS-1$
         "video/",       //$NON-NLS-1$
         };
 
     // List of types that are known to be ascii, although they may appear to be binary
     private static final String[] NON_BINARY_TYPES = {
         "audio/x-mpegurl",  //$NON-NLS-1$ (HLS Media Manifest)
         "video/f4m"         //$NON-NLS-1$ (Flash Media Manifest)
         };
 
 
     /** empty array which can be returned instead of null */
     private static final byte[] EMPTY_BA = new byte[0];
 
     private static final SampleResult[] EMPTY_SR = new SampleResult[0];
 
     private static final AssertionResult[] EMPTY_AR = new AssertionResult[0];
     
     private static final boolean GETBYTES_BODY_REALSIZE = 
         JMeterUtils.getPropDefault("sampleresult.getbytes.body_real_size", true); // $NON-NLS-1$
 
     private static final boolean GETBYTES_HEADERS_SIZE = 
         JMeterUtils.getPropDefault("sampleresult.getbytes.headers_size", true); // $NON-NLS-1$
     
     private static final boolean GETBYTES_NETWORK_SIZE =
             GETBYTES_HEADERS_SIZE && GETBYTES_BODY_REALSIZE;
 
     private static final boolean START_TIMESTAMP = 
             JMeterUtils.getPropDefault("sampleresult.timestamp.start", false);  // $NON-NLS-1$
 
     /**
      * Allow read-only access from test code
      */
     private static final boolean USE_NANO_TIME = 
             JMeterUtils.getPropDefault("sampleresult.useNanoTime", true);  // $NON-NLS-1$
     
     /**
      * How long between checks of nanotime; default 5000ms; set to <=0 to disable the thread
      */
     private static final long NANOTHREAD_SLEEP = 
             JMeterUtils.getPropDefault("sampleresult.nanoThreadSleep", 5000);  // $NON-NLS-1$
 
     static {
         if (START_TIMESTAMP) {
             log.info("Note: Sample TimeStamps are START times");
         } else {
             log.info("Note: Sample TimeStamps are END times");
         }
         log.info("sampleresult.default.encoding is set to " + DEFAULT_ENCODING);
         log.info("sampleresult.useNanoTime="+USE_NANO_TIME);
         log.info("sampleresult.nanoThreadSleep="+NANOTHREAD_SLEEP);
 
         if (USE_NANO_TIME && NANOTHREAD_SLEEP > 0) {
             // Make sure we start with a reasonable value
             NanoOffset.nanoOffset = System.currentTimeMillis() - SampleResult.sampleNsClockInMs();
             NanoOffset nanoOffset = new NanoOffset();
             nanoOffset.setDaemon(true);
             nanoOffset.setName("NanoOffset");
             nanoOffset.start();
         }
     }
     private SampleSaveConfiguration saveConfig;
 
     private SampleResult parent;
 
     private byte[] responseData = EMPTY_BA;
 
     private String responseCode = "";// Never return null
 
     private String label = "";// Never return null
 
     /** Filename used by ResultSaver */
     private String resultFileName = "";
 
     /** The data used by the sampler */
     private String samplerData;
 
     private String threadName = ""; // Never return null
 
     private String responseMessage = "";
 
     private String responseHeaders = ""; // Never return null
 
     private String requestHeaders = "";
 
     /**
      * timeStamp == 0 means either not yet initialised or no stamp available (e.g. when loading a results file)
      * the time stamp - can be start or end 
      */
     private long timeStamp = 0;
 
     private long startTime = 0;
 
     private long endTime = 0;
 
     private long idleTime = 0;// Allow for non-sample time
 
     /** Start of pause (if any) */
     private long pauseTime = 0;
 
     private List<AssertionResult> assertionResults;
 
     private List<SampleResult> subResults;
 
     /**
      * The data type of the sample
      * @see #getDataType()
      * @see #setDataType(String)
      * @see #TEXT
      * @see #BINARY
      */
     private String dataType=""; // Don't return null if not set
 
     private boolean success;
 
     //@GuardedBy("this"")
     /** files that this sample has been saved in */
     /** In Non GUI mode and when best config is used, size never exceeds 1, 
      * but as a compromise set it to 3 
      */
     private final Set<String> files = new HashSet<>(3);
 
     // TODO do contentType and/or dataEncoding belong in HTTPSampleResult instead?
     private String dataEncoding;// (is this really the character set?) e.g.
                                 // ISO-8895-1, UTF-8
 
     private String contentType = ""; // e.g. text/html; charset=utf-8
 
     /** elapsed time */
     private long elapsedTime = 0;
 
     /** time to first response */
     private long latency = 0;
 
     /**
      * time to end connecting
      */
     private long connectTime = 0;
 
     /** Should thread start next iteration ? */
     private boolean startNextThreadLoop = false;
 
     /** Should thread terminate? */
     private boolean stopThread = false;
 
     /** Should test terminate? */
     private boolean stopTest = false;
 
     /** Should test terminate abruptly? */
     private boolean stopTestNow = false;
 
     private int sampleCount = 1;
 
     private long bytes = 0; // Allows override of sample size in case sampler does not want to store all the data
     
     private int headersSize = 0;
     
     private long bodySize = 0;
 
     /** Currently active threads in this thread group */
     private volatile int groupThreads = 0;
 
     /** Currently active threads in all thread groups */
     private volatile int allThreads = 0;
 
     private final long nanoTimeOffset;
 
     // Allow testcode access to the settings
     final boolean useNanoTime;
     
     final long nanoThreadSleep;
     
     private long sentBytes;
     
     private URL location;
 
     /**
      * Cache for responseData as string to avoid multiple computations
      */
     private transient volatile String responseDataAsString;
 
     public SampleResult() {
         this(USE_NANO_TIME, NANOTHREAD_SLEEP);
     }
 
     // Allow test code to change the default useNanoTime setting
     SampleResult(boolean nanoTime) {
         this(nanoTime, NANOTHREAD_SLEEP);
     }
 
     // Allow test code to change the default useNanoTime and nanoThreadSleep settings
     SampleResult(boolean nanoTime, long nanoThreadSleep) {
         this.elapsedTime = 0;
         this.useNanoTime = nanoTime;
         this.nanoThreadSleep = nanoThreadSleep;
         this.nanoTimeOffset = initOffset();
     }
 
     /**
      * Copy constructor.
      * 
      * @param res existing sample result
      */
     public SampleResult(SampleResult res) {
         this();
         allThreads = res.allThreads;//OK
         assertionResults = res.assertionResults;
         bytes = res.bytes;
         headersSize = res.headersSize;
         bodySize = res.bodySize;
         contentType = res.contentType;//OK
         dataEncoding = res.dataEncoding;//OK
         dataType = res.dataType;//OK
         endTime = res.endTime;//OK
         // files is created automatically, and applies per instance
         groupThreads = res.groupThreads;//OK
         idleTime = res.idleTime;
         label = res.label;//OK
         latency = res.latency;
         connectTime = res.connectTime;
         location = res.location;//OK
         parent = res.parent; 
         pauseTime = res.pauseTime;
         requestHeaders = res.requestHeaders;//OK
         responseCode = res.responseCode;//OK
         responseData = res.responseData;//OK
         responseDataAsString = null;
         responseHeaders = res.responseHeaders;//OK
         responseMessage = res.responseMessage;//OK
         /** 
          * Don't copy this; it is per instance resultFileName = res.resultFileName;
          */
         sampleCount = res.sampleCount;
         samplerData = res.samplerData;
         saveConfig = res.saveConfig;
         sentBytes = res.sentBytes;
         startTime = res.startTime;//OK
         stopTest = res.stopTest;
         stopTestNow = res.stopTestNow;
         stopThread = res.stopThread;
         startNextThreadLoop = res.startNextThreadLoop;
         subResults = res.subResults; 
         success = res.success;//OK
         threadName = res.threadName;//OK
         elapsedTime = res.elapsedTime;
         timeStamp = res.timeStamp;
     }
     
     /**
      * Create a sample with a specific elapsed time but don't allow the times to
      * be changed later
      *
      * (only used by HTTPSampleResult)
      *
      * @param elapsed
      *            time
      * @param atend
      *            create the sample finishing now, else starting now
      */
     protected SampleResult(long elapsed, boolean atend) {
         this();
         long now = currentTimeInMillis();
         if (atend) {
             setTimes(now - elapsed, now);
         } else {
             setTimes(now, now + elapsed);
         }
     }
 
     /**
      * Allow users to create a sample with specific timestamp and elapsed times
      * for cloning purposes, but don't allow the times to be changed later
      *
      * Currently used by CSVSaveService and
      * StatisticalSampleResult
      *
      * @param stamp
      *            this may be a start time or an end time (both in
      *            milliseconds)
      * @param elapsed
      *            time in milliseconds
      */
     public SampleResult(long stamp, long elapsed) {
         this();
         stampAndTime(stamp, elapsed);
     }
     
     private long initOffset(){
         if (useNanoTime){
             return nanoThreadSleep > 0 ? NanoOffset.getNanoOffset() : System.currentTimeMillis() - sampleNsClockInMs();
         } else {
             return Long.MIN_VALUE;
         }
     }
     
     /**
      * @param propertiesToSave
      *            The propertiesToSave to set.
      */
     public void setSaveConfig(SampleSaveConfiguration propertiesToSave) {
         this.saveConfig = propertiesToSave;
     }
 
     public SampleSaveConfiguration getSaveConfig() {
         return saveConfig;
     }
 
     public boolean isStampedAtStart() {
         return START_TIMESTAMP;
     }
 
     /**
      * Create a sample with specific start and end times for test purposes, but
      * don't allow the times to be changed later
      *
      * (used by StatVisualizerModel.Test)
      *
      * @param start
      *            start time in milliseconds since unix epoch
      * @param end
      *            end time in milliseconds since unix epoch
      * @return sample with given start and end time
      */
     public static SampleResult createTestSample(long start, long end) {
         SampleResult res = new SampleResult();
         res.setStartTime(start);
         res.setEndTime(end);
         return res;
     }
 
     /**
      * Create a sample with a specific elapsed time for test purposes, but don't
      * allow the times to be changed later
      *
      * @param elapsed
      *            - desired elapsed time in milliseconds
      * @return sample that starts 'now' and ends <code>elapsed</code> milliseconds later
      */
     public static SampleResult createTestSample(long elapsed) {
         long now = System.currentTimeMillis();
         return createTestSample(now, now + elapsed);
     }
 
     private static long sampleNsClockInMs() {
         return System.nanoTime() / 1000000;
     }
 
     /**
      * Helper method to get 1 ms resolution timing.
      * 
      * @return the current time in milliseconds
      * @throws RuntimeException
      *             when <code>useNanoTime</code> is <code>true</code> but
      *             <code>nanoTimeOffset</code> is not set
      */
     public long currentTimeInMillis() {
         if (useNanoTime){
             if (nanoTimeOffset == Long.MIN_VALUE){
                 throw new RuntimeException("Invalid call; nanoTimeOffset as not been set");
             }
             return sampleNsClockInMs() + nanoTimeOffset;            
         }
         return System.currentTimeMillis();
     }
 
     // Helper method to maintain timestamp relationships
     private void stampAndTime(long stamp, long elapsed) {
         if (START_TIMESTAMP) {
             startTime = stamp;
             endTime = stamp + elapsed;
         } else {
             startTime = stamp - elapsed;
             endTime = stamp;
         }
         timeStamp = stamp;
         elapsedTime = elapsed;
     }
 
     /**
      * For use by SaveService only.
      * 
      * @param stamp
      *            this may be a start time or an end time (both in milliseconds)
      * @param elapsed
      *            time in milliseconds
      * @throws RuntimeException
      *             when <code>startTime</code> or <code>endTime</code> has been
      *             set already
      */
     public void setStampAndTime(long stamp, long elapsed) {
         if (startTime != 0 || endTime != 0){
             throw new RuntimeException("Calling setStampAndTime() after start/end times have been set");
         }
         stampAndTime(stamp, elapsed);
     }
 
     /**
      * Set the "marked" flag to show that the result has been written to the file.
      *
      * @param filename the name of the file
      * @return <code>true</code> if the result was previously marked
      */
     public synchronized boolean markFile(String filename) {
         return !files.add(filename);
     }
 
     public String getResponseCode() {
         return responseCode;
     }
 
     /**
      * Set response code to OK, i.e. "200"
      *
      */
     public void setResponseCodeOK(){
         responseCode=OK_CODE;
     }
 
     public void setResponseCode(String code) {
         responseCode = code;
     }
 
     public boolean isResponseCodeOK(){
         return responseCode.equals(OK_CODE);
     }
     public String getResponseMessage() {
         return responseMessage;
     }
 
     public void setResponseMessage(String msg) {
         responseMessage = msg;
     }
 
     public void setResponseMessageOK() {
         responseMessage = OK_MSG;
     }
 
     /**
      * Set result statuses OK - shorthand method to set:
      * <ul>
      * <li>ResponseCode</li>
      * <li>ResponseMessage</li>
      * <li>Successful status</li>
      * </ul>
      */
     public void setResponseOK(){
         setResponseCodeOK();
         setResponseMessageOK();
         setSuccessful(true);
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     public void setThreadName(String threadName) {
         this.threadName = threadName;
     }
 
     /**
      * Get the sample timestamp, which may be either the start time or the end time.
      *
      * @see #getStartTime()
      * @see #getEndTime()
      *
      * @return timeStamp in milliseconds
      */
     public long getTimeStamp() {
         return timeStamp;
     }
 
     public String getSampleLabel() {
         return label;
     }
 
     /**
      * Get the sample label for use in summary reports etc.
      *
      * @param includeGroup whether to include the thread group name
      * @return the label
      */
     public String getSampleLabel(boolean includeGroup) {
         if (includeGroup) {
             StringBuilder sb = new StringBuilder(threadName.substring(0,threadName.lastIndexOf(' '))); //$NON-NLS-1$
             return sb.append(":").append(label).toString(); //$NON-NLS-1$
         }
         return label;
     }
 
     public void setSampleLabel(String label) {
         this.label = label;
     }
 
     public void addAssertionResult(AssertionResult assertResult) {
         if (assertionResults == null) {
             assertionResults = new ArrayList<>();
         }
         assertionResults.add(assertResult);
     }
 
     /**
      * Gets the assertion results associated with this sample.
      *
      * @return an array containing the assertion results for this sample.
      *         Returns empty array if there are no assertion results.
      */
     public AssertionResult[] getAssertionResults() {
         if (assertionResults == null) {
             return EMPTY_AR;
         }
         return assertionResults.toArray(new AssertionResult[assertionResults.size()]);
     }
 
     /**
      * Add a subresult and adjust the parent byte count and end-time.
      * 
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void addSubResult(SampleResult subResult) {
         if(subResult == null) {
             // see https://bz.apache.org/bugzilla/show_bug.cgi?id=54778
             return;
         }
         String tn = getThreadName();
         if (tn.length()==0) {
             tn=Thread.currentThread().getName();//TODO do this more efficiently
             this.setThreadName(tn);
         }
         subResult.setThreadName(tn); // TODO is this really necessary?
 
         // Extend the time to the end of the added sample
         setEndTime(Math.max(getEndTime(), subResult.getEndTime() + nanoTimeOffset - subResult.nanoTimeOffset)); // Bug 51855
         // Include the byte count for the added sample
         setBytes(getBytesAsLong() + subResult.getBytesAsLong());
         setSentBytes(getSentBytes() + subResult.getSentBytes());
         setHeadersSize(getHeadersSize() + subResult.getHeadersSize());
         setBodySize(getBodySizeAsLong() + subResult.getBodySizeAsLong());
         addRawSubResult(subResult);
     }
     
     /**
      * Add a subresult to the collection without updating any parent fields.
      * 
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void addRawSubResult(SampleResult subResult){
         storeSubResult(subResult);
     }
 
     /**
      * Add a subresult read from a results file.
      * <p>
      * As for {@link SampleResult#addSubResult(SampleResult)
      * addSubResult(SampleResult)}, except that the fields don't need to be
      * accumulated
      *
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void storeSubResult(SampleResult subResult) {
         if (subResults == null) {
             subResults = new ArrayList<>();
         }
         subResults.add(subResult);
         subResult.setParent(this);
     }
 
     /**
      * Gets the subresults associated with this sample.
      *
      * @return an array containing the subresults for this sample. Returns an
      *         empty array if there are no subresults.
      */
     public SampleResult[] getSubResults() {
         if (subResults == null) {
             return EMPTY_SR;
         }
         return subResults.toArray(new SampleResult[subResults.size()]);
     }
 
     /**
      * Sets the responseData attribute of the SampleResult object.
      *
      * If the parameter is null, then the responseData is set to an empty byte array.
      * This ensures that getResponseData() can never be null.
      *
      * @param response
      *            the new responseData value
      */
     public void setResponseData(byte[] response) {
         responseDataAsString = null;
         responseData = response == null ? EMPTY_BA : response;
     }
 
     /**
      * Sets the responseData attribute of the SampleResult object.
      * Should only be called after setting the dataEncoding (if necessary)
      *
      * @param response
      *            the new responseData value (String)
      *
      * @deprecated - only intended for use from BeanShell code
      */
     @Deprecated
     public void setResponseData(String response) {
         responseDataAsString = null;
         try {
             responseData = response.getBytes(getDataEncodingWithDefault());
         } catch (UnsupportedEncodingException e) {
             log.warn("Could not convert string, using default encoding. "+e.getLocalizedMessage());
             responseData = response.getBytes(Charset.defaultCharset()); // N.B. default charset is used deliberately here
         }
     }
 
     /**
      * Sets the encoding and responseData attributes of the SampleResult object.
      *
      * @param response the new responseData value (String)
      * @param encoding the encoding to set and then use (if null, use platform default)
      *
      */
     public void setResponseData(final String response, final String encoding) {
         responseDataAsString = null;
         String encodeUsing = encoding != null? encoding : DEFAULT_CHARSET;
         try {
             responseData = response.getBytes(encodeUsing);
             setDataEncoding(encodeUsing);
         } catch (UnsupportedEncodingException e) {
             log.warn("Could not convert string using '"+encodeUsing+
                     "', using default encoding: "+DEFAULT_CHARSET,e);
             responseData = response.getBytes(Charset.defaultCharset()); // N.B. default charset is used deliberately here
             setDataEncoding(DEFAULT_CHARSET);
         }
     }
 
     /**
      * Gets the responseData attribute of the SampleResult object.
      * <p>
      * Note that some samplers may not store all the data, in which case
      * getResponseData().length will be incorrect.
      *
      * Instead, always use {@link #getBytes()} to obtain the sample result byte count.
      * </p>
      * @return the responseData value (cannot be null)
      */
     public byte[] getResponseData() {
         return responseData;
     }
 
     /**
      * Gets the responseData of the SampleResult object as a String
      *
      * @return the responseData value as a String, converted according to the encoding
      */
     public String getResponseDataAsString() {
         try {
             if(responseDataAsString == null) {
                 responseDataAsString= new String(responseData,getDataEncodingWithDefault());
             }
             return responseDataAsString;
         } catch (UnsupportedEncodingException e) {
             log.warn("Using platform default as "+getDataEncodingWithDefault()+" caused "+e);
             return new String(responseData,Charset.defaultCharset()); // N.B. default charset is used deliberately here
         }
     }
 
     public void setSamplerData(String s) {
         samplerData = s;
     }
 
     public String getSamplerData() {
         return samplerData;
     }
 
     /**
      * Get the time it took this sample to occur.
      *
      * @return elapsed time in milliseonds
      *
      */
     public long getTime() {
         return elapsedTime;
     }
 
     public boolean isSuccessful() {
         return success;
     }
 
     /**
      * Sets the data type of the sample.
      * @param dataType String containing {@link #BINARY} or {@link #TEXT}
      * @see #BINARY
      * @see #TEXT
      */
     public void setDataType(String dataType) {
         this.dataType = dataType;
     }
 
     /**
      * Returns the data type of the sample.
      * 
      * @return String containing {@link #BINARY} or {@link #TEXT} or the empty string
      * @see #BINARY
      * @see #TEXT
      */
     public String getDataType() {
         return dataType;
     }
 
     /**
      * Extract and save the DataEncoding and DataType from the parameter provided.
      * Does not save the full content Type.
      * @see #setContentType(String) which should be used to save the full content-type string
      *
      * @param ct - content type (may be null)
      */
     public void setEncodingAndType(String ct){
         if (ct != null) {
             // Extract charset and store as DataEncoding
             // N.B. The meta tag:
             // <META http-equiv="content-type" content="text/html; charset=foobar">
             // is now processed by HTTPSampleResult#getDataEncodingWithDefault
             final String charsetPrefix = "charset="; // $NON-NLS-1$
             int cset = ct.toLowerCase(java.util.Locale.ENGLISH).indexOf(charsetPrefix);
             if (cset >= 0) {
                 String charSet = ct.substring(cset + charsetPrefix.length());
                 // handle: ContentType: text/plain; charset=ISO-8859-1; format=flowed
                 int semiColon = charSet.indexOf(';');
                 if (semiColon >= 0) {
                     charSet=charSet.substring(0, semiColon);
                 }
                 // Check for quoted string
                 if (charSet.startsWith("\"")||charSet.startsWith("\'")){ // $NON-NLS-1$
                     setDataEncoding(charSet.substring(1, charSet.length()-1)); // remove quotes
                 } else {
                     setDataEncoding(charSet);
                 }
             }
             if (isBinaryType(ct)) {
                 setDataType(BINARY);
             } else {
                 setDataType(TEXT);
             }
         }
     }
 
     /*
      * Determine if content-type is known to be binary, i.e. not displayable as text.
      *
      * @param ct content type
      * @return true if content-type is of type binary.
      */
     private static boolean isBinaryType(String ct){
         for (String entry : NON_BINARY_TYPES){
             if (ct.startsWith(entry)){
                 return false;
             }
         }
         for (String binaryType : BINARY_TYPES) {
             if (ct.startsWith(binaryType)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Sets the successful attribute of the SampleResult object.
      *
      * @param success
      *            the new successful value
      */
     public void setSuccessful(boolean success) {
         this.success = success;
     }
 
     /**
      * Returns the display name.
      *
      * @return display name of this sample result
      */
     @Override
     public String toString() {
         return getSampleLabel();
     }
 
     /**
      * Returns the dataEncoding or the default if no dataEncoding was provided.
      * 
      * @return the value of the dataEncoding or DEFAULT_ENCODING
      */
     public String getDataEncodingWithDefault() {
         return getDataEncodingWithDefault(DEFAULT_ENCODING);
     }
 
     /**
      * Returns the dataEncoding or the default if no dataEncoding was provided.
      * 
      * @param defaultEncoding the default to be applied
      * @return the value of the dataEncoding or the provided default
      */
     protected String getDataEncodingWithDefault(String defaultEncoding) {
         if (dataEncoding != null && dataEncoding.length() > 0) {
             return dataEncoding;
         }
         return defaultEncoding;
     }
 
     /**
      * Returns the dataEncoding. May be null or the empty String.
      * @return the value of the dataEncoding
      */
     public String getDataEncodingNoDefault() {
         return dataEncoding;
     }
 
     /**
      * Sets the dataEncoding.
      *
      * @param dataEncoding
      *            the dataEncoding to set, e.g. ISO-8895-1, UTF-8
      */
     public void setDataEncoding(String dataEncoding) {
         this.dataEncoding = dataEncoding;
     }
 
     /**
      * @return whether to stop the test
      */
     public boolean isStopTest() {
         return stopTest;
     }
 
     /**
      * @return whether to stop the test now
      */
     public boolean isStopTestNow() {
         return stopTestNow;
     }
 
     /**
      * @return whether to stop this thread
      */
     public boolean isStopThread() {
         return stopThread;
     }
 
     public void setStopTest(boolean b) {
         stopTest = b;
     }
 
     public void setStopTestNow(boolean b) {
         stopTestNow = b;
     }
 
     public void setStopThread(boolean b) {
         stopThread = b;
     }
 
     /**
      * @return the request headers
      */
     public String getRequestHeaders() {
         return requestHeaders;
     }
 
     /**
      * @return the response headers
      */
     public String getResponseHeaders() {
         return responseHeaders;
     }
 
     /**
      * @param string -
      *            request headers
      */
     public void setRequestHeaders(String string) {
         requestHeaders = string;
     }
 
     /**
      * @param string -
      *            response headers
      */
     public void setResponseHeaders(String string) {
         responseHeaders = string;
     }
 
     /**
      * @return the full content type - e.g. text/html [;charset=utf-8 ]
      */
     public String getContentType() {
         return contentType;
     }
 
     /**
      * Get the media type from the Content Type
      * @return the media type - e.g. text/html (without charset, if any)
      */
     public String getMediaType() {
         return JOrphanUtils.trim(contentType," ;").toLowerCase(java.util.Locale.ENGLISH);
     }
 
     /**
      * Stores the content-type string, e.g. <code>text/xml; charset=utf-8</code>
      * @see #setEncodingAndType(String) which can be used to extract the charset.
      *
      * @param string the content-type to be set
      */
     public void setContentType(String string) {
         contentType = string;
     }
 
     /**
      * @return idleTime
      */
     public long getIdleTime() {
         return idleTime;
     }
 
     /**
      * @return the end time
      */
     public long getEndTime() {
         return endTime;
     }
 
     /**
      * @return the start time
      */
     public long getStartTime() {
         return startTime;
     }
 
     /*
      * Helper methods N.B. setStartTime must be called before setEndTime
      *
      * setStartTime is used by HTTPSampleResult to clone the parent sampler and
      * allow the original start time to be kept
      */
     protected final void setStartTime(long start) {
         startTime = start;
         if (START_TIMESTAMP) {
             timeStamp = startTime;
         }
     }
 
     public void setEndTime(long end) {
diff --git a/src/core/org/apache/jmeter/util/JMeterUtils.java b/src/core/org/apache/jmeter/util/JMeterUtils.java
index 6d0b26358..ab783f1b0 100644
--- a/src/core/org/apache/jmeter/util/JMeterUtils.java
+++ b/src/core/org/apache/jmeter/util/JMeterUtils.java
@@ -1,1158 +1,1157 @@
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
 
 package org.apache.jmeter.util;
 
 import java.awt.Dialog;
 import java.awt.Font;
 import java.awt.Frame;
 import java.awt.HeadlessException;
 import java.awt.Window;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.lang.reflect.InvocationTargetException;
 import java.net.InetAddress;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.MissingResourceException;
 import java.util.Properties;
 import java.util.ResourceBundle;
 import java.util.Set;
 import java.util.Vector;
 import java.util.concurrent.ThreadLocalRandom;
 
 import javax.swing.ImageIcon;
 import javax.swing.JOptionPane;
 import javax.swing.JTable;
 import javax.swing.SwingUtilities;
 import javax.swing.UIManager;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.test.UnitTestManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.PatternCacheLRU;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * This class contains the static utility methods used by JMeter.
  *
  */
 public class JMeterUtils implements UnitTestManager {
     private static final Logger log = LoggingManager.getLoggerForClass();
     
     // Note: cannot use a static variable here, because that would be processed before the JMeter properties
     // have been defined (Bug 52783)
     private static class LazyPatternCacheHolder {
         private LazyPatternCacheHolder() {
             super();
         }
         public static final PatternCacheLRU INSTANCE = new PatternCacheLRU(
                 getPropDefault("oro.patterncache.size",1000), // $NON-NLS-1$
                 new Perl5Compiler());
     }
 
     public static final String RES_KEY_PFX = "[res_key="; // $NON-NLS-1$
 
     private static final String EXPERT_MODE_PROPERTY = "jmeter.expertMode"; // $NON-NLS-1$
     
     private static final String ENGLISH_LANGUAGE = Locale.ENGLISH.getLanguage();
 
     private static volatile Properties appProperties;
 
     private static final Vector<LocaleChangeListener> localeChangeListeners = new Vector<>();
 
     private static volatile Locale locale;
 
     private static volatile ResourceBundle resources;
 
     // What host am I running on?
 
     //@GuardedBy("this")
     private static String localHostIP = null;
     //@GuardedBy("this")
     private static String localHostName = null;
     //@GuardedBy("this")
     private static String localHostFullName = null;
     
     // TODO needs to be synch? Probably not changed after threads have started
     private static String jmDir; // JMeter Home directory (excludes trailing separator)
     private static String jmBin; // JMeter bin directory (excludes trailing separator)
 
     private static volatile boolean ignoreResorces = false; // Special flag for use in debugging resources
 
     private static final ThreadLocal<Perl5Matcher> localMatcher = new ThreadLocal<Perl5Matcher>() {
         @Override
         protected Perl5Matcher initialValue() {
             return new Perl5Matcher();
         }
     };
 
     /**
      * Gets Perl5Matcher for this thread.
      * @return the {@link Perl5Matcher} for this thread
      */
     public static Perl5Matcher getMatcher() {
         return localMatcher.get();
     }
 
     /**
      * This method is used by the init method to load the property file that may
      * even reside in the user space, or in the classpath under
      * org.apache.jmeter.jmeter.properties.
      *
      * The method also initialises logging and sets up the default Locale
      *
      * TODO - perhaps remove?
      * [still used
      *
      * @param file
      *            the file to load
      * @return the Properties from the file
      * @see #getJMeterProperties()
      * @see #loadJMeterProperties(String)
-     * @see #initLogging()
      * @see #initLocale()
      */
     public static Properties getProperties(String file) {
         loadJMeterProperties(file);
-        initLogging();
         initLocale();
         return appProperties;
     }
 
     /**
      * Initialise JMeter logging
+     * @deprecated
      */
+    @Deprecated
     public static void initLogging() {
-        LoggingManager.initializeLogging(appProperties);
     }
 
     /**
      * Initialise the JMeter Locale
      */
     public static void initLocale() {
         String loc = appProperties.getProperty("language"); // $NON-NLS-1$
         if (loc != null) {
             String []parts = JOrphanUtils.split(loc,"_");// $NON-NLS-1$
             if (parts.length==2) {
                 setLocale(new Locale(parts[0], parts[1]));
             } else {
                 setLocale(new Locale(loc, "")); // $NON-NLS-1$
             }
 
         } else {
             setLocale(Locale.getDefault());
         }
     }
 
 
     /**
      * Load the JMeter properties file; if not found, then
      * default to "org/apache/jmeter/jmeter.properties" from the classpath
      *
      * <p>
      * c.f. loadProperties
      *
      * @param file Name of the file from which the JMeter properties should be loaded
      */
     public static void loadJMeterProperties(String file) {
         Properties p = new Properties(System.getProperties());
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 is =
                     ClassLoader.getSystemResourceAsStream("org/apache/jmeter/jmeter.properties"); // $NON-NLS-1$
                 if (is == null) {
                     throw new RuntimeException("Could not read JMeter properties file:"+file);
                 }
                 p.load(is);
             } catch (IOException ex) {
                 // JMeter.fail("Could not read internal resource. " +
                 // "Archive is broken.");
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         appProperties = p;
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @return the Properties from the file, may be null (e.g. file not found)
      */
     public static Properties loadProperties(String file) {
         return loadProperties(file, null);
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @param defaultProps a set of default properties
      * @return the Properties from the file; if it could not be processed, the defaultProps are returned.
      */
     public static Properties loadProperties(String file, Properties defaultProps) {
         Properties p = new Properties(defaultProps);
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 final URL resource = JMeterUtils.class.getClassLoader().getResource(file);
                 if (resource == null) {
                     log.warn("Cannot find " + file);
                     return defaultProps;
                 }
                 is = resource.openStream();
                 if (is == null) {
                     log.warn("Cannot open " + file);
                     return defaultProps;
                 }
                 p.load(is);
             } catch (IOException ex) {
                 log.warn("Error reading " + file + " " + ex.toString());
                 return defaultProps;
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         return p;
     }
 
     public static PatternCacheLRU getPatternCache() {
         return LazyPatternCacheHolder.INSTANCE;
     }
 
     /**
      * Get a compiled expression from the pattern cache (READ_ONLY).
      *
      * @param expression regular expression to be looked up
      * @return compiled pattern
      *
      * @throws MalformedCachePatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression) throws MalformedCachePatternException {
         return getPattern(expression, Perl5Compiler.READ_ONLY_MASK);
     }
 
     /**
      * Get a compiled expression from the pattern cache.
      *
      * @param expression RE
      * @param options e.g. {@link Perl5Compiler#READ_ONLY_MASK READ_ONLY_MASK}
      * @return compiled pattern
      *
      * @throws MalformedCachePatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression, int options) throws MalformedCachePatternException {
         return LazyPatternCacheHolder.INSTANCE.getPattern(expression, options);
     }
 
     @Override
     public void initializeProperties(String file) {
         System.out.println("Initializing Properties: " + file); // NOSONAR intentional
         getProperties(file);
     }
 
     /**
      * Convenience method for
      * {@link ClassFinder#findClassesThatExtend(String[], Class[], boolean)}
      * with the option to include inner classes in the search set to false
      * and the path list is derived from JMeterUtils.getSearchPaths().
      *
      * @param superClass - single class to search for
      * @return List of Strings containing discovered class names.
      * @throws IOException when the used {@link ClassFinder} throws one while searching for the class
      */
     public static List<String> findClassesThatExtend(Class<?> superClass)
         throws IOException {
         return ClassFinder.findClassesThatExtend(getSearchPaths(), new Class[]{superClass}, false);
     }
 
     /**
      * Generate a list of paths to search.
      * The output array always starts with
      * JMETER_HOME/lib/ext
      * and is followed by any paths obtained from the "search_paths" JMeter property.
      * 
      * @return array of path strings
      */
     public static String[] getSearchPaths() {
         String p = JMeterUtils.getPropDefault("search_paths", null); // $NON-NLS-1$
         String[] result = new String[1];
 
         if (p != null) {
             String[] paths = p.split(";"); // $NON-NLS-1$
             result = new String[paths.length + 1];
             System.arraycopy(paths, 0, result, 1, paths.length);
         }
         result[0] = getJMeterHome() + "/lib/ext"; // $NON-NLS-1$
         return result;
     }
 
     /**
      * Provide random numbers
      *
      * @param r -
      *            the upper bound (exclusive)
      * @return a random <code>int</code>
      */
     public static int getRandomInt(int r) {
         return ThreadLocalRandom.current().nextInt(r);
     }
 
     /**
      * Changes the current locale: re-reads resource strings and notifies
      * listeners.
      *
      * @param loc -
      *            new locale
      */
     public static void setLocale(Locale loc) {
         log.info("Setting Locale to " + loc.toString());
         /*
          * See bug 29920. getBundle() defaults to the property file for the
          * default Locale before it defaults to the base property file, so we
          * need to change the default Locale to ensure the base property file is
          * found.
          */
         Locale def = null;
         boolean isDefault = false; // Are we the default language?
         if (loc.getLanguage().equals(ENGLISH_LANGUAGE)) {
             isDefault = true;
             def = Locale.getDefault();
             // Don't change locale from en_GB to en
             if (!def.getLanguage().equals(ENGLISH_LANGUAGE)) {
                 Locale.setDefault(Locale.ENGLISH);
             } else {
                 def = null; // no need to reset Locale
             }
         }
         if ("ignoreResources".equals(loc.toString())){ // $NON-NLS-1$
             log.warn("Resource bundles will be ignored");
             ignoreResorces = true;
             // Keep existing settings
         } else {
             ignoreResorces = false;
             ResourceBundle resBund = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", loc); // $NON-NLS-1$
             resources = resBund;
             locale = loc;
             final Locale resBundLocale = resBund.getLocale();
             if (isDefault || resBundLocale.equals(loc)) {// language change worked
             // Check if we at least found the correct language:
             } else if (resBundLocale.getLanguage().equals(loc.getLanguage())) {
                 log.info("Could not find resources for '"+loc.toString()+"', using '"+resBundLocale.toString()+"'");
             } else {
                 log.error("Could not find resources for '"+loc.toString()+"'");
             }
         }
         notifyLocaleChangeListeners();
         /*
          * Reset Locale if necessary so other locales are properly handled
          */
         if (def != null) {
             Locale.setDefault(def);
         }
     }
 
     /**
      * Gets the current locale.
      *
      * @return current locale
      */
     public static Locale getLocale() {
         return locale;
     }
 
     public static void addLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.add(listener);
     }
 
     public static void removeLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.remove(listener);
     }
 
     /**
      * Notify all listeners interested in locale changes.
      *
      */
     private static void notifyLocaleChangeListeners() {
         LocaleChangeEvent event = new LocaleChangeEvent(JMeterUtils.class, locale);
         @SuppressWarnings("unchecked") // clone will produce correct type
         // TODO but why do we need to clone the list?
         // ANS: to avoid possible ConcurrentUpdateException when unsubscribing
         // Could perhaps avoid need to clone by using a modern concurrent list
         Vector<LocaleChangeListener> listeners = (Vector<LocaleChangeListener>) localeChangeListeners.clone();
         for (LocaleChangeListener listener : listeners) {
             listener.localeChanged(event);
         }
     }
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      */
     public static String getResString(String key) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]"); // $NON-NLS-1$
     }
     
     /**
      * Gets the resource string for this key in Locale.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param forcedLocale Force a particular locale
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      * @since 2.7
      */
     public static String getResString(String key, Locale forcedLocale) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]", // $NON-NLS-1$
                 forcedLocale); 
     }
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param defaultValue -
      *            the default value
      *
      * @return the resource string if the key is found; otherwise, return the
      *         default
      * @deprecated Only intended for use in development; use
      *             getResString(String) normally
      */
     @Deprecated
     public static String getResString(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue);
     }
 
     /**
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue, null);
     }
     /**
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue, Locale forcedLocale) {
         if (key == null) {
             return null;
         }
         // Resource keys cannot contain spaces, and are forced to lower case
         String resKey = key.replace(' ', '_'); // $NON-NLS-1$ // $NON-NLS-2$
         resKey = resKey.toLowerCase(java.util.Locale.ENGLISH);
         String resString = null;
         try {
             ResourceBundle bundle = resources;
             if(forcedLocale != null) {
                 bundle = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", forcedLocale); // $NON-NLS-1$
             }
             if (bundle.containsKey(resKey)) {
                 resString = bundle.getString(resKey);
             } else {
                 log.warn("ERROR! Resource string not found: [" + resKey + "]");
                 resString = defaultValue;                
             }
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "["+key+"]";
             }
         } catch (MissingResourceException mre) {
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "[?"+key+"?]";
             }
             log.warn("ERROR! Resource string not found: [" + resKey + "]", mre);
             resString = defaultValue;
         }
         return resString;
     }
 
     /**
      * To get I18N label from properties file
      * 
      * @param key
      *            in messages.properties
      * @return I18N label without (if exists) last colon ':' and spaces
      */
     public static String getParsedLabel(String key) {
         String value = JMeterUtils.getResString(key);
         return value.replaceFirst("(?m)\\s*?:\\s*$", ""); // $NON-NLS-1$ $NON-NLS-2$
     }
     
     /**
      * Get the locale name as a resource.
      * Does not log an error if the resource does not exist.
      * This is needed to support additional locales, as they won't be in existing messages files.
      *
      * @param locale name
      * @return the locale display name as defined in the current Locale or the original string if not present
      */
     public static String getLocaleString(String locale){
         // All keys in messages.properties are lowercase (historical reasons?)
         String resKey = locale.toLowerCase(java.util.Locale.ENGLISH);
         if (resources.containsKey(resKey)) {
             return resources.getString(resKey);
         }
         return locale;
     }
     /**
      * This gets the currently defined appProperties. It can only be called
      * after the {@link #getProperties(String)} or {@link #loadJMeterProperties(String)} 
      * method has been called.
      *
      * @return The JMeterProperties value, 
      *         may be null if {@link #loadJMeterProperties(String)} has not been called
      * @see #getProperties(String)
      * @see #loadJMeterProperties(String)
      */
     public static Properties getJMeterProperties() {
         return appProperties;
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images.&lt;name&gt;
      *
      * @param name
      *            Description of Parameter
      * @return The Image value
      */
     public static ImageIcon getImage(String name) {
         try {
             URL url = JMeterUtils.class.getClassLoader().getResource(
                     "org/apache/jmeter/images/" + name.trim());
             if(url != null) {
                 return new ImageIcon(url); // $NON-NLS-1$
             } else {
                 log.warn("no icon for " + name);
                 return null;                
             }
         } catch (NoClassDefFoundError | InternalError e) {// Can be returned by headless hosts
             log.info("no icon for " + name + " " + e.getMessage());
             return null;
         }
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images.<em>&lt;name&gt;</em>, and also sets the description
      * of the image, which is useful if the icon is going to be placed
      * on the clipboard.
      *
      * @param name
      *            the name of the image
      * @param description
      *            the description of the image
      * @return The Image value
      */
     public static ImageIcon getImage(String name, String description) {
         ImageIcon icon = getImage(name);
         if(icon != null) {
             icon.setDescription(description);
         }
         return icon;
     }
 
     public static String getResourceFileAsText(String name) {
         BufferedReader fileReader = null;
         try {
             String lineEnd = System.getProperty("line.separator"); // $NON-NLS-1$
             InputStream is = JMeterUtils.class.getClassLoader().getResourceAsStream(name);
             if(is != null) {
                 fileReader = new BufferedReader(new InputStreamReader(is));
                 StringBuilder text = new StringBuilder();
                 String line;
                 while ((line = fileReader.readLine()) != null) {
                     text.append(line);
                     text.append(lineEnd);
                 }
                 return text.toString();
             } else {
                 return ""; // $NON-NLS-1$                
             }
         } catch (IOException e) {
             return ""; // $NON-NLS-1$
         } finally {
             IOUtils.closeQuietly(fileReader);
         }
     }
 
     /**
      * Get a int value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static int getPropDefault(String propName, int defaultVal) {
         int ans;
         try {
             ans = Integer.parseInt(appProperties.getProperty(propName, Integer.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching int property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a boolean value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static boolean getPropDefault(String propName, boolean defaultVal) {
         boolean ans;
         try {
             String strVal = appProperties.getProperty(propName, Boolean.toString(defaultVal)).trim();
             if ("true".equalsIgnoreCase(strVal) || "t".equalsIgnoreCase(strVal)) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = true;
             } else if ("false".equalsIgnoreCase(strVal) || "f".equalsIgnoreCase(strVal)) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = false;
             } else {
                 ans = Integer.parseInt(strVal) == 1;
             }
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching boolean property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a long value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static long getPropDefault(String propName, long defaultVal) {
         long ans;
         try {
             ans = Long.parseLong(appProperties.getProperty(propName, Long.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching long property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
     
     /**
      * Get a float value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static float getPropDefault(String propName, float defaultVal) {
         float ans;
         try {
             ans = Float.parseFloat(appProperties.getProperty(propName, Float.toString(defaultVal)).trim());
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching float property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a String value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value applying a trim on it
      */
     public static String getPropDefault(String propName, String defaultVal) {
         String ans = defaultVal;
         try 
         {
             String value = appProperties.getProperty(propName, defaultVal);
             if(value != null) {
                 ans = value.trim();
             }
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching String property:'"+propName+"', defaulting to:"+defaultVal);
             ans = defaultVal;
         }
         return ans;
     }
     
     /**
      * Get the value of a JMeter property.
      *
      * @param propName
      *            the name of the property.
      * @return the value of the JMeter property, or null if not defined
      */
     public static String getProperty(String propName) {
         String ans = null;
         try {
             ans = appProperties.getProperty(propName);
         } catch (Exception e) {
             log.warn("Exception '"+ e.getMessage()+ "' occurred when fetching String property:'"+propName+"'");
             ans = null;
         }
         return ans;
     }
 
     /**
      * Set a String value
      *
      * @param propName
      *            the name of the property.
      * @param propValue
      *            the value of the property
      * @return the previous value of the property
      */
     public static Object setProperty(String propName, String propValue) {
         return appProperties.setProperty(propName, propValue);
     }
 
     /**
      * Report an error through a dialog box.
      * Title defaults to "error_title" resource string
      * @param errorMsg - the error message.
      */
     public static void reportErrorToUser(String errorMsg) {
         reportErrorToUser(errorMsg, JMeterUtils.getResString("error_title"), null); // $NON-NLS-1$
     }
 
     /**
      * Report an error through a dialog box in GUI mode 
      * or in logs and stdout in Non GUI mode
      *
      * @param errorMsg - the error message.
      * @param titleMsg - title string
      */
     public static void reportErrorToUser(String errorMsg, String titleMsg) {
         reportErrorToUser(errorMsg, titleMsg, null);
     }
 
     /**
      * Report an error through a dialog box.
      * Title defaults to "error_title" resource string
      * @param errorMsg - the error message.
      * @param exception {@link Exception}
      */
     public static void reportErrorToUser(String errorMsg, Exception exception) {
         reportErrorToUser(errorMsg, JMeterUtils.getResString("error_title"), exception);
     }
 
     /**
      * Report an error through a dialog box in GUI mode 
      * or in logs and stdout in Non GUI mode
      *
      * @param errorMsg - the error message.
      * @param titleMsg - title string
      * @param exception Exception
      */
     public static void reportErrorToUser(String errorMsg, String titleMsg, Exception exception) {
         if (errorMsg == null) {
             errorMsg = "Unknown error - see log file";
             log.warn("Unknown error", new Throwable("errorMsg == null"));
         }
         GuiPackage instance = GuiPackage.getInstance();
         if (instance == null) {
             if(exception != null) {
                 log.error(errorMsg, exception);
             } else {
                 log.error(errorMsg);
             }
             System.out.println(errorMsg); // NOSONAR intentional
             return; // Done
         }
         try {
             JOptionPane.showMessageDialog(instance.getMainFrame(),
                     errorMsg,
                     titleMsg,
                     JOptionPane.ERROR_MESSAGE);
         } catch (HeadlessException e) {
             log.warn("reportErrorToUser(\"" + errorMsg + "\") caused", e);
         }
     }
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             }
             if (count + 1 < splittee.length && splittee[count + 1] != null) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
 
     // End Method
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @param def
      *            Default value to replace null values in array.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar, String def) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             } else {
                 retVal.append(def);
             }
             if (count + 1 < splittee.length) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
     
     /**
      * @return true if test is running
      */
     public static boolean isTestRunning() {
         return JMeterContextService.getTestStartTime()>0;
     }
 
     /**
      * Get the JMeter home directory - does not include the trailing separator.
      *
      * @return the home directory
      */
     public static String getJMeterHome() {
         return jmDir;
     }
 
     /**
      * Get the JMeter bin directory - does not include the trailing separator.
      *
      * @return the bin directory
      */
     public static String getJMeterBinDir() {
         return jmBin;
     }
 
     public static void setJMeterHome(String home) {
         jmDir = home;
         jmBin = jmDir + File.separator + "bin"; // $NON-NLS-1$
     }
 
     /**
      * Gets the JMeter Version.
      *
      * @return the JMeter version string
      */
     public static String getJMeterVersion() {
         return JMeterVersion.getVERSION();
     }
 
     /**
      * Gets the JMeter copyright.
      *
      * @return the JMeter copyright string
      */
     public static String getJMeterCopyright() {
         return JMeterVersion.getCopyRight();
     }
 
     /**
      * Determine whether we are in 'expert' mode. Certain features may be hidden
      * from user's view unless in expert mode.
      *
      * @return true iif we're in expert mode
      */
     public static boolean isExpertMode() {
         return JMeterUtils.getPropDefault(EXPERT_MODE_PROPERTY, false);
     }
 
     /**
      * Find a file in the current directory or in the JMeter bin directory.
      *
      * @param fileName the name of the file to find
      * @return File object
      */
     public static File findFile(String fileName){
         File f =new File(fileName);
         if (!f.exists()){
             f=new File(getJMeterBinDir(),fileName);
         }
         return f;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostAddress()
      *
      * @return String representation of local IP address
      */
     public static synchronized String getLocalHostIP(){
         if (localHostIP == null) {
             getLocalHostDetails();
         }
         return localHostIP;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostName()
      *
      * @return local host name
      */
     public static synchronized String getLocalHostName(){
         if (localHostName == null) {
             getLocalHostDetails();
         }
         return localHostName;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getCanonicalHostName()
      *
      * @return local host name in canonical form
      */
     public static synchronized String getLocalHostFullName(){
         if (localHostFullName == null) {
             getLocalHostDetails();
         }
         return localHostFullName;
     }
 
     private static void getLocalHostDetails(){
         InetAddress localHost=null;
         try {
             localHost = InetAddress.getLocalHost();
         } catch (UnknownHostException e1) {
             log.error("Unable to get local host IP address.", e1);
             return; // TODO - perhaps this should be a fatal error?
         }
         localHostIP=localHost.getHostAddress();
         localHostName=localHost.getHostName();
         localHostFullName=localHost.getCanonicalHostName();
     }
     
     /**
      * Split line into name/value pairs and remove colon ':'
      * 
      * @param headers
      *            multi-line string headers
      * @return a map name/value for each header
      */
     public static LinkedHashMap<String, String> parseHeaders(String headers) {
         LinkedHashMap<String, String> linkedHeaders = new LinkedHashMap<>();
         String[] list = headers.split("\n"); // $NON-NLS-1$
         for (String header : list) {
             int colon = header.indexOf(':'); // $NON-NLS-1$
             if (colon <= 0) {
                 linkedHeaders.put(header, ""); // Empty value // $NON-NLS-1$
             } else {
                 linkedHeaders.put(header.substring(0, colon).trim(), header
                         .substring(colon + 1).trim());
             }
         }
         return linkedHeaders;
     }
 
     /**
      * Run the runnable in AWT Thread if current thread is not AWT thread
      * otherwise runs call {@link SwingUtilities#invokeAndWait(Runnable)}
      * @param runnable {@link Runnable}
      */
     public static void runSafe(Runnable runnable) {
         runSafe(true, runnable);
     }
 
     /**
      * Run the runnable in AWT Thread if current thread is not AWT thread
      * otherwise runs call {@link SwingUtilities#invokeAndWait(Runnable)}
      * @param synchronous flag, whether we will wait for the AWT Thread to finish its job.
      * @param runnable {@link Runnable}
      */
     public static void runSafe(boolean synchronous, Runnable runnable) {
         if(SwingUtilities.isEventDispatchThread()) {
             runnable.run();//NOSONAR
         } else {
             if (synchronous) {
                 try {
                     SwingUtilities.invokeAndWait(runnable);
                 } catch (InterruptedException e) {
                     log.warn("Interrupted in thread "
                             + Thread.currentThread().getName(), e);
                     Thread.currentThread().interrupt();
                 } catch (InvocationTargetException e) {
                     throw new Error(e);
                 }
             } else {
                 SwingUtilities.invokeLater(runnable);
             }
         }
     }
 
     /**
      * Help GC by triggering GC and finalization
      */
     public static void helpGC() {
         System.gc(); // NOSONAR Intentional
         System.runFinalization();
     }
 
     /**
      * Hack to make matcher clean the two internal buffers it keeps in memory which size is equivalent to 
      * the unzipped page size
      * @param matcher {@link Perl5Matcher}
      * @param pattern Pattern
      */
     public static void clearMatcherMemory(Perl5Matcher matcher, Pattern pattern) {
         try {
             if (pattern != null) {
                 matcher.matches("", pattern); // $NON-NLS-1$
             }
         } catch (Exception e) {
             // NOOP
         }
     }
 
     /**
      * Provide info, whether we run in HiDPI mode
      * @return {@code true} if we run in HiDPI mode, {@code false} otherwise
      */
     public static boolean getHiDPIMode() {
         return JMeterUtils.getPropDefault("jmeter.hidpi.mode", false);  // $NON-NLS-1$
     }
 
     /**
      * Provide info about the HiDPI scale factor
      * @return the factor by which we should scale elements for HiDPI mode
      */
     public static double getHiDPIScaleFactor() {
         return Double.parseDouble(JMeterUtils.getPropDefault("jmeter.hidpi.scale.factor", "1.0"));  // $NON-NLS-1$  $NON-NLS-2$
     }
 
     /**
      * Apply HiDPI mode management to {@link JTable}
      * @param table the {@link JTable} which should be adapted for HiDPI mode
      */
     public static void applyHiDPI(JTable table) {
         if (JMeterUtils.getHiDPIMode()) {
             table.setRowHeight((int) Math.round(table.getRowHeight() * JMeterUtils.getHiDPIScaleFactor()));
         }
     }
 
     /**
      * Return delimiterValue handling the TAB case
      * @param delimiterValue Delimited value 
      * @return String delimited modified to handle correctly tab
      * @throws JMeterError if delimiterValue has a length different from 1
      */
     public static String getDelimiter(String delimiterValue) {
         if ("\\t".equals(delimiterValue)) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
             delimiterValue="\t";
         }
 
         if (delimiterValue.length() != 1){
             throw new JMeterError("Delimiter '"+delimiterValue+"' must be of length 1.");
         }
         return delimiterValue;
     }
 
     /**
      * Apply HiDPI scale factor on font if HiDPI mode is enabled
      */
     public static void applyHiDPIOnFonts() {
         if (!getHiDPIMode()) {
             return;
