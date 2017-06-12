diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index 00157cb34..263db5623 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,757 +1,758 @@
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
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.gui.util.HorizontalPanel;
+import org.apache.jmeter.gui.util.JSyntaxTextArea;
+import org.apache.jmeter.gui.util.JTextScrollPane;
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
-import org.apache.jorphan.gui.JLabeledTextArea;
 
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
     
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
 
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
 
     // Body data
-    private JLabeledTextArea postBodyContent;
+    private JSyntaxTextArea postBodyContent;
 
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
-            postBodyContent.setText("");// $NON-NLS-1$
+            postBodyContent.setInitialText("");// $NON-NLS-1$
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
      * Compute body data from arguments
      * @param arguments {@link Arguments}
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments) {
         return computePostBody(arguments, false);
     }
 
     /**
      * Compute body data from arguments
      * @param arguments {@link Arguments}
      * @param crlfToLF whether to convert CRLF to LF
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments, boolean crlfToLF) {
         StringBuilder postBody = new StringBuilder();
         PropertyIterator args = arguments.iterator();
         while (args.hasNext()) {
             HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
             String value = arg.getValue();
             if (crlfToLF) {
                 value=value.replaceAll("\r\n", "\n"); // See modifyTestElement
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
-            postBodyContent.setText(postBody);   
+            postBodyContent.setInitialText(postBody);   
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
         proxyServerPanel.add(proxyServer);
         proxyServerPanel.add(proxyLogin);
 
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
         label.setFont(FONT_SMALL);
 
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
         proxyHost = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(proxyHost);
         label.setFont(FONT_SMALL);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyHost, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyUserPanel() {
         proxyUser = new JTextField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
         label.setLabelFor(proxyUser);
         label.setFont(FONT_SMALL);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyUser, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyPassPanel() {
         proxyPass = new JPasswordField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
         label.setLabelFor(proxyPass);
         label.setFont(FONT_SMALL);
 
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
 
         JPanel pathPanel = new HorizontalPanel();
         pathPanel.add(label);
         pathPanel.add(path);
 
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
-            postBodyContent = new JLabeledTextArea(JMeterUtils.getResString("post_body_raw"));// $NON-NLS-1$
-            postContentTabbedPane.add(JMeterUtils.getResString("post_body"), postBodyContent);// $NON-NLS-1$
+            postBodyContent = new JSyntaxTextArea(30, 50);// $NON-NLS-1$
+            postContentTabbedPane.add(JMeterUtils.getResString("post_body"), new JTextScrollPane(postBodyContent));// $NON-NLS-1$
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
-                    postBodyContent.setText("");
+                    postBodyContent.setInitialText("");
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
     @Override
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
-        postBodyContent.setText(computePostBody((Arguments)argsPanel.createTestElement()));
+        postBodyContent.setInitialText(computePostBody((Arguments)argsPanel.createTestElement()));
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
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java
index befdf1250..8ea600820 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java
@@ -1,364 +1,371 @@
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
 
 package org.apache.jmeter.protocol.jms.control.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
+import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.JLabeledRadioI18N;
+import org.apache.jmeter.gui.util.JSyntaxTextArea;
+import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.jms.sampler.PublisherSampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledPasswordField;
-import org.apache.jorphan.gui.JLabeledTextArea;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 /**
  * This is the GUI for JMS Publisher
  *
  */
 public class JMSPublisherGui extends AbstractSamplerGui implements ChangeListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final String ALL_FILES = "*.*"; //$NON-NLS-1$
 
     //++ These names are used in the JMX files, and must not be changed
     /** Take source from the named file */
     public static final String USE_FILE_RSC   = "jms_use_file"; //$NON-NLS-1$
     /** Take source from a random file */
     public static final String USE_RANDOM_RSC = "jms_use_random_file"; //$NON-NLS-1$
     /** Take source from the text area */
     private static final String USE_TEXT_RSC   = "jms_use_text"; //$NON-NLS-1$
 
     /** Create a TextMessage */
     public static final String TEXT_MSG_RSC = "jms_text_message"; //$NON-NLS-1$
     /** Create a MapMessage */
     public static final String MAP_MSG_RSC = "jms_map_message"; //$NON-NLS-1$
     /** Create an ObjectMessage */
     public static final String OBJECT_MSG_RSC = "jms_object_message"; //$NON-NLS-1$
     /** Create a BytesMessage */
     public static final String BYTES_MSG_RSC = "jms_bytes_message"; //$NON-NLS-1$
     //-- End of names used in JMX files
 
     // Button group resources when Bytes Message is selected
     private static final String[] CONFIG_ITEMS_BYTES_MSG = { USE_FILE_RSC, USE_RANDOM_RSC};
 
     // Button group resources
     private static final String[] CONFIG_ITEMS = { USE_FILE_RSC, USE_RANDOM_RSC, USE_TEXT_RSC };
 
     private static final String[] MSGTYPES_ITEMS = { TEXT_MSG_RSC, MAP_MSG_RSC, OBJECT_MSG_RSC, BYTES_MSG_RSC };
 
     private final JCheckBox useProperties = new JCheckBox(JMeterUtils.getResString("jms_use_properties_file"), false); //$NON-NLS-1$
 
     private final JLabeledRadioI18N configChoice = new JLabeledRadioI18N("jms_config", CONFIG_ITEMS, USE_TEXT_RSC); //$NON-NLS-1$
 
     private final JLabeledTextField jndiICF = new JLabeledTextField(JMeterUtils.getResString("jms_initial_context_factory")); //$NON-NLS-1$
 
     private final JLabeledTextField urlField = new JLabeledTextField(JMeterUtils.getResString("jms_provider_url")); //$NON-NLS-1$
 
     private final JLabeledTextField jndiConnFac = new JLabeledTextField(JMeterUtils.getResString("jms_connection_factory")); //$NON-NLS-1$
 
     private final JLabeledTextField jmsDestination = new JLabeledTextField(JMeterUtils.getResString("jms_topic")); //$NON-NLS-1$
 
     private final JCheckBox useAuth = new JCheckBox(JMeterUtils.getResString("jms_use_auth"), false); //$NON-NLS-1$
 
     private final JLabeledTextField jmsUser = new JLabeledTextField(JMeterUtils.getResString("jms_user")); //$NON-NLS-1$
 
     private final JLabeledTextField jmsPwd = new JLabeledPasswordField(JMeterUtils.getResString("jms_pwd")); //$NON-NLS-1$
 
     private final JLabeledTextField iterations = new JLabeledTextField(JMeterUtils.getResString("jms_itertions")); //$NON-NLS-1$
 
     private final FilePanel messageFile = new FilePanel(JMeterUtils.getResString("jms_file"), ALL_FILES); //$NON-NLS-1$
 
     private final FilePanel randomFile = new FilePanel(JMeterUtils.getResString("jms_random_file"), ALL_FILES); //$NON-NLS-1$
 
-    private final JLabeledTextArea textMessage = new JLabeledTextArea(JMeterUtils.getResString("jms_text_area")); // $NON-NLS-1$
+    private final JSyntaxTextArea textMessage = new JSyntaxTextArea(15, 50); // $NON-NLS-1$
 
     private final JLabeledRadioI18N msgChoice = new JLabeledRadioI18N("jms_message_type", MSGTYPES_ITEMS, TEXT_MSG_RSC); //$NON-NLS-1$
     
     private JCheckBox useNonPersistentDelivery;
 
     // These are the names of properties used to define the labels
     private static final String DEST_SETUP_STATIC = "jms_dest_setup_static"; // $NON-NLS-1$
 
     private static final String DEST_SETUP_DYNAMIC = "jms_dest_setup_dynamic"; // $NON-NLS-1$
     // Button group resources
     private static final String[] DEST_SETUP_ITEMS = { DEST_SETUP_STATIC, DEST_SETUP_DYNAMIC };
 
     private final JLabeledRadioI18N destSetup =
         new JLabeledRadioI18N("jms_dest_setup", DEST_SETUP_ITEMS, DEST_SETUP_STATIC); // $NON-NLS-1$
 
     private ArgumentsPanel jmsPropertiesPanel;
 
     public JMSPublisherGui() {
         init();
     }
 
     /**
      * the name of the property for the JMSPublisherGui is jms_publisher.
      */
     @Override
     public String getLabelResource() {
         return "jms_publisher"; //$NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     @Override
     public TestElement createTestElement() {
       PublisherSampler sampler = new PublisherSampler();
       setupSamplerProperties(sampler);
 
       return sampler;
   }
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement s) {
         PublisherSampler sampler = (PublisherSampler) s;
         setupSamplerProperties(sampler);
         sampler.setDestinationStatic(destSetup.getText().equals(DEST_SETUP_STATIC));
     }
 
     /**
      * Initialize the provided {@link PublisherSampler} with all the values as configured in the GUI.
      * 
      * @param sampler {@link PublisherSampler} instance
      */
     private void setupSamplerProperties(final PublisherSampler sampler) {
       this.configureTestElement(sampler);
       sampler.setUseJNDIProperties(String.valueOf(useProperties.isSelected()));
       sampler.setJNDIIntialContextFactory(jndiICF.getText());
       sampler.setProviderUrl(urlField.getText());
       sampler.setConnectionFactory(jndiConnFac.getText());
       sampler.setDestination(jmsDestination.getText());
       sampler.setUsername(jmsUser.getText());
       sampler.setPassword(jmsPwd.getText());
       sampler.setTextMessage(textMessage.getText());
       sampler.setInputFile(messageFile.getFilename());
       sampler.setRandomPath(randomFile.getFilename());
       sampler.setConfigChoice(configChoice.getText());
       sampler.setMessageChoice(msgChoice.getText());
       sampler.setIterations(iterations.getText());
       sampler.setUseAuth(useAuth.isSelected());
       sampler.setUseNonPersistentDelivery(useNonPersistentDelivery.isSelected());
      
       Arguments args = (Arguments) jmsPropertiesPanel.createTestElement();
       sampler.setJMSProperties(args);
     }
 
     /**
      * init() adds jndiICF to the mainPanel. The class reuses logic from
      * SOAPSampler, since it is common.
      */
     private void init() {
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel mainPanel = new VerticalPanel();
         add(mainPanel, BorderLayout.CENTER);
         
         mainPanel.add(useProperties);
         mainPanel.add(jndiICF);
         mainPanel.add(urlField);
         mainPanel.add(jndiConnFac);
         mainPanel.add(createDestinationPane());
         mainPanel.add(createAuthPane());
         mainPanel.add(iterations);
 
         jmsPropertiesPanel = new ArgumentsPanel(JMeterUtils.getResString("jms_props")); //$NON-NLS-1$
         mainPanel.add(jmsPropertiesPanel);
 
         configChoice.setLayout(new BoxLayout(configChoice, BoxLayout.X_AXIS));
         mainPanel.add(configChoice);
         msgChoice.setLayout(new BoxLayout(msgChoice, BoxLayout.X_AXIS));
         mainPanel.add(msgChoice);
         mainPanel.add(messageFile);
         mainPanel.add(randomFile);
-        mainPanel.add(textMessage);
+
+        JPanel messageContentPanel = new JPanel(new BorderLayout());
+        messageContentPanel.add(new JLabel(JMeterUtils.getResString("jms_text_area")), BorderLayout.NORTH);
+        messageContentPanel.add(new JTextScrollPane(textMessage), BorderLayout.CENTER);
+
+        mainPanel.add(messageContentPanel);
         Dimension pref = new Dimension(400, 150);
         textMessage.setPreferredSize(pref);
 
         useProperties.addChangeListener(this);
         useAuth.addChangeListener(this);
         configChoice.addChangeListener(this);
         msgChoice.addChangeListener(this);
     }
 
     @Override
     public void clearGui(){
         super.clearGui();
         useProperties.setSelected(false);
         jndiICF.setText(""); // $NON-NLS-1$
         urlField.setText(""); // $NON-NLS-1$
         jndiConnFac.setText(""); // $NON-NLS-1$
         jmsDestination.setText(""); // $NON-NLS-1$
         jmsUser.setText(""); // $NON-NLS-1$
         jmsPwd.setText(""); // $NON-NLS-1$
-        textMessage.setText(""); // $NON-NLS-1$
+        textMessage.setInitialText(""); // $NON-NLS-1$
         messageFile.setFilename(""); // $NON-NLS-1$
         randomFile.setFilename(""); // $NON-NLS-1$
         msgChoice.setText(""); // $NON-NLS-1$
         configChoice.setText(USE_TEXT_RSC);
         updateConfig(USE_TEXT_RSC);
         msgChoice.setText(TEXT_MSG_RSC);
         iterations.setText("1"); // $NON-NLS-1$
         useAuth.setSelected(false);
         jmsUser.setEnabled(false);
         jmsPwd.setEnabled(false);
         destSetup.setText(DEST_SETUP_STATIC);
         useNonPersistentDelivery.setSelected(false);
         jmsPropertiesPanel.clear();
     }
 
     /**
      * the implementation loads the URL and the soap action for the request.
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         PublisherSampler sampler = (PublisherSampler) el;
         useProperties.setSelected(sampler.getUseJNDIPropertiesAsBoolean());
         jndiICF.setText(sampler.getJNDIInitialContextFactory());
         urlField.setText(sampler.getProviderUrl());
         jndiConnFac.setText(sampler.getConnectionFactory());
         jmsDestination.setText(sampler.getDestination());
         jmsUser.setText(sampler.getUsername());
         jmsPwd.setText(sampler.getPassword());
-        textMessage.setText(sampler.getTextMessage());
+        textMessage.setInitialText(sampler.getTextMessage());
         messageFile.setFilename(sampler.getInputFile());
         randomFile.setFilename(sampler.getRandomPath());
         configChoice.setText(sampler.getConfigChoice());
         msgChoice.setText(sampler.getMessageChoice());
         iterations.setText(sampler.getIterations());
         useAuth.setSelected(sampler.isUseAuth());
         jmsUser.setEnabled(useAuth.isSelected());
         jmsPwd.setEnabled(useAuth.isSelected());
         destSetup.setText(sampler.isDestinationStatic() ? DEST_SETUP_STATIC : DEST_SETUP_DYNAMIC);
         useNonPersistentDelivery.setSelected(sampler.getUseNonPersistentDelivery());
         jmsPropertiesPanel.configure(sampler.getJMSProperties());
         updateChoice(msgChoice.getText());
         updateConfig(sampler.getConfigChoice());
     }
 
     /**
      * When a widget state changes, it will notify this class so we can
      * enable/disable the correct items.
      */
     @Override
     public void stateChanged(ChangeEvent event) {
         if (event.getSource() == configChoice) {
             updateConfig(configChoice.getText());
         } else if (event.getSource() == msgChoice) {
             updateChoice(msgChoice.getText());
         } else if (event.getSource() == useProperties) {
             jndiICF.setEnabled(!useProperties.isSelected());
             urlField.setEnabled(!useProperties.isSelected());
         } else if (event.getSource() == useAuth) {
             jmsUser.setEnabled(useAuth.isSelected());
             jmsPwd.setEnabled(useAuth.isSelected());
         }
     }
     /**
      * Update choice contains the actual logic for hiding or showing Textarea if Bytes message
      * is selected
      *
      * @param command
      * @since 2.9
      */
     private void updateChoice(String command) {
         String oldChoice = configChoice.getText();
         if (BYTES_MSG_RSC.equals(command)) {
             String newChoice = USE_TEXT_RSC.equals(oldChoice) ? 
                     USE_FILE_RSC : oldChoice;
             configChoice.resetButtons(CONFIG_ITEMS_BYTES_MSG, newChoice);
             textMessage.setEnabled(false);
         } else {
             configChoice.resetButtons(CONFIG_ITEMS, oldChoice);
             textMessage.setEnabled(true);
         }
         validate();
     }
     /**
      * Update config contains the actual logic for enabling or disabling text
      * message, file or random path.
      *
      * @param command
      */
     private void updateConfig(String command) {
         if (command.equals(USE_TEXT_RSC)) {
             textMessage.setEnabled(true);
             messageFile.enableFile(false);
             randomFile.enableFile(false);
         } else if (command.equals(USE_RANDOM_RSC)) {
             textMessage.setEnabled(false);
             messageFile.enableFile(false);
             randomFile.enableFile(true);
         } else {
             textMessage.setEnabled(false);
             messageFile.enableFile(true);
             randomFile.enableFile(false);
         }
     }
     
     /**
      * @return JPanel that contains destination infos
      */
     private JPanel createDestinationPane() {
         JPanel pane = new JPanel(new BorderLayout(3, 0));
         pane.add(jmsDestination, BorderLayout.WEST);
         destSetup.setLayout(new BoxLayout(destSetup, BoxLayout.X_AXIS));
         pane.add(destSetup, BorderLayout.CENTER);
         useNonPersistentDelivery = new JCheckBox(JMeterUtils.getResString("jms_use_non_persistent_delivery"),false); //$NON-NLS-1$
         pane.add(useNonPersistentDelivery, BorderLayout.EAST);
         return pane;
     }
     
     /**
      * @return JPanel Panel with checkbox to choose auth , user and password
      */
     private JPanel createAuthPane() {
         JPanel pane = new JPanel();
         pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
         pane.add(useAuth);
         pane.add(Box.createHorizontalStrut(10));
         pane.add(jmsUser);
         pane.add(Box.createHorizontalStrut(10));
         pane.add(jmsPwd);
         return pane;
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSamplerGui.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSamplerGui.java
index 93a1152fc..0bcb22b0e 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSamplerGui.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSamplerGui.java
@@ -1,290 +1,293 @@
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
 
 package org.apache.jmeter.protocol.jms.control.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
+import javax.swing.JLabel;
 import javax.swing.JPanel;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.gui.util.HorizontalPanel;
+import org.apache.jmeter.gui.util.JSyntaxTextArea;
+import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.protocol.jms.sampler.JMSSampler;
+import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jorphan.gui.JLabeledChoice;
-import org.apache.jorphan.gui.JLabeledTextArea;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 /**
  * Configuration screen for Java Messaging Point-to-Point requests. <br>
  * Created on: October 28, 2004
  *
  */
 public class JMSSamplerGui extends AbstractSamplerGui {
 
     private static final long serialVersionUID = 240L;
 
     private JLabeledTextField queueConnectionFactory = new JLabeledTextField(
             JMeterUtils.getResString("jms_queue_connection_factory")); //$NON-NLS-1$
 
     private JLabeledTextField sendQueue = new JLabeledTextField(JMeterUtils.getResString("jms_send_queue")); //$NON-NLS-1$
 
     private JLabeledTextField receiveQueue = new JLabeledTextField(JMeterUtils.getResString("jms_receive_queue")); //$NON-NLS-1$
 
     private JLabeledTextField timeout = new JLabeledTextField(JMeterUtils.getResString("jms_timeout")); //$NON-NLS-1$
 
     private JLabeledTextField jmsSelector = new JLabeledTextField(JMeterUtils.getResString("jms_selector")); //$NON-NLS-1$
 
-    private JLabeledTextArea messageContent = new JLabeledTextArea(JMeterUtils.getResString("jms_msg_content")); //$NON-NLS-1$
+    private JSyntaxTextArea messageContent = new JSyntaxTextArea(15, 50); //$NON-NLS-1$
 
     private JLabeledTextField initialContextFactory = new JLabeledTextField(
             JMeterUtils.getResString("jms_initial_context_factory")); //$NON-NLS-1$
 
     private JLabeledTextField providerUrl = new JLabeledTextField(JMeterUtils.getResString("jms_provider_url")); //$NON-NLS-1$
 
     private String[] labels = new String[] { JMeterUtils.getResString("jms_request"), //$NON-NLS-1$
             JMeterUtils.getResString("jms_requestreply") }; //$NON-NLS-1$
 
     private JLabeledChoice oneWay = new JLabeledChoice(JMeterUtils.getResString("jms_communication_style"), labels); //$NON-NLS-1$
 
     private ArgumentsPanel jmsPropertiesPanel;
 
     private ArgumentsPanel jndiPropertiesPanel;
 
     private JCheckBox useNonPersistentDelivery;
 
     private JCheckBox useReqMsgIdAsCorrelId;
 
     private JCheckBox useResMsgIdAsCorrelId;
 
     public JMSSamplerGui() {
         init();
     }
 
     /**
      * Clears all fields.
      */
     @Override
     public void clearGui() {// renamed from clear
         super.clearGui();
         queueConnectionFactory.setText(""); // $NON-NLS-1$
         sendQueue.setText(""); // $NON-NLS-1$
         receiveQueue.setText(""); // $NON-NLS-1$
         ((JComboBox) oneWay.getComponentList().get(1)).setSelectedItem(JMeterUtils.getResString("jms_request")); //$NON-NLS-1$
         timeout.setText("");  // $NON-NLS-1$
         jmsSelector.setText(""); // $NON-NLS-1$
-        messageContent.setText(""); // $NON-NLS-1$
+        messageContent.setInitialText(""); // $NON-NLS-1$
         initialContextFactory.setText(""); // $NON-NLS-1$
         providerUrl.setText(""); // $NON-NLS-1$
         jmsPropertiesPanel.clear();
         jndiPropertiesPanel.clear();
     }
 
     @Override
     public TestElement createTestElement() {
         JMSSampler sampler = new JMSSampler();
         this.configureTestElement(sampler);
         transfer(sampler);
         return sampler;
     }
 
     private void transfer(JMSSampler element) {
         element.setQueueConnectionFactory(queueConnectionFactory.getText());
         element.setSendQueue(sendQueue.getText());
         element.setReceiveQueue(receiveQueue.getText());
 
         boolean isOneway = oneWay.getText().equals(JMeterUtils.getResString("jms_request")); //$NON-NLS-1$
         element.setIsOneway(isOneway);
 
         element.setNonPersistent(useNonPersistentDelivery.isSelected());
         element.setUseReqMsgIdAsCorrelId(useReqMsgIdAsCorrelId.isSelected());
         element.setUseResMsgIdAsCorrelId(useResMsgIdAsCorrelId.isSelected());
         element.setTimeout(timeout.getText());
         element.setJMSSelector(jmsSelector.getText());
         element.setContent(messageContent.getText());
 
         element.setInitialContextFactory(initialContextFactory.getText());
         element.setContextProvider(providerUrl.getText());
         Arguments jndiArgs = (Arguments) jndiPropertiesPanel.createTestElement();
         element.setJNDIProperties(jndiArgs);
 
         Arguments args = (Arguments) jmsPropertiesPanel.createTestElement();
         element.setJMSProperties(args);
 
     }
 
     /**
      *
      * @param element
      */
     @Override
     public void modifyTestElement(TestElement element) {
         this.configureTestElement(element);
         if (!(element instanceof JMSSampler)) return;
         JMSSampler sampler = (JMSSampler) element;
         transfer(sampler);
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         if (!(el instanceof JMSSampler)) return;
         JMSSampler sampler = (JMSSampler) el;
         queueConnectionFactory.setText(sampler.getQueueConnectionFactory());
         sendQueue.setText(sampler.getSendQueue());
         receiveQueue.setText(sampler.getReceiveQueue());
 
         JComboBox box = (JComboBox) oneWay.getComponentList().get(1);
         String selected = null;
         if (sampler.isOneway()) {
             selected = JMeterUtils.getResString("jms_request"); //$NON-NLS-1$
         } else {
             selected = JMeterUtils.getResString("jms_requestreply"); //$NON-NLS-1$
         }
         box.setSelectedItem(selected);
 
         useNonPersistentDelivery.setSelected(sampler.isNonPersistent());
         useReqMsgIdAsCorrelId.setSelected(sampler.isUseReqMsgIdAsCorrelId());
         useResMsgIdAsCorrelId.setSelected(sampler.isUseResMsgIdAsCorrelId());
 
         timeout.setText(sampler.getTimeout());
         jmsSelector.setText(sampler.getJMSSelector());
-        messageContent.setText(sampler.getContent());
+        messageContent.setInitialText(sampler.getContent());
         initialContextFactory.setText(sampler.getInitialContextFactory());
         providerUrl.setText(sampler.getContextProvider());
 
         jmsPropertiesPanel.configure(sampler.getJMSProperties());
         // (TestElement)
         // el.getProperty(JMSSampler.JMS_PROPERTIES).getObjectValue());
 
         jndiPropertiesPanel.configure(sampler.getJNDIProperties());
         // (TestElement)
         // el.getProperty(JMSSampler.JNDI_PROPERTIES).getObjectValue());
     }
 
     /**
      * Initializes the configuration screen.
      *
      */
     private void init() {
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel jmsQueueingPanel = new JPanel(new BorderLayout());
         jmsQueueingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("jms_queueing"))); //$NON-NLS-1$
 
         JPanel qcfPanel = new JPanel(new BorderLayout(5, 0));
         qcfPanel.add(queueConnectionFactory, BorderLayout.CENTER);
         jmsQueueingPanel.add(qcfPanel, BorderLayout.NORTH);
 
         JPanel sendQueuePanel = new JPanel(new BorderLayout(5, 0));
         sendQueuePanel.add(sendQueue);
         jmsQueueingPanel.add(sendQueuePanel, BorderLayout.CENTER);
 
         JPanel receiveQueuePanel = new JPanel(new BorderLayout(5, 0));
         receiveQueuePanel.add(jmsSelector,BorderLayout.SOUTH);
         receiveQueuePanel.add(receiveQueue,BorderLayout.NORTH);
         jmsQueueingPanel.add(receiveQueuePanel, BorderLayout.SOUTH);
 
         JPanel messagePanel = new JPanel(new BorderLayout());
         messagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("jms_message_title"))); //$NON-NLS-1$
 
         JPanel correlationPanel = new HorizontalPanel();
         correlationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("jms_correlation_title"))); //$NON-NLS-1$
 
         useReqMsgIdAsCorrelId = new JCheckBox(JMeterUtils.getResString("jms_use_req_msgid_as_correlid"),false); //$NON-NLS-1$
 
         useResMsgIdAsCorrelId = new JCheckBox(JMeterUtils.getResString("jms_use_res_msgid_as_correlid"),false); //$NON-NLS-1$
 
         correlationPanel.add(useReqMsgIdAsCorrelId);
         correlationPanel.add(useResMsgIdAsCorrelId);
 
         JPanel messageNorthPanel = new JPanel(new BorderLayout());
         JPanel onewayPanel = new HorizontalPanel();
         onewayPanel.add(oneWay);
         onewayPanel.add(correlationPanel);
         messageNorthPanel.add(onewayPanel, BorderLayout.NORTH);
 
         useNonPersistentDelivery = new JCheckBox(JMeterUtils.getResString("jms_use_non_persistent_delivery"),false); //$NON-NLS-1$
 
         JPanel timeoutPanel = new HorizontalPanel();
         timeoutPanel.add(timeout);
         timeoutPanel.add(useNonPersistentDelivery);
         messageNorthPanel.add(timeoutPanel, BorderLayout.SOUTH);
 
         messagePanel.add(messageNorthPanel, BorderLayout.NORTH);
 
         JPanel messageContentPanel = new JPanel(new BorderLayout());
-        messageContentPanel.add(messageContent);
+        messageContentPanel.add(new JLabel(JMeterUtils.getResString("jms_msg_content")), BorderLayout.NORTH);
+        messageContentPanel.add(new JTextScrollPane(messageContent), BorderLayout.CENTER);
         Dimension pref = new Dimension(400, 150);
         messageContent.setPreferredSize(pref);
         messagePanel.add(messageContentPanel, BorderLayout.CENTER);
 
         jmsPropertiesPanel = new ArgumentsPanel(JMeterUtils.getResString("jms_props")); //$NON-NLS-1$
         messagePanel.add(jmsPropertiesPanel, BorderLayout.SOUTH);
 
         Box mainPanel = Box.createVerticalBox();
         add(mainPanel, BorderLayout.CENTER);
         mainPanel.add(jmsQueueingPanel, BorderLayout.NORTH);
         mainPanel.add(messagePanel, BorderLayout.CENTER);
         JPanel jndiPanel = createJNDIPanel();
         mainPanel.add(jndiPanel, BorderLayout.SOUTH);
 
     }
 
     /**
      * Creates the panel for the JNDI configuration.
      *
      * @return the JNDI Panel
      */
     private JPanel createJNDIPanel() {
         JPanel jndiPanel = new JPanel(new BorderLayout());
         jndiPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("jms_jndi_props"))); //$NON-NLS-1$
 
         JPanel contextPanel = new JPanel(new BorderLayout(10, 0));
         contextPanel.add(initialContextFactory);
         jndiPanel.add(contextPanel, BorderLayout.NORTH);
 
         JPanel providerPanel = new JPanel(new BorderLayout(10, 0));
         providerPanel.add(providerUrl);
         jndiPanel.add(providerPanel, BorderLayout.SOUTH);
 
         jndiPropertiesPanel = new ArgumentsPanel(JMeterUtils.getResString("jms_jndi_props")); //$NON-NLS-1$
         jndiPanel.add(jndiPropertiesPanel);
         return jndiPanel;
     }
 
     @Override
     public String getLabelResource() {
         return "jms_point_to_point"; //$NON-NLS-1$ // TODO - probably wrong
     }
 
 }
\ No newline at end of file
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 9d7e3eb0f..eab0a5a0b 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,489 +1,490 @@
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
 
 
 <!--  =================== 2.10 =================== -->
 
 <h1>Version 2.10</h1>
 
 <h2>New and Noteworthy</h2>
 
 <h3><u>Core Improvements:</u></h3>
 
 <h4>* A Huge performance improvement has been made on High Throughput Tests (no pause), see <bugzilla>54777</bugzilla></h4>
 
 <h4>* New CSS/JQuery Tester in View Tree Results that makes CSS/JQuery Extractor a first class
 citizen in JMeter, you can now test your expressions very easily</h4>
 <p>
 <figure width="1144" height="638" image="changes/2.10/01_css_jquery_tester.png"></figure>
 </p>
 
 <h4>* You can now load test MongoDB through new MongoDB Source Config</h4>
 <p>
 <figure width="912" height="486" image="changes/2.10/02_mongodb_source_config.png"></figure>
 </p>
 <p>
 <figure width="850" height="687" image="changes/2.10/14_mongodb_jsr223.png"></figure>
 </p>
 
 <h4>* Kerberos authentication has been added to Auth Manager</h4>
 <p>
 <figure width="1005" height="364" image="changes/2.10/15_kerberos.png"></figure>
 </p>
 
 <h4>* device can now be used in addition to source IP address</h4>
 
 <p>
 <figure width="1087" height="699" image="changes/2.10/16_device.png"></figure>
 </p>
 
 <h4>* You can now do functional testing of MongoDB scripts through new MongoDB Script</h4>
 <p>
 <figure width="906" height="313" image="changes/2.10/03_mongodb_script_alpha.png"></figure>
 </p>
 
 <h4>* Timeout has been added to OS Process Sampler</h4>
 <p>
 <figure width="684" height="586" image="changes/2.10/17_os_process_timeout.png"></figure>
 </p>
 
 <h4>* Query timeout has been added to JDBC Request</h4>
 <p>
 <figure width="540" height="600" image="changes/2.10/04_jdbc_request_timeout.png"></figure>
 </p>
 
 <h4>* New functions (__urlencode and __urldecode) are now available to encode/decode URL encoded chars</h4>
 <p>
 <figure width="512" height="240" image="changes/2.10/05_urlencode_function.png"></figure>
 </p>
 
 <h4>* Continuous Integration is now eased by addition of a new flag that forces NON-GUI JVM to exit after test end</h4>
 <p>See jmeter property:</p>
 <code>jmeterengine.force.system.exit</code>
 
 <h4>* HttpSampler now allows DELETE Http Method to have a body (works for HC4 and HC31 implementations). This allows for example to test Elastic Search APIs</h4>
 <p>
 <figure width="573" height="444" image="changes/2.10/06_http_request_delete_method.png"></figure>
 </p>
 
 <h4>* Distributed testing has been improved</h4>
 <p>
 <ul>
 <li>
 Number of threads on each node are now reported to controller.
 <p>
 <figure width="988" height="355" image="changes/2.10/17_threads_summariser.png"></figure>
 </p>
 <p>
 <figure width="125" height="33" image="changes/2.10/17_threads_gui.png"></figure>
 </p>
 
 </li>
 <li>Performance improvement on BatchSampleSender(<bugzilla>55423</bugzilla>)</li>
 <li>Addition of 2 SampleSender modes (StrippedAsynch and StrippedDiskStore), see jmeter.properties</li>
 </ul>
 </p>
 
 <h4>* ModuleController has been improved to better handle changes to referenced controllers</h4>
 
 <h4>* Improved class loader configuration, see <bugzilla>55503</bugzilla></h4>
 <p>
 <ul>
 <li>New property "plugin_dependency_paths" for plugin dependencies</li>
 <li>Properties "search_paths", "user.classpath" and "plugin_dependency_paths"
     now automatically add all jars from configured directories</li>
 </ul>
 </p>
 
 <h4>* Best-practices section has been improved, ensure you read it to get the most out of JMeter</h4>
 
 <h3><u>GUI and ergonomy Improvements:</u></h3>
 
 
 <h4>* New Templates feature that allows you to create test plan from existing template or merge
 template into your Test Plan</h4>
 <p>
 <figure width="428" height="130" image="changes/2.10/07_jmeter_templates_icon.png"></figure>
 </p>
 <p>
 <figure width="781" height="472" image="changes/2.10/08_jmeter_templates_box.png"></figure>
 </p>
 
 <h4>* Workbench can now be saved</h4>
 <p>
 <figure width="489" height="198" image="changes/2.10/09_save_workbench.png"></figure>
 </p>
 
 <h4>* Syntax color has been added to scripts elements (BeanShell, BSF, and JSR223), MongoDB and JDBC elements making code much more readable</h4>
 <p>BSF Sampler with syntax color
 <figure width="915" height="620" image="changes/2.10/10_color_syntax_bsf_sampler.png"></figure>
 </p>
 <p>JSR223 Pre Processor with syntax color
 <figure width="911" height="614" image="changes/2.10/11_color_syntax_jsr223_preprocessor.png"></figure>
 </p>
 
 <h4>* JMeter GUI can now be fully Internationalized, all remaining issues have been fixed</h4>
 <h5>Currently French has all its labels translated. Other languages are partly translated, feel free to 
 contribute translations by reading <a href="localising/index.html">Localisation (Translator's Guide)</a></h5>
 
 <h4>* Moving elements in Test plan has been improved in many ways</h4>
 <h5>Drag and drop of elements in Test Plan tree is now much easier and possible on multiple nodes</h5>
 <p>
 <figure width="894" height="236" image="changes/2.10/12_drap_n-drop_multiple.png"></figure>
 </p>
 <p>
 <b>Note that due to this <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6560955">bug in Java</a>,
 you cannot drop a node after last node. The workaround is to drop it before this last node and then Drag and Drop the last node 
 before the one you just dropped.</b>
 </p>
 <h5>New shortcuts have been added to move elements in the tree. </h5>
 <p>(alt + Arrow Up) and (alt + Arrow Down) move the element within the parent node.<br/>
 (alt + Arrow Left) and (alt + Arrow Right) move the element up and down in the tree depth</p>
 
 <h4>* Response Time Graph Y axis can now be scaled</h4>
 <p>
 <figure width="947" height="596" image="changes/2.10/13_response_time_graph_y_scale.png"></figure>
 </p>
 
 <h4>* JUnit Sampler gives now more details on configuration errors</h4>
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see <bugzilla>52496</bugzilla>).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </li>
 
 <li>Webservice sampler does not consider the HTTP response status to compute the status of a response, thus a response 500 containing a non empty body will be considered as successful, see <bugzilla>54006</bugzilla>.
 To workaround this issue, ensure you always read the response and add a Response Assertion checking text inside the response.
 </li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 these only apply to a locally run test; they do not include any threads started on remote systems when using client-server mode, (see <bugzilla>54152</bugzilla>).
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
 With Java 1.6 and Gnome 3 on Linux systems, the JMeter menu may not work correctly (shift between mouse's click and the menu). 
 This is a known Java bug (see  <bugzilla>54477 </bugzilla>). 
 A workaround is to use a Java 7 runtime (OpenJDK or Oracle JDK).
 </li>
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 <p>SMTP Sampler now uses eml file subject if subject field is empty</p>
 
 <p>With this version autoFlush has been turned off on PrintWriter in charge of writing test results. 
 This results in improved throughput for intensive tests but can result in more test data loss in case
 of JMeter crash (very rare). To revert to previous behaviour set jmeter.save.saveservice.autoflush property to true. </p>
 
 <p>
 Shortcut for Function Helper Dialog is now CTRL+SHIFT+F1 (CMD + SHIFT + F1 for Mac OS).
 The original key sequence (Ctrl+F1) did not work in some locations (it is consumed by the Java Swing ToolTipManager).
 It was therefore necessary to change the shortcut.
 </p>
 
 <p>
 Webservice (SOAP) Request has been removed by default from GUI as Element is deprecated (use HTTP Sampler with Body Data), if you need to show it, see property not_in_menu in jmeter.properties
 </p>
 
 <p>
 Transaction Controller now sets Response Code of Generated Parent Sampler (if Generate Parent Sampler is checked) to response code of first failing child in case of failure of one of the children, in previous versions Response Code was empty.
 </p>
 
 <p>
 In previous versions, IncludeController could run Test Elements located inside a Thread Group, this behaviour which was not documented could result in weird behaviour, it has been removed in this version (see <bugzilla>55464</bugzilla>). 
 The correct way to include Test Elements is to use Test Fragment as stated in documentation of Include Controller.
 </p>
 
 <p>
 The retry count for the HttpClient 3.1 and HttpClient 4.x samplers has been changed to 0.
 Previously the default was 1, which could cause unexpected additional traffic.
 </p>
 
 <p>Starting with this version, JMeter Proxy Server tries to detect when a sample is the result of a previous
 redirect. If the current response is a redirect, JMeter will save the redirect URL. When the next request is received, 
 it is compared with the saved redirect URL and if there is a match, JMeter will disable the generated sample.
 To revert to previous behaviour, set the property <code>proxy.redirect.disabling=false</code>
  </p>
 
 <p>__escapeOroRegexpChars function does not trim anymore the value when escaping it from ORO reserved characters (see <bugzilla>55328</bugzilla>)</p>
 
 <p>commons-lang-2.6.jar has been removed from embedded libraries in jmeter/lib folder. If you use any plugin or third-party code that depends on it, you need to add it in jmeter/lib folder</p>
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li><bugzilla>54627</bugzilla> - JMeter Proxy GUI: Type of sampler setting takes the whole screen when there are samplers with long names.</li>
 <li><bugzilla>54629</bugzilla> - HTMLParser does not extract &amp;lt;object&amp;gt; tag urls.</li>
 <li><bugzilla>55023</bugzilla> - SSL Context reuse feature (51380) adversely affects non-ssl request performance/throughput. based on analysis by Brent Cromarty (brent.cromarty at yahoo.ca)</li>
 <li><bugzilla>55092</bugzilla> - Log message "WARN - jmeter.protocol.http.sampler.HTTPSamplerBase: Null URL detected (should not happen)" displayed when embedded resource URL is malformed.</li>
 <li><bugzilla>55161</bugzilla> - Useless processing in SoapSampler.setPostHeaders. Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>54482</bugzilla> - HC fails to follow redirects with non-encoded chars.</li>
 <li><bugzilla>54142</bugzilla> - HTTP Proxy Server throws an exception when path contains "|" character.</li>
 <li><bugzilla>55388</bugzilla> - HC3 does not allow IP Source field to override httpclient.localaddress.</li>
 <li><bugzilla>55450</bugzilla> - HEAD redirects should remain as HEAD</li>
 <li><bugzilla>55455</bugzilla> - HTTPS with HTTPClient4 ignores cps setting</li>
 <li><bugzilla>55502</bugzilla> - Proxy generates empty http:/ entries when recording</li>
 <li><bugzilla>55504</bugzilla> - Proxy incorrectly issues CONNECT requests when browser prompts for certificate override</li>
 <li><bugzilla>55506</bugzilla> - Proxy should deliver failed requests to any configured Listeners</li>
 <li><bugzilla>55545</bugzilla> - HTTP Proxy Server GUI should not allow both Follow and Auto redirect to be selected</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li><bugzilla>54913</bugzilla> - JMSPublisherGui incorrectly restores its state. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55027</bugzilla> - Test Action regression, duration value is not recorded (nightly build).</li>
 <li><bugzilla>55163</bugzilla> - BeanShellTestElement fails to quote string when calling testStarted(String)/testEnded(String).</li>
 <li><bugzilla>55349</bugzilla> - NativeCommand hangs if no input file is specified and the application requests input.</li>
 <li><bugzilla>55462</bugzilla> - System Sampler should not change the sampler label if a sample fails</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54467</bugzilla> - Loop Controller: compute loop value only once per parent iteration.</li>
 <li><bugzilla>54985</bugzilla> - Make Transaction Controller set Response Code of Generated Parent Sampler to response code of first failing child in case of failure of one of its children. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>54950</bugzilla> - ModuleController : Changes to referenced Module are not taken into account if changes occur after first run and referenced node is disabled.</li>
 <li><bugzilla>55201</bugzilla> - ForEach controller excludes start index and includes end index (clarified documentation).</li>
 <li><bugzilla>55334</bugzilla> - Adding Include Controller to test plan (made of Include Controllers) without saving TestPlan leads to included code not being taken into account until save.</li>
 <li><bugzilla>55375</bugzilla> -  StackOverflowError with ModuleController in Non-GUI mode if its name is the same as the target node.</li>
 <li><bugzilla>55464</bugzilla> - Include Controller running included thread group</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54589</bugzilla> - View Results Tree have a lot of Garbage characters if html page uses double-byte charset.</li>
 <li><bugzilla>54753</bugzilla> - StringIndexOutOfBoundsException at SampleResult.getSampleLabel() if key_on_threadname=false when using Statistical mode.</li>
 <li><bugzilla>54685</bugzilla> - ArrayIndexOutOfBoundsException if "sample_variable" is set in client but not server.</li>
 <li><bugzilla>55111</bugzilla> - ViewResultsTree: text not refitted if vertical scrollbar is required. Contributed by Milamber</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54540</bugzilla> - "HTML Parameter Mask" are not marked deprecated in the IHM.</li>
 <li><bugzilla>54575</bugzilla> - CSS/JQuery Extractor : Choosing JODD Implementation always uses JSOUP.</li>
 <li><bugzilla>54901</bugzilla> - Response Assertion GUI behaves weirdly.</li>
 <li><bugzilla>54924</bugzilla> - XMLAssertion uses JMeter JVM file.encoding instead of response encoding and does not clean threadlocal variable.</li>
 <li><bugzilla>53679</bugzilla> -  Constant Throughput Timer bug with localization. Reported by Ludovic Garcia</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>55328</bugzilla> - __escapeOroRegexpChars trims spaces.</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>55437</bugzilla> - ComboStringEditor does not translate EDIT and UNDEFINED strings on language change</li>
 <li><bugzilla>55501</bugzilla> - Incorrect encoding for French description of __char function. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54504</bugzilla> - Resource string not found: [clipboard_node_read_error].</li>
 <li><bugzilla>54538</bugzilla> - GUI: context menu is too big.</li>
 <li><bugzilla>54847</bugzilla> - Cut &amp; Paste is broken with tree multi-selection. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54870</bugzilla> - Tree drag and drop may lose leaf nodes (affected nightly build). Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>55056</bugzilla> - wasted work in Data.append(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55129</bugzilla> -  Change Javadoc generation per CVE-2013-1571, VU#225657.</li>
 <li><bugzilla>55187</bugzilla> - Integer overflow when computing ONE_YEAR_MS in HTTP CacheManager.</li>
 <li><bugzilla>55208</bugzilla> - JSR223 language entries are duplicated; fold to lower case.</li>
 <li><bugzilla>55203</bugzilla> - TestBeanGUI - wrong language settings found.</li>
 <li><bugzilla>55065</bugzilla> - Useless processing in Spline3.converge(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55064</bugzilla> - Useless processing in ReportTreeListener.isValidDragAction(). Contributed by Adrian Nistor (nistor1 at illinois.edu)</li>
 <li><bugzilla>55242</bugzilla> - BeanShell Client jar throws exceptions after upgrading to 2.8.</li>
 <li><bugzilla>55288</bugzilla> - JMeter should default to 0 retries for HTTP requests.</li>
 <li><bugzilla>55405</bugzilla> - ant download_jars task fails if lib/api or lib/doc are missing. Contributed by Antonio Gomes Rodrigues.</li>
 <li><bugzilla>55427</bugzilla> - TestBeanHelper should ignore properties not supported by GenericTestBeanCustomizer</li>
 <li><bugzilla>55459</bugzilla> - Elements using ComboStringEditor lose the input value if user selects another Test Element</li>
 <li><bugzilla>54152</bugzilla> - In distributed testing : activeThreads always show 0 in GUI and Summariser</li>
 <li><bugzilla>55509</bugzilla> - Allow Plugins to be notified of remote thread number progression</li>
 <li><bugzilla>55572</bugzilla> - Detail popup of parameter does not show a Scrollbar when content exceeds display</li>
 <li><bugzilla>55580</bugzilla> -  Help pane does not scroll to start for &lt;a href="#"&gt; links</li>
 <li><bugzilla>55600</bugzilla> - JSyntaxTextArea : Strange behaviour on first undo</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>HTTP Request: Small user interaction improvements in Row parameter Detail Box. Contributed by Milamber</li>
 <li><bugzilla>55255</bugzilla> - Allow Body in HTTP DELETE method to support API that use it (like ElasticSearch).</li>
 <li><bugzilla>53480</bugzilla> - Add Kerberos support to Http Sampler (HttpClient4). Based on patch by Felix Schumacher (felix.schumacher at internetallee.de)</li>
 <li><bugzilla>54874</bugzilla> - Support device in addition to source IP address. Based on patch by Dan Fruehauf (malkodan at gmail.com)</li>
 <li><bugzilla>55488</bugzilla> - Add .ico and .woff file extension to default suggested exclusions in proxy recorder. Contributed by Antonio Gomes Rodrigues</li>
 <li><bugzilla>55525</bugzilla> - Proxy should support alias for keyserver entry</li>
 <li><bugzilla>55531</bugzilla> - Proxy recording and redirects. Added code to disable redirected samples.</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li><bugzilla>54788</bugzilla> - JMS Point-to-Point Sampler - GUI enhancements to increase readability and ease of use. Contributed by Bruno Antunes (b.m.antunes at gmail.com)</li>
 <li><bugzilla>54798</bugzilla> - Using subject from EML-file for SMTP Sampler. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>54759</bugzilla> - SSLPeerUnverifiedException using HTTPS , property documented.</li>
 <li><bugzilla>54896</bugzilla> - JUnit sampler gives only "failed to create an instance of the class" message with constructor problems.</li>
 <li><bugzilla>55084</bugzilla> - Add timeout support for JDBC Request. Contributed by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>55403</bugzilla> - Enhancement to OS sampler: Support for timeout</li>
 <li><bugzilla>55518</bugzilla> - Add ability to limit number of cached PreparedStatements per connection when "Prepared Select Statement", "Prepared Update Statement" or "Callable Statement" query type is selected</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li><bugzilla>54271</bugzilla> - Module Controller breaks if test plan is renamed.</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bugzilla>54532</bugzilla> - Improve Response Time Graph Y axis scale with huge values or small values (&amp;lt; 1000ms). Add a new field to define increment scale. Contributed by Milamber based on patch by Luca Maragnani (luca.maragnani at gmail.com)</li>
 <li><bugzilla>54576</bugzilla> - View Results Tree : Add a CSS/JQuery Tester.</li>
 <li><bugzilla>54777</bugzilla> - Improve Performance of default ResultCollector. Based on patch by Mikhail Epikhin (epihin-m at yandex.ru)</li>
 <li><bugzilla>55389</bugzilla> - Show IP source address in request data</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bugzilla>54789</bugzilla> - XPath Assertion - GUI enhancements to increase readability and ease of use.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bugzilla>54991</bugzilla> - Add functions to encode/decode URL encoded chars (__urlencode and __urldecode). Contributed by Milamber.</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li><bugzilla>55241</bugzilla> - Need GUI Editor to process fields which are based on Enums with localised display strings</li>
 <li><bugzilla>55440</bugzilla> - ComboStringEditor should allow tags to be language dependent</li>
 <li><bugzilla>55432</bugzilla> - CSV Dataset Config loses sharing mode when switching languages</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bugzilla>54584</bugzilla> - MongoDB plugin. Based on patch by Jan Paul Ettles (janpaulettles at gmail.com)</li>
 <li><bugzilla>54669</bugzilla> - Add flag forcing non-GUI JVM to exit after test. Contributed by Scott Emmons</li>
 <li><bugzilla>42428</bugzilla> - Workbench not saved with Test Plan. Contributed by Dzmitry Kashlach (dzmitrykashlach at gmail.com)</li>
 <li><bugzilla>54825</bugzilla> - Add shortcuts to move elements in the tree. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54834</bugzilla> - Improve Drag &amp; Drop in the jmeter tree. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54839</bugzilla> - Set the application name on Mac. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54841</bugzilla> - Correctly handle the quit shortcut on Mac Os (CMD-Q). Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54844</bugzilla> - Set the application icon on Mac Os. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54864</bugzilla> - Enable multi selection drag &amp; drop in the tree without having to start dragging before releasing Shift or Control. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54945</bugzilla> - Add Shutdown Hook to enable trapping kill or CTRL+C signals.</li>
 <li><bugzilla>54990</bugzilla> - Download large files avoiding outOfMemory.</li>
 <li><bugzilla>55085</bugzilla> - UX Improvement : Ability to create New Test Plan from Templates. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55172</bugzilla> - Provide plugins a way to add Top Menu and menu items.</li>
 <li><bugzilla>55202</bugzilla> - Add syntax color for scripts elements (BeanShell, BSF, and JSR223) and JDBC elements with RSyntaxTextArea. Contributed by Milamber based on patch by Marko Vlahovic (vlahovic74 at gmail.com)</li>
 <li><bugzilla>55175</bugzilla> - HTTPHC4Impl refactoring to allow better inheritance.</li>
 <li><bugzilla>55236</bugzilla> - Templates - provide button to reload template details.</li>
 <li><bugzilla>55237</bugzilla> - Template system should support relative fileName entries.</li>
 <li><bugzilla>55423</bugzilla> - BatchSampleSender: Reduce locking granularity by moving listener.processBatch outside of synchronized block</li>
 <li><bugzilla>55424</bugzilla> - Add Stripping to existing SampleSenders</li>
 <li><bugzilla>55451</bugzilla> - Test Element GUI with JSyntaxTextArea scroll down when text content is long enough to add a Scrollbar</li>
 <li><bugzilla>55513</bugzilla> - StreamCopier cannot be used with System.err or System.out as it closes the output stream</li>
 <li><bugzilla>55514</bugzilla> - SystemCommand should support arbitrary input and output streams</li>
 <li><bugzilla>55515</bugzilla> - SystemCommand should support chaining of commands</li>
+<li><bugzilla>55606</bugzilla> - Use JSyntaxtTextArea for Http Request, JMS Test Elements</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Updated to jsoup-1.7.2</li>
 <li><bugzilla>54776</bugzilla> - Update the dependency on Bouncy Castle to 1.48. Contributed by Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Updated to HttpComponents Client 4.2.6 (from 4.2.3)</li>
 <li>Updated to HttpComponents Core 4.2.5 (from 4.2.3)</li>
 <li>Updated to commons-codec 1.8 (from 1.6)</li>
 <li>Updated to commons-io 2.4 (from 2.2)</li>
 <li>Updated to commons-logging 1.1.3 (from 1.1.1)</li>
 <li>Updated to commons-net 3.3 (from 3.1)</li>
 <li>Updated to jdom-1.1.3 (from 1.1.2)</li>
 <li>Updated to jodd-lagarto and jodd-core 3.4.4 (from 3.4.1)</li>
 <li>Updated to junit 4.11 (from 4.10)</li>
 <li>Updated to slf4j-api 1.7.5 (from 1.7.2)</li>
 <li>Updated to tika 1.4 (from 1.3)</li>
 <li>Updated to xmlgraphics-commons 1.5 (from 1.3.1)</li>
 <li>Updated to xstream 1.4.4 (from 1.4.2)</li>
 <li>Updated to BouncyCastle 1.49 (from 1.48)</li>
 <li><bugzilla>54912</bugzilla> - JMeterTreeListener should use constants. Contributed by Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><bugzilla>54903</bugzilla> - Remove the dependency on the Activation Framework. Contributed by Emmanuel Bourg (ebourg at apache.org)</li>
 <li>Moved commons-lang (2.6) to lib/doc as it's only needed by Velocity.</li>
 <li>Re-organised and simplified NOTICE and LICENSE files.</li>
 <li><bugzilla>55411</bugzilla> -  NativeCommand could be useful elsewhere. Copied code to o.a.jorphan.exec.</li>
 <li><bugzilla>55435</bugzilla> - ComboStringEditor could be simplified to make most settings final</li>
 <li><bugzilla>55436</bugzilla> - ComboStringEditor should implement ClearGui</li>
 <li><bugzilla>55463</bugzilla> - Component.requestFocus() is discouraged; use requestFocusInWindow() instead</li>
 <li><bugzilla>55486</bugzilla> - New JMeter Logo. Contributed by UBIK Load Pack (support at ubikloadpack.com)</li>
 <li><bugzilla>55548</bugzilla> - Tidy up use of TestElement.ENABLED; use TestElement.isEnabled()/setEnabled() throughout</li>
 </ul>
 
 <h2>Thanks</h2>
 <p>We thank all contributors mentioned in bug and improvement sections above.<br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 <li>Immanuel Hayden (immanuel.hayden at gmail.com)</li>
 <li>Danny Lade (dlade at web.de)</li>
 <li>Brent Cromarty (brent.cromarty at yahoo.ca)</li>
 <li>Wolfgang Heider (wolfgang.heider at racon.at)</li>
 <li>Shmuel Krakower (shmulikk at gmail.com)</li>
 </ul>
 
  </p>
 </section> 
 </body> 
 </document>
