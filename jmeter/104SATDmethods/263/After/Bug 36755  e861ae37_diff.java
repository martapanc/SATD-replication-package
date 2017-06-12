diff --git a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
index ee3a7ce19..1db559271 100644
--- a/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/StatGraphVisualizer.java
@@ -1,410 +1,412 @@
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
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Vector;
 
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTable;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
 
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.SaveGraphics;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.save.OldSaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.gui.NumberRenderer;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RateRenderer;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
+import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Aggregrate Table-Based Reporting Visualizer for JMeter. Props to the people
  * who've done the other visualizers ahead of me (Stefano Mazzocchi), who I
  * borrowed code from to start me off (and much code may still exist). Thank
  * you!
  * 
  * @version $Revision$ on $Date$
  */
 public class StatGraphVisualizer extends AbstractVisualizer implements Clearable,
 ActionListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
     
 	private final String[] COLUMNS = { JMeterUtils.getResString("URL"), //$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_count"),			//$NON-NLS-1$
 			JMeterUtils.getResString("average"),						//$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_median"),		//$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_90%_line"),		//$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_min"),			//$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_max"),			//$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_error%"),		//$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_rate"),			//$NON-NLS-1$
 			JMeterUtils.getResString("aggregate_report_bandwidth") };	//$NON-NLS-1$
     
     private final String[] GRAPH_COLUMNS = {JMeterUtils.getResString("average"),//$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_median"),		//$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_90%_line"),		//$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_min"),			//$NON-NLS-1$
             JMeterUtils.getResString("aggregate_report_max")};			//$NON-NLS-1$
 
 	private final String TOTAL_ROW_LABEL =
 		JMeterUtils.getResString("aggregate_report_total_label");		//$NON-NLS-1$
 
 	protected JTable myJTable;
 
 	protected JScrollPane myScrollPane;
 
 	transient private ObjectTableModel model;
 
 	Map tableRows = Collections.synchronizedMap(new HashMap());
     
     protected AxisGraph graphPanel = null;
     
     protected VerticalPanel graph = null;
     
     protected JScrollPane graphScroll = null;
     
     protected JSplitPane spane = null;
     
     protected JLabeledChoice columns = 
         new JLabeledChoice(JMeterUtils.getResString("aggregate_graph_column"),GRAPH_COLUMNS);//$NON-NLS-1$
     
     //NOT USED protected double[][] data = null;
     
     protected JButton displayButton = 
         new JButton(JMeterUtils.getResString("aggregate_graph_display"));				//$NON-NLS-1$
     
     protected JButton saveGraph = 
         new JButton(JMeterUtils.getResString("aggregate_graph_save"));					//$NON-NLS-1$
     
     protected JButton saveTable = 
         new JButton(JMeterUtils.getResString("aggregate_graph_save_table"));			//$NON-NLS-1$
     
     JLabeledTextField graphTitle = 
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_user_title"));	//$NON-NLS-1$
     
     JLabeledTextField maxLengthXAxisLabel = 
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_max_length_xaxis_label"));//$NON-NLS-1$
     
     JLabeledTextField graphWidth = 
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_width"));		//$NON-NLS-1$
     JLabeledTextField graphHeight = 
         new JLabeledTextField(JMeterUtils.getResString("aggregate_graph_height"));		//$NON-NLS-1$
     
     protected String yAxisLabel = JMeterUtils.getResString("aggregate_graph_response_time");//$NON-NLS-1$
     
     protected String yAxisTitle = JMeterUtils.getResString("aggregate_graph_ms");		//$NON-NLS-1$
     
     protected boolean saveGraphToFile = false;
     
     protected int defaultWidth = 400;
     
     protected int defaultHeight = 300;
 
 	public StatGraphVisualizer() {
 		super();
 		model = new ObjectTableModel(COLUMNS,
 				SamplingStatCalculator.class,
 				new Functor[] {
 				new Functor("getLabel"),					//$NON-NLS-1$
 				new Functor("getCount"),					//$NON-NLS-1$
 				new Functor("getMeanAsNumber"),				//$NON-NLS-1$
 				new Functor("getMedian"),					//$NON-NLS-1$
 				new Functor("getPercentPoint",				//$NON-NLS-1$
 				new Object[] { new Float(.900) }),
 				new Functor("getMin"),						//$NON-NLS-1$
 				new Functor("getMax"), 						//$NON-NLS-1$
 				new Functor("getErrorPercentage"),	//$NON-NLS-1$
 				new Functor("getRate"),				//$NON-NLS-1$
 				new Functor("getPageSize") },			//$NON-NLS-1$
 				new Functor[] { null, null, null, null, null, null, null, null,	null, null }, 
 				new Class[] { String.class, Long.class, Long.class, Long.class, Long.class, Long.class,
 				Long.class, String.class, String.class, String.class });
 		clear();
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
 		return "aggregate_graph_title";						//$NON-NLS-1$
 	}
 
 	public void add(SampleResult res) {
 		SamplingStatCalculator row = null;
 		synchronized (tableRows) {
 			row = (SamplingStatCalculator) tableRows.get(res.getSampleLabel());
 			if (row == null) {
 				row = new SamplingStatCalculator(res.getSampleLabel());
 				tableRows.put(row.getLabel(), row);
 				model.insertRow(row, model.getRowCount() - 1);
 			}
 		}
 		row.addSample(res);
 		((SamplingStatCalculator) tableRows.get(TOTAL_ROW_LABEL)).addSample(res);
 		model.fireTableDataChanged();
 	}
 
 	/**
 	 * Clears this visualizer and its model, and forces a repaint of the table.
 	 */
 	public void clear() {
 		model.clearData();
 		tableRows.clear();
 		tableRows.put(TOTAL_ROW_LABEL, new SamplingStatCalculator(TOTAL_ROW_LABEL));
 		model.addRow(tableRows.get(TOTAL_ROW_LABEL));
 	}
 
 	// overrides AbstractVisualizer
 	// forces GUI update after sample file has been read
 	public TestElement createTestElement() {
 		TestElement t = super.createTestElement();
 
 		// sleepTill = 0;
 		return t;
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
         
         graph = new VerticalPanel();
         graph.setBorder(margin2);
 
 
         JLabel graphLabel = new JLabel(JMeterUtils.getResString("aggregate_graph")); //$NON-NLS-1$
         graphPanel = new AxisGraph();
         graphPanel.setPreferredSize(new Dimension(defaultWidth,defaultHeight));
 
         // horizontal panel for the buttons
         HorizontalPanel buttonpanel = new HorizontalPanel();
         buttonpanel.add(columns);
         buttonpanel.add(displayButton);
         buttonpanel.add(saveGraph);
         buttonpanel.add(saveTable);
         
         graph.add(graphLabel);
         graph.add(graphTitle);
         graph.add(maxLengthXAxisLabel);
         graph.add(graphWidth);
         graph.add(graphHeight);
         graph.add(buttonpanel);
         graph.add(graphPanel);
 
         displayButton.addActionListener(this);
         saveGraph.addActionListener(this);
         saveTable.addActionListener(this);
         graphScroll = new JScrollPane(graph);
         graphScroll.setAutoscrolls(true);
 
         spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         spane.setLeftComponent(myScrollPane);
         spane.setRightComponent(graphScroll);
         spane.setResizeWeight(.2);
         spane.setContinuousLayout(true);
 
         this.add(mainPanel, BorderLayout.NORTH);
         this.add(spane,BorderLayout.CENTER);
 	}
     
     public void makeGraph() {
         String wstr = graphWidth.getText();
         String hstr = graphHeight.getText();
         String lstr = maxLengthXAxisLabel.getText();
         if (wstr.length() == 0) {
             wstr = "450";//$NON-NLS-1$
         }
         if (hstr.length() == 0) {
             hstr = "250";//$NON-NLS-1$
         }
         if (lstr.length() == 0) {
             lstr = "20";//$NON-NLS-1$
         }
         int width = Integer.parseInt(wstr);
         int height = Integer.parseInt(hstr);
         int maxLength = Integer.parseInt(lstr);
 
         graphPanel.setData(this.getData());
         graphPanel.setHeight(height);
         graphPanel.setWidth(width);
         graphPanel.setTitle(graphTitle.getText());
         graphPanel.setMaxLength(maxLength);
         graphPanel.setXAxisLabels(getAxisLabels());
         graphPanel.setXAxisTitle(columns.getText());
         graphPanel.setYAxisLabels(this.yAxisLabel);
         graphPanel.setYAxisTitle(this.yAxisTitle);
 
         graphPanel.setPreferredSize(new Dimension(width,height));
         graph.setSize(new Dimension(graph.getWidth(), height + 120));
         spane.repaint();
     }
     
     public double[][] getData() {
         if (model.getRowCount() > 1) {
             int count = model.getRowCount() -1;
             int col = model.findColumn(columns.getText());
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
      * @return
      */
     public Vector getAllTableData() {
         Vector data = new Vector();
         if (model.getRowCount() > 0) {
             for (int rw=0; rw < model.getRowCount(); rw++) {
                 int cols = model.getColumnCount();
                 Vector column = new Vector();
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
         if (event.getSource() == displayButton) {
             makeGraph();
         } else if (event.getSource() == saveGraph) {
             saveGraphToFile = true;
             try {
                 ActionRouter.getInstance().getAction(
                         ActionNames.SAVE_GRAPHICS,SaveGraphics.class.getName()).doAction(
                                 new ActionEvent(this,1,ActionNames.SAVE_GRAPHICS));
             } catch (Exception e) {
                 e.printStackTrace();
             }
         } else if (event.getSource() == saveTable) {
             JFileChooser chooser = FileDialoger.promptToSaveFile(
                     "statistics.csv");		//$NON-NLS-1$
             File output = chooser.getSelectedFile();
             FileWriter writer = null;
             try {
                 writer = new FileWriter(output);
                 Vector data = this.getAllTableData();
                 OldSaveService.saveCSVStats(data,writer);
-                writer.close();
             } catch (FileNotFoundException e) {
                 log.warn(e.getMessage());
             } catch (IOException e) {
                 log.warn(e.getMessage());
+            } finally {
+                JOrphanUtils.closeQuietly(writer);
             }
         }
     }
     
     public JComponent getPrintableComponent() {
         if (saveGraphToFile == true) {
             saveGraphToFile = false;
             graphPanel.setBounds(graphPanel.getLocation().x,graphPanel.getLocation().y,
                     graphPanel.width,graphPanel.height);
             return graphPanel;
         }
 		return this;
     }
 }
diff --git a/src/core/org/apache/jmeter/JMeter.java b/src/core/org/apache/jmeter/JMeter.java
index 8d5d931f0..d9e7ff4c3 100644
--- a/src/core/org/apache/jmeter/JMeter.java
+++ b/src/core/org/apache/jmeter/JMeter.java
@@ -1,861 +1,863 @@
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
 
 package org.apache.jmeter;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.Authenticator;
 import java.net.MalformedURLException;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import java.util.Properties;
 import java.util.StringTokenizer;
 
 import org.apache.commons.cli.avalon.CLArgsParser;
 import org.apache.commons.cli.avalon.CLOption;
 import org.apache.commons.cli.avalon.CLOptionDescriptor;
 import org.apache.commons.cli.avalon.CLUtil;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.ReplaceableController;
 import org.apache.jmeter.control.gui.AbstractControllerGui;
 import org.apache.jmeter.control.gui.TestPlanGui;
 import org.apache.jmeter.control.gui.WorkBenchGui;
 import org.apache.jmeter.engine.ClientJMeterEngine;
 import org.apache.jmeter.engine.JMeterEngine;
 import org.apache.jmeter.engine.RemoteJMeterEngineImpl;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.action.ActionNames;
 import org.apache.jmeter.gui.action.ActionRouter;
 import org.apache.jmeter.gui.action.Load;
 import org.apache.jmeter.gui.tree.JMeterTreeListener;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.plugin.JMeterPlugin;
 import org.apache.jmeter.plugin.PluginManager;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.reporters.Summariser;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestListener;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.threads.gui.ThreadGroupGui;
 import org.apache.jmeter.timers.gui.AbstractTimerGui;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.BeanShellServer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassTools;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * @author mstover
  */
 public class JMeter implements JMeterPlugin {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String HTTP_PROXY_PASS = "http.proxyPass"; // $NON-NLS-1$
 
     public static final String HTTP_PROXY_USER = "http.proxyUser"; // $NON-NLS-1$
 
 
     private static final int PROXY_PASSWORD     = 'a';// $NON-NLS-1$
     private static final int JMETER_HOME_OPT    = 'd';// $NON-NLS-1$
     private static final int HELP_OPT           = 'h';// $NON-NLS-1$
     // jmeter.log
     private static final int JMLOGFILE_OPT      = 'j';// $NON-NLS-1$
     // sample result log file
     private static final int LOGFILE_OPT        = 'l';// $NON-NLS-1$
     private static final int NONGUI_OPT         = 'n';// $NON-NLS-1$
     private static final int PROPFILE_OPT       = 'p';// $NON-NLS-1$
 	private static final int PROPFILE2_OPT      = 'q';// $NON-NLS-1$
     private static final int REMOTE_OPT         = 'r';// $NON-NLS-1$
     private static final int SERVER_OPT         = 's';// $NON-NLS-1$
 	private static final int TESTFILE_OPT       = 't';// $NON-NLS-1$
     private static final int PROXY_USERNAME     = 'u';// $NON-NLS-1$
     private static final int VERSION_OPT        = 'v';// $NON-NLS-1$
 
     private static final int SYSTEM_PROPERTY    = 'D';// $NON-NLS-1$
 	private static final int PROXY_HOST         = 'H';// $NON-NLS-1$
     private static final int JMETER_PROPERTY    = 'J';// $NON-NLS-1$
     private static final int LOGLEVEL           = 'L';// $NON-NLS-1$
     private static final int NONPROXY_HOSTS     = 'N';// $NON-NLS-1$
 	private static final int PROXY_PORT         = 'P';// $NON-NLS-1$
     private static final int SYSTEM_PROPFILE    = 'S';// $NON-NLS-1$
 
 
 
 
 
 
 
 	/**
 	 * Define the understood options. Each CLOptionDescriptor contains:
 	 * <ul>
 	 * <li>The "long" version of the option. Eg, "help" means that "--help"
 	 * will be recognised.</li>
 	 * <li>The option flags, governing the option's argument(s).</li>
 	 * <li>The "short" version of the option. Eg, 'h' means that "-h" will be
 	 * recognised.</li>
 	 * <li>A description of the option.</li>
 	 * </ul>
 	 */
 	private static final CLOptionDescriptor[] options = new CLOptionDescriptor[] {
 			new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED, HELP_OPT,
 					"print usage information and exit"),
 			new CLOptionDescriptor("version", CLOptionDescriptor.ARGUMENT_DISALLOWED, VERSION_OPT,
 					"print the version information and exit"),
 			new CLOptionDescriptor("propfile", CLOptionDescriptor.ARGUMENT_REQUIRED, PROPFILE_OPT,
 					"the jmeter property file to use"),
 			new CLOptionDescriptor("addprop", CLOptionDescriptor.ARGUMENT_REQUIRED
 					| CLOptionDescriptor.DUPLICATES_ALLOWED, PROPFILE2_OPT,
 					"additional JMeter property file(s)"),
 			new CLOptionDescriptor("testfile", CLOptionDescriptor.ARGUMENT_REQUIRED, TESTFILE_OPT,
 					"the jmeter test(.jmx) file to run"),
 			new CLOptionDescriptor("logfile", CLOptionDescriptor.ARGUMENT_REQUIRED, LOGFILE_OPT,
 					"the file to log samples to"),
 			new CLOptionDescriptor("jmeterlogfile", CLOptionDescriptor.ARGUMENT_REQUIRED, JMLOGFILE_OPT,
 					"jmeter run log file (jmeter.log)"),
 			new CLOptionDescriptor("nongui", CLOptionDescriptor.ARGUMENT_DISALLOWED, NONGUI_OPT,
 					"run JMeter in nongui mode"),
 			new CLOptionDescriptor("server", CLOptionDescriptor.ARGUMENT_DISALLOWED, SERVER_OPT,
 					"run the JMeter server"),
 			new CLOptionDescriptor("proxyHost", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_HOST,
 					"Set a proxy server for JMeter to use"),
 			new CLOptionDescriptor("proxyPort", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_PORT,
 					"Set proxy server port for JMeter to use"),
             new CLOptionDescriptor("nonProxyHosts", CLOptionDescriptor.ARGUMENT_REQUIRED, NONPROXY_HOSTS,
                     "Set nonproxy host list (e.g. *.apache.org|localhost)"),
 			new CLOptionDescriptor("username", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_USERNAME,
 					"Set username for proxy server that JMeter is to use"),
 			new CLOptionDescriptor("password", CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_PASSWORD,
 					"Set password for proxy server that JMeter is to use"),
 			new CLOptionDescriptor("jmeterproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
 					| CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_PROPERTY, 
                     "Define additional JMeter properties"),
 			new CLOptionDescriptor("systemproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
 					| CLOptionDescriptor.ARGUMENTS_REQUIRED_2, SYSTEM_PROPERTY, 
                     "Define additional system properties"),
             new CLOptionDescriptor("systemPropertyFile", CLOptionDescriptor.DUPLICATES_ALLOWED
             		| CLOptionDescriptor.ARGUMENT_REQUIRED, SYSTEM_PROPFILE,
                     "additional system property file(s)"),
 			new CLOptionDescriptor("loglevel", CLOptionDescriptor.DUPLICATES_ALLOWED
 					| CLOptionDescriptor.ARGUMENTS_REQUIRED_2, LOGLEVEL,
 					"[category=]level e.g. jorphan=INFO or jmeter.util=DEBUG"),
 			new CLOptionDescriptor("runremote", CLOptionDescriptor.ARGUMENT_OPTIONAL, REMOTE_OPT,
 					"Start remote servers from non-gui mode"),
 			new CLOptionDescriptor("homedir", CLOptionDescriptor.ARGUMENT_REQUIRED, JMETER_HOME_OPT,
 					"the jmeter home directory to use"), };
 
 	public JMeter() {
 	}
 
 	// Hack to allow automated tests to find when test has ended
 	//transient boolean testEnded = false;
 
 	private JMeter parent;
 
 	/**
 	 * Starts up JMeter in GUI mode
 	 */
 	public void startGui(CLOption testFile) {
 
 		PluginManager.install(this, true);
 		JMeterTreeModel treeModel = new JMeterTreeModel();
 		JMeterTreeListener treeLis = new JMeterTreeListener(treeModel);
 		treeLis.setActionHandler(ActionRouter.getInstance());
 		// NOTUSED: GuiPackage guiPack =
 		GuiPackage.getInstance(treeLis, treeModel);
 		org.apache.jmeter.gui.MainFrame main = new org.apache.jmeter.gui.MainFrame(ActionRouter.getInstance(),
 				treeModel, treeLis);
 		main.setTitle("Apache JMeter");// $NON-NLS-1$
 		main.setIconImage(JMeterUtils.getImage("jmeter.jpg").getImage());// $NON-NLS-1$
 		ComponentUtil.centerComponentInWindow(main, 80);
 		main.show();
 		ActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, ActionNames.ADD_ALL));
         String arg; 
 		if (testFile != null && (arg = testFile.getArgument()) != null) {
             FileInputStream reader = null;
 			try {
                 File f = new File(arg);
 				log.info("Loading file: " + f);
 				reader = new FileInputStream(f);
 				HashTree tree = SaveService.loadTree(reader);
 
 				GuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());
 
 				new Load().insertLoadedTree(1, tree);
             } catch (ConversionException e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(SaveService.CEtoString(e));
 			} catch (Exception e) {
 				log.error("Failure loading test file", e);
 				JMeterUtils.reportErrorToUser(e.toString());
 			} finally {
                 JOrphanUtils.closeQuietly(reader);
             }
 		}
 	}
 
 	/**
 	 * Takes the command line arguments and uses them to determine how to
 	 * startup JMeter.
 	 */
 	public void start(String[] args) {
 
 		CLArgsParser parser = new CLArgsParser(args, options);
 		if (null != parser.getErrorString()) {
 			System.err.println("Error: " + parser.getErrorString());
 			System.out.println("Usage");
 			System.out.println(CLUtil.describeOptions(options).toString());
 			return;
 		}
 		try {
 			initializeProperties(parser); // Also initialises JMeter logging
 
             /* 
              * The following is needed for HTTPClient.
              * (originally tried doing this in HTTPSampler2, 
              * but it appears that it was done too late when running in GUI mode)
              * Set the commons logging default to Avalon Logkit, if not already defined
              */
             if (System.getProperty("org.apache.commons.logging.Log") == null) { // $NON-NLS-1$
                 System.setProperty("org.apache.commons.logging.Log" // $NON-NLS-1$
                         , "org.apache.commons.logging.impl.LogKitLogger"); // $NON-NLS-1$
             }
 
             log.info(JMeterUtils.getJMeterCopyright());
             log.info("Version " + JMeterUtils.getJMeterVersion());
 			logProperty("java.version"); //$NON-NLS-1$
 			logProperty("os.name"); //$NON-NLS-1$
 			logProperty("os.arch"); //$NON-NLS-1$
 			logProperty("os.version"); //$NON-NLS-1$
 			log.info("Default Locale=" + Locale.getDefault().getDisplayName());// $NON-NLS-1$
             log.info("JMeter  Locale=" + JMeterUtils.getLocale().getDisplayName());// $NON-NLS-1$
 			log.info("JMeterHome="     + JMeterUtils.getJMeterHome());// $NON-NLS-1$
 			logProperty("user.dir","  ="); //$NON-NLS-1$
 			log.info("PWD       ="+new File(".").getCanonicalPath());//$NON-NLS-1$
             setProxy(parser);
             
             updateClassLoader();
             if (log.isDebugEnabled())
             {
                 String jcp=System.getProperty("java.class.path");// $NON-NLS-1$
                 log.debug(jcp);
             }
 
             // Set some (hopefully!) useful properties
             long now=System.currentTimeMillis();
             JMeterUtils.setProperty("START.MS",Long.toString(now));
             Date today=new Date(now); // so it agrees with above
             // TODO perhaps should share code with __time() function for this...
             JMeterUtils.setProperty("START.YMD",new SimpleDateFormat("yyyyMMdd").format(today));
             JMeterUtils.setProperty("START.HMS",new SimpleDateFormat("HHmmss").format(today));
             
 			if (parser.getArgumentById(VERSION_OPT) != null) {
 				System.out.println(JMeterUtils.getJMeterCopyright());
 				System.out.println("Version " + JMeterUtils.getJMeterVersion());
 			} else if (parser.getArgumentById(HELP_OPT) != null) {
 				System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));// $NON-NLS-1$
 			} else if (parser.getArgumentById(SERVER_OPT) != null) {
 				startServer(JMeterUtils.getPropDefault("server_port", 0));// $NON-NLS-1$
 				startOptionalServers();
 			} else if (parser.getArgumentById(NONGUI_OPT) == null) {
 				startGui(parser.getArgumentById(TESTFILE_OPT));
 				startOptionalServers();
 			} else {
 				startNonGui(parser.getArgumentById(TESTFILE_OPT), parser.getArgumentById(LOGFILE_OPT), parser
 						.getArgumentById(REMOTE_OPT));
 				startOptionalServers();
 			}
 		} catch (IllegalUserActionException e) {
 			System.out.println(e.getMessage());
 			System.out.println("Incorrect Usage");
 			System.out.println(CLUtil.describeOptions(options).toString());
 		} catch (Exception e) {
             if (log != null){
                 log.fatalError("An error occurred: ",e);
             }
 			e.printStackTrace();
 			System.out.println("An error occurred: " + e.getMessage());
 			System.exit(-1);
 		}
 	}
 
     // Update classloader if necessary
 	private void updateClassLoader() {
             updatePath("search_paths",";"); //$NON-NLS-1$//$NON-NLS-2$
             updatePath("user.classpath",File.pathSeparator);//$NON-NLS-1$
     }
 
 	private void updatePath(String property, String sep) {
         String userpath= JMeterUtils.getPropDefault(property,"");// $NON-NLS-1$
         if (userpath.length() <= 0) return;
         log.info(property+"="+userpath); //$NON-NLS-1$
 		StringTokenizer tok = new StringTokenizer(userpath, sep);
 		while(tok.hasMoreTokens()) {
 		    String path=tok.nextToken();
 		    File f=new File(path);
 		    if (!f.canRead() && !f.isDirectory()) {
 		        log.warn("Can't read "+path);   
 		    } else {
 	            log.info("Adding to classpath: "+path);
 	            try {
 					NewDriver.addPath(path);
 				} catch (MalformedURLException e) {
 					log.warn("Error adding: "+path+" "+e.getLocalizedMessage());
 				}
 		    }
 		}
 	}
 
     /**
 	 * 
 	 */
 	private void startOptionalServers() {
 		int bshport = JMeterUtils.getPropDefault("beanshell.server.port", 0);// $NON-NLS-1$
 		String bshfile = JMeterUtils.getPropDefault("beanshell.server.file", "");// $NON-NLS-1$ $NON-NLS-2$
 		if (bshport > 0) {
 			log.info("Starting Beanshell server (" + bshport + "," + bshfile + ")");
 			Runnable t = new BeanShellServer(bshport, bshfile);
 			t.run();
 		}
         
         // Should we run a beanshell script on startup?
         String bshinit = JMeterUtils.getProperty("beanshell.init.file");// $NON-NLS-1$
         if (bshinit != null){
             log.info("Run Beanshell on file: "+bshinit);
             try {
                 BeanShellInterpreter bsi = new BeanShellInterpreter();//bshinit,log);
                 bsi.source(bshinit);
             } catch (ClassNotFoundException e) {
                 log.warn("Could not start Beanshell: "+e.getLocalizedMessage());
             } catch (JMeterException e) {
                 log.warn("Could not process Beanshell file: "+e.getLocalizedMessage());
             }
         }
         
         int mirrorPort=JMeterUtils.getPropDefault("mirror.server.port", 0);// $NON-NLS-1$
         if (mirrorPort > 0){
 			log.info("Starting Mirror server (" + mirrorPort + ")");
 			try {
 				Object instance = ClassTools.construct(
 						"org.apache.jmeter.protocol.http.control.HttpMirrorControl",// $NON-NLS-1$
 						mirrorPort);
 	            ClassTools.invoke(instance,"startHttpMirror");
 			} catch (JMeterException e) {
 				log.warn("Could not start Mirror server",e);
 			}
         }
 	}
 
 	/**
 	 * Sets a proxy server for the JVM if the command line arguments are
 	 * specified.
 	 */
 	private void setProxy(CLArgsParser parser) throws IllegalUserActionException {
 		if (parser.getArgumentById(PROXY_USERNAME) != null) {
             Properties jmeterProps = JMeterUtils.getJMeterProperties();
 			if (parser.getArgumentById(PROXY_PASSWORD) != null) {
 				String u, p;
 				Authenticator.setDefault(new ProxyAuthenticator(u = parser.getArgumentById(PROXY_USERNAME)
 						.getArgument(), p = parser.getArgumentById(PROXY_PASSWORD).getArgument()));
 				log.info("Set Proxy login: " + u + "/" + p);
                 jmeterProps.setProperty(HTTP_PROXY_USER, u);//for Httpclient
                 jmeterProps.setProperty(HTTP_PROXY_PASS, p);//for Httpclient
 			} else {
 				String u;
 				Authenticator.setDefault(new ProxyAuthenticator(u = parser.getArgumentById(PROXY_USERNAME)
 						.getArgument(), ""));
 				log.info("Set Proxy login: " + u);
                 jmeterProps.setProperty(HTTP_PROXY_USER, u);
 			}
 		}
 		if (parser.getArgumentById(PROXY_HOST) != null && parser.getArgumentById(PROXY_PORT) != null) {
 			String h = parser.getArgumentById(PROXY_HOST).getArgument();
             String p = parser.getArgumentById(PROXY_PORT).getArgument();
 			System.setProperty("http.proxyHost",  h );// $NON-NLS-1$
 			System.setProperty("https.proxyHost", h);// $NON-NLS-1$
 			System.setProperty("http.proxyPort",  p);// $NON-NLS-1$
 			System.setProperty("https.proxyPort", p);// $NON-NLS-1$
 			log.info("Set http[s].proxyHost: " + h + " Port: " + p);
 		} else if (parser.getArgumentById(PROXY_HOST) != null || parser.getArgumentById(PROXY_PORT) != null) {
 			throw new IllegalUserActionException(JMeterUtils.getResString("proxy_cl_error"));// $NON-NLS-1$
 		}
         
         if (parser.getArgumentById(NONPROXY_HOSTS) != null) {
             String n = parser.getArgumentById(NONPROXY_HOSTS).getArgument();
             System.setProperty("http.nonProxyHosts",  n );// $NON-NLS-1$
             System.setProperty("https.nonProxyHosts", n );// $NON-NLS-1$
             log.info("Set http[s].nonProxyHosts: "+n);
         }
 	}
 
 	private void initializeProperties(CLArgsParser parser) {
 		if (parser.getArgumentById(PROPFILE_OPT) != null) {
 			JMeterUtils.loadJMeterProperties(parser.getArgumentById(PROPFILE_OPT).getArgument());
 		} else {
 			JMeterUtils.loadJMeterProperties(NewDriver.getJMeterDir() + File.separator
                     + "bin" + File.separator // $NON-NLS-1$
 					+ "jmeter.properties");// $NON-NLS-1$
 		}
 
 		if (parser.getArgumentById(JMLOGFILE_OPT) != null){
 			String jmlogfile=parser.getArgumentById(JMLOGFILE_OPT).getArgument();
 			JMeterUtils.setProperty(LoggingManager.LOG_FILE,jmlogfile);
 		}
 		
 		JMeterUtils.initLogging();
 		JMeterUtils.initLocale();
 		// Bug 33845 - allow direct override of Home dir
 		if (parser.getArgumentById(JMETER_HOME_OPT) == null) {
 			JMeterUtils.setJMeterHome(NewDriver.getJMeterDir());
 		} else {
 			JMeterUtils.setJMeterHome(parser.getArgumentById(JMETER_HOME_OPT).getArgument());
 		}
 
 		Properties jmeterProps = JMeterUtils.getJMeterProperties();
 
 		// Add local JMeter properties, if the file is found
 		String userProp = JMeterUtils.getPropDefault("user.properties",""); //$NON-NLS-1$
 		if (userProp.length() > 0){ //$NON-NLS-1$
 			FileInputStream fis=null;
 			try {
                 File file = new File(userProp);
                 if (file.canRead()){
                 	log.info("Loading user properties from: "+userProp);
 					fis = new FileInputStream(file);
 					Properties tmp = new Properties();
 					tmp.load(fis);
 					jmeterProps.putAll(tmp);
 					LoggingManager.setLoggingLevels(tmp);//Do what would be done earlier
                 }
 			} catch (IOException e) {
 				log.warn("Error loading user property file: " + userProp, e);
             } finally {
             	JOrphanUtils.closeQuietly(fis);
 			}			
 		}
 
 		// Add local system properties, if the file is found
 		String sysProp = JMeterUtils.getPropDefault("system.properties",""); //$NON-NLS-1$
 		if (sysProp.length() > 0){
 			FileInputStream fis=null;
 			try {
                 File file = new File(sysProp);
                 if (file.canRead()){
                 	log.info("Loading system properties from: "+sysProp);
 					fis = new FileInputStream(file);
 					System.getProperties().load(fis);
                 }
 			} catch (IOException e) {
 				log.warn("Error loading system property file: " + sysProp, e);
             } finally {
             	JOrphanUtils.closeQuietly(fis);
 			}			
 		}
 
 		// Process command line property definitions
 		// These can potentially occur multiple times
 		
 		List clOptions = parser.getArguments();
 		int size = clOptions.size();
 
 		for (int i = 0; i < size; i++) {
 			CLOption option = (CLOption) clOptions.get(i);
 			String name = option.getArgument(0);
 			String value = option.getArgument(1);
             FileInputStream fis = null;            
 
 			switch (option.getDescriptor().getId()) {
 			
 			// Should not have any text arguments
             case CLOption.TEXT_ARGUMENT:
                 throw new IllegalArgumentException("Unknown arg: "+option.getArgument());
 			
             case PROPFILE2_OPT: // Bug 33920 - allow multiple props
 				try {
                     fis = new FileInputStream(new File(name));
 					Properties tmp = new Properties();
 					tmp.load(fis);
 					jmeterProps.putAll(tmp);
 					LoggingManager.setLoggingLevels(tmp);//Do what would be done earlier
 				} catch (FileNotFoundException e) {
 					log.warn("Can't find additional property file: " + name, e);
 				} catch (IOException e) {
 					log.warn("Error loading additional property file: " + name, e);
                 } finally {
                 	JOrphanUtils.closeQuietly(fis);
 				}
 				break;
             case SYSTEM_PROPFILE:
                 log.info("Setting System properties from file: " + name);
                 try {
                     fis = new FileInputStream(new File(name));
                     System.getProperties().load(fis);
                 } catch (IOException e) {
                     log.warn("Cannot find system property file "+e.getLocalizedMessage());
                 } finally {
                 	JOrphanUtils.closeQuietly(fis);
                 }
                 break;
 			case SYSTEM_PROPERTY:
 				if (value.length() > 0) { // Set it
 					log.info("Setting System property: " + name + "=" + value);
 					System.getProperties().setProperty(name, value);
 				} else { // Reset it
 					log.warn("Removing System property: " + name);
 					System.getProperties().remove(name);
 				}
 				break;
 			case JMETER_PROPERTY:
 				if (value.length() > 0) { // Set it
 					log.info("Setting JMeter property: " + name + "=" + value);
 					jmeterProps.setProperty(name, value);
 				} else { // Reset it
 					log.warn("Removing JMeter property: " + name);
 					jmeterProps.remove(name);
 				}
 				break;
 			case LOGLEVEL:
 				if (value.length() > 0) { // Set category
 					log.info("LogLevel: " + name + "=" + value);
 					LoggingManager.setPriority(value, name);
 				} else { // Set root level
 					log.warn("LogLevel: " + name);
 					LoggingManager.setPriority(name);
 				}
 				break;
 			}
 		}
 	
 	}
 
 	public void startServer() {
 		startServer(0);
 	}
 
 	public void startServer(int port) {
 		try {
 			new RemoteJMeterEngineImpl(port);
 			while (true) {
 				Thread.sleep(Long.MAX_VALUE);
 			}
 		} catch (Exception ex) {
 			log.error("Giving up, as server failed with:", ex);
 			System.exit(0);// Give up
 		}
 	}
 
 	public void startNonGui(CLOption testFile, CLOption logFile, CLOption remoteStart)
 			throws IllegalUserActionException {
 		// add a system property so samplers can check to see if JMeter
 		// is running in NonGui mode
 		System.setProperty("JMeter.NonGui", "true");// $NON-NLS-1$
 		JMeter driver = new JMeter();
 		driver.parent = this;
 		PluginManager.install(this, false);
 
 		String remote_hosts_string = null;
 		if (remoteStart != null) {
 			remote_hosts_string = remoteStart.getArgument();
 			if (remote_hosts_string == null) {
 				remote_hosts_string = JMeterUtils.getPropDefault(
 	                    "remote_hosts", //$NON-NLS-1$ 
 	                    "127.0.0.1");//$NON-NLS-1$				
 			}
 		}
 		if (testFile == null) {
 			throw new IllegalUserActionException();
 		}
 		String argument = testFile.getArgument();
         if (argument == null) {
             throw new IllegalUserActionException();
         }
         if (logFile == null) {
 			driver.run(argument, null, remoteStart != null,remote_hosts_string);
 		} else {
 			driver.run(argument, logFile.getArgument(), remoteStart != null,remote_hosts_string);
 		}
 	}
 
     // run test in batch mode
 	private void run(String testFile, String logFile, boolean remoteStart, String remote_hosts_string) {
 		FileInputStream reader = null;
 		try {
 			File f = new File(testFile);
 			if (!f.exists() || !f.isFile()) {
 				println("Could not open " + testFile);
 				return;
 			}
 			FileServer.getFileServer().setBasedir(f.getAbsolutePath());
 
 			reader = new FileInputStream(f);
 			log.info("Loading file: " + f);
 
 			HashTree tree = SaveService.loadTree(reader);
 
 			// Remove the disabled items
 			// For GUI runs this is done in Start.java
 			convertSubTree(tree);
 
 			if (logFile != null) {
 				ResultCollector logger = new ResultCollector();
 				logger.setFilename(logFile);
 				tree.add(tree.getArray()[0], logger);
 			}
 			String summariserName = JMeterUtils.getPropDefault("summariser.name", "");//$NON-NLS-1$
 			if (summariserName.length() > 0) {
 				log.info("Creating summariser <" + summariserName + ">");
 				println("Creating summariser <" + summariserName + ">");
 				Summariser summer = new Summariser(summariserName);
 				tree.add(tree.getArray()[0], summer);
 			}
 			tree.add(tree.getArray()[0], new ListenToTest(parent));
 			println("Created the tree successfully");
 			JMeterEngine engine = null;
 			if (!remoteStart) {
 				engine = new StandardJMeterEngine();
 				engine.configure(tree);
 				long now=System.currentTimeMillis();
 				println("Starting the test @ "+new Date(now)+" ("+now+")");
 				engine.runTest();
 			} else {
 				java.util.StringTokenizer st = new java.util.StringTokenizer(remote_hosts_string, ",");//$NON-NLS-1$
 				List engines = new LinkedList();
 				while (st.hasMoreElements()) {
 					String el = (String) st.nextElement();
 					println("Configuring remote engine for " + el);
 					engines.add(doRemoteInit(el.trim(), tree));
 				}
 				println("Starting remote engines");
 				Iterator iter = engines.iterator();
 				while (iter.hasNext()) {
 					engine = (JMeterEngine) iter.next();
 					engine.runTest();
 				}
 				println("Remote engines have been started");
 			}
 		} catch (Exception e) {
 			System.out.println("Error in NonGUIDriver " + e.toString());
 			log.error("", e);
-		}
+        } finally {
+            JOrphanUtils.closeQuietly(reader);
+        }
 	}
 
 	/**
 	 * Code copied from AbstractAction.java and modified to suit TestElements
 	 * 
 	 * @param tree
 	 */
 	private void convertSubTree(HashTree tree) {
 		Iterator iter = new LinkedList(tree.list()).iterator();
 		while (iter.hasNext()) {
 			TestElement item = (TestElement) iter.next();
 			if (item.isEnabled()) {
 				// This is done for GUI runs in JMeterTreeModel.addSubTree()
 				if (item instanceof TestPlan) {
 					TestPlan tp = (TestPlan) item;
 					tp.setFunctionalMode(tp.isFunctionalMode());
 					tp.setSerialized(tp.isSerialized());
 				}
                 // TODO: this is a bit of a hack, but seems to work for the Include Controller
 				if (item instanceof ReplaceableController) {
                     // HACK: force the controller to load its tree
                      ReplaceableController rc = (ReplaceableController) item.clone();
                      HashTree subTree = tree.getTree(item);
     				 if (subTree != null) {
                          HashTree replacementTree = rc.getReplacementSubTree();
                          if (replacementTree != null) {
                              convertSubTree(replacementTree);
                              tree.replace(item,rc);
                              tree.set(rc,replacementTree);
                          }
                      } else {
     					convertSubTree(tree.getTree(item));
     				 }
                 } else {
                     convertSubTree(tree.getTree(item));                    
                 } // ReplaceableController
 			} else {// disabled
 				tree.remove(item);
 			}
 		}
 	}
 
 	private JMeterEngine doRemoteInit(String hostName, HashTree testTree) {
 		JMeterEngine engine = null;
 		try {
 			engine = new ClientJMeterEngine(hostName);
 		} catch (Exception e) {
 			log.fatalError("Failure connecting to remote host", e);
 			System.exit(0);
 		}
 		engine.configure(testTree);
 		return engine;
 	}
 
 	/**
 	 * Listen to test and exit program after test completes, after a 5 second
 	 * delay to give listeners a chance to close out their files.
 	 */
 	private static class ListenToTest implements TestListener, Runnable, Remoteable {
 		int started = 0;
 
 		//NOT YET USED private JMeter _parent;
 
 		private ListenToTest(JMeter parent) {
 			//_parent = parent;
 		}
 
 		public synchronized void testEnded(String host) {
 			started--;
 			log.info("Remote host " + host + " finished");
 			if (started == 0) {
 				testEnded();
 			}
 		}
 
 		public void testEnded() {
 			Thread stopSoon = new Thread(this);
 			stopSoon.start();
 		}
 
 		public synchronized void testStarted(String host) {
 			started++;
 			log.info("Started remote host: " + host);
 		}
 
 		public void testStarted() {
 			long now=System.currentTimeMillis();
 			log.info(JMeterUtils.getResString("running_test")+" ("+now+")");//$NON-NLS-1$
 		}
 
 		/**
 		 * This is a hack to allow listeners a chance to close their files. Must
 		 * implement a queue for sample responses tied to the engine, and the
 		 * engine won't deliver testEnded signal till all sample responses have
 		 * been delivered. Should also improve performance of remote JMeter
 		 * testing.
 		 */
 		public void run() {
 			long now = System.currentTimeMillis();
 			println("Tidying up ...    @ "+new Date(now)+" ("+now+")");
 			try {
 				Thread.sleep(5000);
 			} catch (InterruptedException e) {
 				// ignored
 			}
 			println("... end of run");
 			//_parent.testEnded = true;
             System.exit(0); //TODO - make this conditional, so can run automated tests
             /*
              * Note: although it should not be necessary to call System.exit here, in the case
              * of a remote test, a Timer thread seems to be generated by the Naming.lookup()
              * method, and it does not die.
              */
 		}
 
 		/**
 		 * @see TestListener#testIterationStart(LoopIterationEvent)
 		 */
 		public void testIterationStart(LoopIterationEvent event) {
 			// ignored
 		}
 	}
 
 	private static void println(String str) {
 		System.out.println(str);
 	}
 
 	private static final String[][] DEFAULT_ICONS = {
 			{ TestPlanGui.class.getName(), "org/apache/jmeter/images/beaker.gif" },//$NON-NLS-1$
 			{ AbstractTimerGui.class.getName(), "org/apache/jmeter/images/timer.gif" },//$NON-NLS-1$
 			{ ThreadGroupGui.class.getName(), "org/apache/jmeter/images/thread.gif" },//$NON-NLS-1$
 			{ AbstractVisualizer.class.getName(), "org/apache/jmeter/images/meter.png" },//$NON-NLS-1$
 			{ AbstractConfigGui.class.getName(), "org/apache/jmeter/images/testtubes.png" },//$NON-NLS-1$
 			// Note: these were the original settings (just moved to a static
 			// array)
 			// Commented out because there is no such file
 			// {
 			// AbstractPreProcessorGui.class.getName(),
 			// "org/apache/jmeter/images/testtubes.gif" },
 			// {
 			// AbstractPostProcessorGui.class.getName(),
 			// "org/apache/jmeter/images/testtubes.gif" },
 			{ AbstractControllerGui.class.getName(), "org/apache/jmeter/images/knob.gif" },//$NON-NLS-1$
 			{ WorkBenchGui.class.getName(), "org/apache/jmeter/images/clipboard.gif" },//$NON-NLS-1$
 			{ AbstractSamplerGui.class.getName(), "org/apache/jmeter/images/pipet.png" }//$NON-NLS-1$
 	// AbstractAssertionGUI not defined
 	};
 
 	public String[][] getIconMappings() {
 		String iconProp = JMeterUtils.getPropDefault("jmeter.icons",//$NON-NLS-1$
                 "org/apache/jmeter/images/icon.properties");//$NON-NLS-1$
 		Properties p = JMeterUtils.loadProperties(iconProp);
 		if (p == null) {
 			log.info(iconProp + " not found - using default icon set");
 			return DEFAULT_ICONS;
 		}
 		log.info("Loaded icon properties from " + iconProp);
 		String[][] iconlist = new String[p.size()][3];
 		Enumeration pe = p.keys();
 		int i = 0;
 		while (pe.hasMoreElements()) {
 			String key = (String) pe.nextElement();
 			String icons[] = JOrphanUtils.split(p.getProperty(key), " ");//$NON-NLS-1$
 			iconlist[i][0] = key;
 			iconlist[i][1] = icons[0];
 			if (icons.length > 1)
 				iconlist[i][2] = icons[1];
 			i++;
 		}
 		return iconlist;
 	}
 
 	public String[][] getResourceBundles() {
 		return new String[0][];
 	}
 	
 	private void logProperty(String prop){
 		log.info(prop+"="+System.getProperty(prop));//$NON-NLS-1$
 	}
 	private void logProperty(String prop,String separator){
 		log.info(prop+separator+System.getProperty(prop));//$NON-NLS-1$
 	}
 }
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/gui/action/Load.java b/src/core/org/apache/jmeter/gui/action/Load.java
index 9b0d96095..bb24901ac 100644
--- a/src/core/org/apache/jmeter/gui/action/Load.java
+++ b/src/core/org/apache/jmeter/gui/action/Load.java
@@ -1,157 +1,151 @@
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
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileInputStream;
-import java.io.IOException;
 import java.io.InputStream;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 import javax.swing.JTree;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * @author Michael Stover
  * @version $Revision$
  */
 public class Load implements Command {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private static final boolean expandTree = JMeterUtils.getPropDefault("onload.expandtree", true);
 	
 	private static Set commands = new HashSet();
 	static {
 		commands.add(ActionNames.OPEN);
 		commands.add(ActionNames.MERGE);
 	}
 
 	public Load() {
 		super();
 	}
 
 	public Set getActionNames() {
 		return commands;
 	}
 
 	public void doAction(ActionEvent e) {
 		boolean merging = e.getActionCommand().equals(ActionNames.MERGE);
 
 		if (!merging) {
 			ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), "close"));
 		}
 
 		JFileChooser chooser = FileDialoger.promptToOpenFile(new String[] { ".jmx" });
 		if (chooser == null) {
 			return;
 		}
 		boolean isTestPlan = false;
 		InputStream reader = null;
 		File f = null;
 		try {
 			f = chooser.getSelectedFile();
 			if (f != null) {
 				if (merging) {
 					log.info("Merging file: " + f);
 				} else {
 					log.info("Loading file: " + f);
 					FileServer.getFileServer().setBasedir(f.getAbsolutePath());
 				}
 				reader = new FileInputStream(f);
 				HashTree tree = SaveService.loadTree(reader);
 				isTestPlan = insertLoadedTree(e.getID(), tree);
 			}
 		} catch (NoClassDefFoundError ex) // Allow for missing optional jars
 		{
             log.warn("Missing jar file", ex);
 			String msg = ex.getMessage();
 			if (msg == null) {
 				msg = "Missing jar file - see log for details";
 			}
 			JMeterUtils.reportErrorToUser(msg);
         } catch (ConversionException ex) {
             log.warn("Could not convert file "+ex);
             JMeterUtils.reportErrorToUser(SaveService.CEtoString(ex));
 		} catch (Exception ex) {
             log.warn("Unexpected error", ex);
 			String msg = ex.getMessage();
 			if (msg == null) {
 				msg = "Unexpected error - see log for details";
 			}
 			JMeterUtils.reportErrorToUser(msg);
 		} finally {
-			try {
-				if (reader!=null) {
-                    reader.close();
-                }
-			} catch (IOException e1) {
-				// ignored
-			}
+            JOrphanUtils.closeQuietly(reader);
 			GuiPackage.getInstance().updateCurrentGui();
 			GuiPackage.getInstance().getMainFrame().repaint();
 		}
 		// don't change name if merging
 		if (!merging && isTestPlan && f != null) {
 			GuiPackage.getInstance().setTestPlanFile(f.getAbsolutePath());
 		}
 	}
 
 	/**
 	 * Returns a boolean indicating whether the loaded tree was a full test plan
 	 */
 	public boolean insertLoadedTree(int id, HashTree tree) throws Exception, IllegalUserActionException {
 		// convertTree(tree);
 		if (tree == null) {
 			throw new Exception("Error in TestPlan - see log file");
 		}
 		boolean isTestPlan = tree.getArray()[0] instanceof TestPlan;
 		HashTree newTree = GuiPackage.getInstance().addSubTree(tree);
 		GuiPackage.getInstance().updateCurrentGui();
 		GuiPackage.getInstance().getMainFrame().getTree().setSelectionPath(
 				new TreePath(((JMeterTreeNode) newTree.getArray()[0]).getPath()));
 		tree = GuiPackage.getInstance().getCurrentSubTree();
 		ActionRouter.getInstance().actionPerformed(
 				new ActionEvent(tree.get(tree.getArray()[tree.size() - 1]), id, ActionNames.SUB_TREE_LOADED));
 	    if (expandTree) {
 			JTree jTree = GuiPackage.getInstance().getMainFrame().getTree();
 			   for(int i = 0; i < jTree.getRowCount(); i++) {
 			     jTree.expandRow(i);
 			   }
 	    }
 
 		return isTestPlan;
 	}
 }
diff --git a/src/core/org/apache/jmeter/gui/action/Save.java b/src/core/org/apache/jmeter/gui/action/Save.java
index 04a29d8a8..5de5c2814 100644
--- a/src/core/org/apache/jmeter/gui/action/Save.java
+++ b/src/core/org/apache/jmeter/gui/action/Save.java
@@ -1,142 +1,131 @@
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
 
 package org.apache.jmeter.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.FileOutputStream;
-import java.io.IOException;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.save.OldSaveService;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * @author Michael Stover
  * @author <a href="mailto:klancast@swbell.net">Keith Lancaster</a>
  * @version $Revision$ updated on $Date$
  */
 public class Save implements Command {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private static Set commands = new HashSet();
 	static {
 		commands.add(ActionNames.SAVE_AS);
 		commands.add(ActionNames.SAVE_ALL_AS);
 		commands.add(ActionNames.SAVE);
 	}
 
 	/**
 	 * Constructor for the Save object.
 	 */
 	public Save() {
 	}
 
 	/**
 	 * Gets the ActionNames attribute of the Save object.
 	 * 
 	 * @return the ActionNames value
 	 */
 	public Set getActionNames() {
 		return commands;
 	}
 
 	public void doAction(ActionEvent e) throws IllegalUserActionException {
 		HashTree subTree = null;
 		if (!commands.contains(e.getActionCommand())) {
 			throw new IllegalUserActionException("Invalid user command:" + e.getActionCommand());
 		}
 		if (e.getActionCommand().equals(ActionNames.SAVE_AS)) {
 			subTree = GuiPackage.getInstance().getCurrentSubTree();
 		} else {
 			subTree = GuiPackage.getInstance().getTreeModel().getTestPlan();
 		}
 
 		String updateFile = GuiPackage.getInstance().getTestPlanFile();
 		if (!ActionNames.SAVE.equals(e.getActionCommand()) || updateFile == null) {
 			JFileChooser chooser = FileDialoger.promptToSaveFile(GuiPackage.getInstance().getTreeListener()
 					.getCurrentNode().getName()
 					+ ".jmx");
 			if (chooser == null) {
 				return;
 			}
 			updateFile = chooser.getSelectedFile().getAbsolutePath();
 			if (!e.getActionCommand().equals(ActionNames.SAVE_AS)) {
 				GuiPackage.getInstance().setTestPlanFile(updateFile);
 			}
 		}
 		// TODO: doesn't putting this here mark the tree as
 		// saved even though a failure may occur later?
 
 		ActionRouter.getInstance().doActionNow(new ActionEvent(subTree, e.getID(), ActionNames.SUB_TREE_SAVED));
 		try {
 			convertSubTree(subTree);
 		} catch (Exception err) {
 		}
 		FileOutputStream ostream = null;
 		try {
 			ostream = new FileOutputStream(updateFile);
 			if (SaveService.isSaveTestPlanFormat20()) {
 				OldSaveService.saveSubTree(subTree, ostream);
 			} else {
 				SaveService.saveTree(subTree, ostream);
 			}
 		} catch (Throwable ex) {
 			GuiPackage.getInstance().setTestPlanFile(null);
 			log.error("", ex);
 			throw new IllegalUserActionException("Couldn't save test plan to file: " + updateFile);
 		} finally {
-			closeStream(ostream);
+            JOrphanUtils.closeQuietly(ostream);
 		}
 	}
 
 	// package protected to all for separate test code
 	void convertSubTree(HashTree tree) {
 		Iterator iter = new LinkedList(tree.list()).iterator();
 		while (iter.hasNext()) {
 			JMeterTreeNode item = (JMeterTreeNode) iter.next();
 			convertSubTree(tree.getTree(item));
 			TestElement testElement = item.getTestElement();
 			tree.replace(item, testElement);
 		}
 	}
-
-	private void closeStream(FileOutputStream fos) {
-		if (fos != null) {
-			try {
-				fos.close();
-			} catch (IOException ex) {
-				log.error("", ex);
-			}
-		}
-	}
-
 }
diff --git a/src/core/org/apache/jmeter/reporters/ResultSaver.java b/src/core/org/apache/jmeter/reporters/ResultSaver.java
index 819fabc21..41fabb1c7 100644
--- a/src/core/org/apache/jmeter/reporters/ResultSaver.java
+++ b/src/core/org/apache/jmeter/reporters/ResultSaver.java
@@ -1,189 +1,186 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Save Result responseData to a set of files TODO - perhaps save other items
  * such as headers?
  * 
  * This is mainly intended for validation tests
  * 
  */
 public class ResultSaver extends AbstractTestElement implements Serializable, SampleListener, Clearable {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	// File name sequence number
 	private static long sequenceNumber = 0;
 
 	public static final String FILENAME = "FileSaver.filename"; // $NON-NLS-1$
 
 	public static final String ERRORS_ONLY = "FileSaver.errorsonly"; // $NON-NLS-1$
 
 	private static synchronized long nextNumber() {
 		return ++sequenceNumber;
 	}
 
 	/*
 	 * Constructor is initially called once for each occurrence in the test plan
 	 * For GUI, several more instances are created Then clear is called at start
 	 * of test Called several times during test startup The name will not
 	 * necessarily have been set at this point.
 	 */
 	public ResultSaver() {
 		super();
 		// log.debug(Thread.currentThread().getName());
 		// System.out.println(">> "+me+" "+this.getName()+"
 		// "+Thread.currentThread().getName());
 	}
 
 	/*
 	 * Constructor for use during startup (intended for non-GUI use) @param name
 	 * of summariser
 	 */
 	public ResultSaver(String name) {
 		this();
 		setName(name);
 	}
 
 	/*
 	 * This is called once for each occurrence in the test plan, before the
 	 * start of the test. The super.clear() method clears the name (and all
 	 * other properties), so it is called last.
 	 */
 	public void clear() {
 		// System.out.println("-- "+me+this.getName()+"
 		// "+Thread.currentThread().getName());
 		super.clear();
 		sequenceNumber = 0; // TODO is this the right thing to do?
 	}
 
 	/**
 	 * Saves the sample result (and any sub results) in files
 	 * 
 	 * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
 	 */
 	public void sampleOccurred(SampleEvent e) {
       processSample(e.getResult());
    }
 
    /**
     * Recurse the whole (sub)result hierarchy.
     *
     * @param s Sample result
     */
    private void processSample(SampleResult s) {
 		saveSample(s);
 		SampleResult[] sr = s.getSubResults();
 		for (int i = 0; i < sr.length; i++) {
 			processSample(sr[i]);
 		}
 	}
 
 	/**
 	 * @param s
 	 *            SampleResult to save
 	 */
 	private void saveSample(SampleResult s) {
 		// Should we save successful samples?
 		if (s.isSuccessful() && getErrorsOnly())
 			return;
 
 		nextNumber();
 		String fileName = makeFileName(s.getContentType());
 		log.debug("Saving " + s.getSampleLabel() + " in " + fileName);
         s.setResultFileName(fileName);// Associate sample with file name
 		File out = new File(fileName);
 		FileOutputStream pw = null;
 		try {
 			pw = new FileOutputStream(out);
 			pw.write(s.getResponseData());
 		} catch (FileNotFoundException e1) {
 			log.error("Error creating sample file for " + s.getSampleLabel(), e1);
 		} catch (IOException e1) {
 			log.error("Error saving sample " + s.getSampleLabel(), e1);
 		} finally {
-			try {
-				if (pw != null)
-					pw.close();
-			} catch (IOException e) {
-			}
+            JOrphanUtils.closeQuietly(pw);
 		}
 	}
 
 	/**
 	 * @return fileName composed of fixed prefix, a number, and a suffix derived
 	 *         from the contentType e.g. Content-Type:
 	 *         text/html;charset=ISO-8859-1
 	 */
 	private String makeFileName(String contentType) {
 		String suffix = "unknown";
 		if (contentType != null) {
 			int i = contentType.indexOf("/");
 			if (i != -1) {
 				int j = contentType.indexOf(";");
 				if (j != -1) {
 					suffix = contentType.substring(i + 1, j);
 				} else {
 					suffix = contentType.substring(i + 1);
 				}
 			}
 		}
 		return getFilename() + sequenceNumber + "." + suffix;
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.samplers.SampleListener#sampleStarted(org.apache.jmeter.samplers.SampleEvent)
 	 */
 	public void sampleStarted(SampleEvent e) {
 		// not used
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.samplers.SampleListener#sampleStopped(org.apache.jmeter.samplers.SampleEvent)
 	 */
 	public void sampleStopped(SampleEvent e) {
 		// not used
 	}
 
 	private String getFilename() {
 		return getPropertyAsString(FILENAME);
 	}
 
 	private boolean getErrorsOnly() {
 		return getPropertyAsBoolean(ERRORS_ONLY);
 	}
 }
diff --git a/src/core/org/apache/jmeter/save/SaveGraphicsService.java b/src/core/org/apache/jmeter/save/SaveGraphicsService.java
index 38cca87c0..2149c40a3 100644
--- a/src/core/org/apache/jmeter/save/SaveGraphicsService.java
+++ b/src/core/org/apache/jmeter/save/SaveGraphicsService.java
@@ -1,191 +1,188 @@
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
 
 import java.awt.Dimension;
 import java.awt.Graphics2D;
 import java.awt.image.BufferedImage;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.OutputStream;
 
 import javax.swing.JComponent;
 
 import com.sun.image.codec.jpeg.JPEGCodec;
 import com.sun.image.codec.jpeg.JPEGEncodeParam;
 import com.sun.image.codec.jpeg.JPEGImageEncoder;
 
 import org.apache.batik.ext.awt.image.codec.PNGEncodeParam;
 import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;
 import org.apache.batik.ext.awt.image.codec.tiff.TIFFEncodeParam;
 import org.apache.batik.ext.awt.image.codec.tiff.TIFFImageEncoder;
+import org.apache.jorphan.util.JOrphanUtils;
 
 /**
  * Class is responsible for taking a component and saving it as a JPEG, PNG or
  * TIFF. The class is very simple. Thanks to Batik and the developers who worked
  * so hard on it. The original implementation I used JAI, which allows
  * redistribution but requires indemnification. Luckily Batik has an alternative
  * to JAI. Hurray for Apache projects. I don't see any noticeable differences
  * between Batik and JAI.
  */
 public class SaveGraphicsService {
 
 	public static final int PNG = 0;
 
 	public static final int TIFF = 1;
 
 	public static final String PNG_EXTENSION = ".png";
 
 	public static final String TIFF_EXTENSION = ".tif";
 
 	public static final String JPEG_EXTENSION = ".jpg";
 
 	/**
 	 * 
 	 */
 	public SaveGraphicsService() {
 		super();
 	}
 
 	/**
 	 * If someone wants to save a JPEG, use this method. There is a limitation
 	 * though. It uses gray scale instead of color due to artifacts with color
 	 * encoding. For some reason, it does not translate pure red and orange
 	 * correctly. To make the text readable, gray scale is used.
 	 * 
 	 * @param filename
 	 * @param component
 	 */
 	public void saveUsingJPEGEncoder(String filename, JComponent component) {
 		Dimension size = component.getSize();
 		// We use Gray scale, since color produces poor quality
 		// this is an unfortunate result of the default codec
 		// implementation.
 		BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_USHORT_GRAY);
 		Graphics2D grp = image.createGraphics();
 		component.paint(grp);
 
 		File outfile = new File(filename + JPEG_EXTENSION);
 		FileOutputStream fos = createFile(outfile);
 		JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
 		Float q = new Float(1.0);
 		param.setQuality(q.floatValue(), true);
 		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos, param);
 
 		try {
 			encoder.encode(image);
-			fos.close();
 		} catch (Exception e) {
 			e.printStackTrace();
 		} finally {
-			try {
-				fos.close();
-			} catch (Exception e) {
-			}
+            JOrphanUtils.closeQuietly(fos);
 		}
 	}
 
 	/**
 	 * Method will save the JComponent as an image. The formats are PNG, and
 	 * TIFF.
 	 * 
 	 * @param filename
 	 * @param type
 	 * @param component
 	 */
 	public void saveJComponent(String filename, int type, JComponent component) {
 		Dimension size = component.getSize();
 		BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
 		Graphics2D grp = image.createGraphics();
 		component.paint(grp);
 
 		if (type == PNG) {
 			filename += PNG_EXTENSION;
 			this.savePNGWithBatik(filename, image);
 		} else if (type == TIFF) {
 			filename = filename + TIFF_EXTENSION;
 			this.saveTIFFWithBatik(filename, image);
 		}
 	}
 
 	/**
 	 * Use Batik to save a PNG of the graph
 	 * 
 	 * @param filename
 	 * @param image
 	 */
 	public void savePNGWithBatik(String filename, BufferedImage image) {
 		File outfile = new File(filename);
 		OutputStream fos = createFile(outfile);
 		PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam(image);
 		PNGImageEncoder encoder = new PNGImageEncoder(fos, param);
 		try {
 			encoder.encode(image);
 		} catch (Exception e) {
 			// do nothing
 		} finally {
 			try {
 				fos.close();
 			} catch (Exception e) {
 				// do nothing
 			}
 		}
 	}
 
 	/**
 	 * Use Batik to save a TIFF file of the graph
 	 * 
 	 * @param filename
 	 * @param image
 	 */
 	public void saveTIFFWithBatik(String filename, BufferedImage image) {
 		File outfile = new File(filename);
 		OutputStream fos = createFile(outfile);
 		TIFFEncodeParam param = new TIFFEncodeParam();
 		TIFFImageEncoder encoder = new TIFFImageEncoder(fos, param);
 		try {
 			encoder.encode(image);
 		} catch (Exception e) {
 			// do nothing
 		} finally {
 			try {
 				fos.close();
 			} catch (Exception e) {
 				// do nothing
 			}
 		}
 	}
 
 	/**
 	 * Create a new file for the graphics. Since the method creates a new file,
 	 * we shouldn't get a FNFE.
 	 * 
 	 * @param filename
 	 * @return
 	 */
 	public FileOutputStream createFile(File filename) {
 		try {
 			return new FileOutputStream(filename);
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 			return null;
 		}
 	}
 
 }
diff --git a/src/functions/org/apache/jmeter/functions/XPathFileContainer.java b/src/functions/org/apache/jmeter/functions/XPathFileContainer.java
index 2ad8e8f44..cffa657fd 100644
--- a/src/functions/org/apache/jmeter/functions/XPathFileContainer.java
+++ b/src/functions/org/apache/jmeter/functions/XPathFileContainer.java
@@ -1,141 +1,141 @@
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
 
 package org.apache.jmeter.functions;
 
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.xpath.XPathAPI;
 import org.w3c.dom.NodeList;
 import org.xml.sax.SAXException;
 
 //@see org.apache.jmeter.functions.PackageTest for unit tests
 
 /**
  * File data container for XML files Data is accessible via XPath
  * 
  */
 public class XPathFileContainer {
 
 	private static Logger log = LoggingManager.getLoggerForClass();
 
 	private NodeList nodeList;
 
 	private String fileName; // name of the file
 
 	private String xpath;
 
 	/** Keeping track of which row is next to be read. */
 	private int nextRow;
 	int getNextRow(){// give access to Test code
 		return nextRow;
 	}
 	
 	private XPathFileContainer()// Not intended to be called directly
 	{
 	}
 
 	public XPathFileContainer(String file, String xpath) throws FileNotFoundException, IOException,
 			ParserConfigurationException, SAXException, TransformerException {
 		log.debug("XPath(" + file + ") xpath " + xpath + "");
 		fileName = file;
 		this.xpath = xpath;
 		nextRow = 0;
 		load();
 	}
 
 	private void load() throws IOException, FileNotFoundException, ParserConfigurationException, SAXException,
 			TransformerException {
 		InputStream fis = null;
 		try {
 			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
 
 			fis = new FileInputStream(fileName);
 			nodeList = XPathAPI.selectNodeList(builder.parse(fis), xpath);
 			log.debug("found " + nodeList.getLength());
 
 		} catch (FileNotFoundException e) {
 			nodeList = null;
 			log.warn(e.toString());
 			throw e;
 		} catch (IOException e) {
 			nodeList = null;
 			log.warn(e.toString());
 			throw e;
 		} catch (ParserConfigurationException e) {
 			nodeList = null;
 			log.warn(e.toString());
 			throw e;
 		} catch (SAXException e) {
 			nodeList = null;
 			log.warn(e.toString());
 			throw e;
 		} catch (TransformerException e) {
 			nodeList = null;
 			log.warn(e.toString());
 			throw e;
 		} finally {
-			if (fis != null)
-				fis.close();
+            JOrphanUtils.closeQuietly(fis);
 		}
 	}
 
 	public String getXPathString(int num) {
 		return nodeList.item(num).getNodeValue();
 	}
 
 	/**
 	 * Returns the next row to the caller, and updates it, allowing for wrap
 	 * round
 	 * 
 	 * @return the first free (unread) row
 	 * 
 	 */
 	public int nextRow() {
 		int row = nextRow;
 		nextRow++;
 		if (nextRow >= size())// 0-based
 		{
 			nextRow = 0;
 		}
 		log.debug(new StringBuffer("Row: ").append(row).toString());
 		return row;
 	}
 
 	public int size() {
 		return (nodeList == null) ? -1 : nodeList.getLength();
 	}
 
 	/**
 	 * @return the file name for this class
 	 */
 	public String getFileName() {
 		return fileName;
 	}
 
 }
\ No newline at end of file
diff --git a/src/jorphan/org/apache/jorphan/io/TextFile.java b/src/jorphan/org/apache/jorphan/io/TextFile.java
index 1b99a7db9..598ea6db5 100644
--- a/src/jorphan/org/apache/jorphan/io/TextFile.java
+++ b/src/jorphan/org/apache/jorphan/io/TextFile.java
@@ -1,181 +1,176 @@
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
 
 package org.apache.jorphan.io;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.Reader;
 import java.io.Writer;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Utility class to handle text files as a single lump of text.
  * <p>
  * Note this is just as memory-inefficient as handling a text file can be. Use
  * with restraint.
  * 
  * @author Giles Cope (gilescope at users.sourceforge.net)
  * @author Michael Stover (mstover1 at apache.org)
  * @author <a href="mailto:jsalvata@apache.org">Jordi Salvat i Alabart</a>
  * @version $Revision$ updated on $Date$
  */
 public class TextFile extends File {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	/**
 	 * File encoding. null means use the platform's default.
 	 */
 	private String encoding = null;
 
 	/**
 	 * Create a TextFile object to handle the named file with the given
 	 * encoding.
 	 * 
 	 * @param filename
 	 *            File to be read & written through this object.
 	 * @param encoding
 	 *            Encoding to be used when reading & writing this file.
 	 */
 	public TextFile(File filename, String encoding) {
 		super(filename.toString());
 		setEncoding(encoding);
 	}
 
 	/**
 	 * Create a TextFile object to handle the named file with the platform
 	 * default encoding.
 	 * 
 	 * @param filename
 	 *            File to be read & written through this object.
 	 */
 	public TextFile(File filename) {
 		super(filename.toString());
 	}
 
 	/**
 	 * Create a TextFile object to handle the named file with the platform
 	 * default encoding.
 	 * 
 	 * @param filename
 	 *            Name of the file to be read & written through this object.
 	 */
 	public TextFile(String filename) {
 		super(filename);
 	}
 
 	/**
 	 * Create a TextFile object to handle the named file with the given
 	 * encoding.
 	 * 
 	 * @param filename
 	 *            Name of the file to be read & written through this object.
 	 * @param encoding
 	 *            Encoding to be used when reading & writing this file.
 	 */
 	public TextFile(String filename, String encoding) {
 		super(filename);
 	}
 
 	/**
 	 * Create the file with the given string as content -- or replace it's
 	 * content with the given string if the file already existed.
 	 * 
 	 * @param body
 	 *            New content for the file.
 	 */
 	public void setText(String body) {
 		Writer writer = null;
 		try {
 			if (encoding == null) {
 				writer = new FileWriter(this);
 			} else {
 				writer = new OutputStreamWriter(new FileOutputStream(this), encoding);
 			}
 			writer.write(body);
 			writer.flush();
-			writer.close();
 		} catch (IOException ioe) {
-			try {
-				if (writer != null) {
-					writer.close();
-				}
-			} catch (IOException e) {
-			}
 			log.error("", ioe);
-		}
+		} finally {
+            JOrphanUtils.closeQuietly(writer);
+        }
 	}
 
 	/**
 	 * Read the whole file content and return it as a string.
 	 * 
 	 * @return the content of the file
 	 */
 	public String getText() {
 		String lineEnd = System.getProperty("line.separator"); //$NON-NLS-1$
 		StringBuffer sb = new StringBuffer();
 		Reader reader = null;
 		BufferedReader br = null;
 		try {
 			if (encoding == null) {
 				reader = new FileReader(this);
 			} else {
 				reader = new InputStreamReader(new FileInputStream(this), encoding);
 			}
 			br = new BufferedReader(reader);
 			String line = "NOTNULL"; //$NON-NLS-1$
 			while (line != null) {
 				line = br.readLine();
 				if (line != null) {
 					sb.append(line + lineEnd);
 				}
 			}
 		} catch (IOException ioe) {
 			log.error("", ioe); //$NON-NLS-1$
 		} finally {
 			JOrphanUtils.closeQuietly(br); // closes reader as well
 		}
 
 		return sb.toString();
 	}
 
 	/**
 	 * @return Encoding being used to read & write this file.
 	 */
 	public String getEncoding() {
 		return encoding;
 	}
 
 	/**
 	 * @param string
 	 *            Encoding to be used to read & write this file.
 	 */
 	public void setEncoding(String string) {
 		encoding = string;
 	}
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
index acbb459ae..df1cb75b0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
@@ -1,250 +1,244 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.Serializable;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
+import org.apache.jorphan.util.JOrphanUtils;
 
 /**
  * Description:<br>
  * <br>
  * StandardGenerator will be the default generator used to pre-process logs. It
  * uses JMeter classes to generate the .jmx file. The first version of the
  * utility only generated the HTTP requests as XML, but it required users to
  * copy and paste it into a blank jmx file. Doing that way isn't flexible and
  * would require changes to keep the format in sync.
  * <p>
  * This version is a completely new class with a totally different
  * implementation, since generating the XML is no longer handled by the
  * generator. The generator is only responsible for handling the parsed results
  * and passing it to the appropriate JMeter class.
  * <p>
  * Notes:<br>
  * the class needs to first create a thread group and add it to the HashTree.
  * Then the samplers should be added to the thread group. Listeners shouldn't be
  * added and should be left up to the user. One option is to provide parameters,
  * so the user can pass the desired listener to the tool.
  * <p>
  * 
  * author Peter Lin<br>
  * Created on: Jul 1, 2003<br>
  */
 
 public class StandardGenerator implements Generator, Serializable {
 
 	protected HTTPSamplerBase SAMPLE = null;
 
 	transient protected FileWriter WRITER = null;
 
 	transient protected OutputStream OUTPUT = null;
 
 	protected String FILENAME = null;
 
 	protected File FILE = null;
 
 	// NOT USED transient protected ThreadGroup THREADGROUP = null;
 	// Anyway, was this supposed to be the class from java.lang, or
 	// jmeter.threads?
 
 	/**
 	 * The constructor is used by GUI and samplers to generate request objects.
 	 */
 	public StandardGenerator() {
 		super();
 		init();
 	}
 
 	/**
 	 * 
 	 * @param file
 	 */
 	public StandardGenerator(String file) {
 		FILENAME = file;
 		init();
 	}
 
 	/**
 	 * initialize the generator. It should create the following objects.
 	 * <p>
 	 * <ol>
 	 * <li> ListedHashTree</li>
 	 * <li> ThreadGroup</li>
 	 * <li> File object</li>
 	 * <li> Writer</li>
 	 * </ol>
 	 */
 	protected void init() {
 		generateRequest();
 	}
 
 	/**
 	 * Create the FileWriter to save the JMX file.
 	 */
 	protected void initStream() {
 		try {
 			this.OUTPUT = new FileOutputStream(FILE);
 		} catch (IOException exception) {
 			// do nothing
 		}
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#close()
 	 */
 	public void close() {
-		try {
-			if (OUTPUT != null) {
-				OUTPUT.close();
-			}
-			if (WRITER != null) {
-				WRITER.close();
-			}
-		} catch (IOException exception) {
-		}
+        JOrphanUtils.closeQuietly(OUTPUT);
+        JOrphanUtils.closeQuietly(WRITER);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setHost(java.lang.String)
 	 */
 	public void setHost(String host) {
 		SAMPLE.setDomain(host);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setLabel(java.lang.String)
 	 */
 	public void setLabel(String label) {
 
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setMethod(java.lang.String)
 	 */
 	public void setMethod(String post_get) {
 		SAMPLE.setMethod(post_get);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setParams(org.apache.jmeter.protocol.http.util.accesslog.NVPair[])
 	 */
 	public void setParams(NVPair[] params) {
 		for (int idx = 0; idx < params.length; idx++) {
 			SAMPLE.addArgument(params[idx].getName(), params[idx].getValue());
 		}
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setPath(java.lang.String)
 	 */
 	public void setPath(String path) {
 		SAMPLE.setPath(path);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setPort(int)
 	 */
 	public void setPort(int port) {
 		SAMPLE.setPort(port);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setQueryString(java.lang.String)
 	 */
 	public void setQueryString(String querystring) {
 		SAMPLE.parseArguments(querystring);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setSourceLogs(java.lang.String)
 	 */
 	public void setSourceLogs(String sourcefile) {
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#setTarget(java.lang.Object)
 	 */
 	public void setTarget(Object target) {
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.protocol.http.util.accesslog.Generator#generateRequest()
 	 */
 	public Object generateRequest() {
 		try {
 			SAMPLE = HTTPSamplerFactory.newInstance();
 		} catch (NullPointerException e) {
 			e.printStackTrace();
 		}
 		return SAMPLE;
 	}
 
 	/**
 	 * save must be called to write the jmx file, otherwise it will not be
 	 * saved.
 	 */
 	public void save() {
 		try {
 			// no implementation at this time, since
 			// we bypass the idea of having a console
 			// tool to generate test plans. Instead
 			// I decided to have a sampler that uses
 			// the generator and parser directly
 		} catch (Exception exception) {
 		}
 	}
 
 	/**
 	 * Reset the HTTPSampler to make sure it is a new instance.
 	 */
 	public void reset() {
 		SAMPLE = null;
 		generateRequest();
 	}
 
 	// TODO write some tests
 }
diff --git a/src/reports/org/apache/jmeter/JMeterReport.java b/src/reports/org/apache/jmeter/JMeterReport.java
index 79d5f063d..8b1e2598a 100644
--- a/src/reports/org/apache/jmeter/JMeterReport.java
+++ b/src/reports/org/apache/jmeter/JMeterReport.java
@@ -1,518 +1,525 @@
 //$Header$
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
 package org.apache.jmeter;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.Enumeration;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Properties;
 
 import org.apache.commons.cli.avalon.CLArgsParser;
 import org.apache.commons.cli.avalon.CLOption;
 import org.apache.commons.cli.avalon.CLOptionDescriptor;
 import org.apache.commons.cli.avalon.CLUtil;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.gui.ReportGui;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.plugin.JMeterPlugin;
 import org.apache.jmeter.plugin.PluginManager;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestListener;
 import org.apache.jmeter.testelement.ReportPlan;
 import org.apache.jmeter.report.gui.ReportPageGui;
 import org.apache.jmeter.report.gui.action.ReportLoad;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.report.gui.action.ReportCheckDirty;
 import org.apache.jmeter.report.gui.tree.ReportTreeListener;
 import org.apache.jmeter.report.gui.tree.ReportTreeModel;
 import org.apache.jmeter.report.writers.gui.HTMLReportWriterGui;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.reporters.Summariser;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.gui.ComponentUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * @author pete
  *
  * JMeterReport is the main class for the reporting component. For now,
  * the plan is to make the reporting component a separate GUI, which 
  * can run in GUI or console mode. The purpose of the GUI is to design
  * reports, which can then be run. One of the primary goals of the
  * reporting component is to make it so the reports can be run in an
  * automated process.
  * The report GUI is different than the main JMeter GUI in several ways.
  * <ul>
  *   <li> the gui is not multi-threaded</li>
  *   <li> the gui uses different components</li>
  *   <li> the gui is focused on designing reports from the jtl logs
  * generated during a test run</li>
  * </ul>
  * The class follows the same design as JMeter.java. This should keep
  * things consistent and make it easier to maintain.
  */
 public class JMeterReport implements JMeterPlugin {
 
     transient private static Logger log = LoggingManager.getLoggerForClass();
 
     private static final int PROPFILE_OPT = 'p';
 
     private static final int PROPFILE2_OPT = 'q'; // Bug 33920 - additional
                                                     // prop files
 
     private static final int TESTFILE_OPT = 't';
 
     private static final int LOGFILE_OPT = 'l';
 
     private static final int NONGUI_OPT = 'n';
 
     private static final int HELP_OPT = 'h';
 
     private static final int VERSION_OPT = 'v';
 
     private static final int SERVER_OPT = 's';
 
     private static final int JMETER_PROPERTY = 'J';
 
     private static final int SYSTEM_PROPERTY = 'D';
 
     private static final int LOGLEVEL = 'L';
 
     private static final int REMOTE_OPT = 'r';
 
     private static final int JMETER_HOME_OPT = 'd';
 
     private JMeterReport parent;
 
     private static final CLOptionDescriptor[] options = new CLOptionDescriptor[] {
             new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED, HELP_OPT,
                     "print usage information and exit"),
             new CLOptionDescriptor("version", CLOptionDescriptor.ARGUMENT_DISALLOWED, VERSION_OPT,
                     "print the version information and exit"),
             new CLOptionDescriptor("propfile", CLOptionDescriptor.ARGUMENT_REQUIRED, PROPFILE_OPT,
                     "the jmeter property file to use"),
             new CLOptionDescriptor("addprop", CLOptionDescriptor.ARGUMENT_REQUIRED
                     | CLOptionDescriptor.DUPLICATES_ALLOWED, // Bug 33920 -
                                                                 // allow
                                                                 // multiple
                                                                 // props
                     PROPFILE2_OPT, "additional property file(s)"),
             new CLOptionDescriptor("testfile", CLOptionDescriptor.ARGUMENT_REQUIRED, TESTFILE_OPT,
                     "the jmeter test(.jmx) file to run"),
             new CLOptionDescriptor("logfile", CLOptionDescriptor.ARGUMENT_REQUIRED, LOGFILE_OPT,
                     "the file to log samples to"),
             new CLOptionDescriptor("nongui", CLOptionDescriptor.ARGUMENT_DISALLOWED, NONGUI_OPT,
                     "run JMeter in nongui mode"),
             new CLOptionDescriptor("server", CLOptionDescriptor.ARGUMENT_DISALLOWED, SERVER_OPT,
                     "run the JMeter server"),
             new CLOptionDescriptor("jmeterproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, JMETER_PROPERTY, "Define additional JMeter properties"),
             new CLOptionDescriptor("systemproperty", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, SYSTEM_PROPERTY, "Define additional JMeter properties"),
             new CLOptionDescriptor("loglevel", CLOptionDescriptor.DUPLICATES_ALLOWED
                     | CLOptionDescriptor.ARGUMENTS_REQUIRED_2, LOGLEVEL,
                     "Define loglevel: [category=]level e.g. jorphan=INFO or " + "jmeter.util=DEBUG"),
             new CLOptionDescriptor("runremote", CLOptionDescriptor.ARGUMENT_DISALLOWED, REMOTE_OPT,
                     "Start remote servers from non-gui mode"),
             new CLOptionDescriptor("homedir", CLOptionDescriptor.ARGUMENT_REQUIRED, JMETER_HOME_OPT,
                     "the jmeter home directory to use"), };
     
     transient boolean testEnded = false;
 
     /**
 	 * 
 	 */
 	public JMeterReport() {
 		super();
 	}
 
     /**
      * The default icons for the report GUI.
      */
     private static final String[][] DEFAULT_ICONS = {
             { AbstractVisualizer.class.getName(), "org/apache/jmeter/images/meter.png" },
             { AbstractConfigGui.class.getName(), "org/apache/jmeter/images/testtubes.png" },
             { HTMLReportWriterGui.class.getName(), "org/apache/jmeter/images/new/pencil.png" },
             { ReportPageGui.class.getName(), "org/apache/jmeter/images/new/scroll.png" },
             { ReportGui.class.getName(), "org/apache/jmeter/images/new/book.png" }
     };
     
 	/* (non-Javadoc)
 	 * @see org.apache.jmeter.plugin.JMeterPlugin#getIconMappings()
 	 */
 	public String[][] getIconMappings() {
         String iconProp = JMeterUtils.getPropDefault("jmeter.icons", "org/apache/jmeter/images/icon.properties");
         Properties p = JMeterUtils.loadProperties(iconProp);
         if (p == null) {
             log.info(iconProp + " not found - using default icon set");
             return DEFAULT_ICONS;
         }
         log.info("Loaded icon properties from " + iconProp);
         String[][] iconlist = new String[p.size()][3];
         Enumeration pe = p.keys();
         int i = 0;
         while (pe.hasMoreElements()) {
             String key = (String) pe.nextElement();
             String icons[] = JOrphanUtils.split(p.getProperty(key), " ");
             iconlist[i][0] = key;
             iconlist[i][1] = icons[0];
             if (icons.length > 1)
                 iconlist[i][2] = icons[1];
             i++;
         }
         return iconlist;
 	}
 
 	/* (non-Javadoc)
 	 * @see org.apache.jmeter.plugin.JMeterPlugin#getResourceBundles()
 	 */
 	public String[][] getResourceBundles() {
         return new String[0][];
 	}
 
     public void startNonGui(CLOption testFile, CLOption logFile){
         System.setProperty("JMeter.NonGui", "true");
         JMeterReport driver = new JMeterReport();
         driver.parent = this;
         PluginManager.install(this, false);
     }
     
     public void startGui(CLOption testFile) {
         PluginManager.install(this, true);
         ReportTreeModel treeModel = new ReportTreeModel();
         ReportTreeListener treeLis = new ReportTreeListener(treeModel);
         treeLis.setActionHandler(ReportActionRouter.getInstance());
         ReportGuiPackage.getInstance(treeLis, treeModel);
         org.apache.jmeter.gui.ReportMainFrame main = 
             new org.apache.jmeter.gui.ReportMainFrame(ReportActionRouter.getInstance(),
                 treeModel, treeLis);
         main.setTitle("Apache JMeter Report");
         main.setIconImage(JMeterUtils.getImage("jmeter.jpg").getImage());
         ComponentUtil.centerComponentInWindow(main, 80);
         main.show();
 
         ReportActionRouter.getInstance().actionPerformed(new ActionEvent(main, 1, ReportCheckDirty.ADD_ALL));
         if (testFile != null) {
+            FileInputStream reader = null;
             try {
                 File f = new File(testFile.getArgument());
                 log.info("Loading file: " + f);
-                FileInputStream reader = new FileInputStream(f);
+                reader = new FileInputStream(f);
                 HashTree tree = SaveService.loadTree(reader);
 
                 ReportGuiPackage.getInstance().setReportPlanFile(f.getAbsolutePath());
 
                 new ReportLoad().insertLoadedTree(1, tree);
             } catch (Exception e) {
                 log.error("Failure loading test file", e);
                 JMeterUtils.reportErrorToUser(e.toString());
             }
+            finally{
+                JOrphanUtils.closeQuietly(reader);
+            }
         }
     }
 
 	private void run(String testFile, String logFile, boolean remoteStart) {
 		FileInputStream reader = null;
 		try {
 			File f = new File(testFile);
 			if (!f.exists() || !f.isFile()) {
 				System.out.println("Could not open " + testFile);
 				return;
 			}
 			FileServer.getFileServer().setBasedir(f.getAbsolutePath());
 
 			reader = new FileInputStream(f);
 			log.info("Loading file: " + f);
 
 			HashTree tree = SaveService.loadTree(reader);
 
 			// Remove the disabled items
 			// For GUI runs this is done in Start.java
 			convertSubTree(tree);
 
 			if (logFile != null) {
 				ResultCollector logger = new ResultCollector();
 				logger.setFilename(logFile);
 				tree.add(tree.getArray()[0], logger);
 			}
 			String summariserName = JMeterUtils.getPropDefault(
 					"summariser.name", "");//$NON-NLS-1$
 			if (summariserName.length() > 0) {
 				log.info("Creating summariser <" + summariserName + ">");
 				System.out.println("Creating summariser <" + summariserName + ">");
 				Summariser summer = new Summariser(summariserName);
 				tree.add(tree.getArray()[0], summer);
 			}
 			tree.add(tree.getArray()[0], new ListenToTest(parent));
 			System.out.println("Created the tree successfully");
             /**
 			JMeterEngine engine = null;
 			if (!remoteStart) {
 				engine = new StandardJMeterEngine();
 				engine.configure(tree);
 				System.out.println("Starting the test");
 				engine.runTest();
 			} else {
 				String remote_hosts_string = JMeterUtils.getPropDefault(
 						"remote_hosts", "127.0.0.1");
 				java.util.StringTokenizer st = new java.util.StringTokenizer(
 						remote_hosts_string, ",");
 				List engines = new LinkedList();
 				while (st.hasMoreElements()) {
 					String el = (String) st.nextElement();
 					System.out.println("Configuring remote engine for " + el);
 					// engines.add(doRemoteInit(el.trim(), tree));
 				}
 				System.out.println("Starting remote engines");
 				Iterator iter = engines.iterator();
 				while (iter.hasNext()) {
 					engine = (JMeterEngine) iter.next();
 					engine.runTest();
 				}
 				System.out.println("Remote engines have been started");
 			}
             **/
 		} catch (Exception e) {
 			System.out.println("Error in NonGUIDriver " + e.toString());
 			log.error("", e);
 		}
+        finally{
+            JOrphanUtils.closeQuietly(reader);
+        }
 	}
 
     
     /**
      * 
      * @param args
      */
     public void start(String[] args) {
         CLArgsParser parser = new CLArgsParser(args, options);
         if (null != parser.getErrorString()) {
             System.err.println("Error: " + parser.getErrorString());
             System.out.println("Usage");
             System.out.println(CLUtil.describeOptions(options).toString());
             return;
         }
         try {
             initializeProperties(parser);
             log.info("Version " + JMeterUtils.getJMeterVersion());
             log.info("java.version=" + System.getProperty("java.version"));
             log.info(JMeterUtils.getJMeterCopyright());
             if (parser.getArgumentById(VERSION_OPT) != null) {
                 System.out.println(JMeterUtils.getJMeterCopyright());
                 System.out.println("Version " + JMeterUtils.getJMeterVersion());
             } else if (parser.getArgumentById(HELP_OPT) != null) {
                 System.out.println(JMeterUtils.getResourceFileAsText("org/apache/jmeter/help.txt"));
             } else if (parser.getArgumentById(NONGUI_OPT) == null) {
                 startGui(parser.getArgumentById(TESTFILE_OPT));
             } else {
                 startNonGui(parser.getArgumentById(TESTFILE_OPT), parser.getArgumentById(LOGFILE_OPT));
             }
         } catch (Exception e) {
             e.printStackTrace();
             System.out.println("An error occurred: " + e.getMessage());
             System.exit(-1);
         }
     }
     
     private void initializeProperties(CLArgsParser parser) {
         if (parser.getArgumentById(PROPFILE_OPT) != null) {
             JMeterUtils.getProperties(parser.getArgumentById(PROPFILE_OPT).getArgument());
         } else {
             JMeterUtils.getProperties(NewDriver.getJMeterDir() + File.separator + "bin" + File.separator
                     + "jmeter.properties");
         }
 
         // Bug 33845 - allow direct override of Home dir
         if (parser.getArgumentById(JMETER_HOME_OPT) == null) {
             JMeterUtils.setJMeterHome(NewDriver.getJMeterDir());
         } else {
             JMeterUtils.setJMeterHome(parser.getArgumentById(JMETER_HOME_OPT).getArgument());
         }
 
         // Process command line property definitions (can occur multiple times)
 
         Properties jmeterProps = JMeterUtils.getJMeterProperties();
         List clOptions = parser.getArguments();
         int size = clOptions.size();
 
         for (int i = 0; i < size; i++) {
             CLOption option = (CLOption) clOptions.get(i);
             String name = option.getArgument(0);
             String value = option.getArgument(1);
 
             switch (option.getDescriptor().getId()) {
             case PROPFILE2_OPT: // Bug 33920 - allow multiple props
                 File f = new File(name);
                 try {
                     jmeterProps.load(new FileInputStream(f));
                 } catch (FileNotFoundException e) {
                     log.warn("Can't find additional property file: " + name, e);
                 } catch (IOException e) {
                     log.warn("Error loading additional property file: " + name, e);
                 }
                 break;
             case SYSTEM_PROPERTY:
                 if (value.length() > 0) { // Set it
                     log.info("Setting System property: " + name + "=" + value);
                     System.getProperties().setProperty(name, value);
                 } else { // Reset it
                     log.warn("Removing System property: " + name);
                     System.getProperties().remove(name);
                 }
                 break;
             case JMETER_PROPERTY:
                 if (value.length() > 0) { // Set it
                     log.info("Setting JMeter property: " + name + "=" + value);
                     jmeterProps.setProperty(name, value);
                 } else { // Reset it
                     log.warn("Removing JMeter property: " + name);
                     jmeterProps.remove(name);
                 }
                 break;
             case LOGLEVEL:
                 if (value.length() > 0) { // Set category
                     log.info("LogLevel: " + name + "=" + value);
                     LoggingManager.setPriority(value, name);
                 } else { // Set root level
                     log.warn("LogLevel: " + name);
                     LoggingManager.setPriority(name);
                 }
                 break;
             }
         }
 
     }
     
     /**
      * Code copied from AbstractAction.java and modified to suit TestElements
      * 
      * @param tree
      */
     private void convertSubTree(HashTree tree) {// TODO check build dependencies
         Iterator iter = new LinkedList(tree.list()).iterator();
         while (iter.hasNext()) {
             TestElement item = (TestElement) iter.next();
             if (item.isEnabled()) {
                 // This is done for GUI runs in JMeterTreeModel.addSubTree()
                 if (item instanceof ReportPlan) {
                     ReportPlan tp = (ReportPlan) item;
                 }
                 // TODO handle ReplaceableControllers
                 // if (item instanceof ReplaceableController)
                 // {
                 // System.out.println("Replaceable "+item.getClass().getName());
                 // HashTree subTree = tree.getTree(item);
                 //
                 // if (subTree != null)
                 // {
                 // ReplaceableController rc =
                 // (ReplaceableController) item;//.createTestElement();
                 // rc.replace(subTree);
                 // convertSubTree(subTree);
                 // tree.replace(item, rc.getReplacement());
                 // }
                 // }
                 // else
                 {
                     // System.out.println("NonReplaceable
                     // "+item.getClass().getName());
                     convertSubTree(tree.getTree(item));
                     // TestElement testElement = item.createTestElement();
                     // tree.replace(item, testElement);
                 }
             } else {
                 // System.out.println("Disabled "+item.getClass().getName());
                 tree.remove(item);
             }
         }
     }
     
     /**
      * Listen to test and exit program after test completes, after a 5 second
      * delay to give listeners a chance to close out their files.
      */
     private static class ListenToTest implements TestListener, Runnable, Remoteable {
         int started = 0;
 
         private JMeterReport _parent;
 
         private ListenToTest(JMeterReport parent) {
             _parent = parent;
         }
 
         public synchronized void testEnded(String host) {
             started--;
             log.info("Remote host " + host + " finished");
             if (started == 0) {
                 testEnded();
             }
         }
 
         public void testEnded() {
             Thread stopSoon = new Thread(this);
             stopSoon.start();
         }
 
         public synchronized void testStarted(String host) {
             started++;
             log.info("Started remote host: " + host);
         }
 
         public void testStarted() {
             log.info(JMeterUtils.getResString("running_test"));
         }
 
         /**
          * This is a hack to allow listeners a chance to close their files. Must
          * implement a queue for sample responses tied to the engine, and the
          * engine won't deliver testEnded signal till all sample responses have
          * been delivered. Should also improve performance of remote JMeter
          * testing.
          */
         public void run() {
             System.out.println("Tidying up ...");
             try {
                 Thread.sleep(5000);
             } catch (InterruptedException e) {
                 // ignored
             }
             System.out.println("... end of run");
             _parent.testEnded = true;
         }
 
         /**
          * @see TestListener#testIterationStart(LoopIterationEvent)
          */
         public void testIterationStart(LoopIterationEvent event) {
             // ignored
         }
     }
 
 }
\ No newline at end of file
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java b/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java
index 367aeeb0f..08f732967 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportLoad.java
@@ -1,154 +1,148 @@
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.File;
 import java.io.FileInputStream;
-import java.io.IOException;
 import java.io.InputStream;
 import java.util.HashSet;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 import javax.swing.tree.TreePath;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.gui.util.ReportFileDialoger;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.ReportPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.XStream;
 
 /**
  * @author Peter Lin
  * @version $Revision$
  */
 public class ReportLoad implements Command {
 	transient private static Logger log = LoggingManager.getLoggerForClass();
 
 	XStream loadService = new XStream();
 
 	private static Set commands = new HashSet();
 	static {
 		commands.add("open");
 		commands.add("merge");
 	}
 
 	public ReportLoad() {
 		super();
 	}
 
 	public Set getActionNames() {
 		return commands;
 	}
 
 	public void doAction(ActionEvent e) {
 		boolean merging = e.getActionCommand().equals("merge");
 
 		if (!merging) {
 			ReportActionRouter.getInstance().doActionNow(
 					new ActionEvent(e.getSource(), e.getID(), "close"));
 		}
 
 		JFileChooser chooser = ReportFileDialoger
 				.promptToOpenFile(new String[] { ".jmr" });
 		if (chooser == null) {
 			return;
 		}
 		boolean isTestPlan = false;
 		InputStream reader = null;
 		File f = null;
 		try {
 			f = chooser.getSelectedFile();
 			if (f != null) {
 				if (merging) {
 					log.info("Merging file: " + f);
 				} else {
 					log.info("Loading file: " + f);
 					FileServer.getFileServer().setBasedir(f.getAbsolutePath());
 				}
 				reader = new FileInputStream(f);
 				HashTree tree = SaveService.loadTree(reader);
 				isTestPlan = insertLoadedTree(e.getID(), tree);
 			}
 		} catch (NoClassDefFoundError ex) // Allow for missing optional jars
 		{
 			String msg = ex.getMessage();
 			if (msg == null) {
 				msg = "Missing jar file - see log for details";
 				log.warn("Missing jar file", ex);
 			}
 			JMeterUtils.reportErrorToUser(msg);
 		} catch (Exception ex) {
 			String msg = ex.getMessage();
 			if (msg == null) {
 				msg = "Unexpected error - see log for details";
 				log.warn("Unexpected error", ex);
 			}
 			JMeterUtils.reportErrorToUser(msg);
 		} finally {
-			try {
-				if (reader != null) {
-                    reader.close();
-                }
-			} catch (IOException e1) {
-				// ignored
-			}
+		    JOrphanUtils.closeQuietly(reader);
 			ReportGuiPackage.getInstance().updateCurrentGui();
 			ReportGuiPackage.getInstance().getMainFrame().repaint();
 		}
 		// don't change name if merging
 		if (!merging && isTestPlan && f != null) {
 			ReportGuiPackage.getInstance().setReportPlanFile(f.getAbsolutePath());
 		}
 	}
 
 	/**
 	 * Returns a boolean indicating whether the loaded tree was a full test plan
 	 */
 	public boolean insertLoadedTree(int id, HashTree tree) throws Exception,
 			IllegalUserActionException {
 		// convertTree(tree);
 		if (tree == null) {
 			throw new Exception("Error in TestPlan - see log file");
 		}
 		boolean isTestPlan = tree.getArray()[0] instanceof ReportPlan;
 		HashTree newTree = ReportGuiPackage.getInstance().addSubTree(tree);
 		ReportGuiPackage.getInstance().updateCurrentGui();
 		ReportGuiPackage.getInstance().getMainFrame().getTree()
 				.setSelectionPath(
 						new TreePath(((ReportTreeNode) newTree.getArray()[0])
 								.getPath()));
 		tree = ReportGuiPackage.getInstance().getCurrentSubTree();
 		ReportActionRouter.getInstance().actionPerformed(
 				new ActionEvent(tree.get(tree.getArray()[tree.size() - 1]), id,
 						ReportCheckDirty.SUB_TREE_LOADED));
 
 		return isTestPlan;
 	}
 }
diff --git a/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java b/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java
index 3172d3e08..542f56621 100644
--- a/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java
+++ b/src/reports/org/apache/jmeter/report/gui/action/ReportSave.java
@@ -1,152 +1,142 @@
 //$Header$
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
 
 package org.apache.jmeter.report.gui.action;
 
 import java.awt.event.ActionEvent;
 import java.io.FileOutputStream;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.Set;
 
 import javax.swing.JFileChooser;
 
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.gui.ReportGuiPackage;
 import org.apache.jmeter.report.gui.action.ReportActionRouter;
 import org.apache.jmeter.gui.action.Command;
 import org.apache.jmeter.gui.util.ReportFileDialoger;
 import org.apache.jmeter.report.gui.tree.ReportTreeNode;
 import org.apache.jmeter.save.OldSaveService;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
+import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * @author Peter Lin
  * @version $Revision$ updated on $Date$
  */
 public class ReportSave implements Command {
 	transient private static Logger log = LoggingManager.getLoggerForClass();
 
 	public final static String SAVE_ALL_AS = "save_all_as";
 
 	public final static String SAVE_AS = "save_as";
 
 	public final static String SAVE = "save";
 
 	// NOTUSED private String chosenFile;
 
 	private static Set commands = new HashSet();
 	static {
 		commands.add(SAVE_AS);
 		commands.add(SAVE_ALL_AS);
 		commands.add(SAVE);
 	}
 
 	/**
 	 * Constructor for the Save object.
 	 */
 	public ReportSave() {
 	}
 
 	/**
 	 * Gets the ActionNames attribute of the Save object.
 	 * 
 	 * @return the ActionNames value
 	 */
 	public Set getActionNames() {
 		return commands;
 	}
 
 	public void doAction(ActionEvent e) throws IllegalUserActionException {
 		HashTree subTree = null;
 		if (!commands.contains(e.getActionCommand())) {
 			throw new IllegalUserActionException("Invalid user command:" + e.getActionCommand());
 		}
 		if (e.getActionCommand().equals(SAVE_AS)) {
 			subTree = ReportGuiPackage.getInstance().getCurrentSubTree();
 		} else {
 			subTree = ReportGuiPackage.getInstance().getTreeModel().getReportPlan();
 		}
 
 		String updateFile = ReportGuiPackage.getInstance().getReportPlanFile();
 		if (!SAVE.equals(e.getActionCommand()) || updateFile == null) {
 			JFileChooser chooser = ReportFileDialoger.promptToSaveFile(ReportGuiPackage.getInstance().getTreeListener()
 					.getCurrentNode().getName()
 					+ ".jmr");
 			if (chooser == null) {
 				return;
 			}
 			updateFile = chooser.getSelectedFile().getAbsolutePath();
 			if (!e.getActionCommand().equals(SAVE_AS)) {
 				ReportGuiPackage.getInstance().setReportPlanFile(updateFile);
 			}
 		}
 		// TODO: doesn't putting this here mark the tree as
 		// saved even though a failure may occur later?
 
 		ReportActionRouter.getInstance().doActionNow(new ActionEvent(subTree, e.getID(), ReportCheckDirty.SUB_TREE_SAVED));
 		try {
 			convertSubTree(subTree);
 		} catch (Exception err) {
 		}
 		FileOutputStream ostream = null;
 		try {
 			ostream = new FileOutputStream(updateFile);
 			if (SaveService.isSaveTestPlanFormat20()) {
 				OldSaveService.saveSubTree(subTree, ostream);
                 log.info("saveSubTree");
 			} else {
 				SaveService.saveTree(subTree, ostream);
                 log.info("saveTree");
 			}
 		} catch (Throwable ex) {
 			ReportGuiPackage.getInstance().setReportPlanFile(null);
 			log.error("", ex);
 			throw new IllegalUserActionException("Couldn't save test plan to file: " + updateFile);
 		} finally {
-			closeStream(ostream);
+            JOrphanUtils.closeQuietly(ostream);
 		}
 	}
 
 	private void convertSubTree(HashTree tree) {
 		Iterator iter = new LinkedList(tree.list()).iterator();
 		while (iter.hasNext()) {
 			ReportTreeNode item = (ReportTreeNode) iter.next();
 			convertSubTree(tree.getTree(item));
 			TestElement testElement = item.getTestElement();
 			tree.replace(item, testElement);
 		}
 	}
-
-	private void closeStream(FileOutputStream fos) {
-		if (fos != null) {
-			try {
-				fos.close();
-			} catch (Exception ex) {
-				log.error("", ex);
-			}
-		}
-	}
-
 }
