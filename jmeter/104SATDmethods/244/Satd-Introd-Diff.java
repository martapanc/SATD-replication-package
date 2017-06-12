diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/MultipartUrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/MultipartUrlConfigGui.java
index 3aac23640..2bd90a2a4 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/MultipartUrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/MultipartUrlConfigGui.java
@@ -1,216 +1,112 @@
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
-import java.awt.event.ActionEvent;
-import java.awt.event.ActionListener;
-import java.io.File;
 
 import javax.swing.BorderFactory;
 import javax.swing.BoxLayout;
-import javax.swing.JButton;
-import javax.swing.JFileChooser;
-import javax.swing.JLabel;
 import javax.swing.JPanel;
-import javax.swing.JTextField;
 
-import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.VerticalPanel;
+import org.apache.jmeter.protocol.http.gui.HTTPFileArgsPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
+import org.apache.jmeter.protocol.http.util.HTTPFileArg;
+import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.testelement.TestElement;
+import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 
-public class MultipartUrlConfigGui extends UrlConfigGui implements ActionListener {
+public class MultipartUrlConfigGui extends UrlConfigGui {
 
-	private JTextField filenameField;
-
-	private JTextField paramNameField;
-
-	private JTextField mimetypeField;
-
-	// TODO these are used as names for the GUI elements - are they needed? are they NLS?
-	private static final String FILENAME = "filename";
-
-	private static final String BROWSE = "browse";  // $NON-NLS-1$ used as an ActionName locally
-
-	private static final String PARAMNAME = "paramname";
-
-	private static final String MIMETYPE = "mimetype";
+	/**
+	 * Files panel that holds file informations to be uploaded by
+	 * http request.
+	 */
+	private HTTPFileArgsPanel filesPanel;
 
+	// used by HttpTestSampleGui
 	public MultipartUrlConfigGui() {
 		super();
 		init();
 	}
 
+	// not currently used
     public MultipartUrlConfigGui(boolean value) {
         super(value);
         init();
     }
 
-	public TestElement createTestElement() {
-		TestElement ce = super.createTestElement();
-
-		configureTestElement(ce);
-		ce.setProperty(HTTPSamplerBase.MIMETYPE, mimetypeField.getText());
-		ce.setProperty(HTTPSamplerBase.FILE_NAME, filenameField.getText());
-		ce.setProperty(HTTPSamplerBase.FILE_FIELD, paramNameField.getText());
-		return ce;
-	}
-
-	// does not appear to be used
-	// public void configureSampler(HTTPSamplerBase sampler)
-	// {
-	// sampler.setMimetype(mimetypeField.getText());
-	// sampler.setFileField(paramNameField.getText());
-	// sampler.setFilename(filenameField.getText());
-	// super.configureSampler(sampler);
-	// }
+    public void modifyTestElement(TestElement sampler) {
+        super.modifyTestElement(sampler);
+        filesPanel.modifyTestElement(sampler);
+    }
 
 	public void configure(TestElement el) {
 		super.configure(el);
-		mimetypeField.setText(el.getPropertyAsString(HTTPSamplerBase.MIMETYPE));
-		filenameField.setText(el.getPropertyAsString(HTTPSamplerBase.FILE_NAME));
-		paramNameField.setText(el.getPropertyAsString(HTTPSamplerBase.FILE_FIELD));
-	}
-
-	public String getLabelResource() {
-		return "url_multipart_config_title"; // $NON-NLS-1$
-	}
-
-	public void updateGui() {
-	}
-
-	public void actionPerformed(ActionEvent e) {
-		String name = e.getActionCommand();
-
-		if (name.equals(BROWSE)) {
-			JFileChooser chooser = FileDialoger.promptToOpenFile();
-
-			if (chooser == null) {
-				return;
-			}
-			File file = chooser.getSelectedFile();
-
-			if (file != null) {
-				filenameField.setText(file.getPath());
-			}
-		}
+        filesPanel.configure(el);
 	}
 
 	private void init() {// called from ctor, so must not be overridable
 		this.setLayout(new BorderLayout());
 
 		// WEB SERVER PANEL
 		VerticalPanel webServerPanel = new VerticalPanel();
 		webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
 				JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
 		final JPanel domainPanel = getDomainPanel();
 		final JPanel portPanel = getPortPanel();
 		domainPanel.add(portPanel,BorderLayout.EAST);
 		webServerPanel.add(domainPanel);
 		//webServerPanel.add(getPortPanel());
 
 		JPanel northPanel = new JPanel();
 		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
 		northPanel.add(getProtocolAndMethodPanel());
 		northPanel.add(getPathPanel());
 
 		// WEB REQUEST PANEL
 		JPanel webRequestPanel = new JPanel();
 		webRequestPanel.setLayout(new BorderLayout());
 		webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
 				JMeterUtils.getResString("web_request"))); // $NON-NLS-1$
 
 		webRequestPanel.add(northPanel, BorderLayout.NORTH);
 		webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);
-		webRequestPanel.add(getFilePanel(), BorderLayout.SOUTH);
+		webRequestPanel.add(getHTTPFileArgsPanel(), BorderLayout.SOUTH);
 
 		this.add(webServerPanel, BorderLayout.NORTH);
 		this.add(webRequestPanel, BorderLayout.CENTER);
 	}
 
-	protected JPanel getFilePanel() {
-		JPanel filePanel = new VerticalPanel();
-		filePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
-				JMeterUtils.getResString("send_file"))); // $NON-NLS-1$
-
-		filePanel.add(createFilenamePanel());
-		filePanel.add(createFileParamNamePanel());
-		filePanel.add(createFileMimeTypePanel());
-
-		return filePanel;
-	}
-
-	private JPanel createFileMimeTypePanel() {
-		mimetypeField = new JTextField(15);
-		mimetypeField.setName(MIMETYPE);
-
-		JLabel mimetypeLabel = new JLabel(JMeterUtils.getResString("send_file_mime_label")); // $NON-NLS-1$
-		mimetypeLabel.setLabelFor(mimetypeField);
-		JPanel mimePanel = new JPanel(new BorderLayout(5, 0));
-		mimePanel.add(mimetypeLabel, BorderLayout.WEST);
-		mimePanel.add(mimetypeField, BorderLayout.CENTER);
-		return mimePanel;
-	}
-
-	private JPanel createFileParamNamePanel() {
-		paramNameField = new JTextField(15);
-		paramNameField.setName(PARAMNAME);
-
-		JLabel paramNameLabel = new JLabel(JMeterUtils.getResString("send_file_param_name_label")); // $NON-NLS-1$
-		paramNameLabel.setLabelFor(paramNameField);
-
-		JPanel paramNamePanel = new JPanel(new BorderLayout(5, 0));
-		paramNamePanel.add(paramNameLabel, BorderLayout.WEST);
-		paramNamePanel.add(paramNameField, BorderLayout.CENTER);
-		return paramNamePanel;
-	}
-
-	private JPanel createFilenamePanel() {
-		filenameField = new JTextField(15);
-		filenameField.setName(FILENAME);
-
-		JLabel filenameLabel = new JLabel(JMeterUtils.getResString("send_file_filename_label")); // $NON-NLS-1$
-		filenameLabel.setLabelFor(filenameField);
-
-		JButton browseFileButton = new JButton(JMeterUtils.getResString("send_file_browse")); // $NON-NLS-1$
-		browseFileButton.setActionCommand(BROWSE);
-		browseFileButton.addActionListener(this);
-
-		JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
-		filenamePanel.add(filenameLabel, BorderLayout.WEST);
-		filenamePanel.add(filenameField, BorderLayout.CENTER);
-		filenamePanel.add(browseFileButton, BorderLayout.EAST);
-		return filenamePanel;
+	private JPanel getHTTPFileArgsPanel() {
+		filesPanel = new HTTPFileArgsPanel(JMeterUtils.getResString("send_file")); // $NON-NLS-1$
+		return filesPanel;
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.config.gui.UrlConfigGui#clear()
 	 */
 	public void clear() {
-		// TODO Auto-generated method stub
 		super.clear();
-		filenameField.setText(""); // $NON-NLS-1$
-		mimetypeField.setText(""); // $NON-NLS-1$
-		paramNameField.setText(""); // $NON-NLS-1$
+		filesPanel.clear();
 	}
 }
