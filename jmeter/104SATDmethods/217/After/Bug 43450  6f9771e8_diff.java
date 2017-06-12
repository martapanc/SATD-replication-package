diff --git a/bin/testfiles/BatchTestLocal.csv b/bin/testfiles/BatchTestLocal.csv
index 0cf7fa2d7..a207b02dd 100644
--- a/bin/testfiles/BatchTestLocal.csv
+++ b/bin/testfiles/BatchTestLocal.csv
@@ -1,19 +1,19 @@
-label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,grpThreads,allThreads,URL,Filename,SampleCount
-Java 1,200,OK,Thread Group 1-1,text,true,,10,1,1,null,,1
-Java 1,200,OK,Thread Group 1-1,text,true,,10,1,1,null,,1
-Java 1,200,OK,Thread Group 1-1,text,true,,10,1,1,null,,1
-If Test,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Loop,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Module,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Loop,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Module,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Loop,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Module,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Java If once 1,,,Thread Group 1-1,,false,,0,1,1,null,,1
-Java If once 2,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Java If all 1,,,Thread Group 1-1,,false,,0,1,1,null,,1
-Java OK,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Java If once 1,,,Thread Group 1-1,,false,,0,1,1,null,,1
-Java If once 2,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
-Java If all 1,,,Thread Group 1-1,,false,,0,1,1,null,,1
-Java OK,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1
+label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,grpThreads,allThreads,URL,Filename,SampleCount,ErrorCount
+Java 1,200,OK,Thread Group 1-1,text,true,,10,1,1,null,,1,0
+Java 1,200,OK,Thread Group 1-1,text,true,,10,1,1,null,,1,0
+Java 1,200,OK,Thread Group 1-1,text,true,,10,1,1,null,,1,0
+If Test,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Loop,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Module,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Loop,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Module,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Loop,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Module,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Java If once 1,,,Thread Group 1-1,,false,,0,1,1,null,,1,1
+Java If once 2,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Java If all 1,,,Thread Group 1-1,,false,,0,1,1,null,,1,1
+Java OK,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Java If once 1,,,Thread Group 1-1,,false,,0,1,1,null,,1,1
+Java If once 2,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
+Java If all 1,,,Thread Group 1-1,,false,,0,1,1,null,,1,1
+Java OK,200,OK,Thread Group 1-1,,true,,0,1,1,null,,1,0
diff --git a/bin/testfiles/BatchTestLocal.xml b/bin/testfiles/BatchTestLocal.xml
index d7cfcea89..5a456765a 100644
--- a/bin/testfiles/BatchTestLocal.xml
+++ b/bin/testfiles/BatchTestLocal.xml
@@ -1,133 +1,133 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <testResults version="1.2">
-<sample s="true" lb="Java 1" rc="200" rm="OK" tn="Thread Group 1-1" dt="text" by="10" sc="1" ng="1" na="1">
+<sample s="true" lb="Java 1" rc="200" rm="OK" tn="Thread Group 1-1" dt="text" by="10" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String">ResultData</responseData>
   <responseFile class="java.lang.String"></responseFile>
   <samplerData class="java.lang.String">SamplerData</samplerData>
   <null/>
 </sample>
-<sample s="true" lb="Java 1" rc="200" rm="OK" tn="Thread Group 1-1" dt="text" by="10" sc="1" ng="1" na="1">
+<sample s="true" lb="Java 1" rc="200" rm="OK" tn="Thread Group 1-1" dt="text" by="10" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String">ResultData</responseData>
   <responseFile class="java.lang.String"></responseFile>
   <samplerData class="java.lang.String">SamplerData</samplerData>
   <null/>
 </sample>
-<sample s="true" lb="Java 1" rc="200" rm="OK" tn="Thread Group 1-1" dt="text" by="10" sc="1" ng="1" na="1">
+<sample s="true" lb="Java 1" rc="200" rm="OK" tn="Thread Group 1-1" dt="text" by="10" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String">ResultData</responseData>
   <responseFile class="java.lang.String"></responseFile>
   <samplerData class="java.lang.String">SamplerData</samplerData>
   <null/>
 </sample>
-<sample s="true" lb="If Test" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="If Test" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Loop" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Loop" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Module" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Module" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Loop" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Loop" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Module" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Module" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Loop" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Loop" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Module" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Module" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="false" lb="Java If once 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="false" lb="Java If once 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="1" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Java If once 2" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Java If once 2" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="false" lb="Java If all 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="false" lb="Java If all 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="1" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Java OK" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Java OK" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="false" lb="Java If once 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="false" lb="Java If once 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="1" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Java If once 2" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Java If once 2" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="false" lb="Java If all 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="false" lb="Java If all 1" rc="" rm="" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="1" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
-<sample s="true" lb="Java OK" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ng="1" na="1">
+<sample s="true" lb="Java OK" rc="200" rm="OK" tn="Thread Group 1-1" dt="" by="0" sc="1" ec="0" ng="1" na="1">
   <responseHeader class="java.lang.String"></responseHeader>
   <requestHeader class="java.lang.String"></requestHeader>
   <responseData class="java.lang.String"/>
   <responseFile class="java.lang.String"></responseFile>
   <null/>
 </sample>
 
 </testResults>
diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index d8840360e..ac34f543f 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,1129 +1,1130 @@
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
 import java.lang.Character;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
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
 
 	private static final String JSON_BUTTON_LABEL = "Render JSON";
 
 	private static final String XML_BUTTON_LABEL = "Render XML";
 
 	private static final String TEXT_BUTTON_LABEL = "Show Text";
 
 	private static final String TEXT_HTML = "text/html"; // $NON-NLS-1$
 
 	private static final String HTML_COMMAND = "html"; // $NON-NLS-1$
 
 	private static final String JSON_COMMAND = "json"; // $NON-NLS-1$
 
 	private static final String XML_COMMAND = "xml"; // $NON-NLS-1$
 
 	private static final String TEXT_COMMAND = "text"; // $NON-NLS-1$
 
 	private static final String STYLE_SERVER_ERROR = "ServerError"; // $NON-NLS-1$
 
 	private static final String STYLE_CLIENT_ERROR = "ClientError"; // $NON-NLS-1$
 
 	private static final String STYLE_REDIRECT = "Redirect"; // $NON-NLS-1$
 
 	private boolean textMode = true;
 
 	private static final String ESC_CHAR_REGEX = "\\\\[\"\\\\/bfnrt]|\\\\u[0-9A-Fa-f]{4}"; // $NON-NLS-1$
 
 	private static final String NORMAL_CHARACTER_REGEX = "[^\"\\\\]";  // $NON-NLS-1$
 
 	private static final String STRING_REGEX = "\"(" + ESC_CHAR_REGEX + "|" + NORMAL_CHARACTER_REGEX + ")*\""; // $NON-NLS-1$
 
 	// This 'other value' regex is deliberately weak, even accepting an empty string, to be useful when reporting malformed data.
 	private static final String OTHER_VALUE_REGEX = "[^\\{\\[\\]\\}\\,]*"; // $NON-NLS-1$
 
 	private static final String VALUE_OR_PAIR_REGEX = "((" + STRING_REGEX + "\\s*:)?\\s*(" + STRING_REGEX + "|" + OTHER_VALUE_REGEX + ")\\s*,?\\s*)"; // $NON-NLS-1$
 
 	private static final Pattern VALUE_OR_PAIR_PATTERN = Pattern.compile(VALUE_OR_PAIR_REGEX);
 
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
 
 	private JRadioButton jsonButton;
 
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
 						statsBuff.append("Sample Count: ").append(res.getSampleCount()).append(NL);
+						statsBuff.append("Error Count: ").append(res.getErrorCount()).append(NL);
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
 							} else if (command.equals(JSON_COMMAND)) {
 								showRenderJSONResponse(response);
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
 		jsonButton.setEnabled(false);
 		xmlButton.setEnabled(false);
 	}
 
 	protected void showTextResponse(String response) {
 		results.setContentType("text/plain"); // $NON-NLS-1$
 		results.setText(response == null ? "" : response); // $NON-NLS-1$
 		results.setCaretPosition(0);
 		resultsScrollPane.setViewportView(results);
 
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		jsonButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	// It might be useful also to make this available in the 'Request' tab, for
 	// when posting JSON.
 	private static String prettyJSON(String json) {
 		StringBuffer pretty = new StringBuffer(json.length() * 2); // Educated guess
 
 		final String tab = ":   "; // $NON-NLS-1$
 		StringBuffer index = new StringBuffer();
 		String nl = ""; // $NON-NLS-1$
 
 		Matcher valueOrPair = VALUE_OR_PAIR_PATTERN.matcher(json);
 
 		boolean misparse = false;
 
 		for (int i = 0; i < json.length(); ) {
 			final char currentChar = json.charAt(i);
 			if ((currentChar == '{') || (currentChar == '[')) {
 				pretty.append(nl).append(index).append(currentChar);
 				i++;
 				index.append(tab);
 				misparse = false;
 			}
 			else if ((currentChar == '}') || (currentChar == ']')) {
 				if (index.length() > 0) {
 					index.delete(0, tab.length());
 				}
 				pretty.append(nl).append(index).append(currentChar);
 				i++;
 				int j = i;
 				while ((j < json.length()) && Character.isWhitespace(json.charAt(j))) {
 					j++;
 				}
 				if ((j < json.length()) && (json.charAt(j) == ',')) {
 					pretty.append(","); // $NON-NLS-1$
 					i=j+1;
 				}
 				misparse = false;
 			}
 			else if (valueOrPair.find(i) && valueOrPair.group().length() > 0) {
 				pretty.append(nl).append(index).append(valueOrPair.group());
 				i=valueOrPair.end();
 				misparse = false;
 			}
 			else {
 				if (!misparse) {
 					pretty.append(nl).append("- Parse failed from:");
 				}
 				pretty.append(currentChar);
 				i++;
 				misparse = true;
 			}
 			nl = "\n"; // $NON-NLS-1$
 		}
 		return pretty.toString();
 	}
 	
 	private void showRenderJSONResponse(String response) {
 		results.setContentType("text/plain"); // $NON-NLS-1$
 		results.setText(response == null ? "" : prettyJSON(response));
 		results.setCaretPosition(0);
 		resultsScrollPane.setViewportView(results);
 
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		jsonButton.setEnabled(true);
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
 		jsonButton.setEnabled(true);
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
 				&& (command.equals(TEXT_COMMAND) || command.equals(HTML_COMMAND)
  				|| command.equals(JSON_COMMAND) || command.equals(XML_COMMAND))) {
 
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
 			} else if (command.equals(JSON_COMMAND)) {
 				showRenderJSONResponse(response);
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
 		jsonButton.setEnabled(true);
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
 
 		jsonButton = new JRadioButton(JSON_BUTTON_LABEL);
 		jsonButton.setActionCommand(JSON_COMMAND);
 		jsonButton.addActionListener(this);
 		jsonButton.setSelected(!textMode);
 		group.add(jsonButton);
 
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
 		pane.add(jsonButton);
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
diff --git a/src/core/org/apache/jmeter/samplers/SampleResult.java b/src/core/org/apache/jmeter/samplers/SampleResult.java
index 2f3c2cfce..8ad805b6c 100644
--- a/src/core/org/apache/jmeter/samplers/SampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/SampleResult.java
@@ -1,948 +1,976 @@
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
 
 // For unit tests, @see TestSampleResult
 
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
     // needs to be accessible from test code
 	static final String DEFAULT_ENCODING 
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
 	
 	private volatile int groupThreads = 0; // Active threads in this thread group
 	
 	private volatile int allThreads = 0; // Active threads in all thread groups
 
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
 
 		setGroupThreads(res.getGroupThreads());
 		setAllThreads(res.getAllThreads());
 
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
 		// Extend the time to the end of the added sample
 		setEndTime(subResult.getEndTime());
 		// Include the byte count for the added sample
 		setBytes(getBytes() + subResult.getBytes());
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
             final String CS_PFX = "charset="; // $NON-NLS-1$
             int cset = ct.toLowerCase().indexOf(CS_PFX);
             if (cset >= 0) {
             	// TODO - assumes charset is not followed by anything else
                 String charSet = ct.substring(cset + CS_PFX.length());
                 // Check for quoted string
                 if (charSet.startsWith("\"")){ // $NON-NLS-1$
                 	setDataEncoding(charSet.substring(1, charSet.length()-1)); // remove quotes
                 } else {
 				    setDataEncoding(charSet);
                 }
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
 	 * @return the sample count
 	 */
 	public int getSampleCount() {
 		return sampleCount;
 	}
 
 	/**
+	 * Returns the count of errors.
+	 * 
+	 * @return 0 - or 1 if the sample failed
+	 */
+	public int getErrorCount(){
+		return success ? 0 : 1;
+	}
+	
+	public void setErrorCount(int i){// for reading from CSV files
+		// ignored currently
+	}
+	/*
+	 * TODO: error counting needs to be sorted out after 2.3 final.
+	 * At present the Statistical Sampler tracks errors separately
+	 * It would make sense to move the error count here, but this would
+	 * mean lots of changes.
+	 * It's also tricky maintaining the count - it can't just be incremented/decremented
+	 * when the success flag is set as this may be done multiple times.
+	 * The work-round for now is to do the work in the StatisticalSampleResult,
+	 * which overrides this method.
+	 * Note that some JMS samplers also create samples with > 1 sample count
+	 * Also the Transaction Controller probably needs to be changed to do
+	 * proper sample and error accounting.
+	 * The purpose of this work-round is to allow at least minimal support for
+	 * errors in remote statistical batch mode.
+	 * 
+	 */
+	/**
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
 	 * @return byte count
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
 
 	public int getGroupThreads() {
 		return groupThreads;
 	}
 
 	public void setGroupThreads(int n) {
 		this.groupThreads = n;
 	}
 
 	public int getAllThreads() {
 		return allThreads;
 	}
 
 	public void setAllThreads(int n) {
 		this.allThreads = n;
 	}
 }
diff --git a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
index 593f5993e..4130d14fc 100644
--- a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
@@ -1,86 +1,114 @@
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
 
 /**
- * @author Lars Krog-Jensen
- *         Created: 2005-okt-04
+ * Aggregates sample results for use by the Statistical remote batch mode.
+ * Samples are aggregated by the key defined by getKey().
+ * TODO: merge error count into parent class? 
  */
 public class StatisticalSampleResult extends SampleResult implements
 		Serializable {
    
-	private static final long serialVersionUID = 23L;
+	private static final long serialVersionUID = 24L;
 
 	private int errorCount;
 
     public StatisticalSampleResult(){// May be called by XStream
     }
     
+	/**
+	 * Allow OldSaveService to generate a suitable result when sample/error counts have been saved.
+	 * 
+	 * @deprecated Needs to be replaced when multiple sample results are sorted out
+	 * 
+	 * @param stamp
+	 * @param elapsed
+	 */
+	public StatisticalSampleResult(long stamp, long elapsed) {
+		super(stamp, elapsed);
+	}
+
 	public StatisticalSampleResult(SampleResult res) {
-		// Copy data that is shared between samples:
+		// Copy data that is shared between samples (i.e. the key items):
 		setSampleLabel(res.getSampleLabel());
 		setThreadName(res.getThreadName());
+
 		setSuccessful(true); // Assume result is OK
 		setSampleCount(0); // because we add the sample count in later
 	}
 
 	public void add(SampleResult res) {
 		// Add Sample Counter
 		setSampleCount(getSampleCount() + res.getSampleCount());
 
 		setBytes(getBytes() + res.getBytes());
 
 		// Add Error Counter
 		if (!res.isSuccessful()) {
 			errorCount++;
+			this.setSuccessful(false);
 		}
 
 		// Set start/end times
         if (getStartTime()==0){ // Bug 40954 - ensure start time gets started!
             this.setStartTime(res.getStartTime());
         } else {
 		    this.setStartTime(Math.min(getStartTime(), res.getStartTime()));
         }
 		this.setEndTime(Math.max(getEndTime(), res.getEndTime()));
 		
 		setLatency(getLatency()+ res.getLatency());
 
 	}
 
 	public long getTime() {
 		return getEndTime() - getStartTime() - this.getIdleTime();
 	}
 
 	public long getTimeStamp() {
 		return getEndTime();
 	}
 
-	public int getErrorCount() {
+	public int getErrorCount() {// Overrides SampleResult
 		return errorCount;
 	}
 
-	public static String getKey(SampleEvent event) {
-		String key = event.getResult().getSampleLabel() + "-"
-				+ event.getThreadGroup();
+	public void setErrorCount(int e) {// for reading CSV files
+		errorCount = e;
+	}
 
-		return key;
+	/**
+	 * Generates the key to be used for aggregating samples as follows:<br/>
+	 * <code>sampleLabel</code> "-" <code>threadGroup</code>
+	 * 
+	 * N.B. the key should agree with the fixed items that are saved in the sample.
+	 * 
+	 * @param event sample event whose key is to be calculated
+	 * @return the key to use for aggregating samples
+	 */
+	public static String getKey(SampleEvent event) {
+		SampleResult result = event.getResult();
+		StringBuffer sb = new StringBuffer(80);
+		sb.append(result.getSampleLabel()).append("-").append(result.getThreadName());
+		return sb.toString();
 	}
 }
diff --git a/src/core/org/apache/jmeter/save/OldSaveService.java b/src/core/org/apache/jmeter/save/OldSaveService.java
index 7d4dadfd0..7fb9d057a 100644
--- a/src/core/org/apache/jmeter/save/OldSaveService.java
+++ b/src/core/org/apache/jmeter/save/OldSaveService.java
@@ -1,989 +1,1004 @@
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
 
 package org.apache.jmeter.save;
 
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Collection;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Vector;
 
 import org.apache.avalon.framework.configuration.Configuration;
 import org.apache.avalon.framework.configuration.ConfigurationException;
 import org.apache.avalon.framework.configuration.DefaultConfiguration;
 import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
 import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
+import org.apache.jmeter.samplers.StatisticalSampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.MapProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.NameUpdater;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.log.Logger;
 import org.xml.sax.SAXException;
 
 /**
  * This class provides a means for saving test results. Test results are
  * typically saved in an XML file, but other storage mechanisms may also be
  * used, for instance, CSV files or databases.
  * 
  * @version $Revision$ $Date$
  */
 public final class OldSaveService {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
     // ---------------------------------------------------------------------
     // XML RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     // Shared with TestElementSaver
     final static String PRESERVE = "preserve"; // $NON-NLS-1$
     final static String XML_SPACE = "xml:space"; // $NON-NLS-1$
 
     private static final String ASSERTION_RESULT_TAG_NAME = "assertionResult"; // $NON-NLS-1$
     private static final String BINARY = "binary"; // $NON-NLS-1$
     private static final String DATA_TYPE = "dataType"; // $NON-NLS-1$
     private static final String ERROR = "error"; // $NON-NLS-1$
     private static final String FAILURE = "failure"; // $NON-NLS-1$
     private static final String FAILURE_MESSAGE = "failureMessage"; // $NON-NLS-1$
     private static final String LABEL = "label"; // $NON-NLS-1$
     private static final String RESPONSE_CODE = "responseCode"; // $NON-NLS-1$
     private static final String RESPONSE_MESSAGE = "responseMessage"; // $NON-NLS-1$
     private static final String SAMPLE_RESULT_TAG_NAME = "sampleResult"; // $NON-NLS-1$
     private static final String SUCCESSFUL = "success"; // $NON-NLS-1$
     private static final String THREAD_NAME = "threadName"; // $NON-NLS-1$
     private static final String TIME = "time"; // $NON-NLS-1$
     private static final String TIME_STAMP = "timeStamp"; // $NON-NLS-1$
 
     // ---------------------------------------------------------------------
     // ADDITIONAL CSV RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     private static final String CSV_ELAPSED = "elapsed"; // $NON-NLS-1$
     private static final String CSV_BYTES= "bytes"; // $NON-NLS-1$
     private static final String CSV_THREAD_COUNT1 = "grpThreads"; // $NON-NLS-1$
     private static final String CSV_THREAD_COUNT2 = "allThreads"; // $NON-NLS-1$
     private static final String CSV_SAMPLE_COUNT = "SampleCount"; // $NON-NLS-1$
+    private static final String CSV_ERROR_COUNT = "ErrorCount"; // $NON-NLS-1$
     private static final String CSV_URL = "URL"; // $NON-NLS-1$
     private static final String CSV_FILENAME = "Filename"; // $NON-NLS-1$
     private static final String CSV_LATENCY = "Latency"; // $NON-NLS-1$
     private static final String CSV_ENCODING = "Encoding"; // $NON-NLS-1$
     
     // Initial config from properties
 	static private final SampleSaveConfiguration _saveConfig = SampleSaveConfiguration.staticConfig();
 
 	// Date format to try if the time format does not parse as milliseconds
 	// (this is the suggested value in jmeter.properties)
 	private static final String DEFAULT_DATE_FORMAT_STRING = "MM/dd/yy HH:mm:ss"; // $NON-NLS-1$
 	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STRING);
 
 	private static DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
 
 	/**
 	 * Private constructor to prevent instantiation.
 	 */
 	private OldSaveService() {
 	}
 
     //////////////////////////////////////////////////////////////////////////////
     //                  Start of CSV methods
     
     // TODO - move to separate file? If so, remember that some of the
       
     /**
      * Make a SampleResult given a delimited string.
      * 
      * @param inputLine - line from CSV file
      * @param saveConfig - configuration
      * @param lineNumber - line number for error reporting
      * @return SampleResult or null if header line detected
      * 
      * @throws JMeterError
      */
     public static SampleResult makeResultFromDelimitedString(
     		final String inputLine, 
     		final SampleSaveConfiguration saveConfig, // may be updated
     		final long lineNumber) {
  
     	SampleResult result = null;
 		long timeStamp = 0;
 		long elapsed = 0;
 		/*
 		 * Bug 40772: replaced StringTokenizer with String.split(), as the
 		 * former does not return empty tokens.
 		 */
 		// The \Q prefix is needed to ensure that meta-characters (e.g. ".") work.
 		String parts[]=inputLine.split("\\Q"+_saveConfig.getDelimiter());// $NON-NLS-1$
 		String text = null;
 		String field = null; // Save the name for error reporting
 		int i=0;
 
 		try {
 			if (saveConfig.saveTimestamp()){
 				field = TIME_STAMP;
 				text = parts[i++];
 				if (saveConfig.printMilliseconds()) {
 					try {
 						timeStamp = Long.parseLong(text);
 					} catch (NumberFormatException e) {// see if this works
 						log.warn(e.toString());
 						Date stamp = DEFAULT_DATE_FORMAT.parse(text);
 						timeStamp = stamp.getTime();
 						log.warn("Setting date format to: "+DEFAULT_DATE_FORMAT_STRING);
 						saveConfig.setFormatter(DEFAULT_DATE_FORMAT);
 					}
 				} else if (saveConfig.formatter() != null) {
 					Date stamp = saveConfig.formatter().parse(text);
 					timeStamp = stamp.getTime();
 				} else { // can this happen?
 					final String msg = "Unknown timestamp format";
 					log.warn(msg);
 					throw new JMeterError(msg);
 				}
 			}
 
 			if (saveConfig.saveTime()) {
 				field = CSV_ELAPSED;
 				text = parts[i++];
 				elapsed = Long.parseLong(text);
 			}
 
-			result = new SampleResult(timeStamp, elapsed);
+			if (saveConfig.saveSampleCount()) {
+				result = new StatisticalSampleResult(timeStamp, elapsed);
+			} else {
+				result = new SampleResult(timeStamp, elapsed);
+			}
 
 			if (saveConfig.saveLabel()) {
 				field = LABEL;
 				text = parts[i++];
 				result.setSampleLabel(text);
 			}
 			if (saveConfig.saveCode()) {
 				field = RESPONSE_CODE;
 				text = parts[i++];
 				result.setResponseCode(text);
 			}
 
 			if (saveConfig.saveMessage()) {
 				field = RESPONSE_MESSAGE;
 				text = parts[i++];
 				result.setResponseMessage(text);
 			}
 
 			if (saveConfig.saveThreadName()) {
 				field = THREAD_NAME;
 				text = parts[i++];
 				result.setThreadName(text);
 			}
 
 			if (saveConfig.saveDataType()) {
 				field = DATA_TYPE;
 				text = parts[i++];
 				result.setDataType(text);
 			}
 
 			if (saveConfig.saveSuccess()) {
 				field = SUCCESSFUL;
 				text = parts[i++];
 				result.setSuccessful(Boolean.valueOf(text).booleanValue());
 			}
 
 			if (saveConfig.saveAssertionResultsFailureMessage()) {
 				i++;
                 // TODO - should this be restored?
 			}
             
             if (saveConfig.saveBytes()) {
             	field = CSV_BYTES;
                 text = parts[i++];
                 result.setBytes(Integer.parseInt(text));
             }
         
             if (saveConfig.saveThreadCounts()) {
             	field = CSV_THREAD_COUNT1;
                 text = parts[i++];
                 result.setGroupThreads(Integer.parseInt(text));
                 
             	field = CSV_THREAD_COUNT2;
                 text = parts[i++];
                 result.setAllThreads(Integer.parseInt(text));
             }
 
             if (saveConfig.saveUrl()) {
                 i++;
                 // TODO: should this be restored?
             }
         
             if (saveConfig.saveFileName()) {
             	field = CSV_FILENAME;
                 text = parts[i++];
                 result.setResultFileName(text);
             }            
             if (saveConfig.saveLatency()) {
             	field = CSV_LATENCY;
                 text = parts[i++];
                 result.setLatency(Long.parseLong(text));
             }
 
             if (saveConfig.saveEncoding()) {
             	field = CSV_ENCODING;
                 text = parts[i++];
                 result.setEncodingAndType(text);
             }
 
-            if (saveConfig.saveEncoding()) {
+            if (saveConfig.saveSampleCount()) {
             	field = CSV_SAMPLE_COUNT;
                 text = parts[i++];
                 result.setSampleCount(Integer.parseInt(text));
+            	field = CSV_ERROR_COUNT;
+                text = parts[i++];
+                result.setErrorCount(Integer.parseInt(text));
             }
 
             
 		} catch (NumberFormatException e) {
 			log.warn("Error parsing field '" + field + "' at line " + lineNumber + " " + e);
 			throw new JMeterError(e);
 		} catch (ParseException e) {
 			log.warn("Error parsing field '" + field + "' at line " + lineNumber + " " + e);
 			throw new JMeterError(e);
 		} catch (ArrayIndexOutOfBoundsException e){
 			log.warn("Insufficient columns to parse field '" + field + "' at line " + lineNumber);
 			throw new JMeterError(e);
 		}
 		return result;
 	}
 
     /**
      * Generates the field names for the output file
      * 
      * @return the field names as a string
      */
     public static String printableFieldNamesToString() {
         return printableFieldNamesToString(_saveConfig);
     }
     
 	/**
 	 * Generates the field names for the output file
 	 * 
 	 * @return the field names as a string
 	 */
 	public static String printableFieldNamesToString(SampleSaveConfiguration saveConfig) {
 		StringBuffer text = new StringBuffer();
 		String delim = saveConfig.getDelimiter();
 
 		if (saveConfig.saveTimestamp()) {
 			text.append(TIME_STAMP);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveTime()) {
 			text.append(CSV_ELAPSED);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveLabel()) {
 			text.append(LABEL);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveCode()) {
 			text.append(RESPONSE_CODE);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveMessage()) {
 			text.append(RESPONSE_MESSAGE);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveThreadName()) {
 			text.append(THREAD_NAME);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveDataType()) {
 			text.append(DATA_TYPE);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveSuccess()) {
 			text.append(SUCCESSFUL);
 			text.append(delim);
 		}
 
 		if (saveConfig.saveAssertionResultsFailureMessage()) {
 			text.append(FAILURE_MESSAGE);
 			text.append(delim);
 		}
 
         if (saveConfig.saveBytes()) {
             text.append(CSV_BYTES);
             text.append(delim);
         }
 
         if (saveConfig.saveThreadCounts()) {
             text.append(CSV_THREAD_COUNT1);
             text.append(delim);
             text.append(CSV_THREAD_COUNT2);
             text.append(delim);
         }
 
         if (saveConfig.saveUrl()) {
             text.append(CSV_URL);
             text.append(delim);
         }
 
         if (saveConfig.saveFileName()) {
             text.append(CSV_FILENAME);
             text.append(delim);
         }
 
         if (saveConfig.saveLatency()) {
             text.append(CSV_LATENCY);
             text.append(delim);
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(CSV_ENCODING);
             text.append(delim);
         }
 
 		if (saveConfig.saveSampleCount()) {
 			text.append(CSV_SAMPLE_COUNT);
 			text.append(delim);
+			text.append(CSV_ERROR_COUNT);
+			text.append(delim);
 		}
 
 		String resultString = null;
 		int size = text.length();
 		int delSize = delim.length();
 
 		// Strip off the trailing delimiter
 		if (size >= delSize) {
 			resultString = text.substring(0, size - delSize);
 		} else {
 			resultString = text.toString();
 		}
 		return resultString;
 	}
 	
 	// Map header names to set() methods
 	private static final Map headerLabelMethods = new HashMap();
 	
 	static {
 		    headerLabelMethods.put(TIME_STAMP, new Functor("setTimestamp"));
 			headerLabelMethods.put(CSV_ELAPSED, new Functor("setTime"));
 			headerLabelMethods.put(LABEL, new Functor("setLabel"));
 			headerLabelMethods.put(RESPONSE_CODE, new Functor("setCode"));
 			headerLabelMethods.put(RESPONSE_MESSAGE, new Functor("setMessage"));
 			headerLabelMethods.put(THREAD_NAME, new Functor("setThreadName"));
 			headerLabelMethods.put(DATA_TYPE, new Functor("setDataType"));
 			headerLabelMethods.put(SUCCESSFUL, new Functor("setSuccess"));
 			headerLabelMethods.put(FAILURE_MESSAGE, new Functor("setAssertionResultsFailureMessage"));
             headerLabelMethods.put(CSV_BYTES, new Functor("setBytes"));
             headerLabelMethods.put(CSV_URL, new Functor("setUrl"));
             headerLabelMethods.put(CSV_FILENAME, new Functor("setFileName"));
             headerLabelMethods.put(CSV_LATENCY, new Functor("setLatency"));
             headerLabelMethods.put(CSV_ENCODING, new Functor("setEncoding"));
             // Both these are needed in the list even though they set the same variable
             headerLabelMethods.put(CSV_THREAD_COUNT1,new Functor("setThreadCounts"));
             headerLabelMethods.put(CSV_THREAD_COUNT2,new Functor("setThreadCounts"));
+            // Both these are needed in the list even though they set the same variable
             headerLabelMethods.put(CSV_SAMPLE_COUNT, new Functor("setSampleCount"));
+            headerLabelMethods.put(CSV_ERROR_COUNT, new Functor("setSampleCount"));
 	}
 
 	/**
 	 * Parse a CSV header line
 	 * @param headerLine from CSV file
 	 * @return config corresponding to the header items found or null if not a header line
 	 */
 	public static SampleSaveConfiguration getSampleSaveConfiguration(String headerLine){
 		String parts[]=headerLine.split("\\Q"+_saveConfig.getDelimiter());// $NON-NLS-1$
 
 		// Check if the line is a header
 		for(int i=0;i<parts.length;i++){
 			if (!headerLabelMethods.containsKey(parts[i])){
 				return null; // unknown column name
 			}
 		}
 
 		// We know the column names all exist, so create the config 
 		SampleSaveConfiguration saveConfig=new SampleSaveConfiguration(false);
 		
 		for(int i=0;i<parts.length;i++){
 			Functor set = (Functor) headerLabelMethods.get(parts[i]);
 			set.invoke(saveConfig,new Boolean[]{Boolean.TRUE});
 		}
 		return saveConfig;
 	}
 
 	/**
      * Method will save aggregate statistics as CSV. For now I put it here.
      * Not sure if it should go in the newer SaveService instead of here.
      * if we ever decide to get rid of this class, we'll need to move this
      * method to the new save service.
      * @param data
      * @param writer
      * @throws IOException
      */
     public static void saveCSVStats(Vector data, FileWriter writer) throws IOException {
         for (int idx=0; idx < data.size(); idx++) {
             Vector row = (Vector)data.elementAt(idx);
             for (int idy=0; idy < row.size(); idy++) {
                 if (idy > 0) {
                     writer.write(","); // $NON-NLS-1$
                 }
                 Object item = row.elementAt(idy);
                 writer.write( String.valueOf(item) );
             }
             writer.write(System.getProperty("line.separator")); // $NON-NLS-1$
         }
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by the default delimiter.
      * 
      * @param sample
      *            the test result to be converted
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleResult sample) {
     	return resultToDelimitedString(sample, sample.getSaveConfig().getDelimiter());
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by a specified String.
      * 
      * @param sample
      *            the test result to be converted
      * @param delimiter
      *            the separation string
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleResult sample, String delimiter) {
     	StringBuffer text = new StringBuffer();
     	SampleSaveConfiguration saveConfig = sample.getSaveConfig();
     
     	if (saveConfig.saveTimestamp()) {
     		if (saveConfig.printMilliseconds()){
     			text.append(sample.getTimeStamp());
     			text.append(delimiter);
     		} else if (saveConfig.formatter() != null) {
     			String stamp = saveConfig.formatter().format(new Date(sample.getTimeStamp()));
     			text.append(stamp);
     			text.append(delimiter);
     		}
     	}
     
     	if (saveConfig.saveTime()) {
     		text.append(sample.getTime());
     		text.append(delimiter);
     	}
     
     	if (saveConfig.saveLabel()) {
     		text.append(sample.getSampleLabel());
     		text.append(delimiter);
     	}
     
     	if (saveConfig.saveCode()) {
     		text.append(sample.getResponseCode());
     		text.append(delimiter);
     	}
     
     	if (saveConfig.saveMessage()) {
     		text.append(sample.getResponseMessage());
     		text.append(delimiter);
     	}
     
     	if (saveConfig.saveThreadName()) {
     		text.append(sample.getThreadName());
     		text.append(delimiter);
     	}
     
     	if (saveConfig.saveDataType()) {
     		text.append(sample.getDataType());
     		text.append(delimiter);
     	}
     
     	if (saveConfig.saveSuccess()) {
     		text.append(sample.isSuccessful());
     		text.append(delimiter);
     	}
     
     	if (saveConfig.saveAssertionResultsFailureMessage()) {
     		String message = null;
     		AssertionResult[] results = sample.getAssertionResults();
     
     		if (results != null) {
     			// Find the first non-null message
     			for (int i = 0; i < results.length; i++){
         			message = results[i].getFailureMessage();
     				if (message != null) break;
     			}
     		}
     
     		if (message != null) {
     			text.append(message);
     		}
     		text.append(delimiter);
     	}
     
         if (saveConfig.saveBytes()) {
             text.append(sample.getBytes());
             text.append(delimiter);
         }
     
         if (saveConfig.saveThreadCounts()) {
             text.append(sample.getGroupThreads());
             text.append(delimiter);
             text.append(sample.getAllThreads());
             text.append(delimiter);
         }
         if (saveConfig.saveUrl()) {
             text.append(sample.getURL());
             text.append(delimiter);
         }
     
         if (saveConfig.saveFileName()) {
             text.append(sample.getResultFileName());
             text.append(delimiter);
         }
     
         if (saveConfig.saveLatency()) {
             text.append(sample.getLatency());
             text.append(delimiter);
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(sample.getDataEncoding());
             text.append(delimiter);
         }
 
-    	if (saveConfig.saveSampleCount()) {
+    	if (saveConfig.saveSampleCount()) {// Need both sample and error count to be any use
     		text.append(sample.getSampleCount());
     		text.append(delimiter);
+    		text.append(sample.getErrorCount());
+    		text.append(delimiter);
     	}
     
     	String resultString = null;
     	int size = text.length();
     	int delSize = delimiter.length();
     
     	// Strip off the trailing delimiter
     	if (size >= delSize) {
     		resultString = text.substring(0, size - delSize);
     	} else {
     		resultString = text.toString();
     	}
     	return resultString;
     }
 
     //              End of CSV methods
     //////////////////////////////////////////////////////////////////////////////////////////
     //              Start of Avalon methods
     
     public static void saveSubTree(HashTree subTree, OutputStream writer) throws IOException {
 		Configuration config = (Configuration) getConfigsFromTree(subTree).get(0);
 		DefaultConfigurationSerializer saver = new DefaultConfigurationSerializer();
 
 		saver.setIndent(true);
 		try {
 			saver.serialize(writer, config);
 		} catch (SAXException e) {
 			throw new IOException("SAX implementation problem");
 		} catch (ConfigurationException e) {
 			throw new IOException("Problem using Avalon Configuration tools");
 		}
 	}
     
     public static SampleResult getSampleResult(Configuration config) {
 		SampleResult result = new SampleResult(config.getAttributeAsLong(TIME_STAMP, 0L), config.getAttributeAsLong(
 				TIME, 0L));
 
 		result.setThreadName(config.getAttribute(THREAD_NAME, "")); // $NON-NLS-1$
 		result.setDataType(config.getAttribute(DATA_TYPE, ""));
 		result.setResponseCode(config.getAttribute(RESPONSE_CODE, "")); // $NON-NLS-1$
 		result.setResponseMessage(config.getAttribute(RESPONSE_MESSAGE, "")); // $NON-NLS-1$
 		result.setSuccessful(config.getAttributeAsBoolean(SUCCESSFUL, false));
 		result.setSampleLabel(config.getAttribute(LABEL, "")); // $NON-NLS-1$
 		result.setResponseData(getBinaryData(config.getChild(BINARY)));
 		Configuration[] subResults = config.getChildren(SAMPLE_RESULT_TAG_NAME);
 
 		for (int i = 0; i < subResults.length; i++) {
 			result.storeSubResult(getSampleResult(subResults[i]));
 		}
 		Configuration[] assResults = config.getChildren(ASSERTION_RESULT_TAG_NAME);
 
 		for (int i = 0; i < assResults.length; i++) {
 			result.addAssertionResult(getAssertionResult(assResults[i]));
 		}
 
 		Configuration[] samplerData = config.getChildren("property"); // $NON-NLS-1$
 		for (int i = 0; i < samplerData.length; i++) {
 			result.setSamplerData(samplerData[i].getValue("")); // $NON-NLS-1$
 		}
 		return result;
 	}
 
 	private static List getConfigsFromTree(HashTree subTree) {
 		Iterator iter = subTree.list().iterator();
 		List configs = new LinkedList();
 
 		while (iter.hasNext()) {
 			TestElement item = (TestElement) iter.next();
 			DefaultConfiguration config = new DefaultConfiguration("node", "node"); // $NON-NLS-1$ // $NON-NLS-2$
 
 			config.addChild(getConfigForTestElement(null, item));
 			List configList = getConfigsFromTree(subTree.getTree(item));
 			Iterator iter2 = configList.iterator();
 
 			while (iter2.hasNext()) {
 				config.addChild((Configuration) iter2.next());
 			}
 			configs.add(config);
 		}
 		return configs;
 	}
 
 	public static Configuration getConfiguration(byte[] bin) {
 		DefaultConfiguration config = new DefaultConfiguration(BINARY, "JMeter Save Service"); // $NON-NLS-1$
 
 		try {
 			config.setValue(new String(bin, "UTF-8")); // $NON-NLS-1$
 		} catch (UnsupportedEncodingException e) {
 			log.error("", e); // $NON-NLS-1$
 		}
 		return config;
 	}
 
 	public static byte[] getBinaryData(Configuration config) {
 		if (config == null) {
 			return new byte[0];
 		}
 		try {
 			return config.getValue("").getBytes("UTF-8"); // $NON-NLS-1$
 		} catch (UnsupportedEncodingException e) {
 			return new byte[0];
 		}
 	}
 
 	public static AssertionResult getAssertionResult(Configuration config) {
 		AssertionResult result = new AssertionResult(""); //TODO provide proper name?
 		result.setError(config.getAttributeAsBoolean(ERROR, false));
 		result.setFailure(config.getAttributeAsBoolean(FAILURE, false));
 		result.setFailureMessage(config.getAttribute(FAILURE_MESSAGE, ""));
 		return result;
 	}
 
 	public static Configuration getConfiguration(AssertionResult assResult) {
 		DefaultConfiguration config = new DefaultConfiguration(ASSERTION_RESULT_TAG_NAME, "JMeter Save Service");
 
 		config.setAttribute(FAILURE_MESSAGE, assResult.getFailureMessage());
 		config.setAttribute(ERROR, "" + assResult.isError());
 		config.setAttribute(FAILURE, "" + assResult.isFailure());
 		return config;
 	}
 
 	/*
 	 * TODO - I think this is used for the original test plan format
 	 * It seems to be rather out of date, as many attributes are missing?
 	*/
 	/**
 	 * This method determines the content of the result data that will be
 	 * stored.
 	 * 
 	 * @param result
 	 *            the object containing all of the data that has been collected.
 	 * @param saveConfig
 	 *            the configuration giving the data items to be saved.
 	 */
 	public static Configuration getConfiguration(SampleResult result, SampleSaveConfiguration saveConfig) {
 		DefaultConfiguration config = new DefaultConfiguration(SAMPLE_RESULT_TAG_NAME, "JMeter Save Service"); // $NON-NLS-1$
 
 		if (saveConfig.saveTime()) {
 			config.setAttribute(TIME, String.valueOf(result.getTime()));
 		}
 		if (saveConfig.saveLabel()) {
 			config.setAttribute(LABEL, result.getSampleLabel());
 		}
 		if (saveConfig.saveCode()) {
 			config.setAttribute(RESPONSE_CODE, result.getResponseCode());
 		}
 		if (saveConfig.saveMessage()) {
 			config.setAttribute(RESPONSE_MESSAGE, result.getResponseMessage());
 		}
 		if (saveConfig.saveThreadName()) {
 			config.setAttribute(THREAD_NAME, result.getThreadName());
 		}
 		if (saveConfig.saveDataType()) {
 			config.setAttribute(DATA_TYPE, result.getDataType());
 		}
 
 		if (saveConfig.printMilliseconds()) {
 			config.setAttribute(TIME_STAMP, String.valueOf(result.getTimeStamp()));
 		} else if (saveConfig.formatter() != null) {
 			String stamp = saveConfig.formatter().format(new Date(result.getTimeStamp()));
 
 			config.setAttribute(TIME_STAMP, stamp);
 		}
 
 		if (saveConfig.saveSuccess()) {
 			config.setAttribute(SUCCESSFUL, Boolean.toString(result.isSuccessful()));
 		}
 
 		SampleResult[] subResults = result.getSubResults();
 
 		if (subResults != null) {
 			for (int i = 0; i < subResults.length; i++) {
 				config.addChild(getConfiguration(subResults[i], saveConfig));
 			}
 		}
 
 		AssertionResult[] assResults = result.getAssertionResults();
 
 		if (saveConfig.saveSamplerData(result)) {
 			config.addChild(createConfigForString("samplerData", result.getSamplerData())); // $NON-NLS-1$
 		}
 		if (saveConfig.saveAssertions() && assResults != null) {
 			for (int i = 0; i < assResults.length; i++) {
 				config.addChild(getConfiguration(assResults[i]));
 			}
 		}
 		if (saveConfig.saveResponseData(result)) {
 			config.addChild(getConfiguration(result.getResponseData()));
 		}
 		return config;
 	}
 
 	public static Configuration getConfigForTestElement(String named, TestElement item) {
 		TestElementSaver saver = new TestElementSaver(named);
 		item.traverse(saver);
 		Configuration config = saver.getConfiguration();
 		/*
 		 * DefaultConfiguration config = new DefaultConfiguration("testelement",
 		 * "testelement");
 		 * 
 		 * if (named != null) { config.setAttribute("name", named); } if
 		 * (item.getProperty(TestElement.TEST_CLASS) != null) {
 		 * config.setAttribute("class", (String)
 		 * item.getProperty(TestElement.TEST_CLASS)); } else {
 		 * config.setAttribute("class", item.getClass().getName()); } Iterator
 		 * iter = item.getPropertyNames().iterator();
 		 * 
 		 * while (iter.hasNext()) { String name = (String) iter.next(); Object
 		 * value = item.getProperty(name);
 		 * 
 		 * if (value instanceof TestElement) {
 		 * config.addChild(getConfigForTestElement(name, (TestElement) value)); }
 		 * else if (value instanceof Collection) {
 		 * config.addChild(createConfigForCollection(name, (Collection) value)); }
 		 * else if (value != null) { config.addChild(createConfigForString(name,
 		 * value.toString())); } }
 		 */
 		return config;
 	}
 
 
 	private static Configuration createConfigForString(String name, String value) {
 		if (value == null) {
 			value = "";
 		}
 		DefaultConfiguration config = new DefaultConfiguration("property", "property");
 
 		config.setAttribute("name", name);
 		config.setValue(value);
 		config.setAttribute(XML_SPACE, PRESERVE);
 		return config;
 	}
 
 	public synchronized static HashTree loadSubTree(InputStream in) throws IOException {
 		try {
 			Configuration config = builder.build(in);
 			HashTree loadedTree = generateNode(config);
 
 			return loadedTree;
 		} catch (ConfigurationException e) {
 			String message = "Problem loading using Avalon Configuration tools";
 			log.error(message, e);
 			throw new IOException(message);
 		} catch (SAXException e) {
 			String message = "Problem with SAX implementation";
 			log.error(message, e);
 			throw new IOException(message);
 		}
 	}
 
 	public static TestElement createTestElement(Configuration config) throws ConfigurationException,
 			ClassNotFoundException, IllegalAccessException, InstantiationException {
 		TestElement element = null;
 
 		String testClass = config.getAttribute("class"); // $NON-NLS-1$
 		
         String gui_class=""; // $NON-NLS-1$
 		Configuration[] children = config.getChildren();
         for (int i = 0; i < children.length; i++) {
             if (children[i].getName().equals("property")) { // $NON-NLS-1$
                 if (children[i].getAttribute("name").equals(TestElement.GUI_CLASS)){ // $NON-NLS-1$
                     gui_class=children[i].getValue();
                 }
             }  
         }
         
         String newClass = NameUpdater.getCurrentTestName(testClass,gui_class);
 
         element = (TestElement) Class.forName(newClass).newInstance();
 
         for (int i = 0; i < children.length; i++) {
 			if (children[i].getName().equals("property")) { // $NON-NLS-1$
 				try {
                     JMeterProperty prop = createProperty(children[i], newClass);
 					if (prop!=null) element.setProperty(prop);
 				} catch (Exception ex) {
 					log.error("Problem loading property", ex);
 					element.setProperty(children[i].getAttribute("name"), ""); // $NON-NLS-1$ // $NON-NLS-2$
 				}
 			} else if (children[i].getName().equals("testelement")) { // $NON-NLS-1$
 				element.setProperty(new TestElementProperty(children[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
 						createTestElement(children[i])));
 			} else if (children[i].getName().equals("collection")) { // $NON-NLS-1$
 				element.setProperty(new CollectionProperty(children[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
                         createCollection(children[i], newClass)));
 			} else if (children[i].getName().equals("map")) { // $NON-NLS-1$
 				element.setProperty(new MapProperty(children[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
                         createMap(children[i],newClass)));
 			}
 		}
 		return element;
 	}
 
 	private static Collection createCollection(Configuration config, String testClass) throws ConfigurationException,
 			ClassNotFoundException, IllegalAccessException, InstantiationException {
 		Collection coll = (Collection) Class.forName(config.getAttribute("class")).newInstance(); // $NON-NLS-1$ 
 		Configuration[] items = config.getChildren();
 
 		for (int i = 0; i < items.length; i++) {
 			if (items[i].getName().equals("property")) { // $NON-NLS-1$ 
                 JMeterProperty prop = createProperty(items[i], testClass);
 				if (prop!=null) coll.add(prop);
 			} else if (items[i].getName().equals("testelement")) { // $NON-NLS-1$ 
 				coll.add(new TestElementProperty(items[i].getAttribute("name", ""), createTestElement(items[i]))); // $NON-NLS-1$ // $NON-NLS-2$
 			} else if (items[i].getName().equals("collection")) { // $NON-NLS-1$ 
 				coll.add(new CollectionProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
 						createCollection(items[i], testClass)));
 			} else if (items[i].getName().equals("string")) { // $NON-NLS-1$ 
                 JMeterProperty prop = createProperty(items[i], testClass);
 				if (prop!=null) coll.add(prop);
 			} else if (items[i].getName().equals("map")) { // $NON-NLS-1$ 
 				coll.add(new MapProperty(items[i].getAttribute("name", ""), createMap(items[i], testClass))); // $NON-NLS-1$ // $NON-NLS-2$
 			}
 		}
 		return coll;
 	}
 
 	private static JMeterProperty createProperty(Configuration config, String testClass) throws IllegalAccessException,
 			ClassNotFoundException, InstantiationException {
 		String value = config.getValue(""); // $NON-NLS-1$ 
 		String name = config.getAttribute("name", value); // $NON-NLS-1$ 
         String oname = name;
 		String type = config.getAttribute("propType", StringProperty.class.getName()); // $NON-NLS-1$ 
 
 		// Do upgrade translation:
 		name = NameUpdater.getCurrentName(name, testClass);
 		if (TestElement.GUI_CLASS.equals(name)) {
 			value = NameUpdater.getCurrentName(value);
         } else if (TestElement.TEST_CLASS.equals(name)) {
             value=testClass; // must always agree
 		} else {
 			value = NameUpdater.getCurrentName(value, name, testClass);
 		}
 
         // Delete any properties whose name converts to the empty string
         if (oname.length() != 0 && name.length()==0) {
             return null;
         }
 
         // Create the property:
 		JMeterProperty prop = (JMeterProperty) Class.forName(type).newInstance();
 		prop.setName(name);
 		prop.setObjectValue(value);
 
 		return prop;
 	}
 
 	private static Map createMap(Configuration config, String testClass) throws ConfigurationException,
 			ClassNotFoundException, IllegalAccessException, InstantiationException {
 		Map map = (Map) Class.forName(config.getAttribute("class")).newInstance();
 		Configuration[] items = config.getChildren();
 
 		for (int i = 0; i < items.length; i++) {
 			if (items[i].getName().equals("property")) { // $NON-NLS-1$ 
 				JMeterProperty prop = createProperty(items[i], testClass);
 				if (prop!=null) map.put(prop.getName(), prop);
 			} else if (items[i].getName().equals("testelement")) { // $NON-NLS-1$ 
 				map.put(items[i].getAttribute("name", ""), new TestElementProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
 						createTestElement(items[i])));
 			} else if (items[i].getName().equals("collection")) { // $NON-NLS-1$ 
 				map.put(items[i].getAttribute("name"),  // $NON-NLS-1$ 
 						new CollectionProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
 						createCollection(items[i], testClass)));
 			} else if (items[i].getName().equals("map")) { // $NON-NLS-1$ 
 				map.put(items[i].getAttribute("name", ""),  // $NON-NLS-1$ // $NON-NLS-2$
 						new MapProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
 						createMap(items[i], testClass)));
 			}
 		}
 		return map;
 	}
 
 	private static HashTree generateNode(Configuration config) {
 		TestElement element = null;
 
 		try {
 			element = createTestElement(config.getChild("testelement")); // $NON-NLS-1$ 
 		} catch (Exception e) {
 			log.error("Problem loading part of file", e);
 			return null;
 		}
 		HashTree subTree = new ListedHashTree(element);
 		Configuration[] subNodes = config.getChildren("node"); // $NON-NLS-1$ 
 
 		for (int i = 0; i < subNodes.length; i++) {
 			HashTree t = generateNode(subNodes[i]);
 
 			if (t != null) {
 				subTree.add(element, t);
 			}
 		}
 		return subTree;
 	}
 }
diff --git a/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java b/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
index dcdcd7f6c..551434c52 100644
--- a/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
@@ -1,393 +1,396 @@
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
 
 package org.apache.jmeter.save.converters;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jorphan.util.Converter;
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 
 /**
  * XStream Converter for the SampleResult class
  */
 public class SampleResultConverter extends AbstractCollectionConverter {
 	//private static final Logger log = LoggingManager.getLoggerForClass();
  
     private static final String JAVA_LANG_STRING = "java.lang.String"; //$NON-NLS-1$
     private static final String ATT_CLASS = "class"; //$NON-NLS-1$
 
     // Element tags. Must be unique. Keep sorted.
     protected static final String TAG_COOKIES           = "cookies";          //$NON-NLS-1$
     protected static final String TAG_METHOD            = "method";           //$NON-NLS-1$
     protected static final String TAG_QUERY_STRING      = "queryString";      //$NON-NLS-1$
     protected static final String TAG_REDIRECT_LOCATION = "redirectLocation"; //$NON-NLS-1$
     protected static final String TAG_REQUEST_HEADER    = "requestHeader";    //$NON-NLS-1$
 
 	//NOT USED protected   static final String TAG_URL               = "requestUrl";       //$NON-NLS-1$
 
     protected static final String TAG_RESPONSE_DATA     = "responseData";     //$NON-NLS-1$
     protected static final String TAG_RESPONSE_HEADER   = "responseHeader";   //$NON-NLS-1$
     protected static final String TAG_SAMPLER_DATA      = "samplerData";      //$NON-NLS-1$
     protected static final String TAG_RESPONSE_FILE     = "responseFile";     //$NON-NLS-1$
 
     // samplerData attributes. Must be unique. Keep sorted by string value.
     private static final String ATT_BYTES             = "by"; //$NON-NLS-1$
     private static final String ATT_DATA_ENCODING     = "de"; //$NON-NLS-1$
     private static final String ATT_DATA_TYPE         = "dt"; //$NON-NLS-1$
+    private static final String ATT_ERROR_COUNT      = "ec"; //$NON-NLS-1$
     private static final String ATT_LABEL             = "lb"; //$NON-NLS-1$
     private static final String ATT_LATENCY           = "lt"; //$NON-NLS-1$
 
     private static final String ATT_ALL_THRDS         = "na"; //$NON-NLS-1$
     private static final String ATT_GRP_THRDS         = "ng"; //$NON-NLS-1$
 
     // N.B. Originally the response code was saved with the code "rs" 
     // but retrieved with the code "rc". Changed to always use "rc", but
     // allow for "rs" when restoring values.
     private static final String ATT_RESPONSE_CODE     = "rc"; //$NON-NLS-1$
     private static final String ATT_RESPONSE_MESSAGE  = "rm"; //$NON-NLS-1$
     private static final String ATT_RESPONSE_CODE_OLD = "rs"; //$NON-NLS-1$
     
     private static final String ATT_SUCCESS           = "s";  //$NON-NLS-1$
     private static final String ATT_SAMPLE_COUNT      = "sc"; //$NON-NLS-1$
     private static final String ATT_TIME              = "t";  //$NON-NLS-1$
     private static final String ATT_TIME_STAMP        = "ts"; //$NON-NLS-1$
     private static final String ATT_THREADNAME        = "tn"; //$NON-NLS-1$
 
 	/**
 	 * Returns the converter version; used to check for possible
 	 * incompatibilities
 	 */
 	public static String getVersion() {
 		return "$Revision$"; //$NON-NLS-1$
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see com.thoughtworks.xstream.converters.Converter#canConvert(java.lang.Class)
 	 */
 	public boolean canConvert(Class arg0) {
 		return SampleResult.class.equals(arg0);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
 	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
 	 *      com.thoughtworks.xstream.converters.MarshallingContext)
 	 */
 	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
 		SampleResult res = (SampleResult) obj;
 		SampleSaveConfiguration save = res.getSaveConfig();
 		setAttributes(writer, context, res, save);
 		saveAssertions(writer, context, res, save);
 		saveSubResults(writer, context, res, save);
 		saveResponseHeaders(writer, context, res, save);
 		saveRequestHeaders(writer, context, res, save);
 		saveResponseData(writer, context, res, save);
 		saveSamplerData(writer, context, res, save);
 	}
 
 	/**
 	 * @param writer
 	 * @param res
 	 * @param save
 	 */
 	protected void saveSamplerData(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
 			SampleSaveConfiguration save) {
 		if (save.saveSamplerData(res)) {
 			writeString(writer, TAG_SAMPLER_DATA, res.getSamplerData());
 		}
         if (save.saveUrl()) {
             writeItem(res.getURL(), context, writer);
         }
 	}
 
 	/**
 	 * @param writer
 	 * @param res
 	 * @param save
 	 */
 	protected void saveResponseData(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
 			SampleSaveConfiguration save) {
 		if (save.saveResponseData(res)) {
 			writer.startNode(TAG_RESPONSE_DATA);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
 			try {
                 if (SampleResult.TEXT.equals(res.getDataType())){
     				writer.setValue(new String(res.getResponseData(), res.getDataEncoding()));
                 }
                 // Otherwise don't save anything - no point
 			} catch (UnsupportedEncodingException e) {
 				writer.setValue("Unsupported encoding in response data, can't record.");
 			}
 			writer.endNode();
 		}
         if (save.saveFileName()){
             writer.startNode(TAG_RESPONSE_FILE);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
             writer.setValue(res.getResultFileName());
             writer.endNode();            
         }
 	}
 
 	/**
 	 * @param writer
 	 * @param res
 	 * @param save
 	 */
 	protected void saveRequestHeaders(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
 			SampleSaveConfiguration save) {
 		if (save.saveRequestHeaders()) {
 			writeString(writer, TAG_REQUEST_HEADER, res.getRequestHeaders());
 		}
 	}
 
 	/**
 	 * @param writer
 	 * @param res
 	 * @param save
 	 */
 	protected void saveResponseHeaders(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
 			SampleSaveConfiguration save) {
 		if (save.saveResponseHeaders()) {
 			writeString(writer, TAG_RESPONSE_HEADER, res.getResponseHeaders());
 		}
 	}
 
 	/**
 	 * @param writer
 	 * @param context
 	 * @param res
 	 * @param save
 	 */
 	protected void saveSubResults(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
 			SampleSaveConfiguration save) {
 		if (save.saveSubresults()) {
 			SampleResult[] subResults = res.getSubResults();
 			for (int i = 0; i < subResults.length; i++) {
 				subResults[i].setSaveConfig(save);
 				writeItem(subResults[i], context, writer);
 			}
 		}
 	}
 
 	/**
 	 * @param writer
 	 * @param context
 	 * @param res
 	 * @param save
 	 */
 	protected void saveAssertions(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
 			SampleSaveConfiguration save) {
 		if (save.saveAssertions()) {
 			AssertionResult[] assertionResults = res.getAssertionResults();
 			for (int i = 0; i < assertionResults.length; i++) {
 				writeItem(assertionResults[i], context, writer);
 			}
 		}
 	}
 
 	/**
 	 * @param writer
 	 * @param res
 	 * @param save
 	 */
 	protected void setAttributes(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
 			SampleSaveConfiguration save) {
 		if (save.saveTime())
 			writer.addAttribute(ATT_TIME, Long.toString(res.getTime()));
 		if (save.saveLatency())
 			writer.addAttribute(ATT_LATENCY, Long.toString(res.getLatency()));
 		if (save.saveTimestamp())
 			writer.addAttribute(ATT_TIME_STAMP, Long.toString(res.getTimeStamp()));
 		if (save.saveSuccess())
 			writer.addAttribute(ATT_SUCCESS,
 					Boolean.toString(res.isSuccessful()));
 		if (save.saveLabel())
 			writer.addAttribute(ATT_LABEL, ConversionHelp.encode(res.getSampleLabel()));
 		if (save.saveCode())
 			writer.addAttribute(ATT_RESPONSE_CODE, ConversionHelp.encode(res.getResponseCode()));
 		if (save.saveMessage())
 			writer.addAttribute(ATT_RESPONSE_MESSAGE, ConversionHelp.encode(res.getResponseMessage()));
 		if (save.saveThreadName())
 			writer.addAttribute(ATT_THREADNAME, ConversionHelp.encode(res.getThreadName()));
 		if (save.saveDataType())
 			writer.addAttribute(ATT_DATA_TYPE, ConversionHelp.encode(res.getDataType()));
 		if (save.saveEncoding())
 			writer.addAttribute(ATT_DATA_ENCODING, ConversionHelp.encode(res.getDataEncoding()));
 		if (save.saveBytes())
 			writer.addAttribute(ATT_BYTES, String.valueOf(res.getBytes()));
         if (save.saveSampleCount()){
         	writer.addAttribute(ATT_SAMPLE_COUNT, String.valueOf(res.getSampleCount()));
+        	writer.addAttribute(ATT_ERROR_COUNT, String.valueOf(res.getErrorCount()));
         }
         if (save.saveThreadCounts()){
            writer.addAttribute(ATT_GRP_THRDS, String.valueOf(res.getGroupThreads()));
            writer.addAttribute(ATT_ALL_THRDS, String.valueOf(res.getAllThreads()));
         }
 	}
 
 	/**
 	 * @param writer
 	 * @param tag
 	 * @param value
 	 */
 	protected void writeString(HierarchicalStreamWriter writer, String tag, String value) {
 		if (value != null) {
 			writer.startNode(tag);
 			writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
 			writer.setValue(value);
 			writer.endNode();
 		}
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
 	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
 	 */
 	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
 		SampleResult res = (SampleResult) createCollection(context.getRequiredType());
 		retrieveAttributes(reader, context, res);
 		while (reader.hasMoreChildren()) {
 			reader.moveDown();
 			Object subItem = readItem(reader, context, res);
 			retrieveItem(reader, context, res, subItem);
 			reader.moveUp();
 		}
 
         // If we have a file, but no data, then read the file
         String resultFileName = res.getResultFileName();
         if (resultFileName.length()>0 
         &&  res.getResponseData().length == 0) {
             readFile(resultFileName,res);
         }
 		return res;
 	}
 
 	/**
      * 
 	 * @param reader
 	 * @param context
 	 * @param res
      * @return true if the item was processed (for HTTPResultConverter)
 	 */
 	protected boolean retrieveItem(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res,
 			Object subItem) {
 		String nodeName = reader.getNodeName();
 		if (subItem instanceof AssertionResult) {
 			res.addAssertionResult((AssertionResult) subItem);
 		} else if (subItem instanceof SampleResult) {
 			res.storeSubResult((SampleResult) subItem);
 		} else if (nodeName.equals(TAG_RESPONSE_HEADER)) {
 			res.setResponseHeaders((String) subItem);
 		} else if (nodeName.equals(TAG_REQUEST_HEADER)) {
 			res.setRequestHeaders((String) subItem);
 		} else if (nodeName.equals(TAG_RESPONSE_DATA)) {
 			try {
 				res.setResponseData(((String) subItem).getBytes(res.getDataEncoding()));
 			} catch (UnsupportedEncodingException e) {
 				res.setResponseData(("Can't support the char set: " + res.getDataEncoding()).getBytes());
 			}
 		} else if (nodeName.equals(TAG_SAMPLER_DATA)) {
 			res.setSamplerData((String) subItem);
         } else if (nodeName.equals(TAG_RESPONSE_FILE)) {
             res.setResultFileName((String) subItem);
 		// Don't try restoring the URL TODO: wy not?
 		} else {
 			return false;
 		}
 		return true;
 	}
 
 	/**
 	 * @param reader
 	 * @param res
 	 */
 	protected void retrieveAttributes(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res) {
 		res.setSampleLabel(ConversionHelp.decode(reader.getAttribute(ATT_LABEL)));
 		res.setDataEncoding(ConversionHelp.decode(reader.getAttribute(ATT_DATA_ENCODING)));
 		res.setDataType(ConversionHelp.decode(reader.getAttribute(ATT_DATA_TYPE)));
         String oldrc=reader.getAttribute(ATT_RESPONSE_CODE_OLD);
         if (oldrc!=null) {
             res.setResponseCode(ConversionHelp.decode(oldrc));
         } else {
             res.setResponseCode(ConversionHelp.decode(reader.getAttribute(ATT_RESPONSE_CODE)));
         }
 		res.setResponseMessage(ConversionHelp.decode(reader.getAttribute(ATT_RESPONSE_MESSAGE)));
 		res.setSuccessful(Converter.getBoolean(reader.getAttribute(ATT_SUCCESS), true));
 		res.setThreadName(ConversionHelp.decode(reader.getAttribute(ATT_THREADNAME)));
 		res.setStampAndTime(Converter.getLong(reader.getAttribute(ATT_TIME_STAMP)),
 				Converter.getLong(reader.getAttribute(ATT_TIME)));
 		res.setLatency(Converter.getLong(reader.getAttribute(ATT_LATENCY)));
 		res.setBytes(Converter.getInt(reader.getAttribute(ATT_BYTES)));
 		res.setSampleCount(Converter.getInt(reader.getAttribute(ATT_SAMPLE_COUNT),1)); // default is 1
+		res.setErrorCount(Converter.getInt(reader.getAttribute(ATT_ERROR_COUNT),0)); // default is 0
 		res.setGroupThreads(Converter.getInt(reader.getAttribute(ATT_GRP_THRDS)));
 		res.setAllThreads(Converter.getInt(reader.getAttribute(ATT_ALL_THRDS)));
 	}
 
     protected void readFile(String resultFileName, SampleResult res) {
         File in = null;
         FileInputStream fis = null;
         try {
             in = new File(resultFileName);
             fis = new FileInputStream(in);
             ByteArrayOutputStream outstream = new ByteArrayOutputStream(res.getBytes());
             byte[] buffer = new byte[4096];
             int len;
             while ((len = fis.read(buffer)) > 0) {
                 outstream.write(buffer, 0, len);
             }
             outstream.close();
             res.setResponseData(outstream.toByteArray());
         } catch (FileNotFoundException e) {
             //log.warn(e.getLocalizedMessage());
         } catch (IOException e) {
             //log.warn(e.getLocalizedMessage());
         } finally {
             try {
                 if (fis != null) fis.close();
             } catch (IOException e) {
             }
         }
     }
 
 
 	/**
 	 * @param arg0
 	 */
 	public SampleResultConverter(Mapper arg0) {
 		super(arg0);
 	}
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/util/Calculator.java b/src/core/org/apache/jmeter/util/Calculator.java
index b118d6e7e..175dc0ecd 100644
--- a/src/core/org/apache/jmeter/util/Calculator.java
+++ b/src/core/org/apache/jmeter/util/Calculator.java
@@ -1,203 +1,203 @@
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
 
 package org.apache.jmeter.util;
 
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * Class to calculate various items that don't require all previous results to be saved:
  * - mean = average
  * - standard deviation
  * - minimum
  * - maximum
  */
 public class Calculator {
 
 	private double sum = 0;
 
 	private double sumOfSquares = 0;
 
 	private double mean = 0;
 
 	private double deviation = 0;
 
 	private int count = 0;
 
 	private long bytes = 0;
 	
 	private long maximum = Long.MIN_VALUE;
 	
 	private long minimum = Long.MAX_VALUE;
     
     private int errors = 0;
 	
     private final String label;
     
     public Calculator() {
         this("");
     }
 
 	public Calculator(String label) {
         this.label = label;
     }
 
     public void clear() {
 		maximum = Long.MIN_VALUE;
 		minimum = Long.MAX_VALUE;
 		sum = 0;
 		sumOfSquares = 0;
 		mean = 0;
 		deviation = 0;
 		count = 0;
 	}
 
 	public void addValue(long newValue) {
         addValue(newValue,1);
 	}
 
     private void addValue(long newValue, int sampleCount) {
         count += sampleCount;
         minimum=Math.min(newValue, minimum);
         maximum=Math.max(newValue, maximum);
         double currentVal = newValue;
         sum += currentVal;
         sumOfSquares += currentVal * currentVal;
         // Calculate each time, as likely to be called for each add
         mean = sum / count;
         deviation = Math.sqrt((sumOfSquares / count) - (mean * mean));
     }
 
 
 	public void addBytes(long newValue) {
 		bytes += newValue;
 	}
 
     private long startTime = 0;
     private long elapsedTime = 0;
 
     public void addSample(SampleResult res) {
         addBytes(res.getBytes());
         addValue(res.getTime(),res.getSampleCount());
-        if (!res.isSuccessful()) errors++;
+        errors+=res.getErrorCount(); // account for multiple samples
         if (startTime == 0){
             startTime=res.getStartTime();
         }
         startTime = Math.min(startTime, res.getStartTime());
         elapsedTime = Math.max(elapsedTime, res.getEndTime()-startTime);
     }
 
 
     public long getTotalBytes() {
 		return bytes;
 	}
 
 
 	public double getMean() {
 		return mean;
 	}
 
     public Number getMeanAsNumber() {
         return new Long((long) mean);
     }
 
 	public double getStandardDeviation() {
 		return deviation;
 	}
 
 	public long getMin() {
 		return minimum;
 	}
 
 	public long getMax() {
 		return maximum;
 	}
 
 	public int getCount() {
 		return count;
 	}
 
     public String getLabel() {
         return label;
     }
 
     /**
      * Returns the raw double value of the percentage of samples with errors
      * that were recorded. (Between 0.0 and 1.0)
      * 
      * @return the raw double value of the percentage of samples with errors
      *         that were recorded.
      */
     public double getErrorPercentage() {
         double rval = 0.0;
 
         if (count == 0) {
             return (rval);
         }
         rval = (double) errors / (double) count;
         return (rval);
     }
 
     /**
      * Returns the throughput associated to this sampler in requests per second.
      * May be slightly skewed because it takes the timestamps of the first and
      * last samples as the total time passed, and the test may actually have
      * started before that start time and ended after that end time.
      */
     public double getRate() {
         if (elapsedTime == 0)
             return 0.0;
 
         return ((double) count / (double) elapsedTime ) * 1000;
     }
 
     /**
      * calculates the average page size, which means divide the bytes by number
      * of samples.
      * 
-     * @return
+     * @return average page size
      */
     public double getPageSize() {
         if (count > 0 && bytes > 0) {
             return (double) bytes / count;
         }
         return 0.0;
     }
 
     /**
      * Throughput in bytes / second
      * 
-     * @return
+     * @return throughput in bytes/second
      */
     public double getBytesPerSecond() {
         if (elapsedTime > 0) {
-            return bytes / ((double) elapsedTime / 1000);
+            return bytes / ((double) elapsedTime / 1000); // 1000 = millisecs/sec
         }
         return 0.0;
     }
 
     /**
      * Throughput in kilobytes / second
      * 
-     * @return
+     * @return Throughput in kilobytes / second
      */
     public double getKBPerSecond() {
-        return getBytesPerSecond() / 1024;
+        return getBytesPerSecond() / 1024; // 1024=bytes per kb
     }
 
 }
\ No newline at end of file
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 6c9bf143a..cb444ce97 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,1061 +1,1061 @@
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
 
 <h3>Version 2.3.1</h3>
 
 <h4>Bug fixes</h4>
 <ul>
 <li>SaveService no longer needs to instantiate classes</li>
 <li>Bug 43430 - Count of active threads is incorrect for remote samples</li>
 </ul>
 
 <h4>Improvements</h4>
 <ul>
 <li>Add run_gui Ant target, to package and then start the JMeter GUI from Ant</li>
 <li>Add File->Revert to easily drop the current changes and reload the project file currently loaded</li>
 <li>Bug 31366 - Remember recently opened file(s)</li>
 <li>Bug 43351 - Add support for Parameters and script file to all BeanShell test elements</li>
 </ul>
 
 <!--  ===================  -->
 
 <h3>Version 2.3</h3>
 
 <h3>Fixes since 2.3RC4</h3>
 
 <h4>Bug fixes</h4>
 <ul>
 <li>Fix NPE in SampleResultConverter - XStream PrettyPrintWriter cannot handle nulls</li>
 <li>If Java HTTP sampler sees null ResponseMessage, replace with HTTP header</li>
 <li>Bug 43332 - 2.3RC4 does not clear Guis based on TestBean</li>
 <li>Bug 42948 - Problems with Proxy gui table fields in Java 1.6</li>
 <li>Fixup broken jmeter-server script</li>
 <li>Bug 43364 - option to revert If Controller to pre 2.3RC3 behaviour</li>
 <li>Bug 43449 - Statistical Remote mode does not handle Latency</li>
-<li>Bug 43450 (partial fix) - Allow SampleCount to be saved/restored from files</li>
+<li>Bug 43450 (partial fix) - Allow SampleCount and ErrorCount to be saved to/restored from files</li>
 </ul>
 
 <h4>Improvements</h4>
 <ul>
 <li>Add nameSpace option to XPath extractor</li>
 <li>Add NULL parameter option to JDBC sampler</li>
 <li>Add documentation links for Rhino and BeanShell to functions; clarify variables and properties</li>
 <li>Ensure uncaught exceptions are logged</li>
 <li>Look for user.properties and system.properties in JMeter bin directory if not found locally</li>
 </ul>
 
 <h4>Fixes since 2.3RC3</h4>
 <ul>
 <li>Fixed NPE in Summariser (bug introduced in 2.3RC3)</li>
 <li>Fixed setup of proxy port (bug introduced in 2.3RC3)</li>
 <li>Fixed errors when running non-GUI on a headless host (bug introduced in 2.3RC3)</li>
 <li>Bug 43054 - SSLManager causes stress tests to saturate and crash (bug introduced in 2.3RC3)</li>
 <li>Clarified HTTP Request Defaults usage of the port field</li>
 <li>Bug 43006 - NPE if icon.properties file not found</li>
 <li>Bug 42918 - Size Assertion now treats an empty response as having zero length</li>
 <li>Bug 43007 - Test ends before all threadgroups started</li>
 <li>Fix possible NPE in HTTPSampler2 if 302 does not have Location header.</li>
 <li>Bug 42919 - Failure Message blank in CSV output [now records first non-blank message]</li>
 <li>Add link to Extending JMeter PDF</li>
 <li>Allow for quoted charset in Content-Type parsing</li>
 <li>Bug 39792 - ClientJMeter synchronisation needed</li>
 <li>Bug 43122 - GUI changes not always picked up when short-cut keys used (bug introduced in 2.3RC3)</li>
 <li>Bug 42947 - TestBeanGUI changes not picked up when short-cut keys used</li>
 <li>Added serializer.jar (needed for update to xalan 2.7.0)</li>
 <li>Bug 38687 - Module controller does not work in non-GUI mode</li>
 </ul>
 
 <h4>Improvements since 2.3RC3</h4>
 <ul>
 <li>Add stop thread option to CSV Dataset</li>
 <li>Updated commons-httpclient to 3.1</li>
 <li>Bug 28715 - allow variable cookie values (set CookieManager.allow_variable_cookies=false to disable)</li>
 <li>Bug 40873 - add JMS point-to-point non-persistent delivery option</li>
 <li>Bug 43283 - Save action adds .jmx if not present; checks for existing file on Save As</li>
 <li>Control+A key does not work for Save All As; changed to Control+Shift+S</li>
 <li>Bug 40991 - Allow Assertions to check Headers</li>
 </ul>
 
 <h3>Version 2.3RC3</h3>
 
 <h4>Known problems/restrictions:</h4>
 <p>
 The JMeter remote server does not support multiple concurrent tests - each remote test should be run in a separate server.
 Otherwise tests may fail with random Exceptions, e.g. ConcurrentModification Exception in StandardJMeterEngine.
 See bug 43168.
 </p>
 <p>
 The default HTTP Request (not HTTPClient) sampler may not work for HTTPS connections via a proxy.
 This appears to be due to a Java bug, see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=39337">Bug 39337</a>.
 To avoid the problem, try a more recent version of Java, or switch to the HTTPClient version of the HTTP Request sampler.
 </p>
 <p>Transaction Controller parent mode does not support nested Transaction Controllers.
 Doing so may cause a Null Pointer Exception in TestCompiler.
 </p>
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
 Variables and functions do not work in Listeners in client-server (remote) mode so they cannot be used
 to name log files in client-server mode.
 </p>
 <p>
 CSV Dataset variables are defined after configuration processing is completed,
 so they cannot be used for other configuration items such as JDBC Config.
 (see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=40934">Bug 40394 </a>)
 </p>
 
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
 <li>HTTP Proxy supports XML-RPC recording, and other proxy improvements</li>
 <li>__V() function allows support of nested variable references</li>
 <li>LDAP Ext sampler optionally parses result sets and supports secure mode</li>
 <li>FTP Sampler supports Ascii/Binary mode and upload</li>
 <li>Transaction Controller now optionally generates a Sample with subresults</li>
 <li>HTTPS session contexts are now per-thread, rather than shared. This gives better emulation of multiple users</li>
 <li>BeanShell elements now support ThreadListener and TestListener interfaces</li>
 <li>Coloured icons in Tree View Listener and elsewhere to better differentiate failed samples.</li>
 </ul>
 <p>
 The main bug fixes are:
 </p>
 <ul>
 <li>HTTPS (SSL) handling now much improved</li>
 <li>Various Remote mode bugs fixed</li>
 <li>Control+C and Control+V now work in the test tree</li>
 <li>Latency and Encoding now available in CSV log output</li>
 <li>Test elements no longer default to previous contents; test elements no longer cleared when changing language.</li>
 </ul>
 
 <h4>Incompatible changes (usage):</h4>
 <p>
 <b>N.B. The javax.net.ssl properties have been moved from jmeter.properties to system.properties,
 and will no longer work if defined in jmeter.properties.</b>
 <br></br>
 The new arrangement is more flexible, as it allows arbitrary system properties to be defined.
 </p>
 <p>
 SSL session contexts are now created per-thread, rather than being shared.
 This generates a more realistic load for HTTPS tests.
 The change is likely to slow down tests with many SSL threads.
 The original behaviour can be enabled by setting the JMeter property:
 <pre>
 https.sessioncontext.shared=true
 </pre>
 </p>
 <p>
 The LDAP Extended Sampler now uses the same panel for both Thread Bind and Single-Bind tests.
 This means that any tests using the Single-bind test will need to be updated to set the username and password.
 </p>
 <p>
 Bug 41140: JMeterThread behaviour was changed so that PostProcessors are run in forward order
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
 <p>
 Control-Z no longer used for Remote Start All - this now uses Control+Shift+R
 </p>
 <p>
 HttpClient now uses pre-emptive authentication. 
 This can be changed by setting the following:
 <pre>
 jmeter.properties:
 httpclient.parameters.file=httpclient.parameters
 
 httpclient.parameters:
 http.authentication.preemptive$Boolean=false
 </pre>
 </p>
 
 <p>
 The port field in HTTP Request Defaults is no longer ignored for https samplers if it is set to 80.
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
 <p>
 Removed method saveUsingJPEGEncoder() from SaveGraphicsService.
 It was unused so far, and used the only Sun-specific class in JMeter.
 </p>
 
 
 <h4>New functionality/improvements:</h4>
 <ul>
 <li>Add Domain and Realm support to HTTP Authorisation Manager</li>
 <li>HttpClient now behaves the same as the JDK http sampler for invalid certificates etc</li>
 <li>Added httpclient.parameters.file to allow HttpClient parameters to be defined</li>
 <li>Bug 33964 - Http Requests can send a file as the entire post body if name/type are omitted</li>
 <li>Bug 41705 - add content-encoding option to HTTP samplers for POST requests</li>
 <li>Bug 40933, 40945 - optional RE matching when retrieving embedded resource URLs</li>
 <li>Bug 27780 - (patch 19936) create multipart/form-data HTTP request without uploading file</li>
 <li>Bug 42098 - Use specified encoding for parameter values in HTTP GET</li>
 <li>Bug 42506 - JMeter threads now use independent SSL sessions</li>
 <li>Bug 41707 - HTTP Proxy XML-RPC support</li>
 <li>Bug 41880 - Add content-type filtering to HTTP Proxy Server</li>
 <li>Bug 41876 - Add more options to control what the HTTP Proxy generates</li>
 <li>Bug 42158 - Improve support for multipart/form-data requests in HTTP Proxy server</li>
 <li>Bug 42173 - Let HTTP Proxy handle encoding of request, and undecode parameter values</li>
 <li>Bug 42674 - default to pre-emptive HTTP authorisation if not specified</li>
 <li>Support "file" protocol in HTTP Samplers</li>
 <li>Http Autoredirects are now enabled by default when creating new samplers</li>
 
 <li>Bug 40103 - various LDAP enhancements</li>
 <li>Bug 40369 - LDAP: Stable search results in sampler</li>
 <li>Bug 40381 - LDAP: more descriptive strings</li>
 
 <li>BeanShell Post-Processor no longer ignores samples with zero-length result data</li>
 <li>Added beanshell.init.file property to run a BeanShell script at startup</li>
 <li>Bug 39864 - BeanShell init files now found from currrent or bin directory</li>
 <li>BeanShell elements now support ThreadListener and TestListener interfaces</li>
 <li>BSF Sampler passes additional variables to the script</li>
 
 <li>Added timeout for WebService (SOAP) Sampler</li>
 
 <li>Bug 40825 - Add JDBC prepared statement support</li>
 <li>Extend JDBC Sampler: Commit, Rollback, AutoCommit</li>
 
 <li>Bug 41457 - Add TCP Sampler option to not re-use connections</li>
 
 <li>Bug 41522 - Use JUnit sampler name in sample results</li>
 
 <li>Bug 42223 - FTP Sampler can now upload files</li>
 
 <li>Bug 40804 - Change Counter default to max = Long.MAX_VALUE</li>
 
 <li>Use property jmeter.home (if present) to override user.dir when starting JMeter</li>
 <li>New -j option to easily change jmeter log file</li>
 
 <li>HTTP Mirror Server Workbench element</li>
 
 <li>Bug 41253 - extend XPathExtractor to work with non-NodeList XPath expressions</li>
 <li>Bug 42088 - Add XPath Assertion for booleans</li>
 
 <li>Added __V variable function to resolve nested variable names</li>
 
 <li>Bug 40369 - Equals Response Assertion</li>
 <li>Bug 41704 - Allow charset encoding to be specified for CSV DataSet</li>
 <li>Bug 41259 - Comment field added to all test elements</li>
 <li>Add standard deviation to Summary Report</li>
 <li>Bug 41873 - Add name to AssertionResult and display AssertionResult in ViewResultsFullVisualizer</li>
 <li>Bug 36755 - Save XML test files with UTF-8 encoding</li>
 <li>Use ISO date-time format for Tree View Listener (previously the year was not shown)</li>
 <li>Improve loading of CSV files: if possible, use header to determine format; guess timestamp format if not milliseconds</li>
 <li>Bug 41913 - TransactionController now creates samples as sub-samples of the transaction</li>
 <li>Bug 42582 - JSON pretty printing in Tree View Listener</li>
 <li>Bug 40099 - Enable use of object variable in ForEachController</li>
 
 <li>Bug 39693 - View Result Table uses icon instead of check box</li>
 <li>Bug 39717 - use icons in the results tree</li>
 <li>Bug 42247 - improve HCI</li>
 <li>Allow user to cancel out of Close dialogue</li>
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
 <li>JUnit 3.8.2</li>
 <li>velocity 1.5</li>
 <li>commons-io 1.3.1 (added)</li>
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
 <li>Bug 41140 - Post-processors are run in reverse order</li>
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
 <li>Fix SlowSocket to work properly with Httpclient (both http and https)</li>
 <li>Bug 41612 - Loop nested in If Controller behaves erratically</li>
 <li>Bug 42232 - changing language clears UDV contents</li>
 <li>Jexl function did not allow variables</li>
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
 		of test samples.  It is what binds together a Sampler and its test 
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
