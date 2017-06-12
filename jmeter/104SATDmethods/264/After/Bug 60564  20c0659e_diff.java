diff --git a/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java b/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java
index f79ce95e0..8fc68d06c 100644
--- a/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java
+++ b/src/protocol/native/org/apache/jmeter/protocol/system/SystemSampler.java
@@ -1,332 +1,332 @@
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
 
 package org.apache.jmeter.protocol.system;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.exec.SystemCommand;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * A sampler for executing a System function. 
  */
 public class SystemSampler extends AbstractSampler {
 
     private static final int POLL_INTERVAL = JMeterUtils.getPropDefault("os_sampler.poll_for_timeout", SystemCommand.POLL_INTERVAL);
 
     private static final long serialVersionUID = 1;
     
     // + JMX names, do not change their values
     public static final String COMMAND = "SystemSampler.command";
     
     public static final String DIRECTORY = "SystemSampler.directory";
 
     public static final String ARGUMENTS = "SystemSampler.arguments";
     
     public static final String ENVIRONMENT = "SystemSampler.environment";
 
     public static final String CHECK_RETURN_CODE = "SystemSampler.checkReturnCode";
     
     public static final String EXPECTED_RETURN_CODE = "SystemSampler.expectedReturnCode";
     
     private static final String STDOUT = "SystemSampler.stdout";
 
     private static final String STDERR = "SystemSampler.stderr";
 
     private static final String STDIN = "SystemSampler.stdin";
 
     private static final String TIMEOUT = "SystemSampler.timeout";
 
     // - JMX names
 
     /**
      * Logging
      */
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SystemSampler.class);
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));
 
     public static final int DEFAULT_RETURN_CODE = 0;
 
 
     /**
      * Create a SystemSampler.
      */
     public SystemSampler() {
         super();
     }
     
     /**
      * Performs a test sample.
      * 
      * @param entry
      *            the Entry for this sample
      * @return test SampleResult
      */
     @Override
     public SampleResult sample(Entry entry) {
         SampleResult results = new SampleResult();
         results.setDataType(SampleResult.TEXT);
         results.setSampleLabel(getName());
         
         String command = getCommand();
         Arguments args = getArguments();
         Arguments environment = getEnvironmentVariables();
         boolean checkReturnCode = getCheckReturnCode();
         int expectedReturnCode = getExpectedReturnCode();
         List<String> cmds = new ArrayList<>(args.getArgumentCount() + 1);
         StringBuilder cmdLine = new StringBuilder((null == command) ? "" : command);
         cmds.add(command);
         for (int i=0;i<args.getArgumentCount();i++) {
             Argument arg = args.getArgument(i);
             cmds.add(arg.getPropertyAsString(Argument.VALUE));
             cmdLine.append(" ");
             cmdLine.append(cmds.get(i+1));
         }
 
         Map<String,String> env = new HashMap<>();
         for (int i=0;i<environment.getArgumentCount();i++) {
             Argument arg = environment.getArgument(i);
             env.put(arg.getName(), arg.getPropertyAsString(Argument.VALUE));
         }
         
         File directory;
         if(StringUtils.isEmpty(getDirectory())) {
             directory = new File(FileServer.getDefaultBase());
             if(log.isDebugEnabled()) {
                 log.debug("Using default directory:"+directory.getAbsolutePath());
             }
         } else {
             directory = new File(getDirectory());
             if(log.isDebugEnabled()) {
                 log.debug("Using configured directory:"+directory.getAbsolutePath());
             }
         }
         
         if(log.isDebugEnabled()) {
             log.debug("Will run : "+cmdLine + " using working directory:"+directory.getAbsolutePath()+
                     " with environment: "+env);
         }
 
         results.setSamplerData("Working Directory: "+directory.getAbsolutePath()+
                 "\nEnvironment: "+env+
                 "\nExecuting: " + cmdLine.toString());
 
         SystemCommand nativeCommand = null;
         try {
             nativeCommand = new SystemCommand(directory, getTimeout(), POLL_INTERVAL, env, getStdin(), getStdout(), getStderr());
             results.sampleStart();
             int returnCode = nativeCommand.run(cmds);
             results.sampleEnd();
             results.setResponseCode(Integer.toString(returnCode));
             if(log.isDebugEnabled()) {
                 log.debug("Ran : "+cmdLine + " using working directory: "+directory.getAbsolutePath()+
                         " with execution environment: "+nativeCommand.getExecutionEnvironment()+ " => " + returnCode);
             }
 
             if (checkReturnCode && (returnCode != expectedReturnCode)) {
                 results.setSuccessful(false);
                 results.setResponseMessage("Unexpected return code.  Expected ["+expectedReturnCode+"]. Actual ["+returnCode+"].");
             } else {
                 results.setSuccessful(true);
                 results.setResponseMessage("OK");
             }
         } catch (IOException ioe) {
             results.sampleEnd();
             results.setSuccessful(false);
             results.setResponseCode("500"); //$NON-NLS-1$
             results.setResponseMessage("Exception occurred whilst executing system call: " + ioe);
         } catch (InterruptedException ie) {
             results.sampleEnd();
             results.setSuccessful(false);
             results.setResponseCode("500"); //$NON-NLS-1$
             results.setResponseMessage("System Sampler interrupted whilst executing system call: " + ie);
             Thread.currentThread().interrupt();
         }
 
         if (nativeCommand != null) {
             results.setResponseData(nativeCommand.getOutResult().getBytes()); // default charset is deliberate here
         }
             
         return results;
     }
     
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 
     /**
      * @return working directory to use for system commands
      */
     public String getDirectory() {
         return getPropertyAsString(DIRECTORY, FileServer.getDefaultBase());
     }
 
     /**
      * Set the working directory to use for system commands
      * 
      * @param directory
      *            working directory to use for system commands
      */
     public void setDirectory(String directory) {
         setProperty(DIRECTORY, directory, FileServer.getDefaultBase());
     }
 
     /**
      * Sets the Command attribute of the JavaConfig object
      * 
      * @param command
      *            the new Command value
      */
     public void setCommand(String command) {
         setProperty(COMMAND, command);
     }
 
     /**
      * Gets the Command attribute of the JavaConfig object
      * 
      * @return the Command value
      */
     public String getCommand() {
         return getPropertyAsString(COMMAND);
     }
     
     /**
      * Set the arguments (parameters) for the JavaSamplerClient to be executed
      * with.
      * 
      * @param args
      *            the new arguments. These replace any existing arguments.
      */
     public void setArguments(Arguments args) {
         setProperty(new TestElementProperty(ARGUMENTS, args));
     }
 
     /**
      * Get the arguments (parameters) for the JavaSamplerClient to be executed
      * with.
      * 
      * @return the arguments
      */
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
     
     /**
      * @param checkit boolean indicates if we check or not return code
      */
     public void setCheckReturnCode(boolean checkit) {
         setProperty(CHECK_RETURN_CODE, checkit);
     }
     
     /**
      * @return boolean indicating if we check or not return code
      */
     public boolean getCheckReturnCode() {
         return getPropertyAsBoolean(CHECK_RETURN_CODE);
     }
     
     /**
      * @param code expected return code
      */
     public void setExpectedReturnCode(int code) {
         setProperty(EXPECTED_RETURN_CODE, Integer.toString(code));
     }
     
     /**
      * @return expected return code
      */
     public int getExpectedReturnCode() {
         return getPropertyAsInt(EXPECTED_RETURN_CODE);
     }
 
     /**
      * @param arguments Env vars
      */
     public void setEnvironmentVariables(Arguments arguments) {
         setProperty(new TestElementProperty(ENVIRONMENT, arguments));
     }
     
     /**
      * Get the env variables
      * 
      * @return the arguments
      */
     public Arguments getEnvironmentVariables() {
         return (Arguments) getProperty(ENVIRONMENT).getObjectValue();
     }
 
     public String getStdout() {
         return getPropertyAsString(STDOUT, "");
     }
 
     public void setStdout(String filename) {
         setProperty(STDOUT, filename, "");
     }
 
     public String getStderr() {
         return getPropertyAsString(STDERR, "");
     }
 
     public void setStderr(String filename) {
         setProperty(STDERR, filename, "");
     }
 
     public String getStdin() {
         return getPropertyAsString(STDIN, "");
     }
 
     public void setStdin(String filename) {
         setProperty(STDIN, filename, "");
     }
 
     public long getTimeout() {
         return getPropertyAsLong(TIMEOUT, 0L);
     }
 
     public void setTimout(long timeoutMs) {
         setProperty(TIMEOUT, timeoutMs, 0L);
     }
 }
diff --git a/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java b/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java
index e36bfe66b..7cf111283 100644
--- a/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java
+++ b/src/protocol/native/org/apache/jmeter/protocol/system/gui/SystemSamplerGui.java
@@ -1,282 +1,282 @@
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
 
 package org.apache.jmeter.protocol.system.gui;
 
 import java.awt.BorderLayout;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.gui.util.FilePanelEntry;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.system.SystemSampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.gui.ObjectTableModel;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * GUI for {@link SystemSampler}
  */
 public class SystemSamplerGui extends AbstractSamplerGui implements ItemListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SystemSamplerGui.class);
 
     /**
      * 
      */
     private static final long serialVersionUID = -2413845772703695934L;
     
     private JCheckBox checkReturnCode;
     private JLabeledTextField desiredReturnCode;
     private final FilePanelEntry stdin = new FilePanelEntry(JMeterUtils.getResString("system_sampler_stdin")); // $NON-NLS-1$
     private final FilePanelEntry stdout = new FilePanelEntry(JMeterUtils.getResString("system_sampler_stdout")); // $NON-NLS-1$
     private final FilePanelEntry stderr = new FilePanelEntry(JMeterUtils.getResString("system_sampler_stderr")); // $NON-NLS-1$
     private JLabeledTextField directory;
     private JLabeledTextField command;
     private JLabeledTextField timeout;
     private ArgumentsPanel argsPanel;
     private ArgumentsPanel envPanel;
     
     /**
      * Constructor for JavaTestSamplerGui
      */
     public SystemSamplerGui() {
         super();
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "system_sampler_title"; // $NON-NLS-1$
     }
 
     @Override
     public String getStaticLabel() {
         return JMeterUtils.getResString(getLabelResource());
     }
 
     /**
      * Initialize the GUI components and layout.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout());
         setBorder(makeBorder());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
         add(makeCommandPanel(), BorderLayout.CENTER);
         
         JPanel streamsCodePane = new JPanel(new BorderLayout());
         streamsCodePane.add(makeStreamsPanel(), BorderLayout.NORTH);
         streamsCodePane.add(makeReturnCodePanel(), BorderLayout.CENTER);
         streamsCodePane.add(makeTimeoutPanel(), BorderLayout.SOUTH);
         add(streamsCodePane, BorderLayout.SOUTH);
     }
 
     /* Implements JMeterGuiComponent.createTestElement() */
     @Override
     public TestElement createTestElement() {
         SystemSampler sampler = new SystemSampler();
         modifyTestElement(sampler);
         return sampler;
     }
 
     @Override
     public void modifyTestElement(TestElement sampler) {
         super.configureTestElement(sampler);
         SystemSampler systemSampler = (SystemSampler)sampler;
         systemSampler.setCheckReturnCode(checkReturnCode.isSelected());
         if(checkReturnCode.isSelected()) {
             if(!StringUtils.isEmpty(desiredReturnCode.getText())) {
                 systemSampler.setExpectedReturnCode(Integer.parseInt(desiredReturnCode.getText()));
             } else {
                 systemSampler.setExpectedReturnCode(SystemSampler.DEFAULT_RETURN_CODE);
             }
         } else {
             systemSampler.setExpectedReturnCode(SystemSampler.DEFAULT_RETURN_CODE);
         }
         systemSampler.setCommand(command.getText());
         systemSampler.setArguments((Arguments)argsPanel.createTestElement());
         systemSampler.setEnvironmentVariables((Arguments)envPanel.createTestElement());
         systemSampler.setDirectory(directory.getText());
         systemSampler.setStdin(stdin.getFilename());
         systemSampler.setStdout(stdout.getFilename());
         systemSampler.setStderr(stderr.getFilename());
         if(!StringUtils.isEmpty(timeout.getText())) {
             try {
                 systemSampler.setTimout(Long.parseLong(timeout.getText()));
             } catch (NumberFormatException e) {
                 log.error("Error parsing timeout field value:"+timeout.getText(), e);
             }
         } 
     }
 
     /* Overrides AbstractJMeterGuiComponent.configure(TestElement) */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         SystemSampler systemSampler = (SystemSampler) el;
         checkReturnCode.setSelected(systemSampler.getCheckReturnCode());
         desiredReturnCode.setText(Integer.toString(systemSampler.getExpectedReturnCode()));
         desiredReturnCode.setEnabled(checkReturnCode.isSelected());
         command.setText(systemSampler.getCommand());
         argsPanel.configure(systemSampler.getArguments());
         envPanel.configure(systemSampler.getEnvironmentVariables());
         directory.setText(systemSampler.getDirectory());
         stdin.setFilename(systemSampler.getStdin());
         stdout.setFilename(systemSampler.getStdout());
         stderr.setFilename(systemSampler.getStderr());
         timeout.setText(systemSampler.getTimeout() == 0L ? "":  // $NON-NLS-1$
             Long.toString(systemSampler.getTimeout())); // not sure if replace 0L to empty string is the good way.
     }
 
     /**
      * @return JPanel return code config
      */
     private JPanel makeReturnCodePanel() {
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
         panel.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("return_code_config_box_title"))); // $NON-NLS-1$
         checkReturnCode = new JCheckBox(JMeterUtils.getResString("check_return_code_title")); // $NON-NLS-1$
         checkReturnCode.addItemListener(this);
         desiredReturnCode = new JLabeledTextField(JMeterUtils.getResString("expected_return_code_title")); // $NON-NLS-1$
         desiredReturnCode.setSize(desiredReturnCode.getSize().height, 30);
         panel.add(checkReturnCode);
         panel.add(Box.createHorizontalStrut(5));
         panel.add(desiredReturnCode);
         checkReturnCode.setSelected(true);
         return panel;
     }
     
     /**
      * @return JPanel timeout config
      */
     private JPanel makeTimeoutPanel() {
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
         panel.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("timeout_config_box_title"))); // $NON-NLS-1$
         timeout = new JLabeledTextField(JMeterUtils.getResString("timeout_title")); // $NON-NLS-1$
         timeout.setSize(timeout.getSize().height, 30);
         panel.add(timeout);
         return panel;
     }
     
     /**
      * @return JPanel Command + directory
      */
     private JPanel makeCommandPanel() {       
         JPanel cmdPanel = new JPanel();
         cmdPanel.setLayout(new BoxLayout(cmdPanel, BoxLayout.X_AXIS));
 
         JPanel cmdWkDirPane = new JPanel(new BorderLayout());
         command = new JLabeledTextField(JMeterUtils.getResString("command_field_title")); // $NON-NLS-1$
         cmdWkDirPane.add(command, BorderLayout.CENTER);
         directory = new JLabeledTextField(JMeterUtils.getResString("directory_field_title")); // $NON-NLS-1$
         cmdWkDirPane.add(directory, BorderLayout.EAST);
         cmdPanel.add(cmdWkDirPane);
         
         JPanel panel = new VerticalPanel();
         panel.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("command_config_box_title"))); // $NON-NLS-1$
         panel.add(cmdPanel, BorderLayout.NORTH);
         panel.add(makeArgumentsPanel(), BorderLayout.CENTER);
         panel.add(makeEnvironmentPanel(), BorderLayout.SOUTH);
         return panel;
     }
     
     /**
      * @return JPanel Arguments Panel
      */
     private JPanel makeArgumentsPanel() {
         argsPanel = new ArgumentsPanel(JMeterUtils.getResString("arguments_panel_title"), null, true, false ,  // $NON-NLS-1$
                 new ObjectTableModel(new String[] { ArgumentsPanel.COLUMN_RESOURCE_NAMES_1 },
                         Argument.class,
                         new Functor[] {
                         new Functor("getValue") },  // $NON-NLS-1$
                         new Functor[] {
                         new Functor("setValue") }, // $NON-NLS-1$
                         new Class[] {String.class }));
         return argsPanel;
     }
     
     /**
      * @return JPanel Environment Panel
      */
     private JPanel makeEnvironmentPanel() {
         envPanel = new ArgumentsPanel(JMeterUtils.getResString("environment_panel_title")); // $NON-NLS-1$
         return envPanel;
     }
 
     /**
      * @return JPanel Streams Panel
      */
     private JPanel makeStreamsPanel() {
         JPanel stdPane = new JPanel(new BorderLayout());
         stdPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("command_config_std_streams_title"))); // $NON-NLS-1$
         stdPane.add(stdin, BorderLayout.NORTH);
         stdPane.add(stdout, BorderLayout.CENTER);
         stdPane.add(stderr, BorderLayout.SOUTH);
         return stdPane;
     }
 
     /**
      * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#clearGui()
      */
     @Override
     public void clearGui() {
         super.clearGui();
         directory.setText(""); // $NON-NLS-1$
         command.setText(""); // $NON-NLS-1$
         argsPanel.clearGui();
         envPanel.clearGui();
         desiredReturnCode.setText(""); // $NON-NLS-1$
         checkReturnCode.setSelected(false);
         desiredReturnCode.setEnabled(false);
         stdin.clearGui();
         stdout.clearGui();
         stderr.clearGui();
         timeout.setText(""); // $NON-NLS-1$
     }
 
     @Override
     public void itemStateChanged(ItemEvent e) {
         if(e.getSource()==checkReturnCode) {
             desiredReturnCode.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
         }
     }
 }
