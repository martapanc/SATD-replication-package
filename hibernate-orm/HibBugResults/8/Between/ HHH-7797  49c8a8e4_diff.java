diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index 702b721531..c2811e3b42 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,699 +1,694 @@
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
 import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
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
 
-	public boolean supportsUnique() {
-		// Does this dialect support the UNIQUE column syntax?
-		return true;
-	}
-
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
 		return new StringBuilder( 300 )
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
 
 		return new StringBuilder( sql.length() + 8 )
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
 
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new CacheSQLExceptionConversionDelegate( this );
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
index e4bc9bdfc8..9adc838f0b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
@@ -1,390 +1,386 @@
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
 
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 
 import java.sql.SQLException;
 import java.sql.Types;
 
 /**
  * A dialect compatible with the H2 database.
  *
  * @author Thomas Mueller
  */
 public class H2Dialect extends Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, H2Dialect.class.getName());
 
 	private final String querySequenceString;
 
 	public H2Dialect() {
 		super();
 
 		String querySequenceString = "select sequence_name from information_schema.sequences";
 		try {
 			// HHH-2300
 			final Class h2ConstantsClass = ReflectHelper.classForName( "org.h2.engine.Constants" );
 			final int majorVersion = ( Integer ) h2ConstantsClass.getDeclaredField( "VERSION_MAJOR" ).get( null );
 			final int minorVersion = ( Integer ) h2ConstantsClass.getDeclaredField( "VERSION_MINOR" ).get( null );
 			final int buildId = ( Integer ) h2ConstantsClass.getDeclaredField( "BUILD_ID" ).get( null );
 			if ( buildId < 32 ) {
 				querySequenceString = "select name from information_schema.sequences";
 			}
             if ( ! ( majorVersion > 1 || minorVersion > 2 || buildId >= 139 ) ) {
 				LOG.unsupportedMultiTableBulkHqlJpaql( majorVersion, minorVersion, buildId );
 			}
 		}
 		catch ( Exception e ) {
 			// probably H2 not in the classpath, though in certain app server environments it might just mean we are
 			// not using the correct classloader
 			LOG.undeterminedH2Version();
 		}
 
 		this.querySequenceString = querySequenceString;
 
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BINARY, "binary" );
 		registerColumnType( Types.BIT, "boolean" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
 		registerColumnType( Types.NUMERIC, "decimal($p,$s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARBINARY, "binary($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		// Aggregations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		// select topic, syntax from information_schema.help
 		// where section like 'Function%' order by section, topic
 		//
 		// see also ->  http://www.h2database.com/html/functions.html
 
 		// Numeric Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bitand", new StandardSQLFunction( "bitand", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bitor", new StandardSQLFunction( "bitor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bitxor", new StandardSQLFunction( "bitxor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "compress", new StandardSQLFunction( "compress", StandardBasicTypes.BINARY ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "decrypt", new StandardSQLFunction( "decrypt", StandardBasicTypes.BINARY ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "encrypt", new StandardSQLFunction( "encrypt", StandardBasicTypes.BINARY ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "expand", new StandardSQLFunction( "compress", StandardBasicTypes.BINARY ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "hash", new StandardSQLFunction( "hash", StandardBasicTypes.BINARY ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "round", new StandardSQLFunction( "round", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "truncate", new StandardSQLFunction( "truncate", StandardBasicTypes.DOUBLE ) );
 
 		// String Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "difference", new StandardSQLFunction( "difference", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw", StandardBasicTypes.STRING ) );
 		registerFunction( "insert", new StandardSQLFunction( "lower", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "position", new StandardSQLFunction( "position", StandardBasicTypes.INTEGER ) );
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex", StandardBasicTypes.STRING ) );
 		registerFunction( "repeat", new StandardSQLFunction( "repeat", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "stringencode", new StandardSQLFunction( "stringencode", StandardBasicTypes.STRING ) );
 		registerFunction( "stringdecode", new StandardSQLFunction( "stringdecode", StandardBasicTypes.STRING ) );
 		registerFunction( "stringtoutf8", new StandardSQLFunction( "stringtoutf8", StandardBasicTypes.BINARY ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "utf8tostring", new StandardSQLFunction( "utf8tostring", StandardBasicTypes.STRING ) );
 
 		// Time and Date Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "curtimestamp", new NoArgSQLFunction( "curtimestamp", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 
 		// System Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 
 		getDefaultProperties().setProperty( AvailableSettings.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		getDefaultProperties().setProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true" );  // http://code.google.com/p/h2database/issues/detail?id=235
 	}
 
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public String getIdentityColumnString() {
 		return "generated by default as identity"; // not null is implicit
 	}
 
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	public String getIdentityInsertString() {
 		return "null";
 	}
 
 	public String getForUpdateString() {
 		return " for update";
 	}
 
-	public boolean supportsUnique() {
-		return true;
-	}
-
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		return new StringBuilder( sql.length() + 20 )
 				.append( sql )
 				.append( hasOffset ? " limit ? offset ?" : " limit ?" )
 				.toString();
 	}
 
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	public String getQuerySequencesString() {
 		return querySequenceString;
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 			// 23000: Check constraint violation: {0}
 			// 23001: Unique index or primary key violation: {0}
 			if ( sqle.getSQLState().startsWith( "23" ) ) {
 				final String message = sqle.getMessage();
 				int idx = message.indexOf( "violation: " );
 				if ( idx > 0 ) {
 					constraintName = message.substring( idx + "violation: ".length() );
 				}
 			}
 			return constraintName;
 		}
 	};
 	
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		SQLExceptionConversionDelegate delegate = super.buildSQLExceptionConversionDelegate();
 		if (delegate == null) {
 			delegate = new SQLExceptionConversionDelegate() {
 
 				@Override
 				public JDBCException convert(SQLException sqlException, String message, String sql) {
                     JDBCException exception = null;
 
                     int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
 
                     if (40001 == errorCode) { // DEADLOCK DETECTED
                         exception = new LockAcquisitionException(message, sqlException, sql);
                     }
 
                     if (50200 == errorCode) { // LOCK NOT AVAILABLE
                         exception = new PessimisticLockException(message, sqlException, sql);
                     }
 
 					return exception;
 				}
 			};
 		}
 		return delegate;
 	}
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create cached local temporary table if not exists";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		// actually 2 different options are specified here:
 		//		1) [on commit drop] - says to drop the table on transaction commit
 		//		2) [transactional] - says to not perform an implicit commit of any current transaction
 		return "on commit drop transactional";
 	}
 
 	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		// explicitly create the table using the same connection and transaction
 		return Boolean.FALSE;
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp()";
 	}
 
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		// see http://groups.google.com/group/h2-database/browse_thread/thread/562d8a49e2dabe99?hl=en
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
index ca64af3dcd..6c387e6c9e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
@@ -1,714 +1,710 @@
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
 
 import java.io.Serializable;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 
 /**
  * An SQL dialect compatible with HSQLDB (HyperSQL).
  * <p/>
  * Note this version supports HSQLDB version 1.8 and higher, only.
  * <p/>
  * Enhancements to version 3.5.0 GA to provide basic support for both HSQLDB 1.8.x and 2.x
  * Does not works with Hibernate 3.2 - 3.4 without alteration.
  *
  * @author Christoph Sturm
  * @author Phillip Baird
  * @author Fred Toussi
  */
 public class HSQLDialect extends Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HSQLDialect.class.getName());
 
 	/**
 	 * version is 18 for 1.8 or 20 for 2.0
 	 */
 	private int hsqldbVersion = 18;
 
 
 	public HSQLDialect() {
 		super();
 
 		try {
 			Class props = ReflectHelper.classForName( "org.hsqldb.persist.HsqlDatabaseProperties" );
 			String versionString = (String) props.getDeclaredField( "THIS_VERSION" ).get( null );
 
 			hsqldbVersion = Integer.parseInt( versionString.substring( 0, 1 ) ) * 10;
 			hsqldbVersion += Integer.parseInt( versionString.substring( 2, 3 ) );
 		}
 		catch ( Throwable e ) {
 			// must be a very old version
 		}
 
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.BIT, "bit" );
         registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 
 		registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 
 		if ( hsqldbVersion < 20 ) {
 			registerColumnType( Types.NUMERIC, "numeric" );
 		}
 		else {
 			registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		}
 
 		//HSQL has no Blob/Clob support .... but just put these here for now!
 		if ( hsqldbVersion < 20 ) {
 			registerColumnType( Types.BLOB, "longvarbinary" );
 			registerColumnType( Types.CLOB, "longvarchar" );
 		}
 		else {
 			registerColumnType( Types.BLOB, "blob($l)" );
 			registerColumnType( Types.CLOB, "clob($l)" );
 		}
 
 		// aggregate functions
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		// string functions
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as varchar(256))" ) );
 		registerFunction( "to_char", new StandardSQLFunction( "to_char" ) );
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex" ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw" ) );
 
 		// system functions
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 
 		// datetime functions
 		if ( hsqldbVersion < 20 ) {
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 		} else {
 		    registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		}
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false )
 		);
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "cast(second(?1) as int)" ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 
 		// numeric functions
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new StandardSQLFunction( "rand", StandardBasicTypes.FLOAT ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic" ) );
 		registerFunction( "truncate", new StandardSQLFunction( "truncate" ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		// special functions
 		// from v. 2.2.0 ROWNUM() is supported in all modes as the equivalent of Oracle ROWNUM
 		if ( hsqldbVersion > 21 ) {
 		    registerFunction("rownum",
 				     new NoArgSQLFunction("rownum", StandardBasicTypes.INTEGER));
 		}
 
 		// function templates
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public String getIdentityColumnString() {
 		return "generated by default as identity (start with 1)"; //not null is implicit
 	}
 
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	public String getIdentityInsertString() {
 		return hsqldbVersion < 20 ? "null" : "default";
 	}
 
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 
 	public String getForUpdateString() {
 		if ( hsqldbVersion >= 20 ) {
 			return " for update";
 		}
 		else {
 			return "";
 		}
 	}
 
-	public boolean supportsUnique() {
-		return false;
-	}
-
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hsqldbVersion < 20 ) {
 			return new StringBuilder( sql.length() + 10 )
 					.append( sql )
 					.insert(
 							sql.toLowerCase().indexOf( "select" ) + 6,
 							hasOffset ? " limit ? ?" : " top ?"
 					)
 					.toString();
 		}
 		else {
 			return new StringBuilder( sql.length() + 20 )
 					.append( sql )
 					.append( hasOffset ? " offset ? limit ?" : " limit ?" )
 					.toString();
 		}
 	}
 
 	public boolean bindLimitParametersFirst() {
 		return hsqldbVersion < 20;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	public boolean supportsColumnCheck() {
 		return hsqldbVersion >= 20;
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	protected String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	protected String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	public String getQuerySequencesString() {
 		// this assumes schema support, which is present in 1.8.0 and later...
 		return "select sequence_name from information_schema.system_sequences";
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return hsqldbVersion < 20 ? EXTRACTER_18 : EXTRACTER_20;
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
 			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -8 ) {
 				constraintName = extractUsingTemplate(
 						"Integrity constraint violation ", " table:", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -9 ) {
 				constraintName = extractUsingTemplate(
 						"Violation of unique index: ", " in statement [", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -104 ) {
 				constraintName = extractUsingTemplate(
 						"Unique constraint violation: ", " in statement [", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -177 ) {
 				constraintName = extractUsingTemplate(
 						"Integrity constraint violation - no parent ", " table:",
 						sqle.getMessage()
 				);
 			}
 			return constraintName;
 		}
 
 	};
 
 	/**
 	 * HSQLDB 2.0 messages have changed
 	 * messages may be localized - therefore use the common, non-locale element " table: "
 	 */
 	private static ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter() {
 
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
 			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -8 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -9 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -104 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -177 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			return constraintName;
 		}
 	};
 
 	public String getSelectClauseNullString(int sqlType) {
 		String literal;
 		switch ( sqlType ) {
 		        case Types.LONGVARCHAR:
 			case Types.VARCHAR:
 			case Types.CHAR:
 				literal = "cast(null as varchar(100))";
 				break;
 		        case Types.LONGVARBINARY:
 		        case Types.VARBINARY:
 		        case Types.BINARY:
 				literal = "cast(null as varbinary(100))";
 				break;
 		        case Types.CLOB:
 				literal = "cast(null as clob)";
 				break;
 		        case Types.BLOB:
 				literal = "cast(null as blob)";
 				break;
 			case Types.DATE:
 				literal = "cast(null as date)";
 				break;
 			case Types.TIMESTAMP:
 				literal = "cast(null as timestamp)";
 				break;
 		        case Types.BOOLEAN:
 				literal = "cast(null as boolean)";
 				break;
 		        case Types.BIT:
 				literal = "cast(null as bit)";
 				break;
 			case Types.TIME:
 				literal = "cast(null as time)";
 				break;
 			default:
 				literal = "cast(null as int)";
 		}
 		return literal;
 	}
 
     public boolean supportsUnionAll() {
         return true;
     }
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Hibernate uses this information for temporary tables that it uses for its own operations
 	// therefore the appropriate strategy is taken with different versions of HSQLDB
 
 	// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
 	// definition is shared by all users but data is private to the session
 	// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
 	// the definition and data is private to the session and table declaration
 	// can happen in the middle of a transaction
 
 	/**
 	 * Does this dialect support temporary tables?
 	 *
 	 * @return True if temp tables are supported; false otherwise.
 	 */
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	/**
 	 * With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
 	 * statement (in-case there is a global name beginning with HT_)
 	 *
 	 * @param baseTableName The table name from which to base the temp table name.
 	 *
 	 * @return The generated temp table name.
 	 */
 	public String generateTemporaryTableName(String baseTableName) {
 		if ( hsqldbVersion < 20 ) {
 			return "HT_" + baseTableName;
 		}
 		else {
 			return "MODULE.HT_" + baseTableName;
 		}
 	}
 
 	/**
 	 * Command used to create a temporary table.
 	 *
 	 * @return The command used to create a temporary table.
 	 */
 	public String getCreateTemporaryTableString() {
 		if ( hsqldbVersion < 20 ) {
 			return "create global temporary table";
 		}
 		else {
 			return "declare local temporary table";
 		}
 	}
 
 	/**
 	 * No fragment is needed if data is not needed beyond commit, otherwise
 	 * should add "on commit preserve rows"
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
 	 * Different behavior for GLOBAL TEMPORARY (1.8) and LOCAL TEMPORARY (2.0)
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
 		if ( hsqldbVersion < 20 ) {
 			return Boolean.TRUE;
 		}
 		else {
 			return Boolean.FALSE;
 		}
 	}
 
 	/**
 	 * Do we need to drop the temporary table after use?
 	 *
 	 * todo - clarify usage by Hibernate
 	 *
 	 * Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
 	 * of the session (by default, data is cleared at commit).<p>
 	 *
 	 * Version 2.x LOCAL TEMPORARY table definitions do not persist beyond
 	 * the end of the session (by default, data is cleared at commit).
 	 *
 	 * @return True if the table should be dropped.
 	 */
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * HSQLDB 1.8.x requires CALL CURRENT_TIMESTAMP but this should not
 	 * be treated as a callable statement. It is equivalent to
 	 * "select current_timestamp from dual" in some databases.
 	 * HSQLDB 2.0 also supports VALUES CURRENT_TIMESTAMP
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...<p>
 	 *
 	 * CALL CURRENT_TIMESTAMP is used but this should not
 	 * be treated as a callable statement.
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 *         is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp";
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
 
 	/**
 	 * For HSQLDB 2.0, this is a copy of the base class implementation.
 	 * For HSQLDB 1.8, only READ_UNCOMMITTED is supported.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 *
 	 * @return The appropriate locking strategy.
 	 *
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 
 		if ( hsqldbVersion < 20 ) {
 			return new ReadUncommittedLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	public static class ReadUncommittedLockingStrategy extends SelectLockingStrategy {
 		public ReadUncommittedLockingStrategy(Lockable lockable, LockMode lockMode) {
 			super( lockable, lockMode );
 		}
 
 		public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
 				throws StaleObjectStateException, JDBCException {
 			if ( getLockMode().greaterThan( LockMode.READ ) ) {
 				LOG.hsqldbSupportsOnlyReadCommittedIsolation();
 			}
 			super.lock( id, version, object, timeout, session );
 		}
 	}
 
 	public boolean supportsCommentOn() {
 		return hsqldbVersion >= 20;
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	/**
 	 * todo - needs usage clarification
 	 *
 	 * If the SELECT statement is always part of a UNION, then the type of
 	 * parameter is resolved by v. 2.0, but not v. 1.8 (assuming the other
 	 * SELECT in the UNION has a column reference in the same position and
 	 * can be type-resolved).
 	 *
 	 * On the other hand if the SELECT statement is isolated, all versions of
 	 * HSQLDB require casting for "select ? from .." to work.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 *
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return hsqldbVersion >= 20;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return hsqldbVersion >= 20;
 	}
 
 
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
     public String toBooleanValueString(boolean bool) {
         return String.valueOf( bool );
     }
 
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
index a7dbaf194c..e5b9f8a20e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
@@ -1,366 +1,359 @@
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
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Ingres 9.2.
  * <p/>
  * Known limitations:
  * <ul>
  * <li> Only supports simple constants or columns on the left side of an IN, making (1,2,3) in (...) or (&lt;subselect&gt;) in (...) non-supported.
  * <li> Supports only 39 digits in decimal.
  * <li> Explicitly set USE_GET_GENERATED_KEYS property to false.
  * <li> Perform string casts to varchar; removes space padding.
  * </ul>
  * 
  * @author Ian Booth
  * @author Bruce Lunsford
  * @author Max Rydahl Andersen
  * @author Raymond Fan
  */
 public class IngresDialect extends Dialect {
 
 	public IngresDialect() {
 		super();
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.NUMERIC, "decimal($p, $s)" );
 		registerColumnType( Types.DECIMAL, "decimal($p, $s)" );
 		registerColumnType( Types.BINARY, 32000, "byte($l)" );
 		registerColumnType( Types.BINARY, "long byte" );
 		registerColumnType( Types.VARBINARY, 32000, "varbyte($l)" );
 		registerColumnType( Types.VARBINARY, "long byte" );
 		registerColumnType( Types.LONGVARBINARY, "long byte" );
 		registerColumnType( Types.CHAR, 32000, "char($l)" );
 		registerColumnType( Types.VARCHAR, 32000, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, "long varchar" );
 		registerColumnType( Types.LONGVARCHAR, "long varchar" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time with time zone" );
 		registerColumnType( Types.TIMESTAMP, "timestamp with time zone" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_add", new StandardSQLFunction( "bit_add" ) );
 		registerFunction( "bit_and", new StandardSQLFunction( "bit_and" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "octet_length(hex(?1))*4" ) );
 		registerFunction( "bit_not", new StandardSQLFunction( "bit_not" ) );
 		registerFunction( "bit_or", new StandardSQLFunction( "bit_or" ) );
 		registerFunction( "bit_xor", new StandardSQLFunction( "bit_xor" ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
 		registerFunction( "charextract", new StandardSQLFunction( "charextract", StandardBasicTypes.STRING ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "+", ")" ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "current_user", new NoArgSQLFunction( "current_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dba", new NoArgSQLFunction( "dba", StandardBasicTypes.STRING, true ) );
 		registerFunction( "dow", new StandardSQLFunction( "dow", StandardBasicTypes.STRING ) );
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "date_part('?1', ?3)" ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "gmt_timestamp", new StandardSQLFunction( "gmt_timestamp", StandardBasicTypes.STRING ) );
 		registerFunction( "hash", new StandardSQLFunction( "hash", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "initial_user", new NoArgSQLFunction( "initial_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "intextract", new StandardSQLFunction( "intextract", StandardBasicTypes.INTEGER ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.LONG, "locate(?1, ?2)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.LONG ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "lowercase", new StandardSQLFunction( "lowercase" ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
 		registerFunction( "pad", new StandardSQLFunction( "pad", StandardBasicTypes.STRING ) );
 		registerFunction( "position", new StandardSQLFunction( "position", StandardBasicTypes.LONG ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "random", new NoArgSQLFunction( "random", StandardBasicTypes.LONG, true ) );
 		registerFunction( "randomf", new NoArgSQLFunction( "randomf", StandardBasicTypes.DOUBLE, true ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "session_user", new NoArgSQLFunction( "session_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "size", new NoArgSQLFunction( "size", StandardBasicTypes.LONG, true ) );
 		registerFunction( "squeeze", new StandardSQLFunction( "squeeze" ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1 FROM ?2 FOR ?3)" ) );
 		registerFunction( "system_user", new NoArgSQLFunction( "system_user", StandardBasicTypes.STRING, false ) );
 		//registerFunction( "trim", new StandardSQLFunction( "trim", StandardBasicTypes.STRING ) );
 		registerFunction( "unhex", new StandardSQLFunction( "unhex", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "uppercase", new StandardSQLFunction( "uppercase" ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "usercode", new NoArgSQLFunction( "usercode", StandardBasicTypes.STRING, true ) );
 		registerFunction( "username", new NoArgSQLFunction( "username", StandardBasicTypes.STRING, true ) );
 		registerFunction( "uuid_create", new StandardSQLFunction( "uuid_create", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_compare", new StandardSQLFunction( "uuid_compare", StandardBasicTypes.INTEGER ) );
 		registerFunction( "uuid_from_char", new StandardSQLFunction( "uuid_from_char", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_to_char", new StandardSQLFunction( "uuid_to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		// Casting to char of numeric values introduces space padding up to the
 		// maximum width of a value for that return type.  Casting to varchar
 		// does not introduce space padding.
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
         // Ingres driver supports getGeneratedKeys but only in the following
         // form:
         // The Ingres DBMS returns only a single table key or a single object
         // key per insert statement. Ingres does not return table and object
         // keys for INSERT AS SELECT statements. Depending on the keys that are
         // produced by the statement executed, auto-generated key parameters in
         // execute(), executeUpdate(), and prepareStatement() methods are
         // ignored and getGeneratedKeys() returns a result-set containing no
         // rows, a single row with one column, or a single row with two columns.
         // Ingres JDBC Driver returns table and object keys as BINARY values.
         getDefaultProperties().setProperty(Environment.USE_GET_GENERATED_KEYS, "false");
         // There is no support for a native boolean type that accepts values
         // of true, false or unknown. Using the tinyint type requires
         // substitions of true and false.
         getDefaultProperties().setProperty(Environment.QUERY_SUBSTITUTIONS, "true=1,false=0");
 	}
 	/**
 	 * Expression for created UUID string
 	 */
 	public String getSelectGUIDString() {
 		return "select uuid_to_char(uuid_create())";
 	}
 	/**
 	 * Do we need to drop constraints before dropping tables in this dialect?
 	 *
 	 * @return boolean
 	 */
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE OF</tt>, allowing
 	 * particular rows to be locked?
 	 *
 	 * @return True (Ingres does support "for update of" syntax...)
 	 */
 	public boolean supportsForUpdateOf() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 */
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return " with null";
 	}
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	/**
 	 * The syntax that fetches the next value of a sequence, if sequences are supported.
 	 *
 	 * @param sequenceName the name of the sequence
 	 *
 	 * @return String
 	 */
 	public String getSequenceNextValString(String sequenceName) {
 		return "select nextval for " + sequenceName;
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	/**
 	 * The syntax used to create a sequence, if sequences are supported.
 	 *
 	 * @param sequenceName the name of the sequence
 	 *
 	 * @return String
 	 */
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	/**
 	 * The syntax used to drop a sequence, if sequences are supported.
 	 *
 	 * @param sequenceName the name of the sequence
 	 *
 	 * @return String
 	 */
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	/**
 	 * A query used to find all sequences
 	 */
 	public String getQuerySequencesString() {
 		return "select seq_name from iisequence";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return String
 	 */
 	public String getLowercaseFunction() {
 		return "lowercase";
 	}
 
 	/**
 	 * Does this <tt>Dialect</tt> have some kind of <tt>LIMIT</tt> syntax?
 	 */
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support an offset?
 	 */
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	/**
 	 * Add a <tt>LIMIT</tt> clause to the given SQL <tt>SELECT</tt>
 	 *
 	 * @return the modified SQL
 	 */
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 16 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 */
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	/**
-	 * Ingres explicitly needs "unique not null", because "with null" is default
-	 */
-	public boolean supportsNotNullUnique() {
-		return false;
-	}
-
-	/**
 	 * Does this dialect support temporary tables?
 	 */
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "declare global temporary table";
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit preserve rows with norecovery";
 	}
 
 	public String generateTemporaryTableName(String baseTableName) {
 		return "session." + super.generateTemporaryTableName( baseTableName );
 	}
 
 
 	/**
 	 * Expression for current_timestamp
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		return "date(now)";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsSubselectAsInPredicateLHS() {
 		return false;
 	}
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	public boolean supportsExpectedLobUsagePattern () {
 		return false;
 	}
 
 	/**
 	 * Ingres does not support the syntax `count(distinct a,b)`?
 	 *
 	 * @return False, not supported.
 	 */
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
index 6d45c1c6d5..ccec37a7c7 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
@@ -1,365 +1,358 @@
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
 import java.sql.Types;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * This is the Hibernate dialect for the Unisys 2200 Relational Database (RDMS).
  * This dialect was developed for use with Hibernate 3.0.5. Other versions may
  * require modifications to the dialect.
  *
  * Version History:
  * Also change the version displayed below in the constructor
  * 1.1
  * 1.0  2005-10-24  CDH - First dated version for use with CP 11
  *
  * @author Ploski and Hanson
  */
 public class RDMSOS2200Dialect extends Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, RDMSOS2200Dialect.class.getName());
 
 	public RDMSOS2200Dialect() {
 		super();
 		// Display the dialect version.
 		LOG.rdmsOs2200Dialect();
 
         /**
          * This section registers RDMS Built-in Functions (BIFs) with Hibernate.
          * The first parameter is the 'register' function name with Hibernate.
          * The second parameter is the defined RDMS SQL Function and it's
          * characteristics. If StandardSQLFunction(...) is used, the RDMS BIF
          * name and the return type (if any) is specified.  If
          * SQLFunctionTemplate(...) is used, the return type and a template
          * string is provided, plus an optional hasParenthesesIfNoArgs flag.
          */
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 		registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.INTEGER) );
 		registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.INTEGER) );
 
 		// The RDMS concat() function only supports 2 parameters
 		registerFunction( "concat", new SQLFunctionTemplate(StandardBasicTypes.STRING, "concat(?1, ?2)") );
 		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.STRING) );
 		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
 		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
 		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 
 		registerFunction("lcase", new StandardSQLFunction("lcase") );
 		registerFunction("lower", new StandardSQLFunction("lower") );
 		registerFunction("ltrim", new StandardSQLFunction("ltrim") );
 		registerFunction("reverse", new StandardSQLFunction("reverse") );
 		registerFunction("rtrim", new StandardSQLFunction("rtrim") );
 
 		// RDMS does not directly support the trim() function, we use rtrim() and ltrim()
 		registerFunction("trim", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "ltrim(rtrim(?1))" ) );
 		registerFunction("soundex", new StandardSQLFunction("soundex") );
 		registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING) );
 		registerFunction("ucase", new StandardSQLFunction("ucase") );
 		registerFunction("upper", new StandardSQLFunction("upper") );
 
 		registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction("cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
 		registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
 		registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
 		registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE) );
 		registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE) );
 		registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE) );
 		registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction("sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
 		registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction("tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction("curdate", new NoArgSQLFunction("curdate",StandardBasicTypes.DATE) );
 		registerFunction("curtime", new NoArgSQLFunction("curtime",StandardBasicTypes.TIME) );
 		registerFunction("days", new StandardSQLFunction("days",StandardBasicTypes.INTEGER) );
 		registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth",StandardBasicTypes.INTEGER) );
 		registerFunction("dayname", new StandardSQLFunction("dayname",StandardBasicTypes.STRING) );
 		registerFunction("dayofweek", new StandardSQLFunction("dayofweek",StandardBasicTypes.INTEGER) );
 		registerFunction("dayofyear", new StandardSQLFunction("dayofyear",StandardBasicTypes.INTEGER) );
 		registerFunction("hour", new StandardSQLFunction("hour",StandardBasicTypes.INTEGER) );
 		registerFunction("last_day", new StandardSQLFunction("last_day",StandardBasicTypes.DATE) );
 		registerFunction("microsecond", new StandardSQLFunction("microsecond",StandardBasicTypes.INTEGER) );
 		registerFunction("minute", new StandardSQLFunction("minute",StandardBasicTypes.INTEGER) );
 		registerFunction("month", new StandardSQLFunction("month",StandardBasicTypes.INTEGER) );
 		registerFunction("monthname", new StandardSQLFunction("monthname",StandardBasicTypes.STRING) );
 		registerFunction("now", new NoArgSQLFunction("now",StandardBasicTypes.TIMESTAMP) );
 		registerFunction("quarter", new StandardSQLFunction("quarter",StandardBasicTypes.INTEGER) );
 		registerFunction("second", new StandardSQLFunction("second",StandardBasicTypes.INTEGER) );
 		registerFunction("time", new StandardSQLFunction("time",StandardBasicTypes.TIME) );
 		registerFunction("timestamp", new StandardSQLFunction("timestamp",StandardBasicTypes.TIMESTAMP) );
 		registerFunction("week", new StandardSQLFunction("week",StandardBasicTypes.INTEGER) );
 		registerFunction("year", new StandardSQLFunction("year",StandardBasicTypes.INTEGER) );
 
 		registerFunction("atan2", new StandardSQLFunction("atan2",StandardBasicTypes.DOUBLE) );
 		registerFunction( "mod", new StandardSQLFunction("mod",StandardBasicTypes.INTEGER) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.DOUBLE) );
 
 		/**
 		 * For a list of column types to register, see section A-1
 		 * in 7862 7395, the Unisys JDBC manual.
 		 *
 		 * Here are column sizes as documented in Table A-1 of
 		 * 7831 0760, "Enterprise Relational Database Server
 		 * for ClearPath OS2200 Administration Guide"
 		 * Numeric - 21
 		 * Decimal - 22 (21 digits plus one for sign)
 		 * Float   - 60 bits
 		 * Char    - 28000
 		 * NChar   - 14000
 		 * BLOB+   - 4294967296 (4 Gb)
 		 * + RDMS JDBC driver does not support BLOBs
 		 *
 		 * DATE, TIME and TIMESTAMP literal formats are
 		 * are all described in section 2.3.4 DATE Literal Format
 		 * in 7830 8160.
 		 * The DATE literal format is: YYYY-MM-DD
 		 * The TIME literal format is: HH:MM:SS[.[FFFFFF]]
 		 * The TIMESTAMP literal format is: YYYY-MM-DD HH:MM:SS[.[FFFFFF]]
 		 *
 		 * Note that $l (dollar-L) will use the length value if provided.
 		 * Also new for Hibernate3 is the $p percision and $s (scale) parameters
 		 */
 		registerColumnType(Types.BIT, "SMALLINT");
 		registerColumnType(Types.TINYINT, "SMALLINT");
 		registerColumnType(Types.BIGINT, "NUMERIC(21,0)");
 		registerColumnType(Types.SMALLINT, "SMALLINT");
 		registerColumnType(Types.CHAR, "CHARACTER(1)");
 		registerColumnType(Types.DOUBLE, "DOUBLE PRECISION");
 		registerColumnType(Types.FLOAT, "FLOAT");
 		registerColumnType(Types.REAL, "REAL");
 		registerColumnType(Types.INTEGER, "INTEGER");
 		registerColumnType(Types.NUMERIC, "NUMERIC(21,$l)");
 		registerColumnType(Types.DECIMAL, "NUMERIC(21,$l)");
 		registerColumnType(Types.DATE, "DATE");
 		registerColumnType(Types.TIME, "TIME");
 		registerColumnType(Types.TIMESTAMP, "TIMESTAMP");
 		registerColumnType(Types.VARCHAR, "CHARACTER($l)");
         registerColumnType(Types.BLOB, "BLOB($l)" );
         /*
          * The following types are not supported in RDMS/JDBC and therefore commented out.
          * However, in some cases, mapping them to CHARACTER columns works
          * for many applications, but does not work for all cases.
          */
         // registerColumnType(Types.VARBINARY, "CHARACTER($l)");
         // registerColumnType(Types.BLOB, "CHARACTER($l)" );  // For use prior to CP 11.0
         // registerColumnType(Types.CLOB, "CHARACTER($l)" );
 	}
 
 
 	// Dialect method overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
     /**
      * RDMS does not support qualifing index names with the schema name.
      */
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	/**
 	 * The RDMS DB supports the 'FOR UPDATE OF' clause. However, the RDMS-JDBC
      * driver does not support this feature, so a false is return.
      * The base dialect also returns a false, but we will leave this over-ride
      * in to make sure it stays false.
 	 */
 	public boolean forUpdateOfColumns() {
 		return false;
 	}
 
 	/**
 	 * Since the RDMS-JDBC driver does not support for updates, this string is
      * set to an empty string. Whenever, the driver does support this feature,
      * the returned string should be " FOR UPDATE OF". Note that RDMS does not
      * support the string 'FOR UPDATE' string.
 	 */
 	public String getForUpdateString() {
 		return ""; // Original Dialect.java returns " for update";
 	}
 
-    /**
-     * RDMS does not support adding Unique constraints via create and alter table.
-     */
-	public boolean supportsUniqueConstraintInCreateAlterTable() {
-	    return true;
-	}
-
 	// Verify the state of this new method in Hibernate 3.0 Dialect.java
     /**
      * RDMS does not support Cascade Deletes.
      * Need to review this in the future when support is provided.
      */
 	public boolean supportsCascadeDelete() {
 		return false; // Origial Dialect.java returns true;
 	}
 
 	/**
      * Currently, RDMS-JDBC does not support ForUpdate.
      * Need to review this in the future when support is provided.
 	 */
     public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
     // *** Sequence methods - start. The RDMS dialect needs these
 
     // methods to make it possible to use the Native Id generator
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 	    // The where clause was added to eliminate this statement from Brute Force Searches.
         return  "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
         // We must return a valid RDMS/RSA command from this method to
         // prevent RDMS/RSA from issuing *ERROR 400
         return "";
 	}
 
 	public String getDropSequenceString(String sequenceName) {
         // We must return a valid RDMS/RSA command from this method to
         // prevent RDMS/RSA from issuing *ERROR 400
         return "";
 	}
 
 	// *** Sequence methods - end
 
     public String getCascadeConstraintsString() {
         // Used with DROP TABLE to delete all records in the table.
         return " including contents";
     }
 
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
     public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( sql.length() + 40 )
 				.append( sql )
 				.append( " fetch first " )
 				.append( limit )
 				.append( " rows only " )
 				.toString();
 	}
 
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	public boolean supportsUnionAll() {
 		// RDMS supports the UNION ALL clause.
           return true;
 	}
 
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// RDMS has no known variation of a "SELECT ... FOR UPDATE" syntax...
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
index 423ced7cb7..01f8d25776 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SybaseASE15Dialect.java
@@ -1,442 +1,438 @@
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
 
 import java.sql.Types;
 
 import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor;
 
 /**
  * An SQL dialect targeting Sybase Adaptive Server Enterprise (ASE) 15 and higher.
  * <p/>
  * TODO : verify if this also works with 12/12.5
  *
  * @author Gavin King
  */
 public class SybaseASE15Dialect extends SybaseDialect {
 	public SybaseASE15Dialect() {
 		super();
 
 		registerColumnType( Types.LONGVARBINARY, "image" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "numeric($p,$s)" );
 		registerColumnType( Types.TIME, "time" );
         registerColumnType( Types.REAL, "real" );
         registerColumnType( Types.BOOLEAN, "tinyint" );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(second, ?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(minute, ?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(hour, ?1)" ) );
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(?1, ?3)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1 % ?2" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1) * 8" ) );
 		registerFunction(
 				"trim", new AnsiTrimEmulationFunction(
 						AnsiTrimEmulationFunction.LTRIM, AnsiTrimEmulationFunction.RTRIM, "str_replace"
 				)
 		);
 
 		registerFunction( "atan2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "atn2(?1, ?2" ) );
 		registerFunction( "atn2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "atn2(?1, ?2" ) );
 
 		registerFunction( "biginttohex", new SQLFunctionTemplate( StandardBasicTypes.STRING, "biginttohext(?1)" ) );
 		registerFunction( "char_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "char_length(?1)" ) );
 		registerFunction( "charindex", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "charindex(?1, ?2)" ) );
 		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
 		registerFunction( "col_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "col_length(?1, ?2)" ) );
 		registerFunction( "col_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "col_name(?1, ?2)" ) );
 		// Sybase has created current_date and current_time inplace of getdate()
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE ) );
 
 
 		registerFunction( "data_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "data_pages(?1, ?2)" ) );
 		registerFunction(
 				"data_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "data_pages(?1, ?2, ?3)" )
 		);
 		registerFunction(
 				"data_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "data_pages(?1, ?2, ?3, ?4)" )
 		);
 		registerFunction( "datalength", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1)" ) );
 		registerFunction( "dateadd", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "dateadd" ) );
 		registerFunction( "datediff", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datediff" ) );
 		registerFunction( "datepart", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart" ) );
 		registerFunction( "datetime", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "datetime" ) );
 		registerFunction( "db_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "db_id(?1)" ) );
 		registerFunction( "difference", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "difference(?1,?2)" ) );
 		registerFunction( "db_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "db_name(?1)" ) );
 		registerFunction( "has_role", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "has_role(?1, ?2)" ) );
 		registerFunction( "hextobigint", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "hextobigint(?1)" ) );
 		registerFunction( "hextoint", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "hextoint(?1)" ) );
 		registerFunction( "host_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "host_id" ) );
 		registerFunction( "host_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "host_name" ) );
 		registerFunction( "inttohex", new SQLFunctionTemplate( StandardBasicTypes.STRING, "inttohex(?1)" ) );
 		registerFunction( "is_quiesced", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "is_quiesced(?1)" ) );
 		registerFunction(
 				"is_sec_service_on", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "is_sec_service_on(?1)" )
 		);
 		registerFunction( "object_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "object_id(?1)" ) );
 		registerFunction( "object_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "object_name(?1)" ) );
 		registerFunction( "pagesize", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "pagesize(?1)" ) );
 		registerFunction( "pagesize", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "pagesize(?1, ?2)" ) );
 		registerFunction( "pagesize", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "pagesize(?1, ?2, ?3)" ) );
 		registerFunction(
 				"partition_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "partition_id(?1, ?2)" )
 		);
 		registerFunction(
 				"partition_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "partition_id(?1, ?2, ?3)" )
 		);
 		registerFunction(
 				"partition_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "partition_name(?1, ?2)" )
 		);
 		registerFunction(
 				"partition_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "partition_name(?1, ?2, ?3)" )
 		);
 		registerFunction( "patindex", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "patindex" ) );
 		registerFunction( "proc_role", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "proc_role" ) );
 		registerFunction( "role_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "role_name" ) );
 		// check return type
 		registerFunction( "row_count", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "row_count" ) );
 		registerFunction( "rand2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "rand2(?1)" ) );
 		registerFunction( "rand2", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "rand2" ) );
 		registerFunction( "replicate", new SQLFunctionTemplate( StandardBasicTypes.STRING, "replicate(?1,?2)" ) );
 		registerFunction( "role_contain", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "role_contain" ) );
 		registerFunction( "role_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "role_id" ) );
 		registerFunction( "reserved_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "reserved_pages" ) );
 		registerFunction( "right", new SQLFunctionTemplate( StandardBasicTypes.STRING, "right" ) );
 		registerFunction( "show_role", new SQLFunctionTemplate( StandardBasicTypes.STRING, "show_role" ) );
 		registerFunction(
 				"show_sec_services", new SQLFunctionTemplate( StandardBasicTypes.STRING, "show_sec_services" )
 		);
 		registerFunction( "sortkey", new VarArgsSQLFunction( StandardBasicTypes.BINARY, "sortkey(", ",", ")" ) );
 		registerFunction( "soundex", new SQLFunctionTemplate( StandardBasicTypes.STRING, "sounded" ) );
 		registerFunction( "stddev", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "stddev" ) );
 		registerFunction( "stddev_pop", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "stddev_pop" ) );
 		registerFunction( "stddev_samp", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "stddev_samp" ) );
 		registerFunction( "stuff", new SQLFunctionTemplate( StandardBasicTypes.STRING, "stuff" ) );
 		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
 		registerFunction( "suser_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "suser_id" ) );
 		registerFunction( "suser_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "suser_name" ) );
 		registerFunction( "tempdb_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "tempdb_id" ) );
 		registerFunction( "textvalid", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "textvalid" ) );
 		registerFunction( "to_unichar", new SQLFunctionTemplate( StandardBasicTypes.STRING, "to_unichar(?1)" ) );
 		registerFunction(
 				"tran_dumptable_status",
 				new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "ran_dumptable_status(?1)" )
 		);
 		registerFunction( "uhighsurr", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "uhighsurr" ) );
 		registerFunction( "ulowsurr", new SQLFunctionTemplate( StandardBasicTypes.BOOLEAN, "ulowsurr" ) );
 		registerFunction( "uscalar", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "uscalar" ) );
 		registerFunction( "used_pages", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "used_pages" ) );
 		registerFunction( "user_id", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "user_id" ) );
 		registerFunction( "user_name", new SQLFunctionTemplate( StandardBasicTypes.STRING, "user_name" ) );
 		registerFunction( "valid_name", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "valid_name" ) );
 		registerFunction( "valid_user", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "valid_user" ) );
 		registerFunction( "variance", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "variance" ) );
 		registerFunction( "var_pop", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "var_pop" ) );
 		registerFunction( "var_samp", new SQLFunctionTemplate( StandardBasicTypes.DOUBLE, "var_samp" ) );
         registerFunction( "sysdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP) );
 
 		registerSybaseKeywords();
 	}
 
 	private void registerSybaseKeywords() {
 		registerKeyword( "add" );
 		registerKeyword( "all" );
 		registerKeyword( "alter" );
 		registerKeyword( "and" );
 		registerKeyword( "any" );
 		registerKeyword( "arith_overflow" );
 		registerKeyword( "as" );
 		registerKeyword( "asc" );
 		registerKeyword( "at" );
 		registerKeyword( "authorization" );
 		registerKeyword( "avg" );
 		registerKeyword( "begin" );
 		registerKeyword( "between" );
 		registerKeyword( "break" );
 		registerKeyword( "browse" );
 		registerKeyword( "bulk" );
 		registerKeyword( "by" );
 		registerKeyword( "cascade" );
 		registerKeyword( "case" );
 		registerKeyword( "char_convert" );
 		registerKeyword( "check" );
 		registerKeyword( "checkpoint" );
 		registerKeyword( "close" );
 		registerKeyword( "clustered" );
 		registerKeyword( "coalesce" );
 		registerKeyword( "commit" );
 		registerKeyword( "compute" );
 		registerKeyword( "confirm" );
 		registerKeyword( "connect" );
 		registerKeyword( "constraint" );
 		registerKeyword( "continue" );
 		registerKeyword( "controlrow" );
 		registerKeyword( "convert" );
 		registerKeyword( "count" );
 		registerKeyword( "count_big" );
 		registerKeyword( "create" );
 		registerKeyword( "current" );
 		registerKeyword( "cursor" );
 		registerKeyword( "database" );
 		registerKeyword( "dbcc" );
 		registerKeyword( "deallocate" );
 		registerKeyword( "declare" );
 		registerKeyword( "decrypt" );
 		registerKeyword( "default" );
 		registerKeyword( "delete" );
 		registerKeyword( "desc" );
 		registerKeyword( "determnistic" );
 		registerKeyword( "disk" );
 		registerKeyword( "distinct" );
 		registerKeyword( "drop" );
 		registerKeyword( "dummy" );
 		registerKeyword( "dump" );
 		registerKeyword( "else" );
 		registerKeyword( "encrypt" );
 		registerKeyword( "end" );
 		registerKeyword( "endtran" );
 		registerKeyword( "errlvl" );
 		registerKeyword( "errordata" );
 		registerKeyword( "errorexit" );
 		registerKeyword( "escape" );
 		registerKeyword( "except" );
 		registerKeyword( "exclusive" );
 		registerKeyword( "exec" );
 		registerKeyword( "execute" );
 		registerKeyword( "exist" );
 		registerKeyword( "exit" );
 		registerKeyword( "exp_row_size" );
 		registerKeyword( "external" );
 		registerKeyword( "fetch" );
 		registerKeyword( "fillfactor" );
 		registerKeyword( "for" );
 		registerKeyword( "foreign" );
 		registerKeyword( "from" );
 		registerKeyword( "goto" );
 		registerKeyword( "grant" );
 		registerKeyword( "group" );
 		registerKeyword( "having" );
 		registerKeyword( "holdlock" );
 		registerKeyword( "identity" );
 		registerKeyword( "identity_gap" );
 		registerKeyword( "identity_start" );
 		registerKeyword( "if" );
 		registerKeyword( "in" );
 		registerKeyword( "index" );
 		registerKeyword( "inout" );
 		registerKeyword( "insensitive" );
 		registerKeyword( "insert" );
 		registerKeyword( "install" );
 		registerKeyword( "intersect" );
 		registerKeyword( "into" );
 		registerKeyword( "is" );
 		registerKeyword( "isolation" );
 		registerKeyword( "jar" );
 		registerKeyword( "join" );
 		registerKeyword( "key" );
 		registerKeyword( "kill" );
 		registerKeyword( "level" );
 		registerKeyword( "like" );
 		registerKeyword( "lineno" );
 		registerKeyword( "load" );
 		registerKeyword( "lock" );
 		registerKeyword( "materialized" );
 		registerKeyword( "max" );
 		registerKeyword( "max_rows_per_page" );
 		registerKeyword( "min" );
 		registerKeyword( "mirror" );
 		registerKeyword( "mirrorexit" );
 		registerKeyword( "modify" );
 		registerKeyword( "national" );
 		registerKeyword( "new" );
 		registerKeyword( "noholdlock" );
 		registerKeyword( "nonclustered" );
 		registerKeyword( "nonscrollable" );
 		registerKeyword( "non_sensitive" );
 		registerKeyword( "not" );
 		registerKeyword( "null" );
 		registerKeyword( "nullif" );
 		registerKeyword( "numeric_truncation" );
 		registerKeyword( "of" );
 		registerKeyword( "off" );
 		registerKeyword( "offsets" );
 		registerKeyword( "on" );
 		registerKeyword( "once" );
 		registerKeyword( "online" );
 		registerKeyword( "only" );
 		registerKeyword( "open" );
 		registerKeyword( "option" );
 		registerKeyword( "or" );
 		registerKeyword( "order" );
 		registerKeyword( "out" );
 		registerKeyword( "output" );
 		registerKeyword( "over" );
 		registerKeyword( "artition" );
 		registerKeyword( "perm" );
 		registerKeyword( "permanent" );
 		registerKeyword( "plan" );
 		registerKeyword( "prepare" );
 		registerKeyword( "primary" );
 		registerKeyword( "print" );
 		registerKeyword( "privileges" );
 		registerKeyword( "proc" );
 		registerKeyword( "procedure" );
 		registerKeyword( "processexit" );
 		registerKeyword( "proxy_table" );
 		registerKeyword( "public" );
 		registerKeyword( "quiesce" );
 		registerKeyword( "raiserror" );
 		registerKeyword( "read" );
 		registerKeyword( "readpast" );
 		registerKeyword( "readtext" );
 		registerKeyword( "reconfigure" );
 		registerKeyword( "references" );
 		registerKeyword( "remove" );
 		registerKeyword( "reorg" );
 		registerKeyword( "replace" );
 		registerKeyword( "replication" );
 		registerKeyword( "reservepagegap" );
 		registerKeyword( "return" );
 		registerKeyword( "returns" );
 		registerKeyword( "revoke" );
 		registerKeyword( "role" );
 		registerKeyword( "rollback" );
 		registerKeyword( "rowcount" );
 		registerKeyword( "rows" );
 		registerKeyword( "rule" );
 		registerKeyword( "save" );
 		registerKeyword( "schema" );
 		registerKeyword( "scroll" );
 		registerKeyword( "scrollable" );
 		registerKeyword( "select" );
 		registerKeyword( "semi_sensitive" );
 		registerKeyword( "set" );
 		registerKeyword( "setuser" );
 		registerKeyword( "shared" );
 		registerKeyword( "shutdown" );
 		registerKeyword( "some" );
 		registerKeyword( "statistics" );
 		registerKeyword( "stringsize" );
 		registerKeyword( "stripe" );
 		registerKeyword( "sum" );
 		registerKeyword( "syb_identity" );
 		registerKeyword( "syb_restree" );
 		registerKeyword( "syb_terminate" );
 		registerKeyword( "top" );
 		registerKeyword( "table" );
 		registerKeyword( "temp" );
 		registerKeyword( "temporary" );
 		registerKeyword( "textsize" );
 		registerKeyword( "to" );
 		registerKeyword( "tracefile" );
 		registerKeyword( "tran" );
 		registerKeyword( "transaction" );
 		registerKeyword( "trigger" );
 		registerKeyword( "truncate" );
 		registerKeyword( "tsequal" );
 		registerKeyword( "union" );
 		registerKeyword( "unique" );
 		registerKeyword( "unpartition" );
 		registerKeyword( "update" );
 		registerKeyword( "use" );
 		registerKeyword( "user" );
 		registerKeyword( "user_option" );
 		registerKeyword( "using" );
 		registerKeyword( "values" );
 		registerKeyword( "varying" );
 		registerKeyword( "view" );
 		registerKeyword( "waitfor" );
 		registerKeyword( "when" );
 		registerKeyword( "where" );
 		registerKeyword( "while" );
 		registerKeyword( "with" );
 		registerKeyword( "work" );
 		registerKeyword( "writetext" );
 		registerKeyword( "xmlextract" );
 		registerKeyword( "xmlparse" );
 		registerKeyword( "xmltest" );
 		registerKeyword( "xmlvalidate" );
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
 	public int getMaxAliasLength() {
 		return 30;
 	}
 
 	/**
 	 * By default, Sybase string comparisons are case-insensitive.
 	 * <p/>
 	 * If the DB is configured to be case-sensitive, then this return
 	 * value will be incorrect.
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	public String getCurrentTimestampSQLFunctionName() {
 		return "getdate()";
 	}
 
 	/**
 	 * Actually Sybase does not support LOB locators at al.
 	 *
 	 * @return false.
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return false;
 	}
 
-     public boolean supportsUniqueConstraintInCreateAlterTable() {
-         return false;
-     }
-
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
     @Override
     protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
         return sqlCode == Types.BOOLEAN ? TinyIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
     }
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
index b2f611aeb2..f76e3099a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
@@ -1,252 +1,244 @@
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
 
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for TimesTen 5.1.
  * 
  * Known limitations:
  * joined-subclass support because of no CASE support in TimesTen
  * No support for subqueries that includes aggregation
  *  - size() in HQL not supported
  *  - user queries that does subqueries with aggregation
  * No CLOB/BLOB support 
  * No cascade delete support.
  * No Calendar support
  * No support for updating primary keys.
  * 
  * @author Sherry Listgarten and Max Andersen
  */
 public class TimesTenDialect extends Dialect {
 	
 	public TimesTenDialect() {
 		super();
 		registerColumnType( Types.BIT, "TINYINT" );
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.TINYINT, "TINYINT" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.CHAR, "CHAR(1)" );
 		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.DOUBLE, "DOUBLE" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.VARBINARY, "VARBINARY($l)" );
 		registerColumnType( Types.NUMERIC, "DECIMAL($p, $s)" );
 		// TimesTen has no BLOB/CLOB support, but these types may be suitable 
 		// for some applications. The length is limited to 4 million bytes.
         registerColumnType( Types.BLOB, "VARBINARY(4000000)" ); 
         registerColumnType( Types.CLOB, "VARCHAR(4000000)" );
 	
 		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
 		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
 		registerFunction( "concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING) );
 		registerFunction( "mod", new StandardSQLFunction("mod") );
 		registerFunction( "to_char", new StandardSQLFunction("to_char",StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date",StandardBasicTypes.TIMESTAMP) );
 		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "getdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 
 	}
 	
 	public boolean dropConstraints() {
             return true;
 	}
 	
 	public boolean qualifyIndexName() {
             return false;
 	}
-
-	public boolean supportsUnique() {
-		return false;
-	}
-    
-	public boolean supportsUniqueConstraintInCreateAlterTable() {
-		return false;
-	}
 	
     public String getAddColumnString() {
             return "add";
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "select first 1 " + sequenceName + ".nextval from sys.tables";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getQuerySequencesString() {
 		return "select NAME from sys.sequences";
 	}
 
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	// new methods in dialect3
 	/*public boolean supportsForUpdateNowait() {
 		return false;
 	}*/
 	
 	public String getForUpdateString() {
 		return "";
 	}
 	
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	public boolean supportsTableCheck() {
 		return false;
 	}
 	
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "select first 1 sysdate from sys.tables";
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String generateTemporaryTableName(String baseTableName) {
 		String name = super.generateTemporaryTableName(baseTableName);
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// TimesTen has no known variation of a "SELECT ... FOR UPDATE" syntax...
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
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
index 5d9a34881e..6c872434b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/DefaultUniqueDelegate.java
@@ -1,229 +1,155 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.dialect.unique;
 
 import java.util.Iterator;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * The default UniqueDelegate implementation for most dialects.  Uses
  * separate create/alter statements to apply uniqueness to a column.
  * 
  * @author Brett Meyer
  */
 public class DefaultUniqueDelegate implements UniqueDelegate {
 	
 	private final Dialect dialect;
 	
 	public DefaultUniqueDelegate( Dialect dialect ) {
 		this.dialect = dialect;
 	}
 
 	@Override
 	public String applyUniqueToColumn( org.hibernate.mapping.Table table,
 			org.hibernate.mapping.Column column ) {
-//		if ( column.isUnique()
-//				&& ( column.isNullable()
-//						|| dialect.supportsNotNullUnique() ) ) {
-//			if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-//				// If the constraint is supported, do not add to the column syntax.
-//				UniqueKey uk = getOrCreateUniqueKey( column.getQuotedName( dialect ) + '_' );
-//				uk.addColumn( column );
-//			}
-//			else if ( dialect.supportsUnique() ) {
-//				// Otherwise, add to the column syntax if supported.
-//				sb.append( " unique" );
-//			}
-//		}
-		
 		org.hibernate.mapping.UniqueKey uk = table.getOrCreateUniqueKey(
 				column.getQuotedName( dialect ) + '_' );
 		uk.addColumn( column );
 		return "";
 	}
 
 	@Override
 	public String applyUniqueToColumn( Table table, Column column ) {
-//		if ( column.isUnique()
-//				&& ( column.isNullable()
-//						|| dialect.supportsNotNullUnique() ) ) {
-//			if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-//				// If the constraint is supported, do not add to the column syntax.
-//				UniqueKey uk = getOrCreateUniqueKey( column.getQuotedName( dialect ) + '_' );
-//				uk.addColumn( column );
-//			}
-//			else if ( dialect.supportsUnique() ) {
-//				// Otherwise, add to the column syntax if supported.
-//				sb.append( " unique" );
-//			}
-//		}
-		
 		UniqueKey uk = table.getOrCreateUniqueKey( column.getColumnName()
 				.encloseInQuotesIfQuoted( dialect ) + '_' );
 		uk.addColumn( column );
 		return "";
 	}
 
 	@Override
 	public String applyUniquesToTable( org.hibernate.mapping.Table table ) {
-		// TODO: Am I correct that this shouldn't be done unless the constraint
-		// isn't created in an alter table?
-//		Iterator uniqueKeyIterator = table.getUniqueKeyIterator();
-//		while ( uniqueKeyIterator.hasNext() ) {
-//			UniqueKey uniqueKey = (UniqueKey) uniqueKeyIterator.next();
-//			
-//			sb.append( ", " ).append( createUniqueConstraint( uniqueKey) );
-//		}
 		return "";
 	}
 
 	@Override
 	public String applyUniquesToTable( Table table ) {
-		// TODO: Am I correct that this shouldn't be done unless the constraint
-		// isn't created in an alter table?
-//		Iterator uniqueKeyIterator = table.getUniqueKeyIterator();
-//		while ( uniqueKeyIterator.hasNext() ) {
-//			UniqueKey uniqueKey = (UniqueKey) uniqueKeyIterator.next();
-//			
-//			sb.append( ", " ).append( createUniqueConstraint( uniqueKey) );
-//		}
 		return "";
 	}
 	
 	@Override
 	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
-//		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-//			return super.sqlCreateString( dialect, p, defaultCatalog, defaultSchema );
-//		}
-//		else {
-//			return Index.buildSqlCreateIndexString( dialect, getName(), getTable(), getColumnIterator(), true,
-//					defaultCatalog, defaultSchema );
-//		}
-		
+		// Do this here, rather than allowing UniqueKey/Constraint to do it.
+		// We need full, simplified control over whether or not it happens.
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName(
 						dialect, defaultCatalog, defaultSchema ) )
 				.append( " add constraint " )
 				.append( uniqueKey.getName() )
 				.append( uniqueConstraintSql( uniqueKey ) )
 				.toString();
 	}
 	
 	@Override
 	public String applyUniquesOnAlter( UniqueKey uniqueKey  ) {
-//		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-//			return super.sqlCreateString( dialect, p, defaultCatalog, defaultSchema );
-//		}
-//		else {
-//			return Index.buildSqlCreateIndexString( dialect, getName(), getTable(), getColumnIterator(), true,
-//					defaultCatalog, defaultSchema );
-//		}
-		
-		return new StringBuilder( "alter table " )
+		// Do this here, rather than allowing UniqueKey/Constraint to do it.
+				// We need full, simplified control over whether or not it happens.
+				return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
 				.append( " add constraint " )
 				.append( uniqueKey.getName() )
 				.append( uniqueConstraintSql( uniqueKey ) )
 				.toString();
 	}
 	
 	@Override
 	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema ) {
-//		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-//			return super.sqlDropString( dialect, defaultCatalog, defaultSchema );
-//		}
-//		else {
-//			return Index.buildSqlDropIndexString( dialect, getTable(), getName(), defaultCatalog, defaultSchema );
-//		}
-		
+		// Do this here, rather than allowing UniqueKey/Constraint to do it.
+		// We need full, simplified control over whether or not it happens.
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName(
 						dialect, defaultCatalog, defaultSchema ) )
 				.append( " drop constraint " )
 				.append( dialect.quote( uniqueKey.getName() ) )
 				.toString();
 	}
 	
 	@Override
 	public String dropUniquesOnAlter( UniqueKey uniqueKey  ) {
-//		if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
-//			return super.sqlDropString( dialect, defaultCatalog, defaultSchema );
-//		}
-//		else {
-//			return Index.buildSqlDropIndexString( dialect, getTable(), getName(), defaultCatalog, defaultSchema );
-//		}
-		
+		// Do this here, rather than allowing UniqueKey/Constraint to do it.
+		// We need full, simplified control over whether or not it happens.
 		return new StringBuilder( "alter table " )
 				.append( uniqueKey.getTable().getQualifiedName( dialect ) )
 				.append( " drop constraint " )
 				.append( dialect.quote( uniqueKey.getName() ) )
 				.toString();
 	}
 	
 	@Override
 	public String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey ) {
-		// TODO: This may not be necessary, but not all callers currently
-		// check it on their own.  Go through their logic.
-//		if ( !isGenerated( dialect ) ) return null;
-		
 		StringBuilder sb = new StringBuilder();
 		sb.append( " unique (" );
 		Iterator columnIterator = uniqueKey.getColumnIterator();
 		while ( columnIterator.hasNext() ) {
 			org.hibernate.mapping.Column column
 					= (org.hibernate.mapping.Column) columnIterator.next();
 			sb.append( column.getQuotedName( dialect ) );
 			if ( columnIterator.hasNext() ) {
 				sb.append( ", " );
 			}
 		}
 		
 		return sb.append( ')' ).toString();
 	}
 	
 	@Override
 	public String uniqueConstraintSql( UniqueKey uniqueKey ) {
-		// TODO: This may not be necessary, but not all callers currently
-		// check it on their own.  Go through their logic.
-//		if ( !isGenerated( dialect ) ) return null;
-		
 		StringBuilder sb = new StringBuilder();
 		sb.append( " unique (" );
 		Iterator columnIterator = uniqueKey.getColumns().iterator();
 		while ( columnIterator.hasNext() ) {
 			org.hibernate.mapping.Column column
 					= (org.hibernate.mapping.Column) columnIterator.next();
 			sb.append( column.getQuotedName( dialect ) );
 			if ( columnIterator.hasNext() ) {
 				sb.append( ", " );
 			}
 		}
 		
 		return sb.append( ')' ).toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java b/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
index 042f0c8c3c..d5ff514649 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/unique/UniqueDelegate.java
@@ -1,145 +1,151 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.dialect.unique;
 
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.UniqueKey;
 
 /**
  * Dialect-level delegate in charge of applying "uniqueness" to a column.
  * Uniqueness can be defined in 1 of 3 ways:
  * 
- * 1.) Add a unique constraint via separate create/alter table statements.
+ * 1.) Add a unique constraint via separate alter table statements.
  * 2.) Add a unique constraint via dialect-specific syntax in table create statement.
  * 3.) Add "unique" syntax to the column itself.
  * 
  * #1 & #2 are preferred, if possible -- #3 should be solely a fall-back.
  * 
+ * TODO: This could eventually be simplified.  With AST, 1 "applyUniqueness"
+ * method might be possible. But due to .cfg and .mapping still resolving
+ * around StringBuilders, separate methods were needed.
+ * 
  * See HHH-7797.
  * 
  * @author Brett Meyer
  */
 public interface UniqueDelegate {
 	
 	/**
 	 * If the delegate supports unique constraints, this method should simply
 	 * create the UniqueKey on the Table.  Otherwise, the constraint isn't
-	 * supported and "unique" should be added to the column definition.
+	 * supported and "unique" should be returned in order to add it
+	 * to the column definition.
 	 * 
 	 * @param table
 	 * @param column
 	 * @return String
 	 */
 	public String applyUniqueToColumn( org.hibernate.mapping.Table table,
 			org.hibernate.mapping.Column column );
 	
 	/**
 	 * If the delegate supports unique constraints, this method should simply
 	 * create the UniqueKey on the Table.  Otherwise, the constraint isn't
-	 * supported and "unique" should be added to the column definition.
+	 * supported and "unique" should be returned in order to add it
+	 * to the column definition.
 	 * 
 	 * @param table
 	 * @param column
 	 * @return String
 	 */
 	public String applyUniqueToColumn( Table table, Column column );
 	
 	/**
-	 * If creating unique constraints in separate alter statements are not
-	 * supported, this method should return the syntax necessary to create
-	 * the constraint on the original create table statement.
+	 * If constraints are supported, but not in seperate alter statements,
+	 * return uniqueConstraintSql in order to add the constraint to the
+	 * original table definition.
 	 * 
 	 * @param table
 	 * @return String
 	 */
 	public String applyUniquesToTable( org.hibernate.mapping.Table table );
 	
 	/**
-	 * If creating unique constraints in separate alter statements are not
-	 * supported, this method should return the syntax necessary to create
-	 * the constraint on the original create table statement.
+	 * If constraints are supported, but not in seperate alter statements,
+	 * return uniqueConstraintSql in order to add the constraint to the
+	 * original table definition.
 	 * 
 	 * @param table
 	 * @return String
 	 */
 	public String applyUniquesToTable( Table table );
 	
 	/**
 	 * If creating unique constraints in separate alter statements is
 	 * supported, generate the necessary "alter" syntax for the given key.
 	 * 
 	 * @param uniqueKey
 	 * @param defaultCatalog
 	 * @param defaultSchema
 	 * @return String
 	 */
 	public String applyUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema );
 	
 	/**
 	 * If creating unique constraints in separate alter statements is
 	 * supported, generate the necessary "alter" syntax for the given key.
 	 * 
 	 * @param uniqueKey
 	 * @return String
 	 */
 	public String applyUniquesOnAlter( UniqueKey uniqueKey );
 	
 	/**
 	 * If dropping unique constraints in separate alter statements is
 	 * supported, generate the necessary "alter" syntax for the given key.
 	 * 
 	 * @param uniqueKey
 	 * @param defaultCatalog
 	 * @param defaultSchema
 	 * @return String
 	 */
 	public String dropUniquesOnAlter( org.hibernate.mapping.UniqueKey uniqueKey,
 			String defaultCatalog, String defaultSchema );
 	
 	/**
 	 * If dropping unique constraints in separate alter statements is
 	 * supported, generate the necessary "alter" syntax for the given key.
 	 * 
 	 * @param uniqueKey
 	 * @return String
 	 */
 	public String dropUniquesOnAlter( UniqueKey uniqueKey );
 	
 	/**
 	 * Generates the syntax necessary to create the unique constraint (reused
 	 * by all methods).  Ex: "unique (column1, column2, ...)"
 	 * 
 	 * @param uniqueKey
 	 * @return String
 	 */
 	public String uniqueConstraintSql( org.hibernate.mapping.UniqueKey uniqueKey );
 	
 	/**
 	 * Generates the syntax necessary to create the unique constraint (reused
 	 * by all methods).  Ex: "unique (column1, column2, ...)"
 	 * 
 	 * @param uniqueKey
 	 * @return String
 	 */
 	public String uniqueConstraintSql( UniqueKey uniqueKey );
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java b/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java
index 936f61c73c..5685d986d8 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/constraint/ConstraintTest.java
@@ -1,64 +1,85 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.constraint;
 
-import javax.persistence.Column;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
+import javax.persistence.Table;
 
+import org.hibernate.mapping.Column;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
- * HHH-7797 re-wrote the way dialects handle unique constraints.  Test as
- * many variations of unique, not null, and primary key constraints as possible.
+ * HHH-7797 re-wrote the way dialects handle unique constraints.  Test
+ * variations of unique & not null to ensure the constraints are created
+ * correctly for each dialect.
  * 
  * @author Brett Meyer
  */
 @TestForIssue( jiraKey = "HHH-7797" )
 public class ConstraintTest extends BaseCoreFunctionalTestCase {
 	
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {
 				Entity1.class
 		};
 	}
 	
 	@Test
 	public void testConstraints() {
-		// nothing yet -- more interested in DDL creation
+		Column column = (Column) configuration().getClassMapping( Entity1.class.getName() )
+				.getProperty( "foo1" ).getColumnIterator().next();
+		assertFalse( column.isNullable() );
+		assertTrue( column.isUnique() );
+
+		column = (Column) configuration().getClassMapping( Entity1.class.getName() )
+				.getProperty( "foo2" ).getColumnIterator().next();
+		assertTrue( column.isNullable() );
+		assertTrue( column.isUnique() );
+
+		column = (Column) configuration().getClassMapping( Entity1.class.getName() )
+				.getProperty( "id" ).getColumnIterator().next();
+		assertFalse( column.isNullable() );
+		assertTrue( column.isUnique() );
 	}
 	
-	// Primary key w/ not null and unique
 	@Entity
+	@Table( name = "Entity1" )
 	public static class Entity1 {
 		@Id
 		@GeneratedValue
-//		@Column( nullable = false, unique = true)
+		@javax.persistence.Column( nullable = false, unique = true)
 		public long id;
 		
-		@Column( nullable = false, unique = true)
-		public String foo;
+		@javax.persistence.Column( nullable = false, unique = true)
+		public String foo1;
+		
+		@javax.persistence.Column( nullable = true, unique = true)
+		public String foo2;
 	}
 }
\ No newline at end of file
