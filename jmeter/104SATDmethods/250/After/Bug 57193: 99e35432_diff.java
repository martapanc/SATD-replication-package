diff --git a/src/jorphan/org/apache/jorphan/collections/Data.java b/src/jorphan/org/apache/jorphan/collections/Data.java
index 3b7cc99f1..f978ab8e0 100644
--- a/src/jorphan/org/apache/jorphan/collections/Data.java
+++ b/src/jorphan/org/apache/jorphan/collections/Data.java
@@ -1,693 +1,698 @@
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
 
 package org.apache.jorphan.collections;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Use this class to store database-like data. This class uses rows and columns
  * to organize its data. It has some convenience methods that allow fast loading
  * and retrieval of the data into and out of string arrays. It is also handy for
  * reading CSV files.
  *
  * WARNING: the class assumes that column names are unique, but does not enforce this.
  * 
  */
 public class Data implements Serializable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private final Map<String, List<Object>> data;
 
     private List<String> header;
 
     // saves current position in data List
     private int currentPos, size;
 
     /**
      * Constructor - takes no arguments.
      */
     public Data() {
         header = new ArrayList<String>();
         data = new HashMap<String, List<Object>>();
         currentPos = -1;
         size = currentPos + 1;
     }
 
     /**
      * Replaces the given header name with a new header name.
      *
      * @param oldHeader
      *            Old header name.
      * @param newHeader
      *            New header name.
      */
     public void replaceHeader(String oldHeader, String newHeader) {
         List<Object> tempList;
         int index = header.indexOf(oldHeader);
         header.set(index, newHeader);
         tempList = data.remove(oldHeader);
         data.put(newHeader, tempList);
     }
 
     /**
      * Adds the rows of the given Data object to this Data object.
      *
      * @param d
      *            data object to be appended to this one
      */
     public void append(Data d) {
         boolean valid = true;
         String[] headers = getHeaders();
         String[] dHeaders = d.getHeaders();
         if (headers.length != dHeaders.length) {
             valid = false;
         } else {
             for (int count = 0; count < dHeaders.length; count++) {
                 if (!header.contains(dHeaders[count])) {
                     valid = false;
                     break;
                 }
             }
         }
 
         if (valid) {
             currentPos = size;
             d.reset();
             while (d.next()) {
                 for (int count = 0; count < headers.length; count++) {
                     addColumnValue(headers[count], d.getColumnValue(headers[count]));
                 }
             }
         }
     }
 
     /**
      * Get the number of the current row.
      *
      * @return integer representing the current row
      */
     public int getCurrentPos() {
         return currentPos;
     }
 
     /**
      * Removes the current row.
      */
     public void removeRow() {
         List<Object> tempList;
         Iterator<String> it = data.keySet().iterator();
         log.debug("removing row, size = " + size);
         if (currentPos > -1 && currentPos < size) {
             log.debug("got to here");
             while (it.hasNext()) {
                 tempList = data.get(it.next());
                 tempList.remove(currentPos);
             }
             if (currentPos > 0) {
                 currentPos--;
             }
             size--;
         }
     }
 
     public void removeRow(int index) {
         log.debug("Removing row: " + index);
         if (index < size) {
             setCurrentPos(index);
             log.debug("Setting currentpos to " + index);
             removeRow();
         }
     }
 
     public void addRow() {
         String[] headers = getHeaders();
         List<Object> tempList = new ArrayList<Object>();
         for (int i = 0; i < headers.length; i++) {
             if ((tempList = data.get(header.get(i))) == null) {
                 tempList = new ArrayList<Object>();
                 data.put(headers[i], tempList);
             }
             tempList.add("");
         }
         size = tempList.size();
         setCurrentPos(size - 1);
     }
 
     /**
      * Sets the current pos. If value sent to method is not a valid number, the
      * current position is set to one higher than the maximum.
      *
      * @param r
      *            position to set to.
      */
     public void setCurrentPos(int r) {
         currentPos = r;
     }
 
     /**
      * Sorts the data using a given row as the sorting criteria. A boolean value
      * indicates whether to sort ascending or descending.
      *
      * @param column
      *            name of column to use as sorting criteria.
      * @param asc
      *            boolean value indicating whether to sort ascending or
      *            descending. True for asc, false for desc. Currently this
      *            feature is not enabled and all sorts are asc.
      */
     public void sort(String column, boolean asc) {
         sortData(column, 0, size);
     }
 
     private void swapRows(int row1, int row2) {
         List<Object> temp;
         Object o;
         Iterator<String> it = data.keySet().iterator();
         while (it.hasNext()) {
             temp = data.get(it.next());
             o = temp.get(row1);
             temp.set(row1, temp.get(row2));
             temp.set(row2, o);
         }
     }
 
     /**
      * Private method that implements the quicksort algorithm to sort the rows
      * of the Data object.
      *
      * @param column
      *            name of column to use as sorting criteria.
      * @param start
      *            starting index (for quicksort algorithm).
      * @param end
      *            ending index (for quicksort algorithm).
      */
     private void sortData(String column, int start, int end) {
         int x = start, y = end - 1;
         String basis = ((List<?>) data.get(column)).get((x + y) / 2).toString();
         if (x == y) {
             return;
         }
 
         while (x <= y) {
             while (x < end && ((List<?>) data.get(column)).get(x).toString().compareTo(basis) < 0) {
                 x++;
             }
 
             while (y >= (start - 1) && ((List<?>) data.get(column)).get(y).toString().compareTo(basis) > 0) {
                 y--;
             }
 
             if (x <= y) {
                 swapRows(x, y);
                 x++;
                 y--;
             }
         }
 
         if (x == y) {
             x++;
         }
 
         y = end - x;
 
         if (x > 0) {
             sortData(column, start, x);
         }
 
         if (y > 0) {
             sortData(column, x, end);
         }
     }
 
     /**
      * Gets the number of rows in the Data object.
      *
      * @return number of rows in Data object.
      */
     public int size() {
         return size;
     } // end method
 
     /**
      * Adds a value into the Data set at the current row, using a column name to
      * find the column in which to insert the new value.
      *
      * @param column
      *            the name of the column to set.
      * @param value
      *            value to set into column.
      */
     public void addColumnValue(String column, Object value) {
         List<Object> tempList;
         if ((tempList = data.get(column)) == null) {
             tempList = new ArrayList<Object>();
             data.put(column, tempList);
         }
         int s = tempList.size();
         if (currentPos == -1) {
             currentPos = size;
         }
 
         if (currentPos >= size) {
             size = currentPos + 1;
         }
 
         while (currentPos > s) {
             s++;
             tempList.add(null);
         }
 
         if (currentPos == s) {
             tempList.add(value);
         } else {
             tempList.set(currentPos, value);
         }
     }
 
     /**
      * Returns the row number where a certain value is.
      *
      * @param column
      *            column to be searched for value.
      * @param value
      *            object in Search of.
      * @return row # where value exists.
      */
     public int findValue(String column, Object value) {
         return data.get(column).indexOf(value);
     }
 
     /**
      * Sets the value in the Data set at the current row, using a column name to
      * find the column in which to insert the new value.
      *
      * @param column
      *            the name of the column to set.
      * @param value
      *            value to set into column.
      */
     public void setColumnValue(String column, Object value) {
         List<Object> tempList;
         if ((tempList = data.get(column)) == null) {
             tempList = new ArrayList<Object>();
             data.put(column, tempList);
         }
 
         if (currentPos == -1) {
             currentPos = 0;
         }
 
         if (currentPos >= size) {
             size++;
             tempList.add(value);
         } else if (currentPos >= tempList.size()) {
             tempList.add(value);
         } else {
             tempList.set(currentPos, value);
         }
     }
 
     /**
      * Checks to see if a column exists in the Data object.
      *
      * @param column
      *            Name of column header to check for.
      * @return True or False depending on whether the column exists.
      */
     public boolean hasHeader(String column) {
         return data.containsKey(column);
     }
 
     /**
      * Sets the current position of the Data set to the next row.
      *
      * @return True if there is another row. False if there are no more rows.
      */
     public boolean next() {
         return (++currentPos < size);
     }
 
     /**
      * Gets a Data object from a ResultSet.
      *
      * @param rs
      *            ResultSet passed in from a database query
      * @return a Data object
-     * @throws java.sql.SQLException
+     * @throws java.sql.SQLException when database access errors occur
      */
     public static Data getDataFromResultSet(ResultSet rs) throws SQLException {
         ResultSetMetaData meta = rs.getMetaData();
         Data data = new Data();
 
         int numColumns = meta.getColumnCount();
         String[] dbCols = new String[numColumns];
         for (int i = 0; i < numColumns; i++) {
             dbCols[i] = meta.getColumnName(i + 1);
             data.addHeader(dbCols[i]);
         }
 
         while (rs.next()) {
             data.next();
             for (int i = 0; i < numColumns; i++) {
                 Object o = rs.getObject(i + 1);
                 if (o instanceof byte[]) {
                     o = new String((byte[]) o); // TODO - charset?
                 }
                 data.addColumnValue(dbCols[i], o);
             }
         }
         return data;
     }
 
     /**
      * Sets the current position of the Data set to the previous row.
      *
      * @return True if there is another row. False if there are no more rows.
      */
     public boolean previous() {
         return (--currentPos >= 0);
     }
 
     /**
      * Resets the current position of the data set to just before the first
      * element.
      */
     public void reset() {
         currentPos = -1;
     }
 
     /**
      * Gets the value in the current row of the given column.
      *
      * @param column
      *            name of the column.
      * @return an Object which holds the value of the column.
      */
     public Object getColumnValue(String column) {
         try {
             if (currentPos < size) {
                 return ((List<?>) data.get(column)).get(currentPos);
             } else {
                 return null;
             }
         } catch (Exception e) {
             return null;
         }
     }
 
     /**
      * Gets the value in the current row of the given column.
      *
      * @param column
      *            index of the column (starts at 0).
      * @return an Object which holds the value of the column.
      */
     public Object getColumnValue(int column) {
         String columnName = header.get(column);
         try {
             if (currentPos < size) {
                 return ((List<?>) data.get(columnName)).get(currentPos);
             } else {
                 return null;
             }
         } catch (Exception e) {
             return null;
         }
     }
 
     public Object getColumnValue(int column, int row) {
         setCurrentPos(row);
         return getColumnValue(column);
     }
 
     public void removeColumn(int col) {
         String columnName = header.get(col);
         data.remove(columnName);
         header.remove(columnName);
     }
 
     /**
      * Sets the headers for the data set. Each header represents a column of
      * data. Each row's data can be gotten with the column header name, which
      * will always be a string.
      *
      * @param h
      *            array of strings representing the column headers.
      *            these must be distinct - duplicates will cause incorrect behaviour
      */
     public void setHeaders(String[] h) {
         int x = 0;
         header = new ArrayList<String>(h.length);
         for (x = 0; x < h.length; x++) {
             header.add(h[x]);
             data.put(h[x], new ArrayList<Object>());
         }
     }
 
     /**
      * Returns a String array of the column headers.
      *
      * @return array of strings of the column headers.
      */
     public String[] getHeaders() {
         String[] r = new String[header.size()];
         if (r.length > 0) {
             r = header.toArray(r);
         }
         return r;
     }
 
     public int getHeaderCount(){
         return header.size();
     }
 
     /**
      * This method will retrieve every entry in a certain column. It returns an
      * array of Objects from the column.
      *
      * @param columnName
      *            name of the column.
      * @return array of Objects representing the data.
      */
     public List<Object> getColumnAsObjectArray(String columnName) {
         return data.get(columnName);
     }
 
     /**
      * This method will retrieve every entry in a certain column. It returns an
      * array of strings from the column. Even if the data are not strings, they
      * will be returned as strings in this method.
      *
      * @param columnName
      *            name of the column.
      * @return array of Strings representing the data.
      */
     public String[] getColumn(String columnName) {
         String[] returnValue;
         List<?> temp = data.get(columnName);
         if (temp != null) {
             returnValue = new String[temp.size()];
             int index = 0;
             for (Object o : temp) {
                 if (o != null) {
                     if (o instanceof String) {
                         returnValue[index++] = (String) o;
                     } else {
                         returnValue[index++] = o.toString();
                     }
                 }
             }
         } else {
             returnValue = new String[0];
         }
         return returnValue;
     }
 
     /**
      * Use this method to set the entire data set. It takes an array of strings.
      * It uses the first row as the headers, and the next rows as the data
      * elements. Delimiter represents the delimiting character(s) that separate
      * each item in a data row.
      *
      * @param contents
      *            array of strings, the first element is a list of the column
      *            headers, the next elements each represent a single row of
      *            data.
      * @param delimiter
      *            the delimiter character that separates columns within the
      *            string array.
      */
     public void setData(String[] contents, String delimiter) {
         setHeaders(JOrphanUtils.split(contents[0], delimiter));
         int x = 1;
         while (x < contents.length) {
             setLine(JOrphanUtils.split(contents[x++], delimiter));
         }
     }
 
     /*
      * Deletes a header from the Data object. Takes the column name as input. It
      * will delete the entire column.
      *
      * public void deleteHeader(String s) {
      *  }
      */
 
     /**
      * Sets the data for every row in the column.
+     *
+     * @param colName
+     *            name of the column
+     * @param value
+     *            value to be set
      */
     public void setColumnData(String colName, Object value) {
         List<Object> list = this.getColumnAsObjectArray(colName);
         while (list.size() < size()) {
             list.add(value);
         }
     }
 
     public void setColumnData(int col, List<?> data) {
         reset();
         Iterator<?> iter = data.iterator();
         String columnName = header.get(col);
         while (iter.hasNext()) {
             next();
             setColumnValue(columnName, iter.next());
         }
     }
 
     /**
      * Adds a header name to the Data object.
      *
      * @param s
      *            name of header.
      */
     public void addHeader(String s) {
         header.add(s);
         data.put(s, new ArrayList<Object>(Math.max(size(), 100)));
     }
 
     /**
      * Sets a row of data using an array of strings as input. Each value in the
      * array represents a column's value in that row. Assumes the order will be
      * the same order in which the headers were added to the data set.
      *
      * @param line
      *            array of strings representing column values.
      */
     public void setLine(String[] line) {
         List<Object> tempList;
         String[] h = getHeaders();
         for (int count = 0; count < h.length; count++) {
             tempList = data.get(h[count]);
             if (count < line.length && line[count].length() > 0) {
                 tempList.add(line[count]);
             } else {
                 tempList.add("N/A");
             }
         }
         size++;
     }
 
     /**
      * Sets a row of data using an array of strings as input. Each value in the
      * array represents a column's value in that row. Assumes the order will be
      * the same order in which the headers were added to the data set.
      *
      * @param line
      *            array of strings representing column values.
      * @param deflt
      *            default value to be placed in data if line is not as long as
      *            headers.
      */
     public void setLine(String[] line, String deflt) {
         List<Object> tempList;
         String[] h = getHeaders();
         for (int count = 0; count < h.length; count++) {
             tempList = data.get(h[count]);
             if (count < line.length && line[count].length() > 0) {
                 tempList.add(line[count]);
             } else {
                 tempList.add(deflt);
             }
         }
         size++;
     }
 
     /**
      * Returns all the data in the Data set as an array of strings. Each array
      * gives a row of data, each column separated by tabs.
      *
      * @return array of strings.
      */
     public String[] getDataAsText() {
         StringBuilder temp = new StringBuilder("");
         String[] line = new String[size + 1];
         String[] elements = getHeaders();
         for (int count = 0; count < elements.length; count++) {
             temp.append(elements[count]);
             if (count + 1 < elements.length) {
                 temp.append("\t");
             }
         }
         line[0] = temp.toString();
         reset();
         int index = 1;
         temp = new StringBuilder();
         while (next()) {
             temp.setLength(0);
             for (int count = 0; count < elements.length; count++) {
                 temp.append(getColumnValue(count));
                 if (count + 1 < elements.length) {
                     temp.append("\t");
                 }
             }
             line[index++] = temp.toString();
         }
         return line;
     }
 
     @Override
     public String toString() {
         String[] contents = getDataAsText();
         StringBuilder sb = new StringBuilder();
         boolean first = true;
         for (int x = 0; x < contents.length; x++) {
             if (!first) {
                 sb.append("\n");
             } else {
                 first = false;
             }
             sb.append(contents[x]);
         }
         return sb.toString();
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/collections/HashTree.java b/src/jorphan/org/apache/jorphan/collections/HashTree.java
index 938d072f5..6ef66841e 100644
--- a/src/jorphan/org/apache/jorphan/collections/HashTree.java
+++ b/src/jorphan/org/apache/jorphan/collections/HashTree.java
@@ -1,1066 +1,1081 @@
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
 
 package org.apache.jorphan.collections;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 /**
  * This class is used to create a tree structure of objects. Each element in the
  * tree is also a key to the next node down in the tree. It provides many ways
  * to add objects and branches, as well as many ways to retrieve.
  * <p>
  * HashTree implements the Map interface for convenience reasons. The main
  * difference between a Map and a HashTree is that the HashTree organizes the
  * data into a recursive tree structure, and provides the means to manipulate
  * that structure.
  * <p>
  * Of special interest is the {@link #traverse(HashTreeTraverser)} method, which
  * provides an expedient way to traverse any HashTree by implementing the
  * {@link HashTreeTraverser} interface in order to perform some operation on the
  * tree, or to extract information from the tree.
  *
  * @see HashTreeTraverser
  * @see SearchByClass
  */
 public class HashTree implements Serializable, Map<Object, HashTree>, Cloneable {
 
     private static final long serialVersionUID = 240L;
 
     // Used for the RuntimeException to short-circuit the traversal
     private static final String FOUND = "found"; // $NON-NLS-1$
 
     // N.B. The keys can be either JMeterTreeNode or TestElement
     protected final Map<Object, HashTree> data;
 
     /**
      * Creates an empty new HashTree.
      */
     public HashTree() {
         this(null, null);
     }
 
     /**
      * Allow subclasses to provide their own Map.
      */
     protected HashTree(Map<Object, HashTree> _map) {
         this(_map, null);
     }
 
     /**
      * Creates a new HashTree and adds the given object as a top-level node.
      *
      * @param key
+     *            name of the new top-level node
      */
     public HashTree(Object key) {
         this(new HashMap<Object, HashTree>(), key);
     }
     
     /**
      * Uses the new HashTree if not null and adds the given object as a top-level node if not null
      * @param _map
      * @param key
      */
     private HashTree(Map<Object, HashTree> _map, Object key) {
         if(_map != null) {
             data = _map;
         } else {
             data = new HashMap<Object, HashTree>();
         }
         if(key != null) {
             data.put(key, new HashTree());
         }
     }
 
     /**
      * The Map given must also be a HashTree, otherwise an
      * UnsupportedOperationException is thrown. If it is a HashTree, this is
      * like calling the add(HashTree) method.
      *
      * @see #add(HashTree)
      * @see java.util.Map#putAll(Map)
      */
     @Override
     public void putAll(Map<? extends Object, ? extends HashTree> map) {
         if (map instanceof HashTree) {
             this.add((HashTree) map);
         } else {
             throw new UnsupportedOperationException("can only putAll other HashTree objects");
         }
     }
 
     /**
      * Exists to satisfy the Map interface.
      *
      * @see java.util.Map#entrySet()
      */
     @Override
     public Set<Entry<Object, HashTree>> entrySet() {
         return data.entrySet();
     }
 
     /**
      * Implemented as required by the Map interface, but is not very useful
      * here. All 'values' in a HashTree are HashTree's themselves.
      *
      * @param value
      *            Object to be tested as a value.
      * @return True if the HashTree contains the value, false otherwise.
      * @see java.util.Map#containsValue(Object)
      */
     @Override
     public boolean containsValue(Object value) {
         return data.containsValue(value);
     }
 
     /**
      * This is the same as calling HashTree.add(key,value).
      *
      * @param key
      *            to use
      * @param value
      *            to store against key
      * @see java.util.Map#put(Object, Object)
      */
     @Override
     public HashTree put(Object key, HashTree value) {
         HashTree previous = data.get(key);
         add(key, value);
         return previous;
     }
 
     /**
      * Clears the HashTree of all contents.
      *
      * @see java.util.Map#clear()
      */
     @Override
     public void clear() {
         data.clear();
     }
 
     /**
      * Returns a collection of all the sub-trees of the current tree.
      *
      * @see java.util.Map#values()
      */
     @Override
     public Collection<HashTree> values() {
         return data.values();
     }
 
     /**
      * Adds a key as a node at the current level and then adds the given
      * HashTree to that new node.
      *
      * @param key
      *            key to create in this tree
      * @param subTree
      *            sub tree to add to the node created for the first argument.
      */
     public void add(Object key, HashTree subTree) {
         add(key).add(subTree);
     }
 
     /**
      * Adds all the nodes and branches of the given tree to this tree. Is like
      * merging two trees. Duplicates are ignored.
      *
-     * @param newTree
+     * @param newTree the tree to be added
      */
     public void add(HashTree newTree) {
         for (Object item : newTree.list()) {
             add(item).add(newTree.getTree(item));
         }
     }
 
     /**
      * Creates a new HashTree and adds all the objects in the given collection
      * as top-level nodes in the tree.
      *
      * @param keys
      *            a collection of objects to be added to the created HashTree.
      */
     public HashTree(Collection<?> keys) {
         data = new HashMap<Object, HashTree>();
         for (Object o : keys) {
             data.put(o, new HashTree());
         }
     }
 
     /**
      * Creates a new HashTree and adds all the objects in the given array as
      * top-level nodes in the tree.
+     *
+     * @param keys
+     *            array with names for the new top-level nodes
      */
     public HashTree(Object[] keys) {
         data = new HashMap<Object, HashTree>();
         for (int x = 0; x < keys.length; x++) {
             data.put(keys[x], new HashTree());
         }
     }
 
     /**
      * If the HashTree contains the given object as a key at the top level, then
      * a true result is returned, otherwise false.
      *
      * @param o
      *            Object to be tested as a key.
      * @return True if the HashTree contains the key, false otherwise.
      * @see java.util.Map#containsKey(Object)
      */
     @Override
     public boolean containsKey(Object o) {
         return data.containsKey(o);
     }
 
     /**
      * If the HashTree is empty, true is returned, false otherwise.
      *
      * @return True if HashTree is empty, false otherwise.
      */
     @Override
     public boolean isEmpty() {
         return data.isEmpty();
     }
 
     /**
      * Sets a key and it's value in the HashTree. It actually sets up a key, and
      * then creates a node for the key and sets the value to the new node, as a
      * key. Any previous nodes that existed under the given key are lost.
      *
      * @param key
      *            key to be set up
      * @param value
      *            value to be set up as a key in the secondary node
      */
     public void set(Object key, Object value) {
         data.put(key, createNewTree(value));
     }
 
     /**
      * Sets a key into the current tree and assigns it a HashTree as its
      * subtree. Any previous entries under the given key are removed.
      *
      * @param key
      *            key to be set up
      * @param t
      *            HashTree that the key maps to
      */
     public void set(Object key, HashTree t) {
         data.put(key, t);
     }
 
     /**
      * Sets a key and its values in the HashTree. It sets up a key in the
      * current node, and then creates a node for that key, and sets all the
      * values in the array as keys in the new node. Any keys previously held
      * under the given key are lost.
      *
      * @param key
      *            Key to be set up
      * @param values
      *            Array of objects to be added as keys in the secondary node
      */
     public void set(Object key, Object[] values) {
         data.put(key, createNewTree(Arrays.asList(values)));
     }
 
     /**
      * Sets a key and its values in the HashTree. It sets up a key in the
      * current node, and then creates a node for that key, and set all the
      * values in the array as keys in the new node. Any keys previously held
      * under the given key are removed.
      *
      * @param key
      *            key to be set up
      * @param values
      *            Collection of objects to be added as keys in the secondary
      *            node
      */
     public void set(Object key, Collection<?> values) {
         data.put(key, createNewTree(values));
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key array as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the array.
      * Continues recursing in this manner until the end of the first array is
      * reached, at which point all the values of the second array are set as
      * keys to the bottom-most node. All previous keys of that bottom-most node
      * are removed.
      *
      * @param treePath
      *            array of keys to put into HashTree
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void set(Object[] treePath, Object[] values) {
         if (treePath != null && values != null) {
             set(Arrays.asList(treePath), Arrays.asList(values));
         }
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key array as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the array.
      * Continues recursing in this manner until the end of the first array is
      * reached, at which point all the values of the Collection of values are
      * set as keys to the bottom-most node. Any keys previously held by the
      * bottom-most node are lost.
      *
      * @param treePath
      *            array of keys to put into HashTree
      * @param values
      *            Collection of values to be added as keys to bottom-most node
      */
     public void set(Object[] treePath, Collection<?> values) {
         if (treePath != null) {
             set(Arrays.asList(treePath), values);
         }
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key list as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the list.
      * Continues recursing in this manner until the end of the first list is
      * reached, at which point all the values of the array of values are set as
      * keys to the bottom-most node. Any previously existing keys of that bottom
      * node are removed.
      *
      * @param treePath
      *            collection of keys to put into HashTree
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void set(Collection<?> treePath, Object[] values) {
         HashTree tree = addTreePath(treePath);
         tree.set(Arrays.asList(values));
     }
 
     /**
      * Sets the nodes of the current tree to be the objects of the given
      * collection. Any nodes previously in the tree are removed.
      *
      * @param values
      *            Collection of objects to set as nodes.
      */
     public void set(Collection<?> values) {
         clear();
         this.add(values);
     }
 
     /**
      * Sets a series of keys into the HashTree. It sets up the first object in
      * the key list as a key in the current node, recurses into the next
      * HashTree node through that key and adds the second object in the list.
      * Continues recursing in this manner until the end of the first list is
      * reached, at which point all the values of the Collection of values are
      * set as keys to the bottom-most node. Any previously existing keys of that
      * bottom node are lost.
      *
      * @param treePath
      *            list of keys to put into HashTree
      * @param values
      *            collection of values to be added as keys to bottom-most node
      */
     public void set(Collection<?> treePath, Collection<?> values) {
         HashTree tree = addTreePath(treePath);
         tree.set(values);
     }
 
     /**
-     * Adds an key into the HashTree at the current level.
+     * Adds an key into the HashTree at the current level. If a HashTree exists
+     * for the key already, no new tree will be added
      *
      * @param key
      *            key to be added to HashTree
+     * @return newly generated tree, if no tree was found for the given key;
+     *         existing key otherwise
      */
     public HashTree add(Object key) {
         if (!data.containsKey(key)) {
             HashTree newTree = createNewTree();
             data.put(key, newTree);
             return newTree;
         }
         return getTree(key);
     }
 
     /**
      * Adds all the given objects as nodes at the current level.
      *
      * @param keys
      *            Array of Keys to be added to HashTree.
      */
     public void add(Object[] keys) {
         for (int x = 0; x < keys.length; x++) {
             add(keys[x]);
         }
     }
 
     /**
      * Adds a bunch of keys into the HashTree at the current level.
      *
      * @param keys
      *            Collection of Keys to be added to HashTree.
      */
     public void add(Collection<?> keys) {
         for (Object o : keys) {
             add(o);
         }
     }
 
     /**
      * Adds a key and it's value in the HashTree. The first argument becomes a
      * node at the current level, and the second argument becomes a node of it.
      *
      * @param key
      *            key to be added
      * @param value
      *            value to be added as a key in the secondary node
+     * @return HashTree for which <code>value</code> is the key
      */
     public HashTree add(Object key, Object value) {
         return add(key).add(value);
     }
 
     /**
      * Adds a key and it's values in the HashTree. The first argument becomes a
      * node at the current level, and adds all the values in the array to the
      * new node.
      *
      * @param key
      *            key to be added
      * @param values
      *            array of objects to be added as keys in the secondary node
      */
     public void add(Object key, Object[] values) {
         add(key).add(values);
     }
 
     /**
      * Adds a key as a node at the current level and then adds all the objects
      * in the second argument as nodes of the new node.
      *
      * @param key
      *            key to be added
      * @param values
      *            Collection of objects to be added as keys in the secondary
      *            node
      */
     public void add(Object key, Collection<?> values) {
         add(key).add(values);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is an array that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            an array of objects representing a path
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void add(Object[] treePath, Object[] values) {
         if (treePath != null) {
             add(Arrays.asList(treePath), Arrays.asList(values));
         }
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is an array that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            an array of objects representing a path
      * @param values
      *            collection of values to be added as keys to bottom-most node
      */
     public void add(Object[] treePath, Collection<?> values) {
         if (treePath != null) {
             add(Arrays.asList(treePath), values);
         }
     }
 
     public HashTree add(Object[] treePath, Object value) {
         return add(Arrays.asList(treePath), value);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a List that represents a path to a specific node in the tree.
      * If the path doesn't already exist, it is created (the objects are added
      * along the way). At the path, all the objects in the second argument are
      * added as nodes.
      *
      * @param treePath
      *            a list of objects representing a path
      * @param values
      *            array of values to be added as keys to bottom-most node
      */
     public void add(Collection<?> treePath, Object[] values) {
         HashTree tree = addTreePath(treePath);
         tree.add(Arrays.asList(values));
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a List that represents a path to a specific node in the tree.
      * If the path doesn't already exist, it is created (the objects are added
      * along the way). At the path, the object in the second argument is added
      * as a node.
      *
      * @param treePath
      *            a list of objects representing a path
      * @param value
      *            Object to add as a node to bottom-most node
+     * @return HashTree for which <code>value</code> is the key
      */
     public HashTree add(Collection<?> treePath, Object value) {
         HashTree tree = addTreePath(treePath);
         return tree.add(value);
     }
 
     /**
      * Adds a series of nodes into the HashTree using the given path. The first
      * argument is a SortedSet that represents a path to a specific node in the
      * tree. If the path doesn't already exist, it is created (the objects are
      * added along the way). At the path, all the objects in the second argument
      * are added as nodes.
      *
      * @param treePath
      *            a SortedSet of objects representing a path
      * @param values
      *            Collection of values to be added as keys to bottom-most node
      */
     public void add(Collection<?> treePath, Collection<?> values) {
         HashTree tree = addTreePath(treePath);
         tree.add(values);
     }
 
     protected HashTree addTreePath(Collection<?> treePath) {
         HashTree tree = this;
         for (Object temp : treePath) {
             tree = tree.add(temp);
         }
         return tree;
     }
 
     /**
      * Gets the HashTree mapped to the given key.
      *
      * @param key
      *            Key used to find appropriate HashTree()
+     * @return the HashTree for <code>key</code>
      */
     public HashTree getTree(Object key) {
         return data.get(key);
     }
 
     /**
      * Returns the HashTree object associated with the given key. Same as
      * calling {@link #getTree(Object)}.
      *
      * @see java.util.Map#get(Object)
      */
     @Override
     public HashTree get(Object key) {
         return getTree(key);
     }
 
     /**
      * Gets the HashTree object mapped to the last key in the array by recursing
      * through the HashTree structure one key at a time.
      *
      * @param treePath
      *            array of keys.
      * @return HashTree at the end of the recursion.
      */
     public HashTree getTree(Object[] treePath) {
         if (treePath != null) {
             return getTree(Arrays.asList(treePath));
         }
         return this;
     }
 
     /**
      * Create a clone of this HashTree. This is not a deep clone (ie, the
      * contents of the tree are not cloned).
      *
      */
     @Override
     public Object clone() {
         HashTree newTree = new HashTree();
         cloneTree(newTree);
         return newTree;
     }
 
     protected void cloneTree(HashTree newTree) {
         for (Object key : list()) {
             newTree.set(key, (HashTree) getTree(key).clone());
         }
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
      * @return HashTree
      */
     protected HashTree createNewTree() {
         return new HashTree();
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
      * @return HashTree
      */
     protected HashTree createNewTree(Object key) {
         return new HashTree(key);
     }
 
     /**
      * Creates a new tree. This method exists to allow inheriting classes to
      * generate the appropriate types of nodes. For instance, when a node is
      * added, it's value is a HashTree. Rather than directly calling the
      * HashTree() constructor, the createNewTree() method is called. Inheriting
      * classes should override these methods and create the appropriate subclass
      * of HashTree.
      *
      * @return HashTree
      */
     protected HashTree createNewTree(Collection<?> values) {
         return new HashTree(values);
     }
 
     /**
      * Gets the HashTree object mapped to the last key in the SortedSet by
      * recursing through the HashTree structure one key at a time.
      *
      * @param treePath
      *            Collection of keys
      * @return HashTree at the end of the recursion
      */
     public HashTree getTree(Collection<?> treePath) {
         return getTreePath(treePath);
     }
 
     /**
      * Gets a Collection of all keys in the current HashTree node. If the
      * HashTree represented a file system, this would be like getting a
      * collection of all the files in the current folder.
      *
      * @return Set of all keys in this HashTree
      */
     public Collection<Object> list() {
         return data.keySet();
     }
 
     /**
      * Gets a Set of all keys in the HashTree mapped to the given key of the
      * current HashTree object (in other words, one level down. If the HashTree
      * represented a file system, this would like getting a list of all files in
      * a sub-directory (of the current directory) specified by the key argument.
      *
      * @param key
      *            key used to find HashTree to get list of
      * @return Set of all keys in found HashTree.
      */
     public Collection<?> list(Object key) {
         HashTree temp = data.get(key);
         if (temp != null) {
             return temp.list();
         }
         return new HashSet<Object>();
     }
 
     /**
      * Removes the entire branch specified by the given key.
      *
      * @see java.util.Map#remove(Object)
      */
     @Override
     public HashTree remove(Object key) {
         return data.remove(key);
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * array of keys, and returns the Set of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            Array of keys used to recurse into HashTree structure
      * @return Set of all keys found in end HashTree
      */
     public Collection<?> list(Object[] treePath) { // TODO not used?
         if (treePath != null) {
             return list(Arrays.asList(treePath));
         }
         return list();
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * List of keys, and returns the Set of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            List of keys used to recurse into HashTree structure
      * @return Set of all keys found in end HashTree
      */
     public Collection<?> list(Collection<?> treePath) {
         HashTree tree = getTreePath(treePath);
         if (tree != null) {
             return tree.list();
         }
         return new HashSet<Object>();
     }
 
     /**
      * Finds the given current key, and replaces it with the given new key. Any
      * tree structure found under the original key is moved to the new key.
+     *
+     * @param currentKey name of the key to be replaced
+     * @param newKey name of the new key
      */
     public void replaceKey(Object currentKey, Object newKey) {
         HashTree tree = getTree(currentKey);
         data.remove(currentKey);
         data.put(newKey, tree);
     }
 
     /**
      * Gets an array of all keys in the current HashTree node. If the HashTree
      * represented a file system, this would be like getting an array of all the
      * files in the current folder.
      *
      * @return array of all keys in this HashTree.
      */
     public Object[] getArray() {
         return data.keySet().toArray();
     }
 
     /**
      * Gets an array of all keys in the HashTree mapped to the given key of the
      * current HashTree object (in other words, one level down). If the HashTree
      * represented a file system, this would like getting a list of all files in
      * a sub-directory (of the current directory) specified by the key argument.
      *
      * @param key
      *            key used to find HashTree to get list of
      * @return array of all keys in found HashTree
      */
     public Object[] getArray(Object key) {
         HashTree t = getTree(key);
         if (t != null) {
             return t.getArray();
         }
         return null;
     }
 
     /**
      * Recurses down into the HashTree stucture using each subsequent key in the
      * array of keys, and returns an array of keys of the HashTree object at the
      * end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            array of keys used to recurse into HashTree structure
      * @return array of all keys found in end HashTree
      */
     public Object[] getArray(Object[] treePath) {
         if (treePath != null) {
             return getArray(Arrays.asList(treePath));
         }
         return getArray();
     }
 
     /**
-     * Recurses down into the HashTree stucture using each subsequent key in the
+     * Recurses down into the HashTree structure using each subsequent key in the
      * treePath argument, and returns an array of keys of the HashTree object at
      * the end of the recursion. If the HashTree represented a file system, this
      * would be like getting a list of all the files in a directory specified by
      * the treePath, relative from the current directory.
      *
      * @param treePath
      *            list of keys used to recurse into HashTree structure
      * @return array of all keys found in end HashTree
      */
     public Object[] getArray(Collection<?> treePath) {
         HashTree tree = getTreePath(treePath);
         return (tree != null) ? tree.getArray() : null;
     }
 
     protected HashTree getTreePath(Collection<?> treePath) {
         HashTree tree = this;
         Iterator<?> iter = treePath.iterator();
         while (iter.hasNext()) {
             if (tree == null) {
                 return null;
             }
             Object temp = iter.next();
             tree = tree.getTree(temp);
         }
         return tree;
     }
 
     /**
      * Returns a hashcode for this HashTree.
      *
      * @see java.lang.Object#hashCode()
      */
     @Override
     public int hashCode() {
         return data.hashCode() * 7;
     }
 
     /**
      * Compares all objects in the tree and verifies that the two trees contain
      * the same objects at the same tree levels. Returns true if they do, false
      * otherwise.
      *
      * @param o
      *            Object to be compared against
      * @see java.lang.Object#equals(Object)
      */
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof HashTree)) {
             return false;
         }
         HashTree oo = (HashTree) o;
         if (oo.size() != this.size()) {
             return false;
         }
         return data.equals(oo.data);
     }
 
     /**
      * Returns a Set of all the keys in the top-level of this HashTree.
      *
      * @see java.util.Map#keySet()
      */
     @Override
     public Set<Object> keySet() {
         return data.keySet();
     }
 
     /**
      * Searches the HashTree structure for the given key. If it finds the key,
      * it returns the HashTree mapped to the key. If it finds nothing, it
      * returns null.
      *
      * @param key
      *            Key to search for
      * @return HashTree mapped to key, if found, otherwise <code>null</code>
      */
     public HashTree search(Object key) {// TODO does not appear to be used
         HashTree result = getTree(key);
         if (result != null) {
             return result;
         }
         TreeSearcher searcher = new TreeSearcher(key);
         try {
             traverse(searcher);
         } catch (RuntimeException e) {
             if (!e.getMessage().equals(FOUND)){
                 throw e;
             }
             // do nothing - means object is found
         }
         return searcher.getResult();
     }
 
     /**
      * Method readObject.
      */
     private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
         ois.defaultReadObject();
     }
 
     private void writeObject(ObjectOutputStream oos) throws IOException {
         oos.defaultWriteObject();
     }
 
     /**
      * Returns the number of top-level entries in the HashTree.
      *
      * @see java.util.Map#size()
      */
     @Override
     public int size() {
         return data.size();
     }
 
     /**
      * Allows any implementation of the HashTreeTraverser interface to easily
      * traverse (depth-first) all the nodes of the HashTree. The Traverser
      * implementation will be given notification of each node visited.
      *
      * @see HashTreeTraverser
+     * @param visitor
+     *            the visitor that wants to traverse the tree
      */
     public void traverse(HashTreeTraverser visitor) {
         for (Object item : list()) {
             visitor.addNode(item, getTree(item));
             getTree(item).traverseInto(visitor);
         }
     }
 
     /**
      * The recursive method that accomplishes the tree-traversal and performs
      * the callbacks to the HashTreeTraverser.
      */
     private void traverseInto(HashTreeTraverser visitor) {
 
         if (list().size() == 0) {
             visitor.processPath();
         } else {
             for (Object item : list()) {
                 final HashTree treeItem = getTree(item);
                 visitor.addNode(item, treeItem);
                 treeItem.traverseInto(visitor);
             }
         }
         visitor.subtractNode();
     }
 
     /**
      * Generate a printable representation of the tree.
      *
      * @return a representation of the tree
      */
     @Override
     public String toString() {
         ConvertToString converter = new ConvertToString();
         try {
             traverse(converter);
         } catch (Exception e) { // Just in case
             converter.reportError(e);
         }
         return converter.toString();
     }
 
     private static class TreeSearcher implements HashTreeTraverser {
 
         private final Object target;
 
         private HashTree result;
 
         public TreeSearcher(Object t) {
             target = t;
         }
 
         public HashTree getResult() {
             return result;
         }
 
         /** {@inheritDoc} */
         @Override
         public void addNode(Object node, HashTree subTree) {
             result = subTree.getTree(target);
             if (result != null) {
                 // short circuit traversal when found
                 throw new RuntimeException(FOUND);
             }
         }
 
         /** {@inheritDoc} */
         @Override
         public void processPath() {
             // Not used
         }
 
         /** {@inheritDoc} */
         @Override
         public void subtractNode() {
             // Not used
         }
     }
 
     private static class ConvertToString implements HashTreeTraverser {
         private final StringBuilder string = new StringBuilder(getClass().getName() + "{");
 
         private final StringBuilder spaces = new StringBuilder();
 
         private int depth = 0;
 
         @Override
         public void addNode(Object key, HashTree subTree) {
             depth++;
             string.append("\n").append(getSpaces()).append(key);
             string.append(" {");
         }
 
         @Override
         public void subtractNode() {
             string.append("\n" + getSpaces() + "}");
             depth--;
         }
 
         @Override
         public void processPath() {
         }
 
         @Override
         public String toString() {
             string.append("\n}");
             return string.toString();
         }
 
         void reportError(Throwable t){
             string.append("Error: ").append(t.toString());
         }
 
         private String getSpaces() {
             if (spaces.length() < depth * 2) {
                 while (spaces.length() < depth * 2) {
                     spaces.append("  ");
                 }
             } else if (spaces.length() > depth * 2) {
                 spaces.setLength(depth * 2);
             }
             return spaces.toString();
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/collections/SearchByClass.java b/src/jorphan/org/apache/jorphan/collections/SearchByClass.java
index d234992d7..0ce00d0ab 100644
--- a/src/jorphan/org/apache/jorphan/collections/SearchByClass.java
+++ b/src/jorphan/org/apache/jorphan/collections/SearchByClass.java
@@ -1,114 +1,117 @@
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
 
 package org.apache.jorphan.collections;
 
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 /**
  * Useful for finding all nodes in the tree that represent objects of a
  * particular type. For instance, if your tree contains all strings, and a few
- * StringBuilder objects, you can use the SearchByClass traverser to find all the
- * StringBuilder objects in your tree.
+ * StringBuilder objects, you can use the SearchByClass traverser to find all
+ * the StringBuilder objects in your tree.
  * <p>
  * Usage is simple. Given a {@link HashTree} object "tree", and a SearchByClass
  * object:
  *
  * <pre>
  * HashTree tree = new HashTree();
  * // ... tree gets filled with objects
  * SearchByClass searcher = new SearchByClass(StringBuilder.class);
  * tree.traverse(searcher);
  * Iterator iter = searcher.getSearchResults().iterator();
  * while (iter.hasNext()) {
- *  StringBuilder foundNode = (StringBuilder) iter.next();
- *  HashTree subTreeOfFoundNode = searcher.getSubTree(foundNode);
- *  //  .... do something with node and subTree...
+ *     StringBuilder foundNode = (StringBuilder) iter.next();
+ *     HashTree subTreeOfFoundNode = searcher.getSubTree(foundNode);
+ *     // .... do something with node and subTree...
  * }
  * </pre>
  *
  * @see HashTree
  * @see HashTreeTraverser
  *
  * @version $Revision$
+ * @param <T>
+ *            Class that should be searched for
  */
 public class SearchByClass<T> implements HashTreeTraverser {
     private final List<T> objectsOfClass = new LinkedList<T>();
 
     private final Map<Object, ListedHashTree> subTrees = new HashMap<Object, ListedHashTree>();
 
     private final Class<T> searchClass;
 
     /**
      * Creates an instance of SearchByClass, and sets the Class to be searched
      * for.
      *
      * @param searchClass
+     *            class to be searched for
      */
     public SearchByClass(Class<T> searchClass) {
         this.searchClass = searchClass;
     }
 
     /**
      * After traversing the HashTree, call this method to get a collection of
      * the nodes that were found.
      *
      * @return Collection All found nodes of the requested type
      */
     public Collection<T> getSearchResults() { // TODO specify collection type without breaking callers
         return objectsOfClass;
     }
 
     /**
      * Given a specific found node, this method will return the sub tree of that
      * node.
      *
      * @param root
      *            the node for which the sub tree is requested
      * @return HashTree
      */
     public HashTree getSubTree(Object root) {
         return subTrees.get(root);
     }
 
     /** {@inheritDoc} */
     @SuppressWarnings("unchecked")
     @Override
     public void addNode(Object node, HashTree subTree) {
         if (searchClass.isAssignableFrom(node.getClass())) {
             objectsOfClass.add((T) node);
             ListedHashTree tree = new ListedHashTree(node);
             tree.set(node, subTree);
             subTrees.put(node, tree);
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void subtractNode() {
     }
 
     /** {@inheritDoc} */
     @Override
     public void processPath() {
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/exec/KeyToolUtils.java b/src/jorphan/org/apache/jorphan/exec/KeyToolUtils.java
index faae97987..df3748306 100644
--- a/src/jorphan/org/apache/jorphan/exec/KeyToolUtils.java
+++ b/src/jorphan/org/apache/jorphan/exec/KeyToolUtils.java
@@ -1,429 +1,452 @@
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
 
 package org.apache.jorphan.exec;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.lang3.JavaVersion;
 import org.apache.commons.lang3.SystemUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Utilities for working with Java keytool
  */
 public class KeyToolUtils {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // The DNAME which is used if none is provided
     private static final String DEFAULT_DNAME = "cn=JMeter Proxy (DO NOT TRUST)";  // $NON-NLS-1$
 
     // N.B. It seems that Opera needs a chain in order to accept server keys signed by the intermediate CA
     // Opera does not seem to like server keys signed by the root (self-signed) cert.
 
     private static final String DNAME_ROOT_CA_KEY;
 
     private static final String KEYTOOL = "keytool";
 
     /** Name of property that can be used to override the default keytool location */
     private static final String KEYTOOL_DIRECTORY = "keytool.directory"; // $NON-NLS-1$
 
     /**
      * Where to find the keytool application.
-     * If null, then keytool cannot be found.
+     * If <code>null</code>, then keytool cannot be found.
      */
     private static final String KEYTOOL_PATH;
 
     private static void addElement(StringBuilder sb, String prefix, String value) {
         if (value != null) {
             sb.append(", ");
             sb.append(prefix);
             sb.append(value);
         }
     }
 
     static {
         StringBuilder sb = new StringBuilder();
         sb.append("CN=_ DO NOT INSTALL unless this is your certificate (JMeter root CA)"); // $NON-NLS-1$
         String userName = System.getProperty("user.name"); // $NON-NLS-1$
         userName = userName.replace('\\','/'); // Backslash is special (Bugzilla 56178)
         addElement(sb, "OU=Username: ", userName); // $NON-NLS-1$
         addElement(sb, "C=", System.getProperty("user.country")); // $NON-NLS-1$ $NON-NLS-2$
         DNAME_ROOT_CA_KEY = sb.toString();
 
         // Try to find keytool application
         // N.B. Cannot use JMeter property from jorphan jar.
         final String keytoolDir = System.getProperty(KEYTOOL_DIRECTORY);
 
         String keytoolPath; // work field
         if (keytoolDir != null) {
             keytoolPath = new File(new File(keytoolDir),KEYTOOL).getPath();
         if (!checkKeytool(keytoolPath)) {
                 log.error("Cannot find keytool using property " + KEYTOOL_DIRECTORY + "="+keytoolDir);
                 keytoolPath = null; // don't try anything else if the property is provided
             }
         } else {
             keytoolPath = KEYTOOL;
             if (!checkKeytool(keytoolPath)) { // Not found on PATH, check Java Home
                 File javaHome = SystemUtils.getJavaHome();
                 if (javaHome != null) {
                     keytoolPath = new File(new File(javaHome,"bin"),KEYTOOL).getPath(); // $NON-NLS-1$
                     if (!checkKeytool(keytoolPath)) {
                         keytoolPath = null;
                     }
                 } else {
                     keytoolPath = null;
                 }
             }
         }
         if (keytoolPath == null) {
             log.error("Unable to find keytool application. Check PATH or define system property " + KEYTOOL_DIRECTORY);
         } else {
             log.info("keytool found at '" + keytoolPath + "'");
         }
         KEYTOOL_PATH = keytoolPath;
     }
 
     private static final String DNAME_INTERMEDIATE_CA_KEY  = "cn=DO NOT INSTALL THIS CERTIFICATE (JMeter Intermediate CA)"; // $NON-NLS-1$
 
     public static final String ROOT_CACERT_CRT_PFX = "ApacheJMeterTemporaryRootCA"; // $NON-NLS-1$ (do not change)
     private static final String ROOT_CACERT_CRT = ROOT_CACERT_CRT_PFX + ".crt"; // $NON-NLS-1$ (Firefox and Windows)
     private static final String ROOT_CACERT_USR = ROOT_CACERT_CRT_PFX + ".usr"; // $NON-NLS-1$ (Opera)
 
     private static final String ROOTCA_ALIAS = ":root_ca:";  // $NON-NLS-1$
     private static final String INTERMEDIATE_CA_ALIAS = ":intermediate_ca:";  // $NON-NLS-1$
 
     /** Does this class support generation of host certificates? */
     public static final boolean SUPPORTS_HOST_CERT = SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_7);
     // i.e. does keytool support -gencert and -ext ?
 
     private KeyToolUtils() {
         // not instantiable
     }
 
     /**
      * Generate a self-signed keypair using the algorithm "RSA".
      * Requires Java 7 or later if the "ext" parameter is not null.
      *
      * @param keystore the keystore; if it already contains the alias the command will fail
      * @param alias the alias to use, not null
      * @param password the password to use for the store and the key
      * @param validity the validity period in days, greater than 0
-     * @param dname the dname value, if omitted use "cn=JMeter Proxy (DO NOT TRUST)"
+     * @param dname the <em>distinguished name</em> value, if omitted use "cn=JMeter Proxy (DO NOT TRUST)"
      * @param ext if not null, the extension (-ext) to add (e.g. "bc:c"). This requires Java 7.
      *
-     * @throws IOException
+     * @throws IOException if keytool was not configured or running keytool application fails
      */
     public static void genkeypair(final File keystore, String alias, final String password, int validity, String dname, String ext)
             throws IOException {
         final File workingDir = keystore.getParentFile();
         final SystemCommand nativeCommand = new SystemCommand(workingDir, null);
         final List<String> arguments = new ArrayList<String>();
         arguments.add(getKeyToolPath());
         arguments.add("-genkeypair"); // $NON-NLS-1$
         arguments.add("-alias"); // $NON-NLS-1$
         arguments.add(alias);
         arguments.add("-dname"); // $NON-NLS-1$
         arguments.add(dname == null ? DEFAULT_DNAME : dname);
         arguments.add("-keyalg"); // $NON-NLS-1$
         arguments.add("RSA"); // $NON-NLS-1$
 
         arguments.add("-keystore"); // $NON-NLS-1$
         arguments.add(keystore.getName());
         arguments.add("-storepass"); // $NON-NLS-1$
         arguments.add(password);
         arguments.add("-keypass"); // $NON-NLS-1$
         arguments.add(password);
         arguments.add("-validity"); // $NON-NLS-1$
         arguments.add(Integer.toString(validity));
         if (ext != null) { // Requires Java 7
             arguments.add("-ext"); // $NON-NLS-1$
             arguments.add(ext);
         }
         try {
             int exitVal = nativeCommand.run(arguments);
             if (exitVal != 0) {
                 throw new IOException("  >> " + nativeCommand.getOutResult().trim() + " <<"
                     + "\nCommand failed, code: " + exitVal
                     + "\n'" + formatCommand(arguments)+"'");
             }
         } catch (InterruptedException e) {
             throw new IOException("Command was interrupted\n" + nativeCommand.getOutResult(), e);
         }
     }
 
     /**
      * Formats arguments
      * @param arguments
      * @return String command line
      */
     private static String formatCommand(List<String> arguments) {
         StringBuilder builder = new StringBuilder();
         boolean redact = false; // whether to redact next parameter
         for (String string : arguments) {
             final boolean quote = string.contains(" ");
             if (quote) builder.append("\"");
             builder.append(redact? "{redacted}" : string);
             if (quote) builder.append("\"");
             builder.append(" ");
             redact = string.equals("-storepass") || string.equals("-keypass");
         }
         if(arguments.size()>0) {
             builder.setLength(builder.length()-1); // trim trailing space
         }
         return builder.toString();
     }
 
     /**
      * Creates a self-signed Root CA certificate and an intermediate CA certificate
      * (signed by the Root CA certificate) that can be used to sign server certificates.
      * The Root CA certificate file is exported to the same directory as the keystore
      * in formats suitable for Firefox/Chrome/IE (.crt) and Opera (.usr).
      * Requires Java 7 or later.
      *
      * @param keystore the keystore in which to store everything
      * @param password the password for keystore and keys
      * @param validity the validity period in days, must be greater than 0
      *
-     * @throws IOException
+     * @throws IOException if keytool was not configured, running keytool application failed or copying the keys failed
      */
     public static void generateProxyCA(File keystore, String password,  int validity) throws IOException {
         File caCert_crt = new File(ROOT_CACERT_CRT);
         File caCert_usr = new File(ROOT_CACERT_USR);
         boolean fileExists = false;
         if (!keystore.delete() && keystore.exists()) {
             log.warn("Problem deleting the keystore '" + keystore + "'");
             fileExists = true;
         }
         if (!caCert_crt.delete() && caCert_crt.exists()) {
             log.warn("Problem deleting the certificate file '" + caCert_crt + "'");
             fileExists = true;
         }
         if (!caCert_usr.delete() && caCert_usr.exists()) {
             log.warn("Problem deleting the certificate file '" + caCert_usr + "'");
             fileExists = true;
         }
         if (fileExists) {
             log.warn("If problems occur when recording SSL, delete the files manually and retry.");
         }
         // Create the self-signed keypairs (requires Java 7 for -ext flag)
         KeyToolUtils.genkeypair(keystore, ROOTCA_ALIAS, password, validity, DNAME_ROOT_CA_KEY, "bc:c");
         KeyToolUtils.genkeypair(keystore, INTERMEDIATE_CA_ALIAS, password, validity, DNAME_INTERMEDIATE_CA_KEY, "bc:c");
 
         // Create cert for CA using root (requires Java 7 for gencert)
         ByteArrayOutputStream certReqOut = new ByteArrayOutputStream();
         // generate the request
         KeyToolUtils.keytool("-certreq", keystore, password, INTERMEDIATE_CA_ALIAS, null, certReqOut);
 
         // generate the certificate and store in output file
         InputStream certReqIn = new ByteArrayInputStream(certReqOut.toByteArray());
         ByteArrayOutputStream genCertOut = new ByteArrayOutputStream();
         KeyToolUtils.keytool("-gencert", keystore, password, ROOTCA_ALIAS, certReqIn, genCertOut, "-ext", "BC:0");
 
         // import the signed CA cert into the store (root already there) - both are needed to sign the domain certificates
         InputStream genCertIn = new ByteArrayInputStream(genCertOut.toByteArray());
         KeyToolUtils.keytool("-importcert", keystore, password, INTERMEDIATE_CA_ALIAS, genCertIn, null);
 
         // Export the Root CA for Firefox/Chrome/IE
         KeyToolUtils.keytool("-exportcert", keystore, password, ROOTCA_ALIAS, null, null, "-rfc", "-file", ROOT_CACERT_CRT);
         // Copy for Opera
         if(caCert_crt.exists() && caCert_crt.canRead()) {
             FileUtils.copyFile(caCert_crt, caCert_usr);            
         } else {
             log.warn("Failed creating "+caCert_crt.getAbsolutePath()+", check 'keytool' utility in path is available and points to a JDK >= 7");
         }
     }
 
     /**
      * Create a host certificate signed with the CA certificate.
      * Requires Java 7 or later.
      *
      * @param keystore the keystore to use
      * @param password the password to use for the keystore and keys
      * @param host the host, e.g. jmeter.apache.org or *.apache.org; also used as the alias
      * @param validity the validity period for the generated keypair
      *
-     * @throws IOException
+     * @throws IOException if keytool was not configured or running keytool application failed
      *
      */
     public static void generateHostCert(File keystore, String password, String host, int validity) throws IOException {
         // generate the keypair for the host
         generateSignedCert(keystore, password, validity,
                 host,  // alias
                 host); // subject
     }
 
     private static void generateSignedCert(File keystore, String password,
             int validity, String alias, String subject) throws IOException {
         String dname = "cn=" + subject + ", o=JMeter Proxy (TEMPORARY TRUST ONLY)";
         KeyToolUtils.genkeypair(keystore, alias, password, validity, dname, null);
         //rem generate cert for DOMAIN using CA (requires Java7 for gencert) and import it
 
         // get the certificate request
         ByteArrayOutputStream certReqOut = new ByteArrayOutputStream();
         KeyToolUtils.keytool("-certreq", keystore, password, alias, null, certReqOut);
 
         // create the certificate
         //rem ku:c=dig,keyE means KeyUsage:criticial=digitalSignature,keyEncipherment
         InputStream certReqIn = new ByteArrayInputStream(certReqOut.toByteArray());
         ByteArrayOutputStream certOut = new ByteArrayOutputStream();
         KeyToolUtils.keytool("-gencert", keystore, password, INTERMEDIATE_CA_ALIAS, certReqIn, certOut, "-ext", "ku:c=dig,keyE");
 
         // inport the certificate
         InputStream certIn = new ByteArrayInputStream(certOut.toByteArray());
         KeyToolUtils.keytool("-importcert", keystore, password, alias, certIn, null, "-noprompt");
     }
 
     /**
      * List the contents of a keystore
      *
-     * @param keystore the keystore file
-     * @param storePass the keystore password
+     * @param keystore
+     *            the keystore file
+     * @param storePass
+     *            the keystore password
      * @return the output from the command "keytool -list -v"
+     * @throws IOException
+     *             if keytool was not configured or running keytool application
+     *             failed
      */
     public static String list(final File keystore, final String storePass) throws IOException {
         final File workingDir = keystore.getParentFile();
         final SystemCommand nativeCommand = new SystemCommand(workingDir, null);
         final List<String> arguments = new ArrayList<String>();
         arguments.add(getKeyToolPath());
         arguments.add("-list"); // $NON-NLS-1$
         arguments.add("-v"); // $NON-NLS-1$
 
         arguments.add("-keystore"); // $NON-NLS-1$
         arguments.add(keystore.getName());
         arguments.add("-storepass"); // $NON-NLS-1$
         arguments.add(storePass);
         try {
             int exitVal = nativeCommand.run(arguments);
             if (exitVal != 0) {
                 throw new IOException("Command failed, code: " + exitVal + "\n" + nativeCommand.getOutResult());
             }
         } catch (InterruptedException e) {
             throw new IOException("Command was interrupted\n" + nativeCommand.getOutResult(), e);
         }
         return nativeCommand.getOutResult();
     }
 
     /**
      * Returns a list of the CA aliases that should be in the keystore.
      *
      * @return the aliases that are used for the keystore
      */
     public static String[] getCAaliases() {
         return new String[]{ROOTCA_ALIAS, INTERMEDIATE_CA_ALIAS};
     }
 
     /**
      * Get the root CA alias; needed to check the serial number and fingerprint
      *
      * @return the alias
      */
     public static String getRootCAalias() {
         return ROOTCA_ALIAS;
     }
 
     /**
      * Helper method to simplify chaining keytool commands.
      *
-     * @param command the command, not null
-     * @param keystore the keystore, not nill
-     * @param password the password used for keystore and key, not null
-     * @param alias the alias, not null
-     * @param input where to source input, may be null
-     * @param output where to send output, may be null
-     * @param parameters additional parameters to the command, may be null
+     * @param command
+     *            the command, not null
+     * @param keystore
+     *            the keystore, not nill
+     * @param password
+     *            the password used for keystore and key, not null
+     * @param alias
+     *            the alias, not null
+     * @param input
+     *            where to source input, may be null
+     * @param output
+     *            where to send output, may be null
+     * @param parameters
+     *            additional parameters to the command, may be null
      * @throws IOException
+     *             if keytool is not configured or running it failed
      */
     private static void keytool(String command, File keystore, String password, String alias,
             InputStream input, OutputStream output, String ... parameters)
             throws IOException {
         final File workingDir = keystore.getParentFile();
         final SystemCommand nativeCommand = new SystemCommand(workingDir, 0L, 0, null, input, output, null);
         final List<String> arguments = new ArrayList<String>();
         arguments.add(getKeyToolPath());
         arguments.add(command);
         arguments.add("-keystore"); // $NON-NLS-1$
         arguments.add(keystore.getName());
         arguments.add("-storepass"); // $NON-NLS-1$
         arguments.add(password);
         arguments.add("-keypass"); // $NON-NLS-1$
         arguments.add(password);
         arguments.add("-alias"); // $NON-NLS-1$
         arguments.add(alias);
         for (String parameter : parameters) {
             arguments.add(parameter);
         }
 
         try {
             int exitVal = nativeCommand.run(arguments);
             if (exitVal != 0) {
                 throw new IOException("Command failed, code: " + exitVal + "\n" + nativeCommand.getOutResult());
             }
         } catch (InterruptedException e) {
             throw new IOException("Command was interrupted\n" + nativeCommand.getOutResult(), e);
         }
     }
 
+    /**
+     * @return flag whether {@link KeyToolUtils#KEYTOOL_PATH KEYTOOL_PATH} is
+     *         configured (is not <code>null</code>)
+     */
     public static boolean haveKeytool() {
         return KEYTOOL_PATH != null;
     }
 
+    /**
+     * @return path to keytool binary
+     * @throws IOException
+     *             when {@link KeyToolUtils#KEYTOOL_PATH KEYTOOL_PATH} is
+     *             <code>null</code>
+     */
     private static String getKeyToolPath() throws IOException {
         if (KEYTOOL_PATH == null) {
             throw new IOException("keytool application cannot be found");
         }
         return KEYTOOL_PATH;
     }
 
     /**
      * Check if keytool can be found
      * @param keytoolPath the path to check
      */
     private static boolean checkKeytool(String keytoolPath) {
         final SystemCommand nativeCommand = new SystemCommand(null, null);
         final List<String> arguments = new ArrayList<String>();
         arguments.add(keytoolPath);
         arguments.add("-help"); // $NON-NLS-1$
         try {
             int status = nativeCommand.run(arguments);
             if (log.isDebugEnabled()) {
                 log.debug("checkKeyTool:status=" + status);
                 log.debug(nativeCommand.getOutResult());
             }
             /*
              * Some implementations of keytool return status 1 for -help
              * MacOS/Java 7 returns 2 if it cannot find keytool
              */
             return status == 0 || status == 1; // TODO this is rather fragile
         } catch (IOException ioe) {
             return false;
         } catch (InterruptedException e) {
             log.error("Command was interrupted\n" + nativeCommand.getOutResult(), e);
             return false;
         }
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/exec/SystemCommand.java b/src/jorphan/org/apache/jorphan/exec/SystemCommand.java
index 27754744b..0c92f0e55 100644
--- a/src/jorphan/org/apache/jorphan/exec/SystemCommand.java
+++ b/src/jorphan/org/apache/jorphan/exec/SystemCommand.java
@@ -1,256 +1,256 @@
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
 
 package org.apache.jorphan.exec;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jorphan.util.JOrphanUtils;
 
 /**
  * Utility class for invoking native system applications
  */
 public class SystemCommand {
     public static final int POLL_INTERVAL = 100;
     private final File directory;
     private final Map<String, String> env;
     private Map<String, String> executionEnvironment;
     private final InputStream stdin;
     private final OutputStream stdout;
     private final boolean stdoutWasNull;
     private final OutputStream stderr;
     private final long timeoutMillis;
     private final int pollInterval;
 
     /**
      * @param env Environment variables appended to environment (may be null)
      * @param directory File working directory (may be null)
      */
     public SystemCommand(File directory, Map<String, String> env) {
         this(directory, 0L, POLL_INTERVAL, env, (InputStream) null, (OutputStream) null, (OutputStream) null);
     }
 
     /**
      * 
      * @param env Environment variables appended to environment (may be null)
      * @param directory File working directory (may be null)
      * @param timeoutMillis timeout in Milliseconds
      * @param pollInterval Value used to poll for Process execution end
      * @param stdin File name that will contain data to be input to process (may be null)
      * @param stdout File name that will contain out stream (may be null)
      * @param stderr File name that will contain err stream (may be null)
      * @throws IOException if the input file is not found or output cannot be written
      */
     public SystemCommand(File directory, long timeoutMillis, int pollInterval, Map<String, String> env, String stdin, String stdout, String stderr) throws IOException {
         this(directory, timeoutMillis, pollInterval, env, checkIn(stdin), checkOut(stdout), checkOut(stderr));
     }
 
     private static InputStream checkIn(String stdin) throws FileNotFoundException {
         String in = JOrphanUtils.nullifyIfEmptyTrimmed(stdin);
         if (in == null) {
             return null;
         } else {
             return new FileInputStream(in);
         }
     }
 
     private static OutputStream checkOut(String path) throws IOException {
         String in = JOrphanUtils.nullifyIfEmptyTrimmed(path);
         if (in == null) {
             return null;
         } else {
             return new FileOutputStream(path);
         }
     }
 
     /**
      * 
      * @param env Environment variables appended to environment (may be null)
      * @param directory File working directory (may be null)
      * @param timeoutMillis timeout in Milliseconds
      * @param pollInterval Value used to poll for Process execution end
      * @param stdin File name that will contain data to be input to process (may be null)
      * @param stdout File name that will contain out stream (may be null)
      * @param stderr File name that will contain err stream (may be null)
      */
     public SystemCommand(File directory, long timeoutMillis, int pollInterval, Map<String, String> env, InputStream stdin, OutputStream stdout, OutputStream stderr) {
         super();
         this.timeoutMillis = timeoutMillis;
         this.directory = directory;
         this.env = env;
         this.pollInterval = pollInterval;
         this.stdin = stdin;
         this.stdoutWasNull = stdout == null;
         if (stdout == null) {
             this.stdout = new ByteArrayOutputStream(); // capture the output
         } else {
             this.stdout = stdout;
         }
         this.stderr = stderr;
     }
 
     /**
      * @param arguments List of strings, not null
      * @return return code
-     * @throws InterruptedException
-     * @throws IOException
+     * @throws InterruptedException when execution was interrupted
+     * @throws IOException when I/O error occurs while execution
      */
     public int run(List<String> arguments) throws InterruptedException, IOException {
         return run(arguments, stdin, stdout, stderr);
     }
 
     // helper method to allow input and output to be changed for chaining
     private int run(List<String> arguments, InputStream in, OutputStream out, OutputStream err) throws InterruptedException, IOException {
         Process proc = null;
         final ProcessBuilder procBuild = new ProcessBuilder(arguments);
         if (env != null) {
             procBuild.environment().putAll(env);
         }
         this.executionEnvironment = Collections.unmodifiableMap(procBuild.environment());
         procBuild.directory(directory);
         if (err == null) {
             procBuild.redirectErrorStream(true);
         }
         try
         {
             proc = procBuild.start();
 
             final OutputStream procOut = proc.getOutputStream();
             final InputStream procErr = proc.getErrorStream();
             final InputStream procIn = proc.getInputStream();
 
             final StreamCopier swerr;
             if (err != null){
                 swerr = new StreamCopier(procErr, err);
                 swerr.start();
             } else {
                 swerr = null;
             }
 
             final StreamCopier swout = new StreamCopier(procIn, out);
             swout.start();
             
             final StreamCopier swin;
             if (in != null) {
                 swin = new StreamCopier(in, procOut);
                 swin.start();
             } else {
                 swin = null;
                 procOut.close(); // ensure the application does not hang if it requests input
             }
             int exitVal = waitForEndWithTimeout(proc, timeoutMillis);
 
             swout.join();
             if (swerr != null) {
                 swerr.join();
             }
             if (swin != null) {
                 swin.interrupt(); // the copying thread won't generally detect EOF
                 swin.join();
             }
             procErr.close();
             procIn.close();
             procOut.close();
             return exitVal;
         } finally {
             if(proc != null) {
                 try {
                     proc.destroy();
                 } catch (Exception ignored) {
                     // Ignored
                 }
             }
         }
     }
 
     /**
      * Pipe the output of one command into another
      * 
      * @param arguments1 first command to run
      * @param arguments2 second command to run
      * @return exit status
-     * @throws InterruptedException
-     * @throws IOException
+     * @throws InterruptedException when execution gets interrupted
+     * @throws IOException when I/O error occurs while execution
      */
     public int run(List<String> arguments1, List<String> arguments2) throws InterruptedException, IOException {
         ByteArrayOutputStream out = new ByteArrayOutputStream(); // capture the intermediate output
         int exitCode=run(arguments1,stdin,out, stderr);
         if (exitCode == 0) {
             exitCode = run(arguments2,new ByteArrayInputStream(out.toByteArray()),stdout,stderr);
         }
         return exitCode;
     }
 
     /**
      * Wait for end of proc execution or timeout if timeoutInMillis is greater than 0
      * @param proc Process
      * @param timeoutInMillis long timeout in ms
      * @return proc exit value
      * @throws InterruptedException
      */
     private int waitForEndWithTimeout(Process proc, long timeoutInMillis) throws InterruptedException {
         if (timeoutInMillis <= 0L) {
             return proc.waitFor();
         } else {
             long now = System.currentTimeMillis();
             long finish = now + timeoutInMillis;
             while(System.currentTimeMillis() < finish) {
                 try {
                     return proc.exitValue();
                 } catch (IllegalThreadStateException e) { // not yet terminated
                     Thread.sleep(pollInterval);
                 }
             }
             try {
                 return proc.exitValue();
             } catch (IllegalThreadStateException e) { // not yet terminated
                 // N.B. proc.destroy() is called by the finally clause in the run() method
                 throw new InterruptedException( "Process timeout out after " + timeoutInMillis + " milliseconds" );
             }
         }
     }
 
     /**
      * @return Out/Err stream contents
      */
     public String getOutResult() {
         if (stdoutWasNull) { // we are capturing output
             return stdout.toString(); // Default charset is probably appropriate here.
         } else {
             return "";
         }
     }
 
     /**
      * @return the executionEnvironment
      */
     public Map<String, String> getExecutionEnvironment() {
         return executionEnvironment;
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/logging/LoggingManager.java b/src/jorphan/org/apache/jorphan/logging/LoggingManager.java
index 93eee7a17..92756fed4 100644
--- a/src/jorphan/org/apache/jorphan/logging/LoggingManager.java
+++ b/src/jorphan/org/apache/jorphan/logging/LoggingManager.java
@@ -1,361 +1,369 @@
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
 
 package org.apache.jorphan.logging;
 
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.Writer;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Iterator;
 import java.util.Properties;
+import java.util.logging.LogManager;
 
 import org.apache.avalon.excalibur.logger.LogKitLoggerManager;
 import org.apache.avalon.framework.configuration.Configuration;
 import org.apache.avalon.framework.configuration.ConfigurationException;
 import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
 import org.apache.avalon.framework.context.Context;
 import org.apache.avalon.framework.context.ContextException;
 import org.apache.avalon.framework.context.DefaultContext;
 import org.apache.log.Hierarchy;
 import org.apache.log.LogTarget;
 import org.apache.log.Logger;
 import org.apache.log.Priority;
 import org.apache.log.format.PatternFormatter;
 import org.apache.log.output.NullOutputLogTarget;
 import org.apache.log.output.io.WriterTarget;
 import org.xml.sax.SAXException;
 
 /**
  * Manages JMeter logging
  */
 public final class LoggingManager {
     // N.B time pattern is passed to java.text.SimpleDateFormat
     /*
      * Predefined format patterns, selected by the property log_format_type (see
      * jmeter.properties) The new-line is added later
      */
     public static final String DEFAULT_PATTERN = "%{time:yyyy/MM/dd HH:mm:ss} %5.5{priority} - "  //$NON_NLS-1$
             + "%{category}: %{message} %{throwable}"; //$NON_NLS-1$
 
     private static final String PATTERN_THREAD_PREFIX = "%{time:yyyy/MM/dd HH:mm:ss} %5.5{priority} "  //$NON_NLS-1$
             + "%20{thread} %{category}: %{message} %{throwable}";  //$NON_NLS-1$
 
     private static final String PATTERN_THREAD_SUFFIX = "%{time:yyyy/MM/dd HH:mm:ss} %5.5{priority} "  //$NON_NLS-1$
             + "%{category}[%{thread}]: %{message} %{throwable}";  //$NON_NLS-1$
 
     // Needs to be volatile as may be referenced from multiple threads
     // TODO see if this can be made final somehow
     private static volatile PatternFormatter format = null;
 
     /** Used to hold the default logging target. */
     //@GuardedBy("this")
     private static LogTarget target = new NullOutputLogTarget();
 
     // Hack to detect when System.out has been set as the target, to avoid closing it
     private static volatile boolean isTargetSystemOut = false;// Is the target System.out?
 
     private static volatile boolean isWriterSystemOut = false;// Is the Writer System.out?
 
     public static final String LOG_FILE = "log_file";  //$NON_NLS-1$
 
     public static final String LOG_PRIORITY = "log_level";  //$NON_NLS-1$
 
     private LoggingManager() {
         // non-instantiable - static methods only
     }
 
     /**
      * Initialise the logging system from the Jmeter properties. Logkit loggers
      * inherit from their parents.
      *
      * Normally the jmeter properties file defines a single log file, so set
      * this as the default from "log_file", default "jmeter.log" The default
      * priority is set from "log_level", with a default of INFO
      *
+     * @param properties
+     *            {@link Properties} to be used for initialization
      */
     public static void initializeLogging(Properties properties) {
         setFormat(properties);
 
         // Set the top-level defaults
         setTarget(makeWriter(properties.getProperty(LOG_FILE, "jmeter.log"), LOG_FILE));  //$NON_NLS-1$
         setPriority(properties.getProperty(LOG_PRIORITY, "INFO"));
 
         setLoggingLevels(properties);
         // now set the individual categories (if any)
 
         setConfig(properties);// Further configuration
     }
 
     private static void setFormat(Properties properties) {
         String pattern = DEFAULT_PATTERN;
         String type = properties.getProperty("log_format_type", "");  //$NON_NLS-1$
         if (type.length() == 0) {
             pattern = properties.getProperty("log_format", DEFAULT_PATTERN);  //$NON_NLS-1$
         } else {
             if (type.equalsIgnoreCase("thread_suffix")) {  //$NON_NLS-1$
                 pattern = PATTERN_THREAD_SUFFIX;
             } else if (type.equalsIgnoreCase("thread_prefix")) {  //$NON_NLS-1$
                 pattern = PATTERN_THREAD_PREFIX;
             } else {
                 pattern = DEFAULT_PATTERN;
             }
         }
         format = new PatternFormatter(pattern + "\n"); //$NON_NLS-1$
     }
 
     private static void setConfig(Properties p) {
         String cfg = p.getProperty("log_config"); //$NON_NLS-1$
         if (cfg == null) {
             return;
         }
 
         // Make sure same hierarchy is used
         Hierarchy hier = Hierarchy.getDefaultHierarchy();
         LogKitLoggerManager manager = new LogKitLoggerManager(null, hier, null, null);
 
         DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
         try {
             Configuration c = builder.buildFromFile(cfg);
             Context ctx = new DefaultContext();
             manager.contextualize(ctx);
             manager.configure(c);
         } catch (IllegalArgumentException e) {
             // This happens if the default log-target id-ref specifies a non-existent target
             System.out.println("Error processing logging config " + cfg);
             System.out.println(e.toString());
         } catch (NullPointerException e) {
             // This can happen if a log-target id-ref specifies a non-existent target
             System.out.println("Error processing logging config " + cfg);
             System.out.println("Perhaps a log target is missing?");
         } catch (ConfigurationException e) {
             System.out.println("Error processing logging config " + cfg);
             System.out.println(e.toString());
         } catch (SAXException e) {
             System.out.println("Error processing logging config " + cfg);
             System.out.println(e.toString());
         } catch (IOException e) {
             System.out.println("Error processing logging config " + cfg);
             System.out.println(e.toString());
         } catch (ContextException e) {
             System.out.println("Error processing logging config " + cfg);
             System.out.println(e.toString());
         }
     }
 
     /*
      * Helper method to ensure that format is initialised if initializeLogging()
      * has not yet been called.
      */
     private static PatternFormatter getFormat() {
         if (format == null) {
             format = new PatternFormatter(DEFAULT_PATTERN + "\n"); //$NON_NLS-1$
         }
         return format;
     }
 
     /*
      * Helper method to handle log target creation. If there is an error
      * creating the file, then it uses System.out.
      */
     private static Writer makeWriter(String logFile, String propName) {
         // If the name contains at least one set of paired single-quotes, reformat using DateFormat
         final int length = logFile.split("'",-1).length;
         if (length > 1 && length %2 == 1){
             try {
                 SimpleDateFormat df = new SimpleDateFormat(logFile);
                 logFile = df.format(new Date());
             } catch (Exception ignored) {
             }
         }
         Writer wt;
         isWriterSystemOut = false;
         try {
             wt = new FileWriter(logFile);
         } catch (Exception e) {
             System.out.println(propName + "=" + logFile + " " + e.toString());
             System.out.println("[" + propName + "-> System.out]");
             isWriterSystemOut = true;
             wt = new PrintWriter(System.out);
         }
         return wt;
     }
 
     /**
      * Handle LOG_PRIORITY.category=priority and LOG_FILE.category=file_name
      * properties. If the prefix is detected, then remove it to get the
      * category.
+     *
+     * @param appProperties
+     *            {@link Properties} that contain the
+     *            {@link LoggingManager#LOG_PRIORITY LOG_PRIORITY} and
+     *            {@link LoggingManager#LOG_FILE LOG_FILE} prefixed entries
      */
     public static void setLoggingLevels(Properties appProperties) {
         Iterator<?> props = appProperties.keySet().iterator();
         while (props.hasNext()) {
             String prop = (String) props.next();
             if (prop.startsWith(LOG_PRIORITY + ".")) //$NON_NLS-1$
             // don't match the empty category
             {
                 String category = prop.substring(LOG_PRIORITY.length() + 1);
                 setPriority(appProperties.getProperty(prop), category);
             }
             if (prop.startsWith(LOG_FILE + ".")) { //$NON_NLS-1$
                 String category = prop.substring(LOG_FILE.length() + 1);
                 String file = appProperties.getProperty(prop);
                 setTarget(new WriterTarget(makeWriter(file, prop), getFormat()), category);
             }
         }
     }
 
     private static final String PACKAGE_PREFIX = "org.apache."; //$NON_NLS-1$
 
     /**
      * Removes the standard prefix, i.e. "org.apache.".
      * 
      * @param name from which to remove the prefix
      * @return the name with the prefix removed
      */
     public static String removePrefix(String name){
         if (name.startsWith(PACKAGE_PREFIX)) { // remove the package prefix
             name = name.substring(PACKAGE_PREFIX.length());
         }
         return name;
     }
     /**
      * Get the Logger for a class - no argument needed because the calling class
      * name is derived automatically from the call stack.
      *
      * @return Logger
      */
     public static Logger getLoggerForClass() {
         String className = new Exception().getStackTrace()[1].getClassName();
         return Hierarchy.getDefaultHierarchy().getLoggerFor(removePrefix(className));
     }
 
     /**
      * Get the Logger for a class.
      * 
      * @param category - the full name of the logger category
      *
      * @return Logger
      */
     public static Logger getLoggerFor(String category) {
         return Hierarchy.getDefaultHierarchy().getLoggerFor(category);
     }
 
     /**
      * Get the Logger for a class.
      * 
      * @param category - the full name of the logger category, this will have the prefix removed.
      *
      * @return Logger
      */
     public static Logger getLoggerForShortName(String category) {
         return Hierarchy.getDefaultHierarchy().getLoggerFor(removePrefix(category));
     }
 
     /**
      * Set the logging priority for a category.
      * 
      * @param priority - string containing the priority name, e.g. "INFO", "WARN", "DEBUG", "FATAL_ERROR"
      * @param category - string containing the category
      */
     public static void setPriority(String priority, String category) {
         setPriority(Priority.getPriorityForName(priority), category);
     }
 
     /**
      * Set the logging priority for a category.
      * 
      * @param priority - priority, e.g. DEBUG, INFO
      * @param fullName - e.g. org.apache.jmeter.etc, will have the prefix removed.
      */
     public static void setPriorityFullName(String priority, String fullName) {
         setPriority(Priority.getPriorityForName(priority), removePrefix(fullName));
     }
 
     /**
      * Set the logging priority for a category.
      * 
      * @param priority - e.g. Priority.DEBUG
      * @param category - string containing the category
      */
     public static void setPriority(Priority priority, String category) {
         Hierarchy.getDefaultHierarchy().getLoggerFor(category).setPriority(priority);
     }
 
     public static void setPriority(String p) {
         setPriority(Priority.getPriorityForName(p));
     }
 
     /**
      * Set the default logging priority.
      * 
      * @param priority e.g. Priority.DEBUG
      */
     public static void setPriority(Priority priority) {
         Hierarchy.getDefaultHierarchy().setDefaultPriority(priority);
     }
 
     /**
      * Set the logging target for a category.
      * 
      * @param target the LogTarget
      * @param category the category name
      */
     public static void setTarget(LogTarget target, String category) {
         Logger logger = Hierarchy.getDefaultHierarchy().getLoggerFor(category);
         logger.setLogTargets(new LogTarget[] { target });
     }
 
     /**
      * Sets the default log target from the parameter. The existing target is
      * first closed if necessary.
      *
      * @param targetFile
      *            (Writer)
      */
     private static synchronized void setTarget(Writer targetFile) {
         if (target == null) {
             target = getTarget(targetFile, getFormat());
             isTargetSystemOut = isWriterSystemOut;
         } else {
             if (!isTargetSystemOut && target instanceof WriterTarget) {
                 ((WriterTarget) target).close();
             }
             target = getTarget(targetFile, getFormat());
             isTargetSystemOut = isWriterSystemOut;
         }
         Hierarchy.getDefaultHierarchy().setDefaultLogTarget(target);
     }
 
     private static LogTarget getTarget(Writer targetFile, PatternFormatter fmt) {
         return new WriterTarget(targetFile, fmt);
     }
 
     /**
      * Add logTargets to root logger
      * FIXME What's the clean way to add a LogTarget afterwards ?
      * @param logTargets LogTarget array
      */
     public static void addLogTargetToRootLogger(LogTarget[] logTargets) {
         LogTarget[] newLogTargets = new LogTarget[logTargets.length+1];
         System.arraycopy(logTargets, 0, newLogTargets, 1, logTargets.length);
         newLogTargets[0] = target;
         Hierarchy.getDefaultHierarchy().getRootLogger().setLogTargets(newLogTargets);
     }
 }
diff --git a/src/jorphan/org/apache/jorphan/util/Converter.java b/src/jorphan/org/apache/jorphan/util/Converter.java
index 6d54c1106..98ec5f03b 100644
--- a/src/jorphan/org/apache/jorphan/util/Converter.java
+++ b/src/jorphan/org/apache/jorphan/util/Converter.java
@@ -1,383 +1,585 @@
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
 package org.apache.jorphan.util;
 
 import java.io.File;
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.StringTokenizer;
 
 /**
  * Converter utilities for TestBeans
  */
 public class Converter {
 
     /**
      * Convert the given value object to an object of the given type
      *
      * @param value
+     *            object to convert
      * @param toType
-     * @return Object
+     *            type to convert object to
+     * @return converted object or original value if no conversion could be
+     *         applied
      */
     public static Object convert(Object value, Class<?> toType) {
         if (value == null) {
             value = ""; // TODO should we allow null for non-primitive types?
         } else if (toType.isAssignableFrom(value.getClass())) {
             return value;
         } else if (toType.equals(float.class) || toType.equals(Float.class)) {
             return Float.valueOf(getFloat(value));
         } else if (toType.equals(double.class) || toType.equals(Double.class)) {
             return Double.valueOf(getDouble(value));
         } else if (toType.equals(String.class)) {
             return getString(value);
         } else if (toType.equals(int.class) || toType.equals(Integer.class)) {
             return Integer.valueOf(getInt(value));
         } else if (toType.equals(char.class) || toType.equals(Character.class)) {
             return Character.valueOf(getChar(value));
         } else if (toType.equals(long.class) || toType.equals(Long.class)) {
             return Long.valueOf(getLong(value));
         } else if (toType.equals(boolean.class) || toType.equals(Boolean.class)) {
             return  Boolean.valueOf(getBoolean(value));
         } else if (toType.equals(java.util.Date.class)) {
             return getDate(value);
         } else if (toType.equals(Calendar.class)) {
             return getCalendar(value);
         } else if (toType.equals(File.class)) {
             return getFile(value);
         } else if (toType.equals(Class.class)) {
             try {
                 return Class.forName(value.toString());
             } catch (Exception e) {
                 // don't do anything
             }
         }
         return value;
     }
 
     /**
-     * Converts the given object to a calendar object. Defaults to the current
-     * date if the given object can't be converted.
+     * Converts the given object to a calendar object. Defaults to the
+     * <code>defaultValue</code> if the given object can't be converted.
      *
      * @param date
-     * @return Calendar
+     *            object that should be converted to a {@link Calendar}
+     * @param defaultValue
+     *            default value that will be returned if <code>date</code> can
+     *            not be converted
+     * @return {@link Calendar} representing the given <code>date</code> or
+     *         <code>defaultValue</code> if conversion failed
      */
     public static Calendar getCalendar(Object date, Calendar defaultValue) {
         Calendar cal = new GregorianCalendar();
         if (date instanceof java.util.Date) {
             cal.setTime((java.util.Date) date);
             return cal;
         } else if (date != null) {
             DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
             java.util.Date d = null;
             try {
                 d = formatter.parse(date.toString());
             } catch (ParseException e) {
                 formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
                 try {
                     d = formatter.parse((String) date);
                 } catch (ParseException e1) {
                     formatter = DateFormat.getDateInstance(DateFormat.LONG);
                     try {
                         d = formatter.parse((String) date);
                     } catch (ParseException e2) {
                         formatter = DateFormat.getDateInstance(DateFormat.FULL);
                         try {
                             d = formatter.parse((String) date);
                         } catch (ParseException e3) {
                             return defaultValue;
                         }
                     }
                 }
             }
             cal.setTime(d);
         } else {
             cal = defaultValue;
         }
         return cal;
     }
 
+    /**
+     * Converts the given object to a calendar object. Defaults to a calendar
+     * using the current time if the given object can't be converted.
+     *
+     * @param o
+     *            object that should be converted to a {@link Calendar}
+     * @return {@link Calendar} representing the given <code>o</code> or a new
+     *         {@link GregorianCalendar} using the current time if conversion
+     *         failed
+     */
     public static Calendar getCalendar(Object o) {
         return getCalendar(o, new GregorianCalendar());
     }
 
+    /**
+     * Converts the given object to a {@link Date} object. Defaults to the
+     * current time if the given object can't be converted.
+     *
+     * @param date
+     *            object that should be converted to a {@link Date}
+     * @return {@link Date} representing the given <code>date</code> or
+     *         the current time if conversion failed
+     */
     public static Date getDate(Object date) {
         return getDate(date, Calendar.getInstance().getTime());
     }
 
+    /**
+     * Converts the given object to a {@link Date} object. Defaults to the
+     * <code>defaultValue</code> if the given object can't be converted.
+     *
+     * @param date
+     *            object that should be converted to a {@link Date}
+     * @param defaultValue
+     *            default value that will be returned if <code>date</code> can
+     *            not be converted
+     * @return {@link Date} representing the given <code>date</code> or
+     *         <code>defaultValue</code> if conversion failed
+     */
     public static Date getDate(Object date, Date defaultValue) {
         Date val = null;
         if (date instanceof java.util.Date) {
             return (Date) date;
         } else if (date != null) {
             DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
             // java.util.Date d = null;
             try {
                 val = formatter.parse(date.toString());
             } catch (ParseException e) {
                 formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
                 try {
                     val = formatter.parse((String) date);
                 } catch (ParseException e1) {
                     formatter = DateFormat.getDateInstance(DateFormat.LONG);
                     try {
                         val = formatter.parse((String) date);
                     } catch (ParseException e2) {
                         formatter = DateFormat.getDateInstance(DateFormat.FULL);
                         try {
                             val = formatter.parse((String) date);
                         } catch (ParseException e3) {
                             return defaultValue;
                         }
                     }
                 }
             }
         } else {
             return defaultValue;
         }
         return val;
     }
 
+    /**
+     * Convert object to float, or <code>defaultValue</code> if conversion
+     * failed
+     * 
+     * @param o
+     *            object to convert
+     * @param defaultValue
+     *            default value to use, when conversion failed
+     * @return converted float or <code>defaultValue</code> if conversion
+     *         failed
+     */
     public static float getFloat(Object o, float defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).floatValue();
             }
             return Float.parseFloat(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
+    /**
+     * Convert object to float, or <code>0</code> if conversion
+     * failed
+     * 
+     * @param o
+     *            object to convert
+     * @return converted float or <code>0</code> if conversion
+     *         failed
+     */
     public static float getFloat(Object o) {
         return getFloat(o, 0);
     }
 
+    /**
+     * Convert object to double, or <code>defaultValue</code> if conversion
+     * failed
+     * 
+     * @param o
+     *            object to convert
+     * @param defaultValue
+     *            default value to use, when conversion failed
+     * @return converted double or <code>defaultValue</code> if conversion
+     *         failed
+     */
     public static double getDouble(Object o, double defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).doubleValue();
             }
             return Double.parseDouble(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
+    /**
+     * Convert object to double, or <code>0</code> if conversion
+     * failed
+     * 
+     * @param o
+     *            object to convert
+     * @return converted double or <code>0</code> if conversion
+     *         failed
+     */
     public static double getDouble(Object o) {
         return getDouble(o, 0);
     }
 
+    /**
+     * Convert object to boolean, or <code>false</code> if conversion
+     * failed
+     * 
+     * @param o
+     *            object to convert
+     * @return converted boolean or <code>false</code> if conversion
+     *         failed
+     */
     public static boolean getBoolean(Object o) {
         return getBoolean(o, false);
     }
 
+    /**
+     * Convert object to boolean, or <code>defaultValue</code> if conversion
+     * failed
+     * 
+     * @param o
+     *            object to convert
+     * @param defaultValue
+     *            default value to use, when conversion failed
+     * @return converted boolean or <code>defaultValue</code> if conversion
+     *         failed
+     */
     public static boolean getBoolean(Object o, boolean defaultValue) {
         if (o == null) {
             return defaultValue;
         } else if (o instanceof Boolean) {
             return ((Boolean) o).booleanValue();
         }
         return Boolean.parseBoolean(o.toString());
     }
 
     /**
-     * Convert object to integer, return defaultValue if object is not
-     * convertible or is null.
+     * Convert object to integer, return <code>defaultValue</code> if object is not
+     * convertible or is <code>null</code>.
      *
      * @param o
+     *            object to convert
      * @param defaultValue
-     * @return int
+     *            default value to be used when no conversion can be done
+     * @return converted int or default value if conversion failed
      */
     public static int getInt(Object o, int defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).intValue();
             }
             return Integer.parseInt(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
+    /**
+     * Convert object to char, or ' ' if no conversion can
+     * be applied
+     * 
+     * @param o
+     *            object to convert
+     * @return converted char or ' ' if conversion failed
+     */
     public static char getChar(Object o) {
         return getChar(o, ' ');
     }
 
+    /**
+     * Convert object to char, or <code>defaultValue</code> if no conversion can
+     * be applied
+     * 
+     * @param o
+     *            object to convert
+     * @param defaultValue
+     *            default value to use, when conversion failed
+     * @return converted char or <code>defaultValue</code> if conversion failed
+     */
     public static char getChar(Object o, char defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Character) {
                 return ((Character) o).charValue();
             } else if (o instanceof Byte) {
                 return (char) ((Byte) o).byteValue();
             } else if (o instanceof Integer) {
                 return (char) ((Integer) o).intValue();
             } else {
                 String s = o.toString();
                 if (s.length() > 0) {
                     return o.toString().charAt(0);
                 }
                 return defaultValue;
             }
         } catch (Exception e) {
             return defaultValue;
         }
     }
 
     /**
-     * Converts object to an integer, defaults to 0 if object is not convertible
-     * or is null.
+     * Converts object to an integer, defaults to <code>0</code> if object is
+     * not convertible or is <code>null</code>.
      *
      * @param o
-     * @return int
+     *            object to convert
+     * @return converted int, or <code>0</code> if conversion failed
      */
     public static int getInt(Object o) {
         return getInt(o, 0);
     }
 
     /**
-     * Converts object to a long, return defaultValue if object is not
-     * convertible or is null.
+     * Converts object to a long, return <code>defaultValue</code> if object is
+     * not convertible or is <code>null</code>.
      *
      * @param o
+     *            object to convert
      * @param defaultValue
-     * @return long
+     *            default value to use, when conversion failed
+     * @return converted long or <code>defaultValue</code> when conversion
+     *         failed
      */
     public static long getLong(Object o, long defaultValue) {
         try {
             if (o == null) {
                 return defaultValue;
             }
             if (o instanceof Number) {
                 return ((Number) o).longValue();
             }
             return Long.parseLong(o.toString());
         } catch (NumberFormatException e) {
             return defaultValue;
         }
     }
 
     /**
-     * Converts object to a long, defaults to 0 if object is not convertible or
-     * is null
+     * Converts object to a long, defaults to <code>0</code> if object is not
+     * convertible or is <code>null</code>
      *
      * @param o
-     * @return long
+     *            object to convert
+     * @return converted long or <code>0</code> if conversion failed
      */
     public static long getLong(Object o) {
         return getLong(o, 0);
     }
 
+    /**
+     * Format a date using a given pattern
+     *
+     * @param date
+     *            date to format
+     * @param pattern
+     *            pattern to use for formatting
+     * @return formatted date, or empty string if date was <code>null</code>
+     * @throws IllegalArgumentException
+     *             when <code>pattern</code> is invalid
+     */
     public static String formatDate(Date date, String pattern) {
         if (date == null) {
             return "";
         }
         SimpleDateFormat format = new SimpleDateFormat(pattern);
         return format.format(date);
     }
 
+    /**
+     * Format a date using a given pattern
+     *
+     * @param date
+     *            date to format
+     * @param pattern
+     *            pattern to use for formatting
+     * @return formatted date, or empty string if date was <code>null</code>
+     * @throws IllegalArgumentException
+     *             when <code>pattern</code> is invalid
+     */
     public static String formatDate(java.sql.Date date, String pattern) {
         if (date == null) {
             return "";
         }
         SimpleDateFormat format = new SimpleDateFormat(pattern);
         return format.format(date);
     }
 
+    /**
+     * Format a date using a given pattern
+     *
+     * @param date
+     *            date to format
+     * @param pattern
+     *            pattern to use for formatting
+     * @return formatted date, or empty string if date was <code>null</code>
+     * @throws IllegalArgumentException
+     *             when <code>pattern</code> is invalid
+     */
     public static String formatDate(String date, String pattern) {
         return formatDate(getCalendar(date, null), pattern);
     }
 
+    /**
+     * Format a date using a given pattern
+     *
+     * @param date
+     *            date to format
+     * @param pattern
+     *            pattern to use for formatting
+     * @return formatted date, or empty string if date was <code>null</code>
+     * @throws IllegalArgumentException
+     *             when <code>pattern</code> is invalid
+     */
     public static String formatDate(Calendar date, String pattern) {
         return formatCalendar(date, pattern);
     }
 
+    /**
+     * Format a calendar using a given pattern
+     *
+     * @param date
+     *            calendar to format
+     * @param pattern
+     *            pattern to use for formatting
+     * @return formatted date, or empty string if date was <code>null</code>
+     * @throws IllegalArgumentException
+     *             when <code>pattern</code> is invalid
+     */
     public static String formatCalendar(Calendar date, String pattern) {
         if (date == null) {
             return "";
         }
         SimpleDateFormat format = new SimpleDateFormat(pattern);
         return format.format(date.getTime());
     }
 
     /**
-     * Converts object to a String, return defaultValue if object is null.
+     * Converts object to a String, return <code>defaultValue</code> if object
+     * is <code>null</code>.
      *
      * @param o
+     *            object to convert
      * @param defaultValue
-     * @return String
+     *            default value to use when conversion failed
+     * @return converted String or <code>defaultValue</code> when conversion
+     *         failed
      */
     public static String getString(Object o, String defaultValue) {
         if (o == null) {
             return defaultValue;
         }
         return o.toString();
     }
 
+    /**
+     * Replace newlines "\n" with <code>insertion</code>
+     * 
+     * @param v
+     *            String in which the newlines should be replaced
+     * @param insertion
+     *            new string which should be used instead of "\n"
+     * @return new string with newlines replaced by <code>insertion</code>
+     */
     public static String insertLineBreaks(String v, String insertion) {
         if (v == null) {
             return "";
         }
         StringBuilder replacement = new StringBuilder();
         StringTokenizer tokens = new StringTokenizer(v, "\n", true);
         while (tokens.hasMoreTokens()) {
             String token = tokens.nextToken();
             if (token.compareTo("\n") == 0) {
                 replacement.append(insertion);
             } else {
                 replacement.append(token);
             }
         }
         return replacement.toString();
     }
 
     /**
      * Converts object to a String, defaults to empty string if object is null.
      *
      * @param o
-     * @return String
+     *            object to convert
+     * @return converted String or empty string when conversion failed
      */
     public static String getString(Object o) {
         return getString(o, "");
     }
     
+    /**
+     * Converts an object to a {@link File}
+     * 
+     * @param o
+     *            object to convert (must be a {@link String} or a {@link File})
+     * @return converted file
+     * @throws IllegalArgumentException
+     *             when object can not be converted
+     */
     public static File getFile(Object o){
         if (o instanceof File) {
             return (File) o;
         }
         if (o instanceof String) {
             return new File((String) o);
         }
         throw new IllegalArgumentException("Expected String or file, actual "+o.getClass().getName());
     }
 }
