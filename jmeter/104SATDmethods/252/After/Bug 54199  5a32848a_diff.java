diff --git a/src/reports/org/apache/jmeter/JMeterReport.java b/src/reports/org/apache/jmeter/JMeterReport.java
index 9bbb5459d..3abdbb569 100644
--- a/src/reports/org/apache/jmeter/JMeterReport.java
+++ b/src/reports/org/apache/jmeter/JMeterReport.java
@@ -1,404 +1,406 @@
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
 import java.util.Enumeration;
 import java.util.List;
 import java.util.Properties;
 
 import org.apache.commons.cli.avalon.CLArgsParser;
 import org.apache.commons.cli.avalon.CLOption;
 import org.apache.commons.cli.avalon.CLOptionDescriptor;
 import org.apache.commons.cli.avalon.CLUtil;
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.gui.ReportGui;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.plugin.JMeterPlugin;
 import org.apache.jmeter.plugin.PluginManager;
 import org.apache.jmeter.report.gui.ReportPageGui;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.report.gui.action.ReportCheckDirty;
 import org.apache.jmeter.report.gui.action.ReportLoad;
 import org.apache.jmeter.report.gui.tree.ReportTreeListener;
 import org.apache.jmeter.report.gui.tree.ReportTreeModel;
 import org.apache.jmeter.report.writers.gui.HTMLReportWriterGui;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  *
  * JMeterReport is the main class for the reporting component. For now,
  * the plan is to make the reporting component a separate GUI, which
  * can run in GUI or console mode. The purpose of the GUI is to design
  * reports, which can then be run. One of the primary goals of the
  * reporting component is to make it so the reports can be run in an
  * automated process.
  * The report GUI is different than the main JMeter GUI in several ways.
  * <ul>
  *   <li> the gui is not multi-threaded</li>
  *   <li> the gui uses different components</li>
  *   <li> the gui is focused on designing reports from the jtl logs
  * generated during a test run</li>
  * </ul>
  * The class follows the same design as JMeter.java. This should keep
  * things consistent and make it easier to maintain.
  */
 public class JMeterReport implements JMeterPlugin {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final int PROPFILE_OPT = 'p';
 
     private static final int PROPFILE2_OPT = 'q'; // Bug 33920 - additional
                                                     // prop files
 
     private static final int TESTFILE_OPT = 't';
 
     private static final int LOGFILE_OPT = 'l';
 
     private static final int NONGUI_OPT = 'n';
 
     private static final int HELP_OPT = 'h';
 
     private static final int VERSION_OPT = 'v';
 
     private static final int SERVER_OPT = 's';
 
     private static final int JMETER_PROPERTY = 'J';
 
     private static final int SYSTEM_PROPERTY = 'D';
 
     private static final int LOGLEVEL = 'L';
 
     private static final int REMOTE_OPT = 'r';
 
     private static final int JMETER_HOME_OPT = 'd';
 
     private static final CLOptionDescriptor[] options = new CLOptionDescriptor[] {
             new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED, HELP_OPT,
                     "print usage information and exit"),
             new CLOptionDescriptor("version", CLOptionDescriptor.ARGUMENT_DISALLOWED, VERSION_OPT,
                     "print the version information and exit"),
             new CLOptionDescriptor("propfile", CLOptionDescriptor.ARGUMENT_REQUIRED, PROPFILE_OPT,
                     "the jmeter property file to use"),
             new CLOptionDescriptor("addprop", CLOptionDescriptor.ARGUMENT_REQUIRED
                     | CLOptionDescriptor.DUPLICATES_ALLOWED, // Bug 33920 -
                                                                 // allow
                                                                 // multiple
                                                                 // props
                     PROPFILE2_OPT, "additional property file(s)"),
             new CLOptionDescriptor("testfile", CLOptionDescriptor.ARGUMENT_REQUIRED, TESTFILE_OPT,
                     "the jmeter test(.jmx) file to run"),
             new CLOptionDescriptor("logfile", CLOptionDescriptor.ARGUMENT_REQUIRED, LOGFILE_OPT,
                     "the file to log samples to"),
             new CLOptionDescriptor("nongui", CLOptionDescriptor.ARGUMENT_DISALLOWED, NONGUI_OPT,
                     "run JMeter in nongui mode"),
             new CLOptionDescriptor("server", CLOptionDescriptor.ARGUMENT_DISALLOWED, SERVER_OPT,
                     "run the JMeter server"),
             new CLOptionDescriptor("jmeterproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_PROPERTY, "Define additional JMeter properties"),
             new CLOptionDescriptor("systemproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, SYSTEM_PROPERTY, "Define additional JMeter properties"),
             new CLOptionDescriptor("loglevel", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, LOGLEVEL,
                     "Define loglevel: [category=]level e.g. jorphan=INFO or " + "jmeter.util=DEBUG"),
             new CLOptionDescriptor("runremote", CLOptionDescriptor.ARGUMENT_DISALLOWED, REMOTE_OPT,
                     "Start remote servers from non-gui mode"),
             new CLOptionDescriptor("homedir", CLOptionDescriptor.ARGUMENT_REQUIRED, JMETER_HOME_OPT,
                     "the jmeter home directory to use"), };
 
     /**
      *
      */
     public JMeterReport() {
         super();
     }
 
     /**
      * The default icons for the report GUI.
      */
     private static final String[][] DEFAULT_ICONS = {
             { AbstractListenerGui.class.getName(), "org/apache/jmeter/images/meter.png" },
             { AbstractConfigGui.class.getName(), "org/apache/jmeter/images/testtubes.png" },
             { HTMLReportWriterGui.class.getName(), "org/apache/jmeter/images/new/pencil.png" },
             { ReportPageGui.class.getName(), "org/apache/jmeter/images/new/scroll.png" },
             { ReportGui.class.getName(), "org/apache/jmeter/images/new/book.png" }
     };
 
     /** {@inheritDoc} */
+    @Override
     public String[][] getIconMappings() {
         String iconProp = JMeterUtils.getPropDefault("jmeter.icons", "org/apache/jmeter/images/icon.properties");
         Properties p = JMeterUtils.loadProperties(iconProp);
         if (p == null) {
             log.info(iconProp + " not found - using default icon set");
             return DEFAULT_ICONS;
         }
         log.info("Loaded icon properties from " + iconProp);
         String[][] iconlist = new String[p.size()][3];
         Enumeration<Object> pe = p.keys();
         int i = 0;
         while (pe.hasMoreElements()) {
             String key = (String) pe.nextElement();
             String icons[] = JOrphanUtils.split(p.getProperty(key), " ");
             iconlist[i][0] = key;
             iconlist[i][1] = icons[0];
             if (icons.length > 1){
                 iconlist[i][2] = icons[1];
             }
             i++;
         }
         return iconlist;
     }
 
     /** {@inheritDoc} */
+    @Override
     public String[][] getResourceBundles() {
         return new String[0][];
     }
 
     public void startNonGui(CLOption testFile, CLOption logFile){
         System.setProperty(JMeter.JMETER_NON_GUI, "true");
         PluginManager.install(this, false);
     }
 
     public void startGui(CLOption testFile) {
         PluginManager.install(this, true);
         ReportTreeModel treeModel = new ReportTreeModel();
         ReportTreeListener treeLis = new ReportTreeListener(treeModel);
         treeLis.setActionHandler(ReportActionRouter.getInstance());
         ReportGuiPackage.getInstance(treeLis, treeModel);
         org.apache.jmeter.gui.ReportMainFrame main =
             new org.apache.jmeter.gui.ReportMainFrame(
                 treeModel, treeLis);
         ComponentUtil.centerComponentInWindow(main, 80);
         main.setVisible(true);
 
         ReportActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, ReportCheckDirty.ADD_ALL));
         if (testFile != null) {
             FileInputStream reader = null;
             try {
                 File f = new File(testFile.getArgument());
                 log.info("Loading file: " + f);
                 reader = new FileInputStream(f);
                 HashTree tree = SaveService.loadTree(reader);
 
                 ReportGuiPackage.getInstance().setReportPlanFile(f.getAbsolutePath());
 
                 new ReportLoad().insertLoadedTree(1, tree);
             } catch (Exception e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(e.toString());
             }
             finally{
                 JOrphanUtils.closeQuietly(reader);
             }
         }
     }
 
 //    private void run(String testFile, String logFile, boolean remoteStart) {
 //        FileInputStream reader = null;
 //        try {
 //            File f = new File(testFile);
 //            if (!f.exists() || !f.isFile()) {
 //                System.out.println("Could not open " + testFile);
 //                return;
 //            }
 //            FileServer.getFileServer().setBasedir(f.getAbsolutePath());
 //
 //            reader = new FileInputStream(f);
 //            log.info("Loading file: " + f);
 //
 //            HashTree tree = SaveService.loadTree(reader);
 //
 //            // Remove the disabled items
 //            // For GUI runs this is done in Start.java
 //            convertSubTree(tree);
 //
 //            if (logFile != null) {
 //                ResultCollector logger = new ResultCollector();
 //                logger.setFilename(logFile);
 //                tree.add(tree.getArray()[0], logger);
 //            }
 //            String summariserName = JMeterUtils.getPropDefault(
 //                    "summariser.name", "");//$NON-NLS-1$
 //            if (summariserName.length() > 0) {
 //                log.info("Creating summariser <" + summariserName + ">");
 //                System.out.println("Creating summariser <" + summariserName + ">");
 //                Summariser summer = new Summariser(summariserName);
 //                tree.add(tree.getArray()[0], summer);
 //            }
 //            tree.add(tree.getArray()[0], new ListenToTest(parent));
 //            System.out.println("Created the tree successfully");
 //            /**
 //            JMeterEngine engine = null;
 //            if (!remoteStart) {
 //                engine = new StandardJMeterEngine();
 //                engine.configure(tree);
 //                System.out.println("Starting the test");
 //                engine.runTest();
 //            } else {
 //                String remote_hosts_string = JMeterUtils.getPropDefault(
 //                        "remote_hosts", "127.0.0.1");
 //                java.util.StringTokenizer st = new java.util.StringTokenizer(
 //                        remote_hosts_string, ",");
 //                List engines = new LinkedList();
 //                while (st.hasMoreElements()) {
 //                    String el = (String) st.nextElement();
 //                    System.out.println("Configuring remote engine for " + el);
 //                    // engines.add(doRemoteInit(el.trim(), tree));
 //                }
 //                System.out.println("Starting remote engines");
 //                Iterator iter = engines.iterator();
 //                while (iter.hasNext()) {
 //                    engine = (JMeterEngine) iter.next();
 //                    engine.runTest();
 //                }
 //                System.out.println("Remote engines have been started");
 //            }
 //            **/
 //        } catch (Exception e) {
 //            System.out.println("Error in NonGUIDriver " + e.toString());
 //            log.error("", e);
 //        }
 //        finally{
 //            JOrphanUtils.closeQuietly(reader);
 //        }
 //    }
 
 
     /**
      *
      * @param args
      */
     public void start(String[] args) {
         CLArgsParser parser = new CLArgsParser(args, options);
         if (null != parser.getErrorString()) {
             System.err.println("Error: " + parser.getErrorString());
             System.out.println("Usage");
             System.out.println(CLUtil.describeOptions(options).toString());
             return;
         }
         try {
             initializeProperties(parser);
             log.info("Version " + JMeterUtils.getJMeterVersion());
             log.info("java.version=" + System.getProperty("java.version"));
             log.info(JMeterUtils.getJMeterCopyright());
             if (parser.getArgumentById(VERSION_OPT) != null) {
                 System.out.println(JMeterUtils.getJMeterCopyright());
                 System.out.println("Version " + JMeterUtils.getJMeterVersion());
             } else if (parser.getArgumentById(HELP_OPT) != null) {
                 System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));
             } else if (parser.getArgumentById(NONGUI_OPT) == null) {
                 startGui(parser.getArgumentById(TESTFILE_OPT));
             } else {
                 startNonGui(parser.getArgumentById(TESTFILE_OPT), parser.getArgumentById(LOGFILE_OPT));
             }
         } catch (Exception e) {
             e.printStackTrace();
             System.out.println("An error occurred: " + e.getMessage());
             System.exit(-1);
         }
     }
 
     private void initializeProperties(CLArgsParser parser) {
         if (parser.getArgumentById(PROPFILE_OPT) != null) {
             JMeterUtils.getProperties(parser.getArgumentById(PROPFILE_OPT).getArgument());
         } else {
             JMeterUtils.getProperties(NewDriver.getJMeterDir() + File.separator + "bin" + File.separator
                     + "jmeter.properties");
         }
 
         // Bug 33845 - allow direct override of Home dir
         if (parser.getArgumentById(JMETER_HOME_OPT) == null) {
             JMeterUtils.setJMeterHome(NewDriver.getJMeterDir());
         } else {
             JMeterUtils.setJMeterHome(parser.getArgumentById(JMETER_HOME_OPT).getArgument());
         }
 
         // Process command line property definitions (can occur multiple times)
 
         Properties jmeterProps = JMeterUtils.getJMeterProperties();
         List<CLOption> clOptions = parser.getArguments();
         int size = clOptions.size();
 
         for (int i = 0; i < size; i++) {
             CLOption option = clOptions.get(i);
             String name = option.getArgument(0);
             String value = option.getArgument(1);
 
             switch (option.getDescriptor().getId()) {
             case PROPFILE2_OPT: // Bug 33920 - allow multiple props
                 File f = new File(name);
                 FileInputStream inStream = null;
                 try {
                     inStream = new FileInputStream(f);
                     jmeterProps.load(inStream);
                 } catch (FileNotFoundException e) {
                     log.warn("Can't find additional property file: " + name, e);
                 } catch (IOException e) {
                     log.warn("Error loading additional property file: " + name, e);
                 } finally {
                     IOUtils.closeQuietly(inStream);
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
             case LOGLEVEL:
                 if (value.length() > 0) { // Set category
                     log.info("LogLevel: " + name + "=" + value);
                     LoggingManager.setPriority(value, name);
                 } else { // Set root level
                     log.warn("LogLevel: " + name);
                     LoggingManager.setPriority(name);
                 }
                 break;
             default:
                 // ignored
                 break;
             }
         }
 
     }
 }
\ No newline at end of file
diff --git a/src/reports/org/apache/jmeter/control/gui/ReportGui.java b/src/reports/org/apache/jmeter/control/gui/ReportGui.java
index 08c8bef4e..5c67e3fb6 100644
--- a/src/reports/org/apache/jmeter/control/gui/ReportGui.java
+++ b/src/reports/org/apache/jmeter/control/gui/ReportGui.java
@@ -1,186 +1,188 @@
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
 
 package org.apache.jmeter.control.gui;
 
 import java.awt.Color;
 import java.awt.BorderLayout;
 import java.awt.Container;
 import java.util.Collection;
 
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.JTextField;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.gui.util.DirectoryPanel;
 import org.apache.jmeter.gui.util.ReportMenuFactory;
 import org.apache.jmeter.report.gui.AbstractReportGui;
 import org.apache.jmeter.report.gui.ReportPageGui;
 import org.apache.jmeter.report.writers.gui.HTMLReportWriterGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.ReportPlan;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * JMeter GUI component representing the test plan which will be executed when
  * the test is run.
  *
  * @version $Revision$
  */
 public class ReportGui extends AbstractReportGui {
 
     private static final long serialVersionUID = 240L;
 
     /** A panel to contain comments on the test plan. */
     private JTextField commentPanel;
 
     private final DirectoryPanel baseDir =
         new DirectoryPanel(JMeterUtils.getResString("report_base_directory"), 
                 Color.white);
 
     /** A panel allowing the user to define variables. */
     private ArgumentsPanel argsPanel;
 
     /**
      * Create a new TestPlanGui.
      */
     public ReportGui() {
         init();
     }
 
     /**
      * Need to update this to make the context popupmenu correct
      * @return a JPopupMenu appropriate for the component.
      */
     @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         JMenu addMenu = new JMenu(JMeterUtils.getResString("Add"));
         addMenu.add(ReportMenuFactory.makeMenuItem(new ReportPageGui().getStaticLabel(),
                 ReportPageGui.class.getName(),
                 "Add"));
         addMenu.add(ReportMenuFactory.makeMenuItem(new HTMLReportWriterGui().getStaticLabel(),
                 HTMLReportWriterGui.class.getName(),
                 "Add"));
         addMenu.add(ReportMenuFactory.makeMenu(ReportMenuFactory.CONFIG_ELEMENTS, "Add"));
         pop.add(addMenu);
         ReportMenuFactory.addFileMenu(pop);
         ReportMenuFactory.addEditMenu(pop,true);
         return pop;
     }
 
     /* Implements JMeterGUIComponent.createTestElement() */
+    @Override
     public TestElement createTestElement() {
         ReportPlan tp = new ReportPlan();
         modifyTestElement(tp);
         return tp;
     }
 
     /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
+    @Override
     public void modifyTestElement(TestElement plan) {
         super.configureTestElement(plan);
         if (plan instanceof ReportPlan) {
             ReportPlan rp = (ReportPlan) plan;
             rp.setUserDefinedVariables((Arguments) argsPanel.createTestElement());
             rp.setProperty(ReportPlan.REPORT_COMMENTS, commentPanel.getText());
             rp.setBasedir(baseDir.getFilename());
         }
     }
 
     @Override
     public String getLabelResource() {
         return "report_plan";
     }
 
     /**
      * This is the list of menu categories this gui component will be available
      * under. This implementation returns null, since the TestPlan appears at
      * the top level of the tree and cannot be added elsewhere.
      *
      * @return a Collection of Strings, where each element is one of the
      *         constants defined in MenuFactory
      */
     @Override
     public Collection<String> getMenuCategories() {
         return null;
     }
 
     /**
      * A newly created component can be initialized with the contents of a Test
      * Element object by calling this method. The component is responsible for
      * querying the Test Element object for the relevant information to display
      * in its GUI.
      *
      * @param el
      *            the TestElement to configure
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
 
         if (el.getProperty(ReportPlan.USER_DEFINED_VARIABLES) != null) {
             argsPanel.configure((Arguments) el.getProperty(ReportPlan.USER_DEFINED_VARIABLES).getObjectValue());
         }
         commentPanel.setText(el.getPropertyAsString(ReportPlan.REPORT_COMMENTS));
         baseDir.setFilename(el.getPropertyAsString(ReportPlan.BASEDIR));
     }
 
     /**
      * Create a panel allowing the user to define variables for the test.
      *
      * @return a panel for user-defined variables
      */
     private JPanel createVariablePanel() {
         argsPanel =
             new ArgumentsPanel(JMeterUtils.getResString("user_defined_variables"),
                     Color.white);
         return argsPanel;
     }
 
     private Container createCommentPanel() {
         JPanel panel = new JPanel();
         panel.setBackground(Color.white);
         panel.setLayout(new BorderLayout(10, 10));
         Container title = makeTitlePanel();
         commentPanel = new JTextField();
         commentPanel.setBackground(Color.white);
         JLabel label = new JLabel(JMeterUtils.getResString("testplan_comments"));
         label.setBackground(Color.white);
         label.setLabelFor(commentPanel);
         title.add(label);
         title.add(commentPanel);
         panel.add(title,BorderLayout.NORTH);
         panel.add(baseDir,BorderLayout.CENTER);
         return panel;
     }
 
     /**
      * Initialize the components and layout of this component.
      */
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(10, 10));
         setBorder(makeBorder());
         setBackground(Color.white);
         add(createCommentPanel(), BorderLayout.NORTH);
         add(createVariablePanel(), BorderLayout.CENTER);
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java b/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java
index 0a237101d..33409ec4d 100644
--- a/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java
+++ b/src/reports/org/apache/jmeter/gui/ReportGuiPackage.java
@@ -1,617 +1,618 @@
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
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.swing.JPopupMenu;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.report.engine.ValueReplacer;
 import org.apache.jmeter.report.gui.tree.ReportTreeListener;
 import org.apache.jmeter.report.gui.tree.ReportTreeModel;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testbeans.gui.TestBeanGUI;
 import org.apache.jmeter.testelement.ReportPlan;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * ReportGuiPackage is based on GuiPackage, but with changes for
  * the reporting tool. Because of how the gui components work, it
  * was safer to just make a new class, rather than braking existing
  * JMeter gui code.
  *
  */
 public final class ReportGuiPackage implements LocaleChangeListener {
     /** Logging. */
     private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private static final Object LOCK = new Object();
 
     /** Singleton instance. */
     private static volatile ReportGuiPackage guiPack;
 
     /**
      * Flag indicating whether or not parts of the tree have changed since they
      * were last saved.
      */
     private boolean dirty = false;
 
     /**
      * Map from TestElement to JMeterGUIComponent, mapping the nodes in the tree
      * to their corresponding GUI components.
      */
     private Map<TestElement, JMeterGUIComponent> nodesToGui = new HashMap<TestElement, JMeterGUIComponent>();
 
     /**
      * Map from Class to JMeterGUIComponent, mapping the Class of a GUI
      * component to an instance of that component.
      */
     private Map<Class<?>, JMeterGUIComponent> guis = new HashMap<Class<?>, JMeterGUIComponent>();
 
     /**
      * Map from Class to TestBeanGUI, mapping the Class of a TestBean to an
      * instance of TestBeanGUI to be used to edit such components.
      */
     private Map<Class<?>, JMeterGUIComponent> testBeanGUIs = new HashMap<Class<?>, JMeterGUIComponent>();
 
     /** The currently selected node in the tree. */
     private ReportTreeNode currentNode = null;
 
     private boolean currentNodeUpdated = false;
 
     /** The model for JMeter's test tree. */
     private ReportTreeModel treeModel;
 
     /** The listener for JMeter's test tree. */
     private ReportTreeListener treeListener;
 
     /** The main JMeter frame. */
     private ReportMainFrame mainFrame;
 
     /**
      * Private constructor to permit instantiation only from within this class.
      * Use {@link #getInstance()} to retrieve a singleton instance.
      */
     private ReportGuiPackage() {
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     /**
      * Retrieve the singleton GuiPackage instance.
      *
      * @return the GuiPackage instance
      */
     public static ReportGuiPackage getInstance() {
         if (guiPack == null){
             log.error("ReportGuiPackage is null");
         }
         return guiPack;
     }
 
     /**
      * When GuiPackage is requested for the first time, it should be given
      * handles to JMeter's Tree Listener and TreeModel.
      *
      * @param listener
      *            the TreeListener for JMeter's test tree
      * @param treeModel
      *            the model for JMeter's test tree
      *
      * @return GuiPackage
      */
     public static ReportGuiPackage getInstance(ReportTreeListener listener, ReportTreeModel treeModel) {
         if (guiPack == null) {
         	synchronized (LOCK) {
         		if(guiPack== null) {
 	                guiPack = new ReportGuiPackage();
 	                guiPack.setTreeListener(listener);
 	                guiPack.setTreeModel(treeModel);				
         		}
 			}
         }
         return guiPack;
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
             if (testClassName.equals("")) {
                 testClass = node.getClass();
             } else {
                 testClass = Class.forName(testClassName);
             }
             Class<?> guiClass = null;
             if (!guiClassName.equals("")) {
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
      *            the fully qualifed class name of the GUI component which will
      *            be created if it doesn't already exist
      * @param testClass
      *            the fully qualifed class name of the test elements which have
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
     public ReportTreeNode getNodeOf(TestElement userObject) {
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
             throw new RuntimeException(e.toString(), e); // Probably a missing
                                                         // jar
         } catch (ClassNotFoundException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error:
                                                         // bail out.
         } catch (InstantiationException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error:
                                                         // bail out.
         } catch (IllegalAccessException e) {
             log.error("Problem retrieving gui for " + objClass, e);
             throw new RuntimeException(e.toString(), e); // Programming error:
                                                         // bail out.
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
      * @throws ClassNotFoundException
      *             if the specified GUI class cannot be found
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
      *
      */
     public void updateCurrentGui() {
         updateCurrentNode();
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
                 comp.modifyTestElement(el);
             }
             if (currentNode != treeListener.getCurrentNode()) {
                 currentNodeUpdated = true;
             }
             currentNode = treeListener.getCurrentNode();
         } catch (Exception e) {
             log.error("Problem retrieving gui", e);
         }
     }
 
     public ReportTreeNode getCurrentNode() {
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
         return treeModel.addSubTree(subTree, treeListener.getCurrentNode());
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
     public ReportTreeModel getTreeModel() {
         return treeModel;
     }
 
     /**
      * Set the model for JMeter's test tree.
      *
      * @param newTreeModel
      *            the new JMeter tree model
      */
     public void setTreeModel(ReportTreeModel newTreeModel) {
         treeModel = newTreeModel;
     }
 
     /**
      * Get a ValueReplacer for the test tree.
      *
      * @return a ValueReplacer configured for the test tree
      */
     public ValueReplacer getReplacer() {
         return new ValueReplacer((ReportPlan) ((ReportTreeNode) getTreeModel().getReportPlan().getArray()[0])
                 .getTestElement());
     }
 
     /**
      * Set the main JMeter frame.
      *
      * @param newMainFrame
      *            the new JMeter main frame
      */
     public void setMainFrame(ReportMainFrame newMainFrame) {
         this.mainFrame = newMainFrame;
     }
 
     /**
      * Get the main JMeter frame.
      *
      * @return the main JMeter frame
      */
     public ReportMainFrame getMainFrame() {
         return this.mainFrame;
     }
 
     /**
      * Set the listener for JMeter's test tree.
      *
      * @param newTreeListener
      *            the new JMeter test tree listener
      */
     public void setTreeListener(ReportTreeListener newTreeListener) {
         treeListener = newTreeListener;
     }
 
     /**
      * Get the listener for JMeter's test tree.
      *
      * @return the JMeter test tree listener
      */
     public ReportTreeListener getTreeListener() {
         return treeListener;
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
             popup.requestFocus();
         }
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.util.LocaleChangeListener#localeChanged(org.apache.jmeter.util.LocaleChangeEvent)
      */
+    @Override
     public void localeChanged(LocaleChangeEvent event) {
         // FIrst make sure we save the content of the current GUI (since we
         // will flush it away):
         updateCurrentNode();
 
         // Forget about all GUIs we've created so far: we'll need to re-created
         // them all!
         guis = new HashMap<Class<?>, JMeterGUIComponent>();
         nodesToGui = new HashMap<TestElement, JMeterGUIComponent>();
         testBeanGUIs = new HashMap<Class<?>, JMeterGUIComponent>();
 
         // BeanInfo objects also contain locale-sensitive data -- flush them
         // away:
         Introspector.flushCaches();
 
         // Now put the current GUI in place. [This code was copied from the
         // EditCommand action -- we can't just trigger the action because that
         // would populate the current node with the contents of the new GUI --
         // which is empty.]
         ReportMainFrame mf = getMainFrame(); // Fetch once
         if (mf == null) // Probably caused by unit testing on headless system
         {
             log.warn("Mainframe is null");
         } else {
             mf.setMainPanel((javax.swing.JComponent) getCurrentGui());
             mf.setEditMenu(getTreeListener().getCurrentNode().createPopupMenu());
         }
     }
 
     private String reportPlanFile;
 
     /**
      * Sets the filepath of the current test plan. It's shown in the main frame
      * title and used on saving.
      *
      * @param f
      */
     public void setReportPlanFile(String f) {
         reportPlanFile = f;
         ReportGuiPackage.getInstance().getMainFrame().setExtendedFrameTitle(reportPlanFile);
         try {
             FileServer.getFileServer().setBasedir(reportPlanFile);
         } catch (IllegalStateException e1) {
             log.error("Failure setting file server's base dir", e1);
         }
     }
 
     public String getReportPlanFile() {
         return reportPlanFile;
     }
 }
\ No newline at end of file
diff --git a/src/reports/org/apache/jmeter/gui/ReportMainFrame.java b/src/reports/org/apache/jmeter/gui/ReportMainFrame.java
index c6154c94e..ee5baf213 100644
--- a/src/reports/org/apache/jmeter/gui/ReportMainFrame.java
+++ b/src/reports/org/apache/jmeter/gui/ReportMainFrame.java
@@ -1,400 +1,401 @@
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
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.Font;
 import java.awt.Insets;
 import java.awt.event.ActionEvent;
 import java.awt.event.WindowAdapter;
 import java.awt.event.WindowEvent;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.ImageIcon;
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
 import javax.swing.MenuElement;
 import javax.swing.SwingUtilities;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.TreeCellRenderer;
 import javax.swing.tree.TreeModel;
 
 import org.apache.jmeter.gui.util.ReportMenuBar;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.report.gui.tree.ReportCellRenderer;
 import org.apache.jmeter.report.gui.tree.ReportTreeListener;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * ReportMainFrame is based on MainFrame. it uses the same basic structure,
  * but with changes for the report gui.
  *
  */
 public class ReportMainFrame extends JFrame {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // The default title for the Menu bar
     private static final String DEFAULT_TITLE =
         "Apache JMeter ("+JMeterUtils.getJMeterVersion()+")"; // $NON-NLS-1$ $NON-NLS-2$
 
     /** The menu bar. */
     protected ReportMenuBar menuBar;
 
     /** The main panel where components display their GUIs. */
     protected JScrollPane mainPanel;
 
     /** The panel where the test tree is shown. */
     protected JScrollPane treePanel;
 
     /** The test tree. */
     protected JTree tree;
 
     /** An image which is displayed when a test is running. */
     //private ImageIcon runningIcon = JMeterUtils.getImage("thread.enabled.gif");
 
     /** An image which is displayed when a test is not currently running. */
     private final ImageIcon stoppedIcon = JMeterUtils.getImage("thread.disabled.gif");// $NON-NLS-1$
 
     /** The x coordinate of the last location where a component was dragged. */
     private int previousDragXLocation = 0;
 
     /** The y coordinate of the last location where a component was dragged. */
     private int previousDragYLocation = 0;
 
     /** The button used to display the running/stopped image. */
     private JButton runningIndicator;
 
     /** The set of currently running hosts. */
     //private Set hosts = new HashSet();
 
     /** A message dialog shown while JMeter threads are stopping. */
     private JDialog stoppingMessage;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public ReportMainFrame(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
     /**
      * Create a new JMeter frame.
      *
      * @param treeModel
      *            the model for the test tree
      * @param treeListener
      *            the listener for the test tree
      */
     public ReportMainFrame(TreeModel treeModel,
             ReportTreeListener treeListener) {
         runningIndicator = new JButton(stoppedIcon);
         runningIndicator.setMargin(new Insets(0, 0, 0, 0));
         runningIndicator.setBorder(BorderFactory.createEmptyBorder());
 
         this.tree = this.makeTree(treeModel,treeListener);
 
         ReportGuiPackage.getInstance().setMainFrame(this);
         init();
 
         setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
      * Specify whether or not the Edit|Remove menu item should be enabled.
      *
      * @param enabled
      *            true if the menu item should be enabled, false otherwise
      */
     public void setEditRemoveEnabled(boolean enabled) {
         menuBar.setEditRemoveEnabled(enabled);
     }
 
     /**
      * Close the currently selected menu.
      */
     public void closeMenu() {
         if (menuBar.isSelected()) {
             MenuElement[] menuElement = menuBar.getSubElements();
             if (menuElement != null) {
                 for (int i = 0; i < menuElement.length; i++) {
                     JMenu menu = (JMenu) menuElement[i];
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
         stoppingMessage = new JDialog(this, JMeterUtils.getResString("stopping_test_title"), true);// $NON-NLS-1$
         JLabel stopLabel = new JLabel(JMeterUtils.getResString("stopping_test") + ": " + host);// $NON-NLS-1$
         stopLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         stoppingMessage.getContentPane().add(stopLabel);
         stoppingMessage.pack();
         ComponentUtil.centerComponentInComponent(this, stoppingMessage);
         SwingUtilities.invokeLater(new Runnable() {
+            @Override
             public void run() {
                 if (stoppingMessage != null) {
                     stoppingMessage.setVisible(true);
                 }
             }
         });
     }
 
     public void setMainPanel(JComponent comp) {
         mainPanel.setViewportView(comp);
     }
 
     public JTree getTree() {
         return this.tree;
     }
 
     /**
      * Create the GUI components and layout.
      */
     private void init() {// called from ctor, so must not be overridable
         menuBar = new ReportMenuBar();
         setJMenuBar(menuBar);
 
         JPanel all = new JPanel(new BorderLayout());
         all.add(createToolBar(), BorderLayout.NORTH);
 
         JSplitPane treeAndMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
 
         treePanel = createTreePanel();
         treeAndMain.setLeftComponent(treePanel);
 
         mainPanel = createMainPanel();
         treeAndMain.setRightComponent(mainPanel);
 
         treeAndMain.setResizeWeight(.2);
         treeAndMain.setContinuousLayout(true);
         all.add(treeAndMain, BorderLayout.CENTER);
 
         getContentPane().add(all);
 
         tree.setSelectionRow(1);
         addWindowListener(new WindowHappenings());
 
         setTitle(DEFAULT_TITLE);
         setIconImage(JMeterUtils.getImage("jmeter.jpg").getImage());// $NON-NLS-1$
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
     protected Component createToolBar() {
         Box toolPanel = new Box(BoxLayout.X_AXIS);
         toolPanel.add(Box.createRigidArea(new Dimension(10, 15)));
         toolPanel.add(Box.createGlue());
         toolPanel.add(runningIndicator);
         return toolPanel;
     }
 
     /**
      * Create the panel where the GUI representation of the test tree is
      * displayed. The tree should already be created before calling this method.
      *
      * @return a scroll pane containing the test tree GUI
      */
     protected JScrollPane createTreePanel() {
         JScrollPane treeP = new JScrollPane(tree);
         treeP.setMinimumSize(new Dimension(100, 0));
         return treeP;
     }
 
     /**
      * Create the main panel where components can display their GUIs.
      *
      * @return the main scroll pane
      */
     protected JScrollPane createMainPanel() {
         return new JScrollPane();
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
     private JTree makeTree(TreeModel treeModel, ReportTreeListener treeListener) {
         JTree treevar = new JTree(treeModel);
         treevar.setCellRenderer(getCellRenderer());
         treevar.setRootVisible(false);
         treevar.setShowsRootHandles(true);
 
         treeListener.setJTree(treevar);
         treevar.addTreeSelectionListener(treeListener);
         treevar.addMouseListener(treeListener);
         treevar.addMouseMotionListener(treeListener);
         treevar.addKeyListener(treeListener);
 
         return treevar;
     }
 
     /**
      * Create the tree cell renderer used to draw the nodes in the test tree.
      *
      * @return a renderer to draw the test tree nodes
      */
     protected TreeCellRenderer getCellRenderer() {
         DefaultTreeCellRenderer rend = new ReportCellRenderer();
         rend.setFont(new Font("Dialog", Font.PLAIN, 11));
         return rend;
     }
 
     public void drawDraggedComponent(Component dragIcon, int x, int y) {
         Dimension size = dragIcon.getPreferredSize();
         treePanel.paintImmediately(previousDragXLocation, previousDragYLocation, size.width, size.height);
         this.getLayeredPane().setLayer(dragIcon, 400);
         SwingUtilities.paintComponent(treePanel.getGraphics(), dragIcon, treePanel, x, y, size.width, size.height);
         previousDragXLocation = x;
         previousDragYLocation = y;
     }
 
     /**
      * A window adapter used to detect when the main JMeter frame is being
      * closed.
      */
     protected static class WindowHappenings extends WindowAdapter {
         /**
          * Called when the main JMeter frame is being closed. Sends a
          * notification so that JMeter can react appropriately.
          *
          * @param event
          *            the WindowEvent to handle
          */
         @Override
         public void windowClosing(WindowEvent event) {
             ReportActionRouter.getInstance().actionPerformed(new ActionEvent(this, event.getID(), "exit"));
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java b/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java
index 6545f112d..12ab5f2cd 100644
--- a/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java
+++ b/src/reports/org/apache/jmeter/gui/util/DirectoryPanel.java
@@ -1,141 +1,142 @@
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
 
 import java.awt.Color;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.util.JMeterUtils;
 
 public class DirectoryPanel extends HorizontalPanel implements ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final String ACTION_BROWSE = "browse"; // $NON-NSL-1$
 
     private final JTextField filename = new JTextField(20);
 
     private final JButton browse = new JButton(JMeterUtils.getResString("browse"));
 
     private final List<ChangeListener> listeners = new LinkedList<ChangeListener>();
 
     private final String title;
     
     private final Color background;
 
     /**
      * Constructor for the FilePanel object.
      */
     public DirectoryPanel() {
         this("", null);
     }
 
     public DirectoryPanel(String title) {
         this(title, null);
     }
 
     public DirectoryPanel(String title, Color bk) {
         this.title = title;
         this.background = bk;
         init();
     }
     /**
      * Constructor for the FilePanel object.
      */
     public DirectoryPanel(ChangeListener l, String title) {
         this(title);
         listeners.add(l);
     }
 
     public void addChangeListener(ChangeListener l) {
         listeners.add(l);
     }
 
     private void init() {
         setBackground(this.background);
         setBorder(BorderFactory.createTitledBorder(title));
         add(Box.createHorizontalStrut(5));
         add(filename);
         add(Box.createHorizontalStrut(5));
         filename.addActionListener(this);
         add(browse);
         browse.setActionCommand(ACTION_BROWSE);
         browse.addActionListener(this);
     }
 
     /**
      * If the gui needs to enable/disable the FilePanel, call the method.
      *
      * @param enable
      */
     public void enableFile(boolean enable) {
         browse.setEnabled(enable);
         filename.setEnabled(enable);
     }
 
     /**
      * Gets the filename attribute of the FilePanel object.
      *
      * @return the filename value
      */
     public String getFilename() {
         return filename.getText();
     }
 
     /**
      * Sets the filename attribute of the FilePanel object.
      *
      * @param f
      *            the new filename value
      */
     public void setFilename(String f) {
         filename.setText(f);
     }
 
     private void fireFileChanged() {
         Iterator<ChangeListener> iter = listeners.iterator();
         while (iter.hasNext()) {
             iter.next().stateChanged(new ChangeEvent(this));
         }
     }
 
+    @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals(ACTION_BROWSE)) {
             JFileChooser chooser = DirectoryDialoger.promptToOpenFile();
             if (chooser.getSelectedFile() != null) {
                 filename.setText(chooser.getSelectedFile().getPath());
                 fireFileChanged();
             }
         } else {
             fireFileChanged();
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java b/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java
index 5777be9ce..567f4a2a7 100644
--- a/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java
+++ b/src/reports/org/apache/jmeter/gui/util/ReportFilePanel.java
@@ -1,143 +1,144 @@
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
 
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.util.JMeterUtils;
 
 public class ReportFilePanel extends HorizontalPanel implements ActionListener {
     private static final long serialVersionUID = 240L;
 
     private final JTextField filename = new JTextField(20);
 
     private final JLabel label = new JLabel(JMeterUtils.getResString("file_visualizer_filename"));
 
     private final JButton browse = new JButton(JMeterUtils.getResString("browse"));
 
     private final List<ChangeListener> listeners = new LinkedList<ChangeListener>();
 
     private final String title;
 
     private final String filetype;
 
     /**
      * Constructor for the FilePanel object.
      */
     public ReportFilePanel() {
         this("");
     }
 
     public ReportFilePanel(String title) {
         this(title, null);
     }
 
     public ReportFilePanel(String title, String filetype) {
         this.title = title;
         this.filetype = filetype;
         init();
     }
 
     /**
      * Constructor for the FilePanel object.
      */
     public ReportFilePanel(ChangeListener l, String title) {
         this(title);
         listeners.add(l);
     }
 
     public void addChangeListener(ChangeListener l) {
         listeners.add(l);
     }
 
     private void init() {
         setBorder(BorderFactory.createTitledBorder(title));
         add(label);
         add(Box.createHorizontalStrut(5));
         add(filename);
         add(Box.createHorizontalStrut(5));
         filename.addActionListener(this);
         add(browse);
         browse.setActionCommand("browse");
         browse.addActionListener(this);
 
     }
 
     /**
      * If the gui needs to enable/disable the FilePanel, call the method.
      *
      * @param enable
      */
     public void enableFile(boolean enable) {
         browse.setEnabled(enable);
         filename.setEnabled(enable);
     }
 
     /**
      * Gets the filename attribute of the FilePanel object.
      *
      * @return the filename value
      */
     public String getFilename() {
         return filename.getText();
     }
 
     /**
      * Sets the filename attribute of the FilePanel object.
      *
      * @param f
      *            the new filename value
      */
     public void setFilename(String f) {
         filename.setText(f);
     }
 
     private void fireFileChanged() {
         Iterator<ChangeListener> iter = listeners.iterator();
         while (iter.hasNext()) {
             iter.next().stateChanged(new ChangeEvent(this));
         }
     }
 
     /** {@inheritDoc} */
+    @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals("browse")) {
             JFileChooser chooser = ReportFileDialoger.promptToOpenFile(new String[] { filetype });
             if (chooser != null && chooser.getSelectedFile() != null) {
                 filename.setText(chooser.getSelectedFile().getPath());
                 fireFileChanged();
             }
         } else {
             fireFileChanged();
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/gui/util/ReportMenuBar.java b/src/reports/org/apache/jmeter/gui/util/ReportMenuBar.java
index 80782d60d..0901b2f81 100644
--- a/src/reports/org/apache/jmeter/gui/util/ReportMenuBar.java
+++ b/src/reports/org/apache/jmeter/gui/util/ReportMenuBar.java
@@ -1,488 +1,489 @@
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
 
 import java.awt.Component;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 
 import javax.swing.JMenu;
 import javax.swing.JMenuBar;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.MenuElement;
 import javax.swing.UIManager;
 
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * This is a version of the MenuBar for the reporting tool. I started
  * with the existing jmeter menubar.
  */
 public class ReportMenuBar extends JMenuBar implements LocaleChangeListener {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private JMenu fileMenu;
 
     private JMenuItem file_save_as;
 
     private JMenuItem file_load;
 
     private JMenuItem file_merge;
 
     private JMenuItem file_exit;
 
     private JMenuItem file_close;
 
     private JMenu editMenu;
 
     private JMenu edit_add;
 
     private JMenu runMenu;
 
     private JMenuItem run_start;
 
     private JMenu remote_start;
 
     private JMenuItem remote_start_all;
 
     private final Collection<JMenuItem> remote_engine_start;
 
     private JMenuItem run_stop;
 
     private JMenuItem run_shut; // all the others could be private too?
 
     private JMenu remote_stop;
 
     private JMenuItem remote_stop_all;
 
     private final Collection<JMenuItem> remote_engine_stop;
 
     private JMenuItem run_clear;
 
     private JMenuItem run_clearAll;
 
     // JMenu reportMenu;
     // JMenuItem analyze;
     private JMenu optionsMenu;
 
     private JMenu lafMenu;
 
     private JMenuItem sslManager;
 
     private JMenu helpMenu;
 
     private JMenuItem help_about;
 
     private String[] remoteHosts;
 
     private JMenu remote_exit;
 
     private JMenuItem remote_exit_all;
 
     private final Collection<JMenuItem> remote_engine_exit;
 
     public ReportMenuBar() {
         remote_engine_start = new LinkedList<JMenuItem>();
         remote_engine_stop = new LinkedList<JMenuItem>();
         remote_engine_exit = new LinkedList<JMenuItem>();
         remoteHosts = JOrphanUtils.split(JMeterUtils.getPropDefault("remote_hosts", ""), ",");
         if (remoteHosts.length == 1 && remoteHosts[0].equals("")) {
             remoteHosts = new String[0];
         }
         this.getRemoteItems();
         createMenuBar();
     }
 
     public void setFileSaveEnabled(boolean enabled) {
         file_save_as.setEnabled(enabled);
     }
 
     public void setFileLoadEnabled(boolean enabled) {
         if (file_load != null) {
             file_load.setEnabled(enabled);
         }
         if (file_merge != null) {
             file_merge.setEnabled(enabled);
         }
     }
 
     public void setEditEnabled(boolean enabled) {
         if (editMenu != null) {
             editMenu.setEnabled(enabled);
         }
     }
 
     public void setEditAddMenu(JMenu menu) {
         // If the Add menu already exists, remove it.
         if (edit_add != null) {
             editMenu.remove(edit_add);
         }
         // Insert the Add menu as the first menu item in the Edit menu.
         edit_add = menu;
         editMenu.insert(edit_add, 0);
     }
 
     public void setEditMenu(JPopupMenu menu) {
         if (menu != null) {
             editMenu.removeAll();
             Component[] comps = menu.getComponents();
             for (int i = 0; i < comps.length; i++) {
                 editMenu.add(comps[i]);
             }
             editMenu.setEnabled(true);
         } else {
             // editMenu.setEnabled(false);
         }
     }
 
     public void setEditAddEnabled(boolean enabled) {
         // There was a NPE being thrown without the null check here.. JKB
         if (edit_add != null) {
             edit_add.setEnabled(enabled);
         }
         // If we are enabling the Edit-->Add menu item, then we also need to
         // enable the Edit menu. The Edit menu may already be enabled, but
         // there's no harm it trying to enable it again.
         setEditEnabled(enabled);
     }
 
     public void setEditRemoveEnabled(boolean enabled) {
         // If we are enabling the Edit-->Remove menu item, then we also need to
         // enable the Edit menu. The Edit menu may already be enabled, but
         // there's no harm it trying to enable it again.
         if (enabled) {
             setEditEnabled(true);
         } else {
             // If we are disabling the Edit-->Remove menu item and the
             // Edit-->Add menu item is disabled, then we also need to disable
             // the Edit menu.
             // The Java Look and Feel Guidelines say to disable a menu if all
             // menu items are disabled.
             if (!edit_add.isEnabled()) {
                 editMenu.setEnabled(false);
             }
         }
     }
 
     /**
      * Creates the MenuBar for this application. I believe in my heart that this
      * should be defined in a file somewhere, but that is for later.
      */
     public void createMenuBar() {
         makeFileMenu();
         makeEditMenu();
         makeRunMenu();
         makeOptionsMenu();
         makeHelpMenu();
         this.add(fileMenu);
         this.add(editMenu);
         this.add(runMenu);
         this.add(optionsMenu);
         this.add(helpMenu);
     }
 
     private void makeHelpMenu() {
         // HELP MENU
         helpMenu = new JMenu(JMeterUtils.getResString("help"));
         helpMenu.setMnemonic('H');
         JMenuItem contextHelp = new JMenuItem(JMeterUtils.getResString("help"), 'H');
         contextHelp.setActionCommand("help");
         contextHelp.setAccelerator(KeyStrokes.HELP);
         contextHelp.addActionListener(ReportActionRouter.getInstance());
         help_about = new JMenuItem(JMeterUtils.getResString("about"), 'A');
         help_about.setActionCommand("about");
         help_about.addActionListener(ReportActionRouter.getInstance());
         helpMenu.add(contextHelp);
         helpMenu.add(help_about);
     }
 
     private void makeOptionsMenu() {
         // OPTIONS MENU
         optionsMenu = new JMenu(JMeterUtils.getResString("option"));
         JMenuItem functionHelper = new JMenuItem(JMeterUtils.getResString("function_dialog_menu_item"), 'F');
         functionHelper.addActionListener(ReportActionRouter.getInstance());
         functionHelper.setActionCommand("functions");
         functionHelper.setAccelerator(KeyStrokes.FUNCTIONS);
         lafMenu = new JMenu(JMeterUtils.getResString("appearance"));
         UIManager.LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
         for (int i = 0; i < lafs.length; ++i) {
             JMenuItem laf = new JMenuItem(lafs[i].getName());
             laf.addActionListener(ReportActionRouter.getInstance());
             laf.setActionCommand("laf:" + lafs[i].getClassName());
             lafMenu.setMnemonic('L');
             lafMenu.add(laf);
         }
         optionsMenu.setMnemonic('O');
         optionsMenu.add(functionHelper);
         optionsMenu.add(lafMenu);
         if (SSLManager.isSSLSupported()) {
             sslManager = new JMenuItem(JMeterUtils.getResString("sslManager"));
             sslManager.addActionListener(ReportActionRouter.getInstance());
             sslManager.setActionCommand("sslManager");
             sslManager.setMnemonic('S');
             sslManager.setAccelerator(KeyStrokes.SSL_MANAGER);
             optionsMenu.add(sslManager);
         }
         optionsMenu.add(makeLanguageMenu());
     }
 
     // TODO fetch list of languages from a file?
     // N.B. Changes to language list need to be reflected in
     // resources/PackageTest.java
     private JMenu makeLanguageMenu() {
         return JMeterMenuBar.makeLanguageMenu();
     }
 
     /*
      * Strings used to set up and process actions in this menu The strings need
      * to agree with the those in the Action routines
      */
     public static final String ACTION_SHUTDOWN = "shutdown";
 
     public static final String ACTION_STOP = "stop";
 
     public static final String ACTION_START = "start";
 
     private void makeRunMenu() {
         // RUN MENU
         runMenu = new JMenu(JMeterUtils.getResString("run"));
         runMenu.setMnemonic('R');
         run_start = new JMenuItem(JMeterUtils.getResString("start"), 'S');
         run_start.setAccelerator(KeyStrokes.ACTION_START);
         run_start.addActionListener(ReportActionRouter.getInstance());
         run_start.setActionCommand(ACTION_START);
         run_stop = new JMenuItem(JMeterUtils.getResString("stop"), 'T');
         run_stop.setAccelerator(KeyStrokes.ACTION_STOP);
         run_stop.setEnabled(false);
         run_stop.addActionListener(ReportActionRouter.getInstance());
         run_stop.setActionCommand(ACTION_STOP);
 
         run_shut = new JMenuItem(JMeterUtils.getResString("shutdown"), 'Y');
         run_shut.setAccelerator(KeyStrokes.ACTION_SHUTDOWN);
         run_shut.setEnabled(false);
         run_shut.addActionListener(ReportActionRouter.getInstance());
         run_shut.setActionCommand(ACTION_SHUTDOWN);
 
         run_clear = new JMenuItem(JMeterUtils.getResString("clear"), 'C');
         run_clear.addActionListener(ReportActionRouter.getInstance());
         run_clear.setActionCommand(ActionNames.CLEAR);
         run_clearAll = new JMenuItem(JMeterUtils.getResString("clear_all"), 'a');
         run_clearAll.addActionListener(ReportActionRouter.getInstance());
         run_clearAll.setActionCommand(ActionNames.CLEAR_ALL);
         run_clearAll.setAccelerator(KeyStrokes.CLEAR_ALL);
         runMenu.add(run_start);
         if (remote_start != null) {
             runMenu.add(remote_start);
         }
         remote_start_all = new JMenuItem(JMeterUtils.getResString("remote_start_all"), 'Z');
         remote_start_all.setName("remote_start_all");
         remote_start_all.setAccelerator(KeyStrokes.REMOTE_START_ALL);
         remote_start_all.addActionListener(ReportActionRouter.getInstance());
         remote_start_all.setActionCommand("remote_start_all");
         runMenu.add(remote_start_all);
         runMenu.add(run_stop);
         runMenu.add(run_shut);
         if (remote_stop != null) {
             runMenu.add(remote_stop);
         }
         remote_stop_all = new JMenuItem(JMeterUtils.getResString("remote_stop_all"), 'X');
         remote_stop_all.setAccelerator(KeyStrokes.REMOTE_STOP_ALL);
         remote_stop_all.addActionListener(ReportActionRouter.getInstance());
         remote_stop_all.setActionCommand("remote_stop_all");
         runMenu.add(remote_stop_all);
 
         if (remote_exit != null) {
             runMenu.add(remote_exit);
         }
         remote_exit_all = new JMenuItem(JMeterUtils.getResString("remote_exit_all"));
         remote_exit_all.addActionListener(ReportActionRouter.getInstance());
         remote_exit_all.setActionCommand("remote_exit_all");
         runMenu.add(remote_exit_all);
 
         runMenu.addSeparator();
         runMenu.add(run_clear);
         runMenu.add(run_clearAll);
     }
 
     private void makeEditMenu() {
         // EDIT MENU
         editMenu = new JMenu(JMeterUtils.getResString("edit"));
         // From the Java Look and Feel Guidelines: If all items in a menu
         // are disabled, then disable the menu. Makes sense.
         editMenu.setEnabled(false);
     }
 
     private void makeFileMenu() {
         // FILE MENU
         fileMenu = new JMenu(JMeterUtils.getResString("file"));
         fileMenu.setMnemonic('F');
         JMenuItem file_save = new JMenuItem(JMeterUtils.getResString("save"), 'S');
         file_save.setAccelerator(KeyStrokes.SAVE);
         file_save.setActionCommand("save");
         file_save.addActionListener(ReportActionRouter.getInstance());
         file_save.setEnabled(true);
 
         file_save_as = new JMenuItem(JMeterUtils.getResString("save_all_as"), 'A');
         file_save_as.setAccelerator(KeyStrokes.SAVE_ALL_AS);
         file_save_as.setActionCommand("save_all_as");
         file_save_as.addActionListener(ReportActionRouter.getInstance());
         file_save_as.setEnabled(true);
 
         file_load = new JMenuItem(JMeterUtils.getResString("menu_open"), 'O');
         file_load.setAccelerator(KeyStrokes.OPEN);
         file_load.addActionListener(ReportActionRouter.getInstance());
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         file_load.setEnabled(false);
         file_load.setActionCommand("open");
 
         file_close = new JMenuItem(JMeterUtils.getResString("menu_close"), 'C');
         file_close.setAccelerator(KeyStrokes.CLOSE);
         file_close.setActionCommand("close");
         file_close.addActionListener(ReportActionRouter.getInstance());
 
         file_exit = new JMenuItem(JMeterUtils.getResString("exit"), 'X');
         file_exit.setAccelerator(KeyStrokes.EXIT);
         file_exit.setActionCommand("exit");
         file_exit.addActionListener(ReportActionRouter.getInstance());
 
         file_merge = new JMenuItem(JMeterUtils.getResString("menu_merge"), 'M');
         // file_merge.setAccelerator(
         // KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
         file_merge.addActionListener(ReportActionRouter.getInstance());
         // Set default SAVE menu item to disabled since the default node that
         // is selected is ROOT, which does not allow items to be inserted.
         file_merge.setEnabled(false);
         file_merge.setActionCommand("merge");
 
         fileMenu.add(file_close);
         fileMenu.add(file_load);
         fileMenu.add(file_merge);
         fileMenu.add(file_save);
         fileMenu.add(file_save_as);
         fileMenu.addSeparator();
         fileMenu.add(file_exit);
     }
 
     public void setRunning(boolean running, String host) {
         log.info("setRunning(" + running + "," + host + ")");
 
         Iterator<JMenuItem> iter = remote_engine_start.iterator();
         Iterator<JMenuItem> iter2 = remote_engine_stop.iterator();
         Iterator<JMenuItem> iter3 = remote_engine_exit.iterator();
         while (iter.hasNext() && iter2.hasNext() && iter3.hasNext()) {
             JMenuItem start = iter.next();
             JMenuItem stop = iter2.next();
             JMenuItem exit = iter3.next();
             if (start.getText().equals(host)) {
                 log.info("Found start host: " + start.getText());
                 start.setEnabled(!running);
             }
             if (stop.getText().equals(host)) {
                 log.info("Found stop  host: " + stop.getText());
                 stop.setEnabled(running);
             }
             if (exit.getText().equals(host)) {
                 log.info("Found exit  host: " + exit.getText());
                 exit.setEnabled(true);
             }
         }
     }
 
     @Override
     public void setEnabled(boolean enable) {
         run_start.setEnabled(!enable);
         run_stop.setEnabled(enable);
         run_shut.setEnabled(enable);
     }
 
     private void getRemoteItems() {
         if (remoteHosts.length > 0) {
             remote_start = new JMenu(JMeterUtils.getResString("remote_start"));
             remote_stop = new JMenu(JMeterUtils.getResString("remote_stop"));
             remote_exit = new JMenu(JMeterUtils.getResString("remote_exit"));
 
             for (int i = 0; i < remoteHosts.length; i++) {
                 remoteHosts[i] = remoteHosts[i].trim();
                 JMenuItem item = new JMenuItem(remoteHosts[i]);
                 item.setActionCommand("remote_start");
                 item.setName(remoteHosts[i]);
                 item.addActionListener(ReportActionRouter.getInstance());
                 remote_engine_start.add(item);
                 remote_start.add(item);
                 item = new JMenuItem(remoteHosts[i]);
                 item.setActionCommand("remote_stop");
                 item.setName(remoteHosts[i]);
                 item.addActionListener(ReportActionRouter.getInstance());
                 item.setEnabled(false);
                 remote_engine_stop.add(item);
                 remote_stop.add(item);
                 item = new JMenuItem(remoteHosts[i]);
                 item.setActionCommand("remote_exit");
                 item.setName(remoteHosts[i]);
                 item.addActionListener(ReportActionRouter.getInstance());
                 item.setEnabled(false);
                 remote_engine_exit.add(item);
                 remote_exit.add(item);
             }
         }
     }
 
     /**
      * Processes a locale change notification. Changes the texts in all menus to
      * the new language.
      */
+    @Override
     public void localeChanged(LocaleChangeEvent event) {
         updateMenuElement(fileMenu);
         updateMenuElement(editMenu);
         updateMenuElement(runMenu);
         updateMenuElement(optionsMenu);
         updateMenuElement(helpMenu);
     }
 
     /**
      * Refreshes all texts in the menu and all submenus to a new locale.
      */
     private void updateMenuElement(MenuElement menu) {
         Component component = menu.getComponent();
 
         if (component.getName() != null) {
             ((JMenuItem) component).setText(JMeterUtils.getResString(component.getName()));
         }
 
         MenuElement[] subelements = menu.getSubElements();
 
         for (int i = 0; i < subelements.length; i++) {
             updateMenuElement(subelements[i]);
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/engine/StandardReportEngine.java b/src/reports/org/apache/jmeter/report/engine/StandardReportEngine.java
index f41041926..1426cd1e2 100644
--- a/src/reports/org/apache/jmeter/report/engine/StandardReportEngine.java
+++ b/src/reports/org/apache/jmeter/report/engine/StandardReportEngine.java
@@ -1,74 +1,80 @@
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
 package org.apache.jmeter.report.engine;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.engine.JMeterEngineException;
 import org.apache.jorphan.collections.HashTree;
 
 public class StandardReportEngine implements Runnable, Serializable,
         ReportEngine {
 
     private static final long serialVersionUID = 240L;
 
     /**
      *
      */
     public StandardReportEngine() {
         super();
     }
 
     /* (non-Javadoc)
      * @see java.lang.Runnable#run()
      */
+    @Override
     public void run() {
 
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.engine.ReportEngine#configure(org.apache.jorphan.collections.HashTree)
      */
+    @Override
     public void configure(HashTree testPlan) {
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.engine.ReportEngine#runReport()
      */
+    @Override
     public void runReport() throws JMeterEngineException {
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.engine.ReportEngine#stopReport()
      */
+    @Override
     public void stopReport() {
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.engine.ReportEngine#reset()
      */
+    @Override
     public void reset() {
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.engine.ReportEngine#exit()
      */
+    @Override
     public void exit() {
     }
 
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/AbstractReportGui.java b/src/reports/org/apache/jmeter/report/gui/AbstractReportGui.java
index 2ad41f040..317d769a2 100644
--- a/src/reports/org/apache/jmeter/report/gui/AbstractReportGui.java
+++ b/src/reports/org/apache/jmeter/report/gui/AbstractReportGui.java
@@ -1,114 +1,117 @@
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
 package org.apache.jmeter.report.gui;
 
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Container;
 import java.awt.Font;
 import java.util.Arrays;
 import java.util.Collection;
 
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JPopupMenu;
 
 import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
 import org.apache.jmeter.gui.NamePanel;
 import org.apache.jmeter.gui.util.ReportMenuFactory;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  *
  * This is the abstract base for report gui's
  */
 public abstract class AbstractReportGui extends AbstractJMeterGuiComponent
 {
     private static final long serialVersionUID = 240L;
 
     /**
      *
      */
     public AbstractReportGui() {
         this.namePanel = new NamePanel();
         this.namePanel.setBackground(Color.white);
         setName(getStaticLabel());
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.JMeterGUIComponent#getLabelResource()
      */
+    @Override
     public String getLabelResource() {
         return "report_page";
     }
 
     @Override
     public void configureTestElement(TestElement element) {
         super.configureTestElement(element);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createPopupMenu()
      */
+    @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         JMenu addMenu = new JMenu(JMeterUtils.getResString("Add"));
         addMenu.add(ReportMenuFactory.makeMenu(ReportMenuFactory.CONFIG_ELEMENTS, "Add"));
         addMenu.add(ReportMenuFactory.makeMenu(ReportMenuFactory.PRE_PROCESSORS, "Add"));
         addMenu.add(ReportMenuFactory.makeMenu(ReportMenuFactory.POST_PROCESSORS, "Add"));
         pop.add(addMenu);
         ReportMenuFactory.addFileMenu(pop);
         return pop;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories()
      */
+    @Override
     public Collection<String> getMenuCategories() {
         return Arrays.asList(new String[] { ReportMenuFactory.TABLES });
     }
 
     /**
      * This implementaion differs a bit from the normal jmeter gui. it uses
      * a white background instead of the default grey.
      * @return a panel containing the component title and name panel
      */
     @Override
     protected Container makeTitlePanel() {
         VerticalPanel titlePanel = new VerticalPanel();
         titlePanel.setBackground(Color.white);
         titlePanel.add(createTitleLabel());
         titlePanel.add(getNamePanel());
         return titlePanel;
     }
 
     /**
      * This implementaion differs a bit from the normal jmeter gui. it uses
      * a white background instead of the default grey.
      */
     @Override
     protected Component createTitleLabel() {
         JLabel titleLabel = new JLabel(getStaticLabel());
         Font curFont = titleLabel.getFont();
         titleLabel.setFont(curFont.deriveFont((float) curFont.getSize() + 4));
         titleLabel.setBackground(Color.white);
         return titleLabel;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/BarChartGui.java b/src/reports/org/apache/jmeter/report/gui/BarChartGui.java
index 374bf9ab4..bcdff9a32 100644
--- a/src/reports/org/apache/jmeter/report/gui/BarChartGui.java
+++ b/src/reports/org/apache/jmeter/report/gui/BarChartGui.java
@@ -1,153 +1,155 @@
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
 package org.apache.jmeter.report.gui;
 
 import javax.swing.border.EmptyBorder;
 import java.awt.BorderLayout;
 import java.awt.Color;
 
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 
 import org.apache.jmeter.gui.util.ReportMenuFactory;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.AbstractChart;
 import org.apache.jmeter.testelement.AbstractTable;
 import org.apache.jmeter.testelement.BarChart;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 public class BarChartGui extends AbstractReportGui {
 
     private static final long serialVersionUID = 240L;
 
     private JLabeledChoice xAxisLabel = new JLabeledChoice();
 
     private JLabeledTextField yAxisLabel =
         new JLabeledTextField(JMeterUtils.getResString("report_chart_y_axis_label"));
 
     private JLabeledTextField caption =
         new JLabeledTextField(JMeterUtils.getResString("report_chart_caption"),
                 Color.white);
     private JLabeledTextField url =
         new JLabeledTextField(JMeterUtils.getResString("report_bar_graph_url"),
                 Color.white);
 
     private JLabeledChoice yItems = new JLabeledChoice();
     private JLabeledChoice xItems = new JLabeledChoice();
 
     public BarChartGui() {
         super();
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "report_bar_chart";
     }
 
     @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         ReportMenuFactory.addFileMenu(pop);
         ReportMenuFactory.addEditMenu(pop,true);
         return pop;
     }
 
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(10, 10));
         setBorder(makeBorder());
         setBackground(Color.white);
 
         JPanel pane = new JPanel();
         pane.setLayout(new BorderLayout(10,10));
         pane.setBackground(Color.white);
         pane.add(this.getNamePanel(),BorderLayout.NORTH);
 
         VerticalPanel options = new VerticalPanel(Color.white);
         xAxisLabel.setBackground(Color.white);
         yAxisLabel.setBackground(Color.white);
 
         JLabel xLabel = new JLabel(JMeterUtils.getResString("report_chart_x_axis"));
         HorizontalPanel xpanel = new HorizontalPanel(Color.white);
         xLabel.setBorder(new EmptyBorder(5,2,5,2));
         xItems.setBackground(Color.white);
         xItems.setValues(AbstractTable.xitems);
         xpanel.add(xLabel);
         xpanel.add(xItems);
         options.add(xpanel);
 
         JLabel xALabel = new JLabel(JMeterUtils.getResString("report_chart_x_axis_label"));
         HorizontalPanel xApanel = new HorizontalPanel(Color.white);
         xALabel.setBorder(new EmptyBorder(5,2,5,2));
         xAxisLabel.setBackground(Color.white);
         xAxisLabel.setValues(AbstractChart.X_LABELS);
         xApanel.add(xALabel);
         xApanel.add(xAxisLabel);
         options.add(xApanel);
 
         JLabel yLabel = new JLabel(JMeterUtils.getResString("report_chart_y_axis"));
         HorizontalPanel ypanel = new HorizontalPanel(Color.white);
         yLabel.setBorder(new EmptyBorder(5,2,5,2));
         yItems.setBackground(Color.white);
         yItems.setValues(AbstractTable.items);
         ypanel.add(yLabel);
         ypanel.add(yItems);
         options.add(ypanel);
         options.add(yAxisLabel);
         options.add(caption);
         options.add(url);
 
         add(pane,BorderLayout.NORTH);
         add(options,BorderLayout.CENTER);
     }
 
+    @Override
     public TestElement createTestElement() {
         BarChart element = new BarChart();
         modifyTestElement(element);
         return element;
     }
 
+    @Override
     public void modifyTestElement(TestElement element) {
         this.configureTestElement(element);
         BarChart bc = (BarChart)element;
         bc.setXAxis(xItems.getText());
         bc.setYAxis(yItems.getText());
         bc.setXLabel(xAxisLabel.getText());
         bc.setYLabel(yAxisLabel.getText());
         bc.setCaption(caption.getText());
         bc.setURL(url.getText());
     }
 
     @Override
     public void configure(TestElement element) {
         super.configure(element);
         BarChart bc = (BarChart)element;
         xItems.setText(bc.getXAxis());
         yItems.setText(bc.getYAxis());
         xAxisLabel.setText(bc.getXLabel());
         yAxisLabel.setText(bc.getYLabel());
         caption.setText(bc.getCaption());
         url.setText(bc.getURL());
     }
 
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/LineGraphGui.java b/src/reports/org/apache/jmeter/report/gui/LineGraphGui.java
index cd12ea551..aec9336ce 100644
--- a/src/reports/org/apache/jmeter/report/gui/LineGraphGui.java
+++ b/src/reports/org/apache/jmeter/report/gui/LineGraphGui.java
@@ -1,152 +1,154 @@
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
 package org.apache.jmeter.report.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.ReportMenuFactory;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.AbstractTable;
 import org.apache.jmeter.testelement.AbstractChart;
 import org.apache.jmeter.testelement.LineChart;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 public class LineGraphGui extends AbstractReportGui {
 
     private static final long serialVersionUID = 240L;
 
     private JLabeledChoice xAxisLabel = new JLabeledChoice();
 
     private JLabeledTextField yAxisLabel =
         new JLabeledTextField(JMeterUtils.getResString("report_chart_y_axis_label"));
 
     private JLabeledTextField caption =
         new JLabeledTextField(JMeterUtils.getResString("report_chart_caption"),
                 Color.white);
 
     private JLabeledTextField urls =
         new JLabeledTextField(JMeterUtils.getResString("report_line_graph_urls"),
                 Color.white);
 
     private JLabeledChoice yItems = new JLabeledChoice();
     private JLabeledChoice xItems = new JLabeledChoice();
 
     public LineGraphGui() {
         super();
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "report_line_graph";
     }
 
     @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         ReportMenuFactory.addFileMenu(pop);
         ReportMenuFactory.addEditMenu(pop,true);
         return pop;
     }
 
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(10, 10));
         setBorder(makeBorder());
         setBackground(Color.white);
 
         JPanel pane = new JPanel();
         pane.setLayout(new BorderLayout(10,10));
         pane.setBackground(Color.white);
         pane.add(this.getNamePanel(),BorderLayout.NORTH);
 
         VerticalPanel options = new VerticalPanel(Color.white);
         yAxisLabel.setBackground(Color.white);
 
         JLabel xLabel = new JLabel(JMeterUtils.getResString("report_chart_x_axis"));
         HorizontalPanel xpanel = new HorizontalPanel(Color.white);
         xLabel.setBorder(new EmptyBorder(5,2,5,2));
         xItems.setBackground(Color.white);
         xItems.setValues(AbstractTable.xitems);
         xpanel.add(xLabel);
         xpanel.add(xItems);
         options.add(xpanel);
 
         JLabel xALabel = new JLabel(JMeterUtils.getResString("report_chart_x_axis_label"));
         HorizontalPanel xApanel = new HorizontalPanel(Color.white);
         xALabel.setBorder(new EmptyBorder(5,2,5,2));
         xAxisLabel.setBackground(Color.white);
         xAxisLabel.setValues(AbstractChart.X_LABELS);
         xApanel.add(xALabel);
         xApanel.add(xAxisLabel);
         options.add(xApanel);
 
         JLabel yLabel = new JLabel(JMeterUtils.getResString("report_chart_y_axis"));
         HorizontalPanel ypanel = new HorizontalPanel(Color.white);
         yLabel.setBorder(new EmptyBorder(5,2,5,2));
         yItems.setBackground(Color.white);
         yItems.setValues(AbstractTable.items);
         ypanel.add(yLabel);
         ypanel.add(yItems);
         options.add(ypanel);
         options.add(yAxisLabel);
         options.add(caption);
         options.add(urls);
 
         add(pane,BorderLayout.NORTH);
         add(options,BorderLayout.CENTER);
     }
 
+    @Override
     public TestElement createTestElement() {
         LineChart element = new LineChart();
         modifyTestElement(element);
         return element;
     }
 
+    @Override
     public void modifyTestElement(TestElement element) {
         this.configureTestElement(element);
         LineChart bc = (LineChart)element;
         bc.setXAxis(xItems.getText());
         bc.setYAxis(yItems.getText());
         bc.setXLabel(xAxisLabel.getText());
         bc.setYLabel(yAxisLabel.getText());
         bc.setCaption(caption.getText());
         bc.setURLs(urls.getText());
     }
 
     @Override
     public void configure(TestElement element) {
         super.configure(element);
         LineChart bc = (LineChart)element;
         xItems.setText(bc.getXAxis());
         yItems.setText(bc.getYAxis());
         xAxisLabel.setText(bc.getXLabel());
         yAxisLabel.setText(bc.getYLabel());
         caption.setText(bc.getCaption());
         urls.setText(bc.getURLs());
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/ReportPageGui.java b/src/reports/org/apache/jmeter/report/gui/ReportPageGui.java
index 44492237e..374ba9c6a 100644
--- a/src/reports/org/apache/jmeter/report/gui/ReportPageGui.java
+++ b/src/reports/org/apache/jmeter/report/gui/ReportPageGui.java
@@ -1,146 +1,148 @@
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
 package org.apache.jmeter.report.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 
 import javax.swing.JCheckBox;
 import javax.swing.JMenu;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 
 import org.apache.jmeter.gui.util.ReportMenuFactory;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.ReportPage;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledTextArea;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 public class ReportPageGui extends AbstractReportGui {
 
     private static final long serialVersionUID = 240L;
 
     private JLabeledTextField pageTitle = new JLabeledTextField(JMeterUtils.getResString("report_page_title"));
 
     private JCheckBox makeIndex = new JCheckBox(JMeterUtils.getResString("report_page_index"));
 
     private JLabeledTextField cssURL =
         new JLabeledTextField(JMeterUtils.getResString("report_page_style_url"));
 
     private JLabeledTextField headerURL =
         new JLabeledTextField(JMeterUtils.getResString("report_page_header"));
 
     private JLabeledTextField footerURL =
         new JLabeledTextField(JMeterUtils.getResString("report_page_footer"));
 
     private JLabeledTextArea introduction =
         new JLabeledTextArea(JMeterUtils.getResString("report_page_intro"));
 
     /**
      *
      */
     public ReportPageGui() {
         init();
     }
 
     /**
      * Initialize the components and layout of this component.
      */
     private void init() {
         setLayout(new BorderLayout(10, 10));
         setBorder(makeBorder());
         setBackground(Color.white);
 
         JPanel pane = new JPanel();
         pane.setLayout(new BorderLayout(10,10));
         pane.setBackground(Color.white);
         pane.add(this.getNamePanel(),BorderLayout.NORTH);
 
         VerticalPanel options = new VerticalPanel(Color.white);
         pageTitle.setBackground(Color.white);
         makeIndex.setBackground(Color.white);
         cssURL.setBackground(Color.white);
         headerURL.setBackground(Color.white);
         footerURL.setBackground(Color.white);
         introduction.setBackground(Color.white);
         options.add(pageTitle);
         options.add(makeIndex);
         options.add(cssURL);
         options.add(headerURL);
         options.add(footerURL);
         options.add(introduction);
         add(pane,BorderLayout.NORTH);
         add(options,BorderLayout.CENTER);
     }
 
     @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         JMenu addMenu = new JMenu(JMeterUtils.getResString("Add"));
         addMenu.add(ReportMenuFactory.makeMenuItem(new TableGui().getStaticLabel(),
                 TableGui.class.getName(),
                 "Add"));
         addMenu.add(ReportMenuFactory.makeMenuItem(new BarChartGui().getStaticLabel(),
                 BarChartGui.class.getName(),
                 "Add"));
         addMenu.add(ReportMenuFactory.makeMenuItem(new LineGraphGui().getStaticLabel(),
                 LineGraphGui.class.getName(),
                 "Add"));
         pop.add(addMenu);
         ReportMenuFactory.addFileMenu(pop);
         ReportMenuFactory.addEditMenu(pop,true);
         return pop;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
+    @Override
     public TestElement createTestElement() {
         ReportPage element = new ReportPage();
         modifyTestElement(element);
         return element;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(org.apache.jmeter.testelement.TestElement)
      */
+    @Override
     public void modifyTestElement(TestElement element) {
         super.configureTestElement(element);
         ReportPage page = (ReportPage)element;
         page.setCSS(cssURL.getText());
         page.setFooterURL(footerURL.getText());
         page.setHeaderURL(headerURL.getText());
         page.setIndex(String.valueOf(makeIndex.isSelected()));
         page.setIntroduction(introduction.getText());
         page.setTitle(pageTitle.getText());
     }
 
     @Override
     public void configure(TestElement element) {
         super.configure(element);
         ReportPage page = (ReportPage)element;
         cssURL.setText(page.getCSS());
         footerURL.setText(page.getFooterURL());
         headerURL.setText(page.getHeaderURL());
         makeIndex.setSelected(page.getIndex());
         introduction.setText(page.getIntroduction());
         pageTitle.setText(page.getTitle());
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/TableGui.java b/src/reports/org/apache/jmeter/report/gui/TableGui.java
index 2cc060ee2..6cd9dfb16 100644
--- a/src/reports/org/apache/jmeter/report/gui/TableGui.java
+++ b/src/reports/org/apache/jmeter/report/gui/TableGui.java
@@ -1,149 +1,152 @@
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
 package org.apache.jmeter.report.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.util.ReportMenuFactory;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.testelement.Table;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 
 public class TableGui extends AbstractReportGui implements ChangeListener {
 
     private static final long serialVersionUID = 240L;
 
     private JCheckBox meanCheck = new JCheckBox(JMeterUtils.getResString("average"));
     private JCheckBox medianCheck = new JCheckBox(JMeterUtils.getResString("graph_results_median"));
     private JCheckBox maxCheck = new JCheckBox(JMeterUtils.getResString("aggregate_report_max"));
     private JCheckBox minCheck = new JCheckBox(JMeterUtils.getResString("aggregate_report_min"));
     private JCheckBox responseRateCheck =
         new JCheckBox(JMeterUtils.getResString("aggregate_report_rate"));
     private JCheckBox transferRateCheck =
         new JCheckBox(JMeterUtils.getResString("aggregate_report_bandwidth"));
     private JCheckBox fiftypercentCheck =
         new JCheckBox(JMeterUtils.getResString("monitor_label_left_middle"));
     private JCheckBox nintypercentCheck =
         new JCheckBox(JMeterUtils.getResString("aggregate_report_90"));
     private JCheckBox errorRateCheck =
         new JCheckBox(JMeterUtils.getResString("aggregate_report_error"));
 
     public TableGui() {
         super();
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "report_table";
     }
 
     /**
      * Initialize the components and layout of this component.
      */
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(10, 10));
         setBorder(makeBorder());
         setBackground(Color.white);
 
         JPanel pane = new JPanel();
         pane.setLayout(new BorderLayout(10,10));
         pane.setBackground(Color.white);
         pane.add(this.getNamePanel(),BorderLayout.NORTH);
 
         meanCheck.addChangeListener(this);
         VerticalPanel options = new VerticalPanel(Color.white);
         meanCheck.setBackground(Color.white);
         medianCheck.setBackground(Color.white);
         maxCheck.setBackground(Color.white);
         minCheck.setBackground(Color.white);
         responseRateCheck.setBackground(Color.white);
         transferRateCheck.setBackground(Color.white);
         fiftypercentCheck.setBackground(Color.white);
         nintypercentCheck.setBackground(Color.white);
         errorRateCheck.setBackground(Color.white);
         options.add(meanCheck);
         options.add(medianCheck);
         options.add(maxCheck);
         options.add(minCheck);
         options.add(responseRateCheck);
         options.add(transferRateCheck);
         options.add(fiftypercentCheck);
         options.add(nintypercentCheck);
         options.add(errorRateCheck);
 
         add(pane,BorderLayout.NORTH);
         add(options,BorderLayout.CENTER);
     }
 
     @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         ReportMenuFactory.addFileMenu(pop);
         ReportMenuFactory.addEditMenu(pop,true);
         return pop;
     }
 
+    @Override
     public TestElement createTestElement() {
         Table element = new Table();
         modifyTestElement(element);
         return element;
     }
 
+    @Override
     public void modifyTestElement(TestElement element) {
         this.configureTestElement(element);
         Table tb = (Table)element;
         tb.set50Percent(String.valueOf(fiftypercentCheck.isSelected()));
         tb.set90Percent(String.valueOf(nintypercentCheck.isSelected()));
         tb.setErrorRate(String.valueOf(errorRateCheck.isSelected()));
         tb.setMax(String.valueOf(maxCheck.isSelected()));
         tb.setMean(String.valueOf(meanCheck.isSelected()));
         tb.setMedian(String.valueOf(medianCheck.isSelected()));
         tb.setMin(String.valueOf(minCheck.isSelected()));
         tb.setResponseRate(String.valueOf(responseRateCheck.isSelected()));
         tb.setTransferRate(String.valueOf(transferRateCheck.isSelected()));
     }
 
     @Override
     public void configure(TestElement element) {
         super.configure(element);
         Table tb = (Table)element;
         meanCheck.setSelected(tb.getMean());
         medianCheck.setSelected(tb.getMedian());
         maxCheck.setSelected(tb.getMax());
         minCheck.setSelected(tb.getMin());
         fiftypercentCheck.setSelected(tb.get50Percent());
         nintypercentCheck.setSelected(tb.get90Percent());
         errorRateCheck.setSelected(tb.getErrorRate());
         responseRateCheck.setSelected(tb.getResponseRate());
         transferRateCheck.setSelected(tb.getTransferRate());
     }
 
+    @Override
     public void stateChanged(ChangeEvent e) {
         modifyTestElement(ReportGuiPackage.getInstance().getCurrentElement());
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/AbstractAction.java b/src/reports/org/apache/jmeter/report/gui/action/AbstractAction.java
index 64b0310ba..33024a424 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/AbstractAction.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/AbstractAction.java
@@ -1,62 +1,64 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Parent class for implementing Menu item commands
  */
 public abstract class AbstractAction implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * @see Command#doAction(ActionEvent)
      */
+    @Override
     public void doAction(ActionEvent e) {
     }
 
     /**
      * @see Command#getActionNames()
      */
+    @Override
     abstract public Set<String> getActionNames();
 
     /**
      * @param e
      */
     protected void popupShouldSave(ActionEvent e) {
         log.debug("popupShouldSave");
         if (ReportGuiPackage.getInstance().getReportPlanFile() == null) {
             if (JOptionPane.showConfirmDialog(ReportGuiPackage.getInstance().getMainFrame(), JMeterUtils
                     .getResString("should_save"), JMeterUtils.getResString("warning"), JOptionPane.YES_NO_OPTION,
                     JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                 ReportActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ReportSave.SAVE));
             }
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportActionRouter.java b/src/reports/org/apache/jmeter/report/gui/action/ReportActionRouter.java
index c2e2726fa..7151bcb2a 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportActionRouter.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportActionRouter.java
@@ -1,313 +1,315 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.HeadlessException;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.lang.reflect.Modifier;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import javax.swing.SwingUtilities;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.log.Logger;
 
 public final class ReportActionRouter implements ActionListener {
     private Map<String, Set<Command>> commands = new HashMap<String, Set<Command>>();
 
     private static volatile ReportActionRouter router;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private static final Object LOCK = new Object();
 
     private Map<String, HashSet<ActionListener>> preActionListeners =
         new HashMap<String, HashSet<ActionListener>>();
 
     private Map<String, HashSet<ActionListener>> postActionListeners =
         new HashMap<String, HashSet<ActionListener>>();
 
     private ReportActionRouter() {
     }
 
+    @Override
     public void actionPerformed(final ActionEvent e) {
         SwingUtilities.invokeLater(new Runnable() {
+            @Override
             public void run() {
                 performAction(e);
             }
 
         });
     }
 
     private void performAction(final ActionEvent e) {
         try {
             ReportGuiPackage.getInstance().updateCurrentNode();
             Set<Command> commandObjects = commands.get(e.getActionCommand());
             Iterator<Command> iter = commandObjects.iterator();
             while (iter.hasNext()) {
                 try {
                     Command c = iter.next();
                     preActionPerformed(c.getClass(), e);
                     c.doAction(e);
                     postActionPerformed(c.getClass(), e);
                 } catch (IllegalUserActionException err) {
                     JMeterUtils.reportErrorToUser(err.toString());
                 } catch (Exception err) {
                     log.error("", err);
                 }
             }
         } catch (NullPointerException er) {
             log.error("performAction(" + e.getActionCommand() + ") " + e.toString() + " caused", er);
             JMeterUtils.reportErrorToUser("Sorry, this feature (" + e.getActionCommand() + ") not yet implemented");
         }
     }
 
     /**
      * To execute an action immediately in the current thread.
      *
      * @param e
      *            the action to execute
      */
     public void doActionNow(ActionEvent e) {
         performAction(e);
     }
 
     public Set<Command> getAction(String actionName) {
         Set<Command> set = new HashSet<Command>();
         Set<Command> commandObjects = commands.get(actionName);
         Iterator<Command> iter = commandObjects.iterator();
         while (iter.hasNext()) {
             try {
                 set.add(iter.next());
             } catch (Exception err) {
                 log.error("", err);
             }
         }
         return set;
     }
 
     public Command getAction(String actionName, Class<?> actionClass) {
         Set<Command> commandObjects = commands.get(actionName);
         Iterator<Command> iter = commandObjects.iterator();
         while (iter.hasNext()) {
             try {
                 Command com = iter.next();
                 if (com.getClass().equals(actionClass)) {
                     return com;
                 }
             } catch (Exception err) {
                 log.error("", err);
             }
         }
         return null;
     }
 
     public Command getAction(String actionName, String className) {
         Set<Command> commandObjects = commands.get(actionName);
         Iterator<Command> iter = commandObjects.iterator();
         while (iter.hasNext()) {
             try {
                 Command com = iter.next();
                 if (com.getClass().getName().equals(className)) {
                     return com;
                 }
             } catch (Exception err) {
                 log.error("", err);
             }
         }
         return null;
     }
 
     /**
      * Allows an ActionListener to receive notification of a command being
      * executed prior to the actual execution of the command.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.report.gui.action.Command.
      * @param listener
      *            the ActionListener to receive the notifications
      */
     public void addPreActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = preActionListeners.get(action.getName());
             if (set == null) {
                 set = new HashSet<ActionListener>();
             }
             set.add(listener);
             preActionListeners.put(action.getName(), set);
         }
     }
 
     /**
      * Allows an ActionListener to be removed from receiving notifications of a
      * command being executed prior to the actual execution of the command.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.report.gui.action.Command.
      * @param listener
      *            the ActionListener to receive the notifications
      */
     public void removePreActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = preActionListeners.get(action.getName());
             if (set != null) {
                 set.remove(listener);
                 preActionListeners.put(action.getName(), set);
             }
         }
     }
 
     /**
      * Allows an ActionListener to receive notification of a command being
      * executed after the command has executed.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.report.gui.action.Command.
      * @param listener
      */
     public void addPostActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = postActionListeners.get(action.getName());
             if (set == null) {
                 set = new HashSet<ActionListener>();
             }
             set.add(listener);
             postActionListeners.put(action.getName(), set);
         }
     }
 
     /**
      * Allows an ActionListener to be removed from receiving notifications of a
      * command being executed after the command has executed.
      *
      * @param action
      *            the Class of the command for which the listener will
      *            notifications for. Class must extend
      *            org.apache.jmeter.report.gui.action.Command.
      * @param listener
      */
     public void removePostActionListener(Class<?> action, ActionListener listener) {
         if (action != null) {
             HashSet<ActionListener> set = postActionListeners.get(action.getName());
             if (set != null) {
                 set.remove(listener);
                 postActionListeners.put(action.getName(), set);
             }
         }
     }
 
     protected void preActionPerformed(Class<? extends Command> action, ActionEvent e) {
         if (action != null) {
             HashSet<ActionListener> listenerSet = preActionListeners.get(action.getName());
             if (listenerSet != null && listenerSet.size() > 0) {
                 Object[] listeners = listenerSet.toArray();
                 for (int i = 0; i < listeners.length; i++) {
                     ((ActionListener) listeners[i]).actionPerformed(e);
                 }
             }
         }
     }
 
     protected void postActionPerformed(Class<? extends Command> action, ActionEvent e) {
         if (action != null) {
             HashSet<ActionListener> listenerSet = postActionListeners.get(action.getName());
             if (listenerSet != null && listenerSet.size() > 0) {
                 Object[] listeners = listenerSet.toArray();
                 for (int i = 0; i < listeners.length; i++) {
                     ((ActionListener) listeners[i]).actionPerformed(e);
                 }
             }
         }
     }
 
     private void populateCommandMap() {
         log.info("populateCommandMap called");
         List<String> listClasses;
         Command command;
         Iterator<String> iterClasses;
         Class<?> commandClass;
         try {
             listClasses = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(), new Class[] { Class
                     .forName("org.apache.jmeter.gui.action.Command") });
             commands = new HashMap<String, Set<Command>>(listClasses.size());
             if (listClasses.size() == 0) {
                 log.warn("!!!!!Uh-oh, didn't find any action handlers!!!!!");
             }
             iterClasses = listClasses.iterator();
             while (iterClasses.hasNext()) {
                 String strClassName = iterClasses.next();
                 if (strClassName.startsWith("org.apache.jmeter.report.gui.action")) {
                     // log.info("classname:: " + strClassName);
                     commandClass = Class.forName(strClassName);
                     if (!Modifier.isAbstract(commandClass.getModifiers())) {
                         command = (Command) commandClass.newInstance();
                         Iterator<String> iter = command.getActionNames().iterator();
                         while (iter.hasNext()) {
                             String commandName = iter.next();
                             Set<Command> commandObjects = commands.get(commandName);
                             if (commandObjects == null) {
                                 commandObjects = new HashSet<Command>();
                                 commands.put(commandName, commandObjects);
                             }
                             commandObjects.add(command);
                         }
                     }
                 }
             }
         } catch (HeadlessException e){
             log.warn(e.toString());
         } catch (Exception e) {
             log.error("exception finding action handlers", e);
         }
     }
 
     /**
      * Gets the Instance attribute of the ActionRouter class
      *
      * @return The Instance value
      */
     public static ReportActionRouter getInstance() {
         if (router == null) {
         	synchronized (LOCK) {
         		if(router == null) {
 	                router = new ReportActionRouter();
 	                router.populateCommandMap();				
         		}
 			}
         }
         return router;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportAddParent.java b/src/reports/org/apache/jmeter/report/gui/action/ReportAddParent.java
index 8adc12bbc..dd6d5ace3 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportAddParent.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportAddParent.java
@@ -1,82 +1,84 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.Component;
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportAddParent implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add("Add Parent");
     }
 
     public ReportAddParent() {
     }
 
+    @Override
     public void doAction(ActionEvent e) {
         String name = ((Component) e.getSource()).getName();
         try {
             TestElement controller = ReportGuiPackage.getInstance()
                     .createTestElement(name);
             addParentToTree(controller);
         } catch (Exception err) {
             log.error("", err);
         }
 
     }
 
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     protected void addParentToTree(TestElement newParent) {
         ReportGuiPackage guiPackage = ReportGuiPackage.getInstance();
         ReportTreeNode newNode = new ReportTreeNode(newParent, guiPackage
                 .getTreeModel());
         ReportTreeNode currentNode = guiPackage.getTreeListener()
                 .getCurrentNode();
         ReportTreeNode parentNode = (ReportTreeNode) currentNode.getParent();
         int index = parentNode.getIndex(currentNode);
         guiPackage.getTreeModel().insertNodeInto(newNode, parentNode, index);
         ReportTreeNode[] nodes = guiPackage.getTreeListener()
                 .getSelectedNodes();
         for (int i = 0; i < nodes.length; i++) {
             moveNode(guiPackage, nodes[i], newNode);
         }
     }
 
     private void moveNode(ReportGuiPackage guiPackage, ReportTreeNode node,
             ReportTreeNode newParentNode) {
         guiPackage.getTreeModel().removeNodeFromParent(node);
         guiPackage.getTreeModel().insertNodeInto(node, newParentNode,
                 newParentNode.getChildCount());
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportAddToTree.java b/src/reports/org/apache/jmeter/report/gui/action/ReportAddToTree.java
index 1c9d7206c..a2633df72 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportAddToTree.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportAddToTree.java
@@ -1,82 +1,84 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 
 import javax.swing.JComponent;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportAddToTree implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private Map<String, String> allJMeterComponentCommands;
 
     public ReportAddToTree() {
         allJMeterComponentCommands = new HashMap<String, String>();
         allJMeterComponentCommands.put("Add", "Add");
     }
 
     /**
      * Gets the Set of actions this Command class responds to.
      *
      * @return the ActionNames value
      */
+    @Override
     public Set<String> getActionNames() {
         return allJMeterComponentCommands.keySet();
     }
 
     /**
      * Adds the specified class to the current node of the tree.
      */
+    @Override
     public void doAction(ActionEvent e) {
         try {
             TestElement node = ReportGuiPackage.getInstance()
                     .createTestElement(((JComponent) e.getSource()).getName());
             addObjectToTree(node);
         } catch (Exception err) {
             log.error("", err);
         }
     }
 
     protected void addObjectToTree(TestElement el) {
         ReportGuiPackage guiPackage = ReportGuiPackage.getInstance();
         ReportTreeNode node = new ReportTreeNode(el, guiPackage.getTreeModel());
         guiPackage.getTreeModel().insertNodeInto(node,
                 guiPackage.getTreeListener().getCurrentNode(),
                 guiPackage.getTreeListener().getCurrentNode().getChildCount());
         TestElement curNode =
             (TestElement)guiPackage.getTreeListener().getCurrentNode().getUserObject();
         if (curNode != null) {
             curNode.addTestElement(el);
             guiPackage.getMainFrame().getTree().setSelectionPath(
                     new TreePath(node.getPath()));
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportCheckDirty.java b/src/reports/org/apache/jmeter/report/gui/action/ReportCheckDirty.java
index d1815b54a..a8b6d5d36 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportCheckDirty.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportCheckDirty.java
@@ -1,161 +1,165 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.report.gui.action.AbstractAction;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.report.gui.action.ReportExitCommand;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportCheckDirty extends AbstractAction implements HashTreeTraverser, ActionListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static Map<ReportTreeNode, TestElement> previousGuiItems;
 
     public static final String CHECK_DIRTY = "check_dirty";
 
     public static final String SUB_TREE_SAVED = "sub_tree_saved";
 
     public static final String SUB_TREE_LOADED = "sub_tree_loaded";
 
     public static final String ADD_ALL = "add_all";
 
     // Not implemented: public static final String SAVE = "save_as";
     // Not implemented: public static final String SAVE_ALL = "save_all";
     // Not implemented: public static final String SAVE_TO_PREVIOUS = "save";
     public static final String REMOVE = "check_remove";
 
     boolean checkMode = false;
 
     boolean removeMode = false;
 
     boolean dirty = false;
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add(CHECK_DIRTY);
         commands.add(SUB_TREE_SAVED);
         commands.add(SUB_TREE_LOADED);
         commands.add(ADD_ALL);
         commands.add(REMOVE);
     }
 
     public ReportCheckDirty() {
         previousGuiItems = new HashMap<ReportTreeNode, TestElement>();
         ReportActionRouter.getInstance().addPreActionListener(ReportExitCommand.class, this);
     }
 
+    @Override
     public void actionPerformed(ActionEvent e) {
         if (e.getActionCommand().equals(ReportExitCommand.EXIT)) {
             doAction(e);
         }
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
      */
     @Override
     public void doAction(ActionEvent e) {
         String action = e.getActionCommand();
         if (action.equals(SUB_TREE_SAVED)) {
             HashTree subTree = (HashTree) e.getSource();
             subTree.traverse(this);
         } else if (action.equals(SUB_TREE_LOADED)) {
             ListedHashTree addTree = (ListedHashTree) e.getSource();
             addTree.traverse(this);
         } else if (action.equals(ADD_ALL)) {
             previousGuiItems.clear();
             ReportGuiPackage.getInstance().getTreeModel().getReportPlan().traverse(this);
         } else if (action.equals(REMOVE)) {
             ReportGuiPackage guiPackage = ReportGuiPackage.getInstance();
             ReportTreeNode[] nodes = guiPackage.getTreeListener().getSelectedNodes();
             removeMode = true;
             for (int i = nodes.length - 1; i >= 0; i--) {
                 guiPackage.getTreeModel().getCurrentSubTree(nodes[i]).traverse(this);
             }
             removeMode = false;
         }
         checkMode = true;
         dirty = false;
         HashTree wholeTree = ReportGuiPackage.getInstance().getTreeModel().getReportPlan();
         wholeTree.traverse(this);
         ReportGuiPackage.getInstance().setDirty(dirty);
         checkMode = false;
     }
 
     /**
      * The tree traverses itself depth-first, calling processNode for each
      * object it encounters as it goes.
      */
+    @Override
     public void addNode(Object node, HashTree subTree) {
         log.debug("Node is class:" + node.getClass());
         ReportTreeNode treeNode = (ReportTreeNode) node;
         if (checkMode) {
             if (previousGuiItems.containsKey(treeNode)) {
                 if (!previousGuiItems.get(treeNode).equals(treeNode.getTestElement())) {
                     dirty = true;
                 }
             } else {
                 dirty = true;
             }
         } else if (removeMode) {
             previousGuiItems.remove(treeNode);
         } else {
             previousGuiItems.put(treeNode, (TestElement) treeNode.getTestElement().clone());
         }
     }
 
     /**
      * Indicates traversal has moved up a step, and the visitor should remove
      * the top node from it's stack structure.
      */
+    @Override
     public void subtractNode() {
     }
 
     /**
      * Process path is called when a leaf is reached. If a visitor wishes to
      * generate Lists of path elements to each leaf, it should keep a Stack data
      * structure of nodes passed to it with addNode, and removing top items for
      * every subtractNode() call.
      */
+    @Override
     public void processPath() {
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
     @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportClose.java b/src/reports/org/apache/jmeter/report/gui/action/ReportClose.java
index 6218df56c..b383feb7c 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportClose.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportClose.java
@@ -1,89 +1,91 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * This command clears the existing test plan, allowing the creation of a New
  * test plan.
  *
  */
 public class ReportClose implements Command {
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add("close");
     }
 
     /**
      * Constructor for the Close object.
      */
     public ReportClose() {
     }
 
     /**
      * Gets the ActionNames attribute of the Close object.
      *
      * @return the ActionNames value
      */
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     /**
      * This method performs the actual command processing.
      *
      * @param e
      *            the generic UI action event
      */
+    @Override
     public void doAction(ActionEvent e) {
         ReportActionRouter.getInstance().doActionNow(
                 new ActionEvent(e.getSource(), e.getID(),
                         ReportCheckDirty.CHECK_DIRTY));
         ReportGuiPackage guiPackage = ReportGuiPackage.getInstance();
         if (guiPackage.isDirty()) {
             if (JOptionPane.showConfirmDialog(ReportGuiPackage.getInstance()
                     .getMainFrame(), JMeterUtils
                     .getResString("cancel_new_to_save"), JMeterUtils
                     .getResString("Save?"), JOptionPane.YES_NO_OPTION,
                     JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                 ReportActionRouter.getInstance().doActionNow(
                         new ActionEvent(e.getSource(), e.getID(), ActionNames.SAVE));
             }
         }
         guiPackage.getTreeModel().clearTestPlan();
         guiPackage.getTreeListener().getJTree().setSelectionRow(1);
 
         // Clear the name of the test plan file
         ReportGuiPackage.getInstance().setReportPlanFile(null);
 
         ReportActionRouter.getInstance().actionPerformed(
                 new ActionEvent(e.getSource(), e.getID(), ActionNames.ADD_ALL));
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportEditCommand.java b/src/reports/org/apache/jmeter/report/gui/action/ReportEditCommand.java
index 8ab5fc735..220b3999c 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportEditCommand.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportEditCommand.java
@@ -1,57 +1,59 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.NamePanel;
 
 public class ReportEditCommand implements Command {
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add("edit");
     }
 
     public ReportEditCommand() {
     }
 
+    @Override
     public void doAction(ActionEvent e) {
         ReportGuiPackage guiPackage = ReportGuiPackage.getInstance();
         guiPackage.getMainFrame().setMainPanel((javax.swing.JComponent) guiPackage.getCurrentGui());
         guiPackage.getMainFrame().setEditMenu(guiPackage.getTreeListener().getCurrentNode().createPopupMenu());
         // TODO: I believe the following code (to the end of the method) is
         // obsolete,
         // since NamePanel no longer seems to be the GUI for any component:
         if (!(guiPackage.getCurrentGui() instanceof NamePanel)) {
             guiPackage.getMainFrame().setFileLoadEnabled(true);
             guiPackage.getMainFrame().setFileSaveEnabled(true);
         } else {
             guiPackage.getMainFrame().setFileLoadEnabled(false);
             guiPackage.getMainFrame().setFileSaveEnabled(false);
         }
     }
 
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportEnableComponent.java b/src/reports/org/apache/jmeter/report/gui/action/ReportEnableComponent.java
index 7eeed52b0..85c5645f6 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportEnableComponent.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportEnableComponent.java
@@ -1,73 +1,75 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportEnableComponent implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String ENABLE = "enable";
 
     public static final String DISABLE = "disable";
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add(ENABLE);
         commands.add(DISABLE);
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
      */
+    @Override
     public void doAction(ActionEvent e) {
         ReportTreeNode[] nodes = ReportGuiPackage.getInstance().getTreeListener().getSelectedNodes();
 
         if (e.getActionCommand().equals(ENABLE)) {
             log.debug("enabling currently selected gui objects");
             enableComponents(nodes, true);
         } else if (e.getActionCommand().equals(DISABLE)) {
             log.debug("disabling currently selected gui objects");
             enableComponents(nodes, false);
         }
     }
 
     private void enableComponents(ReportTreeNode[] nodes, boolean enable) {
         ReportGuiPackage pack = ReportGuiPackage.getInstance();
         for (int i = 0; i < nodes.length; i++) {
             nodes[i].setEnabled(enable);
             pack.getGui(nodes[i].getTestElement()).setEnabled(enable);
         }
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportExitCommand.java b/src/reports/org/apache/jmeter/report/gui/action/ReportExitCommand.java
index fd7b1abb3..5e3f79f52 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportExitCommand.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportExitCommand.java
@@ -1,87 +1,89 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.report.gui.action.ReportSave;
 import org.apache.jmeter.util.JMeterUtils;
 
 public class ReportExitCommand implements Command {
 
     public static final String EXIT = "exit";
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add(EXIT);
     }
 
     /**
      * Constructor for the ExitCommand object
      */
     public ReportExitCommand() {
     }
 
     /**
      * Gets the ActionNames attribute of the ExitCommand object
      *
      * @return The ActionNames value
      */
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
     /**
      * Description of the Method
      *
      * @param e
      *            Description of Parameter
      */
+    @Override
     public void doAction(ActionEvent e) {
         ReportActionRouter.getInstance().doActionNow(
                 new ActionEvent(e.getSource(), e.getID(),
                         ReportCheckDirty.CHECK_DIRTY));
         if (ReportGuiPackage.getInstance().isDirty()) {
             int chosenOption = JOptionPane.showConfirmDialog(ReportGuiPackage
                     .getInstance().getMainFrame(), JMeterUtils
                     .getResString("cancel_exit_to_save"), JMeterUtils
                     .getResString("Save?"), JOptionPane.YES_NO_CANCEL_OPTION,
                     JOptionPane.QUESTION_MESSAGE);
             if (chosenOption == JOptionPane.NO_OPTION) {
                 System.exit(0);
             } else if (chosenOption == JOptionPane.YES_OPTION) {
                 ReportActionRouter.getInstance().doActionNow(
                         new ActionEvent(e.getSource(), e.getID(),
                                 ReportSave.SAVE_ALL_AS));
                 if (!ReportGuiPackage.getInstance().isDirty()) {
                     System.exit(0);
                 }
             }
         } else {
             System.exit(0);
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportHelp.java b/src/reports/org/apache/jmeter/report/gui/action/ReportHelp.java
index 88869fb62..75a3f06e3 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportHelp.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportHelp.java
@@ -1,121 +1,123 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.Frame;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.io.IOException;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JDialog;
 import javax.swing.JScrollPane;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.swing.HtmlPane;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportHelp implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String HELP = "help";
 
     private static final Set<String> commands = new HashSet<String>();
 
     public static final String HELP_DOCS = "file:///" + JMeterUtils.getJMeterHome() + "/printable_docs/usermanual/";
 
     public static final String HELP_PAGE = HELP_DOCS + "component_reference.html";
 
     public static final String HELP_FUNCTIONS = HELP_DOCS + "functions.html";
 
     private static JDialog helpWindow;
 
     private static HtmlPane helpDoc;
 
     private static JScrollPane scroller;
 
     private static String currentPage;
 
     static {
         commands.add(HELP);
         helpDoc = new HtmlPane();
         scroller = new JScrollPane(helpDoc);
         helpDoc.setEditable(false);
         try {
             helpDoc.setPage(HELP_PAGE);
             currentPage = HELP_PAGE;
         } catch (IOException err) {
             String msg = "Couldn't load help file " + err.toString();
             log.error(msg);
             currentPage = "";// Avoid NPE in resetPage()
         }
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#doAction(ActionEvent)
      */
+    @Override
     public void doAction(ActionEvent e) {
         if (helpWindow == null) {
             helpWindow = new JDialog(new Frame(),// independent frame to
                                                     // allow it to be overlaid
                                                     // by the main frame
                     JMeterUtils.getResString("help"),//$NON-NLS-1$
                     false);
             helpWindow.getContentPane().setLayout(new GridLayout(1, 1));
             ComponentUtil.centerComponentInWindow(helpWindow, 60);
         }
         helpWindow.getContentPane().removeAll();
         helpWindow.getContentPane().add(scroller);
         helpWindow.setVisible(true);
         if (e.getSource() instanceof String[]) {
             String[] source = (String[]) e.getSource();
             resetPage(source[0]);
             helpDoc.scrollToReference(source[1]);
         } else {
             resetPage(HELP_PAGE);
             helpDoc.scrollToReference(ReportGuiPackage.getInstance().getTreeListener().getCurrentNode().getDocAnchor());
 
         }
     }
 
     private void resetPage(String source) {
         if (!currentPage.equals(source)) {
             try {
                 helpDoc.setPage(source);
                 currentPage = source;
             } catch (IOException err) {
                 log.error(err.toString());
                 JMeterUtils.reportErrorToUser("Problem loading a help page - see log for details");
                 currentPage = "";
             }
         }
     }
 
     /**
      * @see org.apache.jmeter.gui.action.Command#getActionNames()
      */
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java b/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java
index 6f3333c72..dbb9de45f 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java
@@ -1,140 +1,142 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.InputStream;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.gui.util.ReportFileDialoger;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.ReportPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public class ReportLoad implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add("open");
         commands.add("merge");
     }
 
     public ReportLoad() {
         super();
     }
 
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
+    @Override
     public void doAction(ActionEvent e) {
         boolean merging = e.getActionCommand().equals("merge");
 
         if (!merging) {
             ReportActionRouter.getInstance().doActionNow(
                     new ActionEvent(e.getSource(), e.getID(), "close"));
         }
 
         JFileChooser chooser = ReportFileDialoger
                 .promptToOpenFile(new String[] { ".jmr" });
         if (chooser == null) {
             return;
         }
         boolean isTestPlan = false;
         InputStream reader = null;
         File f = null;
         try {
             f = chooser.getSelectedFile();
             if (f != null) {
                 if (merging) {
                     log.info("Merging file: " + f);
                 } else {
                     log.info("Loading file: " + f);
                     FileServer.getFileServer().setBaseForScript(f);
                 }
                 reader = new FileInputStream(f);
                 HashTree tree = SaveService.loadTree(reader);
                 isTestPlan = insertLoadedTree(e.getID(), tree);
             }
         } catch (NoClassDefFoundError ex) // Allow for missing optional jars
         {
             String msg = ex.getMessage();
             if (msg == null) {
                 msg = "Missing jar file - see log for details";
                 log.warn("Missing jar file", ex);
             }
             JMeterUtils.reportErrorToUser(msg);
         } catch (Exception ex) {
             String msg = ex.getMessage();
             if (msg == null) {
                 msg = "Unexpected error - see log for details";
                 log.warn("Unexpected error", ex);
             }
             JMeterUtils.reportErrorToUser(msg);
         } finally {
             JOrphanUtils.closeQuietly(reader);
             ReportGuiPackage.getInstance().updateCurrentGui();
             ReportGuiPackage.getInstance().getMainFrame().repaint();
         }
         // don't change name if merging
         if (!merging && isTestPlan && f != null) {
             ReportGuiPackage.getInstance().setReportPlanFile(f.getAbsolutePath());
         }
     }
 
     /**
      * Returns a boolean indicating whether the loaded tree was a full test plan
      */
     public boolean insertLoadedTree(int id, HashTree tree) throws Exception,
             IllegalUserActionException {
         // convertTree(tree);
         if (tree == null) {
             throw new Exception("Error in TestPlan - see log file");
         }
         boolean isTestPlan = tree.getArray()[0] instanceof ReportPlan;
         HashTree newTree = ReportGuiPackage.getInstance().addSubTree(tree);
         ReportGuiPackage.getInstance().updateCurrentGui();
         ReportGuiPackage.getInstance().getMainFrame().getTree()
                 .setSelectionPath(
                         new TreePath(((ReportTreeNode) newTree.getArray()[0])
                                 .getPath()));
         tree = ReportGuiPackage.getInstance().getCurrentSubTree();
         ReportActionRouter.getInstance().actionPerformed(
                 new ActionEvent(tree.get(tree.getArray()[tree.size() - 1]), id,
                         ReportCheckDirty.SUB_TREE_LOADED));
 
         return isTestPlan;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportRemove.java b/src/reports/org/apache/jmeter/report/gui/action/ReportRemove.java
index aeca83696..d3ce3bf77 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportRemove.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportRemove.java
@@ -1,83 +1,85 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JOptionPane;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.report.gui.action.ReportCheckDirty;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.testelement.TestElement;
 
 public class ReportRemove implements Command {
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add("remove");
     }
 
     /**
      * Constructor for the Remove object
      */
     public ReportRemove() {
     }
 
     /**
      * Gets the ActionNames attribute of the Remove object.
      *
      * @return the ActionNames value
      */
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
+    @Override
     public void doAction(ActionEvent e) {
         ReportActionRouter.getInstance().actionPerformed(
                 new ActionEvent(e.getSource(), e.getID(),
                         ReportCheckDirty.REMOVE));
         ReportGuiPackage guiPackage = ReportGuiPackage.getInstance();
         ReportTreeNode[] nodes = guiPackage.getTreeListener()
                 .getSelectedNodes();
         TreePath newTreePath = // Save parent node for later
         guiPackage.getTreeListener().removedSelectedNode();
         for (int i = nodes.length - 1; i >= 0; i--) {
             removeNode(nodes[i]);
         }
         guiPackage.getTreeListener().getJTree().setSelectionPath(newTreePath);
         guiPackage.updateCurrentGui();
     }
 
     public static void removeNode(ReportTreeNode node) {
         TestElement testElement = node.getTestElement();
         if (testElement.canRemove()) {
             ReportGuiPackage.getInstance().getTreeModel().removeNodeFromParent(
                     node);
             ReportGuiPackage.getInstance().removeNode(testElement);
         } else {
             String message = testElement.getClass().getName() + " is busy";
             JOptionPane.showMessageDialog(null, message, "Cannot remove item",
                     JOptionPane.ERROR_MESSAGE);
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java b/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java
index 3ffd4d87e..483921905 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java
@@ -1,131 +1,133 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.FileOutputStream;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.gui.util.ReportFileDialoger;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 public class ReportSave implements Command {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String SAVE_ALL_AS = "save_all_as";
 
     public static final String SAVE_AS = "save_as";
 
     public static final String SAVE = "save";
 
     // NOTUSED private String chosenFile;
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add(SAVE_AS);
         commands.add(SAVE_ALL_AS);
         commands.add(SAVE);
     }
 
     /**
      * Constructor for the Save object.
      */
     public ReportSave() {
     }
 
     /**
      * Gets the ActionNames attribute of the Save object.
      *
      * @return the ActionNames value
      */
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
+    @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         HashTree subTree = null;
         if (!commands.contains(e.getActionCommand())) {
             throw new IllegalUserActionException("Invalid user command:" + e.getActionCommand());
         }
         if (e.getActionCommand().equals(SAVE_AS)) {
             subTree = ReportGuiPackage.getInstance().getCurrentSubTree();
         } else {
             subTree = ReportGuiPackage.getInstance().getTreeModel().getReportPlan();
         }
 
         String updateFile = ReportGuiPackage.getInstance().getReportPlanFile();
         if (!SAVE.equals(e.getActionCommand()) || updateFile == null) {
             JFileChooser chooser = ReportFileDialoger.promptToSaveFile(ReportGuiPackage.getInstance().getTreeListener()
                     .getCurrentNode().getName()
                     + ".jmr");
             if (chooser == null) {
                 return;
             }
             updateFile = chooser.getSelectedFile().getAbsolutePath();
             if (!e.getActionCommand().equals(SAVE_AS)) {
                 ReportGuiPackage.getInstance().setReportPlanFile(updateFile);
             }
         }
         // TODO: doesn't putting this here mark the tree as
         // saved even though a failure may occur later?
 
         ReportActionRouter.getInstance().doActionNow(new ActionEvent(subTree, e.getID(), ReportCheckDirty.SUB_TREE_SAVED));
         try {
             convertSubTree(subTree);
         } catch (Exception err) {
         }
         FileOutputStream ostream = null;
         try {
             ostream = new FileOutputStream(updateFile);
             SaveService.saveTree(subTree, ostream);
             log.info("saveTree");
         } catch (Exception ex) {
             ReportGuiPackage.getInstance().setReportPlanFile(null);
             log.error("", ex);
             throw new IllegalUserActionException("Couldn't save test plan to file: " + updateFile);
         } finally {
             JOrphanUtils.closeQuietly(ostream);
         }
     }
 
     private void convertSubTree(HashTree tree) {
         Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
         while (iter.hasNext()) {
             ReportTreeNode item = (ReportTreeNode) iter.next();
             convertSubTree(tree.getTree(item));
             TestElement testElement = item.getTestElement();
             tree.replace(item, testElement);
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportSaveGraphics.java b/src/reports/org/apache/jmeter/report/gui/action/ReportSaveGraphics.java
index eee048fc7..5ee7ec474 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportSaveGraphics.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportSaveGraphics.java
@@ -1,109 +1,111 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JComponent;
 import javax.swing.JFileChooser;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.util.ReportFileDialoger;
 import org.apache.jmeter.save.SaveGraphicsService;
 import org.apache.jmeter.visualizers.Printable;
 //import org.apache.jorphan.logging.LoggingManager;
 //import org.apache.log.Logger;
 
 /**
  * SaveGraphics action is meant to be a generic reusable Action. The class will
  * use GUIPackage to get the current gui. Once it does, it checks to see if the
  * element implements Printable interface. If it does, it call getPrintable() to
  * get the JComponent. By default, it will use SaveGraphicsService to save a PNG
  * file if no extension is provided. If either .png or .tif is in the filename,
  * it will call SaveGraphicsService to save in the format.
  */
 public class ReportSaveGraphics implements Command {
     //transient private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String SAVE_GRAPHICS = "save_graphics"; // $NON-NLS-1$
 
     private static final Set<String> commands = new HashSet<String>();
     static {
         commands.add(SAVE_GRAPHICS);
     }
 
     private static final String[] extensions = { SaveGraphicsService.TIFF_EXTENSION, SaveGraphicsService.PNG_EXTENSION };
 
     /**
      * Constructor for the Save object.
      */
     public ReportSaveGraphics() {
     }
 
     /**
      * Gets the ActionNames attribute of the Save object.
      *
      * @return the ActionNames value
      */
+    @Override
     public Set<String> getActionNames() {
         return commands;
     }
 
+    @Override
     public void doAction(ActionEvent e) throws IllegalUserActionException {
         JMeterGUIComponent component = null;
         JComponent comp = null;
         if (!commands.contains(e.getActionCommand())) {
             throw new IllegalUserActionException("Invalid user command:" + e.getActionCommand());
         }
         if (e.getActionCommand().equals(SAVE_GRAPHICS)) {
             component = ReportGuiPackage.getInstance().getCurrentGui();
             // get the JComponent from the visualizer
             if (component instanceof Printable) {
                 comp = ((Printable) component).getPrintableComponent();
 
                 String filename;
                 JFileChooser chooser = ReportFileDialoger.promptToSaveFile(ReportGuiPackage.getInstance().getTreeListener()
                         .getCurrentNode().getName(), extensions);
                 if (chooser == null) {
                     return;
                 }
                 // Get the string given from the choose and check
                 // the file extension.
                 filename = chooser.getSelectedFile().getAbsolutePath();
                 if (filename != null) {
                     SaveGraphicsService save = new SaveGraphicsService();
                     String ext = filename.substring(filename.length() - 4);
                     String name = filename.substring(0, filename.length() - 4);
                     if (ext.equals(SaveGraphicsService.PNG_EXTENSION)) {
                         save.saveJComponent(name, SaveGraphicsService.PNG, comp);
                     } else if (ext.equals(SaveGraphicsService.TIFF_EXTENSION)) {
                         save.saveJComponent(name, SaveGraphicsService.TIFF, comp);
                     } else {
                         save.saveJComponent(filename, SaveGraphicsService.PNG, comp);
                     }
                 }
             }
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeListener.java b/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeListener.java
index 274d3f4d3..7ebba1313 100644
--- a/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeListener.java
+++ b/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeListener.java
@@ -1,315 +1,326 @@
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
 
 package org.apache.jmeter.report.gui.tree;
 
 import java.awt.Container;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.InputEvent;
 import java.awt.event.KeyEvent;
 import java.awt.event.KeyListener;
 import java.awt.event.MouseEvent;
 import java.awt.event.MouseListener;
 import java.awt.event.MouseMotionListener;
 
 import javax.swing.JLabel;
 import javax.swing.JMenuItem;
 import javax.swing.JPopupMenu;
 import javax.swing.JTree;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.tree.TreeNode;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.control.gui.ReportGui;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.ReportMainFrame;
 import org.apache.jmeter.report.gui.action.ReportDragNDrop;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportTreeListener implements TreeSelectionListener, MouseListener, KeyListener, MouseMotionListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Container endWindow;
     // JPopupMenu pop;
     private TreePath currentPath;
 
     private ActionListener actionHandler;
 
     private ReportTreeModel model;
 
     private JTree tree;
 
     private boolean dragging = false;
 
     private ReportTreeNode[] draggedNodes;
 
     private JLabel dragIcon = new JLabel(JMeterUtils.getImage("leafnode.gif"));
 
     /**
      * Constructor for the JMeterTreeListener object.
      */
     public ReportTreeListener(ReportTreeModel model) {
         this.model = model;
         dragIcon.validate();
         dragIcon.setVisible(true);
     }
 
     public ReportTreeListener() {
         dragIcon.validate();
         dragIcon.setVisible(true);
     }
 
     public void setModel(ReportTreeModel m) {
         model = m;
     }
 
     /**
      * Sets the ActionHandler attribute of the JMeterTreeListener object.
      *
      * @param ah
      *            the new ActionHandler value
      */
     public void setActionHandler(ActionListener ah) {
         actionHandler = ah;
     }
 
     /**
      * Sets the JTree attribute of the JMeterTreeListener object.
      *
      * @param tree
      *            the new JTree value
      */
     public void setJTree(JTree tree) {
         this.tree = tree;
     }
 
     /**
      * Sets the EndWindow attribute of the JMeterTreeListener object.
      *
      * @param window
      *            the new EndWindow value
      */
     public void setEndWindow(Container window) {
         // endWindow = window;
     }
 
     /**
      * Gets the JTree attribute of the JMeterTreeListener object.
      *
      * @return tree the current JTree value.
      */
     public JTree getJTree() {
         return tree;
     }
 
     /**
      * Gets the CurrentNode attribute of the JMeterTreeListener object.
      *
      * @return the CurrentNode value
      */
     public ReportTreeNode getCurrentNode() {
         if (currentPath != null) {
             if (currentPath.getLastPathComponent() != null) {
                 return (ReportTreeNode) currentPath.getLastPathComponent();
             } else {
                 return (ReportTreeNode) currentPath.getParentPath().getLastPathComponent();
             }
         } else {
             return (ReportTreeNode) model.getRoot();
         }
     }
 
     public ReportTreeNode[] getSelectedNodes() {
         TreePath[] paths = tree.getSelectionPaths();
         if (paths == null) {
             return new ReportTreeNode[] { getCurrentNode() };
         }
         ReportTreeNode[] nodes = new ReportTreeNode[paths.length];
         for (int i = 0; i < paths.length; i++) {
             nodes[i] = (ReportTreeNode) paths[i].getLastPathComponent();
         }
 
         return nodes;
     }
 
     public TreePath removedSelectedNode() {
         currentPath = currentPath.getParentPath();
         return currentPath;
     }
 
+    @Override
     public void valueChanged(TreeSelectionEvent e) {
         log.debug("value changed, updating currentPath");
         currentPath = e.getNewLeadSelectionPath();
         actionHandler.actionPerformed(new ActionEvent(this, 3333, "edit"));
     }
 
+    @Override
     public void mouseClicked(MouseEvent ev) {
     }
 
+    @Override
     public void mouseReleased(MouseEvent e) {
         if (dragging && isValidDragAction(draggedNodes, getCurrentNode())) {
             dragging = false;
             JPopupMenu dragNdrop = new JPopupMenu();
             JMenuItem item = new JMenuItem(JMeterUtils.getResString("Insert Before"));
             item.addActionListener(actionHandler);
             item.setActionCommand(ReportDragNDrop.INSERT_BEFORE);
             dragNdrop.add(item);
             item = new JMenuItem(JMeterUtils.getResString("Insert After"));
             item.addActionListener(actionHandler);
             item.setActionCommand(ReportDragNDrop.INSERT_AFTER);
             dragNdrop.add(item);
             item = new JMenuItem(JMeterUtils.getResString("Add as Child"));
             item.addActionListener(actionHandler);
             item.setActionCommand(ReportDragNDrop.ADD);
             dragNdrop.add(item);
             dragNdrop.addSeparator();
             item = new JMenuItem(JMeterUtils.getResString("Cancel"));
             dragNdrop.add(item);
             displayPopUp(e, dragNdrop);
         } else {
             ReportGuiPackage.getInstance().getMainFrame().repaint();
         }
         dragging = false;
     }
 
     public ReportTreeNode[] getDraggedNodes() {
         return draggedNodes;
     }
 
     /**
      * Tests if the node is being dragged into one of it's own sub-nodes, or
      * into itself.
      */
     private boolean isValidDragAction(ReportTreeNode[] source, ReportTreeNode dest) {
         boolean isValid = true;
         TreeNode[] path = dest.getPath();
         for (int i = 0; i < path.length; i++) {
             if (contains(source, path[i])) {
                 isValid = false;
             }
         }
         return isValid;
     }
 
+    @Override
     public void mouseEntered(MouseEvent e) {
     }
 
     private void changeSelectionIfDragging(MouseEvent e) {
         if (dragging) {
             ReportGuiPackage.getInstance().getMainFrame().drawDraggedComponent(dragIcon, e.getX(), e.getY());
             if (tree.getPathForLocation(e.getX(), e.getY()) != null) {
                 currentPath = tree.getPathForLocation(e.getX(), e.getY());
                 if (!contains(draggedNodes, getCurrentNode())) {
                     tree.setSelectionPath(currentPath);
                 }
             }
         }
     }
 
     private boolean contains(Object[] container, Object item) {
         for (int i = 0; i < container.length; i++) {
             if (container[i] == item) {
                 return true;
             }
         }
         return false;
     }
 
+    @Override
     public void mousePressed(MouseEvent e) {
         // Get the Main Frame.
         ReportMainFrame mainFrame = ReportGuiPackage.getInstance().getMainFrame();
         // Close any Main Menu that is open
         mainFrame.closeMenu();
         int selRow = tree.getRowForLocation(e.getX(), e.getY());
         if (tree.getPathForLocation(e.getX(), e.getY()) != null) {
             log.debug("mouse pressed, updating currentPath");
             currentPath = tree.getPathForLocation(e.getX(), e.getY());
         }
         if (selRow != -1) {
             // updateMainMenu(((JMeterGUIComponent)
             // getCurrentNode().getUserObject()).createPopupMenu());
             if (isRightClick(e)) {
                 if (tree.getSelectionCount() < 2) {
                     tree.setSelectionPath(currentPath);
                 }
                 if (getCurrentNode() != null) {
                     log.debug("About to display pop-up");
                     displayPopUp(e);
                 }
             }
         }
     }
 
+    @Override
     public void mouseDragged(MouseEvent e) {
         if (!dragging) {
             dragging = true;
             draggedNodes = getSelectedNodes();
             if (draggedNodes[0].getUserObject() instanceof ReportGui) {
                 dragging = false;
             }
 
         }
         changeSelectionIfDragging(e);
     }
 
+    @Override
     public void mouseMoved(MouseEvent e) {
     }
 
+    @Override
     public void mouseExited(MouseEvent ev) {
     }
 
+    @Override
     public void keyPressed(KeyEvent e) {
     }
 
+    @Override
     public void keyReleased(KeyEvent e) {
     }
 
+    @Override
     public void keyTyped(KeyEvent e) {
     }
 
     private boolean isRightClick(MouseEvent e) {
         return e.isPopupTrigger() || (InputEvent.BUTTON2_MASK & e.getModifiers()) > 0 || (InputEvent.BUTTON3_MASK == e.getModifiers());
     }
 
     /*
      * NOTUSED private void updateMainMenu(JPopupMenu menu) { try { MainFrame
      * mainFrame = GuiPackage.getInstance().getMainFrame();
      * mainFrame.setEditMenu(menu); } catch (NullPointerException e) {
      * log.error("Null pointer: JMeterTreeListener.updateMenuItem()", e);
      * log.error("", e); } }
      */
 
     private void displayPopUp(MouseEvent e) {
         JPopupMenu pop = getCurrentNode().createPopupMenu();
         ReportGuiPackage.getInstance().displayPopUp(e, pop);
     }
 
     private void displayPopUp(MouseEvent e, JPopupMenu popup) {
         log.warn("Shouldn't be here");
         if (popup != null) {
             popup.pack();
             popup.show(tree, e.getX(), e.getY());
             popup.setVisible(true);
             popup.requestFocus();
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeNode.java b/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeNode.java
index abf955bac..1e2174d9e 100644
--- a/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeNode.java
+++ b/src/reports/org/apache/jmeter/report/gui/tree/ReportTreeNode.java
@@ -1,161 +1,164 @@
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
 
 package org.apache.jmeter.report.gui.tree;
 
 import java.awt.Image;
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.util.Collection;
 
 import javax.swing.ImageIcon;
 import javax.swing.JPopupMenu;
 import javax.swing.tree.DefaultMutableTreeNode;
 
 import org.apache.jmeter.gui.GUIFactory;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.tree.NamedTreeNode;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportTreeNode extends DefaultMutableTreeNode implements
         NamedTreeNode {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final ReportTreeModel treeModel;
 
     // boolean enabled = true;
 
     public ReportTreeNode() {// Allow serializable test to work
         // TODO: is the serializable test necessary now that JMeterTreeNode is
         // no longer a GUI component?
         this(null, null);
     }
 
     public ReportTreeNode(TestElement userObj, ReportTreeModel treeModel) {
         super(userObj);
         this.treeModel = treeModel;
     }
 
     public boolean isEnabled() {
         return ((AbstractTestElement) getTestElement())
                 .getPropertyAsBoolean(TestElement.ENABLED);
     }
 
     public void setEnabled(boolean enabled) {
         getTestElement().setProperty(
                 new BooleanProperty(TestElement.ENABLED, enabled));
         treeModel.nodeChanged(this);
     }
 
     public ImageIcon getIcon() {
         return getIcon(true);
     }
 
     public ImageIcon getIcon(boolean enabled) {
         try {
             if (getTestElement() instanceof TestBean) {
                 try {
                     Image img = Introspector.getBeanInfo(
                             getTestElement().getClass()).getIcon(
                             BeanInfo.ICON_COLOR_16x16);
                     // If icon has not been defined, then use GUI_CLASS property
                     if (img == null) {//
                         Object clazz = Introspector.getBeanInfo(
                                 getTestElement().getClass())
                                 .getBeanDescriptor().getValue(
                                         TestElement.GUI_CLASS);
                         if (clazz == null) {
                             log.error("Can't obtain GUI class for "
                                     + getTestElement().getClass().getName());
                             return null;
                         }
                         return GUIFactory.getIcon(
                                 Class.forName((String) clazz), enabled);
                     }
                     return new ImageIcon(img);
                 } catch (IntrospectionException e1) {
                     log.error("Can't obtain icon", e1);
                     throw new org.apache.jorphan.util.JMeterError(e1);
                 }
             } else {
                 return GUIFactory.getIcon(Class.forName(getTestElement()
                         .getPropertyAsString(TestElement.GUI_CLASS)), enabled);
             }
         } catch (ClassNotFoundException e) {
             log.warn("Can't get icon for class " + getTestElement(), e);
             return null;
         }
     }
 
     public Collection<String> getMenuCategories() {
         try {
             return ReportGuiPackage.getInstance().getGui(getTestElement())
                     .getMenuCategories();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public JPopupMenu createPopupMenu() {
         try {
             return ReportGuiPackage.getInstance().getGui(getTestElement())
                     .createPopupMenu();
         } catch (Exception e) {
             log.error("Can't get popup menu for gui", e);
             return null;
         }
     }
 
     public TestElement getTestElement() {
         return (TestElement) getUserObject();
     }
 
     public String getStaticLabel() {
         return ReportGuiPackage.getInstance().getGui((TestElement) getUserObject())
                 .getStaticLabel();
     }
 
     public String getDocAnchor() {
         return ReportGuiPackage.getInstance().getGui((TestElement) getUserObject())
                 .getDocAnchor();
     }
 
     /** {@inheritDoc} */
+    @Override
     public void setName(String name) {
         ((TestElement) getUserObject()).setName(name);
     }
 
     /** {@inheritDoc} */
+    @Override
     public String getName() {
         return ((TestElement) getUserObject()).getName();
     }
 
     /** {@inheritDoc} */
+    @Override
     public void nameChanged() {
         treeModel.nodeChanged(this);
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/writers/AbstractReportWriter.java b/src/reports/org/apache/jmeter/report/writers/AbstractReportWriter.java
index bd4af7b69..6ffcb0dfd 100644
--- a/src/reports/org/apache/jmeter/report/writers/AbstractReportWriter.java
+++ b/src/reports/org/apache/jmeter/report/writers/AbstractReportWriter.java
@@ -1,99 +1,102 @@
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
 package org.apache.jmeter.report.writers;
 
 import java.io.File;
 import java.util.Calendar;
 
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * The abstract report writer provides the common implementation for subclasses
  * to reuse.
  */
 public abstract class AbstractReportWriter extends AbstractTestElement implements ReportWriter {
 
     private static final long serialVersionUID = 240L;
     public static final String TARGET_DIRECTORY = "ReportWriter.target.directory";
 
     /**
      *
      */
     public AbstractReportWriter() {
         super();
     }
 
     /**
      * Subclasses need to implement this method and provide the necessary
      * logic to produce a ReportSummary object and write the report
      */
+    @Override
     public abstract ReportSummary writeReport(TestElement element);
 
     /**
      * The method simply returns the target directory and doesn't
      * validate it. the abstract class expects some other class will
      * validate the target directory.
      */
+    @Override
     public String getTargetDirectory() {
         return getPropertyAsString(TARGET_DIRECTORY);
     }
 
     /**
      * Set the target directory where the report should be saved
      */
+    @Override
     public void setTargetDirectory(String directory) {
         setProperty(TARGET_DIRECTORY,directory);
     }
 
     public void makeDirectory() {
         File output = new File(getTargetDirectory());
         if (!output.exists() || !output.isDirectory()) {
             if(!output.mkdir()) {
             	throw new IllegalStateException("Could not create directory:"+output.getAbsolutePath());
             }
         }
     }
 
     /**
      * if the target output directory already exists, archive it
      */
     public void archiveDirectory() {
         File output = new File(getTargetDirectory());
         if (output.exists() && output.isDirectory()) {
             // if the directory already exists and is a directory,
             // we just renamed to "archive.date"
             if(!output.renameTo(new File("archive." + getDayString()))) {
             	throw new IllegalStateException("Could not rename directory:"+output.getAbsolutePath()+
             			" to archive." + getDayString());
             }
         }
     }
 
     /**
      * return the day in YYYYMMDD format
      * @return the date
      */
     public String getDayString() {
         Calendar today = Calendar.getInstance();
         String year = String.valueOf(today.get(Calendar.YEAR));
         String month = String.valueOf(today.get(Calendar.MONTH));
         String day = String.valueOf(today.get(Calendar.DATE));
         return year + month + day;
     }
 }
diff --git a/src/reports/org/apache/jmeter/report/writers/DefaultPageSummary.java b/src/reports/org/apache/jmeter/report/writers/DefaultPageSummary.java
index 1f99cce7e..b4b564220 100644
--- a/src/reports/org/apache/jmeter/report/writers/DefaultPageSummary.java
+++ b/src/reports/org/apache/jmeter/report/writers/DefaultPageSummary.java
@@ -1,109 +1,120 @@
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
 package org.apache.jmeter.report.writers;
 
 /**
  * This is a basic implementation of PageSummary interface.
  */
 public class DefaultPageSummary implements PageSummary {
 
     private long START = 0;
     private long END = 0;
     private String title;
     private String fileName;
     private boolean success;
 
     /**
      *
      */
     public DefaultPageSummary() {
         super();
     }
 
     /**
      * Returns the elapsed time in milliseconds
      */
+    @Override
     public long getElapsedTime() {
         return END - START;
     }
 
+    @Override
     public long getEndTimeStamp() {
         return END;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#getFileName()
      */
+    @Override
     public String getFileName() {
         return fileName;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#getPageTitle()
      */
+    @Override
     public String getPageTitle() {
         return title;
     }
 
+    @Override
     public long getStartTimeStamp() {
         return START;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#isSuccessful()
      */
+    @Override
     public boolean isSuccessful() {
         return success;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#pageStarted()
      */
+    @Override
     public void pageStarted() {
         START = System.currentTimeMillis();
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#pageEnded()
      */
+    @Override
     public void pageEnded() {
         END = System.currentTimeMillis();
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#setFileName(java.lang.String)
      */
+    @Override
     public void setFileName(String file) {
         this.fileName = file;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#setPageTitle(java.lang.String)
      */
+    @Override
     public void setPageTitle(String title) {
         this.title = title;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.report.writers.PageSummary#setSuccessful(boolean)
      */
+    @Override
     public void setSuccessful(boolean success) {
         this.success = success;
     }
 
 }
diff --git a/src/reports/org/apache/jmeter/report/writers/DefaultReportSummary.java b/src/reports/org/apache/jmeter/report/writers/DefaultReportSummary.java
index 97248ea0c..3b0537f26 100644
--- a/src/reports/org/apache/jmeter/report/writers/DefaultReportSummary.java
+++ b/src/reports/org/apache/jmeter/report/writers/DefaultReportSummary.java
@@ -1,75 +1,79 @@
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
 package org.apache.jmeter.report.writers;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 
 /**
  * The default implementation of ReportSummary just contains the stats
  * and basic information. It doesn't contain the actual report. In the
  * future we may want to implement a version with all the details to
  * display in a Swing GUI.
  */
 public class DefaultReportSummary implements ReportSummary {
 
     private final ArrayList<PageSummary> pages = new ArrayList<PageSummary>();
 
     /**
      *
      */
     public DefaultReportSummary() {
         super();
     }
 
     /**
      * Add a PageSummary to the report
      */
+    @Override
     public void addPageSummary(PageSummary summary) {
         this.pages.add(summary);
     }
 
     /**
      * current implementation simply iterates over the Page summaries
      * and adds the times.
      */
+    @Override
     public long getElapsedTime() {
         long elpasedTime = 0;
         Iterator<PageSummary> itr = this.pages.iterator();
         while (itr.hasNext()) {
             elpasedTime += itr.next().getElapsedTime();
         }
         return elpasedTime;
     }
 
     /**
      * The current implementation calls ArrayList.toArray(Object[])
      */
+    @Override
     public PageSummary[] getPagesSummaries() {
         PageSummary[] ps = new PageSummary[this.pages.size()];
         return this.pages.toArray(ps);
     }
 
     /**
      * remove a PageSummary
      */
+    @Override
     public void removePageSummary(PageSummary summary) {
         this.pages.remove(summary);
     }
 
 }
diff --git a/src/reports/org/apache/jmeter/report/writers/gui/HTMLReportWriterGui.java b/src/reports/org/apache/jmeter/report/writers/gui/HTMLReportWriterGui.java
index c61863ca4..d33954612 100644
--- a/src/reports/org/apache/jmeter/report/writers/gui/HTMLReportWriterGui.java
+++ b/src/reports/org/apache/jmeter/report/writers/gui/HTMLReportWriterGui.java
@@ -1,101 +1,103 @@
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
 package org.apache.jmeter.report.writers.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 
 import javax.swing.JPanel;
 import javax.swing.JPopupMenu;
 
 import org.apache.jmeter.gui.util.ReportFilePanel;
 import org.apache.jmeter.gui.util.ReportMenuFactory;
 import org.apache.jmeter.report.gui.AbstractReportGui;
 import org.apache.jmeter.report.writers.HTMLReportWriter;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 
 public class HTMLReportWriterGui extends AbstractReportGui {
 
     private static final long serialVersionUID = 240L;
 
     private ReportFilePanel outputDirectory = new ReportFilePanel(
             JMeterUtils.getResString("report_output_directory"), "*");
 
     public HTMLReportWriterGui() {
         super();
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "report_writer_html";
     }
 
     @Override
     public JPopupMenu createPopupMenu() {
         JPopupMenu pop = new JPopupMenu();
         ReportMenuFactory.addFileMenu(pop);
         ReportMenuFactory.addEditMenu(pop,true);
         return pop;
     }
 
     /**
      * init creates the necessary gui stuff.
      */
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(10, 10));
         setBorder(makeBorder());
         setBackground(Color.white);
 
         JPanel pane = new JPanel();
         pane.setLayout(new BorderLayout(10,10));
         pane.setBackground(Color.white);
         pane.add(this.getNamePanel(),BorderLayout.NORTH);
 
         outputDirectory.setBackground(Color.white);
 
         pane.add(outputDirectory,BorderLayout.SOUTH);
         add(pane,BorderLayout.NORTH);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
+    @Override
     public TestElement createTestElement() {
         HTMLReportWriter element = new HTMLReportWriter();
         modifyTestElement(element);
         return element;
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(org.apache.jmeter.testelement.TestElement)
      */
+    @Override
     public void modifyTestElement(TestElement element) {
         this.configureTestElement(element);
         HTMLReportWriter wr = (HTMLReportWriter)element;
         wr.setTargetDirectory(outputDirectory.getFilename());
     }
 
     @Override
     public void configure(TestElement element) {
         super.configure(element);
         HTMLReportWriter wr = (HTMLReportWriter)element;
         outputDirectory.setFilename(wr.getTargetDirectory());
     }
 }
diff --git a/src/reports/org/apache/jmeter/testelement/AbstractChart.java b/src/reports/org/apache/jmeter/testelement/AbstractChart.java
index 415493881..df9b72af2 100644
--- a/src/reports/org/apache/jmeter/testelement/AbstractChart.java
+++ b/src/reports/org/apache/jmeter/testelement/AbstractChart.java
@@ -1,242 +1,243 @@
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
 package org.apache.jmeter.testelement;
 
 import java.awt.image.BufferedImage;
 import java.util.List;
 
 import javax.swing.JComponent;
 
 import org.apache.jmeter.report.DataSet;
 import org.apache.jmeter.report.ReportChart;
 import org.apache.jmeter.visualizers.SamplingStatCalculator;
 import org.apache.jorphan.util.JOrphanUtils;
 
 /**
  * The general idea of the chart graphs information for a table.
  * A chart can only be generated from a specific table, though more
  * than one chart can be generated from a single table.
   *
  */
 public abstract class AbstractChart extends AbstractTestElement implements ReportChart {
 
     private static final long serialVersionUID = 240L;
 
     public static final String REPORT_CHART_X_AXIS = "ReportChart.chart.x.axis";
     public static final String REPORT_CHART_Y_AXIS = "ReportChart.chart.y.axis";
     public static final String REPORT_CHART_X_LABEL = "ReportChart.chart.x.label";
     public static final String REPORT_CHART_Y_LABEL = "ReportChart.chart.y.label";
     public static final String REPORT_CHART_TITLE = "ReportChart.chart.title";
     public static final String REPORT_CHART_CAPTION = "ReportChart.chart.caption";
     public static final String REPORT_CHART_WIDTH = "ReportChart.chart.width";
     public static final String REPORT_CHART_HEIGHT = "ReportChart.chart.height";
 
     public static final int DEFAULT_WIDTH = 350;
     public static final int DEFAULT_HEIGHT = 350;
 
     public static final String X_DATA_FILENAME_LABEL = "Filename";
     public static final String X_DATA_DATE_LABEL = "Date";
     public static final String[] X_LABELS = { X_DATA_FILENAME_LABEL, X_DATA_DATE_LABEL };
     protected BufferedImage image = null;
 
     public AbstractChart() {
         super();
     }
 
     public String getXAxis() {
         return getPropertyAsString(REPORT_CHART_X_AXIS);
     }
 
     public String getFormattedXAxis() {
         String text = getXAxis();
         if (text.indexOf('.') > -1) {
             text = text.substring(text.indexOf('.') + 1);
             text = JOrphanUtils.replaceAllChars(text,'_'," ");
         }
         return text;
     }
     public void setXAxis(String field) {
         setProperty(REPORT_CHART_X_AXIS,field);
     }
 
     public String getYAxis() {
         return getPropertyAsString(REPORT_CHART_Y_AXIS);
     }
 
     public void setYAxis(String scale) {
         setProperty(REPORT_CHART_Y_AXIS,scale);
     }
 
     public String getXLabel() {
         return getPropertyAsString(REPORT_CHART_X_LABEL);
     }
 
     /**
      * The X data labels should be either the filename, date or some
      * other series of values
      * @param label
      */
     public void setXLabel(String label) {
         setProperty(REPORT_CHART_X_LABEL,label);
     }
 
     public String getYLabel() {
         return getPropertyAsString(REPORT_CHART_Y_LABEL);
     }
 
     public void setYLabel(String label) {
         setProperty(REPORT_CHART_Y_LABEL,label);
     }
 
     /**
      * The title is a the name for the chart. A page link will
      * be generated using the title. The title will also be
      * used for a page index.
      * @return chart title
      */
     public String getTitle() {
         return getPropertyAsString(REPORT_CHART_TITLE);
     }
 
     /**
      * The title is a the name for the chart. A page link will
      * be generated using the title. The title will also be
      * used for a page index.
      * @param title
      */
     public void setTitle(String title) {
         setProperty(REPORT_CHART_TITLE,title);
     }
 
     /**
      * The caption is a description for the chart explaining
      * what the chart means.
      * @return caption
      */
     public String getCaption() {
         return getPropertyAsString(REPORT_CHART_CAPTION);
     }
 
     /**
      * The caption is a description for the chart explaining
      * what the chart means.
      * @param caption
      */
     public void setCaption(String caption) {
         setProperty(REPORT_CHART_CAPTION,caption);
     }
 
     /**
      * if no width is set, the default is returned
      * @return width
      */
     public int getWidth() {
         int w = getPropertyAsInt(REPORT_CHART_WIDTH);
         if (w <= 0) {
             return DEFAULT_WIDTH;
         } else {
             return w;
         }
     }
 
     /**
      * set the width of the graph
      * @param width
      */
     public void setWidth(String width) {
         setProperty(REPORT_CHART_WIDTH,String.valueOf(width));
     }
 
     /**
      * if the height is not set, the default is returned
      * @return height
      */
     public int getHeight() {
         int h = getPropertyAsInt(REPORT_CHART_HEIGHT);
         if (h <= 0) {
             return DEFAULT_HEIGHT;
         } else {
             return h;
         }
     }
 
     /**
      * set the height of the graph
      * @param height
      */
     public void setHeight(String height) {
         setProperty(REPORT_CHART_HEIGHT,String.valueOf(height));
     }
 
     /**
      * Subclasses will need to implement the method by doing the following:
      * 1. get the x and y axis
      * 2. filter the table data
      * 3. pass the data to the chart library
      * 4. return the generated chart
      */
+    @Override
     public abstract JComponent renderChart(List<DataSet> data);
 
     /**
      * this makes it easy to get the bufferedImage
      * @return image
      */
     public BufferedImage getBufferedImage() {
         return this.image;
     }
 
     /**
      * in case an user wants set the bufferdImage
      * @param img
      */
     public void setBufferedImage(BufferedImage img) {
         this.image = img;
     }
 
     /**
      * convienance method for getting the selected value. Rather than use
      * Method.invoke(Object,Object[]), it's simpler to just check which
      * column is selected and call the method directly.
      * @param stat
      * @return value
      */
     public double getValue(SamplingStatCalculator stat) {
         if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_50_PERCENT)) {
             return stat.getPercentPoint(.50).doubleValue();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_90_PERCENT)){
             return stat.getPercentPoint(.90).doubleValue();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_ERROR_RATE)) {
             return stat.getErrorPercentage();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_MAX)) {
             return stat.getMax().doubleValue();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_MEAN)) {
             return stat.getMean();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_MEDIAN)) {
             return stat.getMedian().doubleValue();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_MIN)) {
             return stat.getMin().doubleValue();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_RESPONSE_RATE)) {
             return stat.getRate();
         } else if (this.getXAxis().equals(AbstractTable.REPORT_TABLE_TRANSFER_RATE)) {
             // return the pagesize divided by 1024 to get kilobytes
             return stat.getKBPerSecond();
         } else {
             return Double.NaN;
         }
     }
 }
diff --git a/src/reports/org/apache/jmeter/testelement/AbstractTable.java b/src/reports/org/apache/jmeter/testelement/AbstractTable.java
index 93e4ab2ac..3f3bf539e 100644
--- a/src/reports/org/apache/jmeter/testelement/AbstractTable.java
+++ b/src/reports/org/apache/jmeter/testelement/AbstractTable.java
@@ -1,150 +1,151 @@
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
 package org.apache.jmeter.testelement;
 
 import java.util.List;
 
 import org.apache.jmeter.report.ReportTable;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * AbstractTable is the base Element for different kinds of report tables.
  *
  */
 public abstract class AbstractTable extends AbstractTestElement
     implements ReportTable
 {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String REPORT_TABLE_MEAN = "ReportTable.Mean";
     public static final String REPORT_TABLE_MEDIAN = "ReportTable.Median";
     public static final String REPORT_TABLE_MAX = "ReportTable.Max";
     public static final String REPORT_TABLE_MIN = "ReportTable.Min";
     public static final String REPORT_TABLE_RESPONSE_RATE = "ReportTable.Response_rate";
     public static final String REPORT_TABLE_TRANSFER_RATE = "ReportTable.Transfer_rate";
     public static final String REPORT_TABLE_50_PERCENT = "ReportTable.50_percent";
     public static final String REPORT_TABLE_90_PERCENT = "ReportTable.90_percent";
     public static final String REPORT_TABLE_ERROR_RATE = "ReportTable.Error.rate";
     public static final String[] items = {
         REPORT_TABLE_MEAN, REPORT_TABLE_MEDIAN, REPORT_TABLE_MAX, REPORT_TABLE_MIN,
         REPORT_TABLE_RESPONSE_RATE, REPORT_TABLE_TRANSFER_RATE, REPORT_TABLE_50_PERCENT,
         REPORT_TABLE_90_PERCENT, REPORT_TABLE_ERROR_RATE };
 
     public static final String REPORT_TABLE_TOTAL = "ReportTable.total";
     public static final String REPORT_TABLE_URL = "ReportTable.url";
 
     public static final String[] xitems = { REPORT_TABLE_TOTAL,
         REPORT_TABLE_URL };
 
     public AbstractTable() {
         super();
     }
 
     public boolean getMean() {
         return getPropertyAsBoolean(REPORT_TABLE_MEAN);
     }
 
     public void setMean(String set) {
         setProperty(REPORT_TABLE_MEAN,set);
     }
 
     public boolean getMedian() {
         return getPropertyAsBoolean(REPORT_TABLE_MEDIAN);
     }
 
     public void setMedian(String set) {
         setProperty(REPORT_TABLE_MEDIAN,set);
     }
 
     public boolean getMax() {
         return getPropertyAsBoolean(REPORT_TABLE_MAX);
     }
 
     public void setMax(String set) {
         setProperty(REPORT_TABLE_MAX,set);
     }
 
     public boolean getMin() {
         return getPropertyAsBoolean(REPORT_TABLE_MIN);
     }
 
     public void setMin(String set) {
         setProperty(REPORT_TABLE_MIN,set);
     }
 
     public boolean getResponseRate() {
         return getPropertyAsBoolean(REPORT_TABLE_RESPONSE_RATE);
     }
 
     public void setResponseRate(String set) {
         setProperty(REPORT_TABLE_RESPONSE_RATE,set);
     }
 
     public boolean getTransferRate() {
         return getPropertyAsBoolean(REPORT_TABLE_TRANSFER_RATE);
     }
 
     public void setTransferRate(String set) {
         setProperty(REPORT_TABLE_TRANSFER_RATE,set);
     }
 
     public boolean get50Percent() {
         return getPropertyAsBoolean(REPORT_TABLE_50_PERCENT);
     }
 
     public void set50Percent(String set) {
         setProperty(REPORT_TABLE_50_PERCENT,set);
     }
 
     public boolean get90Percent() {
         return getPropertyAsBoolean(REPORT_TABLE_90_PERCENT);
     }
 
     public void set90Percent(String set) {
         setProperty(REPORT_TABLE_90_PERCENT,set);
     }
 
     public boolean getErrorRate() {
         return getPropertyAsBoolean(REPORT_TABLE_ERROR_RATE);
     }
 
     public void setErrorRate(String set) {
         setProperty(REPORT_TABLE_ERROR_RATE,set);
     }
 
     @Override
     public void addTestElement(TestElement el) {
         if (el != null) {
             super.addTestElement(el);
             log.info("TestElement: " + el.getClass().getName());
         }
     }
 
     /**
      * method isn't implemented and is left abstract. Subclasses
      * need to filter the data in the list and return statistics.
      * The statistics should be like the aggregate listener.
      */
     @SuppressWarnings("rawtypes") // TODO fix this when there is a real implementation
+    @Override
     public abstract String[][] getTableData(List data);
 
 }
diff --git a/src/reports/org/apache/jmeter/testelement/JTLData.java b/src/reports/org/apache/jmeter/testelement/JTLData.java
index 394a0189f..32a3b4bd0 100644
--- a/src/reports/org/apache/jmeter/testelement/JTLData.java
+++ b/src/reports/org/apache/jmeter/testelement/JTLData.java
@@ -1,218 +1,235 @@
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
 package org.apache.jmeter.testelement;
 
 import java.io.File;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.apache.jmeter.report.DataSet;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.visualizers.SamplingStatCalculator;
 
 /**
  *
  * The purpose of TableData is to contain the results of a single .jtl file.
  * It is equivalent to what the AggregateListener table. A HashMap is used
  * to store the data. The URL is the key and the value is SamplingStatCalculator
  */
 public class JTLData implements Serializable, DataSet {
 
     private static final long serialVersionUID = 240L;
 
     private final HashMap<String, SamplingStatCalculator> data = new HashMap<String, SamplingStatCalculator>();
     private String jtl_file = null;
     private long startTimestamp = 0;
     private long endTimestamp = 0;
     private transient File inputFile = null;
 
     /**
      *
      */
     public JTLData() {
         super();
     }
 
     /**
      * Return a Set of the URLs
      * @return set of URLs
      */
+    @Override
     public Set<?> getURLs() {
         return this.data.keySet();
     }
 
     /**
      * Return a Set of the values
      * @return values
      */
+    @Override
     public Set<SamplingStatCalculator>  getStats() {
         return (Set<SamplingStatCalculator>) this.data.values();
     }
 
     /**
      * The purpose of the method is to make it convienant to pass a list
      * of the URLs and return a list of the SamplingStatCalculators. If
      * no URLs match, the list is empty.
      * TODO - this method seems to be wrong - it does not agree with the Javadoc
      * The SamplingStatCalculators will be returned in the same sequence
      * as the url list.
      * @param urls
      * @return array list of non-null entries (may be empty)
      */
     @SuppressWarnings({ "rawtypes", "unchecked" }) // Method is broken anyway
+    @Override
     public List getStats(List urls) {
         ArrayList items = new ArrayList();
         Iterator itr = urls.iterator();
         if (itr.hasNext()) {
             SamplingStatCalculator row = (SamplingStatCalculator)itr.next();
             if (row != null) {
                 items.add(row);
             }
         }
         return items;
     }
 
+    @Override
     public void setDataSource(String absolutePath) {
         this.jtl_file = absolutePath;
     }
 
+    @Override
     public String getDataSource() {
         return this.jtl_file;
     }
 
+    @Override
     public String getDataSourceName() {
         if (inputFile == null) {
             inputFile = new File(getDataSource());
         }
         return inputFile.getName().substring(0,inputFile.getName().length() - 4);
     }
 
+    @Override
     public void setStartTimestamp(long stamp) {
         this.startTimestamp = stamp;
     }
 
+    @Override
     public long getStartTimestamp() {
         return this.startTimestamp;
     }
 
+    @Override
     public void setEndTimestamp(long stamp) {
         this.endTimestamp = stamp;
     }
 
+    @Override
     public long getEndTimestamp() {
         return this.endTimestamp;
     }
 
     /**
      * The date we use for the result is the start timestamp. The
      * reasoning is that a test may run for a long time, but it
      * is most likely scheduled to run using CRON on unix or
      * scheduled task in windows.
      * @return start time
      */
+    @Override
     public Date getDate() {
         return new Date(this.startTimestamp);
     }
 
+    @Override
     public String getMonthDayDate() {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(this.startTimestamp);
         return String.valueOf(cal.get(Calendar.MONTH)) + " - " +
         String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
     }
 
+    @Override
     public String getMonthDayYearDate() {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(this.startTimestamp);
         return String.valueOf(cal.get(Calendar.MONTH)) + " - " +
             String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + " - " +
             String.valueOf(cal.get(Calendar.YEAR));
     }
 
     /**
      * The method will SamplingStatCalculator for the given URL. If the URL
      * doesn't exist, the method returns null.
      * @param url
      * @return data for this URL
      */
+    @Override
     public SamplingStatCalculator getStatistics(String url) {
         if (this.data.containsKey(url)) {
             return this.data.get(url);
         } else {
             return null;
         }
     }
 
     /**
      * The implementation loads a single .jtl file and cleans up the
      * ResultCollector.
      */
+    @Override
     public void loadData() {
         if (this.getDataSource() != null) {
             ResultCollector rc = new ResultCollector();
             rc.setFilename(this.getDataSource());
             rc.setListener(this);
             rc.loadExistingFile();
             // we clean up the ResultCollector to make sure there's
             // no slow leaks
             rc.clear();
             rc.setListener(null);
         }
     }
 
     /**
      * the implementation will set the start timestamp if the HashMap
      * is empty. otherwise it will set the end timestamp using the
      * end time
      */
+    @Override
     public void add(SampleResult sample) {
         if (data.size() == 0) {
             this.startTimestamp = sample.getStartTime();
         } else {
             this.endTimestamp = sample.getEndTime();
         }
         // now add the samples to the HashMap
         String url = sample.getSampleLabel();
         if (url == null) {
             url = sample.getURL().toString();
         }
         SamplingStatCalculator row = data.get(url);
         if (row == null) {
             row = new SamplingStatCalculator(url);
             // just like the aggregate listener, we use the sample label to represent
             // a row. in this case, we use it as a key.
             this.data.put(url,row);
         }
         row.addSample(sample);
     }
 
     /**
      * By default, the method always returns true. Subclasses can over
      * ride the implementation.
      */
+    @Override
     public boolean isStats() {
         return true;
     }
 }
diff --git a/src/reports/org/apache/jmeter/testelement/ReportPlan.java b/src/reports/org/apache/jmeter/testelement/ReportPlan.java
index 6fa9149dc..791aad1fb 100644
--- a/src/reports/org/apache/jmeter/testelement/ReportPlan.java
+++ b/src/reports/org/apache/jmeter/testelement/ReportPlan.java
@@ -1,216 +1,220 @@
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
 
 package org.apache.jmeter.testelement;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class ReportPlan extends AbstractTestElement implements Serializable, TestStateListener {
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String REPORT_PAGE = "ReportPlan.report_page";
 
     public static final String USER_DEFINED_VARIABLES = "ReportPlan.user_defined_variables";
 
     public static final String REPORT_COMMENTS = "ReportPlan.comments";
 
     public static final String BASEDIR = "ReportPlan.basedir";
 
     private transient List<AbstractThreadGroup> reportPages = new LinkedList<AbstractThreadGroup>();
 
     private transient List<ConfigElement> configs = new LinkedList<ConfigElement>();
 
     private static final List<String> itemsCanAdd = new LinkedList<String>();
 
     //private static ReportPlan plan;
 
     // There's only 1 test plan, so can cache the mode here
     private static volatile boolean functionalMode = false;
 
     static {
         itemsCanAdd.add(JMeterUtils.getResString("report_page"));
     }
 
     public ReportPlan() {
         this(JMeterUtils.getResString("report_plan"));
     }
 
     public ReportPlan(String name) {
         setName(name);
         setProperty(new CollectionProperty(REPORT_PAGE, reportPages));
     }
 
     public void setUserDefinedVariables(Arguments vars) {
         setProperty(new TestElementProperty(USER_DEFINED_VARIABLES, vars));
     }
 
     public String getBasedir() {
         return getPropertyAsString(BASEDIR);
     }
 
     public void setBasedir(String b) {
         setProperty(BASEDIR, b);
     }
 
     public Map<String, String> getUserDefinedVariables() {
         Arguments args = getVariables();
         return args.getArgumentsAsMap();
     }
 
     private Arguments getVariables() {
         Arguments args = (Arguments) getProperty(USER_DEFINED_VARIABLES).getObjectValue();
         if (args == null) {
             args = new Arguments();
             setUserDefinedVariables(args);
         }
         return args;
     }
 
     /**
      * Gets the static copy of the functional mode
      *
      * @return mode
      */
     public static boolean getFunctionalMode() {
         return functionalMode;
     }
 
     public void addParameter(String name, String value) {
         getVariables().addArgument(name, value);
     }
 
     // FIXME Wrong code that create different constructor for static field depending on caller
 //    public static ReportPlan createReportPlan(String name) {
 //        if (plan == null) {
 //            if (name == null) {
 //                plan = new ReportPlan();
 //            } else {
 //                plan = new ReportPlan(name);
 //            }
 //            plan.setProperty(new StringProperty(TestElement.GUI_CLASS, "org.apache.jmeter.control.gui.ReportGui"));
 //        }
 //        return plan;
 //    }
 
     @Override
     public void addTestElement(TestElement tg) {
         super.addTestElement(tg);
         if (tg instanceof AbstractThreadGroup && !isRunningVersion()) {
             addReportPage((AbstractThreadGroup) tg);
         }
     }
 
     public void addJMeterComponent(TestElement child) {
         if (child instanceof AbstractThreadGroup) {
             addReportPage((AbstractThreadGroup) child);
         }
     }
 
     /**
      * Gets the ThreadGroups attribute of the TestPlan object.
      *
      * @return the ThreadGroups value
      */
     public Collection<AbstractThreadGroup> getReportPages() {
         return reportPages;
     }
 
     /**
      * Adds a feature to the ConfigElement attribute of the TestPlan object.
      *
      * @param c
      *            the feature to be added to the ConfigElement attribute
      */
     public void addConfigElement(ConfigElement c) {
         configs.add(c);
     }
 
     /**
      * Adds a feature to the AbstractThreadGroup attribute of the TestPlan object.
      *
      * @param group
      *            the feature to be added to the AbstractThreadGroup attribute
      */
     public void addReportPage(AbstractThreadGroup group) {
         reportPages.add(group);
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded()
      */
+    @Override
     public void testEnded() {
         try {
             FileServer.getFileServer().closeFiles();
         } catch (IOException e) {
             log.error("Problem closing files at end of test", e);
         }
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded(java.lang.String)
      */
+    @Override
     public void testEnded(String host) {
         testEnded();
 
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted()
      */
+    @Override
     public void testStarted() {
         if (getBasedir() != null && getBasedir().length() > 0) {
             try {
                 FileServer.getFileServer().setBasedir(FileServer.getFileServer().getBaseDir() + getBasedir());
             } catch (IllegalStateException e) {
                 log.error("Failed to set file server base dir with " + getBasedir(), e);
             }
         }
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted(java.lang.String)
      */
+    @Override
     public void testStarted(String host) {
         testStarted();
     }
 }
