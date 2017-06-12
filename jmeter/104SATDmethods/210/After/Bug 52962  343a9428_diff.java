diff --git a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
index dd9168918..72da4790b 100644
--- a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
@@ -1,995 +1,998 @@
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
-import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
+import org.apache.jmeter.gui.util.HeaderAsPropertyRendererWrapper;
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
+import org.apache.jorphan.gui.ObjectTableSorter;
 import org.apache.jorphan.gui.RateRenderer;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.log.Logger;
 
 /**
  * Aggregrate Table-Based Reporting Visualizer for JMeter. Props to the people
  * who've done the other visualizers ahead of me (Stefano Mazzocchi), who I
  * borrowed code from to start me off (and much code may still exist). Thank
  * you!
  *
  */
 public class StatGraphVisualizer extends AbstractVisualizer implements Clearable, ActionListener {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private static final String PCT1_LABEL = JMeterUtils.getPropDefault("aggregate_rpt_pct1", "90");
     private static final String PCT2_LABEL = JMeterUtils.getPropDefault("aggregate_rpt_pct2", "95");
     private static final String PCT3_LABEL = JMeterUtils.getPropDefault("aggregate_rpt_pct3", "99");
     
     private static final Float PCT1_VALUE = new Float(Float.parseFloat(PCT1_LABEL)/100);
     private static final Float PCT2_VALUE =  new Float(Float.parseFloat(PCT2_LABEL)/100);
     private static final Float PCT3_VALUE =  new Float(Float.parseFloat(PCT3_LABEL)/100);
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
-                            Long.class, Long.class, Long.class, Long.class, String.class, 
-                            String.class, String.class, String.class});
+                            Long.class, Long.class, Long.class, Long.class, Double.class,
+                            Double.class, Double.class, Double.class});
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
+        myJTable.setRowSorter(new ObjectTableSorter(model).fixLastRow());
         JMeterUtils.applyHiDPI(myJTable);
         // Fix centering of titles
-        myJTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer(getColumnsMsgParameters()));
+        HeaderAsPropertyRendererWrapper.setupDefaultRenderer(myJTable, getColumnsMsgParameters());
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
+        spane.setOneTouchExpandable(true);
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
diff --git a/src/components/org/apache/jmeter/visualizers/StatVisualizer.java b/src/components/org/apache/jmeter/visualizers/StatVisualizer.java
index 50e8d1fdd..1e5856a17 100644
--- a/src/components/org/apache/jmeter/visualizers/StatVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/StatVisualizer.java
@@ -1,222 +1,223 @@
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
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.nio.charset.Charset;
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
 
 import org.apache.jmeter.gui.util.FileDialoger;
-import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
+import org.apache.jmeter.gui.util.HeaderAsPropertyRendererWrapper;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.ObjectTableModel;
+import org.apache.jorphan.gui.ObjectTableSorter;
 import org.apache.jorphan.gui.RendererUtils;
 
 /**
  * Aggregrate Table-Based Reporting Visualizer for JMeter. Props to the people
  * who've done the other visualizers ahead of me (Stefano Mazzocchi), who I
  * borrowed code from to start me off (and much code may still exist). Thank
  * you!
  *
  */
 public class StatVisualizer extends AbstractVisualizer implements Clearable, ActionListener {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private static final String USE_GROUP_NAME = "useGroupName"; //$NON-NLS-1$
 
     private static final String SAVE_HEADERS = "saveHeaders"; //$NON-NLS-1$
 
     private final String TOTAL_ROW_LABEL = JMeterUtils
             .getResString("aggregate_report_total_label"); //$NON-NLS-1$
 
     private JTable myJTable;
 
     private JScrollPane myScrollPane;
 
     private final JButton saveTable = new JButton(
             JMeterUtils.getResString("aggregate_graph_save_table")); //$NON-NLS-1$
 
     // should header be saved with the data?
     private final JCheckBox saveHeaders = new JCheckBox(
             JMeterUtils.getResString("aggregate_graph_save_table_header"), true); //$NON-NLS-1$
 
     private final JCheckBox useGroupName = new JCheckBox(
             JMeterUtils.getResString("aggregate_graph_use_group_name")); //$NON-NLS-1$
 
     private transient ObjectTableModel model;
 
     /**
      * Lock used to protect tableRows update + model update
      */
     private final transient Object lock = new Object();
 
     private final Map<String, SamplingStatCalculator> tableRows = new ConcurrentHashMap<>();
 
     public StatVisualizer() {
         super();
         model = StatGraphVisualizer.createObjectTableModel();
         clearData();
         init();
     }
 
     /**
      * @return <code>true</code> iff all functors can be found
      * @deprecated - only for use in testing
      * */
     @Deprecated
     public static boolean testFunctors(){
         StatVisualizer instance = new StatVisualizer();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
     @Override
     public String getLabelResource() {
         return "aggregate_report";  //$NON-NLS-1$
     }
 
     @Override
     public void add(final SampleResult res) {
         JMeterUtils.runSafe(false, new Runnable() {
             @Override
             public void run() {
                 SamplingStatCalculator row;
                 final String sampleLabel = res.getSampleLabel(useGroupName.isSelected());
                 synchronized (lock) {
                     row = tableRows.get(sampleLabel);
                     if (row == null) {
                         row = new SamplingStatCalculator(sampleLabel);
                         tableRows.put(row.getLabel(), row);
                         model.insertRow(row, model.getRowCount() - 1);
                     }
                 }
                 /*
                  * Synch is needed because multiple threads can update the counts.
                  */
                 synchronized(row) {
                     row.addSample(res);
                 }
                 SamplingStatCalculator tot = tableRows.get(TOTAL_ROW_LABEL);
                 synchronized(tot) {
                     tot.addSample(res);
                 }
                 model.fireTableDataChanged();
             }
         });
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
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
 
         mainPanel.add(makeTitlePanel());
 
         myJTable = new JTable(model);
+        myJTable.setRowSorter(new ObjectTableSorter(model).fixLastRow());
         JMeterUtils.applyHiDPI(myJTable);
-        myJTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer(StatGraphVisualizer.getColumnsMsgParameters()));
+        HeaderAsPropertyRendererWrapper.setupDefaultRenderer(myJTable, StatGraphVisualizer.getColumnsMsgParameters());
         myJTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
         RendererUtils.applyRenderers(myJTable, StatGraphVisualizer.getRenderers());
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
 
     @Override
     public void actionPerformed(ActionEvent ev) {
         if (ev.getSource() == saveTable) {
             JFileChooser chooser = FileDialoger.promptToSaveFile("aggregate.csv");//$NON-NLS-1$
             if (chooser == null) {
                 return;
             }
             try (FileOutputStream fo = new FileOutputStream(chooser.getSelectedFile());
                     OutputStreamWriter writer = new OutputStreamWriter(fo, Charset.forName("UTF-8"))){
                 CSVSaveService.saveCSVStats(StatGraphVisualizer.getAllTableData(model, StatGraphVisualizer.getFormatters()),
                         writer,
                         saveHeaders.isSelected() ? StatGraphVisualizer.getLabels(StatGraphVisualizer.getColumns()) : null);
             } catch (IOException e) {
                 JMeterUtils.reportErrorToUser(e.getMessage(), "Error saving data");
             }
         }
     }
 }
-
diff --git a/src/components/org/apache/jmeter/visualizers/SummaryReport.java b/src/components/org/apache/jmeter/visualizers/SummaryReport.java
index f5438fb20..8fc7edac0 100644
--- a/src/components/org/apache/jmeter/visualizers/SummaryReport.java
+++ b/src/components/org/apache/jmeter/visualizers/SummaryReport.java
@@ -1,287 +1,289 @@
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
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.nio.charset.Charset;
 import java.text.DecimalFormat;
 import java.text.Format;
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
-import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
+import org.apache.jmeter.gui.util.HeaderAsPropertyRendererWrapper;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.Calculator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.NumberRenderer;
 import org.apache.jorphan.gui.ObjectTableModel;
+import org.apache.jorphan.gui.ObjectTableSorter;
 import org.apache.jorphan.gui.RateRenderer;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.reflect.Functor;
 
 /**
  * Simpler (lower memory) version of Aggregate Report (StatVisualizer).
  * Excludes the Median and 90% columns, which are expensive in memory terms
  */
 public class SummaryReport extends AbstractVisualizer implements Clearable, ActionListener {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
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
             "aggregate_report_sent_bytes_per_sec",  //$NON-NLS-1$
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
 
     private final Map<String, Calculator> tableRows = new ConcurrentHashMap<>();
 
     // Column renderers
     private static final TableCellRenderer[] RENDERERS =
         new TableCellRenderer[]{
             null, // Label
             null, // count
             null, // Mean
             null, // Min
             null, // Max
             new NumberRenderer("#0.00"), // Std Dev. //$NON-NLS-1$
             new NumberRenderer("#0.00%"), // Error %age //$NON-NLS-1$
             new RateRenderer("#.0"),      // Throughput //$NON-NLS-1$
             new NumberRenderer("#0.00"),  // kB/sec //$NON-NLS-1$
             new NumberRenderer("#0.00"),  // sent kB/sec //$NON-NLS-1$
             new NumberRenderer("#.0"),    // avg. pageSize //$NON-NLS-1$
         };
     
     // Column formats
     private static final Format[] FORMATS =
         new Format[]{
             null, // Label
             null, // count
             null, // Mean
             null, // Min
             null, // Max
             new DecimalFormat("#0.00"), // Std Dev. //$NON-NLS-1$
             new DecimalFormat("#0.000%"), // Error %age //$NON-NLS-1$
             new DecimalFormat("#.00000"),      // Throughput //$NON-NLS-1$
             new DecimalFormat("#0.00"),  // kB/sec //$NON-NLS-1$
             new DecimalFormat("#0.00"),  // sent kB/sec //$NON-NLS-1$
             new DecimalFormat("#.0"),    // avg. pageSize //$NON-NLS-1$
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
                     new Functor("getSentKBPerSecond"),        //$NON-NLS-1$
                     new Functor("getAvgPageBytes"),       //$NON-NLS-1$
                 },
                 new Functor[] { null, null, null, null, null, null, null, null , null, null, null },
-                new Class[] { String.class, Long.class, Long.class, Long.class, Long.class,
-                              String.class, String.class, String.class, String.class, String.class, String.class });
+                new Class[] { String.class, Integer.class, Long.class, Long.class, Long.class, 
+                        Double.class, Double.class, Double.class, Double.class, Double.class, Double.class });
         clearData();
         init();
     }
 
     /**
      * @return <code>true</code> if all functors can be found
      * @deprecated - only for use in testing
      * */
     @Deprecated
     public static boolean testFunctors(){
         SummaryReport instance = new SummaryReport();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
     @Override
     public String getLabelResource() {
         return "summary_report";  //$NON-NLS-1$
     }
 
     @Override
     public void add(final SampleResult res) {
         final String sampleLabel = res.getSampleLabel(useGroupName.isSelected());
         JMeterUtils.runSafe(false, new Runnable() {
             @Override
             public void run() {
                 Calculator row;
                 synchronized (lock) {
                     row = tableRows.get(sampleLabel);
                     if (row == null) {
                         row = new Calculator(sampleLabel);
                         tableRows.put(row.getLabel(), row);
                         model.insertRow(row, model.getRowCount() - 1);
                     }
                 }
                 /*
                  * Synch is needed because multiple threads can update the counts.
                  */
                 synchronized(row) {
                     row.addSample(res);
                 }
                 Calculator tot = tableRows.get(TOTAL_ROW_LABEL);
                 synchronized(tot) {
                     tot.addSample(res);
                 }
                 model.fireTableDataChanged();                
             }
         });
     }
 
     /**
      * Clears this visualizer and its model, and forces a repaint of the table.
      */
     @Override
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
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         this.setLayout(new BorderLayout());
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         Border margin = new EmptyBorder(10, 10, 5, 10);
 
         mainPanel.setBorder(margin);
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
 
         mainPanel.add(makeTitlePanel());
 
         myJTable = new JTable(model);
+        myJTable.setRowSorter(new ObjectTableSorter(model).fixLastRow());
         JMeterUtils.applyHiDPI(myJTable);
-        myJTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
+        HeaderAsPropertyRendererWrapper.setupDefaultRenderer(myJTable);
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
 
     @Override
     public void actionPerformed(ActionEvent ev) {
         if (ev.getSource() == saveTable) {
             JFileChooser chooser = FileDialoger.promptToSaveFile("summary.csv");//$NON-NLS-1$
             if (chooser == null) {
                 return;
             }
             try (FileOutputStream fo = new FileOutputStream(chooser.getSelectedFile());
                     OutputStreamWriter writer = new OutputStreamWriter(fo, Charset.forName("UTF-8"))) {
                 CSVSaveService.saveCSVStats(StatGraphVisualizer.getAllTableData(model, FORMATS),writer, 
                         saveHeaders.isSelected() ? StatGraphVisualizer.getLabels(COLUMNS) : null);
             } catch (IOException e) {
                 JMeterUtils.reportErrorToUser(e.getMessage(), "Error saving data");
             } 
         }
     }
 }
diff --git a/src/components/org/apache/jmeter/visualizers/TableVisualizer.java b/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
index a9bbf61a4..213849528 100644
--- a/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/TableVisualizer.java
@@ -1,346 +1,362 @@
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
+import java.util.Comparator;
 
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
 
 import org.apache.jmeter.JMeter;
-import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
+import org.apache.jmeter.gui.util.HeaderAsPropertyRendererWrapper;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.Calculator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.ObjectTableModel;
+import org.apache.jorphan.gui.ObjectTableSorter;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.gui.RightAlignRenderer;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 import org.apache.jorphan.reflect.Functor;
 
 /**
  * This class implements a statistical analyser that calculates both the average
  * and the standard deviation of the sampling process. The samples are displayed
  * in a JTable, and the statistics are displayed at the bottom of the table.
  *
  */
 public class TableVisualizer extends AbstractVisualizer implements Clearable {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private static final String ICON_SIZE = JMeterUtils.getPropDefault(JMeter.TREE_ICON_SIZE, JMeter.DEFAULT_TREE_ICON_SIZE);
 
     // Note: the resource string won't respond to locale-changes,
     // however this does not matter as it is only used when pasting to the clipboard
     private static final ImageIcon imageSuccess = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                                        "vrt/" + ICON_SIZE + "/security-high-2.png"),    //$NON-NLS-1$ $NON-NLS-2$
             JMeterUtils.getResString("table_visualizer_success")); //$NON-NLS-1$
 
     private static final ImageIcon imageFailure = JMeterUtils.getImage(
             JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                                        "vrt/" + ICON_SIZE + "/security-low-2.png"),    //$NON-NLS-1$ $NON-NLS-2$
             JMeterUtils.getResString("table_visualizer_warning")); //$NON-NLS-1$
 
     private static final String[] COLUMNS = new String[] {
             "table_visualizer_sample_num",  // $NON-NLS-1$
             "table_visualizer_start_time",  // $NON-NLS-1$
             "table_visualizer_thread_name", // $NON-NLS-1$
             "sampler_label",                // $NON-NLS-1$
             "table_visualizer_sample_time", // $NON-NLS-1$
             "table_visualizer_status",      // $NON-NLS-1$
             "table_visualizer_bytes",       // $NON-NLS-1$
             "table_visualizer_sent_bytes",       // $NON-NLS-1$
             "table_visualizer_latency",     // $NON-NLS-1$
             "table_visualizer_connect"};    // $NON-NLS-1$
 
     private ObjectTableModel model = null;
 
     private JTable table = null;
 
     private JTextField dataField = null;
 
     private JTextField averageField = null;
 
     private JTextField deviationField = null;
 
     private JTextField noSamplesField = null;
 
     private JScrollPane tableScrollPanel = null;
 
     private JCheckBox autoscroll = null;
 
     private JCheckBox childSamples = null;
 
     private final transient Calculator calc = new Calculator();
 
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
                 new Functor("getSampleNumberString"),  // $NON-NLS-1$
                 new Functor("getStartTimeFormatted",   // $NON-NLS-1$
                         new Object[]{format}),
                 new Functor("getThreadName"),          // $NON-NLS-1$
                 new Functor("getLabel"),               // $NON-NLS-1$
                 new Functor("getElapsed"),             // $NON-NLS-1$
                 new SampleSuccessFunctor("isSuccess"), // $NON-NLS-1$
                 new Functor("getBytes"),               // $NON-NLS-1$
                 new Functor("getSentBytes"),               // $NON-NLS-1$
                 new Functor("getLatency"),             // $NON-NLS-1$
                 new Functor("getConnectTime") },       // $NON-NLS-1$
                 new Functor[] { null, null, null, null, null, null, null, null, null, null },
                 new Class[] {
                 String.class, String.class, String.class, String.class, Long.class, ImageIcon.class, Long.class, Long.class, Long.class, Long.class });
         init();
     }
 
     public static boolean testFunctors(){
         TableVisualizer instance = new TableVisualizer();
         return instance.model.checkFunctors(null,instance.getClass());
     }
 
 
     @Override
     public String getLabelResource() {
         return "view_results_in_table"; // $NON-NLS-1$
     }
 
     protected synchronized void updateTextFields(SampleResult res) {
         noSamplesField.setText(Long.toString(calc.getCount()));
         if(res.getSampleCount() > 0) {
             dataField.setText(Long.toString(res.getTime()/res.getSampleCount()));
         } else {
             dataField.setText("0");
         }
         averageField.setText(Long.toString((long) calc.getMean()));
         deviationField.setText(Long.toString((long) calc.getStandardDeviation()));
     }
 
     @Override
     public void add(final SampleResult res) {
         JMeterUtils.runSafe(false, new Runnable() {
             @Override
             public void run() {
                 if (childSamples.isSelected()) {
                     SampleResult[] subResults = res.getSubResults();
                     if (subResults.length > 0) {
                         for (SampleResult sr : subResults) {
                             add(sr);
                         }
                         return;
                     }
                 }
                 synchronized (calc) {
                     calc.addSample(res);
                     int count = calc.getCount();
                     TableSample newS = new TableSample(
-                            count, 
-                            res.getSampleCount(), 
-                            res.getStartTime(), 
-                            res.getThreadName(), 
+                            count,
+                            res.getSampleCount(),
+                            res.getStartTime(),
+                            res.getThreadName(),
                             res.getSampleLabel(),
                             res.getTime(),
                             res.isSuccessful(),
                             res.getBytesAsLong(),
                             res.getSentBytes(),
                             res.getLatency(),
                             res.getConnectTime()
                             );
                     model.addRow(newS);
                 }
                 updateTextFields(res);
                 if (autoscroll.isSelected()) {
                     table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
                 }
             }
         });
     }
 
     @Override
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
 
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
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
+        table.setRowSorter(new ObjectTableSorter(model).setValueComparator(5, 
+                Comparator.nullsFirst(
+                        (ImageIcon o1, ImageIcon o2) -> {
+                            if (o1 == o2) {
+                                return 0;
+                            }
+                            if (o1 == imageSuccess) {
+                                return -1;
+                            }
+                            if (o1 == imageFailure) {
+                                return 1;
+                            }
+                            throw new IllegalArgumentException("Only success and failure images can be compared");
+                        })));
         JMeterUtils.applyHiDPI(table);
-        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
+        HeaderAsPropertyRendererWrapper.setupDefaultRenderer(table);
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
         public Object invoke(Object pInvokee) {
             Boolean success = (Boolean) super.invoke(pInvokee);
 
             if (success != null) {
                 if (success.booleanValue()) {
                     return imageSuccess;
                 } else {
                     return imageFailure;
                 }
             } else {
                 return null;
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java b/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java
index 925308cab..d794a75c7 100644
--- a/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java
+++ b/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRenderer.java
@@ -1,90 +1,103 @@
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
 
 package org.apache.jmeter.gui.util;
 
 import java.awt.Component;
 import java.text.MessageFormat;
 
 import javax.swing.JTable;
 import javax.swing.SwingConstants;
 import javax.swing.UIManager;
 import javax.swing.table.DefaultTableCellRenderer;
 import javax.swing.table.JTableHeader;
 
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Renders items in a JTable by converting from resource names.
  */
 public class HeaderAsPropertyRenderer extends DefaultTableCellRenderer {
 
     private static final long serialVersionUID = 240L;
     private Object[][] columnsMsgParameters;
 
     /**
      * 
      */
     public HeaderAsPropertyRenderer() {
         this(null);
     }
     
     /**
      * @param columnsMsgParameters Optional parameters of i18n keys
      */
     public HeaderAsPropertyRenderer(Object[][] columnsMsgParameters) {
         super();
         this.columnsMsgParameters = columnsMsgParameters;
     }
 
     @Override
     public Component getTableCellRendererComponent(JTable table, Object value,
             boolean isSelected, boolean hasFocus, int row, int column) {
         if (table != null) {
             JTableHeader header = table.getTableHeader();
             if (header != null){
                 setForeground(header.getForeground());
                 setBackground(header.getBackground());
                 setFont(header.getFont());
             }
             setText(getText(value, row, column));
             setBorder(UIManager.getBorder("TableHeader.cellBorder"));
             setHorizontalAlignment(SwingConstants.CENTER);
         }
         return this;
     }
 
     /**
      * Get the text for the value as the translation of the resource name.
      *
      * @param value value for which to get the translation
      * @param column index which column message parameters should be used
      * @param row not used
      * @return the text
      */
     protected String getText(Object value, int row, int column) {
+        return getText(value, row, column, columnsMsgParameters);
+    }
+    
+    /**
+     * Get the text for the value as the translation of the resource name.
+     *
+     * @param value value for which to get the translation
+     * @param column index which column message parameters should be used
+     * @param row not used
+     * @param columnsMsgParameters
+     * @return the text
+     */
+    static String getText(Object value, int row, int column, Object[][] columnsMsgParameters) {
         if (value == null){
             return "";
         }
         if(columnsMsgParameters != null && columnsMsgParameters[column] != null) {
             return MessageFormat.format(JMeterUtils.getResString(value.toString()), columnsMsgParameters[column]);
         } else {
             return JMeterUtils.getResString(value.toString());
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRendererWrapper.java b/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRendererWrapper.java
new file mode 100644
index 000000000..47aef2dcb
--- /dev/null
+++ b/src/core/org/apache/jmeter/gui/util/HeaderAsPropertyRendererWrapper.java
@@ -0,0 +1,89 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ *
+ */
+
+package org.apache.jmeter.gui.util;
+
+import java.awt.Component;
+import java.io.Serializable;
+
+import javax.swing.JTable;
+import javax.swing.SwingConstants;
+import javax.swing.UIManager;
+import javax.swing.table.DefaultTableCellRenderer;
+import javax.swing.table.JTableHeader;
+import javax.swing.table.TableCellRenderer;
+
+/**
+ * Wraps {@link TableCellRenderer} to renders items in a JTable by using resource names
+ * and control some formatting (centering, fonts and border)
+ */
+public class HeaderAsPropertyRendererWrapper implements TableCellRenderer, Serializable {
+
+    private static final long serialVersionUID = 240L;
+    private Object[][] columnsMsgParameters;
+
+    private TableCellRenderer delegate;
+
+    /**
+     * @param columnsMsgParameters Optional parameters of i18n keys
+     */
+    public HeaderAsPropertyRendererWrapper(TableCellRenderer renderer, Object[][] columnsMsgParameters) {
+        this.delegate = renderer;
+        this.columnsMsgParameters = columnsMsgParameters;
+    }
+
+    @Override
+    public Component getTableCellRendererComponent(JTable table, Object value,
+            boolean isSelected, boolean hasFocus, int row, int column) {
+        if(delegate instanceof DefaultTableCellRenderer) {
+            DefaultTableCellRenderer tr = (DefaultTableCellRenderer) delegate;
+            if (table != null) {
+                JTableHeader header = table.getTableHeader();
+                if (header != null){
+                    tr.setForeground(header.getForeground());
+                    tr.setBackground(header.getBackground());
+                    tr.setFont(header.getFont());
+                }
+            }
+            tr.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
+            tr.setHorizontalAlignment(SwingConstants.CENTER);
+        }
+        return delegate.getTableCellRendererComponent(table, 
+                HeaderAsPropertyRenderer.getText(value, row, column, columnsMsgParameters), 
+                isSelected, hasFocus, row, column);
+    }
+    
+    /**
+     * 
+     * @param table {@link JTable}
+     */
+    public static void setupDefaultRenderer(JTable table) {
+        setupDefaultRenderer(table, null);
+    }
+
+    /**
+     * @param table  {@link JTable}
+     * @param columnsMsgParameters Double dimension array of column message parameters
+     */
+    public static void setupDefaultRenderer(JTable table, Object[][] columnsMsgParameters) {
+        TableCellRenderer defaultRenderer = table.getTableHeader().getDefaultRenderer();
+        HeaderAsPropertyRendererWrapper newRenderer = new HeaderAsPropertyRendererWrapper(defaultRenderer, columnsMsgParameters);
+        table.getTableHeader().setDefaultRenderer(newRenderer);
+    }
+
+}
diff --git a/src/jorphan/org/apache/jorphan/gui/ObjectTableModel.java b/src/jorphan/org/apache/jorphan/gui/ObjectTableModel.java
index a88d72da5..29c495702 100644
--- a/src/jorphan/org/apache/jorphan/gui/ObjectTableModel.java
+++ b/src/jorphan/org/apache/jorphan/gui/ObjectTableModel.java
@@ -1,306 +1,313 @@
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
 
 package org.apache.jorphan.gui;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 
-import javax.swing.event.TableModelEvent;
 import javax.swing.table.DefaultTableModel;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.log.Logger;
 
 /**
  * The ObjectTableModel is a TableModel whose rows are objects;
  * columns are defined as Functors on the object.
  */
 public class ObjectTableModel extends DefaultTableModel {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private transient ArrayList<Object> objects = new ArrayList<>();
 
     private transient List<String> headers = new ArrayList<>();
 
     private transient ArrayList<Class<?>> classes = new ArrayList<>();
 
     private transient ArrayList<Functor> readFunctors = new ArrayList<>();
 
     private transient ArrayList<Functor> writeFunctors = new ArrayList<>();
 
     private transient Class<?> objectClass = null; // if provided
     
     private transient boolean cellEditable = true;
 
     /**
      * The ObjectTableModel is a TableModel whose rows are objects;
      * columns are defined as Functors on the object.
      *
      * @param headers - Column names
      * @param _objClass - Object class that will be used
      * @param readFunctors - used to get the values
      * @param writeFunctors - used to set the values
      * @param editorClasses - class for each column
      */
     public ObjectTableModel(String[] headers, Class<?> _objClass, Functor[] readFunctors, Functor[] writeFunctors, Class<?>[] editorClasses) {
         this(headers, readFunctors, writeFunctors, editorClasses);
         this.objectClass=_objClass;
     }
     
     /**
      * The ObjectTableModel is a TableModel whose rows are objects;
      * columns are defined as Functors on the object.
      *
      * @param headers - Column names
      * @param _objClass - Object class that will be used
      * @param readFunctors - used to get the values
      * @param writeFunctors - used to set the values
      * @param editorClasses - class for each column
      * @param cellEditable - if cell must editable (false to allow double click on cell)
      */
     public ObjectTableModel(String[] headers, Class<?> _objClass, Functor[] readFunctors, 
             Functor[] writeFunctors, Class<?>[] editorClasses, boolean cellEditable) {
         this(headers, readFunctors, writeFunctors, editorClasses);
         this.objectClass=_objClass;
         this.cellEditable = cellEditable;
     }
 
     /**
      * The ObjectTableModel is a TableModel whose rows are objects;
      * columns are defined as Functors on the object.
      *
      * @param headers - Column names
      * @param readFunctors - used to get the values
      * @param writeFunctors - used to set the values
      * @param editorClasses - class for each column
      */
     public ObjectTableModel(String[] headers, Functor[] readFunctors, Functor[] writeFunctors, Class<?>[] editorClasses) {
         this.headers.addAll(Arrays.asList(headers));
         this.classes.addAll(Arrays.asList(editorClasses));
         this.readFunctors = new ArrayList<>(Arrays.asList(readFunctors));
         this.writeFunctors = new ArrayList<>(Arrays.asList(writeFunctors));
 
         int numHeaders = headers.length;
 
         int numClasses = classes.size();
         if (numClasses != numHeaders){
             log.warn("Header count="+numHeaders+" but classes count="+numClasses);
         }
 
         // Functor count = 0 is handled specially
         int numWrite = writeFunctors.length;
         if (numWrite > 0 && numWrite != numHeaders){
             log.warn("Header count="+numHeaders+" but writeFunctor count="+numWrite);
         }
 
         int numRead = readFunctors.length;
         if (numRead > 0 && numRead != numHeaders){
             log.warn("Header count="+numHeaders+" but readFunctor count="+numRead);
         }
     }
 
     private Object readResolve() {
         objects = new ArrayList<>();
         headers = new ArrayList<>();
         classes = new ArrayList<>();
         readFunctors = new ArrayList<>();
         writeFunctors = new ArrayList<>();
         return this;
     }
 
     public Iterator<?> iterator() {
         return objects.iterator();
     }
 
     public void clearData() {
-        int size = getRowCount();
         objects.clear();
-        super.fireTableRowsDeleted(0, size);
+        super.fireTableDataChanged();
     }
 
     public void addRow(Object value) {
         log.debug("Adding row value: " + value);
         if (objectClass != null) {
             final Class<?> valueClass = value.getClass();
             if (!objectClass.isAssignableFrom(valueClass)){
                 throw new IllegalArgumentException("Trying to add class: "+valueClass.getName()
                         +"; expecting class: "+objectClass.getName());
             }
         }
         objects.add(value);
-        super.fireTableRowsInserted(objects.size() - 1, objects.size());
+        super.fireTableRowsInserted(objects.size() - 1, objects.size() - 1);
     }
 
     public void insertRow(Object value, int index) {
         objects.add(index, value);
-        super.fireTableRowsInserted(index, index + 1);
+        super.fireTableRowsInserted(index, index);
     }
 
     /** {@inheritDoc} */
     @Override
     public int getColumnCount() {
         return headers.size();
     }
 
     /** {@inheritDoc} */
     @Override
     public String getColumnName(int col) {
         return headers.get(col);
     }
 
     /** {@inheritDoc} */
     @Override
     public int getRowCount() {
         if (objects == null) {
             return 0;
         }
         return objects.size();
     }
 
     /** {@inheritDoc} */
     @Override
     public Object getValueAt(int row, int col) {
         log.debug("Getting row value");
         Object value = objects.get(row);
         if(headers.size() == 1 && col >= readFunctors.size()) {
             return value;
         }
         Functor getMethod = readFunctors.get(col);
         if (getMethod != null && value != null) {
             return getMethod.invoke(value);
         }
         return null;
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean isCellEditable(int arg0, int arg1) {
         return cellEditable;
     }
 
     /** {@inheritDoc} */
     @Override
     public void moveRow(int start, int end, int to) {
-        List<Object> subList = new ArrayList<>(objects.subList(start, end));
-        for (int x = end - 1; x >= start; x--) {
-            objects.remove(x);
-        }
-        objects.addAll(to, subList);
-        super.fireTableChanged(new TableModelEvent(this));
+        List<Object> subList = objects.subList(start, end);
+        List<Object> backup  = new ArrayList<>(subList);
+        subList.clear();
+        objects.addAll(to, backup);
+        super.fireTableDataChanged();
     }
 
     /** {@inheritDoc} */
     @Override
     public void removeRow(int row) {
         objects.remove(row);
         super.fireTableRowsDeleted(row, row);
     }
 
     /** {@inheritDoc} */
     @Override
     public void setValueAt(Object cellValue, int row, int col) {
         if (row < objects.size()) {
             Object value = objects.get(row);
             if (col < writeFunctors.size()) {
                 Functor setMethod = writeFunctors.get(col);
                 if (setMethod != null) {
                     setMethod.invoke(value, new Object[] { cellValue });
                     super.fireTableDataChanged();
                 }
             }
             else if(headers.size() == 1)
             {
                 objects.set(row,cellValue);
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public Class<?> getColumnClass(int arg0) {
         return classes.get(arg0);
     }
 
     /**
      * Check all registered functors.
      * <p>
      * <b>** only for use in unit test code **</b>
      * </p>
      *
      * @param _value - an instance of the table model row data item
      * (if null, use the class passed to the constructor).
      *
      * @param caller - class of caller.
      *
      * @return false if at least one Functor cannot be found.
      */
     @SuppressWarnings("deprecation")
     public boolean checkFunctors(Object _value, Class<?> caller){
         Object value;
         if (_value == null && objectClass != null) {
             try {
                 value = objectClass.newInstance();
             } catch (InstantiationException e) {
                 log.error("Cannot create instance of class "+objectClass.getName(),e);
                 return false;
             } catch (IllegalAccessException e) {
                 log.error("Cannot create instance of class "+objectClass.getName(),e);
                 return false;
             }
         } else {
             value = _value;
         }
         boolean status = true;
         for(int i=0;i<getColumnCount();i++){
             Functor setMethod = writeFunctors.get(i);
             if (setMethod != null) {
                 if (!setMethod.checkMethod(value,getColumnClass(i))){
                     status=false;
                     log.warn(caller.getName()+" is attempting to use nonexistent "+setMethod.toString());
                 }
             }
             Functor getMethod = readFunctors.get(i);
             if (getMethod != null) {
                 if (!getMethod.checkMethod(value)){
                     status=false;
                     log.warn(caller.getName()+" is attempting to use nonexistent "+getMethod.toString());
                 }
             }
 
         }
         return status;
     }
 
+    /**
+     * @return Object (List of Object)
+     */
     public Object getObjectList() { // used by TableEditor
         return objects;
     }
+    
+    /**
+     * @return List of Object
+     */
+    public List<Object> getObjectListAsList() { 
+        return objects;
+    }
 
     public void setRows(Iterable<?> rows) { // used by TableEditor
         clearData();
         for(Object val : rows)
         {
             addRow(val);
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/gui/ObjectTableSorter.java b/src/jorphan/org/apache/jorphan/gui/ObjectTableSorter.java
new file mode 100644
index 000000000..c96a093e4
--- /dev/null
+++ b/src/jorphan/org/apache/jorphan/gui/ObjectTableSorter.java
@@ -0,0 +1,356 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package org.apache.jorphan.gui;
+
+import static java.lang.String.format;
+
+import java.text.Collator;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.Comparator;
+import java.util.List;
+import java.util.Objects;
+import java.util.function.Function;
+import java.util.stream.IntStream;
+import java.util.stream.Stream;
+
+import javax.swing.RowSorter;
+import javax.swing.SortOrder;
+
+/**
+ * Implementation of a {@link RowSorter} for {@link ObjectTableModel}
+ * @since 3.2
+ *
+ */
+public class ObjectTableSorter extends RowSorter<ObjectTableModel> {
+
+    /**
+     * View row with model mapping. All data relates to model.
+     */
+    public class Row {
+        private int index;
+
+        protected Row(int index) {
+            this.index = index;
+        }
+
+        public int getIndex() {
+            return index;
+        }
+
+        public Object getValue() {
+            return getModel().getObjectListAsList().get(getIndex());
+        }
+
+        public Object getValueAt(int column) {
+            return getModel().getValueAt(getIndex(), column);
+        }
+    }
+
+    protected class PreserveLastRowComparator implements Comparator<Row> {
+        @Override
+        public int compare(Row o1, Row o2) {
+            int lastIndex = model.getRowCount() - 1;
+            if (o1.getIndex() >= lastIndex || o2.getIndex() >= lastIndex) {
+                return o1.getIndex() - o2.getIndex();
+            }
+            return 0;
+        }
+    }
+
+    private ObjectTableModel model;
+    private SortKey sortkey;
+
+    private Comparator<Row> comparator  = null;
+    private ArrayList<Row>  viewToModel = new ArrayList<>();
+    private int[]           modelToView = new int[0];
+
+    private Comparator<Row>  primaryComparator = null;
+    private Comparator<?>[]  valueComparators;
+    private Comparator<Row>  fallbackComparator;
+
+    public ObjectTableSorter(ObjectTableModel model) {
+        this.model = model;
+
+        this.valueComparators = new Comparator<?>[this.model.getColumnCount()];
+        IntStream.range(0, this.valueComparators.length).forEach(i -> this.setValueComparator(i, null));
+
+        setFallbackComparator(null);
+    }
+
+    /**
+     * Comparator used prior to sorted columns.
+     */
+    public Comparator<Row> getPrimaryComparator() {
+        return primaryComparator;
+    }
+
+    /**
+     * Comparator used on sorted columns.
+     */
+    public Comparator<?> getValueComparator(int column) {
+        return valueComparators[column];
+    }
+
+    /**
+     * Comparator if all sorted columns matches. Defaults to model index comparison.
+     */
+    public Comparator<Row> getFallbackComparator() {
+        return fallbackComparator;
+    }
+
+    /**
+     * Comparator used prior to sorted columns.
+     * @return <code>this</code>
+     */
+    public ObjectTableSorter setPrimaryComparator(Comparator<Row> primaryComparator) {
+      invalidate();
+      this.primaryComparator = primaryComparator;
+      return this;
+    }
+
+    /**
+     * Sets {@link #getPrimaryComparator() primary comparator} to one that don't sort last row.
+     * @return <code>this</code>
+     */
+    public ObjectTableSorter fixLastRow() {
+        return setPrimaryComparator(new PreserveLastRowComparator());
+    }
+
+    /**
+     * Assign comparator to given column, if <code>null</code> a {@link #getDefaultComparator(int) default one} is used instead.
+     * @param column Model column index.
+     * @param comparator Column value comparator.
+     * @return <code>this</code>
+     */
+    public ObjectTableSorter setValueComparator(int column, Comparator<?> comparator) {
+        invalidate();
+        if (comparator == null) {
+            comparator = getDefaultComparator(column);
+        }
+        valueComparators[column] = comparator;
+        return this;
+    }
+
+    /**
+     * Builds a default comparator based on model column class. {@link Collator#getInstance()} for {@link String},
+     * {@link Comparator#naturalOrder() natural order} for {@link Comparable}, no sort support for others.
+     * @param column Model column index.
+     */
+    protected Comparator<?> getDefaultComparator(int column) {
+        Class<?> columnClass = model.getColumnClass(column);
+        if (columnClass == null) {
+            return null;
+        }
+        if (columnClass == String.class) {
+            return Comparator.nullsFirst(Collator.getInstance());
+        }
+        if (Comparable.class.isAssignableFrom(columnClass)) {
+            return Comparator.nullsFirst(Comparator.naturalOrder());
+        }
+        return null;
+    }
+
+    /**
+     * Sets a fallback comparator (defaults to model index comparison) if none {@link #getPrimaryComparator() primary}, neither {@link #getValueComparator(int) column value comparators} can make differences between two rows.
+     * @return <code>this</code>
+     */
+    public ObjectTableSorter setFallbackComparator(Comparator<Row> comparator) {
+        invalidate();
+        if (comparator == null) {
+            comparator = Comparator.comparingInt(Row::getIndex);
+        }
+        fallbackComparator = comparator;
+        return this;
+    }
+
+    @Override
+    public ObjectTableModel getModel() {
+        return model;
+    }
+
+    @Override
+    public void toggleSortOrder(int column) {
+        SortKey newSortKey;
+        if (isSortable(column)) {
+            SortOrder newOrder = sortkey == null || sortkey.getColumn() != column
+                    || sortkey.getSortOrder() != SortOrder.ASCENDING ? SortOrder.ASCENDING : SortOrder.DESCENDING;
+            newSortKey = new SortKey(column, newOrder);
+        } else {
+            newSortKey = null;
+        }
+        setSortKey(newSortKey);
+    }
+
+    @Override
+    public int convertRowIndexToModel(int index) {
+        if (!isSorted()) {
+            return index;
+        }
+        validate();
+        return viewToModel.get(index).getIndex();
+    }
+
+    @Override
+    public int convertRowIndexToView(int index) {
+        if (!isSorted()) {
+            return index;
+        }
+        validate();
+        return modelToView[index];
+    }
+
+    @Override
+    public void setSortKeys(List<? extends SortKey> keys) {
+        switch (keys.size()) {
+            case 0:
+                setSortKey(null);
+                break;
+            case 1:
+                setSortKey(keys.get(0));
+                break;
+            default:
+                throw new IllegalArgumentException("Only one column can be sorted");
+        }
+    }
+
+    public void setSortKey(SortKey sortkey) {
+        if (Objects.equals(this.sortkey, sortkey)) {
+            return;
+        }
+
+        invalidate();
+        if (sortkey != null) {
+            int column = sortkey.getColumn();
+            Comparator<?> comparator = valueComparators[column];
+            if (comparator == null) {
+                throw new IllegalArgumentException(format("Can't sort column %s, it is mapped to type %s and this one have no natural order. So an explicit one must be specified", column, model.getColumnClass(column)));
+            }
+        }
+        this.sortkey    = sortkey;
+        this.comparator = null;
+    }
+
+    @Override
+    public List<? extends SortKey> getSortKeys() {
+        return isSorted() ? Collections.singletonList(sortkey) : Collections.emptyList();
+    }
+
+    @Override
+    public int getViewRowCount() {
+        return getModelRowCount();
+    }
+
+    @Override
+    public int getModelRowCount() {
+        return model.getRowCount();
+    }
+
+    @Override
+    public void modelStructureChanged() {
+        setSortKey(null);
+    }
+
+    @Override
+    public void allRowsChanged() {
+        invalidate();
+    }
+
+    @Override
+    public void rowsInserted(int firstRow, int endRow) {
+        rowsChanged(firstRow, endRow, false, true);
+    }
+
+    @Override
+    public void rowsDeleted(int firstRow, int endRow) {
+        rowsChanged(firstRow, endRow, true, false);
+    }
+
+    @Override
+    public void rowsUpdated(int firstRow, int endRow) {
+        rowsChanged(firstRow, endRow, true, true);
+    }
+
+    protected void rowsChanged(int firstRow, int endRow, boolean deleted, boolean inserted) {
+        invalidate();
+    }
+
+    @Override
+    public void rowsUpdated(int firstRow, int endRow, int column) {
+        if (isSorted(column)) {
+            rowsUpdated(firstRow, endRow);
+        }
+    }
+
+    protected boolean isSortable(int column) {
+        return getValueComparator(column) != null;
+    }
+
+    protected boolean isSorted(int column) {
+        return isSorted() && sortkey.getColumn() == column && sortkey.getSortOrder() != SortOrder.UNSORTED;
+    }
+
+    protected boolean isSorted() {
+        return sortkey != null;
+    }
+
+    protected void invalidate() {
+      viewToModel.clear();
+      modelToView = new int[0];
+    }
+
+    protected void validate() {
+      if (isSorted() && viewToModel.isEmpty()) {
+          sort();
+      }
+    }
+
+    @SuppressWarnings({ "rawtypes", "unchecked" })
+    protected Comparator<Row> getComparatorFromSortKey(SortKey sortkey) {
+        Comparator comparator = getValueComparator(sortkey.getColumn());
+        if (sortkey.getSortOrder() == SortOrder.DESCENDING) {
+            comparator = comparator.reversed();
+        }
+        Function<Row,Object> getValueAt = (Row row) -> row.getValueAt(sortkey.getColumn());
+        return Comparator.comparing(getValueAt, comparator);
+    }
+
+    protected void sort() {
+        if (comparator == null) {
+            comparator = Stream.concat(
+                    Stream.concat(
+                            getPrimaryComparator() != null ? Stream.of(getPrimaryComparator()) : Stream.<Comparator<Row>>empty(),
+                            getSortKeys().stream().filter(sk -> sk != null && sk.getSortOrder() != SortOrder.UNSORTED).map(this::getComparatorFromSortKey)
+                    ),
+                    Stream.of(getFallbackComparator())
+            ).reduce(comparator, (result, current) -> result != null ? result.thenComparing(current) : current);
+        }
+
+        viewToModel.clear();
+        viewToModel.ensureCapacity(model.getRowCount());
+        IntStream.range(0, model.getRowCount()).mapToObj(i -> new Row(i)).forEach(viewToModel::add);
+        Collections.sort(viewToModel, comparator);
+
+        updateModelToView();
+    }
+
+    protected void updateModelToView() {
+        modelToView = new int[viewToModel.size()];
+        IntStream.range(0, viewToModel.size()).forEach(viewIndex -> modelToView[viewToModel.get(viewIndex).getIndex()] = viewIndex);
+    }
+}
diff --git a/test/src/org/apache/jorphan/gui/ObjectTableModelTest.java b/test/src/org/apache/jorphan/gui/ObjectTableModelTest.java
new file mode 100644
index 000000000..397514f78
--- /dev/null
+++ b/test/src/org/apache/jorphan/gui/ObjectTableModelTest.java
@@ -0,0 +1,251 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package org.apache.jorphan.gui;
+
+import static java.lang.String.format;
+import static java.util.stream.IntStream.range;
+import static org.junit.Assert.assertEquals;
+
+import java.util.Arrays;
+import java.util.stream.IntStream;
+
+import javax.swing.event.TableModelEvent;
+
+import org.apache.jorphan.reflect.Functor;
+import org.junit.Before;
+import org.junit.Test;
+
+public class ObjectTableModelTest {
+
+    public static class Dummy {
+        String a;
+        String b;
+        String c;
+
+        Dummy(String a, String b, String c) {
+            this.a = a;
+            this.b = b;
+            this.c = c;
+        }
+
+        public String getA() {
+            return a;
+        }
+
+        public String getB() {
+            return b;
+        }
+
+        public String getC() {
+            return c;
+        }
+    }
+
+    ObjectTableModel model;
+    TableModelEventBacker events;
+
+    @Before
+    public void init() {
+        String[] headers = { "a", "b", "c" };
+        Functor[] readFunctors = Arrays.stream(headers).map(name -> "get" + name.toUpperCase()).map(Functor::new).toArray(n -> new Functor[n]);
+        Functor[] writeFunctors = new Functor[headers.length];
+        Class<?>[] editorClasses = new Class<?>[headers.length];
+        Arrays.fill(editorClasses, String.class);
+        model = new ObjectTableModel(headers, readFunctors, writeFunctors, editorClasses);
+        events = new TableModelEventBacker();
+    }
+
+    @Test
+    public void checkAddRow() {
+        model.addTableModelListener(events);
+
+        assertModel();
+
+        model.addRow(new Dummy("1", "1", "1"));
+        assertModel("1");
+        events.assertEvents(
+                events.assertEvent()
+                    .source(model)
+                    .type(TableModelEvent.INSERT)
+                    .column(TableModelEvent.ALL_COLUMNS)
+                    .firstRow(0)
+                    .lastRow(0)
+        );
+
+        model.addRow(new Dummy("2", "1", "1"));
+        assertModel("1", "2");
+        events.assertEvents(
+                events.assertEvent()
+                    .source(model)
+                    .type(TableModelEvent.INSERT)
+                    .column(TableModelEvent.ALL_COLUMNS)
+                    .firstRow(1)
+                    .lastRow(1)
+        );
+    }
+
+    @Test
+    public void checkClear() {
+        // Arrange
+        for (int i = 0; i < 5; i++) {
+            model.addRow(new Dummy("" + i, "" + i%2, "" + i%3));
+        }
+        assertModelRanges(range(0,5));
+
+        // Act
+        model.addTableModelListener(events);
+        model.clearData();
+
+        // Assert
+        assertModelRanges();
+
+
+        events.assertEvents(
+                events.assertEvent()
+                    .source(model)
+                    .type(TableModelEvent.UPDATE)
+                    .column(TableModelEvent.ALL_COLUMNS)
+                    .firstRow(0)
+                    .lastRow(Integer.MAX_VALUE)
+        );
+    }
+
+    @Test
+    public void checkInsertRow() {
+        assertModel();
+        model.addRow(new Dummy("3", "1", "1"));
+        assertModel("3");
+        model.addTableModelListener(events);
+
+        model.insertRow(new Dummy("1", "1", "1"), 0);
+        assertModel("1", "3");
+        events.assertEvents(
+                events.assertEvent()
+                    .source(model)
+                    .type(TableModelEvent.INSERT)
+                    .column(TableModelEvent.ALL_COLUMNS)
+                    .firstRow(0)
+                    .lastRow(0)
+       );
+
+       model.insertRow(new Dummy("2", "1", "1"), 1);
+       assertModel("1", "2", "3");
+       events.assertEvents(
+               events.assertEvent()
+                   .source(model)
+                   .type(TableModelEvent.INSERT)
+                   .column(TableModelEvent.ALL_COLUMNS)
+                   .firstRow(1)
+                   .lastRow(1)
+      );
+
+
+    }
+
+    @Test
+    public void checkMoveRow_from_5_11_to_0() {
+        // Arrange
+        for (int i = 0; i < 20; i++) {
+            model.addRow(new Dummy("" + i, "" + i%2, "" + i%3));
+        }
+        assertModelRanges(range(0, 20));
+
+        // Act
+        model.addTableModelListener(events);
+        model.moveRow(5, 11, 0);
+
+        // Assert
+        assertModelRanges(range(5, 11), range(0, 5), range(11, 20));
+
+        events.assertEvents(
+                events.assertEvent()
+                    .source(model)
+                    .type(TableModelEvent.UPDATE)
+                    .column(TableModelEvent.ALL_COLUMNS)
+                    .firstRow(0)
+                    .lastRow(Integer.MAX_VALUE)
+        );
+    }
+
+    @Test
+    public void checkMoveRow_from_0_6_to_0() {
+        // Arrange
+        for (int i = 0; i < 20; i++) {
+            model.addRow(new Dummy("" + i, "" + i%2, "" + i%3));
+        }
+        assertModelRanges(range(0, 20));
+
+        // Act
+        model.addTableModelListener(events);
+        model.moveRow(0, 6, 0);
+
+        // Assert
+        assertModelRanges(range(0, 20));
+
+        events.assertEvents(
+                events.assertEvent()
+                    .source(model)
+                    .type(TableModelEvent.UPDATE)
+                    .column(TableModelEvent.ALL_COLUMNS)
+                    .firstRow(0)
+                    .lastRow(Integer.MAX_VALUE)
+        );
+    }
+
+    @Test
+    public void checkMoveRow_from_0_6_to_10() {
+        // Arrange
+        for (int i = 0; i < 20; i++) {
+            model.addRow(new Dummy("" + i, "" + i%2, "" + i%3));
+        }
+        assertModelRanges(range(0, 20));
+
+        // Act
+        model.addTableModelListener(events);
+        model.moveRow(0, 6, 10);
+
+        // Assert
+        assertModelRanges(range(6, 16), range(0, 6), range(16, 20));
+
+        events.assertEvents(
+                events.assertEvent()
+                    .source(model)
+                    .type(TableModelEvent.UPDATE)
+                    .column(TableModelEvent.ALL_COLUMNS)
+                    .firstRow(0)
+                    .lastRow(Integer.MAX_VALUE)
+        );
+    }
+
+    private void assertModelRanges(IntStream... ranges) {
+        IntStream ints = IntStream.empty();
+        for (IntStream range : ranges) {
+            ints = IntStream.concat(ints, range);
+        }
+        assertModel(ints.mapToObj(i -> "" + i).toArray(n -> new String[n]));
+    }
+
+    private void assertModel(String... as) {
+        assertEquals("model row count", as.length, model.getRowCount());
+
+        for (int row = 0; row < as.length; row++) {
+            assertEquals(format("model[%d,0]", row), as[row], model.getValueAt(row, 0));
+        }
+    }
+
+}
diff --git a/test/src/org/apache/jorphan/gui/ObjectTableSorterTest.java b/test/src/org/apache/jorphan/gui/ObjectTableSorterTest.java
new file mode 100644
index 000000000..6580bb06e
--- /dev/null
+++ b/test/src/org/apache/jorphan/gui/ObjectTableSorterTest.java
@@ -0,0 +1,304 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package org.apache.jorphan.gui;
+
+import static java.lang.String.format;
+import static java.util.Arrays.asList;
+import static java.util.Collections.emptyList;
+import static java.util.Collections.singletonList;
+import static org.hamcrest.CoreMatchers.allOf;
+import static org.hamcrest.CoreMatchers.equalTo;
+import static org.hamcrest.CoreMatchers.is;
+import static org.hamcrest.CoreMatchers.not;
+import static org.hamcrest.CoreMatchers.nullValue;
+import static org.hamcrest.CoreMatchers.sameInstance;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertSame;
+import static org.junit.Assert.assertThat;
+
+import java.util.AbstractMap;
+import java.util.AbstractMap.SimpleImmutableEntry;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.Comparator;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map.Entry;
+import java.util.stream.Collectors;
+import java.util.stream.IntStream;
+
+import javax.swing.RowSorter.SortKey;
+import javax.swing.SortOrder;
+
+import org.apache.jorphan.reflect.Functor;
+import org.hamcrest.CoreMatchers;
+import org.junit.Before;
+import org.junit.Rule;
+import org.junit.Test;
+import org.junit.rules.ErrorCollector;
+import org.junit.rules.ExpectedException;
+
+public class ObjectTableSorterTest {
+    ObjectTableModel  model;
+    ObjectTableSorter sorter;
+
+    @Rule
+    public ExpectedException expectedException = ExpectedException.none();
+    @Rule
+    public ErrorCollector errorCollector = new ErrorCollector();
+
+    @Before
+    public void createModelAndSorter() {
+        String[] headers         = { "key", "value", "object" };
+        Functor[] readFunctors   = { new Functor("getKey"), new Functor("getValue"), new Functor("getValue") };
+        Functor[] writeFunctors  = { null, null, null };
+        Class<?>[] editorClasses = { String.class, Integer.class, Object.class };
+        model                    = new ObjectTableModel(headers, readFunctors, writeFunctors, editorClasses);
+        sorter                   = new ObjectTableSorter(model);
+        List<Entry<String,Integer>> data = asList(b2(), a3(), d4(), c1());
+        data.forEach(model::addRow);
+    }
+
+    @Test
+    public void noSorting() {
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(b2(), a3(), d4(), c1());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void sortKeyAscending() {
+        sorter.setSortKey(new SortKey(0, SortOrder.ASCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(a3(), b2(), c1(), d4());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void sortKeyDescending() {
+        sorter.setSortKey(new SortKey(0, SortOrder.DESCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(d4(), c1(), b2(), a3());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void sortValueAscending() {
+        sorter.setSortKey(new SortKey(1, SortOrder.ASCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(c1(), b2(), a3(), d4());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void sortValueDescending() {
+        sorter.setSortKey(new SortKey(1, SortOrder.DESCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(d4(), a3(), b2(), c1());
+        assertRowOrderAndIndexes(expected);
+    }
+
+
+    @Test
+    public void fixLastRowWithAscendingKey() {
+        sorter.fixLastRow().setSortKey(new SortKey(0, SortOrder.ASCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(a3(), b2(), d4(), c1());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void fixLastRowWithDescendingKey() {
+        sorter.fixLastRow().setSortKey(new SortKey(0, SortOrder.DESCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(d4(), b2(), a3(), c1());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void fixLastRowWithAscendingValue() {
+        sorter.fixLastRow().setSortKey(new SortKey(1, SortOrder.ASCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(b2(), a3(), d4(), c1());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void fixLastRowWithDescendingValue() {
+        sorter.fixLastRow().setSortKey(new SortKey(1, SortOrder.DESCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(d4(), a3(), b2(), c1());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void customKeyOrder() {
+        HashMap<String, Integer> customKeyOrder = asList("a", "c", "b", "d").stream().reduce(new HashMap<String,Integer>(), (map,key) -> { map.put(key, map.size()); return map; }, (a,b) -> a);
+        Comparator<String> customKeyComparator = (a,b) -> customKeyOrder.get(a).compareTo(customKeyOrder.get(b));
+        sorter.setValueComparator(0, customKeyComparator).setSortKey(new SortKey(0, SortOrder.ASCENDING));
+        List<SimpleImmutableEntry<String, Integer>> expected = asList(a3(), c1(), b2(), d4());
+        assertRowOrderAndIndexes(expected);
+    }
+
+    @Test
+    public void getDefaultComparatorForNullClass() {
+        ObjectTableModel model = new ObjectTableModel(new String[] { "null" }, new Functor[] { null }, new Functor[] { null }, new Class<?>[] { null });
+        ObjectTableSorter sorter = new ObjectTableSorter(model);
+
+        assertThat(sorter.getValueComparator(0), is(nullValue()));
+    }
+
+    @Test
+    public void getDefaultComparatorForStringClass() {
+        ObjectTableModel model = new ObjectTableModel(new String[] { "string" }, new Functor[] { null }, new Functor[] { null }, new Class<?>[] { String.class });
+        ObjectTableSorter sorter = new ObjectTableSorter(model);
+
+        assertThat(sorter.getValueComparator(0), is(CoreMatchers.notNullValue()));
+    }
+
+    @Test
+    public void getDefaultComparatorForIntegerClass() {
+        ObjectTableModel model = new ObjectTableModel(new String[] { "integer" }, new Functor[] { null }, new Functor[] { null }, new Class<?>[] { Integer.class });
+        ObjectTableSorter sorter = new ObjectTableSorter(model);
+
+        assertThat(sorter.getValueComparator(0), is(CoreMatchers.notNullValue()));
+    }
+
+    @Test
+    public void getDefaultComparatorForObjectClass() {
+        ObjectTableModel model = new ObjectTableModel(new String[] { "integer" }, new Functor[] { null }, new Functor[] { null }, new Class<?>[] { Object.class });
+        ObjectTableSorter sorter = new ObjectTableSorter(model);
+
+        assertThat(sorter.getValueComparator(0), is(nullValue()));
+    }
+
+    @Test
+    public void toggleSortOrder_none() {
+        assertSame(emptyList(), sorter.getSortKeys());
+    }
+
+    @Test
+    public void toggleSortOrder_0() {
+        sorter.toggleSortOrder(0);
+        assertEquals(singletonList(new SortKey(0, SortOrder.ASCENDING)), sorter.getSortKeys());
+    }
+
+    @Test
+    public void toggleSortOrder_0_1() {
+        sorter.toggleSortOrder(0);
+        sorter.toggleSortOrder(1);
+        assertEquals(singletonList(new SortKey(1, SortOrder.ASCENDING)), sorter.getSortKeys());
+    }
+
+    @Test
+    public void toggleSortOrder_0_0() {
+        sorter.toggleSortOrder(0);
+        sorter.toggleSortOrder(0);
+        assertEquals(singletonList(new SortKey(0, SortOrder.DESCENDING)), sorter.getSortKeys());
+    }
+
+    @Test
+    public void toggleSortOrder_0_0_0() {
+        sorter.toggleSortOrder(0);
+        sorter.toggleSortOrder(0);
+        sorter.toggleSortOrder(0);
+        assertEquals(singletonList(new SortKey(0, SortOrder.ASCENDING)), sorter.getSortKeys());
+    }
+
+    @Test
+    public void toggleSortOrder_2() {
+        sorter.toggleSortOrder(2);
+        assertSame(emptyList(), sorter.getSortKeys());
+    }
+
+    @Test
+    public void toggleSortOrder_0_2() {
+        sorter.toggleSortOrder(0);
+        sorter.toggleSortOrder(2);
+        assertSame(emptyList(), sorter.getSortKeys());
+    }
+
+    @Test
+    public void setSortKeys_none() {
+        sorter.setSortKeys(new ArrayList<>());
+        assertSame(Collections.emptyList(), sorter.getSortKeys());
+    }
+
+    @Test
+    public void setSortKeys_withSortedThenUnsorted() {
+        sorter.setSortKeys(singletonList(new SortKey(0, SortOrder.ASCENDING)));
+        sorter.setSortKeys(new ArrayList<>());
+        assertSame(Collections.emptyList(), sorter.getSortKeys());
+    }
+
+    @Test
+    public void setSortKeys_single() {
+        List<SortKey> keys = singletonList(new SortKey(0, SortOrder.ASCENDING));
+        sorter.setSortKeys(keys);
+        assertThat(sorter.getSortKeys(), allOf(  is(not(sameInstance(keys))),  is(equalTo(keys)) ));
+    }
+
+    @Test
+    public void setSortKeys_many() {
+        expectedException.expect(IllegalArgumentException.class);
+
+        sorter.setSortKeys(asList(new SortKey(0, SortOrder.ASCENDING), new SortKey(1, SortOrder.ASCENDING)));
+    }
+
+    @Test
+    public void setSortKeys_invalidColumn() {
+        expectedException.expect(IllegalArgumentException.class);
+
+        sorter.setSortKeys(Collections.singletonList(new SortKey(2, SortOrder.ASCENDING)));
+    }
+
+
+    @SuppressWarnings("unchecked")
+    protected List<Entry<String,Integer>> actual() {
+        return IntStream
+                .range(0, sorter.getViewRowCount())
+                .map(sorter::convertRowIndexToModel)
+                .mapToObj(modelIndex -> (Entry<String,Integer>) sorter.getModel().getObjectListAsList().get(modelIndex))
+                .collect(Collectors.toList())
+                ;
+    }
+
+    protected SimpleImmutableEntry<String, Integer> d4() {
+        return new AbstractMap.SimpleImmutableEntry<>("d",  4);
+    }
+
+    protected SimpleImmutableEntry<String, Integer> c1() {
+        return new AbstractMap.SimpleImmutableEntry<>("c",  1);
+    }
+
+    protected SimpleImmutableEntry<String, Integer> b2() {
+        return new AbstractMap.SimpleImmutableEntry<>("b",  2);
+    }
+
+    protected SimpleImmutableEntry<String, Integer> a3() {
+        return new AbstractMap.SimpleImmutableEntry<>("a",  3);
+    }
+
+    protected void assertRowOrderAndIndexes(List<SimpleImmutableEntry<String, Integer>> expected) {
+        assertEquals(expected, actual());
+        assertRowIndexes();
+    }
+
+    protected void assertRowIndexes() {
+        IntStream
+            .range(0, sorter.getViewRowCount())
+            .forEach(viewIndex -> {
+                int modelIndex = sorter.convertRowIndexToModel(viewIndex);
+                errorCollector.checkThat(format("view(%d) model(%d)", viewIndex, modelIndex),
+                        sorter.convertRowIndexToView(modelIndex),
+                        CoreMatchers.equalTo(viewIndex));
+            });
+
+    }
+}
diff --git a/test/src/org/apache/jorphan/gui/TableModelEventBacker.java b/test/src/org/apache/jorphan/gui/TableModelEventBacker.java
new file mode 100644
index 000000000..3d5360899
--- /dev/null
+++ b/test/src/org/apache/jorphan/gui/TableModelEventBacker.java
@@ -0,0 +1,154 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+
+package org.apache.jorphan.gui;
+
+import static java.lang.String.format;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertSame;
+
+import java.util.ArrayList;
+import java.util.Deque;
+import java.util.LinkedList;
+import java.util.List;
+import java.util.function.ObjIntConsumer;
+import java.util.function.ToIntFunction;
+
+import javax.swing.event.TableModelEvent;
+import javax.swing.event.TableModelListener;
+
+/**
+ * Listener implementation that stores {@link TableModelEvent} and can make assertions against them.
+ */
+public class TableModelEventBacker implements TableModelListener {
+
+    /**
+     * Makes assertions for a single {@link TableModelEvent}.
+     */
+    public class EventAssertion {
+        private List<ObjIntConsumer<TableModelEvent>> assertions = new ArrayList<>();
+
+        /**
+         * Adds an assertion first args is table model event, second one is event index.
+         * @return <code>this</code>
+         */
+        public EventAssertion add(ObjIntConsumer<TableModelEvent> assertion) {
+            assertions.add(assertion);
+            return this;
+        }
+
+        /**
+         * Adds assertion based on a {@link ToIntFunction to-int} transformation (examples: <code>TableModelEvent::getType</code>).
+         * @param name Label for assertion reason
+         * @param expected Expected value.
+         * @param f {@link ToIntFunction to-int} transformation (examples: <code>TableModelEvent::getType</code>).
+         * @return <code>this</code>
+         */
+        public EventAssertion addInt(String name, int expected, ToIntFunction<TableModelEvent> f) {
+            return add((e,i) -> assertEquals(format("%s[%d]", name, i), expected, f.applyAsInt(e)));
+        }
+
+        /**
+         * Adds {@link TableModelEvent#getSource()} assertion.
+         * @return <code>this</code>
+         */
+        public EventAssertion source(Object expected) {
+            return add((e,i) -> assertSame(format("source[%d]",i), expected, e.getSource()));
+        }
+
+        /**
+         * Adds {@link TableModelEvent#getType()} assertion.
+         * @return <code>this</code>
+         */
+        public EventAssertion type(int expected) {
+            return addInt("type", expected, TableModelEvent::getType);
+        }
+
+        /**
+         * Adds {@link TableModelEvent#getColumn()} assertion.
+         * @return <code>this</code>
+         */
+        public EventAssertion column(int expected) {
+            return addInt("column", expected, TableModelEvent::getColumn);
+        }
+
+        /**
+         * Adds {@link TableModelEvent#getFirstRow()} assertion.
+         * @return <code>this</code>
+         */
+        public EventAssertion firstRow(int expected) {
+            return addInt("firstRow", expected, TableModelEvent::getFirstRow);
+        }
+
+        /**
+         * Adds {@link TableModelEvent#getLastRow()} assertion.
+         * @return <code>this</code>
+         */
+        public EventAssertion lastRow(int expected) {
+            return addInt("lastRow", expected, TableModelEvent::getLastRow);
+        }
+
+        /**
+         * Check assertion against provided value.
+         * @param event Event to check
+         * @param index Index.
+         */
+        protected void assertEvent(TableModelEvent event, int index) {
+            assertions.forEach(a -> a.accept(event, index));
+        }
+    }
+
+    private Deque<TableModelEvent> events = new LinkedList<>();
+
+    /**
+     * Stores event.
+     */
+    @Override
+    public void tableChanged(TableModelEvent e) {
+        events.add(e);
+    }
+
+    public Deque<TableModelEvent> getEvents() {
+        return events;
+    }
+
+    /**
+     * Creates a new event assertion.
+     * @see #assertEvents(EventAssertion...)
+     */
+    public EventAssertion assertEvent() {
+        return new EventAssertion();
+    }
+
+    /**
+     * Checks each event assertion against each backed event in order. Event storage is cleared after it.
+     */
+    public void assertEvents(EventAssertion... assertions) {
+        try {
+            assertEquals("event count", assertions.length, events.size());
+
+            int i = 0;
+            for (TableModelEvent event : events) {
+                assertions[i].assertEvent(event, i++);
+            }
+        } finally {
+            events.clear();
+        }
+    }
+
+
+}
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 6b0b33c99..b1f8cd252 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,308 +1,309 @@
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
 <!ENTITY rarr     "&#x02192;" >
 <!ENTITY vellip   "&#x022EE;" >
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
 
 
 <!--  =================== 3.2 =================== -->
 
 <h1>Version 3.2</h1>
 <p>
 Summary
 </p>
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Known problems and workarounds">Known problems and workarounds</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_section>IMPORTANT CHANGE</ch_section>
 <p>
 Fill in some detail.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>Documentation review and improvements for easier startup</li>
 <li>New <a href="usermanual/properties_reference.html">properties reference</a> documentation section</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
     <li><bug>60542</bug>View Results Tree : Allow Upper Panel to be collapsed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
+    <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux@gmail.com).</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
-    <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize and Maxime Chassagneux</li>
+    <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux@gmail.com).</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to xxx-1.1 (from 0.2)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of TestHTTPMirrorThread#testSleep(). Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Jerome Loisel (loisel.jerome at gmail.com)</li>
 <li>Liu XP (liu_xp2003 at sina.com)</li>
 <li>Qi Chen (qi.chensh at ele.me)</li>
 <li>(gavin at 16degrees.com.au)</li>
 <li>Thomas Schapitz (ts-nospam12 at online.de)</li>
 <li>Murdecai777 (https://github.com/Murdecai777)</li>
-<li>Logan Mauzaize (https://github.com/loganmzz)</li>
-<li>Maxime Chassagneux (https://github.com/max3163)</li>
+<li>Logan Mauzaize (logan.mauzaize at gmail.com)</li>
+<li>Maxime Chassagneux (maxime.chassagneux@gmail.com)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
 
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads,
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <source>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </source>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry.
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a>
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a>
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
 
 </section>
 </body>
 </document>
