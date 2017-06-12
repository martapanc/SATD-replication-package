diff --git a/docs/manual/CoreTasks/sql.html b/docs/manual/CoreTasks/sql.html
index db9b8ff05..6a78de5e4 100644
--- a/docs/manual/CoreTasks/sql.html
+++ b/docs/manual/CoreTasks/sql.html
@@ -1,394 +1,404 @@
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
 <html>
 <head>
 <meta http-equiv="Content-Language" content="en-us">
 <link rel="stylesheet" type="text/css" href="../stylesheets/style.css">
 <title>SQL Task</title>
 </head>
 <body>
 
 <h2><a name="sql">Sql</a></h2>
 <h3>Description</h3>
 <p>Executes a series of SQL statements via JDBC to a database. Statements can 
 either be read in from a text file using the <i>src</i> attribute or from 
 between the enclosing SQL tags.</p>
 
 <p>Multiple statements can be provided, separated by semicolons (or the 
 defined <i>delimiter</i>). Individual lines within the statements can be 
 commented using either --, // or REM at the start of the line.</p>
 
 <p>The <i>autocommit</i> attribute specifies whether auto-commit should be 
 turned on or off whilst executing the statements. If auto-commit is turned 
 on each statement will be executed and committed. If it is turned off the 
 statements will all be executed as one transaction.</p>
 
 <p>The <i>onerror</i> attribute specifies how to proceed when an error occurs 
 during the execution of one of the statements. 
 The possible values are: <b>continue</b> execution, only show the error;
 <b>stop</b> execution, log the error but don't fail the task
 and <b>abort</b> execution and transaction and fail task.</p>
 
 <p>
 <b>Proxies</b>. Some JDBC drivers (including the Oracle thin driver), 
     use the JVM's proxy settings to route their JDBC operations to the database.
     Since Ant1.7, Ant running on Java1.5 or later defaults to 
     <a href="../proxy.html">using
     the proxy settings of the operating system</a>. 
     Accordingly, the OS proxy settings need to be valid, or Ant's proxy
     support disabled with <code>-noproxy</code> option.
 </p>
 
 <h3>Parameters</h3>
 <table border="1" cellpadding="2" cellspacing="0">
 <tr>
   <td width="12%" valign="top"><b>Attribute</b></td>
   <td width="78%" valign="top"><b>Description</b></td>
   <td width="10%" valign="top"><b>Required</b></td>
 </tr>
 <tr>
   <td width="12%" valign="top">driver</td>
   <td width="78%" valign="top">Class name of the jdbc driver</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">url</td>
   <td width="78%" valign="top">Database connection url</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">userid</td>
   <td width="78%" valign="top">Database user name</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">password</td>
   <td width="78%" valign="top">Database password</td>
   <td width="10%" valign="top">Yes</td>
 </tr>
 <tr>
   <td width="12%" valign="top">src</td>
   <td width="78%" valign="top">File containing SQL statements</td>
   <td width="10%" valign="top">Yes, unless statements enclosed within tags</td>
 </tr>
 <tr>
   <td valign="top">encoding</td>
   <td valign="top">The encoding of the files containing SQL statements</td>
   <td align="center">No - defaults to default JVM encoding</td>
 </tr>
 <tr>
   <td width="12%" valign="top">delimiter</td>
   <td width="78%" valign="top">String that separates SQL statements</td>
   <td width="10%" valign="top">No, default &quot;;&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">autocommit</td>
   <td width="78%" valign="top">Auto commit flag for database connection (default false)</td>
   <td width="10%" valign="top">No, default &quot;false&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">print</td>
   <td width="78%" valign="top">Print result sets from the statements (default false)</td>
   <td width="10%" valign="top">No, default &quot;false&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">showheaders</td>
   <td width="78%" valign="top">Print headers for result sets from the statements (default true)</td>
   <td width="10%" valign="top">No, default &quot;true&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">showtrailers</td>
   <td width="78%" valign="top">Print trailer for number of rows affected (default true)</td>
   <td width="10%" valign="top">No, default &quot;true&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">output</td>
   <td width="78%" valign="top">Output file for result sets (defaults to System.out)</td>
   <td width="10%" valign="top">No (print to System.out by default)</td>
 </tr>
   <tr>
     <td valign="top">append</td>
     <td valign="top">whether output should be appended to or overwrite
     an existing file.  Defaults to false.</td>
     <td align="center" valign="top">No</td>
   </tr>
 <tr>
   <td width="12%" valign="top">classpath</td>
   <td width="78%" valign="top">Classpath used to load driver</td>
   <td width="10%" valign="top">No (use system classpath)</td>
 </tr>
   <tr>
     <td width="12%" valign="top">classpathref</td>
     <td width="78%" valign="top">The classpath to use, given as a <a href="../using.html#references">reference</a> to a path defined elsewhere.</td>
   <td width="10%" valign="top">No (use system classpath)</td>
   </tr>
 <tr>
   <td width="12%" valign="top">onerror</td>
   <td width="78%" valign="top">Action to perform when statement fails: continue, stop, abort</td>
   <td width="10%" valign="top">No, default &quot;abort&quot;</td>
 </tr>
 <tr>
   <td width="12%" valign="top">rdbms</td>
   <td width="78%" valign="top">Execute task only if this rdbms</td>
   <td width="10%" valign="top">No (no restriction)</td>
 </tr>
 <tr>
   <td width="12%" valign="top">version</td>
   <td width="78%" valign="top">Execute task only if rdbms version match</td>
   <td width="10%" valign="top">No (no restriction)</td>
 </tr>
 <tr>
   <td width="12%" valign="top">caching</td>
   <td width="78%" valign="top">Should the task cache loaders and the driver?</td>
   <td width="10%" valign="top">No (default=true)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">delimitertype</td>
   <td width="78%" valign="top">Control whether the delimiter will only be recognized on a line by itself.<br>
     Can be "normal" -anywhere on the line, or "row", meaning it must be on a line by itself</td>
   <td width="10%" valign="top">No (default:normal)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">keepformat</td>
   <td width="78%" valign="top">Control whether the format of the sql will be preserved.<br>
     Useful when loading packages and procedures.
   <td width="10%" valign="top">No (default=false)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">escapeprocessing</td>
   <td width="78%" valign="top">Control whether the Java statement
     object will perform escape substitution.<br>
     See <a
     href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/Statement.html#setEscapeProcessing(boolean)">Statement's
     API docs</a> for details.  <em>Since Ant 1.6</em>.
   <td width="10%" valign="top">No (default=true)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">expandproperties</td>
   <td width="78%" valign="top">Set to true to turn on property expansion in
   nested SQL, inline in the task or nested transactions. <em>Since Ant 1.7</em>.
   <td width="10%" valign="top">No (default=true)</td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">rawblobs</td>
   <td width="78%" valign="top">If true, will write raw streams rather than hex encoding when
     printing BLOB results. <em>Since Ant 1.7.1</em>.</td>
   <td width="10%" valign="top">No, default <em>false</em></td>
 </tr>
 
 <tr>
   <td width="12%" valign="top">failOnConnectionError</td>
   <td width="78%" valign="top">If false, will only print a warning
     message and not execute any statement if the task fails to connect
     to the database.  <em>Since Ant 1.8.0</em>.</td>
   <td width="10%" valign="top">No, default <em>true</em></td>
 </tr>
 </table>
 
+<tr>
+  <td width="12%" valign="top">strictDelimiterMatching</td>
+  <td width="78%" valign="top">If false, delimiters will be searched
+    for in a case-insesitive manner (i.e. delimer="go" matches "GO")
+    and surrounding whitespace will be ignored (delimter="go" matches
+    "GO ").  <em>Since Ant 1.8.0</em>.</td>
+  <td width="10%" valign="top">No, default <em>true</em></td>
+</tr>
+</table>
+
 <h3>Parameters specified as nested elements</h3>
 <h4>transaction</h4>
 <p>Use nested <code>&lt;transaction&gt;</code> 
 elements to specify multiple blocks of commands to the executed
 executed in the same connection but different transactions. This
 is particularly useful when there are multiple files to execute
 on the same schema.</p>
 <table border="1" cellpadding="2" cellspacing="0">
   <tr>
     <td valign="top"><b>Attribute</b></td>
     <td valign="top"><b>Description</b></td>
     <td align="center" valign="top"><b>Required</b></td>
   </tr>
   <tr>
     <td valign="top">src</td>
     <td valign="top">File containing SQL statements</td>
     <td valign="top" align="center">Yes, unless statements enclosed within tags</td>
   </tr>
 </table>
 <p>The <code>&lt;transaction&gt;</code> element supports any <a
 href="../CoreTypes/resources.html">resource</a> or single element
 resource collection as nested element to specify the resource
 containing the SQL statements.</p>
 
 <h4>any <a href="../CoreTypes/resources.html">resource</a> or resource
 collection</h4>
 
 <p>You can specify multiple sources via nested resource collection
 elements.  Each resource of the collection will be run in a
 transaction of its own.  Prior to Ant 1.7 only filesets were
 supported.  Use a sort resource collection to get a predictable order
 of transactions. </p>
 
 <h4>classpath</h4>
 <p><code>Sql</code>'s <em>classpath</em> attribute is a <a
 href="../using.html#path">PATH like structure</a> and can also be set via a nested
 <em>classpath</em> element. It is used to load the JDBC classes.</p>
 
 <h3>Examples</h3>
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     src=&quot;data.sql&quot;
 /&gt;
 </pre></blockquote>
 
 <p>Connects to the database given in <i>url</i> as the sa user using the 
 org.database.jdbcDriver and executes the SQL statements contained within 
 the file data.sql</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     &gt;
 insert
 into table some_table
 values(1,2,3,4);
 
 truncate table some_other_table;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>Connects to the database given in <i>url</i> as the sa
  user using the org.database.jdbcDriver and executes the two SQL statements 
  inserting data into some_table and truncating some_other_table. Ant Properties
  in the nested text will not be expanded.</p>
 
 <p>Note that you may want to enclose your statements in
 <code>&lt;![CDATA[</code> ... <code>]]&gt;</code> sections so you don't
 need to escape <code>&lt;</code>, <code>&gt;</code> <code>&amp;</code>
 or other special characters. For example:</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     &gt;&lt;![CDATA[
 
 update some_table set column1 = column1 + 1 where column2 &lt; 42;
 
 ]]&gt;&lt;/sql&gt;
 </pre></blockquote>
 
 The following command turns property expansion in nested text on (it is off purely for backwards
 compatibility), then creates a new user in the HSQLDB database using Ant properties. 
 
 <blockquote><pre>&lt;sql
     driver="org.hsqldb.jdbcDriver";
     url="jdbc:hsqldb:file:${database.dir}"
     userid="sa"
     password=""
     expandProperties="true"
     &gt;
   &lt;transaction&gt;
     CREATE USER ${newuser} PASSWORD ${newpassword}
   &lt;/transaction&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 
 <p>The following connects to the database given in url as the sa user using 
 the org.database.jdbcDriver and executes the SQL statements contained within 
 the files data1.sql, data2.sql and data3.sql and then executes the truncate 
 operation on <i>some_other_table</i>.</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot; &gt;
   &lt;transaction  src=&quot;data1.sql&quot;/&gt;
   &lt;transaction  src=&quot;data2.sql&quot;/&gt;
   &lt;transaction  src=&quot;data3.sql&quot;/&gt;
   &lt;transaction&gt;
     truncate table some_other_table;
   &lt;/transaction&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>The following example does the same as (and may execute additional
 SQL files if there are more files matching the pattern
 <code>data*.sql</code>) but doesn't guarantee that data1.sql will be
 run before <code>data2.sql</code>.</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;&gt;
   &lt;path&gt;
     &lt;fileset dir=&quot;.&quot;&gt;
       &lt;include name=&quot;data*.sql&quot;/&gt;
     &lt;/fileset&gt;
   &lt;path&gt;
   &lt;transaction&gt;
     truncate table some_other_table;
   &lt;/transaction&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>The following connects to the database given in url as the sa user using the 
 org.database.jdbcDriver and executes the SQL statements contained within the 
 file data.sql, with output piped to outputfile.txt, searching /some/jdbc.jar 
 as well as the system classpath for the driver class.</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     src=&quot;data.sql&quot;
     print=&quot;yes&quot;
     output=&quot;outputfile.txt&quot;
     &gt;
 &lt;classpath&gt;
 	&lt;pathelement location=&quot;/some/jdbc.jar&quot;/&gt;
 &lt;/classpath&gt;
 &lt;/sql&gt;
 </pre></blockquote>
 
 <p>The following will only execute if the RDBMS is &quot;oracle&quot; and the version 
 starts with &quot;8.1.&quot;</p>
 
 <blockquote><pre>&lt;sql
     driver=&quot;org.database.jdbcDriver&quot;
     url=&quot;jdbc:database-url&quot;
     userid=&quot;sa&quot;
     password=&quot;pass&quot;
     src=&quot;data.sql&quot;
     rdbms=&quot;oracle&quot;
     version=&quot;8.1.&quot;
     &gt;
 insert
 into table some_table
 values(1,2,3,4);
 
 truncate table some_other_table;
 &lt;/sql&gt;
 </pre></blockquote>
 
 
 </body>
 </html>
diff --git a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
index cb0f0130f..73b09ad68 100644
--- a/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
+++ b/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
@@ -1,836 +1,894 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.StringUtils;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.FileSet;
 import org.apache.tools.ant.types.Resource;
 import org.apache.tools.ant.types.ResourceCollection;
 import org.apache.tools.ant.types.resources.FileResource;
 import org.apache.tools.ant.types.resources.Union;
 
 import java.io.File;
 import java.io.PrintStream;
 import java.io.BufferedOutputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Reader;
 import java.io.BufferedReader;
 import java.io.StringReader;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.util.Enumeration;
 import java.util.Iterator;
+import java.util.Locale;
 import java.util.StringTokenizer;
 import java.util.Vector;
 
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.Types;
 
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
     private Union resources;
 
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
      * Print SQL stats (rows affected)
      */
     private boolean showtrailers = true;
 
     /**
      * Results Output file.
      */
     private File output = null;
 
 
     /**
      * Action to perform if an error is found
      */
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
      * should properties be expanded in text?
      * false for backwards compatibility
      *
      * @since Ant 1.7
      */
     private boolean expandProperties = true;
 
     /**
      * should we print raw BLOB data?
      * @since Ant 1.7.1
      */
     private boolean rawBlobs;
 
     /**
+     * delimers must match in case and whitespace is significant.
+     * @since Ant 1.8.0
+     */
+    private boolean strictDelimiterMatching = true;
+
+    /**
      * Set the name of the SQL file to be run.
      * Required unless statements are enclosed in the build file
      * @param srcFile the file containing the SQL command.
      */
     public void setSrc(File srcFile) {
         this.srcFile = srcFile;
     }
 
     /**
      * Enable property expansion inside nested text
      *
      * @param expandProperties if true expand properties.
      * @since Ant 1.7
      */
     public void setExpandProperties(boolean expandProperties) {
         this.expandProperties = expandProperties;
     }
 
     /**
      * is property expansion inside inline text enabled?
      *
      * @return true if properties are to be expanded.
      * @since Ant 1.7
      */
     public boolean getExpandProperties() {
         return expandProperties;
     }
 
     /**
      * Set an inline SQL command to execute.
      * NB: Properties are not expanded in this text unless {@link #expandProperties}
      * is set.
      * @param sql an inline string containing the SQL command.
      */
     public void addText(String sql) {
         //there is no need to expand properties here as that happens when Transaction.addText is
         //called; to do so here would be an error.
         this.sqlCommand += sql;
     }
 
     /**
      * Adds a set of files (nested fileset attribute).
      * @param set a set of files contains SQL commands, each File is run in
      *            a separate transaction.
      */
     public void addFileset(FileSet set) {
         add(set);
     }
 
     /**
      * Adds a collection of resources (nested element).
      * @param rc a collection of resources containing SQL commands,
      * each resource is run in a separate transaction.
      * @since Ant 1.7
      */
     public void add(ResourceCollection rc) {
         if (rc == null) {
             throw new BuildException("Cannot add null ResourceCollection");
         }
         synchronized (this) {
             if (resources == null) {
                 resources = new Union();
             }
         }
         resources.add(rc);
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
      * Print trailing info (rows affected) for the SQL
      * Addresses Bug/Request #27446
      * @param showtrailers if true prints the SQL rows affected
      * @since Ant 1.7
      */
     public void setShowtrailers(boolean showtrailers) {
         this.showtrailers = showtrailers;
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
      * Set whether to print raw BLOBs rather than their string (hex) representations.
      * @param rawBlobs whether to print raw BLOBs.
      * @since Ant 1.7.1
      */
     public void setRawBlobs(boolean rawBlobs) {
         this.rawBlobs = rawBlobs;
     }
 
     /**
+     * If false, delimiters will be searched for in a case-insesitive
+     * manner (i.e. delimer="go" matches "GO") and surrounding
+     * whitespace will be ignored (delimter="go" matches "GO ").
+     * @since Ant 1.8.0
+     */
+    public void setStrictDelimiterMatching(boolean b) {
+        strictDelimiterMatching = b;
+    }
+
+    /**
      * Load the sql file and then execute it
      * @throws BuildException on error.
      */
     public void execute() throws BuildException {
         Vector savedTransaction = (Vector) transactions.clone();
         String savedSqlCommand = sqlCommand;
 
         sqlCommand = sqlCommand.trim();
 
         try {
             if (srcFile == null && sqlCommand.length() == 0 && resources == null) {
                 if (transactions.size() == 0) {
                     throw new BuildException("Source file or resource collection, "
                                              + "transactions or sql statement "
                                              + "must be set!", getLocation());
                 }
             }
 
             if (srcFile != null && !srcFile.isFile()) {
                 throw new BuildException("Source file " + srcFile
                         + " is not a file!", getLocation());
             }
 
             if (resources != null) {
                 // deal with the resources
                 Iterator iter = resources.iterator();
                 while (iter.hasNext()) {
                     Resource r = (Resource) iter.next();
                     // Make a transaction for each resource
                     Transaction t = createTransaction();
                     t.setSrcResource(r);
                 }
             }
 
             // Make a transaction group for the outer command
             Transaction t = createTransaction();
             t.setSrc(srcFile);
             t.addText(sqlCommand);
 
             if (getConnection() == null) {
                 // not a valid rdbms
                 return;
             }
 
             try {
                 PrintStream out = System.out;
                 try {
                     if (output != null) {
                         log("Opening PrintStream to output file " + output, Project.MSG_VERBOSE);
                         out = new PrintStream(new BufferedOutputStream(
                                 new FileOutputStream(output.getAbsolutePath(), append)));
                     }
 
                     // Process all transactions
                     for (Enumeration e = transactions.elements();
                          e.hasMoreElements();) {
 
                         ((Transaction) e.nextElement()).runTransaction(out);
                         if (!isAutocommit()) {
                             log("Committing transaction", Project.MSG_VERBOSE);
                             getConnection().commit();
                         }
                     }
                 } finally {
                     FileUtils.close(out);
                 }
             } catch (IOException e) {
                 closeQuietly();
                 if (onError.equals("abort")) {
                     throw new BuildException(e, getLocation());
                 }
             } catch (SQLException e) {
                 closeQuietly();
                 if (onError.equals("abort")) {
                     throw new BuildException(e, getLocation());
                 }
             } finally {
                 try {
                     if (getStatement() != null) {
                         getStatement().close();
                     }
                 } catch (SQLException ex) {
                     // ignore
                 }
                 try {
                     if (getConnection() != null) {
                         getConnection().close();
                     }
                 } catch (SQLException ex) {
                     // ignore
                 }
             }
 
             log(goodSql + " of " + totalSql + " SQL statements executed successfully");
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
         String line;
 
         BufferedReader in = new BufferedReader(reader);
 
         while ((line = in.readLine()) != null) {
             if (!keepformat) {
                 line = line.trim();
             }
             if (expandProperties) {
                 line = getProject().replaceProperties(line);
             }
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
 
             sql.append(keepformat ? "\n" : " ").append(line);
 
             // SQL defines "--" as a comment to EOL
             // and in Oracle it may contain a hint
             // so we cannot just remove it, instead we must end it
             if (!keepformat && line.indexOf("--") >= 0) {
                 sql.append("\n");
             }
-            if ((delimiterType.equals(DelimiterType.NORMAL) && StringUtils.endsWith(sql, delimiter))
-                    || (delimiterType.equals(DelimiterType.ROW) && line.equals(delimiter))) {
-                execSQL(sql.substring(0, sql.length() - delimiter.length()), out);
+            int lastDelimPos = lastDelimiterPosition(sql, line);
+            if (lastDelimPos > -1) {
+                execSQL(sql.substring(0, lastDelimPos), out);
                 sql.replace(0, sql.length(), "");
             }
         }
         // Catch any statements not followed by ;
         if (sql.length() > 0) {
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
 
             ret = getStatement().execute(sql);
             updateCount = getStatement().getUpdateCount();
             do {
                 if (updateCount != -1) {
                     updateCountTotal += updateCount;
                 }
                 if (ret) {
                     resultSet = getStatement().getResultSet();
                     if (print) {
                         printResults(resultSet, out);
                     }
                 }
                 ret = getStatement().getMoreResults();
                 updateCount = getStatement().getUpdateCount();
             } while (ret || updateCount != -1);
 
             log(updateCountTotal + " rows affected", Project.MSG_VERBOSE);
 
             if (print && showtrailers) {
                 out.println(updateCountTotal + " rows affected");
             }
             SQLWarning warning = getConnection().getWarnings();
             while (warning != null) {
                 log(warning + " sql warning", Project.MSG_VERBOSE);
                 warning = warning.getNextWarning();
             }
             getConnection().clearWarnings();
             goodSql++;
         } catch (SQLException e) {
             log("Failed to execute: " + sql, Project.MSG_ERR);
             if (!onError.equals("abort")) {
                 log(e.toString(), Project.MSG_ERR);
             }
             if (!onError.equals("continue")) {
                 throw e;
             }
         } finally {
             if (resultSet != null) {
                 try {
                     resultSet.close();
                 } catch (SQLException e) {
                     //ignore
                 }
             }
         }
     }
 
     /**
      * print any results in the statement
      * @deprecated since 1.6.x.
      *             Use {@link #printResults(java.sql.ResultSet, java.io.PrintStream)
      *             the two arg version} instead.
      * @param out the place to print results
      * @throws SQLException on SQL problems.
      */
     protected void printResults(PrintStream out) throws SQLException {
         ResultSet rs = getStatement().getResultSet();
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
     protected void printResults(ResultSet rs, PrintStream out) throws SQLException {
         if (rs != null) {
             log("Processing new result set.", Project.MSG_VERBOSE);
             ResultSetMetaData md = rs.getMetaData();
             int columnCount = md.getColumnCount();
             if (columnCount > 0) {
                 if (showheaders) {
                     out.print(md.getColumnName(1));
                     for (int col = 2; col <= columnCount; col++) {
                          out.write(',');
                          out.print(md.getColumnName(col));
                     }
                     out.println();
                 }
                 while (rs.next()) {
                     printValue(rs, 1, out);
                     for (int col = 2; col <= columnCount; col++) {
                         out.write(',');
                         printValue(rs, col, out);
                     }
                     out.println();
                 }
             }
         }
         out.println();
     }
 
     private void printValue(ResultSet rs, int col, PrintStream out)
             throws SQLException {
         if (rawBlobs && rs.getMetaData().getColumnType(col) == Types.BLOB) {
             new StreamPumper(rs.getBlob(col).getBinaryStream(), out).run();
         } else {
             out.print(rs.getString(col));
         }
     }
 
     /*
      * Closes an unused connection after an error and doesn't rethrow
      * a possible SQLException
      * @since Ant 1.7
      */
     private void closeQuietly() {
         if (!isAutocommit() && getConnection() != null && onError.equals("abort")) {
             try {
                 getConnection().rollback();
             } catch (SQLException ex) {
                 // ignore
             }
         }
     }
 
 
     /**
      * Caches the connection returned by the base class's getConnection method.
      *
      * <p>Subclasses that need to provide a different connection than
      * the base class would, should override this method but keep in
      * mind that this class expects to get the same connection
      * instance on consecutive calls.</p>
      *
      * <p>returns null if the connection does not connect to the
      * expected RDBMS.</p>
      */
     protected Connection getConnection() {
         if (conn == null) {
             conn = super.getConnection();
             if (!isValidRdbms(conn)) {
                 conn = null;
             }
         }
         return conn;
     }
 
     /**
      * Creates and configures a Statement instance which is then
      * cached for subsequent calls.
      *
      * <p>Subclasses that want to provide different Statement
      * instances, should override this method but keep in mind that
      * this class expects to get the same connection instance on
      * consecutive calls.</p>
      */
     protected Statement getStatement() throws SQLException {
         if (statement == null) {
             statement = getConnection().createStatement();
             statement.setEscapeProcessing(escapeProcessing);
         }
 
         return statement;
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
         private Resource tSrcResource = null;
         private String tSqlCommand = "";
 
         /**
          * Set the source file attribute.
          * @param src the source file
          */
         public void setSrc(File src) {
             //there are places (in this file, and perhaps elsewhere, where it is assumed
             //that null is an acceptable parameter.
             if (src != null) {
                 setSrcResource(new FileResource(src));
             }
         }
 
         /**
          * Set the source resource attribute.
          * @param src the source file
          * @since Ant 1.7
          */
         public void setSrcResource(Resource src) {
             if (tSrcResource != null) {
                 throw new BuildException("only one resource per transaction");
             }
             tSrcResource = src;
         }
 
         /**
          * Set inline text
          * @param sql the inline text
          */
         public void addText(String sql) {
             if (sql != null) {
                 this.tSqlCommand += sql;
             }
         }
 
         /**
          * Set the source resource.
          * @param a the source resource collection.
          * @since Ant 1.7
          */
         public void addConfigured(ResourceCollection a) {
             if (a.size() != 1) {
                 throw new BuildException("only single argument resource "
                                          + "collections are supported.");
             }
             setSrcResource((Resource) a.iterator().next());
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
 
             if (tSrcResource != null) {
                 log("Executing resource: " + tSrcResource.toString(),
                     Project.MSG_INFO);
                 InputStream is = null;
                 Reader reader = null;
                 try {
                     is = tSrcResource.getInputStream();
                     reader = (encoding == null) ? new InputStreamReader(is)
                         : new InputStreamReader(is, encoding);
                     runStatements(reader, out);
                 } finally {
                     FileUtils.close(is);
                     FileUtils.close(reader);
                 }
             }
         }
     }
+
+    public int lastDelimiterPosition(StringBuffer buf, String currentLine) {
+        if (strictDelimiterMatching) {
+            if (delimiterType.equals(DelimiterType.NORMAL)
+                && StringUtils.endsWith(buf, delimiter)) {
+                return buf.length() - delimiter.length();
+            } else if (delimiterType.equals(DelimiterType.ROW)
+                       && currentLine.equals(delimiter)) {
+                return 0;
+            }
+            // no match
+            return -1;
+        } else {
+            String d = delimiter.trim().toLowerCase(Locale.US);
+            if (delimiterType.equals(DelimiterType.NORMAL)) {
+                // still trying to avoid wasteful copying, see
+                // StringUtils.endsWith
+                int endIndex = delimiter.length() - 1;
+                int bufferIndex = buf.length() - 1;
+                while (bufferIndex >= 0
+                       && Character.isWhitespace(buf.charAt(bufferIndex))) {
+                    --bufferIndex;
+                }
+                if (bufferIndex < endIndex) {
+                    return -1;
+                }
+                while (endIndex >= 0) {
+                    if (buf.substring(bufferIndex, 1).toLowerCase(Locale.US)
+                        .charAt(0) != d.charAt(endIndex)) {
+                        return -1;
+                    }
+                    bufferIndex--;
+                    endIndex--;
+                }
+                return bufferIndex;
+            } else {
+                return currentLine.trim().toLowerCase(Locale.US).equals(d)
+                    ? 0 : -1;
+            }
+        }
+    }
 }
diff --git a/src/tests/junit/org/apache/tools/ant/taskdefs/SQLExecTest.java b/src/tests/junit/org/apache/tools/ant/taskdefs/SQLExecTest.java
index 6854b8b31..91c440e51 100644
--- a/src/tests/junit/org/apache/tools/ant/taskdefs/SQLExecTest.java
+++ b/src/tests/junit/org/apache/tools/ant/taskdefs/SQLExecTest.java
@@ -1,237 +1,255 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
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
 
 import java.sql.Driver;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.DriverPropertyInfo;
 import java.util.Properties;
 import java.io.File;
 import java.net.URL;
 
 import junit.framework.TestCase;
 
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.BuildException;
 
 /**
  * Simple testcase to test for driver caching.
  * To test for your own database, you may need to tweak getProperties(int)
  * and add a couple of keys. see testOracle and testMySQL for an example.
  *
  * It would be much better to extend this testcase by using HSQL
  * as the test db, so that a db is really used.
  *
  */
 public class SQLExecTest extends TestCase {
 
     // some database keys, see #getProperties(int)
     public final static int NULL = 0;
     public final static int ORACLE = 1;
     public final static int MYSQL = 2;
 
     // keys used in properties.
     public final static String DRIVER = "driver";
     public final static String USER = "user";
     public final static String PASSWORD = "password";
     public final static String URL = "url";
     public final static String PATH = "path";
     public final static String SQL = "sql";
 
     public SQLExecTest(String s) {
         super(s);
     }
 
     protected void setUp() throws Exception {
         // make sure the cache is cleared.
         JDBCTask.getLoaderMap().clear();
     }
 
    // simple test to ensure that the caching does work...
     public void testDriverCaching(){
         SQLExec sql = createTask(getProperties(NULL));
         assertTrue(!SQLExec.getLoaderMap().containsKey(NULL_DRIVER));
         try {
             sql.execute();
         } catch (BuildException e){
             assertTrue(e.getException().getMessage().indexOf("No suitable Driver") != -1);
         }
         assertTrue(SQLExec.getLoaderMap().containsKey(NULL_DRIVER));
         assertSame(sql.getLoader(), JDBCTask.getLoaderMap().get(NULL_DRIVER));
         ClassLoader loader1 = sql.getLoader();
 
         // 2nd run..
         sql = createTask(getProperties(NULL));
         // the driver must still be cached.
         assertTrue(JDBCTask.getLoaderMap().containsKey(NULL_DRIVER));
         try {
             sql.execute();
         } catch (BuildException e){
             assertTrue(e.getException().getMessage().indexOf("No suitable Driver") != -1);
         }
         assertTrue(JDBCTask.getLoaderMap().containsKey(NULL_DRIVER));
         assertSame(sql.getLoader(), JDBCTask.getLoaderMap().get(NULL_DRIVER));
         assertSame(loader1, sql.getLoader());
     }
 
     public void testNull() throws Exception {
         doMultipleCalls(1000, NULL, true, true);
     }
 
     /*
     public void testOracle(){
         doMultipleCalls(1000, ORACLE, true, false);
     }*/
 
     /*
     public void testMySQL(){
         doMultipleCalls(1000, MYSQL, true, false);
     }*/
 
 
     /**
      * run a sql tasks multiple times.
      * @param calls number of times to execute the task
      * @param database the database to execute on.
      * @param caching should caching be enabled ?
      * @param catchexception true to catch exception for each call, false if not.
      */
     protected void doMultipleCalls(int calls, int database, boolean caching, boolean catchexception){
         Properties props = getProperties(database);
         for (int i = 0; i < calls; i++){
             SQLExec sql = createTask(props);
             sql.setCaching(caching);
             try  {
                 sql.execute();
             } catch (BuildException e){
                 if (!catchexception){
                     throw e;
                 }
             }
         }
     }
 
     /**
      * Create a task from a set of properties
      * @see #getProperties(int)
      */
     protected SQLExec createTask(Properties props){
         SQLExec sql = new SQLExec();
         sql.setProject( new Project() );
         sql.setDriver( props.getProperty(DRIVER) );
         sql.setUserid( props.getProperty(USER) );
         sql.setPassword( props.getProperty(PASSWORD) );
         sql.setUrl( props.getProperty(URL) );
         sql.createClasspath().setLocation( new File(props.getProperty(PATH)) );
         sql.addText( props.getProperty(SQL) );
         return sql;
     }
 
     /**
      * try to find the path from a resource (jar file or directory name)
      * so that it can be used as a classpath to load the resource.
      */
     protected String findResourcePath(String resource){
         resource = resource.replace('.', '/') + ".class";
         URL url = getClass().getClassLoader().getResource(resource);
         if (url == null) {
             return null;
         }
         String u = url.toString();
         if (u.startsWith("jar:file:")) {
             int pling = u.indexOf("!");
             return u.substring("jar:file:".length(), pling);
         } else if (u.startsWith("file:")) {
             int tail = u.indexOf(resource);
             return u.substring("file:".length(), tail);
         }
         return null;
     }
 
     /**
      * returns a configuration associated to a specific database.
      * If you want to test on your specific base, you'd better
      * tweak this to make it run or add your own database.
      * The driver lib should be dropped into the system classloader.
      */
     protected Properties getProperties(int database){
         Properties props = null;
         switch (database){
             case ORACLE:
                 props = getProperties("oracle.jdbc.driver.OracleDriver", "test", "test", "jdbc:oracle:thin:@127.0.0.1:1521:orcl");
                 break;
             case MYSQL:
                 props = getProperties("org.gjt.mm.mysql.Driver", "test", "test", "jdbc:mysql://127.0.0.1:3306/test");
                 break;
             case NULL:
             default:
                 props = getProperties(NULL_DRIVER, "test", "test", "jdbc:database://hostname:port/name");
         }
         // look for the driver path...
         String path = findResourcePath(props.getProperty(DRIVER));
         props.put(PATH, path);
         props.put(SQL, "create table OOME_TEST(X INTEGER NOT NULL);\ndrop table if exists OOME_TEST;");
         return props;
     }
 
     /** helper method to build properties */
     protected Properties getProperties(String driver, String user, String pwd, String url){
         Properties props = new Properties();
         props.put(DRIVER, driver);
         props.put(USER, user);
         props.put(PASSWORD, pwd);
         props.put(URL, url);
         return props;
     }
 
 
 //--- NULL JDBC driver just for simple test since there are no db driver
 // available as a default in Ant :)
 
     public final static String NULL_DRIVER = NullDriver.class.getName();
 
     public static class NullDriver implements Driver {
         public Connection connect(String url, Properties info)
                 throws SQLException {
             return null;
         }
 
         public boolean acceptsURL(String url) throws SQLException {
             return false;
         }
 
         public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
                 throws SQLException {
             return new DriverPropertyInfo[0];
         }
 
         public int getMajorVersion() {
             return 0;
         }
 
         public int getMinorVersion() {
             return 0;
         }
 
         public boolean jdbcCompliant() {
             return false;
         }
     }
 
+    public void testLastDelimiterPositionNormalModeStrict() {
+        SQLExec s = new SQLExec();
+        assertEquals(-1,
+                     s.lastDelimiterPosition(new StringBuffer(), null));
+        assertEquals(-1,
+                     s.lastDelimiterPosition(new StringBuffer("GO"), null));
+        assertEquals(-1,
+                     s.lastDelimiterPosition(new StringBuffer("; "), null));
+        assertEquals(2,
+                     s.lastDelimiterPosition(new StringBuffer("ab;"), null));
+        s.setDelimiter("GO");
+        assertEquals(-1,
+                     s.lastDelimiterPosition(new StringBuffer("GO "), null));
+        assertEquals(-1,
+                     s.lastDelimiterPosition(new StringBuffer("go"), null));
+        assertEquals(0,
+                     s.lastDelimiterPosition(new StringBuffer("GO"), null));
+    }
 }
