diff --git a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
index 30bd9f520..aaf18a3b6 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
@@ -1,697 +1,697 @@
 /*
  * Copyright  2000-2005 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.DirectoryScanner;
 import org.apache.tools.ant.Project;
+import org.apache.tools.ant.util.StringUtils;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 
 import java.io.File;
 import java.io.PrintStream;
 import java.io.BufferedOutputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Reader;
 import java.io.BufferedReader;
 import java.io.StringReader;
 import java.io.FileReader;
 import java.io.InputStreamReader;
 import java.io.FileInputStream;
 import java.util.Enumeration;
 import java.util.StringTokenizer;
 import java.util.Vector;
 
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 
 /**
  * Executes a series of SQL statements on a database using JDBC.
  *
  * <p>Statements can
  * either be read in from a text file using the <i>src</i> attribute or from
  * between the enclosing SQL tags.</p>
  *
  * <p>Multiple statements can be provided, separated by semicolons (or the
  * defined <i>delimiter</i>). Individual lines within the statements can be
  * commented using either --, // or REM at the start of the line.</p>
  *
  * <p>The <i>autocommit</i> attribute specifies whether auto-commit should be
  * turned on or off whilst executing the statements. If auto-commit is turned
  * on each statement will be executed and committed. If it is turned off the
  * statements will all be executed as one transaction.</p>
  *
  * <p>The <i>onerror</i> attribute specifies how to proceed when an error occurs
  * during the execution of one of the statements.
  * The possible values are: <b>continue</b> execution, only show the error;
  * <b>stop</b> execution and commit transaction;
  * and <b>abort</b> execution and transaction and fail task.</p>
  *
  * @since Ant 1.2
  *
  * @ant.task name="sql" category="database"
  */
 public class SQLExec extends JDBCTask {
 
     /**
      * delimiters we support, "normal" and "row"
      */
     public static class DelimiterType extends EnumeratedAttribute {
         /** The enumerated strings */
         public static final String NORMAL = "normal", ROW = "row";
         /** @return the enumerated strings */
         public String[] getValues() {
             return new String[] {NORMAL, ROW};
         }
     }
 
 
 
     private int goodSql = 0;
 
     private int totalSql = 0;
 
     /**
      * Database connection
      */
     private Connection conn = null;
 
     /**
      * files to load
      */
     private Vector filesets = new Vector();
 
     /**
      * SQL statement
      */
     private Statement statement = null;
 
     /**
      * SQL input file
      */
     private File srcFile = null;
 
     /**
      * SQL input command
      */
     private String sqlCommand = "";
 
     /**
      * SQL transactions to perform
      */
     private Vector transactions = new Vector();
 
     /**
      * SQL Statement delimiter
      */
     private String delimiter = ";";
 
     /**
      * The delimiter type indicating whether the delimiter will
      * only be recognized on a line by itself
      */
     private String delimiterType = DelimiterType.NORMAL;
 
     /**
      * Print SQL results.
      */
     private boolean print = false;
 
     /**
      * Print header columns.
      */
     private boolean showheaders = true;
 
     /**
      * Results Output file.
      */
     private File output = null;
 
 
     /**
      * Action to perform if an error is found
      **/
     private String onError = "abort";
 
     /**
      * Encoding to use when reading SQL statements from a file
      */
     private String encoding = null;
 
     /**
      * Append to an existing file or overwrite it?
      */
     private boolean append = false;
 
     /**
      * Keep the format of a sql block?
      */
     private boolean keepformat = false;
 
     /**
      * Argument to Statement.setEscapeProcessing
      *
      * @since Ant 1.6
      */
     private boolean escapeProcessing = true;
 
     /**
      * Set the name of the SQL file to be run.
      * Required unless statements are enclosed in the build file
      * @param srcFile the file containing the SQL command.
      */
     public void setSrc(File srcFile) {
         this.srcFile = srcFile;
     }
 
     /**
      * Set an inline SQL command to execute.
      * NB: Properties are not expanded in this text.
      * @param sql a inline string containing the SQL command.
      */
     public void addText(String sql) {
         this.sqlCommand += sql;
     }
 
     /**
      * Adds a set of files (nested fileset attribute).
      * @param set a set of files contains SQL commands, each File is run in
      *            a separate transaction.
      */
     public void addFileset(FileSet set) {
         filesets.addElement(set);
     }
 
 
     /**
      * Add a SQL transaction to execute
      * @return a Transaction to be configured.
      */
     public Transaction createTransaction() {
         Transaction t = new Transaction();
         transactions.addElement(t);
         return t;
     }
 
     /**
      * Set the file encoding to use on the SQL files read in
      *
      * @param encoding the encoding to use on the files
      */
     public void setEncoding(String encoding) {
         this.encoding = encoding;
     }
 
     /**
      * Set the delimiter that separates SQL statements. Defaults to &quot;;&quot;;
      * optional
      *
      * <p>For example, set this to "go" and delimitertype to "ROW" for
      * Sybase ASE or MS SQL Server.</p>
      * @param delimiter the separator.
      */
     public void setDelimiter(String delimiter) {
         this.delimiter = delimiter;
     }
 
     /**
      * Set the delimiter type: "normal" or "row" (default "normal").
      *
      * <p>The delimiter type takes two values - normal and row. Normal
      * means that any occurrence of the delimiter terminate the SQL
      * command whereas with row, only a line containing just the
      * delimiter is recognized as the end of the command.</p>
      * @param delimiterType the type of delimiter - "normal" or "row".
      */
     public void setDelimiterType(DelimiterType delimiterType) {
         this.delimiterType = delimiterType.getValue();
     }
 
     /**
      * Print result sets from the statements;
      * optional, default false
      * @param print if true print result sets.
      */
     public void setPrint(boolean print) {
         this.print = print;
     }
 
     /**
      * Print headers for result sets from the
      * statements; optional, default true.
      * @param showheaders if true print headers of result sets.
      */
     public void setShowheaders(boolean showheaders) {
         this.showheaders = showheaders;
     }
 
     /**
      * Set the output file;
      * optional, defaults to the Ant log.
      * @param output the output file to use for logging messages.
      */
     public void setOutput(File output) {
         this.output = output;
     }
 
     /**
      * whether output should be appended to or overwrite
      * an existing file.  Defaults to false.
      *
      * @since Ant 1.5
      * @param append if true append to an existing file.
      */
     public void setAppend(boolean append) {
         this.append = append;
     }
 
 
     /**
      * Action to perform when statement fails: continue, stop, or abort
      * optional; default &quot;abort&quot;
      * @param action the action to perform on statement failure.
      */
     public void setOnerror(OnError action) {
         this.onError = action.getValue();
     }
 
     /**
      * whether or not format should be preserved.
      * Defaults to false.
      *
      * @param keepformat The keepformat to set
      */
     public void setKeepformat(boolean keepformat) {
         this.keepformat = keepformat;
     }
 
     /**
      * Set escape processing for statements.
      * @param enable if true enable escape processing, default is true.
      * @since Ant 1.6
      */
     public void setEscapeProcessing(boolean enable) {
         escapeProcessing = enable;
     }
 
     /**
      * Load the sql file and then execute it
      * @throws BuildException on error.
      */
     public void execute() throws BuildException {
         Vector savedTransaction = (Vector) transactions.clone();
         String savedSqlCommand = sqlCommand;
 
         sqlCommand = sqlCommand.trim();
 
         try {
             if (srcFile == null && sqlCommand.length() == 0
                 && filesets.isEmpty()) {
                 if (transactions.size() == 0) {
                     throw new BuildException("Source file or fileset, "
                                              + "transactions or sql statement "
                                              + "must be set!", getLocation());
                 }
             }
 
             if (srcFile != null && !srcFile.exists()) {
                 throw new BuildException("Source file does not exist!", getLocation());
             }
 
             // deal with the filesets
             for (int i = 0; i < filesets.size(); i++) {
                 FileSet fs = (FileSet) filesets.elementAt(i);
                 DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                 File srcDir = fs.getDir(getProject());
 
                 String[] srcFiles = ds.getIncludedFiles();
 
                 // Make a transaction for each file
                 for (int j = 0; j < srcFiles.length; j++) {
                     Transaction t = createTransaction();
                     t.setSrc(new File(srcDir, srcFiles[j]));
                 }
             }
 
             // Make a transaction group for the outer command
             Transaction t = createTransaction();
             t.setSrc(srcFile);
             t.addText(sqlCommand);
             conn = getConnection();
             if (!isValidRdbms(conn)) {
                 return;
             }
             try {
                 statement = conn.createStatement();
                 statement.setEscapeProcessing(escapeProcessing);
 
                 PrintStream out = System.out;
                 try {
                     if (output != null) {
                         log("Opening PrintStream to output file " + output,
                             Project.MSG_VERBOSE);
                         out = new PrintStream(
                                   new BufferedOutputStream(
                                       new FileOutputStream(output
                                                            .getAbsolutePath(),
                                                            append)));
                     }
 
                     // Process all transactions
                     for (Enumeration e = transactions.elements();
                          e.hasMoreElements();) {
 
                         ((Transaction) e.nextElement()).runTransaction(out);
                         if (!isAutocommit()) {
                             log("Committing transaction", Project.MSG_VERBOSE);
                             conn.commit();
                         }
                     }
                 } finally {
                     if (out != null && out != System.out) {
                         out.close();
                     }
                 }
             } catch (IOException e) {
                 if (!isAutocommit() && conn != null && onError.equals("abort")) {
                     try {
                         conn.rollback();
                     } catch (SQLException ex) {
                         // ignore
                     }
                 }
                 throw new BuildException(e, getLocation());
             } catch (SQLException e) {
                 if (!isAutocommit() && conn != null && onError.equals("abort")) {
                     try {
                         conn.rollback();
                     } catch (SQLException ex) {
                         // ignore
                     }
                 }
                 throw new BuildException(e, getLocation());
             } finally {
                 try {
                     if (statement != null) {
                         statement.close();
                     }
                     if (conn != null) {
                         conn.close();
                     }
                 } catch (SQLException ex) {
                     // ignore
                 }
             }
 
             log(goodSql + " of " + totalSql
                 + " SQL statements executed successfully");
         } finally {
             transactions = savedTransaction;
             sqlCommand = savedSqlCommand;
         }
     }
 
     /**
      * read in lines and execute them
      * @param reader the reader contains sql lines.
      * @param out the place to output results.
      * @throws SQLException on sql problems
      * @throws IOException on io problems
      */
     protected void runStatements(Reader reader, PrintStream out)
         throws SQLException, IOException {
         StringBuffer sql = new StringBuffer();
-        String line = "";
+        String line;
 
         BufferedReader in = new BufferedReader(reader);
 
         while ((line = in.readLine()) != null) {
             if (!keepformat) {
                 line = line.trim();
             }
             line = getProject().replaceProperties(line);
             if (!keepformat) {
                 if (line.startsWith("//")) {
                     continue;
                 }
                 if (line.startsWith("--")) {
                     continue;
                 }
                 StringTokenizer st = new StringTokenizer(line);
                 if (st.hasMoreTokens()) {
                     String token = st.nextToken();
                     if ("REM".equalsIgnoreCase(token)) {
                         continue;
                     }
                 }
             }
 
             if (!keepformat) {
                 sql.append(" " + line);
             } else {
                 sql.append("\n" + line);
             }
 
             // SQL defines "--" as a comment to EOL
             // and in Oracle it may contain a hint
             // so we cannot just remove it, instead we must end it
             if (!keepformat) {
                 if (line.indexOf("--") >= 0) {
                     sql.append("\n");
                 }
             }
             if ((delimiterType.equals(DelimiterType.NORMAL)
-                 && sql.toString().endsWith(delimiter))
+                 && StringUtils.endsWith(sql, delimiter))
                 ||
                 (delimiterType.equals(DelimiterType.ROW)
                  && line.equals(delimiter))) {
                 execSQL(sql.substring(0, sql.length() - delimiter.length()),
                         out);
                 sql.replace(0, sql.length(), "");
             }
         }
         // Catch any statements not followed by ;
         if (!sql.equals("")) {
             execSQL(sql.toString(), out);
         }
     }
 
 
     /**
      * Exec the sql statement.
      * @param sql the SQL statement to execute
      * @param out the place to put output
      * @throws SQLException on SQL problems
      */
     protected void execSQL(String sql, PrintStream out) throws SQLException {
         // Check and ignore empty statements
         if ("".equals(sql.trim())) {
             return;
         }
 
         ResultSet resultSet = null;
         try {
             totalSql++;
             log("SQL: " + sql, Project.MSG_VERBOSE);
 
             boolean ret;
             int updateCount = 0, updateCountTotal = 0;
 
             ret = statement.execute(sql);
             updateCount = statement.getUpdateCount();
             resultSet = statement.getResultSet();
             do {
                 if (!ret) {
                     if (updateCount != -1) {
                         updateCountTotal += updateCount;
                     }
                 } else {
                     if (print) {
                         printResults(resultSet, out);
                     }
                 }
                 ret = statement.getMoreResults();
                 if (ret) {
                     updateCount = statement.getUpdateCount();
                     resultSet = statement.getResultSet();
                 }
             } while (ret);
 
             log(updateCountTotal + " rows affected",
                 Project.MSG_VERBOSE);
 
             if (print) {
                 StringBuffer line = new StringBuffer();
                 line.append(updateCountTotal + " rows affected");
                 out.println(line);
             }
 
             SQLWarning warning = conn.getWarnings();
             while (warning != null) {
                 log(warning + " sql warning", Project.MSG_VERBOSE);
                 warning = warning.getNextWarning();
             }
             conn.clearWarnings();
             goodSql++;
         } catch (SQLException e) {
             log("Failed to execute: " + sql, Project.MSG_ERR);
             if (!onError.equals("continue")) {
                 throw e;
             }
             log(e.toString(), Project.MSG_ERR);
         } finally {
             if (resultSet != null) {
                 resultSet.close();
             }
         }
     }
 
     /**
      * print any results in the statement
      * @deprecated use {@link #printResults(java.sql.ResultSet, java.io.PrintStream)
      *             the two arg version} instead.
      * @param out the place to print results
      * @throws SQLException on SQL problems.
      */
     protected void printResults(PrintStream out) throws SQLException {
-        ResultSet rs = null;
-        rs = statement.getResultSet();
+        ResultSet rs = statement.getResultSet();
         try {
             printResults(rs, out);
         } finally {
             if (rs != null) {
                 rs.close();
             }
         }
     }
 
     /**
      * print any results in the result set.
      * @param rs the resultset to print information about
      * @param out the place to print results
      * @throws SQLException on SQL problems.
      * @since Ant 1.6.3
      */
     protected void printResults(ResultSet rs, PrintStream out)
         throws SQLException {
         if (rs != null) {
             log("Processing new result set.", Project.MSG_VERBOSE);
             ResultSetMetaData md = rs.getMetaData();
             int columnCount = md.getColumnCount();
             StringBuffer line = new StringBuffer();
             if (showheaders) {
                 for (int col = 1; col < columnCount; col++) {
                      line.append(md.getColumnName(col));
                      line.append(",");
                 }
                 line.append(md.getColumnName(columnCount));
                 out.println(line);
                 line = new StringBuffer();
             }
             while (rs.next()) {
                 boolean first = true;
                 for (int col = 1; col <= columnCount; col++) {
                     String columnValue = rs.getString(col);
                     if (columnValue != null) {
                         columnValue = columnValue.trim();
                     }
 
                     if (first) {
                         first = false;
                     } else {
                         line.append(",");
                     }
                     line.append(columnValue);
                 }
                 out.println(line);
                 line = new StringBuffer();
             }
         }
         out.println();
     }
 
     /**
      * The action a task should perform on an error,
      * one of "continue", "stop" and "abort"
      */
     public static class OnError extends EnumeratedAttribute {
         /** @return the enumerated values */
         public String[] getValues() {
             return new String[] {"continue", "stop", "abort"};
         }
     }
 
     /**
      * Contains the definition of a new transaction element.
      * Transactions allow several files or blocks of statements
      * to be executed using the same JDBC connection and commit
      * operation in between.
      */
     public class Transaction {
         private File tSrcFile = null;
         private String tSqlCommand = "";
 
         /**
          * Set the source file attribute.
          * @param src the source file
          */
         public void setSrc(File src) {
             this.tSrcFile = src;
         }
 
         /**
          * Set inline text
          * @param sql the inline text
          */
         public void addText(String sql) {
             this.tSqlCommand += sql;
         }
 
         /**
          *
          */
         private void runTransaction(PrintStream out)
             throws IOException, SQLException {
             if (tSqlCommand.length() != 0) {
                 log("Executing commands", Project.MSG_INFO);
                 runStatements(new StringReader(tSqlCommand), out);
             }
 
             if (tSrcFile != null) {
                 log("Executing file: " + tSrcFile.getAbsolutePath(),
                     Project.MSG_INFO);
                 Reader reader =
                     (encoding == null) ? new FileReader(tSrcFile)
                                        : new InputStreamReader(
                                              new FileInputStream(tSrcFile),
                                              encoding);
                 try {
                     runStatements(reader, out);
                 } finally {
                     reader.close();
                 }
             }
         }
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/util/StringUtils.java b/src/main/org/apache/tools/ant/util/StringUtils.java
index 40acacb24..5141c64b2 100644
--- a/src/main/org/apache/tools/ant/util/StringUtils.java
+++ b/src/main/org/apache/tools/ant/util/StringUtils.java
@@ -1,101 +1,132 @@
 /*
  * Copyright  2001-2002,2004-2005 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 package org.apache.tools.ant.util;
 
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.util.Vector;
 
 /**
  * A set of helper methods related to string manipulation.
  *
  */
 public final class StringUtils {
 
     /**
      * constructor to stop anyone instantiating the class
      */
     private StringUtils() {
     }
 
     /** the line separator for this OS */
     public static final String LINE_SEP = System.getProperty("line.separator");
 
     /**
      * Splits up a string into a list of lines. It is equivalent
      * to <tt>split(data, '\n')</tt>.
      * @param data the string to split up into lines.
      * @return the list of lines available in the string.
      */
     public static Vector lineSplit(String data) {
         return split(data, '\n');
     }
 
     /**
      * Splits up a string where elements are separated by a specific
      * character and return all elements.
      * @param data the string to split up.
      * @param ch the separator character.
      * @return the list of elements.
      */
     public static Vector split(String data, int ch) {
         Vector elems = new Vector();
         int pos = -1;
         int i = 0;
         while ((pos = data.indexOf(ch, i)) != -1) {
             String elem = data.substring(i, pos);
             elems.addElement(elem);
             i = pos + 1;
         }
         elems.addElement(data.substring(i));
         return elems;
     }
 
     /**
      * Replace occurrences into a string.
      * @param data the string to replace occurrences into
      * @param from the occurrence to replace.
      * @param to the occurrence to be used as a replacement.
      * @return the new string with replaced occurrences.
      */
     public static String replace(String data, String from, String to) {
         StringBuffer buf = new StringBuffer(data.length());
         int pos = -1;
         int i = 0;
         while ((pos = data.indexOf(from, i)) != -1) {
             buf.append(data.substring(i, pos)).append(to);
             i = pos + from.length();
         }
         buf.append(data.substring(i));
         return buf.toString();
     }
 
     /**
      * Convenient method to retrieve the full stacktrace from a given exception.
      * @param t the exception to get the stacktrace from.
      * @return the stacktrace from the given exception.
      */
     public static String getStackTrace(Throwable t) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw, true);
         t.printStackTrace(pw);
         pw.flush();
         pw.close();
         return sw.toString();
     }
 
+    /**
+     * Checks that a string buffer ends up with a given string. It may sound trivial with the existing
+     * JDK API but the various implementation among JDKs can make those methods extremely resource intensive
+     * and perform poorly due to massive memory allocation and copying. See
+     * @param buffer the buffer to perform the check on
+     * @param suffix the suffix
+     * @return  <code>true</code> if the character sequence represented by the
+     *          argument is a suffix of the character sequence represented by
+     *          the StringBuffer object; <code>false</code> otherwise. Note that the
+     *          result will be <code>true</code> if the argument is the
+     *          empty string.
+     */
+    public static boolean endsWith(StringBuffer buffer, String suffix) {
+        if (suffix.length() > buffer.length()) {
+            return false;
+        }
+        // this loop is done on purpose to avoid memory allocation performance problems on various JDKs
+        // StringBuffer.lastIndexOf() was introduced in jdk 1.4 and implementation is ok though does allocation/copying
+        // StringBuffer.toString().endsWith() does massive memory allocation/copying on JDK 1.5
+        // See http://issues.apache.org/bugzilla/show_bug.cgi?id=37169
+        int endIndex = suffix.length() - 1;
+        int bufferIndex = buffer.length() - 1;
+        while ( endIndex >= 0 ) {
+            if ( buffer.charAt(bufferIndex) != suffix.charAt(endIndex) ) {
+                return false;
+            }
+            bufferIndex--;
+            endIndex--;
+        }
+        return true;
+    }
 }
diff --git a/src/testcases/org/apache/tools/ant/util/StringUtilsTest.java b/src/testcases/org/apache/tools/ant/util/StringUtilsTest.java
index 16c3c0e8c..47ea2a86c 100644
--- a/src/testcases/org/apache/tools/ant/util/StringUtilsTest.java
+++ b/src/testcases/org/apache/tools/ant/util/StringUtilsTest.java
@@ -1,58 +1,105 @@
 /*
  * Copyright  2001,2004 The Apache Software Foundation
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 package org.apache.tools.ant.util;
 
 import java.util.Vector;
 
 import junit.framework.TestCase;
 
 /**
  * Test for StringUtils
  */
 public class StringUtilsTest extends TestCase {
     public StringUtilsTest(String s) {
         super(s);
     }
 
     public void testSplit(){
         final String data = "a,b,,";
         Vector res = StringUtils.split(data, ',');
         assertEquals(4, res.size());
         assertEquals("a", res.elementAt(0));
         assertEquals("b", res.elementAt(1));
         assertEquals("", res.elementAt(2));
         assertEquals("", res.elementAt(3));
     }
 
     public void testSplitLines(){
         final String data = "a\r\nb\nc\nd\ne";
         Vector res = StringUtils.lineSplit(data);
         assertEquals(5, res.size());
         assertEquals("a\r", res.elementAt(0));
         assertEquals("b", res.elementAt(1));
         assertEquals("c", res.elementAt(2));
         assertEquals("d", res.elementAt(3));
         assertEquals("e", res.elementAt(4));
     }
 
     public void testReplace() {
         final String data = "abcabcabca";
         String res = StringUtils.replace(data, "a", "");
         assertEquals("bcbcbc", res);
     }
 
+    public void testEndsWithBothEmpty() {
+        assertTrue( StringUtils.endsWith( new StringBuffer(), "") );
+    }
+
+    public void testEndsWithEmptyString() {
+        assertTrue( StringUtils.endsWith( new StringBuffer("12234545"), "") );
+    }
+
+    public void testEndsWithShorterString() {
+        assertTrue( StringUtils.endsWith( new StringBuffer("12345678"), "78"));
+    }
+
+    public void testEndsWithSameString() {
+        assertTrue( StringUtils.endsWith( new StringBuffer("123"), "123"));
+    }
+
+    public void testEndsWithLongerString() {
+        assertFalse( StringUtils.endsWith( new StringBuffer("12"), "1245"));
+    }
+
+    public void testEndsWithNoMatch() {
+        assertFalse( StringUtils.endsWith( new StringBuffer("12345678"), "789"));
+    }
+
+    public void testEndsWithEmptyBuffer() {
+        assertFalse( StringUtils.endsWith( new StringBuffer(""), "12345667") );
+    }
+
+    public void testEndsWithJDKPerf() {
+        StringBuffer buf = getFilledBuffer(1024*300, 'a');
+        for (int i = 0; i < 1000; i++) {
+            assertTrue(buf.toString().endsWith("aa"));
+        }
+    }
+
+    public void testEndsWithPerf() {
+        StringBuffer buf = getFilledBuffer(1024*300, 'a');
+        for (int i = 0; i < 1000; i++) {
+            assertTrue(StringUtils.endsWith(buf, "aa"));
+        }
+    }
+
+    private StringBuffer getFilledBuffer(int size, char ch) {
+        StringBuffer buf = new StringBuffer(size);
+        for (int i = 0; i < size; i++) { buf.append(ch); };
+        return buf;
+    }
 }
