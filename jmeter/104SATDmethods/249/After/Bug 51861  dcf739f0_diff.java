diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index f241dffbd..bf8897581 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,723 +1,744 @@
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
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JPasswordField;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.commons.lang.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextArea;
 
 /**
  * Basic URL / HTTP Request configuration:
  * - host and port
  * - connect and response timeouts
  * - path, method, encoding, parameters
  * - redirects & keepalive
  */
 public class UrlConfigGui extends JPanel implements ChangeListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final int TAB_PARAMETERS = 0;
     
     private static final int TAB_RAW_BODY = 1;
 
     private HTTPArgumentsPanel argsPanel;
 
     private JTextField domain;
 
     private JTextField port;
 
     private JTextField proxyHost;
 
     private JTextField proxyPort;
 
     private JTextField proxyUser;
 
     private JPasswordField proxyPass;
 
     private JTextField connectTimeOut;
 
     private JTextField responseTimeOut;
 
     private JTextField protocol;
 
     private JTextField contentEncoding;
 
     private JTextField path;
 
     private JCheckBox followRedirects;
 
     private JCheckBox autoRedirects;
 
     private JCheckBox useKeepAlive;
 
     private JCheckBox useMultipartForPost;
 
     private JCheckBox useBrowserCompatibleMultipartMode;
 
     private JLabeledChoice method;
     
     private JLabeledChoice httpImplementation;
 
     private final boolean notConfigOnly;
     // set this false to suppress some items for use in HTTP Request defaults
     
     private final boolean showImplementation; // Set false for AJP
 
     // Raw POST Body 
     private JLabeledTextArea postBodyContent;
 
     // Tabbed pane that contains parameters and raw body
     private ValidationTabbedPane postContentTabbedPane;
 
     private boolean showRawBodyPane;
 
     public UrlConfigGui() {
         this(true);
     }
 
     /**
      * @param showSamplerFields
      */
     public UrlConfigGui(boolean showSamplerFields) {
         this(showSamplerFields, true, true);
     }
 
     /**
      * @param showSamplerFields
      * @param showImplementation Show HTTP Implementation
      * @param showRawBodyPane 
      */
     public UrlConfigGui(boolean showSamplerFields, boolean showImplementation, boolean showRawBodyPane) {
         notConfigOnly=showSamplerFields;
         this.showImplementation = showImplementation;
         this.showRawBodyPane = showRawBodyPane;
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
         if (showImplementation) {
             httpImplementation.setText(""); // $NON-NLS-1$
         }
         path.setText(""); // $NON-NLS-1$
         port.setText(""); // $NON-NLS-1$
         proxyHost.setText(""); // $NON-NLS-1$
         proxyPort.setText(""); // $NON-NLS-1$
         proxyUser.setText(""); // $NON-NLS-1$
         proxyPass.setText(""); // $NON-NLS-1$
         connectTimeOut.setText(""); // $NON-NLS-1$
         responseTimeOut.setText(""); // $NON-NLS-1$
         protocol.setText(""); // $NON-NLS-1$
         contentEncoding.setText(""); // $NON-NLS-1$
         argsPanel.clear();
         if(showRawBodyPane) {
             postBodyContent.setText("");// $NON-NLS-1$
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
      * @param element
      */
     public void modifyTestElement(TestElement element) {
         boolean useRaw = postContentTabbedPane.getSelectedIndex()==TAB_RAW_BODY;
         Arguments args;
         if(useRaw) {
             args = new Arguments();
-            HTTPArgument arg = new HTTPArgument("", postBodyContent.getText(), true);
+            String text = postBodyContent.getText();
+            /*
+             * Textfield uses \n (LF) to delimit lines; we need to send CRLF.
+             * Rather than change the way that arguments are processed by the
+             * samplers for raw data, it is easier to fix the data.
+             * On retrival, CRLF is converted back to LF for storage in the text field.
+             * See
+             */
+            HTTPArgument arg = new HTTPArgument("", text.replaceAll("\n","\r\n"), true);
             arg.setAlwaysEncoded(false);
             args.addArgument(arg);
         } else {
             args = (Arguments) argsPanel.createTestElement();
             HTTPArgument.convertArgumentsToHTTP(args);
         }
         element.setProperty(HTTPSamplerBase.POST_BODY_RAW, useRaw, HTTPSamplerBase.POST_BODY_RAW_DEFAULT);
         element.setProperty(new TestElementProperty(HTTPSamplerBase.ARGUMENTS, args));
         element.setProperty(HTTPSamplerBase.DOMAIN, domain.getText());
         element.setProperty(HTTPSamplerBase.PORT, port.getText());
         element.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPASS, String.valueOf(proxyPass.getPassword()),"");
         element.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
         element.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
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
         if (showImplementation) {
             element.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getText(),"");
         }
     }
 
     // FIXME FACTOR WITH HTTPHC4Impl, HTTPHC3Impl
     // Just append all the parameter values, and use that as the post body
     /**
      * Compute Post body from arguments
      * @param arguments {@link Arguments}
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments) {
+        return computePostBody(arguments, false);
+    }
+
+    /**
+     * Compute Post body from arguments
+     * @param arguments {@link Arguments}
+     * @param crlfToLF whether to convert CRLF to LF
+     * @return {@link String}
+     */
+    private static final String computePostBody(Arguments arguments, boolean crlfToLF) {
         StringBuilder postBody = new StringBuilder();
         PropertyIterator args = arguments.iterator();
         while (args.hasNext()) {
             HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
             String value = arg.getValue();
+            if (crlfToLF) {
+                value=value.replaceAll("\r\n", "\n"); // See modifyTestElement
+            }
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
-            String postBody = computePostBody(arguments);
+            String postBody = computePostBody(arguments, true); // Convert CRLF to CR, see modifyTestElement
             postBodyContent.setText(postBody);   
             postContentTabbedPane.setSelectedIndex(TAB_RAW_BODY, false);
         } else {
             argsPanel.configure(arguments);
             postContentTabbedPane.setSelectedIndex(TAB_PARAMETERS, false);
         }
 
         domain.setText(el.getPropertyAsString(HTTPSamplerBase.DOMAIN));
 
         String portString = el.getPropertyAsString(HTTPSamplerBase.PORT);
 
         // Only display the port number if it is meaningfully specified
         if (portString.equals(HTTPSamplerBase.UNSPECIFIED_PORT_AS_STRING)) {
             port.setText(""); // $NON-NLS-1$
         } else {
             port.setText(portString);
         }
         proxyHost.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
         proxyPort.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
         proxyUser.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
         proxyPass.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
         connectTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
         responseTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
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
         if (showImplementation) {
             httpImplementation.setText(el.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
         }
     }
 
     private void init() {// called from ctor, so must not be overridable
         this.setLayout(new BorderLayout());
 
         // WEB REQUEST PANEL
         JPanel webRequestPanel = new JPanel();
         webRequestPanel.setLayout(new BorderLayout());
         webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_request"))); // $NON-NLS-1$
 
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
         northPanel.add(getProtocolAndMethodPanel());
         northPanel.add(getPathPanel());
 
         webRequestPanel.add(northPanel, BorderLayout.NORTH);
         webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);
 
         this.add(getWebServerTimeoutPanel(), BorderLayout.NORTH);
         this.add(webRequestPanel, BorderLayout.CENTER);
         this.add(getProxyServerPanel(), BorderLayout.SOUTH);
     }
 
     /**
      * Create a panel containing the webserver (domain+port) and timeouts (connect+request).
      *
      * @return the panel
      */
     protected final JPanel getWebServerTimeoutPanel() {
         // WEB SERVER PANEL
         JPanel webServerPanel = new HorizontalPanel();
         webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
         final JPanel domainPanel = getDomainPanel();
         final JPanel portPanel = getPortPanel();
         webServerPanel.add(domainPanel, BorderLayout.CENTER);
         webServerPanel.add(portPanel, BorderLayout.EAST);
 
         JPanel timeOut = new HorizontalPanel();
         timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
         final JPanel connPanel = getConnectTimeOutPanel();
         final JPanel reqPanel = getResponseTimeOutPanel();
         timeOut.add(connPanel);
         timeOut.add(reqPanel);
 
         JPanel webServerTimeoutPanel = new VerticalPanel();
         webServerTimeoutPanel.add(webServerPanel, BorderLayout.CENTER);
         webServerTimeoutPanel.add(timeOut, BorderLayout.EAST);
 
         JPanel bigPanel = new VerticalPanel();
         bigPanel.add(webServerTimeoutPanel);
         return bigPanel;
     }
 
     /**
      * Create a panel containing the proxy server details
      *
      * @return the panel
      */
     protected final JPanel getProxyServerPanel(){
         JPanel proxyServer = new HorizontalPanel();
         proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
         proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);
 
         JPanel proxyLogin = new HorizontalPanel();
         proxyLogin.add(getProxyUserPanel());
         proxyLogin.add(getProxyPassPanel());
 
         JPanel proxyServerPanel = new HorizontalPanel();
         proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
         proxyServerPanel.add(proxyServer, BorderLayout.CENTER);
         proxyServerPanel.add(proxyLogin, BorderLayout.EAST);
 
         return proxyServerPanel;
     }
 
     private JPanel getPortPanel() {
         port = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(port);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(port, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getProxyPortPanel() {
         proxyPort = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(proxyPort);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPort, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getConnectTimeOutPanel() {
         connectTimeOut = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
         label.setLabelFor(connectTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(connectTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getResponseTimeOutPanel() {
         responseTimeOut = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
         label.setLabelFor(responseTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(responseTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getDomainPanel() {
         domain = new JTextField(20);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(domain);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(domain, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyHostPanel() {
         proxyHost = new JTextField(20);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(proxyHost);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyHost, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyUserPanel() {
         proxyUser = new JTextField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
         label.setLabelFor(proxyUser);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyUser, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyPassPanel() {
         proxyPass = new JPasswordField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
         label.setLabelFor(proxyPass);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPass, BorderLayout.CENTER);
         return panel;
     }
 
     /**
      * This method defines the Panel for the HTTP path, 'Follow Redirects'
      * 'Use KeepAlive', and 'Use multipart for HTTP POST' elements.
      *
      * @return JPanel The Panel for the path, 'Follow Redirects' and 'Use
      *         KeepAlive' elements.
      */
     protected Component getPathPanel() {
         path = new JTextField(15);
 
         JLabel label = new JLabel(JMeterUtils.getResString("path")); //$NON-NLS-1$
         label.setLabelFor(path);
 
         if (notConfigOnly){
             followRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects")); // $NON-NLS-1$
             followRedirects.setSelected(true);
             followRedirects.addChangeListener(this);
 
             autoRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects_auto")); //$NON-NLS-1$
             autoRedirects.addChangeListener(this);
             autoRedirects.setSelected(false);// Default changed in 2.3 and again in 2.4
 
             useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
             useKeepAlive.setSelected(true);
 
             useMultipartForPost = new JCheckBox(JMeterUtils.getResString("use_multipart_for_http_post")); // $NON-NLS-1$
             useMultipartForPost.setSelected(false);
 
             useBrowserCompatibleMultipartMode = new JCheckBox(JMeterUtils.getResString("use_multipart_mode_browser")); // $NON-NLS-1$
             useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
 
         }
 
         JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
         pathPanel.add(label, BorderLayout.WEST);
         pathPanel.add(path, BorderLayout.CENTER);
         pathPanel.setMinimumSize(pathPanel.getPreferredSize());
 
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add(pathPanel);
         if (notConfigOnly){
             JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
 
     protected JPanel getProtocolAndMethodPanel() {
 
         // Implementation
         
         if (showImplementation) {
             httpImplementation = new JLabeledChoice(JMeterUtils.getResString("http_implementation"), // $NON-NLS-1$
                     HTTPSamplerFactory.getImplementations());
             httpImplementation.addValue("");
         }
         // PROTOCOL
         protocol = new JTextField(4);
         JLabel protocolLabel = new JLabel(JMeterUtils.getResString("protocol")); // $NON-NLS-1$
         protocolLabel.setLabelFor(protocol);        
         
         // CONTENT_ENCODING
         contentEncoding = new JTextField(10);
         JLabel contentEncodingLabel = new JLabel(JMeterUtils.getResString("content_encoding")); // $NON-NLS-1$
         contentEncodingLabel.setLabelFor(contentEncoding);
 
         if (notConfigOnly){
             method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
                     HTTPSamplerBase.getValidMethodsAsArray());
         }
 
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
 
         if (showImplementation) {
             panel.add(httpImplementation);
         }
         panel.add(protocolLabel);
         panel.add(protocol);
         panel.add(Box.createHorizontalStrut(5));
 
         if (notConfigOnly){
             panel.add(method);
         }
         panel.setMinimumSize(panel.getPreferredSize());
         panel.add(Box.createHorizontalStrut(5));
 
         panel.add(contentEncodingLabel);
         panel.add(contentEncoding);
         panel.setMinimumSize(panel.getPreferredSize());
         return panel;
     }
 
     protected JTabbedPane getParameterPanel() {
         postContentTabbedPane = new ValidationTabbedPane();
         argsPanel = new HTTPArgumentsPanel();
         postContentTabbedPane.add(JMeterUtils.getResString("post_as_parameters"), argsPanel);// $NON-NLS-1$
         if(showRawBodyPane) {
             postBodyContent = new JLabeledTextArea(JMeterUtils.getResString("post_body_raw"));// $NON-NLS-1$
             postContentTabbedPane.add(JMeterUtils.getResString("post_body"), postBodyContent);// $NON-NLS-1$
         }
         return postContentTabbedPane;
     }
 
     /**
      * 
      */
     class ValidationTabbedPane extends JTabbedPane{
 
         /**
          * 
          */
         private static final long serialVersionUID = 7014311238367882880L;
 
         /* (non-Javadoc)
          * @see javax.swing.JTabbedPane#setSelectedIndex(int)
          */
         @Override
         public void setSelectedIndex(int index) {
             setSelectedIndex(index, true);
         }
         /**
          * Apply some check rules if check is true
          */
         public void setSelectedIndex(int index, boolean check) {
             int oldSelectedIndex = getSelectedIndex();
             if(!check || oldSelectedIndex==-1) {
                 super.setSelectedIndex(index);
             }
             else if(index != this.getSelectedIndex())
             {
                 if(noData(getSelectedIndex())) {
                     // If there is no data, then switching between Parameters and Raw should be
                     // allowed with no further user interaction.
                     argsPanel.clear();
                     postBodyContent.setText("");
                     super.setSelectedIndex(index);
                 }
                 else { 
                     if(oldSelectedIndex == TAB_RAW_BODY) {
                         // If RAW data and Parameters match we allow switching
                         if(postBodyContent.getText().equals(computePostBody((Arguments)argsPanel.createTestElement()).trim())) {
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
                         // If the Parameter data can be converted (i.e. no names), we 
                         // warn the user that the Parameter data will be lost.
                         if(canConvertParameters()) {
                             Object[] options = {
                                     JMeterUtils.getResString("confirm"),
                                     JMeterUtils.getResString("cancel")};
                             int n = JOptionPane.showOptionDialog(this,
                                 JMeterUtils.getResString("web_parameters_lost_message"),
                                 JMeterUtils.getResString("warning"),
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
                             JOptionPane.showConfirmDialog(this,
                                     JMeterUtils.getResString("web_cannot_convert_parameters_to_raw"), // $NON-NLS-1$
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
     public void stateChanged(ChangeEvent e) {
         if (e.getSource() == autoRedirects){
             if (autoRedirects.isSelected()) {
                 followRedirects.setSelected(false);
             }
         }
         if (e.getSource() == followRedirects){
             if (followRedirects.isSelected()) {
                 autoRedirects.setSelected(false);
             }
         }
     }
 
 
     /**
      * Convert Parameters to Raw Body
      */
     void convertParametersToRaw() {
         postBodyContent.setText(computePostBody((Arguments)argsPanel.createTestElement()));
     }
 
     /**
      * 
      * @return true if no argument has a name
      */
     boolean canConvertParameters() {
         Arguments arguments = (Arguments)argsPanel.createTestElement();
         for (int i = 0; i < arguments.getArgumentCount(); i++) {
             if(!StringUtils.isEmpty(arguments.getArgument(i).getName())) {
                 return false;
             }
         }
         return true;
     }
 
     /**
      * @return true if neither Parameters tab nor Raw Body tab contain data
      */
     boolean noData(int oldSelectedIndex) {
         if(oldSelectedIndex == TAB_RAW_BODY) {
             return StringUtils.isEmpty(postBodyContent.getText().trim());
         }
         else {
             Arguments element = (Arguments)argsPanel.createTestElement();
             return StringUtils.isEmpty(computePostBody(element));
         }
     }
 }
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index d7ac8f3a5..9d767bc72 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -1,1348 +1,1355 @@
 <?xml version="1.0"?>
 <!-- 
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
  -->
 <!DOCTYPE document
 [
 <!ENTITY sect-num '18'>
 ]>
 <document index="yes" index-level-2="yes" index-numbers="no" colbreak="&sect-num;.4"
   prev="boss.html" next="functions.html" id="$Id$">
 
 <properties>
   <title>User's Manual: Component Reference</title>
 </properties>
 
 <body>
 
 <!--
 	Because this is an XML document, all tags must be properly closed, including ones
 	which are passed unchanged into the HTML output, e.g. <br/>, not just <br>.
 	
 	Unfortunately Java does not currently allow for this - it outputs the trailing > -
 	which messes up the Help display. 
 	To avoid these artefacts, use the form <br></br>, which Java does seem to handle OK.
 
  -->
 <section name="&sect-num;.0 Introduction" anchor="introduction">
 <description>
 <p>
 
 </p>
  <note>
  Several test elements use JMeter properties to control their behaviour.
  These properties are normally resolved when the class is loaded.
  This generally occurs before the test plan starts, so it's not possible to change the settings by using the __setProperty() function.
 </note>
 <p>
 </p>
 </description>
 </section>
  
 <section name="&sect-num;.1 Samplers" anchor="samplers">
 <description>
 	<p>
 	Samplers perform the actual work of JMeter.
 	Each sampler (except Test Action) generates one or more sample results.
 	The sample results have various attributes (success/fail, elapsed time, data size etc) and can be viewed in the various listeners.
 	</p>
 </description>
 <component name="FTP Request" index="&sect-num;.1.1" width="499" height="292" screenshot="ftptest/ftp-request.png">
 <description>
 This controller lets you send an FTP "retrieve file" or "upload file" request to an FTP server.
 If you are going to send multiple requests to the same FTP server, consider
 using a <complink name="FTP Request Defaults"/> Configuration
 Element so you do not have to enter the same information for each FTP Request Generative
 Controller. When downloading a file, it can be stored on disk (Local File) or in the Response Data, or both.
 <p>
 Latency is set to the time it takes to login (versions of JMeter after 2.3.1).
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server Name or IP" required="Yes">Domain name or IP address of the FTP server.</property>
         <property name="Port" required="No">Port to use. If this is  >0, then this specific port is used, otherwise JMeter uses the default FTP port.</property>
         <property name="Remote File:" required="Yes">File to retrieve or name of destination file to upload.</property>
         <property name="Local File:" required="Yes, if uploading (*)">File to upload, or destination for downloads (defaults to remote file name).</property>
         <property name="Local File Contents:" required="Yes, if uploading (*)">Provides the contents for the upload, overrides the Local File property.</property>
         <property name="get(RETR) / put(STOR)" required="Yes">Whether to retrieve or upload a file.</property>
         <property name="Use Binary mode ?" required="Yes">Check this to use Binary mode (default Ascii)</property>
         <property name="Save File in Response ?" required="Yes, if downloading">
         Whether to store contents of retrieved file in response data.
         If the mode is Ascii, then the contents will be visible in the Tree View Listener.
         </property>
         <property name="Username" required="Usually">FTP account username.</property>
         <property name="Password" required="Usually">FTP account password. N.B. This will be visible in the test plan.</property>
 </properties>
 <links>
         <link href="test_plan.html#assertions">Assertions</link>
         <complink name="FTP Request Defaults"/>
         <link href="build-ftp-test-plan.html">Building an FTP Test Plan</link>
 </links>
 
 </component>
 
 <component name="HTTP Request" index="&sect-num;.1.2" width="959" height="828" screenshot="http-request.png">
 
 <description>
         <p>This sampler lets you send an HTTP/HTTPS request to a web server.  It
         also lets you control whether or not JMeter parses HTML files for images and
         other embedded resources and sends HTTP requests to retrieve them.
         The following types of embedded resource are retrieved:</p>
         <ul>
         <li>images</li>
         <li>applets</li>
         <li>stylesheets</li>
         <li>external scripts</li>
         <li>frames, iframes</li>
         <li>background images (body, table, TD, TR)</li>
         <li>background sound</li>
         </ul>
         <p>
         The default parser is htmlparser.
         This can be changed by using the property "htmlparser.classname" - see jmeter.properties for details.
         </p>
         <p>If you are going to send multiple requests to the same web server, consider
         using an <complink name="HTTP Request Defaults"/>
         Configuration Element so you do not have to enter the same information for each
         HTTP Request.</p>
 
         <p>Or, instead of manually adding HTTP Requests, you may want to use
         JMeter's <complink name="HTTP Proxy Server"/> to create
         them.  This can save you time if you have a lot of HTTP requests or requests with many
         parameters.</p>
 
         <p><b>There are two different screens for defining the samplers:</b>
         <ul>
         <li>AJP/1.3 Sampler - uses the Tomcat mod_jk protocol (allows testing of Tomcat in AJP mode without needing Apache httpd)
         The AJP Sampler does not support multiple file upload; only the first file will be used.
         </li>
         <li>HTTP Request - this has an implementation drop-down box, which selects the HTTP protocol implementation to be used:</li>
         <ul>
         <li>Java - uses the HTTP implementation provided by the JVM. 
         This has some limitations in comparison with the HttpClient implementations - see below.</li>
         <li>HTTPClient3.1 - uses Apache Commons HttpClient 3.1. 
         This is no longer being developed, and support for this may be dropped in a future JMeter release.</li>
         <li>HTTPClient4 - uses Apache HttpComponents HttpClient 4.x.</li>
         </ul>
         </ul>
          </p>
          <p>The Java HTTP implementation has some limitations:</p>
          <ul>
          <li>There is no control over how connections are re-used. 
          When a connection is released by JMeter, it may or may not be re-used by the same thread.</li>
          <li>The API is best suited to single-threaded usage - various settings (e.g. proxy) 
          are defined via system properties, and therefore apply to all connections.</li>
          <li>There is a bug in the handling of HTTPS via a Proxy (the CONNECT is not handled correctly).
          See Java bugs 6226610 and 6208335.
          </li>
          <li>It does not support virtual hosts.</li>
          </ul>
          <p>Note: the FILE protocol is intended for testing puposes only. 
          It is handled by the same code regardless of which HTTP Sampler is used.</p>
         <p>If the request requires server or proxy login authorization (i.e. where a browser would create a pop-up dialog box),
          you will also have to add an <complink name="HTTP Authorization Manager"/> Configuration Element.
          For normal logins (i.e. where the user enters login information in a form), you will need to work out what the form submit button does,
          and create an HTTP request with the appropriate method (usually POST) 
          and the appropriate parameters from the form definition. 
          If the page uses HTTP, you can use the JMeter Proxy to capture the login sequence.
         </p>
         <p>
         In versions of JMeter up to 2.2, only a single SSL context was used for all threads and samplers.
         This did not generate the proper load for multiple users.
         A separate SSL context is now used for each thread.
         To revert to the original behaviour, set the JMeter property:
 <pre>
 https.sessioncontext.shared=true
 </pre>
         By default, the SSL context is retained for the duration of the test.
         In versions of JMeter from 2.5.1, the SSL session can be optionally reset for each test iteration.
         To enable this, set the JMeter property:
 <pre>
 https.use.cached.ssl.context=false
 </pre>
         Note: this does not apply to the Java HTTP implementation.
         </p>
         <p>
         JMeter defaults to the SSL protocol level TLS.
         If the server needs a different level, e.g. SSLv3, change the JMeter property, for example:
 <pre>
 https.default.protocol=SSLv3
 </pre> 
         </p>
         <p>
         JMeter also allows one to enable additional protocols, by changing the property <tt>https.socket.protocols</tt>.
         </p>
         <p>If the request uses cookies, then you will also need an
         <complink name="HTTP Cookie Manager"/>.  You can
         add either of these elements to the Thread Group or the HTTP Request.  If you have
         more than one HTTP Request that needs authorizations or cookies, then add the
         elements to the Thread Group.  That way, all HTTP Request controllers will share the
         same Authorization Manager and Cookie Manager elements.</p>
 
         <p>If the request uses a technique called "URL Rewriting" to maintain sessions,
         then see section
         <a href="build-adv-web-test-plan.html#session_url_rewriting">6.1 Handling User Sessions With URL Rewriting</a>
         for additional configuration steps.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="Yes, unless provided by HTTP Request Defaults">
             Domain name or IP address of the web server. e.g. www.example.com. [Do not include the http:// prefix.]
             Note: in JMeter 2.5 (and later) if the "Host" header is defined in a Header Manager, then this will be used
             as the virtual host name.
         </property>
         <property name="Port" required="No">Port the web server is listening to. Default: 80</property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response.</property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the http:// prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server.</property>
         <property name="Implementation" required="No">Java, HttpClient3.1, HttpClient4. 
         If not specified (and not defined by HTTP Request Defaults), the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the Java implementation is used.</property>
         <property name="Protocol" required="No">HTTP, HTTPS or FILE. Default: HTTP</property>
         <property name="Method" required="Yes">GET, POST, HEAD, TRACE, OPTIONS, PUT, DELETE</property>
         <property name="Content Encoding" required="No">Content encoding to be used (for POST and FILE)</property>
 		<property name="Redirect Automatically" required="No">
 		Sets the underlying http protocol handler to automatically follow redirects,
 		so they are not seen by JMeter, and thus will not appear as samples.
 		Should only be used for GET and HEAD requests.
 		The HttpClient sampler will reject attempts to use it for POST or PUT.
 		<b>Warning: see below for information on cookie and header handling.</b>
         </property>
 		<property name="Follow Redirects" required="No">
 		This only has any effect if "Redirect Automatically" is not enabled.
 		If set, the JMeter sampler will check if the response is a redirect and follow it if so.
 		The initial redirect and further responses will appear as additional samples.
         The URL and data fields of the parent sample will be taken from the final (non-redirected)
         sample, but the parent byte count and elapsed time include all samples.
         The latency is taken from the initial response (versions of JMeter after 2.3.4 - previously it was zero).
 		Note that the HttpClient sampler may log the following message:<br/>
 		"Redirect requested but followRedirects is disabled"<br/>
 		This can be ignored.
         <br/>
         In versions after 2.3.4, JMeter will collapse paths of the form '/../segment' in
         both absolute and relative redirect URLs. For example http://host/one/../two => http://host/two.
         If necessary, this behaviour can be suppressed by setting the JMeter property
         <code>httpsampler.redirect.removeslashdotdot=false</code>
 		</property>
 		<property name="Use KeepAlive" required="No">JMeter sets the Connection: keep-alive header. This does not work properly with the default HTTP implementation, as connection re-use is not under user-control. 
                   It does work with the Apache HttpComponents HttpClient implementations.</property>
         <property name="Use multipart/form-data for HTTP POST" required="No">
         Use a multipart/form-data or application/x-www-form-urlencoded post request
         </property>
         <property name="Browser-compatible headers" required="No">
         When using multipart/form-data, this suppresses the Content-Type and 
         Content-Transfer-Encoding headers; only the Content-Disposition header is sent.
         </property>
         <property name="Path" required="Yes">The path to resource (for example, /servlets/myServlet). If the
 resource requires query string parameters, add them below in the
 "Send Parameters With the Request" section.
 <b>
 As a special case, if the path starts with "http://" or "https://" then this is used as the full URL.
 </b>
 In this case, the server, port and protocol are ignored; parameters are also ignored for GET and DELETE methods.
 </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>, the options to encode the parameter, and an option to include or exclude an equals sign (some applications
         don't expect an equals when the value is the empty string).  The query string will be generated in the correct fashion, depending on
         the choice of "Method" you made (ie if you chose GET or DELETE, the query string will be
         appended to the URL, if POST or PUT, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.
         <b>See below for some further information on parameter handling.</b>
         <p>
         Additionally, you can specify whether each parameter should be URL encoded.  If you are not sure what this
         means, it is probably best to select it.  If your values contain characters such as &amp;amp; or spaces, or
         question marks, then encoding is usually required.</p></property>
         <property name="File Path:" required="No">Name of the file to send.  If left blank, JMeter
         does not send a file, if filled in, JMeter automatically sends the request as
         a multipart form request.
         <p>
         If it is a POST or PUT request and there is a single file whose 'name' attribute (below) is omitted, 
         then the file is sent as the entire body
         of the request, i.e. no wrappers are added. This allows arbitrary bodies to be sent. This functionality is present for POST requests
         after version 2.2, and also for PUT requests after version 2.3.
         <b>See below for some further information on parameter handling.</b>
         </p>
         </property>
         <property name="Parameter name:" required="No">Value of the "name" web request parameter.</property>
         <property name="MIME Type" required="No">MIME type (for example, text/plain).
         If it is a POST or PUT request and either the 'name' atribute (below) are omitted or the request body is
         constructed from parameter values only, then the value of this field is used as the value of the
         content-type request header.
         </property>
         <property name="Retrieve All Embedded Resources from HTML Files" required="No">Tell JMeter to parse the HTML file
 and send HTTP/HTTPS requests for all images, Java applets, JavaScript files, CSSs, etc. referenced in the file.
         See below for more details.
         </property>
         <property name="Use as monitor" required="No">For use with the <complink name="Monitor Results"/> listener.</property>
        <property name="Save response as MD5 hash?" required="No">
        If this is selected, then the response is not stored in the sample result.
        Instead, the 32 character MD5 hash of the data is calculated and stored instead.
        This is intended for testing large amounts of data.
        </property>
         <property name="Embedded URLs must match:" required="No">
         If present, this must be a regular expression that is used to match against any embedded URLs found.
         So if you only want to download embedded resources from http://example.com/, use the expression:
         http://example\.com/.*
         </property>
         <property name="Use concurrent pool" required="No">Use a pool of concurrent connections to get embedded resources.</property>
         <property name="Size" required="No">Pool size for concurrent connections used to get embedded resources.</property>
         <property name="Source IP address:" required="No">
         [Only for HTTP Request HTTPClient] 
         Override the default local IP address for this sample.
         The JMeter host must have multiple IP addresses (i.e. IP aliases or network interfaces). 
         If the property <b>httpclient.localaddress</b> is defined, that is used for all HttpClient requests.
         </property>
 </properties>
 <p>
 <b>N.B.</b> when using Automatic Redirection, cookies are only sent for the initial URL.
 This can cause unexpected behaviour for web-sites that redirect to a local server.
 E.g. if www.example.com redirects to www.example.co.uk.
 In this case the server will probably return cookies for both URLs, but JMeter will only see the cookies for the last
 host, i.e. www.example.co.uk. If the next request in the test plan uses www.example.com, 
 rather than www.example.co.uk, it will not get the correct cookies.
 Likewise, Headers are sent for the initial request, and won't be sent for the redirect.
 This is generally only a problem for manually created test plans,
 as a test plan created using a recorder would continue from the redirected URL.
 </p>
 <p>
 <b>Parameter Handling:</b><br></br>
 For the POST and PUT method, if there is no file to send, and the name(s) of the parameter(s) are omitted,
 then the body is created by concatenating all the value(s) of the parameters.
+Note that the values are concatenated without adding any end-of-line characters.
+These can be added by using the __char() function in the value fields.
 This allows arbitrary bodies to be sent.
 The values are encoded if the encoding flag is set (versions of JMeter after 2.3).
 See also the MIME Type above how you can control the content-type request header that is sent.
 <br></br>
 For other methods, if the name of the parameter is missing,
 then the parameter is ignored. This allows the use of optional parameters defined by variables.
 (versions of JMeter after 2.3)
 </p>
 <br/>
-<p>Since JMeter 2.5.2, you have the option to switch to Post Body when a request has only one unnamed parameter.
-This option is useful in the following cases:
+<p>Since JMeter 2.5.2, you have the option to switch to Post Body when a request has only unnamed parameters
+(or no parameters at all).
+This option is useful in the following cases (amongst others):
 <ul>
 <li>GWT RPC HTTP Request</li>
 <li>JSON REST HTTP Request</li>
 <li>XML REST HTTP Request</li>
-<li>...</li>
 </ul>
-Note that once you leave Tree node, you cannot switch anymore to parameters tab.
+Note that once you leave the Tree node, you cannot switch back to the parameter tab unless you clear the Post Body tab of data.
+</p>
+<p>
+In Post Body mode, each line will be sent with CRLF appended, apart from the last line.
+To send a CRLF after the last line of data, just ensure that there is an empty line following it.
+(This cannot be seen, except by noting whether the cursor can be placed on the subsequent line.)
 </p>
 <figure width="956" height="830" image="http-request-raw-single-parameter.png">Figure 1 - HTTP Request with one unnamed parameter</figure>
 <figure width="958" height="833" image="http-request-confirm-raw-body.png">Figure 2 - Confirm dialog to switch</figure>
 <figure width="957" height="831" image="http-request-raw-body.png">Figure 3 - HTTP Request using RAW Post body</figure>
 
 <p>
 <b>Method Handling:</b><br></br>
 The POST and PUT request methods work similarly, except that the PUT method does not support multipart requests.
 The PUT method body must be provided as one of the following:
 <ul>
 <li>define the body as a file</li>
 <li>define the body as parameter value(s) with no name</li>
 </ul>
 If you define any parameters with a name in either the sampler or Http
 defaults then nothing is sent.
 The GET and DELETE request methods work similarly to each other.
 </p>
 <p>Upto and including JMeter 2.1.1, only responses with the content-type "text/html" were scanned for
 embedded resources. Other content-types were assumed to be something other than HTML.
 JMeter 2.1.2 introduces the a new property <b>HTTPResponse.parsers</b>, which is a list of parser ids,
  e.g. <b>htmlParser</b> and <b>wmlParser</b>. For each id found, JMeter checks two further properties:</p>
  <ul>
  <li>id.types - a list of content types</li>
  <li>id.className - the parser to be used to extract the embedded resources</li>
  </ul>
  <p>See jmeter.properties file for the details of the settings. 
  If the HTTPResponse.parser property is not set, JMeter reverts to the previous behaviour,
  i.e. only text/html responses will be scanned</p>
 <b>Emulating slow connections (HttpClient only):</b><br></br>
 The HttpClient version of the sampler supports emulation of slow connections; see the following entries in jmeter.properties:
 <pre>
 # Define characters per second > 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 </pre>
 <p><b>Response size calculation</b><br></br>
 Optional properties to allow change the method to get response size:<br></br>
 <ul><li>Gets the real network size in bytes for the body response
 <pre>sampleresult.getbytes.body_real_size=true</pre></li>
 <li>Add HTTP headers to full response size
 <pre>sampleresult.getbytes.headers_size=true</pre></li></ul>
 <note>Versions of JMeter before 2.5 returns only data response size (uncompressed if request uses gzip/defate mode).
 <br></br>To return to settings before version 2.5, set the two properties to false.</note>
 </p>
 <p>
 <b>Retry handling</b><br></br>
 In version 2.5 of JMeter, the HttpClient4 and Commons HttpClient 3.1 samplers used the default retry count, which was 3.
 In later versions, the retry count has been set to 1, which is what the Java implementation appears to do.
 The retry count can be overridden by setting the relevant JMeter property, for example:
 <pre>
 httpclient4.retrycount=3
 httpclient3.retrycount=3
 </pre>
 </p>
 <links>
         <link href="test_plan.html#assertions">Assertion</link>
         <link href="build-web-test-plan.html">Building a Web Test Plan</link>
         <link href="build-adv-web-test-plan.html">Building an Advanced Web Test Plan</link>
         <complink name="HTTP Authorization Manager"/>
         <complink name="HTTP Cookie Manager"/>
         <complink name="HTTP Header Manager"/>
         <complink name="HTML Link Parser"/>
         <complink name="HTTP Proxy Server"/>
         <complink name="HTTP Request Defaults"/>
         <link href="build-adv-web-test-plan.html#session_url_rewriting">HTTP Requests and Session ID's: URL Rewriting</link>
 </links>
 
 </component>
 
 <component name="JDBC Request" index="&sect-num;.1.3"  width="466" height="334" screenshot="jdbctest/jdbc-request.png">
 
 <description><p>This sampler lets you send an JDBC Request (an SQL query) to a database.</p>
 <p>Before using this you need to set up a
 <complink name="JDBC Connection Configuration"/> Configuration element
 </p>
 <p>
 If the Variable Names list is provided, then for each row returned by a Select statement, the variables are set up
 with the value of the corresponding column (if a variable name is provided), and the count of rows is also set up.
 For example, if the Select statement returns 2 rows of 3 columns, and the variable list is <code>A,,C</code>,
 then the following variables will be set up:
 <pre>
 A_#=2 (number of rows)
 A_1=column 1, row 1
 A_2=column 1, row 2
 C_#=2 (number of rows)
 C_1=column 3, row 1
 C_2=column 3, row 2
 </pre>
 If the Select statement returns zero rows, then the A_# and C_# variables would be set to 0, and no other variables would be set.
 </p>
 <p>
 Old variables are cleared if necessary - e.g. if the first select retrieves 6 rows and a second select returns only 3 rows,
 the additional variables for rows 4, 5 and 6 will be removed.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 		<property name="Variable Name" required="Yes">
 		Name of the JMeter variable that the connection pool is bound to.
 		This must agree with the 'Variable Name' field of a JDBC Connection Configuration.
 		</property>
 		<property name="Query Type" required="Yes">Set this according to the statement type:
 		    <ul>
 		    <li>Select Statement</li>
 		    <li>Update Statement - use this for Inserts as well</li>
 		    <li>Callable Statement</li>
 		    <li>Prepared Select Statement</li>
 		    <li>Prepared Update Statement - use this for Inserts as well</li>
 		    <li>Commit</li>
 		    <li>Rollback</li>
 		    <li>Autocommit(false)</li>
 		    <li>Autocommit(true)</li>
 		    <li>Edit - this should be a variable reference that evaluates to one of the above</li>
 		    </ul>
 		</property>
         <property name="SQL Query" required="Yes">
         SQL query.
         Do not enter a trailing semi-colon.
         There is generally no need to use { and } to enclose Callable statements;
         however they mey be used if the database uses a non-standard syntax.
         [The JDBC driver automatically converts the statement if necessary when it is enclosed in {}].
         For example:
         <ul>
         <li>select * from t_customers where id=23</li>
         <li>CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (null,?, ?, null, null, null)
         <ul>
         <li>Parameter values: tablename,filename</li>
         <li>Parameter types:  VARCHAR,VARCHAR</li>
         </ul>
         </li>
         The second example assumes you are using Apache Derby.
         </ul>
         </property>
         <property name="Parameter values" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of parameter values. Use ]NULL[ to indicate a NULL parameter.
         (If required, the null string can be changed by defining the property "jdbcsampler.nullmarker".)
         <br></br>
         The list must be enclosed in double-quotes if any of the values contain a comma or double-quote,
         and any embedded double-quotes must be doubled-up, for example:
         <pre>"Dbl-Quote: "" and Comma: ,"</pre>
         There must be as many values as there are placeholders in the statement.
         </property>
         <property name="Parameter types" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of SQL parameter types (e.g. INTEGER, DATE, VARCHAR, DOUBLE).
         These are defined as fields in the class java.sql.Types, see for example:
         <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/sql/Types.html">Javadoc for java.sql.Types</a>.
         [Note: JMeter will use whatever types are defined by the runtime JVM, 
         so if you are running on a different JVM, be sure to check the appropriate document]
         If the callable statement has INOUT or OUT parameters, then these must be indicated by prefixing the
         appropriate parameter types, e.g. instead of "INTEGER", use "INOUT INTEGER".
         If not specified, "IN" is assumed, i.e. "DATE" is the same as "IN DATE".
         <br></br>
         If the type is not one of the fields found in java.sql.Types, versions of JMeter after 2.3.2 also
         accept the corresponding integer number, e.g. since INTEGER == 4, you can use "INOUT 4".
         <br></br>
         There must be as many types as there are placeholders in the statement.
         </property>
         <property name="Variable Names" required="No">Comma-separated list of variable names to hold values returned by Select statements</property>
         <property name="Result Variable Name" required="No">
         If specified, this will create an Object variable containing a list of row maps.
         Each map contains the column name as the key and the column data as the value. Usage:<br></br>
         <code>columnValue = vars.getObject("resultObject").get(0).get("Column Name");</code>
         </property>
 </properties>
 
 <links>
         <link href="build-db-test-plan.html">Building a Database Test Plan</link>
         <complink name="JDBC Connection Configuration"/>
 </links>
 <note>Versions of JMeter after 2.3.2 use UTF-8 as the character encoding. Previously the platform default was used.</note>
 </component>
 
 <component name="Java Request" index="&sect-num;.1.4"  width="406" height="307" screenshot="java_request.png">
 
 <description><p>This sampler lets you control a java class that implements the
 <b><code>org.apache.jmeter.protocol.java.sampler.JavaSamplerClient</code></b> interface.
 By writing your own implementation of this interface,
 you can use JMeter to harness multiple threads, input parameter control, and
 data collection.</p>
 <p>The pull-down menu provides the list of all such implementations found by
 JMeter in its classpath.  The parameters can then be specified in the
 table below - as defined by your implementation.  Two simple examples (JavaTest and SleepTest) are provided.
 </p>
 <p>
 The JavaTest example sampler can be useful for checking test plans, because it allows one to set
 values in almost all the fields. These can then be used by Assertions, etc.
 The fields allow variables to be used, so the values of these can readily be seen.
 </p>
 </description>
 
 <note>The Add/Delete buttons don't serve any purpose at present.</note>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="Classname" required="Yes">The specific implementation of
         the JavaSamplerClient interface to be sampled.</property>
         <property name="Send Parameters with Request" required="No">A list of
         arguments that will be passed to the sampled class.  All arguments
         are sent as Strings.</property>
         </properties>
 </component>
 
 <p>The sleep time is calculated as follows:</p>
 <pre>
 SleepTime is in milliseconds
 SleepMask is used to add a "random" element to the time:
 totalSleepTime = SleepTime + (System.currentTimeMillis() % SleepMask)
 </pre>
 <component name="SOAP/XML-RPC Request" index="&sect-num;.1.5"  width="426" height="276" screenshot="soap_sampler.png">
 
 <description><p>This sampler lets you send a SOAP request to a webservice.  It can also be
 used to send XML-RPC over HTTP.  It creates an HTTP POST request, with the specified XML as the
 POST content. 
 To change the "Content-type" from the default of "text/xml", use a HeaderManager. 
 Note that the sampler will use all the headers from the HeaderManager.
 If a SOAP action is specified, that will override any SOAPaction in the HeaderManager.
 The primary difference between the soap sampler and
 webservice sampler, is the soap sampler uses raw post and does not require conformance to
 SOAP 1.1.</p>
 <note>For versions of JMeter later than 2.2, the sampler no longer uses chunked encoding by default.<br/>
 For screen input, it now always uses the size of the data.<br/>
 File input uses the file length as determined by Java.<br/>
 On some OSes this may not work for all files, in which case add a child Header Manager
 with Content-Length set to the actual length of the file.<br/>
 Or set Content-Length to -1 to force chunked encoding.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="URL" required="Yes">The URL to direct the SOAP request to.</property>
         <property name="Send SOAP action" required="No">Send a SOAP action header? (overrides the Header Manager)</property>
         <property name="Use KeepAlive" required="No">If set, sends Connection: keep-alive, else sends Connection: close</property>
         <property name="Soap/XML-RPC Data" required="No">The Soap XML message, or XML-RPC instructions.
         Not used if the filename is provided.
         </property>
         <property name="Filename" required="No">If specified, then the contents of the file are sent, and the Data field is ignored</property>
         </properties>
 
 </component>
 
 <component name="WebService(SOAP) Request" index="&sect-num;.1.6" width="943" height="648" screenshot="webservice_sampler.png">
 <description><p>This sampler has been tested with IIS Webservice running .NET 1.0 and .NET 1.1.
  It has been tested with SUN JWSDP, IBM webservices, Axis and gSoap toolkit for C/C++.
  The sampler uses Apache SOAP driver to serialize the message and set the header
  with the correct SOAPAction. Right now the sampler doesn't support automatic WSDL
  handling, since Apache SOAP currently does not provide support for it. Both IBM
  and SUN provide WSDL drivers. There are 3 options for the post data: text area,
  external file, or directory. If you want the sampler to randomly select a message,
  use the directory. Otherwise, use the text area or a file. The if either the
  file or path are set, it will not use the message in the text area. If you need
  to test a soap service that uses different encoding, use the file or path. If you
  paste the message in to text area, it will not retain the encoding and will result
  in errors. Save your message to a file with the proper encoding, and the sampler
  will read it as java.io.FileInputStream.</p>
  <p>An important note on the sampler is it will automatically use the proxy host
  and port passed to JMeter from command line, if those fields in the sampler are
  left blank. If a sampler has values in the proxy host and port text field, it
  will use the ones provided by the user. This behavior may not be what users
  expect.</p>
  <p>By default, the webservice sampler sets SOAPHTTPConnection.setMaintainSession
  (true). If you need to maintain the session, add a blank Header Manager. The
  sampler uses the Header Manager to store the SOAPHTTPConnection object, since
  the version of apache soap does not provide a easy way to get and set the cookies.</p>
  <p><b>Note:</b> If you are using CSVDataSet, do not check "Memory Cache". If memory
  cache is checked, it will not iterate to the next value. That means all the requests
  will use the first value.</p>
  <p>Make sure you use &amp;lt;soap:Envelope rather than &amp;lt;Envelope. For example:</p>
  <pre>
 &amp;lt;?xml version="1.0" encoding="utf-8"?>
 &amp;lt;soap:Envelope 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
 xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
 &amp;lt;soap:Body>
 &amp;lt;foo xmlns="http://clients-xlmns"/>
 &amp;lt;/soap:Body>
 &amp;lt;/soap:Envelope>
 </pre>
 <note>The SOAP library that is used does not support SOAP 1.2, only SOAP 1.1. 
 Also the library does not provide access to the HTTP response code (e.g. 200) or message (e.g. OK). 
 To get round this, versions of JMeter after 2.3.2 check the returned message length.
 If this is zero, then the request is marked as failed.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="WSDL URL" required="No">The WSDL URL with the service description.
         Versions of JMeter after 2.3.1 support the file: protocol for local WSDL files.
         </property>
         <property name="Web Methods" required="No">Will be populated from the WSDL when the Load WSDL button is pressed.
         Select one of the methods and press the Configure button to populate the Protocol, Server, Port, Path and SOAPAction fields. 
         </property>
         <property name="Protocol" required="Yes">HTTP or HTTPS are acceptable protocol.</property>
         <property name="Server Name or IP" required="Yes">The hostname or IP address.</property>
         <property name="Port Number" required="Yes">Port Number.</property>
         <property name="Timeout" required="No">Connection timeout.</property>
         <property name="Path" required="Yes">Path for the webservice.</property>
         <property name="SOAPAction" required="Yes">The SOAPAction defined in the webservice description or WSDL.</property>
         <property name="Soap/XML-RPC Data" required="Yes">The Soap XML message</property>
         <property name="Soap file" required="No">File containing soap message</property>
         <property name="Message(s) Folder" required="No">Folder containing soap files. Files are choose randomly during test.</property>
         <property name="Memory cache" required="Yes">
         When using external files, setting this causes the file to be processed once and caches the result.
         This may use a lot of memory if there are many different large files.
         </property>
         <property name="Read SOAP Response" required="No">Read the SOAP reponse (consumes performance). Permit to have assertions or post-processors</property>
         <property name="Use HTTP Proxy" required="No">Check box if http proxy should be used</property>
         <property name="Server Name or IP" required="No">Proxy hostname</property>
         <property name="Port Number" required="No">Proxy host port</property>
         </properties>
 
 </component>
 
 <component name="LDAP Request" index="&sect-num;.1.7" width="621" height="462" screenshot="ldap_request.png">
   <description>This Sampler lets you send a different Ldap request(Add, Modify, Delete and Search) to an LDAP server.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> The same way the <complink name="Login Config Element"/> also using for Login and password.
   </description>
 
   <p>There are two ways to create test cases for testing an LDAP Server.</p>
   <ol><li>Inbuilt Test cases.</li>
     <li>User defined Test cases.</li></ol>
 
     <p>There are four test scenarios of testing LDAP. The tests are given below:</p>
     <ol>
       <li>Add Test</li>
       <ol><li>Inbuilt test :
         <p>This will add a pre-defined entry in the LDAP Server and calculate
           the execution time. After execution of the test, the created entry will be
           deleted from the LDAP
           Server.</p></li>
           <li>User defined test :
             <p>This will add the entry in the LDAP Server. User has to enter all the
               attributes in the table.The entries are collected from the table to add. The
               execution time is calculated. The created entry will not be deleted after the
               test.</p></li></ol>
 
               <li>Modify Test</li>
               <ol><li>Inbuilt test :
                 <p>This will create a pre-defined entry first, then will modify the
                   created	entry in the LDAP Server.And calculate the execution time. After
                   execution
                   of the test, the created entry will be deleted from the LDAP Server.</p></li>
                   <li>User defined test
                     <p>This will modify the entry in the LDAP Server. User has to enter all the
                       attributes in the table. The entries are collected from the table to modify.
                       The execution time is calculated. The entry will not be deleted from the LDAP
                       Server.</p></li></ol>
 
                       <li>Search Test</li>
                       <ol><li>Inbuilt test :
                         <p>This will create the entry first, then will search if the attributes
                           are available. It calculates the execution time of the search query. At the
                           end of  the execution,created entry will be deleted from the LDAP Server.</p></li>
                           <li>User defined test
                             <p>This will search the user defined entry(Search filter) in the Search
                               base (again, defined by the user). The entries should be available in the LDAP
                               Server. The execution time is  calculated.</p></li></ol>
 
                               <li>Delete Test</li>
                               <ol><li>Inbuilt test :
                                 <p>This will create a pre-defined entry first, then it will be deleted
                                   from the LDAP Server. The execution time is calculated.</p></li>
 
                                   <li>User defined test
                                     <p>This will delete the user-defined entry in the LDAP Server. The entries
                                       should be available in the LDAP Server. The execution time is calculated.</p></li></ol></ol>
                                       <properties>
                                         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
                                         <property name="Server Name or IP" required="Yes">Domain name or IP address of the LDAP server.
                                           JMeter assumes the LDAP server is listening on the default port(389).</property>
                                           <property name="Port" required="Yes">default port(389).</property>
                                           <property name="root DN" required="Yes">DN for the server to communicate</property>
                                           <property name="Username" required="Usually">LDAP server username.</property>
                                           <property name="Password" required="Usually">LDAP server password.</property>
                                           <property name="Entry DN" required="Yes">the name of the context to create or Modify; may not be empty Example: do you want to add cn=apache,ou=test
                                             you have to add in table name=cn, value=apache
                                           </property>
                                           <property name="Delete" required="Yes">the name of the context to Delete; may not be empty</property>
                                           <property name="Search base" required="Yes">the name of the context or object to search</property>
                                           <property name="Search filter" required="Yes"> the filter expression to use for the search; may not be null</property>
                                           <property name="add test" required="Yes"> this name, value pair to added in the given context object</property>
                                           <property name="modify test" required="Yes"> this name, value pair to add or modify in the given context object</property>
                                       </properties>
 
                                       <links>
                                         <link href="build-ldap-test-plan.html">Building an Ldap Test Plan</link>
                                         <complink name="LDAP Request Defaults"/>
                                       </links>
 
 </component>
 
 <component name="LDAP Extended Request" index="&sect-num;.1.8" width="619" height="371" screenshot="ldapext_request.png">
   <description>This Sampler can send all 8 different LDAP request to an LDAP server. It is an extended version of the LDAP sampler,
   therefore it is harder to configure, but can be made much closer resembling a real LDAP session.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Extended Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> </description>
 
    <p>There are nine test operations defined. These operations are given below:</p>
     <ol>
       <li><b>Thread bind</b></li>
       <p>Any LDAP request is part of an LDAP session, so the first thing that should be done is starting a session to the LDAP server.
        For starting this session a thread bind is used, which is equal to the LDAP "bind" operation.
        The user is requested to give a username (Distinguished name) and password, 
        which will be used to initiate a session.
        When no password, or the wrong password is specified, an anonymous session is started. Take care,
        omitting the password will not fail this test, a wrong password will. </p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
      <property name="Servername" required="Yes">The name (or IP-address) of the LDAP server.</property>
      <property name="Port" required="No">The port number that the LDAP server is listening to. If this is omitted 
      JMeter assumes the LDAP server is listening on the default port(389).</property>
      <property name="DN" required="No">The distinguished name of the base object that will be used for any subsequent operation. 
      It can be used as a starting point for all operations. You cannot start any operation on a higher level than this DN!</property>
      <property name="Username" required="No">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error and revert to an anonymous bind.</property>
     </properties>
  <br />       
       <li><b>Thread unbind</b></li>
       <p>This is simply the operation to end a session. 
       It is equal to the LDAP "unbind" operation.</p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     </properties>
      
  <br />       
       <li><b>Single bind/unbind</b></li>
 		<p> This is a combination of the LDAP "bind" and "unbind" operations.
 		It can be used for an authentication request/password check for any user. It will open an new session, just to
 		check the validity of the user/password combination, and end the session again.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Username" required="Yes">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error.</property>
      </properties>
 		
  <br />       
       <li><b>Rename entry</b></li>
        <p>This is the LDAP "moddn" operation. It can be used to rename an entry, but 
        also for moving an entry or a complete subtree to a different place in 
        the LDAP tree.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Old entry name" required="Yes">The current distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      <property name="New distinguished name" required="Yes">The new distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      </properties>
        
  <br />       
         <li><b>Add test</b></li>
        <p>This is the ldap "add" operation. It can be used to add any kind of 
        object to the LDAP server.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">Distinguished name of the object you want to add, relative to the given DN in the thread bind operation.</property>
      <property name="Add test" required="Yes">A list of attributes and their values you want to use for the object.
      If you need to add a multiple value attribute, you need to add the same attribute with their respective 
      values several times to the list.</property>
      </properties>
        
  <br />       
       <li><b>Delete test</b></li>
        <p> This is the LDAP "delete" operation, it can be used to delete an 
        object from the LDAP tree </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Delete" required="Yes">Distinguished name of the object you want to delete, relative to the given DN in the thread bind operation.</property>
       </properties>
        
  <br />       
       <li><b>Search test</b></li>
        <p>This is the LDAP "search" operation, and will be used for defining searches.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Search base" required="No">Distinguished name of the subtree you want your 
       search to look in, relative to the given DN in the thread bind operation.</property>
       <property name="Search Filter" required="Yes">searchfilter, must be specified in LDAP syntax.</property>
       <property name="Scope" required="No">Use 0 for baseobject-, 1 for onelevel- and 2 for a subtree search. (Default=0)</property>
       <property name="Size Limit" required="No">Specify the maximum number of results you want back from the server. (default=0, which means no limit.) When the sampler hits the maximum number of results, it will fail with errorcode 4</property>
       <property name="Time Limit" required="No">Specify the maximum amount of (cpu)time (in miliseconds) that the server can spend on your search. Take care, this does not say anything about the responsetime. (default is 0, which means no limit)</property>
       <property name="Attributes" required="No">Specify the attributes you want to have returned, seperated by a semicolon. An empty field will return all attributes</property>
       <property name="Return object" required="No">Whether the object will be returned (true) or not (false). Default=false</property>
       <property name="Dereference aliases" required="No">If true, it will dereference aliases, if false, it will not follow them (default=false)</property>
      </properties>
 
  <br />       
       <li><b>Modification test</b></li>
        <p>This is the LDAP "modify" operation. It can be used to modify an object. It
        can be used to add, delete or replace values of an attribute. </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry name" required="Yes">Distinguished name of the object you want to modify, relative 
       to the given DN in the thread bind operation</property>
      <property name="Modification test" required="Yes">The attribute-value-opCode triples. The opCode can be any 
      valid LDAP operationCode (add, delete/remove or replace). If you don't specify a value with a delete operation,
      all values of the given attribute will be deleted. If you do specify a value in a delete operation, only 
      the given value will be deleted. If this value is non-existent, the sampler will fail the test.</property>
      </properties>
        
  <br />       
       <li><b>Compare</b></li>
        <p>This is the LDAP "compare" operation. It can be used to compare the value 
        of a given attribute with some already known value. In reality this is mostly 
        used to check whether a given person is a member of some group. In such a case
         you can compare the DN of the user as a given value, with the values in the
          attribute "member" of an object of the type groupOfNames.
          If the compare operation fails, this test fails with errorcode 49.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">The current distinguished name of the object of 
       which you want  to compare an attribute, relative to the given DN in the thread bind operation.</property>
      <property name="Compare filter" required="Yes">In the form "attribute=value"</property>
      </properties>
     </ol>
        
     <links>
       <link href="build-ldapext-test-plan.html">Building an LDAP Test Plan</link>
       <complink name="LDAP Extended Request Defaults"/>
     </links>
 
 </component>
 
 
 
 
 <component name="Access Log Sampler" index="&sect-num;.1.9"  width="582" height="301" screenshot="accesslogsampler.png">
 <center><h2>(Alpha Code)</h2></center>
 <description><p>AccessLogSampler was designed to read access logs and generate http requests.
 For those not familiar with the access log, it is the log the webserver maintains of every
 request it accepted. This means the every image and html file. The current implementation
 is complete, but some features have not been enabled. There is a filter for the access
 log parser, but I haven't figured out how to link to the pre-processor. Once I do, changes
 to the sampler will be made to enable that functionality.</p>
 <p>Tomcat uses the common format for access logs. This means any webserver that uses the
 common log format can use the AccessLogSampler. Server that use common log format include:
 Tomcat, Resin, Weblogic, and SunOne. Common log format looks
 like this:</p>
 <p>127.0.0.1 - - [21/Oct/2003:05:37:21 -0500] "GET /index.jsp?%2Findex.jsp= HTTP/1.1" 200 8343</p>
 <p>The current implemenation of the parser only looks at the text within the quotes.
 Everything else is stripped out and igored. For example, the response code is completely
 ignored by the parser. For the future, it might be nice to filter out entries that
 do not have a response code of 200. Extending the sampler should be fairly simple. There
 are two interfaces you have to implement.</p>
 <p>org.apache.jmeter.protocol.http.util.accesslog.LogParser</p>
 <p>org.apache.jmeter.protocol.http.util.accesslog.Generator</p>
 <p>The current implementation of AccessLogSampler uses the generator to create a new
 HTTPSampler. The servername, port and get images are set by AccessLogSampler. Next,
 the parser is called with integer 1, telling it to parse one entry. After that,
 HTTPSampler.sample() is called to make the request.
 <code>
 <pre>
             samp = (HTTPSampler) GENERATOR.generateRequest();
             samp.setDomain(this.getDomain());
             samp.setPort(this.getPort());
             samp.setImageParser(this.isImageParser());
             PARSER.parse(1);
             res = samp.sample();
             res.setSampleLabel(samp.toString());
 </pre>
 </code>
 The required methods in LogParser are: setGenerator(Generator) and parse(int).
 Classes implementing Generator interface should provide concrete implementation
 for all the methods. For an example of how to implement either interface, refer to
 StandardGenerator and TCLogParser.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="Yes">Domain name or IP address of the web server.</property>
         <property name="Port" required="No (defaults to 80)">Port the web server is listening to.</property>
         <property name="Log parser class" required="Yes (default provided)">The log parser class is responsible for parsing the logs.</property>
         <property name="Filter" required="No">The filter class is used to filter out certain lines.</property>
         <property name="Location of log file" required="Yes">The location of the access log file.</property>
 </properties>
 <p>
 The TCLogParser processes the access log independently for each thread.
 The SharedTCLogParser and OrderPreservingLogParser share access to the file, 
 i.e. each thread gets the next entry in the log.
 </p>
 <p>
 The SessionFilter is intended to handle Cookies across threads. 
 It does not filter out any entries, but modifies the cookie manager so that the cookies for a given IP are
 processed by a single thread at a time. If two threads try to process samples from the same client IP address,
 then one will be forced to wait until the other has completed.
 </p>
 <p>
 The LogFilter is intended to allow access log entries to be filtered by filename and regex,
 as well as allowing for the replacement of file extensions. However, it is not currently possible
 to configure this via the GUI, so it cannot really be used.
 </p>
 </component>
 
 <component name="BeanShell Sampler" index="&sect-num;.1.10"  width="592" height="303" screenshot="beanshellsampler.png">
 	<description><p>This sampler allows you to write a sampler using the BeanShell scripting language.		
 </p><p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the ThreadListener and TestListener interface methods.
 These must be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 <p>
 From JMeter version 2.5.1, the BeanShell sampler also supports the Interruptible interface.
 The interrupt() method can be defined in the script or the init file.
 </p>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.
     The name is stored in the script variable Label</property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     This is intended for use with script files; for scripts defined in the GUI, you can use whatever
     variable and function references you need within the script itself.
 	The parameters are stored in the following variables:
 	<ul>
 		<li>Parameters - string containing the parameters as a single variable</li>
 	    <li>bsh.args - String array containing parameters, split on white-space</li>
 	</ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable FileName</property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. 
     The return value (if not null) is stored as the sampler result.</property>
 </properties>
 <p>
 N.B. Each Sampler instance has its own BeanShell interpeter,
 and Samplers are only called from a single thread
 </p><p>
 If the property "beanshell.sampler.init" is defined, it is passed to the Interpreter
 as the name of a sourced file.
 This can be used to define common methods and variables. 
 There is a sample init file in the bin directory: BeanShellSampler.bshrc.
 </p><p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 		<p>Before invoking the script, some variables are set up in the BeanShell interpreter:
 			</p>
 				<p>The contents of the Parameters field is put into the variable "Parameters".
 			The string is also split into separate tokens using a single space as the separator, and the resulting list
 			is stored in the String array bsh.args.</p>
 			<p>The full list of BeanShell variables that is set up is as follows:</p>
 		<ul>
 		<li>log - the Logger</li>
 		<li>Label - the Sampler label</li>
 		<li>FileName - the file name, if any</li>
 		<li>Parameters - text from the Parameters field</li>
 		<li>bsh.args - the parameters, split as described above</li>
 		<li>SampleResult - pointer to the current SampleResult</li>
 			<li>ResponseCode = 200</li>
 			<li>ResponseMessage = "OK"</li>
 			<li>IsSuccess = true</li>
 			<li>ctx - JMeterContext</li>
 			<li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3"); vars.putObject("OBJ1",new Object());</li>
             <li>props - JMeterProperties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 		</ul>
 		<p>When the script completes, control is returned to the Sampler, and it copies the contents
 			of the following script variables into the corresponding variables in the SampleResult:</p>
 			<ul>
 			<li>ResponseCode - for example 200</li>
 			<li>ResponseMessage - for example "OK"</li>
 			<li>IsSuccess - true/false</li>
 			</ul>
 			<p>The SampleResult ResponseData is set from the return value of the script.
 			Since version 2.1.2, if the script returns null, it can set the response directly, by using the method 
 			SampleResult.setResponseData(data), where data is either a String or a byte array.
 			The data type defaults to "text", but can be set to binary by using the method
 			SampleResult.setDataType(SampleResult.BINARY).
 			</p>
 			<p>The SampleResult variable gives the script full access to all the fields and
 				methods in the SampleResult. For example, the script has access to the methods
 				setStopThread(boolean) and setStopTest(boolean).
 				
 				Here is a simple (not very useful!) example script:</p>
 				
 <pre>
 if (bsh.args[0].equalsIgnoreCase("StopThread")) {
     log.info("Stop Thread detected!");
     SampleResult.setStopThread(true);
 }
 return "Data from sample with Label "+Label;
 //or, since version 2.1.2
 SampleResult.setResponseData("My data");
 return null;
 </pre>
 <p>Another example:<br></br> ensure that the property <b>beanshell.sampler.init=BeanShellSampler.bshrc</b> is defined in jmeter.properties. 
 The following script will show the values of all the variables in the ResponseData field:
 </p>
 <pre>
 return getVariables();
 </pre>
 <p>
 For details on the methods available for the various classes (JMeterVariables, SampleResult etc) please check the Javadoc or the source code.
 Beware however that misuse of any methods can cause subtle faults that may be difficult to find ...
 </p>
 </component>
 
 
 <component name="BSF Sampler" index="&sect-num;.1.11"  width="622" height="267" screenshot="bsfsampler.png">
 	<description><p>This sampler allows you to write a sampler using a BSF scripting language.<br></br>
 		See the <a href="http://commons.apache.org/bsf/index.html">Apache Bean Scripting Framework</a>
 		website for details of the languages supported.
 		You may need to download the appropriate jars for the language; they should be put in the JMeter <b>lib</b> directory.
 		</p>
 		<p>By default, JMeter supports the following languages:</p>
 		<ul>
 		<li>javascript</li>
         <li>jexl (JMeter version 2.3.2 and later)</li>
         <li>xslt</li>
 		</ul>
         <note>Unlike the BeanShell sampler, the interpreter is not saved between invocations.</note>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 	<property name="Scripting Language" required="Yes">Name of the BSF scripting language to be used.
 	N.B. Not all the languages in the drop-down list are supported by default.
 	The following are supported: jexl, javascript, xslt.
 	Others may be available if the appropriate jar is installed in the JMeter lib directory.
 	</property>
 	<property name="Script File" required="No">Name of a file to be used as a BSF script</property>
 	<property name="Parameters" required="No">List of parameters to be passed to the script file or the script.</property>
 	<property name="Script" required="Yes (unless script file is provided)">Script to be passed to BSF language</property>
 </properties>
 <p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <p>
 Before invoking the script, some variables are set up.
 Note that these are BSF variables - i.e. they can be used directly in the script.
 </p>
 <ul>
 <li>log - the Logger</li>
 <li>Label - the Sampler label</li>
 <li>FileName - the file name, if any</li>
 <li>Parameters - text from the Parameters field</li>
 <li>args - the parameters, split as described above</li>
 <li>SampleResult - pointer to the current SampleResult</li>
 <li>sampler - pointer to current Sampler</li>
 <li>ctx - JMeterContext</li>
 <li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3"); vars.putObject("OBJ1",new Object());</li>
 <li>props - JMeterProperties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 <li>OUT - System.out - e.g. OUT.println("message")</li>
 </ul>
 <p>
 The SampleResult ResponseData is set from the return value of the script.
 If the script returns null, it can set the response directly, by using the method 
 SampleResult.setResponseData(data), where data is either a String or a byte array.
 The data type defaults to "text", but can be set to binary by using the method
 SampleResult.setDataType(SampleResult.BINARY).
 </p>
 <p>
 The SampleResult variable gives the script full access to all the fields and
 methods in the SampleResult. For example, the script has access to the methods
 setStopThread(boolean) and setStopTest(boolean).
 </p>
 <p>
 Unlike the Beanshell Sampler, the BSF Sampler does not set the ResponseCode, ResponseMessage and sample status via script variables.
 Currently the only way to changes these is via the SampleResult methods:
 <ul>
 <li>SampleResult.setSuccessful(true/false)</li>
 <li>SampleResult.setResponseCode("code")</li>
 <li>SampleResult.setResponseMessage("message")</li>
 </ul>
 </p>
 </component>
 
 <component name="JSR223 Sampler" index="&sect-num;.1.11.1">
 <description>
 <p>
 The JSR223 Sampler allows JSR223 script code to be used to perform a sample.
 For details, see <complink name="BSF Sampler"/>.
 </p>
 <note>Unlike the BeanShell sampler, the interpreter is not saved between invocations.</note>
 </description>
 </component>
 
 <component name="TCP Sampler" index="&sect-num;.1.12"  width="743" height="357" screenshot="tcpsampler.png">
 	<description>
 		<p>
 		The TCP Sampler opens a TCP/IP connection to the specified server.
 		It then sends the text, and waits for a response.
 		<br></br>
 		If "Re-use connection" is selected, connections are shared between Samplers in the same thread,
 		provided that the exact same host name string and port are used. 
 		Different hosts/port combinations will use different connections, as will different threads. 
 		<br></br>
 		If an error is detected - or "Re-use connection" is not selected - the socket is closed. 
 		Another socket will be reopened on the next sample.
 		<br></br>
 		The following properties can be used to control its operation:
 		</p>
 		<ul>
 			<li>tcp.status.prefix - text that precedes a status number</li>
 			<li>tcp.status.suffix - text that follows a status number</li>
 			<li>tcp.status.properties - name of property file to convert status codes to messages</li>
 			<li>tcp.handler - Name of TCP Handler class (default TCPClientImpl) - only used if not specified on the GUI</li>
 		</ul>
 		The class that handles the connection is defined by the GUI, failing that the property tcp.handler. 
 		If not found, the class is then searched for in the package org.apache.jmeter.protocol.tcp.sampler.
 		<p>
 		Users can provide their own implementation.
 		The class must extend org.apache.jmeter.protocol.tcp.sampler.TCPClient.
 		</p>
 		<p>
 		The following implementations are currently provided.
 		<ul>
 		<li>TCPClientImpl</li>
         <li>BinaryTCPClientImpl</li>
         <li>LengthPrefixedBinaryTCPClientImpl</li>
 		</ul>
 		The implementations behave as follows:
 		</p>
 		<p><b>TCPClientImpl</b><br></br>
 		This implementation is fairly basic.
         When reading the response, it reads until the end of line byte, if this is defined
         by setting the property <b>tcp.eolByte</b>, otherwise until the end of the input stream.
         </p>
         <p><b>BinaryTCPClientImpl</b><br></br>
         This implementation converts the GUI input, which must be a hex-encoded string, into binary,
         and performs the reverse when reading the response.
         When reading the response, it reads until the end of message byte, if this is defined
         by setting the property <b>tcp.BinaryTCPClient.eomByte</b>, otherwise until the end of the input stream.
         </p>
         <p><b>LengthPrefixedBinaryTCPClientImpl</b><br></br>
         This implementation extends BinaryTCPClientImpl by prefixing the binary message data with a binary length byte.
         The length prefix defaults to 2 bytes.
         This can be changed by setting the property <b>tcp.binarylength.prefix.length</b>.
         </p>
         <p><b>Timeout handling</b>
         If the timeout is set, the read will be terminated when this expires. 
         So if you are using an eolByte/eomByte, make sure the timeout is sufficiently long,
         otherwise the read will be terminated early.    
 		</p>
 		<p><b>Response handling</b>
 		<br></br>
 		If tcp.status.prefix is defined, then the response message is searched for the text following
 		that up to the suffix. If any such text is found, it is used to set the response code.
 		The response message is then fetched from the properties file (if provided).
 		<br></br>
 		For example, if the prefix = "[" and the suffix = "]", then the following repsonse:
 		<br></br>
 		[J28] XI123,23,GBP,CR
 		<br></br>
 		would have the response code J28.
 		<br></br>
 		Response codes in the range "400"-"499" and "500"-"599" are currently regarded as failures;
 		all others are successful. [This needs to be made configurable!]
 		</p>
 <note>The login name/password are not used by the supplied TCP implementations.</note>
 		<br></br>
 		Sockets are disconnected at the end of a test run.
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="TCPClient classname" required="No">Name of the TCPClient class. Defaults to the property tcp.handler, failing that TCPClientImpl.</property>
   <property name="ServerName or IP" required="Yes">Name or IP of TCP server</property>
   <property name="Port Number" required="Yes">Port to be used</property>
   <property name="Re-use connection" required="Yes">If selected, the connection is kept open. Otherwise it is closed when the data has been read.</property>
   <property name="Connect Timeout" required="No">Connect Timeout (milliseconds, 0 disables).</property>
   <property name="Response Timeout" required="No">Response Timeout (milliseconds, 0 disables).</property>
   <property name="Set Nodelay" required="Yes">See java.net.Socket.setTcpNoDelay().
   If selected, this will disable Nagle's algorithm, otherwise Nagle's algorithm will be used.</property>
   <property name="Text to Send" required="Yes">Text to be sent</property>
   <property name="Login User" required="No">User Name - not used by default implementation</property>
   <property name="Password" required="No">Password - not used by default implementation</property>
 </properties>
 </component>
 
 <component name="JMS Publisher" index="&sect-num;.1.13" width="636" height="628" screenshot="jmspublisher.png">
 <note>BETA CODE - the code is still subject to change</note>
 	<description>
 		<p>
 		JMS Publisher will publish messages to a given destination (topic/queue). For those not
 		familiar with JMS, it is the J2EE specification for messaging. There are
 		numerous JMS servers on the market and several open source options.
 		</p>
 		<br></br>
 <note>JMeter does not include any JMS implementation jar; this must be downloaded from the JMS provider and put in the lib directory</note>
 	</description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="use JNDI properties file" required="Yes">use jndi.properties. 
   Note that the file must be on the classpath - e.g. by updating the user.classpath JMeter property.
   If this option is not selected, JMeter uses the "JNDI Initial Context Factory" and "Provider URL" fields
   to create the connection.
   </property>
   <property name="JNDI Initial Context Factory" required="No">Name of the context factory</property>
   <property name="Provider URL" required="Yes, unless using jndi.properties">The URL for the jms provider</property>
   <property name="Destination" required="Yes">The message destination (topic or queue name)</property>
   <property name="Setup" required="Yes">The destination setup type. With At startup, the destination name is static (i.e. always same name during the test), with Each sample, the destination name is dynamic and is evaluate at each sample (i.e. the destination name may be a variable)</property>
   <property name="Authentication" required="Yes">Authentication requirement for the JMS provider</property>
   <property name="User" required="No">User Name</property>
   <property name="Password" required="No">Password</property>
   <property name="Number of samples to aggregate" required="Yes">Number of samples to aggregate</property>
   <property name="Message source" required="Yes">Where to obtain the message</property>
   <property name="Message type" required="Yes">Text, Map or Object message</property>
 </properties>
 <p>
 For the MapMessage type, JMeter reads the source as lines of text.
 Each line must have 3 fields, delimited by commas.
 The fields are:
 <ul>
 <li>Name of entry</li>
 <li>Object class name, e.g. "String" (assumes java.lang package if not specified)</li>
 <li>Object string value</li>
 </ul>
 For each entry, JMeter adds an Object with the given name.
 The value is derived by creating an instance of the class, and using the valueOf(String) method to convert the value if necessary.
 For example:
 <pre>
 name,String,Example
 size,Integer,1234
 </pre>
 This is a very simple implementation; it is not intended to support all possible object types.
 </p>
 <p>
 <b>Note: </b> the Object message type is not implemented yet.
 </p>
 <p>
 The following table shows some values which may be useful when configuring JMS:
 <table>
 <tr>
 <!-- Anakia does not like th cell without any text -->
 <th>Apache <a href="http://activemq.apache.org/">ActiveMQ</a></th>
 <th>Value(s)</th>
 <th>Comment</th>
 </tr>
 <tr><td>Context Factory</td><td>org.apache.activemq.jndi.ActiveMQInitialContextFactory</td><td>.</td></tr>
 <tr><td>Provider URL</td><td>vm://localhost</td><td></td></tr>
 <tr><td>Provider URL</td><td>vm:(broker:(vm://localhost)?persistent=false)</td><td>Disable persistence</td></tr>
 <tr><td>Queue Reference</td><td>dynamicQueues/QUEUENAME</td>
 <td><a href="http://activemq.apache.org/jndi-support.html#JNDISupport-Dynamicallycreatingdestinations">Dynamically define</a> the QUEUENAME to JNDI</td></tr>
 <tr><td>Topic Reference</td><td>dynamicTopics/TOPICNAME</td>
 <td><a href="http://activemq.apache.org/jndi-support.html#JNDISupport-Dynamicallycreatingdestinations">Dynamically define</a> the TOPICNAME to JNDI</td></tr>
 </table>
 </p>
 </component>
 
 <component name="JMS Subscriber" index="&sect-num;.1.14"  width="746" height="634" screenshot="jmssubscriber.png">
 <note>BETA CODE - the code is still subject to change</note>
 	<description>
 		<p>
 		JMS Publisher will subscribe to messages in a given destination (topic or queue). For those not
 		familiar with JMS, it is the J2EE specification for messaging. There are
 		numerous JMS servers on the market and several open source options.
 		</p>
 		<br></br>
 <note>JMeter does not include any JMS implementation jar; this must be downloaded from the JMS provider and put in the lib directory</note>
 	</description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="use JNDI properties file" required="Yes">use jndi.properties. 
   Note that the file must be on the classpath - e.g. by updating the user.classpath JMeter property.
   If this option is not selected, JMeter uses the "JNDI Initial Context Factory" and "Provider URL" fields
   to create the connection.
   </property>
   <property name="JNDI Initial Context Factory" required="No">Name of the context factory</property>
   <property name="Provider URL" required="No">The URL for the jms provider</property>
   <property name="Destination" required="Yes">the message destination (topic or queue name)</property>
   <property name="Durable Subscription ID" required="No">The ID to use for a durable subscription. On first 
   use the respective queue will automatically be generated by the JMS provider if it does not exist yet.</property>
   <property name="Client ID" required="No">The Client ID to use when you you use a durable subscription. 
   Be sure to add a variable like ${__threadNum} when you have more than one Thread.</property>
   <property name="JMS Selector" required="No">Message Selector as defined by JMS specification to extract only 
   messages that respect the Selector condition. Syntax uses subpart of SQL 92.</property>
   <property name="Setup" required="Yes">The destination setup type. With At startup, the destination name is static (i.e. always same name during the test), with Each sample, the destination name is dynamic and is evaluate at each sample (i.e. the destination name may be a variable)</property>
   <property name="Authentication" required="Yes">Authentication requirement for the JMS provider</property>
   <property name="User" required="No">User Name</property>
   <property name="Password" required="No">Password</property>
   <property name="Number of samples to aggregate" required="Yes">number of samples to aggregate</property>
   <property name="Read response" required="Yes">should the sampler read the response. If not, only the response length is returned.</property>
   <property name="Timeout" required="Yes">Specify the timeout to be applied, in milliseconds. 0=none. 
   This is the overall aggregate timeout, not per sample.</property>
   <property name="Client" required="Yes">Which client implementation to use.
   Both of them create connections which can read messages. However they use a different strategy, as described below:
   <ul>
   <li>MessageConsumer.receive() - calls receive() for every requested message. 
   Retains the connection between samples, but does not fetch messages unless the sampler is active.
   This is best suited to Queue subscriptions. 
   </li>
   <li>MessageListener.onMessage() - establishes a Listener that stores all incoming messages on a queue. 
   The listener remains active after the sampler completes.
   This is best suited to Topic subscriptions.</li>
   </ul>
   </property>
