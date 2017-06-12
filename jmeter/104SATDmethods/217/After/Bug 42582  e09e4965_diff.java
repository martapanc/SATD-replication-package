diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index 79f0d11c4..8e42eaaeb 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,1023 +1,1128 @@
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
+import java.lang.Character;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
+import java.util.regex.Matcher;
+import java.util.regex.Pattern;
 
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
 public class ViewResultsFullVisualizer extends AbstractVisualizer 
         implements ActionListener, TreeSelectionListener, Clearable 
     {
 
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	// N.B. these are not multi-threaded, so don't make it static
 	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // ISO format $NON-NLS-1$
 	
 	private static final String NL = "\n"; // $NON-NLS-1$
 
 	private static final String XML_PFX = "<?xml "; // $NON-NLS-1$
 
 	public final static Color SERVER_ERROR_COLOR = Color.red;
 
 	public final static Color CLIENT_ERROR_COLOR = Color.blue;
 
 	public final static Color REDIRECT_COLOR = Color.green;
 
 	private static final String DOWNLOAD_LABEL = "Download embedded resources";
 
 	private static final String HTML_BUTTON_LABEL = "Render HTML";
 
+	private static final String JSON_BUTTON_LABEL = "Render JSON";
+
 	private static final String XML_BUTTON_LABEL = "Render XML";
 
 	private static final String TEXT_BUTTON_LABEL = "Show Text";
 
 	private static final String TEXT_HTML = "text/html"; // $NON-NLS-1$
 
 	private static final String HTML_COMMAND = "html"; // $NON-NLS-1$
 
+	private static final String JSON_COMMAND = "json"; // $NON-NLS-1$
+
 	private static final String XML_COMMAND = "xml"; // $NON-NLS-1$
 
 	private static final String TEXT_COMMAND = "text"; // $NON-NLS-1$
 
 	private static final String STYLE_SERVER_ERROR = "ServerError"; // $NON-NLS-1$
 
 	private static final String STYLE_CLIENT_ERROR = "ClientError"; // $NON-NLS-1$
 
 	private static final String STYLE_REDIRECT = "Redirect"; // $NON-NLS-1$
 
 	private boolean textMode = true;
 
+	private static final String ESC_CHAR_REGEX = "\\\\[\"\\\\/bfnrt]|\\\\u[0-9A-Fa-f]{4}"; // $NON-NLS-1$
+
+	private static final String NORMAL_CHARACTER_REGEX = "[^\"\\\\]";  // $NON-NLS-1$
+
+	private static final String STRING_REGEX = "\"(" + ESC_CHAR_REGEX + "|" + NORMAL_CHARACTER_REGEX + ")*\""; // $NON-NLS-1$
+
+	// This 'other value' regex is deliberately weak, even accepting an empty string, to be useful when reporting malformed data.
+	private static final String OTHER_VALUE_REGEX = "[^\\{\\[\\]\\}\\,]*"; // $NON-NLS-1$
+
+	private static final String VALUE_OR_PAIR_REGEX = "((" + STRING_REGEX + "\\s*:)?\\s*(" + STRING_REGEX + "|" + OTHER_VALUE_REGEX + ")\\s*,?\\s*)"; // $NON-NLS-1$
+
+	private static final Pattern VALUE_OR_PAIR_PATTERN = Pattern.compile(VALUE_OR_PAIR_REGEX);
+
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
 
+	private JRadioButton jsonButton;
+
 	private JRadioButton xmlButton;
 
 	private JCheckBox downloadAll;
 
 	private JTree jTree;
 
 	private JTabbedPane rightSide;
 	
 	private static final ImageIcon imageSuccess = JMeterUtils.getImage(
 	        JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
 	        		"icon_success_sml.gif")); //$NON-NLS-1$
 
 	private static final ImageIcon imageFailure = JMeterUtils.getImage(
 			JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
 					"icon_warning_sml.gif")); //$NON-NLS-1$
 	
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
 	public void clearData() {
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
 
 						StringBuffer statsBuff = new StringBuffer(200);
 						statsBuff.append("Thread Name: ").append(res.getThreadName()).append(NL);
 						String startTime = dateFormat.format(new Date(res.getStartTime()));
 						statsBuff.append("Sample Start: ").append(startTime).append(NL);
 						statsBuff.append("Load time: ").append(res.getTime()).append(NL);
 						statsBuff.append("Size in bytes: ").append(res.getBytes()).append(NL);
 						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
 						statsBuff = new StringBuffer(); //reset for reuse
 						
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
 
 						statsBuff.append("Response code: ").append(responseCode).append(NL);
 						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), style);
 						statsBuff = new StringBuffer(100); //reset for reuse
 
 						// response message label
 						String responseMsgStr = res.getResponseMessage();
 
 						log.debug("valueChanged1 : response message - " + responseMsgStr);
 						statsBuff.append("Response message: ").append(responseMsgStr).append(NL);
 
 						statsBuff.append(NL).append("Response headers:").append(NL);
 						statsBuff.append(res.getResponseHeaders()).append(NL);
 						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
 						statsBuff = null; // Done
 
 						// get the text response and image icon
 						// to determine which is NOT null
 						if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null) is OK
 						{
 							String response = getResponseAsString(res);
 							if (command.equals(TEXT_COMMAND)) {
 								showTextResponse(response);
 							} else if (command.equals(HTML_COMMAND)) {
 								showRenderedResponse(response, res);
+							} else if (command.equals(JSON_COMMAND)) {
+								showRenderJSONResponse(response);
 							} else if (command.equals(XML_COMMAND)) {
 								showRenderXMLResponse(response);
 							}
 						} else {
 							byte[] responseBytes = res.getResponseData();
 							if (responseBytes != null) {
 								showImage(new ImageIcon(responseBytes)); //TODO implement other non-text types
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
 						StringBuffer statsBuff = new StringBuffer(100);
 						statsBuff.append("Assertion error: ").append(res.isError()).append(NL);
 						statsBuff.append("Assertion failure: ").append(res.isFailure()).append(NL);
 						statsBuff.append("Assertion failure message : ").append(res.getFailureMessage()).append(NL);
 						statsDoc.insertString(statsDoc.getLength(), statsBuff.toString(), null);
 						statsBuff = null;
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
+		jsonButton.setEnabled(false);
 		xmlButton.setEnabled(false);
 	}
 
 	protected void showTextResponse(String response) {
 		results.setContentType("text/plain"); // $NON-NLS-1$
 		results.setText(response == null ? "" : response); // $NON-NLS-1$
 		results.setCaretPosition(0);
 		resultsScrollPane.setViewportView(results);
 
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
+		jsonButton.setEnabled(true);
+		xmlButton.setEnabled(true);
+	}
+
+	// It might be useful also to make this available in the 'Request' tab, for
+	// when posting JSON.
+	private static String prettyJSON(String json) {
+		StringBuffer pretty = new StringBuffer(json.length() * 2); // Educated guess
+
+		final String tab = ":   "; // $NON-NLS-1$
+		StringBuffer index = new StringBuffer();
+		String nl = ""; // $NON-NLS-1$
+
+		Matcher valueOrPair = VALUE_OR_PAIR_PATTERN.matcher(json);
+
+		boolean misparse = false;
+
+		for (int i = 0; i < json.length(); ) {
+			final char currentChar = json.charAt(i);
+			if ((currentChar == '{') || (currentChar == '[')) {
+				pretty.append(nl).append(index).append(currentChar);
+				i++;
+				index.append(tab);
+				misparse = false;
+			}
+			else if ((currentChar == '}') || (currentChar == ']')) {
+				if (index.length() > 0) {
+					index.delete(0, tab.length());
+				}
+				pretty.append(nl).append(index).append(currentChar);
+				i++;
+				int j = i;
+				while ((j < json.length()) && Character.isWhitespace(json.charAt(j))) {
+					j++;
+				}
+				if ((j < json.length()) && (json.charAt(j) == ',')) {
+					pretty.append(","); // $NON-NLS-1$
+					i=j+1;
+				}
+				misparse = false;
+			}
+			else if (valueOrPair.find(i) && valueOrPair.group().length() > 0) {
+				pretty.append(nl).append(index).append(valueOrPair.group());
+				i=valueOrPair.end();
+				misparse = false;
+			}
+			else {
+				if (!misparse) {
+					pretty.append(nl).append("- Parse failed from:");
+				}
+				pretty.append(currentChar);
+				i++;
+				misparse = true;
+			}
+			nl = "\n"; // $NON-NLS-1$
+		}
+		return pretty.toString();
+	}
+	
+	private void showRenderJSONResponse(String response) {
+		results.setContentType("text/plain"); // $NON-NLS-1$
+		results.setText(response == null ? "" : prettyJSON(response));
+		results.setCaretPosition(0);
+		resultsScrollPane.setViewportView(results);
+
+		textButton.setEnabled(true);
+		htmlButton.setEnabled(true);
+		jsonButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	private static final SAXErrorHandler saxErrorHandler = new SAXErrorHandler();
 
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
+		jsonButton.setEnabled(true);
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
-				&& (command.equals(TEXT_COMMAND) || command.equals(HTML_COMMAND) || command.equals(XML_COMMAND))) {
+				&& (command.equals(TEXT_COMMAND) || command.equals(HTML_COMMAND)
+ 				|| command.equals(JSON_COMMAND) || command.equals(XML_COMMAND))) {
 
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
+			} else if (command.equals(JSON_COMMAND)) {
+				showRenderJSONResponse(response);
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
+		jsonButton.setEnabled(true);
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
 
+		jsonButton = new JRadioButton(JSON_BUTTON_LABEL);
+		jsonButton.setActionCommand(JSON_COMMAND);
+		jsonButton.addActionListener(this);
+		jsonButton.setSelected(!textMode);
+		group.add(jsonButton);
+
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
+		pane.add(jsonButton);
 		pane.add(downloadAll);
 		return pane;
 	}
 
 	/**
 	 * Initialize this visualizer
 	 */
 	private void init() {
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
 		rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_sampler")); //$NON-NLS-1$
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
 		rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_assertion")); //$NON-NLS-1$
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 426af109e..9d8677542 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,909 +1,910 @@
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
 
 <h3>Version 2.3</h3>
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
 <li>FTP Sampler supports Ascii/Binary mode and upload</li>
 <li>Transaction Controller now generates Sample with subresults</li>
 <li>HTTPS session contexts are now per-thread, rather than shared. This gives better emulation of multiple users</li>
 <li>BeanShell elements now support ThreadListener and TestListener interfaces</li>
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
 <p>The Transaction Controller no longer appears as a separate sample; it now includes its nested samples as subsamples</p>
 <p>
 Control-Z no longer used for Remote Start All; replaced by Control+Shift+R
 </p>
 <p>
 By default, SSL session contexts are now created per-thread, rather than being shared.
 The original behaviour can be enabled by setting the JMeter property:
 <pre>
 https.sessioncontext.shared=true
 </pre>
 </p>
 <h4>Incompatible changes (development):</h4>
 <p>
 <b>N.B.</b>The clear() method was defined in the following interfaces: Clearable, JMeterGUIComponent and TestElement.
 The methods serve different purposes, so two of them were renamed: 
 the Clearable method is now clearData() and the JMeterGUIComponent method is now clearGui().
 3rd party add-ons may need to be rebuilt.
 </p>
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
 <li>Bug 40369 - LDAP: Stable search results in sampler</li>
 <li>Bug 36755 - Save XML test files with UTF-8 encoding</li>
 <li>Bug 42223 - Add ability to upload files via FTP</li>
 <li>Extend JDBC Sampler: Commit, Rollback, AutoCommit</li>
 <li>Bug 42247 - improve HCI</li>
 <li>Support "file" protocol in HTTP Samplers</li>
 <li>Allow user to cancel out of Close dialogue</li>
 <li>Use ISO date-time format for Tree View Listener (previously the year was not shown)</li>
 <li>Improve loading of CSV files: if possible, use header to determine format; guess timestamp format if not milliseconds</li>
 <li>Bug 41913 - TransactionController now creates samples as sub-samples of the transaction</li>
 <li>Bug 42506 - JMeter threads all use the same SSL session</li>
 <li>BeanShell elements now support ThreadListener and TestListener interfaces</li>
+<li>Bug 42582 - JSON pretty printing in Tree View Listener</li>
 </ul>
 
 <h4>Non-functional improvements:</h4>
 <ul>
 <li>Functor calls can now be unit tested</li>
 <li>Replace com.sun.net classes with javax.net</li>
 <li>Extract external jar definitions into build.properties file</li>
 <li>Use specific jar names in build classpaths so errors are detected sooner</li>
 <li>Tidied up ORO calls; now only one cache, size given by oro.patterncache.size, default 1000</li>
 <li>Bug 42326 - Order of elements in .jmx files changes</li>
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
 <li>Bug 42184 - Number of bytes for subsamples not added to sample when sub samples are added</li>
 <li>Bug 42185 - If a HTTP Sampler follows a redirect, and is set up to download images, then images are downloaded multiple times</li>
 <li>Bug 39808 - Invalid redirect causes incorrect sample time</li>
 <li>Bug 42267 - Concurrent GUI update failure in Proxy Recording</li>
 <li>Bug 30120 - Name of simple controller is resetted if a new simple controller is added as child</li>
 <li>Bug 41078 - merge results in name change of test plan</li>
 <li>Bug 40077 - Creating new Elements copies values from Existing elements</li>
 <li>Bug 42325 - Implement the "clear" method for the LogicControllers</li>
 <li>Bug 25441 - TestPlan changes sometimes detected incorrectly (isDirty)</li>
 <li>Bug 39734 - Listeners shared after copy/paste operation</li>
 <li>Bug 40851 - Loop controller with 0 iterations, stops evaluating the iterations field</li>
 <li>Bug 24684 - remote startup problems if spaces in the path of the jmeter</li>
 <li>Use Listener configuration when loading CSV data files</li>
 <li>Function methods setParameters() need to be synchronized</li>
 <li>Fix CLI long optional argument to require "=" (as for short options)</li>
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
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index 3562cac19..809706880 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -666,2015 +666,2015 @@ StandardGenerator and TCLogParser.
 </component>
 
 <component name="BeanShell Sampler" index="&sect-num;.1.10"  width="601" height="244" screenshot="beanshellsampler.png">
 	<description><p>This sampler allows you to write a sampler using the BeanShell scripting language.		
 </p><p>
 		<b>Please note that the BeanShell jar file is not included with JMeter; it needs to be separately downloaded.
 		<br></br>
         For full details on using BeanShell, please see the BeanShell web-site at http://www.beanshell.org/.</b>
 </p>
 <p>
 The test element supports the ThreadListener and TestListener methods.
 These should be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 	</description>
 <properties>
 	<property name="Name" required="no">Descriptive name for this controller that is shown in the tree.</property>
 	<property name="Parameters" required="no">List of parameters to be passed to the script file or the script.</property>
 	<property name="Script File" required="(yes)">Name of a file to be used as a BeanShell script</property>
 	<property name="Script" required="(yes)">Script to be passed to BeanShell</property>
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
 			<p>The full list of variables that is set up is as follows:</p>
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
 			<li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3");</li>
 		</ul>
 		<p>When the script completes, control is returned to the Sampler, and it copies the contents
 			of the following script variables into the corresponding variables in the SampleResult:</p>
 			<ul>
 			<li>ResponseCode - for example 200</li>
 			<li>ResponseMessage - for example "OK"</li>
 			<li>IsSuccess - true/false</li>
 			</ul>
 			<p>The Sampler ResponseData is set from the return value of the script.
 			Since version 2.1.2, if the script returns null, it can set the response directly, by using the method 
 			SampleResponse.setResponseData(data), where data is either a String or a byte array.
 			The data type defaults to "text", but can be set to binary by using the method
 			SampleResponse.setDataType(SampleResponse.BINARY).
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
 
 
 <component name="BSF Sampler" index="&sect-num;.1.11"  width="396" height="217" screenshot="bsfsampler.png">
 <note>This is Alpha code</note>
 	<description><p>This sampler allows you to write a sampler using a BSF scripting language.<br></br>
 		
 		</p>
 	</description>
 <properties>
 	<property name="Name" required="no">Descriptive name for this controller that is shown in the tree.</property>
 	<property name="Parameters" required="no">List of parameters to be passed to the script file or the script.</property>
 	<property name="Script File" required="no">Name of a file to be used as a BeanShell script</property>
 	<property name="Script" required="no">Script to be passed to BeanShell</property>
 </properties>
 TBC
 </component>
 
 <component name="TCP Sampler" index="&sect-num;.1.12"  width="631" height="305" screenshot="tcpsampler.png">
 <note>ALPHA CODE</note>
 	<description>
 		<p>
 		The TCP Sampler opens a TCP/IP connection to the specified server.
 		It then sends the text, and waits for a response.
 		<br></br>
 		Connections are shared between Samplers in the same thread, provided that the exact
 		same host name string and port are used.
 		To force a different socket to be used, change the hostname by changing the case
 		of one of the letters, e.g. www.apache.org and wWw.apache.org will use different
 		sockets.
 		<br></br>
 		If an error is detected, the socket is closed. 
 		Another socket will be reopened on the next sample.
 		<br></br>
 		The following properties can be used to control its operation:
 		</p>
 		<ul>
 			<li>tcp.status.prefix - text that precedes a status number</li>
 			<li>tcp.status.suffix - text that follows a status number</li>
 			<li>tcp.status.properties - name of property file to convert status codes to messages</li>
 			<li>tcp.handler - Name of TCP Handler class (default TCPClientImpl)</li>
 			<li>tcp.eolByte - decimal value. Defines the end of line byte value; used to determine when a response has been received</li>
 		</ul>
 		The class that handles the connection is defined by the property tcp.handler. 
 		If not found, the class is then searched for in the package org.apache.jmeter.protocol.tcp.sampler.
 		<p>
 		Users can provide their own implementation to replace the supplied class TCPClientImpl.
 		The class must extend org.apache.jmeter.protocol.tcp.sampler.TCPClient.
 		</p>
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
 		<br></br>
 <note>The login name/password are not used by the supplied TCP implementation.</note>
 		<br></br>
 		Sockets are disconnected at the end of a test run.
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="ServerName or IP" required="Yes">Name or IP of TCP server</property>
   <property name="Port Number" required="Yes">Port to be used</property>
   <property name="Timeout (milliseconds)" required="No">Timeout for replies</property>
   <property name="Set Nodelay" required="No">Should the nodelay property be set?</property>
   <property name="Text to Send" required="Yes">Text to be sent</property>
   <property name="Login User" required="No">User Name</property>
   <property name="Password" required="No">Password</property>
 </properties>
 </component>
 
 <component name="JMS Publisher" index="&sect-num;.1.13"  width="438" height="750" screenshot="jmspublisher.png">
 <note>ALPHA CODE</note>
 	<description>
 		<p>
 		JMS Publisher will publish messages to a given pub/sub topic. For those not
 		familiar with JMS, it is the J2EE specification for messaging. There are
 		numerous JMS servers on the market and several open source options.
 		</p>
 		<br></br>
 <note>JMeter does not include the JMS jar; this must be downloaded and put in the lib directory</note>
 	</description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="use JNDI properties file" required="Yes">use jndi.properties to create topic</property>
   <property name="JNDI Initial Context Factory" required="No">Name of the context factory</property>
   <property name="Provider URL" required="No">The URL for the jms provider</property>
   <property name="Topic" required="Yes">the message topic</property>
   <property name="Authentication" required="Yes">Authentication requirement for the JMS provider</property>
   <property name="User" required="No">User Name</property>
   <property name="Password" required="No">Password</property>
   <property name="Number of samples to aggregate" required="Yes">number of samples to aggregate</property>
   <property name="configuration" required="Yes">setting for the message</property>
   <property name="Message type" required="Yes">text or object message</property>
 </properties>
 </component>
 
 <component name="JMS Subscriber" index="&sect-num;.1.14"  width="497" height="434" screenshot="jmssubscriber.png">
 <note>ALPHA CODE</note>
 	<description>
 		<p>
 		JMS Publisher will subscribe to messages in a given pub/sub topic. For those not
 		familiar with JMS, it is the J2EE specification for messaging. There are
 		numerous JMS servers on the market and several open source options.
 		</p>
 		<br></br>
 <note>JMeter does not include the JMS jar; this must be downloaded and put in the lib directory</note>
 	</description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="use JNDI properties file" required="Yes">use jndi.properties to create topic</property>
   <property name="JNDI Initial Context Factory" required="No">Name of the context factory</property>
   <property name="Provider URL" required="No">The URL for the jms provider</property>
   <property name="Topic" required="Yes">the message topic</property>
   <property name="Authentication" required="Yes">Authentication requirement for the JMS provider</property>
   <property name="User" required="No">User Name</property>
   <property name="Password" required="No">Password</property>
   <property name="Number of samples to aggregate" required="Yes">number of samples to aggregate</property>
   <property name="Read response" required="Yes">should the sampler read the response</property>
   <property name="Client" required="Yes">Which client to use</property>
 </properties>
 </component>
 
 <component name="JMS Point-to-Point" index="&sect-num;.1.15"  width="568" height="730" screenshot="jms/JMS_Point-to-Point.png">
 <note>ALPHA CODE</note>
 	<description>
 		<p>
 		This sampler sends and optionally receives JMS Messages through point-to-point connections (queues).
         It is different from pub/sub messages and is generally used for handling transactions.
 		</p>
 		<br></br>
 <note>JMeter does not include the JMS jar; this must be downloaded and put in the lib directory</note>
 	</description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="QueueConnection Factory" required="Yes">
     The JNDI name of the queue connection factory to use for connecting to the messaging system.
   </property>
   <property name="JNDI Name Request queue" required="Yes">
     This is the JNDI name of the queue to which the messages are sent.
   </property>
   <property name="JNDI Name Reply queue" required="No">
     The JNDI name of the receiving queue. If a value is provided here and the communication style is Request Response
     this queue will be monitored for responses to the requests sent.
   </property>
   <property name="Communication style" required="Yes">
     The Communication style can be Request Only (also known as Fire and Forget) or Request Reply.
     Request Only will only sent messages and will not monitor replies. As such it can be used to put load on a system.
     Request Reply will sent messages and monitor the replies it receives. Behaviour is depended on the value of the JNDI Name Reply Queue.
     If JNDI Name Reply Queue has a value, this queue is used to monitor the results. Matching of request and reply is done with
     the message id of the request with the correlation id of the reply. If the JNDI Name Reply Queue is empty, then
     temporary queues will be used for the communication between the requestor and the server. This is very different from
     the fixed reply queue. With temporary queues the diffent threads will block until the reply message has been received.
   </property>
   <property name="Timeout" required="Yes">
       The timeout in milliseconds for the reply-messages. If a reply has not been received within the specified
       time, the specific testcase failes and the specific reply message received after the timeout is discarded.
   </property>
   <property name="Content" required="No">
       The content of the message.
   </property>
   <property name="JMS Properties" required="No">
       The JMS Properties are properties specific for the underlying messaging system.
       For example: for WebSphere 5.1 web services you will need to set the JMS Property targetService to test
       webservices through JMS.
   </property>
   <property name="Initial Context Factory" required="No">
     The Initial Context Factory is the factory to be used to look up the JMS Resources.
   </property>
   <property name="JNDI properties" required="No">
      The JNDI Properties are the specific properties for the underlying JNDI implementation.
   </property>
   <property name="Provider URL" required="No">
     The URL for the jms provider.
   </property>
 </properties>
 </component>
 
 
 
 <component name="JUnit Request" index="&sect-num;.1.16"  width="522" height="405" screenshot="junit_sampler.png">
 <description>
 The current implementation supports standard Junit convention and extensions. It also
 includes extensions like oneTimeSetUp and oneTimeTearDown. The sampler works like the
 JavaSampler with some differences.
 <br></br>1. rather than use Jmeter's test interface, it scans the jar files for classes extending junit's TestCase class. That includes any class or subclass.
 <br></br>2. Junit test jar files should be placed in jmeter/lib/junit instead of /lib directory.
 <br></br>3. Junit sampler does not use name/value pairs for configuration like the JavaSampler. The sampler assumes setUp and tearDown will configure the test correctly.
 <br></br>4. The sampler measures the elapsed time only for the test method and does not include setUp and tearDown.
 <br></br>5. Each time the test method is called, Jmeter will pass the result to the listeners.
 <br></br>6. Support for oneTimeSetUp and oneTimeTearDown is done as a method. Since Jmeter is multi-threaded, we cannot call oneTimeSetUp/oneTimeTearDown the same way Maven does it.
 <br></br>7. The sampler reports unexpected exceptions as errors.
 There are some important differences between standard JUnit test runners and JMeter's
 implementation. Rather than make a new instance of the class for each test, JMeter
 creates 1 instance per sampler and reuses it.<br></br>
 The current implementation of the sampler will try to create an instance using the string constructor first. If the test class does not declare a string constructor, the sampler will look for an empty constructor. Example below:&lt;br>
 &lt;br>
 Empty Constructor:&lt;br>
 public class myTestCase {&lt;br>
   public myTestCase() {}&lt;br>
 }&lt;br>
 &lt;br>
 String Constructor:&lt;br>
 public class myTestCase {&lt;br>
   public myTestCase(String text) {&lt;br>
     super(text);&lt;br>
   }&lt;br>
 }&lt;br>
 By default, Jmeter will provide some default values for the success/failure code and message. Users should define a set of unique success and failure codes and use them uniformly across all tests.&lt;br>
 General Guidelines<br></br>
 If you use setUp and tearDown, make sure the methods are declared public. If you do not, the test may not run properly.
 <br></br>
 Here are some general guidelines for writing Junit tests so they work well with Jmeter. Since Jmeter runs multi-threaded, it is important to keep certain things in mind.&lt;br>
 &lt;br>
 1. Write the setUp and tearDown methods so they are thread safe. This generally means avoid using static memebers.&lt;br>
 2. Make the test methods discrete units of work and not long sequences of actions. By keeping the test method to a descrete operation, it makes it easier to combine test methods to create new test plans.&lt;br>
 3. Avoid making test methods depend on each other. Since Jmeter allows arbitrary sequencing of test methods, the runtime behavior is different than the default Junit behavior.&lt;br>
 4. If a test method is configurable, be careful about where the properties are stored. Reading the properties from the Jar file is recommended.&lt;br>
 5. Each sampler creates an instance of the test class, so write your test so the setup happens in oneTimeSetUp and oneTimeTearDown.
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Package filter" required="">Comma separated list of packages to show. Example, org.apache.jmeter,junit.framework.</property>
   <property name="Class name" required="">Fully qualified name of the JUnit test class.</property>
   <property name="Constructor string" required="">String pass to the string constructor. If a string is set, the sampler will use the
    string constructor instead of the empty constructor.</property>
   <property name="Test method" required="">The method to test.</property>
   <property name="Success message" required="">A descriptive message indicating what success means.</property>
   <property name="Success code" required="">An unique code indicating the test was successful.</property>
   <property name="Failure message" required="">A descriptive message indicating what failure means.</property>
   <property name="Failure code" required="">An unique code indicating the test failed.</property>
   <property name="Error message" required="">A description for errors.</property>
   <property name="Error code" required="">Some code for errors. Does not need to be unique.</property>
   <property name="Do not call setUp and tearDown" required="">Set the sampler not to call setUp and tearDown.
    By default, setUp and tearDown should be called. Not calling those methods could affect the test and make it inaccurate.
     This option should only be used with calling oneTimeSetUp and oneTimeTearDown. If the selected method is oneTimeSetUp or oneTimeTearDown,
      this option should be checked.</property>
 </properties>
 </component>
 
 <component name="Mail Reader Sampler"  index="&sect-num;.1.17"  width="344" height="318" screenshot="mailreader_sampler.png">
 <description>TBA</description>
 </component>
 
 <component name="Test Action" index="&sect-num;.1.18"  width="304" height="232" screenshot="test_action.png">
 <description>
 The Test Action sampler is a sampler that is intended for use in a conditional controller.
 Rather than generate a sample, the test element eithers pauses - or stops the selected target.
 <p>This sampler can also be useful in conjunction with the Transaction Controller, as it allows
 pauses to be included without needing to generate a sample. 
 For variable delays, set the pause time to zero, and add a Timer as a child.</p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Target" required="Yes">Current Thread / All Threads (ignored for Pause)</property>
   <property name="Action" required="Yes">Pause / Stop</property>
   <property name="Duration" required="Yes">How long to pause for (milliseconds)</property>
 </properties>
 </component><a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.2 Logic Controllers" anchor="logic_controllers">
 <description>
 	<br>Logic Controllers determine the order in which Samplers are processed.</br>
 </description>
 
 <component name="Simple Controller" index="&sect-num;.2.1" anchor="simple_controller"  width="390" height="62" screenshot="logic-controller/simple-controller.gif">
 <description>
 <p>The Simple Logic Controller lets  you organize your Samplers and other
 Logic Controllers.  Unlike other Logic Controllers, this controller provides no functionality beyond that of a
 storage device.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 <example title="Using the Simple Controller" anchor="simple_controller_example">
 <p><a href="../demos/SimpleTestPlan.jmx">Download</a> this example (see Figure 6).
 In this example, we created a Test Plan that sends two Ant HTTP requests and two
 Log4J HTTP requests.  We grouped the Ant and Log4J requests by placing them inside
 Simple Logic Controllers.  Remember, the Simple Logic Controller has no effect on how JMeter
 processes the controller(s) you add to it.  So, in this example, JMeter sends the requests in the
 following order: Ant Home Page, Ant News Page, Log4J Home Page, Log4J History Page.
 Note, the File Reporter
 is configured to store the results in a file named "simple-test.dat" in the current directory.</p>
 <figure image="logic-controller/simple-example.gif">Figure 6 Simple Controller Example</figure>
 
 </example>
 </component>
 
 <component name="Loop Controller" index="&sect-num;.2.2" anchor="loop"  width="397" height="111" screenshot="logic-controller/loop-controller.gif">
 <description><p>If you add Generative or Logic Controllers to a Loop Controller, JMeter will
 loop through them a certain number of times, in addition to the loop value you
 specified for the Thread Group.  For example, if you add one HTTP Request to a
 Loop Controller with a loop count of two, and configure the Thread Group loop
 count to three, JMeter will send a total of 2 * 3 = 6 HTTP Requests.
 </p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Loop Count" required="Yes, unless &quot;Forever&quot; is checked">
                 The number of times the subelements of this controller will be iterated each time
                 through a test run.
                 <p><b>Special Case:</b> The Loop Controller embedded in the <a href="test_plan.html#thread_group">Thread Group</a>
                 element behaves slightly differently.  Unless set to forever, it stops the test after
                 the given number of iterations have been done.</p></property>
 </properties>
 
 <example title="Looping Example" anchor="loop_example">
 
 <p><a href="../demos/LoopTestPlan.jmx">Download</a> this example (see Figure 4).
 In this example, we created a Test Plan that sends a particular HTTP Request
 only once and sends another HTTP Request five times.</p>
 
 <figure image="logic-controller/loop-example.gif">Figure 4 - Loop Controller Example</figure>
 
 <p>We configured the Thread Group for a single thread and a loop count value of
 one. Instead of letting the Thread Group control the looping, we used a Loop
 Controller.  You can see that we added one HTTP Request to the Thread Group and
 another HTTP Request to a Loop Controller.  We configured the Loop Controller
 with a loop count value of five.</p>
 <p>JMeter will send the requests in the following order: Home Page, News Page,
 News Page, News Page, News Page, and News Page. Note, the File Reporter
 is configured to store the results in a file named "loop-test.dat" in the current directory.</p>
 
 </example>
 
 </component>
 
 <component name="Once Only Controller" index="&sect-num;.2.3" anchor="once_only_controller"  width="390" height="62" screenshot="logic-controller/once-only-controller.gif">
 <description>
 <p>The Once Only Logic Controller tells JMeter to process the controller(s) inside it only once, and pass over any requests under it
 during further iterations through the test plan.</p>
 
 <p>The Once Only Controller will now execute always during the first iteration of any looping parent controller.  Thus, if the Once Only Controller is placed under a Loop Controller specified to loop 5 times, then the Once Only Controller will execute only on the first iteration through the Loop Controller (ie, every 5 times).  Note this means the Once Only Controller will still behave as previously expected if put under a Thread Group (runs only once per test), but now the user has more flexibility in the use of the Once Only Controller.</p>
 
 <p>For testing that requires a login, consider placing the login request in this controller since each thread only needs
 to login once to establish a session.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 <example title="Once Only Example" anchor="once_only_example">
 <p><a href="../demos/OnceOnlyTestPlan.jmx">Download</a> this example (see Figure 5).
 In this example, we created a Test Plan that has two threads that send HTTP request.
 Each thread sends one request to the Home Page, followed by three requests to the Bug Page.
 Although we configured the Thread Group to iterate three times, each JMeter thread only
 sends one request to the Home Page because this request lives inside a Once Only Controller.</p>
 <figure image="logic-controller/once-only-example.png">Figure 5. Once Only Controller Example</figure>
 <p>Each JMeter thread will send the requests in the following order: Home Page, Bug Page,
 Bug Page, Bug Page. Note, the File Reporter is configured to store the results in a file named "loop-test.dat" in the current directory.</p>
 
 </example>
 </component>
 
 <component name="Interleave Controller" index="&sect-num;.2.4"  width="219" height="90" screenshot="logic-controller/interleave-controller.png">
 <description><p>If you add Generative or Logic Controllers to an Interleave Controller, JMeter will alternate among each of the
 other controllers for each loop iteration. </p>
 </description>
 <properties>
         <property name="name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="ignore sub-controller blocks" required="No">If checked, the interleave controller will treat sub-controllers like single request elements and only allow one request per controller at a time.  </property>
 </properties>
 
 <!--
 For example, if you
 add three HTTP Requests to an Interleave Controller and configure the Thread
 Group to loop, here is the sequence in which JMeter sends the requests:
 </p>
 <table border="1" cellspacing="0" cellpadding="4">
 <tr valign="top"><th>Loop Iteration</th><th>Description</th></tr>
 <tr valign="top"><td>1</td><td>JMeter sends the first HTTP Request.</td></tr>
 <tr valign="top"><td>2</td><td>JMeter sends the second HTTP Request.</td></tr>
 <tr valign="top"><td>3</td><td>JMeter sends the third HTTP Request.</td></tr>
 <tr valign="top"><td>4</td><td>Because there are no more requests in controller, JMeter start over and sends the first HTTP Request.</td></tr>
 <tr valign="top"><td>5</td><td>JMeter sends the second HTTP Request.</td></tr>
 <tr valign="top"><td>(and so on)</td><td>...</td></tr>
 </table>
 -->
 <example title="Simple Interleave Example" anchor="simple_interleave_example">
 
 <p><a href="../demos/InterleaveTestPlan.jmx">Download</a> this example (see Figure 1).  In this example,
 we configured the Thread Group to have two threads and a loop count of five, for a total of ten
 requests per thread. See the table below for the sequence JMeter sends the HTTP Requests.</p>
 
 <figure image="logic-controller/interleave.png">Figure 1 - Interleave Controller Example 1</figure>
 
 <table border="1" cellspacing="0" cellpadding="4">
 <tr valign="top"><th>Loop Iteration</th><th>Each JMeter Thread Sends These HTTP Requests</th></tr>
 <tr valign="top"><td>1</td><td>News Page</td></tr>
 <tr valign="top"><td>2</td><td>Log Page</td></tr>
 <tr valign="top"><td>2</td><td>FAQ Page</td></tr>
 <tr valign="top"><td>2</td><td>Log Page</td></tr>
 <tr valign="top"><td>3</td><td>Gump Page</td></tr>
 <tr valign="top"><td>2</td><td>Log Page</td></tr>
 <tr valign="top"><td>4</td><td>Because there are no more requests in the controller,<br> </br> JMeter starts over and sends the first HTTP Request, which is the News Page.</td></tr>
 <tr valign="top"><td>2</td><td>Log Page</td></tr>
 <tr valign="top"><td>5</td><td>FAQ Page</td></tr>
 <tr valign="top"><td>2</td><td>Log Page</td></tr>
 </table>
 
 
 </example>
 
 <example title="Useful Interleave Example" anchor="useful_interleave_example">
 
 <p><a href="../demos/InterleaveTestPlan2.jmx">Download</a> another example (see Figure 2).  In this
 example, we configured the Thread Group
 to have a single thread and a loop count of eight.  Notice that the Test Plan has an outer Interleave Controller with
 two Interleave Controllers inside of it.</p>
 
 <figure image="logic-controller/interleave2.png">
         Figure 2 - Interleave Controller Example 2
 </figure>
 
 <p>The outer Interleave Controller alternates between the
 two inner ones.  Then, each inner Interleave Controller alternates between each of the HTTP Requests.  Each JMeter
 thread will send the requests in the following order: Home Page, Interleaved, Bug Page, Interleaved, CVS Page, Interleaved, and FAQ Page, Interleaved.
 Note, the File Reporter is configured to store the results in a file named "interleave-test2.dat" in the current directory.</p>
 
 <figure image="logic-controller/interleave3.png">
         Figure 3 - Interleave Controller Example 3
 </figure>
 <p>If the two interleave controllers under the main interleave controller were instead simple controllers, then the order would be: Home Page, CVS Page, Interleaved, Bug Page, FAQ Page, Interleaved.  However, if "ignore sub-controller blocks" was checked on the main interleave controller, then the order would be: Home Page, Interleaved, Bug Page, Interleaved, CVS Page, Interleaved, and FAQ Page, Interleaved.</p>
 </example>
 </component>
 
 <component name="Random Controller" index="&sect-num;.2.5"  width="238" height="84" screenshot="logic-controller/random-controller.gif">
 <description>
 <p>The Random Logic Controller acts similarly to the Interleave Controller, except that
 instead of going in order through its sub-controllers and samplers, it picks one
 at random at each pass.</p>
 <note>Interactions between multiple controllers can yield complex behavior.
 This is particularly true of the Random Controller.  Experiment before you assume
 what results any given interaction will give</note>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 </component>
 
 
 
 <component name="Random Order Controller" index="&sect-num;.2.6"  width="358" height="131" screenshot="randomordercontroller.png">
 	<description>
 		<p>The Random Order Controller is much like a Simple Controller in that it will execute each child
 		 element at most once, but the order of execution of the nodes will be random.</p>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 </component>
 
 <component name="Throughput Controller" index="&sect-num;.2.7"  width="223" height="148" screenshot="throughput_controller.png">
 <description>
 <p>The Throughput Controller allows the user to control how often it is executed.  There are two modes - percent execution and total executions.  Percent executions causes the controller to execute a certain percentage of the iterations through the test plan.  Total
 executions causes the controller to stop executing after a certain number of executions have occurred.  Like the Once Only Controller, this
 setting is reset when a parent Loop Controller restarts.
 </p>
 </description>
 <note>The Throughput Controller can yield very complex behavior when combined with other controllers - in particular with interleave or random controllers as parents (also very useful).</note>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Execution Style" required="Yes">Whether the controller will run in percent executions or total executions mode.</property>
         <property name="Throughput" required="Yes">A number.  for percent execution mode, a number from 0-100 that indicates the percentage of times the controller will execute.  "50" means the controller will execute during half the iterations throught the test plan.  for total execution mode, the number indicates the total number of times the controller will execute.</property>
         <property name="Per User" required="No">If checked, per user will cause the controller to calculate whether it should execute on a per user (per thread) basis.  if unchecked, then the calculation will be global for all users.  for example, if using total execution mode, and uncheck "per user", then the number given for throughput will be the total number of executions made.  if "per user" is checked, then the total number of executions would be the number of users times the number given for throughput.</property>
 </properties>
 
 </component>
 
 <component name="Runtime Controller" index="&sect-num;.2.8"  width="358" height="131" screenshot="runtimecontroller.png">
 	<description>
 		<p>The Runtime Controller controls how long its children are allowed to run.
 		</p>
 	</description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 	<property name="Runtime (seconds)" required="Yes">Desired runtime in seconds</property>
 </properties>
 </component>
 
 <component name="If Controller" index="&sect-num;.2.9"  width="358" height="131" screenshot="ifcontroller.png">
 	<description>
 		<p>The If Controller allows the user to control whether the test elements below it (its children) are run or not.</p>
 	</description>
 <properties>
     <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 	<property name="Condition" required="Yes"><b>Javascript</b> code that returns "true" or "false"</property>
 </properties>
 	<p>Examples:
 		<ul>
 			<li>${COUNT} &lt; 10</li>
 			<li>"${VAR}" == "abcd"</li>
 			<li>"${JMeterThread.last_sample_ok}" == "true" (check if last sample succeeded)</li>
 		</ul>
 		If there is an error interpreting the code, the condition is assumed to be false, and a message is logged in jmeter.log.
 	</p>
 </component>
 
 
 
 
 
 
 
 
 <component name="While Controller" index="&sect-num;.2.10"  width="358" height="131" screenshot="whilecontroller.png">
 	<description>
 <p>
 The While Controller runs its children until the condition is "false".
 </p>
 
 <p>Possible condition values:</p>
 <ul>
 <li>blank - exit loop when last sample in loop fails</li>
 <li>LAST - exit loop when last sample in loop fails. 
 If the last sample just before the loop failed, don't enter loop.</li>
 <li>Otherwise - exit (or don't enter) the loop when the condition is equal to the string "false"</li>
 </ul>
 <note>In contrast to the IfController, the condition is not evaluated as a JavaScript expression.
 The condition can be any variable or function that eventually evaluates to the string "false". 
 This allows the use of JavaScript, BeanShell, properties or variables as needed.
 </note>
 <br></br>
 For example:
 <ul>
 	<li>${VAR} - where VAR is set to false by some other test element</li>
 	<li>${__javaScript(${C}==10,dummy)}</li>
 	<li>${__javaScript("${VAR2}"=="abcd",dummy)}</li>
 	<li>${_P(property)} - where property is set to "false" somewhere else</li>
 </ul>
 	</description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 	<property name="Condition" required="Yes">blank, LAST, or variable/function</property>
 </properties>
 </component>
 
 <component name="Switch Controller" index="&sect-num;.2.11"  width="358" height="131" screenshot="switchcontroller.png">
 	<description>
 <p>
 The Switch Controller acts like the <complink name="Interleave Controller"/> 
 in that it runs one of the subordinate elements on each iteration, but rather than
 run them in sequence, the controller runs the element number defined by the switch value.
 </p>
 <p>If the switch value is out of range, it will run the zeroth element, 
 which therefore acts as the default.</p>
 </description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 	<property name="Switch Value" required="Yes">The number of the subordinate element to be invoked. Elements are numbered from 0.</property>
 </properties>
 </component>
 
 <component name="ForEach Controller" index="&sect-num;.2.12" anchor="loop"  width="410" height="153" screenshot="logic-controller/foreach-controller.png">
 <description><p>A ForEach controller loops through the values of a set of related variables. 
 When you add samplers (or controllers) to a ForEach controller, every sample sample (or controller)
 is executed one or more times, where during every loop the variable has a new value.
 The input should consist of several variables, each extended with an underscore and a number.
 Each such variable must have a value.
 So for example when the input variable has the name inputVar, the following variables should have been defined:
 		<ul>
 		<li>inputVar_1 = wendy</li>
 		<li>inputVar_2 = charles</li>
 		<li>inputVar_3 = peter</li>
 		<li>inputVar_4 = john</li>
 		</ul>
 		<p>Note: the "_" separator is now optional.</p>
 When the return variable is given as "returnVar", the collection of samplers and controllers under the ForEach controller will be executed 4 consecutive times,
 with the return variable having the respective above values, which can then be used in the samplers.
 </p>
 <p>
 It is especially suited for running with the regular expression post-processor. 
 This can "create" the necessary input variables out of the result data of a previous request.
 By omitting the "_" separator, the ForEach Controller can be used to loop through the groups by using
 the input variable refName_g, and can also loop through all the groups in all the matches
 by using an input variable of the form refName_${C}_g, where C is a counter variable.
 </p>
 <note>The ForEach Controller does not run any samples if inputVar_1 is null.
 This would be the case if the Regular Expression returned no matches.</note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Input variable prefix" required="Yes">Prefix for the variable names to be used as input.</property>
         <property name="Output variable" required="Yes">
                 The name of the variable which can be used in the loop for replacement in the samplers</property>
 		<property required="Yes" name="Use Separator">If not checked, the "_" separator is omitted.</property>
 </properties>
 
 <example title="ForEach Example" anchor="foreach_example">
 
 <p><a href="../demos/forEachTestPlan.jmx">Download</a> this example (see Figure 7).
 In this example, we created a Test Plan that sends a particular HTTP Request
 only once and sends another HTTP Request to every link that can be found on the page.</p>
 
 <figure image="logic-controller/foreach-example.png">Figure 7 - ForEach Controller Example</figure>
 
 <p>We configured the Thread Group for a single thread and a loop count value of
 one. You can see that we added one HTTP Request to the Thread Group and
 another HTTP Request to the ForEach Controller.</p>
 <p>After the first HTTP request, a regular expression extractor is added, which extracts all the html links
 out of the return page and puts them in the inputVar variable</p>
 <p>In the ForEach loop, a HTTP sampler is added which requests all the links that were extracted from the first returned HTML page.
 </p></example>
 <example title="ForEach Example" anchor="foreach_example2">
 <p>Here is <a href="../demos/ForEachTest2.jmx">another example</a> you can download. 
 This has two Regular Expressions and ForEach Controllers.
 The first RE matches, but the second does not match, 
 so no samples are run by the second ForEach Controller</p>
 <figure image="logic-controller/foreach-example2.png">Figure 8 - ForEach Controller Example 2</figure>
 <p>The Thread Group has a single thread and a loop count of two.
 </p><p>
 Sample 1 uses the JavaTest Sampler to return the string "a b c d".
 </p><p>The Regex Extractor uses the expression <b>(\w)\s</b> which matches a letter followed by a space,
 and returns the letter (not the space). Any matches are prefixed with the string "inputVar".
 </p><p>The ForEach Controller extracts all variables with the prefix "inputVar_", and executes its
 sample, passing the value in the variable "returnVar". In this case it will set the variable to the values "a" "b" and "c" in turn.
 </p><p>The For 1 Sampler is another Java Sampler which uses the return variable "returnVar" as part of the sample Label
 and as the sampler Data.
 </p><p>Sample 2, Regex 2 and For 2 are almost identical, except that the Regex has been changed to "(\w)\sx",
 which clearly won't match. Thus the For 2 Sampler will not be run.
 </p>
 </example>
 </component>
 
 <component name="Module Controller" index="&sect-num;.2.13"  width="409" height="255" screenshot="module_controller.png">
 <description>
 <p>The Module Controller provides a mechanism for substituting test plan fragments into the current test plan at run-time.  To use this
 module effectively, one might have a number of Controllers under the <complink name="WorkBench" />, each with a different series of
 samplers under them.  The module controller can then be used to easily switch between these multiple test cases simply by choosing
 the appropriate controller in it's drop down box.  This provides convenience for running many alternate test plans quickly and easily.
 </p>
 </description>
 <note>The Module Controller should not be used with remote testing or non-gui testing in conjunction with Workbench components since the Workbench test elements are not part of test plan .jmx files.  Any such test will fail.</note>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Module to Run" required="Yes">The module controller provides a list of all controllers loaded into the gui.  Select
         the one you want to substitute in at runtime.</property>
 </properties>
 
 </component>
 
 <component name="Include Controller" index="&sect-num;.2.14"  width="419" height="118" screenshot="includecontroller.png">
 	<description>
 <p>
 The include controller is designed to use an external jmx file. To use it, add
 samples to a simple controller, then save the simple controller as a jmx file.
 The file can then be used in a test plan.
 </p>
 <note>
 This element does not support variables/functions in the filename field.<br></br>
 However, if the property <b>includecontroller.prefix</b> is defined, 
 the contents are used to prefix the pathname.
 </note>
 </description>
 <properties>
 	<property name="Filename" required="Yes">The file to include.</property>
 </properties>
 </component>
 
 <component name="Transaction Controller" index="&sect-num;.2.15"  width="358" height="131" screenshot="transactioncontroller.png">
 	<description>
 		<p>The Transaction Controller times how long it takes for all its children to run.
 			It then adds a "sample" entry to the test output with the total elapsed time.
 			The name of the element is used to name the "sample".
 		</p>
 		<p>
 		The generated sample time includes all the times for the nested samplers, and any timers etc.
 		Depending on the clock resolution, it may be slightly longer than the sum of the individual samplers plus timers.
 		The clock might tick after the controller recorded the start time but before the first sample starts.
 		Similarly at the end.
 		</p>
 		<p>For JMeter versions after 2.2, the controlled samples no longer appear as separate samples, but as subsamples.
 		Also the Transaction Sample is only regarded as successful if all its sub-samples are successful.
 		This is similar to the way the HTTP Samplers work when retrieving embedded objects (images etc).
 		The individual samples can still be seen in the Tree View Listener,
 		but no longer appear as separate entries in other Listeners.
 		</p>
 	</description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 </properties>
 </component>
 
 <component name="Recording Controller" index="&sect-num;.2.16"  width="417" height="70" screenshot="logic-controller/recording-controller.gif">
 <description>
 <p>The Recording Controller is a place holder indicating where the proxy server should
 record samples to.  During test run, it has no effect, similar to the Simple Controller.  But during
 recording using the <complink name="HTTP Proxy Server" />, all recorded samples will by default
 be saved under the Recording Controller.</p>
 
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 </component>
 
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.3 Listeners" anchor="listeners">
 <description>
 	<br></br>
 Most of the listeners perform several roles in addition to "listening"
 to the test results.  
 They also provide means to view, save, and read saved test results.
 <p>Note that Listeners are processed at the end of the scope in which they are found.</p>
 <p>
 The saving and reading of test results is generic.  The various
 listeners have a panel whereby one can specify the file to
 which the results will be written (or read from).
 By default, the results are stored as CSV
 files, typically with a ".jtl" extension.  Storing as CSV is the most efficient option, but is less detailed than XML (the other available option).</p>
 <note>To read existing results and display them, use the file panel Browse button to open the file.
 </note>
 <p>Results can be read from XML or CSV format files.
 When reading from CSV results files, the header (if present) is ignored.
 <b>In order to interpret a CSV file correctly, the appropriate properties must be set in jmeter.properties.</b>
 </p>
 <p><b>Listeners can use a lot of memory if there are a lot of samples.</b>
 Most of the listeners currently keep a copy of every sample in their scope, apart from:
 </p>
 <ul>
 <li>Simple Data Writer</li>
 <li>BeanShell Listener</li>
 <li>Assertion Results</li>
 <li>Mailer Visualizer</li>
 <li>Monitor Results</li>
 <li>Summary Report</li>
 </ul>
 <p>To minimise the amount of memory needed, use the Simple Data Writer, and use the CSV format.</p>
 <p>To change the default format, find the following line in jmeter.properties:</p>
 <p>jmeter.save.saveservice.output_format=csv</p>
 <p>Change "csv" to "xml" for greater detail.</p>
 <p>
 The information to be saved is configurable.  For maximum information, choose "xml" as the format and specify "Functional Test Mode" on the Test Plan element.  If this box is not checked, the default saved
 data includes a time stamp (the number of milliseconds since midnight,
 January 1, 1970 UTC), the data type, the thread name, the label, the
 response time, message, and code, and a success indicator.  If checked, all information, including the full response data will be logged.</p>
 <p>
 One can get a more selective set of information my modifying the
 jmeter.properties file.  The following example indicates how to set
 properties to get a vertical bar ("|") delimited format that will
 output results like:.</p>
 <p>
 <code>
 <pre>
 timeStamp|time|label|responseCode|threadName|dataType|success|failureMessage
 02/06/03 08:21:42|1187|Backoffice Home|200|Thread Group-1|text|true|
 02/06/03 08:21:42|47|Login BO|200|Thread Group-1|text|false|Test Failed, 
 	expected to contain: password etc.
 </pre>
 </code></p>
 <p>
 The corresponding jmeter.properties file excerpt is below.  One oddity
 in this example is that the output_format is set to csv, which
 typically
 indicates comma-separated values.  However, the default_delimiter was
 set to be a vertical bar instead of a comma, so the csv tag is a
 misnomer in this case.</p>
 <p>
 <code>
 <pre>
     #---------------------------------------------------------------------------
     # Results file configuration
     #---------------------------------------------------------------------------
     
     # This section helps determine how result data will be saved.
     # The commented out values are the defaults.
     
     # legitimate values: xml, csv, db.  Only xml and csv are currently supported.
     jmeter.save.saveservice.output_format=csv
     
     # true when field should be saved; false otherwise
     
     # assertion_results_failure_message only affects CSV output
     jmeter.save.saveservice.assertion_results_failure_message=true
     jmeter.save.saveservice.data_type=true
     jmeter.save.saveservice.label=true
     jmeter.save.saveservice.response_code=true
     jmeter.save.saveservice.response_data=false
     jmeter.save.saveservice.response_message=false
     jmeter.save.saveservice.successful=true
     jmeter.save.saveservice.thread_name=true
     jmeter.save.saveservice.time=true
     
     # legitimate values: none, ms, or a format suitable for SimpleDateFormat
     #jmeter.save.saveservice.timestamp_format=ms
     jmeter.save.saveservice.timestamp_format=MM/dd/yy HH:mm:ss
     
     # legitimate values: none, first, all
     jmeter.save.saveservice.assertion_results=first
     
     # For use with Comma-separated value (CSV) files or other formats
     # where the fields' values are separated by specified delimiters.
     jmeter.save.saveservice.default_delimiter=|
     jmeter.save.saveservice.print_field_names=true
 </pre>
 </code></p>
 <p>
 The date format to be used for the timestamp_format is described in <A
 HREF="http://java.sun.com/j2se/1.4/docs/api/java/text/SimpleDateFormat.html">
 <B>SimpleDateFormat</B></A>.
 Bear in mind that choosing a date format other than "ms" is likely to
 make it impossible for JMeter to interpret the value when it is read
 in later for viewing purposes.</p>
 <note>The entries in jmeter.properties are used to define the defaults; 
 these can be overriden for individual listeners by using the Configure button,
 as shown below. 
 The settings in jmeter.properties also apply to the listener that is added
 by using the -l command-line flag.
 </note>
 <p>
 The internal viewing capabilities are described in the following subsections.</p>
 	<br></br>
 
 </description>
 
 <component name="Sample Result Save Configuration" index="&sect-num;.3.1"  width="767" height="252" screenshot="sample_result_config.png">
 <description>
 <p>
 Listeners can be configured to save different items to the result log files (JTL) by using the Config popup as shown below.
 The defaults are defined as described in the previous section.
 </p>
 <p>Meaning of the sample attributes in the log files:</p>
 <ul>
 <li> t - elapsed time (ms)</li>
 <li>lt - latency = time to initial response (ms)</li>
 <li>ts - timestamp (ms since 1970)</li>
 <li> s - successful (true/false)</li>
 <li>lb - label</li>
 <li>rc - response code (e.g. 200)</li>
 <li>rm - response message (e.g. OK)</li>
 <li>tn - thread name</li>
 <li>dt - data type</li>
 <li>de - data encoding</li>
 </ul>
 </description>
 </component>
 
 
 
 <component name="Graph Full Results" index="&sect-num;.3.2"  width="672" height="316" screenshot="graphfullresults.png">
 <description>No Description</description>
 </component>
 
 <component name="Graph Results" index="&sect-num;.3.3"  width="605" height="435" screenshot="graph_results.png">
 <description><p>The Graph Results listener generates a simple graph that plots all sample times.  Along
 the bottom of the graph, the current sample (black), the current average of all samples(blue), the
 current standard deviation (red), and the current throughput rate (green) are displayed in milliseconds.</p>
 <p>The throughput number represents the actual number of requests/minute the server handled.  This calculation
 includes any delays you added to your test and JMeter's own internal processing time.  The advantage
 of doing the calculation like this is that this number represents something
 real - your server in fact handled that many requests per minute, and you can increase the number of threads
 and/or decrease the delays to discover your server's maximum throughput.  Whereas if you made calculations
 that factored out delays and JMeter's processing, it would be unclear what you could conclude from that
 number.</p></description>
 <p>The following table briefly describes the items on the graph. 
 Further details on the precise meaning of the statistical terms can be found on the web
  - e.g. Wikipedia - or by consulting a book on statistics.
  </p>
  <ul>
  <li>Data - plot the actual data values</li>
  <li>Average - plot the Average</li>
  <li>Median - plot the Median (midway value)</li>
  <li>Deviation - plot the Standard Deviation (a measure of the variation)</li>
  <li>Throughput - plot the number of samples per unit of time</li>
  </ul>
  <p>The individual figures at the bottom of the display are the current values. 
  "Latest Sample" is the current elapsed sample time, shown on the graph as "Data".</p>
 </component>
 
 <component name="Spline Visualizer" index="&sect-num;.3.4"  width="581" height="440" screenshot="spline_visualizer.png">
 <description><p>The Spline Visualizer provides a view of all sample times from the start
 of the test till the end, regardless of how many samples have been taken.  The spline
 has 10 points, each representing 10% of the samples, and connected using spline
 logic to show a single continuous line.</p></description>
 </component>
 
 <component name="Assertion Results" index="&sect-num;.3.5"  width="658" height="277" screenshot="assertion_results.png">
 <description><p>The Assertion Results visualizer shows the Label of each sample taken.
 It also reports failures of any <a href="test_plan.html#assertions">Assertions</a> that
 are part of the test plan.</p></description>
 
 <links>
         <complink name="Response Assertion"/>
 </links>
 </component>
 
 <component name="View Results Tree" index="&sect-num;.3.6"  width="791" height="506" screenshot="view_results_tree.png">
 <description>The View Results Tree shows a tree of all sample responses, allowing you to view the
 response for any sample.  In addition to showing the response, you can see the time it took to get
 this response, and some response codes.
 Note that the Request panel only shows the headers added by JMeter.
 It does not show any headers (such as Host) that may be added by the HTTP protocol implementation.
 <p>
-There are three ways to view the response, selectable by a radio
-button.</p>
+There are several ways to view the response, selectable by a radio button.</p>
 <p>The default view shows all of the text contained in the
 response.</p>
 <p>The HTML view attempts to render the response as
 HTML.  The rendered HTML is likely to compare poorly to the view one
 would get in any web browser; however, it does provide a quick
 approximation that is helpful for initial result evaluation. 
 If the "Download embedded resources" check-box is selected, the renderer
 may download images and style-sheets etc referenced by the HTML. 
 If the checkbox is not selected, the renderer will not download images etc.
 </p>
 <p>The Render XML view will show response in tree style. 
 Any DTD nodes or Prolog nodes will not show up in tree; however, response may contain those nodes.
 </p>
+<p>The Render JSON view will show the response in tree style (also handles JSON embedded in JavaScript).</p>
 </description>
 <p>
 	The Control Panel (above) shows an example of an HTML display.
 	Figure 9 (below) shows an example of an XML display.
 <figure image="view_results_tree_xml.png">Figure 9 Sample XML display</figure>
 </p>
 </component>
 
 <component name="Aggregate Report" index="&sect-num;.3.7"  width="762" height="235" screenshot="aggregate_report.png">
 <description>The aggregate report creates a table row for each differently named request in your
 test.  For each request, it totals the response information and provides request count, min, max,
 average, error rate, approximate throughput (request/second) and Kilobytes per second throughput.
 Once the test is done, the throughput is the actual through for the duration of the entire test.
 <p>
 The thoughput is calculated from the point of view of the sampler target 
 (e.g. the remote server in the case of HTTP samples).
 JMeter takes into account the total time over which the requests have been generated.
 If other samplers and timers are in the same thread, these will increase the total time,
 and therefore reduce the throughput value. 
 So two identical samplers with different names will have half the throughput of two samplers with the same name.
 It is important to choose the sampler names correctly to get the best results from
 the Aggregate Report.
 </p>
 <note>
 Calculation of the Median and 90% Line values requires a lot of memory as details of every Sample have to be saved.
 See the <complink name="Summary Report"/> for a similar Listener that does not need so much memory.
 </note>
 <ul>
 <li>Label - The label of the sample.</li>
 <li># Samples - The number of samples for the URL</li>
 <li>Average - The average time of a set of results</li>
 <li>Median - The median is the time in the middle of a set of results.</li>
 <li>90% Line - the maximum time taken for the fastest 90% of the samples. #
 The remaining samples took longer than this.</li>
 <li>Min - The lowest time for the samples of the given URL</li>
 <li>Max - The longest time for the samples of the given URL</li>
 <li>Error % - Percent of requests with errors</li>
 <li>Throughput - Throughput measured in requests per second/minute/hour</li>
 <li>Kb/sec - The throughput measured in Kilobytes per second</li>
 </ul>
 </description>
 </component>
 
 <component name="View Results in Table" index="&sect-num;.3.8"  width="643" height="678" screenshot="table_results.png">
 <description>This visualizer creates a row for every sample result.  Each sample result's URL,
 time in milliseconds, success/failure is displayed.  Like the <complink name="View Results Tree"/>,
 this visualizer uses a lot of memory. The last column shows the number of bytes for the
 response from the server.</description>
 </component>
 
 <component name="Simple Data Writer" index="&sect-num;.3.9"  width="649" height="157" screenshot="simpledatawriter.png">
 <description>This listener can record results to a file
 but not to the UI.  It is meant to provide an efficient means of
 recording data by eliminating GUI overhead.</description>
 </component>
 
 <component name="Monitor Results" index="&sect-num;.3.10"  width="762" height="757" screenshot="monitor_screencap.png">
 <description>
 <p>Monitor Results is a new Visualizer for displaying server
 status. It is designed for Tomcat 5, but any servlet container
 can port the status servlet and use this monitor. There are two primary
 tabs for the monitor. The first is the "Health" tab, which will show the
 status of one or more servers. The second tab labled "Performance" shows
 the performance for one server for the last 1000 samples. The equations
 used for the load calculation is included in the Visualizer.</p>
 <p>Currently, the primary limitation of the monitor is system memory. A
 quick benchmark of memory usage indicates a buffer of 1000 data points for
 100 servers would take roughly 10Mb of RAM. On a 1.4Ghz centrino
 laptop with 1Gb of ram, the monitor should be able to handle several
 hundred servers.</p>
 <p>As a general rule, monitoring production systems should take care to
 set an appropriate interval. Intervals shorter than 5 seconds are too
 aggressive and have a potential of impacting the server. With a buffer of
 1000 data points at 5 second intervals, the monitor would check the server
 status 12 times a minute or 720 times a hour. This means the buffer shows
 the performance history of each machine for the last hour.</p>
 <note>
 The monitor requires Tomcat 5 or above. 
 Use a browser to check that you can access the Tomcat status servlet OK.
 </note>
 </description>
 </component>
 
 <component name="Distribution Graph (alpha)" index="&sect-num;.3.11"  width="819" height="626" screenshot="distribution_graph.png">
 <description>
 <p>The distribution graph will display a bar for every unique response time. Since the
 granularity of System.currentTimeMillis() is 10 milliseconds, the 90% threshold should be
 within the width of the graph. The graph will draw two threshold lines: 50% and 90%.
 What this means is 50% of the response times finished between 0 and the line. The same
 is true of 90% line. Several tests with Tomcat were performed using 30 threads for 600K
 requests. The graph was able to display the distribution without any problems and both
 the 50% and 90% line were within the width of the graph. A performant application will
 generally produce results that clump together. A poorly written application that has
 memory leaks may result in wild fluctuations. In those situations, the threshold lines
 may be beyond the width of the graph. The recommended solution to this specific problem
 is fix the webapp so it performs well. If your test plan produces distribution graphs
 with no apparent clumping or pattern, it may indicate a memory leak. The only way to
 know for sure is to use a profiling tool.</p>
 </description>
 </component>
 
 <component name="Aggregate Graph" index="&sect-num;.3.12"  width="839" height="770" screenshot="aggregate_graph.png">
 <description>The aggregate graph is similar to the aggregate report. The primary
 difference is the aggregate graph provides an easy way to generate bar graphs and save
 the graph as a PNG file. By default, the aggregate graph will generate a bar chart
 450 x 250 pixels.</description>
 </component>
 
 <component name="Mailer Visualizer" index="&sect-num;.3.13"  width="645" height="345" screenshot="mailervisualizer.png">
 <description><p>The mailer visualizer can be set up to send email if a test run receives too many
 failed responses from the server.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="From" required="Yes">Email address to send messages from.</property>
         <property name="Addressie(s)" required="Yes">Email address to send messages to.</property>
         <property name="SMTP Host" required="No">IP address or host name of SMTP (email redirector)
         server.</property>
         <property name="Failure Subject" required="No">Email subject line for fail messages.</property>
         <property name="Success Subject" required="No">Email subject line for success messages.</property>
         <property name="Failure Limit" required="Yes">Once this number of failed responses are received, a failure
         email is sent.</property>
         <property name="Success Limit" required="Yes">Once this number of successful responses are
         received <strong>after previously reaching the failure limit</strong>, a success email
         is sent.  The mailer will thus only send out messages in a sequence of failed-succeeded-failed-succeeded, etc.</property>
         <property name="Test Mail" required="No">Press this button to send a test mail</property>
         <property name="Failures" required="No">A field that keeps a running total of number
         of failures so far received.</property>
 </properties>
 </component>
 
 <component name="BeanShell Listener"  index="&sect-num;.3.14"  width="768" height="230" screenshot="beanshell_listener.png">
 <description>
 <p>
 The BeanShell Listener allows the use of BeanShell for processing samples for saving etc.
 </p>
 <p>
 		<b>Please note that the BeanShell jar file is not included with JMeter; it needs to be separately downloaded.
 		<br></br>
         For full details on using BeanShell, please see the BeanShell web-site at http://www.beanshell.org/.</b>
 </p>
 <p>
 The test element supports the ThreadListener and TestListener methods.
 These should be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 </description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Script" required="Yes">The BeanShell script. The return value is ignored. See below for variables defined to the script.</property>
 </properties>
 <ul>
 <li>log - (Logger) - can be used to write to the log file</li>
 <li>ctx - (JMeterContext) - gives access to the context</li>
 <li>vars - (JMeterVariables) - gives read/write access to variables: vars.get(key); vars.put(key,val)</li>
 <li>sampleResult - (SampleResult) - gives access to the previous SampleResult</li>
 <li>sampleEvent (SampleEvent) gives access to the current sample event</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <b>beanshell.listener.init</b> is defined, this is used to load an initialisation file, which can be used to define methods etc for use in the BeanShell script.</p>
 </component>
 
 <component name="Summary Report" index="&sect-num;.3.15"  width="643" height="255" screenshot="summary_report.png">
 <description>The summary report creates a table row for each differently named request in your
 test. This is similar to the <complink name="Aggregate Report"/> , except that it uses less memory.
 <p>
 The thoughput is calculated from the point of view of the sampler target 
 (e.g. the remote server in the case of HTTP samples).
 JMeter takes into account the total time over which the requests have been generated.
 If other samplers and timers are in the same thread, these will increase the total time,
 and therefore reduce the throughput value. 
 So two identical samplers with different names will have half the throughput of two samplers with the same name.
 It is important to choose the sampler labels correctly to get the best results from
 the Report.
 </p>
 <ul>
 <li>Label - The label of the sample.</li>
 <li># Samples - The number of samples for the URL</li>
 <li>Average - The average elapsed time of a set of results</li>
 <li>Min - The lowest elapsed time for the samples of the given URL</li>
 <li>Max - The longest elapsed time for the samples of the given URL</li>
 <li>Std. Dev. - the standard deviation of the sample elapsed time</li>
 <li>Error % - Percent of requests with errors</li>
 <li>Throughput - Throughput measured in requests per second/minute/hour</li>
 <li>Kb/sec - The throughput measured in Kilobytes per second</li>
 <li>Avg. Bytes - average size of the sample response in bytes. (in JMeter 2.2 it wrongly showed the value in kB)</li>
 </ul>
 </description>
 </component>
 
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.4 Configuration Elements" anchor="config_elements">
 <description>
 	<br></br>
 	Configuration elements can be used to set up defaults and variables for later use by samplers.
 	Note that these elements are processed at the start of the scope in which they are found, 
 	i.e. before any samplers in the same scope.
 	<br></br>
 </description>
 
 <component name="CSV Data Set Config" index="&sect-num;.4.1"  width="360" height="196" screenshot="csvdatasetconfig.png">
 <description>
         <p>
 	CSV Data Set Config is used to read lines from a file, and split them into variables.
 	It is easier to use than the __CSVRead() and _StringFromFile() functions.
 	</p>
 	<p>
 	As a special case, the string "\t" (without quotes) is treated as a Tab.
 	</p>
 	<p>
 	When the end of file is reached, and the recycle option is true, reading starts again with the first line of the file.
 	If the recycle option is false, then all the variables are set to <b>&amp;lt;EOF&gt;</b> when the end of file is reached.
 	This value can be changed by setting the JMeter property <b>csvdataset.eofstring</b>.
 	</p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Filename" required="Yes">Name of the file to be read. 
   Relative file names are resolved with respect to the current test plan.
   Absolute file names are also supported, but note that they are unlikely to work in remote mode, 
   unless the remote server has the same directory structure.
   </property>
   <property name="Variable Names" required="Yes">List of variable names (comma-delimited)</property>
   <property name="Delimiter" required="Yes">Delimiter to be used to split the records in the file.
   If there are fewer values on the line than there are variables the remaining variables are not updated -
   so they will retain their previous value (if any).</property>
   <property name="Recycle on EOF?" required="Yes">Should the file be re-read from the beginning on reaching EOF? (default is true)</property>
 </properties>
 </component>
 
 <component name="FTP Request Defaults" index="&sect-num;.4.2"  width="404" height="144" screenshot="ftp-config/ftp-request-defaults.gif">
 <description></description>
 </component>
 
 <component name="HTTP Authorization Manager" index="&sect-num;.4.3"  width="575" height="340" screenshot="http-config/http-auth-manager.gif">
 <note>If there is more than one Authorization Manager in the scope of a Sampler,
 there is currently no way to sepcify which one is to be used.</note>
 
 <description>
 <p>The Authorization Manager lets you specify one or more user logins for web pages that are
 restricted using Basic HTTP Authentication.  You see this type of authentication when you use
 your browser to access a restricted page, and your browser displays a login dialog box.  JMeter
 transmits the login information when it encounters this type of page.</p>
 
 <note>In the current release, all JMeter threads in a Thread Group use the same username/password
 for a given Base URL even if you create multiple users with the same Base URL in the authorization table.
 We plan to correct this in a future release.  As a workwaround, you can create multiple Thread Groups for your
 Test Plan, with each Thread Group having its own Authorization Manager.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Base URL" required="Yes">A partial or complete URL that matches one or more HTTP Request URLs.  As an example,
 say you specify a Base URL of "http://jakarta.apache.org/restricted/" with a username of "jmeter" and
 a password of "jmeter".  If you send an HTTP request to the URL
 "http://jakarta.apache.org/restricted/ant/myPage.html", the Authorization Manager sends the login
 information for the user named, "jmeter".</property>
   <property name="Username" required="Yes">The username to authorize.</property>
   <property name="Password" required="Yes">The password for the user.</property>
   <property name="Domain" required="No">The domain to use for NTLM.</property>
   <property name="Realm" required="No">The realm to use for NTLM.</property>
 </properties>
 <note>
 The Realm only applies to the HttpClient sampler.
 In JMeter 2.2, the domain and realm did not have separate columns, and were encoded as part of
 the user name in the form: [domain\]username[@realm].
 This was an experimental feature and has been removed.
 </note>
 <b>Controls:</b>
 <ul>
   <li>Add Button - Add an entry to the authorization table.</li>
   <li>Delete Button - Delete the currently selected table entry.</li>
   <li>Load Button - Load a previously saved authorization table and add the entries to the existing
 authorization table entries.</li>
   <li>Save As Button - Save the current authorization table to a file.</li>
 </ul>
 
 <note>When you save the Test Plan, JMeter automatically saves all of the authorization
 table entries.</note>
 
 <example title="Authorization Example" anchor="authorization_example">
 
 <p><a href="../demos/AuthManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan on a local server that sends three HTTP requests, two requiring a login and the
 other is open to everyone.  See figure 10 to see the makeup of our Test Plan.  On our server, we have a restricted
 directory named, "secret", which contains two files, "index.html" and "index2.html".  We created a login id named, "kevin",
 which has a password of "spot".  So, in our Authorization Manager, we created an entry for the restricted directory and
 a username and password (see figure 11).  The two HTTP requests named "SecretPage1" and "SecretPage2" make requests
 to "/secret/index.html" and "/secret/index2.html".  The other HTTP request, named "NoSecretPage" makes a request to
 "/index.html".</p>
 
 <figure image="http-config/auth-manager-example1a.gif">Figure 10 - Test Plan</figure>
 <figure image="http-config/auth-manager-example1b.gif">Figure 11 - Authorization Manager Control Panel</figure>
 
 <p>When we run the Test Plan, JMeter looks in the Authorization table for the URL it is requesting.  If the Base URL matches
 the URL, then JMeter passes this information along with the request.</p>
 
 <note>You can download the Test Plan, but since it is built as a test for our local server, you will not
 be able to run it.  However, you can use it as a reference in constructing your own Test Plan.</note>
 </example>
 
 </component>
 
 <component name="HTTP Cookie Manager" index="&sect-num;.4.4"  width="548" height="319" screenshot="http-config/http-cookie-manager.png">
 
 <note>If there is more than one Cookie Manager in the scope of a Sampler,
 there is currently no way to specify which one is to be used.</note>
 
 <description><p>The Cookie Manager element has two functions:<br></br>
 First, it stores and sends cookies just like a web browser. If you have an HTTP Request and
 the response contains a cookie, the Cookie Manager automatically stores that cookie and will
 use it for all future requests to that particular web site.  Each JMeter thread has its own
 "cookie storage area".  So, if you are testing a web site that uses a cookie for storing
 session information, each JMeter thread will have its own session.</p>
 <p>Received Cookies are stored as JMeter thread variables. 
 Thus the value of a cookie with the name TEST can be referred to as ${TEST}</p>
 <p>Second, you can manually add a cookie to the Cookie Manager.  However, if you do this,
 the cookie will be shared by all JMeter threads.</p>
 <p>Note that such Cookies are created with an Expiration time far in the future</p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Clear Cookies each Iteration" required="Yes">If selected, all cookies are cleared each time the main Thread Group loop is executed.
   This includes all the Cookies stored in the manager, so they will never be sent.</property>
   <property name="Cookie Policy" required="Yes">The cookie policy that will be used to manage the cookies. 
   "compatibility" is the default, and should work in most cases.
   See http://jakarta.apache.org/commons/httpclient/cookies.html and 
   http://jakarta.apache.org/commons/httpclient/apidocs/org/apache/commons/httpclient/cookie/CookiePolicy.html
   [Note: "ignoreCookies" is equivalent to omitting the CookieManager.]
     </property>
   <property name="Cookies Stored in the Cookie Manager" required="No (discouraged, unless you know what you're doing)">This
   gives you the opportunity to use hardcoded cookies that will be used by all threads during the test execution.</property>
   <property name="Add Button" required="N/A">Add an entry to the cookie table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved cookie table and add the entries to the existing
 cookie table entries.</property>
   <property name="Save As Button" required="N/A">
   Save the current cookie table to a file (does not save any cookies extracted from HTTP Responses).
   </property>
 </properties>
 
 </component>
 
 <component name="HTTP Proxy Server" index="&sect-num;.4.5"  width="689" height="464" screenshot="proxy_control.png">
 <note>The Proxy Server can only record HTTP traffic. 
 It is not possible to record HTTPS (SSL) sessions; however there is an HTTPS spoofing mode - see below.</note>
 <description><p>The Proxy Server allows JMeter to watch and record your actions while you browse your web application
 with your normal browser (such as Internet Explorer).  JMeter will create test sample objects and store them
 directly into your test plan as you go (so you can view samples interactively while you make them).</p>
 
 <p>To use the proxy server, <i>add</i> the HTTP Proxy Server element to the workbench.
 Select the WorkBench element in the tree, and right-click on this element to get the
 Add menu (Add --> Non-Test Elements --> HTTP Proxy Server).</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Port" required="Yes">The port that the Proxy Server listens to.  8080 is the default, but you can change it.</property>
 		<property name="Capture HTTP Headers" required="Yes">Should headers be added to the plan?</property>
 		<property name="Set Keep-Alive" required="Yes">Automatically set Keep-Alive in the generated samplers?</property>
 		<property name="Add Assertions" required="Yes">Add a blank assertion to each sampler?</property>
 		<property name="Regex Matching" required="Yes">Use Regex Matching when replacing variables?</property>
         <property name="Attempt https Spoofing" required="Yes">
           When you enable https spoofing, the following  happens:
           <ul>
             <li>All http requests from the client are turned into https (between the proxy
               and the web server).</li>
             <li>All text response data is scanned and any occurrence of the string "https" 
               is replaced with &quot;http.&quot;</li>
           </ul>
           So if you want to use this feature, while you are browsing in your client,
           instead of typing "https://..." into the browser, type &quot;http://...&quot;.  JMeter
           will request and record <i>everything</i> as https, whether it should be or not.
         </property>
         <!--TODO: there's some undocumented flags here -->
         <property name="Target Controller" required="Yes">The controller where the proxy will store the generated samples. By default, it will look for a Recording Controller and store them there wherever it is.</property>
         <property name="Grouping" required="Yes">Whether to group samplers for requests from a single "click" (requests received without significant time separation), and how to represent that grouping in the recording:
            <ul>
              <li>Do not group samplers: store all recorded samplers sequentially, without any grouping.</li>
              <li>Add separators between groups: add a controller named "--------------" to create a visual separation between the groups. Otherwise the samplers are all stored sequentially.</li>
              <li>Put each group in a new controller: create a new <complink name="Simple Controller"/> for each group, and store all samplers for that group in it.</li>
              <li>Store 1st sampler of each group only: only the first request in each group will be recorded. The "Follow Redirects" and "Retrieve All Embedded Resources..." flags will be turned on in those samplers.</li>
            </ul>
         </property>
         <!-- TODO:property name="Group Separation Interval">Inactivity time between two requests needed to consider them in two separate groups.</property-->
         <property name="Patterns to Include" required="No">Regular expressions that are matched against the full URL that is sampled.  Allows filtering of requests that are recorded.  All requests pass through, but only
         those that meet the requirements of the Include/Exclude fields are <i>recorded</i>.  If both Include and Exclude are
         left empty, then everything is recorded (which can result in dozens of samples recorded for each page, as images, stylesheets,
         etc are recorded).  <b>If there is at least one entry in the Include field, then only requests that match one or more Include patterns are
         recorded</b>.</property>
         <property name="Patterns to Exclude" required="No">Regular expressions that are matched against the URL that is sampled.
         <b>Any requests that match one or more Exclude pattern are <i>not</i> recorded</b>.</property>
         <property name="Start Button" required="N/A">Start the proxy server.  JMeter writes the following message to the console once the proxy server has started up and is ready to take requests: "Proxy up and running!".</property>
         <property name="Stop Button" required="N/A">Stop the proxy server.</property>
         <property name="Restart Button" required="N/A">Stops and restarts the proxy server.  This is
   useful when you change/add/delete an include/exclude filter expression.</property>
 </properties>
 
 <p>To add an entry to the Include or Exclude field, type the entry into the text field, and hit &quot;Enter&quot; when done.
 The text will be added to the List box to the right of the text field.  To clear the text field, hit the &quot;clear&quot;
 button.  Currently, there is no way to individually select items and delete them.</p>
 
 <p>These entries will be treated as Perl-type regular expressions.  They will be matched against the host name + the path of
 each browser request.  Thus, if the URL you are browsing is <b>http://jakarta.apache.org/jmeter/index.html?username=xxxx</b>,
 then the regular expression will be tested against the string: <b>&quot;jakarta.apache.org/jmeter/index.html&quot;</b>.  Thus,
 if you wanted to include all .html files, you're regular expression might look like: <b>&quot;.*\.html&quot;</b>.  Using a
 combination of includes and excludes, you should be able to record what you are interested in and skip what you are
 not.</p>
 
 <p>
 N.B. the string that is matched by the regular expression must be the same as the <b>whole</b> host+path string.<br></br>Thus <b>&quot;\.html&quot;</b> will <b>not</b> match <b>j.a.o/index.html</b>
 </p>
 
 <p>It is also possible to have the proxy add timers to the recorded script. To
 do this, create a timer directly within the HTTP Proxy Server component.
 The proxy will place a copy of this timer into each sample it records, or into
 the first sample of each group if you're using grouping. This copy will then be
 scanned for occurences of variable ${T} in its properties, and any such
 occurences will be replaced by the time gap from the previous sampler
 recorded (in milliseconds).</p>
 
 <p>When you are ready to begin, hit &quot;start&quot;.</p>
 <note>You will need to edit the proxy settings of your browser to point at the
 appropriate server and port, where the server is the machine JMeter is running on, and
 the port # is from the Proxy Control Panel shown above.</note>
 
 <b>Where Do Samples Get Recorded?</b>
 <p>JMeter places the recorded samples in the Target Controller you choose. If you choose the default option
 "Use Recording Controller", they will be stored in the first Recording Controller found in the test object tree (so be
 sure to add a Recording Controller before you start recording).</p>
 
 <p>If the HTTP Proxy Server finds enabled <complink name="HTTP Request Defaults"/> directly within the
 controller where samples are being stored, or directly within any of its parent controllers, the recorded samples
 will have empty fields for the default values you specified. You may further control this behaviour by placing an
 HTTP Request Defaults element directly within the HTTP Proxy Server, whose non-blank values will override
 those in the other HTTP Request Defaults. See <a href="best-practices.html#proxy_server"> Best
 Practices with the Proxy Server</a> for more info.</p>
 
 <p>Similarly, if the HTTP Proxy Server finds <complink name="User Defined Variables"/> (UDV) directly within the
 controller where samples are being stored, or directly within any of its parent controllers, the recorded samples
 will have any occurences of the values of those variables replaced by the corresponding variable. Again, you can
 place User Defined Variables directly within the HTTP Proxy Server to override the values to be replaced. See
 <a href="best-practices.html#proxy_server"> Best Practices with the Proxy Server</a> for more info.</p>
 
 <p>Replacement by Variables: by default, the Proxy server looks for all occurences of UDV values. 
 If you define the variable "WEB" with the value "www", for example, 
 the string "www" will be replaced by ${WEB} wherever it is found.
 To avoid this happening everywhere, set the "Regex Matching" check-box.
 This tells the proxy server to treat values as Regexes (using ORO).
 <br></br>
 If you want to match a whole string only, enclose it in ^$, e.g. "^thus$".
 <br></br>
 If you want to match /images at the start of a string only, use the value "^/images".
 Jakarta ORO also supports zero-width look-ahead, so one can match /images/... 
 but retain the trailing / in the output by using "^/images(?=/)".
 Note that the current version of Jakara ORO does not support look-behind - i.e. "(?&amp;lt;=...) or (?&amp;lt;!...)".
 <br></br>
 If there are any problems interpreting any variables as patterns, these are reported in jmeter.log,
 so be sure to check this if UDVs are not working as expected.
 </p>
 <p>When you are done recording your test samples, stop the proxy server (hit the &quot;stop&quot; button).  Remember to reset
 your browser's proxy settings.  Now, you may want to sort and re-order the test script, add timers, listeners, a
 cookie manager, etc.</p>
 
 <b>How can I record the server's responses too?</b>
 <p>Just place a <complink name="View Results Tree"/> listener as a child of the Proxy Server and the responses will be displayed. 
 You can also add a <complink name="Save Responses to a file"/> Post-Processor which will save the responses to files.
 </p>
 
 </component>
 
 <component name="HTTP Request Defaults" index="&sect-num;.4.6" 
          width="533" height="316" screenshot="http-config/http-request-defaults.png">
 <description><p>This element lets you set default values that your HTTP Request controllers use.  For example, if you are
 creating a Test Plan with 25 HTTP Request controllers and all of the requests are being sent to the same server,
 you could add a single HTTP Request Defaults element with the "Server Name or IP" field filled in.  Then, when
 you add the 25 HTTP Request controllers, leave the "Server Name or IP" field empty.  The controllers will inherit
 this field value from the HTTP Request Defaults element.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="Yes">Domain name or IP address of the web server.</property>
         <property name="Port" required="No (defaults to 80)">Port the web server is listening to.</property>
         <property name="Protocol" required="Yes">HTTP or HTTPS.</property>
         <property name="Method" required="Yes">HTTP GET or HTTP POST.</property>
         <property name="Path" required="Yes">The path to resource (for example, /servlets/myServlet). If the
 resource requires query string parameters, add them below in the
 "Send Parameters With the Request" section.</property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>.  The query string will be generated in the correct fashion, depending on
         the choice of "Method" you made (ie if you chose GET, the query string will be
         appended to the URL, if POST, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.</property>
 </properties>
 </component>
 
 <component name="HTTP Header Manager" index="&sect-num;.4.7"  width="" height="" screenshot="http-config/http-header-manager.gif">
 <note>If there is more than one Header Manager in the scope of a Sampler,
 there is currently no way to sepcify which one is to be used.</note>
 
 <description><p>The Header Manager lets you add or override HTTP request headers.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Name (Header)" required="No (You should have at least one, however)">Name of the request header.
         Two common request headers you may want to experiment with
 are "User-Agent" and "Referer".</property>
   <property name="Value" required="No (You should have at least one, however)">Request header value.</property>
   <property name="Add Button" required="N/A">Add an entry to the header table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved header table and add the entries to the existing
 header table entries.</property>
   <property name="Save As Button" required="N/A">Save the current header table to a file.</property>
 </properties>
 
 <example title="Header Manager example" anchor="header_manager_example">
 
 <p><a href="../demos/HeaderManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan
 that tells JMeter to override the default "User-Agent" request header and use a particular Internet Explorer agent string
 instead. (see figures 9 and 10).</p>
 
 <figure image="http-config/header-manager-example1a.gif">Figure 12 - Test Plan</figure>
 <figure image="http-config/header-manager-example1b.gif">Figure 13 - Header Manager Control Panel</figure>
 </example>
 
 </component>
 
 <component name="Java Request Defaults" index="&sect-num;.4.8"  width="454" height="283" screenshot="java_defaults.png">
 <description><p>The Java Request Defaults component lets you set default values for Java testing.  See the <complink name="Java Request" />.</p>
 </description>
 
 </component>
 
 <component name="JDBC Connection Configuration" index="&sect-num;.4.9" 
                  width="369" height="443" screenshot="jdbc-config/jdbc-conn-config.png">
 	<description>Creates a database connection pool (used by <complink name="JDBC Request"/>Sampler)
 	 with JDBC Connection settings.
 	</description>
 	<properties>
 		<property name="Name" required="No">Descriptive name for the connection pool that is shown in the tree.</property>
 		<property name="Variable Name" required="Yes">The name of the variable the connection pool is tied to.  
 		Multiple connection pools can be used, each tied to a different variable, allowing JDBC Samplers
 		to select the pool to draw connections from.</property>
 		<property name="Max Number of Connections" required="Yes">Maximum number of connections allowed in the pool</property>
 		<property name="Pool timeout" required="Yes">Pool throws an error if the timeout period is exceeded in the 
 		process of trying to retrieve a connection</property>
 		<property name="Idle Cleanup Interval (ms)" required="Yes">Uncertain what exactly this does.</property>
 		<property name="Auto Commit" required="Yes">Turn auto commit on or off for the connections.</property>
 		<property name="Keep-alive" required="Yes">Uncertain what exactly this does.</property>
 		<property name="Max Connection Age (ms)" required="Yes">Uncertain what exactly this does.</property>
 		<property name="Validation Query" required="Yes">A simple query used to determine if the database is still
 		responding.</property>
 		<property name="Database URL" required="Yes">JDBC Connection string for the database.</property>
 		<property name="JDBC Driver class" required="Yes">Fully qualified name of driver class. (Must be in
 		JMeter's classpath - easiest to copy .jar file into JMeter's /lib directory).</property>
 		<property name="Username" required="Yes">Name of user to connect as.</property>
 		<property name="Password" required="Yes">Password to connect with.</property>
 	</properties>
 <p>Different databases and JDBC drivers require different JDBC settings. 
 The Database URL and JDBC Driver class are defined by the provider of the JDBC implementation.</p>
 <p>Some possible settings are shown below. Please check the exact details in the JDBC driver documentation.</p>
 
 <table>
 <tr><th>Database</th><th>Driver class</th><th>Database URL</th></tr>
 <tr><td>MySQL</td><td>com.mysql.jdbc.Driver</td><td>jdbc:mysql://host[:port]/dbname</td></tr>
 <tr><td>PostgreSQL</td><td>org.postgresql.Driver</td><td>jdbc:postgresql:{dbname}</td></tr>
 <tr><td>Oracle</td><td>oracle.jdbc.driver.OracleDriver</td><td>jdbc:oracle:thin:user/pass@//host:port/service</td></tr>
 <tr><td>Ingres (2006)</td><td>ingres.jdbc.IngresDriver</td><td>jdbc:ingres://host:port/db[;attr=value]</td></tr>
 </table>
 <note>The above may not be correct - please check the relevant JDBC driver documentation.</note>
 </component>
 
 
 <component name="Login Config Element" index="&sect-num;.4.11"  width="352" height="112" screenshot="login-config.png">
 <description><p>The Login Config Element lets you add or override username and password settings in samplers that use username and password as part of their setup.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Username" required="No">The default username to use.</property>
   <property name="Password" required="No">The default password to use.</property>
 </properties>
 
 </component>
 
 
 
 <component name="LDAP Request Defaults" index="&sect-num;.4.12"  width="465" height="375" screenshot="ldap_defaults.png">
 <description><p>The LDAP Request Defaults component lets you set default values for LDAP testing.  See the <complink name="LDAP Request"/>.</p>
 </description>
 
 </component>
 
 <component name="LDAP Extended Request Defaults" index="&sect-num;.4.13"  width="597" height="545" screenshot="ldapext_defaults.png">
 <description><p>The LDAP Extended Request Defaults component lets you set default values for extended LDAP testing.  See the <complink name="LDAP Extended Request"/>.</p>
 </description>
 
 </component>
 
 <component name="TCP Sampler Config" index="&sect-num;.4.14"  width="645" height="256" screenshot="tcpsamplerconfig.png">
 	<note>ALPHA CODE</note>
 <description>
         <p>
 	The TCP Sampler Config provides default data for the TCP Sampler
 	</p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="ServerName or IP" required="">Name or IP of TCP server</property>
   <property name="Port Number" required="">Port to be used</property>
   <property name="Timeout (milliseconds)" required="">Timeout for replies</property>
   <property name="Set Nodelay" required="">Should the nodelay property be set?</property>
   <property name="Text to Send" required="">Text to be sent</property>
 </properties>
 </component>
 
 <component name="User Defined Variables" index="&sect-num;.4.15"  width="690" height="394" screenshot="user_defined_variables.png">
 <description><p>The User Defined Variables lets you define variables for use in other test elements, just as in the <complink name="Test Plan" />.
 The variables in User Defined Variables components will take precedence over those defined closer to the tree root -- including those defined in the Test Plan.</p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="User Defined Variables" required="">Variable name/value pairs. The string under the "Name"
   	column is what you'll need to place inside the brackets in ${...} constructs to use the variables later on. The
   	whole ${...} will then be replaced by the string in the "Value" column.</property>
 </properties>
 <note>If you have more than one Thread Group, make sure you use different names for different values, as UDVs are shared between Thread Groups.</note>
 </component>
 
 <component name="Simple Config Element" index="&sect-num;.4.16"  width="393" height="245" screenshot="simple_config_element.png">
 <description><p>The Simple Config Element lets you add or override arbitrary values in samplers.  You can choose the name of the value
 and the value itself.  Although some adventurous users might find a use for this element, it's here primarily for developers as a basic
 GUI that they can use while developing new JMeter components.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Parameter Name" required="Yes">The name of each parameter.  These values are internal to JMeter's workings and
   are not generally documented.  Only those familiar with the code will know these values.</property>
   <property name="Parameter Value" required="Yes">The value to apply to that parameter.</property>
 </properties>
 
 </component>
 
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.5 Assertions" anchor="assertions">
 <description>
 	<br></br>
 	Assertions are used to perform additional checks on samplers, and are processed after <b>each sampler</b>
 	in the same scope.
 	<br></br>
 	<note>
 	The variable <b>JMeterThread.last_sample_ok</b> is set to
 	"true" or "false" after all assertions for a sampler have been run.
 	 </note>
 </description>
 <component name="Response Assertion" index="&sect-num;.5.1" anchor="basic_assertion"  width="624" height="363" screenshot="assertion/assertion.gif">
 
 <description><p>The response assertion control panel lets you add pattern strings to be compared against various
 	fields of the response.
 	The pattern strings are Perl5-style regular expressions. 
 	</p>
 	<p>
 	A summary of the pattern matching characters can be found at <a href="http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html">http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html</a>
 	</p>
 	<p>You can also choose whether the strings will be expected
 to <b>match</b> the entire response, or if the response is only expected to <b>contain</b> the
 pattern. You can attach multiple assertions to any controller for additional flexibility.</p>
 <p>Note that the pattern string should not include the enclosing delimiters, 
 	i.e. use <b>Price: \d+</b> not <b>/Price: \d+/</b>.
 	</p>
 	<p>
 	By default, the pattern is in multi-line mode, which means that the "." meta-character does not match newline.
     In multi-line mode, "^" and "$" match the start or end of any line anywhere within the string 
     - not just the start and end of the entire string. Note that \s does match new-line.
 	Case is also significant. To override these settings, one can use the extended regular expression syntax.
 	For example:
 </p>
 <pre>
 	(?i) - ignore case
 	(?s) - treat target as single line, i.e. "." matches new-line
 	(?is) - both the above
     These can be used anywhere within the expression, e.g.
     (?i)apple(?-i) Pie - matches "ApPLe Pie", but not "ApPLe pIe"
 </pre>
 
 </description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Response Field to Test" required="Yes">Instructs JMeter which field of the Response to test.
                 This may be the Response Text from the server, the URL string that was sampled,
                 the Response Code (e.g. 404) or the Response Message (e.g. Not Found).
                 <p>
                 The overall success of the sample is determined by combining the result of the
                 assertion with the existing Response status.
                 When the Ignore Status checkbox is selected, the Response status is forced
                 to successful before evaluating the Assertion.
                 </p>
                 HTTP Responses with statuses in the 4xx and 5xx ranges are normally
                 regarded as unsuccessful. 
                 The "Ignore status" checkbox can be used to set the status successful before performing further checks.
                 Note that this will have the effect of clearing any previous assertion failures,
                 so make sure that this is only set on the first assertion.
                 </property>
         <property name="Pattern Matching Rules" required="Yes">Indicates whether the text being tested
         must CONTAIN or MATCH the test patterns.  NOT may also be selected to indicate the text
         should NOT CONTAIN or NOT MATCH the test patterns.</property>
         <property name="Patterns to Test" required="Yes">A list of regular expressions to
         be tested.  Each pattern is tested separately.  There is no difference between setting up
         one Assertion with multiple patterns and setting up multiple Assertions with one
         pattern each (assuming the other options are the same).
         <b>However, when the Ignore Status checkbox is selected, this has the effect of cancelling any
         previous assertion failures - so make sure that the Ignore Status checkbox is only used on
         the first Assertion.</b>
         </property>
 </properties>
 <p>
 	The pattern is a Perl5-style regular expression, but without the enclosing brackets.
 </p>
 <example title="Assertion Examples" anchor="assertion_examples">
 <figure image="assertion/example1a.png">Figure 14 - Test Plan</figure>
 <figure image="assertion/example1b.png">Figure 15 - Assertion Control Panel with Pattern</figure>
 <figure image="assertion/example1c-pass.gif">Figure 16 - Assertion Listener Results (Pass)</figure>
 <figure image="assertion/example1c-fail.gif">Figure 17 - Assertion Listener Results (Fail)</figure>
 </example>
 
 
 </component>
 
 <component name="Duration Assertion" index="&sect-num;.5.2"  width="391" height="147" screenshot="duration_assertion.png">
 <description><p>The Duration Assertion tests that each response was received within a given amount
 of time.  Any response that takes longer than the given number of milliseconds (specified by the
 user) is marked as a failed response.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Duration in Milliseconds" required="Yes">The maximum number of milliseconds
         each response is allowed before being marked as failed.</property>
 
 </properties>
 </component>
 
 <component name="Size Assertion" index="&sect-num;.5.3"  width="331" height="346" screenshot="size_assertion.png">
 <description><p>The Size Assertion tests that each response contains the right number of bytes in it.  You can specify that
 the size be equal to, greater than, less than, or not equal to a given number of bytes.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Size in bytes" required="Yes">The number of bytes to use in testing the size of the response.</property>
         <property name="Type of Comparison" required="Yes">Whether to test that the response is equal to, greater than, less than,
         or not equal to, the number of bytes specified.</property>
 
 </properties>
 </component>
 
 <component name="XML Assertion" index="&sect-num;.5.4"  width="303" height="196" screenshot="xml_assertion.png">
 <description><p>The XML Assertion tests that the response data consists of a formally correct XML document.  It does not
 validate the XML based on a DTD or schema or do any further validation.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 
 </properties>
 </component>
 
 <component name="BeanShell Assertion" index="&sect-num;.5.5"  width="632" height="253" screenshot="bsh_assertion.png">
 <description><p>The BeanShell Assertion allows the user to perform assertion checking using a BeanShell script.
 </p>
 <p>
 <b>Please note that the BeanShell jar file is not included with JMeter;
 it needs to be downloaded separately and placed in the lib directory.
 <br></br>
 For full details on using BeanShell, please see the BeanShell web-site at http://www.beanshell.org/.</b>
 </p><p>
 Note that a different Interpreter is used for each independent occurence of the assertion
 in each thread in a test script, but the same Interpreter is used for subsequent invocations.
 This means that variables persist across calls to the assertion.
 </p>
 <p>
 All Assertions are called from the same thread as the sampler.
 </p>
 <p>
 If the property "beanshell.assertion.init" is defined, it is passed to the Interpreter
 as the name of a sourced file. This can be used to define common methods and variables.
 There is a sample init file in the bin directory: BeanShellAssertion.bshrc
 </p>
 <p>
 The test element supports the ThreadListener and TestListener methods.
 These should be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 </description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
 	The parameters are stored in the following variables:
 	<ul>
 		<li>Parameters - string containing the parameters as a single variable</li>
 	    <li>bsh.args - String array containing parameters, split on white-space</li>
 	</ul></property>
 <property name="Script file" required="No">A file containing the BeanShell script to run</property>
 <property name="Script" required="No">The BeanShell script to run</property>
 </properties>
 <p>There's a <a href="../demos/BeanShellAssertion.bsh">sample script</a> you can try.</p>
 <p>The following variables are defined to the script. 
 These are strings unless otherwise noted:
 <ul>
 <li>log - the Logger Object. (e.g.) log.warn("Message"[,Throwable])</li>
 	<li>SampleResult - the SampleResult Object; read-write</li>
 	<li>Response - the response Object; read-write</li>
 	<li>Failure - boolean; read-write; used to set the Assertion status</li>
 	<li>FailureMessage - String; read-write; used to set the Assertion message</li>
 	<li>ResponseData - the response body (byte [])</li>
 	<li>ResponseCode - e.g. 200</li>
 	<li>ResponseMessage - e.g. OK</li>
 	<li>ResponseHeaders - contains the HTTP headers</li>
 	<li>RequestHeaders - contains the HTTP headers sent to the server</li>
 	<li>SampleLabel</li>
 	<li>SamplerData - data that was sent to the server</li>
 	<li>ctx - JMeterContext</li>
 	<li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value");</li>
 </ul>
 </p>
 <p>The following methods of the Response object may be useful:
 <ul>
 	<li>setStopThread(boolean)</li>
 	<li>setStopTest(boolean)</li>
 	<li>String getSampleLabel()</li>
 	<li>setSampleLabel(String)</li>
 </ul></p>
 </component>
 
 <component name="MD5Hex Assertion" index="&sect-num;.5.6">
 <description><p>The MD5Hex Assertion allows the user to check the MD5 hash of the response data.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="MD5 sum" required="Yes">32 hex digits representing the MD5 hash (case not significant)</property>
 
 </properties>
 </component>
 
 <component name="HTML Assertion" index="&sect-num;.5.7"  width="631" height="365" screenshot="assertion/HTMLAssertion.png">
 <description><p>The HTML Assertion allows the user to check the HTML syntax of the response data using JTidy.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="doctype" required="Yes">omit/auto/strict/loose</property>
 <property name="Format" required="Yes">HTML, XHTML or XML</property>
 <property name="Errors only" required="Yes">Only take note of errors?</property>
 <property name="Error threshold" required="Yes">Number of errors allowed before classing the response as failed</property>
 <property name="Warning threshold" required="Yes">Number of warnings allowed before classing the response as failed</property>
 <property name="Filename" required="No">Name of file to which report is written</property>
 
 </properties>
 </component>
 <component name="XPath Assertion" index="&sect-num;.5.8"  width="678" height="231" screenshot="xpath_assertion.png">
 <description><p>The XPath Assertion tests a document for well formedness, has the option
 of validating against a DTD, or putting the document through JTidy and testing for an
 XPath.  If that XPath exists, the Assertion is true.  Using "/" will match any well-formed
 document, and is the default XPath Expression. 
 The assertion also supports boolean expressions, such as "count(//*error)=2".
 See <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a> for more information
 on XPath.
 </p></description>
 
 <properties>
 <property name="Name"		required="No">Descriptive name for this element that is shown in the tree.</property>
 <property name="Tolerant Parser"	required="No">Be tolerant of XML/HTML errors</property>
 <property name="Use Namespaces"	required="No">Should namespaces be honoured?</property>
 <property name="Validate XML"	required="No">Check the document against its schema.</property>
 <property name="XPath Assertion"		required="Yes">XPath to match in the document.</property>
 <property name="Ignore Whitespace"	required="No">Ignore Element Whitespace.</property>
 <property name="True if nothing matches"	required="No">True if a XPath expression is not matched</property>
 </properties>
 </component>
 <component name="XML Schema Assertion" index="&sect-num;.5.9"  width="771" height="171" screenshot="assertion/XMLSchemaAssertion.png">
 <description><p>The XML Schema Assertion allows the user to validate a response against an XML Schema.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="File Name" required="Yes">Specify XML Schema File Name</property>
 </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.6 Timers" anchor="timers">
 <description>
 	<br></br>
 	<p>
 	Note that timers are processed <b>before</b> each sampler in the scope in which they are found;
 	if there are several timers in the same scope, <b>all</b> the timers will be processed <b>before
 	each</b> sampler.
 	<br></br>
 	Timers are only processed in conjunction with a sampler.
 	A timer which is not in the same scope as a sampler will not be processed at all.
 	<br></br>
 	To apply a timer to a single sampler, add the timer as a child element of the sampler.
 	The timer will be applied before the sampler is executed.
 	To apply a timer after a sampler, either add it to the next sampler, or add it as the
 	child of a <complink name="Test Action"/> Sampler.
 	</p>
 </description>
 <component name="Constant Timer" index="&sect-num;.6.1" anchor="constant"  width="390" height="100" screenshot="timers/constant_timer.gif">
 <description>
 <p>If you want to have each thread pause for the same amount of time between
 requests, use this timer.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree.</property>
         <property name="Thread Delay" required="Yes">Number of milliseconds to pause.</property>
 </properties>
 </component>
 
 <component name="Gaussian Random Timer" index="&sect-num;.6.2"  width="390" height="182" screenshot="timers/gauss_random_timer.gif">
 
 <description><p>This timer pauses each thread request for a random amount of time, with most
 of the time intervals ocurring near a particular value.  The total delay is the
 sum of the Gaussian distributed value (with mean 0.0 and standard deviation 1.0) times
 the deviation value you specify, and the offset value.</p></description>
 
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree</property>
   <property name="Deviation" required="Yes">Deviation in milliseconds.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Uniform Random Timer" index="&sect-num;.6.3"  width="390" height="182" screenshot="timers/uniform_random_timer.gif">
 
 <description><p>This timer pauses each thread request for a random amount of time, with
 each time interval having the same probability of occurring. The total delay
 is the sum of the random value and the offset value.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Random Delay Maximum" required="Yes">Maxium random number of milliseconds to
 pause.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Constant Throughput Timer" index="&sect-num;.6.4"  width="542" height="155" screenshot="timers/constant_throughput_timer.png">
 
 <description><p>This timer introduces variable pauses, calculated to keep the total throughput (in terms of samples per minute) as close as possible to a give figure. Of course the throughput will be lower if the server is not capable of handling it, or if other timers or time-consuming test elements prevent it.</p>
 <p>
 N.B. although the Timer is called the Constant Throughput timer, the throughput value does not need to be constant.
 It can be defined in terms of a variable or function call, and the value can be changed during a test.
 The value can be changed in various ways:
 </p>
 <ul>
 <li>using a counter variable</li>
 <li>using a JavaScript or BeanShell function to provide a changing value</li>
 <li>using the remote BeanShell server to change a JMeter property</li>
 </ul>
 <p>See <a href="best-practices.html">Best Practices</a> for further details.
 Note that the throughput value should not be changed too often during a test
 - it will take a while for the new value to take effect.
 </p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Target Throughput" required="Yes">Throughput we want the timer to try to generate.</property>
   <property name="Calculate Throughput based on" required="Yes">
    <ul>
     <li>this thread only - each thread will try to maintain the target throughput. The overall throughput will be proportional to the number of active threads.</li>
     <li>all active threads in current thread group - the target throughput is divided amongst all the active threads in the group. 
     Each thread will delay as needed, based on when it last ran.</li>
     <li>all active threads - the target throughput is divided amongst all the active threads in all Thread Groups.
     Each thread will delay as needed, based on when it last ran.
     In this case, each other Thread Group will need a Constant Throughput timer with the same settings.</li>
     <li>all active threads in current thread group (shared) - as above, but each thread is delayed based on when any thread in the group last ran.</li>
     <li>all active threads (shared) - as above; each thread is delayed based on when any thread last ran.</li>
    </ul>
   </property>
   <p>The shared and non-shared algorithms both aim to generate the desired thoughput, and will produce similar results.
   The shared algorithm should generate a more accurate overall transaction rate.
   The non-shared algortihm should generate a more even spread of transactions across threads.</p>
 </properties>
