diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index 1bf32d4ce..d6cb6ceb5 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,905 +1,914 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 import java.io.StringReader;
 import java.io.UnsupportedEncodingException;
 import java.util.Date;
 
 import javax.swing.BorderFactory;
 import javax.swing.ButtonGroup;
 import javax.swing.Icon;
 import javax.swing.ImageIcon;
 import javax.swing.JCheckBox;
 import javax.swing.JEditorPane;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextPane;
 import javax.swing.JTree;
 import javax.swing.ToolTipManager;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.text.BadLocationException;
 import javax.swing.text.ComponentView;
 import javax.swing.text.Document;
 import javax.swing.text.EditorKit;
 import javax.swing.text.Element;
 import javax.swing.text.Style;
 import javax.swing.text.StyleConstants;
 import javax.swing.text.StyledDocument;
 import javax.swing.text.View;
 import javax.swing.text.ViewFactory;
 import javax.swing.text.html.HTML;
 import javax.swing.text.html.HTMLEditorKit;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.DefaultTreeModel;
 import javax.swing.tree.TreePath;
 import javax.swing.tree.TreeSelectionModel;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 /**
  * Allows the tester to view the textual response from sampling an Entry. This
  * also allows to "single step through" the sampling process via a nice
  * "Continue" button.
  * 
  * Created 2001/07/25
  */
 public class ViewResultsFullVisualizer extends AbstractVisualizer implements ActionListener, TreeSelectionListener,
 		Clearable {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private static final String XML_PFX = "<?xml "; // $NON-NLS-1$
 
 	public final static Color SERVER_ERROR_COLOR = Color.red;
 
 	public final static Color CLIENT_ERROR_COLOR = Color.blue;
 
 	public final static Color REDIRECT_COLOR = Color.green;
 
 	private static final String DOWNLOAD_LABEL = "Download embedded resources";
 
 	private static final String HTML_BUTTON_LABEL = "Render HTML";
 
 	private static final String XML_BUTTON_LABEL = "Render XML";
 
 	private static final String TEXT_BUTTON_LABEL = "Show Text";
 
 	private static final String TEXT_HTML = "text/html"; // $NON-NLS-1$
 
 	private static final String HTML_COMMAND = "html"; // $NON-NLS-1$
 
 	private static final String XML_COMMAND = "xml"; // $NON-NLS-1$
 
 	private static final String TEXT_COMMAND = "text"; // $NON-NLS-1$
 
 	private static final String STYLE_SERVER_ERROR = "ServerError"; // $NON-NLS-1$
 
 	private static final String STYLE_CLIENT_ERROR = "ClientError"; // $NON-NLS-1$
 
 	private static final String STYLE_REDIRECT = "Redirect"; // $NON-NLS-1$
 
 	private boolean textMode = true;
 
 	// set default command to Text
 	private String command = TEXT_COMMAND;
 
 	// Keep copies of the two editors needed
 	private static EditorKit customisedEditor = new LocalHTMLEditorKit();
 
 	private static EditorKit defaultHtmlEditor = JEditorPane.createEditorKitForContentType(TEXT_HTML);
 
 	private DefaultMutableTreeNode root;
 
 	private DefaultTreeModel treeModel;
 
 	private JTextPane stats;
 
 	private JEditorPane results;
 
 	private JScrollPane resultsScrollPane;
 
 	private JLabel imageLabel;
 
 	private JTextArea sampleDataField;
 
 	private JRadioButton textButton;
 
 	private JRadioButton htmlButton;
 
 	private JRadioButton xmlButton;
 
 	private JCheckBox downloadAll;
 
 	private JTree jTree;
 
+	private static final ImageIcon imageSuccess = JMeterUtils.getImage(
+	        JMeterUtils.getPropDefault("viewResultsTree.success", "icon_success_sml.gif"));
+
+	private static final ImageIcon imageFailure = JMeterUtils.getImage(
+			JMeterUtils.getPropDefault("viewResultsTree.failure", "icon_warning_sml.gif"));
+	
 	public ViewResultsFullVisualizer() {
 		super();
 		log.debug("Start : ViewResultsFullVisualizer1");
 		init();
 		log.debug("End : ViewResultsFullVisualizer1");
 	}
 
 	public void add(SampleResult res) {
 		updateGui(res);
 	}
 
 	public String getLabelResource() {
 		return "view_results_tree_title"; // $NON-NLS-1$
 	}
 
 	/**
 	 * Update the visualizer with new data.
 	 */
 	public synchronized void updateGui(SampleResult res) {
 		log.debug("Start : updateGui1");
 		if (log.isDebugEnabled()) {
 			log.debug("updateGui1 : sample result - " + res);
 		}
 		DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(res);
 		treeModel.insertNodeInto(currNode, root, root.getChildCount());
 		addSubResults(currNode, res);
 
 		if (root.getChildCount() == 1) {
 			jTree.expandPath(new TreePath(root));
 		}
 		log.debug("End : updateGui1");
 	}
 
 	private void addSubResults(DefaultMutableTreeNode currNode, SampleResult res) {
 		SampleResult[] subResults = res.getSubResults();
 
 		int leafIndex = 0;
 
 		for (int i = 0; i < subResults.length; i++) {
 			SampleResult child = subResults[i];
 
 			if (log.isDebugEnabled()) {
 				log.debug("updateGui1 : child sample result - " + child);
 			}
 			DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(child);
 
 			treeModel.insertNodeInto(leafNode, currNode, leafIndex++);
 			addSubResults(leafNode, child);
 		}
 	}
 
 	/**
 	 * Clears the visualizer.
 	 */
 	public void clear() {
 		log.debug("Start : clear1");
 
 		if (log.isDebugEnabled()) {
 			log.debug("clear1 : total child - " + root.getChildCount());
 		}
 		while (root.getChildCount() > 0) {
 			// the child to be removed will always be 0 'cos as the nodes are
 			// removed the nth node will become (n-1)th
 			treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
 		}
 
 		results.setText("");// Response Data // $NON-NLS-1$
 		sampleDataField.setText("");// Request Data // $NON-NLS-1$
 		log.debug("End : clear1");
 	}
 
 	/**
 	 * Returns the description of this visualizer.
 	 * 
 	 * @return description of this visualizer
 	 */
 	public String toString() {
 		String desc = "Shows the text results of sampling in tree form";
 
 		if (log.isDebugEnabled()) {
 			log.debug("toString1 : Returning description - " + desc);
 		}
 		return desc;
 	}
 
 	/**
 	 * Sets the right pane to correspond to the selected node of the left tree.
 	 */
 	public void valueChanged(TreeSelectionEvent e) {
 		log.debug("Start : valueChanged1");
 		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
 		if (log.isDebugEnabled()) {
 			log.debug("valueChanged : selected node - " + node);
 		}
 
 		StyledDocument statsDoc = stats.getStyledDocument();
 		try {
 			statsDoc.remove(0, statsDoc.getLength());
 			sampleDataField.setText(""); // $NON-NLS-1$
 			results.setText(""); // $NON-NLS-1$
 			if (node != null) {
 				SampleResult res = (SampleResult) node.getUserObject();
 
 				if (log.isDebugEnabled()) {
 					log.debug("valueChanged1 : sample result - " + res);
 				}
 
 				if (res != null) {
 					// load time label
 
 					log.debug("valueChanged1 : load time - " + res.getTime());
                     String sd=res.getSamplerData();
 					if (sd != null) {
 						String rh = res.getRequestHeaders();
 						if (rh != null) {
                             StringBuffer sb = new StringBuffer(sd.length()+rh.length()+20);
                             sb.append(sd);
                             sb.append("\nRequest Headers:\n");
                             sb.append(rh);
 							sd = sb.toString();
                         }
 						sampleDataField.setText(sd);
                     }
 
 					statsDoc.insertString(statsDoc.getLength(), "Thread Name: "+res.getThreadName()+"\n", null);
                     String startTime = new Date(res.getStartTime()).toString();
                     statsDoc.insertString(statsDoc.getLength(), "Sample Start: "+startTime+"\n", null);
 					statsDoc.insertString(statsDoc.getLength(), "Load time: " + res.getTime() + "\n", null);
 
 					String responseCode = res.getResponseCode();
 					log.debug("valueChanged1 : response code - " + responseCode);
 
 					int responseLevel = 0;
 					if (responseCode != null) {
 						try {
 							responseLevel = Integer.parseInt(responseCode) / 100;
 						} catch (NumberFormatException numberFormatException) {
 							// no need to change the foreground color
 						}
 					}
 
 					Style style = null;
 					switch (responseLevel) {
 					case 3:
 						style = statsDoc.getStyle(STYLE_REDIRECT);
 						break;
 					case 4:
 						style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
 						break;
 					case 5:
 						style = statsDoc.getStyle(STYLE_SERVER_ERROR);
 						break;
 					}
 					statsDoc.insertString(statsDoc.getLength(), "HTTP response code: " + responseCode + "\n", style);
 
 					// response message label
 					String responseMsgStr = res.getResponseMessage();
 
 					log.debug("valueChanged1 : response message - " + responseMsgStr);
 					statsDoc
 							.insertString(statsDoc.getLength(), "HTTP response message: " + responseMsgStr + "\n", null);
 
 					statsDoc.insertString(statsDoc.getLength(), "\nHTTP response headers:\n" + res.getResponseHeaders()
 							+ "\n", null);
 
 					// get the text response and image icon
 					// to determine which is NOT null
 					if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null)
 																		// is OK
 					{
 						String response = getResponseAsString(res);
 						if (command.equals(TEXT_COMMAND)) {
 							showTextResponse(response);
 						} else if (command.equals(HTML_COMMAND)) {
 							showRenderedResponse(response, res);
 						} else if (command.equals(XML_COMMAND)) {
 							showRenderXMLResponse(response);
 						}
 					} else {
 						byte[] responseBytes = res.getResponseData();
 						if (responseBytes != null) {
 							showImage(new ImageIcon(responseBytes));
 						}
 					}
 				}
 			}
 		} catch (BadLocationException exc) {
 			log.error("Error setting statistics text", exc);
 			stats.setText("");
 		}
 		log.debug("End : valueChanged1");
 	}
 
 	private void showImage(Icon image) {
 		imageLabel.setIcon(image);
 		resultsScrollPane.setViewportView(imageLabel);
 		textButton.setEnabled(false);
 		htmlButton.setEnabled(false);
 		xmlButton.setEnabled(false);
 	}
 
 	protected void showTextResponse(String response) {
 		results.setContentType("text/plain"); // $NON-NLS-1$
 		results.setText(response == null ? "" : response); // $NON-NLS-1$
 		results.setCaretPosition(0);
 		resultsScrollPane.setViewportView(results);
 
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	transient SAXErrorHandler saxErrorHandler = new SAXErrorHandler();
 
 	private void showRenderXMLResponse(String response) {
 		String parsable="";
 		if (response == null) {
 			results.setText(""); // $NON-NLS-1$
 			parsable = ""; // $NON-NLS-1$
 		} else {
 			results.setText(response);
 			int start = response.indexOf(XML_PFX);
 			if (start > 0) {
 			    parsable = response.substring(start);				
 			} else {
 			    parsable=response;
 			}
 		}
 		results.setContentType("text/xml"); // $NON-NLS-1$
 		results.setCaretPosition(0);
 
 		Component view = results;
 
 		// there is duplicate Document class. Therefore I needed to declare the
 		// specific
 		// class that I want
 		org.w3c.dom.Document document = null;
 
 		try {
 
 			DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
 			parserFactory.setValidating(false);
 			parserFactory.setNamespaceAware(false);
 
 			// create a parser:
 			DocumentBuilder parser = parserFactory.newDocumentBuilder();
 
 			parser.setErrorHandler(saxErrorHandler);
 			document = parser.parse(new InputSource(new StringReader(parsable)));
 
 			JPanel domTreePanel = new DOMTreePanel(document);
 
 			document.normalize();
 
 			view = domTreePanel;
 		} catch (SAXParseException e) {
 			showErrorMessageDialog(saxErrorHandler.getErrorMessage(), saxErrorHandler.getMessageType());
 			log.debug(e.getMessage());
 		} catch (SAXException e) {
 			showErrorMessageDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE);
 			log.debug(e.getMessage());
 		} catch (IOException e) {
 			showErrorMessageDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE);
 			log.debug(e.getMessage());
 		} catch (ParserConfigurationException e) {
 			showErrorMessageDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE);
 			log.debug(e.getMessage());
 		}
 		resultsScrollPane.setViewportView(view);
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	private static String getResponseAsString(SampleResult res) {
 
 		byte[] responseBytes = res.getResponseData();
 		String response = null;
 		if ((SampleResult.TEXT).equals(res.getDataType())) {
 			try {
 				// Showing large strings can be VERY costly, so we will avoid
 				// doing so if the response
 				// data is larger than 200K. TODO: instead, we could delay doing
 				// the result.setText
 				// call until the user chooses the "Response data" tab. Plus we
 				// could warn the user
 				// if this happens and revert the choice if he doesn't confirm
 				// he's ready to wait.
 				if (responseBytes.length > 200 * 1024) {
 					response = ("Response too large to be displayed (" + responseBytes.length + " bytes).");
 					log.warn("Response too large to display.");
 				} else {
 					response = new String(responseBytes, res.getDataEncoding());
 				}
 			} catch (UnsupportedEncodingException err) {
 				log.warn("Could not decode response " + err);
 				response = new String(responseBytes);// Try the default
 														// encoding instead
 			}
 		}
 		return response;
 	}
 
 	/**
 	 * Display the response as text or as rendered HTML. Change the text on the
 	 * button appropriate to the current display.
 	 * 
 	 * @param e
 	 *            the ActionEvent being processed
 	 */
 	public void actionPerformed(ActionEvent e) {
 		command = e.getActionCommand();
 
 		if (command != null
 				&& (command.equals(TEXT_COMMAND) || command.equals(HTML_COMMAND) || command.equals(XML_COMMAND))) {
 
 			textMode = command.equals(TEXT_COMMAND);
 
 			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
 			if (node == null) {
 				results.setText("");
 				return;
 			}
 
 			SampleResult res = (SampleResult) node.getUserObject();
 			String response = getResponseAsString(res);
 
 			if (command.equals(TEXT_COMMAND)) {
 				showTextResponse(response);
 			} else if (command.equals(HTML_COMMAND)) {
 				showRenderedResponse(response, res);
 			} else if (command.equals(XML_COMMAND)) {
 				showRenderXMLResponse(response);
 			}
 		}
 	}
 
 	protected void showRenderedResponse(String response, SampleResult res) {
 		if (response == null) {
 			results.setText("");
 			return;
 		}
 
 		int htmlIndex = response.indexOf("<HTML"); // could be <HTML lang=""> // $NON-NLS-1$
 
 		// Look for a case variation
 		if (htmlIndex < 0) {
 			htmlIndex = response.indexOf("<html"); // ditto // $NON-NLS-1$
 		}
 
 		// If we still can't find it, just try using all of the text
 		if (htmlIndex < 0) {
 			htmlIndex = 0;
 		}
 
 		String html = response.substring(htmlIndex);
 
 		/*
 		 * To disable downloading and rendering of images and frames, enable the
 		 * editor-kit. The Stream property can then be
 		 */
 
 		// Must be done before setContentType
 		results.setEditorKitForContentType(TEXT_HTML, downloadAll.isSelected() ? defaultHtmlEditor : customisedEditor);
 
 		results.setContentType(TEXT_HTML);
 
 		if (downloadAll.isSelected()) {
 			// Allow JMeter to render frames (and relative images)
 			// Must be done after setContentType [Why?]
 			results.getDocument().putProperty(Document.StreamDescriptionProperty, res.getURL());
 		}
 
 		/*
 		 * Get round problems parsing <META http-equiv='content-type'
 		 * content='text/html; charset=utf-8'> See
 		 * http://issues.apache.org/bugzilla/show_bug.cgi?id=23315
 		 * 
 		 * Is this due to a bug in Java?
 		 */
 		results.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE); // $NON-NLS-1$
 
 		results.setText(html);
 		results.setCaretPosition(0);
 		resultsScrollPane.setViewportView(results);
 
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	// TODO this method changed because Render XML button added
 	// Could probably be private anyway, because it's only used locally
 	protected Component createHtmlOrTextPane() {
 		ButtonGroup group = new ButtonGroup();
 
 		textButton = new JRadioButton(TEXT_BUTTON_LABEL);
 		textButton.setActionCommand(TEXT_COMMAND);
 		textButton.addActionListener(this);
 		textButton.setSelected(textMode);
 		group.add(textButton);
 
 		htmlButton = new JRadioButton(HTML_BUTTON_LABEL);
 		htmlButton.setActionCommand(HTML_COMMAND);
 		htmlButton.addActionListener(this);
 		htmlButton.setSelected(!textMode);
 		group.add(htmlButton);
 
 		xmlButton = new JRadioButton(XML_BUTTON_LABEL);
 		xmlButton.setActionCommand(XML_COMMAND);
 		xmlButton.addActionListener(this);
 		xmlButton.setSelected(!textMode);
 		group.add(xmlButton);
 
 		downloadAll = new JCheckBox(DOWNLOAD_LABEL);
 
 		JPanel pane = new JPanel();
 		pane.add(textButton);
 		pane.add(htmlButton);
 		pane.add(xmlButton);
 		pane.add(downloadAll);
 		return pane;
 	}
 
 	/**
 	 * Initialize this visualizer
 	 */
 	protected void init() {
 		setLayout(new BorderLayout(0, 5));
 		setBorder(makeBorder());
 
 		add(makeTitlePanel(), BorderLayout.NORTH);
 
 		Component leftSide = createLeftPanel();
 		JTabbedPane rightSide = new JTabbedPane();
 
 		rightSide.addTab(JMeterUtils.getResString("view_results_tab_sampler"), createResponseMetadataPanel()); // $NON-NLS-1$
 		rightSide.addTab(JMeterUtils.getResString("view_results_tab_request"), createRequestPanel()); // $NON-NLS-1$
 		rightSide.addTab(JMeterUtils.getResString("view_results_tab_response"), createResponseDataPanel()); // $NON-NLS-1$
 
 		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
 		add(mainSplit, BorderLayout.CENTER);
 	}
 
 	private Component createLeftPanel() {
 		SampleResult rootSampleResult = new SampleResult();
 		rootSampleResult.setSampleLabel("Root");
 		rootSampleResult.setSuccessful(true);
 		root = new DefaultMutableTreeNode(rootSampleResult);
 
 		treeModel = new DefaultTreeModel(root);
 		jTree = new JTree(treeModel);
 		jTree.setCellRenderer(new ResultsNodeRenderer());
 		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
 		jTree.addTreeSelectionListener(this);
 		jTree.setRootVisible(false);
 		jTree.setShowsRootHandles(true);
 
 		JScrollPane treePane = new JScrollPane(jTree);
 		treePane.setPreferredSize(new Dimension(200, 300));
 		return treePane;
 	}
 
 	private Component createResponseMetadataPanel() {
 		stats = new JTextPane();
 		stats.setEditable(false);
 		stats.setBackground(getBackground());
 
 		// Add styles to use for different types of status messages
 		StyledDocument doc = (StyledDocument) stats.getDocument();
 
 		Style style = doc.addStyle(STYLE_REDIRECT, null);
 		StyleConstants.setForeground(style, REDIRECT_COLOR);
 
 		style = doc.addStyle(STYLE_CLIENT_ERROR, null);
 		StyleConstants.setForeground(style, CLIENT_ERROR_COLOR);
 
 		style = doc.addStyle(STYLE_SERVER_ERROR, null);
 		StyleConstants.setForeground(style, SERVER_ERROR_COLOR);
 
 		JScrollPane pane = makeScrollPane(stats);
 		pane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
 		return pane;
 	}
 
 	private Component createRequestPanel() {
 		sampleDataField = new JTextArea();
 		sampleDataField.setEditable(false);
 		sampleDataField.setLineWrap(true);
 		sampleDataField.setWrapStyleWord(true);
 
 		JPanel pane = new JPanel(new BorderLayout(0, 5));
 		pane.add(makeScrollPane(sampleDataField));
 		return pane;
 	}
 
 	private Component createResponseDataPanel() {
 		results = new JEditorPane();
 		results.setEditable(false);
 
 		resultsScrollPane = makeScrollPane(results);
 		imageLabel = new JLabel();
 
 		JPanel resultsPane = new JPanel(new BorderLayout());
 		resultsPane.add(resultsScrollPane, BorderLayout.CENTER);
 		resultsPane.add(createHtmlOrTextPane(), BorderLayout.SOUTH);
 
 		return resultsPane;
 	}
 
 	private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
 		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
 				boolean leaf, int row, boolean focus) {
 			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
 			if (!((SampleResult) ((DefaultMutableTreeNode) value).getUserObject()).isSuccessful()) {
 				this.setForeground(Color.red);
+				this.setIcon(imageFailure);
+			} else {
+				this.setIcon(imageSuccess);
 			}
 			return this;
 		}
 	}
 
 	private static class LocalHTMLEditorKit extends HTMLEditorKit {
 
 		private static final ViewFactory defaultFactory = new LocalHTMLFactory();
 
 		public ViewFactory getViewFactory() {
 			return defaultFactory;
 		}
 
 		private static class LocalHTMLFactory extends javax.swing.text.html.HTMLEditorKit.HTMLFactory {
 			/*
 			 * Provide dummy implementations to suppress download and display of
 			 * related resources: - FRAMEs - IMAGEs TODO create better dummy
 			 * displays TODO suppress LINK somehow
 			 */
 			public View create(Element elem) {
 				Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
 				if (o instanceof HTML.Tag) {
 					HTML.Tag kind = (HTML.Tag) o;
 					if (kind == HTML.Tag.FRAME) {
 						return new ComponentView(elem);
 					} else if (kind == HTML.Tag.IMG) {
 						return new ComponentView(elem);
 					}
 				}
 				return super.create(elem);
 			}
 		}
 	}
 
 	/**
 	 * 
 	 * A Dom tree panel for to display response as tree view author <a
 	 * href="mailto:d.maung@mdl.com">Dave Maung</a> TODO implement to find any
 	 * nodes in the tree using TreePath.
 	 * 
 	 */
 	private static class DOMTreePanel extends JPanel {
 
 		private JTree domJTree;
 
 		public DOMTreePanel(org.w3c.dom.Document document) {
 			super(new GridLayout(1, 0));
 			try {
 				Node firstElement = getFirstElement(document);
 				DefaultMutableTreeNode top = new XMLDefaultMutableTreeNode(firstElement);
 				domJTree = new JTree(top);
 
 				domJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
 				domJTree.setShowsRootHandles(true);
 				JScrollPane domJScrollPane = new JScrollPane(domJTree);
 				domJTree.setAutoscrolls(true);
 				this.add(domJScrollPane);
 				ToolTipManager.sharedInstance().registerComponent(domJTree);
 				domJTree.setCellRenderer(new DomTreeRenderer());
 				this.setPreferredSize(new Dimension(800, 600));
 			} catch (SAXException e) {
 				log.warn("", e);
 			}
 
 		}
 
 		/**
 		 * Skip all DTD nodes, all prolog nodes. They dont support in tree view
 		 * We let user to insert them however in DOMTreeView, we dont display it
 		 * 
 		 * @param root
 		 * @return
 		 */
 		private Node getFirstElement(Node parent) {
 			NodeList childNodes = parent.getChildNodes();
 			Node toReturn = null;
 			for (int i = 0; i < childNodes.getLength(); i++) {
 				Node childNode = childNodes.item(i);
 				toReturn = childNode;
 				if (childNode.getNodeType() == Node.ELEMENT_NODE)
 					break;
 
 			}
 			return toReturn;
 		}
 
 		/**
 		 * This class is to view as tooltext. This is very useful, when the
 		 * contents has long string and does not fit in the view. it will also
 		 * automatically wrap line for each 100 characters since tool tip
 		 * support html. author <a href="mailto:d.maung@mdl.com">Dave Maung</a>
 		 */
 		private static class DomTreeRenderer extends DefaultTreeCellRenderer {
 			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
 					boolean leaf, int row, boolean phasFocus) {
 				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, phasFocus);
 
 				DefaultMutableTreeNode valueTreeNode = (DefaultMutableTreeNode) value;
 				setToolTipText(getHTML(valueTreeNode.toString(), "<br>", 100)); // $NON-NLS-1$
 				return this;
 			}
 
 			/**
 			 * get the html
 			 * 
 			 * @param str
 			 * @param separator
 			 * @param maxChar
 			 * @return
 			 */
 			private String getHTML(String str, String separator, int maxChar) {
 				StringBuffer strBuf = new StringBuffer("<html><body bgcolor=\"yellow\"><b>"); // $NON-NLS-1$
 				char[] chars = str.toCharArray();
 				for (int i = 0; i < chars.length; i++) {
 
 					if (i % maxChar == 0 && i != 0)
 						strBuf.append(separator);
 					strBuf.append(encode(chars[i]));
 
 				}
 				strBuf.append("</b></body></html>"); // $NON-NLS-1$
 				return strBuf.toString();
 
 			}
 
 			private String encode(char c) {
 				String toReturn = String.valueOf(c);
 				switch (c) {
 				case '<': // $NON-NLS-1$
 					toReturn = "&lt;"; // $NON-NLS-1$
 					break;
 				case '>': // $NON-NLS-1$
 					toReturn = "&gt;"; // $NON-NLS-1$
 					break;
 				case '\'': // $NON-NLS-1$
 					toReturn = "&apos;"; // $NON-NLS-1$
 					break;
 				case '\"': // $NON-NLS-1$
 					toReturn = "&quot;"; // $NON-NLS-1$
 					break;
 
 				}
 				return toReturn;
 			}
 		}
 	}
 
 	private static void showErrorMessageDialog(String message, int messageType) {
 		JOptionPane.showMessageDialog(null, message, "Error", messageType);
 	}
 
 	// Helper method to construct SAX error details
 	private static String errorDetails(SAXParseException spe) {
 		StringBuffer str = new StringBuffer(80);
 		int i;
 		i = spe.getLineNumber();
 		if (i != -1) {
 			str.append("line=");
 			str.append(i);
 			str.append(" col=");
 			str.append(spe.getColumnNumber());
 			str.append(" ");
 		}
 		str.append(spe.getLocalizedMessage());
 		return str.toString();
 	}
 
 	private static class SAXErrorHandler implements ErrorHandler {
 		private String msg;
 
 		private int messageType;
 
 		public SAXErrorHandler() {
 			msg = ""; // $NON-NLS-1$
 
 		}
 
 		public void error(SAXParseException exception) throws SAXParseException {
 			msg = "error: " + errorDetails(exception);
 
 			log.debug(msg);
 			messageType = JOptionPane.ERROR_MESSAGE;
 			throw exception;
 		}
 
 		/*
 		 * Can be caused by: - premature end of file - non-whitespace content
 		 * after trailer
 		 */
 		public void fatalError(SAXParseException exception) throws SAXParseException {
 
 			msg = "fatal: " + errorDetails(exception);
 			messageType = JOptionPane.ERROR_MESSAGE;
 			log.debug(msg);
 
 			throw exception;
 		}
 
 		/*
 		 * Not clear what can cause this ? conflicting versions perhaps
 		 */
 		public void warning(SAXParseException exception) throws SAXParseException {
 			msg = "warning: " + errorDetails(exception);
 			log.debug(msg);
 			messageType = JOptionPane.WARNING_MESSAGE;
 		}
 
 		/**
 		 * get the JOptionPaneMessage Type
 		 * 
 		 * @return
 		 */
 		public int getMessageType() {
 			return messageType;
 		}
 
 		/**
 		 * get error message
 		 * 
 		 * @return
 		 */
 		public String getErrorMessage() {
 			return msg;
 		}
 	}
 
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index a98ef2e58..f850a24b2 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,749 +1,750 @@
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
 	<author email="jmeter-dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>History of Changes</title>   
 </properties> 
 <body> 
 <section name="History of Changes"> 
 <p><b>Changes are chronologically ordered from top (most recent) to bottom 
 (least recent)</b></p>  
 
 <!--  ===================  -->
 
 <h3>Version 2.2.1</h3>
 <h4>Known problems:</h4>
 <p>Thread active counts are always zero in CSV and XML files when running remote tests.
 </p>
 <p>The property file_format.testlog=2.1 is treated the same as 2.2.
 However JMeter does honour the 3 testplan versions.</p>
 <p>
 Bug 22510 - JMeter always uses the first entry in the keystore.
 </p>
 <h4>Incompatible changes:</h4>
 <p>
 Bug 41104: JMeterThread behaviour was changed so that PostProcessors are run in forward order
 (as they appear in the test plan) rather than reverse order as previously.
 The original behaviour can be restored by setting the following JMeter property:
 <br/>
 jmeterthread.reversePostProcessors=true
 </p>
 <p>
 The HTTP Authorisation Manager now has extra columns for domain and realm, 
 so the temporary work-round of using '\' and '@' in the username to delimit the domain and realm
 has been removed.
 </p>
 <h4>New functionality:</h4>
 <ul>
 <li>Added httpclient.parameters.file to allow HttpClient parameters to be defined</li>
 <li>Added beanshell.init.file property to run a BeanShell script at startup</li>
 <li>Added timeout for WebService (SOAP) Sampler</li>
 <li>Bug 40804 - Change Counter default to max = Long.MAX_VALUE</li>
 <li>BeanShell Post-Processor no longer ignores samples with zero-length result data</li>
 <li>Use property jmeter.home (if present) to override user.dir when starting JMeter</li>
 <li>Bug 41457 - Add TCP Sampler option to not re-use connections</li>
 <li>Bug 41522 - Use JUnit sampler name in sample results</li>
 <li>HttpClient now behaves the same as the JDK http sampler for invalid certificates etc</li>
 <li>Add Domain and Realm support to HTTP Authorisation Manager</li>
 <li>Bug 33964 - send file as entire post body if name/type are omitted</li>
 <li>HTTP Mirror Server Workbench element</li>
 <li>Bug 41253 - extend XPathExtractor to work with non-NodeList XPath expressions</li>
 <li>Change to htmlparser 2.0</li>
 <li>Updated to xstream 1.2.1/xpp3_min-1.1.3.4.O</li>
+<li>Bug 39717 - use icons in the results tree instead of colors</li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 39773 - NTLM now needs local host name - fix other call</li>
 <li>Bug 40438 - setting "httpclient.localaddress" has no effect</li>
 <li>Bug 40419 - Chinese messages translation fix</li>
 <li>Bug 39861 - fix typo</li>
 <li>Bug 40562 - redirects no longer invoke RE post processors</li>
 <li>Bug 40451 - set label if not set by sampler</li>
 <li>Fix NPE in CounterConfig.java in Remote mode</li>
 <li>Bug 40791 - Calculator used by Summary Report</li>
 <li>Bug 40772 - correctly parse missing fields</li>
 <li>Bug 40773 - XML JTL not parsed correctly</li>
 <li>Bug 41029 - JMeter -t fails to close input JMX file</li>
 <li>Bug 40954 - Statistical mode in distributed testing shows wrong results</li>
 <li>Fix ClassCast Exception when using sampler that returns null, e..g TestAction</li>
 <li>Bug 41277 - add Latency and Encoding to CSV output</li>
 <li>Bug 41414 - Mac OS X may add extra item to -jar classpath</li>
 <li>Fix NPE when saving thread counts in remote testing</li>
 <li>Bug 34261 - NPE in HtmlParser (allow for missing attributes)</li>
 <li>Bug 40100 - check FileServer type before calling close</li>
 <li>Replace com.sun.net classes with javax.net</li>
 <li>Bug 39887 - jmeter.util.SSLManager: Couldn't load keystore error message</li>
 <li>Bug 41543 - exception when webserver returns "500 Internal Server Error" and content-length is 0</li>
 <li>Bug 41416 - don't use chunked input for text-box input in SOAP-RPC sampler</li>
 <li>Bug 39827 - SOAP Sampler content length for files</li>
 <li>Bug 40381 - LDAP: more descriptive strings</li>
 <li>Bug 40369 (partial) Equals Response Assertion</li>
 <li>Fix Class cast exception in Clear.java</li>
 <li>Bug 40383 - don't set content-type if already set</li>
 <li>Mailer Visualiser test button now works if test plan has not yet been saved</li>
 <li>Bug 36959 - Shortcuts "ctrl c" and "ctrl v" don't work on the tree elements</li>
 </ul>
 
 <h3>Version 2.2</h3>
 
 <h4>Incompatible changes:</h4>
 <p>
 The time stamp is now set to the sampler start time (it was the end).
 To revert to the previous behaviour, change the property <b>sampleresult.timestamp.start</b> to false (or comment it)
 </p>
 <p>The JMX output format has been simplified and files are not backwards compatible</p>
 <p>
 The JMeter.BAT file no longer changes directory to JMeter home, but runs from the current working directory.
 The jmeter-n.bat and jmeter-t.bat files change to the directory containing the input file.
 </p>
 <p>
 Listeners are now started slightly later in order to allow variable names to be used.
 This may cause some problems; if so define the following in jmeter.properties:
 <br/>
 jmeterengine.startlistenerslater=false
 </p>
 
 <h4>Known problems:</h4>
 <ul>
 <li>Post-processors run in reverse order (see bug 41140)</li>
 <li>Module Controller does not work in non-GUI mode</li>
 <li>Aggregate Report and some other listeners use increasing amounts of memory as a test progresses</li>
 <li>Does not always handle non-default encoding properly</li>
 <li>Spaces in the installation path cause problems for client-server mode</li>
 <li>Change of Language does not propagate to all test elements</li>
 <li>SamplingStatCalculator keeps a List of all samples for calculation purposes; 
 this can cause memory exhaustion in long-running tests</li>
 <li>Does not properly handle server certificates if they are expired or not installed locally</li>
 </ul>
 
 <h4>New functionality:</h4>
 <ul>
 <li>Report function</li>
 <li>XPath Extractor Post-Processor. Handles single and multiple matches.</li>
 <li>Simpler JMX file format (2.2)</li>
 <li>BeanshellSampler code can update ResponseData directly</li>
 <li>Bug 37490 - Allow UDV as delay in Duration Assertion</li>
 <li>Slow connection emulation for HttpClient</li>
 <li>Enhanced JUnitSampler so that by default assert errors and exceptions are not appended to the error message. 
 Users must explicitly check append in the sampler</li>
 <li>Enhanced the documentation for webservice sampler to explain how it works with CSVDataSet</li>
 <li>Enhanced the documentation for javascript function to explain escaping comma</li>
 <li>Allow CSV Data Set file names to be absolute</li>
 <li>Report Tree compiler errors better</li>
 <li>Don't reset Regex Extractor variable if default is empty</li>
 <li>includecontroller.prefix property added</li>
 <li>Regular Expression Extractor sets group count</li>
 <li>Can now save entire screen as an image, not just the right-hand pane</li>
 <li>Bug 38901 - Add optional SOAPAction header to SOAP Sampler</li>
 <li>New BeanShell test elements: Timer, PreProcessor, PostProcessor, Listener</li>
 <li>__split() function now clears next variable, so it can be used with ForEach Controller</li>
 <li>Bug 38682 - add CallableStatement functionality to JDBC Sampler</li>
 <li>Make it easier to change the RMI/Server port</li>
 <li>Add property jmeter.save.saveservice.xml_pi to provide optional xml processing instruction in JTL files</li>
 <li>Add bytes and URL to items that can be saved in sample log files (XML and CSV)</li>
 <li>The Post-Processor "Save Responses to a File" now saves the generated file name with the
 sample, and the file name can be included in the sample log file.
 </li>
 <li>Change jmeter.bat DOS script so it works from any directory</li>
 <li>New -N option to define nonProxyHosts from command-line</li>
 <li>New -S option to define system properties from input file</li>
 <li>Bug 26136 - allow configuration of local address</li>
 <li>Expand tree by default when loading a test plan - can be disabled by setting property onload.expandtree=false</li>
 <li>Bug 11843 - URL Rewriter can now cache the session id</li>
 <li>Counter Pre-Processor now supports formatted numbers</li>
 <li>Add support for HEAD PUT OPTIONS TRACE and DELETE methods</li>
 <li>Allow default HTTP implementation to be changed</li>
 <li>Optionally save active thread counts (group and all) to result files</li>
 <li>Variables/functions can now be used in Listener file names</li>
 <li>New __time() function; define START.MS/START.YMD/START.HMS properties and variables</li>
 <li>Add Thread Name to Tree and Table Views</li>
 <li>Add debug functions: What class, debug on, debug off</li>
 <li>Non-caching Calculator - used by Table Visualiser to reduce memory footprint</li>
 <li>Summary Report - similar to Aggregate Report, but uses less memory</li>
 <li>Bug 39580 - recycle option for CSV Dataset</li>
 <li>Bug 37652 - support for Ajp Tomcat protocol</li>
 <li>Bug 39626 - Loading SOAP/XML-RPC requests from file</li>
 <li>Bug 39652 - Allow truncation of labels on AxisGraph</li>
 <li>Allow use of htmlparser 1.6</li>
 <li>Bug 39656 - always use SOAP action if it is provided</li>
 <li>Automatically include properties from user.properties file</li>
 <li>Add __jexl() function - evaluates Commons JEXL expressions</li>
 <li>Optionally load JMeter properties from user.properties and system properties from system.properties.</li>
 <li>Bug 39707 - allow Regex match against URL</li>
 <li>Add start time to Table Visualiser</li>
 <li>HTTP Samplers can now extract embedded resources for any required media types</li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Fix NPE when no module selected in Module Controller</li>
 <li>Fix NPE in XStream when no ResponseData present</li>
 <li>Remove ?xml prefix when running with Java 1.5 and no x-jars</li>
 <li>Bug 37117 - setProperty() function should return ""; added optional return of original setting</li>
 <li>Fix CSV output time format</li>
 <li>Bug 37140 - handle encoding better in RegexFunction</li>
 <li>Load all cookies, not just the first; fix class cast exception</li>
 <li>Fix default Cookie path name (remove page name)</li>
 <li>Fixed resultcode attribute name</li>
 <li>Bug 36898 - apply encoding to RegexExtractor</li>
 <li>Add properties for saving subresults, assertions, latency, samplerData, responseHeaders, requestHeaders &amp; encoding</li>
 <li>Bug 37705 - Synch Timer now works OK after run is stopped</li>
 <li>Bug 37716 - Proxy request now handles file Post correctly</li>
 <li>HttpClient Sampler now saves latency</li>
 <li>Fix NPE when using JavaScript function on Test Plan</li>
 <li>Fix Base Href parsing in htmlparser</li>
 <li>Bug 38256 - handle cookie with no path</li>
 <li>Bug 38391 - use long when accumulating timer delays</li>
 <li>Bug 38554 - Random function now uses long numbers</li>
 <li>Bug 35224 - allow duplicate attributes for LDAP sampler</li>
 <li>Bug 38693 - Webservice sampler can now use https protocol</li>
 <li>Bug 38646 - Regex Extractor now clears old variables on match failure</li>
 <li>Bug 38640 - fix WebService Sampler pooling</li>
 <li>Bug 38474 - HTML Link Parser doesn't follow frame links</li>
 <li>Bug 36430 - Counter now uses long rather than int to increase the range</li>
 <li>Bug 38302 - fix XPath function</li>
 <li>Bug 38748 - JDBC DataSourceElement fails with remote testing</li>
 <li>Bug 38902 - sometimes -1 seems to be returned unnecessarily for response code</li>
 <li>Bug 38840 - make XML Assertion thread-safe</li>
 <li>Bug 38681 - Include controller now works in non-GUI mode</li>
 <li>Add write(OS,IS) implementation to TCPClientImpl</li>
 <li>Sample Result converter saves response code as "rc". Previously it saved as "rs" but read with "rc"; it will now also read with "rc".
 The XSL stylesheets also now accept either "rc" or "rs"</li>
 <li>Fix counter function so each counter instance is independent (previously the per-user counters were shared between instances of the function)</li>
 <li>Fix TestBean Examples so that they work</li>
 <li>Fix JTidy parser so it does not skip body tags with background images</li>
 <li>Fix HtmlParser parser so it catches all background images</li>
 <li>Bug 39252 set SoapSampler sample result from XML data</li>
 <li>Bug 38694 - WebServiceSampler not setting data encoding correctly</li>
 <li>Result Collector now closes input files read by listeners</li>
 <li>Bug 25505 - First HTTP sampling fails with "HTTPS hostname wrong: should be 'localhost'"</li>
 <li>Bug 25236 - remove double scrollbar from Assertion Result Listener</li>
 <li>Bug 38234 - Graph Listener divide by zero problem</li>
 <li>Bug 38824 - clarify behaviour of Ignore Status</li>
 <li>Bug 38250 - jmeter.properties "language" now supports country suffix, for zh_CN and zh_TW etc</li>
 <li>jmeter.properties file is now closed after it has been read</li>
 <li>Bug 39533 - StatCalculator added wrong items</li>
 <li>Bug 39599 - ConcurrentModificationException</li>
 <li>HTTPSampler2 now handles Auto and Follow redirects correctly</li>
 <li>Bug 29481 - fix reloading sample results so subresults not counted twice</li>
 <li>Bug 30267 - handle AutoRedirects properly</li>
 <li>Bug 39677 - allow for space in JMETER_BIN variable</li>
 <li>Use Commons HttpClient cookie parsing and management. Fix various problems with cookie handling.</li>
 <li>Bug 39773 - NTCredentials needs host name</li>
 </ul>	
 	
 <h4>Other changes</h4>
 <ul>
 <li>Updated to HTTPClient 3.0 (from 2.0)</li>
 <li>Updated to Commons Collections 3.1</li>
 <li>Improved formatting of Request Data in Tree View</li>
 <li>Expanded user documentation</li>
 <li>Added MANIFEST, NOTICE and LICENSE to all jars</li>
 <li>Extract htmlparser interface into separate jarfile to make it possible to replace the parser</li>
 <li>Removed SQL Config GUI as no longer needed (or working!)</li>
 <li>HTTPSampler no longer logs a warning for Page not found (404)</li>
 <li>StringFromFile now callable as __StringFromFile (as well as _StringFromFile)</li>
 <li>Updated to Commons Logging 1.1</li>
 </ul>
 
 <!--  ===================  -->
 
 
 <hr/>
 <h3>Version 2.1.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Include Controller allows a test plan to reference an external jmx file</li>
 <li>New JUnitSampler added for using JUnit Test classes</li>
 <li>New Aggregate Graph listener is capable of graphing aggregate statistics</li>
 <li>Can provide additional classpath entries using the property user.classpath and on the Test Plan element</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>AccessLog Sampler and JDBC test elements populated correctly from 2.0 test plans</li>
 <li>BSF Sampler now populates filename and parameters from saved test plan</li>
 <li>Bug 36500 - handle missing data more gracefully in WebServiceSampler</li>
 <li>Bug 35546 - add merge to right-click menu</li>
 <li>Bug 36642 - Summariser stopped working in 2.1</li>
 <li>Bug 36618 - CSV header line did not match saved data</li>
 <li>JMeter should now run under JVM 1.3 (but does not build with 1.3)</li>
 </ul>	
 	
 
 <!--  ===================  -->
 
 <h3>Version 2.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Test Script file format - smaller, more compact, more readable</li>
 <li>New Sample Result file format - smaller, more compact</li>
 <li>XSchema Assertion</li>
 <li>XML Tree display</li>
 <li>CSV DataSet Config item</li>
 <li>New JDBC Connection Pool Config Element</li>
 <li>Synchronisation Timer</li>
 <li>setProperty function</li>
 <li>Save response data on error</li>
 <li>Ant JMeter XSLT now optionally shows failed responses and has internal links</li>
 <li>Allow JavaScript variable name to be omitted</li>
 <li>Changed following Samplers to set sample label from sampler name</li>
 <li>All Test elements can be saved as a graphics image to a file</li>
 <li>Bug 35026 - add RE pattern matching to Proxy</li>
 <li>Bug 34739 - Enhance constant Throughput timer</li>
 <li>Bug 25052 - use response encoding to create comparison string in Response Assertion</li>
 <li>New optional icons</li>
 <li>Allow icons to be defined via property files</li>
 <li>New stylesheets for 2.1 format XML test output</li>
 <li>Save samplers, config element and listeners as PNG</li>
 <li>Enhanced support for WSDL processing</li>
 <li>New JMS sampler for topic and queue messages</li>
 <li>How-to for JMS samplers</li>
 <li>Bug 35525 - Added Spanish localisation</li>
 <li>Bug 30379 - allow server.rmi.port to be overridden</li>
 <li>enhanced the monitor listener to save the calculated stats</li>
 <li>Functions and variables now work at top level of test plan</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 34586 - XPath always remained as /</li>
 <li>BeanShellInterpreter did not handle null objects properly</li>
 <li>Fix Chinese resource bundle names</li>
 <li>Save field names if required to CSV files</li>
 <li>Ensure XML file is closed</li>
 <li>Correct icons now displayed for TestBean components</li>
 <li>Allow for missing optional jar(s) in creating menus</li>
 <li>Changed Samplers to set sample label from sampler name as was the case for HTTP</li>
 <li>Fix various samplers to avoid NPEs when incomplete data is provided</li>
 <li>Fix Cookie Manager to use seconds; add debug</li>
 <li>Bug 35067 - set up filename when using -t option</li>
 <li>Don't substitute TestElement.* properties by UDVs in Proxy</li>
 <li>Bug 35065 - don't save old extensions in File Saver</li>
 <li>Bug 25413 - don't enable Restart button unnecessarily</li>
 <li>Bug 35059 - Runtime Controller stopped working</li>
 <li>Clear up any left-over connections created by LDAP Extended Sampler</li>
 <li>Bug 23248 - module controller didn't remember stuff between save and reload</li>
 <li>Fix Chinese locales</li>
 <li>Bug 29920 - change default locale if necessary to ensure default properties are picked up when English is selected.</li>
 <li>Bug fixes for Tomcat monitor captions</li> 
 <li>Fixed webservice sampler so it works with user defined variables</li>
 <li>Fixed screen borders for LDAP config GUI elements</li>
 <li>Bug 31184 - make sure encoding is specified in JDBC sampler</li>
 <li>TCP sampler - only share sockets with same host:port details; correct the manual</li>
 <li>Extract src attribute for embed tags in JTidy and Html Parsers</li>
 </ul>	
 
 <!--  ===================  -->
 
 <h3>Version 2.0.3</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>XPath Assertion and XPath Function</li>
 <li>Switch Controller</li>
 <li>ForEach Controller can now loop through sets of groups</li>
 <li>Allow CSVRead delimiter to be changed (see jmeter.properties)</li>
 <li>Bug 33920 - allow additional property files</li>
 <li>Bug 33845 - allow direct override of Home dir</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Regex Extractor nested constant not put in correct place (32395)</li>
 <li>Start time reset to now if necessary so that delay works OK.</li>
 <li>Missing start/end times in scheduler are assumed to be now, not 1970</li>
 <li>Bug 28661 - 304 responses not appearing in listeners</li>
 <li>DOS scripts now handle different disks better</li>
 <li>Bug 32345 - HTTP Rewriter does not work with HTTP Request default</li>
 <li>Catch Runtime Exceptions so an error in one Listener does not affect others</li>
 <li>Bug 33467 - __threadNum() extracted number wrongly </li>
 <li>Bug 29186,33299 - fix CLI parsing of "-" in second argument</li>
 <li>Fix CLI parse bug: -D arg1=arg2. Log more startup parameters.</li>
 <li>Fix JTidy and HTMLParser parsers to handle form src= and link rel=stylesheet</li>
 <li>JMeterThread now logs Errors to jmeter.log which were appearing on console</li>
 <li>Ensure WhileController condition is dynamically checked</li>
 <li>Bug 32790 ensure If Controller condition is re-evaluated each time</li>
 <li>Bug 30266 - document how to display proxy recording responses</li>
 <li>Bug 33921 - merge should not change file name</li>
 <li>Close file now gives chance to save changes</li>
 <li>Bug 33559 - fixes to Runtime Controller</li>
 </ul>
 <h4>Other changes:</h4>
 <ul>
 <li>To help with variable evaluation, JMeterThread sets "sampling started" a bit earlier (see jmeter.properties)</li>
 <li>Bug 33796 - delete cookies with null/empty values</li>
 <li>Better checking of parameter count in JavaScript function</li>
 <li>Thread Group now defaults to 1 loop instead of forever</li>
 <li>All Beanshell access is now via a single class; only need BSH jar at run-time</li>
 <li>Bug 32464 - document Direct Draw settings in jmeter.bat</li>
 <li>Bug 33919 - increase Counter field sizes</li>
 <li>Bug 32252 - ForEach was not initialising counters</li>
 </ul>
 
 <!--  ===================  -->
 
 <h3>Version 2.0.2</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>While Controller</li>
 <li>BeanShell intilisation scripts</li>
 <li>Result Saver can optionally save failed results only</li>
 <li>Display as HTML has option not to download frames and images etc</li>
 <li>Multiple Tree elements can now be enabled/disabled/copied/pasted at once</li>
 <li>__split() function added</li>
 <li>(28699) allow Assertion to regard unsuccessful responses - e.g. 404 - as successful</li>
 <li>(29075) Regex Extractor can now extract data out of http response header as well as the body</li>
 <li>__log() functions can now write to stdout and stderr</li>
 <li>URL Modifier can now optionally ignore query parameters</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>If controller now works after the first false condition (31390)</li>
 <li>Regex GUI was losing track of Header/Body checkbox (29853)</li>
 <li>Display as HTML now handles frames and relative images</li>
 <li>Right-click open replaced by merge</li>
 <li>Fix some drag and drop problems</li>
 <li>Fixed foreach demo example so it works</li>
 <li>(30741) SSL password prompt now works again </li>
 <li>StringFromFile now closes files at end of test; start and end now optional as intended</li>
 <li>(31342) Fixed text of SOAP Sampler headers</li>
 <li>Proxy must now be stopped before it can be removed (25145)</li>
 <li>Link Parser now supports BASE href (25490)</li>
 <li>(30917) Classfinder ignores duplicate names</li>
 <li>(22820) Allow Counter value to be cleared</li>
 <li>(28230) Fix NPE in HTTP Sampler retrieving embedded resources</li>
 <li>Improve handling of StopTest; catch and log some more errors</li>
 <li>ForEach Controller no longer runs any samples if first variable is not defined</li>
 <li>(28663) NPE in remote JDBC execution</li>
 <li>(30110) Deadlock in stopTest processing</li>
 <li>(31696) Duration not working correctly when using Scheduler</li>
 <li>JMeterContext now uses ThreadLocal - should fix some potential NPE errors</li>
 </ul>
 <h3>Version 2.0.1</h3>
 <p>Bug fix release. TBA.</p>
 <h3>Version 2.0</h3>
 <ul>
 	<li>HTML parsing improved; now has choice of 3 parsers, and most embedded elements can now be detected and downloaded.</li>
 <li>Redirects can now be delegated to URLConnection by defining the JMeter property HTTPSamper.delegateRedirects=true (default is false) </li>
 <li>Stop Thread and Stop Test methods added for Samplers and Assertions etc. Samplers can call setStopThread(true) or setStopTest(true) if they detect an error that needs to stop the thread of the test after the sample has been processed </li>
 <li>Thread Group Gui now has an extra pane to specify what happens after a Sampler error: Continue (as now), Stop Thread or Stop Test. 
     This needs to be extended to a lower level at some stage. </li>
 <li>Added Shutdown to Run Menu. This is the same as Stop except that it lets the Threads finish normally (i.e. after the next sample has been completed) </li>
 <li>Remote samples can be cached until the end of a test by defining the property hold_samples=true when running the server.
 More work is needed to be able to control this from the GUI </li>
 <li>Proxy server has option to skip recording browser headers </li>
 <li>Proxy restart works better (stop waits for daemon to finish) </li>
 <li>Scheduler ignores start if it has already passed </li>
 <li>Scheduler now has delay function </li>
 <li>added Summariser test element (mainly for non-GUI) testing. This prints summary statistics to System.out and/or the log file every so oftem (3 minutes by default). Multiple summarisers can be used; samples are accumulated by summariser name. </li>
 <li>Extra Proxy Server options: 
 Create all samplers with keep-alive disabled 
 Add Separator markers between sets of samples 
 Add Response Assertion to first sampler in each set </li>
 <li>Test Plan has a comment field</li>
 	
 	<li>Help Page can now be pushed to background</li>
 	<li>Separate Function help page</li>
 	<li>New / amended functions</li>
 	<ul>
 	  <li>__property() and __P() functions</li>
 	  <li>__log() and __logn() - for writing to the log file</li>
       <li>_StringFromFile can now process a sequence of files, e.g. dir/file01.txt, dir/file02.txt etc </li>
       <li>_StringFromFile() funtion can now use a variable or function for the file name </li>
 	</ul>
 	<li>New / amended Assertions</li>
 	<ul>
         <li>Response Assertion now works for URLs, and it handles null data better </li>
         <li>Response Assertion can now match on Response Code and Response message as well </li>
 		<li>HTML Assertion using JTidy to check for well-formed HTML</li>
 	</ul>
 	<li>If Controller (not fully functional yet)</li>
 	<li>Transaction Controller (aggregates the times of its children)</li>
 	<li>New Samplers</li>
 		<ul>
 			<li>Basic BSF Sampler (optional)</li>
 			<li>BeanShell Sampler (optional, needs to be downloaded from www.beanshell.org</li>
 			<li>Basic TCP Sampler</li>
 		</ul>
      <li>Optionally start BeanShell server (allows remote access to JMeter variables and methods) </li>
 </ul>
 <h3>Version 1.9.1</h3>
 <p>TBA</p>
 <h3>Version 1.9</h3>
 <ul>
 <li>Sample result log files can now be in CSV or XML format</li>
 <li>New Event model for notification of iteration events during test plan run</li>
 <li>New Javascript function for executing arbitrary javascript statements</li>
 <li>Many GUI improvements</li>
 <li>New Pre-processors and Post-processors replace Modifiers and Response-Based Modifiers. </li>
 <li>Compatible with jdk1.3</li>
 <li>JMeter functions are now fully recursive and universal (can use functions as parameters to functions)</li>
 <li>Integrated help window now supports hypertext links</li>
 <li>New Random Function</li>
 <li>New XML Assertion</li>
 <li>New LDAP Sampler (alpha code)</li>
 <li>New Ant Task to run JMeter (in extras folder)</li>
 <li>New Java Sampler test implementation (to assist developers)</li>
 <li>More efficient use of memory, faster loading of .jmx files</li>
 <li>New SOAP Sampler (alpha code)</li>
 <li>New Median calculation in Graph Results visualizer</li>
 <li>Default config element added for developer benefit</li>
 <li>Various performance enhancements during test run</li>
 <li>New Simple File recorder for minimal GUI overhead during test run</li>
 <li>New Function: StringFromFile - grabs values from a file</li>
 <li>New Function: CSVRead - grabs multiple values from a file</li>
 <li>Functions now longer need to be encoded - special values should be escaped 
 with "\" if they are literal values</li>
 <li>New cut/copy/paste functionality</li>
 <li>SSL testing should work with less user-fudging, and in non-gui mode</li>
 <li>Mailer Model works in non-gui mode</li>
 <li>New Througput Controller</li>
 <li>New Module Controller</li>
 <li>Tests can now be scheduled to run from a certain time till a certain time</li>
 <li>Remote JMeter servers can be started from a non-gui client.  Also, in gui mode, all remote servers can be started with a single click</li>
 <li>ThreadGroups can now be run either serially or in parallel (default)</li>
 <li>New command line options to override properties</li>
 <li>New Size Assertion</li>
 
 </ul>
 
 <h3>Version 1.8.1</h3>
 <ul>
 <li>Bug Fix Release.  Many bugs were fixed.</li>
 <li>Removed redundant "Root" node from test tree.</li>
 <li>Re-introduced Icons in test tree.</li>
 <li>Some re-organization of code to improve build process.</li>
 <li>View Results Tree has added option to view results as web document (still buggy at this point).</li>
 <li>New Total line in Aggregate Listener (still buggy at this point).</li>
 <li>Improvements to ability to change JMeter's Locale settings.</li>
 <li>Improvements to SSL Manager.</li>
 </ul>
 
 <h3>Version 1.8</h3>
 <ul>
 <li>Improvement to Aggregate report's calculations.</li>
 <li>Simplified application logging.</li>
 <li>New Duration Assertion.</li>
 <li>Fixed and improved Mailer Visualizer.</li>
 <li>Improvements to HTTP Sampler's recovery of resources (sockets and file handles).</li>
 <li>Improving JMeter's internal handling of test start/stop.</li>
 <li>Fixing and adding options to behavior of Interleave and Random Controllers.</li>
 <li>New Counter config element.</li>
 <li>New User Parameters config element.</li>
 <li>Improved performance of file opener.</li>
 <li>Functions and other elements can access global variables.</li>
 <li>Help system available within JMeter's GUI.</li>
 <li>Test Elements can be disabled.</li>
 <li>Language/Locale can be changed while running JMeter (mostly).</li>
 <li>View Results Tree can be configured to record only errors.</li>
 <li>Various bug fixes.</li>
 </ul>
 
 <b>Changes: for more info, contact <a href="mailto:mstover1@apache.org">Michael Stover</a></b>
 <h3>Version 1.7.3</h3>
 <ul>
 <li>New Functions that provide more ability to change requests dynamically during test runs.</li>
 <li>New language translations in Japanese and German.</li>
 <li>Removed annoying Log4J error messages.</li>
 <li>Improved support for loading JMeter 1.7 version test plan files (.jmx files).</li>
 <li>JMeter now supports proxy servers that require username/password authentication.</li>
 <li>Dialog box indicating test stopping doesn't hang JMeter on problems with stopping test.</li>
 <li>GUI can run multiple remote JMeter servers (fixes GUI bug that prevented this).</li>
 <li>Dialog box to help created function calls in GUI.</li>
 <li>New Keep-alive switch in HTTP Requests to indicate JMeter should or should not use Keep-Alive for sockets.</li>
 <li>HTTP Post requests can have GET style arguments in Path field.  Proxy records them correctly now.</li>
 <li>New User-defined test-wide static variables.</li>
 <li>View Results Tree now displays more information, including name of request (matching the name
 in the test tree) and full request and POST data.</li>
 <li>Removed obsolete View Results Visualizer (use View Results Tree instead).</li>
 <li>Performance enhancements.</li>
 <li>Memory use enhancements.</li>
 <li>Graph visualizer GUI improvements.</li>
 <li>Updates and fixes to Mailer Visualizer.</li>
 </ul>
  
 <h3>Version 1.7.2</h3>
 <ul>
 <li>JMeter now notifies user when test has stopped running.</li>
 <li>HTTP Proxy server records HTTP Requests with re-direct turned off.</li>
 <li>HTTP Requests can be instructed to either follow redirects or ignore them.</li>
 <li>Various GUI improvements.</li>
 <li>New Random Controller.</li>
 <li>New SOAP/XML-RPC Sampler.</li>
 </ul>
 
 <h3>Version 1.7.1</h3>
 <ul>
 <li>JMeter's architecture revamped for a more complete separation between GUI code and
 test engine code.</li>
 <li>Use of Avalon code to save test plans to XML as Configuration Objects</li>
 <li>All listeners can save data to file and load same data at later date.</li>
 </ul>
 
 <h3>Version 1.7Beta</h3> 
 <ul> 
 	<li>Better XML support for special characters (Tushar Bhatia) </li> 
 	<li>Non-GUI functioning  &amp; Non-GUI test plan execution  (Tushar Bhatia)</li> 
 	<li>Removing Swing dependence from base JMeter classes</li> 
 	<li>Internationalization (Takashi Okamoto)</li> 
 	<li>AllTests bug fix (neth6@atozasia.com)</li> 
 	<li>ClassFinder bug fix (neth6@atozasia.com)</li> 
 	<li>New Loop Controller</li> 
 	<li>Proxy Server records HTTP samples from browser 
 		(and documented in the user manual)</li> <li>Multipart Form support</li> 
 	<li>HTTP Header class for Header customization</li> 
 	<li>Extracting HTTP Header information from responses (Jamie Davidson)</li> 
 	<li>Mailer Visualizer re-added to JMeter</li> 
 	<li>JMeter now url encodes parameter names and values</li> 
 	<li>listeners no longer give exceptions if their gui's haven't been initialized</li> 
 	<li>HTTPS and Authorization working together</li> 
 	<li>New Http sampling that automatically parses HTML response 
 		for images to download, and includes the downloading of these 
 		images in total time for request (Neth neth6@atozasia.com) </li> 
 	<li>HTTP responses from server can be parsed for links and forms, 
 		and dynamic data can be extracted and added to test samples 
 		at run-time (documented)</li>  
 	<li>New Ramp-up feature (Jonathan O'Keefe)</li> 
 	<li>New visualizers (Neth)</li> 
 	<li>New Assertions for functional testing</li> 
 </ul>  
 
 <h3>Version 1.6.1</h3> 
 <ul> 
 	<li>Fixed saving and loading of test scripts (no more extra lines)</li> 
 	<li>Can save and load special characters (such as &quot;&amp;&quot; and &quot;&lt;&quot;).</li> 
 	<li>Can save and load timers and listeners.</li> 
 	<li>Minor bug fix for cookies (if you cookie value 
 		contained an &quot;=&quot;, then it broke).</li> 
 	<li>URL's can sample ports other than 80, and can test HTTPS, 
 		provided you have the necessary jars (JSSE)</li> 
 </ul> 
 
 <h3>Version 1.6 Alpha</h3> 
 <ul> 
 	<li>New UI</li> 
 	<li>Separation of GUI and Logic code</li> 	
 	<li>New Plug-in framework for new modules</li> 
 	<li>Enhanced performance</li> 
 	<li>Layering of test logic for greater flexibility</li> 
 	<li>Added support for saving of test elements</li> 
 	<li>Added support for distributed testing using a single client</li> 
 
 </ul> 
 <h3>Version 1.5.1</h3> 
 <ul> 
 	<li>Fixed bug that caused cookies not to be read if header name case not as expected.</li> 
 	<li>Clone entries before sending to sampler - prevents relocations from messing up 
 		information across threads</li> 
 	<li>Minor bug fix to convenience dialog for adding paramters to test sample.  
 		Bug prevented entries in dialog from appearing in test sample.</li> 
 	<li>Added xerces.jar to distribution</li> 
 	<li>Added junit.jar to distribution and created a few tests.</li> 
 	<li>Started work on new framework.  New files in cvs, but do not effect program yet.</li> 
 	<li>Fixed bug that prevent HTTPJMeterThread from delaying according to chosen timer.</li> 
 </ul>  
 <p> 
 <h3>Version 1.5</h3> 
 <ul>   
 	<li>Abstracted out the concept of the Sampler, SamplerController, and TestSample.   
 		A Sampler represents code that understands a protocol (such as HTTP, 
 		or FTP, RMI,   SMTP, etc..).  It is the code that actually makes the 
 		connection to whatever is   being tested.   A SamplerController 
 		represents code that understands how to organize and run a group   
 		of test samples.  It is what binds together a Sampler and it's test 
 		samples and runs them.   A TestSample represents code that understands 
 		how to gather information from the   user about a particular test.  
 		For a website, it would represent a URL and any   information to be sent 
 		with the URL.</li>   
 	<li>The UI has been updated to make entering test samples more convenient.</li>   
 	<li>Thread groups have been added, allowing a user to setup multiple test to run   
 		concurrently, and to allow sharing of test samples between those tests.</li>   
 	<li>It is now possible to save and load test samples.</li>   
 	<li>....and many more minor changes/improvements...</li> 
 </ul> 
 </p> 
 <p> 
 <b>Apache JMeter 1.4.1-dev</b> (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Cleaned up URLSampler code after tons of patches for better readability. (SM)</li>
    <li>Made JMeter send a special &quot;user-agent&quot; identifier. (SM)</li>
    <li>Fixed problems with redirection not sending cookies and authentication info and removed
      a warning with jikes compilation. Thanks to <a href="mailto:wtanaka@yahoo.com">Wesley
      Tanaka</a> for the patches (SM)</li>
    <li>Fixed a bug in the URLSampler that caused to skip one URL when testing lists of URLs and
      a problem with Cookie handling. Thanks to <a
      href="mailto:gjohnson@investlearning.com">Graham Johnson</a> for the patches (SM)</li>
    <li>Fixed a problem with POST actions. Thanks to <a href="mailto:sschaub@bju.edu">Stephen
      Schaub</a> for the patch (SM)</li>
  </ul>
  </p>
  <p>
  <b>Apache JMeter 1.4</b> - Jul 11 1999 (<a href="mailto:cimjpno@be.ibm.com">Jean-Pierre Norguet</a>,
  <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)
   <ul>
    <li>Fixed a problem with POST actions. Thanks to <a href="mailto:bburns@labs.gte.com">Brendan
      Burns</a> for the patch (SM)</li>
    <li>Added close button to the About box for those window managers who don't provide it.
      Thanks to Jan-Henrik Haukeland for pointing it out. (SM)</li>
    <li>Added the simple Spline sample visualizer (JPN)</li> 
 </ul> </p>
   <p><b>Apache JMeter 1.3</b> - Apr 16 1999
   (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>,
  <a href="mailto:luta.raphael@networks.vivendi.net">Raphaël Luta</a>)
 <ul>
    <li>Run the Garbage Collector and run finalization before starting to sampling to ensure
      same state every time (SM)</li>
    <li>Fixed some NullPointerExceptions here and there (SM)</li>
    <li>Added HTTP authentication capabilities (RL)</li>
    <li>Added windowed sample visualizer (SM)</li>
    <li>Fixed stupid bug for command line arguments. Thanks to <a
      href="mailto:jbracer@infoneers.com">Jorge Bracer</a> for pointing this out (SM)</li> 
 </ul> </p>
   <p><b>Apache JMeter 1.2</b> - Mar 17 1999 (<a href="mailto:sdowd@arcmail.com">Sean Dowd</a>, 
 <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Integrated cookie capabilities with JMeter (SM)</li>
    <li>Added the Cookie manager and Netscape file parser (SD)</li>
    <li>Fixed compilation error for JDK 1.1 (SD)</li> </ul> </p>  
 <p> <b>Apache JMeter 1.1</b> - Feb 24 1999 (<a href="mailto:sdowd@arcmail.com">Sean Dowd</a>, 
 <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Created the opportunity to create URL aliasing from the properties file as well as the
      ability to associate aliases to URL sequences instead of single URLs (SM) Thanks to <a
      href="mailto:chatfield@evergreen.com">Simon Chatfield</a> for the very nice suggestions
      and code examples.</li>
    <li>Removed the TextVisualizer and replaced it with the much more useful FileVisualizer (SM)</li>
    <li>Added the known bug list (SM)</li>
    <li>Removed the Java Apache logo (SM)</li>
    <li>Fixed a couple of typos (SM)</li>
    <li>Added UNIX makefile (SD)</li> </ul> </p> 
 <p> <b>Apache JMeter 1.0.1</b> - Jan 25 1999 (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Removed pending issues doc issues (SM)</li>
    <li>Fixed the unix script (SM)</li>
    <li>Added the possibility of running the JAR directly using &quot;java -jar
      ApacheJMeter.jar&quot; with Java 2 (SM)</li>
    <li>Some small updates: fixed Swing location after Java 2(tm) release, license update and
      small cleanups (SM)</li> 
 </ul> </p> 
 <p> <b>Apache JMeter 1.0</b> - Dec 15 1998 (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>) 
 <ul>
    <li>Initial version. (SM)</li> 
 </ul> </p> 
 </section> 
 </body> 
 </document>
