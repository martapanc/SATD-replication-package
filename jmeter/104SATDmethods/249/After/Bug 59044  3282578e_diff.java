diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index 603a238e9..6d33b9774 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,785 +1,792 @@
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
 import javax.swing.UIManager;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.JSyntaxTextArea;
 import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
+import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 
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
     
     private static final int TAB_RAW_BODY = 1;
 
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
     
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));
 
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
     private JSyntaxTextArea postBodyContent;
 
     // Tabbed pane that contains parameters and raw body
     private ValidationTabbedPane postContentTabbedPane;
 
     private boolean showRawBodyPane;
 
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
         this(showSamplerFields, true, true);
     }
 
     /**
      * @param showSamplerFields
      *            flag whether sampler fields should be shown
      * @param showImplementation
      *            Show HTTP Implementation
      * @param showRawBodyPane
      *            flag whether the raw body pane should be shown
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
         port = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(port);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(port, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getProxyPortPanel() {
         proxyPort = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(proxyPort);
         label.setFont(FONT_SMALL);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPort, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getConnectTimeOutPanel() {
         connectTimeOut = new JTextField(10);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
         label.setLabelFor(connectTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(connectTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getResponseTimeOutPanel() {
         responseTimeOut = new JTextField(10);
 
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
+            method.addChangeListener(this);
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
             postBodyContent = new JSyntaxTextArea(30, 50);// $NON-NLS-1$
             postContentTabbedPane.add(JMeterUtils.getResString("post_body"), new JTextScrollPane(postBodyContent));// $NON-NLS-1$
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
          *
          * @param index
          *            index to select
          * @param check
          *            flag whether to perform checks before setting the selected
          *            index
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
                     postBodyContent.setInitialText("");
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
-        if (e.getSource() == followRedirects){
+        else if (e.getSource() == followRedirects){
             if (followRedirects.isSelected()) {
                 autoRedirects.setSelected(false);
             }
         }
+        // disable the multi-part if not a post request
+        else if(e.getSource() == method) {
+            boolean isPostMethod = HTTPConstants.POST.equals(method.getText());
+            useMultipartForPost.setEnabled(isPostMethod);    
+        }
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
         Arguments arguments = (Arguments)argsPanel.createTestElement();
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
      * @param oldSelectedIndex
      *            the tab to check for data
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 01f59fde8..d427be5ff 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,434 +1,435 @@
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
 
 
 <!--  =================== 3.0 =================== -->
 
 <h1>Version 3.0</h1>
 
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
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>Since version 3.0, <code>jmeter.save.saveservice.assertion_results_failure_message</code> property value is true, meaning CSV file for results will contain an additional column containing assertion result response message, see <bugzilla>58978</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.print_field_names</code> property value is true, meaning CSV file for results will contain field names as first line in CSV, see <bugzilla>58991</bugzilla></li>
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
     <li>Since version 3.0, you can use Nashorn Engine (default javascript engine is Rhino) under Java8 for Elements that use Javascript Engine (__javaScript, IfController). If you want to use it, use property <code>javascript.use_rhino=false</code>, see <bugzilla>58406</bugzilla>.
     Note in future versions, we will switch to Nashorn by default, so users are encouraged to report any issue related to broken code when using Nashorn instead of Rhino.
     </li>
     <li>Since version 3.0, JMS Publisher will reload contents of file if Message source is "From File" and the ""Filename" field changes (through variables usage for example)</li>
     <li>org.apache.jmeter.gui.util.ButtonPanel has been removed, if you use it in your 3rd party plugin or custom development ensure you update your code. See <bugzilla>58687</bugzilla></li>
     <li>Property <code>jmeterthread.startearlier</code> has been removed. See <bugzilla>58726</bugzilla></li>   
     <li>Property <code>jmeterengine.startlistenerslater</code> has been removed. See <bugzilla>58728</bugzilla></li>   
     <li>Property <code>jmeterthread.reversePostProcessors</code> has been removed. See <bugzilla>58728</bugzilla></li>  
     <li>MongoDB elements (MongoDB Source Config, MongoDB Script) have been deprecated and will be removed in next version of jmeter. They do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. JMeter team advises not to use them anymore. See <bugzilla>58772</bugzilla></li>
     <li>Summariser listener now outputs a formated duration in HH:mm:ss (Hour:Minute:Second), it previously outputed seconds. See <bugzilla>58776</bugzilla></li>
     <li>WebService(SOAP) Request and HTML Parameter Mask which were deprecated in 2.13 version, have now been removed following our <a href="./usermanual/best-practices.html#deprecation">deprecation strategy</a></li>
     <li>org.apache.jmeter.protocol.http.visualizers.RequestViewHTTP.getQueryMap signature has changed, if you use it ensure you update your code. See <bugzilla>58845</bugzilla></li>
     <li><code>__jexl</code> function has been deprecated and will be removed in next version. See <bugzilla>58903</bugzilla></li>
     <li>JMS Subscriber will consider sample in error if number of received messages is not equals to expected number of messages. It previously considerer sample OK if only 1 message was received. See <bugzilla>58980</bugzilla></li>
     <li>Since version 3.0, HTTP(S) Test Script recorder uses default port 8888 as configured when using Recording Template. See <bugzilla>59006</bugzilla></li>
     <li>Since version 3.0, the parser for embedded ressources (replaced since 2.10 by Lagarto based implementation) relying on htmlparser library (HtmlParserHTMLParser) has been dropped as long as its dependencies.</li>     
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
     <li><bug>58811</bug>When pasting arguments between http samplers the column "Encode" and "Include Equals" are lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58843</bug>Improve the usable space in the HTTP sampler GUI. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58852</bug>Use less memory for <code>PUT</code> requests. The uploaded data will no longer be stored in the Sampler.
         This is the same behaviour as with <code>POST</code> requests.</li>
     <li><bug>58860</bug>HTTP Request : Add automatic variable generation in HTTP parameters table by right click. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58923</bug>normalize URIs when downloading embedded resources.</li>
     <li><bug>59005</bug>HTTP Sampler : Added WebDAV verb (SEARCH).</li>
     <li><bug>59006</bug>Change Default proxy recording port to 8888 to align it with Recording Template. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>58099</bug>Performance : Lazily initialize HttpClient SSL Context to avoid its initialization even for HTTP only scenarios</li>
     <li><bug>57577</bug>HttpSampler : Retrieve All Embedded Resources, add property "httpsampler.embedded_resources_use_md5" to only compute md5 and not keep response data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59023</bug>HttpSampler UI : rework the embedded resources labels and change default number of parallel downloads to 6. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59028</bug>Use SystemDefaultDnsResolver singleton. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59036</bug>FormCharSetFinder : Use JSoup instead of deprecated HTMLParser</li>
     <li><bug>59034</bug>Parallel downloads connection management is not realistic. Contributed by Benoit Wiart (benoit dot wiart at gmail.com) and Philippe Mouawad</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
     <li><bug>58300</bug> Make existing Java Samplers implement Interruptible</li>
     <li><bug>58160</bug>JMS Publisher : reload file content if file name changes. Based partly on a patch contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>58786</bug>JDBC Sampler : Replace Excalibur DataSource by more up to date library commons-dbcp2</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58406</bug>IfController : Allow use of Nashorn Engine if available for JavaScript evaluation</li>
     <li><bug>58281</bug>RandomOrderController : Improve randomization algorithm performance. Contributed by Graham Russell (jmeter at ham1.co.uk)</li> 
     <li><bug>58675</bug>Module controller : error message can easily be missed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58673</bug>Module controller : when the target element is disabled the default jtree icons are displayed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58674</bug>Module controller : it should not be possible to select more than one node in the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58680</bug>Module Controller : ui enhancement. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58989</bug>Record controller gui : add a button to clear all the recorded samples. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
 <li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li><bug>58681</bug>GraphiteBackendListener : Don't send data if no sampling occured</li>
 <li><bug>58776</bug>Summariser should display a more readable duration</li>
 <li><bug>58791</bug>Deprecate listeners:Distribution Graph (alpha) and Spline Visualizer</li>
 <li><bug>58849</bug>View Results Tree : Add a search panel to the request http view to be able to search in the parameters table. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58857</bug>View Results Tree : the request view http does not allow to resize the parameters table first column. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58955</bug>Request view http does not correctly display http parameters in multipart/form-data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
   <li><bug>58303</bug>Change usage of bouncycastle api in SMIMEAssertion to get rid of deprecation warnings.</li>
   <li><bug>58515</bug>New JSON related components : JSON-PATH Extractor and JSON-PATH Renderer in View Results Tree. Donated by Ubik Load Pack (support at ubikloadpack.com).</li>
   <li><bug>58698</bug>Correct parsing of auth-files in HTTP Authorization Manager.</li>
   <li><bug>58756</bug>CookieManager : Cookie Policy select box content must depend on Cookie implementation.</li>
   <li><bug>56358</bug>Cookie manager supports cross port cookies and RFC6265.</li>
   <li><bug>58773</bug>TestCacheManager : Add tests for CacheManager that use HttpClient 4</li>
   <li><bug>58742</bug>CompareAssertion : Reset data in TableEditor when switching between different CompareAssertions in gui.
       Based on a patch by Vincent Herilier (vherilier at gmail.com)</li>
   <li><bug>58848</bug>Argument Panel : when adding an argument (add button or from clipboard) scroll the table to the new line. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
   <li><bug>58865</bug>Allow empty default value in the Regular Expression Extractor. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>58477</bug> __javaScript function : Allow use of Nashorn engine for Java8 and later versions</li>
     <li><bug>58903</bug>Provide __jexl3 function that uses commons-jexl3 and deprecated __jexl (1.1) function</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>58736</bug>Add Sample Timeout support</li>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (<keycombo><keysym>Ctrl</keysym><keysym>1</keysym></keycombo> &hellip;
     <keycombo><keysym>Ctrl</keysym><keysym>9</keysym></keycombo>) to quick add elements into test plan.
     Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 <li><bug>58465</bug>JMS Read response field is badly named and documented</li>
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>$Revision$</code> to sha1 sum of the file itself.</li>
 <li><bug>58677</bug>TestSaveService#testLoadAndSave use the wrong set of files. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58679</bug>Replace the xpp pull parser in xstream with a java6+ standard solution. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58689</bug>Add shortcuts to expand / collapse a part of the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58696</bug>Create Ant task to setup Eclipse project</li>
 <li><bug>58653</bug>New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results. Developed by Ubik-Ingenierie and contributed by Decathlon S.A. and Ubik-Ingenierie / UbikLoadPack</li>
 <li><bug>58699</bug>Workbench changes neither saved nor prompted for saving upon close. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58726</bug>Remove the <code>jmeterthread.startearlier</code> parameter. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58728</bug>Drop old behavioural properties</li>
 <li><bug>57319</bug>Upgrade to HttpClient 4.5.X. With the big help from Oleg Kalnichevski.</li>
 <li><bug>58772</bug>Deprecate MongoDB related elements</li>
 <li><bug>58782</bug>ThreadGroup : Improve ergonomy</li>
 <li><bug>58165</bug>Show the time elapsed since the start of the load test in GUI mode. Partly based on a contribution from Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li><bug>58784</bug>Make JMeterUtils#runSafe sync/async awt invocation configurable and change the visualizers to use the async version.</li>
 <li><bug>58790</bug>Issue in CheckDirty and its relation to ActionRouter</li>
 <li><bug>58814</bug>JVM don't recognize option MaxLiveObjectEvacuationRatio; remove from comments</li>
 <li><bug>58810</bug>Config Element Counter (and others): Check Boxes Toggle Area Too Big</li>
 <li><bug>56554</bug>JSR223 Test Element : Generate compilation cache key automatically. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58911</bug>Header Manager : it should be possible to copy/paste between Header Managers. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58864</bug>Arguments Panel : when moving parameter with up / down, ensure that the selection remains visible. Based on a contribution by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58924</bug>Dashboard / report : It should be possible to export the generated graph as image (PNG). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58884</bug>JMeter report generator : need better error message. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58957</bug>Report/Dashboard: HTML Exporter does not create parent directories for output directory. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58968</bug>Add a new template to allow to record script with think time included. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li><bug>58978</bug>Settings defaults : Switch "jmeter.save.saveservice.assertion_results_failure_message" to true (after 2.13)</li>
 <li><bug>58991</bug>Settings defaults : Switch "jmeter.save.saveservice.print_field_names" to true (after 2.13)</li>
 <li><bug>58987</bug>Report/Dashboard: Improve error reporting.</li>
 <li><bug>58870</bug>TableEditor: minimum size is too small. Contributed by Vincent Herilier (vherilier at gmail.com)</li>
 <li><bug>59037</bug>Drop HtmlParserHTMLParser and dependencies on htmlparser and htmllexer</li>
 <li><bug>58933</bug>JSyntaxTextArea : Ability to set font.  Contributed by Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li><bug>58793</bug>Create developers page explaining how to build and contribute</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to tika-core and tika-parsers 1.12 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7.1 (from 1.7R5)</li>
 <li>Updated to jodd-3.6.7.jar (from 3.6.4)</li>
 <li>Updated to jsoup-1.8.3 (from 1.8.1)</li>
 <li>Updated to rsyntaxtextarea-2.5.8 (from 2.5.6)</li>
 <li>Updated to slf4j-1.7.12 (from 1.7.10)</li>
 <li>Updated to xmlgraphics-commons-2.0.1 (from 1.5)</li>
 <li>Updated to commons-collections-3.2.2 (from 3.2.1)</li>
 <li>Updated to commons-net 3.4 (from 3.3)</li>
 <li>Updated to slf4j 1.7.13 (from 1.7.12)</li>
 <li><bug>57981</bug>Require a minimum of Java 7. Partly contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li><bug>58684</bug>JMeterColor does not need to extend java.awt.Color. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58687</bug>ButtonPanel should die. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58705</bug>Make org.apache.jmeter.testelement.property.MultiProperty iterable. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58729</bug>Cleanup extras folder for maintainability</li>
 <li><bug>57110</bug>Fixed spelling+grammar, formatting, removed commented out code etc. Contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Correct instructions on running jmeter in help.txt. Contributed by Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li><bug>58704</bug>Non regression testing : Ant task batchtest fails if tests and run in a non en_EN locale and use a JMX file that uses a Csv DataSet</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58949</bug>Cleanup of ldap code. Based on a patch by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58967</bug>Use junit categories to exclude tests that need a gui. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59003</bug>ClutilTestCase testSingleArg8 and testSingleArg9 are identical</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com).</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
     <li><bug>58137</bug>JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them).</li>
     <li><bug>58201</bug>Make usage of port in the host header more consistent across the different http samplers.</li>
     <li><bug>58453</bug>HTTP Test Script Recorder : NullPointerException when disabling Capture HTTP Headers </li>
     <li><bug>57804</bug>HTTP Request doesn't reuse cached SSL context when using Client Certificates in HTTPS (only fixed for HttpClient4 implementation)</li>
     <li><bug>58800</bug>proxy.pause default value , fix documentation</li>
     <li><bug>58844</bug>Buttons enable / disable is broken in the arguments panel. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58861</bug>When clicking on up, down or detail while in a cell of the argument panel, newly added content is lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>57935</bug>SSL SNI extension not supported by HttpClient 4.2.6</li>
+    <li><bug>59044</bug>Http Sampler : It should not be possible to select the multipart encoding if the method is not POST. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
     <li><bug>58851</bug>Add a dependency to hamcrest-core to allow JUnit tests with annotations to work</li>
     <li><bug>58947</bug>Connect metric is wrong when ConnectException occurs</li>
     <li><bug>58980</bug>JMS Subscriber will return successful as long as 1 message is received. Contributed by Harrison Termotto (harrison dot termotto at stonybrook.edu)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58714</bug>Module controller : it should not be possible to add a timer as child. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug>SampleResultConverter should note that it cannot record non-TEXT data</li>
 <li><bug>58845</bug>Request http view doesn't display all the parameters. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58413</bug>ViewResultsTree : Request HTTP Renderer does not show correctly parameters that contain ampersand (&amp;). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>58079</bug>Do not cache HTTP samples that have a Vary header when using a HTTP CacheManager.</li>
 <li><bug>58912</bug>Response assertion gui : Deleting more than 1 selected row deletes only one row. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13 (Fixed group ids for Commons Pool and Math)</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
     <li><bug>58293</bug>SOAP/XML-RPC Sampler file browser generates NullPointerException</li>
     <li><bug>58685</bug>JDatefield : Make the modification of the date with up/down arrow work. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58693</bug>Fix "Cannot nest output folder 'jmeter/build/components' inside output folder 'jmeter/build' when setting up eclipse</li>
     <li><bug>58781</bug>Command line option "-?" shows Unknown option</li>
     <li><bug>58795</bug>NPE may occur in GuiPackage#getTestElementCheckSum with some 3rd party plugins</li>
     <li><bug>58913</bug>When closing jmeter should not interpret cancel as "destroy my test plan". Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58952</bug>Report/Dashboard: Generation of aggregated series in graphs does not work. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>58931</bug>New Report/Dashboard : Getting font errors under Firefox and Chrome (not Safari)</li>
     <li><bug>58932</bug>Report / Dashboard: Document clearly and log what report are not generated when saveservice options are not correct. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
 <li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li>Samoht-fr (https://github.com/Samoht-fr)</li>
 <li>Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><a href="http://www.decathlon.com">Decathlon S.A.</a></li>
 <li><a href="http://www.ubik-ingenierie.com">Ubik-Ingenierie S.A.S.</a></li>
 <li>Oleg Kalnichevski (olegk at apache.org)</li>
 <li>Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Florent Sabbe (f dot sabbe at ubik-ingenierie.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Harrison Termotto (harrison dot termotto at stonybrook.edu</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
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
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
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
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (SHIFT + up/down) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
