diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index 1c927d3c7..8cc9e68b4 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,1006 +1,1007 @@
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
 
 import org.apache.jmeter.assertions.AssertionResult;
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
 
 	private JPanel resultsPane;
 
 	private JLabel imageLabel;
 
 	private JTextArea sampleDataField;
 	
 	private JPanel requestPane;
 
 	private JRadioButton textButton;
 
 	private JRadioButton htmlButton;
 
 	private JRadioButton xmlButton;
 
 	private JCheckBox downloadAll;
 
 	private JTree jTree;
 
 	private JTabbedPane rightSide;
 	
 	private static final ImageIcon imageSuccess = JMeterUtils.getImage(
 	        JMeterUtils.getPropDefault("viewResultsTree.success", "icon_success_sml.gif"));
 
 	private static final ImageIcon imageFailure = JMeterUtils.getImage(
 			JMeterUtils.getPropDefault("viewResultsTree.failure", "icon_warning_sml.gif"));
 	
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
 		// Add sample
 		DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(res);
 		treeModel.insertNodeInto(currNode, root, root.getChildCount());
 		addSubResults(currNode, res);
 		// Add any assertion that failed as children of the sample node
 		AssertionResult assertionResults[] = res.getAssertionResults();
 		int assertionIndex = currNode.getChildCount();
 		for (int j = 0; j < assertionResults.length; j++) {
 			AssertionResult item = assertionResults[j];
 			
 			if (item.isFailure() || item.isError()) {
 				DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(item);
 				treeModel.insertNodeInto(assertionNode, currNode, assertionIndex++);
 			}
 		}			
 
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
             // Add any assertion that failed as children of the sample node
             AssertionResult assertionResults[] = child.getAssertionResults();
             int assertionIndex = leafNode.getChildCount();
             for (int j = 0; j < assertionResults.length; j++) {
                 AssertionResult item = assertionResults[j];
                 
                 if (item.isFailure() || item.isError()) {
                     DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(item);
                     treeModel.insertNodeInto(assertionNode, leafNode, assertionIndex++);
                 }
             }
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
 				Object userObject = node.getUserObject();
 				if(userObject instanceof SampleResult) {					
 					SampleResult res = (SampleResult) userObject;
 					
 					// We are displaying a SampleResult
 					setupTabPaneForSampleResult();
 
 					if (log.isDebugEnabled()) {
 						log.debug("valueChanged1 : sample result - " + res);
 					}
 
 					if (res != null) {
 						// load time label
 
 						log.debug("valueChanged1 : load time - " + res.getTime());
 						String sd = res.getSamplerData();
 						if (sd != null) {
 							String rh = res.getRequestHeaders();
 							if (rh != null) {
 								StringBuffer sb = new StringBuffer(sd.length() + rh.length()+20);
 								sb.append(sd);
 								sb.append("\nRequest Headers:\n");
 								sb.append(rh);
 								sd = sb.toString();
 							}
 							sampleDataField.setText(sd);
 						}
 
-						statsDoc.insertString(statsDoc.getLength(), "Thread Name: " + res.getThreadName() + "\n", null);
+						statsDoc.insertString(statsDoc.getLength(), "Thread Name: " + res.getThreadName() + "\n", null); // $NON-NLS-2$
 						String startTime = new Date(res.getStartTime()).toString();
-						statsDoc.insertString(statsDoc.getLength(), "Sample Start: " + startTime + "\n", null);
-						statsDoc.insertString(statsDoc.getLength(), "Load time: " + res.getTime() + "\n", null);
+						statsDoc.insertString(statsDoc.getLength(), "Sample Start: " + startTime + "\n", null); // $NON-NLS-2$
+						statsDoc.insertString(statsDoc.getLength(), "Load time: " + res.getTime() + "\n", null); // $NON-NLS-2$
+						statsDoc.insertString(statsDoc.getLength(), "Size in bytes: " + res.getBytes() + "\n", null); // $NON-NLS-2$
 
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
 						statsDoc.insertString(statsDoc.getLength(), "HTTP response message: " + responseMsgStr + "\n", null);
 
 						statsDoc.insertString(statsDoc.getLength(), "\nHTTP response headers:\n" + res.getResponseHeaders() + "\n", null);
 
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
 				else if(userObject instanceof AssertionResult) {
 					AssertionResult res = (AssertionResult) userObject;
 					
 					// We are displaying an AssertionResult
 					setupTabPaneForAssertionResult();
 					
 					if (log.isDebugEnabled()) {
 						log.debug("valueChanged1 : sample result - " + res);
 					}
 
 					if (res != null) {
 						statsDoc.insertString(statsDoc.getLength(),
 								"Assertion error: " + res.isError() + "\n",
 								null);
 						statsDoc.insertString(statsDoc.getLength(),
 								"Assertion failure: " + res.isFailure() + "\n",
 								null);
 						statsDoc.insertString(statsDoc.getLength(),
 								"Assertion failure message : " + res.getFailureMessage() + "\n",
 								null);
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
 
 	private Component createHtmlOrTextPane() {
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
 		rightSide = new JTabbedPane();
 		// Add the common tab
 		rightSide.addTab(JMeterUtils.getResString("view_results_tab_sampler"), createResponseMetadataPanel()); // $NON-NLS-1$
 		// Create the panels for the other tabs
 		requestPane = createRequestPanel();
 		resultsPane = createResponseDataPanel();
 
 		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
 		add(mainSplit, BorderLayout.CENTER);
 	}
 	
 	private void setupTabPaneForSampleResult() {
 		// Set the title for the first tab
 		rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_sampler"));
 		// Add the other tabs if not present
 		if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")) < 0) { // $NON-NLS-1$
 			rightSide.addTab(JMeterUtils.getResString("view_results_tab_request"), requestPane); // $NON-NLS-1$
 		}
 		if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")) < 0) { // $NON-NLS-1$
 			rightSide.addTab(JMeterUtils.getResString("view_results_tab_response"), resultsPane); // $NON-NLS-1$
 		}
 	}
 	
 	private void setupTabPaneForAssertionResult() {
 		// Set the title for the first tab
 		rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_assertion"));
 		// Remove the other tabs if present
 		int requestTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")); // $NON-NLS-1$
 		if(requestTabIndex >= 0) {
 			rightSide.removeTabAt(requestTabIndex);
 		}
 		int responseTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")); // $NON-NLS-1$
 		if(responseTabIndex >= 0) {
 			rightSide.removeTabAt(responseTabIndex);
 		}
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
 
 	private JPanel createRequestPanel() {
 		sampleDataField = new JTextArea();
 		sampleDataField.setEditable(false);
 		sampleDataField.setLineWrap(true);
 		sampleDataField.setWrapStyleWord(true);
 
 		JPanel pane = new JPanel(new BorderLayout(0, 5));
 		pane.add(makeScrollPane(sampleDataField));
 		return pane;
 	}
 
 	private JPanel createResponseDataPanel() {
 		results = new JEditorPane();
 		results.setEditable(false);
 
 		resultsScrollPane = makeScrollPane(results);
 		imageLabel = new JLabel();
 
 		JPanel panel = new JPanel(new BorderLayout());
 		panel.add(resultsScrollPane, BorderLayout.CENTER);
 		panel.add(createHtmlOrTextPane(), BorderLayout.SOUTH);
 
 		return panel;
 	}
 
 	private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
 		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
 				boolean leaf, int row, boolean focus) {
 			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
 			boolean failure = true;
 			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
 			if(userObject instanceof SampleResult) {
 				failure = !(((SampleResult) userObject).isSuccessful());
 			}
 			else if(userObject instanceof AssertionResult) {
 				AssertionResult assertion = (AssertionResult) userObject;
 				failure =  assertion.isError() || assertion.isFailure();
 			}
 			
 			// Set the status for the node
 			if (failure) {
 				this.setForeground(Color.red);
 				this.setIcon(imageFailure);
 			} else {
 				this.setIcon(imageSuccess);
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
diff --git a/src/core/org/apache/jmeter/samplers/SampleResult.java b/src/core/org/apache/jmeter/samplers/SampleResult.java
index a40a90e67..ee1cd00a2 100644
--- a/src/core/org/apache/jmeter/samplers/SampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/SampleResult.java
@@ -1,913 +1,916 @@
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
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.apache.avalon.framework.configuration.Configuration;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * This is a nice packaging for the various information returned from taking a
  * sample of an entry.
  * 
  */
 public class SampleResult implements Serializable {
 
     // Needs to be accessible from Test code
     static final Logger log = LoggingManager.getLoggerForClass();
 
 	// Bug 33196 - encoding ISO-8859-1 is only suitable for Western countries
 	// However the suggested System.getProperty("file.encoding") is Cp1252 on
 	// Windows
 	// So use a new property with the original value as default
 	private static final String DEFAULT_ENCODING 
             = JMeterUtils.getPropDefault("sampleresult.default.encoding", // $NON-NLS-1$
 			"ISO-8859-1"); // $NON-NLS-1$
 
 	/**
 	 * Data type value indicating that the response data is text.
 	 * 
 	 * @see #getDataType
 	 * @see #setDataType(java.lang.String)
 	 */
 	public final static String TEXT = "text"; // $NON-NLS-1$
 
 	/**
 	 * Data type value indicating that the response data is binary.
 	 * 
 	 * @see #getDataType
 	 * @see #setDataType(java.lang.String)
 	 */
 	public final static String BINARY = "bin"; // $NON-NLS-1$
 
 	/* empty arrays which can be returned instead of null */
 	private static final byte[] EMPTY_BA = new byte[0];
 
 	private static final SampleResult[] EMPTY_SR = new SampleResult[0];
 
 	private static final AssertionResult[] EMPTY_AR = new AssertionResult[0];
 
 	private SampleSaveConfiguration saveConfig;
 
 	private SampleResult parent = null;
 
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
 
 	private byte[] responseData = EMPTY_BA;
 
 	private String responseCode = "";// Never return null
 
 	private String label = "";// Never return null
 
     private String resultFileName = ""; // Filename used by ResultSaver
     
 	private String samplerData;
 
 	private String threadName = ""; // Never return null
 
 	private String responseMessage = "";
 
 	private String responseHeaders = ""; // Never return null
 
 	private String contentType = ""; // e.g. text/html; charset=utf-8
 
 	private String requestHeaders = "";
 
 	private long timeStamp = 0;// the time stamp - can be start or end
 
 	private long startTime = 0;
 
 	private long endTime = 0;
 
 	private long idleTime = 0;// Allow for non-sample time
 
 	private long pauseTime = 0;// Start of pause (if any)
 
 	private List assertionResults;
 
 	private List subResults;
 
 	private String dataType=""; // Don't return null if not set
 
 	private boolean success;
 
 	private Set files; // files that this sample has been saved in
 
 	private String dataEncoding;// (is this really the character set?) e.g.
 								// ISO-8895-1, UTF-8
 	// If null, then DEFAULT_ENCODING is returned by getDataEncoding()
 
 	private long time = 0;
 
 	private long latency = 0;
 
 	private boolean stopThread = false; // Should thread terminate?
 
 	private boolean stopTest = false; // Should test terminate?
 
 	private boolean isMonitor = false;
 
 	private int sampleCount = 1;
 
 	private int bytes = 0;
 
 	// TODO do contentType and/or dataEncoding belong in HTTPSampleResult instead?
 
 	private final static String TOTAL_TIME = "totalTime"; // $NON-NLS-1$
 
 	private static final boolean startTimeStamp 
         = JMeterUtils.getPropDefault("sampleresult.timestamp.start", false);  // $NON-NLS-1$
 
 	static {
 		if (startTimeStamp) {
 			log.info("Note: Sample TimeStamps are START times");
 		} else {
 			log.info("Note: Sample TimeStamps are END times");
 		}
 		log.info("sampleresult.default.encoding is set to " + DEFAULT_ENCODING);
 	}
 
 	public SampleResult() {
 		time = 0;
 	}
 
 	/**
 	 * Construct a 'parent' result for an already-existing result, essentially
 	 * cloning it
 	 * 
 	 * @param res
 	 *            existing sample result
 	 */
 	public SampleResult(SampleResult res) {
 		setStartTime(res.getStartTime());
 		setEndTime(res.getStartTime()); 
 		// was setElapsed(0) which is the same as setStartTime=setEndTime=now
 
 		setSampleLabel(res.getSampleLabel());
 		setRequestHeaders(res.getRequestHeaders());
 		setResponseData(res.getResponseData());
 		setResponseCode(res.getResponseCode());
 		setSuccessful(res.isSuccessful());
 		setResponseMessage(res.getResponseMessage());
 		setDataType(res.getDataType());
 		setResponseHeaders(res.getResponseHeaders());
         setContentType(res.getContentType());
         setDataEncoding(res.getDataEncoding());
 		setURL(res.getURL());
 
 		addSubResult(res); // this will add res.getTime() to getTime().
 	}
 
 	public boolean isStampedAtStart() {
 		return startTimeStamp;
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
 		long now = System.currentTimeMillis();
 		if (atend) {
 			setTimes(now - elapsed, now);
 		} else {
 			setTimes(now, now + elapsed);
 		}
 	}
 
 	/**
 	 * Create a sample with specific start and end times for test purposes, but
 	 * don't allow the times to be changed later
 	 * 
 	 * (used by StatVisualizerModel.Test)
 	 * 
 	 * @param start
 	 *            start time
 	 * @param end
 	 *            end time
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
 	 * @param elapsed -
 	 *            desired elapsed time
 	 */
 	public static SampleResult createTestSample(long elapsed) {
 		long now = System.currentTimeMillis();
 		return createTestSample(now, now + elapsed);
 	}
 
 	/**
 	 * Allow users to create a sample with specific timestamp and elapsed times
 	 * for cloning purposes, but don't allow the times to be changed later
 	 * 
 	 * Currently used by OldSaveService only
 	 * 
 	 * @param stamp -
 	 *            this may be a start time or an end time
 	 * @param elapsed
 	 */
 	public SampleResult(long stamp, long elapsed) {
 		stampAndTime(stamp, elapsed);
 	}
 
 	// Helper method to maintain timestamp relationships
 	private void stampAndTime(long stamp, long elapsed) {
 		if (startTimeStamp) {
 			setTimes(stamp, stamp + elapsed);
 		} else {
 			setTimes(stamp - elapsed, stamp);
 		}
 	}
 
 	/*
 	 * For use by SaveService only.
 	 *  
 	 * @param stamp -
 	 *            this may be a start time or an end time
 	 * @param elapsed
 	 */
 	public void setStampAndTime(long stamp, long elapsed) {
 		if (startTime != 0 || endTime != 0){
 			throw new RuntimeException("Calling setStampAndTime() after start/end times have been set");
 		}
 		stampAndTime(stamp, elapsed);
 	}
 
 	/**
 	 * Method to set the elapsed time for a sample. Retained for backward
 	 * compatibility with 3rd party add-ons.
      * It is assumed that the method is only called at the end of a sample
      * and that timeStamps are end-times
 	 * 
      * Also used by SampleResultConverter when creating results from files.
      * 
 	 * Must not be used in conjunction with sampleStart()/End()
 	 * 
 	 * @deprecated use sampleStart() and sampleEnd() instead
 	 * @param elapsed
 	 *            time in milliseconds
 	 */
 	public void setTime(long elapsed) {
 		if (startTime != 0 || endTime != 0){
 			throw new RuntimeException("Calling setTime() after start/end times have been set");
 		}
 		long now = System.currentTimeMillis();
 	    setTimes(now - elapsed, now);
 	}
 
 	public void setMarked(String filename) {
 		if (files == null) {
 			files = new HashSet();
 		}
 		files.add(filename);
 	}
 
 	public boolean isMarked(String filename) {
 		return files != null && files.contains(filename);
 	}
 
 	public String getResponseCode() {
 		return responseCode;
 	}
 
     private static final String OK = Integer.toString(HttpURLConnection.HTTP_OK);
     
     /**
      * Set response code to OK, i.e. "200"
      *
      */
     public void setResponseCodeOK(){
         responseCode=OK;
     }
     
 	public void setResponseCode(String code) {
 		responseCode = code;
 	}
 
     public boolean isResponseCodeOK(){
         return responseCode.equals(OK);
     }
 	public String getResponseMessage() {
 		return responseMessage;
 	}
 
 	public void setResponseMessage(String msg) {
 		responseMessage = msg;
 	}
 
     public void setResponseMessageOK() {
         responseMessage = "OK"; // $NON-NLS-1$       
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
 
 	public void setSampleLabel(String label) {
 		this.label = label;
 	}
 
 	public void addAssertionResult(AssertionResult assertResult) {
 		if (assertionResults == null) {
 			assertionResults = new ArrayList();
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
 		return (AssertionResult[]) assertionResults.toArray(new AssertionResult[0]);
 	}
 
 	public void addSubResult(SampleResult subResult) {
 		String tn = getThreadName();
         if (tn.length()==0) {
         	tn=Thread.currentThread().getName();//TODO do this more efficiently
             this.setThreadName(tn);
         }
         subResult.setThreadName(tn);
 		if (subResults == null) {
 			subResults = new ArrayList();
 		}
 		subResults.add(subResult);
-		setEndTime(subResult.getEndTime());// Extend the time to the end of the added sample
+		// Extend the time to the end of the added sample
+		setEndTime(subResult.getEndTime());
+		// Include the byte count for the added sample
+		setBytes(getBytes() + subResult.getBytes());
 		subResult.setParent(this);
 	}
 
     /**
      * Add a subresult read from a results file.
      * 
      * As for addSubResult(), except that the fields don't need to be accumulated
      * 
      * @param subResult
      */
     public void storeSubResult(SampleResult subResult) {
         if (subResults == null) {
             subResults = new ArrayList();
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
 		return (SampleResult[]) subResults.toArray(new SampleResult[0]);
 	}
 
 	public void configure(Configuration info) {
 		time = info.getAttributeAsLong(TOTAL_TIME, 0L);
 	}
 
 	/**
 	 * Sets the responseData attribute of the SampleResult object.
 	 * 
 	 * @param response
 	 *            the new responseData value
 	 */
 	public void setResponseData(byte[] response) {
 		responseData = response;
 	}
 
     /**
      * Sets the responseData attribute of the SampleResult object.
      * 
      * @param response
      *            the new responseData value (String)
      * 
      * @deprecated - only intended for use from BeanShell code
      */
     public void setResponseData(String response) {
         responseData = response.getBytes();
     }
 
 	/**
 	 * Gets the responseData attribute of the SampleResult object.
 	 * 
 	 * @return the responseData value
 	 */
 	public byte[] getResponseData() {
 		return responseData;
 	}
 
     /**
      * Gets the responseData attribute of the SampleResult object.
      * 
      * @return the responseData value as a String, converted according to the encoding
      */
     public String getResponseDataAsString() {
         try {
             return new String(responseData,getDataEncoding());
         } catch (UnsupportedEncodingException e) {
             log.warn("Using "+dataEncoding+" caused "+e);
             return new String(responseData);
         }
     }
 
 	/**
 	 * Convenience method to get responseData as a non-null byte array
 	 * 
 	 * @return the responseData. If responseData is null then an empty byte
 	 *         array is returned rather than null.
 	 *
 	 * @deprecated - no longer needed, as getResponseData() does not return null
 	 */
 	public byte[] getResponseDataAsBA() {
 		return responseData == null ? EMPTY_BA : responseData;
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
 		return time;
 	}
 
 	public boolean isSuccessful() {
 		return success;
 	}
 
 	public void setDataType(String dataType) {
 		this.dataType = dataType;
 	}
 
 	public String getDataType() {
 		return dataType;
 	}
     /**
      * Set Encoding and DataType from ContentType
      * @param ct - content type (may be null)
      */
     public void setEncodingAndType(String ct){
         if (ct != null) {
             // Extract charset and store as DataEncoding
             // TODO do we need process http-equiv META tags, e.g.:
             // <META http-equiv="content-type" content="text/html;
             // charset=foobar">
             // or can we leave that to the renderer ?
             String de = ct.toLowerCase();
             final String cs = "charset="; // $NON-NLS-1$
             int cset = de.indexOf(cs);
             if (cset >= 0) {
                 setDataEncoding(de.substring(cset + cs.length()));
             }
             if (ct.startsWith("image/")) {// $NON-NLS-1$
                 setDataType(BINARY);
             } else {
                 setDataType(TEXT);
             }
         }
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
 	public String toString() {
 		return getSampleLabel();
 	}
 
 	/**
 	 * Returns the dataEncoding.
 	 */
 	public String getDataEncoding() {
 		if (dataEncoding != null) {
 			return dataEncoding;
 		}
 		return DEFAULT_ENCODING;
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
 	 * @return whether to stop this thread
 	 */
 	public boolean isStopThread() {
 		return stopThread;
 	}
 
 	/**
 	 * @param b
 	 */
 	public void setStopTest(boolean b) {
 		stopTest = b;
 	}
 
 	/**
 	 * @param b
 	 */
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
         return JOrphanUtils.trim(contentType," ;").toLowerCase();
     }
 
 	/**
 	 * @param string
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
 		if (startTimeStamp) {
 			timeStamp = startTime;
 		}
 	}
 
 	protected void setEndTime(long end) {
 		endTime = end;
 		if (!startTimeStamp) {
 			timeStamp = endTime;
 		}
 		if (startTime == 0) {
 			log.error("setEndTime must be called after setStartTime", new Throwable("Invalid call sequence"));
 			// TODO should this throw an error?
 		} else {
 			time = endTime - startTime - idleTime;
 		}
 	}
 
 	private void setTimes(long start, long end) {
 		setStartTime(start);
 		setEndTime(end);
 	}
 
 	/**
 	 * Record the start time of a sample
 	 * 
 	 */
 	public void sampleStart() {
 		if (startTime == 0) {
 			setStartTime(System.currentTimeMillis());
 		} else {
 			log.error("sampleStart called twice", new Throwable("Invalid call sequence"));
 		}
 	}
 
 	/**
 	 * Record the end time of a sample and calculate the elapsed time
 	 * 
 	 */
 	public void sampleEnd() {
 		if (endTime == 0) {
 			setEndTime(System.currentTimeMillis());
 		} else {
 			log.error("sampleEnd called twice", new Throwable("Invalid call sequence"));
 		}
 	}
 
 	/**
 	 * Pause a sample
 	 * 
 	 */
 	public void samplePause() {
 		if (pauseTime != 0) {
 			log.error("samplePause called twice", new Throwable("Invalid call sequence"));
 		}
 		pauseTime = System.currentTimeMillis();
 	}
 
 	/**
 	 * Resume a sample
 	 * 
 	 */
 	public void sampleResume() {
 		if (pauseTime == 0) {
 			log.error("sampleResume without samplePause", new Throwable("Invalid call sequence"));
 		}
 		idleTime += System.currentTimeMillis() - pauseTime;
 		pauseTime = 0;
 	}
 
 	/**
 	 * When a Sampler is working as a monitor
 	 * 
 	 * @param monitor
 	 */
 	public void setMonitor(boolean monitor) {
 		isMonitor = monitor;
 	}
 
 	/**
 	 * If the sampler is a monitor, method will return true.
 	 * 
 	 * @return true if the sampler is a monitor
 	 */
 	public boolean isMonitor() {
 		return isMonitor;
 	}
 
 	/**
 	 * For the JMS sampler, it can perform multiple samples for greater degree
 	 * of accuracy.
 	 * 
 	 * @param count
 	 */
 	public void setSampleCount(int count) {
 		sampleCount = count;
 	}
 
 	/**
 	 * return the sample count. by default, the value is 1.
 	 * 
 	 * @return
 	 */
 	public int getSampleCount() {
 		return sampleCount;
 	}
 
 	/**
 	 * In the event the sampler does want to pass back the actual contents, we
 	 * still want to calculate the throughput. The bytes is the bytes of the
 	 * response data.
 	 * 
 	 * @param length
 	 */
 	public void setBytes(int length) {
 		bytes = length;
 	}
 
 	/**
 	 * return the bytes returned by the response.
 	 * 
 	 * @return
 	 */
 	public int getBytes() {
 		return bytes == 0 ? responseData.length : bytes;
 	}
 
 	/**
 	 * @return Returns the latency.
 	 */
 	public long getLatency() {
 		return latency;
 	}
 
     /**
      * Set the time to the first response
      *
      */
 	public void latencyEnd() {
 		latency = System.currentTimeMillis() - startTime - idleTime;
 	}
 
 	/**
      * This is only intended for use by SampleResultConverter!
      * 
 	 * @param latency
 	 *            The latency to set.
 	 */
 	public void setLatency(long latency) {
 		this.latency = latency;
 	}
 
 	/**
      * This is only intended for use by SampleResultConverter!
      *
 	 * @param timeStamp
 	 *            The timeStamp to set.
 	 */
 	public void setTimeStamp(long timeStamp) {
 		this.timeStamp = timeStamp;
 	}
 
 	private URL location;
 
 	public void setURL(URL location) {
 		this.location = location;
 	}
 
 	public URL getURL() {
 		return location;
 	}
 	
 	/**
 	 * Get a String representation of the URL (if defined).
 	 * 
 	 * @return ExternalForm of URL, or empty string if url is null
 	 */
 	public String getUrlAsString() {
 		return location == null ? "" : location.toExternalForm();
 	}
 
 	/**
 	 * @return Returns the parent.
 	 */
 	public SampleResult getParent() {
 		return parent;
 	}
 
 	/**
 	 * @param parent
 	 *            The parent to set.
 	 */
 	public void setParent(SampleResult parent) {
 		this.parent = parent;
 	}
 
     public String getResultFileName() {
         return resultFileName;
     }
 
     public void setResultFileName(String resultFileName) {
         this.resultFileName = resultFileName;
     }
 }
diff --git a/test/src/org/apache/jmeter/samplers/TestSampleResult.java b/test/src/org/apache/jmeter/samplers/TestSampleResult.java
index 25f90858c..44d59ef6c 100644
--- a/test/src/org/apache/jmeter/samplers/TestSampleResult.java
+++ b/test/src/org/apache/jmeter/samplers/TestSampleResult.java
@@ -1,90 +1,173 @@
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
 
 import java.io.StringWriter;
 
 import junit.framework.TestCase;
 
+import org.apache.jmeter.util.Calculator;
 import org.apache.log.LogTarget;
 import org.apache.log.format.Formatter;
 import org.apache.log.format.RawFormatter;
 import org.apache.log.output.io.WriterTarget;
 
 // TODO need more tests - particularly for the new functions
 
 public class TestSampleResult extends TestCase {
         public TestSampleResult(String name) {
             super(name);
         }
 
         public void testElapsed() throws Exception {
             SampleResult res = new SampleResult();
 
             // Check sample increments OK
             res.sampleStart();
             Thread.sleep(100);
             res.sampleEnd();
         	long time = res.getTime();
             if(time < 100){
 				fail("Sample time should be >=100, actual "+time);
             }
         }
 
         public void testPause() throws Exception {
             SampleResult res = new SampleResult();
             // Check sample increments OK
             res.sampleStart();
             Thread.sleep(100);
             res.samplePause();
 
             Thread.sleep(200);
 
             // Re-increment
             res.sampleResume();
             Thread.sleep(100);
             res.sampleEnd();
             long sampleTime = res.getTime();
             if ((sampleTime < 200) || (sampleTime > 290)) {
                 fail("Accumulated time (" + sampleTime + ") was not between 200 and 290 ms");
             }
         }
 
         private static Formatter fmt = new RawFormatter();
 
         private StringWriter wr = null;
 
         private void divertLog() {// N.B. This needs to divert the log for SampleResult
             wr = new StringWriter(1000);
             LogTarget[] lt = { new WriterTarget(wr, fmt) };
             SampleResult.log.setLogTargets(lt);
         }
 
         public void testPause2() throws Exception {
             divertLog();
             SampleResult res = new SampleResult();
             res.sampleStart();
             res.samplePause();
             assertTrue(wr.toString().length() == 0);
             res.samplePause();
             assertFalse(wr.toString().length() == 0);
         }
+        
+        public void testByteCount() throws Exception {
+            SampleResult res = new SampleResult();
+            
+            res.sampleStart();
+            res.setBytes(100);
+            res.setSampleLabel("sample of size 100 bytes");
+            res.sampleEnd();
+            assertEquals(100, res.getBytes());
+            assertEquals("sample of size 100 bytes", res.getSampleLabel());
+        }
+
+        public void testSubResults() throws Exception {
+        	// This test tries to emulate a http sample, with two
+        	// subsamples, representing images that are downloaded for the
+        	// page representing the first sample.
+        	
+            // Sample that will get two sub results, simulates a web page load 
+            SampleResult resWithSubResults = new SampleResult();            
+            resWithSubResults.sampleStart();
+            Thread.sleep(100);
+            resWithSubResults.setBytes(300);
+            resWithSubResults.setSampleLabel("sample with two subresults");
+            resWithSubResults.setSuccessful(true);
+            resWithSubResults.sampleEnd();
+            long sampleWithSubResultsTime = resWithSubResults.getTime();
+        	
+            // Sample with no sub results, simulates an image download
+            SampleResult resNoSubResults1 = new SampleResult();            
+            resNoSubResults1.sampleStart();
+            Thread.sleep(100);
+            resNoSubResults1.setBytes(100);
+            resNoSubResults1.setSampleLabel("sample with no subresults");
+            resNoSubResults1.setSuccessful(true);
+            resNoSubResults1.sampleEnd();
+            long sample1Time = resNoSubResults1.getTime();
+
+            assertTrue(resNoSubResults1.isSuccessful());
+            assertEquals(100, resNoSubResults1.getBytes());
+            assertEquals("sample with no subresults", resNoSubResults1.getSampleLabel());
+            assertEquals(1, resNoSubResults1.getSampleCount());
+            assertEquals(0, resNoSubResults1.getSubResults().length);
+            
+            // Sample with no sub results, simulates an image download 
+            SampleResult resNoSubResults2 = new SampleResult();            
+            resNoSubResults2.sampleStart();
+            Thread.sleep(100);
+            resNoSubResults2.setBytes(200);
+            resNoSubResults2.setSampleLabel("sample with no subresults");
+            resNoSubResults2.setSuccessful(true);
+            resNoSubResults2.sampleEnd();
+            long sample2Time = resNoSubResults2.getTime();
+
+            assertTrue(resNoSubResults2.isSuccessful());
+            assertEquals(200, resNoSubResults2.getBytes());
+            assertEquals("sample with no subresults", resNoSubResults2.getSampleLabel());
+            assertEquals(1, resNoSubResults2.getSampleCount());
+            assertEquals(0, resNoSubResults2.getSubResults().length);
+            
+            // Now add the subsamples to the sample
+            resWithSubResults.addSubResult(resNoSubResults1);
+            resWithSubResults.addSubResult(resNoSubResults2);
+            assertTrue(resWithSubResults.isSuccessful());
+            assertEquals(600, resWithSubResults.getBytes());
+            assertEquals("sample with two subresults", resWithSubResults.getSampleLabel());
+            assertEquals(1, resWithSubResults.getSampleCount());
+            assertEquals(2, resWithSubResults.getSubResults().length);
+            long totalTime = resWithSubResults.getTime();
+            
+            // Check the sample times
+            assertEquals(sampleWithSubResultsTime + sample1Time + sample2Time, totalTime);
+            
+            // Check that calculator gets the correct statistics from the sample
+            Calculator calculator = new Calculator();
+            calculator.addSample(resWithSubResults);
+            assertEquals(600, calculator.getTotalBytes());
+            assertEquals(1, calculator.getCount());
+            assertEquals(1d / (totalTime / 1000d), calculator.getRate(),0d);
+            // Check that the throughput uses the time elapsed for the sub results
+            assertFalse(1d / (sampleWithSubResultsTime / 1000d) <= calculator.getRate());
+        }
+
         // TODO some more invalid sequence tests needed
 }
 
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index af98a6ae2..c6e0caf98 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,860 +1,861 @@
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
 <p><b>Changes sections are chronologically ordered from top (most recent) to bottom 
 (least recent)</b></p>  
 
 <!--  ===================  -->
 
 <h3>Version 2.2.1</h3>
 <h4>Summary of changes (for more details, see below)</h4>
 <p>
 Some of the main enhancements are:
 </p>
 <ul>
 <li>Htmlparser 2.0 now used for parsing</li>
 <li>HTTP Authorisation now supports domain and realm</li>
 <li>HttpClient options can be specified via httpclient.parameters file</li>
 <li>HttpClient now behaves the same as Java Http for SSL certificates</li>
 <li>HTTP Mirror Server to allow local testing of HTTP samplers</li>
 <li>HTTP Proxy supports XML-RPC recording</li>
 <li>__V() function allows support of nested variable references</li>
 <li>LDAP Ext sampler optionally parses result sets and supports secure mode</li>
 </ul>
 <p>
 The main bug fixes are:
 </p>
 <ul>
 <li>HTTPS (SSL) handling now much improved</li>
 <li>Various Remote mode bugs fixed</li>
 <li>Control+C and Control+V now work in the test tree</li>
 <li>Latency and Encoding now available in CSV log output</li>
 </ul>
 <h4>Known problems:</h4>
 <p>Thread active counts are always zero in CSV and XML files when running remote tests.
 </p>
 <p>The property file_format.testlog=2.1 is treated the same as 2.2.
 However JMeter does honour the 3 testplan versions.</p>
 <p>
 Bug 22510 - JMeter always uses the first entry in the keystore.
 </p>
 <p>
 Remote mode does not work if JMeter is installed in a directory where the path name contains spaces.
 </p>
 <p>
 BeanShell test elements leak memory.
 This can be reduced by using a file instead of including the script in the test element.
 </p>
 <p>
 Variables and functions do not work in Listeners in client-server (remote) mode so cannot be used
 to name log files.
 </p>
 <h4>Incompatible changes (usage):</h4>
 <p>
 The LDAP Extended Sampler now uses the same panel for both Thread Bind and Single-Bind tests.
 This means that any tests using the Single-bind test will need to be updated to set the username and password.
 </p>
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
 <h4>Incompatible changes (development):</h4>
 <p>
 Calulator and SamplingStatCalculator classes no longer provide any formatting of their data.
 Formatting should now be done using the jorphan.gui Renderer classes.
 </p>
 <p>
 Removed deprecated method JMeterUtils.split() - use JOrphanUtils version instead.
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
 <li>Bug 39717 - use icons in the results tree</li>
 <li>Added __V variable function to resolve nested variable names</li>
 <li>Bug 41707 - HTTP Proxy XML-RPC support</li>
 <li>Bug 40381 - LDAP: more descriptive strings</li>
 <li>Bug 40369 (partial) Equals Response Assertion</li>
 <li>Bug 41705 - add content-encoding option to HTTP samplers for POST requests</li>
 <li>Bug 40933, 40945 - optional matching of embedded resource URLs</li>
 <li>Bug 41704 - Allow charset encoding to be specified for CSV DataSet</li>
 <li>Bug 40103 - various LDAP enhancements</li>
 <li>Bug 39864 - BeanShell init files now found from currrent or bin directory</li>
 <li>New -j option to easily change jmeter log file</li>
 <li>Bug 41259 - Comment field added to all test elements</li>
 <li>Add standard deviation to Summary Report</li>
 <li>Bug 41873 - Add name to AssertionResult and display AssertionResult in ViewResultsFullVisualizer</li>
 <li>Bug 41876 - Add more options to control what the HTTP Proxy generates</li>
 <li>Bug 39693 - View Result Table use icon instead of check box</li>
 <li>Bug 41880 - Add content-type filtering to HTTP Proxy Server</li>
 <li>Bug 40825 - Add JDBC prepared statement support</li>
 <li>Bug 27780 - (patch 19936) create multipart/form-data HTTP request without uploading file</li>
 <li>Bug 42098 - Use specified encoding for parameter values in HTTP GET</li>
 <li>Bug 42088 - Add XPath Assertion for booleans</li>
 <li>Bug 42158 - Improve support for multipart/form-data requests in HTTP Proxy server</li>
 </ul>
 
 <h4>Non-functional improvements:</h4>
 <ul>
 <li>Functor calls can now be unit tested</li>
 <li>Replace com.sun.net classes with javax.net</li>
 <li>Extract external jar definitions into build.properties file</li>
 <li>Use specific jar names in build classpaths so errors are detected sooner</li>
 <li>Tidied up ORO calls; now only one cache, size given by oro.patterncache.size, default 1000</li>
 </ul>
 
 <h4>External jar updates:</h4>
 <ul>
 <li>Htmlparser 2.0-20060923</li>
 <li>xstream 1.2.1/xpp3_min-1.1.3.4.O</li>
 <li>Batik 1.6</li>
 <li>BSF 2.4.0</li>
 <li>commons-collections 3.2</li>
 <li>commons-httpclient-3.1-rc1</li>
 <li>commons-jexl 1.1</li>
 <li>commons-lang-2.3 (added)</li>
 <li>velocity 1.5</li>
 <li></li>
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
 <li>Bug 40772 - correctly parse missing fields in CSV log files</li>
 <li>Bug 40773 - XML log file timestamp not parsed correctly</li>
 <li>Bug 41029 - JMeter -t fails to close input JMX file</li>
 <li>Bug 40954 - Statistical mode in distributed testing shows wrong results</li>
 <li>Fix ClassCast Exception when using sampler that returns null, e..g TestAction</li>
 <li>Bug 41277 - add Latency and Encoding to CSV output</li>
 <li>Bug 41414 - Mac OS X may add extra item to -jar classpath</li>
 <li>Fix NPE when saving thread counts in remote testing</li>
 <li>Bug 34261 - NPE in HtmlParser (allow for missing attributes)</li>
 <li>Bug 40100 - check FileServer type before calling close</li>
 <li>Bug 39887 - jmeter.util.SSLManager: Couldn't load keystore error message</li>
 <li>Bug 41543 - exception when webserver returns "500 Internal Server Error" and content-length is 0</li>
 <li>Bug 41416 - don't use chunked input for text-box input in SOAP-RPC sampler</li>
 <li>Bug 39827 - SOAP Sampler content length for files</li>
 <li>Fix Class cast exception in Clear.java</li>
 <li>Bug 40383 - don't set content-type if already set</li>
 <li>Mailer Visualiser test button now works if test plan has not yet been saved</li>
 <li>Bug 36959 - Shortcuts "ctrl c" and "ctrl v" don't work on the tree elements</li>
 <li>Bug 40696 - retrieve embedded resources from STYLE URL() attributes</li>
 <li>Bug 41568 - Problem when running tests remotely when using a 'Counter'</li>
 <li>Fixed various classes that assumed timestamps were always end time stamps:
 <ul>
 <li>SamplingStatCalculator</li>
 <li>JTLData</li>
 <li>RunningSample</li>
 </ul>
 </li>
 <li>Bug 40325 - allow specification of proxyuser and proxypassword for WebServiceSampler</li>
 <li>Change HttpClient proxy definition to use NTCredentials; added http.proxyDomain property for this</li>
 <li>Bug 40371 - response assertion "pattern to test" scrollbar problem</li>
 <li>Bug 40589 - Unescape XML entities in embedded URLs</li>
 <li>Bug 41902 - NPE in HTTPSampler when responseCode = -1</li>
 <li>Bug 41903 - ViewResultsFullVisualizer : status column looks bad when you do copy and paste</li>
 <li>Bug 41837 - Parameter value corruption in proxy</li>
 <li>Bug 41905 - Can't cut/paste/select Header Manager fields in Java 1.6</li>
 <li>Bug 41928 - Make all request headers sent by HTTP Request sampler appear in sample result</li>
 <li>Bug 41944 - Subresults not handled recursively by ResultSaver</li>
 <li>Bug 42022 - HTTPSampler does not allow multiple headers of same name</li>
 <li>Bug 42019 - Content type not stored in redirected HTTP request with subresults</li>
 <li>Bug 42057 - connection can be null if method is null</li>
 <li>Bug 41518 - JMeter changes the HTTP header Content Type for POST request</li>
 <li>Bug 42156 - HTTPRequest HTTPClient incorrectly urlencodes parameter value in POST</li>
+<li>Bug 42184 - Number of bytes for subsamples not added to sample when sub samples are added</li>
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
