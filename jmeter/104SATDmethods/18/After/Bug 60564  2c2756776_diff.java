diff --git a/src/components/org/apache/jmeter/visualizers/AxisGraph.java b/src/components/org/apache/jmeter/visualizers/AxisGraph.java
index 8b9a79b19..3b1d879fb 100644
--- a/src/components/org/apache/jmeter/visualizers/AxisGraph.java
+++ b/src/components/org/apache/jmeter/visualizers/AxisGraph.java
@@ -1,437 +1,437 @@
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
 
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Font;
 import java.awt.Graphics;
 import java.awt.Graphics2D;
 import java.awt.LayoutManager;
 import java.awt.Paint;
 import java.math.BigDecimal;
 
 import javax.swing.JPanel;
 import javax.swing.UIManager;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.jCharts.axisChart.AxisChart;
 import org.jCharts.axisChart.customRenderers.axisValue.renderers.ValueLabelPosition;
 import org.jCharts.axisChart.customRenderers.axisValue.renderers.ValueLabelRenderer;
 import org.jCharts.chartData.AxisChartDataSet;
 import org.jCharts.chartData.ChartDataException;
 import org.jCharts.chartData.DataSeries;
 import org.jCharts.properties.AxisProperties;
 import org.jCharts.properties.ChartProperties;
 import org.jCharts.properties.ClusteredBarChartProperties;
 import org.jCharts.properties.DataAxisProperties;
 import org.jCharts.properties.LabelAxisProperties;
 import org.jCharts.properties.LegendAreaProperties;
 import org.jCharts.properties.LegendProperties;
 import org.jCharts.properties.PropertyException;
 import org.jCharts.properties.util.ChartFont;
 import org.jCharts.types.ChartType;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  *
  * Axis graph is used by StatGraphVisualizer, which generates bar graphs
  * from the statistical data.
  */
 public class AxisGraph extends JPanel {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AxisGraph.class);
 
     private static final String ELLIPSIS = "..."; //$NON-NLS-1$
     private static final int ELLIPSIS_LEN = ELLIPSIS.length();
 
     protected double[][] data = null;
     protected String title;
     protected String xAxisTitle;
     protected String yAxisTitle;
     protected String yAxisLabel;
     protected int maxLength;
     protected String[] xAxisLabels;
     protected int width;
     protected int height;
     
     protected String[] legendLabels = { JMeterUtils.getResString("aggregate_graph_legend") }; // $NON-NLS-1$
     
     protected int maxYAxisScale;
 
     protected Font titleFont;
 
     protected Font legendFont;
 
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
 
     protected Font valueFont = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.6));
 
     protected Color[] color = { Color.YELLOW };
 
     protected Color foreColor = Color.BLACK;
 
     protected boolean outlinesBarFlag = false;
 
     protected boolean showGrouping = true;
     
     protected boolean valueOrientation = true;
 
     protected int legendPlacement = LegendAreaProperties.BOTTOM;
 
     /**
      *
      */
     public AxisGraph() {
         super();
     }
 
     /**
      * @param layout The {@link LayoutManager} to use
      */
     public AxisGraph(LayoutManager layout) {
         super(layout);
     }
 
     /**
      * @param layout The {@link LayoutManager} to use
      * @param isDoubleBuffered Flag whether double buffering should be used
      */
     public AxisGraph(LayoutManager layout, boolean isDoubleBuffered) {
         super(layout, isDoubleBuffered);
     }
 
     /**
      * Expects null array when no data  not empty array
      * @param data The data to be drawn
      */
     public void setData(double[][] data) {
         this.data = data;
     }
 
     public void setTitle(String title) {
         this.title = title;
     }
 
     public void setMaxLength(int maxLength) {
         this.maxLength = maxLength;
     }
 
     public void setXAxisTitle(String title) {
         this.xAxisTitle = title;
     }
 
     public void setYAxisTitle(String title) {
         this.yAxisTitle = title;
     }
 
     /**
      * Expects null array when no labels not empty array
      * @param labels The labels for the x axis
      */
     public void setXAxisLabels(String[] labels) {
         this.xAxisLabels = labels;
     }
 
     public void setYAxisLabels(String label) {
         this.yAxisLabel = label;
     }
 
     public void setLegendLabels(String[] labels) {
         this.legendLabels = labels;
     }
 
     public void setWidth(int w) {
         this.width = w;
     }
 
     public void setHeight(int h) {
         this.height = h;
     }
 
     /**
      * @return the maxYAxisScale
      */
     public int getMaxYAxisScale() {
         return maxYAxisScale;
     }
 
     /**
      * @param maxYAxisScale the maxYAxisScale to set
      */
     public void setMaxYAxisScale(int maxYAxisScale) {
         this.maxYAxisScale = maxYAxisScale;
     }
 
     /**
      * @return the color
      */
     public Color[] getColor() {
         return color;
     }
 
     /**
      * @param color the color to set
      */
     public void setColor(Color[] color) {
         this.color = color;
     }
 
     /**
      * @return the foreColor
      */
     public Color getForeColor() {
         return foreColor;
     }
 
     /**
      * @param foreColor the foreColor to set
      */
     public void setForeColor(Color foreColor) {
         this.foreColor = foreColor;
     }
 
     /**
      * @return the titleFont
      */
     public Font getTitleFont() {
         return titleFont;
     }
 
     /**
      * @param titleFont the titleFont to set
      */
     public void setTitleFont(Font titleFont) {
         this.titleFont = titleFont;
     }
 
     /**
      * @return the legendFont
      */
     public Font getLegendFont() {
         return legendFont;
     }
 
     /**
      * @param legendFont the legendFont to set
      */
     public void setLegendFont(Font legendFont) {
         this.legendFont = legendFont;
     }
 
     /**
      * @return the valueFont
      */
     public Font getValueFont() {
         return valueFont;
     }
 
     /**
      * @param valueFont the valueFont to set
      */
     public void setValueFont(Font valueFont) {
         this.valueFont = valueFont;
     }
 
     /**
      * @return the legendPlacement
      */
     public int getLegendPlacement() {
         return legendPlacement;
     }
 
     /**
      * @param legendPlacement the legendPlacement to set
      */
     public void setLegendPlacement(int legendPlacement) {
         this.legendPlacement = legendPlacement;
     }
 
     /**
      * @return the outlinesBarFlag
      */
     public boolean isOutlinesBarFlag() {
         return outlinesBarFlag;
     }
 
     /**
      * @param outlinesBarFlag the outlinesBarFlag to set
      */
     public void setOutlinesBarFlag(boolean outlinesBarFlag) {
         this.outlinesBarFlag = outlinesBarFlag;
     }
 
     /**
      * @return the valueOrientation
      */
     public boolean isValueOrientation() {
         return valueOrientation;
     }
 
     /**
      * @param valueOrientation the valueOrientation to set
      */
     public void setValueOrientation(boolean valueOrientation) {
         this.valueOrientation = valueOrientation;
     }
 
     /**
      * @return the showGrouping
      */
     public boolean isShowGrouping() {
         return showGrouping;
     }
 
     /**
      * @param showGrouping the showGrouping to set
      */
     public void setShowGrouping(boolean showGrouping) {
         this.showGrouping = showGrouping;
     }
 
     @Override
     public void paintComponent(Graphics graphics) {
         if (data != null && this.title != null && this.xAxisLabels != null &&
                 this.yAxisLabel != null &&
                 this.yAxisTitle != null) {
             drawSample(this.title, this.maxLength, this.xAxisLabels, 
                     this.yAxisTitle, this.legendLabels,
                     this.data, this.width, this.height, this.color,
                     this.legendFont, graphics);
         }
     }
 
     private double findMax(double[][] _data) {
         double max = _data[0][0];
         for (double[] dArray : _data) {
             for (double d : dArray) {
                 if (d > max) {
                     max = d;
                 }
             }
         }
         return max;
     }
 
     private String squeeze (String input, int _maxLength){
         if (input.length()>_maxLength){
             return input.substring(0,_maxLength-ELLIPSIS_LEN)+ELLIPSIS;
         }
         return input;
     }
 
     private void drawSample(String _title, int _maxLength, String[] _xAxisLabels,
             String _yAxisTitle, String[] _legendLabels, double[][] _data,
             int _width, int _height, Color[] _color,
             Font legendFont, Graphics g) {
         double max = maxYAxisScale > 0 ? maxYAxisScale : findMax(_data); // define max scale y axis
         try {
             /** These controls are already done in StatGraphVisualizer
             if (_width == 0) {
                 _width = 450;
             }
             if (_height == 0) {
                 _height = 250;
             }
             **/
             if (_maxLength < 3) {
                 _maxLength = 3;
             }
             // if the "Title of Graph" is empty, we can assume some default
             if (_title.length() == 0 ) {
                 _title = JMeterUtils.getResString("aggregate_graph_title"); //$NON-NLS-1$
             }
             // if the labels are too long, they'll be "squeezed" to make the chart viewable.
             for (int i = 0; i < _xAxisLabels.length; i++) {
                 String label = _xAxisLabels[i];
                 _xAxisLabels[i]=squeeze(label, _maxLength);
             }
             this.setPreferredSize(new Dimension(_width,_height));
             DataSeries dataSeries = new DataSeries( _xAxisLabels, null, _yAxisTitle, _title ); // replace _xAxisTitle to null (don't display x axis title)
 
             ClusteredBarChartProperties clusteredBarChartProperties= new ClusteredBarChartProperties();
             clusteredBarChartProperties.setShowOutlinesFlag(outlinesBarFlag);
             ValueLabelRenderer valueLabelRenderer = new ValueLabelRenderer(false, false, showGrouping, 0);
             valueLabelRenderer.setValueLabelPosition(ValueLabelPosition.AT_TOP);
 
             valueLabelRenderer.setValueChartFont(new ChartFont(valueFont, foreColor));
             valueLabelRenderer.useVerticalLabels(valueOrientation);
 
             clusteredBarChartProperties.addPostRenderEventListener(valueLabelRenderer);
 
             Paint[] paints = new Paint[_color.length];
             System.arraycopy(_color, 0, paints, 0, paints.length);
             
             AxisChartDataSet axisChartDataSet =
                 new AxisChartDataSet(
                         _data, _legendLabels, paints, ChartType.BAR_CLUSTERED, clusteredBarChartProperties );
             dataSeries.addIAxisPlotDataSet( axisChartDataSet );
 
             ChartProperties chartProperties= new ChartProperties();
             LabelAxisProperties xaxis = new LabelAxisProperties();
             DataAxisProperties yaxis = new DataAxisProperties();
             yaxis.setUseCommas(showGrouping);
 
             if (legendFont != null) {
                 yaxis.setAxisTitleChartFont(new ChartFont(legendFont, new Color(20)));
                 yaxis.setScaleChartFont(new ChartFont(legendFont, new Color(20)));
                 xaxis.setAxisTitleChartFont(new ChartFont(legendFont, new Color(20)));
                 xaxis.setScaleChartFont(new ChartFont(legendFont, new Color(20)));
             }
             if (titleFont != null) {
                 chartProperties.setTitleFont(new ChartFont(titleFont, new Color(0)));
             }
 
             // Y Axis
             try {
                 BigDecimal round = BigDecimal.valueOf(max / 1000d);
                 round = round.setScale(0, BigDecimal.ROUND_UP);
                 double topValue = round.doubleValue() * 1000;
                 yaxis.setUserDefinedScale(0, 500);
                 yaxis.setNumItems((int) (topValue / 500)+1);
                 yaxis.setShowGridLines(1);
             } catch (PropertyException e) {
-                log.warn("",e);
+                log.warn("Chart property exception occurred.", e);
             }
 
             AxisProperties axisProperties= new AxisProperties(xaxis, yaxis);
             axisProperties.setXAxisLabelsAreVertical(true);
             LegendProperties legendProperties= new LegendProperties();
             legendProperties.setBorderStroke(null);
             legendProperties.setPlacement(legendPlacement);
             legendProperties.setIconBorderPaint(Color.WHITE);
             if (legendPlacement == LegendAreaProperties.RIGHT || legendPlacement == LegendAreaProperties.LEFT) {
                 legendProperties.setNumColumns(1);
             }
             if (legendFont != null) {
                 legendProperties.setFont(legendFont); //new Font("SansSerif", Font.PLAIN, 10)
             }
             AxisChart axisChart = new AxisChart(
                     dataSeries, chartProperties, axisProperties,
                     legendProperties, _width, _height );
             axisChart.setGraphics2D((Graphics2D) g);
             axisChart.render();
         } catch (ChartDataException | PropertyException e) {
-            log.warn("",e);
+            log.warn("Exception occurred while rendering chart.", e);
         }
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/BSFListener.java b/src/components/org/apache/jmeter/visualizers/BSFListener.java
index ba4de7ed9..a1e6d5bcc 100644
--- a/src/components/org/apache/jmeter/visualizers/BSFListener.java
+++ b/src/components/org/apache/jmeter/visualizers/BSFListener.java
@@ -1,87 +1,87 @@
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
 
 import org.apache.bsf.BSFException;
 import org.apache.bsf.BSFManager;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.BSFTestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Needs to implement Visualizer so that TestBeanGUI can find the correct GUI class
  */
 public class BSFListener extends BSFTestElement
     implements Cloneable, SampleListener, TestBean, Visualizer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BSFListener.class);
 
     private static final long serialVersionUID = 234L;
 
     @Override
     public void sampleOccurred(SampleEvent event) {
         BSFManager mgr =null;
         try {
             mgr = getManager();
             if (mgr == null) {
                 log.error("Problem creating BSF manager");
                 return;
             }
             mgr.declareBean("sampleEvent", event, SampleEvent.class);
             SampleResult result = event.getResult();
             mgr.declareBean("sampleResult", result, SampleResult.class);
             processFileOrScript(mgr);
         } catch (BSFException e) {
-            log.warn("Problem in BSF script "+e);
+            log.warn("Problem in BSF script. {}", e.toString());
         } finally {
             if (mgr != null) {
                 mgr.terminate();
             }
         }
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
         // NOOP
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
         // NOOP
     }
 
     @Override
     public void add(SampleResult sample) {
         // NOOP
     }
 
     @Override
     public boolean isStats() {
         return false;
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/BeanShellListener.java b/src/components/org/apache/jmeter/visualizers/BeanShellListener.java
index 816843a82..41b870523 100644
--- a/src/components/org/apache/jmeter/visualizers/BeanShellListener.java
+++ b/src/components/org/apache/jmeter/visualizers/BeanShellListener.java
@@ -1,93 +1,93 @@
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
 
 import org.apache.jmeter.gui.UnsharedComponent;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellTestElement;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * We must implement Visualizer so that TestBeanGUI can find the correct GUI class
  *
  */
 public class BeanShellListener extends BeanShellTestElement
     implements Cloneable, SampleListener, TestBean, Visualizer, UnsharedComponent  {
     
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellListener.class);
 
     private static final long serialVersionUID = 4;
 
     // can be specified in jmeter.properties
     private static final String INIT_FILE = "beanshell.listener.init"; //$NON-NLS-1$
 
     @Override
     protected String getInitFileProperty() {
         return INIT_FILE;
     }
 
     @Override
     public void sampleOccurred(SampleEvent se) {
         final BeanShellInterpreter bshInterpreter = getBeanShellInterpreter();
         if (bshInterpreter == null) {
             log.error("BeanShell not found");
             return;
         }
 
         SampleResult samp=se.getResult();
         try {
             bshInterpreter.set("sampleEvent", se);//$NON-NLS-1$
             bshInterpreter.set("sampleResult", samp);//$NON-NLS-1$
             processFileOrScript(bshInterpreter);
         } catch (JMeterException e) {
-            log.warn("Problem in BeanShell script "+e);
+            log.warn("Problem in BeanShell script. {}", e.toString());
         }
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
         // NOOP
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
         // NOOP
     }
 
     @Override
     public void add(SampleResult sample) {
         // NOOP
     }
 
     @Override
     public boolean isStats() { // Needed by Visualizer interface
         return false;
     }
 
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/Graph.java b/src/components/org/apache/jmeter/visualizers/Graph.java
index dad2883ec..565a90ce8 100644
--- a/src/components/org/apache/jmeter/visualizers/Graph.java
+++ b/src/components/org/apache/jmeter/visualizers/Graph.java
@@ -1,261 +1,261 @@
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
 
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Graphics;
 import java.awt.Rectangle;
 import java.util.Iterator;
 import java.util.List;
 
 import javax.swing.JComponent;
 import javax.swing.Scrollable;
 import javax.swing.SwingUtilities;
 
 import org.apache.jmeter.gui.util.JMeterColor;
 import org.apache.jmeter.samplers.Clearable;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements a simple graph for displaying performance results.
  *
  */
 public class Graph extends JComponent implements Scrollable, Clearable {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Graph.class);
 
     private boolean wantData = true;
 
     private boolean wantAverage = true;
 
     private boolean wantDeviation = true;
 
     private boolean wantThroughput = true;
 
     private boolean wantMedian = true;
 
     private CachingStatCalculator model;
 
     private static final int WIDTH = 2000;
 
     private long graphMax = 1;
 
     private double throughputMax = 1;
 
     /**
      * Constructor for the Graph object.
      */
     public Graph() {
         this.setPreferredSize(new Dimension(WIDTH, 100));
     }
 
     /**
      * Constructor for the Graph object.
      * @param model The container for samples and statistics
      */
     public Graph(CachingStatCalculator model) {
         this();
         this.model = model;
     }
 
     /**
      * Gets the PreferredScrollableViewportSize attribute of the Graph object.
      *
      * @return the PreferredScrollableViewportSize value
      */
     @Override
     public Dimension getPreferredScrollableViewportSize() {
         return this.getPreferredSize();
     }
 
     /**
      * Gets the ScrollableUnitIncrement attribute of the Graph object.
      *
      * @return the ScrollableUnitIncrement value
      */
     @Override
     public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
         return 5;
     }
 
     /**
      * Gets the ScrollableBlockIncrement attribute of the Graph object.
      *
      * @return the ScrollableBlockIncrement value
      */
     @Override
     public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
         return (int) (visibleRect.width * .9);
     }
 
     /**
      * Gets the ScrollableTracksViewportWidth attribute of the Graph object.
      *
      * @return the ScrollableTracksViewportWidth value
      */
     @Override
     public boolean getScrollableTracksViewportWidth() {
         return false;
     }
 
     /**
      * Gets the ScrollableTracksViewportHeight attribute of the Graph object.
      *
      * @return the ScrollableTracksViewportHeight value
      */
     @Override
     public boolean getScrollableTracksViewportHeight() {
         return true;
     }
 
     /**
      * Clears this graph.
      */
     @Override
     public void clearData() {
         graphMax = 1;
         throughputMax = 1;
     }
 
     public void enableData(boolean value) {
         this.wantData = value;
     }
 
     public void enableAverage(boolean value) {
         this.wantAverage = value;
     }
 
     public void enableMedian(boolean value) {
         this.wantMedian = value;
     }
 
     public void enableDeviation(boolean value) {
         this.wantDeviation = value;
     }
 
     public void enableThroughput(boolean value) {
         this.wantThroughput = value;
     }
 
     public void updateGui(final Sample oneSample) {
         long h = model.getPercentPoint((float) 0.90).longValue();
         boolean repaint = false;
         if ((oneSample.getCount() % 20 == 0 || oneSample.getCount() < 20) && h > (graphMax * 1.2) || graphMax > (h * 1.2)) {
             if (h >= 1) {
                 graphMax = h;
             } else {
                 graphMax = 1;
             }
             repaint = true;
         }
         if (model.getMaxThroughput() > throughputMax) {
             throughputMax = model.getMaxThroughput() * 1.3;
             repaint = true;
         }
         if (repaint) {
             repaint();
             return;
         }
         final long xPos = model.getCount();
 
         SwingUtilities.invokeLater(new Runnable() {
             @Override
             public void run() {
                 Graphics g = getGraphics();
 
                 if (g != null) {
                     drawSample(xPos, oneSample, g);
                 }
             }
         });
     }
 
     /** {@inheritDoc}} */
     @Override
     public void paintComponent(Graphics g) {
         super.paintComponent(g);
 
         List<Sample> samples = model.getSamples();
         synchronized (samples ) {
             Iterator<Sample> e = samples.iterator();
 
             for (int i = 0; e.hasNext(); i++) {
                 Sample s = e.next();
 
                 drawSample(i, s, g);
             }
         }
     }
 
     private void drawSample(long x, Sample oneSample, Graphics g) {
         int height = getHeight();
-        log.debug("Drawing a sample at " + x);
+        log.debug("Drawing a sample at {}", x);
         int adjustedWidth = (int)(x % WIDTH); // will always be within range of an int: as must be < width
         if (wantData) {
             int data = (int) (oneSample.getData() * height / graphMax);
 
             if (oneSample.isSuccess()) {
                 g.setColor(Color.black);
             } else {
                 g.setColor(Color.YELLOW);
             }
             g.drawLine(adjustedWidth, height - data, adjustedWidth, height - data - 1);
             if (log.isDebugEnabled()) {
-                log.debug("Drawing coords = " + adjustedWidth + "," + (height - data));
+                log.debug("Drawing coords = {}, {}", adjustedWidth, (height - data));
             }
         }
 
         if (wantAverage) {
             int average = (int) (oneSample.getAverage() * height / graphMax);
 
             g.setColor(Color.blue);
             g.drawLine(adjustedWidth, height - average, adjustedWidth, height - average - 1);
         }
 
         if (wantMedian) {
             int median = (int) (oneSample.getMedian() * height / graphMax);
 
             g.setColor(JMeterColor.PURPLE);
             g.drawLine(adjustedWidth, height - median, adjustedWidth, height - median - 1);
         }
 
         if (wantDeviation) {
             int deviation = (int) (oneSample.getDeviation() * height / graphMax);
 
             g.setColor(Color.red);
             g.drawLine(adjustedWidth, height - deviation, adjustedWidth, height - deviation - 1);
         }
         if (wantThroughput) {
             int throughput = (int) (oneSample.getThroughput() * height / throughputMax);
 
             g.setColor(JMeterColor.DARK_GREEN);
             g.drawLine(adjustedWidth, height - throughput, adjustedWidth, height - throughput - 1);
         }
     }
 
     /**
      * @return Returns the graphMax.
      */
     public long getGraphMax() {
         return graphMax;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/JSR223Listener.java b/src/components/org/apache/jmeter/visualizers/JSR223Listener.java
index c86502f0e..f27c91dc9 100644
--- a/src/components/org/apache/jmeter/visualizers/JSR223Listener.java
+++ b/src/components/org/apache/jmeter/visualizers/JSR223Listener.java
@@ -1,83 +1,83 @@
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
 
 import java.io.IOException;
 
 import javax.script.Bindings;
 import javax.script.ScriptEngine;
 import javax.script.ScriptException;
 
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.util.JSR223TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Needs to implement Visualizer so that TestBeanGUI can find the correct GUI class
  *
  */
 public class JSR223Listener extends JSR223TestElement
     implements Cloneable, SampleListener, TestBean, Visualizer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JSR223Listener.class);
 
-    private static final long serialVersionUID = 234L;
+    private static final long serialVersionUID = 235L;
 
     @Override
     public void sampleOccurred(SampleEvent event) {
         try {
             ScriptEngine scriptEngine = getScriptEngine();
             Bindings bindings = scriptEngine.createBindings();
             bindings.put("sampleEvent", event);
             bindings.put("sampleResult", event.getResult());
             processFileOrScript(scriptEngine, bindings);
         } catch (ScriptException | IOException e) {
-            log.error("Problem in JSR223 script "+getName(), e);
+            log.error("Problem in JSR223 script, {}", getName(), e);
         }
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
         // NOOP
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
         // NOOP
     }
 
     @Override
     public void add(SampleResult sample) {
         // NOOP
     }
 
     @Override
     public boolean isStats() {
         return false;
     }
     
     @Override
     public Object clone() {
         return super.clone();
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/LineGraph.java b/src/components/org/apache/jmeter/visualizers/LineGraph.java
index 50576e04d..047a5b219 100644
--- a/src/components/org/apache/jmeter/visualizers/LineGraph.java
+++ b/src/components/org/apache/jmeter/visualizers/LineGraph.java
@@ -1,271 +1,271 @@
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
 
 import java.awt.BasicStroke;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Graphics;
 import java.awt.Graphics2D;
 import java.awt.LayoutManager;
 import java.awt.Paint;
 import java.awt.Shape;
 import java.awt.Stroke;
 
 import javax.swing.JPanel;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.jCharts.axisChart.AxisChart;
 import org.jCharts.chartData.AxisChartDataSet;
 import org.jCharts.chartData.DataSeries;
 import org.jCharts.properties.AxisProperties;
 import org.jCharts.properties.ChartProperties;
 import org.jCharts.properties.DataAxisProperties;
 import org.jCharts.properties.LegendProperties;
 import org.jCharts.properties.LineChartProperties;
 import org.jCharts.properties.PointChartProperties;
 import org.jCharts.types.ChartType;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  *
  * Axis graph is used by StatGraphVisualizer, which generates bar graphs
  * from the statistical data.
  */
 public class LineGraph extends JPanel {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LineGraph.class);
 
     protected double[][] data = null;
     protected String title;
     protected String xAxisTitle;
     protected String yAxisTitle;
     protected String[] xAxisLabels;
     protected String[] yAxisLabel;
     protected int width;
     protected int height;
 
     private static final Shape[] SHAPE_ARRAY = {PointChartProperties.SHAPE_CIRCLE,
             PointChartProperties.SHAPE_DIAMOND,PointChartProperties.SHAPE_SQUARE,
             PointChartProperties.SHAPE_TRIANGLE};
 
     /**
      * 12 basic colors for line graphs. If we need more colors than this,
      * we can add more. Though more than 12 lines per graph will look
      * rather busy and be hard to read.
      */
     private static final Paint[] PAINT_ARRAY = {Color.black,
             Color.blue,Color.green,Color.magenta,Color.orange,
             Color.red,Color.yellow,Color.darkGray,Color.gray,Color.lightGray,
             Color.pink,Color.cyan};
     protected int shape_counter = 0;
     protected int paint_counter = -1;
 
     /**
      *
      */
     public LineGraph() {
         super();
     }
 
     /**
      * @param layout The {@link LayoutManager} to be used
      */
     public LineGraph(LayoutManager layout) {
         super(layout);
     }
 
     /**
      * @param layout The {@link LayoutManager} to be used
      * @param isDoubleBuffered Flag whether double buffering should be used
      */
     public LineGraph(LayoutManager layout, boolean isDoubleBuffered) {
         super(layout, isDoubleBuffered);
     }
 
     public void setData(double[][] data) {
         this.data = data;
     }
 
     public void setTitle(String title) {
         this.title = title;
     }
 
     public void setXAxisTitle(String title) {
         this.xAxisTitle = title;
     }
 
     public void setYAxisTitle(String title) {
         this.yAxisTitle = title;
     }
 
     public void setXAxisLabels(String[] labels) {
         this.xAxisLabels = labels;
     }
 
     public void setYAxisLabels(String[] label) {
         this.yAxisLabel = label;
     }
 
     public void setWidth(int w) {
         this.width = w;
     }
 
     public void setHeight(int h) {
         this.height = h;
     }
 
     @Override
     public void paintComponent(Graphics g) {
         // reset the paint counter
         this.paint_counter = -1;
         if (data != null && this.title != null && this.xAxisLabels != null &&
                 this.xAxisTitle != null && this.yAxisLabel != null &&
                 this.yAxisTitle != null) {
             drawSample(this.title,this.xAxisLabels,this.xAxisTitle,
                     this.yAxisTitle,this.data,this.width,this.height,g);
         }
     }
 
     private void drawSample(String _title, String[] _xAxisLabels, String _xAxisTitle,
             String _yAxisTitle, double[][] _data, int _width, int _height, Graphics g) {
         try {
             if (_width == 0) {
                 _width = 450;
             }
             if (_height == 0) {
                 _height = 250;
             }
             this.setPreferredSize(new Dimension(_width,_height));
             DataSeries dataSeries = new DataSeries( _xAxisLabels, _xAxisTitle, _yAxisTitle, _title );
             String[] legendLabels= yAxisLabel;
             Paint[] paints = this.createPaint(_data.length);
             Shape[] shapes = createShapes(_data.length);
             Stroke[] lstrokes = createStrokes(_data.length);
             LineChartProperties lineChartProperties= new LineChartProperties(lstrokes,shapes);
             AxisChartDataSet axisChartDataSet= new AxisChartDataSet( _data,
                     legendLabels,
                     paints,
                     ChartType.LINE,
                     lineChartProperties );
             dataSeries.addIAxisPlotDataSet( axisChartDataSet );
 
             ChartProperties chartProperties = new ChartProperties();
             AxisProperties axisProperties = new AxisProperties();
             // show the grid lines, to turn it off, set it to zero
             axisProperties.getYAxisProperties().setShowGridLines(1);
             axisProperties.setXAxisLabelsAreVertical(true);
             // set the Y Axis to round
             DataAxisProperties daxp = (DataAxisProperties)axisProperties.getYAxisProperties();
             daxp.setRoundToNearest(1);
             LegendProperties legendProperties = new LegendProperties();
             AxisChart axisChart = new AxisChart(
                     dataSeries, chartProperties, axisProperties,
                     legendProperties, _width, _height );
             axisChart.setGraphics2D((Graphics2D) g);
             axisChart.render();
         } catch (Exception e) {
-            log.error(e.getMessage());
+            log.error("Error while rendering axis chart. {}", e.getMessage());
         }
     }
 
     /**
      * Since we only have 4 shapes, the method will start with the first shape
      * and keep cycling through the shapes in order.
      * 
      * @param count
      *            The number of shapes to be created
      * @return the first n shapes
      */
     public Shape[] createShapes(int count) {
         Shape[] shapes = new Shape[count];
         for (int idx=0; idx < count; idx++) {
             shapes[idx] = nextShape();
         }
         return shapes;
     }
 
     /**
      * Return the next shape
      * @return the next shape
      */
     public Shape nextShape() {
         this.shape_counter++;
         if (shape_counter >= (SHAPE_ARRAY.length - 1)) {
             shape_counter = 0;
         }
         return SHAPE_ARRAY[shape_counter];
     }
 
     /**
      * Create a given number of {@link Stroke}s
      * 
      * @param count
      *            The number of strokes to be created
      * @return the first <code>count</code> strokes
      */
     public Stroke[] createStrokes(int count) {
         Stroke[] str = new Stroke[count];
         for (int idx=0; idx < count; idx++) {
             str[idx] = nextStroke();
         }
         return str;
     }
 
     /**
      * method always return a new BasicStroke with 1.0f weight
      * @return a new BasicStroke with 1.0f weight
      */
     public Stroke nextStroke() {
         return new BasicStroke(1.0f);
     }
 
     /**
      * return an array of Paint with different colors. The current
      * implementation will cycle through 12 colors if a line graph has more than
      * 12 entries
      * 
      * @param count
      *            The number of {@link Paint}s to be created
      * @return an array of Paint with different colors
      */
     public Paint[] createPaint(int count) {
         Paint[] pts = new Paint[count];
         for (int idx=0; idx < count; idx++) {
             pts[idx] = nextPaint();
         }
         return pts;
     }
 
     /**
      * The method will return the next paint color in the PAINT_ARRAY.
      * Rather than return a random color, we want it to always go through
      * the same sequence. This way, the same charts will always use the
      * same color and make it easier to compare side by side.
      * @return the next paint color in the PAINT_ARRAY
      */
     public Paint nextPaint() {
         this.paint_counter++;
         if (this.paint_counter == (PAINT_ARRAY.length - 1)) {
             this.paint_counter = 0;
         }
         return PAINT_ARRAY[this.paint_counter];
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java b/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java
index ab907d9b8..1f5c2842b 100644
--- a/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/MailerVisualizer.java
@@ -1,443 +1,443 @@
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
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.mail.MessagingException;
 import javax.mail.internet.AddressException;
 import javax.swing.BorderFactory;
 import javax.swing.Box;
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
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
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
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MailerVisualizer.class);
 
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
 
     private JComboBox<String> authTypeCombo;
 
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
     @Override
     public synchronized void clearData() {
         if (getModel() != null) {
             MailerModel model = ((MailerResultCollector) getModel()).getMailerModel();
             model.clear();
             updateVisualizer(model);
         }
     }
 
     @Override
     public void add(final SampleResult res) {
         if (getModel() != null) {
             JMeterUtils.runSafe(false, new Runnable() {
                 @Override
                 public void run() {
                     MailerModel model = ((MailerResultCollector) getModel()).getMailerModel();
                     // method called by add is synchronized
                     model.add(res);//this is a different model from the one used by the result collector
                     updateVisualizer(model);                    
                 }
             });
         }
     }
 
     @Override
     public String toString() {
         return JMeterUtils.getResString("mailer_string"); // $NON-NLS-1$
     }
 
     /**
      * Initializes the GUI. Lays out components and adds them to the container.
      */
     private void initGui() {
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new VerticalPanel();
         Border margin = new EmptyBorder(5, 10, 5, 10);
         this.setBorder(margin);
 
         mainPanel.add(makeTitlePanel());
         
         JPanel attributePane = new VerticalPanel();
         attributePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("mailer_title_settings"))); // $NON-NLS-1$
         
         // Settings panes
         attributePane.add(createMailingSettings());      
         attributePane.add(createSmtpSettings());
         
         // Test mail button
         JPanel testerPanel = new JPanel(new BorderLayout());
         testerButton = new JButton(JMeterUtils.getResString("mailer_test_mail")); // $NON-NLS-1$
         testerButton.addActionListener(this);
         testerButton.setEnabled(true);
         testerPanel.add(testerButton, BorderLayout.EAST);
         attributePane.add(testerPanel);
         mainPanel.add(attributePane);
         mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
 
         // Failures count
         JPanel mailerPanel = new JPanel(new BorderLayout());
         mailerPanel.add(new JLabel(JMeterUtils.getResString("mailer_failures")), BorderLayout.WEST); // $NON-NLS-1$
         failureField = new JTextField(6);
         failureField.setEditable(false);
         mailerPanel.add(failureField, BorderLayout.CENTER);
         mainPanel.add(mailerPanel);
 
         this.add(mainPanel, BorderLayout.CENTER);
     }
     
     private JPanel createMailingSettings() {
         JPanel settingsPane = new JPanel(new BorderLayout());
         settingsPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("mailer_title_message"))); // $NON-NLS-1$
         
         JPanel headerPane = new JPanel(new BorderLayout());
         headerPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
         JPanel fromPane = new JPanel(new BorderLayout());
         fromPane.add(new JLabel(JMeterUtils.getResString("mailer_from")), BorderLayout.WEST); // $NON-NLS-1$
         fromField = new JTextField(25);
         fromField.setEditable(true);
         fromPane.add(fromField, BorderLayout.CENTER);
         fromPane.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
         headerPane.add(fromPane, BorderLayout.WEST);
         JPanel addressPane = new JPanel(new BorderLayout());
         addressPane.add(new JLabel(JMeterUtils.getResString("mailer_addressees")), BorderLayout.WEST); // $NON-NLS-1$
         addressField = new JTextField(10);
         addressField.setEditable(true);
         addressPane.add(addressField, BorderLayout.CENTER);
         headerPane.add(addressPane, BorderLayout.CENTER);
         
         JPanel successPane = new JPanel(new BorderLayout());
         successPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
         JPanel succesSubjectPane = new JPanel(new BorderLayout());
         succesSubjectPane.add(new JLabel(JMeterUtils.getResString("mailer_success_subject")), BorderLayout.WEST); // $NON-NLS-1$
         successSubjectField = new JTextField(10);
         successSubjectField.setEditable(true);
         succesSubjectPane.add(successSubjectField, BorderLayout.CENTER);
         succesSubjectPane.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
         successPane.add(succesSubjectPane, BorderLayout.CENTER);
         JPanel successLimitPane = new JPanel(new BorderLayout());
         successLimitPane.add(new JLabel(JMeterUtils.getResString("mailer_success_limit")), BorderLayout.WEST); // $NON-NLS-1$
         successLimitField = new JTextField("2", 5); // $NON-NLS-1$
         successLimitField.setEditable(true);
         successLimitPane.add(successLimitField, BorderLayout.CENTER);
         successPane.add(successLimitPane, BorderLayout.EAST);
         
         JPanel failurePane = new JPanel(new BorderLayout());
         failurePane.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
         JPanel failureSubjectPane = new JPanel(new BorderLayout());
         failureSubjectPane.add(new JLabel(JMeterUtils.getResString("mailer_failure_subject")), BorderLayout.WEST); // $NON-NLS-1$
         failureSubjectField = new JTextField(10);
         failureSubjectField.setEditable(true);
         failureSubjectPane.add(failureSubjectField, BorderLayout.CENTER);
         failureSubjectPane.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
         failurePane.add(failureSubjectPane, BorderLayout.CENTER);
         JPanel failureLimitPane = new JPanel(new BorderLayout());
         failureLimitPane.add(new JLabel(JMeterUtils.getResString("mailer_failure_limit")), BorderLayout.WEST); // $NON-NLS-1$
         failureLimitField = new JTextField("2", 5); // $NON-NLS-1$
         failureLimitField.setEditable(true);
         failureLimitPane.add(failureLimitField, BorderLayout.CENTER);
         failurePane.add(failureLimitPane, BorderLayout.EAST);
         
         settingsPane.add(headerPane, BorderLayout.NORTH);
         settingsPane.add(successPane, BorderLayout.CENTER);
         settingsPane.add(failurePane, BorderLayout.SOUTH);
         
         return settingsPane;
     }
 
     private JPanel createSmtpSettings() {
         JPanel settingsPane = new JPanel(new BorderLayout());
         settingsPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("mailer_title_smtpserver"))); // $NON-NLS-1$
         
         JPanel hostPane = new JPanel(new BorderLayout());
         hostPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
         JPanel smtpHostPane = new JPanel(new BorderLayout());
         smtpHostPane.add(new JLabel(JMeterUtils.getResString("mailer_host")), BorderLayout.WEST); // $NON-NLS-1$
         smtpHostField = new JTextField(10);
         smtpHostField.setEditable(true);
         smtpHostPane.add(smtpHostField, BorderLayout.CENTER);
         smtpHostPane.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
         hostPane.add(smtpHostPane, BorderLayout.CENTER);
         JPanel smtpPortPane = new JPanel(new BorderLayout());
         smtpPortPane.add(new JLabel(JMeterUtils.getResString("mailer_port")), BorderLayout.WEST); // $NON-NLS-1$
         smtpPortField = new JTextField(10);
         smtpPortField.setEditable(true);
         smtpPortPane.add(smtpPortField, BorderLayout.CENTER);
         hostPane.add(smtpPortPane, BorderLayout.EAST);
 
         JPanel authPane = new JPanel(new BorderLayout());
         hostPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
         JPanel smtpLoginPane = new JPanel(new BorderLayout());
         smtpLoginPane.add(new JLabel(JMeterUtils.getResString("mailer_login")), BorderLayout.WEST); // $NON-NLS-1$
         smtpLoginField = new JTextField(10);
         smtpLoginField.setEditable(true);
         smtpLoginPane.add(smtpLoginField, BorderLayout.CENTER);
         smtpLoginPane.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
         authPane.add(smtpLoginPane, BorderLayout.CENTER);
         JPanel smtpPasswordPane = new JPanel(new BorderLayout());
         smtpPasswordPane.add(new JLabel(JMeterUtils.getResString("mailer_password")), BorderLayout.WEST); // $NON-NLS-1$
         smtpPasswordField = new JPasswordField(10);
         smtpPasswordField.setEditable(true);
         smtpPasswordPane.add(smtpPasswordField, BorderLayout.CENTER);
         smtpPasswordPane.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
         authPane.add(smtpPasswordPane, BorderLayout.EAST);
 
         JPanel authTypePane = new JPanel(new BorderLayout());
         authTypePane.add(new JLabel(JMeterUtils.getResString("mailer_connection_security")), BorderLayout.WEST); // $NON-NLS-1$
         authTypeCombo = new JComboBox<>(new String[] { 
                 MailerModel.MailAuthType.NONE.toString(), 
                 MailerModel.MailAuthType.SSL.toString(),
                 MailerModel.MailAuthType.TLS.toString()});
         authTypeCombo.setFont(new Font("SansSerif", Font.PLAIN, 10)); // $NON-NLS-1$
         authTypePane.add(authTypeCombo, BorderLayout.CENTER);
         
         JPanel credPane = new JPanel(new BorderLayout());
         credPane.add(authPane, BorderLayout.CENTER);
         credPane.add(authTypePane, BorderLayout.EAST);
         
         settingsPane.add(hostPane, BorderLayout.NORTH);
         settingsPane.add(credPane, BorderLayout.CENTER);
         
         return settingsPane;
     }
 
     @Override
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
     @Override
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
         int type = isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
         JOptionPane.showMessageDialog(null, message, isError ? 
                 JMeterUtils.getResString("mailer_msg_title_error") :  // $NON-NLS-1$
                     JMeterUtils.getResString("mailer_msg_title_information"), type); // $NON-NLS-1$
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
diff --git a/src/components/org/apache/jmeter/visualizers/RenderAsDocument.java b/src/components/org/apache/jmeter/visualizers/RenderAsDocument.java
index 87a490671..3c3bec65f 100644
--- a/src/components/org/apache/jmeter/visualizers/RenderAsDocument.java
+++ b/src/components/org/apache/jmeter/visualizers/RenderAsDocument.java
@@ -1,58 +1,58 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.Document;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class RenderAsDocument extends SamplerResultTab implements ResultRenderer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsDocument.class);
     
     /** {@inheritDoc} */
     @Override
     public void renderResult(SampleResult sampleResult) {
         try {
             showDocumentResponse(sampleResult);
         } catch (Exception e) {
             results.setText(e.toString());
-            log.error("Error:", e); // $NON-NLS-1$
+            log.error("Error while rendering document.", e); // $NON-NLS-1$
         }
     }
 
     private void showDocumentResponse(SampleResult sampleResult) {
         String response = Document.getTextFromDocument(sampleResult.getResponseData());
 
         results.setContentType("text/plain"); // $NON-NLS-1$
         results.setText(response);
         results.setCaretPosition(0);
         resultsScrollPane.setViewportView(results);
     }
 
     /** {@inheritDoc} */
     @Override
     public String toString() {
         return JMeterUtils.getResString("view_results_render_document"); // $NON-NLS-1$
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/RenderAsHTML.java b/src/components/org/apache/jmeter/visualizers/RenderAsHTML.java
index dca87eafc..1d0f3ce7d 100644
--- a/src/components/org/apache/jmeter/visualizers/RenderAsHTML.java
+++ b/src/components/org/apache/jmeter/visualizers/RenderAsHTML.java
@@ -1,159 +1,159 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import javax.swing.JEditorPane;
 import javax.swing.text.ComponentView;
 import javax.swing.text.Document;
 import javax.swing.text.EditorKit;
 import javax.swing.text.Element;
 import javax.swing.text.StyleConstants;
 import javax.swing.text.View;
 import javax.swing.text.ViewFactory;
 import javax.swing.text.html.HTML;
 import javax.swing.text.html.HTMLEditorKit;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class RenderAsHTML extends SamplerResultTab implements ResultRenderer {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsHTML.class);
 
     private static final String TEXT_HTML = "text/html"; // $NON-NLS-1$
 
     // Keep copies of the two editors needed
     private static final EditorKit customisedEditor = new LocalHTMLEditorKit();
 
     private static final EditorKit defaultHtmlEditor = JEditorPane.createEditorKitForContentType(TEXT_HTML);
 
     /** {@inheritDoc} */
     @Override
     public void renderResult(SampleResult sampleResult) {
         // get the text response and image icon
         // to determine which is NOT null
         String response = ViewResultsFullVisualizer.getResponseAsString(sampleResult);
         showRenderedResponse(response, sampleResult);
     }
 
     protected void showRenderedResponse(String response, SampleResult res) {
         showRenderedResponse(response, res, false);
     }
 
     protected void showRenderedResponse(String response, SampleResult res, boolean embedded) {
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
         results.setEditorKitForContentType(TEXT_HTML, embedded ? defaultHtmlEditor : customisedEditor);
 
         results.setContentType(TEXT_HTML);
 
         if (embedded) {
             // Allow JMeter to render frames (and relative images)
             // Must be done after setContentType [Why?]
             results.getDocument().putProperty(Document.StreamDescriptionProperty, res.getURL());
         }
         /*
          * Get round problems parsing <META http-equiv='content-type'
          * content='text/html; charset=utf-8'> See
          * <a href="http://bz.apache.org/bugzilla/show_bug.cgi?id=23315">Bug 23315</a>
          *
          * Is this due to a bug in Java?
          */
         results.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE); // $NON-NLS-1$
 
         try {
             results.setText(html); // Bug can generate RTE
         } catch (RuntimeException rte) {
             results.setText("Failed to parse HTML: " + rte.getMessage());
         }
         results.setCaretPosition(0);
         try {
             resultsScrollPane.setViewportView(results);
         } catch (NumberFormatException e) {
             // Java Bug : http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=9001188.
             // See https://bz.apache.org/bugzilla/show_bug.cgi?id=54586
             log.warn("An error occured rendering html code", e);
             results.setText("Failed to render HTML: " + e.getMessage() +", use Text renderer");            
         }
     }
 
     private static class LocalHTMLEditorKit extends HTMLEditorKit {
 
         private static final long serialVersionUID = -3399554318202905392L;
 
         private static final ViewFactory defaultFactory = new LocalHTMLFactory();
 
         @Override
         public ViewFactory getViewFactory() {
             return defaultFactory;
         }
 
         private static class LocalHTMLFactory extends javax.swing.text.html.HTMLEditorKit.HTMLFactory {
             /*
              * Provide dummy implementations to suppress download and display of
              * related resources: - FRAMEs - IMAGEs TODO create better dummy
              * displays TODO suppress LINK somehow
              */
             @Override
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
 
     /** {@inheritDoc} */
     @Override
     public String toString() {
         return JMeterUtils.getResString("view_results_render_html"); // $NON-NLS-1$
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/RenderAsXML.java b/src/components/org/apache/jmeter/visualizers/RenderAsXML.java
index 8e0c674c6..1d2f7e0b4 100644
--- a/src/components/org/apache/jmeter/visualizers/RenderAsXML.java
+++ b/src/components/org/apache/jmeter/visualizers/RenderAsXML.java
@@ -1,232 +1,232 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.Component;
 import java.awt.GridLayout;
 import java.io.ByteArrayInputStream;
 import java.io.StringWriter;
 
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTree;
 import javax.swing.ToolTipManager;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.TreeSelectionModel;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.XPathUtil;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 import org.xml.sax.SAXException;
 
 public class RenderAsXML extends SamplerResultTab
     implements ResultRenderer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsXML.class);
 
     private static final byte[] XML_PFX = {'<','?','x','m','l',' '};//"<?xml "
 
     public RenderAsXML(){
         activateSearchExtension = false; // TODO work out how to search the XML pane
     }
 
     /** {@inheritDoc} */
     @Override
     public void renderResult(SampleResult sampleResult) {
         showRenderXMLResponse(sampleResult);
     }
 
     private void showRenderXMLResponse(SampleResult res) {
         results.setContentType("text/xml"); // $NON-NLS-1$
         results.setCaretPosition(0);
         byte[] source = res.getResponseData();
         final ByteArrayInputStream baIS = new ByteArrayInputStream(source);
         for(int i=0; i<source.length-XML_PFX.length; i++){
             if (JOrphanUtils.startsWith(source, XML_PFX, i)){
                 baIS.skip(i);// NOSONAR Skip the leading bytes (if any)
                 break;
             }
         }
 
         StringWriter sw = new StringWriter();
         Tidy tidy = XPathUtil.makeTidyParser(true, true, true, sw);
         org.w3c.dom.Document document = tidy.parseDOM(baIS, null);
         document.normalize();
         if (tidy.getParseErrors() > 0) {
             showErrorMessageDialog(sw.toString(),
                     "Tidy: " + tidy.getParseErrors() + " errors, " + tidy.getParseWarnings() + " warnings",
                     JOptionPane.WARNING_MESSAGE);
         }
 
         JPanel domTreePanel = new DOMTreePanel(document);
         resultsScrollPane.setViewportView(domTreePanel);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.SamplerResultTab#clearData()
      */
     @Override
     public void clearData() {
         super.clearData();
         resultsScrollPane.setViewportView(null); // clear result tab on Ctrl-E
     }
 
     /*
      *
      * A Dom tree panel for to display response as tree view author <a
      * href="mailto:d.maung@mdl.com">Dave Maung</a> 
      * TODO implement to find any nodes in the tree using TreePath.
      *
      */
     private static class DOMTreePanel extends JPanel {
 
         private static final long serialVersionUID = 6871690021183779153L;
 
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
             } catch (SAXException e) {
                 log.warn("Error trying to parse document", e);
             }
 
         }
 
         /**
          * Skip all DTD nodes, all prolog nodes. They are not supported in tree view
          * We let user insert them however in DOMTreeView, we don't display them.
          *
          * @param parent {@link Node}
          * @return
          */
         private Node getFirstElement(Node parent) {
             NodeList childNodes = parent.getChildNodes();
             Node toReturn = parent; // Must return a valid node, or may generate an NPE
             for (int i = 0; i < childNodes.getLength(); i++) {
                 Node childNode = childNodes.item(i);
                 toReturn = childNode;
                 if (childNode.getNodeType() == Node.ELEMENT_NODE){
                     break;
                 }
 
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
 
             private static final long serialVersionUID = 240210061375790195L;
 
             @Override
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
                 StringBuilder strBuf = new StringBuilder("<html><body bgcolor=\"yellow\"><b>"); // $NON-NLS-1$
                 char[] chars = str.toCharArray();
                 for (int i = 0; i < chars.length; i++) {
 
                     if (i % maxChar == 0 && i != 0) {
                         strBuf.append(separator);
                     }
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
                 default:
                     // ignored
                     break;
 
                 }
                 return toReturn;
             }
         }
     }
 
     private static void showErrorMessageDialog(String message, String title, int messageType) {
         JOptionPane.showMessageDialog(null, message, title, messageType);
     }
 
     /** {@inheritDoc} */
     @Override
     public String toString() {
         return JMeterUtils.getResString("view_results_render_xml"); // $NON-NLS-1$
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/RenderAsXPath.java b/src/components/org/apache/jmeter/visualizers/RenderAsXPath.java
index fc17dd931..529b27d15 100644
--- a/src/components/org/apache/jmeter/visualizers/RenderAsXPath.java
+++ b/src/components/org/apache/jmeter/visualizers/RenderAsXPath.java
@@ -1,303 +1,303 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements. See the NOTICE file distributed with this
  * work for additional information regarding copyright ownership. The ASF
  * licenses this file to You under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  * http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.List;
 
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextArea;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.apache.commons.lang3.exception.ExceptionUtils;
 import org.apache.jmeter.assertions.gui.XMLConfPanel;
 import org.apache.jmeter.extractor.XPathExtractor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.TidyException;
 import org.apache.jmeter.util.XPathUtil;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.dom.Document;
 import org.xml.sax.SAXException;
 
 
 /**
  * Implement ResultsRender for XPath tester
  */
 public class RenderAsXPath implements ResultRenderer, ActionListener {
 
-    private static final Logger logger = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RenderAsXPath.class);
 
     private static final String XPATH_TESTER_COMMAND = "xpath_tester"; // $NON-NLS-1$
 
     private JPanel xmlWithXPathPane;
 
     private JTextArea xmlDataField;
 
     private JLabeledTextField xpathExpressionField;
 
     private JTextArea xpathResultField;
 
     private JTabbedPane rightSide;
 
     private SampleResult sampleResult = null;
 
     private JScrollPane xmlDataPane;
     
     // Should we return fragment as text, rather than text of fragment?
     private final JCheckBox getFragment =
         new JCheckBox(JMeterUtils.getResString("xpath_tester_fragment"));//$NON-NLS-1$
 
     private final XMLConfPanel xmlConfPanel = new XMLConfPanel();
 
     /** {@inheritDoc} */
     @Override
     public void clearData() {
         this.xmlDataField.setText(""); // $NON-NLS-1$
         // don't set empty to keep xpath
         // xpathExpressionField.setText(""); // $NON-NLS-1$
         this.xpathResultField.setText(""); // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public void init() {
         // Create the panels for the xpath tab
         xmlWithXPathPane = createXpathExtractorPanel();
     }
 
     /**
      * Display the response as text or as rendered HTML. Change the text on the
      * button appropriate to the current display.
      *
      * @param e the ActionEvent being processed
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         String command = e.getActionCommand();
         if ((sampleResult != null) && (XPATH_TESTER_COMMAND.equals(command))) {
             String response = xmlDataField.getText();
             XPathExtractor extractor = new XPathExtractor();
             xmlConfPanel.modifyTestElement(extractor);
             extractor.setFragment(getFragment.isSelected());
             executeAndShowXPathTester(response, extractor);
         }
     }
 
     /**
      * Launch xpath engine to parse a input text
      * @param textToParse
      */
     private void executeAndShowXPathTester(String textToParse, XPathExtractor extractor) {
         if (textToParse != null && textToParse.length() > 0
                 && this.xpathExpressionField.getText().length() > 0) {
             this.xpathResultField.setText(process(textToParse, extractor));
             this.xpathResultField.setCaretPosition(0); // go to first line
         }
     }
 
     private String process(String textToParse, XPathExtractor extractor) {
         try {
             Document doc = parseResponse(textToParse, extractor);
             List<String> matchStrings = new ArrayList<>();
             XPathUtil.putValuesForXPathInList(doc, xpathExpressionField.getText(),
                     matchStrings, extractor.getFragment());
             StringBuilder builder = new StringBuilder();
             int nbFound = matchStrings.size();
             builder.append("Match count: ").append(nbFound).append("\n");
             for (int i = 0; i < nbFound; i++) {
                 builder.append("Match[").append(i+1).append("]=").append(matchStrings.get(i)).append("\n");
             }                
             return builder.toString();
         } catch (Exception e) {
             return "Exception:"+ ExceptionUtils.getStackTrace(e);
         }
     }
     
     /*================= internal business =================*/
     /**
      * Converts (X)HTML response to DOM object Tree.
      * This version cares of charset of response.
      * @param unicodeData
      * @param extractor
      * @return Document
      *
      */
     private Document parseResponse(String unicodeData, XPathExtractor extractor)
       throws IOException, ParserConfigurationException,SAXException,TidyException
     {
       //TODO: validate contentType for reasonable types?
 
       // NOTE: responseData encoding is server specific
       //       Therefore we do byte -> unicode -> byte conversion
       //       to ensure UTF-8 encoding as required by XPathUtil
       // convert unicode String -> UTF-8 bytes
       byte[] utf8data = unicodeData.getBytes(StandardCharsets.UTF_8);
       ByteArrayInputStream in = new ByteArrayInputStream(utf8data);
       boolean isXML = JOrphanUtils.isXML(utf8data);
       // this method assumes UTF-8 input data
       return XPathUtil.makeDocument(in,false,false,extractor.useNameSpace(),
               extractor.isTolerant(),extractor.isQuiet(),extractor.showWarnings(),
               extractor.reportErrors(),isXML, extractor.isDownloadDTDs());
     }
 
 
     /** {@inheritDoc} */
     @Override
     public void renderResult(SampleResult sampleResult) {
         String response = ViewResultsFullVisualizer.getResponseAsString(sampleResult);
         try {
             xmlDataField.setText(response == null ? "" : response);
             xmlDataField.setCaretPosition(0);
         } catch (Exception e) {
-            logger.error("Exception converting to XML:"+response+ ", message:"+e.getMessage(),e);
+            log.error("Exception converting to XML: {}, message: {}", response, e.getMessage(), e);
             xmlDataField.setText("Exception converting to XML:"+response+ ", message:"+e.getMessage());
             xmlDataField.setCaretPosition(0);
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public String toString() {
         return JMeterUtils.getResString("xpath_tester"); // $NON-NLS-1$
     }
 
 
     /** {@inheritDoc} */
     @Override
     public void setupTabPane() {
          // Add xpath tester pane
         if (rightSide.indexOfTab(JMeterUtils.getResString("xpath_tester_title")) < 0) { // $NON-NLS-1$
             rightSide.addTab(JMeterUtils.getResString("xpath_tester_title"), xmlWithXPathPane); // $NON-NLS-1$
         }
         clearData();
     }
 
     /**
      * @return XPath Tester panel
      */
     private JPanel createXpathExtractorPanel() {
         
         xmlDataField = new JTextArea();
         xmlDataField.setEditable(false);
         xmlDataField.setLineWrap(true);
         xmlDataField.setWrapStyleWord(true);
 
         this.xmlDataPane = GuiUtils.makeScrollPane(xmlDataField);
         xmlDataPane.setPreferredSize(new Dimension(0, 200));
 
         JPanel pane = new JPanel(new BorderLayout(0, 5));
 
         JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 xmlDataPane, createXpathExtractorTasksPanel());
         mainSplit.setDividerLocation(0.6d);
         mainSplit.setOneTouchExpandable(true);
         pane.add(mainSplit, BorderLayout.CENTER);
         return pane;
     }
 
     /**
      * Create the XPath task pane
      *
      * @return XPath task pane
      */
     private JPanel createXpathExtractorTasksPanel() {
         Box xpathActionPanel = Box.createVerticalBox();
         
         Box selectorAndButton = Box.createHorizontalBox();
 
         Border margin = new EmptyBorder(5, 5, 0, 5);
         xpathActionPanel.setBorder(margin);
         xpathExpressionField = new JLabeledTextField(JMeterUtils.getResString("xpath_tester_field")); // $NON-NLS-1$
         
         JButton xpathTester = new JButton(JMeterUtils.getResString("xpath_tester_button_test")); // $NON-NLS-1$
         xpathTester.setActionCommand(XPATH_TESTER_COMMAND);
         xpathTester.addActionListener(this);
         
         selectorAndButton.add(xpathExpressionField);
         selectorAndButton.add(xpathTester);
         
         xpathActionPanel.add(selectorAndButton);
         xpathActionPanel.add(xmlConfPanel);
         xpathActionPanel.add(getFragment);
         
         xpathResultField = new JTextArea();
         xpathResultField.setEditable(false);
         xpathResultField.setLineWrap(true);
         xpathResultField.setWrapStyleWord(true);
 
         JPanel xpathTasksPanel = new JPanel(new BorderLayout(0, 5));
         xpathTasksPanel.add(xpathActionPanel, BorderLayout.NORTH);
         xpathTasksPanel.add(GuiUtils.makeScrollPane(xpathResultField), BorderLayout.CENTER);
 
         return xpathTasksPanel;
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setRightSide(JTabbedPane side) {
         rightSide = side;
     }
 
     /** {@inheritDoc} */
     @Override
     public synchronized void setSamplerResult(Object userObject) {
         if (userObject instanceof SampleResult) {
             sampleResult = (SampleResult) userObject;
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void setLastSelectedTab(int index) {
         // nothing to do
     }
 
     /** {@inheritDoc} */
     @Override
     public void renderImage(SampleResult sampleResult) {
         clearData();
         xmlDataField.setText(JMeterUtils.getResString("xpath_tester_no_text")); // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public void setBackgroundColor(Color backGround) {
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/RequestPanel.java b/src/components/org/apache/jmeter/visualizers/RequestPanel.java
index 8bae3fbe5..408220767 100644
--- a/src/components/org/apache/jmeter/visualizers/RequestPanel.java
+++ b/src/components/org/apache/jmeter/visualizers/RequestPanel.java
@@ -1,120 +1,120 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements. See the NOTICE file distributed with this
  * work for additional information regarding copyright ownership. The ASF
  * licenses this file to You under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  * http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.io.IOException;
 import java.util.Collections;
 import java.util.LinkedList;
 import java.util.List;
 
 import javax.swing.JPanel;
 import javax.swing.JTabbedPane;
 import javax.swing.SwingConstants;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Manipulate all classes which implements request view panel interface
  * and return a super panel with a bottom tab list of this classes
  *
  */
 public class RequestPanel {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RequestPanel.class);
 
     private final LinkedList<RequestView> listRequestView;
 
     private final JPanel panel;
 
     /**
      * Find and instanciate all class that extend RequestView
      * and Create Request Panel
      */
     public RequestPanel() {
         listRequestView = new LinkedList<>();
         List<String> classesToAdd = Collections.<String> emptyList();
         try {
             classesToAdd = JMeterUtils.findClassesThatExtend(RequestView.class);
         } catch (IOException e1) {
             // ignored
         }
         String rawTab = JMeterUtils.getResString(RequestViewRaw.KEY_LABEL); // $NON-NLS-1$
         Object rawObject = null;
         for (String clazz : classesToAdd) {
             try {
                 // Instantiate requestview classes
                 final RequestView requestView = (RequestView) Class.forName(clazz).newInstance();
                 if (rawTab.equals(requestView.getLabel())) {
                     rawObject = requestView; // use later
                 } else {
                     listRequestView.add(requestView);
                 }
             } catch (Exception e) {
-                log.warn("Error in load result render:" + clazz, e); // $NON-NLS-1$
+                log.warn("Error in load result render: {}", clazz, e); // $NON-NLS-1$
             }
         }
         // place raw tab in first position (first tab)
         if (rawObject != null) {
             listRequestView.addFirst((RequestView) rawObject);
         }
         
         // Prepare the Request tabbed pane
         JTabbedPane tabbedRequest = new JTabbedPane(SwingConstants.BOTTOM);
         for (RequestView requestView : listRequestView) {
             requestView.init();
             tabbedRequest.addTab(requestView.getLabel(), requestView.getPanel());
         }
         
         // Hint to background color on bottom tabs (grey, not blue)
         panel = new JPanel(new BorderLayout());
         panel.add(tabbedRequest);
     }
 
     /**
      * Clear data in all request view
      */
     public void clearData() {
         for (RequestView requestView : listRequestView) {
             requestView.clearData();
         }
     }
 
     /**
      * Put SamplerResult in all request view
      * 
      * @param samplerResult The {@link SampleResult} to be put in all {@link RequestView}s
      */
     public void setSamplerResult(SampleResult samplerResult) {
         for (RequestView requestView : listRequestView) {
             requestView.setSamplerResult(samplerResult);
         }
     }
     
     /**
      * @return a tabbed panel for view request
      */
     public JPanel getPanel() {
         return panel;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/RespTimeGraphChart.java b/src/components/org/apache/jmeter/visualizers/RespTimeGraphChart.java
index b5ff2d0b7..557a7d38e 100644
--- a/src/components/org/apache/jmeter/visualizers/RespTimeGraphChart.java
+++ b/src/components/org/apache/jmeter/visualizers/RespTimeGraphChart.java
@@ -1,414 +1,414 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BasicStroke;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Font;
 import java.awt.Graphics;
 import java.awt.Graphics2D;
 import java.awt.LayoutManager;
 import java.awt.Paint;
 import java.awt.Shape;
 import java.awt.Stroke;
 import java.math.BigDecimal;
 
 import javax.swing.JPanel;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.jCharts.axisChart.AxisChart;
 import org.jCharts.chartData.AxisChartDataSet;
 import org.jCharts.chartData.ChartDataException;
 import org.jCharts.chartData.DataSeries;
 import org.jCharts.properties.AxisProperties;
 import org.jCharts.properties.ChartProperties;
 import org.jCharts.properties.DataAxisProperties;
 import org.jCharts.properties.LabelAxisProperties;
 import org.jCharts.properties.LegendAreaProperties;
 import org.jCharts.properties.LegendProperties;
 import org.jCharts.properties.LineChartProperties;
 import org.jCharts.properties.PointChartProperties;
 import org.jCharts.properties.PropertyException;
 import org.jCharts.properties.util.ChartFont;
 import org.jCharts.types.ChartType;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class RespTimeGraphChart extends JPanel {
 
-    private static final long serialVersionUID = 280L;
+    private static final long serialVersionUID = 281L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RespTimeGraphChart.class);
 
     protected double[][] data;
     
     protected String title;
     
     protected String xAxisTitle;
     
     protected String yAxisTitle;
     
     protected String yAxisLabel;
     
     protected String[] xAxisLabels;
     
     protected int width;
     
     protected int height;
 
     protected int incrYAxisScale;
 
     protected String[] legendLabels = { JMeterUtils.getResString("aggregate_graph_legend") }; // $NON-NLS-1$
 
     protected int maxYAxisScale;
 
     protected Font titleFont;
 
     protected Font legendFont;
 
     protected Color[] color;
 
     protected boolean showGrouping = true;
 
     protected int legendPlacement = LegendAreaProperties.BOTTOM;
 
     protected Shape pointShape = PointChartProperties.SHAPE_CIRCLE;
 
     protected float strokeWidth = 3.5f;
 
     /**
     * Constructor
     */
    public RespTimeGraphChart() {
        super();
    }
 
     /**
      * Constructor
      * 
      * @param layout
      *            The {@link LayoutManager} to be used
      */
    public RespTimeGraphChart(LayoutManager layout) {
        super(layout);
    }
 
     /**
      * Constructor
      * 
      * @param layout
      *            The {@link LayoutManager} to be used
      * @param isDoubleBuffered
      *            Flag whether double buffering should be used
      */
    public RespTimeGraphChart(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }
 
     public void setData(double[][] data) {
         this.data = data;
     }
 
     public void setTitle(String title) {
         this.title = title;
     }
 
     public void setXAxisTitle(String title) {
         this.xAxisTitle = title;
     }
 
     public void setYAxisTitle(String title) {
         this.yAxisTitle = title;
     }
 
     public void setXAxisLabels(String[] labels) {
         this.xAxisLabels = labels;
     }
 
     public void setYAxisLabels(String label) {
         this.yAxisLabel = label;
     }
 
     public void setLegendLabels(String[] labels) {
         this.legendLabels = labels;
     }
 
     public void setWidth(int w) {
         this.width = w;
     }
 
     public void setHeight(int h) {
         this.height = h;
     }
 
     /**
      * @param incrYAxisScale the incrYAxisScale to set
      */
     public void setIncrYAxisScale(int incrYAxisScale) {
         this.incrYAxisScale = incrYAxisScale;
     }
 
     /**
      * @return the maxYAxisScale
      */
     public int getMaxYAxisScale() {
         return maxYAxisScale;
     }
 
     /**
      * @param maxYAxisScale the maxYAxisScale to set
      */
     public void setMaxYAxisScale(int maxYAxisScale) {
         this.maxYAxisScale = maxYAxisScale;
     }
 
     /**
      * @return the color
      */
     public Color[] getColor() {
         return color;
     }
 
     /**
      * @param color the color to set
      */
     public void setColor(Color[] color) {
         this.color = color;
     }
 
     /**
      * @return the titleFont
      */
     public Font getTitleFont() {
         return titleFont;
     }
 
     /**
      * @param titleFont the titleFont to set
      */
     public void setTitleFont(Font titleFont) {
         this.titleFont = titleFont;
     }
 
     /**
      * @return the legendFont
      */
     public Font getLegendFont() {
         return legendFont;
     }
 
     /**
      * @param legendFont the legendFont to set
      */
     public void setLegendFont(Font legendFont) {
         this.legendFont = legendFont;
     }
 
     /**
      * @return the legendPlacement
      */
     public int getLegendPlacement() {
         return legendPlacement;
     }
 
     /**
      * @param legendPlacement the legendPlacement to set
      */
     public void setLegendPlacement(int legendPlacement) {
         this.legendPlacement = legendPlacement;
     }
 
     /**
      * @return the pointShape
      */
     public Shape getPointShape() {
         return pointShape;
     }
 
     /**
      * @param pointShape the pointShape to set
      */
     public void setPointShape(Shape pointShape) {
         this.pointShape = pointShape;
     }
 
     /**
      * @return the strokeWidth
      */
     public float getStrokeWidth() {
         return strokeWidth;
     }
 
     /**
      * @param strokeWidth the strokeWidth to set
      */
     public void setStrokeWidth(float strokeWidth) {
         this.strokeWidth = strokeWidth;
     }
 
     /**
      * @return the showGrouping
      */
     public boolean isShowGrouping() {
         return showGrouping;
     }
 
     /**
      * @param showGrouping the showGrouping to set
      */
     public void setShowGrouping(boolean showGrouping) {
         this.showGrouping = showGrouping;
     }
 
     private void drawSample(String _title, String[] _xAxisLabels,
             String _yAxisTitle, String[] _legendLabels, 
             double[][] _data, int _width, int _height, int _incrScaleYAxis,
             Color[] _color, Font legendFont, Graphics g) {
         
         double max = maxYAxisScale > 0 ? maxYAxisScale : getTopValue(findMax(_data), BigDecimal.ROUND_UP); // define max scale y axis
         try {
             // if the title graph is empty, we can assume some default
             if (_title.length() == 0 ) {
                 _title = JMeterUtils.getResString("graph_resp_time_title"); //$NON-NLS-1$
             }
             this.setPreferredSize(new Dimension(_width,_height));
             DataSeries dataSeries = new DataSeries( _xAxisLabels, null, _yAxisTitle, _title ); // replace _xAxisTitle to null (don't display x axis title)
 
             // Stroke and shape line settings
             Stroke[] strokes = new Stroke[_legendLabels.length];
             for (int i = 0; i < _legendLabels.length; i++) {
                 strokes[i] = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5f);
             }
             Shape[] shapes = new Shape[_legendLabels.length];
             for (int i = 0; i < _legendLabels.length; i++) {
                 shapes[i] = pointShape;
             }
             LineChartProperties lineChartProperties= new LineChartProperties(strokes, shapes);
 
             // Lines colors
             Paint[] paints = new Paint[_color.length];
             System.arraycopy(_color, 0, paints, 0, _color.length);
             
             // Define chart type (line)
             AxisChartDataSet axisChartDataSet =
                 new AxisChartDataSet( _data, _legendLabels, paints, ChartType.LINE, lineChartProperties );
             dataSeries.addIAxisPlotDataSet(axisChartDataSet);
 
             ChartProperties chartProperties= new ChartProperties();
             LabelAxisProperties xaxis = new LabelAxisProperties();
             DataAxisProperties yaxis = new DataAxisProperties();
             yaxis.setUseCommas(showGrouping);
 
             if (legendFont != null) {
                 yaxis.setAxisTitleChartFont(new ChartFont(legendFont, new Color(20)));
                 yaxis.setScaleChartFont(new ChartFont(legendFont, new Color(20)));
                 xaxis.setAxisTitleChartFont(new ChartFont(legendFont, new Color(20)));
                 xaxis.setScaleChartFont(new ChartFont(legendFont, new Color(20)));
             }
             if (titleFont != null) {
                 chartProperties.setTitleFont(new ChartFont(titleFont, new Color(0)));
             }
 
             // Y Axis ruler
             try {
                 double numInterval = _height / 50d; // ~a tic every 50 px
                 double incrYAxis = max / numInterval;
                 double incrTopValue = _incrScaleYAxis;
                 if (_incrScaleYAxis == 0) {
                     incrTopValue = getTopValue(incrYAxis, BigDecimal.ROUND_HALF_UP);
                 }
                 if (incrTopValue < 1) { 
                     incrTopValue = 1.0d; // Increment cannot be < 1
                 }
                 yaxis.setUserDefinedScale(0, incrTopValue);
                 yaxis.setNumItems((int)(max / incrTopValue) + 1);
                 yaxis.setShowGridLines(1);
             } catch (PropertyException e) {
-                log.warn("",e);
+                log.warn("Exception while setting Y axis properties.", e);
             }
 
             AxisProperties axisProperties= new AxisProperties(xaxis, yaxis);
             axisProperties.setXAxisLabelsAreVertical(true);
             LegendProperties legendProperties= new LegendProperties();
             legendProperties.setBorderStroke(null);
             legendProperties.setPlacement(legendPlacement);
             legendProperties.setIconBorderPaint(Color.WHITE);
             legendProperties.setIconBorderStroke(new BasicStroke(0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE));
             // Manage legend placement
             legendProperties.setNumColumns(LegendAreaProperties.COLUMNS_FIT_TO_IMAGE);
             if (legendPlacement == LegendAreaProperties.RIGHT || legendPlacement == LegendAreaProperties.LEFT) {
                 legendProperties.setNumColumns(1);
             }
             if (legendFont != null) {
                 legendProperties.setFont(legendFont);
             }
             AxisChart axisChart = new AxisChart(
                     dataSeries, chartProperties, axisProperties,
                     legendProperties, _width, _height );
             axisChart.setGraphics2D((Graphics2D) g);
             axisChart.render();
         } catch (ChartDataException | PropertyException e) {
-            log.warn("", e);
+            log.warn("Exception while rendering axis chart.", e);
         }
     }
 
     private int getTopValue(double value, int roundMode) {
         String maxStr = String.valueOf(Math.round(value));
         StringBuilder divValueStr = new StringBuilder(maxStr.length()+1);
         divValueStr.append("1");
         for (int i = 1; i < maxStr.length(); i++) {
             divValueStr.append("0"); //$NON-NLS-1$
         }
         int divValueInt = Integer.parseInt(divValueStr.toString());
         BigDecimal round = BigDecimal.valueOf(value / divValueInt);
         round = round.setScale(0, roundMode);
         int topValue = round.intValue() * divValueInt;
         return topValue;
     }
 
     @Override
     public void paintComponent(Graphics graphics) {
         if (data != null && this.title != null && this.xAxisLabels != null &&
                 this.yAxisLabel != null && this.yAxisTitle != null) {
             drawSample(this.title, this.xAxisLabels, 
                     this.yAxisTitle, this.legendLabels,
                     this.data, this.width, this.height, this.incrYAxisScale, this.color,
                     this.legendFont, graphics);
         }
     }
 
     /**
      * Find max in datas
      * @param datas array of positive or NaN doubles
      * @return double
      */
     private double findMax(double[][] datas) {
         double max = 0;
         for (double[] data : datas) {
             for (final double value : data) {
                 if ((!Double.isNaN(value)) && (value > max)) {
                     max = value;
                 }
             }
         }
         return max;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/RespTimeGraphVisualizer.java b/src/components/org/apache/jmeter/visualizers/RespTimeGraphVisualizer.java
index fdce14dd3..3f145ad42 100644
--- a/src/components/org/apache/jmeter/visualizers/RespTimeGraphVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/RespTimeGraphVisualizer.java
@@ -1,972 +1,972 @@
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
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.regex.PatternSyntaxException;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComboBox;
 import javax.swing.JComponent;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextField;
 import javax.swing.SwingConstants;
 import javax.swing.UIManager;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.commons.lang3.ArrayUtils;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.SaveGraphics;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jmeter.visualizers.utils.Colors;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.math.StatCalculatorLong;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class RespTimeGraphVisualizer extends AbstractVisualizer implements ActionListener, Clearable {
 
-    private static final long serialVersionUID = 280L;
+    private static final long serialVersionUID = 281L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RespTimeGraphVisualizer.class);
 
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font"); //$NON-NLS-1$
 
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8)); //$NON-NLS-1$
 
     //+ JMX property names; do not change
 
     public static final String INTERVAL = "RespTimeGraph.interval"; // $NON-NLS-1$
 
     public static final String SERIES_SELECTION = "RespTimeGraph.seriesselection"; // $NON-NLS-1$
 
     public static final String SERIES_SELECTION_MATCH_LABEL = "RespTimeGraph.seriesselectionmatchlabel"; // $NON-NLS-1$
 
     public static final String SERIES_SELECTION_CASE_SENSITIVE = "RespTimeGraph.seriesselectioncasesensitive"; // $NON-NLS-1$
 
     public static final String SERIES_SELECTION_REGEXP = "RespTimeGraph.seriesselectionregexp"; // $NON-NLS-1$
     
     public static final String GRAPH_TITLE = "RespTimeGraph.graphtitle"; // $NON-NLS-1$
 
     public static final String GRAPH_TITLE_FONT_NAME = "RespTimeGraph.graphtitlefontname"; // $NON-NLS-1$
 
     public static final String GRAPH_TITLE_FONT_SIZE = "RespTimeGraph.graphtitlefondsize"; // $NON-NLS-1$
 
     public static final String GRAPH_TITLE_FONT_STYLE = "RespTimeGraph.graphtitlefontstyle"; // $NON-NLS-1$
 
     public static final String LINE_STROKE_WIDTH = "RespTimeGraph.linestrockwidth"; // $NON-NLS-1$
 
     public static final String LINE_SHAPE_POINT = "RespTimeGraph.lineshapepoint"; // $NON-NLS-1$
 
     public static final String GRAPH_SIZE_DYNAMIC = "RespTimeGraph.graphsizedynamic"; // $NON-NLS-1$
 
     public static final String GRAPH_SIZE_WIDTH = "RespTimeGraph.graphsizewidth"; // $NON-NLS-1$
 
     public static final String GRAPH_SIZE_HEIGHT = "RespTimeGraph.graphsizeheight"; // $NON-NLS-1$
 
     public static final String XAXIS_TIME_FORMAT = "RespTimeGraph.xaxistimeformat"; // $NON-NLS-1$
 
     public static final String YAXIS_SCALE_MAX_VALUE = "RespTimeGraph.yaxisscalemaxvalue"; // $NON-NLS-1$
 
     public static final String YAXIS_INCREMENT_SCALE = "RespTimeGraph.yaxisscaleincrement"; // $NON-NLS-1$
 
     public static final String YAXIS_NUMBER_GROUPING = "RespTimeGraph.yaxisnumbergrouping"; // $NON-NLS-1$
 
     public static final String LEGEND_PLACEMENT = "RespTimeGraph.legendplacement"; // $NON-NLS-1$
 
     public static final String LEGEND_FONT = "RespTimeGraph.legendfont"; // $NON-NLS-1$
 
     public static final String LEGEND_SIZE = "RespTimeGraph.legendsize"; // $NON-NLS-1$
 
     public static final String LEGEND_STYLE = "RespTimeGraph.legendstyle"; // $NON-NLS-1$
 
     //- JMX property names
 
     public static final int DEFAULT_INTERVAL = 10000; // in milli-seconds // TODO: properties?
 
     public static final boolean DEFAULT_SERIES_SELECTION = false;
     
     public static final boolean DEFAULT_CASE_SENSITIVE = false;
     
     public static final boolean DEFAULT_REGEXP = true;
     
     public static final int DEFAULT_TITLE_FONT_NAME = 0; // default: sans serif
     
     public static final int DEFAULT_TITLE_FONT_SIZE = 6; // default: 16
 
     public static final int DEFAULT_TITLE_FONT_STYLE = 1; // default: bold
 
     public static final int DEFAULT_STROKE_WIDTH_LIST = 4; // default: 3.0f
     
     public static final int DEFAULT_LINE_SHAPE_POINT = 0; // default: circle
 
     public static final boolean DEFAULT_DYNAMIC_GRAPH_SIZE = true; // default: true
 
     public static final String DEFAULT_XAXIS_TIME_FORMAT = "HH:mm:ss"; // $NON-NLS-1$
     
     public static final boolean DEFAULT_NUMBER_SHOW_GROUPING = true;
     
     public static final int DEFAULT_LEGEND_PLACEMENT = 0; // default: bottom
 
     public static final int DEFAULT_LEGEND_FONT = 0; // default: sans serif
     
     public static final int DEFAULT_LEGEND_SIZE = 2; // default: 10
 
     public static final int DEFAULT_LEGEND_STYLE = 0; // default: normal
 
     private static final int DEFAULT_WIDTH = 400;
 
     private static final int DEFAULT_HEIGTH = 300;
 
     private static final String Y_AXIS_LABEL = JMeterUtils.getResString("aggregate_graph_response_time");//$NON-NLS-1$
 
     private static final String Y_AXIS_TITLE = JMeterUtils.getResString("aggregate_graph_ms"); //$NON-NLS-1$
 
     /**
      * Lock used to protect list update
      */
     private final transient Object lock = new Object();
     /**
      * Lock used to protect refresh interval
      */
     private final transient Object lockInterval = new Object();
 
     private RespTimeGraphChart graphPanel = null;
 
     private final JTabbedPane tabbedGraph = new JTabbedPane(SwingConstants.TOP);
     
     private boolean saveGraphToFile = false;
     
     private int intervalValue = DEFAULT_INTERVAL;
 
     private final JLabeledTextField intervalField =
             new JLabeledTextField(JMeterUtils.getResString("graph_resp_time_interval_label"), 7); //$NON-NLS-1$
 
     private final JButton intervalButton = new JButton(JMeterUtils.getResString("graph_resp_time_interval_reload")); // $NON-NLS-1$
 
     private final JButton displayButton =
             new JButton(JMeterUtils.getResString("aggregate_graph_display")); //$NON-NLS-1$
     
     private final JButton saveGraph =
             new JButton(JMeterUtils.getResString("aggregate_graph_save")); //$NON-NLS-1$
 
     private final JCheckBox samplerSelection = new JCheckBox(JMeterUtils.getResString("graph_resp_time_series_selection"), false); //$NON-NLS-1$
 
     private final JTextField samplerMatchLabel = new JTextField();
 
     private final JButton applyFilterBtn = new JButton(JMeterUtils.getResString("graph_apply_filter")); // $NON-NLS-1$
 
     private final JCheckBox caseChkBox = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_case"), false); // $NON-NLS-1$
 
     private final JCheckBox regexpChkBox = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_regexp"), true); // $NON-NLS-1$
 
     private final JComboBox<String> titleFontNameList = new JComboBox<>(StatGraphProperties.getFontNameMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private final JComboBox<String> titleFontSizeList = new JComboBox<>(StatGraphProperties.getFontSize());
 
     private final JComboBox<String> titleFontStyleList = new JComboBox<>(StatGraphProperties.getFontStyleMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private final JComboBox<String> fontNameList = new JComboBox<>(StatGraphProperties.getFontNameMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private final JComboBox<String> fontSizeList = new JComboBox<>(StatGraphProperties.getFontSize());
 
     private final JComboBox<String> fontStyleList = new JComboBox<>(StatGraphProperties.getFontStyleMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private final JComboBox<String> legendPlacementList = new JComboBox<>(StatGraphProperties.getPlacementNameMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
     
     private final JComboBox<String> pointShapeLine = new JComboBox<>(StatGraphProperties.getPointShapeMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private final JComboBox<String> strokeWidthList = new JComboBox<>(StatGraphProperties.getStrokeWidth());
 
     private final JCheckBox numberShowGrouping = new JCheckBox(JMeterUtils.getResString("aggregate_graph_number_grouping"), // $NON-NLS-1$
             DEFAULT_NUMBER_SHOW_GROUPING); // Default checked
 
     private final JButton syncWithName =
             new JButton(JMeterUtils.getResString("aggregate_graph_sync_with_name"));  //$NON-NLS-1$
 
     private final JLabeledTextField graphTitle =
             new JLabeledTextField(JMeterUtils.getResString("graph_resp_time_title_label")); //$NON-NLS-1$
 
     private final JLabeledTextField xAxisTimeFormat =
             new JLabeledTextField(JMeterUtils.getResString("graph_resp_time_xaxis_time_format"), 10); //$NON-NLS-1$ $NON-NLS-2$
 
     private final JLabeledTextField maxValueYAxisLabel =
             new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_yaxis_max_value"), 5); //$NON-NLS-1$
 
     /**
      * checkbox for use dynamic graph size
      */
     private final JCheckBox dynamicGraphSize = new JCheckBox(JMeterUtils.getResString("aggregate_graph_dynamic_size")); // $NON-NLS-1$
 
     private final JLabeledTextField graphWidth =
             new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_width"), 6); //$NON-NLS-1$
     private final JLabeledTextField graphHeight =
             new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_height"), 6); //$NON-NLS-1$
 
     private final JLabeledTextField incrScaleYAxis =
             new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_increment_scale"), 5); //$NON-NLS-1$
 
     private long minStartTime = Long.MAX_VALUE;
 
     private long maxStartTime = Long.MIN_VALUE;
 
     /**
      * We want to retain insertion order, so LinkedHashMap is necessary
      */
     private final Map<String, RespTimeGraphLineBean> seriesNames = new LinkedHashMap<>();
 
     /**
      * We want to retain insertion order, so LinkedHashMap is necessary
      */
     private final Map<String, Map<Long, StatCalculatorLong>> pList = new LinkedHashMap<>();
 
     private long durationTest = 0;
     
     private int colorIdx = 0;
 
     private Pattern pattern = null;
 
     private transient Matcher matcher = null;
 
     private final List<Color> listColors = Colors.getColors();
 
     private final List<RespTimeGraphDataBean> internalList = new ArrayList<>(); // internal list of all results
 
     public RespTimeGraphVisualizer() {
         init();
     }
 
     @Override
     public void add(final SampleResult sampleResult) {
         final String sampleLabel = sampleResult.getSampleLabel();
         // Make a internal list of all results to allow reload data with filter or interval
         synchronized (lockInterval) {
             internalList.add(new RespTimeGraphDataBean(sampleResult.getStartTime(), sampleResult.getTime(), sampleLabel));
         }
 
         // Sampler selection
         if (samplerSelection.isSelected() && pattern != null) {
             matcher = pattern.matcher(sampleLabel);
         }
         if ((matcher == null) || (matcher.find())) {
             final long startTimeMS = sampleResult.getStartTime();
             final long startTimeInterval = startTimeMS / intervalValue;
             JMeterUtils.runSafe(false, new Runnable() {
                 @Override
                 public void run() {
                     synchronized (lock) {
                         // Use for x-axis scale
                         if (startTimeInterval < minStartTime) {
                             minStartTime = startTimeInterval;
                         } else if (startTimeInterval > maxStartTime) {
                             maxStartTime = startTimeInterval;
                         }
                         // Generate x-axis label and associated color
                         if (!seriesNames.containsKey(sampleLabel)) {
                             seriesNames.put(sampleLabel, 
                                     new RespTimeGraphLineBean(sampleLabel, listColors.get(colorIdx++)));
                             // reset colors index
                             if (colorIdx >= listColors.size()) {
                                 colorIdx = 0;
                             }
                         }
                         // List of value by sampler
                         Map<Long, StatCalculatorLong> subList = pList.get(sampleLabel);
                         final Long startTimeIntervalLong = Long.valueOf(startTimeInterval);
                         if (subList != null) {
                             long respTime = sampleResult.getTime();
                             StatCalculatorLong value = subList.get(startTimeIntervalLong);
                             if (value==null) {
                                 value = new StatCalculatorLong();
                                 subList.put(startTimeIntervalLong, value);
                             }
                             value.addValue(respTime, 1);
                         } else {
                             // We want to retain insertion order, so LinkedHashMap is necessary
                             Map<Long, StatCalculatorLong> newSubList = new LinkedHashMap<>(5);
                             StatCalculatorLong helper = new StatCalculatorLong();
                             helper.addValue(Long.valueOf(sampleResult.getTime()),1);
                             newSubList.put(startTimeIntervalLong,  helper);
                             pList.put(sampleLabel, newSubList);
                         }
                     }
                 }
             });
         }
     }
 
     public void makeGraph() {
         Dimension size = graphPanel.getSize();
         // canvas size
         int width = (int) size.getWidth();
         int height = (int) size.getHeight();
         if (!dynamicGraphSize.isSelected()) {
             String wstr = graphWidth.getText();
             String hstr = graphHeight.getText();
             if (wstr.length() != 0) {
                 width = Integer.parseInt(wstr);
             }
             if (hstr.length() != 0) {
                 height = Integer.parseInt(hstr);
             }
         }
 
         String yAxisStr = maxValueYAxisLabel.getText();
         int maxYAxisScale = yAxisStr.length() == 0 ? 0 : Integer.parseInt(yAxisStr);
 
         graphPanel.setData(this.getData());
         graphPanel.setTitle(graphTitle.getText());
         graphPanel.setMaxYAxisScale(maxYAxisScale);
 
         graphPanel.setYAxisLabels(Y_AXIS_LABEL);
         graphPanel.setYAxisTitle(Y_AXIS_TITLE);
         graphPanel.setXAxisLabels(getXAxisLabels());
         graphPanel.setLegendLabels(getLegendLabels());
         graphPanel.setColor(getLinesColors());
         graphPanel.setShowGrouping(numberShowGrouping.isSelected());
         graphPanel.setLegendPlacement(StatGraphProperties.getPlacementNameMap()
                 .get(legendPlacementList.getSelectedItem()).intValue());
         graphPanel.setPointShape(StatGraphProperties.getPointShapeMap().get(pointShapeLine.getSelectedItem()));
         graphPanel.setStrokeWidth(Float.parseFloat((String) strokeWidthList.getSelectedItem()));
 
         graphPanel.setTitleFont(new Font(StatGraphProperties.getFontNameMap().get(titleFontNameList.getSelectedItem()),
                 StatGraphProperties.getFontStyleMap().get(titleFontStyleList.getSelectedItem()).intValue(),
                 Integer.parseInt((String) titleFontSizeList.getSelectedItem())));
         graphPanel.setLegendFont(new Font(StatGraphProperties.getFontNameMap().get(fontNameList.getSelectedItem()),
                 StatGraphProperties.getFontStyleMap().get(fontStyleList.getSelectedItem()).intValue(),
                 Integer.parseInt((String) fontSizeList.getSelectedItem())));
 
         graphPanel.setHeight(height);
         graphPanel.setWidth(width);
         graphPanel.setIncrYAxisScale(getIncrScaleYAxis());
         // Draw the graph
         graphPanel.repaint();
     }
 
     /**
      * Generate the data for the jChart API
      * @return array of array of data to draw
      */
     public double[][] getData() {
         int size = pList.size();
         int max = (int) durationTest; // Test can't have a duration more than 2^31 secs (cast from long to int)
 
         double[][] data = new double[size][max];
 
         double nanLast = 0;
         double nanBegin = 0;
         List<Double> nanList = new ArrayList<>();
         int s = 0;
         for (Map<Long, StatCalculatorLong> subList : pList.values()) {
             int idx = 0;
             while (idx < durationTest) {
                 long keyShift = minStartTime + idx;
                 StatCalculatorLong value = subList.get(Long.valueOf(keyShift));
                 if (value != null) {
                     nanLast = value.getMean();
                     data[s][idx] = nanLast;
                     // Calculate intermediate values (if needed)
                     int nlsize = nanList.size();
                     if (nlsize > 0) {
                         double valPrev = nanBegin;
                         for (int cnt = 0; cnt < nlsize; cnt++) {
                             int pos = idx - (nlsize - cnt);
                             if (pos < 0) { pos = 0; }
                             valPrev = valPrev + ((nanLast - nanBegin) / (nlsize + 2));
                             data[s][pos] = valPrev;
                         }
                         nanList.clear();
                     }
                 } else {
                     nanList.add(Double.valueOf(Double.NaN));
                     nanBegin = nanLast;
                     data[s][idx] = Double.NaN;
                 }
                 idx++;
             }
             s++;
         }
         return data;
     }
 
     @Override
     public String getLabelResource() {
         return "graph_resp_time_title"; // $NON-NLS-1$
     }
 
     @Override
     public void clearData() {
         synchronized (lock) {
             internalList.clear();
             seriesNames.clear();
             pList.clear();
             minStartTime = Long.MAX_VALUE;
             maxStartTime = Long.MIN_VALUE;
             durationTest = 0;
             colorIdx = 0;
         }
         tabbedGraph.setSelectedIndex(0);
     }
 
     /**
      * Initialize the GUI.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
         Border margin2 = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         mainPanel.add(makeTitlePanel());
 
         JPanel settingsPane = new VerticalPanel();
         settingsPane.setBorder(margin2);
 
         graphPanel = new RespTimeGraphChart();
         graphPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGTH));
 
         settingsPane.add(createGraphActionsPane());
         settingsPane.add(createGraphSettingsPane());
         settingsPane.add(createGraphTitlePane());
         settingsPane.add(createLinePane());
         settingsPane.add(createGraphDimensionPane());
         JPanel axisPane = new JPanel(new BorderLayout());
         axisPane.add(createGraphXAxisPane(), BorderLayout.WEST);
         axisPane.add(createGraphYAxisPane(), BorderLayout.CENTER);
         settingsPane.add(axisPane);
         settingsPane.add(createLegendPane());
 
         tabbedGraph.addTab(JMeterUtils.getResString("aggregate_graph_tab_settings"), settingsPane); //$NON-NLS-1$
         tabbedGraph.addTab(JMeterUtils.getResString("aggregate_graph_tab_graph"), graphPanel); //$NON-NLS-1$
         
         // If clic on the Graph tab, make the graph (without apply interval or filter)
         ChangeListener changeListener = new ChangeListener() {
             @Override
             public void stateChanged(ChangeEvent changeEvent) {
                 JTabbedPane srcTab = (JTabbedPane) changeEvent.getSource();
                 int index = srcTab.getSelectedIndex();
                 if (srcTab.getTitleAt(index).equals(JMeterUtils.getResString("aggregate_graph_tab_graph"))) { //$NON-NLS-1$
                     actionMakeGraph();
                 }
             }
         };
         tabbedGraph.addChangeListener(changeListener);
 
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(tabbedGraph, BorderLayout.CENTER);
 
     }
 
     @Override
     public void actionPerformed(ActionEvent event) {
         boolean forceReloadData = false;
         final Object eventSource = event.getSource();
         if (eventSource == displayButton) {
             actionMakeGraph();
         } else if (eventSource == saveGraph) {
             saveGraphToFile = true;
             try {
                 ActionRouter.getInstance().getAction(
                         ActionNames.SAVE_GRAPHICS,SaveGraphics.class.getName()).doAction(
                                 new ActionEvent(this,event.getID(),ActionNames.SAVE_GRAPHICS));
             } catch (Exception e) {
                 log.error(e.getMessage());
             }
         } else if (eventSource == syncWithName) {
             graphTitle.setText(namePanel.getName());
         } else if (eventSource == dynamicGraphSize) {
                 enableDynamicGraph(dynamicGraphSize.isSelected());
         } else if (eventSource == samplerSelection) {
             enableSamplerSelection(samplerSelection.isSelected());
             if (!samplerSelection.isSelected()) {
                 // Force reload data
                 forceReloadData = true;
             }
         }
         // Not 'else if' because forceReloadData 
         if (eventSource == applyFilterBtn || eventSource == intervalButton || forceReloadData) {
             if (eventSource == intervalButton) {
                 intervalValue = Integer.parseInt(intervalField.getText());
             }
             if (eventSource == applyFilterBtn && samplerSelection.isSelected() && samplerMatchLabel.getText() != null
                     && samplerMatchLabel.getText().length() > 0) {
                 pattern = createPattern(samplerMatchLabel.getText());
             } else if (forceReloadData) {
                 pattern = null;
                 matcher = null;
             }
             if (getFile() != null && getFile().length() > 0) {
                 // Reload data from file
                 clearData();
                 FilePanel filePanel = (FilePanel) getFilePanel();
                 filePanel.actionPerformed(event);
             } else {
                 // Reload data form internal list of results
                 synchronized (lockInterval) {
                     if (internalList.size() >= 2) {
                         List<RespTimeGraphDataBean> tempList = new ArrayList<>();
                         tempList.addAll(internalList);
                         this.clearData();
                         for (RespTimeGraphDataBean data : tempList) {
                             SampleResult sr = new SampleResult(data.getStartTime(), data.getTime());
                             sr.setSampleLabel(data.getSamplerLabel());
                             this.add(sr);
                         }
                     }
                 }
             }
         } 
     }
 
     private void actionMakeGraph() {
         String msgErr = null;
         // Calculate the test duration. Needs to xAxis Labels and getData.
         durationTest = maxStartTime - minStartTime;
         if (seriesNames.size() <= 0) {
             msgErr = JMeterUtils.getResString("aggregate_graph_no_values_to_graph"); // $NON-NLS-1$
         } else   if (durationTest < 1) {
             msgErr = JMeterUtils.getResString("graph_resp_time_not_enough_data"); // $NON-NLS-1$
         }
         if (msgErr == null) {
             makeGraph();
             tabbedGraph.setSelectedIndex(1);
         } else {
             tabbedGraph.setSelectedIndex(0);
             JOptionPane.showMessageDialog(null, msgErr, msgErr, JOptionPane.WARNING_MESSAGE);
         }
     }
 
     @Override
     public JComponent getPrintableComponent() {
         if (saveGraphToFile) {
             saveGraphToFile = false;
             // (re)draw the graph first to take settings into account (Bug 58329)
             if (getData().length > 0 && getData()[0].length>0) {
                 makeGraph();
             }
             graphPanel.setBounds(graphPanel.getLocation().x,graphPanel.getLocation().y,
                     graphPanel.width,graphPanel.height);
             return graphPanel;
         }
         return this;
     }
 
     @Override
     public void configure(TestElement te) {
         super.configure(te);
         intervalField.setText(te.getPropertyAsString(INTERVAL, String.valueOf(DEFAULT_INTERVAL)));
         samplerSelection.setSelected(te.getPropertyAsBoolean(SERIES_SELECTION, DEFAULT_SERIES_SELECTION));
         samplerMatchLabel.setText(te.getPropertyAsString(SERIES_SELECTION_MATCH_LABEL, "")); //$NON-NLS-1$
         caseChkBox.setSelected(te.getPropertyAsBoolean(SERIES_SELECTION_CASE_SENSITIVE, DEFAULT_CASE_SENSITIVE));
         regexpChkBox.setSelected(te.getPropertyAsBoolean(SERIES_SELECTION_REGEXP, DEFAULT_REGEXP));
         graphTitle.setText(te.getPropertyAsString(GRAPH_TITLE, "")); //$NON-NLS-1$
         titleFontNameList.setSelectedIndex(te.getPropertyAsInt(GRAPH_TITLE_FONT_NAME, DEFAULT_TITLE_FONT_NAME));
         titleFontSizeList.setSelectedIndex(te.getPropertyAsInt(GRAPH_TITLE_FONT_SIZE, DEFAULT_TITLE_FONT_SIZE));
         titleFontStyleList.setSelectedIndex(te.getPropertyAsInt(GRAPH_TITLE_FONT_STYLE, DEFAULT_TITLE_FONT_STYLE));
         strokeWidthList.setSelectedIndex(te.getPropertyAsInt(LINE_STROKE_WIDTH, DEFAULT_STROKE_WIDTH_LIST));
         pointShapeLine.setSelectedIndex(te.getPropertyAsInt(LINE_SHAPE_POINT, DEFAULT_LINE_SHAPE_POINT));
         dynamicGraphSize.setSelected(te.getPropertyAsBoolean(GRAPH_SIZE_DYNAMIC, DEFAULT_DYNAMIC_GRAPH_SIZE));
         graphWidth.setText(te.getPropertyAsString(GRAPH_SIZE_WIDTH, "")); //$NON-NLS-1$
         graphHeight.setText(te.getPropertyAsString(GRAPH_SIZE_HEIGHT, "")); //$NON-NLS-1$
         xAxisTimeFormat.setText(te.getPropertyAsString(XAXIS_TIME_FORMAT, DEFAULT_XAXIS_TIME_FORMAT));
         maxValueYAxisLabel.setText(te.getPropertyAsString(YAXIS_SCALE_MAX_VALUE, "")); //$NON-NLS-1$
         incrScaleYAxis.setText(te.getPropertyAsString(YAXIS_INCREMENT_SCALE, "")); //$NON-NLS-1$
         numberShowGrouping.setSelected(te.getPropertyAsBoolean(YAXIS_NUMBER_GROUPING, DEFAULT_NUMBER_SHOW_GROUPING));
         legendPlacementList.setSelectedIndex(te.getPropertyAsInt(LEGEND_PLACEMENT, DEFAULT_LEGEND_PLACEMENT));
         fontNameList.setSelectedIndex(te.getPropertyAsInt(LEGEND_FONT, DEFAULT_LEGEND_FONT));
         fontSizeList.setSelectedIndex(te.getPropertyAsInt(LEGEND_SIZE, DEFAULT_LEGEND_SIZE));
         fontStyleList.setSelectedIndex(te.getPropertyAsInt(LEGEND_STYLE, DEFAULT_LEGEND_STYLE));
         
         enableSamplerSelection(samplerSelection.isSelected());
         enableDynamicGraph(dynamicGraphSize.isSelected());
     }
 
     @Override
     public void modifyTestElement(TestElement te) {
         super.modifyTestElement(te);
         te.setProperty(INTERVAL, intervalField.getText(), String.valueOf(DEFAULT_INTERVAL));
         te.setProperty(SERIES_SELECTION, samplerSelection.isSelected(), DEFAULT_SERIES_SELECTION);
         te.setProperty(SERIES_SELECTION_MATCH_LABEL, samplerMatchLabel.getText(), ""); //$NON-NLS-1$
         te.setProperty(SERIES_SELECTION_CASE_SENSITIVE, caseChkBox.isSelected(), DEFAULT_CASE_SENSITIVE);
         te.setProperty(SERIES_SELECTION_REGEXP, regexpChkBox.isSelected(), DEFAULT_REGEXP);
         te.setProperty(GRAPH_TITLE, graphTitle.getText(), ""); //$NON-NLS-1$
         te.setProperty(GRAPH_TITLE_FONT_NAME, titleFontNameList.getSelectedIndex(), DEFAULT_TITLE_FONT_NAME);
         te.setProperty(GRAPH_TITLE_FONT_SIZE, titleFontSizeList.getSelectedIndex(), DEFAULT_TITLE_FONT_SIZE);
         te.setProperty(GRAPH_TITLE_FONT_STYLE, titleFontStyleList.getSelectedIndex(), DEFAULT_TITLE_FONT_STYLE);
         te.setProperty(LINE_STROKE_WIDTH, strokeWidthList.getSelectedIndex(), DEFAULT_STROKE_WIDTH_LIST);
         te.setProperty(LINE_SHAPE_POINT, pointShapeLine.getSelectedIndex(), DEFAULT_LINE_SHAPE_POINT);
         te.setProperty(GRAPH_SIZE_DYNAMIC, dynamicGraphSize.isSelected(), DEFAULT_DYNAMIC_GRAPH_SIZE);
         te.setProperty(GRAPH_SIZE_WIDTH, graphWidth.getText(), ""); //$NON-NLS-1$
         te.setProperty(GRAPH_SIZE_HEIGHT, graphHeight.getText(), ""); //$NON-NLS-1$
         te.setProperty(XAXIS_TIME_FORMAT, xAxisTimeFormat.getText(), DEFAULT_XAXIS_TIME_FORMAT);
         te.setProperty(YAXIS_SCALE_MAX_VALUE, maxValueYAxisLabel.getText(), ""); //$NON-NLS-1$
         te.setProperty(YAXIS_INCREMENT_SCALE, incrScaleYAxis.getText(), ""); //$NON-NLS-1$
         te.setProperty(YAXIS_NUMBER_GROUPING, numberShowGrouping.isSelected(), DEFAULT_NUMBER_SHOW_GROUPING);
         te.setProperty(LEGEND_PLACEMENT, legendPlacementList.getSelectedIndex(), DEFAULT_LEGEND_PLACEMENT);
         te.setProperty(LEGEND_FONT, fontNameList.getSelectedIndex(), DEFAULT_LEGEND_FONT);
         te.setProperty(LEGEND_SIZE, fontSizeList.getSelectedIndex(), DEFAULT_LEGEND_SIZE);
         te.setProperty(LEGEND_STYLE, fontStyleList.getSelectedIndex(), DEFAULT_LEGEND_STYLE);
         
         // Update sub-element visibility and data reload if need
         enableSamplerSelection(samplerSelection.isSelected());
         enableDynamicGraph(dynamicGraphSize.isSelected());
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
         intervalField.setText(String.valueOf(DEFAULT_INTERVAL));
         samplerSelection.setSelected(DEFAULT_SERIES_SELECTION);
         samplerMatchLabel.setText( ""); //$NON-NLS-1$
         caseChkBox.setSelected(DEFAULT_CASE_SENSITIVE);
         regexpChkBox.setSelected(DEFAULT_REGEXP);
         graphTitle.setText(""); //$NON-NLS-1$
         titleFontNameList.setSelectedIndex(DEFAULT_TITLE_FONT_NAME);
         titleFontSizeList.setSelectedIndex(DEFAULT_TITLE_FONT_SIZE);
         titleFontStyleList.setSelectedIndex(DEFAULT_TITLE_FONT_STYLE);
         strokeWidthList.setSelectedIndex(DEFAULT_STROKE_WIDTH_LIST);
         pointShapeLine.setSelectedIndex(DEFAULT_LINE_SHAPE_POINT);
         dynamicGraphSize.setSelected(DEFAULT_DYNAMIC_GRAPH_SIZE);
         graphWidth.setText(""); //$NON-NLS-1$
         graphHeight.setText(""); //$NON-NLS-1$
         xAxisTimeFormat.setText(DEFAULT_XAXIS_TIME_FORMAT);
         maxValueYAxisLabel.setText(""); //$NON-NLS-1$
         incrScaleYAxis.setText(""); //$NON-NLS-1$
         numberShowGrouping.setSelected(DEFAULT_NUMBER_SHOW_GROUPING);
         legendPlacementList.setSelectedIndex(DEFAULT_LEGEND_PLACEMENT);
         fontNameList.setSelectedIndex(DEFAULT_LEGEND_FONT);
         fontSizeList.setSelectedIndex(DEFAULT_LEGEND_SIZE);
         fontStyleList.setSelectedIndex(DEFAULT_LEGEND_STYLE);
     }
 
     private JPanel createGraphActionsPane() {
         JPanel buttonPanel = new JPanel(new BorderLayout());
         JPanel displayPane = new JPanel();
         displayPane.add(displayButton);
         displayButton.addActionListener(this);
         buttonPanel.add(displayPane, BorderLayout.WEST);
 
         JPanel savePane = new JPanel();
         savePane.add(saveGraph);
         saveGraph.addActionListener(this);
         syncWithName.addActionListener(this);
         buttonPanel.add(savePane, BorderLayout.EAST);
 
         return buttonPanel;
     }
 
     public String[] getXAxisLabels() {
         SimpleDateFormat formatter = new SimpleDateFormat(xAxisTimeFormat.getText()); //$NON-NLS-1$ 
         String[] xAxisLabels = new String[(int) durationTest]; // Test can't have a duration more than 2^31 secs (cast from long to int)
         for (int j = 0; j < durationTest; j++) {
             xAxisLabels[j] = formatter.format(new Date((minStartTime + j) * intervalValue));
         }
         return xAxisLabels;
     }
 
     private String[] getLegendLabels() {
         String[] legends = new String[seriesNames.size()];
         int i = 0;
         for (Map.Entry<String, RespTimeGraphLineBean> entry : seriesNames.entrySet()) {
             RespTimeGraphLineBean val = entry.getValue();
             legends[i] = val.getLabel();
             i++;
         }
         return legends;
     }
 
     private Color[] getLinesColors() {
         Color[] linesColors = new Color[seriesNames.size()];
         int i = 0;
         for (Map.Entry<String, RespTimeGraphLineBean> entry : seriesNames.entrySet()) {
             RespTimeGraphLineBean val = entry.getValue();
             linesColors[i] = val.getLineColor();
             i++;
         }
         return linesColors;
     }
 
     private int getIncrScaleYAxis() {
         int incrYAxisScale = 0;
         String iyas = incrScaleYAxis.getText();
         if (iyas.length() != 0) {
             incrYAxisScale = Integer.parseInt(iyas);
         }
         return incrYAxisScale;
     }
 
     private JPanel createGraphSettingsPane() {
         JPanel settingsPane = new JPanel(new BorderLayout());
         settingsPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("graph_resp_time_settings_pane"))); // $NON-NLS-1$
         
         JPanel intervalPane = new JPanel();
         intervalPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         intervalField.setText(String.valueOf(DEFAULT_INTERVAL));
         intervalPane.add(intervalField);
         
         // Button
         intervalButton.setFont(FONT_SMALL);
         intervalButton.addActionListener(this);
         intervalPane.add(intervalButton);
 
         settingsPane.add(intervalPane, BorderLayout.NORTH);
         settingsPane.add(createGraphSelectionSubPane(), BorderLayout.SOUTH);
 
         return settingsPane;
     }
 
     private JPanel createGraphSelectionSubPane() {
         // Search field
         JPanel searchPanel = new JPanel();
         searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
         searchPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
 
         searchPanel.add(samplerSelection);
         samplerMatchLabel.setEnabled(false);
         applyFilterBtn.setEnabled(false);
         caseChkBox.setEnabled(false);
         regexpChkBox.setEnabled(false);
         samplerSelection.addActionListener(this);
 
         searchPanel.add(samplerMatchLabel);
         searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
 
         // Button
         applyFilterBtn.setFont(FONT_SMALL);
         applyFilterBtn.addActionListener(this);
         searchPanel.add(applyFilterBtn);
 
         // checkboxes
         caseChkBox.setFont(FONT_SMALL);
         searchPanel.add(caseChkBox);
         regexpChkBox.setFont(FONT_SMALL);
         searchPanel.add(regexpChkBox);
 
         return searchPanel;
     }
 
     private JPanel createGraphTitlePane() {
         JPanel titleNamePane = new JPanel(new BorderLayout());
         syncWithName.setFont(FONT_SMALL);
         titleNamePane.add(graphTitle, BorderLayout.CENTER);
         titleNamePane.add(syncWithName, BorderLayout.EAST);
 
         JPanel titleStylePane = new JPanel();
         titleStylePane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
         titleStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_font"), //$NON-NLS-1$
                 titleFontNameList));
         titleFontNameList.setSelectedIndex(DEFAULT_TITLE_FONT_NAME);
         titleStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_size"), //$NON-NLS-1$
                 titleFontSizeList));
         titleFontSizeList.setSelectedItem(StatGraphProperties.getFontSize()[DEFAULT_TITLE_FONT_SIZE]);
         titleStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_style"), //$NON-NLS-1$
                 titleFontStyleList));
         titleFontStyleList.setSelectedIndex(DEFAULT_TITLE_FONT_STYLE);
 
         JPanel titlePane = new JPanel(new BorderLayout());
         titlePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_title_group"))); // $NON-NLS-1$
         titlePane.add(titleNamePane, BorderLayout.NORTH);
         titlePane.add(titleStylePane, BorderLayout.SOUTH);
         return titlePane;
     }
 
     private JPanel createLinePane() {       
         JPanel lineStylePane = new JPanel();
         lineStylePane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         lineStylePane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("graph_resp_time_settings_line"))); // $NON-NLS-1$
         lineStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("graph_resp_time_stroke_width"), //$NON-NLS-1$
                 strokeWidthList));
         strokeWidthList.setSelectedItem(StatGraphProperties.getStrokeWidth()[DEFAULT_STROKE_WIDTH_LIST]);
         lineStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("graph_resp_time_shape_label"), //$NON-NLS-1$
                 pointShapeLine));
         pointShapeLine.setSelectedIndex(DEFAULT_LINE_SHAPE_POINT);
         return lineStylePane;
     }
 
     private JPanel createGraphDimensionPane() {
         JPanel dimensionPane = new JPanel();
         dimensionPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         dimensionPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_dimension"))); // $NON-NLS-1$
 
         dimensionPane.add(dynamicGraphSize);
         dynamicGraphSize.setSelected(DEFAULT_DYNAMIC_GRAPH_SIZE);
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
         xAxisTimeFormat.setText(DEFAULT_XAXIS_TIME_FORMAT); // $NON-NLS-1$
         xAxisPane.add(xAxisTimeFormat);
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
         yAxisPane.add(incrScaleYAxis);
         yAxisPane.add(numberShowGrouping);
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
 
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_legend_placement"), //$NON-NLS-1$
                 legendPlacementList));
         legendPlacementList.setSelectedIndex(DEFAULT_LEGEND_PLACEMENT);
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_font"), //$NON-NLS-1$
                 fontNameList));
         fontNameList.setSelectedIndex(DEFAULT_LEGEND_FONT);
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_size"), //$NON-NLS-1$
                 fontSizeList));
         fontSizeList.setSelectedItem(StatGraphProperties.getFontSize()[DEFAULT_LEGEND_SIZE]);
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_style"), //$NON-NLS-1$
                 fontStyleList));
         fontStyleList.setSelectedIndex(DEFAULT_LEGEND_STYLE);
 
         return legendPanel;
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
     
     private void enableDynamicGraph(boolean enable) {
         // if use dynamic graph size is checked, we disable the dimension fields
         if (enable) {
             graphWidth.setEnabled(false);
             graphHeight.setEnabled(false);
         } else {
             graphWidth.setEnabled(true);
             graphHeight.setEnabled(true);
         }
     }
 
     private void enableSamplerSelection(boolean enable) {
         if (enable) {
             samplerMatchLabel.setEnabled(true);
             applyFilterBtn.setEnabled(true);
             caseChkBox.setEnabled(true);
             regexpChkBox.setEnabled(true);
         } else {
             samplerMatchLabel.setEnabled(false);
             applyFilterBtn.setEnabled(false);
             caseChkBox.setEnabled(false);
             regexpChkBox.setEnabled(false);
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/SearchTextExtension.java b/src/components/org/apache/jmeter/visualizers/SearchTextExtension.java
index d27621af3..dfa2107d8 100644
--- a/src/components/org/apache/jmeter/visualizers/SearchTextExtension.java
+++ b/src/components/org/apache/jmeter/visualizers/SearchTextExtension.java
@@ -1,339 +1,337 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.regex.PatternSyntaxException;
 
 import javax.swing.AbstractAction;
 import javax.swing.ActionMap;
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.InputMap;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JComponent;
 import javax.swing.JEditorPane;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
 import javax.swing.UIManager;
 import javax.swing.event.DocumentEvent;
 import javax.swing.event.DocumentListener;
 import javax.swing.text.BadLocationException;
 import javax.swing.text.DefaultHighlighter;
 import javax.swing.text.Document;
 import javax.swing.text.Highlighter;
 
 import org.apache.jmeter.gui.action.KeyStrokes;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class SearchTextExtension implements ActionListener, DocumentListener {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SearchTextExtension.class);
 
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
 
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));
 
     private static final String SEARCH_TEXT_COMMAND = "search_text"; // $NON-NLS-1$
 
     public static final Color LIGHT_RED = new Color(0xFF, 0x80, 0x80);
 
     private JLabel label;
 
     private JButton findButton;
 
     private JTextField textToFindField;
 
     private JCheckBox caseChkBox;
 
     private JCheckBox regexpChkBox;
 
     private JPanel searchPanel;
     
     private String lastTextTofind;
     
     private ISearchTextExtensionProvider searchProvider;
 
     public void init(JPanel resultsPane) {}
 
     public void setResults(JEditorPane results) {
         setSearchProvider(new JEditorPaneSearchProvider(results));
     }
     
     public void setSearchProvider(ISearchTextExtensionProvider searchProvider) {
         if (this.searchProvider != null) {
             this.searchProvider.resetTextToFind();
         }
         
         this.searchProvider = searchProvider;
     }
 
     /**
      * Launch find text engine on response text
      */
     private void executeAndShowTextFind() {
         String textToFind = textToFindField.getText();
         if (this.searchProvider != null) {
             // new search?
             if (lastTextTofind != null && !lastTextTofind.equals(textToFind)) {
                 searchProvider.resetTextToFind();
                 textToFindField.setBackground(Color.WHITE);
                 textToFindField.setForeground(Color.BLACK);
             }
             
             try {
                 Pattern pattern = createPattern(textToFindField.getText());
                 boolean found = searchProvider.executeAndShowTextFind(pattern);
                 if(found) {
                     findButton.setText(JMeterUtils.getResString("search_text_button_next"));// $NON-NLS-1$
                     lastTextTofind = textToFind;
                     textToFindField.setBackground(Color.WHITE);
                     textToFindField.setForeground(Color.BLACK);
                 }
                 else {
                     findButton.setText(JMeterUtils.getResString("search_text_button_find"));// $NON-NLS-1$
                     textToFindField.setBackground(LIGHT_RED);
                     textToFindField.setForeground(Color.WHITE);
                 }
             } catch (PatternSyntaxException pse) {
                 JOptionPane.showMessageDialog(null, 
                         pse.toString(),// $NON-NLS-1$
                         JMeterUtils.getResString("error_title"), // $NON-NLS-1$
                         JOptionPane.WARNING_MESSAGE);
             }
         }
     }
 
     /**
      * Create the text find task pane
      *
      * @return Text find task pane
      */
     private JPanel createSearchTextPanel() {
         // Search field
         searchPanel = new JPanel();
         searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
         searchPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
         label = new JLabel(JMeterUtils.getResString("search_text_field")); // $NON-NLS-1$
         label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
         searchPanel.add(label);
         textToFindField = new JTextField(); // $NON-NLS-1$
         searchPanel.add(textToFindField);
         searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
 
         // add listener to intercept texttofind changes and reset search
         textToFindField.getDocument().addDocumentListener(this);
 
         // Buttons
         findButton = new JButton(JMeterUtils
                 .getResString("search_text_button_find")); // $NON-NLS-1$
         findButton.setFont(FONT_SMALL);
         findButton.setActionCommand(SEARCH_TEXT_COMMAND);
         findButton.addActionListener(this);
         searchPanel.add(findButton);
 
         // checkboxes
         caseChkBox = new JCheckBox(JMeterUtils
                 .getResString("search_text_chkbox_case"), false); // $NON-NLS-1$
         caseChkBox.setFont(FONT_SMALL);
         searchPanel.add(caseChkBox);
         regexpChkBox = new JCheckBox(JMeterUtils
                 .getResString("search_text_chkbox_regexp"), false); // $NON-NLS-1$
         regexpChkBox.setFont(FONT_SMALL);
         searchPanel.add(regexpChkBox);
 
         // when Enter is pressed, search start
         InputMap im = textToFindField
                 .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         im.put(KeyStrokes.ENTER, SEARCH_TEXT_COMMAND);
         ActionMap am = textToFindField.getActionMap();
         am.put(SEARCH_TEXT_COMMAND, new EnterAction());
 
         // default not visible
         searchPanel.setVisible(true);
         return searchPanel;
     }
 
     public JPanel createSearchTextExtensionPane() {
         JPanel pane = new JPanel();
         pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
         pane.add(createSearchTextPanel());
         return pane;
     }
 
     /**
      * Display the response as text or as rendered HTML. Change the text on the
      * button appropriate to the current display.
      *
      * @param e the ActionEvent being processed
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         String command = e.getActionCommand();
 
         // Search text in response data
         if (SEARCH_TEXT_COMMAND.equals(command)) {
             executeAndShowTextFind();
         }
     }
 
     private class EnterAction extends AbstractAction {
-        private static final long serialVersionUID = 1L;
+        private static final long serialVersionUID = 2L;
         @Override
         public void actionPerformed(ActionEvent ev) {
             executeAndShowTextFind();
         }
     }
 
     // DocumentListener method
     @Override
     public void changedUpdate(DocumentEvent e) {
         // do nothing
     }
 
     // DocumentListener method
     @Override
     public void insertUpdate(DocumentEvent e) {
         resetTextToFind();
     }
 
     // DocumentListener method
     @Override
     public void removeUpdate(DocumentEvent e) {
         resetTextToFind();
     }
 
     public void resetTextToFind() {
         if (this.searchProvider != null) {
             searchProvider.resetTextToFind();
         }
         lastTextTofind = null;
         findButton.setText(JMeterUtils.getResString("search_text_button_find"));// $NON-NLS-1$
     }
 
     private Pattern createPattern(String textToFind) {
         // desactivate or not specials regexp char
         String textToFindQ = regexpChkBox.isSelected() ? textToFind : Pattern.quote(textToFind);        
         return caseChkBox.isSelected() ? Pattern.compile(textToFindQ) :
             Pattern.compile(textToFindQ, Pattern.CASE_INSENSITIVE);
     }
     
     /**
      * Search provider definition
      * Allow the search extension to search on any component
      */
     public interface ISearchTextExtensionProvider {
         
         /**
          * reset the provider
          */
         void resetTextToFind();
         
         /**
          * Launch find text engine on target component
          * @param pattern text pattern to search
          * @return true if there was a match, false otherwise
          */
         boolean executeAndShowTextFind(Pattern pattern);
     }
     
     /**
      * JEditorPane search provider
      * Should probably be moved in its on file
      */
     private static class JEditorPaneSearchProvider implements ISearchTextExtensionProvider {
 
         private static volatile int LAST_POSITION_DEFAULT = 0;
         private static final Color HILIT_COLOR = Color.LIGHT_GRAY;
         private JEditorPane results;
         private Highlighter selection;
         private Highlighter.HighlightPainter painter;
         private int lastPosition = LAST_POSITION_DEFAULT;
         
         public JEditorPaneSearchProvider(JEditorPane results) {
             this.results = results;
             
             // prepare highlighter to show text find with search command
             selection = new DefaultHighlighter();
             painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
             results.setHighlighter(selection);
         }
 
         @Override
         public void resetTextToFind() {
             // Reset search
             lastPosition = LAST_POSITION_DEFAULT;
             selection.removeAllHighlights();
             results.setCaretPosition(0);
         }
 
         @Override
         public boolean executeAndShowTextFind(Pattern pattern) {
             boolean found = false;
             if (results != null && results.getText().length() > 0
                     && pattern != null) {
 
-                if (log.isDebugEnabled()) {
-                    log.debug("lastPosition=" + lastPosition);
-                }
-                
+                log.debug("lastPosition={}", lastPosition);
+
                 Matcher matcher = null;
                 try {
                     Document contentDoc = results.getDocument();
                     String body = contentDoc.getText(lastPosition, contentDoc.getLength() - lastPosition);
                     matcher = pattern.matcher(body);
 
                     if ((matcher != null) && (matcher.find())) {
                         selection.removeAllHighlights();
                         selection.addHighlight(lastPosition + matcher.start(),
                                 lastPosition + matcher.end(), painter);
                         results.setCaretPosition(lastPosition + matcher.end());
 
                         // save search position
                         lastPosition = lastPosition + matcher.end();
                         found = true;
                     }
                     else {
                         // reset search
                         lastPosition = LAST_POSITION_DEFAULT;
                         results.setCaretPosition(0);
                     }
                 } catch (BadLocationException ble) {
                     log.error("Location exception in text find", ble);// $NON-NLS-1$
                 }
             }
             
             return found;
         }
         
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/SearchTreePanel.java b/src/components/org/apache/jmeter/visualizers/SearchTreePanel.java
index b656f2f37..7c02d2985 100644
--- a/src/components/org/apache/jmeter/visualizers/SearchTreePanel.java
+++ b/src/components/org/apache/jmeter/visualizers/SearchTreePanel.java
@@ -1,182 +1,182 @@
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
 import java.awt.FlowLayout;
 import java.awt.Font;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 import javax.swing.UIManager;
 import javax.swing.tree.DefaultMutableTreeNode;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.gui.action.RawTextSearcher;
 import org.apache.jmeter.gui.action.RegexpSearcher;
 import org.apache.jmeter.gui.action.Searcher;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Panel used by {@link ViewResultsFullVisualizer} to search for data within the Tree
  * @since 3.0
  */
 public class SearchTreePanel extends JPanel implements ActionListener {
 
-    private static final long serialVersionUID = -4436834972710248247L;
+    private static final long serialVersionUID = 1L;
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SearchTreePanel.class);
 
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font");
 
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8));
 
     private JButton searchButton;
 
     private JLabeledTextField searchTF;
 
     private JCheckBox isRegexpCB;
 
     private JCheckBox isCaseSensitiveCB;
 
     private JButton resetButton;
 
     private DefaultMutableTreeNode defaultMutableTreeNode;
 
     public SearchTreePanel(DefaultMutableTreeNode defaultMutableTreeNode) {
         super(); 
         init();
         this.defaultMutableTreeNode = defaultMutableTreeNode;
     }
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public SearchTreePanel(){
 //        log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout(10,10));
 
         searchTF = new JLabeledTextField(JMeterUtils.getResString("search_text_field"), 20); //$NON-NLS-1$
         isRegexpCB = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_regexp"), false); //$NON-NLS-1$
         isCaseSensitiveCB = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_case"), false); //$NON-NLS-1$
         
         isRegexpCB.setFont(FONT_SMALL);
         isCaseSensitiveCB.setFont(FONT_SMALL);
 
         searchButton = new JButton(JMeterUtils.getResString("search")); //$NON-NLS-1$
         searchButton.addActionListener(this);
         resetButton = new JButton(JMeterUtils.getResString("reset")); //$NON-NLS-1$
         resetButton.addActionListener(this);
 
         JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
         
         searchPanel.add(searchTF);
         searchPanel.add(isCaseSensitiveCB);
         searchPanel.add(isRegexpCB);        
         searchPanel.add(searchButton);
         searchPanel.add(resetButton);
         add(searchPanel);
     }
 
     /**
      * Do search
      * @param e {@link ActionEvent}
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         if(e.getSource() == searchButton) {
             doSearch(e);
         } else if (e.getSource() == resetButton) {
             doResetSearch((SearchableTreeNode)defaultMutableTreeNode);
         }
     }
 
     /**
      * @param searchableTreeNode
      */
     private void doResetSearch(SearchableTreeNode searchableTreeNode) {
         searchableTreeNode.reset();
         searchableTreeNode.updateState();
         for (int i = 0; i < searchableTreeNode.getChildCount(); i++) {
             doResetSearch((SearchableTreeNode)searchableTreeNode.getChildAt(i));
         }
     }
 
 
     /**
      * @param e {@link ActionEvent}
      */
     private void doSearch(ActionEvent e) {
         String wordToSearch = searchTF.getText();
         if (StringUtils.isEmpty(wordToSearch)) {
             return;
         }
         Searcher searcher = isRegexpCB.isSelected() ?
             new RegexpSearcher(isCaseSensitiveCB.isSelected(), searchTF.getText()) : 
             new RawTextSearcher(isCaseSensitiveCB.isSelected(), searchTF.getText());        
         searchInNode(searcher, (SearchableTreeNode)defaultMutableTreeNode);
     }
 
     /**
      * @param searcher
      * @param node
      */
     private boolean searchInNode(Searcher searcher, SearchableTreeNode node) {
         node.reset();
         Object userObject = node.getUserObject();
         
         try {
             Searchable searchable;
             if(userObject instanceof Searchable) {
                 searchable = (Searchable) userObject;
             } else {
                 return false;
             }
             if(searcher.search(searchable.getSearchableTokens())) {
                 node.setNodeHasMatched(true);
             }
             boolean foundInChildren = false;
             for (int i = 0; i < node.getChildCount(); i++) {
                 searchInNode(searcher, (SearchableTreeNode)node.getChildAt(i));
                 foundInChildren =  
                         searchInNode(searcher, (SearchableTreeNode)node.getChildAt(i))
                         || foundInChildren; // Must be the last in condition
             }
             if(!node.isNodeHasMatched()) {
                 node.setChildrenNodesHaveMatched(foundInChildren);
             }
             node.updateState();
             return node.isNodeHasMatched() || node.isChildrenNodesHaveMatched();
         } catch (Exception e) {
-            LOG.error("Error extracting data from tree node");
+            log.error("Error extracting data from tree node");
             return false;
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
index 72da4790b..9c8681bca 100644
--- a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
@@ -1,998 +1,998 @@
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
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.nio.charset.Charset;
 import java.text.DecimalFormat;
 import java.text.Format;
 import java.text.MessageFormat;
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
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTable;
 import javax.swing.JTextField;
 import javax.swing.SwingConstants;
 import javax.swing.UIManager;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
 
 import org.apache.commons.lang3.ArrayUtils;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.SaveGraphics;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRendererWrapper;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.gui.NumberRenderer;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.ObjectTableSorter;
 import org.apache.jorphan.gui.RateRenderer;
 import org.apache.jorphan.gui.RendererUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Aggregrate Table-Based Reporting Visualizer for JMeter. Props to the people
  * who've done the other visualizers ahead of me (Stefano Mazzocchi), who I
  * borrowed code from to start me off (and much code may still exist). Thank
  * you!
  *
  */
 public class StatGraphVisualizer extends AbstractVisualizer implements Clearable, ActionListener {
-    private static final long serialVersionUID = 241L;
+    private static final long serialVersionUID = 242L;
 
     private static final String PCT1_LABEL = JMeterUtils.getPropDefault("aggregate_rpt_pct1", "90");
     private static final String PCT2_LABEL = JMeterUtils.getPropDefault("aggregate_rpt_pct2", "95");
     private static final String PCT3_LABEL = JMeterUtils.getPropDefault("aggregate_rpt_pct3", "99");
     
     private static final Float PCT1_VALUE = new Float(Float.parseFloat(PCT1_LABEL)/100);
     private static final Float PCT2_VALUE =  new Float(Float.parseFloat(PCT2_LABEL)/100);
     private static final Float PCT3_VALUE =  new Float(Float.parseFloat(PCT3_LABEL)/100);
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(StatGraphVisualizer.class);
 
     private static final String[] COLUMNS = { 
             "sampler_label",                  //$NON-NLS-1$
             "aggregate_report_count",         //$NON-NLS-1$
             "average",                        //$NON-NLS-1$
             "aggregate_report_median",        //$NON-NLS-1$
             "aggregate_report_xx_pct1_line",      //$NON-NLS-1$
             "aggregate_report_xx_pct2_line",      //$NON-NLS-1$
             "aggregate_report_xx_pct3_line",      //$NON-NLS-1$
             "aggregate_report_min",           //$NON-NLS-1$
             "aggregate_report_max",           //$NON-NLS-1$
             "aggregate_report_error%",        //$NON-NLS-1$
             "aggregate_report_rate",          //$NON-NLS-1$
             "aggregate_report_bandwidth",     //$NON-NLS-1$
             "aggregate_report_sent_bytes_per_sec"  //$NON-NLS-1$
     };
 
     private static final String[] GRAPH_COLUMNS = {"average",//$NON-NLS-1$
             "aggregate_report_median",        //$NON-NLS-1$
             "aggregate_report_xx_pct1_line",      //$NON-NLS-1$
             "aggregate_report_xx_pct2_line",      //$NON-NLS-1$
             "aggregate_report_xx_pct3_line",      //$NON-NLS-1$
             "aggregate_report_min",           //$NON-NLS-1$
             "aggregate_report_max"};          //$NON-NLS-1$
 
     private static final String TOTAL_ROW_LABEL =
         JMeterUtils.getResString("aggregate_report_total_label");       //$NON-NLS-1$
 
     private static final Font FONT_DEFAULT = UIManager.getDefaults().getFont("TextField.font"); //$NON-NLS-1$
 
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, (int) Math.round(FONT_DEFAULT.getSize() * 0.8)); //$NON-NLS-1$
 
     private JTable myJTable;
 
     private JScrollPane myScrollPane;
 
     private transient ObjectTableModel model;
 
     /**
      * Lock used to protect tableRows update + model update
      */
     private final transient Object lock = new Object();
     
     private final Map<String, SamplingStatCalculator> tableRows = new ConcurrentHashMap<>();
 
     private AxisGraph graphPanel = null;
 
     private JPanel settingsPane = null;
 
     private JSplitPane spane = null;
 
     private JTabbedPane tabbedGraph = new JTabbedPane(SwingConstants.TOP);
 
     private JButton displayButton =
         new JButton(JMeterUtils.getResString("aggregate_graph_display"));                //$NON-NLS-1$
 
     private JButton saveGraph =
         new JButton(JMeterUtils.getResString("aggregate_graph_save"));                    //$NON-NLS-1$
 
     private JButton saveTable =
         new JButton(JMeterUtils.getResString("aggregate_graph_save_table"));            //$NON-NLS-1$
 
     private JButton chooseForeColor =
         new JButton(JMeterUtils.getResString("aggregate_graph_choose_foreground_color"));            //$NON-NLS-1$
 
     private JButton syncWithName =
         new JButton(JMeterUtils.getResString("aggregate_graph_sync_with_name"));            //$NON-NLS-1$
 
     private JCheckBox saveHeaders = // should header be saved with the data?
         new JCheckBox(JMeterUtils.getResString("aggregate_graph_save_table_header"));    //$NON-NLS-1$
 
     private JLabeledTextField graphTitle =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_user_title"));    //$NON-NLS-1$
 
     private JLabeledTextField maxLengthXAxisLabel =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_max_length_xaxis_label"), 8);//$NON-NLS-1$
 
     private JLabeledTextField maxValueYAxisLabel =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_yaxis_max_value"), 8);//$NON-NLS-1$
 
     /**
      * checkbox for use dynamic graph size
      */
     private JCheckBox dynamicGraphSize = new JCheckBox(JMeterUtils.getResString("aggregate_graph_dynamic_size")); // $NON-NLS-1$
 
     private JLabeledTextField graphWidth =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_width"), 6);        //$NON-NLS-1$
     private JLabeledTextField graphHeight =
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_height"), 6);        //$NON-NLS-1$
 
     private String yAxisLabel = JMeterUtils.getResString("aggregate_graph_response_time");//$NON-NLS-1$
 
     private String yAxisTitle = JMeterUtils.getResString("aggregate_graph_ms");        //$NON-NLS-1$
 
     private boolean saveGraphToFile = false;
 
     private int defaultWidth = 400;
 
     private int defaultHeight = 300;
 
     private JComboBox<String> columnsList = new JComboBox<>(GRAPH_COLUMNS);
 
     private List<BarGraph> eltList = new ArrayList<>();
 
     private JCheckBox columnSelection = new JCheckBox(JMeterUtils.getResString("aggregate_graph_column_selection"), false); //$NON-NLS-1$
 
     private JTextField columnMatchLabel = new JTextField();
 
     private JButton applyFilterBtn = new JButton(JMeterUtils.getResString("graph_apply_filter")); // $NON-NLS-1$
 
     private JCheckBox caseChkBox = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_case"), false); // $NON-NLS-1$
 
     private JCheckBox regexpChkBox = new JCheckBox(JMeterUtils.getResString("search_text_chkbox_regexp"), true); // $NON-NLS-1$
 
     private JComboBox<String> titleFontNameList = new JComboBox<>(StatGraphProperties.getFontNameMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private JComboBox<String> titleFontSizeList = new JComboBox<>(StatGraphProperties.getFontSize());
 
     private JComboBox<String> titleFontStyleList = new JComboBox<>(StatGraphProperties.getFontStyleMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private JComboBox<String> valueFontNameList = new JComboBox<>(StatGraphProperties.getFontNameMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private JComboBox<String> valueFontSizeList = new JComboBox<>(StatGraphProperties.getFontSize());
 
     private JComboBox<String> valueFontStyleList = new JComboBox<>(StatGraphProperties.getFontStyleMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private JComboBox<String> fontNameList = new JComboBox<>(StatGraphProperties.getFontNameMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private JComboBox<String> fontSizeList = new JComboBox<>(StatGraphProperties.getFontSize());
 
     private JComboBox<String> fontStyleList = new JComboBox<>(StatGraphProperties.getFontStyleMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private JComboBox<String> legendPlacementList = new JComboBox<>(StatGraphProperties.getPlacementNameMap().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
 
     private JCheckBox drawOutlinesBar = new JCheckBox(JMeterUtils.getResString("aggregate_graph_draw_outlines"), true); // Default checked // $NON-NLS-1$
 
     private JCheckBox numberShowGrouping = new JCheckBox(JMeterUtils.getResString("aggregate_graph_number_grouping"), true); // Default checked // $NON-NLS-1$
     
     private JCheckBox valueLabelsVertical = new JCheckBox(JMeterUtils.getResString("aggregate_graph_value_labels_vertical"), true); // Default checked // $NON-NLS-1$
 
     private Color colorBarGraph = Color.YELLOW;
 
     private Color colorForeGraph = Color.BLACK;
     
     private int nbColToGraph = 1;
 
     private Pattern pattern = null;
 
     public StatGraphVisualizer() {
         super();
         model = createObjectTableModel();
         eltList.add(new BarGraph(JMeterUtils.getResString("average"), true, new Color(202, 0, 0)));
         eltList.add(new BarGraph(JMeterUtils.getResString("aggregate_report_median"), false, new Color(49, 49, 181)));
         eltList.add(new BarGraph(MessageFormat.format(JMeterUtils.getResString("aggregate_report_xx_pct1_line"),new Object[]{PCT1_LABEL}), false, new Color(42, 121, 42)));
         eltList.add(new BarGraph(MessageFormat.format(JMeterUtils.getResString("aggregate_report_xx_pct2_line"),new Object[]{PCT2_LABEL}), false, new Color(242, 226, 8)));
         eltList.add(new BarGraph(MessageFormat.format(JMeterUtils.getResString("aggregate_report_xx_pct3_line"),new Object[]{PCT3_LABEL}), false, new Color(202, 10 , 232)));
         eltList.add(new BarGraph(JMeterUtils.getResString("aggregate_report_min"), false, Color.LIGHT_GRAY));
         eltList.add(new BarGraph(JMeterUtils.getResString("aggregate_report_max"), false, Color.DARK_GRAY));
         clearData();
         init();
     }
     
     static final Object[][] getColumnsMsgParameters() { 
         Object[][] result =  { null, 
             null,
             null,
             null,
             new Object[]{PCT1_LABEL},
             new Object[]{PCT2_LABEL},
             new Object[]{PCT3_LABEL},
             null,
             null,
             null,
             null,
             null,
             null};
         return result;
     }
     
     /**
      * @return array of String containing column names
      */
     public static final String[] getColumns() {
         String[] columns = new String[COLUMNS.length];
         System.arraycopy(COLUMNS, 0, columns, 0, COLUMNS.length);
         return columns;
     }
 
     /**
      * Creates that Table model 
      * @return ObjectTableModel
      */
     static ObjectTableModel createObjectTableModel() {
         return new ObjectTableModel(COLUMNS,
                 SamplingStatCalculator.class,
                 new Functor[] {
                 new Functor("getLabel"),                    //$NON-NLS-1$
                 new Functor("getCount"),                    //$NON-NLS-1$
                 new Functor("getMeanAsNumber"),                //$NON-NLS-1$
                 new Functor("getMedian"),                    //$NON-NLS-1$
                 new Functor("getPercentPoint",                //$NON-NLS-1$
                         new Object[] { PCT1_VALUE }),
                 new Functor("getPercentPoint",                //$NON-NLS-1$
                         new Object[] { PCT2_VALUE }),
                 new Functor("getPercentPoint",                //$NON-NLS-1$
                         new Object[] { PCT3_VALUE }),
                 new Functor("getMin"),                        //$NON-NLS-1$
                 new Functor("getMax"),                         //$NON-NLS-1$
                 new Functor("getErrorPercentage"),            //$NON-NLS-1$
                 new Functor("getRate"),                        //$NON-NLS-1$
                 new Functor("getKBPerSecond"),                 //$NON-NLS-1$
                 new Functor("getSentKBPerSecond") },            //$NON-NLS-1$
                 new Functor[] { null, null, null, null, null, null, null, null, null, null, null, null, null },
                 new Class[] { String.class, Long.class, Long.class, Long.class, Long.class, 
                             Long.class, Long.class, Long.class, Long.class, Double.class,
                             Double.class, Double.class, Double.class});
     }
 
     // Column formats
     static final Format[] getFormatters() {
         return new Format[]{
             null, // Label
             null, // count
             null, // Mean
             null, // median
             null, // 90%
             null, // 95%
             null, // 99%
             null, // Min
             null, // Max
             new DecimalFormat("#0.000%"), // Error %age //$NON-NLS-1$
             new DecimalFormat("#.00000"),      // Throughput //$NON-NLS-1$
             new DecimalFormat("#0.00"),      // Throughput //$NON-NLS-1$
             new DecimalFormat("#0.00")    // pageSize   //$NON-NLS-1$
         };
     }
     
     // Column renderers
     static final TableCellRenderer[] getRenderers() {
         return new TableCellRenderer[]{
             null, // Label
             null, // count
             null, // Mean
             null, // median
             null, // 90%
             null, // 95%
             null, // 99%
             null, // Min
             null, // Max
             new NumberRenderer("#0.00%"), // Error %age //$NON-NLS-1$
             new RateRenderer("#.0"),      // Throughput //$NON-NLS-1$
             new NumberRenderer("#0.00"),      // Received bytes per sec //$NON-NLS-1$
             new NumberRenderer("#0.00"),    // Sent bytes per sec   //$NON-NLS-1$
         };
     }
     
     /**
      * 
      * @param keys I18N keys
      * @return labels
      */
     static String[] getLabels(String[] keys) {
         String[] labels = new String[keys.length];
         for (int i = 0; i < labels.length; i++) {
             labels[i]=MessageFormat.format(JMeterUtils.getResString(keys[i]), getColumnsMsgParameters()[i]);
         }
         return labels;
     }
     
     /**
      * We use this method to get the data, since we are using
      * ObjectTableModel, so the calling getDataVector doesn't
      * work as expected.
      * @param model {@link ObjectTableModel}
      * @param formats Array of {@link Format} array can contain null formatters in this case value is added as is
      * @return the data from the model
      */
     public static List<List<Object>> getAllTableData(ObjectTableModel model, Format[] formats) {
         List<List<Object>> data = new ArrayList<>();
         if (model.getRowCount() > 0) {
             for (int rw=0; rw < model.getRowCount(); rw++) {
                 int cols = model.getColumnCount();
                 List<Object> column = new ArrayList<>();
                 data.add(column);
                 for (int idx=0; idx < cols; idx++) {
                     Object val = model.getValueAt(rw,idx);
                     if(formats[idx] != null) {
                         column.add(formats[idx].format(val));
                     } else {
                         column.add(val);
                     }
                 }
             }
         }
         return data;
     }
 
     public static boolean testFunctors(){
         StatGraphVisualizer instance = new StatGraphVisualizer();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
     @Override
     public String getLabelResource() {
         return "aggregate_graph_title";                        //$NON-NLS-1$
     }
 
     @Override
     public void add(final SampleResult res) {
         final String sampleLabel = res.getSampleLabel();
         // Sampler selection
         Matcher matcher = null;
         if (columnSelection.isSelected() && pattern != null) {
             matcher = pattern.matcher(sampleLabel);
         }
         if ((matcher == null) || (matcher.find())) {
             JMeterUtils.runSafe(false, () -> {
                     SamplingStatCalculator row = null;
                     synchronized (lock) {
                         row = tableRows.get(sampleLabel);
                         if (row == null) {
                             row = new SamplingStatCalculator(sampleLabel);
                             tableRows.put(row.getLabel(), row);
                             model.insertRow(row, model.getRowCount() - 1);
                         }
                     }
                     row.addSample(res);
                     tableRows.get(TOTAL_ROW_LABEL).addSample(res);
                     model.fireTableDataChanged();
             });
         }
     }
 
     /**
      * Clears this visualizer and its model, and forces a repaint of the table.
      */
     @Override
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
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
         Border margin2 = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         mainPanel.add(makeTitlePanel());
 
         myJTable = new JTable(model);
         myJTable.setRowSorter(new ObjectTableSorter(model).fixLastRow());
         JMeterUtils.applyHiDPI(myJTable);
         // Fix centering of titles
         HeaderAsPropertyRendererWrapper.setupDefaultRenderer(myJTable, getColumnsMsgParameters());
         myJTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
         RendererUtils.applyRenderers(myJTable, getRenderers());
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
 
         // If clic on the Graph tab, make the graph (without apply interval or filter)
         tabbedGraph.addChangeListener(changeEvent -> {
             JTabbedPane srcTab = (JTabbedPane) changeEvent.getSource();
             int index = srcTab.getSelectedIndex();
             if (srcTab.getTitleAt(index).equals(JMeterUtils.getResString("aggregate_graph_tab_graph"))) { //$NON-NLS-1$
                 actionMakeGraph();
             }
         });
 
         spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         spane.setOneTouchExpandable(true);
         spane.setLeftComponent(myScrollPane);
         spane.setRightComponent(tabbedGraph);
         spane.setResizeWeight(.2);
         spane.setBorder(null); // see bug jdk 4131528
         spane.setContinuousLayout(true);
 
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(spane, BorderLayout.CENTER);
     }
 
     public void makeGraph() {
         nbColToGraph = getNbColumns();
         Dimension size = graphPanel.getSize();
         String lstr = maxLengthXAxisLabel.getText();
         // canvas size
         int width = (int) size.getWidth();
         int height = (int) size.getHeight();
         if (!dynamicGraphSize.isSelected()) {
             String wstr = graphWidth.getText();
             String hstr = graphHeight.getText();
             if (wstr.length() != 0) {
                 width = Integer.parseInt(wstr);
             }
             if (hstr.length() != 0) {
                 height = Integer.parseInt(hstr);
             }
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
         graphPanel.setXAxisTitle(JMeterUtils.getResString((String) columnsList.getSelectedItem()));
         graphPanel.setYAxisLabels(this.yAxisLabel);
         graphPanel.setYAxisTitle(this.yAxisTitle);
         graphPanel.setLegendLabels(getLegendLabels());
         graphPanel.setColor(getBackColors());
         graphPanel.setForeColor(colorForeGraph);
         graphPanel.setOutlinesBarFlag(drawOutlinesBar.isSelected());
         graphPanel.setShowGrouping(numberShowGrouping.isSelected());
         graphPanel.setValueOrientation(valueLabelsVertical.isSelected());
         graphPanel.setLegendPlacement(StatGraphProperties.getPlacementNameMap()
                 .get(legendPlacementList.getSelectedItem()).intValue());
 
         graphPanel.setTitleFont(new Font(StatGraphProperties.getFontNameMap().get(titleFontNameList.getSelectedItem()),
                 StatGraphProperties.getFontStyleMap().get(titleFontStyleList.getSelectedItem()).intValue(),
                 Integer.parseInt((String) titleFontSizeList.getSelectedItem())));
         graphPanel.setLegendFont(new Font(StatGraphProperties.getFontNameMap().get(fontNameList.getSelectedItem()),
                 StatGraphProperties.getFontStyleMap().get(fontStyleList.getSelectedItem()).intValue(),
                 Integer.parseInt((String) fontSizeList.getSelectedItem())));
         graphPanel.setValueFont(new Font(StatGraphProperties.getFontNameMap().get(valueFontNameList.getSelectedItem()),
                 StatGraphProperties.getFontStyleMap().get(valueFontStyleList.getSelectedItem()).intValue(),
                 Integer.parseInt((String) valueFontSizeList.getSelectedItem())));
 
         graphPanel.setHeight(height);
         graphPanel.setWidth(width);
         spane.repaint();
     }
 
     public double[][] getData() {
         if (model.getRowCount() > 1) {
             int count = model.getRowCount() -1;
             
             int size = nbColToGraph;
             double[][] data = new double[size][count];
             int s = 0;
             int cpt = 0;
             for (BarGraph bar : eltList) {
                 if (bar.getChkBox().isSelected()) {
                     int col = model.findColumn(columnsList.getItemAt(cpt));
                     for (int idx=0; idx < count; idx++) {
                         data[s][idx] = ((Number)model.getValueAt(idx,col)).doubleValue();
                     }
                     s++;
                 }
                 cpt++;
             }
             return data;
         }
         // API expects null, not empty array
         return null;
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
         // API expects null, not empty array
         return null;
     }
 
     private String[] getLegendLabels() {
         String[] legends = new String[nbColToGraph];
         int i = 0;
         for (BarGraph bar : eltList) {
             if (bar.getChkBox().isSelected()) {
                 legends[i] = bar.getLabel();
                 i++;
             }
         }
         return legends;
     }
 
     private Color[] getBackColors() {
         Color[] backColors = new Color[nbColToGraph];
         int i = 0;
         for (BarGraph bar : eltList) {
             if (bar.getChkBox().isSelected()) {
                 backColors[i] = bar.getBackColor();
                 i++;
             }
         }
         return backColors;
     }
 
     private int getNbColumns() {
         int i = 0;
         for (BarGraph bar : eltList) {
             if (bar.getChkBox().isSelected()) {
                 i++;
             }
         }
         return i;
     }
 
     @Override
     public void actionPerformed(ActionEvent event) {
         boolean forceReloadData = false;
         final Object eventSource = event.getSource();
         if (eventSource == displayButton) {
             actionMakeGraph();
         } else if (eventSource == saveGraph) {
             saveGraphToFile = true;
             try {
                 ActionRouter.getInstance().getAction(
                         ActionNames.SAVE_GRAPHICS,SaveGraphics.class.getName()).doAction(
                                 new ActionEvent(this,event.getID(),ActionNames.SAVE_GRAPHICS));
             } catch (Exception e) {
                 log.error("Error saving to file", e);
             }
         } else if (eventSource == saveTable) {
             JFileChooser chooser = FileDialoger.promptToSaveFile("statistics.csv");    //$NON-NLS-1$
             if (chooser == null) {
                 return;
             }
             try (FileOutputStream fo = new FileOutputStream(chooser.getSelectedFile()); 
                     OutputStreamWriter writer = new OutputStreamWriter(fo, Charset.forName("UTF-8"))){ 
                 CSVSaveService.saveCSVStats(getAllTableData(model, getFormatters()),writer,saveHeaders.isSelected() ? getLabels(COLUMNS) : null);
             } catch (IOException e) { // NOSONAR Error is reported in GUI
                 JMeterUtils.reportErrorToUser(e.getMessage(), "Error saving data");
             } 
         } else if (eventSource == chooseForeColor) {
             Color color = JColorChooser.showDialog(
                     null,
                     JMeterUtils.getResString("aggregate_graph_choose_color"), //$NON-NLS-1$
                     colorBarGraph);
             if (color != null) {
                 colorForeGraph = color;
             }
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
                 applyFilterBtn.setEnabled(true);
                 caseChkBox.setEnabled(true);
                 regexpChkBox.setEnabled(true);
             } else {
                 columnMatchLabel.setEnabled(false);
                 applyFilterBtn.setEnabled(false);
                 caseChkBox.setEnabled(false);
                 regexpChkBox.setEnabled(false);
                 // Force reload data
                 forceReloadData = true;
             }
         }
         // Not 'else if' because forceReloadData 
         if (eventSource == applyFilterBtn || forceReloadData) {
             if (columnSelection.isSelected() && columnMatchLabel.getText() != null
                     && columnMatchLabel.getText().length() > 0) {
                 pattern = createPattern(columnMatchLabel.getText());
             } else if (forceReloadData) {
                 pattern = null;
             }
             if (getFile() != null && getFile().length() > 0) {
                 clearData();
                 FilePanel filePanel = (FilePanel) getFilePanel();
                 filePanel.actionPerformed(event);
             }
         } else if (eventSource instanceof JButton) {
             // Changing color for column
             JButton btn = (JButton) eventSource;
             if (btn.getName() != null) {
                 try {
                     BarGraph bar = eltList.get(Integer.parseInt(btn.getName()));
                     Color color = JColorChooser.showDialog(null, bar.getLabel(), bar.getBackColor());
                     if (color != null) {
                         bar.setBackColor(color);
                         btn.setBackground(bar.getBackColor());
                     }
                 } catch (NumberFormatException nfe) { 
                     // nothing to do
                 } 
             }
         }
     }
 
     private void actionMakeGraph() {
         if (model.getRowCount() > 1) {
             makeGraph();
             tabbedGraph.setSelectedIndex(1);
         } else {
             JOptionPane.showMessageDialog(null, JMeterUtils
                     .getResString("aggregate_graph_no_values_to_graph"), // $NON-NLS-1$
                     JMeterUtils.getResString("aggregate_graph_no_values_to_graph"), // $NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
         }
     }
     @Override
     public JComponent getPrintableComponent() {
         if (saveGraphToFile) {
             saveGraphToFile = false;
             
             // (re)draw the graph first to take settings into account (Bug 58329)
             if (model.getRowCount() > 1) {
                 makeGraph();
             }
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
         JPanel colPanel = new JPanel();
         colPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
 
         JLabel label = new JLabel(JMeterUtils.getResString("aggregate_graph_columns_to_display")); //$NON-NLS-1$
         colPanel.add(label);
         for (BarGraph bar : eltList) {
             colPanel.add(bar.getChkBox());
             colPanel.add(createColorBarButton(bar, eltList.indexOf(bar)));
         }
         colPanel.add(Box.createRigidArea(new Dimension(5,0)));
         chooseForeColor.setFont(FONT_SMALL);
         colPanel.add(chooseForeColor);
         chooseForeColor.addActionListener(this);
 
         JPanel optionsPanel = new JPanel();
         optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         optionsPanel.add(createGraphFontValuePane());
         optionsPanel.add(drawOutlinesBar);
         optionsPanel.add(numberShowGrouping);
         optionsPanel.add(valueLabelsVertical);
         
         JPanel barPane = new JPanel(new BorderLayout());
         barPane.add(colPanel, BorderLayout.NORTH);
         barPane.add(Box.createRigidArea(new Dimension(0,3)), BorderLayout.CENTER);
         barPane.add(optionsPanel, BorderLayout.SOUTH);
 
         JPanel columnPane = new JPanel(new BorderLayout());
         columnPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_column_settings"))); // $NON-NLS-1$
         columnPane.add(barPane, BorderLayout.NORTH);
         columnPane.add(Box.createRigidArea(new Dimension(0,3)), BorderLayout.CENTER);
         columnPane.add(createGraphSelectionSubPane(), BorderLayout.SOUTH);
         
         return columnPane;
     }
 
     private JButton createColorBarButton(BarGraph barGraph, int index) {
         // Button
         JButton colorBtn = new JButton();
         colorBtn.setName(String.valueOf(index));
         colorBtn.setFont(FONT_SMALL);
         colorBtn.addActionListener(this);
         colorBtn.setBackground(barGraph.getBackColor());
         return colorBtn;
     }
 
     private JPanel createGraphSelectionSubPane() {
         // Search field
         JPanel searchPanel = new JPanel();
         searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
         searchPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
 
         searchPanel.add(columnSelection);
         columnMatchLabel.setEnabled(false);
         applyFilterBtn.setEnabled(false);
         caseChkBox.setEnabled(false);
         regexpChkBox.setEnabled(false);
         columnSelection.addActionListener(this);
 
         searchPanel.add(columnMatchLabel);
         searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
 
         // Button
         applyFilterBtn.setFont(FONT_SMALL);
         applyFilterBtn.addActionListener(this);
         searchPanel.add(applyFilterBtn);
 
         // checkboxes
         caseChkBox.setFont(FONT_SMALL);
         searchPanel.add(caseChkBox);
         regexpChkBox.setFont(FONT_SMALL);
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
         titleStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_font"), //$NON-NLS-1$
                 titleFontNameList));
         titleFontNameList.setSelectedIndex(0); // default: sans serif
         titleStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_size"), //$NON-NLS-1$
                 titleFontSizeList));
         titleFontSizeList.setSelectedItem(StatGraphProperties.getFontSize()[6]); // default: 16
         titleStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_style"), //$NON-NLS-1$
                 titleFontStyleList));
         titleFontStyleList.setSelectedItem(JMeterUtils.getResString("fontstyle.bold"));  // $NON-NLS-1$ // default: bold
 
         JPanel titlePane = new JPanel(new BorderLayout());
         titlePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("aggregate_graph_title_group"))); // $NON-NLS-1$
         titlePane.add(titleNamePane, BorderLayout.NORTH);
         titlePane.add(titleStylePane, BorderLayout.SOUTH);
         return titlePane;
     }
 
     private JPanel createGraphFontValuePane() {       
         JPanel fontValueStylePane = new JPanel();
         fontValueStylePane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         fontValueStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_value_font"), //$NON-NLS-1$
                 valueFontNameList));
         valueFontNameList.setSelectedIndex(0); // default: sans serif
         fontValueStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_size"), //$NON-NLS-1$
                 valueFontSizeList));
         valueFontSizeList.setSelectedItem(StatGraphProperties.getFontSize()[2]); // default: 10
         fontValueStylePane.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_style"), //$NON-NLS-1$
                 valueFontStyleList));
         valueFontStyleList.setSelectedItem(JMeterUtils.getResString("fontstyle.normal")); // default: normal //$NON-NLS-1$
 
         return fontValueStylePane;
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
 
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_legend_placement"), //$NON-NLS-1$
                 legendPlacementList));
         legendPlacementList.setSelectedItem(JMeterUtils.getResString("aggregate_graph_legend.placement.bottom"));  // $NON-NLS-1$ // default: bottom
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_font"), //$NON-NLS-1$
                 fontNameList));
         fontNameList.setSelectedIndex(0); // default: sans serif
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_size"), //$NON-NLS-1$
                 fontSizeList));
         fontSizeList.setSelectedItem(StatGraphProperties.getFontSize()[2]); // default: 10
         legendPanel.add(GuiUtils.createLabelCombo(JMeterUtils.getResString("aggregate_graph_style"), //$NON-NLS-1$
                 fontStyleList));
         fontStyleList.setSelectedItem(JMeterUtils.getResString("fontstyle.normal"));  // $NON-NLS-1$ // default: normal
 
         return legendPanel;
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
         Pattern result = null;
         try {
             if (caseChkBox.isSelected()) {
                 result = Pattern.compile(textToFindQ);
             } else {
                 result = Pattern.compile(textToFindQ, Pattern.CASE_INSENSITIVE);
             }
         } catch (PatternSyntaxException pse) {
             return null;
         }
         return result;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/XMLDefaultMutableTreeNode.java b/src/components/org/apache/jmeter/visualizers/XMLDefaultMutableTreeNode.java
index 939ce56d9..63baa7cd0 100644
--- a/src/components/org/apache/jmeter/visualizers/XMLDefaultMutableTreeNode.java
+++ b/src/components/org/apache/jmeter/visualizers/XMLDefaultMutableTreeNode.java
@@ -1,210 +1,210 @@
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
 
 import javax.swing.tree.DefaultMutableTreeNode;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.dom.Attr;
 import org.w3c.dom.CDATASection;
 import org.w3c.dom.Comment;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.dom.Text;
 import org.xml.sax.SAXException;
 
 /**
  * A extended class of DefaultMutableTreeNode except that it also attached XML
  * node and convert XML document into DefaultMutableTreeNode.
  *
  */
 public class XMLDefaultMutableTreeNode extends DefaultMutableTreeNode {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XMLDefaultMutableTreeNode.class);
     private transient Node xmlNode;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public XMLDefaultMutableTreeNode(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
 
     public XMLDefaultMutableTreeNode(Node root) throws SAXException {
         super(root.getNodeName());
         initAttributeNode(root, this);
         initRoot(root);
 
     }
 
     public XMLDefaultMutableTreeNode(String name, Node xmlNode) {
         super(name);
         this.xmlNode = xmlNode;
 
     }
 
     /**
      * init root
      *
      * @param xmlRoot
      * @throws SAXException
      */
     private void initRoot(Node xmlRoot) throws SAXException {
 
         NodeList childNodes = xmlRoot.getChildNodes();
         for (int i = 0; i < childNodes.getLength(); i++) {
             Node childNode = childNodes.item(i);
             initNode(childNode, this);
         }
 
     }
 
     /**
      * init node
      *
      * @param node
      * @param mTreeNode
      * @throws SAXException
      */
     private void initNode(Node node, XMLDefaultMutableTreeNode mTreeNode) throws SAXException {
 
         switch (node.getNodeType()) {
         case Node.ELEMENT_NODE:
             initElementNode(node, mTreeNode);
             break;
 
         case Node.TEXT_NODE:
             initTextNode((Text) node, mTreeNode);
             break;
 
         case Node.CDATA_SECTION_NODE:
             initCDATASectionNode((CDATASection) node, mTreeNode);
             break;
         case Node.COMMENT_NODE:
             initCommentNode((Comment) node, mTreeNode);
             break;
 
         default:
             // if other node type, we will just skip it
             break;
 
         }
 
     }
 
     /**
      * init element node
      *
      * @param node
      * @param mTreeNode
      * @throws SAXException
      */
     private void initElementNode(Node node, DefaultMutableTreeNode mTreeNode) throws SAXException {
         String nodeName = node.getNodeName();
 
         NodeList childNodes = node.getChildNodes();
         XMLDefaultMutableTreeNode childTreeNode = new XMLDefaultMutableTreeNode(nodeName, node);
 
         mTreeNode.add(childTreeNode);
         initAttributeNode(node, childTreeNode);
         for (int i = 0; i < childNodes.getLength(); i++) {
             Node childNode = childNodes.item(i);
             initNode(childNode, childTreeNode);
         }
 
     }
 
     /**
      * init attribute node
      *
      * @param node
      * @param mTreeNode
      * @throws SAXException
      */
     private void initAttributeNode(Node node, DefaultMutableTreeNode mTreeNode) throws SAXException {
         NamedNodeMap nm = node.getAttributes();
         for (int i = 0; i < nm.getLength(); i++) {
             Attr nmNode = (Attr) nm.item(i);
             String value = nmNode.getName() + " = \"" + nmNode.getValue() + "\""; // $NON-NLS-1$ $NON-NLS-2$
             XMLDefaultMutableTreeNode attributeNode = new XMLDefaultMutableTreeNode(value, nmNode);
             mTreeNode.add(attributeNode);
 
         }
     }
 
     /**
      * init comment Node
      *
      * @param node
      * @param mTreeNode
      * @throws SAXException
      */
     private void initCommentNode(Comment node, DefaultMutableTreeNode mTreeNode) throws SAXException {
         String data = node.getData();
         if (data != null && data.length() > 0) {
             String value = "<!--" + node.getData() + "-->"; // $NON-NLS-1$ $NON-NLS-2$
             XMLDefaultMutableTreeNode commentNode = new XMLDefaultMutableTreeNode(value, node);
             mTreeNode.add(commentNode);
         }
     }
 
     /**
      * init CDATASection Node
      *
      * @param node
      * @param mTreeNode
      * @throws SAXException
      */
     private void initCDATASectionNode(CDATASection node, DefaultMutableTreeNode mTreeNode) throws SAXException {
         String data = node.getData();
         if (data != null && data.length() > 0) {
             String value = "<!-[CDATA" + node.getData() + "]]>"; // $NON-NLS-1$ $NON-NLS-2$
             XMLDefaultMutableTreeNode commentNode = new XMLDefaultMutableTreeNode(value, node);
             mTreeNode.add(commentNode);
         }
     }
 
     /**
      * init the TextNode
      *
      * @param node
      * @param mTreeNode
      * @throws SAXException
      */
     private void initTextNode(Text node, DefaultMutableTreeNode mTreeNode) throws SAXException {
         String text = node.getNodeValue().trim();
         if (text != null && text.length() > 0) {
             XMLDefaultMutableTreeNode textNode = new XMLDefaultMutableTreeNode(text, node);
             mTreeNode.add(textNode);
         }
     }
 
     /**
      * get the xml node
      *
      * @return the XML node
      */
     public Node getXMLNode() {
         return xmlNode;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/AbstractBackendListenerClient.java b/src/components/org/apache/jmeter/visualizers/backend/AbstractBackendListenerClient.java
index 202ee2bdc..62df851d8 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/AbstractBackendListenerClient.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/AbstractBackendListenerClient.java
@@ -1,130 +1,134 @@
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
 
 package org.apache.jmeter.visualizers.backend;
 
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.samplers.SampleResult;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * An abstract implementation of the BackendListenerClient interface. This
  * implementation provides default implementations of most of the methods in the
  * interface, as well as some convenience methods, in order to simplify
  * development of BackendListenerClient implementations.
  * 
  * While it may be necessary to make changes to the BackendListenerClient interface
  * from time to time (therefore requiring changes to any implementations of this
  * interface), we intend to make this abstract class provide reasonable
  * implementations of any new methods so that subclasses do not necessarily need
  * to be updated for new versions. Therefore, when creating a new
  * BackendListenerClient implementation, developers are encouraged to subclass this
  * abstract class rather than implementing the BackendListenerClient interface
  * directly. Implementing BackendListenerClient directly will continue to be
  * supported for cases where extending this class is not possible (for example,
  * when the client class is already a subclass of some other class).
  * <p>
  * The {@link BackendListenerClient#handleSampleResults(java.util.List, BackendListenerContext)}
  * method of BackendListenerClient does not have a default
  * implementation here, so subclasses must define at least this method. It may
  * be useful to override other methods as well.
  *
  * @see BackendListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
  * @since 2.13
  */
 public abstract class AbstractBackendListenerClient implements BackendListenerClient {
 
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AbstractBackendListenerClient.class);
     private UserMetric userMetrics = new UserMetric();
     
     private ConcurrentHashMap<String, SamplerMetric> metricsPerSampler = new ConcurrentHashMap<>();
 
     /* Implements BackendListenerClient.setupTest(BackendListenerContext) */
     @Override
     public void setupTest(BackendListenerContext context) throws Exception {
-        LOGGER.debug(getClass().getName() + ": setupTest");
+        if(log.isDebugEnabled()) {
+            log.debug("{}: setupTest", getClass().getName());
+        }
         metricsPerSampler.clear();
         userMetrics.clear();
     }
 
     /* Implements BackendListenerClient.teardownTest(BackendListenerContext) */
     @Override
     public void teardownTest(BackendListenerContext context) throws Exception {
-        LOGGER.debug(getClass().getName() + ": teardownTest");
+        if(log.isDebugEnabled()) {
+            log.debug("{}: teardownTest", getClass().getName());
+        }
         metricsPerSampler.clear();
         userMetrics.clear();
     }
 
     /* Implements BackendListenerClient.getDefaultParameters() */
     @Override
     public Arguments getDefaultParameters() {
         return null;
     }
 
     /**
      * Get a Logger instance which can be used by subclasses to log information.
      * As this class is designed to be subclassed this is useful.
      *
      * @return a Logger instance which can be used for logging
      */
     protected Logger getLogger() {
-        return LOGGER;
+        return log;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public SampleResult createSampleResult(BackendListenerContext context, SampleResult result) {
         return result;
     }
 
     /**
      * @param sampleLabel Name of sample used as key
      * @return {@link SamplerMetric}
      */
     protected final SamplerMetric getSamplerMetric(String sampleLabel) {
         SamplerMetric samplerMetric = metricsPerSampler.get(sampleLabel);
         if(samplerMetric == null) {
             samplerMetric = new SamplerMetric();
             SamplerMetric oldValue = metricsPerSampler.putIfAbsent(sampleLabel, samplerMetric);
             if(oldValue != null ){
                 samplerMetric = oldValue;
             }
         }
         return samplerMetric;
     }
     
     /**
      * @return Map where key is SampleLabel and {@link SamplerMetric} is the metrics of this Sample
      */
     protected Map<String, SamplerMetric> getMetricsPerSampler() {
         return metricsPerSampler;
     }
 
     /**
      * @return {@link UserMetric}
      */
     protected UserMetric getUserMetrics() {
         return userMetrics;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/BackendListener.java b/src/components/org/apache/jmeter/visualizers/backend/BackendListener.java
index 353ecf094..1d3654ec9 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/BackendListener.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/BackendListener.java
@@ -1,493 +1,499 @@
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
 
 package org.apache.jmeter.visualizers.backend;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ArrayBlockingQueue;
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.concurrent.locks.LockSupport;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.visualizers.backend.graphite.GraphiteBackendListenerClient;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Async Listener that delegates SampleResult handling to implementations of {@link BackendListenerClient}
  * @since 2.13
  */
 public class BackendListener extends AbstractTestElement
     implements Backend, Serializable, SampleListener, 
         TestStateListener, NoThreadClone, Remoteable {
 
     /**
      * 
      */
     private static final class ListenerClientData {
         private BackendListenerClient client;
         private BlockingQueue<SampleResult> queue;
         private AtomicLong queueWaits; // how many times we had to wait to queue a SampleResult        
         private AtomicLong queueWaitTime; // how long we had to wait (nanoSeconds)
         // @GuardedBy("LOCK")
         private int instanceCount; // number of active tests
         private CountDownLatch latch;
     }
 
     /**
      * 
      */
-    private static final long serialVersionUID = 8184103677832024335L;
+    private static final long serialVersionUID = 1L;
 
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BackendListener.class);
 
     /**
      * Property key representing the classname of the BackendListenerClient to user.
      */
     public static final String CLASSNAME = "classname";
 
     /**
      * Queue size
      */
     public static final String QUEUE_SIZE = "QUEUE_SIZE";
 
     /**
      * Lock used to protect accumulators update + instanceCount update
      */
     private static final Object LOCK = new Object();
 
     /**
      * Property key representing the arguments for the BackendListenerClient.
      */
     public static final String ARGUMENTS = "arguments";
 
     /**
      * The BackendListenerClient class used by this sampler.
      * Created by testStarted; copied to cloned instances.
      */
     private Class<?> clientClass;
 
     public static final String DEFAULT_QUEUE_SIZE = "5000";
 
     // Create unique object as marker for end of queue
     private static transient final SampleResult FINAL_SAMPLE_RESULT = new SampleResult();
 
     /*
      * This is needed for distributed testing where there is 1 instance
      * per server. But we need the total to be shared.
      */
     //@GuardedBy("LOCK") - needed to ensure consistency between this and instanceCount
     private static final Map<String, ListenerClientData> queuesByTestElementName =
             new ConcurrentHashMap<>();
 
     // Name of the test element. Set up by testStarted().
     private transient String myName;
 
     // Holds listenerClientData for this test element
     private transient ListenerClientData listenerClientData;
 
     /**
      * Create a BackendListener.
      */
     public BackendListener() {
         synchronized (LOCK) {
             queuesByTestElementName.clear();
         }
 
         setArguments(new Arguments());
     }
 
     /*
      * Ensure that the required class variables are cloned,
      * as this is not currently done by the super-implementation.
      */
     @Override
     public Object clone() {
         BackendListener clone = (BackendListener) super.clone();
         clone.clientClass = this.clientClass;
         return clone;
     }
 
     private Class<?> initClass() {
         String name = getClassname().trim();
         try {
             return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
         } catch (Exception e) {
-            LOGGER.error(whoAmI() + "\tException initialising: " + name, e);
+            log.error("{}\tException initialising: {}", whoAmI(), name, e);
         }
         return null;
     }
 
     /**
      * Generate a String identifier of this instance for debugging purposes.
      *
      * @return a String identifier for this sampler instance
      */
     private String whoAmI() {
         StringBuilder sb = new StringBuilder();
         sb.append(Thread.currentThread().getName());
         sb.append("@");
         sb.append(Integer.toHexString(hashCode()));
         sb.append("-");
         sb.append(getName());
         return sb.toString();
     }
 
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
      */
     @Override
     public void sampleOccurred(SampleEvent event) {
         Arguments args = getArguments();
         BackendListenerContext context = new BackendListenerContext(args);
 
         SampleResult sr = listenerClientData.client.createSampleResult(context, event.getResult());
         if(sr == null) {
-            if(LOGGER.isDebugEnabled()) {
-                LOGGER.debug(getName()+"=>Dropping SampleResult:"+event.getResult());
+            if (log.isDebugEnabled()) {
+                log.debug("{} => Dropping SampleResult: {}", getName(), event.getResult());
             }
             return;
         }
         try {
             if (!listenerClientData.queue.offer(sr)){ // we failed to add the element first time
                 listenerClientData.queueWaits.incrementAndGet();
                 long t1 = System.nanoTime();
                 listenerClientData.queue.put(sr);
                 long t2 = System.nanoTime();
                 listenerClientData.queueWaitTime.addAndGet(t2-t1);
             }
         } catch (Exception err) {
-            LOGGER.error("sampleOccurred, failed to queue the sample", err);
+            log.error("sampleOccurred, failed to queue the sample", err);
         }
     }
 
     /**
      * Thread that dequeus data from queue to send it to {@link BackendListenerClient}
      */
     private static final class Worker extends Thread {
 
         private final ListenerClientData listenerClientData;
         private final BackendListenerContext context;
         private final BackendListenerClient backendListenerClient;
         private Worker(BackendListenerClient backendListenerClient, Arguments arguments, ListenerClientData listenerClientData){
             this.listenerClientData = listenerClientData;
             // Allow BackendListenerClient implementations to get access to test element name
             arguments.addArgument(TestElement.NAME, getName());
             context = new BackendListenerContext(arguments);
             this.backendListenerClient = backendListenerClient;
         }
 
         @Override
         public void run() {
-            boolean isDebugEnabled = LOGGER.isDebugEnabled();
+            final boolean isDebugEnabled = log.isDebugEnabled();
             List<SampleResult> sampleResults = new ArrayList<>(listenerClientData.queue.size());
             try {
                 try {
 
                     boolean endOfLoop = false;
                     while (!endOfLoop) {
-                        if(isDebugEnabled) {
-                            LOGGER.debug("Thread:"+Thread.currentThread().getName()+" taking SampleResult from queue:"+listenerClientData.queue.size());
+                        if (isDebugEnabled) {
+                            log.debug("Thread: {} taking SampleResult from queue: {}", Thread.currentThread().getName(),
+                                    listenerClientData.queue.size());
                         }
                         SampleResult sampleResult = listenerClientData.queue.take();
-                        if(isDebugEnabled) {
-                            LOGGER.debug("Thread:"+Thread.currentThread().getName()+" took SampleResult:"+sampleResult+", isFinal:" + (sampleResult==FINAL_SAMPLE_RESULT));
+                        if (isDebugEnabled) {
+                            log.debug("Thread: {} took SampleResult: {}, isFinal: {}", Thread.currentThread().getName(),
+                                    sampleResult, (sampleResult == FINAL_SAMPLE_RESULT));
                         }
                         while (!(endOfLoop = (sampleResult == FINAL_SAMPLE_RESULT)) && sampleResult != null ) { // try to process as many as possible
                             sampleResults.add(sampleResult);
-                            if(isDebugEnabled) {
-                                LOGGER.debug("Thread:"+Thread.currentThread().getName()+" polling from queue:"+listenerClientData.queue.size());
+                            if (isDebugEnabled) {
+                                log.debug("Thread: {} polling from queue: {}", Thread.currentThread().getName(),
+                                        listenerClientData.queue.size());
                             }
                             sampleResult = listenerClientData.queue.poll(); // returns null if nothing on queue currently
-                            if(isDebugEnabled) {
-                                LOGGER.debug("Thread:"+Thread.currentThread().getName()+" took from queue:"+sampleResult+", isFinal:" + (sampleResult==FINAL_SAMPLE_RESULT));
+                            if (isDebugEnabled) {
+                                log.debug("Thread: {} took from queue: {}, isFinal:", Thread.currentThread().getName(),
+                                        sampleResult, (sampleResult == FINAL_SAMPLE_RESULT));
                             }
                         }
-                        if(isDebugEnabled) {
-                            LOGGER.debug("Thread:"+Thread.currentThread().getName()+
-                                    " exiting with FINAL EVENT:"+(sampleResult == FINAL_SAMPLE_RESULT)
-                                    +", null:" + (sampleResult==null));
+                        if (isDebugEnabled) {
+                            log.debug("Thread: {} exiting with FINAL EVENT: {}, null: {}",
+                                    Thread.currentThread().getName(), sampleResult == FINAL_SAMPLE_RESULT,
+                                    sampleResult == null);
                         }
                         sendToListener(backendListenerClient, context, sampleResults);
                         if(!endOfLoop) {
                             LockSupport.parkNanos(100);
                         }
                     }
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 }
                 // We may have been interrupted
                 sendToListener(backendListenerClient, context, sampleResults);
-                LOGGER.info("Worker ended");
+                log.info("Worker ended");
             } finally {
                 listenerClientData.latch.countDown();
             }
         }
     }
 
     /**
      * Send sampleResults to {@link BackendListenerClient}
      * @param backendListenerClient {@link BackendListenerClient}
      * @param context {@link BackendListenerContext}
      * @param sampleResults List of {@link SampleResult}
      */
     static void sendToListener(
             final BackendListenerClient backendListenerClient,
             final BackendListenerContext context,
             final List<SampleResult> sampleResults) {
         if (!sampleResults.isEmpty()) {
             backendListenerClient.handleSampleResults(sampleResults, context);
             sampleResults.clear();
         }
     }
 
     /**
      * Returns reference to {@link BackendListener}
      * @param clientClass {@link BackendListenerClient} client class
      * @return BackendListenerClient reference.
      */
     static BackendListenerClient createBackendListenerClientImpl(Class<?> clientClass) {
         if (clientClass == null) { // failed to initialise the class
             return new ErrorBackendListenerClient();
         }
         try {
             return (BackendListenerClient) clientClass.newInstance();
         } catch (Exception e) {
-            LOGGER.error("Exception creating: " + clientClass, e);
+            log.error("Exception creating: {}", clientClass, e);
             return new ErrorBackendListenerClient();
         }
     }
 
     // TestStateListener implementation
     /**
      *  Implements TestStateListener.testStarted() 
      **/
     @Override
     public void testStarted() {
         testStarted("local"); //$NON-NLS-1$
     }
 
     /** Implements TestStateListener.testStarted(String) 
      **/
     @Override
     public void testStarted(String host) {
-        if(LOGGER.isDebugEnabled()){
-            LOGGER.debug(whoAmI() + "\ttestStarted(" + host + ")");
+        if (log.isDebugEnabled()) {
+            log.debug("{}\ttestStarted({})", whoAmI(), host);
         }
 
         int queueSize;
         final String size = getQueueSize();
         try {
             queueSize = Integer.parseInt(size);
         } catch (NumberFormatException nfe) {
-            LOGGER.warn("Invalid queue size '" + size + "' defaulting to " + DEFAULT_QUEUE_SIZE);
+            log.warn("Invalid queue size '{}' defaulting to {}", size, DEFAULT_QUEUE_SIZE);
             queueSize = Integer.parseInt(DEFAULT_QUEUE_SIZE);
         }
 
         synchronized (LOCK) {
             myName = getName();
             listenerClientData = queuesByTestElementName.get(myName);
             if (listenerClientData == null){
                 // We need to do this to ensure in Distributed testing 
                 // that only 1 instance of BackendListenerClient is used
                 clientClass = initClass(); // may be null
                 BackendListenerClient backendListenerClient = createBackendListenerClientImpl(clientClass);
                 BackendListenerContext context = new BackendListenerContext((Arguments)getArguments().clone());
 
                 listenerClientData = new ListenerClientData();
                 listenerClientData.queue = new ArrayBlockingQueue<>(queueSize);
                 listenerClientData.queueWaits = new AtomicLong(0L);
                 listenerClientData.queueWaitTime = new AtomicLong(0L);
                 listenerClientData.latch = new CountDownLatch(1);
                 listenerClientData.client = backendListenerClient;
-                LOGGER.info(getName()+":Starting worker with class:"+clientClass +" and queue capacity:"+getQueueSize());
+                log.info("{}: Starting worker with class: {} and queue capacity: {}", getName(), clientClass,
+                        getQueueSize());
                 Worker worker = new Worker(backendListenerClient, (Arguments) getArguments().clone(), listenerClientData);
                 worker.setDaemon(true);
                 worker.start();
-                LOGGER.info(getName()+": Started  worker with class:"+clientClass);
+                log.info("{}: Started  worker with class: {}", getName(), clientClass);
                 try {
                     backendListenerClient.setupTest(context);
                 } catch (Exception e) {
                     throw new java.lang.IllegalStateException("Failed calling setupTest", e);
                 }
                 queuesByTestElementName.put(myName, listenerClientData);
             }
             listenerClientData.instanceCount++;
         }
     }
 
     /**
      * Method called at the end of the test. This is called only on one instance
      * of BackendListener. This method will loop through all of the other
      * BackendListenerClients which have been registered (automatically in the
      * constructor) and notify them that the test has ended, allowing the
      * BackendListenerClients to cleanup.
      * Implements TestStateListener.testEnded(String)
      */
     @Override
     public void testEnded(String host) {
         synchronized (LOCK) {
             ListenerClientData listenerClientDataForName = queuesByTestElementName.get(myName);
-            if(LOGGER.isDebugEnabled()) {
-                LOGGER.debug("testEnded called on instance "+myName+"#"+listenerClientDataForName.instanceCount);
+            if (log.isDebugEnabled()) {
+                log.debug("testEnded called on instance {}#{}", myName, listenerClientDataForName.instanceCount);
             }
             listenerClientDataForName.instanceCount--;
             if (listenerClientDataForName.instanceCount > 0){
                 // Not the last instance of myName
                 return;
             }
         }
         try {
             listenerClientData.queue.put(FINAL_SAMPLE_RESULT);
         } catch (Exception ex) {
-            LOGGER.warn("testEnded() with exception:"+ex.getMessage(), ex);
+            log.warn("testEnded() with exception: {}", ex.getMessage(), ex);
         }
         if (listenerClientData.queueWaits.get() > 0) {
-            LOGGER.warn("QueueWaits: "+listenerClientData.queueWaits+"; QueueWaitTime: "+listenerClientData.queueWaitTime+
-                    " (nanoseconds), you may need to increase queue capacity, see property 'backend_queue_capacity'");
+            log.warn(
+                    "QueueWaits: {}; QueueWaitTime: {} (nanoseconds), you may need to increase queue capacity, see property 'backend_queue_capacity'",
+                    listenerClientData.queueWaits, listenerClientData.queueWaitTime);
         }
         try {
             listenerClientData.latch.await();
             BackendListenerContext context = new BackendListenerContext(getArguments());
             listenerClientData.client.teardownTest(context);
         } catch (Exception e) {
             throw new java.lang.IllegalStateException("Failed calling teardownTest", e);
         }
     }
 
     /** Implements TestStateListener.testEnded(String)
      **/
     @Override
     public void testEnded() {
         testEnded("local"); //$NON-NLS-1$
     }
 
     /**
      * A {@link BackendListenerClient} implementation used for error handling. If an
      * error occurs while creating the real BackendListenerClient object, it is
      * replaced with an instance of this class. Each time a sample occurs with
      * this class, the result is marked as a failure so the user can see that
      * the test failed.
      */
     static class ErrorBackendListenerClient extends AbstractBackendListenerClient {
         /**
          * Return SampleResult with data on error.
          *
          * @see BackendListenerClient#handleSampleResults(List, BackendListenerContext)
          */
         @Override
         public void handleSampleResults(List<SampleResult> sampleResults, BackendListenerContext context) {
-            LOGGER.warn("ErrorBackendListenerClient#handleSampleResult called, noop");
+            log.warn("ErrorBackendListenerClient#handleSampleResult called, noop");
             Thread.yield();
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.samplers.SampleListener#sampleStarted(org.apache.jmeter.samplers.SampleEvent)
      */
     @Override
     public void sampleStarted(SampleEvent e) {
         // NOOP
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.samplers.SampleListener#sampleStopped(org.apache.jmeter.samplers.SampleEvent)
      */
     @Override
     public void sampleStopped(SampleEvent e) {
         // NOOP
     }
 
     /**
      * Set the arguments (parameters) for the BackendListenerClient to be executed
      * with.
      *
      * @param args
      *            the new arguments. These replace any existing arguments.
      */
     public void setArguments(Arguments args) {
         // Bug 59173 - don't save new default argument
         args.removeArgument(GraphiteBackendListenerClient.USE_REGEXP_FOR_SAMPLERS_LIST, 
                 GraphiteBackendListenerClient.USE_REGEXP_FOR_SAMPLERS_LIST_DEFAULT);
         setProperty(new TestElementProperty(ARGUMENTS, args));
     }
 
     /**
      * Get the arguments (parameters) for the BackendListenerClient to be executed
      * with.
      *
      * @return the arguments
      */
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * Sets the Classname of the BackendListenerClient object
      *
      * @param classname
      *            the new Classname value
      */
     public void setClassname(String classname) {
         setProperty(CLASSNAME, classname);
     }
 
     /**
      * Gets the Classname of the BackendListenerClient object
      *
      * @return the Classname value
      */
     public String getClassname() {
         return getPropertyAsString(CLASSNAME);
     }
 
     /**
      * Sets the queue size
      *
      * @param queueSize the size of the queue
      *
      */
     public void setQueueSize(String queueSize) {
         setProperty(QUEUE_SIZE, queueSize, DEFAULT_QUEUE_SIZE);
     }
 
     /**
      * Gets the queue size
      *
      * @return int queueSize
      */
     public String getQueueSize() {
         return getPropertyAsString(QUEUE_SIZE, DEFAULT_QUEUE_SIZE);
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/BackendListenerContext.java b/src/components/org/apache/jmeter/visualizers/backend/BackendListenerContext.java
index ab7f1a023..af10face2 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/BackendListenerContext.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/BackendListenerContext.java
@@ -1,236 +1,236 @@
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
 
 package org.apache.jmeter.visualizers.backend;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.apache.jmeter.config.Arguments;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * BackendListenerContext is used to provide context information to a
  * BackendListenerClient implementation. This currently consists of the
  * initialization parameters which were specified in the GUI. 
  * @since 2.13
  */
 public class BackendListenerContext {
     /*
      * Implementation notes:
      *
      * All of the methods in this class are currently read-only. If update
      * methods are included in the future, they should be defined so that a
      * single instance of BackendListenerContext can be associated with each thread.
      * Therefore, no synchronization should be needed. The same instance should
      * be used for the call to setupTest, all calls to runTest, and the call to
      * teardownTest.
      */
 
     /** Logging */
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BackendListenerContext.class);
 
     /**
      * Map containing the initialization parameters for the BackendListenerClient.
      */
     private final Map<String, String> params;
 
     /**
      *
      * @param args
      *            the initialization parameters.
      */
     public BackendListenerContext(Arguments args) {
         this.params = args.getArgumentsAsMap();
     }
 
     /**
      * Determine whether or not a value has been specified for the parameter
      * with this name.
      *
      * @param name
      *            the name of the parameter to test
      * @return true if the parameter value has been specified, false otherwise.
      */
     public boolean containsParameter(String name) {
         return params.containsKey(name);
     }
 
     /**
      * Get an iterator of the parameter names. Each entry in the Iterator is a
      * String.
      *
      * @return an Iterator of Strings listing the names of the parameters which
      *         have been specified for this test.
      */
     public Iterator<String> getParameterNamesIterator() {
         return params.keySet().iterator();
     }
 
     /**
      * Get the value of a specific parameter as a String, or null if the value
      * was not specified.
      *
      * @param name
      *            the name of the parameter whose value should be retrieved
      * @return the value of the parameter, or null if the value was not
      *         specified
      */
     public String getParameter(String name) {
         return getParameter(name, null);
     }
 
     /**
      * Get the value of a specified parameter as a String, or return the
      * specified default value if the value was not specified.
      *
      * @param name
      *            the name of the parameter whose value should be retrieved
      * @param defaultValue
      *            the default value to return if the value of this parameter was
      *            not specified
      * @return the value of the parameter, or the default value if the parameter
      *         was not specified
      */
     public String getParameter(String name, String defaultValue) {
         if (params == null || !params.containsKey(name)) {
             return defaultValue;
         }
         return params.get(name);
     }
 
     /**
      * Get the value of a specified parameter as an integer. An exception will
      * be thrown if the parameter is not specified or if it is not an integer.
      * The value may be specified in decimal, hexadecimal, or octal, as defined
      * by Integer.decode().
      *
      * @param name
      *            the name of the parameter whose value should be retrieved
      * @return the value of the parameter
      *
      * @throws NumberFormatException
      *             if the parameter is not specified or is not an integer
      *
      * @see java.lang.Integer#decode(java.lang.String)
      */
     public int getIntParameter(String name) throws NumberFormatException {
         if (params == null || !params.containsKey(name)) {
             throw new IllegalArgumentException("No value for parameter named '" + name + "'.");
         }
 
         return Integer.parseInt(params.get(name));
     }
 
     /**
      * Get the value of a specified parameter as an integer, or return the
      * specified default value if the value was not specified or is not an
      * integer. A warning will be logged if the value is not an integer. The
      * value may be specified in decimal, hexadecimal, or octal, as defined by
      * Integer.decode().
      *
      * @param name
      *            the name of the parameter whose value should be retrieved
      * @param defaultValue
      *            the default value to return if the value of this parameter was
      *            not specified
      * @return the value of the parameter, or the default value if the parameter
      *         was not specified
      *
      * @see java.lang.Integer#decode(java.lang.String)
      */
     public int getIntParameter(String name, int defaultValue) {
         if (params == null || !params.containsKey(name)) {
             return defaultValue;
         }
 
         try {
             return Integer.parseInt(params.get(name));
         } catch (NumberFormatException e) {
-            LOGGER.warn("Value for parameter '" + name + "' not an integer: '" + params.get(name) + "'.  Using default: '"
-                    + defaultValue + "'.", e);
+            log.warn("Value for parameter '{}' not an integer: '{}'.  Using default: '{}'.", name, params.get(name),
+                    defaultValue, e);
             return defaultValue;
         }
     }
 
     /**
      * Get the value of a specified parameter as a long. An exception will be
      * thrown if the parameter is not specified or if it is not a long. The
      * value may be specified in decimal, hexadecimal, or octal, as defined by
      * Long.decode().
      *
      * @param name
      *            the name of the parameter whose value should be retrieved
      * @return the value of the parameter
      *
      * @throws NumberFormatException
      *             if the parameter is not specified or is not a long
      *
      * @see Long#decode(String)
      */
     public long getLongParameter(String name) throws NumberFormatException {
         if (params == null || !params.containsKey(name)) {
             throw new NumberFormatException("No value for parameter named '" + name + "'.");
         }
 
         return Long.parseLong(params.get(name));
     }
 
     /**
      * Get the value of a specified parameter as along, or return the specified
      * default value if the value was not specified or is not a long. A warning
      * will be logged if the value is not a long. The value may be specified in
      * decimal, hexadecimal, or octal, as defined by Long.decode().
      *
      * @param name
      *            the name of the parameter whose value should be retrieved
      * @param defaultValue
      *            the default value to return if the value of this parameter was
      *            not specified
      * @return the value of the parameter, or the default value if the parameter
      *         was not specified
      *
      * @see Long#decode(String)
      */
     public long getLongParameter(String name, long defaultValue) {
         if (params == null || !params.containsKey(name)) {
             return defaultValue;
         }
         try {
             return Long.decode(params.get(name)).longValue();
         } catch (NumberFormatException e) {
-            LOGGER.warn("Value for parameter '" + name + "' not a long: '" + params.get(name) + "'.  Using default: '"
-                    + defaultValue + "'.", e);
+            log.warn("Value for parameter '{}' not a long: '{}'.  Using default: '{}'.", name, params.get(name),
+                    defaultValue, e);
             return defaultValue;
         }
     }
 
     /**
      * @param name Parameter name
      * @param defaultValue Default value used if name is not in params
      * @return boolean
      */
     public boolean getBooleanParameter(String name, boolean defaultValue) {
         if (params == null || !params.containsKey(name)) {
             return defaultValue;
         }
         return Boolean.parseBoolean(params.get(name));
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/BackendListenerGui.java b/src/components/org/apache/jmeter/visualizers/backend/BackendListenerGui.java
index 72d71ea6b..8b1db25bc 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/BackendListenerGui.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/BackendListenerGui.java
@@ -1,283 +1,284 @@
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
 
 package org.apache.jmeter.visualizers.backend;
 
 import java.awt.BorderLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import javax.swing.ComboBoxModel;
 import javax.swing.JComboBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
 
 import org.apache.commons.lang3.ArrayUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.gui.ArgumentsPanel;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The {@link BackendListenerGui} class provides the user interface for the
  * {@link BackendListener} object.
  * @since 2.13
  */
 public class BackendListenerGui extends AbstractListenerGui implements ActionListener {
 
     /**
      * 
      */
-    private static final long serialVersionUID = 4331668988576438604L;
+    private static final long serialVersionUID = 1L;
 
     /** Logging */
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BackendListenerGui.class);
 
     /** A combo box allowing the user to choose a backend class. */
     private JComboBox<String> classnameCombo;
     
     /**
      * A field allowing the user to specify the size of Queue
      */
     private JTextField queueSize;
 
     /** A panel allowing the user to set arguments for this test. */
     private ArgumentsPanel argsPanel;
 
     /**
      * Create a new BackendListenerGui as a standalone component.
      */
     public BackendListenerGui() {
         super();
         init();
     }
 
 
     /** {@inheritDoc} */
     @Override
     public String getLabelResource() {
         return "backend_listener"; // $NON-NLS-1$
     }
 
     /**
      * Initialize the GUI components and layout.
      */
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout(0, 5));
 
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel classnameRequestPanel = new JPanel(new BorderLayout(0, 5));
         classnameRequestPanel.add(createClassnamePanel(), BorderLayout.NORTH);
         classnameRequestPanel.add(createParameterPanel(), BorderLayout.CENTER);
 
         add(classnameRequestPanel, BorderLayout.CENTER);
     }
 
     /**
      * Create a panel with GUI components allowing the user to select a test
      * class.
      *
      * @return a panel containing the relevant components
      */
     private JPanel createClassnamePanel() {
         List<String> possibleClasses = new ArrayList<>();
 
         try {
             // Find all the classes which implement the BackendListenerClient
             // interface.
             possibleClasses = ClassFinder.findClassesThatExtend(JMeterUtils.getSearchPaths(),
                     new Class[] { BackendListenerClient.class });
 
             // Remove the BackendListener class from the list since it only
             // implements the interface for error conditions.
 
             possibleClasses.remove(BackendListener.class.getName() + "$ErrorBackendListenerClient");
         } catch (Exception e) {
-            LOGGER.debug("Exception getting interfaces.", e);
+            log.debug("Exception getting interfaces.", e);
         }
 
         JLabel label = new JLabel(JMeterUtils.getResString("backend_listener_classname")); // $NON-NLS-1$
 
         classnameCombo = new JComboBox<>(possibleClasses.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
         classnameCombo.addActionListener(this);
         classnameCombo.setEditable(false);
         label.setLabelFor(classnameCombo);
 
         HorizontalPanel classNamePanel = new HorizontalPanel();
         classNamePanel.add(label);
         classNamePanel.add(classnameCombo);
 
         queueSize = new JTextField(BackendListener.DEFAULT_QUEUE_SIZE, 5);
         queueSize.setName("Queue Size"); //$NON-NLS-1$
         JLabel queueSizeLabel = new JLabel(JMeterUtils.getResString("backend_listener_queue_size")); // $NON-NLS-1$
         queueSizeLabel.setLabelFor(queueSize);
         HorizontalPanel queueSizePanel = new HorizontalPanel();
         queueSizePanel.add(queueSizeLabel, BorderLayout.WEST);
         queueSizePanel.add(queueSize);
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
         panel.add(classNamePanel, BorderLayout.NORTH);
         panel.add(queueSizePanel, BorderLayout.CENTER);
         return panel;
     }
 
     /**
      * Handle action events for this component. This method currently handles
      * events for the classname combo box.
      *
      * @param event
      *            the ActionEvent to be handled
      */
     @Override
     public void actionPerformed(ActionEvent event) {
         if (event.getSource() == classnameCombo) {
             String className = ((String) classnameCombo.getSelectedItem()).trim();
             try {
                 BackendListenerClient client = (BackendListenerClient) Class.forName(className, true,
                         Thread.currentThread().getContextClassLoader()).newInstance();
 
                 Arguments currArgs = new Arguments();
                 argsPanel.modifyTestElement(currArgs);
                 Map<String, String> currArgsMap = currArgs.getArgumentsAsMap();
 
                 Arguments newArgs = new Arguments();
                 Arguments testParams = null;
                 try {
                     testParams = client.getDefaultParameters();
                 } catch (AbstractMethodError e) {
-                    LOGGER.warn("BackendListenerClient doesn't implement "
+                    log.warn("BackendListenerClient doesn't implement "
                             + "getDefaultParameters.  Default parameters won't "
-                            + "be shown.  Please update your client class: " + className);
+                            + "be shown.  Please update your client class: {}", className);
                 }
 
                 if (testParams != null) {
                     for (JMeterProperty jMeterProperty : testParams.getArguments()) {
                         Argument arg = (Argument) jMeterProperty.getObjectValue();
                         String name = arg.getName();
                         String value = arg.getValue();
 
                         // If a user has set parameters in one test, and then
                         // selects a different test which supports the same
                         // parameters, those parameters should have the same
                         // values that they did in the original test.
                         if (currArgsMap.containsKey(name)) {
                             String newVal = currArgsMap.get(name);
                             if (newVal != null && newVal.length() > 0) {
                                 value = newVal;
                             }
                         }
                         newArgs.addArgument(name, value);
                     }
                 }
 
                 argsPanel.configure(newArgs);
             } catch (Exception e) {
-                LOGGER.error("Error getting argument list for " + className, e);
+                log.error("Error getting argument list for {}", className, e);
             }
         }
     }
 
     /**
      * Create a panel containing components allowing the user to provide
      * arguments to be passed to the test class instance.
      *
      * @return a panel containing the relevant components
      */
     private JPanel createParameterPanel() {
         argsPanel = new ArgumentsPanel(JMeterUtils.getResString("backend_listener_paramtable")); // $NON-NLS-1$
         return argsPanel;
     }
 
     /** {@inheritDoc} */
     @Override
     public void configure(TestElement config) {
         super.configure(config);
 
         argsPanel.configure((Arguments) config.getProperty(BackendListener.ARGUMENTS).getObjectValue());
 
         String className = config.getPropertyAsString(BackendListener.CLASSNAME);
         if(checkContainsClassName(classnameCombo.getModel(), className)) {
             classnameCombo.setSelectedItem(className);
         } else {
-            LOGGER.error("Error setting class:'"+className+"' in BackendListener: "+getName()+
-                    ", check for a missing jar in your jmeter 'search_paths' and 'plugin_dependency_paths' properties");
+            log.error(
+                    "Error setting class: '{}' in BackendListener: {}, check for a missing jar in your jmeter 'search_paths' and 'plugin_dependency_paths' properties",
+                    className, getName());
         }
         queueSize.setText(((BackendListener)config).getQueueSize());
     }
 
     /**
      * Check combo contains className
      * @param model ComboBoxModel
      * @param className String class name
      * @return boolean true if model contains className
      */
     private static boolean checkContainsClassName(
             ComboBoxModel<?> model, String className) {
         int size = model.getSize();
         Set<String> set = new HashSet<>(size);
         for (int i = 0; i < size; i++) {
             set.add((String)model.getElementAt(i));
         }
         return set.contains(className);
     }
 
     /** {@inheritDoc} */
     @Override
     public TestElement createTestElement() {
         BackendListener config = new BackendListener();
         modifyTestElement(config);
         return config;
     }
 
     /** {@inheritDoc} */
     @Override
     public void modifyTestElement(TestElement config) {
         configureTestElement(config);
         BackendListener backendListener = (BackendListener) config;
         backendListener.setArguments((Arguments) argsPanel.createTestElement());
         backendListener.setClassname(String.valueOf(classnameCombo.getSelectedItem()));
         backendListener.setQueueSize(queueSize.getText());
         
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#clearGui()
      */
     @Override
     public void clearGui() {
         super.clearGui();
         argsPanel.clearGui();
         classnameCombo.setSelectedIndex(0);
         queueSize.setText(BackendListener.DEFAULT_QUEUE_SIZE);
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java b/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java
index 1c60e0e14..1d9c1b58f 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/graphite/GraphiteBackendListenerClient.java
@@ -1,352 +1,350 @@
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
 
 package org.apache.jmeter.visualizers.backend.graphite;
 
 import java.text.DecimalFormat;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.Executors;
 import java.util.concurrent.ScheduledExecutorService;
 import java.util.concurrent.ScheduledFuture;
 import java.util.concurrent.TimeUnit;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.backend.AbstractBackendListenerClient;
 import org.apache.jmeter.visualizers.backend.BackendListenerContext;
 import org.apache.jmeter.visualizers.backend.SamplerMetric;
 import org.apache.jmeter.visualizers.backend.UserMetric;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Graphite based Listener using Pickle Protocol
  * @see <a href="http://graphite.readthedocs.org/en/latest/overview.html">Graphite Overview</a>
  * @since 2.13
  */
 public class GraphiteBackendListenerClient extends AbstractBackendListenerClient implements Runnable {
 
     //+ Argument names
     // These are stored in the JMX file, so DO NOT CHANGE ANY VALUES 
     private static final String GRAPHITE_METRICS_SENDER = "graphiteMetricsSender"; //$NON-NLS-1$
     private static final String GRAPHITE_HOST = "graphiteHost"; //$NON-NLS-1$
     private static final String GRAPHITE_PORT = "graphitePort"; //$NON-NLS-1$
     private static final String ROOT_METRICS_PREFIX = "rootMetricsPrefix"; //$NON-NLS-1$
     private static final String PERCENTILES = "percentiles"; //$NON-NLS-1$
     private static final String SAMPLERS_LIST = "samplersList"; //$NON-NLS-1$
     public static final String USE_REGEXP_FOR_SAMPLERS_LIST = "useRegexpForSamplersList"; //$NON-NLS-1$
     public static final String USE_REGEXP_FOR_SAMPLERS_LIST_DEFAULT = "false";
     private static final String SUMMARY_ONLY = "summaryOnly"; //$NON-NLS-1$
     //- Argument names
 
     private static final int DEFAULT_PLAINTEXT_PROTOCOL_PORT = 2003;
     private static final String TEST_CONTEXT_NAME = "test";
     private static final String ALL_CONTEXT_NAME = "all";
 
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(GraphiteBackendListenerClient.class);
     private static final String DEFAULT_METRICS_PREFIX = "jmeter."; //$NON-NLS-1$
     private static final String CUMULATED_METRICS = "__cumulated__"; //$NON-NLS-1$
     // User Metrics
     private static final String METRIC_MAX_ACTIVE_THREADS = "maxAT"; //$NON-NLS-1$
     private static final String METRIC_MIN_ACTIVE_THREADS = "minAT"; //$NON-NLS-1$
     private static final String METRIC_MEAN_ACTIVE_THREADS = "meanAT"; //$NON-NLS-1$
     private static final String METRIC_STARTED_THREADS = "startedT"; //$NON-NLS-1$
     private static final String METRIC_FINISHED_THREADS = "endedT"; //$NON-NLS-1$
     
     // Response time Metrics
     private static final String METRIC_SEPARATOR = "."; //$NON-NLS-1$
     private static final String METRIC_OK_PREFIX = "ok"; //$NON-NLS-1$
     private static final String METRIC_KO_PREFIX = "ko"; //$NON-NLS-1$
     private static final String METRIC_ALL_PREFIX = "a"; //$NON-NLS-1$
     private static final String METRIC_HITS_PREFIX = "h"; //$NON-NLS-1$
     
     private static final String METRIC_COUNT = "count"; //$NON-NLS-1$
     private static final String METRIC_MIN_RESPONSE_TIME = "min"; //$NON-NLS-1$
     private static final String METRIC_MAX_RESPONSE_TIME = "max"; //$NON-NLS-1$
     private static final String METRIC_AVG_RESPONSE_TIME = "avg"; //$NON-NLS-1$
     private static final String METRIC_PERCENTILE = "pct"; //$NON-NLS-1$
     
     private static final String METRIC_OK_COUNT             = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
     private static final String METRIC_OK_MIN_RESPONSE_TIME = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_MIN_RESPONSE_TIME;
     private static final String METRIC_OK_MAX_RESPONSE_TIME = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_MAX_RESPONSE_TIME;
     private static final String METRIC_OK_AVG_RESPONSE_TIME = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_AVG_RESPONSE_TIME;
     private static final String METRIC_OK_PERCENTILE_PREFIX = METRIC_OK_PREFIX+METRIC_SEPARATOR+METRIC_PERCENTILE;
 
     private static final String METRIC_KO_COUNT             = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
     private static final String METRIC_KO_MIN_RESPONSE_TIME = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_MIN_RESPONSE_TIME;
     private static final String METRIC_KO_MAX_RESPONSE_TIME = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_MAX_RESPONSE_TIME;
     private static final String METRIC_KO_AVG_RESPONSE_TIME = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_AVG_RESPONSE_TIME;
     private static final String METRIC_KO_PERCENTILE_PREFIX = METRIC_KO_PREFIX+METRIC_SEPARATOR+METRIC_PERCENTILE;
 
     private static final String METRIC_ALL_COUNT             = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
     private static final String METRIC_ALL_MIN_RESPONSE_TIME = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_MIN_RESPONSE_TIME;
     private static final String METRIC_ALL_MAX_RESPONSE_TIME = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_MAX_RESPONSE_TIME;
     private static final String METRIC_ALL_AVG_RESPONSE_TIME = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_AVG_RESPONSE_TIME;
     private static final String METRIC_ALL_PERCENTILE_PREFIX = METRIC_ALL_PREFIX+METRIC_SEPARATOR+METRIC_PERCENTILE;
 
     private static final String METRIC_ALL_HITS_COUNT        = METRIC_HITS_PREFIX+METRIC_SEPARATOR+METRIC_COUNT;
 
     private static final long SEND_INTERVAL = JMeterUtils.getPropDefault("backend_graphite.send_interval", 5);
     private static final int MAX_POOL_SIZE = 1;
     private static final String DEFAULT_PERCENTILES = "90;95;99";
     private static final String SEPARATOR = ";"; //$NON-NLS-1$
     private static final Object LOCK = new Object();
 
     private String graphiteHost;
     private int graphitePort;
     private boolean summaryOnly;
     private String rootMetricsPrefix;
     private String samplersList = ""; //$NON-NLS-1$
     private boolean useRegexpForSamplersList;
     private Set<String> samplersToFilter;
     private Map<String, Float> okPercentiles;
     private Map<String, Float> koPercentiles;
     private Map<String, Float> allPercentiles;
     
 
     private GraphiteMetricsSender graphiteMetricsManager;
 
     private ScheduledExecutorService scheduler;
     private ScheduledFuture<?> timerHandle;
     
     private Pattern pattern;
 
     public GraphiteBackendListenerClient() {
         super();
     }    
 
     @Override
     public void run() {
         sendMetrics();
     }
 
     /**
      * Send metrics to Graphite
      */
     protected void sendMetrics() {
         // Need to convert millis to seconds for Graphite
         long timestampInSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
         synchronized (LOCK) {
             for (Map.Entry<String, SamplerMetric> entry : getMetricsPerSampler().entrySet()) {
                 final String key = entry.getKey();
                 final SamplerMetric metric = entry.getValue();
                 if(key.equals(CUMULATED_METRICS)) {
                     addMetrics(timestampInSeconds, ALL_CONTEXT_NAME, metric);
                 } else {
                     addMetrics(timestampInSeconds, AbstractGraphiteMetricsSender.sanitizeString(key), metric);                
                 }
                 // We are computing on interval basis so cleanup
                 metric.resetForTimeInterval();
             }
         }        
         UserMetric userMetric = getUserMetrics();
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_MIN_ACTIVE_THREADS, Integer.toString(userMetric.getMinActiveThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_MAX_ACTIVE_THREADS, Integer.toString(userMetric.getMaxActiveThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_MEAN_ACTIVE_THREADS, Integer.toString(userMetric.getMeanActiveThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_STARTED_THREADS, Integer.toString(userMetric.getStartedThreads()));
         graphiteMetricsManager.addMetric(timestampInSeconds, TEST_CONTEXT_NAME, METRIC_FINISHED_THREADS, Integer.toString(userMetric.getFinishedThreads()));
 
         graphiteMetricsManager.writeAndSendMetrics();
     }
 
 
     /**
      * Add request metrics to metrics manager.
      * Note if total number of requests is 0, no response time metrics are sent.
      * @param timestampInSeconds long
      * @param contextName String
      * @param metric {@link SamplerMetric}
      */
     private void addMetrics(long timestampInSeconds, String contextName, SamplerMetric metric) {
 
         // See https://bz.apache.org/bugzilla/show_bug.cgi?id=57350
         if(metric.getTotal() > 0) { 
             graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_OK_COUNT, Integer.toString(metric.getSuccesses()));
             graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_KO_COUNT, Integer.toString(metric.getFailures()));
             graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_COUNT, Integer.toString(metric.getTotal()));
             graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_HITS_COUNT, Integer.toString(metric.getHits()));
             if(metric.getSuccesses()>0) {
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_OK_MIN_RESPONSE_TIME, Double.toString(metric.getOkMinTime()));
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_OK_MAX_RESPONSE_TIME, Double.toString(metric.getOkMaxTime()));
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_OK_AVG_RESPONSE_TIME, Double.toString(metric.getOkMean()));
                 for (Map.Entry<String, Float> entry : okPercentiles.entrySet()) {
                     graphiteMetricsManager.addMetric(timestampInSeconds, contextName, 
                             entry.getKey(), 
                             Double.toString(metric.getOkPercentile(entry.getValue().floatValue())));
                 }
             } 
             if(metric.getFailures()>0) {
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_KO_MIN_RESPONSE_TIME, Double.toString(metric.getKoMinTime()));
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_KO_MAX_RESPONSE_TIME, Double.toString(metric.getKoMaxTime()));
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_KO_AVG_RESPONSE_TIME, Double.toString(metric.getKoMean()));
                 for (Map.Entry<String, Float> entry : koPercentiles.entrySet()) {
                     graphiteMetricsManager.addMetric(timestampInSeconds, contextName, 
                             entry.getKey(), 
                             Double.toString(metric.getKoPercentile(entry.getValue().floatValue())));
                 }   
             }
             graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_MIN_RESPONSE_TIME, Double.toString(metric.getAllMinTime()));
             graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_MAX_RESPONSE_TIME, Double.toString(metric.getAllMaxTime()));
             graphiteMetricsManager.addMetric(timestampInSeconds, contextName, METRIC_ALL_AVG_RESPONSE_TIME, Double.toString(metric.getAllMean()));
             for (Map.Entry<String, Float> entry : allPercentiles.entrySet()) {
                 graphiteMetricsManager.addMetric(timestampInSeconds, contextName, 
                         entry.getKey(), 
                         Double.toString(metric.getAllPercentile(entry.getValue().floatValue())));
             }
         }
     }
 
     /**
      * @return the samplersList
      */
     public String getSamplersList() {
         return samplersList;
     }
 
     /**
      * @param samplersList the samplersList to set
      */
     public void setSamplersList(String samplersList) {
         this.samplersList = samplersList;
     }
 
     @Override
     public void handleSampleResults(List<SampleResult> sampleResults,
             BackendListenerContext context) {
         boolean samplersToFilterMatch;
         synchronized (LOCK) {
             UserMetric userMetrics = getUserMetrics();
             for (SampleResult sampleResult : sampleResults) {
                 userMetrics.add(sampleResult);
                 
                 if(!summaryOnly) {
                     if (useRegexpForSamplersList) {
                         Matcher matcher = pattern.matcher(sampleResult.getSampleLabel());
                         samplersToFilterMatch = matcher.matches();
                     } else {
                         samplersToFilterMatch = samplersToFilter.contains(sampleResult.getSampleLabel()); 
                     }
                     if (samplersToFilterMatch) {
                         SamplerMetric samplerMetric = getSamplerMetric(sampleResult.getSampleLabel());
                         samplerMetric.add(sampleResult);
                     }
                 }
                 SamplerMetric cumulatedMetrics = getSamplerMetric(CUMULATED_METRICS);
                 cumulatedMetrics.add(sampleResult);                    
             }
         }
     }
 
     @Override
     public void setupTest(BackendListenerContext context) throws Exception {
         String graphiteMetricsSenderClass = context.getParameter(GRAPHITE_METRICS_SENDER);
         
         graphiteHost = context.getParameter(GRAPHITE_HOST);
         graphitePort = context.getIntParameter(GRAPHITE_PORT, DEFAULT_PLAINTEXT_PROTOCOL_PORT);
         summaryOnly = context.getBooleanParameter(SUMMARY_ONLY, true);
         samplersList = context.getParameter(SAMPLERS_LIST, "");
         useRegexpForSamplersList = context.getBooleanParameter(USE_REGEXP_FOR_SAMPLERS_LIST, false);
         rootMetricsPrefix = context.getParameter(ROOT_METRICS_PREFIX, DEFAULT_METRICS_PREFIX);
         String[]  percentilesStringArray = context.getParameter(PERCENTILES, DEFAULT_METRICS_PREFIX).split(SEPARATOR);
         okPercentiles = new HashMap<>(percentilesStringArray.length);
         koPercentiles = new HashMap<>(percentilesStringArray.length);
         allPercentiles = new HashMap<>(percentilesStringArray.length);
         DecimalFormat decimalFormat = new DecimalFormat("0.##");
         for (String percentilesString : percentilesStringArray) {
             if (!StringUtils.isEmpty(percentilesString.trim())) {
                 try {
                     Float percentileValue = Float.valueOf(percentilesString.trim());
                     String sanitizedFormattedPercentile =
                             AbstractGraphiteMetricsSender.sanitizeString(
                                     decimalFormat.format(percentileValue));
                     okPercentiles.put(
                             METRIC_OK_PERCENTILE_PREFIX + sanitizedFormattedPercentile,
                             percentileValue);
                     koPercentiles.put(
                             METRIC_KO_PERCENTILE_PREFIX + sanitizedFormattedPercentile,
                             percentileValue);
                     allPercentiles.put(
                             METRIC_ALL_PERCENTILE_PREFIX + sanitizedFormattedPercentile,
                             percentileValue);
 
                 } catch (Exception e) {
-                    LOGGER.error("Error parsing percentile:'" + percentilesString + "'", e);
+                    log.error("Error parsing percentile: '{}'", percentilesString, e);
                 }
             }
         }
         Class<?> clazz = Class.forName(graphiteMetricsSenderClass);
         this.graphiteMetricsManager = (GraphiteMetricsSender) clazz.newInstance();
         graphiteMetricsManager.setup(graphiteHost, graphitePort, rootMetricsPrefix);
         if (useRegexpForSamplersList) {
             pattern = Pattern.compile(samplersList);
         } else {
             String[] samplers = samplersList.split(SEPARATOR);
             samplersToFilter = new HashSet<>();
             Collections.addAll(samplersToFilter, samplers);
         }
         scheduler = Executors.newScheduledThreadPool(MAX_POOL_SIZE);
         // Don't change this as metrics are per second
         this.timerHandle = scheduler.scheduleAtFixedRate(this, SEND_INTERVAL, SEND_INTERVAL, TimeUnit.SECONDS);
     }
 
     @Override
     public void teardownTest(BackendListenerContext context) throws Exception {
         boolean cancelState = timerHandle.cancel(false);
-        if(LOGGER.isDebugEnabled()) {
-            LOGGER.debug("Canceled state:"+cancelState);
-        }
+        log.debug("Canceled state: {}", cancelState);
         scheduler.shutdown();
         try {
             scheduler.awaitTermination(30, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
-            LOGGER.error("Error waiting for end of scheduler");
+            log.error("Error waiting for end of scheduler");
             Thread.currentThread().interrupt();
         }
         // Send last set of data before ending
         sendMetrics();
 
         if (samplersToFilter != null) {
             samplersToFilter.clear();
         }
         graphiteMetricsManager.destroy();
         super.teardownTest(context);
     }
 
     @Override
     public Arguments getDefaultParameters() {
         Arguments arguments = new Arguments();
         arguments.addArgument(GRAPHITE_METRICS_SENDER, TextGraphiteMetricsSender.class.getName());
         arguments.addArgument(GRAPHITE_HOST, "");
         arguments.addArgument(GRAPHITE_PORT, Integer.toString(DEFAULT_PLAINTEXT_PROTOCOL_PORT));
         arguments.addArgument(ROOT_METRICS_PREFIX, DEFAULT_METRICS_PREFIX);
         arguments.addArgument(SUMMARY_ONLY, "true");
         arguments.addArgument(SAMPLERS_LIST, "");
         arguments.addArgument(USE_REGEXP_FOR_SAMPLERS_LIST, USE_REGEXP_FOR_SAMPLERS_LIST_DEFAULT);
         arguments.addArgument(PERCENTILES, DEFAULT_PERCENTILES);
         return arguments;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/graphite/PickleGraphiteMetricsSender.java b/src/components/org/apache/jmeter/visualizers/backend/graphite/PickleGraphiteMetricsSender.java
index 7c689733f..2548463f2 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/graphite/PickleGraphiteMetricsSender.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/graphite/PickleGraphiteMetricsSender.java
@@ -1,195 +1,195 @@
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
 
 package org.apache.jmeter.visualizers.backend.graphite;
 
 import java.io.OutputStreamWriter;
 import java.io.Writer;
 import java.nio.ByteBuffer;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Pickle Graphite format 
  * Partly based on https://github.com/BrightcoveOS/metrics-graphite-pickle/blob/master/src/main/java/com/brightcove/metrics/reporting/GraphitePickleReporter.java 
  * as per license https://github.com/BrightcoveOS/metrics-graphite-pickle/blob/master/LICENSE.txt
  * @since 2.13
  */
 class PickleGraphiteMetricsSender extends AbstractGraphiteMetricsSender {
-    private static final Logger LOG = LoggingManager.getLoggerForClass();    
+    private static final Logger log = LoggerFactory.getLogger(PickleGraphiteMetricsSender.class);
 
     /**
      * Pickle opcodes needed for implementation
      */
     private static final char APPEND = 'a';
     private static final char LIST = 'l';
     private static final char LONG = 'L';
     private static final char MARK = '(';
     private static final char STOP = '.';
     private static final char STRING = 'S';
     private static final char TUPLE = 't';
     private static final char QUOTE = '\'';
     private static final char LF = '\n';
         
     private String prefix;
 
     private final Object lock = new Object();
 
     // graphite expects a python-pickled list of nested tuples.
     private List<MetricTuple> metrics = new LinkedList<>();
 
     private GenericKeyedObjectPool<SocketConnectionInfos, SocketOutputStream> socketOutputStreamPool;
 
     private SocketConnectionInfos socketConnectionInfos;
 
 
     PickleGraphiteMetricsSender() {
         super();
     }
     
     /**
      * @param graphiteHost Graphite Host
      * @param graphitePort Graphite Port
      * @param prefix Common Metrics prefix
      */
     @Override
     public void setup(String graphiteHost, int graphitePort, String prefix) {
         this.prefix = prefix;
         this.socketConnectionInfos = new SocketConnectionInfos(graphiteHost, graphitePort);
         this.socketOutputStreamPool = createSocketOutputStreamPool();
 
-        if(LOG.isInfoEnabled()) {
-            LOG.info("Created PickleGraphiteMetricsSender with host:"+graphiteHost+", port:"+graphitePort+", prefix:"+prefix);
-        }
+        log.info("Created PickleGraphiteMetricsSender with host: {}, port: {}, prefix: {}", graphiteHost, graphitePort,
+                prefix);
     }
     
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#addMetric(long, java.lang.String, java.lang.String, java.lang.String)
      */
     @Override
     public void addMetric(long timestamp, String contextName, String metricName, String metricValue) {
         StringBuilder sb = new StringBuilder(50);
         sb
             .append(prefix)
             .append(contextName)
             .append(".")
             .append(metricName);
         synchronized (lock) {
             metrics.add(new MetricTuple(sb.toString(), timestamp, metricValue));
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#writeAndSendMetrics()
      */
     @Override
     public void writeAndSendMetrics() {    
         List<MetricTuple> tempMetrics;
         synchronized (lock) {
             if(metrics.isEmpty()) {
                 return;
             }
             tempMetrics = metrics;
             metrics = new LinkedList<>();            
         }
         final List<MetricTuple> copyMetrics = tempMetrics;
         if (!copyMetrics.isEmpty()) {
             SocketOutputStream out = null;
             try {
                 String payload = convertMetricsToPickleFormat(copyMetrics);
 
                 int length = payload.length();
                 byte[] header = ByteBuffer.allocate(4).putInt(length).array();
 
                 out = socketOutputStreamPool.borrowObject(socketConnectionInfos);
                 out.write(header);
                 // pickleWriter is not closed as it would close the underlying pooled out
                 Writer pickleWriter = new OutputStreamWriter(out, CHARSET_NAME);
                 pickleWriter.write(payload);
                 pickleWriter.flush();
                 socketOutputStreamPool.returnObject(socketConnectionInfos, out);
             } catch (Exception e) {
                 if(out != null) {
                     try {
                         socketOutputStreamPool.invalidateObject(socketConnectionInfos, out);
                     } catch (Exception e1) {
-                        LOG.warn("Exception invalidating socketOutputStream connected to graphite server '"+socketConnectionInfos.getHost()+"':"+socketConnectionInfos.getPort(), e1);
+                        log.warn("Exception invalidating socketOutputStream connected to graphite server. '{}':{}",
+                                socketConnectionInfos.getHost(), socketConnectionInfos.getPort(), e1);
                     }
                 }
-                LOG.error("Error writing to Graphite:"+e.getMessage());
+                log.error("Error writing to Graphite: {}", e.getMessage());
             }
             
             // if there was an error, we might miss some data. for now, drop those on the floor and
             // try to keep going.
-            if(LOG.isDebugEnabled()) {
-                LOG.debug("Wrote "+ copyMetrics.size() +" metrics");
+            if (log.isDebugEnabled()) {
+                log.debug("Wrote {} metrics", copyMetrics.size());
             }
             copyMetrics.clear();
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#destroy()
      */
     @Override
     public void destroy() {
         socketOutputStreamPool.close();
     }
 
     /**
      * See: http://readthedocs.org/docs/graphite/en/1.0/feeding-carbon.html
      */
     private static String convertMetricsToPickleFormat(List<MetricTuple> metrics) {
         StringBuilder pickled = new StringBuilder(metrics.size()*75);
         pickled.append(MARK).append(LIST);
 
         for (MetricTuple tuple : metrics) {
             // begin outer tuple
             pickled.append(MARK);
 
             // the metric name is a string.
             pickled.append(STRING)
             // the single quotes are to match python's repr("abcd")
                 .append(QUOTE).append(tuple.name).append(QUOTE).append(LF);
 
             // begin the inner tuple
             pickled.append(MARK);
 
             // timestamp is a long
             pickled.append(LONG).append(tuple.timestamp)
             // the trailing L is to match python's repr(long(1234))             
                 .append(LONG).append(LF);
 
             // and the value is a string.
             pickled.append(STRING).append(QUOTE).append(tuple.value).append(QUOTE).append(LF);
 
             pickled.append(TUPLE) // end inner tuple
                 .append(TUPLE); // end outer tuple
 
             pickled.append(APPEND);
         }
 
         // every pickle ends with STOP
         pickled.append(STOP);
         return pickled.toString();
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/graphite/TextGraphiteMetricsSender.java b/src/components/org/apache/jmeter/visualizers/backend/graphite/TextGraphiteMetricsSender.java
index cb7fcfaa3..c2b5adc7f 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/graphite/TextGraphiteMetricsSender.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/graphite/TextGraphiteMetricsSender.java
@@ -1,138 +1,137 @@
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
 
 package org.apache.jmeter.visualizers.backend.graphite;
 
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * PlainText Graphite sender
  * @since 2.13
  */
 class TextGraphiteMetricsSender extends AbstractGraphiteMetricsSender {
-    private static final Logger LOG = LoggingManager.getLoggerForClass();        
+    private static final Logger log = LoggerFactory.getLogger(TextGraphiteMetricsSender.class);
         
     private String prefix;
 
     private final Object lock = new Object();
 
     private List<MetricTuple> metrics = new ArrayList<>();
 
     private GenericKeyedObjectPool<SocketConnectionInfos, SocketOutputStream> socketOutputStreamPool;
 
     private SocketConnectionInfos socketConnectionInfos;
 
 
     TextGraphiteMetricsSender() {
         super();
     }
 
     /**
      * @param graphiteHost Graphite Host
      * @param graphitePort Graphite Port
      * @param prefix Common Metrics prefix
      */
     @Override
     public void setup(String graphiteHost, int graphitePort, String prefix) {
         this.prefix = prefix;
         this.socketConnectionInfos = new SocketConnectionInfos(graphiteHost, graphitePort);
         this.socketOutputStreamPool = createSocketOutputStreamPool();
 
-        if(LOG.isInfoEnabled()) {
-            LOG.info("Created TextGraphiteMetricsSender with host:"+graphiteHost+", port:"+graphitePort+", prefix:"+prefix);
-        }
+        log.info("Created TextGraphiteMetricsSender with host: {}, port: {}, prefix: {}", graphiteHost, graphitePort,
+                prefix);
     }
     
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#addMetric(long, java.lang.String, java.lang.String, java.lang.String)
      */
     @Override
     public void addMetric(long timestamp, String contextName, String metricName, String metricValue) {
         StringBuilder sb = new StringBuilder(50);
         sb
             .append(prefix)
             .append(contextName)
             .append(".")
             .append(metricName);
         synchronized (lock) {
             metrics.add(new MetricTuple(sb.toString(), timestamp, metricValue));            
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#writeAndSendMetrics()
      */
     @Override
     public void writeAndSendMetrics() {
         List<MetricTuple> tempMetrics;
         synchronized (lock) {
             if(metrics.isEmpty()) {
                 return;
             }
             tempMetrics = metrics;
             metrics = new ArrayList<>(tempMetrics.size());            
         }
         final List<MetricTuple> copyMetrics = tempMetrics;
         if (!copyMetrics.isEmpty()) {
             SocketOutputStream out = null;
             try {
                 out = socketOutputStreamPool.borrowObject(socketConnectionInfos);
                 // pw is not closed as it would close the underlying pooled out
                 PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, CHARSET_NAME), false);
                 for (MetricTuple metric: copyMetrics) {
                     pw.printf("%s %s %d%n", metric.name, metric.value, Long.valueOf(metric.timestamp));
                 }
                 pw.flush();
                 // if there was an error, we might miss some data. for now, drop those on the floor and
                 // try to keep going.
-                if(LOG.isDebugEnabled()) {
-                    LOG.debug("Wrote "+ copyMetrics.size() +" metrics");
+                if (log.isDebugEnabled()) {
+                    log.debug("Wrote {} metrics", copyMetrics.size());
                 }
                 socketOutputStreamPool.returnObject(socketConnectionInfos, out);
             } catch (Exception e) {
                 if(out != null) {
                     try {
                         socketOutputStreamPool.invalidateObject(socketConnectionInfos, out);
                     } catch (Exception e1) {
-                        LOG.warn("Exception invalidating socketOutputStream connected to graphite server '"+
-                                socketConnectionInfos.getHost()+"':"+socketConnectionInfos.getPort(), e1);
+                        log.warn("Exception invalidating socketOutputStream connected to graphite server '{}':{}",
+                                socketConnectionInfos.getHost(), socketConnectionInfos.getPort(), e1);
                     }
                 }
-                LOG.error("Error writing to Graphite:"+e.getMessage());
+                log.error("Error writing to Graphite: {}", e.getMessage());
             }
             // We drop metrics in all cases
             copyMetrics.clear();
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#destroy()
      */
     @Override
     public void destroy() {
         socketOutputStreamPool.close();
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/influxdb/HttpMetricsSender.java b/src/components/org/apache/jmeter/visualizers/backend/influxdb/HttpMetricsSender.java
index 7bc7d9b8d..2691ca63c 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/influxdb/HttpMetricsSender.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/influxdb/HttpMetricsSender.java
@@ -1,235 +1,231 @@
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
 
 package org.apache.jmeter.visualizers.backend.influxdb;
 
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.http.HttpResponse;
 import org.apache.http.client.config.RequestConfig;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.concurrent.FutureCallback;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
 import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
 import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
 import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
 import org.apache.http.impl.nio.reactor.IOReactorConfig;
 import org.apache.http.nio.reactor.ConnectingIOReactor;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Influxdb sender base on The Line Protocol. The Line Protocol is a text based
  * format for writing points to InfluxDB. Syntax : <measurement>[,<tag_key>=
  * <tag_value>[,<tag_key>=<tag_value>]] <field_key>=<field_value>[,<field_key>=
  * <field_value>] [<timestamp>] Each line, separated by the newline character,
  * represents a single point in InfluxDB. Line Protocol is whitespace sensitive.
  * 
  * @since 3.2
  */
 class HttpMetricsSender extends AbstractInfluxdbMetricsSender {
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HttpMetricsSender.class);
 
     private final Object lock = new Object();
 
     private List<MetricTuple> metrics = new ArrayList<>();
 
     private HttpPost httpRequest;
 
     private CloseableHttpAsyncClient httpClient;
 
     private URL url;
 
     private Future<HttpResponse> lastRequest;
 
     HttpMetricsSender() {
         super();
     }
 
     /**
      * The HTTP API is the primary means of writing data into InfluxDB, by
      * sending POST requests to the /write endpoint. Initiate the HttpClient
      * client with a HttpPost request from influxdb url
      * 
      * @param influxdbUrl
      *            example : http://localhost:8086/write?db=myd&rp=one_week
      * @see org.apache.jmeter.visualizers.backend.influxdb.InfluxdbMetricsSender#setup(java.lang.String)
      */
     @Override
     public void setup(String influxdbUrl) throws Exception {
         // Create I/O reactor configuration
         IOReactorConfig ioReactorConfig = IOReactorConfig
                 .custom()
                 .setIoThreadCount(1)
                 .setConnectTimeout(JMeterUtils.getPropDefault("backend_influxdb.connection_timeout", 1000))
                 .setSoTimeout(JMeterUtils.getPropDefault("backend_influxdb.socket_timeout", 3000))
                 .build();
         // Create a custom I/O reactor
         ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
 
         // Create a connection manager with custom configuration.
         PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
                 ioReactor);
         
         httpClient = HttpAsyncClientBuilder.create()
                 .setConnectionManager(connManager)
                 .setMaxConnPerRoute(2)
                 .setMaxConnTotal(2)
                 .setUserAgent("ApacheJMeter"+JMeterUtils.getJMeterVersion())
                 .disableCookieManagement()
                 .disableConnectionState()
                 .build();
         url = new URL(influxdbUrl);
         httpRequest = createRequest(url);
         httpClient.start();
     }
 
     /**
      * @param influxdbUrl
      * @return 
      * @throws URISyntaxException 
      */
     private HttpPost createRequest(URL url) throws URISyntaxException {
         RequestConfig defaultRequestConfig = RequestConfig.custom()
                 .setConnectTimeout(JMeterUtils.getPropDefault("backend_influxdb.connection_timeout", 1000))
                 .setSocketTimeout(JMeterUtils.getPropDefault("backend_influxdb.socket_timeout", 3000))
                 .setConnectionRequestTimeout(JMeterUtils.getPropDefault("backend_influxdb.connection_request_timeout", 100))
                 .build();
         
         HttpPost httpRequest = new HttpPost(url.toURI());
         httpRequest.setConfig(defaultRequestConfig);
-        if (LOG.isDebugEnabled()) {
-            LOG.debug("Created InfluxDBMetricsSender with url:" + url);
-        }
+        log.debug("Created InfluxDBMetricsSender with url: {}", url);
         return httpRequest;
     }
 
     @Override
     public void addMetric(String mesurement, String tag, String field) {
         synchronized (lock) {
             metrics.add(new MetricTuple(mesurement, tag, field, System.currentTimeMillis()));            
         }
     }
 
     /**
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#
      *      writeAndSendMetrics()
      */
     @Override
     public void writeAndSendMetrics() {
         List<MetricTuple> tempMetrics;
         synchronized (lock) {
             if(metrics.isEmpty()) {
                 return;
             }
             tempMetrics = metrics;
             metrics = new ArrayList<>(tempMetrics.size());            
         }
         final List<MetricTuple> copyMetrics = tempMetrics;
         if (!copyMetrics.isEmpty()) {
             try {
                 if(httpRequest == null) {
                     httpRequest = createRequest(url);
                 }
                 StringBuilder sb = new StringBuilder(copyMetrics.size()*35);
                 for (MetricTuple metric : copyMetrics) {
                     // Add TimeStamp in nanosecond from epoch ( default in InfluxDB )
                     sb.append(metric.measurement)
                         .append(metric.tag)
                         .append(" ") //$NON-NLS-1$
                         .append(metric.field)
                         .append(" ")
                         .append(metric.timestamp+"000000") 
                         .append("\n"); //$NON-NLS-1$
                 }
 
                 StringEntity entity = new StringEntity(sb.toString(), StandardCharsets.UTF_8);
                 
                 httpRequest.setEntity(entity);
                 lastRequest = httpClient.execute(httpRequest, new FutureCallback<HttpResponse>() {
                     @Override
                     public void completed(final HttpResponse response) {
                         int code = response.getStatusLine().getStatusCode();
                         /*
                          * HTTP response summary 2xx: If your write request received
                          * HTTP 204 No Content, it was a success! 4xx: InfluxDB
                          * could not understand the request. 5xx: The system is
                          * overloaded or significantly impaired.
                          */
                         switch (code) {
                         case 204:
-                            if(LOG.isDebugEnabled()) {
-                                LOG.debug("Success, number of metrics written : " + copyMetrics.size());
+                            if (log.isDebugEnabled()) {
+                                log.debug("Success, number of metrics written: {}", copyMetrics.size());
                             }
                             break;
                         default:
-                            if(LOG.isDebugEnabled()) {
-                                LOG.debug("Error writing metrics to influxDB Url: "+ url+", responseCode: " + code);
-                            }
+                            log.debug("Error writing metrics to influxDB Url: {}, responseCode: {}", url, code);
                         }
                     }
                     @Override
                     public void failed(final Exception ex) {
-                        LOG.error("failed to send data to influxDB server : " + ex.getMessage());
+                        log.error("failed to send data to influxDB server : {}", ex.getMessage());
                     }
                     @Override
                     public void cancelled() {
-                        LOG.warn("Request to influxDB server was cancelled");
+                        log.warn("Request to influxDB server was cancelled");
                     }
                 });
                
             }catch (URISyntaxException ex ) {
-                LOG.error(ex.getMessage());
+                log.error(ex.getMessage());
             }
         }
 
         // We drop metrics in all cases
         copyMetrics.clear();
     }
 
     /**
      * @see org.apache.jmeter.visualizers.backend.graphite.GraphiteMetricsSender#
      *      destroy()
      */
     @Override
     public void destroy() {
         // Give some time to send last metrics before shutting down
-        LOG.info("Destroying ");
+        log.info("Destroying ");
         try {
             lastRequest.get(5, TimeUnit.SECONDS);
         } catch (InterruptedException | ExecutionException | TimeoutException e) {
-            LOG.error("Error waiting for last request to be send to InfluxDB", e);
+            log.error("Error waiting for last request to be send to InfluxDB", e);
         }
         if(httpRequest != null) {
             httpRequest.abort();
         }
         IOUtils.closeQuietly(httpClient);
     }
 
 }
diff --git a/src/components/org/apache/jmeter/visualizers/backend/influxdb/InfluxdbBackendListenerClient.java b/src/components/org/apache/jmeter/visualizers/backend/influxdb/InfluxdbBackendListenerClient.java
index e8eccddc9..4ac37bd0b 100644
--- a/src/components/org/apache/jmeter/visualizers/backend/influxdb/InfluxdbBackendListenerClient.java
+++ b/src/components/org/apache/jmeter/visualizers/backend/influxdb/InfluxdbBackendListenerClient.java
@@ -1,380 +1,378 @@
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
 
 package org.apache.jmeter.visualizers.backend.influxdb;
 
 import java.text.DecimalFormat;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.Executors;
 import java.util.concurrent.ScheduledExecutorService;
 import java.util.concurrent.ScheduledFuture;
 import java.util.concurrent.TimeUnit;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.backend.AbstractBackendListenerClient;
 import org.apache.jmeter.visualizers.backend.BackendListenerContext;
 import org.apache.jmeter.visualizers.backend.SamplerMetric;
 import org.apache.jmeter.visualizers.backend.UserMetric;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implementation of {@link AbstractBackendListenerClient} to write in an InfluxDB using 
  * custom schema
  * @since 3.2
  */
 public class InfluxdbBackendListenerClient extends AbstractBackendListenerClient implements Runnable {
 
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(InfluxdbBackendListenerClient.class);
     private ConcurrentHashMap<String, SamplerMetric> metricsPerSampler = new ConcurrentHashMap<>();
     // Name of the measurement
     private static final String EVENTS_FOR_ANNOTATION = "events";
     
     private static final String TAGS = ",tags=";
     private static final String TEXT = "text=\"";
 
     // Name of the measurement
     private static final String DEFAULT_MEASUREMENT = "jmeter";
 
     private static final String TAG_TRANSACTION = ",transaction=";
 
     private static final String TAG_STATUT = ",statut=";
     private static final String TAG_APPLICATION = ",application=";
 
     private static final String METRIC_COUNT = "count=";
     private static final String METRIC_COUNT_ERREUR = "countError=";
     private static final String METRIC_MIN = "min=";
     private static final String METRIC_MAX = "max=";
     private static final String METRIC_AVG = "avg=";
 
     private static final String METRIC_HIT = "hit=";
     private static final String METRIC_PCT = "pct";
 
     private static final String METRIC_MAXAT = "maxAT=";
     private static final String METRIC_MINAT = "minAT=";
     private static final String METRIC_MEANAT = "meanAT=";
     private static final String METRIC_STARTEDT = "startedT=";
     private static final String METRIC_ENDEDT = "endedT=";
 
     private static final String TAG_OK = "ok";
     private static final String TAG_KO = "ko";
     private static final String TAG_ALL = "all";
 
     private static final String CUMULATED_METRICS = "all";
     private static final long SEND_INTERVAL = JMeterUtils.getPropDefault("backend_influxdb.send_interval", 5);
     private static final int MAX_POOL_SIZE = 1;
     private static final String SEPARATOR = ";"; //$NON-NLS-1$
     private static final Object LOCK = new Object();
 
     private boolean summaryOnly;
     private String measurement = "DEFAULT_MEASUREMENT";
     private String influxdbUrl = "";
     private String samplersRegex = "";
     private Pattern samplersToFilter;
     private Map<String, Float> okPercentiles;
     private Map<String, Float> koPercentiles;
     private Map<String, Float> allPercentiles;
     private String testTitle;
     private String testTags;
     // Name of the application tested
     private String application = "";
 
     private InfluxdbMetricsSender influxdbMetricsManager;
 
     private ScheduledExecutorService scheduler;
     private ScheduledFuture<?> timerHandle;
 
     public InfluxdbBackendListenerClient() {
         super();
     }
 
     @Override
     public void run() {
         sendMetrics();
     }
 
     /**
      * Send metrics
      */
     protected void sendMetrics() {
 
         synchronized (LOCK) {
             for (Map.Entry<String, SamplerMetric> entry : getMetricsInfluxdbPerSampler().entrySet()) {
                 SamplerMetric metric = entry.getValue();
                 if (entry.getKey().equals(CUMULATED_METRICS)) {
                     addCumulatedMetrics(metric);
                 } else {
                     addMetrics(AbstractInfluxdbMetricsSender.tagToStringValue(entry.getKey()), metric);
                 }
                 // We are computing on interval basis so cleanup
                 metric.resetForTimeInterval();
             }
         }
 
         UserMetric userMetrics = getUserMetrics();
         // For JMETER context
         StringBuilder tag = new StringBuilder(60);
         tag.append(TAG_APPLICATION).append(application);
         tag.append(TAG_TRANSACTION).append("internal");
         StringBuilder field = new StringBuilder(80);
         field.append(METRIC_MINAT).append(userMetrics.getMinActiveThreads()).append(",");
         field.append(METRIC_MAXAT).append(userMetrics.getMaxActiveThreads()).append(",");
         field.append(METRIC_MEANAT).append(userMetrics.getMeanActiveThreads()).append(",");
         field.append(METRIC_STARTEDT).append(userMetrics.getStartedThreads()).append(",");
         field.append(METRIC_ENDEDT).append(userMetrics.getFinishedThreads());
 
         influxdbMetricsManager.addMetric(measurement, tag.toString(), field.toString());
 
         influxdbMetricsManager.writeAndSendMetrics();
     }
 
     /**
      * Add request metrics to metrics manager.
      * 
      * @param metric
      *            {@link SamplerMetric}
      */
     private void addMetrics(String transaction, SamplerMetric metric) {
         // FOR ALL STATUS
         addMetric(transaction, metric, metric.getTotal(), false, TAG_ALL, metric.getAllMean(), metric.getAllMinTime(),
                 metric.getAllMaxTime(), allPercentiles.values());
         // FOR OK STATUS
         addMetric(transaction, metric, metric.getSuccesses(), false, TAG_OK, metric.getOkMean(), metric.getOkMinTime(),
                 metric.getOkMaxTime(), Collections.<Float> emptySet());
         // FOR KO STATUS
         addMetric(transaction, metric, metric.getFailures(), true, TAG_KO, metric.getKoMean(), metric.getKoMinTime(),
                 metric.getKoMaxTime(), Collections.<Float> emptySet());
     }
 
     private void addMetric(String transaction, SamplerMetric metric, int count, boolean includeResponseCode,
             String statut, double mean, double minTime, double maxTime, Collection<Float> pcts) {
         if (count > 0) {
             StringBuilder tag = new StringBuilder(70);
             tag.append(TAG_APPLICATION).append(application);
             tag.append(TAG_STATUT).append(statut);
             tag.append(TAG_TRANSACTION).append(transaction);
             StringBuilder field = new StringBuilder(80);
             field.append(METRIC_COUNT).append(count);
             if (!Double.isNaN(mean)) {
                 field.append(",").append(METRIC_AVG).append(mean);
             }
             if (!Double.isNaN(minTime)) {
                 field.append(",").append(METRIC_MIN).append(minTime);
             }
             if (!Double.isNaN(maxTime)) {
                 field.append(",").append(METRIC_MAX).append(maxTime);
             }
             for (Float pct : pcts) {
                 field.append(",").append(METRIC_PCT).append(pct).append("=").append(metric.getAllPercentile(pct));
             }
             influxdbMetricsManager.addMetric(measurement, tag.toString(), field.toString());
         }
     }
 
     private void addCumulatedMetrics(SamplerMetric metric) {
         int total = metric.getTotal();
         if (total > 0) {
             StringBuilder tag = new StringBuilder(70);
             StringBuilder field = new StringBuilder(100);
             Collection<Float> pcts = allPercentiles.values();
             tag.append(TAG_APPLICATION).append(application);
             tag.append(TAG_TRANSACTION).append(CUMULATED_METRICS);
             tag.append(TAG_STATUT).append(CUMULATED_METRICS);
 
             field.append(METRIC_COUNT).append(total);
             field.append(",").append(METRIC_COUNT_ERREUR).append(metric.getFailures());
 
             if (!Double.isNaN(metric.getOkMean())) {
                 field.append(",").append(METRIC_AVG).append(Double.toString(metric.getOkMean()));
             }
             if (!Double.isNaN(metric.getOkMinTime())) {
                 field.append(",").append(METRIC_MIN).append(Double.toString(metric.getOkMinTime()));
             }
             if (!Double.isNaN(metric.getOkMaxTime())) {
                 field.append(",").append(METRIC_MAX).append(Double.toString(metric.getOkMaxTime()));
             }
 
             field.append(",").append(METRIC_HIT).append(metric.getHits());
             for (Float pct : pcts) {
                 field.append(",").append(METRIC_PCT).append(pct).append("=").append(Double.toString(metric.getAllPercentile(pct)));
             }
             field.append(",").append(METRIC_HIT).append(metric.getHits());
             influxdbMetricsManager.addMetric(measurement, tag.toString(), field.toString());
         }
     }
 
     /**
      * @return the samplersList
      */
     public String getSamplersRegex() {
         return samplersRegex;
     }
 
     /**
      * @param samplersList
      *            the samplersList to set
      */
     public void setSamplersList(String samplersList) {
         this.samplersRegex = samplersList;
     }
 
     @Override
     public void handleSampleResults(List<SampleResult> sampleResults, BackendListenerContext context) {
         synchronized (LOCK) {
             UserMetric userMetrics = getUserMetrics();
             for (SampleResult sampleResult : sampleResults) {
                 userMetrics.add(sampleResult);
                 Matcher matcher = samplersToFilter.matcher(sampleResult.getSampleLabel());
                 if (!summaryOnly && (matcher.find())) {
                     SamplerMetric samplerMetric = getSamplerMetricInfluxdb(sampleResult.getSampleLabel());
                     samplerMetric.add(sampleResult);
                 }
                 SamplerMetric cumulatedMetrics = getSamplerMetricInfluxdb(CUMULATED_METRICS);
                 cumulatedMetrics.add(sampleResult);
             }
         }
     }
 
     @Override
     public void setupTest(BackendListenerContext context) throws Exception {
         String influxdbMetricsSender = context.getParameter("influxdbMetricsSender");
         influxdbUrl = context.getParameter("influxdbUrl");
         summaryOnly = context.getBooleanParameter("summaryOnly", false);
         samplersRegex = context.getParameter("samplersRegex", "");
         application = AbstractInfluxdbMetricsSender.tagToStringValue(context.getParameter("application", ""));
         measurement = AbstractInfluxdbMetricsSender
                 .tagToStringValue(context.getParameter("measurement", DEFAULT_MEASUREMENT));
         testTitle = context.getParameter("testTitle", "Test");
         testTags = AbstractInfluxdbMetricsSender.tagToStringValue(context.getParameter("eventTags", ""));
         String percentilesAsString = context.getParameter("percentiles", "");
         String[] percentilesStringArray = percentilesAsString.split(SEPARATOR);
         okPercentiles = new HashMap<>(percentilesStringArray.length);
         koPercentiles = new HashMap<>(percentilesStringArray.length);
         allPercentiles = new HashMap<>(percentilesStringArray.length);
         DecimalFormat format = new DecimalFormat("0.##");
         for (int i = 0; i < percentilesStringArray.length; i++) {
             if (!StringUtils.isEmpty(percentilesStringArray[i].trim())) {
                 try {
                     Float percentileValue = Float.valueOf(percentilesStringArray[i].trim());
                     okPercentiles.put(AbstractInfluxdbMetricsSender.tagToStringValue(format.format(percentileValue)),
                             percentileValue);
                     koPercentiles.put(AbstractInfluxdbMetricsSender.tagToStringValue(format.format(percentileValue)),
                             percentileValue);
                     allPercentiles.put(AbstractInfluxdbMetricsSender.tagToStringValue(format.format(percentileValue)),
                             percentileValue);
 
                 } catch (Exception e) {
-                    LOGGER.error("Error parsing percentile:'" + percentilesStringArray[i] + "'", e);
+                    log.error("Error parsing percentile: '{}'", percentilesStringArray[i], e);
                 }
             }
         }
         Class<?> clazz = Class.forName(influxdbMetricsSender);
         this.influxdbMetricsManager = (InfluxdbMetricsSender) clazz.newInstance();
         influxdbMetricsManager.setup(influxdbUrl);
         samplersToFilter = Pattern.compile(samplersRegex);
         addAnnotation(true);
 
         scheduler = Executors.newScheduledThreadPool(MAX_POOL_SIZE);
         // Start immediately the scheduler and put the pooling ( 5 seconds by default )
         this.timerHandle = scheduler.scheduleAtFixedRate(this, 0, SEND_INTERVAL, TimeUnit.SECONDS);
 
     }
 
     protected SamplerMetric getSamplerMetricInfluxdb(String sampleLabel) {
         SamplerMetric samplerMetric = metricsPerSampler.get(sampleLabel);
         if (samplerMetric == null) {
             samplerMetric = new SamplerMetric();
             SamplerMetric oldValue = metricsPerSampler.putIfAbsent(sampleLabel, samplerMetric);
             if (oldValue != null) {
                 samplerMetric = oldValue;
             }
         }
         return samplerMetric;
     }
 
     private Map<String, SamplerMetric> getMetricsInfluxdbPerSampler() {
         return metricsPerSampler;
     }
 
     @Override
     public void teardownTest(BackendListenerContext context) throws Exception {
         boolean cancelState = timerHandle.cancel(false);
-        if (LOGGER.isDebugEnabled()) {
-            LOGGER.debug("Canceled state:" + cancelState);
-        }
+        log.debug("Canceled state: {}", cancelState);
         scheduler.shutdown();
         try {
             scheduler.awaitTermination(30, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
-            LOGGER.error("Error waiting for end of scheduler");
+            log.error("Error waiting for end of scheduler");
             Thread.currentThread().interrupt();
         }
 
         addAnnotation(false);
 
         // Send last set of data before ending
-        LOGGER.info("Sending last metrics");
+        log.info("Sending last metrics");
         sendMetrics();
 
         influxdbMetricsManager.destroy();
         super.teardownTest(context);
     }
 
     /**
      * Add Annotation at start or end of the run ( usefull with Grafana )
      * Grafana will let you send HTML in the “Text” such as a link to the release notes
      * Tags are separated by spaces in grafana
      * Tags is put as InfluxdbTag for better query performance on it
      * Never double or single quotes in influxdb except for string field
      * see : https://docs.influxdata.com/influxdb/v1.1/write_protocols/line_protocol_reference/#quoting-special-characters-and-additional-naming-guidelines
      * * @param startOrEnd boolean true for start, false for end
      */
     private void addAnnotation(boolean startOrEnd) {
         influxdbMetricsManager.addMetric(EVENTS_FOR_ANNOTATION, 
                 TAG_APPLICATION + application + ",title=ApacheJMeter"+
                 (StringUtils.isNotEmpty(testTags) ? TAGS+ testTags : ""), 
                 TEXT +  
                         AbstractInfluxdbMetricsSender.fieldToStringValue(testTitle +
                                 (startOrEnd ? " started" : " ended")) + "\"" );
     }
     
     @Override
     public Arguments getDefaultParameters() {
         Arguments arguments = new Arguments();
         arguments.addArgument("influxdbMetricsSender", HttpMetricsSender.class.getName());
         arguments.addArgument("influxdbUrl", "");
         arguments.addArgument("application", "application name");
         arguments.addArgument("measurement", DEFAULT_MEASUREMENT);
         arguments.addArgument("summaryOnly", "false");
         arguments.addArgument("samplersRegex", ".*");
         arguments.addArgument("percentiles", "99,95,90");
         arguments.addArgument("testTitle", "Test name");
         arguments.addArgument("eventTags", "");
         return arguments;
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/utils/Colors.java b/src/components/org/apache/jmeter/visualizers/utils/Colors.java
index 9fab901df..091477431 100644
--- a/src/components/org/apache/jmeter/visualizers/utils/Colors.java
+++ b/src/components/org/apache/jmeter/visualizers/utils/Colors.java
@@ -1,96 +1,96 @@
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
 
 package org.apache.jmeter.visualizers.utils;
 
 import java.awt.Color;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class Colors {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Colors.class);
 
     private static final String ENTRY_SEP = ",";  //$NON-NLS-1$
 
     private static final String ORDER_PROP_NAME = "order"; //$NON-NLS-1$
 
     protected static final String DEFAULT_COLORS_PROPERTY_FILE = "org/apache/jmeter/visualizers/utils/colors.properties"; //$NON-NLS-1$
 
     protected static final String USER_DEFINED_COLORS_PROPERTY_FILE = "jmeter.colors"; //$NON-NLS-1$
     
     private static final String COLORS_ORDER = "jmeter.order";
     
     /**
      * Parse icon set file.
      * @return List of icons/action definition
      */
     public static List<Color> getColors() {
         Properties defaultProps = JMeterUtils.loadProperties(DEFAULT_COLORS_PROPERTY_FILE);
         if (defaultProps == null) {
             JOptionPane.showMessageDialog(null, 
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
             return null;
         }
         Properties p;
         String userProp = JMeterUtils.getProperty(USER_DEFINED_COLORS_PROPERTY_FILE); 
         if (userProp != null){
             p = JMeterUtils.loadProperties(userProp, defaultProps);
         } else {
             p=defaultProps;
         }
 
         String order = JMeterUtils.getPropDefault(COLORS_ORDER, p.getProperty(ORDER_PROP_NAME));
 
         if (order == null) {
             log.warn("Could not find order list");
             JOptionPane.showMessageDialog(null, 
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
             return null;
         }
 
         String[] oList = order.split(ENTRY_SEP);
 
         List<Color> listColors = new ArrayList<>();
         for (String key : oList) {
             String trimmed = key.trim();
             String property = p.getProperty(trimmed);
             try {
                 String[] lcol = property.split(ENTRY_SEP);
                 Color itb = new Color(Integer.parseInt(lcol[0]), Integer.parseInt(lcol[1]), Integer.parseInt(lcol[2]));
                 listColors.add(itb);
             } catch (java.lang.Exception e) {
-                log.warn("Error in colors.properties, current property=" + property); // $NON-NLS-1$
+                log.warn("Error in colors.properties, current property={}", property); // $NON-NLS-1$
             }
         }
         return listColors;
     }
 
 }
