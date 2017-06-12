diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java
index 5f9266a38..fbab2a83c 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/HttpDefaultsGui.java
@@ -1,289 +1,468 @@
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
 
 package org.apache.jmeter.protocol.http.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
+import java.awt.Font;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
+import javax.swing.JLabel;
 import javax.swing.JPanel;
+import javax.swing.JPasswordField;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextField;
+import javax.swing.UIManager;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
+import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
+import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 /**
  * GUI for Http Request defaults
  *
  */
 public class HttpDefaultsGui extends AbstractConfigGui {
 
     private static final long serialVersionUID = 241L;
 
+    private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
+    
+    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));
+
     private UrlConfigGui urlConfigGui;
 
     private JCheckBox retrieveEmbeddedResources;
     
     private JCheckBox concurrentDwn;
     
     private JTextField concurrentPool; 
 
     private JCheckBox useMD5;
 
     private JLabeledTextField embeddedRE; // regular expression used to match against embedded resource URLs
 
     private JTextField sourceIpAddr; // does not apply to Java implementation
     
     private JComboBox<String> sourceIpType = new JComboBox<>(HTTPSamplerBase.getSourceTypeList());
 
+    private JTextField proxyHost;
+
+    private JTextField proxyPort;
+
+    private JTextField proxyUser;
+
+    private JPasswordField proxyPass;
+    
+    private JLabeledChoice httpImplementation;
+
+    private JTextField connectTimeOut;
+
+    private JTextField responseTimeOut;
+
+
     public HttpDefaultsGui() {
         super();
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "url_config_title"; // $NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     @Override
     public TestElement createTestElement() {
         ConfigTestElement config = new ConfigTestElement();
         modifyTestElement(config);
         return config;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement config) {
         ConfigTestElement cfg = (ConfigTestElement ) config;
         ConfigTestElement el = (ConfigTestElement) urlConfigGui.createTestElement();
         cfg.clear(); 
         cfg.addConfigElement(el);
         super.configureTestElement(config);
         if (retrieveEmbeddedResources.isSelected()) {
             config.setProperty(new BooleanProperty(HTTPSamplerBase.IMAGE_PARSER, true));
         } else {
             config.removeProperty(HTTPSamplerBase.IMAGE_PARSER);
         }
         enableConcurrentDwn(retrieveEmbeddedResources.isSelected());
         if (concurrentDwn.isSelected()) {
             config.setProperty(new BooleanProperty(HTTPSamplerBase.CONCURRENT_DWN, true));
         } else {
             // The default is false, so we can remove the property to simplify JMX files
             // This also allows HTTPDefaults to work for this checkbox
             config.removeProperty(HTTPSamplerBase.CONCURRENT_DWN);
         }
         if(!StringUtils.isEmpty(concurrentPool.getText())) {
             config.setProperty(new StringProperty(HTTPSamplerBase.CONCURRENT_POOL,
                     concurrentPool.getText()));
         } else {
             config.setProperty(new StringProperty(HTTPSamplerBase.CONCURRENT_POOL,
                     String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE)));
         }
         if(useMD5.isSelected()) {
             config.setProperty(new BooleanProperty(HTTPSamplerBase.MD5, true));
         } else {
             config.removeProperty(HTTPSamplerBase.MD5);
         }
         if (!StringUtils.isEmpty(embeddedRE.getText())) {
             config.setProperty(new StringProperty(HTTPSamplerBase.EMBEDDED_URL_RE,
                     embeddedRE.getText()));
         } else {
             config.removeProperty(HTTPSamplerBase.EMBEDDED_URL_RE);
         }
         
         if(!StringUtils.isEmpty(sourceIpAddr.getText())) {
             config.setProperty(new StringProperty(HTTPSamplerBase.IP_SOURCE,
                     sourceIpAddr.getText()));
             config.setProperty(new IntegerProperty(HTTPSamplerBase.IP_SOURCE_TYPE,
                     sourceIpType.getSelectedIndex()));
         } else {
             config.removeProperty(HTTPSamplerBase.IP_SOURCE);
             config.removeProperty(HTTPSamplerBase.IP_SOURCE_TYPE);
         }
+
+        config.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
+        config.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
+        config.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
+        config.setProperty(HTTPSamplerBase.PROXYPASS, String.valueOf(proxyPass.getPassword()),"");
+        config.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getText(),"");
+        config.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
+        config.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
+
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
         retrieveEmbeddedResources.setSelected(false);
         concurrentDwn.setSelected(false);
         concurrentPool.setText(String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE));
         enableConcurrentDwn(false);
         useMD5.setSelected(false);
         urlConfigGui.clear();
         embeddedRE.setText(""); // $NON-NLS-1$
         sourceIpAddr.setText(""); // $NON-NLS-1$
         sourceIpType.setSelectedIndex(HTTPSamplerBase.SourceType.HOSTNAME.ordinal()); //default: IP/Hostname
+        proxyHost.setText(""); // $NON-NLS-1$
+        proxyPort.setText(""); // $NON-NLS-1$
+        proxyUser.setText(""); // $NON-NLS-1$
+        proxyPass.setText(""); // $NON-NLS-1$
+        httpImplementation.setText(""); // $NON-NLS-1$
+        connectTimeOut.setText(""); // $NON-NLS-1$
+        responseTimeOut.setText(""); // $NON-NLS-1$
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         AbstractTestElement samplerBase = (AbstractTestElement) el;
         urlConfigGui.configure(el);
         retrieveEmbeddedResources.setSelected(samplerBase.getPropertyAsBoolean(HTTPSamplerBase.IMAGE_PARSER));
         concurrentDwn.setSelected(samplerBase.getPropertyAsBoolean(HTTPSamplerBase.CONCURRENT_DWN));
         concurrentPool.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.CONCURRENT_POOL));
         useMD5.setSelected(samplerBase.getPropertyAsBoolean(HTTPSamplerBase.MD5, false));
         embeddedRE.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.EMBEDDED_URL_RE, ""));//$NON-NLS-1$
         sourceIpAddr.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.IP_SOURCE)); //$NON-NLS-1$
         sourceIpType.setSelectedIndex(
                 samplerBase.getPropertyAsInt(HTTPSamplerBase.IP_SOURCE_TYPE,
                         HTTPSamplerBase.SOURCE_TYPE_DEFAULT));
+
+        proxyHost.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
+        proxyPort.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
+        proxyUser.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
+        proxyPass.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
+        httpImplementation.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
+        connectTimeOut.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
+        responseTimeOut.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
 
         // URL CONFIG
         urlConfigGui = new UrlConfigGui(false, true, false);
 
         // AdvancedPanel (embedded resources, source address and optional tasks)
         JPanel advancedPanel = new VerticalPanel();
         advancedPanel.add(createEmbeddedRsrcPanel());
+        advancedPanel.add(getTimeOutPanel());
+        advancedPanel.add(getImplementationPanel());
         advancedPanel.add(createSourceAddrPanel());
+        advancedPanel.add(getProxyServerPanel());
         advancedPanel.add(createOptionalTasksPanel());
 
         JTabbedPane tabbedPane = new JTabbedPane();
         tabbedPane.add(JMeterUtils
                 .getResString("web_testing_basic"), urlConfigGui);
         tabbedPane.add(JMeterUtils
                 .getResString("web_testing_advanced"), advancedPanel);
 
         JPanel emptyPanel = new JPanel();
         emptyPanel.setMaximumSize(new Dimension());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
         add(tabbedPane, BorderLayout.CENTER);        
         add(emptyPanel, BorderLayout.SOUTH);
     }
     
+    private JPanel getTimeOutPanel() {
+        JPanel timeOut = new HorizontalPanel();
+        timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
+                JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
+        final JPanel connPanel = getConnectTimeOutPanel();
+        final JPanel reqPanel = getResponseTimeOutPanel();
+        timeOut.add(connPanel);
+        timeOut.add(reqPanel);
+        return timeOut;
+    }
+    
+    private JPanel getConnectTimeOutPanel() {
+        connectTimeOut = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
+        label.setLabelFor(connectTimeOut);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(connectTimeOut, BorderLayout.CENTER);
+
+        return panel;
+    }
+
+    private JPanel getResponseTimeOutPanel() {
+        responseTimeOut = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
+        label.setLabelFor(responseTimeOut);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(responseTimeOut, BorderLayout.CENTER);
+
+        return panel;
+    }
+    
     protected JPanel createEmbeddedRsrcPanel() {
         // retrieve Embedded resources
         retrieveEmbeddedResources = new JCheckBox(JMeterUtils.getResString("web_testing_retrieve_images")); // $NON-NLS-1$
         // add a listener to activate or not concurrent dwn.
         retrieveEmbeddedResources.addItemListener(new ItemListener() {
             @Override
             public void itemStateChanged(final ItemEvent e) {
                 if (e.getStateChange() == ItemEvent.SELECTED) { enableConcurrentDwn(true); }
                 else { enableConcurrentDwn(false); }
             }
         });
         // Download concurrent resources
         concurrentDwn = new JCheckBox(JMeterUtils.getResString("web_testing_concurrent_download")); // $NON-NLS-1$
         concurrentDwn.addItemListener(new ItemListener() {
             @Override
             public void itemStateChanged(final ItemEvent e) {
                 if (retrieveEmbeddedResources.isSelected() && e.getStateChange() == ItemEvent.SELECTED) { concurrentPool.setEnabled(true); }
                 else { concurrentPool.setEnabled(false); }
             }
         });
         concurrentPool = new JTextField(2); // 2 columns size
         concurrentPool.setMinimumSize(new Dimension(10, (int) concurrentPool.getPreferredSize().getHeight()));
         concurrentPool.setMaximumSize(new Dimension(30, (int) concurrentPool.getPreferredSize().getHeight()));
 
         final JPanel embeddedRsrcPanel = new HorizontalPanel();
         embeddedRsrcPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("web_testing_retrieve_title"))); // $NON-NLS-1$
         embeddedRsrcPanel.add(retrieveEmbeddedResources);
         embeddedRsrcPanel.add(concurrentDwn);
         embeddedRsrcPanel.add(concurrentPool);
         
         // Embedded URL match regex
         embeddedRE = new JLabeledTextField(JMeterUtils.getResString("web_testing_embedded_url_pattern"),20); // $NON-NLS-1$
         embeddedRsrcPanel.add(embeddedRE); 
         
         return embeddedRsrcPanel;
     }
     
     protected JPanel createSourceAddrPanel() {
         final JPanel sourceAddrPanel = new HorizontalPanel();
         sourceAddrPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("web_testing_source_ip"))); // $NON-NLS-1$
 
         sourceIpType.setSelectedIndex(HTTPSamplerBase.SourceType.HOSTNAME.ordinal()); //default: IP/Hostname
         sourceAddrPanel.add(sourceIpType);
 
         sourceIpAddr = new JTextField();
         sourceAddrPanel.add(sourceIpAddr);
         return sourceAddrPanel;
     }
     
     protected JPanel createOptionalTasksPanel() {
         // OPTIONAL TASKS
         final JPanel checkBoxPanel = new VerticalPanel();
         checkBoxPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("optional_tasks"))); // $NON-NLS-1$
 
         // Use MD5
         useMD5 = new JCheckBox(JMeterUtils.getResString("response_save_as_md5")); // $NON-NLS-1$
 
         checkBoxPanel.add(useMD5);
 
         return checkBoxPanel;
     }
 
     @Override
     public Dimension getPreferredSize() {
         return getMinimumSize();
     }
     
     private void enableConcurrentDwn(final boolean enable) {
         if (enable) {
             concurrentDwn.setEnabled(true);
             embeddedRE.setEnabled(true);
             if (concurrentDwn.isSelected()) {
                 concurrentPool.setEnabled(true);
             }
         } else {
             concurrentDwn.setEnabled(false);
             concurrentPool.setEnabled(false);
             embeddedRE.setEnabled(false);
         }
     }
+    
+    /**
+     * Create a panel containing the implementation details
+     *
+     * @return the panel
+     */
+    protected final JPanel getImplementationPanel(){
+        JPanel implPanel = new HorizontalPanel();
+        httpImplementation = new JLabeledChoice(JMeterUtils.getResString("http_implementation"), // $NON-NLS-1$
+                HTTPSamplerFactory.getImplementations());
+        httpImplementation.addValue("");
+        implPanel.add(httpImplementation);
+        return implPanel;
+    }
+
+    /**
+     * Create a panel containing the proxy server details
+     *
+     * @return the panel
+     */
+    protected final JPanel getProxyServerPanel(){
+        JPanel proxyServer = new HorizontalPanel();
+        proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
+        proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);
+
+        JPanel proxyLogin = new HorizontalPanel();
+        proxyLogin.add(getProxyUserPanel());
+        proxyLogin.add(getProxyPassPanel());
+
+        JPanel proxyServerPanel = new HorizontalPanel();
+        proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
+                JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
+        proxyServerPanel.add(proxyServer);
+        proxyServerPanel.add(proxyLogin);
+
+        return proxyServerPanel;
+    }
+
+    private JPanel getProxyHostPanel() {
+        proxyHost = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
+        label.setLabelFor(proxyHost);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyHost, BorderLayout.CENTER);
+        return panel;
+    }
+    
+    private JPanel getProxyPortPanel() {
+        proxyPort = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
+        label.setLabelFor(proxyPort);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyPort, BorderLayout.CENTER);
+
+        return panel;
+    }
+
+    private JPanel getProxyUserPanel() {
+        proxyUser = new JTextField(5);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
+        label.setLabelFor(proxyUser);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyUser, BorderLayout.CENTER);
+        return panel;
+    }
+
+    private JPanel getProxyPassPanel() {
+        proxyPass = new JPasswordField(5);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
+        label.setLabelFor(proxyPass);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyPass, BorderLayout.CENTER);
+        return panel;
+    }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index c48750a9e..79fbbb9b7 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,852 +1,617 @@
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
 
 package org.apache.jmeter.protocol.http.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.FlowLayout;
 import java.awt.Font;
 
 import javax.swing.BorderFactory;
-import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
-import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
-import javax.swing.JPasswordField;
 import javax.swing.JTabbedPane;
-import javax.swing.JTextField;
 import javax.swing.UIManager;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.JSyntaxTextArea;
 import org.apache.jmeter.gui.util.JTextScrollPane;
-import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
 import org.apache.jmeter.protocol.http.gui.HTTPFileArgsPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
-import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
+import org.apache.jorphan.gui.JLabeledTextField;
 
 /**
  * Basic URL / HTTP Request configuration:
  * <ul>
  * <li>host and port</li>
  * <li>connect and response timeouts</li>
  * <li>path, method, encoding, parameters</li>
  * <li>redirects and keepalive</li>
  * </ul>
  */
 public class UrlConfigGui extends JPanel implements ChangeListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final int TAB_PARAMETERS = 0;
     
     private int tabRawBodyIndex = 1;
     
     private int tabFileUploadIndex = 2;
 
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
     
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));
 
     private HTTPArgumentsPanel argsPanel;
     
     private HTTPFileArgsPanel filesPanel;
 
-    private JTextField domain;
+    private JLabeledTextField domain;
 
-    private JTextField port;
+    private JLabeledTextField port;
 
-    private JTextField proxyHost;
+    private JLabeledTextField protocol;
 
-    private JTextField proxyPort;
+    private JLabeledTextField contentEncoding;
 
-    private JTextField proxyUser;
-
-    private JPasswordField proxyPass;
-
-    private JTextField connectTimeOut;
-
-    private JTextField responseTimeOut;
-
-    private JTextField protocol;
-
-    private JTextField contentEncoding;
-
-    private JTextField path;
+    private JLabeledTextField path;
 
     private JCheckBox followRedirects;
 
     private JCheckBox autoRedirects;
 
     private JCheckBox useKeepAlive;
 
     private JCheckBox useMultipartForPost;
 
     private JCheckBox useBrowserCompatibleMultipartMode;
 
     private JLabeledChoice method;
     
-    private JLabeledChoice httpImplementation;
-
     // set this false to suppress some items for use in HTTP Request defaults
     private final boolean notConfigOnly;
     
-    private final boolean showImplementation; // Set false for AJP
-
     // Body data
     private JSyntaxTextArea postBodyContent;
 
     // Tabbed pane that contains parameters and raw body
     private ValidationTabbedPane postContentTabbedPane;
 
     private boolean showRawBodyPane;
     private boolean showFileUploadPane;
 
     /**
      * Constructor which is setup to show HTTP implementation, raw body pane and
      * sampler fields.
      */
     public UrlConfigGui() {
         this(true);
     }
 
     /**
      * Constructor which is setup to show HTTP implementation and raw body pane.
      *
      * @param showSamplerFields
      *            flag whether sampler fields should be shown.
      */
     public UrlConfigGui(boolean showSamplerFields) {
-        this(showSamplerFields, true, true);
+        this(showSamplerFields, true);
     }
 
     /**
      * @param showSamplerFields
      *            flag whether sampler fields should be shown
-     * @param showImplementation
-     *            Show HTTP Implementation
      * @param showRawBodyPane
      *            flag whether the raw body pane should be shown
      */
-    public UrlConfigGui(boolean showSamplerFields, boolean showImplementation, boolean showRawBodyPane) {
-        this(showSamplerFields, showImplementation, showRawBodyPane, false);
+    public UrlConfigGui(boolean showSamplerFields, boolean showRawBodyPane) {
+        this(showSamplerFields, showRawBodyPane, false);
     }
     
     /**
      * @param showSamplerFields
      *            flag whether sampler fields should be shown
-     * @param showImplementation
-     *            Show HTTP Implementation
      * @param showRawBodyPane
      *            flag whether the raw body pane should be shown
      * @param showFileUploadPane flag whether the file upload pane should be shown
      */
-    public UrlConfigGui(boolean showSamplerFields, boolean showImplementation, boolean showRawBodyPane, boolean showFileUploadPane) {
+    public UrlConfigGui(boolean showSamplerFields, boolean showRawBodyPane, boolean showFileUploadPane) {
         this.notConfigOnly = showSamplerFields;
-        this.showImplementation = showImplementation;
         this.showRawBodyPane = showRawBodyPane;
         this.showFileUploadPane = showFileUploadPane;
         init();
     }
 
     public void clear() {
         domain.setText(""); // $NON-NLS-1$
         if (notConfigOnly){
             followRedirects.setSelected(true);
             autoRedirects.setSelected(false);
             method.setText(HTTPSamplerBase.DEFAULT_METHOD);
             useKeepAlive.setSelected(true);
             useMultipartForPost.setSelected(false);
             useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
-        if (showImplementation) {
-            httpImplementation.setText(""); // $NON-NLS-1$
-        }
         path.setText(""); // $NON-NLS-1$
         port.setText(""); // $NON-NLS-1$
-        proxyHost.setText(""); // $NON-NLS-1$
-        proxyPort.setText(""); // $NON-NLS-1$
-        proxyUser.setText(""); // $NON-NLS-1$
-        proxyPass.setText(""); // $NON-NLS-1$
-        connectTimeOut.setText(""); // $NON-NLS-1$
-        responseTimeOut.setText(""); // $NON-NLS-1$
         protocol.setText(""); // $NON-NLS-1$
         contentEncoding.setText(""); // $NON-NLS-1$
         argsPanel.clear();
         if(showFileUploadPane) {
             filesPanel.clear();
         }
         if(showRawBodyPane) {
             postBodyContent.setInitialText("");// $NON-NLS-1$
         }
         postContentTabbedPane.setSelectedIndex(TAB_PARAMETERS, false);
     }
 
     public TestElement createTestElement() {
         ConfigTestElement element = new ConfigTestElement();
 
         element.setName(this.getName());
         element.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
         element.setProperty(TestElement.TEST_CLASS, element.getClass().getName());
         modifyTestElement(element);
         return element;
     }
 
     /**
      * Save the GUI values in the sampler.
      *
      * @param element {@link TestElement} to modify
      */
     public void modifyTestElement(TestElement element) {
         boolean useRaw = postContentTabbedPane.getSelectedIndex()==tabRawBodyIndex;
         Arguments args;
         if(useRaw) {
             args = new Arguments();
             String text = postBodyContent.getText();
             /*
              * Textfield uses \n (LF) to delimit lines; we need to send CRLF.
              * Rather than change the way that arguments are processed by the
              * samplers for raw data, it is easier to fix the data.
              * On retrival, CRLF is converted back to LF for storage in the text field.
              * See
              */
             HTTPArgument arg = new HTTPArgument("", text.replaceAll("\n","\r\n"), false);
             arg.setAlwaysEncoded(false);
             args.addArgument(arg);
         } else {
             args = (Arguments) argsPanel.createTestElement();
             HTTPArgument.convertArgumentsToHTTP(args);
             if(showFileUploadPane) {
                 filesPanel.modifyTestElement(element);
             }
         }
         element.setProperty(HTTPSamplerBase.POST_BODY_RAW, useRaw, HTTPSamplerBase.POST_BODY_RAW_DEFAULT);
         element.setProperty(new TestElementProperty(HTTPSamplerBase.ARGUMENTS, args));
         element.setProperty(HTTPSamplerBase.DOMAIN, domain.getText());
         element.setProperty(HTTPSamplerBase.PORT, port.getText());
-        element.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
-        element.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
-        element.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
-        element.setProperty(HTTPSamplerBase.PROXYPASS, String.valueOf(proxyPass.getPassword()),"");
-        element.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
-        element.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
         element.setProperty(HTTPSamplerBase.PROTOCOL, protocol.getText());
         element.setProperty(HTTPSamplerBase.CONTENT_ENCODING, contentEncoding.getText());
         element.setProperty(HTTPSamplerBase.PATH, path.getText());
         if (notConfigOnly){
             element.setProperty(HTTPSamplerBase.METHOD, method.getText());
             element.setProperty(new BooleanProperty(HTTPSamplerBase.FOLLOW_REDIRECTS, followRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.AUTO_REDIRECTS, autoRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.USE_KEEPALIVE, useKeepAlive.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.DO_MULTIPART_POST, useMultipartForPost.isSelected()));
             element.setProperty(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, useBrowserCompatibleMultipartMode.isSelected(),HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
-        if (showImplementation) {
-            element.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getText(),"");
-        }
     }
 
     // FIXME FACTOR WITH HTTPHC4Impl, HTTPHC3Impl
     // Just append all the parameter values, and use that as the post body
     /**
      * Compute body data from arguments
      * @param arguments {@link Arguments}
      * @return {@link String}
      */
     private static String computePostBody(Arguments arguments) {
         return computePostBody(arguments, false);
     }
 
     /**
      * Compute body data from arguments
      * @param arguments {@link Arguments}
      * @param crlfToLF whether to convert CRLF to LF
      * @return {@link String}
      */
     private static String computePostBody(Arguments arguments, boolean crlfToLF) {
         StringBuilder postBody = new StringBuilder();
         for (JMeterProperty argument : arguments) {
             HTTPArgument arg = (HTTPArgument) argument.getObjectValue();
             String value = arg.getValue();
             if (crlfToLF) {
                 value = value.replaceAll("\r\n", "\n"); // See modifyTestElement
             }
             postBody.append(value);
         }
         return postBody.toString();
     }
 
     /**
      * Set the text, etc. in the UI.
      *
      * @param el
      *            contains the data to be displayed
      */
     public void configure(TestElement el) {
         setName(el.getName());
         Arguments arguments = (Arguments) el.getProperty(HTTPSamplerBase.ARGUMENTS).getObjectValue();
 
         boolean useRaw = el.getPropertyAsBoolean(HTTPSamplerBase.POST_BODY_RAW, HTTPSamplerBase.POST_BODY_RAW_DEFAULT);
         if(useRaw) {
             String postBody = computePostBody(arguments, true); // Convert CRLF to CR, see modifyTestElement
             postBodyContent.setInitialText(postBody); 
             postBodyContent.setCaretPosition(0);
             postContentTabbedPane.setSelectedIndex(tabRawBodyIndex, false);
         } else {
             argsPanel.configure(arguments);
             postContentTabbedPane.setSelectedIndex(TAB_PARAMETERS, false);
             if(showFileUploadPane) {
                 filesPanel.configure(el);                
             }
         }
 
         domain.setText(el.getPropertyAsString(HTTPSamplerBase.DOMAIN));
 
         String portString = el.getPropertyAsString(HTTPSamplerBase.PORT);
 
         // Only display the port number if it is meaningfully specified
         if (portString.equals(HTTPSamplerBase.UNSPECIFIED_PORT_AS_STRING)) {
             port.setText(""); // $NON-NLS-1$
         } else {
             port.setText(portString);
         }
-        proxyHost.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
-        proxyPort.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
-        proxyUser.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
-        proxyPass.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
-        connectTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
-        responseTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
         protocol.setText(el.getPropertyAsString(HTTPSamplerBase.PROTOCOL));
         contentEncoding.setText(el.getPropertyAsString(HTTPSamplerBase.CONTENT_ENCODING));
         path.setText(el.getPropertyAsString(HTTPSamplerBase.PATH));
         if (notConfigOnly){
             method.setText(el.getPropertyAsString(HTTPSamplerBase.METHOD));
             followRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.FOLLOW_REDIRECTS));
             autoRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.AUTO_REDIRECTS));
             useKeepAlive.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.USE_KEEPALIVE));
             useMultipartForPost.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.DO_MULTIPART_POST));
             useBrowserCompatibleMultipartMode.setSelected(el.getPropertyAsBoolean(
                     HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT));
         }
-        if (showImplementation) {
-            httpImplementation.setText(el.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
-        }
     }
 
     private void init() {// called from ctor, so must not be overridable
         this.setLayout(new BorderLayout());
 
         // WEB REQUEST PANEL
         JPanel webRequestPanel = new JPanel();
         webRequestPanel.setLayout(new BorderLayout());
         webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_request"))); // $NON-NLS-1$
 
-        JPanel northPanel = new JPanel();
-        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
-        northPanel.add(getProtocolAndMethodPanel());
-        northPanel.add(getPathPanel());
-
-        webRequestPanel.add(northPanel, BorderLayout.NORTH);
+        webRequestPanel.add(getPathPanel(), BorderLayout.NORTH);
         webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);
 
-        this.add(getWebServerTimeoutPanel(), BorderLayout.NORTH);
+        this.add(getWebServerPanel(), BorderLayout.NORTH);
         this.add(webRequestPanel, BorderLayout.CENTER);
-        this.add(getProxyServerPanel(), BorderLayout.SOUTH); 
     }
 
     /**
-     * Create a panel containing the webserver (domain+port) and timeouts (connect+request).
+     * Create a panel containing the webserver (domain+port) and scheme.
      *
      * @return the panel
      */
-    protected final JPanel getWebServerTimeoutPanel() {
-        // WEB SERVER PANEL
+    protected final JPanel getWebServerPanel() {        
+        // PROTOCOL
+        protocol = new JLabeledTextField(JMeterUtils.getResString("protocol"), 4); // $NON-NLS-1$
+        port = new JLabeledTextField(JMeterUtils.getResString("web_server_port"), 7); // $NON-NLS-1$
+        domain = new JLabeledTextField(JMeterUtils.getResString("web_server_domain"), 40); // $NON-NLS-1$
+
         JPanel webServerPanel = new HorizontalPanel();
         webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
-        final JPanel domainPanel = getDomainPanel();
-        final JPanel portPanel = getPortPanel();
-        webServerPanel.add(domainPanel, BorderLayout.CENTER);
-        webServerPanel.add(portPanel, BorderLayout.EAST);
-
-        JPanel timeOut = new HorizontalPanel();
-        timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
-                JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
-        final JPanel connPanel = getConnectTimeOutPanel();
-        final JPanel reqPanel = getResponseTimeOutPanel();
-        timeOut.add(connPanel);
-        timeOut.add(reqPanel);
-
-        JPanel webServerTimeoutPanel = new VerticalPanel();
-        webServerTimeoutPanel.add(webServerPanel, BorderLayout.CENTER);
-        webServerTimeoutPanel.add(timeOut, BorderLayout.EAST);
-
-        JPanel bigPanel = new VerticalPanel();
-        bigPanel.add(webServerTimeoutPanel);
-        return bigPanel;
-    }
-
-    /**
-     * Create a panel containing the proxy server details
-     *
-     * @return the panel
-     */
-    protected final JPanel getProxyServerPanel(){
-        JPanel proxyServer = new HorizontalPanel();
-        proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
-        proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);
-
-        JPanel proxyLogin = new HorizontalPanel();
-        proxyLogin.add(getProxyUserPanel());
-        proxyLogin.add(getProxyPassPanel());
-
-        JPanel proxyServerPanel = new HorizontalPanel();
-        proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
-                JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
-        proxyServerPanel.add(proxyServer);
-        proxyServerPanel.add(proxyLogin);
-
-        return proxyServerPanel;
-    }
-
-    private JPanel getPortPanel() {
-        port = new JTextField(10);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
-        label.setLabelFor(port);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(port, BorderLayout.CENTER);
-
-        return panel;
-    }
-
-    private JPanel getProxyPortPanel() {
-        proxyPort = new JTextField(10);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
-        label.setLabelFor(proxyPort);
-        label.setFont(FONT_SMALL);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(proxyPort, BorderLayout.CENTER);
-
-        return panel;
-    }
-
-    private JPanel getConnectTimeOutPanel() {
-        connectTimeOut = new JTextField(10);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
-        label.setLabelFor(connectTimeOut);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(connectTimeOut, BorderLayout.CENTER);
-
-        return panel;
-    }
-
-    private JPanel getResponseTimeOutPanel() {
-        responseTimeOut = new JTextField(10);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
-        label.setLabelFor(responseTimeOut);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(responseTimeOut, BorderLayout.CENTER);
-
-        return panel;
-    }
-
-    private JPanel getDomainPanel() {
-        domain = new JTextField(20);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
-        label.setLabelFor(domain);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(domain, BorderLayout.CENTER);
-        return panel;
-    }
-
-    private JPanel getProxyHostPanel() {
-        proxyHost = new JTextField(10);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
-        label.setLabelFor(proxyHost);
-        label.setFont(FONT_SMALL);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(proxyHost, BorderLayout.CENTER);
-        return panel;
-    }
-
-    private JPanel getProxyUserPanel() {
-        proxyUser = new JTextField(5);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
-        label.setLabelFor(proxyUser);
-        label.setFont(FONT_SMALL);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(proxyUser, BorderLayout.CENTER);
-        return panel;
+        webServerPanel.add(protocol);
+        webServerPanel.add(domain);
+        webServerPanel.add(port);
+        return webServerPanel;
     }
 
-    private JPanel getProxyPassPanel() {
-        proxyPass = new JPasswordField(5);
-
-        JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
-        label.setLabelFor(proxyPass);
-        label.setFont(FONT_SMALL);
-
-        JPanel panel = new JPanel(new BorderLayout(5, 0));
-        panel.add(label, BorderLayout.WEST);
-        panel.add(proxyPass, BorderLayout.CENTER);
-        return panel;
-    }
 
     /**
-     * This method defines the Panel for the HTTP path, 'Follow Redirects'
-     * 'Use KeepAlive', and 'Use multipart for HTTP POST' elements.
+     * This method defines the Panel for:
+     *  the HTTP path, Method and Content Encoding
+     *  'Follow Redirects', 'Use KeepAlive', and 'Use multipart for HTTP POST' elements.
      *
      * @return JPanel The Panel for the path, 'Follow Redirects' and 'Use
      *         KeepAlive' elements.
      */
     protected Component getPathPanel() {
-        path = new JTextField(15);
+        path = new JLabeledTextField(JMeterUtils.getResString("path"), 80); //$NON-NLS-1$
+        // CONTENT_ENCODING
+        contentEncoding = new JLabeledTextField(JMeterUtils.getResString("content_encoding"), 7); // $NON-NLS-1$
 
-        JLabel label = new JLabel(JMeterUtils.getResString("path")); //$NON-NLS-1$
-        label.setLabelFor(path);
+        if (notConfigOnly){
+            method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
+                    HTTPSamplerBase.getValidMethodsAsArray(), true, false);
+            method.addChangeListener(this);
+        }
 
         if (notConfigOnly){
             followRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects")); // $NON-NLS-1$
             followRedirects.setFont(null);
             followRedirects.setSelected(true);
             followRedirects.addChangeListener(this);
 
             autoRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects_auto")); //$NON-NLS-1$
             autoRedirects.setFont(null);
             autoRedirects.addChangeListener(this);
             autoRedirects.setSelected(false);// Default changed in 2.3 and again in 2.4
 
             useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
             useKeepAlive.setFont(null);
             useKeepAlive.setSelected(true);
 
             useMultipartForPost = new JCheckBox(JMeterUtils.getResString("use_multipart_for_http_post")); // $NON-NLS-1$
             useMultipartForPost.setFont(null);
             useMultipartForPost.setSelected(false);
 
             useBrowserCompatibleMultipartMode = new JCheckBox(JMeterUtils.getResString("use_multipart_mode_browser")); // $NON-NLS-1$
             useBrowserCompatibleMultipartMode.setFont(null);
             useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
 
         }
 
-        JPanel pathPanel = new HorizontalPanel();
-        pathPanel.add(label);
+        JPanel pathPanel =  new HorizontalPanel();
+        if (notConfigOnly){
+            pathPanel.add(method);
+        }
         pathPanel.add(path);
-
+        pathPanel.add(contentEncoding);
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add(pathPanel);
         if (notConfigOnly){
             JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
             optionPanel.setFont(FONT_SMALL); // all sub-components with setFont(null) inherit this font
             optionPanel.add(autoRedirects);
             optionPanel.add(followRedirects);
             optionPanel.add(useKeepAlive);
             optionPanel.add(useMultipartForPost);
             optionPanel.add(useBrowserCompatibleMultipartMode);
             optionPanel.setMinimumSize(optionPanel.getPreferredSize());
             panel.add(optionPanel);
         }
 
         return panel;
     }
 
-    protected JPanel getProtocolAndMethodPanel() {
-
-        // Implementation
-        if (showImplementation) {
-            httpImplementation = new JLabeledChoice(JMeterUtils.getResString("http_implementation"), // $NON-NLS-1$
-                    HTTPSamplerFactory.getImplementations());
-            httpImplementation.addValue("");
-        }
-        
-        // PROTOCOL
-        protocol = new JTextField(4);
-        JLabel protocolLabel = new JLabel(JMeterUtils.getResString("protocol")); // $NON-NLS-1$
-        protocolLabel.setLabelFor(protocol);        
-        
-        // CONTENT_ENCODING
-        contentEncoding = new JTextField(10);
-        JLabel contentEncodingLabel = new JLabel(JMeterUtils.getResString("content_encoding")); // $NON-NLS-1$
-        contentEncodingLabel.setLabelFor(contentEncoding);
-
-        if (notConfigOnly){
-            method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
-                    HTTPSamplerBase.getValidMethodsAsArray(), true, false);
-            method.addChangeListener(this);
-        }
-
-        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
-
-        if (showImplementation) {
-            panel.add(httpImplementation);
-        }
-        panel.add(protocolLabel);
-        panel.add(protocol);
-        panel.add(Box.createHorizontalStrut(5));
-
-        if (notConfigOnly){
-            panel.add(method);
-        }
-        panel.setMinimumSize(panel.getPreferredSize());
-        panel.add(Box.createHorizontalStrut(5));
-
-        panel.add(contentEncodingLabel);
-        panel.add(contentEncoding);
-        panel.setMinimumSize(panel.getPreferredSize());
-        return panel;
-    }
-
     protected JTabbedPane getParameterPanel() {
         postContentTabbedPane = new ValidationTabbedPane();
         argsPanel = new HTTPArgumentsPanel();
         postContentTabbedPane.add(JMeterUtils.getResString("post_as_parameters"), argsPanel);// $NON-NLS-1$
         
         int indx = TAB_PARAMETERS;
         if(showRawBodyPane) {
             tabRawBodyIndex = ++indx;
             postBodyContent = JSyntaxTextArea.getInstance(30, 50);// $NON-NLS-1$
             postContentTabbedPane.add(JMeterUtils.getResString("post_body"), JTextScrollPane.getInstance(postBodyContent));// $NON-NLS-1$
         }
         
         if(showFileUploadPane) {
             tabFileUploadIndex = ++indx;
             filesPanel = new HTTPFileArgsPanel();
             postContentTabbedPane.add(JMeterUtils.getResString("post_files_upload"), filesPanel);            
         }
         return postContentTabbedPane;
     }
 
     /**
      * 
      */
     class ValidationTabbedPane extends JTabbedPane {
 
         /**
          * 
          */
         private static final long serialVersionUID = 7014311238367882880L;
 
         
         @Override
         public void setSelectedIndex(int index) {
             setSelectedIndex(index, true);
         }
         
         /**
          * Apply some check rules if check is true
          *
          * @param index
          *            index to select
          * @param check
          *            flag whether to perform checks before setting the selected
          *            index
          */
         public void setSelectedIndex(int index, boolean check) {
             int oldSelectedIndex = this.getSelectedIndex();
             if(!check || oldSelectedIndex == -1) {
                 super.setSelectedIndex(index);
             }
             else if(index != oldSelectedIndex)
             {
                 // If there is no data, then switching between Parameters/file upload and Raw should be
                 // allowed with no further user interaction.
                 if(noData(oldSelectedIndex)) {
                     argsPanel.clear();
                     postBodyContent.setInitialText("");
                     if(showFileUploadPane) {
                         filesPanel.clear();
                     }
                     super.setSelectedIndex(index);
                 }
                 else {
                     boolean filePanelHasData = false;
                     if(showFileUploadPane) {
                         filePanelHasData = filesPanel.hasData();
                     }
                     
                     if(oldSelectedIndex == tabRawBodyIndex) {
                         
                         // If RAW data and Parameters match we allow switching
                         if(index == TAB_PARAMETERS && postBodyContent.getText().equals(computePostBody((Arguments)argsPanel.createTestElement()).trim())) {
                             super.setSelectedIndex(index);
                         }
                         else {
                             // If there is data in the Raw panel, then the user should be 
                             // prevented from switching (that would be easy to track).
                             JOptionPane.showConfirmDialog(this,
                                     JMeterUtils.getResString("web_cannot_switch_tab"), // $NON-NLS-1$
                                     JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                     JOptionPane.DEFAULT_OPTION, 
                                     JOptionPane.ERROR_MESSAGE);
                             return;
                         }
                     }
                     else {
                         // can switch from parameter to fileupload
                         if((oldSelectedIndex == TAB_PARAMETERS
                                 && index == tabFileUploadIndex)
                              || (oldSelectedIndex == tabFileUploadIndex
                                      && index == TAB_PARAMETERS)) {
                             super.setSelectedIndex(index);
                             return;
                         }
                         
                         // If the Parameter data can be converted (i.e. no names) and there is no data in file upload
                         // we warn the user that the Parameter data will be lost.
                         if(oldSelectedIndex == TAB_PARAMETERS && !filePanelHasData && canConvertParameters()) {
                             Object[] options = {
                                     JMeterUtils.getResString("confirm"), // $NON-NLS-1$
                                     JMeterUtils.getResString("cancel")}; // $NON-NLS-1$
                             int n = JOptionPane.showOptionDialog(this,
                                 JMeterUtils.getResString("web_parameters_lost_message"), // $NON-NLS-1$
                                 JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                 JOptionPane.QUESTION_MESSAGE,
                                 null,
                                 options,
                                 options[1]);
                             if(n == JOptionPane.YES_OPTION) {
                                 convertParametersToRaw();
                                 super.setSelectedIndex(index);
                             }
                             else{
                                 return;
                             }
                         }
                         else {
                             // If the Parameter data cannot be converted to Raw, then the user should be
                             // prevented from doing so raise an error dialog
                             String messageKey = filePanelHasData?"web_cannot_switch_tab":"web_cannot_convert_parameters_to_raw";
                             JOptionPane.showConfirmDialog(this,
                                     JMeterUtils.getResString(messageKey), // $NON-NLS-1$
                                     JMeterUtils.getResString("warning"), // $NON-NLS-1$
                                     JOptionPane.DEFAULT_OPTION, 
                                     JOptionPane.ERROR_MESSAGE);
                             return;
                         }
                     }
                 }
             }
         }   
     }
     // autoRedirects and followRedirects cannot both be selected
     @Override
     public void stateChanged(ChangeEvent e) {
         if (e.getSource() == autoRedirects){
             if (autoRedirects.isSelected()) {
                 followRedirects.setSelected(false);
             }
         }
         else if (e.getSource() == followRedirects){
             if (followRedirects.isSelected()) {
                 autoRedirects.setSelected(false);
             }
         }
         // disable the multi-part if not a post request
         else if(e.getSource() == method) {
             boolean isPostMethod = HTTPConstants.POST.equals(method.getText());
             useMultipartForPost.setEnabled(isPostMethod);    
         }
     }
 
 
     /**
      * Convert Parameters to Raw Body
      */
     void convertParametersToRaw() {
         postBodyContent.setInitialText(computePostBody((Arguments)argsPanel.createTestElement()));
         postBodyContent.setCaretPosition(0);
     }
 
     /**
      * 
      * @return true if no argument has a name
      */
     boolean canConvertParameters() {
         Arguments arguments = (Arguments) argsPanel.createTestElement();
         for (int i = 0; i < arguments.getArgumentCount(); i++) {
             if(!StringUtils.isEmpty(arguments.getArgument(i).getName())) {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * Checks if no data is available in the selected tab
      *
      * @param oldSelectedIndex the tab to check for data
      * @return true if neither Parameters tab nor Raw Body tab contain data
      */
     boolean noData(int oldSelectedIndex) {
         if(oldSelectedIndex == tabRawBodyIndex) {
             return StringUtils.isEmpty(postBodyContent.getText().trim());
         }
         else {
             boolean noData = true;
             Arguments element = (Arguments) argsPanel.createTestElement();
             
             if(showFileUploadPane) {
                 noData &= !filesPanel.hasData();
             }
             
             return noData && StringUtils.isEmpty(computePostBody(element));
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/HttpTestSampleGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/HttpTestSampleGui.java
index d6aede380..7b403a23d 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/HttpTestSampleGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/HttpTestSampleGui.java
@@ -1,276 +1,453 @@
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
 
 package org.apache.jmeter.protocol.http.control.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
+import java.awt.Font;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
+import javax.swing.JLabel;
 import javax.swing.JPanel;
+import javax.swing.JPasswordField;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextField;
+import javax.swing.UIManager;
 
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.config.gui.UrlConfigGui;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
+import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
+import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 //For unit tests, @see TestHttpTestSampleGui
 
 /**
  * HTTP Sampler GUI
  *
  */
 public class HttpTestSampleGui extends AbstractSamplerGui {
     
     private static final long serialVersionUID = 241L;
     
+    private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
+    
+    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));
+    
     private UrlConfigGui urlConfigGui;
 
     private JCheckBox retrieveEmbeddedResources;
     
     private JCheckBox concurrentDwn;
     
     private JTextField concurrentPool; 
 
     private JCheckBox useMD5;
 
     private JLabeledTextField embeddedRE; // regular expression used to match against embedded resource URLs
 
     private JTextField sourceIpAddr; // does not apply to Java implementation
     
     private JComboBox<String> sourceIpType = new JComboBox<>(HTTPSamplerBase.getSourceTypeList());
 
     private final boolean isAJP;
     
+    private JTextField proxyHost;
+
+    private JTextField proxyPort;
+
+    private JTextField proxyUser;
+
+    private JPasswordField proxyPass;
+    
+    private JLabeledChoice httpImplementation;
+
+    private JTextField connectTimeOut;
+
+    private JTextField responseTimeOut;
+
     public HttpTestSampleGui() {
         isAJP = false;
         init();
     }
 
     // For use by AJP
     protected HttpTestSampleGui(boolean ajp) {
         isAJP = ajp;
         init();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void configure(TestElement element) {
         super.configure(element);
         final HTTPSamplerBase samplerBase = (HTTPSamplerBase) element;
         urlConfigGui.configure(element);
         retrieveEmbeddedResources.setSelected(samplerBase.isImageParser());
         concurrentDwn.setSelected(samplerBase.isConcurrentDwn());
         concurrentPool.setText(samplerBase.getConcurrentPool());
         useMD5.setSelected(samplerBase.useMD5());
         embeddedRE.setText(samplerBase.getEmbeddedUrlRE());
         if (!isAJP) {
             sourceIpAddr.setText(samplerBase.getIpSource());
             sourceIpType.setSelectedIndex(samplerBase.getIpSourceType());
+            proxyHost.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
+            proxyPort.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
+            proxyUser.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
+            proxyPass.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
+            httpImplementation.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
+            connectTimeOut.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
+            responseTimeOut.setText(samplerBase.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
         }
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public TestElement createTestElement() {
         HTTPSamplerBase sampler = new HTTPSamplerProxy();
         modifyTestElement(sampler);
         return sampler;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      * <p>
      * {@inheritDoc}
      */
     @Override
     public void modifyTestElement(TestElement sampler) {
         sampler.clear();
         urlConfigGui.modifyTestElement(sampler);
         final HTTPSamplerBase samplerBase = (HTTPSamplerBase) sampler;
         samplerBase.setImageParser(retrieveEmbeddedResources.isSelected());
         enableConcurrentDwn(retrieveEmbeddedResources.isSelected());
         samplerBase.setConcurrentDwn(concurrentDwn.isSelected());
         samplerBase.setConcurrentPool(concurrentPool.getText());
         samplerBase.setMD5(useMD5.isSelected());
         samplerBase.setEmbeddedUrlRE(embeddedRE.getText());
         if (!isAJP) {
             samplerBase.setIpSource(sourceIpAddr.getText());
             samplerBase.setIpSourceType(sourceIpType.getSelectedIndex());
+            samplerBase.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
+            samplerBase.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
+            samplerBase.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
+            samplerBase.setProperty(HTTPSamplerBase.PROXYPASS, String.valueOf(proxyPass.getPassword()),"");
+            samplerBase.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getText(),"");
+            samplerBase.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
+            samplerBase.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
         }
         super.configureTestElement(sampler);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public String getLabelResource() {
         return "web_testing_title"; // $NON-NLS-1$
     }
 
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
 
         // URL CONFIG
-        urlConfigGui = new UrlConfigGui(true, !isAJP, true, true);
+        urlConfigGui = new UrlConfigGui(true, true, true);
         
         // AdvancedPanel (embedded resources, source address and optional tasks)
         JPanel advancedPanel = new VerticalPanel();
         advancedPanel.add(createEmbeddedRsrcPanel());
-        advancedPanel.add(createSourceAddrPanel());
+        if (!isAJP) {
+            advancedPanel.add(getTimeOutPanel());
+            advancedPanel.add(getImplementationPanel());
+            advancedPanel.add(createSourceAddrPanel());
+            advancedPanel.add(getProxyServerPanel());
+        }
+        
         advancedPanel.add(createOptionalTasksPanel());
         
         JTabbedPane tabbedPane = new JTabbedPane();
         tabbedPane.add(JMeterUtils
                 .getResString("web_testing_basic"), urlConfigGui);
         tabbedPane.add(JMeterUtils
                 .getResString("web_testing_advanced"), advancedPanel);
 
         JPanel emptyPanel = new JPanel();
         emptyPanel.setMaximumSize(new Dimension());
 
         add(makeTitlePanel(), BorderLayout.NORTH);
         add(tabbedPane, BorderLayout.CENTER);
         add(emptyPanel, BorderLayout.SOUTH);
     }
 
+    private JPanel getTimeOutPanel() {
+        JPanel timeOut = new HorizontalPanel();
+        timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
+                JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
+        final JPanel connPanel = getConnectTimeOutPanel();
+        final JPanel reqPanel = getResponseTimeOutPanel();
+        timeOut.add(connPanel);
+        timeOut.add(reqPanel);
+        return timeOut;
+    }
+    
+    private JPanel getConnectTimeOutPanel() {
+        connectTimeOut = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
+        label.setLabelFor(connectTimeOut);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(connectTimeOut, BorderLayout.CENTER);
+
+        return panel;
+    }
+
+    private JPanel getResponseTimeOutPanel() {
+        responseTimeOut = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
+        label.setLabelFor(responseTimeOut);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(responseTimeOut, BorderLayout.CENTER);
+
+        return panel;
+    }
+
     protected JPanel createEmbeddedRsrcPanel() {
         // retrieve Embedded resources
         retrieveEmbeddedResources = new JCheckBox(JMeterUtils.getResString("web_testing_retrieve_images")); // $NON-NLS-1$
         // add a listener to activate or not concurrent dwn.
         retrieveEmbeddedResources.addItemListener(new ItemListener() {
             @Override
             public void itemStateChanged(final ItemEvent e) {
                 if (e.getStateChange() == ItemEvent.SELECTED) { enableConcurrentDwn(true); }
                 else { enableConcurrentDwn(false); }
             }
         });
         // Download concurrent resources
         concurrentDwn = new JCheckBox(JMeterUtils.getResString("web_testing_concurrent_download")); // $NON-NLS-1$
         concurrentDwn.addItemListener(new ItemListener() {
             @Override
             public void itemStateChanged(final ItemEvent e) {
                 if (retrieveEmbeddedResources.isSelected() && e.getStateChange() == ItemEvent.SELECTED) { concurrentPool.setEnabled(true); }
                 else { concurrentPool.setEnabled(false); }
             }
         });
         concurrentPool = new JTextField(2); // 2 column size
         concurrentPool.setMinimumSize(new Dimension(10, (int) concurrentPool.getPreferredSize().getHeight()));
         concurrentPool.setMaximumSize(new Dimension(30, (int) concurrentPool.getPreferredSize().getHeight()));
 
         final JPanel embeddedRsrcPanel = new HorizontalPanel();
         embeddedRsrcPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("web_testing_retrieve_title"))); // $NON-NLS-1$
         embeddedRsrcPanel.add(retrieveEmbeddedResources);
         embeddedRsrcPanel.add(concurrentDwn);
         embeddedRsrcPanel.add(concurrentPool);
 
         // Embedded URL match regex
         embeddedRE = new JLabeledTextField(JMeterUtils.getResString("web_testing_embedded_url_pattern"),20); // $NON-NLS-1$
         embeddedRsrcPanel.add(embeddedRE);
         
         return embeddedRsrcPanel;
     }
 
+    /**
+     * Create a panel containing the implementation details
+     *
+     * @return the panel
+     */
+    protected final JPanel getImplementationPanel(){
+        JPanel implPanel = new HorizontalPanel();
+        httpImplementation = new JLabeledChoice(JMeterUtils.getResString("http_implementation"), // $NON-NLS-1$
+                HTTPSamplerFactory.getImplementations());
+        httpImplementation.addValue("");
+        implPanel.add(httpImplementation);
+        return implPanel;
+    }
+    
     protected JPanel createOptionalTasksPanel() {
         // OPTIONAL TASKS
         final JPanel checkBoxPanel = new VerticalPanel();
         checkBoxPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("optional_tasks"))); // $NON-NLS-1$
 
         // Use MD5
         useMD5 = new JCheckBox(JMeterUtils.getResString("response_save_as_md5")); // $NON-NLS-1$
         checkBoxPanel.add(useMD5);
 
         return checkBoxPanel;
     }
     
     protected JPanel createSourceAddrPanel() {
         final JPanel sourceAddrPanel = new HorizontalPanel();
         sourceAddrPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                 .getResString("web_testing_source_ip"))); // $NON-NLS-1$
 
-        if (!isAJP) {
-            // Add a new field source ip address (for HC implementations only)
-            sourceIpType.setSelectedIndex(HTTPSamplerBase.SourceType.HOSTNAME.ordinal()); //default: IP/Hostname
-            sourceAddrPanel.add(sourceIpType);
+        // Add a new field source ip address (for HC implementations only)
+        sourceIpType.setSelectedIndex(HTTPSamplerBase.SourceType.HOSTNAME.ordinal()); //default: IP/Hostname
+        sourceAddrPanel.add(sourceIpType);
 
-            sourceIpAddr = new JTextField();
-            sourceAddrPanel.add(sourceIpAddr);
-        }
+        sourceIpAddr = new JTextField();
+        sourceAddrPanel.add(sourceIpAddr);
         return sourceAddrPanel;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Dimension getPreferredSize() {
         return getMinimumSize();
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void clearGui() {
         super.clearGui();
         retrieveEmbeddedResources.setSelected(false);
         concurrentDwn.setSelected(false);
         concurrentPool.setText(String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE));
         enableConcurrentDwn(false);
         useMD5.setSelected(false);
         urlConfigGui.clear();
         embeddedRE.setText(""); // $NON-NLS-1$
         if (!isAJP) {
             sourceIpAddr.setText(""); // $NON-NLS-1$
             sourceIpType.setSelectedIndex(HTTPSamplerBase.SourceType.HOSTNAME.ordinal()); //default: IP/Hostname
+            proxyHost.setText(""); // $NON-NLS-1$
+            proxyPort.setText(""); // $NON-NLS-1$
+            proxyUser.setText(""); // $NON-NLS-1$
+            proxyPass.setText(""); // $NON-NLS-1$
+            httpImplementation.setText(""); // $NON-NLS-1$
+            connectTimeOut.setText(""); // $NON-NLS-1$
+            responseTimeOut.setText(""); // $NON-NLS-1$
         }
     }
     
     private void enableConcurrentDwn(boolean enable) {
         if (enable) {
             concurrentDwn.setEnabled(true);
             embeddedRE.setEnabled(true);
             if (concurrentDwn.isSelected()) {
                 concurrentPool.setEnabled(true);
             }
         } else {
             concurrentDwn.setEnabled(false);
             concurrentPool.setEnabled(false);
             embeddedRE.setEnabled(false);
         }
     }
+    
+
+    /**
+     * Create a panel containing the proxy server details
+     *
+     * @return the panel
+     */
+    protected final JPanel getProxyServerPanel(){
+        JPanel proxyServer = new HorizontalPanel();
+        proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
+        proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);
+
+        JPanel proxyLogin = new HorizontalPanel();
+        proxyLogin.add(getProxyUserPanel());
+        proxyLogin.add(getProxyPassPanel());
+
+        JPanel proxyServerPanel = new HorizontalPanel();
+        proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
+                JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
+        proxyServerPanel.add(proxyServer);
+        proxyServerPanel.add(proxyLogin);
+
+        return proxyServerPanel;
+    }
+
+    private JPanel getProxyHostPanel() {
+        proxyHost = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
+        label.setLabelFor(proxyHost);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyHost, BorderLayout.CENTER);
+        return panel;
+    }
+    
+    private JPanel getProxyPortPanel() {
+        proxyPort = new JTextField(10);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
+        label.setLabelFor(proxyPort);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyPort, BorderLayout.CENTER);
+
+        return panel;
+    }
+
+    private JPanel getProxyUserPanel() {
+        proxyUser = new JTextField(5);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
+        label.setLabelFor(proxyUser);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyUser, BorderLayout.CENTER);
+        return panel;
+    }
+
+    private JPanel getProxyPassPanel() {
+        proxyPass = new JPasswordField(5);
+
+        JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
+        label.setLabelFor(proxyPass);
+        label.setFont(FONT_SMALL);
+
+        JPanel panel = new JPanel(new BorderLayout(5, 0));
+        panel.add(label, BorderLayout.WEST);
+        panel.add(proxyPass, BorderLayout.CENTER);
+        return panel;
+    }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 9d5ae197f..b521a04b6 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,304 +1,305 @@
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
 Fill in some detail.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>Documentation review and improvements for easier startup</li>
 <li>New <a href="usermanual/properties_reference.html">properties reference</a> documentation section</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
+    <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize and Maxime Chassagneux</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to xxx-1.1 (from 0.2)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
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
 <li>Logan Mauzaize (https://github.com/loganmzz)</li>
 <li>Maxime Chassagneux (https://github.com/max3163)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
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
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <source>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </source>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
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
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry.
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
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
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later.
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
