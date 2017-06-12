diff --git a/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java b/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
index 2906c19e2..cc7c71272 100644
--- a/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
@@ -1,115 +1,119 @@
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
 import java.awt.Dimension;
 
 import javax.swing.Box;
 import javax.swing.JLabel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 
 public class AssertionVisualizer extends AbstractVisualizer implements Clearable {
 
     private static final long serialVersionUID = 240L;
 
     private JTextArea textArea;
 
     public AssertionVisualizer() {
         init();
         setName(getStaticLabel());
     }
 
     public String getLabelResource() {
         return "assertion_visualizer_title"; // $NON-NLS-1$
     }
 
     public void add(SampleResult sample) {
-        StringBuilder sb = new StringBuilder(100);
+        final StringBuilder sb = new StringBuilder(100);
         sb.append(sample.getSampleLabel());
         sb.append(getAssertionResult(sample));
         sb.append("\n"); // $NON-NLS-1$
-        synchronized (textArea) {
-            textArea.append(sb.toString());
-            textArea.setCaretPosition(textArea.getText().length());
-        }
+        JMeterUtils.runSafe(new Runnable() {
+            public void run() {
+                synchronized (textArea) {
+                    textArea.append(sb.toString());
+                    textArea.setCaretPosition(textArea.getText().length());
+                }                
+            }
+        });
     }
 
     public void clearData() {
         textArea.setText(""); // $NON-NLS-1$
     }
 
     private String getAssertionResult(SampleResult res) {
         if (res != null) {
             StringBuilder display = new StringBuilder();
             AssertionResult assertionResults[] = res.getAssertionResults();
             for (int i = 0; i < assertionResults.length; i++) {
                 AssertionResult item = assertionResults[i];
 
                 if (item.isFailure() || item.isError()) {
                     display.append("\n\t"); // $NON-NLS-1$
                     display.append(item.getName() != null ? item.getName() + " : " : "");// $NON-NLS-1$
                     display.append(item.getFailureMessage());
                 }
             }
             return display.toString();
         }
         return "";
     }
 
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         this.setBorder(margin);
 
         // NAME
         this.add(makeTitlePanel(), BorderLayout.NORTH);
 
         // TEXTAREA LABEL
         JLabel textAreaLabel =
             new JLabel(JMeterUtils.getResString("assertion_textarea_label")); // $NON-NLS-1$
         Box mainPanel = Box.createVerticalBox();
         mainPanel.add(textAreaLabel);
 
         // TEXTAREA
         textArea = new JTextArea();
         textArea.setEditable(false);
         textArea.setLineWrap(false);
         JScrollPane areaScrollPane = new JScrollPane(textArea);
 
         areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
         areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 
         areaScrollPane.setPreferredSize(new Dimension(mainPanel.getWidth(),mainPanel.getHeight()));
         mainPanel.add(areaScrollPane);
         this.add(mainPanel, BorderLayout.CENTER);
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/ComparisonVisualizer.java b/src/components/org/apache/jmeter/visualizers/ComparisonVisualizer.java
index 9d2bde81e..b4699f43f 100644
--- a/src/components/org/apache/jmeter/visualizers/ComparisonVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ComparisonVisualizer.java
@@ -1,166 +1,169 @@
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
 import java.awt.Dimension;
 import java.awt.GridLayout;
 
 import javax.swing.JComponent;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTextPane;
 import javax.swing.JTree;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeModel;
 import javax.swing.tree.TreePath;
 import javax.swing.tree.TreeSelectionModel;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.assertions.CompareAssertionResult;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 
 public class ComparisonVisualizer extends AbstractVisualizer implements Clearable {
     private static final long serialVersionUID = 240L;
 
     private JTree resultsTree;
 
     private DefaultTreeModel treeModel;
 
     private DefaultMutableTreeNode root;
 
     private JTextPane base, secondary;
 
     public ComparisonVisualizer() {
         super();
         init();
     }
 
-    public void add(SampleResult sample) {
-
-        DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(sample);
-        treeModel.insertNodeInto(currNode, root, root.getChildCount());
-        if (root.getChildCount() == 1) {
-            resultsTree.expandPath(new TreePath(root));
-        }
+    public void add(final SampleResult sample) {
+        JMeterUtils.runSafe(new Runnable() {
+            public void run() {
+                DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(sample);
+                treeModel.insertNodeInto(currNode, root, root.getChildCount());
+                if (root.getChildCount() == 1) {
+                    resultsTree.expandPath(new TreePath(root));
+                }                
+            }
+        });
     }
 
     public String getLabelResource() {
         return "comparison_visualizer_title"; //$NON-NLS-1$
     }
 
     private void init() {
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
         JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
         split.add(getTreePanel());
         split.add(getSideBySidePanel());
         add(split, BorderLayout.CENTER);
     }
 
     private JComponent getSideBySidePanel() {
         JPanel main = new JPanel(new GridLayout(1, 2));
         JScrollPane base = new JScrollPane(getBaseTextPane());
         base.setPreferredSize(base.getMinimumSize());
         JScrollPane secondary = new JScrollPane(getSecondaryTextPane());
         secondary.setPreferredSize(secondary.getMinimumSize());
         main.add(base);
         main.add(secondary);
         main.setPreferredSize(main.getMinimumSize());
         return main;
     }
 
     private JTextPane getBaseTextPane() {
         base = new JTextPane();
         base.setEditable(false);
         base.setBackground(getBackground());
         return base;
     }
 
     private JTextPane getSecondaryTextPane() {
         secondary = new JTextPane();
         secondary.setEditable(false);
         return secondary;
     }
 
     private JComponent getTreePanel() {
         root = new DefaultMutableTreeNode("Root"); //$NON-NLS-1$
         treeModel = new DefaultTreeModel(root);
         resultsTree = new JTree(treeModel);
         resultsTree.setCellRenderer(new TreeNodeRenderer());
         resultsTree.setCellRenderer(new TreeNodeRenderer());
         resultsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
         resultsTree.addTreeSelectionListener(new Selector());
         resultsTree.setRootVisible(false);
         resultsTree.setShowsRootHandles(true);
 
         JScrollPane treePane = new JScrollPane(resultsTree);
         treePane.setPreferredSize(new Dimension(150, 50));
         JPanel panel = new JPanel(new GridLayout(1, 1));
         panel.add(treePane);
         return panel;
     }
 
     private class Selector implements TreeSelectionListener {
         /**
          * {@inheritDoc}
          */
         public void valueChanged(TreeSelectionEvent e) {
             try {
                 DefaultMutableTreeNode node = (DefaultMutableTreeNode) resultsTree.getLastSelectedPathComponent();
                 SampleResult sr = (SampleResult) node.getUserObject();
                 AssertionResult[] results = sr.getAssertionResults();
                 CompareAssertionResult result = null;
                 for (AssertionResult r : results) {
                     if (r instanceof CompareAssertionResult) {
                         result = (CompareAssertionResult) r;
                         break;
                     }
                 }
                 if (result == null)
                     result = new CompareAssertionResult(getName());
                 base.setText(result.getBaseResult());
                 secondary.setText(result.getSecondaryResult());
             } catch (Exception err) {
                 base.setText(JMeterUtils.getResString("comparison_invalid_node") + err); //$NON-NLS-1$
                 secondary.setText(JMeterUtils.getResString("comparison_invalid_node") + err); //$NON-NLS-1$
             }
             base.setCaretPosition(0);
             secondary.setCaretPosition(0);
         }
     }
 
     public void clearData() {
         while (root.getChildCount() > 0) {
             // the child to be removed will always be 0 'cos as the nodes are
             // removed the nth node will become (n-1)th
             treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
             base.setText(""); //$NON-NLS-1$
             secondary.setText(""); //$NON-NLS-1$
         }
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/DistributionGraphVisualizer.java b/src/components/org/apache/jmeter/visualizers/DistributionGraphVisualizer.java
index 5e269784c..c716f3462 100644
--- a/src/components/org/apache/jmeter/visualizers/DistributionGraphVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/DistributionGraphVisualizer.java
@@ -1,231 +1,236 @@
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
 import java.awt.Image;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JComponent;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
 import javax.swing.border.BevelBorder;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 
 /**
  * This class implements the visualizer for displaying the distribution graph.
  * Distribution graphs are useful for standard benchmarks and viewing the
  * distribution of data points. Results tend to clump together.
  *
  * Created May 25, 2004
  */
 public class DistributionGraphVisualizer extends AbstractVisualizer implements ImageVisualizer, GraphListener,
         Clearable {
     private static final long serialVersionUID = 240L;
 
     private SamplingStatCalculator model;
 
     private JPanel graphPanel = null;
 
     private DistributionGraph graph;
 
     private JTextField noteField;
 
     private int delay = 10;
 
     private int counter = 0;
 
     /**
      * Constructor for the GraphVisualizer object.
      */
     public DistributionGraphVisualizer() {
         model = new SamplingStatCalculator("Distribution");
         graph = new DistributionGraph(model);
         graph.setBackground(Color.white);
         init();
     }
 
     /**
      * Gets the Image attribute of the GraphVisualizer object.
      *
      * @return the Image value
      */
     public Image getImage() {
         Image result = graph.createImage(graph.getWidth(), graph.getHeight());
 
         graph.paintComponent(result.getGraphics());
 
         return result;
     }
 
     public synchronized void updateGui() {
         if (graph.getWidth() < 10) {
             graph.setPreferredSize(new Dimension(getWidth() - 40, getHeight() - 160));
         }
         graphPanel.updateUI();
         graph.repaint();
     }
 
     public synchronized void updateGui(Sample s) {
         // We have received one more sample
         if (delay == counter) {
             updateGui();
             counter = 0;
         } else {
             counter++;
         }
     }
 
-    public synchronized void add(SampleResult res) {
-        model.addSample(res);
-        updateGui(model.getCurrentSample());
+    public void add(final SampleResult res) {
+        JMeterUtils.runSafe(new Runnable() {
+            public void run() {
+                // made currentSample volatile
+                model.addSample(res);
+                updateGui(model.getCurrentSample());                
+            }
+        });
     }
 
     public String getLabelResource() {
         return "distribution_graph_title"; // $NON-NLS-1$
     }
 
     public synchronized void clearData() {
         this.graph.clearData();
         model.clear();
         repaint();
     }
 
     @Override
     public String toString() {
         return "Show the samples in a distribution graph";
     }
 
     /**
      * Initialize the GUI.
      */
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         this.setBorder(margin);
 
         // Set up the graph with header, footer, Y axis and graph display
         JPanel lgraphPanel = new JPanel(new BorderLayout());
         lgraphPanel.add(createGraphPanel(), BorderLayout.CENTER);
         lgraphPanel.add(createGraphInfoPanel(), BorderLayout.SOUTH);
 
         // Add the main panel and the graph
         this.add(makeTitlePanel(), BorderLayout.NORTH);
         this.add(lgraphPanel, BorderLayout.CENTER);
     }
 
     // Methods used in creating the GUI
 
     /**
      * Creates a scroll pane containing the actual graph of the results.
      *
      * @return a scroll pane containing the graph
      */
     private Component createGraphPanel() {
         graphPanel = new JPanel();
         graphPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.darkGray));
         graphPanel.add(graph);
         graphPanel.setBackground(Color.white);
         return graphPanel;
     }
 
     // /**
     // * Creates one of the fields used to display the graph's current
     // * values.
     // *
     // * @param color the color used to draw the value. By convention
     // * this is the same color that is used to draw the
     // * graph for this value and in the choose panel.
     // * @param length the number of digits which the field should be
     // * able to display
     // *
     // * @return a text field configured to display one of the
     // * current graph values
     // */
     // private JTextField createInfoField(Color color, int length)
     // {
     // JTextField field = new JTextField(length);
     // field.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
     // field.setEditable(false);
     // field.setForeground(color);
     // field.setBackground(getBackground());
     //
     // // The text field should expand horizontally, but have
     // // a fixed height
     // field.setMaximumSize(new Dimension(
     // field.getMaximumSize().width,
     // field.getPreferredSize().height));
     // return field;
     // }
 
     /**
      * Creates a label for one of the fields used to display the graph's current
      * values. Neither the label created by this method or the
      * <code>field</code> passed as a parameter is added to the GUI here.
      *
      * @param labelResourceName
      *            the name of the label resource. This is used to look up the
      *            label text using {@link JMeterUtils#getResString(String)}.
      * @param field
      *            the field this label is being created for.
      */
     private JLabel createInfoLabel(String labelResourceName, JTextField field) {
         JLabel label = new JLabel(JMeterUtils.getResString(labelResourceName));
         label.setForeground(field.getForeground());
         label.setLabelFor(field);
         return label;
     }
 
     /**
      * Creates the information Panel at the bottom
      *
      * @return
      */
     private Box createGraphInfoPanel() {
         Box graphInfoPanel = Box.createHorizontalBox();
         this.noteField = new JTextField();
         graphInfoPanel.add(this.createInfoLabel("distribution_note1", this.noteField)); // $NON-NLS-1$
         return graphInfoPanel;
     }
 
     /**
      * Method implements Printable, which is suppose to return the correct
      * internal component. The Action class can then print or save the graphics
      * to a file.
      */
     @Override
     public JComponent getPrintableComponent() {
         return this.graphPanel;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/GraphVisualizer.java b/src/components/org/apache/jmeter/visualizers/GraphVisualizer.java
index d68daedc5..66afb8a2a 100644
--- a/src/components/org/apache/jmeter/visualizers/GraphVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/GraphVisualizer.java
@@ -1,452 +1,456 @@
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
 import java.awt.FlowLayout;
 import java.awt.Image;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 import java.text.NumberFormat;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextField;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.gui.util.JMeterColor;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 
 /**
  * This class implements a statistical analyser that calculates both the average
  * and the standard deviation of the sampling process and outputs them as
  * autoscaling plots.
  *
  * Created February 8, 2001
  *
  */
 public class GraphVisualizer extends AbstractVisualizer implements ImageVisualizer, ItemListener, Clearable {
 
     private static final long serialVersionUID = 240L;
 
     private static final String ZERO = "0";  //$NON-NLS-1$
 
     private NumberFormat nf = NumberFormat.getInstance(); // OK, because used in synchronised method
 
     private CachingStatCalculator model;
 
     private JTextField maxYField = null;
 
     private JTextField minYField = null;
 
     private JTextField noSamplesField = null;
 
     private String minute = JMeterUtils.getResString("minute"); // $NON-NLS-1$
 
     private Graph graph;
 
     private JCheckBox data;
 
     private JCheckBox average;
 
     private JCheckBox deviation;
 
     private JCheckBox throughput;
 
     private JCheckBox median;
 
     private JTextField dataField;
 
     private JTextField averageField;
 
     private JTextField deviationField;
 
     private JTextField throughputField;
 
     private JTextField medianField;
 
     /**
      * Constructor for the GraphVisualizer object.
      */
     public GraphVisualizer() {
         model = new CachingStatCalculator("Graph");
         graph = new Graph(model);
         init();
     }
 
     /**
      * Gets the Image attribute of the GraphVisualizer object.
      *
      * @return the Image value
      */
     public Image getImage() {
         Image result = graph.createImage(graph.getWidth(), graph.getHeight());
 
         graph.paintComponent(result.getGraphics());
 
         return result;
     }
 
     public synchronized void updateGui(Sample s) {
         // We have received one more sample
         graph.updateGui(s);
         noSamplesField.setText(Long.toString(s.getCount()));
         dataField.setText(Long.toString(s.getData()));
         averageField.setText(Long.toString(s.getAverage()));
         deviationField.setText(Long.toString(s.getDeviation()));
         throughputField.setText(nf.format(60 * s.getThroughput()) + "/" + minute); // $NON-NLS-1$
         medianField.setText(Long.toString(s.getMedian()));
         updateYAxis();
     }
 
-    public void add(SampleResult res) {
-        updateGui(model.addSample(res));
+    public void add(final SampleResult res) {
+        JMeterUtils.runSafe(new Runnable() {            
+            public void run() {
+                updateGui(model.addSample(res));
+            }
+        });
     }
 
     public String getLabelResource() {
         return "graph_results_title"; // $NON-NLS-1$
     }
 
     public void itemStateChanged(ItemEvent e) {
         if (e.getItem() == data) {
             this.graph.enableData(e.getStateChange() == ItemEvent.SELECTED);
         } else if (e.getItem() == average) {
             this.graph.enableAverage(e.getStateChange() == ItemEvent.SELECTED);
         } else if (e.getItem() == deviation) {
             this.graph.enableDeviation(e.getStateChange() == ItemEvent.SELECTED);
         } else if (e.getItem() == throughput) {
             this.graph.enableThroughput(e.getStateChange() == ItemEvent.SELECTED);
         } else if (e.getItem() == median) {
             this.graph.enableMedian(e.getStateChange() == ItemEvent.SELECTED);
         }
         this.graph.repaint();
     }
 
     public void clearData() {
         graph.clearData();
         model.clear();
         dataField.setText(ZERO);
         averageField.setText(ZERO);
         deviationField.setText(ZERO);
         throughputField.setText("0/" + minute); //$NON-NLS-1$
         medianField.setText(ZERO);
         noSamplesField.setText(ZERO);
         updateYAxis();
         repaint();
     }
 
     @Override
     public String toString() {
         return "Show the samples analysis as dot plots";
     }
 
     /**
      * Update the max and min value of the Y axis.
      */
     private void updateYAxis() {
         maxYField.setText(Long.toString(graph.getGraphMax()));
         minYField.setText(ZERO);
     }
 
     /**
      * Initialize the GUI.
      */
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         this.setBorder(margin);
 
         // Set up the graph with header, footer, Y axis and graph display
         JPanel graphPanel = new JPanel(new BorderLayout());
         graphPanel.add(createYAxis(), BorderLayout.WEST);
         graphPanel.add(createChoosePanel(), BorderLayout.NORTH);
         graphPanel.add(createGraphPanel(), BorderLayout.CENTER);
         graphPanel.add(createGraphInfoPanel(), BorderLayout.SOUTH);
 
         // Add the main panel and the graph
         this.add(makeTitlePanel(), BorderLayout.NORTH);
         this.add(graphPanel, BorderLayout.CENTER);
     }
 
     // Methods used in creating the GUI
 
     /**
      * Creates the panel containing the graph's Y axis labels.
      *
      * @return the Y axis panel
      */
     private JPanel createYAxis() {
         JPanel graphYAxisPanel = new JPanel();
 
         graphYAxisPanel.setLayout(new BorderLayout());
 
         maxYField = createYAxisField(5);
         minYField = createYAxisField(3);
 
         graphYAxisPanel.add(createYAxisPanel("graph_results_ms", maxYField), BorderLayout.NORTH); // $NON-NLS-1$
         graphYAxisPanel.add(createYAxisPanel("graph_results_ms", minYField), BorderLayout.SOUTH); // $NON-NLS-1$
 
         return graphYAxisPanel;
     }
 
     /**
      * Creates a text field to be used for the value of a Y axis label. These
      * fields hold the minimum and maximum values for the graph. The units are
      * kept in a separate label outside of this field.
      *
      * @param length
      *            the number of characters which the field will use to calculate
      *            its preferred width. This should be set to the maximum number
      *            of digits that are expected to be necessary to hold the label
      *            value.
      *
      * @see #createYAxisPanel(String, JTextField)
      *
      * @return a text field configured to be used in the Y axis
      */
     private JTextField createYAxisField(int length) {
         JTextField field = new JTextField(length);
         field.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
         field.setEditable(false);
         field.setForeground(Color.black);
         field.setBackground(getBackground());
         field.setHorizontalAlignment(JTextField.RIGHT);
         return field;
     }
 
     /**
      * Creates a panel for an entire Y axis label. This includes the dynamic
      * value as well as the unit label.
      *
      * @param labelResourceName
      *            the name of the label resource. This is used to look up the
      *            label text using {@link JMeterUtils#getResString(String)}.
      *
      * @return a panel containing both the dynamic and static parts of a Y axis
      *         label
      */
     private JPanel createYAxisPanel(String labelResourceName, JTextField field) {
         JPanel panel = new JPanel(new FlowLayout());
         JLabel label = new JLabel(JMeterUtils.getResString(labelResourceName));
 
         panel.add(field);
         panel.add(label);
         return panel;
     }
 
     /**
      * Creates a panel which allows the user to choose which graphs to display.
      * This panel consists of a check box for each type of graph (current
      * sample, average, deviation, and throughput).
      *
      * @return a panel allowing the user to choose which graphs to display
      */
     private JPanel createChoosePanel() {
         JPanel chooseGraphsPanel = new JPanel();
 
         chooseGraphsPanel.setLayout(new FlowLayout());
         JLabel selectGraphsLabel = new JLabel(JMeterUtils.getResString("graph_choose_graphs")); //$NON-NLS-1$
         data = createChooseCheckBox("graph_results_data", Color.black); // $NON-NLS-1$
         average = createChooseCheckBox("graph_results_average", Color.blue); // $NON-NLS-1$
         deviation = createChooseCheckBox("graph_results_deviation", Color.red); // $NON-NLS-1$
         throughput = createChooseCheckBox("graph_results_throughput", JMeterColor.dark_green); // $NON-NLS-1$
         median = createChooseCheckBox("graph_results_median", JMeterColor.purple); // $NON-NLS-1$
 
         chooseGraphsPanel.add(selectGraphsLabel);
         chooseGraphsPanel.add(data);
         chooseGraphsPanel.add(average);
         chooseGraphsPanel.add(median);
         chooseGraphsPanel.add(deviation);
         chooseGraphsPanel.add(throughput);
         return chooseGraphsPanel;
     }
 
     /**
      * Creates a check box configured to be used to in the choose panel allowing
      * the user to select whether or not a particular kind of graph data will be
      * displayed.
      *
      * @param labelResourceName
      *            the name of the label resource. This is used to look up the
      *            label text using {@link JMeterUtils#getResString(String)}.
      * @param color
      *            the color used for the checkbox text. By convention this is
      *            the same color that is used to draw the graph and for the
      *            corresponding info field.
      *
      * @return a checkbox allowing the user to select whether or not a kind of
      *         graph data will be displayed
      */
     private JCheckBox createChooseCheckBox(String labelResourceName, Color color) {
         JCheckBox checkBox = new JCheckBox(JMeterUtils.getResString(labelResourceName));
         checkBox.setSelected(true);
         checkBox.addItemListener(this);
         checkBox.setForeground(color);
         return checkBox;
     }
 
     /**
      * Creates a scroll pane containing the actual graph of the results.
      *
      * @return a scroll pane containing the graph
      */
     private Component createGraphPanel() {
         JScrollPane graphScrollPanel = makeScrollPane(graph, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         graphScrollPanel.setViewportBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
         graphScrollPanel.setPreferredSize(graphScrollPanel.getMinimumSize());
 
         return graphScrollPanel;
     }
 
     /**
      * Creates a panel which numerically displays the current graph values.
      *
      * @return a panel showing the current graph values
      */
     private Box createGraphInfoPanel() {
         Box graphInfoPanel = Box.createHorizontalBox();
 
         noSamplesField = createInfoField(Color.black, 6);
         dataField = createInfoField(Color.black, 5);
         averageField = createInfoField(Color.blue, 5);
         deviationField = createInfoField(Color.red, 5);
         throughputField = createInfoField(JMeterColor.dark_green, 15);
         medianField = createInfoField(JMeterColor.purple, 5);
 
         graphInfoPanel.add(createInfoColumn(createInfoLabel("graph_results_no_samples", noSamplesField), // $NON-NLS-1$
                 noSamplesField, createInfoLabel("graph_results_deviation", deviationField), deviationField)); // $NON-NLS-1$
         graphInfoPanel.add(Box.createHorizontalGlue());
 
         graphInfoPanel.add(createInfoColumn(createInfoLabel("graph_results_latest_sample", dataField), dataField, // $NON-NLS-1$
                 createInfoLabel("graph_results_throughput", throughputField), throughputField)); // $NON-NLS-1$
         graphInfoPanel.add(Box.createHorizontalGlue());
 
         graphInfoPanel.add(createInfoColumn(createInfoLabel("graph_results_average", averageField), averageField, // $NON-NLS-1$
                 createInfoLabel("graph_results_median", medianField), medianField)); // $NON-NLS-1$
         graphInfoPanel.add(Box.createHorizontalGlue());
         return graphInfoPanel;
     }
 
     /**
      * Creates one of the fields used to display the graph's current values.
      *
      * @param color
      *            the color used to draw the value. By convention this is the
      *            same color that is used to draw the graph for this value and
      *            in the choose panel.
      * @param length
      *            the number of digits which the field should be able to display
      *
      * @return a text field configured to display one of the current graph
      *         values
      */
     private JTextField createInfoField(Color color, int length) {
         JTextField field = new JTextField(length);
         field.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         field.setEditable(false);
         field.setForeground(color);
         field.setBackground(getBackground());
 
         // The text field should expand horizontally, but have
         // a fixed height
         field.setMaximumSize(new Dimension(field.getMaximumSize().width, field.getPreferredSize().height));
         return field;
     }
 
     /**
      * Creates a label for one of the fields used to display the graph's current
      * values. Neither the label created by this method or the
      * <code>field</code> passed as a parameter is added to the GUI here.
      *
      * @param labelResourceName
      *            the name of the label resource. This is used to look up the
      *            label text using {@link JMeterUtils#getResString(String)}.
      * @param field
      *            the field this label is being created for.
      */
     private JLabel createInfoLabel(String labelResourceName, JTextField field) {
         JLabel label = new JLabel(JMeterUtils.getResString(labelResourceName));
         label.setForeground(field.getForeground());
         label.setLabelFor(field);
         return label;
     }
 
     /**
      * Creates a panel containing two pairs of labels and fields for displaying
      * the current graph values. This method exists to help with laying out the
      * fields in columns. If one or more components are null then these
      * components will be represented by blank space.
      *
      * @param label1
      *            the label for the first field. This label will be placed in
      *            the upper left section of the panel. If this parameter is
      *            null, this section of the panel will be left blank.
      * @param field1
      *            the field corresponding to the first label. This field will be
      *            placed in the upper right section of the panel. If this
      *            parameter is null, this section of the panel will be left
      *            blank.
      * @param label2
      *            the label for the second field. This label will be placed in
      *            the lower left section of the panel. If this parameter is
      *            null, this section of the panel will be left blank.
      * @param field2
      *            the field corresponding to the second label. This field will
      *            be placed in the lower right section of the panel. If this
      *            parameter is null, this section of the panel will be left
      *            blank.
      */
     private Box createInfoColumn(JLabel label1, JTextField field1, JLabel label2, JTextField field2) {
         // This column actually consists of a row with two sub-columns
         // The first column contains the labels, and the second
         // column contains the fields.
         Box row = Box.createHorizontalBox();
         Box col = Box.createVerticalBox();
         col.add(label1 != null ? label1 : Box.createVerticalGlue());
         col.add(label2 != null ? label2 : Box.createVerticalGlue());
         row.add(col);
 
         row.add(Box.createHorizontalStrut(5));
 
         col = Box.createVerticalBox();
         col.add(field1 != null ? field1 : Box.createVerticalGlue());
         col.add(field2 != null ? field2 : Box.createVerticalGlue());
         row.add(col);
 
         row.add(Box.createHorizontalStrut(5));
 
         return row;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java b/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java
index 521f08348..7c979cf95 100644
--- a/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java
@@ -1,439 +1,444 @@
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
 import java.awt.GridBagConstraints;
 import java.awt.GridBagLayout;
 import java.awt.Insets;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.mail.MessagingException;
 import javax.mail.internet.AddressException;
 import javax.swing.BorderFactory;
 import javax.swing.JButton;
 import javax.swing.JComboBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JPasswordField;
 import javax.swing.JTextField;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.reporters.MailerModel;
 import org.apache.jmeter.reporters.MailerResultCollector;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /*
  * TODO : - Create a subpanel for other visualizers - connect to the properties. -
  * Get the specific URL that is failing. - add a seperate interface to collect
  * the thrown failure messages. - - suggestions ;-)
  */
 
 /**
  * This class implements a visualizer that mails a message when an error occurs.
  *
  */
 public class MailerVisualizer extends AbstractVisualizer implements ActionListener, Clearable, ChangeListener {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private JButton testerButton;
 
     private JTextField addressField;
 
     private JTextField fromField;
 
     private JTextField smtpHostField;
 
     private JTextField smtpPortField;
 
     private JTextField failureSubjectField;
 
     private JTextField successSubjectField;
 
     private JTextField failureField;
 
     private JTextField failureLimitField;
 
     private JTextField successLimitField;
 
     private JTextField smtpLoginField;
 
     private JTextField smtpPasswordField;
 
     private JComboBox authTypeCombo;
 
 
     // private JPanel mainPanel;
     // private JLabel panelTitleLabel;
 
     /**
      * Constructs the MailerVisualizer and initializes its GUI.
      */
     public MailerVisualizer() {
         super();
         setModel(new MailerResultCollector());
         // initialize GUI.
         initGui();
     }
 
     public JPanel getControlPanel() {
         return this;
     }
 
     /**
      * Clears any stored sampling-informations.
      */
     public synchronized void clearData() {
         if (getModel() != null) {
             MailerModel model = ((MailerResultCollector) getModel()).getMailerModel();
             model.clear();
             updateVisualizer(model);
         }
     }
 
-    public synchronized void add(SampleResult res) {
+    public void add(final SampleResult res) {
         if (getModel() != null) {
-            MailerModel model = ((MailerResultCollector) getModel()).getMailerModel();
-            model.add(res);//this is a different model from the one used by the result collector
-            updateVisualizer(model);
+            JMeterUtils.runSafe(new Runnable() {
+                public void run() {
+                    MailerModel model = ((MailerResultCollector) getModel()).getMailerModel();
+                    // method called by add is synchronized
+                    model.add(res);//this is a different model from the one used by the result collector
+                    updateVisualizer(model);                    
+                }
+            });
         }
     }
 
     @Override
     public String toString() {
         return "E-Mail Notification";
     }
 
     /**
      * Initializes the GUI. Lays out components and adds them to the container.
      */
     private void initGui() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new VerticalPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         this.setBorder(margin);
 
         // NAME
         mainPanel.add(makeTitlePanel());
 
         // mailer panel
         JPanel mailerPanel = new JPanel();
 
         mailerPanel.setBorder(BorderFactory
                 .createTitledBorder(BorderFactory.createEtchedBorder(), getAttributesTitle()));
         GridBagLayout g = new GridBagLayout();
 
         mailerPanel.setLayout(g);
         GridBagConstraints c = new GridBagConstraints();
 
         c.anchor = GridBagConstraints.NORTHWEST;
         c.insets = new Insets(0, 0, 0, 0);
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("From:"));
 
         fromField = new JTextField(25);
         fromField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(fromField, c);
         mailerPanel.add(fromField);
 
         c.anchor = GridBagConstraints.NORTHWEST;
         c.insets = new Insets(0, 0, 0, 0);
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("Addressee(s):"));
 
         addressField = new JTextField(25);
         addressField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(addressField, c);
         mailerPanel.add(addressField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("SMTP Host:"));
 
         smtpHostField = new JTextField(25);
         smtpHostField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(smtpHostField, c);
         mailerPanel.add(smtpHostField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("SMTP Port:"));
 
         smtpPortField = new JTextField(25);
         smtpPortField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(smtpPortField, c);
         mailerPanel.add(smtpPortField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("SMTP Login:"));
 
         smtpLoginField = new JTextField(25);
         smtpLoginField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(smtpLoginField, c);
         mailerPanel.add(smtpLoginField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("SMTP Password:"));
 
         smtpPasswordField = new JPasswordField(25);
         smtpPasswordField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(smtpPasswordField, c);
         mailerPanel.add(smtpPasswordField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("AUTH TYPE"));
         
         authTypeCombo = new JComboBox(new Object[] { 
                 MailerModel.MailAuthType.NONE.toString(), 
                 MailerModel.MailAuthType.SSL.toString(),
                 MailerModel.MailAuthType.TLS.toString()});
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(authTypeCombo, c);
         mailerPanel.add(authTypeCombo);
         
         
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("Failure Subject:"));
 
         failureSubjectField = new JTextField(25);
         failureSubjectField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(failureSubjectField, c);
         mailerPanel.add(failureSubjectField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("Success Subject:"));
 
         successSubjectField = new JTextField(25);
         successSubjectField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(successSubjectField, c);
         mailerPanel.add(successSubjectField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("Failure Limit:"));
 
         failureLimitField = new JTextField("2", 25);
         failureLimitField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(failureLimitField, c);
         mailerPanel.add(failureLimitField);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("Success Limit:"));
 
         successLimitField = new JTextField("2", 25);
         successLimitField.setEditable(true);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(successLimitField, c);
         mailerPanel.add(successLimitField);
 
         testerButton = new JButton("Test Mail");
         testerButton.addActionListener(this);
         testerButton.setEnabled(true);
         c.gridwidth = 1;
         g.setConstraints(testerButton, c);
         mailerPanel.add(testerButton);
 
         c.gridwidth = 1;
         mailerPanel.add(new JLabel("Failures:"));
         failureField = new JTextField(6);
         failureField.setEditable(false);
         c.gridwidth = GridBagConstraints.REMAINDER;
         g.setConstraints(failureField, c);
         mailerPanel.add(failureField);
 
         mainPanel.add(mailerPanel);
 
         this.add(mainPanel, BorderLayout.WEST);
     }
 
     public String getLabelResource() {
         return "mailer_visualizer_title"; //$NON-NLS-1$
     }
 
     /**
      * Returns a String for the title of the attributes-panel as set up in the
      * properties-file using the lookup-constant "mailer_attributes_panel".
      *
      * @return The title of the component.
      */
     public String getAttributesTitle() {
         return JMeterUtils.getResString("mailer_attributes_panel"); //$NON-NLS-1$
     }
 
     // ////////////////////////////////////////////////////////////
     //
     // Implementation of the ActionListener-Interface.
     //
     // ////////////////////////////////////////////////////////////
 
     /**
      * Reacts on an ActionEvent (like pressing a button).
      *
      * @param e
      *            The ActionEvent with information about the event and its
      *            source.
      */
     public void actionPerformed(ActionEvent e) {
         if (e.getSource() == testerButton) {
             ResultCollector testElement = getModel();
             modifyTestElement(testElement);
             try {
                 MailerModel model = ((MailerResultCollector) testElement).getMailerModel();
                 model.sendTestMail();
                 displayMessage(JMeterUtils.getResString("mail_sent"), false); //$NON-NLS-1$
             } catch (AddressException ex) {
                 log.error("Invalid mail address ", ex);
                 displayMessage(JMeterUtils.getResString("invalid_mail_address") //$NON-NLS-1$
                         + "\n" + ex.getMessage(), true); //$NON-NLS-1$
             } catch (MessagingException ex) {
                 log.error("Couldn't send mail...", ex);
                 displayMessage(JMeterUtils.getResString("invalid_mail") //$NON-NLS-1$
                         + "\n" + ex.getMessage(), true); //$NON-NLS-1$
             }
         }
     }
 
     // ////////////////////////////////////////////////////////////
     //
     // Methods used to store and retrieve the MailerVisualizer.
     //
     // ////////////////////////////////////////////////////////////
 
     /**
      * Restores MailerVisualizer.
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         updateVisualizer(((MailerResultCollector) el).getMailerModel());
     }
 
     /**
      * Makes MailerVisualizer storable.
      */
     @Override
     public TestElement createTestElement() {
         ResultCollector model = getModel();
         if (model == null) {
             model = new MailerResultCollector();
             setModel(model);
         }
         modifyTestElement(model);
         return model;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void modifyTestElement(TestElement c) {
         super.modifyTestElement(c);
         MailerModel mailerModel = ((MailerResultCollector) c).getMailerModel();
         mailerModel.setFailureLimit(failureLimitField.getText());
         mailerModel.setFailureSubject(failureSubjectField.getText());
         mailerModel.setFromAddress(fromField.getText());
         mailerModel.setSmtpHost(smtpHostField.getText());
         mailerModel.setSmtpPort(smtpPortField.getText());
         mailerModel.setLogin(smtpLoginField.getText());
         mailerModel.setPassword(smtpPasswordField.getText());
         mailerModel.setMailAuthType(
                 authTypeCombo.getSelectedItem().toString());
         mailerModel.setSuccessLimit(successLimitField.getText());
         mailerModel.setSuccessSubject(successSubjectField.getText());
         mailerModel.setToAddress(addressField.getText());
     }
 
     // ////////////////////////////////////////////////////////////
     //
     // Methods to implement the ModelListener.
     //
     // ////////////////////////////////////////////////////////////
 
     /**
      * Notifies this Visualizer about model-changes. Causes the Visualizer to
      * query the model about its new state.
      */
     private void updateVisualizer(MailerModel model) {
         addressField.setText(model.getToAddress());
         fromField.setText(model.getFromAddress());
         smtpHostField.setText(model.getSmtpHost());
         smtpPortField.setText(model.getSmtpPort());
         smtpLoginField.setText(model.getLogin());
         smtpPasswordField.setText(model.getPassword());
         authTypeCombo.setSelectedItem(model.getMailAuthType().toString());
         successSubjectField.setText(model.getSuccessSubject());
         failureSubjectField.setText(model.getFailureSubject());
         failureLimitField.setText(String.valueOf(model.getFailureLimit()));
         failureField.setText(String.valueOf(model.getFailureCount()));
         successLimitField.setText(String.valueOf(model.getSuccessLimit()));
         repaint();
     }
 
     /**
      * Shows a message using a DialogBox.
      */
     private void displayMessage(String message, boolean isError) {
         int type = 0;
 
         if (isError) {
             type = JOptionPane.ERROR_MESSAGE;
         } else {
             type = JOptionPane.INFORMATION_MESSAGE;
         }
         JOptionPane.showMessageDialog(null, message, isError ? "Error" : "Information", type);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void stateChanged(ChangeEvent e) {
         if (e.getSource() instanceof MailerModel) {
             MailerModel testModel = (MailerModel) e.getSource();
             updateVisualizer(testModel);
         } else {
             super.stateChanged(e);
         }
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/SplineVisualizer.java b/src/components/org/apache/jmeter/visualizers/SplineVisualizer.java
index 93e0d8e77..608dc4592 100644
--- a/src/components/org/apache/jmeter/visualizers/SplineVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/SplineVisualizer.java
@@ -1,333 +1,337 @@
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
 import java.awt.Dimension;
 import java.awt.Graphics;
 import java.awt.GridLayout;
 import java.awt.Image;
 
 import javax.swing.BorderFactory;
 import javax.swing.JComponent;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 
 /**
  * This class implements a statistical analyser that takes samples to process a
  * Spline interpolated curve. Currently, it tries to look mostly like the
  * GraphVisualizer.
  *
  */
 public class SplineVisualizer extends AbstractVisualizer implements ImageVisualizer, GraphListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final String SUFFIX_MS = " ms";  //$NON-NLS-1$
 
     protected final Color BACKGROUND_COLOR = getBackground();
 
     protected final Color MINIMUM_COLOR = new Color(0F, 0.5F, 0F);
 
     protected final Color MAXIMUM_COLOR = new Color(0.9F, 0F, 0F);
 
     protected final Color AVERAGE_COLOR = new Color(0F, 0F, 0.75F);
 
     protected final Color INCOMING_COLOR = Color.black;
 
     protected final int NUMBERS_TO_DISPLAY = 4;
 
     protected final boolean FILL_UP_WITH_ZEROS = false;
 
     private transient SplineGraph graph = null;
 
     private JLabel minimumLabel = null;
 
     private JLabel maximumLabel = null;
 
     private JLabel averageLabel = null;
 
     private JLabel incomingLabel = null;
 
     private JLabel minimumNumberLabel = null;
 
     private JLabel maximumNumberLabel = null;
 
     private JLabel averageNumberLabel = null;
 
     private JLabel incomingNumberLabel = null;
 
     private transient SplineModel model;
 
     public SplineVisualizer() {
         super();
         model = new SplineModel();
         graph = new SplineGraph();
         this.model.setListener(this);
         setGUI();
     }
 
-    public void add(SampleResult res) {
-        model.add(res);
+    public void add(final SampleResult res) {
+        JMeterUtils.runSafe(new Runnable() {            
+            public void run() {
+                model.add(res);
+            }
+        });
     }
 
     public String getLabelResource() {
         return "spline_visualizer_title"; //$NON-NLS-1$
     }
 
     public void updateGui(Sample s) {
         updateGui();
     }
 
     public void clearData() {
         model.clearData();
     }
 
     private void setGUI() {
         Color backColor = BACKGROUND_COLOR;
 
         this.setBackground(backColor);
 
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
 
         // NAME
         mainPanel.add(makeTitlePanel());
 
         maximumLabel = new JLabel(JMeterUtils.getResString("spline_visualizer_maximum")); //$NON-NLS-1$
         maximumLabel.setForeground(MAXIMUM_COLOR);
         maximumLabel.setBackground(backColor);
 
         averageLabel = new JLabel(JMeterUtils.getResString("spline_visualizer_average")); //$NON-NLS-1$
         averageLabel.setForeground(AVERAGE_COLOR);
         averageLabel.setBackground(backColor);
 
         incomingLabel = new JLabel(JMeterUtils.getResString("spline_visualizer_incoming")); //$NON-NLS-1$
         incomingLabel.setForeground(INCOMING_COLOR);
         incomingLabel.setBackground(backColor);
 
         minimumLabel = new JLabel(JMeterUtils.getResString("spline_visualizer_minimum")); //$NON-NLS-1$
         minimumLabel.setForeground(MINIMUM_COLOR);
         minimumLabel.setBackground(backColor);
 
         maximumNumberLabel = new JLabel("0 ms"); //$NON-NLS-1$
         maximumNumberLabel.setHorizontalAlignment(JLabel.RIGHT);
         maximumNumberLabel.setForeground(MAXIMUM_COLOR);
         maximumNumberLabel.setBackground(backColor);
 
         averageNumberLabel = new JLabel("0 ms"); //$NON-NLS-1$
         averageNumberLabel.setHorizontalAlignment(JLabel.RIGHT);
         averageNumberLabel.setForeground(AVERAGE_COLOR);
         averageNumberLabel.setBackground(backColor);
 
         incomingNumberLabel = new JLabel("0 ms"); //$NON-NLS-1$
         incomingNumberLabel.setHorizontalAlignment(JLabel.RIGHT);
         incomingNumberLabel.setForeground(INCOMING_COLOR);
         incomingNumberLabel.setBackground(backColor);
 
         minimumNumberLabel = new JLabel("0 ms"); //$NON-NLS-1$
         minimumNumberLabel.setHorizontalAlignment(JLabel.RIGHT);
         minimumNumberLabel.setForeground(MINIMUM_COLOR);
         minimumNumberLabel.setBackground(backColor);
 
         // description Panel
         JPanel labelPanel = new JPanel();
 
         labelPanel.setLayout(new GridLayout(0, 1));
         labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
         labelPanel.setBackground(backColor);
         labelPanel.add(maximumLabel);
         labelPanel.add(averageLabel);
         if (model.SHOW_INCOMING_SAMPLES) {
             labelPanel.add(incomingLabel);
         }
         labelPanel.add(minimumLabel);
         // number Panel
         JPanel numberPanel = new JPanel();
 
         numberPanel.setLayout(new GridLayout(0, 1));
         numberPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
         numberPanel.setBackground(backColor);
         numberPanel.add(maximumNumberLabel);
         numberPanel.add(averageNumberLabel);
         if (model.SHOW_INCOMING_SAMPLES) {
             numberPanel.add(incomingNumberLabel);
         }
         numberPanel.add(minimumNumberLabel);
         // information display Panel
         JPanel infoPanel = new JPanel();
 
         infoPanel.setLayout(new BorderLayout());
         infoPanel.add(labelPanel, BorderLayout.CENTER);
         infoPanel.add(numberPanel, BorderLayout.EAST);
 
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(infoPanel, BorderLayout.WEST);
         this.add(graph, BorderLayout.CENTER);
         // everyone is free to swing on its side :)
         // add(infoPanel, BorderLayout.EAST);
     }
 
     public void updateGui() {
         repaint();
         synchronized (this) {
             setMinimum(model.getMinimum());
             setMaximum(model.getMaximum());
             setAverage(model.getAverage());
             setIncoming(model.getCurrent());
         }
     }
 
     @Override
     public String toString() {
         return "Show the samples analysis as a Spline curve";
     }
 
     private String formatMeasureToDisplay(long measure) {
         String numberString = String.valueOf(measure);
 
         if (FILL_UP_WITH_ZEROS) {
             for (int i = numberString.length(); i < NUMBERS_TO_DISPLAY; i++) {
                 numberString = "0" + numberString; //$NON-NLS-1$
             }
         }
         return numberString;
     }
 
     private void setMinimum(long n) {
         String text = this.formatMeasureToDisplay(n) + SUFFIX_MS;
 
         this.minimumNumberLabel.setText(text);
     }
 
     private void setMaximum(long n) {
         String text = this.formatMeasureToDisplay(n) + SUFFIX_MS;
 
         this.maximumNumberLabel.setText(text);
     }
 
     private void setAverage(long n) {
         String text = this.formatMeasureToDisplay(n) + SUFFIX_MS;
 
         this.averageNumberLabel.setText(text);
     }
 
     private void setIncoming(long n) {
         String text = this.formatMeasureToDisplay(n) + SUFFIX_MS;
 
         this.incomingNumberLabel.setText(text);
     }
 
     public JPanel getControlPanel() {// TODO - is this needed?
         return this;
     }
 
     public Image getImage() {
         Image result = graph.createImage(graph.getWidth(), graph.getHeight());
 
         graph.paintComponent(result.getGraphics());
 
         return result;
     }
 
     /**
      * Component showing a Spline curve.
      *
      */
     public class SplineGraph extends JComponent {
 
         private static final long serialVersionUID = 240L;
 
         private final Color WAITING_COLOR = Color.darkGray;
 
         private int lastWidth = -1;
 
         private int lastHeight = -1;
 
         private int[] plot = null;
 
         public SplineGraph() {
         }
 
         /**
          * Clear the Spline graph and get ready for the next wave.
          */
         public void clear() {
             lastWidth = -1;
             lastHeight = -1;
             plot = null;
             this.repaint();
         }
 
         @Override
         public void paintComponent(Graphics g) {
             super.paintComponent(g);
 
             Dimension dimension = this.getSize();
             int width = dimension.width;
             int height = dimension.height;
 
             if (model.getDataCurve() == null) {
                 g.setColor(this.getBackground());
                 g.fillRect(0, 0, width, height);
                 g.setColor(WAITING_COLOR);
                 g.drawString(JMeterUtils.getResString("spline_visualizer_waitingmessage"),  //$NON-NLS-1$
                         (width - 120) / 2, height - (height - 12) / 2);
                 return;
             }
 
             // boolean resized = true;
 
             if (width == lastWidth && height == lastHeight) {
                 // dimension of the SplineGraph is the same
                 // resized = false;
             } else {
                 // dimension changed
                 // resized = true;
                 lastWidth = width;
                 lastHeight = height;
             }
 
             this.plot = model.getDataCurve().getPlots(width, height); // rounds!
 
             int n = plot.length;
             int curY = plot[0];
 
             for (int i = 1; i < n; i++) {
                 g.setColor(Color.black);
                 g.drawLine(i - 1, height - curY - 1, i, height - plot[i] - 1);
                 curY = plot[i];
             }
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
index debddd3c9..eddfa3940 100644
--- a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
@@ -1,748 +1,752 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.FileNotFoundException;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.regex.PatternSyntaxException;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JColorChooser;
 import javax.swing.JComboBox;
 import javax.swing.JComponent;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTable;
 import javax.swing.JTextField;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
 
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.SaveGraphics;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.gui.NumberRenderer;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RateRenderer;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Aggregrate Table-Based Reporting Visualizer for JMeter. Props to the people
  * who've done the other visualizers ahead of me (Stefano Mazzocchi), who I
  * borrowed code from to start me off (and much code may still exist). Thank
  * you!
  *
  */
 public class StatGraphVisualizer extends AbstractVisualizer implements Clearable, ActionListener {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final String[] COLUMNS = { JMeterUtils.getResString("sampler_label"), //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_count"),         //$NON-NLS-1$
             JMeterUtils.getResString("average"),                        //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_median"),        //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_90%_line"),      //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_min"),           //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_max"),           //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_error%"),        //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_rate"),          //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_bandwidth") };   //$NON-NLS-1$
 
     private final String[] GRAPH_COLUMNS = {JMeterUtils.getResString("average"),//$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_median"),        //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_90%_line"),      //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_min"),           //$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_max")};          //$NON-NLS-1$
 
     private final String TOTAL_ROW_LABEL =
         JMeterUtils.getResString("aggregate_report_total_label");       //$NON-NLS-1$
     
     private final Border MARGIN = new EmptyBorder(0, 5, 0, 5);
 
     private JTable myJTable;
 
     private JScrollPane myScrollPane;
 
     private transient ObjectTableModel model;
 
     /**
      * Lock used to protect tableRows update + model update
      */
     private final transient Object lock = new Object();
     
     private final Map<String, SamplingStatCalculator> tableRows =
         new ConcurrentHashMap<String, SamplingStatCalculator>();
 
     private AxisGraph graphPanel = null;
 
     private JPanel settingsPane = null;
 
     private JSplitPane spane = null;
 
     //NOT USED protected double[][] data = null;
 
     private JTabbedPane tabbedGraph = new JTabbedPane(JTabbedPane.TOP);
 
     private JButton displayButton =
         new JButton(JMeterUtils.getResString("aggregate_graph_display"));                //$NON-NLS-1$
 
     private JButton saveGraph =
         new JButton(JMeterUtils.getResString("aggregate_graph_save"));                    //$NON-NLS-1$
 
     private JButton saveTable =
         new JButton(JMeterUtils.getResString("aggregate_graph_save_table"));            //$NON-NLS-1$
 
     private JButton chooseBarColor =
         new JButton(JMeterUtils.getResString("aggregate_graph_choose_bar_color"));            //$NON-NLS-1$
 
     private JButton chooseForeColor =
         new JButton(JMeterUtils.getResString("aggregate_graph_choose_foreground_color"));            //$NON-NLS-1$
 
     private JButton syncWithName =
         new JButton(JMeterUtils.getResString("aggregate_graph_sync_with_name"));            //$NON-NLS-1$
 
     private JCheckBox saveHeaders = // should header be saved with the data?
         new JCheckBox(JMeterUtils.getResString("aggregate_graph_save_table_header"));    //$NON-NLS-1$
 
     private JLabeledTextField graphTitle =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_user_title"));    //$NON-NLS-1$
 
     private JLabeledTextField maxLengthXAxisLabel =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_max_length_xaxis_label"));//$NON-NLS-1$
 
     private JLabeledTextField maxValueYAxisLabel =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_yaxis_max_value"));//$NON-NLS-1$
 
     /**
      * checkbox for use dynamic graph size
      */
     private JCheckBox dynamicGraphSize = new JCheckBox(JMeterUtils.getResString("aggregate_graph_dynamic_size")); // $NON-NLS-1$
 
     private JLabeledTextField graphWidth =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_width"));        //$NON-NLS-1$
     private JLabeledTextField graphHeight =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_height"));        //$NON-NLS-1$
 
     private String yAxisLabel = JMeterUtils.getResString("aggregate_graph_response_time");//$NON-NLS-1$
 
     private String yAxisTitle = JMeterUtils.getResString("aggregate_graph_ms");        //$NON-NLS-1$
 
     private boolean saveGraphToFile = false;
 
     private int defaultWidth = 400;
 
     private int defaultHeight = 300;
 
     private JLabel currentColor = new JLabel(JMeterUtils.getResString("aggregate_graph_current_colors"));   //$NON-NLS-1$
 
     private JComboBox columnsList = new JComboBox(GRAPH_COLUMNS);
 
     private JCheckBox columnSelection = new JCheckBox(JMeterUtils.getResString("aggregate_graph_column_selection"), false); //$NON-NLS-1$
 
     private JTextField columnMatchLabel = new JTextField();
 
     private JButton reloadButton = new JButton(JMeterUtils.getResString("aggregate_graph_reload_data")); // $NON-NLS-1$
 
     private JCheckBox caseChkBox = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_case"), false); // $NON-NLS-1$
 
     private JCheckBox regexpChkBox = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_regexp"), true); // $NON-NLS-1$
 
     private JComboBox titleFontNameList = new JComboBox(StatGraphProperties.getFontNameMap().keySet().toArray());
 
     private JComboBox titleFontSizeList = new JComboBox(StatGraphProperties.fontSize);
 
     private JComboBox titleFontStyleList = new JComboBox(StatGraphProperties.getFontStyleMap().keySet().toArray());
 
     private JComboBox fontNameList = new JComboBox(StatGraphProperties.getFontNameMap().keySet().toArray());
 
     private JComboBox fontSizeList = new JComboBox(StatGraphProperties.fontSize);
 
     private JComboBox fontStyleList = new JComboBox(StatGraphProperties.getFontStyleMap().keySet().toArray());
 
     private JComboBox legendPlacementList = new JComboBox(StatGraphProperties.getPlacementNameMap().keySet().toArray());
 
     private JCheckBox drawOutlinesBar = new JCheckBox(JMeterUtils.getResString("aggregate_graph_draw_outlines"), true); // Default checked // $NON-NLS-1$
 
     private JCheckBox numberShowGrouping = new JCheckBox(JMeterUtils.getResString("aggregate_graph_number_grouping"), true); // Default checked // $NON-NLS-1$
 
     private Color colorBarGraph = Color.YELLOW;
 
     private Color colorForeGraph = Color.BLACK;
 
     public StatGraphVisualizer() {
         super();
         model = new ObjectTableModel(COLUMNS,
                 SamplingStatCalculator.class,
                 new Functor[] {
                 new Functor("getLabel"),                    //$NON-NLS-1$
                 new Functor("getCount"),                    //$NON-NLS-1$
                 new Functor("getMeanAsNumber"),                //$NON-NLS-1$
                 new Functor("getMedian"),                    //$NON-NLS-1$
                 new Functor("getPercentPoint",                //$NON-NLS-1$
                 new Object[] { new Float(.900) }),
                 new Functor("getMin"),                        //$NON-NLS-1$
                 new Functor("getMax"),                         //$NON-NLS-1$
                 new Functor("getErrorPercentage"),            //$NON-NLS-1$
                 new Functor("getRate"),                        //$NON-NLS-1$
                 new Functor("getKBPerSecond") },            //$NON-NLS-1$
                 new Functor[] { null, null, null, null, null, null, null, null,    null, null },
                 new Class[] { String.class, Long.class, Long.class, Long.class, Long.class, Long.class,
                 Long.class, String.class, String.class, String.class });
         clearData();
         init();
     }
 
     // Column renderers
     private static final TableCellRenderer[] RENDERERS =
         new TableCellRenderer[]{
             null, // Label
             null, // count
             null, // Mean
             null, // median
             null, // 90%
             null, // Min
             null, // Max
             new NumberRenderer("#0.00%"), // Error %age
             new RateRenderer("#.0"),      // Throughpur
             new NumberRenderer("#.0"),    // pageSize
         };
 
     public static boolean testFunctors(){
         StatGraphVisualizer instance = new StatGraphVisualizer();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
     public String getLabelResource() {
         return "aggregate_graph_title";                        //$NON-NLS-1$
     }
 
-    public void add(SampleResult res) {
-        SamplingStatCalculator row = null;
+    public void add(final SampleResult res) {
         final String sampleLabel = res.getSampleLabel();
         Matcher matcher = null;
         if (columnSelection.isSelected() && columnMatchLabel.getText() != null && columnMatchLabel.getText().length() > 0) {
                 Pattern pattern = createPattern(columnMatchLabel.getText());
                 matcher = pattern.matcher(sampleLabel);
         }
         if ((matcher == null) || (matcher.find())) {
-            synchronized (lock) {
-                row = tableRows.get(sampleLabel);
-                if (row == null) {
-                    row = new SamplingStatCalculator(sampleLabel);
-                    tableRows.put(row.getLabel(), row);
-                    model.insertRow(row, model.getRowCount() - 1);
+            JMeterUtils.runSafe(new Runnable() {
+                public void run() {
+                    SamplingStatCalculator row = null;
+                    synchronized (lock) {
+                        row = tableRows.get(sampleLabel);
+                        if (row == null) {
+                            row = new SamplingStatCalculator(sampleLabel);
+                            tableRows.put(row.getLabel(), row);
+                            model.insertRow(row, model.getRowCount() - 1);
+                        }
+                    }
+                    row.addSample(res);
+                    tableRows.get(TOTAL_ROW_LABEL).addSample(res);
+                    model.fireTableDataChanged();                    
                 }
-            }
-            row.addSample(res);
-            tableRows.get(TOTAL_ROW_LABEL).addSample(res);
-            model.fireTableDataChanged();
+            });
         }
     }
 
     /**
      * Clears this visualizer and its model, and forces a repaint of the table.
      */
     public void clearData() {
         synchronized (lock) {
 	        model.clearData();
 	        tableRows.clear();
 	        tableRows.put(TOTAL_ROW_LABEL, new SamplingStatCalculator(TOTAL_ROW_LABEL));
 	        model.addRow(tableRows.get(TOTAL_ROW_LABEL));
         }
     }
 
     /**
      * Main visualizer setup.
      */
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
         Border margin2 = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         mainPanel.add(makeTitlePanel());
 
         myJTable = new JTable(model);
         myJTable.setPreferredScrollableViewportSize(new Dimension(500, 80));
         RendererUtils.applyRenderers(myJTable, RENDERERS);
         myScrollPane = new JScrollPane(myJTable);
 
         settingsPane = new VerticalPanel();
         settingsPane.setBorder(margin2);
 
         graphPanel = new AxisGraph();
         graphPanel.setPreferredSize(new Dimension(defaultWidth, defaultHeight));
 
         settingsPane.add(createGraphActionsPane());
         settingsPane.add(createGraphColumnPane());
         settingsPane.add(createGraphTitlePane());
         settingsPane.add(createGraphDimensionPane());
         JPanel axisPane = new JPanel(new BorderLayout());
         axisPane.add(createGraphXAxisPane(), BorderLayout.WEST);
         axisPane.add(createGraphYAxisPane(), BorderLayout.CENTER);
         settingsPane.add(axisPane);
         settingsPane.add(createLegendPane());
 
         tabbedGraph.addTab(JMeterUtils.getResString("aggregate_graph_tab_settings"), settingsPane); //$NON-NLS-1$
         tabbedGraph.addTab(JMeterUtils.getResString("aggregate_graph_tab_graph"), graphPanel); //$NON-NLS-1$
 
         spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         spane.setLeftComponent(myScrollPane);
         spane.setRightComponent(tabbedGraph);
         spane.setResizeWeight(.2);
         spane.setContinuousLayout(true);
 
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(spane, BorderLayout.CENTER);
     }
 
     public void makeGraph() {
         Dimension size = graphPanel.getSize();
         String wstr = graphWidth.getText();
         String hstr = graphHeight.getText();
         String lstr = maxLengthXAxisLabel.getText();
         int width = (int) size.getWidth();
         if (wstr.length() != 0) {
             width = Integer.parseInt(wstr);
         }
         int height = (int) size.getHeight();
         if (hstr.length() != 0) {
             height = Integer.parseInt(hstr);
         }
         if (lstr.length() == 0) {
             lstr = "20";//$NON-NLS-1$
         }
         int maxLength = Integer.parseInt(lstr);
         String yAxisStr = maxValueYAxisLabel.getText();
         int maxYAxisScale = yAxisStr.length() == 0 ? 0 : Integer.parseInt(yAxisStr);
 
         graphPanel.setData(this.getData());
         graphPanel.setTitle(graphTitle.getText());
         graphPanel.setMaxLength(maxLength);
         graphPanel.setMaxYAxisScale(maxYAxisScale);
         graphPanel.setXAxisLabels(getAxisLabels());
         graphPanel.setXAxisTitle((String) columnsList.getSelectedItem());
         graphPanel.setYAxisLabels(this.yAxisLabel);
         graphPanel.setYAxisTitle(this.yAxisTitle);
         graphPanel.setColor(colorBarGraph);
         graphPanel.setForeColor(colorForeGraph);
         graphPanel.setOutlinesBarFlag(drawOutlinesBar.isSelected());
         graphPanel.setShowGrouping(numberShowGrouping.isSelected());
         graphPanel.setLegendPlacement(StatGraphProperties.getPlacementNameMap()
                 .get(legendPlacementList.getSelectedItem()).intValue());
 
         graphPanel.setTitleFont(new Font(StatGraphProperties.getFontNameMap().get(titleFontNameList.getSelectedItem()),
                 StatGraphProperties.getFontStyleMap().get(titleFontStyleList.getSelectedItem()).intValue(),
                 Integer.parseInt((String) titleFontSizeList.getSelectedItem())));
         graphPanel.setLegendFont(new Font(StatGraphProperties.getFontNameMap().get(fontNameList.getSelectedItem()),
                 StatGraphProperties.getFontStyleMap().get(fontStyleList.getSelectedItem()).intValue(),
                 Integer.parseInt((String) fontSizeList.getSelectedItem())));
 
         graphPanel.setHeight(height);
         graphPanel.setWidth(width);
         spane.repaint();
     }
 
     public double[][] getData() {
         if (model.getRowCount() > 1) {
             int count = model.getRowCount() -1;
             int col = model.findColumn((String) columnsList.getSelectedItem());
             double[][] data = new double[1][count];
             for (int idx=0; idx < count; idx++) {
                 data[0][idx] = ((Number)model.getValueAt(idx,col)).doubleValue();
             }
             return data;
         }
         return new double[][]{ { 250, 45, 36, 66, 145, 80, 55  } };
     }
 
     public String[] getAxisLabels() {
         if (model.getRowCount() > 1) {
             int count = model.getRowCount() -1;
             String[] labels = new String[count];
             for (int idx=0; idx < count; idx++) {
                 labels[idx] = (String)model.getValueAt(idx,0);
             }
             return labels;
         }
         return new String[]{ "/", "/samples", "/jsp-samples", "/manager", "/manager/status", "/hello", "/world" };
     }
 
     /**
      * We use this method to get the data, since we are using
      * ObjectTableModel, so the calling getDataVector doesn't
      * work as expected.
      * @return the data from the model
      */
     public List<List<Object>> getAllTableData() {
         List<List<Object>> data = new ArrayList<List<Object>>();
         if (model.getRowCount() > 0) {
             for (int rw=0; rw < model.getRowCount(); rw++) {
                 int cols = model.getColumnCount();
                 List<Object> column = new ArrayList<Object>();
                 data.add(column);
                 for (int idx=0; idx < cols; idx++) {
                     Object val = model.getValueAt(rw,idx);
                     column.add(val);
                 }
             }
         }
         return data;
     }
 
     public void actionPerformed(ActionEvent event) {
         final Object eventSource = event.getSource();
         if (eventSource == displayButton) {
             makeGraph();
             tabbedGraph.setSelectedIndex(1);
         } else if (eventSource == saveGraph) {
             saveGraphToFile = true;
             try {
                 ActionRouter.getInstance().getAction(
                         ActionNames.SAVE_GRAPHICS,SaveGraphics.class.getName()).doAction(
                                 new ActionEvent(this,1,ActionNames.SAVE_GRAPHICS));
             } catch (Exception e) {
                 log.error(e.getMessage());
             }
         } else if (eventSource == saveTable) {
             JFileChooser chooser = FileDialoger.promptToSaveFile("statistics.csv");    //$NON-NLS-1$
             if (chooser == null) {
                 return;
             }
             FileWriter writer = null;
             try {
                 writer = new FileWriter(chooser.getSelectedFile()); // TODO Charset ?
                 CSVSaveService.saveCSVStats(getAllTableData(),writer,saveHeaders.isSelected() ? COLUMNS : null);
             } catch (FileNotFoundException e) {
                 log.warn(e.getMessage());
             } catch (IOException e) {
                 log.warn(e.getMessage());
             } finally {
                 JOrphanUtils.closeQuietly(writer);
             }
         } else if (eventSource == chooseBarColor) {
             colorBarGraph = JColorChooser.showDialog(
                     null,
                     JMeterUtils.getResString("aggregate_graph_choose_color"), //$NON-NLS-1$
                     colorBarGraph);
             currentColor.setBackground(colorBarGraph);
         } else if (eventSource == chooseForeColor) {
             colorForeGraph = JColorChooser.showDialog(
                     null,
                     JMeterUtils.getResString("aggregate_graph_choose_color"), //$NON-NLS-1$
                     colorBarGraph);
             currentColor.setForeground(colorForeGraph);
         } else if (eventSource == syncWithName) {
             graphTitle.setText(namePanel.getName());
         } else if (eventSource == dynamicGraphSize) {
             // if use dynamic graph size is checked, we disable the dimension fields
             if (dynamicGraphSize.isSelected()) {
                 graphWidth.setEnabled(false);
                 graphHeight.setEnabled(false);
             } else {
                 graphWidth.setEnabled(true);
                 graphHeight.setEnabled(true);
             }
         } else if (eventSource == columnSelection) {
             if (columnSelection.isSelected()) {
                 columnMatchLabel.setEnabled(true);
                 reloadButton.setEnabled(true);
                 caseChkBox.setEnabled(true);
                 regexpChkBox.setEnabled(true);
             } else {
                 columnMatchLabel.setEnabled(false);
                 reloadButton.setEnabled(false);
                 caseChkBox.setEnabled(false);
                 regexpChkBox.setEnabled(false);
             }
         } else if (eventSource == reloadButton) {
             if (getFile() != null && getFile().length() > 0) {
                 clearData();
                 FilePanel filePanel = (FilePanel) getFilePanel();
                 filePanel.actionPerformed(event);
             }
         }
     }
 
     @Override
     public JComponent getPrintableComponent() {
         if (saveGraphToFile == true) {
             saveGraphToFile = false;
             graphPanel.setBounds(graphPanel.getLocation().x,graphPanel.getLocation().y,
                     graphPanel.width,graphPanel.height);
             return graphPanel;
         }
         return this;
     }
 
     private JPanel createGraphActionsPane() {
         JPanel buttonPanel = new JPanel(new BorderLayout());
         JPanel displayPane = new JPanel();
         displayPane.add(displayButton);
         displayButton.addActionListener(this);
         buttonPanel.add(displayPane, BorderLayout.WEST);
 
         JPanel savePane = new JPanel();
         savePane.add(saveGraph);
         savePane.add(saveTable);
         savePane.add(saveHeaders);
         saveGraph.addActionListener(this);
         saveTable.addActionListener(this);
         syncWithName.addActionListener(this);
         buttonPanel.add(savePane, BorderLayout.EAST);
 
         return buttonPanel;
     }
 
     private JPanel createGraphColumnPane() {
         JPanel barPanel = new JPanel();
         barPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
 
         barPanel.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_column"), //$NON-NLS-1$
                 columnsList));
 
         currentColor.setBorder(new EmptyBorder(2, 5, 2, 5));
         currentColor.setOpaque(true);
         currentColor.setBackground(colorBarGraph);
 
         barPanel.add(Box.createRigidArea(new Dimension(5,0)));
         barPanel.add(currentColor);
         barPanel.add(Box.createRigidArea(new Dimension(5,0)));
         barPanel.add(chooseBarColor);
         chooseBarColor.addActionListener(this);
         barPanel.add(Box.createRigidArea(new Dimension(5,0)));
         barPanel.add(chooseForeColor);
         chooseForeColor.addActionListener(this);
 
         barPanel.add(drawOutlinesBar);
         barPanel.add(numberShowGrouping);
 
         JPanel columnPane = new JPanel(new BorderLayout());
         columnPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_column_settings"))); // $NON-NLS-1$
         columnPane.add(barPanel, BorderLayout.NORTH);
         columnPane.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.CENTER);
         columnPane.add(createGraphSelectionSubPane(), BorderLayout.SOUTH);
         
         return columnPane;
     }
     
     private JPanel createGraphSelectionSubPane() {
         Font font = new Font("SansSerif", Font.PLAIN, 10);
         // Search field
         JPanel searchPanel = new JPanel();
         searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
         searchPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
 
         searchPanel.add(columnSelection);
         columnMatchLabel.setEnabled(false);
         reloadButton.setEnabled(false);
         caseChkBox.setEnabled(false);
         regexpChkBox.setEnabled(false);
         columnSelection.addActionListener(this);
 
         searchPanel.add(columnMatchLabel);
         searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
 
         // Button
         reloadButton.setFont(font);
         reloadButton.addActionListener(this);
         searchPanel.add(reloadButton);
 
         // checkboxes
         caseChkBox.setFont(font);
         searchPanel.add(caseChkBox);
         regexpChkBox.setFont(font);
         searchPanel.add(regexpChkBox);
 
         return searchPanel;
     }
 
     private JPanel createGraphTitlePane() {
         JPanel titleNamePane = new JPanel(new BorderLayout());
         syncWithName.setFont(new Font("SansSerif", Font.PLAIN, 10));
         titleNamePane.add(graphTitle, BorderLayout.CENTER);
         titleNamePane.add(syncWithName, BorderLayout.EAST);
         
         JPanel titleStylePane = new JPanel();
         titleStylePane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
         titleStylePane.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_font"), //$NON-NLS-1$
                 titleFontNameList));
         titleFontNameList.setSelectedIndex(0); // default: sans serif
         titleStylePane.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_size"), //$NON-NLS-1$
                 titleFontSizeList));
         titleFontSizeList.setSelectedItem(StatGraphProperties.fontSize[6]); // default: 16
         titleStylePane.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_style"), //$NON-NLS-1$
                 titleFontStyleList));
         titleFontStyleList.setSelectedItem(JMeterUtils.getResString("fontstyle.bold")); // default: bold
 
         JPanel titlePane = new JPanel(new BorderLayout());
         titlePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_title_group"))); // $NON-NLS-1$
         titlePane.add(titleNamePane, BorderLayout.NORTH);
         titlePane.add(titleStylePane, BorderLayout.SOUTH);
         return titlePane;
     }
 
     private JPanel createGraphDimensionPane() {
         JPanel dimensionPane = new JPanel();
         dimensionPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         dimensionPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_dimension"))); // $NON-NLS-1$
 
         dimensionPane.add(dynamicGraphSize);
         dynamicGraphSize.setSelected(true); // default option
         graphWidth.setEnabled(false);
         graphHeight.setEnabled(false);
         dynamicGraphSize.addActionListener(this);
         dimensionPane.add(Box.createRigidArea(new Dimension(10,0)));
         dimensionPane.add(graphWidth);
         dimensionPane.add(Box.createRigidArea(new Dimension(5,0)));
         dimensionPane.add(graphHeight);
         return dimensionPane;
     }
 
     /**
      * Create pane for X Axis options
      * @return X Axis pane
      */
     private JPanel createGraphXAxisPane() {
         JPanel xAxisPane = new JPanel();
         xAxisPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         xAxisPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_xaxis_group"))); // $NON-NLS-1$
         xAxisPane.add(maxLengthXAxisLabel);
         return xAxisPane;
     }
 
     /**
      * Create pane for Y Axis options
      * @return Y Axis pane
      */
     private JPanel createGraphYAxisPane() {
         JPanel yAxisPane = new JPanel();
         yAxisPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         yAxisPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_yaxis_group"))); // $NON-NLS-1$
         yAxisPane.add(maxValueYAxisLabel);
         return yAxisPane;
     }
 
     /**
      * Create pane for legend settings
      * @return Legend pane
      */
     private JPanel createLegendPane() {
         JPanel legendPanel = new JPanel();
         legendPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         legendPanel.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_legend"))); // $NON-NLS-1$
 
         legendPanel.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_legend_placement"), //$NON-NLS-1$
                 legendPlacementList));
         legendPlacementList.setSelectedItem(JMeterUtils.getResString("aggregate_graph_legend.placement.right")); // default: right
         legendPanel.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_font"), //$NON-NLS-1$
                 fontNameList));
         fontNameList.setSelectedIndex(0); // default: sans serif
         legendPanel.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_size"), //$NON-NLS-1$
                 fontSizeList));
         fontSizeList.setSelectedItem(StatGraphProperties.fontSize[2]); // default: 10
         legendPanel.add(createLabelCombo(JMeterUtils.getResString("aggregate_graph_style"), //$NON-NLS-1$
                 fontStyleList));
         fontStyleList.setSelectedItem(JMeterUtils.getResString("fontstyle.normal")); // default: normal
 
         return legendPanel;
     }
 
     private JComponent createLabelCombo(String label, JComboBox comboBox) {
         JPanel labelCombo = new JPanel();
         labelCombo.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         JLabel caption = new JLabel(label);//$NON-NLS-1$
         caption.setBorder(MARGIN);
         labelCombo.add(caption);
         labelCombo.add(comboBox);
         return labelCombo;
     }
 
     /**
      * @param textToFind
      * @return pattern ready to search
      */
     private Pattern createPattern(String textToFind) {
         String textToFindQ = Pattern.quote(textToFind);
         if (regexpChkBox.isSelected()) {
             textToFindQ = textToFind;
         }
         Pattern pattern = null;
         try {
             if (caseChkBox.isSelected()) {
                 pattern = Pattern.compile(textToFindQ);
             } else {
                 pattern = Pattern.compile(textToFindQ, Pattern.CASE_INSENSITIVE);
             }
         } catch (PatternSyntaxException pse) {
             return null;
         }
         return pattern;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/StatVisualizer.java b/src/components/org/apache/jmeter/visualizers/StatVisualizer.java
index b0a7a45f3..8c5dbda1c 100644
--- a/src/components/org/apache/jmeter/visualizers/StatVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/StatVisualizer.java
@@ -1,358 +1,362 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.FileNotFoundException;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JFileChooser;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
 //import javax.swing.table.AbstractTableModel;
 //import javax.swing.table.TableModel;
 
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.NumberRenderer;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RateRenderer;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Aggregrate Table-Based Reporting Visualizer for JMeter. Props to the people
  * who've done the other visualizers ahead of me (Stefano Mazzocchi), who I
  * borrowed code from to start me off (and much code may still exist). Thank
  * you!
  *
  */
 public class StatVisualizer extends AbstractVisualizer implements Clearable, ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String USE_GROUP_NAME = "useGroupName"; //$NON-NLS-1$
 
     private static final String SAVE_HEADERS   = "saveHeaders"; //$NON-NLS-1$
 
     private static final String[] COLUMNS = {
             "sampler_label",                 //$NON-NLS-1$
             "aggregate_report_count",        //$NON-NLS-1$
             "average",                       //$NON-NLS-1$
             "aggregate_report_median",       //$NON-NLS-1$
             "aggregate_report_90%_line",     //$NON-NLS-1$
             "aggregate_report_min",          //$NON-NLS-1$
             "aggregate_report_max",          //$NON-NLS-1$
             "aggregate_report_error%",       //$NON-NLS-1$
             "aggregate_report_rate",         //$NON-NLS-1$
             "aggregate_report_bandwidth" };  //$NON-NLS-1$
 
     private final String TOTAL_ROW_LABEL
         = JMeterUtils.getResString("aggregate_report_total_label");  //$NON-NLS-1$
 
     private JTable myJTable;
 
     private JScrollPane myScrollPane;
 
     private final JButton saveTable =
         new JButton(JMeterUtils.getResString("aggregate_graph_save_table"));            //$NON-NLS-1$
 
     private final JCheckBox saveHeaders = // should header be saved with the data?
         new JCheckBox(JMeterUtils.getResString("aggregate_graph_save_table_header"),true);    //$NON-NLS-1$
 
     private final JCheckBox useGroupName =
         new JCheckBox(JMeterUtils.getResString("aggregate_graph_use_group_name"));            //$NON-NLS-1$
 
     private transient ObjectTableModel model;
 
     /**
      * Lock used to protect tableRows update + model update
      */
     private final transient Object lock = new Object();
 
     private final Map<String, SamplingStatCalculator> tableRows =
         new ConcurrentHashMap<String, SamplingStatCalculator>();
 
     public StatVisualizer() {
         super();
         model = new ObjectTableModel(COLUMNS,
                 SamplingStatCalculator.class,
                 new Functor[] {
                     new Functor("getLabel"),   //$NON-NLS-1$
                     new Functor("getCount"),  //$NON-NLS-1$
                     new Functor("getMeanAsNumber"),   //$NON-NLS-1$
                     new Functor("getMedian"),  //$NON-NLS-1$
                     new Functor("getPercentPoint",  //$NON-NLS-1$
                             new Object[] { new Float(.900) }),
                     new Functor("getMin"),  //$NON-NLS-1$
                     new Functor("getMax"),   //$NON-NLS-1$
                     new Functor("getErrorPercentage"),   //$NON-NLS-1$
                     new Functor("getRate"),  //$NON-NLS-1$
                     new Functor("getKBPerSecond")   //$NON-NLS-1$
                 },
                 new Functor[] { null, null, null, null, null, null, null, null, null, null },
                 new Class[] { String.class, Long.class, Long.class, Long.class, Long.class,
                               Long.class, Long.class, String.class, String.class, String.class });
         clearData();
         init();
     }
 
     // Column renderers
     private static final TableCellRenderer[] RENDERERS =
         new TableCellRenderer[]{
             null, // Label
             null, // count
             null, // Mean
             null, // median
             null, // 90%
             null, // Min
             null, // Max
             new NumberRenderer("#0.00%"), // Error %age //$NON-NLS-1$
             new RateRenderer("#.0"),      // Throughput //$NON-NLS-1$
             new NumberRenderer("#.0"),    // pageSize   //$NON-NLS-1$
         };
 
     /** @deprecated - only for use in testing */
     @Deprecated
     public static boolean testFunctors(){
         StatVisualizer instance = new StatVisualizer();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
     public String getLabelResource() {
         return "aggregate_report";  //$NON-NLS-1$
     }
 
-    public void add(SampleResult res) {
-        SamplingStatCalculator row = null;
-        final String sampleLabel = res.getSampleLabel(useGroupName.isSelected());
-        synchronized (lock) {
-            row = tableRows.get(sampleLabel);
-            if (row == null) {
-                row = new SamplingStatCalculator(sampleLabel);
-                tableRows.put(row.getLabel(), row);
-                model.insertRow(row, model.getRowCount() - 1);
+    public void add(final SampleResult res) {
+        JMeterUtils.runSafe(new Runnable() {
+            public void run() {
+                SamplingStatCalculator row = null;
+                final String sampleLabel = res.getSampleLabel(useGroupName.isSelected());
+                synchronized (lock) {
+                    row = tableRows.get(sampleLabel);
+                    if (row == null) {
+                        row = new SamplingStatCalculator(sampleLabel);
+                        tableRows.put(row.getLabel(), row);
+                        model.insertRow(row, model.getRowCount() - 1);
+                    }
+                }
+                /*
+                 * Synch is needed because multiple threads can update the counts.
+                 */
+                synchronized(row) {
+                    row.addSample(res);
+                }
+                SamplingStatCalculator tot = tableRows.get(TOTAL_ROW_LABEL);
+                synchronized(tot) {
+                    tot.addSample(res);
+                }
+                model.fireTableDataChanged();                
             }
-        }
-        /*
-         * Synch is needed because multiple threads can update the counts.
-         */
-        synchronized(row) {
-            row.addSample(res);
-        }
-        SamplingStatCalculator tot = tableRows.get(TOTAL_ROW_LABEL);
-        synchronized(tot) {
-            tot.addSample(res);
-        }
-        model.fireTableDataChanged();
+        });
     }
 
     /**
      * Clears this visualizer and its model, and forces a repaint of the table.
      */
     public void clearData() {
         synchronized (lock) {
             model.clearData();
             tableRows.clear();
             tableRows.put(TOTAL_ROW_LABEL, new SamplingStatCalculator(TOTAL_ROW_LABEL));
             model.addRow(tableRows.get(TOTAL_ROW_LABEL));
         }
     }
 
     /**
      * Main visualizer setup.
      */
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
 
         mainPanel.add(makeTitlePanel());
 
         // SortFilterModel mySortedModel =
         // new SortFilterModel(myStatTableModel);
         myJTable = new JTable(model);
         myJTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         myJTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
         RendererUtils.applyRenderers(myJTable, RENDERERS);
         myScrollPane = new JScrollPane(myJTable);
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(myScrollPane, BorderLayout.CENTER);
         saveTable.addActionListener(this);
         JPanel opts = new JPanel();
         opts.add(useGroupName, BorderLayout.WEST);
         opts.add(saveTable, BorderLayout.CENTER);
         opts.add(saveHeaders, BorderLayout.EAST);
         this.add(opts,BorderLayout.SOUTH);
     }
 
     @Override
     public void modifyTestElement(TestElement c) {
         super.modifyTestElement(c);
         c.setProperty(USE_GROUP_NAME, useGroupName.isSelected(), false);
         c.setProperty(SAVE_HEADERS, saveHeaders.isSelected(), true);
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         useGroupName.setSelected(el.getPropertyAsBoolean(USE_GROUP_NAME, false));
         saveHeaders.setSelected(el.getPropertyAsBoolean(SAVE_HEADERS, true));
     }
 
     public void actionPerformed(ActionEvent ev) {
         if (ev.getSource() == saveTable) {
             JFileChooser chooser = FileDialoger.promptToSaveFile("aggregate.csv");//$NON-NLS-1$
             if (chooser == null) {
                 return;
             }
             FileWriter writer = null;
             try {
                 writer = new FileWriter(chooser.getSelectedFile()); // TODO Charset ?
                 CSVSaveService.saveCSVStats(model,writer, saveHeaders.isSelected());
             } catch (FileNotFoundException e) {
                 log.warn(e.getMessage());
             } catch (IOException e) {
                 log.warn(e.getMessage());
             } finally {
                 JOrphanUtils.closeQuietly(writer);
             }
         }
     }
 }
 
 /**
  * Pulled this mainly out of a Core Java book to implement a sorted table -
  * haven't implemented this yet, it needs some non-trivial work done to it to
  * support our dynamically-sizing TableModel for this visualizer.
  *
  */
 
 //class SortFilterModel extends AbstractTableModel {
 //  private TableModel model;
 //
 //  private int sortColumn;
 //
 //  private Row[] rows;
 //
 //  public SortFilterModel(TableModel m) {
 //      model = m;
 //      rows = new Row[model.getRowCount()];
 //      for (int i = 0; i < rows.length; i++) {
 //          rows[i] = new Row();
 //          rows[i].index = i;
 //      }
 //  }
 //
 //  public SortFilterModel() {
 //  }
 //
 //  public void setValueAt(Object aValue, int r, int c) {
 //        model.setValueAt(aValue, rows[r].index, c);
 //    }
 //
 //    public Object getValueAt(int r, int c) {
 //        return model.getValueAt(rows[r].index, c);
 //    }
 //
 //    public boolean isCellEditable(int r, int c) {
 //        return model.isCellEditable(rows[r].index, c);
 //    }
 //
 //    public int getRowCount() {
 //        return model.getRowCount();
 //    }
 //
 //    public int getColumnCount() {
 //        return model.getColumnCount();
 //    }
 //
 //    public String getColumnName(int c) {
 //        return model.getColumnName(c);
 //    }
 //
 //    public Class getColumnClass(int c) {
 //        return model.getColumnClass(c);
 //    }
 //
 //    public void sort(int c) {
 //        sortColumn = c;
 //        Arrays.sort(rows);
 //        fireTableDataChanged();
 //    }
 //
 //    public void addMouseListener(final JTable table) {
 //        table.getTableHeader().addMouseListener(new MouseAdapter() {
 //            public void mouseClicked(MouseEvent event) {
 //                if (event.getClickCount() < 2) {
 //                    return;
 //                }
 //                int tableColumn = table.columnAtPoint(event.getPoint());
 //                int modelColumn = table.convertColumnIndexToModel(tableColumn);
 //
 //                sort(modelColumn);
 //            }
 //        });
 //    }
 //
 //    private class Row implements Comparable {
 //        public int index;
 //
 //        public int compareTo(Object other) {
 //            Row otherRow = (Row) other;
 //            Object a = model.getValueAt(index, sortColumn);
 //            Object b = model.getValueAt(otherRow.index, sortColumn);
 //
 //            if (a instanceof Comparable) {
 //                return ((Comparable) a).compareTo(b);
 //            } else {
 //                return index - otherRow.index;
 //            }
 //        }
 //    }
 //} // class SortFilterModel
diff --git a/src/components/org/apache/jmeter/visualizers/SummaryReport.java b/src/components/org/apache/jmeter/visualizers/SummaryReport.java
index c4c1e8b3f..c0cd805f8 100644
--- a/src/components/org/apache/jmeter/visualizers/SummaryReport.java
+++ b/src/components/org/apache/jmeter/visualizers/SummaryReport.java
@@ -1,262 +1,266 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.FileNotFoundException;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JFileChooser;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
 
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.Calculator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.NumberRenderer;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RateRenderer;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Simpler (lower memory) version of Aggregate Report (StatVisualizer).
  * Excludes the Median and 90% columns, which are expensive in memory terms
  */
 public class SummaryReport extends AbstractVisualizer implements Clearable, ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String USE_GROUP_NAME = "useGroupName"; //$NON-NLS-1$
 
     private static final String SAVE_HEADERS   = "saveHeaders"; //$NON-NLS-1$
 
     private static final String[] COLUMNS = {
             "sampler_label",               //$NON-NLS-1$
             "aggregate_report_count",      //$NON-NLS-1$
             "average",                     //$NON-NLS-1$
             "aggregate_report_min",        //$NON-NLS-1$
             "aggregate_report_max",        //$NON-NLS-1$
             "aggregate_report_stddev",     //$NON-NLS-1$
             "aggregate_report_error%",     //$NON-NLS-1$
             "aggregate_report_rate",       //$NON-NLS-1$
             "aggregate_report_bandwidth",  //$NON-NLS-1$
             "average_bytes",               //$NON-NLS-1$
             };
 
     private final String TOTAL_ROW_LABEL
         = JMeterUtils.getResString("aggregate_report_total_label");  //$NON-NLS-1$
 
     private JTable myJTable;
 
     private JScrollPane myScrollPane;
 
     private final JButton saveTable =
         new JButton(JMeterUtils.getResString("aggregate_graph_save_table"));            //$NON-NLS-1$
 
     private final JCheckBox saveHeaders = // should header be saved with the data?
         new JCheckBox(JMeterUtils.getResString("aggregate_graph_save_table_header"),true);    //$NON-NLS-1$
 
     private final JCheckBox useGroupName =
         new JCheckBox(JMeterUtils.getResString("aggregate_graph_use_group_name"));            //$NON-NLS-1$
 
     private transient ObjectTableModel model;
 
     /**
      * Lock used to protect tableRows update + model update
      */
     private final transient Object lock = new Object();
 
     private final Map<String, Calculator> tableRows =
         new ConcurrentHashMap<String, Calculator>();
 
     // Column renderers
     private static final TableCellRenderer[] RENDERERS =
         new TableCellRenderer[]{
             null, // Label
             null, // count
             null, // Mean
             null, // Min
             null, // Max
             new NumberRenderer("#0.00"), // Std Dev.
             new NumberRenderer("#0.00%"), // Error %age
             new RateRenderer("#.0"),      // Throughpur
             new NumberRenderer("#0.00"),  // kB/sec
             new NumberRenderer("#.0"),    // avg. pageSize
         };
 
     public SummaryReport() {
         super();
         model = new ObjectTableModel(COLUMNS,
                 Calculator.class,// All rows have this class
                 new Functor[] {
                     new Functor("getLabel"),              //$NON-NLS-1$
                     new Functor("getCount"),              //$NON-NLS-1$
                     new Functor("getMeanAsNumber"),       //$NON-NLS-1$
                     new Functor("getMin"),                //$NON-NLS-1$
                     new Functor("getMax"),                //$NON-NLS-1$
                     new Functor("getStandardDeviation"),  //$NON-NLS-1$
                     new Functor("getErrorPercentage"),    //$NON-NLS-1$
                     new Functor("getRate"),               //$NON-NLS-1$
                     new Functor("getKBPerSecond"),        //$NON-NLS-1$
                     new Functor("getAvgPageBytes"),       //$NON-NLS-1$
                 },
                 new Functor[] { null, null, null, null, null, null, null, null , null, null },
                 new Class[] { String.class, Long.class, Long.class, Long.class, Long.class,
                               String.class, String.class, String.class, String.class, String.class });
         clearData();
         init();
     }
 
     /** @deprecated - only for use in testing */
     @Deprecated
     public static boolean testFunctors(){
         SummaryReport instance = new SummaryReport();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
     public String getLabelResource() {
         return "summary_report";  //$NON-NLS-1$
     }
 
-    public void add(SampleResult res) {
-        Calculator row = null;
+    public void add(final SampleResult res) {
         final String sampleLabel = res.getSampleLabel(useGroupName.isSelected());
-        synchronized (lock) {
-            row = tableRows.get(sampleLabel);
-            if (row == null) {
-                row = new Calculator(sampleLabel);
-                tableRows.put(row.getLabel(), row);
-                model.insertRow(row, model.getRowCount() - 1);
+        JMeterUtils.runSafe(new Runnable() {
+            public void run() {
+                Calculator row = null;
+                synchronized (lock) {
+                    row = tableRows.get(sampleLabel);
+                    if (row == null) {
+                        row = new Calculator(sampleLabel);
+                        tableRows.put(row.getLabel(), row);
+                        model.insertRow(row, model.getRowCount() - 1);
+                    }
+                }
+                /*
+                 * Synch is needed because multiple threads can update the counts.
+                 */
+                synchronized(row) {
+                    row.addSample(res);
+                }
+                Calculator tot = tableRows.get(TOTAL_ROW_LABEL);
+                synchronized(tot) {
+                    tot.addSample(res);
+                }
+                model.fireTableDataChanged();                
             }
-        }
-        /*
-         * Synch is needed because multiple threads can update the counts.
-         */
-        synchronized(row) {
-            row.addSample(res);
-        }
-        Calculator tot = tableRows.get(TOTAL_ROW_LABEL);
-        synchronized(tot) {
-            tot.addSample(res);
-        }
-        model.fireTableDataChanged();
+        });
     }
 
     /**
      * Clears this visualizer and its model, and forces a repaint of the table.
      */
     public void clearData() {
         //Synch is needed because a clear can occur while add occurs
         synchronized (lock) {
             model.clearData();
             tableRows.clear();
             tableRows.put(TOTAL_ROW_LABEL, new Calculator(TOTAL_ROW_LABEL));
             model.addRow(tableRows.get(TOTAL_ROW_LABEL));
         }
     }
 
     /**
      * Main visualizer setup.
      */
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
 
         mainPanel.add(makeTitlePanel());
 
         myJTable = new JTable(model);
         myJTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         myJTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
         RendererUtils.applyRenderers(myJTable, RENDERERS);
         myScrollPane = new JScrollPane(myJTable);
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(myScrollPane, BorderLayout.CENTER);
         saveTable.addActionListener(this);
         JPanel opts = new JPanel();
         opts.add(useGroupName, BorderLayout.WEST);
         opts.add(saveTable, BorderLayout.CENTER);
         opts.add(saveHeaders, BorderLayout.EAST);
         this.add(opts,BorderLayout.SOUTH);
     }
 
     @Override
     public void modifyTestElement(TestElement c) {
         super.modifyTestElement(c);
         c.setProperty(USE_GROUP_NAME, useGroupName.isSelected(), false);
         c.setProperty(SAVE_HEADERS, saveHeaders.isSelected(), true);
     }
 
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         useGroupName.setSelected(el.getPropertyAsBoolean(USE_GROUP_NAME, false));
         saveHeaders.setSelected(el.getPropertyAsBoolean(SAVE_HEADERS, true));
     }
 
     public void actionPerformed(ActionEvent ev) {
         if (ev.getSource() == saveTable) {
             JFileChooser chooser = FileDialoger.promptToSaveFile("summary.csv");//$NON-NLS-1$
             if (chooser == null) {
                 return;
             }
             FileWriter writer = null;
             try {
                 writer = new FileWriter(chooser.getSelectedFile());
                 CSVSaveService.saveCSVStats(model,writer, saveHeaders.isSelected());
             } catch (FileNotFoundException e) {
                 log.warn(e.getMessage());
             } catch (IOException e) {
                 log.warn(e.getMessage());
             } finally {
                 JOrphanUtils.closeQuietly(writer);
             }
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/TableVisualizer.java b/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
index 4a66c8408..f81238151 100644
--- a/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
@@ -1,325 +1,329 @@
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
 import java.awt.FlowLayout;
 import java.text.Format;
 import java.text.SimpleDateFormat;
 
 import javax.swing.BorderFactory;
 import javax.swing.ImageIcon;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.JTextField;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
 
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.Calculator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.gui.RightAlignRenderer;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 import org.apache.jorphan.reflect.Functor;
 
 /**
  * This class implements a statistical analyser that calculates both the average
  * and the standard deviation of the sampling process. The samples are displayed
  * in a JTable, and the statistics are displayed at the bottom of the table.
  *
  * created March 10, 2002
  *
  */
 public class TableVisualizer extends AbstractVisualizer implements Clearable {
 
     private static final long serialVersionUID = 240L;
 
     // Note: the resource string won't respond to locale-changes,
     // however this does not matter as it is only used when pasting to the clipboard
     private static final ImageIcon imageSuccess = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                                        "icon_success_sml.gif"),    //$NON-NLS-1$
             JMeterUtils.getResString("table_visualizer_success")); //$NON-NLS-1$
 
     private static final ImageIcon imageFailure = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                                        "icon_warning_sml.gif"),    //$NON-NLS-1$
             JMeterUtils.getResString("table_visualizer_warning")); //$NON-NLS-1$
 
     private static final String[] COLUMNS = new String[] {
             "table_visualizer_sample_num",  // $NON-NLS-1$
             "table_visualizer_start_time",  // $NON-NLS-1$
             "table_visualizer_thread_name", // $NON-NLS-1$
             "sampler_label",                // $NON-NLS-1$
             "table_visualizer_sample_time", // $NON-NLS-1$
             "table_visualizer_status",      // $NON-NLS-1$
             "table_visualizer_bytes" };     // $NON-NLS-1$
 
     private ObjectTableModel model = null;
 
     private JTable table = null;
 
     private JTextField dataField = null;
 
     private JTextField averageField = null;
 
     private JTextField deviationField = null;
 
     private JTextField noSamplesField = null;
 
     private JScrollPane tableScrollPanel = null;
 
     private JCheckBox autoscroll = null;
 
     private JCheckBox childSamples = null;
 
     private transient Calculator calc = new Calculator();
 
     private Format format = new SimpleDateFormat("HH:mm:ss.SSS"); //$NON-NLS-1$
 
     // Column renderers
     private static final TableCellRenderer[] RENDERERS =
         new TableCellRenderer[]{
             new RightAlignRenderer(), // Sample number (string)
             new RightAlignRenderer(), // Start Time
             null, // Thread Name
             null, // Label
             null, // Sample Time
             null, // Status
             null, // Bytes
         };
 
     /**
      * Constructor for the TableVisualizer object.
      */
     public TableVisualizer() {
         super();
         model = new ObjectTableModel(COLUMNS,
                 TableSample.class,         // The object used for each row
                 new Functor[] {
                 new Functor("getSampleNumberString"), // $NON-NLS-1$
                 new Functor("getStartTimeFormatted",  // $NON-NLS-1$
                         new Object[]{format}),
                 new Functor("getThreadName"), // $NON-NLS-1$
                 new Functor("getLabel"), // $NON-NLS-1$
                 new Functor("getElapsed"), // $NON-NLS-1$
                 new SampleSuccessFunctor("isSuccess"), // $NON-NLS-1$
                 new Functor("getBytes") }, // $NON-NLS-1$
                 new Functor[] { null, null, null, null, null, null, null },
                 new Class[] {
                 String.class, String.class, String.class, String.class, Long.class, ImageIcon.class, Integer.class });
         init();
     }
 
     public static boolean testFunctors(){
         TableVisualizer instance = new TableVisualizer();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
 
     public String getLabelResource() {
         return "view_results_in_table"; // $NON-NLS-1$
     }
 
     protected synchronized void updateTextFields(SampleResult res) {
         noSamplesField.setText(Long.toString(calc.getCount()));
         dataField.setText(Long.toString(res.getTime()/res.getSampleCount()));
         averageField.setText(Long.toString((long) calc.getMean()));
         deviationField.setText(Long.toString((long) calc.getStandardDeviation()));
     }
 
-    public void add(SampleResult res) {
-        if (childSamples.isSelected()) {
-            SampleResult[] subResults = res.getSubResults();
-            if (subResults.length > 0) {
-                for (SampleResult sr : subResults) {
-                    add(sr);
+    public void add(final SampleResult res) {
+        JMeterUtils.runSafe(new Runnable() {
+            public void run() {
+                if (childSamples.isSelected()) {
+                    SampleResult[] subResults = res.getSubResults();
+                    if (subResults.length > 0) {
+                        for (SampleResult sr : subResults) {
+                            add(sr);
+                        }
+                        return;
+                    }
+                }
+                synchronized (calc) {
+                    calc.addSample(res);
+                    int count = calc.getCount();
+                    TableSample newS = new TableSample(
+                            count, 
+                            res.getSampleCount(), 
+                            res.getStartTime(), 
+                            res.getThreadName(), 
+                            res.getSampleLabel(),
+                            res.getTime(),
+                            res.isSuccessful(),
+                            res.getBytes());
+                    model.addRow(newS);
+                }
+                updateTextFields(res);
+                if (autoscroll.isSelected()) {
+                    table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
                 }
-                return;
             }
-        }
-        synchronized (calc) {
-            calc.addSample(res);
-            int count = calc.getCount();
-            TableSample newS = new TableSample(
-                    count, 
-                    res.getSampleCount(), 
-                    res.getStartTime(), 
-                    res.getThreadName(), 
-                    res.getSampleLabel(),
-                    res.getTime(),
-                    res.isSuccessful(),
-                    res.getBytes());
-            model.addRow(newS);
-        }
-        updateTextFields(res);
-        if (autoscroll.isSelected()) {
-            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
-        }
+        });
     }
 
     public synchronized void clearData() {
         model.clearData();
         calc.clear();
         noSamplesField.setText("0"); // $NON-NLS-1$
         dataField.setText("0"); // $NON-NLS-1$
         averageField.setText("0"); // $NON-NLS-1$
         deviationField.setText("0"); // $NON-NLS-1$
         repaint();
     }
 
     @Override
     public String toString() {
         return "Show the samples in a table";
     }
 
     private void init() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
 
         // NAME
         mainPanel.add(makeTitlePanel());
 
         // Set up the table itself
         table = new JTable(model);
         table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         // table.getTableHeader().setReorderingAllowed(false);
         RendererUtils.applyRenderers(table, RENDERERS);
 
         tableScrollPanel = new JScrollPane(table);
         tableScrollPanel.setViewportBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
 
         autoscroll = new JCheckBox(JMeterUtils.getResString("view_results_autoscroll")); //$NON-NLS-1$
 
         childSamples = new JCheckBox(JMeterUtils.getResString("view_results_childsamples")); //$NON-NLS-1$
 
         // Set up footer of table which displays numerics of the graphs
         JPanel dataPanel = new JPanel();
         JLabel dataLabel = new JLabel(JMeterUtils.getResString("graph_results_latest_sample")); // $NON-NLS-1$
         dataLabel.setForeground(Color.black);
         dataField = new JTextField(5);
         dataField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         dataField.setEditable(false);
         dataField.setForeground(Color.black);
         dataField.setBackground(getBackground());
         dataPanel.add(dataLabel);
         dataPanel.add(dataField);
 
         JPanel averagePanel = new JPanel();
         JLabel averageLabel = new JLabel(JMeterUtils.getResString("graph_results_average")); // $NON-NLS-1$
         averageLabel.setForeground(Color.blue);
         averageField = new JTextField(5);
         averageField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         averageField.setEditable(false);
         averageField.setForeground(Color.blue);
         averageField.setBackground(getBackground());
         averagePanel.add(averageLabel);
         averagePanel.add(averageField);
 
         JPanel deviationPanel = new JPanel();
         JLabel deviationLabel = new JLabel(JMeterUtils.getResString("graph_results_deviation")); // $NON-NLS-1$
         deviationLabel.setForeground(Color.red);
         deviationField = new JTextField(5);
         deviationField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         deviationField.setEditable(false);
         deviationField.setForeground(Color.red);
         deviationField.setBackground(getBackground());
         deviationPanel.add(deviationLabel);
         deviationPanel.add(deviationField);
 
         JPanel noSamplesPanel = new JPanel();
         JLabel noSamplesLabel = new JLabel(JMeterUtils.getResString("graph_results_no_samples")); // $NON-NLS-1$
 
         noSamplesField = new JTextField(8);
         noSamplesField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         noSamplesField.setEditable(false);
         noSamplesField.setForeground(Color.black);
         noSamplesField.setBackground(getBackground());
         noSamplesPanel.add(noSamplesLabel);
         noSamplesPanel.add(noSamplesField);
 
         JPanel tableInfoPanel = new JPanel();
         tableInfoPanel.setLayout(new FlowLayout());
         tableInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
         tableInfoPanel.add(noSamplesPanel);
         tableInfoPanel.add(dataPanel);
         tableInfoPanel.add(averagePanel);
         tableInfoPanel.add(deviationPanel);
 
         JPanel tableControlsPanel = new JPanel(new BorderLayout());
         tableControlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
         JPanel jp = new HorizontalPanel();
         jp.add(autoscroll);
         jp.add(childSamples);
         tableControlsPanel.add(jp, BorderLayout.WEST);
         tableControlsPanel.add(tableInfoPanel, BorderLayout.CENTER);
 
         // Set up the table with footer
         JPanel tablePanel = new JPanel();
 
         tablePanel.setLayout(new BorderLayout());
         tablePanel.add(tableScrollPanel, BorderLayout.CENTER);
         tablePanel.add(tableControlsPanel, BorderLayout.SOUTH);
 
         // Add the main panel and the graph
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(tablePanel, BorderLayout.CENTER);
     }
 
     public static class SampleSuccessFunctor extends Functor {
         public SampleSuccessFunctor(String methodName) {
             super(methodName);
         }
 
         @Override
         public Object invoke(Object p_invokee) {
             Boolean success = (Boolean)super.invoke(p_invokee);
 
             if(success != null) {
                 if(success.booleanValue()) {
                     return imageSuccess;
                 }
                 else {
                     return imageFailure;
                 }
             }
             else {
                 return null;
             }
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index 156c2b123..c9aef6be1 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,404 +1,408 @@
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
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  *
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 /**
  *
  */
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.ItemEvent;
 import java.awt.event.ItemListener;
 import java.io.IOException;
 import java.util.Collections;
 import java.util.List;
 
 import javax.swing.ComboBoxModel;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.ImageIcon;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTree;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.DefaultTreeModel;
 import javax.swing.tree.TreePath;
 import javax.swing.tree.TreeSelectionModel;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Base for ViewResults
  *
  */
 public class ViewResultsFullVisualizer extends AbstractVisualizer
 implements ActionListener, TreeSelectionListener, Clearable, ItemListener {
 
     private static final long serialVersionUID = 7338676747296593842L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final Color SERVER_ERROR_COLOR = Color.red;
 
     public static final Color CLIENT_ERROR_COLOR = Color.blue;
 
     public static final Color REDIRECT_COLOR = Color.green;
 
     private  JSplitPane mainSplit;
 
     private DefaultMutableTreeNode root;
 
     private DefaultTreeModel treeModel;
 
     private JTree jTree;
 
     private Component leftSide;
 
     private JTabbedPane rightSide;
 
     private JComboBox selectRenderPanel;
 
     private int selectedTab;
 
     protected static final String COMBO_CHANGE_COMMAND = "change_combo"; // $NON-NLS-1$
 
     private static final ImageIcon imageSuccess = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                     "icon_success_sml.gif")); //$NON-NLS-1$
 
     private static final ImageIcon imageFailure = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                     "icon_warning_sml.gif")); //$NON-NLS-1$
 
     // Maximum size that we will display
     private static final int MAX_DISPLAY_SIZE =
         JMeterUtils.getPropDefault("view.results.tree.max_size", 200 * 1024); // $NON-NLS-1$
 
     private ResultRenderer resultsRender = null;
 
     private TreeSelectionEvent lastSelectionEvent;
 
     private JCheckBox autoScrollCB;
 
     /**
      * Constructor
      */
     public ViewResultsFullVisualizer() {
         super();
         init();
     }
 
     /** {@inheritDoc} */
-    public void add(SampleResult sample) {
-        updateGui(sample);
+    public void add(final SampleResult sample) {
+        JMeterUtils.runSafe(new Runnable() {
+            public void run() {
+                updateGui(sample);
+            }
+        });
     }
 
     /**
      * Update the visualizer with new data.
      */
     private synchronized void updateGui(SampleResult res) {
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
         if (autoScrollCB.isSelected() && root.getChildCount() > 1) {
             jTree.scrollPathToVisible(new TreePath(new Object[] { root,
                     treeModel.getChild(root, root.getChildCount() - 1) }));
         }
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
 
     /** {@inheritDoc} */
     public synchronized void clearData() {
         while (root.getChildCount() > 0) {
             // the child to be removed will always be 0 'cos as the nodes are
             // removed the nth node will become (n-1)th
             treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
         }
         resultsRender.clearData();
     }
 
     /** {@inheritDoc} */
     public String getLabelResource() {
         return "view_results_tree_title"; // $NON-NLS-1$
     }
 
     /**
      * Initialize this visualizer
      */
     protected void init() {
         log.debug("init() - pass");
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         leftSide = createLeftPanel();
         // Prepare the common tab
         rightSide = new JTabbedPane();
 
         // Create the split pane
         mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
         add(mainSplit, BorderLayout.CENTER);
         // init right side with first render
         resultsRender.setRightSide(rightSide);
         resultsRender.init();
     }
 
     /** {@inheritDoc} */
     public void valueChanged(TreeSelectionEvent e) {
         lastSelectionEvent = e;
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
         if (node != null) {
             // to restore last tab used
             if (rightSide.getTabCount() > selectedTab) {
                 resultsRender.setLastSelectedTab(rightSide.getSelectedIndex());
             }
             Object userObject = node.getUserObject();
             resultsRender.setSamplerResult(userObject);
             resultsRender.setupTabPane(); // Processes Assertions
             // display a SampleResult
             if (userObject instanceof SampleResult) {
                 SampleResult sampleResult = (SampleResult) userObject;
                 if ((SampleResult.TEXT).equals(sampleResult.getDataType())){
                     resultsRender.renderResult(sampleResult);
                 } else {
                     byte[] responseBytes = sampleResult.getResponseData();
                     if (responseBytes != null) {
                         resultsRender.renderImage(sampleResult);
                     }
                 }
             }
         }
     }
 
     private synchronized Component createLeftPanel() {
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
 
         VerticalPanel leftPane = new VerticalPanel();
         leftPane.add(treePane, BorderLayout.CENTER);
         VerticalPanel leftDownPane = new VerticalPanel();
         leftDownPane.add(createComboRender(), BorderLayout.NORTH);
         autoScrollCB = new JCheckBox(JMeterUtils.getResString("view_results_autoscroll"));
         autoScrollCB.setSelected(false);
         autoScrollCB.addItemListener(this);
         leftDownPane.add(autoScrollCB, BorderLayout.SOUTH);
         leftPane.add(leftDownPane, BorderLayout.SOUTH);
         return leftPane;
     }
 
     /**
      * Create the drop-down list to changer render
      * @return List of all render (implement ResultsRender)
      */
     private Component createComboRender() {
         ComboBoxModel nodesModel = new DefaultComboBoxModel();
         // drop-down list for renderer
         selectRenderPanel = new JComboBox(nodesModel);
         selectRenderPanel.setActionCommand(COMBO_CHANGE_COMMAND);
         selectRenderPanel.addActionListener(this);
 
         // if no results render in jmeter.properties, load Standard (default)
         List<String> classesToAdd = Collections.<String>emptyList();
         try {
             classesToAdd = JMeterUtils.findClassesThatExtend(ResultRenderer.class);
         } catch (IOException e1) {
             // ignored
         }
         String textRenderer = JMeterUtils.getResString("view_results_render_text"); // $NON-NLS-1$
         Object textObject = null;
         for (String clazz : classesToAdd) {
             try {
                 // Instantiate render classes
                 final ResultRenderer renderer = (ResultRenderer) Class.forName(clazz).newInstance();
                 if (textRenderer.equals(renderer.toString())){
                     textObject=renderer;
                 }
                 renderer.setBackgroundColor(getBackground());
                 selectRenderPanel.addItem(renderer);
             } catch (Exception e) {
                 log.warn("Error in load result render:" + clazz, e);
             }
         }
         nodesModel.setSelectedItem(textObject); // preset to "Text" option
         return selectRenderPanel;
     }
 
     /** {@inheritDoc} */
     public void actionPerformed(ActionEvent event) {
         String command = event.getActionCommand();
         if (COMBO_CHANGE_COMMAND.equals(command)) {
             JComboBox jcb = (JComboBox) event.getSource();
 
             if (jcb != null) {
                 resultsRender = (ResultRenderer) jcb.getSelectedItem();
                 if (rightSide != null) {
                     // to restore last selected tab (better user-friendly)
                     selectedTab = rightSide.getSelectedIndex();
                     // Remove old right side
                     mainSplit.remove(rightSide);
 
                     // create and add a new right side
                     rightSide = new JTabbedPane();
                     mainSplit.add(rightSide);
                     resultsRender.setRightSide(rightSide);
                     resultsRender.setLastSelectedTab(selectedTab);
                     log.debug("selectedTab=" + selectedTab);
                     resultsRender.init();
                     // To display current sampler result before change
                     this.valueChanged(lastSelectionEvent);
                 }
             }
         }
     }
 
     public static String getResponseAsString(SampleResult res) {
         String response = null;
         if ((SampleResult.TEXT).equals(res.getDataType())) {
             // Showing large strings can be VERY costly, so we will avoid
             // doing so if the response
             // data is larger than 200K. TODO: instead, we could delay doing
             // the result.setText
             // call until the user chooses the "Response data" tab. Plus we
             // could warn the user
             // if this happens and revert the choice if he doesn't confirm
             // he's ready to wait.
             int len = res.getResponseData().length;
             if (MAX_DISPLAY_SIZE > 0 && len > MAX_DISPLAY_SIZE) {
                 StringBuilder builder = new StringBuilder(MAX_DISPLAY_SIZE+100);
                 builder.append(JMeterUtils.getResString("view_results_response_too_large_message")) //$NON-NLS-1$
                     .append(len).append(" > Max: ").append(MAX_DISPLAY_SIZE)
                     .append(", ").append(JMeterUtils.getResString("view_results_response_partial_message"))
                     .append("\n").append(res.getResponseDataAsString().substring(0, MAX_DISPLAY_SIZE)).append("\n...");
                 response = builder.toString();
                 log.warn(response);
             } else {
                 response = res.getResponseDataAsString();
             }
         }
         return response;
     }
 
     private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
         private static final long serialVersionUID = 4159626601097711565L;
 
         @Override
         public Component getTreeCellRendererComponent(JTree tree, Object value,
                 boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
             super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
             boolean failure = true;
             Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
             if (userObject instanceof SampleResult) {
                 failure = !(((SampleResult) userObject).isSuccessful());
             } else if (userObject instanceof AssertionResult) {
                 AssertionResult assertion = (AssertionResult) userObject;
                 failure = assertion.isError() || assertion.isFailure();
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
 
     /**
      * Handler for Checkbox
      */
     public void itemStateChanged(ItemEvent e) {
         // NOOP state is held by component
     }
 }
diff --git a/src/core/org/apache/jmeter/util/JMeterUtils.java b/src/core/org/apache/jmeter/util/JMeterUtils.java
index 1a8753fb9..77bc816df 100644
--- a/src/core/org/apache/jmeter/util/JMeterUtils.java
+++ b/src/core/org/apache/jmeter/util/JMeterUtils.java
@@ -1,1268 +1,1288 @@
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
 
 import java.awt.Dimension;
 import java.awt.HeadlessException;
 import java.awt.event.ActionListener;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
+import java.lang.reflect.InvocationTargetException;
 import java.net.InetAddress;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.MissingResourceException;
 import java.util.Properties;
 import java.util.Random;
 import java.util.ResourceBundle;
 import java.util.Vector;
 
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import javax.swing.JComboBox;
 import javax.swing.JOptionPane;
+import javax.swing.SwingUtilities;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
 import org.apache.jorphan.test.UnitTestManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.PatternCacheLRU;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.xml.sax.XMLReader;
 
 /**
  * This class contains the static utility methods used by JMeter.
  *
  */
 public class JMeterUtils implements UnitTestManager {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final PatternCacheLRU patternCache = new PatternCacheLRU(
             getPropDefault("oro.patterncache.size",1000), // $NON-NLS-1$
             new Perl5Compiler());
 
     private static final String EXPERT_MODE_PROPERTY = "jmeter.expertMode"; // $NON-NLS-1$
     
     private static final String ENGLISH_LANGUAGE = Locale.ENGLISH.getLanguage();
 
     private static volatile Properties appProperties;
 
     private static final Vector<LocaleChangeListener> localeChangeListeners = new Vector<LocaleChangeListener>();
 
     private static volatile Locale locale;
 
     private static volatile ResourceBundle resources;
 
     // What host am I running on?
 
     //@GuardedBy("this")
     private static String localHostIP = null;
     //@GuardedBy("this")
     private static String localHostName = null;
     //@GuardedBy("this")
     private static String localHostFullName = null;
 
     private static volatile boolean ignoreResorces = false; // Special flag for use in debugging resources
 
     private static final ThreadLocal<Perl5Matcher> localMatcher = new ThreadLocal<Perl5Matcher>() {
         @Override
         protected Perl5Matcher initialValue() {
             return new Perl5Matcher();
         }
     };
 
     // Provide Random numbers to whomever wants one
     private static final Random rand = new Random();
 
     /**
      * Gets Perl5Matcher for this thread.
      */
     public static Perl5Matcher getMatcher() {
         return localMatcher.get();
     }
 
     /**
      * This method is used by the init method to load the property file that may
      * even reside in the user space, or in the classpath under
      * org.apache.jmeter.jmeter.properties.
      *
      * The method also initialises logging and sets up the default Locale
      *
      * TODO - perhaps remove?
      * [still used
      *
      * @param file
      *            the file to load
      * @return the Properties from the file
      * @see #getJMeterProperties()
      * @see #loadJMeterProperties(String)
      * @see #initLogging()
      * @see #initLocale()
      */
     public static Properties getProperties(String file) {
         loadJMeterProperties(file);
         initLogging();
         initLocale();
         return appProperties;
     }
 
     /**
      * Initialise JMeter logging
      */
     public static void initLogging() {
         LoggingManager.initializeLogging(appProperties);
     }
 
     /**
      * Initialise the JMeter Locale
      */
     public static void initLocale() {
         String loc = appProperties.getProperty("language"); // $NON-NLS-1$
         if (loc != null) {
             String []parts = JOrphanUtils.split(loc,"_");// $NON-NLS-1$
             if (parts.length==2) {
                 setLocale(new Locale(parts[0], parts[1]));
             } else {
                 setLocale(new Locale(loc, "")); // $NON-NLS-1$
             }
 
         } else {
             setLocale(Locale.getDefault());
         }
     }
 
 
     /**
      * Load the JMeter properties file; if not found, then
      * default to "org/apache/jmeter/jmeter.properties" from the classpath
      *
      * c.f. loadProperties
      *
      */
     public static void loadJMeterProperties(String file) {
         Properties p = new Properties(System.getProperties());
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 is =
                     ClassLoader.getSystemResourceAsStream("org/apache/jmeter/jmeter.properties"); // $NON-NLS-1$
                 if (is == null) {
                     throw new RuntimeException("Could not read JMeter properties file");
                 }
                 p.load(is);
             } catch (IOException ex) {
                 // JMeter.fail("Could not read internal resource. " +
                 // "Archive is broken.");
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         appProperties = p;
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @return the Properties from the file, may be null (e.g. file not found)
      */
     public static Properties loadProperties(String file) {
         return loadProperties(file, null);
     }
 
     /**
      * This method loads a property file that may reside in the user space, or
      * in the classpath
      *
      * @param file
      *            the file to load
      * @param defaultProps a set of default properties
      * @return the Properties from the file; if it could not be processed, the defaultProps are returned.
      */
     public static Properties loadProperties(String file, Properties defaultProps) {
         Properties p = new Properties(defaultProps);
         InputStream is = null;
         try {
             File f = new File(file);
             is = new FileInputStream(f);
             p.load(is);
         } catch (IOException e) {
             try {
                 final URL resource = JMeterUtils.class.getClassLoader().getResource(file);
                 if (resource == null) {
                     log.warn("Cannot find " + file);
                     return defaultProps;
                 }
                 is = resource.openStream();
                 if (is == null) {
                     log.warn("Cannot open " + file);
                     return defaultProps;
                 }
                 p.load(is);
             } catch (IOException ex) {
                 log.warn("Error reading " + file + " " + ex.toString());
                 return defaultProps;
             }
         } finally {
             JOrphanUtils.closeQuietly(is);
         }
         return p;
     }
 
     public static PatternCacheLRU getPatternCache() {
         return patternCache;
     }
 
     /**
      * Get a compiled expression from the pattern cache (READ_ONLY).
      *
      * @param expression
      * @return compiled pattern
      *
      * @throws  org.apache.oro.text.regex.MalformedPatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression){
         return getPattern(expression, Perl5Compiler.READ_ONLY_MASK);
     }
 
     /**
      * Get a compiled expression from the pattern cache.
      *
      * @param expression RE
      * @param options e.g. READ_ONLY_MASK
      * @return compiled pattern
      *
      * @throws  org.apache.oro.text.regex.MalformedPatternException (Runtime)
      * This should be caught for expressions that may vary (e.g. user input)
      *
      */
     public static Pattern getPattern(String expression, int options){
         return patternCache.getPattern(expression, options);
     }
 
     public void initializeProperties(String file) {
         System.out.println("Initializing Properties: " + file);
         getProperties(file);
     }
 
     /**
      * Convenience method for
      * {@link ClassFinder#findClassesThatExtend(String[], Class[], boolean)}
      * with the option to include inner classes in the search set to false
      * and the path list is derived from JMeterUtils.getSearchPaths().
      *
      * @param superClass - single class to search for
      * @return List of Strings containing discovered class names.
      */
     public static List<String> findClassesThatExtend(Class<?> superClass)
         throws IOException {
         return ClassFinder.findClassesThatExtend(getSearchPaths(), new Class[]{superClass}, false);
     }
 
     /**
      * Generate a list of paths to search.
      * The output array always starts with
      * JMETER_HOME/lib/ext
      * and is followed by any paths obtained from the "search_paths" JMeter property.
      * 
      * @return array of path strings
      */
     public static String[] getSearchPaths() {
         String p = JMeterUtils.getPropDefault("search_paths", null); // $NON-NLS-1$
         String[] result = new String[1];
 
         if (p != null) {
             String[] paths = p.split(";"); // $NON-NLS-1$
             result = new String[paths.length + 1];
             for (int i = 1; i < result.length; i++) {
                 result[i] = paths[i - 1];
             }
         }
         result[0] = getJMeterHome() + "/lib/ext"; // $NON-NLS-1$
         return result;
     }
 
     /**
      * Provide random numbers
      *
      * @param r -
      *            the upper bound (exclusive)
      */
     public static int getRandomInt(int r) {
         return rand.nextInt(r);
     }
 
     /**
      * Changes the current locale: re-reads resource strings and notifies
      * listeners.
      *
      * @param loc -
      *            new locale
      */
     public static void setLocale(Locale loc) {
         log.info("Setting Locale to " + loc.toString());
         /*
          * See bug 29920. getBundle() defaults to the property file for the
          * default Locale before it defaults to the base property file, so we
          * need to change the default Locale to ensure the base property file is
          * found.
          */
         Locale def = null;
         boolean isDefault = false; // Are we the default language?
         if (loc.getLanguage().equals(ENGLISH_LANGUAGE)) {
             isDefault = true;
             def = Locale.getDefault();
             // Don't change locale from en_GB to en
             if (!def.getLanguage().equals(ENGLISH_LANGUAGE)) {
                 Locale.setDefault(Locale.ENGLISH);
             } else {
                 def = null; // no need to reset Locale
             }
         }
         if (loc.toString().equals("ignoreResources")){ // $NON-NLS-1$
             log.warn("Resource bundles will be ignored");
             ignoreResorces = true;
             // Keep existing settings
         } else {
             ignoreResorces = false;
             ResourceBundle resBund = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", loc); // $NON-NLS-1$
             resources = resBund;
             locale = loc;
             final Locale resBundLocale = resBund.getLocale();
             if (isDefault || resBundLocale.equals(loc)) {// language change worked
             // Check if we at least found the correct language:
             } else if (resBundLocale.getLanguage().equals(loc.getLanguage())) {
                 log.info("Could not find resources for '"+loc.toString()+"', using '"+resBundLocale.toString()+"'");
             } else {
                 log.error("Could not find resources for '"+loc.toString()+"'");
             }
         }
         notifyLocaleChangeListeners();
         /*
          * Reset Locale if necessary so other locales are properly handled
          */
         if (def != null) {
             Locale.setDefault(def);
         }
     }
 
     /**
      * Gets the current locale.
      *
      * @return current locale
      */
     public static Locale getLocale() {
         return locale;
     }
 
     public static void addLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.add(listener);
     }
 
     public static void removeLocaleChangeListener(LocaleChangeListener listener) {
         localeChangeListeners.remove(listener);
     }
 
     /**
      * Notify all listeners interested in locale changes.
      *
      */
     private static void notifyLocaleChangeListeners() {
         LocaleChangeEvent event = new LocaleChangeEvent(JMeterUtils.class, locale);
         @SuppressWarnings("unchecked") // clone will produce correct type
         // TODO but why do we need to clone the list?
         Vector<LocaleChangeListener> listeners = (Vector<LocaleChangeListener>) localeChangeListeners.clone();
         for (LocaleChangeListener listener : listeners) {
             listener.localeChanged(event);
         }
     }
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      */
     public static String getResString(String key) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]"); // $NON-NLS-1$
     }
     
     /**
      * Gets the resource string for this key in Locale.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param forcedLocale Force a particular locale
      * @return the resource string if the key is found; otherwise, return
      *         "[res_key="+key+"]"
      * @since 2.7
      */
     public static String getResString(String key, Locale forcedLocale) {
         return getResStringDefault(key, RES_KEY_PFX + key + "]", // $NON-NLS-1$
                 forcedLocale); 
     }
 
     public static final String RES_KEY_PFX = "[res_key="; // $NON-NLS-1$
 
     /**
      * Gets the resource string for this key.
      *
      * If the resource is not found, a warning is logged
      *
      * @param key
      *            the key in the resource file
      * @param defaultValue -
      *            the default value
      *
      * @return the resource string if the key is found; otherwise, return the
      *         default
      * @deprecated Only intended for use in development; use
      *             getResString(String) normally
      */
     @Deprecated
     public static String getResString(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue);
     }
 
     /*
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue) {
         return getResStringDefault(key, defaultValue, null);
     }
     /*
      * Helper method to do the actual work of fetching resources; allows
      * getResString(S,S) to be deprecated without affecting getResString(S);
      */
     private static String getResStringDefault(String key, String defaultValue, Locale forcedLocale) {
         if (key == null) {
             return null;
         }
         // Resource keys cannot contain spaces, and are forced to lower case
         String resKey = key.replace(' ', '_'); // $NON-NLS-1$ // $NON-NLS-2$
         resKey = resKey.toLowerCase(java.util.Locale.ENGLISH);
         String resString = null;
         try {
             ResourceBundle bundle = resources;
             if(forcedLocale != null) {
                 bundle = ResourceBundle.getBundle("org.apache.jmeter.resources.messages", forcedLocale); // $NON-NLS-1$
             }
             resString = bundle.getString(resKey);
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "["+key+"]";
             }
         } catch (MissingResourceException mre) {
             if (ignoreResorces ){ // Special mode for debugging resource handling
                 return "[?"+key+"?]";
             }
             log.warn("ERROR! Resource string not found: [" + resKey + "]", mre);
             resString = defaultValue;
         }
         return resString;
     }
 
     /**
      * To get I18N label from properties file
      * 
      * @param key
      *            in messages.properties
      * @return I18N label without (if exists) last colon ':' and spaces
      */
     public static String getParsedLabel(String key) {
         String value = JMeterUtils.getResString(key);
         return value.replaceFirst("(?m)\\s*?:\\s*$", ""); // $NON-NLS-1$ $NON-NLS-2$
     }
     
     /**
      * Get the locale name as a resource.
      * Does not log an error if the resource does not exist.
      * This is needed to support additional locales, as they won't be in existing messages files.
      *
      * @param locale name
      * @return the locale display name as defined in the current Locale or the original string if not present
      */
     public static String getLocaleString(String locale){
         // All keys in messages.properties are lowercase (historical reasons?)
         String resKey = locale.toLowerCase(java.util.Locale.ENGLISH);
         try {
             return resources.getString(resKey);
         } catch (MissingResourceException e) {
         }
         return locale;
     }
     /**
      * This gets the currently defined appProperties. It can only be called
      * after the {@link #getProperties(String)} or {@link #loadJMeterProperties(String)} 
      * method has been called.
      *
      * @return The JMeterProperties value, 
      *         may be null if {@link #loadJMeterProperties(String)} has not been called
      * @see #getProperties(String)
      * @see #loadJMeterProperties(String)
      */
     public static Properties getJMeterProperties() {
         return appProperties;
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images. <name>
      *
      * @param name
      *            Description of Parameter
      * @return The Image value
      */
     public static ImageIcon getImage(String name) {
         try {
             return new ImageIcon(JMeterUtils.class.getClassLoader().getResource(
                     "org/apache/jmeter/images/" + name.trim())); // $NON-NLS-1$
         } catch (NullPointerException e) {
             log.warn("no icon for " + name);
             return null;
         } catch (NoClassDefFoundError e) {// Can be returned by headless hosts
             log.info("no icon for " + name + " " + e.getMessage());
             return null;
         } catch (InternalError e) {// Can be returned by headless hosts
             log.info("no icon for " + name + " " + e.getMessage());
             return null;
         }
     }
 
     /**
      * This looks for the requested image in the classpath under
      * org.apache.jmeter.images. <name>, and also sets the description
      * of the image, which is useful if the icon is going to be placed
      * on the clipboard.
      *
      * @param name
      *            the name of the image
      * @param description
      *            the description of the image
      * @return The Image value
      */
     public static ImageIcon getImage(String name, String description) {
         ImageIcon icon = getImage(name);
         if(icon != null) {
             icon.setDescription(description);
         }
         return icon;
     }
 
     public static String getResourceFileAsText(String name) {
         BufferedReader fileReader = null;
         try {
             String lineEnd = System.getProperty("line.separator"); // $NON-NLS-1$
             fileReader = new BufferedReader(new InputStreamReader(JMeterUtils.class.getClassLoader()
                     .getResourceAsStream(name)));
             StringBuilder text = new StringBuilder();
             String line = "NOTNULL"; // $NON-NLS-1$
             while (line != null) {
                 line = fileReader.readLine();
                 if (line != null) {
                     text.append(line);
                     text.append(lineEnd);
                 }
             }
             // Done by finally block: fileReader.close();
             return text.toString();
         } catch (NullPointerException e) // Cannot find file
         {
             return ""; // $NON-NLS-1$
         } catch (IOException e) {
             return ""; // $NON-NLS-1$
         } finally {
             IOUtils.closeQuietly(fileReader);
         }
     }
 
     /**
      * Creates the vector of Timers plugins.
      *
      * @param properties
      *            Description of Parameter
      * @return The Timers value
      */
     public static Vector<Object> getTimers(Properties properties) {
         return instantiate(getVector(properties, "timer."), // $NON-NLS-1$
                 "org.apache.jmeter.timers.Timer"); // $NON-NLS-1$
     }
 
     /**
      * Creates the vector of visualizer plugins.
      *
      * @param properties
      *            Description of Parameter
      * @return The Visualizers value
      */
     public static Vector<Object> getVisualizers(Properties properties) {
         return instantiate(getVector(properties, "visualizer."), // $NON-NLS-1$
                 "org.apache.jmeter.visualizers.Visualizer"); // $NON-NLS-1$
     }
 
     /**
      * Creates a vector of SampleController plugins.
      *
      * @param properties
      *            The properties with information about the samplers
      * @return The Controllers value
      */
     // TODO - does not appear to be called directly
     public static Vector<Object> getControllers(Properties properties) {
         String name = "controller."; // $NON-NLS-1$
         Vector<Object> v = new Vector<Object>();
         Enumeration<?> names = properties.keys();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(name)) {
                 Object o = instantiate(properties.getProperty(prop),
                         "org.apache.jmeter.control.SamplerController"); // $NON-NLS-1$
                 v.addElement(o);
             }
         }
         return v;
     }
 
     /**
      * Create a string of class names for a particular SamplerController
      *
      * @param properties
      *            The properties with info about the samples.
      * @param name
      *            The name of the sampler controller.
      * @return The TestSamples value
      */
     public static String[] getTestSamples(Properties properties, String name) {
         Vector<String> vector = getVector(properties, name + ".testsample"); // $NON-NLS-1$
         return vector.toArray(new String[vector.size()]);
     }
 
     /**
      * Create an instance of an org.xml.sax.Parser based on the default props.
      *
      * @return The XMLParser value
      */
     public static XMLReader getXMLParser() {
         XMLReader reader = null;
         try {
             reader = (XMLReader) instantiate(getPropDefault("xml.parser", // $NON-NLS-1$
                     "org.apache.xerces.parsers.SAXParser"), // $NON-NLS-1$
                     "org.xml.sax.XMLReader"); // $NON-NLS-1$
             // reader = xmlFactory.newSAXParser().getXMLReader();
         } catch (Exception e) {
             reader = (XMLReader) instantiate(getPropDefault("xml.parser", // $NON-NLS-1$
                     "org.apache.xerces.parsers.SAXParser"), // $NON-NLS-1$
                     "org.xml.sax.XMLReader"); // $NON-NLS-1$
         }
         return reader;
     }
 
     /**
      * Creates the vector of alias strings.
      *
      * @param properties
      * @return The Alias value
      */
     public static Hashtable<String, String> getAlias(Properties properties) {
         return getHashtable(properties, "alias."); // $NON-NLS-1$
     }
 
     /**
      * Creates a vector of strings for all the properties that start with a
      * common prefix.
      *
      * @param properties
      *            Description of Parameter
      * @param name
      *            Description of Parameter
      * @return The Vector value
      */
     public static Vector<String> getVector(Properties properties, String name) {
         Vector<String> v = new Vector<String>();
         Enumeration<?> names = properties.keys();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(name)) {
                 v.addElement(properties.getProperty(prop));
             }
         }
         return v;
     }
 
     /**
      * Creates a table of strings for all the properties that start with a
      * common prefix.
      *
      * @param properties input to search
      * @param prefix to match against properties
      * @return a Hashtable where the keys are the original keys with the prefix removed
      */
     public static Hashtable<String, String> getHashtable(Properties properties, String prefix) {
         Hashtable<String, String> t = new Hashtable<String, String>();
         Enumeration<?> names = properties.keys();
         final int length = prefix.length();
         while (names.hasMoreElements()) {
             String prop = (String) names.nextElement();
             if (prop.startsWith(prefix)) {
                 t.put(prop.substring(length), properties.getProperty(prop));
             }
         }
         return t;
     }
 
     /**
      * Get a int value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static int getPropDefault(String propName, int defaultVal) {
         int ans;
         try {
             ans = (Integer.valueOf(appProperties.getProperty(propName, Integer.toString(defaultVal)).trim()))
                     .intValue();
         } catch (Exception e) {
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a boolean value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static boolean getPropDefault(String propName, boolean defaultVal) {
         boolean ans;
         try {
             String strVal = appProperties.getProperty(propName, Boolean.toString(defaultVal)).trim();
             if (strVal.equalsIgnoreCase("true") || strVal.equalsIgnoreCase("t")) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = true;
             } else if (strVal.equalsIgnoreCase("false") || strVal.equalsIgnoreCase("f")) { // $NON-NLS-1$  // $NON-NLS-2$
                 ans = false;
             } else {
                 ans = ((Integer.valueOf(strVal)).intValue() == 1);
             }
         } catch (Exception e) {
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a long value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static long getPropDefault(String propName, long defaultVal) {
         long ans;
         try {
             ans = (Long.valueOf(appProperties.getProperty(propName, Long.toString(defaultVal)).trim())).longValue();
         } catch (Exception e) {
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get a String value with default if not present.
      *
      * @param propName
      *            the name of the property.
      * @param defaultVal
      *            the default value.
      * @return The PropDefault value
      */
     public static String getPropDefault(String propName, String defaultVal) {
         String ans;
         try {
             ans = appProperties.getProperty(propName, defaultVal).trim();
         } catch (Exception e) {
             ans = defaultVal;
         }
         return ans;
     }
 
     /**
      * Get the value of a JMeter property.
      *
      * @param propName
      *            the name of the property.
      * @return the value of the JMeter property, or null if not defined
      */
     public static String getProperty(String propName) {
         String ans = null;
         try {
             ans = appProperties.getProperty(propName);
         } catch (Exception e) {
             ans = null;
         }
         return ans;
     }
 
     /**
      * Set a String value
      *
      * @param propName
      *            the name of the property.
      * @param propValue
      *            the value of the property
      * @return the previous value of the property
      */
     public static Object setProperty(String propName, String propValue) {
         return appProperties.setProperty(propName, propValue);
     }
 
     /**
      * Sets the selection of the JComboBox to the Object 'name' from the list in
      * namVec.
      * NOTUSED?
      */
     public static void selJComboBoxItem(Properties properties, JComboBox combo, Vector<?> namVec, String name) {
         int idx = namVec.indexOf(name);
         combo.setSelectedIndex(idx);
         // Redisplay.
         combo.updateUI();
         return;
     }
 
     /**
      * Instatiate an object and guarantee its class.
      *
      * @param className
      *            The name of the class to instantiate.
      * @param impls
      *            The name of the class it subclases.
      * @return Description of the Returned Value
      */
     public static Object instantiate(String className, String impls) {
         if (className != null) {
             className = className.trim();
         }
 
         if (impls != null) {
             impls = impls.trim();
         }
 
         try {
             Class<?> c = Class.forName(impls);
             try {
                 Class<?> o = Class.forName(className);
                 Object res = o.newInstance();
                 if (c.isInstance(res)) {
                     return res;
                 }
                 throw new IllegalArgumentException(className + " is not an instance of " + impls);
             } catch (ClassNotFoundException e) {
                 log.error("Error loading class " + className + ": class is not found");
             } catch (IllegalAccessException e) {
                 log.error("Error loading class " + className + ": does not have access");
             } catch (InstantiationException e) {
                 log.error("Error loading class " + className + ": could not instantiate");
             } catch (NoClassDefFoundError e) {
                 log.error("Error loading class " + className + ": couldn't find class " + e.getMessage());
             }
         } catch (ClassNotFoundException e) {
             log.error("Error loading class " + impls + ": was not found.");
         }
         return null;
     }
 
     /**
      * Instantiate a vector of classes
      *
      * @param v
      *            Description of Parameter
      * @param className
      *            Description of Parameter
      * @return Description of the Returned Value
      */
     public static Vector<Object> instantiate(Vector<String> v, String className) {
         Vector<Object> i = new Vector<Object>();
         try {
             Class<?> c = Class.forName(className);
             Enumeration<String> elements = v.elements();
             while (elements.hasMoreElements()) {
                 String name = elements.nextElement();
                 try {
                     Object o = Class.forName(name).newInstance();
                     if (c.isInstance(o)) {
                         i.addElement(o);
                     }
                 } catch (ClassNotFoundException e) {
                     log.error("Error loading class " + name + ": class is not found");
                 } catch (IllegalAccessException e) {
                     log.error("Error loading class " + name + ": does not have access");
                 } catch (InstantiationException e) {
                     log.error("Error loading class " + name + ": could not instantiate");
                 } catch (NoClassDefFoundError e) {
                     log.error("Error loading class " + name + ": couldn't find class " + e.getMessage());
                 }
             }
         } catch (ClassNotFoundException e) {
             log.error("Error loading class " + className + ": class is not found");
         }
         return i;
     }
 
     /**
      * Create a button with the netscape style
      *
      * @param name
      *            Description of Parameter
      * @param listener
      *            Description of Parameter
      * @return Description of the Returned Value
      */
     public static JButton createButton(String name, ActionListener listener) {
         JButton button = new JButton(getImage(name + ".on.gif")); // $NON-NLS-1$
         button.setDisabledIcon(getImage(name + ".off.gif")); // $NON-NLS-1$
         button.setRolloverIcon(getImage(name + ".over.gif")); // $NON-NLS-1$
         button.setPressedIcon(getImage(name + ".down.gif")); // $NON-NLS-1$
         button.setActionCommand(name);
         button.addActionListener(listener);
         button.setRolloverEnabled(true);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(false);
         button.setPreferredSize(new Dimension(24, 24));
         return button;
     }
 
     /**
      * Create a button with the netscape style
      *
      * @param name
      *            Description of Parameter
      * @param listener
      *            Description of Parameter
      * @return Description of the Returned Value
      */
     public static JButton createSimpleButton(String name, ActionListener listener) {
         JButton button = new JButton(getImage(name + ".gif")); // $NON-NLS-1$
         button.setActionCommand(name);
         button.addActionListener(listener);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(false);
         button.setPreferredSize(new Dimension(25, 25));
         return button;
     }
 
 
     /**
      * Report an error through a dialog box.
      * Title defaults to "error_title" resource string
      * @param errorMsg - the error message.
      */
     public static void reportErrorToUser(String errorMsg) {
         reportErrorToUser(errorMsg, JMeterUtils.getResString("error_title")); // $NON-NLS-1$
     }
 
     /**
      * Report an error through a dialog box.
      *
      * @param errorMsg - the error message.
      * @param titleMsg - title string
      */
     public static void reportErrorToUser(String errorMsg, String titleMsg) {
         if (errorMsg == null) {
             errorMsg = "Unknown error - see log file";
             log.warn("Unknown error", new Throwable("errorMsg == null"));
         }
         GuiPackage instance = GuiPackage.getInstance();
         if (instance == null) {
             System.out.println(errorMsg);
             return; // Done
         }
         try {
             JOptionPane.showMessageDialog(instance.getMainFrame(),
                     errorMsg,
                     titleMsg,
                     JOptionPane.ERROR_MESSAGE);
         } catch (HeadlessException e) {
                 log.warn("reportErrorToUser(\"" + errorMsg + "\") caused", e);
         }
     }
 
     /**
      * Finds a string in an array of strings and returns the
      *
      * @param array
      *            Array of strings.
      * @param value
      *            String to compare to array values.
      * @return Index of value in array, or -1 if not in array.
      */
     //TODO - move to JOrphanUtils?
     public static int findInArray(String[] array, String value) {
         int count = -1;
         int index = -1;
         if (array != null && value != null) {
             while (++count < array.length) {
                 if (array[count] != null && array[count].equals(value)) {
                     index = count;
                     break;
                 }
             }
         }
         return index;
     }
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             }
             if (count + 1 < splittee.length && splittee[count + 1] != null) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
 
     // End Method
 
     /**
      * Takes an array of strings and a tokenizer character, and returns a string
      * of all the strings concatenated with the tokenizer string in between each
      * one.
      *
      * @param splittee
      *            Array of Objects to be concatenated.
      * @param splitChar
      *            Object to unsplit the strings with.
      * @param def
      *            Default value to replace null values in array.
      * @return Array of all the tokens.
      */
     //TODO - move to JOrphanUtils?
     public static String unsplit(Object[] splittee, Object splitChar, String def) {
         StringBuilder retVal = new StringBuilder();
         int count = -1;
         while (++count < splittee.length) {
             if (splittee[count] != null) {
                 retVal.append(splittee[count]);
             } else {
                 retVal.append(def);
             }
             if (count + 1 < splittee.length) {
                 retVal.append(splitChar);
             }
         }
         return retVal.toString();
     }
 
     /**
      * Get the JMeter home directory - does not include the trailing separator.
      *
      * @return the home directory
      */
     public static String getJMeterHome() {
         return jmDir;
     }
 
     /**
      * Get the JMeter bin directory - does not include the trailing separator.
      *
      * @return the bin directory
      */
     public static String getJMeterBinDir() {
         return jmBin;
     }
 
     public static void setJMeterHome(String home) {
         jmDir = home;
         jmBin = jmDir + File.separator + "bin"; // $NON-NLS-1$
     }
 
     // TODO needs to be synch? Probably not changed after threads have started
     private static String jmDir; // JMeter Home directory (excludes trailing separator)
     private static String jmBin; // JMeter bin directory (excludes trailing separator)
 
 
     /**
      * Gets the JMeter Version.
      *
      * @return the JMeter version string
      */
     public static String getJMeterVersion() {
         return JMeterVersion.getVERSION();
     }
 
     /**
      * Gets the JMeter copyright.
      *
      * @return the JMeter copyright string
      */
     public static String getJMeterCopyright() {
         return JMeterVersion.getCopyRight();
     }
 
     /**
      * Determine whether we are in 'expert' mode. Certain features may be hidden
      * from user's view unless in expert mode.
      *
      * @return true iif we're in expert mode
      */
     public static boolean isExpertMode() {
         return JMeterUtils.getPropDefault(EXPERT_MODE_PROPERTY, false);
     }
 
     /**
      * Find a file in the current directory or in the JMeter bin directory.
      *
      * @param fileName
      * @return File object
      */
     public static File findFile(String fileName){
         File f =new File(fileName);
         if (!f.exists()){
             f=new File(getJMeterBinDir(),fileName);
         }
         return f;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostAddress()
      *
      * @return String representation of local IP address
      */
     public static synchronized String getLocalHostIP(){
         if (localHostIP == null) {
             getLocalHostDetails();
         }
         return localHostIP;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getHostName()
      *
      * @return local host name
      */
     public static synchronized String getLocalHostName(){
         if (localHostName == null) {
             getLocalHostDetails();
         }
         return localHostName;
     }
 
     /**
      * Returns the cached result from calling
      * InetAddress.getLocalHost().getCanonicalHostName()
      *
      * @return local host name in canonical form
      */
     public static synchronized String getLocalHostFullName(){
         if (localHostFullName == null) {
             getLocalHostDetails();
         }
         return localHostFullName;
     }
 
     private static void getLocalHostDetails(){
         InetAddress localHost=null;
         try {
             localHost = InetAddress.getLocalHost();
         } catch (UnknownHostException e1) {
             log.error("Unable to get local host IP address.");
             return; // TODO - perhaps this should be a fatal error?
         }
         localHostIP=localHost.getHostAddress();
         localHostName=localHost.getHostName();
         localHostFullName=localHost.getCanonicalHostName();
     }
     
     /**
      * Split line into name/value pairs and remove colon ':'
      * 
      * @param headers
      *            multi-line string headers
      * @return a map name/value for each header
      */
     public static LinkedHashMap<String, String> parseHeaders(String headers) {
         LinkedHashMap<String, String> linkedHeaders = new LinkedHashMap<String, String>();
         String[] list = headers.split("\n"); // $NON-NLS-1$
         for (String header : list) {
             int colon = header.indexOf(':'); // $NON-NLS-1$
             if (colon <= 0) {
                 linkedHeaders.put(header, ""); // Empty value // $NON-NLS-1$
             } else {
                 linkedHeaders.put(header.substring(0, colon).trim(), header
                         .substring(colon + 1).trim());
             }
         }
         return linkedHeaders;
     }
-    
+
+    /**
+     * Run the runnable in AWT Thread if current thread is not AWT thread
+     * otherwise runs call {@link SwingUtilities#invokeAndWait(Runnable)}
+     * @param runnable {@link Runnable}
+     */
+    public static final void runSafe(Runnable runnable) {
+        if(SwingUtilities.isEventDispatchThread()) {
+            runnable.run();
+        } else {
+            try {
+                SwingUtilities.invokeAndWait(runnable);
+            } catch (InterruptedException e) {
+                throw new Error(e);
+            } catch (InvocationTargetException e) {
+                throw new Error(e);
+            }
+        }
+    }
 }
diff --git a/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java b/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java
index b8b4db7ff..bb7049242 100644
--- a/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java
+++ b/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java
@@ -1,289 +1,289 @@
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
 
 import java.util.Map;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jorphan.math.StatCalculatorLong;
 
 /**
  * Aggegate sample data container. Just instantiate a new instance of this
  * class, and then call {@link #addSample(SampleResult)} a few times, and pull
  * the stats out with whatever methods you prefer.
  *
  */
 public class SamplingStatCalculator {
     private final StatCalculatorLong calculator = new StatCalculatorLong();
 
     private double maxThroughput;
 
     private long firstTime;
 
     private String label;
 
-    private Sample currentSample;
+    private volatile Sample currentSample;
 
     public SamplingStatCalculator(){ // Only for use by test code
         this("");
     }
 
     public SamplingStatCalculator(String label) {
         this.label = label;
         init();
     }
 
     private void init() {
         firstTime = Long.MAX_VALUE;
         calculator.clear();
         maxThroughput = Double.MIN_VALUE;
         currentSample = new Sample();
     }
 
     /**
      * Clear the counters (useful for differential stats)
      *
      */
     public synchronized void clear() {
         init();
     }
 
     public Sample getCurrentSample() {
         return currentSample;
     }
 
     /**
      * Get the elapsed time for the samples
      *
      * @return how long the samples took
      */
     public long getElapsed() {
         if (getCurrentSample().getEndTime() == 0) {
             return 0;// No samples collected ...
         }
         return getCurrentSample().getEndTime() - firstTime;
     }
 
     /**
      * Returns the throughput associated to this sampler in requests per second.
      * May be slightly skewed because it takes the timestamps of the first and
      * last samples as the total time passed, and the test may actually have
      * started before that start time and ended after that end time.
      */
     public double getRate() {
         if (calculator.getCount() == 0) {
             return 0.0; // Better behaviour when howLong=0 or lastTime=0
         }
 
         return getCurrentSample().getThroughput();
     }
 
     /**
      * Throughput in bytes / second
      *
      * @return throughput in bytes/second
      */
     public double getBytesPerSecond() {
         // Code duplicated from getPageSize()
         double rate = 0;
         if (this.getElapsed() > 0 && calculator.getTotalBytes() > 0) {
             rate = calculator.getTotalBytes() / ((double) this.getElapsed() / 1000);
         }
         if (rate < 0) {
             rate = 0;
         }
         return rate;
     }
 
     /**
      * Throughput in kilobytes / second
      *
      * @return Throughput in kilobytes / second
      */
     public double getKBPerSecond() {
         return getBytesPerSecond() / 1024; // 1024=bytes per kb
     }
 
     /**
      * calculates the average page size, which means divide the bytes by number
      * of samples.
      *
      * @return average page size in bytes (0 if sample count is zero)
      */
     public double getAvgPageBytes() {
         long count = calculator.getCount();
         if (count == 0) {
             return 0;
         }
         return calculator.getTotalBytes() / (double) count;
     }
 
     public String getLabel() {
         return label;
     }
 
     /**
      * Records a sample.
      *
      */
     public Sample addSample(SampleResult res) {
         long rtime, cmean, cstdv, cmedian, cpercent, eCount, endTime;
         double throughput;
         boolean rbool;
         synchronized (calculator) {
             calculator.addValue(res.getTime(), res.getSampleCount());
             calculator.addBytes(res.getBytes());
             setStartTime(res);
             eCount = getCurrentSample().getErrorCount();
             if (!res.isSuccessful()) {
                 eCount++;
             }
             endTime = getEndTime(res);
             long howLongRunning = endTime - firstTime;
             throughput = ((double) calculator.getCount() / (double) howLongRunning) * 1000.0;
             if (throughput > maxThroughput) {
                 maxThroughput = throughput;
             }
 
             rtime = res.getTime();
             cmean = (long)calculator.getMean();
             cstdv = (long)calculator.getStandardDeviation();
             cmedian = calculator.getMedian().longValue();
             cpercent = calculator.getPercentPoint( 0.500 ).longValue();
 // TODO cpercent is the same as cmedian here - why? and why pass it to "distributionLine"?
             rbool = res.isSuccessful();
         }
 
         long count = calculator.getCount();
         Sample s =
             new Sample( null, rtime, cmean, cstdv, cmedian, cpercent, throughput, eCount, rbool, count, endTime );
         currentSample = s;
         return s;
     }
 
     private long getEndTime(SampleResult res) {
         long endTime = res.getEndTime();
         long lastTime = getCurrentSample().getEndTime();
         if (lastTime < endTime) {
             lastTime = endTime;
         }
         return lastTime;
     }
 
     /**
      * @param res
      */
     private void setStartTime(SampleResult res) {
         long startTime = res.getStartTime();
         if (firstTime > startTime) {
             // this is our first sample, set the start time to current timestamp
             firstTime = startTime;
         }
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
 
         if (calculator.getCount() == 0) {
             return (rval);
         }
         rval = (double) getCurrentSample().getErrorCount() / (double) calculator.getCount();
         return (rval);
     }
 
     /**
      * For debugging purposes, only.
      */
     @Override
     public String toString() {
         StringBuilder mySB = new StringBuilder();
 
         mySB.append("Samples: " + this.getCount() + "  ");
         mySB.append("Avg: " + this.getMean() + "  ");
         mySB.append("Min: " + this.getMin() + "  ");
         mySB.append("Max: " + this.getMax() + "  ");
         mySB.append("Error Rate: " + this.getErrorPercentage() + "  ");
         mySB.append("Sample Rate: " + this.getRate());
         return (mySB.toString());
     }
 
     /**
      * @return errorCount
      */
     public long getErrorCount() {
         return getCurrentSample().getErrorCount();
     }
 
     /**
      * @return Returns the maxThroughput.
      */
     public double getMaxThroughput() {
         return maxThroughput;
     }
 
     public Map<Number, Number[]> getDistribution() {
         return calculator.getDistribution();
     }
 
     public Number getPercentPoint(double percent) {
         return calculator.getPercentPoint(percent);
     }
 
     public long getCount() {
         return calculator.getCount();
     }
 
     public Number getMax() {
         return calculator.getMax();
     }
 
     public double getMean() {
         return calculator.getMean();
     }
 
     public Number getMeanAsNumber() {
         return Long.valueOf((long) calculator.getMean());
     }
 
     public Number getMedian() {
         return calculator.getMedian();
     }
 
     public Number getMin() {
         if (calculator.getMin().longValue() < 0) {
             return Long.valueOf(0);
         }
         return calculator.getMin();
     }
 
     public Number getPercentPoint(float percent) {
         return calculator.getPercentPoint(percent);
     }
 
     public double getStandardDeviation() {
         return calculator.getStandardDeviation();
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 34f0a9971..0437d24b8 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,157 +1,158 @@
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
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 2.7 =================== -->
 
 <h1>Version 2.7</h1>
 
 <h2>New and Noteworthy</h2>
 
 
 <!--  =================== Known bugs =================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode (see Bugs 40671, 41286, 44973, 50898). 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>Listeners don't show iteration counts when a If Controller has a condition which is always false from the first iteration (see Bug 52496).  
 A workaround is to add a sampler at the same level as (or superior to) the If Controller.
 For example a Test Action sampler with 0 wait time (which doesn't generate a sample),
 or a Debug Sampler with all fields set to False (to reduce the sample size).
 </p>
 
 <!-- =================== Incompatible changes =================== -->
 
 <h2>Incompatible changes</h2>
 
 
 
 <!-- =================== Bug fixes =================== -->
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 52613 - Using Raw Post Body option, text gets encoded</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 51737 - TCPSampler : Packet gets converted/corrupted</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 <li>Bug 52551 - Function Helper Dialog does not switch language correctly</li>
 <li>Bug 52552 - Help reference only works in English</li>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 52639 - JSplitPane divider for log panel should be hidden if log is not activated</li>
 <li>Bug 52672 - Change Controller action deletes all but one child samplers</li>
+<li>Bug 52694 - Deadlock in GUI related to non AWT Threads updating GUI</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 52603 - MailerVisualizer : Enable SSL , TLS and Authentication</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 45839 - Test Action : Allow premature exit from a loop</li>
 <li>Bug 52614 - MailerModel.sendMail has strange way to calculate debug setting</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Upgraded to rhino 1.7R3 (was js-1.7R2.jar). 
 Note: the Maven coordinates for the jar were changed from rhino:js to org.mozilla:rhino.
 This does not affect JMeter directly, but might cause problems if using JMeter in a Maven project
 with other code that depends on an earlier version of the Rhino Javascript jar.
 </li>
 <li>Bug 52675 - Refactor Proxy and HttpRequestHdr to allow Sampler Creation by Proxy</li>
 </ul>
 
 </section> 
 </body> 
 </document>
