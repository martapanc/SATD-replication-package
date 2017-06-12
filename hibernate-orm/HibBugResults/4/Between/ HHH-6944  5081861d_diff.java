diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index c9d0f45790..e6be68ad22 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,695 +1,699 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.dialect;
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.ConditionalParenthesisFunction;
 import org.hibernate.dialect.function.ConvertFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
-import org.hibernate.exception.internal.CacheSQLStateConverter;
-import org.hibernate.exception.spi.SQLExceptionConverter;
+import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;
+import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CacheJoinFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * Cach&eacute; 2007.1 dialect. This class is required in order to use Hibernate with Intersystems Cach&eacute; SQL.<br>
  * <br>
  * Compatible with Cach&eacute; 2007.1.
  * <br>
  * <head>
  * <title>Cach&eacute; and Hibernate</title>
  * </head>
  * <body>
  * <h1>Cach&eacute; and Hibernate</h1>
  * <h2>PREREQUISITES</h2>
  * These setup instructions assume that both Cach&eacute; and Hibernate are installed and operational.
  * <br>
  * <h2>HIBERNATE DIRECTORIES AND FILES</h2>
  * JBoss distributes the InterSystems Cache' dialect for Hibernate 3.2.1
  * For earlier versions of Hibernate please contact
  * <a href="http://www.intersystems.com/support/cache-support.html">InterSystems Worldwide Response Center</A> (WRC)
  * for the appropriate source files.
  * <br>
  * <h2>CACH&Eacute; DOCUMENTATION</h2>
  * Documentation for Cach&eacute; is available online when Cach&eacute; is running.
  * It can also be obtained from the
  * <a href="http://www.intersystems.com/cache/downloads/documentation.html">InterSystems</A> website.
  * The book, "Object-oriented Application Development Using the Cach&eacute; Post-relational Database:
  * is also available from Springer-Verlag.
  * <br>
  * <h2>HIBERNATE DOCUMENTATION</h2>
  * Hibernate comes with extensive electronic documentation.
  * In addition, several books on Hibernate are available from
  * <a href="http://www.manning.com">Manning Publications Co</a>.
  * Three available titles are "Hibernate Quickly", "Hibernate in Action", and "Java Persistence with Hibernate".
  * <br>
  * <h2>TO SET UP HIBERNATE FOR USE WITH CACH&Eacute;</h2>
  * The following steps assume that the directory where Cach&eacute; was installed is C:\CacheSys.
  * This is the default installation directory for  Cach&eacute;.
  * The default installation directory for Hibernate is assumed to be C:\Hibernate.
  * <p/>
  * If either product is installed in a different location, the pathnames that follow should be modified appropriately.
  * <p/>
  * Cach&eacute; version 2007.1 and above is recommended for use with
  * Hibernate.  The next step depends on the location of your
  * CacheDB.jar depending on your version of Cach&eacute;.
  * <ol>
  * <li>Copy C:\CacheSys\dev\java\lib\JDK15\CacheDB.jar to C:\Hibernate\lib\CacheDB.jar.</li>
  * <p/>
  * <li>Insert the following files into your Java classpath:
  * <p/>
  * <ul>
  * <li>All jar files in the directory C:\Hibernate\lib</li>
  * <li>The directory (or directories) where hibernate.properties and/or hibernate.cfg.xml are kept.</li>
  * </ul>
  * </li>
  * <p/>
  * <li>In the file, hibernate.properties (or hibernate.cfg.xml),
  * specify the Cach&eacute; dialect and the Cach&eacute; version URL settings.</li>
  * </ol>
  * <p/>
  * For example, in Hibernate 3.2, typical entries in hibernate.properties would have the following
  * "name=value" pairs:
  * <p/>
  * <table cols=3 border cellpadding=5 cellspacing=0>
  * <tr>
  * <th>Property Name</th>
  * <th>Property Value</th>
  * </tr>
  * <tr>
  * <td>hibernate.dialect</td>
  * <td>org.hibernate.dialect.Cache71Dialect</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.driver_class</td>
  * <td>com.intersys.jdbc.CacheDriver</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.username</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.password</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.url</td>
  * <td>jdbc:Cache://127.0.0.1:1972/USER</td>
  * </tr>
  * </table>
  * <p/>
  * <dl>
  * <dt><b>Note 1</b></dt>
  * <dd>Please contact your administrator for the userid and password you should use when attempting access via JDBC.
  * By default, these are chosen to be "_SYSTEM" and "SYS" respectively as noted in the SQL standard.</dd>
  * </dl>
  * <br>
  * <h2>CACH&Eacute; VERSION URL</h2>
  * This is the standard URL for the JDBC driver.
  * For a JDBC driver on the machine hosting Cach&eacute;, use the IP "loopback" address, 127.0.0.1.
  * For 1972, the default port, specify the super server port of your Cach&eacute; instance.
  * For USER, substitute the NAMESPACE which contains your Cach&eacute; database data.
  * <br>
  * <h2>CACH&Eacute; DIALECTS</h2>
  * Choices for Dialect are:
  * <br>
  * <p/>
  * <ol>
  * <li>org.hibernate.dialect.Cache71Dialect (requires Cach&eacute;
  * 2007.1 or above)</li>
  * <p/>
  * </ol>
  * <br>
  * <h2>SUPPORT FOR IDENTITY COLUMNS</h2>
  * Cach&eacute; 2007.1 or later supports identity columns.  For
  * Hibernate to use identity columns, specify "native" as the
  * generator.
  * <br>
  * <h2>SEQUENCE DIALECTS SUPPORT SEQUENCES</h2>
  * <p/>
  * To use Hibernate sequence support with Cach&eacute; in a namespace, you must FIRST load the following file into that namespace:
  * <pre>
  *     etc\CacheSequences.xml
  * </pre>
  * For example, at the COS terminal prompt in the namespace, run the
  * following command:
  * <p>
  * d LoadFile^%apiOBJ("c:\hibernate\etc\CacheSequences.xml","ck")
  * <p>
  * In your Hibernate mapping you can specify sequence use.
  * <p>
  * For example, the following shows the use of a sequence generator in a Hibernate mapping:
  * <pre>
  *     &lt;id name="id" column="uid" type="long" unsaved-value="null"&gt;
  *         &lt;generator class="sequence"/&gt;
  *     &lt;/id&gt;
  * </pre>
  * <br>
  * <p/>
  * Some versions of Hibernate under some circumstances call
  * getSelectSequenceNextValString() in the dialect.  If this happens
  * you will receive the error message: new MappingException( "Dialect
  * does not support sequences" ).
  * <br>
  * <h2>HIBERNATE FILES ASSOCIATED WITH CACH&Eacute; DIALECT</h2>
  * The following files are associated with Cach&eacute; dialect:
  * <p/>
  * <ol>
  * <li>src\org\hibernate\dialect\Cache71Dialect.java</li>
  * <li>src\org\hibernate\dialect\function\ConditionalParenthesisFunction.java</li>
  * <li>src\org\hibernate\dialect\function\ConvertFunction.java</li>
  * <li>src\org\hibernate\exception\CacheSQLStateConverter.java</li>
  * <li>src\org\hibernate\sql\CacheJoinFragment.java</li>
  * </ol>
  * Cache71Dialect ships with Hibernate 3.2.  All other dialects are distributed by InterSystems and subclass Cache71Dialect.
  *
  * @author Jonathan Levinson
  */
 
 public class Cache71Dialect extends Dialect {
 
 	/**
 	 * Creates new <code>Cache71Dialect</code> instance. Sets up the JDBC /
 	 * Cach&eacute; type mappings.
 	 */
 	public Cache71Dialect() {
 		super();
 		commonRegistration();
 		register71Functions();
 	}
 
 	protected final void commonRegistration() {
 		// Note: For object <-> SQL datatype mappings see:
 		//	 Configuration Manager | Advanced | SQL | System DDL Datatype Mappings
 		//
 		//	TBD	registerColumnType(Types.BINARY,        "binary($1)");
 		// changed 08-11-2005, jsl
 		registerColumnType( Types.BINARY, "varbinary($1)" );
 		registerColumnType( Types.BIGINT, "BigInt" );
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );	// binary %Stream
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );		// character %Stream
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		// TBD should this be varbinary($1)?
 		//		registerColumnType(Types.VARBINARY,     "binary($1)");
 		registerColumnType( Types.VARBINARY, "longvarbinary" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.BLOB, "longvarbinary" );
 		registerColumnType( Types.CLOB, "longvarchar" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		//getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
 
 		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", StandardBasicTypes.STRING ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
 		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "($length(?1)*8)" ) );
 		// hibernate impelemnts cast in Dialect.java
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardJDBCEscapeFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "cos", new StandardJDBCEscapeFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardJDBCEscapeFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 		registerFunction( "convert", new ConvertFunction() );
 		registerFunction( "curdate", new StandardJDBCEscapeFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction(
 				"current_timestamp", new ConditionalParenthesisFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP )
 		);
 		registerFunction( "curtime", new StandardJDBCEscapeFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "database", new StandardJDBCEscapeFunction( "database", StandardBasicTypes.STRING ) );
 		registerFunction( "dateadd", new VarArgsSQLFunction( StandardBasicTypes.TIMESTAMP, "dateadd(", ",", ")" ) );
 		registerFunction( "datediff", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datediff(", ",", ")" ) );
 		registerFunction( "datename", new VarArgsSQLFunction( StandardBasicTypes.STRING, "datename(", ",", ")" ) );
 		registerFunction( "datepart", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datepart(", ",", ")" ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardJDBCEscapeFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardJDBCEscapeFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardJDBCEscapeFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardJDBCEscapeFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		// is it necessary to register %exact since it can only appear in a where clause?
 		registerFunction( "%exact", new StandardSQLFunction( "%exact", StandardBasicTypes.STRING ) );
 		registerFunction( "exp", new StandardJDBCEscapeFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%external", new StandardSQLFunction( "%external", StandardBasicTypes.STRING ) );
 		registerFunction( "$extract", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$extract(", ",", ")" ) );
 		registerFunction( "$find", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$find(", ",", ")" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "getdate", new StandardSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "hour", new StandardJDBCEscapeFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(", ",", ")" ) );
 		registerFunction( "%internal", new StandardSQLFunction( "%internal" ) );
 		registerFunction( "isnull", new VarArgsSQLFunction( "isnull(", ",", ")" ) );
 		registerFunction( "isnumeric", new StandardSQLFunction( "isnumeric", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lcase", new StandardJDBCEscapeFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardJDBCEscapeFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
 		registerFunction( "$length", new VarArgsSQLFunction( "$length(", ",", ")" ) );
 		// aggregate functions shouldn't be registered, right?
 		//registerFunction( "list", new StandardSQLFunction("list",StandardBasicTypes.STRING) );
 		// stopped on $list
 		registerFunction( "$list", new VarArgsSQLFunction( "$list(", ",", ")" ) );
 		registerFunction( "$listdata", new VarArgsSQLFunction( "$listdata(", ",", ")" ) );
 		registerFunction( "$listfind", new VarArgsSQLFunction( "$listfind(", ",", ")" ) );
 		registerFunction( "$listget", new VarArgsSQLFunction( "$listget(", ",", ")" ) );
 		registerFunction( "$listlength", new StandardSQLFunction( "$listlength", StandardBasicTypes.INTEGER ) );
 		registerFunction( "locate", new StandardSQLFunction( "$FIND", StandardBasicTypes.INTEGER ) );
 		registerFunction( "log", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "minute", new StandardJDBCEscapeFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "mod", new StandardJDBCEscapeFunction( "mod", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "month", new StandardJDBCEscapeFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "nullif", new VarArgsSQLFunction( "nullif(", ",", ")" ) );
 		registerFunction( "nvl", new NvlFunction() );
 		registerFunction( "%odbcin", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%odbcout", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%pattern", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%pattern", "" ) );
 		registerFunction( "pi", new StandardJDBCEscapeFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "$piece", new VarArgsSQLFunction( StandardBasicTypes.STRING, "$piece(", ",", ")" ) );
 		registerFunction( "position", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "position(", " in ", ")" ) );
 		registerFunction( "power", new VarArgsSQLFunction( StandardBasicTypes.STRING, "power(", ",", ")" ) );
 		registerFunction( "quarter", new StandardJDBCEscapeFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "repeat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "repeat(", ",", ")" ) );
 		registerFunction( "replicate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "replicate(", ",", ")" ) );
 		registerFunction( "right", new StandardJDBCEscapeFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "round", new VarArgsSQLFunction( StandardBasicTypes.FLOAT, "round(", ",", ")" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "second", new StandardJDBCEscapeFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardJDBCEscapeFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "%sqlstring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlstring(", ",", ")" ) );
 		registerFunction( "%sqlupper", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlupper(", ",", ")" ) );
 		registerFunction( "sqrt", new StandardJDBCEscapeFunction( "SQRT", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%startswith", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%startswith", "" ) );
 		// below is for Cache' that don't have str in 2007.1 there is str and we register str directly
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as char varying)" ) );
 		registerFunction( "string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "string(", ",", ")" ) );
 		// note that %string is deprecated
 		registerFunction( "%string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%string(", ",", ")" ) );
 		registerFunction( "substr", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substr(", ",", ")" ) );
 		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "tan", new StandardJDBCEscapeFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampadd", new StandardJDBCEscapeFunction( "timestampadd", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampdiff", new StandardJDBCEscapeFunction( "timestampdiff", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tochar", new VarArgsSQLFunction( StandardBasicTypes.STRING, "tochar(", ",", ")" ) );
 		registerFunction( "to_char", new VarArgsSQLFunction( StandardBasicTypes.STRING, "to_char(", ",", ")" ) );
 		registerFunction( "todate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "to_date", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "tonumber", new StandardSQLFunction( "tonumber" ) );
 		registerFunction( "to_number", new StandardSQLFunction( "tonumber" ) );
 		// TRIM(end_keyword string-expression-1 FROM string-expression-2)
 		// use Hibernate implementation "From" is one of the parameters they pass in position ?3
 		//registerFunction( "trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1 ?2 from ?3)") );
 		registerFunction( "truncate", new StandardJDBCEscapeFunction( "truncate", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardJDBCEscapeFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		// %upper is deprecated
 		registerFunction( "%upper", new StandardSQLFunction( "%upper" ) );
 		registerFunction( "user", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "week", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.INTEGER ) );
 		registerFunction( "xmlconcat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlconcat(", ",", ")" ) );
 		registerFunction( "xmlelement", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlelement(", ",", ")" ) );
 		// xmlforest requires a new kind of function constructor
 		registerFunction( "year", new StandardJDBCEscapeFunction( "year", StandardBasicTypes.INTEGER ) );
 	}
 
 	protected final void register71Functions() {
 		this.registerFunction( "str", new VarArgsSQLFunction( StandardBasicTypes.STRING, "str(", ",", ")" ) );
 	}
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean hasAlterTable() {
 		// Does this dialect support the ALTER TABLE syntax?
 		return true;
 	}
 
 	public boolean qualifyIndexName() {
 		// Do we need to qualify index names with the schema name?
 		return false;
 	}
 
 	public boolean supportsUnique() {
 		// Does this dialect support the UNIQUE column syntax?
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @return String
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		// The syntax used to add a foreign key constraint to a table.
 		return new StringBuffer( 300 )
 				.append( " ADD CONSTRAINT " )
 				.append( constraintName )
 				.append( " FOREIGN KEY " )
 				.append( constraintName )
 				.append( " (" )
 				.append( StringHelper.join( ", ", foreignKey ) )	// identifier-commalist
 				.append( ") REFERENCES " )
 				.append( referencedTable )
 				.append( " (" )
 				.append( StringHelper.join( ", ", primaryKey ) ) // identifier-commalist
 				.append( ") " )
 				.toString();
 	}
 
 	public boolean supportsCheck() {
 		// Does this dialect support check constraints?
 		return false;
 	}
 
 	public String getAddColumnString() {
 		// The syntax used to add a column to a table
 		return " add column";
 	}
 
 	public String getCascadeConstraintsString() {
 		// Completely optional cascading drop clause.
 		return "";
 	}
 
 	public boolean dropConstraints() {
 		// Do we need to drop constraints before dropping tables in this dialect?
 		return true;
 	}
 
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return true;
 	}
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String generateTemporaryTableName(String baseTableName) {
 		String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 25 ? name.substring( 1, 25 ) : name;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return Boolean.FALSE;
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public Class getNativeIdentifierGeneratorClass() {
 		return IdentityGenerator.class;
 	}
 
 	public boolean hasDataTypeInIdentityColumn() {
 		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
 		// data type
 		return true;
 	}
 
 	public String getIdentityColumnString() throws MappingException {
 		// The keyword used to specify an identity column, if identity column key generation is supported.
 		return "identity";
 	}
 
 	public String getIdentitySelectString() {
 		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsSequences() {
 		return false;
 	}
 
 // It really does support sequences, but InterSystems elects to suggest usage of IDENTITY instead :/
 // Anyway, below are the actual support overrides for users wanting to use this combo...
 //
 //	public String getSequenceNextValString(String sequenceName) {
 //		return "select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getSelectSequenceNextValString(String sequenceName) {
 //		return "(select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getCreateSequenceString(String sequenceName) {
 //		return "insert into InterSystems.Sequences(Name) values (ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getDropSequenceString(String sequenceName) {
 //		return "delete from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getQuerySequencesString() {
 //		return "select name from InterSystems.Sequences";
 //	}
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsForUpdate() {
 		// Does this dialect support the FOR UPDATE syntax?
 		return false;
 	}
 
 	public boolean supportsForUpdateOf() {
 		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
 		return false;
 	}
 
 	public boolean supportsForUpdateNowait() {
 		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
 		return false;
 	}
 
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// InterSystems Cache' does not current support "SELECT ... FOR UPDATE" syntax...
 		// Set your transaction mode to READ_COMMITTED before using
 		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC) {
 			return new OptimisticLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// LIMIT support (ala TOP) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
 	public boolean bindLimitParametersFirst() {
 		// Does the LIMIT clause come at the start of the SELECT statement, rather than at the end?
 		return true;
 	}
 
 	public boolean useMaxForLimit() {
 		// Does the LIMIT clause take a "maximum" row number instead of a total number of returned rows?
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hasOffset ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 
 		// This does not support the Cache SQL 'DISTINCT BY (comma-list)' extensions,
 		// but this extension is not supported through Hibernate anyway.
 		int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
 
 		return new StringBuffer( sql.length() + 8 )
 				.append( sql )
 				.insert( insertionPoint, " TOP ? " )
 				.toString();
 	}
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return ( ResultSet ) ps.getObject( 1 );
 	}
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getLowercaseFunction() {
 		// The name of the SQL function that transforms a string to lowercase
 		return "lower";
 	}
 
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
 	public JoinFragment createOuterJoinFragment() {
 		// Create an OuterJoinGenerator for this dialect.
 		return new CacheJoinFragment();
 	}
 
 	public String getNoColumnsInsertString() {
 		// The keyword used to insert a row without specifying
 		// any column values
 		return " default values";
 	}
 
-	public SQLExceptionConverter buildSQLExceptionConverter() {
-		return new CacheSQLStateConverter( EXTRACTER );
+	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
+		return new CacheSQLExceptionConversionDelegate( this );
+	}
+
+	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
+		return EXTRACTER;
 	}
 
 	public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
 		}
 	};
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index b39fd2d501..548442f7e5 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,2199 +1,2241 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.dialect;
 
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.NClob;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CastFunction;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.internal.SQLStateConverter;
+import org.hibernate.exception.spi.ConversionContext;
+import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.
  * Subclasses implement Hibernate compatibility with different systems.<br>
  * <br>
  * Subclasses should provide a public default constructor that <tt>register()</tt>
  * a set of type mappings and default Hibernate properties.<br>
  * <br>
  * Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
-public abstract class Dialect {
+public abstract class Dialect implements ConversionContext {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Dialect.class.getName());
 
 	public static final String DEFAULT_BATCH_SIZE = "15";
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
 		LOG.usingDialect( this );
 		StandardAnsiSqlAggregationFunctions.primeFunctionMap( sqlFunctions );
 
 		// standard sql92 functions (can be overridden by subclasses)
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1, ?2, ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "locate(?1, ?2, ?3)" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "cast", new CastFunction() );
 		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(?1 ?2 ?3)") );
 
 		//map second/minute/hour/day/month/year to ANSI extract(), override on subclasses
 		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(second from ?1)") );
 		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(minute from ?1)") );
 		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(hour from ?1)") );
 		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(day from ?1)") );
 		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(month from ?1)") );
 		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)") );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char)") );
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.FLOAT, "float($p)" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.LONGVARBINARY, "bit varying($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar($l)" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.NCHAR, "nchar($l)" );
 		registerColumnType( Types.NVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.LONGNVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.NCLOB, "nclob" );
 
         // register hibernate types for default use in scalar sqlquery type auto detection
 		registerHibernateType( Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName() );
 		registerHibernateType( Types.BINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.BIT, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.CHAR, StandardBasicTypes.CHARACTER.getName() );
         registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
         registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.DATE, StandardBasicTypes.DATE.getName() );
 		registerHibernateType( Types.DOUBLE, StandardBasicTypes.DOUBLE.getName() );
 		registerHibernateType( Types.FLOAT, StandardBasicTypes.FLOAT.getName() );
 		registerHibernateType( Types.INTEGER, StandardBasicTypes.INTEGER.getName() );
 		registerHibernateType( Types.SMALLINT, StandardBasicTypes.SHORT.getName() );
 		registerHibernateType( Types.TINYINT, StandardBasicTypes.BYTE.getName() );
 		registerHibernateType( Types.TIME, StandardBasicTypes.TIME.getName() );
 		registerHibernateType( Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName() );
 		registerHibernateType( Types.VARCHAR, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.VARBINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName() );
 		registerHibernateType( Types.LONGVARBINARY, StandardBasicTypes.IMAGE.getName() );
 		registerHibernateType( Types.NUMERIC, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.BLOB, StandardBasicTypes.BLOB.getName() );
 		registerHibernateType( Types.CLOB, StandardBasicTypes.CLOB.getName() );
 		registerHibernateType( Types.REAL, StandardBasicTypes.FLOAT.getName() );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		String dialectName = Environment.getProperties().getProperty( Environment.DIALECT );
 		return instantiateDialect( dialectName );
 	}
 
 
 	/**
 	 * Get an instance of the dialect specified by the given properties or by
 	 * the current <tt>System</tt> properties.
 	 *
 	 * @param props The properties to use for finding the dialect class to use.
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect(Properties props) throws HibernateException {
 		String dialectName = props.getProperty( Environment.DIALECT );
 		if ( dialectName == null ) {
 			return getDialect();
 		}
 		return instantiateDialect( dialectName );
 	}
 
 	private static Dialect instantiateDialect(String dialectName) throws HibernateException {
 		if ( dialectName == null ) {
 			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
 		}
 		try {
 			return ( Dialect ) ReflectHelper.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
 		}
 	}
 
 	/**
 	 * Retrieve a set of default Hibernate properties for this database.
 	 *
 	 * @return a set of Hibernate properties
 	 */
 	public final Properties getDefaultProperties() {
 		return properties;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName();
 	}
 
 
 	// database type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		String result = typeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode with the given storage specification
 	 * parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
 		String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length ));
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type appropriate for casting operations
 	 * (via the CAST() SQL function) for the given {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The database type name
 	 */
 	public String getCastTypeName(int code) {
 		return getTypeName( code, Column.DEFAULT_LENGTH, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, long capacity, String name) {
 		typeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
 	 * the type name with be replaced by the column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, String name) {
 		typeNames.put( code, name );
 	}
 
 	/**
 	 * Allows the dialect to override a {@link SqlTypeDescriptor}.
 	 * <p/>
 	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
 	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
 	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} doe not allow itself to be
 	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
 	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
 	 * @see #getSqlTypeDescriptorOverride
 	 */
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( sqlTypeDescriptor == null ) {
 			throw new IllegalArgumentException( "sqlTypeDescriptor is null" );
 		}
 		if ( ! sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final SqlTypeDescriptor overridden = getSqlTypeDescriptorOverride( sqlTypeDescriptor.getSqlType() );
 		return overridden == null ? sqlTypeDescriptor : overridden;
 	}
 
 	/**
 	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
 	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
 				descriptor = useInputStreamToInsertBlob() ? BlobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			case Types.CLOB: {
 				descriptor = useInputStreamToInsertBlob() ? ClobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			default: {
 				descriptor = null;
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	/**
 	 * The legacy behavior of Hibernate.  LOBs are not processed by merge
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy LEGACY_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setBinaryStream( 1L );  // the BLOB just read during the load phase of merge
 					InputStream detachedStream = original.getBinaryStream();      // the BLOB from the detached state
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeBlob( original, target, session );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the CLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the CLOB from the detached state
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob( original, target, session );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the NCLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the NCLOB from the detached state
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					"No Hibernate type mapping for java.sql.Types code: " +
 					code +
 					", length: " +
 					length
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name);
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name);
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		sqlFunctions.put( name, function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		sqlKeywords.add(word);
 	}
 
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 */
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else if ( supportsSequences() ) {
 			return SequenceGenerator.class;
 		}
 		else {
 			return TableHiLoGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
 	 */
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
 	 */
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
 	 */
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
 	/**
 	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
 	 * "select identity" clause to the  insert statement.
 	 *  <p/>
 	 * Note, if {@link #supportsInsertSelectIdentity} == false then
 	 * the insert-string should be returned without modification.
 	 *
 	 * @param insertString The insert command
 	 * @return The insert command with any necessary identity select
 	 * clause attached.
 	 */
 	public String appendIdentitySelectToInsert(String insertString) {
 		return insertString;
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value for a particular table
 	 *
 	 * @param table The table into which the insert was done
 	 * @param column The PK column.
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
 		return getIdentitySelectString();
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value.
 	 *
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentitySelectString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentityColumnString(int type) throws MappingException {
 		return getIdentityColumnString();
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY.
 	 *
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentityColumnString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
 	 */
 	public String getIdentityInsertString() {
 		return null;
 	}
 
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
     public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 */
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 */
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 */
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 */
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 */
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 */
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 */
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Given a limit and an offset, apply the limit clause to the query.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param offset The offset of the limit
 	 * @param limit The limit of the limit ;)
 	 * @return The modified query statement with the limit applied.
 	 */
 	public String getLimitString(String query, int offset, int limit) {
 		return getLimitString( query, ( offset > 0 || forceLimitUsage() )  );
 	}
 
 	/**
 	 * Apply s limit clause to the query.
 	 * <p/>
 	 * Typically dialects utilize {@link #supportsVariableLimit() variable}
 	 * limit clauses when they support limits.  Thus, when building the
 	 * select command we do not actually need to know the limit or the offest
 	 * since we will just be using placeholders.
 	 * <p/>
 	 * Here we do still pass along whether or not an offset was specified
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 */
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
 	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 *
 	 * @return The corresponding db/dialect specific offset.
 	 *
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 */
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
         switch ( lockMode ) {
             case PESSIMISTIC_FORCE_INCREMENT:
                 return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_WRITE:
                 return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_READ:
                 return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
             case OPTIMISTIC:
                 return new OptimisticLockingStrategy( lockable, lockMode );
             case OPTIMISTIC_FORCE_INCREMENT:
                 return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
             default:
                 return new SelectLockingStrategy( lockable, lockMode );
         }
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
         LockMode lockMode = lockOptions.getLockMode();
         return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
     @SuppressWarnings( {"deprecation"})
 	private String getForUpdateString(LockMode lockMode, int timeout){
        switch ( lockMode ) {
             case UPGRADE:
                 return getForUpdateString();
             case PESSIMISTIC_READ:
                 return getReadLockString( timeout );
             case PESSIMISTIC_WRITE:
                 return getWriteLockString( timeout );
             case UPGRADE_NOWAIT:
             case FORCE:
             case PESSIMISTIC_FORCE_INCREMENT:
                 return getForUpdateNowaitString();
             default:
                 return "";
         }
     }
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	public String getForUpdateString(String aliases) {
 		// by default we simply return the getForUpdateString() result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @param lockOptions the lock options to apply
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan(lockMode) ) {
 				lockMode = lm;
 			}
 		}
 		lockOptions.setLockMode( lockMode );
 		return getForUpdateString( lockOptions );
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE NOWAIT</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString() {
 		// by default we report no support for NOWAIT lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF colunm_list NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param mode The lock mode to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockMode mode, String tableName) {
 		return tableName;
 	}
 
 	/**
 	 * Modifies the given SQL by applying the appropriate updates for the specified
 	 * lock modes and key columns.
 	 * <p/>
 	 * The behavior here is that of an ANSI SQL <tt>SELECT FOR UPDATE</tt>.  This
 	 * method is really intended to allow dialects which do not support
 	 * <tt>SELECT FOR UPDATE</tt> to achieve this in their own fashion.
 	 *
 	 * @param sql the SQL string to modify
 	 * @param aliasedLockOptions lock options indexed by aliased table names.
 	 * @param keyColumnNames a map of key columns indexed by aliased table names.
 	 * @return the modified SQL string.
 	 */
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 
 	// table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Command used to create a table.
 	 *
 	 * @return The command used to create a table.
 	 */
 	public String getCreateTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Slight variation on {@link #getCreateTableString}.  Here, we have the
 	 * command used to create a table when there is no primary key and
 	 * duplicate rows are expected.
 	 * <p/>
 	 * Most databases do not care about the distinction; originally added for
 	 * Teradata support which does care.
 	 *
 	 * @return The command used to create a multiset table.
 	 */
 	public String getCreateMultisetTableString() {
 		return getCreateTableString();
 	}
 
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support temporary tables?
 	 *
 	 * @return True if temp tables are supported; false otherwise.
 	 */
 	public boolean supportsTemporaryTables() {
 		return false;
 	}
 
 	/**
 	 * Generate a temporary table name given the base table.
 	 *
 	 * @param baseTableName The table name from which to base the temp table name.
 	 * @return The generated temp table name.
 	 */
 	public String generateTemporaryTableName(String baseTableName) {
 		return "HT_" + baseTableName;
 	}
 
 	/**
 	 * Command used to create a temporary table.
 	 *
 	 * @return The command used to create a temporary table.
 	 */
 	public String getCreateTemporaryTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Get any fragments needing to be postfixed to the command for
 	 * temporary table creation.
 	 *
 	 * @return Any required postfix.
 	 */
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	/**
 	 * Command used to drop a temporary table.
 	 *
 	 * @return The command used to drop a temporary table.
 	 */
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
 	/**
 	 * Does the dialect require that temporary table DDL statements occur in
 	 * isolation from other statements?  This would be the case if the creation
 	 * would cause any current transaction to get committed implicitly.
 	 * <p/>
 	 * JDBC defines a standard way to query for this information via the
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}
 	 * method.  However, that does not distinguish between temporary table
 	 * DDL and other forms of DDL; MySQL, for example, reports DDL causing a
 	 * transaction commit via its driver, even though that is not the case for
 	 * temporary table DDL.
 	 * <p/>
 	 * Possible return values and their meanings:<ul>
 	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
 	 * in isolation.</li>
 	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
 	 * temporary table DDL in isolation.</li>
 	 * <li><i>null</i> - defer to the JDBC driver response in regards to
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
 	 * </ul>
 	 *
 	 * @return see the result matrix above.
 	 */
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return null;
 	}
 
 	/**
 	 * Do we need to drop the temporary table after use?
 	 *
 	 * @return True if the table should be dropped.
 	 */
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Registers an OUT parameter which will be returning a
 	 * {@link java.sql.ResultSet}.  How this is accomplished varies greatly
 	 * from DB to DB, hence its inclusion (along with {@link #getResultSet}) here.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the OUT param.
 	 * @return The number of (contiguous) bind positions used.
 	 * @throws SQLException Indicates problems registering the OUT param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @return The extracted result set.
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
-	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.  The default
-	 * Dialect implementation simply returns a converter based on X/Open SQLState codes.
+	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
-	 * It is strongly recommended that specific Dialect implementations override this
-	 * method, since interpretation of a SQL error is much more accurate when based on
-	 * the ErrorCode rather than the SQLState.  Unfortunately, the ErrorCode is a vendor-
-	 * specific approach.
+	 * The preferred method is to not override this method; if possible,
+	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
+	 * instead.
+	 *
+	 * If this method is not overridden, the default SQLExceptionConverter
+	 * implementation executes 3 SQLException converter delegates:
+	 * <ol>
+	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
+	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
+	 *         (it is strongly recommended that specific Dialect implementations
+	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
+	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
+	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
+	 * </ol>
+	 * <p/>
+	 * If this method is overridden, it is strongly recommended that the
+	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
+	 * vendor-specific error codes rather than the SQLState since the
+	 * interpretation is more accurate when using vendor-specific ErrorCodes.
+	 *
+	 * @return The Dialect's preferred SQLExceptionConverter, or null to
+	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
-	 * @return The Dialect's preferred SQLExceptionConverter.
+	 * @see {@link #buildSQLExceptionConversionDelegate()} should be
+	 * overridden instead.
 	 */
 	public SQLExceptionConverter buildSQLExceptionConverter() {
-		// The default SQLExceptionConverter for all dialects is based on SQLState
-		// since SQLErrorCode is extremely vendor-specific.  Specific Dialects
-		// may override to return whatever is most appropriate for that vendor.
-//		return new SQLStateConverter( getViolatedConstraintNameExtracter() );
+		return null;
+	}
+
+	/**
+	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
+	 * interpreting dialect-specific error or SQLState codes.
+	 * <p/>
+	 * When {@link #buildSQLExceptionConverter} returns null, the default 
+	 * {@link SQLExceptionConverter} is used to interpret SQLState and
+	 * error codes. If this method is overridden to return a non-null value,
+	 * the default {@link SQLExceptionConverter} will use the returned
+	 * {@link SQLExceptionConversionDelegate} in addition to the following 
+	 * standard delegates:
+	 * <ol>
+	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
+	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
+	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
+	 * </ol>
+	 * <p/>
+	 * It is strongly recommended that specific Dialect implementations override this
+	 * method, since interpretation of a SQL error is much more accurate when based on
+	 * the a vendor-specific ErrorCode rather than the SQLState.
+	 * <p/>
+	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
+	 */
+	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * @return {@code true} if the underlying Database supports case insensitive like comparison, {@code false} otherwise.
 	 * The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints before dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsUnique() {
 		return true;
 	}
 
     /**
      * Does this dialect support adding Unique constraints via create and alter table ?
      * @return boolean
      */
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 	    return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( constraintName )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
     /**
      * The syntax used to add a unique constraint to a table.
      *
      * @param constraintName The name of the unique constraint.
      * @return The "add unique" fragment
      */
     public String getAddUniqueConstraintString(String constraintName) {
         return " add constraint " + constraintName + " unique ";
     }
 
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * @return Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
     /**
      * Does this dialect support `count(distinct a,b)`?
      *
      * @return True if the database supports counting distinct tuples; false otherwise.
      */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
index 9086ec00d5..70cc4c2375 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
@@ -1,48 +1,55 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.dialect;
+import org.hibernate.exception.internal.OracleSQLExceptionConversionDelegate;
+import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.JoinFragment;
 
 
 /**
  * A dialect specifically for use with Oracle 10g.
  * <p/>
  * The main difference between this dialect and {@link Oracle9iDialect}
  * is the use of "ANSI join syntax".  This dialect also retires the use
  * of the <tt>oracle.jdbc.driver</tt> package in favor of 
  * <tt>oracle.jdbc</tt>.
  *
  * @author Steve Ebersole
  */
 public class Oracle10gDialect extends Oracle9iDialect {
 
 	public Oracle10gDialect() {
 		super();
 	}
 
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
+
+	@Override
+	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
+		return new OracleSQLExceptionConversionDelegate( this );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
index f997d83a85..76230cedc6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
@@ -1,457 +1,446 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.engine.jdbc.internal;
 
 import java.lang.reflect.InvocationTargetException;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.exception.internal.SQLExceptionTypeDelegate;
 import org.hibernate.exception.internal.StandardSQLExceptionConverter;
 import org.hibernate.exception.internal.SQLStateConversionDelegate;
-import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConverter;
-import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Standard implementation of the {@link JdbcServices} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesImpl implements JdbcServices, ServiceRegistryAwareService, Configurable {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JdbcServicesImpl.class.getName());
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private Dialect dialect;
 	private ConnectionProvider connectionProvider;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper sqlExceptionHelper;
 	private ExtractedDatabaseMetaData extractedMetaDataSupport;
 	private LobCreatorBuilder lobCreatorBuilder;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public void configure(Map configValues) {
 		final JdbcConnectionAccess jdbcConnectionAccess = buildJdbcConnectionAccess( configValues );
 		final DialectFactory dialectFactory = serviceRegistry.getService( DialectFactory.class );
 
 		Dialect dialect = null;
 		LobCreatorBuilder lobCreatorBuilder = null;
 
 		boolean metaSupportsScrollable = false;
 		boolean metaSupportsGetGeneratedKeys = false;
 		boolean metaSupportsBatchUpdates = false;
 		boolean metaReportsDDLCausesTxnCommit = false;
 		boolean metaReportsDDLInTxnSupported = true;
 		String extraKeywordsString = "";
 		int sqlStateType = -1;
 		boolean lobLocatorUpdateCopy = false;
 		String catalogName = null;
 		String schemaName = null;
 		LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
 
 		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
 		// The need for it is intended to be alleviated with future development, thus it is
 		// not defined as an Environment constant...
 		//
 		// it is used to control whether we should consult the JDBC metadata to determine
 		// certain Settings default values; it is useful to *not* do this when the database
 		// may not be available (mainly in tools usage).
 		boolean useJdbcMetadata = ConfigurationHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", configValues, true );
 		if ( useJdbcMetadata ) {
 			try {
 				Connection connection = jdbcConnectionAccess.obtainConnection();
 				try {
 					DatabaseMetaData meta = connection.getMetaData();
 					if(LOG.isDebugEnabled()) {
 						LOG.debugf( "Database ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s",
 									meta.getDatabaseProductName(),
 									meta.getDatabaseProductVersion(),
 									meta.getDatabaseMajorVersion(),
 									meta.getDatabaseMinorVersion()
 						);
 						LOG.debugf( "Driver ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s",
 									meta.getDriverName(),
 									meta.getDriverVersion(),
 									meta.getDriverMajorVersion(),
 									meta.getDriverMinorVersion()
 						);
 						LOG.debugf( "JDBC version : %s.%s", meta.getJDBCMajorVersion(), meta.getJDBCMinorVersion() );
 					}
 
 					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
 					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
 					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
 					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
 					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
 					extraKeywordsString = meta.getSQLKeywords();
 					sqlStateType = meta.getSQLStateType();
 					lobLocatorUpdateCopy = meta.locatorsUpdateCopy();
 					typeInfoSet.addAll( TypeInfoExtracter.extractTypeInfo( meta ) );
 
 					dialect = dialectFactory.buildDialect( configValues, connection );
 
 					catalogName = connection.getCatalog();
 					SchemaNameResolver schemaNameResolver = determineExplicitSchemaNameResolver( configValues );
 					if ( schemaNameResolver == null ) {
 // todo : add dialect method
 //						schemaNameResolver = dialect.getSchemaNameResolver();
 					}
 					if ( schemaNameResolver != null ) {
 						schemaName = schemaNameResolver.resolveSchemaName( connection );
 					}
 					lobCreatorBuilder = new LobCreatorBuilder( configValues, connection );
 				}
 				catch ( SQLException sqle ) {
 					LOG.unableToObtainConnectionMetadata( sqle.getMessage() );
 				}
 				finally {
 					if ( connection != null ) {
 						jdbcConnectionAccess.releaseConnection( connection );
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				LOG.unableToObtainConnectionToQueryMetadata( sqle.getMessage() );
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 			catch ( UnsupportedOperationException uoe ) {
 				// user supplied JDBC connections
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 		}
 		else {
 			dialect = dialectFactory.buildDialect( configValues, null );
 		}
 
 		final boolean showSQL = ConfigurationHelper.getBoolean( Environment.SHOW_SQL, configValues, false );
 		final boolean formatSQL = ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, configValues, false );
 
 		this.dialect = dialect;
 		this.lobCreatorBuilder = (
 				lobCreatorBuilder == null ?
 						new LobCreatorBuilder( configValues, null ) :
 						lobCreatorBuilder
 		);
 
 		this.sqlStatementLogger =  new SqlStatementLogger( showSQL, formatSQL );
 
 		this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl(
 				metaSupportsScrollable,
 				metaSupportsGetGeneratedKeys,
 				metaSupportsBatchUpdates,
 				metaReportsDDLInTxnSupported,
 				metaReportsDDLCausesTxnCommit,
 				parseKeywords( extraKeywordsString ),
 				parseSQLStateType( sqlStateType ),
 				lobLocatorUpdateCopy,
 				schemaName,
 				catalogName,
 				typeInfoSet
 		);
 
 		SQLExceptionConverter sqlExceptionConverter = dialect.buildSQLExceptionConverter();
 		if ( sqlExceptionConverter == null ) {
-			final Dialect finalDialect = dialect;
-			final ConversionContext conversionContext = new ConversionContext() {
-				private final ViolatedConstraintNameExtracter extracter = finalDialect.getViolatedConstraintNameExtracter();
-
-				@Override
-				public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
-					return extracter;
-				}
-			};
 			final StandardSQLExceptionConverter converter = new StandardSQLExceptionConverter();
 			sqlExceptionConverter = converter;
-			converter.addDelegate( new SQLExceptionTypeDelegate( conversionContext ) );
+			converter.addDelegate( new SQLExceptionTypeDelegate( dialect ) );
+			converter.addDelegate( dialect.buildSQLExceptionConversionDelegate() );
 			// todo : vary this based on extractedMetaDataSupport.getSqlStateType()
-			converter.addDelegate( new SQLStateConversionDelegate( conversionContext ) );
-			// todo : add Dialect#getSQLExceptionConversionDelegate method and add result here if non-null
+			converter.addDelegate( new SQLStateConversionDelegate( dialect ) );
 		}
 		this.sqlExceptionHelper = new SqlExceptionHelper( sqlExceptionConverter );
 	}
 
 	private JdbcConnectionAccess buildJdbcConnectionAccess(Map configValues) {
 		final MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configValues );
 
 		if ( MultiTenancyStrategy.NONE == multiTenancyStrategy ) {
 			connectionProvider = serviceRegistry.getService( ConnectionProvider.class );
 			return new ConnectionProviderJdbcConnectionAccess( connectionProvider );
 		}
 		else {
 			connectionProvider = null;
 			final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService( MultiTenantConnectionProvider.class );
 			return new MultiTenantConnectionProviderJdbcConnectionAccess( multiTenantConnectionProvider );
 		}
 	}
 
 	private static class ConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final ConnectionProvider connectionProvider;
 
 		public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
 	}
 
 	private static class MultiTenantConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getAnyConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.releaseAnyConnection( connection );
 		}
 	}
 
 
 	// todo : add to Environment
 	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
 
 	private SchemaNameResolver determineExplicitSchemaNameResolver(Map configValues) {
 		Object setting = configValues.get( SCHEMA_NAME_RESOLVER );
 		if ( SchemaNameResolver.class.isInstance( setting ) ) {
 			return (SchemaNameResolver) setting;
 		}
 
 		String resolverClassName = (String) setting;
 		if ( resolverClassName != null ) {
 			try {
 				Class resolverClass = ReflectHelper.classForName( resolverClassName, getClass() );
 				return (SchemaNameResolver) ReflectHelper.getDefaultConstructor( resolverClass ).newInstance();
 			}
 			catch ( ClassNotFoundException e ) {
 				LOG.unableToLocateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 			catch ( InvocationTargetException e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.getTargetException().toString() );
 			}
 			catch ( Exception e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 		}
 		return null;
 	}
 
 	private Set<String> parseKeywords(String extraKeywordsString) {
 		Set<String> keywordSet = new HashSet<String>();
 		keywordSet.addAll( Arrays.asList( extraKeywordsString.split( "," ) ) );
 		return keywordSet;
 	}
 
 	private ExtractedDatabaseMetaData.SQLStateType parseSQLStateType(int sqlStateType) {
 		switch ( sqlStateType ) {
 			case DatabaseMetaData.sqlStateSQL99 : {
 				return ExtractedDatabaseMetaData.SQLStateType.SQL99;
 			}
 			case DatabaseMetaData.sqlStateXOpen : {
 				return ExtractedDatabaseMetaData.SQLStateType.XOpen;
 			}
 			default : {
 				return ExtractedDatabaseMetaData.SQLStateType.UNKOWN;
 			}
 		}
 	}
 
 	private static class ExtractedDatabaseMetaDataImpl implements ExtractedDatabaseMetaData {
 		private final boolean supportsScrollableResults;
 		private final boolean supportsGetGeneratedKeys;
 		private final boolean supportsBatchUpdates;
 		private final boolean supportsDataDefinitionInTransaction;
 		private final boolean doesDataDefinitionCauseTransactionCommit;
 		private final Set<String> extraKeywords;
 		private final SQLStateType sqlStateType;
 		private final boolean lobLocatorUpdateCopy;
 		private final String connectionSchemaName;
 		private final String connectionCatalogName;
 		private final LinkedHashSet<TypeInfo> typeInfoSet;
 
 		private ExtractedDatabaseMetaDataImpl(
 				boolean supportsScrollableResults,
 				boolean supportsGetGeneratedKeys,
 				boolean supportsBatchUpdates,
 				boolean supportsDataDefinitionInTransaction,
 				boolean doesDataDefinitionCauseTransactionCommit,
 				Set<String> extraKeywords,
 				SQLStateType sqlStateType,
 				boolean lobLocatorUpdateCopy,
 				String connectionSchemaName,
 				String connectionCatalogName,
 				LinkedHashSet<TypeInfo> typeInfoSet) {
 			this.supportsScrollableResults = supportsScrollableResults;
 			this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
 			this.supportsBatchUpdates = supportsBatchUpdates;
 			this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
 			this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
 			this.extraKeywords = extraKeywords;
 			this.sqlStateType = sqlStateType;
 			this.lobLocatorUpdateCopy = lobLocatorUpdateCopy;
 			this.connectionSchemaName = connectionSchemaName;
 			this.connectionCatalogName = connectionCatalogName;
 			this.typeInfoSet = typeInfoSet;
 		}
 
 		@Override
 		public boolean supportsScrollableResults() {
 			return supportsScrollableResults;
 		}
 
 		@Override
 		public boolean supportsGetGeneratedKeys() {
 			return supportsGetGeneratedKeys;
 		}
 
 		@Override
 		public boolean supportsBatchUpdates() {
 			return supportsBatchUpdates;
 		}
 
 		@Override
 		public boolean supportsDataDefinitionInTransaction() {
 			return supportsDataDefinitionInTransaction;
 		}
 
 		@Override
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return doesDataDefinitionCauseTransactionCommit;
 		}
 
 		@Override
 		public Set<String> getExtraKeywords() {
 			return extraKeywords;
 		}
 
 		@Override
 		public SQLStateType getSqlStateType() {
 			return sqlStateType;
 		}
 
 		@Override
 		public boolean doesLobLocatorUpdateCopy() {
 			return lobLocatorUpdateCopy;
 		}
 
 		@Override
 		public String getConnectionSchemaName() {
 			return connectionSchemaName;
 		}
 
 		@Override
 		public String getConnectionCatalogName() {
 			return connectionCatalogName;
 		}
 
 		@Override
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return typeInfoSet;
 		}
 	}
 
 	@Override
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	@Override
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	@Override
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	@Override
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	@Override
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return extractedMetaDataSupport;
 	}
 
 	@Override
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
 		return lobCreatorBuilder.buildLobCreator( lobCreationContext );
 	}
 
 	@Override
 	public ResultSetWrapper getResultSetWrapper() {
 		return ResultSetWrapperImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLStateConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLExceptionConversionDelegate.java
similarity index 55%
rename from hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLStateConverter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLExceptionConversionDelegate.java
index a56de18e70..8e12e63cc1 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLStateConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLExceptionConversionDelegate.java
@@ -1,123 +1,94 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.exception.internal;
 
 import java.sql.SQLException;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.JDBCException;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.DataException;
-import org.hibernate.exception.GenericJDBCException;
-import org.hibernate.exception.JDBCConnectionException;
-import org.hibernate.exception.SQLGrammarException;
-import org.hibernate.exception.spi.SQLExceptionConverter;
-import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.AbstractSQLExceptionConversionDelegate;
+import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 
 /**
- * A SQLExceptionConverter implementation specific to Cach&eacute; SQL,
- * accounting for its custom integrity constraint violation error codes.
+ * A {@link org.hibernate.exception.spi.SQLExceptionConversionDelegate}
+ * implementation specific to Cach&eacute; SQL, accounting for its custom
+ * integrity constraint violation error codes.
  *
  * @author Jonathan Levinson
  */
-public class CacheSQLStateConverter implements SQLExceptionConverter {
+public class CacheSQLExceptionConversionDelegate extends AbstractSQLExceptionConversionDelegate {
 
-	private ViolatedConstraintNameExtracter extracter;
-
-	private static final Set<String> SQL_GRAMMAR_CATEGORIES = new HashSet<String>();
 	private static final Set<String> DATA_CATEGORIES = new HashSet<String>();
 	private static final Set<Integer> INTEGRITY_VIOLATION_CATEGORIES = new HashSet<Integer>();
-	private static final Set<String> CONNECTION_CATEGORIES = new HashSet<String>();
 
 	static {
-		SQL_GRAMMAR_CATEGORIES.add( "07" );
-		SQL_GRAMMAR_CATEGORIES.add( "37" );
-		SQL_GRAMMAR_CATEGORIES.add( "42" );
-		SQL_GRAMMAR_CATEGORIES.add( "65" );
-		SQL_GRAMMAR_CATEGORIES.add( "S0" );
-		SQL_GRAMMAR_CATEGORIES.add( "20" );
-
 		DATA_CATEGORIES.add( "22" );
 		DATA_CATEGORIES.add( "21" );
 		DATA_CATEGORIES.add( "02" );
 
 		INTEGRITY_VIOLATION_CATEGORIES.add( 119 );
 		INTEGRITY_VIOLATION_CATEGORIES.add( 120 );
 		INTEGRITY_VIOLATION_CATEGORIES.add( 121 );
 		INTEGRITY_VIOLATION_CATEGORIES.add( 122 );
 		INTEGRITY_VIOLATION_CATEGORIES.add( 123 );
 		INTEGRITY_VIOLATION_CATEGORIES.add( 124 );
 		INTEGRITY_VIOLATION_CATEGORIES.add( 125 );
 		INTEGRITY_VIOLATION_CATEGORIES.add( 127 );
-
-		CONNECTION_CATEGORIES.add( "08" );
 	}
 
-	public CacheSQLStateConverter(ViolatedConstraintNameExtracter extracter) {
-		this.extracter = extracter;
+	public CacheSQLExceptionConversionDelegate(ConversionContext conversionContext) {
+		super( conversionContext );
 	}
 
 	/**
 	 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
 	 *
 	 * @param sqlException The SQLException to be converted.
 	 * @param message	  An optional error message.
 	 * @param sql		  Optionally, the sql being performed when the exception occurred.
-	 * @return The resulting JDBCException.
+	 * @return The resulting JDBCException; returns null if it could not be converted.
 	 */
+	@Override
 	public JDBCException convert(SQLException sqlException, String message, String sql) {
 		String sqlStateClassCode = JdbcExceptionHelper.extractSqlStateClassCode( sqlException );
-		Integer errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 		if ( sqlStateClassCode != null ) {
-			if ( SQL_GRAMMAR_CATEGORIES.contains( sqlStateClassCode ) ) {
-				return new SQLGrammarException( message, sqlException, sql );
-			}
-			else if ( INTEGRITY_VIOLATION_CATEGORIES.contains( errorCode ) ) {
-				String constraintName = extracter.extractConstraintName( sqlException );
+			Integer errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
+			if ( INTEGRITY_VIOLATION_CATEGORIES.contains( errorCode ) ) {
+				String constraintName =
+						getConversionContext()
+								.getViolatedConstraintNameExtracter()
+								.extractConstraintName( sqlException );
 				return new ConstraintViolationException( message, sqlException, sql, constraintName );
 			}
-			else if ( CONNECTION_CATEGORIES.contains( sqlStateClassCode ) ) {
-				return new JDBCConnectionException( message, sqlException, sql );
-			}
 			else if ( DATA_CATEGORIES.contains( sqlStateClassCode ) ) {
 				return new DataException( message, sqlException, sql );
 			}
 		}
-		return handledNonSpecificException( sqlException, message, sql );
-	}
-
-	/**
-	 * Handle an exception not converted to a specific type based on the SQLState.
-	 *
-	 * @param sqlException The exception to be handled.
-	 * @param message	  An optional message
-	 * @param sql		  Optionally, the sql being performed when the exception occurred.
-	 * @return The converted exception; should <b>never</b> be null.
-	 */
-	protected JDBCException handledNonSpecificException(SQLException sqlException, String message, String sql) {
-		return new GenericJDBCException( message, sqlException, sql );
+		return null; // allow other delegates the chance to look
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/internal/OracleSQLExceptionConversionDelegate.java b/hibernate-core/src/main/java/org/hibernate/exception/internal/OracleSQLExceptionConversionDelegate.java
new file mode 100644
index 0000000000..f97fe3a0a1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/exception/internal/OracleSQLExceptionConversionDelegate.java
@@ -0,0 +1,55 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.exception.internal;
+
+import java.sql.SQLException;
+
+import org.hibernate.JDBCException;
+import org.hibernate.PessimisticLockException;
+import org.hibernate.exception.spi.AbstractSQLExceptionConversionDelegate;
+import org.hibernate.exception.spi.ConversionContext;
+
+/**
+ * A {@link org.hibernate.exception.spi.SQLExceptionConversionDelegate}
+ * implementation specific to Oracle.
+ *
+ * @author Gail Badner
+ */
+public class OracleSQLExceptionConversionDelegate extends AbstractSQLExceptionConversionDelegate {
+	private static final int PESSIMISTIC_LOCK_ERROR_CODE = 30006;
+
+	public OracleSQLExceptionConversionDelegate(ConversionContext conversionContext) {
+		super( conversionContext );
+	}
+
+	@Override
+	public JDBCException convert(SQLException sqlException, String message, String sql) {
+		if ( sqlException.getErrorCode() == PESSIMISTIC_LOCK_ERROR_CODE ) {
+			return new PessimisticLockException( message, sqlException, sql );
+		}
+		else {
+			return null; // allow other delegates the chance to look
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/internal/StandardSQLExceptionConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/internal/StandardSQLExceptionConverter.java
index 880598b199..ff3bb9ccca 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/internal/StandardSQLExceptionConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/internal/StandardSQLExceptionConverter.java
@@ -1,54 +1,56 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.exception.internal;
 
 import java.sql.SQLException;
 import java.util.ArrayList;
 
 import org.hibernate.JDBCException;
 import org.hibernate.exception.GenericJDBCException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardSQLExceptionConverter implements SQLExceptionConverter {
 	private ArrayList<SQLExceptionConversionDelegate> delegates = new ArrayList<SQLExceptionConversionDelegate>();
 
 	public void addDelegate(SQLExceptionConversionDelegate delegate) {
-		this.delegates.add( delegate );
+		if ( delegate != null ) {
+			this.delegates.add( delegate );
+		}
 	}
 
 	@Override
 	public JDBCException convert(SQLException sqlException, String message, String sql) {
 		for ( SQLExceptionConversionDelegate delegate : delegates ) {
 			final JDBCException jdbcException = delegate.convert( sqlException, message, sql );
 			if ( jdbcException != null ) {
 				return jdbcException;
 			}
 		}
 		return new GenericJDBCException( message, sqlException, sql );
 	}
 }
