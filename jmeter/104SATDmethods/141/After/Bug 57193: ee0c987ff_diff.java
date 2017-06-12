diff --git a/src/functions/org/apache/jmeter/functions/XPathWrapper.java b/src/functions/org/apache/jmeter/functions/XPathWrapper.java
index cbfd1f49c..47c338332 100644
--- a/src/functions/org/apache/jmeter/functions/XPathWrapper.java
+++ b/src/functions/org/apache/jmeter/functions/XPathWrapper.java
@@ -1,133 +1,135 @@
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
 
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.util.HashMap;
 import java.util.Map;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.xml.sax.SAXException;
 
 /**
  * This class wraps the XPathFileContainer for use across multiple threads.
  *
  * It maintains a list of nodelist containers, one for each file/xpath combination
  *
  */
 final class XPathWrapper {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /*
      * This Map serves two purposes:
-     * - maps names to  containers
-     * - ensures only one container per file across all threads
+     * <ul>
+     *   <li>maps names to  containers</li>
+     *   <li>ensures only one container per file across all threads</li>
+     * </ul>
      * The key is the concatenation of the file name and the XPath string
      */
     //@GuardedBy("fileContainers")
     private static final Map<String, XPathFileContainer> fileContainers =
         new HashMap<String, XPathFileContainer>();
 
     /* The cache of file packs - for faster local access */
     private static final ThreadLocal<Map<String, XPathFileContainer>> filePacks =
         new ThreadLocal<Map<String, XPathFileContainer>>() {
         @Override
         protected Map<String, XPathFileContainer> initialValue() {
             return new HashMap<String, XPathFileContainer>();
         }
     };
 
     private XPathWrapper() {// Prevent separate instantiation
         super();
     }
 
     private static XPathFileContainer open(String file, String xpathString) {
         String tname = Thread.currentThread().getName();
         log.info(tname+": Opening " + file);
         XPathFileContainer frcc=null;
         try {
             frcc = new XPathFileContainer(file, xpathString);
         } catch (FileNotFoundException e) {
             log.warn(e.getLocalizedMessage());
         } catch (IOException e) {
             log.warn(e.getLocalizedMessage());
         } catch (ParserConfigurationException e) {
             log.warn(e.getLocalizedMessage());
         } catch (SAXException e) {
             log.warn(e.getLocalizedMessage());
         } catch (TransformerException e) {
             log.warn(e.getLocalizedMessage());
         }
         return frcc;
     }
 
     /**
      * Not thread-safe - must be called from a synchronized method.
      *
-     * @param file
-     * @param xpathString
+     * @param file name of the file
+     * @param xpathString xpath to look up in file
      * @return the next row from the file container
      */
     public static String getXPathString(String file, String xpathString) {
         Map<String, XPathFileContainer> my = filePacks.get();
         String key = file+xpathString;
         XPathFileContainer xpfc = my.get(key);
         if (xpfc == null) // We don't have a local copy
         {
             synchronized(fileContainers){
                 xpfc = fileContainers.get(key);
                 if (xpfc == null) { // There's no global copy either
                     xpfc=open(file, xpathString);
                 }
                 if (xpfc != null) {
                     fileContainers.put(key, xpfc);// save the global copy
                 }
             }
             // TODO improve the error handling
             if (xpfc == null) {
                 log.error("XPathFileContainer is null!");
                 return ""; //$NON-NLS-1$
             }
             my.put(key,xpfc); // save our local copy
         }
         if (xpfc.size()==0){
             log.warn("XPathFileContainer has no nodes: "+file+" "+xpathString);
             return ""; //$NON-NLS-1$
         }
         int currentRow = xpfc.nextRow();
         log.debug("getting match number " + currentRow);
         return xpfc.getXPathString(currentRow);
     }
 
     public static void clearAll() {
         log.debug("clearAll()");
         filePacks.get().clear();
         String tname = Thread.currentThread().getName();
         log.info(tname+": clearing container");
         synchronized (fileContainers) {
             fileContainers.clear();
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/gui/DefaultTreeTableModel.java b/src/jorphan/org/apache/jorphan/gui/DefaultTreeTableModel.java
index dcc94cb09..aae5553bc 100644
--- a/src/jorphan/org/apache/jorphan/gui/DefaultTreeTableModel.java
+++ b/src/jorphan/org/apache/jorphan/gui/DefaultTreeTableModel.java
@@ -1,52 +1,52 @@
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
 
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.TreeNode;
 
 import org.apache.jorphan.reflect.Functor;
 
 public class DefaultTreeTableModel extends AbstractTreeTableModel {
 
     private static final long serialVersionUID = 240L;
 
     public DefaultTreeTableModel() {
         this(new DefaultMutableTreeNode());
     }
 
     /**
-     * @param root
+     * @param root the {@link TreeNode} to use as root
      */
     public DefaultTreeTableModel(TreeNode root) {
         super(root);
     }
 
     /**
-     * @param headers
-     * @param readFunctors
-     * @param writeFunctors
-     * @param editorClasses
+     * @param headers the headers to use
+     * @param readFunctors the read functors to use
+     * @param writeFunctors the write functors to use
+     * @param editorClasses the editor classes to use
      */
     public DefaultTreeTableModel(String[] headers, Functor[] readFunctors,
             Functor[] writeFunctors, Class<?>[] editorClasses) {
         super(headers, readFunctors, writeFunctors, editorClasses);
     }
 
 }
diff --git a/src/jorphan/org/apache/jorphan/gui/GuiUtils.java b/src/jorphan/org/apache/jorphan/gui/GuiUtils.java
index 851d341cf..70526380a 100644
--- a/src/jorphan/org/apache/jorphan/gui/GuiUtils.java
+++ b/src/jorphan/org/apache/jorphan/gui/GuiUtils.java
@@ -1,144 +1,149 @@
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
 
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.Toolkit;
 import java.awt.datatransfer.Clipboard;
 import java.awt.datatransfer.DataFlavor;
 import java.awt.datatransfer.Transferable;
 import java.awt.datatransfer.UnsupportedFlavorException;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 
 import javax.swing.JComboBox;
 import javax.swing.JComponent;
 import javax.swing.JLabel;
 import javax.swing.JMenu;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellEditor;
 import javax.swing.table.TableCellRenderer;
 import javax.swing.table.TableColumn;
 
 public final class GuiUtils {
 
     /**
      * Create a scroll panel that sets its preferred size to its minimum size.
      * Explicitly for scroll panes that live inside other scroll panes, or
      * within containers that stretch components to fill the area they exist in.
      * Use this for any component you would put in a scroll pane (such as
      * TextAreas, tables, JLists, etc). It is here for convenience and to avoid
      * duplicate code. JMeter displays best if you follow this custom.
      *
      * @param comp
      *            the component which should be placed inside the scroll pane
      * @return a JScrollPane containing the specified component
      */
     public static JScrollPane makeScrollPane(Component comp) {
         JScrollPane pane = new JScrollPane(comp);
         pane.setPreferredSize(pane.getMinimumSize());
         return pane;
     }
 
     /**
      * Fix the size of a column according to the header text.
      * 
      * @param column to be resized
      * @param table containing the column
      */
     public static void fixSize(TableColumn column, JTable table) {
         TableCellRenderer rndr;
         rndr = column.getHeaderRenderer();
         if (rndr == null){
             rndr = table.getTableHeader().getDefaultRenderer();
         }
         Component c = rndr.getTableCellRendererComponent(
                 table, column.getHeaderValue(), false, false, -1, column.getModelIndex());
         int width = c.getPreferredSize().width+10;
         column.setMaxWidth(width);
         column.setPreferredWidth(width);
         column.setResizable(false);        
     }
     
     /**
      * Create a GUI component JLabel + JComboBox with a left and right margin (5px)
-     * @param label
-     * @param comboBox
+     * @param label the label
+     * @param comboBox the combo box
      * @return the JComponent (margin+JLabel+margin+JComboBox)
      */
     public static JComponent createLabelCombo(String label, JComboBox comboBox) {
         JPanel labelCombo = new JPanel();
         labelCombo.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         JLabel caption = new JLabel(label);
         caption.setBorder(new EmptyBorder(0, 5, 0, 5));
         labelCombo.add(caption);
         labelCombo.add(comboBox);
         return labelCombo;
     }
 
     /**
      * Stop any editing that is currently being done on the table. This will
      * save any changes that have already been made.
+     *
+     * @param table the table to stop on editing
      */
     public static void stopTableEditing(JTable table) {
         if (table.isEditing()) {
             TableCellEditor cellEditor = table.getCellEditor(table.getEditingRow(), table.getEditingColumn());
             cellEditor.stopCellEditing();
         }
     }
     
     /**
      * Get pasted text from clipboard
+     *
      * @return String Pasted text
      * @throws UnsupportedFlavorException
+     *             if the clipboard data can not be get as a {@link String}
      * @throws IOException
+     *             if the clipboard data is no longer available
      */
     public static String getPastedText() throws UnsupportedFlavorException, IOException {
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         Transferable trans = clipboard.getContents(null);
         DataFlavor[] flavourList = trans.getTransferDataFlavors();
         Collection<DataFlavor> flavours = new ArrayList<DataFlavor>(flavourList.length);
         if (Collections.addAll(flavours, flavourList) && flavours.contains(DataFlavor.stringFlavor)) {
             return (String) trans.getTransferData(DataFlavor.stringFlavor);
         } else {
             return null;
         }
     }
 
     /**
      * Make menu scrollable
      * @param menu {@link JMenu}
      */
     public static void makeScrollableMenu(JMenu menu) { 
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         if(menu.getItemCount()>0) {
             // We use 80% of height
             int maxItems = (int)Math.round(
                     screenSize.getHeight()*0.8/menu.getMenuComponent(0).getPreferredSize().getHeight());
             MenuScroller.setScrollerFor(menu, maxItems, 200);
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/gui/JTreeTable.java b/src/jorphan/org/apache/jorphan/gui/JTreeTable.java
index 15ec80afb..8a5bf542a 100644
--- a/src/jorphan/org/apache/jorphan/gui/JTreeTable.java
+++ b/src/jorphan/org/apache/jorphan/gui/JTreeTable.java
@@ -1,67 +1,67 @@
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
 
 import java.util.Vector;
 
 import javax.swing.JTable;
 
 public class JTreeTable extends JTable {
 
     private static final long serialVersionUID = 240L;
 
     /**
      * The default implementation will use DefaultTreeTableModel
      */
     public JTreeTable() {
         super(new DefaultTreeTableModel());
     }
 
     /**
-     * @param numRows
-     * @param numColumns
+     * @param numRows number of rows the table holds
+     * @param numColumns number of columns the table holds
      */
     public JTreeTable(int numRows, int numColumns) {
         super(numRows, numColumns);
     }
 
     /**
-     * @param dm
+     * @param dm the data model to use
      */
     public JTreeTable(TreeTableModel dm) {
         super(dm);
     }
 
     /**
-     * @param rowData
-     * @param columnNames
+     * @param rowData the data for the table
+     * @param columnNames the names for the columns
      */
     public JTreeTable(Object[][] rowData, Object[] columnNames) {
         super(rowData, columnNames);
     }
 
     /**
-     * @param rowData
-     * @param columnNames
+     * @param rowData the data for the table
+     * @param columnNames the names for the columns
      */
     public JTreeTable(Vector<?> rowData, Vector<?> columnNames) {
         super(rowData, columnNames);
     }
 
 }
diff --git a/src/jorphan/org/apache/jorphan/math/StatCalculator.java b/src/jorphan/org/apache/jorphan/math/StatCalculator.java
index 6c53f303b..398959d06 100644
--- a/src/jorphan/org/apache/jorphan/math/StatCalculator.java
+++ b/src/jorphan/org/apache/jorphan/math/StatCalculator.java
@@ -1,270 +1,276 @@
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
 
 package org.apache.jorphan.math;
 
 import java.util.ConcurrentModificationException;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.TreeMap;
 import java.util.Map.Entry;
 
 import org.apache.commons.lang3.mutable.MutableLong;
 
 /**
  * This class serves as a way to calculate the median, max, min etc. of a list of values.
  * It is not threadsafe.
  *
+ * @param <T> type parameter for the calculator
+ *
  */
 public abstract class StatCalculator<T extends Number & Comparable<? super T>> {
 
     // key is the type to collect (usually long), value = count of entries
     private final Map<T, MutableLong> valuesMap = new TreeMap<T, MutableLong>();
     // We use a TreeMap because we need the entries to be sorted
 
     // Running values, updated for each sample
     private double sum = 0;
 
     private double sumOfSquares = 0;
 
     private double mean = 0;
 
     private double deviation = 0;
 
     private long count = 0;
 
     private T min;
 
     private T max;
 
     private long bytes = 0;
 
     private final T ZERO;
 
     private final T MAX_VALUE; // e.g. Long.MAX_VALUE
 
     private final T MIN_VALUE; // e.g. Long.MIN_VALUE
 
     /**
      * This constructor is used to set up particular values for the generic class instance.
      *
      * @param zero - value to return for Median and PercentPoint if there are no values
      * @param min - value to return for minimum if there are no values
      * @param max - value to return for maximum if there are no values
      */
     public StatCalculator(final T zero, final T min, final T max) {
         super();
         ZERO = zero;
         MAX_VALUE = max;
         MIN_VALUE = min;
         this.min = MAX_VALUE;
         this.max = MIN_VALUE;
     }
 
     public void clear() {
         valuesMap.clear();
         sum = 0;
         sumOfSquares = 0;
         mean = 0;
         deviation = 0;
         count = 0;
         bytes = 0;
         max = MIN_VALUE;
         min = MAX_VALUE;
     }
 
 
     public void addBytes(long newValue) {
         bytes += newValue;
     }
 
     public void addAll(StatCalculator<T> calc) {
         for(Entry<T, MutableLong> ent : calc.valuesMap.entrySet()) {
             addEachValue(ent.getKey(), ent.getValue().longValue());
         }
     }
 
     public T getMedian() {
         return getPercentPoint(0.5);
     }
 
     public long getTotalBytes() {
         return bytes;
     }
 
     /**
      * Get the value which %percent% of the values are less than. This works
      * just like median (where median represents the 50% point). A typical
      * desire is to see the 90% point - the value that 90% of the data points
      * are below, the remaining 10% are above.
      *
      * @param percent
+     *            number representing the wished percent (between <code>0</code>
+     *            and <code>1.0</code>)
      * @return number of values less than the percentage
      */
     public T getPercentPoint(float percent) {
         return getPercentPoint((double) percent);
     }
 
     /**
      * Get the value which %percent% of the values are less than. This works
      * just like median (where median represents the 50% point). A typical
      * desire is to see the 90% point - the value that 90% of the data points
      * are below, the remaining 10% are above.
      *
      * @param percent
+     *            number representing the wished percent (between <code>0</code>
+     *            and <code>1.0</code>)
      * @return the value which %percent% of the values are less than
      */
     public T getPercentPoint(double percent) {
         if (count <= 0) {
                 return ZERO;
         }
         if (percent >= 1.0) {
             return getMax();
         }
 
         // use Math.round () instead of simple (long) to provide correct value rounding
         long target = Math.round (count * percent);
         try {
             for (Entry<T, MutableLong> val : valuesMap.entrySet()) {
                 target -= val.getValue().longValue();
                 if (target <= 0){
                     return val.getKey();
                 }
             }
         } catch (ConcurrentModificationException ignored) {
             // ignored. May happen occasionally, but no harm done if so.
         }
         return ZERO; // TODO should this be getMin()?
     }
 
     /**
      * Returns the distribution of the values in the list.
      *
      * @return map containing either Integer or Long keys; entries are a Number array containing the key and the [Integer] count.
      * TODO - why is the key value also stored in the entry array? See Bug 53825
      */
     public Map<Number, Number[]> getDistribution() {
         Map<Number, Number[]> items = new HashMap<Number, Number[]>();
 
         for (Entry<T, MutableLong> entry : valuesMap.entrySet()) {
             Number[] dis = new Number[2];
             dis[0] = entry.getKey();
             dis[1] = entry.getValue();
             items.put(entry.getKey(), dis);
         }
         return items;
     }
 
     public double getMean() {
         return mean;
     }
 
     public double getStandardDeviation() {
         return deviation;
     }
 
     public T getMin() {
         return min;
     }
 
     public T getMax() {
         return max;
     }
 
     public long getCount() {
         return count;
     }
 
     public double getSum() {
         return sum;
     }
 
     protected abstract T divide(T val, int n);
 
     protected abstract T divide(T val, long n);
 
     /**
      * Update the calculator with the values for a set of samples.
      * 
      * @param val the common value, normally the elapsed time
      * @param sampleCount the number of samples with the same value
      */
     void addEachValue(T val, long sampleCount) {
         count += sampleCount;
         double currentVal = val.doubleValue();
         sum += currentVal * sampleCount;
         // For n same values in sum of square is equal to n*val^2
         sumOfSquares += currentVal * currentVal * sampleCount;
         updateValueCount(val, sampleCount);
         calculateDerivedValues(val);
     }
 
     /**
      * Update the calculator with the value for an aggregated sample.
      * 
      * @param val the aggregate value, normally the elapsed time
      * @param sampleCount the number of samples contributing to the aggregate value
      */
     public void addValue(T val, long sampleCount) {
         count += sampleCount;
         double currentVal = val.doubleValue();
         sum += currentVal;
         T actualValue = val;
         if (sampleCount > 1){
             // For n values in an aggregate sample the average value = (val/n)
             // So need to add n * (val/n) * (val/n) = val * val / n
             sumOfSquares += currentVal * currentVal / sampleCount;
             actualValue = divide(val, sampleCount);
         } else { // no need to divide by 1
             sumOfSquares += currentVal * currentVal;
         }
         updateValueCount(actualValue, sampleCount);
         calculateDerivedValues(actualValue);
     }
 
     private void calculateDerivedValues(T actualValue) {
         mean = sum / count;
         deviation = Math.sqrt((sumOfSquares / count) - (mean * mean));
         if (actualValue.compareTo(max) > 0){
             max=actualValue;
         }
         if (actualValue.compareTo(min) < 0){
             min=actualValue;
         }
     }
 
     /**
      * Add a single value (normally elapsed time)
      * 
      * @param val the value to add, which should correspond with a single sample
      * @see #addValue(Number, long)
      */
     public void addValue(T val) {
         addValue(val, 1L);
     }
 
     private void updateValueCount(T actualValue, long sampleCount) {
         MutableLong count = valuesMap.get(actualValue);
         if (count != null) {
             count.add(sampleCount);
         } else {
             // insert new value
             valuesMap.put(actualValue, new MutableLong(sampleCount));
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/reflect/ClassFinder.java b/src/jorphan/org/apache/jorphan/reflect/ClassFinder.java
index b4b89f725..6e44398ea 100644
--- a/src/jorphan/org/apache/jorphan/reflect/ClassFinder.java
+++ b/src/jorphan/org/apache/jorphan/reflect/ClassFinder.java
@@ -1,566 +1,577 @@
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
 
 package org.apache.jorphan.reflect;
 
 import java.io.File;
 import java.io.FilenameFilter;
 import java.io.IOException;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeSet;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipFile;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * This class finds classes that extend one of a set of parent classes
  *
  */
 public final class ClassFinder {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String DOT_JAR = ".jar"; // $NON-NLS-1$
     private static final String DOT_CLASS = ".class"; // $NON-NLS-1$
     private static final int DOT_CLASS_LEN = DOT_CLASS.length();
 
     // static only
     private ClassFinder() {
     }
 
     /**
      * Filter updates to TreeSet by only storing classes
      * that extend one of the parent classes
      *
      *
      */
     private static class FilterTreeSet extends TreeSet<String>{
         private static final long serialVersionUID = 234L;
 
         private final Class<?>[] parents; // parent classes to check
         private final boolean inner; // are inner classes OK?
 
         // hack to reduce the need to load every class in non-GUI mode, which only needs functions
         // TODO perhaps use BCEL to scan class files instead?
         private final String contains; // class name should contain this string
         private final String notContains; // class name should not contain this string
 
         private final transient ClassLoader contextClassLoader
             = Thread.currentThread().getContextClassLoader(); // Potentially expensive; do it once
 
         FilterTreeSet(Class<?> []parents, boolean inner, String contains, String notContains){
             super();
             this.parents=parents;
             this.inner=inner;
             this.contains=contains;
             this.notContains=notContains;
         }
 
         /**
          * Override the superclass so we only add classnames that
          * meet the criteria.
          *
          * @param s - classname (must be a String)
          * @return true if it is a new entry
          *
          * @see java.util.TreeSet#add(java.lang.Object)
          */
         @Override
         public boolean add(String s){
             if (contains(s)) {
                 return false;// No need to check it again
             }
             if (contains!=null && s.indexOf(contains) == -1){
                 return false; // It does not contain a required string
             }
             if (notContains!=null && s.indexOf(notContains) != -1){
                 return false; // It contains a banned string
             }
             if ((s.indexOf('$') == -1) || inner) { // $NON-NLS-1$
                 if (isChildOf(parents,s, contextClassLoader)) {
                     return super.add(s);
                 }
             }
             return false;
         }
     }
 
     private static class AnnoFilterTreeSet extends TreeSet<String>{
         private static final long serialVersionUID = 240L;
 
         private final boolean inner; // are inner classes OK?
 
         private final Class<? extends Annotation>[] annotations; // annotation classes to check
         private final transient ClassLoader contextClassLoader
             = Thread.currentThread().getContextClassLoader(); // Potentially expensive; do it once
         AnnoFilterTreeSet(Class<? extends Annotation> []annotations, boolean inner){
             super();
             this.annotations = annotations;
             this.inner=inner;
         }
         /**
          * Override the superclass so we only add classnames that
          * meet the criteria.
          *
          * @param s - classname (must be a String)
          * @return true if it is a new entry
          *
          * @see java.util.TreeSet#add(java.lang.Object)
          */
         @Override
         public boolean add(String s){
             if (contains(s)) {
                 return false;// No need to check it again
             }
             if ((s.indexOf('$') == -1) || inner) { // $NON-NLS-1$
                 if (hasAnnotationOnMethod(annotations,s, contextClassLoader)) {
                     return super.add(s);
                 }
             }
             return false;
         }
     }
 
     /**
      * Convenience method for
-     * {@link #findClassesThatExtend(String[], Class[], boolean)}
-     * with the option to include inner classes in the search set to false.
+     * {@link #findClassesThatExtend(String[], Class[], boolean)} with the
+     * option to include inner classes in the search set to false.
      *
+     * @param paths
+     *            pathnames or jarfiles to search for classes
+     * @param superClasses
+     *            required parent class(es)
      * @return List of Strings containing discovered class names.
+     * @throws IOException
+     *             when scanning the classes fails
      */
     public static List<String> findClassesThatExtend(String[] paths, Class<?>[] superClasses)
         throws IOException {
         return findClassesThatExtend(paths, superClasses, false);
     }
 
     // For each directory in the search path, add all the jars found there
     private static String[] addJarsInPath(String[] paths) {
         Set<String> fullList = new HashSet<String>();
         for (int i = 0; i < paths.length; i++) {
             final String path = paths[i];
             fullList.add(path); // Keep the unexpanded path
             // TODO - allow directories to end with .jar by removing this check?
             if (!path.endsWith(DOT_JAR)) {
                 File dir = new File(path);
                 if (dir.exists() && dir.isDirectory()) {
                     String[] jars = dir.list(new FilenameFilter() {
                         @Override
                         public boolean accept(File f, String name) {
                             return name.endsWith(DOT_JAR);
                         }
                     });
                     for (int x = 0; x < jars.length; x++) {
                         fullList.add(jars[x]);
                     }
                 }
             }
         }
         return fullList.toArray(new String[fullList.size()]);
     }
 
     /**
      * Find classes in the provided path(s)/jar(s) that extend the class(es).
      * @param strPathsOrJars - pathnames or jarfiles to search for classes
      * @param superClasses - required parent class(es)
      * @param innerClasses - should we include inner classes?
      *
      * @return List containing discovered classes
+     * @throws IOException when scanning for classes fails
      */
     public static List<String> findClassesThatExtend(String[] strPathsOrJars,
             final Class<?>[] superClasses, final boolean innerClasses)
             throws IOException  {
         return findClassesThatExtend(strPathsOrJars,superClasses,innerClasses,null,null);
     }
 
     /**
      * Find classes in the provided path(s)/jar(s) that extend the class(es).
      * @param strPathsOrJars - pathnames or jarfiles to search for classes
      * @param superClasses - required parent class(es)
      * @param innerClasses - should we include inner classes?
      * @param contains - classname should contain this string
      * @param notContains - classname should not contain this string
      *
      * @return List containing discovered classes
+     * @throws IOException when scanning classes fails
      */
     public static List<String> findClassesThatExtend(String[] strPathsOrJars,
             final Class<?>[] superClasses, final boolean innerClasses,
             String contains, String notContains)
             throws IOException  {
         return findClassesThatExtend(strPathsOrJars, superClasses, innerClasses, contains, notContains, false);
     }
 
     /**
      * Find classes in the provided path(s)/jar(s) that extend the class(es).
      * @param strPathsOrJars - pathnames or jarfiles to search for classes
      * @param annotations - required annotations
      * @param innerClasses - should we include inner classes?
      *
      * @return List containing discovered classes
+     * @throws IOException when scanning classes fails
      */
     public static List<String> findAnnotatedClasses(String[] strPathsOrJars,
             final Class<? extends Annotation>[] annotations, final boolean innerClasses)
             throws IOException  {
         return findClassesThatExtend(strPathsOrJars, annotations, innerClasses, null, null, true);
     }
 
     /**
      * Find classes in the provided path(s)/jar(s) that extend the class(es).
      * Inner classes are not searched.
      *
      * @param strPathsOrJars - pathnames or jarfiles to search for classes
      * @param annotations - required annotations
      *
      * @return List containing discovered classes
+     * @throws IOException when scanning classes fails
      */
     public static List<String> findAnnotatedClasses(String[] strPathsOrJars,
             final Class<? extends Annotation>[] annotations)
             throws IOException  {
         return findClassesThatExtend(strPathsOrJars, annotations, false, null, null, true);
     }
 
     /**
      * Find classes in the provided path(s)/jar(s) that extend the class(es).
      * @param searchPathsOrJars - pathnames or jarfiles to search for classes
      * @param classNames - required parent class(es) or annotations
      * @param innerClasses - should we include inner classes?
      * @param contains - classname should contain this string
      * @param notContains - classname should not contain this string
      * @param annotations - true if classnames are annotations
      *
      * @return List containing discovered classes
+     * @throws IOException when scanning classes fails
      */
     public static List<String> findClassesThatExtend(String[] searchPathsOrJars,
                 final Class<?>[] classNames, final boolean innerClasses,
                 String contains, String notContains, boolean annotations)
                 throws IOException  {
         if (log.isDebugEnabled()) {
             log.debug("searchPathsOrJars : " + Arrays.toString(searchPathsOrJars));
             log.debug("superclass : " + Arrays.toString(classNames));
             log.debug("innerClasses : " + innerClasses + " annotations: " + annotations);
             log.debug("contains: " + contains + " notContains: " + notContains);
         }
 
         // Find all jars in the search path
         String[] strPathsOrJars = addJarsInPath(searchPathsOrJars);
         for (int k = 0; k < strPathsOrJars.length; k++) {
             strPathsOrJars[k] = fixPathEntry(strPathsOrJars[k]);
         }
 
         // Now eliminate any classpath entries that do not "match" the search
         List<String> listPaths = getClasspathMatches(strPathsOrJars);
         if (log.isDebugEnabled()) {
             for (String path : listPaths) {
                 log.debug("listPaths : " + path);
             }
         }
 
         @SuppressWarnings("unchecked") // Should only be called with classes that extend annotations
         final Class<? extends Annotation>[] annoclassNames = (Class<? extends Annotation>[]) classNames;
         Set<String> listClasses =
             annotations ?
                 new AnnoFilterTreeSet(annoclassNames, innerClasses)
                 :
                 new FilterTreeSet(classNames, innerClasses, contains, notContains);
         // first get all the classes
         findClassesInPaths(listPaths, listClasses);
         if (log.isDebugEnabled()) {
             log.debug("listClasses.size()="+listClasses.size());
             for (String clazz : listClasses) {
                 log.debug("listClasses : " + clazz);
             }
         }
 
 //        // Now keep only the required classes
 //        Set subClassList = findAllSubclasses(superClasses, listClasses, innerClasses);
 //        if (log.isDebugEnabled()) {
 //            log.debug("subClassList.size()="+subClassList.size());
 //            Iterator tIter = subClassList.iterator();
 //            while (tIter.hasNext()) {
 //                log.debug("subClassList : " + tIter.next());
 //            }
 //        }
 
         return new ArrayList<String>(listClasses);//subClassList);
     }
 
     /*
      * Returns the classpath entries that match the search list of jars and paths
      */
     private static List<String> getClasspathMatches(String[] strPathsOrJars) {
         final String javaClassPath = System.getProperty("java.class.path"); // $NON-NLS-1$
         StringTokenizer stPaths =
             new StringTokenizer(javaClassPath, File.pathSeparator);
         if (log.isDebugEnabled()) {
             log.debug("Classpath = " + javaClassPath);
             for (int i = 0; i < strPathsOrJars.length; i++) {
                 log.debug("strPathsOrJars[" + i + "] : " + strPathsOrJars[i]);
             }
         }
 
         // find all jar files or paths that end with strPathOrJar
         ArrayList<String> listPaths = new ArrayList<String>();
         String strPath = null;
         while (stPaths.hasMoreTokens()) {
             strPath = fixPathEntry(stPaths.nextToken());
             if (strPathsOrJars == null) {
                 log.debug("Adding: " + strPath);
                 listPaths.add(strPath);
             } else {
                 boolean found = false;
                 for (int i = 0; i < strPathsOrJars.length; i++) {
                     if (strPath.endsWith(strPathsOrJars[i])) {
                         found = true;
                         log.debug("Adding " + strPath + " found at " + i);
                         listPaths.add(strPath);
                         break;// no need to look further
                     }
                 }
                 if (!found) {
                     log.debug("Did not find: " + strPath);
                 }
             }
         }
         return listPaths;
     }
 
     /**
      * Fix a path:
      * - replace "." by current directory
      * - trim any trailing spaces
      * - replace \ by /
      * - replace // by /
      * - remove all trailing /
      */
     private static String fixPathEntry(String path){
         if (path == null ) {
             return null;
         }
         if (path.equals(".")) { // $NON-NLS-1$
             return System.getProperty("user.dir"); // $NON-NLS-1$
         }
         path = path.trim().replace('\\', '/'); // $NON-NLS-1$ // $NON-NLS-2$
         path = JOrphanUtils.substitute(path, "//", "/"); // $NON-NLS-1$// $NON-NLS-2$
 
         while (path.endsWith("/")) { // $NON-NLS-1$
             path = path.substring(0, path.length() - 1);
         }
         return path;
     }
 
     /*
      * NOTUSED * Determine if the class implements the interface.
      *
      * @param theClass
      *            the class to check
      * @param theInterface
      *            the interface to look for
      * @return boolean true if it implements
      *
      * private static boolean classImplementsInterface( Class theClass, Class
      * theInterface) { HashMap mapInterfaces = new HashMap(); String strKey =
      * null; // pass in the map by reference since the method is recursive
      * getAllInterfaces(theClass, mapInterfaces); Iterator iterInterfaces =
      * mapInterfaces.keySet().iterator(); while (iterInterfaces.hasNext()) {
      * strKey = (String) iterInterfaces.next(); if (mapInterfaces.get(strKey) ==
      * theInterface) { return true; } } return false; }
      */
 
     /*
      * Finds all classes that extend the classes in the listSuperClasses
      * ArrayList, searching in the listAllClasses ArrayList.
      *
      * @param superClasses
      *            the base classes to find subclasses for
      * @param listAllClasses
      *            the collection of classes to search in
      * @param innerClasses
      *            indicate whether to include inner classes in the search
      * @return ArrayList of the subclasses
      */
 //  private static Set findAllSubclasses(Class []superClasses, Set listAllClasses, boolean innerClasses) {
 //      Set listSubClasses = new TreeSet();
 //      for (int i=0; i< superClasses.length; i++) {
 //          findAllSubclassesOneClass(superClasses[i], listAllClasses, listSubClasses, innerClasses);
 //      }
 //      return listSubClasses;
 //  }
 
     /*
      * Finds all classes that extend the class, searching in the listAllClasses
      * ArrayList.
      *
      * @param theClass
      *            the parent class
      * @param listAllClasses
      *            the collection of classes to search in
      * @param listSubClasses
      *            the collection of discovered subclasses
      * @param innerClasses
      *            indicates whether inners classes should be included in the
      *            search
      */
 //  private static void findAllSubclassesOneClass(Class theClass, Set listAllClasses, Set listSubClasses,
 //          boolean innerClasses) {
 //        Iterator iterClasses = listAllClasses.iterator();
 //      while (iterClasses.hasNext()) {
 //            String strClassName = (String) iterClasses.next();
 //          // only check classes if they are not inner classes
 //          // or we intend to check for inner classes
 //          if ((strClassName.indexOf("$") == -1) || innerClasses) { // $NON-NLS-1$
 //              // might throw an exception, assume this is ignorable
 //              try {
 //                  Class c = Class.forName(strClassName, false, Thread.currentThread().getContextClassLoader());
 //
 //                  if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
 //                        if(theClass.isAssignableFrom(c)){
 //                            listSubClasses.add(strClassName);
 //                        }
 //                    }
 //              } catch (Throwable ignored) {
 //                    log.debug(ignored.getLocalizedMessage());
 //              }
 //          }
 //      }
 //  }
 
     /**
      *
      * @param parentClasses list of classes to check for
      * @param strClassName name of class to be checked
      * @param innerClasses should we allow inner classes?
      * @param contextClassLoader the classloader to use
      * @return true if the class is a non-abstract, non-interface instance of at least one of the parent classes
      */
     private static boolean isChildOf(Class<?> [] parentClasses, String strClassName,
             ClassLoader contextClassLoader){
             // might throw an exception, assume this is ignorable
             try {
                 Class<?> c = Class.forName(strClassName, false, contextClassLoader);
 
                 if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                     for (int i=0; i< parentClasses.length; i++) {
                         if(parentClasses[i].isAssignableFrom(c)){
                             return true;
                         }
                     }
                 }
             } catch (UnsupportedClassVersionError ignored) {
                 log.debug(ignored.getLocalizedMessage());
             } catch (NoClassDefFoundError ignored) {
                 log.debug(ignored.getLocalizedMessage());
             } catch (ClassNotFoundException ignored) {
                 log.debug(ignored.getLocalizedMessage());
             }
         return false;
     }
 
     private static boolean hasAnnotationOnMethod(Class<? extends Annotation>[] annotations, String classInQuestion,
         ClassLoader contextClassLoader ){
         try{
             Class<?> c = Class.forName(classInQuestion, false, contextClassLoader);
             for(Method method : c.getMethods()) {
                 for(Class<? extends Annotation> annotation : annotations) {
                     if(method.isAnnotationPresent(annotation)) {
                         return true;
                     }
                 }
             }
         } catch (NoClassDefFoundError ignored) {
             log.debug(ignored.getLocalizedMessage());
         } catch (ClassNotFoundException ignored) {
             log.debug(ignored.getLocalizedMessage());
         }
         return false;
     }
 
 
     /*
      * Converts a class file from the text stored in a Jar file to a version
      * that can be used in Class.forName().
      *
      * @param strClassName
      *            the class name from a Jar file
      * @return String the Java-style dotted version of the name
      */
     private static String fixClassName(String strClassName) {
         strClassName = strClassName.replace('\\', '.'); // $NON-NLS-1$ // $NON-NLS-2$
         strClassName = strClassName.replace('/', '.'); // $NON-NLS-1$ // $NON-NLS-2$
         // remove ".class"
         strClassName = strClassName.substring(0, strClassName.length() - DOT_CLASS_LEN);
         return strClassName;
     }
 
     private static void findClassesInOnePath(String strPath, Set<String> listClasses) throws IOException {
         File file = new File(strPath);
         if (file.isDirectory()) {
             findClassesInPathsDir(strPath, file, listClasses);
         } else if (file.exists()) {
             ZipFile zipFile = null;
             try {
                 zipFile = new ZipFile(file);
                 Enumeration<? extends ZipEntry> entries = zipFile.entries();
                 while (entries.hasMoreElements()) {
                     String strEntry = entries.nextElement().toString();
                     if (strEntry.endsWith(DOT_CLASS)) {
                         listClasses.add(fixClassName(strEntry));
                     }
                 }
             } catch (IOException e) {
                 log.warn("Can not open the jar " + strPath + " " + e.getLocalizedMessage(),e);
             }
             finally {
                 if(zipFile != null) {
                     try {zipFile.close();} catch (Exception e) {}
                 }
             }
         }
     }
 
     private static void findClassesInPaths(List<String> listPaths, Set<String> listClasses) throws IOException {
         for (String path : listPaths) {
             findClassesInOnePath(path, listClasses);
         }
     }
 
     private static void findClassesInPathsDir(String strPathElement, File dir, Set<String> listClasses) throws IOException {
         String[] list = dir.list();
         for (int i = 0; i < list.length; i++) {
             File file = new File(dir, list[i]);
             if (file.isDirectory()) {
                 // Recursive call
                 findClassesInPathsDir(strPathElement, file, listClasses);
             } else if (list[i].endsWith(DOT_CLASS) && file.exists() && (file.length() != 0)) {
                 final String path = file.getPath();
                 listClasses.add(path.substring(strPathElement.length() + 1,
                         path.lastIndexOf('.')) // $NON-NLS-1$
                         .replace(File.separator.charAt(0), '.')); // $NON-NLS-1$
             }
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index 4f173d40f..2e6d39916 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,762 +1,779 @@
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
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
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
     
     private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
 
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
 
+    /**
+     * Constructor which is setup to show HTTP implementation, raw body pane and
+     * sampler fields.
+     */
     public UrlConfigGui() {
         this(true);
     }
 
     /**
+     * Constructor which is setup to show HTTP implementation and raw body pane.
+     *
      * @param showSamplerFields
+     *            flag whether sampler fields should be shown.
      */
     public UrlConfigGui(boolean showSamplerFields) {
         this(showSamplerFields, true, true);
     }
 
     /**
      * @param showSamplerFields
-     * @param showImplementation Show HTTP Implementation
-     * @param showRawBodyPane 
+     *            flag whether sampler fields should be shown
+     * @param showImplementation
+     *            Show HTTP Implementation
+     * @param showRawBodyPane
+     *            flag whether the raw body pane should be shown
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
-     * @param element
+     * @param element {@link TestElement} to modify
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
     private static final String computePostBody(Arguments arguments) {
         return computePostBody(arguments, false);
     }
 
     /**
      * Compute body data from arguments
      * @param arguments {@link Arguments}
      * @param crlfToLF whether to convert CRLF to LF
      * @return {@link String}
      */
     private static final String computePostBody(Arguments arguments, boolean crlfToLF) {
         StringBuilder postBody = new StringBuilder();
         PropertyIterator args = arguments.iterator();
         while (args.hasNext()) {
             HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
             String value = arg.getValue();
             if (crlfToLF) {
                 value=value.replaceAll("\r\n", "\n"); // See modifyTestElement
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
+        
         /**
          * Apply some check rules if check is true
+         *
+         * @param index
+         *            index to select
+         * @param check
+         *            flag whether to perform checks before setting the selected
+         *            index
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
         if (e.getSource() == followRedirects){
             if (followRedirects.isSelected()) {
                 autoRedirects.setSelected(false);
             }
         }
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
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
index 37f1bda91..2cea3265a 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
@@ -1,465 +1,473 @@
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
 
 // For unit tests @see TestCookieManager
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.Serializable;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Date;
 import java.util.Map;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.URIException;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.http.HttpResponse;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.impl.cookie.DateParseException;
 import org.apache.http.impl.cookie.DateUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Handles HTTP Caching
  */
 public class CacheManager extends ConfigTestElement implements TestStateListener, TestIterationListener, Serializable {
 
     private static final Date EXPIRED_DATE = new Date(0L);
 
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String[] CACHEABLE_METHODS = JMeterUtils.getPropDefault("cacheable_methods", "GET").split("[ ,]");
 
     static {
         log.info("Will only cache the following methods: "+Arrays.toString(CACHEABLE_METHODS));
     }
 
     //+ JMX attributes, do not change values
     public static final String CLEAR = "clearEachIteration"; // $NON-NLS-1$
     public static final String USE_EXPIRES = "useExpires"; // $NON-NLS-1$
     public static final String MAX_SIZE = "maxSize";  // $NON-NLS-1$
     //-
 
     private transient InheritableThreadLocal<Map<String, CacheEntry>> threadCache;
 
     private transient boolean useExpires; // Cached value
 
     private static final int DEFAULT_MAX_SIZE = 5000;
 
     private static final long ONE_YEAR_MS = 365*24*60*60*1000L;
 
     public CacheManager() {
         setProperty(new BooleanProperty(CLEAR, false));
         setProperty(new BooleanProperty(USE_EXPIRES, false));
         clearCache();
         useExpires = false;
     }
 
     /*
      * Holder for storing cache details.
      * Perhaps add original response later?
      */
     // package-protected to allow access by unit-test cases
     static class CacheEntry{
         private final String lastModified;
         private final String etag;
         private final Date expires;
         public CacheEntry(String lastModified, Date expires, String etag){
            this.lastModified = lastModified;
            this.etag = etag;
            this.expires = expires;
        }
         public String getLastModified() {
             return lastModified;
         }
         public String getEtag() {
             return etag;
         }
         @Override
         public String toString(){
             return lastModified+" "+etag;
         }
         public Date getExpires() {
             return expires;
         }
     }
 
     /**
      * Save the Last-Modified, Etag, and Expires headers if the result is cacheable.
      * Version for Java implementation.
      * @param conn connection
      * @param res result
      */
     public void saveDetails(URLConnection conn, HTTPSampleResult res){
         if (isCacheable(res)){
             String lastModified = conn.getHeaderField(HTTPConstants.LAST_MODIFIED);
             String expires = conn.getHeaderField(HTTPConstants.EXPIRES);
             String etag = conn.getHeaderField(HTTPConstants.ETAG);
             String url = conn.getURL().toString();
             String cacheControl = conn.getHeaderField(HTTPConstants.CACHE_CONTROL);
             String date = conn.getHeaderField(HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, url, date);
         }
     }
 
     /**
-     * Save the Last-Modified, Etag, and Expires headers if the result is cacheable.
-     * Version for Commons HttpClient implementation.
+     * Save the Last-Modified, Etag, and Expires headers if the result is
+     * cacheable. Version for Commons HttpClient implementation.
+     *
      * @param method
-     * @param res result
+     *            {@link HttpMethod} to get header information from
+     * @param res
+     *            result to decide if result is cacheable
+     * @throws URIException
+     *             if extraction of the the uri from <code>method</code> fails
      */
     public void saveDetails(HttpMethod method, HTTPSampleResult res) throws URIException{
         if (isCacheable(res)){
             String lastModified = getHeader(method ,HTTPConstants.LAST_MODIFIED);
             String expires = getHeader(method ,HTTPConstants.EXPIRES);
             String etag = getHeader(method ,HTTPConstants.ETAG);
             String url = method.getURI().toString();
             String cacheControl = getHeader(method, HTTPConstants.CACHE_CONTROL);
             String date = getHeader(method, HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, url, date);
         }
     }
 
     /**
-     * Save the Last-Modified, Etag, and Expires headers if the result is cacheable.
-     * Version for Apache HttpClient implementation.
+     * Save the Last-Modified, Etag, and Expires headers if the result is
+     * cacheable. Version for Apache HttpClient implementation.
+     *
      * @param method
-     * @param res result
+     *            {@link HttpResponse} to extract header information from
+     * @param res
+     *            result to decide if result is cacheable
      */
     public void saveDetails(HttpResponse method, HTTPSampleResult res) {
         if (isCacheable(res)){
             String lastModified = getHeader(method ,HTTPConstants.LAST_MODIFIED);
             String expires = getHeader(method ,HTTPConstants.EXPIRES);
             String etag = getHeader(method ,HTTPConstants.ETAG);
             String cacheControl = getHeader(method, HTTPConstants.CACHE_CONTROL);
             String date = getHeader(method, HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, res.getUrlAsString(), date); // TODO correct URL?
         }
     }
 
     // helper method to save the cache entry
     private void setCache(String lastModified, String cacheControl, String expires, String etag, String url, String date) {
         if (log.isDebugEnabled()){
             log.debug("setCache("
                   + lastModified + "," 
                   + cacheControl + ","
                   + expires + "," 
                   + etag + ","
                   + url + ","
                   + date
                   + ")");
         }
         Date expiresDate = null; // i.e. not using Expires
         if (useExpires) {// Check that we are processing Expires/CacheControl
             final String MAX_AGE = "max-age=";
             
             if(cacheControl != null && cacheControl.contains("no-store")) {
                 // We must not store an CacheEntry, otherwise a 
                 // conditional request may be made
                 return;
             }
             if (expires != null) {
                 try {
                     expiresDate = DateUtils.parseDate(expires);
                 } catch (org.apache.http.impl.cookie.DateParseException e) {
                     if (log.isDebugEnabled()){
                         log.debug("Unable to parse Expires: '"+expires+"' "+e);
                     }
                     expiresDate = CacheManager.EXPIRED_DATE; // invalid dates must be treated as expired
                 }
             }
             // if no-cache is present, ensure that expiresDate remains null, which forces revalidation
             if(cacheControl != null && !cacheControl.contains("no-cache")) {    
                 // the max-age directive overrides the Expires header,
                 if(cacheControl.contains(MAX_AGE)) {
                     long maxAgeInSecs = Long.parseLong(
                             cacheControl.substring(cacheControl.indexOf(MAX_AGE)+MAX_AGE.length())
                                 .split("[, ]")[0] // Bug 51932 - allow for optional trailing attributes
                             );
                     expiresDate=new Date(System.currentTimeMillis()+maxAgeInSecs*1000);
 
                 } else if(expires==null) { // No max-age && No expires
                     if(!StringUtils.isEmpty(lastModified) && !StringUtils.isEmpty(date)) {
                         try {
                             Date responseDate = DateUtils.parseDate( date );
                             Date lastModifiedAsDate = DateUtils.parseDate( lastModified );
                             // see https://developer.mozilla.org/en/HTTP_Caching_FAQ
                             // see http://www.ietf.org/rfc/rfc2616.txt#13.2.4 
                             expiresDate=new Date(System.currentTimeMillis()
                                     +Math.round((responseDate.getTime()-lastModifiedAsDate.getTime())*0.1));
                         } catch(DateParseException e) {
                             // date or lastModified may be null or in bad format
                             if(log.isWarnEnabled()) {
                                 log.warn("Failed computing expiration date with following info:"
                                     +lastModified + "," 
                                     + cacheControl + ","
                                     + expires + "," 
                                     + etag + ","
                                     + url + ","
                                     + date);
                             }
                             // TODO Can't see anything in SPEC
                             expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);                      
                         }
                     } else {
                         // TODO Can't see anything in SPEC
                         expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);                      
                     }
                 }  
                 // else expiresDate computed in (expires!=null) condition is used
             }
         }
         getCache().put(url, new CacheEntry(lastModified, expiresDate, etag));
     }
 
     // Helper method to deal with missing headers - Commons HttpClient
     private String getHeader(HttpMethod method, String name){
         org.apache.commons.httpclient.Header hdr = method.getResponseHeader(name);
         return hdr != null ? hdr.getValue() : null;
     }
 
     // Apache HttpClient
     private String getHeader(HttpResponse method, String name) {
         org.apache.http.Header hdr = method.getLastHeader(name);
         return hdr != null ? hdr.getValue() : null;
     }
 
     /*
      * Is the sample result OK to cache?
      * i.e is it in the 2xx range, and is it a cacheable method?
      */
     private boolean isCacheable(HTTPSampleResult res){
         final String responseCode = res.getResponseCode();
         return isCacheableMethod(res)
             && "200".compareTo(responseCode) <= 0  // $NON-NLS-1$
             && "299".compareTo(responseCode) >= 0;  // $NON-NLS-1$
     }
 
     private boolean isCacheableMethod(HTTPSampleResult res) {
         final String resMethod = res.getHTTPMethod();
         for(String method : CACHEABLE_METHODS) {
             if (method.equalsIgnoreCase(resMethod)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:
      * <ul>
      * <li>If-Modified-Since</li>
      * <li>If-None-Match</li>
      * </ul>
      * Commons HttpClient version
      * @param url URL to look up in cache
      * @param method where to set the headers
      */
     public void setHeaders(URL url, HttpMethod method) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(method.getName()+"(OACH) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 method.setRequestHeader(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 method.setRequestHeader(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:
      * <ul>
      * <li>If-Modified-Since</li>
      * <li>If-None-Match</li>
      * </ul>
      * Apache HttpClient version.
      * @param url {@link URL} to look up in cache
      * @param request where to set the headers
      */
     public void setHeaders(URL url, HttpRequestBase request) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(request.getMethod()+"(OAH) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 request.setHeader(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 request.setHeader(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:
      * <ul>
      * <li>If-Modified-Since</li>
      * <li>If-None-Match</li>
      * </ul>
      * @param url {@link URL} to look up in cache
      * @param conn where to set the headers
      */
     public void setHeaders(HttpURLConnection conn, URL url) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(conn.getRequestMethod()+"(Java) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 conn.addRequestProperty(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 conn.addRequestProperty(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, if the entry has an expires header and the entry has not expired, return true<br>
      * @param url {@link URL} to look up in cache
      * @return <code>true</code> if entry has an expires header and the entry has not expired, else <code>false</code>
      */
     public boolean inCache(URL url) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug("inCache "+url.toString()+" "+entry);
         }
         if (entry != null){
             final Date expiresDate = entry.getExpires();
             if (expiresDate != null) {
                 if (expiresDate.after(new Date())) {
                     if (log.isDebugEnabled()){
                         log.debug("Expires= " + expiresDate + " (Valid)");
                     }
                     return true;
                 } else {
                     if (log.isDebugEnabled()){
                         log.debug("Expires= " + expiresDate + " (Expired)");
                     }
                 }
             }
         }
         return false;
     }
 
     private Map<String, CacheEntry> getCache(){
         return threadCache.get();
     }
 
     public boolean getClearEachIteration() {
         return getPropertyAsBoolean(CLEAR);
     }
 
     public void setClearEachIteration(boolean clear) {
         setProperty(new BooleanProperty(CLEAR, clear));
     }
 
     public boolean getUseExpires() {
         return getPropertyAsBoolean(USE_EXPIRES);
     }
 
     public void setUseExpires(boolean expires) {
         setProperty(new BooleanProperty(USE_EXPIRES, expires));
     }
     
     /**
      * @return int cache max size
      */
     public int getMaxSize() {
         return getPropertyAsInt(MAX_SIZE, DEFAULT_MAX_SIZE);
     }
 
     /**
      * @param size int cache max size
      */
     public void setMaxSize(int size) {
         setProperty(MAX_SIZE, size, DEFAULT_MAX_SIZE);
     }
     
 
     @Override
     public void clear(){
         super.clear();
         clearCache();
     }
 
     private void clearCache() {
         log.debug("Clear cache");
         threadCache = new InheritableThreadLocal<Map<String, CacheEntry>>(){
             @Override
             protected Map<String, CacheEntry> initialValue(){
                 // Bug 51942 - this map may be used from multiple threads
                 @SuppressWarnings("unchecked") // LRUMap is not generic currently
                 Map<String, CacheEntry> map = new LRUMap(getMaxSize());
                 return Collections.<String, CacheEntry>synchronizedMap(map);
             }
         };
     }
 
     @Override
     public void testStarted() {
     }
 
     @Override
     public void testEnded() {
     }
 
     @Override
     public void testStarted(String host) {
     }
 
     @Override
     public void testEnded(String host) {
     }
 
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         if (getClearEachIteration()) {
             clearCache();
         }
         useExpires=getUseExpires(); // cache the value
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieHandler.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieHandler.java
index be34234a0..fbf7dc46b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieHandler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieHandler.java
@@ -1,51 +1,52 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.net.URL;
 
 import org.apache.jmeter.testelement.property.CollectionProperty;
 
 /**
  * Interface to be implemented by CookieHandler
  */
 public interface CookieHandler {
 
     /**
      * Add cookie to CookieManager from cookieHeader and URL
      * @param cookieManager CookieManager on which cookies are added
      * @param checkCookies boolean to indicate if cookies must be validated against spec
      * @param cookieHeader String cookie Header
      * @param url URL 
      */
     void addCookieFromHeader(CookieManager cookieManager, boolean checkCookies,
             String cookieHeader, URL url);
 
     /**
      * Find cookies applicable to the given URL and build the Cookie header from
      * them.
      * @param cookiesCP {@link CollectionProperty} of {@link Cookie}
      * @param url
      *            URL of the request to which the returned header will be added.
+     * @param allowVariableCookie flag whether to allow jmeter variables in cookie values
      * @return the value string for the cookie header (goes after "Cookie: ").
      */
     String getCookieHeaderForURL(CollectionProperty cookiesCP, URL url,
             boolean allowVariableCookie);
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java
index df8c79a20..d9d668625 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/CookieManager.java
@@ -1,417 +1,439 @@
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
 
 // For unit tests @see TestCookieManager
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.Serializable;
 import java.net.URL;
 import java.util.ArrayList;
 
 import org.apache.http.client.params.CookiePolicy;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassTools;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * This class provides an interface to the netscape cookies file to pass cookies
  * along with a request.
  *
  * Now uses Commons HttpClient parsing and matching code (since 2.1.2)
  *
  */
 public class CookieManager extends ConfigTestElement implements TestStateListener, TestIterationListener, Serializable {
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //++ JMX tag values
     private static final String CLEAR = "CookieManager.clearEachIteration";// $NON-NLS-1$
 
     private static final String COOKIES = "CookieManager.cookies";// $NON-NLS-1$
 
     private static final String POLICY = "CookieManager.policy"; //$NON-NLS-1$
     
     private static final String IMPLEMENTATION = "CookieManager.implementation"; //$NON-NLS-1$
     //-- JMX tag values
 
     private static final String TAB = "\t"; //$NON-NLS-1$
 
     // See bug 33796
     private static final boolean DELETE_NULL_COOKIES =
         JMeterUtils.getPropDefault("CookieManager.delete_null_cookies", true);// $NON-NLS-1$
 
     // See bug 28715
     // Package protected for tests
     static final boolean ALLOW_VARIABLE_COOKIES
         = JMeterUtils.getPropDefault("CookieManager.allow_variable_cookies", true);// $NON-NLS-1$
 
     private static final String COOKIE_NAME_PREFIX =
         JMeterUtils.getPropDefault("CookieManager.name.prefix", "COOKIE_").trim();// $NON-NLS-1$ $NON-NLS-2$
 
     private static final boolean SAVE_COOKIES =
         JMeterUtils.getPropDefault("CookieManager.save.cookies", false);// $NON-NLS-1$
 
     private static final boolean CHECK_COOKIES =
         JMeterUtils.getPropDefault("CookieManager.check.cookies", true);// $NON-NLS-1$
 
     static {
         log.info("Settings:"
                 + " Delete null: " + DELETE_NULL_COOKIES
                 + " Check: " + CHECK_COOKIES
                 + " Allow variable: " + ALLOW_VARIABLE_COOKIES
                 + " Save: " + SAVE_COOKIES
                 + " Prefix: " + COOKIE_NAME_PREFIX
                 );
     }
     private transient CookieHandler cookieHandler;
 
     private transient CollectionProperty initialCookies;
 
     public static final String DEFAULT_POLICY = CookiePolicy.BROWSER_COMPATIBILITY;
     
     public static final String DEFAULT_IMPLEMENTATION = HC3CookieHandler.class.getName();
 
     public CookieManager() {
         clearCookies(); // Ensure that there is always a collection available
     }
 
     // ensure that the initial cookies are copied to the per-thread instances
     /** {@inheritDoc} */
     @Override
     public Object clone(){
         CookieManager clone = (CookieManager) super.clone();
         clone.initialCookies = initialCookies;
         clone.cookieHandler = cookieHandler;
         return clone;
     }
 
     public String getPolicy() {
         return getPropertyAsString(POLICY, DEFAULT_POLICY);
     }
 
     public void setCookiePolicy(String policy){
         setProperty(POLICY, policy, DEFAULT_POLICY);
     }
 
     public CollectionProperty getCookies() {
         return (CollectionProperty) getProperty(COOKIES);
     }
 
     public int getCookieCount() {// Used by GUI
         return getCookies().size();
     }
 
     public boolean getClearEachIteration() {
         return getPropertyAsBoolean(CLEAR);
     }
 
     public void setClearEachIteration(boolean clear) {
         setProperty(new BooleanProperty(CLEAR, clear));
     }
 
     public String getImplementation() {
         return getPropertyAsString(IMPLEMENTATION, DEFAULT_IMPLEMENTATION);
     }
 
     public void setImplementation(String implementation){
         setProperty(IMPLEMENTATION, implementation, DEFAULT_IMPLEMENTATION);
     }
 
     /**
      * Save the static cookie data to a file.
+     * <p>
      * Cookies are only taken from the GUI - runtime cookies are not included.
+     *
+     * @param authFile
+     *            name of the file to store the cookies into. If the name is
+     *            relative, the system property <code>user.dir</code> will be
+     *            prepended
+     * @throws IOException
+     *             when writing to that file fails
      */
     public void save(String authFile) throws IOException {
         File file = new File(authFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir") // $NON-NLS-1$
                     + File.separator + authFile);
         }
         PrintWriter writer = new PrintWriter(new FileWriter(file)); // TODO Charset ?
         writer.println("# JMeter generated Cookie file");// $NON-NLS-1$
         PropertyIterator cookies = getCookies().iterator();
         long now = System.currentTimeMillis();
         while (cookies.hasNext()) {
             Cookie cook = (Cookie) cookies.next().getObjectValue();
             final long expiresMillis = cook.getExpiresMillis();
             if (expiresMillis == 0 || expiresMillis > now) { // only save unexpired cookies
                 writer.println(cookieToString(cook));
             }
         }
         writer.flush();
         writer.close();
     }
 
     /**
      * Add cookie data from a file.
+     *
+     * @param cookieFile
+     *            name of the file to read the cookies from. If the name is
+     *            relative, the system property <code>user.dir</code> will be
+     *            prepended
+     * @throws IOException
+     *             if reading the file fails
      */
     public void addFile(String cookieFile) throws IOException {
         File file = new File(cookieFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir") // $NON-NLS-1$
                     + File.separator + cookieFile);
         }
         BufferedReader reader = null;
         if (file.canRead()) {
             reader = new BufferedReader(new FileReader(file)); // TODO Charset ?
         } else {
             throw new IOException("The file you specified cannot be read.");
         }
 
         // N.B. this must agree with the save() and cookieToString() methods
         String line;
         try {
             final CollectionProperty cookies = getCookies();
             while ((line = reader.readLine()) != null) {
                 try {
                     if (line.startsWith("#") || JOrphanUtils.isBlank(line)) {//$NON-NLS-1$
                         continue;
                     }
                     String[] st = JOrphanUtils.split(line, TAB, false);
 
                     final int _domain = 0;
                     //final int _ignored = 1;
                     final int _path = 2;
                     final int _secure = 3;
                     final int _expires = 4;
                     final int _name = 5;
                     final int _value = 6;
                     final int _fields = 7;
                     if (st.length!=_fields) {
                         throw new IOException("Expected "+_fields+" fields, found "+st.length+" in "+line);
                     }
 
                     if (st[_path].length()==0) {
                         st[_path] = "/"; //$NON-NLS-1$
                     }
                     boolean secure = Boolean.parseBoolean(st[_secure]);
                     long expires = Long.parseLong(st[_expires]);
                     if (expires==Long.MAX_VALUE) {
                         expires=0;
                     }
                     //long max was used to represent a non-expiring cookie, but that caused problems
                     Cookie cookie = new Cookie(st[_name], st[_value], st[_domain], st[_path], secure, expires);
                     cookies.addItem(cookie);
                 } catch (NumberFormatException e) {
                     throw new IOException("Error parsing cookie line\n\t'" + line + "'\n\t" + e);
                 }
             }
         } finally {
             reader.close();
          }
     }
 
     private String cookieToString(Cookie c){
         StringBuilder sb=new StringBuilder(80);
         sb.append(c.getDomain());
         //flag - if all machines within a given domain can access the variable.
         //(from http://www.cookiecentral.com/faq/ 3.5)
         sb.append(TAB).append("TRUE");
         sb.append(TAB).append(c.getPath());
         sb.append(TAB).append(JOrphanUtils.booleanToSTRING(c.getSecure()));
         sb.append(TAB).append(c.getExpires());
         sb.append(TAB).append(c.getName());
         sb.append(TAB).append(c.getValue());
         return sb.toString();
     }
 
     /** {@inheritDoc} */
     @Override
     public void recoverRunningVersion() {
         // do nothing, the cookie manager has to accept changes.
     }
 
     /** {@inheritDoc} */
     @Override
     public void setRunningVersion(boolean running) {
         // do nothing, the cookie manager has to accept changes.
     }
 
     /**
      * Add a cookie.
+     *
+     * @param c cookie to be added
      */
     public void add(Cookie c) {
         String cv = c.getValue();
         String cn = c.getName();
         removeMatchingCookies(c); // Can't have two matching cookies
 
         if (DELETE_NULL_COOKIES && (null == cv || cv.length()==0)) {
             if (log.isDebugEnabled()) {
                 log.debug("Dropping cookie with null value " + c.toString());
             }
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Add cookie to store " + c.toString());
             }
             getCookies().addItem(c);
             if (SAVE_COOKIES)  {
                 JMeterContext context = getThreadContext();
                 if (context.isSamplingStarted()) {
                     context.getVariables().put(COOKIE_NAME_PREFIX+cn, cv);
                 }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void clear(){
         super.clear();
         clearCookies(); // ensure data is set up OK initially
     }
 
     /*
      * Remove all the cookies.
      */
     private void clearCookies() {
         log.debug("Clear all cookies from store");
         setProperty(new CollectionProperty(COOKIES, new ArrayList<Object>()));
     }
 
     /**
      * Remove a cookie.
+     *
+     * @param index index of the cookie to remove
      */
     public void remove(int index) {// TODO not used by GUI
         getCookies().remove(index);
     }
 
     /**
      * Return the cookie at index i.
+     *
+     * @param i index of the cookie to get
+     * @return cookie at index <code>i</code>
      */
     public Cookie get(int i) {// Only used by GUI
         return (Cookie) getCookies().get(i).getObjectValue();
     }
 
     /**
      * Find cookies applicable to the given URL and build the Cookie header from
      * them.
      *
      * @param url
      *            URL of the request to which the returned header will be added.
      * @return the value string for the cookie header (goes after "Cookie: ").
      */
     public String getCookieHeaderForURL(URL url) {
         return cookieHandler.getCookieHeaderForURL(getCookies(), url, ALLOW_VARIABLE_COOKIES);
     }
 
 
     public void addCookieFromHeader(String cookieHeader, URL url){
         cookieHandler.addCookieFromHeader(this, CHECK_COOKIES, cookieHeader, url);
     }
     /**
      * Check if cookies match, i.e. name, path and domain are equal.
      * <br/>
      * TODO - should we compare secure too?
      * @param a
      * @param b
      * @return true if cookies match
      */
     private boolean match(Cookie a, Cookie b){
         return
         a.getName().equals(b.getName())
         &&
         a.getPath().equals(b.getPath())
         &&
         a.getDomain().equals(b.getDomain());
     }
 
     void removeMatchingCookies(Cookie newCookie){
         // Scan for any matching cookies
         PropertyIterator iter = getCookies().iterator();
         while (iter.hasNext()) {
             Cookie cookie = (Cookie) iter.next().getObjectValue();
             if (cookie == null) {// TODO is this possible?
                 continue;
             }
             if (match(cookie,newCookie)) {
                 if (log.isDebugEnabled()) {
                     log.debug("New Cookie = " + newCookie.toString()
                               + " removing matching Cookie " + cookie.toString());
                 }
                 iter.remove();
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted() {
         initialCookies = getCookies();
         try {
             cookieHandler = (CookieHandler) ClassTools.construct(getImplementation(), getPolicy());
         } catch (JMeterException e) {
             log.error("Unable to load or invoke class: " + getImplementation(), e);
         }
         if (log.isDebugEnabled()){
             log.debug("Policy: "+getPolicy()+" Clear: "+getClearEachIteration());
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded() {
     }
 
     /** {@inheritDoc} */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     /** {@inheritDoc} */
     @Override
     public void testEnded(String host) {
     }
 
     /** {@inheritDoc} */
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         if (getClearEachIteration()) {
             log.debug("Initialise cookies from pre-defined list");
             // No need to call clear
             setProperty(initialCookies.clone());
         }
     }
 
     /**
      * Package protected for tests
      * @return the cookieHandler
      */
     CookieHandler getCookieHandler() {
         return cookieHandler;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java
index dec27de83..c3862b2d6 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/DNSCacheManager.java
@@ -1,235 +1,237 @@
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
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations under
  * the License.
  */
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.Serializable;
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.LinkedHashMap;
 import java.util.Map;
 
 import org.apache.http.conn.DnsResolver;
 import org.apache.http.impl.conn.SystemDefaultDnsResolver;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.xbill.DNS.ARecord;
 import org.xbill.DNS.Cache;
 import org.xbill.DNS.ExtendedResolver;
 import org.xbill.DNS.Lookup;
 import org.xbill.DNS.Record;
 import org.xbill.DNS.Resolver;
 import org.xbill.DNS.SimpleResolver;
 import org.xbill.DNS.TextParseException;
 import org.xbill.DNS.Type;
 
 /**
  * This config element provides ability to have flexible control over DNS
  * caching function. Depending on option from @see
  * {@link org.apache.jmeter.protocol.http.gui.DNSCachePanel}, either system or
  * custom resolver can be used. Custom resolver uses dnsjava library, and gives
  * ability to bypass both OS and JVM cache. It allows to use paradigm
  * "1 virtual user - 1 DNS cache" in performance tests.
  *
  * @since 2.12
  */
 
 public class DNSCacheManager extends ConfigTestElement implements TestIterationListener, Serializable, DnsResolver {
     private static final long serialVersionUID = 2120L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private transient SystemDefaultDnsResolver systemDefaultDnsResolver = null;
 
     private Map<String, InetAddress[]> cache = null;
 
     private transient Resolver resolver = null;
 
     //++ JMX tag values
     public static final String CLEAR_CACHE_EACH_ITER = "DNSCacheManager.clearEachIteration"; // $NON-NLS-1$
 
     public static final String SERVERS = "DNSCacheManager.servers"; // $NON-NLS-1$
 
     public static final String IS_CUSTOM_RESOLVER = "DNSCacheManager.isCustomResolver"; // $NON-NLS-1$
     //-- JMX tag values
 
     public static final boolean DEFAULT_CLEAR_CACHE_EACH_ITER = false;
 
     public static final String DEFAULT_SERVERS = ""; // $NON-NLS-1$
 
     public static final boolean DEFAULT_IS_CUSTOM_RESOLVER = false;
 
     private final transient Cache lookupCache;
 
     // ensure that the initial DNSServers are copied to the per-thread instances
 
     public DNSCacheManager() {
         setProperty(new CollectionProperty(SERVERS, new ArrayList<String>()));
         //disabling cache
         lookupCache = new Cache();
         lookupCache.setMaxCache(0);
         lookupCache.setMaxEntries(0);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         DNSCacheManager clone = (DNSCacheManager) super.clone();
         clone.systemDefaultDnsResolver = new SystemDefaultDnsResolver();
         clone.cache = new LinkedHashMap<String, InetAddress[]>();
         CollectionProperty dnsServers = getServers();
         try {
             clone.resolver = new ExtendedResolver();
             PropertyIterator dnsServIt = dnsServers.iterator();
             while (dnsServIt.hasNext()) {
                 String dnsServer = dnsServIt.next().getStringValue();
                 ((ExtendedResolver) clone.resolver).addResolver(new SimpleResolver(dnsServer));
             }
             // resolvers will be chosen via round-robin
             ((ExtendedResolver) clone.resolver).setLoadBalance(true);
         } catch (UnknownHostException uhe) {
             log.warn("Failed to create Extended resolver: " + uhe.getMessage());
         }
         return clone;
     }
 
     /**
      *
      * Resolves address using system or custom DNS resolver
      */
     @Override
     public InetAddress[] resolve(String host) throws UnknownHostException {
         if (cache.containsKey(host)) {
             if (log.isDebugEnabled()) {
                 log.debug("Cache hit thr#" + JMeterContextService.getContext().getThreadNum() + ": " + host + "=>"
                         + Arrays.toString(cache.get(host)));
             }
             return cache.get(host);
         } else {
             InetAddress[] addresses = requestLookup(host);
             if (log.isDebugEnabled()) {
                 log.debug("Cache miss thr#" + JMeterContextService.getContext().getThreadNum() + ": " + host + "=>"
                         + Arrays.toString(addresses));
             }
             cache.put(host, addresses);
             return addresses;
         }
     }
 
     /**
      * Sends DNS request via system or custom DNS resolver
      */
     private InetAddress[] requestLookup(String host) throws UnknownHostException {
         InetAddress[] addresses = null;
         if (isCustomResolver() && ((ExtendedResolver) resolver).getResolvers().length > 1) {
             try {
                 Lookup lookup = new Lookup(host, Type.A);
                 lookup.setCache(lookupCache);
                 lookup.setResolver(resolver);
                 Record[] records = lookup.run();
                 if (records.length == 0) {
                     throw new UnknownHostException("Failed to resolve host name: " + host);
                 }
                 addresses = new InetAddress[records.length];
                 for (int i = 0; i < records.length; i++) {
                     addresses[i] = ((ARecord) records[i]).getAddress();
                 }
             } catch (TextParseException tpe) {
                 log.debug("Failed to create Lookup object: " + tpe);
             }
         } else {
             addresses = systemDefaultDnsResolver.resolve(host);
             if (log.isDebugEnabled()) {
                 log.debug("Cache miss: " + host + " Thread #" + JMeterContextService.getContext().getThreadNum()
                         + ", resolved with system resolver into " + Arrays.toString(addresses));
             }
         }
         return addresses;
     }
 
     /**
      * {@inheritDoc} Clean DNS cache if appropriate check-box was selected
      */
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         if (isClearEachIteration()) {
             this.cache.clear();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void clear() {
         super.clear();
         clearServers(); // ensure data is set up OK initially
     }
 
     /**
      * Remove all the servers.
      */
     private void clearServers() {
         log.debug("Clear all servers from store");
         setProperty(new CollectionProperty(SERVERS, new ArrayList<String>()));
     }
 
     public void addServer(String dnsServer) {
         getServers().addItem(dnsServer);
     }
 
     public CollectionProperty getServers() {
         return (CollectionProperty) getProperty(SERVERS);
     }
 
     /**
      * Clean DNS cache each iteration
      * 
      * @return boolean
      */
     public boolean isClearEachIteration() {
         return this.getPropertyAsBoolean(CLEAR_CACHE_EACH_ITER, DEFAULT_CLEAR_CACHE_EACH_ITER);
     }
 
     /**
      * Clean DNS cache each iteration
      *
+     * @param clear
+     *            flag whether DNS cache should be cleared on each iteration
      */
     public void setClearEachIteration(boolean clear) {
         setProperty(new BooleanProperty(CLEAR_CACHE_EACH_ITER, clear));
     }
 
     public boolean isCustomResolver() {
         return this.getPropertyAsBoolean(IS_CUSTOM_RESOLVER, DEFAULT_IS_CUSTOM_RESOLVER);
     }
 
     public void setCustomResolver(boolean isCustomResolver) {
         this.setProperty(IS_CUSTOM_RESOLVER, isCustomResolver);
     }
 
 }
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
index 48aa25fab..edb0806f7 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/HC3CookieHandler.java
@@ -1,196 +1,200 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.net.URL;
 import java.util.Date;
 
 import org.apache.commons.httpclient.cookie.CookiePolicy;
 import org.apache.commons.httpclient.cookie.CookieSpec;
 import org.apache.commons.httpclient.cookie.MalformedCookieException;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTPClient 3.1 implementation
  */
 public class HC3CookieHandler implements CookieHandler {
    private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final transient CookieSpec cookieSpec;
 
     /**
-     * 
+     * @param policy
+     *            cookie policy to which to conform (see
+     *            {@link CookiePolicy#getCookieSpec(String)}
      */
     public HC3CookieHandler(String policy) {
         super();
         this.cookieSpec = CookiePolicy.getCookieSpec(policy);
     }
 
     /**
      * Create an HttpClient cookie from a JMeter cookie
      */
     private org.apache.commons.httpclient.Cookie makeCookie(Cookie jmc){
         long exp = jmc.getExpiresMillis();
         org.apache.commons.httpclient.Cookie ret=
             new org.apache.commons.httpclient.Cookie(
                 jmc.getDomain(),
                 jmc.getName(),
                 jmc.getValue(),
                 jmc.getPath(),
                 exp > 0 ? new Date(exp) : null, // use null for no expiry
                 jmc.getSecure()
                );
         ret.setPathAttributeSpecified(jmc.isPathSpecified());
         ret.setDomainAttributeSpecified(jmc.isDomainSpecified());
         ret.setVersion(jmc.getVersion());
         return ret;
     }
     /**
      * Get array of valid HttpClient cookies for the URL
      *
+     * @param cookiesCP cookies to consider
      * @param url the target URL
+     * @param allowVariableCookie flag whether to allow jmeter variables in cookie values
      * @return array of HttpClient cookies
      *
      */
     org.apache.commons.httpclient.Cookie[] getCookiesForUrl(
             CollectionProperty cookiesCP,
             URL url, 
             boolean allowVariableCookie){
         org.apache.commons.httpclient.Cookie cookies[]=
             new org.apache.commons.httpclient.Cookie[cookiesCP.size()];
         int i=0;
         for (PropertyIterator iter = cookiesCP.iterator(); iter.hasNext();) {
             Cookie jmcookie = (Cookie) iter.next().getObjectValue();
             // Set to running version, to allow function evaluation for the cookie values (bug 28715)
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(true);
             }
             cookies[i++] = makeCookie(jmcookie);
             if (allowVariableCookie) {
                 jmcookie.setRunningVersion(false);
             }
         }
         String host = url.getHost();
         String protocol = url.getProtocol();
         int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
         String path = url.getPath();
         boolean secure = HTTPSamplerBase.isSecure(protocol);
         return cookieSpec.match(host, port, path, secure, cookies);
     }
     
     /**
      * Find cookies applicable to the given URL and build the Cookie header from
      * them.
      *
      * @param url
      *            URL of the request to which the returned header will be added.
      * @return the value string for the cookie header (goes after "Cookie: ").
      */
     @Override
     public String getCookieHeaderForURL(
             CollectionProperty cookiesCP,
             URL url,
             boolean allowVariableCookie) {
         org.apache.commons.httpclient.Cookie[] c = 
                 getCookiesForUrl(cookiesCP, url, allowVariableCookie);
         int count = c.length;
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled){
             log.debug("Found "+count+" cookies for "+url.toExternalForm());
         }
         if (count <=0){
             return null;
         }
         String hdr=cookieSpec.formatCookieHeader(c).getValue();
         if (debugEnabled){
             log.debug("Cookie: "+hdr);
         }
         return hdr;
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void addCookieFromHeader(CookieManager cookieManager,
             boolean checkCookies,String cookieHeader, URL url){
         boolean debugEnabled = log.isDebugEnabled();
         if (debugEnabled) {
             log.debug("Received Cookie: " + cookieHeader + " From: " + url.toExternalForm());
         }
         String protocol = url.getProtocol();
         String host = url.getHost();
         int port= HTTPSamplerBase.getDefaultPort(protocol,url.getPort());
         String path = url.getPath();
         boolean isSecure=HTTPSamplerBase.isSecure(protocol);
         org.apache.commons.httpclient.Cookie[] cookies= null;
         try {
             cookies = cookieSpec.parse(host, port, path, isSecure, cookieHeader);
         } catch (MalformedCookieException e) {
             log.warn(cookieHeader+e.getLocalizedMessage());
         } catch (IllegalArgumentException e) {
             log.warn(cookieHeader+e.getLocalizedMessage());
         }
         if (cookies == null) {
             return;
         }
         for(org.apache.commons.httpclient.Cookie cookie : cookies){
             try {
                 if (checkCookies) {
                     cookieSpec.validate(host, port, path, isSecure, cookie);
                 }
                 Date expiryDate = cookie.getExpiryDate();
                 long exp = 0;
                 if (expiryDate!= null) {
                     exp=expiryDate.getTime();
                 }
                 Cookie newCookie = new Cookie(
                         cookie.getName(),
                         cookie.getValue(),
                         cookie.getDomain(),
                         cookie.getPath(),
                         cookie.getSecure(),
                         exp / 1000,
                         cookie.isPathAttributeSpecified(),
                         cookie.isDomainAttributeSpecified()
                         );
 
                 // Store session cookies as well as unexpired ones
                 if (exp == 0 || exp >= System.currentTimeMillis()) {
                     newCookie.setVersion(cookie.getVersion());
                     cookieManager.add(newCookie); // Has its own debug log; removes matching cookies
                 } else {
                     cookieManager.removeMatchingCookies(newCookie);
                     if (debugEnabled){
                         log.debug("Dropping expired Cookie: "+newCookie.toString());
                     }
                 }
             } catch (MalformedCookieException e) { // This means the cookie was wrong for the URL
                 log.warn("Not storing invalid cookie: <"+cookieHeader+"> for URL "+url+" ("+e.getLocalizedMessage()+")");
             } catch (IllegalArgumentException e) {
                 log.warn(cookieHeader+e.getLocalizedMessage());
             }
         }
 
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/Header.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/Header.java
index b8f4ca906..746484de4 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/Header.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/Header.java
@@ -1,98 +1,111 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.testelement.AbstractTestElement;
 
 /**
  * This class is an HTTP Header encapsulator.
  *
  */
 public class Header extends AbstractTestElement implements Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private static final String HNAME = "Header.name";  //$NON-NLS-1$
     // See TestElementPropertyConverter
 
     private static final String VALUE = "Header.value"; //$NON-NLS-1$
 
     /**
-     * Create the header.
+     * Create the header. Uses an empty name and value as default
      */
     public Header() {
         this("", ""); //$NON-NLS-1$ $NON-NLS-2$
     }
 
     /**
-     * Create the coookie.
+     * Create the header.
+     *
+     * @param name
+     *            name of the header
+     * @param value
+     *            name of the header
      */
     public Header(String name, String value) {
         this.setName(name);
         this.setValue(value);
     }
 
     public void addConfigElement(ConfigElement config) {
     }
 
     public boolean expectsModification() {
         return false;
     }
 
     /**
      * Get the name for this object.
+     *
+     * @return the name of this header
      */
     @Override
     public String getName() {
         return getPropertyAsString(HNAME);
     }
 
     /**
      * Set the name for this object.
+     *
+     * @param name the name of this header
      */
     @Override
     public void setName(String name) {
         this.setProperty(HNAME, name);
     }
 
     /**
      * Get the value for this object.
+     *
+     * @return the value of this header
      */
     public String getValue() {
         return getPropertyAsString(VALUE);
     }
 
     /**
      * Set the value for this object.
+     *
+     * @param value the value of this header
      */
     public void setValue(String value) {
         this.setProperty(VALUE, value);
     }
 
     /**
      * Creates a string representation of this header.
      */
     @Override
     public String toString() {
         return getName() + "\t" + getValue(); //$NON-NLS-1$
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/HeaderManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/HeaderManager.java
index b11e5e385..153ee83ef 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/HeaderManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/HeaderManager.java
@@ -1,283 +1,312 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jorphan.util.JOrphanUtils;
 
 /**
  * This class provides an interface to headers file to pass HTTP headers along
  * with a request.
  *
  * @version $Revision$
  */
 public class HeaderManager extends ConfigTestElement implements Serializable {
 
     private static final long serialVersionUID = 240L;
 
     public static final String HEADERS = "HeaderManager.headers";// $NON-NLS-1$
 
     private static final String[] COLUMN_RESOURCE_NAMES = {
           "name",             // $NON-NLS-1$
           "value"             // $NON-NLS-1$
         };
 
     private static final int COLUMN_COUNT = COLUMN_RESOURCE_NAMES.length;
 
 
     /**
      * Apache SOAP driver does not provide an easy way to get and set the cookie
      * or HTTP header. Therefore it is necessary to store the SOAPHTTPConnection
      * object and reuse it.
      */
     private Object SOAPHeader = null;
 
     public HeaderManager() {
         setProperty(new CollectionProperty(HEADERS, new ArrayList<Object>()));
     }
 
     /** {@inheritDoc} */
     @Override
     public void clear() {
         super.clear();
         setProperty(new CollectionProperty(HEADERS, new ArrayList<Object>()));
     }
 
     /**
      * Get the collection of JMeterProperty entries representing the headers.
      *
      * @return the header collection property
      */
     public CollectionProperty getHeaders() {
         return (CollectionProperty) getProperty(HEADERS);
     }
 
     public int getColumnCount() {
         return COLUMN_COUNT;
     }
 
     public String getColumnName(int column) {
         return COLUMN_RESOURCE_NAMES[column];
     }
 
     public Class<? extends String> getColumnClass(int column) {
         return COLUMN_RESOURCE_NAMES[column].getClass();
     }
 
     public Header getHeader(int row) {
         return (Header) getHeaders().get(row).getObjectValue();
     }
 
     /**
      * Save the header data to a file.
+     *
+     * @param headFile
+     *            name of the file to store headers into. If name is relative
+     *            the system property <code>user.dir</code> will be prepended
+     * @throws IOException
+     *             if writing the headers fails
      */
     public void save(String headFile) throws IOException {
         File file = new File(headFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir")// $NON-NLS-1$
                     + File.separator + headFile);
         }
         PrintWriter writer = new PrintWriter(new FileWriter(file)); // TODO Charset ?
         writer.println("# JMeter generated Header file");// $NON-NLS-1$
         final CollectionProperty hdrs = getHeaders();
         for (int i = 0; i < hdrs.size(); i++) {
             final JMeterProperty hdr = hdrs.get(i);
             Header head = (Header) hdr.getObjectValue();
             writer.println(head.toString());
         }
         writer.flush();
         writer.close();
     }
 
     /**
      * Add header data from a file.
+     *
+     * @param headerFile
+     *            name of the file to read headers from. If name is relative the
+     *            system property <code>user.dir</code> will be prepended
+     * @throws IOException
+     *             if reading headers fails
      */
     public void addFile(String headerFile) throws IOException {
         File file = new File(headerFile);
         if (!file.isAbsolute()) {
             file = new File(System.getProperty("user.dir")// $NON-NLS-1$
                     + File.separator + headerFile);
         }
         if (!file.canRead()) {
             throw new IOException("The file you specified cannot be read.");
         }
 
         BufferedReader reader = null;
         try {
             reader = new BufferedReader(new FileReader(file)); // TODO Charset ?
             String line;
             while ((line = reader.readLine()) != null) {
                 try {
                     if (line.startsWith("#") || JOrphanUtils.isBlank(line)) {// $NON-NLS-1$
                         continue;
                     }
                     String[] st = JOrphanUtils.split(line, "\t", " ");// $NON-NLS-1$ $NON-NLS-2$
                     int name = 0;
                     int value = 1;
                     Header header = new Header(st[name], st[value]);
                     getHeaders().addItem(header);
                 } catch (Exception e) {
                     throw new IOException("Error parsing header line\n\t'" + line + "'\n\t" + e);
                 }
             }
         } finally {
             IOUtils.closeQuietly(reader);
         }
     }
 
     /**
      * Add a header.
+     *
+     * @param h {@link Header} to add
      */
     public void add(Header h) {
         getHeaders().addItem(h);
     }
 
     /**
      * Add an empty header.
      */
     public void add() {
         getHeaders().addItem(new Header());
     }
 
     /**
      * Remove a header.
+     *
+     * @param index index from the header to remove
      */
     public void remove(int index) {
         getHeaders().remove(index);
     }
 
     /**
      * Return the number of headers.
+     *
+     * @return number of headers
      */
     public int size() {
         return getHeaders().size();
     }
 
     /**
      * Return the header at index i.
+     *
+     * @param i
+     *            index of the header to get
+     * @return {@link Header} at index <code>i</code>
      */
     public Header get(int i) {
         return (Header) getHeaders().get(i).getObjectValue();
     }
 
     /**
      * Remove from Headers the header named name
      * @param name header name
      */
     public void removeHeaderNamed(String name) {
         List<Integer> removeIndices = new ArrayList<Integer>();
         for (int i = getHeaders().size() - 1; i >= 0; i--) {
             Header header = (Header) getHeaders().get(i).getObjectValue();
             if (header == null) {
                 continue;
             }
             if (header.getName().equalsIgnoreCase(name)) {
                 removeIndices.add(Integer.valueOf(i));
             }
         }
         for (Integer indice : removeIndices) {
             getHeaders().remove(indice.intValue());
         }
     }
 
     /**
      * Added support for SOAP related header stuff. 1-29-04 Peter Lin
      *
      * @return the SOAP header Object
      */
     public Object getSOAPHeader() {
         return this.SOAPHeader;
     }
 
     /**
      * Set the SOAPHeader with the SOAPHTTPConnection object. We may or may not
-     * want to rename this to setHeaderObject(Object). Concievably, other
+     * want to rename this to setHeaderObject(Object). Conceivably, other
      * samplers may need this kind of functionality. 1-29-04 Peter Lin
      *
-     * @param header
+     * @param header soap header
      */
     public void setSOAPHeader(Object header) {
         this.SOAPHeader = header;
     }
 
     /**
      * Merge the attributes with a another HeaderManager's attributes.
-     * @param element The object to be merged with
-     * @param preferLocalValues When both objects have a value for the
-     *        same attribute, this flag determines which value is preferresd.
+     * 
+     * @param element
+     *            The object to be merged with
+     * @param preferLocalValues
+     *            When both objects have a value for the same attribute, this
+     *            flag determines which value is preferred.
+     * @return merged HeaderManager
+     * @throws IllegalArgumentException
+     *             if <code>element</code> is not an instance of
+     *             {@link HeaderManager}
      */
     public HeaderManager merge(TestElement element, boolean preferLocalValues) {
         if (!(element instanceof HeaderManager)) {
             throw new IllegalArgumentException("Cannot merge type:" + this.getClass().getName() + " with type:" + element.getClass().getName());
         }
 
         // start off with a merged object as a copy of the local object
         HeaderManager merged = (HeaderManager)this.clone();
 
         HeaderManager other = (HeaderManager)element;
         // iterate thru each of the other headers
         for (int i = 0; i < other.getHeaders().size(); i++) {
             Header otherHeader = other.get(i);
             boolean found = false;
             // find the same property in the local headers
             for (int j = 0; j < merged.getHeaders().size(); j++) {
                 Header mergedHeader = merged.get(j);
                 if (mergedHeader.getName().equalsIgnoreCase(otherHeader.getName())) {
                     // we have a match
                     found = true;
                     if (!preferLocalValues) {
                         // prefer values from the other object
                         if ( (otherHeader.getValue() == null) || (otherHeader.getValue().length() == 0) ) {
                             // the other object has an empty value, so remove this value from the merged object
                             merged.remove(j);
                         } else {
                             // use the other object's value
                             mergedHeader.setValue(otherHeader.getValue());
                         }
                     }
                     // break out of the inner loop
                     break;
                 }
             }
             if (!found) {
                 // the other object has a new value to be added to the merged
                 merged.add(otherHeader);
             }
         }
 
         // finally, merge the names so it's clear they've been merged
         merged.setName(merged.getName() + ":" + other.getName());
 
         return merged;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java
index 691a2bd8e..df6474c07 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/gui/WebServiceSamplerGui.java
@@ -1,530 +1,533 @@
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
 
 package org.apache.jmeter.protocol.http.control.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.commons.lang3.ArrayUtils;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.WebServiceSampler;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.WSDLHelper;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 import org.apache.jorphan.gui.JLabeledTextField;
 import org.apache.jorphan.gui.layout.VerticalLayout;
 
 /**
  * This is the GUI for the webservice samplers. It extends AbstractSamplerGui
  * and is modeled after the SOAP sampler GUI. I've added instructional notes to
  * the GUI for instructional purposes. XML parsing is pretty heavy weight,
  * therefore the notes address those situations. <br>
  * Created on: Jun 26, 2003
  *
  */
 public class WebServiceSamplerGui extends AbstractSamplerGui implements java.awt.event.ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     private final JLabeledTextField domain = new JLabeledTextField(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
 
     private final JLabeledTextField protocol = new JLabeledTextField(JMeterUtils.getResString("protocol"), 4); // $NON-NLS-1$
 
     private final JLabeledTextField port = new JLabeledTextField(JMeterUtils.getResString("web_server_port"), 4); // $NON-NLS-1$
 
     private final JLabeledTextField path = new JLabeledTextField(JMeterUtils.getResString("path")); // $NON-NLS-1$
 
     private final JLabeledTextField soapAction = new JLabeledTextField(JMeterUtils.getResString("webservice_soap_action")); // $NON-NLS-1$
 
     /**
      * checkbox for Session maintenance.
      */
     private JCheckBox maintainSession = new JCheckBox(JMeterUtils.getResString("webservice_maintain_session"), true); // $NON-NLS-1$
 
     
     private JTextArea soapXml;
 
     private final JLabeledTextField wsdlField = new JLabeledTextField(JMeterUtils.getResString("wsdl_url")); // $NON-NLS-1$
 
     private final JButton wsdlButton = new JButton(JMeterUtils.getResString("load_wsdl")); // $NON-NLS-1$
 
     private final JButton selectButton = new JButton(JMeterUtils.getResString("configure_wsdl")); // $NON-NLS-1$
 
     private JLabeledChoice wsdlMethods = null;
 
     private transient WSDLHelper HELPER = null;
 
     private final FilePanel soapXmlFile = new FilePanel(JMeterUtils.getResString("get_xml_from_file"), ".xml"); // $NON-NLS-1$
 
     private final JLabeledTextField randomXmlFile = new JLabeledTextField(JMeterUtils.getResString("get_xml_from_random")); // $NON-NLS-1$
 
     private final JLabeledTextField connectTimeout = new JLabeledTextField(JMeterUtils.getResString("webservice_timeout"), 4); // $NON-NLS-1$
 
     /**
      * checkbox for memory cache.
      */
     private JCheckBox memCache = new JCheckBox(JMeterUtils.getResString("memory_cache"), true); // $NON-NLS-1$
 
     /**
      * checkbox for reading the response
      */
     private JCheckBox readResponse = new JCheckBox(JMeterUtils.getResString("read_soap_response")); // $NON-NLS-1$
 
     /**
      * checkbox for use proxy
      */
     private JCheckBox useProxy = new JCheckBox(JMeterUtils.getResString("webservice_use_proxy")); // $NON-NLS-1$
 
     /**
      * text field for the proxy host
      */
     private JTextField proxyHost;
 
     /**
      * text field for the proxy port
      */
     private JTextField proxyPort;
 
     /**
      * Text note about read response and its usage.
      */
     private String readToolTip = JMeterUtils.getResString("read_response_note") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("read_response_note2") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("read_response_note3"); // $NON-NLS-1$
 
     /**
      * Text note for proxy
      */
     private String proxyToolTip = JMeterUtils.getResString("webservice_proxy_note") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("webservice_proxy_note2") // $NON-NLS-1$
                                   + " " // $NON-NLS-1$
                                   + JMeterUtils.getResString("webservice_proxy_note3"); // $NON-NLS-1$
     public WebServiceSamplerGui() {
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "webservice_sampler_title"; // $NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     @Override
     public TestElement createTestElement() {
         WebServiceSampler sampler = new WebServiceSampler();
         this.configureTestElement(sampler);
         this.modifyTestElement(sampler);
         return sampler;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement s) {
         WebServiceSampler sampler = (WebServiceSampler) s;
         this.configureTestElement(sampler);
         sampler.setDomain(domain.getText());
         sampler.setProperty(HTTPSamplerBase.PORT,port.getText());
         sampler.setProtocol(protocol.getText());
         sampler.setPath(path.getText());
         sampler.setWsdlURL(wsdlField.getText());
         sampler.setMethod(HTTPConstants.POST);
         sampler.setSoapAction(soapAction.getText());
         sampler.setMaintainSession(maintainSession.isSelected());
         sampler.setXmlData(soapXml.getText());
         sampler.setXmlFile(soapXmlFile.getFilename());
         sampler.setXmlPathLoc(randomXmlFile.getText());
         sampler.setTimeout(connectTimeout.getText());
         sampler.setMemoryCache(memCache.isSelected());
         sampler.setReadResponse(readResponse.isSelected());
         sampler.setUseProxy(useProxy.isSelected());
         sampler.setProxyHost(proxyHost.getText());
         sampler.setProxyPort(proxyPort.getText());
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     @Override
     public void clearGui() {
         super.clearGui();
         wsdlMethods.setValues(new String[0]);
         domain.setText(""); //$NON-NLS-1$
         protocol.setText(""); //$NON-NLS-1$
         port.setText(""); //$NON-NLS-1$
         path.setText(""); //$NON-NLS-1$
         soapAction.setText(""); //$NON-NLS-1$
         maintainSession.setSelected(WebServiceSampler.MAINTAIN_SESSION_DEFAULT);
         soapXml.setText(""); //$NON-NLS-1$
         wsdlField.setText(""); //$NON-NLS-1$
         randomXmlFile.setText(""); //$NON-NLS-1$
         connectTimeout.setText(""); //$NON-NLS-1$
         proxyHost.setText(""); //$NON-NLS-1$
         proxyPort.setText(""); //$NON-NLS-1$
         memCache.setSelected(true);
         readResponse.setSelected(false);
         useProxy.setSelected(false);
         soapXmlFile.setFilename(""); //$NON-NLS-1$
     }
 
     /**
      * init() adds soapAction to the mainPanel. The class reuses logic from
      * SOAPSampler, since it is common.
      */
     private void init() {
         setLayout(new BorderLayout(0, 5));
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         // MAIN PANEL
         JPanel mainPanel = new JPanel();
         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         
         mainPanel.add(createTopPanel(), BorderLayout.NORTH);
         mainPanel.add(createMessagePanel(), BorderLayout.CENTER);
         mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
         this.add(mainPanel);
     }
 
     private final JPanel createTopPanel() {
         JPanel topPanel = new JPanel();
         topPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));
         
         JPanel wsdlHelper = new JPanel();
         wsdlHelper.setLayout(new BoxLayout(wsdlHelper, BoxLayout.Y_AXIS));
         wsdlHelper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("webservice_configuration_wizard"))); // $NON-NLS-1$
 
         // Button for browsing webservice wsdl
         JPanel wsdlEntry = new JPanel();
         wsdlEntry.setLayout(new BoxLayout(wsdlEntry, BoxLayout.X_AXIS));
         Border margin = new EmptyBorder(0, 5, 0, 5);
         wsdlEntry.setBorder(margin);
         wsdlHelper.add(wsdlEntry);
         wsdlEntry.add(wsdlField);
         wsdlEntry.add(wsdlButton);
         wsdlButton.addActionListener(this);
 
         // Web Methods
         JPanel listPanel = new JPanel();
         listPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
         JLabel selectLabel = new JLabel(JMeterUtils.getResString("webservice_methods")); // $NON-NLS-1$
         wsdlMethods = new JLabeledChoice();
         wsdlHelper.add(listPanel);
         listPanel.add(selectLabel);
         listPanel.add(wsdlMethods);
         listPanel.add(selectButton);
         selectButton.addActionListener(this);
 
         topPanel.add(wsdlHelper);
         
         JPanel urlPane = new JPanel();
         urlPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         urlPane.add(protocol);
         urlPane.add(Box.createRigidArea(new Dimension(5,0)));
         urlPane.add(domain);
         urlPane.add(Box.createRigidArea(new Dimension(5,0)));
         urlPane.add(port);
         urlPane.add(Box.createRigidArea(new Dimension(5,0)));
         urlPane.add(connectTimeout);
         topPanel.add(urlPane);
         
         topPanel.add(createParametersPanel());
         
         return topPanel;
     }
 
     private final JPanel createParametersPanel() {
         JPanel paramsPanel = new JPanel();
         paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.X_AXIS));
         paramsPanel.add(path);
         paramsPanel.add(Box.createHorizontalGlue());        
         paramsPanel.add(soapAction);
         paramsPanel.add(Box.createHorizontalGlue());        
         paramsPanel.add(maintainSession);
         return paramsPanel;
     }
     
     private final JPanel createMessagePanel() {
         JPanel msgPanel = new JPanel();
         msgPanel.setLayout(new BorderLayout(5, 0));
         msgPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("webservice_message_soap"))); // $NON-NLS-1$
 
         JPanel soapXmlPane = new JPanel();
         soapXmlPane.setLayout(new BorderLayout(5, 0));
         soapXmlPane.setBorder(BorderFactory.createTitledBorder(
                 JMeterUtils.getResString("soap_data_title"))); // $NON-NLS-1$
         soapXmlPane.setPreferredSize(new Dimension(4, 4)); // Permit dynamic resize of TextArea
         soapXml = new JTextArea();
         soapXml.setLineWrap(true);
         soapXml.setWrapStyleWord(true);
         soapXml.setTabSize(4); // improve xml display
         soapXmlPane.add(new JScrollPane(soapXml), BorderLayout.CENTER);
         msgPanel.add(soapXmlPane, BorderLayout.CENTER);
         
         JPanel southPane = new JPanel();
         southPane.setLayout(new BoxLayout(southPane, BoxLayout.Y_AXIS));
         southPane.add(soapXmlFile);
         JPanel randomXmlPane = new JPanel();
         randomXmlPane.setLayout(new BorderLayout(5, 0));
         randomXmlPane.setBorder(BorderFactory.createTitledBorder(
                 JMeterUtils.getResString("webservice_get_xml_from_random_title"))); // $NON-NLS-1$
         randomXmlPane.add(randomXmlFile, BorderLayout.CENTER);
         southPane.add(randomXmlPane);
         msgPanel.add(southPane, BorderLayout.SOUTH);
         return msgPanel;
     }
     
     private final JPanel createBottomPanel() {
         JPanel optionPane = new JPanel();
         optionPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("option"))); // $NON-NLS-1$
         optionPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         JPanel ckboxPane = new HorizontalPanel();
         ckboxPane.add(memCache, BorderLayout.WEST);
         ckboxPane.add(readResponse, BorderLayout.CENTER);
         readResponse.setToolTipText(readToolTip);
         optionPane.add(ckboxPane);
 
         // add the proxy elements
         optionPane.add(getProxyServerPanel());
         return optionPane;
         
     }
     /**
      * Create a panel containing the proxy server details
      *
      * @return the panel
      */
     private final JPanel getProxyServerPanel(){
         JPanel proxyServer = new JPanel();
         proxyServer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
         proxyServer.add(useProxy);
         useProxy.addActionListener(this);
         useProxy.setToolTipText(proxyToolTip);
         proxyServer.add(Box.createRigidArea(new Dimension(5,0)));
         proxyServer.add(getProxyHostPanel());
         proxyServer.add(Box.createRigidArea(new Dimension(5,0)));
         proxyServer.add(getProxyPortPanel());
         return proxyServer;
     }
     
     private JPanel getProxyHostPanel() {
         proxyHost = new JTextField(12);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(proxyHost);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyHost, BorderLayout.CENTER);
         return panel;
     }
     
     private JPanel getProxyPortPanel() {
         proxyPort = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(proxyPort);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPort, BorderLayout.CENTER);
 
         return panel;
     }
 
     /**
      * the implementation loads the URL and the soap action for the request.
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         WebServiceSampler sampler = (WebServiceSampler) el;
         wsdlField.setText(sampler.getWsdlURL());
         final String wsdlText = wsdlField.getText();
         if (wsdlText != null && wsdlText.length() > 0) {
             fillWsdlMethods(wsdlField.getText(), true, sampler.getSoapAction());
         }
         protocol.setText(sampler.getProtocol());
         domain.setText(sampler.getDomain());
         port.setText(sampler.getPropertyAsString(HTTPSamplerBase.PORT));
         path.setText(sampler.getPath());
         soapAction.setText(sampler.getSoapAction());
         maintainSession.setSelected(sampler.getMaintainSession());
         soapXml.setText(sampler.getXmlData());
         soapXml.setCaretPosition(0); // go to 1st line
         soapXmlFile.setFilename(sampler.getXmlFile());
         randomXmlFile.setText(sampler.getXmlPathLoc());
         connectTimeout.setText(sampler.getTimeout());
         memCache.setSelected(sampler.getMemoryCache());
         readResponse.setSelected(sampler.getReadResponse());
         useProxy.setSelected(sampler.getUseProxy());
         if (sampler.getProxyHost().length() == 0) {
             proxyHost.setEnabled(false);
         } else {
             proxyHost.setText(sampler.getProxyHost());
         }
         if (sampler.getProxyPort() == 0) {
             proxyPort.setEnabled(false);
         } else {
             proxyPort.setText(String.valueOf(sampler.getProxyPort()));
         }
     }
 
     /**
      * configure the sampler from the WSDL. If the WSDL did not include service
      * node, it will use the original URL minus the querystring. That may not be
      * correct, so we should probably add a note. For Microsoft webservices it
      * will work, since that's how IIS works.
      */
     public void configureFromWSDL() {
         if (HELPER != null) {
             if(HELPER.getBinding() != null) {
                 this.protocol.setText(HELPER.getProtocol());
                 this.domain.setText(HELPER.getBindingHost());
                 if (HELPER.getBindingPort() > 0) {
                     this.port.setText(String.valueOf(HELPER.getBindingPort()));
                 } else {
                     this.port.setText("80"); // $NON-NLS-1$
                 }
                 this.path.setText(HELPER.getBindingPath());
             }
             this.soapAction.setText(HELPER.getSoapAction(this.wsdlMethods.getText()));
         }
     }
 
     /**
      * The method uses WSDLHelper to get the information from the WSDL. Since
      * the logic for getting the description is isolated to this method, we can
      * easily replace it with a different WSDL driver later on.
      *
      * @param url
-     * @param silent 
+     *            URL to the WSDL
+     * @param silent
+     *            flag whether errors parsing the WSDL should be shown to the
+     *            user. If <code>true</code> errors will be silently ignored
      * @return array of web methods
      */
     public String[] browseWSDL(String url, boolean silent) {
         try {
             // We get the AuthManager and pass it to the WSDLHelper
             // once the sampler is updated to Axis, all of this stuff
             // should not be necessary. Now I just need to find the
             // time and motivation to do it.
             WebServiceSampler sampler = (WebServiceSampler) this.createTestElement();
             AuthManager manager = sampler.getAuthManager();
             HELPER = new WSDLHelper(url, manager);
             HELPER.parse();
             return HELPER.getWebMethods();
         } catch (Exception exception) {
             if (!silent) {
                 JOptionPane.showConfirmDialog(this,
                         JMeterUtils.getResString("wsdl_helper_error") // $NON-NLS-1$
                         +"\n"+exception, // $NON-NLS-1$
                         JMeterUtils.getResString("warning"), // $NON-NLS-1$
                         JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
             }
             return ArrayUtils.EMPTY_STRING_ARRAY;
         }
     }
 
     /**
      * method from ActionListener
      *
      * @param event
      *            that occurred
      */
     @Override
     public void actionPerformed(ActionEvent event) {
         final Object eventSource = event.getSource();
         if (eventSource == selectButton) {
             this.configureFromWSDL();
         } else if (eventSource == useProxy) {
             // if use proxy is checked, we enable
             // the text fields for the host and port
             boolean use = useProxy.isSelected();
             if (use) {
                 proxyHost.setEnabled(true);
                 proxyPort.setEnabled(true);
             } else {
                 proxyHost.setEnabled(false);
                 proxyPort.setEnabled(false);
             }
         } else if (eventSource == wsdlButton){
             final String wsdlText = wsdlField.getText();
             if (wsdlText != null && wsdlText.length() > 0) {
                 fillWsdlMethods(wsdlText, false, null);
             } else {
                 JOptionPane.showConfirmDialog(this,
                         JMeterUtils.getResString("wsdl_url_error"), // $NON-NLS-1$
                         JMeterUtils.getResString("warning"), // $NON-NLS-1$
                         JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
             }
         }
     }
 
     /**
      * @param wsdlText
      * @param silent
      * @param soapAction 
      */
     private void fillWsdlMethods(final String wsdlText, boolean silent, String soapAction) {
         String[] wsdlData = browseWSDL(wsdlText, silent);
         if (wsdlData != null) {
             wsdlMethods.setValues(wsdlData);
             if (HELPER != null && soapAction != null) {
                 String selected = HELPER.getSoapActionName(soapAction);
                 if (selected != null) {
                     wsdlMethods.setText(selected);
                 }
             }
             wsdlMethods.repaint();
         }
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/gui/HTTPFileArgsPanel.java b/src/protocol/http/org/apache/jmeter/protocol/http/gui/HTTPFileArgsPanel.java
index db06c80f3..812e552f7 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/gui/HTTPFileArgsPanel.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/gui/HTTPFileArgsPanel.java
@@ -1,404 +1,404 @@
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
 
 package org.apache.jmeter.protocol.http.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.File;
 import java.util.Iterator;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.table.TableCellEditor;
 
 import org.apache.jmeter.gui.util.FileDialoger;
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.reflect.Functor;
 
 /*
  * Note: this class is currently only suitable for use with HTTSamplerBase.
  * If it is required for other classes, then the appropriate configure() and modifyTestElement()
  * method code needs to be written.
  */
 /**
  * A GUI panel allowing the user to enter file information for http upload.
  * Used by MultipartUrlConfigGui for use in HTTP Samplers.
  */
 public class HTTPFileArgsPanel extends JPanel implements ActionListener {
 
     private static final long serialVersionUID = 240L;
 
     /** The title label for this component. */
     private JLabel tableLabel;
 
     /** The table containing the list of files. */
     private transient JTable table;
 
     /** The model for the files table. */
     private transient ObjectTableModel tableModel; // only contains HTTPFileArg elements
 
     /** A button for adding new files to the table. */
     private JButton add;
 
     /** A button for browsing file system to set path of selected row in table. */
     private JButton browse;
 
     /** A button for removing files from the table. */
     private JButton delete;
 
     /** Command for adding a row to the table. */
     private static final String ADD = "add"; // $NON-NLS-1$
 
     /** Command for browsing filesystem to set path of selected row in table. */
     private static final String BROWSE = "browse"; // $NON-NLS-1$
 
     /** Command for removing a row from the table. */
     private static final String DELETE = "delete"; // $NON-NLS-1$
 
     private static final String FILEPATH = "send_file_filename_label"; // $NON-NLS-1$
 
     /** The parameter name column title of file table. */
     private static final String PARAMNAME = "send_file_param_name_label"; //$NON-NLS-1$
 
     /** The mime type column title of file table. */
     private static final String MIMETYPE = "send_file_mime_label"; //$NON-NLS-1$
 
     public HTTPFileArgsPanel() {
         this(""); // required for unit tests
     }
 
     /**
      * Create a new HTTPFileArgsPanel as an embedded component, using the
      * specified title.
      *
      * @param label
      *  the title for the component.
      */
     public HTTPFileArgsPanel(String label) {
         tableLabel = new JLabel(label);
         init();
     }
 
     /**
      * Initialize the table model used for the http files table.
      */
     private void initializeTableModel() {
         tableModel = new ObjectTableModel(new String[] {
                 FILEPATH, PARAMNAME, MIMETYPE},
             HTTPFileArg.class,
             new Functor[] {
                 new Functor("getPath"), //$NON-NLS-1$
                 new Functor("getParamName"), //$NON-NLS-1$
                 new Functor("getMimeType")}, //$NON-NLS-1$
             new Functor[] {
                 new Functor("setPath"), //$NON-NLS-1$
                 new Functor("setParamName"), //$NON-NLS-1$
                 new Functor("setMimeType")}, //$NON-NLS-1$
             new Class[] {String.class, String.class, String.class});
     }
 
     public static boolean testFunctors(){
         HTTPFileArgsPanel instance = new HTTPFileArgsPanel(""); //$NON-NLS-1$
         instance.initializeTableModel();
         return instance.tableModel.checkFunctors(null,instance.getClass());
     }
 
     /**
      * Resize the table columns to appropriate widths.
      *
      * @param table
      *  the table to resize columns for
      */
     private void sizeColumns(JTable table) {
         GuiUtils.fixSize(table.getColumn(PARAMNAME), table);
         GuiUtils.fixSize(table.getColumn(MIMETYPE), table);
     }
 
     /**
      * Save the GUI data in the HTTPSamplerBase element.
      *
-     * @param testElement
+     * @param testElement {@link TestElement} to modify
      */
     public void modifyTestElement(TestElement testElement) {
         GuiUtils.stopTableEditing(table);
         if (testElement instanceof HTTPSamplerBase) {
             HTTPSamplerBase base = (HTTPSamplerBase) testElement;
             int rows = tableModel.getRowCount();
             @SuppressWarnings("unchecked") // we only put HTTPFileArgs in it
             Iterator<HTTPFileArg> modelData = (Iterator<HTTPFileArg>) tableModel.iterator();
             HTTPFileArg[] files = new HTTPFileArg[rows];
             int row=0;
             while (modelData.hasNext()) {
                 HTTPFileArg file = modelData.next();
                 files[row++]=file;
             }
             base.setHTTPFiles(files);
         }
     }
 
     /**
      * A newly created component can be initialized with the contents of a
      * HTTPSamplerBase object by calling this method. The component is responsible for
      * querying the Test Element object for the relevant information to display
      * in its GUI.
      *
      * @param testElement the HTTPSamplerBase to be used to configure the GUI
      */
     public void configure(TestElement testElement) {
         if (testElement instanceof HTTPSamplerBase) {
             HTTPSamplerBase base = (HTTPSamplerBase) testElement;
             tableModel.clearData();
             for(HTTPFileArg file : base.getHTTPFiles()){
                 tableModel.addRow(file);
             }
             checkDeleteAndBrowseStatus();
         }
     }
 
 
     /**
      * Enable or disable the delete button depending on whether or not there is
      * a row to be deleted.
      */
     private void checkDeleteAndBrowseStatus() {
         // Disable DELETE and BROWSE buttons if there are no rows in
         // the table to delete.
         if (tableModel.getRowCount() == 0) {
             browse.setEnabled(false);
             delete.setEnabled(false);
         } else {
             browse.setEnabled(true);
             delete.setEnabled(true);
         }
     }
 
     /**
      * Clear all rows from the table.
      */
     public void clear() {
         GuiUtils.stopTableEditing(table);
         tableModel.clearData();
     }
 
     /**
      * Invoked when an action occurs. This implementation supports the add and
      * delete buttons.
      *
      * @param e
      *  the event that has occurred
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
         if (action.equals(ADD)) {
             addFile(""); //$NON-NLS-1$
         }
         runCommandOnSelectedFile(action);
     }
 
     /**
      * runs specified command on currently selected file.
      *
      * @param command specifies which process will be done on selected
      * file. it's coming from action command currently catched by
      * action listener.
      *
      * @see runCommandOnRow
      */
     private void runCommandOnSelectedFile(String command) {
         // If a table cell is being edited, we must cancel the editing before
         // deleting the row
         if (table.isEditing()) {
             TableCellEditor cellEditor = table.getCellEditor(table.getEditingRow(), table.getEditingColumn());
             cellEditor.cancelCellEditing();
         }
         int rowSelected = table.getSelectedRow();
         if (rowSelected >= 0) {
             runCommandOnRow(command, rowSelected);
             tableModel.fireTableDataChanged();
             // Disable DELETE and BROWSE if there are no rows in the table to delete.
             checkDeleteAndBrowseStatus();
             // Table still contains one or more rows, so highlight (select)
             // the appropriate one.
             if (tableModel.getRowCount() != 0) {
                 int rowToSelect = rowSelected;
                 if (rowSelected >= tableModel.getRowCount()) {
                     rowToSelect = rowSelected - 1;
                 }
                 table.setRowSelectionInterval(rowToSelect, rowToSelect);
             }
         }
     }
 
     /**
      * runs specified command on currently selected table row.
      *
      * @param command specifies which process will be done on selected
      * file. it's coming from action command currently catched by
      * action listener.
      *
      * @param rowSelected index of selected row.
      */
     private void runCommandOnRow(String command, int rowSelected) {
         if (DELETE.equals(command)) {
             tableModel.removeRow(rowSelected);
         } else if (BROWSE.equals(command)) {
             String path = browseAndGetFilePath();
             tableModel.setValueAt(path, rowSelected, 0);
         }
     }
 
     /**
      * Add a new file row to the table.
      */
     private void addFile(String path) {
         // If a table cell is being edited, we should accept the current value
         // and stop the editing before adding a new row.
         GuiUtils.stopTableEditing(table);
 
         tableModel.addRow(new HTTPFileArg(path));
 
         // Enable DELETE (which may already be enabled, but it won't hurt)
         delete.setEnabled(true);
         browse.setEnabled(true);
 
         // Highlight (select) the appropriate row.
         int rowToSelect = tableModel.getRowCount() - 1;
         table.setRowSelectionInterval(rowToSelect, rowToSelect);
     }
 
     /**
      * opens a dialog box to choose a file and returns selected file's
      * path.
      *
      * @return a new File object
      */
     private String browseAndGetFilePath() {
         String path = ""; //$NON-NLS-1$
         JFileChooser chooser = FileDialoger.promptToOpenFile();
         if (chooser != null) {
             File file = chooser.getSelectedFile();
             if (file != null) {
                 path = file.getPath();
             }
         }
         return path;
     }
 
     /**
      * Stop any editing that is currently being done on the table. This will
      * save any changes that have already been made.
      */
     protected void stopTableEditing() {
         GuiUtils.stopTableEditing(table);
     }
     
     /**
      * Create the main GUI panel which contains the file table.
      *
      * @return the main GUI panel
      */
     private Component makeMainPanel() {
         initializeTableModel();
         table = new JTable(tableModel);
         table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         return makeScrollPane(table);
     }
 
     /**
      * Create a panel containing the title label for the table.
      *
      * @return a panel containing the title label
      */
     private Component makeLabelPanel() {
         JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         labelPanel.add(tableLabel);
         return labelPanel;
     }
 
     /**
      * Create a panel containing the add and delete buttons.
      *
      * @return a GUI panel containing the buttons
      */
     private JPanel makeButtonPanel() {
         add = new JButton(JMeterUtils.getResString("add")); // $NON-NLS-1$
         add.setActionCommand(ADD);
         add.setEnabled(true);
 
         browse = new JButton(JMeterUtils.getResString("browse")); // $NON-NLS-1$
         browse.setActionCommand(BROWSE);
 
         delete = new JButton(JMeterUtils.getResString("delete")); // $NON-NLS-1$
         delete.setActionCommand(DELETE);
 
         checkDeleteAndBrowseStatus();
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
         add.addActionListener(this);
         browse.addActionListener(this);
         delete.addActionListener(this);
         buttonPanel.add(add);
         buttonPanel.add(browse);
         buttonPanel.add(delete);
         return buttonPanel;
     }
 
     /**
      * Initialize the components and layout of this component.
      */
     private void init() {
         JPanel p = this;
 
         p.setLayout(new BorderLayout());
 
         p.add(makeLabelPanel(), BorderLayout.NORTH);
         p.add(makeMainPanel(), BorderLayout.CENTER);
         // Force a minimum table height of 70 pixels
         p.add(Box.createVerticalStrut(70), BorderLayout.WEST);
         p.add(makeButtonPanel(), BorderLayout.SOUTH);
 
         table.revalidate();
         sizeColumns(table);
     }
 
     private JScrollPane makeScrollPane(Component comp) {
         JScrollPane pane = new JScrollPane(comp);
         pane.setPreferredSize(pane.getMinimumSize());
         return pane;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/modifier/UserSequence.java b/src/protocol/http/org/apache/jmeter/protocol/http/modifier/UserSequence.java
index 88a6bd286..25eb1a8c8 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/modifier/UserSequence.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/modifier/UserSequence.java
@@ -1,95 +1,97 @@
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
 
 package org.apache.jmeter.protocol.http.modifier;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * This module controls the Sequence in which user details are returned. This
  * module uses round robin allocation of users.
  *
  * @version $Revision$
  */
 public class UserSequence implements Serializable {
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // -------------------------------------------
     // Constants and Data Members
     // -------------------------------------------
     private List<Map<String, String>> allUsers;
 
     private transient Iterator<Map<String, String>> indexOfUsers;
 
     // -------------------------------------------
     // Constructors
     // -------------------------------------------
 
     public UserSequence() {
     }
 
     /**
      * Load all user and parameter data into the sequence module.
      * <P>
      * ie a Set of Mapped "parameter names and parameter values" for each user
      * to be loaded into the sequencer.
+     *
+     * @param allUsers users and parameter data to be used
      */
     public UserSequence(List<Map<String, String>> allUsers) {
         this.allUsers = allUsers;
 
         // initalise pointer to first user
         indexOfUsers = allUsers.iterator();
     }
 
     // -------------------------------------------
     // Methods
     // -------------------------------------------
 
     /**
      * Returns the parameter data for the next user in the sequence
      *
      * @return a Map object of parameter names and matching parameter values for
      *         the next user
      */
     public synchronized Map<String, String> getNextUserMods() {
         // Use round robin allocation of user details
         if (!indexOfUsers.hasNext()) {
             indexOfUsers = allUsers.iterator();
         }
 
         Map<String, String> user;
         if (indexOfUsers.hasNext()) {
             user = indexOfUsers.next();
             log.debug("UserSequence.getNextuserMods(): current parameters will be " + "changed to: " + user);
         } else {
             // no entries in all users, therefore create an empty Map object
             user = new HashMap<String, String>();
         }
 
         return user;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
index 6267f3055..e79252a94 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HTMLParser.java
@@ -1,267 +1,270 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HtmlParsers can parse HTML content to obtain URLs.
  *
  */
 public abstract class HTMLParser {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     protected static final String ATT_BACKGROUND    = "background";// $NON-NLS-1$
     protected static final String ATT_CODE          = "code";// $NON-NLS-1$
     protected static final String ATT_CODEBASE      = "codebase";// $NON-NLS-1$
     protected static final String ATT_DATA          = "data";// $NON-NLS-1$
     protected static final String ATT_HREF          = "href";// $NON-NLS-1$
     protected static final String ATT_REL           = "rel";// $NON-NLS-1$
     protected static final String ATT_SRC           = "src";// $NON-NLS-1$
     protected static final String ATT_STYLE         = "style";// $NON-NLS-1$
     protected static final String ATT_TYPE          = "type";// $NON-NLS-1$
     protected static final String ATT_IS_IMAGE      = "image";// $NON-NLS-1$
     protected static final String TAG_APPLET        = "applet";// $NON-NLS-1$
     protected static final String TAG_BASE          = "base";// $NON-NLS-1$
     protected static final String TAG_BGSOUND       = "bgsound";// $NON-NLS-1$
     protected static final String TAG_BODY          = "body";// $NON-NLS-1$
     protected static final String TAG_EMBED         = "embed";// $NON-NLS-1$
     protected static final String TAG_FRAME         = "frame";// $NON-NLS-1$
     protected static final String TAG_IFRAME        = "iframe";// $NON-NLS-1$
     protected static final String TAG_IMAGE         = "img";// $NON-NLS-1$
     protected static final String TAG_INPUT         = "input";// $NON-NLS-1$
     protected static final String TAG_LINK          = "link";// $NON-NLS-1$
     protected static final String TAG_OBJECT        = "object";// $NON-NLS-1$
     protected static final String TAG_SCRIPT        = "script";// $NON-NLS-1$
     protected static final String STYLESHEET        = "stylesheet";// $NON-NLS-1$
 
     protected static final String IE_UA             = "MSIE ([0-9]+.[0-9]+)";// $NON-NLS-1$
     protected static final Pattern IE_UA_PATTERN    = Pattern.compile(IE_UA);
     private   static final float IE_10                = 10.0f;
 
     // Cache of parsers - parsers must be re-usable
     private static final Map<String, HTMLParser> parsers = new ConcurrentHashMap<String, HTMLParser>(4);
 
     public static final String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$
 
     public static final String DEFAULT_PARSER =
         "org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser"; // $NON-NLS-1$
 
     /**
      * Protected constructor to prevent instantiation except from within
      * subclasses.
      */
     protected HTMLParser() {
     }
 
     public static final HTMLParser getParser() {
         return getParser(JMeterUtils.getPropDefault(PARSER_CLASSNAME, DEFAULT_PARSER));
     }
 
     public static final HTMLParser getParser(String htmlParserClassName) {
 
         // Is there a cached parser?
         HTMLParser pars = parsers.get(htmlParserClassName);
         if (pars != null) {
             log.debug("Fetched " + htmlParserClassName);
             return pars;
         }
 
         try {
             Object clazz = Class.forName(htmlParserClassName).newInstance();
             if (clazz instanceof HTMLParser) {
                 pars = (HTMLParser) clazz;
             } else {
                 throw new HTMLParseError(new ClassCastException(htmlParserClassName));
             }
         } catch (InstantiationException e) {
             throw new HTMLParseError(e);
         } catch (IllegalAccessException e) {
             throw new HTMLParseError(e);
         } catch (ClassNotFoundException e) {
             throw new HTMLParseError(e);
         }
         log.info("Created " + htmlParserClassName);
         if (pars.isReusable()) {
             parsers.put(htmlParserClassName, pars);// cache the parser
         }
 
         return pars;
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * URLs should not appear twice in the returned iterator.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
      * @param userAgent
      *            User Agent
      *
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param encoding Charset
      * @return an Iterator for the resource URLs
+     * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, String encoding) throws HTMLParseException {
         // The Set is used to ignore duplicated binary files.
         // Using a LinkedHashSet to avoid unnecessary overhead in iterating
         // the elements in the set later on. As a side-effect, this will keep
         // them roughly in order, which should be a better model of browser
         // behaviour.
 
         Collection<URLString> col = new LinkedHashSet<URLString>();
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(col),encoding);
 
         // An additional note on using HashSets to store URLs: I just
         // discovered that obtaining the hashCode of a java.net.URL implies
         // a domain-name resolution process. This means significant delays
         // can occur, even more so if the domain name is not resolvable.
         // Whether this can be a problem in practical situations I can't tell,
         // but
         // thought I'd keep a note just in case...
         // BTW, note that using a List and removing duplicates via scan
         // would not help, since URL.equals requires name resolution too.
         // The above problem has now been addressed with the URLString and
         // URLCollection classes.
 
     }
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
      * <p>
      * All URLs should be added to the Collection.
      * <p>
      * Malformed URLs can be reported to the caller by having the Iterator
      * return the corresponding RL String. Overall problems parsing the html
      * should be reported by throwing an HTMLParseException.
-     *
+     * <p>
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            URLCollection
      * @param encoding Charset
      * @return an Iterator for the resource URLs
+     * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public abstract Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection coll, String encoding)
             throws HTMLParseException;
 
     /**
      * Get the URLs for all the resources that a browser would automatically
      * download following the download of the HTML content, that is: images,
      * stylesheets, javascript files, applets, etc...
-     *
+     * <p>
      * N.B. The Iterator returns URLs, but the Collection will contain objects
      * of class URLString.
      *
      * @param userAgent
      *            User Agent
      * @param html
      *            HTML code
      * @param baseUrl
      *            Base URL from which the HTML code was obtained
      * @param coll
      *            Collection - will contain URLString objects, not URLs
      * @param encoding Charset
      * @return an Iterator for the resource URLs
+     * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, Collection<URLString> coll, String encoding) throws HTMLParseException {
         return getEmbeddedResourceURLs(userAgent, html, baseUrl, new URLCollection(coll), encoding);
     }
 
     /**
      * Parsers should over-ride this method if the parser class is re-usable, in
      * which case the class will be cached for the next getParser() call.
      *
      * @return true if the Parser is reusable
      */
     protected boolean isReusable() {
         return false;
     }
     
     /**
      * 
      * @param ieVersion Float IE version
      * @return true if IE version &lt; IE v10
      */
     protected final boolean isEnableConditionalComments(Float ieVersion) {
         if(ieVersion == null) {
             return false;
         }
         // Conditionnal comment have been dropped in IE10
         // http://msdn.microsoft.com/en-us/library/ie/hh801214%28v=vs.85%29.aspx
         return ieVersion.floatValue() < IE_10;
     }
     
     /**
      * 
      * @param userAgent User Agent
      * @return version null if not IE or the version after MSIE
      */
     protected Float extractIEVersion(String userAgent) {
         if(StringUtils.isEmpty(userAgent)) {
             log.info("userAgent is null");
             return null;
         }
         Matcher matcher = IE_UA_PATTERN.matcher(userAgent);
         String ieVersion = null;
         while (matcher.find()) {
             if (matcher.groupCount() > 0) {
                 ieVersion = matcher.group(1);
             } else {
                 ieVersion = matcher.group();
             }
             break;
         }
         if(ieVersion != null) {
             return Float.valueOf(ieVersion);
         } else {
             return null;
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
index 22bc379e6..bf55eb4e9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/HtmlParsingUtils.java
@@ -1,392 +1,403 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.io.ByteArrayInputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.PatternCacheLRU;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.w3c.dom.Document;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 
 // For Junit tests @see TestHtmlParsingUtils
 
 public final class HtmlParsingUtils {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * Private constructor to prevent instantiation.
      */
     private HtmlParsingUtils() {
     }
 
     /**
      * Check if anchor matches by checking against:
      * - protocol
      * - domain
      * - path
      * - parameter names
      *
      * @param newLink target to match
      * @param config pattern to match against
      *
      * @return true if target URL matches pattern URL
      */
     public static boolean isAnchorMatched(HTTPSamplerBase newLink, HTTPSamplerBase config)
     {
         String query = null;
         try {
             query = URLDecoder.decode(newLink.getQueryString(), "UTF-8"); // $NON-NLS-1$
         } catch (UnsupportedEncodingException e) {
             // UTF-8 unsupported? You must be joking!
             log.error("UTF-8 encoding not supported!");
             throw new Error("Should not happen: " + e.toString(), e);
         }
 
         final Arguments arguments = config.getArguments();
 
         final Perl5Matcher matcher = JMeterUtils.getMatcher();
         final PatternCacheLRU patternCache = JMeterUtils.getPatternCache();
 
         if (!isEqualOrMatches(newLink.getProtocol(), config.getProtocol(), matcher, patternCache)){
             return false;
         }
 
         final String domain = config.getDomain();
         if (domain != null && domain.length() > 0) {
             if (!isEqualOrMatches(newLink.getDomain(), domain, matcher, patternCache)){
                 return false;
             }
         }
 
         final String path = config.getPath();
         if (!newLink.getPath().equals(path)
                 && !matcher.matches(newLink.getPath(), patternCache.getPattern("[/]*" + path, // $NON-NLS-1$
                         Perl5Compiler.READ_ONLY_MASK))) {
             return false;
         }
 
         PropertyIterator iter = arguments.iterator();
         while (iter.hasNext()) {
             Argument item = (Argument) iter.next().getObjectValue();
             final String name = item.getName();
             if (query.indexOf(name + "=") == -1) { // $NON-NLS-1$
                 if (!(matcher.contains(query, patternCache.getPattern(name, Perl5Compiler.READ_ONLY_MASK)))) {
                     return false;
                 }
             }
         }
 
         return true;
     }
 
     /**
      * Arguments match if the input name matches the corresponding pattern name
      * and the input value matches the pattern value, where the matching is done
      * first using String equals, and then Regular Expression matching if the equals test fails.
      *
      * @param arg - input Argument
      * @param patternArg - pattern to match against
      * @return true if both name and value match
      */
     public static boolean isArgumentMatched(Argument arg, Argument patternArg) {
         final Perl5Matcher matcher = JMeterUtils.getMatcher();
         final PatternCacheLRU patternCache = JMeterUtils.getPatternCache();
         return
             isEqualOrMatches(arg.getName(), patternArg.getName(), matcher, patternCache)
         &&
             isEqualOrMatches(arg.getValue(), patternArg.getValue(), matcher, patternCache);
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails.
      *
      * @param arg input string
      * @param pat pattern string
      * @param matcher Perl5Matcher
      * @param cache PatternCache
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatches(String arg, String pat, Perl5Matcher matcher, PatternCacheLRU cache){
         return
             arg.equals(pat)
             ||
             matcher.matches(arg,cache.getPattern(pat,Perl5Compiler.READ_ONLY_MASK));
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails
      * using case-insenssitive matching.
      *
      * @param arg input string
      * @param pat pattern string
      * @param matcher Perl5Matcher
      * @param cache PatternCache
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatchesCaseBlind(String arg, String pat, Perl5Matcher matcher, PatternCacheLRU cache){
         return
             arg.equalsIgnoreCase(pat)
             ||
             matcher.matches(arg,cache.getPattern(pat,Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK));
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails
      * using case-insensitive matching.
      *
      * @param arg input string
      * @param pat pattern string
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatches(String arg, String pat){
         return isEqualOrMatches(arg, pat, JMeterUtils.getMatcher(), JMeterUtils.getPatternCache());
     }
 
     /**
      * Match the input argument against the pattern using String.equals() or pattern matching if that fails
      * using case-insensitive matching.
      *
      * @param arg input string
      * @param pat pattern string
      *
      * @return true if input matches the pattern
      */
     public static boolean isEqualOrMatchesCaseBlind(String arg, String pat){
         return isEqualOrMatchesCaseBlind(arg, pat, JMeterUtils.getMatcher(), JMeterUtils.getPatternCache());
     }
 
     /**
      * Returns <code>tidy</code> as HTML parser.
      *
      * @return a <code>tidy</code> HTML parser
      */
     public static Tidy getParser() {
         log.debug("Start : getParser1");
         Tidy tidy = new Tidy();
         tidy.setInputEncoding("UTF8");
         tidy.setOutputEncoding("UTF8");
         tidy.setQuiet(true);
         tidy.setShowWarnings(false);
 
         if (log.isDebugEnabled()) {
             log.debug("getParser1 : tidy parser created - " + tidy);
         }
 
         log.debug("End : getParser1");
 
         return tidy;
     }
 
     /**
      * Returns a node representing a whole xml given an xml document.
      *
      * @param text
      *            an xml document
      * @return a node representing a whole xml
      */
     public static Node getDOM(String text) {
         log.debug("Start : getDOM1");
 
         try {
             Node node = getParser().parseDOM(new ByteArrayInputStream(text.getBytes("UTF-8")), null);// $NON-NLS-1$
 
             if (log.isDebugEnabled()) {
                 log.debug("node : " + node);
             }
 
             log.debug("End : getDOM1");
 
             return node;
         } catch (UnsupportedEncodingException e) {
             log.error("getDOM1 : Unsupported encoding exception - " + e);
             log.debug("End : getDOM1");
             throw new RuntimeException("UTF-8 encoding failed", e);
         }
     }
 
     public static Document createEmptyDoc() {
         return Tidy.createEmptyDocument();
     }
 
     /**
      * Create a new Sampler based on an HREF string plus a contextual URL
      * object. Given that an HREF string might be of three possible forms, some
      * processing is required.
+     *
+     * @param parsedUrlString
+     *            the url from the href
+     * @param context
+     *            the context in which the href was found. This is used to
+     *            extract url information that might be missing in
+     *            <code>parsedUrlString</code>
+     * @return sampler with filled in information about the fully parsed url
+     * @throws MalformedURLException
+     *             when the given url (<code>parsedUrlString</code> plus
+     *             <code>context</code> is malformed)
      */
     public static HTTPSamplerBase createUrlFromAnchor(String parsedUrlString, URL context) throws MalformedURLException {
         if (log.isDebugEnabled()) {
             log.debug("Creating URL from Anchor: " + parsedUrlString + ", base: " + context);
         }
         URL url = ConversionUtils.makeRelativeURL(context, parsedUrlString);
         HTTPSamplerBase sampler =HTTPSamplerFactory.newInstance();
         sampler.setDomain(url.getHost());
         sampler.setProtocol(url.getProtocol());
         sampler.setPort(url.getPort());
         sampler.setPath(url.getPath());
         sampler.parseArguments(url.getQuery());
 
         return sampler;
     }
 
     public static List<HTTPSamplerBase> createURLFromForm(Node doc, URL context) {
         String selectName = null;
         LinkedList<HTTPSamplerBase> urlConfigs = new LinkedList<HTTPSamplerBase>();
         recurseForm(doc, urlConfigs, context, selectName, false);
         /*
          * NamedNodeMap atts = formNode.getAttributes();
          * if(atts.getNamedItem("action") == null) { throw new
          * MalformedURLException(); } String action =
          * atts.getNamedItem("action").getNodeValue(); UrlConfig url =
          * createUrlFromAnchor(action, context); recurseForm(doc, url,
          * selectName,true,formStart);
          */
         return urlConfigs;
     }
 
     // N.B. Since the tags are extracted from an HTML Form, any values must already have been encoded
     private static boolean recurseForm(Node tempNode, LinkedList<HTTPSamplerBase> urlConfigs, URL context, String selectName,
             boolean inForm) {
         NamedNodeMap nodeAtts = tempNode.getAttributes();
         String tag = tempNode.getNodeName();
         try {
             if (inForm) {
                 HTTPSamplerBase url = urlConfigs.getLast();
                 if (tag.equalsIgnoreCase("form")) { // $NON-NLS-1$
                     try {
                         urlConfigs.add(createFormUrlConfig(tempNode, context));
                     } catch (MalformedURLException e) {
                         inForm = false;
                     }
                 } else if (tag.equalsIgnoreCase("input")) { // $NON-NLS-1$
                     url.addEncodedArgument(getAttributeValue(nodeAtts, "name"),  // $NON-NLS-1$
                             getAttributeValue(nodeAtts, "value")); // $NON-NLS-1$
                 } else if (tag.equalsIgnoreCase("textarea")) { // $NON-NLS-1$
                     try {
                         url.addEncodedArgument(getAttributeValue(nodeAtts, "name"),  // $NON-NLS-1$
                                 tempNode.getFirstChild().getNodeValue());
                     } catch (NullPointerException e) {
                         url.addArgument(getAttributeValue(nodeAtts, "name"), ""); // $NON-NLS-1$
                     }
                 } else if (tag.equalsIgnoreCase("select")) { // $NON-NLS-1$
                     selectName = getAttributeValue(nodeAtts, "name"); // $NON-NLS-1$
                 } else if (tag.equalsIgnoreCase("option")) { // $NON-NLS-1$
                     String value = getAttributeValue(nodeAtts, "value"); // $NON-NLS-1$
                     if (value == null) {
                         try {
                             value = tempNode.getFirstChild().getNodeValue();
                         } catch (NullPointerException e) {
                             value = ""; // $NON-NLS-1$
                         }
                     }
                     url.addEncodedArgument(selectName, value);
                 }
             } else if (tag.equalsIgnoreCase("form")) { // $NON-NLS-1$
                 try {
                     urlConfigs.add(createFormUrlConfig(tempNode, context));
                     inForm = true;
                 } catch (MalformedURLException e) {
                     inForm = false;
                 }
             }
         } catch (Exception ex) {
             log.warn("Some bad HTML " + printNode(tempNode), ex);
         }
         NodeList childNodes = tempNode.getChildNodes();
         for (int x = 0; x < childNodes.getLength(); x++) {
             inForm = recurseForm(childNodes.item(x), urlConfigs, context, selectName, inForm);
         }
         return inForm;
     }
 
     private static String getAttributeValue(NamedNodeMap att, String attName) {
         try {
             return att.getNamedItem(attName).getNodeValue();
         } catch (Exception ex) {
             return ""; // $NON-NLS-1$
         }
     }
 
     private static String printNode(Node node) {
         StringBuilder buf = new StringBuilder();
         buf.append("<"); // $NON-NLS-1$
         buf.append(node.getNodeName());
         NamedNodeMap atts = node.getAttributes();
         for (int x = 0; x < atts.getLength(); x++) {
             buf.append(" "); // $NON-NLS-1$
             buf.append(atts.item(x).getNodeName());
             buf.append("=\""); // $NON-NLS-1$
             buf.append(atts.item(x).getNodeValue());
             buf.append("\""); // $NON-NLS-1$
         }
 
         buf.append(">"); // $NON-NLS-1$
 
         return buf.toString();
     }
 
     private static HTTPSamplerBase createFormUrlConfig(Node tempNode, URL context) throws MalformedURLException {
         NamedNodeMap atts = tempNode.getAttributes();
         if (atts.getNamedItem("action") == null) { // $NON-NLS-1$
             throw new MalformedURLException();
         }
         String action = atts.getNamedItem("action").getNodeValue(); // $NON-NLS-1$
         return createUrlFromAnchor(action, context);
     }
 
     public static void extractStyleURLs(final URL baseUrl, final URLCollection urls, String styleTagStr) {
         Perl5Matcher matcher = JMeterUtils.getMatcher();
         Pattern pattern = JMeterUtils.getPatternCache().getPattern(
                 "URL\\(\\s*('|\")(.*)('|\")\\s*\\)", // $NON-NLS-1$
                 Perl5Compiler.CASE_INSENSITIVE_MASK | Perl5Compiler.SINGLELINE_MASK | Perl5Compiler.READ_ONLY_MASK);
         PatternMatcherInput input = null;
         input = new PatternMatcherInput(styleTagStr);
         while (matcher.contains(input, pattern)) {
             MatchResult match = matcher.getMatch();
             // The value is in the second group
             String styleUrl = match.group(2);
             urls.addURL(styleUrl, baseUrl);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
index f7689d04c..d91e9778a 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/JsoupBasedHtmlParser.java
@@ -1,163 +1,163 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Iterator;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 //import org.apache.jorphan.logging.LoggingManager;
 //import org.apache.log.Logger;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.Node;
 import org.jsoup.select.NodeTraversor;
 import org.jsoup.select.NodeVisitor;
 
 /**
  * Parser based on JSOUP
  * @since 2.10
  * TODO Factor out common code between {@link LagartoBasedHtmlParser} and this one (adapter pattern)
  */
 public class JsoupBasedHtmlParser extends HTMLParser {
 //    private static final Logger log = LoggingManager.getLoggerForClass();
 
     /*
      * A dummy class to pass the pointer of URL.
      */
     private static class URLPointer {
         private URLPointer(URL newUrl) {
             url = newUrl;
         }
         private URL url;
     }
 
     private static final class JMeterNodeVisitor implements NodeVisitor {
 
         private URLCollection urls;
         private URLPointer baseUrl;
 
         /**
-         * @param baseUrl
-         * @param urls
+         * @param baseUrl base url to extract possibly missing information from urls found in <code>urls</code>
+         * @param urls collection of urls to consider
          */
         public JMeterNodeVisitor(final URLPointer baseUrl, URLCollection urls) {
             this.urls = urls;
             this.baseUrl = baseUrl;
         }
 
         private final void extractAttribute(Element tag, String attributeName) {
             String url = tag.attr(attributeName);
             if (!StringUtils.isEmpty(url)) {
                 urls.addURL(url, baseUrl.url);
             }
         }
 
         @Override
         public void head(Node node, int depth) {
             if (!(node instanceof Element)) {
                 return;
             }
             Element tag = (Element) node;
             String tagName = tag.tagName().toLowerCase();
             if (tagName.equals(TAG_BODY)) {
                 extractAttribute(tag, ATT_BACKGROUND);
             } else if (tagName.equals(TAG_SCRIPT)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_BASE)) {
                 String baseref = tag.attr(ATT_HREF);
                 try {
                     if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
                     {
                         baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref);
                     }
                 } catch (MalformedURLException e1) {
                     throw new RuntimeException(e1);
                 }
             } else if (tagName.equals(TAG_IMAGE)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_APPLET)) {
                 extractAttribute(tag, ATT_CODE);
             } else if (tagName.equals(TAG_OBJECT)) {
                 extractAttribute(tag, ATT_CODEBASE);
                 extractAttribute(tag, ATT_DATA);
             } else if (tagName.equals(TAG_INPUT)) {
                 // we check the input tag type for image
                 if (ATT_IS_IMAGE.equalsIgnoreCase(tag.attr(ATT_TYPE))) {
                     // then we need to download the binary
                     extractAttribute(tag, ATT_SRC);
                 }
             } else if (tagName.equals(TAG_SCRIPT)) {
                 extractAttribute(tag, ATT_SRC);
                 // Bug 51750
             } else if (tagName.equals(TAG_FRAME) || tagName.equals(TAG_IFRAME)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_EMBED)) {
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_BGSOUND)){
                 extractAttribute(tag, ATT_SRC);
             } else if (tagName.equals(TAG_LINK)) {
                 // Putting the string first means it works even if the attribute is null
                 if (STYLESHEET.equalsIgnoreCase(tag.attr(ATT_REL))) {
                     extractAttribute(tag, ATT_HREF);
                 }
             } else {
                 extractAttribute(tag, ATT_BACKGROUND);
             }
 
 
             // Now look for URLs in the STYLE attribute
             String styleTagStr = tag.attr(ATT_STYLE);
             if(styleTagStr != null) {
                 HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr);
             }
         }
 
         @Override
         public void tail(Node arg0, int arg1) {
             // Noop
         }
     }
 
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
             // TODO Handle conditional comments for IE
             String contents = new String(html,encoding);
             Document doc = Jsoup.parse(contents);
             JMeterNodeVisitor nodeVisitor = new JMeterNodeVisitor(new URLPointer(baseUrl), coll);
             new NodeTraversor(nodeVisitor).traverse(doc);
             return coll.iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.parser.HTMLParser#isReusable()
      */
     @Override
     protected boolean isReusable() {
         return true;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
index aaf1482c5..6ef938276 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
@@ -1,242 +1,242 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.Stack;
 
 import jodd.lagarto.EmptyTagVisitor;
 import jodd.lagarto.LagartoException;
 import jodd.lagarto.LagartoParser;
 import jodd.lagarto.LagartoParserConfig;
 import jodd.lagarto.Tag;
 import jodd.lagarto.TagType;
 import jodd.lagarto.TagUtil;
 import jodd.lagarto.dom.HtmlCCommentExpressionMatcher;
 import jodd.log.LoggerFactory;
 import jodd.log.impl.Slf4jLoggerFactory;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Parser based on Lagarto
  * @since 2.10
  */
 public class LagartoBasedHtmlParser extends HTMLParser {
     private static final Logger log = LoggingManager.getLoggerForClass();
     static {
         LoggerFactory.setLoggerFactory(new Slf4jLoggerFactory());
     }
 
     /*
      * A dummy class to pass the pointer of URL.
      */
     private static class URLPointer {
         private URLPointer(URL newUrl) {
             url = newUrl;
         }
         private URL url;
     }
     
     private static final class JMeterTagVisitor extends EmptyTagVisitor {
         private HtmlCCommentExpressionMatcher htmlCCommentExpressionMatcher;
         private URLCollection urls;
         private URLPointer baseUrl;
         private Float ieVersion;
         private Stack<Boolean> enabled = new Stack<Boolean>();
 
         /**
-         * @param baseUrl 
-         * @param urls 
-         * @param ieVersion 
+         * @param baseUrl base url to add possibly missing information to urls found in <code>urls</code>
+         * @param urls collection of urls to consider
+         * @param ieVersion version number of IE to emulate
          */
         public JMeterTagVisitor(final URLPointer baseUrl, URLCollection urls, Float ieVersion) {
             this.urls = urls;
             this.baseUrl = baseUrl;
             this.ieVersion = ieVersion;
         }
 
         private final void extractAttribute(Tag tag, String attributeName) {
             CharSequence url = tag.getAttributeValue(attributeName);
             if (!StringUtils.isEmpty(url)) {
                 urls.addURL(url.toString(), baseUrl.url);
             }
         }
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#script(jodd.lagarto.Tag,
          * java.lang.CharSequence)
          */
         @Override
         public void script(Tag tag, CharSequence body) {
             if (!enabled.peek()) {
                 return;
             }
             extractAttribute(tag, ATT_SRC);
         }
 
         /*
          * (non-Javadoc)
          * 
          * @see jodd.lagarto.EmptyTagVisitor#tag(jodd.lagarto.Tag)
          */
         @Override
         public void tag(Tag tag) {
             if (!enabled.peek()) {
                 return;
             }
             TagType tagType = tag.getType();
             switch (tagType) {
             case START:
             case SELF_CLOSING:
                 if (tag.nameEquals(TAG_BODY)) {
                     extractAttribute(tag, ATT_BACKGROUND);
                 } else if (tag.nameEquals(TAG_BASE)) {
                     CharSequence baseref = tag.getAttributeValue(ATT_HREF);
                     try {
                         if (!StringUtils.isEmpty(baseref))// Bugzilla 30713
                         {
                             baseUrl.url = ConversionUtils.makeRelativeURL(baseUrl.url, baseref.toString());
                         }
                     } catch (MalformedURLException e1) {
                         throw new RuntimeException(e1);
                     }
                 } else if (tag.nameEquals(TAG_IMAGE)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_APPLET)) {
                     extractAttribute(tag, ATT_CODE);
                 } else if (tag.nameEquals(TAG_OBJECT)) {
                     extractAttribute(tag, ATT_CODEBASE);                
                     extractAttribute(tag, ATT_DATA);                 
                 } else if (tag.nameEquals(TAG_INPUT)) {
                     // we check the input tag type for image
                     CharSequence type = tag.getAttributeValue(ATT_TYPE);
                     if (type != null && TagUtil.equalsIgnoreCase(ATT_IS_IMAGE, type)) {
                         // then we need to download the binary
                         extractAttribute(tag, ATT_SRC);
                     }
                 } else if (tag.nameEquals(TAG_SCRIPT)) {
                     extractAttribute(tag, ATT_SRC);
                     // Bug 51750
                 } else if (tag.nameEquals(TAG_FRAME) || tag.nameEquals(TAG_IFRAME)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_EMBED)) {
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_BGSOUND)){
                     extractAttribute(tag, ATT_SRC);
                 } else if (tag.nameEquals(TAG_LINK)) {
                     CharSequence relAttribute = tag.getAttributeValue(ATT_REL);
                     // Putting the string first means it works even if the attribute is null
                     if (relAttribute != null && TagUtil.equalsIgnoreCase(STYLESHEET,relAttribute)) {
                         extractAttribute(tag, ATT_HREF);
                     }
                 } else {
                     extractAttribute(tag, ATT_BACKGROUND);
                 }
     
     
                 // Now look for URLs in the STYLE attribute
                 CharSequence styleTagStr = tag.getAttributeValue(ATT_STYLE);
                 if(!StringUtils.isEmpty(styleTagStr)) {
                     HtmlParsingUtils.extractStyleURLs(baseUrl.url, urls, styleTagStr.toString());
                 }
                 break;
             case END:
                 break;
             }
         }
 
         /* (non-Javadoc)
          * @see jodd.lagarto.EmptyTagVisitor#condComment(java.lang.CharSequence, boolean, boolean, boolean)
          */
         @Override
         public void condComment(CharSequence expression, boolean isStartingTag,
                 boolean isHidden, boolean isHiddenEndTag) {
             // See http://css-tricks.com/how-to-create-an-ie-only-stylesheet/
             if(!isStartingTag) {
                 enabled.pop();
             } else {
                 if (htmlCCommentExpressionMatcher == null) {
                     htmlCCommentExpressionMatcher = new HtmlCCommentExpressionMatcher();
                 }
                 String expressionString = expression.toString().trim();
                 enabled.push(Boolean.valueOf(htmlCCommentExpressionMatcher.match(ieVersion.floatValue(),
                         expressionString)));                
             }
         }
 
         /* (non-Javadoc)
          * @see jodd.lagarto.EmptyTagVisitor#start()
          */
         @Override
         public void start() {
             super.start();
             enabled.clear();
             enabled.push(Boolean.TRUE);
         }
     }
 
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
             URLCollection coll, String encoding) throws HTMLParseException {
         try {
             Float ieVersion = extractIEVersion(userAgent);
             
             String contents = new String(html,encoding); 
             // As per Jodd javadocs, emitStrings should be false for visitor for better performances
             LagartoParser lagartoParser = new LagartoParser(contents, false);
             LagartoParserConfig<LagartoParserConfig<?>> config = new LagartoParserConfig<LagartoParserConfig<?>>();
             config.setCaseSensitive(false);
             // Conditional comments only apply for IE < 10
             config.setEnableConditionalComments(isEnableConditionalComments(ieVersion));
             
             lagartoParser.setConfig(config);
             JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll, ieVersion);
             lagartoParser.parse(tagVisitor);
             return coll.iterator();
         } catch (LagartoException e) {
             // TODO is it the best way ? https://issues.apache.org/bugzilla/show_bug.cgi?id=55634
             if(log.isDebugEnabled()) {
                 log.debug("Error extracting embedded resource URLs from:'"+baseUrl+"', probably not text content, message:"+e.getMessage());
             }
             return Collections.<URL>emptyList().iterator();
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
     }
 
     
 
 
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.parser.HTMLParser#isReusable()
      */
     @Override
     protected boolean isReusable() {
         return true;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
index 49f16cfdd..eadcf9379 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/RegexpHTMLParser.java
@@ -1,206 +1,205 @@
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Iterator;
 
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 //NOTE: Also looked at using Java 1.4 regexp instead of ORO. The change was
 //trivial. Performance did not improve -- at least not significantly.
 //Finally decided for ORO following advise from Stefan Bodewig (message
 //to jmeter-dev dated 25 Nov 2003 8:52 CET) [Jordi]
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.MatchResult;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * HtmlParser implementation using regular expressions.
  * <p>
  * This class will find RLs specified in the following ways (where <b>url</b>
  * represents the RL being found:
  * <ul>
  * <li>&lt;img src=<b>url</b> ... &gt;
  * <li>&lt;script src=<b>url</b> ... &gt;
  * <li>&lt;applet code=<b>url</b> ... &gt;
  * <li>&lt;input type=image src=<b>url</b> ... &gt;
  * <li>&lt;body background=<b>url</b> ... &gt;
  * <li>&lt;table background=<b>url</b> ... &gt;
  * <li>&lt;td background=<b>url</b> ... &gt;
  * <li>&lt;tr background=<b>url</b> ... &gt;
  * <li>&lt;applet ... codebase=<b>url</b> ... &gt;
  * <li>&lt;embed src=<b>url</b> ... &gt;
  * <li>&lt;embed codebase=<b>url</b> ... &gt;
  * <li>&lt;object codebase=<b>url</b> ... &gt;
  * <li>&lt;link rel=stylesheet href=<b>url</b>... gt;
  * <li>&lt;bgsound src=<b>url</b> ... &gt;
  * <li>&lt;frame src=<b>url</b> ... &gt;
  * </ul>
  *
  * <p>
  * This class will take into account the following construct:
  * <ul>
  * <li>&lt;base href=<b>url</b>&gt;
  * </ul>
  *
  * <p>
  * But not the following:
  * <ul>
  * <li>&lt; ... codebase=<b>url</b> ... &gt;
  * </ul>
  *
  */
 class RegexpHTMLParser extends HTMLParser {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * Regexp fragment matching a tag attribute's value (including the equals
      * sign and any spaces before it). Note it matches unquoted values, which to
      * my understanding, are not conformant to any of the HTML specifications,
      * but are still quite common in the web and all browsers seem to understand
      * them.
      */
     private static final String VALUE = "\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'\\s>\\\\][^\\s>]*)(?=[\\s>]))";
 
     // Note there's 3 capturing groups per value
 
     /**
      * Regexp fragment matching the separation between two tag attributes.
      */
     private static final String SEP = "\\s(?:[^>]*\\s)?";
 
     /**
      * Regular expression used against the HTML code to find the URIs of images,
      * etc.:
      */
     private static final String REGEXP =
               "<(?:" + "!--.*?-->"
             + "|BASE" + SEP + "HREF" + VALUE
             + "|(?:IMG|SCRIPT|FRAME|IFRAME|BGSOUND)" + SEP + "SRC" + VALUE
             + "|APPLET" + SEP + "CODE(?:BASE)?" + VALUE
             + "|(?:EMBED|OBJECT)" + SEP + "(?:SRC|CODEBASE|DATA)" + VALUE
             + "|(?:BODY|TABLE|TR|TD)" + SEP + "BACKGROUND" + VALUE
             + "|[^<]+?STYLE\\s*=['\"].*?URL\\(\\s*['\"](.+?)['\"]\\s*\\)"
             + "|INPUT(?:" + SEP + "(?:SRC" + VALUE
             + "|TYPE\\s*=\\s*(?:\"image\"|'image'|image(?=[\\s>])))){2,}"
             + "|LINK(?:" + SEP + "(?:HREF" + VALUE
             + "|REL\\s*=\\s*(?:\"stylesheet\"|'stylesheet'|stylesheet(?=[\\s>])))){2,}" + ")";
 
     // Number of capturing groups possibly containing Base HREFs:
     private static final int NUM_BASE_GROUPS = 3;
 
     /**
      * Thread-local input:
      */
     private static final ThreadLocal<PatternMatcherInput> localInput =
         new ThreadLocal<PatternMatcherInput>() {
         @Override
         protected PatternMatcherInput initialValue() {
             return new PatternMatcherInput(new char[0]);
         }
     };
 
     /**
      * {@inheritDoc}
      */
     @Override
     protected boolean isReusable() {
         return true;
     }
 
     /**
      * Make sure to compile the regular expression upon instantiation:
      */
     protected RegexpHTMLParser() {
         super();
     }
 
     /**
      * {@inheritDoc}
-     * @throws HTMLParseException 
      */
     @Override
     public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl, URLCollection urls, String encoding) throws HTMLParseException {
         Pattern pattern= null;
         Perl5Matcher matcher = null;
         try {
             matcher = JMeterUtils.getMatcher();
             PatternMatcherInput input = localInput.get();
             // TODO: find a way to avoid the cost of creating a String here --
             // probably a new PatternMatcherInput working on a byte[] would do
             // better.
             input.setInput(new String(html, encoding)); 
             pattern=JMeterUtils.getPatternCache().getPattern(
                     REGEXP,
                     Perl5Compiler.CASE_INSENSITIVE_MASK
                     | Perl5Compiler.SINGLELINE_MASK
                     | Perl5Compiler.READ_ONLY_MASK);
 
             while (matcher.contains(input, pattern)) {
                 MatchResult match = matcher.getMatch();
                 String s;
                 if (log.isDebugEnabled()) {
                     log.debug("match groups " + match.groups() + " " + match.toString());
                 }
                 // Check for a BASE HREF:
                 for (int g = 1; g <= NUM_BASE_GROUPS && g <= match.groups(); g++) {
                     s = match.group(g);
                     if (s != null) {
                         if (log.isDebugEnabled()) {
                             log.debug("new baseUrl: " + s + " - " + baseUrl.toString());
                         }
                         try {
                             baseUrl = ConversionUtils.makeRelativeURL(baseUrl, s);
                         } catch (MalformedURLException e) {
                             // Doesn't even look like a URL?
                             // Maybe it isn't: Ignore the exception.
                             if (log.isDebugEnabled()) {
                                 log.debug("Can't build base URL from RL " + s + " in page " + baseUrl, e);
                             }
                         }
                     }
                 }
                 for (int g = NUM_BASE_GROUPS + 1; g <= match.groups(); g++) {
                     s = match.group(g);
                     if (s != null) {
                         if (log.isDebugEnabled()) {
                             log.debug("group " + g + " - " + match.group(g));
                         }
                         urls.addURL(s, baseUrl);
                     }
                 }
             }
             return urls.iterator();
         } catch (UnsupportedEncodingException e) {
             throw new HTMLParseException(e.getMessage(), e);
         } catch (MalformedCachePatternException e) {
             throw new HTMLParseException(e.getMessage(), e);
         } finally {
             JMeterUtils.clearMatcherMemory(matcher, pattern);
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java b/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
index 9d22f7850..0c69a1883 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/parser/URLCollection.java
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
 
 package org.apache.jmeter.protocol.http.parser;
 
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Collection;
 import java.util.Iterator;
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Collection class designed for handling URLs
- *
+ * <p>
  * Before a URL is added to the collection, it is wrapped in a URLString class.
  * The iterator unwraps the URL before return.
- *
+ * <p>
  * N.B. Designed for use by HTMLParser, so is not a full implementation - e.g.
  * does not support remove()
  *
  */
 public class URLCollection {
     private static final Logger log = LoggingManager.getLoggerForClass();
     private final Collection<URLString> coll;
 
     /**
      * Creates a new URLCollection from an existing Collection
      *
+     * @param c collection to start with
      */
     public URLCollection(Collection<URLString> c) {
         coll = c;
     }
 
     /**
      * Adds the URL to the Collection, first wrapping it in the URLString class
      *
      * @param u
      *            URL to add
      * @return boolean condition returned by the add() method of the underlying
      *         collection
      */
     public boolean add(URL u) {
         return coll.add(new URLString(u));
     }
 
     /**
-     * Convenience method for adding URLs to the collection If the url parameter
-     * is null, empty or URL is malformed, nothing is done
+     * Convenience method for adding URLs to the collection. If the url
+     * parameter is <code>null</code>, empty or URL is malformed, nothing is
+     * done
      *
      * @param url
      *            String, may be null or empty
      * @param baseUrl
+     *            base for <code>url</code> to add information, which might be
+     *            missing in <code>url</code>
      * @return boolean condition returned by the add() method of the underlying
      *         collection
      */
     public boolean addURL(String url, URL baseUrl) {
         if (url == null || url.length() == 0) {
             return false;
         }
         //url.replace('+',' ');
         url=StringEscapeUtils.unescapeXml(url);
         boolean b = false;
         try {
             b = this.add(ConversionUtils.makeRelativeURL(baseUrl, url));
         } catch (MalformedURLException mfue) {
             // No WARN message to avoid performance impact
             if(log.isDebugEnabled()) {
                 log.debug("Error occured building relative url for:"+url+", message:"+mfue.getMessage());
             }
             // No point in adding the URL as String as it will result in null 
             // returned during iteration, see URLString
             // See https://issues.apache.org/bugzilla/show_bug.cgi?id=55092
             return false;
         }
         return b;
     }
 
     public Iterator<URL> iterator() {
         return new UrlIterator(coll.iterator());
     }
 
     /*
      * Private iterator used to unwrap the URL from the URLString class
      *
      */
     private static class UrlIterator implements Iterator<URL> {
         private final Iterator<URLString> iter;
 
         UrlIterator(Iterator<URLString> i) {
             iter = i;
         }
 
         @Override
         public boolean hasNext() {
             return iter.hasNext();
         }
 
         /*
          * Unwraps the URLString class to return the URL
          */
         @Override
         public URL next() {
             return iter.next().getURL();
         }
 
         @Override
         public void remove() {
             throw new UnsupportedOperationException();
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java
index 5f5b9d8c7..7ca22dfc6 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/Daemon.java
@@ -1,159 +1,164 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.IOException;
 import java.io.InterruptedIOException;
 import java.net.ServerSocket;
 import java.net.Socket;
+import java.net.SocketException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.jmeter.gui.Stoppable;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Web daemon thread. Creates main socket on port 8080 and listens on it
  * forever. For each client request, creates a Proxy thread to handle the
  * request.
  *
  */
 public class Daemon extends Thread implements Stoppable {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * The time (in milliseconds) to wait when accepting a client connection.
      * The accept will be retried until the Daemon is told to stop. So this
      * interval is the longest time that the Daemon will have to wait after
      * being told to stop.
      */
     private static final int ACCEPT_TIMEOUT = 1000;
 
     /** The port to listen on. */
     private final int daemonPort;
 
     private final ServerSocket mainSocket;
 
     /** True if the Daemon is currently running. */
     private volatile boolean running;
 
     /** The target which will receive the generated JMeter test components. */
     private final ProxyControl target;
 
     /**
      * The proxy class which will be used to handle individual requests. This
      * class must be the {@link Proxy} class or a subclass.
      */
     private final Class<? extends Proxy> proxyClass;
 
     /**
      * Create a new Daemon with the specified port and target.
      *
      * @param port
      *            the port to listen on.
      * @param target
      *            the target which will receive the generated JMeter test
      *            components.
-     * @throws IOException 
+     * @throws IOException if an I/O error occurs opening the socket
+     * @throws IllegalArgumentException if <code>port</code> is outside the allowed range from <code>0</code> to <code>65535</code>
+     * @throws SocketException when something is wrong on the underlying protocol layer
      */
     public Daemon(int port, ProxyControl target) throws IOException {
         this(port, target, Proxy.class);
     }
 
     /**
      * Create a new Daemon with the specified port and target, using the
      * specified class to handle individual requests.
      *
      * @param port
      *            the port to listen on.
      * @param target
      *            the target which will receive the generated JMeter test
      *            components.
      * @param proxyClass
      *            the proxy class to use to handle individual requests. This
      *            class must be the {@link Proxy} class or a subclass.
-     * @throws IOException 
+     * @throws IOException if an I/O error occurs opening the socket
+     * @throws IllegalArgumentException if <code>port</code> is outside the allowed range from <code>0</code> to <code>65535</code>
+     * @throws SocketException when something is wrong on the underlying protocol layer
      */
     public Daemon(int port, ProxyControl target, Class<? extends Proxy> proxyClass) throws IOException {
         super("HTTP Proxy Daemon");
         this.target = target;
         this.daemonPort = port;
         this.proxyClass = proxyClass;
         log.info("Creating Daemon Socket on port: " + daemonPort);
         mainSocket = new ServerSocket(daemonPort);
         mainSocket.setSoTimeout(ACCEPT_TIMEOUT);
     }
 
     /**
      * Listen on the daemon port and handle incoming requests. This method will
      * not exit until {@link #stopServer()} is called or an error occurs.
      */
     @Override
     public void run() {
         running = true;
         log.info("Test Script Recorder up and running!");
 
         // Maps to contain page and form encodings
         // TODO - do these really need to be shared between all Proxy instances?
         Map<String, String> pageEncodings = Collections.synchronizedMap(new HashMap<String, String>());
         Map<String, String> formEncodings = Collections.synchronizedMap(new HashMap<String, String>());
 
         try {
             while (running) {
                 try {
                     // Listen on main socket
                     Socket clientSocket = mainSocket.accept();
                     if (running) {
                         // Pass request to new proxy thread
                         Proxy thd = proxyClass.newInstance();
                         thd.configure(clientSocket, target, pageEncodings, formEncodings);
                         thd.start();
                     }
                 } catch (InterruptedIOException e) {
                     continue;
                     // Timeout occurred. Ignore, and keep looping until we're
                     // told to stop running.
                 }
             }
             log.info("HTTP(S) Test Script Recorder stopped");
         } catch (Exception e) {
             log.warn("HTTP(S) Test Script Recorder stopped", e);
         } finally {
             JOrphanUtils.closeQuietly(mainSocket);
         }
 
         // Clear maps
         pageEncodings = null;
         formEncodings = null;
     }
 
     /**
      * Stop the proxy daemon. The daemon may not stop immediately.
      *
      * see #ACCEPT_TIMEOUT
      */
     @Override
     public void stopServer() {
         running = false;
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java
index 8c4478815..20f78caf1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/FormCharSetFinder.java
@@ -1,135 +1,135 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.util.Map;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.htmlparser.Node;
 import org.htmlparser.Parser;
 import org.htmlparser.Tag;
 import org.htmlparser.tags.CompositeTag;
 import org.htmlparser.tags.FormTag;
 import org.htmlparser.util.NodeIterator;
 import org.htmlparser.util.ParserException;
 
 /**
  * A parser for html, to find the form tags, and their accept-charset value
  */
 // made public see Bug 49976
 public class FormCharSetFinder {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     static {
         log.info("Using htmlparser version: "+Parser.getVersion());
     }
 
     public FormCharSetFinder() {
         super();
     }
 
     /**
      * Add form action urls and their corresponding encodings for all forms on the page
      *
      * @param html the html to parse for form encodings
      * @param formEncodings the Map where form encodings should be added
      * @param pageEncoding the encoding used for the whole page
-     * @throws HTMLParseException
+     * @throws HTMLParseException when parsing the <code>html</code> fails
      */
     public void addFormActionsAndCharSet(String html, Map<String, String> formEncodings, String pageEncoding)
             throws HTMLParseException {
         if (log.isDebugEnabled()) {
             log.debug("Parsing html of: " + html);
         }
 
         Parser htmlParser = null;
         try {
             htmlParser = new Parser();
             htmlParser.setInputHTML(html);
         } catch (Exception e) {
             throw new HTMLParseException(e);
         }
 
         // Now parse the DOM tree
         try {
             // we start to iterate through the elements
             parseNodes(htmlParser.elements(), formEncodings, pageEncoding);
             log.debug("End   : parseNodes");
         } catch (ParserException e) {
             throw new HTMLParseException(e);
         }
     }
 
     /**
      * Recursively parse all nodes to pick up all form encodings
      *
      * @param e the nodes to be parsed
      * @param formEncodings the Map where we should add form encodings found
      * @param pageEncoding the encoding used for the page where the nodes are present
      */
     private void parseNodes(final NodeIterator e, Map<String, String> formEncodings, String pageEncoding)
         throws HTMLParseException, ParserException {
         while(e.hasMoreNodes()) {
             Node node = e.nextNode();
             // a url is always in a Tag.
             if (!(node instanceof Tag)) {
                 continue;
             }
             Tag tag = (Tag) node;
 
             // Only check form tags
             if (tag instanceof FormTag) {
                 // Find the action / form url
                 String action = tag.getAttribute("action");
                 String acceptCharSet = tag.getAttribute("accept-charset");
                 if(action != null && action.length() > 0) {
                     // We use the page encoding where the form resides, as the
                     // default encoding for the form
                     String formCharSet = pageEncoding;
                     // Check if we found an accept-charset attribute on the form
                     if(acceptCharSet != null) {
                         String[] charSets = JOrphanUtils.split(acceptCharSet, ",");
                         // Just use the first one of the possible many charsets
                         if(charSets.length > 0) {
                             formCharSet = charSets[0].trim();
                             if(formCharSet.length() == 0) {
                                 formCharSet = null;
                             }
                         }
                     }
                     if(formCharSet != null) {
                         synchronized (formEncodings) {
                             formEncodings.put(action, formCharSet);
                         }
                     }
                 }
             }
 
             // second, if the tag was a composite tag,
             // recursively parse its children.
             if (tag instanceof CompositeTag) {
                 CompositeTag composite = (CompositeTag) tag;
                 parseNodes(composite.elements(), formEncodings, pageEncoding);
             }
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
index 476853159..e39a98db7 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/HttpRequestHdr.java
@@ -1,458 +1,459 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.StringTokenizer;
 
 import org.apache.commons.lang3.CharUtils;
 import org.apache.jmeter.protocol.http.config.MultipartUrlConfig;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.gui.HeaderPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 //For unit tests, @see TestHttpRequestHdr
 
 /**
  * The headers of the client HTTP request.
  *
  */
 public class HttpRequestHdr {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String HTTP = "http"; // $NON-NLS-1$
     private static final String HTTPS = "https"; // $NON-NLS-1$
     private static final String PROXY_CONNECTION = "proxy-connection"; // $NON-NLS-1$
     public static final String CONTENT_TYPE = "content-type"; // $NON-NLS-1$
     public static final String CONTENT_LENGTH = "content-length"; // $NON-NLS-1$
 
 
     /**
      * Http Request method, uppercased, e.g. GET or POST.
      */
     private String method = ""; // $NON-NLS-1$
 
     /** CONNECT url. */
     private String paramHttps = ""; // $NON-NLS-1$
 
     /**
      * The requested url. The universal resource locator that hopefully uniquely
      * describes the object or service the client is requesting.
      */
     private String url = ""; // $NON-NLS-1$
 
     /**
      * Version of http being used. Such as HTTP/1.0.
      */
     private String version = ""; // NOTREAD // $NON-NLS-1$
 
     private byte[] rawPostData;
 
     private final Map<String, Header> headers = new HashMap<String, Header>();
 
     private final String httpSamplerName;
 
     private HeaderManager headerManager;
 
     private String firstLine; // saved copy of first line for error reports
 
     public HttpRequestHdr() {
         this.httpSamplerName = ""; // $NON-NLS-1$
         this.firstLine = "" ; // $NON-NLS-1$
     }
 
     /**
      * @param httpSamplerName the http sampler name
      */
     public HttpRequestHdr(String httpSamplerName) {
         this.httpSamplerName = httpSamplerName;
     }
 
     /**
      * Parses a http header from a stream.
      *
      * @param in
      *            the stream to parse.
      * @return array of bytes from client.
+     * @throws IOException when reading the input stream fails
      */
     public byte[] parse(InputStream in) throws IOException {
         boolean inHeaders = true;
         int readLength = 0;
         int dataLength = 0;
         boolean firstLine = true;
         ByteArrayOutputStream clientRequest = new ByteArrayOutputStream();
         ByteArrayOutputStream line = new ByteArrayOutputStream();
         int x;
         while ((inHeaders || readLength < dataLength) && ((x = in.read()) != -1)) {
             line.write(x);
             clientRequest.write(x);
             if (firstLine && !CharUtils.isAscii((char) x)){// includes \n
                 throw new IllegalArgumentException("Only ASCII supported in headers (perhaps SSL was used?)");
             }
             if (inHeaders && (byte) x == (byte) '\n') { // $NON-NLS-1$
                 if (line.size() < 3) {
                     inHeaders = false;
                     firstLine = false; // cannot be first line either
                 }
                 final String reqLine = line.toString();
                 if (firstLine) {
                     parseFirstLine(reqLine);
                     firstLine = false;
                 } else {
                     // parse other header lines, looking for Content-Length
                     final int contentLen = parseLine(reqLine);
                     if (contentLen > 0) {
                         dataLength = contentLen; // Save the last valid content length one
                     }
                 }
                 if (log.isDebugEnabled()){
                     log.debug("Client Request Line: '" + reqLine.replaceFirst("\r\n$", "<CRLF>") + "'");
                 }
                 line.reset();
             } else if (!inHeaders) {
                 readLength++;
             }
         }
         // Keep the raw post data
         rawPostData = line.toByteArray();
 
         if (log.isDebugEnabled()){
             log.debug("rawPostData in default JRE encoding: " + new String(rawPostData)); // TODO - charset?
             log.debug("Request: '" + clientRequest.toString().replaceAll("\r\n", "<CRLF>") + "'");
         }
         return clientRequest.toByteArray();
     }
 
     private void parseFirstLine(String firstLine) {
         this.firstLine = firstLine;
         if (log.isDebugEnabled()) {
             log.debug("browser request: " + firstLine.replaceFirst("\r\n$", "<CRLF>"));
         }
         StringTokenizer tz = new StringTokenizer(firstLine);
         method = getToken(tz).toUpperCase(java.util.Locale.ENGLISH);
         url = getToken(tz);
         version = getToken(tz);
         if (log.isDebugEnabled()) {
             log.debug("parsed method:   " + method);
             log.debug("parsed url/host: " + url); // will be host:port for CONNECT
             log.debug("parsed version:  " + version);
         }
         // SSL connection
         if (getMethod().startsWith(HTTPConstants.CONNECT)) {
             paramHttps = url;
             return; // Don't try to adjust the host name
         }
         /* The next line looks odd, but proxied HTTP requests look like:
          * GET http://www.apache.org/foundation/ HTTP/1.1
          * i.e. url starts with "http:", not "/"
          * whereas HTTPS proxy requests look like:
          * CONNECT www.google.co.uk:443 HTTP/1.1
          * followed by
          * GET /?gws_rd=cr HTTP/1.1
          */
         if (url.startsWith("/")) { // it must be a proxied HTTPS request
             url = HTTPS + "://" + paramHttps + url; // $NON-NLS-1$
         }
         // JAVA Impl accepts URLs with unsafe characters so don't do anything
         if(HTTPSamplerFactory.IMPL_JAVA.equals(httpSamplerName)) {
             log.debug("First Line url: " + url);
             return;
         }
         try {
             // See Bug 54482
             URI testCleanUri = new URI(url);
             if(log.isDebugEnabled()) {
                 log.debug("Successfully built URI from url:"+url+" => " + testCleanUri.toString());
             }
         } catch (URISyntaxException e) {
             log.warn("Url '" + url + "' contains unsafe characters, will escape it, message:"+e.getMessage());
             try {
                 String escapedUrl = ConversionUtils.escapeIllegalURLCharacters(url);
                 if(log.isDebugEnabled()) {
                     log.debug("Successfully escaped url:'"+url +"' to:'"+escapedUrl+"'");
                 }
                 url = escapedUrl;
             } catch (Exception e1) {
                 log.error("Error escaping URL:'"+url+"', message:"+e1.getMessage());
             }
         }
         log.debug("First Line url: " + url);
     }
 
     /*
      * Split line into name/value pairs and store in headers if relevant
      * If name = "content-length", then return value as int, else return 0
      */
     private int parseLine(String nextLine) {
         int colon = nextLine.indexOf(':');
         if (colon <= 0){
             return 0; // Nothing to do
         }
         String name = nextLine.substring(0, colon).trim();
         String value = nextLine.substring(colon+1).trim();
         headers.put(name.toLowerCase(java.util.Locale.ENGLISH), new Header(name, value));
         if (name.equalsIgnoreCase(CONTENT_LENGTH)) {
             return Integer.parseInt(value);
         }
         return 0;
     }
 
     private HeaderManager createHeaderManager() {
         HeaderManager manager = new HeaderManager();
         for (String key : headers.keySet()) {
             if (!key.equals(PROXY_CONNECTION)
              && !key.equals(CONTENT_LENGTH)
              && !key.equalsIgnoreCase(HTTPConstants.HEADER_CONNECTION)) {
                 manager.add(headers.get(key));
             }
         }
         manager.setName(JMeterUtils.getResString("header_manager_title")); // $NON-NLS-1$
         manager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
         manager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
         return manager;
     }
 
     public HeaderManager getHeaderManager() {
         if(headerManager == null) {
             headerManager = createHeaderManager();
         }
         return headerManager;
     }
 
     public String getContentType() {
         Header contentTypeHeader = headers.get(CONTENT_TYPE);
         if (contentTypeHeader != null) {
             return contentTypeHeader.getValue();
         }
         return null;
     }
 
     private boolean isMultipart(String contentType) {
         if (contentType != null && contentType.startsWith(HTTPConstants.MULTIPART_FORM_DATA)) {
             return true;
         }
         return false;
     }
 
     public MultipartUrlConfig getMultipartConfig(String contentType) {
         if(isMultipart(contentType)) {
             // Get the boundary string for the multiparts from the content type
             String boundaryString = contentType.substring(contentType.toLowerCase(java.util.Locale.ENGLISH).indexOf("boundary=") + "boundary=".length());
             return new MultipartUrlConfig(boundaryString);
         }
         return null;
     }
 
     //
     // Parsing Methods
     //
 
     /**
      * Find the //server.name from an url.
      *
      * @return server's internet name
      */
     public String serverName() {
         // chop to "server.name:x/thing"
         String str = url;
         int i = str.indexOf("//"); // $NON-NLS-1$
         if (i > 0) {
             str = str.substring(i + 2);
         }
         // chop to server.name:xx
         i = str.indexOf('/'); // $NON-NLS-1$
         if (0 < i) {
             str = str.substring(0, i);
         }
         // chop to server.name
         i = str.lastIndexOf(':'); // $NON-NLS-1$
         if (0 < i) {
             str = str.substring(0, i);
         }
         // Handle IPv6 urls
         if(str.startsWith("[")&& str.endsWith("]")) {
             return str.substring(1, str.length()-1);
         }
         return str;
     }
 
     // TODO replace repeated substr() above and below with more efficient method.
 
     /**
      * Find the :PORT from http://server.ect:PORT/some/file.xxx
      *
      * @return server's port (or UNSPECIFIED if not found)
      */
     public int serverPort() {
         String str = url;
         // chop to "server.name:x/thing"
         int i = str.indexOf("//");
         if (i > 0) {
             str = str.substring(i + 2);
         }
         // chop to server.name:xx
         i = str.indexOf('/');
         if (0 < i) {
             str = str.substring(0, i);
         }
         // chop to server.name
         i = str.lastIndexOf(':');
         if (0 < i) {
             return Integer.parseInt(str.substring(i + 1).trim());
         }
         return HTTPSamplerBase.UNSPECIFIED_PORT;
     }
 
     /**
      * Find the /some/file.xxxx from http://server.ect:PORT/some/file.xxx
      *
      * @return the path
      */
     public String getPath() {
         String str = url;
         int i = str.indexOf("//");
         if (i > 0) {
             str = str.substring(i + 2);
         }
         i = str.indexOf('/');
         if (i < 0) {
             return "";
         }
         return str.substring(i);
     }
 
     /**
      * Returns the url string extracted from the first line of the client request.
      *
      * @return the url
      */
     public String getUrl(){
         return url;
     }
 
     /**
      * Returns the method string extracted from the first line of the client request.
      *
      * @return the method (will always be upper case)
      */
     public String getMethod(){
         return method;
     }
 
     public String getFirstLine() {
         return firstLine;
     }
 
     /**
      * Returns the next token in a string.
      *
      * @param tk
      *            String that is partially tokenized.
      * @return The remainder
      */
     private String getToken(StringTokenizer tk) {
         if (tk.hasMoreTokens()) {
             return tk.nextToken();
         }
         return "";// $NON-NLS-1$
     }
 
 //    /**
 //     * Returns the remainder of a tokenized string.
 //     *
 //     * @param tk
 //     *            String that is partially tokenized.
 //     * @return The remainder
 //     */
 //    private String getRemainder(StringTokenizer tk) {
 //        StringBuilder strBuff = new StringBuilder();
 //        if (tk.hasMoreTokens()) {
 //            strBuff.append(tk.nextToken());
 //        }
 //        while (tk.hasMoreTokens()) {
 //            strBuff.append(" "); // $NON-NLS-1$
 //            strBuff.append(tk.nextToken());
 //        }
 //        return strBuff.toString();
 //    }
 
     public String getUrlWithoutQuery(URL _url) {
         String fullUrl = _url.toString();
         String urlWithoutQuery = fullUrl;
         String query = _url.getQuery();
         if(query != null) {
             // Get rid of the query and the ?
             urlWithoutQuery = urlWithoutQuery.substring(0, urlWithoutQuery.length() - query.length() - 1);
         }
         return urlWithoutQuery;
     }
 
     /**
      * @return the httpSamplerName
      */
     public String getHttpSamplerName() {
         return httpSamplerName;
     }
 
     /**
      * @return byte[] Raw post data
      */
     public byte[] getRawPostData() {
         return rawPostData;
     }
 
     /**
      * @param sampler {@link HTTPSamplerBase}
      * @return String Protocol (http or https)
      */
     public String getProtocol(HTTPSamplerBase sampler) {
         if (url.indexOf("//") > -1) {
             String protocol = url.substring(0, url.indexOf(':'));
             if (log.isDebugEnabled()) {
                 log.debug("Proxy: setting protocol to : " + protocol);
             }
             return protocol;
         } else if (sampler.getPort() == HTTPConstants.DEFAULT_HTTPS_PORT) {
             if (log.isDebugEnabled()) {
                 log.debug("Proxy: setting protocol to https");
             }
             return HTTPS;
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Proxy setting default protocol to: http");
             }
             return HTTP;
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java
index a69307eab..1f55d08d8 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/AccessLogSampler.java
@@ -1,357 +1,363 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.util.accesslog.Filter;
 import org.apache.jmeter.protocol.http.util.accesslog.LogParser;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestCloneable;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.log.Logger;
 
 /**
  * Description: <br>
  * <br>
  * AccessLogSampler is responsible for a couple of things:
  * <p>
  * <ul>
  * <li>creating instances of Generator
  * <li>creating instances of Parser
  * <li>triggering popup windows
  * <li>calling Generator.generateRequest()
  * <li>checking to make sure the classes are valid
  * <li>making sure a class can be instantiated
  * </ul>
  * The intent of this sampler is it uses the generator and parser to create a
  * HTTPSampler when it is needed. It does not contain logic about how to parse
  * the logs. It also doesn't care how Generator is implemented, as long as it
  * implements the interface. This means a person could simply implement a dummy
  * parser to generate random parameters and the generator consumes the results.
  * This wasn't the original intent of the sampler. I originaly wanted to write
  * this sampler, so that I can take production logs to simulate production
  * traffic in a test environment. Doing so is desirable to study odd or unusual
  * behavior. It's also good to compare a new system against an existing system
  * to get near apples- to-apples comparison. I've been asked if benchmarks are
  * really fair comparisons just about every single time, so this helps me
  * accomplish that task.
  * <p>
  * Some bugs only appear under production traffic, so it is useful to generate
  * traffic using production logs. This way, JMeter can record when problems
  * occur and provide a way to match the server logs.
  * <p>
  * Created on: Jun 26, 2003
  *
  */
 public class AccessLogSampler extends HTTPSampler implements TestBean,ThreadListener {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 232L; // Remember to change this when the class changes ...
 
     public static final String DEFAULT_CLASS = "org.apache.jmeter.protocol.http.util.accesslog.TCLogParser"; // $NON-NLS-1$
 
     /** private members used by class * */
     private transient LogParser PARSER = null;
 
     // NOTUSED private Class PARSERCLASS = null;
     private String logFile, parserClassName, filterClassName;
 
     private transient Filter filter;
 
     private int count = 0;
 
     private boolean started = false;
 
     /**
      * Set the path where XML messages are stored for random selection.
+     *
+     * @param path path where to store XML messages
      */
     public void setLogFile(String path) {
         logFile = path;
     }
 
     /**
      * Get the path where XML messages are stored. this is the directory where
      * JMeter will randomly select a file.
+     *
+     * @return path where XML messages are stored
      */
     public String getLogFile() {
         return logFile;
     }
 
     /**
      * it's kinda obvious, but we state it anyways. Set the xml file with a
      * string path.
      *
      * @param classname -
      *            parser class name
      */
     public void setParserClassName(String classname) {
         parserClassName = classname;
     }
 
     /**
      * Get the file location of the xml file.
      *
      * @return String file path.
      */
     public String getParserClassName() {
         return parserClassName;
     }
 
     /**
      * sample gets a new HTTPSampler from the generator and calls it's sample()
      * method.
+     *
+     * @return newly generated and called sample
      */
     public SampleResult sampleWithParser() {
         initFilter();
         instantiateParser();
         SampleResult res = null;
         try {
 
             if (PARSER == null) {
                 throw new JMeterException("No Parser available");
             }
             /*
              * samp.setDomain(this.getDomain()); samp.setPort(this.getPort());
              */
             // we call parse with 1 to get only one.
             // this also means if we change the implementation
             // to use 2, it would use every other entry and
             // so on. Not that it is really useful, but a
             // person could use it that way if they have a
             // huge gigabyte log file and they only want to
             // use a quarter of the entries.
             int thisCount = PARSER.parseAndConfigure(1, this);
             if (thisCount < 0) // Was there an error?
             {
                 return errorResult(new Error("Problem parsing the log file"), new HTTPSampleResult());
             }
             if (thisCount == 0) {
                 if (count == 0 || filter == null) {
                     log.info("Stopping current thread");
                     JMeterContextService.getContext().getThread().stop();
                 }
                 if (filter != null) {
                     filter.reset();
                 }
                 CookieManager cm = getCookieManager();
                 if (cm != null) {
                     cm.clear();
                 }
                 count = 0;
                 return errorResult(new Error("No entries found"), new HTTPSampleResult());
             }
             count = thisCount;
             res = sample();
             res.setSampleLabel(toString());
         } catch (Exception e) {
             log.warn("Sampling failure", e);
             return errorResult(e, new HTTPSampleResult());
         }
         return res;
     }
 
     /**
      * sample(Entry e) simply calls sample().
      *
      * @param e -
      *            ignored
      * @return the new sample
      */
     @Override
     public SampleResult sample(Entry e) {
         return sampleWithParser();
     }
 
     /**
      * Method will instantiate the log parser based on the class in the text
      * field. This was done to make it easier for people to plugin their own log
      * parser and use different log parser.
      */
     public void instantiateParser() {
         if (PARSER == null) {
             try {
                 if (this.getParserClassName() != null && this.getParserClassName().length() > 0) {
                     if (this.getLogFile() != null && this.getLogFile().length() > 0) {
                         PARSER = (LogParser) Class.forName(getParserClassName()).newInstance();
                         PARSER.setSourceFile(this.getLogFile());
                         PARSER.setFilter(filter);
                     } else {
                         log.error("No log file specified");
                     }
                 }
             } catch (InstantiationException e) {
                 log.error("", e);
             } catch (IllegalAccessException e) {
                 log.error("", e);
             } catch (ClassNotFoundException e) {
                 log.error("", e);
             }
         }
     }
 
     /**
      * @return Returns the filterClassName.
      */
     public String getFilterClassName() {
         return filterClassName;
     }
 
     /**
      * @param filterClassName
      *            The filterClassName to set.
      */
     public void setFilterClassName(String filterClassName) {
         this.filterClassName = filterClassName;
     }
 
     /**
      * @return Returns the domain.
      */
     @Override
     public String getDomain() { // N.B. Must be in this class for the TestBean code to work
         return super.getDomain();
     }
 
     /**
      * @param domain
      *            The domain to set.
      */
     @Override
     public void setDomain(String domain) { // N.B. Must be in this class for the TestBean code to work
         super.setDomain(domain);
     }
 
     /**
      * @return Returns the imageParsing.
      */
     public boolean isImageParsing() {
         return super.isImageParser();
     }
 
     /**
      * @param imageParsing
      *            The imageParsing to set.
      */
     public void setImageParsing(boolean imageParsing) {
         super.setImageParser(imageParsing);
     }
 
     /**
      * @return Returns the port.
      */
     public String getPortString() {
         return super.getPropertyAsString(HTTPSamplerBase.PORT);
     }
 
     /**
      * @param port
      *            The port to set.
      */
     public void setPortString(String port) {
         super.setProperty(HTTPSamplerBase.PORT, port);
     }
 
     /**
      *
      */
     public AccessLogSampler() {
         super();
     }
 
     protected void initFilter() {
         if (filter == null && filterClassName != null && filterClassName.length() > 0) {
             try {
                 filter = (Filter) Class.forName(filterClassName).newInstance();
             } catch (Exception e) {
                 log.warn("Couldn't instantiate filter '" + filterClassName + "'", e);
             }
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         AccessLogSampler s = (AccessLogSampler) super.clone();
         if (started) {
             if (filterClassName != null && filterClassName.length() > 0) {
 
                 try {
                     if (TestCloneable.class.isAssignableFrom(Class.forName(filterClassName))) {
                         initFilter();
                         s.filter = (Filter) ((TestCloneable) filter).clone();
                     }
                     if(TestCloneable.class.isAssignableFrom(Class.forName(parserClassName)))
                     {
                         instantiateParser();
                         s.PARSER = (LogParser)((TestCloneable)PARSER).clone();
                         if(filter != null)
                         {
                             s.PARSER.setFilter(s.filter);
                         }
                     }
                 } catch (Exception e) {
                     log.warn("Could not clone cloneable filter", e);
                 }
             }
         }
         return s;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         if (PARSER != null) {
             PARSER.close();
         }
         filter = null;
         started = false;
         super.testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
         started = true;
         super.testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void threadFinished() {
         if(PARSER instanceof ThreadListener) {
             ((ThreadListener)PARSER).threadFinished();
         }
         if(filter instanceof ThreadListener) {
             ((ThreadListener)filter).threadFinished();
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index f92316a75..692c863f0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1981 +1,1992 @@
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
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.ThreadPoolExecutor;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.TimeoutException;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.DNSCacheManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.parser.HTMLParser;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Common constants and methods for HTTP samplers
  *
  */
 public abstract class HTTPSamplerBase extends AbstractSampler
     implements TestStateListener, TestIterationListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui",
                     "org.apache.jmeter.protocol.http.gui.HeaderPanel",
                     "org.apache.jmeter.protocol.http.control.DNSCacheManager",
                     "org.apache.jmeter.protocol.http.gui.DNSCachePanel",
                     "org.apache.jmeter.protocol.http.gui.AuthPanel",
                     "org.apache.jmeter.protocol.http.gui.CacheManagerGui",
                     "org.apache.jmeter.protocol.http.gui.CookiePanel"}));
     
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DNS_CACHE_MANAGER = "HTTPSampler.dns_cache_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     /** This is the encoding used for the content, i.e. the charset name, not the header "Content-Encoding" */
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = HTTPConstants.PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
 
     public static final String IP_SOURCE_TYPE = "HTTPSampler.ipSourceType"; // $NON-NLS-1$
 
     public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
 
     public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
 
     public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
     
     public static final String CONCURRENT_DWN = "HTTPSampler.concurrentDwn"; // $NON-NLS-1$
     
     public static final String CONCURRENT_POOL = "HTTPSampler.concurrentPool"; // $NON-NLS-1$
 
     private static final String CONCURRENT_POOL_DEFAULT = "4"; // default for concurrent pool (do not change)
     
     private static final String USER_AGENT = "User-Agent"; // $NON-NLS-1$
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
     
     private static final long KEEPALIVETIME = 0; // for Thread Pool for resources but no need to use a special value?
     
     private static final long AWAIT_TERMINATION_TIMEOUT = 
         JMeterUtils.getPropDefault("httpsampler.await_termination_timeout", 60); // $NON-NLS-1$ // default value: 60 secs 
     
     private static final boolean IGNORE_FAILED_EMBEDDED_RESOURCES = 
             JMeterUtils.getPropDefault("httpsampler.ignore_failed_embedded_resources", false); // $NON-NLS-1$ // default value: false
 
     public static final int CONCURRENT_POOL_SIZE = 4; // Default concurrent pool size for download embedded resources
 
     public static enum SourceType {
         HOSTNAME("web_testing_source_ip_hostname"), //$NON-NLS-1$
         DEVICE("web_testing_source_ip_device"), //$NON-NLS-1$
         DEVICE_IPV4("web_testing_source_ip_device_ipv4"), //$NON-NLS-1$
         DEVICE_IPV6("web_testing_source_ip_device_ipv6"); //$NON-NLS-1$
         
         public final String propertyName;
         SourceType(String propertyName) {
             this.propertyName = propertyName;
         }
     }
 
     private static final int SOURCE_TYPE_DEFAULT = HTTPSamplerBase.SourceType.HOSTNAME.ordinal();
 
     // Use for ComboBox Source Address Type. Preserve order (specially with localization)
     public static final String[] getSourceTypeList() {
         final SourceType[] types = SourceType.values();
         final String[] displayStrings = new String[types.length];
         for(int i = 0; i < types.length; i++) {
             displayStrings[i] = JMeterUtils.getResString(types[i].propertyName);
         }
         return displayStrings;
     }
 
     public static final String DEFAULT_METHOD = HTTPConstants.GET; // $NON-NLS-1$
     // Supported methods:
     private static final String [] METHODS = {
         DEFAULT_METHOD, // i.e. GET
         HTTPConstants.POST,
         HTTPConstants.HEAD,
         HTTPConstants.PUT,
         HTTPConstants.OPTIONS,
         HTTPConstants.TRACE,
         HTTPConstants.DELETE,
         HTTPConstants.PATCH,
         HTTPConstants.PROPFIND,
         HTTPConstants.PROPPATCH,
         HTTPConstants.MKCOL,
         HTTPConstants.COPY,
         HTTPConstants.MOVE,
         HTTPConstants.LOCK,
         HTTPConstants.UNLOCK
         };
 
     private static final List<String> METHODLIST = Collections.unmodifiableList(Arrays.asList(METHODS));
 
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private static final String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
     // MIMETYPE is kept for backward compatibility with old test plans
     private static final String MIMETYPE = "HTTPSampler.mimetype"; // $NON-NLS-1$
     // FILE_NAME is kept for backward compatibility with old test plans
     private static final String FILE_NAME = "HTTPSampler.FILE_NAME"; // $NON-NLS-1$
     /* Shown as Parameter Name on the GUI */
     // FILE_FIELD is kept for backward compatibility with old test plans
     private static final String FILE_FIELD = "HTTPSampler.FILE_FIELD"; // $NON-NLS-1$
 
     public static final String CONTENT_TYPE = "HTTPSampler.CONTENT_TYPE"; // $NON-NLS-1$
 
     // IMAGE_PARSER now really means EMBEDDED_PARSER
     public static final String IMAGE_PARSER = "HTTPSampler.image_parser"; // $NON-NLS-1$
 
     // Embedded URLs must match this RE (if provided)
     public static final String EMBEDDED_URL_RE = "HTTPSampler.embedded_url_re"; // $NON-NLS-1$
 
     public static final String MONITOR = "HTTPSampler.monitor"; // $NON-NLS-1$
 
     // Store MD5 hash instead of storing response
     private static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere 
 
     public static final boolean POST_BODY_RAW_DEFAULT = false;
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 5); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> parsersForType = new HashMap<String, String>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS= // list of parsers
         JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
     static{
         String []parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (int i=0;i<parsers.length;i++){
             final String parser = parsers[i];
             String classname=JMeterUtils.getProperty(parser+".className");//$NON-NLS-1$
             if (classname == null){
                 log.info("Cannot find .className property for "+parser+", using default");
                 classname="";
             }
             String typelist=JMeterUtils.getProperty(parser+".types");//$NON-NLS-1$
             if (typelist != null){
                 String []types=JOrphanUtils.split(typelist, " " , true);
                 for (int j=0;j<types.length;j++){
                     final String type = types[j];
                     log.info("Parser for "+type+" is "+classname);
                     parsersForType.put(type,classname);
                 }
             } else {
                 log.warn("Cannot find .types property for "+parser);
             }
         }
         if (parsers.length==0){ // revert to previous behaviour
             parsersForType.put("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
             log.info("No response parsers defined: text/html only will be scanned for embedded resources");
         }
         
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static final boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Content body,
      * i.e. without any additional wrapping.
      *
      * @return true if specified file is to be sent as the body,
      * i.e. there is a single file entry which has a non-empty path and
      * an empty Parameter name.
      */
     public boolean getSendFileAsPostBody() {
         // If there is one file with no parameter name, the file will
         // be sent as post body.
         HTTPFileArg[] files = getHTTPFiles();
         return (files.length == 1)
             && (files[0].getPath().length() > 0)
             && (files[0].getParamName().length() == 0);
     }
 
     /**
      * Determine if none of the parameters have a name, and if that
      * is the case, it means that the parameter values should be sent
      * as the entity body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         if(getPostBodyRaw()) {
             return true;
         } else {
             boolean noArgumentsHasName = true;
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 if(arg.getName() != null && arg.getName().length() > 0) {
                     noArgumentsHasName = false;
                     break;
                 }
             }
             return noArgumentsHasName;
         }
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost(){
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         if(HTTPConstants.POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
             return true;
         }
         return false;
     }
 
     public void setProtocol(String value) {
         setProperty(PROTOCOL, value.toLowerCase(java.util.Locale.ENGLISH));
     }
 
     /**
      * Gets the protocol, with default.
      *
      * @return the protocol
      */
     public String getProtocol() {
         String protocol = getPropertyAsString(PROTOCOL);
         if (protocol == null || protocol.length() == 0 ) {
             return DEFAULT_PROTOCOL;
         }
         return protocol;
     }
 
     /**
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      */
     public void setPath(String path) {
         // We know that URL arguments should always be encoded in UTF-8 according to spec
         setPath(path, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Sets the PATH property; if the request is a GET or DELETE (and the path
      * does not start with http[s]://) it also calls {@link #parseArguments(String, String)}
      * to extract and store any query arguments.
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         boolean fullUrl = path.startsWith(HTTP_PREFIX) || path.startsWith(HTTPS_PREFIX); 
         if (!fullUrl && (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod()))) {
             int index = path.indexOf(QRY_PFX);
             if (index > -1) {
                 setProperty(PATH, path.substring(0, index));
                 // Parse the arguments in querystring, assuming specified encoding for values
                 parseArguments(path.substring(index + 1), contentEncoding);
             } else {
                 setProperty(PATH, path);
             }
         } else {
             setProperty(PATH, path);
         }
     }
 
     public String getPath() {
         String p = getPropertyAsString(PATH);
         return encodeSpaces(p);
     }
 
     public void setFollowRedirects(boolean value) {
         setProperty(new BooleanProperty(FOLLOW_REDIRECTS, value));
     }
 
     public boolean getFollowRedirects() {
         return getPropertyAsBoolean(FOLLOW_REDIRECTS);
     }
 
     public void setAutoRedirects(boolean value) {
         setProperty(new BooleanProperty(AUTO_REDIRECTS, value));
     }
 
     public boolean getAutoRedirects() {
         return getPropertyAsBoolean(AUTO_REDIRECTS);
     }
 
     public void setMethod(String value) {
         setProperty(METHOD, value);
     }
 
     public String getMethod() {
         return getPropertyAsString(METHOD);
     }
 
     /**
      * Sets the value of the encoding to be used for the content.
      * 
      * @param charsetName the name of the encoding to be used
      */
     public void setContentEncoding(String charsetName) {
         setProperty(CONTENT_ENCODING, charsetName);
     }
 
     /**
      * 
      * @return the encoding of the content, i.e. its charset name
      */
     public String getContentEncoding() {
         return getPropertyAsString(CONTENT_ENCODING);
     }
 
     public void setUseKeepAlive(boolean value) {
         setProperty(new BooleanProperty(USE_KEEPALIVE, value));
     }
 
     public boolean getUseKeepAlive() {
         return getPropertyAsBoolean(USE_KEEPALIVE);
     }
 
     public void setDoMultipartPost(boolean value) {
         setProperty(new BooleanProperty(DO_MULTIPART_POST, value));
     }
 
     public boolean getDoMultipartPost() {
         return getPropertyAsBoolean(DO_MULTIPART_POST, false);
     }
 
     public void setDoBrowserCompatibleMultipart(boolean value) {
         setProperty(BROWSER_COMPATIBLE_MULTIPART, value, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public boolean getDoBrowserCompatibleMultipart() {
         return getPropertyAsBoolean(BROWSER_COMPATIBLE_MULTIPART, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public void setMonitor(String value) {
         this.setProperty(MONITOR, value);
     }
 
     public void setMonitor(boolean truth) {
         this.setProperty(MONITOR, truth);
     }
 
     public String getMonitor() {
         return this.getPropertyAsString(MONITOR);
     }
 
     public boolean isMonitor() {
         return this.getPropertyAsBoolean(MONITOR);
     }
 
     public void setImplementation(String value) {
         this.setProperty(IMPLEMENTATION, value);
     }
 
     public String getImplementation() {
         return this.getPropertyAsString(IMPLEMENTATION);
     }
 
     public boolean useMD5() {
         return this.getPropertyAsBoolean(MD5, false);
     }
 
    public void setMD5(boolean truth) {
         this.setProperty(MD5, truth, false);
     }
 
     /**
      * Add an argument which has already been encoded
+     *
+     * @param name name of the argument
+     * @param value value of the argument
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     /**
      * Creates an HTTPArgument and adds it to the current set {@link #getArguments()} of arguments.
      * 
      * @param name - the parameter name
      * @param value - the parameter value
      * @param metaData - normally just '='
      * @param contentEncoding - the encoding, may be null
      */
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()){
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg = null;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if(nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(nonEmptyEncoding) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             }
             catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         }
         else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName()) && arg.getValue().equals(valueEncoded)) {
             arg.setAlwaysEncoded(false);
         }
         this.getArguments().addArgument(arg);
     }
 
     public void addEncodedArgument(String name, String value, String metaData) {
         this.addEncodedArgument(name, value, metaData, null);
     }
 
     public void addNonEncodedArgument(String name, String value, String metadata) {
         HTTPArgument arg = new HTTPArgument(name, value, metadata, false);
         arg.setAlwaysEncoded(false);
         this.getArguments().addArgument(arg);
     }
 
     public void addArgument(String name, String value) {
         this.getArguments().addArgument(new HTTPArgument(name, value));
     }
 
     public void addArgument(String name, String value, String metadata) {
         this.getArguments().addArgument(new HTTPArgument(name, value, metadata));
     }
 
     public boolean hasArguments() {
         return getArguments().getArgumentCount() > 0;
     }
 
     @Override
     public void addTestElement(TestElement el) {
         if (el instanceof CookieManager) {
             setCookieManager((CookieManager) el);
         } else if (el instanceof CacheManager) {
             setCacheManager((CacheManager) el);
         } else if (el instanceof HeaderManager) {
             setHeaderManager((HeaderManager) el);
         } else if (el instanceof AuthManager) {
             setAuthManager((AuthManager) el);
         } else if (el instanceof DNSCacheManager) {
             setDNSResolver((DNSCacheManager) el);
         } else {
             super.addTestElement(el);
         }
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Clears the Header Manager property so subsequent loops don't keep merging more elements
      */
     @Override
     public void clearTestElementChildren(){
         removeProperty(HEADER_MANAGER);
     }
 
     public void setPort(int value) {
         setProperty(new IntegerProperty(PORT, value));
     }
 
     /**
      * Get the port number for a URL, applying defaults if necessary.
      * (Called by CookieManager.)
      * @param protocol from {@link URL#getProtocol()}
      * @param port number from {@link URL#getPort()}
      * @return the default port for the protocol
      */
     public static int getDefaultPort(String protocol,int port){
         if (port==URL_UNSPECIFIED_PORT){
             return
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)  ? HTTPConstants.DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS) ? HTTPConstants.DEFAULT_HTTPS_PORT :
                     port;
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String port_s = getPropertyAsString(PORT, UNSPECIFIED_PORT_AS_STRING);
         try {
             return Integer.parseInt(port_s.trim());
         } catch (NumberFormatException e) {
             return UNSPECIFIED_PORT;
         }
     }
 
     /**
      * Tell whether the default port for the specified protocol is used
      *
      * @return true if the default port number for the protocol is used, false otherwise
      */
     public boolean isProtocolDefaultPort() {
         final int port = getPortIfSpecified();
         final String protocol = getProtocol();
         if (port == UNSPECIFIED_PORT ||
                 (HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTP_PORT) ||
                 (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTPS_PORT)) {
             return true;
         }
         return false;
     }
 
     /**
      * Get the port; apply the default for the protocol if necessary.
      *
      * @return the port number, with default applied if required.
      */
     public int getPort() {
         final int port = getPortIfSpecified();
         if (port == UNSPECIFIED_PORT) {
             String prot = getProtocol();
             if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
             if (!HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
                 // TODO - should this return something else?
             }
             return HTTPConstants.DEFAULT_HTTP_PORT;
         }
         return port;
     }
 
     public void setDomain(String value) {
         setProperty(DOMAIN, value);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setConnectTimeout(String value) {
         setProperty(CONNECT_TIMEOUT, value, "");
     }
 
     public int getConnectTimeout() {
         return getPropertyAsInt(CONNECT_TIMEOUT, 0);
     }
 
     public void setResponseTimeout(String value) {
         setProperty(RESPONSE_TIMEOUT, value, "");
     }
 
     public int getResponseTimeout() {
         return getPropertyAsInt(RESPONSE_TIMEOUT, 0);
     }
 
     public String getProxyHost() {
         return getPropertyAsString(PROXYHOST);
     }
 
     public int getProxyPortInt() {
         return getPropertyAsInt(PROXYPORT, 0);
     }
 
     public String getProxyUser() {
         return getPropertyAsString(PROXYUSER);
     }
 
     public String getProxyPass() {
         return getPropertyAsString(PROXYPASS);
     }
 
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * @param value Boolean that indicates body will be sent as is
      */
     public void setPostBodyRaw(boolean value) {
         setProperty(POST_BODY_RAW, value, POST_BODY_RAW_DEFAULT);
     }
 
     /**
      * @return boolean that indicates body will be sent as is
      */
     public boolean getPostBodyRaw() {
         return getPropertyAsBoolean(POST_BODY_RAW, POST_BODY_RAW_DEFAULT);
     }
 
     public void setAuthManager(AuthManager value) {
         AuthManager mgr = getAuthManager();
         if (mgr != null) {
             log.warn("Existing AuthManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(AUTH_MANAGER, value));
     }
 
     public AuthManager getAuthManager() {
         return (AuthManager) getProperty(AUTH_MANAGER).getObjectValue();
     }
 
     public void setHeaderManager(HeaderManager value) {
         HeaderManager mgr = getHeaderManager();
         if (mgr != null) {
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                 for (int i=0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCookieManagerProperty(CookieManager value) {
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));        
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCookieManagerProperty(value);
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCacheManagerProperty(CacheManager value) {
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCacheManagerProperty(value);
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public DNSCacheManager getDNSResolver() {
         return (DNSCacheManager) getProperty(DNS_CACHE_MANAGER).getObjectValue();
     }
 
     public void setDNSResolver(DNSCacheManager cacheManager) {
         DNSCacheManager mgr = getDNSResolver();
         if (mgr != null) {
             log.warn("Existing DNSCacheManager " + mgr.getName() + " superseded by " + cacheManager.getName());
         }
         setProperty(new TestElementProperty(DNS_CACHE_MANAGER, cacheManager));
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER, false);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(IMAGE_PARSER, parseImages, false);
     }
 
     /**
      * Get the regular expression URLs must match.
      *
      * @return regular expression (or empty) string
      */
     public String getEmbeddedUrlRE() {
         return getPropertyAsString(EMBEDDED_URL_RE,"");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Populates the provided HTTPSampleResult with details from the Exception.
      * Does not create a new instance, so should not be used directly to add a subsample.
      * 
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult to be modified
      * @return the modified sampling result containing details of the Exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel("Error: " + res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     // Bug 51939
     private static final boolean SEPARATE_CONTAINER = 
             JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
-     * @throws MalformedURLException
+     * @throws MalformedURLException if url is malformed
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain=null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")){ // $NON-NLS-1$
                 pathAndQuery.append("/"); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if(queryString.length() > 0) {
                 if (path.indexOf(QRY_PFX) > -1) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if(isProtocolDefaultPort()) {
             return new URL(protocol, domain, pathAndQuery.toString());
         }
         return new URL(protocol, domain, getPort(), pathAndQuery.toString());
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using
      * UTF-8 to encode the URL
      *
      * @return the QueryString value
      */
     public String getQueryString() {
         // We use the encoding which should be used according to the HTTP spec, which is UTF-8
         return getQueryString(EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using the
      * specified encoding to encode the parameter values put into the URL
      *
      * @param contentEncoding the encoding to use for encoding parameter values
      * @return the QueryString value
      */
     public String getQueryString(String contentEncoding) {
          // Check if the sampler has a specified content encoding
          if(JOrphanUtils.isBlank(contentEncoding)) {
              // We use the encoding which should be used according to the HTTP spec, which is UTF-8
              contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
          }
         StringBuilder buf = new StringBuilder();
         PropertyIterator iter = getArguments().iterator();
         boolean first = true;
         while (iter.hasNext()) {
             HTTPArgument item = null;
             /*
              * N.B. Revision 323346 introduced the ClassCast check, but then used iter.next()
              * to fetch the item to be cast, thus skipping the element that did not cast.
              * Reverted to work more like the original code, but with the check in place.
              * Added a warning message so can track whether it is necessary
              */
             Object objectValue = iter.next().getObjectValue();
             try {
                 item = (HTTPArgument) objectValue;
             } catch (ClassCastException e) {
                 log.warn("Unexpected argument type: "+objectValue.getClass().getName());
                 item = new HTTPArgument((Argument) objectValue);
             }
             final String encodedName = item.getEncodedName();
             if (encodedName.length() == 0) {
                 continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
             }
             if (!first) {
                 buf.append(QRY_SEP);
             } else {
                 first = false;
             }
             buf.append(encodedName);
             if (item.getMetaData() == null) {
                 buf.append(ARG_VAL_SEP);
             } else {
                 buf.append(item.getMetaData());
             }
 
             // Encode the parameter value in the specified content encoding
             try {
                 buf.append(item.getEncodedValue(contentEncoding));
             }
             catch(UnsupportedEncodingException e) {
                 log.warn("Unable to encode parameter in encoding " + contentEncoding + ", parameter value not included in query string");
             }
         }
         return buf.toString();
     }
 
     // Mark Walsh 2002-08-03, modified to also parse a parameter name value
     // string, where string contains only the parameter name and no equal sign.
     /**
      * This method allows a proxy server to send over the raw text from a
      * browser's output stream to be parsed and stored correctly into the
      * UrlConfig object.
      *
      * For each name found, addArgument() is called
      *
      * @param queryString -
      *            the query string, might be the post body of a http post request.
      * @param contentEncoding -
      *            the content encoding of the query string; 
      *            if non-null then it is used to decode the 
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         final boolean isDebug = log.isDebugEnabled();
         for (int i = 0; i < args.length; i++) {
             if (isDebug) {
                 log.debug("Arg: " + args[i]);
             }
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existance of an equal sign
             String name;
             String value;
             int length = args[i].length();
             int endOfNameIndex = args[i].indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = args[i].substring(0, endOfNameIndex);
                 value = args[i].substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name=args[i];
                 value="";
             }
             if (name.length() > 0) {
                 if (isDebug) {
                     log.debug("Name: " + name+ " Value: " + value+ " Metadata: " + metaData);
                 }
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if(!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 }
                 else {
                     // If we do not know the encoding, we just use the encoded value
                     // The browser has already done the encoding, so save the values as is
                     addNonEncodedArgument(name, value, metaData);
                 }
             }
         }
     }
 
     public void parseArguments(String queryString) {
         // We do not know the content encoding of the query string
         parseArguments(queryString, null);
     }
 
     @Override
     public String toString() {
         try {
             StringBuilder stringBuffer = new StringBuilder();
             stringBuffer.append(this.getUrl().toString());
             // Append body if it is a post or put
             if(HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
                 stringBuffer.append("\nQuery Data: ");
                 stringBuffer.append(getQueryString());
             }
             return stringBuffer.toString();
         } catch (MalformedURLException e) {
             return "";
         }
     }
 
     /**
      * Do a sampling and return its results.
      *
      * @param e
      *            <code>Entry</code> to be sampled
      * @return results of the sampling
      */
     @Override
     public SampleResult sample(Entry e) {
         return sample();
     }
 
     /**
      * Perform a sample, and return the results
      *
      * @return results of the sampling
      */
     public SampleResult sample() {
         SampleResult res = null;
         try {
             res = sample(getUrl(), getMethod(), false, 0);
             res.setSampleLabel(getName());
             return res;
         } catch (Exception e) {
             return errorResult(e, new HTTPSampleResult());
         }
     }
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param u
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param depth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling
      */
     protected abstract HTTPSampleResult sample(URL u,
             String method, boolean areFollowingRedirect, int depth);
 
     /**
      * Download the resources of an HTML page.
      * 
      * @param res
      *            result of the initial request - must contain an HTML response
      * @param container
      *            for storing the results, if any
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return res if no resources exist, otherwise the "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
         Iterator<URL> urls = null;
         try {
             final byte[] responseData = res.getResponseData();
             if (responseData.length > 0){  // Bug 39205
                 String parserName = getParserClass(res);
                 if(parserName != null)
                 {
                     final HTMLParser parser =
                         parserName.length() > 0 ? // we have a name
                         HTMLParser.getParser(parserName)
                         :
                         HTMLParser.getParser(); // we don't; use the default parser
                     String userAgent = getUserAgent(res);
                     urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
                 }
             }
         } catch (HTMLParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
             setParentSampleSuccess(res, false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 container = new HTTPSampleResult(res);
                 container.addRawSubResult(res);
             }
             res = container;
 
             // Get the URL matcher
             String re=getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length()>0){
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: "+e.getMessage());
                 }
             }
             
             // For concurrent get resources
             final List<Callable<AsynSamplerResultHolder>> liste = new ArrayList<Callable<AsynSamplerResultHolder>>();
 
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         String urlstr = url.toString();
                         String urlStrEnc=encodeSpaces(urlstr);
                         if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
                             try {
                                 url = new URL(urlStrEnc);
                             } catch (MalformedURLException e) {
                                 res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), new HTTPSampleResult(res)));
                                 setParentSampleSuccess(res, false);
                                 continue;
                             }
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         
                         if (isConcurrentDwn()) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             liste.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes != null ? binRes.isSuccessful() : true));
                         }
 
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), new HTTPSampleResult(res)));
                     setParentSampleSuccess(res, false);
                     continue;
                 }
             }
             // IF for download concurrent embedded resources
             if (isConcurrentDwn()) {
                 int poolSize = CONCURRENT_POOL_SIZE; // init with default value
                 try {
                     poolSize = Integer.parseInt(getConcurrentPool());
                 } catch (NumberFormatException nfe) {
                     log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                             + "but pool size value is bad. Use default value");// $NON-NLS-1$
                 }
                 // Thread pool Executor to get resources 
                 // use a LinkedBlockingQueue, note: max pool size doesn't effect
                 final ThreadPoolExecutor exec = new ThreadPoolExecutor(
                         poolSize, poolSize, KEEPALIVETIME, TimeUnit.SECONDS,
                         new LinkedBlockingQueue<Runnable>(),
                         new ThreadFactory() {
                             @Override
                             public Thread newThread(final Runnable r) {
                                 Thread t = new CleanerThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         try {
                                             r.run();
                                         } finally {
                                             ((CleanerThread)Thread.currentThread()).notifyThreadEnd();
                                         }
                                     }
                                 });
                                 return t;
                             }
                         });
 
                 boolean tasksCompleted = false;
                 try {
                     // sample all resources with threadpool
                     final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(liste);
                     // call normal shutdown (wait ending all tasks)
                     exec.shutdown();
                     // put a timeout if tasks couldn't terminate
                     exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
                     CookieManager cookieManager = getCookieManager();
                     // add result to main sampleResult
                     for (Future<AsynSamplerResultHolder> future : retExec) {
                         AsynSamplerResultHolder binRes;
                         try {
                             binRes = future.get(1, TimeUnit.MILLISECONDS);
                             if(cookieManager != null) {
                                 CollectionProperty cookies = binRes.getCookies();
                                 PropertyIterator iter = cookies.iterator();
                                 while (iter.hasNext()) {
                                     Cookie cookie = (Cookie) iter.next().getObjectValue();
                                     cookieManager.add(cookie) ;
                                 }
                             }
                             res.addSubResult(binRes.getResult());
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
                         } catch (TimeoutException e) {
                             errorResult(e, res);
                         }
                     }
                     tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
                 } catch (InterruptedException ie) {
                     log.warn("Interruped fetching embedded resources", ie); // $NON-NLS-1$
                 } catch (ExecutionException ee) {
                     log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                 } finally {
                     if (!tasksCompleted) {
                         exec.shutdownNow(); // kill any remaining tasks
                     }
                 }
             }
         }
         return res;
     }
     
     /**
      * Extract User-Agent header value
      * @param sampleResult HTTPSampleResult
      * @return User Agent part
      */
     private String getUserAgent(HTTPSampleResult sampleResult) {
         String res = sampleResult.getRequestHeaders();
         int index = res.indexOf(USER_AGENT);
         if(index >=0) {
             // see HTTPHC3Impl#getConnectionHeaders
             // see HTTPHC4Impl#getConnectionHeaders
             // see HTTPJavaImpl#getConnectionHeaders    
             //': ' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
             final String userAgentPrefix = USER_AGENT+": ";
             String userAgentHdr = res.substring(
                     index+userAgentPrefix.length(), 
                     res.indexOf('\n',// '\n' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
                             index+userAgentPrefix.length()+1));
             return userAgentHdr.trim();
         } else {
             if(log.isInfoEnabled()) {
                 log.info("No user agent extracted from requestHeaders:"+res);
             }
             return null;
         }
     }
 
     /**
      * Set parent successful attribute based on IGNORE_FAILED_EMBEDDED_RESOURCES parameter
      * @param res {@link HTTPSampleResult}
      * @param initialValue boolean
      */
     private void setParentSampleSuccess(HTTPSampleResult res, boolean initialValue) {
         if(!IGNORE_FAILED_EMBEDDED_RESOURCES) {
             res.setSuccessful(initialValue);
         }
     }
 
     /*
      * @param res HTTPSampleResult to check
      * @return parser class name (may be "") or null if entry does not exist
      */
     private String getParserClass(HTTPSampleResult res) {
         final String ct = res.getMediaType();
         return parsersForType.get(ct);
     }
 
     // TODO: make static?
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded(String host) {
         testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         HTTPSamplerBase base = (HTTPSamplerBase) super.clone();
         return base;
     }
 
     /**
      * Iteratively download the redirect targets of a redirect response.
      * <p>
      * The returned result will contain one subsample for each request issued,
      * including the original one that was passed in. It will be an
      * HTTPSampleResult that should mostly look as if the final destination of
      * the redirect chain had been obtained in a single shot.
      *
      * @param res
      *            result of the initial request - must be a redirect response
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult followRedirects(HTTPSampleResult res, int frameDepth) {
         HTTPSampleResult totalRes = new HTTPSampleResult(res);
         totalRes.addRawSubResult(res);
         HTTPSampleResult lastRes = res;
 
         int redirect;
         for (redirect = 0; redirect < MAX_REDIRECTS; redirect++) {
             boolean invalidRedirectUrl = false;
             String location = lastRes.getRedirectLocation(); 
             if (log.isDebugEnabled()) {
                 log.debug("Initial location: " + location);
             }
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             location = encodeSpaces(location);
             if (log.isDebugEnabled()) {
                 log.debug("Location after /. and space transforms: " + location);
             }
             // Change all but HEAD into GET (Bug 55450)
             String method = lastRes.getHTTPMethod();
             if (!HTTPConstants.HEAD.equalsIgnoreCase(method)) {
                 method = HTTPConstants.GET;
             }
             try {
                 URL url = ConversionUtils.makeRelativeURL(lastRes.getURL(), location);
                 url = ConversionUtils.sanitizeUrl(url).toURL();
                 if (log.isDebugEnabled()) {
                     log.debug("Location as URL: " + url.toString());
                 }
                 lastRes = sample(url, method, true, frameDepth);
             } catch (MalformedURLException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             } catch (URISyntaxException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (int i = 0; i < subs.length; i++) {
                     totalRes.addSubResult(subs[i]);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if(!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeeded maximum number of redirects: " + MAX_REDIRECTS), new HTTPSampleResult(lastRes));
             totalRes.addSubResult(lastRes);
         }
 
         // Now populate the any totalRes fields that need to
         // come from lastRes:
         totalRes.setSampleLabel(totalRes.getSampleLabel() + "->" + lastRes.getSampleLabel());
         // The following three can be discussed: should they be from the
         // first request or from the final one? I chose to do it this way
         // because that's what browsers do: they show the final URL of the
         // redirect chain in the location field.
         totalRes.setURL(lastRes.getURL());
         totalRes.setHTTPMethod(lastRes.getHTTPMethod());
         totalRes.setQueryString(lastRes.getQueryString());
         totalRes.setRequestHeaders(lastRes.getRequestHeaders());
 
         totalRes.setResponseData(lastRes.getResponseData());
         totalRes.setResponseCode(lastRes.getResponseCode());
         totalRes.setSuccessful(lastRes.isSuccessful());
         totalRes.setResponseMessage(lastRes.getResponseMessage());
         totalRes.setDataType(lastRes.getDataType());
         totalRes.setResponseHeaders(lastRes.getResponseHeaders());
         totalRes.setContentType(lastRes.getContentType());
         totalRes.setDataEncoding(lastRes.getDataEncodingNoDefault());
         return totalRes;
     }
 
     /**
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param areFollowingRedirect
      * @param frameDepth
      * @param res
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect, int frameDepth, HTTPSampleResult res) {
         boolean wasRedirected = false;
         if (!areFollowingRedirect) {
             if (res.isRedirect()) {
                 log.debug("Location set to - " + res.getRedirectLocation());
 
                 if (getFollowRedirects()) {
                     res = followRedirects(res, frameDepth);
                     areFollowingRedirect = true;
                     wasRedirected = true;
                 }
             }
         }
         if (isImageParser() && (SampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), new HTTPSampleResult(res)));
             } else {
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 // otherwise, use null so the container is created if necessary unless
                 // the flag is false, in which case revert to broken 2.1 behaviour 
                 // Bug 51939 -  https://issues.apache.org/bugzilla/show_bug.cgi?id=51939
                 if(!wasRedirected) {
                     HTTPSampleResult container = (HTTPSampleResult) (
                             areFollowingRedirect ? res.getParent() : SEPARATE_CONTAINER ? null : res);
                     res = downloadPageResources(res, container, frameDepth);
                 }
             }
         }
         return res;
     }
 
     /**
      * Determine if the HTTP status code is successful or not
      * i.e. in range 200 to 399 inclusive
      *
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code){
         return (code >= 200 && code <= 399);
     }
 
     protected static String encodeBackSlashes(String value) {
         StringBuilder newValue = new StringBuilder();
         for (int i = 0; i < value.length(); i++) {
             char charAt = value.charAt(i);
             if (charAt == '\\') { // $NON-NLS-1$
                 newValue.append("\\\\"); // $NON-NLS-1$
             } else {
                 newValue.append(charAt);
             }
         }
         return newValue.toString();
     }
 
     /*
      * Method to set files list to be uploaded.
      *
      * @param value
      *   HTTPFileArgs object that stores file list to be uploaded.
      */
     private void setHTTPFileArgs(HTTPFileArgs value) {
         if (value.getHTTPFileArgCount() > 0){
             setProperty(new TestElementProperty(FILE_ARGS, value));
         } else {
             removeProperty(FILE_ARGS); // no point saving an empty list
         }
     }
 
     /*
      * Method to get files list to be uploaded.
      */
     private HTTPFileArgs getHTTPFileArgs() {
         return (HTTPFileArgs) getProperty(FILE_ARGS).getObjectValue();
     }
 
     /**
      * Get the collection of files as a list.
      * The list is built up from the filename/filefield/mimetype properties,
      * plus any additional entries saved in the FILE_ARGS property.
      *
      * If there are no valid file entries, then an empty list is returned.
      *
      * @return an array of file arguments (never null)
      */
     public HTTPFileArg[] getHTTPFiles() {
         final HTTPFileArgs fileArgs = getHTTPFileArgs();
         return fileArgs == null ? new HTTPFileArg[] {} : fileArgs.asArray();
     }
 
     public int getHTTPFileCount(){
         return getHTTPFiles().length;
     }
     /**
      * Saves the list of files.
      * The first file is saved in the Filename/field/mimetype properties.
      * Any additional files are saved in the FILE_ARGS array.
      *
      * @param files list of files to save
      */
     public void setHTTPFiles(HTTPFileArg[] files) {
         HTTPFileArgs fileArgs = new HTTPFileArgs();
         // Weed out the empty files
         if (files.length > 0) {
             for(int i=0; i < files.length; i++){
                 HTTPFileArg file = files[i];
                 if (file.isNotEmpty()){
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray(){
         return METHODLIST.toArray(new String[METHODLIST.size()]);
     }
 
     public static boolean isSecure(String protocol){
         return HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url){
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     @Override
     public void threadStarted(){
     }
 
     @Override
     public void threadFinished(){
     }
 
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         // NOOP to provide based empty impl and avoid breaking existing implementations
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
-     *
+     * <p>
      * For the MD5 case, the result byte count is set to the size of the original response.
-     * 
+     * <p>
      * Closes the inputStream 
      * 
-     * @param sampleResult
-     * @param in input stream
+     * @param sampleResult sample to store information about the response into
+     * @param in input stream from which to read the response
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
-     * @throws IOException
+     * @throws IOException if reading the result fails
      */
     public byte[] readResponse(SampleResult sampleResult, InputStream in, int length) throws IOException {
         try {
             byte[] readBuffer = new byte[8192]; // 8kB is the (max) size to have the latency ('the first packet')
             int bufferSize=32;// Enough for MD5
     
             MessageDigest md=null;
             boolean asMD5 = useMD5();
             if (asMD5) {
                 try {
                     md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
                 } catch (NoSuchAlgorithmException e) {
                     log.error("Should not happen - could not find MD5 digest", e);
                     asMD5=false;
                 }
             } else {
                 if (length <= 0) {// may also happen if long value > int.max
                     bufferSize = 4 * 1024;
                 } else {
                     bufferSize = length;
                 }
             }
             ByteArrayOutputStream w = new ByteArrayOutputStream(bufferSize);
             int bytesRead = 0;
             int totalBytes = 0;
             boolean first = true;
             while ((bytesRead = in.read(readBuffer)) > -1) {
                 if (first) {
                     sampleResult.latencyEnd();
                     first = false;
                 }
                 if (asMD5 && md != null) {
                     md.update(readBuffer, 0 , bytesRead);
                     totalBytes += bytesRead;
                 } else {
                     w.write(readBuffer, 0, bytesRead);
                 }
             }
             if (first){ // Bug 46838 - if there was no data, still need to set latency
                 sampleResult.latencyEnd();
             }
             in.close();
             w.flush();
             if (asMD5 && md != null) {
                 byte[] md5Result = md.digest();
                 w.write(JOrphanUtils.baToHexBytes(md5Result)); 
                 sampleResult.setBytes(totalBytes);
             }
             w.close();
             return w.toByteArray();
         } finally {
             IOUtils.closeQuietly(in);
         }
     }
 
     /**
      * JMeter 2.3.1 and earlier only had fields for one file on the GUI:
-     * - FILE_NAME
-     * - FILE_FIELD
-     * - MIMETYPE
+     * <ul>
+     *   <li>FILE_NAME</li>
+     *   <li>FILE_FIELD</li>
+     *   <li>MIMETYPE</li>
+     * </ul>
      * These were stored in their own individual properties.
-     *
+     * <p>
      * Version 2.3.3 introduced a list of files, each with their own path, name and mimetype.
-     *
+     * <p>
      * In order to maintain backwards compatibility of test plans, the 3 original properties
      * were retained; additional file entries are stored in an HTTPFileArgs class.
      * The HTTPFileArgs class was only present if there is more than 1 file; this means that
      * such test plans are backward compatible.
-     *
+     * <p>
      * Versions after 2.3.4 dispense with the original set of 3 properties.
      * Test plans that use them are converted to use a single HTTPFileArgs list.
      *
      * @see HTTPSamplerBaseConverter
      */
     void mergeFileProperties() {
         JMeterProperty fileName = getProperty(FILE_NAME);
         JMeterProperty paramName = getProperty(FILE_FIELD);
         JMeterProperty mimeType = getProperty(MIMETYPE);
         HTTPFileArg oldStyleFile = new HTTPFileArg(fileName, paramName, mimeType);
 
         HTTPFileArgs fileArgs = getHTTPFileArgs();
 
         HTTPFileArgs allFileArgs = new HTTPFileArgs();
         if(oldStyleFile.isNotEmpty()) { // OK, we have an old-style file definition
             allFileArgs.addHTTPFileArg(oldStyleFile); // save it
             // Now deal with any additional file arguments
             if(fileArgs != null) {
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (int i = 0; i < infiles.length; i++){
                     allFileArgs.addHTTPFileArg(infiles[i]);
                 }
             }
         } else {
             if(fileArgs != null) { // for new test plans that don't have FILE/PARAM/MIME properties
                 allFileArgs = fileArgs;
             }
         }
         // Updated the property lists
         setHTTPFileArgs(allFileArgs);
         removeProperty(FILE_FIELD);
         removeProperty(FILE_NAME);
         removeProperty(MIMETYPE);
     }
 
     /**
      * set IP source to use - does not apply to Java HTTP implementation currently
+     *
+     * @param value IP source to use
      */
     public void setIpSource(String value) {
         setProperty(IP_SOURCE, value, "");
     }
 
     /**
      * get IP source to use - does not apply to Java HTTP implementation currently
+     *
+     * @return IP source to use
      */
     public String getIpSource() {
         return getPropertyAsString(IP_SOURCE,"");
     }
  
     /**
      * set IP/address source type to use
+     *
+     * @param value type of the IP/address source
      */
     public void setIpSourceType(int value) {
         setProperty(IP_SOURCE_TYPE, value, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * get IP/address source type to use
      * 
      * @return address source type
      */
     public int getIpSourceType() {
         return getPropertyAsInt(IP_SOURCE_TYPE, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * Return if used a concurrent thread pool to get embedded resources.
      *
      * @return true if used
      */
     public boolean isConcurrentDwn() {
         return getPropertyAsBoolean(CONCURRENT_DWN, false);
     }
 
     public void setConcurrentDwn(boolean concurrentDwn) {
         setProperty(CONCURRENT_DWN, concurrentDwn, false);
     }
 
     /**
      * Get the pool size for concurrent thread pool to get embedded resources.
      *
      * @return the pool size
      */
     public String getConcurrentPool() {
         return getPropertyAsString(CONCURRENT_POOL,CONCURRENT_POOL_DEFAULT);
     }
 
     public void setConcurrentPool(String poolSize) {
         setProperty(CONCURRENT_POOL, poolSize, CONCURRENT_POOL_DEFAULT);
     }
 
     
     /**
      * Callable class to sample asynchronously resources embedded
      *
      */
     private static class ASyncSample implements Callable<AsynSamplerResultHolder> {
         final private URL url;
         final private String method;
         final private boolean areFollowingRedirect;
         final private int depth;
         private final HTTPSamplerBase sampler;
         private final JMeterContext jmeterContextOfParentThread;
 
         ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth,  CookieManager cookieManager, HTTPSamplerBase base){
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
             this.sampler = (HTTPSamplerBase) base.clone();
             // We don't want to use CacheManager clone but the parent one, and CacheManager is Thread Safe
             CacheManager cacheManager = base.getCacheManager();
             if (cacheManager != null) {
                 this.sampler.setCacheManagerProperty(cacheManager);
             }
             
             if(cookieManager != null) {
                 CookieManager clonedCookieManager = (CookieManager) cookieManager.clone();
                 this.sampler.setCookieManagerProperty(clonedCookieManager);
             } 
             this.jmeterContextOfParentThread = JMeterContextService.getContext();
         }
 
         @Override
         public AsynSamplerResultHolder call() {
             JMeterContextService.replaceContext(jmeterContextOfParentThread);
             ((CleanerThread) Thread.currentThread()).registerSamplerForEndNotification(sampler);
             HTTPSampleResult httpSampleResult = sampler.sample(url, method, areFollowingRedirect, depth);
             if(sampler.getCookieManager() != null) {
                 CollectionProperty cookies = sampler.getCookieManager().getCookies();
                 return new AsynSamplerResultHolder(httpSampleResult, cookies);
             } else {
                 return new AsynSamplerResultHolder(httpSampleResult, new CollectionProperty());
             }
         }
     }
     
     /**
      * Custom thread implementation that 
      *
      */
     private static class CleanerThread extends Thread {
         private final List<HTTPSamplerBase> samplersToNotify = new ArrayList<HTTPSamplerBase>();
         /**
          * @param runnable Runnable
          */
         public CleanerThread(Runnable runnable) {
            super(runnable);
         }
         
         /**
          * Notify of thread end
          */
         public void notifyThreadEnd() {
             for (HTTPSamplerBase samplerBase : samplersToNotify) {
                 samplerBase.threadFinished();
             }
             samplersToNotify.clear();
         }
 
         /**
          * Register sampler to be notify at end of thread
          * @param sampler {@link HTTPSamplerBase}
          */
         public void registerSamplerForEndNotification(HTTPSamplerBase sampler) {
             this.samplersToNotify.add(sampler);
         }
     }
     
     /**
      * Holder of AsynSampler result
      */
     private static class AsynSamplerResultHolder {
         private final HTTPSampleResult result;
         private final CollectionProperty cookies;
         /**
-         * @param result
-         * @param cookies
+         * @param result {@link HTTPSampleResult} to hold
+         * @param cookies cookies to hold
          */
         public AsynSamplerResultHolder(HTTPSampleResult result, CollectionProperty cookies) {
             super();
             this.result = result;
             this.cookies = cookies;
         }
         /**
          * @return the result
          */
         public HTTPSampleResult getResult() {
             return result;
         }
         /**
          * @return the cookies
          */
         public CollectionProperty getCookies() {
             return cookies;
         }
     }
     
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpWebdav.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpWebdav.java
index 065a2ed90..72211a514 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpWebdav.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HttpWebdav.java
@@ -1,70 +1,70 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.net.URI;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 
 /**
  * WebDav request
  * @since 2.12
  */
 public final class HttpWebdav extends HttpEntityEnclosingRequestBase {
     private static final Set<String> WEBDAV_METHODS = 
             new HashSet<String>(Arrays.asList(new String[] {
                     HTTPConstants.PROPFIND,
                     HTTPConstants.PROPPATCH,
                     HTTPConstants.MKCOL,
                     HTTPConstants.COPY,
                     HTTPConstants.MOVE,
                     HTTPConstants.LOCK,
                     HTTPConstants.UNLOCK
             }));
     
     private String davMethod;
 
     /**
      * 
-     * @param davMethod
-     * @param uri
+     * @param davMethod method to use (has to be a Webdav method as identified by {@link #isWebdavMethod(String)})
+     * @param uri {@link URI} to use
      */
     public HttpWebdav(final String davMethod, final URI uri) {
         super();
         this.davMethod = davMethod;
         setURI(uri);
     }
 
     @Override
     public String getMethod() {
         return davMethod;
     }
 
     /**
      * @param method Http Method
-     * @return true if method is a Webdav one
+     * @return <code>true</code> if method is a Webdav one
      */
     public static boolean isWebdavMethod(String method) {
         return WEBDAV_METHODS.contains(method);
     }
 }
\ No newline at end of file
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java
index 5d5b2b259..c03b17656 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java
@@ -1,458 +1,463 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.URLConnection;
 
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jorphan.util.JOrphanUtils;
 
 /**
  * Class for setting the necessary headers for a POST request, and sending the
  * body of the POST.
  */
 public class PostWriter {
 
     private static final String DASH_DASH = "--";  // $NON-NLS-1$
     private static final byte[] DASH_DASH_BYTES = {'-', '-'};
 
-    /** The bounday string between multiparts */
+    /** The boundary string between multiparts */
     protected static final String BOUNDARY = "---------------------------7d159c1302d0y0"; // $NON-NLS-1$
 
     private static final byte[] CRLF = { 0x0d, 0x0A };
 
     public static final String ENCODING = "ISO-8859-1"; // $NON-NLS-1$
 
     /** The form data that is going to be sent as url encoded */
     protected byte[] formDataUrlEncoded;
     /** The form data that is going to be sent in post body */
     protected byte[] formDataPostBody;
     /** The boundary string for multipart */
     private final String boundary;
 
     /**
      * Constructor for PostWriter.
      * Uses the PostWriter.BOUNDARY as the boundary string
      *
      */
     public PostWriter() {
         this(BOUNDARY);
     }
 
     /**
      * Constructor for PostWriter
      *
      * @param boundary the boundary string to use as marker between multipart parts
      */
     public PostWriter(String boundary) {
         this.boundary = boundary;
     }
 
     /**
      * Send POST data from Entry to the open connection.
      *
-     * @return the post body sent. Actual file content is not returned, it
-     * is just shown as a placeholder text "actual file content"
+     * @param connection
+     *            the open connection to use for sending data
+     * @param sampler
+     *            sampler to get information about what to send
+     * @return the post body sent. Actual file content is not returned, it is
+     *         just shown as a placeholder text "actual file content"
+     * @throws IOException when writing data fails
      */
     public String sendPostData(URLConnection connection, HTTPSamplerBase sampler) throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
 
         HTTPFileArg files[] = sampler.getHTTPFiles();
 
         String contentEncoding = sampler.getContentEncoding();
         if(contentEncoding == null || contentEncoding.length() == 0) {
             contentEncoding = ENCODING;
         }
 
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(sampler.getUseMultipartForPost()) {
             OutputStream out = connection.getOutputStream();
 
             // Write the form data post body, which we have constructed
             // in the setHeaders. This contains the multipart start divider
             // and any form data, i.e. arguments
             out.write(formDataPostBody);
             // Retrieve the formatted data using the same encoding used to create it
             postedBody.append(new String(formDataPostBody, contentEncoding));
 
             // Add any files
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 // First write the start multipart file
                 byte[] header = file.getHeader().getBytes();  // TODO - charset?
                 out.write(header);
                 // Retrieve the formatted data using the same encoding used to create it
                 postedBody.append(new String(header)); // TODO - charset?
                 // Write the actual file content
                 writeFileToStream(file.getPath(), out);
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>"); // $NON-NLS-1$
                 // Write the end of multipart file
                 byte[] fileMultipartEndDivider = getFileMultipartEndDivider();
                 out.write(fileMultipartEndDivider);
                 // Retrieve the formatted data using the same encoding used to create it
                 postedBody.append(new String(fileMultipartEndDivider, ENCODING));
                 if(i + 1 < files.length) {
                     out.write(CRLF);
                     postedBody.append(new String(CRLF, SampleResult.DEFAULT_HTTP_ENCODING));
                 }
             }
             // Write end of multipart
             byte[] multipartEndDivider = getMultipartEndDivider();
             out.write(multipartEndDivider);
             postedBody.append(new String(multipartEndDivider, ENCODING));
 
             out.flush();
             out.close();
         }
         else {
             // If there are no arguments, we can send a file as the body of the request
             if(sampler.getArguments() != null && !sampler.hasArguments() && sampler.getSendFileAsPostBody()) {
                 OutputStream out = connection.getOutputStream();
                 // we're sure that there is at least one file because of
                 // getSendFileAsPostBody method's return value.
                 HTTPFileArg file = files[0];
                 writeFileToStream(file.getPath(), out);
                 out.flush();
                 out.close();
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>"); // $NON-NLS-1$
             }
             else if (formDataUrlEncoded != null){ // may be null for PUT
                 // In an application/x-www-form-urlencoded request, we only support
                 // parameters, no file upload is allowed
                 OutputStream out = connection.getOutputStream();
                 out.write(formDataUrlEncoded);
                 out.flush();
                 out.close();
 
                 postedBody.append(new String(formDataUrlEncoded, contentEncoding));
             }
         }
         return postedBody.toString();
     }
 
     public void setHeaders(URLConnection connection, HTTPSamplerBase sampler) throws IOException {
         // Get the encoding to use for the request
         String contentEncoding = sampler.getContentEncoding();
         if(contentEncoding == null || contentEncoding.length() == 0) {
             contentEncoding = ENCODING;
         }
         long contentLength = 0L;
         HTTPFileArg files[] = sampler.getHTTPFiles();
 
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(sampler.getUseMultipartForPost()) {
             // Set the content type
             connection.setRequestProperty(
                     HTTPConstants.HEADER_CONTENT_TYPE,
                     HTTPConstants.MULTIPART_FORM_DATA + "; boundary=" + getBoundary()); // $NON-NLS-1$
 
             // Write the form section
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
             // First the multipart start divider
             bos.write(getMultipartDivider());
             // Add any parameters
             PropertyIterator args = sampler.getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)){
                     continue;
                 }
                 // End the previous multipart
                 bos.write(CRLF);
                 // Write multipart for parameter
                 writeFormMultipart(bos, parameterName, arg.getValue(), contentEncoding, sampler.getDoBrowserCompatibleMultipart());
             }
             // If there are any files, we need to end the previous multipart
             if(files.length > 0) {
                 // End the previous multipart
                 bos.write(CRLF);
             }
             bos.flush();
             // Keep the content, will be sent later
             formDataPostBody = bos.toByteArray();
             bos.close();
             contentLength = formDataPostBody.length;
 
             // Now we just construct any multipart for the files
             // We only construct the file multipart start, we do not write
             // the actual file content
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 // Write multipart for file
                 bos = new ByteArrayOutputStream();
                 writeStartFileMultipart(bos, file.getPath(), file.getParamName(), file.getMimeType());
                 bos.flush();
                 String header = bos.toString(contentEncoding);// TODO is this correct?
                 // If this is not the first file we can't write its header now
                 // for simplicity we always save it, even if there is only one file
                 file.setHeader(header);
                 bos.close();
                 contentLength += header.length();
                 // Add also the length of the file content
                 File uploadFile = new File(file.getPath());
                 contentLength += uploadFile.length();
                 // And the end of the file multipart
                 contentLength += getFileMultipartEndDivider().length;
                 if(i+1 < files.length) {
                     contentLength += CRLF.length;
                 }
             }
 
             // Add the end of multipart
             contentLength += getMultipartEndDivider().length;
 
             // Set the content length
             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(contentLength));
 
             // Make the connection ready for sending post data
             connection.setDoOutput(true);
             connection.setDoInput(true);
         }
         else {
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             String contentTypeHeader = connection.getRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.length() > 0;
 
             // If there are no arguments, we can send a file as the body of the request
             if(sampler.getArguments() != null && sampler.getArguments().getArgumentCount() == 0 && sampler.getSendFileAsPostBody()) {
                 // we're sure that there is one file because of
                 // getSendFileAsPostBody method's return value.
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
                 // Create the content length we are going to write
                 File inputFile = new File(file.getPath());
                 contentLength = inputFile.length();
             }
             else {
                 // We create the post body content now, so we know the size
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
                 // If none of the arguments have a name specified, we
                 // just send all the values as the post body
                 String postBody = null;
                 if(!sampler.getSendParameterValuesAsPostBody()) {
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
 
                     // It is a normal post request, with parameter names and values
                     postBody = sampler.getQueryString(contentEncoding);
                 }
                 else {
                     // Allow the mimetype of the file to control the content type
                     // This is not obvious in GUI if you are not uploading any files,
                     // but just sending the content of nameless parameters
                     // TODO: needs a multiple file upload scenerio
                     if(!hasContentTypeHeader) {
                         HTTPFileArg file = files.length > 0? files[0] : null;
                         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                             // TODO: is this the correct default?
                             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBodyBuffer = new StringBuilder();
                     PropertyIterator args = sampler.getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         postBodyBuffer.append(arg.getEncodedValue(contentEncoding));
                     }
                     postBody = postBodyBuffer.toString();
                 }
 
                 bos.write(postBody.getBytes(contentEncoding));
                 bos.flush();
                 bos.close();
 
                 // Keep the content, will be sent later
                 formDataUrlEncoded = bos.toByteArray();
                 contentLength = bos.toByteArray().length;
             }
 
             // Set the content length
             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(contentLength));
 
             // Make the connection ready for sending post data
             connection.setDoOutput(true);
         }
     }
 
     /**
      * Get the boundary string, used to separate multiparts
      *
      * @return the boundary string
      */
     protected String getBoundary() {
         return boundary;
     }
 
     /**
      * Get the bytes used to separate multiparts
      * Encoded using ENCODING
      *
      * @return the bytes used to separate multiparts
      * @throws IOException
      */
     private byte[] getMultipartDivider() throws IOException {
         return (DASH_DASH + getBoundary()).getBytes(ENCODING);
     }
 
     /**
      * Get the bytes used to end a file multipart
      * Encoded using ENCODING
      *
      * @return the bytes used to end a file multipart
      * @throws IOException
      */
     private byte[] getFileMultipartEndDivider() throws IOException{
         byte[] ending = getMultipartDivider();
         byte[] completeEnding = new byte[ending.length + CRLF.length];
         System.arraycopy(CRLF, 0, completeEnding, 0, CRLF.length);
         System.arraycopy(ending, 0, completeEnding, CRLF.length, ending.length);
         return completeEnding;
     }
 
     /**
      * Get the bytes used to end the multipart request
      *
      * @return the bytes used to end the multipart request
      */
     private byte[] getMultipartEndDivider(){
         byte[] ending = DASH_DASH_BYTES;
         byte[] completeEnding = new byte[ending.length + CRLF.length];
         System.arraycopy(ending, 0, completeEnding, 0, ending.length);
         System.arraycopy(CRLF, 0, completeEnding, ending.length, CRLF.length);
         return completeEnding;
     }
 
     /**
      * Write the start of a file multipart, up to the point where the
      * actual file content should be written
      */
     private void writeStartFileMultipart(OutputStream out, String filename,
             String nameField, String mimetype)
             throws IOException {
         write(out, "Content-Disposition: form-data; name=\""); // $NON-NLS-1$
         write(out, nameField);
         write(out, "\"; filename=\"");// $NON-NLS-1$
         write(out, new File(filename).getName());
         writeln(out, "\""); // $NON-NLS-1$
         writeln(out, "Content-Type: " + mimetype); // $NON-NLS-1$
         writeln(out, "Content-Transfer-Encoding: binary"); // $NON-NLS-1$
         out.write(CRLF);
     }
 
     /**
      * Write the content of a file to the output stream
      *
      * @param filename the filename of the file to write to the stream
      * @param out the stream to write to
      * @throws IOException
      */
     private static void writeFileToStream(String filename, OutputStream out) throws IOException {
         byte[] buf = new byte[1024];
         // 1k - the previous 100k made no sense (there's tons of buffers
         // elsewhere in the chain) and it caused OOM when many concurrent
         // uploads were being done. Could be fixed by increasing the evacuation
         // ratio in bin/jmeter[.bat], but this is better.
         InputStream in = new BufferedInputStream(new FileInputStream(filename));
         int read;
         boolean noException = false;
         try { 
             while ((read = in.read(buf)) > 0) {
                 out.write(buf, 0, read);
             }
             noException = true;
         }
         finally {
             if(!noException) {
                 // Exception in progress
                 JOrphanUtils.closeQuietly(in);
             } else {
                 in.close();
             }
         }
     }
 
     /**
      * Writes form data in multipart format.
      */
     private void writeFormMultipart(OutputStream out, String name, String value, String charSet, 
             boolean browserCompatibleMultipart)
         throws IOException {
         writeln(out, "Content-Disposition: form-data; name=\"" + name + "\""); // $NON-NLS-1$ // $NON-NLS-2$
         if (!browserCompatibleMultipart){
             writeln(out, "Content-Type: text/plain; charset=" + charSet); // $NON-NLS-1$
             writeln(out, "Content-Transfer-Encoding: 8bit"); // $NON-NLS-1$
         }
         out.write(CRLF);
         out.write(value.getBytes(charSet));
         out.write(CRLF);
         // Write boundary end marker
         out.write(getMultipartDivider());
     }
 
     private void write(OutputStream out, String value)
     throws UnsupportedEncodingException, IOException
     {
         out.write(value.getBytes(ENCODING));
     }
 
 
     private void writeln(OutputStream out, String value)
     throws UnsupportedEncodingException, IOException
     {
         out.write(value.getBytes(ENCODING));
         out.write(CRLF);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
index 37d9a7e5f..1f75e2f95 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/SoapSampler.java
@@ -1,377 +1,377 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.methods.RequestEntity;
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Commons HTTPClient based soap sampler
  */
 public class SoapSampler extends HTTPSampler2 implements Interruptible { // Implemented by parent class
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     public static final String XML_DATA = "HTTPSamper.xml_data"; //$NON-NLS-1$
 
     public static final String URL_DATA = "SoapSampler.URL_DATA"; //$NON-NLS-1$
 
     public static final String SOAP_ACTION = "SoapSampler.SOAP_ACTION"; //$NON-NLS-1$
 
     public static final String SEND_SOAP_ACTION = "SoapSampler.SEND_SOAP_ACTION"; //$NON-NLS-1$
 
     public static final String XML_DATA_FILE = "SoapSampler.xml_data_file"; //$NON-NLS-1$
 
     private static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$
 
     private static final String SOAPACTION = "SOAPAction"; //$NON-NLS-1$
 
     private static final String ENCODING = "utf-8"; //$NON-NLS-1$ TODO should this be variable?
 
     private static final String DEFAULT_CONTENT_TYPE = "text/xml"; //$NON-NLS-1$
 
     public void setXmlData(String data) {
         setProperty(XML_DATA, data);
     }
 
     public String getXmlData() {
         return getPropertyAsString(XML_DATA);
     }
 
     /**
      * it's kinda obvious, but we state it anyways. Set the xml file with a
      * string path.
      *
-     * @param filename
+     * @param filename path to the xml file
      */
     public void setXmlFile(String filename) {
         setProperty(XML_DATA_FILE, filename);
     }
 
     /**
      * Get the file location of the xml file.
      *
      * @return String file path.
      */
     public String getXmlFile() {
         return getPropertyAsString(XML_DATA_FILE);
     }
 
     public String getURLData() {
         return getPropertyAsString(URL_DATA);
     }
 
     public void setURLData(String url) {
         setProperty(URL_DATA, url);
     }
 
     public String getSOAPAction() {
         return getPropertyAsString(SOAP_ACTION);
     }
 
     public String getSOAPActionQuoted() {
         String action = getSOAPAction();
         StringBuilder sb = new StringBuilder(action.length()+2);
         sb.append(DOUBLE_QUOTE);
         sb.append(action);
         sb.append(DOUBLE_QUOTE);
         return sb.toString();
     }
 
     public void setSOAPAction(String action) {
         setProperty(SOAP_ACTION, action);
     }
 
     public boolean getSendSOAPAction() {
         return getPropertyAsBoolean(SEND_SOAP_ACTION);
     }
 
     public void setSendSOAPAction(boolean action) {
         setProperty(SEND_SOAP_ACTION, String.valueOf(action));
     }
 
     protected int setPostHeaders(PostMethod post) {
         int length=0;// Take length from file
         if (getHeaderManager() != null) {
             // headerManager was set, so let's set the connection
             // to use it.
             HeaderManager mngr = getHeaderManager();
             int headerSize = mngr.size();
             for (int idx = 0; idx < headerSize; idx++) {
                 Header hd = mngr.getHeader(idx);
                 if (HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(hd.getName())) {// Use this to override file length
                     length = Integer.parseInt(hd.getValue());
                     break;
                 }
                 // All the other headers are set up by HTTPSampler2.setupConnection()
             }
         } else {
             // otherwise we use "text/xml" as the default
             post.setRequestHeader(HTTPConstants.HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE); //$NON-NLS-1$
         }
         if (getSendSOAPAction()) {
             post.setRequestHeader(SOAPACTION, getSOAPActionQuoted());
         }
         return length;
     }
 
     /**
      * Send POST data from <code>Entry</code> to the open connection.
      *
      * @param post
      * @throws IOException if an I/O exception occurs
      */
     private String sendPostData(PostMethod post, final int length) {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         final String xmlFile = getXmlFile();
         if (xmlFile != null && xmlFile.length() > 0) {
             File xmlFileAsFile = new File(xmlFile);
             if(!(xmlFileAsFile.exists() && xmlFileAsFile.canRead())) {
                 throw new IllegalArgumentException(JMeterUtils.getResString("soap_sampler_file_invalid") // $NON-NLS-1$
                         + xmlFileAsFile.getAbsolutePath());
             }
             // We just add placeholder text for file content
             postedBody.append("Filename: ").append(xmlFile).append("\n");
             postedBody.append("<actual file content, not shown here>");
             post.setRequestEntity(new RequestEntity() {
                 @Override
                 public boolean isRepeatable() {
                     return true;
                 }
 
                 @Override
                 public void writeRequest(OutputStream out) throws IOException {
                     InputStream in = null;
                     try{
                         in = new BufferedInputStream(new FileInputStream(xmlFile));
                         IOUtils.copy(in, out);
                         out.flush();
                     } finally {
                         IOUtils.closeQuietly(in);
                     }
                 }
 
                 @Override
                 public long getContentLength() {
                     switch(length){
                         case -1:
                             return -1;
                         case 0: // No header provided
                             return new File(xmlFile).length();
                         default:
                             return length;
                         }
                 }
 
                 @Override
                 public String getContentType() {
                     // TODO do we need to add a charset for the file contents?
                     return DEFAULT_CONTENT_TYPE; // $NON-NLS-1$
                 }
             });
         } else {
             postedBody.append(getXmlData());
             post.setRequestEntity(new RequestEntity() {
                 @Override
                 public boolean isRepeatable() {
                     return true;
                 }
 
                 @Override
                 public void writeRequest(OutputStream out) throws IOException {
                     // charset must agree with content-type below
                     IOUtils.write(getXmlData(), out, ENCODING); // $NON-NLS-1$
                     out.flush();
                 }
 
                 @Override
                 public long getContentLength() {
                     try {
                         return getXmlData().getBytes(ENCODING).length; // so we don't generate chunked encoding
                     } catch (UnsupportedEncodingException e) {
                         log.warn(e.getLocalizedMessage());
                         return -1; // will use chunked encoding
                     }
                 }
 
                 @Override
                 public String getContentType() {
                     return DEFAULT_CONTENT_TYPE+"; charset="+ENCODING; // $NON-NLS-1$
                 }
             });
         }
         return postedBody.toString();
     }
 
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
 
         String urlStr = url.toString();
 
         log.debug("Start : sample " + urlStr);
 
         PostMethod httpMethod;
         httpMethod = new PostMethod(urlStr);
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(false);
 
         res.setSampleLabel(urlStr); // May be replaced later
         res.setHTTPMethod(HTTPConstants.POST);
         res.setURL(url);
         res.sampleStart(); // Count the retries as well in the time
         HttpClient client = null;
         InputStream instream = null;
         try {
             int content_len = setPostHeaders(httpMethod);
             client = setupConnection(url, httpMethod, res);
             setSavedClient(client);
 
             res.setQueryString(sendPostData(httpMethod,content_len));
             int statusCode = client.executeMethod(httpMethod);
             // Some headers are set by executeMethod()
             res.setRequestHeaders(getConnectionHeaders(httpMethod));
 
             // Request sent. Now get the response:
             instream = httpMethod.getResponseBodyAsStream();
 
             if (instream != null) {// will be null for HEAD
 
                 org.apache.commons.httpclient.Header responseHeader = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_ENCODING);
                 if (responseHeader != null && HTTPConstants.ENCODING_GZIP.equals(responseHeader.getValue())) {
                     instream = new GZIPInputStream(instream);
                 }
 
                 //int contentLength = httpMethod.getResponseContentLength();Not visible ...
                 //TODO size ouststream according to actual content length
                 ByteArrayOutputStream outstream = new ByteArrayOutputStream(4 * 1024);
                 //contentLength > 0 ? contentLength : DEFAULT_INITIAL_BUFFER_SIZE);
                 byte[] buffer = new byte[4096];
                 int len;
                 boolean first = true;// first response
                 while ((len = instream.read(buffer)) > 0) {
                     if (first) { // save the latency
                         res.latencyEnd();
                         first = false;
                     }
                     outstream.write(buffer, 0, len);
                 }
 
                 res.setResponseData(outstream.toByteArray());
                 outstream.close();
 
             }
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setSampleLabel(httpMethod.getURI().toString());
             // Pick up Actual path (after redirects)
 
             res.setResponseCode(Integer.toString(statusCode));
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseMessage(httpMethod.getStatusText());
 
             // Set up the defaults (may be overridden below)
             res.setDataEncoding(ENCODING);
             res.setContentType(DEFAULT_CONTENT_TYPE);
             String ct = null;
             org.apache.commons.httpclient.Header h
                     = httpMethod.getResponseHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (h != null)// Can be missing, e.g. on redirect
             {
                 ct = h.getValue();
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             res.setResponseHeaders(getResponseHeaders(httpMethod));
             if (res.isRedirect()) {
                 res.setRedirectLocation(httpMethod.getResponseHeader(HTTPConstants.HEADER_LOCATION).getValue());
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()) {
                 res.setURL(new URL(httpMethod.getURI().toString()));
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());
 
             // Save cache information
             final CacheManager cacheManager = getCacheManager();
             if (cacheManager != null){
                 cacheManager.saveDetails(httpMethod, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             httpMethod.releaseConnection();
             return res;
         } catch (IllegalArgumentException e)// e.g. some kinds of invalid URL
         {
             res.sampleEnd();
             errorResult(e, res);
             return res;
         } catch (IOException e) {
             res.sampleEnd();
             errorResult(e, res);
             return res;
         } finally {
             JOrphanUtils.closeQuietly(instream);
             setSavedClient(null);
             httpMethod.releaseConnection();
         }
     }
 
     @Override
     public URL getUrl() throws MalformedURLException {
         return new URL(getURLData());
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/WebServiceSampler.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/WebServiceSampler.java
index 435427772..c370b8d40 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/WebServiceSampler.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/WebServiceSampler.java
@@ -1,705 +1,710 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.StringReader;
 import java.io.StringWriter;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Hashtable;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Random;
 
 import javax.xml.parsers.DocumentBuilder;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.JMeter;
 import org.apache.jmeter.gui.JMeterFileFilter;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.DOMPool;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.io.TextFile;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.soap.Envelope;
 import org.apache.soap.SOAPException;
 import org.apache.soap.rpc.SOAPContext;
 import org.apache.soap.transport.http.SOAPHTTPConnection;
 import org.apache.soap.util.xml.XMLParserUtils;
 import org.w3c.dom.Document;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 
 /**
  * Sampler to handle Web Service requests. It uses Apache SOAP drivers to
  * perform the XML generation, connection, SOAP encoding and other SOAP
  * functions.
  * <p>
  * Created on: Jun 26, 2003
  *
  */
 public class WebServiceSampler extends HTTPSamplerBase  {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
-    //+ JMX file attribut names - do not change!
+    //+ JMX file attribute names - do not change!
     private static final String XML_DATA = "HTTPSamper.xml_data"; //$NON-NLS-1$
 
     private static final String SOAP_ACTION = "Soap.Action"; //$NON-NLS-1$
 
     private static final String XML_DATA_FILE = "WebServiceSampler.xml_data_file"; //$NON-NLS-1$
 
     private static final String XML_PATH_LOC = "WebServiceSampler.xml_path_loc"; //$NON-NLS-1$
 
     private static final String MEMORY_CACHE = "WebServiceSampler.memory_cache"; //$NON-NLS-1$
 
     private static final String MAINTAIN_SESSION = "WebServiceSampler.maintain_session"; //$NON-NLS-1$
     
     private static final String READ_RESPONSE = "WebServiceSampler.read_response"; //$NON-NLS-1$
 
     private static final String USE_PROXY = "WebServiceSampler.use_proxy"; //$NON-NLS-1$
 
     private static final String PROXY_HOST = "WebServiceSampler.proxy_host"; //$NON-NLS-1$
 
     private static final String PROXY_PORT = "WebServiceSampler.proxy_port"; //$NON-NLS-1$
 
     private static final String WSDL_URL = "WebserviceSampler.wsdl_url"; //$NON-NLS-1$
 
     private static final String TIMEOUT = "WebserviceSampler.timeout"; //$NON-NLS-1$
-    //- JMX file attribut names - do not change!
+    //- JMX file attribute names - do not change!
 
     private static final String PROXY_USER =
         JMeterUtils.getPropDefault(JMeter.HTTP_PROXY_USER,""); // $NON-NLS-1$
 
     private static final String PROXY_PASS =
         JMeterUtils.getPropDefault(JMeter.HTTP_PROXY_PASS,""); // $NON-NLS-1$
 
     private static final String ENCODING = "UTF-8"; // $NON-NLS-1$ TODO should this be a variable?
 
     public static final boolean MAINTAIN_SESSION_DEFAULT = true;
 
     /*
      * Random class for generating random numbers.
      */
     private final Random RANDOM = new Random();
 
     private String fileContents = null;
 
     /**
      * Set the path where XML messages are stored for random selection.
+     *
+     * @param path where XML messages are stored
      */
     public void setXmlPathLoc(String path) {
         setProperty(XML_PATH_LOC, path);
     }
 
     /**
-     * Get the path where XML messages are stored. this is the directory where
+     * Get the path where XML messages are stored. This is the directory where
      * JMeter will randomly select a file.
+     *
+     * @return path where XML messages are stored
      */
     public String getXmlPathLoc() {
         return getPropertyAsString(XML_PATH_LOC);
     }
 
     /**
      * it's kinda obvious, but we state it anyways. Set the xml file with a
      * string path.
      *
-     * @param filename
+     * @param filename path to xml file
      */
     public void setXmlFile(String filename) {
         setProperty(XML_DATA_FILE, filename);
     }
 
     /**
      * Get the file location of the xml file.
      *
      * @return String file path.
      */
     public String getXmlFile() {
         return getPropertyAsString(XML_DATA_FILE);
     }
 
     /**
      * Method is used internally to check if a random file should be used for
      * the message. Messages must be valid. This is one way to load test with
      * different messages. The limitation of this approach is parsing XML takes
      * CPU resources, so it could affect JMeter GUI responsiveness.
      *
      * @return String filename
      */
     protected String getRandomFileName() {
         if (this.getXmlPathLoc() != null) {
             File src = new File(this.getXmlPathLoc());
             if (src.isDirectory() && src.list() != null) {
                 File [] fileList = src.listFiles(new JMeterFileFilter(new String[] { ".xml" }, false));
                 File one = fileList[RANDOM.nextInt(fileList.length)];
                 // return the absolutePath of the file
                 return one.getAbsolutePath();
             }
             return getXmlFile();
         }
         return getXmlFile();
     }
 
     /**
      * Set the XML data.
      *
-     * @param data
+     * @param data xml data
      */
     public void setXmlData(String data) {
         setProperty(XML_DATA, data);
     }
 
     /**
      * Get the XML data as a string.
      *
      * @return String data
      */
     public String getXmlData() {
         return getPropertyAsString(XML_DATA);
     }
 
     /**
      * Set the soap action which should be in the form of an URN.
      *
-     * @param data
+     * @param data soap action
      */
     public void setSoapAction(String data) {
         setProperty(SOAP_ACTION, data);
     }
 
     /**
      * Return the soap action string.
      *
      * @return String soap action
      */
     public String getSoapAction() {
         return getPropertyAsString(SOAP_ACTION);
     }
 
     /**
      * Set the maintain session option.
      *
-     * @param maintainSession
+     * @param maintainSession flag whether to maintain a session
      */
     public void setMaintainSession(boolean maintainSession) {
         setProperty(MAINTAIN_SESSION, maintainSession, MAINTAIN_SESSION_DEFAULT);
     }
 
     /**
      * Get the maintain session option.
      *
-     * @return boolean cache
+     * @return flag whether to maintain a session
      */
     public boolean getMaintainSession() {
         return getPropertyAsBoolean(MAINTAIN_SESSION, MAINTAIN_SESSION_DEFAULT);
     }
     
     /**
      * Set the memory cache.
      *
-     * @param cache
+     * @param cache flag whether to use the memory cache
      */
     public void setMemoryCache(boolean cache) {
         setProperty(MEMORY_CACHE, String.valueOf(cache));
     }
 
     /**
      * Get the memory cache.
      *
-     * @return boolean cache
+     * @return flag whether to use the memory cache
      */
     public boolean getMemoryCache() {
         return getPropertyAsBoolean(MEMORY_CACHE);
     }
 
     /**
      * Set whether the sampler should read the response or not.
      *
      * @param read
+     *            flag whether the response should be read
      */
     public void setReadResponse(boolean read) {
         setProperty(READ_RESPONSE, String.valueOf(read));
     }
 
     /**
      * Return whether or not to read the response.
      *
-     * @return boolean
+     * @return flag whether the response should be read
      */
     public boolean getReadResponse() {
         return this.getPropertyAsBoolean(READ_RESPONSE);
     }
 
     /**
      * Set whether or not to use a proxy
      *
-     * @param proxy
+     * @param proxy flag whether to use a proxy
      */
     public void setUseProxy(boolean proxy) {
         setProperty(USE_PROXY, String.valueOf(proxy));
     }
 
     /**
      * Return whether or not to use proxy
      *
-     * @return true if should use proxy
+     * @return <code>true</code> if a proxy should be used
      */
     public boolean getUseProxy() {
         return this.getPropertyAsBoolean(USE_PROXY);
     }
 
     /**
      * Set the proxy hostname
      *
-     * @param host
+     * @param host the hostname of the proxy
      */
     public void setProxyHost(String host) {
         setProperty(PROXY_HOST, host);
     }
 
     /**
      * Return the proxy hostname
      *
      * @return the proxy hostname
      */
     @Override
     public String getProxyHost() {
         this.checkProxy();
         return this.getPropertyAsString(PROXY_HOST);
     }
 
     /**
      * Set the proxy port
      *
-     * @param port
+     * @param port the port of the proxy
      */
     public void setProxyPort(String port) {
         setProperty(PROXY_PORT, port);
     }
 
     /**
      * Return the proxy port
      *
      * @return the proxy port
      */
     public int getProxyPort() {
         this.checkProxy();
         return this.getPropertyAsInt(PROXY_PORT);
     }
 
     /**
      *
-     * @param url
+     * @param url the URL of the WSDL
      */
     public void setWsdlURL(String url) {
         this.setProperty(WSDL_URL, url);
     }
 
     /**
      * method returns the WSDL URL
      *
      * @return the WSDL URL
      */
     public String getWsdlURL() {
         return getPropertyAsString(WSDL_URL);
     }
 
     /*
      * The method will check to see if JMeter was started in NonGui mode. If it
      * was, it will try to pick up the proxy host and port values if they were
      * passed to JMeter.java.
      */
     private void checkProxy() {
         if (JMeter.isNonGUI()) {
             this.setUseProxy(true);
             // we check to see if the proxy host and port are set
             String port = this.getPropertyAsString(PROXY_PORT);
             String host = this.getPropertyAsString(PROXY_HOST);
             if (host == null || host.length() == 0) {
                 // it's not set, lets check if the user passed
                 // proxy host and port from command line
                 host = System.getProperty("http.proxyHost");
                 if (host != null) {
                     this.setProxyHost(host);
                 }
             }
             if (port == null || port.length() == 0) {
                 // it's not set, lets check if the user passed
                 // proxy host and port from command line
                 port = System.getProperty("http.proxyPort");
                 if (port != null) {
                     this.setProxyPort(port);
                 }
             }
         }
     }
 
     /*
      * This method uses Apache soap util to create the proper DOM elements.
      *
      * @return Element
      */
     private org.w3c.dom.Element createDocument() throws SAXException, IOException {
         Document doc = null;
         String next = this.getRandomFileName();//get filename or ""
 
         /* Note that the filename is also used as a key to the pool (if used)
         ** Documents provided in the testplan are not currently pooled, as they may change
         *  between samples.
         */
 
         if (next.length() > 0 && getMemoryCache()) {
             doc = DOMPool.getDocument(next);
             if (doc == null){
                 doc = openDocument(next);
                 if (doc != null) {// we created the document
                     DOMPool.putDocument(next, doc);
                 }
             }
         } else { // Must be local content - or not using pool
             doc = openDocument(next);
         }
 
         if (doc == null) {
             return null;
         }
         return doc.getDocumentElement();
     }
 
     /**
      * Open the file and create a Document.
      *
      * @param file - input filename or empty if using data from tesplan
      * @return Document
      * @throws IOException
      * @throws SAXException
      */
     private Document openDocument(String file) throws SAXException, IOException {
         /*
          * Consider using Apache commons pool to create a pool of document
          * builders or make sure XMLParserUtils creates builders efficiently.
          */
         DocumentBuilder XDB = XMLParserUtils.getXMLDocBuilder();
         XDB.setErrorHandler(null);//Suppress messages to stdout
 
         Document doc = null;
         // if either a file or path location is given,
         // get the file object.
         if (file.length() > 0) {// we have a file
             if (this.getReadResponse()) {
                 TextFile tfile = new TextFile(file);
                 fileContents = tfile.getText();
             }
             InputStream fileInputStream = null;
             try {
                 fileInputStream = new BufferedInputStream(new FileInputStream(file));
                 doc = XDB.parse(fileInputStream);
             } finally {
                 JOrphanUtils.closeQuietly(fileInputStream);
             }
         } else {// must be a "here" document
             fileContents = getXmlData();
             if (fileContents != null && fileContents.length() > 0) {
                 doc = XDB.parse(new InputSource(new StringReader(fileContents)));
             } else {
                 log.warn("No post data provided!");
             }
         }
         return doc;
     }
 
     /*
      * Required to satisfy HTTPSamplerBase Should not be called, as we override
      * sample()
      */
 
     @Override
     protected HTTPSampleResult sample(URL u, String s, boolean b, int i) {
         throw new RuntimeException("Not implemented - should not be called");
     }
 
     /**
      * Sample the URL using Apache SOAP driver. Implementation note for myself
      * and those that are curious. Current logic marks the end after the
      * response has been read. If read response is set to false, the buffered
      * reader will read, but do nothing with it. Essentially, the stream from
      * the server goes into the ether.
      */
     @Override
     public SampleResult sample() {
         SampleResult result = new SampleResult();
         result.setSuccessful(false); // Assume it will fail
         result.setResponseCode("000"); // ditto $NON-NLS-1$
         result.setSampleLabel(getName());
         try {
             result.setURL(this.getUrl());
             org.w3c.dom.Element rdoc = createDocument();
             if (rdoc == null) {
                 throw new SOAPException("Could not create document", null);
             }
             // set the response defaults
             result.setDataEncoding(ENCODING);
             result.setContentType("text/xml"); // $NON-NLS-1$
             result.setDataType(SampleResult.TEXT);
             result.setSamplerData(fileContents);// WARNING - could be large
 
             Envelope msgEnv = Envelope.unmarshall(rdoc);
             result.sampleStart();
             SOAPHTTPConnection spconn = null;
             // if a blank HeaderManager exists, try to
             // get the SOAPHTTPConnection. After the first
             // request, there should be a connection object
             // stored with the cookie header info.
             if (this.getHeaderManager() != null && this.getHeaderManager().getSOAPHeader() != null) {
                 spconn = (SOAPHTTPConnection) this.getHeaderManager().getSOAPHeader();
             } else {
                 spconn = new SOAPHTTPConnection();
             }
             spconn.setTimeout(getTimeoutAsInt());
 
             // set the auth. thanks to KiYun Roe for contributing the patch
             // I cleaned up the patch slightly. 5-26-05
             if (getAuthManager() != null) {
                 if (getAuthManager().getAuthForURL(getUrl()) != null) {
                     AuthManager authmanager = getAuthManager();
                     Authorization auth = authmanager.getAuthForURL(getUrl());
                     spconn.setUserName(auth.getUser());
                     spconn.setPassword(auth.getPass());
                 } else {
                     log.warn("the URL for the auth was null." + " Username and password not set");
                 }
             }
             // check the proxy
             String phost = "";
             int pport = 0;
             // if use proxy is set, we try to pick up the
             // proxy host and port from either the text
             // fields or from JMeterUtil if they were passed
             // from command line
             if (this.getUseProxy()) {
                 if (this.getProxyHost().length() > 0 && this.getProxyPort() > 0) {
                     phost = this.getProxyHost();
                     pport = this.getProxyPort();
                 } else {
                     if (System.getProperty("http.proxyHost") != null || System.getProperty("http.proxyPort") != null) {
                         phost = System.getProperty("http.proxyHost");
                         pport = Integer.parseInt(System.getProperty("http.proxyPort"));
                     }
                 }
                 // if for some reason the host is blank and the port is
                 // zero, the sampler will fail silently
                 if (phost.length() > 0 && pport > 0) {
                     spconn.setProxyHost(phost);
                     spconn.setProxyPort(pport);
                     if (PROXY_USER.length()>0 && PROXY_PASS.length()>0){
                         spconn.setProxyUserName(PROXY_USER);
                         spconn.setProxyPassword(PROXY_PASS);
                     }
                 }
             }
             
             HeaderManager headerManager = this.getHeaderManager();
             Hashtable<String,String> reqHeaders = null;
             if(headerManager != null) {
                 int size = headerManager.getHeaders().size();
                 reqHeaders = new Hashtable<String, String>(size);
                 for (int i = 0; i < size; i++) {
                     Header header = headerManager.get(i);
                     reqHeaders.put(header.getName(), header.getValue());
                 }         
             }
             spconn.setMaintainSession(getMaintainSession());
             spconn.send(this.getUrl(), this.getSoapAction(), reqHeaders, msgEnv, 
                     null, new SOAPContext());
 
             @SuppressWarnings("unchecked") // API uses raw types
             final Map<String, String> headers = spconn.getHeaders();
             result.setResponseHeaders(convertSoapHeaders(headers));
 
             if (this.getHeaderManager() != null) {
                 this.getHeaderManager().setSOAPHeader(spconn);
             }
 
             BufferedReader br = null;
             if (spconn.receive() != null) {
                 br = spconn.receive();
                 SOAPContext sc = spconn.getResponseSOAPContext();
                 // Set details from the actual response
                 // Needs to be done before response can be stored
                 final String contentType = sc.getContentType();
                 result.setContentType(contentType);
                 result.setEncodingAndType(contentType);
                 int length=0;
                 if (getReadResponse()) {
                     StringWriter sw = new StringWriter();
                     length=IOUtils.copy(br, sw);
                     result.sampleEnd();
                     result.setResponseData(sw.toString().getBytes(result.getDataEncodingWithDefault()));
                 } else {
                     // by not reading the response
                     // for real, it improves the
                     // performance on slow clients
                     length=br.read();
                     result.sampleEnd();
                     result.setResponseData(JMeterUtils.getResString("read_response_message"), null); //$NON-NLS-1$
                 }
                 // It is not possible to access the actual HTTP response code, so we assume no data means failure
                 if (length > 0){
                     result.setSuccessful(true);
                     result.setResponseCodeOK();
                     result.setResponseMessageOK();
                 } else {
                     result.setSuccessful(false);
                     result.setResponseCode("999");
                     result.setResponseMessage("Empty response");
                 }
             } else {
                 result.sampleEnd();
                 result.setSuccessful(false);
                 final String contentType = spconn.getResponseSOAPContext().getContentType();
                 result.setContentType(contentType);
                 result.setEncodingAndType(contentType);
                 result.setResponseData(spconn.getResponseSOAPContext().toString().getBytes(result.getDataEncodingWithDefault()));
             }
             if (br != null) {
                 br.close();
             }
             // reponse code doesn't really apply, since
             // the soap driver doesn't provide a
             // response code
         } catch (IllegalArgumentException exception){
             String message = exception.getMessage();
             log.warn(message);
             result.setResponseMessage(message);
         } catch (SAXException exception) {
             log.warn(exception.toString());
             result.setResponseMessage(exception.getMessage());
         } catch (SOAPException exception) {
             log.warn(exception.toString());
             result.setResponseMessage(exception.getMessage());
         } catch (MalformedURLException exception) {
             String message = exception.getMessage();
             log.warn(message);
             result.setResponseMessage(message);
         } catch (IOException exception) {
             String message = exception.getMessage();
             log.warn(message);
             result.setResponseMessage(message);
         } catch (NoClassDefFoundError error){
             log.error("Missing class: ",error);
             result.setResponseMessage(error.toString());
         } catch (Exception exception) {
             if ("javax.mail.MessagingException".equals(exception.getClass().getName())){
                 log.warn(exception.toString());
                 result.setResponseMessage(exception.getMessage());
             } else {
                 log.error("Problem processing the SOAP request", exception);
                 result.setResponseMessage(exception.toString());
             }
         } finally {
             // Make sure the sample start time and sample end time are recorded
             // in order not to confuse the statistics calculation methods: if
             //  an error occurs and an exception is thrown it is possible that
             // the result.sampleStart() or result.sampleEnd() won't be called
             if (result.getStartTime() == 0)
             {
                 result.sampleStart();
             }
             if (result.getEndTime() == 0)
             {
                 result.sampleEnd();
             }
         }
         return result;
     }
 
     /**
      * We override this to prevent the wrong encoding and provide no
      * implementation. We want to reuse the other parts of HTTPSampler, but not
      * the connection. The connection is handled by the Apache SOAP driver.
      */
     @Override
     public void addEncodedArgument(String name, String value, String metaData) {
     }
 
     public String convertSoapHeaders(Map<String, String> ht) {
         StringBuilder buf = new StringBuilder();
         for (Entry<String, String> entry : ht.entrySet()) {
             buf.append(entry.getKey()).append("=").append(entry.getValue()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
         }
         return buf.toString();
     }
 
 //    /**
 //     * Process headerLines
 //     * @param en enumeration of Strings
 //     * @return String containing the lines
 //     */
 //    private String convertSoapHeaders(Enumeration en) {
 //        StringBuilder buf = new StringBuilder(100);
 //        while (en.hasMoreElements()) {
 //            buf.append(en.nextElement()).append("\n"); //$NON-NLS-1$
 //        }
 //        return buf.toString();
 //    }
 
     public String getTimeout() {
         return getPropertyAsString(TIMEOUT);
     }
 
     public int getTimeoutAsInt() {
         return getPropertyAsInt(TIMEOUT);
     }
 
     public void setTimeout(String text) {
         setProperty(TIMEOUT, text);
     }
     
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase#testEnded()
      */
     @Override
     public void testEnded() {
         DOMPool.clear();
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase#testEnded(java.lang.String)
      */
     @Override
     public void testEnded(String host) {
         testEnded();
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
index 4acc76217..556fd03d9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPArgument.java
@@ -1,231 +1,268 @@
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
 
 package org.apache.jmeter.protocol.http.util;
 
 import java.io.Serializable;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 //For unit tests, @see TestHTTPArgument
 
 /*
  *
  * Represents an Argument for HTTP requests.
  */
 public class HTTPArgument extends Argument implements Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String ALWAYS_ENCODE = "HTTPArgument.always_encode";
 
     private static final String USE_EQUALS = "HTTPArgument.use_equals";
 
     private static final EncoderCache cache = new EncoderCache(1000);
 
     /**
      * Constructor for the Argument object.
+     * <p>
+     * The value is assumed to be not encoded.
+     *
+     * @param name
+     *            name of the paramter
+     * @param value
+     *            value of the parameter
+     * @param metadata
+     *            the separator to use between name and value
      */
     public HTTPArgument(String name, String value, String metadata) {
         this(name, value, false);
         this.setMetaData(metadata);
     }
 
     public void setUseEquals(boolean ue) {
         if (ue) {
             setMetaData("=");
         } else {
             setMetaData("");
         }
         setProperty(new BooleanProperty(USE_EQUALS, ue));
     }
 
     public boolean isUseEquals() {
         boolean eq = getPropertyAsBoolean(USE_EQUALS);
         if (getMetaData().equals("=") || (getValue() != null && getValue().length() > 0)) {
             setUseEquals(true);
             return true;
         }
         return eq;
 
     }
 
     public void setAlwaysEncoded(boolean ae) {
         setProperty(new BooleanProperty(ALWAYS_ENCODE, ae));
     }
 
     public boolean isAlwaysEncoded() {
         return getPropertyAsBoolean(ALWAYS_ENCODE);
     }
 
     /**
      * Constructor for the Argument object.
+     * <p>
+     * The value is assumed to be not encoded.
+     *
+     * @param name
+     *            name of the parameter
+     * @param value
+     *            value of the parameter
      */
     public HTTPArgument(String name, String value) {
         this(name, value, false);
     }
 
+    /**
+     * @param name
+     *            name of the parameter
+     * @param value
+     *            value of the parameter
+     * @param alreadyEncoded
+     *            <code>true</code> if the value is already encoded, in which
+     *            case they are decoded before storage
+     */
     public HTTPArgument(String name, String value, boolean alreadyEncoded) {
         // We assume the argument value is encoded according to the HTTP spec, i.e. UTF-8
         this(name, value, alreadyEncoded, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Construct a new HTTPArgument instance; alwaysEncoded is set to true.
      *
      * @param name the name of the parameter
      * @param value the value of the parameter
      * @param alreadyEncoded true if the name and value is already encoded, in which case they are decoded before storage.
      * @param contentEncoding the encoding used for the parameter value
      */
     public HTTPArgument(String name, String value, boolean alreadyEncoded, String contentEncoding) {
         setAlwaysEncoded(true);
         if (alreadyEncoded) {
             try {
                 // We assume the name is always encoded according to spec
                 if(log.isDebugEnabled()) {
                     log.debug("Decoding name, calling URLDecoder.decode with '"+name+"' and contentEncoding:"+EncoderCache.URL_ARGUMENT_ENCODING);
                 }
                 name = URLDecoder.decode(name, EncoderCache.URL_ARGUMENT_ENCODING);
                 // The value is encoded in the specified encoding
                 if(log.isDebugEnabled()) {
                     log.debug("Decoding value, calling URLDecoder.decode with '"+value+"' and contentEncoding:"+contentEncoding);
                 }
                 value = URLDecoder.decode(value, contentEncoding);
             } catch (UnsupportedEncodingException e) {
                 log.error(contentEncoding + " encoding not supported!");
                 throw new Error(e.toString(), e);
             }
         }
         setName(name);
         setValue(value);
         setMetaData("=");
     }
 
+    /**
+     * Construct a new HTTPArgument instance
+     *
+     * @param name
+     *            the name of the parameter
+     * @param value
+     *            the value of the parameter
+     * @param metaData
+     *            the separator to use between name and value
+     * @param alreadyEncoded
+     *            true if the name and value is already encoded
+     */
     public HTTPArgument(String name, String value, String metaData, boolean alreadyEncoded) {
         // We assume the argument value is encoded according to the HTTP spec, i.e. UTF-8
         this(name, value, metaData, alreadyEncoded, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Construct a new HTTPArgument instance
      *
      * @param name the name of the parameter
      * @param value the value of the parameter
      * @param metaData the separator to use between name and value
      * @param alreadyEncoded true if the name and value is already encoded
      * @param contentEncoding the encoding used for the parameter value
      */
     public HTTPArgument(String name, String value, String metaData, boolean alreadyEncoded, String contentEncoding) {
         this(name, value, alreadyEncoded, contentEncoding);
         setMetaData(metaData);
     }
 
     public HTTPArgument(Argument arg) {
         this(arg.getName(), arg.getValue(), arg.getMetaData());
     }
 
     /**
      * Constructor for the Argument object
      */
     public HTTPArgument() {
     }
 
     /**
      * Sets the Name attribute of the Argument object.
      *
      * @param newName
      *            the new Name value
      */
     @Override
     public void setName(String newName) {
         if (newName == null || !newName.equals(getName())) {
             super.setName(newName);
         }
     }
 
     /**
      * Get the argument value encoded using UTF-8
      *
      * @return the argument value encoded in UTF-8
      */
     public String getEncodedValue() {
         // Encode according to the HTTP spec, i.e. UTF-8
         try {
             return getEncodedValue(EncoderCache.URL_ARGUMENT_ENCODING);
         } catch (UnsupportedEncodingException e) {
             // This can't happen (how should utf8 not be supported!?!),
             // so just throw an Error:
             throw new Error("Should not happen: " + e.toString());
         }
     }
 
     /**
      * Get the argument value encoded in the specified encoding
      *
      * @param contentEncoding the encoding to use when encoding the argument value
      * @return the argument value encoded in the specified encoding
-     * @throws UnsupportedEncodingException
+     * @throws UnsupportedEncodingException of the encoding is not supported
      */
     public String getEncodedValue(String contentEncoding) throws UnsupportedEncodingException {
         if (isAlwaysEncoded()) {
             return cache.getEncoded(getValue(), contentEncoding);
         } else {
             return getValue();
         }
     }
 
     public String getEncodedName() {
         if (isAlwaysEncoded()) {
             return cache.getEncoded(getName());
         } else {
             return getName();
         }
 
     }
 
     /**
      * Converts all {@link Argument} entries in the collection to {@link HTTPArgument} entries.
      * 
      * @param args collection of {@link Argument} and/or {@link HTTPArgument} entries
      */
     public static void convertArgumentsToHTTP(Arguments args) {
         List<Argument> newArguments = new LinkedList<Argument>();
         PropertyIterator iter = args.getArguments().iterator();
         while (iter.hasNext()) {
             Argument arg = (Argument) iter.next().getObjectValue();
             if (!(arg instanceof HTTPArgument)) {
                 newArguments.add(new HTTPArgument(arg));
             } else {
                 newArguments.add(arg);
             }
         }
         args.removeAllArguments();
         args.setArguments(newArguments);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPFileArg.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPFileArg.java
index 21d4f416d..9a4af3ff0 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPFileArg.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPFileArg.java
@@ -1,203 +1,234 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements. See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License. You may obtain a copy of the License at
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
 
 package org.apache.jmeter.protocol.http.util;
 
 import java.io.Serializable;
 
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 
 /**
  * Class representing a file parameter for http upload.
  * Consists of a http parameter name/file path pair with (optional) mimetype.
  *
  * Also provides temporary storage for the headers which are sent with files.
  *
  */
 public class HTTPFileArg extends AbstractTestElement implements Serializable {
 
     private static final long serialVersionUID = 240L;
 
     /** Name used to store the file's path. */
     private static final String FILEPATH = "File.path";
 
     /** Name used to store the file's paramname. */
     private static final String PARAMNAME = "File.paramname";
 
     /** Name used to store the file's mimetype. */
     private static final String MIMETYPE = "File.mimetype";
 
     /** temporary storage area for the body header. */
     private String header;
 
     /**
      * Constructor for an empty HTTPFileArg object
      */
     public HTTPFileArg() {
     }
 
     /**
      * Constructor for the HTTPFileArg object with given path.
+     *
+     * @param path
+     *            path to the file to use
+     * @throws IllegalArgumentException
+     *             if <code>path</code> is <code>null</code>
      */
     public HTTPFileArg(String path) {
         this(path, "", "");
     }
 
     /**
      * Constructor for the HTTPFileArg object with full information.
+     *
+     * @param path
+     *            path of the file to use
+     * @param paramname
+     *            name of the http parameter to use for the file
+     * @param mimetype
+     *            mimetype of the file
+     * @throws IllegalArgumentException
+     *             if any parameter is <code>null</code>
      */
     public HTTPFileArg(String path, String paramname, String mimetype) {
         if (path == null || paramname == null || mimetype == null){
             throw new IllegalArgumentException("Parameters must not be null");
         }
         setPath(path);
         setParamName(paramname);
         setMimeType(mimetype);
     }
 
     /**
      * Constructor for the HTTPFileArg object with full information,
      * using existing properties
+     *
+     * @param path
+     *            path of the file to use
+     * @param paramname
+     *            name of the http parameter to use for the file
+     * @param mimetype
+     *            mimetype of the file
+     * @throws IllegalArgumentException
+     *             if any parameter is <code>null</code>
      */
     public HTTPFileArg(JMeterProperty path, JMeterProperty paramname, JMeterProperty mimetype) {
         if (path == null || paramname == null || mimetype == null){
             throw new IllegalArgumentException("Parameters must not be null");
         }
         setProperty(FILEPATH, path);
         setProperty(MIMETYPE, mimetype);
         setProperty(PARAMNAME, paramname);
     }
 
     private void setProperty(String name, JMeterProperty prop) {
         JMeterProperty jmp = prop.clone();
         jmp.setName(name);
         setProperty(jmp);
     }
 
     /**
      * Copy Constructor.
+     *
+     * @param file
+     *            {@link HTTPFileArg} to get information about the path, http
+     *            parameter name and mimetype of the file
+     * @throws IllegalArgumentException
+     *             if any of those retrieved information is <code>null</code>
      */
     public HTTPFileArg(HTTPFileArg file) {
         this(file.getPath(), file.getParamName(), file.getMimeType());
     }
 
     /**
      * Set the http parameter name of the File.
      *
      * @param newParamName
      * the new http parameter name
      */
     public void setParamName(String newParamName) {
         setProperty(new StringProperty(PARAMNAME, newParamName));
     }
 
     /**
      * Get the http parameter name of the File.
      *
      * @return the http parameter name
      */
     public String getParamName() {
         return getPropertyAsString(PARAMNAME);
     }
 
     /**
      * Set the mimetype of the File.
      *
      * @param newMimeType
      * the new mimetype
      */
     public void setMimeType(String newMimeType) {
         setProperty(new StringProperty(MIMETYPE, newMimeType));
     }
 
     /**
      * Get the mimetype of the File.
      *
      * @return the http parameter mimetype
      */
     public String getMimeType() {
         return getPropertyAsString(MIMETYPE);
     }
 
     /**
      * Set the path of the File.
      *
      * @param newPath
      *  the new path
      */
     public void setPath(String newPath) {
         setProperty(new StringProperty(FILEPATH, newPath));
     }
 
     /**
      * Get the path of the File.
      *
      * @return the file's path
      */
     public String getPath() {
         return getPropertyAsString(FILEPATH);
     }
 
    /**
     * Sets the body header for the HTTPFileArg object. Header
     * contains path, parameter name and mime type information.
     * This is only intended for use by methods which need to store information
     * temporarily whilst creating the HTTP body.
     * 
     * @param newHeader
     *  the new Header value
     */
    public void setHeader(String newHeader) {
        header = newHeader;
    }
 
    /**
     * Gets the saved body header for the HTTPFileArg object.
+    *
+    * @return saved body header
     */
    public String getHeader() {
        return header;
    }
 
     /**
      * returns path, param name, mime type information of
      * HTTPFileArg object.
      *
      * @return the string demonstration of HTTPFileArg object in this
      * format:
      *    "path:'&lt;PATH&gt;'|param:'&lt;PARAM NAME&gt;'|mimetype:'&lt;MIME TYPE&gt;'"
      */
     @Override
     public String toString() {
         return "path:'" + getPath()
             + "'|param:'" + getParamName()
             + "'|mimetype:'" + getMimeType() + "'";
     }
 
     /**
      * Check if the entry is not empty.
      * @return true if Path, name or mimetype fields are not the empty string
      */
     public boolean isNotEmpty() {
         return getPath().length() > 0
             || getParamName().length() > 0
             || getMimeType().length() > 0; // TODO should we allow mimetype only?
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java
index 3776abc44..d6dd06175 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/HTTPResultConverter.java
@@ -1,131 +1,131 @@
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
 
 /*
  * Created on Sep 14, 2004
  *
  */
 package org.apache.jmeter.protocol.http.util;
 
 import java.net.URL;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.converters.SampleResultConverter;
 
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 
 /**
  * Class for XStream conversion of HTTPResult
  *
  */
 public class HTTPResultConverter extends SampleResultConverter {
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
      */
     public static String getVersion() {
         return "$Revision$";  //$NON-NLS-1$
     }
 
     /**
-     * @param arg0
+     * @param arg0 the mapper
      */
     public HTTPResultConverter(Mapper arg0) {
         super(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not support types
         return HTTPSampleResult.class.equals(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
         HTTPSampleResult res = (HTTPSampleResult) obj;
         SampleSaveConfiguration save = res.getSaveConfig();
         setAttributes(writer, context, res, save);
         saveAssertions(writer, context, res, save);
         saveSubResults(writer, context, res, save);
         saveResponseHeaders(writer, context, res, save);
         saveRequestHeaders(writer, context, res, save);
         saveResponseData(writer, context, res, save);
         saveSamplerData(writer, context, res, save);
     }
 
     private void saveSamplerData(HierarchicalStreamWriter writer, MarshallingContext context, HTTPSampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveSamplerData(res)) {
             writeString(writer, TAG_COOKIES, res.getCookies());
             writeString(writer, TAG_METHOD, res.getHTTPMethod());
             writeString(writer, TAG_QUERY_STRING, res.getQueryString());
             writeString(writer, TAG_REDIRECT_LOCATION, res.getRedirectLocation());
         }
         if (save.saveUrl()) {
             writeItem(res.getURL(), context, writer);
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         HTTPSampleResult res = (HTTPSampleResult) createCollection(context.getRequiredType());
         retrieveAttributes(reader, context, res);
         while (reader.hasMoreChildren()) {
             reader.moveDown();
             Object subItem = readItem(reader, context, res);
             if (!retrieveItem(reader, context, res, subItem)) {
                 retrieveHTTPItem(reader, res, subItem);
             }
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
 
     private void retrieveHTTPItem(HierarchicalStreamReader reader, 
             HTTPSampleResult res, Object subItem) {
         if (subItem instanceof URL) {
             res.setURL((URL) subItem);
         } else {
             String nodeName = reader.getNodeName();
             if (nodeName.equals(TAG_COOKIES)) {
                 res.setCookies((String) subItem);
             } else if (nodeName.equals(TAG_METHOD)) {
                 res.setHTTPMethod((String) subItem);
             } else if (nodeName.equals(TAG_QUERY_STRING)) {
                 res.setQueryString((String) subItem);
             } else if (nodeName.equals(TAG_REDIRECT_LOCATION)) {
                 res.setRedirectLocation((String) subItem);
             }
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLException.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLException.java
index 44f0529b4..5c3548be1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLException.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLException.java
@@ -1,46 +1,46 @@
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
 
 package org.apache.jmeter.protocol.http.util;
 
 /**
  * Created on: Jun 3, 2003<br>
  *
  */
 public class WSDLException extends Exception {
 
     private static final long serialVersionUID = 240L;
 
     public WSDLException() {
         super();
     }
 
     /**
-     * @param message
+     * @param message detailed error message to include in exception
      */
     public WSDLException(String message) {
         super(message);
     }
 
     /**
-     * @param exception
+     * @param exception exception to extract the detailed message from and include that message as detailed message in this exception
      */
     public WSDLException(Exception exception) {
         super(exception.getMessage());
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java
index f21fb9a98..d28c9e973 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/WSDLHelper.java
@@ -1,390 +1,421 @@
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
 
 package org.apache.jmeter.protocol.http.util;
 
 import java.io.IOException;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.xml.sax.SAXException;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * For now I use DOM for WSDLHelper, but it would be more efficient to use JAXB
  * to generate an object model for WSDL and use it to perform serialization and
  * deserialization. It also makes it easier to traverse the WSDL to get
  * necessary information.
  * <p>
  * Created on: Jun 3, 2003<br>
  *
  */
 public class WSDLHelper {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/"; //$NON-NLS-1$
 
     private static final String SOAP11_BINDING_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/"; //$NON-NLS-1$
     
     private static final String SOAP12_BINDING_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/"; //$NON-NLS-1$
 
     private static int GET_WDSL_TIMEOUT = 5000; // timeout to retrieve wsdl when server not response
     
     /**
      * -------------------------------------------- The members used by the
      * class to do its work --------------------------------------------
      */
 
     private URL WSDLURL = null;
 
     private URLConnection CONN = null;
 
     private Document WSDLDOC = null;
 
     private String SOAPBINDING = null;
 
     private URL bindingURL = null;
 
     private Object[] SOAPOPS = null;
 
     private final Map<String, String> ACTIONS = new HashMap<String, String>();
 
     private final AuthManager AUTH;
 
     /**
      * Default constructor takes a string URL
+     *
+     * @param url
+     *            url to the wsdl
+     * @throws MalformedURLException
+     *             if <code>url</code> is malformed
      */
     public WSDLHelper(String url) throws MalformedURLException {
         this(url, null);
     }
 
+    /**
+     * @param url
+     *            url to the wsdl
+     * @param auth
+     *            {@link AuthManager} to use
+     * @throws MalformedURLException
+     *             if <code>url</code> is malformed
+     */
     public WSDLHelper(String url, AuthManager auth) throws MalformedURLException {
         WSDLURL = new URL(url);
         this.AUTH = auth;
     }
 
     /**
      * Returns the URL
      *
      * @return the URL
      */
     public URL getURL() {
         return this.WSDLURL;
     }
 
     /**
      * Return the protocol from the URL. this is needed, so that HTTPS works
      * as expected.
+     *
+     * @return protocol extracted from url
      */
     public String getProtocol() {
         return this.bindingURL.getProtocol();
     }
 
     /**
      * Return the host in the WSDL binding address
+     *
+     * @return host extracted from url
      */
     public String getBindingHost() {
         return this.bindingURL.getHost();
     }
 
     /**
      * Return the path in the WSDL for the binding address
+     *
+     * @return path extracted from url
      */
     public String getBindingPath() {
         return this.bindingURL.getPath();
     }
 
     /**
      * Return the port for the binding address
+     *
+     * @return port extracted from url
      */
     public int getBindingPort() {
         return this.bindingURL.getPort();
     }
 
     /**
      * Returns the binding point for the webservice. Right now it naively
      * assumes there's only one binding point with numerous soap operations.
      *
      * @return String
      */
     public String getBinding() {
         try {
             NodeList services = this.WSDLDOC.getElementsByTagNameNS(WSDL_NAMESPACE, "service");
             // the document should only have one service node
             // if it doesn't it may not work!
             Element node = (Element) services.item(0);
             NodeList ports = node.getElementsByTagNameNS(WSDL_NAMESPACE, "port");            
             if(ports.getLength()>0) {
                 Element pnode = (Element) ports.item(0);
                 // NOTUSED String portname = pnode.getAttribute("name");
                 // used to check binding, but now it doesn't. it was
                 // failing when wsdl did not using binding as expected
                 NodeList servlist = pnode.getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE, "address");
                 // check soap12
                 if (servlist.getLength() == 0) {
                     servlist = pnode.getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE, "address");
                 }
                 Element addr = (Element) servlist.item(0);
                 this.SOAPBINDING = addr.getAttribute("location");
                 this.bindingURL = new URL(this.SOAPBINDING);
                 return this.SOAPBINDING;
             } else {
                 return null;
             }
         } catch (Exception exception) {
             log.warn("Exception calling getBinding:"+exception.getMessage(),exception);
             return null;
         }
     }
 
     /**
      * Method is used internally to connect to the URL. It's protected;
      * therefore external classes should use parse to get the resource at the
      * given location.
      *
      * @throws IOException
      */
     protected void connect() throws IOException {
         CONN = WSDLURL.openConnection();
         CONN.setConnectTimeout(GET_WDSL_TIMEOUT);
         CONN.setReadTimeout(GET_WDSL_TIMEOUT);
         // in the rare case the WSDL is protected and requires
         // authentication, use the AuthManager to set the
         // authorization. Basic and Digest authorization are
         // pretty weak and don't provide real security.
         if (CONN instanceof HttpURLConnection && this.AUTH != null && this.AUTH.getAuthHeaderForURL(this.WSDLURL) != null) {
             CONN.setRequestProperty("Authorization", this.AUTH.getAuthHeaderForURL(this.WSDLURL));
         }
     }
 
     /**
      * We try to close the connection to make sure it doesn't hang around.
      */
     protected void close() {
         try {
             if (CONN != null) {
                 CONN.getInputStream().close();
             }
         } catch (IOException ignored) {
             // do nothing
         }
     }
 
     /**
      * Method is used internally to parse the InputStream and build the document
      * using javax.xml.parser API.
      */
     protected void buildDocument() throws ParserConfigurationException, IOException, SAXException {
         DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
         dbfactory.setNamespaceAware(true);
         DocumentBuilder docbuild = dbfactory.newDocumentBuilder();
         WSDLDOC = docbuild.parse(CONN.getInputStream());
     }
 
     /**
      * Call this method to retrieve the WSDL. This method must be called,
      * otherwise a connection to the URL won't be made and the stream won't be
      * parsed.
+     *
+     * @throws WSDLException when parsing fails
      */
     public void parse() throws WSDLException {
         try {
             this.connect();
             this.buildDocument();
             SOAPOPS = this.getOperations();
         } catch (IOException exception) {
             throw (new WSDLException(exception));
         } catch (SAXException exception) {
             throw (new WSDLException(exception));
         } catch (ParserConfigurationException exception) {
             throw (new WSDLException(exception));
         } finally {
             this.close();
         }
     }
 
     /**
      * Get a list of the web methods as a string array.
+     *
+     * @return list of web methods
      */
     public String[] getWebMethods() {
         for (int idx = 0; idx < SOAPOPS.length; idx++) {
             // get the node
             Node act = (Node) SOAPOPS[idx];
             // get the soap:operation
             NodeList opers = ((Element) act).getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE, "operation");
             if (opers.getLength() == 0) {
                 opers = ((Element) act).getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE, "operation");
             }
 
             // there should only be one soap:operation node per operation
             Element op = (Element) opers.item(0);
             String value;
             if (op != null) {
                 value = op.getAttribute("soapAction");
             } else {
                 value = "";
             }
             String key = ((Element) act).getAttribute("name");
             this.ACTIONS.put(key, value);
         }
         Set<String> keys = this.ACTIONS.keySet();
         String[] stringmeth = new String[keys.size()];
         Object[] stringKeys = keys.toArray();
         System.arraycopy(stringKeys, 0, stringmeth, 0, keys.size());
         return stringmeth;
     }
 
     /**
      * Return the soap action matching the operation name.
+     *
+     * @param key
+     *            name of the operation
+     * @return associated action
      */
     public String getSoapAction(String key) {
         return this.ACTIONS.get(key);
     }
 
     /**
      * Get the wsdl document.
+     *
+     * @return wsdl document
      */
     public Document getWSDLDocument() {
         return WSDLDOC;
     }
 
     /**
      * Method will look at the binding nodes and see if the first child is a
      * soap:binding. If it is, it adds it to an array.
      *
      * @return Node[]
      */
     public Object[] getSOAPBindings() {
         ArrayList<Element> list = new ArrayList<Element>();
         NodeList bindings = WSDLDOC.getElementsByTagNameNS(WSDL_NAMESPACE,"binding");
         for (int idx = 0; idx < bindings.getLength(); idx++) {
             Element nd = (Element) bindings.item(idx);
             NodeList slist = nd.getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE,"binding");
             if(slist.getLength()==0) {
                 slist = nd.getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE,"binding");
             }
             if (slist.getLength() > 0) {
                 nd.getAttribute("name");
                 list.add(nd);
             }
         }
         if (list.size() > 0) {
             return list.toArray();
         }
         return new Object[0];
     }
 
     /**
      * Look at the bindings with soap operations and get the soap operations.
      * Since WSDL may describe multiple bindings and each binding may have
      * multiple soap operations, we iterate through the binding nodes with a
      * first child that is a soap binding. If a WSDL doesn't use the same
      * formatting convention, it is possible we may not get a list of all the
      * soap operations. If that is the case, getSOAPBindings() will need to be
      * changed. I should double check the WSDL spec to see what the official
      * requirement is. Another option is to get all operation nodes and check to
      * see if the first child is a soap:operation. The benefit of not getting
      * all operation nodes is WSDL could contain duplicate operations that are
      * not SOAP methods. If there are a large number of methods and half of them
      * are HTTP operations, getting all operations could slow things down.
      *
      * @return Node[]
      */
     public Object[] getOperations() {
         Object[] res = this.getSOAPBindings();
         ArrayList<Element> ops = new ArrayList<Element>();
         // first we iterate through the bindings
         for (int idx = 0; idx < res.length; idx++) {
             Element one = (Element) res[idx];
             NodeList opnodes = one.getElementsByTagNameNS(WSDL_NAMESPACE, "operation");
             // now we iterate through the operations
             for (int idz = 0; idz < opnodes.getLength(); idz++) {
                 // if the first child is soap:operation
                 // we add it to the array
                 Element child = (Element) opnodes.item(idz);
                 int numberOfSoapOperationNodes = child.getElementsByTagNameNS(SOAP11_BINDING_NAMESPACE, "operation").getLength()
                         + child.getElementsByTagNameNS(SOAP12_BINDING_NAMESPACE, "operation").getLength();
                 if (numberOfSoapOperationNodes>0) {
                     ops.add(child);
                 }
             }
         }
         return ops.toArray();
     }
 
     /**
      * return the "wsdl method name" from a soap action
      * @param soapAction the soap action
      * @return the associated "wsdl method name" or null if not found
      */
     public String getSoapActionName(String soapAction) {
         for (Map.Entry<String, String> entry : ACTIONS.entrySet()) {
             if (entry.getValue().equals(soapAction)) {
                 return entry.getKey();
             }
         }
         return null;
     }
     
     /**
      * Simple test for the class uses bidbuy.wsdl from Apache's soap driver
      * examples.
      *
-     * @param args
+     * @param args standard arguments for a main class (not used here)
      */
     public static void main(String[] args) {
         try {
             WSDLHelper help =
             // new WSDLHelper("http://localhost/WSTest/WSTest.asmx?WSDL");
             // new WSDLHelper("http://localhost/AxisWSDL.xml");
             //new WSDLHelper("http://localhost:8080/WSMyUpper.wsdl");
             //new WSDLHelper("http://localhost:8080/test.wsdl");
             new WSDLHelper("http://localhost:8080/ServiceGateway.wsdl");
             // new WSDLHelper("http://services.bio.ifi.lmu.de:1046/prothesaurus/services/BiologicalNameService?wsdl");
             long start = System.currentTimeMillis();
             help.parse();
             String[] methods = help.getWebMethods();
             System.out.println("el: " + (System.currentTimeMillis() - start));
             for (int idx = 0; idx < methods.length; idx++) {
                 System.out.println("method name: " + methods[idx]);
             }
             System.out.println("service url: " + help.getBinding());
             System.out.println("protocol: " + help.getProtocol());
             System.out.println("port=" + help.getURL().getPort());
         } catch (Exception exception) {
             System.out.println("main method catch:");
             exception.printStackTrace();
         }
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Filter.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Filter.java
index 25808bc5c..d014e83e6 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Filter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Filter.java
@@ -1,106 +1,111 @@
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
 
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * Description:<br>
  * <br>
  * Filter interface is designed to make it easier to use Access Logs for JMeter
  * test plans. Normally, a person would have to clean a log file manually and
  * create the JMeter requests. The access log parse utility uses the filter to
  * include/exclude files by either file name or regular expression pattern.
  * <p>
  * It will also be used by HttpSamplers that use access logs. Using access logs
  * is intended as a way to simulate production traffic. For functional testing,
  * it is better to use the standard functional testing tools in JMeter. Using
  * access logs can also reduce the amount of memory needed to run large test
  * plans. <br>
  *
  * @version $Revision$
  */
 
 public interface Filter {
 
     /**
      * @param oldextension
      * @param newextension
      */
     void setReplaceExtension(String oldextension, String newextension);
 
     /**
      * Include all files in the array.
      *
      * @param filenames
      */
     void includeFiles(String[] filenames);
 
     /**
      * Exclude all files in the array
      *
      * @param filenames
      */
     void excludeFiles(String[] filenames);
 
     /**
      * Include any log entry that contains the following regular expression
      * pattern.
      *
      * @param regexp
      */
     void includePattern(String[] regexp);
 
     /**
      * Exclude any log entry that contains the following regular expression
      * pattern.
      *
      * @param regexp
      */
     void excludePattern(String[] regexp);
 
     /**
      * Log parser will call this method to see if a particular entry should be
      * filtered or not.
      *
      * @param path
-     * @return boolean
+     *            log line that should be checked if it should to be filtered
+     *            out
+     * @param sampler
+     *            {@link TestElement} in which the line would be added
+     * @return boolean <code>true</code> if line should be filtered out,
+     *         <code>false</code> otherwise
      */
     boolean isFiltered(String path,TestElement sampler);
 
     /**
      * In case the user wants to replace the file extension, log parsers should
      * call this method. This is useful for regression test plans. If a website
      * is migrating from one platform to another and the file extension changes,
      * the filter provides an easy way to do it without spending a lot of time.
      *
      * @param text
      * @return String
      */
     String filter(String text);
 
     /**
      * Tell the filter when the parsing has reached the end of the log file and
      * is about to begin again. Gives the filter a chance to adjust it's values,
      * if needed.
      *
      */
     void reset();
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java
index 20d15bf04..ee885acd9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/Generator.java
@@ -1,136 +1,138 @@
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
 
 /**
  * Description:<br>
  * <br>
  * Generator is a base interface that defines the minimum methods needed to
  * implement a concrete generator. The reason for creating this interface is
  * eventually JMeter could use the logs directly rather than pre- process the
  * logs into a JMeter .jmx file. In situations where a test plan simulates load
  * from production logs, it is more efficient for JMeter to use the logs
  * directly.
  * <p>
  * From first hand experience, loading a test plan with 10K or more Requests
  * requires a lot of memory. It's important to keep in mind this type of testing
  * is closer to functional and regression testing than the typical stress tests.
  * Typically, this kind of testing is most useful for search sites that get a
  * large number of requests per day, but the request parameters vary
  * dramatically. E-commerce sites typically have limited inventory, therefore it
  * is better to design test plans that use data from the database.
  * <p>
  *
  * @version $Revision$
  */
 
 public interface Generator {
 
     /**
      * close the generator
      */
     void close();
 
     /**
      * The host is the name of the server.
      *
      * @param host
      */
     void setHost(String host);
 
     /**
      * This is the label for the request, which is used in the logs and results.
      *
      * @param label
      */
     void setLabel(String label);
 
     /**
      * The method is the HTTP request method. It's normally POST or GET.
      *
      * @param post_get
      */
     void setMethod(String post_get);
 
     /**
      * Set the request parameters
      *
      * @param params
      */
     void setParams(NVPair[] params);
 
     /**
      * The path is the web page you want to test.
      *
      * @param path
      */
     void setPath(String path);
 
     /**
      * The default port for HTTP is 80, but not all servers run on that port.
      *
      * @param port -
      *            port number
      */
     void setPort(int port);
 
     /**
      * Set the querystring for the request if the method is GET.
      *
      * @param querystring
      */
     void setQueryString(String querystring);
 
     /**
      * The source logs is the location where the access log resides.
      *
      * @param sourcefile
      */
     void setSourceLogs(String sourcefile);
 
     /**
      * The target can be either a java.io.File or a Sampler. We make it generic,
      * so that later on we can use these classes directly from a HTTPSampler.
      *
      * @param target
      */
     void setTarget(Object target);
 
     /**
      * The method is responsible for calling the necessary methods to generate a
      * valid request. If the generator is used to pre-process access logs, the
      * method wouldn't return anything. If the generator is used by a control
      * element, it should return the correct Sampler class with the required
      * fields set.
+     *
+     * @return prefilled sampler
      */
     Object generateRequest();
 
     /**
      * If the generator is converting the logs to a .jmx file, save should be
      * called.
      */
     void save();
 
     /**
      * The purpose of the reset is so Samplers can explicitly call reset to
      * create a new instance of HTTPSampler.
      *
      */
     void reset();
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogParser.java
index 787b00f97..b9ed226ed 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogParser.java
@@ -1,76 +1,77 @@
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
 
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * Description:<br>
  * <br>
  * LogParser is the base interface for classes implementing concrete parse
  * logic. For an example of how to use the interface, look at the Tomcat access
  * log parser.
  * <p>
  * The original log parser was written in 2 hours to parse access logs. Since
  * then, the design and implementation has been rewritten from scratch several
  * times to make it more generic and extensible. The first version was hard
  * coded and written over the weekend.
  * <p>
  *
  * @version $Revision$
  */
 
 public interface LogParser {
 
     /**
      * close the any streams or readers.
      */
     void close();
 
     /**
      * the method will parse the given number of lines. Pass "-1" to parse the
      * entire file. If the end of the file is reached without parsing a line, a
      * 0 is returned. If the method is subsequently called again, it will
      * restart parsing at the beginning.
      *
-     * @param count
-     * @return int
+     * @param count max lines to parse, or <code>-1</code> for the entire file
+     * @param el {@link TestElement} to read lines into
+     * @return number of lines parsed
      */
     int parseAndConfigure(int count, TestElement el);
 
     /**
      * We allow for filters, so that users can simply point to an Access log
      * without having to clean it up. This makes it significantly easier and
      * reduces the amount of work. Plus I'm lazy, so going through a log file to
      * clean it up is a bit tedious. One example of this is using the filter to
      * exclude any log entry that has a 505 response code.
      *
      * @param filter
      */
     void setFilter(Filter filter);
 
     /**
      * The method is provided to make it easy to dynamically create new classes
      * using Class.newInstance(). Then the access log file is set using this
      * method.
      *
      * @param source
      */
     void setSourceFile(String source);
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/OrderPreservingLogParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/OrderPreservingLogParser.java
index f678073b5..10ec624d2 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/OrderPreservingLogParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/OrderPreservingLogParser.java
@@ -1,46 +1,46 @@
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
 
 import org.apache.jmeter.testelement.TestElement;
 
 public class OrderPreservingLogParser extends SharedTCLogParser {
 
     public OrderPreservingLogParser() {
         super();
     }
 
     public OrderPreservingLogParser(String source) {
         super(source);
     }
 
     /**
      * parse a set number of lines from the access log. Keep in mind the number
      * of lines parsed will depend the filter and number of lines in the log.
      * The method returns the actual lines parsed.
      * 
-     * @param count
+     * @param count number of max lines to read
      * @return lines parsed
      */
     @Override
     public synchronized int parseAndConfigure(int count, TestElement el) {
         return this.parse(el, count);
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
index 86fc64c5e..7999dda88 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/StandardGenerator.java
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
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
  */
 
 public class StandardGenerator implements Generator, Serializable {
 
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     protected HTTPSamplerBase SAMPLE = null;
 
     protected transient FileWriter WRITER = null;
 
     protected transient OutputStream OUTPUT = null;
 
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
-     * @param file
+     * @param file name of a file (TODO seems not to be used anywhere)
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
     private void init() {// called from ctor, so must not be overridable
         generateRequest();
     }
 
     /**
      * Create the FileWriter to save the JMX file.
      */
     protected void initStream() {
         try {
             this.OUTPUT = new FileOutputStream(FILE);
         } catch (IOException exception) {
             log.error(exception.getMessage());
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void close() {
         JOrphanUtils.closeQuietly(OUTPUT);
         JOrphanUtils.closeQuietly(WRITER);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setHost(String host) {
         SAMPLE.setDomain(host);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setLabel(String label) {
 
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setMethod(String post_get) {
         SAMPLE.setMethod(post_get);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setParams(NVPair[] params) {
         for (int idx = 0; idx < params.length; idx++) {
             SAMPLE.addArgument(params[idx].getName(), params[idx].getValue());
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setPath(String path) {
         SAMPLE.setPath(path);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setPort(int port) {
         SAMPLE.setPort(port);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setQueryString(String querystring) {
         SAMPLE.parseArguments(querystring);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setSourceLogs(String sourcefile) {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setTarget(Object target) {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object generateRequest() {
         SAMPLE = HTTPSamplerFactory.newInstance();
         return SAMPLE;
     }
 
     /**
      * save must be called to write the jmx file, otherwise it will not be
      * saved.
      */
     @Override
     public void save() {
         // no implementation at this time, since
         // we bypass the idea of having a console
         // tool to generate test plans. Instead
         // I decided to have a sampler that uses
         // the generator and parser directly
     }
 
     /**
      * Reset the HTTPSampler to make sure it is a new instance.
      * <p>
      * {@inheritDoc}
      */
     @Override
     public void reset() {
         SAMPLE = null;
         generateRequest();
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
index 575d76f10..3dca711ce 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/TCLogParser.java
@@ -1,585 +1,586 @@
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
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.StringTokenizer;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 // For JUnit tests, @see TestTCLogParser
 
 /**
  * Description:<br>
  * <br>
  * Currently the parser only handles GET/POST requests. It's easy enough to add
  * support for other request methods by changing checkMethod. The is a complete
  * rewrite of a tool I wrote for myself earlier. The older algorithm was basic
  * and did not provide the same level of flexibility I want, so I wrote a new
  * one using a totally new algorithm. This implementation reads one line at a
  * time using BufferedReader. When it gets to the end of the file and the
  * sampler needs to get more requests, the parser will re-initialize the
  * BufferedReader. The implementation uses StringTokenizer to create tokens.
  * <p>
  * The parse algorithm is the following:
  * <p>
  * <ol>
  * <li> cleans the entry by looking for backslash "\"
  * <li> looks to see if GET or POST is in the line
  * <li> tokenizes using quotes "
  * <li> finds the token with the request method
  * <li> gets the string of the token and tokenizes it using space
  * <li> finds the first token beginning with slash character
  * <li> tokenizes the string using question mark "?"
  * <li> get the path from the first token
  * <li> returns the second token and checks it for parameters
  * <li> tokenizes the string using ampersand "&amp;"
  * <li> parses each token to name/value pairs
  * </ol>
  * <p>
  * Extending this class is fairly simple. Most access logs use the same format
  * starting from the request method. Therefore, changing the implementation of
  * cleanURL(string) method should be sufficient to support new log formats.
  * Tomcat uses common log format, so any webserver that uses the format should
  * work with this parser. Servers that are known to use non standard formats are
  * IIS and Netscape.
  * <p>
  *
  */
 
 public class TCLogParser implements LogParser {
     protected static final Logger log = LoggingManager.getLoggerForClass();
 
     /*
      * TODO should these fields be public?
      * They don't appear to be used externally.
      * 
      * Also, are they any different from HTTPConstants.GET etc. ?
      * In some cases they seem to be used as the method name from the Tomcat log.
      * However the RMETHOD field is used as the value for HTTPSamplerBase.METHOD,
      * for which HTTPConstants is most approriate.
      */
     public static final String GET = "GET";
 
     public static final String POST = "POST";
 
     public static final String HEAD = "HEAD";
 
     /** protected members * */
     protected String RMETHOD = null;
 
     /**
      * The path to the access log file
      */
     protected String URL_PATH = null;
 
     protected boolean useFILE = true;
 
     protected File SOURCE = null;
 
     protected String FILENAME = null;
 
     protected BufferedReader READER = null;
 
     /**
      * Handles to supporting classes
      */
     protected Filter FILTER = null;
 
     /**
      * by default, we probably should decode the parameter values
      */
     protected boolean decode = true;
 
     // TODO downcase UPPER case non-final variables
 
     /**
      *
      */
     public TCLogParser() {
         super();
     }
 
     /**
-     * @param source
+     * @param source name of the source file
      */
     public TCLogParser(String source) {
         setSourceFile(source);
     }
 
     /**
      * by default decode is set to true. if the parameters shouldn't be
      * decoded, call the method with false
      * @param decodeparams flag whether parameters should be decoded
      */
     public void setDecodeParameterValues(boolean decodeparams) {
         this.decode = decodeparams;
     }
 
     /**
      * decode the parameter values is to true by default
      * @return <code>true</code> if parameter values should be decoded, <code>false</code> otherwise
      */
     public boolean decodeParameterValue() {
         return this.decode;
     }
 
     /**
      * Calls this method to set whether or not to use the path in the log. We
      * may want to provide the ability to filter the log file later on. By
      * default, the parser uses the file in the log.
      *
      * @param file
+     *            flag whether to use the path from the log
      */
     public void setUseParsedFile(boolean file) {
         this.useFILE = file;
     }
 
     /**
      * Use the filter to include/exclude files in the access logs. This is
      * provided as a convenience and reduce the need to spend hours cleaning up
      * log files.
      *
      * @param filter {@link Filter} to be used while reading the log lines
      */
     @Override
     public void setFilter(Filter filter) {
         FILTER = filter;
     }
 
     /**
      * Sets the source file.
      *
      * @param source name of the source file
      */
     @Override
     public void setSourceFile(String source) {
         this.FILENAME = source;
     }
 
     /**
      * parse the entire file.
      *
      * @param el TestElement to read the lines into
      * @param parseCount number of max lines to read
      * @return number of read lines, or <code>-1</code> if an error occurred while reading
      */
     public int parse(TestElement el, int parseCount) {
         if (this.SOURCE == null) {
             this.SOURCE = new File(this.FILENAME);
         }
         try {
             if (this.READER == null) {
                 this.READER = getReader(this.SOURCE);
             }
             return parse(this.READER, el, parseCount);
         } catch (Exception exception) {
             log.error("Problem creating samples", exception);
         }
         return -1;// indicate that an error occured
     }
 
     private static BufferedReader getReader(File file) throws IOException {
         if (! isGZIP(file)) {
             return new BufferedReader(new FileReader(file));
         }
         GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
         return new BufferedReader(new InputStreamReader(in));
     }
 
     private static boolean isGZIP(File file) throws IOException {
         FileInputStream in = new FileInputStream(file);
         try {
             return in.read() == (GZIPInputStream.GZIP_MAGIC & 0xFF)
                 && in.read() == (GZIPInputStream.GZIP_MAGIC >> 8);
         } finally {
             in.close();
         }
     }
 
     /**
      * parse a set number of lines from the access log. Keep in mind the number
      * of lines parsed will depend on the filter and number of lines in the log.
      * The method returns the actual number of lines parsed.
      *
      * @param count number of lines to read
      * @param el {@link TestElement} to read lines into
      * @return lines parsed
      */
     @Override
     public int parseAndConfigure(int count, TestElement el) {
         return this.parse(el, count);
     }
 
     /**
      * The method is responsible for reading each line, and breaking out of the
      * while loop if a set number of lines is given.<br>
      * Note: empty lines will not be counted
      *
      * @param breader {@link BufferedReader} to read lines from
      * @param el {@link TestElement} to read lines into
      * @param parseCount number of lines to read
      */
     protected int parse(BufferedReader breader, TestElement el, int parseCount) {
         int actualCount = 0;
         String line = null;
         try {
             // read one line at a time using
             // BufferedReader
             line = breader.readLine();
             while (line != null) {
                 if (line.length() > 0) {
                     actualCount += this.parseLine(line, el);
                 }
                 // we check the count to see if we have exceeded
                 // the number of lines to parse. There's no way
                 // to know where to stop in the file. Therefore
                 // we use break to escape the while loop when
                 // we've reached the count.
                 if (parseCount != -1 && actualCount >= parseCount) {
                     break;
                 }
                 line = breader.readLine();
             }
             if (line == null) {
                 breader.close();
                 this.READER = null;
                 // this.READER = new BufferedReader(new
                 // FileReader(this.SOURCE));
                 // parse(this.READER,el);
             }
         } catch (IOException ioe) {
             log.error("Error reading log file", ioe);
         }
         return actualCount;
     }
 
     /**
      * parseLine calls the other parse methods to parse the given text.
      *
      * @param line single line to be parsed
      * @param el {@link TestElement} in which the line will be added
      */
     protected int parseLine(String line, TestElement el) {
         int count = 0;
         // we clean the line to get
         // rid of extra stuff
         String cleanedLine = this.cleanURL(line);
         log.debug("parsing line: " + line);
         // now we set request method
         el.setProperty(HTTPSamplerBase.METHOD, RMETHOD);
         if (FILTER != null) {
             log.debug("filter is not null");
             if (!FILTER.isFiltered(line,el)) {
                 log.debug("line was not filtered");
                 // increment the current count
                 count++;
                 // we filter the line first, before we try
                 // to separate the URL into file and
                 // parameters.
                 line = FILTER.filter(cleanedLine);
                 if (line != null) {
                     createUrl(line, el);
                 }
             } else {
                 log.debug("Line was filtered");
             }
         } else {
             log.debug("filter was null");
             // increment the current count
             count++;
             // in the case when the filter is not set, we
             // parse all the lines
             createUrl(cleanedLine, el);
         }
         return count;
     }
 
     /**
      * @param line single line of which the url should be extracted 
      * @param el {@link TestElement} into which the url will be added
      */
     private void createUrl(String line, TestElement el) {
         String paramString = null;
         // check the URL for "?" symbol
         paramString = this.stripFile(line, el);
         if (paramString != null) {
             this.checkParamFormat(line);
             // now that we have stripped the file, we can parse the parameters
             this.convertStringToJMRequest(paramString, el);
         }
     }
 
     /**
      * The method cleans the URL using the following algorithm.
      * <ol>
      * <li> check for double quotes
      * <li> check the request method
      * <li> tokenize using double quotes
      * <li> find first token containing request method
      * <li> tokenize string using space
      * <li> find first token that begins with "/"
      * </ol>
      * Example Tomcat log entry:
      * <p>
      * 127.0.0.1 - - [08/Jan/2003:07:03:54 -0500] "GET /addrbook/ HTTP/1.1" 200
      * 1981
      * <p>
      * would result in the extracted url <code>/addrbook/</code>
      *
      * @param entry line from which the url is to be extracted
      * @return cleaned url
      */
     public String cleanURL(String entry) {
         String url = entry;
         // if the string contains atleast one double
         // quote and checkMethod is true, go ahead
         // and tokenize the string.
         if (entry.indexOf('"') > -1 && checkMethod(entry)) {
             StringTokenizer tokens = null;
             // we tokenize using double quotes. this means
             // for tomcat we should have 3 tokens if there
             // isn't any additional information in the logs
             tokens = this.tokenize(entry, "\"");
             while (tokens.hasMoreTokens()) {
                 String toke = tokens.nextToken();
                 // if checkMethod on the token is true
                 // we tokenzie it using space and escape
                 // the while loop. Only the first matching
                 // token will be used
                 if (checkMethod(toke)) {
                     StringTokenizer token2 = this.tokenize(toke, " ");
                     while (token2.hasMoreTokens()) {
                         String t = (String) token2.nextElement();
                         if (t.equalsIgnoreCase(GET)) {
                             RMETHOD = GET;
                         } else if (t.equalsIgnoreCase(POST)) {
                             RMETHOD = POST;
                         } else if (t.equalsIgnoreCase(HEAD)) {
                             RMETHOD = HEAD;
                         }
                         // there should only be one token
                         // that starts with slash character
                         if (t.startsWith("/")) {
                             url = t;
                             break;
                         }
                     }
                     break;
                 }
             }
             return url;
         }
         // we return the original string
         return url;
     }
 
     /**
      * The method checks for <code>POST</code>, <code>GET</code> and <code>HEAD</code> methods currently.
      * The other methods aren't supported yet.
      *
      * @param text text to be checked for HTTP method
      * @return <code>true</code> if method is supported, <code>false</code> otherwise
      */
     public boolean checkMethod(String text) {
         if (text.indexOf("GET") > -1) {
             this.RMETHOD = GET;
             return true;
         } else if (text.indexOf("POST") > -1) {
             this.RMETHOD = POST;
             return true;
         } else if (text.indexOf("HEAD") > -1) {
             this.RMETHOD = HEAD;
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * Tokenize the URL into two tokens. If the URL has more than one "?", the
      * parse may fail. Only the first two tokens are used. The first token is
      * automatically parsed and set at {@link TCLogParser#URL_PATH URL_PATH}.
      *
      * @param url url which should be stripped from parameters
      * @param el {@link TestElement} to parse url into
      * @return String presenting the parameters, or <code>null</code> when none where found
      */
     public String stripFile(String url, TestElement el) {
         if (url.indexOf('?') > -1) {
             StringTokenizer tokens = this.tokenize(url, "?");
             this.URL_PATH = tokens.nextToken();
             el.setProperty(HTTPSamplerBase.PATH, URL_PATH);
             return tokens.hasMoreTokens() ? tokens.nextToken() : null;
         }
         el.setProperty(HTTPSamplerBase.PATH, url);
         return null;
     }
 
     /**
      * Checks the string to make sure it has <code>/path/file?name=value</code> format. If
      * the string doesn't contains a "?", it will return <code>false</code>.
      *
      * @param url url to check for parameters
      * @return <code>true</code> if url contains a <code>?</code>,
      *         <code>false</code> otherwise
      */
     public boolean checkURL(String url) {
         if (url.indexOf('?') > -1) {
             return true;
         }
         return false;
     }
 
     /**
      * Checks the string to see if it contains "&amp;" and "=". If it does, return
      * <code>true</code>, so that it can be parsed.
      *
      * @param text text to be checked for <code>&amp;</code> and <code>=</code>
      * @return <code>true</code> if <code>text</code> contains both <code>&amp;</code>
      *         and <code>=</code>, <code>false</code> otherwise
      */
     public boolean checkParamFormat(String text) {
         if (text.indexOf('&') > -1 && text.indexOf('=') > -1) {
             return true;
         }
         return false;
     }
 
     /**
      * Convert a single line into XML
      *
      * @param text to be be converted
      * @param el {@link HTTPSamplerBase} which consumes the <code>text</code>
      */
     public void convertStringToJMRequest(String text, TestElement el) {
         ((HTTPSamplerBase) el).parseArguments(text);
     }
 
     /**
      * Parse the string parameters into NVPair[] array. Once they are parsed, it
      * is returned. The method uses parseOneParameter(string) to convert each
      * pair.
      *
      * @param stringparams String with parameters to be parsed
      * @return array of {@link NVPair}s
      */
     public NVPair[] convertStringtoNVPair(String stringparams) {
         List<String> vparams = this.parseParameters(stringparams);
         NVPair[] nvparams = new NVPair[vparams.size()];
         // convert the Parameters
         for (int idx = 0; idx < nvparams.length; idx++) {
             nvparams[idx] = this.parseOneParameter(vparams.get(idx));
         }
         return nvparams;
     }
 
     /**
      * Method expects name and value to be separated by an equal sign "=". The
      * method uses StringTokenizer to make a NVPair object. If there happens to
      * be more than one "=" sign, the others are ignored. The chance of a string
      * containing more than one is unlikely and would not conform to HTTP spec.
      * I should double check the protocol spec to make sure this is accurate.
      *
      * @param parameter
      *            to be parsed
      * @return {@link NVPair} with the parsed name and value of the parameter
      */
     protected NVPair parseOneParameter(String parameter) {
         String name = ""; // avoid possible NPE when trimming the name
         String value = null;
         try {
             StringTokenizer param = this.tokenize(parameter, "=");
             name = param.nextToken();
             value = param.nextToken();
         } catch (Exception e) {
             // do nothing. it's naive, but since
             // the utility is meant to parse access
             // logs the formatting should be correct
         }
         if (value == null) {
             value = "";
         } else {
             if (decode) {
                 try {
                     value = URLDecoder.decode(value,"UTF-8");
                 } catch (UnsupportedEncodingException e) {
                     log.warn(e.getMessage());
                 }
             }
         }
         return new NVPair(name.trim(), value.trim());
     }
 
     /**
      * Method uses StringTokenizer to convert the string into single pairs. The
      * string should conform to HTTP protocol spec, which means the name/value
      * pairs are separated by the ampersand symbol "&amp;". Someone could write the
      * querystrings by hand, but that would be round about and go against the
      * purpose of this utility.
      *
      * @param parameters string to be parsed
      * @return List of name/value pairs
      */
     protected List<String> parseParameters(String parameters) {
         List<String> parsedParams = new ArrayList<String>();
         StringTokenizer paramtokens = this.tokenize(parameters, "&");
         while (paramtokens.hasMoreElements()) {
             parsedParams.add(paramtokens.nextToken());
         }
         return parsedParams;
     }
 
     /**
      * Parses the line using java.util.StringTokenizer.
      *
      * @param line
      *            line to be parsed
      * @param delim
      *            delimiter
      * @return StringTokenizer constructed with <code>line</code> and <code>delim</code>
      */
     public StringTokenizer tokenize(String line, String delim) {
         return new StringTokenizer(line, delim);
     }
 
     @Override
     public void close() {
         try {
             this.READER.close();
             this.READER = null;
             this.SOURCE = null;
         } catch (IOException e) {
             // do nothing
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java b/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java
index e0ba1c8d7..43fcb032b 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/visualizers/RequestViewHTTP.java
@@ -1,353 +1,354 @@
 /*
 o * Licensed to the Apache Software Foundation (ASF) under one or more
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
 
 package org.apache.jmeter.protocol.http.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.io.UnsupportedEncodingException;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Set;
 
 import javax.swing.JPanel;
 import javax.swing.JSplitPane;
 import javax.swing.JTable;
 import javax.swing.table.TableCellRenderer;
 import javax.swing.table.TableColumn;
 
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
 import org.apache.jmeter.gui.util.TextBoxDialoger.TextBoxDoubleClick;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.RequestView;
 import org.apache.jmeter.visualizers.SamplerResultTab.RowResult;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
 import org.apache.jorphan.gui.RendererUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.log.Logger;
 
 /**
  * Specializer panel to view a HTTP request parsed
  *
  */
 public class RequestViewHTTP implements RequestView {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String KEY_LABEL = "view_results_table_request_tab_http"; //$NON-NLS-1$
     
     private static final String CHARSET_DECODE = "ISO-8859-1"; //$NON-NLS-1$
     
     private static final String PARAM_CONCATENATE = "&"; //$NON-NLS-1$
 
     private JPanel paneParsed;
 
     private ObjectTableModel requestModel = null;
 
     private ObjectTableModel paramsModel = null;
 
     private ObjectTableModel headersModel = null;
 
     private static final String[] COLUMNS_REQUEST = new String[] {
             " ", // one space for blank header // $NON-NLS-1$ 
             " " }; // one space for blank header  // $NON-NLS-1$
 
     private static final String[] COLUMNS_PARAMS = new String[] {
             "view_results_table_request_params_key", // $NON-NLS-1$
             "view_results_table_request_params_value" }; // $NON-NLS-1$
 
     private static final String[] COLUMNS_HEADERS = new String[] {
             "view_results_table_request_headers_key", // $NON-NLS-1$
             "view_results_table_request_headers_value" }; // $NON-NLS-1$
 
     private JTable tableRequest = null;
 
     private JTable tableParams = null;
 
     private JTable tableHeaders = null;
 
     // Request headers column renderers
     private static final TableCellRenderer[] RENDERERS_REQUEST = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     // Request headers column renderers
     private static final TableCellRenderer[] RENDERERS_PARAMS = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     // Request headers column renderers
     private static final TableCellRenderer[] RENDERERS_HEADERS = new TableCellRenderer[] {
             null, // Key
             null, // Value
     };
 
     /**
      * Pane to view HTTP request sample in view results tree
      */
     public RequestViewHTTP() {
         requestModel = new ObjectTableModel(COLUMNS_REQUEST, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
         paramsModel = new ObjectTableModel(COLUMNS_PARAMS, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
         headersModel = new ObjectTableModel(COLUMNS_HEADERS, RowResult.class, // The object used for each row
                 new Functor[] {
                         new Functor("getKey"), // $NON-NLS-1$
                         new Functor("getValue") }, // $NON-NLS-1$
                 new Functor[] {
                         null, null }, new Class[] {
                         String.class, String.class }, false);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#init()
      */
     @Override
     public void init() {
         paneParsed = new JPanel(new BorderLayout(0, 5));
         paneParsed.add(createRequestPane());
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#clearData()
      */
     @Override
     public void clearData() {
         requestModel.clearData();
         paramsModel.clearData();
         headersModel.clearData(); // clear results table before filling
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#setSamplerResult(java.lang.Object)
      */
     @Override
     public void setSamplerResult(Object objectResult) {
 
         if (objectResult instanceof HTTPSampleResult) {
             HTTPSampleResult sampleResult = (HTTPSampleResult) objectResult;
 
             // Display with same order HTTP protocol
             requestModel.addRow(new RowResult(
                     JMeterUtils.getResString("view_results_table_request_http_method"), //$NON-NLS-1$
                     sampleResult.getHTTPMethod()));
 
             URL hUrl = sampleResult.getURL();
             if (hUrl != null){ // can be null - e.g. if URL was invalid
                 requestModel.addRow(new RowResult(JMeterUtils
                         .getResString("view_results_table_request_http_protocol"), //$NON-NLS-1$
                         hUrl.getProtocol()));
                 requestModel.addRow(new RowResult(
                         JMeterUtils.getResString("view_results_table_request_http_host"), //$NON-NLS-1$
                         hUrl.getHost()));
                 int port = hUrl.getPort() == -1 ? hUrl.getDefaultPort() : hUrl.getPort();
                 requestModel.addRow(new RowResult(
                         JMeterUtils.getResString("view_results_table_request_http_port"), //$NON-NLS-1$
                         Integer.valueOf(port)));
                 requestModel.addRow(new RowResult(
                         JMeterUtils.getResString("view_results_table_request_http_path"), //$NON-NLS-1$
                         hUrl.getPath()));
     
                 String queryGet = hUrl.getQuery() == null ? "" : hUrl.getQuery(); //$NON-NLS-1$
                 // Concatenate query post if exists
                 String queryPost = sampleResult.getQueryString();
                 if (queryPost != null && queryPost.length() > 0) {
                     if (queryGet.length() > 0) {
                         queryGet += PARAM_CONCATENATE; 
                     }
                     queryGet += queryPost;
                 }
                 queryGet = RequestViewHTTP.decodeQuery(queryGet);
                 if (queryGet != null) {
                     Set<Entry<String, String>> keys = RequestViewHTTP.getQueryMap(queryGet).entrySet();
                     for (Entry<String, String> entry : keys) {
                         paramsModel.addRow(new RowResult(entry.getKey(),entry.getValue()));
                     }
                 }
             }
             // Display cookie in headers table (same location on http protocol)
             String cookie = sampleResult.getCookies();
             if (cookie != null && cookie.length() > 0) {
                 headersModel.addRow(new RowResult(
                         JMeterUtils.getParsedLabel("view_results_table_request_http_cookie"), //$NON-NLS-1$
                         sampleResult.getCookies()));
             }
             // Parsed request headers
             LinkedHashMap<String, String> lhm = JMeterUtils.parseHeaders(sampleResult.getRequestHeaders());
             for (Iterator<Map.Entry<String, String>> iterator = lhm.entrySet().iterator(); iterator.hasNext();) {
                 Map.Entry<String, String> entry = iterator.next();
                 headersModel.addRow(new RowResult(entry.getKey(), entry.getValue()));   
             }
 
         } else {
             // add a message when no http sample
             requestModel.addRow(new RowResult("", //$NON-NLS-1$
                     JMeterUtils.getResString("view_results_table_request_http_nohttp"))); //$NON-NLS-1$
         }
     }
 
     /**
      * @param query
+     *            query to parse for param and value pairs
      * @return Map params and Svalue
      */
     //TODO: move to utils class (JMeterUtils?)
     public static Map<String, String> getQueryMap(String query) {
 
         Map<String, String> map = new HashMap<String, String>();
         if (query.trim().startsWith("<?")) { // $NON-NLS-1$
             // SOAP request (generally)
             map.put(" ", query); //blank name // $NON-NLS-1$
             return map;
         }
         String[] params = query.split(PARAM_CONCATENATE);
         for (String param : params) {
             String[] paramSplit = param.split("="); // $NON-NLS-1$
             if (paramSplit.length > 2 ) {// detected invalid syntax (Bug 52491)
                 // Return as for SOAP above
                 map.clear();
                 map.put(" ", query); //blank name // $NON-NLS-1$
                 return map;
             }
             String name = null;
             if (paramSplit.length > 0) {
                 name = paramSplit[0];
             }
             String value = ""; // empty init // $NON-NLS-1$
             if (paramSplit.length > 1) {
                 // We use substring to keep = sign (Bug 54055), we are sure = is present
                 value = param.substring(param.indexOf("=")+1); // $NON-NLS-1$
             }
             map.put(name, value);
         }
         return map;
     }
 
     /**
      * Decode a query string
      * 
      * @param query
      *            to decode
      * @return a decode query string
      */
     public static String decodeQuery(String query) {
         if (query != null && query.length() > 0) {
             try {
                 query = URLDecoder.decode(query, CHARSET_DECODE); // better ISO-8859-1 than UTF-8
             } catch(IllegalArgumentException e) {
                 log.warn("Error decoding query, maybe your request parameters should be encoded:" + query, e);
                 return null;
             } catch (UnsupportedEncodingException uee) {
                 log.warn("Error decoding query, maybe your request parameters should be encoded:" + query, uee);
                 return null;
             } 
             return query;
         }
         return null;
     }
 
     @Override
     public JPanel getPanel() {
         return paneParsed;
     }
 
     /**
      * Create a pane with three tables (request, params, headers)
      * 
      * @return Pane to display request data
      */
     private Component createRequestPane() {
         // Set up the 1st table Result with empty headers
         tableRequest = new JTable(requestModel);
         tableRequest.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableRequest.addMouseListener(new TextBoxDoubleClick(tableRequest));
         
         setFirstColumnPreferredSize(tableRequest);
         RendererUtils.applyRenderers(tableRequest, RENDERERS_REQUEST);
 
         // Set up the 2nd table 
         tableParams = new JTable(paramsModel);
         tableParams.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableParams.addMouseListener(new TextBoxDoubleClick(tableParams));
         setFirstColumnPreferredSize(tableParams);
         tableParams.getTableHeader().setDefaultRenderer(
                 new HeaderAsPropertyRenderer());
         RendererUtils.applyRenderers(tableParams, RENDERERS_PARAMS);
 
         // Set up the 3rd table 
         tableHeaders = new JTable(headersModel);
         tableHeaders.setToolTipText(JMeterUtils.getResString("textbox_tooltip_cell")); // $NON-NLS-1$
         tableHeaders.addMouseListener(new TextBoxDoubleClick(tableHeaders));
         setFirstColumnPreferredSize(tableHeaders);
         tableHeaders.getTableHeader().setDefaultRenderer(
                 new HeaderAsPropertyRenderer());
         RendererUtils.applyRenderers(tableHeaders, RENDERERS_HEADERS);
 
         // Create the split pane
         JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 GuiUtils.makeScrollPane(tableParams),
                 GuiUtils.makeScrollPane(tableHeaders));
         topSplit.setOneTouchExpandable(true);
         topSplit.setResizeWeight(0.50); // set split ratio
         topSplit.setBorder(null); // see bug jdk 4131528
 
         JSplitPane paneParsed = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                 GuiUtils.makeScrollPane(tableRequest), topSplit);
         paneParsed.setOneTouchExpandable(true);
         paneParsed.setResizeWeight(0.25); // set split ratio (only 5 lines to display)
         paneParsed.setBorder(null); // see bug jdk 4131528
 
         // Hint to background color on bottom tabs (grey, not blue)
         JPanel panel = new JPanel(new BorderLayout());
         panel.add(paneParsed);
         return panel;
     }
 
     private void setFirstColumnPreferredSize(JTable table) {
         TableColumn column = table.getColumnModel().getColumn(0);
         column.setMaxWidth(300);
         column.setPreferredWidth(160);
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.visualizers.request.RequestView#getLabel()
      */
     @Override
     public String getLabel() {
         return JMeterUtils.getResString(KEY_LABEL);
     }
 }
diff --git a/test/src/org/apache/jmeter/JMeterVersionTest.java b/test/src/org/apache/jmeter/JMeterVersionTest.java
index ebe620a23..db0a1e8cf 100644
--- a/test/src/org/apache/jmeter/JMeterVersionTest.java
+++ b/test/src/org/apache/jmeter/JMeterVersionTest.java
@@ -1,238 +1,238 @@
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
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.FilenameFilter;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Properties;
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Check the eclipse and Maven version definitions against build.properties
  */
 public class JMeterVersionTest extends JMeterTestCase {
 
     // Convert between eclipse jar name and build.properties name
     private static Map<String, String> JAR_TO_BUILD_PROP = new HashMap<String, String>();
     static {
         JAR_TO_BUILD_PROP.put("bsf", "apache-bsf");
         JAR_TO_BUILD_PROP.put("bsh", "beanshell");
         JAR_TO_BUILD_PROP.put("geronimo-jms_1.1_spec", "jms");
         JAR_TO_BUILD_PROP.put("htmllexer", "htmlparser"); // two jars same version
         JAR_TO_BUILD_PROP.put("httpmime", "httpclient"); // two jars same version
         JAR_TO_BUILD_PROP.put("mail", "javamail");
         JAR_TO_BUILD_PROP.put("oro", "jakarta-oro");
         JAR_TO_BUILD_PROP.put("xercesImpl", "xerces");
         JAR_TO_BUILD_PROP.put("xpp3_min", "xpp3");
     }
 
     private static final File JMETER_HOME = new File(JMeterUtils.getJMeterHome());
 
     public JMeterVersionTest() {
         super();
     }
 
     public JMeterVersionTest(String arg0) {
         super(arg0);
     }
 
     private final Map<String, String> versions = new HashMap<String, String>();
     private final Set<String> propNames = new HashSet<String>();
 
     private File getFileFromHome(String relativeFile) {
         return new File(JMETER_HOME, relativeFile);
     }
 
     private Properties prop;
 
     @Override
     protected void setUp() throws Exception {
         final Properties buildProp = new Properties();
         final FileInputStream bp = new FileInputStream(getFileFromHome("build.properties"));
         buildProp.load(bp);
         bp.close();
         for (Entry<Object, Object> entry : buildProp.entrySet()) {
             final String key = (String) entry.getKey();
             if (key.endsWith(".version")) {
                 final String value = (String) entry.getValue();
                 final String jarprop = key.replace(".version","");
                 final String old = versions.put(jarprop, value);
                 propNames.add(jarprop);
                 if (old != null) {
                     fail("Already have entry for "+key);
                 }
             }
         }
         // remove docs-only jars
         propNames.remove("velocity");
         propNames.remove("commons-lang");
         prop = buildProp;
     }
 
     /**
      * Check eclipse.classpath contains the jars declared in build.properties
-     * @throws Exception
+     * @throws Exception if something fails
      */
     public void testEclipse() throws Exception {
         final BufferedReader eclipse = new BufferedReader(
                 new FileReader(getFileFromHome("eclipse.classpath"))); // assume default charset is OK here
 //      <classpathentry kind="lib" path="lib/geronimo-jms_1.1_spec-1.1.1.jar"/>
 //      <classpathentry kind="lib" path="lib/activation-1.1.1.jar"/>
 //      <classpathentry kind="lib" path="lib/jtidy-r938.jar"/>
         final Pattern p = Pattern.compile("\\s+<classpathentry kind=\"lib\" path=\"lib/(?:api/)?(.+?)-([^-]+(-b\\d+|-BETA\\d)?)\\.jar\"/>");
         final Pattern versionPat = Pattern.compile("\\$\\{(.+)\\.version\\}");
         String line;
         final ArrayList<String> toRemove = new ArrayList<String>();
         while((line=eclipse.readLine()) != null){
             final Matcher m = p.matcher(line);
             if (m.matches()) {
                 String jar = m.group(1);
                 String version = m.group(2);
 //                System.out.println(jar + " => " + version);
                 if (jar.endsWith("-jdk15on")) { // special handling
                     jar=jar.replace("-jdk15on","");
                 } else if (jar.equals("commons-jexl") && version.startsWith("2")) { // special handling
                     jar="commons-jexl2";
                 } else {
                     String tmp = JAR_TO_BUILD_PROP.get(jar);
                     if (tmp != null) {
                         jar = tmp;
                     }
                 }
                 String expected = versions.get(jar);
                 if(expected == null) {
                     System.err.println("Didn't find version for jar name extracted by regexp, jar name extracted:"+jar+", version extracted:"+version+", current line:"+line);
                     fail("Didn't find version for jar name extracted by regexp, jar name extracted:"+jar+", version extracted:"+version+", current line:"+line);
                 }
                 // Process ${xxx.version} references
                 final Matcher mp = versionPat.matcher(expected);
                 if (mp.matches()) {
                     String key = mp.group(1);
                     expected = versions.get(key);
                     toRemove.add(key); // in case it is not itself used we remove it later
                 }
                 propNames.remove(jar);
                 if (expected == null) {
                     fail("Versions list does not contain: " + jar);
                 } else {
                     if (!version.equals(expected)) {
                         assertEquals(jar,version,expected);
                     }
                 }
             }
         }
         // remove any possibly unused references
         for(Object key : toRemove.toArray()) {
             propNames.remove(key);            
         }
         eclipse.close();
         if (propNames.size() > 0) {
             fail("Should have no names left: "+Arrays.toString(propNames.toArray()) + ". Check eclipse.classpath");
         }
     }
 
     public void testMaven() throws Exception {
         final BufferedReader maven = new BufferedReader(
                 new FileReader(getFileFromHome("res/maven/ApacheJMeter_parent.pom"))); // assume default charset is OK here
 //      <apache-bsf.version>2.4.0</apache-bsf.version>
         final Pattern p = Pattern.compile("\\s+<([^\\.]+)\\.version>([^<]+)<.*");
 
         String line;
         while((line=maven.readLine()) != null){
             final Matcher m = p.matcher(line);
             if (m.matches()) {
                 String jar = m.group(1);
                 String version = m.group(2);
                 String expected = versions.get(jar);
                 propNames.remove(jar);
                 if (expected == null) {
                     fail("Versions list does not contain: " + jar);
                 } else {
                     if (!version.equals(expected)) {
                         assertEquals(jar,expected,version);
                     }
                 }
             }
         }
         maven.close();
         if (propNames.size() > 0) {
             fail("Should have no names left: "+Arrays.toString(propNames.toArray()) + ". Check ApacheJMeter_parent.pom");
         }
    }
 
     public void testLicences() {
         Set<String> liceNames = new HashSet<String>();
         for (Map.Entry<String, String> me : versions.entrySet()) {
         final String key = me.getKey();
             liceNames.add(key+"-"+me.getValue()+".txt");
             if (key.equals("htmlparser")) {
                 liceNames.add("htmllexer"+"-"+me.getValue()+".txt");
             }
         }
         File licencesDir = getFileFromHome("licenses/bin");
         String [] lice = licencesDir.list(new FilenameFilter() {
             @Override
             public boolean accept(File dir, String name) {
                 return ! name.equalsIgnoreCase("README.txt") 
                         && !name.equals(".svn"); // Allow for old-style SVN workspaces
             }
         });
         assertTrue("Expected at least one license file",lice.length > 0);
         for(String l : lice) {
             if (!liceNames.remove(l)) {
                 fail("Mismatched version in license file " + l);
             }
         }
     }
 
     /**
      * Check that all downloads use Maven Central
      */
     public void testMavenDownload() {
         int fails = 0;
         for (Entry<Object, Object> entry : prop.entrySet()) {
             final String key = (String) entry.getKey();
             if (key.endsWith(".loc")) {
                 final String value = (String) entry.getValue();
                 if (! value.startsWith("${maven2.repo}")) {
                     fails++;
                     System.err.println("ERROR: non-Maven download detected\n" + key + "=" +value);
                 }
             }
         }
         if (fails > 0) {
             // TODO replace with fail()
             System.err.println("ERROR: All files must be available from Maven Central; but " + fails + " use(s) a different download source");
         }
     }
 }
diff --git a/test/src/org/apache/jmeter/control/TestIfController.java b/test/src/org/apache/jmeter/control/TestIfController.java
index 0ee93c156..766a89beb 100644
--- a/test/src/org/apache/jmeter/control/TestIfController.java
+++ b/test/src/org/apache/jmeter/control/TestIfController.java
@@ -1,293 +1,293 @@
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
 
 package org.apache.jmeter.control;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.junit.stubs.TestSampler;
 import org.apache.jmeter.modifiers.CounterConfig;
 import org.apache.jmeter.sampler.DebugSampler;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 
 public class TestIfController extends JMeterTestCase {
         public TestIfController(String name) {
             super(name);
         }
         
         /**
          * See Bug 56160
-         * @throws Exception
+         * @throws Exception if something fails
          */
         public void testStackOverflow() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(1);
             controller.setContinueForever(false);
             
             IfController ifCont = new IfController("true==false");
             ifCont.setUseExpression(false);
             ifCont.setEvaluateAll(false);
             WhileController whileController = new WhileController();
             whileController.setCondition("${__javaScript(\"true\" != \"false\")}");
             whileController.addTestElement(new TestSampler("Sample1"));
             
 
             controller.addTestElement(ifCont);
             ifCont.addTestElement(whileController);
 
             Sampler sampler = null;
             int counter = 0;
             controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             whileController.setRunningVersion(true);
 
             try {
                 while ((sampler = controller.next()) != null) {
                     sampler.sample(null);
                     counter++;
                 }
                 assertEquals(0, counter);
             } catch(StackOverflowError e) {
                 fail("Stackoverflow occured in testStackOverflow");
             }
         }
         
         /**
          * See Bug 53768
-         * @throws Exception
+         * @throws Exception if something fails
          */
         public void testBug53768() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(1);
             controller.setContinueForever(false);
             
             Arguments arguments = new Arguments();
             arguments.addArgument("VAR1", "0", "=");
             
             DebugSampler debugSampler1 = new DebugSampler();
             debugSampler1.setName("VAR1 = ${VAR1}");
             
             IfController ifCont = new IfController("true==false");
             ifCont.setUseExpression(false);
             ifCont.setEvaluateAll(false);
             
             IfController ifCont2 = new IfController("true==true");
             ifCont2.setUseExpression(false);
             ifCont2.setEvaluateAll(false);
             
             CounterConfig counterConfig = new CounterConfig();
             counterConfig.setStart(1);
             counterConfig.setIncrement(1);
             counterConfig.setVarName("VAR1");
             
             DebugSampler debugSampler2 = new DebugSampler();
             debugSampler2.setName("VAR1 = ${VAR1}");
 
             controller.addTestElement(arguments);
             controller.addTestElement(debugSampler1);
             controller.addTestElement(ifCont);
             ifCont.addTestElement(ifCont2);
             ifCont2.addTestElement(counterConfig);
             controller.addTestElement(debugSampler2);
             
             
 
             controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             ifCont2.setRunningVersion(true);
             counterConfig.setRunningVersion(true);
             arguments.setRunningVersion(true);
             debugSampler1.setRunningVersion(true);
             debugSampler2.setRunningVersion(true);
             ifCont2.addIterationListener(counterConfig);
             JMeterVariables vars = new JMeterVariables();
             JMeterContext jmctx = JMeterContextService.getContext();
 
             jmctx.setVariables(vars);
             vars.put("VAR1", "0");
             try {
 
                 Sampler sampler = controller.next();
                 SampleResult sampleResult1 = sampler.sample(null);
                 assertEquals("0", vars.get("VAR1"));
                 sampler = controller.next();
                 SampleResult sampleResult2 = sampler.sample(null);
                 assertEquals("0", vars.get("VAR1"));
                 
 
             } catch(StackOverflowError e) {
                 fail("Stackoverflow occured in testStackOverflow");
             }
         }
 
         public void testProcessing() throws Exception {
 
             GenericController controller = new GenericController();
 
             controller.addTestElement(new IfController("false==false"));
             controller.addTestElement(new IfController(" \"a\".equals(\"a\")"));
             controller.addTestElement(new IfController("2<100"));
 
             //TODO enable some proper tests!!
             
             /*
              * GenericController sub_1 = new GenericController();
              * sub_1.addTestElement(new IfController("3==3"));
              * controller.addTestElement(sub_1); controller.addTestElement(new
              * IfController("false==true"));
              */
 
             /*
              * GenericController controller = new GenericController();
              * GenericController sub_1 = new GenericController();
              * sub_1.addTestElement(new IfController("10<100"));
              * sub_1.addTestElement(new IfController("true==false"));
              * controller.addTestElement(sub_1); controller.addTestElement(new
              * IfController("false==false"));
              * 
              * IfController sub_2 = new IfController(); sub_2.setCondition( "10<10000");
              * GenericController sub_3 = new GenericController();
              * 
              * sub_2.addTestElement(new IfController( " \"a\".equals(\"a\")" ) );
              * sub_3.addTestElement(new IfController("2>100"));
              * sub_3.addTestElement(new IfController("false==true"));
              * sub_2.addTestElement(sub_3); sub_2.addTestElement(new
              * IfController("2==3")); controller.addTestElement(sub_2);
              */
 
             /*
              * IfController controller = new IfController("12==12");
              * controller.initialize();
              */
 //          TestElement sampler = null;
 //          while ((sampler = controller.next()) != null) {
 //              logger.debug("    ->>>  Gonna assertTrue :" + sampler.getClass().getName() + " Property is   ---->>>"
 //                      + sampler.getName());
 //          }
         }
    
         public void testProcessingTrue() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(2);
             controller.addTestElement(new TestSampler("Sample1"));
             IfController ifCont = new IfController("true==true");
             ifCont.setEvaluateAll(true);
             ifCont.addTestElement(new TestSampler("Sample2"));
             TestSampler sample3 = new TestSampler("Sample3");            
             ifCont.addTestElement(sample3);
             controller.addTestElement(ifCont);
                         
             String[] order = new String[] { "Sample1", "Sample2", "Sample3", 
                     "Sample1", "Sample2", "Sample3" };
             int counter = 0;
             controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             
             Sampler sampler = null;
             while ((sampler = controller.next()) != null) {
                 sampler.sample(null);
                 assertEquals(order[counter], sampler.getName());
                 counter++;
             }
             assertEquals(counter, 6);
         }
         
         /**
          * Test false return on sample3 (sample4 doesn't execute)
-         * @throws Exception
+         * @throws Exception if something fails
          */
         public void testEvaluateAllChildrenWithoutSubController() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(2);
             controller.addTestElement(new TestSampler("Sample1"));
             IfController ifCont = new IfController("true==true");
             ifCont.setEvaluateAll(true);
             controller.addTestElement(ifCont);
             
             ifCont.addTestElement(new TestSampler("Sample2"));
             TestSampler sample3 = new TestSampler("Sample3");            
             ifCont.addTestElement(sample3);
             TestSampler sample4 = new TestSampler("Sample4");
             ifCont.addTestElement(sample4);
             
             String[] order = new String[] { "Sample1", "Sample2", "Sample3", 
                     "Sample1", "Sample2", "Sample3" };
             int counter = 0;
             controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             
             Sampler sampler = null;
             while ((sampler = controller.next()) != null) {
                 sampler.sample(null);
                 if (sampler.getName().equals("Sample3")) {
                     ifCont.setCondition("true==false");
                 }
                 assertEquals(order[counter], sampler.getName());
                 counter++;
             }
             assertEquals(counter, 6);
         }
         
         /**
          * test 2 loops with a sub generic controller (sample4 doesn't execute)
-         * @throws Exception
+         * @throws Exception if something fails
          */
         public void testEvaluateAllChildrenWithSubController() throws Exception {
             LoopController controller = new LoopController();
             controller.setLoops(2);
             controller.addTestElement(new TestSampler("Sample1"));
             IfController ifCont = new IfController("true==true");
             ifCont.setEvaluateAll(true);
             controller.addTestElement(ifCont);
             ifCont.addTestElement(new TestSampler("Sample2"));
             
             GenericController genericCont = new GenericController();
             TestSampler sample3 = new TestSampler("Sample3");            
             genericCont.addTestElement(sample3);
             TestSampler sample4 = new TestSampler("Sample4");
             genericCont.addTestElement(sample4);
             ifCont.addTestElement(genericCont);
             
             String[] order = new String[] { "Sample1", "Sample2", "Sample3", 
                     "Sample1", "Sample2", "Sample3" };
             int counter = 0;
             controller.initialize();
             controller.setRunningVersion(true);
             ifCont.setRunningVersion(true);
             genericCont.setRunningVersion(true);
 
             Sampler sampler = null;
             while ((sampler = controller.next()) != null) {
                 sampler.sample(null);
                 if (sampler.getName().equals("Sample3")) {
                     ifCont.setCondition("true==false");
                 }
                 assertEquals(order[counter], sampler.getName());
                 counter++;
             }
             assertEquals(counter, 6); 
         }
 }
diff --git a/test/src/org/apache/jmeter/protocol/http/control/TestCookieManager.java b/test/src/org/apache/jmeter/protocol/http/control/TestCookieManager.java
index 5a9f6cb2e..ae6720a46 100644
--- a/test/src/org/apache/jmeter/protocol/http/control/TestCookieManager.java
+++ b/test/src/org/apache/jmeter/protocol/http/control/TestCookieManager.java
@@ -1,409 +1,423 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.net.URL;
 
 import org.apache.commons.httpclient.cookie.CookiePolicy;
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.protocol.http.sampler.HTTPNullSampler;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 
 public class TestCookieManager extends JMeterTestCase {
         private CookieManager man = null;
 
         public TestCookieManager(String name) {
             super(name);
         }
 
         private JMeterContext jmctx = null;
 
         @Override
         public void setUp() throws Exception {
             super.setUp();
             jmctx = JMeterContextService.getContext();
             man = new CookieManager();
             man.setThreadContext(jmctx);
             man.testStarted();// This is needed in order to set up the cookie policy
         }
 
         public void testRemoveCookie() throws Exception {
             man.setThreadContext(jmctx);
             Cookie c = new Cookie("id", "me", "127.0.0.1", "/", false, 0);
             man.add(c);
             assertEquals(1, man.getCookieCount());
             // This should be ignored, as there is no value
             Cookie d = new Cookie("id", "", "127.0.0.1", "/", false, 0);
             man.add(d);
             assertEquals(0, man.getCookieCount());
             man.add(c);
             man.add(c);
             assertEquals(1, man.getCookieCount());
             Cookie e = new Cookie("id", "me2", "127.0.0.1", "/", false, 0);
             man.add(e);
             assertEquals(1, man.getCookieCount());
         }
 
         public void testSendCookie() throws Exception {
             man.add(new Cookie("id", "value", "jakarta.apache.org", "/", false, 9999999999L));
             HTTPSamplerBase sampler = new HTTPNullSampler();
             sampler.setDomain("jakarta.apache.org");
             sampler.setPath("/index.html");
             sampler.setMethod(HTTPConstants.GET);
             assertNotNull(man.getCookieHeaderForURL(sampler.getUrl()));
         }
 
         public void testSendCookie2() throws Exception {
             man.add(new Cookie("id", "value", ".apache.org", "/", false, 9999999999L));
             HTTPSamplerBase sampler = new HTTPNullSampler();
             sampler.setDomain("jakarta.apache.org");
             sampler.setPath("/index.html");
             sampler.setMethod(HTTPConstants.GET);
             assertNotNull(man.getCookieHeaderForURL(sampler.getUrl()));
         }
 
         /**
          * Test that the cookie domain field is actually handled as browsers do
          * (i.e.: host X matches domain .X):
+         *
+         * @throws Exception if something fails
          */
         public void testDomainHandling() throws Exception {
             URL url = new URL("http://jakarta.apache.org/");
             man.addCookieFromHeader("test=1;domain=.jakarta.apache.org", url);
             assertNotNull(man.getCookieHeaderForURL(url));
         }
 
         public void testCrossDomainHandling() throws Exception {
             URL url = new URL("http://jakarta.apache.org/");
             assertEquals(0,man.getCookieCount()); // starts empty
             man.addCookieFromHeader("test=2;domain=.hc.apache.org", url);
             assertEquals(0,man.getCookieCount()); // should not be stored
             man.addCookieFromHeader("test=1;domain=.jakarta.apache.org", url);
             assertEquals(1,man.getCookieCount()); // OK
         }
 
         /**
          * Test that we won't be tricked by similar host names (this was a past
          * bug, although it never got reported in the bug database):
+         *
+         * @throws Exception if something fails
          */
         public void testSimilarHostNames() throws Exception {
             URL url = new URL("http://ache.org/");
             man.addCookieFromHeader("test=1", url);
             url = new URL("http://jakarta.apache.org/");
             assertNull(man.getCookieHeaderForURL(url));
         }
 
         // Test session cookie is returned
         public void testSessionCookie() throws Exception {
             URL url = new URL("http://a.b.c/");
             man.addCookieFromHeader("test=1", url);
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test=1", s);
         }
 
         // Bug 2063
         public void testCookieWithEquals() throws Exception {
             URL url = new URL("http://a.b.c/");
             man.addCookieFromHeader("NSCP_USER_LOGIN1_NEW=SHA=xxxxx", url);
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("NSCP_USER_LOGIN1_NEW=SHA=xxxxx", s);
             Cookie c=man.get(0);
             assertEquals("NSCP_USER_LOGIN1_NEW",c.getName());
             assertEquals("SHA=xxxxx",c.getValue());
         }
 
         // Test Old cookie is not returned
         public void testOldCookie() throws Exception {
             URL url = new URL("http://a.b.c/");
             man.addCookieFromHeader("test=1; expires=Mon, 01-Jan-1990 00:00:00 GMT", url);
             String s = man.getCookieHeaderForURL(url);
             assertNull(s);
         }
 
         // Test New cookie is returned
         public void testNewCookie() throws Exception {
             URL url = new URL("http://a.b.c/");
             man.addCookieFromHeader("test=1; expires=Mon, 01-Jan-2990 00:00:00 GMT", url);
             assertEquals(1,man.getCookieCount());
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test=1", s);
         }
 
         // Test multi-cookie header handling
         public void testCookies1() throws Exception {
             URL url = new URL("http://a.b.c.d/testCookies1");
             man.addCookieFromHeader("test1=1; comment=\"how,now\", test2=2; version=1", url);
             assertEquals(2,man.getCookieCount());
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test1=1; test2=2", s);
         }
         
         public void testCookies2() throws Exception {
             URL url = new URL("https://a.b.c.d/testCookies2");
             man.addCookieFromHeader("test1=1;secure, test2=2;secure", url);
             assertEquals(2,man.getCookieCount());
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test1=1; test2=2", s);
         }
 
         // Test duplicate cookie handling
         public void testDuplicateCookie() throws Exception {
             URL url = new URL("http://a.b.c/");
             man.addCookieFromHeader("test=1", url);
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test=1", s);
             man.addCookieFromHeader("test=2", url);
             s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test=2", s);
         }
         public void testDuplicateCookie2() throws Exception {
             URL url = new URL("http://a.b.c/");
             man.addCookieFromHeader("test=1", url);
             man.addCookieFromHeader("test2=a", url);
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test=1; test2=a", s); // Assumes some kind of list is used
             man.addCookieFromHeader("test=2", url);
             man.addCookieFromHeader("test3=b", url);
             s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test2=a; test=2; test3=b", s);// Assumes some kind of list is use
             // If not using a list that retains the order, then the asserts would need to change
         }
         
          
         /** Tests missing cookie path for a trivial URL fetch from the domain 
          *  Note that this fails prior to a fix for BUG 38256
+         *
+         * @throws Exception if something fails
          */
         public void testMissingPath0() throws Exception {
             URL url = new URL("http://d.e.f/goo.html");
             man.addCookieFromHeader("test=moo", url);
             String s = man.getCookieHeaderForURL(new URL("http://d.e.f/"));
             assertNotNull(s);
             assertEquals("test=moo", s);
         }
         
         /** Tests missing cookie path for a non-trivial URL fetch from the 
          *  domain.  Note that this fails prior to a fix for BUG 38256
+         *
+         * @throws Exception if something fails
          */
         public void testMissingPath1() throws Exception {
             URL url = new URL("http://d.e.f/moo.html");
             man.addCookieFromHeader("test=moo", url);
             String s = man.getCookieHeaderForURL(new URL("http://d.e.f/goo.html"));
             assertNotNull(s);
             assertEquals("test=moo", s);
         }
         
-        /** Tests explicit root path with a trivial URL fetch from the domain */
+        /** Tests explicit root path with a trivial URL fetch from the domain
+         *
+         * @throws Exception if something fails
+         */
         public void testRootPath0() throws Exception {
             URL url = new URL("http://d.e.f/goo.html");
             man.addCookieFromHeader("test=moo;path=/", url);
             String s = man.getCookieHeaderForURL(new URL("http://d.e.f/"));
             assertNotNull(s);
             assertEquals("test=moo", s);
         }
         
-        /** Tests explicit root path with a non-trivial URL fetch from the domain */
+        /** Tests explicit root path with a non-trivial URL fetch from the domain
+         *
+         * @throws Exception if something fails
+         */
         public void testRootPath1() throws Exception {
             URL url = new URL("http://d.e.f/moo.html");
             man.addCookieFromHeader("test=moo;path=/", url);
             String s = man.getCookieHeaderForURL(new URL("http://d.e.f/goo.html"));
             assertNotNull(s);
             assertEquals("test=moo", s);
         }
         
         // Test cookie matching
         public void testCookieMatching() throws Exception {
             URL url = new URL("http://a.b.c:8080/TopDir/fred.jsp");
             man.addCookieFromHeader("ID=abcd; Path=/TopDir", url);
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("ID=abcd", s);
 
             url = new URL("http://a.b.c:8080/other.jsp");
             s=man.getCookieHeaderForURL(url);
             assertNull(s);
             
             url = new URL("http://a.b.c:8080/TopDir/suub/another.jsp");
             s=man.getCookieHeaderForURL(url);
             assertNotNull(s);
             
             url = new URL("http://a.b.c:8080/TopDir");
             s=man.getCookieHeaderForURL(url);
             assertNotNull(s);
             
             url = new URL("http://a.b.d/");
             s=man.getCookieHeaderForURL(url);
             assertNull(s);
         }
 
         public void testCookieOrdering1() throws Exception {
             URL url = new URL("http://order.now/sub1/moo.html");
             man.addCookieFromHeader("test1=moo1;path=/", url);
             man.addCookieFromHeader("test2=moo2;path=/sub1", url);
             man.addCookieFromHeader("test2=moo3;path=/", url);
             assertEquals(3,man.getCookieCount());
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             assertEquals("test2=moo2; test1=moo1; test2=moo3", s);
         }
 
         public void testCookieOrdering2() throws Exception {
             URL url = new URL("http://order.now/sub1/moo.html");
             man.addCookieFromHeader("test1=moo1;", url);
             man.addCookieFromHeader("test2=moo2;path=/sub1", url);
             man.addCookieFromHeader("test2=moo3;path=/", url);
             assertEquals(3,man.getCookieCount());
             assertEquals("/sub1",man.get(0).getPath()); // Defaults to caller URL
             assertEquals("/sub1",man.get(1).getPath());
             assertEquals("/",man.get(2).getPath());
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             HC3CookieHandler hc3CookieHandler = (HC3CookieHandler) man.getCookieHandler();
             org.apache.commons.httpclient.Cookie[] c = 
                     hc3CookieHandler.getCookiesForUrl(man.getCookies(), url, 
                     CookieManager.ALLOW_VARIABLE_COOKIES);
             assertEquals("/sub1",c[0].getPath());
             assertFalse(c[0].isPathAttributeSpecified());
             assertEquals("/sub1",c[1].getPath());
             assertTrue(c[1].isPathAttributeSpecified());
             assertEquals("/",c[2].getPath());
             assertEquals("test1=moo1; test2=moo2; test2=moo3", s);
         }
         
         public void testCookiePolicy2109() throws Exception {
             man.setCookiePolicy(CookiePolicy.RFC_2109);
             man.testStarted(); // ensure policy is picked up
             URL url = new URL("http://order.now/sub1/moo.html");
             man.addCookieFromHeader("test1=moo1;", url);
             man.addCookieFromHeader("test2=moo2;path=/sub1", url);
             man.addCookieFromHeader("test2=moo3;path=/", url);
             assertEquals(3,man.getCookieCount());
             //assertEquals("/",man.get(0).getPath());
             assertEquals("/sub1",man.get(1).getPath());
             assertEquals("/",man.get(2).getPath());
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             HC3CookieHandler hc3CookieHandler = (HC3CookieHandler) man.getCookieHandler();
             org.apache.commons.httpclient.Cookie[] c = 
                     hc3CookieHandler.getCookiesForUrl(man.getCookies(), url, 
                     CookieManager.ALLOW_VARIABLE_COOKIES);
             assertEquals("/sub1",c[0].getPath());
             assertFalse(c[0].isPathAttributeSpecified());
             assertEquals("/sub1",c[1].getPath());
             assertTrue(c[1].isPathAttributeSpecified());
             assertEquals("/",c[2].getPath());
             assertTrue(c[2].isPathAttributeSpecified());
             assertEquals("$Version=0; test1=moo1; test2=moo2; $Path=/sub1; test2=moo3; $Path=/", s);
         }
 
         public void testCookiePolicyNetscape() throws Exception {
             man.setCookiePolicy(CookiePolicy.NETSCAPE);
             man.testStarted(); // ensure policy is picked up
             URL url = new URL("http://www.order.now/sub1/moo.html");
             man.addCookieFromHeader("test1=moo1;", url);
             man.addCookieFromHeader("test2=moo2;path=/sub1", url);
             man.addCookieFromHeader("test2=moo3;path=/", url);
             assertEquals(3,man.getCookieCount());
             assertEquals("/sub1",man.get(0).getPath());
             assertEquals("/sub1",man.get(1).getPath());
             assertEquals("/",man.get(2).getPath());
             String s = man.getCookieHeaderForURL(url);
             assertNotNull(s);
             HC3CookieHandler hc3CookieHandler = (HC3CookieHandler) man.getCookieHandler();
            
             org.apache.commons.httpclient.Cookie[] c = 
                     hc3CookieHandler.getCookiesForUrl(man.getCookies(), url, 
                     CookieManager.ALLOW_VARIABLE_COOKIES);
             assertEquals("/sub1",c[0].getPath());
             assertFalse(c[0].isPathAttributeSpecified());
             assertEquals("/sub1",c[1].getPath());
             assertTrue(c[1].isPathAttributeSpecified());
             assertEquals("/",c[2].getPath());
             assertTrue(c[2].isPathAttributeSpecified());
             assertEquals("test1=moo1; test2=moo2; test2=moo3", s);
         }
 
         public void testCookiePolicyIgnore() throws Exception {
             man.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
             man.testStarted(); // ensure policy is picked up
             URL url = new URL("http://order.now/sub1/moo.html");
             man.addCookieFromHeader("test1=moo1;", url);
             man.addCookieFromHeader("test2=moo2;path=/sub1", url);
             man.addCookieFromHeader("test2=moo3;path=/", url);
             assertEquals(0,man.getCookieCount());// Cookies are ignored
             Cookie cc;
             cc=new Cookie("test1","moo1",null,"/sub1",false,0,false,false);
             man.add(cc);
             cc=new Cookie("test2","moo2",null,"/sub1",false,0,true,false);
             man.add(cc);
             cc=new Cookie("test3","moo3",null,"/",false,0,false,false);
             man.add(cc);
             assertEquals(3,man.getCookieCount());
             assertEquals("/sub1",man.get(0).getPath());
             assertEquals("/sub1",man.get(1).getPath());
             assertEquals("/",man.get(2).getPath());
             String s = man.getCookieHeaderForURL(url);
             assertNull(s);
             HC3CookieHandler hc3CookieHandler = (HC3CookieHandler) man.getCookieHandler();
             org.apache.commons.httpclient.Cookie[] c = 
                     hc3CookieHandler.getCookiesForUrl(man.getCookies(), url, 
                     CookieManager.ALLOW_VARIABLE_COOKIES);
             assertEquals(0,c.length); // Cookies again ignored
         }
 
         public void testLoad() throws Exception{
             assertEquals(0,man.getCookieCount());
             man.addFile(findTestPath("testfiles/cookies.txt"));
             assertEquals(3,man.getCookieCount());
 
             int num = 0;
             assertEquals("name",man.get(num).getName());
             assertEquals("value",man.get(num).getValue());
             assertEquals("path",man.get(num).getPath());
             assertEquals("domain",man.get(num).getDomain());
             assertTrue(man.get(num).getSecure());
             assertEquals(num,man.get(num).getExpires());
 
             num++;
             assertEquals("name2",man.get(num).getName());
             assertEquals("value2",man.get(num).getValue());
             assertEquals("/",man.get(num).getPath());
             assertEquals("",man.get(num).getDomain());
             assertFalse(man.get(num).getSecure());
             assertEquals(0,man.get(num).getExpires());
 
             num++;
             assertEquals("a",man.get(num).getName());
             assertEquals("b",man.get(num).getValue());
             assertEquals("d",man.get(num).getPath());
             assertEquals("c",man.get(num).getDomain());
             assertTrue(man.get(num).getSecure());
             assertEquals(0,man.get(num).getExpires()); // Show that maxlong now saved as 0
         }
 }
diff --git a/test/src/org/apache/jmeter/protocol/http/control/TestHTTPMirrorThread.java b/test/src/org/apache/jmeter/protocol/http/control/TestHTTPMirrorThread.java
index 45603b339..e17a5cef0 100644
--- a/test/src/org/apache/jmeter/protocol/http/control/TestHTTPMirrorThread.java
+++ b/test/src/org/apache/jmeter/protocol/http/control/TestHTTPMirrorThread.java
@@ -1,472 +1,478 @@
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
 
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.ByteArrayOutputStream;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.HttpURLConnection;
 import java.net.Socket;
 import java.net.URI;
 import java.net.URL;
 
 import org.apache.commons.io.IOUtils;
 
 import junit.framework.Test;
 import junit.framework.TestCase;
 import junit.framework.TestSuite;
 import junit.extensions.TestSetup;
 
 /**
  * Class for testing the HTTPMirrorThread, which is handling the
  * incoming requests for the HTTPMirrorServer
  */
 public class TestHTTPMirrorThread extends TestCase {
     /** The encodings used for http headers and control information */
     private static final String ISO_8859_1 = "ISO-8859-1"; // $NON-NLS-1$
     private static final String UTF_8 = "UTF-8"; // $NON-NLS-1$
 
     private static final byte[] CRLF = { 0x0d, 0x0a };
     private static final int HTTP_SERVER_PORT = 8181;
 
     public TestHTTPMirrorThread(String arg0) {
         super(arg0);
     }
 
     // We need to use a suite in order to preserve the server across test cases
     // With JUnit4 we could use before/after class annotations
     public static Test suite(){
         TestSetup setup = new TestSetup(new TestSuite(TestHTTPMirrorThread.class)){
             private HttpMirrorServer httpServer;
 
             @Override
             protected void setUp() throws Exception {
                 httpServer = startHttpMirror(HTTP_SERVER_PORT);
             }
 
             @Override
             protected void tearDown() throws Exception {
                 // Shutdown the http server
                 httpServer.stopServer();
                 httpServer = null;
             }
         };
         return setup;
     }
 
     /**
-     * Utility method to handle starting the HttpMirrorServer for testing.
-     * Also used by TestHTTPSamplersAgainstHttpMirrorServer
+     * Utility method to handle starting the HttpMirrorServer for testing. Also
+     * used by TestHTTPSamplersAgainstHttpMirrorServer
+     * 
+     * @param port
+     *            port on which the mirror should be started
+     * @return newly created http mirror server
+     * @throws Exception
+     *             if something fails
      */
     public static HttpMirrorServer startHttpMirror(int port) throws Exception {
         HttpMirrorServer server = null;
         server = new HttpMirrorServer(port);
         server.start();
         Exception e = null;
         for (int i=0; i < 10; i++) {// Wait up to 1 second
             try {
                 Thread.sleep(100);
             } catch (InterruptedException ignored) {
             }
             e = server.getException();
             if (e != null) {// Already failed
                 throw new Exception("Could not start mirror server on port: "+port+". "+e);
             }
             if (server.isAlive()) {
                 break; // succeeded
             }
         }
 
         if (!server.isAlive()){
             throw new Exception("Could not start mirror server on port: "+port);
         }
         return server;
     }
 
     public void testGetRequest() throws Exception {
         // Connect to the http server, and do a simple http get
         Socket clientSocket = new Socket("localhost", HTTP_SERVER_PORT);
         OutputStream outputStream = clientSocket.getOutputStream();
         InputStream inputStream = clientSocket.getInputStream();
 
         // Write to the socket
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         // Headers
         bos.write("GET / HTTP 1.1".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write("Host: localhost".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(CRLF);
         bos.close();
         outputStream.write(bos.toByteArray());
 
         // Read the response
         ByteArrayOutputStream response = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int length = 0;
         while(( length = inputStream.read(buffer)) != -1) {
             response.write(buffer, 0, length);
         }
         response.close();
         byte[] mirroredResponse = getMirroredResponse(response.toByteArray());
         // Check that the request and response matches
         checkArraysHaveSameContent(bos.toByteArray(), mirroredResponse);
         // Close the connection
         outputStream.close();
         inputStream.close();
         clientSocket.close();
 
         // Connect to the http server, and do a simple http get, with
         // a pause in the middle of transmitting the header
         clientSocket = new Socket("localhost", HTTP_SERVER_PORT);
         outputStream = clientSocket.getOutputStream();
         inputStream = clientSocket.getInputStream();
 
         // Write to the socket
         bos = new ByteArrayOutputStream();
         // Headers
         bos.write("GET / HTTP 1.1".getBytes(ISO_8859_1));
         bos.write(CRLF);
         // Write the start of the headers, and then sleep, so that the mirror
         // thread will have to block to wait for more data to appear
         bos.close();
         byte[] firstChunk = bos.toByteArray();
         outputStream.write(firstChunk);
         Thread.sleep(300);
         // Write the rest of the headers
         bos = new ByteArrayOutputStream();
         bos.write("Host: localhost".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(CRLF);
         bos.close();
         byte[] secondChunk = bos.toByteArray();
         outputStream.write(secondChunk);
         // Read the response
         response = new ByteArrayOutputStream();
         buffer = new byte[1024];
         length = 0;
         while((length = inputStream.read(buffer)) != -1) {
             response.write(buffer, 0, length);
         }
         response.close();
         mirroredResponse = getMirroredResponse(response.toByteArray());
         // The content sent
         bos = new ByteArrayOutputStream();
         bos.write(firstChunk);
         bos.write(secondChunk);
         bos.close();
         // Check that the request and response matches
         checkArraysHaveSameContent(bos.toByteArray(), mirroredResponse);
         // Close the connection
         outputStream.close();
         inputStream.close();
         clientSocket.close();
     }
 
     public void testPostRequest() throws Exception {
         // Connect to the http server, and do a simple http post
         Socket clientSocket = new Socket("localhost", HTTP_SERVER_PORT);
         OutputStream outputStream = clientSocket.getOutputStream();
         InputStream inputStream = clientSocket.getInputStream();
         // Construct body
         StringBuilder postBodyBuffer = new StringBuilder();
         for(int i = 0; i < 1000; i++) {
             postBodyBuffer.append("abc");
         }
         byte[] postBody = postBodyBuffer.toString().getBytes(ISO_8859_1);
 
         // Write to the socket
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         // Headers
         bos.write("GET / HTTP 1.1".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write("Host: localhost".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(("Content-type: text/plain; charset=" + ISO_8859_1).getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(("Content-length: " + postBody.length).getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(CRLF);
         bos.write(postBody);
         bos.close();
         // Write the headers and body
         outputStream.write(bos.toByteArray());
         // Read the response
         ByteArrayOutputStream response = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int length = 0;
         while((length = inputStream.read(buffer)) != -1) {
             response.write(buffer, 0, length);
         }
         response.close();
         byte[] mirroredResponse = getMirroredResponse(response.toByteArray());
         // Check that the request and response matches
         checkArraysHaveSameContent(bos.toByteArray(), mirroredResponse);
         // Close the connection
         outputStream.close();
         inputStream.close();
         clientSocket.close();
 
         // Connect to the http server, and do a simple http post, with
         // a pause after transmitting the headers
         clientSocket = new Socket("localhost", HTTP_SERVER_PORT);
         outputStream = clientSocket.getOutputStream();
         inputStream = clientSocket.getInputStream();
 
         // Write to the socket
         bos = new ByteArrayOutputStream();
         // Headers
         bos.write("GET / HTTP 1.1".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write("Host: localhost".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(("Content-type: text/plain; charset=" + ISO_8859_1).getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(("Content-length: " + postBody.length).getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(CRLF);
         bos.close();
         // Write the headers, and then sleep
         bos.close();
         byte[] firstChunk = bos.toByteArray();
         outputStream.write(firstChunk);
         Thread.sleep(300);
 
         // Write the body
         byte[] secondChunk = postBody;
         outputStream.write(secondChunk);
         // Read the response
         response = new ByteArrayOutputStream();
         buffer = new byte[1024];
         length = 0;
         while((length = inputStream.read(buffer)) != -1) {
             response.write(buffer, 0, length);
         }
         response.close();
         mirroredResponse = getMirroredResponse(response.toByteArray());
         // The content sent
         bos = new ByteArrayOutputStream();
         bos.write(firstChunk);
         bos.write(secondChunk);
         bos.close();
         // Check that the request and response matches
         checkArraysHaveSameContent(bos.toByteArray(), mirroredResponse);
         // Close the connection
         outputStream.close();
         inputStream.close();
         clientSocket.close();
 
         // Connect to the http server, and do a simple http post with utf-8
         // encoding of the body, which caused problems when reader/writer
         // classes were used in the HttpMirrorThread
         clientSocket = new Socket("localhost", HTTP_SERVER_PORT);
         outputStream = clientSocket.getOutputStream();
         inputStream = clientSocket.getInputStream();
         // Construct body
         postBodyBuffer = new StringBuilder();
         for(int i = 0; i < 1000; i++) {
             postBodyBuffer.append("\u0364\u00c5\u2052");
         }
         postBody = postBodyBuffer.toString().getBytes(UTF_8);
 
         // Write to the socket
         bos = new ByteArrayOutputStream();
         // Headers
         bos.write("GET / HTTP 1.1".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write("Host: localhost".getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(("Content-type: text/plain; charset=" + UTF_8).getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(("Content-length: " + postBody.length).getBytes(ISO_8859_1));
         bos.write(CRLF);
         bos.write(CRLF);
         bos.close();
         // Write the headers, and then sleep
         bos.close();
         firstChunk = bos.toByteArray();
         outputStream.write(firstChunk);
         Thread.sleep(300);
 
         // Write the body
         secondChunk = postBody;
         outputStream.write(secondChunk);
         // Read the response
         response = new ByteArrayOutputStream();
         buffer = new byte[1024];
         length = 0;
         while((length = inputStream.read(buffer)) != -1) {
             response.write(buffer, 0, length);
         }
         response.close();
         mirroredResponse = getMirroredResponse(response.toByteArray());
         // The content sent
         bos = new ByteArrayOutputStream();
         bos.write(firstChunk);
         bos.write(secondChunk);
         bos.close();
         // Check that the request and response matches
         checkArraysHaveSameContent(bos.toByteArray(), mirroredResponse);
         // Close the connection
         outputStream.close();
         inputStream.close();
         clientSocket.close();
     }
 
 /*
     public void testPostRequestChunked() throws Exception {
         // TODO - implement testing of chunked post request
     }
 */
 
     public void testStatus() throws Exception {
         URL url = new URL("http", "localhost", HTTP_SERVER_PORT, "/");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.addRequestProperty("X-ResponseStatus", "302 Temporary Redirect");
         conn.connect();
         assertEquals(302, conn.getResponseCode());
         assertEquals("Temporary Redirect", conn.getResponseMessage());
     }
 
     public void testQueryStatus() throws Exception {
         URL url = new URI("http",null,"localhost",HTTP_SERVER_PORT,"/path","status=303 See Other",null).toURL();
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.connect();
         assertEquals(303, conn.getResponseCode());
         assertEquals("See Other", conn.getResponseMessage());
     }
 
     public void testQueryRedirect() throws Exception {
         URL url = new URI("http",null,"localhost",HTTP_SERVER_PORT,"/path","redirect=/a/b/c/d?q",null).toURL();
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setInstanceFollowRedirects(false);
         conn.connect();
         assertEquals(302, conn.getResponseCode());
         assertEquals("Temporary Redirect", conn.getResponseMessage());
         assertEquals("/a/b/c/d?q",conn.getHeaderField("Location"));
     }
 
     public void testHeaders() throws Exception {
         URL url = new URL("http", "localhost", HTTP_SERVER_PORT, "/");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.addRequestProperty("X-SetHeaders", "Location: /abcd|X-Dummy: none");
         conn.connect();
         assertEquals(200, conn.getResponseCode());
         assertEquals("OK", conn.getResponseMessage());
         assertEquals("/abcd",conn.getHeaderField("Location"));
         assertEquals("none",conn.getHeaderField("X-Dummy"));
     }
 
     public void testResponseLength() throws Exception {
         URL url = new URL("http", "localhost", HTTP_SERVER_PORT, "/");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.addRequestProperty("X-ResponseLength", "10");
         conn.connect();
         final InputStream inputStream = conn.getInputStream();
         assertEquals(10, IOUtils.toByteArray(inputStream).length);
         inputStream.close();
     }
 
     public void testCookie() throws Exception {
         URL url = new URL("http", "localhost", HTTP_SERVER_PORT, "/");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.addRequestProperty("X-SetCookie", "four=2*2");
         conn.connect();
         assertEquals("four=2*2",conn.getHeaderField("Set-Cookie"));
     }
 
     public void testSleep() throws Exception {
         URL url = new URL("http", "localhost", HTTP_SERVER_PORT, "/");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.addRequestProperty("X-Sleep", "1000");
         conn.connect();
         // use nanoTime to do timing measurement or calculation
         // See https://blogs.oracle.com/dholmes/entry/inside_the_hotspot_vm_clocks
         long now = System.nanoTime();
         final InputStream inputStream = conn.getInputStream();
         while(inputStream.read() != -1) {}
         inputStream.close();
         final long elapsed = (System.nanoTime() - now)/1000000L;
         assertTrue("Expected > 1000 " + elapsed, elapsed >= 1000);
     }
 
     /**
      * Check that the the two byte arrays have identical content
      *
      * @param expected
      * @param actual
      * @throws UnsupportedEncodingException
      */
     private void checkArraysHaveSameContent(byte[] expected, byte[] actual) throws UnsupportedEncodingException {
         if(expected != null && actual != null) {
             if(expected.length != actual.length) {
                 System.out.println(">>>>>>>>>>>>>>>>>>>> (expected) : length " + expected.length);
                 System.out.println(new String(expected,"UTF-8"));
                 System.out.println("==================== (actual) : length " + actual.length);
                 System.out.println(new String(actual,"UTF-8"));
                 System.out.println("<<<<<<<<<<<<<<<<<<<<");
                 fail("arrays have different length, expected is " + expected.length + ", actual is " + actual.length);
             }
             else {
                 for(int i = 0; i < expected.length; i++) {
                     if(expected[i] != actual[i]) {
                         System.out.println(">>>>>>>>>>>>>>>>>>>> (expected) : length " + expected.length);
                         System.out.println(new String(expected,0,i+1, ISO_8859_1));
                         System.out.println("==================== (actual) : length " + actual.length);
                         System.out.println(new String(actual,0,i+1, ISO_8859_1));
                         System.out.println("<<<<<<<<<<<<<<<<<<<<");
 /*
                         // Useful to when debugging
                         for(int j = 0; j  < expected.length; j++) {
                             System.out.print(expected[j] + " ");
                         }
                         System.out.println();
                         for(int j = 0; j  < actual.length; j++) {
                             System.out.print(actual[j] + " ");
                         }
                         System.out.println();
 */
                         fail("byte at position " + i + " is different, expected is " + expected[i] + ", actual is " + actual[i]);
                     }
                 }
             }
         }
         else {
             fail("expected or actual byte arrays were null");
         }
     }
 
     private byte[] getMirroredResponse(byte[] allResponse) {
         // The response includes the headers from the mirror server,
         // we want to skip those, to only keep the content mirrored.
         // Look for the first CRLFCRLF section
         int startOfMirrorResponse = 0;
         for(int i = 0; i < allResponse.length; i++) {
             // TODO : This is a bit fragile
             if(allResponse[i] == 0x0d && allResponse[i+1] == 0x0a && allResponse[i+2] == 0x0d && allResponse[i+3] == 0x0a) {
                 startOfMirrorResponse = i + 4;
                 break;
             }
         }
         byte[] mirrorResponse = new byte[allResponse.length - startOfMirrorResponse];
         System.arraycopy(allResponse, startOfMirrorResponse, mirrorResponse, 0, mirrorResponse.length);
         return mirrorResponse;
     }
 }
diff --git a/test/src/org/apache/jmeter/resources/PackageTest.java b/test/src/org/apache/jmeter/resources/PackageTest.java
index ba6020fa8..285808f37 100644
--- a/test/src/org/apache/jmeter/resources/PackageTest.java
+++ b/test/src/org/apache/jmeter/resources/PackageTest.java
@@ -1,461 +1,467 @@
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
 
 package org.apache.jmeter.resources;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FilenameFilter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetEncoder;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.MissingResourceException;
 import java.util.Properties;
 import java.util.PropertyResourceBundle;
 import java.util.Set;
 import java.util.TreeMap;
 import java.util.TreeSet;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import junit.framework.Test;
 import junit.framework.TestCase;
 import junit.framework.TestSuite;
 
 import org.apache.jmeter.gui.util.JMeterMenuBar;
 import org.apache.jorphan.util.JOrphanUtils;
 
 /*
  * Created on Nov 29, 2003
  * 
  * Test the composition of the messages*.properties files
  * - properties files exist
  * - properties files don't have duplicate keys
  * - non-default properties files don't have any extra keys.
  * 
  * N.B. If there is a default resource, ResourceBundle does not detect missing
  * resources, i.e. the presence of messages.properties means that the
  * ResourceBundle for Locale "XYZ" would still be found, and have the same keys
  * as the default. This makes it not very useful for checking properties files.
  * 
  * This is why the tests use Class.getResourceAsStream() etc
  * 
  * The tests don't quite follow the normal JUnit test strategy of one test per
  * possible failure. This was done in order to make it easier to report exactly
  * why the tests failed.
  */
 
 public class PackageTest extends TestCase {
     private static final String basedir = new File(System.getProperty("user.dir")).getParent(); // assumes the test starts in the bin directory
 
     private static final File srcFiledir = new File(basedir,"src");
 
     private static final String MESSAGES = "messages";
 
     private static PropertyResourceBundle defaultPRB; // current default language properties file
 
     private static PropertyResourceBundle messagePRB; // messages.properties
 
     private static final CharsetEncoder ASCII_ENCODER = 
         Charset.forName("US-ASCII").newEncoder(); // Ensure properties files don't use special characters
     
     private static boolean isPureAscii(String v) {
       return ASCII_ENCODER.canEncode(v);
     }
 
     // Read resource into ResourceBundle and store in List
     private PropertyResourceBundle getRAS(String res) throws Exception {
         InputStream ras = this.getClass().getResourceAsStream(res);
         if (ras == null){
             return null;
         }
         return new PropertyResourceBundle(ras);
     }
 
     private static final Object[] DUMMY_PARAMS = new Object[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
 
     // Read resource file saving the keys
     private int readRF(String res, List<String> l) throws Exception {
         int fails = 0;
         InputStream ras = this.getClass().getResourceAsStream(res);
         if (ras==null){
             if (MESSAGES.equals(resourcePrefix)|| lang.length() == 0 ){
                 throw new IOException("Cannot open resource file "+res);
             } else {
                 return 0;
             }
         }
         BufferedReader fileReader = null;
         try {
             fileReader = new BufferedReader(new InputStreamReader(ras));
             String s;
             while ((s = fileReader.readLine()) != null) {
                 if (s.length() > 0 && !s.startsWith("#") && !s.startsWith("!")) {
                     int equ = s.indexOf('=');
                     String key = s.substring(0, equ);
                     if (resourcePrefix.equals(MESSAGES)){// Only relevant for messages
                         /*
                          * JMeterUtils.getResString() converts space to _ and lowercases
                          * the key, so make sure all keys pass the test
                          */
                         if ((key.indexOf(' ') >= 0) || !key.toLowerCase(java.util.Locale.ENGLISH).equals(key)) {
                             System.out.println("Invalid key for JMeterUtils " + key);
                             fails++;
                         }
                     }
                     String val = s.substring(equ + 1);
                     l.add(key); // Store the key
                     /*
                      * Now check for invalid message format: if string contains {0}
                      * and ' there may be a problem, so do a format with dummy
                      * parameters and check if there is a { in the output. A bit
                      * crude, but should be enough for now.
                      */
                     if (val.indexOf("{0}") > 0 && val.indexOf('\'') > 0) {
                         String m = java.text.MessageFormat.format(val, DUMMY_PARAMS);
                         if (m.indexOf('{') > 0) {
                             fails++;
                             System.out.println("Incorrect message format ? (input/output) for: "+key);
                             System.out.println(val);
                             System.out.println(m);
                         }
                     }
 
                     if (!isPureAscii(val)) {
                         fails++;
                         System.out.println("Incorrect char value in: "+s);                    
                     }
                 }
             }
             return fails;
         }
         finally {
             JOrphanUtils.closeQuietly(fileReader);
         }
     }
 
     // Helper method to construct resource name
     private String getResName(String lang) {
         if (lang.length() == 0) {
             return resourcePrefix+".properties";
         } else {
             return resourcePrefix+"_" + lang + ".properties";
         }
     }
 
     private void check(String resname) throws Exception {
         check(resname, true);// check that there aren't any extra entries
     }
 
     /*
      * perform the checks on the resources
      * 
      */
     private void check(String resname, boolean checkUnexpected) throws Exception {
         ArrayList<String> alf = new ArrayList<String>(500);// holds keys from file
         String res = getResName(resname);
         subTestFailures += readRF(res, alf);
         Collections.sort(alf);
 
         // Look for duplicate keys in the file
         String last = "";
         for (int i = 0; i < alf.size(); i++) {
             String curr = alf.get(i);
             if (curr.equals(last)) {
                 subTestFailures++;
                 System.out.println("\nDuplicate key =" + curr + " in " + res);
             }
             last = curr;
         }
 
         if (resname.length() == 0) // Must be the default resource file
         {
             defaultPRB = getRAS(res);
             if (defaultPRB == null){
                 throw new IOException("Could not find required file: "+res);
             }
             if (resourcePrefix.endsWith(MESSAGES)) {
                 messagePRB = defaultPRB;
             }
         } else if (checkUnexpected) {
             // Check all the keys are in the default props file
             PropertyResourceBundle prb = getRAS(res); 
             if (prb == null){
                 return;
             }
             final ArrayList<String> list = Collections.list(prb.getKeys());
             Collections.sort(list);
             final boolean mainResourceFile = resname.startsWith("messages");
             for (String key : list) {
                 try {
                     String val = defaultPRB.getString(key); // Also Check key is in default
                     if (mainResourceFile && val.equals(prb.getString(key))){
                         System.out.println("Duplicate value? "+key+"="+val+" in "+res);
                         subTestFailures++;
                     }
                 } catch (MissingResourceException e) {
                     subTestFailures++;
                     System.out.println(resourcePrefix + "_" + resname + " has unexpected key: " + key);
                 }
             }
         }
 
         if (subTestFailures > 0) {
             fail("One or more subtests failed");
         }
     }
 
     private static final String[] prefixList = getResources(srcFiledir);
 
     /**
      * Find I18N resources in classpath
-     * @param srcFiledir
+     * @param srcFiledir directory in which the files reside
      * @return list of properties files subject to I18N
      */
     public static final String[] getResources(File srcFiledir) {
         Set<String> set = new TreeSet<String>();
         findFile(srcFiledir, set, new FilenameFilter() {
             @Override
             public boolean accept(File dir, String name) {
                 return new File(dir, name).isDirectory() 
                         || (
                                 name.equals("messages.properties") ||
                                 (name.endsWith("Resources.properties")
                                 && !name.matches("Example\\d+Resources\\.properties")));
             }
         });
         return set.toArray(new String[set.size()]);
     }
     
     /**
-     * Find resources matching filenamefiler and adds them to set removing everything before "/org"
+     * Find resources matching filenamefiler and adds them to set removing
+     * everything before "/org"
+     * 
      * @param file
+     *            directory in which the files reside
      * @param set
+     *            container into which the names of the files should be added
      * @param filenameFilter
+     *            filter that the files must satisfy to be included into
+     *            <code>set</code>
      */
     private static void findFile(File file, Set<String> set,
             FilenameFilter filenameFilter) {
         File[] foundFiles = file.listFiles(filenameFilter);
         assertNotNull("Not a directory: "+file, foundFiles);
         for (File file2 : foundFiles) {
             if(file2.isDirectory()) {
                 findFile(file2, set, filenameFilter);
             } else {
                 String absPath2 = file2.getAbsolutePath().replace('\\', '/'); // Fix up Windows paths
                 int indexOfOrg = absPath2.indexOf("/org");
                 int lastIndex = absPath2.lastIndexOf('.');
                 set.add(absPath2.substring(indexOfOrg, lastIndex));
             }
         }
     }
     
     /*
      * Use a suite to ensure that the default is done first
     */
     public static Test suite() {
         TestSuite ts = new TestSuite("Resources PackageTest");
         String languages[] = JMeterMenuBar.getLanguages();
         for(String prefix : prefixList){
             TestSuite pfx = new TestSuite(prefix) ;
             pfx.addTest(new PackageTest("testLang","", prefix)); // load the default resource
             for(String language : languages){
                 if (!"en".equals(language)){ // Don't try to check the default language
                     pfx.addTest(new PackageTest("testLang", language, prefix));
                 }
             }
             ts.addTest(pfx);
         }
         ts.addTest(new PackageTest("checkI18n", "fr"));
         // TODO Add these some day
 //        ts.addTest(new PackageTest("checkI18n", "es"));
 //        ts.addTest(new PackageTest("checkI18n", "pl"));
 //        ts.addTest(new PackageTest("checkI18n", "pt_BR"));
 //        ts.addTest(new PackageTest("checkI18n", "tr"));
 //        ts.addTest(new PackageTest("checkI18n", Locale.JAPANESE.toString()));
 //        ts.addTest(new PackageTest("checkI18n", Locale.SIMPLIFIED_CHINESE.toString()));
 //        ts.addTest(new PackageTest("checkI18n", Locale.TRADITIONAL_CHINESE.toString()));
         ts.addTest(new PackageTest("checkResourceReferences", ""));
         return ts;
     }
    
     
     private int subTestFailures;
 
     private final String lang;
     
     private final String resourcePrefix; // e.g. "/org/apache/jmeter/resources/messages"
 
     public PackageTest(String testName, String _lang) {
         this(testName, _lang, MESSAGES);
     }
 
     public PackageTest(String testName, String _lang, String propName) {
         super(testName);
         lang=_lang;
         subTestFailures = 0;
         resourcePrefix = propName;
     }
 
     public void testLang() throws Exception{
         check(lang);
     }
 
     /**
      * Check all messages are available in one language
-     * @throws Exception
+     * @throws Exception if something fails
      */
     public void checkI18n() throws Exception {
         Map<String, Map<String,String>> missingLabelsPerBundle = new HashMap<String, Map<String,String>>();
         for (String prefix : prefixList) {
             Properties messages = new Properties();
             messages.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(prefix.substring(1)+".properties"));
             checkMessagesForLanguage( missingLabelsPerBundle , missingLabelsPerBundle, messages,prefix.substring(1), lang);
         }
         
         assertEquals(missingLabelsPerBundle.size()+" missing labels, labels missing:"+printLabels(missingLabelsPerBundle), 0, missingLabelsPerBundle.size());
     }
 
     /**
      * Check messages are available in language
      * @param missingLabelsPerBundle2 
      * @param missingLabelsPerBundle 
      * @param messages Properties messages in english
      * @param language Language 
      * @throws IOException
      */
     private void checkMessagesForLanguage(Map<String, Map<String, String>> missingLabelsPerBundle, Map<String, Map<String, String>> missingLabelsPerBundle2, Properties messages, String bundlePath,String language)
             throws IOException {
         Properties messagesFr = new Properties();
         String languageBundle = bundlePath+"_"+language+ ".properties";
         InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(languageBundle);
         if(inputStream == null) {
             Map<String, String> messagesAsProperties = new HashMap<String, String>();
             for (Iterator<Map.Entry<Object, Object>> iterator = messages.entrySet().iterator(); iterator.hasNext();) {
                 Map.Entry<Object, Object> entry = iterator.next();
                 messagesAsProperties.put((String) entry.getKey(), (String) entry.getValue()); 
             }
             missingLabelsPerBundle.put(languageBundle, messagesAsProperties);
             return;
         }
         messagesFr.load(inputStream);
     
         Map<String, String> missingLabels = new TreeMap<String,String>();
         for (Iterator<Map.Entry<Object,Object>> iterator =  messages.entrySet().iterator(); iterator.hasNext();) {
             Map.Entry<Object,Object> entry = iterator.next();
             String key = (String)entry.getKey();
             final String I18NString = "[\\d% ]+";// numeric, space and % don't need translation
             if(!messagesFr.containsKey(key)) {
                 String value = (String) entry.getValue();
                 // TODO improve check of values that don't need translation
                 if (value.matches(I18NString)) {
                     // System.out.println("Ignoring missing "+key+"="+value+" in "+languageBundle); // TODO convert to list and display at end
                 } else {
                     missingLabels.put(key,(String) entry.getValue());
                 }
             } else {
                 String value = (String) entry.getValue();
                 if (value.matches(I18NString)) {
                     System.out.println("Unnecessary entry "+key+"="+value+" in "+languageBundle);
                 }
             }
         }
         if(!missingLabels.isEmpty()) {
             missingLabelsPerBundle.put(languageBundle, missingLabels);
         }
     }
 
     /**
      * Build message with misssing labels per bundle
      * @param missingLabelsPerBundle
      * @return String
      */
     private String printLabels(Map<String, Map<String, String>> missingLabelsPerBundle) {
         StringBuilder builder = new StringBuilder();
         for (Iterator<Map.Entry<String,Map<String, String>>> iterator =  missingLabelsPerBundle.entrySet().iterator(); iterator.hasNext();) {
             Map.Entry<String,Map<String, String>> entry = iterator.next();
             builder.append("Missing labels in bundle:"+entry.getKey()+"\r\n");
             for (Iterator<Map.Entry<String,String>> it2 =  entry.getValue().entrySet().iterator(); it2.hasNext();) {
                 Map.Entry<String,String> entry2 = it2.next();
                 builder.append(entry2.getKey()+"="+entry2.getValue()+"\r\n");
             }
             builder.append("======================================================\r\n");
         }
         return builder.toString();
     }
 
     // Check that calls to getResString use a valid property key name
     public void checkResourceReferences() {
         final AtomicInteger errors = new AtomicInteger(0);
         findFile(srcFiledir, null, new FilenameFilter() {
             @Override
             public boolean accept(File dir, String name) {
                 final File file = new File(dir, name);
                 // Look for calls to JMeterUtils.getResString()
                 final Pattern pat = Pattern.compile(".*getResString\\(\"([^\"]+)\"\\).*");
                 if (name.endsWith(".java")) {
                   BufferedReader fileReader = null;
                   try {
                     fileReader = new BufferedReader(new FileReader(file));
                     String s;
                     while ((s = fileReader.readLine()) != null) {
                         if (s.matches("\\s*//.*")) { // leading comment
                             continue;
                         }
                         Matcher m = pat.matcher(s);
                         if (m.matches()) {
                             final String key = m.group(1);
                             // Resource keys cannot contain spaces, and are forced to lower case
                             String resKey = key.replace(' ', '_'); // $NON-NLS-1$ // $NON-NLS-2$
                             resKey = resKey.toLowerCase(java.util.Locale.ENGLISH);
                             if (!key.equals(resKey)) {
                                 System.out.println(file+": non-standard message key: '"+key+"'");
                             }
                             try {
                                 messagePRB.getString(resKey);
                             } catch (MissingResourceException e) {
                                 System.out.println(file+": missing message key: '"+key+"'");
                                 errors.incrementAndGet();
                             }
                         }
                     }
                 } catch (IOException e) {
                     e.printStackTrace();
                 } finally {
                     JOrphanUtils.closeQuietly(fileReader);
                 }
                  
                 }
                 return file.isDirectory();
             }
         });
         int errs = errors.get();
         if (errs > 0) {
             fail("Detected "+errs+" missing message property keys");
         }
     }
 }
diff --git a/test/src/org/apache/jmeter/testelement/BarChartTest.java b/test/src/org/apache/jmeter/testelement/BarChartTest.java
index 0103dd30b..355729db2 100644
--- a/test/src/org/apache/jmeter/testelement/BarChartTest.java
+++ b/test/src/org/apache/jmeter/testelement/BarChartTest.java
@@ -1,88 +1,88 @@
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
 package org.apache.jmeter.testelement;
 
 import java.io.File;
 import java.io.IOException;
 
 import javax.swing.JComponent;
 
 import org.apache.jmeter.report.DataSet;
 import org.apache.jmeter.save.SaveGraphicsService;
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class BarChartTest extends JMeterTestCase {
     
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
-     * @param arg0
+     * @param arg0 name of the test case
      */
     public BarChartTest(String arg0) {
         super(arg0);
     }
 
     public void testGenerateBarChart() throws IOException {
         log.info("jtl version=" + JMeterUtils.getProperty("file_format.testlog"));
         // String sampleLog = "C:/eclipse3/workspace/jmeter-21/bin/testfiles/sample_log1.jtl";
         String sampleLog = findTestPath("testfiles/sample_log1.jtl");
         String sampleLog2 = findTestPath("testfiles/sample_log1b.jtl");
         String sampleLog3 = findTestPath("testfiles/sample_log1c.jtl");
         JTLData input = new JTLData();
         JTLData input2 = new JTLData();
         JTLData input3 = new JTLData();
         input.setDataSource(sampleLog);
         input.loadData();
         input2.setDataSource(sampleLog2);
         input2.loadData();
         input3.setDataSource(sampleLog3);
         input3.loadData();
 
         assertTrue((input.getStartTimestamp() > 0));
         assertTrue((input.getEndTimestamp() > input.getStartTimestamp()));
         assertTrue((input.getURLs().size() > 0));
         log.info("URL count=" + input.getURLs().size());
         java.util.ArrayList<DataSet> list = new java.util.ArrayList<DataSet>();
         list.add(input);
         list.add(input2);
         list.add(input3);
 
         BarChart bchart = new BarChart();
         bchart.setTitle("Sample Chart");
         bchart.setCaption("Sample");
         bchart.setName("Sample");
         bchart.setYAxis("milliseconds");
         bchart.setYLabel("Test Runs");
         bchart.setXAxis(AbstractTable.REPORT_TABLE_90_PERCENT);
         bchart.setXLabel(AbstractChart.X_DATA_DATE_LABEL);
         bchart.setURL("jakarta_home");
         JComponent gr = bchart.renderChart(list);
         assertNotNull(gr);
         SaveGraphicsService serv = new SaveGraphicsService();
         String filename = bchart.getTitle();
         filename = filename.replace(' ','_');
         if (!"true".equalsIgnoreCase(System.getProperty("java.awt.headless"))){
             String outName = File.createTempFile(filename, null).getAbsolutePath(); // tweak.
             serv.saveJComponent(outName,SaveGraphicsService.PNG,gr);
             assertTrue("Should have created the file",new File(outName+".png").exists());
         }
     }
 }
diff --git a/test/src/org/apache/jmeter/testelement/LineGraphTest.java b/test/src/org/apache/jmeter/testelement/LineGraphTest.java
index 98f8574b9..14b331400 100644
--- a/test/src/org/apache/jmeter/testelement/LineGraphTest.java
+++ b/test/src/org/apache/jmeter/testelement/LineGraphTest.java
@@ -1,88 +1,88 @@
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
 package org.apache.jmeter.testelement;
 
 import java.io.File;
 import java.io.IOException;
 
 import javax.swing.JComponent;
 
 import org.apache.jmeter.report.DataSet;
 import org.apache.jmeter.save.SaveGraphicsService;
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class LineGraphTest extends JMeterTestCase {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
-     * @param arg0
+     * @param arg0 name of the test case
      */
     public LineGraphTest(String arg0) {
         super(arg0);
     }
 
     public void testGenerateLineChart() throws IOException {
         log.info("jtl version=" + JMeterUtils.getProperty("file_format.testlog"));
         // String sampleLog = "C:/eclipse3/workspace/jmeter-21/bin/testfiles/sample_log1.jtl";
         String sampleLog = findTestPath("testfiles/sample_log1.jtl");
         String sampleLog2 = findTestPath("testfiles/sample_log1b.jtl");
         String sampleLog3 = findTestPath("testfiles/sample_log1c.jtl");
         JTLData input = new JTLData();
         JTLData input2 = new JTLData();
         JTLData input3 = new JTLData();
         input.setDataSource(sampleLog);
         input.loadData();
         input2.setDataSource(sampleLog2);
         input2.loadData();
         input3.setDataSource(sampleLog3);
         input3.loadData();
 
         assertTrue((input.getStartTimestamp() > 0));
         assertTrue((input.getEndTimestamp() > input.getStartTimestamp()));
         assertTrue((input.getURLs().size() > 0));
         log.info("URL count=" + input.getURLs().size());
         java.util.ArrayList<DataSet> list = new java.util.ArrayList<DataSet>();
         list.add(input);
         list.add(input2);
         list.add(input3);
 
         LineChart lgraph = new LineChart();
         lgraph.setTitle("Sample Line Graph");
         lgraph.setCaption("Sample");
         lgraph.setName("Sample");
         lgraph.setYAxis("milliseconds");
         lgraph.setYLabel("Test Runs");
         lgraph.setXAxis(AbstractTable.REPORT_TABLE_MAX);
         lgraph.setXLabel(AbstractChart.X_DATA_FILENAME_LABEL);
         lgraph.setURLs("jakarta_home,jmeter_home");
         JComponent gr = lgraph.renderChart(list);
         assertNotNull(gr);
         SaveGraphicsService serv = new SaveGraphicsService();
         String filename = lgraph.getTitle();
         filename = filename.replace(' ','_');
         if (!"true".equalsIgnoreCase(System.getProperty("java.awt.headless"))){
             String outPfx = File.createTempFile(filename, null).getAbsolutePath(); // tweak.
             serv.saveJComponent(outPfx,SaveGraphicsService.PNG,gr);
             assertTrue("Should have created file",new File(outPfx+".png").exists());
         }
     }
 }
